
package sidekick.html.parser.html;

import java.util.*;
import javax.swing.tree.*;

public  class HtmlTreeBuilder extends HtmlVisitor {
    
    private DefaultMutableTreeNode root = null;
    
    private Stack stack = new Stack();
    private DefaultMutableTreeNode currentNode = null;
    
    public HtmlTreeBuilder(DefaultMutableTreeNode root) {
        this.root = root;
        currentNode = root;
    }
    
    public void visit(HtmlDocument.Tag t) {
    }

    public void visit(HtmlDocument.EndTag t) {
    }

    public void visit(HtmlDocument.Comment c) {
    }

    public void visit(HtmlDocument.JspComment c) {
    }
    
    public void visit(HtmlDocument.Text t) {
    }

    public void visit(HtmlDocument.Newline n) {
    }

    public void visit(HtmlDocument.Annotation a) {
    }

    public void visit(HtmlDocument.TagBlock bl) {
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(bl);
        currentNode.add(childNode);
        stack.push(currentNode);
        currentNode = childNode;
        bl.startTag.accept(this);
        visit(bl.body);
        bl.endTag.accept(this);
        currentNode = (DefaultMutableTreeNode)stack.pop();
    }

    public void visit(HtmlDocument.ElementSequence s) {
        for (Iterator iterator = s.iterator(); iterator.hasNext();) {
            HtmlDocument.HtmlElement htmlElement = (HtmlDocument.HtmlElement) iterator.next();
            htmlElement.accept(this);
        }
    }

    public void visit(HtmlDocument d) {
        start();
        visit(d.elements);
        finish();
    }

    public void start() {
    }

    public void finish() {
    }
}

