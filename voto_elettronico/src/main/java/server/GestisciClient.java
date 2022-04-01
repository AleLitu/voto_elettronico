package server;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
				if(so.isInputShutdown()) {

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
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String partito = new String(buffer, 0, letti);
						inserisciPartito(partito);
						break;
					case "c":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String voto = new String(buffer, 0, letti);
						inserisciRefVoto(voto);
						break;
					}
									
				}else {
					so.close();
					return;
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
	
	public void inserisciRefVoto(String voto) {
		try {
			if(voto.equals("no")) {
				//Query per inserire il no al referendum
	    		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Referendum (Voto) (?);");
	    		stmt.setString(1, "No");
	    		ResultSet rs = stmt.executeQuery();
			}
			else {
				//Query per inserire il si al referendum
	    		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Referendum (Voto) (?);");
	    		stmt.setString(1, "Si");
	    		ResultSet rs = stmt.executeQuery();
			}
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
	}
	
	public void inserisciPartito(String partito) {
		int dim_buffer = 500;
		byte buffer[] = new byte[dim_buffer];
		int id;
    	try {    		
    		//Query per inserire il partito
    		PreparedStatement stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE partito = ?");
    		stmt.setString(1, partito);
    		ResultSet rs = stmt.executeQuery();
    		if(rs.next()) {
    			id = rs.getInt("idPartito");
    		} else {
    			stmt = conn.prepareStatement("INSERT INTO Partiti (partito) VALUES (?)");
        		stmt.setString(1, partito);
        		stmt.execute();
        		
        		//Query per prendere l'id del partito appena inserito
        		stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE partito = ?");
        		stmt.setString(1, partito);
        		rs = stmt.executeQuery();
        		rs.next();
        		id = rs.getInt("idPartito");
    		}
    		outputStream.write("ok".getBytes(), 0, "ok".length());
			int letti = inputStream.read(buffer);
			String candidati = new String(buffer, 0, letti);
			inserisciCandidati(id, candidati);
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}		
	}
	
	public void inserisciCandidati(int id, String candidati) {
		try {
    	String[] c = candidati.split(", ");
    	int count = 0;
		//Ciclo con query per inserire i vari candidati di quel partito
		for(int i = 0; i < c.length; i++) {
			PreparedStatement stmt = conn.prepareStatement("SELECT idCandidato FROM candidati WHERE candidato = ?");
    		stmt.setString(1, c[i]);
    		ResultSet rs = stmt.executeQuery();
    		if(!rs.next()) {
    			stmt = conn.prepareStatement("INSERT INTO Candidati (candidato, idPartito) VALUES (?, ?)");
        		stmt.setString(1, c[i]);
        		stmt.setInt(2, id);
        		stmt.execute();
    		} else {
    			count++;
    		}
		}
		outputStream.write(Integer.toString(count).getBytes(), 0, Integer.toString(count).length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}