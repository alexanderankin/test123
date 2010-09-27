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

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Stack that can have a maximum size.  Another property of this stack is that
 * while it will allow duplicates, it won't allow them consecutively.
 */
class NavStack<E> extends Stack<E> implements ComboBoxModel {

    private int maxSize = 512;
    
    private Set<ListDataListener> listeners = null;
    
    private Object selectedItem = null;

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
        contentsChanged();
        return item;
    }
    
    @Override
    public E pop() {
        E e = super.pop();
        contentsChanged();
        return e;
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
    
    public void addListDataListener(ListDataListener listener) {
        if (listeners == null) {
            listeners = new HashSet<ListDataListener>();   
        }
        listeners.add(listener);
    }
    
    public void removeListDataListener(ListDataListener listener) {
        if (listeners == null) {
            return;   
        }
        listeners.remove(listener);
    }
    
    public Object getElementAt(int index) {
        return get(index);    
    }
    
    private void contentsChanged() {
        if (listeners == null) {
            return;
        }   
        ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize());
        for (ListDataListener listener : listeners) {
            listener.contentsChanged(event);   
        }
    }
    
    public int getSize() {
        return size();   
    }
    
    public boolean add(E e) {
        boolean b = super.add(e);
        if (b) contentsChanged();
        return b;
    }
    
    public void add(int index, E e) {
        super.add(index, e);
        contentsChanged();
    }
    
    public boolean addAll(Collection<? extends E> c) {
        boolean b = super.addAll(c);
        if (b) contentsChanged();
        return b;
    }
    
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean b = super.addAll(index, c);
        if (b) contentsChanged();
        return b;
    }
    
    public void addElement(E e) {
        super.addElement(e);
        contentsChanged();
    }
    
    public void clear() {
        super.clear();
        contentsChanged();
    }
    
    public void insertElementAt(E e, int index) {
        super.insertElementAt(e, index);
        contentsChanged();
    }
    
    public E remove(int index) {
        E e = super.remove(index);
        contentsChanged();
        return e;
    }
    
    public boolean remove(Object o) {
        boolean b = super.remove(o);
        if (b) contentsChanged();
        return b;
    }
    
    public boolean removeAll(Collection<?> c) {
        boolean b = super.removeAll(c);
        if (b) contentsChanged();
        return b;
    }
    
    public void removeAllElements() {
        super.removeAllElements();
        contentsChanged();
    }
    
    public boolean removeElement(Object obj) {
        boolean b = super.removeElement(obj);
        if (b) contentsChanged();
        return b;
    }
    
    public void removeElementAt(int index) {
        super.removeElementAt(index);
        contentsChanged();
    }
    
    protected void removeRange(int start, int end) {
        super.removeRange(start, end);
        contentsChanged();
    }
    
    public E set(int index, E e) {
        E old = super.set(index, e);
        contentsChanged();
        return old;
    }
    
    public void setElementAt(E e, int index) {
        super.setElementAt(e, index);
        contentsChanged();
    }
    
    public void setSize(int newSize) {
        super.setSize(newSize);
        contentsChanged();
    }
    
    // for ComboBoxModel
    public Object getSelectedItem() {
        return selectedItem;   
    }
    
    public void setSelectedItem(Object item) {
        selectedItem = item;
        contentsChanged();
    }
}