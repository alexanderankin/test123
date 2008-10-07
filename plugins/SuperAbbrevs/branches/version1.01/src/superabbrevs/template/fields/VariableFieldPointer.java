package superabbrevs.template.fields;

import superabbrevs.template.fields.visitor.TemplateFieldVisitor;

public class VariableFieldPointer implements Field {

    private VariableField field;

    public VariableFieldPointer(VariableField field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return field.toString();
    }

    public int getLength() {
        return field.getLength();
    }

    public void accept(TemplateFieldVisitor visitor) {
        visitor.visit(this);
    }
}
