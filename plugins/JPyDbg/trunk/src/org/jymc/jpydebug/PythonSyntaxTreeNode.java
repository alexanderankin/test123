/**
* Copyright (C) 2003,2004 Jean-Yves Mengant
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


/**
 * @author jean-yves
 *
 * Python SyntaxTree main class
 *  
 */
public class PythonSyntaxTreeNode
{
   public final static int MODULE_TYPE   = 0 ; 
   public final static int CLASS_TYPE   = 1 ; 
   public final static int METHOD_TYPE  = 2 ; 
   public final static int ARG_TYPE = 3 ; 
   public final static int IMPORT_TYPE = 4 ; 
   public final static int VARIABLE_TYPE = 5 ; 
   public final static int NOSUGGESTION_TYPE = 6 ; 
   
   private final static String _EMPTY_ = "" ;
   private final static String _DOT_   = "." ; 
    
   private PythonSyntaxTreeNode _declaringClass = null ; 
   private String  _nodeName ; 
   private int     _type     ;
   private int     _lineNumber ; 
   private String  _doc        ; 
   private String  _location   ;

   /** loading failures are stored here */
   private String _loadError = null ; 
   
   /** Modules imports node */
   private PythonSyntaxTreeNode _imports = null ; 
   /** IMPORT list of modules */
   private Hashtable _moduleList = null ; 
   
   /** used for module type only */
   private Hashtable _classList = null ; 

   /** used for methods/fields */
   private Hashtable _elementList = null ;
   
   /** used for methods or args node entries */
   private Vector _argList = null ;
   
   /** used for var fields node entries */
   private Vector _fields = null ;
   
   public boolean isMethod()
   { 
     if ( _type == METHOD_TYPE )
       return true ; 
     return false ; 
   } 
   
   public boolean isClass()
   { 
     if ( _type == CLASS_TYPE )
       return true ; 
     return false ; 
   } 
   
   public boolean isModule()
   { 
     if ( _type == MODULE_TYPE )
       return true ; 
     return false ; 
   } 
   
   public boolean isField()
   { 
     if ( _type == VARIABLE_TYPE )
       return true ; 
     return false ; 
   } 
   
   public String get_nodeName()
   { return _nodeName ; }
   public void set_nodeName(String nodeName) 
   { _nodeName = nodeName ; }
   
   public String toString()
   { return _nodeName ; }
   
   public String get_loadError()
   {return _loadError ; }
   
   public int get_type()
   { return _type ; }
   
   public int get_lineNumber()
   { return _lineNumber ; }
   
   public String get_doc()
   { return _doc ; }
   
   public Hashtable get_methodList()
   { return _elementList ; }
   public Enumeration get_methodElements()
   {
     if ( _elementList != null )
       return _elementList.elements() ;
     return null ;
   }
   public boolean hasMethods()
   {
     if ( _elementList != null )
       return true ;
     return false ; 
   }
   
   public Enumeration get_fields()
   { 
     if ( _fields != null )
       return _fields.elements() ;
     return null ; 
   }
   public boolean hasFields()
   {
     if ( _fields != null )
       return true ;
     return false ; 
   }
   
   public Hashtable get_classList()
   { return _classList ; }
   public Enumeration get_classElements()
   { 
     if ( _classList != null )
       return _classList.elements() ; 
     return null ;
   }
   public boolean hasClass()
   {
     if ( _classList != null )
       return true ;
     return false ; 
   }
   
   public Hashtable get_moduleList()
   { return _moduleList ; }
   public Enumeration get_moduleElements()
   { 
     if ( _moduleList != null )
       return _moduleList.elements() ;
     return null ; 
   }
   
   public Vector get_argList()
   { return _argList ; }
   public Enumeration get_argElements()
   {
     if ( _argList != null )
       return _argList.elements() ;
     return null ;
   }
   public boolean hasArgs()
   {
     if ( _classList != null )
       return true ;
     return false ; 
   }
   
   public PythonSyntaxTreeNode get_imports()
   { return _imports ; }
   
   public String get_location()
   { return _location ; }
   public void set_location( String location)
   { _location = location ; } 
   
   
   public String get_declaringClassName()
   {
     if ( _declaringClass == null )
       return null ; 
     return _declaringClass._nodeName ; 
   }
   
   public PythonSyntaxTreeNode( int type ,
                                int line , 
                                String name ,
                                String doc
                              )
   {
     _type = type ;
     _lineNumber = line ; 
     _nodeName = name ;
     if ( _type == MODULE_TYPE )
     {  
       _location = doc ; 
       _doc = _EMPTY_ ;
     }  
     else
       _doc = doc ; 	
     /*_doc = doc ; */
   }
   
   /** add a new class to _classList */
   public PythonSyntaxTreeNode addClass( int line ,
					 String name ,
					 String doc 
					  )   
   {
	PythonSyntaxTreeNode returned = new PythonSyntaxTreeNode( 
					 CLASS_TYPE ,
					 line ,
					 name , 
					 doc
					 ) ;	
	if ( _classList == null )
	  _classList = new Hashtable() ;
	_classList.put( name , returned ) ; 
	return returned ;   	
   }
   
   /** add a new field to _classList */
   public PythonSyntaxTreeNode addField( int line ,
					      String name ,
					      String doc 
					    )   
   {
	PythonSyntaxTreeNode returned = new PythonSyntaxTreeNode( 
					 VARIABLE_TYPE ,
					 line ,
					 name , 
					 doc
					 ) ;	
	if ( _fields == null )
	  _fields = new Vector() ;
	_fields.add( returned ) ; 
	return returned ;   	
   }
   
   
   /** add a new Import set to module */
   public PythonSyntaxTreeNode addImport( int line , String name )   
   {
     _imports =  new PythonSyntaxTreeNode( IMPORT_TYPE  ,
                                       line ,
                                       name ,
                                       _EMPTY_
                                     ) ; 
     return _imports ; 
   }
   
