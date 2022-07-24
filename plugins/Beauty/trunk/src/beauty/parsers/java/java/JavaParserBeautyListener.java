
package beauty.parsers.java.java;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import static beauty.parsers.java.java.JavaParser.*;

import java.util.*;

/**
 * Beautifier for Java 18 and below.
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
    private int blankLinesAfterClassDeclaration = 1; 
    private int blankLinesAfterClassBody = 2;
    private int blankLinesBeforeMethods = 1;  
    private int blankLinesAfterMethods = 1;
    private boolean sortModifiers = true;               
    private int collapseMultipleBlankLinesTo = 2; 
    

    
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
    
    public static void main (String[] args) {
        if (args == null)
            return;
        try {
            // set up the parser
            long startTime = System.currentTimeMillis();
            java.io.FileReader input = new java.io.FileReader(args[0]);
            CharStream antlrInput = CharStreams.fromReader(input);
            JavaLexer lexer = new JavaLexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            JavaParser javaParser = new JavaParser( tokens );
            //javaParser.setTrace(true);
            
            // parse and beautify the buffer contents
            JavaParserBeautyListener listener = new JavaParserBeautyListener(16 * 1024, tokens);
            listener.setUseSoftTabs(true);
            listener.setIndentWidth(4);
            listener.setPadParens(true);
            ParseTreeWalker walker = new ParseTreeWalker();
            ParseTree tree = javaParser.compilationUnit();
            walker.walk( listener, tree );

            System.out.println("----- final output -----");
            System.out.println(listener.getText());
            System.out.println("------------------------");
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.println("elapsed time: " + elapsed + " ms");
            
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

	@Override public void exitAltAnnotationQualifiedName(AltAnnotationQualifiedNameContext ctx) { }
	
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
	            element = pop();
	        }
	        String lp = pop();    // (
	        elements.append(lp).append(element).append(rp);
	    }
	    String name = pop();
	    StringBuilder annotation = new StringBuilder();
	    String at = pop();
	    annotation.append(at).append(name).append(elements);
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
	        defaultValue = pop();
	    }
	    String rp = pop();    // )
	    String lp = pop();    // (
	    StringBuilder identifier = new StringBuilder(pop());
	    identifier.append(lp).append(rp).append(defaultValue);
	    push(identifier);
	}
	
	/**
 	* 	annotationTypeBody
 	*     : '{' (annotationTypeElementDeclaration)* '}'
 	*     ;
 	*/
 	@Override public void exitAnnotationTypeBody(AnnotationTypeBodyContext ctx) { 
 	    String rb = pop();    // }
 	    StringBuilder body = new StringBuilder(rb).append('\n');
 	    if (ctx.annotationTypeElementDeclaration() != null) {
 	        int size = ctx.annotationTypeElementDeclaration().size();
 	        List<String> parts = reverse(size);
 	        for (String part : parts) {
 	            body.append(indent(part)).append('\n');
 	        }
 	    }
 	    String lb = pop();
 	    body.append('\n').append(lb);
 	    push(body);       // push it right off of the cliff
 	}
 	
 	/**
  	* annotationTypeDeclaration
  	*     : '@' INTERFACE identifier annotationTypeBody
  	*     ;
  	*/
	@Override public void exitAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) { 
	    String body = pop();
	    String identifier = pop();
	    String interface_ = pop();    // interface keyword
	    String at = pop();    // @
	    StringBuilder sb = new StringBuilder();
	    sb.append(at).append(interface_).append(' ').append(identifier).append(' ').append(body);
	    push(sb);
	}
	@Override public void exitAnnotationTypeElementDeclaration(AnnotationTypeElementDeclarationContext ctx) { }
	
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
        if (ctx.SEMI() != null) {
            String semi = pop();    // pop the semicolon
            StringBuilder type = new StringBuilder();
            if (ctx.typeType() != null) {
                List<String> parts = reverse(2);    // typeType and annotationMethodOrConstantRest
                for (int i = 0; i < 2; i++) {
                    type.append(parts.get(i)).append(' ');   
                }
            }
            else {
                type.append(pop());   // one of the other 5 choices   
            }
            type.append(semi);
            push(type);
        }
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
 	        expressionList = pop();
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
 	*     : '[' (']' ('[' ']')* arrayInitializer | expression ']' ('[' expression ']')* ('[' ']')*)
 	*     ;
 	*/
	@Override public void exitArrayCreatorRest(ArrayCreatorRestContext ctx) {
	    // TODO: this is a pain and not right, in fact, I'm not sure the regular expression is correct
	    int size = ctx.LBRACK().size();
	    size += ctx.RBRACK().size();
	    if (ctx.expression() != null) {
	        size += ctx.expression().size();   
	    }
	    if (ctx.arrayInitializer() != null) {
	        ++ size;   
	    }
	    pop(size);
	    push(ctx.getText());    // TODO: this also drops any comments that may have been attached
	}
	
	
	/**
 	* arrayInitializer
 	*     : '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
 	*     ;
 	*/
	@Override public void exitArrayInitializer(ArrayInitializerContext ctx) { 
	    String rb = pop();    // }
	    int size = ctx.variableInitializer().size();
	    if (ctx.COMMA() != null) {
	        size += ctx.COMMA().size();   
	    }
	    String variableInitializers = reverse(size, " ", 2);
	    String lb = pop();    // {
	    StringBuilder sb = new StringBuilder();
	    sb.append(lb).append(variableInitializers).append(rb);
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
	    String rbrace = trimFront(pop());
	    StringBuilder blockStatements = new StringBuilder();
	    if (ctx.blockStatement() != null) {
	        int size = ctx.blockStatement().size();
	        List<String> parts = reverse(size);
	        for (String part : parts) {
	            blockStatements.append(indent(part)).append('\n');  
	        }
	        trimEnd(blockStatements);
	    }
	    String lbrace = pop().trim();
	    --tabCount;
	    
	    StringBuilder sb = new StringBuilder();
	    if (brokenBracket) {
            sb.append('\n');	        
	    }
	    sb.append(lbrace).append(blockStatements).append('\n').append(rbrace);
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
	    if (ctx.SEMI() != null) {
	        String semi = pop();    
	        String localVariableDeclaration = pop();
	        push(indent(localVariableDeclaration + semi));
	    }
	    else { 
            String stmtOrDecl = pop();
            push(indent(stmtOrDecl));
	    }
	}
	
	/**
 	* catchClause
 	*     : CATCH '(' variableModifier* catchType identifier ')' block
 	*     ;
 	*/
	@Override public void exitCatchClause(CatchClauseContext ctx) { 
	    String block = pop();
	    String rb = pop();    // )
	    String identifier = pop();
	    String catchType = pop();
	    String variableModifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        variableModifiers = reverse(size, " ");
	    }
	    String lb = pop();    // (
	    String catch_ = pop();    // catch keyword
	    StringBuilder sb = new StringBuilder(catch_);
	    sb.append(padParen(lb)).append(variableModifiers).append(catchType).append(' ').append(identifier).append(padParen(rb)).append(block);
	    push(sb);
	}
	
	
	/**
 	* catchType
 	*     : qualifiedName ('|' qualifiedName)*
 	*     ;
 	*/
	@Override public void exitCatchType(CatchTypeContext ctx) { 
	    int size = ctx.qualifiedName().size();
	    String qualifiedNames = reverse(size * 2 - 1, padOperator("|"));
	    push(qualifiedNames);
	}
	
	/**
 	* classBody
 	*     : '{' classBodyDeclaration* '}'
 	*     ;
 	*/
	@Override public void exitClassBody(ClassBodyContext ctx) { 
	    String rbrace = pop();
	    StringBuilder classBodyDecl = new StringBuilder();
	    if (ctx.classBodyDeclaration() != null) {
	        int size = ctx.classBodyDeclaration().size();
	        List<String> parts = reverse(size);
	        for (String part : parts) {
	            classBodyDecl.append(indent(part)).append('\n');   
	        }
	        trimEnd(classBodyDecl);
	    }
	    String lbrace = pop();
	    
	    StringBuilder classBody = new StringBuilder();
	    classBody.append(lbrace).append('\n').append(classBodyDecl).append('\n').append(rbrace);
	    push(classBody);
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
	        return;     // only thing in the class body is a semicolon, and it's already on the stack   
	    }
	    
	    if (ctx.block() != null) {
	         StringBuilder block = new StringBuilder(pop());
	         if (ctx.STATIC() != null) {
	             String static_ = pop();    // static keyword
	             block.insert(0, static_ + ' ');
	         }
	         push(block);
	         return;
	    }
	    else {
            String memberDeclaration = pop();
            StringBuilder modifiers = new StringBuilder();
            if (ctx.modifier() != null) {
                int size = ctx.modifier().size();
                List<String> parts = reverse(size);
                for (String part : parts) {
                    modifiers.append(part).append(' ');
                }
            }
            modifiers.append(memberDeclaration);
            push(modifiers);
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
	    String permitsList = "";
	    if (ctx.PERMITS() != null) {
	        String typeList = pop();
	        String permits_ = pop();
	        permitsList = permits_ + ' ' + typeList;
	    }
	    String implementsList = "";
	    if (ctx.IMPLEMENTS() != null) {
	         String typeList = pop();
	         String implements_ = pop();
	         implementsList = implements_ + ' ' + typeList;
	    }
	    String extendsType = "";
	    if (ctx.EXTENDS() != null) {
	         String typeType = pop();
	         String extends_ = pop();
	         extendsType = extends_ + ' ' + typeType;
	    }
	    String typeParameters = "";
	    if (ctx.typeParameters() != null) {
	         typeParameters = pop();   
	    }
	    String identifier = pop();
	    String class_ = pop();    // class keyword
	    
	    StringBuilder classDecl = new StringBuilder();
	    classDecl.append(class_).append(identifier).append(' ').append(typeParameters).append(' ').append(extendsType).append(implementsList).append(permitsList).append(classBody);
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
  	* typeArguments
  	*     : '<' typeArgument (',' typeArgument)* '>'
  	*     ;
 	*/
	@Override public void exitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) {
	    int size = ctx.identifier().size();
	    if (size == 1) {
	         return;    // only have an identifier, and it's already on the stack   
	    }
	    
	    // there are at least 2 identifiers
	    List<String> parts = new ArrayList<String>();
	    for (int i = 0; i < size - 1; i++) {
	        String maybeType = stack.peek();
	        if (maybeType.startsWith("<")) {
	            parts.add(pop());    // typeArguments
	        }
            parts.add(pop());    // identifier
            parts.add(pop());    // .
	    }
	    parts.add(pop());    // first identifier
	    Collections.reverse(parts);
	    StringBuilder sb = new StringBuilder();
	    for (String part : parts) {
	        sb.append(part);
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
                typeDeclarations = reverse(ctx.typeDeclaration().size(), "");
                typeDeclarations = removeBlankLines(typeDeclarations, BOTH) + '\n';
                typeDeclarations = removeExcessWhitespace(typeDeclarations);
            }
            String importDeclarations = "";
            if (ctx.importDeclaration() != null) {
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
	    String semi = pop();
	    int size = ctx.constantDeclarator().size();
	    String constantDeclarator = reverse(size * 2 - 1, " ", 2);
	    String typeType = pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(typeType).append(' ').append(constantDeclarator).append(semi);
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
	    StringBuilder throwsList = new StringBuilder();;
	    if (ctx.THROWS() != null) {
	        String qualifiedNameList = pop();
	        String throws_ = pop();    // throws keyword
	        throwsList.append(throws_).append(' ').append(qualifiedNameList);
	    }
	    String formalParameters = pop();
	    String identifier = pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(identifier).append(' ').append(formalParameters).append(' ').append(throwsList).append(block);
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
	        sb.append(nonWildcardTypeArguments).append(' ').append(createdName).append(' ').append(classCreatorRest);
	        push(sb);
	    }
	    else {
	        // second choice
	        String end = pop();    // either arrayCreatorRest or classCreatorRest
	        String createdName = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(createdName).append(' ').append(end);
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
	    // nothing to do here, one of the choices should already be on the stack
	}
	
	/**
 	* elementValueArrayInitializer
 	*     : '{' (elementValue (',' elementValue)*)? (',')? '}'
 	*     ;
 	*/
	@Override public void exitElementValueArrayInitializer(ElementValueArrayInitializerContext ctx) { 
	    String rbrace = pop();
	    StringBuilder elements = new StringBuilder();
	    if (ctx.elementValue() != null) {
	        int size = ctx.elementValue().size();
	        if (ctx.COMMA() != null) {
	            size += ctx.COMMA().size();   
	        }
	        List<String> elementParts = reverse(size);
	        for (int i = 0; i < elementParts.size(); i++) {
	            elements.append(elementParts.get(i));
	            if (i % 2 == 1) {
	                elements.append(' ');   
	            }
	        }
	    }
	    String lbrace = pop();
	    elements.insert(0, lbrace);
	    elements.append(rbrace);
	    push(elements);
	}
	
	/**
 	* elementValuePair
 	*     : identifier '=' elementValue
 	*     ;
 	*/
	@Override public void exitElementValuePair(ElementValuePairContext ctx) {
	    String value = pop();
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
        String pairs = reverse(size * 2 - 1, " ", 2);
        push(pairs);
    }
    
    
    /**
     * enhancedForControl
     *     : variableModifier* (typeType | VAR) variableDeclaratorId ':' expression
     *     ;
     */
	@Override public void exitEnhancedForControl(EnhancedForControlContext ctx) { 
	    String expression = pop();
	    String colon = pop();    // :
	    String variableDeclaratorId = pop();
	    String typeOrVar = pop();
	    String variableModifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        variableModifiers = reverse(size, " ");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(variableModifiers).append(typeOrVar).append(' ').append(variableDeclaratorId).append(padOperator(colon)).append(expression);
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
	        classBodyDeclaration = reverse(size, " ");    // TODO: should space be \n?
	    }
	    String semi = pop();    // ;
	    push(semi + classBodyDeclaration);
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
	    StringBuilder sb = new StringBuilder();
	    sb.append(annotations).append(identifier).append(' ');
	    if (arguments.length() > 0) {
	        sb.append(arguments).append(' ');   
	    }
	    sb.append(classBody);
	    push(sb);
	}
	
	/**
 	* enumConstants
 	*     : enumConstant (',' enumConstant)*
 	*     ;
 	*/
	@Override public void exitEnumConstants(EnumConstantsContext ctx) { 
	    int size = ctx.enumConstant().size();
	    String enumConstants = reverse(size * 2 - 1, " ", 2);
	    push(enumConstants);
	}
	
	
	/**
 	* enumDeclaration
 	*     : ENUM identifier (IMPLEMENTS typeList)? '{' enumConstants? ','? enumBodyDeclarations? '}'
 	*     ;
 	*/
	@Override public void exitEnumDeclaration(EnumDeclarationContext ctx) { 
	    String rb = pop();    // }
	    String enumBodyDeclarations = ctx.enumBodyDeclarations() == null ? "" : pop();
	    String comma = ctx.COMMA() == null ? " " : ", ";
	    String enumConstants = ctx.enumConstants() == null ? "" : pop();
	    String lb = pop();    // {
	    String typeList = " ";
	    if (ctx.typeList() != null) {
	        String implements_ = pop();    // implements keyword
	        typeList = implements_ + ' ' + pop();
	    }
	    String identifier = pop();
	    String enum_ = pop();    // enum keyword
	    StringBuilder sb = new StringBuilder();
	    sb.append(enum_).append(' ').append(identifier).append(typeList).append('{').append(enumConstants).append(comma).append(enumBodyDeclarations);
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
	    if (ctx.SUPER() != null) {
	        String suffix = pop();
	        String super_ = pop();    // super keyword
	        push(super_ + ' ' + suffix);
	    }
	    else {
	        String arguments = pop();
	        String identifier = pop();
	        push(identifier + " " + arguments);
	    }
	}

	
	/*
	* Yes, this is ugly.
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
	    if (ctx.primary() != null)
	        return;
	    if (ctx.methodCall() != null && ctx.expression() == null)
	        return;
	    if (ctx.lambdaExpression() != null)
	        return;
	    if (ctx.switchExpression() != null)
	        return;
	    
	    // things with NEW
	    if (ctx.NEW() != null) {
	        if (ctx.creator() != null) {
	            // NEW creator
	            String creator = pop();
	            String new_ = pop();    // new keyword
	            push(new_ + ' ' + creator);
	            return;
	        }
	        if (ctx.classType() != null || ctx.typeType() != null) {
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
	            String end = pop();
	            String dot = pop();    // . bop
	            StringBuilder expression = new StringBuilder(pop());
	            expression.append(dot);
	            expression.append(end);
	            push(expression);
	            return;
	        }
	        if (ctx.NEW() != null) {
	            String innerCreator = pop();
	            String typeArgs = "";
	            if (ctx.nonWildcardTypeArguments() != null) {
	                 typeArgs = pop() + ' ';   
	            }
	            String new_ = pop();    // new keyword
	            String dot = pop();    // . bop
	            StringBuilder expression = new StringBuilder(pop());
	            expression.append(dot);
	            expression.append(new_);
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
	            expression.append(super_).append(' ');
	            expression.append(superSuffix);
	            push(expression);
	            return;
	        }
	        if (ctx.typeType() != null && ctx.RPAREN() != null && ctx.LPAREN() != null) {
	            // '(' annotation* typeType ('&' typeType)* ')' expression
	            String expression = pop();
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
	            sb.append(rp);
	            sb.append(expression);
	            push(sb);
	            return;
	        }
	        if (ctx.INSTANCEOF() != null) {
	            // expression bop=INSTANCEOF (typeType | pattern)
	            String typeOrPattern = pop();
	            String instanceOf = pop();
	            StringBuilder expression = new StringBuilder(pop());
	            expression.append(' ').append(instanceOf).append(' ').append(typeOrPattern);
	            push(expression);
	            return;
	        }
	        
	        // there are 3 remaining cases with one expression:
            // | expression postfix=('++' | '--')
            // | prefix=('+'|'-'|'++'|'--') expression
            // | prefix=('~'|'!') expression
            String post = stack.peek();
            if (post.indexOf('+') > -1 || post.indexOf('-') > -1) {
                // it's the first case, the postfix expression
                post = pop();     // ++ or --
                StringBuilder expression = new StringBuilder(pop());
                expression.append(padOperator(post));
                push(expression);
                return;
            }
            else {
                // it's one of the prefix expressions, and they are all the same
                StringBuilder expression = new StringBuilder(pop());
                expression.append(padOperator(pop()));    // one of + - ++ -- ~ !
                push(expression);
                return;
            }
            
            
	    }
	    else if (expressionCount == 2) {
	        if (ctx.RBRACK() != null) {
	            // expression '[' expression ']'
	            String rb = pop();    // ]
	            String expression = pop();
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
            String expression = pop();
            String bop = pop();
            StringBuilder sb = new StringBuilder(pop());
            sb.append(padOperator(bop));
            sb.append(expression);
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
            String expression = reverse(size * 2 - 1, " ", 2);
            push(expression);
	    }
	}
	
	
	/**
 	* 	fieldDeclaration
 	*     : typeType variableDeclarators ';'
 	*     ;
 	*/
    @Override public void exitFieldDeclaration(FieldDeclarationContext ctx) { 
        String semi = pop();    // semicolon
        String variableDeclarators = pop();
        StringBuilder fieldDecl = new StringBuilder(pop());   // typeType
        fieldDecl.append(' ').append(variableDeclarators).append(semi);
        push(fieldDecl);
    }
    
    /**
     * finallyBlock
     *     : FINALLY block
     *     ;
     */
	@Override public void exitFinallyBlock(FinallyBlockContext ctx) {
	    String block = pop();
	    String finally_ = pop();    // finally keyword
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
	    String expressionList = ctx.expressionList() == null ? "" : pop();
	    String semi2 = pop();    // ;
	    String expression = ctx.expression() == null ? "" : pop();
	    String semi1 = pop();    // ;
	    String forInit = ctx.forInit() == null ? "" : pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(forInit).append(semi1).append(' ').append(expression).append(semi2).append(' ').append(expressionList);
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
	        modifiers = reverse(size, " ");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(typeType).append(' ').append(id);
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
	        if (ctx.lastFormalParameter() != null) {
	            lastFormalParameter = pop();
	            String comma = pop();    // ,
	            lastFormalParameter = comma + ' ' + lastFormalParameter;
	        }
	        int size = ctx.formalParameter().size();
	        String formalParameter = size == 1 ? pop() : reverse(size * 2 - 1, " ", 2);    // TODO test the 'reverse' method
	        StringBuilder sb = new StringBuilder();
	        sb.append(formalParameter).append(lastFormalParameter);
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
	    String rp = pop();    // )
	    if (ctx.receiverParameter() != null && ctx.formalParameterList() != null) {
	        String formalParameterList = pop();
	        String comma = pop();
	        String receiverParameter = pop();
	        String lp = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(padParen(lp)).append(receiverParameter).append(comma).append(' ').append(formalParameterList).append(padParen(rp));
	        push(sb);
	    }
	    else if (ctx.receiverParameter() != null || ctx.formalParameterList() != null) {
	        String param = pop();
	        String lp = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(padParen(lp)).append(param).append(padParen(rp));
	        push(sb);
	    }
	}
	
	
	/**
 	* genericConstructorDeclaration
 	*     : typeParameters constructorDeclaration
 	*     ;
 	*/
	@Override public void exitGenericConstructorDeclaration(GenericConstructorDeclarationContext ctx) { 
	    String decl = pop();
	    String typeParameters = pop();
	    push(typeParameters + " " + decl);
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
	        modifiers = reverse(size, " ");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(typeParameters).append(' ').append(decl);
	    push(sb);
	}
	
	
	/**
 	* genericMethodDeclaration
 	*     : typeParameters methodDeclaration
 	*     ;
 	*/
	@Override public void exitGenericMethodDeclaration(GenericMethodDeclarationContext ctx) { 
	    String method = pop();
	    String typeParameters = pop();
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
	        String rp = pop();    // )
	        String guardedPattern = pop();
	        String lp = pop();    // (
	        StringBuilder sb = new StringBuilder();
	        sb.append(lp).append(guardedPattern).append(rp);
	        push(sb);
	    }
	    else if (ctx.typeType() != null) {
	        // second choice 
	        StringBuilder expression = new StringBuilder();
	        if (ctx.expression() != null) {
	            int size = ctx.expression().size();
	            List<String> parts = new ArrayList<String>();
	            for (int i = 0; i < size; i++) {
	                parts.add(pop());    // expression
	                parts.add(padOperator(pop()));    // &&
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
                modifiers = reverse(size, " ");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(modifiers).append(typeType).append(' ').append(annotations).append(identifier).append(expression);
            push(sb);
	    }
	    else {
	        // third choice
	        String expression = pop();
	        String amps = pop();    // &&
	        String guardedPattern = pop();
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
	    String classCreatorRest = pop();
	    String args = ctx.nonWildcardTypeArgumentsOrDiamond() == null ? " " : pop();
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

	
	/**
 	* interfaceBody
 	*     : '{' interfaceBodyDeclaration* '}'
 	*     ;
 	*/
	@Override public void exitInterfaceBody(InterfaceBodyContext ctx) { 
	    String rp = pop();     // }
	    String decl = pop();
	    String lp = pop();     // {
	    StringBuilder sb = new StringBuilder();
	    sb.append(rp).append('\n').append(indent(decl)).append('\n').append(lp);
	    push(sb);
	}
	
	/**
 	* interfaceBodyDeclaration
 	*     : modifier* interfaceMemberDeclaration
 	*     | ';'
 	*     ;
 	*/
	@Override public void exitInterfaceBodyDeclaration(InterfaceBodyDeclarationContext ctx) {
	    if (ctx.interfaceMemberDeclaration() != null) {
	        String decl = pop();
	        String modifiers = "";
            if (ctx.modifier() != null) {
                int size = ctx.modifier().size();
                modifiers = reverse(size, " ");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(modifiers).append(decl);
            push(sb);
	    }
	    // the semicolon choice is already on the stack
	}
	
	
	/**
 	* interfaceCommonBodyDeclaration
 	*     : annotation* typeTypeOrVoid identifier formalParameters ('[' ']')* (THROWS qualifiedNameList)? methodBody
 	*     ;
 	*/
	@Override public void exitInterfaceCommonBodyDeclaration(InterfaceCommonBodyDeclarationContext ctx) { 
	    String methodBody = pop();
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
	    sb.append(annotations).append(typeTypeOrVoid).append(' ').append(identifier).append(' ').append(formalParameters).append(brackets).append(qualifiedNameList).append(methodBody);
	    push(sb);
	}
	
	
	/**
 	* interfaceDeclaration
 	*     : INTERFACE identifier typeParameters? (EXTENDS typeList)? (PERMITS typeList)? interfaceBody
 	*     ;
 	*/
	@Override public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) { 
	    String interfaceBody = pop();
	    String permits_ = "";
	    if (ctx.PERMITS() != null) {
	        String typeList = pop();
	        permits_ = pop();    // permits keyword
	        permits_ += ' ' + typeList;
	    }
	    StringBuilder extends_ = new StringBuilder();
	    if (ctx.EXTENDS() != null) {
	        String typeList = pop();
	        String extendsKeyword = pop();
	        extends_.append(' ').append(extendsKeyword).append(' ').append(typeList);
	    }
	    String typeParameters = ctx.typeParameters() == null ? "" : pop();
	    String identifier = pop();
	    String interface_ = pop();    // interface keyword
	    StringBuilder sb = new StringBuilder();
	    sb.append(interface_).append(' ').append(identifier).append(' ').append(typeParameters).append(extends_).append(permits_).append(interfaceBody);
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
	        modifiers = reverse(size, " ");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(decl);
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
	        modifiers = reverse(size, " ");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(var_).append(' ').append(identifier);
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
	        modifiers = reverse(size, " ");
	    }
        StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(typeType).append(' ').append(annotations).append(ellipsis).append(id);
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
 	* This is idential to typeDeclaration
 	*/
	@Override public void exitLocalTypeDeclaration(LocalTypeDeclarationContext ctx) { 
	    if (ctx.SEMI() != null) {
	        return;    // Just a semicolon in the type declaration
	    }
	    String declaration = pop();
	    StringBuilder typeDeclaration = new StringBuilder();
	    if (ctx.classOrInterfaceModifier() != null) {
	        int size = ctx.classOrInterfaceModifier().size();
	        List<String> parts = reverse(size);
	        for (int i = 0; i < size; i++) {
	             typeDeclaration.append(parts.get(i)).append(' ');   
	        }
	    }
	    typeDeclaration.append(declaration);
	    push(typeDeclaration);
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
            type.append(var_).append(' ').append(identifier).append(equals).append(expression);
        }
        else {
            String variableDecls = pop();
            String typeType = pop();
            type.append(typeType).append(' ').append(variableDecls);
        }
        StringBuilder variableModifiers = new StringBuilder();
        if (ctx.variableModifier() != null) {
            int size = ctx.variableModifier().size();
            List<String> parts = reverse(size);
            for (String part : parts) {
                variableModifiers.append(part).append(' ');    
            }
        }
        variableModifiers.append(type);
        push(variableModifiers);
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
	    // Nothing to do here, one of the choices should already be on the stack.
	}
	
	/**
 	* methodCall
 	*     : identifier '(' expressionList? ')'
 	*     | THIS '(' expressionList? ')'
 	*     | SUPER '(' expressionList? ')'
 	*     ;
 	*/
	@Override public void exitMethodCall(MethodCallContext ctx) { 
	    String rp = pop();    // )
	    String expressionList = ctx.expressionList() == null ? "" : pop();
	    String lp = pop();    // (
	    String start = pop();    // one of 'identifier', 'this', or 'super'
	    StringBuilder sb = new StringBuilder();
	    sb.append(start).append(padParen(lp)).append(expressionList).append(padParen(rp));
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
	    String methodBody = pop();
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
	    String formalParameters = pop();
	    String identifier = pop();
	    String typeOrVoid = pop();
	    StringBuilder method = new StringBuilder();
	    method.append(typeOrVoid).append(' ').append(identifier).append(' ').append(formalParameters).append(' ').append(brackets).append(throwsList).append(methodBody);
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

	
	
	/**
 	* moduleBody
 	*     : '{' moduleDirective* '}'
 	*     ;
 	*/
	@Override public void exitModuleBody(ModuleBodyContext ctx) { 
	    String rbrace = pop();
	    StringBuilder moduleDirectives = new StringBuilder();
	    if (ctx.moduleDirective() != null) {
	        int size = ctx.moduleDirective().size();
	        List<String> directives = reverse(size);
	        for (int i = 0; i < size; i++) {
	            moduleDirectives.append(directives.get(i)).append('\n');   
	        }
	    }
	    String lbrace = pop() + '\n';
	    StringBuilder moduleBody = new StringBuilder();
	    moduleBody.append(lbrace);
	    moduleBody.append(indent(moduleDirectives.toString()));
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
        push(moduleDirective);
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
	    String expression = pop();
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
        String annotations = " ";
	    if (ctx.annotation() != null) {
	        int size = ctx.annotation().size();
	        annotations = reverse(size, " ");
	    }
	    String typeType = pop();
        String modifiers = "";
	    if (ctx.variableModifier() != null) {
	        int size = ctx.variableModifier().size();
	        modifiers = reverse(size, " ");
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(modifiers).append(typeType).append(annotations).append(identifier);
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
	        // first choice
	        String rp = pop();    // )
	        String expression = pop();
	        String lp = pop();    // (
	        StringBuilder sb = new StringBuilder();
	        sb.append(padParen(lp)).append(expression).append(padParen(rp));
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
	
	/**
 	* recordBody
 	*     : '{' classBodyDeclaration* '}'
 	*     ;
 	*/
	@Override public void exitRecordBody(RecordBodyContext ctx) { 
	    String rb = pop();    // }
        String classBodyDeclaration = "";
	    if (ctx.classBodyDeclaration() != null) {
	        int size = ctx.classBodyDeclaration().size();
	        classBodyDeclaration = reverse(size, " ");
	    }
	    String lb = pop();    // {
	    StringBuilder sb = new StringBuilder();
	    // TODO: adjust brackets
	    sb.append(lb).append('\n').append(indent(classBodyDeclaration)).append(rb).append('\n');
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
	    String recordHeader = pop();
	    String typeParameters = ctx.typeParameters() == null ? " " : pop();
	    String identifier = pop();
	    String record_ = pop();    // record keyword
	    StringBuilder sb = new StringBuilder();
	    sb.append(record_).append(' ').append(identifier).append(typeParameters).append(recordHeader).append(impl).append(recordBody);
	    push(sb);
	}
	
	/**
 	* recordHeader
 	*     : '(' recordComponentList? ')'
 	*     ;
 	*/
	@Override public void exitRecordHeader(RecordHeaderContext ctx) {
	    String rp = pop();    // )
	    String list = ctx.recordComponentList() == null ? "" : pop();
	    String lp = pop();    // (
	    StringBuilder sb = new StringBuilder();
	    sb.append(padParen(lp)).append(list).append(padParen(rp));
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
	        String expression = pop();
	        String equals = pop();    // =
	        String middle = "";
	        if (ctx.VAR() != null) {
	            String identifier = pop();
	            String var_ = pop();    // var keyword
	            middle = var_ + ' ' + identifier;
	        }
	        else {
	            String id = pop();
	            String type = pop();
	            middle = type + ' ' + type;
	        }
            String modifiers = "";
            if (ctx.variableModifier() != null) {
                int size = ctx.variableModifier().size();
                modifiers = reverse(size, " ");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(modifiers).append(middle).append(padOperator(equals)).append(expression);
            push(sb);
	    }
	    // otherwise, 'identifier' is already on the stack
	}
	
	/**
 	* resourceSpecification
 	*     : '(' resources ';'? ')'
 	*     ;
 	*/
	@Override public void exitResourceSpecification(ResourceSpecificationContext ctx) { 
	    String rp = pop();    // )
	    String semi = ctx.SEMI() == null ? "" : ";";
	    String resources = pop();
	    String lp = pop();    // (
	    StringBuilder sb = new StringBuilder();
	    sb.append(padParen(lp)).append(resources).append(semi).append(padParen(rp));
	    push(sb);
	}
	
	/**
 	* resources
 	*     : resource (';' resource)*
 	*     ;
 	*/
	@Override public void exitResources(ResourcesContext ctx) {
	    int size = ctx.resource().size();
	    push(reverse(size * 2 - 1, " ", 2));
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
            // ASSERT expression (':' expression)? ';'
            String semi = pop();  // ;
            String expression = "";
            String colon = "";
            String expression2 = "";
            if (ctx.expression().size() > 1) {
                expression2 = pop();
                colon = pop();  // :
            }
            expression = pop();
            String assert_ = pop();  // assert keyword
            StringBuilder sb = new StringBuilder();
            sb.append(assert_).append(' ').append(expression).append(colon).append(expression2).append(semi);   // TODO: pad colon?
            push(sb);
        }
        else if (ctx.IF() != null) {
            // IF parExpression statement (ELSE statement)?
            String elseStatement = "";
            if (ctx.ELSE() != null) {
                elseStatement = pop();
                String else_ = pop();  // else keyword
                elseStatement = new StringBuilder(else_).append(' ').append(elseStatement).toString();
            }
            String statement = pop();
            String parExpression = pop();
            String if_ = pop();  // if keyword
            StringBuilder sb = new StringBuilder();
            sb.append(if_).append(' ').append(parExpression).append(' ').append(statement).append(elseStatement);
            push(sb);
        }
        else if (ctx.FOR() != null) {
            // FOR '(' forControl ')' statement 
            String statement = pop();
            String rp = pop();  // )
            String forControl = pop();
            String lp = pop();  // (
            String for_ = pop();  // for keyword
            StringBuilder sb = new StringBuilder();
            sb.append(for_).append(' ').append(padParen(lp)).append(forControl).append(padParen(rp)).append(statement);
            push(sb);
        }
        else if (ctx.WHILE() != null) {
            // WHILE parExpression statement
            String statement = pop();
            String parExpression = pop();
            String while_ = pop();  // while keyword
            StringBuilder sb = new StringBuilder();
            sb.append(while_).append(' ').append(parExpression).append(statement);
            push(sb);
        }
        else if (ctx.DO() != null) {
            // DO statement WHILE parExpression ';'
            String semi = pop();  // ;
            String parExpression = pop();
            String while_ = pop();  // while keyword
            String statement = pop();
            String do_ = pop();  // do keyword
            StringBuilder sb = new StringBuilder();
            sb.append(do_).append(' ').append(statement).append(' ').append(while_).append(' ').append(parExpression).append(semi);
            push(sb);
        }
        else if (ctx.TRY() != null) {
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
                String block = pop();
                String spec = pop();
                String try_ = pop();  // try keyword
                StringBuilder sb = new StringBuilder();
                sb.append(try_).append(' ').append(spec).append(block).append(catchClause).append(finallyBlock);
                push(sb);
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
                String try_ = pop();  // try keyword
                StringBuilder sb = new StringBuilder();
                sb.append(try_).append(' ').append(block).append(catchClause).append(finallyBlock);
                push(sb);
            }
        }
        else if (ctx.SWITCH() != null) {
            // SWITCH parExpression '{' switchBlockStatementGroup* switchLabel* '}'
            String rp = pop();  // }
            String switchLabel = "";
            if (ctx.switchLabel() != null) {
                int size = ctx.switchLabel().size();
                switchLabel = reverse(size, " ");
            }
            String switchBlockStatementGroup = "";
            if (ctx.switchBlockStatementGroup() != null) {
                int size = ctx.switchBlockStatementGroup().size();
                switchBlockStatementGroup = reverse(size, " ");
            }
            String lp = pop();  // {
            String parExpression = pop();
            String switch_ = pop();  // switch keyword
            StringBuilder sb = new StringBuilder();
            sb.append(switch_).append(' ').append(parExpression).append(lp).append('\n').append(switchBlockStatementGroup).append(switchLabel).append('\n').append(rp);
            push(sb);
        }
        else if (ctx.SYNCHRONIZED() != null) {
            // SYNCHRONIZED parExpression block
            String block = pop();
            String parExpression = pop();
            String synchronized_ = pop();  // synchronized keyword
            StringBuilder sb = new StringBuilder();
            sb.append(synchronized_).append(' ').append(parExpression).append(block);
            push(sb);
        }
        else if (ctx.RETURN() != null || ctx.THROW() != null || ctx.YIELD() != null) {
            // these 3 are pretty much the same
            // RETURN expression? ';'
            // THROW expression ';'
            // YIELD expression ';'            
            String semi = pop();  //;
            String expression = ctx.expression() == null ? "" : pop();
            String keyword = pop();
            StringBuilder sb = new StringBuilder();
            sb.append(keyword).append(' ').append(expression).append(semi);
            push(sb);
        }
        else if (ctx.BREAK() != null || ctx.CONTINUE() != null) {
            // BREAK identifier? ';'
            // CONTINUE identifier? ';' 
            String semi = pop();  //;
            String identifier = ctx.identifier() == null ? "" : pop();
            String keyword = pop();
            StringBuilder sb = new StringBuilder();
            sb.append(keyword).append(' ').append(identifier).append(semi);
            push(sb);
        }
        else if (ctx.SEMI() != null) {
            // only these 2 choices are remaining with a semicolon
            // statementExpression=expression ';'
            // switchExpression ';'? // Java17      
            String semi = pop();  // ;
            String expression = pop();
            StringBuilder sb = new StringBuilder();
            sb.append(expression).append(semi);
            push(sb);
        }
        else if (ctx.COLON() != null) {
            // this is the only one left with a colon
            // identifierLabel=identifier ':' statement
            String statement = pop();
            String colon = pop();  // :
            String identifier = pop();
            StringBuilder sb = new StringBuilder();
            sb.append(identifier).append(padOperator(colon)).append(statement);
            push(sb);
        }
        /*
        else {
            // block is already on the stack, but needs adjusted
            String block = pop();
            if (block.endsWith("}")) {
                String[] lines = block.split("\n");
                String lastLine = lines[lines.length - 1];
                lastLine = outdent(lastLine);
                lines[lines.length - 1] = lastLine;
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    sb.append(line);
                    if (!line.endsWith("\n")) {
                        sb.append('\n');    
                    }
                }
                block = sb.toString();
            }
            push(block);
        }
        */
    }
	
	
    /**
     * superSuffix
     *     : arguments
     *     | '.' typeArguments? identifier arguments?
     *     ;
     */
	@Override public void exitSuperSuffix(SuperSuffixContext ctx) { 
	    if (ctx.identifier() != null) {
	        // second choice
	        String arguments = ctx.arguments() == null ? "" : " " + pop();
	        String identifier = pop();
	        String typeArguments = ctx.typeArguments() != null ? " " : pop();
	        String dot = pop();    // .
	        StringBuilder sb = new StringBuilder();
	        sb.append(dot).append(typeArguments).append(identifier).append(arguments);
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
	    StringBuilder sb = new StringBuilder();
	    sb.append(switchLabels).append(blockStatements);
	    push(sb);
	}
	
	/**
 	* // Java17
 	* switchExpression
 	*     : SWITCH parExpression '{' switchLabeledRule* '}'
 	*     ;
 	*/
	@Override public void exitSwitchExpression(SwitchExpressionContext ctx) { 
	    String rb = pop();    // }
	    String switchLabeledRule = "";
	    if (ctx.switchLabeledRule() != null) {
	        int size = ctx.switchLabeledRule().size();
	        switchLabeledRule = reverse(size, " ");
	    }
	    String lb = pop();    // {
	    String parExpression = pop();
	    String switch_ = pop();    // switch keyword
	    StringBuilder sb = new StringBuilder();
	    sb.append(switch_).append(' ').append(parExpression).append(lb).append('\n').append(switchLabeledRule).append('\n').append(rb);
	    push(sb);
	}
	
	/**
 	* switchLabel
 	*     : CASE (constantExpression=expression | enumConstantName=IDENTIFIER | typeType varName=identifier) ':'
 	*     | DEFAULT ':'
 	*     ;
 	*/
	@Override public void exitSwitchLabel(SwitchLabelContext ctx) { 
	    if (ctx.DEFAULT() != null) {
	        String colon = pop();    // :
	        String default_ = pop();    // default keyword
	        push(default_ + colon);
	    }
	    else {
	        String colon = pop();    // :
	        String label = "";
	        if (ctx.typeType() != null) {
	            String identifier = pop();
	            String typeType = pop();
	            label = typeType + " " + identifier;
	        }
	        else {
	            label = pop();   
	        }
	        String case_ = pop();
	        StringBuilder sb = new StringBuilder();
	        sb.append(case_).append(' ').append(label).append(colon);
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
	        String switchRuleOutcome = pop();
	        String operator = pop();
	        String choice = pop();
	        String case_ = pop();    // case keyword
	        StringBuilder sb = new StringBuilder();
	        sb.append(case_).append(' ').append(choice).append(padOperator(operator)).append(switchRuleOutcome);
	        push(sb);
	    }
	    else {
	        String switchRuleOutcome = pop();
	        String operator = pop();
	        String default_ = pop();    // default keyword
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
	    if (ctx.blockStatement() != null) {
            String blockStatement = "";
            if (ctx.blockStatement() != null) {
                int size = ctx.blockStatement().size();
                blockStatement = reverse(size, " ");
            }
            push(blockStatement);
	    }
	    // otherwise, 'block' is already on the stack
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
	             typeType = pop() + " " + typeType;
	        }
	        String q = pop();    // ?
            String annotation = "";
            if (ctx.annotation() != null) {
                int size = ctx.annotation().size();
                annotation = reverse(size, " ");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(annotation).append(padOperator(q)).append(typeType);
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
	        return;    // Just a semicolon in the type declaration
	    }
	    String declaration = pop();
	    StringBuilder typeDeclaration = new StringBuilder();
	    if (ctx.classOrInterfaceModifier() != null) {
	        int size = ctx.classOrInterfaceModifier().size();
	        List<String> parts = reverse(size);
	        for (int i = 0; i < size; i++) {
	             typeDeclaration.append(parts.get(i)).append(' ');   
	        }
	    }
	    typeDeclaration.append(declaration);
	    push(typeDeclaration);
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
                annotations.append(pop()).append(' ');
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
            annotations.append(pop()).append(' ');
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
	    if (ctx.RBRACK() != null) {
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
                            endPart.append(pop());    // annotation
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
	    StringBuilder typeType = new StringBuilder();
	    if (ctx.annotation() != null) {
	        List<String> anns = reverse(ctx.annotation().size() - annotationCount);
	        for (String a : anns) {
	            typeType.append(a).append(' ');
	        }
	    }
	    
	    // put it all together
	    typeType.append(type);
	    if (endPart.length() > 0) {
	        typeType.append(endPart);
	    }
	    push(typeType);
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
	    if (ctx.variableInitializer() != null) {
	        variableInitializer = pop();
	        equals = padOperator(pop());
	    }
	    StringBuilder variableDecl = new StringBuilder();
	    variableDecl.append(pop());    // variableDeclaratorId
	    variableDecl.append(equals);
	    variableDecl.append(variableInitializer);
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
	    variableDeclId.insert(0, pop());    // identifier
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
        String terminalText = node.getText();
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
    
    public void setBlankLinesAfterClassDeclaration(int lines) {
        blankLinesAfterClassDeclaration = lines;
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
            sb.insert(0, tab );
        }
        return sb.toString();
    }
    
    /**
     * Removes one indent from each line in the given string.
     */
    private String outdent(String s) {
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        String indent = getIndent();
        for (String line : lines) {
            if (line.startsWith(indent)) {
                line = line.substring(indent.length());
            }
            sb.append(line);
            if (!line.endsWith("\n")) {
                sb.append('\n');    
            }
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
     * Split the given string into lines and indent each line one more tab than
     * the current tab count.
     */
    private String indent(String s) {
        ++tabCount;
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        String indent = getIndent();
        for (String line : lines) {
            if (indent.isEmpty() || !line.startsWith(indent)) {
                line = line.trim();
                if (line.startsWith("*")) {
                    // assume it's a comment line
                    // TODO: verify this is always true
                    line = new StringBuilder(indent).append(' ').append(line).toString();
                }
                else if (!"".equals(line)) {
                    line = new StringBuilder(indent).append(line).toString();
                }
            }
            sb.append(line);
            if (!line.endsWith("\n")) {
                sb.append('\n');    
            }
        }
        --tabCount;
        return sb.toString();
    }
    
    /**
     * Assumes the given string is already indented but needs one additional indentation.    
     */
    private String indentAgain(String s) {
        StringBuilder sb = new StringBuilder();
        String[] lines = s.split("\n");
        for (String line : lines) {
            sb.append(tab).append(line);
            if (!line.endsWith("\n")) {
                sb.append('\n');    
            }
        }
        return sb.toString();
    }
    
    private String indentRBrace(String s) {
        s = s.trim();
        String start = s.substring(0, s.indexOf('}'));
        String rbrace = s.substring(s.indexOf('}'));
        if (!start.isEmpty()) {
            start = indent(start);
        }
        rbrace = indent(rbrace);
        return start + rbrace;
    }
    
    /**
     * Add blank lines to the end of the last item on the stack.  Note that all
     * blank lines are first removed then exactly <code>howMany</code> are added.
     * @return true if blank lines were added to the last item on the stack, false
     * if there were no items on the stack.
     */
    private boolean addBlankLines(int howMany) {
        if (!stack.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < howMany + 1; i++) {
                sb.append('\n');    
            }
            String ending = sb.toString();
            String last = pop();
            last = new StringBuilder(removeBlankLines(last, END)).append(ending).toString();
            push(last);
            return true;
        }
        return false;
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
        switch(whichEnd) {
            case START:
                while(sb.length() > 0 && sb.charAt(0) == '\n') {
                    sb.deleteCharAt(0);
                }
                break;
            case END:
                while(sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                break;
            case BOTH:
            default:
                while(sb.length() > 0 && sb.charAt(0) == '\n') {
                    sb.deleteCharAt(0);
                }
                while(sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
                    sb.deleteCharAt(sb.length() - 1);
                }
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
     * Given "a,b,c" on the stack, where each character is a separate string on the stack,
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
                        // of the previous item.
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
                    if (previous != null && previous.indexOf(comment) > -1 && previous.indexOf(comment.trim()) > -1) {
                        // already have this comment added as an end of line comment
                        // for the previous token
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
	    if (howMany <= 0) {
	        return "";   
	    }
	    
	    // gather the modifiers into a list, then reverse the list so they are
	    // in the right order
	    List<String> modifiers = reverse(howMany);
	    if (sortModifiers) {
	        Collections.sort(modifiers, modifierComparator);    
	    }
        
        // modifiers shouldn't be on multiple lines, but comments preceding the
        // modifier may already be attached and can have multiple lines.
        // Handle the multiple lines here.
        StringBuilder sb = new StringBuilder();
        for (String modifier : modifiers) {
            modifier = modifier.trim();
            String[] lines = modifier.split("\n");
            if (lines.length == 1) {
                // something like "public "
                sb.append(modifier).append(' ');    
                if (modifier.startsWith("@")) {
                    sb.append('\n');   
                }
            }
            else {
                // multiple lines are likely comment lines
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    sb.append(line);
                    // annotations should be on a separate line also, but they
                    // should have already been handled above
                    if (i < lines.length - 1 || line.startsWith("@")) {
                        sb.append('\n');
                    }
                }
                sb.append(' ');
            }
        }
        
        // next indent each line of the modifier
        String indented = indent(sb.toString());
        sb = new StringBuilder(indented);
        if (sb.length() > 0) {
            sb.append(' ');    
        }
	    if (sb.length() == 0) {
	        indent(sb);   
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
	    // TOOD: update this list, need sealed and non-sealed for sure, others?
        private String modifiers = "public protected private abstract static final synchronized native strictfp transient volatile";
        
	    public int compare(String a, String b) {
	        String a_ = a.trim();
	        String b_ = b.trim();
	        
	        // comments may be included in the choices
	        if (a_.startsWith("/") || a_.startsWith("*")) {
	            return -1;    
	        }
	        if (b_.startsWith("/") || b_.startsWith("*")) {
	            return 1;    
	        }
	        
	        // annotations may be included in the choices
	        if (a_.startsWith("@")) {
	            return -1;   
	        }
	        if (b_.startsWith("@")) {
	            return 1;   
	        }
	        
	        return modifiers.indexOf(a_) - modifiers.indexOf(b_);
	    }
	};
	
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
	
	/**
 	 * Removes all excess whitespace from each of the lines in <code>s</code>.	
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
	
	
//}}}    

}