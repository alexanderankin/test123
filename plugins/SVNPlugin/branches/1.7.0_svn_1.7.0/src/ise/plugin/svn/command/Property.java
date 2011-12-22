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

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.ISVNPropertyHandler;
import org.tmatesoft.svn.core.wc.SVNPropertyData;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.SVNException;


import ise.plugin.svn.data.PropertyData;

/**
 * Gets properties for a file or directory.  Can recurse to get properties for
 * files in subdirectories.
 */
public class Property {

    // tree map sorts by key
    private TreeMap < String, Properties> results = new TreeMap < String, Properties> ();

    public PrintStream out = null;

    public void doSetProperties( PropertyData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();
        if ( data.getPaths() == null ) {
            return ;     // nothing to do
        }
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // convert paths to Files -- for property set, the paths should be files
        // since properties are only set on working copies.
        List<String> paths = data.getPaths();
        File[] localPaths = null;
        if ( !data.pathsAreURLs() ) {
            localPaths = new File[ paths.size() ];
            for ( int i = 0; i < paths.size(); i++ ) {
                localPaths[ i ] = new File( paths.get( i ) );
                // check for file existence?
            }

            // use the svnkit client manager
            SVNClientManager clientManager;
            if ( data.getUsername() != null ) {
                // use default svn config options
                ISVNOptions options = SVNWCUtil.createDefaultOptions( true );
                clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager( data.getUsername(), data.getDecryptedPassword() ) );
            }
            else {
                clientManager = SVNClientManager.newInstance();
            }

            // get a working copy client
            SVNWCClient wc_client = clientManager.getWCClient();

            // set an event handler so that messages go to the data streams for display
            wc_client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

            out = data.getOut();

            for ( File file : localPaths ) {
                // TODO: use user set revisions
                String name = data.getName();
                String value = data.getValue();
                PropertyHandler handler = new PropertyHandler( file );
                SVNDepth depth = data.getRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY;
                if ( name != null ) {
                    wc_client.doSetProperty( file, name, SVNPropertyValue.create( value ), data.getForce(), depth, handler, ( Collection<String> ) null );
                }
                else {
                    // check for multiple properties
                    Properties props = data.getProperties();
                    Set < Map.Entry < Object, Object >> set = props.entrySet();
                    for ( Map.Entry < Object, Object > me : set ) {
                        Object key = me.getKey();
                        if ( key != null ) {
                            name = key.toString();
                            value = String.valueOf( me.getValue() );
                            wc_client.doSetProperty( file, name, SVNPropertyValue.create( value ), data.getForce(), depth, handler, ( Collection<String> ) null );
                        }
                    }
                }
            }
        }
        out.flush();
        out.close();
    }

    /**
     * Fills a map of Properties based on the given data.
     * @param data data object containing the information necessary to fetch properties.
     */
    public void doGetProperties( PropertyData data ) throws CommandInitializationException, SVNException {
        SVNKit.setupLibrary();

        // validate data values
        if ( data.getPaths() == null ) {
            return ;     // nothing to do
        }
        if ( data.getOut() == null ) {
            throw new CommandInitializationException( "Invalid output stream." );
        }
        if ( data.getErr() == null ) {
            data.setErr( data.getOut() );
        }

        // convert paths to Files
        List<String> paths = data.getPaths();
        File[] localPaths = null;
        if ( !data.pathsAreURLs() ) {
            localPaths = new File[ paths.size() ];
            for ( int i = 0; i < paths.size(); i++ ) {
                localPaths[ i ] = new File( paths.get( i ) );
                // check for file existence?
            }
        }

        // use the svnkit client manager
        SVNClientManager clientManager;
        if ( data.getUsername() != null ) {
            // use default svn config options
            ISVNOptions options = SVNWCUtil.createDefaultOptions( true );
            clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager( data.getUsername(), data.getDecryptedPassword() ) );
        }
        else {
            clientManager = SVNClientManager.newInstance();
        }

        // get a working copy client
        SVNWCClient wc_client = clientManager.getWCClient();

        // set an event handler so that messages go to the data streams for display
        wc_client.setEventHandler( new SVNCommandEventProcessor( data.getOut(), data.getErr(), false ) );

        out = data.getOut();

        if ( data.pathsAreURLs() ) {
            for ( String path : data.getPaths() ) {
                SVNURL svnurl = SVNURL.parseURIDecoded( path );
                PropertyHandler handler = new PropertyHandler( path );
                // svnkit 1.2.x:
                // doGetProperty(SVNURL url, String propName, SVNRevision pegRevision, SVNRevision revision, SVNDepth depth, ISVNPropertyHandler handler)
                SVNDepth depth = data.isRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY;
                wc_client.doGetProperty( svnurl, data.getName(), SVNRevision.UNDEFINED, data.getRevision(), depth, handler );
                mergeResults( handler.getResults() );
            }
        }
        else {
            for ( File file : localPaths ) {
                PropertyHandler handler = new PropertyHandler( file );
                // svnkit 1.2.x:
                // doGetProperty(File path, String propName, SVNRevision pegRevision, SVNRevision revision, SVNDepth depth, ISVNPropertyHandler handler, Collection changeLists)
                SVNDepth depth = data.isRecursive() ? SVNDepth.INFINITY : SVNDepth.EMPTY;
                wc_client.doGetProperty( file, data.getName(), SVNRevision.UNDEFINED, data.getRevision(), depth, handler, null );
                mergeResults( handler.getResults() );
            }
        }
        out.flush();
        out.close();
    }

    private void mergeResults( TreeMap<String, Properties> handler_results ) {
        if ( handler_results == null || handler_results.size() == 0 ) {
            return ;
        }

        // the results are a path <-> properties map, there may be multiple path
        // entries if recursive is true, so need to merge the handler results
        // with previous results.
        Set < Map.Entry < String, Properties >> set = handler_results.entrySet();
        for ( Map.Entry<String, Properties> me : set ) {
            String handler_name = me.getKey();
            if ( handler_name != null ) {
                Properties handler_value = me.getValue() == null ? new Properties() : me.getValue();
                Properties results_value = ( Properties ) results.get( handler_name );
                if ( results_value == null ) {
                    results_value = handler_value;
                }
                else {
                    results_value.putAll( handler_value );
                }
                results.put( handler_name, results_value );
            }
        }
    }

    public class PropertyHandler implements ISVNPropertyHandler {
        private String path = "";
        private TreeMap < String, Properties> results = new TreeMap < String, Properties> ();

        public PropertyHandler( File f ) {
            path = f.toString();
        }

        public PropertyHandler( String p ) {
            path = p;
        }
        public void handleProperty( File path, SVNPropertyData property ) {
            if ( property == null ) {
                return ;
            }
            String key = path.toString();
            Properties prop = ( Properties ) results.get( key );
            if ( prop == null ) {
                prop = new Properties();
                results.put( key, prop );
            }
            String name = property.getName();
            String value = SVNPropertyValue.getPropertyAsString(property.getValue());
            prop.setProperty( name, value );
            out.println( "F " + key + ": " + value + " = " + value );
        }

        public void handleProperty( long revision, SVNPropertyData property ) {
            // this is for revision properties,
            // research needed -- will this be called for each revision between
            // peg and end for the same file?
            Properties prop = ( Properties ) results.get( path );
            if ( prop == null ) {
                prop = new Properties();
                results.put( path, prop );
            }
            String key = property.getName();
            String value = SVNPropertyValue.getPropertyAsString(property.getValue());
            prop.setProperty( key, value );
            out.println( "R " + path + ": " + key + " = " + value );
        }

        public void handleProperty( SVNURL url, SVNPropertyData property ) {
            String key = url.toString();
            Properties prop = ( Properties ) results.get( key );
            if ( prop == null ) {
                prop = new Properties();
                results.put( key, prop );
            }
            String name = property.getName();
            String value = SVNPropertyValue.getPropertyAsString(property.getValue());
            prop.setProperty( name, value );
            out.println( "U " + key + ": " + name + " = " + value );
        }

        public String getPath() {
            return path;
        }

        public TreeMap<String, Properties> getResults() {
            if ( results.size() == 0 ) {
                Properties p = new Properties();
                p.setProperty( "", "" );
                results.put( path, p );
            }
            return results;
        }
    }

    public PropertyHandler getPropertyHandler( File file ) {
        return new PropertyHandler( file );
    }

    public TreeMap<String, Properties> getProperties() {
        return results;
    }

    /*
    public static void main ( String[] args ) {
        // for testing
        try {
            org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory.setup();
            org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl.setup();
            org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory.setup();
            ISVNOptions options = SVNWCUtil.createDefaultOptions( true );
            SVNClientManager clientManager = SVNClientManager.newInstance( options, SVNWCUtil.createDefaultAuthenticationManager( "daleanson", "" ) );
            SVNWCClient wc_client = clientManager.getWCClient();
            Property prop = new Property();
            prop.out = System.out;
            File file = new File( args[ 0 ] );
            PropertyHandler handler = prop.getPropertyHandler( file );
            //wc_client.doSetProperty( file, "test:key", "test value", false, false, handler );
            wc_client.doGetProperty( file, null, SVNRevision.UNDEFINED, SVNRevision.WORKING, false, handler );

        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
}
    */
}