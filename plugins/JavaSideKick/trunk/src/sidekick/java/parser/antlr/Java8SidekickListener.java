// Generated from Java8.g4 by ANTLR 4.4

package sidekick.java.parser.antlr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.java.node.*;

import sidekick.util.Location;


/**
 * This class provides an empty implementation of {@link Java8Listener},
 * which can be extended to create a listener which only needs to handle a subset
 * of the available methods.
 */
public class Java8SidekickListener extends Java8BaseListener {
    Deque<TigerNode> stack = new ArrayDeque<TigerNode>();
    
    List<ErrorNode> errors = new ArrayList<ErrorNode>();
    
    // top level node to describe a java file
    private CUNode cuNode;
    
    // accumulates counts of classes, interfaces, methods and fields.
    private Results results = new Results();
    

    public CUNode getCompilationUnit() {
        return cuNode;   
    }
    
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassMemberDeclaration(@NotNull Java8Parser.ClassMemberDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassMemberDeclaration(@NotNull Java8Parser.ClassMemberDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStatementNoShortIf(@NotNull Java8Parser.StatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStatementNoShortIf(@NotNull Java8Parser.StatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx) { }
	/**
        constantModifier
            :	annotation
            |	'public'
            |	'static'
            |	'final'
            ;
	 */
	@Override public void enterConstantModifier(@NotNull Java8Parser.ConstantModifierContext ctx) { 
        int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "public":
                modifier &= ModifierSet.PUBLIC;
                break;
            case "static":
                modifier &= ModifierSet.STATIC;
                break;
            case "final":
                modifier &= ModifierSet.FINAL;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
            }
        TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
	
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConstantModifier(@NotNull Java8Parser.ConstantModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLambdaBody(@NotNull Java8Parser.LambdaBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLambdaBody(@NotNull Java8Parser.LambdaBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassInstanceCreationExpression_lfno_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassInstanceCreationExpression_lfno_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnaryExpression(@NotNull Java8Parser.UnaryExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnaryExpression(@NotNull Java8Parser.UnaryExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassType_lfno_classOrInterfaceType(@NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassType_lfno_classOrInterfaceType(@NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterArrayType(@NotNull Java8Parser.ArrayTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitArrayType(@NotNull Java8Parser.ArrayTypeContext ctx) { }
	/**
	 * simpleTypeName
	 * 	:	Identifier
	 * 	;
	 */
	@Override public void enterSimpleTypeName(@NotNull Java8Parser.SimpleTypeNameContext ctx) {
	    TigerNode node = stack.peek();
	    node.setName(ctx.getText());
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStatementWithoutTrailingSubstatement(@NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStatementWithoutTrailingSubstatement(@NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx) { }
	/**
	 * constructorDeclarator
	 * 	:	typeParameters? simpleTypeName '(' formalParameterList? ')'
	 * 	;
	 */
	@Override public void enterConstructorDeclarator(@NotNull Java8Parser.ConstructorDeclaratorContext ctx) { 
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConstructorDeclarator(@NotNull Java8Parser.ConstructorDeclaratorContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAssertStatement(@NotNull Java8Parser.AssertStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAssertStatement(@NotNull Java8Parser.AssertStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterArrayCreationExpression(@NotNull Java8Parser.ArrayCreationExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitArrayCreationExpression(@NotNull Java8Parser.ArrayCreationExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannArrayType(@NotNull Java8Parser.UnannArrayTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannArrayType(@NotNull Java8Parser.UnannArrayTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) {
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTryWithResourcesStatement(@NotNull Java8Parser.TryWithResourcesStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTryWithResourcesStatement(@NotNull Java8Parser.TryWithResourcesStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExceptionType(@NotNull Java8Parser.ExceptionTypeContext ctx) {
	    ThrowsNode tn = new ThrowsNode(ctx.getText());
	    tn.setStartLocation(getStartLocation(ctx));
	    tn.setEndLocation(getEndLocation(ctx));
	    stack.push(tn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExceptionType(@NotNull Java8Parser.ExceptionTypeContext ctx) {
	    TigerNode node = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(node);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExceptionTypeList(@NotNull Java8Parser.ExceptionTypeListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExceptionTypeList(@NotNull Java8Parser.ExceptionTypeListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAdditiveExpression(@NotNull Java8Parser.AdditiveExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAdditiveExpression(@NotNull Java8Parser.AdditiveExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterRelationalExpression(@NotNull Java8Parser.RelationalExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitRelationalExpression(@NotNull Java8Parser.RelationalExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterReferenceType(@NotNull Java8Parser.ReferenceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitReferenceType(@NotNull Java8Parser.ReferenceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterArrayAccess_lf_primary(@NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitArrayAccess_lf_primary(@NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInferredFormalParameterList(@NotNull Java8Parser.InferredFormalParameterListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInferredFormalParameterList(@NotNull Java8Parser.InferredFormalParameterListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
        ImportNode in = new ImportNode(ctx.packageOrTypeName().getText() + ".*");
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        stack.push(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
	    ImportNode in = (ImportNode)stack.pop();
        CUNode cu = (CUNode)stack.peek();
        cu.addImport(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx) {
	    Type t = new Type();
	    t.setStartLocation(getStartLocation(ctx));
	    t.setEndLocation(getEndLocation(ctx));
	    stack.push(t);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx) {
	    Type t = (Type)stack.pop();
	    TigerNode parent = stack.peek();
	    parent.setType(t);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) { 
	    if (ctx.variableDeclaratorId() == null) {
	        return;
	    }
	    Parameter p = new Parameter(ctx.variableDeclaratorId().getText(), null);
	    p.setVarArg(ctx.getText().indexOf("...") > 0);
	    p.setStartLocation(getStartLocation(ctx));
	    p.setEndLocation(getEndLocation(ctx));
	    stack.push(p);    // for annotations, type, and variable modifier
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) {
	    if (ctx.variableDeclaratorId() == null) {
	        return;
	    }
	    Parameter p = (Parameter)stack.pop();
	    Parameterizable parent = (Parameterizable)stack.peek();
	    parent.addFormalParameter(p);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLiteral(@NotNull Java8Parser.LiteralContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLiteral(@NotNull Java8Parser.LiteralContext ctx) { }
	/**
	 * result
	 * 	:	unannType
	 * 	|	'void'
	 *  ;
	 */	
	@Override public void enterResult(@NotNull Java8Parser.ResultContext ctx) {
        String text = ctx.getText();
        Type returnType = null;
        if ("void".equals(text)) {
            returnType = new Type("void");
            returnType.setStartLocation(getStartLocation(ctx));
            returnType.setEndLocation(getEndLocation(ctx));
        } else {
            Java8Parser.UnannTypeContext utc = ctx.unannType();
            returnType = new Type(utc.getText());
            returnType.setName(utc.getText());
            returnType.setStartLocation(getStartLocation(utc));
            returnType.setEndLocation(getEndLocation(utc));
        }
        if (stack.peek() instanceof MethodNode) {
            MethodNode parent = (MethodNode)stack.peek();
            parent.setReturnType(returnType);
        }
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitResult(@NotNull Java8Parser.ResultContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) { }
	/**
        variableDeclarator
            :	variableDeclaratorId ('=' variableInitializer)?
            ;
        
        variableDeclaratorId
            :	Identifier dims?
            ;
	 */
	@Override public void enterVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) {
	    VariableDeclarator vd = new VariableDeclarator(ctx.variableDeclaratorId().Identifier().getText());
	    vd.setStartLocation(getStartLocation(ctx));
	    vd.setEndLocation(getEndLocation(ctx));
	    stack.push(vd);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) { 
	    TigerNode vd = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(vd);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFinally_(@NotNull Java8Parser.Finally_Context ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFinally_(@NotNull Java8Parser.Finally_Context ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassBody(@NotNull Java8Parser.ClassBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassBody(@NotNull Java8Parser.ClassBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannInterfaceType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannInterfaceType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx) { }
	/**
        constantDeclaration
            :	constantModifier* unannType variableDeclaratorList ';'
            ;
	 */
	@Override public void enterConstantDeclaration(@NotNull Java8Parser.ConstantDeclarationContext ctx) {
 	    FieldNode fn = new FieldNode();
	    fn.setStartLocation(getStartLocation(ctx));
	    fn.setEndLocation(getEndLocation(ctx));
	    stack.push(fn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConstantDeclaration(@NotNull Java8Parser.ConstantDeclarationContext ctx) { 
	    FieldNode fn = (FieldNode)stack.pop();
	    if (fn.getChildren() != null) {
	        TigerNode parent = stack.peek();
	        for (TigerNode child : fn.getChildren()) {
	            FieldNode newField = new FieldNode(child.getName(), fn.getModifiers(), fn.getRealType());
	            newField.setStartLocation(fn.getStartLocation());
	            newField.setEndLocation(fn.getEndLocation());
	            parent.addChild(newField);
	        }
	    }
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPostfixExpression(@NotNull Java8Parser.PostfixExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPostfixExpression(@NotNull Java8Parser.PostfixExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAnnotation(@NotNull Java8Parser.AnnotationContext ctx) { 
        AnnotationNode node = new AnnotationNode(ctx.getText());
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        stack.push(node);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAnnotation(@NotNull Java8Parser.AnnotationContext ctx) { 
	    AnnotationNode an = (AnnotationNode)stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addAnnotation(an);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx) {
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx) {
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) {
        ImportNode in = new ImportNode(ctx.typeName().getText() + ".*");
        in.setModifiers(ModifierSet.STATIC);
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        stack.push(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) { 
	    ImportNode in = (ImportNode)stack.pop();
	    CUNode cu = (CUNode)stack.peek();
	    cu.addImport(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExpression(@NotNull Java8Parser.ExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExpression(@NotNull Java8Parser.ExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterThrowStatement(@NotNull Java8Parser.ThrowStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitThrowStatement(@NotNull Java8Parser.ThrowStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) { 
        ImportNode in = new ImportNode(ctx.typeName().packageOrTypeName().getText() + '.' + ctx.typeName().Identifier());
        in.setModifiers(ModifierSet.STATIC);
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        stack.push(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) {
	    ImportNode in = (ImportNode)stack.pop();
	    CUNode cu = (CUNode)stack.peek();
	    cu.addImport(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLambdaParameters(@NotNull Java8Parser.LambdaParametersContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLambdaParameters(@NotNull Java8Parser.LambdaParametersContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterConditionalAndExpression(@NotNull Java8Parser.ConditionalAndExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConditionalAndExpression(@NotNull Java8Parser.ConditionalAndExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMultiplicativeExpression(@NotNull Java8Parser.MultiplicativeExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMultiplicativeExpression(@NotNull Java8Parser.MultiplicativeExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPackageModifier(@NotNull Java8Parser.PackageModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPackageModifier(@NotNull Java8Parser.PackageModifierContext ctx) { }
	/**
	 * constructorDeclaration
	 * 	:	constructorModifier* constructorDeclarator throws_? constructorBody
	 * 	;
	 */
	@Override public void enterConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx) {
	    ConstructorNode node = new ConstructorNode();
	    node.setStartLocation(getStartLocation(ctx));
	    node.setEndLocation(getEndLocation(ctx));
	    stack.push(node);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx) { 
	    TigerNode node = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(node);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lfno_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lfno_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannTypeVariable(@NotNull Java8Parser.UnannTypeVariableContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannTypeVariable(@NotNull Java8Parser.UnannTypeVariableContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterNormalInterfaceDeclaration(@NotNull Java8Parser.NormalInterfaceDeclarationContext ctx) {
	    InterfaceNode cn = new InterfaceNode(ctx.Identifier().getText());
	    cn.setStartLocation(getStartLocation(ctx));
	    cn.setEndLocation(getEndLocation(ctx));
	    stack.push(cn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitNormalInterfaceDeclaration(@NotNull Java8Parser.NormalInterfaceDeclarationContext ctx) { 
	    TigerNode cn = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(cn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInterfaceType_lfno_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceType_lfno_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterConstructorModifier(@NotNull Java8Parser.ConstructorModifierContext ctx) { 
        int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "public":
                modifier &= ModifierSet.PUBLIC;
                break;
            case "protected":
                modifier &= ModifierSet.PROTECTED;
                break;
            case "private":
                modifier &= ModifierSet.PRIVATE;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
            }
        TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConstructorModifier(@NotNull Java8Parser.ConstructorModifierContext ctx) {}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassInstanceCreationExpression(@NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassInstanceCreationExpression(@NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx) { }
	/**
	 * methodDeclarator
	 * 	:	Identifier '(' formalParameterList? ')' dims?
	 * 	;
	 * The use of dims is very strongly discouraged and is only for backward compatibility with very early versions of Java.
	 */
	@Override public void enterMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) {
	    TigerNode parent = stack.peek();
	    parent.setName(ctx.Identifier().getText());
	    // TODO: dims
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAnnotationTypeMemberDeclaration(@NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAnnotationTypeMemberDeclaration(@NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPreDecrementExpression(@NotNull Java8Parser.PreDecrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPreDecrementExpression(@NotNull Java8Parser.PreDecrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterVariableInitializerList(@NotNull Java8Parser.VariableInitializerListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVariableInitializerList(@NotNull Java8Parser.VariableInitializerListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExtendsInterfaces(@NotNull Java8Parser.ExtendsInterfacesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExtendsInterfaces(@NotNull Java8Parser.ExtendsInterfacesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterElementValue(@NotNull Java8Parser.ElementValueContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitElementValue(@NotNull Java8Parser.ElementValueContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterArrayAccess(@NotNull Java8Parser.ArrayAccessContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitArrayAccess(@NotNull Java8Parser.ArrayAccessContext ctx) { }
	/**
     *  methodModifier
     *      :	annotation
     *      |	'public'
     *      |	'protected'
     *      |	'private'
     *      |	'abstract'
     *      |	'static'
     *      |	'final'
     *      |	'synchronized'
     *      |	'native'
     *      |	'strictfp'
     *      ;
	 */
	@Override public void enterMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) { 
	    int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "public":
                modifier &= ModifierSet.PUBLIC;
                break;
            case "protected":
                modifier &= ModifierSet.PROTECTED;
                break;
            case "private":
                modifier &= ModifierSet.PRIVATE;
                break;
            case "abstract":
                modifier &= ModifierSet.ABSTRACT;
                break;
            case "static":
                modifier &= ModifierSet.STATIC;
                break;
            case "final":
                modifier &= ModifierSet.FINAL;
                break;
            case "synchronized":
                modifier &= ModifierSet.SYNCHRONIZED;
                break;
            case "native":
                modifier &= ModifierSet.NATIVE;
                break;
            case "strictfp":
                modifier &= ModifierSet.STRICTFP;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
            }
	    TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
    }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannClassType(@NotNull Java8Parser.UnannClassTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannClassType(@NotNull Java8Parser.UnannClassTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLambdaExpression(@NotNull Java8Parser.LambdaExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLambdaExpression(@NotNull Java8Parser.LambdaExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAssignmentExpression(@NotNull Java8Parser.AssignmentExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAssignmentExpression(@NotNull Java8Parser.AssignmentExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeParameterList(@NotNull Java8Parser.TypeParameterListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeParameterList(@NotNull Java8Parser.TypeParameterListContext ctx) { }
	/**
        normalClassDeclaration
            :	classModifier* 'class' Identifier typeParameters? superclass? superinterfaces? classBody
            ;
	 */
	@Override public void enterNormalClassDeclaration(@NotNull Java8Parser.NormalClassDeclarationContext ctx) { 
	    ClassNode cn = new ClassNode(ctx.Identifier().getText());
	    cn.setStartLocation(getStartLocation(ctx));
	    cn.setEndLocation(getEndLocation(ctx));
	    stack.push(cn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitNormalClassDeclaration(@NotNull Java8Parser.NormalClassDeclarationContext ctx) { 
	    TigerNode cn = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(cn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx) {
	    InterfaceNode cn = new InterfaceNode(ctx.Identifier().getText());
	    cn.setStartLocation(getStartLocation(ctx));
	    cn.setEndLocation(getEndLocation(ctx));
	    stack.push(cn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx) { 
	    TigerNode cn = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(cn);
	}
	/**
	 * compilationUnit
	 * 	:	packageDeclaration? importDeclaration* typeDeclaration* EOF
	 * 	;
	 */
	@Override public void enterCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx) {
	    TigerNode node = new CUNode();
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
	    stack.push(node);
	}
	/**
	 *
	 */
	@Override public void exitCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx) { 
	    cuNode = (CUNode)stack.pop();
	    dumpErrors();
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterWildcardBounds(@NotNull Java8Parser.WildcardBoundsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitWildcardBounds(@NotNull Java8Parser.WildcardBoundsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lf_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lf_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeVariable(@NotNull Java8Parser.TypeVariableContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeVariable(@NotNull Java8Parser.TypeVariableContext ctx) { }
	/**
	 * typeParameter
	 * 	:	typeParameterModifier* Identifier typeBound?
	 * 	;
	 */
	@Override public void enterTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx) { }
	/**
	 * methodDeclaration
	 * 	:	methodModifier* methodHeader methodBody
	 * 	;
	 */
	@Override public void enterMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
	    MethodNode mn = new MethodNode();
	    mn.setStartLocation(getStartLocation(ctx));
	    mn.setEndLocation(getEndLocation(ctx));
	    stack.push(mn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) {
	    TigerNode node = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(node);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodBody(@NotNull Java8Parser.MethodBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodBody(@NotNull Java8Parser.MethodBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterDims(@NotNull Java8Parser.DimsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitDims(@NotNull Java8Parser.DimsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannPrimitiveType(@NotNull Java8Parser.UnannPrimitiveTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannPrimitiveType(@NotNull Java8Parser.UnannPrimitiveTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExplicitConstructorInvocation(@NotNull Java8Parser.ExplicitConstructorInvocationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExplicitConstructorInvocation(@NotNull Java8Parser.ExplicitConstructorInvocationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnumBody(@NotNull Java8Parser.EnumBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnumBody(@NotNull Java8Parser.EnumBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAdditionalBound(@NotNull Java8Parser.AdditionalBoundContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAdditionalBound(@NotNull Java8Parser.AdditionalBoundContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannClassOrInterfaceType(@NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannClassOrInterfaceType(@NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPostDecrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPostDecrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) { }
	/**
        interfaceType
            :	classType
            ;
	 */
	@Override public void enterInterfaceType(@NotNull Java8Parser.InterfaceTypeContext ctx) {
	    ImplementsNode it = new ImplementsNode(ctx.classType().Identifier().getText());
	    it.setStartLocation(getStartLocation(ctx));
	    it.setEndLocation(getEndLocation(ctx));
	    stack.push(it);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceType(@NotNull Java8Parser.InterfaceTypeContext ctx) {
	    TigerNode it = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(it);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodReference_lf_primary(@NotNull Java8Parser.MethodReference_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodReference_lf_primary(@NotNull Java8Parser.MethodReference_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAndExpression(@NotNull Java8Parser.AndExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAndExpression(@NotNull Java8Parser.AndExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnumConstantModifier(@NotNull Java8Parser.EnumConstantModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnumConstantModifier(@NotNull Java8Parser.EnumConstantModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannClassType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannClassType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStatement(@NotNull Java8Parser.StatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStatement(@NotNull Java8Parser.StatementContext ctx) { }
	/**
        fieldModifier
            :	annotation
            |	'public'
            |	'protected'
            |	'private'
            |	'static'
            |	'final'
            |	'transient'
            |	'volatile'
            ;
	 */
	@Override public void enterFieldModifier(@NotNull Java8Parser.FieldModifierContext ctx) { 
        int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "public":
                modifier &= ModifierSet.PUBLIC;
                break;
            case "protected":
                modifier &= ModifierSet.PROTECTED;
                break;
            case "private":
                modifier &= ModifierSet.PRIVATE;
                break;
            case "static":
                modifier &= ModifierSet.STATIC;
                break;
            case "final":
                modifier &= ModifierSet.FINAL;
                break;
            case "transient":
                modifier &= ModifierSet.TRANSIENT;
                break;
            case "volatile":
                modifier &= ModifierSet.VOLATILE;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
            }
        TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
	}
	@Override public void exitFieldModifier(@NotNull Java8Parser.FieldModifierContext ctx) { }
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMarkerAnnotation(@NotNull Java8Parser.MarkerAnnotationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMarkerAnnotation(@NotNull Java8Parser.MarkerAnnotationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterResourceList(@NotNull Java8Parser.ResourceListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitResourceList(@NotNull Java8Parser.ResourceListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterArrayAccess_lfno_primary(@NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitArrayAccess_lfno_primary(@NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStaticInitializer(@NotNull Java8Parser.StaticInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStaticInitializer(@NotNull Java8Parser.StaticInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) { }
	/**
        fieldDeclaration
            :	fieldModifier* unannType variableDeclaratorList ';'
            ;
	 */
	@Override public void enterFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx) { 
 	    FieldNode fn = new FieldNode();
	    fn.setStartLocation(getStartLocation(ctx));
	    fn.setEndLocation(getEndLocation(ctx));
	    stack.push(fn);
	}
	@Override public void exitFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx) {
	    FieldNode fn = (FieldNode)stack.pop();
	    if (fn.getChildren() != null) {
	        TigerNode parent = stack.peek();
	        for (TigerNode child : fn.getChildren()) {
	            FieldNode newField = new FieldNode(child.getName(), fn.getModifiers(), fn.getRealType());
	            newField.setStartLocation(fn.getStartLocation());
	            newField.setEndLocation(fn.getEndLocation());
	            parent.addChild(newField);
	        }
	    }
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLeftHandSide(@NotNull Java8Parser.LeftHandSideContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLeftHandSide(@NotNull Java8Parser.LeftHandSideContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) { }
	/**
	 * packageDeclaration
	 * 	:	packageModifier* 'package' Identifier ('.' Identifier)* ';'
	 * 	;
	 */
	@Override public void enterPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) { 
	    TigerNode packageNode = new TigerNode();
	    packageNode.setStartLocation(getStartLocation(ctx));
	    packageNode.setEndLocation(getEndLocation(ctx));
	    stack.push(packageNode);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) { 
	    TigerNode node = stack.pop();
	    CUNode cu = (CUNode)stack.peek();
	    cu.setPackage(node);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx) { 
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx) {
	}
	/**
        superinterfaces
            :	'implements' interfaceTypeList
            ;
	 */
	@Override public void enterSuperinterfaces(@NotNull Java8Parser.SuperinterfacesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSuperinterfaces(@NotNull Java8Parser.SuperinterfacesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassInstanceCreationExpression_lf_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassInstanceCreationExpression_lf_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSwitchBlock(@NotNull Java8Parser.SwitchBlockContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSwitchBlock(@NotNull Java8Parser.SwitchBlockContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterForInit(@NotNull Java8Parser.ForInitContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitForInit(@NotNull Java8Parser.ForInitContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterBlockStatements(@NotNull Java8Parser.BlockStatementsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitBlockStatements(@NotNull Java8Parser.BlockStatementsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterType(@NotNull Java8Parser.TypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitType(@NotNull Java8Parser.TypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterElementValuePairList(@NotNull Java8Parser.ElementValuePairListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitElementValuePairList(@NotNull Java8Parser.ElementValuePairListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSynchronizedStatement(@NotNull Java8Parser.SynchronizedStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSynchronizedStatement(@NotNull Java8Parser.SynchronizedStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannClassType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannClassType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) { }
	/**
        superclass
            :	'extends' classType
            ;
	 */
	@Override public void enterSuperclass(@NotNull Java8Parser.SuperclassContext ctx) { 
	    ExtendsNode en = new ExtendsNode(ctx.classType().Identifier().getText());
	    en.setStartLocation(getStartLocation(ctx));
	    en.setEndLocation(getEndLocation(ctx));
	    stack.push(en);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSuperclass(@NotNull Java8Parser.SuperclassContext ctx) { 
	    TigerNode en = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(en);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterBlock(@NotNull Java8Parser.BlockContext ctx) {
	    BlockNode bn = new BlockNode();
	    bn.setStartLocation(getStartLocation(ctx));
	    bn.setEndLocation(getEndLocation(ctx));
	    stack.push(bn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitBlock(@NotNull Java8Parser.BlockContext ctx) { 
	    TigerNode bn = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(bn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExpressionStatement(@NotNull Java8Parser.ExpressionStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExpressionStatement(@NotNull Java8Parser.ExpressionStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPreIncrementExpression(@NotNull Java8Parser.PreIncrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPreIncrementExpression(@NotNull Java8Parser.PreIncrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterDimExprs(@NotNull Java8Parser.DimExprsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitDimExprs(@NotNull Java8Parser.DimExprsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterForUpdate(@NotNull Java8Parser.ForUpdateContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitForUpdate(@NotNull Java8Parser.ForUpdateContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEmptyStatement(@NotNull Java8Parser.EmptyStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEmptyStatement(@NotNull Java8Parser.EmptyStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAnnotationTypeElementModifier(@NotNull Java8Parser.AnnotationTypeElementModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAnnotationTypeElementModifier(@NotNull Java8Parser.AnnotationTypeElementModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterShiftExpression(@NotNull Java8Parser.ShiftExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitShiftExpression(@NotNull Java8Parser.ShiftExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInstanceInitializer(@NotNull Java8Parser.InstanceInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInstanceInitializer(@NotNull Java8Parser.InstanceInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannType(@NotNull Java8Parser.UnannTypeContext ctx) { 
	    Type type = new Type();
	    type.setName(ctx.getText());
	    type.setTypeName(ctx.getText());
	    type.setStartLocation(getStartLocation(ctx));
	    type.setEndLocation(getEndLocation(ctx));
	    stack.push(type);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannType(@NotNull Java8Parser.UnannTypeContext ctx) {
	    Type type = (Type)stack.pop();
	    TigerNode parent = stack.peek();
	    parent.setType(type);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterIntegralType(@NotNull Java8Parser.IntegralTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitIntegralType(@NotNull Java8Parser.IntegralTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPostIncrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPostIncrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEqualityExpression(@NotNull Java8Parser.EqualityExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEqualityExpression(@NotNull Java8Parser.EqualityExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterNormalAnnotation(@NotNull Java8Parser.NormalAnnotationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitNormalAnnotation(@NotNull Java8Parser.NormalAnnotationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeBound(@NotNull Java8Parser.TypeBoundContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeBound(@NotNull Java8Parser.TypeBoundContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimary(@NotNull Java8Parser.PrimaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimary(@NotNull Java8Parser.PrimaryContext ctx) { }
	/**
        classModifier
            :	annotation
            |	'public'
            |	'protected'
            |	'private'
            |	'abstract'
            |	'static'
            |	'final'
            |	'strictfp'
            ;
	 */
	@Override public void enterClassModifier(@NotNull Java8Parser.ClassModifierContext ctx) { 
        int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "public":
                modifier &= ModifierSet.PUBLIC;
                break;
            case "protected":
                modifier &= ModifierSet.PROTECTED;
                break;
            case "private":
                modifier &= ModifierSet.PRIVATE;
                break;
            case "abstract":
                modifier &= ModifierSet.ABSTRACT;
                break;
            case "static":
                modifier &= ModifierSet.STATIC;
                break;
            case "final":
                modifier &= ModifierSet.FINAL;
                break;
            case "strictfp":
                modifier &= ModifierSet.STRICTFP;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
        }
        TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassModifier(@NotNull Java8Parser.ClassModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeName(@NotNull Java8Parser.TypeNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeName(@NotNull Java8Parser.TypeNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterCatchClause(@NotNull Java8Parser.CatchClauseContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitCatchClause(@NotNull Java8Parser.CatchClauseContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLabeledStatement(@NotNull Java8Parser.LabeledStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLabeledStatement(@NotNull Java8Parser.LabeledStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSwitchLabels(@NotNull Java8Parser.SwitchLabelsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSwitchLabels(@NotNull Java8Parser.SwitchLabelsContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) {
	    Parameter p = new Parameter(ctx.variableDeclaratorId().getText(), null);
	    p.setStartLocation(getStartLocation(ctx));
	    p.setEndLocation(getEndLocation(ctx));
	    stack.push(p);    // for annotations and type
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) {
	    Parameter p = (Parameter)stack.pop();
	    if (stack.peek() instanceof Parameterizable) {
            Parameterizable parent = (Parameterizable)stack.peek();
            parent.addFormalParameter(p);
	    }
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInterfaceType_lf_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceType_lf_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) { 
        ImportNode in = new ImportNode(ctx.typeName().packageOrTypeName().getText() + '.' + ctx.typeName().Identifier());
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        stack.push(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) {
	    ImportNode in = (ImportNode)stack.pop();
        CUNode cu = (CUNode)stack.peek();
        cu.addImport(in);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSingleElementAnnotation(@NotNull Java8Parser.SingleElementAnnotationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSingleElementAnnotation(@NotNull Java8Parser.SingleElementAnnotationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterForStatement(@NotNull Java8Parser.ForStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitForStatement(@NotNull Java8Parser.ForStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeArgumentList(@NotNull Java8Parser.TypeArgumentListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeArgumentList(@NotNull Java8Parser.TypeArgumentListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPostDecrementExpression(@NotNull Java8Parser.PostDecrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPostDecrementExpression(@NotNull Java8Parser.PostDecrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterReceiverParameter(@NotNull Java8Parser.ReceiverParameterContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitReceiverParameter(@NotNull Java8Parser.ReceiverParameterContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnumConstantList(@NotNull Java8Parser.EnumConstantListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnumConstantList(@NotNull Java8Parser.EnumConstantListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterDoStatement(@NotNull Java8Parser.DoStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitDoStatement(@NotNull Java8Parser.DoStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterCatchType(@NotNull Java8Parser.CatchTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitCatchType(@NotNull Java8Parser.CatchTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterForStatementNoShortIf(@NotNull Java8Parser.ForStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitForStatementNoShortIf(@NotNull Java8Parser.ForStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAssignmentOperator(@NotNull Java8Parser.AssignmentOperatorContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAssignmentOperator(@NotNull Java8Parser.AssignmentOperatorContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLabeledStatementNoShortIf(@NotNull Java8Parser.LabeledStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLabeledStatementNoShortIf(@NotNull Java8Parser.LabeledStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterCatches(@NotNull Java8Parser.CatchesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitCatches(@NotNull Java8Parser.CatchesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodReference_lfno_primary(@NotNull Java8Parser.MethodReference_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodReference_lfno_primary(@NotNull Java8Parser.MethodReference_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterVariableDeclaratorList(@NotNull Java8Parser.VariableDeclaratorListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVariableDeclaratorList(@NotNull Java8Parser.VariableDeclaratorListContext ctx) { }
	/**
	 * variableModifier
	 * 	:	annotation
	 * 	|	'final'
	 */	;
	@Override public void enterVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx) { 
        int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "final":
                modifier &= ModifierSet.FINAL;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
            }
        TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx) {
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterCastExpression(@NotNull Java8Parser.CastExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitCastExpression(@NotNull Java8Parser.CastExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterConditionalOrExpression(@NotNull Java8Parser.ConditionalOrExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitConditionalOrExpression(@NotNull Java8Parser.ConditionalOrExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeParameterModifier(@NotNull Java8Parser.TypeParameterModifierContext ctx) { 
	    // annotation
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeParameterModifier(@NotNull Java8Parser.TypeParameterModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFloatingPointType(@NotNull Java8Parser.FloatingPointTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitFloatingPointType(@NotNull Java8Parser.FloatingPointTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterDimExpr(@NotNull Java8Parser.DimExprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitDimExpr(@NotNull Java8Parser.DimExprContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannInterfaceType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannInterfaceType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterResource(@NotNull Java8Parser.ResourceContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitResource(@NotNull Java8Parser.ResourceContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInclusiveOrExpression(@NotNull Java8Parser.InclusiveOrExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInclusiveOrExpression(@NotNull Java8Parser.InclusiveOrExpressionContext ctx) { }
	/**
        interfaceMethodModifier
            :	annotation
            |	'public'
            |	'abstract'
            |	'default'
            |	'static'
            |	'strictfp'
            ;
	 */
	@Override public void enterInterfaceMethodModifier(@NotNull Java8Parser.InterfaceMethodModifierContext ctx) { 
        int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "public":
                modifier &= ModifierSet.PUBLIC;
                break;
            case "abstract":
                modifier &= ModifierSet.ABSTRACT;
                break;
            case "default":
                modifier &= ModifierSet.DEFAULT;
                break;
            case "static":
                modifier &= ModifierSet.STATIC;
                break;
            case "strictfp":
                modifier &= ModifierSet.STRICTFP;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
            }
        TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceMethodModifier(@NotNull Java8Parser.InterfaceMethodModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInterfaceTypeList(@NotNull Java8Parser.InterfaceTypeListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceTypeList(@NotNull Java8Parser.InterfaceTypeListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannInterfaceType(@NotNull Java8Parser.UnannInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannInterfaceType(@NotNull Java8Parser.UnannInterfaceTypeContext ctx) { }
	/**
        interfaceMethodModifier
            :	annotation
            |	'public'
            |	'abstract'
            |	'default'
            |	'static'
            |	'strictfp'
            ;
	 */
	@Override public void enterInterfaceModifier(@NotNull Java8Parser.InterfaceModifierContext ctx) {
        int modifier = 0;
        String text = ctx.getText();
        switch (text) {
            case "public":
                modifier &= ModifierSet.PUBLIC;
                break;
            case "abstract":
                modifier &= ModifierSet.ABSTRACT;
                break;
            case "static":
                modifier &= ModifierSet.STATIC;
                break;
            case "final":
                modifier &= ModifierSet.DEFAULT;
                break;
            case "strictfp":
                modifier &= ModifierSet.STRICTFP;
                break;
            default:
                if (text.startsWith("@")) {
                    modifier &= ModifierSet.ANNOTATION;   
                }
        }
        TigerNode parent = stack.peek();
        parent.setModifiers(modifier);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceModifier(@NotNull Java8Parser.InterfaceModifierContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterExclusiveOrExpression(@NotNull Java8Parser.ExclusiveOrExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitExclusiveOrExpression(@NotNull Java8Parser.ExclusiveOrExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassType(@NotNull Java8Parser.ClassTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassType(@NotNull Java8Parser.ClassTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPostIncrementExpression(@NotNull Java8Parser.PostIncrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPostIncrementExpression(@NotNull Java8Parser.PostIncrementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTryStatement(@NotNull Java8Parser.TryStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTryStatement(@NotNull Java8Parser.TryStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterElementValueList(@NotNull Java8Parser.ElementValueListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitElementValueList(@NotNull Java8Parser.ElementValueListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterWildcard(@NotNull Java8Parser.WildcardContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitWildcard(@NotNull Java8Parser.WildcardContext ctx) { }
	/**
	 * classDeclaration
	 * 	:	normalClassDeclaration
	 * 	|	enumDeclaration
	 *  ;
	 */	
	@Override public void enterClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx) { 
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx) { 
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnaryExpressionNotPlusMinus(@NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnaryExpressionNotPlusMinus(@NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterUnannReferenceType(@NotNull Java8Parser.UnannReferenceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitUnannReferenceType(@NotNull Java8Parser.UnannReferenceTypeContext ctx) { }
	/**
	 * methodHeader
	 * 	:	result methodDeclarator throws_?
	 * 	|	typeParameters annotation* result methodDeclarator throws_?
	 *  ;
	 */	
	@Override public void enterMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) { 
	    TigerNode m = stack.peek();
	    m.setName(ctx.getText());
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPackageName(@NotNull Java8Parser.PackageNameContext ctx) { 
	    TigerNode node = stack.peek();
	    node.setName(ctx.getText());
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPackageName(@NotNull Java8Parser.PackageNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx) {
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx) {
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterClassType_lf_classOrInterfaceType(@NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitClassType_lf_classOrInterfaceType(@NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPackageOrTypeName(@NotNull Java8Parser.PackageOrTypeNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPackageOrTypeName(@NotNull Java8Parser.PackageOrTypeNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAssignment(@NotNull Java8Parser.AssignmentContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAssignment(@NotNull Java8Parser.AssignmentContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodName(@NotNull Java8Parser.MethodNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodName(@NotNull Java8Parser.MethodNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterBreakStatement(@NotNull Java8Parser.BreakStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitBreakStatement(@NotNull Java8Parser.BreakStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStatementExpressionList(@NotNull Java8Parser.StatementExpressionListContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStatementExpressionList(@NotNull Java8Parser.StatementExpressionListContext ctx) { }
	/**
        interfaceMethodDeclaration
            :	interfaceMethodModifier* methodHeader methodBody
            ;
	 */
	@Override public void enterInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx) { 
	    MethodNode mn = new MethodNode();
	    mn.setStartLocation(getStartLocation(ctx));
	    mn.setEndLocation(getEndLocation(ctx));
	    stack.push(mn);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx) { 
	    TigerNode node = stack.pop();
	    TigerNode parent = stack.peek();
	    parent.addChild(node);
	}
	/**
	 * throws_
	 * 	:	'throws' exceptionTypeList
	 * 	;
	 */
	@Override public void enterThrows_(@NotNull Java8Parser.Throws_Context ctx) {
	}
	@Override public void exitThrows_(@NotNull Java8Parser.Throws_Context ctx) { 
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterMethodReference(@NotNull Java8Parser.MethodReferenceContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitMethodReference(@NotNull Java8Parser.MethodReferenceContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterPrimaryNoNewArray(@NotNull Java8Parser.PrimaryNoNewArrayContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitPrimaryNoNewArray(@NotNull Java8Parser.PrimaryNoNewArrayContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterNumericType(@NotNull Java8Parser.NumericTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitNumericType(@NotNull Java8Parser.NumericTypeContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterContinueStatement(@NotNull Java8Parser.ContinueStatementContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitContinueStatement(@NotNull Java8Parser.ContinueStatementContext ctx) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEveryRule(@NotNull ParserRuleContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEveryRule(@NotNull ParserRuleContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void visitTerminal(@NotNull TerminalNode node) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
    @Override public void visitErrorNode(@NotNull ErrorNode node) { 
        errors.add(node);
    }
    
    private void dumpErrors() {
        for (ErrorNode error : errors) {
            System.out.println("+++++ " + error);   
        }
    }
     
    // return a Location representing the start of the rule context
    private Location getStartLocation(ParserRuleContext ctx) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new Location(line, col);
    }
    
    // return a Location representing the end of the rule context
    private Location getEndLocation(ParserRuleContext ctx) {
        int line = ctx.getStop().getLine();
        int col = ctx.getStop().getCharPositionInLine();
        return new Location(line, col);
    }
    
    // return the accumulation of item counts
    public Results getResults() {
        return results;   
    }
	
}