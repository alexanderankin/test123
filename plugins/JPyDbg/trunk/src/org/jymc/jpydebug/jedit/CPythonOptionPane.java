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

import org.gjt.sp.jedit.*;
import org.jymc.jpydebug.PythonDebugParameters;
import org.jymc.jpydebug.swing.ui.PythonConfigurationPanel; 
import java.awt.event.*;


/**
 * @author jean-yves
 *
 * Option Pane used to modify JPython CPython Compiler's location
 * 
 */

public class CPythonOptionPane
extends AbstractOptionPane
{
    private final static String _EMPTY_ = "" ; 
    private PythonConfigurationPanel _pyConfPanel = new PythonConfigurationPanel() ;   
  
    public CPythonOptionPane()
    { super("cpython-options"); }
    
    
    class _PYTHON_CHECK_ 
    implements ActionListener 
    {
      public void actionPerformed( ActionEvent e )
      { 
        // apply changes before proceeding
        _save() ;
        new PythonCheck(false).actionPerformed(e) ;
      }
      
    }
    
   
    /**
     *  init option pane panel
     */
    protected void _init()
    {
      setLayout(new BoxLayout( this , BoxLayout.Y_AXIS)  )  ;
      _pyConfPanel.set_label(   PythonConfigurationPanel.LOCALPYTHON_PROPERTY_LABEL ,
                                jEdit.getProperty( PythonConfigurationPanel.LOCALPYTHON_PROPERTY_LABEL ) );
      _pyConfPanel.set_value(   PythonConfigurationPanel.LOCALPYTHON_PROPERTY ,    
                                jEdit.getProperty( PythonConfigurationPanel.LOCALPYTHON_PROPERTY ) );
      PythonDebugParameters.set_pythonShellPath(jEdit.getProperty( PythonConfigurationPanel.LOCALPYTHON_PROPERTY )) ;
      
      _pyConfPanel.doMyLayout( new _PYTHON_CHECK_() ) ; 
      add( _pyConfPanel ) ;  
    }

    /**
     *  save options properties
     */
    protected void _save()
    {
      // populate fields first
      _pyConfPanel.populateFields() ;
      // functional properties
      jEdit.setProperty( PythonConfigurationPanel.LOCALPYTHON_PROPERTY , 
                         PythonDebugParameters.get_pythonShellPath ()
                      ) ; 
      // Do not forget to save PythonPath panel changes
      _pyConfPanel.get_pythonPathPanel().writePath(false) ;       
      
    }
  }

