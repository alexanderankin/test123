package org.jedit.plugins.columnruler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.ColorWellButton;

/**
 *  Dialog for creating new marks/guides.
 *
 * @author     Brad Mace
 * @version    $Revision: 1.1 $ $Date: 2006-02-27 15:00:45 $
 */
class MarkDialog extends JDialog implements ActionListener {
	private MarkContainer markContainer;
	private Mark mark;
	private JTextField name;
	private JTextField column;
	private ColorWellButton color;
	private JButton ok;
	private JButton cancel;

	/*
	 * Creates dialog for adding a new mark.
	 */
	public MarkDialog(MarkContainer mc, int column) {
		super(jEdit.getActiveView(), "Add Mark");
		markContainer = mc;
		name = new JTextField(5);
		this.column = new JTextField(column + "");
		color = new ColorWellButton(Color.WHITE);
		init();
	}

	/**
	 * Creates dialog for editing a mark.
	 */
	public MarkDialog(Mark m, String title) {
		super(jEdit.getActiveView(), title, true);
		this.mark = m;
		if (m == null) {
			name = new JTextField(5);
			column = new JTextField(4);
			color = new ColorWellButton(Color.WHITE);
		} else {
			name = new JTextField(m.getName());
			column = new JTextField(m.getColumn() + "");
			color = new ColorWellButton(m.getColor());
		}
		init();
	}

	private void init() {
		ok = new JButton("OK");
		ok.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		getContentPane().setLayout(new GridLayout(4, 2));
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
			if (mark == null) {
				mark = new Mark(name.getText());
			} else {
				mark.setName(name.getText());
			}
			mark.setColumn(Integer.parseInt(column.getText()));
			mark.setColor(color.getSelectedColor());
			if (markContainer != null) {
				if (!markContainer.containsMark(mark)) {
					markContainer.addMark(mark);
				}
			}
		}
		dispose();
	}
}
