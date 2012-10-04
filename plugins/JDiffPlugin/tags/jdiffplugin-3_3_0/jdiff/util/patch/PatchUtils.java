
package jdiff.util.patch;

import java.io.*;

public class PatchUtils {

    public static final int NORMAL = 0;
    public static final int EDIT = 1;
    public static final int CONTEXT = 2;
    public static final int UNIFIED = 3;

    /**
     * Identify the type of the patch as one of normal, edit, context, or unified diff.
     * @param patch the contents of a patch file
     * @return one of NORMAL, EDIT, CONTEXT, or UNIFIED
     * @exception Exception in the impossible event that the string can't be read
     */
    public static int getPatchType( String patch ) throws Exception {
        BufferedReader reader = new BufferedReader( new StringReader( patch ) );
        String firstLine = reader.readLine();

        // check for unified, the first line of a unified diff always starts
        // with ---
        if ( firstLine.startsWith( "---" ) ) {
            return UNIFIED;
        }

        // check for context, the first line of a context diff always starts
        // with ***
        if ( firstLine.startsWith( "***" ) ) {
            return CONTEXT;
        }

        // if it's not unified or context, then it's either normal or edit.
        // In a normal diff file, every line will start with one of
        // digit, -, <, or >.
        while ( firstLine != null ) {
            if (firstLine.length() == 0) {
                return EDIT;
            }
            char firstChar = firstLine.charAt( 0 );
            if ( firstChar == '-' || firstChar == '<' || firstChar == '>' || Character.isDigit( firstChar ) ) {
                firstLine = reader.readLine();
                continue;
            }
            else {
                return EDIT;
            }
        }
        return NORMAL;
    }

    /**
     * Load a patch file.
     * @param filename filename of the patch file
     * @return the patch file contents.
     * @exception on read error
     */
    public static String loadPatchFile( String filename ) throws Exception {
        Reader reader = new BufferedReader( new FileReader( filename ) );
        StringWriter writer = new StringWriter();
        copyToWriter( reader, writer );
        return writer.toString();
    }

    public static void copyToWriter( Reader from, Writer to ) throws Exception {
        char[] buffer = new char[ 8192 ];
        int chars_read;
        while ( true ) {
            chars_read = from.read( buffer );
            if ( chars_read == -1 )
                break;
            to.write( buffer, 0, chars_read );
        }
        to.flush();
        from.close();
    }

    // for testing
    public static void main (String[] args) {
        try {
            String filename = "/home/danson/src/plugins/JDiffPlugin/test/unified_diff.diff";
            String patch = PatchUtils.loadPatchFile(filename);
            int type = getPatchType(patch);
            System.out.println("Unified diff identification passed? " + (PatchUtils.UNIFIED == type));

            filename = "/home/danson/src/plugins/JDiffPlugin/test/context_diff.diff";
            patch = PatchUtils.loadPatchFile(filename);
            type = getPatchType(patch);
            System.out.println("Context diff identification passed? " + (PatchUtils.CONTEXT == type));

            filename = "/home/danson/src/plugins/JDiffPlugin/test/normal_diff.diff";
            patch = PatchUtils.loadPatchFile(filename);
            type = getPatchType(patch);
            System.out.println("Normal diff identification passed? " + (PatchUtils.NORMAL == type));

            filename = "/home/danson/src/plugins/JDiffPlugin/test/edit_diff.diff";
            patch = PatchUtils.loadPatchFile(filename);
            type = getPatchType(patch);
            System.out.println("Edit diff identification passed? " + (PatchUtils.EDIT == type));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}