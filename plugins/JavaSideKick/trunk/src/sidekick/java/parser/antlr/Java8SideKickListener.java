
package sidekick.java.parser.antlr;


import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.java.node.*;
import sidekick.util.Location;


public class Java8SideKickListener extends Java8BaseListener {

    Deque<TigerNode> stack = new ArrayDeque<TigerNode>();
    private CUNode cuNode;
    private Results results;
    private List<sidekick.java.node.ErrorNode> exceptions = new ArrayList<sidekick.java.node.ErrorNode>();


    public CUNode getCompilationUnit() {
        return cuNode;
    }


    public List<sidekick.java.node.ErrorNode> getErrors() {
        return exceptions;
    }


    private void addException( ParserException pe ) {
        sidekick.java.node.ErrorNode en = new sidekick.java.node.ErrorNode( pe );
        exceptions.add( en );
    }


    /**
     * @return the accumulated counts of classes, interfaces, methods, and fields.
     */
    public Results getResults() {
        return results;
    }


    // return a Location representing the start of the rule context
    private Location getStartLocation( ParserRuleContext ctx ) {
        int line = ctx.getStart().getLine();
        int col = ctx.getStart().getCharPositionInLine();
        return new Location( line, col );
    }


    // return a Location representing the end of the rule context
    private Location getEndLocation( ParserRuleContext ctx ) {
        int line = ctx.getStop().getLine();
        int col = ctx.getStop().getCharPositionInLine();
        return new Location( line, col );
    }


    private void setStartLocation( TigerNode tn, ParserRuleContext ctx ) {
        tn.setStartLocation( getStartLocation( ctx ) );
    }


    private void setEndLocation( TigerNode tn, ParserRuleContext ctx ) {
        tn.setEndLocation( getEndLocation( ctx ) );
    }


    private void setStartLocation( TigerNode tn, Token token ) {
        tn.setStartLocation( new Location( token.getLine(), token.getCharPositionInLine() ) );
    }


    private void setEndLocation( TigerNode tn, Token token ) {
        tn.setEndLocation( new Location( token.getLine(), token.getCharPositionInLine() + token.getText().length() ) );
    }


    @Override
    public void exitLiteral( @NotNull Java8Parser.LiteralContext ctx ) {
    }


    @Override
    public void exitType( @NotNull Java8Parser.TypeContext ctx ) {
    }


    @Override
    public void exitPrimitiveType( @NotNull Java8Parser.PrimitiveTypeContext ctx ) {
    }


    @Override
    public void exitNumericType( @NotNull Java8Parser.NumericTypeContext ctx ) {
    }


    @Override
    public void exitIntegralType( @NotNull Java8Parser.IntegralTypeContext ctx ) {
    }


    @Override
    public void exitFloatingPointType( @NotNull Java8Parser.FloatingPointTypeContext ctx ) {
    }


    @Override
    public void exitReferenceType( @NotNull Java8Parser.ReferenceTypeContext ctx ) {
    }


