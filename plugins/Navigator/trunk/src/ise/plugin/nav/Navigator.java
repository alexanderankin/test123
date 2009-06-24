/*
Copyright (c) 2002, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package ise.plugin.nav;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PositionChanging;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

/**
 * NavigatorPlugin for keeping track of where we were.
 *
 * @author Dale Anson
 * @author Alan Ezust
 *
 * @version $Id$
 */
public class Navigator implements ActionListener {

    /** Action command to go to the previous item. */
    public final static String BACK = "back";

    /** Action command to go to the next item. */
    public final static String FORWARD = "forward";

    /** Action command to indicate that it is okay to go back. */
    public final static String CAN_GO_BACK = "canGoBack";

    /** Action command to indicate that it is not okay to go back. */
    public final static String CANNOT_GO_BACK = "cannotGoBack";

    /** Action command to indicate that it is okay to go forward. */
    public final static String CAN_GO_FORWARD = "canGoForward";

    /** Action command to indicate that it is not okay to go forward. */
    public final static String CANNOT_GO_FORWARD = "cannotGoForward";

    // History is stored in 3 separate containers.  backHistory contains those
    // positions reachable by the back button, forwardHistory contains those
    // positions reachable by the forward button, and current contains the
    // current position.  As the user moves back, the current position is pushed
    // onto the forward stack, current becomes the position popped off of the
    // back stack, and vice versa for when the user is moving forward through
    // the history.  The actual Stack implementation is a NavStack, which
    // respects the maxStackSize setting.
    private NavStack<NavPosition> backHistory;
    private NavPosition current = null;
    private NavStack<NavPosition> forwardHistory;

    // max size for history
    private int maxStackSize = jEdit.getIntegerProperty( "navigator.maxStackSize", 512 );

    // forward and back button models
    private DefaultButtonModel backButtonModel;
    private DefaultButtonModel forwardButtonModel;

    // the view we're following
    private View view;

    // the edit pane we're following, This will be null if following View, so
    // this can be used as a simple check for the scope.
    private EditPane _editPane;

    // flag -- set to true to not addToHistory the nav position while switching to
    // another view or edit pane
    private boolean ignoreUpdates;

    /**
     * Constructor for Navigator
     *
     * @param vw
     * @param position
     */
    public Navigator( View view ) {
        this.view = view;
        init();

        // add a mouse listener to each text area in the view. Each mouse click
        // on a text area is stored
        for ( EditPane editPane : Navigator.this.view.getEditPanes() ) {
            addMouseListenerTo( editPane );
        }
    }

    public Navigator( EditPane editPane ) {
        this._editPane = editPane;
        this.view = editPane.getView();
        init();

        // add a mouse listener to the EditPane. Each mouse click
        // on a text area is stored
        addMouseListenerTo( Navigator.this.getEditPane() );
    }

    // initialize this Navigator
    private void init() {
        ignoreUpdates = false;

        // set up button models
        backButtonModel = new DefaultButtonModel();
        backButtonModel.setActionCommand( Navigator.BACK );
        backButtonModel.addActionListener( this );

        forwardButtonModel = new DefaultButtonModel();
        forwardButtonModel.setActionCommand( Navigator.FORWARD );
        forwardButtonModel.addActionListener( this );

        // set up the history stacks
        backHistory = new NavStack<NavPosition>( maxStackSize );
        forwardHistory = new NavStack<NavPosition>( maxStackSize );
        clearHistory();
        current = currentPosition();
    }

    /**
     * Adds a mouse listener to the text area of the given EditPane.  Each mouse
     * click in the text area is recorded in the Navigator history.
     */
    public void addMouseListenerTo( EditPane editPane ) {
        if ( editPane == null ) {
            return ;
        }
        TextAreaPainter painter = editPane.getTextArea().getPainter();
        MouseListener listeners[] = painter.getMouseListeners();
        painter.addMouseListener( new NavMouseListener( this ) );
    }

    public EditPane getEditPane() {
        return _editPane;
    }

    public void setEditPane( EditPane editPane ) {
        _editPane = editPane;
    }

    /**
     * @return model for the back button
     */
    public ButtonModel getBackModel() {
        return backButtonModel;
    }

    /**
     * @return model for the forward button
     */
    public ButtonModel getForwardModel() {
        return forwardButtonModel;
    }

    /**
     * Calculate the current position and create a NavPosition.
     */
    private NavPosition currentPosition() {
        EditPane editPane = getEditPane();
        JEditTextArea textarea = null;
        if ( editPane != null ) {
            // edit pane scope
            textarea = editPane.getTextArea();
        }
        else {
            // view scope
            editPane = view.getEditPane();
            if ( editPane != null ) {
                // editPane could be null on Navigator startup
                textarea = editPane.getTextArea();
            }
        }

        if ( textarea == null ) {
            // textarea could be null on startup -- Navigator will be loaded by
            // jEdit before there is a View or EditPane
            return null;
        }

        Buffer buffer = editPane.getBuffer();
        if ( ( buffer.getLength() == 0 ) && buffer.getName().startsWith( "Untitled" ) ) {
            // skip empty untitled buffers
            return null;
        }

        int caretPosition = textarea.getCaretPosition();
        String linetext = textarea.getLineText( textarea.getCaretLine() );
        return new NavPosition( editPane, buffer, caretPosition, linetext );
    }

