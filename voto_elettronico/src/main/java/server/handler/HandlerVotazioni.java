package server.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Candidato;
import model.Votazione;

public abstract class HandlerVotazioni{
	protected static Connection conn;
	
	public HandlerVotazioni(Connection conn) {
		this.conn = conn;
	}
	
	public abstract void avvia(String[] v) throws SQLException;
	public abstract void calcola();
	public abstract void termina(Votazione v) throws SQLException;

	
	public static ArrayList<String> getSpecificCalculated(String nome_t) throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT tipoVot FROM calcolate WHERE nomeVot = ?");
		String digits = nome_t.replaceAll("[^0-9]", "");
		String nome = nome_t.replaceAll("\\d","");
		stmt.setString(1, nome + "@" + digits);
		ResultSet rs2 = stmt.executeQuery();
		rs2.next();
		ArrayList<String> messaggio = new ArrayList<>();
		if(rs2.getString("tipoVot").equals("referendum"))
			stmt = conn.prepareStatement("SELECT * FROM `" + nome_t +"`");
		else 
			stmt = conn.prepareStatement("SELECT * FROM `" + nome_t +"` ORDER BY id");
		ResultSet rs1 = stmt.executeQuery();
		rs1.next();
		if(rs1.isLast()) {
			messaggio.add(rs1.getString("nome") + "@" + rs1.getString("testo") + "@" + rs1.getString("si")+ "@" + rs1.getString("no") + "@" + rs1.getString("sb") + "@" + rs1.getString("vincitore"));
		} else {
			messaggio.add(nome);
			messaggio.add(rs2.getString("tipoVot"));
			//TODO se c'è tempo cercare candidati di quel partito e mostrarli vicini in fase di stampa risultati --> bisogna modificare la tabella della votazione già in creazione e mettere una colonna con il partito di quel candidato 
			if(rs2.getString("tipoVot").equals("categorico")) {
				do {
					if(rs1.getString("tipo").equals("bianche"))
						messaggio.add(rs1.getString("tipo") + "@" + rs1.getInt("voto"));
					if(rs1.getString("tipo").equals("partito"))
						messaggio.add(rs1.getString("tipo") + "@" + rs1.getString("nome") + "@" + rs1.getInt("voto") + "@" + rs1.getString("vincitore"));
				} while(rs1.next());
			}else if(rs2.getString("tipoVot").equals("ordinale")) {
				PreparedStatement stmt11 = conn.prepareStatement("SELECT * FROM `" + nome_t + "1" +"` ORDER BY id");
				ResultSet rs11 = stmt11.executeQuery();
				while(rs11.next()) {
						messaggio.add(rs11.getString("tipo") + "@" + rs11.getString("numero") + "@" + rs11.getString("nome") + "@" + rs11.getInt("voto") + "@" + rs11.getString("vincitore"));
				}
			} else {
				do {
					if(rs1.getString("tipo").equals("bianche"))
						messaggio.add(rs1.getString("tipo") + "@" + rs1.getInt("voto"));
					else 
						messaggio.add(rs1.getString("tipo") + "@" + rs1.getString("nome") + "@" + rs1.getInt("voto") + "@" + rs1.getString("vincitore"));
				} while(rs1.next());
			}
		}
		return messaggio;
	}
	
	public static ArrayList<String> getCalcolate() throws SQLException{
		PreparedStatement stmt = conn.prepareStatement("SELECT nomeVot FROM calcolate");
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) {
			return null;
		} else {
			ArrayList<String> vot= new ArrayList<>();
			do {
				vot.add(rs.getString("nomeVot"));
			} while(rs.next());
			return vot;
		}
	}
}