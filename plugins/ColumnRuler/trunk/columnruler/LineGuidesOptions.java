package columnruler;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;

public class LineGuidesOptions extends AbstractOptionPane implements ActionListener {
	private JCheckBox caretGuide;
	private JCheckBox wrapGuide;
	private JButton addButton;
	private JButton removeButton;
	private JTable table;
	private GuideTableModel model;
	private ArrayList guides;
	private Comparator columnComparator;
	
	public LineGuidesOptions() {
		super("columnruler.lineguides");
	}
	
	protected void _init() {
		guides = new ArrayList();
		columnComparator = new ColumnComparator();
		
		int i = 0;
		String name = jEdit.getProperty("options.columnruler.marks."+i+".name");
		while (name != null) {
			Mark m = new Mark(name);
			m.setColumn(jEdit.getIntegerProperty("options.columnruler.marks."+i+".column",0));
			m.setColor(jEdit.getColorProperty("options.columnruler.marks."+i+".color",Color.WHITE));
			guides.add(m);
			i++;
			name = jEdit.getProperty("options.columnruler.marks."+i+".name");
		}
			
		
		JPanel dynamicGuides = new JPanel(new GridLayout(2,1));
		dynamicGuides.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Dynamic Marks/Guides"));
		caretGuide = new JCheckBox("Show caret guide",jEdit.getBooleanProperty("options.columnruler.marks.caret.guide"));
		wrapGuide = new JCheckBox("Show wrap guide",jEdit.getBooleanProperty("options.columnruler.marks.wrap.guide"));
		dynamicGuides.add(caretGuide);
		dynamicGuides.add(wrapGuide);
		addComponent(dynamicGuides,GridBagConstraints.HORIZONTAL);
		
		model = new GuideTableModel();
		table = new JTable(model);
		addComponent(createTablePanel(),GridBagConstraints.BOTH);
	}

	
	protected void _save() {
		jEdit.setBooleanProperty("options.columnruler.marks.caret.guide",caretGuide.isSelected());
		jEdit.setBooleanProperty("options.columnruler.marks.wrap.guide",wrapGuide.isSelected());

		int i = 0;
		for (i = 0; i < guides.size(); i++) {
			Mark mark = (Mark) guides.get(i);
			jEdit.setProperty("options.columnruler.marks."+i+".name",mark.getName());
			jEdit.setIntegerProperty("options.columnruler.marks."+i+".column",mark.getColumn());
			jEdit.setColorProperty("options.columnruler.marks."+i+".color",mark.getColor());
			jEdit.setBooleanProperty("options.columnruler.marks."+i+".guide",true);
		}
		jEdit.unsetProperty("options.columnruler.marks."+i+".name");
		jEdit.getActiveView().getTextArea().repaint();
	}
	
	//{{{ actionPerformed()
	public void actionPerformed(ActionEvent evt)
	{
		Object obj = evt.getSource();

		if (obj == addButton)
			addGuide();
		else if (obj == removeButton)
			removeGuide(table.getSelectedRow());
	}//}}}

	private JPanel createTablePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Static Marks/Guides"));
		
		JPanel buttonPanel = new JPanel();
		addButton = new JButton("Add Mark/Guide");
		addButton.addActionListener(this);
		buttonPanel.add(addButton);
		removeButton = new JButton("Remove Mark/Guide");
		removeButton.addActionListener(this);
		buttonPanel.add(removeButton);
		panel.add(buttonPanel,BorderLayout.SOUTH);
		
		panel.add(new JScrollPane(table),BorderLayout.CENTER);
		return panel;
	}
	
	//{{{ add/remove guide
	public void addGuide() {
		MarkDialog dialog = new MarkDialog(null,"Add Mark/Guide");
		dialog.pack();
		dialog.show();
	}
	
	public void removeGuide(int index) {
		guides.remove(index);
		model.fireTableDataChanged();
	}
	//}}}
	
	//{{{ Inner Classes
	
	//{{{ GuideTableModel
	class GuideTableModel extends AbstractTableModel {
		public GuideTableModel() {}
		
		public String getColumnName(int col) {
			switch(col) {
				case 0:
				return "Name";
				case 1: 
				return "Column";
				case 2:
				return "Color";
				default:
				return "error";
			}
		}
		
		public int getColumnCount() {
			return 3;
		}
		
		public int getRowCount() {
			return guides.size();
		}
		
		public Object getValueAt(int row, int col) {
			Mark mark = (Mark) guides.get(row);
			switch(col) {
				case 0:
					return mark.getName();
				case 1:
					return new Integer(mark.getColumn());
				case 2:
					return mark.getColor();
				default:
					return "error";
			}
		}
	} //}}}
	
	//{{{ ColumnComparator
	class ColumnComparator implements Comparator {
		public int compare(Object a, Object b) {
			Mark first = (Mark) a;
			Mark second = (Mark) b;
			return first.getColumn() - second.getColumn();
		}
		public boolean equals(Object other) {
			return other instanceof ColumnComparator;
		}
	}//}}}
	
	//{{{ MarkDialog
	class MarkDialog extends JDialog implements ActionListener {
		private JTextField name;
		private JTextField column;
		private ColorWellButton color;
		private JButton ok;
		private JButton cancel;
		
		public MarkDialog(Mark m,String title) {
			super(jEdit.getActiveView(),title,true);
			if (m == null) {
				name = new JTextField(30);
				column = new JTextField(4);
				color = new ColorWellButton(Color.WHITE);
			} else {
				name = new JTextField(m.getName());
				column = new JTextField(m.getColumn()+"");
				color = new ColorWellButton(m.getColor());
			}
			ok = new JButton("OK");
			ok.addActionListener(this);
			cancel = new JButton("Cancel");
			cancel.addActionListener(this);
			getContentPane().setLayout(new GridLayout(4,2));
			getContentPane().add(new JLabel("Name"));
			getContentPane().add(name);
			getContentPane().add(new JLabel("Column"));
			getContentPane().add(column);
			getContentPane().add(new JLabel("Color"));
			getContentPane().add(color);
			getContentPane().add(cancel);
			getContentPane().add(ok);
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ok) {
				Mark mark = new Mark(name.getText());
				mark.setColumn(Integer.parseInt(column.getText()));
				mark.setColor(color.getSelectedColor());
				guides.add(mark);
				Collections.sort(guides,columnComparator);
				model.fireTableDataChanged();
			}
			dispose();
		}
	} //}}}

	//}}}
	
}
