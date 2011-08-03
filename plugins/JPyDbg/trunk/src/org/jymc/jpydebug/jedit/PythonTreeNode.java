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
package org.jymc.jpydebug.jedit;

import javax.swing.Icon;
import javax.swing.ImageIcon;

//import org.gjt.sp.jedit.Buffer;
import org.jymc.jpydebug.PythonSyntaxTreeNode;
import org.jymc.jpydebug.jedit.ImportNavigator;

//import sidekick.Asset;

/**
 * Python semantic node used by JpyDbg misc tree structures
 * @author jean-yves
 */
public class PythonTreeNode
extends JpyDbgAsset
{

  private final static JpyDbgPluginTest _DUMMY_ = new JpyDbgPluginTest() ;
  
  private final static ImageIcon _CLASS_ICON_  =  new ImageIcon(
								  _DUMMY_.getClass().getResource("images/class.gif") ,
																 "class"
																 ) ;

  private final static ImageIcon _METHOD_ICON_  =  new ImageIcon(
									_DUMMY_.getClass().getResource("images/method.gif") ,
																   "method"
																   ) ;

  private final static ImageIcon _PARAM_ICON_  =  new ImageIcon(
									_DUMMY_.getClass().getResource("images/param.gif") ,
																   "param"
																   ) ;

  private final static ImageIcon _MODULE_ICON_  =  new ImageIcon(
		_DUMMY_.getClass().getResource("images/module.gif") ,
									   "module"
									   ) ;

  private final static ImageIcon _BUILTIN_MODULE_ICON_  =  new ImageIcon(
		_DUMMY_.getClass().getResource("images/builtinmodule.gif") ,
									   "builtinmodule"
									   ) ;

  private final static ImageIcon _BINARY_MODULE_ICON_  =  new ImageIcon(
		_DUMMY_.getClass().getResource("images/binarymodule.gif") ,
									   "binmodule"
									   ) ;

  private final static ImageIcon _MODULE_LOADERR_ICON_ =  new ImageIcon(
		_DUMMY_.getClass().getResource("images/moduleloaderror.gif") ,
									   "module"
									   ) ;

  private final static ImageIcon _IMPORTS_ICON_  =  new ImageIcon(
		_DUMMY_.getClass().getResource("images/imports.gif") ,
									   "imports"
									   ) ;

	
  
  private Icon _icon ;
  private String _longText ;  
  protected String _shortText ;
  private int _lineNo ;   
  private int _type ;
  protected String _sourceName ; 
  protected PythonSyntaxTreeNode _node ; 
  private boolean _hasLoadFailed = false ; 
  private boolean _binary = false ; 
  private boolean _builtin = false ; 
	    
  
    public void set_hasLoadfailed( boolean failed )
    { 
      _hasLoadFailed = failed ; 
      if ( _hasLoadFailed )
        _icon = _MODULE_LOADERR_ICON_ ; 
      else 
      {  
        if ( _binary )  
          _icon = _BINARY_MODULE_ICON_ ;
        else if ( _builtin )
          _icon = _BUILTIN_MODULE_ICON_ ; 
        else  
          _icon = _MODULE_ICON_ ; 
      }  
    }
    
    public void set_binary( boolean binary )
    { 
      _binary = binary ; 
      _icon = _BINARY_MODULE_ICON_ ;
    }
    public boolean is_binary()
    { return _binary ; }
    
    public boolean isSource()
    {
      if ( _binary || _hasLoadFailed || _builtin )
        return false ;
      return true ; 
    }
    
    public boolean hasLoadFailed()
    { return _hasLoadFailed ; }
    
    public void set_builtin( boolean builtin )
    { 
      _builtin = builtin ; 
      _icon = _BUILTIN_MODULE_ICON_ ;
    }
    public boolean is_builtin()
    { return _builtin;}
    
     
    public String get_location() 
    { return _node.get_location() ; }
    
    private void init(String sourceName , PythonSyntaxTreeNode node)
    {
      _type = node.get_type() ; 
      _sourceName = sourceName ; 
      _node  = node ; 
    	
	  switch ( _type )
	  {
		case PythonSyntaxTreeNode.ARG_TYPE :
		  _icon = _PARAM_ICON_ ; 
		  break ; 
		case PythonSyntaxTreeNode.CLASS_TYPE :
		  _icon = _CLASS_ICON_ ; 
		  break ; 
		case PythonSyntaxTreeNode.METHOD_TYPE :
		  _icon = _METHOD_ICON_ ; 
		  break ; 
		case PythonSyntaxTreeNode.MODULE_TYPE :
		    _icon = _MODULE_ICON_ ; 
		  break ; 
		case PythonSyntaxTreeNode.IMPORT_TYPE :
		  _icon = _IMPORTS_ICON_ ; 
		  break ; 
		default :
		  _icon = null ; 	
		  break ; 
	  }  	
	  _shortText = node.get_nodeName() ; 
	  _longText = node.get_doc() ;   
	  if ( _longText == null )
		_longText = _shortText ; 
	  _lineNo = node.get_lineNumber() ;
	  if ( _lineNo > 0 )
		_lineNo-- ;  
    }
  
    /**
     * Import Navigator constructor
     * @param importNode source syntax tree root
     */
    public PythonTreeNode( PythonSyntaxTreeNode importNode )
    {
      super(importNode.get_nodeName()) ;
      init(null,importNode) ; 
    }
  
    public void identify()
    {
      if ( get_location() == null ) 
        _node.set_location(_sourceName) ;
      String full = _node.get_location() ; 
      if ( full.equals(ImportNavigator.BUILTIN ) )
        set_builtin(true) ; 
      else if ( full.equals( ImportNavigator.IMPORT_FAILURE ) )
        set_hasLoadfailed(true) ; 
      else         
      {
        if ( !( full.endsWith(".pyc" ) || full.endsWith(".py")) )
          set_binary(true) ;
      }
    }
    
    
    /**
     * Sidekick constructor 
     *
     * @param sourceName name of the candidate python source file
     * @param node synatx node root 
     */
    public PythonTreeNode( String sourceName , PythonSyntaxTreeNode node )	
    {
      super( node.get_nodeName() ) ;
      init(sourceName , node) ; 
    }
    
    public Icon getIcon()
    { return _icon ; }	
    
    public String getLongString()
    {return _longText ;  }
	  
    public String getShortString()
    {return _shortText ; }
  
    public int get_type()
    { return _type ;  }
    
    public String getSourceName()
    { return _sourceName ; }
    public int getLineNo()
    { return _lineNo ; }
}
