
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
            
            // parse and beautify the buffer contents
            JavaParserBeautyListener listener = new JavaParserBeautyListener(16 * 1024, tokens);
            listener.setUseSoftTabs(true);
            listener.setIndentWidth(4);
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
	
	}
	
	/**
 	* annotationConstantRest
 	*     : variableDeclarators
 	*     ;
 	*/
	@Override public void exitAnnotationConstantRest(AnnotationConstantRestContext ctx) { }
	
	/**
 	* annotationMethodOrConstantRest
 	*     : annotationMethodRest
 	*     | annotationConstantRest
 	*     ;
 	*/
	@Override public void exitAnnotationMethodOrConstantRest(AnnotationMethodOrConstantRestContext ctx) { }
	
	/**
 	* annotationMethodRest
 	*     : identifier '(' ')' defaultValue?
 	*     ;
 	*/
	@Override public void exitAnnotationMethodRest(AnnotationMethodRestContext ctx) { }
	
	/**
 	* 	annotationTypeBody
 	*     : '{' (annotationTypeElementDeclaration)* '}'
 	*     ;
 	*/
 	@Override public void exitAnnotationTypeBody(AnnotationTypeBodyContext ctx) { }
 	
 	/**
  	* annotationTypeDeclaration
  	*     : '@' INTERFACE identifier annotationTypeBody
  	*     ;
  	*/
	@Override public void exitAnnotationTypeDeclaration(AnnotationTypeDeclarationContext ctx) { }
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
            stack.pop();    // pop the semicolon
            StringBuilder type = new StringBuilder();
            if (ctx.typeType() != null) {
                List<String> parts = reverse(2);    // typeType and annotationMethodOrConstantRest
                for (int i = 0; i < 2; i++) {
                    type.append(parts.get(i)).append(' ');   
                }
            }
            else {
                type.append(stack.pop());   // one of the other 5 choices   
            }
            type.append(';');
            stack.push(type.toString());
        }
    }
    
	@Override public void exitArguments(ArgumentsContext ctx) { }
	@Override public void exitArrayCreatorRest(ArrayCreatorRestContext ctx) { }
	@Override public void exitArrayInitializer(ArrayInitializerContext ctx) { }
	
	/**
 	* block
 	*     : '{' blockStatement* '}'
 	*     ;
 	*/
	@Override public void exitBlock(BlockContext ctx) { 
	    String lbrace = stack.pop();
	    StringBuilder blockStatements = new StringBuilder();
	    if (ctx.blockStatement() != null) {
	        int size = ctx.blockStatement().size();
	        List<String> parts = reverse(size);
	        for (int i = 0; i < size; i++) {
	            blockStatements.append(parts.get(i)).append('\n');   
	        }
	    }
	    String rbrace = stack.pop();
	    blockStatements.insert(0, '\n');
	    blockStatements.insert(0, rbrace);
	    blockStatements.append('\n').append(lbrace);
	    stack.push(blockStatements.toString());
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
	        stack.pop();    // semi
	        String localVariableDeclaration = stack.pop();
	        stack.push(localVariableDeclaration + ';');
	        return;
	    }
	    // Nothing else to do here, one of the other choices should already be on the stack.
	}
	
	@Override public void exitCatchClause(CatchClauseContext ctx) { }
	@Override public void exitCatchType(CatchTypeContext ctx) { }
	
	/**
 	* classBody
 	*     : '{' classBodyDeclaration* '}'
 	*     ;
 	*/
	@Override public void exitClassBody(ClassBodyContext ctx) { 
	    String rbrace = stack.pop();
	    StringBuilder classBodyDecl = new StringBuilder();
	    if (ctx.classBodyDeclaration() != null) {
	        int size = ctx.classBodyDeclaration().size();
	        List<String> parts = reverse(size);
	        for (String part : parts) {
	            classBodyDecl.append(indent(part)).append('\n');   
	        }
	        trimEnd(classBodyDecl);
	    }
	    String lbrace = stack.pop();
	    
	    StringBuilder classBody = new StringBuilder();
	    classBody.append('\n').append(lbrace).append('\n').append(classBodyDecl).append('\n').append(rbrace);
	    stack.push(classBody.toString());
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
	         StringBuilder block = new StringBuilder(stack.pop());
	         if (ctx.STATIC() != null) {
	             stack.pop();    // static keyword
	             block.insert(0, "static ");
	         }
	         stack.push(block.toString());
	         return;
	    }
	    else {
            String memberDeclaration = stack.pop();
            StringBuilder modifiers = new StringBuilder();
            if (ctx.modifier() != null) {
                int size = ctx.modifier().size();
                List<String> parts = reverse(size);
                for (String part : parts) {
                    modifiers.append(part).append(' ');
                }
            }
            modifiers.append(memberDeclaration);
            stack.push(modifiers.toString());
	    }
	}
	
	
	@Override public void exitClassCreatorRest(ClassCreatorRestContext ctx) { }
	
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
	    String classBody = stack.pop();
	    String permitsList = "";
	    if (ctx.PERMITS() != null) {
	        String typeList = stack.pop();
	        permitsList = "permits " + typeList;
	        stack.pop();    // permits keyword
	    }
	    String implementsList = "";
	    if (ctx.IMPLEMENTS() != null) {
	         String typeList = stack.pop();
	         implementsList = "implements " + typeList;
	         stack.pop();     // implements keyword
	    }
	    String extendsType = "";
	    if (ctx.EXTENDS() != null) {
	         String typeType = stack.pop();
	         extendsType = "extends " + typeType;
	         stack.pop();    // extends keyword
	    }
	    String typeParameters = "";
	    if (ctx.typeParameters() != null) {
	         typeParameters = stack.pop();   
	    }
	    String identifier = stack.pop();
	    stack.pop();    // class keyword
	    
	    StringBuilder classDecl = new StringBuilder();
	    classDecl.append("class ").append(identifier).append(' ').append(typeParameters).append(' ').append(extendsType).append(implementsList).append(permitsList).append(classBody);
	    stack.push(classDecl.toString());
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
 	*/
	@Override public void exitClassOrInterfaceType(ClassOrInterfaceTypeContext ctx) { 
	    // TODO: fill this in!
	}
	
	/**
 	* classType
 	*     : (classOrInterfaceType '.')? annotation* identifier typeArguments?
 	*     ;
 	*/
	@Override public void exitClassType(ClassTypeContext ctx) { 
	    // TODO: fill this in!
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
            String moduleDeclaration = stack.pop();
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
                packageDeclaration = stack.pop();
                packageDeclaration = getBlankLines(blankLinesBeforePackage) + removeBlankLines(packageDeclaration, BOTH);
                packageDeclaration = removeExcessWhitespace(packageDeclaration) + getBlankLines(blankLinesAfterPackage);
            }
            
            output.append(packageDeclaration);
            output.append(importDeclarations);
            output.append(typeDeclarations);
        }
	    // all done!
	}
	
	@Override public void exitConstDeclaration(ConstDeclarationContext ctx) { }
	@Override public void exitConstantDeclarator(ConstantDeclaratorContext ctx) { }
	@Override public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) { }
	@Override public void exitCreatedName(CreatedNameContext ctx) { }
	@Override public void exitCreator(CreatorContext ctx) { }
	@Override public void exitDefaultValue(DefaultValueContext ctx) { }
	
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
	    String rbrace = stack.pop();
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
	    String lbrace = stack.pop();
	    elements.insert(0, lbrace);
	    elements.append(rbrace);
	    stack.push(elements.toString());
	}
	
	/**
 	* elementValuePair
 	*     : identifier '=' elementValue
 	*     ;
 	*/
	@Override public void exitElementValuePair(ElementValuePairContext ctx) { }
	
	/**
 	* 	elementValuePairs
 	*     : elementValuePair (',' elementValuePair)*
 	*     ;
 	*/
    @Override public void exitElementValuePairs(ElementValuePairsContext ctx) { }
	@Override public void exitEnhancedForControl(EnhancedForControlContext ctx) { }
	@Override public void exitEnumBodyDeclarations(EnumBodyDeclarationsContext ctx) { }
	@Override public void exitEnumConstant(EnumConstantContext ctx) { }
	@Override public void exitEnumConstants(EnumConstantsContext ctx) { }
	@Override public void exitEnumDeclaration(EnumDeclarationContext ctx) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }
	@Override public void exitExplicitGenericInvocation(ExplicitGenericInvocationContext ctx) { }
	@Override public void exitExplicitGenericInvocationSuffix(ExplicitGenericInvocationSuffixContext ctx) { }

	
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
	            String creator = stack.pop();
	            stack.pop();    // new keyword
	            stack.push("new " + creator);
	            return;
	        }
	        if (ctx.classType() != null || ctx.typeType() != null) {
	            // classType '::' typeArguments? NEW
	            // typeType '::' (typeArguments? identifier | NEW)
	            // in typeType case, there is a NEW, so there won't be an identifier,
	            // and the processing is the same as classType. Need to handle the
	            // case with an identifier separately.
	            stack.pop();    // new keyword
	            String typeArguments = "";
	            if (ctx.typeArguments() != null) {
	                typeArguments = stack.pop();
	            }
	            stack.pop();    // :: 
	            StringBuilder classType = new StringBuilder();
                classType.append(stack.pop());  // classType
	            classType.append(padOperator("::"));
	            classType.append(typeArguments);
	            classType.append("new");
	            stack.push(classType.toString());
	            return;
	        }
	    }
	    if (ctx.typeType() != null && ctx.identifier() != null) {
	        // typeType '::' (typeArguments? identifier | NEW)
	        // the case with NEW has already been handled above, just need to do 
	        // the case with an identifier
	        String identifier = stack.pop();
            String typeArguments = "";
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();
            }
            stack.pop();    // :: 
            StringBuilder typeType = new StringBuilder();
            typeType.append(stack.pop());   // typeType
            typeType.append(padOperator("::"));
            typeType.append(typeArguments);
            if (typeArguments.length() > 0) {
                typeType.append(' ');
            }
            typeType.append(identifier);
            stack.push(typeType.toString());
            return;
	    }
	    if (ctx.COLONCOLON() != null) {
	        // expression '::' typeArguments? identifier
	        // This is the only remaining item with ::
	        String identifier = stack.pop();
            String typeArguments = "";
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();
            }
            stack.pop();    // :: 
            StringBuilder expression = new StringBuilder();
            expression.append(stack.pop());   // typeType
            expression.append(padOperator("::"));
            expression.append(typeArguments);
            if (typeArguments.length() > 0) {
                expression.append(' ');
            }
            expression.append(identifier);
            stack.push(expression.toString());
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
	        if (ctx.identifier() != null) {
	            String identifier = stack.pop();
	            stack.pop();    // . bop
	            StringBuilder expression = new StringBuilder(stack.pop());
	            expression.append('.');
	            expression.append(identifier);
	            stack.push(expression.toString());
	            return;
	        }
	        if (ctx.methodCall() != null) {
	            String methodCall = stack.pop();
	            stack.pop();    // . bop
	            StringBuilder expression = new StringBuilder(stack.pop());
	            expression.append('.');
	            expression.append(methodCall);
	            stack.push(expression.toString());
	            return;
	        }
	        if (ctx.THIS() != null) {
	            String this_ = stack.pop();
	            stack.pop();    // . bop
	            StringBuilder expression = new StringBuilder(stack.pop());
	            expression.append('.');
	            expression.append(this_);
	            stack.push(expression.toString());
	            return;
	        }
	        if (ctx.NEW() != null) {
	            String innerCreator = stack.pop();
	            String typeArgs = "";
	            if (ctx.nonWildcardTypeArguments() != null) {
	                 typeArgs = stack.pop() + ' ';   
	            }
	            stack.pop();    // new keyword
	            stack.pop();    // . bop
	            StringBuilder expression = new StringBuilder(stack.pop());
	            expression.append('.');
	            expression.append("new ");
	            expression.append(typeArgs);
	            expression.append(innerCreator);
	            stack.push(expression.toString());
	            return;
	        }
	        if (ctx.SUPER() != null) {
	            String superSuffix = stack.pop();
	            stack.pop();    // super keyword
	            stack.pop();    // . bop
	            StringBuilder expression = new StringBuilder(stack.pop());
	            expression.append('.');
	            expression.append("super ");
	            expression.append(superSuffix);
	            stack.push(expression.toString());
	            return;
	        }
	        if (ctx.explicitGenericInvocation() != null) {
	            String invocation = stack.pop();
	            stack.pop();    // . bop
	            StringBuilder expression = new StringBuilder(stack.pop());
	            expression.append('.');
                expression.append(invocation);
	            stack.push(expression.toString());
	            return;
	        }
	        if (ctx.typeType() != null) {
	            // '(' annotation* typeType ('&' typeType)* ')' expression
	            String expression = stack.pop();
	            stack.pop();    // )
	            String typeTypes = "";
	            if (ctx.typeType().size() > 1) {
	                List<String> parts = new ArrayList<String>();
	                for (int i = 0; i < ctx.typeType().size() - 1; i++) {
	                    parts.add(stack.pop());    // typeType
	                    parts.add(stack.pop());    // &
	                }
	                Collections.reverse(parts);
	                StringBuilder sb = new StringBuilder();
	                for (String part : parts) {
	                    sb.append(part).append(' ');   
	                }
	                typeTypes = sb.toString();
	            }
	            String typeType = stack.pop();
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
	            stack.pop();    // (
	            StringBuilder sb = new StringBuilder();
	            sb.append('(');
	            sb.append(annotations);
	            sb.append(typeType);
	            if (typeTypes.length() > 0) {
	                sb.append(' ').append(typeTypes);   
	            }
	            sb.append(')');
	            sb.append(expression);
	            stack.push(sb.toString());
	            return;
	        }
	        if (ctx.INSTANCEOF() != null) {
	            // expression bop=INSTANCEOF (typeType | pattern)
	            String typeOrPattern = stack.pop();
	            String instanceOf = stack.pop();
	            StringBuilder expression = new StringBuilder(stack.pop());
	            expression.append(' ').append(instanceOf).append(' ').append(typeOrPattern);
	            stack.push(expression.toString());
	            return;
	        }
	        
	        // there are 3 remaining cases with one expression:
            // | expression postfix=('++' | '--')
            // | prefix=('+'|'-'|'++'|'--') expression
            // | prefix=('~'|'!') expression
            String post = stack.peek();
            if (post.indexOf('+') > -1 || post.indexOf('-') > -1) {
                // it's the first case, the postfix expression
                post = stack.pop();     // ++ or --
                StringBuilder expression = new StringBuilder(stack.pop());
                expression.append(padOperator(post));
                stack.push(expression.toString());
                return;
            }
            else {
                // it's one of the prefix expressions, and they are all the same
                StringBuilder expression = new StringBuilder(stack.pop());
                expression.append(padOperator(stack.pop()));    // one of + - ++ -- ~ !
                stack.push(expression.toString());
                return;
            }
            
            
	    }
	    else if (expressionCount == 2) {
	        if (ctx.RBRACK() != null) {
	            // expression '[' expression ']'
	            stack.pop();    // ]
	            String expression = stack.pop();
	            stack.pop();    // [
	            StringBuilder sb = new StringBuilder(stack.pop());
	            sb.append('[').append(expression).append(']');
	            stack.push(sb.toString());
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
            
            String expression = stack.pop();
            String bop = stack.pop();
            StringBuilder sb = new StringBuilder(stack.pop());
            sb.append(padOperator(bop));
            sb.append(expression);
            stack.push(sb.toString());
            return;
	    }
	    else if (expressionCount == 3) {
	        // only have one case with 3 expressions
	        //<assoc=right> expression bop='?' expression ':' expression
	        String exp2 = stack.pop();
	        stack.pop();    // :
	        String exp1 = stack.pop();
	        stack.pop();    // ?
	        StringBuilder expression = new StringBuilder(stack.pop());
	        expression.append(padOperator("?")).append(exp1).append(padOperator(":")).append(exp2);
	        stack.push(expression.toString());
	        return;
	    }
	}
	
	/**
 	* expressionList
 	*     : expression (',' expression)*
 	*     ;
 	*/
	@Override public void exitExpressionList(ExpressionListContext ctx) { 
	    
	}
	
	
	/**
 	* 	fieldDeclaration
 	*     : typeType variableDeclarators ';'
 	*     ;
 	*/
    @Override public void exitFieldDeclaration(FieldDeclarationContext ctx) { 
        stack.pop();    // semicolon
        String variableDeclarators = stack.pop();
        StringBuilder fieldDecl = new StringBuilder(stack.pop());   // typeType
        fieldDecl.append(' ').append(variableDeclarators).append(';');
        stack.push(fieldDecl.toString());
    }
    
	@Override public void exitFinallyBlock(FinallyBlockContext ctx) { }
	@Override public void exitFloatLiteral(FloatLiteralContext ctx) { }
	@Override public void exitForControl(ForControlContext ctx) { }
	@Override public void exitForInit(ForInitContext ctx) { }
	@Override public void exitFormalParameter(FormalParameterContext ctx) { }
	@Override public void exitFormalParameterList(FormalParameterListContext ctx) { }
	@Override public void exitFormalParameters(FormalParametersContext ctx) { }
	@Override public void exitGenericConstructorDeclaration(GenericConstructorDeclarationContext ctx) { }
	@Override public void exitGenericInterfaceMethodDeclaration(GenericInterfaceMethodDeclarationContext ctx) { }
	
	
	/**
 	* genericMethodDeclaration
 	*     : typeParameters methodDeclaration
 	*     ;
 	*/
	@Override public void exitGenericMethodDeclaration(GenericMethodDeclarationContext ctx) { 
	    String method = stack.pop();
	    String typeParameters = stack.pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(typeParameters).append(' ').append(method);
	    stack.push(sb.toString());
	}
	
	@Override public void exitGuardedPattern(GuardedPatternContext ctx) { }
	
	
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
	    String semi = stack.pop();
	    String dotstar = "";
	    if (ctx.DOT() != null) {
	        stack.pop();    // *
	        stack.pop();    // .
	        dotstar = ".*";
	    }
	    String qualifiedName = stack.pop();
	    String static_ = "";
	    if (ctx.STATIC() != null) {
	        stack.pop();    // static keyword
	        static_ = "static ";
	    }
	    stack.pop();    // import keyword
	    StringBuilder importDecl = new StringBuilder();
	    importDecl.append("import ").append(static_).append(qualifiedName).append(dotstar).append(semi);
	    stack.push(importDecl.toString());
	}
	
	@Override public void exitInnerCreator(InnerCreatorContext ctx) { }
	@Override public void exitIntegerLiteral(IntegerLiteralContext ctx) { }
	@Override public void exitInterfaceBody(InterfaceBodyContext ctx) { }
	@Override public void exitInterfaceBodyDeclaration(InterfaceBodyDeclarationContext ctx) { }
	@Override public void exitInterfaceCommonBodyDeclaration(InterfaceCommonBodyDeclarationContext ctx) { }
	@Override public void exitInterfaceDeclaration(InterfaceDeclarationContext ctx) { }
	
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
    
	@Override public void exitInterfaceMethodDeclaration(InterfaceMethodDeclarationContext ctx) { }
	@Override public void exitInterfaceMethodModifier(InterfaceMethodModifierContext ctx) { }
	@Override public void exitLambdaBody(LambdaBodyContext ctx) { }
	@Override public void exitLambdaExpression(LambdaExpressionContext ctx) { }
	@Override public void exitLambdaLVTIList(LambdaLVTIListContext ctx) { }
	@Override public void exitLambdaLVTIParameter(LambdaLVTIParameterContext ctx) { }
	@Override public void exitLambdaParameters(LambdaParametersContext ctx) { }
	@Override public void exitLastFormalParameter(LastFormalParameterContext ctx) { }
	@Override public void exitLiteral(LiteralContext ctx) { }
	
	
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
	    String declaration = stack.pop();
	    StringBuilder typeDeclaration = new StringBuilder();
	    if (ctx.classOrInterfaceModifier() != null) {
	        int size = ctx.classOrInterfaceModifier().size();
	        List<String> parts = reverse(size);
	        for (int i = 0; i < size; i++) {
	             typeDeclaration.append(parts.get(i)).append(' ');   
	        }
	    }
	    typeDeclaration.append(declaration);
	    stack.push(typeDeclaration.toString());
	}
	
	
	@Override public void exitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) { }
	
	
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
	
	@Override public void exitMethodCall(MethodCallContext ctx) { }
	
	
	/**
 	* methodDeclaration
 	*     : typeTypeOrVoid identifier formalParameters ('[' ']')*
 	*       (THROWS qualifiedNameList)?
 	*       methodBody
 	*     ;
 	*/
	@Override public void exitMethodDeclaration(MethodDeclarationContext ctx) { 
	    String methodBody = stack.pop();
	    String throwsList = "";
	    if (ctx.THROWS() != null) {
	        String qualifiedNameList = stack.pop();
	        stack.pop();    // throws keyword
	        throwsList = "throws " + qualifiedNameList;
	    }
	    StringBuilder brackets = new StringBuilder();
	    if (ctx.RBRACK() != null) {
	        int size = ctx.RBRACK().size() * 2;     // * 2 for LBRACK
	        for (int i = 0; i < size; i++) {
	            stack.pop();    
	            if (i % 2 == 0) {
	                brackets.append("[]");    
	            }
	        }
	    }
	    String formalParameters = stack.pop();
	    String identifier = stack.pop();
	    String typeOrVoid = stack.pop();
	    StringBuilder method = new StringBuilder();
	    method.append(typeOrVoid).append(' ').append(identifier).append(' ').append(formalParameters).append(' ').append(brackets).append(throwsList).append(methodBody);
	    stack.push(method.toString());
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
	    String rbrace = stack.pop();
	    StringBuilder moduleDirectives = new StringBuilder();
	    if (ctx.moduleDirective() != null) {
	        int size = ctx.moduleDirective().size();
	        List<String> directives = reverse(size);
	        for (int i = 0; i < size; i++) {
	            moduleDirectives.append(directives.get(i)).append('\n');   
	        }
	    }
	    String lbrace = stack.pop() + '\n';
	    StringBuilder moduleBody = new StringBuilder();
	    moduleBody.append(lbrace);
	    moduleBody.append(indent(moduleDirectives.toString()));
	    moduleBody.append(rbrace);
	    stack.push(moduleBody.toString());
	}

	
	/**
 	*  moduleDeclaration
 	*     : OPEN? MODULE qualifiedName moduleBody
 	*     ;
 	*/
    @Override public void exitModuleDeclaration(ModuleDeclarationContext ctx) { 
        String moduleBody = stack.pop();
        String qualifiedName = stack.pop();
        stack.pop();    // module keyword
        StringBuilder moduleDeclaration = new StringBuilder("module ");
        moduleDeclaration.append(qualifiedName);
        moduleDeclaration.append(brokenBracket ? '\n' : ' ');
        moduleDeclaration.append(moduleBody);
        if (ctx.OPEN() != null) {
            moduleDeclaration.insert(0, "open ");    
        }
        stack.push(moduleDeclaration.toString());
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
	    String semi = stack.pop();
	    StringBuilder moduleDirective = new StringBuilder();
	    if (ctx.REQUIRES() != null) {
	        // REQUIRES requiresModifier* qualifiedName ';'
	        moduleDirective.append("requires ");
	        String qualifiedName = stack.pop();
	        if (ctx.requiresModifier() != null) {
	            int size = ctx.requiresModifier().size();
	            List<String> parts = reverse(size);
	            for (int i = 0; i < size; i++) {
	                moduleDirective.append(parts.get(i)).append(' ');
	            }
	        }
	        moduleDirective.append(qualifiedName);
	        stack.pop();    // requires keyword
	    }
	    else if (ctx.EXPORTS() != null) {
	        // EXPORTS qualifiedName (TO qualifiedName)? ';'
	        moduleDirective.append("exports ");
	        if (ctx.TO() != null) {
	            String qualifiedName2 = stack.pop();
	            stack.pop();    // to keyword
	            String qualifiedName = stack.pop();
	            moduleDirective.append(qualifiedName).append(" to ").append(qualifiedName2);   
	        }
	        else {
	            String qualifiedName = stack.pop();
	            moduleDirective.append(qualifiedName);
	        }
	        stack.pop();    // exports keyword
	    }
	    else if (ctx.OPENS() != null) {
	        // OPENS qualifiedName (TO qualifiedName)? ';'
	        moduleDirective.append("opens ");
	        if (ctx.TO() != null) {
	            String qualifiedName2 = stack.pop();
	            stack.pop();    // to keyword
	            String qualifiedName = stack.pop();
	            moduleDirective.append(qualifiedName).append(" to ").append(qualifiedName2);   
	        }
	        else {
	            String qualifiedName = stack.pop();
	            moduleDirective.append(qualifiedName);
	        }
	        stack.pop();    // opens keyword
	    }
	    else if (ctx.USES() != null) {
	        // USES qualifiedName ';'
	        moduleDirective.append("uses ");
            String qualifiedName = stack.pop();
            moduleDirective.append(qualifiedName);
            stack.pop();    // uses keyword
	    }
	    else if (ctx.PROVIDES() != null) {
	        // PROVIDES qualifiedName WITH qualifiedName ';'
	        moduleDirective.append("provides ");
            String qualifiedName2 = stack.pop();
            stack.pop();    // with keyword
            String qualifiedName = stack.pop();
            moduleDirective.append(qualifiedName).append(" with ").append(qualifiedName2); 
            stack.pop();    // provides keyword
	    }
	    moduleDirective.append(semi);
        stack.push(moduleDirective.toString());
	}
	
	@Override public void exitNonWildcardTypeArguments(NonWildcardTypeArgumentsContext ctx) { }
	@Override public void exitNonWildcardTypeArgumentsOrDiamond(NonWildcardTypeArgumentsOrDiamondContext ctx) { }
	
	/**
 	*  packageDeclaration
 	*      : annotation* PACKAGE qualifiedName ';'
 	*      ;
 	*/
	@Override public void exitPackageDeclaration(PackageDeclarationContext ctx) { 
        String semi = stack.pop();
        String qualifiedName = stack.pop();
        stack.pop();    // package keyword
        StringBuilder packageDecl = new StringBuilder();
        if (ctx.annotation() != null) {
            int size = ctx.annotation().size();
            List<String> parts = reverse(size);
            for (int i = 0; i < size; i++) {
                packageDecl.append(parts.get(i)).append(' ');   
            }
            packageDecl.append('\n');
        }
        packageDecl.append("package ");
        packageDecl.append(qualifiedName);
        packageDecl.append(semi);
        stack.push(packageDecl.toString());
	}
	
	@Override public void exitParExpression(ParExpressionContext ctx) { }
	@Override public void exitPattern(PatternContext ctx) { }
	@Override public void exitPrimary(PrimaryContext ctx) { }
	
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
	    stack.push(qualifiedName.toString());
	}
	
	@Override public void exitQualifiedNameList(QualifiedNameListContext ctx) { }
	@Override public void exitReceiverParameter(ReceiverParameterContext ctx) { }
	
	/**
 	* recordBody
 	*     : '{' classBodyDeclaration* '}'
 	*     ;
 	*/
	@Override public void exitRecordBody(RecordBodyContext ctx) { }
	
	/**
 	* recordComponent
 	*     : typeType identifier
 	*     ;
 	*/
	@Override public void exitRecordComponent(RecordComponentContext ctx) { }
	
	/**
 	* recordComponentList
 	*     : recordComponent (',' recordComponent)*
 	*     ;
 	*/
	@Override public void exitRecordComponentList(RecordComponentListContext ctx) { }
	
	/**
 	* recordDeclaration
 	*     : RECORD identifier typeParameters? recordHeader
 	*       (IMPLEMENTS typeList)?
 	*       recordBody
 	*     ;
 	*/
	@Override public void exitRecordDeclaration(RecordDeclarationContext ctx) { }
	
	/**
 	* recordHeader
 	*     : '(' recordComponentList? ')'
 	*     ;
 	*/
	@Override public void exitRecordHeader(RecordHeaderContext ctx) { }
	
	/**
 	* requiresModifier
 	* 	: TRANSITIVE
 	* 	| STATIC
 	* 	;
 	*/
	@Override public void exitRequiresModifier(RequiresModifierContext ctx) { 
	    // Nothing to do here, one or more of the choices should already be on the stack
	}
	
	@Override public void exitResource(ResourceContext ctx) { }
	@Override public void exitResourceSpecification(ResourceSpecificationContext ctx) { }
	@Override public void exitResources(ResourcesContext ctx) { }
	@Override public void exitStatement(StatementContext ctx) { }
	@Override public void exitSuperSuffix(SuperSuffixContext ctx) { }
	@Override public void exitSwitchBlockStatementGroup(SwitchBlockStatementGroupContext ctx) { }
	@Override public void exitSwitchExpression(SwitchExpressionContext ctx) { }
	@Override public void exitSwitchLabel(SwitchLabelContext ctx) { }
	@Override public void exitSwitchLabeledRule(SwitchLabeledRuleContext ctx) { }
	@Override public void exitSwitchRuleOutcome(SwitchRuleOutcomeContext ctx) { }
	@Override public void exitTypeArgument(TypeArgumentContext ctx) { }
	@Override public void exitTypeArguments(TypeArgumentsContext ctx) { }
	@Override public void exitTypeArgumentsOrDiamond(TypeArgumentsOrDiamondContext ctx) { }
	@Override public void exitTypeBound(TypeBoundContext ctx) { }
	
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
	    String declaration = stack.pop();
	    StringBuilder typeDeclaration = new StringBuilder();
	    if (ctx.classOrInterfaceModifier() != null) {
	        int size = ctx.classOrInterfaceModifier().size();
	        List<String> parts = reverse(size);
	        for (int i = 0; i < size; i++) {
	             typeDeclaration.append(parts.get(i)).append(' ');   
	        }
	    }
	    typeDeclaration.append(declaration);
	    stack.push(typeDeclaration.toString());
	}
	
	@Override public void exitTypeList(TypeListContext ctx) { }
	@Override public void exitTypeParameter(TypeParameterContext ctx) { }
	@Override public void exitTypeParameters(TypeParametersContext ctx) { }
	
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
	            endPart.append(stack.pop());    // ]
	            endPart.append(stack.pop());    // [
	            
                // any annotations?
                if (ctx.annotation() != null) {
                    for (int j = 0; j < ctx.annotation().size(); j++) {
                        String ann = stack.peek();
                        if (ann.indexOf('@') > -1) {    
                            // using indexOf to be able to check for altAnnotationQualifiedName
                            // as well as regular annotation
                            endPart.append(stack.pop());    // annotation
                            ++ annotationCount;
                        }
                        else {
                            break;   
                        }
                    }
                }
	        }
	    }
	    
	    String type = stack.pop();    // one of (classOrInterfaceType | primitiveType)
	    
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
	        typeType.append(' ').append(endPart);
	    }
	    stack.push(typeType.toString());
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
	    printStack("variable declarator");
	    String variableInitializer = "";
	    String equals = "";
	    if (ctx.variableInitializer() != null) {
	        variableInitializer = stack.pop();
	        equals = padOperator(stack.pop());
	    }
	    StringBuilder variableDecl = new StringBuilder();
	    variableDecl.append(stack.pop());    // variableDeclaratorId
	    variableDecl.append(equals);
	    variableDecl.append(variableInitializer);
	    stack.push(variableDecl.toString());
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
	    variableDeclId.insert(0, stack.pop());    // identifier
	    stack.push(variableDeclId.toString());
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
	    if (ctx.COMMA() != null) {
	        size += ctx.COMMA().size();
	    }
	    String variableDecls = reverse(size, " ", 2);    // TODO: check this, space or comma?
	    stack.push(variableDecls);
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
        stack.push(terminalText);
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
            String last = stack.pop();
            last = new StringBuilder(removeBlankLines(last, END)).append(ending).toString();
            stack.push(last);
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
     * Pops <code>howMany</code> items off of the stack.    
     */
    public void pop(int howMany) {
        for (int i = 0; i < howMany; i++) {
            stack.pop();   
        }
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
            list.add(stack.pop());    
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
                    if (t.getChannel() == JavaLexer.WS &&
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
                        if (t.getChannel() == JavaLexer.WS &&
                            (tokenText.indexOf('\n') > -1 || tokenText.indexOf('\r') > -1)) {
                            hasLineEnder = true;
                            break;
                        }
                    }
                    if (hasLineEnder) {
                        // have "token comment \n", append this comment to the end
                        // of the previous item.
                        StringBuilder item = new StringBuilder(stack.pop());
                        String comment = nextCommentToken.getText();
                        if (item.indexOf(comment) == -1) {
                            item.append(tab).append(comment);
                            stack.push(item.toString());
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
                    String current = stack.pop();
                    String previous = stack.peek();
                    stack.push(current);
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
                            current = stack.pop();
                            previous = stack.peek();
                            if (previous != null && previous.indexOf(comment) == -1) {
                                previous = stack.pop();
                                previous = new StringBuilder(trimEnd(previous)).append(comment).toString();
                                stack.push(previous);
                            }
                            stack.push(current);
                            continue;
                        case JavaLexer.COMMENT:
                            // check if this is an in-line  or trailing comment, e.g.
                            // something /star comment star/ more things or
                            // something /star comment star/\n
                            int commentTokenIndex = commentToken.getTokenIndex();
                            boolean tokenOnLeft = true;
                            boolean tokenOnRight = true;
                            List<Token> wsTokens = tokens.getHiddenTokensToLeft(commentTokenIndex, JavaLexer.WS);
                            if (wsTokens != null && wsTokens.size() > 0) {
                                for (Token wsToken : wsTokens) {
                                    String wsText = wsToken.getText();
                                    if (wsText.indexOf('\n') > -1 || wsText.indexOf('\r') > -1) {
                                        tokenOnLeft = false;
                                        break;
                                    }
                                }
                            }
                            wsTokens = tokens.getHiddenTokensToRight(commentTokenIndex, JavaLexer.WS);
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
                        stack.push(comment);
                    }
                    else {
                        String last = stack.peek();
                        if (last != null && last.indexOf(comment) == -1) {
                            last = stack.pop();
                            last = new StringBuilder(comment).append(last).toString();
                            stack.push(last);
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
        List<Token> wsTokens = tokens.getHiddenTokensToLeft(tokenIndex, JavaLexer.WS);
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
                    last = stack.pop();
                    last = blankLines + removeBlankLines(last, START);
                    stack.push(last);
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