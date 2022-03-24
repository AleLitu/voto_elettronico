package model;
import java.sql.*;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
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
   
	public User getUser(String username, String password) {
		Connection conn = connection();
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM utenti WHERE utenti.username = ?");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			String pwd = rs.getString("password");
			String salt = rs.getString("salt");
			String type = rs.getString("type");
			User u = new User(username, password, salt, type);
			if(u.getPassword().equals(pwd)) {
				u.setType(rs.getString("type"));
				return u;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}