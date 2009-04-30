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

import java.io.*;
import java.nio.*;

import java.util.*;
import javax.swing.*;

import jdiff.component.DiffLocalOverview;
import jdiff.component.DiffGlobalPhysicalOverview;
import jdiff.component.DiffOverview;
import jdiff.component.DiffLineOverview;
import jdiff.component.DiffTextAreaModel;
import jdiff.text.FileLine;
import jdiff.util.Diff;
import jdiff.util.DualDiffUtil;
import jdiff.util.ScrollHandler;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;


public class DualDiff implements EBComponent {
    // diff options
    private boolean ignoreCase;
    private boolean trimWhitespace;
    private boolean ignoreAmountOfWhitespace;
    private boolean ignoreAllWhitespace;

    // the actual diffs
    private Diff.Change edits;

    // gui objects this dual diff is acting on
    private View view;
    private EditPane editPane0;
    private EditPane editPane1;
    private JEditTextArea textArea0;
    private JEditTextArea textArea1;
    private DiffOverview diffOverview0;
    private DiffOverview diffOverview1;
    private DiffLineOverview diffLineOverview;
    private ScrollHandler scrollHandler;

    protected DualDiff( View view ) {
        this( view, DualDiffUtil.ignoreCaseDefault, DualDiffUtil.trimWhitespaceDefault,
              DualDiffUtil.ignoreAmountOfWhitespaceDefault, DualDiffUtil.ignoreAllWhitespaceDefault );
    }

    protected DualDiff( View view, boolean ignoreCase, boolean trimWhitespace,
            boolean ignoreAmountOfWhiteSpace, boolean ignoreAllWhiteSpace ) {

        // diff options
        this.ignoreCase = ignoreCase;
        this.trimWhitespace = trimWhitespace;
        this.ignoreAmountOfWhitespace = ignoreAmountOfWhiteSpace;
        this.ignoreAllWhitespace = ignoreAllWhiteSpace;

        // gui objects
        this.view = view;
        EditPane[] editPanes = this.view.getEditPanes();
        this.editPane0 = editPanes[ 0 ];
        this.editPane1 = editPanes[ 1 ];
        this.textArea0 = this.editPane0.getTextArea();
        this.textArea1 = this.editPane1.getTextArea();
        scrollHandler = new ScrollHandler( this );

        // initialize
        refresh();
    }

    public EditPane getEditPane0() {
        return editPane0;
    }

    public EditPane getEditPane1() {
        return editPane1;
    }

    public TextArea getTextArea0() {
        return textArea0;
    }

    public TextArea getTextArea1() {
        return textArea1;
    }

    public DiffOverview getDiffOverview0() {
        return diffOverview0;
    }

