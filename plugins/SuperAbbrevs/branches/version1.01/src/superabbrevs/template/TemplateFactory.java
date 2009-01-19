package superabbrevs.template;

import java.io.IOException;
import java.io.StringReader;

import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.ParseException;
import org.gjt.sp.jedit.bsh.TargetError;

import superabbrevs.lexer.template.TemplateLexer;
import superabbrevs.lexer.template.TemplateParser;
import superabbrevs.lexer.templategenerator.TemplateGeneratorLexer;
import superabbrevs.lexer.templategenerator.TemplateGeneratorParser;

/**
 * @author Sune Simonsen
 * class TemplateFactory
 * Creates templates from strings
 */
public class TemplateFactory {

    private TemplateInterpreter interpreter;
    private String indent;

    public TemplateFactory(TemplateInterpreter interpreter, String indent) {
        this.interpreter = interpreter;
        this.indent = indent;
    }

    /**
     * Creates a new template from a string
     */
    public Template createTemplate(String template)
            throws TargetError, ParseException, EvalError, IOException {

        StringReader reader = new StringReader(template);

        TemplateGeneratorLexer templateGeneratorLexer =
                new TemplateGeneratorLexer(reader);
        TemplateGeneratorParser templateGeneratorParser =
                new TemplateGeneratorParser(templateGeneratorLexer, interpreter);

        // returns the generated template  
        String generatedTemplate = templateGeneratorParser.parse();
        // indent the generated template
        generatedTemplate = generatedTemplate.replaceAll("\n", "\n" + indent);

        reader = new StringReader(generatedTemplate);
        TemplateLexer templateLexer = new TemplateLexer(reader);
        TemplateParser templateParser = new TemplateParser(templateLexer, interpreter);

        return templateParser.parse();
    }
}
