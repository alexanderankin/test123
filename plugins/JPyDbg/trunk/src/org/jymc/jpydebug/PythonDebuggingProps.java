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

package org.jymc.jpydebug;

import java.util.* ;
import java.io.* ;

/**
 * @author jean-yves
 *
 * context debugging properties
 * 
 */


public class PythonDebuggingProps
extends Observable
{
  public final static String CONFIGURATION = "configuration." ;	
  public final static String PROGRAM_CONFIGURATION = "program.configuration." ;	
  public final static String PROGRAM_BPS = "program.bps." ;	
  public static final String NOARGS = "no args" ; 	
  
  private final static String _COMMA_ = "," ; 
  private final static String _CONFIG_HEADER_ = "Python Program Args configuration file" ; 
  /** global property instance */
  private static Properties _props = null ;  
  
  private static PythonDebuggingProps _observable = new PythonDebuggingProps() ; 
  
  /** commonly used property file */
  private static File _propFile = null ; 

  /**
   * load JpyDbg user debug context file 
   * @param propFile
   * @return
   * @throws PythonDebugException
   */
  public static Properties load( File propFile )
  throws PythonDebugException
  {
  	if ( _props != null )
  	  return _props ;
  	   
    _props = new Properties() ; 
    _propFile = propFile      ; 
    
	if ( _propFile.exists() && _propFile.isFile() )
	{
	  try {	
		_props.load( new BufferedInputStream( new FileInputStream(_propFile))) ;	
	  } catch ( IOException e )
	  { throw new PythonDebugException( "IoError on configuration load :" + e.getMessage() ) ; }  
	}
	return _props ; 
  }

  private static void setFilterProperty( String filter , String name , String value  )
  {
	if ( _props != null )
	{	
	StringBuffer wk = new StringBuffer(filter) ; 
	  if ( ( name != null) && ( name.length() > 0) )
	  { 
	    wk.append( name ) ; 
	    _props.setProperty( wk.toString() , value ) ; 
	  }  
	}  
  }

  private static void removeFilterProperty( String filter , String name   )
  {
	if ( _props != null )
	{	
	StringBuffer wk = new StringBuffer(filter) ; 
	  wk.append( name ) ; 
	  _props.remove( wk.toString()) ; 
	}  
  }

  private static String getFilterProperty( String filter , String name )
  {
	if ( _props != null )
	{	
	StringBuffer wk = new StringBuffer(filter) ; 
	  wk.append( name ) ; 
	  return _props.getProperty( wk.toString() ) ; 
	}  
	return null ; 
  }	

  private static Vector filter( String filter )
  {
	Vector wk = new Vector() ;
	  if ( _props != null )
	  {
	  Enumeration keys = _props.keys() ; 
		while ( keys.hasMoreElements() )
		{
		String cur = (String)keys.nextElement() ; 
		  if ( cur.startsWith( filter ) && ( cur.length() > filter.length() ))
		    wk.addElement( cur.substring(filter.length())) ; 	
		}
	  }
	  return wk   ; 	
  }

  public static Vector getConfigurations()
  { return filter(CONFIGURATION) ; }
  public static void setConfigurationProperty( String name , String value )
  { 
  	setFilterProperty(CONFIGURATION , name , value ) ; 
	_observable.setChanged() ;  // populate CONFIGURATION change status to observers  
  }	
  public static String getConfigurationProperty( String name )
  { return getFilterProperty(CONFIGURATION , name ) ; }
  public static void removeConfigurationProperty( String name )
  { removeFilterProperty(CONFIGURATION,name) ; }	
 

  public static String getProgramConfigurationProperty( String name )
  { return getFilterProperty(PROGRAM_CONFIGURATION , name ) ; }
  public static void setProgramConfigurationProperty( String pgmName , String argKey  )
  {
    if ( argKey == NOARGS )
    {
      if ( getProgramConfigurationProperty(pgmName) != null )
	    removeFilterProperty(PROGRAM_CONFIGURATION,pgmName) ; // clean old reference	
    }
    else
      setFilterProperty(PROGRAM_CONFIGURATION,pgmName,argKey) ; 
  }
  public static void removeProgramConfigurationProperty(String pgmName)
  { removeFilterProperty(PROGRAM_CONFIGURATION,pgmName) ; }
  
  public static Hashtable getBreakPoints( String pgmName )
  {
  String strBps = getFilterProperty( PROGRAM_BPS , pgmName ) ; 
  Hashtable returned = null ;
    if ( strBps != null )
    {
    StringTokenizer parser = new StringTokenizer(strBps,_COMMA_) ;	
      returned = new Hashtable() ;  
      while ( parser.hasMoreTokens() )
      {
      String curLine = parser.nextToken() ; 
      Integer line = new Integer(-1) ; 
        try {
          line = new Integer(curLine) ; 	
        } catch ( NumberFormatException e )
        {} 
        if ( line.intValue() != -1 )
          returned.put( line , line ) ; 	
      }
    }
    return returned ; 	 
  }

  public static void setBreakPoints( String pgmName , Enumeration bpsList )
  {
  StringBuffer bpsBuff = new StringBuffer() ; 
    while ( bpsList.hasMoreElements() )
    {
      bpsBuff.append(bpsList.nextElement()) ; 
      if (  bpsList.hasMoreElements() )
        bpsBuff.append(_COMMA_) ; 	
    }	
    if ( bpsBuff.length() > 0 )
      setFilterProperty(PROGRAM_BPS,pgmName,bpsBuff.toString()) ; 
    else 
      removeFilterProperty(PROGRAM_BPS,pgmName) ; // cleanup any remainding ref to empty BP containers  
  }
  	
  public static void addPropertyObserver( Observer observer )
  { _observable.addObserver(observer) ; }  
  public static void removePropertyObserver( Observer observer )
  { _observable.deleteObserver(observer) ; }  

  public static void save()
  throws PythonDebugException
  {
  	Assert.that(_propFile != null ) ; 
  	if ( _props == null )
  	  return ; 
	try {	
	  _props.store( new BufferedOutputStream( new FileOutputStream(_propFile)),_CONFIG_HEADER_) ;
	  if ( _observable.hasChanged() )
	  {
	    _observable.notifyObservers() ; 
	    _observable.clearChanged() ; 
	  }  
	} catch ( IOException e )
	{ throw new PythonDebugException( "IoError on configuration store :" + e.getMessage() ) ; }  
  }


}
