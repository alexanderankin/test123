/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.lexer.templategenerator.tokens.visitor;

import superabbrevs.lexer.templategenerator.tokens.CodeOutputFieldToken;
import superabbrevs.lexer.templategenerator.tokens.CodeToken;
import superabbrevs.lexer.templategenerator.tokens.TextFieldToken;

/**
 *
 * @author sune
 */
public interface TemplateTokenVisitor {
    public void visit(CodeOutputFieldToken token);
    public void visit(CodeToken token);
    public void visit(TextFieldToken token);
}
