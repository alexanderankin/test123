/*
 * CatalogOptionPane.java - Catalog editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2002 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.options;

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
import xml.*;
//}}}

public class CatalogOptionPane extends AbstractOptionPane
{
	//{{{ CatalogsOptionPane constructor
	public CatalogOptionPane()
	{
		super("xml.catalog");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());

		add(BorderLayout.NORTH,new JLabel(jEdit.getProperty(
			"options.xml.catalog.caption")));

		catalogModel = new CatalogModel();
		catalogTable = new JTable(catalogModel);
		catalogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		catalogTable.getTableHeader().setReorderingAllowed(false);
		catalogTable.getTableHeader().addMouseListener(new HeaderMouseHandler());
		catalogTable.getSelectionModel().addListSelectionListener(
			new SelectionHandler());

		EntryTypeRenderer comboBox = new EntryTypeRenderer();
		catalogTable.setRowHeight(comboBox.getPreferredSize().height);
		TableColumn column = catalogTable.getColumnModel().getColumn(0);
		column.setCellRenderer(comboBox);
		column.setCellEditor(new DefaultCellEditor(new EntryTypeRenderer()));

		Dimension d = catalogTable.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(catalogTable);
		scroller.setPreferredSize(d);
		add(BorderLayout.CENTER,scroller);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.setBorder(new EmptyBorder(6,0,0,0));

		add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.setToolTipText(jEdit.getProperty("options.xml.catalog.add"));
		add.addActionListener(new ActionHandler());
		buttons.add(add);
		remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.setToolTipText(jEdit.getProperty("options.xml.catalog.remove"));
		remove.addActionListener(new ActionHandler());
		buttons.add(remove);
		buttons.add(Box.createGlue());

		add(BorderLayout.SOUTH,buttons);

		updateEnabled();
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		if(catalogTable.getCellEditor() != null)
			catalogTable.getCellEditor().stopCellEditing();

		catalogModel.save();
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JTable catalogTable;
	private CatalogModel catalogModel;
	private JButton add;
	private JButton remove;
	//}}}

	//{{{ updateEnabled() method
	private void updateEnabled()
	{
		int selectedRow = catalogTable.getSelectedRow();
		remove.setEnabled(selectedRow != -1);
	} //}}}

	//}}}

	//{{{ HeaderMouseHandler class
	class HeaderMouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			switch(catalogTable.getTableHeader().columnAtPoint(evt.getPoint()))
			{
			case 1:
				((CatalogModel)catalogTable.getModel()).sort(1);
				break;
			case 2:
				((CatalogModel)catalogTable.getModel()).sort(2);
				break;
			}
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
			Object source = evt.getSource();
			if(source == add)
			{
				catalogModel.add(new CatalogManager.Entry(
					CatalogManager.Entry.SYSTEM,null,null));
				int index = catalogModel.getRowCount() - 1;
				catalogTable.getSelectionModel()
					.setSelectionInterval(index,index);
				Rectangle rect = catalogTable.getCellRect(
					index,0,true);
				catalogTable.scrollRectToVisible(rect);
				updateEnabled();
			}
			if(source == remove)
			{
				int selectedRow = catalogTable.getSelectedRow();
				catalogModel.remove(selectedRow);
				updateEnabled();
			}
		}
	} //}}}

	//{{{ EntryTypeRenderer class
	class EntryTypeRenderer extends JComboBox
		implements TableCellRenderer
	{
		EntryTypeRenderer()
		{
			super(new String[] {
				jEdit.getProperty("options.xml.catalog.system"),
				jEdit.getProperty("options.xml.catalog.public")
			});
			EntryTypeRenderer.this.setRequestFocusEnabled(false);
		}

		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			setSelectedIndex(((Integer)value).intValue());
			return this;
		}
	} //}}}
} //}}}

//{{{ CatalogModel class
class CatalogModel extends AbstractTableModel
{
	Vector entries;

	//{{{ CatalogModel constructor
	CatalogModel()
	{
		entries = new Vector();
		HashMap catalogHash = CatalogManager.getUserCatalog();

		Iterator entryEnum = catalogHash.keySet().iterator();

		while(entryEnum.hasNext())
			entries.addElement(entryEnum.next());

		sort(1);
	} //}}}

	//{{{ sort() method
	void sort(int col)
	{
		MiscUtilities.quicksort(entries,new EntryCompare(col));
		fireTableDataChanged();
	} //}}}

	//{{{ add() method
	void add(CatalogManager.Entry entry)
	{
		entries.addElement(entry);
		fireTableStructureChanged();
	} //}}}

	//{{{ remove() method
	void remove(int index)
	{
		entries.removeElementAt(index);
		fireTableStructureChanged();
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount()
	{
		return 3;
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount()
	{
		return entries.size();
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		CatalogManager.Entry entry = (CatalogManager.Entry)entries.elementAt(row);
		switch(col)
		{
		case 0:
			return new Integer(entry.type);
		case 1:
			return entry.id;
		default:
			return entry.uri;
		}
	} //}}}

	//{{{ isCellEditable() method
	public boolean isCellEditable(int row, int col)
	{
		return true;
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		if(value == null)
			value = "";

		CatalogManager.Entry entry = (CatalogManager.Entry)entries.elementAt(row);

		if(col == 0)
			entry.type = ((Integer)value).intValue();
		else if(col == 1)
			entry.id = (String)value;
		else
			entry.uri = (String)value;

		fireTableRowsUpdated(row,row);
	} //}}}

	//{{{ getColumnClass() method
	public Class getColumnClass(int col)
	{
		switch(col)
		{
		case 0:
			return Integer.class;
		default:
			return String.class;
		}
	} //}}}

	//{{{ getColumnName() method
	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.xml.catalog.type");
		case 1:
			return jEdit.getProperty("options.xml.catalog.id");
		default:
			return jEdit.getProperty("options.xml.catalog.uri");
		}
	} //}}}

	//{{{ toHashtable() method
	public HashMap toHashtable()
	{
		HashMap hash = new HashMap();
		for(int i = 0; i < entries.size(); i++)
		{
			CatalogManager.Entry entry = (CatalogManager.Entry)
				entries.elementAt(i);
			if(entry.id != null
				&& entry.id.length() > 0
				&& entry.uri != null
				&& entry.uri.length() > 0)
			{
				hash.put(entry,entry.uri);
			}
		}
		return hash;
	} //}}}

	//{{{ save() method
	public void save()
	{
		CatalogManager.setUserCatalog(toHashtable());
	} //}}}

	//{{{ EntryCompare class
	class EntryCompare implements MiscUtilities.Compare
	{
		int col;

		EntryCompare(int col)
		{
			this.col = col;
		}

		public int compare(Object obj1, Object obj2)
		{
			CatalogManager.Entry e1 = (CatalogManager.Entry)obj1;
			CatalogManager.Entry e2 = (CatalogManager.Entry)obj2;

			String str1 = (col == 1 ? e1.id : e1.uri);
			String str2 = (col == 1 ? e2.id : e2.uri);

			return MiscUtilities.compareStrings(str1,str2,true);
		}
	} //}}}
} //}}}
