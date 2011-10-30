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
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Vector;

import java.util.regex.Pattern;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import java.awt.Font;
import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.Action;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.awt.Dialog;

import javax.swing.SwingUtilities;
import javax.swing.JLabel;

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
	
	private FileTextField externalLibField;
	
	public HunspellOptionPane(){
		super("spellcheck.hunspell");
		HunspellEngineManager engMan = (HunspellEngineManager)ServiceManager.getService(EngineManager.class.getName(), "Hunspell");
		assert(engMan!=null);
		dictsManager = engMan.getDictsManager();
	}
	
	protected void _init(){
		
		//external library
		String externalLib = jEdit.getProperty(HunspellEngineManager.HUNSPELL_LIBRARY_PROP,"");

		JLabel lbl = new JLabel(jEdit.getProperty("options.spellcheck.hunspell.external-lib"));
		addComponent(lbl);
		externalLibField = new FileTextField(externalLib,true);
		externalLibField.getTextField().setColumns(50);
		addComponent(externalLibField);
		
		
		//installed/availables
		final SortedListModel dlm = new SortedListModel();
		List<Dictionary> dicts = dictsManager.getInstalled();
		for(Dictionary d : dicts)dlm.add(d);
		
		listInstalled = new JList(dlm);
		JScrollPane pi = new JScrollPane(listInstalled);
		addSeparator("options.spellcheck.hunspell.installed");
		addComponent(pi);
		
		final SortedListModel dlma = new SortedListModel();
		
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
		listInstalled.setCellRenderer(new MyCellRenderer());

		
		JPanel p = new JPanel();
		JButton bout = new JButton(install);
		bout.setName("install");
		p.add(bout);

		bout = new JButton(remove);
		bout.setName("remove");
		p.add(bout);

		addComponent(p);
		
		//offline installation
		addSeparator("options.spellcheck.hunspell.offline");
		String url = jEdit.getProperty(HunspellDictsManager.OOO_DICTS_PROP);
		String offlineMsg = jEdit.getProperty("options.spellcheck.hunspell.offline.msg",new Object[]{url});
		JTextArea offlineTF = new JTextArea(offlineMsg);
		offlineTF.setEditable(false);
		JButton offlineButton = new JButton(new InstallOfflineAction());
		addComponent(offlineButton);
		addComponent(offlineTF);
		
		new Thread(){
			public void run(){
				ProgressObs po = new ProgressObs(this);
				JComponent poComp = po.asComponent(
						jEdit.getProperty("options.spellcheck.hunspell.fetch-availables.title")
					);
				HunspellOptionPane.this.addComponent(poComp);
				HunspellOptionPane.this.revalidate();
				Log.log(Log.DEBUG,HunspellOptionPane.class,"Refresh availables started");
				List<Dictionary> av = dictsManager.getAvailables(po);
				poComp.setVisible(false);
				for(Dictionary d : av)dlma.add(d);
				Log.log(Log.DEBUG,HunspellOptionPane.class,"Refresh availables done");
			}
		}.start();
		
	}
	protected void _save(){
		String newLib = externalLibField.getTextField().getText();
		if("".equals(newLib))jEdit.unsetProperty(HunspellEngineManager.HUNSPELL_LIBRARY_PROP);
		else jEdit.setProperty(HunspellEngineManager.HUNSPELL_LIBRARY_PROP,newLib);
	}
	

	private class InstallOfflineAction extends AbstractAction{
		InstallOfflineAction(){
			super(jEdit.getProperty("options.spellcheck.hunspell.install-offline"));
		}
		public void actionPerformed(ActionEvent ae){
			new Thread(){
				public void run(){
					//select file
					JFileChooser chooser = new JFileChooser();
					javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter(){
						public boolean accept(File f){return f.isDirectory() || f.getName().endsWith(".zip");}
						public String getDescription(){return "dictionary archive (*.zip)";}
					};
					chooser.addChoosableFileFilter(filter);
					int returnVal = chooser.showOpenDialog(GUIUtilities.getParentDialog(listAvailable));
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						File archive = chooser.getSelectedFile();
						
						//install
						ProgressObs po = new ProgressObs(this);
						JDialog poDialog = po.asDialog(
							GUIUtilities.getParentDialog(listAvailable),
							jEdit.getProperty("options.spellcheck.hunspell.install-offline")
						);

						Dictionary installed = dictsManager.installOffline(archive,po);
						poDialog.setVisible(false);
						if(installed==null){
							GUIUtilities.error(
								GUIUtilities.getParentDialog(listAvailable),
								"spell-check-hunspell-install-failed",
								new String[]{});
						}else{
							((SortedListModel)listInstalled.getModel()).add(installed);

						}
					}
				}
			}.start();
		}
	}

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
						ProgressObs po = new ProgressObs(this);
						JDialog poDialog = po.asDialog(
							GUIUtilities.getParentDialog(listAvailable),
							jEdit.getProperty("options.spellcheck.hunspell.install-dict.title",new String[]{dict.getDescription()})
						);
						if(update){
							boolean updated = dictsManager.update(dict,po);
							poDialog.setVisible(false);
							if(!updated){
								GUIUtilities.error(
									GUIUtilities.getParentDialog(listAvailable),
									(dict.isInstalled()?
										"spell-check-hunspell-update-failed.old"
										:"spell-check-hunspell-update-failed.broken"),
									new String[]{dict.toString()});
							}
							listInstalled.repaint();
						}else{
							boolean installed  = dictsManager.install(dict,po);
							poDialog.setVisible(false);
							if(installed){
								SwingUtilities.invokeLater(new Runnable(){
										public void run(){
											((SortedListModel)listInstalled.getModel()).add(dict);
											((SortedListModel)listAvailable.getModel()).remove(index);
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
				currentDict = (Dictionary)src.getModel().getElementAt(currentIndex);
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
										((SortedListModel)listAvailable.getModel()).add(dict);
										((SortedListModel)listInstalled.getModel()).remove(index);
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
				currentDict = (Dictionary)src.getModel().getElementAt(currentIndex);
				setEnabled(true);
				listAvailable.clearSelection();
			}
		}
	}
	
	private class MyCellRenderer extends DefaultListCellRenderer {
		private Font outdatedFont;
		
		public Component getListCellRendererComponent(
			JList list,
			Object value,            // value to display
			int index,               // cell index
			boolean isSelected,      // is the cell selected
			boolean cellHasFocus)    // the list and the cell have the focus
		{
			if(outdatedFont==null){
				outdatedFont = list.getFont().deriveFont(Font.ITALIC|Font.BOLD);
			}
			
			Dictionary d = (Dictionary)value;
			String s = d.toString();
			if(d.isOutdated()){
				s = jEdit.getProperty("options.spellcheck.hunspell.outdated-label",new String[]{s});
				super.getListCellRendererComponent(list,s,index,isSelected,cellHasFocus);
				setFont(outdatedFont);
			}else{
				super.getListCellRendererComponent(list,s,index,isSelected,cellHasFocus);
			}
			
			return this;
		}
	}
	/**
	 * keeps data in a sorted set, to restore elements at their right place
	 */
	private class SortedListModel extends AbstractListModel{
		private List<Dictionary> data;
		SortedListModel(){
			data = new ArrayList<Dictionary>();
		}
		public void add(Dictionary d){
			for(ListIterator<Dictionary> it=data.listIterator();it.hasNext();){
				int cmp = d.compareTo(it.next());
				if(cmp<0){
					it.previous();
					it.add(d);
					fireIntervalAdded(this,it.previousIndex(),it.previousIndex());
					return;
				}
			}
			data.add(d);
			fireIntervalAdded(this,data.size()-1,data.size()-1);
		}
		public void remove(int index){
			if(index<0||index>=data.size())throw new IllegalArgumentException("invalid index:"+index);
			data.remove(index);
			fireIntervalRemoved(this,index,index);
		}
		
		public int getSize() { return data.size(); }
		public Object getElementAt(int index) { return data.get(index); }
	}
	

}
