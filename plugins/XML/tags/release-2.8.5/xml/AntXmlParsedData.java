package xml;


import sidekick.ExpansionModel;

/**
 * Special version of XmlParsedData for Ant build files.
 */
public class AntXmlParsedData extends XmlParsedData {

    public AntXmlParsedData(String filename, boolean html) {
        super(filename, html);
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