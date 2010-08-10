package xml;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import sidekick.Asset;
import sidekick.IAsset;
import xml.parser.XmlTag;

/**
 * Special version of XmlParsedData for tld files.  Tld files have very few
 * tags with attributes, and the interesting things to see in the SideKick
 * tree are generally hidden from view, like the 'name' child tag of a 'tag'
 * or 'function' node,  This class renames those nodes with a child node
 * named 'name' so that the name is more readily visible in the tree.
 */
public class TldXmlParsedData extends XmlParsedData {

    public TldXmlParsedData(String filename, boolean html) {
        super(filename, html);
    }

    @Override
    /**
     * Rename the assets contained in the tree nodes to use the value
     * contained in the body of any child tags named 'name'.
     */
    public void done() {
        Enumeration children = root.children();
        while(children.hasMoreElements()) {
            processChild((TreeNode)children.nextElement());   
        }
        tree.reload();
    }
    
    private void processChild(TreeNode node) {
        IAsset asset = (IAsset)((DefaultMutableTreeNode)node).getUserObject();
        if (asset.getName().indexOf(":") == -1) {
            renameAsset(node, asset);
            Enumeration children = node.children();
            while(children.hasMoreElements()) {
                processChild((TreeNode)children.nextElement());   
            }
        }
    }
    
    /**
     * @param node It had better be a DefaultMutableTreeNode and it had better
     * contain an Asset as the user object.  At the time of this writing, this
     * was true.
     */
    private void renameAsset(final TreeNode node, IAsset asset) {
        if (asset instanceof Asset && node instanceof DefaultMutableTreeNode) {
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
                XmlTag uo = (XmlTag) child.getUserObject();
                if ("name".equals(uo.getName())) {
                    ((Asset) asset).setName(asset.getName() + ": " + uo.getCharacters());
                    ((DefaultMutableTreeNode) node).setUserObject(asset);
                }
            }
        }
    }
}