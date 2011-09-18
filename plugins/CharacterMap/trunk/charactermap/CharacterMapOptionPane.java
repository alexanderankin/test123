/*
 *  CharacterMapOptionPane.java
 *  Copyright (c) 2003 Mark Wickens
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package charactermap;
 
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * Options pane displayed when Character Map
 * is selected in the Plugin Options... tree
 * Allows the user to customize the appearence of the
 * character map plugin.
 *
 * @author     mawic
 * @version    1.2
 */
public class CharacterMapOptionPane extends AbstractOptionPane
{
	/** Checkbox controlling display of large character  */
	private JCheckBox showLarge;
	/** Textfield containing size of large character in points */
	private JTextField largeSize;
	/** Panel containing components controlling large size character options */
	private JPanel largeSizePanel;
	/** Checkbox controlling display of super-size character */
	private JCheckBox showSuper;
	/** Textfield containing size of super character in points */
	private JTextField superSize;
	/** Panel containing components controlling super size character options */
	private JPanel superSizePanel;
	/* Checkbox controlling whether super character is offset */
	private JCheckBox superOffset;
	/** Spinnner controlling number of columns displayed in table */
	private JSpinner columnsSpinner;
	/** Model for spinner options */
	private SpinnerModel spinnerModel;
	/** Panel containing components controlling table columns */
	private JPanel columnsPanel;
	/** Checkbox controlling display of status line */
	private JCheckBox status;
	/** Checkbox controlling display of encoding combo box */
	private JCheckBox encoding;
	/** Checkbox controlling display of unicode blocks slider */
	private JCheckBox blocks;
	/** Checkbox controlling anti-aliasing */
	private JCheckBox antialias;
	
	/**
	 * Default constructor.
	 */
	public CharacterMapOptionPane()
	{
		super("character-map");
	}

