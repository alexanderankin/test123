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

    private DiffLineOverview diffLineOverview;

    private final ScrollHandler scrollHandler;

    protected DualDiff( View view ) {
        this( view, DualDiffUtil.ignoreCaseDefault, DualDiffUtil.trimWhitespaceDefault,
              DualDiffUtil.ignoreAmountOfWhitespaceDefault, DualDiffUtil.ignoreAllWhitespaceDefault );
    }

    protected DualDiff( View view, boolean ignoreCase, boolean trimWhitespace,
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
        scrollHandler = new ScrollHandler( this );

        this.initOverviews();
        this.addOverviews();
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
            Buffer b0 = ( Buffer ) this.textArea0.getBuffer();
            Buffer b1 = ( Buffer ) this.textArea1.getBuffer();
            if ( bu.getBuffer() != b0 && bu.getBuffer() != b1 ) {
                // Not concerned by this message
                return ;
            }
            if ( bu.getWhat() == BufferUpdate.LOADED || bu.getWhat() == BufferUpdate.SAVED || bu.getWhat() == BufferUpdate.DIRTY_CHANGED ) {
                this.refresh();
            }
        }
        else if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            EditPane editPane = epu.getEditPane();
            View view = editPane.getView();
            if ( !DualDiffManager.isEnabledFor( view ) ) {
                return ;
            }
            if ( epu.getWhat() == EditPaneUpdate.CREATED ) {
                DualDiffManager.editPaneCreated( view );
            }
            else if ( epu.getWhat() == EditPaneUpdate.DESTROYED ) {
                DualDiffManager.editPaneDestroyed( view, editPane );
            }
            else if ( epu.getWhat() == EditPaneUpdate.BUFFER_CHANGED ) {
                DualDiffManager.editPaneBufferChanged( view );
            }
        }
        else if ( message instanceof PropertiesChanged ) {
            setIgnoreCase( jEdit.getBooleanProperty( "jdiff.ignore-case", false ) );
            setTrimWhitespace( jEdit.getBooleanProperty( "jdiff.trim-whitespace", false ) );
            setIgnoreAmountOfWhitespace( jEdit.getBooleanProperty( "jdiff.ignore-amount-whitespace", false ) );
            setIgnoreAllWhitespace( jEdit.getBooleanProperty( "jdiff.ignore-all-whitespace", false ) );
            refresh();
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
    }

    private void addOverviews() {
        textArea0.addLeftOfScrollBar( diffOverview0 );
        textArea1.addLeftOfScrollBar( diffOverview1 );
        setDiffLineOverview( diffLineOverview );
    }

    // remove overviews and merge controls
    protected void removeOverviews() {
        textArea0.removeLeftOfScrollBar( this.diffOverview0 );
        textArea1.removeLeftOfScrollBar( this.diffOverview1 );
    }

    protected void refresh() {
        removeHandlers();
        disableHighlighters();

        removeOverviews();
        initOverviews();
        addOverviews();

        enableHighlighters();
        addHandlers();

        diffLineOverview.clear();
        DiffTextAreaModel taModel = new DiffTextAreaModel( this );
        diffOverview0.setModel( taModel );
        diffOverview0.synchroScrollRight();
        diffOverview1.setModel( taModel );
        diffOverview1.repaint();
    }

    protected void enableHighlighters() {
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

    protected void disableHighlighters() {
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

    protected void addHandlers() {
        this.textArea0.addScrollListener( this.scrollHandler );
        this.textArea0.addFocusListener( this.scrollHandler );

        this.textArea1.addScrollListener( this.scrollHandler );
        this.textArea1.addFocusListener( this.scrollHandler );
    }

    protected void removeHandlers() {
        this.textArea0.removeScrollListener( this.scrollHandler );
        this.textArea0.removeFocusListener( this.scrollHandler );

        this.textArea1.removeScrollListener( this.scrollHandler );
        this.textArea1.removeFocusListener( this.scrollHandler );
    }

    public Font getFont() {
        return textArea0.getPainter().getFont();
    }

    public Color getBackground() {
        return textArea0.getPainter().getBackground();
    }

    protected void nextDiff0() {
        Diff.Change hunk = this.edits;
        int caretLine = this.textArea0.getCaretLine();
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
                this.textArea1.setCaretPosition( caret_position, false );
                this.textArea1.setFirstLine( hunk.first1 - distance );

                // maybe move the caret to the first actual diff character
                if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                    DualDiffUtil.centerOnDiff( this.textArea0, this.textArea1 );

                    // maybe select the first diff word
                    if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                        this.textArea0.selectWord();
                        this.textArea1.selectWord();
                    }
                }

                if ( this.textArea0.getFirstLine() != line &&
                        jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                    this.textArea0.getToolkit().beep();
                }
                return ;
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            this.textArea1.getToolkit().beep();
        }
    }

    protected void nextDiff1() {
        Diff.Change hunk = this.edits;
        int caretLine = this.textArea1.getCaretLine();
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
                this.textArea0.setCaretPosition( caret_position, false );
                this.textArea0.setFirstLine( hunk.first0 - distance );

                // maybe move the caret to the first actual diff character
                if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                    DualDiffUtil.centerOnDiff( this.textArea0, this.textArea1 );

                    // maybe select the first diff word
                    if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                        this.textArea0.selectWord();
                        this.textArea1.selectWord();
                    }
                }

                if ( this.textArea1.getFirstLine() != line &&
                        jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                    this.textArea1.getToolkit().beep();
                }
                return ;
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            this.textArea1.getToolkit().beep();
        }
    }

    protected void prevDiff0() {
        Diff.Change hunk = this.edits;
        int caretLine = this.textArea0.getCaretLine();
        for ( ; hunk != null; hunk = hunk.next ) {
            if ( hunk.first0 < caretLine ) {
                // hunk starts before the caret line.  If caret line is in hunk,
                // go to start of current hunk.  If caret line is after end of
                // current hunk, but before the next hunk, go to start of current
                // hunk.
                if ( hunk.first0 + hunk.lines0 > caretLine ||       // NOPMD caret is in current hunk
                        hunk.next == null ||                        // caret is after last hunk
                        hunk.next.first0 >= caretLine ) {         // caret is before next hunk
                    int line = hunk.first0;      // first line of diff hunk

                    // move caret to start of diff hunk
                    int caret_position = textArea0.getLineStartOffset( line );
                    this.textArea0.setCaretPosition( caret_position, false );

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
                    this.textArea1.setCaretPosition( caret_position, false );
                    this.textArea1.setFirstLine( hunk.first1 - distance );

                    // maybe move the caret to the first actual diff character
                    if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                        DualDiffUtil.centerOnDiff( textArea0, textArea1 );

                        // maybe select the first diff word
                        if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                            textArea0.selectWord();
                            textArea1.selectWord();
                        }
                    }

                    if ( this.textArea0.getFirstLine() != line &&
                            jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                        this.textArea0.getToolkit().beep();
                    }
                    return ;
                }
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            this.textArea0.getToolkit().beep();
        }
    }

    protected void prevDiff1() {
        Diff.Change hunk = this.edits;
        int caretLine = this.textArea1.getCaretLine();
        for ( ; hunk != null; hunk = hunk.next ) {
            if ( hunk.first1 < caretLine ) {
                // hunk starts before the caret line.  If caret line is in hunk,
                // go to start of current hunk.  If caret line is after end of
                // current hunk, but before current hunk, go to start of current
                // hunk.
                if ( hunk.first1 + hunk.lines1 > caretLine ||      // NOPMD caret is in current hunk
                        hunk.next == null ||                        // caret is after last hunk
                        hunk.next.first1 >= caretLine ) {         // caret is before next hunk
                    int line = hunk.first1;      // first line of hunk

                    // move caret to start of diff hunk
                    int caret_position = textArea1.getLineStartOffset( line );
                    this.textArea1.setCaretPosition( caret_position, false );

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
                    this.textArea0.setCaretPosition( caret_position, false );
                    this.textArea0.setFirstLine( hunk.first0 - distance );

                    // maybe move the caret to the first actual diff character
                    if ( jEdit.getBooleanProperty( DualDiffManager.HORIZ_SCROLL ) ) {
                        DualDiffUtil.centerOnDiff( textArea0, textArea1 );

                        // maybe select the first diff word
                        if ( jEdit.getBooleanProperty( DualDiffManager.SELECT_WORD ) ) {
                            textArea0.selectWord();
                            textArea1.selectWord();
                        }
                    }

                    if ( this.textArea1.getFirstLine() != line &&
                            jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
                        this.textArea1.getToolkit().beep();
                    }
                    return ;
                }
            }
        }

        if ( jEdit.getBooleanProperty( DualDiffManager.BEEP_ON_ERROR ) ) {
            this.textArea1.getToolkit().beep();
        }
    }
}