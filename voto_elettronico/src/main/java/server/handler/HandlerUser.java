package server.handler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HandlerUser{
	
	protected static Connection conn;
	
	public HandlerUser(Connection conn) {
		this.conn = conn;
	}
	
	public void registrazione(String s) throws SQLException, IOException {
		System.out.println("utente: " + s);
		String[] r = s.split("@");
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO utenti VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setString(1, r[0]);
		stmt.setString(2, r[1]);
		stmt.setString(3, r[2]);
		String[] psw =r[3].split(":");
		stmt.setString(4, psw[0]);
		stmt.setString(5, psw[1]);
		stmt.setString(6, r[4]);
		stmt.setInt(7, Integer.parseInt(r[5]));
		stmt.setInt(8, Integer.parseInt(r[6]));
		stmt.setInt(9, Integer.parseInt(r[7]));
		stmt.setString(10, r[8]);
		stmt.setString(11, r[9]);
		stmt.setString(12, r[10]);
		stmt.setString(12, r[11]);
    	stmt.execute();
		stmt = conn.prepareStatement("INSERT INTO votato (codFiscale) VALUES (?)");
		stmt.setString(1, r[0]);
		stmt.execute();
		
	}
	
	public String login(String cf) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utenti WHERE utenti.codiceFiscale = ?");
		stmt.setString(1, cf);
		ResultSet rs = stmt.executeQuery();
		if(!rs.next())
			return "";
		else
			return rs.getString("codiceFiscale") + "@" + rs.getString("cognome") + "@" +rs.getString("nome") + "@" +rs.getString("password") + "@" +rs.getString("salt") + "@" +rs.getString("sesso") + "@" +rs.getInt("anno") + "@" +rs.getInt("mese") + "@" + rs.getInt("giorno") + "@" + rs.getString("paese") + "@" + rs.getString("citta") + "@" + rs.getString("comune") + "@" + rs.getString("type");
	}
	
	public boolean controllaCF(String cf) throws SQLException, IOException {
		PreparedStatement stmt = conn.prepareStatement("SELECT codiceFiscale FROM utenti WHERE codiceFiscale = ?");
		stmt.setString(1, cf);
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			return false;
		}else {
			return true;
		}
	}
}