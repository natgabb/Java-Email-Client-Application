package jeep.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage.RecipientType;

import com.sun.mail.pop3.POP3SSLStore;

import jeep.data.beans.MailConfig;
import jeep.data.beans.MailMessage;
import jeep.gui.MailApp;

/**
 * This class takes care of the POP side of the mail client. It tries to get
 * messages based on the configuration it is given.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class MailReceiveController {

	private Logger logger = null;
	private MailConfig mailConfig = null;
	private ArrayList<MailMessage> mailMessageList = null;

	/**
	 * Constructor
	 * 
	 * @param mailConfig
	 *            The configuration
	 * @param logger
	 *            The logger
	 */
	public MailReceiveController(MailConfig mailConfig, Logger logger) {
		this.mailConfig = mailConfig;
		if (logger != null)
			this.logger = logger;
		else
			this.logger = Logger.getLogger(getClass().getName());
		mailMessageList = new ArrayList<MailMessage>();
	}

	/**
	 * Gets all the new mail from the mail server.
	 * 
	 * @return A list of all the messages.
	 */
	public ArrayList<MailMessage> getMail() {
		mailMessageList.clear();
		boolean retValue = true;
		retValue = mailReceive();
		if (!retValue)
			return null;
		else
			return mailMessageList;
	}

	/*
	 * Tries to receive the mail.
	 */
	private boolean mailReceive() {

		boolean retVal = true;

		Store store = null;
		Folder folder = null;
		Session session = null;

		Properties pop3Props = new Properties();

		try {

			if (mailConfig.getIsGmailAccount()) { // Gmail config

				// Store configuration information for accessing the
				// server in the properties object
				pop3Props.put("mail.pop3.host", mailConfig.getUrlPOP3Server());
				pop3Props
						.setProperty("mail.user", mailConfig.getUserNamePOP3());
				pop3Props.setProperty("mail.passwd",
						mailConfig.getPasswordPOP3());
				pop3Props.setProperty("mail.pop3.port",
						mailConfig.getPortPOP3Server());
				String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
				pop3Props.setProperty("mail.pop3.socketFactory.class",
						SSL_FACTORY);
				pop3Props.setProperty("mail.pop3.socketFactory.port",
						mailConfig.getPortPOP3Server());
				pop3Props.setProperty("mail.pop3.ssl", "true");
				pop3Props.setProperty("mail.pop3.socketFactory.fallback",
						"false");

				URLName url = new URLName("pop3://"
						+ pop3Props.getProperty("mail.user") + ":"
						+ pop3Props.getProperty("mail.passwd") + "@"
						+ pop3Props.getProperty("mail.pop3.host") + ":"
						+ pop3Props.getProperty("mail.pop3.port"));

				// Create a mail session
				session = Session.getDefaultInstance(pop3Props, null);

				// Get hold of a POP3 message store, and connect to it
				store = new POP3SSLStore(session, url);

			} else { // POP3 config
				// Create a mail session
				session = Session.getDefaultInstance(pop3Props, null);

				// Get hold of a POP3 message store, and connect to it
				store = session.getStore("pop3");
			}

			if (MailApp.DEBUG)
				session.setDebug(true);

			// Connect to server
			store.connect(mailConfig.getUrlPOP3Server(),
					mailConfig.getUserNamePOP3(), mailConfig.getPasswordPOP3());

			// Get the default folder
			folder = store.getDefaultFolder();
			if (folder == null)
				throw new Exception(":No default folder");

			// Get the INBOX from the default folder
			folder = folder.getFolder("INBOX");
			if (folder == null)
				throw new Exception(":No POP3 INBOX");

			// Open the folder for read/write, can delete messages
			folder.open(Folder.READ_WRITE);

			// Get all the waiting messages
			Message[] msgs = folder.getMessages();

			// Process the messages into beans
			retVal = process(msgs);

		} catch (NoSuchProviderException e) {
			logger.log(Level.SEVERE, "There is no server at the POP3 address.",
					e);
			retVal = false;
		} catch (AuthenticationFailedException e) {
			logger.log(Level.SEVERE,
					"There is a with the username or password.", e);
			retVal = false;
		} catch (MessagingException e) {
			logger.log(Level.SEVERE, "There is a problem with the message.", e);
			retVal = false;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "There has been an unknown error", e);
			retVal = false;
		} finally {
			try {
				if (folder != null)
					folder.close(true);
				if (store != null)
					store.close();
			} catch (Exception ex2) {
				logger.log(
						Level.SEVERE,
						"There has been an error closing a folder\non the POP3 server.",
						ex2);
			}
		}
		return retVal;
	}

	/*
	 * Processes all the messages into their respective beans.
	 */
	private boolean process(Message[] messages) {

		boolean retVal = true;
		MailMessage mmd = null;

		for (int msgNum = 0; msgNum < messages.length; msgNum++) {
			mmd = new MailMessage();
			try {
				String from = null;
				Address[] addresses = null;
				ArrayList<String> cc = new ArrayList<String>();
				ArrayList<String> to = new ArrayList<String>();
				try {
					from = ((InternetAddress) messages[msgNum].getFrom()[0])
							.getPersonal();
					addresses = messages[msgNum]
							.getRecipients(RecipientType.CC);
					if (addresses != null) {
						for (Address a : addresses)
							cc.add(a.toString());
					}
					addresses = messages[msgNum]
							.getRecipients(RecipientType.TO);
					if (addresses != null) {
						for (Address a : addresses)
							to.add(a.toString());
					}
					if (from == null)
						from = ((InternetAddress) messages[msgNum].getFrom()[0])
								.getAddress();
					else
						from = ((InternetAddress) messages[msgNum].getFrom()[0])
								.getAddress();
					mmd.setSenderEmail(from);
					mmd.setReceiverEmail(to);
					mmd.setCc(cc);

				} catch (AddressException e) {
					from = "";
				}
				String subject = messages[msgNum].getSubject();
				mmd.setSubject(subject);

				Date date = messages[msgNum].getSentDate();
				mmd.setMessageDate(date);

				Part messagePart = messages[msgNum];
				String msgText = getMessageText(messagePart);
				mmd.setMessage(msgText);
				mmd.setFolderId(1);

				mailMessageList.add(mmd);

				messages[msgNum].setFlag(Flags.Flag.DELETED, true);

			} catch (MessagingException e) {
				logger.log(Level.SEVERE,
						"There is a problem reading a message.", e);
				retVal = false;
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "There has been an unknown error.", ex);
				retVal = false;
			}
		}
		return retVal;
	}

	/*
	 * Processes parts of the messages.
	 */
	private String getMessageText(Part part) throws MessagingException,
			IOException {

		if (part.isMimeType("text/*")) {
			String content = (String) part.getContent();
			return content;
		}

		if (part.isMimeType("multipart/alternative")) {
			Multipart multiPart = (Multipart) part.getContent();
			String text = null;
			for (int i = 0; i < multiPart.getCount(); i++) {
				Part bodyPart = multiPart.getBodyPart(i);
				if (bodyPart.isMimeType("text/html")) {
					if (text == null)
						text = getMessageText(bodyPart);
					continue;
				} else if (bodyPart.isMimeType("text/plain")) {
					String bodyPartMessageText = getMessageText(bodyPart);
					if (bodyPartMessageText != null)
						return bodyPartMessageText;
				} else {
					return getMessageText(bodyPart);
				}
			}
			return text;
		} else if (part.isMimeType("multipart/*")) {
			Multipart multiPart = (Multipart) part.getContent();
			for (int i = 0; i < multiPart.getCount(); i++) {
				String bodyPartMessageText = getMessageText(multiPart
						.getBodyPart(i));
				if (bodyPartMessageText != null)
					return bodyPartMessageText;
			}
		}
		return null;
	}
}
