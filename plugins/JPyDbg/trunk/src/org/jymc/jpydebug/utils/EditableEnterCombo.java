/**
* Copyright (C) 2003 Jean-Yves Mengant
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


package org.jymc.jpydebug.utils;


import java.awt.event.* ;
import javax.swing.* ; 


/**
 * @author jean-yves
 *
 * A JComboBox reacting to ENTER_KEY keystrocke
 * 
 */
public class EditableEnterCombo
extends HistoryTextField 
// implements ActionListener
{
  private final static String _PYTHON_CONSOLE_ = "JpyDbg.console" ; 
  private final static String _EMPTY_ = "" ; 
  private ActionListener _targetAction  = null ; 
  
  	
  public EditableEnterCombo( ActionListener enterAction )
  {
  	super() ; 
  	super.setModel(_PYTHON_CONSOLE_) ; 
  	// System.out.println("entering ECombo") ;   
    setEditable(true) ; 
	_targetAction = enterAction ; 
    super.addActionListener(_targetAction) ; 
  }		

  //{{{ processKeyEvent() method
  protected void processKeyEvent(KeyEvent evt)
  {
  	// default to parent behavior
	super.processKeyEvent(evt);
	// cleanup textfield on completion
    if ( ( evt.getID() == KeyEvent.KEY_PRESSED) &&
         ( evt.getKeyCode() == KeyEvent.VK_ENTER ) )
 	  super.setText(_EMPTY_) ; 
	  
  } //}}}

  public static void main(String[] args)
  {
	JFrame f = new JFrame("Testing Swing Status bar")  ;

	// status.setText("Hello") ;
	EditableEnterCombo b = new EditableEnterCombo(null) ;

	f.getContentPane().add("North", b) ;
	f.pack() ;
	f.setVisible(true) ;
  	
  }
}
