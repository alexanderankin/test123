/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package browser;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import options.GlobalOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.util.Log;

abstract public class GlobalResultsView extends JPanel implements DefaultFocusComponent,
	GlobalDockableInterface
{

	private View view;
	private JTable table;
	private GlobalTableModel model;
	private JTextField symbolTF;
	
	protected GlobalResultsView(final View view) {
		super(new BorderLayout());
	
		this.view = view;
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(false);
		table.setCellEditor(null);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = table.rowAtPoint(e.getPoint());
					GlobalReference ref = ((GlobalTableModel)table.getModel()).getRef(index);
					ref.jump(view);
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent ev) {
				if (ev.getKeyCode() == KeyEvent.VK_ENTER)
					ev.consume();
			}
			@Override
			public void keyReleased(KeyEvent ev) {
				if (ev.getKeyCode() == KeyEvent.VK_ENTER)
					ev.consume();
			}
			@Override
			public void keyPressed(KeyEvent ev) {
				if (ev.getKeyCode() == KeyEvent.VK_ENTER)
				{
					int index = table.getSelectedRow();
					GlobalReference ref = ((GlobalTableModel)table.getModel()).getRef(index);
					ref.jump(view);
					ev.consume();
				}	
			}
		});
		model = new GlobalTableModel();
		table.setModel(model);
		add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel symbolPanel = new JPanel(new BorderLayout());
		symbolPanel.add(new JLabel("Symbol:"), BorderLayout.WEST);
		symbolTF = new JTextField(40);
		symbolTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					show(view, symbolTF.getText());
			}
		});
		symbolPanel.add(symbolTF, BorderLayout.CENTER);
		add(symbolPanel, BorderLayout.NORTH);
	}
	
	abstract protected String getParam();
	
	private String getBufferDirectory() {
		File file = new File(view.getBuffer().getPath());
		return file.getParent();
	}
	
	public void show(View view, String identifier) {
		long start = System.currentTimeMillis();
		model.clear();
		symbolTF.setText(identifier);
		Vector<GlobalRecord> refs = GlobalLauncher.instance().run(
			getParam() + " " + identifier, getBufferDirectory());
		GlobalReference ref = null;
		for (int i = 0; i < refs.size(); i++)
			model.add(ref = new GlobalReference(refs.get(i)));
		if (ref != null && model.getRowCount() == 1 && GlobalOptionPane.isJumpImmediately())
			ref.jump(view);
		long end = System.currentTimeMillis();
		Log.log(Log.DEBUG, this.getClass(), "GlobalResultsView(" + getParam() +
			", " + identifier + "' took " + (end - start) * .001 + " seconds.");
	}
	
	public void focusOnDefaultComponent() {
		table.requestFocus();
	}
}