   public boolean hasArguments()
   {
     if ( _argList == null )
       return false ; 
     return true ; 
   }
   
   /** add a new imported module */
   public PythonSyntaxTreeNode addModule( int line , String name , String location)
   {
   PythonSyntaxTreeNode node = new PythonSyntaxTreeNode( MODULE_TYPE  ,
                                                         line ,
                                                         name ,
                                                         location
                                                       ) ; 
     if ( _moduleList == null )
       _moduleList = new Hashtable() ;
     _moduleList.put( name , node ) ;
     return node ;
   }
   
   public void addArgument( String name , int lineNo  )
   {
     if ( _argList == null )	 
       _argList = new Vector() ; 
       
   	 _argList.add( new PythonSyntaxTreeNode (  ARG_TYPE ,
   	                                           lineNo ,
   	                                           name ,
   	                                           null  
   	                                        ) 
   	             ); 
   }
   
   /** add a new method to _elementList */
   public PythonSyntaxTreeNode addMethod( int line ,
					  String name ,
					  String doc   
					)   
   {
   PythonSyntaxTreeNode returned = new PythonSyntaxTreeNode( 
					 METHOD_TYPE ,
					 line ,
					 name , 
					 doc
							 ) ;	
															 
	if ( _elementList == null )
	  _elementList = new Hashtable() ;
	_elementList.put( name , returned ) ; 
	return returned ;   	
   }
   
   private PythonSyntaxTreeNode matchName( Enumeration enm , Collection list , String match )
   {
   PythonSyntaxTreeNode last = null ; 
   
     while ( enm.hasMoreElements())
     {
       PythonSyntaxTreeNode cur = (PythonSyntaxTreeNode) enm.nextElement() ; 
       if ( cur.get_nodeName().startsWith(match))
       {
         last = cur ;
         list.add(cur) ;
       }  
     }
     return last  ;
   }
   
   public boolean hasLoaded()
   {
     if ( (_type == MODULE_TYPE ) && 
          ( _moduleList == null ) &&
          ( _classList == null )
        )
      return false ;
    return true ; 
   }
   
   public PythonSyntaxTreeNode reload()
   {
   PythonPath path = new PythonPath() ;  
     
     path.readPath(PythonDebugParameters.get_jythonActivated()) ;  // populate path list
     PythonPathElement toLoad = path.locatePythonSource(_nodeName) ;
     if ( toLoad != null )
     {
       try {
         // proceed with source loading through inspector
         PythonSyntaxTreeNode returned = PythonInspector.launchInspector( toLoad.get_candidate() , false ) ; 
         // proveed with PylintInspector next if requested
         PylintInspector.launchPyLint(toLoad.get_candidate() ) ;
         return returned ; 
       } catch (PythonDebugException e)
       { _loadError = e.getLocalizedMessage() ;  }
     }  
     return null ; 
   }
   
   private PythonSyntaxTreeNode matchImported( String match , Collection list)
   {
     if ( ( _type == MODULE_TYPE ) && ( _imports != null ) )
     {
     Hashtable modTable = _imports.get_moduleList() ; 
     Enumeration modlist = modTable.elements() ; 
       while ( modlist.hasMoreElements() )
       {
       PythonSyntaxTreeNode cur = (PythonSyntaxTreeNode) modlist.nextElement() ;  
         if ( ! cur.hasLoaded() )
         {  
           // proceed with load and semantics resolutions
           cur = cur.reload() ;
           modTable.put(cur.get_nodeName() , cur) ;
         }  
         if ( cur.get_nodeName().equals(match) )
           // module matching 
           return cur ; 
         // try to match class 
         if ( _classList != null )
         {  
           Enumeration classes = get_classList().elements() ; 
           PythonSyntaxTreeNode parent =  matchName(classes , list , match ) ; 
           return parent ; 
         }  
       }  
     }
     return null ; 
   }
   
   /**
    * try to match provided name :
    * (class if this is MODULE_TYPE , method if this is CLASS_TYPE)
    * @param match
    * @param fully
    */
   public PythonSyntaxTreeNode  match(  String match , Collection list )
   {
   int dot = match.indexOf( _DOT_) ;
   String className = null ; 
   String methodName =null ;
     if ( dot == -1  )
       methodName = match ; 
     else
     {
       className = match.substring(0,dot) ; 
       methodName = match.substring(dot+1) ; 
     }
     if ( className == null )
     {
     // match locally  
       if ( get_methodList() != null )
       {    
       Enumeration methods = get_methodList().elements() ; 
       PythonSyntaxTreeNode matchMethod = matchName(methods , list , methodName) ; 
         if ( matchMethod != null )
           // add class or module parent matching methodto list
           list.add(this) ;
         return matchMethod ;
       }
       else 
         className = match ;   
     }
     Enumeration classes = get_classList().elements() ; 
     PythonSyntaxTreeNode parent =  matchName(classes , list , className ) ; 
     if ( parent != null )
       // match through local classes
       return parent.match(methodName,list) ; 
     // match through imported method
     PythonSyntaxTreeNode module = parent.matchImported(className,list) ; 
     if ( module != null )
       // locate method inside found class
       return module.match(methodName , list) ; 
     return null ; 
   }
   
}
