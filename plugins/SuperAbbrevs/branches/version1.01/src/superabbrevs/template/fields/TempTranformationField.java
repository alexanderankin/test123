package superabbrevs.template.fields;

import superabbrevs.template.fields.visitor.TemplateFieldVisitor;

/**
 * @author Sune Simonsen
 * class TempTranformationField
 */
public class TempTranformationField implements Field {

    private String code;
    private int index;

    /*
     * Constructor for TempTranformationField
     */
    public TempTranformationField(int index, String code) {
        this.index = index;
        this.code = code;
    }

    public int getIndex() {
        return index;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        // sould never be shown
        return "<code>";
    }

    public int getLength() {
        return 6;
    }

    public void accept(TemplateFieldVisitor visitor) {
        visitor.visit(this);
    }
}
