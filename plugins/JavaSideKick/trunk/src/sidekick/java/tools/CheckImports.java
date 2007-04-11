package sidekick.java.tools;

import java.util.*;
import sidekick.java.*;
import sidekick.java.node.*;
import sidekick.java.classloader.*;
import errorlist.*;
import org.gjt.sp.jedit.*;


public class CheckImports {
    private DefaultErrorSource myErrorSource = JavaSideKickPlugin.ERROR_SOURCE;
    private JavaCompletionFinder finder = new JavaCompletionFinder();
    private AntClassLoader loader = null;

    // find the definition of the asset at the given position
    public void findDefinition( Buffer buffer, int caret_position ) {}

    /**
     * This method will parse the given buffer prior to checking the imports.
     * Check the import statements for a java file.  Checks for both duplicate
     * imports and unused imports (that is, imports that are listed, but no classes
     * from that import is actually used in the java file).
     * @param buffer the buffer containing the java file to check.  The filename
     * of the file in the buffer must end with ".java" or the file won't be
     * checked.
     */
    public void checkImports( final Buffer buffer ) {
        myErrorSource.clear();
        final String filename = buffer.getPath();
        if ( !filename.endsWith( ".java" ) ) {
            return ;     // not a java file, don't check
        }

        JavaParser parser = new JavaParser();
        CUNode cu = parser.parse( buffer );
        cu.setFilename( buffer.getPath() );
        checkImports( cu );
    }

    /**
     * This method assumes the given CUNode is from a recent parse. Assumes
     * CUNode.getFilename won't return null.
     * Check the import statements for a java file.  Checks for both duplicate
     * imports and unused imports (that is, imports that are listed, but no classes
     * from that import is actually used in the java file).
     * @param buffer the buffer containing the java file to check.  The filename
     * of the file in the buffer must end with ".java" or the file won't be
     * checked.
     */
    public void checkImports( CUNode cunode ) {
        if ( cunode == null ) {
            return ;
        }

        final CUNode cu = cunode;
        final String filename = cu.getFilename();
        final List imports = cu.getImportNodes();
        if ( imports == null ) {
            return ;    // nothing to check
        }
        Thread checker = new Thread() {
                    public void run() {
                        long start_time = new Date().getTime();
                        boolean hadDups = checkDuplicateImports( imports, filename );
                        boolean hadUnused = checkUnusedImports( cu, imports, filename );
                        long end_time = new Date().getTime();
                        long time = end_time - start_time;
                        if ( !hadDups && !hadUnused ) {
                            jEdit.getActiveView().getStatus().setMessage( "Imports okay, " + time + "ms." );
                        }
                        else {
                            jEdit.getActiveView().getStatus().setMessage( "Imports had errors, " + time + "ms." );
                        }
                    }
                };
        checker.setPriority( Thread.MIN_PRIORITY );
        checker.start();
    }

    private boolean checkDuplicateImports( List imports, String filename ) {
        boolean hadDups = false;
        // check for duplicate imports
        HashSet no_dups = new HashSet( imports );       // hashset doesn't allow duplicates
        List maybe_dups = new ArrayList( imports );     // arraylist does
        if ( no_dups.size() < maybe_dups.size() ) {
            // there are duplicates, identify them
            for ( Iterator it = no_dups.iterator(); it.hasNext(); ) {
                ImportNode in = ( ImportNode ) it.next();
                int index = maybe_dups.indexOf( in );
                maybe_dups.remove( index );
            }
            hadDups = maybe_dups.size() > 0;
            // what's left in maybe_dups are now definite duplicates
            for ( Iterator it = maybe_dups.iterator(); it.hasNext(); ) {
                ImportNode in = ( ImportNode ) it.next();
                Range range = new Range( in.getStartLocation(), in.getEndLocation() );
                myErrorSource.addError( ErrorSource.WARNING, filename, range.startLine - 1, range.startColumn - 1, range.endColumn, "Duplicate import: " + in.getName() );
            }
        }
        return hadDups;
    }

