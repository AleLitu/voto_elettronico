package server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class Server{
	private static PublicKey pubKey;
	private static PrivateKey privKey;
	ServerSocket sSrv;
	Socket toClient;
	
	public void start() {
		try {
			LogHandler.createLog();
			LogHandler.writeLog("Server started");
			KeyPair loadedKeyPair = LoadKeyPair("RSA");
			pubKey = loadedKeyPair.getPublic();
			privKey = loadedKeyPair.getPrivate();
			System.out.println("Chiavi caricate correttamente");
		}catch(FileNotFoundException e) {
			GenerateKeys();
		} catch (IOException e) {
			System.out.println("Errore nella lettura delle chiavi dai file");
			return;
		} catch(InvalidKeySpecException e) {
			System.out.println("Le chiavi hanno subito delle modifiche, verranno ricreate");
			GenerateKeys();
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		try {			
			sSrv = new ServerSocket(50000);
			System.out.println("Indirizzo: " + sSrv.getInetAddress() + "; porta: " + sSrv.getLocalPort());
			while(true) {
				toClient = sSrv.accept();
				System.out.println("Indirizzo client: " + toClient.getInetAddress() + "; porta: " + toClient.getPort());
				GestisciClient client = new GestisciClient(toClient);
				Thread t = new Thread(client);
				t.start();
			}
		}catch(SocketException e) {
			return;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() throws IOException {
		if(toClient != null)
			toClient.close();
		if(sSrv != null)
			sSrv.close();
	}
	
	/*public static void main(String[] args) {
		ServerSocket sSrv;
		Socket toClient;
		try {
			KeyPair loadedKeyPair = LoadKeyPair("RSA");
			pubKey = loadedKeyPair.getPublic();
			privKey = loadedKeyPair.getPrivate();
			System.out.println("Chiavi caricate correttamente");
		}catch(FileNotFoundException e) {
			GenerateKeys();
		} catch (IOException e) {
			System.out.println("Errore nella lettura delle chiavi dai file");
			return;
		} catch(InvalidKeySpecException e) {
			System.out.println("Le chiavi hanno subito delle modifiche, verranno ricreate");
			GenerateKeys();
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		try {			
			sSrv = new ServerSocket(50000);
			System.out.println("Indirizzo: " + sSrv.getInetAddress() + "; porta: " + sSrv.getLocalPort());
			while(true) {
				toClient = sSrv.accept();
				System.out.println("Indirizzo client: " + toClient.getInetAddress() + "; porta: " + toClient.getPort());
				GestisciClient client = new GestisciClient(toClient);
				Thread t = new Thread(client);
				t.start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
	protected static PublicKey getPublicKey() {
		return pubKey;
	}
	
	protected static PrivateKey getPrivateKey() {
		return privKey;
	}
	
	private static void GenerateKeys() { 
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			KeyPair generatedKeyPair = generator.generateKeyPair();
			privKey = generatedKeyPair.getPrivate();
			pubKey = generatedKeyPair.getPublic();
			SaveKeyPair(generatedKeyPair);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Errore nell'algoritmo di creazione delle chiavi");
		} catch (IOException e) {
			System.out.println("Errore nel salvataggio delle chiavi");
		} 
		
	}
	
	private static void SaveKeyPair(KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
 
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream("keys/public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream("keys/private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
		System.out.println("Chiavi create e salvate correttamente");
	}
	
	private static KeyPair LoadKeyPair(String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File("keys/public.key");
		FileInputStream fis = new FileInputStream("keys/public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
 
		// Read Private Key.
		File filePrivateKey = new File("keys/private.key");
		fis = new FileInputStream("keys/private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
 
		return new KeyPair(publicKey, privateKey);
	}
}