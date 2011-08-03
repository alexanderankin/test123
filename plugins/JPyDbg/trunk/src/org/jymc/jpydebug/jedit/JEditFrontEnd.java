/*
 * JEditFrontEnd.java
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
* 
*/

package org.jymc.jpydebug.jedit;

import org.jymc.jpydebug.* ;
import org.gjt.sp.util.Log ; 
import org.gjt.sp.jedit.jEdit ; 
import org.gjt.sp.jedit.View ; 
import org.gjt.sp.jedit.Buffer ; 
import javax.swing.JOptionPane ;
import org.gjt.sp.jedit.MiscUtilities ;
import java.io.* ; 

/**
 * IDEFrontEnd implementation for NetBeans
 * @author jean-yves
 */
public class JEditFrontEnd
implements IDEFrontEnd    
{
  private final static String _JPYDBG_FATAL_MESSAGE = "JpyDbgForNetbeans FATAL error"  ; 
  
  
  public void logError( Object source , String message ) 
  { Log.log( Log.ERROR , source , message )  ; }
  
  public void logWarning( Object source , String message ) 
  { Log.log( Log.WARNING , source , message )  ;   }
  
  public void logDebug( Object source , String message ) 
  { Log.log( Log.DEBUG , source , message )  ; }

  public void logInfo( Object source , String message ) 
  { Log.log( Log.MESSAGE , source , message )  ; }
  
  /** Creates a new instance of JEditFrontEnd */
  public JEditFrontEnd()
  {}
  
  public File getFile( String fName )
  throws PythonDebugException 
  // TODO: To be implemented
  { return null ; }
  
  public void populateFatalError( String message )
  {
    JOptionPane.showMessageDialog( null , message , _JPYDBG_FATAL_MESSAGE ,JOptionPane.ERROR_MESSAGE ) ;
  }
  
  public String getSettingsDirectory() 
  { 
  StringBuffer returned = new StringBuffer(jEdit.getSettingsDirectory() )  ;
    returned.append( File.separatorChar ) ; 
    returned.append( PythonInspector.JPYDBGXML) ;
    return returned.toString() ; 
  }

  /** request Shortcut key to host IDE */
  public String getShortcutKeyInfo( String msginf , String shortCut ) 
  {
    if ( shortCut == null )
      return msginf ; // no shortcut provided
    StringBuffer ret = new StringBuffer(msginf) ;   
    String shortcutKey = jEdit.getProperty(shortCut) ;
    if ( shortcutKey == null )
      return msginf ;
      
    ret.append('(') ;
    ret.append(shortcutKey) ;
    ret.append(')') ;
    return ret.toString() ;

  }

  public String constructPath(String parent, String path) 
  {
    return MiscUtilities.constructPath(parent,path) ;
  }
  
  /** request given source to be populated inside Editor  */
  public Object displaySource( String source ) 
  {
    if ( source != null  ) 
    {  
    View curView = jEdit.getActiveView() ;  
      return jEdit.openFile(curView,source) ;
    }
    return null ;
  }
  
  /* not used from JEdit */
  public Object displaySourceLine( String source , int line ) 
  { return null ; }

  
  /** send back the current selected focussed source full path info */
  public String getCurrentSource() 
  {
    View curView = jEdit.getActiveView() ;  
    if ( curView != null )
    {  
    Buffer buf = curView.getBuffer() ; 
      return buf.getPath()  ; 
    }
    return null ;
  }

  /** send back a neutral object buffer context (null if not jEdit) */
  public Object getBufferContext() 
  {
    View curView = jEdit.getActiveView() ;  
    if ( curView != null )
      return curView.getBuffer() ;   
    return null ;
  }
  
  /** used for netbeans only */
  public Object getBufferContext( String fName ) 
  {
    return null ; 
  }

  public JpyDbgErrorSource getDefaultErrorSource()
  {
    return JPYPythonParser.get_defaultErrorSource()  ;
  }
  
  public String getBufferPath( Object buffer ) 
  { return ((Buffer)buffer).getPath () ; }

}
