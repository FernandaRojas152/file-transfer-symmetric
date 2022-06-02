package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.DigestInputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DHParameterSpec;

public class Client {
	private String clientHost;
	private int port;
	private String hash;
	private DataOutputStream out;
	private DataInputStream in;
	private Key key;
	public static String CLIENT_FOLDER = "../ClientFiles/";

	public Client() {
		clientHost = "Fer";
		port = 9090;
		hash = "";
	}

	public static void main(String[] args) throws Exception{
		
		Client client = new Client();
		client.connect();
		
		BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
		
		try {
			client.initializeDiffieHelman();
			System.out.println("Coloque el nombre del archivo(tiene que estar presente dentro del proyecto)");
			String filename = br.readLine();
			
			
			
			
			
			System.out.println("Deseea encriptar y enviar el archivo ? Si = 1, No = 0");
			
			int ansEnc = br.read();
			if(ansEnc != 1) {
				System.exit(0);
				
			}
			client.generateSHA256(CLIENT_FOLDER, filename);
			
			
			
		
			
		}catch(Exception e) {
			e.getStackTrace();
		}

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
			System.err.println("Couldn't get I/O for the connection to " + clientHost);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateSHA256(String parentDirectoryPath, String fileName) throws Exception {

		out.writeUTF("cypher");
		String fullPath = parentDirectoryPath + "\\" + fileName;
		System.out.println(fullPath);
		String encryptedData = encryptFile(fullPath, key);
		// Se escriben los datos encripdatos localmente
		String encryptedFilePath = parentDirectoryPath + "\\" + "clientFileEncrypted.txt";
		File encryptedFile = new File(encryptedFilePath);
		writeFile(encryptedFile, encryptedData.getBytes());
		// Se envia el nombre del archivo

		sendMessage(fileName, out);

		// Se envia el archivo cifrado al servidor
		sendFile(encryptedFile, out);

		// Se imprime el Sha1 del archivo no cifrado
		InputStream is = new FileInputStream(fullPath);
		// String digest = DigestUtils.sha1Hex((is));
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		String digest = toHexString(md.digest(is.readAllBytes()));
		hash = digest;
		out.writeUTF(digest);
		System.out.println("full path: " + fullPath);
		System.out.println("Sha1 of unencrypted file: " + digest);

		// Tells server to stop listening
		out.writeUTF("exit");
	}

	private String encryptFile(String fullPath, Key secretKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		Cipher cipher = Cipher.getInstance("AES");
		byte[] textByte = Files.readAllBytes(Paths.get(fullPath));
		String encryptedFile = "";

		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encrypted = cipher.doFinal(textByte);
			Base64.Encoder encoder = Base64.getEncoder();
			encryptedFile = encoder.encodeToString(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encryptedFile;
	}

	public static String toHexString(byte[] hash) {
		// Convert byte array into signum representation
		BigInteger number = new BigInteger(1, hash);

		// Convert message digest into hex value
		StringBuilder hexString = new StringBuilder(number.toString(16));

		// Pad with leading zeros
		while (hexString.length() < 64) {
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}

	public void initializeDiffieHelman() throws NoSuchAlgorithmException, InvalidParameterSpecException, IOException {

		out.writeUTF("df");
		AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
		paramGen.init(1024, new SecureRandom());
		AlgorithmParameters params = paramGen.generateParameters();
		DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);

		Random randomGenerator = new Random();

		BigInteger a = new BigInteger(1024, randomGenerator); // El secreto(privado)
		BigInteger p = dhSpec.getP(); // Numero primo(publico)
		BigInteger g = dhSpec.getG(); // Numero Primo generador de primos (publico)

		BigInteger A = g.modPow(a, p); // llave del cliente (A=g^a(modp)) (publico)

		// Mandar el numero primo
		out.writeUTF(p.toString());

		// mandar Primo generador de primos
		out.writeUTF(g.toString());

		// mandar calculo de A (llave cliente)
		out.writeUTF(A.toString());
		// Recibir la llave
		BigInteger B = new BigInteger(in.readUTF());

		// Calcular la llave secreta
		BigInteger encryptionKey = B.modPow(a, p);

		GenKeys gk = new GenKeys();
		key = gk.generateKey(encryptionKey.toByteArray());

	}

	public void sendFile(File file, DataOutputStream out) {
		try {

			FileInputStream fileIn = new FileInputStream(file);
			byte[] buf = new byte[Short.MAX_VALUE];
			int bytesRead;
			while ((bytesRead = fileIn.read(buf)) != -1) {
				out.writeShort(bytesRead);
				out.write(buf, 0, bytesRead);
			}
			out.writeShort(-1);
			fileIn.close();
		} catch (IOException i) {

		}

	}

	private void writeFile(File file, byte[] data) {
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message, DataOutputStream out) throws IOException {
		out.writeUTF(message);
		out.flush();
	}

}
