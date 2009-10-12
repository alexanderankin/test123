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

package ise.plugin.svn.data;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PrivilegedAccessor;

/**
 * Base class to contain data to pass to the various subversion commands.
 */
public class SVNData implements Serializable {

    private static final long serialVersionUID = 42L;

    private transient ConsolePrintStream out;
    private transient ConsolePrintStream err;
    private List<String> paths;
    private boolean pathsAreUrls = false;
    private String username = null;
    private String password = null;
    private boolean recursive = false;
    private boolean force = false;
    private boolean dryRun = false;
    private boolean remote = true;

    public SVNData() {}

    public SVNData( ConsolePrintStream out,
            ConsolePrintStream err,
            List<String> paths,
            String username,
            String password,
            boolean recursive ) {
        this.out = out;
        this.err = err;
        this.paths = paths;
        this.username = username;
        this.password = password;
        this.recursive = recursive;
    }

    /**
     * Returns the value of out.
     */
    public ConsolePrintStream getOut() {
        return out;
    }

    /**
     * Sets the value of out.
     * @param out The value to assign out.
     */
    public void setOut( ConsolePrintStream out ) {
        this.out = out;
    }

    /**
     * Returns the value of err.
     */
    public ConsolePrintStream getErr() {
        return err;
    }

    /**
     * Sets the value of err.
     * @param err The value to assign err.
     */
    public void setErr( ConsolePrintStream err ) {
        this.err = err;
    }

    /**
     * Returns the value of paths.
     */
    public List<String> getPaths() {
        return new ArrayList<String>( paths );
    }

    /**
     * Sets the value of paths.
     * @param paths The value to assign paths.
     */
    public void setPaths( List<String> paths ) {
        this.paths = paths;
    }

    public void addPath( String path ) {
        if ( paths == null ) {
            paths = new ArrayList<String>();
        }
        paths.add( path );
    }

    public boolean pathsAreURLs() {
        return pathsAreUrls;
    }

    public void setPathsAreURLs( boolean b ) {
        pathsAreUrls = b;
    }

    /**
     * Returns the value of username, returns null if previously set to empty string.
     */
    public String getUsername() {
        return username == null || username.length() == 0 ? null : username;
    }

    /**
     * Sets the value of username.
     * @param username The value to assign username.
     */
    public void setUsername( String username ) {
        this.username = username;
    }

    /**
     * Returns the value of password, returns null if previously set to empty string.
     */
    public String getPassword() {
        return password == null || password.length() == 0 ? null : password;
    }

    public String getDecryptedPassword() {
        return PasswordHandler.decryptPassword( password );
    }

    /**
     * Sets the value of encrypted password.
     * @param password The value to assign password.
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * Returns the value of recursive.
     */
    public boolean getRecursive() {
        return recursive;
    }

    /**
     * Sets the value of recursive.
     * @param recursive The value to assign recursive.
     */
    public void setRecursive( boolean recursive ) {
        this.recursive = recursive;
    }

    /**
     * Returns the value of force.
     */
    public boolean getForce() {
        return force;
    }

    /**
     * Sets the value of force.
     * @param force The value to assign force.
     */
    public void setForce( boolean force ) {
        this.force = force;
    }

    /**
     * Returns the value of dryRun.
     */
    public boolean getDryRun() {
        return dryRun;
    }

    /**
     * Sets the value of dryRun.
     * @param dryRun The value to assign dryRun.
     */
    public void setDryRun( boolean dryRun ) {
        this.dryRun = dryRun;
    }

    /**
     * Returns the value of remote.
     */
    public boolean getRemote() {
        return remote;
    }

    /**
     * Set whether this data represents about a working copy or a remote/repository
     * copy of a file or directory.
     */
    public void setRemote( boolean b ) {
        remote = b;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append( this.getClass().getName() ).append( '[' );
            Field[] fields = this.getClass().getDeclaredFields();
            for ( Field field : fields ) {
                sb.append( field.getName() ).append( '=' ).append( PrivilegedAccessor.getValue( this, field.getName() ) ).append( ',' );
            }
            sb.append( ']' );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String toString( Object data ) {
        if ( data == null ) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        try {
            sb.append( data.getClass().getName() ).append( '\n' );
            Field[] fields = data.getClass().getDeclaredFields();
            List<String> fieldNames = new ArrayList<String>();
            for ( Field field : fields ) {
                fieldNames.add( field.getName() );
            }
            Collections.sort( fieldNames );
            int longest = 0;
            for ( String name : fieldNames ) {
                if ( name.length() > longest ) {
                    longest = name.length();
                }
            }
            String padding = "                            ";
            for ( String name : fieldNames ) {
                if ( "password".equals( name ) ) {
                    continue;
                }
                sb.append( '\t' ).append( padding.substring( 0, longest - name.length() ) ).append( name ).append( ": " ).append( PrivilegedAccessor.getValue( data, name ) ).append( '\n' );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}