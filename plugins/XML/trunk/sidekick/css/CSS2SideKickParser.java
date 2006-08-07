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
package sidekick.css;

import java.io.StringReader;
import java.util.*;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import errorlist.DefaultErrorSource;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.PropertiesChanged;

import sidekick.*;
import sidekick.enhanced.*; 
import sidekick.util.*;

import sidekick.css.parser.CSSNode;
import sidekick.css.parser.CSS2Parser;

/**
 * @author    Dale Anson
 * @version   $Revision$
 */
public class CSS2SideKickParser extends SideKickParser {

    private View currentView = null;

    public static boolean showAll = true;
    private int lineOffset = 0;

    public CSS2SideKickParser() {
        super("css");
    }
    
	/**
	 * If called by another parser to parse part of a file (for example, to parse
	 * a style tag in an html document), this can be set to the offset of the
	 * style tag so that the node locations can be set correctly.
	 */
    public void setLineOffset( int offset ) {
        if (offset > 0) {
            lineOffset = offset;   
        }
    }
    
    public void parse() {
        if (currentView != null) {
            parse(currentView.getBuffer(), null);
        }
    }

    /**
     * Parse the contents of the given buffer.
     *
     * @param buffer       the buffer to parse
     * @param errorSource
     * @return             Description of the Returned Value
     */
    public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
        return parse(buffer, buffer.getText(0, buffer.getLength()), errorSource);
    }
    
    public SideKickParsedData parse(Buffer buffer, String text, DefaultErrorSource errorSource) {

        String filename = buffer.getPath();
        SideKickParsedData parsedData = new SideKickParsedData(buffer.getName());
        DefaultMutableTreeNode root = parsedData.root;

        StringReader reader = new StringReader(text);
        try {
            // parse
            CSS2Parser parser = new CSS2Parser(reader);
            parser.setLineOffset(lineOffset);
            CSSNode ss = parser.styleSheet();
            
            // make a tree
            addTreeNodes(root, ss);
            
            /*
             * need to convert the CSSNodes that
             * are currently the user objects in the tree nodes to
             * SideKick Assets
             */
            convert(buffer, root);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            reader.close();
        }
        return parsedData;
    }
    
    private void addTreeNodes(DefaultMutableTreeNode root, CSSNode ss) {
        if (ss.hasChildren()) {
            for (Iterator it = ss.getChildren().iterator(); it.hasNext(); ) {
                CSSNode cssChild = (CSSNode)it.next();
                if (cssChild != null) {
                    DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode(cssChild);
                    root.add(dmtNode);
                    addTreeNodeChildren(dmtNode, cssChild);
                }
            }
        }
    }

    private void addTreeNodeChildren(DefaultMutableTreeNode dmtNode, CSSNode cssNode) {
        if (cssNode.hasChildren()) {
            for (Iterator it = cssNode.getChildren().iterator(); it.hasNext(); ) {
                CSSNode cssChild = (CSSNode)it.next();
                if (cssChild != null) {
                    DefaultMutableTreeNode dmtChild = new DefaultMutableTreeNode(cssChild);
                    dmtNode.add(dmtChild);
                    addTreeNodeChildren(dmtChild, cssChild);
                }
            }
        }
    }
    
    /**
     * Description of the Method
     *
     * @param buffer
     * @param node
     */
    private void convert(Buffer buffer, DefaultMutableTreeNode node) {
        // convert the children of the node
        Enumeration children = node.children();
        while (children.hasMoreElements()) {
            convert(buffer, (DefaultMutableTreeNode) children.nextElement());
        }

        // convert the node itself
        if (!(node.getUserObject() instanceof IAsset)) {
            SideKickElement userObject = (SideKickElement)node.getUserObject();
            Position start_position = ElementUtil.createStartPosition(buffer, userObject);
            Position end_position = ElementUtil.createEndPosition(buffer, userObject);
            SideKickAsset asset = new SideKickAsset(userObject);
            asset.setStart(start_position);
            asset.setEnd(end_position);
            node.setUserObject(asset);
        }
    }

	public boolean supportsCompletion() {
		return true;
	}
	
	public boolean canCompleteAnywhere() {
		return true;
	} 
	
	public SideKickCompletion complete(EditPane editPane, int caret) {
		CompletionRequest cr = new CompletionRequest(editPane, caret);
		return cr.getSideKickCompletion();
	}

}

