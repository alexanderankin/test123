/*
Copyright (c) 2005, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
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
package sidekick.java.node;

import java.util.*;

/**
 * An extension of TigerNode for a compilation unit.  A compilation unit should
 * be the top-level unit for parsing, all other productions represented by TigerNodes
 * should be children of a CUNode.
 */

public class CUNode extends TigerNode {

    private String packageName = "";
    private List<ImportNode> imports = null;
    private Results results = null;
    private String filename = null;

    public CUNode() {
        super( "", 0 );
    }

    public void setFilename( String name ) {
        filename = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setPackageName( String name ) {
        packageName = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getOrdinal() {
        return TigerNode.COMPILATION_UNIT;
    }

    public void addImport( ImportNode in ) {
        if ( in == null )
            return ;
        if ( imports == null )
            imports = new ArrayList<ImportNode>();
        imports.add( in );
    }

    /** @return List<String> */
    public List<String> getImports() {
        List<String> list = new ArrayList<String>();
        if ( imports == null ) {
            return list;
        }
        for ( ImportNode in : imports ) {
            list.add( in.getName() );
        }
        Collections.sort( list );
        return list;
    }

    /** @return List<ImportNode> */
    public List<ImportNode> getImportNodes() {
        if ( imports != null ) {
            Collections.sort( imports, new Comparator<ImportNode>() {
                        public int compare( ImportNode a, ImportNode b ) {
                            return a.getName().compareTo( b.getName() );
                        }
                    }
                            );
            return new ArrayList<ImportNode>(imports);
        }
        return new ArrayList<ImportNode>();
    }

    public ImportNode getImport( String name ) {
        if ( imports == null ) {
            return null;
        }
        for ( Iterator it = imports.iterator(); it.hasNext(); ) {
            ImportNode in = ( ImportNode ) it.next();
            if ( in.getName().equals( name ) ) {
                return in;
            }
        }
        return null;
    }

    public void setResults( Results r ) {
        results = r;
    }

    public Results getResults() {
        return results;
    }

    public String toString() {
        return super.toString() + ( results != null ? ", " + results.toString() : "" );
    }

}