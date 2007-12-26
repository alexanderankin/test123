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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import ise.java.awt.KappaLayout;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.table.*;

import org.tmatesoft.svn.cli.command.SVNCommandEventProcessor;
import org.tmatesoft.svn.cli.SVNArgument;
import org.tmatesoft.svn.cli.SVNCommand;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNFormatUtil;
import org.tmatesoft.svn.core.internal.util.SVNPathUtil;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.xml.SVNXMLInfoHandler;
import org.tmatesoft.svn.core.wc.xml.SVNXMLSerializer;

public class SVNInfoPanel extends JPanel {


    public SVNInfoPanel( List<SVNInfo> infos ) {
        super( new GridLayout( 0, 1, 0, 3 ) );
        for ( SVNInfo info : infos ) {
            addInfo( info );
        }
    }

    private void addInfo( SVNInfo info ) {
        if ( info == null ) {
            return ;
        }
        final DefaultTableModel info_table_model = new DefaultTableModel( 1, 2 );

        // load the table model
        if ( !info.isRemote() ) {
            info_table_model.addRow( new String[] {"Path", SVNFormatUtil.formatPath( info.getFile() ) } );
        }
        else if ( info.getPath() != null ) {
            String path = info.getPath();
            path = path.replace( '/', File.separatorChar );
            info_table_model.addRow( new String[] {"Path", path} );
        }
        if ( info.getKind() != SVNNodeKind.DIR ) {
            String v = "";
            if ( info.isRemote() ) {
                v = SVNPathUtil.tail( info.getPath() );
            }
            else {
                v = info.getFile().getName();
            }
            info_table_model.addRow( new String[] {"Name", v} );
        }
        if ( info.getURL() != null ) {
            info_table_model.addRow( new String[] {"URL", info.getURL().toString() } );
        }
        if ( info.getRepositoryRootURL() != null ) {
            info_table_model.addRow( new String[] {"Repository Root", String.valueOf( info.getRepositoryRootURL() ) } );
        }
        if ( info.isRemote() && info.getRepositoryUUID() != null ) {
            info_table_model.addRow( new String[] {"Repository UUID", info.getRepositoryUUID() } );
        }
        if ( info.getRevision() != null && info.getRevision().isValid() ) {
            info_table_model.addRow( new String[] {"Revision", String.valueOf( info.getRevision() ) } );
        }
        if ( info.getKind() == SVNNodeKind.DIR ) {
            info_table_model.addRow( new String[] {"Node Kind", "directory"} );
        }
        else if ( info.getKind() == SVNNodeKind.FILE ) {
            info_table_model.addRow( new String[] {"Node Kind", "file"} );
        }
        else if ( info.getKind() == SVNNodeKind.NONE ) {
            info_table_model.addRow( new String[] {"Node Kind", "none"} );
        }
        else {
            info_table_model.addRow( new String[] {"Node Kind", "unknown"} );
        }
        if ( info.getSchedule() == null && !info.isRemote() ) {
            info_table_model.addRow( new String[] {"Schedule", "normal"} );
        }
        else if ( !info.isRemote() ) {
            info_table_model.addRow( new String[] {"Schedule", info.getSchedule() } );
        }
        if ( info.getAuthor() != null ) {
            info_table_model.addRow( new String[] {"Last Changed Author", info.getAuthor() } );
        }
        if ( info.getCommittedRevision() != null && info.getCommittedRevision().getNumber() >= 0 ) {
            info_table_model.addRow( new String[] {"Last Changed Revision", String.valueOf( info.getCommittedRevision() ) } );
        }
        if ( info.getCommittedDate() != null ) {
            info_table_model.addRow( new String[] {"Last Changed Date", formatDate( info.getCommittedDate() ) } );
        }
        if ( !info.isRemote() ) {
            if ( info.getTextTime() != null ) {
                info_table_model.addRow( new String[] {"Text Last Updated", formatDate( info.getTextTime() ) } );
            }
            if ( info.getPropTime() != null ) {
                info_table_model.addRow( new String[] {"Properties Last Updated", formatDate( info.getPropTime() ) } );
            }
            if ( info.getChecksum() != null ) {
                info_table_model.addRow( new String[] {"Checksum", info.getChecksum() } );
            }
            if ( info.getCopyFromURL() != null ) {
                info_table_model.addRow( new String[] {"Copied From URL", String.valueOf( info.getCopyFromURL() ) } );
            }
            if ( info.getCopyFromRevision() != null && info.getCopyFromRevision().getNumber() >= 0 ) {
                info_table_model.addRow( new String[] {"Copied From Revision", String.valueOf( info.getCopyFromRevision() ) } );
            }
            if ( info.getConflictOldFile() != null ) {
                info_table_model.addRow( new String[] {"Conflict Previous Base File", info.getConflictOldFile().getName() } );
            }
            if ( info.getConflictWrkFile() != null ) {
                info_table_model.addRow( new String[] {"Conflict Previous Working File", info.getConflictWrkFile().getName() } );
            }
            if ( info.getConflictNewFile() != null ) {
                info_table_model.addRow( new String[] {"Conflict Current Base File", info.getConflictNewFile().getName() } );
            }
            if ( info.getPropConflictFile() != null ) {
                info_table_model.addRow( new String[] {"Conflict Properties File", info.getPropConflictFile().getName() } );
            }
        }
        if ( info.getLock() != null ) {
            SVNLock lock = info.getLock();
            info_table_model.addRow( new String[] {"Lock Token", lock.getID() } );
            info_table_model.addRow( new String[] {"Lock Owner", lock.getOwner() } );
            info_table_model.addRow( new String[] {"Lock Created", formatDate( lock.getCreationDate() ) } );
            if ( lock.getComment() != null ) {
                int lineCount = getLineCount( lock.getComment() );
                StringBuffer sb = new StringBuffer();
                if ( lineCount == 1 ) {
                    sb.append( "(1 line)" );
                }
                else {
                    sb.append( "(" + lineCount + " lines)" );
                }
                sb.append( ":\n" + lock.getComment() + "\n" );
                info_table_model.addRow( new String[] {"Lock Comment", sb.toString() } );
            }
        }

        info_table_model.removeRow(0);
        JPanel panel = new JPanel( new BorderLayout() );
        add( panel );
        panel.setBorder( new EtchedBorder() );
        BestRowTable info_table = new BestRowTable();
        panel.add( info_table, BorderLayout.CENTER );
        info_table.setModel( info_table_model );
        TableColumn column1 = info_table.getColumnModel().getColumn( 1 );
        column1.setCellRenderer( new BestRowTable.ValueCellRenderer() );
        info_table.getColumnModel().getColumn( 0 ).setMaxWidth( 150 );
        info_table.getColumnModel().getColumn( 0 ).setMinWidth( 150 );
        info_table.getColumnModel().getColumn( 1 ).setPreferredWidth( 600 );
        info_table.packRows();
    }

    private String formatDate( Date date ) {
        return new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z (EE, d MMM yyyy)", Locale.getDefault() ).format( date );
    }

    private int getLineCount( String s ) {
        int count = 0;
        BreakIterator boundary = BreakIterator.getLineInstance();
        int start = boundary.first();
        for ( int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next() ) {
            ++count;
        }
        return count;
    }
}
