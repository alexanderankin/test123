package jdiff.util.patch.normal;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This Library implements a simple patch algorithm which is able to process
 * the output of diff in normal format.<br>
 * <br>
 * This class implements the algorithm.<br>
 * <br>
 * The Method you're probably looking for is PatchUtils.patch(diff, target).<br>
 * <br>
 * Example usage in comparison to GNU patch:<br>
 * GNU patch: "patch target < diff"<br>
 * jPatchLib: "PatchUtils.patch(diff, target"<br>
 *
 *
 * see <a href="http://www.gnu.org/software/diffutils/manual/html_mono/diff.html#Normal">http://www.gnu.org/software/diffutils/manual/html_mono/diff.html#Normal</a>
 *
 * <pre>
 *          Copyright (c) 2007 Dominik Schulz
 *
 *          This file is part of jPatchLib.
 *
 *          jPatchLib is free software; you can redistribute it and/or modify
 *          it under the terms of the GNU General Public License as published by
 *          the Free Software Foundation; either version 2 of the License, or
 *          (at your option) any later version.
 *
 *          jPatchLib is distributed in the hope that it will be useful,
 *          but WITHOUT ANY WARRANTY; without even the implied warranty of
 *          MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *          GNU General Public License for more details.
 *
 *          You should have received a copy of the GNU General Public License
 *          along with jPatchLib; if not, write to the Free Software
 *          Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * </pre>
 *
 * @author Dominik
 */

/*
This comment is covered under the GNU Free Documentation License.

Detailed Description of Normal Format

The normal output format consists of one or more hunks of differences; each hunk
shows one area where the files differ. Normal format hunks look like this:

change-command
< from-file-line
< from-file-line...
---
> to-file-line
> to-file-line...

There are three types of change commands. Each consists of a line number or
comma-separated range of lines in the first file, a single character indicating
the kind of change to make, and a line number or comma-separated range of lines
in the second file. All line numbers are the original line numbers in each file.
The types of change commands are:

lar
    Add the lines in range r of the second file after line l of the first file.
    For example, 8a12,15 means append lines 12-15 of file 2 after line 8 of file
    1; or, if changing file 2 into file 1, delete lines 12-15 of file 2.
fct
    Replace the lines in range f of the first file with lines in range t of the
    second file. This is like a combined add and delete, but more compact. For
    example, 5,7c8,10 means change lines 5-7 of file 1 to read as lines 8-10 of
    file 2; or, if changing file 2 into file 1, change lines 8-10 of file 2 to
    read as lines 5-7 of file 1.
rdl
    Delete the lines in range r from the first file; line l is where they would
    have appeared in the second file had they not been deleted. For example,
    5,7d3 means delete lines 5-7 of file 1; or, if changing file 2 into file 1,
    append lines 5-7 of file 1 after line 3 of file 2.
*/
public class Patch {

    /**
     * Checks if the string starts with a number.
     * Use to check if the string (line) is the beginning of a new chunk.
     * @param str the string to be checked
     * @return true if the string starts with a number.
     */
    public static boolean startWithNumber( String str ) {
        char c = str.charAt( 0 );
        return ( '\u0030' <= c && c <= '\u0039' );
    }

