/*
 * AbbrevsOptionPane.java - Abbrevs options panel
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=2:
 *
 * Copyright (C) 1999, 2000, 2001, 2002 Slava Pestov
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

package superabbrevs.gui;

//{{{ Imports
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.View;

import superabbrevs.SuperAbbrevs;
//}}}

//{{{ AbbrevsOptionPane class
/**
 * Abbrev editor.
 *
 * I modified Slava Pestov code
 * @author Sune Simonsen 
 */
public class AbbrevsOptionPane extends AbstractOptionPane
{
	//{{{ AbbrevsOptionPane constructor
	public AbbrevsOptionPane(View view)
	{
		super("superabbrevs");
		
		this.view = view;
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());

		JPanel showDialogPanel = new JPanel();
		
		showDialogCheckBox = new JCheckBox(jEdit.getProperty("SuperAbbrevs.abbrev.showDialogIfNotExists"));
		
		String  showDialog = jEdit.getProperty("SuperAbbrevs.abbrev.showDialog");
		showDialog = (showDialog==null)?"false":showDialog; 
		showDialogCheckBox.setSelected(showDialog.equals("true"));
		
		showDialogPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		showDialogPanel.add(showDialogCheckBox);

		
		JPanel abbrevsSetPanel = new JPanel();
		JLabel label = new JLabel(jEdit.getProperty("options.abbrevs.set"));
		label.setBorder(new EmptyBorder(0,0,0,12));
		abbrevsSetPanel.add(label);

		modeAbbrevs = new Hashtable();
		Mode[] modes = jEdit.getModes();
		Arrays.sort(modes,new MiscUtilities.StringICaseCompare());
		String[] sets = new String[modes.length + 1];
		sets[0] = "global";
		modeAbbrevs.put(sets[0],
				new AbbrevsModel(SuperAbbrevs.loadAbbrevs(sets[0])));
		
		
		int selectedIndex = 0;
		String mode = view.getBuffer().getMode().getName();
		for(int i = 0; i < modes.length; i++)
		{
			String name = modes[i].getName();
			sets[i+1] = name;
			modeAbbrevs.put(name,
				new AbbrevsModel(SuperAbbrevs.loadAbbrevs(name)));
			
			if(name.equals(mode)){
				selectedIndex = i+1;
			}
		}
		
		
		setsComboBox = new JComboBox(sets);
		ActionHandler actionHandler = new ActionHandler();
		setsComboBox.addActionListener(actionHandler);
		
