package superabbrevs.lexer;

import superabbrevs.Paths;
import java.io.*;
import bsh.*;
import superabbrevs.utilities.TextUtil;

/**
 * @author Sune Simonsen
 * class TemplateGeneratorParser
 * Parses the input given by the lexer
 */
public class TemplateGeneratorParser {

    private TemplateGeneratorLexer lexer;
    private Interpreter interpreter;

    /*
     * Constructor for TemplateGeneratorParser
     */
    public TemplateGeneratorParser(TemplateGeneratorLexer lexer,
            Interpreter interpreter) {
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
        StringBuffer code = new StringBuffer();
        Token t;

        interpreter.source(Paths.ABBREVS_FUNCTION_PATH);
        interpreter.source(Paths.TEMPLATE_GENERATION_FUNCTION_PATH);

        StringBuffer out = new StringBuffer();
        interpreter.set("_out", out);
        while (null != (t = lexer.nextToken())) {
            switch (t.getType()) {
                case Token.CODE_OUTPUT_FIELD: appendCodeField(code, t); break;
                case Token.CODE: appendCode(code, t); break;
                case Token.TEXT_FIELD: appendText(code, t); break;
            }
        }
        
        interpreter.eval(code.toString());
        return out.toString();
    }

    private void appendCode(StringBuffer code, Token t) {
        code.append(t.getValue(0) + "\n");
    }

    private void appendCodeField(StringBuffer code, Token t) {
        String outputField = t.getStringValue(0);
        String whiteSpace = t.getStringValue(1);
                    
        if (whiteSpace != null) {
            whiteSpace = TextUtil.escape(whiteSpace);
            code.append("_out.append(("+outputField+").replaceAll(" +
                    "\"\\n\",\"\\n"+whiteSpace+"\"));\n");
        } else {
            code.append("_out.append(" + outputField + ");\n");
        }
    }

    private void appendText(StringBuffer code, Token t) {
        code.append("_out.append(\"" + 
                TextUtil.escape(t.getStringValue(0)) + "\");\n");
    }
}

