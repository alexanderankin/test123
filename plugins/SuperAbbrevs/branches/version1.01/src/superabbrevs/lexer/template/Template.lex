package superabbrevs.lexer.template;
import superabbrevs.lexer.template.tokens.*;
%%

%public
%class TemplateLexer
%unicode
%line
%column

%eofclose

%function nextToken
%type Token

 
%{
  StringBuffer field = new StringBuffer();
  Integer fieldNumber;
  StringBuffer text = new StringBuffer();
  int braceCount = 0;
  
  boolean end = false;
  
  private Integer readInteger(String s, int prefixLength, int suffixLength){
	  int length = s.length();
	  s = s.substring(prefixLength,length-suffixLength);
	  return new Integer(s);
  }
  
  private String unEscape(String escaped){
  	return escaped.substring(1);
  }
%}

/*
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]
*/

DecIntegerLiteral = 0 | [1-9][0-9]*

 
%state FIELD VARIABLE_START TRANSFORMATION_FIELD 

%%

 
/* keywords */
<YYINITIAL> {
	
	\\ ("\\" | "$") {
		text.append(unEscape(yytext()));
	}
	
    "$" { 
		yybegin(VARIABLE_START);
		if (text.length() != 0){
			Token t = new TextFieldToken(text.toString());
			text.setLength(0);
			return t;
		} else { 
			text.setLength(0); 
		} 
	}
	
	\n { 
		text.append("\n"); 
	}
	
	\t { 
		text.append("\t"); 
	}
	
	\r { 
		text.append("\r"); 
	}
	
	<<EOF>> { 
		if (end || text.length() == 0) { 
			return null; 
		} else {
			end = true;
			return new TextFieldToken(text.toString()); 
		} 
	}
	
	. { 
		text.append(yytext());
	}
	
}


<VARIABLE_START> {
	
	"end" | "{end}" {
		yybegin(YYINITIAL);
		return new EndFieldToken();
	}
	
	{DecIntegerLiteral} { 
		yybegin(YYINITIAL);
		return new FieldPointerToken(new Integer(yytext())); 
	}
	
	"{" {DecIntegerLiteral} ":" { 
		fieldNumber = readInteger(yytext(),1,1);
		field.setLength(0); 
		yybegin(FIELD); 
	}
	
	"{" {DecIntegerLiteral} "=" { 
		fieldNumber = readInteger(yytext(),1,1);
		field.setLength(0); 
		yybegin(TRANSFORMATION_FIELD); 
	}
	
	\n { 
		text.append("$\n");
		yybegin(YYINITIAL);
	}
	
	\t { 
		text.append("$\t"); 
	}
	
	\r { 
		text.append("$\r"); 
	}
	
	<<EOF>> { 
		if (end) { 
			return null; 
		}
		else {
			end = true; text.append("$");
			return new TextFieldToken(text.toString()); 
		} 
	}
	
	. { 
		text.append("$"+yytext()); 
		yybegin(YYINITIAL); 
	}
	
}
 
<FIELD> {
	\\ ("\\" | "{" | "}") {
		field.append(unEscape(yytext()));
	}
	
  	"}" { 
		yybegin(YYINITIAL);
		return new FieldToken(fieldNumber, field.toString()); 
	}
	
	\n { 
		field.append("\n"); 
	}
	
	\t { 
		field.append("\t"); 
	}
	
	\r { 
		field.append("\r"); 
	}
	
	<<EOF>> { 
		if (end || field.length() == 0) { 
			return null; 
		} else {
			end = true; 
			return new FieldToken(fieldNumber, field.toString()); 
   		} 
	}
	
	. { 
		field.append(yytext()); 
	}
	
}

/* it should be valid beanshell code so it ok the make { } if the balance*/
<TRANSFORMATION_FIELD> {
	
	"{" {
		braceCount++;
		field.append( "{" ); 
	}
	
	"}"	{ 
		if (braceCount == 0) {
			yybegin(YYINITIAL); 
			return new TransformationFieldToken(fieldNumber, field.toString());
		} else {
			field.append( "}" );
			braceCount--;
		} 
	}
	
	<<EOF>> { 
		if (end || field.length() == 0) { 
			return null; 
		} else {
			end = true; 
			return new TransformationFieldToken(fieldNumber, field.toString());
		} 
	}
	
	. { 
		field.append(yytext()); 
	}
	
}
