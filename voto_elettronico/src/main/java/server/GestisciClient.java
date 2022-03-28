package server;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class GestisciClient implements Runnable{
	private Socket so;
	InputStream inputStream;
	OutputStream outputStream;
	Connection conn;
	
	public GestisciClient(Socket socket) {
		try {
			so = socket;
			inputStream = so.getInputStream();
			outputStream = so.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		System.out.println("0");
		int dim_buffer = 100;
		byte buffer[] = new byte[dim_buffer];
		
		String url = "jdbc:mysql://localhost:3306/votazioni?";
    	String usr = "root";
    	String pwd = "";
    	try {
    		conn = DriverManager.getConnection(url, usr, pwd);
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
		
		while(true) {
			try {
				int letti = inputStream.read(buffer);
				if(letti > 0) {
					String scelta = new String(buffer, 0, letti);
					System.out.println(scelta);
					switch(scelta) {
					case "a":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String testo = new String(buffer, 0, letti);
						inserisciRef(testo);
						outputStream.write("ok".getBytes(), 0, "ok".length());
						break;
					case "b":
						break;
					}
									
				}else {
					so.close();
					return;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void inserisciRef(String testo) {
		try {    	
	    	//Query per inserire il referendum
	    	PreparedStatement stmt = conn.prepareStatement("INSERT INTO Referendum (testo) VALUES (?);");
	    	stmt.setString(1, testo);
	    	stmt.execute();
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
	}
	
	public void inserisciPartito() {
		
	}
	
	public void inserisciCandidati() {
		
	}
}