package jeep.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 * This class handles the transfer of record on the Jtable side. It sends out
 * the row id of the selection.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class TableRowTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	// This is the DataFlavor we will drag
	private final DataFlavor localObjectFlavor = new ActivationDataFlavor(
			Integer.class, DataFlavor.javaJVMLocalObjectMimeType,
			"Integer Row Index");
	private JTable table = null;

	/**
	 * Constructor
	 * 
	 * @param table
	 */
	public TableRowTransferHandler(JTable table) {
		this.table = table;
	}

	/**
	 * @see
	 * javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		assert (c == table);
		return new DataHandler(new Integer(table.getSelectedRow()),
				localObjectFlavor.getMimeType());
	}

	/**
	 * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

}