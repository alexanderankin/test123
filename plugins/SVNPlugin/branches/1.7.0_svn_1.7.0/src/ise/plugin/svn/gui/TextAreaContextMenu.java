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
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import ise.plugin.svn.PVHelper;
import ise.plugin.svn.action.*;
import ise.plugin.svn.data.*;
import ise.plugin.svn.library.GUIUtils;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Subversion context menu to add to the jEdit text area context menu.
 */
public class TextAreaContextMenu extends JMenu {

    private View view = null;

    public TextAreaContextMenu( View view ) {
        super( jEdit.getProperty( "ips.Subversion", "Subversion" ) );
        this.view = view;

        /* these items act on working copies. This is the order in the PV
        context menu:

        status
        update
        commit
        -
        revert
        add
        resolve
        delete
        ignore
        -
        info
        log
        properties
        diff
        merge
        time lapse
        -
        checkout
        switch
        cleanup
        -
        copy
        move
        import
        export
        -
        lock
        unlock
        */

        JMenuItem item = new JMenuItem( jEdit.getProperty( "ips.Status", "Status" ) );
        item.addActionListener( getStatusActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Update...", "Update..." ) );
        item.addActionListener( getUpdateActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Commit...", "Commit..." ) );
        item.addActionListener( getCommitActionListener() );
        add( item );
        addSeparator();
        item = new JMenuItem( jEdit.getProperty( "ips.Revert...", "Revert..." ) );
        item.addActionListener( getRevertActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Add...", "Add..." ) );
        item.addActionListener( getAddActionListener() );
        add( item );
        /* resolve/resolved doesn't work with svnkit 1.7 alpha
        item = new JMenuItem( jEdit.getProperty( "ips.Resolve_Conflicts...", "Resolve Conflicts..." ) );
        item.addActionListener( getResolveConflictsActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Mark_File_Resolved...", "Mark File Resolved..." ) );
        item.addActionListener( getResolvedActionListener() );
        */
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Delete...", "Delete..." ) );
        item.addActionListener( getDeleteActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Ignore...", "Ignore..." ) );
        item.addActionListener( getIgnoreActionListener() );
        add( item );
        addSeparator();
        item = new JMenuItem( jEdit.getProperty( "ips.Info...", "Info..." ) );
        item.addActionListener( getInfoActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Log...", "Log..." ) );
        item.addActionListener( getLogActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Blame", "Blame" ) );
        item.addActionListener( getBlameActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Properties...", "Properties..." ) );
        item.addActionListener( getPropertyActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Diff...", "Diff..." ) );
        item.addActionListener( getDiffActionListener() );
        add( item );
        /* TODO: merge doesn't work with svnkit 1.7 alpha
        item = new JMenuItem( jEdit.getProperty( "ips.Merge", "Merge" ) + "..." );
        item.addActionListener( getMergeActionListener() );
        add( item );
        */
        /// TODO: uncomment these when time lapse works
        //item = new JMenuItem( jEdit.getProperty("ips.TimeLapse...", "Time Lapse") );
        //item.addActionListener( getTimeLapseActionListener() );
        //add( item );
        addSeparator();
        item = new JMenuItem( jEdit.getProperty( "ips.Checkout", "Checkout" ) );
        item.addActionListener( getCheckoutActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Switch", "Switch" ) );
        item.addActionListener( getSwitchActionListener() );
        add( item );
        /* TODO: cleanup doesn't work with svnkit 1.7 alpha
        item = new JMenuItem( jEdit.getProperty( "ips.Cleanup...", "Cleanup..." ) );
        item.addActionListener( getCleanupActionListener() );
        add( item );
        */
        addSeparator();
        item = new JMenuItem( jEdit.getProperty( "ips.Copy", "Copy" ) );
        item.addActionListener( getCopyActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Move", "Move" ) );
        item.addActionListener( getMoveActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Import", "Import" ) );
        item.addActionListener( getImportActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Export", "Export" ) );
        item.addActionListener( getExportActionListener() );
        add( item );
        addSeparator();
        item = new JMenuItem( jEdit.getProperty( "ips.Lock", "Lock" ) );
        item.addActionListener( getLockActionListener() );
        add( item );
        item = new JMenuItem( jEdit.getProperty( "ips.Unlock", "Unlock" ) );
        item.addActionListener( getUnlockActionListener() );
        add( item );

    }

    public String toString() {
        return jEdit.getProperty( "ips.Subversion", "Subversion" );
    }

    // get a list containing a single path representing the file in the current
    // buffer.
    private List<String> getPaths() {
        List<String> paths = new ArrayList<String>();
        String path = view.getBuffer().getPath();
        paths.add( path );
        return paths;
    }

    private ActionListener getStatusActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       StatusAction action = new StatusAction( view, getPaths(), null, null );
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

    private ActionListener getResolveConflictsActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       ResolveConflictDialog dialog = new ResolveConflictDialog( view, view.getBuffer().getPath() );
                       dialog.setVisible( true );
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

    private ActionListener getDeleteActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       DeleteAction action = new DeleteAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getIgnoreActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       IgnoreAction action = new IgnoreAction( view, view.getBuffer().getPath() );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getInfoActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       InfoAction action = new InfoAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getLogActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       LogAction action = new LogAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getBlameActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       BlameAction action = new BlameAction( view, getPaths(), null, null );
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

    private ActionListener getDiffActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       DiffAction action = new DiffAction( view, getPaths().get( 0 ), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getMergeActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       MergeDialog dialog = new MergeDialog( view, getPaths().get( 0 ) );
                       GUIUtils.center( view, dialog );
                       dialog.setVisible( true );
                       MergeData data = dialog.getData();
                       if ( data == null ) {
                           return ;     // user canceled
                       }
                       MergeAction action = new MergeAction( view, data );
                       action.actionPerformed( ae );
                   }
               };
    }

    /// TODO: uncomment when time lapse works
    /*
    private ActionListener getTimeLapseActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       TimeLapseAction action = new TimeLapseAction( view, getPaths().get( 0 ), null, null );
                       action.actionPerformed( ae );
                   }
               };
}
    */

    private ActionListener getCheckoutActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       CheckoutData data = new CheckoutData( null, null, null );
                       CheckoutAction action = new CheckoutAction( view, data );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getSwitchActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       UpdateData data = new UpdateData();
                       data.setPaths( getPaths() );
                       SwitchAction action = new SwitchAction( view, data );
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

    private ActionListener getCopyActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       // show the copy dialog
                       List<File> files = new ArrayList<File>();
                       files.add( new File( getPaths().get( 0 ) ) );
                       String default_destination = files.size() == 1 ? files.get( 0 ).getAbsolutePath() : PVHelper.getProjectRoot( view );
                       CopyDialog dialog = new CopyDialog( view, files, default_destination, null );   // TODO: fill in remote destination
                       GUIUtils.center( view, dialog );
                       dialog.setVisible( true );
                       CopyData data = dialog.getData();
                       if ( data == null ) {
                           return ;     // user canceled
                       }

                       // do the copy
                       CopyAction action = new CopyAction( view, data );
                       action.actionPerformed( null );
                   }
               };
    }

    private ActionListener getMoveActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       // show the copy dialog
                       List<File> files = new ArrayList<File>();
                       files.add( new File( getPaths().get( 0 ) ) );
                       String default_destination = files.size() == 1 ? files.get( 0 ).getAbsolutePath() : PVHelper.getProjectRoot( view );
                       MoveDialog dialog = new MoveDialog( view, files, default_destination );
                       GUIUtils.center( view, dialog );
                       dialog.setVisible( true );
                       CopyData data = dialog.getData();
                       if ( data == null ) {
                           return ;     // user canceled
                       }

                       MoveAction action = new MoveAction( view, data );
                       action.actionPerformed( null );
                   }
               };
    }

    private ActionListener getImportActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       ImportAction action = new ImportAction( view );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getExportActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       List<File> file = new ArrayList<File>();
                       file.add( new File( getPaths().get( 0 ) ) );
                       ExportAction action = new ExportAction( view, file, null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getLockActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       LockAction action = new LockAction( view, getPaths(), null, null, false );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getUnlockActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       UnlockAction action = new UnlockAction( view, getPaths(), null, null, false );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getUpdateActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       UpdateAction action = new UpdateAction( view, getPaths(), null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

    private ActionListener getCommitActionListener() {
        return new ActionListener() {
                   public void actionPerformed( ActionEvent ae ) {
                       TreeMap<String, String> paths = new TreeMap<String, String>();
                       for ( String path : getPaths() ) {
                           paths.put( path, "" );
                       }
                       CommitAction action = new CommitAction( view, paths, null, null );
                       action.actionPerformed( ae );
                   }
               };
    }

}