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

import sidekick.util.Location;
import sidekick.util.Range;

/**
 * An extension of TigerNode for a compilation unit.  A compilation unit should
 * be the top-level unit for parsing, all other productions represented by TigerNodes
 * should be children of a CUNode.
 */

public class CUNode extends TigerNode {

    private String packageName = "";
    private TigerNode packageNode = null;
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

    @Deprecated
    public void setPackageName( String name ) {
        packageName = name;
    }

    @Deprecated
    public String getPackageName() {
        return packageNode == null ? "" : packageNode.toString();
    }
    
    public void setPackage(TigerNode packageNode) {
        this.packageNode = packageNode;   
    }

    public TigerNode getPackage() {
        return packageNode;   
    }
    
    public int getOrdinal() {
        return TigerNode.COMPILATION_UNIT;
    }

    public void addImport( ImportNode in ) {
        if ( in == null ) {
            return;
        }
        if ( imports == null ) {
            imports = new ArrayList<ImportNode>();
        }
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
            return new ArrayList<ImportNode>( imports );
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

    /**
     * @return A range representing the entire span of the import statements or null
     * if there are no import statements.
     */
    public Range getImportsRange() {
        if ( imports == null ) {
            return null;
        }

        switch ( imports.size() ) {
            case 0:
                return null;
            case 1:
                ImportNode in = imports.get( 0 );
                return new Range( in.getStartLocation(), in.getEndLocation() );
            default:
                // there are at least 2 import nodes
                ImportNode node = imports.get( 0 );
                Range range = new Range( node.getStartLocation(), node.getEndLocation() );
                for ( int i = 1; i < imports.size(); i++ ) {
                    node = imports.get( i );
                    if ( node.getStartLocation().compareTo( range.getStartLocation() ) < 0 ) {
                        range.setStartLocation( node.getStartLocation() );
                    }
                    if ( node.getEndLocation().compareTo( range.getEndLocation() ) > 0 ) {
                        range.setEndLocation( node.getEndLocation() );
                    }
                }
                return range;
        }

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