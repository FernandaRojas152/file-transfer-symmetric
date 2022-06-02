package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
		// TODO Auto-generated constructor stub
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
	
	

}
