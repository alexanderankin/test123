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
import java.util.*;
import ise.plugin.svn.io.ConsolePrintStream;

/**
 * Base class to contain data to pass to the various subversion commands.
 */
public class SVNData {
    private ConsolePrintStream out;
    private ConsolePrintStream err;
    private List<String> paths;
    private boolean pathsAreUrls = false;
    private String username = "";
    private String password = "";
    private boolean recursive = false;
    private boolean force = false;
    private boolean dryRun = false;

    public SVNData(){}

    public SVNData( ConsolePrintStream out,
            ConsolePrintStream err,
            List<String> paths,
            String username,
            String password,
            boolean recursive) {
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
        return paths;
    }

    /**
     * Sets the value of paths.
     * @param paths The value to assign paths.
     */
    public void setPaths( List<String> paths ) {
        this.paths = paths;
    }

    public void addPath(String path) {
        if (paths == null) {
            paths = new ArrayList<String>();
        }
        paths.add(path);
    }

    public boolean pathsAreURLs() {
        return pathsAreUrls;
    }

    public void setPathsAreURLs(boolean b) {
        pathsAreUrls = b;
    }

    /**
     * Returns the value of username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of username.
     * @param username The value to assign username.
     */
    public void setUsername( String username ) {
        this.username = username;
    }

    /**
     * Returns the value of password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of password.
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

}
