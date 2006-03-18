package sidekick.java;

import java.lang.reflect.*;
import java.util.*;

import sidekick.java.node.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import sidekick.SideKickCompletion;
import sidekick.SideKickParser;
import sidekick.SideKickParsedData;

/**
 * Finds 2 kinds of completions, word completions and method/field completions,
 * also known as "dot" completions.
 * Word completions complete a partial word, for example, typing "String.val"
 * would show a popup containing "valueOf".  Method/field completions are
 * activated when the last character in the word is a dot, for example, typing
 * "String." would list all methods and fields in the String class.
 */
public class JavaCompletionFinder {

    private JavaSideKickParsedData data = null;
    private EditPane editPane = null;
    private Buffer buffer = null;
    private int caret = 0;


    public JavaCompletion complete( EditPane editPane, int caret ) {
        this.editPane = editPane;
        buffer = editPane.getBuffer();
        this.caret = caret;

        SideKickParsedData skpd = SideKickParsedData.getParsedData( editPane.getView() );
        if ( skpd == null ) {
            return null;
        }

        if ( skpd instanceof JavaSideKickParsedData ) {
            data = ( JavaSideKickParsedData ) skpd;
        }
        else
            return null;


        // get the word just before the caret.  It might be a partial word, that's okay.
        String word = getWordAtCursor( );

        if ( word == null || word.length() == 0 )
            return null;

        /*
            initial completion goals:
            1. partial word: get matching fields and methods in the class
            2. words ending with dot: get matching fields and methods in the
                class for the type represented by the word.
        */ 
        return getPossibleCompletions( word ) ;
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
    private String getWordAtCursor( ) {
        if ( caret < 0 )
            return "";
        if ( data == null )
            return null;

        // get the text in the current asset just before the cursor
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
        if ( tn == null )
            return null;
        int start = tn.getStart().getOffset();
        String text = buffer.getText( start, caret - start );
        if ( text == null || text.length() == 0 )
            return null;

        Mode mode = buffer.getMode();
        String word_break_chars = ( String ) mode.getProperty( "wordBreakChars" );
        if ( word_break_chars == null ) {
            word_break_chars = "";
        }
        word_break_chars += ";}()";

        // remove line enders and tabs
        text = text.replaceAll( "[\\n\\r\\t]", "" );

        for ( int i = text.length() - 1; i >= 0; i-- ) {
            char c = text.charAt( i );
            if ( word_break_chars.indexOf( c ) > -1 ) {
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

        // check if qualified
        boolean qualified = word.lastIndexOf( "." ) > 0;
        if ( qualified ) {
            return getPossibleQualifiedCompletions( word );
        }
        else {
            return getPossibleNonQualifiedCompletions( word );
        }
    }


    private JavaCompletion getPossibleQualifiedCompletions( String word ) {

        String qualification = word.substring( 0, word.lastIndexOf( "." ) );

        // might have super.something
        if ( qualification.equals( "super" ) ) {
            return getSuperCompletion( word );
        }

        // might have this.something or Class.this.something
        if ( qualification.equals( "this" ) )
            return getThisCompletion( word );
        if ( qualification.endsWith( ".this" ) )
            return getQualifiedThisCompletion( word );

        // possibly local variable/field, e.g. data.get
        FieldNode node = getLocalVariable( qualification );

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
            String filter = word.substring( word.lastIndexOf( "." ) + 1 );
            if ( filter != null && filter.length() == 0 )
                filter = null;
            List members = getMembersForClass( c, filter, static_only );
            if ( members != null && members.size() > 0 ) {
                if (members.size() == 1 && members.get( 0 ).equals( word ) ) {
                    return null;
                }
                Log.log(Log.DEBUG, this, "===== getPossibleQualifiedCompletions, list = " + members);
                return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, members );
            }
        }

        // could have package.partialClass, e.g. javax.swing.tree.DefaultMu
        List possibles = Locator.getClassPathClasses( word );
        if ( possibles == null || possibles.size() == 0 )
            possibles = Locator.getRuntimeClasses( word );
        if ( possibles != null && possibles.size() > 0 ) {
            if (possibles.size() == 1 && possibles.get( 0 ).equals( word ) ) {
                return null;
            }
                Log.log(Log.DEBUG, this, "===== getPossibleQualifiedCompletions, list = " + possibles);
            
            return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, possibles );
        }

        // didn't find anything
        return null;

    }


