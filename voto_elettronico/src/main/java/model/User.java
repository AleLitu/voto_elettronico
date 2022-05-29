package model;
import java.security.SecureRandom;
import java.util.Random;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class User {
	//private int id;
	private String codiceFiscale;
	private String cognome;
	private String nome;
	//private String username;
	private String password;
	private String sesso;
	private int anno;
	private int mese;
	private int giorno;
	private String paese;
	private String citta;
	private String comune;
	private String type;
	static Random _rnd;
	
	public User(String codiceFiscale, String cognome, String nome, String password, String sesso, int anno, int mese, int giorno, String paese, String citta, String comune, String type) {
		this.codiceFiscale = codiceFiscale;
		this.cognome = cognome;
		this.nome = nome;
		this.password = create(password);
		this.sesso = sesso;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;
		this.paese = paese;
		this.citta = citta;
		this.comune = comune;
		this.type = type;
	}

	/*public User(int id, String username, String password, String type){
		this.id = id;
		this.username = username;
		this.password = create(password);
		this.type = type;
	}
	public User(int id, String username, String password, String salt, String type){
		this.id = id;
		this.username = username;
		this.password = md5(password+salt);
		this.type = type;
	}*/
	
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public int getAnno() {
		return anno;
	}

	public void setAnno(int anno) {
		this.anno = anno;
	}

	public int getMese() {
		return mese;
	}

	public void setMese(int mese) {
		this.mese = mese;
	}

	public int getGiorno() {
		return giorno;
	}

	public void setGiorno(int giorno) {
		this.giorno = giorno;
	}

	public String getPaese() {
		return paese;
	}

	public void setPaese(String paese) {
		this.paese = paese;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/*public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}*/

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public static String create(String passwd) {
		StringBuffer saltBuf = new StringBuffer();
		if (_rnd==null) _rnd=new SecureRandom();
		for (int i=0; i<32; i++) {
			saltBuf.append(Integer.toString(_rnd.nextInt(36),36));
		}
		String salt = saltBuf.toString();
		return md5(passwd+salt)+":"+salt;
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
	
	@Override
	public String toString() {
		return codiceFiscale + "@" + cognome + "@" + nome + "@" + password + "@" + sesso + "@" + anno + "@" + mese + "@" + giorno + "@" + paese + "@" + citta + "@" + comune + "@" + type;
	}

	public boolean check(String passwd,String salt, String cpasswd) {
		return md5(passwd+salt).equals(cpasswd);
	}
}