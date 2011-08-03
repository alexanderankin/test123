/**
* Copyright (C) 2005 Jean-Yves Mengant
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

package org.jymc.jpydebug;

import java.io.* ;

/**
 * This interface is there to provide inter IDE compatibility inside
 * Jpydbg and must be implemented for each IDE implmentation 
 * @author jean-yves
 */
public interface IDEFrontEnd
{
  /* login mecanism */
  
  public void logError( Object source , String message ) ; 
  public void logWarning( Object source , String message ) ; 
  public void logDebug( Object source , String message ) ; 
  public void logInfo( Object source , String message ) ; 
  
  
  /* file access facilities */
  public File getFile( String fName )
  throws PythonDebugException ;
  
  /** display fatal inside the IDE */
  public void populateFatalError( String message ) ;
  
  /** get IDE configuration directory */
  public String getSettingsDirectory() ; 
  
  /** request Shortcut key to host IDE */
  public String getShortcutKeyInfo( String msginf , String shortCut ) ;

  public String constructPath(String parent, String path) ; 
  
  /** request given source to be populated inside Editor  */
  public Object displaySource( String source ) ;

  public Object displaySourceLine( String source , int line ) ; 
   
  /** send back the current selected focussed source full path info */
  public String getCurrentSource() ;
  
  /** send back a neutral object buffer context (null if not jEdit) */
  public Object getBufferContext() ;
  public Object getBufferContext( String fName ) ; 
  public String getBufferPath( Object buffer ) ;
  
  /** return back the error Source interface */
  public JpyDbgErrorSource getDefaultErrorSource() ;

}
