/*
Adapted from:
Core SWING Advanced Programming
By Kim Topley
ISBN: 0 13 083292 8
Publisher: Prentice Hall
*/
package ise.plugin.svn.gui;

import java.awt.Color;

import java.util.regex.*;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * Searches a text area for bug strings/ids as specified in bugtraq:logregex.
 */
public class WordSearcher {

    protected JTextComponent comp;
    private String regex0;
    private String regex1;
    protected Highlighter.HighlightPainter painter;

    /**
     * @param comp the text area to search in
     * @param regex0 See the bugtraq docs, this is the first regex from bugtraq:logregex
     * @param regex1 See the bugtraq docs, this is the second regex from bugtraq:logregex
     */
    public WordSearcher( JTextComponent comp, String regex0, String regex1 ) {
        this.comp = comp;
        this.regex0 = regex0;
        this.regex1 = regex1;
        // TODO: use null instead of GREEN? null will cause the text area selection
        // color as provided by the look and feel to be used.
        this.painter = new UnderlineHighlighter.UnderlineHighlightPainter( Color.GREEN );
    }

    /**
     * Search for a word matching the bugtraq regex and highlight the
     * first occurance.
     * @return true if the search was successful
     */
    public boolean search() {
        Highlighter highlighter = comp.getHighlighter();

        // Remove any existing highlights for last word
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for ( int i = 0; i < highlights.length; i++ ) {
            Highlighter.Highlight h = highlights[ i ];
            if ( h.getPainter() instanceof UnderlineHighlighter.UnderlineHighlightPainter ) {
                highlighter.removeHighlight( h );
            }
        }

        if ( regex0 == null || regex0.equals( "" ) ) {
            return false;
        }

        // Look for the word we are given
        String content = null;
        try {
            Document d = comp.getDocument();
            content = d.getText( 0, d.getLength() );
        }
        catch ( BadLocationException e ) {
            // Cannot happen
            return false;
        }

        if ( regex1 == null || regex1.length() == 0 ) {
            // only have regex0 to find bug pattern
            Pattern p = Pattern.compile( regex0, Pattern.DOTALL );
            Matcher m = p.matcher( content );
            if ( m.find() ) {
                int start = m.start();
                int end = m.end();
                try {
                    highlighter.addHighlight( start, end, painter );
                    return true;
                }
                catch ( BadLocationException e ) {  // NOPMD
                    // Nothing to do
                }
            }
        }
        else {
            // have both regex0 and regex1.  Use regex0 to find the bug id string,
            // then use regex1 to find the actual bug id within that string.
            Pattern p = Pattern.compile( regex0, Pattern.DOTALL );
            Matcher m = p.matcher( content );
            if ( m.find() ) {
                int start = m.start();
                int end = m.end();
                String bug_string = content.substring( start, end );
                p = Pattern.compile( regex1, Pattern.DOTALL );
                m = p.matcher( bug_string );
                if ( m.find() ) {
                    int s = m.start();
                    int e = m.end();
                    int length = e - s;
                    start += s;
                    try {
                        highlighter.addHighlight( start, start + length, painter );
                        return true;
                    }
                    catch ( BadLocationException ble ) {  // NOPMD
                        // Nothing to do
                    }
                }
            }
        }
        return false;
    }
}