    /**
     * Updates to current position unless ignoreUpdates is set.
     */
    public void addToHistory() {
        if ( ignoreUpdates ) {
            return ;
        }
        addToHistory( currentPosition() );
    }

    /**
     * Updates the stacks and button state based on the given node. Pushes
     * the node on to the "back" history, clears the "forward" history.
     *
     * @param node
     *                an instance of NavPosition.
     */
    private void addToHistory( NavPosition node ) {
        if ( node == null ) {
            return ;
        }
        // don't add same node twice in a row
        if ( backHistory.empty() || !current.equals( node ) ) {
            backHistory.push( current );
            current = node;
            forwardHistory.clear();
            setButtonState();
        }
    }

    /**
     * The action handler for this class. Actions can be invoked by calling
     * this method and passing an ActionEvent with one of the action
     * commands defined in this class (BACK, FORWARD, etc).
     *
     * @param ae
     *                the action event to kick a response.
     */
    public void actionPerformed( ActionEvent ae ) {
        if ( ae.getActionCommand().equals( BACK ) ) {
            goBack();
        }
        else if ( ae.getActionCommand().equals( FORWARD ) ) {
            goForward();
        }
        else if ( ae.getActionCommand().equals( CAN_GO_BACK ) ) {
            backButtonModel.setEnabled( true );
        }
        else if ( ae.getActionCommand().equals( CANNOT_GO_BACK ) ) {
            backButtonModel.setEnabled( false );
        }
        else if ( ae.getActionCommand().equals( CAN_GO_FORWARD ) ) {
            forwardButtonModel.setEnabled( true );
        }
        else if ( ae.getActionCommand().equals( CANNOT_GO_FORWARD ) ) {
            forwardButtonModel.setEnabled( false );
        }
    }

    /**
     * Removes an invalid node from the navigation history.
     *
     * @param node
     *                an invalid node
     */
    public void remove( Object node ) {
        backHistory.remove( node );
        forwardHistory.remove( node );
    }

    /**
     * Set the maximum size of the back and forward history stacks.  Both
     * stacks will be the same size.  The default is 512, which means a total of
     * 1025 positions can be remembered (512 in back history, 512 in forward
     * history, and 1 in current).
     * @param size the new size, must be greater than 0.
     */
    public void setMaxHistorySize( int size ) {
        if ( size > 0 ) {
            backHistory.setMaxSize( size );
            forwardHistory.setMaxSize( size );
            maxStackSize = size;
        }
    }

    /**
     * @return the maximum size of the back and forward history stacks.
     * Default is 512 entries.
     */
    public int getMaxHistorySize() {
        return maxStackSize;
    }

    /**
     * Clears both the back and forward histories.
     */
    public void clearHistory() {
        backHistory.clear();
        current = null;
        forwardHistory.clear();
        setButtonState();
    }

    /**
     * Sets the state of the navigation buttons.
     */
    private void setButtonState() {
        backButtonModel.setEnabled( backHistory.size() > 0 );
        forwardButtonModel.setEnabled( forwardHistory.size() > 0 );
    }

    /**
     * For View scope.  If scope is EditPane, then _editPane should not be
     * null, and that EditPane is returned.
     * @return the EditPane containing the buffer from the position in the
     * given View.  If the EditPane is not found (e.g. it was closed), searches
     * through all EditPanes in the view for the buffer, returning the EditPane
     * containing the buffer if one is found.  If not, returns the current
     * EditPane for the View.
     */
    private EditPane findEditPane( NavPosition position ) {
        if ( NavigatorPlugin.getScope() == NavigatorPlugin.EDITPANE_SCOPE ) {
            if ( getEditPane() == null ) {
                setEditPane( view.getEditPane() );
            }
            return getEditPane();
        }

        for ( EditPane editPane : view.getEditPanes() ) {
            if ( editPane.hashCode() == position.editPane ) {
                // this is the preferred edit pane
                return editPane;
            }
        }

        // didn't find the preferred edit pane, probably because it was closed,
        // so search all edit panes
        for ( EditPane editPane : view.getEditPanes() ) {
            Buffer[] buffers = editPane.getBufferSet().getAllBuffers();
            for ( Buffer buffer : buffers ) {
                if ( position.path.equals( buffer.getPath() ) ) {
                    // found an edit pane containing the buffer.  Set the
                    // preferred edit pane to the one where we found it.
                    position.editPane = editPane.hashCode();
                    return editPane;
                }
            }
        }

        // didn't find any edit pane that contains the buffer, so just
        // return the current EditPane
        EditPane editPane = view.getEditPane();
        position.editPane = editPane.hashCode();
        return editPane;
    }

