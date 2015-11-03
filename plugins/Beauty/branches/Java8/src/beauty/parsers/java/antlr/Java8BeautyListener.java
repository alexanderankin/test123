
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class provides an empty implementation of {@link Java8Listener},
 * which can be extended to create a listener which only needs to handle a subset
 * of the available methods.
 *
 * TODO: check all whatever*, they are probably backwards
 */
public class Java8BeautyListener implements Java8Listener {
    
    // token stream, need this to capture comments
    BufferedTokenStream tokens;
    TokenStreamRewriter rewriter;
    
    // accumulate final output here
    private StringBuilder output;
    
    // for tabs, 'tabCount' is current tab level, 'tab' is the string to use
    // for a tab, either '\t' or some number of spaces
    private int tabCount = 0;
    private String tab;
    
    // stack for holding intermediate formatted parts 
    private Deque<String> stack = new ArrayDeque<String>();
    
    /**
     * @param initialSize Initial size of the output buffer.
     * @param softTabs If true, use spaces rather than '\t' for tabs.
     * @param tabWidth The number of spaces to use if softTabs.
     */
    public Java8BeautyListener(int initialSize, boolean softTabs, int tabWidth, BufferedTokenStream tokenStream ) {
        output = new StringBuilder(initialSize);
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
        this.tokens = tokenStream;
        this.rewriter = new TokenStreamRewriter(tokens);
    }
    
    public void printStack() {
        printStack("");
    }
    
    // this is useful for debugging
    public void printStack(String name) {
        System.out.println("+++++ stack " + name + " +++++");
        for (String item : stack) {
            System.out.println("-------------------------------------");
            System.out.println(item);    
        }
        System.out.println("+++++ end stack +++++");
    }
    
    /**
     * @return The formatted string for the file contents.    
     */
    public String getText() {
        return output.toString();   
    }
    
    // Add tabCount tabs to the beginning of the given string builder.
    private void indent(StringBuilder sb) {
        if (sb == null) {
            sb = output;   
        }
        for ( int i = 0; i < tabCount; i++ ) {
            sb.insert(0, tab );
        }
    }
    
    private String indent(String s) {
        for (int i = 0; i < tabCount; i++) {
            s = tab + s;    
        }
        return s;
    }
    
    // increase the tabCount by 1 then indent
    private void incDent(StringBuilder sb) {
        ++ tabCount;
        indent(sb);
    }
    
    // decrease the tabCount by 1 then indent
    private void decDent(StringBuilder sb) {
        -- tabCount;
        indent(sb);
    }
    
    // remove all trailing tabs from the output
    private void outdent(StringBuilder sb) {
        if (sb == null) {
            sb = output;   
        }
        if (sb.length() < tab.length()) {
            return;   
        }
        while(sb.charAt(sb.length() - 1) == '\t' || sb.charAt(sb.length() - 1) == ' ') {
            sb.deleteCharAt(sb.length() - 1);   
        }
    }
    
    // StringBuilder doesn't have an "endsWith" method
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
    
