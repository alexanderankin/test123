/*
* PythonShell is a Console shell for hosting a Python REPL.
* Copyright (c) 2012 Damien Radtke - www.damienradtke.org
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version
* 2.0 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
*
* For more information, visit http://www.gnu.org/copyleft
*/

package python.shell;

//{{{ Imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSeparator;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;
//}}}

public class PythonShellOptionPane extends AbstractOptionPane {
	private LinkedList<InterpreterWrapper> list;
	private JSeparator separator;
	private RolloverButton addButton;

	/**
	 * Constructor
	 */
	public PythonShellOptionPane() {
		super("python-shell");
	}

	/**
	 * Utility method for referencing interpreter properties.
	 */
	private String interpreter(int i) {
		return "python-shell.interpreter." + String.valueOf(i);
	}

	/**
	 * Utility method for referencing interpreter parameter properties.
	 */
	private String parameters(int i) {
		return "python-shell.parameters." + String.valueOf(i);
	}

	/**
	 * Draws the option pane.
	 */
	private void draw() {
		removeAll();

		addComponent(new JLabel(jEdit.getProperty("options.python-shell.description")));
		for (InterpreterWrapper wrapper : this.list) {
			addComponent(wrapper.panel);
		}

		addSeparator();
		addComponent(addButton);
		revalidate();
		repaint();
	}

	/**
	 * Initialize the option pane.
	 */
	protected void _init() {
		this.list = new LinkedList<InterpreterWrapper>();
		for (int i = 0; true; i++) {
			String cmd = jEdit.getProperty(interpreter(i));
			if (cmd == null)
				break;

			String params = jEdit.getProperty(parameters(i), "");
			InterpreterWrapper wrapper = new InterpreterWrapper(i, cmd, params);
			this.list.add(wrapper);
		}

		addButton = new RolloverButton(GUIUtilities.loadIcon(jEdit.getProperty("options.toolbar.add.icon")));
		addButton.setToolTipText(jEdit.getProperty("options.python-shell.tooltip.add"));
		addButton.addActionListener(new AddHandler());

		this.draw();
	}

	/**
	 * Save current settings.
	 */
	protected void _save() {
		for (InterpreterWrapper wrapper : this.list) {
			jEdit.setProperty(interpreter(wrapper.index), wrapper.cmdField.getActualText());
			jEdit.setProperty(parameters(wrapper.index), wrapper.paramsField.getActualText());
		}

		// remove ones that may have been deleted
		for (int i = this.list.size(); true; i++) {
			String propName = interpreter(i);
			if (jEdit.getProperty(propName) == null)
				break;

			jEdit.unsetProperty(propName);
			jEdit.unsetProperty(parameters(i));
		}

		EditBus.send(new DynamicMenuChanged("plugin.python.shell.PythonShellPlugin.menu"));
	}

	/**
	 * Listener for Browse actions.
	 */
	class BrowseHandler implements ActionListener {
		private InterpreterWrapper wrapper;
		public BrowseHandler(InterpreterWrapper wrapper) {
			this.wrapper = wrapper;
		}

		public void actionPerformed(ActionEvent e) {
			// TODO: change the default directory to be where the current interpreter is
			VFSFileChooserDialog dialog = new VFSFileChooserDialog(
				jEdit.getActiveView(), System.getProperty("user.dir") + File.separator,
				VFSBrowser.OPEN_DIALOG, false, true);
			String[] files = dialog.getSelectedFiles();
			if (files != null) {
				this.wrapper.cmdField.setText(files[0]);
			}
		}
	}

	/**
	 * Listener for removing an interpreter.
	 */
	class RemoveHandler implements ActionListener {
		private InterpreterWrapper wrapper;
		public RemoveHandler(InterpreterWrapper wrapper) {
			this.wrapper = wrapper;
		}

		public void actionPerformed(ActionEvent e) {
			list.remove(this.wrapper);
			for (int i = 0; i<list.size(); i++) {
				InterpreterWrapper wrapper = list.get(i);
				wrapper.index = i;
			}

			draw();
		}
	}

	/**
	 * Listener for adding an interpreter.
	 */
	class AddHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			InterpreterWrapper wrapper = new InterpreterWrapper(list.size(), "", "");
			list.add(wrapper);
			draw();
		}
	}

	/**
	 * Wrapper class for each interpreter.
	 */
	class InterpreterWrapper {
		public PlaceholderField cmdField;
		public PlaceholderField paramsField;
		public JPanel panel;
		public int index;

		public InterpreterWrapper(int index, String cmd, String params) {
			this.index = index;
			this.cmdField = new PlaceholderField(cmd, 30, "Interpreter");
			this.paramsField = new PlaceholderField(params, 20, "Parameters");

			JButton browseButton = new JButton(jEdit.getProperty("vfs.browser.browse.label"));
			browseButton.setToolTipText(jEdit.getProperty("options.python-shell.tooltip.browse"));
			browseButton.addActionListener(new BrowseHandler(this));

			this.panel = new JPanel();
			this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.X_AXIS));
			this.panel.add(this.cmdField);
			this.panel.add(Box.createRigidArea(new Dimension(2, 0)));
			this.panel.add(this.paramsField);
			this.panel.add(browseButton);
			if (index > 0) {
				RolloverButton removeButton = new RolloverButton(GUIUtilities.loadIcon(jEdit.getProperty("options.toolbar.remove.icon")));
				removeButton.setToolTipText(jEdit.getProperty("options.python-shell.tooltip.remove"));
				removeButton.addActionListener(new RemoveHandler(this));
				this.panel.add(removeButton);
			}
		}
	}

	/**
	 * Custom text field that displays a placeholder text when empty.
	 */
	class PlaceholderField extends JTextField {
		private boolean isEmpty;

		public PlaceholderField(String text, int columns, final String placeholder) {
			super(text, columns);
			this.isEmpty = (text.length() == 0);
			final Color regularColor = Color.BLACK;
			final Color placeholderColor = Color.GRAY;

			if (this.isEmpty) {
				this.setForeground(placeholderColor);
				this.setText(placeholder);
			} else {
				this.setForeground(regularColor);
				this.setText(getText());
			}

			this.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (isEmpty) {
						setForeground(regularColor);
						setText("");
						isEmpty = false;
					}
				}

				public void focusLost(FocusEvent e) {
					if (getText().length() == 0) {
						isEmpty = true;
						setForeground(placeholderColor);
						setText(placeholder);
					}
				}
			});
		}

		public String getActualText() {
			return (this.isEmpty ? "" : getText());
		}
	}
}
