package server.handler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Candidato;
import model.Partito;
import model.Votazione;
import server.LogHandler;

public class HandlerVotazione extends HandlerVotazioni{
		
	public HandlerVotazione(Connection conn) {
		super(conn);
	}
	
	public boolean inserisci(String s) throws SQLException {
		String[] v = s.split("€");
		String partito = v[0];
		String candidati = v[1];
		int id;   		
		PreparedStatement stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE nome = ?");
		stmt.setString(1, partito);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			id = rs.getInt("idPartito");
		} else {
			stmt = conn.prepareStatement("INSERT INTO Partiti (nome) VALUES (?)");
    		stmt.setString(1, partito);
    		stmt.execute();
    		stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE nome = ?");
    		stmt.setString(1, partito);
    		rs = stmt.executeQuery();
    		rs.next();
    		id = rs.getInt("idPartito");
		}
		return inserisciCandidati(id, candidati);
	}
	
	public boolean inserisciCandidati(int id, String candidati) throws SQLException {
    	String[] c = candidati.split(", ");
    	boolean count = false;
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
			return true;
		else
			return false;
	}

	public void avvia(String[] v) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO attive (nome, tipo) VALUES (?, ?)");
		stmt.setString(1, v[0]);
		stmt.setString(2, v[1]);
    	stmt.execute();
    	stmt = conn.prepareStatement("SELECT * FROM attive WHERE nome = ?");
		stmt.setString(1, v[0]);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		String nome_t = rs.getString("nome") + rs.getInt("idAttive");
		stmt = conn.prepareStatement("CREATE TABLE " + nome_t + " (id INT, tipo VARCHAR(9), nome VARCHAR(45), voto INT NOT NULL DEFAULT 0, PRIMARY KEY(id, tipo))");
		stmt.execute();
		stmt = conn.prepareStatement("ALTER TABLE votato ADD " + nome_t + " INT NOT NULL DEFAULT 0");
    	stmt.execute();
	}
	
	public void addAvvia(String[] v, String votazione) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM attive WHERE nome = ?");
		stmt.setString(1, v[0]);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		String nome_t = rs.getString("nome") + rs.getInt("idAttive");
		
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
    	try {
    		if(!votazione.equals("Voto ordinale")) {
    	    	stmt = conn.prepareStatement("INSERT INTO " + nome_t + " (id, tipo, nome) VALUES (?, ?, ?)");
    	    	stmt.setInt(1, -1);
    	    	stmt.setString(2, "bianche");
    			stmt.setString(3, "schede bianche");
    			stmt.execute();
        	}else {
        		stmt = conn.prepareStatement("CREATE TABLE " + nome_t + "1" + " (id INT, numero INT, tipo VARCHAR(9), nome VARCHAR(45), voto INT NOT NULL DEFAULT 0, PRIMARY KEY(id, numero))");
    			stmt.execute();
        	}
    	} catch(Exception e) {}
	}
	
	public ArrayList<Partito> getVotazione(String nome_t) throws SQLException{
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
		return partiti;
	}

	public boolean inserisciVoto(String voto) throws SQLException {
		String v[] = voto.split("€");
		if(v[1].equals("vc")) {
			return votoCategorico(v[0]);
		} else if(v[1].equals("vcp")) {
			return votoCategoricoPreferenze(v[0]);
		} else {
			return votoOrdinale(v[0]);
		}
	}
	
	public boolean votoCategoricoPreferenze(String s) {
		try {
			String[] v = s.split(",");
			String nome_t = v[0].split("@")[0]+v[0].split("@")[1];
			votato(v[1], nome_t);
			PreparedStatement stmt = conn.prepareStatement("UPDATE "+ nome_t +" SET voto = voto + ? WHERE id = ? AND nome = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(v[2].split("@")[0]));
			stmt.setString(3, v[2].split("@")[1]);
			//stmt.setString(4, "partito");
	    	stmt.execute();
			for(int i = 3; i < v.length; i++) {
				stmt = conn.prepareStatement("UPDATE " + nome_t + " SET voto = voto + ? WHERE id = ? AND nome = ?");
				stmt.setInt(1, 1);
				stmt.setInt(2, Integer.parseInt(v[i].split("@")[0]));
				stmt.setString(3, v[i].split("@")[1]);
				//stmt.setString(4, "candidato");
		    	stmt.execute();
			}
			return true;
		}catch (Exception e) {
			return false;
    	}
	}
	
	public boolean votoCategorico(String votazione) {
		try {
			String[] voto = votazione.split(",");
			String[] tabella = voto[0].split("@");
			votato(voto[1], tabella[0]+tabella[1]);
			PreparedStatement stmt = conn.prepareStatement("UPDATE " + tabella[0]+tabella[1] + " SET voto = voto + ? WHERE id = ? AND nome = ?");
			stmt.setInt(1, 1);
			stmt.setInt(2, Integer.parseInt(voto[2].split("@")[0]));
			stmt.setString(3, voto[2].split("@")[1]);
			//stmt.setString(4, "partito");
			stmt.execute();
			return true;
    	}catch (Exception e) {
    		return false;
    	}
	}
	
	public boolean votoOrdinale(String s) throws SQLException {
		try {
			String[] v = s.split(",");
			String nome_t = v[0].split("@")[0]+v[0].split("@")[1];
			votato(v[1], nome_t);
			PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS num FROM " + nome_t); 
			ResultSet rs = stmt.executeQuery();
			int num = 0;
			if(rs.next()) {
				num = rs.getInt("num");
			}
			stmt = conn.prepareStatement("SELECT * FROM " + nome_t + "1"); 
			rs = stmt.executeQuery();	
			boolean b = false;
			if(rs.next()) {
				b = true;
			}
			if(!b) {
				stmt = conn.prepareStatement("SELECT * FROM " + nome_t); 
				rs = stmt.executeQuery();
				while(rs.next()) {
					stmt = conn.prepareStatement("INSERT INTO " + nome_t + "1" + " (id, numero, tipo, nome, voto) VALUES (?, ?, ?, ?, ?)");
					stmt.setInt(1, -1);
					stmt.setInt(2, 0);
					stmt.setString(3, null);
					stmt.setString(4, "schede bianche");
					stmt.setInt(5, 0);
					stmt.execute();
					for(int i = 0; i < num; i++) {
						stmt = conn.prepareStatement("INSERT INTO " + nome_t + "1" + " (id, numero, tipo, nome, voto) VALUES (?, ?, ?, ?, ?)");
						stmt.setInt(1, rs.getInt("id"));
						stmt.setInt(2, i + 1);
						stmt.setString(3, rs.getString("tipo"));
						stmt.setString(4, rs.getString("nome"));
						stmt.setInt(5, 0);
						stmt.execute();
					}
				}
			}
			int j = 0;
	    	if(v[2].equals("p")) {
	    		for(int i = 3; i < v.length; i++) {
	    			stmt = conn.prepareStatement("UPDATE " + nome_t + "1" + " SET voto = voto + 1 WHERE id = ? AND numero = ?");
					stmt.setInt(1, Integer.parseInt(v[i].split("@")[0]));
					stmt.setInt(2, j + 1);
					j++;
			    	stmt.execute();
				}
				return true;
	    	}else if(v[2].equals("c")){
	    		for(int i = 3; i < v.length; i++) {
    				stmt = conn.prepareStatement("UPDATE " + nome_t + "1" + " SET voto = voto + 1 WHERE id = ? AND numero = ?");
    				stmt.setInt(1, Integer.parseInt(v[i].split("@")[0]));
    				stmt.setInt(2, j + 1);
    				j++;
    		    	stmt.execute();
				}
				return true;
	    	}else {
				stmt = conn.prepareStatement("UPDATE " + nome_t + "1" + " SET voto = voto + 1 WHERE id = ? AND nome = ?");
				stmt.setInt(1, Integer.parseInt(v[2].split("@")[0]));
				stmt.setString(2, v[2].split("@")[1]);
		    	stmt.execute();
		    	return true;
	    	}
		}catch (Exception e) {
    		return false;
    	}
	}
	
	public ArrayList<Partito> getPartiti() throws SQLException {
		int idp;
		PreparedStatement stmt = conn.prepareStatement("SELECT idPartito, nome FROM partiti");
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			return null;
		} else {
			ArrayList<Partito> partiti = new ArrayList<>();
			do {
				idp = rs.getInt("idPartito");
				PreparedStatement stmt1 = conn.prepareStatement("SELECT idCandidato, nome FROM candidati WHERE idPartito = ?");
				stmt1.setInt(1, idp);
				ResultSet rs1 = stmt1.executeQuery();
				ArrayList<Candidato> candidati = new ArrayList<>();
				if(rs1.next()) {
					do {
						candidati.add(new Candidato(rs1.getInt("idCandidato"), rs1.getString("nome")));
					} while(rs1.next());
				}
				partiti.add(new Partito(idp, rs.getString("nome"), candidati));
			}while(rs.next());
			return partiti;
		}
	}
	
	public ArrayList<Candidato> getCandidati() throws SQLException, IOException {
		PreparedStatement stmt = conn.prepareStatement("SELECT idCandidato, nome FROM candidati ORDER BY nome");
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			return null;
		} else {
			ArrayList<Candidato> candidati = new ArrayList<>();
			do {
				candidati.add(new Candidato(rs.getInt("idCandidato"), rs.getString("nome")));
			} while(rs.next());
			return candidati;
		}
	}

	public void termina(Votazione v) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO terminate (idTerminate, nome, tipo) VALUES (?, ?, ?)");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		stmt.setString(3, v.getTipo());
    	stmt.execute();
    	stmt = conn.prepareStatement("DELETE FROM attive WHERE idAttive = ? AND nome = ?");
    	stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		stmt.execute();
	}
	
	public ArrayList<Votazione> getAttive() throws SQLException{
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
	
	public ArrayList<Votazione> getTerminate() throws SQLException{
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
	
	public void calcolaMaggioranza(Votazione v) throws SQLException {
		String nome_tab = v.getNome() + v.getId();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + nome_tab);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		stmt = conn.prepareStatement("SELECT tipo FROM terminate WHERE idTerminate = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		ResultSet rs1 = stmt.executeQuery();
		rs1.next();
		String tipo_vot = rs1.getString("tipo");
		ArrayList<Candidato> idp_max = new ArrayList<>();
		ArrayList<Candidato> idc_max = new ArrayList<>();
		ArrayList<String> messaggio = new ArrayList<>();
		messaggio.add(v.getNome());
		int max_p = 0;
		stmt = conn.prepareStatement("ALTER TABLE " + nome_tab + " ADD vincitore INT NOT NULL DEFAULT 0");
		stmt.execute();
		rs.next();
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
			for(int j = 0; j < idp_max.size(); j++) {
				idc_max.clear();
				int max_c = 0;
				stmt = conn.prepareStatement("SELECT id, " + nome_tab + ".nome, voto FROM " + nome_tab + " INNER JOIN candidati ON id = idCandidato WHERE idPartito = ?");
				stmt.setInt(1, idp_max.get(j).getId());
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
				for(int k = 0; k < idc_max.size(); k++) {
					stmt = conn.prepareStatement("UPDATE " + nome_tab + " SET vincitore = ? WHERE id = ? AND nome = ?;");
					stmt.setInt(1, 1);
					stmt.setInt(2, idc_max.get(k).getId());
					stmt.setString(3, idc_max.get(k).getNome());
					stmt.execute();
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
		stmt = conn.prepareStatement("DELETE FROM terminate WHERE idTerminate = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		stmt.execute();
		stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo, tipoVot) VALUES (?, ?, ?)");
		stmt.setString(1, v.getNome() + "@" + v.getId());
		stmt.setString(2, v.getTipo());
		stmt.setString(3, tipo_vot);
		stmt.execute();
	}
	
	public void calcolaMaggioranzaAssoluta(Votazione v) throws SQLException {
		String nome_tab = v.getNome() + v.getId();
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + nome_tab);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		stmt = conn.prepareStatement("SELECT tipo FROM terminate WHERE idTerminate = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		ResultSet rs1 = stmt.executeQuery();
		rs1.next();
		String tipo_vot = rs1.getString("tipo");
		ArrayList<Candidato> idp_max = new ArrayList<>();
		ArrayList<Candidato> idc_max = new ArrayList<>();
		ArrayList<String> messaggio = new ArrayList<>();
		messaggio.add(v.getNome());
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
				for(int j = 0; j < idp_max.size(); j++) {
					idc_max.clear();
					int max_c = 0;
					stmt = conn.prepareStatement("SELECT id, " + nome_tab + ".nome, voto FROM " + nome_tab + " INNER JOIN candidati ON id = idCandidato WHERE idPartito = ?");
					stmt.setInt(1, idp_max.get(j).getId());
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
			}else {
				PreparedStatement stmt1 = conn.prepareStatement("UPDATE " + nome_tab + " SET vincitore = ?;");
				stmt1.setInt(1, -1);
				stmt1.execute();
			}
		}
			
		stmt = conn.prepareStatement("DELETE FROM terminate WHERE idTerminate = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		stmt.execute();
		stmt = conn.prepareStatement("INSERT INTO calcolate (nomeVot, tipoCalcolo, tipoVot) VALUES (?, ?, ?)");
		stmt.setString(1, v.getNome() + "@" + v.getId());
		stmt.setString(2, v.getTipo());
		stmt.setString(3, tipo_vot);
		stmt.execute();
	}
}
