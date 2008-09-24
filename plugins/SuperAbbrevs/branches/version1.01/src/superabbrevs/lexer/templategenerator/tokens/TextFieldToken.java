/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.lexer.templategenerator.tokens;

import superabbrevs.lexer.templategenerator.tokens.visitor.TemplateTokenVisitor;

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
       
    public void accept(TemplateTokenVisitor visitor) {
        visitor.visit(this);
    }
}
