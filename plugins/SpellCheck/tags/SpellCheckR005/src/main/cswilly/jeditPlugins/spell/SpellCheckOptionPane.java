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

import static cswilly.jeditPlugins.spell.SpellCheckPlugin.*;

public class SpellCheckOptionPane
  extends AbstractOptionPane
{
	static final String LISTING_DICTS = "spell-check.listing-dicts.combo-item";
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

    String aspellExecutable = SpellCheckPlugin.getAspellExeFilename();
	propertyStore.put(ASPELL_EXE_PROP,aspellExecutable);
	
	String lang = SpellCheckPlugin.getAspellMainLanguage();
	propertyStore.put(ASPELL_LANG_PROP,lang);
	
	AspellMarkupMode modeValue = AspellMarkupMode.AUTO_MARKUP_MODE;
	try{
		modeValue = AspellMarkupMode.fromString(jEdit.getProperty( ASPELL_MARKUP_MODE_PROP, ""));
	}catch(IllegalArgumentException iae){}
	propertyStore.put(ASPELL_MARKUP_MODE_PROP,modeValue.toString());
	
	String otherParams = jEdit.getProperty( ASPELL_OTHER_PARAMS_PROP,"" );
	propertyStore.put(ASPELL_OTHER_PARAMS_PROP,otherParams);
	
	boolean spellOnSave = jEdit.getBooleanProperty( SPELLCHECK_ON_SAVE_PROP);
	propertyStore.put(SPELLCHECK_ON_SAVE_PROP,String.valueOf(spellOnSave));
	
    /* aspell executable */
    JLabel _aspellExeFilenameLabel = new JLabel();
    _aspellExeFilenameLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellExe" ) );

    addComponent( _aspellExeFilenameLabel );

    final JTextField _aspellExeFilenameField = new JTextField( 25 );
    _aspellExeFilenameField.setText( aspellExecutable );
	_aspellExeFilenameField.setName("AspellPath");

	TextFieldHandler exeHandler = new TextFieldHandler(ASPELL_EXE_PROP);
	_aspellExeFilenameField.addFocusListener(exeHandler);
	_aspellExeFilenameField.addActionListener(exeHandler);
	
	//synchronize file chooser and text-field via the property
	propertyStore.addPropertyChangeListener(ASPELL_EXE_PROP, new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt){
				_aspellExeFilenameField.setText((String)evt.getNewValue());
			}
	});
	/* file chooser */
    JButton _fileChooser = new JButton( jEdit.getProperty( "options.SpellCheck.fileChooser" ) );
    _fileChooser.addActionListener(new FileActionHandler());
	_fileChooser.setActionCommand(BROWSE);
	_fileChooser.setName("Browse");
    JPanel _filePanel = new JPanel( new BorderLayout( 5, 0 ) );
    _filePanel.add( _aspellExeFilenameField, BorderLayout.WEST );
    _filePanel.add( _fileChooser, BorderLayout.EAST );

    addComponent( _filePanel );

    addComponent(Box.createVerticalStrut( ASPELL_OPTION_VERTICAL_STRUT ));

    /* aspell main lang dictionary */
    JLabel _aspellMainLanguageLabel = new JLabel();
    _aspellMainLanguageLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellLang" ) );

    addComponent( _aspellMainLanguageLabel );

    JPanel listingPanel = new JPanel( new BorderLayout( 5, 0 ) );

	MutableComboBoxModel model = new DefaultComboBoxModel();
	MutableComboBoxModel model2 = new DefaultComboBoxModel();
	
	final AspellCapabilitiesAction actionRefresh = new AspellCapabilitiesAction(model,model2);
	propertyStore.addPropertyChangeListener(ASPELL_EXE_PROP,actionRefresh);


	final JComboBox _aspellMainLanguageList = new JComboBox( model );
	_aspellMainLanguageList.setEditable( true );	
	_aspellMainLanguageList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String dict = (String)_aspellMainLanguageList.getSelectedItem();
				if(dict==null)return;
				propertyStore.put(ASPELL_LANG_PROP,dict);
				Log.log(Log.DEBUG,SpellCheckOptionPane.this,"changed : "+dict);
			}
	});
	
	listingPanel.add( _aspellMainLanguageList , BorderLayout.WEST );

    JButton _refreshButton = new JButton(actionRefresh);
	_refreshButton.setName("Refresh");
	listingPanel.add( _refreshButton, BorderLayout.EAST );

	addComponent(listingPanel);
    addComponent(Box.createVerticalStrut( ASPELL_OPTION_VERTICAL_STRUT ));

    /* aspell markup mode */
    JLabel _aspellMarkupModeLabel = new JLabel();
    _aspellMarkupModeLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellMarkupMode" ) );

    addComponent( _aspellMarkupModeLabel );
	
    ButtonGroup _markupModeGroup = new ButtonGroup();
    JPanel _markupModePanel = new JPanel( new BorderLayout( 5, 0 ) );

	final EnumMap<AspellMarkupMode,String> positions = new EnumMap<AspellMarkupMode,String>(AspellMarkupMode.class);
	positions.put(AspellMarkupMode.NO_MARKUP_MODE,BorderLayout.NORTH);
	positions.put(AspellMarkupMode.AUTO_MARKUP_MODE,BorderLayout.CENTER);
	positions.put(AspellMarkupMode.MANUAL_MARKUP_MODE,BorderLayout.SOUTH);
	
	for(final SpellCheckPlugin.AspellMarkupMode mode : SpellCheckPlugin.AspellMarkupMode.values()){
		JRadioButton _aspellMarkupMode = new JRadioButton();
		_aspellMarkupMode.setName( mode.toString());
		_aspellMarkupMode.setText( jEdit.getProperty( "options.SpellCheck."+mode.toString() ) );
		_aspellMarkupMode.setToolTipText( jEdit.getProperty( "options.SpellCheck."+mode.toString()+".tooltip" ) );
		_aspellMarkupMode.setSelected( mode == modeValue);
		_aspellMarkupMode.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				    if (e.getStateChange() == ItemEvent.SELECTED) {
						propertyStore.put(ASPELL_MARKUP_MODE_PROP,mode.toString());
					}
			}
		});
		_markupModeGroup.add( _aspellMarkupMode );
		_markupModePanel.add( _aspellMarkupMode, positions.get(mode) );
	}


    addComponent( _markupModePanel );

    addComponent( createModesSelector(model2) );

    addComponent(Box.createVerticalStrut( ASPELL_OPTION_VERTICAL_STRUT ));

    /* aspell other parameters */
    JLabel _aspellOtherParamsLabel = new JLabel();
    _aspellOtherParamsLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellOtherParams" ) );

    addComponent( _aspellOtherParamsLabel );

    final JTextField _aspellOtherParamsField = new JTextField( 25 );
	_aspellOtherParamsField.setName("AdditionalParameters");
	_aspellOtherParamsField.setText( otherParams );
	TextFieldHandler otherParamsHandler = new TextFieldHandler(ASPELL_OTHER_PARAMS_PROP);
	_aspellOtherParamsField.addActionListener(otherParamsHandler);
	_aspellOtherParamsField.addFocusListener(otherParamsHandler);
    addComponent( _aspellOtherParamsField );
	
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
    jEdit.setProperty( ASPELL_EXE_PROP, propertyStore.get(ASPELL_EXE_PROP) );

	String lang = propertyStore.get(ASPELL_LANG_PROP);
	if(jEdit.getProperty(NO_DICTIONARY).equals(lang))lang="";
		jEdit.setProperty( ASPELL_LANG_PROP, lang);
	
    jEdit.setProperty( ASPELL_MARKUP_MODE_PROP, propertyStore.get(ASPELL_MARKUP_MODE_PROP));
    jEdit.setProperty( ASPELL_OTHER_PARAMS_PROP, propertyStore.get(ASPELL_OTHER_PARAMS_PROP) );
	jEdit.setProperty( SPELLCHECK_ON_SAVE_PROP,  propertyStore.get(SPELLCHECK_ON_SAVE_PROP) );
    model.save();
  }

  private ModeTableModel model;

  
  private JScrollPane createModesSelector(final MutableComboBoxModel comboModel)
  {
    model = new ModeTableModel();
    JTable table = new JTable(model);
	table.setName("filtersTable");
    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setRowSelectionAllowed(false);
    table.setCellSelectionEnabled(false);

	for(String filter: model.getUsedFilters())comboModel.addElement(filter);
	final JComboBox comboBox = new JComboBox(comboModel);
	comboBox.setName("filtersCombo");
    table.setRowHeight(comboBox.getPreferredSize().height);
    table.getColumnModel().getColumn(0).setPreferredWidth(200);
    TableColumn column = table.getColumnModel().getColumn(1);

    column.setCellEditor(new DefaultCellEditor(comboBox));
    column.setPreferredWidth(comboBox.getPreferredSize().width);

    Dimension d = table.getPreferredSize();
    d.height = Math.min(d.height,200);
    JScrollPane scroller = new JScrollPane(table);
    scroller.setPreferredSize(d);
		
    return scroller;
  }

    
  private class TextFieldHandler extends FocusAdapter implements ActionListener{
	  private String propName;
	  
	  
	  TextFieldHandler(String propName){
		  this.propName = propName;
	  }
	  
	  public void actionPerformed( ActionEvent evt )
	  {
		  String value = ((JTextComponent)evt.getSource()).getText().trim();
		  propertyStore.put(propName,value);
	  }
	  
	  public void focusLost(FocusEvent e){
		  String value = ((JTextComponent)e.getSource()).getText().trim();
		  propertyStore.put(propName,value);
	  }
  }
  
  private class FileActionHandler implements ActionListener
  {
    public void actionPerformed( ActionEvent evt )
    {
			//It's better to show last one, not saved one !
			String initialPath = propertyStore.get( ASPELL_EXE_PROP );
			
			String[] paths = GUIUtilities.showVFSFileDialog( null, initialPath, JFileChooser.OPEN_DIALOG, false );
			
			if ( paths != null ){
				propertyStore.put(ASPELL_EXE_PROP,paths[0]);
			}

    }	
  }
  
  private class AspellCapabilitiesAction extends AbstractAction implements PropertyChangeListener{
	  private MutableComboBoxModel modelDicts;
	  private MutableComboBoxModel modelFilters;
	  
	  private Future<Vector<String>> futureDicts;
	  private Future<Map<String,String>> futureModes;
	  
	  AspellCapabilitiesAction(MutableComboBoxModel modelDicts, MutableComboBoxModel modelFilters){
		  super(jEdit.getProperty(REFRESH_BUTTON_START));
		  this.modelDicts=modelDicts;
		  this.modelFilters=modelFilters;
		  setEnabled(false);
	  }

	public void actionPerformed( ActionEvent evt )
    {
		if(futureDicts == null && futureModes==null){
			refreshList();
		}else{
			setEnabled(false);//disable while threads cleanup
			if(futureDicts!=null)futureDicts.cancel(true);
			futureDicts = null;
			if(futureModes!=null)futureModes.cancel(true);
			futureModes = null;
		}
	}

	public void propertyChange(PropertyChangeEvent e){
		if(ASPELL_EXE_PROP.equals(e.getPropertyName())){
			//when Aspell Exe Path changes
			if(!isEnabled()){
				setEnabled(true);
			}
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
			putValue(Action.NAME,jEdit.getProperty(REFRESH_BUTTON_STOP));
			String listing = jEdit.getProperty( LISTING_DICTS, "" );
			modelDicts.addElement(listing);
			modelDicts.setSelectedItem(listing);
			final Vector<String> dicts = new Vector<String>();
			final Map<String,String> filters = new HashMap<String,String>();
			
			
			FutureListModes ftModes = new FutureListModes(propertyStore.get(ASPELL_EXE_PROP));
			FutureListDicts ft = new FutureListDicts(propertyStore.get(ASPELL_EXE_PROP));
			futureModes = ftModes;
			//have a stable state, but give a reference to the outside, so it is able to cancel.
			futureDicts = ft;

		
			setEnabled(true);
			

			try
			{
				dicts.addAll(ft.get(10,TimeUnit.SECONDS));
				filters.putAll(ftModes.get(10,TimeUnit.SECONDS));
			}
			catch(InterruptedException ie){
				Log.log(Log.ERROR,SpellCheckOptionPane.this,ie);
				GUIUtilities.error(SpellCheckOptionPane.this,
					"ioerror",new String[] { "Interrupted while listing dictionaries" });
				ft.cancel(true);
				ftModes.cancel(true);
			}
			catch(CancellationException ce){
				Log.log(Log.DEBUG,SpellCheckOptionPane.this,"Cancel Button worked");
			}
			catch(ExecutionException ee){
				Log.log(Log.ERROR,SpellCheckOptionPane.this,ee);
				GUIUtilities.error(SpellCheckOptionPane.this,
					"ioerror",new String[] { jEdit.getProperty("list-dict-error.message",new Object[]{ee.getCause().getMessage()}) });
				ft.cancel(true);
				ftModes.cancel(true);
			}
			catch(TimeoutException te){
				Log.log(Log.ERROR,SpellCheckOptionPane.this,te);
				GUIUtilities.error(SpellCheckOptionPane.this,
					"ioerror",new String[] { jEdit.getProperty("list-dict-timeout-error.message") });
				ft.cancel(true);
				ftModes.cancel(true);
			}

			if(!dicts.isEmpty())
				Log.log(Log.DEBUG,SpellCheckOptionPane.this,"finished refreshList ("+dicts.size()+" dictionaries found)");
			else
				Log.log(Log.ERROR,SpellCheckOptionPane.this,"no dictionary found)");
			if(!filters.isEmpty())
				Log.log(Log.DEBUG,SpellCheckOptionPane.this,"finished refreshList ("+filters.size()+" filters found)");
			else
				Log.log(Log.ERROR,SpellCheckOptionPane.this,"no filter found)");

			putValue(Action.NAME,jEdit.getProperty(REFRESH_BUTTON_START));
			setEnabled(true);
			futureModes = null;
			futureDicts = null;
			updateModels(dicts,filters);
			}
		}.start();
	}
	
	
	private void updateModels(final java.util.List<String> dicts, final Map<String,String> modes){
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if(!dicts.isEmpty())
					{
						while(modelFilters.getSize()!=0)modelFilters.removeElementAt(0);//remove old modes
						for(String mode: modes.keySet())modelFilters.addElement(mode);
						modelFilters.addElement(SpellCheckPlugin.FILTER_AUTO);
					}

					while(modelDicts.getSize()!=0)modelDicts.removeElementAt(0);//remove listing...
					for(int i=0;i<dicts.size();i++)modelDicts.addElement(dicts.get(i));
					if(dicts.isEmpty()){
						String nothing = jEdit.getProperty( NO_DICTIONARY, "" );
						modelDicts.addElement(nothing);
						modelDicts.setSelectedItem(nothing);
					}else{
						String dict = jEdit.getProperty(SpellCheckPlugin.ASPELL_LANG_PROP);
						if(dicts.contains(dict))modelDicts.setSelectedItem(dict);
					}
		
				}
			});
		}
  }

}

