/*
 * CompletionClient.java
 *
 * Created on 13 avril 2006, 18:15
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

import errorlist.DefaultErrorSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.Socket;
import java.util.ArrayList;

import java.util.Collection;
import java.util.Stack;

/**
 * Python Ip based Completion client
 *
 * @author jean-yves
 */
public class CompletionClient
{
  private final static String _CLASSHEADER_       = "@@CLASS@@";
  private final static String _VARHEADER_         = "@@VARIABLE@@";
  private final static String _FUNCHEADER_        = "@@FUNCTION@@";
  private final static String _MODULEHEADER_      = "@@MODULE@@";
  private final static String _CHILDREN_          = "'children':";
  private final static String _LOCALHOST_         = "127.0.0.1";
  private static final String _ENCODING_PROPERTY_ = "file.encoding";
  private static final String _WELCOME_           = "WELCOME";
  private static final String _NO_SUGGESTION_     = "No suggestions" ;
  private static final String _NONE_              = "None" ;
  private static final String _COMPLETE_          = "COMPLETE " ;

  /** completion daemon mandatory counterpart */
  private CompletionDaemon _completionDaemon = null;

  private Socket         _connection;
  private BufferedWriter _cmdStream; // Use it to build Python Dbg commands
  private BufferedReader _answStream; // Use it to get Python Dbg returns back
  private String         _codePage;
  private int            _elementPos ;
  private PythonSyntaxTreeNode _topNode = null ;
  private PythonSyntaxTreeNode _curNode = null ;
  private PythonSyntaxTreeNode _curMethod ;
  private Stack          _parentStack = new Stack() ;
  
  /**
   * Creates a new instance of CompletionClient
   */
  public CompletionClient(){ }

  private void errorHandler ( String origin , String err )
  {
  JpyDbgErrorSource errSource = null ;
    if ( PythonDebugParameters.ideFront != null )
       errSource =PythonDebugParameters.ideFront.getDefaultErrorSource();
    if ( errSource != null )  
      errSource.addError(
                         DefaultErrorSource.ERROR,
                         origin,
                         0,
                         0,
                         0,
                         err
                      );
     else
       System.out.println("ERROR ("+origin+"):"+err ) ;
  }
  
  public PythonSyntaxTreeNode get_topNode()
  { return _topNode ; }
  
  /**
   * reading messages back from Python Debugger side
   */
  private String getMessage() throws PythonDebugException
  {
    try
    {
      return _answStream.readLine();
    }
    catch (IOException e)
    {
      throw new PythonDebugException( "Socket read Command error " +
                                      e.toString()
                                   );
    }
  }


  /**
   * Sending a command to Python Debugger side
   */
  public void sendCommand( String cmd ) throws PythonDebugException
  {
    if (_cmdStream == null)
      return; // not yet launched
    try
    {
      _cmdStream.write( cmd + "\n" );
      _cmdStream.flush();
    }
    catch (IOException e)
    {
      throw new PythonDebugException( "Socket Write Command error " +
                                      e.toString()
                                   );
    }
  }


  /**
   * called back when completion daemon launcher is started
   *
   * @param connectingPort connecting port 
   */
  public boolean canConnect( int connectingPort )
  {
    if (connectingPort == -1)
      _completionDaemon = null; // reset on error
    else
    {
      // proceed with initial connection
      try
      {
        _codePage   = System.getProperty( _ENCODING_PROPERTY_ );
        _connection = new Socket( _LOCALHOST_, connectingPort );
        _cmdStream  = new BufferedWriter(
                                         new OutputStreamWriter( _connection.getOutputStream(),
                                                                 _codePage
                                                              )
                                      );
        _answStream = new BufferedReader(
                                         new InputStreamReader( _connection.getInputStream(),
                                                                _codePage
                                                             )
                                      );

        String wellcome = getMessage();
        if (wellcome.equals( _WELCOME_ ))
          return true ;
        errorHandler("CompletionClient" , "bad completion ACK=" + wellcome) ;
      }
      catch (PythonDebugException e)
      {
        errorHandler("CompletionClient" , e.getMessage()) ;
      }
      catch (UnsupportedEncodingException e)
      {
        errorHandler("CompletionClient" , "unsupported encoding=" + e.getMessage()) ;
      }
      catch (IOException e)
      {
        errorHandler("CompletionClient" , "Socket Write Command error " + e.getMessage()) ;
      }
    }
    return false ;
  }


  /**
   * Initial launch of completion engine + connection
   *
   * @param  connectingPort listening completion port
   *
   * @throws PythonDebugException when something goes wrong
   */
  public void init( int connectingPort ) throws PythonDebugException
  {
    // let's make a try mainly to be able to launch completion daemon
    // by hand for debugging
    if ( canConnect( connectingPort) )
      return ;      
    // check for completion daemon launching first
    if (_completionDaemon == null)
    {
      _completionDaemon =
        CompletionDaemon.launchCompletion( connectingPort );
      if ( _completionDaemon != null )
        canConnect(connectingPort) ;          
    }
  }
  
