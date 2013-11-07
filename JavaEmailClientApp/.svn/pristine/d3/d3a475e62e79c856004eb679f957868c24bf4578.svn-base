package jeep.gui.focus;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Vector;

/**
 * Custom FocusTraversalPolicy When the user presses Tab or Shift-Tab this
 * determines where the focus goes
 * 
 * @author neon
 * 
 */
public class MyFocusTravelPolicy extends FocusTraversalPolicy {

	Vector<Component> order;

	/**
	 * Constructor Copies the contents of the vector parameter to the class
	 * vector
	 * 
	 * @param order
	 */
	public MyFocusTravelPolicy(Vector<Component> order) {
		this.order = new Vector<Component>(order.size());
		this.order.addAll(order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.FocusTraversalPolicy#getComponentAfter(java.awt.Container,
	 * java.awt.Component)
	 */
	public Component getComponentAfter(Container focusCycleRoot,
			Component aComponent) {
		int idx = (order.indexOf(aComponent) + 1) % order.size();
		return order.get(idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.FocusTraversalPolicy#getComponentBefore(java.awt.Container,
	 * java.awt.Component)
	 */
	public Component getComponentBefore(Container focusCycleRoot,
			Component aComponent) {
		int idx = order.indexOf(aComponent) - 1;
		if (idx < 0) {
			idx = order.size() - 1;
		}
		return order.get(idx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.FocusTraversalPolicy#getDefaultComponent(java.awt.Container)
	 */
	public Component getDefaultComponent(Container focusCycleRoot) {
		return order.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.FocusTraversalPolicy#getLastComponent(java.awt.Container)
	 */
	public Component getLastComponent(Container focusCycleRoot) {
		return order.lastElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.FocusTraversalPolicy#getFirstComponent(java.awt.Container)
	 */
	public Component getFirstComponent(Container focusCycleRoot) {
		return order.get(0);
	}

}
