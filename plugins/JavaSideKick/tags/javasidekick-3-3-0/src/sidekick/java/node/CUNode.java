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

import sidekick.util.Range;

/**
 * An extension of TigerNode for a compilation unit.  A compilation unit should
 * be the top-level unit for parsing, all other productions represented by TigerNodes
 * should be children of a CUNode.
 */

public class CUNode extends TigerNode {

    private TigerNode packageNode = null;
    private ImportNode importNode = null;
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

    public void setPackage( TigerNode packageNode ) {
        this.packageNode = packageNode;
    }

    public TigerNode getPackage() {
        return packageNode;
    }

    public int getOrdinal() {
        return TigerNode.COMPILATION_UNIT;
    }

    /**
     * Used once by completion finder.
     * TODO: change completion finder to use one of the other methods?
     * @return List<String> of import names, This will be an empty list if there
     * are no import statements.
     */
    public List<String> getImportNames() {
        List<String> list = new ArrayList<String>();
        if ( importNode == null ) {
            return list;
        }
        for ( TigerNode in : importNode.getChildren() ) {
            list.add( in.getName() );
        }
        Collections.sort( list );
        return list;
    }
    
    /**
     * @return A list sorted by name of the import statement nodes as stored in the
     * node returned by <code>getImmportNode</code>. This will be an empty
     * list if there are no import statements.
     */
    public List<TigerNode> getImportNodes() {
        if ( importNode == null ) {
            return new ArrayList<TigerNode>();
        }
        ArrayList<TigerNode> imports = new ArrayList<TigerNode>( importNode.getChildren() );
        Collections.sort( imports, new Comparator<TigerNode>() {
            public int compare( TigerNode a, TigerNode b ) {
                return a.getName().compareTo( b.getName() );
            }
        }
        );
        return imports;
    }

    /**
     * @return A range representing the entire span of the import statements or null
     * if there are no import statements.
     */
    public Range getImportsRange() {
        if ( importNode == null ) {
            return null;
        }
        return new Range( importNode.getStartLocation(), importNode.getEndLocation() );
    }
    
    /**
     * @return the top level import node, this node contains all the actual import statement nodes.    
     */
    public ImportNode getImportNode() {
        return importNode;   
    }

    public void setImportNode( ImportNode in ) {
        importNode = in;
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