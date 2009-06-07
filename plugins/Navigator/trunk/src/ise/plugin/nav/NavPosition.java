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


import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;

/**
 * This object is used to mark caret positions in jEdit buffers. Implements
 * comparable so lists of these can be merged and sorted.
 *
 * @author Dale Anson
 */
public class NavPosition implements Comparable {

    public EditPane editPane;
    public String path = null;
    public int caret = 0;
    public int lineno = 0;
    public String linetext = "";

    /**
     * @param view the View containing the editPane
     * @param editPane the EditPane containing the text area that is displaying the buffer
     * @param buffer the buffer
     * @param caretPosition the position of the caret within the text area containing the buffer
     * @param the text of the caret line
     */
    public NavPosition( EditPane editPane, Buffer buffer, int caretPosition, String linetext ) {
        if ( editPane == null ) {
            throw new IllegalArgumentException( "editPane cannot be null" );
        }
        if ( buffer == null ) {
            throw new IllegalArgumentException( "buffer cannot be null" );
        }
        if ( caretPosition < 0 ) {
            throw new IllegalArgumentException( "caret position cannot less than 0" );
        }
        this.editPane = editPane;
        path = buffer.getPath();
        caret = caretPosition;
        lineno = buffer.getLineOfOffset( caret );
        this.linetext = linetext == null ? "" : linetext;
    }

    public boolean equals( NavPosition other ) {
        // 2 nav positions are equal to each other if they have the same
        // path and caret position
        if ( other == null ) {
            return false;
        }
        return ( path.equals( other.path ) && caret == other.caret );
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public int compareTo( Object other ) {
        if ( other == null ) {
            return 1;
        }
        return toString().compareTo( other.toString() );
    }

    @Override
    public String toString() {
        return path + ":" + ( lineno + 1 );
    }

    /**
     * @return an HTML representation of this position for display in the
     * back and forward popup lists.
     */
    public String toHtml() {
        // might need to escape the line text as it might already be html
        String text = linetext;
        text = text.replaceAll("[<]", "&lt;");
        text = text.replaceAll("[>]", "&gt;");
        return "<html><tt>" + path + ":" + ( lineno + 1 ) + "</tt><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + text;
    }
}