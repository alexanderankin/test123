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


package org.jymc.jpydebug.jedit;

import org.gjt.sp.jedit.* ;
import org.gjt.sp.util.Log;
// import java.util.* ; 
import org.gjt.sp.jedit.menu.* ; 
import org.gjt.sp.jedit.gui.* ; 

import javax.swing.* ;

import java.awt.event.* ;

import org.jymc.jpydebug.PythonDebugParameters;
import org.jymc.jpydebug.PythonInstaller;

/**
 * @author jean-yves
 *
 * 'Main' JEdit entry for JEdit Python Debugging environment pluggin
 *
 */

public class JPYJeditPlugin
extends EditPlugin
implements DynamicMenuProvider
{
  public final static String ACTION = "JpyDebug-mainentry" ;
	
  public static final String NAME = "JPyDebug" ; 
  public static final String DOCKABLE = "pythonpanel" ; 

  private static DynamicMenuProvider _jpyMenu  = null ;
  private static JComponent          _pythonPane = null ; 
  private static EditAction          _jpydebugMainEntry = null ;  
	
  public JComponent get_pythonPane()
  { return _pythonPane ; }
    
  public EditAction get_jpydebugMainEntry()
  { return _jpydebugMainEntry ; }
	
  public void start()
  {
    if ( PythonDebugParameters.get_debugTrace())
      Log.log(Log.DEBUG,JPYJeditPlugin.class,"'start' of Python Debug Plugin");
    _jpyMenu = this ; 
    // Setup IDE frontend asap
    PythonDebugParameters.ideFront = new JEditFrontEnd() ;
	// Check for Python sources update request before any other actions
	PythonInstaller installer = new PythonInstaller() ;
	installer.putInPlace() ; 
  }
	
  public void stop()
  {
    if (PythonDebugParameters.get_debugTrace())
      Log.log(Log.DEBUG,JPYJeditPlugin.class,"'stop' of Python Debug Plugin");
  }
	
  class _MAIN_JPYDBG_MENU_ENTERED_
  implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      // eye catcher for Jedit log  
      Log.log(Log.DEBUG,JPYJeditPlugin.class,"Entering python debug menu");
      View cur = jEdit.getActiveView() ;
      DockableWindowManager mngr = cur.getDockableWindowManager() ; 
      mngr.showDockableWindow(DOCKABLE) ; 	
    }
  }
	
  public void update( JMenu jmenu )
  {
  JMenuItem main = new JMenuItem("JpyDbg python debugger") ;
    main.addActionListener( new _MAIN_JPYDBG_MENU_ENTERED_() ) ;	
    jmenu.add(main) ; 	
  }
	
  // Bring jpydbg pane upfront 
  public static void showJpyDbg()
  {
  View view = jEdit.getActiveView() ;  
  DockableWindowManager dockableWindowManager = view.getDockableWindowManager();
    dockableWindowManager.addDockableWindow(DOCKABLE);
  } //}}}
	
	public boolean updateEveryTime() 
	{ return false ; }
	
  /**
  * JEdit python debug action
  */
  public static void jpydbg ( View view , Buffer buffer )
  {
    // eye catcher for Jedit log  
    if (PythonDebugParameters.get_debugTrace())
      Log.log(Log.DEBUG,JPYJeditPlugin.class,"Entering Python Debug Pluggin");
  }

  public static DynamicMenuProvider get_jpyMenu() 
  { return _jpyMenu ; }
    

}
