package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;

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

	public void setPort(int port) {
		this.port = port;
	}


	public String getHash() {
		return hash;
	}


	public void setHash(String hash) {
		this.hash = hash;
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


	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}


	public String getServerSHA() {
		return serverSHA;
	}


	public void setServerSHA(String serverSHA) {
		this.serverSHA = serverSHA;
	}


	public String getClientSHA() {
		return clientSHA;
	}

	public void setClientSHA(String clientSHA) {
		this.clientSHA = clientSHA;
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
}
