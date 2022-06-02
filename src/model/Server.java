package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Server{
	private int port;
	private String hash;
	private DataOutputStream out;
	private DataInputStream in;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private String serverSHA;
	private String clientSHA;
	
	public Server() {
		port= 9090;
		serverSHA= "";
	}
	
	public static void main(String[] args) {
		
	}

	public int getPort() {
		return port;
	}

	public String getHash() {
		return hash;
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return decryptedFile;
	}
}
