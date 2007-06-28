package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import ise.plugin.svn.data.CheckoutData;
import javax.swing.tree.DefaultMutableTreeNode;


public class BrowseRepository {

    public DefaultMutableTreeNode getRepository( CheckoutData cd ) throws CommandInitializationException, SVNException {

        // validate data values
        if ( cd.getURL() == null ) {
            return null;     // nothing to do
        }
        if ( cd.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( cd.getErr() == null ) {
            cd.setErr( cd.getOut() );
        }
        PrintStream out = cd.getOut();

        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( cd.getURL() ) );
        }
        catch ( SVNException svne ) {
            // perhaps a malformed URL is the cause of this exception
            cd.getOut().printError( "Error while creating an SVNRepository for location '"
                    + cd.getURL() + "': " + svne.getMessage() );
            return null;
        }

        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( cd.getUsername(), cd.getPassword() );
        repository.setAuthenticationManager( authManager );



        try {
            /*
             * Checks up if the specified path/to/repository part of the URL
             * really corresponds to a directory. If doesn't the program exits.
             * SVNNodeKind is that one who says what is located at a path in a
             * revision. -1 means the latest revision.
             */
            SVNNodeKind nodeKind = repository.checkPath( "", -1 );
            if ( nodeKind == SVNNodeKind.NONE ) {
                cd.getOut().printError( "There is no entry at '" + cd.getURL() + "'." );
                return null;
            }
            else if ( nodeKind == SVNNodeKind.FILE ) {
                cd.getOut().printError( "The entry at '" + cd.getURL() + "' is a file while a directory was expected." );
                return null;
            }
            /*
             * getRepositoryRoot() returns the actual root directory where the
             * repository was created. 'true' forces to connect to the repository
             * if the root url is not cached yet.
             */
            out.println( "Repository Root: " + repository.getRepositoryRoot( true ) );
            /*
             * getRepositoryUUID() returns Universal Unique IDentifier (UUID) of the
             * repository. 'true' forces to connect to the repository
             * if the UUID is not cached yet.
             */
            out.println( "Repository UUID: " + repository.getRepositoryUUID( true ) );
            out.println( "" );

            /*
             * Displays the repository tree at the current path - "" (what means
             * the path/to/repository directory)
             */
            listEntries( repository, "", out );
        }
        catch ( SVNException svne ) {
            cd.getOut().printError( "error while listing entries: "
                    + svne.getMessage() );
            return null;
        }
        /*
         * Gets the latest revision number of the repository
         */
        long latestRevision = -1;
        try {
            latestRevision = repository.getLatestRevision();
        }
        catch ( SVNException svne ) {
            cd.getOut().printError( "error while fetching the latest repository revision: "
                    + svne.getMessage() );
            return null;
        }
        out.println( "" );
        out.println( "---------------------------------------------" );
        out.println( "Repository latest revision: " + latestRevision );

        out.flush();
        out.close();

        return null;
    }


    /*
     * Called recursively to obtain all entries that make up the repository tree
     * repository - an SVNRepository which interface is used to carry out the
     * request, in this case it's a request to get all entries in the directory
     * located at the path parameter;
     *
     * path is a directory path relative to the repository location path (that
     * is a part of the URL used to create an SVNRepository instance);
     *
     */
    public static void listEntries( SVNRepository repository, String path, PrintStream out )
    throws SVNException {
        /*
         * Gets the contents of the directory specified by path at the latest
         * revision (for this purpose -1 is used here as the revision number to
         * mean HEAD-revision) getDir returns a Collection of SVNDirEntry
         * elements. SVNDirEntry represents information about the directory
         * entry. Here this information is used to get the entry name, the name
         * of the person who last changed this entry, the number of the revision
         * when it was last changed and the entry type to determine whether it's
         * a directory or a file. If it's a directory listEntries steps into a
         * next recursion to display the contents of this directory. The third
         * parameter of getDir is null and means that a user is not interested
         * in directory properties. The fourth one is null, too - the user
         * doesn't provide its own Collection instance and uses the one returned
         * by getDir.
         */
        Collection entries = repository.getDir( path, -1, null,
                ( Collection ) null );
        Iterator iterator = entries.iterator();
        while ( iterator.hasNext() ) {
            SVNDirEntry entry = ( SVNDirEntry ) iterator.next();
            out.println( "/" + ( path.equals( "" ) ? "" : path + "/" )
                    + entry.getName() + " (author: '" + entry.getAuthor()
                    + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")" );
            /*
             * Checking up if the entry is a directory.
             */
            if ( entry.getKind() == SVNNodeKind.DIR ) {
                listEntries( repository, ( path.equals( "" ) ) ? entry.getName()
                        : path + "/" + entry.getName(), out );
            }
        }
    }

}
