package server;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
import model.Votazione;

public class GestisciClient implements Runnable{
	private Socket so;
	InputStream inputStream;
	OutputStream outputStream;
	Connection conn;
	int dim_buffer;
	byte buffer[];
	int letti;
	//String votazione_conclusa;
	int id_ref;			//per sapere quale referendum è stato chiuso e poi per prendere i dati per fare il file dei risultati
	
	public GestisciClient(Socket socket) {
		try {
			dim_buffer = 100;
			buffer = new byte[dim_buffer];
			so = socket;
			inputStream = so.getInputStream();
			outputStream = so.getOutputStream();
			id_ref = -1;
			//votazione_conclusa = "null";
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
						inserisciRef();
						//outputStream.write("ok".getBytes(), 0, "ok".length());
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
					case "avvio":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						//if(Server.getVotazione().equals("null")) {
							letti = inputStream.read(buffer);
							String votazione = new String(buffer, 0, letti);
							avviaVotazione(votazione);
						/*} else {
							String messaggio = Server.getVotazione() + " già avviato";
							outputStream.write(messaggio.getBytes(), 0, messaggio.length());
						}*/
						break;
					/*case "type":
						outputStream.write(Server.getVotazione().getBytes(), 0, Server.getVotazione().length());
						break;*/
					case "domanda":
						getDomanda();
						break;
					case "partiti":
						getPartiti();
						break;
					case "vc":
						letti = inputStream.read(buffer);
						votoCategorico(new String(buffer, 0, letti));
						break;
					case "vcp":
						letti = inputStream.read(buffer);
						String s = new String(buffer, 0, letti);
						votoCategoricoPreferenze(s);
						break;
					case "scrutinio":
						getTerminate();
						break;
					case "calculated":
						mostraRisultati();
						break;
					case "end":
						terminaVotazione();
						//outputStream.write("ok".getBytes(), 0, "ok".length());
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
	
	public ArrayList<Votazione> getReferendumTerminati() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT idReferendum, nome FROM referendum WHERE attivo = ?");
		stmt.setInt(1, -1);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Votazione> ref = new ArrayList<>();
		if(!rs.next()) {
			return null;
		} else {
			do {
				ref.add(new Votazione(rs.getInt("idReferendum"), "referendum", rs.getString("nome")));
			} while(rs.next());
		}
		return ref;
	}
	
	public ArrayList<Votazione> getVotazioniTerminate() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM terminate");
		ResultSet rs = stmt.executeQuery();
		ArrayList<Votazione> vot = new ArrayList<>();
		if(!rs.next()) {
			return null;
		} else {
			do {
				vot.add(new Votazione(rs.getInt("idTerminate"), rs.getString("tipo"), rs.getString("nome")));
			} while(rs.next());
		}
		return vot;
	}
	