    public DiffOverview getDiffOverview1() {
        return diffOverview1;
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
            Buffer b0 = ( Buffer ) textArea0.getBuffer();
            Buffer b1 = ( Buffer ) textArea1.getBuffer();
            if ( bu.getBuffer() != b0 && bu.getBuffer() != b1 ) {
                // Not concerned by this message
                return ;
            }
            if ( bu.getWhat() == BufferUpdate.LOADED || bu.getWhat() == BufferUpdate.SAVED || bu.getWhat() == BufferUpdate.DIRTY_CHANGED ) {
                refresh();
            }
        }
        else if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            EditPane editPane = epu.getEditPane();
            if ( !view.equals( editPane.getView() ) ) {
                // not my view
                return ;
            }
            if ( epu.getWhat() == EditPaneUpdate.CREATED || epu.getWhat() == EditPaneUpdate.DESTROYED ) {
                remove();
            }
            else if ( epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED ) {
                refresh();
            }
        }
        else if ( message instanceof PropertiesChanged ) {
            // update properties
            setIgnoreCase( jEdit.getBooleanProperty( "jdiff.ignore-case", false ) );
            setTrimWhitespace( jEdit.getBooleanProperty( "jdiff.trim-whitespace", false ) );
            setIgnoreAmountOfWhitespace( jEdit.getBooleanProperty( "jdiff.ignore-amount-whitespace", false ) );
            setIgnoreAllWhitespace( jEdit.getBooleanProperty( "jdiff.ignore-all-whitespace", false ) );
            refresh();
        }
    }

    public boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase( boolean ignoreCase ) {
        this.ignoreCase = ignoreCase;
    }

    public void toggleIgnoreCase() {
        ignoreCase = !ignoreCase;
    }

    public boolean getTrimWhitespace() {
        return trimWhitespace;
    }

    public void setTrimWhitespace( boolean trimWhitespace ) {
        this.trimWhitespace = trimWhitespace;
    }

    public void toggleTrimWhitespace() {
        trimWhitespace = !trimWhitespace;
    }

    public boolean getIgnoreAmountOfWhitespace() {
        return ignoreAmountOfWhitespace;
    }

    public void setIgnoreAmountOfWhitespace( boolean ignoreAmountOfWhitespace ) {
        this.ignoreAmountOfWhitespace = ignoreAmountOfWhitespace;
    }

    public void toggleIgnoreAmountOfWhitespace() {
        ignoreAmountOfWhitespace = !ignoreAmountOfWhitespace;
    }

    public boolean getIgnoreAllWhitespace() {
        return ignoreAllWhitespace;
    }

    public void setIgnoreAllWhitespace( boolean ignoreAllWhitespace ) {
        this.ignoreAllWhitespace = ignoreAllWhitespace;
    }

    public void toggleIgnoreAllWhitespace() {
        ignoreAllWhitespace = !ignoreAllWhitespace;
    }

    public void setDiffLineOverview( DiffLineOverview diffLineOverview ) {
        this.diffLineOverview = diffLineOverview;
    }

    public DiffLineOverview getDiffLineOverview() {
        return diffLineOverview;
    }

    private void installOverviews() {
        Buffer buf0 = editPane0.getBuffer();
        Buffer buf1 = editPane1.getBuffer();

        if ( !buf0.isLoaded() || !buf1.isLoaded() ) {
            edits = null;
            diffOverview0 = new DiffLocalOverview( this );
            diffOverview1 = new DiffGlobalPhysicalOverview( this );
        }
        else {
            FileLine[] fileLines0 = DualDiffUtil.getFileLines( this, buf0 );
            FileLine[] fileLines1 = DualDiffUtil.getFileLines( this, buf1 );

            Diff d = new Diff( fileLines0, fileLines1 );
            edits = d.diff_2();
            diffOverview0 = new DiffLocalOverview( this );
            diffOverview1 = new DiffGlobalPhysicalOverview( this );
            diffLineOverview = new DiffLineOverview( this, view );
        }
        textArea0.addLeftOfScrollBar( diffOverview0 );
        textArea1.addLeftOfScrollBar( diffOverview1 );

        setDiffLineOverview( diffLineOverview );
    }

    // remove overviews and merge controls
    protected void removeOverviews() {
        if ( textArea0 != null && diffOverview0 != null ) {
            textArea0.removeLeftOfScrollBar( diffOverview0 );
        }
        if ( textArea1 != null && diffOverview1 != null ) {
            textArea1.removeLeftOfScrollBar( diffOverview1 );
        }
    }

    // removes this DualDiff and reinstalls it
    protected void refresh() {
        // remove
        EditBus.removeFromBus( this );
        removeHandlers();
        removeHighlighters();
        removeOverviews();

        // install
        installOverviews();
        installHighlighters();
        installHandlers();

        // reset overviews
        diffLineOverview.clear();
        DiffTextAreaModel taModel = new DiffTextAreaModel( this );
        diffOverview0.setModel( taModel );
        diffOverview0.synchroScrollRight();
        diffOverview1.setModel( taModel );
        diffOverview1.repaint();

        EditBus.addToBus( this );

        // make sure View divider is in the middle
        SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        JSplitPane sp = view.getSplitPane();
                        if ( sp != null ) {
                            sp.setDividerLocation( 0.5 );
                        }
                        view.invalidate();
                        view.validate();
                    }
                }
                                  );
    }

    // removes this DualDiff from our View
    protected void remove() {
        EditBus.removeFromBus( this );
        removeOverviews();
        removeHighlighters();
        removeHandlers();
        getDiffLineOverview().setModel( null );
        DualDiffManager.removeFrom( view );
    }

    private void installHighlighters() {
        DiffHighlight diffHighlight0 = ( DiffHighlight ) DiffHighlight.getHighlightFor( editPane0 );
        if ( diffHighlight0 == null ) {
            diffHighlight0 = ( DiffHighlight ) DiffHighlight.addHighlightTo( editPane0, edits, DiffHighlight.LEFT );
            textArea0.getPainter().addExtension( TextAreaPainter.BELOW_SELECTION_LAYER, diffHighlight0 );
        }
        else {
            diffHighlight0.setEdits( edits );
            diffHighlight0.setPosition( DiffHighlight.LEFT );
        }
        diffHighlight0.setEnabled( true );
        diffHighlight0.updateTextArea();

        DiffHighlight diffHighlight1 = ( DiffHighlight ) DiffHighlight.getHighlightFor( editPane1 );
        if ( diffHighlight1 == null ) {
            diffHighlight1 = ( DiffHighlight ) DiffHighlight.addHighlightTo( editPane1, edits, DiffHighlight.RIGHT );
            textArea1.getPainter().addExtension( TextAreaPainter.BELOW_SELECTION_LAYER, diffHighlight1 );
        }
        else {
            diffHighlight1.setEdits( edits );
            diffHighlight1.setPosition( DiffHighlight.RIGHT );
        }
        diffHighlight1.setEnabled( true );
        diffHighlight1.updateTextArea();
    }

    protected void removeHighlighters() {
        DiffHighlight diffHighlight0 = ( DiffHighlight ) DiffHighlight.getHighlightFor( editPane0 );
        if ( diffHighlight0 != null ) {
            diffHighlight0.setEnabled( false );
            diffHighlight0.updateTextArea();
            DiffHighlight.removeHighlightFrom( editPane0 );
        }

        DiffHighlight diffHighlight1 = ( DiffHighlight ) DiffHighlight.getHighlightFor( editPane1 );
        if ( diffHighlight1 != null ) {
            diffHighlight1.setEnabled( false );
            diffHighlight1.updateTextArea();
            DiffHighlight.removeHighlightFrom( editPane1 );
        }
    }

    protected void installHandlers() {
        textArea0.addScrollListener( scrollHandler );
        textArea0.addFocusListener( scrollHandler );

        textArea1.addScrollListener( scrollHandler );
        textArea1.addFocusListener( scrollHandler );
    }

    protected void removeHandlers() {
        textArea0.removeScrollListener( scrollHandler );
        textArea0.removeFocusListener( scrollHandler );

        textArea1.removeScrollListener( scrollHandler );
        textArea1.removeFocusListener( scrollHandler );
    }

    public Font getFont() {
        return textArea0.getPainter().getFont();
    }

    public Color getBackground() {
        return textArea0.getPainter().getBackground();
    }

    protected void nextDiff0() {
        Diff.Change hunk = edits;
        int caretLine = textArea0.getCaretLine();
        for ( ; hunk != null; hunk = hunk.next ) {
            if ( hunk.first0 > caretLine + ( ( hunk.lines0 == 0 ) ? 1 : 0 ) ) {
                int line = hunk.first0;

                // move the caret to the start of the first line of the diff
                int caret_position = textArea0.getLineStartOffset( line );
                textArea0.setCaretPosition( caret_position, false );

                // scroll so line is visible
                int visibleLines = textArea0.getVisibleLines();
                int leftLineCount = textArea0.getLineCount();
                int distance = 1;
                if ( line > leftLineCount - visibleLines ) {
                    textArea0.setFirstLine( leftLineCount - visibleLines );
                    distance = line - ( leftLineCount - visibleLines );
                }
                else {
                    textArea0.setFirstLine( line - 1 );
                }

                // move caret in other text area to start of diff hunk
                // and scroll to it
                caret_position = textArea1.getLineStartOffset( hunk.first1 );
                textArea1.setCaretPosition( caret_position, false );
                textArea1.setFirstLine( hunk.first1 - distance );

                // maybe move the caret to the first actual diff character
                if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                    DualDiffUtil.centerOnDiff( textArea0, textArea1 );

                    // maybe select the first diff word
                    if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                        textArea0.selectWord();
                        textArea1.selectWord();
                    }
                }

                if ( textArea0.getFirstLine() != line &&
                        jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                    textArea0.getToolkit().beep();
                }
                return ;
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            textArea1.getToolkit().beep();
        }
    }

    protected void nextDiff1() {
        Diff.Change hunk = edits;
        int caretLine = textArea1.getCaretLine();
        for ( ; hunk != null; hunk = hunk.next ) {
            if ( hunk.first1 > caretLine + ( ( hunk.lines1 == 0 ) ? 1 : 0 ) ) {
                int line = hunk.first1;

                // move the caret to the start of the first line of the diff
                int caret_position = textArea1.getLineStartOffset( line );
                textArea1.setCaretPosition( caret_position, false );

                // scroll so line is visible
                int visibleLines = textArea1.getVisibleLines();
                int rightLineCount = textArea1.getLineCount();
                int distance = 1;
                if ( line > rightLineCount - visibleLines ) {
                    textArea1.setFirstLine( rightLineCount - visibleLines );
                    distance = line - ( rightLineCount - visibleLines );
                }
                else {
                    textArea1.setFirstLine( line - 1 );
                }

                // move caret in other text area to start of diff hunk
                // and scroll to it
                caret_position = textArea0.getLineStartOffset( hunk.first0 );
                textArea0.setCaretPosition( caret_position, false );
                textArea0.setFirstLine( hunk.first0 - distance );

                // maybe move the caret to the first actual diff character
                if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                    DualDiffUtil.centerOnDiff( textArea0, textArea1 );

                    // maybe select the first diff word
                    if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                        textArea0.selectWord();
                        textArea1.selectWord();
                    }
                }

                if ( textArea1.getFirstLine() != line &&
                        jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                    textArea1.getToolkit().beep();
                }
                return ;
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            textArea1.getToolkit().beep();
        }
    }

    protected void prevDiff0() {
        Diff.Change hunk = edits;
        int caretLine = textArea0.getCaretLine();
        for ( ; hunk != null; hunk = hunk.next ) {
            if ( hunk.first0 < caretLine ) {
                // hunk starts before the caret line.  If caret line is in hunk,
                // go to start of current hunk.  If caret line is after end of
                // current hunk, but before the next hunk, go to start of current
                // hunk.
                if ( hunk.first0 + hunk.lines0 > caretLine ||               // NOPMD caret is in current hunk
                        hunk.next == null ||                                // caret is after last hunk
                        hunk.next.first0 >= caretLine ) {         // caret is before next hunk
                    int line = hunk.first0;      // first line of diff hunk

                    // move caret to start of diff hunk
                    int caret_position = textArea0.getLineStartOffset( line );
                    textArea0.setCaretPosition( caret_position, false );

                    // scroll so line is visible
                    int visibleLines = textArea0.getVisibleLines();
                    int leftLineCount = textArea0.getLineCount();
                    int distance = 1;
                    if ( line > leftLineCount - visibleLines ) {
                        textArea0.setFirstLine( leftLineCount - visibleLines );
                        distance = line - ( leftLineCount - visibleLines );
                    }
                    else {
                        textArea0.setFirstLine( line - 1 );
                    }

                    // move caret in other text area to start of diff hunk
                    // and scroll to it
                    caret_position = textArea1.getLineStartOffset( hunk.first1 );
                    textArea1.setCaretPosition( caret_position, false );
                    textArea1.setFirstLine( hunk.first1 - distance );

                    // maybe move the caret to the first actual diff character
                    if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                        DualDiffUtil.centerOnDiff( textArea0, textArea1 );

                        // maybe select the first diff word
                        if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                            textArea0.selectWord();
                            textArea1.selectWord();
                        }
                    }

                    if ( textArea0.getFirstLine() != line &&
                            jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                        textArea0.getToolkit().beep();
                    }
                    return ;
                }
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            textArea0.getToolkit().beep();
        }
    }

    protected void prevDiff1() {
        Diff.Change hunk = edits;
        int caretLine = textArea1.getCaretLine();
        for ( ; hunk != null; hunk = hunk.next ) {
            if ( hunk.first1 < caretLine ) {
                // hunk starts before the caret line.  If caret line is in hunk,
                // go to start of current hunk.  If caret line is after end of
                // current hunk, but before current hunk, go to start of current
                // hunk.
                if ( hunk.first1 + hunk.lines1 > caretLine ||              // NOPMD caret is in current hunk
                        hunk.next == null ||                                // caret is after last hunk
                        hunk.next.first1 >= caretLine ) {         // caret is before next hunk
                    int line = hunk.first1;      // first line of hunk

                    // move caret to start of diff hunk
                    int caret_position = textArea1.getLineStartOffset( line );
                    textArea1.setCaretPosition( caret_position, false );

                    // scroll so line is visible
                    int visibleLines = textArea1.getVisibleLines();
                    int rightLineCount = textArea1.getLineCount();
                    int distance = 1;
                    if ( line > rightLineCount - visibleLines ) {
                        textArea1.setFirstLine( rightLineCount - visibleLines );
                        distance = line - ( rightLineCount - visibleLines );
                    }
                    else {
                        textArea1.setFirstLine( line - 1 );
                    }

                    // move caret in other text area to start of diff hunk
                    // and scroll to it
                    caret_position = textArea0.getLineStartOffset( hunk.first0 );
                    textArea0.setCaretPosition( caret_position, false );
                    textArea0.setFirstLine( hunk.first0 - distance );

                    // maybe move the caret to the first actual diff character
                    if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                        DualDiffUtil.centerOnDiff( textArea0, textArea1 );

                        // maybe select the first diff word
                        if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                            textArea0.selectWord();
                            textArea1.selectWord();
                        }
                    }

                    if ( textArea1.getFirstLine() != line &&
                            jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                        textArea1.getToolkit().beep();
                    }
                    return ;
                }
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            textArea1.getToolkit().beep();
        }
    }

    protected void moveRight() {
        diffOverview0.moveRight( textArea0.getCaretLine() );
    }

    protected void moveLeft() {
        diffOverview1.moveLeft( textArea1.getCaretLine() );
    }
}