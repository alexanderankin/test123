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
public class CodeOutputFieldToken implements Token {
    private String outputField;
    private String whiteSpace;

    public CodeOutputFieldToken(String outputField, String whiteSpace) {
        this.outputField = outputField;
        this.whiteSpace = whiteSpace;
    }

    public String getOutputField() {
        return outputField;
    }

    public String getWhiteSpace() {
        return whiteSpace;
    }
    
    public void accept(TemplateTokenVisitor visitor) {
        visitor.visit(this);
    }
}
