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


// an extension of TigerNode for a Class
public class ClassNode extends TigerNode {

    private String typeParams = null;

    public ClassNode( String name, int modifiers ) {
        super( name, modifiers );
    }

    public int getOrdinal() {
        return CLASS;
    }

    public void setTypeParams( String p ) {
        typeParams = p;
    }

    public String getTypeParams() {
        return typeParams == null ? "" : typeParams;
    }


    public void setExtendsList( List list ) {
        if ( list == null )
            return ;
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            Type t = ( Type ) it.next();
            addChild( new ExtendsNode( t ) );
        }
    }

    public void setImplementsList( List list ) {
        if ( list == null )
            return ;
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            Type t = ( Type ) it.next();
            addChild( new ImplementsNode( t ) );
        }
    }
    
    public boolean isInnerClass() {
        TigerNode parent = getParent();
		return (parent != null && parent.getOrdinal() == TigerNode.CLASS);
    }
    

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( super.toString() );
        if ( getTypeParams() != null )
            sb.append( getTypeParams() );

        StringBuffer extendsList = new StringBuffer();
        StringBuffer implementsList = new StringBuffer();
        List children = getChildren();
        if ( children != null ) {
            for ( Iterator it = children.iterator(); it.hasNext(); ) {
                TigerNode child = ( TigerNode ) it.next();
                if ( child instanceof ExtendsNode )
                    extendsList.append( child.getName() );
                else if ( child instanceof ImplementsNode ) {
                    if ( implementsList.length() > 0 )
                        implementsList.append( ", " );
                    implementsList.append( child.getName() );
                }
            }
        }
        if ( extendsList.length() > 0 )
            sb.append( " extends " ).append( extendsList.toString() );
        if ( implementsList.length() > 0 )
            sb.append( " implements " ).append( implementsList.toString() );
        return sb.toString();
    }
}


