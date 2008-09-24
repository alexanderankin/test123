/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.lexer.template.tokens;

import superabbrevs.lexer.template.tokens.visitor.TemplateGeneratorTokenVisitor;

/**
 *
 * @author sune
 */
public class FieldToken implements Token {
    private int index;
    private String initialValue;

    public FieldToken(int index, String initialValue) {
        this.index = index;
        this.initialValue = initialValue;
    }

    public int getIndex() {
        return index;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void accept(TemplateGeneratorTokenVisitor visitor) {
        visitor.visit(this);
    }
}
