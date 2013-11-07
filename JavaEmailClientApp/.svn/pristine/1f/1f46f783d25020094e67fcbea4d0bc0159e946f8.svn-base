package jeep.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * This class is used to create a custom document to limit the amount of
 * characters that can be written into a field.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class DocumentLimit extends PlainDocument {
	private static final long serialVersionUID = -6866642890559876889L;
	private int limit;

	/**
	 * Constructor
	 * 
	 * @param limit
	 *            The limit of characters
	 */
	public DocumentLimit(int limit) {
		super();
		this.limit = limit;
	}

	/**
	 * Constructor
	 * 
	 * @param limit
	 *            The limit of characters
	 * @param upper
	 */
	public DocumentLimit(int limit, boolean upper) {
		super();
		this.limit = limit;
	}

	/**
	 * Inserts some content into the document.
	 */
	public void insertString(int offset, String str, AttributeSet attr)
			throws BadLocationException {
		if (str == null)
			return;

		if ((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, attr);
		}
	}
}