    @Override
    public void exitClassOrInterfaceType( @NotNull Java8Parser.ClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitClassType( @NotNull Java8Parser.ClassTypeContext ctx ) {
    }


    @Override
    public void exitClassType_lf_classOrInterfaceType( @NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitClassType_lfno_classOrInterfaceType( @NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitInterfaceType( @NotNull Java8Parser.InterfaceTypeContext ctx ) {
    }


    @Override
    public void exitInterfaceType_lf_classOrInterfaceType( @NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitInterfaceType_lfno_classOrInterfaceType( @NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitTypeVariable( @NotNull Java8Parser.TypeVariableContext ctx ) {
    }


    @Override
    public void exitArrayType( @NotNull Java8Parser.ArrayTypeContext ctx ) {
    }


    @Override
    public void exitDims( @NotNull Java8Parser.DimsContext ctx ) {
    }


    @Override
    public void exitTypeParameter( @NotNull Java8Parser.TypeParameterContext ctx ) {
    }


    @Override
    public void exitTypeParameterModifier( @NotNull Java8Parser.TypeParameterModifierContext ctx ) {
    }


    @Override
    public void exitTypeBound( @NotNull Java8Parser.TypeBoundContext ctx ) {
    }


    @Override
    public void exitAdditionalBound( @NotNull Java8Parser.AdditionalBoundContext ctx ) {
    }


    @Override
    public void exitTypeArguments( @NotNull Java8Parser.TypeArgumentsContext ctx ) {
    }


    @Override
    public void exitTypeArgumentList( @NotNull Java8Parser.TypeArgumentListContext ctx ) {
    }


    @Override
    public void exitTypeArgument( @NotNull Java8Parser.TypeArgumentContext ctx ) {
    }


    @Override
    public void exitWildcard( @NotNull Java8Parser.WildcardContext ctx ) {
    }


    @Override
    public void exitWildcardBounds( @NotNull Java8Parser.WildcardBoundsContext ctx ) {
    }


    @Override
    public void exitPackageName( @NotNull Java8Parser.PackageNameContext ctx ) {
    }


    @Override
    public void exitTypeName( @NotNull Java8Parser.TypeNameContext ctx ) {
    }


    @Override
    public void exitPackageOrTypeName( @NotNull Java8Parser.PackageOrTypeNameContext ctx ) {
    }


    @Override
    public void exitExpressionName( @NotNull Java8Parser.ExpressionNameContext ctx ) {
    }


    @Override
    public void exitMethodName( @NotNull Java8Parser.MethodNameContext ctx ) {
    }


    @Override
    public void exitAmbiguousName( @NotNull Java8Parser.AmbiguousNameContext ctx ) {
    }


    @Override
    public void exitCompilationUnit( @NotNull Java8Parser.CompilationUnitContext ctx ) {
        try {
            System.out.println( "+++++ exitCompilation start" );
            System.out.println( "+++++ stack.size = " + stack.size() );
            cuNode = new CUNode();
            setStartLocation( cuNode, ctx );
            setEndLocation( cuNode, ctx );
            if ( ctx.typeDeclaration() != null ) {
                System.out.println( "+++++ # of type declarations = " + ctx.typeDeclaration().size() );
                for ( int i = 0; i < ctx.typeDeclaration().size(); i++ ) {
                    cuNode.addChild( stack.pop() );
                }
            }


            if ( ctx.importDeclaration() != null ) {
                ImportNode importNode = new ImportNode( "Imports" );
                System.out.println( "+++++ # of import declarations = " + ctx.importDeclaration().size() );
                for ( int i = 0; i < ctx.importDeclaration().size(); i++ ) {
                    importNode.addChild( stack.pop() );
                }
                cuNode.setImportNode( importNode );
            }


            if ( ctx.packageDeclaration() != null ) {
                cuNode.setPackage( stack.pop() );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        System.out.println( "+++++ exitCompilationUnit end" );
        System.out.println( "+++++ dump:\n" + cuNode.dump() );
    }


    @Override
    public void exitPackageDeclaration( @NotNull Java8Parser.PackageDeclarationContext ctx ) {
        TigerNode parent = new TigerNode();
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < ctx.Identifier().size(); i++ ) {
                sb.append( ctx.Identifier( i ).getSymbol().getText() );
                if ( i < ctx.Identifier().size() - 1 ) {
                    sb.append( '.' );
                }
            }
            parent.setName( sb.toString() );
        }


        // packageModifier is 0 or 1 annotation only
        if ( ctx.packageModifier() != null ) {
            for ( int i = 0; i < ctx.packageModifier().size(); i++ ) {
                parent.addAnnotation( ( AnnotationNode )stack.pop() );
            }
        }


        stack.push( parent );
        System.out.println( "+++++ exit packageDeclaration: " + parent.getName() );
    }


    @Override
    public void exitPackageModifier( @NotNull Java8Parser.PackageModifierContext ctx ) {
    }


    @Override
    public void exitImportDeclaration( @NotNull Java8Parser.ImportDeclarationContext ctx ) {
        TigerNode parent = new ImportNode( ctx.getText().substring( "import".length() ) );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        stack.push( parent );
        System.out.println( "+++++ exitImportDeclaration" );
    }


    @Override
    public void exitSingleTypeImportDeclaration( @NotNull Java8Parser.SingleTypeImportDeclarationContext ctx ) {
    }


    @Override
    public void exitTypeImportOnDemandDeclaration( @NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx ) {
    }


    @Override
    public void exitSingleStaticImportDeclaration( @NotNull Java8Parser.SingleStaticImportDeclarationContext ctx ) {
    }


    @Override
    public void exitStaticImportOnDemandDeclaration( @NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx ) {
    }


    @Override
    public void exitTypeDeclaration( @NotNull Java8Parser.TypeDeclarationContext ctx ) {
    }


    @Override
    public void exitClassDeclaration( @NotNull Java8Parser.ClassDeclarationContext ctx ) {
    }


    /**
     * normalClassDeclaration
     * 	:	classModifiers 'class' Identifier typeParameters? superclass? superinterfaces? classBody
     * 	;
     */
    @Override
    public void exitNormalClassDeclaration( @NotNull Java8Parser.NormalClassDeclarationContext ctx ) {
        System.out.println( "+++++ normal class declaration" );
        TigerNode parent = new ClassNode( ctx.Identifier().getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        stack.push( parent );

        Java8Parser.ClassBodyContext classBodyContext = ctx.classBody();
        List<Java8Parser.ClassBodyDeclarationContext> declarations = ( List <Java8Parser.ClassBodyDeclarationContext> )classBodyContext.classBodyDeclaration();
        for ( Java8Parser.ClassBodyDeclarationContext declaration : declarations ) {
            if ( declaration.classMemberDeclaration() != null ) {
                Java8Parser.ClassMemberDeclarationContext dctx = declaration.classMemberDeclaration();
                if ( dctx.fieldDeclaration() != null ) {
                    Java8Parser.FieldDeclarationContext fieldCtx = dctx.fieldDeclaration();
                    
                    // type
                    Type type = new Type(fieldCtx.unannType().getText());
                    
                    // modifiers
                    int size = fieldCtx.fieldModifiers().fieldModifier().size();
                    String[] modifierNames = new String[size];
                    for (int i = 0; i < size; i++) {
                        modifierNames[i] = fieldCtx.fieldModifiers().fieldModifier(i).getText();  
                    }
                    int modifiers = ModifierSet.getModifiers(modifierNames);
                    
                    // variable declarations
                    size = fieldCtx.variableDeclaratorList().variableDeclarator().size();
                    for (int i = 0; i < size; i++) {
                        Java8Parser.VariableDeclaratorContext vdc = fieldCtx.variableDeclaratorList().variableDeclarator(i);
                        TigerNode tn = new FieldNode( vdc.variableDeclaratorId().getText(), modifiers, type );
                        setStartLocation( tn, vdc );
                        setEndLocation( tn, vdc );
                        parent.addChild( tn );
                    }
                }
                else if ( dctx.methodDeclaration() != null ) {
                    TigerNode tn = new MethodNode();
                    tn.setName( dctx.methodDeclaration().getText() );
                    setStartLocation( tn, dctx );
                    setEndLocation( tn, dctx );
                    parent.addChild( tn );
                }
                else if ( dctx.classDeclaration() != null ) {
                    TigerNode tn = new ClassNode( dctx.classDeclaration().getText() );
                    setStartLocation( tn, dctx );
                    setEndLocation( tn, dctx );
                    parent.addChild( tn );
                }
                else if ( dctx.interfaceDeclaration() != null ) {
                    TigerNode tn = new InterfaceNode( dctx.interfaceDeclaration().getText() );
                    setStartLocation( tn, dctx );
                    setEndLocation( tn, dctx );
                    parent.addChild( tn );
                }
            }
        }
    }


    @Override
    public void exitClassModifiers( @NotNull Java8Parser.ClassModifiersContext ctx ) {
    }


    @Override
    public void exitClassModifier( @NotNull Java8Parser.ClassModifierContext ctx ) {
    }


    @Override
    public void exitTypeParameters( @NotNull Java8Parser.TypeParametersContext ctx ) {
    }


    @Override
    public void exitTypeParameterList( @NotNull Java8Parser.TypeParameterListContext ctx ) {
    }


    @Override
    public void exitSuperclass( @NotNull Java8Parser.SuperclassContext ctx ) {
    }


    @Override
    public void exitSuperinterfaces( @NotNull Java8Parser.SuperinterfacesContext ctx ) {
    }


    @Override
    public void exitInterfaceTypeList( @NotNull Java8Parser.InterfaceTypeListContext ctx ) {
    }


    @Override
    public void exitClassBody( @NotNull Java8Parser.ClassBodyContext ctx ) {
    }


    @Override
    public void exitClassBodyDeclaration( @NotNull Java8Parser.ClassBodyDeclarationContext ctx ) {
    }


    @Override
    public void exitClassMemberDeclaration( @NotNull Java8Parser.ClassMemberDeclarationContext ctx ) {
    }


    @Override
    public void exitFieldDeclaration( @NotNull Java8Parser.FieldDeclarationContext ctx ) {
    }


    @Override
    public void exitFieldModifiers( @NotNull Java8Parser.FieldModifiersContext ctx ) {
    }


    @Override
    public void exitFieldModifier( @NotNull Java8Parser.FieldModifierContext ctx ) {
    }


    @Override
    public void exitVariableDeclaratorList( @NotNull Java8Parser.VariableDeclaratorListContext ctx ) {
    }


    @Override
    public void exitVariableDeclarator( @NotNull Java8Parser.VariableDeclaratorContext ctx ) {
    }


    @Override
    public void exitVariableDeclaratorId( @NotNull Java8Parser.VariableDeclaratorIdContext ctx ) {
    }


    @Override
    public void exitVariableInitializer( @NotNull Java8Parser.VariableInitializerContext ctx ) {
    }


    @Override
    public void exitUnannType( @NotNull Java8Parser.UnannTypeContext ctx ) {
    }


    @Override
    public void exitUnannPrimitiveType( @NotNull Java8Parser.UnannPrimitiveTypeContext ctx ) {
    }


    @Override
    public void exitUnannReferenceType( @NotNull Java8Parser.UnannReferenceTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassOrInterfaceType( @NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassType( @NotNull Java8Parser.UnannClassTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassType_lf_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassType_lfno_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannInterfaceType( @NotNull Java8Parser.UnannInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannInterfaceType_lf_unannClassOrInterfaceType( @NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannInterfaceType_lfno_unannClassOrInterfaceType( @NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannTypeVariable( @NotNull Java8Parser.UnannTypeVariableContext ctx ) {
    }


    @Override
    public void exitUnannArrayType( @NotNull Java8Parser.UnannArrayTypeContext ctx ) {
    }


    @Override
    public void exitMethodDeclaration( @NotNull Java8Parser.MethodDeclarationContext ctx ) {
    }


    @Override
    public void exitMethodModifiers( @NotNull Java8Parser.MethodModifiersContext ctx ) {
    }


    @Override
    public void exitMethodModifier( @NotNull Java8Parser.MethodModifierContext ctx ) {
    }


    @Override
    public void exitMethodHeader( @NotNull Java8Parser.MethodHeaderContext ctx ) {
    }


    @Override
    public void exitResult( @NotNull Java8Parser.ResultContext ctx ) {
    }


    @Override
    public void exitMethodDeclarator( @NotNull Java8Parser.MethodDeclaratorContext ctx ) {
    }


    @Override
    public void exitFormalParameterList( @NotNull Java8Parser.FormalParameterListContext ctx ) {
    }


    @Override
    public void exitFormalParameters( @NotNull Java8Parser.FormalParametersContext ctx ) {
    }


    @Override
    public void exitFormalParameter( @NotNull Java8Parser.FormalParameterContext ctx ) {
    }


    @Override
    public void exitVariableModifier( @NotNull Java8Parser.VariableModifierContext ctx ) {
    }


    @Override
    public void exitLastFormalParameter( @NotNull Java8Parser.LastFormalParameterContext ctx ) {
    }


    @Override
    public void exitReceiverParameter( @NotNull Java8Parser.ReceiverParameterContext ctx ) {
    }


    @Override
    public void exitThrows_( @NotNull Java8Parser.Throws_Context ctx ) {
    }


    @Override
    public void exitExceptionTypeList( @NotNull Java8Parser.ExceptionTypeListContext ctx ) {
    }


    @Override
    public void exitExceptionType( @NotNull Java8Parser.ExceptionTypeContext ctx ) {
    }


    @Override
    public void exitMethodBody( @NotNull Java8Parser.MethodBodyContext ctx ) {
    }


    @Override
    public void exitInstanceInitializer( @NotNull Java8Parser.InstanceInitializerContext ctx ) {
    }


    @Override
    public void exitStaticInitializer( @NotNull Java8Parser.StaticInitializerContext ctx ) {
    }


    @Override
    public void exitConstructorDeclaration( @NotNull Java8Parser.ConstructorDeclarationContext ctx ) {
    }


    @Override
    public void exitConstructorModifiers( @NotNull Java8Parser.ConstructorModifiersContext ctx ) {
    }


    @Override
    public void exitConstructorModifier( @NotNull Java8Parser.ConstructorModifierContext ctx ) {
    }


    @Override
    public void exitConstructorDeclarator( @NotNull Java8Parser.ConstructorDeclaratorContext ctx ) {
    }


    @Override
    public void exitSimpleTypeName( @NotNull Java8Parser.SimpleTypeNameContext ctx ) {
    }


    @Override
    public void exitConstructorBody( @NotNull Java8Parser.ConstructorBodyContext ctx ) {
    }


    @Override
    public void exitExplicitConstructorInvocation( @NotNull Java8Parser.ExplicitConstructorInvocationContext ctx ) {
    }


    @Override
    public void exitEnumDeclaration( @NotNull Java8Parser.EnumDeclarationContext ctx ) {
    }


    @Override
    public void exitEnumBody( @NotNull Java8Parser.EnumBodyContext ctx ) {
    }


    @Override
    public void exitEnumConstantList( @NotNull Java8Parser.EnumConstantListContext ctx ) {
    }


    @Override
    public void exitEnumConstant( @NotNull Java8Parser.EnumConstantContext ctx ) {
    }


    @Override
    public void exitEnumConstantModifier( @NotNull Java8Parser.EnumConstantModifierContext ctx ) {
    }


    @Override
    public void exitEnumBodyDeclarations( @NotNull Java8Parser.EnumBodyDeclarationsContext ctx ) {
    }


    @Override
    public void exitInterfaceDeclaration( @NotNull Java8Parser.InterfaceDeclarationContext ctx ) {
    }


    @Override
    public void exitNormalInterfaceDeclaration( @NotNull Java8Parser.NormalInterfaceDeclarationContext ctx ) {
    }


    @Override
    public void exitInterfaceModifiers( @NotNull Java8Parser.InterfaceModifiersContext ctx ) {
    }


    @Override
    public void exitInterfaceModifier( @NotNull Java8Parser.InterfaceModifierContext ctx ) {
    }


    @Override
    public void exitExtendsInterfaces( @NotNull Java8Parser.ExtendsInterfacesContext ctx ) {
    }


    @Override
    public void exitInterfaceBody( @NotNull Java8Parser.InterfaceBodyContext ctx ) {
    }


    @Override
    public void exitInterfaceMemberDeclaration( @NotNull Java8Parser.InterfaceMemberDeclarationContext ctx ) {
    }


    @Override
    public void exitConstantDeclaration( @NotNull Java8Parser.ConstantDeclarationContext ctx ) {
    }


    @Override
    public void exitConstantModifier( @NotNull Java8Parser.ConstantModifierContext ctx ) {
    }


    @Override
    public void exitInterfaceMethodDeclaration( @NotNull Java8Parser.InterfaceMethodDeclarationContext ctx ) {
    }


    @Override
    public void exitInterfaceMethodModifier( @NotNull Java8Parser.InterfaceMethodModifierContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeDeclaration( @NotNull Java8Parser.AnnotationTypeDeclarationContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeBody( @NotNull Java8Parser.AnnotationTypeBodyContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeMemberDeclaration( @NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeElementDeclaration( @NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeElementModifier( @NotNull Java8Parser.AnnotationTypeElementModifierContext ctx ) {
    }


    @Override
    public void exitDefaultValue( @NotNull Java8Parser.DefaultValueContext ctx ) {
    }


    @Override
    public void exitAnnotation( @NotNull Java8Parser.AnnotationContext ctx ) {
    }


    @Override
    public void exitAnnotationIdentifier( @NotNull Java8Parser.AnnotationIdentifierContext ctx ) {
    }


    @Override
    public void exitAnnotationDim( @NotNull Java8Parser.AnnotationDimContext ctx ) {
    }


    @Override
    public void exitNormalAnnotation( @NotNull Java8Parser.NormalAnnotationContext ctx ) {
    }


    @Override
    public void exitElementValuePairList( @NotNull Java8Parser.ElementValuePairListContext ctx ) {
    }


    @Override
    public void exitElementValuePair( @NotNull Java8Parser.ElementValuePairContext ctx ) {
    }


    @Override
    public void exitElementValue( @NotNull Java8Parser.ElementValueContext ctx ) {
    }


    @Override
    public void exitElementValueArrayInitializer( @NotNull Java8Parser.ElementValueArrayInitializerContext ctx ) {
    }


    @Override
    public void exitElementValueList( @NotNull Java8Parser.ElementValueListContext ctx ) {
    }


    @Override
    public void exitMarkerAnnotation( @NotNull Java8Parser.MarkerAnnotationContext ctx ) {
    }


    @Override
    public void exitSingleElementAnnotation( @NotNull Java8Parser.SingleElementAnnotationContext ctx ) {
    }


    @Override
    public void exitArrayInitializer( @NotNull Java8Parser.ArrayInitializerContext ctx ) {
    }


    @Override
    public void exitVariableInitializerList( @NotNull Java8Parser.VariableInitializerListContext ctx ) {
    }


    @Override
    public void exitBlock( @NotNull Java8Parser.BlockContext ctx ) {
    }


    @Override
    public void exitBlockStatements( @NotNull Java8Parser.BlockStatementsContext ctx ) {
    }


    @Override
    public void exitBlockStatement( @NotNull Java8Parser.BlockStatementContext ctx ) {
    }


    @Override
    public void exitLocalVariableDeclarationStatement( @NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx ) {
    }


    @Override
    public void exitLocalVariableDeclaration( @NotNull Java8Parser.LocalVariableDeclarationContext ctx ) {
    }


    @Override
    public void exitStatement( @NotNull Java8Parser.StatementContext ctx ) {
    }


    @Override
    public void exitStatementNoShortIf( @NotNull Java8Parser.StatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitStatementWithoutTrailingSubstatement( @NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx ) {
    }


    @Override
    public void exitEmptyStatement( @NotNull Java8Parser.EmptyStatementContext ctx ) {
    }


    @Override
    public void exitLabeledStatement( @NotNull Java8Parser.LabeledStatementContext ctx ) {
    }


    @Override
    public void exitLabeledStatementNoShortIf( @NotNull Java8Parser.LabeledStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitExpressionStatement( @NotNull Java8Parser.ExpressionStatementContext ctx ) {
    }


    @Override
    public void exitStatementExpression( @NotNull Java8Parser.StatementExpressionContext ctx ) {
    }


    @Override
    public void exitIfThenStatement( @NotNull Java8Parser.IfThenStatementContext ctx ) {
    }


    @Override
    public void exitIfThenElseStatement( @NotNull Java8Parser.IfThenElseStatementContext ctx ) {
    }


    @Override
    public void exitIfThenElseStatementNoShortIf( @NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitAssertStatement( @NotNull Java8Parser.AssertStatementContext ctx ) {
    }


    @Override
    public void exitSwitchStatement( @NotNull Java8Parser.SwitchStatementContext ctx ) {
    }


    @Override
    public void exitSwitchBlock( @NotNull Java8Parser.SwitchBlockContext ctx ) {
    }


    @Override
    public void exitSwitchBlockStatementGroup( @NotNull Java8Parser.SwitchBlockStatementGroupContext ctx ) {
    }


    @Override
    public void exitSwitchLabels( @NotNull Java8Parser.SwitchLabelsContext ctx ) {
    }


    @Override
    public void exitSwitchLabel( @NotNull Java8Parser.SwitchLabelContext ctx ) {
    }


    @Override
    public void exitEnumConstantName( @NotNull Java8Parser.EnumConstantNameContext ctx ) {
    }


    @Override
    public void exitWhileStatement( @NotNull Java8Parser.WhileStatementContext ctx ) {
    }


    @Override
    public void exitWhileStatementNoShortIf( @NotNull Java8Parser.WhileStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitDoStatement( @NotNull Java8Parser.DoStatementContext ctx ) {
    }


    @Override
    public void exitForStatement( @NotNull Java8Parser.ForStatementContext ctx ) {
    }


    @Override
    public void exitForStatementNoShortIf( @NotNull Java8Parser.ForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitBasicForStatement( @NotNull Java8Parser.BasicForStatementContext ctx ) {
    }


    @Override
    public void exitBasicForStatementNoShortIf( @NotNull Java8Parser.BasicForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitForInit( @NotNull Java8Parser.ForInitContext ctx ) {
    }


    @Override
    public void exitForUpdate( @NotNull Java8Parser.ForUpdateContext ctx ) {
    }


    @Override
    public void exitStatementExpressionList( @NotNull Java8Parser.StatementExpressionListContext ctx ) {
    }


    @Override
    public void exitEnhancedForStatement( @NotNull Java8Parser.EnhancedForStatementContext ctx ) {
    }


    @Override
    public void exitEnhancedForStatementNoShortIf( @NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitBreakStatement( @NotNull Java8Parser.BreakStatementContext ctx ) {
    }


    @Override
    public void exitContinueStatement( @NotNull Java8Parser.ContinueStatementContext ctx ) {
    }


    @Override
    public void exitReturnStatement( @NotNull Java8Parser.ReturnStatementContext ctx ) {
    }


    @Override
    public void exitThrowStatement( @NotNull Java8Parser.ThrowStatementContext ctx ) {
    }


    @Override
    public void exitSynchronizedStatement( @NotNull Java8Parser.SynchronizedStatementContext ctx ) {
    }


    @Override
    public void exitTryStatement( @NotNull Java8Parser.TryStatementContext ctx ) {
    }


    @Override
    public void exitCatches( @NotNull Java8Parser.CatchesContext ctx ) {
    }


    @Override
    public void exitCatchClause( @NotNull Java8Parser.CatchClauseContext ctx ) {
    }


    @Override
    public void exitCatchFormalParameter( @NotNull Java8Parser.CatchFormalParameterContext ctx ) {
    }


    @Override
    public void exitCatchType( @NotNull Java8Parser.CatchTypeContext ctx ) {
    }


    @Override
    public void exitFinally_( @NotNull Java8Parser.Finally_Context ctx ) {
    }


    @Override
    public void exitTryWithResourcesStatement( @NotNull Java8Parser.TryWithResourcesStatementContext ctx ) {
    }


    @Override
    public void exitResourceSpecification( @NotNull Java8Parser.ResourceSpecificationContext ctx ) {
    }


    @Override
    public void exitResourceList( @NotNull Java8Parser.ResourceListContext ctx ) {
    }


    @Override
    public void exitResource( @NotNull Java8Parser.ResourceContext ctx ) {
    }


    @Override
    public void exitPrimary( @NotNull Java8Parser.PrimaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray( @NotNull Java8Parser.PrimaryNoNewArrayContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_arrayAccess( @NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_arrayAccess( @NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_primary( @NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary( @NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary( @NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_primary( @NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary( @NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary( @NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitClassInstanceCreationExpression( @NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx ) {
    }


    @Override
    public void exitClassInstanceCreationExpression_lf_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx ) {
    }


    @Override
    public void exitClassInstanceCreationExpression_lfno_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitTypeArgumentsOrDiamond( @NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx ) {
    }


    @Override
    public void exitFieldAccess( @NotNull Java8Parser.FieldAccessContext ctx ) {
    }


    @Override
    public void exitFieldAccess_lf_primary( @NotNull Java8Parser.FieldAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitFieldAccess_lfno_primary( @NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitArrayAccess( @NotNull Java8Parser.ArrayAccessContext ctx ) {
    }


    @Override
    public void exitArrayAccess_lf_primary( @NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitArrayAccess_lfno_primary( @NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitMethodInvocation( @NotNull Java8Parser.MethodInvocationContext ctx ) {
    }


    @Override
    public void exitMethodInvocation_lf_primary( @NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx ) {
    }


    @Override
    public void exitMethodInvocation_lfno_primary( @NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitArgumentList( @NotNull Java8Parser.ArgumentListContext ctx ) {
    }


    @Override
    public void exitMethodReference( @NotNull Java8Parser.MethodReferenceContext ctx ) {
    }


    @Override
    public void exitMethodReference_lf_primary( @NotNull Java8Parser.MethodReference_lf_primaryContext ctx ) {
    }


    @Override
    public void exitMethodReference_lfno_primary( @NotNull Java8Parser.MethodReference_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitArrayCreationExpression( @NotNull Java8Parser.ArrayCreationExpressionContext ctx ) {
    }


    @Override
    public void exitDimExprs( @NotNull Java8Parser.DimExprsContext ctx ) {
    }


    @Override
    public void exitDimExpr( @NotNull Java8Parser.DimExprContext ctx ) {
    }


    @Override
    public void exitConstantExpression( @NotNull Java8Parser.ConstantExpressionContext ctx ) {
    }


    @Override
    public void exitExpression( @NotNull Java8Parser.ExpressionContext ctx ) {
    }


    @Override
    public void exitLambdaExpression( @NotNull Java8Parser.LambdaExpressionContext ctx ) {
    }


    @Override
    public void exitLambdaParameters( @NotNull Java8Parser.LambdaParametersContext ctx ) {
    }


    @Override
    public void exitInferredFormalParameterList( @NotNull Java8Parser.InferredFormalParameterListContext ctx ) {
    }


    @Override
    public void exitLambdaBody( @NotNull Java8Parser.LambdaBodyContext ctx ) {
    }


    @Override
    public void exitAssignmentExpression( @NotNull Java8Parser.AssignmentExpressionContext ctx ) {
    }


    @Override
    public void exitAssignment( @NotNull Java8Parser.AssignmentContext ctx ) {
    }


    @Override
    public void exitLeftHandSide( @NotNull Java8Parser.LeftHandSideContext ctx ) {
    }


    @Override
    public void exitAssignmentOperator( @NotNull Java8Parser.AssignmentOperatorContext ctx ) {
    }


    @Override
    public void exitAdditiveOperator( @NotNull Java8Parser.AdditiveOperatorContext ctx ) {
    }


    @Override
    public void exitRelationalOperator( @NotNull Java8Parser.RelationalOperatorContext ctx ) {
    }


    @Override
    public void exitMultiplicativeOperator( @NotNull Java8Parser.MultiplicativeOperatorContext ctx ) {
    }


    @Override
    public void exitSquareBrackets( @NotNull Java8Parser.SquareBracketsContext ctx ) {
    }


    @Override
    public void exitConditionalExpression( @NotNull Java8Parser.ConditionalExpressionContext ctx ) {
    }


    @Override
    public void exitConditionalOrExpression( @NotNull Java8Parser.ConditionalOrExpressionContext ctx ) {
    }


    @Override
    public void exitConditionalAndExpression( @NotNull Java8Parser.ConditionalAndExpressionContext ctx ) {
    }


    @Override
    public void exitInclusiveOrExpression( @NotNull Java8Parser.InclusiveOrExpressionContext ctx ) {
    }


    @Override
    public void exitExclusiveOrExpression( @NotNull Java8Parser.ExclusiveOrExpressionContext ctx ) {
    }


    @Override
    public void exitAndExpression( @NotNull Java8Parser.AndExpressionContext ctx ) {
    }


    @Override
    public void exitEqualityExpression( @NotNull Java8Parser.EqualityExpressionContext ctx ) {
    }


    @Override
    public void exitRelationalExpression( @NotNull Java8Parser.RelationalExpressionContext ctx ) {
    }


    @Override
    public void exitShiftExpression( @NotNull Java8Parser.ShiftExpressionContext ctx ) {
    }


    @Override
    public void exitShiftOperator( @NotNull Java8Parser.ShiftOperatorContext ctx ) {
    }


    @Override
    public void exitAdditiveExpression( @NotNull Java8Parser.AdditiveExpressionContext ctx ) {
    }


    @Override
    public void exitMultiplicativeExpression( @NotNull Java8Parser.MultiplicativeExpressionContext ctx ) {
    }


    @Override
    public void exitUnaryExpression( @NotNull Java8Parser.UnaryExpressionContext ctx ) {
    }


    @Override
    public void exitPreIncrementExpression( @NotNull Java8Parser.PreIncrementExpressionContext ctx ) {
    }


    @Override
    public void exitPreDecrementExpression( @NotNull Java8Parser.PreDecrementExpressionContext ctx ) {
    }


    @Override
    public void exitUnaryExpressionNotPlusMinus( @NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx ) {
    }


    @Override
    public void exitPostfixExpression( @NotNull Java8Parser.PostfixExpressionContext ctx ) {
    }


    @Override
    public void exitPostIncrementExpression( @NotNull Java8Parser.PostIncrementExpressionContext ctx ) {
    }


    @Override
    public void exitPostIncrementExpression_lf_postfixExpression( @NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx ) {
    }


    @Override
    public void exitPostDecrementExpression( @NotNull Java8Parser.PostDecrementExpressionContext ctx ) {
    }


    @Override
    public void exitPostDecrementExpression_lf_postfixExpression( @NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx ) {
    }


    @Override
    public void exitCastExpression( @NotNull Java8Parser.CastExpressionContext ctx ) {
    }


    @Override
    public void exitEveryRule( @NotNull ParserRuleContext ctx ) {
    }


    @Override
    public void visitTerminal( @NotNull TerminalNode node ) {
    }


    @Override
    public void visitErrorNode( @NotNull ErrorNode node ) {
    }
}

