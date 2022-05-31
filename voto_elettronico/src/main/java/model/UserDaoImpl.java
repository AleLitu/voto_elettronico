package model;
import java.sql.*;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class UserDaoImpl implements UserDao {
	
	public UserDaoImpl(){
		
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
   
	public User getUser(String utente) {
		if(utente.equals(""))
			return null;
		String[] s = utente.split("@");
		String codiceFiscale = s[0];
		String cognome = s[1];
		String nome = s[2];
		String pwd = s[3];
		String salt = s[4];
		String pass = pwd + ":" + salt;
		String sesso = s[5];
		int anno = Integer.parseInt(s[6]);
		int mese = Integer.parseInt(s[7]);
		int giorno = Integer.parseInt(s[8]);
		String paese = s[9];
		String citta = s[10];
		String comune = s[11];
		String type = s[12];
		User u = new User(codiceFiscale, cognome, nome, pass, sesso, anno, mese, giorno, paese, citta, comune, type);
		u.setPassword(pass);
		if(u.getPassword().equals(pass)) {
			u.setType(s[12]);
			return u;
		}
		return null;
	}
}