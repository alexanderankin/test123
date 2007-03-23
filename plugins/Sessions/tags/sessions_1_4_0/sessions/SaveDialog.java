/*
 * SaveDialog.java - Close all buffers dialog
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Davide Del Vento
 * Copyright (C) 2007 Steve Jakob
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

package sessions;

import org.gjt.sp.jedit.gui.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.*;

public class SaveDialog extends EnhancedDialog
{

	public SaveDialog(View view)
	{
		super(view,jEdit.getProperty("session.savedialog.title"),true);

		this.view = view;

		JPanel content = new JPanel(new BorderLayout(12,12));
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		Box iconBox = new Box(BoxLayout.Y_AXIS);
		iconBox.add(new JLabel(UIManager.getIcon("OptionPane.warningIcon")));
		iconBox.add(Box.createGlue());
		content.add(BorderLayout.WEST,iconBox);

		JPanel centerPanel = new JPanel(new BorderLayout());

		JLabel label = new JLabel(jEdit.getProperty("session.savedialog.caption"));
		label.setBorder(new EmptyBorder(0,0,6,0));
		centerPanel.add(BorderLayout.NORTH,label);

		bufferList = new JList(bufferModel = new DefaultListModel());
		bufferList.setVisibleRowCount(10);
		bufferList.setEnabled(false);
		
		Session currentSession = SessionManager.getInstance().getCurrentSessionInstance();

		bufferModel.addElement(jEdit.getProperty("session.savedialog.added"));
		Buffer[] buffers = jEdit.getBuffers();
		Vector newFiles = new Vector(buffers.length);
		
		for(int i = 0; i < buffers.length; i++)
		{
			Buffer buffer = buffers[i];
			if (!currentSession.hasFile(buffer.getPath()))
				bufferModel.addElement(buffer.getPath());
			
			newFiles.add(buffer.getPath());
		}
		
		bufferModel.addElement("\n");
		bufferModel.addElement(jEdit.getProperty("session.savedialog.removed"));
		
		Enumeration oldFiles = currentSession.getAllFiles();
		
		if (oldFiles.hasMoreElements())
		{
			for(Object oneFile = oldFiles.nextElement(); oldFiles.hasMoreElements(); oneFile=oldFiles.nextElement())
			{
				if (!newFiles.contains(oneFile))
				{
					bufferModel.addElement(oneFile);
				}
			}
		}

		centerPanel.add(BorderLayout.CENTER,new JScrollPane(bufferList));

		content.add(BorderLayout.CENTER,centerPanel);

		ActionHandler actionListener = new ActionHandler();

		Box buttons = new Box(BoxLayout.X_AXIS);
		buttons.add(Box.createGlue());
		buttons.add(bt_ok = new JButton(jEdit.getProperty("session.savedialog.save")));
		bt_ok.addActionListener(actionListener);
		buttons.add(Box.createGlue());
		buttons.add(bt_cancel = new JButton(jEdit.getProperty("session.savedialog.dontsave")));
		bt_cancel.addActionListener(actionListener);
		buttons.add(Box.createGlue());

		content.add(BorderLayout.SOUTH,buttons);

		GUIUtilities.requestFocus(this,bufferList);

		pack();
		setLocationRelativeTo(view);
		setVisible(true);
	} 
	
	public boolean isOK()
	{
		return ok;
	} 

	public void ok()
	{
		ok = true;
		dispose();
	}

	public void cancel()
	{
		ok = false;
		dispose();
	}

	private View view;
	private JList bufferList;
	private DefaultListModel bufferModel;
	private JButton bt_ok;
	private JButton bt_cancel;

	private boolean ok = true; // default = save

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == bt_ok)
			{
				ok();
			}
			else if(source == bt_cancel)
			{
				cancel();
			}
		}
	} 

}
