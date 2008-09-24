package superabbrevs.lexer.templategenerator.tokens;

import superabbrevs.lexer.templategenerator.tokens.visitor.TemplateTokenVisitor;

public class CodeToken implements Token {
    private String code;

    public CodeToken(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
    
    public void accept(TemplateTokenVisitor visitor) {
        visitor.visit(this);
    }
}
