package jeep.data.beans;

/**
 * This is a java bean for the database Folder table.
 * 
 * @author Natacha Gabbamonte 0932340
 * 
 */
public class MailFolder {

	private int folderId;
	private String name = null;

	/**
	 * Constructor, no parameters. Sets folder id to -1 and name to "".
	 */
	public MailFolder() {
		super();
		this.folderId = -1;
		this.name = "";
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param folderId
	 * @param name
	 */
	public MailFolder(int folderId, String name) {
		super();
		this.folderId = folderId;
		this.name = name;
	}

	/**
	 * Returns the folder id.
	 * 
	 * @return The folder id.
	 */
	public int getFolderId() {
		return folderId;
	}

	/**
	 * Sets the folder id.
	 * 
	 * @param folderId
	 *            The folder id.
	 */
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	/**
	 * Returns the name of the folder.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the folder.
	 * 
	 * @param name
	 *            The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a String representation of Folder object.
	 */
	@Override
	public String toString() {
		return name;
	}
}
