package server.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import server.LogHandler;

public class HandlerKeys{
	private static HandlerKeys instance = null;
	private static PublicKey pubKey;
	private static PrivateKey privKey;
	
	private HandlerKeys() {
		
	}
	
	public static synchronized HandlerKeys getInstance() {
		if(instance == null) {
			instance = new HandlerKeys();
		}
		return instance;
	}
	
	public static synchronized PublicKey getPublicKey() {
		return pubKey;
	}

	public static synchronized PrivateKey getPrivateKey() {
		return privKey;
	}

	public static void GenerateKeys() throws IOException { 
		KeyPairGenerator generator;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			KeyPair generatedKeyPair = generator.generateKeyPair();
			privKey = generatedKeyPair.getPrivate();
			pubKey = generatedKeyPair.getPublic();
			SaveKeyPair(generatedKeyPair);
		} catch (NoSuchAlgorithmException e) {
			LogHandler.writeLog("Errore nell'algoritmo di creazione delle chiavi");
		} catch (IOException e) {
			LogHandler.writeLog("Errore nel salvataggio delle chiavi");
		}
	}
	
	public static void SaveKeyPair(KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
 
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream("keys/public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		fos = new FileOutputStream("keys/private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
		LogHandler.writeLog("Chiavi create e salvate correttamente");
	}
	
	public static void LoadKeyPair(String algorithm) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		pubKey = keyFactory.generatePublic(publicKeySpec);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		privKey = keyFactory.generatePrivate(privateKeySpec);

	}
}