/*
Copyright (c) 2005, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.
* Neither the name of the <ORGANIZATION> nor the names of its contributors 
may be used to endorse or promote products derived from this software without 
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sidekick.java;

import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import sidekick.java.node.*;
import sidekick.java.options.*;
import sidekick.java.parser.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.util.*;
import errorlist.*;

import sidekick.SideKickCompletion;
import sidekick.SideKickParser;
import sidekick.SideKickParsedData;

public class JavaParser extends SideKickParser implements EBComponent {
    private View currentView = null;
    private JBrowseOptionDialog optionDialog;
    private GeneralOptions options;
    private MutableFilterOptions filterOpt;
    private MutableDisplayOptions displayOpt;
    private boolean sorted = true;      // are the tree nodes sorted by type?

    private Set methods = null;
    private Set fields = null;


    public JavaParser() {
        super( "java" );
        loadOptions();
    }

    private void loadOptions() {
        options = new GeneralOptions();
        options.load( new JEditPropertyAccessor() );
        filterOpt = options.getFilterOptions();
        displayOpt = options.getDisplayOptions();

        // re-init the labeler
        TigerLabeler.setDisplayOptions( displayOpt );
    }


    /**
     * This method is called when a buffer using this parser is selected
     * in the specified view.
     * @param editPane The edit pane
     * @since SideKick 0.3.1
     */
    public void activate( EditPane editPane ) {
        super.activate( editPane );
        currentView = editPane.getView();
        EditBus.addToBus( this );
    }

    public void deactivate( EditPane editPane ) {
        super.deactivate( editPane );
        EditBus.removeFromBus( this );
    }

    public void handleMessage( EBMessage msg ) {
        if ( msg instanceof PropertiesChanged ) {
            loadOptions();
            parse();
        }
    }

    public void parse() {
        if ( currentView != null )
            parse( currentView.getBuffer(), null );
    }

    /**
     * Parse the contents of the given buffer.
     * @param buffer the buffer to parse
     */
    public SideKickParsedData parse( Buffer buffer, DefaultErrorSource errorSource ) {
        // re-init the labeler
        TigerLabeler.setDisplayOptions( displayOpt );

        String filename = buffer.getPath();
        SideKickParsedData spd = new JavaSideKickParsedData( filename );
        DefaultMutableTreeNode root = spd.root;
        if ( buffer.getLength() <= 0 )
            return spd;
        // read the source code directly from the Buffer rather than from the
        // file.  This means:
        // 1) a modifed buffer can be parsed without a save
        // 2) reading the buffer should be faster than reading from the file, and
        // 3) jEdit has that 'gzip file on disk' option which won't parse.
        ByteArrayInputStream input = new ByteArrayInputStream( buffer.getText( 0, buffer.getLength() ).getBytes() );
        TigerParser parser = new TigerParser( input );
        
        try {
            methods = new HashSet();
            fields = new HashSet();
            CUNode cu = parser.CompilationUnit();
            cu.setName( buffer.getName() );
            root.setUserObject( cu );
            if ( cu.getChildren() != null ) {
                Collections.sort( cu.getChildren(), nodeSorter );
                for ( Iterator it = cu.getChildren().iterator(); it.hasNext(); ) {
                    TigerNode child = ( TigerNode ) it.next();
                    
                    switch ( child.getOrdinal() ) {
                        case TigerNode.METHOD:
                            methods.add( child.getName() + "(" );
                            break;
                        case TigerNode.FIELD:
                            fields.add( child.getName() );
                            break;
                    }

                    child.setStart( createStartPosition( buffer, child ) );
                    child.setEnd( createEndPosition( buffer, child ) );
                    if ( canShow( child ) ) {
                        DefaultMutableTreeNode cuc = new DefaultMutableTreeNode( child );
                        root.add( cuc );
                        addChildren( buffer, cuc, child );
                    }
                }
            }
            /* // no place to put the status panel in JavaSideKick :(
            if ( statusPanel != null )
                statusPanel.showResults( parser.getResults() );
            */
            try {
                input.close();
            }
            catch ( Exception e ) {
                // not to worry
            }
        }
        catch ( ParseException e ) {
            ErrorNode eu = new ErrorNode(e);
            eu.setName( buffer.getName() );
            root.setUserObject( eu );
            String msg = e.getMessage();
            boolean isJava = buffer.getName().endsWith( ".java" );
            if ( !isJava )
                msg += ( " - Not a java file?" );
            root.add( new DefaultMutableTreeNode( "<html><font color=red>" + msg ) );
            Location loc = getExceptionLocation( e );
            errorSource.addError( ErrorSource.ERROR, buffer.getPath(), loc.line, loc.column, loc.column, e.getMessage() + ( isJava ? "" : " - Not a java file?" ) );
        }
        handleErrors(errorSource, parser, buffer);
        return spd;
    }
    
    private void handleErrors(DefaultErrorSource errorSource, TigerParser parser, Buffer buffer) {
        for (Iterator it = parser.getErrors().iterator(); it.hasNext(); ) {
            ErrorNode en = (ErrorNode)it.next();
            Exception e = en.getException();
            ParseException pe = null;
            Location loc = new Location(0, 0);
            if (e instanceof ParseException) {
                pe = (ParseException)e;
                loc = getExceptionLocation( pe );
            }
            errorSource.addError( ErrorSource.ERROR, buffer.getPath(), loc.line, loc.column, loc.column, e.getMessage() );
            
        }
    }

    private void addChildren( Buffer buffer, DefaultMutableTreeNode parent, TigerNode tn ) {
        if ( tn.getChildCount() > 0) {
            List children = tn.getChildren();
            Collections.sort( children, nodeSorter );
            for ( Iterator it = children.iterator(); it.hasNext(); ) {
                TigerNode child = ( TigerNode ) it.next();

                switch ( child.getOrdinal() ) {
                    case TigerNode.METHOD:
                        methods.add( child.getName() + "(" );
                        break;
                    case TigerNode.FIELD:
                        fields.add( child.getName() );
                        break;
                }

                child.setStart( createStartPosition( buffer, child ) );
                child.setEnd( createEndPosition( buffer, child ) );
                if ( canShow( child ) ) {
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( child );
                    parent.add( treeNode );
                    if ( child.getChildren() != null && child.getChildren().size() > 0 ) {
                        addChildren( buffer, treeNode, child );
                    }
                }
                else {
                    // need to fill in start and end positions
                    setChildPositions(buffer, child);   
                }
            }
        }
    }
    
    private void setChildPositions(Buffer buffer, TigerNode tn) {
        for (int i = 0; i < tn.getChildCount(); i++) {
            TigerNode child = tn.getChildAt(i);
            child.setStart( createStartPosition( buffer, child ) );
            child.setEnd( createEndPosition( buffer, child ) );
            setChildPositions(buffer, child);
        }
    }

    /**
     * Need to create positions for each node.
     */
    private Position createStartPosition( Buffer buffer, TigerNode child ) {
        final int offset = buffer.getLineStartOffset( Math.max( child.getStartLocation().line - 1, 0 ) ) + child.getStartLocation().column;
        return new Position() {
                   public int getOffset() {
                       return offset;
                   }
               };
    }

    /**
     * Need to create positions for each node.
     */
    private Position createEndPosition( Buffer buffer, TigerNode child ) {
        final int offset = buffer.getLineStartOffset( Math.max( child.getEndLocation().line - 1, 0 ) ) + child.getEndLocation().column;
        return new Position() {
                   public int getOffset() {
                       return offset;
                   }
               };
    }

    /**
     * @return attempts to return a Location indicating the location of a parser
     * exception.  If the ParseException contains a Token reference, all is well,
     * otherwise, this method attempts to parse the message string for the 
     * exception.  
     */
    private Location getExceptionLocation( ParseException pe ) {
        Token t = pe.currentToken;
        if ( t != null ) {
            return new Location( t.next.beginLine - 1, t.next.beginColumn );
        }

        // ParseException message look like: "Parse error at line 116, column 5.  Encountered: }"
        try {
            Pattern p = Pattern.compile( "(.*?)(\\d+)(.*?)(\\d+)(.*?)" );
            Matcher m = p.matcher( pe.getMessage() );
            if ( m.matches() ) {
                String ln = m.group( 2 );
                String cn = m.group( 4 );
                int line_number = -1;
                int column_number = 0;
                if ( ln != null )
                    line_number = Integer.parseInt( ln );
                if ( cn != null )
                    column_number = Integer.parseInt( cn );
                return line_number > -1 ? new Location( line_number - 1, column_number ) : null;
            }
            return new Location( 0, 0 );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return new Location( 0, 0 );
        }
    }

    // single place to check the filter settings, that is, check to see if it
    // is okay to show a particular node
    private boolean canShow( TigerNode node ) {
        if ( !isVisible( node ) )
            return false;
        if ( node.getOrdinal() == TigerNode.BLOCK)
            return false;
        if ( node.getOrdinal() == TigerNode.INITIALIZER )
            return filterOpt.getShowInitializers();
        if ( node.getOrdinal() == TigerNode.EXTENDS )
            return filterOpt.getShowGeneralizations();
        if ( node.getOrdinal() == TigerNode.IMPLEMENTS )
            return filterOpt.getShowGeneralizations();
        if ( node.getOrdinal() == TigerNode.FIELD && filterOpt.getShowFields() ) {
            if ( ( ( FieldNode ) node ).isPrimitive() )
                return filterOpt.getShowPrimitives();
            return true;
        }
        if ( node.getOrdinal() == TigerNode.VARIABLE ) {
            return filterOpt.getShowVariables();
        }
        if ( node.getOrdinal() == TigerNode.THROWS )
            return filterOpt.getShowThrows();
        return true;
    }

    // check if a node should be visible based on the 'top level' or 'member visible' settings
    private boolean isVisible( TigerNode tn ) {
        if ( ( tn.getOrdinal() == TigerNode.CLASS || tn.getOrdinal() == TigerNode.INTERFACE ) && tn.getParent() != null && tn.getParent().getOrdinal() == TigerNode.COMPILATION_UNIT ) {
            int visible_level = filterOpt.getTopLevelVisIndex();
            switch ( visible_level ) {
                case MutableModifier.TOPLEVEL_VIS_PUBLIC:
                    return ModifierSet.isPublic( tn.getModifiers() );

                case MutableModifier.TOPLEVEL_VIS_PACKAGE:
                    return true;
            }
        }

        int visible_level = filterOpt.getMemberVisIndex();
        switch ( visible_level ) {
            case MutableModifier.MEMBER_VIS_PACKAGE:
                return ModifierSet.isPackage( tn.getModifiers() ) || ModifierSet.isProtected( tn.getModifiers() ) || ModifierSet.isPublic( tn.getModifiers() );
            case MutableModifier.MEMBER_VIS_PROTECTED:
                return ModifierSet.isProtected( tn.getModifiers() ) || ModifierSet.isPublic( tn.getModifiers() );
            case MutableModifier.MEMBER_VIS_PUBLIC:
                return ModifierSet.isPublic( tn.getModifiers() );
            default:
                return true;
        }
    }

    private Comparator nodeSorter = new Comparator() {
                String LINE = jEdit.getProperty( "options.sidekick.java.sortByLine", "Line" );
                String NAME = jEdit.getProperty( "options.sidekick.java.sortByName", "Name" );
                String VISIBILITY = jEdit.getProperty( "options.sidekick.java.sortByVisibility", "Visibility" );

                /**
                 * Compares a TigerNode to another TigerNode for sorting. Sorting may be by
                 * line number or node type as determined by the value of "sorted".
                 * @param o a TigerNode to compare to this node.
                 * @return a negative integer, zero, or a positive integer as this TigerNode is 
                 * less than, equal to, or greater than the specified TigerNode.
                 */
                public int compare( Object a, Object b ) {
                    if ( ! ( a instanceof TigerNode ) )
                        return -1;
                    if ( ! ( b instanceof TigerNode ) )
                        return 1;
                    TigerNode tna = ( TigerNode ) a;
                    TigerNode tnb = ( TigerNode ) b;
                    String sortBy = displayOpt.getSortBy();
                    if ( LINE.equals( sortBy ) ) {
                        // sort by line
                        Integer my_line = new Integer( tna.getStartLocation().line );
                        Integer other_line = new Integer( tnb.getStartLocation().line );
                        return my_line.compareTo( other_line );
                    }
                    else if ( VISIBILITY.equals( sortBy ) ) {
                        Integer my_vis = new Integer( ModifierSet.visibilityRank( tna.getModifiers() ) );
                        Integer other_vis = new Integer( ModifierSet.visibilityRank( tnb.getModifiers() ) );
                        int comp = my_vis.compareTo(other_vis);
                        return comp == 0 ? compareNames(tna, tnb) : comp;
                    }
                    else {
                        // sort by name
                        return compareNames( tna, tnb );
                    }
                }

                private int compareNames( TigerNode tna, TigerNode tnb ) {
                    // sort by name
                    Integer my_ordinal = new Integer( tna.getOrdinal() );
                    Integer other_ordinal = new Integer( tnb.getOrdinal() );
                    int comp = my_ordinal.compareTo(other_ordinal);
                    return comp == 0 ? tna.getName().compareTo( tnb.getName()) : comp;
                }
            };

    /******************************************************************************/
    // taken from jbrowse.JBrowse
    /******************************************************************************/


    private class StatusBarOptionAction implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            JavaParser.this.setStatusVisible(
                JavaParser.this.options.getShowStatusBar()
            );
        }
    }
    public boolean isStatusVisible() {
        return false;
        ///return statusPanel.isVisible();
    }


    public void setStatusVisible( boolean visible ) {
        ///statusPanel.setVisible( visible );
    }

    private ActionListener statusBarOptionAction = null;

    public ActionListener getStatusBarOptionAction() {
        if ( statusBarOptionAction == null ) {
            statusBarOptionAction = new StatusBarOptionAction();
        }

        return statusBarOptionAction;
    }



    public ActionListener getResizeAction() {
        return null;        /// might want to implement this
    }



    // sorting is by line or by node type, with node type being the initial default.
    // The sort type is signalled by setting a System property.
    private ActionListener sortOptionAction = null;
    public ActionListener getSortOptionAction() {
        if ( sortOptionAction == null ) {
            sortOptionAction = new SortOptionAction();
        }

        return sortOptionAction;
    }
    private class SortOptionAction implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            // on action, toggle between unsorted (sort by line number) or
            // or sorted (sort by ordinal);
            sorted = !sorted;
            if ( currentView != null )
                parse( currentView.getBuffer(), null );
        }
    }



    // controls what is visible/displayed in the tree
    private ActionListener filterOptionAction = null;
    public ActionListener getFilterOptionAction() {
        if ( filterOptionAction == null ) {
            filterOptionAction = new FilterOptionAction();
        }
        return filterOptionAction;
    }
    private class FilterOptionAction implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            // whether or not to show fields, throws, and other visibility levels
            // just reparsing should do it
            if ( currentView != null )
                parse( currentView.getBuffer(), null );
        }
    }



    // controls how the visible items in the tree are displayed
    private ActionListener displayOptionAction = null;
    public ActionListener getDisplayOptionAction() {
        if ( displayOptionAction == null ) {
            displayOptionAction = new DisplayOptionAction();
        }

        return displayOptionAction;
    }
    private class DisplayOptionAction implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            // whether or not to show line numbers, arguments, etc
            // just reparsing should do it
            if ( currentView != null )
                parse( currentView.getBuffer(), null );
        }
    }

    public ActionListener getPropertySaveListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       if ( currentView != null )
                           parse( currentView.getBuffer(), null );
                   }
               };
    }

    /**
     * Returns if the parser supports code completion.
     *
     * Returns false by default.
     */
    public boolean supportsCompletion() {
        return false;
    }

    public SideKickCompletion complete( EditPane editPane, int caret ) {
        SideKickParsedData data = SideKickParsedData.getParsedData( editPane.getView() );
        if ( data == null )
            System.out.println( "++++++++++ data is null" );


        Buffer buffer = editPane.getBuffer();
        
        // first, we get the word before the caret
        List allowedCompletions = new ArrayList( 20 );

        int caretLine = buffer.getLineOfOffset( caret );
        String text = buffer.getText( 0, caret );
        int lineStart = buffer.getLineStartOffset( caretLine );

        int wordStart = -1;
        for ( int i = caret - 1; i >= lineStart; i-- ) {
            char ch = text.charAt( i );
            if ( ch == ' ' ) {
                wordStart = i;
                break;
            }
        }

        String word;
        
        if ( wordStart != -1 ) {
            word = text.substring( wordStart + 1, caret );
            
            // check for "this."
            if ( word != null ) {
                List list = new ArrayList();
                
                if ( word.startsWith( "this." ) ) {
                    String maybe = word.substring( "this.".length() );
                    for ( Iterator it = methods.iterator(); it.hasNext(); ) {
                        String name = ( String ) it.next();
                        if ( name.startsWith( maybe ) )
                            list.add( name );
                    }
                    for ( Iterator it = fields.iterator(); it.hasNext(); ) {
                        String name = ( String ) it.next();
                        if ( name.startsWith( maybe ) )
                            list.add( name );
                    }
                    return new JavaCompletion( editPane.getView(), word, true, list );
                }

                // check for local variable within current asset
                TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
                List children = tn.getChildren();
                if ( children != null ) {
                    for ( Iterator it = children.iterator(); it.hasNext(); ) {
                        TigerNode child = (TigerNode)it.next();
                        if (child.getOrdinal() == TigerNode.VARIABLE) {
                            LocalVariableNode lvn = ( LocalVariableNode ) child;
                            if ( lvn.isPrimitive() )
                                continue;
                            String name = lvn.getName();
                            
                            if ( name.startsWith( word ) ) {
                                String type = lvn.getType();
                                List m = getMethodsForType( type, ( CUNode ) data.root.getUserObject() );
                                if ( m != null ) {
                                    list.addAll( m );
                                    return new JavaCompletion(editPane.getView(), word, list);   
                                }
                            }
                        }
                    }
                }
                
                // check parents...
            }

            

        }
        return null;
    }

    private List getMethodsForType( String type, CUNode cu ) {
        List list = null;
        String packageName = cu.getPackageName();
        
        if ( packageName != null ) {
            // check same package
            String className = ( packageName.length() > 0 ? packageName + "." : "" ) + type;
            list = getElementsForClass( className );
            if ( list != null && list.size() > 0 )
                return list;
        }
        // check imports
        List imports = cu.getImports();
        for ( Iterator it = imports.iterator(); it.hasNext(); ) {
            packageName = ( String ) it.next();
            if ( packageName != null ) {
                if ( packageName.endsWith( ".*" ) )
                    packageName = packageName.substring( 0, packageName.length() - 2 );
                String className = packageName + "." + type;
                list = getElementsForClass( className );
                if ( list != null && list.size() > 0 )
                    return list;
            }
        }

        // check runtime
        String className = Locator.getRuntimeClassName( type );
        return getElementsForClass( className );
    }

    private List getElementsForClass( String className ) {
        if ( className == null )
            return null;
        List list = new ArrayList();
        
        try {
            Class c = Class.forName( className );
            if ( c != null ) {
                Method[] methods = c.getMethods();
                for ( int i = 0; i < methods.length; i++ ) {
                    list.add( methods[ i ].getName() );
                }
                Field[] fields = c.getFields();
                for ( int i = 0; i < fields.length; i++ ) {
                    list.add( fields[ i ].getName() );
                }
            }
        }
        catch ( Exception e ) {
            //e.printStackTrace();
            return null;
        }
        return list;
    }
}