class ModeTableModel extends AbstractTableModel
{
  private ArrayList modes;
  private java.util.List<String> usedFilters;
  ModeTableModel()
  {
    Mode[] _modes = jEdit.getModes();

    modes = new ArrayList(_modes.length);
	usedFilters = new ArrayList(10);
    for(int i = 0; i < _modes.length; i++)
    {
      Entry e = new Entry(_modes[i].getName());
	  modes.add(e);
	  if(!usedFilters.contains(e.filter))usedFilters.add(e.filter);
    }
  }

  public int getColumnCount()
  {
    return 2;
  }

  public int getRowCount()
  {
    return modes.size();
  }

  public Class getColumnClass(int col)
  {
    return String.class;
  }

  public Object getValueAt(int row, int col)
  {
    Entry mode = (Entry)modes.get(row);
    switch(col)
    {
    case 0:
      return mode.name;
    case 1:
      return mode.filter;
    default:
      throw new InternalError();
    }
  }

  public boolean isCellEditable(int row, int col)
  {
    return (col != 0);
  }

  public void setValueAt(Object value, int row, int col)
  {
    if(col == 0)
      return;

    Entry mode = (Entry)modes.get(row);
    switch(col)
    {
    case 1:
      mode.filter = (String)value;
      break;
    default:
      throw new InternalError();
    }

    fireTableRowsUpdated(row,row);
  }

