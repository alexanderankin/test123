package jdiff.util;

import java.awt.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.TextArea;

import jdiff.DualDiff;
import jdiff.text.FileLine;

public class DualDiffUtil {

    public static boolean ignoreCaseDefault = jEdit.getBooleanProperty( "jdiff.ignore-case", false );
    public static boolean trimWhitespaceDefault = jEdit.getBooleanProperty( "jdiff.trim-whitespace", false );
    public static boolean ignoreAmountOfWhitespaceDefault = jEdit.getBooleanProperty( "jdiff.ignore-amount-whitespace", false );
    public static boolean ignoreLineSeparatorsDefault = jEdit.getBooleanProperty( "jdiff.ignore-line-separators", true );
    public static boolean ignoreAllWhitespaceDefault = jEdit.getBooleanProperty( "jdiff.ignore-all-whitespace", false );

    public static void propertiesChanged() {
        ignoreCaseDefault = jEdit.getBooleanProperty( "jdiff.ignore-case", false );
        trimWhitespaceDefault = jEdit.getBooleanProperty(
                    "jdiff.trim-whitespace", false );
        ignoreAmountOfWhitespaceDefault = jEdit.getBooleanProperty(
                    "jdiff.ignore-amount-whitespace", false );
        ignoreLineSeparatorsDefault = jEdit.getBooleanProperty(
                    "jdiff.ignore-line-separators", true );
        ignoreAllWhitespaceDefault = jEdit.getBooleanProperty(
                    "jdiff.ignore-all-whitespace", false );
    }

    /**
     * @param ta The TextArea in which to find the text of the current line.    
     * @return The text of the current line, where the current line is the line
     * containing the caret, or the empty String if the given text area is null.
     */
    public static String getCurrentLineText( TextArea ta ) {
        if ( ta == null ) {
            return "";
        }
        int caretPosition = ta.getCaretPosition();
        int lineOffset = ta.getLineOfOffset( caretPosition );
        return ta.getLineText( lineOffset );
    }

    /**
     * Scrolls the text area horizontally so the caret is visible.
     * @param ta The TextArea to scroll.
     */
    public static void alignCaretLeft( TextArea ta ) {
        int caretPhysOffset = ta.getCaretPosition();
        int caretPhysLine = ta.getLineOfOffset( caretPhysOffset );
        int caretPhysLineStartOffset = ta.getLineStartOffset( caretPhysLine );
        int caretRelOffset = caretPhysOffset - caretPhysLineStartOffset;
        // caretLocation will be (X,Y) relative to the current view area
        Point caretLocation = ta.offsetToXY( caretPhysLine, caretRelOffset );
        // to scroll the caret to the left, we need to add caretLocation.x to the horizontalOffset
        ta.setHorizontalOffset( -1 * ( Math.abs( ta.getHorizontalOffset() ) + caretLocation.x ) );
    }

    /**
     * Finds the index of the first character where the two given strings do not
     * match.
     * @param left One String to compare.
     * @param right The other String to compare.
     * @return the index of the first character where <code>left</code> is not the
     * same as <code>right</code>.
     */
    public static int firstNoMatch( String left, String right ) {
        if ( left == null || right == null ) {
            return 0;
        }
        boolean ignoreCase = jEdit.getBooleanProperty( "jdiff.ignore-case" );
        if ( ignoreCase ) {
            if ( left == right || left.equalsIgnoreCase( right ) ) {
                return -1;
            }
        }
        else {
            if ( left == right || left.equals( right ) ) {
                return -1;
            }
        }

        if ( left.charAt( 0 ) != right.charAt( 0 ) ) {
            return 0;
        }

        int minLength = Math.min( left.length(), right.length() );
        if ( ignoreCase ) {
            left = left.toLowerCase();
            right = right.toLowerCase();
        }

        int i;
        for ( i = 0; i < minLength; i++ ) {
            if ( left.charAt( i ) != right.charAt( i ) ) {
                break;
            }
        }
        return i;
    }

    /**
     * Centers both TextAreas on a diff.
     */
    public static void centerOnDiff( TextArea leftTextArea, TextArea rightTextArea ) {
        if ( leftTextArea == null || rightTextArea == null ) {
            return ;
        }

        String leftLine = DualDiffUtil.getCurrentLineText( leftTextArea );
        String rightLine = DualDiffUtil.getCurrentLineText( rightTextArea );

        int diffOffset = DualDiffUtil.firstNoMatch( leftLine, rightLine );

        leftTextArea.setCaretPosition( leftTextArea.getCaretPosition() + diffOffset, false );
        rightTextArea.setCaretPosition( rightTextArea.getCaretPosition() + diffOffset, false );

        DualDiffUtil.alignCaretLeft( leftTextArea );
        DualDiffUtil.alignCaretLeft( rightTextArea );
    }