		abbrevsSetPanel.add(setsComboBox);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2,1));
		topPanel.add(showDialogPanel);
		topPanel.add(abbrevsSetPanel);
		
		add(BorderLayout.NORTH,topPanel);

		abbrevsTable = new JTable((AbbrevsModel)modeAbbrevs.get("global"));
		abbrevsTable.getColumnModel().getColumn(1).setCellRenderer(
			new Renderer());
		abbrevsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		abbrevsTable.getTableHeader().setReorderingAllowed(false);
		abbrevsTable.getTableHeader().addMouseListener(new HeaderMouseHandler());
		abbrevsTable.getSelectionModel().addListSelectionListener(
			new SelectionHandler());
		abbrevsTable.getSelectionModel().setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION);
		abbrevsTable.addMouseListener(new TableMouseHandler());
		Dimension d = abbrevsTable.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(abbrevsTable);
		scroller.setPreferredSize(d);
		add(BorderLayout.CENTER,scroller);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.setBorder(new EmptyBorder(6,0,0,0));

		add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.setToolTipText(jEdit.getProperty("options.abbrevs.add"));
		add.addActionListener(actionHandler);
		buttons.add(add);
		remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.setToolTipText(jEdit.getProperty("options.abbrevs.remove"));
		remove.addActionListener(actionHandler);
		buttons.add(remove);
		edit = new RolloverButton(GUIUtilities.loadIcon("ButtonProperties.png"));
		edit.setToolTipText(jEdit.getProperty("options.abbrevs.edit"));
		edit.addActionListener(actionHandler);
		buttons.add(edit);
		buttons.add(Box.createGlue());
		
		importAbbrevs = new JButton("Import normal abbrevs");
		importAbbrevs.addActionListener(actionHandler);
		JPanel bottomPanel = new JPanel(new BorderLayout());		
		bottomPanel.add(BorderLayout.WEST, buttons);
		bottomPanel.add(BorderLayout.EAST, new JPanel().add(importAbbrevs));
		
		add(BorderLayout.SOUTH,bottomPanel);

		
		setsComboBox.setSelectedIndex(selectedIndex);
		
		updateEnabled();
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		if(abbrevsTable.getCellEditor() != null)
			abbrevsTable.getCellEditor().stopCellEditing();
		
		//SuperAbbrevs.saveAbbrevs("global",globalAbbrevs.toHashtable());
		
		
		Enumeration keys = modeAbbrevs.keys();
		Enumeration values = modeAbbrevs.elements();
		while(keys.hasMoreElements())
		{
			SuperAbbrevs.saveAbbrevs((String)keys.nextElement(),
				((AbbrevsModel)values.nextElement()).toHashtable());
		}
		
		String showDialog = showDialogCheckBox.isSelected()?"true":"false";
		jEdit.setProperty("SuperAbbrevs.abbrev.showDialog",showDialog);
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JComboBox setsComboBox;
	//private JCheckBox expandOnInput;
	private JTable abbrevsTable;
	private Hashtable modeAbbrevs;
	private JButton add;
	private JButton edit;
	private JButton remove;
	private JButton importAbbrevs;
	private JCheckBox showDialogCheckBox;
	private View view;
	//}}}

	//{{{ updateEnabled() method
	private void updateEnabled()
	{
		int selectedRow = abbrevsTable.getSelectedRow();
		edit.setEnabled(selectedRow != -1);
		remove.setEnabled(selectedRow != -1);
	} //}}}

	//{{{ edit() method
	private void edit()
	{
		AbbrevsModel abbrevsModel = (AbbrevsModel)abbrevsTable.getModel();

		int row = abbrevsTable.getSelectedRow();

		String abbrev = (String)abbrevsModel.getValueAt(row,0);
		String expansion = (String)abbrevsModel.getValueAt(row,1);
		String oldAbbrev = abbrev;

		EditAbbrevDialog dialog = new EditAbbrevDialog(
			GUIUtilities.getParentDialog(AbbrevsOptionPane.this),
			abbrev,expansion,abbrevsModel.toHashtable());
		abbrev = dialog.getAbbrev();
		expansion = dialog.getExpansion();
		if(abbrev != null && expansion != null)
		{
			for(int i = 0; i < abbrevsModel.getRowCount(); i++)
			{
				if(abbrevsModel.getValueAt(i,0).equals(oldAbbrev))
				{
					abbrevsModel.remove(i);
					break;
				}
			}

			add(abbrevsModel,abbrev,expansion);
		}
	} //}}}

	//{{{ add() method
	private void add(AbbrevsModel abbrevsModel, String abbrev,
		String expansion)
	{
		for(int i = 0; i < abbrevsModel.getRowCount(); i++)
		{
			if(abbrevsModel.getValueAt(i,0).equals(abbrev))
			{
				abbrevsModel.remove(i);
				break;
			}
		}

		abbrevsModel.add(abbrev,expansion);
		updateEnabled();
	} //}}}

	//{{{ importAbbrevs method
	private void importAbbrevs() {
		if(abbrevsTable.getCellEditor() != null)
			abbrevsTable.getCellEditor().stopCellEditing();
		
		Enumeration modes = Abbrevs.getModeAbbrevs().keys();
		
		while(modes.hasMoreElements()){
			String mode = (String)modes.nextElement();
			
			importAbbrevs(mode);
		}
		
		//update global
		importAbbrevs("global");
		
		//update model
		String selectedMode = (String)setsComboBox.getSelectedItem();
		abbrevsTable.setModel((AbbrevsModel)modeAbbrevs.get(selectedMode));
		
		
	}
	//}}}
	
	private void importAbbrevs(String mode) {
		//get the superAbbrevs hashtable for the specific mode 
		Hashtable superModeAbbrevs = 
			((AbbrevsModel)modeAbbrevs.get(mode)).toHashtable();
		if (superModeAbbrevs == null){
			superModeAbbrevs = new Hashtable();
		}	
		Hashtable normalModeAbbrevs;
		if (mode.equals("global")){
			normalModeAbbrevs = Abbrevs.getGlobalAbbrevs();
		} else {
			normalModeAbbrevs = (Hashtable)Abbrevs.getModeAbbrevs().get(mode);
		}
		
		//add normalAbbrevs to superAbbrevs 
		Enumeration abbrevs = normalModeAbbrevs.keys();
		while (abbrevs.hasMoreElements()) {
			String abbrev = (String)abbrevs.nextElement();
			//only import the abbrev if it doesn't exists 
			if (!superModeAbbrevs.containsKey(abbrev)) {
				String abbrevExpand = (String)normalModeAbbrevs.get(abbrev);
				abbrevExpand = abbrevExpand.replaceFirst("\\\\[|]","\\$end");
				abbrevExpand = abbrevExpand.replaceAll("\\\\n","\n");
				abbrevExpand = abbrevExpand.replaceAll("\\\\t","\t");
				superModeAbbrevs.put(abbrev,abbrevExpand);
			}
		}
		
		AbbrevsModel model = new AbbrevsModel(superModeAbbrevs);
		modeAbbrevs.put(mode,model);
	}
	
	//}}}

	//{{{ HeaderMouseHandler class
	class HeaderMouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			switch(abbrevsTable.getTableHeader().columnAtPoint(evt.getPoint()))
			{
			case 0:
				((AbbrevsModel)abbrevsTable.getModel()).sort(0);
				break;
			case 1:
				((AbbrevsModel)abbrevsTable.getModel()).sort(1);
				break;
			}
		}
	} //}}}

	//{{{ TableMouseHandler class
	class TableMouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount() == 2)
				edit();
		}
	} //}}}

	//{{{ SelectionHandler class
	class SelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateEnabled();
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			AbbrevsModel abbrevsModel = (AbbrevsModel)abbrevsTable.getModel();

			Object source = evt.getSource();
			if(source == setsComboBox)
			{
				String selected = (String)setsComboBox.getSelectedItem();
				/*if(selected.equals("global"))
				{
					abbrevsTable.setModel(globalAbbrevs);
				}
				else
				{*/
					abbrevsTable.setModel((AbbrevsModel)
						modeAbbrevs.get(selected));
				//}
				updateEnabled();
			}
			else if(source == add)
			{
				EditAbbrevDialog dialog = new EditAbbrevDialog(
					GUIUtilities.getParentDialog(AbbrevsOptionPane.this),
					null,null,abbrevsModel.toHashtable());
				String abbrev = dialog.getAbbrev();
				String expansion = dialog.getExpansion();
				if(abbrev != null && abbrev.length() != 0
					&& expansion != null
					&& expansion.length() != 0)
				{
					add(abbrevsModel,abbrev,expansion);
				}
			}
			else if(source == edit)
			{
				edit();
			}
			else if(source == remove)
			{
				int selectedRow = abbrevsTable.getSelectedRow();
				abbrevsModel.remove(selectedRow);
				updateEnabled();
			}
			else if (source == importAbbrevs){
				//import normal abbreviations
				importAbbrevs();
			}
		}
	} //}}}

	//{{{ Renderer class
	static class Renderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean cellHasFocus,
			int row,
			int col)
		{
			String valueStr = value.toString();

			// workaround for Swing's annoying processing of
			// labels starting with <html>, which often breaks
			if(valueStr.toLowerCase().startsWith("<html>"))
				valueStr = " " + valueStr;
			return super.getTableCellRendererComponent(table,valueStr,
				isSelected,cellHasFocus,row,col);
		}
	} //}}}
} //}}}

