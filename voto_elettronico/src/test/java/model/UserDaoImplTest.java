package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserDaoImplTest {

	@Test
	void testGetUser() {	
		UserDao usr = new UserDaoImpl();
		User expected, result;
		
		expected = null;
		result = usr.getUser("", "");
		assertEquals(expected, result);
		
		expected = null;
		result = usr.getUser("", "12345");
		assertEquals(expected, result);
		
		boolean exp, res;
		exp = true;
		User u = new User("ABCD", "Giorgio", "Rossi", "password:salt", "M", 1999, 5, 19, "Italia", "Milano", "Milano", "Elettore");
		u.setPassword("password:salt");
		User r = usr.getUser("ABCD@Giorgio@Rossi@password@salt@M@1999@5@19@Italia@Milano@Milano@Elettore", " ");
		res = u.equals(r);
		assertEquals(expected, result);
	}
}