    /**
     * Centers <code>you</code> on <code>me</code>. Useful for centering
     * dialogs on their parent frames.
     *
     * @param me   Component to use as basis for centering.
     * @param you  Component to center on <code>me</code>.
     */
    public static void center( Component me, Component you ) {
        Rectangle my = me.getBounds();
        Dimension your = you.getSize();
        int x = my.x + ( my.width - your.width ) / 2;
        if ( x < 0 )
            x = 0;
        int y = my.y + ( my.height - your.height ) / 2;
        if ( y < 0 )
            y = 0;
        you.setLocation( x, y );
    }

    public static FileLine[] getFileLines( DualDiff dualDiff, Buffer buffer ) {
        buffer.readLock();
        FileLine[] lines = new FileLine[ buffer.getLineCount() ];
        String lineSep = buffer.getStringProperty( Buffer.LINESEP );

        for ( int i = buffer.getLineCount() - 1; i >= 0; i-- ) {
            String text = buffer.getLineText( i );
            lines[ i ] = getFileLine( dualDiff, text, lineSep );
        }
        buffer.readUnlock();
        return lines;
    }

    public static FileLine getFileLine( DualDiff dualDiff, String text, String lineSep ) {
        if ( !text.endsWith( lineSep ) ) {
            text += lineSep;
        }
        String canonical = text;
        if ( dualDiff.getIgnoreCase() ) {
            canonical = canonical.toUpperCase();
        }
        
        // trim leading and trailing whitespace
        if ( dualDiff.getTrimWhitespace() && !dualDiff.getIgnoreAllWhitespace() ) {
            canonical = trimWhitespaces( canonical );
            if ( !dualDiff.getIgnoreLineSeparators() ) {
                canonical += lineSep;
            }
        }
        
        // trim leading and trailing whitespace and compress internal whitespace
        // to a single space
        if ( dualDiff.getIgnoreAmountOfWhitespace() && !dualDiff.getIgnoreAllWhitespace() ) {
            canonical = squeezeRepeatedWhitespaces( canonical );
            if ( !dualDiff.getIgnoreLineSeparators() ) {
                canonical += lineSep;
            }
        }
        
        // remove line separators
        if ( dualDiff.getIgnoreLineSeparators() ) {
            if ( canonical.endsWith( lineSep ) ) {
                canonical = canonical.substring( 0, canonical.length() - lineSep.length() );
            }
        }
        
        // remove all whitespace
        if ( dualDiff.getIgnoreAllWhitespace() ) {
            canonical = removeWhitespaces( canonical );
        }

        return new FileLine( text, canonical );
    }
    
    /**
     * Replaces repeated whitespace within the given string with a single space.
     * Leading and trailing whitespace are removed from the string.
     * @param str The string within which to compress whitespace.
     * @return The string with repeated whitespace compressed to a single space.
     * Leading and trailing whitespace are removed from the string.
     */
    public static String squeezeRepeatedWhitespaces( String str ) {
        int inLen = str.length();
        int outLen = 0;
        char[] inStr = new char[ inLen ];
        char[] outStr = new char[ inLen ];
        str.getChars( 0, inLen, inStr, 0 );

        boolean space = false;
        
        int idx = 0;
        str = trimWhitespaces( str );

        for ( ; idx < inLen; idx++ ) {
            if ( Character.isWhitespace( inStr[ idx ] ) ) {
                space = true;
                continue;
            }

            if ( space ) {
                outStr[ outLen++ ] = ' ';
                space = false;
            }
            outStr[ outLen++ ] = inStr[ idx ];
        }

        return new String( outStr, 0, outLen );
    }
    
    /**
     * Removes all whitespace from the given string.
     * @param str The string to act on.
     * @return The string with all whitespace removed.
     */
    public static String removeWhitespaces( String str ) {
        int inLen = str.length();
        int outLen = 0;
        char[] inStr = new char[ inLen ];
        char[] outStr = new char[ inLen ];
        str.getChars( 0, inLen, inStr, 0 );

        for ( int i = 0; i < inLen; i++ ) {
            if ( !Character.isWhitespace( inStr[ i ] ) ) {
                outStr[ outLen ] = inStr[ i ];
                outLen++;
            }
        }

        return new String( outStr, 0, outLen );
    }
    
    /**
     * Remove leading and trailing whitespace from the given string.
     * @param str The string to act on.
     * @return The string with all leading and trailing whitespace removed.
     */
    public static String trimWhitespaces( String str ) {
        return trimLeadingWhitespace( trimTrailingWhitespace( str ) );
    }

    /**
     * Remove leading whitespace from the given string.
     * @param str The string to act on.
     * @return The string with all leading whitespace removed.
     */
    private static String trimLeadingWhitespace( String str ) {
        int index = 0;
        int len = str.length();
        while ( ( index < len ) && Character.isWhitespace( str.charAt( index ) ) ) {
            ++index;
        }
        return str.substring( index );
    }

    /**
     * Remove trailing whitespace from the given string.
     * @param str The string to act on.
     * @return The string with all trailing whitespace removed.
     */
    private static String trimTrailingWhitespace( String str ) {
        int index = str.length() - 1;
        while ( ( index >= 0 ) && Character.isWhitespace( str.charAt( index ) ) ) {
            --index;
        }
        return str.substring( 0, index + 1 );
    }
}