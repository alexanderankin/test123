/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2008 Eric Le Lay
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

package cswilly.jeditPlugins.spell;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.io.File;
import java.util.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


import cswilly.spell.SpellException;
import cswilly.spell.FutureListDicts;

import static cswilly.jeditPlugins.spell.SpellCheckPlugin.*;


/**
 * encapsulates dictionnary selection.
 * pilot via refresh Action
**/
public class DictionaryPicker{
	static final String LISTING_DICTS = "spell-check.listing-dicts.combo-item";
	static final String REFRESH_BUTTON_START= "options.SpellCheck.refresh-langs";
	static final String REFRESH_BUTTON_STOP= "options.SpellCheck.stop-refresh-langs";
	static final String NO_DICTIONARY="options.SpellCheck.no-dictionary";

	private static final String INITIAL_LANG_PROP = "dict-picker-initial-lang";
	public static final String LANG_PROP = "dict-picker-lang";

	private MutableComboBoxModel modelDicts;
	private Future<Vector<String>> futureDicts;
	private Action refreshAction;
	
	private PropertyStore propertyStore;
	
	public DictionaryPicker(String initial){
		modelDicts= new DefaultComboBoxModel();
		futureDicts = null;
		refreshAction = null;
		propertyStore = new PropertyStore(this);
		propertyStore.put(INITIAL_LANG_PROP,initial);
	}
	
	public JComboBox asComboBox(){
		final JComboBox _aspellMainLanguageList = new JComboBox( modelDicts );
		_aspellMainLanguageList.setEditable( true );	
		_aspellMainLanguageList.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String dict = (String)_aspellMainLanguageList.getSelectedItem();
					if(dict==null)return;
					propertyStore.put(LANG_PROP,dict);
					Log.log(Log.DEBUG,DictionaryPicker.this,"changed : "+dict);
				}
		});
		return _aspellMainLanguageList;
	}
	
	public PropertyStore getPropertyStore(){
		return propertyStore;
	}
	
	private class RefreshAction extends AbstractAction{
		RefreshAction(){
			setEnabled(false);
		}
		
		public void actionPerformed( ActionEvent evt )
		{
			if(futureDicts == null)
			{
				refreshList();
			}
			else
			{
				cancel();
			}
		}
	}
	
	public Action getRefreshAction(){
		if(refreshAction==null) refreshAction = new RefreshAction();
		return refreshAction;
	}
	
	public void cancel(){
		if(futureDicts!=null)
		{
			getRefreshAction().setEnabled(false);//disable while threads cleanup
			futureDicts.cancel(true);
		}
	}
	
	/**
	* executes a FutureListDicts in a new Thread.
	* finally, update the combo model to contain all found dictionnaries
	* the futureDicts variable serves both to cancel in response to user action on Stop! button
	* and to flag that some work is done (while it's not null).
	* The aspell executable used is the one currently configured, even if not applied yet.
	**/
	private void refreshList(){
		new Thread(){
			public void run(){
				refreshAction.putValue(Action.NAME,jEdit.getProperty(REFRESH_BUTTON_STOP));
				String listing = jEdit.getProperty( LISTING_DICTS, "" );
				modelDicts.addElement(listing);
				modelDicts.setSelectedItem(listing);
				final Vector<String> dicts = new Vector<String>();
				
				getRefreshAction().setEnabled(true);
				
				FutureListDicts ft = new FutureListDicts(propertyStore.get(ASPELL_EXE_PROP));
				//have a stable state, but give a reference to the outside, so it is able to cancel.
				futureDicts = ft;
				try
				{
					dicts.addAll(ft.get(10,TimeUnit.SECONDS));
				}
				catch(InterruptedException ie){
					Log.log(Log.ERROR,DictionaryPicker.this,ie);
					GUIUtilities.error(null,
						"ioerror",new String[] { "Interrupted while listing dictionaries" });
					ft.cancel(true);
				}
				catch(CancellationException ce){
					Log.log(Log.DEBUG,DictionaryPicker.this,"Cancel Button worked");
				}
				catch(ExecutionException ee){
					Log.log(Log.ERROR,DictionaryPicker.this,ee);
					GUIUtilities.error(null,
						"ioerror",new String[] { jEdit.getProperty("list-dict-error.message",new Object[]{ee.getCause().getMessage()}) });
					ft.cancel(true);
				}
				catch(TimeoutException te){
					Log.log(Log.ERROR,DictionaryPicker.this,te);
					GUIUtilities.error(null,
						"ioerror",new String[] { jEdit.getProperty("list-dict-timeout-error.message") });
					ft.cancel(true);
				}
				
				SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							if(!dicts.isEmpty())
								Log.log(Log.DEBUG,DictionaryPicker.this,"finished refreshList ("+dicts.size()+" dictionaries found)");
							else
								Log.log(Log.ERROR,DictionaryPicker.this,"no dictionary found)");
							while(modelDicts.getSize()!=0)modelDicts.removeElementAt(0);//remove listing...
							for(int i=0;i<dicts.size();i++)modelDicts.addElement(dicts.get(i));
							if(dicts.isEmpty()){
								String nothing = jEdit.getProperty( NO_DICTIONARY, "" );
								modelDicts.addElement(nothing);
								modelDicts.setSelectedItem(nothing);
							}else{
								String dict = propertyStore.get(INITIAL_LANG_PROP);
								Log.log(Log.DEBUG,DictionaryPicker.this,"dict was "+dict);
								if(dicts.contains(dict))modelDicts.setSelectedItem(dict);
							}
							
							getRefreshAction().putValue(Action.NAME,jEdit.getProperty(REFRESH_BUTTON_START));
							getRefreshAction().setEnabled(true);
							//we are done!
							futureDicts = null;
						}
					});
		}}.start();
	}
}
