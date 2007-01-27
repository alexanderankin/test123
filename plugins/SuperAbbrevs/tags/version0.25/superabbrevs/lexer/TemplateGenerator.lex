package superabbrevs.lexer;
%%

%public
%class TemplateGeneratorLexer
%unicode
%line
%column

%eofclose

%function nextToken
%type Token

 
%{
  StringBuffer code = new StringBuffer();
  StringBuffer text = new StringBuffer();
  
  String whiteSpace = null;
  
  boolean end = false;
  boolean inLineComment = false;
  boolean inRangeComment = false;
  boolean endOfLine = false;
  
  private Token token(int type, Object value) {
	Token t = new Token(type);
	t.addValue(value);
    return t;
  }
  
  private Token token(int type, Object value1, Object value2) {
	Token t = new Token(type);
	t.addValue(value1);
	t.addValue(value2);
    return t;
  }
%}

LineTerminator = \r|\n|\r\n
// InputCharacter = [^\r\n]
WhiteSpace     = [ \t\f]
 

%state CODE CODE_OUTPUT_FIELD

%%

 
/* keywords */
<YYINITIAL> {
	
	{WhiteSpace}* "<#=" {
		
		whiteSpace = yytext().substring(0,yytext().length()-3);
		text.append(whiteSpace);
		
		yybegin(CODE_OUTPUT_FIELD); 
		code.setLength(0);
		if (text.length() != 0){
			
			Token t = token(Token.TEXT_FIELD,text.toString());
			text.setLength(0);
			return t;
		} else { 
			text.setLength(0); 
		}
	}
	
    {WhiteSpace}* "<#" { 
		endOfLine = false;
		yybegin(CODE); 
		code.setLength(0);
		if (text.length() != 0){
			Token t = token(Token.TEXT_FIELD,text.toString());
			text.setLength(0);
			return t;
		} else { 
			text.setLength(0); 
		}
	}
	
	<<EOF>> { 
		if (end || text.length() == 0) { return null; }
		else {
			end = true;
			return token(Token.TEXT_FIELD,text.toString()); 
		} 
	}
	
	{LineTerminator} {
		endOfLine = true;
		text.append(yytext());
	}
	
	. { 
		endOfLine = false;
		text.append(yytext());
	}
	
}
/*TODO merge CODE and CODE_OUT_FIELD*/
<CODE> {

	"//" {
		inLineComment = true;
	}
	
	"/*" {
		inRangeComment = true;
	}
	
	"*/" {
		inRangeComment = false;
	}
	
	"<#" {
		if (!inLineComment && !inRangeComment){
			String temp = "<#"+code.toString();
			code.setLength(0);
			endOfLine = false;
			return token(Token.TEXT_FIELD,temp);
		}
	}
	
	"<#=" {
		if (!inLineComment && !inRangeComment){
			yybegin(CODE_OUTPUT_FIELD);
			String temp = "<#="+code.toString();
			code.setLength(0);
			endOfLine = false;
			return token(Token.TEXT_FIELD,temp);
		}
	}
	
	"#>" {WhiteSpace}* {LineTerminator} {
		endOfLine = true;
		whiteSpace = null;
		if (!inLineComment && !inRangeComment){
			yybegin(YYINITIAL); 
			return token(Token.CODE, code.toString());
		}
	}
	
	"#>" {
		endOfLine = false;
		whiteSpace = null;
		if (!inLineComment && !inRangeComment){
			yybegin(YYINITIAL); 
			return token(Token.CODE, code.toString());
		}
	}
	
	\n { 
		if (!inRangeComment){
			inLineComment = false;
			code.append("\n");
		}
	}
	
	<<EOF>> { 
		if (end || code.length() == 0) { return null; }
		else {
			end = true; 
			return token(Token.TEXT_FIELD,code.toString()); 
		}
	}
	
	\t | \r {
		code.append(yytext());
	}
	
	. { 
		if (!inRangeComment){
			code.append(yytext());
		}
	}
	
}

<CODE_OUTPUT_FIELD> {
	
	"//" {
		inLineComment = true;
	}
	
	"/*" {
		inRangeComment = true;
	}
	
	"*/" {
		inRangeComment = false;
	}
	
	"<#" {
		if (!inLineComment && !inRangeComment){
			yybegin(CODE);
			String temp = "<#"+code.toString();
			code.setLength(0);
			endOfLine = false;
			return token(Token.TEXT_FIELD,temp);
		}
	}
	
	"<#=" {
		if (!inLineComment && !inRangeComment){
			String temp = "<#="+code.toString();
			code.setLength(0);
			endOfLine = false;
			return token(Token.TEXT_FIELD,temp);
		}
	}
	
	"#>" { 
		if (!inLineComment && !inRangeComment){
			yybegin(YYINITIAL);
			
			if(!endOfLine || whiteSpace.equals("")){
				whiteSpace = null;
			}
			endOfLine = false;
			
			return token(Token.CODE_OUTPUT_FIELD, code.toString(), whiteSpace);
		} 
	}
	
	\n { 
		if (!inRangeComment){
			inLineComment = false;
			code.append("\n");
		}
	}
	
	\t | \r {
		code.append(yytext());
	}
	
	<<EOF>> { 
		if (end || code.length() == 0) { 
			return null; 
		} else {
			end = true; 
			return token(Token.TEXT_FIELD,code.toString()); 
		} 
	}
	
	. { 
		if (!inRangeComment){
			code.append(yytext());
		}
	}
	
}
