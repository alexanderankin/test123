package org.jymc.jpydebug.jedit;
/**
* Copyright (C) 2003,2004,2005 Jean-Yves Mengant
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

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gjt.sp.jedit.*;
import org.jymc.jpydebug.PythonDebugParameters;
import org.jymc.jpydebug.swing.ui.PythonPathTreePanel;
import org.jymc.jpydebug.utils.* ; 
import org.jymc.jpydebug.swing.ui.JythonConfigurationPanel ;

public class JythonOptionPane
extends AbstractOptionPane
{
    private final static String _EMPTY_ = "" ; 
    private JythonConfigurationPanel _jyConfPanel = new JythonConfigurationPanel() ;
  
  
    public JythonOptionPane()
    { super("jython-options"); }

    class _PYTHON_CHECK_ 
    implements ActionListener 
    {
      public void actionPerformed( ActionEvent e )
      { 
        // apply changes before proceeding
        _save() ;
        new PythonCheck(true).actionPerformed(e) ;
      }
    }
    
    
    /**
     *  init option pane panel
     */
    protected void _init()
    {
      setLayout(new BoxLayout( this , BoxLayout.Y_AXIS)  )  ;
      //setLayout( new BorderLayout() ) ;  
      
      _jyConfPanel.set_label( JythonConfigurationPanel.JYTHON_HOME_LABEL ,
	                     jEdit.getProperty(JythonConfigurationPanel.JYTHON_HOME_LABEL)) ; 
      _jyConfPanel.set_value( JythonConfigurationPanel.JYTHON_HOME, 
	                     jEdit.getProperty( JythonConfigurationPanel.JYTHON_HOME ));
      PythonDebugParameters.set_jythonHome(jEdit.getProperty( JythonConfigurationPanel.JYTHON_HOME )) ;
      PythonDebugParameters.set_pythonShellPath(jEdit.getProperty(JythonConfigurationPanel.JYTHON_HOME)) ;
      
      
      _jyConfPanel.set_label( JythonConfigurationPanel.LOCALJYTHONJVM_PROPERTY_LABEL ,
	                     jEdit.getProperty(JythonConfigurationPanel.LOCALJYTHONJVM_PROPERTY_LABEL)) ; 
      _jyConfPanel.set_value( JythonConfigurationPanel.LOCALJYTHONJVM_PROPERTY ,
                             jEdit.getProperty( JythonConfigurationPanel.LOCALJYTHONJVM_PROPERTY ));
      PythonDebugParameters.set_jythonShellJvm(jEdit.getProperty( JythonConfigurationPanel.LOCALJYTHONJVM_PROPERTY )) ;
      /*
      _localJythonArgs.setText(jEdit.getProperty( LOCALJYTHON_PROPERTY ));
      PythonDebugParameters.set_jythonShellArgs(jEdit.getProperty( LOCALJYTHON_PROPERTY )) ;
      */
      _jyConfPanel.set_label( JythonConfigurationPanel.JYTHON_ENVIRONMENT_LABEL ,
	                     jEdit.getProperty(JythonConfigurationPanel.JYTHON_ENVIRONMENT_LABEL )) ; 
      _jyConfPanel.set_booleanValue( JythonConfigurationPanel.JYTHON_ENVIRONMENT ,
	                            jEdit.getBooleanProperty(JythonConfigurationPanel.JYTHON_ENVIRONMENT) ) ;
      PythonDebugParameters.set_jythonActivated(jEdit.getBooleanProperty(JythonConfigurationPanel.JYTHON_ENVIRONMENT)) ;

      _jyConfPanel.doMyLayout( new _PYTHON_CHECK_() ) ;
      
      add( _jyConfPanel ) ;  
    }

    /**
     *  save options properties
     */
    protected void _save()
    {
      // populateFileds first 
      _jyConfPanel.populateFields() ;
      // functional properties
      jEdit.setProperty( JythonConfigurationPanel.LOCALJYTHONJVM_PROPERTY , 
	                 PythonDebugParameters.get_jythonShellJvm () ) ; 
      jEdit.setProperty( JythonConfigurationPanel.JYTHON_HOME , 
	                 PythonDebugParameters.get_jythonHome () ) ; 
      // jEdit.setProperty( LOCALJYTHON_PROPERTY , _localJythonArgs.getText() ) ; 
      jEdit.setBooleanProperty( JythonConfigurationPanel.JYTHON_ENVIRONMENT , 
	                 PythonDebugParameters.get_jythonActivated ()) ; 
      // Do not forget to save PythonPath panel changes
      _jyConfPanel.get_pythonPathPanel().writePath(true) ;       
    }



}
