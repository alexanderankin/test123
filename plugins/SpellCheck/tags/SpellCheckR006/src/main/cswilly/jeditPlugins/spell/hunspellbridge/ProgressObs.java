/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2008 Eric Le Lay
 *
 * ProgressObs is freely adapted from
 * org/gjt/sp/jedit/pluginmgr/PluginManagerProgress.java
 * Copyright (C) 2000, 2001 Slava Pestov
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

package cswilly.jeditPlugins.spell.hunspellbridge;

import javax.swing.JButton;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.awt.Dialog;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;

public class ProgressObs extends JDialog implements ProgressObserver{
		//{{{ ProgressObs constructor
		public ProgressObs(Frame parent,String title,ActionListener action)
		{
			super(parent,title);
			this.stopAction = action;
			init();
		}

		public ProgressObs(Frame parent,String title,Thread thread)
		{
			super(parent,title);
			this.thread = thread;
			init();
		}
		
		public ProgressObs(Dialog parent,String title,Thread thread)
		{
			super(parent,title);
			this.thread = thread;
			init();
		}
		
		private void init(){
			JPanel content = new JPanel(new BorderLayout(12,12));
			content.setBorder(new EmptyBorder(12,12,12,12));
			setContentPane(content);
	
			progress = new JProgressBar();
			progress.setStringPainted(true);
			progress.setString("<WAITING>");
	
	
			progress.setMaximum(1);
			content.add(BorderLayout.NORTH,progress);
	
			
			if(thread!=null && stopAction==null){
				stopAction = new ActionListener(){
				public void actionPerformed(ActionEvent evt){
						ProgressObs.this.thread.stop();
						setVisible(false);
				}};
			}
			
			stop = new JButton(jEdit.getProperty("options.spellcheck.hunspell.stop","Stop"));
			stop.addActionListener(stopAction);
			JPanel panel = new JPanel(new FlowLayout(
				FlowLayout.CENTER,0,0));
			panel.add(stop);
			content.add(BorderLayout.CENTER,panel);
	
			addWindowListener(new WindowHandler());
	
			pack();
			new Thread(){
				public void run(){
					setVisible(true);
			}}.start();
		} //}}}
	
	
		//{{{ setValue() method
		/**
		 * Update the progress value.
		 *
		 * @param value the new value
		 */
		public void setValue(final long value)
		{
			SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						progress.setValue(valueSoFar + (int) value);
					}
				});
		} //}}}
	
		//{{{ setMaximum() method
		/**
		 * This method is unused with the plugin manager.
		 *
		 * @param value the new max value (it will be ignored)
		 * @since jEdit 4.3pre3
		 */
		public void setMaximum(final long value) 
		{
			SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						progress.setMaximum((int)value);
					}
				});
		} //}}}
	
		//{{{ setStatus() method
		 public void setStatus(final String status) 
		 {
			SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						 progress.setString(status);
					}
				});
		} //}}}
	
	
		//{{{ Private members
	
		//{{{ Instance variables
		private Thread thread;
	
		private JProgressBar progress;
		private JButton stop;
		private ActionListener stopAction;
		// progress value as of start of current task
		private int valueSoFar;
	
		//}}}
	
	
		//{{{ WindowHandler class
		class WindowHandler extends WindowAdapter
		{
			public void windowClosing(WindowEvent evt)
			{
				thread.stop();
				dispose();
			}
		} //}}}
	
		//}}}
	}

