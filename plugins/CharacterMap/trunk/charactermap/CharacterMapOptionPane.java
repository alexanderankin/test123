/*
 *  CharacterMapOptionPane.java
 * :folding=explicit:collapseFolds=1:
 *
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

//{{{ Imports
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.gui.DockableWindowManager;
//import org.gjt.sp.util.Log;
//}}}

/**
 * Options pane displayed when Character Map
 * is selected in the Plugin Options... tree
 * Allows the user to customize the appearence of the
 * character map plugin.
 *
 * @author     mawic
 * @version    1.3
 */
public class CharacterMapOptionPane extends AbstractOptionPane
{
//{{{ Private Variables
	/** Checkbox controlling display of status line */
	private JCheckBox status;
	/** Checkbox controlling display of encoding combo box */
	private JCheckBox encoding;
	/** Checkbox controlling display of unicode blocks slider */
	private JCheckBox blocks;
	/** Enable higher Unicode planes (Characters above 65536 or 0xFFFF) */
	private JCheckBox higherplanes;
	/** Checkbox controlling anti-aliasing */
	private JCheckBox antialias;

	/** Spinner controlling number of columns displayed in floating table */
	private JSpinner columnsSpinner;
	/** Spinner controlling number of columns displayed in left/right docked table */
	private JSpinner columnsSpinnerDockLR;
	/** Spinner controlling number of columns displayed in top/bottom docked table */	
	private JSpinner columnsSpinnerDockTB;
	/** Model for spinner options (floating) */
	private SpinnerModel spinnerModel;
	/** Model for spinner options (docked left/right) */
	private SpinnerModel spinnerModelDockLR;
	/** Model for spinner options (docked top/bottom) */
	private SpinnerModel spinnerModelDockTB;

	/** Checkbox controlling display of large character  */
	private JCheckBox showLarge;
	/** Textfield containing size of large character in points */
	private JTextField largeSize;
	/** Checkbox controlling display of super-size character */
	private JCheckBox showSuper;
	/** Checkbox controlling whether super character is offset */
	private JCheckBox superOffset;
	/** Textfield containing size of super character in points */
	private JTextField superSize;
//}}}

//{{{ Constructor
		/**
	 * Default constructor.
	 */
	public CharacterMapOptionPane()
	{
		super("character-map-options");
	}
//}}}

//{{{ _init() method
	/**
	 * Create and initialise the options page with options
	 * and labels read from the properties for this plugin
	 */
	@Override
	public void _init()
	{
		//Initialise general option components
		status = new JCheckBox(jEdit.getProperty("options.character-map.status.label"),
			jEdit.getBooleanProperty("options.character-map.status", true));
		encoding = new JCheckBox(jEdit.getProperty("options.character-map.encoding.label"),
			jEdit.getBooleanProperty("options.character-map.encoding", true));
		blocks = new JCheckBox(jEdit.getProperty("options.character-map.blocks.label"),
			jEdit.getBooleanProperty("options.character-map.blocks", true));
		higherplanes = new JCheckBox(jEdit.getProperty("options.character-map.higherplanes.label"),
			jEdit.getBooleanProperty("options.character-map.higherplanes", false));
		antialias = new JCheckBox(jEdit.getProperty("options.character-map.antialias.label"),
			jEdit.getBooleanProperty("options.character-map.antialias", false));

		//Initialise table option components
		ArrayList<Integer> spinnerValues = new ArrayList<Integer>();
		for (int i = 8; i >= 0; i -= 1) {
			spinnerValues.add(new Integer(1 << i));
		}

		spinnerModel = new SpinnerListModel(spinnerValues);
		columnsSpinner = new JSpinner(spinnerModel);
		JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) columnsSpinner.getEditor();
		JTextField columnsTextField = spinnerEditor.getTextField();
		columnsTextField.setColumns(3);
		columnsTextField.setHorizontalAlignment(JTextField.LEFT);
		int column = jEdit.getIntegerProperty("options.character-map.columns", 16);
		columnsSpinner.setValue(new Integer(column));

		spinnerModelDockLR = new SpinnerListModel(spinnerValues);
		columnsSpinnerDockLR = new JSpinner(spinnerModelDockLR);
		spinnerEditor = (JSpinner.DefaultEditor) columnsSpinnerDockLR.getEditor();
		columnsTextField = spinnerEditor.getTextField();
		columnsTextField.setColumns(3);
		columnsTextField.setHorizontalAlignment(JTextField.LEFT);
		column = jEdit.getIntegerProperty("options.character-map.columns-dock-lr", 8);
		columnsSpinnerDockLR.setValue(new Integer(column));

