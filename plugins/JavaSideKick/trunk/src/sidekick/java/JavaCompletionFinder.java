package sidekick.java;

import java.lang.reflect.*;
import java.util.*;

import sidekick.java.classloader.AntClassLoader;
import sidekick.java.node.*;
import sidekick.java.options.*;
import sidekick.java.util.*;

import org.gjt.sp.jedit.*;

import sidekick.SideKickParsedData;

/**
 * Finds 2 kinds of completions, word completions and method/field completions,
 * also known as "dot" completions.
 * Word completions complete a partial word, for example, typing "String.val"
 * would show a popup containing "valueOf".  Method/field completions are
 * activated when the last character in the word is a dot, for example, typing
 * "String." would list all methods and fields in the String class.
 *
 * TODO: completion inside anonymous inner classes doesn't work.  For example,
 * button.addActionListener( 
 *     new ActionListener() {
 *         public void actionPerformed( ActionEvent ae ) {
 *             ae.      // nothing happens here
 *         }
 *     }
 * );
 * 
 * TODO: code completion is active inside of comments. Is this useful or annoying?
 */
public class JavaCompletionFinder {

    private JavaSideKickParsedData data = null;
    private EditPane editPane = null;
    private int caret = 0;

    public JavaCompletion complete( EditPane editPane, int caret ) {
        this.editPane = editPane;
        this.caret = caret;

        SideKickParsedData skpd = SideKickParsedData.getParsedData( editPane.getView() );
        if ( skpd == null ) {
            return null;
        }

        if ( skpd instanceof JavaSideKickParsedData ) {
            data = ( JavaSideKickParsedData ) skpd;
        }
        else {
            return null;
        }

        // get the word just before the caret.  It might be a partial word, that's okay.
        String word = getWordAtCursor( editPane.getBuffer() );
        if ( word == null || word.length() == 0 ) {
            return null;
        }

        /*
            initial completion goals:
            1. partial word: get matching fields and methods in the class
            2. words ending with dot: get matching fields and methods in the
                class for the type represented by the word.
        */ 
        //long start = System.currentTimeMillis();
        JavaCompletion completion = getPossibleCompletions( word );
        //Log.log(Log.DEBUG, this, "time: " + (System.currentTimeMillis() - start));
        return completion ;
    }


    /// doesn't handle identifiers spanning several lines, like:
    /// com.
    /// foo.
    /// Bar.getSomething
    /// which is legal. All this will return is Bar.getSomething. Without the
    /// qualifiers, finding the class name is unlikely, so completion won't work.
    /// Doesn't handle whitespace within an identifier, like:
    /// com.     foo.   Bar.   getSomething, which is also legal.
    /// Doesn't handle implicit return types, like:
    /// StringBuffer sb = new StringBuffer(); sb.append().app
    ///
    private String getWordAtCursor( Buffer buffer ) {
        if ( caret <= 0 )
            return "";
        if ( data == null )
            return null;

        // get the text in the current asset just before the cursor
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
        if ( tn == null ) {
            return null;
        }
        int start = tn.getStart().getOffset();
        if ( caret - start < 0 ) {
            return "";
        }

        String text = buffer.getText( start, caret - start );
        if ( text == null || text.length() == 0 ) {
            return null;
        }

        Mode mode = buffer.getMode();       // TODO: check for java mode?
        String word_break_chars = ( String ) mode.getProperty( "wordBreakChars" );
        if ( word_break_chars == null ) {
            word_break_chars = "";
        }
        word_break_chars += "!;{}()";        // NOPMD

        // remove line enders and tabs
        text = text.replaceAll( "[\\n\\r\\t]", "" );

        // read the text backwards until a word break character is found.  It is
        // possible that there is no word break character.
        for ( int i = text.length() - 1; i >= 0; i-- ) {
            char c = text.charAt( i );
            if ( word_break_chars.indexOf( c ) > -1 ) {
                //Log.log( Log.DEBUG, this, "word break char is " + c );
                text = text.substring( i + 1 );
                // remove all spaces
                text = text.replaceAll( " ", "" );
                break;
            }
        }
        return text;
    }

