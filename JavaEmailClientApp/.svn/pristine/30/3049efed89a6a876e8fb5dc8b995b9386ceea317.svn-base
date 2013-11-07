package jeep.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import jeep.data.beans.Contact;
import jeep.data.beans.MailMessage;
import jeep.data.bundles.MessageController;
import jeep.data.configuration.ConfigurationController;
import jeep.data.mysql.DatabaseController;
import jeep.gui.focus.MyFocusTravelPolicy;

/**
 * This panel displays the contact table as well as a new message area so that
 * the user may easily select who they wish to send their message to.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class NewPanel extends JPanel {

	private static final long serialVersionUID = 7540780360963221291L;

	public static final int TOSEND_FOLDER = 3;
	public static final int SENT_FOLDER = 2;

	private Logger logger = null;
	private ContactTableModel contactTableModel = null;
	private DatabaseController dbController = null;
	private MessageController messages = null;
	private ConfigurationController configuration = null;

	private MyFocusTravelPolicy focusPolicy = null;

	JLabel headingLabel = null;

	private JTable table = null;
	private JScrollPane scrollPan = null;

	private JPanel newMessagePanel = null;
	JEditorPane textArea = null;
	JTextField subjectField = null;
	JTextField toField = null;
	JTextField ccField = null;
	JTextField bccField = null;
	JLabel errorMessage = null;

	private JTextField fieldHasFocus = null;

	/**
	 * Constructor
	 * 
	 * @param mailApp
	 *            The MailApp
	 */
	public NewPanel(MailApp mailApp) {
		this.logger = mailApp.getLogger();
		this.configuration = mailApp.getConfiguration();
		this.messages = mailApp.getMessageController();
		this.contactTableModel = mailApp.getContactTableModel();
		this.dbController = mailApp.getDbController();
		this.setLayout(new BorderLayout());

		getTable();
		getNewMessagePanel();

		Font font = new Font("Times New Roman", Font.BOLD, 20);
		headingLabel = new JLabel(" ", JLabel.CENTER);
		headingLabel.setFont(font);
		headingLabel.setText(messages.getString("new_heading"));

		headingLabel.setIcon(MailApp.createImageIcon("images/new.png"));
		add(headingLabel, BorderLayout.NORTH);
		add(scrollPan, BorderLayout.CENTER);
		add(newMessagePanel, BorderLayout.SOUTH);
	}

	/**
	 * Prints the current new message and all its fields.
	 * 
	 * @return If the printing was successful.
	 */
	public boolean printPage() {
		JEditorPane printingPane = new JEditorPane();
		try {
			String text = "";
			text = messages.getString("new_heading") + "\n\n"
					+ messages.getString("table_messages_date") + ": "
					+ (new Date()).toString() + "\n"
					+ messages.getString("view_from") + " "
					+ configuration.getMailConfig().getEmailAddress() + "\n"
					+ messages.getString("view_to") + " " + toField.getText()
					+ "\n" + messages.getString("view_cc") + " "
					+ ccField.getText() + "\n" + messages.getString("view_bcc")
					+ " " + bccField.getText() + "\n"
					+ messages.getString("view_subject") + " "
					+ subjectField.getText() + "\n"
					+ messages.getString("new_message") + "\n"
					+ textArea.getText();

			printingPane.setText(text);
			printingPane.print(null, null);
			return true;
		} catch (PrinterException e) {
			logger.log(Level.WARNING,
					"Was unable to print: " + printingPane.getText(), e);
			return false;
		}
	}

	/**
	 * Creates the table and populates it from the model.
	 */
	private void getTable() {
		if (scrollPan != null)
			remove(scrollPan);

		table = new JTable(contactTableModel);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setPreferredScrollableViewportSize(new Dimension(600, 200));
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);

		// Create the scroll pane and add the table to it.
		scrollPan = new JScrollPane(table);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Contact contact = contactTableModel.getContact(table
						.getSelectedRow());
				addToEmailField(contact.getEmail());
			}
		});
	}

	/*
	 * Creates the new message panel which will contain all the fields for a new
	 * message.
	 */
	private void getNewMessagePanel() {
		newMessagePanel = new JPanel();
		newMessagePanel.setLayout(new GridBagLayout());

		JLabel label;

		// TO label & field
		label = new JLabel(messages.getString("view_to"));
		toField = new JTextField(30);
		toField.addFocusListener(fieldFocusListener);
		toField.setDocument(new DocumentLimit(500));
		newMessagePanel.add(label, MailApp.makeConstraints(0, 0, 1, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		newMessagePanel.add(toField, MailApp.makeConstraints(1, 0, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// CC label & field
		label = new JLabel(messages.getString("view_cc"));
		ccField = new JTextField(30);
		ccField.addFocusListener(fieldFocusListener);
		ccField.setDocument(new DocumentLimit(500));
		newMessagePanel.add(label, MailApp.makeConstraints(0, 1, 1, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		newMessagePanel.add(ccField, MailApp.makeConstraints(1, 1, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// BCC label & field
		label = new JLabel(messages.getString("view_bcc"));
		bccField = new JTextField(30);
		bccField.addFocusListener(fieldFocusListener);
		bccField.setDocument(new DocumentLimit(500));
		newMessagePanel.add(label, MailApp.makeConstraints(0, 2, 1, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		newMessagePanel.add(bccField, MailApp.makeConstraints(1, 2, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// Subject label & field
		label = new JLabel(messages.getString("view_subject"));
		subjectField = new JTextField(30);
		subjectField.setDocument(new DocumentLimit(100));
		newMessagePanel.add(label, MailApp.makeConstraints(0, 3, 1, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		newMessagePanel.add(subjectField, MailApp.makeConstraints(1, 3, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// EditorPane
		textArea = new JEditorPane();
		textArea.setPreferredSize(new Dimension(500, 100));
		textArea.setDocument(new DocumentLimit(65535));

		// Put it inside a ScrollPane
		JScrollPane scroll = new JScrollPane(textArea);

		newMessagePanel.add(scroll, MailApp.makeConstraints(0, 4, 4, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.BOTH));

		// Send button

		JButton sendButton = new JButton(messages.getString("new_send"));
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String error = null;
				// This will create a new MailMessage and set it's folder_id to
				// TOSEND.
				String toEmails = toField.getText().trim();
				String ccEmails = null;
				String bccEmails = null;
				String subject = null;
				String message = null;
				ArrayList<String> toList = null;
				ArrayList<String> ccList = null;
				ArrayList<String> bccList = null;

				if (toEmails.isEmpty())
					error = messages.getString("new_error_atleastone");
				else {
					ccEmails = ccField.getText().trim();
					bccEmails = bccField.getText().trim();
					subject = subjectField.getText().trim();
					if (subject.isEmpty())
						subject = "No Subject";
					message = textArea.getText();

					toList = DatabaseController.emailStringToList(toEmails);

					ccList = DatabaseController.emailStringToList(ccEmails);

					bccList = DatabaseController.emailStringToList(bccEmails);
					if (!checkIfEmailsAreValid(toList))
						error = messages.getString("new_error_invalidto");
					else if (!checkIfEmailsAreValid(ccList))
						error = messages.getString("new_error_invalidcc");
					else if (!checkIfEmailsAreValid(bccList))
						error = messages.getString("new_error_invalidbcc");
				}
				if (error == null) {
					// Everything is valid, continue.
					MailMessage newMessage = new MailMessage();
					newMessage.setSenderEmail(configuration.getMailConfig()
							.getEmailAddress());
					newMessage.setReceiverEmail(toList);
					newMessage.setCc(ccList);
					newMessage.setBcc(bccList);
					newMessage.setMessageDate(new Date());
					newMessage.setSubject(subject);
					newMessage.setMessage(message);
					newMessage.setFolderId(TOSEND_FOLDER);
					dbController.insertMessage(newMessage);
					clearAllFields();
					error = " ";
				}
				errorMessage.setText(error);
			}

		});
		newMessagePanel.add(sendButton, MailApp.makeConstraints(0, 5, 4, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));
		errorMessage = new JLabel(" ", JLabel.CENTER);
		errorMessage.setForeground(Color.RED);
		newMessagePanel.add(errorMessage, MailApp.makeConstraints(0, 6, 4, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// Setting the Focus Travel Policy.
		Vector<Component> order = new Vector<Component>(7);
		order.add(table);
		order.add(toField);
		order.add(ccField);
		order.add(bccField);
		order.add(subjectField);
		order.add(textArea);
		order.add(sendButton);
		focusPolicy = new MyFocusTravelPolicy(order);
		this.setFocusTraversalPolicy(focusPolicy);
	}

	/*
	 * Checks if each email in the list is a valid email.
	 */
	private boolean checkIfEmailsAreValid(ArrayList<String> emails) {
		String emailRegEx = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		for (String email : emails)
			if (!Pattern.matches(emailRegEx, email))
				return false;
		return true;
	}

	/*
	 * Adds the email sent in to the field that has the current focus.
	 */
	private void addToEmailField(String email) {
		if (fieldHasFocus != null) {
			String emails = fieldHasFocus.getText().trim();
			if (!emails.isEmpty()
					&& !emails.endsWith(DatabaseController.EMAIL_DELIMITER))
				emails += DatabaseController.EMAIL_DELIMITER;
			emails += email;
			fieldHasFocus.setText(emails);
		}
	}

	/**
	 * Clears all the fields in the form.
	 */
	public void clearAllFields() {
		textArea.setText("");
		subjectField.setText("");
		toField.setText("");
		ccField.setText("");
		bccField.setText("");
	}

	/**
	 * Sets up the form for a reply
	 * 
	 * @param to
	 *            The email to put in the to field.
	 * @param subject
	 *            The reply subject
	 */
	public void setReply(String to, String subject) {
		toField.setText(to);
		subjectField.setText(subject);
	}

	/*
	 * This listens to see if any of the three recipient fields (TO, CC, BCC)
	 * have gained focus and then saves which field had last focus.
	 */
	private FocusListener fieldFocusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			JTextField field = (JTextField) e.getSource();
			fieldHasFocus = field;
		}

		@Override
		public void focusLost(FocusEvent e) {
			// Nothing needs to be done.
		}

	};
}