		spinnerModelDockTB = new SpinnerListModel(spinnerValues);
		columnsSpinnerDockTB = new JSpinner(spinnerModelDockTB);
		spinnerEditor = (JSpinner.DefaultEditor) columnsSpinnerDockTB.getEditor();
		columnsTextField = spinnerEditor.getTextField();
		columnsTextField.setColumns(3);
		columnsTextField.setHorizontalAlignment(JTextField.LEFT);
		column = jEdit.getIntegerProperty("options.character-map.columns-dock-tb", 32);
		columnsSpinnerDockTB.setValue(new Integer(column));

		//Initialise character option components
		showLarge = new JCheckBox(jEdit.getProperty("options.character-map.large.label"),
			jEdit.getBooleanProperty("options.character-map.large", true));
		largeSize = new JTextField(jEdit.getProperty("options.character-map.large-size", "36"));
		largeSize.setEnabled(showLarge.isSelected());

		showSuper = new JCheckBox(jEdit.getProperty("options.character-map.super.label"),
			jEdit.getBooleanProperty("options.character-map.super", true));
		superOffset = new JCheckBox(jEdit.getProperty("options.character-map.super-offset.label"),
			jEdit.getBooleanProperty("options.character-map.super-offset", true));
		superOffset.setEnabled(showSuper.isSelected());
		superSize = new JTextField(jEdit.getProperty("options.character-map.super-size", "128"));
		superSize.setEnabled(showSuper.isSelected());

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

		//Display components

		addSeparator("options.character-map.separator-general.label");

		addComponent(status);

		addComponent(encoding);

		addComponent(blocks);

		addComponent(higherplanes);

		addComponent(antialias);


		addSeparator("options.character-map.separator-table.label");

		addComponent(jEdit.getProperty("options.character-map.columns.label"), columnsSpinner);

		addComponent(jEdit.getProperty("options.character-map.columns-dock-lr.label"), columnsSpinnerDockLR);

		addComponent(jEdit.getProperty("options.character-map.columns-dock-tb.label"), columnsSpinnerDockTB);


		addSeparator("options.character-map.separator-chars.label");

		addComponent(showLarge);

		addComponent(jEdit.getProperty("options.character-map.large-size.label"), largeSize);

		addComponent(showSuper);

		addComponent(superOffset);

		addComponent(jEdit.getProperty("options.character-map.super-size.label"), superSize);
	}
//}}}

//{{{ _save() method
	/**
	 * Store the options selected on the pane back to the 
	 * jedit properties.
	 */
	@Override
	public void _save()
	{
		// Save options
		jEdit.setBooleanProperty("options.character-map.status", status.isSelected());
		jEdit.setBooleanProperty("options.character-map.encoding", encoding.isSelected());
		jEdit.setBooleanProperty("options.character-map.blocks", blocks.isSelected());
		jEdit.setBooleanProperty("options.character-map.higherplanes", higherplanes.isSelected());
		jEdit.setBooleanProperty("options.character-map.anti-alias", antialias.isSelected());

		int column = ((Integer) columnsSpinner.getValue()).intValue();
		jEdit.setIntegerProperty("options.character-map.columns", column);
		column = ((Integer) columnsSpinnerDockLR.getValue()).intValue();
		jEdit.setIntegerProperty("options.character-map.columns-dock-lr", column);
		column = ((Integer) columnsSpinnerDockTB.getValue()).intValue();
		jEdit.setIntegerProperty("options.character-map.columns-dock-tb", column);

		jEdit.setBooleanProperty("options.character-map.large", showLarge.isSelected());
		jEdit.setBooleanProperty("options.character-map.super", showSuper.isSelected());
		setIntegerPropertyFromTextField("options.character-map.large-size", largeSize, 36);
		setIntegerPropertyFromTextField("options.character-map.super-size", superSize, 128);
		jEdit.setBooleanProperty("options.character-map.super-offset", superOffset.isSelected());

		// Reload CharacterMap.jar
		PluginJAR jar = jEdit.getPlugin("charactermap.CharacterMapPlugin").getPluginJAR();
		jEdit.removePluginJAR(jar,false);
		jEdit.addPluginJAR(jar.getPath());
		boolean isFloating = jEdit.getProperty("character-map.dock-position",
			DockableWindowManager.FLOATING).equalsIgnoreCase(DockableWindowManager.FLOATING);
		if (!isFloating)
		{
			EditAction act = jEdit.getAction("character-map");
			act.invoke(jEdit.getActiveView());
		}
	}
//}}}

//{{{ Auxiliary function
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
//}}}
}