    /**
     * Get the single chunks from the patch.
     * @param patchSrc the patch as a string array
     * @return the array of chunks from the patch file.
     */
    public static Chunk[] getChunks( String[] patchSrc ) {
        // count the chunks
        int numChunks = 0;
        for ( int i = 0; i < patchSrc.length; i++ ) {
            if ( startWithNumber( patchSrc[ i ] ) ) {
                numChunks++;
            }
        }

        // split into chunks (start at (some number) to (some number)).
        Chunk[] chunks = new Chunk[ numChunks ];
        numChunks = -1; // must be -1 because in the next step its incremented
        ArrayList<String> fromBuf = new ArrayList<String>();
        ArrayList<String> toBuf = new ArrayList<String>();
        char opBuf = ' ';
        int from1buf = -1;
        int from2buf = -1;
        int to1buf = -1;
        int to2buf = -1;
        for ( int i = 0; i < patchSrc.length; i++ ) {
            if ( startWithNumber( patchSrc[ i ] ) ) {
                // save the old from buffers
                if ( numChunks > -1 ) {
                    // char op, int from1, int from2, int to1, int to2, String[] patch, String[] target
                    chunks[ numChunks ] = new Chunk( opBuf, from1buf, from2buf, to1buf, to2buf, fromBuf
                            .toArray( new String[ 1 ] ), toBuf.toArray( new String[ 1 ] ) );
                }
                numChunks++; // increment to the next chunk
                fromBuf = new ArrayList<String>(); // reset the arrayLists
                toBuf = new ArrayList<String>();


                // parse the change-command line
                StringTokenizer tok1;
                if ( patchSrc[ i ].contains( "a" ) ) {
                    tok1 = new StringTokenizer( patchSrc[ i ], "a" );
                    opBuf = 'a';
                }
                else if ( patchSrc[ i ].contains( "c" ) ) {
                    tok1 = new StringTokenizer( patchSrc[ i ], "c" );
                    opBuf = 'c';
                }
                else if ( patchSrc[ i ].contains( "d" ) ) {
                    tok1 = new StringTokenizer( patchSrc[ i ], "d" );
                    opBuf = 'd';
                }
                else
                    tok1 = new StringTokenizer( patchSrc[ i ], "a" );
                String fromRange = tok1.nextToken();
                String toRange = tok1.nextToken();
                log( "fromRange: " + fromRange );
                log( "toRange: " + toRange );
                StringTokenizer tokFrom = new StringTokenizer( fromRange, "," );
                String from1 = tokFrom.nextToken();
                log( "From1: " + from1 );
                String from2 = "0";
                if ( tokFrom.hasMoreTokens() ) {
                    from2 = tokFrom.nextToken();
                    log( "From2: " + from2 );
                }
                StringTokenizer tokTo = new StringTokenizer( toRange, "," );
                String to1 = tokTo.nextToken();
                log( "To1: " + to1 );
                String to2 = "0";
                if ( tokTo.hasMoreTokens() ) {
                    to2 = tokTo.nextToken();
                    log( "To2: " + to2 );
                }
                from1buf = Integer.parseInt( from1 );
                from2buf = Integer.parseInt( from2 );
                to1buf = Integer.parseInt( to1 );
                to2buf = Integer.parseInt( to2 );
                if ( chunks[ numChunks ] != null )
                    log( "--- Chunk Name: " + chunks[ numChunks ].getName() );
            }
            else if ( patchSrc[ i ].startsWith( "<" ) ) {
                String line = "";
                if ( patchSrc[ i ].length() > 2 )
                    line = patchSrc[ i ].substring( 2 ); // cut off the <
                log( "< " + line );
                fromBuf.add( line );
            }
            else if ( patchSrc[ i ].startsWith( ">" ) ) {
                String line = "";
                if ( patchSrc[ i ].length() > 2 )
                    line = patchSrc[ i ].substring( 2 ); // cut off the >
                log( "> " + line );
                toBuf.add( line );
            }
            else if ( patchSrc[ i ].startsWith( "---" ) ) {
                log( "Got separation line: " + patchSrc[ i ] );
            }
            else {
                log( "Ignoring: " + patchSrc[ i ] );
            }
        }
        // finalize
        if ( numChunks > -1 ) {
            chunks[ numChunks ] = new Chunk( opBuf, from1buf, from2buf, to1buf, to2buf, fromBuf.toArray( new String[ 1 ] ),
                    toBuf.toArray( new String[ 1 ] ) );
        }
        return chunks;
    }

    /**
     * Apply the chunks from the chunks array to the target.
     * @param targetSrc the target to be patched
     * @param chunks the chunks to apply
     * @return The patched targetSrc array.
     */
    public static String[] applyChunks( String[] targetSrc, Chunk[] chunks ) {
        // apply each chunk
        log( "--- Now applying the Chunks: ---" );
        log( "--- Target Text before: ---" );
        log( StringTools.arrayToString( targetSrc ) );
        /*
         * Some words about the offset:
         * This will adjust the following chunk operations so that they match the right lines.
         * Without this there could be errors after the first chunk was applied, if this changed
         * the number of lines. So we keep track of how many lines were added and/or removed and
         * adjust the operations by this number of lines.
         */
        int offset = 0;
        for ( int i = 0; i < chunks.length; i++ ) {
            // apply this chunk
            Chunk c = chunks[ i ];
            if ( c.getOp() == 'a' ) {
                // This will handle an "add" chunk
                targetSrc = add( c.getFrom1(), c.getTarget(), targetSrc, offset );
                offset -= c.getTarget().length;
                log( "--- Applied an ADD Chunk: ---" );
                log( c.getName() );
                log( StringTools.arrayToString( targetSrc ) );
            }
            else if ( c.getOp() == 'c' ) {
                // This will handle an "change" chunk
                targetSrc = change( c.getFrom1(), c.getFrom2(), c.getTo1(), c.getTo2(), c.getTarget(), targetSrc, offset );
                offset -= (c.getTo2() - c.getTo1()) - (c.getFrom2() - c.getFrom1());
                log( "--- Applied an CHANGE Chunk: ---" );
                log( c.getName() );
                log( StringTools.arrayToString( targetSrc ) );
            }
            else if ( c.getOp() == 'd' ) {
                // This will handle an "delete" chunk
            	int from1 = c.getFrom1();
            	int from2 = c.getFrom2();
            	if ( from2 == 0 )
            		from2 = from1;
                targetSrc = delete( from1, from2, targetSrc, offset );
                offset += from2 - from1 + 1;
                log( "--- Applied an DELETE Chunk: ---" );
                log( c.getName() );
                log( StringTools.arrayToString( targetSrc ) );
            }
        }
        log( "--- Done applying the Chunks: ---" );
        log( "--- Target Text after: ---" );
        log( StringTools.arrayToString( targetSrc ) );
        return targetSrc;
    }

