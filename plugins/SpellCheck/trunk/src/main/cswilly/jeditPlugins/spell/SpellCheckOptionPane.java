/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2001 C. Scott Willy
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.File;
import java.util.*;

import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.util.Log;

/*
import org.gjt.sp.util.WorkRequest;
import org.gjt.sp.jedit.io.VFSManager;
*/
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


import cswilly.spell.SpellException;
import cswilly.spell.FutureListDicts;
import cswilly.spell.FutureListModes;
import cswilly.spell.EngineManager;

import static cswilly.jeditPlugins.spell.SpellCheckPlugin.*;

public class SpellCheckOptionPane
  extends AbstractOptionPane
{
	static final String LISTING = "spell-check.listing.combo-item";
	static final String REFRESH_BUTTON_START= "options.SpellCheck.refresh-langs";
	static final String REFRESH_BUTTON_STOP= "options.SpellCheck.stop-refresh-langs";
	static final String BROWSE= "browse";

	static final String NO_DICTIONARY="options.SpellCheck.no-dictionary";

	//the properties in this Option Pane
	private PropertyStore propertyStore;

  private static final int ASPELL_OPTION_VERTICAL_STRUT  = 7;

  public SpellCheckOptionPane()
  {
    super( SpellCheckPlugin.PLUGIN_NAME );
  }

  public void _init()
  {

	/* Properties */
	propertyStore = new PropertyStore(this);

	String engineManager = jEdit.getProperty(ENGINE_MANAGER_PROP);
	propertyStore.put(ENGINE_MANAGER_PROP,engineManager);

	String lang = SpellCheckPlugin.getMainLanguage();
	propertyStore.put(MAIN_LANGUAGE_PROP,lang);
	
	boolean spellOnSave = jEdit.getBooleanProperty( SPELLCHECK_ON_SAVE_PROP);
	propertyStore.put(SPELLCHECK_ON_SAVE_PROP,String.valueOf(spellOnSave));
	
	/* engine manager to use */
	String[] engineManagers = ServiceManager.getServiceNames(EngineManager.class.getName());
	
	final JComboBox engineManagerBox = new JComboBox(engineManagers);
	engineManagerBox.setSelectedItem(engineManager);
	engineManagerBox.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String itm = (String)engineManagerBox.getSelectedItem();
					if(itm==null)return;
					propertyStore.put(ENGINE_MANAGER_PROP,itm);
				}
		});

	addComponent(jEdit.getProperty( "options.SpellCheck.engineManager" ),
				 engineManagerBox);
	
    /* aspell main lang dictionary */
    JLabel _aspellMainLanguageLabel = new JLabel();
    _aspellMainLanguageLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellLang" ) );

    addComponent( _aspellMainLanguageLabel );

    JPanel listingPanel = new JPanel( new BorderLayout( 5, 0 ) );


	DictionaryPicker picker = new DictionaryPicker(SpellCheckPlugin.getEngineManager(),lang);
	
	// TODO: add listener on EngineManager change
	//propertyStore.addPropertyChangeListener(ASPELL_EXE_PROP,actionRefresh);


	JComboBox aspellMainLanguageList = picker.asComboBox();
	
	PropertyStore pickerStore  = picker.getPropertyStore();
	
	final Action actionRefresh = picker.getRefreshAction();
	
	pickerStore.addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent pce){
				if(DictionaryPicker.LANG_PROP.equals(pce.getPropertyName())){
					propertyStore.put(MAIN_LANGUAGE_PROP,(String)pce.getNewValue());
				}
			}
	});
	
	listingPanel.add( aspellMainLanguageList , BorderLayout.WEST );

    JButton refreshButton = new JButton(actionRefresh);
	refreshButton.setName("Refresh");
	listingPanel.add( refreshButton, BorderLayout.EAST );

	addComponent(listingPanel);
    addComponent(Box.createVerticalStrut( ASPELL_OPTION_VERTICAL_STRUT ));

	/* spell-check on save */
	final JCheckBox spellOnSaveBox = new JCheckBox();
	spellOnSaveBox.setName("SpellCheckOnSave");
	spellOnSaveBox.setText( jEdit.getProperty( "options.SpellCheck.spellcheckOnSave" ));
	spellOnSaveBox.setToolTipText( jEdit.getProperty( "options.SpellCheck.spellcheckOnSave.tooltip" ));
	spellOnSaveBox.setSelected(spellOnSave);
	spellOnSaveBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				propertyStore.put(SPELLCHECK_ON_SAVE_PROP,
					String.valueOf(ItemEvent.SELECTED == e.getStateChange()));
			}
	});
	addComponent(spellOnSaveBox);
	
	
	
	/* trigger a refresh */
	/* we run when the dialog is shown to prevent a bug when
	   dictionnary listing exit quickly with an error,
	   so refreshList() displays an Error Dialog before the OptionPane is
	   visible, so the Option Pane is on top of it, and we can close neither of them. 
	 */
	 /* DOESN'T WORK !
	addComponentListener(
		 new ComponentAdapter(){
			public void componentHidden(ComponentEvent ce){
					Log.log(Log.ERROR,SpellCheckOptionPane.this,"Hidden");
			}
			public void componentShown(ComponentEvent ce){
					Log.log(Log.DEBUG,this,"Hello");
					actionRefresh.actionPerformed(null);
					SpellCheckOptionPane.this.removeComponentListener(this);
			}
		});
	*/
	SwingUtilities.invokeLater(new Runnable(){
			public void run(){
					actionRefresh.actionPerformed(null);
			}
	});
  }

  public void _save()
  {

	String lang = propertyStore.get(MAIN_LANGUAGE_PROP);
	if(jEdit.getProperty(NO_DICTIONARY).equals(lang))lang="";
		jEdit.setProperty( MAIN_LANGUAGE_PROP, lang);
	
	jEdit.setProperty( ENGINE_MANAGER_PROP,  propertyStore.get(ENGINE_MANAGER_PROP) );
	jEdit.setProperty( SPELLCHECK_ON_SAVE_PROP,  propertyStore.get(SPELLCHECK_ON_SAVE_PROP) );
  }

}