package org.jymc.jpydebug.swing.ui;

import javax.swing.* ;
import java.awt.event.*    ;

import java.util.*   ;

import org.jymc.jpydebug.* ;


/**
 * @author jean-yves
 * Module / Configuration program line argument displayobject
 *
 */
public class ModuleConfigCombo
extends JComboBox
implements Observer
{
  private static final String _EMPTY_ARGS_ = "empty program command line arguments" ;
  private static final String _EMPTY_STRING_ = "" ;
  
  private String _curModName = null ;
  private boolean _initing = false ;
  private String _curKey = null ;
  private String _curValue = null ;
  
  
  private String getArguments ( String key )
  {
    if ( key == PythonDebuggingProps.NOARGS )
      return _EMPTY_ARGS_ ;
    String curArgs = PythonDebuggingProps.getConfigurationProperty (key) ;
    if ( curArgs == null )
      return _EMPTY_ARGS_ ;
    return curArgs ;
  }
  
  class _SELECTION_CHANGED_
  implements ActionListener
  {
    /** current selected changed by user selection */
    public void actionPerformed ( ActionEvent e )
    {
      _curKey = (String)getSelectedItem () ;
      _curValue = getArguments (_curKey) ;
      setToolTipText (_curValue) ;
      // only store config changes on user combo selection changes
      if ( ! _initing  )
      {
        PythonDebuggingProps.setProgramConfigurationProperty (_curModName , _curKey) ;
        try
        {
          PythonDebuggingProps.save () ; // save on change
        }
        catch ( PythonDebugException ex )
        { JOptionPane.showMessageDialog ( ModuleConfigCombo.this , ex.getMessage () ) ; }
      }
    }
  }
  
  public ModuleConfigCombo ()
  {
    _SELECTION_CHANGED_ comboChanges = new _SELECTION_CHANGED_ () ;
    super.addActionListener (comboChanges) ;
    // super.addItemListener(comboChanges) ;
    PythonDebuggingProps.addPropertyObserver (this)   ;
  }
  
  /**
   * bind modname with command line arguments
   * @param modName
   */
  public void populate_program_arguments ( String modName )
  {
    _curModName = modName ;
    _initing    = true   ;
    removeAllItems () ; // cleanup old content
    addItem (PythonDebuggingProps.NOARGS) ;
    setToolTipText (_EMPTY_ARGS_) ;
    Vector configs = PythonDebuggingProps.getConfigurations () ;
    Enumeration configList = configs.elements () ;
    while ( configList.hasMoreElements ()  )
    {
      String curArg = (String) configList.nextElement () ;
      addItem (curArg) ;
    }
    // make either NOARGS or associated configuration as current selection
    String curConfig = PythonDebuggingProps.getProgramConfigurationProperty (modName) ;
    if ( curConfig != null )
    {
      if ( configs.contains ( curConfig  ) )
      {
        setSelectedItem ( curConfig ) ;
        setToolTipText (PythonDebuggingProps.getConfigurationProperty (curConfig)) ;
      }
      else
        PythonDebuggingProps.removeProgramConfigurationProperty (modName) ; // no more valid
    }
    invalidate () ;
    _initing = false ;
  }
  
  public String getArgValue ()
  {
    if ( _curKey == PythonDebuggingProps.NOARGS )
      return _EMPTY_STRING_ ;
    return _curValue ;
  }
  
  /** just synchronize the prop content to reflect changes */
  public void update ( Observable observable , Object object )
  { populate_program_arguments (_curModName) ; }
  
}
