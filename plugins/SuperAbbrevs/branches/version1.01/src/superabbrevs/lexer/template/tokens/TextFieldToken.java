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
public class TextFieldToken implements Token {

    private String text;

    public TextFieldToken(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
    public void accept(TemplateGeneratorTokenVisitor visitor) {
        visitor.visit(this);
    }

}
