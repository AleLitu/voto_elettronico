package client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ControllerRegistrazioneTest {
	
	@Test
	void testMaggiorenne() {
		boolean expected;
		expected = ControllerRegistrazione.maggiorenne(1999, 5, 19);
	}

	@Test
	void testIsInteger() {
		int expected, result;
		
		expected = 127;
		result = ControllerRegistrazione.isInteger("127");
		assertEquals(expected, result);
		
		expected = -1;
		result = ControllerRegistrazione.isInteger("ciao");
		assertEquals(expected, result);
	}

	@Test
	void testCreazione_cf() {
		char[] expected, result;
		
		expected = "JRRDNL00A09A326X".toCharArray();
		result = ControllerRegistrazione.creazione_cf("Daniel", "Jorrioz", "Italia", "M".charAt(0), 9, 1, 2000, "JRRDNL00A09A326X".toCharArray());
		for(int i = 0; i < result.length; i++)
			assertEquals(expected[i], result[i]);
	}

	@Test
	void testCodiceFiscaleIsOk() {
		boolean expected, result;
		
		expected = true;
		result = ControllerRegistrazione.codiceFiscaleIsOk("JRRDNL00A09A326X", "JRRDNL00A09A326X".toCharArray());
		assertEquals(expected, result);
		
		expected = false;
		result = ControllerRegistrazione.codiceFiscaleIsOk("JRRANL00A09A326X", "JRRDNL00A09A326X".toCharArray());
		assertEquals(expected, result);
	}

}
