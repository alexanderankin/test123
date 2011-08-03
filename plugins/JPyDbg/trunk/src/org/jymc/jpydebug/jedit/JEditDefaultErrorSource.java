/*
 * JEditDefaultErrorSource.java
 *
 * Created on December 24, 2005, 4:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jymc.jpydebug.jedit;

import errorlist.* ; 
import org.jymc.jpydebug.* ;

/**
 *
 * @author jean-yves
 */
public class JEditDefaultErrorSource
implements JpyDbgErrorSource
{
  
  private static DefaultErrorSource _defaultErrorSource = null ; 
  
  /** Creates a new instance of JEditDefaultErrorSource */
  public JEditDefaultErrorSource( DefaultErrorSource defaultErrorSource ) 
  { _defaultErrorSource = defaultErrorSource ; }
  
  public void addError ( int type , 
                         String path,
		         int lineIndex , 
                         int start , 
                         int end, 
                         String error) 
  {
    if ( _defaultErrorSource != null )
      _defaultErrorSource.addError(type,path,lineIndex , start , end , error ) ;
  }

  /**
   * directly handled by ErrorList  pluggin
  */ 
  public void show()
  {
  }
  
}
