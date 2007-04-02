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


/**
 * An extension of TigerNode to represent a field.  This may be either a
 * primitive field or a reference field.
 * Note: in UML, this is known as an "attribute".  I avoid that usage in
 * TigerBrowser and use "field" as this is the term used throughout the
 * JVM Specification.
 */
public class FieldNode extends TigerNode {


    Type type = null;
    boolean isFinal = false;

    public FieldNode() {
    }

    public FieldNode( String name, int modifiers, Type type ) {
        super( name, modifiers );
        this.type = type;
    }

    /**
     * @returns true if this field represents a primitive type.
     */
    public boolean isPrimitive() {
        return type == null ? false : type.isPrimitive;
    }

    public void setFinal( boolean b ) {
        isFinal = b;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public String getType() {
        return type == null ? "" : type.getType();
    }

    public String getTypeParams() {
        return type == null ? "" : type.getTypeParams();
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( super.toString() );
        if ( type != null ) {
            sb.append( ": " ).append( type.toString() );
        }
        return sb.toString();
    }


    public int getOrdinal() {
        return FIELD;
    }

    /**
     * Overridden to return false, a field may not have children.
     * -- changed to true, need children for code completion.
     */
    public boolean canAdd( TigerNode node ) {
        return true;
    }
}
