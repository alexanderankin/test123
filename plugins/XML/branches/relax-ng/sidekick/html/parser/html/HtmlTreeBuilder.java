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
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import errorlist.DefaultErrorSource;
import sidekick.css.CSS2SideKickParser;
import sidekick.ecmascript.EcmaScriptSideKickParser;
import sidekick.javascript.JavaScriptParser;
import sidekick.SideKickParsedData;
import sidekick.enhanced.PartialParser;

/**
 * danson: A class to build a tree of TreeNodes out of an HTML document.
 */
public class HtmlTreeBuilder extends HtmlVisitor {

    private DefaultMutableTreeNode root = null;

    private Stack stack = new Stack();
    private DefaultMutableTreeNode currentNode = null;
    private Buffer buffer = null;
    private DefaultErrorSource errorSource = null;
    private boolean showAll = true;

    public HtmlTreeBuilder( DefaultMutableTreeNode root ) {
        this.root = root;
        currentNode = root;
    }

    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void setErrorSource(DefaultErrorSource errorSource) {
        this.errorSource = errorSource;
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

            // special handling for <style> tags, pass contents to css parser to get the child nodes
            if (bl.startTag.tagName.equalsIgnoreCase("style") && buffer != null) {
                // style tag stores its complete contents in a single text tag.
                String text = null;
                if (bl.body.getElementAt(0) != null) {
                    text = bl.body.getElementAt(0).toString();
                }
                if (text != null) {
                    // send the style content to the css parser.  The css parser
                    // will return a single node named "style" with 0 or more children.
                    // I don't want the top node, I do want the children. so...
                    // create the parser
                    CSS2SideKickParser cssparser = new CSS2SideKickParser();
                    // set the line offset to the line number of the style block so
                    // the location gets set correctly on the child nodes
                    cssparser.setLineOffset(bl.getStartLocation().line);
                    // actually do the parse
                    SideKickParsedData data = cssparser.parse(buffer, text, errorSource);
                    // copy a reference to the child nodes to a list
                    List<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
                    // remove the child nodes from their current parent
                    for (int i = 0; i < data.root.getChildCount(); i++) {
                        children.add((DefaultMutableTreeNode)data.root.getChildAt(i));
                    }
                    // add them to our current parent
                    for (DefaultMutableTreeNode child : children) {
                        data.root.remove(child);
                        currentNode.add(child);
                    }
                }
            }
            // special handling for <script> tags, pass contents to ecmascript parser to get the child nodes
            /// need to do more than just check for the 'script' tag, need to check that the type is javascript
            else if (bl.startTag.tagName.equalsIgnoreCase("script") && buffer != null) {
                // script tag stores its complete contents in a single text tag.
                String text = null;
                if (bl.body.getElementAt(0) != null) {
                    text = bl.body.getElementAt(0).toString();
                }
                if (text != null) {
                    // if the user has selected the ecmascript parser to parse javascript,
                    // parse it in-line.  If they've selected the javascript parser, then
                    // do  nothing with in-line javascript.
                    String parser_name = jEdit.getProperty("mode.javascript.sidekick.parser");
                    if ("ecmascript".equals(parser_name) || "javascript".equals(parser_name)) {
                        // send the script content to the ecmascript parser.  The ecmascript parser
                        // will return a single node named "script" with 0 or more children.
                        // I don't want the top node, I do want the children. so...
                        // create the parser
                        PartialParser scriptparser = "ecmascript".equals(parser_name) ? new EcmaScriptSideKickParser() : new JavaScriptParser();
                        // set the line offset to the line number of the script block so
                        // the location gets set correctly on the child nodes
                        scriptparser.setStartLine(bl.getStartLocation().line);
                        // actually do the parse
                        SideKickParsedData data = scriptparser.parse(buffer, text, errorSource);
                        // copy a reference to the child nodes to a list
                        List<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
                        for (int i = 0; i < data.root.getChildCount(); i++) {
                            children.add((DefaultMutableTreeNode)data.root.getChildAt(i));
                        }
                        // remove the child nodes from their current parent
                        // add them to our current parent
                        for (DefaultMutableTreeNode child : children) {
                            data.root.remove(child);
                            currentNode.add(child);
                        }
                    }
                }
            }
            else {
                //bl.startTag.accept( this );       // don't visit start tags, doing so causes duplicate nodes
                visit( bl.body );
                //bl.endTag.accept( this );         // don't visit end tags, there is no need
            }
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
