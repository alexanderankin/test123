/*
 * ====================================================================
 * Copyright (c) 2004-2008 TMate Software Ltd.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://svnkit.com/license.html
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
 
/*
danson, copied from svnkit 1.1.8 code since this class has been removed from
the svnkit 1.2.x codebase and it is used quite a bit by classes in this 
package.
*/
 
//package org.tmatesoft.svn.cli.command;
package ise.plugin.svn.command;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNLock;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNStatusType;

/**
 * @version 1.1.1
 * @author  TMate Software Ltd.
 */
public class SVNCommandEventProcessor implements ISVNEventHandler {

    private boolean myIsExternal;
    private boolean myIsChanged;
    private boolean myIsExternalChanged;
    private boolean myIsCheckout;
    private boolean myIsExport;
    private boolean myIsDelta;

    private final PrintStream myPrintStream;
    private PrintStream myErrStream;

    public SVNCommandEventProcessor(PrintStream out, PrintStream err, boolean checkout) {
        this(out, err, checkout, false);
    }

    public SVNCommandEventProcessor(PrintStream out, PrintStream err, boolean checkout, boolean export) {
        myPrintStream = out;
        myErrStream = err;
        myIsCheckout = checkout;
        myIsExport = export;
    }

    public void handleEvent(SVNEvent event, double progress) {
        String commitPath = null;
        if (event.getAction() == SVNEventAction.COMMIT_ADDED || event.getAction() == SVNEventAction.COMMIT_MODIFIED ||
                event.getAction() == SVNEventAction.COMMIT_DELETED || event.getAction() == SVNEventAction.COMMIT_REPLACED) {
            File root = new File(".");
            File file = event.getFile();
            try {
                if (root.getCanonicalFile().equals(file.getCanonicalFile()) || 
                    file.getCanonicalFile().toString().startsWith(root.getCanonicalFile().toString())) {
                    commitPath = SVNFormatUtil.formatPath(event.getFile());
                } else {
                    commitPath = getPath(event);
                    if ("".equals(commitPath)) {
                        commitPath = ".";
                    }
                }
            } catch (IOException e) {   // NOPMD
            }
        }
        if (event.getAction() == SVNEventAction.COMMIT_MODIFIED) {
            myPrintStream.println( "Sending        " + commitPath);
        } else if (event.getAction() == SVNEventAction.COMMIT_DELETED) {
            myPrintStream.println( "Deleting       " + commitPath);
        } else if (event.getAction() == SVNEventAction.COMMIT_REPLACED) {
            myPrintStream.println( "Replacing      " + commitPath);
        } else if (event.getAction() == SVNEventAction.COMMIT_DELTA_SENT) {
            if (!myIsDelta) {
                myPrintStream.print( "Transmitting file data ");
                myIsDelta = true;
            }
            myPrintStream.print( ".");
        } else if (event.getAction() == SVNEventAction.COMMIT_ADDED) {
            String mimeType = event.getMimeType();
            if (SVNProperty.isBinaryMimeType(mimeType)) {
                myPrintStream.println( "Adding  (bin)  " + commitPath);
            } else {
                myPrintStream.println( "Adding         " + commitPath);
            }
        } else if (event.getAction() == SVNEventAction.REVERT) {
            myPrintStream.println( "Reverted '" + SVNFormatUtil.formatPath(event.getFile()) + "'");
        } else if (event.getAction() == SVNEventAction.FAILED_REVERT) {
            myPrintStream.println( "Failed to revert '" + SVNFormatUtil.formatPath(event.getFile()) + "' -- try updating instead.");
        } else if (event.getAction() == SVNEventAction.LOCKED) {
            String path = getPath(event);
            if (event.getFile() != null) {
                path = SVNFormatUtil.formatPath(event.getFile());
            }
            SVNLock lock = event.getLock();
            myPrintStream.println( "'" + path + "' locked by user '" + lock.getOwner() + "'.");
        } else if (event.getAction() == SVNEventAction.UNLOCKED) {
            String path = getPath(event);
            if (event.getFile() != null) {
                path = SVNFormatUtil.formatPath(event.getFile());
            }
            myPrintStream.println( "'" + path + "' unlocked.");
        } else if (event.getAction() == SVNEventAction.UNLOCK_FAILED) {
            myErrStream.println("error: " + event.getErrorMessage());
        } else if (event.getAction() == SVNEventAction.LOCK_FAILED) {
            myErrStream.println("error: " + event.getErrorMessage());
        } else if (event.getAction() == SVNEventAction.UPDATE_ADD) {
            if (myIsExternal) {
                myIsExternalChanged = true;
            } else {
                myIsChanged = true;
            }
            if (event.getContentsStatus() == SVNStatusType.CONFLICTED) {
                myPrintStream.println( "C    " + SVNFormatUtil.formatPath(event.getFile()));
            } else {
                myPrintStream.println( "A    " + SVNFormatUtil.formatPath(event.getFile()));
            }
        } else if (event.getAction() == SVNEventAction.UPDATE_DELETE) {
            if (myIsExternal) {
                myIsExternalChanged = true;
            } else {
                myIsChanged = true;
            }
            myPrintStream.println( "D    " + SVNFormatUtil.formatPath(event.getFile()));
        } else if (event.getAction() == SVNEventAction.UPDATE_UPDATE) {
            StringBuffer sb = new StringBuffer();
            if (event.getNodeKind() != SVNNodeKind.DIR) {
                if (event.getContentsStatus() == SVNStatusType.CHANGED) {
                    sb.append("U");
                } else if (event.getContentsStatus() == SVNStatusType.CONFLICTED) {
                    sb.append("C");
                } else if (event.getContentsStatus() == SVNStatusType.MERGED) {
                    sb.append("G");
                } else {
                    sb.append(" ");
                }
            } else {
                sb.append(' ');
            }
            if (event.getPropertiesStatus() == SVNStatusType.CHANGED) {
                sb.append("U");
            } else if (event.getPropertiesStatus() == SVNStatusType.CONFLICTED) {
                sb.append("C");
            } else if (event.getPropertiesStatus() == SVNStatusType.MERGED) {
                sb.append("G");
            } else {
                sb.append(" ");
            }
            if (sb.toString().trim().length() != 0) {
                if (myIsExternal) {
                    myIsExternalChanged = true;
                } else {
                    myIsChanged = true;
                }
            }
            if (event.getLockStatus() == SVNStatusType.LOCK_UNLOCKED) {
                sb.append("B");
            } else {
                sb.append(" ");
            }
            if (sb.toString().trim().length() > 0) {
                myPrintStream.println( sb.toString() + "  " + SVNFormatUtil.formatPath(event.getFile()));
            }
        } else if (event.getAction() == SVNEventAction.UPDATE_COMPLETED) {
            if (!myIsExternal) {
                if (myIsChanged) {
                    if (myIsCheckout) {
                        myPrintStream.println( "Checked out revision " + event.getRevision() + ".");
                    } else if (myIsExport) {
                        myPrintStream.println( "Export complete.");
                    } else {
                        myPrintStream.println( "Updated to revision " + event.getRevision() + ".");
                    }
                } else {
                    if (myIsCheckout) {
                        myPrintStream.println( "Checked out revision " + event.getRevision() + ".");
                    } else if (myIsExport) {
                        myPrintStream.println( "Export complete.");
                    } else {
                        myPrintStream.println( "At revision " + event.getRevision() + ".");
                    }
                }
            } else {
                if (myIsExternalChanged) {
                    if (myIsCheckout) {
                        myPrintStream.println( "Checked out external at revision " + event.getRevision() + ".");
                    } else if (myIsExport) {
                        myPrintStream.println( "Export complete.");
                    } else {
                        myPrintStream.println( "Updated external to revision " + event.getRevision() + ".");
                    }
                } else {
                    myPrintStream.println( "External at revision " + event.getRevision() + ".");
                }
                myPrintStream.println();
                myIsExternalChanged = false;
                myIsExternal = false;
            }
        } else if (event.getAction() == SVNEventAction.UPDATE_EXTERNAL) {
            myPrintStream.println();
            String path = getPath(event).replace('/', File.separatorChar);
            if (myIsCheckout) {
                myPrintStream.println( "Fetching external item into '" + path + "'");
            } else {
                myPrintStream.println( "Updating external item at '" + path + "'");
            }
            myIsExternal = true;
        } else if (event.getAction() == SVNEventAction.STATUS_EXTERNAL) {
            myPrintStream.println();
            String path = getPath(event).replace('/', File.separatorChar);
            myPrintStream.println( "Performing status on external item at '" + path + "'");
            myIsExternal = true;
        } else if (event.getAction() == SVNEventAction.RESTORE) {
            myPrintStream.println( "Restored '" + SVNFormatUtil.formatPath(event.getFile()) + "'");
        } else if (event.getAction() == SVNEventAction.ADD) {
            if (SVNProperty.isBinaryMimeType(event.getMimeType())) {
                myPrintStream.println( "A  (bin)  " + SVNFormatUtil.formatPath(event.getFile()));
            } else {
                myPrintStream.println( "A         " + SVNFormatUtil.formatPath(event.getFile()));
            }
        } else if (event.getAction() == SVNEventAction.DELETE) {
            myPrintStream.println(     "D         " + SVNFormatUtil.formatPath(event.getFile()));
        } else if (event.getAction() == SVNEventAction.SKIP) {
            String path = SVNFormatUtil.formatPath(event.getFile());
            if (path != null) {
                myPrintStream.println( "Skipped '" + path + "'");
            }
            else {
                myPrintStream.println("Skipped file. Check username/password.");   
            }
            if (myIsExternal && event.getExpectedAction() == SVNEventAction.UPDATE_EXTERNAL) {
                myIsExternal = false;
                myIsExternalChanged = false;
            }
        } else if (event.getAction() == SVNEventAction.RESOLVED) {
            myPrintStream.println( "Resolved conflicted state of '" + SVNFormatUtil.formatPath(event.getFile()) + "'");
        } else if (event.getAction() == SVNEventAction.STATUS_COMPLETED) {
            myPrintStream.println( "Status against revision: " + SVNFormatUtil.formatString(Long.toString(event.getRevision()), 6, false));
        }
    }

    private String getPath(SVNEvent event) {
        File f = event.getFile();
        return f == null ? "" : f.getAbsolutePath();
    }
    
    public void checkCancelled() throws SVNCancelException {
    }
}