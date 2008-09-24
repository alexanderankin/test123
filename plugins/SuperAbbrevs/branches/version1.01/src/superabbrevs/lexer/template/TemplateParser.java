package superabbrevs.lexer.template;

import superabbrevs.template.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import superabbrevs.lexer.template.tokens.Token;

/**
 * @author Sune Simonsen
 * class TemplateParser
 * Parses the input given by the lexer
 */
public class TemplateParser {//implements TemplateGeneratorTokenVisitor {

    private TemplateLexer lexer;
    
    private TemplateInterpreter interpreter;

    /*
     * Constructor for Parser
     */
    public TemplateParser(TemplateLexer lexer, TemplateInterpreter interpreter) {
        this.lexer = lexer;
        this.interpreter = interpreter;
    }

    /**
     * Method parse()
     * parse the input from the lexer
     * @return the constructed template
     */
    public Template parse() throws IOException {
        Iterable<Token> tokens = createTokens(lexer); 
        TemplateBuilder tb = new TemplateBuilder();
        return tb.buildTemplate(tokens, interpreter);
    }

    private Iterable<Token> createTokens(TemplateLexer lexer) throws IOException {
        List<Token> tokenList = new ArrayList<Token>();
        Token t;
        while (null != (t = lexer.nextToken())) {
            tokenList.add(t);
        }
        return tokenList;
    }
}
