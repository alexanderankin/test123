/* % [{
% (C) Copyright 2008 Nicolas Carranza and individual contributors.
% See the CandyFolds-copyright.txt file in the CandyFolds distribution for a full
% listing of individual contributors.
%
% This file is part of CandyFolds.
%
% CandyFolds is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License,
% or (at your option) any later version.
%
% CandyFolds is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with CandyFolds.  If not, see <http://www.gnu.org/licenses/>.
% }] */
package candyfolds.config.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import candyfolds.config.FoldConfig;
import candyfolds.config.ModeConfig;
import org.gjt.sp.util.Log;

class FoldConfigsTable
	extends AbstractTableModel {
	private ModeConfig modeConfig;

	JTable table=new JTable(this);

	FoldConfigsTable() {
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		table.setDefaultRenderer(Color.class, new ColorCellRenderer());
		table.setDefaultEditor(Color.class, new ColorCellEditor());
	}

	class ColorCellRenderer
		extends DefaultTableCellRenderer {
		private Color color;
		private Rectangle rectangle=new Rectangle(0, 0, 0,0);
		{setOpaque(true);}
		@Override
		public Component getTableCellRendererComponent(JTable table,
		    Object value,
		    boolean isSelected,
		    boolean hasFocus,
		    int row,
		    int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setValue(null);
			FoldConfig foldConfig=getFoldConfig(row);
			//Log.log(Log.NOTICE, this, "foldConfig on Renderer: "+foldConfig.getName());
			if(foldConfig==null) {
				throw new AssertionError();
			}
			color=foldConfig.getColor();
			return this;
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(color);
			Dimension d=getSize();
			g.fillRect(2, 2, d.width-4, d.height-4);
		}
	}

	class ColorCellEditor
				extends AbstractCellEditor
		implements TableCellEditor {

		private Color color;

		private final JButton button=new JButton() {
			    {
				    addActionListener(new ActionListener() {
					        @Override
					        public void actionPerformed(ActionEvent ev) {
						        Color newColor=FullColorChooser.showDialog(button, "Select Candy Color", color);
						        if(newColor!=null)
							        color=newColor;
						        fireEditingStopped();
					        }
				        }
				                     );
			    }
			    @Override
			    public void paintComponent(Graphics g) {
				    Dimension d=getSize();
				    g.setColor(Color.white);
				    g.fillRect(2, 2, d.width-4, d.height-4);
				    g.setColor(color);
				    g.fillRect(2, 2, d.width-4, d.height-4);
			    }
		    };

		@Override
		public Component getTableCellEditorComponent(JTable table,
		    Object value,
		    boolean isSelected,
		    int row,
		    int column) {
			FoldConfig foldConfig=getFoldConfig(row);
			//Log.log(Log.NOTICE, this, "foldConfig of cellEditor: "+foldConfig.getName());
			if(foldConfig==null)
				throw new AssertionError();
			this.color=foldConfig.getColor();
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return color;
		}
	}

	public void setModeConfig(ModeConfig modeConfig) {
		this.modeConfig=modeConfig;
		fireTableDataChanged();
	}

	ModeConfig getModeConfig() {
		return modeConfig;
	}

	@Override
	public int getRowCount() {
		return modeConfig==null? 0: modeConfig.foldConfigsA.size();
	}
	@Override
	public int getColumnCount() {
		return 3;
	}
	@Override
	public String getColumnName(int col) {
		switch(col) {
		case 0:
			return ""	;
		case 1:
			return "Name";
		case 2:
			return "Regular Expression";
		default:
			return null;
		}
	}

	int getSelectedIndex() {
		return table.getSelectedRow();
	}

	void setSelectedRow(int row) {
		if(row<0 || row>=modeConfig.foldConfigsA.size())
			return;
		table.setRowSelectionInterval(row, row);
	}

	FoldConfig getSelected() {
		int row=table.getSelectedRow();
		if(row<0)
			return null;
		return modeConfig.foldConfigsA.get(row);
	}

	private FoldConfig getFoldConfig(int row) {
		return modeConfig==null? null: modeConfig.foldConfigsA.get(row);
	}

	@Override
	public Object getValueAt(int row, int col) {
		FoldConfig foldConfig=getFoldConfig(row);
		if(foldConfig==null)
			return null;
		switch(col) {
		case 0:
			return foldConfig.getColor();
		case 1 :
			return foldConfig.getName();
		case 2:
			return foldConfig.getRegex();
		default:
			return null;
		}
	}

	@Override
	public Class getColumnClass(int col) {
		switch(col) {
		case 0:
			return Color.class;
		default:
			return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if(modeConfig!=null && modeConfig==modeConfig.config.defaultModeConfig &&
		        col!=0)
			return false;
		return true;
	}

	@Override
	public void setValueAt(Object o, int row, int col) {
		FoldConfig foldConfig=getFoldConfig(row);
		if(foldConfig==null)
			throw new AssertionError();
		switch(col) {
		case 0:
			Color color=(Color)o;
			foldConfig.setColor(color);
			break;
		case 1:
			String name=(String)o;
			foldConfig.setName(name);
			break;
		case 2:
			try {
				String regex=(String)o;
				foldConfig.setRegex(regex);
			} catch(PatternSyntaxException ex) {}
			break;
		}
	}

}
