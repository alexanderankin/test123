/*
Copyright (c) 2006, Dale Anson
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

package sidekick.html.parser.html;

import java.util.*;
import javax.swing.tree.*;

/**
 * danson: A class to build a tree of TreeNodes out of an HTML document.
 */
public class HtmlTreeBuilder extends HtmlVisitor {

    private DefaultMutableTreeNode root = null;

    private Stack stack = new Stack();
    private DefaultMutableTreeNode currentNode = null;

    private boolean showAll = true;

    public HtmlTreeBuilder( DefaultMutableTreeNode root ) {
        this.root = root;
        currentNode = root;
    }

    public void setShowAll( boolean b ) {
        showAll = b;
    }

    public void visit( HtmlDocument.Tag t ) {
        if ( showAll ) {
            if ( !currentNode.getUserObject().equals( t ) && t.toString() != null ) {
                currentNode.add( new DefaultMutableTreeNode( t ) );
            }
        }
    }

    public void visit( HtmlDocument.EndTag t ) {}

    public void visit( HtmlDocument.Comment c ) {}

    public void visit( HtmlDocument.JspComment c ) {}

    public void visit( HtmlDocument.Text t ) {}

    public void visit( HtmlDocument.Newline n ) {}

    public void visit( HtmlDocument.Annotation a ) {}

    public void visit( HtmlDocument.TagBlock bl ) {
        if ( bl != null && bl.toString() != null ) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode( bl );
            currentNode.add( childNode );
            stack.push( currentNode );
            currentNode = childNode;
            //bl.startTag.accept( this );       // don't visit start tags, doing so causes duplicate nodes
            visit( bl.body );
            //bl.endTag.accept( this );         // don't visit end tags, there is no need
            currentNode = ( DefaultMutableTreeNode ) stack.pop();
        }
    }

    public void visit( HtmlDocument.ElementSequence s ) {
        if (s == null)
            return;
        for ( Iterator iterator = s.iterator(); iterator.hasNext(); ) {
            HtmlDocument.HtmlElement htmlElement = ( HtmlDocument.HtmlElement ) iterator.next();
            htmlElement.accept( this );
        }
    }

    public void visit( HtmlDocument d ) {
        if (d == null)
            return;
        start();
        visit( d.elements );
        finish();
    }

    public void start() {}

    public void finish() {}
}
