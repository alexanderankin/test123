/**
* Copyright (C) 2006 Jean-Yves Mengant
*
 * PythonInstaller.java
 *
 * Created on January 4, 2006, 10:02 AM
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

import org.jymc.jpydebug.swing.ui.PythonDebugContainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;


/**
 * automatically install and check necessary Python resources for the IDE
 *
 * @author jean-yves
 */
public class PythonInstaller
{
  // private final static String _PYLOC_   = "python/";
  // moving back to root 
  // used for old cleaning old  
  private final static String _PYLOC_   = "/python/jpydbg/";
  private final static String _LOCATOR_ = _PYLOC_ + "MANIFEST";
  private final static String _JPYDBGSCRIPT_ = "jpydaemon.py" ; 
  private final static String _WORK_ = "tmp" ; 
  private final static String _VERSIONPREFIX_ = "JpyDbg_" ; 
  private final static String _PYSUFFIX_ = ".py" ; 
  
  /**
   * Creates a new instance of PythonInstaller
   */
  public PythonInstaller(){ }

  private String getPythonDir()
  {
    return PythonDebugParameters.ideFront.getSettingsDirectory() + '/' +_PYLOC_;
  }

  private File getPythonWorkDir()
  {
  String dir = PythonDebugParameters.ideFront.getSettingsDirectory() + '/' +_WORK_;
  File fDir  = new File(dir) ; 
    if ( fDir.exists() )
	  return fDir ; 
    fDir.mkdir() ;
	return fDir ; 
  }
  
  private void cleanupPythonDir( File pythonDir )
  {
    if ( pythonDir.isDirectory() )
    {
      String fileNames[] = pythonDir.list() ;
      for ( int ii = 0 ; ii < fileNames.length ; ii++ )
      {
        if ( ( fileNames[ii].startsWith(_VERSIONPREFIX_ ) ) ||
             ( fileNames[ii].endsWith(_PYSUFFIX_))   
           )
        {
        File f = new File(pythonDir , fileNames[ii]) ;
          f.delete() ;
        }    
      }    
    }   
  }

  private void cleanupVersions( File vFName )
  {
  File curDir = vFName.getParentFile() ; 
    // ./python/jpydbg
    cleanupPythonDir(curDir) ;
    // cleanup old obsolete parent installation location if requested
    // ./python
    if ( curDir.getParentFile() != null )
      cleanupPythonDir(curDir.getParentFile()) ;
    
  }

  private File getFileNameVersion()
  {
    return new File(
                    getPythonDir(),
                    PythonDebugContainer.VERSION.replace( ' ', '_' )
                 );
  }


  private boolean checkVersion()
  {
    File versionFile = getFileNameVersion();
    if (versionFile.exists())
      return true;
    else
    {
      // cleanup previous version file and return false
      // to install new  
      cleanupVersions( versionFile ) ;  
      return false;
    }  
  }


  private InputStream getResourceFromDistrib( String name )
  {
    return this.getClass().getResourceAsStream( name );
  }


  /**
   * Check that the correct version of JpyDbg Python stuff is in place
   *
   * @return true if in place
   */
  public boolean checkInPlace()
  {
    return checkVersion();
  }


