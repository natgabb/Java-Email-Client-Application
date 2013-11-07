package jeep.gui;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import jeep.data.beans.Contact;
import jeep.data.bundles.MessageController;

/**
 * This model will populate the contacts' table.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class ContactTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 5423943180962769278L;
	private Vector<Contact> data = new Vector<Contact>();
	private Vector<String> columnNames = new Vector<String>();

	private String[] headings = new String[3];

	/**
	 * Constructor
	 * 
	 * @param messages
	 *            The messages that will be used to get the headings' names.
	 */
	public ContactTableModel(MessageController messages) {
		headings[0] = messages.getString("table_contacts_first");
		headings[1] = messages.getString("table_contacts_last");
		headings[2] = messages.getString("table_contacts_email");
		for (String h : headings)
			columnNames.addElement(h);
	}

	/**
	 * This loads the data from a list of contacts.
	 * 
	 * @param newContacts
	 */
	public void loadData(ArrayList<Contact> newContacts) {
		data.removeAllElements();
		for (Contact c : newContacts)
			data.addElement(c);
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
	 * This returns the contact at the given row.
	 * 
	 * @param row
	 *            The row
	 * @return The contact
	 */
	public Contact getContact(int row) {
		if (row >= 0 && row < data.size())
			return data.elementAt(row);
		else
			return null;
	}

	/**
	 * Returns the number of rows.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/**
	 * Returns the value at the given row and col.
	 */
	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return data.elementAt(row).getFirstName();
		case 1:
			return data.elementAt(row).getLastName();
		case 2:
			return data.elementAt(row).getEmail();
		}
		// This should never happen.
		return null;
	}

	/**
	 * Adds the given contact to the data vector.
	 * 
	 * @param contact
	 *            The contact to add.
	 */
	public void addNewContact(Contact contact) {
		data.add(contact);
		this.fireTableDataChanged();
	}

}
