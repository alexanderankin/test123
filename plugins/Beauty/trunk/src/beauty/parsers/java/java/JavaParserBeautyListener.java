
package beauty.parsers.java.java;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import static beauty.parsers.java.java.JavaParser.*;

import java.util.*;
import java.util.regex.*;

/**
 Beautifier for Java 17 and below.
 
 Reference Java Language Specification 17 Edition, https://docs.oracle.com/javase/specs/jls/se17/html/index.html
 
 Notes about indenting:
 
 Parent sets the indent level on entry
 Child applies the indent level to itself
 Parent adds the child
 
 Standard parent/child tree hierarchy. The leaf child will be a one liner.
 
 
 */

public class JavaParserBeautyListener extends JavaParserBaseListener {
    
    // token stream, need this to capture comments
    BufferedTokenStream tokens;

    // accumulate final output here
    private StringBuilder output;       // NOPMD
    
    // for tabs, 'tabCount' is current tab level, 'tab' is the string to use
    // for a tab, either '\t' or some number of spaces
    private int tabCount = 0;
    private String tab;
    
    // stack for holding intermediate formatted parts 
    private Deque<String> stack = new ArrayDeque<String>();
    
    // constants for trimming strings
    public static final int START = 1;
    public static final int END = 2;
    public static final int BOTH = 3;
    
    // bracket styles
    public static final int ATTACHED = 1;
    public static final int BROKEN = 2;
    
    // format settings
    private boolean softTabs = true;
    private int tabWidth = 4;
    private int bracketStyle = ATTACHED;        
    private boolean brokenBracket = false;
    private boolean breakElse = true;
    private boolean padParens = false;
    private boolean padOperators = true;
    private int blankLinesBeforePackage = 0;            
    private int blankLinesAfterPackage = 1;             
    private int blankLinesAfterImports = 2;             
    private boolean sortImports = true;                 
    private boolean groupImports = true;                
    private int blankLinesBetweenImportGroups = 1;      
    private int blankLinesAfterClassBody = 1;
    private int blankLinesBeforeMethods = 1;  
    private int blankLinesAfterMethods = 1;
    private boolean sortModifiers = true;               
    private int collapseMultipleBlankLinesTo = 1; 
    private int wrapLineLength = 120;
    
    // declarations
	private final int METHOD = 1;
	private final int FIELD = 2;
	private final int CONSTRUCTOR = 3;
	private final int INTERFACE = 4;
	private final int ANNOTATION = 5;
	private final int CLASS = 6;
	private final int ENUM = 7;
	private final int RECORD = 8;
	private final int CONST = 9;
    
    /**
     * @param initialSize Initial size of the output buffer.
     * @param softTabs If true, use spaces rather than '\t' for tabs.
     * @param tabWidth The number of spaces to use if softTabs.
     */
    public JavaParserBeautyListener(int initialSize, BufferedTokenStream tokenStream ) {
        output = new StringBuilder(initialSize);
        this.tokens = tokenStream;
    }