  private void populatePythonDirectory( InputStream myStream )
                                throws PythonDebugException
  {
    BufferedReader rdr  =
      new BufferedReader( new InputStreamReader( myStream ) );
    Vector         list = new Vector();
    try
    {
      String curPython = rdr.readLine();
      while (curPython != null)
      {
        list.addElement( curPython );
        curPython = rdr.readLine();
      }
      rdr.close();

    }
    catch (IOException e)
    {
      throw new PythonDebugException(
                                     " IO Error reading Manifest in populatePythonDirectory"
                                  );
    }

    // just use to monitor python setup
    _MONITOR_ mon = new _MONITOR_( list.size() );

    Enumeration pyList  = list.elements();
    int         counter = 0;
    while (pyList.hasMoreElements())
    {
      String      curPy = (String) pyList.nextElement();
      InputStream cur   = getResourceFromDistrib( _PYLOC_ + curPy );
      if (cur == null)
        throw new PythonDebugException( _PYLOC_ + curPy + " resource missing in JpyDbg distribution" );

      BufferedReader in = new BufferedReader( new InputStreamReader( cur ) );
      try
      {
        File fDir = new File( getPythonDir() );
        if (!fDir.exists())
          fDir.mkdirs();
        else if (!fDir.isDirectory())
        // give up when python is not a subdir !!!!!!
          throw new PythonDebugException( fDir.toString() + " exists but is not a Directory => Giving up on JpyDbgSetup" );

        File           f    = new File(
                                       fDir,
                                       curPy
                                    );
        BufferedWriter dest = new BufferedWriter( new FileWriter( f ) );
        String         line = in.readLine();
        while (line != null)
        {
          dest.write( line );
          dest.newLine();
          line = in.readLine();
        }
        in.close();
        dest.close();
      }
      catch (IOException e)
      {
        throw new PythonDebugException(
                                       "JpyDbg : severe IOError populating python directory : " + e.getMessage()
                                    );

      }
      mon.statusChanged( ++counter, "File " + curPy + " processed " );
    }

    try
    {

      // Finally acknowledge by writing an empty VersionFile inside directory
      BufferedWriter dest =
        new BufferedWriter( new FileWriter( getFileNameVersion() ) );
      dest.close();
    }
    catch (IOException e)
    {
      throw new PythonDebugException(
                                     "JpyDbg : severe IOError writing :" + getFileNameVersion() + "=" + e.getMessage()
                                  );

    }


  }


  /**
   * Put the /python requested utilities at the right place under the
   * configuration directory , in a dedicated Python subfolder from the
   * distribution jar ./Python subfolder where the pythonsetup.txt describes the
   * list of python sources to install This process is JEdit / Netbeans
   * compliant and must be activated on plugin load
   *
   * @throws PythonDebugException When any configuration problem is encountererd
   */
  public void putInPlace()
  {

    if (!checkInPlace())
    {
      InputStream myStream = getResourceFromDistrib( _LOCATOR_ );
      try
      {
        if (myStream == null)
          throw new PythonDebugException(
                                         _LOCATOR_ +
                                         " not in JpyDbg package jar => JpyDbg Packaging error"
                                      );
        populatePythonDirectory( myStream );
      }
      catch (PythonDebugException e)
      {
        JOptionPane.showMessageDialog( null, "Python setup error = " + e.getMessage(), "Severe jpyDbg Setup Error", JOptionPane.ERROR_MESSAGE );
      }
    }
    // populate the jpyDbg directory accordingly + setup a tmp work directory as well
	File f = new File( getPythonDir() , _JPYDBGSCRIPT_ ) ; 
	if ( ! f.exists() || f.isDirectory() )
        JOptionPane.showMessageDialog( null, "Python setup error : missing jpydbgdaemon = " + f.toString() , "Severe jpyDbg Setup Error", JOptionPane.ERROR_MESSAGE );

	// set JpyDbgLocation
	PythonDebugParameters.set_jpydbgScript(f.toString()) ;
	// set working directory
	PythonDebugParameters.set_tempDir(getPythonWorkDir().getAbsolutePath()) ;
  }


  class _MONITOR_ extends Thread
  {
    private ProgressMonitor _pBar;
    private int             _size;

    public _MONITOR_( int size )
    {
      _size = size;
      SwingUtilities.invokeLater( this );
    }

    public void run()
    {
      _pBar = new ProgressMonitor(
                                  null,
                                  "Upgrading JpyDbg Python's stuff for " + PythonDebugContainer.VERSION,
                                  "Initializing ...",
                                  0,
                                  _size
                               );

    }


    private void statusChanged(
                               final int    counter,
                               final String message
                            )
    {
      SwingUtilities.invokeLater(
                                 new Runnable()
        {
          public void run()
          {
            _pBar.setProgress( counter );
            _pBar.setNote( message );
          }
        }
                              );
    }
  }


}
