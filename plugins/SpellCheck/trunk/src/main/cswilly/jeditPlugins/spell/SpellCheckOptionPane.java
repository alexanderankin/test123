/*
 * $Revision$
 * $Date$
 * $Author$
 *
 * Copyright (C) 2001 C. Scott Willy
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
import java.io.File;
import java.util.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.WorkRequest;
import org.gjt.sp.jedit.io.VFSManager;

import cswilly.spell.SpellException;

public class SpellCheckOptionPane
  extends AbstractOptionPane
{
	static final String LISTING_DICTS = "spell-check.listing-dicts.combo-item";
	static final String REFRESH_BUTTON= "options.SpellCheck.refresh-langs";
  private JTextField    _aspellExeFilenameField;
  private JComboBox     _aspellMainLanguageList;
  private JRadioButton  _aspellNoMarkupMode;
  private JRadioButton  _aspellManualMarkupMode;
  private JRadioButton  _aspellAutoMarkupMode;
  // private JTextField    _aspellMarkupModesField;
  private JTextField    _aspellOtherParamsField;

  private static final int ASPELL_OPTION_VERTICAL_STRUT  = 7;

  public SpellCheckOptionPane()
  {
    super( SpellCheckPlugin.PLUGIN_NAME );
  }

  public void _init()
  {
    Vector<String> values = null;
	
    /* aspell executable */
    JLabel _aspellExeFilenameLabel = new JLabel();
    _aspellExeFilenameLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellExe" ) );

    addComponent( _aspellExeFilenameLabel );

    String aspellExecutable = jEdit.getProperty( SpellCheckPlugin.ASPELL_EXE_PROP, "" );

    _aspellExeFilenameField = new JTextField( 25 );
    _aspellExeFilenameField.setText( aspellExecutable );

    JButton _fileChooser = new JButton( jEdit.getProperty( "options.SpellCheck.fileChooser" ) );
    _fileChooser.addActionListener( new FileActionHandler() );

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

	DefaultComboBoxModel model = new DefaultComboBoxModel();
	_aspellMainLanguageList = new JComboBox( model );
	_aspellMainLanguageList.setEditable( true );
	refreshList(model);
	listingPanel.add( _aspellMainLanguageList, BorderLayout.WEST );
	
    String  refresh = jEdit.getProperty( REFRESH_BUTTON, "" );
    JButton refreshButton = new JButton(refresh);
	refreshButton.addActionListener(new ListDictsActionHandler(model));
	listingPanel.add( refreshButton, BorderLayout.EAST );
	
	addComponent(listingPanel);

    addComponent(Box.createVerticalStrut( ASPELL_OPTION_VERTICAL_STRUT ));

    /* aspell markup mode */
    JLabel _aspellMarkupModeLabel = new JLabel();
    _aspellMarkupModeLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellMarkupMode" ) );

    addComponent( _aspellMarkupModeLabel );

    _aspellNoMarkupMode = new JRadioButton();
    _aspellNoMarkupMode.setText( jEdit.getProperty( "options.SpellCheck.aspellNoMarkupMode" ) );
    _aspellNoMarkupMode.setSelected( jEdit.getBooleanProperty( SpellCheckPlugin.ASPELL_NO_MARKUP_MODE, false) );

    _aspellManualMarkupMode = new JRadioButton();
    _aspellManualMarkupMode.setText( jEdit.getProperty( "options.SpellCheck.aspellManualMarkupMode" ) );
    _aspellManualMarkupMode.setSelected( jEdit.getBooleanProperty( SpellCheckPlugin.ASPELL_MANUAL_MARKUP_MODE, false) );

    _aspellAutoMarkupMode = new JRadioButton();
    _aspellAutoMarkupMode.setText( jEdit.getProperty( "options.SpellCheck.aspellAutoMarkupMode" ) );
    _aspellAutoMarkupMode.setSelected( jEdit.getBooleanProperty( SpellCheckPlugin.ASPELL_AUTO_MARKUP_MODE, true ) );

    ButtonGroup _markupModeGroup = new ButtonGroup();
    _markupModeGroup.add( _aspellNoMarkupMode );
    _markupModeGroup.add( _aspellManualMarkupMode );
    _markupModeGroup.add( _aspellAutoMarkupMode );

    JPanel _markupModePanel = new JPanel( new BorderLayout( 5, 0 ) );
    _markupModePanel.add( _aspellNoMarkupMode, BorderLayout.NORTH );
    _markupModePanel.add( _aspellManualMarkupMode, BorderLayout.CENTER );
    _markupModePanel.add( _aspellAutoMarkupMode, BorderLayout.SOUTH );

    addComponent( _markupModePanel );

    addComponent( createModesSelector() );

    // _aspellMarkupModesField = new JTextField( 25 );
    // _aspellMarkupModesField.setText( jEdit.getProperty( SpellCheckPlugin.ASPELL_MARKUP_MODES, "sgml, html, xml, xsl" ) );
    // _aspellMarkupModesField.setEditable(false);

    // addComponent( _aspellMarkupModesField );

    addComponent(Box.createVerticalStrut( ASPELL_OPTION_VERTICAL_STRUT ));

    /* aspell other parameters */
    JLabel _aspellOtherParamsLabel = new JLabel();
    _aspellOtherParamsLabel.setText( jEdit.getProperty( "options.SpellCheck.aspellOtherParams" ) );

    addComponent( _aspellOtherParamsLabel );

    _aspellOtherParamsField = new JTextField( 25 );
    _aspellOtherParamsField.setText( jEdit.getProperty( SpellCheckPlugin.ASPELL_OTHER_PARAMS, "" ) );

    addComponent( _aspellOtherParamsField );

  }

  public void _save()
  {
    jEdit.setProperty( SpellCheckPlugin.ASPELL_EXE_PROP, _aspellExeFilenameField.getText().trim() );
	jEdit.setProperty( SpellCheckPlugin.ASPELL_LANG_PROP, _aspellMainLanguageList.getSelectedItem().toString().trim() );
    jEdit.setBooleanProperty( SpellCheckPlugin.ASPELL_NO_MARKUP_MODE, _aspellNoMarkupMode.isSelected() );
    jEdit.setBooleanProperty( SpellCheckPlugin.ASPELL_MANUAL_MARKUP_MODE, _aspellManualMarkupMode.isSelected() );
    jEdit.setBooleanProperty( SpellCheckPlugin.ASPELL_AUTO_MARKUP_MODE, _aspellAutoMarkupMode.isSelected() );
    //jEdit.setProperty( SpellCheckPlugin.ASPELL_MARKUP_MODES, _aspellMarkupModesField.getText() );
    jEdit.setProperty( SpellCheckPlugin.ASPELL_OTHER_PARAMS, _aspellOtherParamsField.getText() );
    model.save();
  }

  private ModeTableModel model;

  private JScrollPane createModesSelector()
  {
    model = new ModeTableModel();
    JTable table = new JTable(model);
    table.getTableHeader().setReorderingAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setRowSelectionAllowed(false);
    table.setCellSelectionEnabled(false);

    CheckBoxCellRenderer checkBox = new CheckBoxCellRenderer();
    checkBox.setRequestFocusEnabled(false);
    table.setRowHeight(checkBox.getPreferredSize().height);
    table.getColumnModel().getColumn(0).setPreferredWidth(200);
    TableColumn column = table.getColumnModel().getColumn(1);
    column.setCellRenderer(checkBox);
    checkBox = new CheckBoxCellRenderer();
    checkBox.setRequestFocusEnabled(false);
    column.setCellEditor(new DefaultCellEditor(checkBox));
    column.setPreferredWidth(checkBox.getPreferredSize().width);

    Dimension d = table.getPreferredSize();
    d.height = Math.min(d.height,200);
    JScrollPane scroller = new JScrollPane(table);
    scroller.setPreferredSize(d);
    return scroller;
  }

	/**
	 * Trigger async call to SpellCheckPlugin.getAlternateLangDictionaries()
	 * in a WorkThread
	**/
	private void refreshList(MutableComboBoxModel model){
		String  listing = jEdit.getProperty( LISTING_DICTS, "" );
		model.addElement(listing);
		model.setSelectedItem(listing);
		VFSManager.runInWorkThread(new ListDictionnariesThread(model));
	}
  
  class CheckBoxCellRenderer extends JCheckBox
    implements TableCellRenderer
  {
    CheckBoxCellRenderer()
    {
      super();
    }

    public Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus,
      int row, int column)
    {
      Boolean val = (Boolean) value;
      setSelected(val.booleanValue());
      // Entry val = (Entry)value;
      // setSelected(val.isSelected.booleanValue());
      // setText(val.name);
      return this;
    }
  }

  private class FileActionHandler implements ActionListener
  {
    public void actionPerformed( ActionEvent evt )
    {
      String initialPath = jEdit.getProperty( SpellCheckPlugin.ASPELL_EXE_PROP );

      String[] paths = GUIUtilities.showVFSFileDialog( null, initialPath, JFileChooser.OPEN_DIALOG, false );

      if ( paths != null )
        SpellCheckOptionPane.this._aspellExeFilenameField.setText( paths[0] );
    }
  }
  
  private class ListDictsActionHandler implements ActionListener{
	  private MutableComboBoxModel model;
	  public ListDictsActionHandler(MutableComboBoxModel m){
		  model=m;
	  }
    public void actionPerformed( ActionEvent evt )
    {
			refreshList(model);
	}
  }

  /** run SpellCheckPlugin.listDictionnaries in a separate thread
   *  adapted from Proxy listing in PluginManagerOptionPane
   */
	class ListDictionnariesThread extends WorkRequest
	{
		private MutableComboBoxModel model;
		public ListDictionnariesThread(MutableComboBoxModel m){
			model = m;
			setAbortable(true);
		}
		public void run()
		{
			setStatus(jEdit.getProperty("spell-check.workthread-status"));
			setMaximum(1);
			setValue(0);

			final Vector<String> dicts = new Vector<String>();
			try
			{
				dicts.addAll(SpellCheckPlugin.getAlternateLangDictionaries());
			}
			catch (SpellException ex)
			{
				Log.log(Log.ERROR,this,ex);
				GUIUtilities.error(SpellCheckOptionPane.this,
					"ioerror",new String[] { ex.toString() });
			}

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					while(model.getSize()!=0)model.removeElementAt(0);//remove listing...
					for(int i=0;i<dicts.size();i++)model.addElement(dicts.get(i));
						

					String dict = jEdit.getProperty(SpellCheckPlugin.ASPELL_LANG_PROP);
					model.setSelectedItem(dict);
				}
			});

			setValue(1);
		}
	} //}}}
}

class ModeTableModel extends AbstractTableModel
{
  private ArrayList modes;

  ModeTableModel()
  {
    Mode[] _modes = jEdit.getModes();

    modes = new ArrayList(_modes.length);

    for(int i = 0; i < _modes.length; i++)
    {
      modes.add(new Entry(_modes[i].getName()));
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
      return mode.isSelected;
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
      mode.isSelected = (Boolean)value;
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
  Boolean isSelected;

  Entry( String name )
  {
    this.name = name;
    boolean isDefault = ( ( SpellCheckPlugin.defaultModes.contains( name ) ) ? true : false );
    isSelected = new Boolean( jEdit.getBooleanProperty( "spell-check-mode-" + name + "-isSelected", isDefault ) );
  }

  void save()
  {
    if ( isSelected.booleanValue() || SpellCheckPlugin.defaultModes.contains( name ) )
      jEdit.setBooleanProperty( "spell-check-mode-" + name + "-isSelected", isSelected.booleanValue() );
    else
      jEdit.unsetProperty( "spell-check-mode-" + name + "-isSelected" );
  }
  
}