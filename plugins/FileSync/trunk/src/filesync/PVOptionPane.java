/*
Copyright (c) 2012, Dale Anson
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

package filesync;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;

import ise.java.awt.KappaLayout;

import projectviewer.vpt.VPTProject;

/**
 * Option pane for setting the project files to sync and where to sync them to.
 * DONE: add checkboxes for includes and excludes for case-sensitivity. -- done,
 * but not here. This is handled in the GlobFileFilter instead, where case-sensitivity
 * is based on the operating system.
 * DONE: add checkboxes for excluding cvs, svn, and git folders.
 */
public class PVOptionPane extends AbstractOptionPane {

    private VPTProject project;
    private static final String internalName = "filesync.pv.options";

    private JCheckBox enableSync;
    private JCheckBox cvs;
    private JCheckBox svn;
    private JCheckBox git;
    private JButton addButton;
    private JButton editButton;
    private JButton removeButton;
    private JTree syncTree;

    private Properties props;

    public PVOptionPane( VPTProject project ) {
        super( internalName );
        this.project = project;
        props = FileSyncPlugin.getSyncProperties( project.getName() );
        setLayout( new KappaLayout() );
    }

    /** Initialises the option pane. */
    protected void _init() {
        initComponents();
        initListeners();

    }

    private void initComponents() {
        setBorder( BorderFactory.createEmptyBorder(11, 11, 11, 11 ) );

        enableSync = new JCheckBox( jEdit.getProperty("filesync.Enable_file_sync", "Enable file sync") );
        enableSync.setSelected( props.getProperty( "enableSync", "false" ).equals( "true" ) ? true : false );

        syncTree = new JTree();
        syncTree.setRootVisible( true );
        syncTree.setModel( getTreeModel() );
        DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        for ( int i = 0; i < syncTree.getRowCount(); i++ ) {
            syncTree.expandRow( i );
        }
        selectionModel.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        syncTree.setSelectionModel( selectionModel );

        JPanel buttonPanel = new JPanel( new KappaLayout() );
        addButton = new JButton( jEdit.getProperty("filesync.Add", "Add") );
        editButton = new JButton( jEdit.getProperty("filesync.Edit", "Edit") );
        removeButton = new JButton( jEdit.getProperty("filesync.Remove", "Remove") );
        buttonPanel.add( "0, 0, 1, 1, 0, w, 3", addButton );
        buttonPanel.add( "0, 1, 1, 1, 0, w, 3", editButton );
        buttonPanel.add( "0, 2, 1, 1, 0, w, 3", removeButton );

        JPanel scmPanel = new JPanel( new KappaLayout() );
        JLabel scmLabel = new JLabel( jEdit.getProperty("filesync.Auto-exclude_SCM_folders>", "Auto-exclude SCM folders:") );
        cvs = new JCheckBox( "cvs" );
        svn = new JCheckBox( "svn" );
        git = new JCheckBox( "git" );
        cvs.setSelected( props.getProperty( "noCvs", "true" ).equals( "true" ) ? true : false );
        svn.setSelected( props.getProperty( "noSvn", "true" ).equals( "true" ) ? true : false );
        git.setSelected( props.getProperty( "noGit", "true" ).equals( "true" ) ? true : false );
        scmPanel.add( "0, 1, 1, 1, W, w, 3", scmLabel );
        scmPanel.add( "1, 1, 1, 1, W, w, 3", cvs );
        scmPanel.add( "2, 1, 1, 1, W, w, 3", svn );
        scmPanel.add( "3, 1, 1, 1, W, w, 3", git );

        add( "0, 0, 4, 1, W, wh, 3", enableSync );
        add( "0, 1, 3, 1, 0, wh, 3", new JScrollPane( syncTree ) );
        add( "0, 2", KappaLayout.createStrut(250, 11 ) );
        add( "3, 1, 1, 1, 0, w,  3", buttonPanel );
        add( "0, 3, 4, 1, W, wh, 3", scmPanel );
    }

