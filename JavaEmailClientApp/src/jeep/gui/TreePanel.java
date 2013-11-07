package jeep.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jeep.data.beans.MailFolder;
import jeep.data.beans.MailMessage;
import jeep.data.bundles.MessageController;
import jeep.data.mysql.DatabaseController;
import jeep.gui.dnd.JTreeTransferHandler;

/**
 * This panel takes care of displaying a tree of folders. It also lets the user
 * add, delete and update the folders (except for the 3 default folders).
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class TreePanel extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 59783497902323798L;

	private Logger logger = null;

	private MailApp mailApp = null;
	private ViewPanel viewPanel = null;
	private DatabaseController dbController = null;
	private MessageTableModel messageTableModel = null;
	private MessageController messages = null;

	private List<MailFolder> allFolders = null;
	private JTree tree = null;
	private DefaultMutableTreeNode top = null;
	private JScrollPane treeView = null;

	private MailFolder lastClickedFolder = null;
	private int lastClickedFolderRow;

	/**
	 * Constructor
	 * 
	 * @param mailApp
	 *            The MailApp
	 */
	public TreePanel(MailApp mailApp) {
		this.mailApp = mailApp;
		this.messages = mailApp.getMessageController();
		logger = mailApp.getLogger();
		this.messageTableModel = mailApp.getMessageTableModel();
		this.viewPanel = mailApp.getViewPanel();
		this.dbController = mailApp.getDbController();
		createTree();
		this.add(treeView, BorderLayout.CENTER);
	}

	/**
	 * This creates the tree from the folders stored in the database.
	 */
	private void createTree() {
		allFolders = dbController.getFolders();
		top = new DefaultMutableTreeNode("Mail");

		for (MailFolder mf : allFolders) {
			DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(mf);
			top.add(dmtn);
		}

		tree = new JTree(top);
		tree.setPreferredSize(new Dimension(150, 400));
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

		tree.setDropMode(DropMode.ON);
		tree.setTransferHandler(new JTreeTransferHandler(tree,
				messageTableModel, dbController, logger));

		ImageIcon leafIcon = MailApp.createImageIcon("images/folder_icon.png");
		if (leafIcon != null) {
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setLeafIcon(leafIcon);
			tree.setCellRenderer(renderer);
		} else {
			logger.log(Level.WARNING, "Leaf icon missing; using default.");
		}

		// Make the popup menu
		makeTreePopupMenu();

		tree.addTreeSelectionListener(this);
		tree.setRootVisible(false);

		treeView = new JScrollPane(tree);
	}

	/**
	 * Create the popup menu and attach it to the tree.
	 */
	private void makeTreePopupMenu() {
		JMenuItem menuItem;

		// Create the popup menu.
		JPopupMenu popup = new JPopupMenu();

		menuItem = new JMenuItem(messages.getString("tree_popup_add"));
		menuItem.setActionCommand("Add");
		menuItem.addActionListener(popupClick);
		popup.add(menuItem);

		menuItem = new JMenuItem(messages.getString("tree_popup_edit"));
		menuItem.setActionCommand("Edit");
		menuItem.addActionListener(popupClick);
		popup.add(menuItem);

		popup.addSeparator();

		menuItem = new JMenuItem(messages.getString("tree_popup_delete"));
		menuItem.setActionCommand("Delete");
		menuItem.addActionListener(popupClick);
		popup.add(menuItem);

		// Add mouse listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);

		// Add listener to the tree.
		tree.addMouseListener(popupListener);
	}

	/**
	 * If another tree node was selected, ot changes the values displayed in the
	 * tree accordingly.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (tree.getSelectionPath() != null) {
			String folderName = tree.getSelectionPath().getLastPathComponent()
					.toString();

			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			viewPanel.setDisplayPanel(new MailMessage());
			messageTableModel.loadData(dbController
					.getMessagesInFolder(folderName));
			this.setCursor(Cursor.getDefaultCursor());

			viewPanel.setHeading(folderName);
			mailApp.changeDisplayPanel(MailApp.VIEW_PANEL);
		}
	}

	/**
	 * This listens for a click for the popup menu. It uses the last clicked
	 * folder to know which folder to affect.
	 */
	private ActionListener popupClick = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem popUpButton = (JMenuItem) e.getSource();
			switch (popUpButton.getActionCommand()) {
			case "Add":
				String folderName = JOptionPane.showInputDialog(null,
						messages.getString("tree_popup_add_dialog_message"),
						messages.getString("tree_popup_add_dialog_title"),
						JOptionPane.INFORMATION_MESSAGE);
				if (folderName != null && !folderName.isEmpty()) {
					MailFolder newFolder = new MailFolder(-1, folderName);
					if (dbController.insertFolder(newFolder)) {
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
								newFolder);
						top.add(newNode);
						((DefaultTreeModel) tree.getModel()).reload();
					} else {
						JOptionPane
								.showMessageDialog(
										null,
										messages.getString("tree_popup_add_error_message")
												+ " " + folderName,
										messages.getString("tree_popup_error_title"),
										JOptionPane.WARNING_MESSAGE);
					}
				}

				break;
			case "Delete":
				if (lastClickedFolder != null)
					if (lastClickedFolder.getFolderId() != 1
							&& lastClickedFolder.getFolderId() != 2
							&& lastClickedFolder.getFolderId() != 3) {
						if (dbController.deleteFolder(lastClickedFolder
								.getFolderId())) {
							top.remove(lastClickedFolderRow);
							((DefaultTreeModel) tree.getModel()).reload();
						} else {
							JOptionPane
									.showMessageDialog(
											null,
											messages.getString("tree_popup_delete_error_message")
													+ " "
													+ lastClickedFolder
															.getName(),
											messages.getString("tree_popup_error_title"),
											JOptionPane.ERROR_MESSAGE);
						}
					}
				break;
			case "Edit":
				if (lastClickedFolder != null)
					if (lastClickedFolder.getFolderId() != 1
							&& lastClickedFolder.getFolderId() != 2
							&& lastClickedFolder.getFolderId() != 3) {
						String oldName = lastClickedFolder.getName();
						String newName = JOptionPane
								.showInputDialog(
										null,
										messages.getString("tree_popup_edit_dialog_message")
												+ " " + oldName + ": ",
										messages.getString("tree_popup_edit_dialog_title"),
										JOptionPane.INFORMATION_MESSAGE);
						if (newName != null && !newName.isEmpty()) {
							lastClickedFolder.setName(newName);
							if (!dbController.updateFolder(lastClickedFolder)) {
								JOptionPane
										.showMessageDialog(
												null,
												messages.getString("tree_popup_edit_error_message")
														+ " "
														+ oldName
														+ " --> " + newName,
												messages.getString("tree_popup_error_title"),
												JOptionPane.WARNING_MESSAGE);
								lastClickedFolder.setName(oldName);
							}
						}
					}
				break;
			}
		}

	};

	/**
	 * This displays a pop up menu at the appropriate times.
	 * 
	 * @author Natacha Gabbamonte
	 * 
	 */
	class PopupListener extends MouseAdapter {

		JPopupMenu popup;

		public PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e, false);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e, true);
		}

		/**
		 * This will show a pop up menu at the current location if the right
		 * mouse button click was pressed. Otherwise it will register the last
		 * clicked folder.
		 * 
		 * @param e
		 * @param pressed
		 */
		private void maybeShowPopup(MouseEvent e, boolean pressed) {
			// If this is the right mouse button
			if (e.isPopupTrigger()) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					popup.show(e.getComponent(), e.getX(), e.getY());
					pressed = false;
				}
			} else {
				// If this is the left mouse button
				if (pressed) {
					// Determine which leaf (row) we are nearest to
					int selRow = tree.getRowForLocation(e.getX(), e.getY());
					// Determine the path to this leaf
					TreePath selPath = tree.getPathForLocation(e.getX(),
							e.getY());
					if (selPath != null) {
						lastClickedFolder = (MailFolder) ((DefaultMutableTreeNode) selPath
								.getLastPathComponent()).getUserObject();
						lastClickedFolderRow = selRow;
						// We are near enough to a leaf
						if (MailApp.DEBUG) {
							if (selRow != -1) {
								if (e.getClickCount() == 1) {
									System.out.println("One click at row "
											+ selRow + " and path " + selPath);
								} else if (e.getClickCount() == 2) {
									System.out.println("Double click at row "
											+ selRow + " and path " + selPath);
								}
							}
						}
					}
				}
			}
		}
	}
}
