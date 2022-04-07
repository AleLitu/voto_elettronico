package server;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
	private static String votazione;
	public static void main(String[] args) {
		ServerSocket sSrv;
		Socket toClient;
		votazione = "null";
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
	}
	static String getVotazione() {
		return votazione;
	}
	static void setVotazione(String v) {
		votazione = v;
	}
}