  private String parseDictValue( String toParse )
  {
  StringBuffer returned = new StringBuffer() ; 
  int pos = 0 ;
     if ( toParse.charAt(pos) == '\'' )
     {
       pos++ ;
       while( toParse.charAt(pos) != '\'' )
           returned.append(toParse.charAt(pos++ )) ; 
     }  
     else if ( toParse.charAt(pos) == '"' )
     {
       pos++ ;
       while( toParse.charAt(pos ) != '"' )
           returned.append(toParse.charAt(pos++ )) ;   
     }  
     else 
       while ( ( toParse.charAt(pos) != ',' )  &&
               ( toParse.charAt(pos) != '}' )
             )
         returned.append(toParse.charAt(pos++)) ;
     if ( returned.toString().equals( _NONE_) )
       return null ;
     return returned.toString() ;
  }

  private String parseDictElement( String toParse ,String name )
  {
  int limit = toParse.indexOf('[') ; // scope limit to any children scope start
  if ( limit != -1 )
    toParse = toParse.substring(0,limit) ;
  _elementPos = toParse.indexOf(name) ;
  String returned = null ;
    if ( _elementPos != -1 )
    {
      _elementPos += name.length()  ;
      returned = parseDictValue( toParse.substring( _elementPos  ) ) ;
      if ( returned == null )
        _elementPos += _NONE_.length() + 1 ; // add one for ' ending quote'
      else  
        _elementPos += returned.length() + 1 ; // add one for ' ending quote'
    }    
    return returned ;
  }
  
  private boolean matchNode ( String toMatch , String matcher )
  {
    if ( matcher.length() > 0 )
    {  
      if ( toMatch.startsWith(matcher) )
        return true ; 
    }    
    return false ;
  }
  
  private int parseDict( String toParse , int type )
  {
  //int endScope = toParse.indexOf('}') ;
  //String lParse = toParse.substring(0,endScope) ; 
  String name = parseDictElement( toParse , "'name':" ) ;      
  String doc = parseDictElement( toParse , "'doc':" ) ;      
  String args = parseDictElement( toParse , "'args':" ) ; 
  String startLine = parseDictElement( toParse , "'startline':" ) ;
  String endLine = parseDictElement( toParse , "'endline':" ) ;
  int startLineNo = -1 ;
  
    if ( startLine != null )
      startLineNo = Integer.parseInt(startLine) ;
  
    switch ( type )
    {
      case PythonSyntaxTreeNode.CLASS_TYPE :
        _curNode = _topNode.addClass( startLineNo , name , doc ); 
        break ; 
        
        
      case PythonSyntaxTreeNode.MODULE_TYPE :
        if ( _topNode == null )
        {  
          _curNode = new PythonSyntaxTreeNode( type ,
                                               0 ,
                                               name ,
                                               doc
                                             ) ; 
          _topNode = _curNode ; 
        }
        else 
          _curNode = _topNode.addModule( startLineNo , name, doc ) ;
        break ;
        
      case PythonSyntaxTreeNode.METHOD_TYPE :
        _curMethod = _topNode.addMethod( startLineNo ,
                                         name ,
                                         doc 
                                       ) ; 
        if ( args != null )
        {  
          // populate arguments
          System.out.println("args=" + args) ;
          int posComma = args.indexOf(',') ;
          while ( posComma != - 1 )
          {
          String argName = args.substring(0,posComma) ;
            _curMethod.addArgument(argName,startLineNo) ;
            args = args.substring(posComma+1) ;
            posComma = args.indexOf(',') ;
          }  
          _curMethod.addArgument(args,startLineNo) ;
        }
        break ;
        
      case PythonSyntaxTreeNode.VARIABLE_TYPE :
        _topNode.addField(startLineNo , name , doc ) ;
        break ; 
    }
 
    return _elementPos ;
  }
  
  private String parseChildren( String toParse , int type  )
  {
  String newParse = toParse ;   
    if ( toParse.charAt(0) == '[')
    {
      // push parent node for later restore
      _parentStack.push( _topNode );
      _topNode = _curNode ;
      char childEnd = toParse.charAt(0) ;
      while ( childEnd != ']' )
      {  
        newParse = newParse.substring(1)  ; // bypass ',' 
        newParse = parse( newParse ) ;
        childEnd = newParse.charAt(0) ;
      }  
      // pop parentNode back
      _topNode = (PythonSyntaxTreeNode)_parentStack.pop();
    }
    return newParse ;
  } 
  
