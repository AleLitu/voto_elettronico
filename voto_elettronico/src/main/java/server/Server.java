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

import server.handler.HandlerKeys;


public class Server{
	private static PublicKey pubKey;
	private static PrivateKey privKey;
	private static HandlerKeys keys;
	ServerSocket sSrv;
	Socket toClient;
	
	public void start() throws IOException {
		try {
			LogHandler.createLog();
			LogHandler.writeLog("Server started");
			keys = HandlerKeys.getInstance();
			keys.LoadKeyPair("RSA");
			//KeyPair loadedKeyPair = LoadKeyPair("RSA");
			//pubKey = loadedKeyPair.getPublic();
			//privKey = loadedKeyPair.getPrivate();
			LogHandler.writeLog("Chiavi caricate correttamente");
		}catch(FileNotFoundException e) {
			keys.GenerateKeys();
		} catch (IOException e) {
			LogHandler.writeLog("Errore nella lettura delle chiavi dai file");
			return;
		} catch(InvalidKeySpecException e) {
			LogHandler.writeLog("Le chiavi hanno subito delle modifiche, verranno ricreate");
			keys.GenerateKeys();
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		try {			
			sSrv = new ServerSocket(50000);
			//System.out.println("Indirizzo: " + sSrv.getInetAddress() + "; porta: " + sSrv.getLocalPort());
			while(true) {
				toClient = sSrv.accept();
				LogHandler.writeLog("Client connesso: " + toClient.getInetAddress() + "; porta: " + toClient.getPort());
				GestisciClient client = new GestisciClient(toClient);
				Thread t = new Thread(client);
				t.start();
			}
		}catch(SocketException e) {
			LogHandler.writeLog("Client disconnesso");
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
		LogHandler.writeLog("Server spento");
	}
	
	
	
	protected static PublicKey getPublicKey() {
		return pubKey;
	}
	
	protected static PrivateKey getPrivateKey() {
		return privKey;
	}
}