	public void getTerminate() throws SQLException, IOException, ClassNotFoundException {
		ArrayList<Votazione> ref = getReferendumTerminati();
		ArrayList<Votazione> vot = getVotazioniTerminate();
		if(ref == null && vot == null) {
			outputStream.write("no".getBytes(), 0, "no".length());
		} else {
			outputStream.write("ok".getBytes(), 0, "ok".length());
			ObjectOutputStream oout = new ObjectOutputStream(outputStream);
			if(vot == null) {
				oout.writeObject(ref);
			} else if(ref == null) {
				oout.writeObject(vot);
			} else {
				vot.addAll(ref);
				oout.writeObject(vot);
			}
			ObjectInputStream oin = new ObjectInputStream(inputStream);
			ArrayList<Votazione> list = (ArrayList<Votazione>) oin.readObject();
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getTipo().equals("Senza quorum")) {
					PreparedStatement stmt = conn.prepareStatement("SELECT * FROM referendum WHERE idReferendum = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					ResultSet rs = stmt.executeQuery();
					rs.next();
					String nome_t = list.get(i).getNome() + list.get(i).getId();
					stmt = conn.prepareStatement("CREATE TABLE `" + nome_t + "` (nome VARCHAR(45), testo VARCHAR(200), si INT, no INT, sb INT, vincitore VARCHAR(2))");
					stmt.execute();
					String vincitore = "No";
					if(rs.getInt("si") > rs.getInt("no")) {
						vincitore = "Sì";
					}
					stmt = conn.prepareStatement("INSERT INTO `" + nome_t + "` (nome, testo, si, no, sb, vincitore) VALUES (?, ?, ?, ?, ?, ?)");
					stmt.setString(1, rs.getString("nome"));
					stmt.setString(2, rs.getString("testo"));
					stmt.setInt(3, rs.getInt("si"));
					stmt.setInt(4, rs.getInt("no"));
					stmt.setInt(5, rs.getInt("sb"));
					stmt.setString(6, vincitore);
					stmt.execute();
					stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo) VALUES (?, ?)");
					stmt.setString(1, list.get(i).getNome() + "@" + list.get(i).getId());
					stmt.setString(2, list.get(i).getTipo());
					stmt.execute();
					stmt = conn.prepareStatement("DELETE FROM referendum WHERE idReferendum = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					stmt.execute();
				} else if(list.get(i).getTipo().equals("Con quorum")) {
					PreparedStatement stmt = conn.prepareStatement("SELECT * FROM referendum WHERE idReferendum = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					ResultSet rs = stmt.executeQuery();
					rs.next();
					String nome_t = list.get(i).getNome() + list.get(i).getId();
					stmt = conn.prepareStatement("SELECT COUNT(*) AS votanti FROM utenti WHERE type = ?");
					stmt.setString(1, "elettore");
					ResultSet rs1 = stmt.executeQuery();
					rs1.next();
					stmt = conn.prepareStatement("CREATE TABLE `" + nome_t + "` (nome VARCHAR(45), testo VARCHAR(200), si INT, no INT, sb INT, vincitore VARCHAR(20))");
					stmt.execute();
					String vincitore = "No";
					if(rs.getInt("si") > rs.getInt("no")) {
						vincitore = "Sì";
					}
					int sum = rs.getInt("si") + rs.getInt("no") + rs.getInt("sb");
					if((rs1.getInt("votanti") / 2) >= sum) {
						vincitore = "Quorum non raggiunto";
					}
					stmt = conn.prepareStatement("INSERT INTO `" + nome_t + "` (nome, testo, si, no, sb, vincitore) VALUES (?, ?, ?, ?, ?, ?)");
					stmt.setString(1, rs.getString("nome"));
					stmt.setString(2, rs.getString("testo"));
					stmt.setInt(3, rs.getInt("si"));
					stmt.setInt(4, rs.getInt("no"));
					stmt.setInt(5, rs.getInt("sb"));
					stmt.setString(6, vincitore);
					stmt.execute();
					stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo) VALUES (?, ?)");
					stmt.setString(1, list.get(i).getNome() + "@" + list.get(i).getId());
					stmt.setString(2, list.get(i).getTipo());
					stmt.execute();
					stmt = conn.prepareStatement("DELETE FROM referendum WHERE idReferendum = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					stmt.execute();
				} else if(list.get(i).getTipo().equals("Maggioranza")) {
					//TODO da rifare
					String nome_tab = list.get(i).getNome() + list.get(i).getId();
					//String nome_view = list.get(i).getId() + list.get(i).getNome();
					/*
					PreparedStatement stmt = conn.prepareStatement("CREATE VIEW `" + nome_view + "` AS SELECT id, nome, voto FROM " + nome_tab);
					stmt.execute();
					*/
					PreparedStatement stmt = conn.prepareStatement("DELETE FROM terminate WHERE idTerminate = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					stmt.execute();
					stmt = conn.prepareStatement("INSERT INTO calcolare (nomeVot, tipoCalcolo) VALUES (?, ?)");
					stmt.setString(1, nome_tab);
					stmt.setString(2, list.get(i).getTipo());
					stmt.execute();
				} else if(list.get(i).getTipo().equals("Maggioranza assoluta")) {

				}
			}
		}
	}
	
	//da mergiare con getDomanda()
	public ArrayList<Votazione> getReferendumAttivi() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT idReferendum, nome FROM referendum WHERE attivo = ?");
		stmt.setInt(1, 1);
		ResultSet rs = stmt.executeQuery();
		ArrayList<Votazione> ref = new ArrayList<>();
		if(!rs.next()) {
			return null;
		} else {
			do {
				ref.add(new Votazione(rs.getInt("idReferendum"), "referendum", rs.getString("nome")));
			} while(rs.next());
		}
		return ref;
	}
	
