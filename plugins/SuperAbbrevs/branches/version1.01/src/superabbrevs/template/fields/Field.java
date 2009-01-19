package superabbrevs.template.fields;

import superabbrevs.template.fields.visitor.TemplateFieldVisitor;

public interface Field {
    public String toString();
    public int getLength();
    public void accept(TemplateFieldVisitor visitor);
}
