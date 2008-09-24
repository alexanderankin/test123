/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.lexer.template.tokens.visitor;

import superabbrevs.lexer.template.tokens.EndFieldToken;
import superabbrevs.lexer.template.tokens.FieldPointerToken;
import superabbrevs.lexer.template.tokens.FieldToken;
import superabbrevs.lexer.template.tokens.TextFieldToken;
import superabbrevs.lexer.template.tokens.TransformationFieldToken;

/**
 *
 * @author sune
 */
public interface TemplateGeneratorTokenVisitor {
    public void visit(EndFieldToken token);
    public void visit(FieldPointerToken token);
    public void visit(FieldToken token);
    public void visit(TextFieldToken token);
    public void visit(TransformationFieldToken token);
}
