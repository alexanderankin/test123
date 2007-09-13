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

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import options.GlobalOptionPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.util.Log;

abstract public class GlobalResultsView extends JPanel implements DefaultFocusComponent,
	GlobalDockableInterface
{

	private View view;
	private JList list;
	private DefaultListModel model;
	private JTextField symbolTF;
	
	protected GlobalResultsView(final View view) {
		super(new BorderLayout());
	
		this.view = view;
		list = new JList();
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = list.locationToIndex(e.getPoint());
					Object obj = list.getModel().getElementAt(index);
					if (obj instanceof GlobalReference)
						((GlobalReference) obj).jump(view);
				}
			}
		});
		list.addKeyListener(new KeyAdapter() {
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
					Object obj = list.getModel().getElementAt(list.getSelectedIndex());
					if (obj instanceof GlobalReference)
						((GlobalReference) obj).jump(view);
					ev.consume();
				}	
			}
		});
		model = new DefaultListModel();
		list.setModel(model);
		add(new JScrollPane(list), BorderLayout.CENTER);
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
		model.removeAllElements();
		symbolTF.setText(identifier);
		Vector<GlobalRecord> refs = GlobalLauncher.instance().run(
			getParam() + " " + identifier, getBufferDirectory());
		GlobalReference ref = null;
		for (int i = 0; i < refs.size(); i++)
			model.addElement(ref = new GlobalReference(refs.get(i)));
		if (ref != null && model.size() == 1 && GlobalOptionPane.isJumpImmediately())
			ref.jump(view);
		long end = System.currentTimeMillis();
		Log.log(Log.DEBUG, this.getClass(), "GlobalResultsView(" + getParam() +
			", " + identifier + "' took " + (end - start) * .001 + " seconds.");
	}
	
	public void focusOnDefaultComponent() {
		list.requestFocus();
	}
}
