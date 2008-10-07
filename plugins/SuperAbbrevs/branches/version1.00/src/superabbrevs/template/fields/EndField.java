package superabbrevs.template.fields;

import superabbrevs.template.fields.visitor.TemplateFieldVisitor;

public class EndField extends SelectableField {

    private int index = Integer.MAX_VALUE;

    public int getIndex() {
        return index;
    }
    
    @Override
    public String toString() {
        return "";
    }

    public int getLength() {
        return toString().length();
    }

    public void accept(TemplateFieldVisitor visitor) {
        visitor.visit(this);
    }
}
