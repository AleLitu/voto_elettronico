package server;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import model.Candidato;
import model.Partito;
import model.Referendum;
import model.Votazione;

public class GestisciClient implements Runnable{
	private Socket so;
	InputStream inputStream;
	OutputStream outputStream;
	DataInputStream dis;
	Connection conn;
	int dim_buffer;
	byte buffer[];
	byte[] cipherData;
	int letti;
	Cipher cipher;
	
	public GestisciClient(Socket socket) {
		try {
			dim_buffer = 100;
			buffer = new byte[dim_buffer];
			so = socket;
			inputStream = so.getInputStream();
			outputStream = so.getOutputStream();
			cipher = Cipher.getInstance("RSA");
		} catch (Exception e) {
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
					case "ref":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						//letti = inputStream.read(buffer);
						//String id = new String(buffer, 0, letti);
						//inserisciVotato(Integer.parseInt(id));
						//outputStream.write("ok".getBytes(), 0, "ok".length());
						dis = new DataInputStream(inputStream);
					    letti = dis.readInt();
					    cipherData = new byte[letti];
					    dis.readFully(cipherData);
						cipher.init(Cipher.DECRYPT_MODE, Server.getPrivateKey());
						String voto = new String(cipher.doFinal(cipherData), StandardCharsets.UTF_8);
						if(!voto.equals("err")) {
							inserisciRefVoto(voto);
						}
						break;
					case "avvio":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String votazione = new String(buffer, 0, letti);
						avviaVotazione(votazione);
						break;
					case "attive":
						getAttive();
						break;
					case "candidati":
						getCandidati();
						break;
					case "votazione":
						getVotazione();
						break;
					case "vc":
						dis = new DataInputStream(inputStream);
					    letti = dis.readInt();
					    cipherData = new byte[letti];
					    dis.readFully(cipherData);
						cipher.init(Cipher.DECRYPT_MODE, Server.getPrivateKey());
						voto = new String(cipher.doFinal(cipherData), StandardCharsets.UTF_8);
						if(!voto.equals("err")) {
							votoCategorico(voto);
						}
						break;
					case "vcp":
						dis = new DataInputStream(inputStream);
					    letti = dis.readInt();
					    cipherData = new byte[letti];
					    dis.readFully(cipherData);
						cipher.init(Cipher.DECRYPT_MODE, Server.getPrivateKey());
						voto = new String(cipher.doFinal(cipherData), StandardCharsets.UTF_8);
						if(!voto.equals("err")) {
							votoCategoricoPreferenze(voto);
						}
						break;
					case "vo":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String voti = new String(buffer, 0, letti);
						outputStream.write("ok".getBytes(), 0, "ok".length());
						votoOrdinale(voti);
						LogHandler.writeLog("Voto ricevuto");
						break;	
					case "scrutinio":
						getTerminate();
						break;
					case "calculated":
						mostraRisultati();
						break;
					case "end":
						terminaVotazione();
						break;
					case "logout":
						inputStream.close();
						outputStream.close();
						so.close();
						LogHandler.writeLog("Client disconnesso");
						return;
					}				
				} else {
					so.close();
					return;
				}
			} catch(SocketException se) {
				return;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getCandidati() throws SQLException, IOException {
		PreparedStatement stmt = conn.prepareStatement("SELECT idCandidato, nome FROM candidati ORDER BY nome");
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			//TODO
		} else {
			ArrayList<Candidato> candidati = new ArrayList<>();
			do {
				candidati.add(new Candidato(rs.getInt("idCandidato"), rs.getString("nome")));
			}while(rs.next());
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(candidati);
		}
	}
	
	public void votoOrdinale(String voto) throws SQLException, IOException, ClassNotFoundException {
		outputStream.write("ok".getBytes(), 0, "ok".length());
		if(voto.equals("partiti")) {
			List<Partito> voti;
			ObjectInputStream oin = new ObjectInputStream(inputStream);
	        voti = (List<Partito>) oin.readObject();
			outputStream.write("ok".getBytes(), 0, "ok".length());
			for(int i = 0; i < voti.size(); i++) {
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO voto_partito (idPartito, voto) VALUES (?, ?)");
				stmt.setInt(1, voti.get(i).getId());
				stmt.setInt(2, i + 1);
		    	stmt.execute();
			}
		}else {
			List<Candidato> voti;
			ObjectInputStream oin = new ObjectInputStream(inputStream);
	        voti = (List<Candidato>) oin.readObject();
			outputStream.write("ok".getBytes(), 0, "ok".length());
			System.out.println(voti.size());
			for(int i = 0; i < voti.size(); i++) {
				System.out.println("1");
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO voto_candidato (idCandidato, voto) VALUES (?, ?)");
				stmt.setInt(1, voti.get(i).getId());
				stmt.setInt(2, i + 1);
		    	stmt.execute();
			}
		}
	}
	
	//invia la votazione selezionata dal client per votare
	public void getVotazione() throws IOException, SQLException {
		letti = inputStream.read(buffer);
		String[] v = new String(buffer, 0, letti).split("@");
		if(!v[2].equals("referendum")) {
			String nome_t = v[0] + v[1];
			PreparedStatement stmt = conn.prepareStatement("SELECT id, nome FROM " + nome_t + " WHERE tipo = ?");
			stmt.setString(1, "partito");
			ResultSet rs = stmt.executeQuery();
			ArrayList<Partito> partiti = new ArrayList<>();
			while(rs.next()) {
				PreparedStatement stmt1 = conn.prepareStatement("SELECT id, " + nome_t + ".nome FROM " + nome_t + " INNER JOIN candidati ON id = idCandidato WHERE idPartito = ? AND tipo = ?");
				stmt1.setInt(1, rs.getInt("id"));
				stmt1.setString(2, "candidato");
				ResultSet rs1 = stmt1.executeQuery();
				ArrayList<Candidato> candidati = new ArrayList<>();
				while(rs1.next()) {
					candidati.add(new Candidato(rs1.getInt("id"), rs1.getString("nome")));
				}
				partiti.add(new Partito(rs.getInt("id"), rs.getString("nome"), candidati));
			}
			ObjectOutputStream oout = new ObjectOutputStream(outputStream);
			oout.writeObject(nome_t.replaceAll("\\d",""));
			oout.flush();
			ObjectOutputStream pubkey = new ObjectOutputStream(outputStream);
			pubkey.writeObject(Server.getPublicKey());
			oout = new ObjectOutputStream(outputStream);
			oout.writeObject(partiti);
		} else {
			getDomanda(Integer.parseInt(v[1]), v[0]);
		}
	}
	
	//invia la lista delle votazioni attive al client per selezionare quale votare
	public void getAttive() throws SQLException, IOException {
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
					stmt = conn.prepareStatement("CREATE TABLE `" + nome_t + "` (nome VARCHAR(45), testo VARCHAR(200), si VARCHAR(20), no VARCHAR(20), sb VARCHAR(20), vincitore VARCHAR(2))");
					stmt.execute();
					String vincitore = "No";
					if(rs.getInt("si") > rs.getInt("no")) {
						vincitore = "Sì";
					}
					float tot = rs.getInt("si") + rs.getInt("no") + rs.getInt("sb");
					float perc_si = (rs.getInt("si") / tot) * 100;
					float perc_no = (rs.getInt("no") / tot) * 100;
					float perc_sb = (rs.getInt("sb") / tot) * 100;
					stmt = conn.prepareStatement("INSERT INTO `" + nome_t + "` (nome, testo, si, no, sb, vincitore) VALUES (?, ?, ?, ?, ?, ?)");
					stmt.setString(1, rs.getString("nome"));
					stmt.setString(2, rs.getString("testo"));
					stmt.setString(3, rs.getInt("si") + "%" + perc_si);
					stmt.setString(4, rs.getInt("no") + "%" + perc_no);
					stmt.setString(5, rs.getInt("sb") + "%" + perc_sb);
					stmt.setString(6, vincitore);
					stmt.execute();
					stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo, tipoVot) VALUES (?, ?, ?)");
					stmt.setString(1, list.get(i).getNome() + "@" + list.get(i).getId());
					stmt.setString(2, list.get(i).getTipo());
					stmt.setString(3, "referendum");
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
					stmt = conn.prepareStatement("CREATE TABLE `" + nome_t + "` (nome VARCHAR(45), testo VARCHAR(200), si VARCHAR(20), no VARCHAR(20), sb VARCHAR(20), vincitore VARCHAR(30))");
					stmt.execute();
					String vincitore = "No";
					if(rs.getInt("si") > rs.getInt("no")) {
						vincitore = "Sì";
					}
					float tot = rs.getInt("si") + rs.getInt("no") + rs.getInt("sb");
					float perc_si = (rs.getInt("si") / tot) * 100;
					float perc_no = (rs.getInt("no") / tot) * 100;
					float perc_sb = (rs.getInt("sb") / tot) * 100;
					float perc_quo = (tot / rs1.getInt("votanti")) * 100;
					if((rs1.getInt("votanti") / 2) >= tot) {
						vincitore = "Quorum non raggiunto%" + perc_quo;
					}
					stmt = conn.prepareStatement("INSERT INTO `" + nome_t + "` (nome, testo, si, no, sb, vincitore) VALUES (?, ?, ?, ?, ?, ?)");
					stmt.setString(1, rs.getString("nome"));
					stmt.setString(2, rs.getString("testo"));
					stmt.setString(3, rs.getInt("si") + "%" + perc_si);
					stmt.setString(4, rs.getInt("no") + "%" + perc_no);
					stmt.setString(5, rs.getInt("sb") + "%" + perc_sb);
					stmt.setString(6, vincitore);
					stmt.execute();
					stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo, tipoVot) VALUES (?, ?, ?)");
					stmt.setString(1, list.get(i).getNome() + "@" + list.get(i).getId());
					stmt.setString(2, list.get(i).getTipo());
					stmt.setString(3, "referendum");
					stmt.execute();
					stmt = conn.prepareStatement("DELETE FROM referendum WHERE idReferendum = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					stmt.execute();
				} else if(list.get(i).getTipo().equals("Maggioranza")) {
					String nome_tab = list.get(i).getNome() + list.get(i).getId();
					PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + nome_tab);
					ResultSet rs = stmt.executeQuery();
					rs.next();
					stmt = conn.prepareStatement("SELECT tipo FROM terminate WHERE idTerminate = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					ResultSet rs1 = stmt.executeQuery();
					rs1.next();
					String tipo_vot = rs1.getString("tipo");
					ArrayList<Candidato> idp_max = new ArrayList<>();
					ArrayList<Candidato> idc_max = new ArrayList<>();
					ArrayList<String> messaggio = new ArrayList<>();
					messaggio.add(list.get(i).getNome());
					int max_p = 0;
					while(rs.getString("tipo").equals("partito")) {
						rs.getString("nome");
						if(rs.getInt("voto") >= max_p) {
							if(rs.getInt("voto") > max_p) {
								idp_max.clear();
								idp_max.add(new Candidato(rs.getInt("id"), rs.getString("nome")));
							} else {
								idp_max.add(new Candidato(rs.getInt("id"), rs.getString("nome")));
							}
							max_p = rs.getInt("voto");
						}
						rs.next();
					}
					if(!tipo_vot.equals("categorico")) {
						int max_c = 0;
						stmt = conn.prepareStatement("SELECT id, " + nome_tab + ".nome, voto FROM " + nome_tab + " INNER JOIN candidati ON id = idCandidato WHERE idPartito = ?");
						stmt.setInt(1, idp_max.get(i).getId());
						rs1 = stmt.executeQuery();
						while(rs1.next()) {
							if(rs1.getInt("voto") >= max_c) {
								if(rs1.getInt("voto") > max_c) {
									idc_max.clear();
									idc_max.add(new Candidato(rs1.getInt("id"), rs1.getString("nome")));
									
								} else {
									idc_max.add(new Candidato(rs1.getInt("id"), rs1.getString("nome")));
								}
								max_c = rs1.getInt("voto");
							}
						}
						
					}
					stmt = conn.prepareStatement("ALTER TABLE " + nome_tab + " ADD vincitore INT NOT NULL DEFAULT 0");
					stmt.execute();
					for(int j = 0; j < idp_max.size(); j++) {
						stmt = conn.prepareStatement("UPDATE " + nome_tab + " SET vincitore = ? WHERE id = ? AND nome = ?;");
						stmt.setInt(1, 1);
						stmt.setInt(2, idp_max.get(j).getId());
						stmt.setString(3, idp_max.get(j).getNome());
						stmt.execute();
					}
					if(!tipo_vot.equals("categorico")) {
						for(int j = 0; j < idc_max.size(); j++) {
							stmt = conn.prepareStatement("UPDATE " + nome_tab + " SET vincitore = ? WHERE id = ? AND nome = ?;");
							stmt.setInt(1, 1);
							stmt.setInt(2, idc_max.get(j).getId());
							stmt.setString(3, idc_max.get(j).getNome());
							stmt.execute();
						}
					}
					stmt = conn.prepareStatement("DELETE FROM terminate WHERE idTerminate = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					stmt.execute();
					stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo, tipoVot) VALUES (?, ?, ?)");
					stmt.setString(1, list.get(i).getNome() + "@" + list.get(i).getId());
					stmt.setString(2, list.get(i).getTipo());
					stmt.setString(3, tipo_vot);
					stmt.execute();
					
				} else if(list.get(i).getTipo().equals("Maggioranza assoluta")) {
					String nome_tab = list.get(i).getNome() + list.get(i).getId();
					PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + nome_tab);
					ResultSet rs = stmt.executeQuery();
					rs.next();
					stmt = conn.prepareStatement("SELECT tipo FROM terminate WHERE idTerminate = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					ResultSet rs1 = stmt.executeQuery();
					rs1.next();
					String tipo_vot = rs1.getString("tipo");
					ArrayList<Candidato> idp_max = new ArrayList<>();
					ArrayList<Candidato> idc_max = new ArrayList<>();
					ArrayList<String> messaggio = new ArrayList<>();
					messaggio.add(list.get(i).getNome());
					int max_p = 0, tot_p = 0;
					while(rs.getString("tipo").equals("partito")) {
						rs.getString("nome");
						if(rs.getInt("voto") >= max_p) {
							if(rs.getInt("voto") > max_p) {
								idp_max.clear();
								idp_max.add(new Candidato(rs.getInt("id"), rs.getString("nome")));
							} else {
								idp_max.add(new Candidato(rs.getInt("id"), rs.getString("nome")));
							}
							max_p = rs.getInt("voto");
						}
						tot_p += rs.getInt("voto");
						rs.next();
					}
					stmt = conn.prepareStatement("ALTER TABLE " + nome_tab + " ADD vincitore INT NOT NULL DEFAULT 0");
					stmt.execute();
					if((tot_p / 2) < max_p) {
						if(!tipo_vot.equals("categorico")) {
							int max_c = 0;
							stmt = conn.prepareStatement("SELECT id, " + nome_tab + ".nome, voto FROM " + nome_tab + " INNER JOIN candidati ON id = idCandidato WHERE idPartito = ?");
							stmt.setInt(1, idp_max.get(i).getId());
							rs1 = stmt.executeQuery();
							while(rs1.next()) {
								if(rs1.getInt("voto") >= max_c) {
									if(rs1.getInt("voto") > max_c) {
										idc_max.clear();
										idc_max.add(new Candidato(rs1.getInt("id"), rs1.getString("nome")));
										
									} else {
										idc_max.add(new Candidato(rs1.getInt("id"), rs1.getString("nome")));
									}
									max_c = rs1.getInt("voto");
								}
							}
							
						}
						for(int j = 0; j < idp_max.size(); j++) {
							stmt = conn.prepareStatement("UPDATE " + nome_tab + " SET vincitore = ? WHERE id = ? AND nome = ?;");
							stmt.setInt(1, 1);
							stmt.setInt(2, idp_max.get(j).getId());
							stmt.setString(3, idp_max.get(j).getNome());
							stmt.execute();
						}
						if(!tipo_vot.equals("categorico")) {
							for(int j = 0; j < idc_max.size(); j++) {
								stmt = conn.prepareStatement("UPDATE " + nome_tab + " SET vincitore = ? WHERE id = ? AND nome = ?;");
								stmt.setInt(1, 1);
								stmt.setInt(2, idc_max.get(j).getId());
								stmt.setString(3, idc_max.get(j).getNome());
								stmt.execute();
							}
						}
					} else {
						PreparedStatement stmt1 = conn.prepareStatement("UPDATE " + nome_tab + " SET vincitore = ?;");
						stmt1.setInt(1, -1);
						stmt1.execute();
					}
					
					stmt = conn.prepareStatement("DELETE FROM terminate WHERE idTerminate = ? AND nome = ?");
					stmt.setInt(1, list.get(i).getId());
					stmt.setString(2, list.get(i).getNome());
					stmt.execute();
					stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo, tipoVot) VALUES (?, ?, ?)");
					stmt.setString(1, list.get(i).getNome() + "@" + list.get(i).getId());
					stmt.setString(2, list.get(i).getTipo());
					stmt.setString(3, tipo_vot);
					stmt.execute();
				}
				LogHandler.writeLog("Votazione calcolata");
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
				LogHandler.writeLog("Votazione terminata");
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
			    	LogHandler.writeLog("Nuovo referendum avviato");
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
				    	stmt = conn.prepareStatement("INSERT INTO " + nome_t + " (id, tipo, nome) VALUES (?, ?, ?)");
				    	stmt.setInt(1, -1);
				    	stmt.setString(2, "bianche");
						stmt.setString(3, "schede bianche");
						stmt.execute();
				    	LogHandler.writeLog("Nuova votazione avviata");
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
	    	LogHandler.writeLog("Nuovo referendum creato");
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
	
	public void inserisciRefVoto(String voto) throws IOException {
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
			outputStream.write("ok".getBytes(), 0, "ok".length());
			LogHandler.writeLog("Voto ricevuto");
    	}catch (Exception e) {
    		outputStream.write("err".getBytes(), 0, "err".length());
    		LogHandler.writeLog("Errore inserimento voto");
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
			LogHandler.writeLog("Nuova lista creata");
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
	
	public void getDomanda(int id, String nome) throws SQLException, IOException {
		PreparedStatement stmt = conn.prepareStatement("SELECT idReferendum, testo FROM referendum WHERE idReferendum = ? AND nome = ?");
		stmt.setInt(1, id);
		stmt.setString(2, nome);
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			//TODO
		} else {
			ObjectOutputStream pubkey = new ObjectOutputStream(outputStream);
			pubkey.writeObject(Server.getPublicKey());
			//TODO mettere anche il nome nella classe referendum così si può mostrare anche quello durante la votazione
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
	
	public void votoCategorico(String votazione) throws IOException{
		try {
			String[] voto = votazione.split(",");
			String[] tabella = voto[0].split("@");
			PreparedStatement stmt = conn.prepareStatement("UPDATE " + tabella[0]+tabella[1] + " SET voto = voto + ? WHERE id = ? AND nome = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(voto[1].split("@")[0]));
			stmt.setString(3, voto[1].split("@")[1]);
			//stmt.setString(4, "partito");
			stmt.execute();
			outputStream.write("ok".getBytes(), 0, "ok".length());
			LogHandler.writeLog("Voto ricevuto");
    	}catch (Exception e) {
    		outputStream.write("err".getBytes(), 0, "err".length());
    		LogHandler.writeLog("Errore inserimento voto");
    	}
	}
	
	public void votoCategoricoPreferenze(String s) throws IOException{
		try {
			String[] v = s.split(",");
			String nome_t = v[0].split("@")[0]+v[0].split("@")[1];
			PreparedStatement stmt = conn.prepareStatement("UPDATE "+ nome_t +" SET voto = voto + ? WHERE id = ? AND nome = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(v[1].split("@")[0]));
			stmt.setString(3, v[1].split("@")[1]);
			//stmt.setString(4, "partito");
	    	stmt.execute();
			for(int i = 2; i < v.length; i++) {
				stmt = conn.prepareStatement("UPDATE " + nome_t + " SET voto = voto + ? WHERE id = ? AND nome = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, Integer.parseInt(v[i].split("@")[0]));
				stmt.setString(3, v[i].split("@")[1]);
				//stmt.setString(4, "candidato");
		    	stmt.execute();
			}
			outputStream.write("ok".getBytes(), 0, "ok".length());
			LogHandler.writeLog("Voto ricevuto");
		}catch (Exception e) {
    		outputStream.write("err".getBytes(), 0, "err".length());
    		LogHandler.writeLog("Errore inserimento voto");
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
				ArrayList<String> messaggio = new ArrayList<>();
				ObjectOutputStream oout1 = new ObjectOutputStream(outputStream);
				if(rs1.isLast()) {
					messaggio.add(rs1.getString("nome") + "@" + rs1.getString("testo") + "@" + rs1.getString("si")+ "@" + rs1.getString("no") + "@" + rs1.getString("sb") + "@" + rs1.getString("vincitore"));
				} else {
					stmt = conn.prepareStatement("SELECT tipoVot FROM calcolate WHERE nomeVot = ?");
					String digits = reply.replaceAll("[^0-9]", "");
					reply = reply.replaceAll("\\d","");
					stmt.setString(1, reply + "@" + digits);
					ResultSet rs2 = stmt.executeQuery();
					rs2.next();
					messaggio.add(reply);
					messaggio.add(rs2.getString("tipoVot"));
					//TODO se c'è tempo cercare candidati di quel partito e mostrarli vicini in fase di stampa risultati --> bisogna modificare la tabella della votazione già in creazione e mettere una colonna con il partito di quel candidato 
					if(rs2.getString("tipoVot").equals("categorico")) {
						do {
							if(rs1.getString("tipo").equals("partito"))
								messaggio.add(rs1.getString("tipo") + "@" + rs1.getString("nome") + "@" + rs1.getInt("voto") + "@" + rs1.getString("vincitore"));
						} while(rs1.next());
					} else {
						do {
							messaggio.add(rs1.getString("tipo") + "@" + rs1.getString("nome") + "@" + rs1.getInt("voto") + "@" + rs1.getString("vincitore"));
						} while(rs1.next());
					}
				}
				oout1.writeObject(messaggio);
			} else {
				//TODO
			}
		}
	}
}