    /**
     * Set up tabs.    
     */
    private void init() {
        if (softTabs) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tabWidth; i++) {
                sb.append(' ');   
            }
            tab = sb.toString();
        }
        else {
            tab = "\t";   
        }
    }
    
    // this is useful for debugging
    private void printStack() {         // NOPMD
        printStack("");
    }
    
    // this is useful for debugging
    private void printStack(String name) {
        System.out.println("+++++ stack " + name + " +++++");
        for (String item : stack) {
            System.out.println("-------------------------------------");
            System.out.println(item);    
        }
        System.out.println("+++++ end stack " + name + " +++++");
    }
    
    // for testing
    public static void main (String[] args) {
        if (args == null)
            return;
        try {
            // set up the parser
            //long startTime = System.currentTimeMillis();
            java.io.FileReader input = new java.io.FileReader(args[0]);
            CharStream antlrInput = CharStreams.fromReader(input);
            JavaLexer lexer = new JavaLexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            JavaParser javaParser = new JavaParser( tokens );
            javaParser.setTrace(true);
            
            // parse and beautify the buffer contents
            JavaParserBeautyListener listener = new JavaParserBeautyListener(16 * 1024, tokens);
            listener.setUseSoftTabs(true);
            listener.setIndentWidth(4);
            listener.setPadParens(true);
            listener.setBracketStyle(BROKEN);
            ParseTreeWalker walker = new ParseTreeWalker();
            ParseTree tree = javaParser.compilationUnit();
            walker.walk( listener, tree );

            //System.out.println("----- final output -----");
            System.out.println(listener.getText());
            //System.out.println("------------------------");
            //long elapsed = System.currentTimeMillis() - startTime;
            //System.out.println("elapsed time: " + elapsed + " ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
/*
--------------------------------------------------------------------------------
Parser methods follow.
--------------------------------------------------------------------------------
*/

	/**
 	 * Main entry point.	
 	 */
	@Override public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) { 
	    init();    // initialize the formatting settings
	}

	
	/**
 	* altAnnotationQualifiedName
 	*     : (identifier DOT)* '@' identifier
 	*     ;
 	*/
	@Override public void exitAltAnnotationQualifiedName(AltAnnotationQualifiedNameContext ctx) { 
	    String identifier = pop();
	    String at = pop();
	    String start = "";
	    if (ctx.DOT() != null && ctx.DOT().size() > 0) {
	        int size = ctx.DOT().size() * 2;
	        start = reverse(size, "");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(start).append(at).append(identifier);
	    push(sb);
	}
	
	/**
 	* annotation
 	*     : ('@' qualifiedName | altAnnotationQualifiedName) ('(' ( elementValuePairs | elementValue )? ')')?
 	*     ;
 	*/
	@Override public void exitAnnotation(AnnotationContext ctx) {
	    StringBuilder elements = new StringBuilder();
	    if (ctx.RPAREN() != null) {
	        String rp = pop();    // )
	        String element = "";
	        if (ctx.elementValue() != null || ctx.elementValuePairs() != null) {
	            element = pop().trim();
	        }
	        String lp = pop();    // (
	        elements.append(lp).append(element).append(rp);
	    }
	    String name = pop().trim();
	    String at = "";
	    if (ctx.AT() != null) {
	        at = pop().trim();   
	    }
	    StringBuilder annotation = new StringBuilder();
	    annotation.append(at).append(name).append(elements);
	    if (!endsWith(elements, "\n")) {
	        annotation.append('\n');    
	    }
	    push(annotation);
	}
	
	/**
 	* annotationConstantRest
 	*     : variableDeclarators
 	*     ;
 	*/
	@Override public void exitAnnotationConstantRest(AnnotationConstantRestContext ctx) { 
	    // Nothing to do here, the only choice should already be on the stack.
	}
	
	/**
 	* annotationMethodOrConstantRest
 	*     : annotationMethodRest
 	*     | annotationConstantRest
 	*     ;
 	*/
	@Override public void exitAnnotationMethodOrConstantRest(AnnotationMethodOrConstantRestContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* annotationMethodRest
 	*     : identifier '(' ')' defaultValue?
 	*     ;
 	*/
	@Override public void exitAnnotationMethodRest(AnnotationMethodRestContext ctx) {
	    String defaultValue = "";
	    if (ctx.defaultValue() != null) {
	        defaultValue = pop().trim();
	    }
	    String rp = pop().trim();    // )
	    String lp = pop().trim();    // (
	    StringBuilder identifier = new StringBuilder(pop());
	    identifier.append(lp).append(rp).append(' ').append(defaultValue);
	    push(identifier);
	}
	
 	@Override public void enterAnnotationTypeBody(AnnotationTypeBodyContext ctx) {
 	    ++tabCount;  
 	}
 	
 	
	/**
 	* 	annotationTypeBody
 	*     : '{' (annotationTypeElementDeclaration)* '}'
 	*     ;
 	*/
 	@Override public void exitAnnotationTypeBody(AnnotationTypeBodyContext ctx) {
 	    --tabCount;
 	    String rb = pop().trim();    // }
 	    StringBuilder body = new StringBuilder();
 	    if (ctx.annotationTypeElementDeclaration() != null && ctx.annotationTypeElementDeclaration().size() > 0) {
 	        int size = ctx.annotationTypeElementDeclaration().size();
 	        List<String> parts = reverse(size);
 	        for (String part : parts) {
 	            body.append(part);
 	        }
 	    }
 	    String lb = pop();
 	    if (bracketStyle == BROKEN) {
 	        StringBuilder sb = new StringBuilder().append('\n').append(indent(lb));
 	        lb = sb.toString();    
 	    }
 	    StringBuilder sb = new StringBuilder();
 	    if (!isWhitespace(body)) {
 	        sb.append(lb);
 	        sb.append('\n').append(body);
 	        if (!endsWith(sb, "\n")) {
 	            sb.append('\n');   
 	        }
 	        rb = indent(rb);
 	        sb.append(rb).append('\n');
 	    }
 	    else {
 	        sb.append(lb.trim()).append('\n').append(indent(rb.trim())).append('\n');        
 	    }
 	    push(sb);
 	}
 	
 	/**
  	* annotationTypeDeclaration
  	*     : '@' INTERFACE identifier annotationTypeBody
  	*     ;
  	*/
	@Override public void exitAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) { 
	    String body = pop();
	    if (bracketStyle == ATTACHED) {
	        body = body.trim();    
	    }
	    String identifier = pop();
	    String interface_ = pop();    // interface keyword
	    String at = pop();    // @
	    at = indent(at);
	    StringBuilder sb = new StringBuilder();
	    sb.append(at).append(interface_).append(' ').append(identifier).append(' ').append(body);
	    if (!body.endsWith("\n")) {
	        sb.append('\n');
	    }
	    String anno = removeBlankLines(sb.toString(), BOTH);
	    push(anno);
	}
	
	
	/**
 	* annotationTypeElementDeclaration
 	*     : modifier* annotationTypeElementRest
 	*     | ';' // this is not allowed by the grammar, but apparently allowed by the actual compiler
 	*     ;
 	*/
	@Override public void exitAnnotationTypeElementDeclaration(AnnotationTypeElementDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.SEMI() != null) {
	        String semi = pop().trim();
	        sb.append(semi).append('\n');
	        push(sb);
	    }
	    else {
	        String rest = pop();
	        String modifiers = "";
	        if (ctx.modifier() != null && ctx.modifier().size() > 0) {
	            int size = ctx.modifier().size();
	            modifiers = formatModifiers(size).trim() + ' ';    
	        }
	        modifiers = modifiers.isEmpty() ? getIndent() : indent(modifiers);
	        sb.append(modifiers).append(rest).append('\n');
	        push(sb);
	    }
	}
	
	/**
 	* 	annotationTypeElementRest
 	*     : typeType annotationMethodOrConstantRest ';'
 	*     | classDeclaration ';'?
 	*     | interfaceDeclaration ';'?
 	*     | enumDeclaration ';'?
 	*     | annotationTypeDeclaration ';'?
 	*     | recordDeclaration ';'? // Java17
 	*     ;
 	*/
    @Override public void exitAnnotationTypeElementRest(AnnotationTypeElementRestContext ctx) { 
        String semi = "";
        if (ctx.SEMI() != null) {
            semi = pop().trim();
        }
        String decl = pop().trim();
        String typeType = "";
        if (ctx.typeType() != null) {
            typeType = pop().trim() + ' ';
        }
        StringBuilder sb = new StringBuilder();
        sb.append(typeType).append(decl).append(semi);
        push(sb);
    }
    
    /**
     * arguments
     *     : '(' expressionList? ')'
     *     ;
     */
	@Override public void exitArguments(ArgumentsContext ctx) { 
 	    String rp = pop();    // )
 	    String expressionList = "";
 	    if (ctx.expressionList() != null) {
 	        expressionList = pop().trim();
 	    }
 	    String lp = pop();    // (
 	    StringBuilder sb = new StringBuilder();
 	    if (expressionList.length() == 0) {
 	        sb.append("()");   
 	    }
 	    else {
 	        sb.append(padParen(lp)).append(expressionList).append(padParen(rp));
 	    }
 	    push(sb);
	}
	
	
	/**
 	* arrayCreatorRest
 	*     : '[' 
 	            (']' ('[' ']')* 
 	                arrayInitializer | expression 
 	        ']' 
 	         ('[' expression ']')* 
 	         ('[' ']')*)
 	*     ;
 	*/
	@Override public void exitArrayCreatorRest(ArrayCreatorRestContext ctx) {
	    // context has:
	    // list of [
	    // list of ]
	    // one array initializer, might be null
	    // list of expressions
	    
	    int size = 0;
	    if (ctx.LBRACK() != null && ctx.LBRACK().size() > 0) {
	        size += ctx.LBRACK().size();   
	    }
	    if (ctx.RBRACK() != null && ctx.RBRACK().size() > 0) {
	        size += ctx.RBRACK().size();   
	    }
	    if (ctx.arrayInitializer() != null) {
	        ++size;   
	    }
	    if (ctx.expression() != null && ctx.expression().size() > 0) {
	        size += ctx.expression().size();       
	    }
	    
	    List<String>parts = reverse(size);
	    StringBuilder sb = new StringBuilder();
	    for (String part : parts) {
	        sb.append(part.trim());    
	    }
	    push(sb);
	}
	
	
	/**
 	* arrayInitializer
 	*     : '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
 	*     ;
 	*/
	@Override public void exitArrayInitializer(ArrayInitializerContext ctx) { 
	    String rb = pop();    // }
	    
	    int size = ctx.variableInitializer().size();
	    if (ctx.COMMA() != null && ctx.COMMA().size() > 0) {
	        size += ctx.COMMA().size();   
	    }
	    String variableInitializer = reverse(size, " ", 2);
	    String lb = pop().trim();    // {
	    
	    String[] lines = variableInitializer.split("\n");
	    if (lines.length > 1) {
	        // if it's multiple lines, just indent them, don't wrap because the lines
	        // are probably already arranged 
	        StringBuilder indented = new StringBuilder();
	        ++tabCount;
	        for (String line : lines) {
	            indented.append(indent(line)).append('\n');
	        }
	        --tabCount;
	        indented.insert(0, '\n');
	        variableInitializer = indented.toString();
	        rb = indent(rb);
	    }
	    else {
	        // check line length, if over 120, split the line
	        if (variableInitializer.length() > wrapLineLength) {
	            StringBuilder sb = new StringBuilder();
	            String[] parts = variableInitializer.split("[,]");
	            StringBuilder line = new StringBuilder("\n");
	            int lineCount = 0;
	            for (int i = 0; i < parts.length; i++) {
	                String part = parts[i].trim();
	                if (!part.isEmpty()) {
	                    line.append(part);
                        line.append(", ");
	                }
	                if (line.length() >= 120) {
	                    String l = line.toString();
                        l = indent(l); 
                        l = indentAgain(l);
                        sb.append(l);    
                        ++lineCount;
                        line.delete(0, line.length());
	                }
	            }
	            // last line
	            String l = line.toString();
	            if (l.endsWith(", ")) {
	                l = l.substring(0, l.length() - 2);    
	            }
	            l = indent(l);
	            l = indentAgain(l);
	            sb.append(l);
	            variableInitializer = sb.toString();
	            rb = indent(rb);
	        }
 	    }
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append(lb).append(variableInitializer).append(rb);
	    push(sb);
	}
	
	@Override public void enterBlock(BlockContext ctx) {
	    ++tabCount;
	}
	
	/**
 	 * block
 	 *     : '{' blockStatement* '}'
 	 *     ;
 	 */
	@Override public void exitBlock(BlockContext ctx) {
	    --tabCount;
	    
	    String rb = pop().trim();
	    
	    String blockStatements = "";
	    if (ctx.blockStatement() != null && ctx.blockStatement().size() > 0) {
	        int size = ctx.blockStatement().size();
	        blockStatements = reverse(size, "");
	        blockStatements = removeBlankLines(blockStatements, BOTH);
	    }
	    String lb = pop().trim();
	    lb = indent(lb);    // need to indent in the case of a stand-alone block
	    
	    // assemble the parts
	    StringBuilder sb = new StringBuilder();
	    if (brokenBracket) {
	        sb.append('\n');
	        sb.append(indent(lb));
	    }
	    else {
	        sb.append(lb);
	    }
	    if (!blockStatements.isEmpty()) {
	        if (!lb.endsWith("\n") && !blockStatements.startsWith("\n")) {
	            sb.append('\n'); 
	        }
	        sb.append(blockStatements);
	        if (!blockStatements.endsWith("\n")) {
	            sb.append('\n');    
	        }
	    }
        rb = indent(rb);
        if (!endsWith(sb, "\n")) {
            sb.append('\n');
        }
        sb.append(rb).append('\n');
	    push(sb);
	}
	
	/**
 	* blockStatement
 	*     : localVariableDeclaration ';'
 	*     | statement
 	*     | localTypeDeclaration
 	*     ;
 	*/
	@Override public void exitBlockStatement(BlockStatementContext ctx) {
	    if (ctx.SEMI() != null && ctx.localVariableDeclaration() != null) {
	        String semi = pop().trim();    
	        String localVariableDeclaration = pop();
	        StringBuilder sb = new StringBuilder();
            sb.append(localVariableDeclaration).append(semi).append('\n');
	        push(sb);
	    }
	    else {
	        String stmt = pop();
	        if (stmt.startsWith("\n")) {
	            stmt = stmt.substring(1);   
	        }
	        push(stmt);
	    }
	}
	
	/**
 	* catchClause
 	*     : CATCH '(' variableModifier* catchType identifier ')' block
 	*     ;
 	*/
	@Override public void exitCatchClause(CatchClauseContext ctx) { 
	    String block = pop();
	    if (bracketStyle == BROKEN) {
	        block = new StringBuilder().append('\n').append(block).toString();    
	    }
	    else {
	        block = block.trim();
	    }
	    String rb = pop();    // )
	    String identifier = pop();
	    String catchType = pop();
	    String variableModifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        variableModifiers = formatModifiers(size, true).trim();
	    }
	    String lb = pop();    // (
	    String catch_ = pop();    // catch keyword
	    catch_ = indent(catch_);
	    StringBuilder sb = new StringBuilder();
	    sb.append(catch_).append(' ').append(padParen(lb)).append(variableModifiers).append(' ').append(catchType).append(' ').append(identifier).append(padParen(rb)).append(' ').append(block);
	    push(sb);
	}
	
	
	/**
 	* catchType
 	*     : qualifiedName ('|' qualifiedName)*
 	*     ;
 	*/
	@Override public void exitCatchType(CatchTypeContext ctx) { 
	    int size = ctx.qualifiedName().size();
	    String qualifiedNames = reverse(size * 2 - 1, " ");
	    push(qualifiedNames);
	}
	
	@Override public void enterClassBody(ClassBodyContext ctx) {
	    ++tabCount;
	}

	/**
 	* classBody
 	*     : '{' classBodyDeclaration* '}'
 	*     ;
 	*/
	@Override public void exitClassBody(ClassBodyContext ctx) {
	    --tabCount;
	    String rbrace = pop().trim();
	    rbrace = indent(rbrace);
	    String classBodyDecl = "";
	    if (ctx.classBodyDeclaration() != null) {
	        int size = ctx.classBodyDeclaration().size();
	        classBodyDecl = reverse(size, "");
	        classBodyDecl = removeBlankLines(classBodyDecl, START);
	    }
	    String lbrace = pop().trim();
	    if (bracketStyle == BROKEN) {
	        lbrace = new StringBuilder().append('\n').append(indent(lbrace)).toString();    
	    }
	    else {
	        lbrace = lbrace.trim();    
	    }

	    StringBuilder sb = new StringBuilder();
        if (brokenBracket) {  
            sb.append('\n');
            sb.append(indent(lbrace));
        }
        else {
            sb.append(lbrace);    
        }
        sb.append('\n');
        if (!classBodyDecl.isEmpty()) {  
            sb.append(classBodyDecl);
            if (!classBodyDecl.endsWith("\n")) {
                sb.append('\n');
            }
        }
        sb.append(rbrace);
	    push(sb);
	}
	
	
	/**
 	*     classBodyDeclaration
 	*         : ';'
 	*         | STATIC? block
 	*         | modifier* memberDeclaration
 	*         ;
 	*/
	@Override public void exitClassBodyDeclaration(ClassBodyDeclarationContext ctx) { 
	    if (ctx.SEMI() != null) {
	        String semi = pop();
	        semi = indent(semi);
	        push(semi);
	        return;        
	    }
	    
	    if (ctx.block() != null) {
	        String block = pop();
	        StringBuilder sb = new StringBuilder();
            if (ctx.STATIC() != null) {
                String static_ = pop();    // static keyword
                static_ = indent(static_);
                sb.append(static_).append(' ');
                block = block.trim();
            }
            sb.append(block);
            push(sb);
            return;
	    }
	    else {
            String memberDeclaration = pop();
            StringBuilder sb = new StringBuilder();
            String modifiers = "";
            if (ctx.modifier() != null && ctx.modifier().size() > 0) {
                int size = ctx.modifier().size();
                modifiers = formatModifiers(size, false);
            }
            if (!modifiers.isEmpty()) {
                boolean hasNewLine = modifiers.endsWith("\n");
                modifiers = indent(modifiers);
                sb.append(modifiers);
                sb.append(hasNewLine ? '\n' : ' ');
                sb.append(trimFront(memberDeclaration));
            }
            else {
                sb.append(memberDeclaration);
            }
            
            // handle blank lines here, check the memberDeclaration for what it is (field, method, etc)
            // and apply the blank line rules accordingly. 
            String decl = sb.toString();
            decl = removeBlankLines(decl, BOTH);
            MemberDeclarationContext mdc = ctx.memberDeclaration();
            int type = getMemberType(mdc);
            switch(type) {
                case CONSTRUCTOR:
                case METHOD:
                    decl = addBlankLines(decl, blankLinesBeforeMethods, START);
                    decl = addBlankLines(decl, blankLinesAfterMethods, END);
                    break;
                case ANNOTATION:
                case CLASS:
                case ENUM:
                case INTERFACE:
                case RECORD:
                    decl = addBlankLines(decl, blankLinesAfterClassBody, END);
                    break;
                case FIELD:
                default:
                    decl = addBlankLines(decl, 1, BOTH);
                    break;        
            }
            push(decl);
	    }
	}
	
	/**
 	* classCreatorRest
 	*     : arguments classBody?
 	*     ;
 	*/
	@Override public void exitClassCreatorRest(ClassCreatorRestContext ctx) {
	    String classBody = "";
	    if (ctx.classBody() != null) {
	        classBody = pop();
	    }
	    String arguments = pop();
	    StringBuilder sb = new StringBuilder(arguments);
	    if (classBody.length() > 0) {
	        sb.append(' ').append(classBody);   
	    }
	    push(sb);
	}
	
	/**
 	* classDeclaration
 	*     : CLASS identifier typeParameters?
 	*       (EXTENDS typeType)?
 	*       (IMPLEMENTS typeList)?
 	*       (PERMITS typeList)? // Java17
 	*       classBody
 	*     ;
 	*/
	@Override public void exitClassDeclaration(ClassDeclarationContext ctx) { 
	    String classBody = pop();
	    StringBuilder permitsList = new StringBuilder();
	    if (ctx.PERMITS() != null && ctx.typeList() != null) {
	        String typeList = pop();
	        String permits_ = pop();
	        permitsList.append(' ').append(permits_).append(' ').append(typeList);
	    }
	    StringBuilder implementsList = new StringBuilder();
	    if (ctx.IMPLEMENTS() != null && ctx.typeList() != null) {
	         String typeList = pop();
	         String implements_ = pop();
	         implementsList.append(' ').append(implements_).append(' ').append(typeList);
	    }
	    StringBuilder extendsType = new StringBuilder();
	    if (ctx.EXTENDS() != null && ctx.typeList() != null) {
	         String typeType = pop();
	         String extends_ = pop();
	         extendsType.append(' ').append(extends_).append(' ').append(typeType);
	    }
	    StringBuilder typeParameters = new StringBuilder();
	    if (ctx.typeParameters() != null) {
	        String params = pop();
	        typeParameters.append(' ').append(params);
	    }
	    String identifier = pop();
	    String class_ = pop();    // class keyword
	    class_ = indent(class_);
	    
	    StringBuilder classDecl = new StringBuilder();
	    classDecl.append(class_).append(' ').append(identifier).append(typeParameters).append(extendsType).append(implementsList).append(permitsList).append(' ').append(classBody);
	    push(classDecl);
	}
	
	/**
 	* classOrInterfaceModifier
 	*     : annotation
 	*     | PUBLIC
 	*     | PROTECTED
 	*     | PRIVATE
 	*     | STATIC
 	*     | ABSTRACT
 	*     | FINAL    // FINAL for class only -- does not apply to interfaces
 	*     | STRICTFP
 	*     | SEALED // Java17
 	*     | NON_SEALED // Java17
 	*     ;
 	*/
	@Override public void exitClassOrInterfaceModifier(ClassOrInterfaceModifierContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	
	/**
 	* classOrInterfaceType
 	*     : identifier typeArguments? ('.' identifier typeArguments?)*
 	*     ;
  	*      typeArguments
  	*          : '<' typeArgument (',' typeArgument)* '>'
  	*          ;
 	*/
	@Override public void exitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    int size = ctx.identifier().size();
	    if (size == 1) {
	        // only 1 identifier and maybe 1 typeArgument
	        String typeArguments = "";
	        if (ctx.typeArguments() != null && ctx.typeArguments().size() > 0) {
	            typeArguments = pop();
	        }
	        String identifier = pop();
	        sb.append(identifier).append(typeArguments);
	    }
	    else {
            // there are at least 2 identifiers
	        size += ctx.typeArguments().size() + ctx.DOT().size();
	        String type = reverse(size, "");
	        sb.append(type);
	    }
	    push(sb);
	}
	
	/**
 	* classType
 	*     : (classOrInterfaceType '.')? annotation* identifier typeArguments?
 	*     ;
 	*/
	@Override public void exitClassType(ClassTypeContext ctx) {
	    String typeArguments = "";
	    if (ctx.typeArguments() != null) {
	        typeArguments = pop();   
	    }
	    String identifier = pop();
	    String annotations = "";
	    if (ctx.annotation() != null) {
	        int size = ctx.annotation().size();
	        annotations = reverse(size, " ");
	    }
	    String classOrInterfaceType = "";
	    if (ctx.classOrInterfaceType() != null) {
	        String dot = pop();    // .
	        classOrInterfaceType = pop() + dot;
	    }
	    if (!classOrInterfaceType.isEmpty()) {
	        classOrInterfaceType = indent(classOrInterfaceType);    
	    }
	    else if (!annotations.isEmpty()) {
	        annotations = indent(annotations);    
	    }
	    else {
	        identifier = indent(identifier);    
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(classOrInterfaceType).append(annotations).append(' ').append(identifier).append(typeArguments);
	    push(sb);
	}
	
    /*
    compilationUnit
        : packageDeclaration? importDeclaration* typeDeclaration*
        | moduleDeclaration EOF
        ;
        Final output is assembled here.
    */
	@Override public void exitCompilationUnit(CompilationUnitContext ctx) {
        if (ctx.moduleDeclaration() != null) {
            String moduleDeclaration = pop();
            output.append(moduleDeclaration);
	    }
        else {	    
            String typeDeclarations = "";
            if (ctx.typeDeclaration() != null) {
                typeDeclarations = reverse(ctx.typeDeclaration().size(), "\n");
                typeDeclarations = removeBlankLines(typeDeclarations, BOTH);
                typeDeclarations = removeExcessWhitespace(typeDeclarations);
            }
            
            String importDeclarations = "";
            if (ctx.importDeclaration() != null && ctx.importDeclaration().size() > 0) {
                importDeclarations = sortAndGroupImports(reverse(ctx.importDeclaration().size()));
                importDeclarations = removeBlankLines(importDeclarations, BOTH);
                importDeclarations = removeExcessWhitespace(importDeclarations) + getBlankLines(blankLinesAfterImports);
            }
            
            String packageDeclaration = "";
            if (ctx.packageDeclaration() != null) {
                packageDeclaration = pop();
                packageDeclaration = getBlankLines(blankLinesBeforePackage) + removeBlankLines(packageDeclaration, BOTH);
                packageDeclaration = removeExcessWhitespace(packageDeclaration) + getBlankLines(blankLinesAfterPackage);
            }
            
            output.append(packageDeclaration);
            output.append(importDeclarations);
            output.append(typeDeclarations);
        }
	    // all done!
	}
	
	/**
 	* constDeclaration
 	*     : typeType constantDeclarator (',' constantDeclarator)* ';'
 	*     ;
 	*/
	@Override public void exitConstDeclaration(ConstDeclarationContext ctx) { 
	    String semi = pop().trim();
	    int size = ctx.constantDeclarator().size();
	    String constantDeclarator = reverse(size * 2 - 1, " ", 2).trim();
	    String typeType = pop().trim();
	    typeType = indent(typeType);
	    StringBuilder sb = new StringBuilder();
	    sb.append(typeType).append(' ').append(constantDeclarator).append(semi).append('\n');
	    push(sb);
	}
	
	/**
 	* constantDeclarator
 	*     : identifier ('[' ']')* '=' variableInitializer
 	*     ;
 	*/
	@Override public void exitConstantDeclarator(ConstantDeclaratorContext ctx) { 
	    String variableInitializer = pop();
	    String equals = pop();
	    String brackets = "";
	    if (ctx.LBRACK() != null) {
	        int size = ctx.LBRACK().size();
	        brackets = reverse(size, "");
	    }
	    String identifier = stack.pop();
	    identifier = indent(identifier);
	    StringBuilder sb = new StringBuilder();
	    sb.append(identifier).append(brackets).append(padOperator(equals)).append(variableInitializer);
	    push(sb);
	}
	
	/**
 	* constructorDeclaration
 	*     : identifier formalParameters (THROWS qualifiedNameList)? constructorBody=block
 	*     ;
 	*/
	@Override public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) {
	    String block = pop();
	    if (bracketStyle == BROKEN) {
	        block = new StringBuilder().append('\n').append(block).toString();    
	    }
	    else {
	        block = block.trim();   
	    }
	    StringBuilder throwsList = new StringBuilder();
	    if (ctx.THROWS() != null) {
	        String qualifiedNameList = pop().trim();
	        String throws_ = pop();    // throws keyword
	        throwsList.append(throws_).append(' ').append(qualifiedNameList);
	    }
	    String formalParameters = pop().trim();
	    String identifier = pop().trim();
	    identifier = indent(identifier);
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append(identifier).append(formalParameters).append(' ').append(throwsList).append(block);
	    push(sb);
	}
	
	/**
 	* createdName
 	*     : identifier typeArgumentsOrDiamond? ('.' identifier typeArgumentsOrDiamond?)*
 	*     | primitiveType
 	*     ;
 	*/
	@Override public void exitCreatedName(CreatedNameContext ctx) {
	    if (ctx.identifier() != null) {
	        StringBuilder backHalf = new StringBuilder();
	        if (ctx.DOT() != null) {
	             int size = ctx.DOT().size();
	             List<String> parts = new ArrayList<String>();
	             for (int i = 0; i < size; i++) {
	                 String maybeType = stack.peek();   
	                 if (maybeType.startsWith("<")) {
	                     parts.add(pop());    // typeArgumentOrDiamond   
	                 }
	                 parts.add(pop());    // identifier
	                 parts.add(pop());    // dot
	             }
	             Collections.reverse(parts);
	             for (String part : parts) {
	                 backHalf.append(part);   
	             }
	        }
	        String maybeType = stack.peek();
	        if (maybeType.startsWith("<")) {
	            maybeType = pop();    // first typeArgumentsOrDiamond
	        }
	        else {
	             maybeType = "";   
	        }
	        String identifier = pop();
	        identifier = indent(identifier);
	        StringBuilder sb = new StringBuilder();
	        sb.append(identifier).append(maybeType).append(backHalf);
	        push(sb);
	    }
	    // otherwise, nothing to do as primitiveType should already be on the stack
	}
	
	/**
 	* creator
 	*     : nonWildcardTypeArguments createdName classCreatorRest
 	*     | createdName (arrayCreatorRest | classCreatorRest)
 	*     ;
 	*/
	@Override public void exitCreator(CreatorContext ctx) { 
	    if (ctx.nonWildcardTypeArguments() != null) {
	        // first choice
	        String classCreatorRest = pop();
	        String createdName = pop();
	        String nonWildcardTypeArguments = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(nonWildcardTypeArguments).append(' ').append(createdName).append(classCreatorRest);
	        push(sb);
	    }
	    else {
	        // second choice
	        String end = pop();    // either arrayCreatorRest or classCreatorRest
	        String createdName = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(createdName).append(end);
	        push(sb);
	    }
	}
	
	/**
 	* defaultValue
 	*     : DEFAULT elementValue
 	*     ;
 	*/
	@Override public void exitDefaultValue(DefaultValueContext ctx) { 
	    String elementValue = pop();
	    String default_ = pop();
	    default_ = indent(default_);
	    StringBuilder sb = new StringBuilder();
	    sb.append(default_).append(' ').append(elementValue);
	    push(sb);
	}
	
	/**
 	* elementValue
 	*     : expression
 	*     | annotation
 	*     | elementValueArrayInitializer
 	*     ;
 	*/
	@Override public void exitElementValue(ElementValueContext ctx) {
	    // check for long lines, wrap if needed
	    String element = stack.pop();
	    if (element.length() > wrapLineLength) {
	        element = wrapLongLine(element);            
	    }
	    push(element);
	}
	
	/**
 	* elementValueArrayInitializer
 	*     : '{' (elementValue (',' elementValue)*)? (',')? '}'
 	*     ;
 	*/
	@Override public void exitElementValueArrayInitializer(ElementValueArrayInitializerContext ctx) { 
	    String rbrace = pop();
	    StringBuilder sb = new StringBuilder();
	    if (ctx.elementValue() != null) {
	        int size = ctx.elementValue().size();
	        if (ctx.COMMA() != null) {
	            size += ctx.COMMA().size();   
	        }
	        List<String> parts = reverse(size);
	        for (String part : parts) {
	            sb.append(part.trim());
	            if (part.equals(",")) {
	                sb.append(' ');   
	            }
	        }
	    }
	    String lbrace = pop();
	    sb.insert(0, lbrace);
	    sb.append(rbrace);
	    push(sb);
	}
	
	/**
 	* elementValuePair
 	*     : identifier '=' elementValue
 	*     ;
 	*/
	@Override public void exitElementValuePair(ElementValuePairContext ctx) {
	    String value = pop();
	    System.out.println("+++++ value>" + value + "<");
	    String equals = pop();    // equals
	    String identifier = pop();
	    StringBuilder pair = new StringBuilder();
	    pair.append(identifier).append(padOperator(equals)).append(value);
	    push(pair);
	}
	
	/**
 	* 	elementValuePairs
 	*     : elementValuePair (',' elementValuePair)*
 	*     ;
 	*/
    @Override public void exitElementValuePairs(ElementValuePairsContext ctx) { 
        int size = ctx.elementValuePair().size();
        if (ctx.COMMA() != null) {
            size += ctx.COMMA().size();    
        }
        List<String> parts = reverse(size);
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            part = part.trim();
            //part = part.replace('\n', ' ');
            sb.append(part);
            if (part.equals(",")) {
                sb.append(' ');   
            }
        }
        push(sb);
    }
    
    
    /**
     * enhancedForControl
     *     : variableModifier* (typeType | VAR) variableDeclaratorId ':' expression
     *     ;
     */
	@Override public void exitEnhancedForControl(EnhancedForControlContext ctx) { 
	    String expression = pop().trim();
	    String colon = pop();    // :
	    String variableDeclaratorId = pop();
	    String typeOrVar = pop();
	    String variableModifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        variableModifiers = formatModifiers(size, true).trim();
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(variableModifiers).append(' ').append(typeOrVar).append(' ').append(variableDeclaratorId).append(padOperator(colon)).append(expression);
	    push(sb);
	}
	
	
	/**
 	* enumBodyDeclarations
 	*     : ';' classBodyDeclaration*
 	*     ;
 	*/
	@Override public void exitEnumBodyDeclarations(EnumBodyDeclarationsContext ctx) { 
	    String classBodyDeclaration = "";
	    if (ctx.classBodyDeclaration() != null) {
	        int size = ctx.classBodyDeclaration().size();
	        StringBuilder sb = new StringBuilder();
	        List<String> parts = reverse(size);
	        for (String part : parts) {
	            sb.append(part).append('\n');    
	        }
	        classBodyDeclaration = sb.toString();
	        classBodyDeclaration = removeBlankLines(classBodyDeclaration, BOTH);
	    }
	    String semi = pop().trim();    // ;
	    StringBuilder sb = new StringBuilder();
	    sb.append(semi).append('\n');
	    if (!isWhitespace(classBodyDeclaration)) {
	        sb.append('\n');
	        sb.append(classBodyDeclaration);
	    }
	    push(sb);
	}
	
	
	/**
 	* enumConstant
 	*     : annotation* identifier arguments? classBody?
 	*     ;
 	*/
	@Override public void exitEnumConstant(EnumConstantContext ctx) { 
	    String classBody = "";
	    if (ctx.classBody() != null) {
	        classBody = pop(); 
	    }
	    String arguments = "";
	    if (ctx.arguments() != null) {
	        arguments = pop();    
	    }
	    String identifier = pop();
	    String annotations = "";
	    if (ctx.annotation() != null) {
	        int size = ctx.annotation().size();
	        annotations = reverse(size, " ");
	    }
	    if (!annotations.isEmpty()) {
	        annotations = indent(annotations);
	    }
	    else {
	        identifier = indent(identifier);   
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(annotations).append(identifier);
	    if (arguments.length() > 0) {
	        sb.append(' ').append(arguments);   
	    }
	    if (!classBody.isEmpty()) {
	        sb.append(' ').append(classBody);
	    }
	    push(sb);
	}
	
	/**
 	* enumConstants
 	*     : enumConstant (',' enumConstant)*
 	*     ;
 	*/
	@Override public void exitEnumConstants(EnumConstantsContext ctx) {
	    int size = ctx.enumConstant().size();
	    if (ctx.COMMA() != null) {
	        size += ctx.COMMA().size();   
	    }
	    StringBuilder sb = new StringBuilder();
	    List<String> constants = reverse(size);
	    for (int i = 0; i < size; i++) {
	        String constant = constants.get(i);
	        long lines = constant.lines().count();
	        if (lines == 1) {
	            sb.append(constant.trim());
	            if (constant.equals(",")) {
	                sb.append(' ');
	            }
	        }
	        else {
	            sb.append(constant);
	            if (constant.equals(",")) {
	                sb.append('\n');
	            }
	        }
	    }
	    String enumConstants = sb.toString();
	    enumConstants = trimEnd(enumConstants);
	    push(enumConstants);
	}
	
	@Override public void enterEnumDeclaration(EnumDeclarationContext ctx) {
	    ++tabCount;
	}
	
	/**
 	* enumDeclaration
 	*     : ENUM identifier (IMPLEMENTS typeList)? '{' enumConstants? ','? enumBodyDeclarations? '}'
 	*     ;
 	*/
	@Override public void exitEnumDeclaration(EnumDeclarationContext ctx) {
	    --tabCount;
	    String rb = pop();    // }
	    String enumBodyDeclarations = ctx.enumBodyDeclarations() == null ? "" : pop();
	    String comma = ctx.COMMA() == null ? "" : pop();
	    String enumConstants = ctx.enumConstants() == null ? "" : pop();
	    String lb = pop();    // {
	    if (bracketStyle == BROKEN) {
	        lb = new StringBuilder().append('\n').append(indent(lb)).toString();    
	    }
	    else {
	        lb = lb.trim();    
	    }
	    String implements_ = "";
	    if (ctx.IMPLEMENTS() != null && ctx.typeList() != null) {
	        String typeList = pop(); 
	        String impl = pop();
	        implements_ = new StringBuilder(impl).append(' ').append(typeList).append(' ').toString();
	    }
	    String identifier = pop();
	    String enum_ = pop();    // enum keyword
	    enum_ = indent(enum_);
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append(enum_).append(' ').append(identifier).append(' ').append(implements_).append(lb);
	    if (!lb.endsWith("\n")) {
	        sb.append('\n');
	    }
	    if (!enumConstants.isEmpty() || !comma.isEmpty() || !enumBodyDeclarations.isEmpty()) {
	        enumConstants = removeBlankLines(enumConstants, BOTH);
            sb.append(enumConstants);
            if (!comma.isEmpty()) {
                sb.append(comma);
            }
            if (enumBodyDeclarations.trim().length() > 1) {
                enumBodyDeclarations = trimFront(enumBodyDeclarations);
            }
            sb.append(enumBodyDeclarations);
	    }
	    rb = removeBlankLines(rb, START);
	    rb = indent(rb);
	    sb.append('\n').append(rb);
	    if (!rb.endsWith("\n")) {
	        sb.append('\n');
	    }
	    push(sb);
	}
	
	@Override public void exitEveryRule(ParserRuleContext ctx) { 
	    // Nothing to do here
	}
	
	
	/**
 	* explicitGenericInvocation
 	*     : nonWildcardTypeArguments explicitGenericInvocationSuffix
 	*     ;
 	*/
	@Override public void exitExplicitGenericInvocation(ExplicitGenericInvocationContext ctx) { 
	    String suffix = pop();
	    String args = pop();
	    args = indent(args);
	    StringBuilder sb = new StringBuilder();
	    sb.append(args).append(' ').append(suffix);
	    push(sb);
	}
	
	/**
 	* explicitGenericInvocationSuffix
 	*     : SUPER superSuffix
 	*     | identifier arguments
 	*     ;
 	*/
	@Override public void exitExplicitGenericInvocationSuffix(ExplicitGenericInvocationSuffixContext ctx) { 
        String second = pop();  // superSuffix or arguments
        String first = pop();    // super keyword or identifier
        StringBuilder sb = new StringBuilder();
        sb.append(first).append(second);
        push(sb);
	}

	
	/*
	* Yes, this is ugly.
	* These should all be one-liners.
	*
  	* expression
  	*     : primary
  	*     | expression bop='.'
  	*       (
  	*          identifier
  	*        | methodCall
  	*        | THIS
  	*        | NEW nonWildcardTypeArguments? innerCreator
  	*        | SUPER superSuffix
  	*        | explicitGenericInvocation
  	*       )
  	*     | expression '[' expression ']'
  	*     | methodCall
  	*     | NEW creator
  	*     | '(' annotation* typeType ('&' typeType)* ')' expression
  	*     | expression postfix=('++' | '--')
  	*     | prefix=('+'|'-'|'++'|'--') expression
  	*     | prefix=('~'|'!') expression
  	*     | expression bop=('*'|'/'|'%') expression
  	*     | expression bop=('+'|'-') expression
  	*     | expression ('<' '<' | '>' '>' '>' | '>' '>') expression
  	*     | expression bop=('<=' | '>=' | '>' | '<') expression
  	*     | expression bop=INSTANCEOF (typeType | pattern)
  	*     | expression bop=('==' | '!=') expression
  	*     | expression bop='&' expression
  	*     | expression bop='^' expression
  	*     | expression bop='|' expression
  	*     | expression bop='&&' expression
  	*     | expression bop='||' expression
  	*     | <assoc=right> expression bop='?' expression ':' expression
  	*     | <assoc=right> expression
  	*       bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
  	*       expression
  	*     | lambdaExpression // Java8
  	*     | switchExpression // Java17
  	* 
  	*     // Java 8 methodReference
  	*     | expression '::' typeArguments? identifier
  	*     | typeType '::' (typeArguments? identifier | NEW)
  	*     | classType '::' typeArguments? NEW
  	*     ;
  	*/
	@Override public void exitExpression(ExpressionContext ctx) { 
	    // a few of the choices can be skipped since they should already be on the stack
	    if (ctx.primary() != null) {
	        return;
	    }
	    if (ctx.methodCall() != null && (ctx.expression() == null || ctx.expression().size() == 0)) {
	        return;
	    }
	    if (ctx.lambdaExpression() != null)
	        return;
	    if (ctx.switchExpression() != null) {
	        return;
	    }
	    
	    // things with NEW
	    if (ctx.NEW() != null) {
	        if (ctx.creator() != null) {
	            // NEW creator
	            String creator = pop().trim();
	            String new_ = pop().trim();    // new keyword
	            StringBuilder sb = new StringBuilder();
	            sb.append(new_).append(' ').append(creator);
	            push(sb);
	            return;
	        }
	        if ((ctx.classType() != null || ctx.typeType() != null) && ctx.COLONCOLON() != null) {
	            // classType '::' typeArguments? NEW
	            // typeType '::' (typeArguments? identifier | NEW)
	            // in typeType case, there is a NEW, so there won't be an identifier,
	            // and the processing is the same as classType. Need to handle the
	            // case with an identifier separately.
	            String new_ = "";
	            if (ctx.NEW() != null) {
	                new_ = pop();
	            }
	            String identifier = ctx.identifier() == null ? "" : pop();
	            String typeArguments = "";
	            if (ctx.typeArguments() != null) {
	                typeArguments = pop();
	            }
	            String cc = pop();    // :: 
	            StringBuilder type = new StringBuilder();
                type.append(pop());  // classType or typeType
	            type.append(padOperator(cc));
	            type.append(typeArguments);
	            type.append(identifier);
	            type.append(new_);
	            push(type);
	            return;
	        }
	    }
	    if (ctx.typeType() != null && ctx.identifier() != null && ctx.COLONCOLON() != null) {
	        // typeType '::' (typeArguments? identifier | NEW)
	        // the case with NEW has already been handled above, just need to do 
	        // the case with an identifier
	        String identifier = pop();
            String typeArguments = ctx.typeArguments() == null ? "" : pop();
            String cc = pop();    // :: 
            StringBuilder typeType = new StringBuilder();
            typeType.append(pop());   // typeType
            typeType.append(padOperator(cc));
            typeType.append(typeArguments);
            if (typeArguments.length() > 0) {
                typeType.append(' ');
            }
            typeType.append(identifier);
            push(typeType);
            return;
	    }
	    if (ctx.COLONCOLON() != null) {
	        // expression '::' typeArguments? identifier
	        // This is the only remaining item with ::
	        String identifier = pop();
            String typeArguments = "";
            if (ctx.typeArguments() != null) {
                typeArguments = pop();
            }
            String cc = pop();    // :: 
            StringBuilder expression = new StringBuilder();
            expression.append(pop());   // typeType
            expression.append(padOperator(cc));
            expression.append(typeArguments);
            if (typeArguments.length() > 0) {
                expression.append(' ');
            }
            expression.append(identifier);
            push(expression);
            return;
	    }
	    // all the rest of the cases have at least one expression
	    int expressionCount = ctx.expression().size();
	    if (expressionCount == 1) {
            //expression bop='.'
            //      (
            //         identifier
            //       | methodCall
            //       | THIS
            //       | NEW nonWildcardTypeArguments? innerCreator
            //       | SUPER superSuffix
            //       | explicitGenericInvocation
            //      )
	        if (ctx.identifier() != null || ctx.methodCall() != null || ctx.THIS() != null || ctx.explicitGenericInvocation() != null) {
	            String end = pop().trim();
	            String dot = pop().trim();    // . bop
	            String expression = pop().trim();
	            expression = indent(expression);
	            StringBuilder sb = new StringBuilder();
	            sb.append(expression);
	            sb.append(dot);
	            sb.append(end);
	            push(sb);
	            return;
	        }
	        if (ctx.NEW() != null) {
	            String innerCreator = pop().trim();
	            String typeArgs = "";
	            if (ctx.nonWildcardTypeArguments() != null) {
	                 typeArgs = pop() + ' ';   
	            }
	            String new_ = pop();    // new keyword
	            String dot = pop().trim();    // . bop
	            StringBuilder expression = new StringBuilder(pop());
	            expression.append(dot);
	            expression.append(new_).append(' ');
	            expression.append(typeArgs);
	            expression.append(innerCreator);
	            push(expression);
	            return;
	        }
	        if (ctx.SUPER() != null) {
	            String superSuffix = pop();
	            String super_ = pop();    // super keyword
	            String dot = pop();    // . bop
	            StringBuilder expression = new StringBuilder(pop());
	            expression.append(dot);
	            expression.append(super_);
	            expression.append(superSuffix);
	            push(expression);
	            return;
	        }
	        if (ctx.typeType() != null && ctx.RPAREN() != null && ctx.LPAREN() != null) {
	            // '(' annotation* typeType ('&' typeType)* ')' expression
	            String expression = pop().trim();
	            String rp = pop();    // )
	            String typeTypes = "";
	            if (ctx.typeType().size() > 1) {
	                List<String> parts = new ArrayList<String>();
	                for (int i = 0; i < ctx.typeType().size() - 1; i++) {
	                    parts.add(pop());    // typeType
	                    parts.add(pop());    // &
	                }
	                Collections.reverse(parts);
	                StringBuilder sb = new StringBuilder();
	                for (String part : parts) {
	                    sb.append(part).append(' ');   
	                }
	                typeTypes = sb.toString();
	            }
	            String typeType = pop();
	            String annotations = "";
	            if (ctx.annotation() != null) {
	                int size = ctx.annotation().size();
	                List<String> parts = reverse(size);
	                StringBuilder sb = new StringBuilder();
	                for (String part : parts) {
	                    sb.append(part).append(' ');   
	                }
	                annotations = sb.toString();
	            }
	            String lp = pop();    // (
	            StringBuilder sb = new StringBuilder();
	            sb.append(lp);
	            sb.append(annotations);
	            sb.append(typeType);
	            if (typeTypes.length() > 0) {
	                sb.append(' ').append(typeTypes);   
	            }
	            sb.append(rp).append(' ').append(expression);
	            push(sb);
	            return;
	        }
	        if (ctx.INSTANCEOF() != null) {
	            // expression bop=INSTANCEOF (typeType | pattern)
	            // 'typeType' may have annotations preceding it, and formatModifiers will
	            // put a new line following the annotation. The next few lines bring
	            // the 'instanceof" back to a single line expression.
	            String typeOrPattern = pop();
	            typeOrPattern = typeOrPattern.replace("\n", " ");
	            typeOrPattern = typeOrPattern.replace("  ", " ");
	            String instanceOf = pop();
	            StringBuilder expression = new StringBuilder(pop());
	            expression.append(' ').append(instanceOf).append(' ').append(typeOrPattern);
	            push(expression);
	            return;
	        }
	        
	        // there are 3 remaining cases with one expression. postfix and prefix operators:
            // | expression postfix=('++' | '--')
            // | prefix=('+'|'-'|'++'|'--') expression
            // | prefix=('~'|'!') expression
            String post = stack.peek();
            if (post.indexOf("++") > 1 || post.indexOf("--") > 1) {
                // it's the first case, the postfix expression
                post = pop().trim();     // ++ or --
                String expression = pop();
                expression = removeBlankLines(expression, BOTH);
                StringBuilder sb = new StringBuilder();
                sb.append(expression).append(post);
                push(sb);
                return;
            }
            else {
                // it's one of the prefix expressions, and they are all the same
                String expression = pop().trim();
                String pre = pop().trim();
                StringBuilder sb = new StringBuilder();
                sb.append(pre).append(expression);
                pre = indent(sb.toString());
                push(pre);
                return;
            }
            
            
	    }
	    else if (expressionCount == 2) {
	        if (ctx.RBRACK() != null) {
	            // expression '[' expression ']'
	            String rb = pop();    // ]
	            String expression = pop().trim();
	            String lb = pop();    // [
	            StringBuilder sb = new StringBuilder(pop());
	            sb.append(lb).append(expression).append(rb);
	            push(sb);
	            return;
	        }
	        // the rest of the cases with 2 expressions can be handled the same
            // expression bop=('*'|'/'|'%') expression
            // expression bop=('+'|'-') expression
            // expression ('<' '<' | '>' '>' '>' | '>' '>') expression
            // expression bop=('<=' | '>=' | '>' | '<') expression
            // expression bop=INSTANCEOF (typeType | pattern)
            // expression bop=('==' | '!=') expression
            // expression bop='&' expression
            // expression bop='^' expression
            // expression bop='|' expression
            // expression bop='&&' expression
            // expression bop='||' expression
            // <assoc=right> expression
            // bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
            // expression
            String expression2 = pop().trim();
            String bop = pop().trim();
            if (bop.equals("<") ) {
                String peek = stack.peek();
                if (peek.equals("<")) {
                    stack.pop();
                    bop = "<<";   
                }
            }
            else if (bop.equals(">") ) {
                String peek = stack.peek();
                if (peek.equals(">")) {
                    stack.pop();
                    peek = stack.peek();    // might be >>>
                    if (peek.equals(">")) {
                        stack.pop();
                        bop = ">>>";
                    }
                    else {
                        bop = ">>";
                    }
                }
            }

            String expression1 = pop().trim();
            expression1 = removeBlankLines(expression1, BOTH);
            StringBuilder sb = new StringBuilder();
            sb.append(expression1);
            sb.append(padOperator(bop));
            sb.append(expression2);
            push(sb);
            return;
	    }
	    else if (expressionCount == 3) {
	        // only have one case with 3 expressions
	        //<assoc=right> expression bop='?' expression ':' expression
	        String exp2 = pop();
	        String colon = pop();    // :
	        String exp1 = pop();
	        String question = pop();    // ?
	        StringBuilder expression = new StringBuilder(pop());
	        expression.append(padOperator(question)).append(exp1).append(padOperator(colon)).append(exp2);
	        push(expression);
	        return;
	    }
	}
	
	/**
 	* expressionList
 	*     : expression (',' expression)*
 	*     ;
 	*/
	@Override public void exitExpressionList(ExpressionListContext ctx) { 
	    int size = ctx.expression().size();
	    if (size > 1) {
	        StringBuilder sb = new StringBuilder();
	        List<String> parts = reverse(size * 2 - 1);
            for (String part : parts) {
                sb.append(part.trim());
                if (part.equals(",")) {
                    sb.append(' ');   
                }
            }
            push(sb);
	    }
	    // otherwise, the first expression is already on the stack
	}
	
	
	/**
 	* 	fieldDeclaration
 	*     : typeType variableDeclarators ';'
 	*     ;
 	*/
    @Override public void exitFieldDeclaration(FieldDeclarationContext ctx) { 
        String semi = pop();    // semicolon
        String variableDeclarators = pop().trim();
        String typeType = stack.pop();
        typeType = indent(typeType);
        StringBuilder sb = new StringBuilder();
        sb.append(typeType).append(' ').append(variableDeclarators).append(semi).append('\n');
        push(sb);
    }
    
    /**
     * finallyBlock
     *     : FINALLY block
     *     ;
     */
	@Override public void exitFinallyBlock(FinallyBlockContext ctx) {
	    String block = pop();
	    if (bracketStyle == BROKEN) {
	        block = new StringBuilder().append('\n').append(block).toString();    
	    }
	    else {
	        block = block.trim();    
	    }
	    String finally_ = pop();    // finally keyword
	    finally_ = indent(finally_);
	    push(finally_ + ' ' + block);
	}
	
	
	/**
 	* floatLiteral
 	*     : FLOAT_LITERAL
 	*     | HEX_FLOAT_LITERAL
 	*     ;
 	*/
	@Override public void exitFloatLiteral(FloatLiteralContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* forControl
 	*     : enhancedForControl
 	*     | forInit? ';' expression? ';' forUpdate=expressionList?
 	*     ;
 	*/
	@Override public void exitForControl(ForControlContext ctx) { 
	    if (ctx.enhancedForControl() != null) {
	        return;    // it's already on the stack   
	    }
	    String expressionList = ctx.expressionList() == null ? "" : pop().trim();
	    String semi2 = pop().trim();    // ;
	    String expression = ctx.expression() == null ? "" : pop().trim();
	    String semi1 = pop().trim();    // ;
	    String forInit = ctx.forInit() == null ? "" : pop().trim();
	    StringBuilder sb = new StringBuilder();
	    sb.append(forInit).append(semi1);
	    if (!expression.isEmpty()) {
	        sb.append(' ');
	    }
	    sb.append(expression).append(semi2);
	    if (!expressionList.isEmpty()) {
	        sb.append(' ');
	    }
	    sb.append(expressionList);
	    push(sb);
	}
	
	/**
 	* forInit
 	*     : localVariableDeclaration
 	*     | expressionList
 	*     ;
 	*/
	@Override public void exitForInit(ForInitContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* formalParameter
 	*     : variableModifier* typeType variableDeclaratorId
 	*     ;
 	*/
	@Override public void exitFormalParameter(FormalParameterContext ctx) { 
	    String id = pop();
	    String typeType = pop();
	    String modifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        modifiers = formatModifiers(size, true).trim();
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(' ').append(typeType).append(' ').append(id);
	    push(sb);
	}
	
	/**
 	* formalParameterList
 	*     : formalParameter (',' formalParameter)* (',' lastFormalParameter)?
 	*     | lastFormalParameter
 	*     ;
 	*/
	@Override public void exitFormalParameterList(FormalParameterListContext ctx) {
	    // only care about the first choice here, the second choice is already on the stack
	    if (ctx.formalParameter() != null) {
	        String lastFormalParameter = "";
	        String comma = "";
	        if (ctx.lastFormalParameter() != null) {
	            lastFormalParameter = pop();
	            comma = pop();    // ,
	        }
	        
	        StringBuilder sb = new StringBuilder();
	        
	        int size = ctx.formalParameter().size() * 2 - 1;
	        List<String> parts = reverse(size);
	        for (String part : parts) {
	            part = part.trim();
	            sb.append(part);
	            if (part.equals(",")) {
	                sb.append(' ');    
	            }
	        }
	        sb.append(comma).append(' ').append(lastFormalParameter);
	        push(sb);
	    }
	}
	
	/**
 	* formalParameters
 	*     : '(' ( receiverParameter?
 	*           | receiverParameter (',' formalParameterList)?
 	*           | formalParameterList?
 	*           ) ')'
 	*     ;
 	*/
	@Override public void exitFormalParameters(FormalParametersContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rp = pop().trim();    // )
	    if (ctx.receiverParameter() != null && ctx.formalParameterList() != null) {
	        // second choice
	        String formalParameterList = pop();
	        String comma = pop().trim();
	        String receiverParameter = pop();
	        String lp = pop().trim();
	        sb.append(padParen(lp)).append(receiverParameter).append(comma).append(' ').append(formalParameterList).append(padParen(rp));
	    }
	    else if (ctx.receiverParameter() != null || ctx.formalParameterList() != null) {
	        // first or third choice
	        String param = pop().trim();
	        String lp = pop().trim();
	        sb.append(padParen(lp)).append(param).append(padParen(rp));
	    }
	    else {
	        // only have parens
	        String lp = pop();
	        sb.append(lp).append(rp);
	    }
	    push(sb);
	}
	
	
	/**
 	* genericConstructorDeclaration
 	*     : typeParameters constructorDeclaration
 	*     ;
 	*/
	@Override public void exitGenericConstructorDeclaration(GenericConstructorDeclarationContext ctx) { 
	    String decl = pop().trim();
	    String typeParameters = pop().trim();
	    typeParameters = indent(typeParameters);
	    StringBuilder sb = new StringBuilder();
	    sb.append(typeParameters).append(' ').append(decl);
	    push(sb);
	}
	
	/**
 	* genericInterfaceMethodDeclaration
 	*     : interfaceMethodModifier* typeParameters interfaceCommonBodyDeclaration
 	*     ;
 	*/
	@Override public void exitGenericInterfaceMethodDeclaration(GenericInterfaceMethodDeclarationContext ctx) { 
	    String decl = pop();
	    String typeParameters = pop();
	    String modifiers = "";
	    if (ctx.interfaceMethodModifier() != null) {
	        int size = ctx.interfaceMethodModifier().size();
	        modifiers = formatModifiers(size).trim();
	    }
	    modifiers = modifiers.isEmpty() ? getIndent() : indent(modifiers);
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(' ').append(typeParameters).append(' ').append(decl);
	    push(sb);
	}
	
	
	/**
 	* genericMethodDeclaration
 	*     : typeParameters methodDeclaration
 	*     ;
 	*/
	@Override public void exitGenericMethodDeclaration(GenericMethodDeclarationContext ctx) { 
	    String method = pop().trim();
	    String typeParameters = pop().trim();
	    typeParameters = indent(typeParameters);
	    StringBuilder sb = new StringBuilder();
	    sb.append(typeParameters).append(' ').append(method);
	    push(sb);
	}
	
	/**
 	* Java17
 	* guardedPattern
 	*     : '(' guardedPattern ')'
 	*     | variableModifier* typeType annotation* identifier ('&&' expression)*
 	*     | guardedPattern '&&' expression
 	*     ;
 	*/
	@Override public void exitGuardedPattern(GuardedPatternContext ctx) { 
	    if (ctx.RPAREN() != null) {
	        // first choice
	        String rp = pop().trim();    // )
	        String guardedPattern = pop().trim();
	        String lp = pop().trim();    // (
	        StringBuilder sb = new StringBuilder();
	        sb.append(padParen(lp)).append(guardedPattern).append(padParen(rp));
	        push(sb);
	    }
	    else if (ctx.typeType() != null) {
	        // second choice 
	        StringBuilder expression = new StringBuilder();
	        if (ctx.expression() != null) {
	            int size = ctx.expression().size();
	            List<String> parts = new ArrayList<String>();
	            for (int i = 0; i < size; i++) {
	                parts.add(pop().trim());    // expression
	                parts.add(padOperator(pop().trim()));    // &&
	            }
	            Collections.reverse(parts);
	            for (String part : parts) {
	                expression.append(part);
	            }
	        }
	        String identifier = pop();
            String annotations = "";
            if (ctx.annotation() != null) {
                int size = ctx.annotation().size();
                annotations = reverse(size, " ");
            }
            String typeType = pop();
            String modifiers = "";
            if (ctx.variableModifier() != null) {
                int size = ctx.variableModifier().size();
                modifiers = formatModifiers(size, true).trim();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(modifiers).append(' ').append(typeType).append(' ').append(annotations).append(identifier).append(expression);
            push(sb);
	    }
	    else {
	        // third choice
	        String expression = pop().trim();
	        String amps = pop().trim();    // &&
	        String guardedPattern = pop().trim();
	        StringBuilder sb = new StringBuilder();
	        sb.append(guardedPattern).append(padOperator(amps)).append(expression);
	        push(sb);
	    }
	}
	
	
	/**
 	* identifier
 	*     : IDENTIFIER
 	*     | MODULE
 	*     | OPEN
 	*     | REQUIRES
 	*     | EXPORTS
 	*     | OPENS
 	*     | TO
 	*     | USES
 	*     | PROVIDES
 	*     | WITH
 	*     | TRANSITIVE
 	*     | YIELD
 	*     | SEALED
 	*     | PERMITS
 	*     | RECORD
 	*     | VAR
 	*     ;
 	*/
	@Override public void exitIdentifier(IdentifierContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack
	}
	
	
	/**
 	* importDeclaration
 	*     : IMPORT STATIC? qualifiedName ('.' '*')? ';'
 	*     ;
 	*/
	@Override public void exitImportDeclaration(ImportDeclarationContext ctx) { 
	    String semi = pop();
	    String dotstar = "";
	    if (ctx.DOT() != null) {
	        String star = pop();    // *
	        String dot = pop();    // .
	        dotstar = dot + star;
	    }
	    String qualifiedName = pop();
	    String static_ = "";
	    if (ctx.STATIC() != null) {
	        static_ = pop() + ' ';    // static keyword
	    }
	    String import_ = pop();    // import keyword
	    import_ = indent(import_);
	    StringBuilder importDecl = new StringBuilder();
	    importDecl.append(import_).append(' ').append(static_).append(qualifiedName).append(dotstar).append(semi);
	    push(importDecl);
	}
	
	/**
 	* innerCreator
 	*     : identifier nonWildcardTypeArgumentsOrDiamond? classCreatorRest
 	*     ;
 	*/
	@Override public void exitInnerCreator(InnerCreatorContext ctx) { 
	    String classCreatorRest = pop().trim();
	    String args = ctx.nonWildcardTypeArgumentsOrDiamond() == null ? "" : pop();
	    String identifier = pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(identifier).append(args).append(classCreatorRest);
	    push(sb);
	}
	
	/**
 	* integerLiteral
 	*     : DECIMAL_LITERAL
 	*     | HEX_LITERAL
 	*     | OCT_LITERAL
 	*     | BINARY_LITERAL
 	*     ;
 	*/
	@Override public void exitIntegerLiteral(IntegerLiteralContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterInterfaceBody(InterfaceBodyContext ctx) {
	    ++tabCount;
	}	
	/**
 	* interfaceBody
 	*     : '{' interfaceBodyDeclaration* '}'
 	*     ;
 	*
 	*/
	@Override public void exitInterfaceBody(InterfaceBodyContext ctx) { 
	    --tabCount;
	    String rb = pop().trim();     // }
	    
	    String body = "";
	    if (ctx.interfaceBodyDeclaration() != null && ctx.interfaceBodyDeclaration().size() > 0) {
	        body = reverse(ctx.interfaceBodyDeclaration().size(), "");
	        body = removeBlankLines(body, BOTH);
	    }
	    
	    String lb = pop().trim();    // {
	    if (bracketStyle == BROKEN) {
	        lb = new StringBuilder().append('\n').append(lb).append('\n').toString();    
	    }
	    else {
	        lb = lb.trim();    
	    }
        	    
	    StringBuilder sb = new StringBuilder();
        sb.append(lb).append('\n');
	    sb.append(body);
	    
        trimEnd(sb);
        rb = indent(rb);  
        sb.append('\n').append(rb).append('\n');
        push(sb);
	}
	
	/**
 	* interfaceBodyDeclaration
 	*     : modifier* interfaceMemberDeclaration
 	*     | ';'
 	*     ;
 	*/
	@Override public void exitInterfaceBodyDeclaration(InterfaceBodyDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.SEMI() != null) {
	        String semi = pop().trim();
	        //semi = indent(semi);
	        sb.append(semi).append('\n');
	        push(sb);
	    }
	    else {
	        String decl = pop().trim();
	        String modifiers = "";
            if (ctx.modifier() != null) {
                int size = ctx.modifier().size();
                modifiers = formatModifiers(size).trim();
            }
            modifiers = modifiers.isEmpty() ? getIndent() : indent(modifiers) + ' ';
            sb.append(modifiers).append(decl).append('\n');
            
            // handle blank lines here, check the interfaceMemberDeclaration for what it is (field, method, etc)
            // and apply the blank line rules accordingly. 
            decl = sb.toString();
            decl = removeBlankLines(decl, BOTH);
            InterfaceMemberDeclarationContext mdc = ctx.interfaceMemberDeclaration();
            int type = getInterfaceMemberType(mdc);
            switch(type) {
                case CONST:
                    break;
                case METHOD:
                    decl = addBlankLines(decl, blankLinesBeforeMethods, START);
                    decl = addBlankLines(decl, blankLinesAfterMethods, END);
                    break;
                case INTERFACE:
                case ANNOTATION:
                case CLASS:
                case ENUM:
                case RECORD:
                    decl = addBlankLines(decl, blankLinesAfterClassBody, END);
                    break;
                default:
                    decl = addBlankLines(decl, 1, BOTH);
                    break;        
            }
            push(decl);
	    }
	}
	
	
	/**
 	* interfaceCommonBodyDeclaration
 	*     : annotation* typeTypeOrVoid identifier formalParameters ('[' ']')* (THROWS qualifiedNameList)? methodBody
 	*     ;
 	*/
	@Override public void exitInterfaceCommonBodyDeclaration(InterfaceCommonBodyDeclarationContext ctx) { 
	    String methodBody = pop().trim();
	    StringBuilder qualifiedNameList = new StringBuilder();
	    if (ctx.qualifiedNameList() != null) {
	        String list = pop();
	        String throws_ = pop();    // throws keyword
	        qualifiedNameList.append(throws_).append(' ').append(list);
	    }
	    String brackets = "";
	    if (ctx.RBRACK() != null) {
	        int size = ctx.RBRACK().size();
	        brackets = reverse(size * 2, "");
	    }
	    String formalParameters = pop();
	    String identifier = pop();
	    String typeTypeOrVoid = pop();
        String annotations = "";
	    if (ctx.annotation() != null) {
	        int size = ctx.annotation().size();
	        annotations = reverse(size, " ");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(annotations).append(typeTypeOrVoid).append(' ').append(identifier).append(formalParameters).append(brackets).append(qualifiedNameList).append(methodBody);
	    push(sb);
	}
	
	
	/**
 	* interfaceDeclaration
 	*     : INTERFACE identifier typeParameters? (EXTENDS typeList)? (PERMITS typeList)? interfaceBody
 	*     ;
 	*/
	@Override public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) { 
	    String interfaceBody = pop().trim();
	    String permits_ = "";
	    if (ctx.PERMITS() != null && ctx.typeList() != null && ctx.typeList().size() > 0) {
	        String typeList = pop().trim();
	        permits_ = pop().trim();    // permits keyword
	        permits_ = new StringBuilder().append(' ').append(permits_).append(' ').append(typeList).toString();
	    }
	    String extends_ = "";
	    if (ctx.EXTENDS() != null && ctx.typeList() != null && ctx.typeList().size() > 0) {
	        String typeList = pop().trim();
	        String keyword = pop().trim();    // extends
	        extends_ = new StringBuilder().append(' ').append(keyword).append(' ').append(typeList).toString();
	    }
	    String typeParameters = ctx.typeParameters() == null ? "" : ' ' +pop();
	    String identifier = pop().trim();
	    String interface_ = pop().trim();    // interface keyword
	    interface_ = indent(interface_);
	    StringBuilder sb = new StringBuilder();
	    sb.append(interface_).append(' ').append(identifier).append(typeParameters).append(extends_).append(permits_).append(' ').append(interfaceBody);
	    push(sb);
	}
	
	/**
 	* 	interfaceMemberDeclaration
 	*     : constDeclaration
 	*     | interfaceMethodDeclaration
 	*     | genericInterfaceMethodDeclaration
 	*     | interfaceDeclaration
 	*     | annotationTypeDeclaration
 	*     | classDeclaration
 	*     | enumDeclaration
 	*     | recordDeclaration // Java17
 	*     ;
 	*/
    @Override public void exitInterfaceMemberDeclaration(InterfaceMemberDeclarationContext ctx) { 
        // Nothing to do here, one of the choices should already be on the stack.
    }
    
    /**
     * interfaceMethodDeclaration
     *     : interfaceMethodModifier* interfaceCommonBodyDeclaration
     *     ;
     */
	@Override public void exitInterfaceMethodDeclaration(InterfaceMethodDeclarationContext ctx) { 
	    String decl = pop();
        String modifiers = "";
	    if (ctx.interfaceMethodModifier() != null) {
	        int size = ctx.interfaceMethodModifier().size();
	        modifiers = formatModifiers(size).trim();
	    }
	    modifiers = indent(modifiers);
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(' ').append(decl);
	    push(sb);
	}
	
	/**
 	* // Java8
 	* interfaceMethodModifier
 	*     : annotation
 	*     | PUBLIC
 	*     | ABSTRACT
 	*     | DEFAULT
 	*     | STATIC
 	*     | STRICTFP
 	*     ;
 	*/
	@Override public void exitInterfaceMethodModifier(InterfaceMethodModifierContext ctx) { 
        // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* // Java8
 	* lambdaBody
 	*     : expression
 	*     | block
 	*     ;
 	*/
	@Override public void exitLambdaBody(LambdaBodyContext ctx) { 
        // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* // Java8
 	* lambdaExpression
 	*     : lambdaParameters '->' lambdaBody
 	*     ;
 	*/
	@Override public void exitLambdaExpression(LambdaExpressionContext ctx) { 
	    String body = pop();
	    String arrow = pop();    // ->
	    String params = pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(params).append(padOperator(arrow)).append(body);
	    push(sb);
	}
	
	
	/**
 	* // local variable type inference
 	* lambdaLVTIList
 	*     : lambdaLVTIParameter (',' lambdaLVTIParameter)*
 	*     ;
 	*/
	@Override public void exitLambdaLVTIList(LambdaLVTIListContext ctx) { 
	    int size = ctx.lambdaLVTIParameter().size();
	    String list = reverse(size * 2 - 1, " ", 2);
	    push(list);
	}
	
	
	/**
 	* lambdaLVTIParameter
 	*     : variableModifier* VAR identifier
 	*     ;
 	*/
	@Override public void exitLambdaLVTIParameter(LambdaLVTIParameterContext ctx) { 
	    String identifier = pop();
	    String var_ = pop();    // var keyword
        String modifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        modifiers = formatModifiers(size, true).trim();
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(' ').append(var_).append(' ').append(identifier);
	    push(sb);
	}
	
	
	
	/**
 	* // Java8
 	* lambdaParameters
 	*     : identifier
 	*     | '(' formalParameterList? ')'
 	*     | '(' identifier (',' identifier)* ')'
 	*     | '(' lambdaLVTIList? ')'
 	*     ;
 	*/
	@Override public void exitLambdaParameters(LambdaParametersContext ctx) { 
	    if (ctx.RPAREN() != null) {
	        String rp = pop();     // )
	        if (ctx.formalParameterList() != null || ctx.lambdaLVTIList() != null) {
	            String part = pop();
	            String lp = pop();    // (
	            StringBuilder sb = new StringBuilder();
	            sb.append(padParen(lp)).append(part).append(padParen(rp));
	            push(sb);
	        }
	        else {
	            int size = ctx.identifier().size();
	            String identifiers = reverse(size * 2 - 1, " ", 2);
	            String lp = pop();    // (
	            StringBuilder sb = new StringBuilder();
	            sb.append(padParen(lp)).append(identifiers).append(padParen(rp));
	            push(sb);
	        }
	    }
	    // otherwise, 'identifier' is already on the stack
	}
	
	/**
 	* lastFormalParameter
 	*     : variableModifier* typeType annotation* '...' variableDeclaratorId
 	*     ;
 	*/
	@Override public void exitLastFormalParameter(LastFormalParameterContext ctx) { 
	    String id = pop();
	    String ellipsis = pop();    // ...
        String annotations = "";
	    if (ctx.annotation() != null) {
	        int size = ctx.annotation().size();
	        annotations = reverse(size, " ");
	    }
	    String typeType = pop();
	    String modifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        modifiers = formatModifiers(size, true).trim();
	    }
        StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(' ').append(typeType).append(' ').append(annotations).append(ellipsis).append(id);
	    push(sb);
	}
	
	
	/**
 	* literal
 	*     : integerLiteral
 	*     | floatLiteral
 	*     | CHAR_LITERAL
 	*     | STRING_LITERAL
 	*     | BOOL_LITERAL
 	*     | NULL_LITERAL
 	*     | TEXT_BLOCK // Java17
 	*     ;
 	*/
	@Override public void exitLiteral(LiteralContext ctx) { 
        // Nothing to do here, one of the choices should already be on the stack.
	}
	
	
	/**
 	* localTypeDeclaration
 	*     : classOrInterfaceModifier*
 	*       (classDeclaration | interfaceDeclaration | recordDeclaration)
 	*     | ';'
 	*     ;
 	* This is identical to typeDeclaration
 	*/
	@Override public void exitLocalTypeDeclaration(LocalTypeDeclarationContext ctx) { 
	    if (ctx.SEMI() != null) {
	        String semi = pop();
	        semi = indent(semi);
	        push(semi);
	        return;    
	    }
	    String declaration = pop();
	    String modifiers = "";
	    if (ctx.classOrInterfaceModifier() != null && ctx.classOrInterfaceModifier().size() > 0) {
	        int size = ctx.classOrInterfaceModifier().size();
	        modifiers = formatModifiers(size).trim();
	        declaration = declaration.trim();
	    }
	    modifiers = indent(modifiers);
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(' ').append(declaration);
	    push(sb);
	}
	
	
	/**
 	* localVariableDeclaration
 	*     : variableModifier* (typeType variableDeclarators | VAR identifier '=' expression)
 	*     ;
 	*/
    @Override public void exitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) { 
        StringBuilder type = new StringBuilder();
        if (ctx.VAR() != null) {
            String expression = pop();
            String equals = padOperator(pop());
            String identifier = pop();
            String var_ = pop();    // var keyword
            var_ = indent(var_);
            type.append(var_).append(' ').append(identifier).append(equals).append(expression);
        }
        else {
            String variableDecls = pop().trim();
            String typeType = pop();
            typeType = indent(typeType);
            type.append(typeType).append(' ').append(variableDecls);
        }
        
        String modifiers = "";
        String decl = type.toString();
        if (ctx.variableModifier() != null && ctx.variableModifier().size() > 0) {
            int size = ctx.variableModifier().size();
            modifiers = formatModifiers(size, true);
            modifiers = indent(modifiers);
            decl = trimFront(decl);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(modifiers);
        if (!modifiers.isEmpty()) {
            sb.append(' ');    
        }
        sb.append(decl);
        push(sb);
    }
	
	
	/**
 	* memberDeclaration
 	*     : methodDeclaration
 	*     | genericMethodDeclaration
 	*     | fieldDeclaration
 	*     | constructorDeclaration
 	*     | genericConstructorDeclaration
 	*     | interfaceDeclaration
 	*     | annotationTypeDeclaration
 	*     | classDeclaration
 	*     | enumDeclaration
 	*     | recordDeclaration //Java17
 	*     ;
 	*/
	@Override public void exitMemberDeclaration(MemberDeclarationContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* methodBody
 	*     : block
 	*     | ';'
 	*     ;
 	*/
	@Override public void exitMethodBody(MethodBodyContext ctx) {
	    if (ctx.SEMI() != null) {
	        String semi = pop();
	        semi = indent(semi);
	        push(semi);
	    }
	    // otherwise, block is already on the stack
	}
	
	/**
 	* methodCall
 	*     : identifier '(' expressionList? ')'
 	*     | THIS '(' expressionList? ')'
 	*     | SUPER '(' expressionList? ')'
 	*     ;
 	*/
	@Override public void exitMethodCall(MethodCallContext ctx) { 
	    String rp = pop().trim();    // )
	    String expressionList = ctx.expressionList() == null ? "" : pop();
	    long lines = expressionList.lines().count();
	    String lp = pop().trim();    // (
	    String start = pop().trim();    // one of 'identifier', 'this', or 'super'
	    start = indent(start);
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append(start);
	    if (lines > 1 && !expressionList.startsWith("\"\"\"")) {    // don't worry about text blocks
	        sb.append(lp).append('\n');  
	        expressionList = removeBlankLines(expressionList, BOTH);
	        sb.append(indentAgain(expressionList));
	        sb.append(indent(rp));
	    }
	    else {
	        if (!expressionList.isEmpty()) {
	            lp = padParen(lp);
	            rp = padParen(rp);
	        }
	        sb.append(lp);
	        sb.append(expressionList.trim());
	        sb.append(rp);
	    }
	    push(sb);
	}
	
	
	/**
 	* methodDeclaration
 	*     : typeTypeOrVoid identifier formalParameters ('[' ']')*
 	*       (THROWS qualifiedNameList)?
 	*       methodBody
 	*     ;
 	*/
	@Override public void exitMethodDeclaration(MethodDeclarationContext ctx) { 
	    String methodBody = pop().trim();
	    String throwsList = "";
	    if (ctx.THROWS() != null) {
	        String qualifiedNameList = pop();
	        String throws_ = pop();    // throws keyword
	        throwsList = throws_ + ' ' + qualifiedNameList;
	    }
	    String brackets = "";
	    if (ctx.RBRACK() != null) {
	        int size = ctx.RBRACK().size() * 2;     // * 2 for LBRACK
	        brackets = reverse(size, "");
	    }
	    String formalParameters = pop().trim();
	    String identifier = pop().trim();
	    String typeOrVoid = pop().trim();
	    typeOrVoid = indent(typeOrVoid);
	    StringBuilder method = new StringBuilder();
	    method.append(typeOrVoid).append(' ').append(identifier).append(formalParameters).append(' ').append(brackets).append(throwsList);
	    if (!throwsList.isEmpty()) {
	        method.append(' ');
	    }
	    method.append(methodBody);
	    push(method);
	}
	
	/**
 	* modifier
 	*     : classOrInterfaceModifier
 	*     | NATIVE
 	*     | SYNCHRONIZED
 	*     | TRANSIENT
 	*     | VOLATILE
 	*     ;
 	*/
	@Override public void exitModifier(ModifierContext ctx) { 
	    // Nothing to do here, one of the choices should aready be on the stack.
	}

	@Override public void enterModuleBody(ModuleBodyContext ctx) {
	    ++tabCount;
	}
	
	
	/**
 	* moduleBody
 	*     : '{' moduleDirective* '}'
 	*     ;
 	*/
	@Override public void exitModuleBody(ModuleBodyContext ctx) {
	    --tabCount;
	    String rbrace = pop();
	    rbrace = indent(rbrace);
	    StringBuilder moduleDirectives = new StringBuilder();
	    if (ctx.moduleDirective() != null) {
	        int size = ctx.moduleDirective().size();
	        List<String> directives = reverse(size);
	        for (int i = 0; i < size; i++) {
	            moduleDirectives.append(directives.get(i)).append('\n');   
	        }
	    }
	    String lbrace = indent(pop());
	    StringBuilder moduleBody = new StringBuilder();
	    moduleBody.append(lbrace).append('\n');
	    moduleBody.append(moduleDirectives);
	    moduleBody.append(rbrace);
	    push(moduleBody);
	}

	
	/**
 	*  moduleDeclaration
 	*     : OPEN? MODULE qualifiedName moduleBody
 	*     ;
 	*/
    @Override public void exitModuleDeclaration(ModuleDeclarationContext ctx) { 
        String moduleBody = pop();
        String qualifiedName = pop();
        String module_ = pop();    // module keyword
        StringBuilder moduleDeclaration = new StringBuilder();
        moduleDeclaration.append(module_).append(' ');
        moduleDeclaration.append(qualifiedName);
        moduleDeclaration.append(brokenBracket ? '\n' : ' ');
        moduleDeclaration.append(moduleBody);
        if (ctx.OPEN() != null) {
            String open_ = pop() + ' ';
            moduleDeclaration.insert(0, open_);    
        }
        push(moduleDeclaration);
    }
    
    /**
     * moduleDirective
     * 	: REQUIRES requiresModifier* qualifiedName ';'
     * 	| EXPORTS qualifiedName (TO qualifiedName)? ';'
     * 	| OPENS qualifiedName (TO qualifiedName)? ';'
     * 	| USES qualifiedName ';'
     * 	| PROVIDES qualifiedName WITH qualifiedName ';'
     * 	;
     */
	@Override public void exitModuleDirective(ModuleDirectiveContext ctx) {
	    String semi = pop();
	    StringBuilder moduleDirective = new StringBuilder();
	    if (ctx.REQUIRES() != null) {
	        // REQUIRES requiresModifier* qualifiedName ';'
	        String qualifiedName = pop();
	        if (ctx.requiresModifier() != null) {
	            int size = ctx.requiresModifier().size();
	            List<String> parts = reverse(size);
	            for (int i = 0; i < size; i++) {
	                moduleDirective.append(parts.get(i)).append(' ');
	            }
	        }
	        moduleDirective.append(qualifiedName);
	        moduleDirective.insert(0,  pop() + ' ');    // requires keyword
	        
	    }
	    else if (ctx.EXPORTS() != null) {
	        // EXPORTS qualifiedName (TO qualifiedName)? ';'
	        if (ctx.TO() != null) {
	            String qualifiedName2 = pop();
	            String to_ = pop();    // to keyword
	            String qualifiedName = pop();
	            moduleDirective.append(qualifiedName).append(' ').append(to_).append(' ').append(qualifiedName2);   
	        }
	        else {
	            String qualifiedName = pop();
	            moduleDirective.append(qualifiedName);
	        }
	        moduleDirective.insert(0, pop() + ' ');    // exports keyword
	    }
	    else if (ctx.OPENS() != null) {
	        // OPENS qualifiedName (TO qualifiedName)? ';'
	        if (ctx.TO() != null) {
	            String qualifiedName2 = pop();
	            String to_ = pop();    // to keyword
	            String qualifiedName = pop();
	            moduleDirective.append(qualifiedName).append(' ').append(to_).append(' ').append(qualifiedName2);   
	        }
	        else {
	            String qualifiedName = pop();
	            moduleDirective.append(qualifiedName);
	        }
	        moduleDirective.insert(0, pop() + ' ');    // opens keyword
	    }
	    else if (ctx.USES() != null) {
	        // USES qualifiedName ';'
            String qualifiedName = pop();
            String uses_ = pop();
            moduleDirective.append(uses_).append(' ').append(qualifiedName);
	    }
	    else if (ctx.PROVIDES() != null) {
	        // PROVIDES qualifiedName WITH qualifiedName ';'
	        moduleDirective.append("provides ");
            String qualifiedName2 = pop();
            String with_ = pop();    // with keyword
            String qualifiedName = pop();
            String provides_ = pop();
            moduleDirective.append(provides_).append(' ').append(qualifiedName).append(' ').append(with_).append(' ').append(qualifiedName2);
	    }
	    moduleDirective.append(semi);
        push(indent(moduleDirective.toString()));
	}
	
	/**
 	* nonWildcardTypeArguments
 	*     : '<' typeList '>'
 	*     ;
 	*/
	@Override public void exitNonWildcardTypeArguments(NonWildcardTypeArgumentsContext ctx) { 
	    String gt = pop();    // >
	    String typeList = pop();
	    String lt = pop();    // <
	    StringBuilder sb = new StringBuilder();
	    sb.append(lt).append(typeList).append(gt);
	    push(sb);
	}
	
	
	/**
 	* nonWildcardTypeArgumentsOrDiamond
 	*     : '<' '>'
 	*     | nonWildcardTypeArguments
 	*     ;
 	*/
	@Override public void exitNonWildcardTypeArgumentsOrDiamond(NonWildcardTypeArgumentsOrDiamondContext ctx) { 
	    if (ctx.nonWildcardTypeArguments() == null) {
	        // first choice, diamonds 
	        String gt = pop();    // >
	        String lt = pop();    // <
	        push(lt + gt);
	    }
	    // otherwise, nonWildcardTypeArguments is already on the stack
	}
	
	/**
 	*  packageDeclaration
 	*      : annotation* PACKAGE qualifiedName ';'
 	*      ;
 	*/
	@Override public void exitPackageDeclaration(PackageDeclarationContext ctx) { 
        String semi = pop();
        String qualifiedName = pop();
        String package_ = pop();    // package keyword
        StringBuilder packageDecl = new StringBuilder();
        if (ctx.annotation() != null) {
            int size = ctx.annotation().size();
            List<String> parts = reverse(size);
            for (int i = 0; i < size; i++) {
                packageDecl.append(parts.get(i)).append(' ');   
            }
            packageDecl.append('\n');
        }
        packageDecl.append(package_).append(' ').append(qualifiedName).append(semi);
        push(packageDecl);
	}
	
	/**
 	* parExpression
 	*     : '(' expression ')'
 	*     ;
 	*/
	@Override public void exitParExpression(ParExpressionContext ctx) { 
	    String rp = pop();    // )
	    String expression = pop().trim();
	    String lp = pop();    // (
	    StringBuilder sb = new StringBuilder();
	    sb.append(padParen(lp)).append(expression).append(padParen(rp));
	    push(sb);
	}
	
	/**
 	* // Java17
 	* pattern
 	*     : variableModifier* typeType annotation* identifier
 	*     ;
 	*/
	@Override public void exitPattern(PatternContext ctx) { 
	    String identifier = pop();
        String annotations = "";
	    if (ctx.annotation() != null) {
	        int size = ctx.annotation().size();
	        annotations = reverse(size, " ");
	    }
	    String typeType = pop();
        String modifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        modifiers = formatModifiers(size, true).trim();
	    }
	    StringBuilder sb = new StringBuilder();
	    if (!modifiers.isEmpty()) {
	        sb.append(modifiers).append(' ');   
	    }
	    sb.append(typeType);
	    if (!annotations.isEmpty()) {
	        sb.append(annotations);    
	    }
	    sb.append(' ').append(identifier);
	    push(sb);
	}
	
	/**
 	* primary
 	*     : '(' expression ')'
 	*     | THIS
 	*     | SUPER
 	*     | literal
 	*     | identifier
 	*     | typeTypeOrVoid '.' CLASS
 	*     | nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
 	*     ;
 	*/
	@Override public void exitPrimary(PrimaryContext ctx) { 
	    if (ctx.RPAREN() != null) {
            String rp = pop().trim();    // )
            String expression = ctx.expression() == null ? "" : pop();
            long lines = expression.lines().count();
            String lp = pop().trim();    // (
            
            StringBuilder sb = new StringBuilder("\n");
            if (lines > 1) {
                sb.append(lp).append('\n');  
                expression = removeBlankLines(expression, BOTH);
                sb.append(indentAgain(expression));
                sb.append(indent(rp));
            }
            else {
                if (!expression.isEmpty()) {
                    lp = padParen(lp);
                    rp = padParen(rp);
                }
                sb.append(lp);
                sb.append(expression.trim());
                sb.append(rp);
            }
            push(sb);
	    }
	    else if (ctx.CLASS() != null) {
	        // sixth choice
	        String class_ = pop();    // class keyword
	        String dot = pop();    // .
	        String typeTypeOrVoid = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(typeTypeOrVoid).append(dot).append(class_);
	        push(sb);
	    }
	    else if (ctx.nonWildcardTypeArguments() != null) {
	        // last choice
	        String end = "";
	        if (ctx.THIS() != null) {
	            String arguments = pop();
	            String this_ = pop();    // this keyword
	            end = this_ + ' ' + arguments;
	        }
	        else if (ctx.explicitGenericInvocationSuffix() != null) {
	            end = pop();
	        }
	        String args = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(args).append(' ').append(end);
	    }
	    // remaining choices are already on the stack    
	}
	
	/**
 	* primitiveType
 	*     : BOOLEAN
 	*     | CHAR
 	*     | BYTE
 	*     | SHORT
 	*     | INT
 	*     | LONG
 	*     | FLOAT
 	*     | DOUBLE
 	*     ;
 	*/
	@Override public void exitPrimitiveType(PrimitiveTypeContext ctx) {
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* qualifiedName
 	*     : identifier ('.' identifier)*
 	*     ;
 	*/
	@Override public void exitQualifiedName(QualifiedNameContext ctx) { 
	    int size = ctx.identifier().size();
	    if (ctx.DOT() != null) {
	        size += ctx.DOT().size();
	    }
	    List<String> parts = reverse(size);
	    StringBuilder qualifiedName = new StringBuilder();
	    for (int i = 0; i < size; i++) {
	        qualifiedName.append(parts.get(i));
	    }
	    push(qualifiedName);
	}
	
	/**
 	* qualifiedNameList
 	*     : qualifiedName (',' qualifiedName)*
 	*     ;
 	*/
	@Override public void exitQualifiedNameList(QualifiedNameListContext ctx) { 
	    int size = ctx.qualifiedName().size();
	    push(reverse(size * 2 - 1, " ", 2));
	}
	
	/**
 	* receiverParameter
 	*     : typeType (identifier '.')* THIS
 	*     ;
 	*/
	@Override public void exitReceiverParameter(ReceiverParameterContext ctx) { 
	    String this_ = pop();    // this keyword
	    String identifiers = "";
	    if (ctx.identifier() != null) {
	        int size = ctx.identifier().size();
	        identifiers = reverse(size * 2, "");
	    }
	    String typeType = pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(typeType).append(identifiers).append(this_);
	    push(sb);
	}
	
	@Override public void enterRecordBody(RecordBodyContext ctx) {
	    ++tabCount;    
	}
	
	
	/**
 	* recordBody
 	*     : '{' classBodyDeclaration* '}'
 	*     ;
 	*/
	@Override public void exitRecordBody(RecordBodyContext ctx) {
	    --tabCount;
	    String rb = pop().trim();    // }
	    rb = indent(rb);
        String classBodyDeclaration = "";
	    if (ctx.classBodyDeclaration() != null && ctx.classBodyDeclaration().size() > 0) {
	        int size = ctx.classBodyDeclaration().size();
	        classBodyDeclaration = reverse(size, "");
	        classBodyDeclaration = removeBlankLines(classBodyDeclaration, START);
	    }
	    String lb = pop().trim();    // {
	    if (bracketStyle == BROKEN) {
	        lb = new StringBuilder().append('\n').append(indent(lb)).toString();    
	    }
	    else {
	        lb = lb.trim();    
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(lb).append('\n');
	    if (!classBodyDeclaration.isEmpty()) {
	        sb.append(classBodyDeclaration);
	        if (!classBodyDeclaration.endsWith("\n")) {
	            sb.append('\n');    
	        }
	    }
	    sb.append(rb).append('\n');
	    push(sb);
	}
	
	/**
 	* recordComponent
 	*     : typeType identifier
 	*     ;
 	*/
	@Override public void exitRecordComponent(RecordComponentContext ctx) { 
	    String identifier = pop();
	    String typeType = pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(typeType).append(' ').append(identifier);
	    push(sb);
	}
	
	/**
 	* recordComponentList
 	*     : recordComponent (',' recordComponent)*
 	*     ;
 	*/
	@Override public void exitRecordComponentList(RecordComponentListContext ctx) {
	    int size = ctx.recordComponent().size();
	    push(reverse(size * 2 - 1, " ", 2));
	}
	
	/**
 	* recordDeclaration
 	*     : RECORD identifier typeParameters? recordHeader
 	*       (IMPLEMENTS typeList)?
 	*       recordBody
 	*     ;
 	*/
	@Override public void exitRecordDeclaration(RecordDeclarationContext ctx) { 
	    String recordBody = pop();
	    String impl = "";
	    if (ctx.IMPLEMENTS() != null) {
	         String typeList = pop();
	         String implements_ = pop();    // implements keyword
	         impl = implements_ + ' ' + typeList;
	    }
	    String recordHeader = pop().trim();
	    String typeParameters = ctx.typeParameters() == null ? "" : pop();
	    String identifier = pop();
	    String record_ = pop();    // record keyword
	    record_ = indent(record_);
	    StringBuilder sb = new StringBuilder();
	    sb.append(record_).append(' ').append(identifier);
	    if (typeParameters.isEmpty()) {
	        sb.append(typeParameters);
	    }
	    sb.append(recordHeader);
	    sb.append(' ');
	    if (!impl.isEmpty()) {
	        sb.append(impl).append(' ');
	    }
	    sb.append(recordBody);
	    push(sb);
	}
	
	/**
 	* recordHeader
 	*     : '(' recordComponentList? ')'
 	*     ;
 	*/
	@Override public void exitRecordHeader(RecordHeaderContext ctx) {
	    String rp = pop().trim();    // )
	    String list = ctx.recordComponentList() == null ? "" : pop().trim();
	    String lp = pop().trim();    // (
	    StringBuilder sb = new StringBuilder();
	    if (list.isEmpty()) {
	        sb.append(lp).append(rp);   
	    }
	    else {
	        sb.append(padParen(lp)).append(list).append(padParen(rp));
	    }
	    push(sb);
	}
	
	/**
 	* requiresModifier
 	* 	: TRANSITIVE
 	* 	| STATIC
 	* 	;
 	*/
	@Override public void exitRequiresModifier(RequiresModifierContext ctx) { 
	    // Nothing to do here, one or more of the choices should already be on the stack
	}
	
	/**
 	* resource
 	*     : variableModifier* ( classOrInterfaceType variableDeclaratorId | VAR identifier ) '=' expression
 	*     | identifier
 	*     ;
 	*/
	@Override public void exitResource(ResourceContext ctx) { 
	    if (ctx.expression() != null) {
	        // first choice   
	        String expression = pop().trim();
	        String equals = pop().trim();    // =
	        String middle = "";
	        if (ctx.VAR() != null) {
	            String identifier = pop();
	            String var_ = pop();    // var keyword
	            middle = var_ + ' ' + identifier;
	        }
	        else {
	            String id = pop().trim();
	            String type = pop().trim();
	            middle = type + ' ' + id;
	        }
            String modifiers = "";
            if (ctx.variableModifier() != null) {
                int size = ctx.variableModifier().size();
                modifiers = formatModifiers(size, true).trim();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(modifiers).append(' ').append(middle).append(padOperator(equals)).append(expression);
            push(sb);
	    }
	    // otherwise, 'identifier' is already on the stack
	}
	
	/**
 	* resourceSpecification
 	*     : '(' resources ';'? ')'
 	*     ;
 	* Multiple resources are already on separate lines;
 	*/
	@Override public void exitResourceSpecification(ResourceSpecificationContext ctx) { 
	    String rp = pop();    // )
	    String semi = ctx.SEMI() == null ? "" : pop();
	    String resources = pop();
	    String lp = pop();    // (

	    StringBuilder sb = new StringBuilder();
	    sb.append(padParen(lp));
	    long lines = resources.lines().count();
	    if (lines > 1) {
            sb.append('\n');
            ++tabCount;
            resources = indent(resources);
            --tabCount;
            sb.append(resources).append(semi);
            if (!semi.endsWith("\n")) {
                sb.append('\n');   
            }
            rp = indent(rp);
            sb.append(rp);
	    }
	    else {
	         sb.append(resources.trim());
	         sb.append(padParen(rp));
	    }
	    push(sb);
	}
	
	/**
 	* resources
 	*     : resource (';' resource)*
 	*     ;
 	* If there are more than one resource, put them on separate lines.
 	*/
	@Override public void exitResources(ResourcesContext ctx) {
	    int size = ctx.resource().size();
	    if (size > 1) {
            StringBuilder sb = new StringBuilder();
            List<String> parts = reverse(size * 2 - 1);
            for (String part : parts) {
                if (part.equals(";")) {
                    sb.append(part);
                    sb.append('\n');
                }
                else {
                    part = indent(part);
                    sb.append(part);
                }
            }
            push(sb);
	    }
	    // otherwise, the single resource is already on the stack
	}
	
    @Override public void enterStatement(StatementContext ctx) {
        if (ctx.switchExpression() != null) {
            // do not increment the tabCount here. Note that ctx.SWITCH will not
            // be null in this case
            return;
        }
        else if (ctx.SWITCH() != null) {
            ++tabCount;   
        }
    }
	
	/**
 	* 	statement
 	*     : blockLabel=block
 	*     | ASSERT expression (':' expression)? ';'
 	*     | IF parExpression statement (ELSE statement)?
 	*     | FOR '(' forControl ')' statement
 	*     | WHILE parExpression statement
 	*     | DO statement WHILE parExpression ';'
 	*     | TRY block (catchClause+ finallyBlock? | finallyBlock)
 	*     | TRY resourceSpecification block catchClause* finallyBlock?
 	*     | SWITCH parExpression '{' switchBlockStatementGroup* switchLabel* '}'
 	*     | SYNCHRONIZED parExpression block
 	*     | RETURN expression? ';'
 	*     | THROW expression ';'
 	*     | BREAK identifier? ';'
 	*     | CONTINUE identifier? ';'  
 	*     | YIELD expression ';' // Java17
 	*     | SEMI
 	*     | statementExpression=expression ';'
 	*     | switchExpression ';'? // Java17   
 	*     | identifierLabel=identifier ':' statement
 	*     ;
 	*/
    @Override public void exitStatement(StatementContext ctx) { 
        if (ctx.ASSERT() != null) {
            formatAssert(ctx);
        }
        else if (ctx.IF() != null) {
            formatIf(ctx);
        }
        else if (ctx.FOR() != null) {
            formatFor();
        }
        else if (ctx.DO() != null && ctx.WHILE() != null) {
            formatDo();
        }
        else if (ctx.WHILE() != null) {
            formatWhile();
        }
        else if (ctx.TRY() != null) {
            formatTry(ctx);
        }
        else if (ctx.SWITCH() != null) {
            --tabCount;
            formatSwitch(ctx);
        }
        else if (ctx.switchExpression() != null) {
            formatSwitchExpression(ctx);
        }
        else if (ctx.SYNCHRONIZED() != null) {
            formatSynchronized(ctx);
        }
        else if (ctx.RETURN() != null || ctx.THROW() != null || ctx.YIELD() != null) {
            formatReturnThrowYield(ctx);
        }
        else if (ctx.BREAK() != null || ctx.CONTINUE() != null) {
            formatBreakOrContinue(ctx);
        }
        else if (ctx.SEMI() != null) {
            formatSemi(ctx);
        }
        else if (ctx.COLON() != null) {
            formatColon();
        }
        // block is the only remaining choice, and it should already be on the stack
    }
    
    private void formatAssert(StatementContext ctx) {
        // ASSERT expression (':' expression)? ';'
        String semi = pop().trim();  // ;
        String expression = "";
        String colon = "";
        String expression2 = "";
        if (ctx.expression().size() > 1 && ctx.COLON() != null) {
            expression2 = pop().trim();
            colon = pop().trim();  // :
        }
        expression = pop().trim();
        String assert_ = pop();  // assert keyword
        assert_ = indent(assert_);
        StringBuilder sb = new StringBuilder();
        sb.append(assert_).append(' ').append(expression);
        if (!colon.isEmpty() && !expression2.isEmpty()) {
            sb.append(padOperator(colon)).append(expression2);
        }
        sb.append(semi);   
        push(sb);
    }
    
    private void formatIf(StatementContext ctx) {
        // IF parExpression statement (ELSE statement)?
        StringBuilder elseStatement = new StringBuilder();
        if (ctx.ELSE() != null) {
            String es = pop().trim();
            String else_ = indent(pop().trim());  // else keyword
            elseStatement.append(else_).append(' ');
            
            // require brackets unless it's an "else if" statement
            if (!es.startsWith("{") && !es.startsWith("if")) {
                if (bracketStyle == BROKEN) {
                    elseStatement.append('\n').append(indent("{")).append('\n');
                }
                else {
                    elseStatement.append("{\n");
                }
                ++tabCount;
                elseStatement.append(indent(es)).append('\n');
                --tabCount;
                elseStatement.append(indent("}")).append('\n');
            }
            else {
                elseStatement.append(es);
            }
        }
        
        String ifStatement = pop().trim();
        String parExpression = pop();
        String if_ = pop();  // if keyword
        
        StringBuilder sb = new StringBuilder();
        sb.append(indent(if_)).append(' ').append(parExpression).append(' ');
        
        // require brackets on ifStatement unless the statement is just ";"
        if (!ifStatement.startsWith("{") && !ifStatement.equals(";")) {
            if (bracketStyle == BROKEN) {
                sb.append('\n').append(indent("{")).append('\n');    
            }
            else {
                sb.append("{\n");
            }
            ++tabCount;
            sb.append(indent(ifStatement)).append('\n');
            --tabCount;
            sb.append(indent("}")).append('\n');
        }
        else {
            sb.append(ifStatement).append('\n');
        }
        
        sb.append(elseStatement);
        if (!endsWith(sb, "\n") && breakElse) {
            sb.append('\n');   
        }
        push(sb);
    }
    
    private void formatFor() {
        // FOR '(' forControl ')' statement 
        String statement = pop().trim();
        
        // always require brackets
        if (!statement.startsWith("{")) {
            StringBuilder sb = new StringBuilder();
            if (bracketStyle == BROKEN) {
                sb.append('\n').append(indent("{")).append('\n');    
            }
            else {
                sb.append(" {\n");
            }
            ++tabCount;
            sb.append(indent(statement)).append('\n');
            --tabCount;
            sb.append(indent("}"));
            statement = sb.toString();     
        }
        
        String rp = pop();  // )
        String forControl = pop();
        String lp = pop();  // (
        String for_ = pop();
        for_ = indent(for_);
        
        StringBuilder sb = new StringBuilder();
        sb.append(for_).append(' ').append(padParen(lp)).append(forControl).append(padParen(rp)).append(' ').append(statement);
        if (!statement.endsWith("\n")) {
            sb.append('\n');
        }
        push(sb);
    }
    
    private void formatWhile() {
        // WHILE parExpression statement
        String statement = pop().trim();
        
        // always require brackets
        if (!statement.startsWith("{")) {
            StringBuilder sb = new StringBuilder();
            if (bracketStyle == BROKEN) {
                sb.append('\n').append(indent("{")).append('\n');
                ++tabCount;
                sb.append(indent(statement)).append('\n');
                --tabCount;
                statement = sb.toString();
            }
            else {
                sb.append(" {\n");    
                ++tabCount;
                sb.append(indent(statement)).append('\n');
                --tabCount;
            }
            sb.append(indent("}"));
            statement = sb.toString();
        }
        
        String parExpression = pop();
        String while_ = pop().trim();  // while keyword
        while_ = indent(while_);
        StringBuilder sb = new StringBuilder();
        sb.append(while_).append(' ').append(parExpression).append(' ').append(statement);
        push(sb);
    }
    
    private void formatDo() {
        // DO statement WHILE parExpression ';'
        String semi = pop();  // ;
        String parExpression = pop();
        String while_ = indent(pop());  // while keyword
        String statement = pop().trim();
        
        // always require brackets
        if (!statement.startsWith("{")) {
            StringBuilder sb = new StringBuilder();
            if (bracketStyle == BROKEN) {
                sb.append('\n').append(indent("{")).append('\n');
                ++tabCount;
                sb.append(indent(statement)).append('\n');
                --tabCount;
                statement = sb.toString();
            }
            else {
                sb.append(" {\n");    
                ++tabCount;
                sb.append(indent(statement)).append('\n');
                --tabCount;
            }
            sb.append(indent("}"));
            statement = sb.toString();
        }
        String do_ = pop();  // do keyword
        do_ = indent(do_);
        StringBuilder sb = new StringBuilder();
        sb.append(do_).append(' ').append(statement).append(' ').append(while_).append(' ').append(parExpression).append(semi);
        push(sb);
    }
    
    private void formatTry(StatementContext ctx) {
        // TRY block (catchClause+ finallyBlock? | finallyBlock)
        // TRY resourceSpecification block catchClause* finallyBlock?
        if (ctx.resourceSpecification() != null) {
            // second choice
            String finallyBlock = ctx.finallyBlock() == null ? "" : pop();
            String catchClause = "";
            if (ctx.catchClause() != null) {
                int size = ctx.catchClause().size();
                catchClause = reverse(size, "");
            }
            String block = pop().trim();
            String spec = pop();
            String try_ = pop();  // try keyword
            try_ = indent(try_);
            StringBuilder sb = new StringBuilder();
            sb.append(try_).append(' ').append(spec).append(block);
            if (!block.endsWith("\n")) {
                sb.append('\n');
            }
            sb.append(catchClause);
            if (!catchClause.endsWith("\n")) {
                sb.append('\n');
            }
            sb.append(finallyBlock);
            String tryStatement = sb.toString();
            tryStatement = removeBlankLines(tryStatement, BOTH);
            push(tryStatement);
        }
        else {
            String catchClause = "";
            String finallyBlock = "";
            if (ctx.catchClause() != null) {
                finallyBlock = ctx.finallyBlock() == null ? "" : pop();
                int size = ctx.catchClause().size();
                catchClause = reverse(size, "");
            }
            else {
                finallyBlock = pop();   
            }
            String block = pop();
            if (bracketStyle == BROKEN) {
                block = new StringBuilder().append('\n').append(block).toString();    
            }
            else {
                block = block.trim();    
            }
            
            String try_ = pop().trim();  // try keyword
            try_ = indent(try_);
            StringBuilder sb = new StringBuilder();
            sb.append(try_).append(' ').append(block);
            if (!catchClause.isEmpty()) {
                sb.append('\n').append(catchClause);
            }
            if (!finallyBlock.isEmpty()) {
                finallyBlock = removeBlankLines(finallyBlock, BOTH);
                sb.append('\n').append(finallyBlock);
            }
            push(sb);
        }
    }
    
    private void formatSwitch(StatementContext ctx) {
        // SWITCH parExpression '{' switchBlockStatementGroup* switchLabel* '}'
        String rp = pop().trim();  // }
        rp = indent(rp);
        String switchLabel = "";
        if (ctx.switchLabel() != null && ctx.switchLabel().size() > 0) {
            int size = ctx.switchLabel().size();
            switchLabel = reverse(size, "");
        }
        String switchBlockStatementGroup = "";
        if (ctx.switchBlockStatementGroup() != null && ctx.switchBlockStatementGroup().size() > 0) {
            int size = ctx.switchBlockStatementGroup().size();
            switchBlockStatementGroup = reverse(size, "");  
        }
        String lp = pop();  // {
	    if (bracketStyle == BROKEN) {
	        lp = new StringBuilder().append('\n').append(lp).toString();    
	    }
	    else {
	        lp = lp.trim();    
	    }
        String parExpression = pop();
        String switch_ = pop().trim();  // switch keyword
        switch_ = indent(switch_);
        StringBuilder sb = new StringBuilder();
        sb.append(switch_).append(' ').append(parExpression).append(' ').append(lp).append('\n').append(switchBlockStatementGroup).append(switchLabel).append(rp).append('\n');
        push(sb);
    }
    
    private void formatSwitchExpression(StatementContext ctx) {
        // switchExpression ';'? // Java17
        String semi = "";
        if (ctx.SEMI() != null) {
            semi = pop().trim();
        }
        String expression = pop();
        StringBuilder sb = new StringBuilder();
        sb.append(expression);
        sb.append(semi);
        push(sb);
    }
    
    private void formatSynchronized(StatementContext ctx) {     // NOPMD
        // SYNCHRONIZED parExpression block
        String block = pop();
	    if (bracketStyle == BROKEN) {
	        block = new StringBuilder().append('\n').append(block).toString();    
	    }
	    else {
	        block = block.trim();    
	    }
        String parExpression = pop().trim();
        String synchronized_ = pop().trim();  // synchronized keyword
        synchronized_ = indent(synchronized_);
        StringBuilder sb = new StringBuilder();
        sb.append(synchronized_).append(' ').append(parExpression).append(block);
        if (!block.endsWith("\n")) {
            sb.append('\n');
        }
        push(sb);
    }
    
    private void formatReturnThrowYield(StatementContext ctx) {
        // these 3 are pretty much the same
        // RETURN expression? ';'
        // THROW expression ';'
        // YIELD expression ';'            
        String semi = pop().trim();  //;
        String expression = "";
        if (ctx.expression() != null && ctx.expression().size() > 0) {
            expression = pop().trim();    
        }
        String keyword = pop().trim();
        keyword = indent(keyword);
        StringBuilder sb = new StringBuilder();
        sb.append(keyword);
        if (!expression.isEmpty()) {
            sb.append(' ');
        }
        sb.append(expression).append(semi);
        push(sb);
    }
    
    private void formatBreakOrContinue(StatementContext ctx) {
        // BREAK identifier? ';'
        // CONTINUE identifier? ';' 
        String semi = pop().trim(); 
        String identifier = ctx.identifier() == null ? "" : " " + pop().trim();
        String keyword = pop().trim();
        keyword = indent(keyword);
        StringBuilder sb = new StringBuilder();
        sb.append(keyword).append(identifier).append(semi);
        push(sb);
    }
    
    private void formatSemi(StatementContext ctx) {
        // only these 2 statement choices are remaining with a semicolon
        // SEMI
        // statementExpression=expression ';'
        String semi = pop().trim();  // ;
        if (ctx.expression() == null || ctx.expression().size() == 0) {
            push(semi);
            return;
        }

        String expression = pop();
        long lines = expression.lines().count();
        String indent = getIndent();
        if (lines > 1) {
            expression = removeBlankLines(expression, END);
            if (!expression.startsWith(indent)) {
                expression = indent(expression);   
            }
        }
        else {
            if (!expression.startsWith(indent)) {
                expression = new StringBuilder(indent).append(expression).toString();   
            }
            if (!expression.startsWith("\n")) {
                expression = new StringBuilder("\n").append(expression).toString();   
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(expression).append(semi).append('\n');
        push(sb);
    }
    
    private void formatColon() {
        // This is the only statement left with a colon
        // identifierLabel=identifier ':' statement
        // This is a labeled statement, section 14.7 of JLS 17 and should be formatted like:
        // label:
        //     statement
        // where label is outdented one tab and statement is indented with the current
        // tab count indenting
        
        String statement = pop();
        statement = removeBlankLines(statement, BOTH);
        String colon = pop().trim();  // :
        String identifier = pop();
        // indent to current tab count, then outdent one tab
        identifier = indent(identifier);
        identifier = outdent(identifier);
        StringBuilder sb = new StringBuilder();
        sb.append(identifier).append(colon).append('\n').append(statement);
        push(sb);
    }
	
	
    /**
     * superSuffix
     *     : arguments
     *     | '.' typeArguments? identifier arguments?
     *     ;
     */
	@Override public void exitSuperSuffix(SuperSuffixContext ctx) { 
	    if (ctx.DOT() != null && ctx.identifier() != null) {
	        // second choice
	        String arguments = ctx.arguments() == null ? "" : pop();
	        String identifier = pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : pop();
	        String dot = pop();    // .
	        StringBuilder sb = new StringBuilder();
	        sb.append(dot);
	        if (!typeArguments.isEmpty()) {
	            sb.append(typeArguments);   
	        }
	        sb.append(identifier);
	        if (!arguments.isEmpty()) {
	            sb.append(arguments);    
	        }
	        push(sb);
	    }
	    // otherwise, arguments is already on the stack
	}
	
	/**
 	* switchBlockStatementGroup
 	*     : switchLabel+ blockStatement+
 	*     ;
 	*/
	@Override public void exitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) {
	    int size = ctx.blockStatement().size();
	    String blockStatements = reverse(size, "");
	    size = ctx.switchLabel().size();
	    String switchLabels = reverse(size, "");
	    
	    if (blockStatements.trim().startsWith("{")) {
	        // it's a block
	        blockStatements = blockStatements.trim();
	        switchLabels = trimEnd(switchLabels) + ' ';
	    }
	    else if (!switchLabels.endsWith("\n")) {
	        switchLabels = switchLabels + '\n';      // NOPMD
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append(switchLabels);
	    sb.append(blockStatements);
	    if (!blockStatements.endsWith("\n")) {
	        sb.append('\n');       
	    }
	    push(sb);
	}
	
	@Override public void enterSwitchExpression(SwitchExpressionContext ctx) {
	    ++tabCount;
	}
	
	/**
 	* // Java17
 	* switchExpression
 	*     : SWITCH parExpression '{' switchLabeledRule* '}'
 	*     ;
 	*/
	@Override public void exitSwitchExpression(SwitchExpressionContext ctx) {
	    --tabCount;
	    String rb = pop().trim();    // }
	    rb = indent(rb);
	    
	    String switchLabeledRule = "";
	    if (ctx.switchLabeledRule() != null) {
	        int size = ctx.switchLabeledRule().size();
	        switchLabeledRule = reverse(size, "");
	        if (!switchLabeledRule.endsWith("\n")) {
	            switchLabeledRule += '\n';    // NOPMD
	        } 
	    }
	    String lb = pop();    // {
	    if (bracketStyle == BROKEN) {
	        lb = new StringBuilder().append('\n').append(lb).toString();    
	    }
	    else {
	        lb = lb.trim();    
	    }
	    String parExpression = pop();
	    String switch_ = pop();    // switch keyword
	    switch_ = indent(switch_);
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append(switch_).append(' ').append(parExpression).append(' ').append(lb).append('\n').append(switchLabeledRule).append(rb).append('\n');
	    push(sb);
	}
	
	@Override public void enterSwitchLabel(SwitchLabelContext ctx) {
	}
	/**
 	* switchLabel
 	*     : CASE (constantExpression=expression | enumConstantName=IDENTIFIER | typeType varName=identifier) ':'
 	*     | DEFAULT ':'
 	*     ;
 	*/
	@Override public void exitSwitchLabel(SwitchLabelContext ctx) {
	    if (ctx.DEFAULT() != null) {
	        String colon = pop().trim();
	        String default_ = pop();    // default keyword
	        default_ = indent(default_);
	        StringBuilder sb = new StringBuilder();
	        sb.append(default_).append(colon).append('\n');
	        push(sb);
	    }
	    else {
	        String colon = pop().trim();    
	        String label = "";
	        if (ctx.typeType() != null) {
	            String identifier = pop().trim();
	            String typeType = pop().trim();
	            label = new StringBuilder(typeType).append(' ').append(identifier).toString();
	        }
	        else {
	            label = pop().trim();   
	        }
	        String case_ = pop().trim();
	        case_ = indent(case_);
	        StringBuilder sb = new StringBuilder();
	        sb.append(case_).append(' ').append(label).append(colon).append('\n');
	        push(sb);
	    }
	}
	
	
	
	/**
 	* // Java17
 	* switchLabeledRule
 	*     : CASE (expressionList | NULL_LITERAL | guardedPattern) (ARROW | COLON) switchRuleOutcome
 	*     | DEFAULT (ARROW | COLON) switchRuleOutcome
 	*     ;
 	*/
	@Override public void exitSwitchLabeledRule(SwitchLabeledRuleContext ctx) { 
	    if (ctx.CASE() != null) {
	        String switchRuleOutcome = "";
	        if (ctx.switchRuleOutcome() != null) {
	            switchRuleOutcome = pop().trim();
	        }
	        String operator = pop().trim();    // arrow or colon
	        String choice = pop().trim();    // expressionList, null, or guardedPattern
	        String case_ = pop().trim();
	        case_ = indent(case_);
	        StringBuilder sb = new StringBuilder();
	        sb.append(case_).append(' ').append(choice).append(padOperator(operator)).append(switchRuleOutcome).append('\n');
	        push(sb);
	    }
	    else {
	        String switchRuleOutcome = pop().trim();
	        String operator = pop().trim();
	        String default_ = pop().trim();
	        default_ = indent(default_);
	        StringBuilder sb = new StringBuilder();
	        sb.append(default_).append(padOperator(operator)).append(switchRuleOutcome);
	        push(sb);
	    }
	}
	
	
	/**
 	* // Java17
 	* switchRuleOutcome
 	*     : block
 	*     | blockStatement*
 	*     ;
 	*/
	@Override public void exitSwitchRuleOutcome(SwitchRuleOutcomeContext ctx) { 
	    if (ctx.blockStatement() != null && ctx.blockStatement().size() > 0) {
            String blockStatement = "";
            if (ctx.blockStatement() != null) {
                int size = ctx.blockStatement().size();
                blockStatement = reverse(size, " ");
            }
            push(blockStatement.trim());
	    }
	    else if (ctx.block() == null) {
	        // no block and no blockStatements, need a placeholder on the stack,
	        // this could happen like this:
	        // case 1, 2:
	        // case 3, 4:
	        //    blah...
	        // then case 1, 2: doesn't actually have a statement following it
	        push("");   
	    }
	}
	
	
	/**
 	* typeArgument
 	*     : typeType
 	*     | annotation* '?' ((EXTENDS | SUPER) typeType)?
 	*     ;
 	*/
	@Override public void exitTypeArgument(TypeArgumentContext ctx) { 
	    if (ctx.QUESTION() != null) {
	        String typeType = "";
	        if (ctx.typeType() != null) {
	             typeType = pop();
	             typeType = pop() + " " + typeType;    // NOPMD extends or super
	        }
	        
	        String q = pop().trim();    // ?
            String annotation = "";
            if (ctx.annotation() != null) {
                int size = ctx.annotation().size();
                annotation = reverse(size, " ");
            }
            annotation = indent(annotation);
            StringBuilder sb = new StringBuilder();
            sb.append(annotation).append(q).append(typeType);
            push(sb);
	    }
	}
	
	
	/**
 	* typeArguments
 	*     : '<' typeArgument (',' typeArgument)* '>'
 	*     ;
 	*/
	@Override public void exitTypeArguments(TypeArgumentsContext ctx) { 
	    String gt = pop();    // >
	    int size = ctx.typeArgument().size();
	    String typeArguments = reverse(size * 2 - 1, " ", 2);
	    String lt = pop();    // <
	    StringBuilder sb = new StringBuilder();
	    sb.append(lt).append(typeArguments).append(gt);
	    push(sb);
	}
	
	
	/**
 	* typeArgumentsOrDiamond
 	*     : '<' '>'
 	*     | typeArguments
 	*     ;
 	*/
	@Override public void exitTypeArgumentsOrDiamond(TypeArgumentsOrDiamondContext ctx) { 
	    if (ctx.GT() != null) {
	        String gt = pop();
	        String lt = pop();
	        push(lt + gt);   
	    }
	    // otherwise, 'typeArguments' is already on the stack
	}
	
	/**
 	* typeBound
 	*     : typeType ('&' typeType)*
 	*     ;
 	*/
	@Override public void exitTypeBound(TypeBoundContext ctx) { 
	    int size = ctx.typeType().size();
	    String typeBound = reverse(size * 2 - 1, " ");
	    push(typeBound);
	}
	
	/**
 	* typeDeclaration
 	*     : classOrInterfaceModifier*
 	*       (classDeclaration | enumDeclaration | interfaceDeclaration | annotationTypeDeclaration | recordDeclaration)
 	*     | ';'   
 	*     ;
 	*/
	@Override public void exitTypeDeclaration(TypeDeclarationContext ctx) { 
	    if (ctx.SEMI() != null) {
	        // append semi to previous stack item
	        String semi = pop().trim();
	        String previous = pop();
	        previous = trimEnd(previous);
	        StringBuilder sb = new StringBuilder();
	        sb.append(previous).append(semi).append('\n');
	        push(sb);
	        push("\n");    // need a type declaration placeholder since 2 are combined above
	    }
	    else {
            String declaration = pop();
            StringBuilder typeDeclaration = new StringBuilder();
            if (ctx.classOrInterfaceModifier() != null && ctx.classOrInterfaceModifier().size() > 0) {
                int size = ctx.classOrInterfaceModifier().size();
                String mods = formatModifiers(size);    // don't trim the modifies, it may be only an annotation, which should have a new line after it
                typeDeclaration.append(mods);
                if (!mods.endsWith("\n")) {
                    typeDeclaration.append(' ');
                }
            }
            typeDeclaration.append(declaration);
            push(typeDeclaration);
	    }
	}
	
	/**
 	* typeList
 	*     : typeType (',' typeType)*
 	*     ;
 	*/
	@Override public void exitTypeList(TypeListContext ctx) { 
	    int size = ctx.typeType().size();
	    String typeType = reverse(size * 2 - 1, " ", 2);
	    push(typeType);
	}
	
	
	/**
 	* typeParameter
 	*     : annotation* identifier (EXTENDS annotation* typeBound)?
 	*     ;
 	*/
	@Override public void exitTypeParameter(TypeParameterContext ctx) {
	    String extends_ = "";
	    if (ctx.EXTENDS() != null) {
	        String typeBound = pop();
            StringBuilder annotations = new StringBuilder();
            while(stack.peek().startsWith("@")) {
                annotations.insert(0, pop()).append(' ');
            }
	        extends_ = pop();    // extends keyword
	        annotations.append(typeBound);
	        annotations.insert(0, ' ');
	        annotations.insert(0, extends_);
	        annotations.insert(0, ' ');
	        extends_ = annotations.toString();
	    }
	    String identifier = pop();
        StringBuilder annotations = new StringBuilder();
        while(stack.peek().startsWith("@")) {
            annotations.insert(0, pop()).append(' ');
        }
        annotations.append(identifier).append(extends_);
        push(annotations);
	}
	
	
	/**
 	* typeParameters
 	*     : '<' typeParameter (',' typeParameter)* '>'
 	*     ;
 	*/
	@Override public void exitTypeParameters(TypeParametersContext ctx) { 
	    String gt = pop();    // >
	    int size = ctx.typeParameter().size();
	    String typeParameter = reverse(size * 2 - 1, " ", 2);
	    String lt = pop();    // <
	    StringBuilder sb = new StringBuilder();
	    sb.append(lt).append(typeParameter).append(gt);
	    push(sb);
	}
	
	/**
 	* typeType
 	*     : annotation* (classOrInterfaceType | primitiveType) (annotation* '[' ']')*
 	*     ;
 	*/
	@Override public void exitTypeType(TypeTypeContext ctx) {
	    int annotationCount = 0;    // keep track of how many annotations have been popped
	    
	    // handle the end part, (annotation* '[' ']')*
	    StringBuilder endPart = new StringBuilder();
	    if (ctx.RBRACK() != null && ctx.LBRACK() != null && ctx.RBRACK().size() > 0 && ctx.LBRACK().size() > 0) {
	        for (int i = 0; i < ctx.RBRACK().size(); i++) {
	            // get the brackets
	            endPart.insert(0, pop());    // ]
	            endPart.insert(0, pop());    // [
	            
                // any annotations?
                if (ctx.annotation() != null) {
                    for (int j = 0; j < ctx.annotation().size(); j++) {
                        String ann = stack.peek();
                        if (ann.indexOf('@') > -1) {    
                            // using indexOf to be able to check for altAnnotationQualifiedName
                            // as well as regular annotation
                            endPart.append(pop().trim()).append(' ');    // annotation
                            ++ annotationCount;
                        }
                        else {
                            break;   
                        }
                    }
                }
	        }
	    }
	    String type = pop();    // one of (classOrInterfaceType | primitiveType)
	    // handle the rest of the annotations, if any
	    StringBuilder annotations = new StringBuilder();
	    if (ctx.annotation() != null) {
	        List<String> anns = reverse(ctx.annotation().size() - annotationCount);
	        for (String a : anns) {
	            annotations.append(a.trim()).append(' ');
	        }
	    }
	    
	    // put it all together
	    StringBuilder sb = new StringBuilder();
	    sb.append(annotations).append(type);
	    if (endPart.length() > 0) {
	        sb.append(endPart);
	    }
	    push(sb);
	}
	
	
	/**
 	* typeTypeOrVoid
 	*     : typeType
 	*     | VOID
 	*     ;
 	*/
	@Override public void exitTypeTypeOrVoid(TypeTypeOrVoidContext ctx) {
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* variableDeclarator
 	*     : variableDeclaratorId ('=' variableInitializer)?
 	*     ;
 	*/
	@Override public void exitVariableDeclarator(VariableDeclaratorContext ctx) {
	    String variableInitializer = "";
	    String equals = "";
	    if (ctx.variableInitializer() != null && ctx.ASSIGN() != null) {
	        variableInitializer = pop().trim();
	        if (variableInitializer.length() > wrapLineLength) {
	            variableInitializer = wrapLongLine(variableInitializer);  
	            variableInitializer = indent(variableInitializer);
	        }
	        equals = pop().trim();
	    }
	    String variableDeclId = stack.pop();
	    
	    StringBuilder variableDecl = new StringBuilder();
	    variableDecl.append(variableDeclId);
	    if (!equals.isEmpty() && !variableInitializer.isEmpty()) {
	        variableDecl.append(padOperator(equals));
	        variableDecl.append(variableInitializer);
	    }
	    
	    push(variableDecl);
	}
	
	/**
 	* variableDeclaratorId
 	*     : identifier ('[' ']')*
 	*     ;
 	*/
	@Override public void exitVariableDeclaratorId(VariableDeclaratorIdContext ctx) { 
	    int size = 0;
	    StringBuilder variableDeclId = new StringBuilder();
	    if (ctx.LBRACK() != null) {
	        size = ctx.LBRACK().size() + ctx.RBRACK().size();
	        List<String> parts = reverse(size);
	        for (String part : parts) {
	            variableDeclId.append(part);   
	        }
	    }
	    String identifier = pop();
	    variableDeclId.insert(0, identifier);
	    push(variableDeclId);
	}
	
	
	/**
 	* variableDeclarators
 	*     : variableDeclarator (',' variableDeclarator)*
 	*     ;
 	*/
	@Override public void exitVariableDeclarators(VariableDeclaratorsContext ctx) { 
	    int size = ctx.variableDeclarator().size();
	    if (size == 1) {
	        return;    // only have one variableDeclarator and it's already on the stack   
	    }
	    String variableDecls = reverse(size * 2 - 1, " ", 2);    
	    push(variableDecls);
	}
	
	/**
 	* variableInitializer
 	*     : arrayInitializer
 	*     | expression
 	*     ;
 	*/
	@Override public void exitVariableInitializer(VariableInitializerContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* variableModifier
 	*     : FINAL
 	*     | annotation
 	*     ;
 	*/
	@Override public void exitVariableModifier(VariableModifierContext ctx) { 
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	@Override public void visitErrorNode(ErrorNode node) { }
	
	@Override public void visitTerminal(TerminalNode node) { 
        String terminalText = node.getText().trim();
        push(terminalText);
        processComments(node);
        processWhitespace(node);
	}


/*
--------------------------------------------------------------------------------
Formatting settings
--------------------------------------------------------------------------------
*/
//{{{
    public void setIndentWidth(int i) {
        tabWidth = i;
    }

    public void setUseSoftTabs(boolean b) {
        softTabs = b;
    }
            
    public void setBracketStyle(int style) {
        bracketStyle = style;
        brokenBracket = BROKEN == style;
    }
    
    public void setBreakElse(boolean b) {
        breakElse = b;
    }
    
    public void setPadParens(boolean pad) {
        padParens = pad; 
    }
    
    public void setPadOperators(boolean pad) {
        padOperators = pad;   
    }
    
    public void setBlankLinesBeforePackage(int lines) {
        blankLinesBeforePackage = lines;    
    }
    
    public void setBlankLinesAfterPackage(int lines) {
        blankLinesAfterPackage = lines;
    }
    
    public void setBlankLinesAfterImports(int lines) {
        blankLinesAfterImports = lines;
    }

    public void setSortImports(boolean sort) {
        sortImports = sort;
    }
    
    public void setGroupImports(boolean group) {
        groupImports = group;
    }
    
    public void setBlankLinesBetweenImportGroups(int lines) {
        blankLinesBetweenImportGroups = lines;
    }
    
    public void setBlankLinesAfterClassBody(int lines) {
        blankLinesAfterClassBody = lines;
    }
    
    public void setBlankLinesBeforeMethods(int lines) {
        blankLinesBeforeMethods = lines;
    }
    
    public void setBlankLinesAfterMethods(int lines) {
        blankLinesAfterMethods = lines;
    }
    
    public void setSortModifiers(boolean sort) {
        sortModifiers = sort;
    }
    
    public void setCollapseMultipleBlankLinesTo(int lines) {
        collapseMultipleBlankLinesTo = lines;        
    }
    
    public void setWrapLongLineLength(int length) {
        wrapLineLength = length;
    }
    
//}}}    
/*
--------------------------------------------------------------------------------
Formatting methods.
--------------------------------------------------------------------------------
*/
//{{{    
    /**
     * @return The formatted string for the file contents.    
     */
    public String getText() {
        return output.toString();   
    }
    
    /**
     * @return A string with the current indent amount.   
     */
    private String getIndent() {
        StringBuilder sb = new StringBuilder();    
        for ( int i = 0; i < tabCount; i++ ) {
            sb.append( tab );
        }
        return sb.toString();
    }
    
    /**
     * Add tabCount tabs to the beginning of the given string builder.
     */
    private void indent(StringBuilder sb) {
        if (sb == null) {
            sb = output;   
        }
        for ( int i = 0; i < tabCount; i++ ) {
            sb.insert(0, tab );
        }
    }
    
    
    /**
     * Split the given string into lines, trim each line, then add tabcount * tab
     * whitespace to the start of each line and a new line at the end of each line.
     */
    private String indent(String s) {
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        String indent = getIndent();
        for (String line : lines) {
            sb.append(indent);
            line = line.trim();
            if (line.startsWith("*")) {
                // odds are extremely high that this is a comment line
                sb.append(' ');
            }
            sb.append(line).append('\n');
        }
        return trimEnd(sb);
    }
    
    /**
     * Assumes the given string is already indented but needs one additional indentation.    
     */
    private String indentAgain(String s) {
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        for (String line : lines) {
            sb.append(tab).append(line).append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Removes one tab from the beginning of each line in <code>s</code>.    
     */
    private String outdent(String s) {
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        for (String line : lines) {
            if (line.startsWith(tab)) {
                line = line.substring(tab.length());    
            }
            sb.append(line);
            if (!line.endsWith("\n")) {
                sb.append('\n');    
            }
        }
        if (!s.endsWith("\n")) {
            sb.deleteCharAt(sb.length() - 1);   
        }
        return sb.toString();
    }
    
    private String addBlankLines(StringBuilder sb, int howMany, int whichEnd) {
        return addBlankLines(sb.toString(), howMany, whichEnd);   
    }
    
    /**
     * Add blank lines to the given string. If <code>whichEnd</code> is START, this
     * just adds <code>howMany</code> new line characters to the start of the string.
     * If <code>whichEnd</code> is END, then this first checks for a trailing new 
     * line character, adds one if needed, then adds <code>howMany</code> new line
     * characters to the end of the string.
     */
    private String addBlankLines(String s, int howMany, int whichEnd) {
        if (s == null || s.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < howMany; i++) {
                sb.append('\n');    
            }
            return sb.toString();
        }
        
        // ensure the given string ends with a new line so that subsequent lines
        // will actually be blank lines
        if (whichEnd == END && !s.endsWith("\n")) {
            ++howMany;   
        }
        
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < howMany; i++) {
            lines.append('\n');    
        }
        
        StringBuilder sb = new StringBuilder();
        switch(whichEnd) {
            case START:
                sb.append(lines);
                sb.append(s);
                break;
            case END:
                sb.append(s);
                sb.append(lines);
                break;
            case BOTH:
                sb.append(lines);
                sb.append(s);
                sb.append(lines);
                break;
        }
        return sb.toString();
    }
    
    /**
     * Remove blank lines from the ends of the given string.
     * @param s The string to remove blank lines from.
     * @param whichEnd Which end of the string to remove blank lines from, 
     * one of START, END, or BOTH.
     */
    private String removeBlankLines(String s, int whichEnd) {
        if (s == null || s.isEmpty()) {
            return s;    
        }
        StringBuilder sb = new StringBuilder(s);
        int index;
        switch(whichEnd) {
            case START:
                index = sb.indexOf("\n");
                while (index > -1) {
                    String blank = sb.substring(0, index);
                    if (isWhitespace(blank)) {
                        sb.delete(0, index + 1);
                        index = sb.indexOf("\n");
                    }
                    else {
                        break;
                    }
                }
                break;
            case END:
                index = sb.lastIndexOf("\n");
                while (index > -1) {
                    String blank = sb.substring(index, sb.length());
                    if (isWhitespace(blank)) {
                        sb.delete(index, sb.length());   
                        index = sb.lastIndexOf("\n");
                    }
                    else {
                        break;
                    }
                }
                break;
            case BOTH:
            default:
                s = removeBlankLines(s, START);
                s = removeBlankLines(s, END);
                sb = new StringBuilder(s);
                break;
        }
        return sb.toString();
    }
    
    /**
     * @return A string with the appropriate amount of blank lines.    
     */
    private String getBlankLines(int howMany) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < howMany; i++) {
            sb.append('\n');    
        }
        return sb.toString();
    }
    
    /**
     * Pads parenthesis so "(" becomes "( " and ")" becomes " )".
     * @param paren The paren to pad
     */
    private String padParen(String paren) {
        return padParen(paren, " ");    
    }
    
    /**
     * Pads parenthesis so "(" becomes "( " and ")" becomes " )".
     * @param paren The paren to pad
     * @param item The item immediately following or preceding the paren.
     */
    private String padParen(String paren, String item) {
        if (paren == null || paren.isEmpty() || !padParens || item == null || item.isEmpty()) {
            return paren;    
        }
        StringBuilder sb = new StringBuilder(paren);
        int index = sb.indexOf("(");
        if (index > -1) {
            sb.insert(index + 1, " ");
        }
        else {
            index = sb.indexOf(")");
            if (index > -1) {
                sb.insert(index == 0 ? 0 : index - 1, " ");
            }
        }
        return sb.toString();
    }
    
    /**
     * StringBuilder doesn't have an "endsWith" method.
     * @param sb The string builder to test.
     * @param s The string to test for.
     * @return true if the string builder ends with <code>s</code>.
     */
    public boolean endsWith(StringBuilder sb, String s) {
        if (sb == null && s == null)
            return true;
        if (sb == null && s != null)
            return false;
        if (sb.length() < s.length())
            return false;
        String end = sb.substring(sb.length() - s.length());
        return end.equals(s);
    }
    
    /**
     * StringBuilder doesn't have a "trim" method. This trims whitespace from
     * the end of the string builder. Whitespace on the front is not touched.
     * There are two ways to use this, pass in a StringBuilder then use the same
     * StringBuilder, it's end will have been trimmed, or pass in a StringBuilder
     * and use the returned String.
     * @param sb The StringBuilder to trim.
     * @return The trimmed string.
     */
    public String trimEnd(StringBuilder sb) {
        if (sb == null || sb.length() == 0) {
            return "";
        }
        while(sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    /**
     * Removes whitespace from the end of a string.
     * @param s The string to trim.
     * @return The trimmed string.
     */
    public String trimEnd(String s) {
        return trimEnd(new StringBuilder(s));   
    }
    
    /**
     * StringBuilder doesn't have a "trim" method. This trims whitespace from
     * the start of the string builder. Whitespace on the end is not touched.
     * There are two ways to use this, pass in a StringBuilder then use the same
     * StringBuilder, it's start will have been trimmed, or pass in a StringBuilder
     * and use the returned String.
     * @param sb The StringBuilder to trim.
     * @return The trimmed string.
     */
    public String trimFront(StringBuilder sb) {
        if (sb == null || sb.length() == 0) {
            return "";
        }
        while(sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }
    
    /**
     * Removes whitespace from the start of a string.
     * @param s The string to trim.
     * @return The trimmed string.
     */
    public String trimFront(String s) {
        return trimFront(new StringBuilder(s));    
    }
    
    /**
     * Pops the first item off the top of the stack.    
     */
    public String pop() {
        return stack.pop();
    }
    
    /**
     * Pops <code>howMany</code> items off of the stack.    
     */
    public void pop(int howMany) {
        for (int i = 0; i < howMany; i++) {
            stack.pop();   
        }
    }
    
    public void push(String s) {
        stack.push(s);   
    }
    
    public void push(StringBuilder sb) {
        stack.push(sb.toString());   
    }
    
    /**
     * Pops 'howMany' items off of the stack, reverses the order, then
     * assembles a string using 'separator' between the items. A separator is
     * not appended to the end of the string, and the separator is only inserted
     * every 'howOften' items. For example:
     * Given "c,b,a" on the stack, where each character is a separate string on the stack,
     * calling <code>reverse(5, " ", 2)</code> would return "a, b, c".
     * @param howMany How many items to pop off of the stack and assemble into a string.
     * @param separator A string to be placed between each item. The separator is only
     * added if the item does not already end with the separator.
     * @param howOften How often a separator should be inserted.
     * @return A string with the items reversed and separated.
     */
    private String reverse(int howMany, String separator, int howOften) {
        StringBuilder sb = new StringBuilder();
	    List<String> list = reverse(howMany);
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            sb.append(item);
            if (i < list.size() - 1) {
                if ((howOften == 1 || i % howOften == 1) && !item.endsWith(separator)) {    // NOPMD
                    sb.append(separator); 
                }
            }
        }
	    return sb.toString();
    }
    
    /**
     * Pops 'howMany' items off of the stack, reverses the order, then
     * assembles a string using 'separator' between the items. A separator is
     * not appended to the end of the string, and the separator is inserted
     * each item popped off of the stack.
     * Given "a,b,c" on the stack, where each character is a separate string on the stack,
     * calling <code>reverse(5, " ")</code> would return "a , b , c".
     * @param howMany How many items to pop off of the stack and assemble into a string.
     * @param separator A string to be placed between each item.
     * @return A string with the items reversed and separated.
     */
    private String reverse(int howMany, String separator) {
        return reverse(howMany, separator, 1);
    }
    
    /**
     * Pops 'howMany' items off of the stack, puts the items into a list, and reverses
     * the list. 
     * @param howMany How many items to pop off of the stack to add to the list.
     * @return A list of stack items in reverse order.
     */
    private List<String> reverse(int howMany) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < howMany && !stack.isEmpty(); i++) {
            list.add(pop());    
        }
        Collections.reverse(list);
        return list;
    }
    
    /**
     * All comments are handled here.    
     */
	private void processComments(TerminalNode node) {
        Token token = node.getSymbol(); 
        int tokenIndex = token.getTokenIndex();

        // ...
        // End of Line Comments
        // ...
	    // check to the right of the current token for an end of line comment,
	    // this handles both // and /* end of line comments. Only end of line
	    // comments are handled in this section, and such comments are appended
	    // to the end of the previous stack item. All other comments are handled
	    // by looking to the left of the current token and are prepended to the
	    // previous stack item.
        List<Token> commentTokens = tokens.getHiddenTokensToRight(tokenIndex, 2);
        if (commentTokens != null && commentTokens.size() > 0 && token.getType() != Token.EOF) {
            // get the very next comment
            Token nextCommentToken = commentTokens.get(0);
            
            int commentIndex = nextCommentToken.getTokenIndex();
            
            // get the hidden tokens between the current token and the next non-hidden token
            List<Token> hiddenTokens = tokens.getHiddenTokensToRight(tokenIndex);
            
            // check if there is a line ender between the current token and the comment token
            // is it "token \n comment" or "token comment \n"?
            boolean hasLineEnder = false;
            if (hiddenTokens != null && hiddenTokens.size() > 0) {
                for (int i = 0; i < commentIndex - tokenIndex; i++) {
                    Token t = hiddenTokens.get(i);
                    String tokenText = t.getText();
                    if (t.getChannel() == 1 && //JavaLexer.WS &&
                        (tokenText.indexOf('\n') > -1 || tokenText.indexOf('\r') > -1)) {
                        hasLineEnder = true;
                        break;
                    }
                }
                if (!hasLineEnder) {
                    // have 'token comment' now check for line ender after the comment
                    hasLineEnder = false;
                    for (int i = commentIndex - tokenIndex; i < hiddenTokens.size(); i++) {
                        Token t = hiddenTokens.get(i);
                        String tokenText = t.getText();
                        if (t.getChannel() == 1 && //JavaLexer.WS &&
                            (tokenText.indexOf('\n') > -1 || tokenText.indexOf('\r') > -1)) {
                            hasLineEnder = true;
                            break;
                        }
                    }
                    if (hasLineEnder) {
                        // have "token comment \n", append this comment to the end
                        // of the token.
                        StringBuilder item = new StringBuilder(pop());
                        String comment = nextCommentToken.getText();
                        if (item.indexOf(comment) == -1) {
                            item.append(tab).append(comment);
                            push(item);
                            tokens.seek(commentIndex + 1);
                            return;
                        }
                    }
                }
            }
	    }
        
	    // ...
	    // Line, Regular, and Doc Comments, and jEdit fold markers
	    // A line comment is like this line
	    // /* A regular comment is like this one, and could span several lines */
	    // /** 
	    //  * A doc comment is like this one, double asterisk at the start and
	    //  * could also span several lines 
	    //  */
	    // ...
	    // check to the left of the current token for line. regular, and doc comments
        commentTokens = tokens.getHiddenTokensToLeft(tokenIndex, 2);
        if (commentTokens != null && commentTokens.size() > 0) {
            // there could be multiple line comments
            // like this comment is on two lines
            Collections.reverse(commentTokens);
            for (Token commentToken : commentTokens) {
                if ( commentToken != null) {
                    String comment = commentToken.getText();
                    String current = pop();
                    String previous = stack.peek();
                    push(current);
                    if (previous != null && previous.indexOf(comment) > -1 && previous.trim().endsWith(comment.trim())) { 
                        // already have this comment added as an end of line comment to the right of the previous token.
                        // It's unlikely there would be two comments in a row with the exact same comment. If that were
                        // to actually happen, the second one will be dropped.
                        continue;        
                    }
                    switch(commentToken.getType()) {
                        case JavaLexer.LINE_COMMENT:
                            comment = formatLineComment(comment);
                            break;
                        case JavaLexer.JEDIT_FOLD_MARKER:
                            comment = formatJEditFoldMarkerComment(comment);
                            current = pop();
                            previous = stack.peek();
                            if (previous != null && previous.indexOf(comment) == -1) {
                                previous = pop();
                                previous = new StringBuilder(trimEnd(previous)).append(comment).toString();
                                push(previous);
                            }
                            push(current);
                            continue;
                        case JavaLexer.COMMENT:
                            // check if this is an in-line  or trailing comment, e.g.
                            // something /star comment star/ more things or
                            // something /star comment star/\n
                            // In the first case, there is no way to tell if the comment goes with
                            // the preceding token or the trailing token.
                            int commentTokenIndex = commentToken.getTokenIndex();
                            boolean tokenOnLeft = true;
                            boolean tokenOnRight = true;
                            List<Token> wsTokens = tokens.getHiddenTokensToLeft(commentTokenIndex, 1); //JavaLexer.WS);
                            if (wsTokens != null && wsTokens.size() > 0) {
                                for (Token wsToken : wsTokens) {
                                    String wsText = wsToken.getText();
                                    if (wsText.indexOf('\n') > -1 || wsText.indexOf('\r') > -1) {
                                        tokenOnLeft = false;
                                        break;
                                    }
                                }
                            }
                            wsTokens = tokens.getHiddenTokensToRight(commentTokenIndex, 1); //JavaLexer.WS);
                            if (wsTokens != null && wsTokens.size() > 0) {
                                for (Token wsToken : wsTokens) {
                                    String wsText = wsToken.getText();
                                    if (wsText.indexOf('\n') > -1 || wsText.indexOf('\r') > -1) {
                                        tokenOnRight = false;
                                        break;
                                    }
                                }
                            }
                            if (tokenOnLeft && tokenOnRight) {
                                comment += " ";     // NOPMD
                            }
                            else {
                                comment = formatComment(comment);   
                            }
                            String last = stack.peek();
                            if (last != null && last.indexOf(comment) == -1) {
                                last = pop();
                                last = new StringBuilder(comment).append(last).toString();
                                push(last);
                            }
                            break;
                        case JavaLexer.DOC_COMMENT: 
                            comment = '\n'+ formatDocComment(comment);
                            break;
                    }
                    if (stack.size() == 0) {
                        push(comment);
                    }
                    else {
                        String last = stack.peek();
                        if (last != null && last.indexOf(comment) == -1) {
                            last = pop();
                            if (!comment.endsWith("\n")) {
                                comment += '\n';    // NOPMD
                            }
                            last = new StringBuilder(comment).append(last).toString();
                            push(last);
                        }
                    }
                }
            }
        }
	}
	
	/**
 	 * Formats a line comment. Current rule is to ensure there is a space following the
 	 * slashes, so "//comment" becomes "// comment". Also handle the case where there
 	 * are more than 2 slashes the same way, so "////comment" becomes "//// comment".
 	 * @param comment An end of line comment.
 	 * @return The formatted comment.
 	 */
	private String formatLineComment(String comment) {
	    // ensure there is a space after the comment start. Handle the case
	    // of multiple /, e.g. ////this is a comment
	    // should look like    //// this is a comment
	    String c = comment.trim();
	    int slashCount = 0;
	    for (int i = 0; i < c.length(); i++) {
	        if (c.charAt(i) == '/') {
	            ++ slashCount;    
	        }
	        else {
	            break;    
	        }
	    }
	    c = c.substring(slashCount).trim();
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < slashCount; i++) {
	        sb.append('/');
	    }
	    sb.append(' ').append(c);
	    c = sb.toString();
	    return indent(c);
	}
	
	/**
 	 * Formats a jEdit fold marker, // }}}. Current rule is to ensure there is a space following the
 	 * slashes, so "//}}}" becomes "// }}}". Also handle the case where there
 	 * are more than 2 slashes the same way, so "////}}}" becomes "//// }}}".
 	 * @param comment A jEdit fold marker.
 	 * @return The formatted fold marker.
 	 */
	private String formatJEditFoldMarkerComment(String comment) {
	    // ensure there is a space after the comment start. Handle the case
	    // of multiple /, e.g. ////}}}
	    // should look like    //// }}}
	    String c = comment.trim();
	    int slashCount = 0;
	    for (int i = 0; i < c.length(); i++) {
	        if (c.charAt(i) == '/') {
	            ++ slashCount;    
	        }
	        else {
	            break;    
	        }
	    }
	    c = c.substring(slashCount).trim();
	    StringBuilder sb = new StringBuilder();
	    sb.append('\n');
	    for (int i = 0; i < slashCount; i++) {
	        sb.append('/');
	    }
	    sb.append(' ').append(c).append('\n');
	    c = sb.toString();
	    c = indent(c);
	    return c;
	}
	
	/**
 	 * Formats a regular block comment. This method splits the 
 	 * given comment into lines, indents each line, and ensures there is a * at the
 	 * start of each line.
 	 * @param comment A block comment.
 	 * @return The formatted comment.
 	 */
	private String formatComment(String comment) {
	    if (comment == null || comment.isEmpty()) {
	        return "";
	    }
        String[] lines = comment.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("/*")) {
                sb.append(line).append('\n');    
            }
            else if (line.startsWith("*")) {
                sb.append(' ').append(line).append('\n');
            }
            else {
                sb.append(" * ").append(line).append('\n');    
            }
        }
        return sb.toString();
	}
	
	/**
	 * Notes from Sun's javadoc documentation:
 	 * Each line in the comment is indented to align with the code below the comment.
 	 * The first line contains the begin-comment delimiter ( /**).
 	 * Starting with Javadoc 1.4, the leading asterisks are optional.
 	 * Write the first sentence as a short summary of the method, as Javadoc automatically places it in the method summary table (and index).
 	 * Notice the inline tag {@link URL}, which converts to an HTML hyperlink pointing to the documentation for the URL class. This inline tag can be used anywhere that a comment can be written, such as in the text following block tags.
 	 * If you have more than one paragraph in the doc comment, separate the paragraphs with a <p> paragraph tag, as shown.
 	 * Insert a blank comment line between the description and the list of tags, as shown.
 	 * The first line that begins with an "@" character ends the description. There is only one description block per doc comment; you cannot continue the description following block tags.
 	 * The last line contains the end-comment delimiter. Note that unlike the begin-comment delimiter, the end-comment contains only a single asterisk.
 	 */
	private String formatDocComment(String comment) {
	    return formatComment(comment);
	}
	
	/**
 	 * Handles new line tokens preceding the <code>node</code>.	
 	 */
	private void processWhitespace(TerminalNode node) {
        Token token = node.getSymbol();
        
        int tokenIndex = token.getTokenIndex();
        List<Token> wsTokens = tokens.getHiddenTokensToLeft(tokenIndex, 1); //JavaLexer.WS);
        if (wsTokens != null && wsTokens.size() > 0) {
            // count the number of new line tokens preceding this token
            int nlCount = 0;
            for (Token ws : wsTokens) {
                String s = ws.getText();
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '\n') {
                        ++ nlCount;    
                    }
                }
            }
            // add no more than collapseMultipleBlankLinesTo new lines to the
            // front of the token
            if (nlCount > 0) {
                if (nlCount > collapseMultipleBlankLinesTo) {
                    nlCount = collapseMultipleBlankLinesTo;    
                }
                String blankLines = getBlankLines(nlCount);
                String last = stack.peek();
                if (last != null) {
                    last = pop();
                    last = blankLines + removeBlankLines(last, START);
                    push(last);
                }
            }
        }
	}
	
	/**
 	 * Pops <code>howMany</code> items from the stack. Assumes these items are modifiers
 	 * for a constructor, method, field, or class. Formats the modifiers. Annotations
 	 * are on a separate line above the other modifiers, remaining modifiers are separated
 	 * by spaces.
 	 * @param howMany The number of modifiers to pop off of the stack.
 	 * @return The formatted modifiers.
 	 */
	private String formatModifiers(int howMany) {
	    return formatModifiers(howMany, false);
	}
	
	/**
 	 * Pops <code>howMany</code> items from the stack. Assumes these items are modifiers
 	 * for a constructor, method, field, or class. Formats the modifiers. Annotations
 	 * are on a separate line above the other modifiers, remaining modifiers are separated
 	 * by spaces.
 	 * @param howMany The number of modifiers to pop off of the stack.
 	 * @param singleLine If true, all modifiers are on a single line, if false, annotations
 	 * will be on the first line and remaining modifiers a the second line.
 	 * @return The formatted modifiers.
 	 */
	private String formatModifiers(int howMany, boolean singleLine) {
	    if (howMany <= 0) {
	        return "";   
	    }
	    
	    // gather the modifiers into a list, then reverse the list so they are
	    // in the right order
	    List<String> modifiers = reverse(howMany);
	    
	    // check for comments, if there is one or more single line comments (//...)
	    // pull them out and save them for later. These comments should be in front
	    // of the modifier, unless it follows an annotation.
	    // Keep inline comments (e.g. private /* words */ ) attached to the modifier
	    // and let the sorter deal with them
	    String lineComment = "";
	    for (int i = 0; i < modifiers.size(); i++) {
	        String mod = modifiers.get(i);
	        if (mod.indexOf('\n') > -1 && mod.indexOf("//") > -1 && !mod.startsWith("@")) {
	            String[] lines = mod.split("\n");
	            for (String line : lines) {
	                 if (line.indexOf("//") > -1) {
	                     lineComment = new StringBuilder(lineComment).append(line).append('\n').toString();
	                 }
	                 else {
	                      modifiers.set(i, lines[lines.length - 1]);   
	                 }
	            }
	        }
	    }
	    
	    if (sortModifiers) {
	        Collections.sort(modifiers, modifierComparator);    
	    }
	    
        // modifiers shouldn't be on multiple lines, but comments preceding the
        // modifier may already be attached and can have multiple lines.
        // Handle the multiple lines here.
        StringBuilder sb = new StringBuilder();
        if (!lineComment.isEmpty()) {
            sb.append(lineComment);   
        }
        for (int i = 0; i < modifiers.size(); i++) {
            String modifier = modifiers.get(i);
            modifier = modifier.trim();
            if (modifier.startsWith("@") && !singleLine) {
                modifier += '\n';   // NOPMD
            }
            sb.append(modifier);
            if (i < modifiers.size() - 1) {
                sb.append(' ');   
            }
        }
	    return sb.toString();
	}
	
	/**
 	 * This is for sorting the modifiers.	
 	 */
	private static Comparator<String> modifierComparator = new Comparator<String>() {
	    // From Java 8 language specification: "If two or more (distinct) class modifiers 
	    // appear in a class declaration, then it is customary, though not required, 
	    // that they appear in the order consistent with that shown above in the production 
	    // for ClassModifier."
	    // This list contains all possible modifiers in the correct order. Not all
	    // modifiers are allowed for all constructs, but this method does not check
	    // for illegal modifiers. The parser should flag an error in those cases.
	    // Annotations count as modifiers, annotations go before public.
        private String modifiers = "public protected private abstract static final synchronized native sealed non-sealed strictfp transient volatile";
        
	    public int compare(String a, String b) {
	        // the modifier may have comments attaches, remove them before comparing
	        String a_ = removeComments(a);
	        String b_ = removeComments(b);
	        
	        
	        // annotations may be included in the choices
	        if (a_.startsWith("@")) {
	            return -1;   
	        }
	        if (b_.startsWith("@")) {
	            return 1;   
	        }
	        
	        // comments may be included in the choices
	        if (a_.startsWith("/") || a_.startsWith("*")) {
	            return -1;    
	        }
	        if (b_.startsWith("/") || b_.startsWith("*")) {
	            return 1;    
	        }
	        
	        return modifiers.indexOf(a_) - modifiers.indexOf(b_);
	    }
	};
	
	/**
 	* Removes comments from the given string and trims whitespace after the comment is
 	* removed.
 	*/
	private static String removeComments(String s) {
	    String nc = new String(s);    // NOPMD
        nc = nc.replaceAll("//.*?\\n", "");
        nc = nc.replaceAll("/[*].*?[*]/", "");
        return nc.trim();
	}
	
	private String sortAndGroupImports(List<String> importList) {
	    if (sortImports == false && groupImports == false) {
	        StringBuilder sb = new StringBuilder();
	        for (String imp : importList) {
	            sb.append(imp);    
	        }
	        return sb.toString();    
	    }
	    
	    // remove null items and blank strings
	    ListIterator<String> it = importList.listIterator();
	    while(it.hasNext()) {
	        String next = it.next();
	        if (next == null || next.trim().isEmpty()) {
	            it.remove();    
	        }
	        it.set(next.trim() + '\n');
	    }
	    
	    // sort imports
	    if (sortImports || groupImports) {
	        // grouping imports requires sorting them first
	        Collections.sort(importList, importComparator);            
	    }
	    
	    // remove duplicates but keep order
	    importList = new ArrayList<String>(new LinkedHashSet<String>(importList));
	    
	    // group imports
	    if (groupImports) {
	        String blankLines = getBlankLines(blankLinesBetweenImportGroups);
            List<String> groups = new ArrayList<String>();
            for (int i = 0; i < importList.size(); i++) {
                String imp = importList.get(i);
                if (i == 0) {
                    groups.add(imp);
                    continue;
                }
                String a = getImportName(importList.get(i - 1));
                if (a.indexOf('.') > -1) {
                    a = a.substring(0, a.indexOf('.'));    
                }
                String b = getImportName(importList.get(i));
                if (b.indexOf('.') > -1) {
                    b = b.substring(0, b.indexOf('.'));    
                }
                if (!a.equals(b)) {
                    groups.add(blankLines);
                }
                groups.add(imp);
            }
            importList.clear();
            importList.addAll(groups);
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    for (String imp : importList) {
	        sb.append(imp);    
	    }
	    return sb.toString();
	}
	
	/**
 	 * Get just the name of the import in the import statement, e.g. given "import java.util.*;"
 	 * this method would return "java.util.*;". Note that there may be comments included
 	 * with <code>s</code>.
 	 */
	private String getImportName(String s) {
	    String[] lines = s.split("\n");
	    // just check the last non-blank line
	    for (int i = lines.length - 1; i >= 0; i--) {
	        String line = lines[i].trim();
	        if (line.startsWith("import ")) {
	            return line.substring(line.lastIndexOf(' '));    
	        }
	    }
	    return "";
	}
	
	private Comparator<String> importComparator = new Comparator<String>() {
        @Override
	    public int compare(String a, String b) {
	        String a_ = getImportName(a);
	        String b_ = getImportName(b);
	        return a_.compareTo(b_);
	    }
	};
	
	private Pattern whitespacePattern = Pattern.compile("\\s*");
	private boolean isWhitespace(StringBuilder sb) {
	    if (sb == null || sb.length() == 0) {
	        return true;   
	    }
	    return isWhitespace(sb.toString());    
	}
	
	private boolean isWhitespace(String s) {
	    if (s == null || s.length() == 0) {
	        return true;    
	    }
	    Matcher m = whitespacePattern.matcher(s);
	    return m.matches();
	}
	
	/**
 	 * Removes all excess whitespace from the end of each of the lines in <code>s</code>.	
 	 */
	private String removeExcessWhitespace(String s) {
	    String[] lines = s.split("\n");
	    StringBuilder sb = new StringBuilder();
	    for (String line : lines) {
	        line = trimEnd(line);
	        sb.append(line).append('\n');
	    }
	    while (endsWith(sb, "\n\n")) {
	        sb.deleteCharAt(sb.length() - 1);    
	    }
	    return sb.toString();
	}

	// puts a space before and after the given operator
	private String padOperator(String operator) {
	    operator = operator.trim();
	    if (padOperators) {
	        StringBuilder sb = new StringBuilder();
	        sb.append(' ').append(operator).append(' ');
	        operator = sb.toString();
	    }
	    return operator;
	}
	
	private int getMemberType(MemberDeclarationContext ctx) {
	    // just a bunch of 'if's here
	    if (ctx.methodDeclaration() != null || ctx.genericMethodDeclaration() != null) {
	        return METHOD;   
	    }
	    if (ctx.fieldDeclaration() != null) {
	        return FIELD;   
	    }
	    if (ctx.constructorDeclaration() != null || ctx.genericConstructorDeclaration() != null) {
	        return CONSTRUCTOR;   
	    }
	    if (ctx.interfaceDeclaration() != null) {
	        return INTERFACE;    
	    }
	    if (ctx.annotationTypeDeclaration() != null) {
	        return ANNOTATION;   
	    }
	    if (ctx.classDeclaration() != null) {
	        return CLASS;   
	    }
	    if (ctx.enumDeclaration() != null) {
	        return ENUM;    
	    }
	    if (ctx.recordDeclaration() != null) {
	        return RECORD;    
	    }
	    return 0;
	}
	
	/**
 	* interfaceMemberDeclaration
 	*     : constDeclaration
 	*     | interfaceMethodDeclaration
 	*     | genericInterfaceMethodDeclaration
 	*     | interfaceDeclaration
 	*     | annotationTypeDeclaration
 	*     | classDeclaration
 	*     | enumDeclaration
 	*     | recordDeclaration // Java17
 	*     ;
 	*/
	private int getInterfaceMemberType(InterfaceMemberDeclarationContext ctx) {
	    if (ctx == null) {
	        return 0;    
	    }
	    // just a bunch of 'if's here
	    if (ctx.constDeclaration() != null) {
	        return CONST;   
	    }
	    if (ctx.interfaceMethodDeclaration() != null || ctx.genericInterfaceMethodDeclaration() != null) {
	        return METHOD;   
	    }
	    if (ctx.interfaceDeclaration() != null) {
	        return INTERFACE;    
	    }
	    if (ctx.annotationTypeDeclaration() != null) {
	        return ANNOTATION;   
	    }
	    if (ctx.classDeclaration() != null) {
	        return CLASS;   
	    }
	    if (ctx.enumDeclaration() != null) {
	        return ENUM;    
	    }
	    if (ctx.recordDeclaration() != null) {
	        return RECORD;    
	    }
	    return 0;
	}
	
	/**
 	* Takes a long line as input, splits it into line of no more than <code>wrapLineLength</code>.	
 	*/
    private String wrapLongLine(String s) {
        if (wrapLineLength == 0) {
            return s;   // 0 means don't wrap    
        }
        StringTokenizer st = new StringTokenizer(s, " ");
        StringBuilder sb = new StringBuilder(s.length());
        sb.append('\n');    // it's a long line so start it on a new line
        int lineLength = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (lineLength + token.length() > wrapLineLength) {
                sb.append('\n');
                lineLength = 0;
            }
            sb.append(token);
            lineLength += token.length();
        }
        return sb.toString();
    }	
//}}}    

}