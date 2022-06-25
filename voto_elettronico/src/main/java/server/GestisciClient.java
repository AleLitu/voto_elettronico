package server;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
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

import client.ControllerLogin;
import model.Candidato;
import model.Partito;
import model.Referendum;
import model.Votazione;
import server.handler.HandlerKeys;
import server.handler.HandlerReferendum;
import server.handler.HandlerVotazione;
import server.handler.HandlerVotazioni;
import model.User;
import model.UserDao;
import model.UserDaoImpl;

public class GestisciClient implements Runnable, Serializable{
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
	private HandlerReferendum href;
	private HandlerVotazione hvot;
	private UserDao userDao;
	private HandlerKeys hk;
	
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
    	href = new HandlerReferendum(conn);
    	hvot = new HandlerVotazione(conn);
    	hk = HandlerKeys.getInstance();
    	userDao = new UserDaoImpl();
    	userDao.connection(conn);
		    	
		while(true) {
			try {
				letti = inputStream.read(buffer);
				if(letti > 0) {
					String scelta = new String(buffer, 0, letti);
					switch(scelta) {
					case "registrazione":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						byte buffer1[] = new byte[150];
						letti = inputStream.read(buffer1);
						String reg = new String(buffer1, 0, letti);
						if(!reg.equals("err")) {
							userDao.registrazione(reg);
							outputStream.write("ok".getBytes(), 0, "ok".length());
						}
						break;
					case "login":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String cf = new String(buffer, 0, letti);
						outputStream.write("ok".getBytes(), 0, "ok".length());
						String[] s = cf.split(",");
						letti = inputStream.read(buffer);
						cf = new String(buffer, 0, letti);
						String r = userDao.login(s[0]);
						ObjectOutputStream oout2 = new ObjectOutputStream(outputStream);
						User user;
						if(!r.equals("")) {
							UserDao userdao = new UserDaoImpl();
		                	user = userdao.getUser(r, s[1]);
						} else {
							UserDao userdao = new UserDaoImpl();
		                	user = userdao.getUser("", s[1]);
						}
						oout2.writeObject(user);
						break;
					case "codiceFiscale":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String codFiscale = new String(buffer, 0, letti);
						if(!userDao.controllaCF(codFiscale))
							outputStream.write("err".getBytes(), 0, "err".length());
						else
							outputStream.write("ok".getBytes(), 0, "ok".length());
						break;
					case "votante":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String codFiscale1 = new String(buffer, 0, letti);
						if(hvot.inserisciVotante(codFiscale1))
							outputStream.write("ok".getBytes(), 0, "ok".length());
						else
							outputStream.write("no".getBytes(), 0, "no".length());
						break;
					case "a":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String testo = new String(buffer, 0, letti);
						if(href.inserisci(testo))
							LogHandler.writeLog("Nuovo referendum creato");
						else
							LogHandler.writeLog("Errore creazione nuovo referendum");
						break;
					case "b":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String partito = new String(buffer, 0, letti);
						int dim = 500;
						byte buf[] = new byte[dim];
						letti = inputStream.read(buf);
						String candidati = new String(buf, 0, letti);
						if(hvot.inserisci(partito + "€" + candidati)) {
							outputStream.write("true".getBytes(), 0, "true".length());
							LogHandler.writeLog("Nuova lista creata");
						} else {
							outputStream.write("false".getBytes(), 0, "false".length());
							LogHandler.writeLog("Errore nell'inserimento di uan nuova lista");
						}
						break;
					case "ref":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						dis = new DataInputStream(inputStream);
					    letti = dis.readInt();
					    cipherData = new byte[letti];
					    dis.readFully(cipherData);
						cipher.init(Cipher.DECRYPT_MODE, hk.getPrivateKey());
						String voto = new String(cipher.doFinal(cipherData), StandardCharsets.UTF_8);
						if(!voto.equals("err")) {
							if(href.inserisciVoto(voto)) {
								outputStream.write("ok".getBytes(), 0, "ok".length());
								LogHandler.writeLog("Voto ricevuto");
							} else {
								outputStream.write("err".getBytes(), 0, "err".length());
					    		LogHandler.writeLog("Errore inserimento voto");
							}
						}
						break;
					case "avvio":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String votazione = new String(buffer, 0, letti);
						if(votazione.equals("Referendum")) {
							ArrayList<Votazione> refNAtt = href.getNonAttivi();
				    		if(refNAtt == null) {
				    			outputStream.write("no".getBytes(), 0, "no".length());
				    		} else {
				    			outputStream.write("ok".getBytes(), 0, "ok".length());
				    			ObjectOutputStream out = new ObjectOutputStream(outputStream);
								out.writeObject(refNAtt);
								letti = inputStream.read(buffer);
								String risposta = new String(buffer, 0, letti);
								if(risposta.equals("esc")) {
									break;
								} else {
									href.avvia(risposta.split("@"));
							    	LogHandler.writeLog("Nuovo referendum avviato");
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
									break;
								}
								ArrayList<Partito> partiti = hvot.getPartiti();
								if(partiti == null)
									break;
								else {
									ObjectOutputStream oos = new ObjectOutputStream(outputStream);
									oos.writeObject(partiti);
									boolean done = false;
									while(true) {
										letti = inputStream.read(buffer);
										String risposta = new String(buffer, 0, letti);
										if(risposta.equals("esc")) {
											break;
										} else {
											String[] v = risposta.split(",");
											if(!done) {
												hvot.avvia(v);
										    	done = true;
											}
											hvot.addAvvia(v, votazione);
										}
									}
									LogHandler.writeLog("Nuova votazione avviata");
								}
				    		}
						}
						break;
					case "attive":
						outputStream.write("ok".getBytes(), 0, "ok".length());
						letti = inputStream.read(buffer);
						String codFiscale2 = new String(buffer, 0, letti);
						outputStream.write("ok".getBytes(), 0, "ok".length());
						ArrayList<Votazione> vot = hvot.getVotAttive(codFiscale2);
						if(vot == null)
							outputStream.write("no".getBytes(), 0, "no".length());
						else {
							outputStream.write("ok".getBytes(), 0, "ok".length());
							ObjectOutputStream oout = new ObjectOutputStream(outputStream);
							oout.writeObject(vot);
						}
						break;
					case "candidati":
						ArrayList<Candidato> c = hvot.getCandidati();
						if(c != null) {
							ObjectOutputStream oos = new ObjectOutputStream(outputStream);
							oos.writeObject(c);
						}
						break;
					case "partiti":
						ArrayList<Partito> p = hvot.getPartiti();
						if(p != null) {
							ObjectOutputStream oos = new ObjectOutputStream(outputStream);
							oos.writeObject(p);
						}
						break;
					case "votazione":
						letti = inputStream.read(buffer);
						String[] v = new String(buffer, 0, letti).split("@");
						if(!v[2].equals("referendum")) {
							String nome_t = v[0] + v[1];
							ArrayList<Partito> partiti = hvot.getVotazione(nome_t);
							ObjectOutputStream oout = new ObjectOutputStream(outputStream);
							oout.writeObject(nome_t.replaceAll("\\d",""));
							oout.flush();
							ObjectOutputStream pubkey = new ObjectOutputStream(outputStream);
							pubkey.writeObject(hk.getPublicKey());
							oout = new ObjectOutputStream(outputStream);
							oout.writeObject(partiti);
						} else {
							Referendum re = href.getDomanda(Integer.parseInt(v[1]), v[0]);
							if(re != null) {
								ObjectOutputStream pubkey = new ObjectOutputStream(outputStream);
								pubkey.writeObject(hk.getPublicKey());
								ObjectOutputStream oos = new ObjectOutputStream(outputStream);
								oos.writeObject(re);
							}
						}
						break;
					case "vc":
						dis = new DataInputStream(inputStream);
					    letti = dis.readInt();
					    cipherData = new byte[letti];
					    dis.readFully(cipherData);
						cipher.init(Cipher.DECRYPT_MODE, hk.getPrivateKey());
						voto = new String(cipher.doFinal(cipherData), StandardCharsets.UTF_8);
						if(!voto.equals("err")) {
							if(hvot.inserisciVoto(voto+"€vc")) {
								outputStream.write("ok".getBytes(), 0, "ok".length());
								LogHandler.writeLog("Voto ricevuto");
							} else {
								outputStream.write("err".getBytes(), 0, "err".length());
					    		LogHandler.writeLog("Errore inserimento voto");
							}
						}
						break;
					case "vcp":
						dis = new DataInputStream(inputStream);
					    letti = dis.readInt();
					    cipherData = new byte[letti];
					    dis.readFully(cipherData);
						cipher.init(Cipher.DECRYPT_MODE, hk.getPrivateKey());
						voto = new String(cipher.doFinal(cipherData), StandardCharsets.UTF_8);
						if(!voto.equals("err")) {
							if(hvot.inserisciVoto(voto+"€vcp")) {
								outputStream.write("ok".getBytes(), 0, "ok".length());
								LogHandler.writeLog("Voto ricevuto");
							} else {
								outputStream.write("err".getBytes(), 0, "err".length());
					    		LogHandler.writeLog("Errore inserimento voto");
							}
						}
						break;
					case "vo":
						dis = new DataInputStream(inputStream);
					    letti = dis.readInt();
					    cipherData = new byte[letti];
					    dis.readFully(cipherData);
						cipher.init(Cipher.DECRYPT_MODE, hk.getPrivateKey());
						voto = new String(cipher.doFinal(cipherData), StandardCharsets.UTF_8);
						if(!voto.equals("err")) {
							if(hvot.inserisciVoto(voto+"€vo")) {
								outputStream.write("ok".getBytes(), 0, "ok".length());
								LogHandler.writeLog("Voto ricevuto");
							} else {
								outputStream.write("err".getBytes(), 0, "err".length());
					    		LogHandler.writeLog("Errore inserimento voto");
							}
						}
						break;	
					case "scrutinio":
						ArrayList<Votazione> refTer = href.getTerminati();
						ArrayList<Votazione> votTer = hvot.getTerminate();
						if(refTer == null && votTer == null) {
							outputStream.write("no".getBytes(), 0, "no".length());
						} else {
							outputStream.write("ok".getBytes(), 0, "ok".length());
							ObjectOutputStream oout = new ObjectOutputStream(outputStream);
							if(votTer == null) {
								oout.writeObject(refTer);
							} else if(refTer == null) {
								oout.writeObject(votTer);
							} else {
								votTer.addAll(refTer);
								oout.writeObject(votTer);
							}
							ObjectInputStream oin = new ObjectInputStream(inputStream);
							ArrayList<Votazione> list = (ArrayList<Votazione>) oin.readObject();
							for(int i = 0; i < list.size(); i++) {
								if(list.get(i).getTipo().equals("Senza quorum")) {
									href.calcolaSenzaQuorum(list.get(i));
								} else if(list.get(i).getTipo().equals("Con quorum")) {
									href.calcolaConQuorum(list.get(i));
								} else if(list.get(i).getTipo().equals("Maggioranza")) {
									hvot.calcolaMaggioranza(list.get(i));
								} else if(list.get(i).getTipo().equals("Maggioranza assoluta")) {
									hvot.calcolaMaggioranzaAssoluta(list.get(i));
								}
								LogHandler.writeLog("Votazione calcolata");
							}
						}
						break;
					case "calculated":
						ArrayList<String> calcolate = HandlerVotazioni.getCalcolate();
						if(calcolate == null) {
							outputStream.write("no".getBytes(), 0, "no".length());
						} else {
							outputStream.write("ok".getBytes(), 0, "ok".length());
							ObjectOutputStream oout = new ObjectOutputStream(outputStream);
							oout.writeObject(calcolate);
							letti = inputStream.read(buffer);
							String reply = new String(buffer, 0, letti);
							if(!reply.equals("no")) {
								ArrayList<String> messaggio = HandlerVotazioni.getSpecificCalculated(reply);
								ObjectOutputStream oout1 = new ObjectOutputStream(outputStream);
								oout1.writeObject(messaggio);
							}
						}
						break;
					case "end":
						ArrayList<Votazione> refAtt = href.getAttivi();
						ArrayList<Votazione> votAtt = hvot.getAttive();
						if(refAtt == null && votAtt == null) {
							outputStream.write("no".getBytes(), 0, "no".length());
						} else {
							outputStream.write("ok".getBytes(), 0, "ok".length());
							ObjectOutputStream oout = new ObjectOutputStream(outputStream);
							if(votAtt == null) {
								oout.writeObject(refAtt);
							} else if(refAtt == null) {
								oout.writeObject(votAtt);
							} else {
								votAtt.addAll(refAtt);
								oout.writeObject(votAtt);
							}	
							ObjectInputStream oin = new ObjectInputStream(inputStream);
							ArrayList<Votazione> list = (ArrayList<Votazione>) oin.readObject();
							if(list != null) {
								for(int i = 0; i < list.size(); i++) {
									if(list.get(i).getTipo().equals("referendum")) {
										href.termina(list.get(i));
									} else {
										hvot.termina(list.get(i));
									}
									LogHandler.writeLog("Votazione terminata");
								}
							}
						}
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
}
