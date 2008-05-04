/*
* Copyright (c) 2008, Dale Anson
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

package jdiff.util.patch.unified;

import java.io.*;
import java.util.*;

/**
 * A utility to apply a patch created as a unified diff (diff -u).  Currently
 * ONLY supports applying a unified diff to a single file at a time.  Neither
 * edit diffs (diff -e) nor context diffs (diff -c) nor "normal" diffs are
 * supported. Applying a recursive diff is not supported either (diff -ur).
 *
 * Oddly enough, I looked
 * around the internet for quite a while before writing this.  It seems there
 * are very few implementations of diff in java, and only 2 patch implementations,
 * neither of which support unified diff.
 *
 * The "Normal Diff Output" provided by the JDiff plugin is not yet supported by
 * this class.
 *
 * The SVN diff command is in unified diff format.  GNU diff with the -u option
 * is the standard for unified diff.  Both of these are supported by this class.
 */
public class Patch {

    public static String LINE_SEPARATOR = "\n";

    /**
     * This is like the patch utility from GNU diff/patch where it is expected
     * that the patch was produced as a "unified" diff, that is, with "diff -u".
     * Note that svn diff produces a unified diff that is completely acceptable
     * to this method.
     *
     * @param patchText the output from diff -u
     * @param targetText the text on which to apply the patch
     * @return the result of applying the patch
     */
    public static String patchUnified( String patchText, String targetText ) {
        List<String> patchSrc = getLines( patchText, true );
        List<String> targetSrc = getLines( targetText, false );
        List<UnifiedChunk> chunks = getUnifiedChunks( patchSrc );
        return applyUnifiedChunks( targetSrc, chunks );
    }