    private JavaCompletion getPossibleNonQualifiedCompletions( String word ) {
        // partialword
        // find all fields/variables declarations, methods, and classes in scope
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
        Set choices = new HashSet();
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
                                if ( param.getName().startsWith( word ) )
                                    choices.add( param.getName() );
                            }
                        case TigerNode.FIELD:
                        case TigerNode.VARIABLE:
                        case TigerNode.CLASS:
                        case TigerNode.ENUM:
                        case TigerNode.INTERFACE:
                            if ( child.getName().startsWith( word ) )
                                choices.add( child.getName() );
                            break;
                    }
                }
            }
            tn = tn.getParent();
            if ( tn == null )         //|| tn.getOrdinal() == TigerNode.COMPILATION_UNIT )
                break;
        }
        if ( choices.size() > 0 ) {
            List list = new ArrayList( choices );
            // don't show the completion popup if the only choice is an
            // exact match for the word
            if ( list.size() == 1 && word.equals( list.get( 0 ) ) )
                return null;
            else {
                Collections.sort( list );
                Log.log(Log.DEBUG, this, "===== getPossibleNonQualifiedCompletions, list = " + list);
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
        if (m.size() == 1 && m.get(0).equals(word)) {
            return null;   
        }
                Log.log(Log.DEBUG, this, "===== getSuperCompletion, list = " + m);
        
        return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, m );
    }


    // returns a completion containing a list of fields and methods contained by
    // the enclosing class
    private JavaCompletion getThisCompletion( String word ) {
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
        List m = getMembersForClass( ( ClassNode ) tn );
        if ( m == null || m.size() == 0 )
            return null;
        if (m.size() == 1 && m.get(0).equals(word)) {
            return null;   
        }
                Log.log(Log.DEBUG, this, "===== getThis Completion, list = " + m);
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
        if (m.size() == 1 && m.get(0).equals(word)) {
            return null;   
        }
                Log.log(Log.DEBUG, this, "===== getQualifiedThisCompletion, list = " + m);
        
        return new JavaCompletion( editPane.getView(), word, JavaCompletion.DOT, m );
    }


    // returns a completion containing a list of fields and methods contained contained by the type defined by the word,
    // for example, if the word is "my_word" and it is a String, return the fields and methods
    // for String.
    private JavaCompletion getLocalVariableCompletion( String word ) {
        String my_word = word.endsWith( "." ) ? word.substring( 0, word.length() - 1 ) : word;
        FieldNode lvn = getLocalVariable( my_word );
        if ( lvn == null )
            return null;

        String name = lvn.getName();
        if ( name.startsWith( my_word ) ) {
            int insertionType = JavaCompletion.PARTIAL;
            if ( name.equals( my_word ) )
                insertionType = JavaCompletion.DOT;
            String type = lvn.getType();
            Class c = getClassForType( type, ( CUNode ) data.root.getUserObject() );
            if ( c != null ) {
                List m = getMembersForClass( c );
                if ( m != null ) {
                Log.log(Log.DEBUG, this, "===== getLocalVariableCompletion, list = " + m);
                    
                    return new JavaCompletion( editPane.getView(), word, insertionType, m );
                }
            }
        }
        return null;
    }


    private FieldNode getLocalVariable( String name ) {
        TigerNode tn = ( TigerNode ) data.getAssetAtOffset( caret );
        while ( true ) {
            List children = tn.getChildren();
            if ( children != null ) {
                for ( Iterator it = children.iterator(); it.hasNext(); ) {
                    TigerNode child = ( TigerNode ) it.next();
                    if ( child instanceof FieldNode ) {     // LocalVariableNode is a subclass of FieldNode
                        FieldNode lvn = ( FieldNode ) child;
                        if ( lvn.isPrimitive() )
                            continue;
                        if ( lvn.getName().startsWith( name ) )
                            return lvn;
                    }
                }
            }
            if ( tn.getOrdinal() == TigerNode.CONSTRUCTOR || tn.getOrdinal() == TigerNode.METHOD ) {
                List params = ( ( Parameterizable ) tn ).getFormalParams();
                for ( Iterator jt = params.iterator(); jt.hasNext(); ) {
                    Parameter param = ( Parameter ) jt.next();
                    if ( param.getName().startsWith( name ) )
                        return param;
                }
            }
            tn = tn.getParent();
            if ( tn == null )
                break;
        }
        return null;
    }


    private Class getClassForType( String type, CUNode cu ) {
        // check in same package
        String packageName = cu.getPackageName();

        if ( packageName != null ) {
            // check same package
            String className = ( packageName.length() > 0 ? packageName + "." : "" ) + type;
            Class c = validateClassName( className );
            if ( c != null )
                return c;
        }

        // check imports
        List imports = cu.getImports();
        for ( Iterator it = imports.iterator(); it.hasNext(); ) {
            packageName = ( String ) it.next();
            if ( packageName != null ) {
                String className = packageName;
                // might have a fully qualified import
                if ( className.endsWith( type ) ) {
                    Class c = validateClassName( className );
                    if ( c != null )
                        return c;
                }
                else {
                    // wildcard import, need to add . and type
                    className = packageName + "." + type;
                    Class c = validateClassName( className );
                    if ( c != null )
                        return c;
                }
            }
        }

        // check classpath
        String className = Locator.getClassPathClassName( type );
        Class c = validateClassName( className );
        if ( c == null ) {
            // check runtime
            className = Locator.getRuntimeClassName( type );
            c = validateClassName( className );
        }
        return c;
    }


    private Class validateClassName( String classname ) {
        try {
            Class c = Class.forName( classname );
            return c;
        }
        catch ( Exception e ) {
            return null;
        }
    }


    // returns a list of TigerNodes that are immediate children of the given
    // class node
    private List getMembersForClass( ClassNode cn ) {
        if ( cn.getChildCount() == 0 )
            return null;

        Set members = new HashSet();
        for ( Iterator it = cn.getChildren().iterator(); it.hasNext(); ) {
            TigerNode child = ( TigerNode ) it.next();
            switch ( child.getOrdinal() ) {
                case TigerNode.ENUM:
                case TigerNode.FIELD:
                case TigerNode.METHOD:
                case TigerNode.CLASS:
                    members.add( child.getName() );
            }
        }
        return new ArrayList( members );
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
        List members = new ArrayList( list );
        Collections.sort( members );
        return members;
    }

}
