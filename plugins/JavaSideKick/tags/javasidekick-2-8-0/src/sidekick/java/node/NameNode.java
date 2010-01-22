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
 * Represents some sort of named object in the sense of JLS 6.5.6.  This could
 * represent a package name in an import statement, e.g. the 'java.util.*' part
 * of 'import java.util.*;' or a static method in a class, e.g. 'GUIUtilities.centerOnScreen'.
 * To help differentiate these names in the proper context, everything from the
 * start of the name to the last '.' is used as the Type of this node, everything
 * after the last '.' is the name of this node.  The dot is lost.  Note that the
 * Type may be null, but the name should never be null.
 */
public class NameNode extends TigerNode {

    public NameNode() {

    }

    public NameNode(String name, Type type) {
        setName(name);
        setType(type);
    }

    public String getFullyQualifiedTypeName() {
        String type = getType();
        String rtn = getName();
        if (type != null && type.length() > 0) {
            rtn = type + "." + rtn;
        }
        return rtn;
    }

    public String toString() {
        return getFullyQualifiedTypeName();
    }

}

