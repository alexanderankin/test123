/*
Copyright (c) 2002, Dale Anson
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

package ise.plugin.nav;

import java.util.Stack;

/**
 * Stack that can have a maximum size.  Another property of this stack is that
 * while it will allow duplicates, it won't allow them consecutively.
 */
class NavStack<E> extends Stack<E> {

    private int maxSize = 512;

    /**
     * Create a NavStack with a default size of 512.    
     */
    public NavStack() {
        super();
    }

    /**
     * Create a NavStack with a specified size.
     * @param size The maximum number of items this stack may contain.
     */
    public NavStack( int size ) {
        super();
        maxSize = size;
    }

    /**
     * Sets the maximum size of this stack.  If the new size is smaller than the
     * previous size, removes the oldest items until the stack is this size.
     * @param size The new maximum size of this stack.
     */

    public void setMaxSize( int size ) {
        maxSize = size;
        while ( size() > maxSize ) {
            remove( 0 );
        }
    }

    /**
     * @return The maximum number of items this stack may contain.    
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Push an item on to the stack.  Null items are not added.      
     */
    @Override
    public E push( E item ) {
        // do not allow nulls
        if ( item == null ) {
            return null;
        }

        // do not allow same item twice in a row
        if ( size() > 0 && item.equals( peek() ) ) {
            return null;
        }

        // okay to add item to stack
        super.push( item );

        // remove oldest item if stack is now larger than max size
        if ( size() > maxSize ) {
            remove( 0 );
        }
        return item;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "NavStack[\n" );
        for ( Object o : NavStack.this ) {
            sb.append( o.toString() ).append( ",\n" );
        }
        sb.append( ']' );
        return sb.toString();
    }
}