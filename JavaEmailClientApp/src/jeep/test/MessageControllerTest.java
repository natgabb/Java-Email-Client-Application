package jeep.test;

import static org.junit.Assert.*;

import java.util.Locale;

import jeep.data.bundles.MessageController;

import org.junit.Test;

/**
 * This tests the MessageController object class.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class MessageControllerTest {

	/**
	 * This tests for all 3 MessagesBundles (Default, en-CA, and fr).
	 */
	@Test
	public void test() {
		Locale locale = Locale.forLanguageTag("en-US");
		MessageController m = new MessageController(locale);
		String message = m.getString("splashMessage");
		System.out.println("Locale: " + locale + "\nMessage: " + message);
		assertEquals(message,
				"This Java Exclusively Email Program (JEEP) was coded by Natacha Gabbamonte.");

		locale = Locale.forLanguageTag("en-CA");
		m = new MessageController(locale);
		message = m.getString("splashMessage");
		System.out.println("Locale: " + locale + "\nMessage: " + message);
		assertEquals(message,
				"This Java Exclusively Email Program (JEEP) was coded by Natacha Gabbamonte.");

		locale = Locale.forLanguageTag("fr-CA");
		m = new MessageController(locale);
		message = m.getString("splashMessage");
		System.out.println("Locale: " + locale + "\nMessage: " + message);
		assertEquals(message,
				"Java Exclusively Email Program (JEEP) à été écrit par Natacha Gabbamonte.");
	}

}
