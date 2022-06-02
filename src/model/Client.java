package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.digest.DigestUtils;

public class Client{
	private String clientHost;
	private int port;
	private String hash;
	private DataOutputStream out;
	private DataInputStream in;
	public Client() {
		clientHost="Fer";
		port= 9090;
		hash= "";
	}
	
	public static void main(String[] args) {
		
	}
	
	public String getClientHost() {
		return clientHost;
	}

	public void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void connect() {
        try {
            Socket echoSocket = new Socket(clientHost, port);
            out = new DataOutputStream(echoSocket.getOutputStream());
            in = new DataInputStream(new DataInputStream(echoSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected to server: " + echoSocket.getRemoteSocketAddress());
        } catch (UnknownHostException e) {
            System.err.println("Non-existent host or wrong input " + clientHost);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    clientHost);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	  public void generateSHA256(String parentDirectoryPath, String fileName) throws Exception {
	        String fullPath = parentDirectoryPath + "\\" + fileName;
	        System.out.println(fullPath);
	        //byte[] encryptedData = encryptFile(fullPath);
	        //Se escriben los datos encripdatos localmente
	        String encryptedFilePath = parentDirectoryPath + "\\" + "clientFileEncrypted.txt";
	        File encryptedFile = new File(encryptedFilePath);
	        //writeFile(encryptedFile, encryptedData);
	        //Se envia el nombre del archivo
	        
	        //sendMessage(fileName, out);
	        
	        //Se envia el archivo cifrado al servidor
	        //sendFile(encryptedFile, out);
	        
	        
	        //Se imprime el Sha1 del archivo no cifrado
	        InputStream is = new FileInputStream(fullPath);
	        String digest = DigestUtils.sha1Hex((is));
	        hash = digest;
	        out.writeUTF(digest);
	        System.out.println("full path: "+fullPath);
	        System.out.println("Sha1 of unencrypted file: " + digest);
	        
	        //Tells server to stop listening
	        out.writeUTF("exit");
	    }

	private String encryptFile(String fullPath, Key secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher= Cipher.getInstance("AES");	
		byte[] textByte= fullPath.getBytes();
		String encryptedFile= "";
		
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encrypted= cipher.doFinal(textByte);
			Base64.Encoder encoder= Base64.getEncoder();
			encryptedFile= encoder.encodeToString(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return encryptedFile;
	}
}
