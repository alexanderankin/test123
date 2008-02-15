/*
* DualDiff.java
* Copyright (c) 2000, 2001, 2002 Andre Kaplan
* Copyright (c) 2006 Denis Koryavov
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

package jdiff;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.StringWriter;

import java.nio.*;

import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import jdiff.component.DiffLocalOverview;
import jdiff.component.DiffGlobalPhysicalOverview;
import jdiff.component.DiffOverview;
import jdiff.component.DiffLineOverview;
import jdiff.text.FileLine;
import jdiff.util.Diff;
import jdiff.util.DiffOutput;
import jdiff.util.DiffNormalOutput;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;
import org.gjt.sp.jedit.textarea.ScrollListener;

import org.gjt.sp.util.Log;

public class DualDiff implements EBComponent {
    private static boolean ignoreCaseDefault = jEdit.getBooleanProperty( "jdiff.ignore-case",
            false );

    private static boolean trimWhitespaceDefault = jEdit.getBooleanProperty(
                "jdiff.trim-whitespace", false );

    private static boolean ignoreAmountOfWhitespaceDefault = jEdit.getBooleanProperty(
                "jdiff.ignore-amount-whitespace", false );

    private static boolean ignoreAllWhitespaceDefault = jEdit.getBooleanProperty(
                "jdiff.ignore-all-whitespace", false );

    private static Hashtable dualDiffs = new Hashtable();

    private boolean ignoreCase;

    private boolean trimWhitespace;

    private boolean ignoreAmountOfWhitespace;

    private boolean ignoreAllWhitespace;

    private View view;

    private EditPane editPane0;

    private EditPane editPane1;

    private JEditTextArea textArea0;

    private JEditTextArea textArea1;

    private Diff.Change edits;

    private DiffOverview diffOverview0;

    private DiffOverview diffOverview1;

    private Box box0;

    private final ScrollHandler scrollHandler = new ScrollHandler();

    private DiffLineOverview diffLineOverview;

    private DualDiff( View view ) {
        this( view, ignoreCaseDefault, trimWhitespaceDefault,
              ignoreAmountOfWhitespaceDefault, ignoreAllWhitespaceDefault );
    }

    private DualDiff( View view, boolean ignoreCase, boolean trimWhitespace,
            boolean ignoreAmountOfWhiteSpace, boolean ignoreAllWhiteSpace ) {
        this.ignoreCase = ignoreCase;
        this.trimWhitespace = trimWhitespace;
        this.ignoreAmountOfWhitespace = ignoreAmountOfWhiteSpace;
        this.ignoreAllWhitespace = ignoreAllWhiteSpace;

        this.view = view;

        EditPane[] editPanes = this.view.getEditPanes();

        this.editPane0 = editPanes[ 0 ];
        this.editPane1 = editPanes[ 1 ];

        this.textArea0 = this.editPane0.getTextArea();
        this.textArea1 = this.editPane1.getTextArea();

        this.initOverviews();
        this.addOverviews();
    }

    public View getView() {
        return view;
    }

    public Diff.Change getEdits() {
        return edits;
    }

    public void handleMessage( EBMessage message ) {
        if ( message instanceof BufferUpdate ) {
            BufferUpdate bu = ( BufferUpdate ) message;
            Buffer b0 = ( Buffer ) this.textArea0.getBuffer();
            Buffer b1 = ( Buffer ) this.textArea1.getBuffer();
            if ( bu.getBuffer() != b0 && bu.getBuffer() != b1 ) {
                // Not concerned by this message
                return ;
            }
            if ( bu.getWhat() == BufferUpdate.LOADED ) {
                this.refresh();
            }
        }
        else if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            EditPane editPane = epu.getEditPane();
            View view = editPane.getView();
            if ( !DualDiff.isEnabledFor( view ) ) {
                return ;
            }
            if ( epu.getWhat() == EditPaneUpdate.CREATED ) {
                DualDiff.editPaneCreated( view );
            }
            else if ( epu.getWhat() == EditPaneUpdate.DESTROYED ) {
                DualDiff.editPaneDestroyed( view, editPane );
            }
            else if ( epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED ) {
                DualDiff.editPaneBufferChanged( view );
            }
        }
    }

    public boolean getIgnoreCase() {
        return this.ignoreCase;
    }

    public void setIgnoreCase( boolean ignoreCase ) {
        this.ignoreCase = ignoreCase;
    }

    public void toggleIgnoreCase() {
        this.ignoreCase = !this.ignoreCase;
    }

    public boolean getTrimWhitespace() {
        return this.trimWhitespace;
    }

    public void setTrimWhitespace( boolean trimWhitespace ) {
        this.trimWhitespace = trimWhitespace;
    }

    public void toggleTrimWhitespace() {
        this.trimWhitespace = !this.trimWhitespace;
    }

    public boolean getIgnoreAmountOfWhitespace() {
        return this.ignoreAmountOfWhitespace;
    }

    public void setIgnoreAmountOfWhitespace( boolean ignoreAmountOfWhitespace ) {
        this.ignoreAmountOfWhitespace = ignoreAmountOfWhitespace;
    }

    public void toggleIgnoreAmountOfWhitespace() {
        this.ignoreAmountOfWhitespace = !this.ignoreAmountOfWhitespace;
    }

    public boolean getIgnoreAllWhitespace() {
        return this.ignoreAllWhitespace;
    }

    public void setIgnoreAllWhitespace( boolean ignoreAllWhitespace ) {
        this.ignoreAllWhitespace = ignoreAllWhitespace;
    }

    public void toggleIgnoreAllWhitespace() {
        this.ignoreAllWhitespace = !this.ignoreAllWhitespace;
    }

    public void setDiffLineOverview( DiffLineOverview diffLineOverview ) {
        this.diffLineOverview = diffLineOverview;
    }

    public DiffLineOverview getDiffLineOverview() {
        return diffLineOverview;
    }

    // initialize the overviews and merge controls
    private void initOverviews() {
        Buffer buf0 = this.editPane0.getBuffer();
        Buffer buf1 = this.editPane1.getBuffer();

        if ( !buf0.isLoaded() || !buf1.isLoaded() ) {
            this.edits = null;
            this.diffOverview0 = new DiffLocalOverview( DualDiff.this );
            this.diffOverview1 = new DiffGlobalPhysicalOverview( DualDiff.this );
        }
        else {
            FileLine[] fileLines0 = this.getFileLines( buf0 );
            FileLine[] fileLines1 = this.getFileLines( buf1 );

            Diff d = new Diff( fileLines0, fileLines1 );
            this.edits = d.diff_2( false );
            this.diffOverview0 = new DiffLocalOverview( DualDiff.this );
            this.diffOverview1 = new DiffGlobalPhysicalOverview( DualDiff.this );
        }
    }

    private void addOverviews() {
        this.textArea0.addLeftOfScrollBar( this.diffOverview0 );
        this.textArea1.addLeftOfScrollBar( this.diffOverview1 );
    }

    // remove overviews and merge controls
    private void removeOverviews() {
        this.textArea0.removeLeftOfScrollBar( this.diffOverview0 );
        this.textArea1.removeLeftOfScrollBar( this.diffOverview1 );
    }

    private void refresh() {
        this.removeHandlers();
        this.disableHighlighters();

        this.removeOverviews();
        this.initOverviews();
        this.addOverviews();

        this.enableHighlighters();
        this.addHandlers();

        diffLineOverview.clear();
        EditPane editPane = view.getEditPane();
        final int caret = editPane.getTextArea().getCaretPosition();
        this.diffOverview0.synchroScrollRight();
        this.diffOverview1.repaint();
    }

    private void enableHighlighters() {
        DiffHighlight diffHighlight0 = ( DiffHighlight ) DiffHighlight
                .getHighlightFor( this.editPane0 );

        if ( diffHighlight0 == null ) {
            diffHighlight0 = ( DiffHighlight ) DiffHighlight.addHighlightTo(
                        this.editPane0, this.edits, DiffHighlight.LEFT );
            this.textArea0.getPainter().addExtension(
                TextAreaPainter.BELOW_SELECTION_LAYER, diffHighlight0 );
        }
        else {
            diffHighlight0.setEdits( this.edits );
            diffHighlight0.setPosition( DiffHighlight.LEFT );
        }

        DiffHighlight diffHighlight1 = ( DiffHighlight ) DiffHighlight
                .getHighlightFor( this.editPane1 );

        if ( diffHighlight1 == null ) {
            diffHighlight1 = ( DiffHighlight ) DiffHighlight.addHighlightTo(
                        this.editPane1, this.edits, DiffHighlight.RIGHT );
            this.textArea1.getPainter().addExtension(
                TextAreaPainter.BELOW_SELECTION_LAYER, diffHighlight1 );
        }
        else {
            diffHighlight1.setEdits( this.edits );
            diffHighlight1.setPosition( DiffHighlight.RIGHT );
        }

        diffHighlight0.setEnabled( true );
        diffHighlight0.updateTextArea();
        diffHighlight1.setEnabled( true );
        diffHighlight1.updateTextArea();
    }

    private void disableHighlighters() {
        DiffHighlight diffHighlight0 = ( DiffHighlight ) DiffHighlight
                .getHighlightFor( this.editPane0 );

        if ( diffHighlight0 != null ) {
            diffHighlight0.setEnabled( false );
            diffHighlight0.updateTextArea();
        }

        DiffHighlight diffHighlight1 = ( DiffHighlight ) DiffHighlight
                .getHighlightFor( this.editPane1 );

        if ( diffHighlight1 != null ) {
            diffHighlight1.setEnabled( false );
            diffHighlight1.updateTextArea();
        }
    }

    private void addHandlers() {
        this.textArea0.addScrollListener( this.scrollHandler );
        this.textArea0.addFocusListener( this.scrollHandler );

        this.textArea1.addScrollListener( this.scrollHandler );
        this.textArea1.addFocusListener( this.scrollHandler );
    }

    private void removeHandlers() {
        this.textArea0.removeScrollListener( this.scrollHandler );
        this.textArea0.removeFocusListener( this.scrollHandler );

        this.textArea1.removeScrollListener( this.scrollHandler );
        this.textArea1.removeFocusListener( this.scrollHandler );
    }

    private FileLine[] getFileLines( Buffer buffer ) {
        FileLine[] lines = new FileLine[ buffer.getLineCount() ];

        for ( int i = buffer.getLineCount() - 1; i >= 0; i-- ) {
            int start = buffer.getLineStartOffset( i );
            int end = buffer.getLineEndOffset( i );

            // We get the line i without the line separator (always
            // \n)
            int len = ( end - 1 ) - start;
            if ( len == 0 ) {
                lines[ i ] = new FileLine( "", "" );
                continue;
            }

            String text = "";
            String canonical = "";

            text = buffer.getText( start, len );
            canonical = text;
            if ( ignoreCase ) {
                canonical = canonical.toUpperCase();
            }
            if ( trimWhitespace && !ignoreAllWhitespace ) {
                canonical = trimWhitespaces( canonical );
            }
            if ( ignoreAmountOfWhitespace && !ignoreAllWhitespace ) {
                canonical = squeezeRepeatedWhitespaces( canonical );
            }
            if ( ignoreAllWhitespace ) {
                canonical = removeWhitespaces( canonical );
            }

            lines[ i ] = new FileLine( text, canonical );
        }

        return lines;
    }

    public static String squeezeRepeatedWhitespaces( String str ) {
        int inLen = str.length();
        int outLen = 0;
        char[] inStr = new char[ inLen ];
        char[] outStr = new char[ inLen ];
        str.getChars( 0, inLen, inStr, 0 );

        boolean space = false;

        int idx = 0;
        // Skip leading whitespaces
        while ( idx < inLen && Character.isWhitespace( inStr[ idx ] ) ) {
            idx++;
        }

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

    public static String trimWhitespaces( String str ) {
        int inLen = str.length();
        char[] inStr = new char[ inLen ];
        str.getChars( 0, inLen, inStr, 0 );

        // Skip leading whitespaces
        int startIdx = 0;
        while ( ( startIdx < inLen ) && Character.isWhitespace( inStr[ startIdx ] ) ) {
            startIdx++;
        }

        // Skip trailing whitespaces
        int endIdx = inLen - 1;
        while ( ( endIdx >= startIdx ) && Character.isWhitespace( inStr[ endIdx ] ) ) {
            endIdx--;
        }

        if ( ( startIdx > 0 ) || ( endIdx < inLen - 1 ) ) {
            return new String( inStr, startIdx, endIdx - startIdx + 1 );
        }
        else {
            return str;
        }
    }

    public static DualDiff getDualDiffFor( View view ) {
        return ( DualDiff ) dualDiffs.get( view );
    }

    public static boolean isEnabledFor( View view ) {
        return ( dualDiffs.get( view ) != null );
    }

    private static void editPaneCreated( View view ) {
        DualDiff.removeFrom( view );
    }

    private static void editPaneDestroyed( View view, EditPane editPane ) {
        DualDiff.removeFrom( view );
        DiffHighlight.removeHighlightFrom( editPane );
    }

    private static void editPaneBufferChanged( View view ) {
        DualDiff.refreshFor( view );
    }

    public static void toggleFor( final View view ) {
        Runnable r = new Runnable() {
                    public void run() {
                        if ( DualDiff.isEnabledFor( view ) ) {
                            DualDiff.removeFrom( view );
                            view.getDockableWindowManager().hideDockableWindow( "jdiff-lines" );
                        }
                        else {
                            EditPane[] editPanes = view.getEditPanes();
                            if ( editPanes.length != 2 ) {
                                Log.log( Log.DEBUG, DualDiff.class,
                                        "Splitting: the view has to be split in two" );
                                if ( editPanes.length > 2 ) {
                                    view.unsplit();
                                }
                                view.splitVertically();

                                // danson, make sure the divider is in the middle.  For some reason,
                                // the left side would be much smaller than the right side, this
                                // takes care of that.
                                JSplitPane sp = view.getSplitPane();
                                sp.setDividerLocation( 0.5 );
                            }
                            DualDiff.addTo( view );
                            DockableWindowManager dwm = view.getDockableWindowManager();
                            if ( !dwm.isDockableWindowVisible( "jdiff-lines" ) ) {
                                if ( dwm.getDockableWindow( "jdiff-lines" ) == null ) {
                                    dwm.addDockableWindow( "jdiff-lines" );
                                }
                                if ( jEdit.getBooleanProperty( "jdiff.auto-show-dockable" ) ) {
                                    dwm.showDockableWindow( "jdiff-lines" );
                                }
                            }
                        }

                        view.invalidate();
                        view.validate();
                    }
                };
        SwingUtilities.invokeLater( r );
    }

    public static void refreshFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            view.getToolkit().beep();
        }
    }

    public static boolean getIgnoreCaseFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        JSplitPane sp = view.getSplitPane();
        sp.setDividerLocation( 0.5 );

        return dualDiff.getIgnoreCase();
    }

    public static void toggleIgnoreCaseFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleIgnoreCase();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            view.getToolkit().beep();
        }
    }

    public static boolean getTrimWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        return dualDiff.getTrimWhitespace();
    }

    public static void toggleTrimWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleTrimWhitespace();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            view.getToolkit().beep();
        }
    }

    public static boolean getIgnoreAmountOfWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        return dualDiff.getIgnoreAmountOfWhitespace();
    }

    public static void toggleIgnoreAmountOfWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleIgnoreAmountOfWhitespace();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            view.getToolkit().beep();
        }
    }

    public static boolean getIgnoreAllWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff == null ) {
            return false;
        }

        return dualDiff.getIgnoreAllWhitespace();
    }

    public static void toggleIgnoreAllWhitespaceFor( View view ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( view );
        if ( dualDiff != null ) {
            dualDiff.toggleIgnoreAllWhitespace();
            dualDiff.refresh();

            JSplitPane sp = view.getSplitPane();
            sp.setDividerLocation( 0.5 );

            view.invalidate();
            view.validate();
        }
        else {
            view.getToolkit().beep();
        }
    }

    public Font getFont() {
        return textArea0.getPainter().getFont();
    }

    public Color getBackground() {
        return textArea0.getPainter().getBackground();
    }

    public static void nextDiff( EditPane editPane ) {
        // danson, the nextDiff0 and nextDiff1 weren't working correctly, they
        // were using the first visible line rather than the caret line to
        // calculate the next diff position.  Using the first physical line
        // meant the "next" diff was always the first visible diff, even if
        // there were 2 visible diffs.
        DualDiff dualDiff = DualDiff.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            editPane.getToolkit().beep();
            return ;
        }

        if ( dualDiff.editPane0 == editPane ) {
            dualDiff.nextDiff0();
        }
        else if ( dualDiff.editPane1 == editPane ) {
            dualDiff.nextDiff1();
        }
        else {
            editPane.getToolkit().beep();
        }
    }

    public static void prevDiff( EditPane editPane ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            editPane.getToolkit().beep();
            return ;
        }

        if ( dualDiff.editPane0 == editPane ) {
            dualDiff.prevDiff0();
        }
        else if ( dualDiff.editPane1 == editPane ) {
            dualDiff.prevDiff1();
        }
        else {
            editPane.getToolkit().beep();
        }
    }

    public static void moveRight( EditPane editPane ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            editPane.getToolkit().beep();
            return ;
        }
        editPane = editPane.getView().getEditPanes() [ 0 ];
        dualDiff.diffOverview0.moveRight( editPane.getTextArea().getCaretLine() );
    }

    public static void moveLeft( EditPane editPane ) {
        DualDiff dualDiff = DualDiff.getDualDiffFor( editPane.getView() );
        if ( dualDiff == null ) {
            editPane.getToolkit().beep();
            return ;
        }
        editPane = editPane.getView().getEditPanes() [ 1 ];
        dualDiff.diffOverview0.moveLeft( editPane.getTextArea().getCaretLine() );
    }

    public static void diffNormalOutput( View view ) {
        if ( !DualDiff.isEnabledFor( view ) ) {
            view.getToolkit().beep();
            return ;
        }

        DualDiff dualDiff = DualDiff.getDualDiffFor( view );

        // Generate the script
        Buffer buf0 = dualDiff.editPane0.getBuffer();
        Buffer buf1 = dualDiff.editPane1.getBuffer();

        FileLine[] fileLines0 = dualDiff.getFileLines( buf0 );
        FileLine[] fileLines1 = dualDiff.getFileLines( buf1 );

        Diff d = new Diff( fileLines0, fileLines1 );
        Diff.Change script = d.diff_2( false );

        // Files are identical: return
        if ( script == null ) {
            GUIUtilities.message( view, "jdiff.identical-files", null );
            return ;
        }

        // Generate the normal output
        StringWriter sw = new StringWriter();
        DiffOutput diffOutput = new DiffNormalOutput( fileLines0, fileLines1 );
        diffOutput.setOut( new BufferedWriter( sw ) );
        diffOutput.setLineSeparator( "\n" );
        try {
            diffOutput.writeScript( script );
        }
        catch ( IOException ioe ) {
            Log.log( Log.DEBUG, DualDiff.class, ioe );
        }

        // Get/create the output view and create a new buffer
        View outputView = jEdit.getFirstView();
        for ( ; outputView != null; outputView = outputView.getNext() ) {
            if ( !DualDiff.isEnabledFor( outputView ) ) {
                break;
            }
        }
        if ( outputView == null ) {
            outputView = jEdit.newView( view, view.getBuffer() );
        }
        Buffer outputBuffer = jEdit.newFile( outputView );

        // Insert the normal output into the buffer
        String s = sw.toString();
        outputBuffer.insert( 0, s );
        // When the string ends with a newline, the generated buffer
        // adds one extra newline so we remove it
        if ( s.endsWith( "\n" ) && outputBuffer.getLength() > 0 ) {
            outputBuffer.remove( outputBuffer.getLength() - 1, 1 );
        }
    }

    private void nextDiff0() {
        Diff.Change hunk = this.edits;
        int firstLine = this.textArea0.getCaretLine();
        for ( ; hunk != null; hunk = hunk.link ) {
            if ( hunk.line0 > firstLine + ( ( hunk.deleted == 0 ) ? 1 : 0 ) ) {
                int line = 0;
                if ( hunk.deleted == 0 && hunk.line0 > 0 ) {
                    line = hunk.line0 - 1;
                }
                else {
                    line = hunk.line0;
                }
                this.textArea0.setFirstLine( line );

                // move the caret to the start of the first line of the diff
                int caret_position = textArea0.getLineStartOffset( line );
                this.textArea0.setCaretPosition( caret_position );
                caret_position = textArea1.getLineStartOffset( hunk.line1 );
                this.textArea1.setCaretPosition( caret_position );

                if ( this.textArea0.getFirstLine() != line ) {
                    this.textArea0.getToolkit().beep();
                }
                return ;
            }
        }

        this.textArea1.getToolkit().beep();
    }

    private void nextDiff1() {
        Diff.Change hunk = this.edits;
        int firstLine = this.textArea1.getCaretLine();
        for ( ; hunk != null; hunk = hunk.link ) {
            if ( hunk.line1 > firstLine + ( ( hunk.inserted == 0 ) ? 1 : 0 ) ) {
                int line = 0;
                if ( hunk.inserted == 0 && hunk.line1 > 0 ) {
                    line = hunk.line1 - 1;
                }
                else {
                    line = hunk.line1;
                }
                this.textArea1.setFirstLine( line );

                // move the caret to the start of the first line of the diff
                int caret_position = textArea1.getLineStartOffset( line );
                this.textArea1.setCaretPosition( caret_position );
                caret_position = textArea0.getLineStartOffset( hunk.line0 );
                this.textArea0.setCaretPosition( caret_position );

                if ( this.textArea1.getFirstLine() != line ) {
                    this.textArea1.getToolkit().beep();
                }
                return ;
            }
        }

        this.textArea1.getToolkit().beep();
    }

    private void prevDiff0() {
        Diff.Change hunk = this.edits;
        int firstLine = this.textArea0.getFirstLine();
        for ( ; hunk != null; hunk = hunk.link ) {
            if ( hunk.line0 < firstLine ) {
                if ( hunk.link == null || hunk.link.line0 >= firstLine ) {
                    int line = 0;
                    if ( hunk.deleted == 0 && hunk.line0 > 0 ) {
                        line = hunk.line0 - 1;
                    }
                    else {
                        line = hunk.line0;
                    }
                    this.textArea0.setFirstLine( line );

                    // move the caret to the start of the first line of the diff
                    int caret_position = textArea0.getLineStartOffset( line );
                    this.textArea0.setCaretPosition( caret_position );
                    caret_position = textArea1.getLineStartOffset( hunk.line1 );
                    this.textArea1.setCaretPosition( caret_position );

                    if ( this.textArea0.getFirstLine() != line ) {
                        this.textArea0.getToolkit().beep();
                    }
                    return ;
                }
            }
        }

        this.textArea0.getToolkit().beep();
    }

    private void prevDiff1() {
        Diff.Change hunk = this.edits;
        int firstLine = this.textArea1.getFirstLine();
        for ( ; hunk != null; hunk = hunk.link ) {
            if ( hunk.line1 < firstLine ) {
                if ( hunk.link == null || hunk.link.line1 >= firstLine ) {
                    int line = 0;
                    if ( hunk.inserted == 0 && hunk.line1 > 0 ) {
                        line = hunk.line1 - 1;
                    }
                    else {
                        line = hunk.line1;
                    }
                    this.textArea1.setFirstLine( line );

                    // move the caret to the start of the first line of the diff
                    int caret_position = textArea1.getLineStartOffset( line );
                    this.textArea1.setCaretPosition( caret_position );
                    caret_position = textArea0.getLineStartOffset( hunk.line0 );
                    this.textArea0.setCaretPosition( caret_position );

                    if ( this.textArea1.getFirstLine() != line ) {
                        this.textArea1.getToolkit().beep();
                    }
                    return ;
                }
            }
        }

        this.textArea1.getToolkit().beep();
    }

    private static void addTo( View view ) {
        DualDiff dualDiff = new DualDiff( view );

        EditBus.addToBus( dualDiff );

        dualDiff.enableHighlighters();
        dualDiff.addHandlers();
        jdiff.component.DiffLineOverview diffLineOverview = new jdiff.component.DiffLineOverview( dualDiff, view );
        dualDiff.setDiffLineOverview( diffLineOverview );

        dualDiff.diffOverview0.synchroScrollRight();
        dualDiff.diffOverview1.repaint();

        dualDiffs.put( view, dualDiff );
        diffLineOverview.reset();
    }

    private static void removeFrom( View view ) {
        DualDiff dualDiff = ( DualDiff ) dualDiffs.get( view );

        EditBus.removeFromBus( dualDiff );

        if ( dualDiff != null ) {
            dualDiff.removeHandlers();
            dualDiff.disableHighlighters();

            dualDiff.removeOverviews();

            dualDiffs.remove( view );

            dualDiff.getDiffLineOverview().setModel( null );
        }
    }

    private class ScrollHandler implements ScrollListener, FocusListener, MouseListener {
        private Runnable syncWithRightVert = new Runnable() {
                    public void run() {
                        DualDiff.this.diffOverview0.repaint();
                        DualDiff.this.diffOverview0.synchroScrollRight();

                        DualDiff.this.diffOverview1.repaint();
                    }
                };

        private Runnable syncWithLeftVert = new Runnable() {
                    public void run() {
                        DualDiff.this.diffOverview1.repaint();
                        DualDiff.this.diffOverview1.synchroScrollLeft();

                        DualDiff.this.diffOverview0.repaint();
                    }
                };

        private Runnable syncWithRightHoriz = new Runnable() {
                    public void run() {
                        DualDiff.this.textArea1.setHorizontalOffset( DualDiff.this.textArea0
                                .getHorizontalOffset() );
                    }
                };

        private Runnable syncWithLeftHoriz = new Runnable() {
                    public void run() {
                        DualDiff.this.textArea0.setHorizontalOffset( DualDiff.this.textArea1
                                .getHorizontalOffset() );
                    }
                };


        public void scrolledHorizontally( TextArea textArea ) {
            // Log.log(Log.DEBUG, this, "**** Adjustment " + e);

            if ( textArea == DualDiff.this.textArea0 ) {
                SwingUtilities.invokeLater( this.syncWithRightHoriz );
            }
            else if ( textArea == DualDiff.this.textArea1 ) {
                SwingUtilities.invokeLater( this.syncWithLeftHoriz );
            }
            else {}
        }

        public void scrolledVertically( TextArea textArea ) {
            if ( textArea == DualDiff.this.textArea0 ) {
                SwingUtilities.invokeLater( this.syncWithRightVert );
            }
            else if ( textArea == DualDiff.this.textArea1 ) {
                SwingUtilities.invokeLater( this.syncWithLeftVert );
            }
            else {}
        }

        public void focusGained( FocusEvent e ) {
            Log.log( Log.DEBUG, this, "**** focusGained " + e );
            if ( jEdit.getBooleanProperty( "jdiff.auto-show-dockable" ) ) {
                if ( !view.getDockableWindowManager().isDockableWindowVisible( "jdiff-lines" ) ) {
                    view.getDockableWindowManager().showDockableWindow( "jdiff-lines" );
                }
            }
        }

        public void focusLost( FocusEvent e ) {
            Log.log( Log.DEBUG, this, "**** focusLost " + e );
        }

        public void mouseClicked( MouseEvent e ) {}

        public void mouseEntered( MouseEvent e ) {}

        public void mouseExited( MouseEvent e ) {}

        public void mousePressed( MouseEvent e ) {
            Log.log( Log.DEBUG, this, "**** mousePressed " + e );
        }

        public void mouseReleased( MouseEvent e ) {
            Log.log( Log.DEBUG, this, "**** mouseReleased " + e );
        }

    }

    public static void propertiesChanged() {
        boolean newIgnoreCaseDefault = jEdit.getBooleanProperty( "jdiff.ignore-case", false );
        boolean newTrimWhitespaceDefault = jEdit.getBooleanProperty(
                    "jdiff.trim-whitespace", false );
        boolean newIgnoreAmountOfWhitespaceDefault = jEdit.getBooleanProperty(
                    "jdiff.ignore-amount-whitespace", false );
        boolean newIgnoreAllWhitespaceDefault = jEdit.getBooleanProperty(
                    "jdiff.ignore-all-whitespace", false );

        if ( ( newIgnoreCaseDefault != ignoreCaseDefault )
                || ( newTrimWhitespaceDefault != trimWhitespaceDefault )
                || ( newIgnoreAmountOfWhitespaceDefault != ignoreAmountOfWhitespaceDefault )
                || ( newIgnoreAllWhitespaceDefault != ignoreAllWhitespaceDefault ) ) {
            ignoreCaseDefault = newIgnoreCaseDefault;
            trimWhitespaceDefault = newTrimWhitespaceDefault;
            ignoreAmountOfWhitespaceDefault = newIgnoreAmountOfWhitespaceDefault;
            ignoreAllWhitespaceDefault = newIgnoreAllWhitespaceDefault;
        }
    }
}
