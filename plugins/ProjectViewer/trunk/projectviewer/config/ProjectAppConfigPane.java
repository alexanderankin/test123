/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package projectviewer.config;

//{{{ Imports
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.table.TableColumn;
import javax.swing.table.AbstractTableModel;

import java.util.Set;
import java.util.Map;
import java.util.Iterator;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

import projectviewer.gui.ModalJFileChooser;
//}}}

/**
 *	Option pane for the external applications configuration.
 *
 *	@author		Matthew Payne
 *	@version	$Id$
 */
public class ProjectAppConfigPane extends AbstractOptionPane
							  implements ActionListener, MouseListener {

	//{{{ Static constants

	private final static String EXTENSIONS_TEXT  = "Extension:";
	private final static String APPLICATION_TEXT = "Application:";

	//}}}

 	//{{{ Private members
	private JPopupMenu popmenu;
	private JMenuItem menuActions;
	private JMenuItem delApp;

	private JButton cmdAdd;
	private JButton cmdChooseFile;

	private JTextField appField;
	private JTextField extField;

	private JTable appTable;
	private AppLauncher apps;
	private AppModel model;
	//}}}

	//{{{ Constructors

	public ProjectAppConfigPane(String name) {
		super(name);
	}

	//}}}

	//{{{ _init() method
	protected void _init() {

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gb);

		JLabel extLabel = new JLabel(EXTENSIONS_TEXT);
		JLabel appLabel = new JLabel(APPLICATION_TEXT);

		appField = new JTextField();
		extField = new JTextField();

		cmdAdd = new JButton("Add");
		cmdAdd.addActionListener(this);

		cmdChooseFile = new JButton("...");
		cmdChooseFile.addActionListener(this);

		// first line: labels

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gb.setConstraints(extLabel, gbc);
		add(extLabel);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.weightx = 2.0;
		gb.setConstraints(appLabel, gbc);
		add(appLabel);

		// second line: text fields and buttons

		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gb.setConstraints(extField, gbc);
		add(extField);


		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2.0;
		gb.setConstraints(appField, gbc);
		add(appField);

		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gb.setConstraints(cmdChooseFile, gbc);
		add(cmdChooseFile);

		gbc.gridx = 3;
		gb.setConstraints(cmdAdd, gbc);
		add(cmdAdd);

		// third line: table

		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		apps = new AppLauncher();
		apps.copy(AppLauncher.getInstance());
		model = new AppModel(apps);
		appTable = new JTable(model);
		appTable.addMouseListener(this);

  		TableColumn col = appTable.getColumnModel().getColumn(1);
		col.setPreferredWidth(appTable.getColumnModel().getColumn(0).getWidth() * 5);
		JScrollPane jsp = new JScrollPane(appTable);
		gb.setConstraints(jsp, gbc);
		add(jsp);

		// popup menu
		popmenu = new JPopupMenu();

		/*
		menuAction = new JMenuItem();
		menuActions.setText("Actions");
		menuActions.setEnabled(false);
		popmenu.add(menuActions);
		popmenu.addSeparator();
		*/

		delApp = new JMenuItem("Delete");
		delApp.addActionListener(this);
		popmenu.add(delApp);

	} //}}}

	//{{{ Event Handling

	//{{{ actionPerformed() method
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == cmdAdd) {
			if (extField.getText().length() >= 1 && appField.getText().length() >=1) {
				apps.addAppExt(extField.getText(), replaceString(appField.getText(), "\\", "/"));
				extField.setText("");
				appField.setText("");
				model.requestRefresh();
			 }
		} else if (ae.getSource() == cmdChooseFile) {
			doChoose();
		} else if (ae.getSource() == delApp) {
			deleteRow();
		}

	} //}}}

	//{{{ Mouse Listener Interface Implementation

	private void handleMouseEvent(MouseEvent evt) {
		if (evt.isPopupTrigger()) {
			if (popmenu.isVisible()) {
				popmenu.setVisible(false);
			} else {
				popmenu.show((Component)evt.getSource(), evt.getX(), evt.getY());
			}
		}
	}

	public void mousePressed(MouseEvent evt) {
		handleMouseEvent(evt);
	}

	public void mouseReleased(MouseEvent evt) {
		handleMouseEvent(evt);
	}

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	//}}}

	//}}}

	//{{{ doChoose() method
	public void doChoose() {
		// Used for selected and executable file
		JFileChooser chooser = new ModalJFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showDialog(this, jEdit.getProperty("projectviewer.general.choose"))
				!= JFileChooser.APPROVE_OPTION)
			return;
		try {
			appField.setText(chooser.getSelectedFile().getPath());
		} catch (Exception Excp) { }
	} //}}}

	//{{{ replaceString() method
	private static String replaceString(String aSearch, String aFind, String aReplace)
	{
		String result = aSearch;
		if (result != null && result.length() > 0) {
			int a = 0;
			int b = 0;
			while (true) {
				a = result.indexOf(aFind, b);
				if (a != -1) {
					result = result.substring(0, a) + aReplace + result.substring(a + aFind.length());
					b = a + aReplace.length();
				}
				else
				break;
			}
		}
		return result;
	} //}}}

	//{{{ deleteRow() method
	private void deleteRow() {
		// Deletes a row from the Table:
		int targetRow;
		String keyCol;

		if (appTable.getSelectedRowCount() > 0) {
			targetRow = appTable.getSelectedRow();
			keyCol = (String)appTable.getValueAt(targetRow, 0);
			apps.removeAppExt(keyCol);
			model.requestRefresh();
		}
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		try {
			AppLauncher launcher = AppLauncher.getInstance();
			launcher.copy(apps);
			launcher.storeExts();
		} catch (java.io.IOException e) {
			Log.log(Log.ERROR, this, e);
		}
	}//}}}

	//{{{ AppModel class
	private static class AppModel extends AbstractTableModel {

		/**
		 * Constructs an AppList table model.
		 * @param appAssoc  the collection of extentions and associations
		 */
		public AppModel(AppLauncher appList) {
			appSet = appList.getAppList();
		}

		public int getRowCount() {
			return appSet.size();
		}

		public void requestRefresh() {
			/* Used to refresh the table */
			super.fireTableDataChanged();
		}

		public int getColumnCount() {
			return 2;
		}

		public Object getValueAt(int r, int c) {
			String sValue;

			Iterator iter = appSet.iterator();
			int iCurrentRow = 0;

			while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
				if (iCurrentRow == r)
				switch(c) {
					case 0: return entry.getKey();
					case 1: return entry.getValue();
				}
				Object key = entry.getKey();
				Object value = entry.getValue();
				iCurrentRow++;
			}
			return jEdit.getProperty("projectviewer.appconfig.no_value");
		}

		public String getColumnName(int c) {
			return (c == 0) ?
				jEdit.getProperty("projectviewer.appconfig.extension") :
				jEdit.getProperty("projectviewer.appconfig.application");
		}

		private Set appSet;

	} //}}}

}