    /**
     * This is like the patch utility from GNU diff/patch.
     * @param patchText The output from diff
     * @param targetText The text on which the diff will be applied
     * @return the result of the patching
     */
    public static String patchNormal( String patchText, String targetText ) {
        String[] patchSrc = StringTools.stringToArray( patchText );
        String[] targetSrc = StringTools.stringToArray( targetText );
        Chunk[] chunks = getChunks( patchSrc );
        return StringTools.arrayToString( applyChunks( targetSrc, chunks ) );
    }

    /**
     * This applys a chunk of the type delete
     * @param fromLine the first line to delete
     * @param toLine the last line to delete
     * @param baseText the text to be patched
     * @param offset the offset. see ... uhm, somewhere else
     * @return the modified baseText
     */
    private static String[] delete( int fromLine, int toLine, String[] baseText, int offset ) {
        // check input
        if ( fromLine < 0 || toLine < 0 || baseText == null ) {
            log( "### delete() - Argument ERROR" );
            return baseText;
        }
        // adjust from and to
        fromLine = fromLine - offset;
        toLine = toLine - offset;
        log( "--- Offset: " + offset );
        log( "--- Base Text before: ---" );
        log( StringTools.arrayToString( baseText ) );
        log( "--- fromLine: " + fromLine + ", toLine: " + toLine );
        ArrayList<String> buff = new ArrayList<String>();
        for ( int i = 0; i < ( fromLine - 1 ) && i < baseText.length; i++ ) {
            buff.add( baseText[ i ] );
        }
        // just "drop" the deleted lines
        for ( int j = toLine; j < baseText.length; j++ ) {
            buff.add( baseText[ j ] );
        }
        return buff.toArray( new String[ 1 ] );
    }

    /**
     * This applys a chunk of the type change.
     * @param fromLine
     * @param toLine
     * @param to1
     * @param to2
     * @param changeTo
     * @param baseText
     * @param offset
     * @return the modified baseText
     */
    private static String[] change( int fromLine, int toLine, int to1, int to2, String[] changeTo, String[] baseText, int offset ) {
        log( "######### CHANGE #########" );
        // check input
        if ( fromLine < 0 || toLine < 0 || baseText == null || changeTo == null ) {
            log( "### change() - Argument ERROR" );
            return baseText;
        }
        // adjust from and to
        fromLine = fromLine - offset;
        toLine = toLine - offset;
        log( "--- Offset: " + offset );
        log( "--- Base Text before: ---" );
        log( StringTools.arrayToString( baseText ) );
        log( "--- ChangeTo Text before: ---" );
        log( StringTools.arrayToString( changeTo ) );
        log( "--- fromLine: " + fromLine + ", toLine: " + toLine + ", changeTo.length: " + changeTo.length );
        ArrayList<String> buff = new ArrayList<String>();
        int i = 0;
        for ( ; i < ( fromLine - 1 ) && i < baseText.length; i++ ) { // stop _before_ the line(s) that has(have) to be changed
            log( "--- change() - Adding(1): " + baseText[ i ] );
            buff.add( baseText[ i ] );
        }
        for ( int j = 0; j < changeTo.length; j++ ) {
            log( "--- change() - Adding(2): " + changeTo[ j ] );
            buff.add( changeTo[ j ] );
            i++;
        }
        // fix the current line, this may have change due to insertion or removal of lines
        int linesRemoved = fromLine;
        if ( fromLine > 1 )
            linesRemoved = fromLine - 1;
        if ( toLine > 0 )
            linesRemoved = toLine - ( fromLine - 1 );
        int linesInserted = 1; // = 1?
        if ( to2 > 0 ) {
            linesInserted = (to2 - to1) + 1;
        }
        log( "--- change() - Ajusting i by: " + ( linesRemoved - linesInserted ) );
        i = i + ( linesRemoved - linesInserted );
        for ( int j = i; j < baseText.length; j++ ) {
            log( "--- change() - Adding(3): " + baseText[ j ] );
            buff.add( baseText[ j ] );
        }
        return buff.toArray( new String[ 1 ] );
    }

    /**
     * Apply an add chunk.
     * @param line
     * @param insertion
     * @param baseText
     * @param offset
     * @return the modified baseText
     */
    private static String[] add( int line, String[] insertion, String[] baseText, int offset ) {
        // check input
        if ( line < 0 || baseText == null || insertion == null ) {
            log( "### add(line: " + line + ", insertion: " + insertion + ", baseText: " + baseText + ") - Argument ERROR" );
            return baseText;
        }
        // adjust from and to
        line = line - offset;
        log( "--- Offset: " + offset );
        ArrayList<String> buff = new ArrayList<String>();
        int i = 0;
        for ( ; i < line && i < baseText.length; i++ ) {
            buff.add( baseText[ i ] );
        }
        for ( int j = 0; j < insertion.length; j++ ) {
            buff.add( insertion[ j ] );
        }
        for ( int j = i; j < baseText.length; j++ ) {
            buff.add( baseText[ j ] );
        }
        return buff.toArray( new String[ 1 ] );
    }

    private static final boolean DEBUG = false;

    private static void log( String logLine ) {
        if ( DEBUG ) {
            System.out.println( logLine );
        }
    }
}