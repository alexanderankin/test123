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

package ise.plugin.svn.command;

import java.io.*;
import java.util.*;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import ise.plugin.svn.data.CheckoutData;
import ise.plugin.svn.gui.DirTreeNode;
import ise.plugin.svn.io.SVNFile;
import ise.plugin.svn.library.FileUtilities;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.SVNPlugin;


public class BrowseRepository {

    public List<DirTreeNode> getRepository( CheckoutData cd ) throws CommandInitializationException, SVNException {
        return getRepository( null, cd );
    }

    public List<DirTreeNode> getRepository( DirTreeNode node, CheckoutData cd ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate data values
        if ( node == null && cd.getURL() == null ) {
            return null;     // nothing to do
        }
        String url = node == null ? cd.getURL() : node.getRepositoryLocation();
        if ( cd.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( cd.getErr() == null ) {
            cd.setErr( cd.getOut() );
        }
        PrintStream out = cd.getOut();

        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
        }
        catch ( SVNException svne ) {
            // perhaps a malformed URL is the cause of this exception
            cd.getOut().printError( "Error while creating an SVNRepository for location '"
                    + url + "': " + svne.getMessage() );
            return null;
        }

        // set up authentication
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(),  cd.getUsername(), cd.getDecryptedPassword() );
        repository.setAuthenticationManager( authManager );

