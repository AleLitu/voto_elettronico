import java.net.ServerSocket;
import java.net.Socket;

public class Server{
	public static void main(String[] args) {
		ServerSocket sSrv;
		Socket toClient;
		try {
			sSrv = new ServerSocket(0);
			System.out.println("Indirizzo: " + sSrv.getInetAddress() + "; porta: " + sSrv.getLocalPort());
			while(true) {
				toClient = sSrv.accept();
				System.out.println("Indirizzo client: " + toClient.getInetAddress() + "; porta: " + toClient.getPort());
				Thread t = new GestisciClient(toClient);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}