  public String getColumnName(int index)
  {
    switch(index)
    {
    case 0:
      return null; // jEdit.getProperty("options.SpellCheck.aspellAutoMarkupMode.name");
    case 1:
      return null; // jEdit.getProperty("options.SpellCheck.aspellAutoMarkupMode.isSelected");
    default:
      throw new InternalError();
    }
  }

  public java.util.List<String> getUsedFilters(){
	  return usedFilters;
  }
  
  public void save()
  {
    for(int i = 0; i < modes.size(); i++)
    {
      ((Entry)modes.get(i)).save();
    }
  }
}

class Entry
{
  String name;
  String filter;
  Entry( String name )
  {
    this.name = name;
    filter = jEdit.getProperty(SpellCheckPlugin.FILTERS_PROP+"."+name,SpellCheckPlugin.FILTER_AUTO);
  }

  void save()
  {
	  if(!SpellCheckPlugin.FILTER_AUTO.equals(filter))
      jEdit.setProperty(SpellCheckPlugin.FILTERS_PROP+"."+name, filter);
    else
      jEdit.unsetProperty(SpellCheckPlugin.FILTERS_PROP+"."+name);
  }
  
}

class PropertyStore extends PropertyChangeSupport{
	private Map<String,String> values;
	PropertyStore(Object source){
		super(source);
		values = new HashMap<String,String>();
	}
	
	public void put(String name,String value){
		if(name == null)throw new IllegalArgumentException("property name shouldn't be null");
		if(value == null)throw new IllegalArgumentException("property value shouldn't be null");
		if(values.containsKey(name) && values.get(name).equals(value))return;//ignore
		firePropertyChange(name,values.get(name),value);
		values.put(name,value);
	}
	
	public String get(String name){
		return values.get(name);
	}
}