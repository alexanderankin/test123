/*
 * BufferTabsOptionPane.java - Option pane for BufferTabs
 * Copyright (C) 1999, 2000 Jason Ginchereau, Andre Kaplan
 * Copyright (C) 2000, 2001, 2002, 2003 Andre Kaplan
 * Copyright (C) 2003 Chris Samuels
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package buffertabs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;


@SuppressWarnings("serial")
public class BufferTabsOptionPane extends AbstractOptionPane implements ItemListener
{
	private static final String BUFFERTABS_WRAP = "buffertabs.wrap";
	private JCheckBox enableCB;
	private JCheckBox iconsCB;
	private JCheckBox popupCB;
	private JCheckBox closeButtonCB;
	private JComboBox locationChoice;

	private JRadioButton colorTabRB;
	private JRadioButton colorTextRB;
	private JRadioButton colorSelTabRB;
	private JRadioButton colorSelTextRB;
	private JCheckBox enableColorsCB;
	private JCheckBox muteColorsCB;
	private JCheckBox variationColorsCB;
	private JCheckBox highlightColorsCB;
	private JCheckBox doubleClickCB;
	private JCheckBox middleClickCB;
	private JCheckBox toggleDocksDoubleClickCB;
	private JCheckBox doNotStretchTabsCB;
	private JCheckBox wrapTabsCB;

	public BufferTabsOptionPane() {
		super("buffertabs");
	}


	public void _init() {

		final Dimension ySpace = new Dimension(0, 10);
		final Dimension xSpace = new Dimension(15, 0);
		enableCB = new JCheckBox(jEdit.getProperty("options.buffertabs.enable.label"));
		addComponent(enableCB);
		addSeparator("options.buffertabs.mouse-options.label");
		popupCB = new JCheckBox(jEdit.getProperty("options.buffertabs.popup.label"));
		addComponent(popupCB);
		closeButtonCB = new JCheckBox(jEdit.getProperty("options.buffertabs.closeButton.label"));
		addComponent(closeButtonCB);

		addComponent(new Box.Filler(ySpace, ySpace, ySpace));


		// Close tabs on option.
		DoubleClickOptionSwitch dcSwitch = new DoubleClickOptionSwitch();

		addComponent(new JLabel(jEdit.getProperty("options.buffertabs.close-tab-on.label")));

		JPanel checkBoxPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;

		doubleClickCB = new JCheckBox(jEdit.getProperty("options.buffertabs.close-tab-on.double-left-click.label"));
		doubleClickCB.addItemListener(dcSwitch);
		middleClickCB = new JCheckBox(jEdit.getProperty("options.buffertabs.close-tab-on.single-middle-click.label"));

		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 1;
		checkBoxPanel.add(new Box.Filler(xSpace, xSpace, xSpace), c);

		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		checkBoxPanel.add(doubleClickCB, c);

		c.gridy = 1;
		c.gridx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		checkBoxPanel.add(middleClickCB, c);

		addComponent(checkBoxPanel);
		addComponent(new Box.Filler(ySpace, ySpace, ySpace));

		// Toggle docking areas option.
		addComponent(new JLabel(jEdit.getProperty("options.buffertabs.toggle-docks-on.label")));

		JPanel docksCheckBoxPanel = new JPanel(new GridBagLayout());
		GridBagConstraints dC = new GridBagConstraints();
		dC.anchor = GridBagConstraints.LINE_START;

		toggleDocksDoubleClickCB = new JCheckBox(jEdit.getProperty("options.buffertabs.close-tab-on.double-left-click.label"));
		toggleDocksDoubleClickCB.addItemListener(dcSwitch);


		dC.gridy = 0;
		dC.gridx = 0;
		dC.gridwidth = 1;
		docksCheckBoxPanel.add(new Box.Filler(xSpace, xSpace, xSpace), dC);

		dC.gridy = 0;
		dC.gridx = 1;
		dC.gridwidth = GridBagConstraints.REMAINDER;
		docksCheckBoxPanel.add(toggleDocksDoubleClickCB, dC);

		addComponent(docksCheckBoxPanel);

		addSeparator("options.buffertabs.layout-and-style-options.label");

		iconsCB = new JCheckBox(jEdit.getProperty("options.buffertabs.icons.label"));
		addComponent(iconsCB);
		doNotStretchTabsCB = new JCheckBox(jEdit.getProperty("options.buffertabs.nostretch.label"));
		addComponent(doNotStretchTabsCB);
		wrapTabsCB = new JCheckBox(jEdit.getProperty("options.buffertabs.wrap.label"));
		addComponent(wrapTabsCB);
		addComponent(new Box.Filler(ySpace, ySpace, ySpace));

		locationChoice = new JComboBox(new String[] { "top", "bottom", "left", "right"});
		JPanel locationChoicePanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0 , 0));
		locationChoicePanel.add(new JLabel(jEdit.getProperty("options.buffertabs.location.label") + " "));
		locationChoicePanel.add(locationChoice);
		addComponent(locationChoicePanel);

		addComponent(new Box.Filler(ySpace, ySpace, ySpace));

		enableColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-tabs.label" ) );
		enableColorsCB.addItemListener( this );
		addComponent( enableColorsCB );


		JPanel colorTabPanel = new JPanel(new GridBagLayout());

		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 1;
		colorTabPanel.add(new Box.Filler(xSpace, xSpace, xSpace), c);

		colorTabRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.color-background.label" ) );
		colorTextRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.color-foreground.label" ) );
		ButtonGroup group = new ButtonGroup();
		group.add( colorTabRB );
		group.add( colorTextRB );

		JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		colorPanel.add(colorTabRB);
		colorPanel.add(colorTextRB);

		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		colorTabPanel.add(colorPanel, c);

		highlightColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-selected.label" ) );
		highlightColorsCB.addItemListener( this );


		c.gridy = 1;
		c.gridx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		colorTabPanel.add(highlightColorsCB, c);

		c.gridy = 2;
		c.gridx = 1;
		c.gridwidth = 1;
		colorTabPanel.add(new Box.Filler(xSpace, xSpace, xSpace), c);

		colorSelTabRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.color-selected-background.label" ) );

		colorSelTextRB = new JRadioButton( jEdit.getProperty( "options.buffertabs.color-selected-foreground.label" ) );

		ButtonGroup groupSel = new ButtonGroup();
		groupSel.add( colorSelTabRB );
		groupSel.add( colorSelTextRB );

		JPanel colorSelPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
		colorSelPanel.add(colorSelTabRB);
		colorSelPanel.add(colorSelTextRB);

		c.gridy = 2;
		c.gridx = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		colorTabPanel.add(colorSelPanel, c);

		muteColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-mute.label" ) );
		muteColorsCB.addItemListener( this );

		c.gridy = 3;
		c.gridx = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		colorTabPanel.add(muteColorsCB, c);

		variationColorsCB = new JCheckBox( jEdit.getProperty( "options.buffertabs.color-variation.label" ) );
		variationColorsCB.addItemListener( this );


		c.gridy = 4;
		c.gridx = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		colorTabPanel.add(variationColorsCB, c);

		addComponent(colorTabPanel);

		load();
	}


	public void load() {
		enableCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.enable", false)
				);
		iconsCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.icons", true)
				);
		doNotStretchTabsCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.nostretch", false)
				);
		wrapTabsCB.setSelected(getWrapTabsProperty());
		popupCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.usePopup", true)
				);
		closeButtonCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.closeButton", true)
				);

		locationChoice.setSelectedItem(
				getLocationProperty("buffertabs.location", "bottom")
				);

		doubleClickCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.close-tab-on.double-left-click", true)
				);
		middleClickCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.close-tab-on.single-middle-click", true)
				);
		toggleDocksDoubleClickCB.setSelected(
				jEdit.getBooleanProperty("buffertabs.toggle-docks-on.double-left-click", false)
				);

		//CES: Color tabs
		enableColorsCB.setSelected(
				jEdit.getBooleanProperty( "buffertabs.color-tabs", false )
				);

		highlightColorsCB.setSelected(
				jEdit.getBooleanProperty( "buffertabs.color-selected", true )
				);

		muteColorsCB.setSelected(
				jEdit.getBooleanProperty( "buffertabs.color-mute", true )
				);

		variationColorsCB.setSelected(
				jEdit.getBooleanProperty( "buffertabs.color-variation", true )
				);

		colorTabRB.setSelected(
				!jEdit.getBooleanProperty( "buffertabs.color-foreground", false )
				);

		colorTextRB.setSelected(
				jEdit.getBooleanProperty( "buffertabs.color-foreground", false )     
				);

		colorSelTabRB.setSelected(
				!jEdit.getBooleanProperty( "buffertabs.color-selected-foreground", false )
				);

		colorSelTextRB.setSelected(
				jEdit.getBooleanProperty( "buffertabs.color-selected-foreground", false )			
				);


		muteColorsCB.setEnabled( enableColorsCB.isSelected() );
		variationColorsCB.setEnabled( muteColorsCB.isSelected() && enableColorsCB.isSelected());
		highlightColorsCB.setEnabled( enableColorsCB.isSelected() );
		colorTabRB.setEnabled( enableColorsCB.isSelected() );
		colorTextRB.setEnabled( enableColorsCB.isSelected() );
		colorSelTabRB.setEnabled( enableColorsCB.isSelected() && highlightColorsCB.isSelected());
		colorSelTextRB.setEnabled( enableColorsCB.isSelected() && highlightColorsCB.isSelected());


	}


	/**
	 * Called when the options dialog's `OK' button is pressed.
	 * This should save any properties saved in this option pane.
	 */
	public void _save() {
		jEdit.setBooleanProperty("buffertabs.enable", enableCB.isSelected());
		jEdit.setBooleanProperty("buffertabs.icons", iconsCB.isSelected());
		jEdit.setBooleanProperty("buffertabs.nostretch", doNotStretchTabsCB.isSelected());
		jEdit.setBooleanProperty(BUFFERTABS_WRAP, wrapTabsCB.isSelected());
		jEdit.setBooleanProperty("buffertabs.usePopup", popupCB.isSelected());
		jEdit.setBooleanProperty("buffertabs.closeButton", closeButtonCB.isSelected());
		jEdit.setProperty("buffertabs.location",
				locationChoice.getSelectedItem().toString());

		//CES: Color tabs
		jEdit.setBooleanProperty( "buffertabs.color-tabs", enableColorsCB.isSelected() );
		jEdit.setBooleanProperty( "buffertabs.color-selected", highlightColorsCB.isSelected() );
		jEdit.setBooleanProperty( "buffertabs.color-mute", muteColorsCB.isSelected() );
		jEdit.setBooleanProperty( "buffertabs.color-variation", variationColorsCB.isSelected() );
		jEdit.setBooleanProperty( "buffertabs.color-foreground", colorTextRB.isSelected() );
		jEdit.setBooleanProperty("buffertabs.close-tab-on.single-middle-click", middleClickCB.isSelected());		jEdit.setBooleanProperty( "buffertabs.color-selected-foreground", colorSelTextRB.isSelected() );
		jEdit.setBooleanProperty("buffertabs.close-tab-on.double-left-click", doubleClickCB.isSelected());
		jEdit.setBooleanProperty("buffertabs.toggle-docks-on.double-left-click", toggleDocksDoubleClickCB.isSelected());


	}

	public static boolean getWrapTabsProperty()
	{
		return jEdit.getBooleanProperty(BUFFERTABS_WRAP, true);
	}

	public static String getLocationProperty(String prop, String defaultVal) {
		String location = jEdit.getProperty(prop);
		if (location == null) {
			location = defaultVal;
		}
		location = location.toLowerCase();
		if (!(     location.equals("top")
				|| location.equals("bottom")
				|| location.equals("left")
				|| location.equals("right")
				)
				) {
			location = defaultVal;
		}
		return location;
	}


	/**
	 * Update options pane to reflect option changes
	 */
	public void itemStateChanged( ItemEvent e ) {
		muteColorsCB.setEnabled( enableColorsCB.isSelected() );
		variationColorsCB.setEnabled( muteColorsCB.isSelected() && enableColorsCB.isSelected());
		highlightColorsCB.setEnabled( enableColorsCB.isSelected() );
		colorTabRB.setEnabled( enableColorsCB.isSelected() );
		colorTextRB.setEnabled( enableColorsCB.isSelected() );
		colorSelTabRB.setEnabled( enableColorsCB.isSelected() && highlightColorsCB.isSelected());
		colorSelTextRB.setEnabled( enableColorsCB.isSelected() && highlightColorsCB.isSelected());
	}

	/**
	 * Toggles the two double click options. If "close on" double click is selected, and "toggle docking areas" is also
	 * selected, then "toggle docking areas" is deselected. Likewise if "toggle docking areas" is selected, and
	 * "close on" is already selected, "close on" is deselected.
	 */
	private class DoubleClickOptionSwitch implements ItemListener {

		public void itemStateChanged(ItemEvent e) {

			JCheckBox cb = (JCheckBox)e.getSource();

			if(cb.isSelected() && cb == doubleClickCB) {

				toggleDocksDoubleClickCB.setSelected(false);

			} else if (cb.isSelected() && cb == toggleDocksDoubleClickCB) {

				doubleClickCB.setSelected(false);

			}
		}

	}
}

