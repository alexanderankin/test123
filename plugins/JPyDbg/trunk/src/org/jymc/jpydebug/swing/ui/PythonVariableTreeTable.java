/**
* Copyright (C) 2003,2004,2005 Jean-Yves Mengant
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

package org.jymc.jpydebug.swing.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;

import org.jymc.jpydebug.utils.* ; 
import org.jymc.jpydebug.* ; 



/**
 * @author jean-yves
 *
 * replacing Variable Table Display by a tree table in order to be able to get
 * a better display of the complex python data structure this class is planned to replace 
 * the PythonVariableTable class previously implemented
 *
 */
public class PythonVariableTreeTable
extends JPanel
{
  private final static PythonVariableTreeTable _DUMMY_ = new PythonVariableTreeTable() ;
  
  public final static ImageIcon ROOTVAR_ICON  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/python16.jpg") ,
                             "python16"
                             ) ;
  public final static ImageIcon COMPOSITEVAR_ICON  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/compositevar.gif") ,
                             "compositevar"
                             ) ;

  public final static ImageIcon LEAFVAR_ICON  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/leafvar.gif") ,
                             "leafvar"
                             ) ;
  
  private final static ImageIcon _MODULE_ICON_  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/module.gif") ,
                             "module"
                             ) ;
  private final static ImageIcon _METHOD_ICON_  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/method.gif") ,
                             "method"
                             ) ;
  private final static ImageIcon _CLASS_ICON_  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/class.gif") ,
                             "class"
                             ) ;
  private final static ImageIcon _MAP_ICON_  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/pymap.gif") ,
                             "pymap"
                             ) ;
  private final static ImageIcon _LIST_ICON_  =  new ImageIcon(
      _DUMMY_.getClass().getResource("images/pylist.gif") ,
                             "pylist"
                             ) ;
  private final static String _FUNCTION_ = "<function" ;  
  private final static String _UNBOUND_ = "<unbound method" ;  
  private final static String _BOUND_ = "<bound method" ;  
  private final static String _METHOD_ = "<method" ;  
  private final static String _METHOD_WRAPPER_ = "<method-wrapper" ;  
  private final static String _MODULE_ = "<module" ;  
  private final static String _BUILTIN_ = "<built-in" ;  
  private final static String _CLASS_ = "<class" ;  
  private final static String _MAP_ = "MAP" ; 
  private final static String _LIST_ = "LIST" ; 
  
  private final static String _EMPTY_ = "" ;
  private JTreeTable _table ; 
  private JTree _tree ; 
  private PythonVariableTreeTableModel _model;
  private PythonContainer _parent ; 
  private PythonVariableTreeDataNode _rootNode = null ; 
  /** true when table conatins global variables references */
  private boolean _global = false ;
  private _EXPANDER_ _expander ; 
  
  class _TREE_RENDERER_
  extends DefaultTreeCellRenderer
  {
    private boolean isMethodOrFunction( String curVal )
    {
      if ( curVal.startsWith(_UNBOUND_) || 
           curVal.startsWith(_BOUND_) || 
          curVal.startsWith(_METHOD_) || 
           curVal.startsWith(_METHOD_WRAPPER_) || 
           curVal.startsWith(_FUNCTION_)  ||
           curVal.startsWith(_BUILTIN_) 
         )    
        return true ; 
      return false ; 
    }
    
    private boolean isModule( String curVal )
    {
      if ( curVal.startsWith(_MODULE_ ))
        return true ; 
      return false ; 
    }
    
    private boolean isClass( String curVal )
    {
      if ( curVal.startsWith(_CLASS_ ))
        return true ; 
      return false ; 
    }
    
    private boolean isList( String curType )
    {
      if ( curType.equals(_LIST_ ) )
        return true ; 
      return false ; 
    }
    
    private boolean isMap( String curType )
    {
      if ( curType.equals(_MAP_ ) )
        return true ; 
      return false ; 
    }
    
    public Component getTreeCellRendererComponent( JTree tree , 
                                                   Object value ,
                                                   boolean mselected ,
                                                   boolean expanded ,
                                                   boolean leaf ,
                                                   int row ,
                                                   boolean mhasFocus
                                                   
                                                 )
    {
    PythonVariableTreeDataNode cur = (PythonVariableTreeDataNode)value ;  
    String curVal = cur.get_varContent() ; 
      this.setText(cur.get_varName()) ;
      if ( isModule(curVal) )
        this.setIcon(_MODULE_ICON_) ; 
      else if ( isMethodOrFunction(curVal) )
        this.setIcon(_METHOD_ICON_) ; 
      else if ( isClass(curVal) )
        this.setIcon(_CLASS_ICON_) ; 
      else if ( isList(cur.get_varType() ) )
        this.setIcon(_LIST_ICON_) ; 
      else if ( isMap(cur.get_varType() ) )
        this.setIcon(_MAP_ICON_) ; 
      else
      {  
        if ( cur.get_parent() == null )
          this.setIcon(ROOTVAR_ICON) ;
        else if ( cur.isLeaf() )
          this.setIcon(LEAFVAR_ICON) ;
        else 
          this.setIcon(COMPOSITEVAR_ICON) ; 
      }
      return this ;
    }
  }
  
  class  _EXPANDER_ 
  implements TreeExpansionListener ,
             CompositeCallback
  {
    private PythonVariableTreeDataNode _curNode ; 
    private Hashtable _expanded = new Hashtable() ;
    
    private String getExpandedName( PythonVariableTreeDataNode node )
    {
    StringBuffer pathName = new StringBuffer() ;
    PythonVariableTreeDataNode path[] = node.getPathToRoot(node,0)   ; 
       for ( int ii = 1 ; ii < path.length ; ii++ )
       {    
         pathName.append ( path[ii].get_varName() );   
         if ( ii+1 != path.length )
             pathName.append('.') ;
       }
       return pathName.toString() ;
    }
    
    public void treeExpanded( TreeExpansionEvent evt )
    {
      System.out.println("expending tree node") ;
      TreePath candidate = evt.getPath() ; 
      _curNode = (PythonVariableTreeDataNode)candidate.getLastPathComponent() ;
      String pathName = getExpandedName(_curNode) ;
      _expanded.put( pathName , pathName );
      if ( _parent != null )
      {  
        // check if children has already expanded 
        // Assume not necessary to expand twice
        if ( ! _curNode.hasChildren() )
        {  
          // lookup variable content
          String pythonVarName = PythonVariableTreeDataNode.buildPythonName(_curNode.getPath()) ;
          _parent.inspectCompositeCommand(this,pythonVarName ) ; 
        }
      }  
    }
    
    public boolean hasExpanded( PythonVariableTreeDataNode node )
    {
    String key = getExpandedName(node)  ;
       if ( _expanded.get(key) != null ) 
           return true ;
       return false ; 
    }
    
    public void treeCollapsed( TreeExpansionEvent evt )
    {
      System.out.println("collapsing tree node") ; 
      TreePath candidate = evt.getPath() ; 
      _curNode = (PythonVariableTreeDataNode)candidate.getLastPathComponent() ;
      String pathName = getExpandedName(_curNode) ;
      _expanded.remove( pathName );
    }
    
    public void callbackWithValuesSet( TreeMap values , TreeMap types )
    {
      System.out.println("comming back from valuation") ; 
      setNodeValue(_curNode,values,types) ;
    }
  }
  
  /** Icon _DUMMY_ initializer use only */
  public PythonVariableTreeTable()
  {}
  
  public PythonVariableTreeTable( boolean global  )
  {
    super(new GridLayout(1,1));
    _global = global ; 
    //Table building
    String title = "Local Python object instances" ; 
    
    //remove previous instance from container if any
    if ( _table != null )
    {  
      super.remove(_table) ; 
      super.invalidate() ; 
    }
    
    if ( _global )
      title = "Global Python object instances"  ;
    _rootNode = PythonVariableTreeDataNode.buildDataNodes( null , 
                                                           title , 
                                                           _EMPTY_ ,
                                                           PythonVariableTreeDataNode.COMPOSITE
                                                         ) ;
    
    _model = new PythonVariableTreeTableModel( 
                    _rootNode ,
                    new String[]{" Object instance name "," Value "} ,
                     _global
                                             );
    _table = new JTreeTable(_model);
    _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF) ; 
    _tree = _table.getTree() ; 
    _tree.setShowsRootHandles(true) ; 
    _tree.setRootVisible(true) ; 
    _tree.setCellRenderer( new _TREE_RENDERER_() ) ; 
    _expander = new _EXPANDER_() ;
    _tree.addTreeExpansionListener( _expander ) ;
    // adjust column sizes to fit container sizes
    _table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    this.add(new JScrollPane(_table), BorderLayout.CENTER );
    
    
  } 
  
  private void setColumnWidth()
  {
  int prefWidth = super.getSize().width / 2 ;
    if ( prefWidth !=  0 )
    {
    
      //Get the column model.
      TableColumnModel colModel = _table.getColumnModel();
      //Get the column at index pColumn, and set its preferred width.
      colModel.getColumn(0).setPreferredWidth(prefWidth);
      colModel.getColumn(1).setPreferredWidth(prefWidth);
    }
  }

  private void checkExpanded( PythonVariableTreeDataNode node )
  {
    if ( _expander.hasExpanded(node) )
    {
      TreePath path = new TreePath(node.getPath()) ;
      if ( ! _tree.isExpanded(path ) )
          _tree.expandPath(path) ;
    }    
    PythonVariableTreeDataNode children[]  = node.get_children() ;
    if ( children != null )
      for ( int ii = 0 ; ii < children.length ; ii++  )
          checkExpanded( children[ii] ) ; 
  }
  
  private void setNodeValue( PythonVariableTreeDataNode node , TreeMap values , TreeMap types )
  {
    node.set_children(values,types) ;
    _model.nodeStructureChange(node) ;   
    // restore expansions 
    checkExpanded(node) ;
  }
  
  public void set_tableValue( TreeMap values , TreeMap types)
  {
    setNodeValue(_rootNode,values,types) ;
    setColumnWidth() ;
    super.invalidate() ;
  }
  
  
  public void set_parent( PythonContainer parent )
  { 
    _parent = parent ; 
    _model.set_parent(parent) ; 
  } 


  
  
   public static void main(String[] args)
   {
     TreeMap testH = new TreeMap() ;
     TreeMap testT = new TreeMap() ;
     testH.put("nom1" , "Valeur3") ; 
     testH.put("nom3" , "Valeur4") ; 
     testH.put("compo1" , "<dbgutils.jpyutils instance at 0x009F1210>") ; 
     testT.put("nom1" , "SIMPLE") ; 
     testT.put("nom3" , "SIMPLE") ; 
     testT.put("compo1" , "COMPOSITE") ; 
     final PythonVariableTreeTable dbg = new PythonVariableTreeTable(false) ; 
     JFrame myFrame = new JFrame(  "simple test" ) ;
     myFrame.setSize(300,200) ;
     dbg.set_tableValue(testH,testT) ; 
     // dbg.set_tableValue(testH) ; 
     
     myFrame.addWindowListener( 
         new WindowAdapter() 
         {  public void windowClosing( WindowEvent e ) 
            { 
              System.exit(0) ;
            }  
         }        
       ) ; 
     myFrame.getContentPane().setLayout( new GridLayout(1,1 ) )  ; 
     myFrame.getContentPane().add(dbg ) ;  
//     myFrame.pack() ; 
     myFrame.setVisible(true) ; 
   }

}
