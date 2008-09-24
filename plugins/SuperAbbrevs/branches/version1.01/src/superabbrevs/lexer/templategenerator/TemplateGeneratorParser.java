package superabbrevs.lexer.templategenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.ParseException;
import org.gjt.sp.jedit.bsh.TargetError;
import superabbrevs.lexer.templategenerator.tokens.Token;
import superabbrevs.template.TemplateInterpreter;

/**
 * @author Sune Simonsen
 * class TemplateGeneratorParser
 * Parses the input given by the lexer
 */
public class TemplateGeneratorParser {

    private TemplateGeneratorLexer lexer;
    private TemplateInterpreter interpreter;

    /*
     * Constructor for TemplateGeneratorParser
     */
    public TemplateGeneratorParser(TemplateGeneratorLexer lexer,
            TemplateInterpreter interpreter) {
        this.lexer = lexer;
        this.interpreter = interpreter;
    }

    /**
     * Method parse()
     * parse the input from the lexer
     * @return the generated template
     */
    public String parse()
            throws TargetError, ParseException, EvalError, IOException {
        CodeBuilder cb = new CodeBuilder();
        Iterable<Token> tokens = createTokens(lexer);

        String code = cb.buildCode(tokens);
        return interpreter.evaluateTemplateGenerationCode(code);
    }

    private Iterable<Token> createTokens(TemplateGeneratorLexer lexer) throws IOException {
        List<Token> tokenList = new ArrayList<Token>();
        Token t;
        while (null != (t = lexer.nextToken())) {
            tokenList.add(t);
        }
        return tokenList;
    }
}