    private boolean checkUnusedImports( CUNode cu, List imports, String filename ) {
        boolean hadUnused = false;
        Set checked = new HashSet();
        checkChildImports( cu, cu, checked, filename );

        // check that checked and imports are the same
        for ( Iterator it = checked.iterator(); it.hasNext(); ) {
            String name = it.next().toString();
            ImportNode in = cu.getImport( name );
            if ( in != null ) {
                imports.remove( in );
            }
            if (name.indexOf(".") > 0)
                name = name.substring( 0, name.lastIndexOf( "." ) );
            in = cu.getImport( name );
            if ( in != null ) {
                imports.remove( in );
            }
        }
        if ( imports.size() > 0 ) {
            hadUnused = true;
            for ( Iterator it = imports.iterator(); it.hasNext(); ) {
                ImportNode in = ( ImportNode ) it.next();
                Range range = new Range( in.getStartLocation(), in.getEndLocation() );
                myErrorSource.addError( ErrorSource.WARNING, filename, range.startLine - 1, range.startColumn - 1, range.endColumn, "Unused import: " + in.getName() );
            }
        }
        return hadUnused;
    }


    private void checkChildImports( CUNode cu, TigerNode child, Set checked, String filename ) {
        Class c = null;
        String type = null;
        switch ( child.getOrdinal() ) {
            case TigerNode.COMPILATION_UNIT:
                break;
            case TigerNode.CLASS:
                type = child.getName();
                if ( type != null && type.length() > 0 )
                    c = finder.getClassForType( type, cu, filename );
                break;
            case TigerNode.EXTENDS:
            case TigerNode.IMPLEMENTS:
                type = child.getName();
                if ( type != null && type.length() > 0 )
                    c = finder.getClassForType( type, cu, filename );
                break;
            case TigerNode.PRIMARY_EXPRESSION:
                type = child.getName();
                if ( type.indexOf( "." ) > -1 ) {
                    type = type.substring( 0, type.indexOf( "." ) );
                }
                if ( type != null && type.length() > 0 )
                    c = finder.getClassForType( type, cu, filename );
                break;
            case TigerNode.CONSTRUCTOR:
            case TigerNode.METHOD:
                // need to check parameters
                Parameterizable pn = ( Parameterizable ) child;
                List params = pn.getFormalParams();
                for ( Iterator it = params.iterator(); it.hasNext(); ) {
                    Parameter param = ( Parameter ) it.next();
                    checkChildImports( cu, param, checked, filename );
                }
                // also need to check return type for methods
                if ( child.getOrdinal() == TigerNode.METHOD ) {
                    Type return_type = ( ( MethodNode ) child ).getReturnType();
                    if ( return_type.isPrimitive )
                        break;
                    c = finder.getClassForType( return_type.getType(), cu, filename );
                    type = return_type.getName();
                }
                break;
            case TigerNode.FIELD:
            case TigerNode.PARAMETER:
            case TigerNode.VARIABLE:
                FieldNode fn = ( FieldNode ) child;
                if ( fn.isPrimitive() )
                    return ;     // don't need to do anything with primitives
                type = fn.getType();
                if ( type != null && type.length() > 0 )
                    c = finder.getClassForType( type, cu, filename );
                break;
            case TigerNode.TYPE:
                if ( ( ( Type ) child ).isVoid ) {
                    return ;     // don't need to do anything with void
                }
                type = ( ( Type ) child ).getName();
                if ( type != null && type.length() > 0 ) {
                    c = finder.getClassForType( type, cu, filename );
                }
                break;
            default:
                type = child.getType();
                //if ("true".equals(jEdit.getProperty("javasidekick.dump"))) {
                //    System.out.println( "+++++ default, type = " + type + ", classname = " + child.getClass().getName() + ", " + child.toString() );
                //}
                if ( type != null && type.length() > 0 )
                    c = finder.getClassForType( type, cu, filename );
                break;
        }
        if ( c != null ) {
            if ( c.getPackage() != null ) {
                type = c.getPackage().getName() + "." + type;
            }
            else {
                type = c.getName();
            }
                child.setFullyQualifiedTypeName( type );
            checked.add( type );
        }
        else {
            // it could be that the type does exist in the project, but it's not
            // compiled yet.
        }
        if ( child.getChildren() != null ) {
            for ( Iterator it = child.getChildren().iterator(); it.hasNext(); ) {
                TigerNode node = ( TigerNode ) it.next();
                checkChildImports( cu, node, checked, filename );
            }
        }
    }

}