//{{{ AbbrevsModel class
class AbbrevsModel extends AbstractTableModel
{
	Vector abbrevs;
	int lastSort;

	//{{{ AbbrevsModel constructor
	AbbrevsModel(Hashtable abbrevHash)
	{
		abbrevs = new Vector();

		if(abbrevHash != null)
		{
			Enumeration abbrevEnum = abbrevHash.keys();
			Enumeration expandEnum = abbrevHash.elements();

			while(abbrevEnum.hasMoreElements())
			{
				abbrevs.addElement(new Abbrev((String)abbrevEnum.nextElement(),
					(String)expandEnum.nextElement()));
			}

			sort(0);
		}
	} //}}}

	//{{{ sort() method
	void sort(int col)
	{
		lastSort = col;
		MiscUtilities.quicksort(abbrevs,new AbbrevCompare(col));
		fireTableDataChanged();
	} //}}}

	//{{{ add() method
	void add(String abbrev, String expansion)
	{
		abbrevs.addElement(new Abbrev(abbrev,expansion));
		sort(lastSort);
	} //}}}

	//{{{ remove() method
	void remove(int index)
	{
		abbrevs.removeElementAt(index);
		fireTableStructureChanged();
	} //}}}

	//{{{ toHashtable() method
	public Hashtable toHashtable()
	{
		Hashtable hash = new Hashtable();
		for(int i = 0; i < abbrevs.size(); i++)
		{
			Abbrev abbrev = (Abbrev)abbrevs.elementAt(i);
			if(abbrev.abbrev.length() > 0
				&& abbrev.expand.length() > 0)
			{
				hash.put(abbrev.abbrev,abbrev.expand);
			}
		}
		return hash;
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount()
	{
		return 2;
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount()
	{
		return abbrevs.size();
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		Abbrev abbrev = (Abbrev)abbrevs.elementAt(row);
		switch(col)
		{
		case 0:
			return abbrev.abbrev;
		case 1:
			return abbrev.expand;
		default:
			return null;
		}
	} //}}}

	//{{{ isCellEditable() method
	public boolean isCellEditable(int row, int col)
	{
		return false;
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		if(value == null)
			value = "";

		Abbrev abbrev = (Abbrev)abbrevs.elementAt(row);

		if(col == 0)
			abbrev.abbrev = (String)value;
		else
			abbrev.expand = (String)value;

		fireTableRowsUpdated(row,row);
	} //}}}

	//{{{ getColumnName() method
	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.abbrevs.abbrev");
		case 1:
			return jEdit.getProperty("options.abbrevs.expand");
		default:
			return null;
		}
	} //}}}

	//{{{ AbbrevCompare class
	class AbbrevCompare implements MiscUtilities.Compare
	{
		int col;

		AbbrevCompare(int col)
		{
			this.col = col;
		}

		public int compare(Object obj1, Object obj2)
		{
			Abbrev a1 = (Abbrev)obj1;
			Abbrev a2 = (Abbrev)obj2;

			if(col == 0)
			{
				String abbrev1 = a1.abbrev.toLowerCase();
				String abbrev2 = a2.abbrev.toLowerCase();

				return MiscUtilities.compareStrings(
					abbrev1,abbrev2,true);
			}
			else
			{
				String expand1 = a1.expand.toLowerCase();
				String expand2 = a2.expand.toLowerCase();

				return MiscUtilities.compareStrings(
					expand1,expand2,true);
			}
		}
	} //}}}
} //}}}

//{{{ Abbrev class
class Abbrev
{
	Abbrev() {}

	Abbrev(String abbrev, String expand)
	{
		this.abbrev = abbrev;
		this.expand = expand;
	}

	String abbrev;
	String expand;
} //}}}