    /**
     * Sets the position of the cursor to the given NavPosition.
     *
     * @param o
     *                The new NavPosition value
     */
    public void setPosition( NavPosition position ) {
        if ( position == null ) {
            return ;
        }

        // stop listening to EditBus events while we are changing buffers
        ignoreUpdates = true;

        // see if the buffer is already open in one of the current edit panes
        EditPane editPaneForPosition = findEditPane( position );
        Buffer[] buffers = editPaneForPosition.getBufferSet().getAllBuffers();
        Buffer buffer = null;
        String path = position.path;
        for ( Buffer b : buffers ) {
            if ( path.equals( b.getPath() ) ) {
                buffer = b;
                break;
            }
        }

        if ( buffer == null ) {
            // if here, then the buffer was not open in any edit pane.  Do not
            // create a new edit pane for it, just open the buffer in the EditPane
            // that was found by findEditPane.
            buffer = jEdit.openFile( view, position.path );
            if ( buffer == null ) {
                // maybe the file was deleted, so there is nothing to open
                remove( position );
                ignoreUpdates = false;
                return ;
            }
        }

        // have EditPane and Buffer, display and move the caret
        EditBus.send( new PositionChanging( view.getEditPane() ) );
        editPaneForPosition.setBuffer( buffer );
        int caret = position.caret;
        if ( caret >= buffer.getLength() ) {
            caret = buffer.getLength() - 1;
        }
        if ( caret < 0 ) {
            caret = 0;
        }
        try {
            editPaneForPosition.getTextArea().setCaretPosition( caret, true );
            editPaneForPosition.getTextArea().requestFocus();
        }
        catch ( NullPointerException npe ) {    // NOPMD
            npe.printStackTrace();
            // sometimes Buffer.markTokens throws a NPE here, catch
            // it and silently ignore it.
        }
        ignoreUpdates = false;
    }

    /**
     * Show a popup containing the back history list.
     */
    public void backList() {
        boolean combineLists = jEdit.getBooleanProperty( "navigator.combineLists", false );
        if ( backHistory.size() == 0 && !combineLists ) {
            JOptionPane.showMessageDialog( view, "No backward items", "Info", JOptionPane.INFORMATION_MESSAGE );
            return ;
        }
        if ( combineLists ) {
            NavStack stack = new NavStack( backHistory.size() + forwardHistory.size() + 1 );
            stack.addAll( backHistory );
            stack.add( current );
            stack.addAll( forwardHistory );
            new NavHistoryPopup( view, this, ( Vector ) stack, current );
        }
        else {
            new NavHistoryPopup( view, this, ( Vector ) backHistory.clone() );
        }
    }

    /**
     * Show a popup containing the forward history list.
     */
    public void forwardList() {
        boolean combineLists = jEdit.getBooleanProperty( "navigator.combineLists", false );
        if ( forwardHistory.size() == 0 && !combineLists ) {
            JOptionPane.showMessageDialog( view, "No forward items", "Info", JOptionPane.INFORMATION_MESSAGE );
            return ;
        }
        if ( combineLists ) {
            NavStack stack = new NavStack( backHistory.size() + forwardHistory.size() + 1 );
            stack.addAll( backHistory );
            stack.add( current );
            stack.addAll( forwardHistory );
            new NavHistoryPopup( view, this, ( Vector ) stack, current );
        }
        else {
            new NavHistoryPopup( view, this, ( Vector ) forwardHistory.clone() );
        }
    }

    /**
     * Moves to the previous item in the "back" history.
     */
    public void goBack() {
        if ( backHistory.size() > 0 ) {
            if ( current != null ) {
                forwardHistory.push( current );
            }
            current = backHistory.pop();
            setPosition( current );
            setButtonState();
        }
    }

    /**
     * Moves to the next item in the "forward" history.
     */
    public void goForward() {
        if ( forwardHistory.size() > 0 ) {
            if ( current != null ) {
                backHistory.push( current );
            }
            current = forwardHistory.pop();
            setPosition( current );
            setButtonState();
        }
    }

    /**
     * Jumps to a specific position in the history.
     * @param position the position to jump to.
     */
    public void jump( NavPosition position ) {
        ignoreUpdates = true;
        // find the position.  If it is in the back history, copy all positions
        // after it to the forward history, vice versa if it is in the forward
        // history.
        if ( backHistory.contains( position ) ) {
            forwardHistory.push( current );
            while ( true ) {
                current = backHistory.pop();
                if ( current.equals( position ) ) {
                    break;
                }
                forwardHistory.push( current );
            }
        }
        else if ( forwardHistory.contains( position ) ) {
            backHistory.push( current );
            while ( true ) {
                current = forwardHistory.pop();
                if ( current.equals( position ) ) {
                    break;
                }
                backHistory.push( current );
            }
        }
        else {
            // this shouldn't happen, since the given position should have been
            // picked from either the backList or the forwardList.  If somehow
            // we do get here, then 'position' is a new position, so just insert
            // it into the history at the current history location.
            backHistory.push( current );
            current = position;
        }
        setPosition( current );
        setButtonState();
        ignoreUpdates = false;
    }
}