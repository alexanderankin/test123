/*
Copyright (c) 2022, Dale Anson
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

// an extension of TigerNode for a Record
public class RecordNode extends TigerNode implements Parameterizable {
    String typeParams = null;
    List<Parameter> params = null;

    public RecordNode() { 
    }

    public RecordNode( String name, int modifiers ) {
        super( name, modifiers );
    }

    public int getOrdinal() {
        return RECORD;
    }

    // list is a list of Types
    public void setImplementsList( List<TigerNode> list ) {
        if ( list == null ) {
            return;
        }
        for ( Iterator<TigerNode> it = list.iterator(); it.hasNext(); ) {
            TigerNode t = it.next();
            addChild( new ImplementsNode( t.getName() ) );
        }
    }
    
    public void addParameter( Parameter p ) {
        if ( params == null ) {
            params = new ArrayList<Parameter>();
        }
        params.add( p );
    }

    public void setParameters( List<Parameter> p ) {
        params = p;
    }

    public List<Parameter> getParameters() {
        return params;
    }

    /**
     * Returns a string showing the formal parameters for this method.  The
     * returned string is a comma separated list of parameter types, if
     * <code>withNames</code> is true, then the returned string is a comma
     * separated list of type:name.
     * <p>
     * Example: method is "void getX(int a, int b)",
     * <code>withNames</code> is false, returned string is "int,int".
     * <p>
     * Example: method is "void getX(int a, int b)",
     * <code>withNames</code> is true, returned string is "int a,int b".
     * @param withNames should returned string include the formal parameter names
     * @param typeAsSuffix if true and if withNames is true, name and type will
     * be reversed, e.g. method is "void getX(int a, int b), returned string is
     * "a : int, b : int"
     * @param includeFinal if true, include any "final" modifier, e.g. method is
     * "void getX(int a, final int b)", returned string is "int, final int", or
     * if withNames is true, "int a, final int b", and if typeAsSuffix is true,
     * "a : int, b : final int"
     * @return parameters as string, see above
     */
    public String getParams( boolean withNames, boolean typeAsSuffix, boolean includeTypeArgs ) {

        if ( params == null || params.size() == 0 ) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for ( Iterator it = params.iterator(); it.hasNext(); ) {
            Parameter param = ( Parameter ) it.next();
            if ( typeAsSuffix ) {
                sb.append( param.getType() );
                if ( includeTypeArgs && param.getType() != null ) {
                    sb.append( param.getRealType().typeArgs );
                }
                if ( withNames ) {
                    sb.append( " : " ).append( param.getType() );
                }
            } else {
                if ( withNames ) {
                    sb.append( param.getName() ).append( " : " );
                }
                sb.append( param.getType() );
                    if ( includeTypeArgs ) {
                    sb.append( param.getTypeParams() );
                }
            }
            if ( it.hasNext() ) {
                sb.append( ", " );
            }
        }
        return sb.toString();
    }

    public void setTypeParams( String p ) {
        typeParams = p;
    }
    public String getTypeParams() {
        return typeParams == null ? "" : typeParams;
    }


    /**
     */
    public boolean canAdd( TigerNode node ) {
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( super.toString() );
        sb.append( '(' ).append( getParams( true, true, true ) ).append( ')' );     // NOPMD sometimes PMD doesn't know what it's doing
        sb.append( ": <init>" );
        return sb.toString();
    }
}

