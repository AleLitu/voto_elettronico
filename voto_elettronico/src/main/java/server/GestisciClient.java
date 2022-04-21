package server;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Candidato;
import model.Partito;
import model.Referendum;

public class GestisciClient implements Runnable{
	private Socket so;
	InputStream inputStream;
	OutputStream outputStream;
	Connection conn;
	int dim_buffer;
	byte buffer[];
	int letti;
	
	public GestisciClient(Socket socket) {
		try {
			dim_buffer = 100;
			buffer = new byte[dim_buffer];
			so = socket;
			inputStream = so.getInputStream();
			outputStream = so.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
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
				//System.out.println("0");
				//if(so.isInputShutdown()) {
					//System.out.println("1");

				letti = inputStream.read(buffer);
				if(letti > 0) {
					String scelta = new String(buffer, 0, letti);
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
						String id = new String(buffer, 0, letti);
						inserisciVotato(Integer.parseInt(id));
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String voto = new String(buffer, 0, letti);
						inserisciRefVoto(voto);
						break;
					case "d":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						if(Server.getVotazione().equals("null")) {
							letti = inputStream.read(buffer);
							String votazione = new String(buffer, 0, letti);
							avviaVotazione(votazione);
						} else {
							String messaggio = Server.getVotazione() + " già avviato";
							outputStream.write(messaggio.getBytes(), 0, messaggio.length());
						}
						break;
					case "type":
						outputStream.write(Server.getVotazione().getBytes(), 0, Server.getVotazione().length());
						break;
					case "domanda":
						getDomanda();
						break;
					case "partiti":
						getPartiti();
						break;
					case "vc":
						letti = inputStream.read(buffer);
						String votazione = new String(buffer, 0, letti);
						votoCategorico(votazione);
						break;
					case "vcp":
						letti = inputStream.read(buffer);
						String s = new String(buffer, 0, letti);
						votoCategoricoPreferenze(s);
						break;
					case "end":
						Server.setVotazione("null");
						outputStream.write("ok".getBytes(), 0, "ok".length());
					}
									
				}else {
					so.close();
					return;
				}
				/*}else {
					so.close();
					return;
				}*/
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void avviaVotazione(String votazione) throws IOException, SQLException {
		if(votazione.equals("Referendum")) {
			outputStream.write("ok".getBytes(), 0, "ok".length());
			PreparedStatement stmt = conn.prepareStatement("SELECT idReferendum, testo FROM Referendum");
    		ResultSet rs = stmt.executeQuery();
    		if(!rs.next()) {
    			outputStream.write("Inserire prima un referendum".getBytes(), 0, "Inserire prima un referendum".length());
    		} else {
    			List<Referendum> lista = new ArrayList<>();
    			ObjectOutputStream out = new ObjectOutputStream(outputStream);
    			do {
    				lista.add(new Referendum(rs.getInt("idReferendum"), rs.getString("testo")));
    			}while(rs.next());
				out.writeObject(lista);
				letti = inputStream.read(buffer);
				int id = Integer.parseInt(new String(buffer, 0, letti));
				stmt = conn.prepareStatement("UPDATE referendum  SET attivo = ? WHERE idReferendum = ?;");
	    		stmt.setInt(1, 1);
	    		stmt.setInt(2, id);
		    	stmt.execute();
		    	Server.setVotazione(votazione);
				//outputStream.write("ok".getBytes(), 0, "ok".length());
    		}
		} else if(!votazione.equals("Referendum")) {
			PreparedStatement stmt = conn.prepareStatement("SELECT idPartito FROM Partiti");
    		ResultSet rs = stmt.executeQuery();
    		PreparedStatement stmt1 = conn.prepareStatement("SELECT idCandidato FROM Candidati");
    		ResultSet rs1 = stmt1.executeQuery();
    		if(!rs.next() || !rs1.next()) {
    			outputStream.write("Inserire prima partiti e candidati".getBytes(), 0, "Inserire prima partiti e candidati".length());
    		} else {
				Server.setVotazione(votazione);
				outputStream.write("ok".getBytes(), 0, "ok".length());
    		}
		}
		return;
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
	
	public void inserisciVotato(int id) {
		try {    	
	    	//Query per inserire che un utente ha votato
	    	PreparedStatement stmt = conn.prepareStatement("UPDATE utenti SET votato = TRUE WHERE id = ?;");
	    	stmt.setInt(1, id);
	    	stmt.execute();
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
	}
	
	public void inserisciRefVoto(String voto) {	
		try {
			if(voto.equals("no")) {
				PreparedStatement stmt = conn.prepareStatement("UPDATE referendum SET no = no + ? WHERE attivo = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, 1);
		    	stmt.execute();
		    	/*
				PreparedStatement stmt = conn.prepareStatement("SELECT no FROM referendum");
				ResultSet rs = stmt.executeQuery();
				rs.next();
				String no = rs.getString("no");
				//Query per inserire il no al referendum
	    		stmt = conn.prepareStatement("INSERT INTO referendum (no) VALUES (?);");
	    		stmt.setString(1, no + 1);
		    	stmt.execute();*/
			}
			else if(voto.equals("si")){
				PreparedStatement stmt = conn.prepareStatement("UPDATE referendum SET si = si + ? WHERE attivo = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, 1);
		    	stmt.execute();
		    	/*
				PreparedStatement stmt = conn.prepareStatement("SELECT si FROM referendum");
				ResultSet rs = stmt.executeQuery();
				rs.next();
				String si = rs.getString("si");
				//Query per inserire il si al referendum
	    		stmt = conn.prepareStatement("INSERT INTO referendum (si) VALUES (?);");
	    		stmt.setString(1, si + 1);
		    	stmt.execute();*/
			}
			else{
				PreparedStatement stmt = conn.prepareStatement("UPDATE referendum SET sb = sb + ? WHERE attivo = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, 1);
		    	stmt.execute();
		    	/*
				PreparedStatement stmt = conn.prepareStatement("SELECT sb FROM referendum");
				ResultSet rs = stmt.executeQuery();
				rs.next();
				String sb = rs.getString("no");
				//Query per inserire il sb al referendum
	    		stmt = conn.prepareStatement("INSERT INTO referendum (sb) VALUES (?);");
	    		stmt.setString(1, sb + 1);
		    	stmt.execute();*/
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
    	boolean count = false;
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
    			count = true;
    		}
		}
		if(count)
			outputStream.write("true".getBytes(), 0, "true".length());
		else
			outputStream.write("false".getBytes(), 0, "false".length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDomanda() throws SQLException, IOException {
		PreparedStatement stmt = conn.prepareStatement("SELECT idReferendum, testo FROM referendum WHERE attivo = ?");
		stmt.setInt(1, 1);
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			//TODO
		} else {
			Referendum re = new Referendum(rs.getInt("idReferendum"), rs.getString("testo"));
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(re);
		}
	}
	public void getPartiti() throws SQLException, IOException {
		int idp;
		PreparedStatement stmt = conn.prepareStatement("SELECT idPartito, partito FROM partiti");
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			//TODO
		} else {
			ArrayList<Partito> partiti = new ArrayList<>();
			do {
				idp = rs.getInt("idPartito");
				PreparedStatement stmt1 = conn.prepareStatement("SELECT idCandidato, candidato FROM candidati WHERE idPartito = ?");
				stmt1.setInt(1, idp);
				ResultSet rs1 = stmt1.executeQuery();
				ArrayList<Candidato> candidati = new ArrayList<>();
				if(!rs1.next()) {
					//TODO
				} else {
					do {
						candidati.add(new Candidato(rs1.getInt("idCandidato"), rs1.getString("Candidato")));
					} while(rs1.next());
				}
				partiti.add(new Partito(idp, rs.getString("partito"), candidati));
			}while(rs.next());
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(partiti);
		}
	}
	
	public void votoCategorico(String votazione) throws SQLException {
		String[] v = votazione.split(",");
		if(v[0].equals("partito")) {
			PreparedStatement stmt = conn.prepareStatement("UPDATE partiti SET voto = voto + ? WHERE idPartito = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(v[1]));
	    	stmt.execute();
		} else if(v[0].equals("candidato")) {
			PreparedStatement stmt = conn.prepareStatement("UPDATE candidati SET voto = voto + ? WHERE idCandidato = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(v[1]));
	    	stmt.execute();
		}
	}
	
	public void votoCategoricoPreferenze(String s) throws SQLException {
		if(!s.contains(",")) {
			PreparedStatement stmt = conn.prepareStatement("UPDATE partiti SET voto = voto + ? WHERE idPartito = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(s));
	    	stmt.execute();
		} else {
			String[] v = s.split(",");
			PreparedStatement stmt = conn.prepareStatement("UPDATE partiti SET voto = voto + ? WHERE idPartito = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(v[0]));
	    	stmt.execute();
    		for(int i = 1; i < v.length; i++) {
    			stmt = conn.prepareStatement("UPDATE candidati SET voto = voto + ? WHERE idCandidato = ?");
    			stmt.setInt(1, 1);
    			stmt.setInt(2, Integer.parseInt(v[i]));
    	    	stmt.execute();
    		}
		}
	}
}