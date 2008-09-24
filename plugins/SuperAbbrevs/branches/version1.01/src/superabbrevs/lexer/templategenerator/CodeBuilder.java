package superabbrevs.lexer.templategenerator;

import superabbrevs.lexer.templategenerator.tokens.CodeOutputFieldToken;
import superabbrevs.lexer.templategenerator.tokens.CodeToken;
import superabbrevs.lexer.templategenerator.tokens.TextFieldToken;
import superabbrevs.lexer.templategenerator.tokens.Token;
import superabbrevs.lexer.templategenerator.tokens.visitor.TemplateTokenVisitor;
import superabbrevs.utilities.TextUtil;

class CodeBuilder implements TemplateTokenVisitor {
    private StringBuilder code;
    
    public String buildCode(Iterable<Token> tokens) {
        code = new StringBuilder();
        for (Token token : tokens) {
            token.accept(this);
        }
        return code.toString();
    }
    
    public void visit(CodeOutputFieldToken token) {
        String outputField = token.getOutputField();
        String whiteSpace = token.getWhiteSpace();
                    
        if (whiteSpace != null) {
            whiteSpace = TextUtil.escape(whiteSpace);
            code.append("tpg.print(("+outputField+").replaceAll(" +
                    "\"\\n\",\"\\n"+whiteSpace+"\"));\n");
        } else {
            code.append("tpg.print(" + outputField + ");\n");
        }
    }

    public void visit(CodeToken token) {
        code.append(token.getCode() + "\n");
    }
    
    public void visit(TextFieldToken token) {
        code.append("tpg.print(\"" + 
                TextUtil.escape(token.getText()) + "\");\n");
    }
}
