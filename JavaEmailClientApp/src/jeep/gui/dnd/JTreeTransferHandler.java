package jeep.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.ActivationDataFlavor;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import jeep.data.beans.MailFolder;
import jeep.data.beans.MailMessage;
import jeep.data.mysql.DatabaseController;
import jeep.gui.MessageTableModel;

/**
 * This class handles the transfer of record on the JTree side.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class JTreeTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	protected DefaultTreeModel tree = null;
	private MessageTableModel tableModel = null;
	private DatabaseController dbController = null;
	private Logger logger = null;

	private final DataFlavor localObjectFlavor = new ActivationDataFlavor(
			Integer.class, DataFlavor.javaJVMLocalObjectMimeType,
			"Integer Row Index");

	/**
	 * Creates a JTreeTransferHandler to handle a certain tree.
	 * 
	 * @param tree
	 */
	public JTreeTransferHandler(JTree tree, MessageTableModel tableModel,
			DatabaseController dbController, Logger logger) {
		super();
		this.logger = logger;
		this.tree = (DefaultTreeModel) tree.getModel();
		this.tableModel = tableModel;
		this.dbController = dbController;
	}

	/**
	 * This component is not a source for dragging
	 * 
	 * @param c
	 * @return
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.NONE;
	}

	/**
	 * Verify that the data dropped is the DataFlavor (Type) expected
	 * 
	 * @param supp
	 * @return
	 */
	@Override
	public boolean canImport(TransferSupport supp) {
		// Setup so we can always see what it is we are dropping onto.
		supp.setShowDropLocation(true);
		if (supp.isDataFlavorSupported(localObjectFlavor)) {
			return true;
		}
		// something prevented this import from going forward
		return false;
	}

	/**
	 * Extract the data from the drop from the table This data is the row number
	 * dropped
	 * 
	 * @param supp
	 * @return
	 */
	@Override
	public boolean importData(TransferSupport supp) {
		if (this.canImport(supp)) {
			try {
				// Fetch the data to transfer
				Transferable t = supp.getTransferable();
				int theRow = (Integer) t.getTransferData(localObjectFlavor);
				// Fetch the drop location
				TreePath loc = ((javax.swing.JTree.DropLocation) supp
						.getDropLocation()).getPath();
				MailFolder dropFolder = (MailFolder) ((DefaultMutableTreeNode) loc
						.getLastPathComponent()).getUserObject();

				MailMessage transferredMessage = tableModel
						.getMailMessage(theRow);
				transferredMessage.setFolderId(dropFolder.getFolderId());
				dbController.updateMessage(transferredMessage);
				tableModel.removeRowAt(theRow);
				return true;
			} catch (UnsupportedFlavorException e) {
				logger.log(
						Level.SEVERE,
						"Something went wrong when trying to import data into the JTree",
						e);
			} catch (IOException e) {
				logger.log(
						Level.SEVERE,
						"Something went wrong when trying to import data into the JTree",
						e);
			}
		}
		// import isn't allowed at this time.
		return false;
	}
}
