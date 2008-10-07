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
public interface Token {
    public void accept(TemplateTokenVisitor visitor);
}
