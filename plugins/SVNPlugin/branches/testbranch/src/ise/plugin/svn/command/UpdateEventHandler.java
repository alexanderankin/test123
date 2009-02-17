/*
* ====================================================================
* Copyright (c) 2004-2007 TMate Software Ltd.  All rights reserved.
*
* This software is licensed as described in the file COPYING, which
* you should have received as part of this distribution.  The terms
* are also available at http://svnkit.com/license.html
* If newer versions of this license are posted there, you may use a
* newer version instead, at your option.
*
* danson, modified from SVNCommandEventProcessor for jEdit.
* ====================================================================
*/
package ise.plugin.svn.command;

import ise.plugin.svn.data.UpdateData;

import java.io.File;
import java.io.PrintStream;

import java.util.List;
import java.util.ArrayList;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNStatusType;

/**
 * @version 1.1.1
 * @author  TMate Software Ltd.
 */
public class UpdateEventHandler implements ISVNEventHandler {

    private boolean myIsExternal;
    private boolean myIsChanged;
    private boolean myIsExternalChanged;
    private boolean myIsCheckout = false;
    private boolean myIsExport = false;
    //private boolean myIsDelta;

    private UpdateData data = null;
    private final PrintStream myPrintStream;
    //private PrintStream myErrStream;

    private List<String> conflictedFiles;
    private List<String> addedFiles;
    private List<String> deletedFiles;
    private List<String> updatedFiles;

    public UpdateEventHandler( UpdateData data ) {
        this.data = data;
        myPrintStream = data.getOut();
        //myErrStream = data.getErr();
    }

    public void handleEvent( SVNEvent event, double progress ) {
        if ( event.getAction() == SVNEventAction.UPDATE_ADD ) {
            if ( myIsExternal ) {
                myIsExternalChanged = true;
            }
            else {
                myIsChanged = true;
            }
            if ( event.getContentsStatus() == SVNStatusType.CONFLICTED ) {
                myPrintStream.println( "C    " + SVNFormatUtil.formatPath( event.getFile() ) );
                if ( conflictedFiles == null ) {
                    conflictedFiles = new ArrayList<String>();
                }
                conflictedFiles.add( event.getFile().toString() );
            }
            else {
                myPrintStream.println( "A    " + SVNFormatUtil.formatPath( event.getFile() ) );
                if ( addedFiles == null )
                    addedFiles = new ArrayList<String>();
                addedFiles.add( event.getFile().toString() );
            }
        }
        else if ( event.getAction() == SVNEventAction.UPDATE_DELETE ) {
            if ( myIsExternal ) {
                myIsExternalChanged = true;
            }
            else {
                myIsChanged = true;
            }
            myPrintStream.println( "D    " + SVNFormatUtil.formatPath( event.getFile() ) );
            if ( deletedFiles == null )
                deletedFiles = new ArrayList<String>();
            deletedFiles.add( event.getFile().toString() );
        }
        else if ( event.getAction() == SVNEventAction.UPDATE_UPDATE ) {
            StringBuffer sb = new StringBuffer();
            if ( event.getNodeKind() != SVNNodeKind.DIR ) {
                if ( event.getContentsStatus() == SVNStatusType.CHANGED ) {
                    sb.append( "U" );
                }
                else if ( event.getContentsStatus() == SVNStatusType.CONFLICTED ) {
                    sb.append( "C" );
                    if ( conflictedFiles == null ) {
                        conflictedFiles = new ArrayList<String>();
                    }
                    conflictedFiles.add( event.getFile().toString() );
                }
                else if ( event.getContentsStatus() == SVNStatusType.MERGED ) {
                    sb.append( "G" );
                }
                else {
                    sb.append( " " );
                }
            }
            else {
                sb.append( ' ' );
            }
            if ( event.getPropertiesStatus() == SVNStatusType.CHANGED ) {
                sb.append( "U" );
            }
            else if ( event.getPropertiesStatus() == SVNStatusType.CONFLICTED ) {
                sb.append( "C" );
                if ( conflictedFiles == null ) {
                    conflictedFiles = new ArrayList<String>();
                }
                conflictedFiles.add( event.getFile().toString() );
            }
            else if ( event.getPropertiesStatus() == SVNStatusType.MERGED ) {
                sb.append( "G" );
            }
            else {
                sb.append( " " );
            }
            if ( sb.toString().trim().length() != 0 ) {
                if ( myIsExternal ) {
                    myIsExternalChanged = true;
                }
                else {
                    myIsChanged = true;
                }
            }
            if ( event.getLockStatus() == SVNStatusType.LOCK_UNLOCKED ) {
                sb.append( "B" );
            }
            else {
                sb.append( " " );
            }
            if ( sb.toString().trim().length() > 0 ) {
                myPrintStream.println( sb.toString() + "  " + SVNFormatUtil.formatPath( event.getFile() ) );
                if ( updatedFiles == null )
                    updatedFiles = new ArrayList<String>();
                updatedFiles.add( event.getFile().toString() );
            }
        }
        else if ( event.getAction() == SVNEventAction.UPDATE_COMPLETED ) {
            if ( !myIsExternal ) {
                if ( myIsChanged ) {
                    if ( myIsCheckout ) {
                        myPrintStream.println( "Checked out revision " + event.getRevision() + "." );
                    }
                    else if ( myIsExport ) {
                        myPrintStream.println( "Export complete." );
                    }
                    else {
                        myPrintStream.println( "Updated to revision " + event.getRevision() + "." );
                    }
                }
                else {
                    if ( myIsCheckout ) {
                        myPrintStream.println( "Checked out revision " + event.getRevision() + "." );
                    }
                    else if ( myIsExport ) {
                        myPrintStream.println( "Export complete." );
                    }
                    else {
                        myPrintStream.println( "At revision " + event.getRevision() + "." );
                    }
                }
            }
            else {
                if ( myIsExternalChanged ) {
                    if ( myIsCheckout ) {
                        myPrintStream.println( "Checked out external at revision " + event.getRevision() + "." );
                    }
                    else if ( myIsExport ) {
                        myPrintStream.println( "Export complete." );
                    }
                    else {
                        myPrintStream.println( "Updated external to revision " + event.getRevision() + "." );
                    }
                }
                else {
                    myPrintStream.println( "External at revision " + event.getRevision() + "." );
                }
                myPrintStream.println();
                myIsExternalChanged = false;
                myIsExternal = false;
            }
        }
        else if ( event.getAction() == SVNEventAction.UPDATE_EXTERNAL ) {
            myPrintStream.println();
            String path = getPath(event).replace( '/', File.separatorChar );
            if ( myIsCheckout ) {
                myPrintStream.println( "Fetching external item into '" + path + "'" );
            }
            else {
                myPrintStream.println( "Updating external item at '" + path + "'" );
            }
            myIsExternal = true;
        }
    }

    private String getPath(SVNEvent event) {
        File f = event.getFile();
        return f == null ? "" : f.getAbsolutePath();
    }
    public void checkCancelled() throws SVNCancelException {}

    public UpdateData getData() {
        data.setAddedFiles( addedFiles );
        data.setConflictedFiles( conflictedFiles );
        data.setDeletedFiles( deletedFiles );
        data.setUpdatedFiles( updatedFiles );
        return data;
    }

}