	public ArrayList<Votazione> getVotazioniAttive() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM attive");
		ResultSet rs = stmt.executeQuery();
		ArrayList<Votazione> vot = new ArrayList<>();
		if(!rs.next()) {
			return null;
		} else {
			do {
				vot.add(new Votazione(rs.getInt("idAttive"), rs.getString("tipo"), rs.getString("nome")));
			} while(rs.next());
		}
		return vot;
	}
	
	public void terminaVotazione() throws SQLException, IOException, ClassNotFoundException {
		ArrayList<Votazione> ref = getReferendumAttivi();
		ArrayList<Votazione> vot = getVotazioniAttive();
		if(ref == null && vot == null) {
			outputStream.write("no".getBytes(), 0, "no".length());
		} else {
			outputStream.write("ok".getBytes(), 0, "ok".length());
			ObjectOutputStream oout = new ObjectOutputStream(outputStream);
			if(vot == null) {
				oout.writeObject(ref);
			} else if(ref == null) {
				oout.writeObject(vot);
			} else {
				vot.addAll(ref);
				oout.writeObject(vot);
			}
			ObjectInputStream oin = new ObjectInputStream(inputStream);
			ArrayList<Votazione> list = (ArrayList<Votazione>) oin.readObject();
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getTipo().equals("referendum")) {
					PreparedStatement stmt = conn.prepareStatement("UPDATE referendum SET attivo = ? WHERE idReferendum = ?;");
		    		stmt.setInt(1, -1);
		    		stmt.setInt(2, list.get(i).getId());
			    	stmt.execute();
				} else {
					PreparedStatement stmt = conn.prepareStatement("INSERT INTO terminate (idTerminate, nome, tipo) VALUES (?, ?, ?)");
		    		stmt.setInt(1, list.get(i).getId());
		    		stmt.setString(2, list.get(i).getNome());
		    		stmt.setString(3, list.get(i).getTipo());
			    	stmt.execute();
			    	stmt = conn.prepareStatement("DELETE FROM attive WHERE idAttive = ? AND nome = ?");
			    	stmt.setInt(1, list.get(i).getId());
		    		stmt.setString(2, list.get(i).getNome());
		    		stmt.execute();
				}
			}
		}
	}
	
	public void avviaVotazione(String votazione) throws IOException, SQLException {
		if(votazione.equals("Referendum")) {
			//outputStream.write("ok".getBytes(), 0, "ok".length());
			PreparedStatement stmt = conn.prepareStatement("SELECT idReferendum, nome FROM Referendum WHERE attivo = ?");
			stmt.setInt(1, 0);
    		ResultSet rs = stmt.executeQuery();
    		if(!rs.next()) {
    			outputStream.write("no".getBytes(), 0, "no".length());
    		} else {
    			outputStream.write("ok".getBytes(), 0, "ok".length());
    			List<Votazione> lista = new ArrayList<>();
    			ObjectOutputStream out = new ObjectOutputStream(outputStream);
    			do {
    				lista.add(new Votazione(rs.getInt("idReferendum"), "referendum", rs.getString("nome")));
    			}while(rs.next());
				out.writeObject(lista);
				letti = inputStream.read(buffer);
				String risposta = new String(buffer, 0, letti);
				if(risposta.equals("esc")) {
					return;
				} else {
					int id = Integer.parseInt(risposta);
					stmt = conn.prepareStatement("UPDATE referendum  SET attivo = ? WHERE idReferendum = ?;");
		    		stmt.setInt(1, 1);
		    		stmt.setInt(2, id);
			    	stmt.execute();
			    	//Server.setVotazione(votazione);
					//outputStream.write("ok".getBytes(), 0, "ok".length());
				}
    		}
		} else if(!votazione.equals("Referendum")) {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Partiti");
    		ResultSet rs = stmt.executeQuery();
    		PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM Candidati");
    		ResultSet rs1 = stmt1.executeQuery();
    		if(!rs.next() || !rs1.next()) {
    			outputStream.write("no".getBytes(), 0, "no".length());
    		} else {
    			outputStream.write("ok".getBytes(), 0, "ok".length());
    			letti = inputStream.read(buffer);
				if(new String(buffer, 0, letti).equals("no")) {
					return;
				}
				getPartiti();
				boolean done = false;
				String nome_t = "";
				while(true) {
					letti = inputStream.read(buffer);
					String risposta = new String(buffer, 0, letti);
					if(risposta.equals("esc")) {
						return;
					} else {
						String[] v = risposta.split(",");
						if(!done) {
							stmt = conn.prepareStatement("INSERT INTO attive (nome, tipo) VALUES (?, ?)");
							stmt.setString(1, v[0]);
							stmt.setString(2, v[1]);
					    	stmt.execute();
					    	stmt = conn.prepareStatement("SELECT * FROM attive WHERE nome = ?");
							stmt.setString(1, v[0]);
				    		rs = stmt.executeQuery();
				    		rs.next();
				    		nome_t = rs.getString("nome") + rs.getInt("idAttive");
							stmt = conn.prepareStatement("CREATE TABLE " + nome_t + " (id INT, tipo VARCHAR(9), nome VARCHAR(45), voto INT NOT NULL DEFAULT 0, PRIMARY KEY(id, tipo))");
					    	stmt.execute();
					    	done = true;
						}
						
				    	for(int i = 2; i < v.length; i++) {
				    		stmt = conn.prepareStatement("INSERT INTO " + nome_t + " (id, tipo, nome) VALUES (?, ?, ?)");
							stmt.setInt(1, Integer.parseInt(v[i]));
							if(i == 2) {
								PreparedStatement stmt_nome = conn.prepareStatement("SELECT nome FROM partiti WHERE idPartito = ?");
								stmt_nome.setInt(1, Integer.parseInt(v[i]));
					    		ResultSet rs2 = stmt_nome.executeQuery();
					    		rs2.next();
					    		String nome = rs2.getString("nome");
								stmt.setString(2, "partito");
								stmt.setString(3, nome);
							} else {
								PreparedStatement stmt_nome = conn.prepareStatement("SELECT nome FROM candidati WHERE idCandidato = ?");
								stmt_nome.setInt(1, Integer.parseInt(v[i]));
								ResultSet rs2 = stmt_nome.executeQuery();
					    		rs2.next();
					    		String nome = rs2.getString("nome");
								stmt.setString(2, "candidato");
								stmt.setString(3, nome);
							}
					    	stmt.execute();
				    	}
				    	
					}
				}
    		}
		}
		return;
	}
	
	public void inserisciRef() {
		try {    	
			letti = inputStream.read(buffer);
			String testo = new String(buffer, 0, letti);
			if(testo.equals("no")) 
				return;
			String v[] = testo.split(",");
	    	//Query per inserire il referendum
	    	PreparedStatement stmt = conn.prepareStatement("INSERT INTO Referendum (testo, nome) VALUES (?, ?);");
	    	stmt.setString(1, v[0]);
	    	stmt.setString(2, v[1]);
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
			}
			else if(voto.equals("si")){
				PreparedStatement stmt = conn.prepareStatement("UPDATE referendum SET si = si + ? WHERE attivo = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, 1);
		    	stmt.execute();
			}
			else{
				PreparedStatement stmt = conn.prepareStatement("UPDATE referendum SET sb = sb + ? WHERE attivo = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, 1);
		    	stmt.execute();
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
    		PreparedStatement stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE nome = ?");
    		stmt.setString(1, partito);
    		ResultSet rs = stmt.executeQuery();
    		if(rs.next()) {
    			id = rs.getInt("idPartito");
    		} else {
    			stmt = conn.prepareStatement("INSERT INTO Partiti (nome) VALUES (?)");
        		stmt.setString(1, partito);
        		stmt.execute();
        		
        		//Query per prendere l'id del partito appena inserito
        		stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE nome = ?");
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
			PreparedStatement stmt = conn.prepareStatement("SELECT idCandidato FROM candidati WHERE nome = ?");
    		stmt.setString(1, c[i]);
    		ResultSet rs = stmt.executeQuery();
    		if(!rs.next()) {
    			stmt = conn.prepareStatement("INSERT INTO Candidati (nome, idPartito) VALUES (?, ?)");
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
		PreparedStatement stmt = conn.prepareStatement("SELECT idPartito, nome FROM partiti");
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			//TODO
		} else {
			ArrayList<Partito> partiti = new ArrayList<>();
			do {
				idp = rs.getInt("idPartito");
				PreparedStatement stmt1 = conn.prepareStatement("SELECT idCandidato, nome FROM candidati WHERE idPartito = ?");
				stmt1.setInt(1, idp);
				ResultSet rs1 = stmt1.executeQuery();
				ArrayList<Candidato> candidati = new ArrayList<>();
				if(!rs1.next()) {
					//TODO
				} else {
					do {
						candidati.add(new Candidato(rs1.getInt("idCandidato"), rs1.getString("nome")));
					} while(rs1.next());
				}
				partiti.add(new Partito(idp, rs.getString("nome"), candidati));
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
	
	public void mostraRisultati() throws SQLException, IOException {
		PreparedStatement stmt = conn.prepareStatement("SELECT nomeVot FROM calcolate");
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			outputStream.write("no".getBytes(), 0, "no".length());
		} else {
			ArrayList<String> vot= new ArrayList<>(); 
			outputStream.write("ok".getBytes(), 0, "ok".length());
			ObjectOutputStream oout = new ObjectOutputStream(outputStream);
			do {
				vot.add(rs.getString("nomeVot"));
			} while(rs.next());
			oout.writeObject(vot);
			letti = inputStream.read(buffer);
			String reply = new String(buffer, 0, letti);
			if(!reply.equals("no")) {
				stmt = conn.prepareStatement("SELECT * FROM `" + reply +"`");
				ResultSet rs1 = stmt.executeQuery();
				rs1.next();
				String s = rs1.getString("nome") + "@" + rs1.getString("testo") + "@" + rs1.getInt("si") + "@" + rs1.getInt("no") + "@" + rs1.getInt("sb") + "@" + rs1.getString("vincitore");
				oout.writeObject(s);
			}
		}
	}
}