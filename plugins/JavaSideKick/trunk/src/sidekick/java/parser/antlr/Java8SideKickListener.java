
package sidekick.java.parser.antlr;


import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.java.node.*;
import static sidekick.java.parser.antlr.Java8Parser.*;
import sidekick.util.Location;

// TODO: need to add better tree building, especially inside methods so that
// local variable declarations, inner classes, and so on are part of the tree.
// Might need to remove the 'process*' methods and fill in the various 'exit*'
// methods to properly fill in the tree.
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
    public void exitCompilationUnit( CompilationUnitContext ctx ) {
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
    public void exitPackageDeclaration( PackageDeclarationContext ctx ) {
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
    public void exitImportDeclaration( ImportDeclarationContext ctx ) {
        TigerNode parent = new ImportNode( ctx.getText().substring( "import".length() ) );
        setLocations( parent, ctx );

        stack.push( parent );
    }


    @Override
    public void exitTypeDeclaration( TypeDeclarationContext ctx ) {
    }


    /**
     * normalClassDeclaration
     * 	:	classModifiers 'class' Identifier typeParameters? superclass? superinterfaces? classBody
     * 	;
     */
    @Override
    public void exitNormalClassDeclaration( NormalClassDeclarationContext ctx ) {
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
            List<TigerNode> interfaceTypes = new ArrayList<TigerNode>();
            for ( InterfaceTypeContext itc : interfaceTypeContexts ) {
                TigerNode type = new TigerNode( itc.getText() );
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

    /**
     * enumDeclaration
     * 	:	classModifiers 'enum' Identifier superinterfaces? enumBody
     * 	;
     */
    @Override
    public void exitEnumDeclaration( EnumDeclarationContext ctx ) {

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

    /**
     * normalInterfaceDeclaration
     * 	:	interfaceModifiers 'interface' Identifier typeParameters? extendsInterfaces? interfaceBody
     * 	;
     */
    @Override
    public void exitNormalInterfaceDeclaration( NormalInterfaceDeclarationContext ctx ) {
        
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


    /**
     * annotationTypeDeclaration
     * 	:	interfaceModifier* '@' 'interface' Identifier annotationTypeBody
     * 	;
     */
    @Override
    public void exitAnnotationTypeDeclaration( AnnotationTypeDeclarationContext ctx ) {

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

}
