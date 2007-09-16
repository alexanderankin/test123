/*
Copyright (C) 2006  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

*/

package ctags.sidekick;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

public class MapperOptionPane extends AbstractOptionPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JComboBox modeCB;
	JList mapperList;
	DefaultListModel mapperModel;
	HashMap<String, DefaultListModel> mapperModels;
	JList componentList;
	DefaultListModel componentListModel;
	
	public MapperOptionPane()
	{
		super("CtagsSideKick-mappers");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		mapperModels = new HashMap<String, DefaultListModel>();
		Mode[] modes = jEdit.getModes();
		Arrays.sort(modes,new MiscUtilities.StringICaseCompare());
		String[] modeNames = new String[modes.length + 1];
		modeNames[0] = "<global defaults";
		for(int i = 0; i < modes.length; i++)
			modeNames[i + 1] = modes[i].getName();
		modeCB = new JComboBox(modeNames);
		modeCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				modeSelected();
			}
		});
		addComponent("Change mapper for mode:", modeCB);
		
		mapperList = new JList();
		mapperList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mapperList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {
			}
		});
		addComponent(new JScrollPane(mapperList));
		modeCB.setSelectedIndex(0);
		
		JButton add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				addMapper();
			}
		});
		JButton remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				removeMapper();
			}
		});
		JButton up = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mapperUp();
			}
		});
		JButton down = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				mapperDown();
			}
		});

		JPanel buttons = new JPanel(new GridLayout(1, 0));
		buttons.add(add);
		buttons.add(remove);
		buttons.add(up);
		buttons.add(down);
		addComponent(buttons);
		
		addSeparator();
	}

	private void modeSelected() {
		int index = modeCB.getSelectedIndex();
		String mode = (String) modeCB.getItemAt(index);
		mapperModel = mapperModels.get(mode);
		if (mapperModel == null)
		{
			mapperModel = new DefaultListModel();
			mapperModels.put(mode, mapperModel);
			ListTreeMapper mapper;
			if (index == 0)
				mapper = (ListTreeMapper) MapperManager.getMapperForMode(null);
			else
				mapper = (ListTreeMapper) MapperManager.getMapperForMode(mode);
			Vector<ITreeMapper> components = mapper.getComponents();
			for (int i = 0; i < components.size(); i++)
				mapperModel.addElement(components.get(i));
		}
		mapperList.setModel(mapperModel);
	}

	protected void addMapper() {
		ITreeMapper mapper = new TreeMapperEditor(
				GUIUtilities.getParentDialog(this)).getMapper();
		if (mapper != null) {
			int index = mapperList.getSelectedIndex();
			mapperModel.add(index + 1, mapper);
			mapperList.setSelectedIndex(index + 1);
		}
	}

	protected void removeMapper() {
		int index = mapperList.getSelectedIndex();
		if (index >= 0) {
			mapperModel.remove(index);
			if (index < mapperModel.size())
				mapperList.setSelectedIndex(index);
		}
	}

	private void mapperDown() {
		int index = mapperList.getSelectedIndex();
		if (index < mapperModel.size() - 1) {
			ITreeMapper current = (ITreeMapper) mapperModel.get(index);
			ITreeMapper other = (ITreeMapper) mapperModel.get(index + 1);
			mapperModel.set(index + 1, current);
			mapperModel.set(index, other);
			mapperList.setSelectedIndex(index + 1);
		}
	}
	private void mapperUp() {
		int index = mapperList.getSelectedIndex();
		if (index > 0) {
			ITreeMapper current = (ITreeMapper) mapperModel.get(index);
			ITreeMapper other = (ITreeMapper) mapperModel.get(index - 1);
			mapperModel.set(index - 1, current);
			mapperModel.set(index, other);
			mapperList.setSelectedIndex(index - 1);
		}
	}

	public void save()
	{
		Iterator models = mapperModels.entrySet().iterator();
		while (models.hasNext()) {
			Entry e = (Entry) models.next();
			String mode = (String) e.getKey();
			DefaultListModel model = (DefaultListModel) e.getValue();
			ListTreeMapper mapper = new ListTreeMapper();
			for (int i = 0; i < model.getSize(); i++)
				mapper.add((ITreeMapper) model.get(i));
			if (mode.equals(modeCB.getItemAt(0)))
				MapperManager.setMapperForMode(null, mapper);
			else
				MapperManager.setMapperForMode(mode, mapper);
		}
	}
}
/** ***********************************************************************EOF */

