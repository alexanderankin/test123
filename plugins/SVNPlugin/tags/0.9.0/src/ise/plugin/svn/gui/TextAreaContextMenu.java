/*
Copyright (c) 2007, Dale Anson
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

package ise.plugin.svn.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import org.gjt.sp.jedit.View;
import ise.plugin.svn.action.*;
import ise.plugin.svn.data.PropertyData;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Subversion context menu to add to the jEdit text area context menu.
 */
public class TextAreaContextMenu extends JMenu {

    private View view = null;

    public TextAreaContextMenu( View view ) {
        super( "Subversion" );
        this.view = view;

        // these items act on working copies.  TODO: I'd like to add update and commit
        // to this menu, but then I'd need to figure out how to find the repository
        // info for the file in the current buffer.  Maybe call info first?
        JMenuItem item = new JMenuItem( "Add" );
        item.addActionListener( getAddActionListener() );
        add( item );
        item = new JMenuItem( "Revert" );
        item.addActionListener( getRevertActionListener() );
        add( item );
        item = new JMenuItem( "Resolve" );
        item.addActionListener( getResolvedActionListener() );
        add( item );
        addSeparator();
        item = new JMenuItem( "Info" );
        item.addActionListener( getInfoActionListener() );
        add( item );
        item = new JMenuItem( "Properties" );
        item.addActionListener( getPropertyActionListener() );
        add( item );
        addSeparator();
        item = new JMenuItem( "Cleanup" );
        item.addActionListener( getCleanupActionListener() );
        add( item );
        item = new JMenuItem( "Delete" );
        item.addActionListener( getDeleteActionListener() );
        add( item );
    }

    // get a list containing a single path representing the file in the current
    // buffer.
    private List<String> getPaths() {
        List<String> paths = new ArrayList<String>();
        String path = view.getBuffer().getPath();
        paths.add( path );
        return paths;
    }

    private ActionListener getInfoActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       InfoAction action = new InfoAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getRevertActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       RevertAction action = new RevertAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getAddActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       AddAction action = new AddAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getPropertyActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       PropertyData data = new PropertyData();
                       data.setPaths( getPaths() );
                       data.setPathsAreURLs( false );
                       data.setHasDirectory( false );
                       data.setRemote( false );
                       data.setRevision( SVNRevision.WORKING );
                       PropertyAction action = new PropertyAction( view, data );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getCleanupActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       CleanupAction action = new CleanupAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getDeleteActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       DeleteAction action = new DeleteAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getResolvedActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       ResolvedAction action = new ResolvedAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

}