    private JavaCompletion getPossibleCompletions( String word ) {
        if ( word == null || word.length() == 0 )
            return null;

        // possibles:
        // cast
        // partialword
        // class.partialword
        // package.class.partialword
        // this.partialword
        // class.this.partialword
        // super.partialword
        // static field, like System.out
        // static method, like String.valueOf

        // check if cast
        /* // needs work.  This doesn't feel right, hand parsing a cast could
           // be difficult as there are several variations in the depth of
           // parens.
        if (word.startsWith("(")) {
            int index = word.indexOf(")");
            if (index > 1) {
                String cast = word.substring(1, index);
                if (cast != null && cast.length() > 0) {
                    Class c = getClassForType( cast, ( CUNode ) data.root.getUserObject() );
                    if ( c != null ) {
                        // filter the members of the class by the part of the word
                        // following the last dot.  The completion will replace this
                        // part of a word
                        String filter = word.substring( word.lastIndexOf( "." ) + 1 );
                        List members = getMembersForClass( c, filter );
                        if ( members != null && members.size() > 0 )
                            return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, members );
                    }
                }
            }
    }
        */

        // check if "qualified", "qualified" means there is something.something
        boolean qualified = word.lastIndexOf( '.' ) > 0;
        if ( qualified ) {
            return getPossibleQualifiedCompletions( word );
        }
        else {
            return getPossibleNonQualifiedCompletions( word );
        }
    }


    private JavaCompletion getPossibleQualifiedCompletions( String word ) {
        String qualification = word.substring( 0, word.lastIndexOf( '.' ) );

        // might have super.something
        if ( "super".equals( qualification ) ) {
            return getSuperCompletion( word );
        }

        // might have this.something or Class.this.something
        if ( "this".equals( qualification ) )
            return getThisCompletion( word );
        if ( qualification.endsWith( ".this" ) )
            return getQualifiedThisCompletion( word );

        // possibly local variable/field, e.g. data.get
        FieldNode node = getLocalVariable( qualification );
        //Log.log( Log.DEBUG, this, "field node: " + node );

        Class c = null;
        if ( node != null ) {
            c = getClassForType( node.getType(), ( CUNode ) data.root.getUserObject() );
        }

        boolean static_only = false;
        // could have package.class.partialword, e.g. java.lang.String.valu
        if ( c == null ) {
            c = validateClassName( qualification );
            static_only = c != null;
        }

        // could have class.partialword, e.g. Math.ab
        if ( c == null ) {
            c = getClassForType( qualification, ( CUNode ) data.root.getUserObject() );
            static_only = c != null;
        }
        if ( c != null ) {
            // filter the members of the class by the part of the word
            // following the last dot.  The completion will replace this
            // part of a word
            String filter = word.substring( word.lastIndexOf( '.' ) + 1 );
            if ( filter != null && filter.length() == 0 )
                filter = null;
            List members = getMembersForClass( c, filter, static_only );
            if ( members != null && members.size() > 0 ) {
                if ( members.size() == 1 && members.get( 0 ).equals( word ) ) {
                    return null;
                }
                return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, members );
            }
        }

