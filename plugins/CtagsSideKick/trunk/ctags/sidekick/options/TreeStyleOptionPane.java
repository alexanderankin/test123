/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

*/

package ctags.sidekick.options;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;

import ctags.sidekick.FilterManager;
import ctags.sidekick.MapperManager;
import ctags.sidekick.SorterManager;


public class TreeStyleOptionPane extends AbstractOptionPane implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JComboBox modeCB;
	Vector<IModeOptionPane> modePanes;
	JCheckBox useDefaultsCheck;
	
	public TreeStyleOptionPane()
	{
		super("CtagsSideKick-tree-style");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		Mode[] modes = jEdit.getModes();
		Arrays.sort(modes,new MiscUtilities.StringICaseCompare());
		String[] modeNames = new String[modes.length + 1];
		modeNames[0] = "<global defaults>";
		for(int i = 0; i < modes.length; i++)
			modeNames[i + 1] = modes[i].getName();
		modeCB = new JComboBox(modeNames);
		modeCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				modeSelected();
			}
		});
		addComponent("Change settings for mode:", modeCB);

		modePanes = new Vector<IModeOptionPane>();
		
		CtagsCmdOptionsPane invocationPane = new CtagsCmdOptionsPane();
		addComponent(invocationPane);
		modePanes.add(invocationPane);
		
		JPanel optionPanes = new JPanel(new GridLayout(1, 0));
		addComponent(optionPanes);

		ObjectProcessorListEditor mapperPane =
			new ObjectProcessorListEditor(MapperManager.getInstance());
		optionPanes.add(mapperPane);
		modePanes.add(mapperPane);
		
		ObjectProcessorListEditor sorterPane =
			new ObjectProcessorListEditor(SorterManager.getInstance());
		optionPanes.add(sorterPane);
		modePanes.add(sorterPane);
		
		ObjectProcessorListEditor filterPane =
			new ObjectProcessorListEditor(FilterManager.getInstance());
		optionPanes.add(filterPane);
		modePanes.add(filterPane);
		
		addSeparator();
		
		useDefaultsCheck = new JCheckBox("Use default settings");
		useDefaultsCheck.addActionListener(this);
		addComponent(useDefaultsCheck);

		modeCB.setSelectedIndex(0);
	}

	private void modeSelected() {
		int index = modeCB.getSelectedIndex();
		String mode;
		if (index == 0) {
			mode = null;
			useDefaultsCheck.setEnabled(false);
		} else {
			mode = (String) modeCB.getItemAt(index); 
			useDefaultsCheck.setEnabled(true);
		}
		for (int i = 0; i < modePanes.size(); i++)
			modePanes.get(i).modeSelected(mode);
	}

	public void save()
	{
		for (int i = 0; i < modePanes.size(); i++)
			modePanes.get(i).save();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == useDefaultsCheck) {
			for (int i = 0; i < modePanes.size(); i++)
				modePanes.get(i).setUseDefaults(useDefaultsCheck.isSelected());
		}
		
	}
}
/** ***********************************************************************EOF */