    /**
     * Splits the given string in to a list of lines.  If pad is true, each
     * 0-length line will be prepended with a single space character.
     * @param s the string to split into lines
     * @param pad if true, each 0-length line will be prepended with a single space
     * @return a list of strings, one per line in the original.  Line separators
     * are not included.
     */
    private static List<String> getLines( String s, boolean pad ) {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader( new StringReader( s ) );
            String line = null;
            while ( ( line = reader.readLine() ) != null ) {
                if ( line.length() == 0 && pad ) {
                    // in a patch file created by a unified diff, each line must
                    // start with +, -, or space, put a space at the front of any
                    // blank lines
                    line = " ";
                }
                lines.add( line );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return lines;
    }

    private static List<UnifiedChunk> getUnifiedChunks( List<String> patchSrc ) {
        // count the chunks
        int numChunks = 0;
        for ( String line : patchSrc ) {
            if ( line.startsWith( "@@" ) ) {
                ++numChunks;
            }
        }

        // calculate offsets to the start of each chunk. Need one more offset than
        // the actual number of chunks, the last offset points to the last line
        // of the patchSrc.  This is so the chunks can be extracted as a range
        // of lines.
        int[] chunkOffsets = new int[ numChunks + 1 ];
        int index = 0;
        for ( int i = 0; i < patchSrc.size(); i++ ) {
            String line = patchSrc.get( i );
            if ( line.startsWith( "@@" ) ) {
                chunkOffsets[ index ] = i;
                ++index;
            }
        }
        chunkOffsets[ chunkOffsets.length - 1 ] = patchSrc.size();

        // group the lines into individual chunks
        List < List < String >> chunkLines = new ArrayList < List < String >> ();
        for ( int i = 0; i < chunkOffsets.length - 1; i++ ) {
            int start = chunkOffsets[ i ];
            int end = chunkOffsets[ i + 1 ];
            List<String> lines = new ArrayList<String>();
            for ( int j = start; j < end; j++ ) {
                lines.add( patchSrc.get( j ) );
            }
            if ( lines.size() > 0 ) {
                chunkLines.add( lines );
            }
        }

        // parse the individual sets of chunk lines to create UnifiedChunks.
        // Each chunk of a patch file created by a unified diff starts with a
        // range line followed by the lines that have changed.  Changed
        // lines are marked with + (for added) or - (for removed).  Unified diff
        // also produces context lines. By default, there are 3 lines before and
        // 3 lines after that help show how the changed lines fit in the file
        // overall.  Context lines start with a space.
        List<UnifiedChunk> unifiedChunks = new ArrayList<UnifiedChunk>();
        for ( List<String> lines : chunkLines ) {
            if ( lines.size() == 0 ) {
                continue;
            }
            String rangeLine = lines.get( 0 );

            /*
            Format of the range line is "@@ -L,S +L,S @@" where
               - means the old file,
               + means the new file,
               L is the offset line in that file, this is 1 based,
               S is the number of lines following the offset line
            so "@@ -86,3 +86,12 @@"
            means replace the 3 lines starting at line 86 in the old file
            with 12 patch lines.  The comma and S value are optional,
            if missing, assume S = 1.
            */

            // parse the range line to get the L and S values
            rangeLine = rangeLine.replaceAll( "@", "" );
            rangeLine = rangeLine.trim();     // now have "L,S L,S"

            // get the 2 ranges
            String[] ranges = rangeLine.split( "[ ]" );
            if ( ranges.length != 2 ) {
                // must be exactly 2 ranges, else chunk is invalid
                continue;
            }

            // get the L and S values for the 1st range.  Need to subtract 1 from
            // the L value since it is 1-based where the list of patch file lines
            // is 0-based.
            String[] oldParts = ranges[ 0 ].split( "[,]" );
            int oldStartLine = Integer.parseInt( oldParts[ 0 ].substring( 1 ) ) - 1;
            int oldRange = 1;
            if ( oldParts.length == 2 ) {
                oldRange = Integer.parseInt( oldParts[ 1 ] );
            }

            // get the L and S values for the 2nd range
            String[] newParts = ranges[ 1 ].split( "[,]" );
            int newStartLine = Integer.parseInt( newParts[ 0 ].substring( 1 ) ) - 1;
            int newRange = 1;
            if ( newParts.length == 2 ) {
                newRange = Integer.parseInt( newParts[ 1 ] );
            }

            // count line types (-, +, space) to validate ranges.  Accumulate
            // + and space lines here too for later use.
            int minus_lines = 0;
            int plus_lines = 0;
            int space_lines = 0;
            List<String> insertLines = new ArrayList<String>();
            for ( int i = 1; i < lines.size(); i++ ) {
                String line = lines.get( i );
                if ( line.startsWith( "-" ) ) {
                    ++minus_lines;
                }
                else if ( line.startsWith( "+" ) ) {
                    ++plus_lines;
                    insertLines.add( line.substring( 1 ) );
                }
                else if ( line.startsWith( " " ) ) {
                    ++space_lines;
                    insertLines.add( line.substring( 1 ) );
                }
            }

            // validate ranges
            if ( oldRange != minus_lines + space_lines ) {
                // old range is invalid
                continue;
            }
            if ( newRange != plus_lines + space_lines ) {
                // new range is invalid
                continue;
            }

            // if here, chunk is valid
            UnifiedChunk unifiedChunk = new UnifiedChunk();
            unifiedChunk.oldStartLine = oldStartLine;
            unifiedChunk.oldRange = oldRange;
            unifiedChunk.newStartLine = newStartLine;
            unifiedChunk.newRange = newRange;
            unifiedChunk.lines = insertLines;
            unifiedChunks.add( unifiedChunk );
        }

        return unifiedChunks;
    }

    private static String applyUnifiedChunks( List<String> lines, List<UnifiedChunk>chunks ) {
        List<String> newLines = new ArrayList<String>();
        int line_number = 0;
        for ( UnifiedChunk chunk : chunks ) {
            // chunks can overlap, back up a few lines if necessary
            if ( line_number >= chunk.oldStartLine ) {
                int to_remove = line_number - chunk.oldStartLine;
                line_number = chunk.oldStartLine;
                for ( int i = 0; i < to_remove; i++ ) {
                    newLines.remove( newLines.size() - 1 );
                }
            }

            // add original lines between last chunk and next chunk
            for ( int i = line_number; i < chunk.oldStartLine; i++ ) {
                newLines.add( lines.get( line_number ) );
                ++line_number;
            }

            // add lines for chunk
            for ( int i = 0; i < chunk.lines.size(); i++ ) {
                newLines.add( chunk.lines.get( i ) );
            }

            // skip to end of old chunk
            line_number += chunk.oldRange;
        }

        // add any lines remaining after the last chunk
        for ( int i = line_number; i < lines.size(); i++ ) {
            newLines.add( lines.get( i ) );
        }

        // put together a string of the patched file
        StringBuffer sb = new StringBuffer();
        for ( String line : newLines ) {
            sb.append( line ).append( LINE_SEPARATOR );
        }
        return sb.toString();
    }
}