  /**
   * parse completion string as PythonSyntaxTreeNodes list components
   *
  */
  private String parseElements( String toParse , int type  )
  {
    if ( toParse.charAt(0) == '{' )
    {    
      toParse = toParse.substring(1) ;
      int pos = parseDict( toParse ,type ) ;
        while ( ( pos+2 < toParse.length() ) &&
                ( toParse.charAt(pos+1) == ',' ) 
              )  
        {    
          toParse = toParse.substring(pos+2) ;

          if ( toParse.startsWith(_CHILDREN_) )
            return parseChildren( toParse.substring(_CHILDREN_.length() ),  type ) ;  
          else if ( isType( toParse ) )
            return parse( toParse ) ; // parse consecutive 
          else
            pos =  parseDict( toParse ,  type) ;    
        }  
              
        if ( ( pos+2 < toParse.length() )  )
          // bypass last 
          toParse = toParse.substring(pos+2) ;
          
    }
    else if ( toParse.equals(_NO_SUGGESTION_) )
        _topNode = new PythonSyntaxTreeNode( PythonSyntaxTreeNode.NOSUGGESTION_TYPE ,
                                                  0 ,
                                                  _NO_SUGGESTION_ ,
                                                  ""
                                                ) ;
    return toParse ;
  }
  
  private boolean isType( String toParse )
  {
      if ( toParse.startsWith(_CLASSHEADER_ ) ||
           toParse.startsWith(_VARHEADER_ ) ||
           toParse.startsWith(_FUNCHEADER_ ) 
         )
        return true ;
      return false ;
  }
  
  public String parse( String toParse )
  {
    System.out.println("toParse ="+ toParse) ; 
    int type = PythonSyntaxTreeNode.NOSUGGESTION_TYPE ;
    if ( toParse.startsWith(_CLASSHEADER_) )
    {
      toParse = toParse.substring(_CLASSHEADER_.length()) ;    
      type = PythonSyntaxTreeNode.CLASS_TYPE ; 
    }   
    else if ( toParse.startsWith(_VARHEADER_) )
    {
      toParse = toParse.substring(_VARHEADER_.length()) ;    
      type = PythonSyntaxTreeNode.VARIABLE_TYPE ; 
    }
    else if ( toParse.startsWith(_FUNCHEADER_) )
    {
      toParse = toParse.substring(_FUNCHEADER_.length()) ;    
      type = PythonSyntaxTreeNode.METHOD_TYPE ;
    }
    else if ( toParse.startsWith(_MODULEHEADER_) )
    {
      toParse = toParse.substring(_MODULEHEADER_.length()) ;    
      type = PythonSyntaxTreeNode.MODULE_TYPE ;
    }
    return parseElements(toParse,type) ;
  }

  /**
   * autocompletion list builder 
   *
   * @param context    completion context to resolve
   * @param memberList returned completion list of PythonSyntaxTreeNodes
   */
  public void match( String context,  String curSource , int curLine )
  throws PythonDebugException
  {    
    if ( _connection != null  )
    {    
    StringBuffer command = new StringBuffer() ;
      _topNode = null ; // cleanup previous competion tree if any
      command.append(_COMPLETE_) ; 
      command.append(curSource) ;
      command.append(' ') ;
      command.append( Integer.toString(curLine) ) ;
      command.append(' ') ;
      command.append(context) ; 
      sendCommand( command.toString() ) ; 
      String returned = getMessage() ; 
      String matcher = context.substring( context.indexOf('.')+1 ) ;
      if ( returned == null )
        errorHandler( "Completionclient.match" , "Completion Daemon failure : returned null message back");
      else   
        parse( returned.trim() ) ;
    }  
  }


  /**
   * close completion connection
   *
   * @throws PythonDebugException when soemthing wrong happens
   */
  public void terminate() throws PythonDebugException
  {
    if (_connection != null)
    {
      try
      {
        _connection.close();
      }
      catch (IOException e)
      {
        throw new PythonDebugException( "completion terminate error :" + e.getMessage() );
      }
    }
  }


  /**
   * test main
   *
   * @param args DOCUMENT ME!
   */
  public static void main( String[] args )
  {
    System.out.println( "completion client test" );

    CompletionClient client = new CompletionClient();
    try
    {
      client.init( 29001 );
      client.sendCommand( "COMPLETE D:\\sourceforgecvs\\jpydebugforge\\src\\python\\jpydbg\\inspector.py 363 self " );

      String returned = client.getMessage();
      System.out.println( "returned =" + returned );
      client.parse( returned   ) ;
      
      client.sendCommand( "COMPLETE D:\\sourceforgecvs\\jpydebugforge\\src\\python\\jpydbg\\inspector.py 467 self." );

      returned = client.getMessage();
      System.out.println( "returned =" + returned );
      client.parse( returned   ) ;
      
      client.sendCommand( "COMPLETE D:\\sourceforgecvs\\jpydebugforge\\src\\python\\jpydbg\\firstsample.py 32 socket." );

      returned = client.getMessage();
      System.out.println( "returned =" + returned );
      client.parse( returned ) ;
      client.terminate();
    }
    catch (PythonDebugException e)
    {
      System.out.println( "error :" + e.getMessage() );
    }
  }


}