        // could have package.partialClass, e.g. javax.swing.tree.DefaultMu
        String projectName = PVHelper.getProjectName( editPane.getView() );
        List<String> possibles = Locator.getInstance().getProjectClasses( projectName, word );
        if ( possibles == null || possibles.size() == 0 ) {
            possibles = Locator.getInstance().getClassPathClasses( word );
        }
        if ( possibles == null || possibles.size() == 0 ) {
            possibles = Locator.getInstance().getRuntimeClasses( word );
        }
        if ( possibles != null && possibles.size() > 0 ) {
            if ( possibles.size() == 1 && possibles.get( 0 ).equals( word ) ) {
                return null;
            }
            return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, possibles );
        }
        return getLocalVariableCompletion( word );
    }


    private JavaCompletion getPossibleNonQualifiedCompletions( String word ) {
        // partialword
        // find all fields/variables declarations, methods, and classes in scope
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
        Set<String> choices = new HashSet<String>();
        while ( true ) {
            List children = tn.getChildren();
            if ( children != null ) {
                for ( Iterator it = children.iterator(); it.hasNext(); ) {
                    TigerNode child = ( TigerNode ) it.next();
                    switch ( child.getOrdinal() ) {
                        case TigerNode.CONSTRUCTOR:
                        case TigerNode.METHOD:
                            List params = ( ( Parameterizable ) child ).getFormalParams();
                            for ( Iterator jt = params.iterator(); jt.hasNext(); ) {
                                Parameter param = ( Parameter ) jt.next();
                                if ( param.getName().startsWith( word ) ) {
                                    choices.add( param.getName() );
                                }
                            }
                        case TigerNode.FIELD:
                        case TigerNode.VARIABLE:
                        case TigerNode.CLASS:
                        case TigerNode.ENUM:
                        case TigerNode.INTERFACE:
                            if ( child.getName().startsWith( word ) ) {
                                choices.add( child.getName() );
                            }
                            break;
                    }
                }
            }
            tn = tn.getParent();
            if ( tn == null )                 //|| tn.getOrdinal() == TigerNode.COMPILATION_UNIT )
                break;
        }
        List<String> list = new ArrayList<String>( choices );
        JavaCompletion jc = getSuperCompletion( word );
        if ( jc != null ) {
            list.addAll( jc.getChoices() );
        }
        if ( list.size() > 0 ) {
            // don't show the completion popup if the only choice is an
            // exact match for the word
            if ( list.size() == 1 && word.equals( list.get( 0 ) ) )
                return null;
            else {
                Collections.sort( list );
                return new JavaCompletion( editPane.getView(), word, list );
            }
        }
        return null;
    }


    // returns a completion containing a list of fields and methods contained by
    // the super class
    private JavaCompletion getSuperCompletion( String word ) {
        // get the containing asset
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );

        // find the first parent class containing this asset
        while ( tn.getOrdinal() != TigerNode.CLASS ) {
            if ( tn.getParent() != null )
                tn = tn.getParent();
            else
                return null;    // shouldn't get here
        }

        // find the superclass of the enclosing class
        Class c = getClassForType( tn.getName(), ( CUNode ) data.root.getUserObject() );
        if ( c != null )
            c = c.getSuperclass();
        if ( c == null )
            return null;

        // get the members (fields and methods) for the class node
        List m = getMembersForClass( c );
        if ( m == null || m.size() == 0 )
            return null;
        if ( m.size() == 1 && m.get( 0 ).equals( word ) ) {
            return null;
        }
        for ( ListIterator it = m.listIterator(); it.hasNext(); ) {
            if ( !it.next().toString().startsWith( word ) ) {
                it.remove();
            }
        }

        return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, m );
    }


    // returns a completion containing a list of fields and methods contained by
    // the enclosing class
    private JavaCompletion getThisCompletion( String word ) {
        if ( word.startsWith( "this." ) ) {
            word = word.substring( "this.".length() );
        }

        // get the containing asset
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );

        // find the first parent class containing this asset
        while ( tn.getOrdinal() != TigerNode.CLASS ) {
            if ( tn.getParent() != null )
                tn = tn.getParent();
            else
                return null;    // shouldn't get here
        }

        // get the members (fields and methods) for the class node
        List m = getMembersForClass( ( ClassNode ) tn, word );
        if ( m == null || m.size() == 0 )
            return null;
        if ( m.size() == 1 && m.get( 0 ).equals( word ) ) {
            return null;
        }
        return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, m );
    }


    // returns a completion containing a list of fields and methods contained by a
    // specific enclosing class
    private JavaCompletion getQualifiedThisCompletion( String word ) {
        int index = word.lastIndexOf( ".this" );
        String classname = word.substring( 0, index );

        // get the containing asset
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );

        // find the first parent class with the classname
        while ( tn.getOrdinal() != TigerNode.CLASS ) {
            if ( tn.getName().endsWith( classname ) )
                break;
            if ( tn.getParent() != null )
                tn = tn.getParent();
            else
                return null;    // shouldn't get here
        }

        // get the members (fields and methods) for the class node
        List m = getMembersForClass( ( ClassNode ) tn );
        if ( m == null || m.size() == 0 )
            return null;
        if ( m.size() == 1 && m.get( 0 ).equals( word ) ) {
            return null;
        }
        return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, m );
    }


    // returns a completion containing a list of fields and methods contained contained by the type defined by the word,
    // for example, if the word is "my_word" and it is a String, return the fields and methods
    // for String.
    private JavaCompletion getLocalVariableCompletion( String word ) {
        String my_word = word.indexOf('.') > -1 ? word.substring(0, word.lastIndexOf('.')) : word;
        FieldNode lvn = getLocalVariable( my_word );
        if ( lvn == null ) {
            return null;
        }

        String name = lvn.getName();
        if ( name.startsWith( my_word ) ) {
            int insertionType = JavaCompletion.PARTIAL;
            if ( name.equals( my_word ) ) {
                insertionType = JavaCompletion.DOT;
            }
            String type = lvn.getType();
            Class c = getClassForType( type, ( CUNode ) data.root.getUserObject() );
            if ( c != null ) {
                List m = getMembersForClass( c );
                if ( m != null ) {
                    return new JavaCompletion( editPane.getView(), word, insertionType, m );
                }
            }
        }
        return null;
    }


    private FieldNode getLocalVariable( String name ) {
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
        while ( true ) {
            // check children of the node first
            List<TigerNode> children = tn.getChildren();
            if ( children != null ) {
                for (TigerNode child : children) {
                    if ( child instanceof FieldNode ) {     // LocalVariableNode is a subclass of FieldNode
                        FieldNode lvn = ( FieldNode ) child;
                        if ( !lvn.isPrimitive() && lvn.getName().startsWith( name ) ) {
                            return lvn;
                        }
                    }
                }
            }
            
            // check parameters to constructors and methods
            if ( tn.getOrdinal() == TigerNode.CONSTRUCTOR || tn.getOrdinal() == TigerNode.METHOD ) {
                List params = ( ( Parameterizable ) tn ).getFormalParams();
                for ( Iterator jt = params.iterator(); jt.hasNext(); ) {
                    Parameter param = ( Parameter ) jt.next();  // Parameter is a subclass of FieldNode
                    if ( param.getName().startsWith( name ) ) {
                        return param;
                    }
                }
            }
            
            // up the tree
            tn = tn.getParent();
            if ( tn == null ) {
                break;
            }
        }
        return null;
    }

    /**
     * Given a type, such as "String" or "Object", and a compilation unit,
     * this method attempts to create an actual class of that type.
     * @return a Class of the given type
     */
    public Class getClassForType( String type, CUNode cu ) {
        return getClassForType( type, cu, null );
    }

    /**
     * @param type name
     * @param cu the top leve CU node
     * @param filename the filename of the buffer
     */
    public Class getClassForType( String type, CUNode cu, String filename ) {
        // check in same package
        String packageName = cu.getPackageName();
        if ( packageName != null ) {
            // check same package
            String className = ( packageName.length() > 0 ? packageName + "." : "" ) + type;
            Class c = validateClassName( className );
            if ( c != null ) {
                return c;
            }
        }

        // check imports
        List imports = cu.getImports();
        for ( Iterator it = imports.iterator(); it.hasNext(); ) {
            packageName = ( String ) it.next();
            if ( packageName != null ) {
                String className = packageName;
                // might have a fully qualified import
                if ( className.endsWith( type ) ) {
                    Class c = validateClassName( className, type, filename );
                    if ( c != null ) {
                        return c;
                    }
                }
                else {
                    // wildcard import, need to add . and type
                    className = packageName + "." + type;
                    try {
                        Class c = validateClassName( className, type, filename );
                        if ( c != null ) {
                            return c;
                        }
                    }
                    catch ( Exception e ) {
                        continue;
                    }
                }
            }
        }

        // check jars in project classpath. These are the jars and/or directories
        // specified in the ProjectViewer "Classpath settings" option pane.
        String projectName = PVHelper.getProjectName( editPane.getView() );
        String className = Locator.getInstance().getProjectClassName( projectName, type );
        Class c = validateClassName( className, type, filename );

        // check jars in classpath.  These are the jars and/or directories specified
        // in System.getProperty("java.class.path").
        if ( c == null && PVHelper.useJavaClasspath( projectName ) ) {
            className = Locator.getInstance().getClassPathClassName( type );
            c = validateClassName( className, type, filename );
        }

        // check Java runtime jars.  These are the jars specified in $JAVA_HOME/lib,
        // ext dirs, and endorsed dirs.
        if ( c == null ) {
            className = Locator.getInstance().getRuntimeClassName( type );
            c = validateClassName( className, type, filename );
        }
        return c;
    }


    private Class validateClassName( String classname ) {
        return validateClassName( classname, null, null );
    }

    /**
     * Attempts to find the class in the current classloader.  If not found,
     * attempts to find the class in the project classloader.  If not found,
     * attempts to find the class in the build output directory.
     *
     * @param classname The name of the class to find.
     * @param type The type that represents the class.
     * @param filename The name of the current file/buffer.
     * @return The class if found, null if not.
     */
    private Class validateClassName( String classname, String type, String filename ) {
        if ( classname == null ) {
            return null;
        }
        try {
            // check current classloader
            return Class.forName( classname );
        }
        catch ( ClassNotFoundException cnfe ) {     // NOPMD
            try {
                // check the project classloader
                AntClassLoader classloader = Locator.getInstance().getProjectClassLoader();
                if ( classloader == null ) {
                    throw new ClassNotFoundException();
                }
                return classloader.forceLoadClass( classname );
            }
            catch ( Exception pcnfe ) {
                // check the project build output directory
                return findClassInProject( classname, type, filename );
            }
        }
    }

    /**
     * Finds a class in the build output path for a project. Does not search
     * system classpath. These are loaded into a separate class loader since they
     * probably aren't loaded in any class loader available to jEdit.
     */
    private Class findClassInProject( String classname, String type, String filename ) {
        if ( filename == null ) {
            return null;
        }
        String project_name = PVHelper.getProjectNameForFile( filename );
        Class c = null;
        if ( project_name != null ) {
            String pc = PVHelper.getBuildOutputPathForProject( project_name );
            AntClassLoader loader = new AntClassLoader( new Path( pc ), false );
            try {
                c = loader.findClass( type );
            }
            catch ( Exception e ) {
                try {
                    c = loader.findClass( classname );
                }
                catch ( Exception ee ) {   // NOPMD
                }
            }
        }
        return c;
    }


    // returns a list of TigerNodes that are immediate children of the given
    // class node
    private List getMembersForClass( ClassNode cn ) {
        return getMembersForClass( cn, null );
    }

    // returns a list of TigerNodes that are immediate children of the given
    // class node, filtered with the given filter.  A null filter returns all
    // children.
    private List getMembersForClass( ClassNode cn, String filter ) {
        if ( cn.getChildCount() == 0 )
            return null;

        Set members = new HashSet();
        for ( Iterator it = cn.getChildren().iterator(); it.hasNext(); ) {
            TigerNode child = ( TigerNode ) it.next();
            switch ( child.getOrdinal() ) {     // NOPMD
                case TigerNode.ENUM:
                case TigerNode.FIELD:
                case TigerNode.METHOD:
                case TigerNode.CLASS:
                    if ( filter == null || child.getName().startsWith( filter ) ) {
                        members.add( child.getName() );
                    }
            }
        }
        ArrayList list = new ArrayList( members );
        Collections.sort( list );
        return list;
    }


    private List getMembersForClass( Class c ) {
        return getMembersForClass( c, null );
    }


    private List getMembersForClass( Class c, String filter ) {
        return getMembersForClass( c, filter, false );
    }

    private List getMembersForClass( Class c, String filter, boolean static_only ) {
        if ( c == null )
            return null;

        Set list = new HashSet();

        try {
            Method[] methods = c.getMethods();
            for ( int i = 0; i < methods.length; i++ ) {
                int modifiers = methods[ i ].getModifiers();
                if ( static_only && !Modifier.isStatic( modifiers ) ) {
                    continue;
                }
                if ( filter == null || methods[ i ].getName().startsWith( filter ) ) {
                    list.add( methods[ i ].getName() );
                }
            }
            Field[] fields = c.getFields();
            for ( int i = 0; i < fields.length; i++ ) {
                if ( filter == null || fields[ i ].getName().startsWith( filter ) )
                    list.add( fields[ i ].getName() );
            }
        }
        catch ( Exception e ) {
            return null;
        }
        catch ( NoClassDefFoundError ncdfe ) {
            // TODO: logging to the activity log is useless for the end user.  Need to
            // find a better way to let them know about this problem.
            editPane.getView().getStatus().setMessage("Class not in classpath: " + ncdfe.getMessage());
            return null;
        }
        List members = new ArrayList( list );
        Collections.sort( members );
        return members;
    }

}