    private void initListeners() {
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                showEditDialog( null );
            }
        }
        );

        editButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                TreePath treePath = syncTree.getSelectionPath();
                switch ( treePath.getPathCount() ) {
                    case 1:                        // root node, do nothing
                        return;
                    case 2:                        // this is the node we're after
                        break;
                    default:                        // one level too low, need to go up one
                        treePath = treePath.getParentPath();
                        break;
                }
                DefaultMutableTreeNode sourceNode = ( DefaultMutableTreeNode ) treePath.getLastPathComponent();
                showEditDialog( sourceNode );
            }
        }
        );

        removeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                TreePath treePath = syncTree.getSelectionPath();
                switch ( treePath.getPathCount() ) {
                    case 1:                        // root node, do nothing
                        return;
                    case 2:                        // this is the node we're after
                        break;
                    default:                        // one level too low, need to go up one
                        treePath = treePath.getParentPath();
                        break;
                }
                // maybe delete the files
                int delete = JOptionPane.showConfirmDialog( PVOptionPane.this, jEdit.getProperty("filesync.Delete_files_from_target?", "Delete files from target?"), jEdit.getProperty("filesync.Delete_files_from_target?", "Delete files from target?"), JOptionPane.YES_NO_CANCEL_OPTION );
                DefaultMutableTreeNode sourceNode = ( DefaultMutableTreeNode ) treePath.getLastPathComponent();
                switch ( delete ) {
                case JOptionPane.YES_OPTION:
                    String target = String.valueOf( ( ( DefaultMutableTreeNode ) sourceNode.getChildAt(2 ) ).getUserObject() );
                        if ( target.indexOf( ": " ) > 0 ) {
                            target = target.substring( target.indexOf( ": " ) + 2 );
                        }
                        FileSyncPlugin.removeFiles( new File( target ) );
                        // also do the "no" option
                    case JOptionPane.NO_OPTION:
                        // remove the node from the tree
                        DefaultMutableTreeNode root = ( DefaultMutableTreeNode ) sourceNode.getParent();
                        root.remove( sourceNode );
                        ( ( DefaultTreeModel ) syncTree.getModel() ).nodeStructureChanged( root );
                        syncTree.validate();
                        syncTree.repaint();
                        for (int i = 0; i < syncTree.getRowCount(); i++) {
                            syncTree.expandRow(i);   
                        }
                        break;
                    default:
                        // do nothing on cancel
                }

            }
        }
        );
    }

    private void showEditDialog( final DefaultMutableTreeNode sourceNode ) {
        String source = null;
        String includes = null;
        String excludes = null;
        String target = null;
        if ( sourceNode != null ) {
            source = String.valueOf( sourceNode.getUserObject() );
            if ( source.indexOf( ": " ) > 0 ) {
                source = source.substring( source.indexOf( ": " ) + 2 );
            }
            includes = String.valueOf( ( ( DefaultMutableTreeNode ) sourceNode.getChildAt(0 ) ).getUserObject() );
            if ( includes.indexOf( ": " ) > 0 ) {
                includes = includes.substring( includes.indexOf( ": " ) + 2 );
            }
            excludes = String.valueOf( ( ( DefaultMutableTreeNode ) sourceNode.getChildAt(1 ) ).getUserObject() );
            if ( excludes.indexOf( ": " ) > 0 ) {
                excludes = excludes.substring( excludes.indexOf( ": " ) + 2 );
            }
            target = String.valueOf( ( ( DefaultMutableTreeNode ) sourceNode.getChildAt(2 ) ).getUserObject() );
            if ( target.indexOf( ": " ) > 0 ) {
                target = target.substring( target.indexOf( ": " ) + 2 );
            }
        }

        final View view = jEdit.getActiveView();
        final JDialog dialog = new JDialog( view, jEdit.getProperty("filesync.Sync_Files", "Sync Files"), true );
        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder(11, 11, 11, 11 ) );

        JLabel sourceLabel = new JLabel( jEdit.getProperty("filesync.Source>", "Source:") );
        final JTextField sourceField = new JTextField( source == null ? "" : source );
        JButton sourceBrowse = new JButton( jEdit.getProperty("filesync.Browse...", "Browse...") );
        JLabel includesLabel = new JLabel( jEdit.getProperty("filesync.Includes>", "Includes:") );
        final JTextField includesField = new JTextField( includes == null ? "*" : includes );
        JLabel excludesLabel = new JLabel( jEdit.getProperty("filesync.Excludes>", "Excludes:") );
        final JTextField excludesField = new JTextField( excludes == null ? "" : excludes );
        JLabel targetLabel = new JLabel( jEdit.getProperty("filesync.Target>", "Target:") );
        final JTextField targetField = new JTextField( target == null ? "" : target );
        JButton targetBrowse = new JButton( jEdit.getProperty("filesync.Browse...", "Browse...") );

        JPanel buttonPanel = new JPanel( new KappaLayout() );
        JButton okayButton = new JButton( jEdit.getProperty("filesync.Ok", "Ok") );
        JButton cancelButton = new JButton( jEdit.getProperty("filesync.Cancel", "Cancel") );
        buttonPanel.add( "0, 0, 1, 1, 0, wh, 3", okayButton );
        buttonPanel.add( "1, 0, 1, 1, 0, wh, 3", cancelButton );

        sourceBrowse.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                String[] paths = GUIUtilities.showVFSFileDialog( dialog, view, sourceField.getText().isEmpty() ? project.getRootPath() : sourceField.getText(), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                sourceField.setText( paths[0] );
            }
        }
        );

        targetBrowse.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                String[] paths = GUIUtilities.showVFSFileDialog( dialog, view, targetField.getText().isEmpty() ? project.getRootPath() : targetField.getText(), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false );
                targetField.setText( paths[0] );
            }
        }
        );

        okayButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                if ( sourceField.getText().isEmpty() ) {
                    JOptionPane.showMessageDialog( dialog, jEdit.getProperty("filesync.Source_directory_is_required.", "Source directory is required."), jEdit.getProperty("filesync.Error", "Error"), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                File file = new File( sourceField.getText() );
                if ( !file.exists() ) {
                    JOptionPane.showMessageDialog( dialog, jEdit.getProperty("filesync.Source_directory_does_not_exist.", "Source directory does not exist."), jEdit.getProperty("filesync.Error", "Error"), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                if ( includesField.getText().isEmpty() ) {
                    JOptionPane.showMessageDialog( dialog, jEdit.getProperty("filesync.Includes_is_required.", "Includes is required."), jEdit.getProperty("filesync.Error", "Error"), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                if ( targetField.getText().isEmpty() ) {
                    JOptionPane.showMessageDialog( dialog, jEdit.getProperty("filesync.Target_directory_is_required.", "Target directory is required."), jEdit.getProperty("filesync.Error", "Error"), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                file = new File( targetField.getText() );
                if ( !file.exists() ) {
                    JOptionPane.showMessageDialog( dialog, jEdit.getProperty("filesync.Target_directory_does_not_exist.", "Target directory does not exist."), jEdit.getProperty("filesync.Error", "Error"), JOptionPane.ERROR_MESSAGE );
                    return;
                }
                dialog.dispose();
                DefaultMutableTreeNode existingNode;
                if ( sourceNode == null ) {
                    existingNode = new DefaultMutableTreeNode( "Source: " + sourceField.getText() );
                    ( ( DefaultTreeModel ) syncTree.getModel() ).insertNodeInto( existingNode, ( DefaultMutableTreeNode ) syncTree.getModel().getRoot(), 0 );
                } else {
                    existingNode = sourceNode;
                    existingNode.removeAllChildren();
                }
                existingNode.add( new DefaultMutableTreeNode( "Include: " + includesField.getText() ) );
                existingNode.add( new DefaultMutableTreeNode( "Exclude: " + excludesField.getText() ) );
                existingNode.add( new DefaultMutableTreeNode( "Target: " + targetField.getText() ) );
                ( ( DefaultTreeModel ) syncTree.getModel() ).nodeStructureChanged( existingNode );
                for ( int i = 0; i < syncTree.getRowCount(); i++ ) {
                    syncTree.expandRow( i );
                }
                syncTree.validate();
                syncTree.repaint();
            }
        }
        );

        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                dialog.dispose();
            }
        }
        );

        panel.add( "0, 0, 1, 1, E, wh, 3", sourceLabel );
        panel.add( "1, 0, 2, 1, 0, wh, 3", sourceField );
        panel.add( "3, 0, 1, 1, W, wh, 3", sourceBrowse );
        panel.add( "0, 1, 1, 1, E, wh, 3", includesLabel );
        panel.add( "1, 1, 3, 1, 0, wh, 3", includesField );
        panel.add( "0, 2, 1, 1, E, wh, 3", excludesLabel );
        panel.add( "1, 2, 3, 1, 0, wh, 3", excludesField );
        panel.add( "0, 3, 1, 1, E, wh, 3", targetLabel );
        panel.add( "1, 3, 2, 1, 0, wh, 3", targetField );
        panel.add( "3, 3, 1, 1, W, wh, 3", targetBrowse );
        panel.add( "1, 4", KappaLayout.createStrut(200, 16 ) );
        panel.add( "0, 5, 4, 1, E,, 3", buttonPanel );

        dialog.setContentPane( panel );
        dialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        dialog.pack();
        center( view, dialog );
        dialog.setVisible( true );
    }

    private TreeModel getTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode( project.getName() );
        DefaultTreeModel model = new DefaultTreeModel( root );
        int i = 0;
        while ( true ) {
            String sourceFolder = props.getProperty( "sourcefolder." + i );
            if ( sourceFolder == null ) {
                break;
            }
            DefaultMutableTreeNode sourceNode = new DefaultMutableTreeNode( "Source: " + sourceFolder );
            root.add( sourceNode );
            String include = props.getProperty( "include." + i, "" );
            sourceNode.add( new DefaultMutableTreeNode( "Include: " + include ) );
            String exclude = props.getProperty( "exclude." + i, "" );
            sourceNode.add( new DefaultMutableTreeNode( "Exclude: " + exclude ) );
            String target = props.getProperty( "target." + i, "" );
            sourceNode.add( new DefaultMutableTreeNode( "Target: " + target ) );
            ++i;
        }
        return model;
    }

    /** Saves properties from the option pane. */
    protected void _save() {
        props = new Properties();
        props.setProperty( "enableSync", enableSync.isSelected() ? "true" : "false" );
        props.setProperty( "noCvs", cvs.isSelected() ? "true" : "false" );
        props.setProperty( "noSvn", svn.isSelected() ? "true" : "false" );
        props.setProperty( "noGit", git.isSelected() ? "true" : "false" );

        DefaultTreeModel model = ( DefaultTreeModel ) syncTree.getModel();
        DefaultMutableTreeNode root = ( DefaultMutableTreeNode ) model.getRoot();
        for ( int i = 0; i < root.getChildCount(); i++ ) {
            DefaultMutableTreeNode sourceNode = ( DefaultMutableTreeNode ) root.getChildAt( i );
            String source = String.valueOf( sourceNode.getUserObject() );
            if ( source.indexOf( ": " ) > 0 ) {
                source = source.substring( source.indexOf( ": " ) + 2 );
            }
            String includes = String.valueOf( ( ( DefaultMutableTreeNode ) sourceNode.getChildAt(0 ) ).getUserObject() );
            if ( includes.indexOf( ": " ) > 0 ) {
                includes = includes.substring( includes.indexOf( ": " ) + 2 );
            }
            String excludes = String.valueOf( ( ( DefaultMutableTreeNode ) sourceNode.getChildAt(1 ) ).getUserObject() );
            if ( excludes.indexOf( ": " ) > 0 ) {
                excludes = excludes.substring( excludes.indexOf( ": " ) + 2 );
            }
            String target = String.valueOf( ( ( DefaultMutableTreeNode ) sourceNode.getChildAt(2 ) ).getUserObject() );
            if ( target.indexOf( ": " ) > 0 ) {
                target = target.substring( target.indexOf( ": " ) + 2 );
            }
            props.setProperty( "sourcefolder." + i, source );
            props.setProperty( "include." + i, includes );
            props.setProperty( "exclude." + i, excludes );
            props.setProperty( "target." + i, target );
        }

        FileSyncPlugin.saveSyncProperties( project.getName(), props );
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
        if ( x < 0 ) {
            x = 0;
        }
        int y = my.y + ( my.height - your.height ) / 2;
        if ( y < 0 ) {
            y = 0;
        }
        you.setLocation( x, y );
    }

}