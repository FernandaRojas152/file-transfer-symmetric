package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Server{
	private int port;
	private DataOutputStream out;
	private DataInputStream in;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private String serverSHA;
	private String clientSHA;
	private String targetFileName;
	private String encryptedFilePath;
	
	
	private Key serverKey;
	
	

	
	
	
	public Server() {
		port= 9090;
		serverSHA= "";
	}
	
	public static void main(String[] args) {
		
	}

	public int getPort() {
		return port;
	}




	public ServerSocket getServerSocket() {
		return serverSocket;
	}


	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}


	public String getServerSHA() {
		return serverSHA;
	}


	public String getClientSHA() {
		return clientSHA;
	}

	
	public void start() {
		try {
			serverSocket= new ServerSocket(port);
			clientSocket= serverSocket.accept();
			
			out= new DataOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(new DataInputStream(clientSocket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Key generateKey(byte[] key){
		byte[] byteKey= new byte[16];
		for (int i = 0; i < 16; i++) {
			byteKey[i]= key[i];
		}
		try {
			Key keyAES= new SecretKeySpec(byteKey, "AES");
			return keyAES;
		} catch (Exception e) {
			System.out.println("Error while generating key" + e);	
		}
		return null;
	}
	
	public String decryptFile(String filePath, Key secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException {
		
      
        
		Cipher cipher= Cipher.getInstance("AES");
		Base64.Decoder decoder= Base64.getDecoder();
		byte[] encrypted= decoder.decode(filePath);
		String decryptedFile="";
		try {
			
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decrypted= cipher.doFinal(encrypted);
			decryptedFile= new String(decrypted);
			
			  //String digest = DigestUtils.sha1Hex((is));
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        
	        
	        String digest = toHexString(md.digest(decryptedFile.getBytes(StandardCharsets.UTF_8)));
	        serverSHA = digest;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return decryptedFile;
	}
	  public static String toHexString(byte[] hash)
	    {
	        // Convert byte array into signum representation
	        BigInteger number = new BigInteger(1, hash);
	 
	        // Convert message digest into hex value
	        StringBuilder hexString = new StringBuilder(number.toString(16));
	 
	        // Pad with leading zeros
	        while (hexString.length() < 64)
	        {
	            hexString.insert(0, '0');
	        }
	 
	        return hexString.toString();
	    }

	 public int awaitCommand() throws IOException {
	        int interrupt = -1;
	        String command;
	        try {
	            command = in.readUTF();
	            switch (Objects.requireNonNull(command)) {
	                case "df" -> DiffieHelmanAnswer();                  
	                case "cypher" -> {
	                    targetFileName = in.readUTF();
	                    
	                    receiveFile(new File(encryptedFilePath), in);
	                    decryptFile(encryptedFilePath,serverKey);
	                    clientSHA = in.readUTF();
	                }
	                case "exit" -> {
	                    clientSocket.close();
	                    serverSocket.close();
	                    interrupt = 1;
	                    System.out.println("Connection terminated by client");
	                }
	                default -> {
	                    out.writeUTF("Command not found");
	                }
	            }
	            System.out.println("model.Client command: " + command);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return interrupt;
	    }
	 public void DiffieHelmanAnswer() throws IOException {
		 //Recibe params del DiffieHelman
		 BigInteger p = new BigInteger(in.readUTF());
		 BigInteger g = new BigInteger(in.readUTF());
		 BigInteger A =  new BigInteger(in.readUTF());
		 
		 //Generar el b secreto
		 Random randomGenerator = new Random();
         BigInteger b = new BigInteger(1024, randomGenerator);
        //Calcular el B publico 
         BigInteger B = g.modPow(b, p);
         
         //Mandar el B publico 
         out.writeUTF(B.toString());
         
         
      // Calcular la llave secreta
         BigInteger decryptionKey = A.modPow(b, p);
         //Generar llave AES
         serverKey = generateKey(decryptionKey.toByteArray());
          
         
         
         
         
		 
		 
	 }
	 
	  public void receiveFile(File file, DataInputStream in) throws IOException {
	        FileOutputStream fileOut = new FileOutputStream(file);
	        byte[] buf = new byte[Short.MAX_VALUE];
	        int bytesSent;
	        while ((bytesSent = in.readShort()) != -1) {
	            in.readFully(buf, 0, bytesSent);
	            fileOut.write(buf, 0, bytesSent);
	        }
	        fileOut.close();
	    }
	  
	  
	  public boolean compareSHAS() {
		  
		  
		  
		  return serverSHA.equals(clientSHA);
		  
	  }

	 
	
}