    // StringBuilder doesn't have a "trim" method. This trims whitespace from
    // the end of the string builder. Whitespace on the front is not touched.
    public void trim(StringBuilder sb) {
        while(Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }
    
    // pops 'howMany' items off of the stack, reverses the order, then
    // assembles a string using 'separator' between the items. A separator is
    // not appended to the end of the string.
    private String reverse(int howMany, String separator) {
        StringBuilder sb = new StringBuilder();
	    List<String> list = new ArrayList<String>();
        for (int i = 0; i < howMany; i++) {
            list.add(stack.pop());
        }
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator); 
            }
        }
	    return sb.toString();
    }
    
    // for testing, first arg is a java file to parse, optional second arg is
    // 'trace' to turn on parser tracing, e.g.
    // java -cp .:/home/danson/apps/antlr/antlr-4.4-complete.jar Java8BeautyListener /home/danson/tmp/test/LambdaScopeTest.java trace
    public static void main (String[] args) {
        if (args == null)
            return;
        try {
            // set up the parser
            java.io.FileReader input = new java.io.FileReader(args[0]);
            ANTLRInputStream antlrInput = new ANTLRInputStream( input );
            Java8Lexer lexer = new Java8Lexer( antlrInput );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            Java8Parser javaParser = new Java8Parser( tokens );
            
            // for debugging
            javaParser.setTrace(args.length > 1 && "trace".equals(args[1]));

            // parse and beautify the buffer contents
            ParseTree tree = javaParser.compilationUnit();
            ParseTreeWalker walker = new ParseTreeWalker();
            Java8BeautyListener listener = new Java8BeautyListener(16 * 1024, true, 4, tokens);
            walker.walk( listener, tree );

            System.out.println(listener.getText());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
/*
--------------------------------------------------------------------------------
Parser methods follow.
--------------------------------------------------------------------------------
*/
    
	@Override public void enterClassMemberDeclaration(@NotNull Java8Parser.ClassMemberDeclarationContext ctx) { 
	    // FieldDeclaration
	    // MethodDeclaration
	    // ClassDeclaration
	    // InterfaceDeclaration
	    // ;
	}
	@Override public void exitClassMemberDeclaration(@NotNull Java8Parser.ClassMemberDeclarationContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterStatementNoShortIf(@NotNull Java8Parser.StatementNoShortIfContext ctx) { 
	    // StatementWithoutTrailingSubstatement
	    // LabeledStatementNoShortIf
	    // IfThenElseStatementNoShortIf
	    // WhileStatementNoShortIf
	    // ForStatementNoShortIf
	}
	@Override public void exitStatementNoShortIf(@NotNull Java8Parser.StatementNoShortIfContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
	    // {AnnotationTypeElementModifier} UnannType Identifier ( ) [Dims] [DefaultValue] ;	
	}
	@Override public void exitAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String defaultValue = ctx.defaultValue() == null ? "" : stack.pop();;
	    String dims = ctx.dims() == null ? "" : stack.pop();
	    String rparen = stack.pop();
	    String lparen = stack.pop();
	    String identifier = stack.pop();
	    String type = stack.pop();
	    if (ctx.annotationTypeElementModifier() != null) {
	        sb.append(reverse(ctx.annotationTypeElementModifier().size(), " ")).append(' ');
	    }
	    sb.append(type).append(' ').append(identifier).append(lparen).append(rparen);
	    if (!dims.isEmpty()) {
	        sb.append(' ').append(dims);    
	    }
	    if (!defaultValue.isEmpty()) {
	        sb.append(' ').append(defaultValue);    
	    }
	    sb.append(semi).append("\n");
	    stack.push(sb.toString());
	}

	@Override public void enterMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
	    // 	'.' typeArguments? Identifier '(' argumentList? ')'
	}
	@Override public void exitMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String identifier = stack.pop();
	    String typeArguments = ctx.typeArguments() == null ? "" : stack.pop();
	    String dot = stack.pop();
	    sb.append(dot).append(typeArguments).append(' ').append(identifier).append(lparen).append(argumentList).append(rparen);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) { 
	    // do nothing here
	}
	@Override public void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) { 
	    // do nothing here
	}

	@Override public void enterAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx) { 
	    // { {AnnotationTypeMemberDeclaration} }	
	}
	@Override public void exitAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rbrace = stack.pop();
	    if (ctx.annotationTypeMemberDeclaration() != null) {
	        for (int i = 0; i < ctx.annotationTypeMemberDeclaration().size(); i++) {
	            sb.append(stack.pop()).append("\n");    
	        }
	    }
	    String lbrace = stack.pop();
	    sb.insert(0, lbrace);
	    sb.append(rbrace).append("\n");
	    stack.push(sb.toString());
	}

	@Override public void enterRelationalOperator(@NotNull Java8Parser.RelationalOperatorContext ctx) { }
	
	@Override public void exitRelationalOperator(@NotNull Java8Parser.RelationalOperatorContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}
	
	@Override public void enterConstantModifier(@NotNull Java8Parser.ConstantModifierContext ctx) {
	    // (one of) Annotation public 
	    // static final	
	}
	@Override public void exitConstantModifier(@NotNull Java8Parser.ConstantModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterLambdaBody(@NotNull Java8Parser.LambdaBodyContext ctx) { 
	    // Expression 
	    // Block	
	}
	@Override public void exitLambdaBody(@NotNull Java8Parser.LambdaBodyContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) {
	    // Expression {, Expression}
	}
	@Override public void exitArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) {
	    StringBuilder sb = new StringBuilder();
        sb.append(reverse(ctx.expression().size() * 2, " "));
	    stack.push(sb.toString());
	}

	@Override public void enterShiftOperator(@NotNull Java8Parser.ShiftOperatorContext ctx) {
        // :   '<' '<'
        // |   '>' '>'
        // |   '>' '>' '>'
 	}
	@Override public void exitShiftOperator(@NotNull Java8Parser.ShiftOperatorContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}
	
	@Override public void enterClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx) {
	    // ClassMemberDeclaration
	    // InstanceInitializer
	    // StaticInitializer
	    // ConstructorDeclaration
	}
	@Override public void exitClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterClassInstanceCreationExpression_lfno_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) { 
	    // :	'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	    // |	expressionName '.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	}
	@Override public void exitClassInstanceCreationExpression_lfno_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    String classBody = ctx.classBody() == null ? "" : stack.pop();
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String typeArgumentsOrDiamond = ctx.typeArgumentsOrDiamond() == null ? "" : stack.pop();
	    
	    // expression name choice
	    if (ctx.expressionName() != null) {
	        String annotationIdentifier = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop();
	        String new_ = stack.pop();    // 'new'
	        String dot = stack.pop();
	        String name = stack.pop();    // expression name
	        sb.append(name).append(dot).append(new_).append(typeArguments).append(annotationIdentifier);
	    }
	    else {
            // 'new' choice
            String annotationIdentifiers = "";
            if (ctx.annotationIdentifier() != null) {
                annotationIdentifiers = reverse(ctx.annotationIdentifier().size(), "");
            }
            String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
            String new_ = stack.pop();
            sb.append(new_).append(' ').append(typeArguments).append(annotationIdentifiers);
	    }
	    
	    // common ending
	    sb.append(typeArgumentsOrDiamond).append(lparen).append(argumentList).append(rparen).append(classBody);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {
        // :	classInstanceCreationExpression_lf_primary
        // |	fieldAccess_lf_primary
        // |	methodInvocation_lf_primary
        // |	methodReference_lf_primary
	
	}
	@Override public void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnaryExpression(@NotNull Java8Parser.UnaryExpressionContext ctx) { 
	    // PreIncrementExpression 
	    // PreDecrementExpression 
	    // + UnaryExpression -- changed to AdditiveOperator UnaryExpression 
	    // - UnaryExpression -- changed to AdditiveOperator UnaryExpression
	    // UnaryExpressionNotPlusMinus	
	}
	@Override public void exitUnaryExpression(@NotNull Java8Parser.UnaryExpressionContext ctx) {
	    
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    if (ctx.additiveOperator() != null) {
	        String operator = stack.pop();
	        sb.append(operator).append(' ');
	    }
	    sb.append(expression);
	    stack.push(sb.toString());
	}

	@Override public void enterClassType_lfno_classOrInterfaceType(@NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
	    // annotationIdentifier typeArguments?
	}
	@Override public void exitClassType_lfno_classOrInterfaceType(@NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String typeArgs = ctx.typeArguments() == null ? "" : " " + stack.pop();
	    sb.append(stack.pop()).append(typeArgs);
	    stack.push(sb.toString());
	}

	@Override public void enterArrayType(@NotNull Java8Parser.ArrayTypeContext ctx) { 
	    // PrimitiveType Dims
	    // ClassOrInterfaceType Dims
	    // TypeVariable Dims
	}
	@Override public void exitArrayType(@NotNull Java8Parser.ArrayTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = stack.pop();
	    String type = stack.pop();
	    sb.append(type).append(' ').append(dims);
	    stack.push(sb.toString());
	}

	@Override public void enterSimpleTypeName(@NotNull Java8Parser.SimpleTypeNameContext ctx) {
	    // Identifier
	}
	@Override public void exitSimpleTypeName(@NotNull Java8Parser.SimpleTypeNameContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { 
	    // Identifier
	    // AmbiguousName . Identifier
	}
	@Override public void exitExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    if (ctx.ambiguousName() != null) {
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot);
	    }
	    sb.append(identifier);
	    stack.push(sb.toString());
	    
	}

	@Override public void enterStatementWithoutTrailingSubstatement(@NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
	    // Block
	    // EmptyStatement
	    // ExpressionStatement 
        // AssertStatement 
        // SwitchStatement 
        // DoStatement 
        // BreakStatement 
        // ContinueStatement 
        // ReturnStatement 
        // SynchronizedStatement 
        // ThrowStatement 
        // TryStatement

	}
	@Override public void exitStatementWithoutTrailingSubstatement(@NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterConstructorDeclarator(@NotNull Java8Parser.ConstructorDeclaratorContext ctx) {
	    // [TypeParameters] SimpleTypeName ( [FormalParameterList] )
	}
	@Override public void exitConstructorDeclarator(@NotNull Java8Parser.ConstructorDeclaratorContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
	    String rparen = stack.pop();
	    String formalParameters = "";
	    if (ctx.formalParameterList() != null) {
	        formalParameters = stack.pop();    
	    }
	    String lparen = stack.pop();
	    String typeName = stack.pop();
	    String typeParameters = "";
	    if (ctx.typeParameters() != null) {
	        typeParameters = stack.pop();    
	    }
	    if (!typeParameters.isEmpty()) {
	        sb.append(typeParameters).append(' ');    
	    }
	    sb.append(typeName).append(lparen);
        sb.append(formalParameters);
        sb.append(rparen);
        stack.push(sb.toString());
	}

	@Override public void enterAssertStatement(@NotNull Java8Parser.AssertStatementContext ctx) {
        // assert Expression ; 
        // assert Expression : Expression ;	
	}
	@Override public void exitAssertStatement(@NotNull Java8Parser.AssertStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    if (ctx.expression() != null) {
	        switch(ctx.expression().size()) {
                case 1:
                    String expression = stack.pop();
                    sb.append(expression).append(semi).append("\n");
                    break;
                case 2:
                    String expression2 = stack.pop();
                    String colon = stack.pop();
                    String expression1 = stack.pop();
                    sb.append(expression1).append(' ').append(colon).append(' ').append(expression2).append(semi).append("\n");
	        }
	    }
	    stack.push(sb.toString());
	}
	@Override public void enterArrayCreationExpression(@NotNull Java8Parser.ArrayCreationExpressionContext ctx) {
	    // new PrimitiveType DimExprs [Dims] 
	    // new ClassOrInterfaceType DimExprs [Dims] 
	    // new PrimitiveType Dims ArrayInitializer 
	    // new ClassOrInterfaceType Dims ArrayInitializer	
	}
	@Override public void exitArrayCreationExpression(@NotNull Java8Parser.ArrayCreationExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.arrayInitializer() != null) {
	        // last two choices 
	        String ai = stack.pop();
	        String dims = stack.pop();
	        String type = stack.pop();
	        String new_ = stack.pop();
	        sb.append(new_).append(' ').append(type).append(' ').append(dims).append(' ').append(ai);
	    }
	    else {
	        // first two choices    
	        String dims = "";
	        if (ctx.dims() != null) {
	            dims = stack.pop();    
	        }
	        String dimExprs = stack.pop();
	        String type = stack.pop();
	        String new_ = stack.pop();
	        sb.append(new_).append(' ').append(type).append(' ').append(dimExprs);
	        if (!dims.isEmpty()) {
	            sb.append(' ').append(dims);    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterUnannArrayType(@NotNull Java8Parser.UnannArrayTypeContext ctx) {
	    // UnannPrimitiveType Dims
	    // UnannClassOrInterfaceType Dims
	    // UnannTypeVariable Dims
	}
	@Override public void exitUnannArrayType(@NotNull Java8Parser.UnannArrayTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = stack.pop();
	    if (ctx.unannPrimitiveType() != null) {
	        sb.append(stack.pop());
	    }
	    else if (ctx.unannClassOrInterfaceType() != null) {
	        sb.append(stack.pop());   
	    }
	    else if (ctx.unannTypeVariable() != null) {
	        sb.append(stack.pop());
	    }
	    sb.append(' ').append(dims);
	    stack.push(sb.toString());
	}

	@Override public void enterVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) { 
	    // Identifier [Dims]
	}
	@Override public void exitVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = ctx.dims() == null ? "" : stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier);
	    if (!dims.isEmpty()) {
	        sb.append(' ').append(dims);   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterTryWithResourcesStatement(@NotNull Java8Parser.TryWithResourcesStatementContext ctx) { 
	    // try ResourceSpecification Block [Catches] [Finally]	
	}
	@Override public void exitTryWithResourcesStatement(@NotNull Java8Parser.TryWithResourcesStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String finally_ = "";
	    if (ctx.finally_() != null) {
	        finally_ = stack.pop();    
	    }
	    String catches = "";
	    if (ctx.catches() != null) {
	        catches = stack.pop();   
	    }
	    String block = stack.pop();
	    String rs = stack.pop();
	    String try_ = stack.pop();
	    sb.append(try_).append(' ').append(rs).append(' ').append(block).append(catches).append(finally_);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx) {
	    // < TypeArgumentList >
	}
	@Override public void exitTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rangle = stack.pop();
	    String list = stack.pop();
	    String langle = stack.pop();
	    sb.append(langle).append(list).append(rangle);
	    stack.push(sb.toString());
	    
	}
	@Override public void enterExceptionType(@NotNull Java8Parser.ExceptionTypeContext ctx) { 
	    // ClassType
	    // TypeVariable
	}
	@Override public void exitExceptionType(@NotNull Java8Parser.ExceptionTypeContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterExceptionTypeList(@NotNull Java8Parser.ExceptionTypeListContext ctx) { 
	    // ExceptionType {, ExceptionType}
	}
	@Override public void exitExceptionTypeList(@NotNull Java8Parser.ExceptionTypeListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.exceptionType() != null) {
	        sb.append(reverse(ctx.exceptionType().size() * 2, " "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterAdditiveExpression(@NotNull Java8Parser.AdditiveExpressionContext ctx) { 
	    // MultiplicativeExpression 
	    // AdditiveExpression AdditiveOperator MultiplicativeExpression 
	    // AdditiveExpression AdditiveOperator MultiplicativeExpression	
	}
	@Override public void exitAdditiveExpression(@NotNull Java8Parser.AdditiveExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.additiveExpression() != null) {
	        // one of the last two choices
	        String me = stack.pop().trim();
	        String ao = stack.pop();
	        String ae = stack.pop().trim();
	        sb.append(ae).append(' ').append(ao).append(' ').append(me);
	    }
	    else {
	        // the first choice
	        sb.append(stack.pop());
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterRelationalExpression(@NotNull Java8Parser.RelationalExpressionContext ctx) {
	    // ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ShiftExpression 
	    // RelationalExpression RelationalOperator ReferenceType	
	}
	@Override public void exitRelationalExpression(@NotNull Java8Parser.RelationalExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String shiftExpression = stack.pop();
	    String operator = "";
	    String relationalExpression = "";
	    if (ctx.relationalOperator() != null) {
	        operator = stack.pop();
	        relationalExpression = stack.pop();
	        sb.append(relationalExpression).append(' ').append(operator).append(' ');
	    }
	    sb.append(shiftExpression);
	    stack.push(sb.toString());
	}

	@Override public void enterReferenceType(@NotNull Java8Parser.ReferenceTypeContext ctx) { 
	    // ClassOrInterfaceType
	    // TypeVariable
	    // ArrayType
	}
	@Override public void exitReferenceType(@NotNull Java8Parser.ReferenceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterArrayAccess_lf_primary(@NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx) { 
        // :   (	primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary '[' expression ']'
        //     )
        //     (	primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary '[' expression ']'
        //     )*
        // ;
	}
	@Override public void exitArrayAccess_lf_primary(@NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    StringBuilder sh = new StringBuilder();
	    if (ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary() != null) {
	        for (int i = 0; i < ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary().size(); i++) {
	            String rsquare = stack.pop();
                String expr = stack.pop();
                String lsquare = stack.pop();
                String pn = stack.pop();
                sb.append(pn).append(lsquare).append(expr).append(rsquare).append(' ');
	        }
	    }
	    String rsquare = stack.pop();
	    String expr = stack.pop();
	    String lsquare = stack.pop();
	    String pn = stack.pop();
	    sb.append(pn).append(lsquare).append(expr).append(rsquare).append(' ').append(sh);
	    stack.push(sb.toString());
	}

	@Override public void enterInferredFormalParameterList(@NotNull Java8Parser.InferredFormalParameterListContext ctx) { 
	    // Identifier {, Identifier}	
	}
	@Override public void exitInferredFormalParameterList(@NotNull Java8Parser.InferredFormalParameterListContext ctx) {
	    StringBuilder sb = new StringBuilder();
        // TODO: check if these are backwards, maybe use reverse?
        for (int i = 0; i < ctx.Identifier().size(); i++) {
            sb.append(stack.pop());
            if (i < ctx.Identifier().size() - 1) {
                sb.append(stack.pop()); // pop the comma
            }
        }
	    stack.push(sb.toString());
	}

	@Override public void enterReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) {
	    // return [Expression] ;
	}
	@Override public void exitReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String expression = ctx.expression() == null ? "" : stack.pop();
	    String return_ = stack.pop();
	    sb.append(return_);
	    if (!expression.isEmpty()) {
	        sb.append(' ').append(expression);
	    }
	    sb.append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) { 
	    // import PackageOrTypeName . * ;
	}
	@Override public void exitTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String star = stack.pop();
	    String dot = stack.pop();
	    String name = stack.pop();
	    String import_ = stack.pop();
	    sb.append(import_).append(' ').append(name).append(dot).append(star).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx) { 
	    // < TypeParameterList >
	}
	@Override public void exitTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rangle = stack.pop();
	    String list = stack.pop();
	    String langle = stack.pop();
	    sb.append(langle).append(list).append(rangle);
	    stack.push(sb.toString());
	}
	@Override public void enterLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) { 
	    // {VariableModifier} UnannType {Annotation} ... VariableDeclaratorId
	    // FormalParameter
	}
	@Override public void exitLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    // only need to handle the first choice
	    if (ctx.unannType() != null) {
            String vdi = stack.pop();
            String ellipsis = stack.pop();
            String annotations = "";
            if (ctx.annotation() != null && !ctx.annotation().isEmpty()) {
                annotations = reverse(ctx.annotation().size(), " ") + ' ';
            }
            String type = stack.pop();
            if (ctx.variableModifier() != null) {
                for (int i = 0; i < ctx.variableModifier().size(); i++) {
                    sb.append(stack.pop()).append(' ');    
                }
            }
            sb.append(type);
            if (!annotations.isEmpty()) {
                sb.append(' ').append(annotations);
            } else {
                trim(sb);   // remove space after type
            }
            sb.append(ellipsis).append(' ').append(vdi);
            stack.push(sb.toString());
	    }
	}

	@Override public void enterLiteral(@NotNull Java8Parser.LiteralContext ctx) {
	    // IntegerLiteral
	    // FloatingPointLiteral
	    // BooleanLiteral
	    // CharacterLiteral
	    // StringLiteral
	    // NullLiteral
	}
	@Override public void exitLiteral(@NotNull Java8Parser.LiteralContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterResult(@NotNull Java8Parser.ResultContext ctx) {
	    // UnannType
	    // void
	}
	@Override public void exitResult(@NotNull Java8Parser.ResultContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) {
	    // :	'super' '.' Identifier
	    // |	typeName '.' 'super' '.' Identifier
	
	}
	@Override public void exitFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = stack.pop();
	    String dot = stack.pop();
	    String super_ = stack.pop();
	    if (ctx.typeName() != null) {
	        String dot2 = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot2);    
	    }
	    sb.append(super_).append(dot).append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) {
	    // VariableDeclaratorId [= VariableInitializer]
	}
	@Override public void exitVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String vi = ctx.variableInitializer() == null ? "" : stack.pop();
	    String equal = ctx.variableInitializer() == null ? "" : stack.pop();
	    String vd = stack.pop();
	    sb.append(vd);
	    if (!vi.isEmpty()) {
	        sb.append(' ').append(equal).append(' ').append(vi);    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterFinally_(@NotNull Java8Parser.Finally_Context ctx) {
	    // finally Block
	}
	@Override public void exitFinally_(@NotNull Java8Parser.Finally_Context ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String block = stack.pop();
	    String finally_ = stack.pop();
	    sb.append(finally_).append(' ').append(block);
	    stack.push(sb.toString());
	}

	@Override public void enterClassBody(@NotNull Java8Parser.ClassBodyContext ctx) {
	    // { {ClassBodyDeclaration} }
	    ++tabCount;
	}
	@Override public void exitClassBody(@NotNull Java8Parser.ClassBodyContext ctx) {
	    String rbrace = stack.pop();    // }
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
	    if (ctx.classBodyDeclaration() != null) {
	        sb.append(reverse(ctx.classBodyDeclaration().size(), ""));
	    }
	    String lbrace = stack.pop();    // {
	    sb.insert(0, lbrace);
	    --tabCount;
	    rbrace = indent(rbrace);
	    sb.append(rbrace).append("\n");
	    stack.push(sb.toString());
	}
	
	@Override public void enterUnannInterfaceType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    // unannClassType_lfno_unannClassOrInterfaceType
	}
	@Override public void exitUnannInterfaceType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx) {
	    // {ClassModifier} enum Identifier [Superinterfaces] EnumBody
	}
	@Override public void exitEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String enumBody = stack.pop();
	    String superInterfaces = "";
	    if (ctx.superinterfaces() != null) {
	        superInterfaces = stack.pop();    
	    }
	    String identifier = stack.pop();
	    String enum_ = stack.pop();
	    if (ctx.classModifier() != null) {
	        sb.append(reverse(ctx.classModifier().size(), " ")).append(' ');
	    }
	    sb.append(enum_).append(' ').append(identifier);
	    if (!superInterfaces.isEmpty()) {
	        sb.append(' ').append(superInterfaces).append(' ');    
	    }
	    sb.append(enumBody);
	    stack.push(sb.toString());
	}

	@Override public void enterConstantDeclaration(@NotNull Java8Parser.ConstantDeclarationContext ctx) {
	    // {ConstantModifier} UnannType VariableDeclaratorList ;	
	}
	@Override public void exitConstantDeclaration(@NotNull Java8Parser.ConstantDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String vdl = stack.pop();
	    String type = stack.pop();
	    if (ctx.constantModifier() != null) {
	        sb.append(reverse(ctx.constantModifier().size(), " ")).append(' ');
	    }
	    sb.append(type).append(' ').append(vdl).append(semi).append("\n");
	    stack.push(sb.toString());
	}

	@Override public void enterPostfixExpression(@NotNull Java8Parser.PostfixExpressionContext ctx) { 
	    // Primary 
	    // ExpressionName 
	    // PostIncrementExpression 
	    // PostDecrementExpression	
	}
	@Override public void exitPostfixExpression(@NotNull Java8Parser.PostfixExpressionContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	    
	}

	@Override public void enterAnnotation(@NotNull Java8Parser.AnnotationContext ctx) {
	    // NormalAnnotation
	    // MarkerAnnotation
	    // SingleElementAnnotation
	}
	@Override public void exitAnnotation(@NotNull Java8Parser.AnnotationContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx) {
	    // Expression
	    // ArrayInitializer
	}
	@Override public void exitVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) { 
	    // import static TypeName . * ;
	}
	@Override public void exitStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String star = stack.pop();
	    String dot = stack.pop();
	    String name = stack.pop();
	    String static_ = stack.pop();
	    String import_ = stack.pop();
	    sb.append(import_).append(' ').append(static_).append(' ').append(name).append(dot).append(star).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	
	@Override public void enterExpression(@NotNull Java8Parser.ExpressionContext ctx) { 
	    // LambdaExpression 
	    // AssignmentExpression	
	}
	@Override public void exitExpression(@NotNull Java8Parser.ExpressionContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	    
	}

	@Override public void enterThrowStatement(@NotNull Java8Parser.ThrowStatementContext ctx) {
	    // throw Expression ;
	}
	@Override public void exitThrowStatement(@NotNull Java8Parser.ThrowStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String expression = stack.pop();
	    String throw_ = stack.pop();
	    sb.append(throw_).append(' ').append(expression).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) { 
	    // MethodName ( [ArgumentList] ) 
	    // TypeName . [TypeArguments] Identifier ( [ArgumentList] ) 
	    // ExpressionName . [TypeArguments] Identifier ( [ArgumentList] ) 
	    // Primary . [TypeArguments] Identifier ( [ArgumentList] ) 
	    // super . [TypeArguments] Identifier ( [ArgumentList] ) 
	    // TypeName . super . [TypeArguments] Identifier ( [ArgumentList] )	
	}
	@Override public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String rparen = stack.pop();
	    if (ctx.methodName() != null) {
	        String argList = ctx.argumentList() == null ? "" : stack.pop();
	        String lparen = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(lparen).append(argList).append(rparen);    
	    }
	    else if (ctx.expressionName() != null || ctx.primary() != null) {
	        String argList = ctx.argumentList() == null ? "" : stack.pop();
	        String lparen = stack.pop();
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop();
	        String dot = stack.pop();
	        String name = stack.pop();
	        sb.append(name).append(dot).append(typeArgs).append(' ').append(identifier).append(lparen).append(argList).append(rparen);
	    }
	    else if (ctx.typeName() != null) {
	        String argList = ctx.argumentList() == null ? "" : stack.pop();
	        String lparen = stack.pop();
	        String identifier = stack.pop();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop();
	        String raw = ctx.getText();
            boolean hasSuper = raw.indexOf("super") > -1;
            String dot = stack.pop();
            String name = stack.pop();  // typename
            sb.append(name).append(dot); 
            if (hasSuper) {
                String dot2 = stack.pop();
                String super_ = stack.pop();
                sb.append(super_).append(dot2);
            }
	        if (!typeArgs.isEmpty()) {
	            sb.append(typeArgs).append(' ');        
	        }
	        sb.append(identifier).append(lparen).append(argList).append(rparen);
	    }
	    else {
	        String argList = ctx.argumentList() == null ? "" : stack.pop();
	        String lparen = stack.pop();
	        String identifier = ctx.Identifier() == null ? "" : ctx.Identifier().getText();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop();
	        String dot = stack.pop();
	        String super_ = stack.pop();
	        sb.append(super_).append(dot);           
	        if (!typeArgs.isEmpty()) {
	            sb.append(typeArgs).append(' ');        
	        }
	        sb.append(identifier).append(lparen).append(argList).append(rparen);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) {
	    // import static TypeName . Identifier ; 
	}
	@Override public void exitSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String semi = stack.pop();
	    String identifier = stack.pop();
	    String dot = stack.pop();
	    String name = stack.pop();
	    String static_ = stack.pop();
	    String import_ = stack.pop();
	    sb.append(import_).append(' ').append(static_).append(' ').append(name).append(dot).append(identifier).append(semi).append('\n');
	    stack.push(sb.toString());
	}
	@Override public void enterLambdaParameters(@NotNull Java8Parser.LambdaParametersContext ctx) { 
	    // Identifier
	    // ( [FormalParameterList] ) 
	    // ( InferredFormalParameterList )	
	}
	@Override public void exitLambdaParameters(@NotNull Java8Parser.LambdaParametersContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.Identifier() != null) {
	        sb.append(stack.pop());
	    }
	    else {
	        String rparen = stack.pop();
	        String list = "";
	        if (ctx.formalParameterList() != null || ctx.inferredFormalParameterList() != null) {
	            list = stack.pop();
	        }
	        String lparen = stack.pop();
	        sb.append(lparen).append(list).append(rparen);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterConditionalAndExpression(@NotNull Java8Parser.ConditionalAndExpressionContext ctx) {
	    // InclusiveOrExpression 
	    // ConditionalAndExpression && InclusiveOrExpression	
	}
	@Override public void exitConditionalAndExpression(@NotNull Java8Parser.ConditionalAndExpressionContext ctx) {
	    
	    StringBuilder sb = new StringBuilder();
	    String ioe = stack.pop();
	    if (ctx.conditionalAndExpression() != null) {
	        String and = stack.pop();
	        String cae = stack.pop();
	        sb.append(cae).append(' ').append(and).append(' ');
	    }
	    sb.append(ioe);
	    stack.push(sb.toString());
	}

	@Override public void enterMultiplicativeExpression(@NotNull Java8Parser.MultiplicativeExpressionContext ctx) {
	    // UnaryExpression 
	    // MultiplicativeExpression MultiplicativeOperator UnaryExpression 
	}
	@Override public void exitMultiplicativeExpression(@NotNull Java8Parser.MultiplicativeExpressionContext ctx) { 
	    
	    StringBuilder sb = new StringBuilder();
	    String ue = stack.pop();
	    if (ctx.multiplicativeExpression() != null) {
	        String operator = stack.pop();
	        String me = stack.pop();
	        sb.append(me).append(' ').append(operator).append(' ');
	    }
	    sb.append(ue);
	    stack.push(sb.toString());
	}

	@Override public void enterPackageModifier(@NotNull Java8Parser.PackageModifierContext ctx) {
	    // Annotation
	}
	@Override public void exitPackageModifier(@NotNull Java8Parser.PackageModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx) {
	    // {ConstructorModifier} ConstructorDeclarator [Throws] ConstructorBody
	}
	@Override public void exitConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String body = stack.pop();
	    String throws_ = ctx.throws_() == null ? "" : stack.pop() + ' ';
	    String cd = stack.pop();
	    if (ctx.constructorModifier() != null && !ctx.constructorModifier().isEmpty()) {
	        sb.append(reverse(ctx.constructorModifier().size(), " ")).append(' ');
	    }
	    sb.append(cd).append(' ').append(throws_).append(body);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lfno_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) { 
        // :	literal
        // |	typeName ('[' ']')* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression
        // |	fieldAccess
        // |	methodInvocation
        // |	methodReference
	}
	@Override public void exitPrimaryNoNewArray_lfno_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) { 
	    if (ctx.literal() != null || 
	        ctx.classInstanceCreationExpression() != null ||
	        ctx.fieldAccess() != null ||
	        ctx.methodInvocation() != null || 
	        ctx.methodReference() != null) {
	        // one of these choices is already on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    
	    // expression
	    if (ctx.expression() != null) {
	        String rparen = stack.pop();
	        String expression = stack.pop();
	        String lparen = stack.pop();
	        sb.append(lparen).append(expression).append(rparen);
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            String class_ = stack.pop();
	            String dot = stack.pop();
	            StringBuilder brackets = new StringBuilder();
	            if (ctx.squareBrackets() != null) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append(stack.pop());
	                }
	            }
	            String name = stack.pop();
	            sb.append(name).append(brackets).append(dot).append(class_);
	        }
	        else {
	            // second typeName choice
	            String this_ = stack.pop();
	            String dot = stack.pop();
	            String name = stack.pop();
	            sb.append(name).append(dot).append(this);
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
	        String class_ = stack.pop();
	        String dot = stack.pop();
	        String void_ = stack.pop();
	        sb.append(void_).append(dot).append(class_);
	    }
	    else {
	        sb.append(stack.pop());    // 'this' choice   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterUnannTypeVariable(@NotNull Java8Parser.UnannTypeVariableContext ctx) { 
	    // Identifier
	}
	@Override public void exitUnannTypeVariable(@NotNull Java8Parser.UnannTypeVariableContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterNormalInterfaceDeclaration(@NotNull Java8Parser.NormalInterfaceDeclarationContext ctx) {
	    // {InterfaceModifier} interface Identifier [TypeParameters] [ExtendsInterfaces] InterfaceBody	
	}
	@Override public void exitNormalInterfaceDeclaration(@NotNull Java8Parser.NormalInterfaceDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String body = stack.pop();
	    String ifs = ctx.extendsInterfaces() == null ? "" : stack.pop();
	    String params = ctx.typeParameters() == null ? "" : stack.pop() + ' ';
	    String identifier = stack.pop();
	    String interface_ = stack.pop();
	    if (ctx.interfaceModifier() != null) {
	        sb.append(reverse(ctx.interfaceModifier().size(), " ")).append(' ');
	    }
	    sb.append(interface_).append(' ').append(identifier).append(params).append(ifs).append(body);
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceType_lfno_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
	    // classType_lfno_classOrInterfaceType
	}
	@Override public void exitInterfaceType_lfno_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterConstructorModifier(@NotNull Java8Parser.ConstructorModifierContext ctx) {
	    // (one of) Annotation public protected private
	}
	@Override public void exitConstructorModifier(@NotNull Java8Parser.ConstructorModifierContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx) {
	    // Identifier
	}
	@Override public void exitEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterClassInstanceCreationExpression(@NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        // :    'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // |	expressionName '.' 'new' typeArguments? annotationIdentifier          typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
        // |	primary        '.' 'new' typeArguments? annotationIdentifier          typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	}
	@Override public void exitClassInstanceCreationExpression(@NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    String classBody = ctx.classBody() == null ? "" : stack.pop();
	    String rparen = stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String typeArgumentsOrDiamond = ctx.typeArgumentsOrDiamond() == null ? "" : stack.pop();
	    
	    // expression name and primary
	    if (ctx.expressionName() != null || ctx.primary() != null) {
	        String annotationIdentifier = stack.pop();
	        String typeArguments = ctx.typeArguments() == null ? "" : stack.pop();
	        String new_ = stack.pop();
	        String dot = stack.pop();
	        String name = stack.pop();    // expression name or primary
	        sb.append(name).append(dot).append(new_).append(typeArguments).append(annotationIdentifier);
	    }
	    else {
            // 'new' choice
            StringBuilder annotationIdentifiers = new StringBuilder();
            if (ctx.annotationIdentifier() != null) {
                // TODO: this is wrong, the dots are on the stack
                for (int i = 0; i < ctx.annotationIdentifier().size(); i++) {
                    annotationIdentifiers.append(stack.pop());
                    if (i < ctx.annotationIdentifier().size() - 1) {
                        annotationIdentifiers.append('.');    
                    }
                }
            }
            String typeArguments = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
            String new_ = stack.pop();
            sb.append(new_).append(' ').append(typeArguments).append(annotationIdentifiers.toString());
	    }
	    
	    // common ending
	    sb.append(typeArgumentsOrDiamond).append(lparen).append(argumentList).append(rparen).append(classBody);
	    stack.push(sb.toString());
	}

	@Override public void enterMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) {
	    // Identifier ( [FormalParameterList] ) [Dims]
	}
	@Override public void exitMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String dims = ctx.dims() == null ? "" : stack.pop();
	    String rparen = stack.pop();
	    String params = ctx.formalParameterList() == null ? "" : stack.pop();
	    String lparen = stack.pop();
	    String identifier = stack.pop();
	    sb.append(identifier).append(lparen).append(params).append(rparen).append(dims);
	    stack.push(sb.toString());
	}

	@Override public void enterAnnotationTypeMemberDeclaration(@NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx) {
	    // AnnotationTypeElementDeclaration 
	    // ConstantDeclaration 
	    // ClassDeclaration 
	    // InterfaceDeclaration 
	    // ;	
	}
	@Override public void exitAnnotationTypeMemberDeclaration(@NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterPreDecrementExpression(@NotNull Java8Parser.PreDecrementExpressionContext ctx) {
	    // -- UnaryExpression	
	}
	@Override public void exitPreDecrementExpression(@NotNull Java8Parser.PreDecrementExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    String dec = stack.pop();
	    sb.append(dec).append(' ').append(expression);
	    stack.push(sb.toString());
	}

	@Override public void enterMultiplicativeOperator(@NotNull Java8Parser.MultiplicativeOperatorContext ctx) { 
	    // one of: * / %
	}
	@Override public void exitMultiplicativeOperator(@NotNull Java8Parser.MultiplicativeOperatorContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}
	
	@Override public void enterVariableInitializerList(@NotNull Java8Parser.VariableInitializerListContext ctx) {
	    // VariableInitializer {, VariableInitializer}
	}
	@Override public void exitVariableInitializerList(@NotNull Java8Parser.VariableInitializerListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    // TODO: fix this, the dot is already on the stack
	    if (ctx.variableInitializer() != null) {
	        sb.append(reverse(ctx.variableInitializer().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterExtendsInterfaces(@NotNull Java8Parser.ExtendsInterfacesContext ctx) {
	    // extends InterfaceTypeList
	}
	@Override public void exitExtendsInterfaces(@NotNull Java8Parser.ExtendsInterfacesContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String list = stack.pop();
	    String extends_ = stack.pop();
	    sb.append(extends_).append(' ').append(list);
	    stack.push(sb.toString());
	}

	@Override public void enterElementValue(@NotNull Java8Parser.ElementValueContext ctx) {
	    // ConditionalExpression 
	    // ElementValueArrayInitializer 
	    // Annotation	
	}
	@Override public void exitElementValue(@NotNull Java8Parser.ElementValueContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx) { 
	    // { [VariableInitializerList] [,] }
	}
	@Override public void exitArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rbracket = stack.pop();
	    String vil = ctx.variableInitializerList() == null ? "" : stack.pop();
	    String lbracket = stack.pop();
	    sb.append(lbracket).append(vil).append(rbracket);
	    stack.push(sb.toString());
	}
	
	@Override public void enterArrayAccess(@NotNull Java8Parser.ArrayAccessContext ctx) {
	    // ExpressionName [ Expression ] 
	    // PrimaryNoNewArray [ Expression ]	
	}
	@Override public void exitArrayAccess(@NotNull Java8Parser.ArrayAccessContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String rsquare = stack.pop();
	    String expr = stack.pop();
	    String lsquare = stack.pop();
	    String item = stack.pop();
	    sb.append(item).append(lsquare).append(expr).append(rsquare);
	    stack.push(sb.toString());
	}

	@Override public void enterMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) {
	    // (one of) Annotation public protected private - Done
	    // abstract static final synchronized native strictfp - Done
	}
	@Override public void exitMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnannClassType(@NotNull Java8Parser.UnannClassTypeContext ctx) {
	    // Identifier [TypeArguments]
	    // UnannClassOrInterfaceType . {Annotation} Identifier [TypeArguments]
	}
	@Override public void exitUnannClassType(@NotNull Java8Parser.UnannClassTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.unannClassOrInterfaceType() == null) {
	        // have first option
	        String typeargs = "";
	        if (ctx.typeArguments() != null) {
	            typeargs = stack.pop();
	        }
	        sb.append(stack.pop());    // identifier
	        if (!typeargs.isEmpty()) {
	            sb.append(typeargs);   
	        }
	    }
	    else {
	        String typeargs = "";
	        if (ctx.typeArguments() != null) {
	            typeargs = stack.pop();
	        }
	        String identifier = stack.pop();
	        StringBuilder annotations = new StringBuilder();
	        if (ctx.annotation() != null) {
	            for (int i = 0; i < ctx.annotation().size(); i++) {
	                annotations.append(stack.pop());   
	            }
	        }
	        String dot = stack.pop();
	        String type = stack.pop();
	        sb.append(type).append(dot);
	        sb.append(annotations.toString());
	        sb.append(identifier).append(' ');
	        sb.append(typeargs);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterLambdaExpression(@NotNull Java8Parser.LambdaExpressionContext ctx) { 
	    // LambdaParameters -> LambdaBody	
	}
	@Override public void exitLambdaExpression(@NotNull Java8Parser.LambdaExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String lb = stack.pop();
	    String pointer = stack.pop();
	    String lp = stack.pop();
	    sb.append(lp).append(' ').append(pointer).append(' ').append(lb);
	    stack.push(sb.toString());
	}



	@Override public void enterAssignmentExpression(@NotNull Java8Parser.AssignmentExpressionContext ctx) {
	    // ConditionalExpression 
	    // Assignment	
	}
	@Override public void exitAssignmentExpression(@NotNull Java8Parser.AssignmentExpressionContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}


	@Override public void enterTypeParameterList(@NotNull Java8Parser.TypeParameterListContext ctx) {
	    // TypeParameter {, TypeParameter}
	}
	@Override public void exitTypeParameterList(@NotNull Java8Parser.TypeParameterListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    // TODO: fix this, the dots are already on the stack
	    if (ctx.typeParameter() != null) {
	        sb.append(reverse(ctx.typeParameter().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterNormalClassDeclaration(@NotNull Java8Parser.NormalClassDeclarationContext ctx) {
	    // {ClassModifier} class Identifier [TypeParameters] [Superclass] [Superinterfaces] ClassBody
	}
	@Override public void exitNormalClassDeclaration(@NotNull Java8Parser.NormalClassDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (tabCount > 0) {
	        indent(sb);
	    }
        String body = stack.pop();
        String superInterfaces = ctx.superinterfaces() == null ? "" : stack.pop();
        String superClass = ctx.superclass() == null ? "" : stack.pop() + ' ';
        String params = ctx.typeParameters() == null ? "" : stack.pop() + ' ';
        String identifier = stack.pop();
        String classNode = stack.pop();
        if (ctx.classModifier() != null && !ctx.classModifier().isEmpty()) {
	        sb.append(reverse(ctx.classModifier().size(), " ")).append(' ');
        }
        sb.append(classNode).append(' ').append(identifier).append(' ').append(params).append(superClass).append(superInterfaces).append(body);
        stack.push(sb.toString());
	}

	@Override public void enterFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx) { 
	    // ReceiverParameter
	    // FormalParameters , LastFormalParameter
	    // LastFormalParameter
	}
	@Override public void exitFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx) { 
	    // only need to handle 2nd choice, others should already be on the stack
	    if (ctx.formalParameters() != null) {
	        String lfp = stack.pop();
	        String comma = stack.pop();
	        String fp = stack.pop();
	        StringBuilder sb = new StringBuilder(fp);
	        sb.append(comma).append(' ').append(lfp);
	        stack.push(sb.toString());
	    }
	}

	@Override public void enterEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
	    // for ( {VariableModifier} UnannType VariableDeclaratorId : Expression ) StatementNoShortIf
	    
	}
	@Override public void exitEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    String rparen = stack.pop();
	    String expression = stack.pop();
	    String colon = stack.pop();
	    String vdi = stack.pop();
	    String type = stack.pop();
	    String vm = "";
	    if (ctx.variableModifier() != null) {
	         vm = reverse(ctx.variableModifier().size(), " ") + ' ';   
	    }
	    String lparen = stack.pop();
	    String for_ = stack.pop();
	    sb.append(for_).append(' ').append(lparen).append(vm).append(type).append(' ').append(vdi).append(' ').append(colon).append(' ').append(expression).append(rparen).append(' ').append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx) {
	    // {InterfaceModifier} @ interface Identifier AnnotationTypeBody
	    
	}
	@Override public void exitAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String body = stack.pop();
	    String identifier = stack.pop();
	    String interface_ = stack.pop();
	    String at = stack.pop();
	    String ifm = "";
	    if (ctx.interfaceModifier() != null) {
	        ifm = reverse(ctx.interfaceModifier().size(), " ") + ' ';    
	    }
	    sb.append(ifm).append(' ').append(at).append(interface_).append(' ').append(identifier).append(' ').append(body);
	    stack.push(sb.toString());
	}

	@Override public void enterCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx) {
	    // [PackageDeclaration] {ImportDeclaration} {TypeDeclaration}
	}
	@Override public void exitCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx) {
	    // TODO: there is an extra blank item on the stack, need to figure out where it's coming from.
	    // The next line is a temporary work-around.
	    stack.pop();
	    
	    String typeDeclarations = "";
	    if (ctx.typeDeclaration() != null) {
	        typeDeclarations = reverse(ctx.typeDeclaration().size(), "");
	    }
	    String importDeclarations = "";
	    if (ctx.importDeclaration() != null) {
	        importDeclarations = reverse(ctx.importDeclaration().size(), "") + "\n";
	    }
        String packageDeclaration = ctx.packageDeclaration() == null ? "" : stack.pop() + "\n\n";
        
        output.append(packageDeclaration);
        output.append(importDeclarations);
        output.append(typeDeclarations);
	    // all done!
	}

	@Override public void enterWildcardBounds(@NotNull Java8Parser.WildcardBoundsContext ctx) { 
	    // extends ReferenceType
	    // super ReferenceType
	}
	@Override public void exitWildcardBounds(@NotNull Java8Parser.WildcardBoundsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String type = stack.pop();
	    String word = stack.pop();    // extends or super
	    sb.append(word).append(' ').append(type);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lf_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) {
	    // do nothing here
	}
	@Override public void exitPrimaryNoNewArray_lf_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) { 
	    // do nothing here
	}

	@Override public void enterEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) { 
	    // for ( {VariableModifier} UnannType VariableDeclaratorId : Expression ) Statement    
	}
	@Override public void exitEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String statement = stack.pop();
	    String expression = stack.pop();
	    String vdi = stack.pop();
	    String type = stack.pop();
	    String modifiers = "";
	    if (ctx.variableModifier() != null) {
	        modifiers = reverse(ctx.variableModifier().size(), " ") + ' ';
	    }
	    sb.append("for (").append(modifiers).append(type).append(' ').append(vdi).append(" : ").append(expression).append(")").append(statement);
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx) {
	    // SwitchLabels BlockStatements
	}
	@Override public void exitSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String bs = stack.pop();
	    String sl = stack.pop();
	    sb.append(sl).append(bs);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeVariable(@NotNull Java8Parser.TypeVariableContext ctx) {
	    // {Annotation} Identifier
	}
	@Override public void exitTypeVariable(@NotNull Java8Parser.TypeVariableContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = ctx.Identifier().getText();
	    if (ctx.annotation() != null) {
	        for (int i = 0; i < ctx.annotation().size(); i++) {
	            sb.append(stack.pop()).append(' ');    
	        }
	    }
	    sb.append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx) { 
	    // {TypeParameterModifier} Identifier [TypeBound]
	}
	@Override public void exitTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String typeBound = ctx.typeBound() == null ? "" : stack.pop();
	    String identifier = ctx.Identifier().getText();
	    if (ctx.typeParameterModifier() != null) {
	        sb.append(reverse(ctx.typeParameterModifier().size(), " ")).append(' ');
	    }
	    sb.append(identifier);
	    if (!typeBound.isEmpty()) {
	        sb.append(' ').append(typeBound);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
	    // {MethodModifier] MethodHeader MethodBody
	}
	@Override public void exitMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
        String body = stack.pop();
	    String header = stack.pop();
	    if (ctx.methodModifier() != null) {
	        for (int i = 0; i < ctx.methodModifier().size(); i++) {
	            sb.append(stack.pop()).append(' ');    
	        }
	    }
	    sb.append(header).append(' ').append(body);
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx) { 
	    // { {InterfaceMemberDeclaration} }	
	}
	@Override public void exitInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    sb.append("{\n");
	    sb.append(ctx.interfaceMemberDeclaration() == null ? "" : stack.pop());
	    sb.append("\n}\n");
	    stack.push(sb.toString());
	}

	@Override public void enterMethodBody(@NotNull Java8Parser.MethodBodyContext ctx) {
	    // Block
	    // ;
	}
	@Override public void exitMethodBody(@NotNull Java8Parser.MethodBodyContext ctx) {
	    if (ctx.block() == null) {
	        stack.push(";\n");
	    }
	}

	@Override public void enterDims(@NotNull Java8Parser.DimsContext ctx) { 
	    // {Annotation} [ ] {{Annotation} [ ]}
	    // changed this to:
	    // annotationDim (annotationDim)*
	}
	@Override public void exitDims(@NotNull Java8Parser.DimsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.annotationDim() != null) {
	        for (int i = 0; i < ctx.annotationDim().size(); i++) {
	            sb.append(stack.pop()).append(' ');    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx) {
	    // ReferenceType
	    // Wildcard
	}
	@Override public void exitTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnannPrimitiveType(@NotNull Java8Parser.UnannPrimitiveTypeContext ctx) {
	    // NumericType 
	    // boolean
	}
	@Override public void exitUnannPrimitiveType(@NotNull Java8Parser.UnannPrimitiveTypeContext ctx) {
	    if (ctx.numericType() == null) {
	        stack.push("boolean");
	    }
	    // else NumericType is already on the stack
	}

	@Override public void enterExplicitConstructorInvocation(@NotNull Java8Parser.ExplicitConstructorInvocationContext ctx) { 
	    // [TypeArguments] this ( [ArgumentList] ) ;
	    // [TypeArguments] super ( [ArgumentList] ) ;
	    // ExpressionName . [TypeArguments] super ( [ArgumentList] ) ;
	    // Primary . [TypeArguments] super ( [ArgumentList] ) ;
	}
	@Override public void exitExplicitConstructorInvocation(@NotNull Java8Parser.ExplicitConstructorInvocationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
	    String args = ctx.argumentList() == null ? "" : stack.pop();
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	    
	    // 3rd and 4th choice
	    if (ctx.expressionName() != null || ctx.primary() != null) {
	        sb.append(stack.pop()).append('.').append(typeArgs).append("super(").append(args).append(");\n");
	    }
	    else {
	        // 1st and second choice
	        sb.append(typeArgs);
	        if (!typeArgs.isEmpty()) {
	            sb.append(' ');    
	        }
	        String raw = ctx.getText();
	        sb.append(raw.indexOf("this") > -1 ? "this" : "super");
	        sb.append("(").append(args).append(");\n");
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterEnumBody(@NotNull Java8Parser.EnumBodyContext ctx) {
	    // { [EnumConstantList] [,] [EnumBodyDeclarations] }
	}
	@Override public void exitEnumBody(@NotNull Java8Parser.EnumBodyContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String body = ctx.enumBodyDeclarations() == null ? "" : stack.pop();
	    String list = ctx.enumConstantList() == null ? "" : stack.pop();
	    sb.append("{\n").append(list);
	    if (!list.isEmpty() && !body.isEmpty()) {
	        sb.append(", ");    
	    }
	    sb.append(body);
	    sb.append("}\n");
	    stack.push(sb.toString());
	}
	@Override public void enterAdditionalBound(@NotNull Java8Parser.AdditionalBoundContext ctx) { 
	    // & Interface
	}
	@Override public void exitAdditionalBound(@NotNull Java8Parser.AdditionalBoundContext ctx) {
	    StringBuilder sb = new StringBuilder("&");
	    sb.append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) {
        // :	literal
        // |	typeName ('[' ']')* '.' 'class'
        // |	unannPrimitiveType ('[' ']')* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression_lfno_primary
        // |	fieldAccess_lfno_primary
        // |	arrayAccess_lfno_primary
        // |	methodInvocation_lfno_primary
        // |	methodReference_lfno_primary
	}
	@Override public void exitPrimaryNoNewArray_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) { 
	    if (ctx.literal() != null || ctx.classInstanceCreationExpression_lfno_primary() != null ||
	        ctx.fieldAccess_lfno_primary() != null || ctx.arrayAccess_lfno_primary() != null ||
	        ctx.methodInvocation_lfno_primary() != null || ctx.methodReference_lfno_primary() != null) {
	        // one of these choices is already on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
	    
	    // expression
	    if (ctx.expression() != null) {
	        sb.append("(").append(stack.pop()).append(")");
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        sb.append(stack.pop());
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            if (ctx.squareBrackets() != null) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append("[]");
	                }
	            }
	            sb.append(".class");
	        }
	        else {
	            // second typeName choice
	            sb.append(".this");
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // unann primitive type
	    if (ctx.unannPrimitiveType() != null) {
	        sb.append(stack.pop());
	        if (raw.indexOf("class") > -1) {
	            if (ctx.squareBrackets() != null) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append("[]");
	                }
	            }
	            sb.append(".class");
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
	        sb.append("void.class");
	    }
	    else {
	        sb.append("this");   
	    }
	    stack.push(sb.toString());
	    
	}

	@Override public void enterUnannClassOrInterfaceType(@NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx) { 
	    // :(	unannClassType_lfno_unannClassOrInterfaceType
	    // |	unannInterfaceType_lfno_unannClassOrInterfaceType
	    // )
	    // (	unannClassType_lf_unannClassOrInterfaceType
	    // |	unannInterfaceType_lf_unannClassOrInterfaceType
	    // )*
	}
	@Override public void exitUnannClassOrInterfaceType(@NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    // second half
	    String sh = "";
	    if (ctx.unannClassType_lf_unannClassOrInterfaceType() != null || ctx.unannInterfaceType_lf_unannClassOrInterfaceType() != null) {
	        int howMany = ctx.unannClassType_lf_unannClassOrInterfaceType() == null ? 0 : ctx.unannClassType_lf_unannClassOrInterfaceType().size();
	        howMany += ctx.unannInterfaceType_lf_unannClassOrInterfaceType() == null ? 0 : ctx.unannInterfaceType_lf_unannClassOrInterfaceType().size();
	        sh = reverse(howMany, " ");
	    }
	    sb.append(stack.pop());        // first half
	    if (!sh.isEmpty()) {
	        sb.append(' ').append(sh);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) { 
	    // if ( Expression ) Statement
	}
	@Override public void exitIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    String expr = stack.pop();
	    sb.append("if (").append(expr).append(')').append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterPostDecrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
	    // '--'
	}
	@Override public void exitPostDecrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
	    stack.push("--");
	}

	@Override public void enterInterfaceType(@NotNull Java8Parser.InterfaceTypeContext ctx) {
	    // ClassType
	}
	@Override public void exitInterfaceType(@NotNull Java8Parser.InterfaceTypeContext ctx) {
	    // nothing to do here, class type should already be on the stack.
	}

	@Override public void enterMethodReference_lf_primary(@NotNull Java8Parser.MethodReference_lf_primaryContext ctx) { 
	    // '::' typeArguments? Identifier
	}
	@Override public void exitMethodReference_lf_primary(@NotNull Java8Parser.MethodReference_lf_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = ctx.Identifier().getText();
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	    sb.append(":: ").append(typeArgs).append(identifier);
	    stack.push(sb.toString());
	}

	@Override public void enterAndExpression(@NotNull Java8Parser.AndExpressionContext ctx) { 
	    // EqualityExpression 
	    // AndExpression & EqualityExpression	
	}
	@Override public void exitAndExpression(@NotNull Java8Parser.AndExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String ee = stack.pop();
	    String ae = ctx.andExpression() == null ? "" : stack.pop() + " & ";
	    sb.append(ae).append(ee);
	    stack.push(sb.toString());
	}

	@Override public void enterEnumConstantModifier(@NotNull Java8Parser.EnumConstantModifierContext ctx) { 
	    // Annotation
	}
	@Override public void exitEnumConstantModifier(@NotNull Java8Parser.EnumConstantModifierContext ctx) {
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterUnannClassType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    // Identifier typeArguments?
	}
	@Override public void exitUnannClassType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typeArgs = ctx.typeArguments() == null ? "" : " " + stack.pop();
	    sb.append(ctx.Identifier().getText()).append(typeArgs);
	    stack.push(sb.toString());
	}

	@Override public void enterStatement(@NotNull Java8Parser.StatementContext ctx) {
	    // StatementWithoutTrailingSubstatement
	    // LabeledStatement
	    // IfThenStatement
	    // IfThenElseStatement
	    // WhileStatement
	    // ForStatement
	}
	@Override public void exitStatement(@NotNull Java8Parser.StatementContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterFieldModifier(@NotNull Java8Parser.FieldModifierContext ctx) { 
	    // (one of) Annotation public protected private
	    // static final transient volatile
	}
	@Override public void exitFieldModifier(@NotNull Java8Parser.FieldModifierContext ctx) {
	    if (ctx.annotation() == null) {
	        stack.push(ctx.getText());
	    }
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterMarkerAnnotation(@NotNull Java8Parser.MarkerAnnotationContext ctx) { 
	    // @ TypeName	
	}
	@Override public void exitMarkerAnnotation(@NotNull Java8Parser.MarkerAnnotationContext ctx) {
	    StringBuilder sb = new StringBuilder("@");
	    sb.append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterResourceList(@NotNull Java8Parser.ResourceListContext ctx) {
	    // Resource {; Resource}	
	}
	@Override public void exitResourceList(@NotNull Java8Parser.ResourceListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.resource() != null) {
	        sb.append(reverse(ctx.resource().size(), ": ")).append(' ');
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterArrayAccess_lfno_primary(@NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx) {
        // (	expressionName '[' expression ']'
        // |	primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']'
        // )
        // (	primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']'
        // )*
	}
	@Override public void exitArrayAccess_lfno_primary(@NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    StringBuilder sh = new StringBuilder();
	    if (ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary() != null) {
	        for (int i = 0; i < ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary().size(); i++) {
	            String expr = stack.pop();
	            String pn = stack.pop();
	            sh.append(pn).append('[').append(expr).append(']').append(' ');
	        }
	    }
	    String expr = stack.pop();
	    String name = stack.pop();
	    sb.append(name).append('[').append(expr).append("] ").append(sh);
	    stack.push(sb.toString());
	}

	@Override public void enterStaticInitializer(@NotNull Java8Parser.StaticInitializerContext ctx) { 
	    // static Block
	}
	@Override public void exitStaticInitializer(@NotNull Java8Parser.StaticInitializerContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
	    sb.append("\n");
	    sb.append(indent("static "));
	    sb.append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) {
	    // ConditionalOrExpression 
	    // ConditionalOrExpression ? Expression : ConditionalExpression 
	    // ConditionalOrExpression ? Expression : LambdaExpression 	
	}
	@Override public void exitConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    // only need to handle the last 2 choices
	    if (ctx.conditionalExpression() != null || ctx.lambdaExpression() != null) {
	        String last = stack.pop();
	        String expr = stack.pop();
	        String coe = stack.pop();
	        sb.append(coe).append(" ? ").append(expr).append(" : ").append(last);
	        stack.push(sb.toString());
	    }
	}

	@Override public void enterFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx) {
	    // {FieldModifier} UnannType VariableDeclaratorList ;
	}
	@Override public void exitFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
	    String vdl = stack.pop();
	    String type = stack.pop();
	    if (ctx.fieldModifier() != null && !ctx.fieldModifier().isEmpty()) {
	        sb.append(reverse(ctx.fieldModifier().size(), " ")).append(' ');
	    }
	    sb.append(type).append(' ');
	    sb.append(vdl).append(";\n");
	    stack.push(sb.toString());
	}

	@Override public void enterLeftHandSide(@NotNull Java8Parser.LeftHandSideContext ctx) {
	    // :	expressionName
	    // |	fieldAccess
	    // |	arrayAccess
	
	}
	@Override public void exitLeftHandSide(@NotNull Java8Parser.LeftHandSideContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) { 
	    // for ( [ForInit] ; [Expression] ; [ForUpdate] ) Statement
	}
	@Override public void exitBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    String update = ctx.forUpdate() == null ? "" : stack.pop();
	    String expr = ctx.expression() == null ? "" : stack.pop();
	    String init = ctx.forInit() == null ? "" : stack.pop();
	    sb.append("for (").append(init).append(" : ").append(expr).append(" : ").append(update).append(")").append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) { 
	    // while ( Expression ) Statement
	}
	@Override public void exitWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    String expr = stack.pop();
	    sb.append("while (").append(expr).append(")").append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) { 
	    // {PackageModifier} package Identifier {. Identifier} ;
	}
	@Override public void exitPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    StringBuilder identifiers = new StringBuilder();
	    if (ctx.Identifier() != null ) {
            for (int i = 0; i < ctx.Identifier().size(); i++) {
                identifiers.append(ctx.Identifier(i));
                if (i < ctx.Identifier().size() - 1) {
                    identifiers.append("."); 
                }
            }
	    }
	    if (ctx.packageModifier() != null && !ctx.packageModifier().isEmpty()) {
	        sb.append(reverse(ctx.packageModifier().size(), " ")).append(' ');
	    }
	    sb.append("package ").append(identifiers.toString()).append(";");
	    stack.push(sb.toString());
	}

	@Override public void enterLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx) { 
	    // {VariableModifier} UnannyType VariableDeclaratorList
	}
	@Override public void exitLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
        String vdl = stack.pop();
        String type = stack.pop();
	    if (ctx.variableModifier() != null && !ctx.variableModifier().isEmpty()) {
	        sb.append(reverse(ctx.variableModifier().size(), " ")).append(' ');
	    }
        sb.append(type).append(' ').append(vdl);
        stack.push(sb.toString());
	}

	@Override public void enterSuperinterfaces(@NotNull Java8Parser.SuperinterfacesContext ctx) { 
	    // implements InterfaceTypeList
	}
	@Override public void exitSuperinterfaces(@NotNull Java8Parser.SuperinterfacesContext ctx) { 
	    StringBuilder sb = new StringBuilder("implements ");
	    sb.append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx) { 
	    // ConstantDeclaration 
	    // InterfaceMethodDeclaration 
	    // ClassDeclaration 
	    // InterfaceDeclaration 
	    // ;	
	}
	@Override public void exitInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx) {
	    if (ctx.constantDeclaration() == null && ctx.interfaceMethodDeclaration() == null
	        && ctx.classDeclaration() == null && ctx.interfaceDeclaration() == null) {
	        stack.push(";\n");
	    }
	}

	@Override public void enterClassInstanceCreationExpression_lf_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
	    // '.' 'new' typeArguments? annotation* Identifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
	}
	@Override public void exitClassInstanceCreationExpression_lf_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String classBody = ctx.classBody() == null ? "" : stack.pop();
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    String typeOrDiamond = ctx.typeArgumentsOrDiamond() == null ? "" : stack.pop();
	    String identifier = ctx.Identifier().getText();
	    StringBuilder annotations = new StringBuilder();
	    if (ctx.annotation() != null) {
	        for (int i = 0; i < ctx.annotation().size(); i++) {
	            annotations.append(stack.pop()).append(' ');    
	        }
	    }
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	    sb.append(".new ").append(typeArgs).append(annotations).append(typeOrDiamond).append('(').append(argumentList).append(')').append(classBody);
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchBlock(@NotNull Java8Parser.SwitchBlockContext ctx) { 
	    // { {SwitchBlockStatementGroup} {SwitchLabel} }
	}
	@Override public void exitSwitchBlock(@NotNull Java8Parser.SwitchBlockContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String label = stack.pop();
	    String group = stack.pop();
	    sb.append("{\n").append(group).append(' ').append(label).append("\n}\n");
	    stack.push(sb.toString());
	}

	@Override public void enterForInit(@NotNull Java8Parser.ForInitContext ctx) { 
	    // StatementExpressionList
	    // LocalVariableDeclaration
	}
	@Override public void exitForInit(@NotNull Java8Parser.ForInitContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterAnnotationDim(@NotNull Java8Parser.AnnotationDimContext ctx) { 
	    // annotation* squareBrackets
	}
	@Override public void exitAnnotationDim(@NotNull Java8Parser.AnnotationDimContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String squareBrackets = stack.pop();
	    if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                sb.append(stack.pop()).append(' ');                
            }
	    }
	    sb.append(squareBrackets);
	    stack.push(sb.toString());
	}

	@Override public void enterBlockStatements(@NotNull Java8Parser.BlockStatementsContext ctx) {
	    // BlockStatement {BlockStatement}
	}
	@Override public void exitBlockStatements(@NotNull Java8Parser.BlockStatementsContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.blockStatement() != null) {
	        sb.append(reverse(ctx.blockStatement().size(), "\n"));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx) { 
	    // FormalParameter {, FormalParameter}
	    // ReceiverParameter {, FormalParameter}
	}
	@Override public void exitFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.formalParameter() != null) {
	        sb.append(reverse(ctx.formalParameter().size(), ", "));
	    }
        if (ctx.receiverParameter() != null) {
            sb.insert(0, stack.pop());    
        }
	    stack.push(sb.toString());
	}

	@Override public void enterTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx) {
	    // TypeArguments 
	    // <>	
	}
	@Override public void exitTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx) {
	    if (ctx.typeArguments() == null) {
	        stack.push("<>");    
	    }
	}

	@Override public void enterPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) { 
        // :	literal
        // |	typeName ('[' ']')* '.' 'class'
        // |	unannPrimitiveType ('[' ']')* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression_lfno_primary
        // |	fieldAccess_lfno_primary
        // |	methodInvocation_lfno_primary
        // |	methodReference_lfno_primary
	
	}
	@Override public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) { 
	    if (ctx.literal() != null || ctx.classInstanceCreationExpression_lfno_primary() != null ||
	        ctx.fieldAccess_lfno_primary() != null  ||
	        ctx.methodInvocation_lfno_primary() != null || ctx.methodReference_lfno_primary() != null) {
	        // one of these choices is already on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    
	    // expression
	    if (ctx.expression() != null) {
	        sb.append("(").append(stack.pop()).append(")");
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        sb.append(stack.pop());
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            if (ctx.squareBrackets() != null) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append("[]");
	                }
	            }
	            sb.append(".class");
	        }
	        else {
	            // second typeName choice
	            sb.append(".this");
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // unann primitive type
	    if (ctx.unannPrimitiveType() != null) {
	        sb.append(stack.pop());
	        if (raw.indexOf("class") > -1) {
	            if (ctx.squareBrackets() != null) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append("[]");
	                }
	            }
	            sb.append(".class");
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
	        sb.append("void.class");
	    }
	    else {
	        sb.append("this");   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx) { 
	    // default ElementValue	
	}
	@Override public void exitDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx) { 
	    StringBuilder sb = new StringBuilder("default ");
	    sb.append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterType(@NotNull Java8Parser.TypeContext ctx) {
	    // :	primitiveType
	    // |	referenceType
	}
	@Override public void exitType(@NotNull Java8Parser.TypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterElementValuePairList(@NotNull Java8Parser.ElementValuePairListContext ctx) { 
	    // ElementValuePair {, ElementValuePair}	
	}
	@Override public void exitElementValuePairList(@NotNull Java8Parser.ElementValuePairListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.elementValuePair() != null) {
	        sb.append(reverse(ctx.elementValuePair().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterSynchronizedStatement(@NotNull Java8Parser.SynchronizedStatementContext ctx) {
	    // synchronized ( Expression ) Block
	}
	@Override public void exitSynchronizedStatement(@NotNull Java8Parser.SynchronizedStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder("synchronized (");
	    String block = stack.pop();
	    String expr = stack.pop();
	    sb.append(expr).append(") ").append(block);
	    stack.push(sb.toString());
	}

	@Override public void enterUnannClassType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) { 
	    // '.' annotationIdentifier typeArguments?
	}
	@Override public void exitUnannClassType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String args = ctx.typeArguments() == null ? "" : stack.pop();
	    sb.append('.').append(stack.pop()).append(' ').append(args);
	    stack.push(sb.toString());
	}

	@Override public void enterAdditiveOperator(@NotNull Java8Parser.AdditiveOperatorContext ctx) { 
        // :   '+'
        // |   '-'
	}
	@Override public void exitAdditiveOperator(@NotNull Java8Parser.AdditiveOperatorContext ctx) { 
	    stack.push(ctx.getText());
	}

	@Override public void enterSuperclass(@NotNull Java8Parser.SuperclassContext ctx) { 
	    // extends ClassType
	}
	@Override public void exitSuperclass(@NotNull Java8Parser.SuperclassContext ctx) {
	    stack.push("extends " + stack.pop());
	}

	@Override public void enterBlock(@NotNull Java8Parser.BlockContext ctx) {
	    // { [BlockStatements] }
	    ++tabCount;
	}
	@Override public void exitBlock(@NotNull Java8Parser.BlockContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String blockEnd = stack.pop();
	    sb.append("{\n");
	    if (ctx.blockStatements() != null) {
	        sb.append(stack.pop()).append('\n');   
	    }
	    --tabCount;
	    String blockStart = stack.pop();
	    String end = indent("}");
	    sb.append(end).append("\n");
	    stack.push(sb.toString());
	}

	@Override public void enterExpressionStatement(@NotNull Java8Parser.ExpressionStatementContext ctx) { 
	    // StatementExpression ;
	}
	@Override public void exitExpressionStatement(@NotNull Java8Parser.ExpressionStatementContext ctx) { 
	    stack.push(stack.pop() + ";");
	}

	@Override public void enterPreIncrementExpression(@NotNull Java8Parser.PreIncrementExpressionContext ctx) { 
	    // ++ UnaryExpression	
	}
	@Override public void exitPreIncrementExpression(@NotNull Java8Parser.PreIncrementExpressionContext ctx) {
	    stack.push("++ " + stack.pop());
	}

	@Override public void enterEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx) { 
	    // ; {ClassBodyDeclaration}
	}
	@Override public void exitEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx) { 
	    StringBuilder sb = new StringBuilder("; ");
	    sb.append(ctx.classBodyDeclaration() == null ? "" : stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterDimExprs(@NotNull Java8Parser.DimExprsContext ctx) { 
	    // DimExpr {DimExpr}
	}
	@Override public void exitDimExprs(@NotNull Java8Parser.DimExprsContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.dimExpr() != null) {
	        for (int i = 0; i < ctx.dimExpr().size(); i++) {
	            sb.append(stack.pop()).append(' ');    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterForUpdate(@NotNull Java8Parser.ForUpdateContext ctx) { 
	    // StatementExpressionList
	}
	@Override public void exitForUpdate(@NotNull Java8Parser.ForUpdateContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterEmptyStatement(@NotNull Java8Parser.EmptyStatementContext ctx) {
	    // ; 
	}
	@Override public void exitEmptyStatement(@NotNull Java8Parser.EmptyStatementContext ctx) { 
	    stack.push(";\n");
	}

	@Override public void enterPrimaryNoNewArray_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) { 
        // :	classInstanceCreationExpression_lf_primary
        // |	fieldAccess_lf_primary
        // |	arrayAccess_lf_primary
        // |	methodInvocation_lf_primary
        // |	methodReference_lf_primary
	
	}
	@Override public void exitPrimaryNoNewArray_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.	
	}

	@Override public void enterAnnotationTypeElementModifier(@NotNull Java8Parser.AnnotationTypeElementModifierContext ctx) {
	    // (one of) Annotation public abstract - Done	
	}
	@Override public void exitAnnotationTypeElementModifier(@NotNull Java8Parser.AnnotationTypeElementModifierContext ctx) { 
	    if (ctx.annotation() == null) {
	        stack.push(ctx.getText());
	    }
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterShiftExpression(@NotNull Java8Parser.ShiftExpressionContext ctx) { 
	    // AdditiveExpression 
	    // ShiftExpression shiftOperator AdditiveExpression 
	    // ShiftExpression shiftOperator AdditiveExpression 
	    // ShiftExpression shiftOperator AdditiveExpression	
	}
	@Override public void exitShiftExpression(@NotNull Java8Parser.ShiftExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String additiveExpression = stack.pop();
	    if (ctx.shiftExpression() != null) {
	         String shiftExpression = stack.pop();
	         String shiftOperator = stack.pop();
	         sb.append(shiftExpression).append(' ').append(shiftOperator).append(' ');
	    }
	    sb.append(additiveExpression);
	    stack.push(sb.toString());
	}
	
	@Override public void enterFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) { 
	    // '.' Identifier
	}
	@Override public void exitFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    sb.append('.').append(ctx.Identifier().getText());
	    stack.push(sb.toString());
	}

	@Override public void enterInstanceInitializer(@NotNull Java8Parser.InstanceInitializerContext ctx) { 
	    // Block
	}
	@Override public void exitInstanceInitializer(@NotNull Java8Parser.InstanceInitializerContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterUnannType(@NotNull Java8Parser.UnannTypeContext ctx) {
	    // UnannPrimitiveType
	    // UnannReferenceType
	}
	@Override public void exitUnannType(@NotNull Java8Parser.UnannTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterIntegralType(@NotNull Java8Parser.IntegralTypeContext ctx) {
	    // (one of) byte short int long char - Done
	}
	@Override public void exitIntegralType(@NotNull Java8Parser.IntegralTypeContext ctx) { 
	    stack.push(ctx.getText());
	}

	@Override public void enterPostIncrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) { 
	    // '++'
	}
	@Override public void exitPostIncrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) {
	    stack.push("++");
	}

	@Override public void enterClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx) {
	    // ClassType
	    // InterfaceType
	}
	@Override public void exitClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx) { 
		// nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterEqualityExpression(@NotNull Java8Parser.EqualityExpressionContext ctx) {
	    // RelationalExpression 
	    // EqualityExpression == RelationalExpression 
	    // EqualityExpression != RelationalExpression	
	}
	@Override public void exitEqualityExpression(@NotNull Java8Parser.EqualityExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String re = stack.pop();
	    if (ctx.equalityExpression() != null) {
	        String ee = stack.pop();
	        String raw = ctx.getText();
	        String operator = raw.indexOf("==") > -1 ? " == " : " != ";
	        sb.append(ee).append(operator);
	    }
	    sb.append(re);
	    stack.push(sb.toString());
	}

	@Override public void enterNormalAnnotation(@NotNull Java8Parser.NormalAnnotationContext ctx) { 
	    // @ TypeName ( [ElementValuePairList] ) - Done
	}
	@Override public void exitNormalAnnotation(@NotNull Java8Parser.NormalAnnotationContext ctx) {
	    StringBuilder sb = new StringBuilder("@");
        sb.append(stack.pop());     // TypeName
        sb.append("(");
        sb.append(stack.pop());     // ElementValuePairList
        sb.append(")");
        stack.push(sb.toString());
	}

	@Override public void enterTypeBound(@NotNull Java8Parser.TypeBoundContext ctx) { 
	    // extends TypeVariable
	    // extends ClassOrInterfaceType {AdditionalBound}
	}
	@Override public void exitTypeBound(@NotNull Java8Parser.TypeBoundContext ctx) { 
	    StringBuilder sb = new StringBuilder("extends ");
	    if (ctx.typeVariable() != null) {
	        sb.append(stack.pop());    
	    }
	    else {
	        StringBuilder bounds = new StringBuilder();
	        if (ctx.additionalBound() != null) {
	            for (int i = 0; i < ctx.additionalBound().size(); i++) {
	                bounds.append(stack.pop()).append(' ');    
	            }
	        }
	        sb.append(stack.pop());
	        if (!bounds.toString().isEmpty()) {
	            sb.append(' ').append(bounds.toString());    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPrimary(@NotNull Java8Parser.PrimaryContext ctx) {
        // :	(	primaryNoNewArray_lfno_primary
        //     |	arrayCreationExpression
        //     )
        //     (	primaryNoNewArray_lf_primary
        //     )*
        // ;
	}
	@Override public void exitPrimary(@NotNull Java8Parser.PrimaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    StringBuilder lf_primary = new StringBuilder();
	    if (ctx.primaryNoNewArray_lf_primary() != null) {
            for (int i = 0; i < ctx.primaryNoNewArray_lf_primary().size(); i++) {
                lf_primary.append(stack.pop()).append(' ');    
            }
	    }
	    if (ctx.primaryNoNewArray_lfno_primary() != null || ctx.arrayCreationExpression() != null) {
	        sb.append(stack.pop());    
	    }
	    sb.append(lf_primary.toString());
	    
	    stack.push(sb.toString());
	}

	@Override public void enterClassModifier(@NotNull Java8Parser.ClassModifierContext ctx) { 
	    // (one of) Annotation public protected private
	    // abstract static final strictfp
	}
	@Override public void exitClassModifier(@NotNull Java8Parser.ClassModifierContext ctx) {
	    /*
	    if (ctx.annotation() == null) {
	        stack.push(ctx.getText());
	    }
	    */
	    // if there is an annotation, it will already be on the stack
	    // nothing to do here, either an annotation or one of the terminals will be on the stack
	}

	@Override public void enterFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) { 
	    // Primary . Identifier
	    // super . Identifier
	    // TypeName . super . Identifier
	}
	@Override public void exitFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.primary() != null) {
	        sb.append(stack.pop().trim()).append('.').append(ctx.Identifier());
	    }
	    else if (ctx.typeName() != null) {
	        sb.append(stack.pop()).append(".super.").append(ctx.Identifier());    
	    }
	    else {
	        sb.append("super.").append(ctx.Identifier());    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterTypeName(@NotNull Java8Parser.TypeNameContext ctx) {
	    // Identifier
	    // PackageOrTypeName . Identifier 
	}
	@Override public void exitTypeName(@NotNull Java8Parser.TypeNameContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typename = ctx.packageOrTypeName() == null ? "" : stack.pop() + '.';
	    sb.append(typename).append(ctx.Identifier().getText());
	    stack.push(sb.toString());
	}

	@Override public void enterConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx) {
	    // { [ExplicitConstructorInvocation] [BlockStatement] }
	    ++tabCount;
	}
	@Override public void exitConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String bs = ctx.blockStatements() == null ? "" : stack.pop();
	    String eci = ctx.explicitConstructorInvocation() == null ? "" : stack.pop();
	    sb.append("{\n").append(eci).append(bs).append("\n");
	    --tabCount;
	    String end = indent("}");
	    sb.append(end).append("\n");
	    stack.push(sb.toString());
	}

	@Override public void enterCatchClause(@NotNull Java8Parser.CatchClauseContext ctx) {
	    // catch ( CatchFormalParameter ) Block	
	}
	@Override public void exitCatchClause(@NotNull Java8Parser.CatchClauseContext ctx) { 
	    StringBuilder sb = new StringBuilder("catch (");
	    String block = stack.pop();
	    String cfp = stack.pop();
	    sb.append(cfp).append(")").append(block);
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) { 
	    // do nothing here
	}
	@Override public void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) { 
	    // do nothing here
	}

	@Override public void enterLabeledStatement(@NotNull Java8Parser.LabeledStatementContext ctx) {
	    // Identifier : Statement
	}
	@Override public void exitLabeledStatement(@NotNull Java8Parser.LabeledStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(ctx.Identifier().getText()).append(": ").append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchLabels(@NotNull Java8Parser.SwitchLabelsContext ctx) { 
	    // SwitchLabel {SwitchLabel}
	}
	@Override public void exitSwitchLabels(@NotNull Java8Parser.SwitchLabelsContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.switchLabel() != null) {
	        sb.append(reverse(ctx.switchLabel().size(), " ")).append(' ');
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) {
	    // {VariableModifier} UnannType VariableDeclaratorId
	}
	@Override public void exitFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String vdi = stack.pop();
	    String unannType = stack.pop();
	    if (ctx.variableModifier() != null && !ctx.variableModifier().isEmpty()) {
	        sb.append(reverse(ctx.variableModifier().size(), " ")).append(' ');
	    }
	    sb.append(unannType).append(' ').append(vdi);
	    stack.push(sb.toString());
	}
	@Override public void enterInterfaceType_lf_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) { 
	    // classType_lf_classOrInterfaceType
	}
	@Override public void exitInterfaceType_lf_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) { 
	    // import TypeName ;
	}
	@Override public void exitSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("import ");
	    sb.append(stack.pop());
	    sb.append(";\n");
	    stack.push(sb.toString());
	}

	@Override public void enterSingleElementAnnotation(@NotNull Java8Parser.SingleElementAnnotationContext ctx) { 
	    // @ TypeName ( ElementValue ) - Done	
	}
	@Override public void exitSingleElementAnnotation(@NotNull Java8Parser.SingleElementAnnotationContext ctx) {
	    StringBuilder sb = new StringBuilder("@");
	    sb.append(stack.pop());    // TypeName
	    sb.append("(");
	    sb.append(stack.pop());    // ElementValue
	    sb.append(")");
	    stack.push(sb.toString());
	}

	@Override public void enterElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx) {
	    // { [ElementValueList] [,] }	
	}
	@Override public void exitElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx) { 
	    StringBuilder sb = new StringBuilder("{");
	    if (ctx.elementValueList() != null) {
	        sb.append(stack.pop());    
	    }
	    sb.append("}");
	    stack.push(sb.toString());
	}

	@Override public void enterConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx) { 
	    // Expression
	}
	@Override public void exitConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx) { 
	    // nothing to do here, expression should already be on the stack.
	}

	@Override public void enterForStatement(@NotNull Java8Parser.ForStatementContext ctx) {
	    // BasicForStatement
	    // EnhancedForStatement
	}
	@Override public void exitForStatement(@NotNull Java8Parser.ForStatementContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterTypeArgumentList(@NotNull Java8Parser.TypeArgumentListContext ctx) { 
	    // TypeArgument {, TypeArgument}
	}
	@Override public void exitTypeArgumentList(@NotNull Java8Parser.TypeArgumentListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.typeArgument() != null) {
	        sb.append(reverse(ctx.typeArgument().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPostDecrementExpression(@NotNull Java8Parser.PostDecrementExpressionContext ctx) { 
	    // PostfixExpression --	
	}
	@Override public void exitPostDecrementExpression(@NotNull Java8Parser.PostDecrementExpressionContext ctx) { 
	    stack.push(stack.pop() + " -- ");
	}

	@Override public void enterReceiverParameter(@NotNull Java8Parser.ReceiverParameterContext ctx) { 
	    // {Annotation} UnannType [Identifier .] this
	}
	@Override public void exitReceiverParameter(@NotNull Java8Parser.ReceiverParameterContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String identifier = ctx.Identifier() == null ? "" : ctx.Identifier().getText() + '.';
	    String type = stack.pop();
	    if (ctx.annotation() != null) {
	        for (int i = 0; i < ctx.annotation().size(); i++) {
	            sb.append(stack.pop()).append(' ');    
	        }
	    }
	    sb.append(type).append(' ').append(identifier).append("this");
	    stack.push(sb.toString());
	}

	@Override public void enterWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) { 
	    // while ( Expression ) StatementNoShortIf
	}
	@Override public void exitWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder("while (");
	    String stmt = stack.pop();
	    String expr = stack.pop();
	    sb.append(expr).append(")").append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterEnumConstantList(@NotNull Java8Parser.EnumConstantListContext ctx) {
	    // EnumConstant {, EnumConstant}
	}
	@Override public void exitEnumConstantList(@NotNull Java8Parser.EnumConstantListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.enumConstant() != null) {
	        sb.append(reverse(ctx.enumConstant().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterDoStatement(@NotNull Java8Parser.DoStatementContext ctx) { 
	    // do Statement while ( Expression )
	}
	@Override public void exitDoStatement(@NotNull Java8Parser.DoStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
	    String stmt = stack.pop();
	    sb.append("do ").append(stmt).append(" while (").append(expr).append(")");
	    stack.push(sb.toString());
	}

	@Override public void enterCatchType(@NotNull Java8Parser.CatchTypeContext ctx) {
	    // UnannClassType {| ClassType}	
	}
	@Override public void exitCatchType(@NotNull Java8Parser.CatchTypeContext ctx) { 
	    StringBuilder sb = new StringBuilder(" ");
	    if (ctx.classType() != null) {
	        for (int i = 0; i < ctx.classType().size(); i++) {
	            sb.append(stack.pop()).append(" | ");    
	        }
	    }
	    stack.push(stack.pop() + sb.toString()); 
	}

	@Override public void enterForStatementNoShortIf(@NotNull Java8Parser.ForStatementNoShortIfContext ctx) { 
	    // BasicForStatementNoShortIf
	    // EnhancedForStatementNoShortIf
	}
	@Override public void exitForStatementNoShortIf(@NotNull Java8Parser.ForStatementNoShortIfContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) { 
	    // if ( Expression ) statementNoShortIf else Statement
	}
	@Override public void exitIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    String sn = stack.pop();
	    String expr = stack.pop();
	    sb.append("if (").append(expr).append(")").append(sn).append(" else ").append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterAssignmentOperator(@NotNull Java8Parser.AssignmentOperatorContext ctx) {
	    // (one of) =  *=  /=  %=  +=  -=  <<=  >>=  >>>=  &=  ^=  |=
	}
	@Override public void exitAssignmentOperator(@NotNull Java8Parser.AssignmentOperatorContext ctx) {
	    stack.push(ctx.getText());
	}

	@Override public void enterLabeledStatementNoShortIf(@NotNull Java8Parser.LabeledStatementNoShortIfContext ctx) { 
	    // Identifier ':' statementNoShortIf
	}
	@Override public void exitLabeledStatementNoShortIf(@NotNull Java8Parser.LabeledStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String stmt = stack.pop();
	    sb.append(ctx.Identifier().getText()).append('.').append(stmt);
	    stack.push(sb.toString());
	}

	@Override public void enterCatches(@NotNull Java8Parser.CatchesContext ctx) { 
	    // CatchClause {CatchClause}	
	}
	@Override public void exitCatches(@NotNull Java8Parser.CatchesContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.catchClause() != null) {
	        for (int i = 0; i < ctx.catchClause().size(); i++) {
	            sb.append(stack.pop()).append(' ');    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodReference_lfno_primary(@NotNull Java8Parser.MethodReference_lfno_primaryContext ctx) {
	    // :	expressionName '::' typeArguments? Identifier
	    // |	referenceType '::' typeArguments? Identifier
	    // |	'super' '::' typeArguments? Identifier
	    // |	typeName '.' 'super' '::' typeArguments? Identifier
	    // |	classType '::' typeArguments? 'new'
	    // |	arrayType '::' 'new'
	
	}
	@Override public void exitMethodReference_lfno_primary(@NotNull Java8Parser.MethodReference_lfno_primaryContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.expressionName() != null) {
	        String identifier = ctx.Identifier().getText();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        sb.append(stack.pop()).append(" :: ").append(typeArgs).append(identifier);
	    }
	    else if (ctx.referenceType() != null) {
	        String identifier = ctx.Identifier().getText();    
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        sb.append(stack.pop()).append(" :: ").append(typeArgs).append(identifier);
	    }
	    else if (ctx.typeName() != null) {
	        String identifier = ctx.Identifier().getText();    
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        sb.append(stack.pop()).append(".super :: ").append(typeArgs).append(identifier);
	    }
	    else if (ctx.classType() != null) {
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        sb.append(stack.pop()).append(" :: ").append(typeArgs).append(" new ");
	    }
	    else if (ctx.arrayType() != null) {
	        sb.append(stack.pop()).append(" :: new ");
	    }
	    else {
	        String identifier = ctx.Identifier().getText();    
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        sb.append("super :: ").append(typeArgs).append(identifier);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterVariableDeclaratorList(@NotNull Java8Parser.VariableDeclaratorListContext ctx) { 
	    // VariableDeclarator {, VariableDeclarator}
	}
	@Override public void exitVariableDeclaratorList(@NotNull Java8Parser.VariableDeclaratorListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.variableDeclarator() != null) {
	        sb.append(reverse(ctx.variableDeclarator().size(), ", "));
	    }
	    
	    stack.push(sb.toString());
	}

	@Override public void enterVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx) {
	    // (one of) Annotation final
	}
	@Override public void exitVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx) {
	    if (ctx.annotation() == null) {
	        stack.push(ctx.getText());
	    }
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterCastExpression(@NotNull Java8Parser.CastExpressionContext ctx) { 
	    // ( PrimitiveType ) UnaryExpression 
	    // ( ReferenceType {AdditionalBound} ) UnaryExpressionNotPlusMinus 
	    // ( ReferenceType {AdditionalBound} ) LambdaExpression 	
	}
	@Override public void exitCastExpression(@NotNull Java8Parser.CastExpressionContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.unaryExpression() != null) {
	        String expr = stack.pop().trim();
	        String type = stack.pop();
	        sb.append("(").append(type).append(")").append(expr);
	    }
	    else {
	        String expr = stack.pop().trim();
	        StringBuilder bounds = new StringBuilder();
	        if (ctx.additionalBound() != null) {
	            for (int i = 0; i < ctx.additionalBound().size(); i++) {
	                bounds.append(stack.pop()).append(' ');    
	            }
	        }
	        String type = stack.pop();
	        sb.append("(").append(type);
	        if (!bounds.toString().isEmpty()) {
	            sb.append(' ').append(bounds.toString());
	        }
	        sb.append(")");
	        sb.append(expr);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterConditionalOrExpression(@NotNull Java8Parser.ConditionalOrExpressionContext ctx) { 
	    // ConditionalAndExpression 
	    // ConditionalOrExpression || ConditionalAndExpression	
	}
	@Override public void exitConditionalOrExpression(@NotNull Java8Parser.ConditionalOrExpressionContext ctx) {
	    
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
	    if (ctx.conditionalOrExpression() != null) {
	        sb.append(stack.pop()).append(" || ");    
	    }
	    sb.append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeParameterModifier(@NotNull Java8Parser.TypeParameterModifierContext ctx) { 
	    // Annotation
	}
	@Override public void exitTypeParameterModifier(@NotNull Java8Parser.TypeParameterModifierContext ctx) { 
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx) {
	    // Identifier = ElementValue	
	}
	@Override public void exitElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    sb.append(ctx.Identifier().getText()).append(" = ").append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterFloatingPointType(@NotNull Java8Parser.FloatingPointTypeContext ctx) { 
	    // (one of) float double
	}
	@Override public void exitFloatingPointType(@NotNull Java8Parser.FloatingPointTypeContext ctx) { 
	    stack.push(ctx.getText());
	}

	@Override public void enterDimExpr(@NotNull Java8Parser.DimExprContext ctx) {
	    // {Annotation} [ Expression ]	
	}
	@Override public void exitDimExpr(@NotNull Java8Parser.DimExprContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expression = ctx.expression() == null ? "" : stack.pop();
	    if (ctx.annotation() != null) {
	        for (int i = 0; i < ctx.annotation().size(); i++) {
	            String annotation = stack.pop();
	            sb.append(annotation).append(' ');
	        }
	    }
	    sb.append('[').append(expression).append(']');
	    stack.push(sb.toString());
	}

	@Override public void enterUnannInterfaceType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {
	    // unannClassType_lf_unannClassOrInterfaceType
	}
	@Override public void exitUnannInterfaceType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterResource(@NotNull Java8Parser.ResourceContext ctx) {
	    // {VariableModifier} UnannType VariableDeclaratorId = Expression	
	}
	@Override public void exitResource(@NotNull Java8Parser.ResourceContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
	    String vdi = stack.pop();
	    String type = stack.pop();
	    if (ctx.variableModifier() != null) {
	        sb.append(reverse(ctx.variableModifier().size(), " ")).append(' ');
	    }
	    sb.append(type).append(' ').append(vdi).append(" = ").append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterInclusiveOrExpression(@NotNull Java8Parser.InclusiveOrExpressionContext ctx) {
	    // ExclusiveOrExpression 
	    // InclusiveOrExpression | ExclusiveOrExpression	
	}
	@Override public void exitInclusiveOrExpression(@NotNull Java8Parser.InclusiveOrExpressionContext ctx) { 
	    
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
        if (ctx.inclusiveOrExpression() != null) {
            sb.append(stack.pop()).append(" | ");            
        }
        sb.append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceMethodModifier(@NotNull Java8Parser.InterfaceMethodModifierContext ctx) { 
	    // (one of) Annotation public 
	    // abstract default static strictfp	
	}
	@Override public void exitInterfaceMethodModifier(@NotNull Java8Parser.InterfaceMethodModifierContext ctx) { 
	    if (ctx.annotation() == null) {
	        stack.push(ctx.getText());
	    }
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx) {
	    // ( ResourceList [;] )	
	}
	@Override public void exitResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("(").append(stack.pop()).append(")");
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceTypeList(@NotNull Java8Parser.InterfaceTypeListContext ctx) {
	    // InterfaceType {, InterfaceType}
	}
	@Override public void exitInterfaceTypeList(@NotNull Java8Parser.InterfaceTypeListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.interfaceType() != null) {
	        sb.append(reverse(ctx.interfaceType().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) { 
	    // if ( Expression ) StatementNoShortIf else StatementNoShortIf    
	}
	@Override public void exitIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String elseStmt = stack.pop();
	    String ifStmt = stack.pop();
	    String expr = stack.pop();
	    sb.append("if (").append(expr).append(")").append(ifStmt).append(" else ").append(elseStmt);
	    stack.push(sb.toString());
	}

	@Override public void enterUnannInterfaceType(@NotNull Java8Parser.UnannInterfaceTypeContext ctx) { 
	    // UnannClassType
	}
	@Override public void exitUnannInterfaceType(@NotNull Java8Parser.UnannInterfaceTypeContext ctx) { 
	    // nothing to do here, class type should already be on the stack.
	}

	@Override public void enterInterfaceModifier(@NotNull Java8Parser.InterfaceModifierContext ctx) {
	    // (one of) Annotation public protected private 
	    // abstract static strictfp	
	}
	@Override public void exitInterfaceModifier(@NotNull Java8Parser.InterfaceModifierContext ctx) { 
	    if (ctx.annotation() == null) {
	        stack.push(ctx.getText());
	    }
	    // if there is an annotation, it will already be on the stack
	}

	@Override public void enterExclusiveOrExpression(@NotNull Java8Parser.ExclusiveOrExpressionContext ctx) {
	    // AndExpression 
	    // ExclusiveOrExpression ^ AndExpression
	}
	@Override public void exitExclusiveOrExpression(@NotNull Java8Parser.ExclusiveOrExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String expr = stack.pop();
        if (ctx.exclusiveOrExpression() != null) {
            sb.append(stack.pop()).append(" ^ ");            
        }
        sb.append(expr);
	    stack.push(sb.toString());
	}

	@Override public void enterClassType(@NotNull Java8Parser.ClassTypeContext ctx) { 
	    // {Annotation} Identifier [TypeArguments]
	    // ClassOrInterfaceType . {Annotation} Identifier [TypeArguments]
	}
	@Override public void exitClassType(@NotNull Java8Parser.ClassTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.classOrInterfaceType() == null) {
	        // must have the first option
	        String typeArguments = "";
	        if (ctx.typeArguments() != null) {
	            typeArguments = stack.pop();
	        }
	        if (ctx.annotation() != null) {
	            for (int i = 0; i < ctx.annotation().size(); i++) {
	                sb.append(stack.pop()).append(' ');    
	            }
	        }
	        sb.append(ctx.Identifier().getText());
	        if (!typeArguments.isEmpty()) {
	            sb.append(' ').append(typeArguments);    
	        }
	    }
	    else {
	        // second option    
	        String typeArguments = "";
	        if (ctx.typeArguments() != null) {
	            typeArguments = stack.pop();
	        }
	        StringBuilder annotations = new StringBuilder();
	        if (ctx.annotation() != null) {
	            for (int i = 0; i < ctx.annotation().size(); i++) {
	                annotations.append(stack.pop()).append(' ');    
	            }
	        }
	        String type = stack.pop();
	        sb.append(type).append('.');
	        sb.append(annotations.toString());
	        sb.append(ctx.Identifier().getText());
	        if (!typeArguments.isEmpty()) {
	            sb.append(' ').append(typeArguments);    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPostIncrementExpression(@NotNull Java8Parser.PostIncrementExpressionContext ctx) {
	    // PostfixExpression ++	
	}
	@Override public void exitPostIncrementExpression(@NotNull Java8Parser.PostIncrementExpressionContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(stack.pop());    // postfix expression
	    sb.append(" ++");
	    stack.push(sb.toString());
	}

	@Override public void enterTryStatement(@NotNull Java8Parser.TryStatementContext ctx) {
	    // try Block Catches 
	    // try Block [Catches] Finally 
	    // TryWithResourcesStatement	
	}
	@Override public void exitTryStatement(@NotNull Java8Parser.TryStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.tryWithResourcesStatement() != null) {
	        // third option   
	        sb.append(stack.pop());
	    }
	    else if (ctx.finally_() != null) {
	        // second option
	        String finally_ = stack.pop();
	        String catches = "";
	        if (ctx.catches() != null) {
	            catches = stack.pop();    
	        }
	        String block = stack.pop();
	        sb.append("try ").append(block).append(catches).append(finally_);
	    }
	    else {
	        // first option
	        String catches = stack.pop();
	        String block = stack.pop();
	        sb.append("try ").append(block).append(catches);
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterAnnotationIdentifier(@NotNull Java8Parser.AnnotationIdentifierContext ctx) { 
	    // annotation* Identifier
	}
	@Override public void exitAnnotationIdentifier(@NotNull Java8Parser.AnnotationIdentifierContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                sb.append(stack.pop()).append(' ');                
            }
	    }
	    sb.append(ctx.Identifier().getText());
	    stack.push(sb.toString());
	}
	
	
	@Override public void enterElementValueList(@NotNull Java8Parser.ElementValueListContext ctx) {
	    // ElementValue {, ElementValue}
	}
	@Override public void exitElementValueList(@NotNull Java8Parser.ElementValueListContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.elementValue() != null) {
	        sb.append(reverse(ctx.elementValue().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) { 
	    // for ( [ForInit] ; [Expression] ; [ForUpdate] ) StatementNoShortIf
	}
	@Override public void exitBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String statement = stack.pop();
	    String forUpdate = ctx.forUpdate() == null ? "" : stack.pop();
	    String expression = ctx.expression() == null ? "" : stack.pop();
	    String forInit = ctx.forInit() == null ? "" : stack.pop();
	    sb.append("for (").append(forInit).append(" : ").append(expression).append(" : ").append(forUpdate).append(")").append(statement);
	    stack.push(sb.toString());
	}

	@Override public void enterTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) {
	    // ClassDeclaration
	    // InterfaceDeclaration
	    // ;
	}
	@Override public void exitTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.classDeclaration() == null && ctx.interfaceDeclaration() == null) {
	        sb.append(";\n");
	    }
        stack.push(sb.toString());
    }

	@Override public void enterSquareBrackets(@NotNull Java8Parser.SquareBracketsContext ctx) { 
	    //  '[]'    
	}

	@Override public void exitSquareBrackets(@NotNull Java8Parser.SquareBracketsContext ctx) { 
	    stack.push("[]");
	}
	
	@Override public void enterSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) {
	    // switch ( Expression ) SwitchBlock
	}
	@Override public void exitSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String switchBlock = stack.pop();
	    String expression = stack.pop();
	    sb.append("switch (").append(expression).append(switchBlock);
	    stack.push(sb.toString());
	}

	@Override public void enterWildcard(@NotNull Java8Parser.WildcardContext ctx) { 
	    // {Annotation} ? [WildcardBounds]
	}
	@Override public void exitWildcard(@NotNull Java8Parser.WildcardContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String wildcardBounds = "";
	    if (ctx.wildcardBounds() != null) {
	        wildcardBounds = stack.pop();   
	    }
	    StringBuilder annotations = new StringBuilder();
	    if (ctx.annotation() != null) {
	        for (int i = 0; i < ctx.annotation().size(); i++) {
	            annotations.append(stack.pop()).append(' ');    
	        }
	    }
	    sb.append(annotations.toString()).append('?').append(wildcardBounds);
	    stack.push(sb.toString());
	}

	@Override public void enterClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx) { 
	    // NormalClassDeclaration
	    // EnumDeclaration
	}
	@Override public void exitClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx) {
	    // nothing to do here, either normal class declaration or enum declaration should be 
	    // next on the stack.
	}

	@Override public void enterUnaryExpressionNotPlusMinus(@NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx) {
	    // PostfixExpression 
	    // ~ UnaryExpression 
	    // ! UnaryExpression 
	    // CastExpression	
	}
	@Override public void exitUnaryExpressionNotPlusMinus(@NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    String raw = ctx.getText();
	    if (raw.startsWith("~")) {
	        sb.append("~");    
	    }
	    else if (raw.startsWith("!")) {
	        sb.append("!");    
	    }
	    sb.append(expression);
	    stack.push(sb.toString());
	}

	@Override public void enterUnannReferenceType(@NotNull Java8Parser.UnannReferenceTypeContext ctx) {
	    // UnannClassOrInterfaceType
	    // UnannTypeVariable
	    // UnannArrayType
	}
	@Override public void exitUnannReferenceType(@NotNull Java8Parser.UnannReferenceTypeContext ctx) {
	    // nothig to do here, one of these choices should be next on the stack, so no need
	    // to pop it and push it.
	}

	@Override public void enterMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) { 
	    // Result MethodDeclarator [Throws]
	    // TypeParameters {Annotation} Result MethodDeclarator [throws]
	}
	@Override public void exitMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.typeParameters() == null) {
	        // first option
	        String throws_ = ctx.throws_() == null ? "" : stack.pop();
	        String methodDeclarator = stack.pop();
	        String result = stack.pop();
	        sb.append(result).append(' ').append(methodDeclarator);
	        if (!throws_.isEmpty()) {
	            sb.append(' ').append(throws_);    
	        }
	    }
	    else {
	        // second option 
	        String throws_ = ctx.throws_() == null ? "" : stack.pop();
	        String methodDeclarator = stack.pop();
	        String result = stack.pop();
	        StringBuilder annotations = new StringBuilder();
	        if (ctx.annotation() != null) {
	            for (int i = 0; i < ctx.annotation().size(); i++) {
	                annotations.append(stack.pop()).append(' ');    
	            }
	        }
	        String typeParameters = stack.pop();
	        sb.append(typeParameters).append(' ');
	        sb.append(annotations.toString());
	        sb.append(result).append(' ').append(methodDeclarator);
	        if (!throws_.isEmpty()) {
	            sb.append(throws_);    
	        }
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) {
	    // {VariableModifier} CatchType VariableDeclaratorId	
	}
	@Override public void exitCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String vdi = stack.pop();
	    String type = stack.pop();
	    if (ctx.variableModifier() != null) {
	        sb.append(reverse(ctx.variableModifier().size(), " ")).append(' ');
	    }
	    sb.append(type).append(' ').append(vdi);
	    stack.push(sb.toString());
	}

	@Override public void enterEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx) { 
	    // {EnumConstantModifier} Identifier [( [ArgumentList] )] [ClassBody]
	}
	@Override public void exitEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    String body = ctx.classBody() == null ? "" : stack.pop();
	    String args = ctx.argumentList() == null ? "" : stack.pop();
	    String identifier = ctx.Identifier().getText();
	    if (ctx.enumConstantModifier() != null) {
	        sb.append(reverse(ctx.enumConstantModifier().size(), " ")).append(' ');
	    }
	    sb.append(identifier);
	    if (!args.isEmpty()) {
	        sb.append("(").append(args).append(")");    
	    }
	    if (!body.isEmpty()) {
	        sb.append(' ').append(body);    
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
	    // :	methodName '(' argumentList? ')'
	    // |	typeName '.' typeArguments? Identifier '(' argumentList? ')'
	    // |	expressionName '.' typeArguments? Identifier '(' argumentList? ')'
	    // |	'super' '.' typeArguments? Identifier '(' argumentList? ')'
	    // |	typeName '.' 'super' '.' typeArguments? Identifier '(' argumentList? ')'
	}
	@Override public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    indent(sb);
	    String argumentList = ctx.argumentList() == null ? "" : stack.pop();
	    
	    // method name
	    if (ctx.methodName() != null) {
	        sb.append(stack.pop());    
	    }
	    else if (ctx.expressionName() != null) {
	        // expression name
	        String identifier = ctx.Identifier().getText();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String exprName = stack.pop();
	        sb.append(exprName).append('.').append(typeArgs).append(identifier);
	    }
	    else if (ctx.typeName() != null) {
	        // 2 type names
	        String identifier = ctx.Identifier().getText();
	        String typeArgs = ctx.typeArguments() == null ? "" : stack.pop() + ' ';
	        String typeName = stack.pop();
	        sb.append(typeName).append('.');
	        String raw = ctx.getText();
	        if (raw.indexOf("super") > -1) {
	            sb.append("super.");
	        }
	        sb.append(typeArgs).append(identifier);
	    }
	    else {
	        // super
	        System.out.println("+++++ super before: >" + sb.toString() + "<");
	        String identifier = ctx.Identifier().getText();
	        String typeArgs = ctx.typeArguments() == null || ctx.typeArguments().isEmpty() ? "" : stack.pop() + ' ';
	        sb.append("super.").append(typeArgs).append(identifier);
	        System.out.println("+++++ super after: >" + sb.toString() + "<");
	    }
	    // common ending
	    sb.append('(').append(argumentList).append(')');
	    stack.push(sb.toString());
	}

	@Override public void enterPackageName(@NotNull Java8Parser.PackageNameContext ctx) { 
	    // Identifier
	    // PackageName . Identifier
	}
	@Override public void exitPackageName(@NotNull Java8Parser.PackageNameContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typename = ctx.packageName() == null ? "" : stack.pop() + '.';
	    sb.append(typename).append(ctx.Identifier().getText());
	    stack.push(sb.toString());
	}

	@Override public void enterImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx) {
	    // SingleTypeImportDeclaration
	    // TypeImportOnDemandDeclaration
	    // SingleStaticImportDeclaration
	    // StaticImportOnDemandDeclaration
	}
	@Override public void exitImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx) {
	    // {Annotation} NumericType
	    // {Annotation} boolean
	}
	@Override public void exitPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    // could be several annotations
	    if (ctx.annotation() != null) {
	        for (int i = 0; i < ctx.annotation().size(); i++) {
	            sb.append(ctx.annotation(i)).append(' ');
	        }
	    }
	    if (ctx.numericType() != null) {
	        sb.append(ctx.numericType().getText());
	    }
	    else {
	        sb.append("boolean");
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx) { 
	    // NormalInterfaceDeclaration 
	    // AnnotationTypeDeclaration	
	}
	@Override public void exitInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx) { 
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx) {
	    // LocalVariableDeclaration ;
	}
	@Override public void exitLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    //indent(sb);
        sb.append(stack.pop().trim()).append(";");
	    stack.push(sb.toString());
	}

	@Override public void enterBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx) {
	    // LocalVariableDeclarationStatement
	    // ClassDeclaration
	    // Statement
	}
	@Override public void exitBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	    StringBuilder sb = new StringBuilder(stack.pop());
	    indent(sb);
	    stack.push(sb.toString());
	}

	@Override public void enterClassType_lf_classOrInterfaceType(@NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) { 
	    // '.' annotationIdentifier typeArguments?
	}
	@Override public void exitClassType_lf_classOrInterfaceType(@NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typeArgs = ctx.typeArguments() == null ? "" : stack.pop();
	    sb.append('.').append(stack.pop()).append(' ').append(typeArgs);
	    stack.push(sb.toString());
	}

	@Override public void enterPackageOrTypeName(@NotNull Java8Parser.PackageOrTypeNameContext ctx) {
        // Identifier 
        // PackageOrTypeName . Identifier	    
	}
	@Override public void exitPackageOrTypeName(@NotNull Java8Parser.PackageOrTypeNameContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typename = ctx.packageOrTypeName() == null ? "" : stack.pop() + '.';
	    sb.append(typename).append(ctx.Identifier().getText());
	    stack.push(sb.toString());
	}

	@Override public void enterAssignment(@NotNull Java8Parser.AssignmentContext ctx) {
	    // LeftHandSide AssignmentOperator Expression	
	}
	@Override public void exitAssignment(@NotNull Java8Parser.AssignmentContext ctx) {
	    
	    StringBuilder sb = new StringBuilder();
	    String expression = stack.pop();
	    String operator = stack.pop();
	    String lhs = stack.pop();
	    sb.append(lhs).append(' ').append(operator).append(' ').append(expression);
	    
	    stack.push(sb.toString());
	}

	@Override public void enterMethodName(@NotNull Java8Parser.MethodNameContext ctx) {
	    // Identifier
	}
	@Override public void exitMethodName(@NotNull Java8Parser.MethodNameContext ctx) {
	    stack.push(ctx.Identifier().getText());
	}

	@Override public void enterStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx) {
        // Assignment 
        // PreIncrementExpression 
        // PreDecrementExpression 
        // PostIncrementExpression 
        // PostDecrementExpression 
        // MethodInvocation 
        // ClassInstanceCreationExpression	    
	}
	@Override public void exitStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterBreakStatement(@NotNull Java8Parser.BreakStatementContext ctx) {
	    // break [Identifier] ;
	}
	@Override public void exitBreakStatement(@NotNull Java8Parser.BreakStatementContext ctx) { 
	    StringBuilder sb = new StringBuilder("break");
	    if (ctx.Identifier() != null) {
	        sb.append(' ');
	        sb.append(ctx.Identifier().getText());
	    }
	    sb.append(";\n");
	    stack.push(sb.toString());
	}

	@Override public void enterAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) { 
	    // Identifier 
	    // AmbiguousName . Identifier
	}
	@Override public void exitAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String typename = ctx.ambiguousName() == null ? "" : stack.pop() + '.';
	    sb.append(typename).append(ctx.Identifier().getText());
	    stack.push(sb.toString());
	}

	@Override public void enterStatementExpressionList(@NotNull Java8Parser.StatementExpressionListContext ctx) { 
	    // StatementExpression {, StatementExpression}
	}
	@Override public void exitStatementExpressionList(@NotNull Java8Parser.StatementExpressionListContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    
	    if (ctx.statementExpression() != null) {
	        sb.append(reverse(ctx.statementExpression().size(), ", "));
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx) {
	    // {InterfaceMethodModifier} MethodHeader MethodBody	
	}
	@Override public void exitInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx) {
	    StringBuilder sb = new StringBuilder();
	    String methodBody = stack.pop();
	    String methodHeader = stack.pop();
	    if (ctx.interfaceMethodModifier() != null) {
	        sb.append(reverse(ctx.interfaceMethodModifier().size(), " ")).append(' ');
	    }
	    sb.append(methodHeader).append(methodBody);
	    stack.push(sb.toString());
	}

	@Override public void enterThrows_(@NotNull Java8Parser.Throws_Context ctx) { 
	    // throws ExceptionTypeList
	}
	@Override public void exitThrows_(@NotNull Java8Parser.Throws_Context ctx) { 
	    StringBuilder sb = new StringBuilder();
	    sb.append("throws ").append(stack.pop());
	    stack.push(sb.toString());
	}

	@Override public void enterSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) { 
        // case ConstantExpression : 
        // case EnumConstantName : 
        // default :	
	}
	@Override public void exitSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.constantExpression() != null || ctx.enumConstantName() != null) {
	        sb.append("case ").append(stack.pop()).append(":\n");   
	    }
	    else {
	        sb.append("default:\n");   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterMethodReference(@NotNull Java8Parser.MethodReferenceContext ctx) {
	    // ExpressionName :: [TypeArguments] Identifier 
	    // ReferenceType :: [TypeArguments] Identifier 
	    // Primary :: [TypeArguments] Identifier 
	    // super :: [TypeArguments] Identifier 
	    // TypeName . super :: [TypeArguments] Identifier 
	    // ClassType :: [TypeArguments] new 
	    // ArrayType :: new	
	}
	@Override public void exitMethodReference(@NotNull Java8Parser.MethodReferenceContext ctx) { 
	    StringBuilder sb = new StringBuilder();
	    if (ctx.expressionName() != null || ctx.referenceType() != null || ctx.primary() != null ) {
	        String typeArguments = "";
	        if (ctx.typeArguments() != null) {
	            typeArguments = stack.pop();    
	        }
	        String expressionName = stack.pop();
	        sb.append(expressionName).append(" :: ");
	        if (!typeArguments.isEmpty()) {
	            sb.append(typeArguments).append(' ');    
	        }
	        sb.append(ctx.Identifier().getText());
	    }
	    else if (ctx.typeName() != null) {
	        String typeArguments = "";
	        if (ctx.typeArguments() != null) {
	            typeArguments = stack.pop();    
	        }
	        String typeName = stack.pop();
	        sb.append(typeName).append(".super :: ");
	        if (!typeArguments.isEmpty()) {
	            sb.append(typeArguments).append(' ');    
	        }
	        sb.append(ctx.Identifier().getText());
	    }
	    else if (ctx.classType() != null) {
	        String typeArguments = "";
	        if (ctx.typeArguments() != null) {
	            typeArguments = stack.pop();    
	        }
	        String classType = stack.pop();
	        sb.append(classType).append(" :: ");
	        if (!typeArguments.isEmpty()) {
	            sb.append(typeArguments).append(' ');    
	        }
	        sb.append("new");
	    }
	    else if (ctx.arrayType() != null) {
	        String arrayType = stack.pop();
	        sb.append(arrayType).append(" :: new");
	    }
	    else {
	        sb.append("super :: ");
	        if (ctx.typeArguments() != null) {
	            sb.append(stack.pop()).append(' ');    
	        }
	        sb.append(ctx.Identifier().getText());
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterPrimaryNoNewArray(@NotNull Java8Parser.PrimaryNoNewArrayContext ctx) { 
        // :	literal
        // |	typeName ('[' ']')* '.' 'class'
        // |	'void' '.' 'class'
        // |	'this'
        // |	typeName '.' 'this'
        // |	'(' expression ')'
        // |	classInstanceCreationExpression
        // |	fieldAccess
        // |	arrayAccess
        // |	methodInvocation
        // |	methodReference
	}
	@Override public void exitPrimaryNoNewArray(@NotNull Java8Parser.PrimaryNoNewArrayContext ctx) { 
	    if (ctx.literal() != null || ctx.classInstanceCreationExpression() != null ||
	        ctx.fieldAccess() != null || ctx.arrayAccess() != null || 
	        ctx.methodInvocation() != null || ctx.methodReference() != null ) {
	        // one of these choices will already be on the stack
	        return;
	    }
	    
	    StringBuilder sb = new StringBuilder();
	    
	    // expression
	    if (ctx.expression() != null) {
	        sb.append("(").append(stack.pop()).append(")");
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 2 typeName choices
	    String raw = ctx.getText();
	    if (ctx.typeName() != null) {
	        sb.append(stack.pop());
	        if (raw.indexOf("class") > -1) {
	            // first typeName choice
	            if (ctx.squareBrackets() != null) {
	                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
	                    sb.append("[]");
	                }
	            }
	            sb.append(".class");
	        }
	        else {
	            // second typeName choice
	            sb.append(".this");
	        }
	        stack.push(sb.toString());
	        return;
	    }
	    
	    // 'void' and 'this' choice
	    if (raw.indexOf("void") > -1) {
	        sb.append("void.class");
	    }
	    else {
	        sb.append("this");   
	    }
	    stack.push(sb.toString());
	}

	@Override public void enterNumericType(@NotNull Java8Parser.NumericTypeContext ctx) {
	    // IntegerType
	    // FloatingPointType
	}
	@Override public void exitNumericType(@NotNull Java8Parser.NumericTypeContext ctx) {
	    // nothing to do here, one of the choices should already be on the stack.
	}

	@Override public void enterContinueStatement(@NotNull Java8Parser.ContinueStatementContext ctx) { 
	    // continue [Identifier] ;
	}
	@Override public void exitContinueStatement(@NotNull Java8Parser.ContinueStatementContext ctx) {
	    StringBuilder sb = new StringBuilder("continue");
	    if (ctx.Identifier() != null) {
	        sb.append(' ');
	        sb.append(ctx.Identifier().getText());
	    }
	    sb.append(";\n");
	    stack.push(sb.toString());
	}


	@Override public void enterEveryRule(@NotNull ParserRuleContext ctx) {
	    /*
        Token token = ctx.getStart(); 
        int tokenIndex = token.getTokenIndex();
        System.out.println("+++++ token: " + token + ", " + tokenIndex);
        List<Token> commentTokens = tokens.getHiddenTokensToLeft(tokenIndex, Java8Lexer.COMMENTS);
        if (commentTokens != null) {
            if ( commentTokens != null ) {
                // there could be multiple line comments
                // like this comment is on two lines
                for (Token commentToken : commentTokens) {
                    System.out.println("+++++ commentToken: " + (commentToken == null ? "null" : commentToken.getText()));
                    if ( commentToken != null && commentToken.getType() == Java8Lexer.LINE_COMMENT) {
                        String comment = commentToken.getText();
                        if (stack.size() == 0) {
                            stack.push(comment + '\n');
                        }
                        else {
                            String last = stack.peek();
                            if (last != null && last.indexOf(comment) == -1) {
                                last = stack.pop();
                                last = last + "    " + comment + "\n";
                                stack.push(last);
                            }
                        }
                    }
                    else if ( commentToken != null && (commentToken.getType() == Java8Lexer.COMMENT || commentToken.getType() == Java8Lexer.DOC_COMMENT)) {
                        String comment = commentToken.getText();
                        if (stack.size() == 0) {
                            stack.push(comment + '\n');
                        }
                        else {
                            String last = stack.peek();
                            if (last != null && last.indexOf(comment) == -1) {
                                last = stack.pop();
                                last = last + comment;
                                stack.push(last);
                            }
                        }
                    }
                }
            }
        }
        */
	}
	
	// handle comments
	@Override public void exitEveryRule(@NotNull ParserRuleContext ctx) { 
	    /*
        Token semi = ctx.getStop(); 
        if (Java8Lexer.SEMI == semi.getType()) {
            int i = semi.getTokenIndex();
            List<Token> commentTokens = tokens.getHiddenTokensToRight(i, Java8Lexer.COMMENTS); 
            if ( commentTokens != null ) {
                for (Token commentToken : commentTokens) {
                    if ( commentToken != null && commentToken.getType() == Java8Lexer.LINE_COMMENT) {
                        String comment = commentToken.getText();
                        String last = stack.peek();
                        if (last.indexOf(comment) == -1) {
                            last = stack.pop().trim();
                            last = last + "    " + comment;
                            stack.push(last);
                        }
                    }
                    else if ( commentToken != null && (commentToken.getType() == Java8Lexer.COMMENT || commentToken.getType() == Java8Lexer.DOC_COMMENT)) {
                        String comment = commentToken.getText();
                        String last = stack.peek();
                        if (last.indexOf(comment) == -1) {
                            last = stack.pop();
                            last = last + comment;
                            stack.push(last);
                        }
                    }
                }
            }
        }
        */
	}

	@Override public void visitTerminal(@NotNull TerminalNode node) { 
        String terminalText = node.getText();
        stack.push(terminalText);
        System.out.println("+++++ visitTerminal: " + terminalText);        
        Token token = node.getSymbol(); 
        int tokenIndex = token.getTokenIndex();
        List<Token> commentTokens = tokens.getHiddenTokensToLeft(tokenIndex, Java8Lexer.COMMENTS);
        if (commentTokens != null) {
            if ( commentTokens != null ) {
                // there could be multiple line comments
                // like this comment is on two lines
                for (Token commentToken : commentTokens) {
                    if ( commentToken != null && commentToken.getType() == Java8Lexer.LINE_COMMENT) {
                        String comment = commentToken.getText();
                        
                        if (stack.size() == 0) {
                            stack.push(comment + '\n');
                        }
                        else {
                            String last = stack.peek();
                            if (last != null && last.indexOf(comment) == -1) {
                                last = stack.pop();
                                last = last + "    " + comment + "\n";
                                stack.push(last);
                            }
                        }
                    }
                    else if ( commentToken != null && (commentToken.getType() == Java8Lexer.COMMENT || commentToken.getType() == Java8Lexer.DOC_COMMENT)) {
                        String comment = commentToken.getText();
                        String last = stack.peek();
                        if (last != null && last.indexOf(comment) == -1) {
                            last = stack.pop();
                            last = last + comment;
                            stack.push(last);
                        }
                    }
                }
            }
        }
	}
	@Override public void visitErrorNode(@NotNull ErrorNode node) { }
}