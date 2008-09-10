/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2008 Eric Le Lay
 *
 * ProgressObserver is freely adapted from
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

import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import java.util.regex.Pattern;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.DefaultListModel;


import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.Action;
import java.awt.event.ActionEvent;

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
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;

import common.gui.FileTextField;

import static cswilly.jeditPlugins.spell.hunspellbridge.HunspellDictsManager.Dictionary;

import cswilly.spell.EngineManager;


public class HunspellOptionPane extends AbstractOptionPane{
	private HunspellDictsManager dictsManager;
	
	private JList listAvailable;
	private JList listInstalled;
	
	public HunspellOptionPane(){
		super("SpellCheck-Hunspell-bridge");
		HunspellEngineManager engMan = (HunspellEngineManager)ServiceManager.getService(EngineManager.class.getName(), "Hunspell");
		assert(engMan!=null);
		dictsManager = engMan.getDictsManager();
	}
	
	protected void _init(){
		
		//external library
		// TODO: external library
		final DefaultListModel dlm = new DefaultListModel();
		
		listInstalled = new JList(dlm);
		JScrollPane pi = new JScrollPane(listInstalled);
		addSeparator("options.spellcheck.hunspell.installed");
		addComponent(pi);
		
		final DefaultListModel dlma = new DefaultListModel();
		
		listAvailable = new JList(dlma);
		JScrollPane pa = new JScrollPane(listAvailable);
		addSeparator("options.spellcheck.hunspell.available");
		addComponent(pa);

		InstallUpdateAction install = new InstallUpdateAction();
		RemoveAction remove = new RemoveAction();
		
		listAvailable.addListSelectionListener(install);
		listAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listInstalled.addListSelectionListener(install);
		listInstalled.addListSelectionListener(remove);
		listInstalled.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		JPanel p = new JPanel();
		JButton bout = new JButton(install);
		bout.setName("install");
		p.add(bout);

		bout = new JButton(remove);
		bout.setName("remove");
		p.add(bout);

		addComponent(p);
		
		new Thread(){
			public void run(){
				// TODO: use a component inside the option pane to have it visible 
				ProgressObs po = new ProgressObs(
						jEdit.getActiveView(),
						jEdit.getProperty("options.spellcheck.hunspell.fetch-availables.title"),
						this
					);
				Log.log(Log.DEBUG,HunspellOptionPane.class,"Refresh availables started");
				List<Dictionary> av = dictsManager.getAvailables(po);
				po.setVisible(false);
				List<Dictionary> dicts = dictsManager.getInstalled();
				for(Dictionary d : av)dlma.addElement(d);
				for(Dictionary d : dicts)dlm.addElement(d);
				Log.log(Log.DEBUG,HunspellOptionPane.class,"Refresh availables done");
			}
		}.start();
		
	}
	protected void _save(){}
	
	private class InstallUpdateAction extends AbstractAction implements ListSelectionListener{
		Dictionary currentDict;
		int currentIndex;
		boolean update;
		private String updtText = jEdit.getProperty("options.spellcheck.hunspell.update");
		private String instalText = jEdit.getProperty("options.spellcheck.hunspell.install");
		InstallUpdateAction(){
			super(jEdit.getProperty("options.spellcheck.hunspell.install"));
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e){
			if(currentDict!=null){
				new Thread(){
					private Dictionary dict = currentDict;
					private int index = currentIndex;
					public void run(){
						ProgressObs po = new ProgressObs(
							GUIUtilities.getParentDialog(listAvailable),
							jEdit.getProperty("options.spellcheck.hunspell.install-dict.title",new String[]{dict.getDescription()}),
							this);
						if(update){
							boolean updated = dictsManager.update(dict,po);
							if(!updated){
								//todo
							}
						}else{
							boolean installed  = dictsManager.install(dict,po);
							po.setVisible(false);
							if(installed){
								SwingUtilities.invokeLater(new Runnable(){
										public void run(){
											((DefaultListModel)listInstalled.getModel()).addElement(dict);
											((DefaultListModel)listAvailable.getModel()).remove(index);
								}});
							}else{
								// TODO: nice message to the user...
							}
						}
					}
				}.start();
			}
		}
		
		public void valueChanged(ListSelectionEvent lse){
			JList src = (JList) lse.getSource();
			
			update = src==listInstalled;
			
			int[] indices = src.getSelectedIndices();
			if(indices.length==0){
				if(currentDict == null)return;
				setEnabled(false);
				currentIndex = -1;
				currentDict = null;
			}else{
				currentIndex = indices[0];
				currentDict = (Dictionary)((DefaultListModel)src.getModel()).elementAt(currentIndex);
				if(update){
					setEnabled(currentDict.isOutdated());
					putValue(Action.NAME,updtText);
					listAvailable.clearSelection();
				}else{
					setEnabled(true);
					putValue(Action.NAME,instalText);
					listInstalled.clearSelection();
				}
			}
		}
	}
	
	private class RemoveAction extends AbstractAction implements ListSelectionListener{
		Dictionary currentDict;
		int currentIndex;
		boolean update;
		
		RemoveAction(){
			super(jEdit.getProperty("options.spellcheck.hunspell.remove"));
			setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e){
			if(currentDict!=null){
				new Thread(){
					private Dictionary dict = currentDict;
					private int index = currentIndex;
					public void run(){
						boolean removed  = dictsManager.remove(dict);
						if(removed){
							SwingUtilities.invokeLater(new Runnable(){
									public void run(){
										((DefaultListModel)listAvailable.getModel()).addElement(dict);
										((DefaultListModel)listInstalled.getModel()).remove(index);
							}});
						}else{
							// TODO: nice message to the user...
						}
					}
				}.start();
			}
		}
		
		public void valueChanged(ListSelectionEvent lse){
			JList src = (JList) lse.getSource();
			
			int[] indices = src.getSelectedIndices();
			if(indices.length==0){
				if(currentDict == null)return;
				setEnabled(false);
				currentIndex = -1;
				currentDict = null;
			}else{
				currentIndex = indices[0];
				currentDict = (Dictionary)((DefaultListModel)src.getModel()).elementAt(currentIndex);
				setEnabled(true);
				listAvailable.clearSelection();
			}
		}
	}
	

	private class ProgressObs extends JDialog implements ProgressObserver
	{
		//{{{ ProgressObs constructor
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
			progress.setString(jEdit.getProperty("spell-check-hunspell-download"));
	
	
			progress.setMaximum(1);
			content.add(BorderLayout.NORTH,progress);
	
			stop = new JButton(jEdit.getProperty("options.spellcheck.hunspell.stop"));
			stop.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
						ProgressObs.this.thread.stop();
						setVisible(false);
				}});
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
		 * @param value the new value
		 */
		public void setValue(final int value)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					progress.setValue(valueSoFar + value);
				}
			});
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
}
