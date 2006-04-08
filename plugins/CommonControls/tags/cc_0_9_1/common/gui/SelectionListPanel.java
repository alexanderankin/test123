package common.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import org.gjt.sp.jedit.*;

import common.gui.actions.*;
import common.gui.util.*;

/**
 * A panel with two lists, allowing the user to move items between them.
 * All methods ensure that an element cannot appear in both lists simultaneously.
 *
 * @author    mace
 * @version   $Revision$ modified $Date$ by $Author$
 */
public class SelectionListPanel extends JPanel {
	/** The constant referring to the left list */
	public final static int LEFT = 0;
	/** The constant referring to the right list */
	public final static int RIGHT = 1;
	protected String title = "Select some options";
	protected ConstraintFactory cf;
	protected JButton moveRight;
	protected JButton moveLeft;
	protected ListPanel[] lists = new ListPanel[2];

	/**
	 * Creates a basic panel.
	 * You will want to use the other methods to customize the panel before using it.
	 */
	public SelectionListPanel() {
		cf = new ConstraintFactory();
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEtchedBorder());

		for (int i = 0; i < lists.length; i++) {
			lists[i] = new ListPanel("List " + i);
		}

		add(lists[LEFT], cf.buildConstraints(0, 0, 10, 20, cf.CENTER, cf.BOTH, 100, 100));
		add(buildMovePanel(), cf.buildConstraints(10, 0, 2, 10, cf.CENTER, cf.V, 0, 100));
		add(lists[RIGHT], cf.buildConstraints(12, 0, 10, 20, cf.CENTER, cf.BOTH, 100, 100));
	}

	private JPanel buildMovePanel() {
		CustomAction moveRightAction =
			new CustomAction("Move Right", GUIUtilities.loadIcon("ArrowR.png")) {
				public void actionPerformed(ActionEvent ae) {
					moveElements(LEFT, RIGHT);
				}
			};
		moveRight = new JButton(moveRightAction);
		moveRight.setText("");

		CustomAction moveLeftAction =
			new CustomAction("Move Left", GUIUtilities.loadIcon("ArrowL.png")) {
				public void actionPerformed(ActionEvent ae) {
					moveElements(RIGHT, LEFT);
				}
			};
		moveLeft = new JButton(moveLeftAction);
		moveLeft.setText("");

		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(moveRight, cf.buildConstraints(0, 0, 1, 1, cf.S, cf.NONE, 0, 0));
		panel.add(moveLeft, cf.buildConstraints(0, 1, 1, 1, cf.N, cf.NONE, 0, 0));
		return panel;
	}

	/**
	 * Returns the list which is not the given list.
	 */
	protected int otherList(int list) {
		return Math.abs(list-1);
	}

	protected void addElement(int list, Object element) {
		removeElement(otherList(list),element);
		lists[list].addElement(element);
	}

	protected boolean removeElement(int list, Object element) {
		return lists[list].removeElement(element);
	}

	protected void moveElements(int srcList, int destList) {
		Object[] selected = lists[srcList].getSelectedValues();
		for (int i = 0; i < selected.length; i++) {
			int index = lists[srcList].getLastSelectedIndex();
			removeElement(srcList, selected[i]);
			addElement(destList, selected[i]);
			lists[srcList].setSelectedIndex(index);
		}
	}

	/**
	 * Saves a list to a property array (first item stored in "propertyName.0").  All elements are treated as strings.
	 *
	 * @param list          Description of the Parameter
	 * @param propertyName  Description of the Parameter
	 */
	public void saveToPropertyAsString(int list, String propertyName) {
		Object[] elements = lists[list].toArray();
		int i;
		for (i = 0; i < elements.length; i++) {
			jEdit.setProperty(propertyName + "." + i, elements[i].toString());
		}
		jEdit.unsetProperty(propertyName+"."+i);
	}

	public void loadFromPropertyAsString(int list, String propertyName) {
		ArrayList elements = new ArrayList();
		int index = 0;
		String s = jEdit.getProperty(propertyName + "." + index);
		index++;
		while (s != null) {
			addElement(list, new String(s));
			s = jEdit.getProperty(propertyName + "." + index);
			index++;
		}
	}

	//{{{ Mutators
	/**
	 * Sets the title of the panel.
	 *
	 * @param text  The new title value
	 */
	public void setTitle(String text) {
		title = text;
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
	}

	/**
	 * Sets the label for a list.
	 *
	 * @param list  LEFT or RIGHT
	 * @param text  The new list label
	 */
	public void setListLabel(int list, String text) {
		lists[list].setLabel(text);
	}

	/**
	 * Sets the contents of a list to the given options.
	 *
	 * @param list     LEFT or RIGHT
	 * @param options  The new listContents value
	 */
	public void setListContents(int list, Object[] options) {
		int otherList = Math.abs(list - 1);
		for (int i = 0; i < options.length; i++) {
			removeElement(otherList, options[i]);
			addElement(list, options[i]);
		}
	}

	//}}}

	//{{{ Accessors
	/**
	 * Returns all selected values in the given list.
	 *
	 * @param list  LEFT or RIGHT
	 * @return      all selected values in the given list
	 */
	public Object[] getSelectedValues(int list) {
		return lists[list].getSelectedValues();
	}

	/**
	 * Returns all values in the given list.
	 *
	 * @param list  LEFT or RIGHT
	 * @return      all values in the given list
	 */
	public Object[] getValues(int list) {
		return lists[list].toArray();
	}
	//}}}
}

