package jeep.gui;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import jeep.data.beans.MailMessage;
import jeep.data.bundles.MessageController;

/**
 * This is the model for the messages' table.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class MessageTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -741654996687365388L;

	private Vector<MailMessage> data = new Vector<MailMessage>();
	private Vector<String> columnNames = new Vector<String>();

	private String[] headings = new String[3];

	/**
	 * Constructor
	 * 
	 * @param messages
	 *            The messages that will be used to get the headings' names.
	 */
	public MessageTableModel(MessageController messages) {
		headings[0] = messages.getString("table_messages_from");
		headings[1] = messages.getString("table_messages_subject");
		headings[2] = messages.getString("table_messages_date");
		for (String h : headings)
			columnNames.addElement(h);
	}

	/**
	 * Loads the data from the list of messages sent in.
	 * 
	 * @param newMessages
	 *            The list of messages.
	 */
	public void loadData(ArrayList<MailMessage> newMessages) {
		data.removeAllElements();
		for (MailMessage m : newMessages)
			data.add(m);
		this.fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return (String) columnNames.elementAt(col);
	}

	/**
	 * Returns the message at the given row.
	 * 
	 * @param row
	 *            The row
	 * @return The message
	 */
	public MailMessage getMailMessage(int row) {
		if (row >= 0 && row < data.size())
			return data.elementAt(row);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return data.size();
	}

	/**
	 * Returns the number of columns.
	 */
	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	/**
	 * Returns the value at the give row and col.
	 */
	@Override
	public Object getValueAt(int row, int col) {
		if (row < data.size() && col < columnNames.size())
			switch (col) {
			case 0:
				return data.elementAt(row).getSenderEmail();
			case 1:
				return data.elementAt(row).getSubject();
			case 2:
				return data.elementAt(row).getMessageDate();
			}
		// This should never happen.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/**
	 * Removes the row at the given row.
	 * 
	 * @param row
	 *            The row
	 */
	public void removeRowAt(int row) {
		data.remove(row);
		this.fireTableDataChanged();
	}
}
