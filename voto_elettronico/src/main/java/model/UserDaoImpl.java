package model;
import java.sql.*;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class UserDaoImpl implements UserDao {
	
	public UserDaoImpl(){
		
	}
	
	public Connection connection() {
		String url = "jdbc:mysql://localhost:3306/votazioni?";
		String usr = "root";
		String pwd = "";
		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			return conn;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	private static String md5(String data) {
		byte[] bdata = new byte[data.length()]; int i; byte[] hash;
		for (i=0;i<data.length();i++) bdata[i]=(byte)(data.charAt(i)&0xff );
		try {
			MessageDigest md5er = MessageDigest.getInstance("MD5");
			hash = md5er.digest(bdata);
		} catch (GeneralSecurityException e) { throw new RuntimeException(e); }
		StringBuffer r = new StringBuffer(32);
		for (i=0;i<hash.length;i++) {
			String x = Integer.toHexString(hash[i]&0xff);
			if (x.length()<2) r.append("0");
			r.append(x);
		}
		return r.toString();      
	}
   
	public User getUser(String username, String password) {
		Connection conn = connection();
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utenti WHERE utenti.codiceFiscale = ?");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			String codiceFiscale = rs.getString("codiceFiscale");
			String cognome = rs.getString("cognome");
			String nome = rs.getString("nome");
			String pwd = rs.getString("password");
			String salt = rs.getString("salt");
			String pass = pwd + ":" + salt;
			String sesso = rs.getString("sesso");
			int anno = rs.getInt("anno");
			int mese = rs.getInt("mese");
			int giorno = rs.getInt("giorno");
			String paese = rs.getString("paese");
			String citta = rs.getString("citta");
			String comune = rs.getString("comune");
			String type = rs.getString("type");
			User u = new User(codiceFiscale, cognome, nome, pass, sesso, anno, mese, giorno, paese, citta, comune, type);
			u.setPassword(pass);
			if(u.getPassword().equals(md5(password+salt)+":"+salt)) {
				u.setType(rs.getString("type"));
				return u;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}