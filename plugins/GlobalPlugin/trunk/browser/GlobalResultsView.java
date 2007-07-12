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
import java.awt.Toolkit;
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

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.util.Log;

import tags.TagsPlugin;

abstract public class GlobalResultsView extends JPanel implements DefaultFocusComponent {

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
		model = new DefaultListModel();
		list.setModel(model);
		setLayout(new BorderLayout());
		add(new JScrollPane(list), BorderLayout.CENTER);
		JPanel symbolPanel = new JPanel(new BorderLayout());
		symbolPanel.add(new JLabel("Symbol:"), BorderLayout.WEST);
		symbolTF = new JTextField(40);
		symbolTF.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					showResults(view, symbolTF.getText());
			}
		});
		symbolPanel.add(symbolTF, BorderLayout.CENTER);
		add(symbolPanel, BorderLayout.NORTH);
	}
	
	abstract protected String getParam();
	
	private void showResults(View view, String function) {
		long start = System.currentTimeMillis();
		model.removeAllElements();
		symbolTF.setText(function);
		Vector<GlobalRecord> refs = GlobalLauncher.instance().run(
			getParam() + " " + function, getBufferDirectory()); 
		for (int i = 0; i < refs.size(); i++)
			model.addElement(new GlobalReference(refs.get(i)));
		long end = System.currentTimeMillis();
		Log.log(Log.DEBUG, this.getClass(), "GlobalResultsView(" + getParam() +
			", " + function + "' took " + (end - start) * .001 + " seconds.");
	}
	
	private String getBufferDirectory() {
		File file = new File(view.getBuffer().getPath());
		return file.getParent();
	}
	
	public void show(View view) {
		String selected = view.getTextArea().getSelectedText();
		if (selected == null)
			selected = TagsPlugin.getTagNameAtCursor(view.getTextArea());
		if (selected == null) {
			Log.log(Log.ERROR, CallTree.class,
					"No function selected");
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		showResults(view, selected);
	}
	
	public void focusOnDefaultComponent() {
		list.requestFocus();
	}
}
