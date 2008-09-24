/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.template.fields;

import superabbrevs.template.fields.visitor.TemplateFieldVisitor;

/**
 *
 * @author sune
 */
public class TempVariableFieldPointer implements Field {
    private int index;

    public TempVariableFieldPointer(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getLength() {
        return toString().length();
    }

    @Override
    public String toString() {
        return "<field "+getIndex()+">";
    }

    public void accept(TemplateFieldVisitor visitor) {
        visitor.visit(this);
    }
}
