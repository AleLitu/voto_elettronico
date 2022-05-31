package server.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Votazione;

public class HandlerReferendum extends HandlerVotazioni{
		
	public HandlerReferendum(Connection conn) {
		super(conn);
	}

	public void avvia(String[] s) throws SQLException {
		int id = Integer.parseInt(s[1]);
		String tabella = s[0] + s[1];
		PreparedStatement stmt = conn.prepareStatement("UPDATE referendum  SET attivo = ? WHERE idReferendum = ?;");
		stmt.setInt(1, 1);
		stmt.setInt(2, id);
    	stmt.execute();
    	stmt = conn.prepareStatement("ALTER TABLE votato ADD " + tabella + " INT NOT NULL DEFAULT 0");
    	stmt.execute();
	}
	
	public void calcola() {
		// TODO Auto-generated method stub
		
	}

	public void termina(Votazione v) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("UPDATE referendum SET attivo = ? WHERE idReferendum = ?;");
		stmt.setInt(1, -1);
		stmt.setInt(2, v.getId());
    	stmt.execute();
	}
	
	public ArrayList<Votazione> getTerminati() throws SQLException{
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
	
	public ArrayList<Votazione> getNonAttivi() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT idReferendum, nome FROM referendum WHERE attivo = ?");
		stmt.setInt(1, 0);
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
	
	public ArrayList<Votazione> getAttivi() throws SQLException{
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
	
	public void calcolaSenzaQuorum(Votazione v) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM referendum WHERE idReferendum = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		ResultSet rs = stmt.executeQuery();
		rs.next();
		String nome_t = v.getNome() + v.getId();
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
		stmt.setString(1, v.getNome() + "@" + v.getId());
		stmt.setString(2, v.getTipo());
		stmt.setString(3, "referendum");
		stmt.execute();
		stmt = conn.prepareStatement("DELETE FROM referendum WHERE idReferendum = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		stmt.execute();
	}
	
	public void calcolaConQuorum(Votazione v) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM referendum WHERE idReferendum = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		ResultSet rs = stmt.executeQuery();
		rs.next();
		String nome_t = v.getNome() + v.getId();
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
		stmt.setString(1, v.getNome() + "@" + v.getId());
		stmt.setString(2, v.getTipo());
		stmt.setString(3, "referendum");
		stmt.execute();
		stmt = conn.prepareStatement("DELETE FROM referendum WHERE idReferendum = ? AND nome = ?");
		stmt.setInt(1, v.getId());
		stmt.setString(2, v.getNome());
		stmt.execute();
	}
}