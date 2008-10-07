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
public class FieldPointerToken implements Token {

    private int index;

    public FieldPointerToken(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
    
    public void accept(TemplateGeneratorTokenVisitor visitor) {
        visitor.visit(this);
    }

}
