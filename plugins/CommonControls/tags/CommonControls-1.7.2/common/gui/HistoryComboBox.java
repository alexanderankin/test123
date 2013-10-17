package common.gui;

import java.awt.event.*;
import javax.swing.*;

import org.gjt.sp.jedit.*;

/** A Combo Box that remembers a given number of previous entries, even between jEdit runs.
 *
 * @author    mace
 * @version   $Revision$ modified $Date$ by $Author$
 * @see org.gjt.sp.jedit.gui.HistoryTextField
 *
 * Should this be deprecated? I don't see the point of this class if we also have HistoryTextField.
 */
public class HistoryComboBox extends JComboBox<String> {
	private int entryCount;
	private DefaultComboBoxModel<String> model;
	private String propertyName;

	/**
	 * Build a combo box which will remember the given the given number
	 * of previous entries, and store them in the given property.
	 *
	 * @param entryCount  Description of the Parameter
	 * @param propName    Description of the Parameter
	 */
	public HistoryComboBox(int entryCount, String propName) {
		propertyName = propName;
		setEditable(true);
		model = new DefaultComboBoxModel<String>();
		setModel(model);
		this.entryCount = entryCount;
		if (propertyName != null)
			loadFromProperty(propertyName);
	}

	/**
	 * Creates a combo box that remembers the given number of entries
	 * but doesn't not remember them between sessions.
	 */
	public HistoryComboBox(int entryCount) {
		this(entryCount, null);
	}

	protected void addEntry() {
		model.addElement(getSelectedItem().toString());
		if (model.getSize() > entryCount) {
			model.removeElementAt(0);
		}
		setSelectedItem("");
		repaint();
	}

	public void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			fireActionEvent();
		}
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		addEntry();
		if (propertyName != null)
			storeToProperty(propertyName);
	}

	/**
	 * Stores this box's elements in the given property.
	 *
	 * @param name  Description of the Parameter
	 */
	protected void storeToProperty(String name) {
		int i = 1;
		while (i < getItemCount()) {
			//Log.log(Log.DEBUG,this,"Setting '"+name+"."+i+"' to '"+getItemAt(i)+"'");
			jEdit.setProperty(name + "." + i, getItemAt(i).toString());
			i++;
		}
		jEdit.unsetProperty(name + "." + i);
	}

	/**
	 * Loads elements into this box from the given property.
	 *
	 * @param name  Description of the Parameter
	 */
	protected void loadFromProperty(String name) {
		removeAllItems();
		int i = 1;
		String query = jEdit.getProperty(name + "." + i);
		while (query != null) {
			if (!query.equals("")) {
				addItem(query);
			}
			query = jEdit.getProperty(name + "." + i);
			i++;
		}
		setSelectedItem("");
	}
}

