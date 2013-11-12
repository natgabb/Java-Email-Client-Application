package jeep.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import jeep.data.beans.MailConfig;
import jeep.data.beans.MailMessage;
import jeep.data.configuration.ConfigurationController;
import jeep.mail.MailSendController;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This tests sending an e-mail with the MailSendController.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class MailSendControllerTest {

	private MailSendController controller = null;

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Tests sending an e-mail with a Gmail account. Assumes properties are set
	 * for Gmail.
	 */
	@Ignore
	@Test
	public void sendMainGmail() {
		ConfigurationController configs = new ConfigurationController(null);
		configs.loadProperties();
		MailConfig mailConfig = configs.getMailConfig();
		System.out.println(mailConfig);
		controller = new MailSendController(mailConfig, null);
		ArrayList<String> receiverEmails = new ArrayList<String>();
		receiverEmails.add("M0932340@waldo.dawsoncollege.qc.ca");
		receiverEmails.add("email@hotmail.com");
		receiverEmails.add(mailConfig.getEmailAddress());
		MailMessage message = new MailMessage(-1, mailConfig.getEmailAddress(),
				receiverEmails, receiverEmails, receiverEmails,
				"This is a subject (1)",
				"And this is the message. Super duper interesting stuff.",
				new Date(), 3);
		System.out.println(mailConfig);
		System.out.println(message);
		assertTrue(controller.sendMail(message));
	}

	/**
	 * Tests sending an e-mail with an email other than a Gmail. Assumes the
	 * properties are already set. if using Waldo e-mail: will only work from
	 * Dawson!
	 */
	@Test
	public void sendMainWaldo() {
		ConfigurationController configs = new ConfigurationController(null);
		configs.loadProperties();
		MailConfig mailConfig = configs.getMailConfig();
		System.out.println(mailConfig);
		controller = new MailSendController(mailConfig, null);
		ArrayList<String> receiverEmails = new ArrayList<String>();
		receiverEmails.add("516jeep@gmail.com");
		receiverEmails.add(mailConfig.getEmailAddress());
		MailMessage message = new MailMessage(-1, mailConfig.getEmailAddress(),
				receiverEmails, receiverEmails, receiverEmails,
				"This is a subject (1)",
				"And this is the message. Super duper interesting stuff.",
				new Date(), 3);
		assertTrue(controller.sendMail(message));
	}

}
