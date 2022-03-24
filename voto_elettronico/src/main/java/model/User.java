import java.security.SecureRandom;
import java.util.Random;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class User {
	private String username;
	private String password;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User(String username, String password, String type){
		this.username = username;
		this.password = create(password);
		this.type = type;
	}
	public User(String username, String password, String salt, String type){
		this.username = username;
		this.password = md5(password+salt);
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = create(password);
		}   
   
	static Random _rnd;
	public String create(String passwd) {
		StringBuffer saltBuf = new StringBuffer();
		if (_rnd==null) _rnd=new SecureRandom();
		for (int i=0; i<32; i++) {
			saltBuf.append(Integer.toString(_rnd.nextInt(36),36));
		}
		String salt = saltBuf.toString();
		return md5(passwd+salt)+":"+salt;
	}

	private String md5(String data) {
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

	public boolean check(String passwd,String salt, String cpasswd) {
		return md5(passwd+salt).equals(cpasswd);
	}
}