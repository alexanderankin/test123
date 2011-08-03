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



package org.jymc.jpydebug.swing.ui;

import java.util.EventObject;
import javax.swing.* ;

/**
 * @author jean-yves
 *
 *  Techprint debugging event 
 */
public class DebugEvent 
extends EventObject 
{
  public final static String STOP =  "shutdown Python environment" ; 
  public final static String START =  "startup local debugging session" ; 
  public final static String REMOTESTART =  "startup remote debugging session" ; 
  public final static String STEPOVER = "Debug statement step Over"  ; 
  public final static String STEPINTO = "Debug statement step Into" ; 
  public final static String RUN = "Run" ; 
  public final static String SENDCOMMAND = "execute Python Command"  ; 
  public final static String COMMANDFIELD = "Python command field"   ; 
  public final static String TOGGLEJYTHON = "Jython / CPython language switch"   ; 
  public final static String PGMARGS = "Add python programs arguments to args table"   ; 
    
  private String _moduleName ; 
  private Action _action ; 
  private AbstractButton _guiButton ;   
    
  public DebugEvent( Object source , 
                     String moduleName , 
                     Action action , 
                     AbstractButton gui
                   )
  {
    super(source) ;     
    _action = action ; 
    _guiButton = gui ; 
  }

  public String get_moduleName()
  { return _moduleName ; }
  
  public Action get_action()
  { return _action ; }
  
  public AbstractButton get_guiButton()
  { return _guiButton ; }

}
