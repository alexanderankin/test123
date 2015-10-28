package xml;


import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.View;

import sidekick.ExpansionModel;
import xml.parser.RenamedXmlTag;

/**
 * Special version of XmlParsedData for Ant build files.
 * nodes are not renamed here, contrary to MvnXmlParsedData, because it can be done
 * easily in AntXmlTag.
 */
public class AntXmlParsedData extends XmlParsedData {

    public AntXmlParsedData(String filename, boolean html) {
        super(filename, html);
    }

    // Overridden so name and type sorting work correctly for these files.
    // "name" is the original name of the xml tag, like "artifactId".
    // "short string" is the name provided by the child element named "name". So sort
    // by name uses "short string" and sort by type uses "name".
    @Override
    protected Comparator<DefaultMutableTreeNode> getSorter() {
        return new Comparator<DefaultMutableTreeNode>() {
            public int compare(DefaultMutableTreeNode tna, DefaultMutableTreeNode tnb) {
                int sortBy = getSortBy();
                switch (sortBy) {                // NOPMD, no breaks are necessary here
                    case SORT_BY_LINE:
                        Integer my_line = new Integer(((RenamedXmlTag)tna.getUserObject()).getStart().getOffset());
                        Integer other_line = new Integer(((RenamedXmlTag)tnb.getUserObject()).getStart().getOffset());
                        return my_line.compareTo(other_line) * (sortDown ? 1 : -1);
                    case SORT_BY_TYPE:
                        String my_on = ((RenamedXmlTag)tna.getUserObject()).getName().toLowerCase();
                        String other_on = ((RenamedXmlTag)tnb.getUserObject()).getName().toLowerCase();
                        return my_on.compareTo(other_on) * (sortDown ? 1 : -1);
                    case SORT_BY_NAME:
                    default:
                        return compareNames(tna, tnb);
                }
            }
    
            private int compareNames(DefaultMutableTreeNode tna, DefaultMutableTreeNode tnb) {
                // sort by name
                String my_name = ((RenamedXmlTag)tna.getUserObject()).getShortString().toLowerCase();
                String other_name = ((RenamedXmlTag)tnb.getUserObject()).getShortString().toLowerCase();
                return my_name.compareTo(other_name) * (sortDown ? 1 : -1);
            }
        } ;
    }

    @Override
    // TODO: remove? Currently, this is the same as the super method. If removed,
    // this entire class can be removed.
    protected ExpansionModel createExpansionModel() {
        ExpansionModel em = new ExpansionModel();
        em.add();   // root (filename node)
        em.add();   // project node
        for (int i = 0; i < root.getChildAt(0).getChildCount(); i++) {
            em.inc();   // property, target, etc, nodes   
        }
        return em;
    }
}