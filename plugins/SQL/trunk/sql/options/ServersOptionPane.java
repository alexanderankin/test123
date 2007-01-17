/**
 * ServersOptionPane.java - Sql Plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 *
 * Copyright (C) 2001 Sergey V. Udaltsov
 * svu@users.sourceforge.net
 *
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

package sql.options;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;

import common.gui.pathbuilder.*;

import projectviewer.vpt.*;

import sql.*;
import sql.preprocessors.*;

/**
 *  Description of the Class
 *
 * @author     svu
 */
public class ServersOptionPane extends SqlOptionPane
			implements AncestorListener
{
	private JList allServersLst;

	private JButton addServerBtn;
	private JButton editServerBtn;
	private JButton delServerBtn;
	private JButton importServerBtn;
	private JButton exportServerBtn;

	private JDialog parentDialog = null;

	private VPTProject project;


	/**
	 *  Constructor for the SqlOptionPane object
	 *
	 * @param  project  Description of Parameter
	 * @since
	 */
	public ServersOptionPane(VPTProject project)
	{
		super("sql.servers");
		this.project = project;
	}


	/**
	 *Description of the Method
	 *
	 * @since
	 */
	public void _init()
	{
		super._init();

		JPanel panel = new JPanel();
		{
			panel.setLayout(new BorderLayout(5, 5));

			JPanel hp = new JPanel(new BorderLayout(5, 5));
			{
				hp.add(new JLabel(jEdit.getProperty("sql.options.servers.label")), BorderLayout.WEST);
			}
			panel.add(hp, BorderLayout.NORTH);

			hp = new JPanel(new BorderLayout(5, 5));
			{
				hp.setBorder(new BevelBorder(BevelBorder.LOWERED));
				allServersLst = new JList();
				hp.add(allServersLst, BorderLayout.CENTER);
			}
			panel.add(hp, BorderLayout.CENTER);

			hp = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			JPanel bp = new JPanel(new GridLayout(1, 0, 5, 5));
			{
				addServerBtn = new JButton(jEdit.getProperty("sql.options.addServerBtn.label"));
				bp.add(addServerBtn);

				editServerBtn = new JButton(jEdit.getProperty("sql.options.editServerBtn.label"));
				bp.add(editServerBtn);

				delServerBtn = new JButton(jEdit.getProperty("sql.options.delServerBtn.label"));
				bp.add(delServerBtn);

				importServerBtn = new JButton(jEdit.getProperty("sql.options.importServerBtn.label"));
				bp.add(importServerBtn);

				exportServerBtn = new JButton(jEdit.getProperty("sql.options.exportServerBtn.label"));
				bp.add(exportServerBtn);
			}
			hp.add(bp);
			panel.add(hp, BorderLayout.SOUTH);
		}
		add(panel, BorderLayout.NORTH);

		allServersLst.addListSelectionListener(
		        new ListSelectionListener()
		        {
			        public void valueChanged(ListSelectionEvent evt)
			        {
				        updateServerListButtons();
			        }
		        });

		addServerBtn.addActionListener(
		        new ActionListener()
		        {
			        public void actionPerformed(ActionEvent evt)
			        {
				        final SqlServerDialog dlg = new SqlServerDialog(parentDialog,
				                                    null,
				                                    SqlServerDialog.ADD_MODE);
				        dlg.setVisible(true);
				        final SqlServerRecord rec = dlg.getResult();
				        if (rec == null)
					        return;

				        rec.save(project);
				        updateServerList();
			        }
		        }
		);

		editServerBtn.addActionListener(
		        new ActionListener()
		        {
			        public void actionPerformed(ActionEvent evt)
			        {
				        final String name = (String) allServersLst.getSelectedValue();
				        if (name == null)
					        return;
				        final SqlServerRecord rec = SqlServerRecord.get(project, name);
				        if (rec == null)
					        return;

				        final SqlServerDialog dlg = new SqlServerDialog(parentDialog,
				                                    rec,
				                                    SqlServerDialog.EDIT_MODE);

				        dlg.setVisible(true);
				        if (dlg.getResult() == null)
					        return;

				        rec.save(project);
			        }
		        }
		);

		delServerBtn.addActionListener(
		        new ActionListener()
		        {
			        public void actionPerformed(ActionEvent evt)
			        {
				        final String name = (String) allServersLst.getSelectedValue();
				        if (name == null)
					        return;
				        final SqlServerRecord rec = SqlServerRecord.get(project, name);
				        if (rec == null)
					        return;

				        final SqlServerDialog dlg = new SqlServerDialog(parentDialog,
				                                    rec,
				                                    SqlServerDialog.DEL_MODE);
				        dlg.setVisible(true);

				        if (dlg.getResult() != null)
				        {
					        rec.delete(project);
					        updateServerList();
				        }
			        }
		        }
		);

		exportServerBtn.addActionListener(
		        new ActionListener()
		        {
			        public void actionPerformed(ActionEvent evt)
			        {
				        int btn = GUIUtilities.confirm(parentDialog, "sql.options.export.warning",
				                                       null, JOptionPane.OK_CANCEL_OPTION,
				                                       JOptionPane.WARNING_MESSAGE);

				        if (btn == JOptionPane.CANCEL_OPTION)
					        return;

				        final String name = (String) allServersLst.getSelectedValue();
				        if (name == null)
					        return;
				        final SqlServerRecord rec = SqlServerRecord.get(project, name);
				        if (rec == null)
					        return;

				        final String files[] = GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), null, VFSBrowser.SAVE_DIALOG, false);
				        if (files == null || files.length == 0)
					        return;

				        final String file = files[0];
				        Log.log(Log.DEBUG, ServersOptionPane.class,
				                "Exporting " + name + " to the file: " + file);

				        rec.exportTo(file, parentDialog);
			        }
		        }
		);

		importServerBtn.addActionListener(
		        new ActionListener()
		        {
			        public void actionPerformed(ActionEvent evt)
			        {
				        final String files[] = GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), null, VFSBrowser.OPEN_DIALOG, false);
				        if (files == null || files.length == 0)
					        return;

				        final String file = files[0];
				        Log.log(Log.DEBUG, ServersOptionPane.class,
				                "Importing from the file: " + file);

				        final SqlServerRecord rec = SqlServerRecord.importFrom(file);
				        if (rec == null)
					        return;

				        rec.save(project);
				        updateServerList();
			        }
		        }
		);

		updateServerList();

		addAncestorListener(this);
	}


	public void ancestorAdded(AncestorEvent evt)
	{
		final Component cp = this.getTopLevelAncestor();
		parentDialog = (cp instanceof JDialog) ? (JDialog) cp : null;
	}


	public void ancestorMoved(AncestorEvent evt)
	{
	}


	public void ancestorRemoved(AncestorEvent evt)
	{
		ancestorAdded(evt);
	}


	/**
	 *  Description of the Method
	 *
	 * @since
	 */
	public void _save()
	{
		for (Iterator e = SqlServerRecord.getAllRecords(project).values().iterator(); e.hasNext();)
		{
			final SqlServerRecord rec = (SqlServerRecord) e.next();
			rec.save(project);
		}

		final String name = (String) allServersLst.getSelectedValue();
		if (name != null)
			SqlUtils.setSelectedServerName(project, name);
		else
			SqlUtils.setSelectedServerName(project, null);
	}


	/**
	 *  Description of the Method
	 *
	 * @since
	 */
	protected void updateServerList()
	{
		allServersLst.setListData(SqlServerRecord.getAllNames(project));

		final String srv2select = SqlUtils.getSelectedServerName(project);

		allServersLst.setSelectedValue(srv2select, true);

		updateServerListButtons();
	}


	private void updateServerListButtons()
	{
		final boolean isAny = allServersLst.getSelectedIndex() != -1;

		delServerBtn.setEnabled(isAny);
		editServerBtn.setEnabled(isAny);
		exportServerBtn.setEnabled(isAny);
	}
}

