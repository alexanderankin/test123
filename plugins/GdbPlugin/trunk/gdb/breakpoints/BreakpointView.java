package gdb.breakpoints;

import gdb.breakpoints.BreakpointList.BreakpointListListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


@SuppressWarnings("serial")
public class BreakpointView extends JPanel {
	static private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private JList list;
	private DefaultListModel model;

	public BreakpointView() {
		setLayout(new BorderLayout());
		list = new JList();
		list.setCellRenderer(new BreakpointCellRenderer());
		list.addMouseListener(new MouseAdapter()
        {
           public void mousePressed(MouseEvent e)
           {
              int index = list.locationToIndex(e.getPoint());
              if (index != -1) {
                 BreakpointCheckBox bp =
                	 (BreakpointCheckBox)list.getModel().getElementAt(index);
                 bp.setSelected(!bp.isSelected());
                 repaint();
              }
           }
        });
		add(new JScrollPane(list));
		model = new DefaultListModel();
		list.setModel(model);
		Vector<Breakpoint> brks = BreakpointList.getInstance().getBreakpoints();
		for (int i = 0; i < brks.size(); i++)
		{
			Breakpoint bp = (Breakpoint)brks.get(i);
			model.addElement(new BreakpointCheckBox(bp));
		}
		BreakpointList.getInstance().addListListener(new BreakpointListListener() {
			public void breakpointAdded(Breakpoint bp) {
				model.addElement(new BreakpointCheckBox(bp));
			}
			public void breakpointRemoved(Breakpoint bp) {
				model.removeElement(new BreakpointCheckBox(bp));
			}
			public void breakpointChanged(Breakpoint bp) {
			}
		});
	}
	
	@SuppressWarnings("serial")
	static private class BreakpointCheckBox extends JCheckBox {
		private Breakpoint bp;
		
		public BreakpointCheckBox(Breakpoint bpt) {
			super(bpt.getFile() + ":" + bpt.getLine());
	      	bp = bpt;
	      	setSelected(bp.isEnabled());
    	}

		@Override
		public void setSelected(boolean b) {
			super.setSelected(b);
			bp.setEnabled(b);
		}
		
		public boolean equals(BreakpointCheckBox bp) {
			return (this.bp == bp.bp);
		}
	}
	static private class BreakpointCellRenderer implements ListCellRenderer
	{
		public Component getListCellRendererComponent(
				JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus)
		{
			BreakpointCheckBox cb = (BreakpointCheckBox)value;
			cb.setBackground(isSelected ?
					list.getSelectionBackground() : list.getBackground());
			cb.setForeground(isSelected ?
					list.getSelectionForeground() : list.getForeground());
			cb.setEnabled(list.isEnabled());
			cb.setFont(list.getFont());
			cb.setFocusPainted(false);
			cb.setBorderPainted(true);
			cb.setBorder(isSelected ?
					UIManager.getBorder("List.focusCellHighlightBorder") :
					noFocusBorder);
			return cb;
		}
	}
}