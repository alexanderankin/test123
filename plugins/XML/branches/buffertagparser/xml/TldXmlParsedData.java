package xml;

import java.util.Comparator;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import sidekick.Asset;
import sidekick.IAsset;
import sidekick.ExpansionModel;
import xml.parser.TldXmlTag;

import org.gjt.sp.jedit.View;

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

    /**
     * Rename the assets contained in the tree nodes to use the value
     * contained in the body of any child tags named 'name'.
     */ 
    @Override
    public void done(View view) {
        Enumeration children = root.children();
        while (children.hasMoreElements()) {
            processChild((TreeNode) children.nextElement());
        }
        tree.reload();
        sort(view);
    }

    private void processChild(TreeNode node) {
        IAsset asset = (IAsset) ((DefaultMutableTreeNode) node).getUserObject();
        if (asset.getName().indexOf(":") == - 1) {
            renameAsset(node, asset);
            Enumeration children = node.children();
            while (children.hasMoreElements()) {
                processChild((TreeNode) children.nextElement());
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
                TldXmlTag uo = (TldXmlTag) child.getUserObject();
                if ("name".equals(uo.getName())) {
                    ((Asset) asset).setName(uo.getCharacters());
                    ((DefaultMutableTreeNode) node).setUserObject(asset);
                }
            }
        }
    }

    // Overridden so name and type sorting work correctly for these files.
    // "original name" is the original name of the xml tag, like "tag-file" or "function".
    // "short string" is the name provided by the child element named "name". So sort
    // by name uses "short string" and sort by type uses "original name".
    @Override
    protected Comparator<DefaultMutableTreeNode> getSorter() {
        return new Comparator<DefaultMutableTreeNode>() {
            public int compare(DefaultMutableTreeNode tna, DefaultMutableTreeNode tnb) {
                int sortBy = getSortBy();
                switch (sortBy) {                // NOPMD, no breaks are necessary here
                    case SORT_BY_LINE:
                        Integer my_line = new Integer(((TldXmlTag)tna.getUserObject()).getStart().getOffset());
                        Integer other_line = new Integer(((TldXmlTag)tnb.getUserObject()).getStart().getOffset());
                        return my_line.compareTo(other_line) * (sortDown ? 1 : -1);
                    case SORT_BY_TYPE:
                        String my_on = ((TldXmlTag)tna.getUserObject()).getOriginalName().toLowerCase();
                        String other_on = ((TldXmlTag)tnb.getUserObject()).getOriginalName().toLowerCase();
                        return my_on.compareTo(other_on) * (sortDown ? 1 : -1);
                    case SORT_BY_NAME:
                    default:
                        return compareNames(tna, tnb);
                }
            }
    
            private int compareNames(DefaultMutableTreeNode tna, DefaultMutableTreeNode tnb) {
                // sort by name
                String my_name = ((TldXmlTag)tna.getUserObject()).getShortString().toLowerCase();
                String other_name = ((TldXmlTag)tnb.getUserObject()).getShortString().toLowerCase();
                return my_name.compareTo(other_name) * (sortDown ? 1 : -1);
            }
        } ;
    }
    
    @Override
    protected ExpansionModel createExpansionModel() {
        ExpansionModel em = new ExpansionModel();
        em.add();   // root (filename node)
        em.add();   // main node
        for (int i = 0; i < root.getChildAt(0).getChildCount(); i++) {
            em.inc();   // tag, function, etc, nodes   
        }
        return em;
    }
}