	/**
	 * Create and initialise the options page with options
	 * and labels read from the properties for this plugin
	 */
	public void _init()
	{
		showLarge = this.createCheckBox("character-map.large", true);
		largeSize = this.createTextField(4, "character-map.large-size", "36");
		largeSize.setEnabled(showLarge.isSelected());
		showSuper = this.createCheckBox("character-map.super", true);
		superSize = this.createTextField(4, "character-map.super-size", "128");
		superSize.setEnabled(showSuper.isSelected());
		superOffset = this.createCheckBox("character-map.super-offset", true);
		superOffset.setEnabled(showSuper.isSelected());
		status = this.createCheckBox("character-map.status", true);
		encoding = this.createCheckBox("character-map.encoding", true);
		blocks = this.createCheckBox("character-map.blocks", true);
		antialias = this.createCheckBox("character-map.anti-alias", false);
		
		showLarge.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					boolean selected = showLarge.isSelected();
					largeSize.setEnabled(selected);
				}
			});
		showSuper.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					boolean selected = showSuper.isSelected();
					superSize.setEnabled(selected);
					superOffset.setEnabled(selected);
				}
			});

		//addComponent(new JLabel(jEdit.getProperty("character-map.options")));

		addComponent(this.showLarge);

		largeSizePanel = createLabelledComponent(largeSize,
			jEdit.getProperty("character-map.large-size"));
		addComponent(largeSizePanel);

		addComponent(this.showSuper);
		superSizePanel = createLabelledComponent(superSize,
			jEdit.getProperty("character-map.super-size"));
		addComponent(superSizePanel);

		addComponent(superOffset);

		Vector values = new Vector();
		for (int i = 8; i >= 0; i -= 1) {
			values.addElement(new Integer(1 << i));
		}
		spinnerModel = new SpinnerListModel(values);
		columnsSpinner = new JSpinner(spinnerModel);
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) columnsSpinner.getEditor();
		JTextField columnsTextField = editor.getTextField();
		columnsTextField.setColumns(3);

		int column = jEdit.getIntegerProperty("options.character-map.columns", 16);
		columnsSpinner.setValue(new Integer(column));
		columnsPanel = createLabelledComponent(columnsSpinner,
			jEdit.getProperty("character-map.columns"));
		addComponent(columnsPanel);

		addComponent(status);
		addComponent(encoding);
		addComponent(blocks);
		addComponent(antialias);
	}

	/**
	 * Store the options selected on the pane back to the 
	 * jedit properties.
	 */
	public void _save()
	{
		jEdit.setBooleanProperty("options.character-map.large", showLarge.isSelected());
		jEdit.setBooleanProperty("options.character-map.super", showSuper.isSelected());
		setIntegerPropertyFromTextField("options.character-map.large-size", largeSize, 36);
		setIntegerPropertyFromTextField("options.character-map.super-size", superSize, 128);
		jEdit.setBooleanProperty("options.character-map.super-offset", superOffset.isSelected());
		int column = ((Integer) columnsSpinner.getValue()).intValue();
		jEdit.setIntegerProperty("options.character-map.columns", column);
		jEdit.setBooleanProperty("options.character-map.status", status.isSelected());
		jEdit.setBooleanProperty("options.character-map.encoding", encoding.isSelected());
		jEdit.setBooleanProperty("options.character-map.blocks", blocks.isSelected());
		jEdit.setBooleanProperty("options.character-map.anti-alias", antialias.isSelected());
	}

	/**
	 * Determine the value of the given text field and store in the 
	 * named jedit property. If the valued does not parse as an integer,
	 * use the specified default instead.
	 * @param property The name of the jedit property to receive the value
	 * @param tf Text field containing the string to be parsed as an integer
	 * @param defaultValue If tf does not contain an integer, use this value instead
	 */
	private void setIntegerPropertyFromTextField(String property, JTextField tf, int defaultValue)
	{
		int value = defaultValue;
		String sValue = tf.getText();
		try {
			value = Integer.parseInt(sValue);
		}
		catch (NumberFormatException nfe) {
			value = jEdit.getIntegerProperty(property, defaultValue);
		}
		finally {
			jEdit.setIntegerProperty(property, value);
		}
	}

	/** 
	 * Create a JLabel containing the given string and put it together
	 * with the given component into a panel The two are separated by a colon,
	 * and are layed out using the flow layout manager.
	 * @param component Will appear after the label
	 * @param label Value to be assigned to label
	 * @return Panel containing label and given component
	 */
	private JPanel createLabelledComponent(JComponent component, String label)
	{
		JPanel rtn = new JPanel();
		rtn.setLayout(new FlowLayout());
		rtn.add(new JLabel(label + ": "));
		rtn.add(component);
		return rtn;
	}

	/**
	 * Create a checkbox with label as specified in the given property,
	 * and set the value to the property stored with the given name
	 * pre-pended with "options.". If the value property does not exist,
	 * use the given default.
	 * @param property Name of property and sub-string of property value
	 * @param defaultValue Used if value cannot be determined from property
	 * @return JCheckBox labelled with start value set
	 */
	private JCheckBox createCheckBox(String property, boolean defaultValue)
	{
		JCheckBox cb = new JCheckBox(jEdit.getProperty(property));
		cb.setSelected(jEdit.getBooleanProperty("options." + property, defaultValue));
		return cb;
	}

	/**
	 * Create a text field with value set to the property stored with 
	 * the given name pre-pended with "options.". If the value property 
	 * does not exist, use the given default. The initial width of the text field
	 * is set from the size parameter.
	 * @param size initial width (in characters) of the text field
	 * @param property Sub-string of property value
	 * @param defaultValue Used if value cannot be determined from property
	 * @return JTextfield with start value set and width specified
	 */
	private JTextField createTextField(int size, String property, String defaultValue)
	{
		JTextField tf = new JTextField(jEdit.getProperty("options." + property, defaultValue), size);
		return tf;
	}
}

