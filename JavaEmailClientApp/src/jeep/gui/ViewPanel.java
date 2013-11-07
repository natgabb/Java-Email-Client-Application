package jeep.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import jeep.data.beans.MailMessage;
import jeep.data.bundles.MessageController;
import jeep.data.mysql.DatabaseController;
import jeep.gui.dnd.TableRowTransferHandler;
import jeep.gui.focus.MyFocusTravelPolicy;

/**
 * This panel displays a table with the messages from a specific folder, as well
 * as a view panel to display the information contained within these messages.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class ViewPanel extends JPanel {

	private static final long serialVersionUID = -2207425570312903903L;

	private MailApp mailApp = null;
	private Logger logger = null;
	private MessageTableModel messageTableModel = null;
	private JTable table = null;
	private JScrollPane scrollPan = null;

	private MessageController messages = null;
	private DatabaseController dbController = null;

	JLabel headingLabel = null;

	// SOUTH PANELS
	private JPanel displayPanel = null;
	JEditorPane textArea = null;
	JTextField fromField = null;
	JTextField sentField = null;
	JTextField toField = null;
	JTextField ccField = null;
	MailMessage currentMessage = null;

	JButton replyButton = null;
	JButton deleteButton = null;

	private MyFocusTravelPolicy focusPolicy = null;

	/**
	 * Constructor
	 * 
	 * @param mailApp
	 *            The MailApp
	 */
	public ViewPanel(MailApp mailApp) {
		this.mailApp = mailApp;
		logger = mailApp.getLogger();
		messageTableModel = mailApp.getMessageTableModel();
		dbController = mailApp.getDbController();
		messages = mailApp.getMessageController();
		this.setLayout(new BorderLayout());
		getTable();
		getDisplayPanel();
		Font font = new Font("Times New Roman", Font.BOLD, 20);
		headingLabel = new JLabel(" ", JLabel.CENTER);
		headingLabel.setFont(font);
		headingLabel.setIcon(MailApp.createImageIcon("images/view.png"));
		add(headingLabel, BorderLayout.NORTH);
		add(scrollPan, BorderLayout.CENTER);
		add(displayPanel, BorderLayout.SOUTH);
		// Setting the Focus Travel Policy.
		Vector<Component> order = new Vector<Component>(3);
		order.add(table);
		order.add(replyButton);
		order.add(deleteButton);
		focusPolicy = new MyFocusTravelPolicy(order);
		this.setFocusTraversalPolicy(focusPolicy);
	}

	/**
	 * Tries to print the currently selected message.
	 * 
	 * @return If the printing was successful.
	 */
	public boolean printPage() {
		JEditorPane printingPane = new JEditorPane();
		try {
			String text = "";
			if (currentMessage != null) {
				text = messages.getString("view_print_heading") + " "
						+ headingLabel.getText() + "\n\n"
						+ messages.getString("table_messages_date") + ": "
						+ currentMessage.getMessageDate() + "\n"
						+ messages.getString("view_from") + " "
						+ fromField.getText() + "\n"
						+ messages.getString("view_to") + " "
						+ toField.getText() + "\n"
						+ messages.getString("view_cc") + " "
						+ ccField.getText() + "\n"
						+ messages.getString("view_subject") + " "
						+ currentMessage.getSubject() + "\n"
						+ messages.getString("view_print_message") + "\n"
						+ textArea.getText();
			}
			printingPane.setText(text);
			printingPane.print(null, null);
			return true;
		} catch (PrinterException e) {
			logger.log(Level.WARNING,
					"Was unable to print: " + printingPane.getText(), e);
			return false;
		}
	}

	/*
	 * Instantiates the table and populates it from the model.
	 */
	private void getTable() {

		if (scrollPan != null)
			remove(scrollPan);

		table = new JTable(messageTableModel);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setPreferredScrollableViewportSize(new Dimension(600, 200));
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDragEnabled(true);
		table.setTransferHandler(new TableRowTransferHandler(table));

		// Create the scroll pane and add the table to it.
		scrollPan = new JScrollPane(table);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				MailMessage message = messageTableModel.getMailMessage(table
						.getSelectedRow());
				if (message != null)
					setDisplayPanel(message);
			}
		});
	}

	/*
	 * Instantiates the display panel.
	 */
	private void getDisplayPanel() {
		displayPanel = new JPanel();
		displayPanel.setLayout(new GridBagLayout());

		JLabel label;

		// FROM label & field
		label = new JLabel(messages.getString("view_from"));
		fromField = new JTextField(30);
		fromField.setEditable(false);
		displayPanel.add(label, MailApp.makeConstraints(0, 0, 1, 1, new int[] {
				1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));
		displayPanel.add(fromField, MailApp.makeConstraints(1, 0, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// SENT label & field
		label = new JLabel(messages.getString("view_sent"));
		sentField = new JTextField(30);
		sentField.setEditable(false);
		displayPanel.add(label, MailApp.makeConstraints(0, 1, 1, 1, new int[] {
				1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		displayPanel.add(sentField, MailApp.makeConstraints(1, 1, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// TO label & field
		label = new JLabel(messages.getString("view_to"));
		toField = new JTextField(30);
		toField.setEditable(false);
		displayPanel.add(label, MailApp.makeConstraints(0, 2, 1, 1, new int[] {
				1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		displayPanel.add(toField, MailApp.makeConstraints(1, 2, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// CC label & field
		label = new JLabel(messages.getString("view_cc"));
		ccField = new JTextField(30);
		ccField.setEditable(false);
		displayPanel.add(label, MailApp.makeConstraints(0, 3, 1, 1, new int[] {
				1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		displayPanel.add(ccField, MailApp.makeConstraints(1, 3, 3, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// EditorPane
		textArea = new JEditorPane();
		textArea.setEditable(false);
		textArea.setPreferredSize(new Dimension(500, 100));
		textArea.setMinimumSize(new Dimension(500, 100));
		textArea.setMaximumSize(new Dimension(500, 100));

		// Put it inside a ScrollPane
		JScrollPane scroll = new JScrollPane(textArea);

		displayPanel.add(scroll, MailApp.makeConstraints(0, 4, 4, 1, new int[] {
				1, 1, 1, 1 }, 1, 1, GridBagConstraints.BOTH));

		// Buttons
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridBagLayout());

		// Reply button
		replyButton = new JButton(messages.getString("view_reply"));
		replyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!fromField.getText().isEmpty()) {
					mailApp.getNewPanel().setReply(fromField.getText(),
							"RE: " + currentMessage.getSubject());
					mailApp.changeDisplayPanel(MailApp.NEW_PANEL);
				}
			}

		});
		buttons.add(replyButton, MailApp.makeConstraints(0, 1, 1, 1, new int[] {
				1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		// Delete button
		deleteButton = new JButton(messages.getString("view_delete"));
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if (row != -1) {
					MailMessage message = messageTableModel.getMailMessage(row);
					dbController.deleteMessage(message.getMessageId());
					messageTableModel.loadData(dbController
							.getMessagesInFolder(headingLabel.getText()));
					setDisplayPanel(new MailMessage());
				}
			}

		});
		buttons.add(deleteButton, MailApp.makeConstraints(1, 1, 1, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));

		displayPanel.add(buttons, MailApp.makeConstraints(0, 5, 4, 1,
				new int[] { 1, 1, 1, 1 }, 1, 1, GridBagConstraints.HORIZONTAL));
	}

	/**
	 * Sets what message is currently displayed in the display panel.
	 * 
	 * @param message
	 *            The message to be displayed.
	 */
	public void setDisplayPanel(MailMessage message) {
		fromField.setText(message.getSenderEmail());
		if (message.getMessageDate() != null)
			sentField.setText(message.getMessageDate().toString());
		else
			sentField.setText("");
		toField.setText(DatabaseController.emailListToString(message
				.getReceiverEmail()));
		ccField.setText(DatabaseController.emailListToString(message.getCc()));
		textArea.setText(message.getMessage());
		currentMessage = message;
	}

	/**
	 * Sets the heading, which is usually the currently selected folder name.
	 * 
	 * @param heading
	 *            The folder name.
	 */
	public void setHeading(String heading) {
		headingLabel.setText(heading);
	}

}
