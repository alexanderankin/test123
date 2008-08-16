package superabbrevs.lexer;

import superabbrevs.SuperAbbrevsIO;
import superabbrevs.TextUtil;
import java.io.*;

import org.gjt.sp.jedit.bsh.*;
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
								   Interpreter interpreter){
		this.lexer = lexer;
		this.interpreter = interpreter;
	}
	
	/**
	 * Method parse()
	 * parse the input from the lexer
	 * @return the generated template
	 */
	public String parse() throws TargetError, ParseException, EvalError, IOException {
		StringBuffer code = new StringBuffer();
		Token t;
		
		interpreter.source(SuperAbbrevsIO.getAbbrevsFunctionPath());
		interpreter.source(SuperAbbrevsIO.getTemplateGenerationFunctionPath());
		
		StringBuffer out = new StringBuffer();
		interpreter.set("_out",out);
		while (null != (t = lexer.nextToken())){
			switch(t.getType()){
				case Token.CODE_OUTPUT_FIELD:
					String outputField = t.getStringValue(0);
					String whiteSpace = t.getStringValue(1);
					if(whiteSpace != null){
						whiteSpace = TextUtil.escape(whiteSpace);
						code.append("_out.append(_indent(\""+whiteSpace+"\","+outputField+"));\n");
					} else {
						code.append("_out.append("+outputField+");\n");
					}
					
					break;
				case Token.CODE:
					code.append(t.getValue(0)+"\n");
					break;
				case Token.TEXT_FIELD:
					code.append("_out.append(\""+TextUtil.escape((String)t.getValue(0))+"\");\n");
					break;	
			}
		}
		interpreter.eval(code.toString());
		
		return out.toString();
	}
}

