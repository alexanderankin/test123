package org.jymc.jpydebug.jedit;

/**
* Copyright (C) 2003-2004 Jean-Yves Mengant
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


import java.io.File;
import java.util.* ;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.jymc.jpydebug.PythonDebugParameters;

/**
 * @author jean-yves
 * Managing jpydbg compliancy with JEdit FTP buffers
 */
public class FtpBuffers
{
  private final static String _FTP_ = "ftp" ; 
  private final static String _SFTP_ = "sftp" ; 
  private static Hashtable _ftpBuffers = new Hashtable() ;
  
  private static void ioWait(Buffer buffer )
  {
    try {
      while ( buffer.isPerformingIO() )
        Thread.sleep(10) ; 
    } catch (InterruptedException e )
    {}
  }
  
  private static String checkFtpHash( String curPath )
  {
  String curFile = (String )_ftpBuffers.get(curPath ) ;  
    return curFile ; 
  }
  
  public static String checkBufferPath( String curPath )
  {
    File f = new File(curPath) ; 
    if ( f.isFile() )
      return curPath ; 
    return checkFtpHash(curPath) ; 
  }
  
  public static boolean isFtp( String curPath )
  { if ( ( curPath.startsWith(_FTP_ ) ) || (curPath.startsWith(_SFTP_ )) )
      return true ; 
    return false ; 
  }  
  
  public static String checkBufferPath( Object buf )
  {
    if ( buf == null )
      return null ; 
    Buffer buffer = (Buffer)buf ;
    String curPath = (buffer).getPath() ;   
    File f = new File(curPath) ; 
    if ( f.isFile() )
      return curPath ; 
    String wkPath = checkFtpHash(curPath) ; 
    // This may be a FTP stored source or whatever else
    // we need to ask jedit to save in WK and return saved path
    // back
    if ( wkPath == null )
    {  
      wkPath = PythonDebugParameters.get_workDir() + 
                    File.separator +
                    f.getName() ; 
      _ftpBuffers.put( curPath , wkPath ) ; 
    }  
    buffer.save( jEdit.getActiveView() , wkPath ,false ) ; 
    ioWait(buffer) ;  // safely wait for IO completions 

    return wkPath ;   
  }
  
  

}
