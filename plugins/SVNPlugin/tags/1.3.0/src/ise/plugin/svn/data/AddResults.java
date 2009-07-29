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


import java.util.*;

/**
 * Data returned from an "add" command.
 */
public class AddResults {

    // paths to be added
    private List<String> paths = new ArrayList<String>();

    // paths that can't be added and the reason why
    private TreeMap<String, String> error_paths = new TreeMap<String, String>();

    /**
     * @param path a path to add to these results
     */
    public void addPath( String path ) {
        paths.add( path );
    }

    public void addPaths( List<String> paths ) {
        if ( paths != null ) {
            this.paths.addAll( paths );
        }
    }

    public List<String> getPaths() {
        return paths;
    }

    /**
     * @param path a path that cannot be added
     * @param msg the reason why the path can't be added
     */
    public void addErrorPath( String path, String msg ) {
        error_paths.put( path, msg );
    }

    public Map<String, String> getErrorPaths() {
        return error_paths;
    }
}
