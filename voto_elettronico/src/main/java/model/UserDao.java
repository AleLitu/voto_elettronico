package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface UserDao {
	
	public void registrazione(String s) throws SQLException, IOException;
	
	public String login(String cf) throws SQLException ;
	
	public boolean controllaCF(String cf) throws SQLException, IOException;
	
	public void connection(Connection conn);	
	
	public User getUser(String user, String password);
}