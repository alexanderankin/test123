
package sidekick.java.parser.antlr;


import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.java.node.*;
import static sidekick.java.parser.antlr.Java8Parser.*;
import sidekick.util.Location;


public class Java8SideKickListener extends Java8BaseListener {

    Deque<TigerNode> stack = new ArrayDeque<TigerNode>();
    private CUNode cuNode;
    private Results results = new Results();


    public CUNode getCompilationUnit() {
        return cuNode;
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
        int col = ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length();
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


    private void setLocations( TigerNode tn, ParserRuleContext ctx ) {
        setStartLocation( tn, ctx );
        setEndLocation( tn, ctx );
    }


    @Override
    public void exitLiteral( @NotNull LiteralContext ctx ) {
    }


    @Override
    public void exitType( @NotNull TypeContext ctx ) {
    }


    @Override
    public void exitPrimitiveType( @NotNull PrimitiveTypeContext ctx ) {
    }


    @Override
    public void exitNumericType( @NotNull NumericTypeContext ctx ) {
    }


    @Override
    public void exitIntegralType( @NotNull IntegralTypeContext ctx ) {
    }


    @Override
    public void exitFloatingPointType( @NotNull FloatingPointTypeContext ctx ) {
    }


    @Override
    public void exitReferenceType( @NotNull ReferenceTypeContext ctx ) {
    }


    @Override
    public void exitClassOrInterfaceType( @NotNull ClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitClassType( @NotNull ClassTypeContext ctx ) {
    }


    @Override
    public void exitClassType_lf_classOrInterfaceType( @NotNull ClassType_lf_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitClassType_lfno_classOrInterfaceType( @NotNull ClassType_lfno_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitInterfaceType( @NotNull InterfaceTypeContext ctx ) {
    }


    @Override
    public void exitInterfaceType_lf_classOrInterfaceType( @NotNull InterfaceType_lf_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitInterfaceType_lfno_classOrInterfaceType( @NotNull InterfaceType_lfno_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitTypeVariable( @NotNull TypeVariableContext ctx ) {
    }


    @Override
    public void exitArrayType( @NotNull ArrayTypeContext ctx ) {
    }


    @Override
    public void exitDims( @NotNull DimsContext ctx ) {
    }


    @Override
    public void exitTypeParameter( @NotNull TypeParameterContext ctx ) {
    }


    @Override
    public void exitTypeParameterModifier( @NotNull TypeParameterModifierContext ctx ) {
    }


    @Override
    public void exitTypeBound( @NotNull TypeBoundContext ctx ) {
    }


    @Override
    public void exitAdditionalBound( @NotNull AdditionalBoundContext ctx ) {
    }


    @Override
    public void exitTypeArguments( @NotNull TypeArgumentsContext ctx ) {
    }


    @Override
    public void exitTypeArgumentList( @NotNull TypeArgumentListContext ctx ) {
    }


    @Override
    public void exitTypeArgument( @NotNull TypeArgumentContext ctx ) {
    }


    @Override
    public void exitWildcard( @NotNull WildcardContext ctx ) {
    }


    @Override
    public void exitWildcardBounds( @NotNull WildcardBoundsContext ctx ) {
    }


    @Override
    public void exitPackageName( @NotNull PackageNameContext ctx ) {
    }


    @Override
    public void exitTypeName( @NotNull TypeNameContext ctx ) {
    }


    @Override
    public void exitPackageOrTypeName( @NotNull PackageOrTypeNameContext ctx ) {
    }


    @Override
    public void exitExpressionName( @NotNull ExpressionNameContext ctx ) {
    }


    @Override
    public void exitMethodName( @NotNull MethodNameContext ctx ) {
    }


    @Override
    public void exitAmbiguousName( @NotNull AmbiguousNameContext ctx ) {
    }


    @Override
    public void exitCompilationUnit( @NotNull CompilationUnitContext ctx ) {
        try {
            cuNode = new CUNode();
            setLocations( cuNode, ctx );

            if ( ctx.typeDeclaration() != null ) {
                for ( int i = 0; i < ctx.typeDeclaration().size(); i++ ) {
                    cuNode.addChild( stack.pop() );
                }
            }


            if ( ctx.importDeclaration() != null ) {
                ImportNode importNode = new ImportNode( "Imports" );
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
    }


    @Override
    public void exitPackageDeclaration( @NotNull PackageDeclarationContext ctx ) {
        TigerNode parent = new TigerNode();
        setLocations( parent, ctx );

        if ( ctx.identifier() != null ) {
            StringBuilder sb = new StringBuilder();
            for ( int i = 0; i < ctx.identifier().size(); i++ ) {
                sb.append( ctx.identifier( i ).Identifier().getSymbol().getText() );
                if ( i < ctx.identifier().size() - 1 ) {
                    sb.append( '.' );
                }
            }
            parent.setName( sb.toString() );
        }


        // packageModifier is 0 or 1 annotation only
        if ( ctx.packageModifier() != null ) {
            for ( int i = 0; i < ctx.packageModifier().size() && stack.size() > 0; i++ ) {
                parent.addAnnotation( ( AnnotationNode )stack.pop() );
            }
        }


        stack.push( parent );
    }


    @Override
    public void exitPackageModifier( @NotNull PackageModifierContext ctx ) {
    }


    @Override
    public void exitImportDeclaration( @NotNull ImportDeclarationContext ctx ) {
        TigerNode parent = new ImportNode( ctx.getText().substring( "import".length() ) );
        setLocations( parent, ctx );

        stack.push( parent );
    }


    @Override
    public void exitSingleTypeImportDeclaration( @NotNull SingleTypeImportDeclarationContext ctx ) {
    }


    @Override
    public void exitTypeImportOnDemandDeclaration( @NotNull TypeImportOnDemandDeclarationContext ctx ) {
    }


    @Override
    public void exitSingleStaticImportDeclaration( @NotNull SingleStaticImportDeclarationContext ctx ) {
    }


    @Override
    public void exitStaticImportOnDemandDeclaration( @NotNull StaticImportOnDemandDeclarationContext ctx ) {
    }


    @Override
    public void exitTypeDeclaration( @NotNull TypeDeclarationContext ctx ) {
    }


	@Override public void exitModuleDeclaration(@NotNull Java8Parser.ModuleDeclarationContext ctx) { }
	@Override public void exitModuleStatement(@NotNull Java8Parser.ModuleStatementContext ctx) { }
	@Override public void exitRequiresModifier(@NotNull Java8Parser.RequiresModifierContext ctx) { }
	@Override public void enterModuleName(@NotNull Java8Parser.ModuleNameContext ctx) { }
	@Override public void exitModuleName(@NotNull Java8Parser.ModuleNameContext ctx) { }
	
    @Override
    public void exitClassDeclaration( @NotNull ClassDeclarationContext ctx ) {
    }


    /**
     * normalClassDeclaration
     * 	:	classModifiers 'class' Identifier typeParameters? superclass? superinterfaces? classBody
     * 	;
     */
    @Override
    public void exitNormalClassDeclaration( @NotNull NormalClassDeclarationContext ctx ) {
        ClassNode parent = new ClassNode( ctx.identifier().getText() );
        setLocations( parent, ctx );

        // modifiers
        int size = ctx.classModifiers().classModifier().size();
        String[] modifierNames = new String [size];
        for ( int i = 0; i < size; i++ ) {
            modifierNames[i] = ctx.classModifiers().classModifier( i ).getText();
        }
        int modifiers = ModifierSet.getModifiers( modifierNames );
        parent.setModifiers( modifiers );

        // superclasses
        if ( ctx.superclass() != null ) {
            SuperclassContext superClassContext = ctx.superclass();
            ClassTypeContext classTypeContext = superClassContext.classType();
            Type superClassType = new Type( classTypeContext.getText() );
            setLocations( superClassType, classTypeContext );
            List<Type> superTypes = new ArrayList<Type>();
            superTypes.add( superClassType );
            parent.setExtendsList( superTypes );
        }


        // superinterfaces
        if ( ctx.superinterfaces() != null ) {
            SuperinterfacesContext superInterfacesContext = ctx.superinterfaces();
            InterfaceTypeListContext interfaceTypeListContext = superInterfacesContext.interfaceTypeList();
            List<InterfaceTypeContext> interfaceTypeContexts = ( List <InterfaceTypeContext> )interfaceTypeListContext.interfaceType();
            List<Type> interfaceTypes = new ArrayList<Type>();
            for ( InterfaceTypeContext itc : interfaceTypeContexts ) {
                Type type = new Type( itc.getText() );
                setLocations( type, itc );
                interfaceTypes.add( type );
            }
            parent.setImplementsList( interfaceTypes );
        }


        // add the children of this class. Only need the fields and methods here,
        // the inner classes and interface declarations are handled elsewhere
        ClassBodyContext classBodyContext = ctx.classBody();
        List<ClassBodyDeclarationContext> declarations = ( List <ClassBodyDeclarationContext> )classBodyContext.classBodyDeclaration();
        for ( ClassBodyDeclarationContext declaration : declarations ) {
            if ( declaration.classMemberDeclaration() != null ) {
                ClassMemberDeclarationContext dctx = declaration.classMemberDeclaration();
                if ( dctx.fieldDeclaration() != null ) {
                    processFieldDeclaration( parent, dctx );
                }


                if ( dctx.methodDeclaration() != null ) {
                    processMethodDeclaration( parent, dctx.methodDeclaration() );
                }


                if ( dctx.classDeclaration() != null ) {
                    parent.addChild( stack.pop() );
                }


                if ( dctx.interfaceDeclaration() != null ) {
                    parent.addChild( stack.pop() );
                }
            }
            else if ( declaration.constructorDeclaration() != null ) {
                processConstructorDeclaration( parent, declaration.constructorDeclaration() );
            }
        }
        results.incClassCount();
        stack.push( parent );
    }


    private void processConstructorDeclaration( TigerNode parent, ConstructorDeclarationContext ctx ) {
        ConstructorNode constructorNode = new ConstructorNode();

        setLocations( constructorNode, ctx );

        // modifiers
        int modifiers = 0;
        ConstructorModifiersContext constructorModifierContext = ctx.constructorModifiers();
        int size = constructorModifierContext.constructorModifier().size();
        String[] modifierNames = new String [size];
        for ( int i = 0; i < size; i++ ) {
            modifierNames[i] = constructorModifierContext.constructorModifier( i ).getText();
        }
        modifiers = ModifierSet.getModifiers( modifierNames );
        constructorNode.setModifiers( modifiers );

        ConstructorDeclaratorContext constructorDeclaratorContext = ctx.constructorDeclarator();

        // constructor name
        String name = constructorDeclaratorContext.simpleTypeName().getText();
        constructorNode.setName( name );

        // type parameters
        if ( constructorDeclaratorContext.typeParameters() != null ) {
            List<TypeParameterContext> typeParameterContexts = ( List <TypeParameterContext> )constructorDeclaratorContext.typeParameters().typeParameterList().typeParameter();
            StringBuilder sb = new StringBuilder( "<" );
            for ( TypeParameterContext typeParam : typeParameterContexts ) {
                sb.append( typeParam.identifier().getText() ).append( ',' );
            }
            if ( sb.length() > 1 ) {
                sb.deleteCharAt( sb.length() - 1 );
            }


            constructorNode.setTypeParams( sb.toString() + ">" );
        }


        // parameters
        // TODO: receiver parameter
        FormalParameterListContext formalParameterListContext = constructorDeclaratorContext.formalParameterList();
        if ( formalParameterListContext != null ) {
            FormalParametersContext formalParametersContext = formalParameterListContext.formalParameters();

            if ( formalParametersContext != null ) {

                List<FormalParameterContext> params = ( List <FormalParameterContext> )formalParametersContext.formalParameter();
                if ( params != null ) {
                    for ( FormalParameterContext param : params ) {
                        Parameter parameterNode = new Parameter();
                        setLocations( parameterNode, param );
                        Type type = new Type( param.unannType().getText() );
                        parameterNode.setType( type );
                        parameterNode.setName( param.variableDeclaratorId().identifier().getText() );

                        // modifiers
                        size = param.variableModifier().size();
                        modifierNames = new String [size] ;
                        for ( int i = 0; i < size; i++ ) {
                            modifierNames[i] = param.variableModifier( i ).getText();
                        }
                        modifiers = ModifierSet.getModifiers( modifierNames );
                        parameterNode.setModifiers( modifiers );
                        constructorNode.addFormalParameter( parameterNode );
                    }
                }
            }
        }


        // last formal parameter
        if ( formalParameterListContext != null ) {
            LastFormalParameterContext lastFormalParameterContext = formalParameterListContext.lastFormalParameter();
            if ( lastFormalParameterContext != null ) {
                Parameter parameterNode = new Parameter();
                setLocations( parameterNode, lastFormalParameterContext );
                if ( lastFormalParameterContext.formalParameter() != null ) {
                    FormalParameterContext param = lastFormalParameterContext.formalParameter();
                    Type type = new Type( param.unannType().getText() );
                    parameterNode.setType( type );
                    parameterNode.setName( param.variableDeclaratorId().identifier().getText() );

                    // modifiers
                    size = param.variableModifier().size();
                    modifierNames = new String [size] ;
                    for ( int i = 0; i < size; i++ ) {
                        modifierNames[i] = param.variableModifier( i ).getText();
                    }
                    modifiers = ModifierSet.getModifiers( modifierNames );
                    parameterNode.setModifiers( modifiers );
                }
                else {
                    if ( lastFormalParameterContext.unannType() != null ) {
                        Type type = new Type( lastFormalParameterContext.unannType().getText() );
                        parameterNode.setType( type );
                    }


                    if ( lastFormalParameterContext.variableDeclaratorId() != null ) {
                        parameterNode.setName( lastFormalParameterContext.variableDeclaratorId().identifier().getText() );
                    }


                    if ( lastFormalParameterContext.variableModifier() != null ) {
                        size = lastFormalParameterContext.variableModifier().size();
                        modifierNames = new String [size] ;
                        for ( int i = 0; i < size; i++ ) {
                            modifierNames[i] = lastFormalParameterContext.variableModifier( i ).getText();
                        }
                        modifiers = ModifierSet.getModifiers( modifierNames );
                        parameterNode.setModifiers( modifiers );
                    }
                }


                constructorNode.addFormalParameter( parameterNode );
            }
        }


        // throws
        Throws_Context throwsContext = ctx.throws_();
        if ( throwsContext != null ) {
            List<ExceptionTypeContext> exceptionTypeContext = ( List <ExceptionTypeContext> )throwsContext.exceptionTypeList().exceptionType();
            List<TigerNode> exceptionList = new ArrayList<TigerNode>();
            for ( ExceptionTypeContext e : exceptionTypeContext ) {
                TigerNode tn = new TigerNode( e.getText() );
                setLocations( tn, e );
                exceptionList.add( tn );
            }
            constructorNode.setThrows( exceptionList );
        }


        results.incMethodCount();
        parent.addChild( constructorNode );
    }


    private void processFieldDeclaration( TigerNode parent, ClassMemberDeclarationContext dctx ) {
        FieldDeclarationContext fieldCtx = dctx.fieldDeclaration();

        // type
        Type type = new Type( fieldCtx.unannType().getText() );

        // modifiers
        int size = fieldCtx.fieldModifiers().fieldModifier().size();
        String[] modifierNames = new String [size];
        for ( int i = 0; i < size; i++ ) {
            modifierNames[i] = fieldCtx.fieldModifiers().fieldModifier( i ).getText();
        }
        int modifiers = ModifierSet.getModifiers( modifierNames );

        // variable declarations, make a field node per variable
        size = fieldCtx.variableDeclaratorList().variableDeclarator().size();
        for ( int i = 0; i < size; i++ ) {
            VariableDeclaratorContext vdc = fieldCtx.variableDeclaratorList().variableDeclarator( i );
            VariableDeclarator name = new VariableDeclarator(vdc.variableDeclaratorId().getText());
            FieldNode fn = new FieldNode( name.getName(), modifiers, type );
            fn.addChild(name);
            setLocations( fn, vdc );
            if (fn.isPrimitive())
              results.incPrimitiveFieldCount();
            else
              results.incReferenceFieldCount();
            results.incReferenceFieldCount();
            parent.addChild( fn );
        }
    }


    private void processMethodDeclaration( TigerNode parent, ParserRuleContext ctx ) {
        boolean methodCtx = ctx instanceof MethodDeclarationContext ? true : false;

        MethodNode methodNode = new MethodNode();

        setLocations( methodNode, ctx );

        // modifiers
        int modifiers = 0;
        if ( methodCtx ) {
            MethodModifiersContext methodModifierContext = ( ( MethodDeclarationContext )ctx ).methodModifiers();
            int size = methodModifierContext.methodModifier().size();
            String[] modifierNames = new String [size];
            for ( int i = 0; i < size; i++ ) {
                modifierNames[i] = methodModifierContext.methodModifier( i ).getText();
            }
            modifiers = ModifierSet.getModifiers( modifierNames );
            methodNode.setModifiers( modifiers );
        }
        else {
            List<InterfaceMethodModifierContext> modifierContexts = ( List <InterfaceMethodModifierContext> )( ( InterfaceMethodDeclarationContext )ctx ).interfaceMethodModifier();
            int size = modifierContexts.size();
            String[] modifierNames = new String [size];
            for ( int i = 0; i < size; i++ ) {
                modifierNames[i] = ( ( InterfaceMethodDeclarationContext )ctx ).interfaceMethodModifier( i ).getText();
            }
            modifiers = ModifierSet.getModifiers( modifierNames );
            methodNode.setModifiers( modifiers );
        }


        // method node constructor:
        // public MethodNode( String name, int modifiers, String typeParams, List formalParams, Type returnType ) {
        MethodHeaderContext methodHeaderContext = methodCtx ? ( ( MethodDeclarationContext )ctx ).methodHeader() : ( ( InterfaceMethodDeclarationContext )ctx ).methodHeader();

        // return type
        ResultContext rc = methodHeaderContext.result();
        if ( rc != null ) {

            // method return type is a type or void
            if ( rc.unannType() != null ) {
                Type returnType = new Type( rc.unannType().getText() );
                setLocations( returnType, rc );
                methodNode.setReturnType( returnType );
            }
            else {
                Type returnType = new Type( "void" );
                setLocations( returnType, rc );
                methodNode.setReturnType( returnType );
            }
        }


        // method name
        MethodDeclaratorContext methodDeclaratorContext = methodHeaderContext.methodDeclarator();
        String name = methodDeclaratorContext.identifier().getText();
        methodNode.setName( name );

        // parameters
        // TODO: receiver parameter
        FormalParameterListContext formalParameterListContext = methodDeclaratorContext.formalParameterList();
        if ( formalParameterListContext != null ) {
            FormalParametersContext formalParametersContext = formalParameterListContext.formalParameters();

            if ( formalParametersContext != null ) {

                List<FormalParameterContext> params = ( List <FormalParameterContext> )formalParametersContext.formalParameter();
                if ( params != null ) {
                    for ( FormalParameterContext param : params ) {
                        Parameter parameterNode = new Parameter();
                        setLocations( parameterNode, param );
                        Type type = new Type( param.unannType().getText() );
                        parameterNode.setType( type );
                        parameterNode.setName( param.variableDeclaratorId().identifier().getText() );

                        // modifiers
                        int size = param.variableModifier().size();
                        String[] modifierNames = new String [size];
                        for ( int i = 0; i < size; i++ ) {
                            modifierNames[i] = param.variableModifier( i ).getText();
                        }
                        modifiers = ModifierSet.getModifiers( modifierNames );
                        parameterNode.setModifiers( modifiers );
                        methodNode.addFormalParameter( parameterNode );
                    }
                }
            }
        }


        // last formal parameter
        if ( formalParameterListContext != null ) {
            LastFormalParameterContext lastFormalParameterContext = formalParameterListContext.lastFormalParameter();
            if ( lastFormalParameterContext != null ) {
                Parameter parameterNode = new Parameter();
                setLocations( parameterNode, lastFormalParameterContext );
                if ( lastFormalParameterContext.formalParameter() != null ) {
                    FormalParameterContext param = lastFormalParameterContext.formalParameter();
                    Type type = new Type( param.unannType().getText() );
                    parameterNode.setType( type );
                    parameterNode.setName( param.variableDeclaratorId().identifier().getText() );

                    // modifiers
                    int size = param.variableModifier().size();
                    String[] modifierNames = new String [size];
                    for ( int i = 0; i < size; i++ ) {
                        modifierNames[i] = param.variableModifier( i ).getText();
                    }
                    modifiers = ModifierSet.getModifiers( modifierNames );
                    parameterNode.setModifiers( modifiers );
                }
                else {
                    if ( lastFormalParameterContext.unannType() != null ) {
                        Type type = new Type( lastFormalParameterContext.unannType().getText() );
                        parameterNode.setType( type );
                    }


                    if ( lastFormalParameterContext.variableDeclaratorId() != null ) {
                        parameterNode.setName( lastFormalParameterContext.variableDeclaratorId().identifier().getText() );
                    }


                    if ( lastFormalParameterContext.variableModifier() != null ) {
                        int size = lastFormalParameterContext.variableModifier().size();
                        String[] modifierNames = new String [size];
                        for ( int i = 0; i < size; i++ ) {
                            modifierNames[i] = lastFormalParameterContext.variableModifier( i ).getText();
                        }
                        modifiers = ModifierSet.getModifiers( modifierNames );
                        parameterNode.setModifiers( modifiers );
                    }
                }


                methodNode.addFormalParameter( parameterNode );
            }
        }


        // throws
        Throws_Context throwsContext = methodHeaderContext.throws_();
        if ( throwsContext != null ) {
            List<ExceptionTypeContext> exceptionTypeContext = ( List <ExceptionTypeContext> )throwsContext.exceptionTypeList().exceptionType();
            List<TigerNode> exceptionList = new ArrayList<TigerNode>();
            for ( ExceptionTypeContext e : exceptionTypeContext ) {
                TigerNode tn = new TigerNode( e.getText() );
                setLocations( tn, e );
                exceptionList.add( tn );
            }
            methodNode.setThrows( exceptionList );
        }


        // type parameters
        if ( methodHeaderContext.typeParameters() != null ) {
            List<TypeParameterContext> typeParameterContexts = ( List <TypeParameterContext> )methodHeaderContext.typeParameters().typeParameterList().typeParameter();
            StringBuilder sb = new StringBuilder( "<" );
            for ( TypeParameterContext typeParam : typeParameterContexts ) {
                sb.append( typeParam.identifier().getText() ).append( ',' );
            }
            if ( sb.length() > 1 ) {
                sb.deleteCharAt( sb.length() - 1 );
            }


            methodNode.setTypeParams( sb.toString() + ">" );
        }


        // annotations
        List<AnnotationContext> annotationContexts = ( List <AnnotationContext> )methodHeaderContext.annotation();
        if ( annotationContexts != null ) {
            for ( AnnotationContext ann : annotationContexts ) {
                AnnotationNode annotationNode = new AnnotationNode( ann.getText() );
                setLocations( annotationNode, ann );
                methodNode.addAnnotation( annotationNode );
            }
        }


        results.incMethodCount();
        parent.addChild( methodNode );
    }


    @Override
    public void exitClassModifiers( @NotNull ClassModifiersContext ctx ) {
    }


    @Override
    public void exitClassModifier( @NotNull ClassModifierContext ctx ) {
    }


    @Override
    public void exitTypeParameters( @NotNull TypeParametersContext ctx ) {
    }


    @Override
    public void exitTypeParameterList( @NotNull TypeParameterListContext ctx ) {
    }


    @Override
    public void exitSuperclass( @NotNull SuperclassContext ctx ) {
    }


    @Override
    public void exitSuperinterfaces( @NotNull SuperinterfacesContext ctx ) {
    }


    @Override
    public void exitInterfaceTypeList( @NotNull InterfaceTypeListContext ctx ) {
    }


    @Override
    public void exitClassBody( @NotNull ClassBodyContext ctx ) {
    }


    @Override
    public void exitClassBodyDeclaration( @NotNull ClassBodyDeclarationContext ctx ) {
    }


    @Override
    public void exitClassMemberDeclaration( @NotNull ClassMemberDeclarationContext ctx ) {
    }


    @Override
    public void exitFieldDeclaration( @NotNull FieldDeclarationContext ctx ) {
    }


    @Override
    public void exitFieldModifiers( @NotNull FieldModifiersContext ctx ) {
    }


    @Override
    public void exitFieldModifier( @NotNull FieldModifierContext ctx ) {
    }


    @Override
    public void exitVariableDeclaratorList( @NotNull VariableDeclaratorListContext ctx ) {
    }


    @Override
    public void exitVariableDeclarator( @NotNull VariableDeclaratorContext ctx ) {
    }


    @Override
    public void exitVariableDeclaratorId( @NotNull VariableDeclaratorIdContext ctx ) {
    }


    @Override
    public void exitVariableInitializer( @NotNull VariableInitializerContext ctx ) {
    }


    @Override
    public void exitUnannType( @NotNull UnannTypeContext ctx ) {
    }


    @Override
    public void exitUnannPrimitiveType( @NotNull UnannPrimitiveTypeContext ctx ) {
    }


    @Override
    public void exitUnannReferenceType( @NotNull UnannReferenceTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassOrInterfaceType( @NotNull UnannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassType( @NotNull UnannClassTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassType_lf_unannClassOrInterfaceType( @NotNull UnannClassType_lf_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannClassType_lfno_unannClassOrInterfaceType( @NotNull UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannInterfaceType( @NotNull UnannInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannInterfaceType_lf_unannClassOrInterfaceType( @NotNull UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannInterfaceType_lfno_unannClassOrInterfaceType( @NotNull UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void exitUnannTypeVariable( @NotNull UnannTypeVariableContext ctx ) {
    }


    @Override
    public void exitUnannArrayType( @NotNull UnannArrayTypeContext ctx ) {
    }


    @Override
    public void exitMethodDeclaration( @NotNull MethodDeclarationContext ctx ) {
    }


    @Override
    public void exitMethodModifiers( @NotNull MethodModifiersContext ctx ) {
    }


    @Override
    public void exitMethodModifier( @NotNull MethodModifierContext ctx ) {
    }


    @Override
    public void exitMethodHeader( @NotNull MethodHeaderContext ctx ) {
    }


    @Override
    public void exitResult( @NotNull ResultContext ctx ) {
    }


    @Override
    public void exitMethodDeclarator( @NotNull MethodDeclaratorContext ctx ) {
    }


    @Override
    public void exitFormalParameterList( @NotNull FormalParameterListContext ctx ) {
    }


    @Override
    public void exitFormalParameters( @NotNull FormalParametersContext ctx ) {
    }


    @Override
    public void exitFormalParameter( @NotNull FormalParameterContext ctx ) {
    }


    @Override
    public void exitVariableModifier( @NotNull VariableModifierContext ctx ) {
    }


    @Override
    public void exitLastFormalParameter( @NotNull LastFormalParameterContext ctx ) {
    }


    @Override
    public void exitReceiverParameter( @NotNull ReceiverParameterContext ctx ) {
    }


    @Override
    public void exitThrows_( @NotNull Throws_Context ctx ) {
    }


    @Override
    public void exitExceptionTypeList( @NotNull ExceptionTypeListContext ctx ) {
    }


    @Override
    public void exitExceptionType( @NotNull ExceptionTypeContext ctx ) {
    }


    @Override
    public void exitMethodBody( @NotNull MethodBodyContext ctx ) {
    }


    @Override
    public void exitInstanceInitializer( @NotNull InstanceInitializerContext ctx ) {
    }


    @Override
    public void exitStaticInitializer( @NotNull StaticInitializerContext ctx ) {
    }


    @Override
    public void exitConstructorDeclaration( @NotNull ConstructorDeclarationContext ctx ) {
    }


    @Override
    public void exitConstructorModifiers( @NotNull ConstructorModifiersContext ctx ) {
    }


    @Override
    public void exitConstructorModifier( @NotNull ConstructorModifierContext ctx ) {
    }


    @Override
    public void exitConstructorDeclarator( @NotNull ConstructorDeclaratorContext ctx ) {
    }


    @Override
    public void exitSimpleTypeName( @NotNull SimpleTypeNameContext ctx ) {
    }


    @Override
    public void exitConstructorBody( @NotNull ConstructorBodyContext ctx ) {
    }


    @Override
    public void exitExplicitConstructorInvocation( @NotNull ExplicitConstructorInvocationContext ctx ) {
    }


    /**
     * enumDeclaration
     * 	:	classModifiers 'enum' Identifier superinterfaces? enumBody
     * 	;
     */
    @Override
    public void exitEnumDeclaration( @NotNull EnumDeclarationContext ctx ) {

        // modifiers
        int size = ctx.classModifiers().classModifier().size();
        String[] modifierNames = new String [size];
        for ( int i = 0; i < size; i++ ) {
            modifierNames[i] = ctx.classModifiers().classModifier( i ).getText();
        }
        int modifiers = ModifierSet.getModifiers( modifierNames );

        EnumNode parent = new EnumNode( ctx.identifier().getText(), modifiers );
        setLocations( parent, ctx );

        // superinterfaces
        if ( ctx.superinterfaces() != null ) {
            SuperinterfacesContext superInterfacesContext = ctx.superinterfaces();
            InterfaceTypeListContext interfaceTypeListContext = superInterfacesContext.interfaceTypeList();
            List<InterfaceTypeContext> interfaceTypeContexts = ( List <InterfaceTypeContext> )interfaceTypeListContext.interfaceType();
            for ( InterfaceTypeContext itc : interfaceTypeContexts ) {
                Type type = new Type( itc.getText() );
                setLocations( type, itc );
                parent.addChild( type );
            }
        }


        // enumBody
        // :	'{' enumConstantList? COMMA? enumBodyDeclarations? '}'
        // ;
        // add the children of this class. Only need the fields and methods here,
        // the inner classes and interface declarations are handled elsewhere
        EnumBodyContext enumBodyContext = ctx.enumBody();
        if ( enumBodyContext.enumConstantList() != null ) {
            EnumConstantListContext enumConstantListContext = enumBodyContext.enumConstantList();
            List<EnumConstantContext> constants = ( List <EnumConstantContext> )enumConstantListContext.enumConstant();
            for ( EnumConstantContext constant : constants ) {
                TigerNode tn = new TigerNode( constant.getText() );
                setLocations( tn, constant );
                parent.addChild( tn );
            }
        }


        if ( enumBodyContext.enumBodyDeclarations() != null ) {

            // add the children of this class. Only need the fields and methods here,
            // the inner classes and interface declarations are handled elsewhere
            EnumBodyDeclarationsContext enumBodyDeclarationsContext = enumBodyContext.enumBodyDeclarations();
            List<ClassBodyDeclarationContext> declarations = ( List <ClassBodyDeclarationContext> )enumBodyDeclarationsContext.classBodyDeclaration();
            for ( ClassBodyDeclarationContext declaration : declarations ) {
                if ( declaration.classMemberDeclaration() != null ) {
                    ClassMemberDeclarationContext dctx = declaration.classMemberDeclaration();
                    if ( dctx.fieldDeclaration() != null ) {
                        processFieldDeclaration( parent, dctx );
                    }


                    if ( dctx.methodDeclaration() != null ) {
                        processMethodDeclaration( parent, dctx.methodDeclaration() );
                    }


                    if ( dctx.classDeclaration() != null ) {
                        parent.addChild( stack.pop() );
                    }


                    if ( dctx.interfaceDeclaration() != null ) {
                        parent.addChild( stack.pop() );
                    }
                }
            }
        }


        stack.push( parent );
    }


    @Override
    public void exitEnumBody( @NotNull EnumBodyContext ctx ) {
    }


    @Override
    public void exitEnumConstantList( @NotNull EnumConstantListContext ctx ) {
    }


    @Override
    public void exitEnumConstant( @NotNull EnumConstantContext ctx ) {
    }


    @Override
    public void exitEnumConstantModifier( @NotNull EnumConstantModifierContext ctx ) {
    }


    @Override
    public void exitEnumBodyDeclarations( @NotNull EnumBodyDeclarationsContext ctx ) {
    }


    /**
     * interfaceDeclaration
     * 	:	normalInterfaceDeclaration
     * 	|	annotationTypeDeclaration
     * 	;
     */
    @Override
    public void exitInterfaceDeclaration( @NotNull InterfaceDeclarationContext ctx ) {
    }


    /**
     * normalInterfaceDeclaration
     * 	:	interfaceModifiers 'interface' Identifier typeParameters? extendsInterfaces? interfaceBody
     * 	;
     */
    @Override
    public void exitNormalInterfaceDeclaration( @NotNull NormalInterfaceDeclarationContext ctx ) {
        
        // modifiers
        InterfaceModifiersContext imc = ctx.interfaceModifiers();
        int size = imc.interfaceModifier().size();
        String[] modifierNames = new String [size];
        for ( int i = 0; i < size; i++ ) {
            modifierNames[i] = imc.interfaceModifier( i ).getText();
        }
        int modifiers = ModifierSet.getModifiers( modifierNames );
        InterfaceNode parent = new InterfaceNode( ctx.identifier().getText(), modifiers );
        setLocations( parent, ctx );

        // typeParameters
        TypeParametersContext typeParametersContext = ctx.typeParameters();
        if (typeParametersContext != null) {
            TypeParameterListContext typeListContext = typeParametersContext.typeParameterList();
            List<TypeParameterContext> tpc = ( List <TypeParameterContext> )typeListContext.typeParameter();
            StringBuilder sb = new StringBuilder( "<" );
            for ( TypeParameterContext t : tpc ) {
                sb.append( t.identifier().getText() ).append( ',' );
            }
            if ( sb.length() > 1 ) {
                sb.deleteCharAt( sb.length() - 1 );
            }

            parent.setTypeParams( sb.toString() + ">" );
        }

        // extendsInterfaces
        ExtendsInterfacesContext eic = ctx.extendsInterfaces();
        if (eic != null) {
            InterfaceTypeListContext itlc = eic.interfaceTypeList();
            if (itlc != null) {
                List<InterfaceTypeContext> itc = ( List <InterfaceTypeContext> )itlc.interfaceType();
                List<Type> extendsTypes = new ArrayList<Type>();
                for ( InterfaceTypeContext i : itc ) {
                    Type it = new Type( i.getText() );
                    setLocations( it, i );
                    extendsTypes.add( it );
                }
                parent.setExtendsList( extendsTypes );
            }
        }

        // interfaceBody
        InterfaceBodyContext ibc = ctx.interfaceBody();
        List<InterfaceMemberDeclarationContext> imdc = ( List <InterfaceMemberDeclarationContext> )ibc.interfaceMemberDeclaration();
        for ( InterfaceMemberDeclarationContext i : imdc ) {
            if ( i.constantDeclaration() != null ) {
                processConstantDeclaration( parent, i.constantDeclaration() );
            }


            if ( i.interfaceMethodDeclaration() != null ) {
                processMethodDeclaration( parent, i.interfaceMethodDeclaration() );
            }


            if ( i.classDeclaration() != null ) {
                parent.addChild( stack.pop() );
            }


            if ( i.interfaceDeclaration() != null ) {
                parent.addChild( stack.pop() );
            }
        }

        results.incInterfaceCount();
        stack.push( parent );
    }


    /**
     * constantDeclaration
     * 	:	constantModifier* unannType variableDeclaratorList ';'
     * 	;
     */
    private void processConstantDeclaration( TigerNode parent, ConstantDeclarationContext ctx ) {

        // modifiers
        List<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        List<String> modifierNames = new ArrayList<String>();
        if ( ctx.constantModifier() != null ) {
            List<ConstantModifierContext> cmc = ( List <ConstantModifierContext> )ctx.constantModifier();
            for ( ConstantModifierContext c : cmc ) {
                if ( c.annotation() != null ) {
                    AnnotationNode an = new AnnotationNode( c.annotation().getText() );
                    setLocations( an, c.annotation() );
                }
                else {
                    modifierNames.add( c.getText() );
                }
            }
        }


        int modifiers = ModifierSet.getModifiers( modifierNames.toArray( new String [0]  ) );

        // type
        Type type = null;
        if ( ctx.unannType() != null ) {
            type = new Type( ctx.unannType().getText() );
            setLocations( type, ctx );
        }


        // variable declarators
        if ( ctx.variableDeclaratorList() != null ) {
            VariableDeclaratorListContext vdlc = ctx.variableDeclaratorList();
            List<VariableDeclaratorContext> vList = ( List <VariableDeclaratorContext> )vdlc.variableDeclarator();
            for ( VariableDeclaratorContext v : vList ) {
                VariableDeclarator vd = new VariableDeclarator( v.getText() );
                setLocations( vd, ctx );
                vd.setModifiers( modifiers );
                if ( type != null ) {
                    vd.setType( type );
                }


                if ( annotations.size() > 0 ) {
                    vd.addAnnotations( annotations );
                }


                parent.addChild( vd );
            }
        }


        stack.push( parent );
    }


    @Override
    public void exitInterfaceModifiers( @NotNull InterfaceModifiersContext ctx ) {
    }


    @Override
    public void exitInterfaceModifier( @NotNull InterfaceModifierContext ctx ) {
    }


    @Override
    public void exitExtendsInterfaces( @NotNull ExtendsInterfacesContext ctx ) {
    }


    @Override
    public void exitInterfaceBody( @NotNull InterfaceBodyContext ctx ) {
    }


    @Override
    public void exitInterfaceMemberDeclaration( @NotNull InterfaceMemberDeclarationContext ctx ) {
    }


    @Override
    public void exitConstantDeclaration( @NotNull ConstantDeclarationContext ctx ) {
    }


    @Override
    public void exitConstantModifier( @NotNull ConstantModifierContext ctx ) {
    }


    @Override
    public void exitInterfaceMethodDeclaration( @NotNull InterfaceMethodDeclarationContext ctx ) {
    }


    @Override
    public void exitInterfaceMethodModifier( @NotNull InterfaceMethodModifierContext ctx ) {
    }


    /**
     * annotationTypeDeclaration
     * 	:	interfaceModifier* '@' 'interface' Identifier annotationTypeBody
     * 	;
     */
    @Override
    public void exitAnnotationTypeDeclaration( @NotNull AnnotationTypeDeclarationContext ctx ) {

        // modifiers
        int size = ctx.interfaceModifier().size();
        String[] modifierNames = new String [size];
        for ( int i = 0; i < size; i++ ) {
            modifierNames[i] = ctx.interfaceModifier( i ).getText();
        }
        int modifiers = ModifierSet.getModifiers( modifierNames );
        AnnotationTypeNode parent = new AnnotationTypeNode( ctx.identifier().getText(), modifiers );
        setLocations( parent, ctx );

        // body contents
        AnnotationTypeBodyContext annotationTypeBodyContext = ctx.annotationTypeBody();
        List<AnnotationTypeMemberDeclarationContext> atmdc = ( List <AnnotationTypeMemberDeclarationContext> )annotationTypeBodyContext.annotationTypeMemberDeclaration();
        for ( AnnotationTypeMemberDeclarationContext a : atmdc ) {
            TigerNode tn = new TigerNode( a.getText() );
            setLocations( tn, a );
            parent.addChild( tn );
        }

        stack.push( parent );
    }


    @Override
    public void exitAnnotationTypeBody( @NotNull AnnotationTypeBodyContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeMemberDeclaration( @NotNull AnnotationTypeMemberDeclarationContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeElementDeclaration( @NotNull AnnotationTypeElementDeclarationContext ctx ) {
    }


    @Override
    public void exitAnnotationTypeElementModifier( @NotNull AnnotationTypeElementModifierContext ctx ) {
    }


    @Override
    public void exitDefaultValue( @NotNull DefaultValueContext ctx ) {
    }


    @Override
    public void exitAnnotation( @NotNull AnnotationContext ctx ) {
    }


    @Override
    public void exitAnnotationIdentifier( @NotNull AnnotationIdentifierContext ctx ) {
    }


    @Override
    public void exitAnnotationDim( @NotNull AnnotationDimContext ctx ) {
    }


    @Override
    public void exitNormalAnnotation( @NotNull NormalAnnotationContext ctx ) {
    }


    @Override
    public void exitElementValuePairList( @NotNull ElementValuePairListContext ctx ) {
    }


    @Override
    public void exitElementValuePair( @NotNull ElementValuePairContext ctx ) {
    }


    @Override
    public void exitElementValue( @NotNull ElementValueContext ctx ) {
    }


    @Override
    public void exitElementValueArrayInitializer( @NotNull ElementValueArrayInitializerContext ctx ) {
    }


    @Override
    public void exitElementValueList( @NotNull ElementValueListContext ctx ) {
    }


    @Override
    public void exitMarkerAnnotation( @NotNull MarkerAnnotationContext ctx ) {
    }


    @Override
    public void exitSingleElementAnnotation( @NotNull SingleElementAnnotationContext ctx ) {
    }


    @Override
    public void exitArrayInitializer( @NotNull ArrayInitializerContext ctx ) {
    }


    @Override
    public void exitVariableInitializerList( @NotNull VariableInitializerListContext ctx ) {
    }


    @Override
    public void exitBlock( @NotNull BlockContext ctx ) {
    }


    @Override
    public void exitBlockStatements( @NotNull BlockStatementsContext ctx ) {
    }


    @Override
    public void exitBlockStatement( @NotNull BlockStatementContext ctx ) {
    }


    @Override
    public void exitLocalVariableDeclarationStatement( @NotNull LocalVariableDeclarationStatementContext ctx ) {
    }


    @Override
    public void exitLocalVariableDeclaration( @NotNull LocalVariableDeclarationContext ctx ) {
    }


    @Override
    public void exitStatement( @NotNull StatementContext ctx ) {
    }


    @Override
    public void exitStatementNoShortIf( @NotNull StatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitStatementWithoutTrailingSubstatement( @NotNull StatementWithoutTrailingSubstatementContext ctx ) {
    }


    @Override
    public void exitEmptyStatement( @NotNull EmptyStatementContext ctx ) {
    }


    @Override
    public void exitLabeledStatement( @NotNull LabeledStatementContext ctx ) {
    }


    @Override
    public void exitLabeledStatementNoShortIf( @NotNull LabeledStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitExpressionStatement( @NotNull ExpressionStatementContext ctx ) {
    }


    @Override
    public void exitStatementExpression( @NotNull StatementExpressionContext ctx ) {
    }


    @Override
    public void exitIfThenStatement( @NotNull IfThenStatementContext ctx ) {
    }


    @Override
    public void exitIfThenElseStatement( @NotNull IfThenElseStatementContext ctx ) {
    }


    @Override
    public void exitIfThenElseStatementNoShortIf( @NotNull IfThenElseStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitAssertStatement( @NotNull AssertStatementContext ctx ) {
    }


    @Override
    public void exitSwitchStatement( @NotNull SwitchStatementContext ctx ) {
    }


    @Override
    public void exitSwitchBlock( @NotNull SwitchBlockContext ctx ) {
    }


    @Override
    public void exitSwitchBlockStatementGroup( @NotNull SwitchBlockStatementGroupContext ctx ) {
    }


    @Override
    public void exitSwitchLabels( @NotNull SwitchLabelsContext ctx ) {
    }


    @Override
    public void exitSwitchLabel( @NotNull SwitchLabelContext ctx ) {
    }


    @Override
    public void exitEnumConstantName( @NotNull EnumConstantNameContext ctx ) {
    }


    @Override
    public void exitWhileStatement( @NotNull WhileStatementContext ctx ) {
    }


    @Override
    public void exitWhileStatementNoShortIf( @NotNull WhileStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitDoStatement( @NotNull DoStatementContext ctx ) {
    }


    @Override
    public void exitForStatement( @NotNull ForStatementContext ctx ) {
    }


    @Override
    public void exitForStatementNoShortIf( @NotNull ForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitBasicForStatement( @NotNull BasicForStatementContext ctx ) {
    }


    @Override
    public void exitBasicForStatementNoShortIf( @NotNull BasicForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitForInit( @NotNull ForInitContext ctx ) {
    }


    @Override
    public void exitForUpdate( @NotNull ForUpdateContext ctx ) {
    }


    @Override
    public void exitStatementExpressionList( @NotNull StatementExpressionListContext ctx ) {
    }


    @Override
    public void exitEnhancedForStatement( @NotNull EnhancedForStatementContext ctx ) {
    }


    @Override
    public void exitEnhancedForStatementNoShortIf( @NotNull EnhancedForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void exitBreakStatement( @NotNull BreakStatementContext ctx ) {
    }


    @Override
    public void exitContinueStatement( @NotNull ContinueStatementContext ctx ) {
    }


    @Override
    public void exitReturnStatement( @NotNull ReturnStatementContext ctx ) {
    }


    @Override
    public void exitThrowStatement( @NotNull ThrowStatementContext ctx ) {
    }


    @Override
    public void exitSynchronizedStatement( @NotNull SynchronizedStatementContext ctx ) {
    }


    @Override
    public void exitTryStatement( @NotNull TryStatementContext ctx ) {
    }


    @Override
    public void exitCatches( @NotNull CatchesContext ctx ) {
    }


    @Override
    public void exitCatchClause( @NotNull CatchClauseContext ctx ) {
    }


    @Override
    public void exitCatchFormalParameter( @NotNull CatchFormalParameterContext ctx ) {
    }


    @Override
    public void exitCatchType( @NotNull CatchTypeContext ctx ) {
    }


    @Override
    public void exitFinally_( @NotNull Finally_Context ctx ) {
    }


    @Override
    public void exitTryWithResourcesStatement( @NotNull TryWithResourcesStatementContext ctx ) {
    }


    @Override
    public void exitResourceSpecification( @NotNull ResourceSpecificationContext ctx ) {
    }


    @Override
    public void exitResourceList( @NotNull ResourceListContext ctx ) {
    }


    @Override
    public void exitResource( @NotNull ResourceContext ctx ) {
    }


    @Override
    public void exitPrimary( @NotNull PrimaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray( @NotNull PrimaryNoNewArrayContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_arrayAccess( @NotNull PrimaryNoNewArray_lf_arrayAccessContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_arrayAccess( @NotNull PrimaryNoNewArray_lfno_arrayAccessContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_primary( @NotNull PrimaryNoNewArray_lf_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary( @NotNull PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary( @NotNull PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_primary( @NotNull PrimaryNoNewArray_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary( @NotNull PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary( @NotNull PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitClassInstanceCreationExpression( @NotNull ClassInstanceCreationExpressionContext ctx ) {
    }


    @Override
    public void exitClassInstanceCreationExpression_lf_primary( @NotNull ClassInstanceCreationExpression_lf_primaryContext ctx ) {
    }


    @Override
    public void exitClassInstanceCreationExpression_lfno_primary( @NotNull ClassInstanceCreationExpression_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitTypeArgumentsOrDiamond( @NotNull TypeArgumentsOrDiamondContext ctx ) {
    }


    @Override
    public void exitFieldAccess( @NotNull FieldAccessContext ctx ) {
    }


    @Override
    public void exitFieldAccess_lf_primary( @NotNull FieldAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitFieldAccess_lfno_primary( @NotNull FieldAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitArrayAccess( @NotNull ArrayAccessContext ctx ) {
    }


    @Override
    public void exitArrayAccess_lf_primary( @NotNull ArrayAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void exitArrayAccess_lfno_primary( @NotNull ArrayAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitMethodInvocation( @NotNull MethodInvocationContext ctx ) {
    }


    @Override
    public void exitMethodInvocation_lf_primary( @NotNull MethodInvocation_lf_primaryContext ctx ) {
    }


    @Override
    public void exitMethodInvocation_lfno_primary( @NotNull MethodInvocation_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitArgumentList( @NotNull ArgumentListContext ctx ) {
    }


    @Override
    public void exitMethodReference( @NotNull MethodReferenceContext ctx ) {
    }


    @Override
    public void exitMethodReference_lf_primary( @NotNull MethodReference_lf_primaryContext ctx ) {
    }


    @Override
    public void exitMethodReference_lfno_primary( @NotNull MethodReference_lfno_primaryContext ctx ) {
    }


    @Override
    public void exitArrayCreationExpression( @NotNull ArrayCreationExpressionContext ctx ) {
    }


    @Override
    public void exitDimExprs( @NotNull DimExprsContext ctx ) {
    }


    @Override
    public void exitDimExpr( @NotNull DimExprContext ctx ) {
    }


    @Override
    public void exitConstantExpression( @NotNull ConstantExpressionContext ctx ) {
    }


    @Override
    public void exitExpression( @NotNull ExpressionContext ctx ) {
    }


    @Override
    public void exitLambdaExpression( @NotNull LambdaExpressionContext ctx ) {
    }


    @Override
    public void exitLambdaParameters( @NotNull LambdaParametersContext ctx ) {
    }


    @Override
    public void exitInferredFormalParameterList( @NotNull InferredFormalParameterListContext ctx ) {
    }


    @Override
    public void exitLambdaBody( @NotNull LambdaBodyContext ctx ) {
    }


    @Override
    public void exitAssignmentExpression( @NotNull AssignmentExpressionContext ctx ) {
    }


    @Override
    public void exitAssignment( @NotNull AssignmentContext ctx ) {
    }


    @Override
    public void exitLeftHandSide( @NotNull LeftHandSideContext ctx ) {
    }


    @Override
    public void exitAssignmentOperator( @NotNull AssignmentOperatorContext ctx ) {
    }


    @Override
    public void exitAdditiveOperator( @NotNull AdditiveOperatorContext ctx ) {
    }


    @Override
    public void exitRelationalOperator( @NotNull RelationalOperatorContext ctx ) {
    }


    @Override
    public void exitMultiplicativeOperator( @NotNull MultiplicativeOperatorContext ctx ) {
    }


    @Override
    public void exitSquareBrackets( @NotNull SquareBracketsContext ctx ) {
    }


    @Override
    public void exitConditionalExpression( @NotNull ConditionalExpressionContext ctx ) {
    }


    @Override
    public void exitConditionalOrExpression( @NotNull ConditionalOrExpressionContext ctx ) {
    }


    @Override
    public void exitConditionalAndExpression( @NotNull ConditionalAndExpressionContext ctx ) {
    }


    @Override
    public void exitInclusiveOrExpression( @NotNull InclusiveOrExpressionContext ctx ) {
    }


    @Override
    public void exitExclusiveOrExpression( @NotNull ExclusiveOrExpressionContext ctx ) {
    }


    @Override
    public void exitAndExpression( @NotNull AndExpressionContext ctx ) {
    }


    @Override
    public void exitEqualityExpression( @NotNull EqualityExpressionContext ctx ) {
    }


    @Override
    public void exitRelationalExpression( @NotNull RelationalExpressionContext ctx ) {
    }


    @Override
    public void exitShiftExpression( @NotNull ShiftExpressionContext ctx ) {
    }


    @Override
    public void exitShiftOperator( @NotNull ShiftOperatorContext ctx ) {
    }


    @Override
    public void exitAdditiveExpression( @NotNull AdditiveExpressionContext ctx ) {
    }


    @Override
    public void exitMultiplicativeExpression( @NotNull MultiplicativeExpressionContext ctx ) {
    }


    @Override
    public void exitUnaryExpression( @NotNull UnaryExpressionContext ctx ) {
    }


    @Override
    public void exitPreIncrementExpression( @NotNull PreIncrementExpressionContext ctx ) {
    }


    @Override
    public void exitPreDecrementExpression( @NotNull PreDecrementExpressionContext ctx ) {
    }


    @Override
    public void exitUnaryExpressionNotPlusMinus( @NotNull UnaryExpressionNotPlusMinusContext ctx ) {
    }


    @Override
    public void exitPostfixExpression( @NotNull PostfixExpressionContext ctx ) {
    }


    @Override
    public void exitPostIncrementExpression( @NotNull PostIncrementExpressionContext ctx ) {
    }


    @Override
    public void exitPostIncrementExpression_lf_postfixExpression( @NotNull PostIncrementExpression_lf_postfixExpressionContext ctx ) {
    }


    @Override
    public void exitPostDecrementExpression( @NotNull PostDecrementExpressionContext ctx ) {
    }


    @Override
    public void exitPostDecrementExpression_lf_postfixExpression( @NotNull PostDecrementExpression_lf_postfixExpressionContext ctx ) {
    }


    @Override
    public void exitCastExpression( @NotNull CastExpressionContext ctx ) {
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
