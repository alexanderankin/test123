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

import java.awt.*;

import org.gjt.sp.jedit.*;
import java.awt.event.*;


/**
 * @author jean-yves
 *
 * Option Pane used to modify JPython CPython Compiler's location
 * 
 */

public class ProfilerOptionPane
extends AbstractOptionPane
{
  public final static String LOCALPYLINT_PROPERTY = "jpydebug-dbgoptions.profiler" ; 
  
  public ProfilerOptionPane()
  
  { super("profiler-options"); }
  
  class _PANE_VERTICAL_ELEMENT_
  extends JPanel 
  {
    public _PANE_VERTICAL_ELEMENT_( JComponent label , JComponent field )
    {
      setLayout ( new FlowLayout( FlowLayout.LEFT ,5,5) ) ;   
      add(label) ; 
      add(field) ; 
    } 
  }
  
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
  
  class _PROFILER_PANE_
  extends JPanel 
  {
    public _PROFILER_PANE_()
    {
      setLayout( new GridLayout(1,1)) ;
      add( new JLabel("             to be implemented in next release") ) ;
    } 
  }
  
  /**
   *  init option pane panel
   */
  protected void _init()
  {
    setLayout(new BoxLayout( this , BoxLayout.Y_AXIS)  )  ;
    //setLayout( new BorderLayout() ) ;   
    add( new _PROFILER_PANE_() ) ;  
  }

  /**
   *  save options properties
   */
  protected void _save()
  {
  }
  }

