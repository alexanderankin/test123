package superabbrevs.template;

import bsh.*;
import java.io.*;
import superabbrevs.lexer.*;

/**
 * @author Sune Simonsen
 * class TemplateFactory
 * Creates templates from strings
 */
public class TemplateFactory {
	/**
	 * Method createTemplate(String templateString)
	 * Creates a new template from a string
	 */
	public static Template createTemplate(String template, Interpreter interpreter, String indent) 
		throws TargetError, ParseException, EvalError, IOException {
			
		StringReader reader = new StringReader(template);
		
		TemplateGeneratorLexer templateGeneratorLexer = 
			new TemplateGeneratorLexer(reader);
		TemplateGeneratorParser templateGeneratorParser = 
			new TemplateGeneratorParser(templateGeneratorLexer, interpreter);
		
		// returns the generated template  
		String generatedTemplate = templateGeneratorParser.parse();
		// indent the generated template
		generatedTemplate = generatedTemplate.replaceAll("\n", "\n"+indent);
		
		//interpreter = new Interpreter();
		interpreter.set("indent",indent);
		
		reader = new StringReader(generatedTemplate);
		TemplateLexer templateLexer = new TemplateLexer(reader);
		TemplateParser templateParser = new TemplateParser(templateLexer, interpreter);
		
		return templateParser.parse();
	}
}
