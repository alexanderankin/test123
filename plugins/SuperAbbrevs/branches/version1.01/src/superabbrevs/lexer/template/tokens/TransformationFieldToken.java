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
public class TransformationFieldToken implements Token {
    private int index;
    private String code;

    public TransformationFieldToken(int index, String code) {
        this.index = index;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public int getIndex() {
        return index;
    }

    public void accept(TemplateGeneratorTokenVisitor visitor) {
        visitor.visit(this);
    }
}
