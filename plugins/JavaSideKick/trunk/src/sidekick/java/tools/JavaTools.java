package sidekick.java;

import java.util.*;
import sidekick.java.node.*;
import sidekick.java.util.*;
import sidekick.java.classloader.*;
import javax.swing.SwingUtilities;
import errorlist.*;
import org.gjt.sp.jedit.*;



public class JavaTools {
    private DefaultErrorSource myErrorSource = JavaSideKickPlugin.ERROR_SOURCE;
    private JavaCompletionFinder finder = new JavaCompletionFinder();
    private AntClassLoader loader = null;

    /**
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

        final CUNode cu = parser.parse( buffer );
        final List imports = cu.getImportNodes();
        if ( imports == null ) {
            return ;    // nothing to check
        }
        SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        checkDuplicateImports( imports, filename );
                        if ( PVHelper.isProjectViewerAvailable() ) {
                            String projectName = PVHelper.getProjectNameForFile( filename );
                            Path path = PVHelper.getClassPathForProject( projectName );
                            loader = new AntClassLoader( path );
                        }
                        checkUnusedImports( cu, imports, filename );
                    }
                }
                                  );
    }

    private void checkDuplicateImports( List imports, String filename ) {
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
            // what's left in maybe_dups are now definite duplicates
            for ( Iterator it = maybe_dups.iterator(); it.hasNext(); ) {
                ImportNode in = ( ImportNode ) it.next();
                Range range = new Range( in.getStartLocation(), in.getEndLocation() );
                myErrorSource.addError( ErrorSource.ERROR, filename, range.startLine - 1, range.startColumn - 1, range.endColumn, "Duplicate import: " + in.getName() );
            }
        }
    }

    private void checkUnusedImports( CUNode cu, List imports, String filename ) {
        Set checked = new HashSet();
        checkChildImports( cu, cu, checked, filename );

        // check that checked and imports are the same
        for ( Iterator it = checked.iterator(); it.hasNext(); ) {
            String name = it.next().toString();
            ImportNode in = cu.getImport( name );
            if ( in != null ) {
                imports.remove( in );
            }
            name = name.substring( 0, name.lastIndexOf( "." ) );
            in = cu.getImport( name );
            if ( in != null ) {
                imports.remove( in );
            }
        }
        if ( imports.size() > 0 ) {
            List original_imports = cu.getImports();
            for ( Iterator it = imports.iterator(); it.hasNext(); ) {
                ImportNode in = ( ImportNode ) it.next();
                Range range = new Range( in.getStartLocation(), in.getEndLocation() );
                myErrorSource.addError( ErrorSource.ERROR, filename, range.startLine - 1, range.startColumn - 1, range.endColumn, "Unused import: " + in.getName() );
            }
        }
    }


    private void checkChildImports( CUNode cu, TigerNode child, Set checked, String filename ) {
        Class c = null;
        String type = null;
        switch ( child.getOrdinal() ) {
            case TigerNode.CLASS:
                type = child.getName();
                c = finder.getClassForType( type, cu, filename );
                break;
            case TigerNode.EXTENDS:
            case TigerNode.IMPLEMENTS:
            case TigerNode.PRIMARY_EXPRESSION:
                type = child.getName();
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
                    break;
                type = fn.getType();
                c = finder.getClassForType( type, cu, filename );
                break;
            case TigerNode.TYPE:
                type = ( ( Type ) child ).getName();
                c = finder.getClassForType( type, cu, filename );
                break;
            default:
                type = child.getType();
                if ( type != null ) {
                    c = finder.getClassForType( type, cu, filename );
                }
                break;
        }
        if ( c != null ) {
            String package_name = c.getPackage().getName();
            checked.add( package_name + "." + type );
        }
        if ( child.getChildren() != null ) {
            for ( Iterator it = child.getChildren().iterator(); it.hasNext(); ) {
                TigerNode node = ( TigerNode ) it.next();
                checkChildImports( cu, node, checked, filename );
            }
        }
    }

}
