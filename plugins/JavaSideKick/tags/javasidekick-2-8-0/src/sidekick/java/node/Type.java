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

// represents a type, such as "int" or "Object", also includes generic type
// arguments and class extends and implements types.
public class Type extends TigerNode {
    
    // is the type a primitive, e.g. "int", "short", etc.
    public boolean isPrimitive = false;
    
    public boolean isVoid = false;
    
    // is the type an array, e.g. "int[]"
    public boolean isArray = false;
    
    // number of dimensions if it is an array 
    public int arrayDimensions = 0;
    
    // name of the type, e.g. "int", "String", etc
    public String type = "";
    
    // generic type parameters, e.g. the "<String>" in "List<String>"
    public String typeArgs = "";
    
    // to support WildcardBounds, qualifier will be either "extends" or "super"
    public String qualifier = "";
    
    public Type() {
        
    }
    
    public Type(Type t) {
        if (t != null) {
            type = t.getType();
            typeArgs = t.getTypeParams();
            setStartLocation(t.getStartLocation());
            setEndLocation(t.getEndLocation());
        }
    }
    
    public int getOrdinal() {
        return TigerNode.TYPE;   
    }
    
    public String getName() {
        return getType();   
    }
    
    public String getType() {
        return type == null ? "" : type + dimString();
    }
    
    public String getTypeParams() {
        return typeArgs == null ? "" : typeArgs;   
    }
    
    public void setIsArray(boolean b) {
        isArray = b;
    }
    
    public boolean isArray() {
        return isArray;
    }
    
    public void setDimensions(int i) {
        arrayDimensions = i;  
    }
    
    public int getDimensions() {
        return arrayDimensions;   
    }
    
    private String dimString() {
        String dims = "";
        for (int i = 0; i < arrayDimensions; i++) {
            dims += "[]";
        }
        return dims;
    }
    
    /**
     * Overridden to return false.    
     */
    public boolean canAdd( TigerNode node ) {
        return true;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (qualifier != null && qualifier.length() > 0) {
            sb.append(qualifier).append(" ");   
        }
        sb.append(type).append(dimString()).append(typeArgs);
        return sb.toString();
    }
}