        List<DirTreeNode> children = null;
        try {
            /*
             * checks if the specified path/to/repository part of the URL
             * really corresponds to a directory. If doesn't the program exits.
             * SVNNodeKind is that one who says what is located at a path in a
             * revision. -1 means the latest revision.
             */
            SVNNodeKind nodeKind = repository.checkPath( "", -1 );
            if ( nodeKind == SVNNodeKind.NONE ) {
                cd.getOut().printError( "There is no entry at '" + url + "'." );
                return null;
            }
            else if ( nodeKind == SVNNodeKind.FILE ) {
                cd.getOut().printError( "The entry at '" + url + "' is a file while a directory was expected." );
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
            boolean isExternal = false;
            if ( node != null ) {
                isExternal = node.isExternal();
            }
            children = listEntries( repository, isExternal, "", out );
        }
        catch ( SVNException svne ) {
            cd.getOut().printError( "error while listing entries: "
                    + svne.getMessage() );
            svne.printStackTrace();
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

        return children;
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
    public List<DirTreeNode> listEntries( SVNRepository repository, boolean isExternal, String path, PrintStream out )
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
        List<DirTreeNode> list = new ArrayList<DirTreeNode>();
        SVNProperties dir_props = new SVNProperties();
        Collection entries = repository.getDir( path, -1L, dir_props, ( Collection ) null );
        Iterator iterator = entries.iterator();
        while ( iterator.hasNext() ) {
            SVNDirEntry entry = ( SVNDirEntry ) iterator.next();
            out.println( "/" + ( path.equals( "" ) ? "" : path + "/" )
                    + entry.getName() + " (author: '" + entry.getAuthor()
                    + "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")" );
            DirTreeNode node = new DirTreeNode( entry.getName(), !( entry.getKind() == SVNNodeKind.DIR ) );
            String repositoryLocation = repository.getLocation().toString() + "/" + entry.getName();
            node.setHasProperties( entry.hasProperties() );
            if ( isExternal ) {
                node.setExternal( true );
                node.setRepositoryLocation( repositoryLocation );
            }
            list.add( node );
        }

        // if the directory entry has svn:externals property, load those external
        // entries also and add them to the list
        if ( dir_props.size() > 0 ) {
            Object externals = dir_props.asMap().get( SVNProperty.EXTERNALS );
            try {
                if ( externals != null ) {
                    String value = externals.toString();
                    BufferedReader br = new BufferedReader( new StringReader( value ) );
                    String line = br.readLine();
                    while ( line != null ) {
                        String dir = line.substring( 0, line.indexOf( ' ' ) );
                        String rep = line.substring( line.indexOf( ' ' ) + 1 );
                        DirTreeNode node = new DirTreeNode( dir, false );
                        node.setExternal( true );
                        node.setRepositoryLocation( rep );
                        list.add( node );
                        line = br.readLine();
                    }
                    br.close();
                }
            }
            catch ( Exception e ) {     // NOPMD
                //e.printStackTrace();
                // ignored
            }
        }
        Collections.sort( list );
        List<DirTreeNode> newList = new ArrayList<DirTreeNode>();
        for ( DirTreeNode node : list ) {
            newList.add( ( DirTreeNode ) node );
        }
        return newList;
    }

    /**
     * Get a file at a particular revision.
     * @param url the repository url
     * @param filepath location of the file
     * @param revision the revision desired
     * @param username username requesting the file
     * @param password password of the user
     */
    public SVNFile getFile( String url, String filepath, SVNRevision revision, String username, String password ) {
        long rev = getRevisionNumber( url, filepath, revision, username, password );
        return getFile( url, filepath, rev, username, password );
    }

    /**
     * @param password encrypted password
     */
    public long getRevisionNumber( String url, String filepath, SVNRevision revision, String username, String password ) {
        setupLibrary();
        SVNRepository repository = null;
        long rev = -1;
        try {
            repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
            String pwd = null;
            if ( password != null && password.length() > 0 ) {
                pwd = PasswordHandler.decryptPassword( password );
            }
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(),  username, pwd );
            repository.setAuthenticationManager( authManager );
            if ( revision.getDate() != null ) {
                rev = repository.getDatedRevision( revision.getDate() );
            }
            else {
                rev = revision.getNumber();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return rev;
    }

    /**
     * Get a file at a particular revision.
     * @param url the repository url
     * @param filepath location of the file
     * @param revision the revision desired
     * @param username username requesting the file
     * @param password encrypted password of the user
     */
    public SVNFile getFile( String url, String filepath, long revision, String username, String password ) {
        setupLibrary();
        SVNRepository repository = null;
        SVNFile outfile = null;
        try {
            repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
            String pwd = null;
            if ( password != null && password.length() > 0 ) {
                pwd = PasswordHandler.decryptPassword( password );
            }
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(SVNPlugin.getSvnStorageDir(),  username, pwd );
            repository.setAuthenticationManager( authManager );

            SVNNodeKind nodeKind = repository.checkPath( filepath , revision );
            if ( nodeKind == SVNNodeKind.NONE || nodeKind == SVNNodeKind.DIR ) {
                return null;
            }
            SVNProperties fileproperties = new SVNProperties();
            ByteArrayOutputStream baos = new ByteArrayOutputStream( );
            repository.getFile( filepath , revision , fileproperties , baos );

            SVNPropertyValue mime_property = fileproperties.getSVNPropertyValue( SVNProperty.MIME_TYPE );
            String mimeType = mime_property == null ? null : mime_property.getString();
            boolean isTextType = SVNProperty.isTextMimeType( mimeType );

            // copy the properties to a Properties
            Properties props = convertMap( fileproperties );

            // ignore non-text files for now
            if ( isTextType ) {
                // copy the file contents to a temp file.  Preserve
                // the file name extension so that jEdit can apply highlighting.
                // Insert the revision number into the file name just before the
                // file extension (if any) to prevent overwriting.
                int index = filepath.lastIndexOf( '.' );
                index = index < 0 ? 0 : index;
                if ( index == 0 ) {
                    int slash_index = filepath.lastIndexOf( '/' );
                    if ( slash_index > 0 && slash_index < filepath.length() ) {
                        index = slash_index + 1;
                    }
                }
                String filename = filepath.substring( 0, index ) + "-" + ( revision < 0L ? "HEAD" : String.valueOf( revision ) ) + filepath.substring( index );
                filename = System.getProperty( "java.io.tmpdir" ) + "/" + filename;
                outfile = new SVNFile( filename );
                if ( outfile.exists() ) {
                    outfile.delete();
                }
                outfile.deleteOnExit();     // automatic cleanup
                outfile.getParentFile().mkdirs();
                StringReader reader = new StringReader( baos.toString() );
                BufferedWriter writer = new BufferedWriter( new FileWriter( outfile ) );
                FileUtilities.copy( reader, writer );
                writer.close();
                outfile.setProperties( props );
                return outfile;
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            outfile = null;
        }
        return outfile;
    }

    // converts SVNProperties to a Properties by taking the string value of the
    // names and values
    private Properties convertMap( SVNProperties map ) {
        Properties props = new Properties();
        Set names = map.nameSet();
        for ( Object name : names ) {
            if ( name == null ) {
                continue;
            }
            SVNPropertyValue value = map.getSVNPropertyValue( name.toString() );
            if ( value != null ) {
                props.setProperty( name.toString(), SVNPropertyValue.getPropertyAsString( value ) );
            }
        }
        return props;
    }

    /*
     * Initializes the svnkit library to work with a repository via
     * different protocols.
     */
    public static void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();

        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }
}