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
 * Accumulates the count of specific node types, that is, this keeps a count
 * of the number of classed, interfaces, methods, and fields that the parser
 * found.
 */
public class Results {
    // Parse Counters
    private int classCount = 0;
    private int interfaceCount = 0;
    private int methodCount = 0;
    private int referenceFieldCount = 0;    // count of fields that are some sort of Object, not a primitive
    private int primitiveFieldCount = 0;    // count of fields that are some sort of primitive, i.e. int, char, etc.

    // Accessor Methods
    public int getClassCount() {
        return classCount;
    }
    public int getInterfaceCount() {
        return interfaceCount;
    }
    public int getMethodCount() {
        return methodCount;
    }
    public int getReferenceFieldCount() {
        return referenceFieldCount;
    }
    public int getPrimitiveFieldCount() {
        return primitiveFieldCount;
    }


    // Increment
    public void incClassCount() {
        classCount++;
    }


    public void incInterfaceCount() {
        interfaceCount++;
    }


    /**
     * Used to count methods        
     */
    public void incMethodCount() {
        methodCount++;
    }


    /**
     * Used to count fields that are Objects, for example, <code>public static String DAY = "day";</code>        
     */
    public void incReferenceFieldCount() {
        referenceFieldCount++;
    }


    /**
     * Used to count fields that are primitives, for example, <code>private static int x = 2;</code>        
     */
    public void incPrimitiveFieldCount() {
        primitiveFieldCount++;
    }


    /**
     * This method resets all the result variables to their initial state,
     * i.e. all counts to 0, in anticipation of performing a new parse
     * which will use the result object to count what it finds.
     */
    void reset() {
        classCount = 0;
        interfaceCount = 0;
        methodCount = 0;
        referenceFieldCount = 0;
        primitiveFieldCount = 0;
    }


    public String toString() {
        return " Classes: " + classCount + ", Interfaces: " + interfaceCount + ", Methods: " + methodCount + ", Fields: " + ( referenceFieldCount + primitiveFieldCount );
    }


}