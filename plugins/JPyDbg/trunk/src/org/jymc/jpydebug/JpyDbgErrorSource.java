/*
 * JpyDbgErrorSource.java
 *
 * Created on December 24, 2005, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jymc.jpydebug;

/**
 * As a Model use the Jedit Based ErrorManagement plugin interface
 *
 * @author jean-yves
 */
public interface JpyDbgErrorSource
{
  /* Window error management */

  public final static int ERROR   = 0;
  public final static int WARNING = 1;

  /**
   * Adds an error to a Error Window Manager. This method is thread-safe.
   *
   * @param errorType The error type (ErrorSource.ERROR or ErrorSource.WARNING)
   * @param path      The path name
   * @param lineIndex The line number
   * @param start     The start offset
   * @param end       The end offset
   * @param error     The error message
   */
  public void addError(
                       int    type,
                       String path,
                       int    lineIndex,
                       int    start,
                       int    end,
                       String error
                    );


  /**
   * make the error source window visible upfront 
   */
  public void show();
}
