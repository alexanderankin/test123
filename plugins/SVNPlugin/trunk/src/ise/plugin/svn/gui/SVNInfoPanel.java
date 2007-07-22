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

import java.awt.GridLayout;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import ise.java.awt.KappaLayout;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z (EE, d MMM yyyy)", Locale.getDefault() );

    public SVNInfoPanel( List<SVNInfo> infos ) {
        super( new GridLayout( 0, 1, 0, 3 ) );
        for ( SVNInfo info : infos ) {
            addInfo( info );
        }
    }

    private void addInfo( SVNInfo info ) {
        JPanel panel = new JPanel( new KappaLayout() );
        add(panel);
        panel.setBorder(new EtchedBorder());
        KappaLayout.Constraints con = KappaLayout.createConstraint();
        con.p = 3;
        con.a = KappaLayout.W;
        con.y = -1;
        if ( !info.isRemote() ) {
            ++con.y;
            JLabel label = new JLabel( "Path:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( SVNFormatUtil.formatPath( info.getFile() ) );
            con.x = 1;
            panel.add( value, con );
        }
        else if ( info.getPath() != null ) {
            String path = info.getPath();
            path = path.replace( '/', File.separatorChar );
            ++con.y;
            JLabel label = new JLabel( "Path:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( path );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getKind() != SVNNodeKind.DIR ) {
            String v = "";
            if ( info.isRemote() ) {
                v = SVNPathUtil.tail( info.getPath() );
            }
            else {
                v = info.getFile().getName();
            }
            ++con.y;
            JLabel label = new JLabel( "Name:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( v );
            con.x = 1;
            panel.add( value, con );
        }
        if (info.getURL() != null) {
            ++con.y;
            JLabel label = new JLabel( "URL:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( info.getURL().toString() );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getRepositoryRootURL() != null ) {
            ++con.y;
            JLabel label = new JLabel( "Repository Root:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( info.getRepositoryRootURL().toString() );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.isRemote() && info.getRepositoryUUID() != null ) {
            ++con.y;
            JLabel label = new JLabel( "Repository UUID:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( info.getRepositoryUUID() );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getRevision() != null && info.getRevision().isValid() ) {
            ++con.y;
            JLabel label = new JLabel( "Revision:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( String.valueOf(info.getRevision()) );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getKind() == SVNNodeKind.DIR ) {
            ++con.y;
            JLabel label = new JLabel( "Node Kind:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( "directory" );
            con.x = 1;
            panel.add( value, con );
        }
        else if ( info.getKind() == SVNNodeKind.FILE ) {
            ++con.y;
            JLabel label = new JLabel( "Node Kind:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( "file" );
            con.x = 1;
            panel.add( value, con );
        }
        else if ( info.getKind() == SVNNodeKind.NONE ) {
            ++con.y;
            JLabel label = new JLabel( "Node Kind:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( "none" );
            con.x = 1;
            panel.add( value, con );
        }
        else {
            ++con.y;
            JLabel label = new JLabel( "Node Kind:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( "unknown" );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getSchedule() == null && !info.isRemote() ) {
            ++con.y;
            JLabel label = new JLabel( "Schedule:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( "normal" );
            con.x = 1;
            panel.add( value, con );
        }
        else if ( !info.isRemote() ) {
            ++con.y;
            JLabel label = new JLabel( "Schedule:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( info.getSchedule() );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getAuthor() != null ) {
            ++con.y;
            JLabel label = new JLabel( "Last Changed Author:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( info.getAuthor() );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getCommittedRevision() != null && info.getCommittedRevision().getNumber() >= 0 ) {
            ++con.y;
            JLabel label = new JLabel( "Last Changed Rev:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( String.valueOf(info.getCommittedRevision()) );
            con.x = 1;
            panel.add( value, con );
        }
        if ( info.getCommittedDate() != null ) {
            ++con.y;
            JLabel label = new JLabel( "Last Changed Date:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel( formatDate( info.getCommittedDate() ) );
            con.x = 1;
            panel.add( value, con );
        }
        if ( !info.isRemote() ) {
            if ( info.getTextTime() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Text Last Updated:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(formatDate( info.getTextTime() ));
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getPropTime() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Properties Last Updated:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(formatDate( info.getPropTime() ));
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getChecksum() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Checksum:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(info.getChecksum());
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getCopyFromURL() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Copied From URL:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(info.getCopyFromURL().toString());
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getCopyFromRevision() != null && info.getCopyFromRevision().getNumber() >= 0 ) {
                ++con.y;
                JLabel label = new JLabel( "Copied From Rev:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(String.valueOf(info.getCopyFromRevision()));
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getConflictOldFile() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Conflict Previous Base File:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(info.getConflictOldFile().getName());
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getConflictWrkFile() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Conflict Previous Working File:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(info.getConflictWrkFile().getName());
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getConflictNewFile() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Conflict Current Base File:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(info.getConflictNewFile().getName());
                con.x = 1;
                panel.add( value, con );
            }
            if ( info.getPropConflictFile() != null ) {
                ++con.y;
                JLabel label = new JLabel( "Conflict Properties File:" );
                con.x = 0;
                panel.add( label, con );
                JLabel value = new JLabel(info.getPropConflictFile().getName());
                con.x = 1;
                panel.add( value, con );
            }
        }
        if ( info.getLock() != null ) {
            SVNLock lock = info.getLock();
            ++con.y;
            JLabel label = new JLabel( "Lock Token:" );
            con.x = 0;
            panel.add( label, con );
            JLabel value = new JLabel(lock.getID());
            con.x = 1;
            panel.add( value, con );
            ++con.y;
            label = new JLabel( "Lock Owner:" );
            con.x = 0;
            panel.add( label, con );
            value = new JLabel(lock.getOwner());
            con.x = 1;
            panel.add( value, con );
            ++con.y;
            label = new JLabel( "Lock Created:" );
            con.x = 0;
            panel.add( label, con );
            value = new JLabel(formatDate( lock.getCreationDate() ));
            con.x = 1;
            panel.add( value, con );
            if ( lock.getComment() != null ) {
                ++con.y;
                label = new JLabel( "Lock Comment" );
                con.x = 0;
                panel.add( label, con );
                int lineCount = getLineCount( lock.getComment() );
                StringBuffer sb = new StringBuffer();
                if ( lineCount == 1 ) {
                    sb.append( "(1 line)" );
                }
                else {
                    sb.append( "(" + lineCount + " lines)" );
                }
                sb.append( ":\n" + lock.getComment() + "\n" );
                value = new JLabel(sb.toString());
                con.x = 1;
                panel.add( value, con );
            }
        }
    }

    private String formatDate( Date date ) {
        return DATE_FORMAT.format( date );
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
