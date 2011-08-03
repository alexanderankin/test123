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

import sidekick.SideKickParsedData;
import org.jymc.jpydebug.* ;
import javax.swing.tree.* ;
import org.gjt.sp.jedit.* ; 
import java.util.* ; 

/**
 * @author jean-yves
 * 
 * used to populate the sidekick tree with the python parsed 
 * data 
 *
 */
public class PythonParsedData
extends SideKickParsedData
{
   
    
    private void scanElements( Buffer buffer , 
	                       DefaultMutableTreeNode rooot , 
	                       Object list 
                             ) 
    {
      if ( list == null )
        return ; 
	  Enumeration elements = null ;	
      if ( list instanceof Hashtable )
        elements = ((Hashtable)list).elements() ; 
      else if ( list instanceof Vector )  
        elements = ((Vector)list).elements() ; 
      while ( elements.hasMoreElements() )
        populateTree( buffer , rooot , (PythonSyntaxTreeNode)elements.nextElement() ) ; 	
    }
    
    /**
     * recursive way of populating python tree
     * @param buffer
     * @param syntaxTree
     */
    private void populateTree( Buffer buffer , 
	                       DefaultMutableTreeNode rooot , 
	                       PythonSyntaxTreeNode node 
                             )
    {
      if ( node == null )
        return ; 
        
      DefaultMutableTreeNode parent = null ; 
      PythonTreeNode jpNode ; 	    	
      switch ( node.get_type() )
      {	
	case PythonSyntaxTreeNode.MODULE_TYPE :
	  jpNode = new PythonTreeNode(PythonDebugParameters.ideFront.getCurrentSource(),node) ;
	  parent=  new DefaultMutableTreeNode( new  JEditPythonTreeNode(jpNode,buffer) ) ;
	  rooot.add(parent) ;
	  // scan imports 
	  if ( node.get_imports() != null )
	    populateTree( buffer , parent , node.get_imports() ) ;
	  //  scan classes
	  scanElements( buffer , parent , node.get_classList()) ; 
	  //  scan stand alone methods
	  scanElements( buffer , parent , node.get_methodList() ) ; 
	  break ; 
		  
	case PythonSyntaxTreeNode.IMPORT_TYPE :
	  jpNode = new PythonTreeNode(PythonDebugParameters.ideFront.getCurrentSource(),node) ;
	  parent=  new DefaultMutableTreeNode( new  JEditPythonTreeNode(jpNode,buffer)) ;
	  rooot.add(parent) ;
	  //  scan imported modules
	  scanElements( buffer , parent , node.get_moduleList()) ; 
	  break ; 
		  
	case PythonSyntaxTreeNode.CLASS_TYPE :
	  jpNode = new PythonTreeNode(PythonDebugParameters.ideFront.getCurrentSource(),node) ;
	  parent = new DefaultMutableTreeNode( new  JEditPythonTreeNode(jpNode,buffer)) ;
	  rooot.add(parent) ;
	  // scan child methods 
	  scanElements( buffer , parent , node.get_methodList() ) ; 
	  break ; 
		  
	case PythonSyntaxTreeNode.METHOD_TYPE :
	  jpNode = new PythonTreeNode(PythonDebugParameters.ideFront.getCurrentSource(),node) ;
	  parent = new DefaultMutableTreeNode(new  JEditPythonTreeNode(jpNode,buffer)) ;
	  rooot.add(parent) ;
	  // scan arguments methods 
	  scanElements( buffer , parent , node.get_argList() ) ; 
	  break ; 
		
	case PythonSyntaxTreeNode.ARG_TYPE :
	  jpNode = new PythonTreeNode(PythonDebugParameters.ideFront.getCurrentSource(),node) ;
	  parent = new DefaultMutableTreeNode(new  JEditPythonTreeNode(jpNode,buffer));
	  rooot.add(parent) ;
          break ; 
      }	
    }

    public PythonParsedData ( Buffer buffer , PythonSyntaxTreeNode syntaxTree )
    {	  	
      super( syntaxTree.get_nodeName() + ".py" ) ; 
      System.out.println("entering PythonSidekick") ; 
      populateTree(buffer,root,syntaxTree) ; 	  
    }
	
    /** used only for dummy context allocation */
    public PythonParsedData ()
    {
      super( "" ) ; 	
    }

}
