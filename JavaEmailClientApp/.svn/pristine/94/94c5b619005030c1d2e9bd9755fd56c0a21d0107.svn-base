package jeep.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jeep.data.beans.Contact;
import jeep.data.beans.MailConfig;
import jeep.data.beans.MailMessage;
import jeep.data.bundles.MessageController;
import jeep.data.configuration.ConfigurationController;
import jeep.data.mysql.DatabaseController;
import jeep.mail.MailReceiveController;
import jeep.mail.MailSendController;

import javax.help.*;

/**
 * This is the main GUI app for the JEEP application.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class MailApp extends JFrame {
	private Logger logger = Logger.getLogger(getClass().getName());
	static {
		final InputStream inputStream = MailApp.class
				.getResourceAsStream("/logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(inputStream);

		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe(
					"Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}

	public static final boolean DEBUG = false;
	private static final long serialVersionUID = -6807928585770576304L;

	private MessageController messages = null;
	private ConfigurationController configuration = null;

	private DatabaseController dbController = null;

	String currentPanel = null;
	public final static String VIEW_PANEL = "View";
	public final static String NEW_PANEL = "New";
	public final static String CONTACT_PANEL = "Contact";
	public final static String SETTINGS_PANEL = "Settings";

	private TreePanel treePanel = null;

	private JPanel cardPanel = null;
	private ViewPanel viewPanel = null;
	private NewPanel newPanel = null;

	private ContactPanel contactPanel = null;
	private SettingsPanel settingsPanel = null;

	private MessageTableModel messageTableModel = null;
	private ContactTableModel contactTableModel = null;

	private boolean connected = false;

	// Help variables.
	private HelpSet hs = null;
	private HelpBroker hb = null;
	private String helpHS;

	/**
	 * Sets up the main app.
	 */
	public MailApp() {
		super();
		// Loads the configuration properties.
		configuration = new ConfigurationController(logger);
		configuration.loadProperties();
		MailConfig mailConfig = configuration.getMailConfig();

		// Loads the messages from the language stored in the configurations.
		messages = new MessageController(Locale.forLanguageTag(mailConfig
				.getLanguage()));

		makeHelp();
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"The look and feel you selected is not supported.", e);
		}
		populateGUI();
	}

	/*
	 * Instantiates the help objects.
	 */
	private void makeHelp() {
		// Find the HelpSet file and create the HelpSet object:
		helpHS = "hs/main.hs";
		ClassLoader cl = MailApp.class.getClassLoader();
		try {
			URL hsURL = HelpSet.findHelpSet(cl, helpHS);
			hs = new HelpSet(null, hsURL);
		} catch (Exception ee) {
			logger.log(Level.SEVERE, "HelpSet " + helpHS + " not found", ee);
			return;
		}
		// Create a HelpBroker object:
		hb = hs.createHelpBroker();
	}

	/**
	 * Retrieves the person's e-mails from their email provider's POP3 server.
	 * 
	 * @return Whether it was successful or not.
	 */
	public boolean retrieveEmails() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		boolean successful = false;

		MailReceiveController mailController = new MailReceiveController(
				configuration.getMailConfig(), logger);
		ArrayList<MailMessage> msgs = mailController.getMail();
		if (msgs != null) {
			successful = true;
			for (MailMessage m : msgs) {
				dbController.insertMessage(m);
			}
			ArrayList<MailMessage> messages = dbController
					.getMessagesInFolder(1);
			messageTableModel.loadData(messages);
			viewPanel.setHeading("Inbox");
		}

		this.setCursor(Cursor.getDefaultCursor());
		return successful;
	}

	/**
	 * Sends all the e-mails in the ToSend folder to it's destination.
	 * 
	 * @return Whether it was successful.
	 */
	public boolean sendEmailsInToSend() {
		if (MailApp.DEBUG)
			System.out.println("\n\nABOUT TO SEND EMAILS\nMail Config:\n"
					+ configuration.getMailConfig() + "\n\n");
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		boolean isValid = true;

		ArrayList<MailMessage> messagesToSend = dbController
				.getMessagesInFolder(NewPanel.TOSEND_FOLDER);
		MailSendController mailController = new MailSendController(
				configuration.getMailConfig(), logger);
		for (MailMessage message : messagesToSend) {
			if (MailApp.DEBUG)
				System.out.println("\n\nTRYING TO SEND: " + message + "\n\n");
			if (!mailController.sendMail(message))
				isValid = false;
		}
		if (isValid) {
			// Change the folder id from TOSEND to SENT.
			MailMessage message = null;
			for (int i = 0; i < messagesToSend.size(); i++) {
				message = messagesToSend.get(i);
				message.setFolderId(NewPanel.SENT_FOLDER);
				dbController.updateMessage(message);
			}
		}

		this.setCursor(Cursor.getDefaultCursor());
		return isValid;
	}

	/**
	 * This populates the frame with all the components.
	 */
	public void populateGUI() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		connected = false;
		this.getContentPane().removeAll();

		MailConfig mailConfig = configuration.getMailConfig();

		// Create the models
		messageTableModel = new MessageTableModel(messages);
		contactTableModel = new ContactTableModel(messages);

		dbController = new DatabaseController(mailConfig.getUrlMySQLServer(),
				mailConfig.getPortMySQLServer(), mailConfig.getUserNameMySQL(),
				mailConfig.getPasswordMySQL(), mailConfig.getDatabaseMySQL(),
				logger);

		this.setPreferredSize(new Dimension(800, 600));
		// this.setLayout(new BorderLayout());
		setTitle(getMessageController().getString("applicationTitle"));

		// Add the MenuPanel
		setJMenuBar(createMenuBar());

		add(createToolBar(), BorderLayout.NORTH);

		// Add the content panel
		createCardLayoutPanel();
		add(cardPanel, BorderLayout.CENTER);

		// Add the TreePanel
		treePanel = new TreePanel(this);
		add(treePanel, BorderLayout.WEST);

		pack();
		this.setVisible(true);
		setLocationRelativeTo(null);

		ArrayList<Contact> contacts = dbController.getContacts();
		contactTableModel.loadData(contacts);

		connected = retrieveEmails();
		if (!connected) {
			JOptionPane.showMessageDialog(MailApp.this,
					messages.getString("main_receive_error_message"),
					messages.getString("main_error_title"),
					JOptionPane.ERROR_MESSAGE);
			this.changeDisplayPanel(SETTINGS_PANEL);
		}

		this.setCursor(Cursor.getDefaultCursor());
	}

	ActionListener menuBarListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			String panel = ((JMenuItem) e.getSource()).getActionCommand();
			switch (panel) {
			case "Send&Receive":
				sendAndReceive();
				break;
			case "Print":
				printCurrentPanel();
				break;
			case "Exit":
				System.exit(0);
				break;
			}
		}

	};

	ActionListener toolBarListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			String panel = ((JButton) e.getSource()).getActionCommand();
			if (panel.equals("SendAndReceive")) {
				sendAndReceive();
			} else
				MailApp.this.changeDisplayPanel(panel);
		}

	};

	/*
	 * Sends all messages in the ToSend folder and retreives emails from the
	 * user's email provider.
	 */
	private void sendAndReceive() {
		if (!retrieveEmails()) {
			JOptionPane.showMessageDialog(MailApp.this,
					messages.getString("main_receive_error_message"),
					messages.getString("main_error_title"),
					JOptionPane.ERROR_MESSAGE);
			MailApp.this.changeDisplayPanel(SETTINGS_PANEL);
		}
		if (!sendEmailsInToSend()) {
			JOptionPane.showMessageDialog(MailApp.this,
					messages.getString("main_send_error_message"),
					messages.getString("main_error_title"),
					JOptionPane.ERROR_MESSAGE);
			MailApp.this.changeDisplayPanel(SETTINGS_PANEL);
		}
	}

	/*
	 * Prints the currently selected panel.
	 */
	private void printCurrentPanel() {
		boolean worked = false;
		switch (currentPanel) {
		case VIEW_PANEL:
			worked = viewPanel.printPage();
			break;
		case NEW_PANEL:
			worked = newPanel.printPage();
			break;
		case CONTACT_PANEL:
			worked = contactPanel.printPage();
			break;
		case SETTINGS_PANEL:
			worked = settingsPanel.printPage();
			break;
		}

		if (!worked)
			JOptionPane.showMessageDialog(MailApp.this,
					messages.getString("main_print_error_message"),
					messages.getString("main_error_title"),
					JOptionPane.ERROR_MESSAGE);
	}

	/*
	 * Creates the menu bar.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		// FILE MENU
		menu = new JMenu(messages.getString("main_menu_file"));
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
				messages.getString("main_menu_file_text"));
		menu.setToolTipText(messages.getString("main_menu_file_text"));

		menuBar.add(menu);

		// Send All & Receive
		menuItem = new JMenuItem(
				messages.getString("main_menu_sendAndReceive"), KeyEvent.VK_E);

		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		menuItem.setIcon(MailApp
				.createImageIcon("images/sendAndReceive_small.png"));
		menuItem.getAccessibleContext().setAccessibleDescription(
				messages.getString("main_menu_sendAndReceive_text"));
		menuItem.setToolTipText(messages
				.getString("main_menu_sendAndReceive_text"));
		menuItem.setActionCommand("Send&Receive");
		menuItem.addActionListener(menuBarListener);

		menu.add(menuItem);

		// Print
		menuItem = new JMenuItem(messages.getString("main_menu_print"),
				KeyEvent.VK_P);

		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));
		menuItem.setIcon(MailApp.createImageIcon("images/print.png"));
		menuItem.getAccessibleContext().setAccessibleDescription(
				messages.getString("main_menu_print_text"));
		menuItem.setToolTipText(messages.getString("main_menu_print_text"));
		menuItem.setActionCommand("Print");
		menuItem.addActionListener(menuBarListener);

		menu.add(menuItem);

		// Draw a separator line in the menu
		menu.addSeparator();

		// Exit
		menuItem = new JMenuItem(messages.getString("main_menu_exit"),
				KeyEvent.VK_I);

		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				messages.getString("main_menu_exit_text"));
		menuItem.setToolTipText(messages.getString("main_menu_exit_text"));
		menuItem.setActionCommand("Exit");
		menuItem.addActionListener(menuBarListener);

		menu.add(menuItem);

		// HELP MENU
		menu = new JMenu(messages.getString("main_menu_help"));
		menu.setMnemonic(KeyEvent.VK_E);
		menu.getAccessibleContext().setAccessibleDescription(
				messages.getString("main_menu_help_text"));
		menu.setToolTipText(messages.getString("main_menu_help_text"));

		// Launch help
		menuItem = new JMenuItem(messages.getString("main_menu_launchhelp"),
				KeyEvent.VK_P);
		menuItem.setIcon(MailApp.createImageIcon("images/help.gif"));

		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				messages.getString("main_menu_launchhelp_text"));
		menuItem.setToolTipText(messages.getString("main_menu_launchhelp_text"));
		menuItem.setActionCommand("LaunchHelp");
		menuItem.addActionListener(new CSH.DisplayHelpFromSource(hb));

		menu.add(menuItem);
		menuBar.add(menu);

		return menuBar;
	}

	/*
	 * Creates the toolbar.
	 */
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		JButton button = null;

		// Send & Receive Button
		button = makeToolBarButton("sendAndReceive", "SendAndReceive",
				messages.getString("main_bar_sendAndReceive_desc"),
				messages.getString("main_bar_sendAndReceive_alt"));
		toolBar.add(button);

		// View Button
		button = makeToolBarButton("view", VIEW_PANEL,
				messages.getString("main_bar_view_desc"),
				messages.getString("main_bar_view_alt"));
		toolBar.add(button);

		// New Button
		button = makeToolBarButton("new", NEW_PANEL,
				messages.getString("main_bar_new_desc"),
				messages.getString("main_bar_new_alt"));
		toolBar.add(button);

		// Contacts Button
		button = makeToolBarButton("contacts", CONTACT_PANEL,
				messages.getString("main_bar_contacts_desc"),
				messages.getString("main_bar_contacts_alt"));
		toolBar.add(button);

		// Settings Button
		button = makeToolBarButton("settings", SETTINGS_PANEL,
				messages.getString("main_bar_settings_desc"),
				messages.getString("main_bar_settings_alt"));
		toolBar.add(button);

		toolBar.setFloatable(false);
		toolBar.setRollover(false);

		return toolBar;
	}

	/*
	 * Create the buttons that will be placed in the tool bar.
	 */
	private JButton makeToolBarButton(String imageName, String actionCommand,
			String toolTipText, String altText) {

		// Look for the image.
		String imgLocation = "images/" + imageName + ".png";
		URL imageURL = MailApp.class.getResource(imgLocation);

		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(toolBarListener);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			logger.log(Level.WARNING, "Resource not found: " + imgLocation);
		}

		return button;
	}

	/**
	 * This creates the card panel which contains the main GUI.
	 */
	private void createCardLayoutPanel() {
		cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout(5, 5));

		// Panels
		viewPanel = new ViewPanel(this);
		newPanel = new NewPanel(this);
		contactPanel = new ContactPanel(this);
		settingsPanel = new SettingsPanel(this);

		cardPanel.add(viewPanel, VIEW_PANEL);
		cardPanel.add(newPanel, NEW_PANEL);
		cardPanel.add(contactPanel, CONTACT_PANEL);
		cardPanel.add(settingsPanel, SETTINGS_PANEL);
		currentPanel = VIEW_PANEL;
	}

	/**
	 * Changes the panel in the card panel that is currently being displayed.
	 * 
	 * @param panel
	 *            The panel to display.
	 */
	public void changeDisplayPanel(String panel) {
		currentPanel = panel;
		((CardLayout) cardPanel.getLayout()).show(cardPanel, panel);
	}

	/**
	 * Returns the MessageController which contains all the strings for the
	 * application.
	 * 
	 * @return The MessageController.
	 */
	public MessageController getMessageController() {
		return messages;
	}

	/**
	 * Sets the MessageController with a new locale.
	 * 
	 * @param locale
	 *            The locale
	 */
	public void setMessageController(Locale locale) {
		messages = new MessageController(locale);
	}

	/**
	 * Returns the ConfigurationController.
	 * 
	 * @return The controller.
	 */
	public ConfigurationController getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the logger.
	 * 
	 * @return The logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Returns the ViewPanel.
	 * 
	 * @return The ViewPanel.
	 */
	public ViewPanel getViewPanel() {
		return viewPanel;
	}

	/**
	 * Returns the message table model.
	 * 
	 * @return The message table model.
	 */
	public MessageTableModel getMessageTableModel() {
		return messageTableModel;
	}

	/**
	 * Returns the contact table model.
	 * 
	 * @return The contact table model.
	 */
	public ContactTableModel getContactTableModel() {
		return contactTableModel;
	}

	/**
	 * Returns the database controller.
	 * 
	 * @return The database controller.
	 */
	public DatabaseController getDbController() {
		return dbController;
	}

	/**
	 * Returns the NewPanel.
	 * 
	 * @return The NewPanel.
	 */
	public NewPanel getNewPanel() {
		return newPanel;
	}

	/**
	 * Creates an ImageIcon from a path.
	 * 
	 * @param path
	 *            The path
	 * @return The ImageIcon
	 */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = MailApp.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			if (MailApp.DEBUG)
				System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Creates a GridBagConstraints object with the sent in values.
	 * 
	 * @param gridx
	 *            The column
	 * @param gridy
	 *            The row
	 * @param gridwidth
	 *            The width
	 * @param gridheight
	 *            The height
	 * @return The GridBagConstraints object
	 */
	public static GridBagConstraints makeConstraints(int gridx, int gridy,
			int gridwidth, int gridheight, int[] insets, double weightx,
			double weighty, int fill) {
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridheight = gridheight;
		constraints.gridwidth = gridwidth;
		constraints.gridx = gridx;
		constraints.gridy = gridy;

		// Default for all the components.
		constraints.insets = new Insets(insets[0], insets[1], insets[2],
				insets[3]);
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.fill = fill;

		return constraints;
	}

	/**
	 * Main method of the application. Gets the configuration for the whole app,
	 * and the messages. It displays the splash screen first and then the main
	 * app.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Displays Splash screen for 5 seconds.
		new SplashScreen(5000).showSplash();

		// Displays main app.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MailApp app = new MailApp();
				app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				app.setIconImage(createImageIcon("images/view.png").getImage());
			}
		});
	}
}
