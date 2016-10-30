
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

    Deque<TigerNode> stack = new ArrayDeque<TigerNode>(){

        /*
         * public TigerNode pop() {
         * TigerNode tn = super.pop();
         * System.out.println("+++++ pop: " + tn);
         * return tn;
         *
         *
         *
    }
     *
     * public void push(TigerNode tn) {
     * System.out.println("+++++ push: " + tn);
     * super.push(tn);
     * }
     */
    };
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


    /* =============================================================================
     * Productions from §4 (Types, Values, and Variables)
     */

    /**
     * primitiveType
     * 	:	annotation* numericType
     * 	|	annotation* 'boolean'
     * 	;
     */
    @Override
    public void exitPrimitiveType( @NotNull Java8Parser.PrimitiveTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );

        if ( ctx.numericType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotation() != null ) {
            for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * integralType
     * 	:	'byte'
     * 	|	'short'
     * 	|	'int'
     * 	|	'long'
     * 	|	'char'
     * 	;
     */
    @Override
    public void exitIntegralType( @NotNull Java8Parser.IntegralTypeContext ctx ) {
        stack.push( new TigerNode( ctx.getText() ) );
    }


    /**
     * floatingPointType
     * 	:	'float'
     * 	|	'double'
     * 	;
     */
    @Override
    public void exitFloatingPointType( @NotNull Java8Parser.FloatingPointTypeContext ctx ) {
        stack.push( new TigerNode( ctx.getText() ) );
    }


    /**
     * classOrInterfaceType
     * 	:	(	classType_lfno_classOrInterfaceType
     * 		|	interfaceType_lfno_classOrInterfaceType
     * 		)
     * 		(	classType_lf_classOrInterfaceType
     * 		|	interfaceType_lf_classOrInterfaceType
     * 		)*
     * 	;
     */
    @Override
    public void exitClassOrInterfaceType( @NotNull Java8Parser.ClassOrInterfaceTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        int size = 0;
        if ( ctx.interfaceType_lf_classOrInterfaceType() != null ) {
            size = ctx.interfaceType_lf_classOrInterfaceType().size();
        }


        if ( ctx.classType_lf_classOrInterfaceType() != null ) {
            size += ctx.classType_lf_classOrInterfaceType().size();
        }


        for ( int i = 0; i < size; i++ ) {
            parent.addChild( stack.pop() );
        }
        parent.addChild( stack.pop() );    // classType_lfno_classOrInterfaceType or interfaceType_lfno_classOrInterfaceType
        stack.push( parent );
    }


    /**
     * classType
     * 	:	annotationIdentifier typeArguments?
     * 	|	classOrInterfaceType '.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitClassType( @NotNull Java8Parser.ClassTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        parent.addChild( stack.pop() );    // annotationIdentifier
        if ( ctx.classOrInterfaceType() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * classType_lf_classOrInterfaceType
     * 	:	'.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitClassType_lf_classOrInterfaceType( @NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        parent.addChild( stack.pop() );

        stack.push( parent );
    }


    /**
     * classType_lfno_classOrInterfaceType
     * 	:	annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitClassType_lfno_classOrInterfaceType( @NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        parent.addChild( stack.pop() );

        stack.push( parent );
    }


    /**
     * arrayType
     * 	:	primitiveType dims
     * 	|	classOrInterfaceType dims
     * 	|	typeVariable dims
     * 	;
     */
    @Override
    public void exitArrayType( @NotNull Java8Parser.ArrayTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.dims() != null ) {
            parent.addChild( stack.pop() );
        }


        parent.addChild( stack.pop() );

        stack.push( parent );
    }


    /**
     * dims
     * 	:	annotationDim (annotationDim)*
     * 	;
     */
    @Override
    public void exitDims( @NotNull Java8Parser.DimsContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotationDim() != null ) {
            for ( int i = 0; i < ctx.annotationDim().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * typeParameter
     * 	:	typeParameterModifier* Identifier typeBound?
     * 	;
     * typeParameterModifier is annotation
     */
    @Override
    public void exitTypeParameter( @NotNull Java8Parser.TypeParameterContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeBound() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getSymbol().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeParameterModifier() != null ) {
            for ( int i = 0; i < ctx.typeParameterModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * typeBound
     * 	:	'extends' typeVariable
     * 	|	'extends' classOrInterfaceType additionalBound*
     * 	;
     */
    @Override
    public void exitTypeBound( @NotNull Java8Parser.TypeBoundContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.additionalBound() != null ) {
            for ( int i = 0; i < ctx.additionalBound().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        parent.addChild( stack.pop() );    // typeVariable or classOrInterfaceType

        stack.push( parent );
    }


    /**
     * typeArgumentList
     * 	:	typeArgument (',' typeArgument)*
     * 	;
     */
    @Override
    public void exitTypeArgumentList( @NotNull Java8Parser.TypeArgumentListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeArgument() != null ) {
            for ( int i = 0; i < ctx.typeArgument().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * wildcard
     * 	:	annotation* '?' wildcardBounds?
     * 	;
     */
    @Override
    public void exitWildcard( @NotNull Java8Parser.WildcardContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );

        if ( ctx.wildcardBounds() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotation() != null ) {
            for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * wildcardBounds
     * 	:	'extends' referenceType
     * 	|	SUPER referenceType
     * 	;
     */
    @Override
    public void exitWildcardBounds( @NotNull Java8Parser.WildcardBoundsContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );

        if ( ctx.referenceType() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /* =============================================================================
     * Productions from §6 (Names)
     */

    /**
     * typeName
     * 	:	Identifier
     * 	|	packageOrTypeName '.' Identifier
     * 	;
     */
    @Override
    public void exitTypeName( @NotNull Java8Parser.TypeNameContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getSymbol().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.packageOrTypeName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * packageOrTypeName
     * 	:	Identifier
     * 	|	packageOrTypeName '.' Identifier
     * 	;
     */
    @Override
    public void exitPackageOrTypeName( @NotNull Java8Parser.PackageOrTypeNameContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getSymbol().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.packageOrTypeName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * expressionName
     * 	:	Identifier
     * 	|	ambiguousName '.' Identifier
     * 	;
     */
    @Override
    public void exitExpressionName( @NotNull Java8Parser.ExpressionNameContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getSymbol().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.ambiguousName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * methodName
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitMethodName( @NotNull Java8Parser.MethodNameContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getSymbol().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * ambiguousName
     * 	:	Identifier
     * 	|	ambiguousName '.' Identifier
     * 	;
     */
    @Override
    public void exitAmbiguousName( @NotNull Java8Parser.AmbiguousNameContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getSymbol().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.ambiguousName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /* =============================================================================
     * Productions from §7 (Packages)
     */
    @Override
    public void enterCompilationUnit( @NotNull Java8Parser.CompilationUnitContext ctx ) {
        System.out.println( "+++++ enterCompilationUnit" );
    }


    /**
     * compilationUnit
     * 	:	packageDeclaration? importDeclaration* typeDeclaration* EOF
     * 	;
     */
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


    /**
     * packageDeclaration
     * 	:	packageModifier* 'package' Identifier ('.' Identifier)* ';'
     * 	;
     */
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
        System.out.println( "+++++ packageDeclaration: " + parent.getName() );
    }


    /**
     * singleTypeImportDeclaration
     * 	:	'import' typeName ';'
     * 	;
     */
    @Override
    public void exitSingleTypeImportDeclaration( @NotNull Java8Parser.SingleTypeImportDeclarationContext ctx ) {
        TigerNode parent = new ImportNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        TigerNode tn = stack.pop();
        parent.setName( tn.getName() );

        stack.push( parent );
        System.out.println( "+++++ exitSingleTypeImportDeclaration: " + parent );
    }


    /**
     * typeImportOnDemandDeclaration
     * 	:	'import' packageOrTypeName '.' '*' ';'
     * 	;
     */
    @Override
    public void exitTypeImportOnDemandDeclaration( @NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx ) {
        TigerNode parent = new ImportNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        TigerNode tn = stack.pop();
        parent.setName( tn.getName() + ".*" );

        stack.push( parent );
        System.out.println( "+++++ exitTypeImportOnDemandDeclaration: " + parent );
    }


    /**
     * singleStaticImportDeclaration
     * 	:	'import' 'static' typeName '.' Identifier ';'
     * 	;
     */
    @Override
    public void exitSingleStaticImportDeclaration( @NotNull Java8Parser.SingleStaticImportDeclarationContext ctx ) {
        TigerNode parent = new ImportNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        String id = ctx.Identifier().getText();
        TigerNode tn = stack.pop();
        parent.setName( tn.getName() + '.' + id );

        stack.push( parent );
        System.out.println( "+++++ exitSingleStaticImportDeclaration: " + parent );
    }


    /**
     * staticImportOnDemandDeclaration
     * 	:	'import' 'static' typeName '.' '*' ';'
     * 	;
     */
    @Override
    public void exitStaticImportOnDemandDeclaration( @NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx ) {
        TigerNode parent = new ImportNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        TigerNode tn = stack.pop();
        parent.setName( tn.getName() + ".*" );

        stack.push( parent );
        System.out.println( "+++++ exitStaticImportOnDemandDeclaration: " + parent );
    }


    /* =============================================================================
     * Productions from §8 (Classes)
     */

    /**
     * normalClassDeclaration
     * 	:	classModifiers 'class' Identifier typeParameters? superclass? superinterfaces? classBody
     * 	;
     */
    @Override
    public void exitNormalClassDeclaration( @NotNull Java8Parser.NormalClassDeclarationContext ctx ) {
        String name = "";
        if ( ctx.Identifier() != null ) {
            name = ctx.Identifier().getText();
        }


        TigerNode parent = new ClassNode( name );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.superinterfaces() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.superclass() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeParameters() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.classModifiers() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * classModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'protected'
     * 	|	'private'
     * 	|	'abstract'
     * 	|	'static'
     * 	|	'final'
     * 	|	'strictfp'
     * 	;
     */
    @Override
    public void exitClassModifier( @NotNull Java8Parser.ClassModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * typeParameterList
     * 	:	typeParameter (',' typeParameter)*
     * 	;
     */
    @Override
    public void exitTypeParameterList( @NotNull Java8Parser.TypeParameterListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeParameter() != null ) {
            for ( int i = 0; i < ctx.typeParameter().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * superclass
     * 	:	'extends' classType
     * 	;
     */
    @Override
    public void exitSuperclass( @NotNull Java8Parser.SuperclassContext ctx ) {
        TigerNode parent = new ExtendsNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classType() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * superinterfaces
     * 	:	'implements' interfaceTypeList
     * 	;
     */
    @Override
    public void exitSuperinterfaces( @NotNull Java8Parser.SuperinterfacesContext ctx ) {
        TigerNode parent = new ImplementsNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.interfaceTypeList() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * interfaceTypeList
     * 	:	interfaceType (',' interfaceType)*
     * 	;
     */
    @Override
    public void exitInterfaceTypeList( @NotNull Java8Parser.InterfaceTypeListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.interfaceType() != null ) {
            for ( int i = 0; i < ctx.interfaceType().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * classBody
     * 	:	'{' classBodyDeclaration* '}'
     * 	;
     */
    @Override
    public void exitClassBody( @NotNull Java8Parser.ClassBodyContext ctx ) {
        if ( ctx.classBodyDeclaration() != null ) {
            TigerNode parent = new TigerNode( "classBody" );
            setStartLocation( parent, ctx );
            setEndLocation( parent, ctx );
            for ( int i = 0; i < ctx.classBodyDeclaration().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
            stack.push( parent );
        }
    }


    /**
     * fieldDeclaration
     * 	:	fieldModifiers unannType variableDeclaratorList ';'
     * 	;
     */
    @Override
    public void exitFieldDeclaration( @NotNull Java8Parser.FieldDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableDeclaratorList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.fieldModifiers() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * fieldModifiers
     *     :   fieldModifier*
     *     ;
     */
    @Override
    public void exitFieldModifiers( @NotNull Java8Parser.FieldModifiersContext ctx ) {

        // TODO: do the other modifier methods like this one, don't add an empty parent
        if ( ctx.fieldModifier() != null ) {
            TigerNode parent = new TigerNode( ctx.getText() );
            setStartLocation( parent, ctx );
            setEndLocation( parent, ctx );
            for ( int i = 0; i < ctx.fieldModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
            stack.push( parent );
        }
    }


    /**
     * fieldModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'protected'
     * 	|	'private'
     * 	|	'static'
     * 	|	'final'
     * 	|	'transient'
     * 	|	'volatile'
     * 	;
     */
    @Override
    public void exitFieldModifier( @NotNull Java8Parser.FieldModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * variableDeclaratorList
     * 	:	variableDeclarator (',' variableDeclarator)*
     * 	;
     */
    @Override
    public void exitVariableDeclaratorList( @NotNull Java8Parser.VariableDeclaratorListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableDeclarator() != null ) {
            for ( int i = 0; i < ctx.variableDeclarator().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.variableDeclarator() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * variableDeclarator
     * 	:	variableDeclaratorId ('=' variableInitializer)?
     * 	;
     */
    @Override
    public void exitVariableDeclarator( @NotNull Java8Parser.VariableDeclaratorContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableInitializer() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableDeclaratorId() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * variableDeclaratorId
     * 	:	Identifier dims?
     * 	;
     */
    @Override
    public void exitVariableDeclaratorId( @NotNull Java8Parser.VariableDeclaratorIdContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.dims() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * unannPrimitiveType
     * 	:	numericType
     * 	|	'boolean'
     * 	;
     */
    @Override
    public void exitUnannPrimitiveType( @NotNull Java8Parser.UnannPrimitiveTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.numericType() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * unannClassOrInterfaceType
     * 	:	(	unannClassType_lfno_unannClassOrInterfaceType
     * 		|	unannInterfaceType_lfno_unannClassOrInterfaceType
     * 		)
     * 		(	unannClassType_lf_unannClassOrInterfaceType
     * 		|	unannInterfaceType_lf_unannClassOrInterfaceType
     * 		)*
     * 	;
     */
    @Override
    public void exitUnannClassOrInterfaceType( @NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        int size = 0;
        if ( ctx.unannClassType_lf_unannClassOrInterfaceType() != null ) {
            size = ctx.unannClassType_lf_unannClassOrInterfaceType().size();
        }


        if ( ctx.unannInterfaceType_lf_unannClassOrInterfaceType() != null ) {
            size += ctx.unannInterfaceType_lf_unannClassOrInterfaceType().size();
        }


        for ( int i = 0; i < size; i++ ) {
            parent.addChild( stack.pop() );
        }
        parent.addChild( stack.pop() );    // unannClassType_lfno_unannClassOrInterfaceType or unannInterfaceType_lfno_unannClassOrInterfaceType
        stack.push( parent );
    }


    /**
     * unannClassType
     * 	:	Identifier typeArguments?
     * 	|	unannClassOrInterfaceType '.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitUnannClassType( @NotNull Java8Parser.UnannClassTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }
        else {
            if ( ctx.annotationIdentifier() != null ) {
                parent.addChild( stack.pop() );
            }


            if ( ctx.unannClassOrInterfaceType() != null ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * unannClassType_lf_unannClassOrInterfaceType
     * 	:	'.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitUnannClassType_lf_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotationIdentifier() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * unannClassType_lfno_unannClassOrInterfaceType
     * 	:	Identifier typeArguments?
     * 	;
     */
    @Override
    public void exitUnannClassType_lfno_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * unannTypeVariable
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitUnannTypeVariable( @NotNull Java8Parser.UnannTypeVariableContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * unannArrayType
     * 	:	unannPrimitiveType dims
     * 	|	unannClassOrInterfaceType dims
     * 	|	unannTypeVariable dims
     * 	;
     */
    @Override
    public void exitUnannArrayType( @NotNull Java8Parser.UnannArrayTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.dims() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannPrimitiveType() != null || ctx.unannClassOrInterfaceType() != null || ctx.unannTypeVariable() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * methodDeclaration
     * 	:	methodModifiers methodHeader methodBody
     * 	;
     */
    @Override
    public void exitMethodDeclaration( @NotNull Java8Parser.MethodDeclarationContext ctx ) {
        TigerNode parent = new MethodNode();
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.methodBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.methodHeader() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.methodModifiers() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * methodModifiers
     *     :   methodModifier*
     *     ;
     */
    @Override
    public void exitMethodModifiers( @NotNull Java8Parser.MethodModifiersContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.methodModifier() != null ) {
            for ( int i = 0; i < ctx.methodModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * methodModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'protected'
     * 	|	'private'
     * 	|	'abstract'
     * 	|	'static'
     * 	|	'final'
     * 	|	'synchronized'
     * 	|	'native'
     * 	|	'strictfp'
     * 	;
     */
    @Override
    public void exitMethodModifier( @NotNull Java8Parser.MethodModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * methodHeader
     * 	:	result methodDeclarator throws_?
     * 	|	typeParameters annotation* result methodDeclarator throws_?
     * 	;
     */
    @Override
    public void exitMethodHeader( @NotNull Java8Parser.MethodHeaderContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.throws_() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.methodDeclarator() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.result() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotation() != null ) {
            for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.typeParameters() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * result
     * 	:	unannType
     * 	|	'void'
     * 	;
     */
    @Override
    public void exitResult( @NotNull Java8Parser.ResultContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * methodDeclarator
     * 	:	Identifier '(' formalParameterList? ')' dims?
     * 	;
     */
    @Override
    public void exitMethodDeclarator( @NotNull Java8Parser.MethodDeclaratorContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.dims() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.formalParameterList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * formalParameterList
     * 	:	formalParameters ',' lastFormalParameter
     * 	|	lastFormalParameter
     * 	;
     */
    @Override
    public void exitFormalParameterList( @NotNull Java8Parser.FormalParameterListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.lastFormalParameter() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.formalParameters() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * formalParameters
     * 	:	formalParameter (',' formalParameter)*
     * 	|	receiverParameter (',' formalParameter)*
     * 	;
     */
    @Override
    public void exitFormalParameters( @NotNull Java8Parser.FormalParametersContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.formalParameter() != null ) {
            for ( int i = 0; i < ctx.formalParameter().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.receiverParameter() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * formalParameter
     * 	:	variableModifier* unannType variableDeclaratorId
     * 	;
     */
    @Override
    public void exitFormalParameter( @NotNull Java8Parser.FormalParameterContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableDeclaratorId() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableModifier() != null ) {
            for ( int i = 0; i < ctx.variableModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * variableModifier
     * 	:	annotation
     * 	|	'final'
     * 	;
     */
    @Override
    public void exitVariableModifier( @NotNull Java8Parser.VariableModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * lastFormalParameter
     * 	:	variableModifier* unannType annotation* '...' variableDeclaratorId
     * 	|	formalParameter
     * 	;
     */
    @Override
    public void exitLastFormalParameter( @NotNull Java8Parser.LastFormalParameterContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.formalParameter() != null ) {
            parent.addChild( stack.pop() );
        }
        else {
            if ( ctx.variableDeclaratorId() != null ) {
                parent.addChild( stack.pop() );
            }


            if ( ctx.annotation() != null ) {
                for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                    parent.addChild( stack.pop() );
                }
            }


            if ( ctx.unannType() != null ) {
                parent.addChild( stack.pop() );
            }


            if ( ctx.variableModifier() != null ) {
                for ( int i = 0; i < ctx.variableModifier().size(); i++ ) {
                    parent.addChild( stack.pop() );
                }
            }
        }


        stack.push( parent );
    }


    /**
     * receiverParameter
     * 	:	annotation* unannType (Identifier '.')? 'this'
     * 	;
     */
    @Override
    public void exitReceiverParameter( @NotNull Java8Parser.ReceiverParameterContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotation() != null ) {
            for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * throws_
     * 	:	'throws' exceptionTypeList
     * 	;
     */
    @Override
    public void exitThrows_( @NotNull Java8Parser.Throws_Context ctx ) {
        TigerNode parent = new TigerNode( "throws" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.exceptionTypeList() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * exceptionTypeList
     * 	:	exceptionType (',' exceptionType)*
     * 	;
     */
    @Override
    public void exitExceptionTypeList( @NotNull Java8Parser.ExceptionTypeListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.exceptionType() != null ) {
            for ( int i = 0; i < ctx.exceptionType().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * constructorDeclaration
     * 	:	constructorModifiers constructorDeclarator throws_? constructorBody
     * 	;
     */
    @Override
    public void exitConstructorDeclaration( @NotNull Java8Parser.ConstructorDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( "constructor" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.constructorBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.throws_() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.constructorDeclarator() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.constructorModifiers() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * constructorModifiers
     *     :   constructorModifier*
     *     ;
     */
    @Override
    public void exitConstructorModifiers( @NotNull Java8Parser.ConstructorModifiersContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.constructorModifier() != null ) {
            for ( int i = 0; i < ctx.constructorModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * constructorModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'protected'
     * 	|	'private'
     * 	;
     */
    @Override
    public void exitConstructorModifier( @NotNull Java8Parser.ConstructorModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * constructorDeclarator
     * 	:	typeParameters? simpleTypeName '(' formalParameterList? ')'
     * 	;
     */
    @Override
    public void exitConstructorDeclarator( @NotNull Java8Parser.ConstructorDeclaratorContext ctx ) {
        TigerNode parent = new TigerNode( "constructorBody" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.formalParameterList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.simpleTypeName() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeParameters() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * simpleTypeName
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitSimpleTypeName( @NotNull Java8Parser.SimpleTypeNameContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * constructorBody
     * 	:	'{' explicitConstructorInvocation? blockStatements? '}'
     * 	;
     */
    @Override
    public void exitConstructorBody( @NotNull Java8Parser.ConstructorBodyContext ctx ) {
        TigerNode parent = new TigerNode( "constructorBody" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.blockStatements() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.explicitConstructorInvocation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * explicitConstructorInvocation
     * 	:	typeArguments? 'this' '(' argumentList? ')' ';'
     * 	|	typeArguments? SUPER '(' argumentList? ')' ';'
     * 	|	expressionName '.' typeArguments? SUPER '(' argumentList? ')' ';'
     * 	|	primary '.' typeArguments? SUPER '(' argumentList? ')' ';'
     * 	;
     */
    @Override
    public void exitExplicitConstructorInvocation( @NotNull Java8Parser.ExplicitConstructorInvocationContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expressionName() != null || ctx.primary() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * enumDeclaration
     * 	:	classModifiers 'enum' Identifier superinterfaces? enumBody
     * 	;
     */
    @Override
    public void exitEnumDeclaration( @NotNull Java8Parser.EnumDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( "enumBody" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.enumBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.superinterfaces() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.classModifiers() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * enumBody
     * 	:	'{' enumConstantList? COMMA? enumBodyDeclarations? '}'
     * 	;
     */
    @Override
    public void exitEnumBody( @NotNull Java8Parser.EnumBodyContext ctx ) {
        TigerNode parent = new TigerNode( "enumBody" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.enumBodyDeclarations() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.enumConstantList() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * enumConstantList
     * 	:	enumConstant (',' enumConstant)*
     * 	;
     */
    @Override
    public void exitEnumConstantList( @NotNull Java8Parser.EnumConstantListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.enumConstant() != null ) {
            for ( int i = 0; i < ctx.enumConstant().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * enumConstant
     * 	:	enumConstantModifier* Identifier ('(' argumentList? ')')? classBody?
     * 	;
     */
    @Override
    public void exitEnumConstant( @NotNull Java8Parser.EnumConstantContext ctx ) {
        TigerNode parent = new TigerNode( "interface" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.enumConstantModifier() != null ) {
            for ( int i = 0; i < ctx.enumConstantModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * enumBodyDeclarations
     * 	:	';' classBodyDeclaration*
     * 	;
     */
    @Override
    public void exitEnumBodyDeclarations( @NotNull Java8Parser.EnumBodyDeclarationsContext ctx ) {
        TigerNode parent = new TigerNode( "enumBodyDeclarations" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classBodyDeclaration() != null ) {
            for ( int i = 0; i < ctx.classBodyDeclaration().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /* =============================================================================
     * Productions from §9 (Interfaces)
     */

    /**
     * normalInterfaceDeclaration
     * 	:	interfaceModifiers 'interface' Identifier typeParameters? extendsInterfaces? interfaceBody
     * 	;
     */
    @Override
    public void exitNormalInterfaceDeclaration( @NotNull Java8Parser.NormalInterfaceDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( "interface" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.interfaceBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.extendsInterfaces() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeParameters() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.interfaceModifiers() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * interfaceModifiers
     *     : interfaceModifier*
     *     ;
     */
    @Override
    public void exitInterfaceModifiers( @NotNull Java8Parser.InterfaceModifiersContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.interfaceModifier() != null ) {
            for ( int i = 0; i < ctx.interfaceModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * interfaceModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'protected'
     * 	|	'private'
     * 	|	'abstract'
     * 	|	'static'
     * 	|	'strictfp'
     * 	;
     */
    @Override
    public void exitInterfaceModifier( @NotNull Java8Parser.InterfaceModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * extendsInterfaces
     * 	:	'extends' interfaceTypeList
     * 	;
     */
    @Override
    public void exitExtendsInterfaces( @NotNull Java8Parser.ExtendsInterfacesContext ctx ) {
        TigerNode parent = new TigerNode( "extends" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.interfaceTypeList() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * interfaceBody
     * 	:	'{' interfaceMemberDeclaration* '}'
     * 	;
     */
    @Override
    public void exitInterfaceBody( @NotNull Java8Parser.InterfaceBodyContext ctx ) {
        TigerNode parent = new TigerNode( "block" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.interfaceMemberDeclaration() != null ) {
            for ( int i = 0; i < ctx.interfaceMemberDeclaration().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * constantDeclaration
     * 	:	constantModifier* unannType variableDeclaratorList ';'
     * 	;
     */
    @Override
    public void exitConstantDeclaration( @NotNull Java8Parser.ConstantDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableDeclaratorList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.constantModifier() != null ) {
            for ( int i = 0; i < ctx.constantModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * constantModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'static'
     * 	|	'final'
     * 	;
     */
    @Override
    public void exitConstantModifier( @NotNull Java8Parser.ConstantModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * interfaceMethodDeclaration
     * 	:	interfaceMethodModifier* methodHeader methodBody
     * 	;
     */
    @Override
    public void exitInterfaceMethodDeclaration( @NotNull Java8Parser.InterfaceMethodDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( "method" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.methodBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.methodHeader() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.interfaceMethodModifier() != null ) {
            for ( int i = 0; i < ctx.interfaceMethodModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * interfaceMethodModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'abstract'
     * 	|	'default'
     * 	|	'static'
     * 	|	'strictfp'
     * 	;
     */
    @Override
    public void exitInterfaceMethodModifier( @NotNull Java8Parser.InterfaceMethodModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * annotationTypeDeclaration
     * 	:	interfaceModifier* '@' 'interface' Identifier annotationTypeBody
     * 	;
     */
    @Override
    public void exitAnnotationTypeDeclaration( @NotNull Java8Parser.AnnotationTypeDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( "interface" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotationTypeBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.interfaceModifier() != null ) {
            for ( int i = 0; i < ctx.interfaceModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * annotationTypeBody
     * 	:	'{' annotationTypeMemberDeclaration* '}'
     * 	;
     */
    @Override
    public void exitAnnotationTypeBody( @NotNull Java8Parser.AnnotationTypeBodyContext ctx ) {
        TigerNode parent = new TigerNode( "annotationBody" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotationTypeMemberDeclaration() != null ) {
            for ( int i = 0; i < ctx.annotationTypeMemberDeclaration().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * annotationTypeElementDeclaration
     * 	:	annotationTypeElementModifier* unannType Identifier '(' ')' dims? defaultValue? ';'
     * 	;
     */
    @Override
    public void exitAnnotationTypeElementDeclaration( @NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.defaultValue() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.dims() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotationTypeElementModifier() != null ) {
            for ( int i = 0; i < ctx.annotationTypeElementModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * annotationTypeElementModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'abstract'
     * 	;
     */
    @Override
    public void exitAnnotationTypeElementModifier( @NotNull Java8Parser.AnnotationTypeElementModifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * defaultValue
     * 	:	'default' elementValue
     * 	;
     */
    @Override
    public void exitDefaultValue( @NotNull Java8Parser.DefaultValueContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.elementValue() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * annotationIdentifier
     *     :   annotation* Identifier
     *     ;
     */
    @Override
    public void exitAnnotationIdentifier( @NotNull Java8Parser.AnnotationIdentifierContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.annotation() != null ) {
            for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * annotationDim
     *     : annotation* squareBrackets
     *     ;
     */
    @Override
    public void exitAnnotationDim( @NotNull Java8Parser.AnnotationDimContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.annotation() != null ) {
            for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * normalAnnotation
     * 	:	'@' typeName '(' elementValuePairList? ')'
     * 	;
     */
    @Override
    public void exitNormalAnnotation( @NotNull Java8Parser.NormalAnnotationContext ctx ) {
        TigerNode parent = null;
        TigerNode evp = null;
        if ( ctx.elementValuePairList() != null ) {
            evp = stack.pop();
        }


        if ( ctx.typeName() != null ) {
            TigerNode tn = stack.pop();
            parent = new AnnotationNode( tn.getName() );
            setStartLocation( parent, ctx );
            setEndLocation( parent, ctx );
        }


        if ( parent != null ) {
            parent.addChildren( evp.getChildren() );
        }


        stack.push( parent );
    }


    /**
     * elementValuePairList
     * 	:	elementValuePair (',' elementValuePair)*
     * 	;
     */
    @Override
    public void exitElementValuePairList( @NotNull Java8Parser.ElementValuePairListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.elementValuePair() != null ) {
            for ( int i = 0; i < ctx.elementValuePair().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * elementValuePair
     * 	:	Identifier '=' elementValue
     * 	;
     */
    @Override
    public void exitElementValuePair( @NotNull Java8Parser.ElementValuePairContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.elementValue() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * elementValueList
     * 	:	elementValue (',' elementValue)*
     * 	;
     */
    @Override
    public void exitElementValueList( @NotNull Java8Parser.ElementValueListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.elementValue() != null ) {
            for ( int i = 0; i < ctx.elementValue().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * markerAnnotation
     * 	:	'@' typeName
     * 	;
     */
    @Override
    public void exitMarkerAnnotation( @NotNull Java8Parser.MarkerAnnotationContext ctx ) {
        TigerNode parent = null;
        if ( ctx.typeName() != null ) {
            TigerNode tn = stack.pop();
            parent = new AnnotationNode( tn.getName() );
            setStartLocation( parent, ctx );
            setEndLocation( parent, ctx );
        }


        stack.push( parent );
    }


    /**
     * singleElementAnnotation
     * 	:	'@' typeName '(' elementValue ')'
     * 	;
     */
    @Override
    public void exitSingleElementAnnotation( @NotNull Java8Parser.SingleElementAnnotationContext ctx ) {
        TigerNode parent = null;
        TigerNode ev = null;
        if ( ctx.elementValue() != null ) {
            ev = stack.pop();
        }


        if ( ctx.typeName() != null ) {
            TigerNode tn = stack.pop();
            parent = new AnnotationNode( tn.getName() );
            setStartLocation( parent, ctx );
            setEndLocation( parent, ctx );
        }


        if ( parent != null ) {
            parent.addChild( ev );
        }


        stack.push( parent );
    }


    /* =============================================================================
     * Productions from §10 (Arrays)
     */

    /**
     * variableInitializerList
     * 	:	variableInitializer (',' variableInitializer)*
     * 	;
     */
    @Override
    public void exitVariableInitializerList( @NotNull Java8Parser.VariableInitializerListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableInitializer() != null ) {
            for ( int i = 0; i < ctx.variableInitializer().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /* =============================================================================
     * Productions from §14 (Blocks and Statements)
     */

    /**
     * blockStatements
     * 	:	blockStatement blockStatement*
     * 	;
     */
    @Override
    public void exitBlockStatements( @NotNull Java8Parser.BlockStatementsContext ctx ) {
        TigerNode parent = new TigerNode( "block" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.blockStatement() != null ) {
            for ( int i = 0; i < ctx.blockStatement().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * localVariableDeclaration
     * 	:	variableModifier* unannType variableDeclaratorList
     * 	;
     */
    @Override
    public void exitLocalVariableDeclaration( @NotNull Java8Parser.LocalVariableDeclarationContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableDeclaratorList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableModifier() != null ) {
            for ( int i = 0; i < ctx.variableModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * labeledStatement
     * 	:	Identifier ':' statement
     * 	;
     */
    @Override
    public void exitLabeledStatement( @NotNull Java8Parser.LabeledStatementContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statement() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * labeledStatementNoShortIf
     * 	:	Identifier ':' statementNoShortIf
     * 	;
     */
    @Override
    public void exitLabeledStatementNoShortIf( @NotNull Java8Parser.LabeledStatementNoShortIfContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statementNoShortIf() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * ifThenStatement
     * 	:	'if' '(' expression ')' statement
     * 	;
     */
    @Override
    public void exitIfThenStatement( @NotNull Java8Parser.IfThenStatementContext ctx ) {
        TigerNode parent = new TigerNode( "for" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statement() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * ifThenElseStatement
     * 	:	'if' '(' expression ')' statementNoShortIf 'else' statement
     * 	;
     */
    @Override
    public void exitIfThenElseStatement( @NotNull Java8Parser.IfThenElseStatementContext ctx ) {
        TigerNode parent = new TigerNode( "for" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statement() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.statementNoShortIf() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * ifThenElseStatementNoShortIf
     * 	:	'if' '(' expression ')' statementNoShortIf 'else' statementNoShortIf
     * 	;
     */
    @Override
    public void exitIfThenElseStatementNoShortIf( @NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx ) {
        TigerNode parent = new TigerNode( "for" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statementNoShortIf() != null ) {
            parent.addChild( stack.pop() );
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * assertStatement
     * 	:	'assert' expression ';'
     * 	|	'assert' expression ':' expression ';'
     * 	;
     */
    @Override
    public void exitAssertStatement( @NotNull Java8Parser.AssertStatementContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.expression() != null ) {
            for ( int i = 0; i < ctx.expression().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * switchStatement
     * 	:	'switch' '(' expression ')' switchBlock
     * 	;
     */
    @Override
    public void exitSwitchStatement( @NotNull Java8Parser.SwitchStatementContext ctx ) {
        TigerNode parent = new TigerNode( "switch" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.switchBlock() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * switchBlock
     * 	:	'{' switchBlockStatementGroup* switchLabel* '}'
     * 	;
     */
    @Override
    public void exitSwitchBlock( @NotNull Java8Parser.SwitchBlockContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.switchLabel() != null ) {
            for ( int i = 0; i < ctx.switchLabel().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.switchBlockStatementGroup() != null ) {
            for ( int i = 0; i < ctx.switchBlockStatementGroup().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * switchBlockStatementGroup
     * 	:	switchLabels blockStatements
     * 	;
     */
    @Override
    public void exitSwitchBlockStatementGroup( @NotNull Java8Parser.SwitchBlockStatementGroupContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.blockStatements() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.switchLabels() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * switchLabels
     * 	:	switchLabel switchLabel*
     * 	;
     */
    @Override
    public void exitSwitchLabels( @NotNull Java8Parser.SwitchLabelsContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.switchLabel() != null ) {
            for ( int i = 0; i < ctx.switchLabel().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * switchLabel
     * 	:	'case' constantExpression ':'
     * 	|	'case' enumConstantName ':'
     * 	|	'default' ':'
     * 	;
     */
    @Override
    public void exitSwitchLabel( @NotNull Java8Parser.SwitchLabelContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.constantExpression() != null || ctx.enumConstantName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * enumConstantName
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitEnumConstantName( @NotNull Java8Parser.EnumConstantNameContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * whileStatement
     * 	:	'while' '(' expression ')' statement
     * 	;
     */
    @Override
    public void exitWhileStatement( @NotNull Java8Parser.WhileStatementContext ctx ) {
        TigerNode parent = new TigerNode( "while" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statement() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * whileStatementNoShortIf
     * 	:	'while' '(' expression ')' statementNoShortIf
     * 	;
     */
    @Override
    public void exitWhileStatementNoShortIf( @NotNull Java8Parser.WhileStatementNoShortIfContext ctx ) {
        TigerNode parent = new TigerNode( "while" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statementNoShortIf() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * doStatement
     * 	:	'do' statement 'while' '(' expression ')' ';'
     * 	;
     */
    @Override
    public void exitDoStatement( @NotNull Java8Parser.DoStatementContext ctx ) {
        TigerNode parent = new TigerNode( "do" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.statement() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * basicForStatement
     * 	:	'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
     * 	;
     */
    @Override
    public void exitBasicForStatement( @NotNull Java8Parser.BasicForStatementContext ctx ) {
        TigerNode parent = new TigerNode( "for" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statement() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.forUpdate() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.forInit() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * basicForStatementNoShortIf
     * 	:	'for' '(' forInit? ';' expression? ';' forUpdate? ')' statementNoShortIf
     * 	;
     */
    @Override
    public void exitBasicForStatementNoShortIf( @NotNull Java8Parser.BasicForStatementNoShortIfContext ctx ) {
        TigerNode parent = new TigerNode( "for" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statementNoShortIf() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.forUpdate() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.forInit() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * statementExpressionList
     * 	:	statementExpression (',' statementExpression)*
     * 	;
     */
    @Override
    public void exitStatementExpressionList( @NotNull Java8Parser.StatementExpressionListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statementExpression() != null ) {
            for ( int i = 0; i < ctx.statementExpression().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * enhancedForStatement
     * 	:	'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statement
     * 	;
     */
    @Override
    public void exitEnhancedForStatement( @NotNull Java8Parser.EnhancedForStatementContext ctx ) {
        TigerNode parent = new TigerNode( "for" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statement() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableDeclaratorId() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableModifier() != null ) {
            for ( int i = 0; i < ctx.variableModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * enhancedForStatementNoShortIf
     * 	:	'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statementNoShortIf
     * 	;
     */
    @Override
    public void exitEnhancedForStatementNoShortIf( @NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx ) {
        TigerNode parent = new TigerNode( "for" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.statementNoShortIf() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableDeclaratorId() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableModifier() != null ) {
            for ( int i = 0; i < ctx.variableModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * breakStatement
     * 	:	'break' Identifier? ';'
     * 	;
     */
    @Override
    public void exitBreakStatement( @NotNull Java8Parser.BreakStatementContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * continueStatement
     * 	:	'continue' Identifier? ';'
     * 	;
     */
    @Override
    public void exitContinueStatement( @NotNull Java8Parser.ContinueStatementContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * returnStatement
     * 	:	'return' expression? ';'
     * 	;
     */
    @Override
    public void exitReturnStatement( @NotNull Java8Parser.ReturnStatementContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * throwStatement
     * 	:	'throw' expression ';'
     * 	;
     */
    @Override
    public void exitThrowStatement( @NotNull Java8Parser.ThrowStatementContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * synchronizedStatement
     * 	:	'synchronized' '(' expression ')' block
     * 	;
     */
    @Override
    public void exitSynchronizedStatement( @NotNull Java8Parser.SynchronizedStatementContext ctx ) {
        TigerNode parent = new TigerNode( "synchronized" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.block() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * tryStatement
     * 	:	'try' block catches
     * 	|	'try' block catches? finally_
     * 	|	tryWithResourcesStatement
     * 	;
     */
    @Override
    public void exitTryStatement( @NotNull Java8Parser.TryStatementContext ctx ) {
        TigerNode parent = new TigerNode( "try" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.finally_() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.catches() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.block() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.tryWithResourcesStatement() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * catches
     * 	:	catchClause catchClause*
     * 	;
     */
    @Override
    public void exitCatches( @NotNull Java8Parser.CatchesContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.catchClause() != null ) {
            for ( int i = 0; i < ctx.catchClause().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * catchClause
     * 	:	'catch' '(' catchFormalParameter ')' block
     * 	;
     */
    @Override
    public void exitCatchClause( @NotNull Java8Parser.CatchClauseContext ctx ) {
        TigerNode parent = new TigerNode( "catch" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.block() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.catchFormalParameter() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * catchFormalParameter
     * 	:	variableModifier* catchType variableDeclaratorId
     * 	;
     */
    @Override
    public void exitCatchFormalParameter( @NotNull Java8Parser.CatchFormalParameterContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.variableDeclaratorId() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.catchType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableModifier() != null ) {
            for ( int i = 0; i < ctx.variableModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * catchType
     * 	:	unannClassType ('|' classType)*
     * 	;
     */
    @Override
    public void exitCatchType( @NotNull Java8Parser.CatchTypeContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classType() != null ) {
            for ( int i = 0; i < ctx.classType().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.unannClassType() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * finally_
     * 	:	'finally' block
     * 	;
     */
    @Override
    public void exitFinally_( @NotNull Java8Parser.Finally_Context ctx ) {
        TigerNode parent = new TigerNode( "finally" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.block() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * tryWithResourcesStatement
     * 	:	'try' resourceSpecification block catches? finally_?
     * 	;
     */
    @Override
    public void exitTryWithResourcesStatement( @NotNull Java8Parser.TryWithResourcesStatementContext ctx ) {
        TigerNode parent = new TigerNode( "try" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.finally_() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.catches() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.block() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.resourceSpecification() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * resourceList
     * 	:	resource (';' resource)*
     * 	;
     */
    @Override
    public void exitResourceList( @NotNull Java8Parser.ResourceListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.resource() != null ) {
            for ( int i = 0; i < ctx.resource().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * resource
     * 	:	variableModifier* unannType variableDeclaratorId '=' expression
     * 	;
     */
    @Override
    public void exitResource( @NotNull Java8Parser.ResourceContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableDeclaratorId() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.unannType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.variableModifier() != null ) {
            for ( int i = 0; i < ctx.variableModifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /* =============================================================================
     * Productions from §15 (Expressions)
     */

    /**
     * primary
     * 	:	(	primaryNoNewArray_lf_primary
     * 		|	arrayCreationExpression
     * 		)
     * 		(	primaryNoNewArray_lf_primary
     * 		)*
     * 	;
     */
    @Override
    public void exitPrimary( @NotNull Java8Parser.PrimaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        int size = 0;
        if ( ctx.primaryNoNewArray_lf_primary() != null ) {
            size = ctx.primaryNoNewArray_lf_primary().size();
        }


        for ( int i = 0; i < size; i++ ) {
            parent.addChild( stack.pop() );
        }
        parent.addChild( stack.pop() );    // primaryNoNewArray_lf_primary or arrayCreationExpression
        stack.push( parent );
    }


    /**
     * classInstanceCreationExpression
     * 	:	'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	expressionName '.' 'new' typeArguments?          annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	primary        '.' 'new' typeArguments?          annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override
    public void exitClassInstanceCreationExpression( @NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx ) {
        TigerNode parent = new TigerNode( "classInstanceCreationExpression" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeArgumentsOrDiamond() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotationIdentifier() != null ) {
            for ( int i = 0; i < ctx.annotationIdentifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expressionName() != null || ctx.primary() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * classInstanceCreationExpression_lf_primary
     * 	:	'.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override
    public void exitClassInstanceCreationExpression_lf_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx ) {
        TigerNode parent = new TigerNode( "classInstanceCreationExpression_lf_primary" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeArgumentsOrDiamond() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotationIdentifier() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * classInstanceCreationExpression_lfno_primary
     * 	:	'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	expressionName '.' 'new' typeArguments?          annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override
    public void exitClassInstanceCreationExpression_lfno_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx ) {
        TigerNode parent = new TigerNode( "classInstanceCreationExpression" );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.classBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.typeArgumentsOrDiamond() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotationIdentifier() != null ) {
            for ( int i = 0; i < ctx.annotationIdentifier().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expressionName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * fieldAccess
     * 	:	primary '.' Identifier
     * 	|	SUPER '.' Identifier
     * 	|	typeName '.' SUPER '.' Identifier
     * 	;
     */
    @Override
    public void exitFieldAccess( @NotNull Java8Parser.FieldAccessContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.primary() != null || ctx.typeName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * fieldAccess_lf_primary
     * 	:	'.' Identifier
     * 	;
     */
    @Override
    public void exitFieldAccess_lf_primary( @NotNull Java8Parser.FieldAccess_lf_primaryContext ctx ) {
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            stack.push( child );
        }
    }


    /**
     * fieldAccess_lfno_primary
     * 	:	SUPER '.' Identifier
     * 	|	typeName '.' SUPER '.' Identifier
     * 	;
     */
    @Override
    public void exitFieldAccess_lfno_primary( @NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeName() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * arrayAccess
     * 	:	(	expressionName '[' expression ']'
     * 		|	primaryNoNewArray_lfno_arrayAccess '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lf_arrayAccess '[' expression ']'
     * 		)*
     * 	;
     */
    @Override
    public void exitArrayAccess( @NotNull Java8Parser.ArrayAccessContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.primaryNoNewArray_lf_arrayAccess() != null ) {
            for ( int i = 0; i < ctx.primaryNoNewArray_lf_arrayAccess().size(); i++ ) {
                parent.addChild( stack.pop() );    // expression
                parent.addChild( stack.pop() );    // primaryNoNewArray_lf_arrayAccess
            }
        }


        parent.addChild( stack.pop() );    // expression
        parent.addChild( stack.pop() );    // expressionName or primaryNoNewArray_lfno_arrayAccess

        stack.push( parent );
    }


    /**
     * arrayAccess_lf_primary
     * 	:	(	primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary '[' expression ']'
     * 		)*
     * 	;
     */
    @Override
    public void exitArrayAccess_lf_primary( @NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary() != null ) {
            for ( int i = 0; i < ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary().size(); i++ ) {
                parent.addChild( stack.pop() );    // expression
                parent.addChild( stack.pop() );    // primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary
            }
        }


        parent.addChild( stack.pop() );    // expression
        parent.addChild( stack.pop() );    // primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary

        stack.push( parent );
    }


    /**
     * arrayAccess_lfno_primary
     * 	:	(	expressionName '[' expression ']'
     * 		|	primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']'
     * 		)*
     * 	;
     */
    @Override
    public void exitArrayAccess_lfno_primary( @NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary() != null ) {
            for ( int i = 0; i < ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary().size(); i++ ) {
                parent.addChild( stack.pop() );    // expression
                parent.addChild( stack.pop() );    // primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary
            }
        }


        parent.addChild( stack.pop() );    // expression
        parent.addChild( stack.pop() );    // expressionName or primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary

        stack.push( parent );
    }


    /**
     * methodInvocation
     * 	:	methodName                                       '(' argumentList? ')'
     * 	|	typeName '.'           typeArguments? Identifier '(' argumentList? ')'
     * 	|	expressionName '.'     typeArguments? Identifier '(' argumentList? ')'
     * 	|	primary '.'            typeArguments? Identifier '(' argumentList? ')'
     * 	|	SUPER '.'              typeArguments? Identifier '(' argumentList? ')'
     * 	|	typeName '.' SUPER '.' typeArguments? Identifier '(' argumentList? ')'
     * 	;
     */
    @Override
    public void exitMethodInvocation( @NotNull Java8Parser.MethodInvocationContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.SUPER() == null ) {
            parent.addChild( stack.pop() );    // methodName, typeName, etc
        }


        stack.push( parent );
    }


    /**
     * methodInvocation_lf_primary
     * 	:	'.' typeArguments? Identifier '(' argumentList? ')'
     * 	;
     */
    @Override
    public void exitMethodInvocation_lf_primary( @NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * methodInvocation_lfno_primary
     * 	:	methodName                                       '(' argumentList? ')'
     * 	|	typeName '.'           typeArguments? Identifier '(' argumentList? ')'
     * 	|	expressionName '.'     typeArguments? Identifier '(' argumentList? ')'
     * 	|	SUPER '.'              typeArguments? Identifier '(' argumentList? ')'
     * 	|	typeName '.' SUPER '.' typeArguments? Identifier '(' argumentList? ')'
     * 	;
     */
    @Override
    public void exitMethodInvocation_lfno_primary( @NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.argumentList() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeArguments() != null ) {
            for ( int i = 0; i < ctx.typeArguments().typeArgumentList().typeArgument().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.SUPER() == null ) {
            parent.addChild( stack.pop() );    // methodName, typeName, etc
        }


        stack.push( parent );
    }


    /**
     * argumentList
     * 	:	expression (',' expression)*
     * 	;
     */
    @Override
    public void exitArgumentList( @NotNull Java8Parser.ArgumentListContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.expression() != null ) {
            for ( int i = 0; i < ctx.expression().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * methodReference
     * 	:	expressionName     '::' typeArguments? Identifier
     * 	|	referenceType      '::' typeArguments? Identifier
     * 	|	primary            '::' typeArguments? Identifier
     * 	|	SUPER              '::' typeArguments? Identifier
     * 	|	typeName '.' SUPER '::' typeArguments? Identifier
     * 	|	classType          '::' typeArguments? 'new'
     * 	|	arrayType          '::' 'new'
     * 	;
     */
    @Override
    public void exitMethodReference( @NotNull Java8Parser.MethodReferenceContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeArguments() != null ) {
            for ( int i = 0; i < ctx.typeArguments().typeArgumentList().typeArgument().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        if ( ctx.SUPER() == null ) {
            parent.addChild( stack.pop() );    // expressonName, referenceType, etc
        }


        stack.push( parent );
    }


    /**
     * methodReference_lf_primary
     * 	:	'::' typeArguments? Identifier
     * 	;
     */
    @Override
    public void exitMethodReference_lf_primary( @NotNull Java8Parser.MethodReference_lf_primaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeArguments() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * methodReference_lfno_primary
     * 	:	expressionName     '::' typeArguments? Identifier
     * 	|	referenceType      '::' typeArguments? Identifier
     * 	|	SUPER              '::' typeArguments? Identifier
     * 	|	typeName '.' SUPER '::' typeArguments? Identifier
     * 	|	classType          '::' typeArguments? 'new'
     * 	|	arrayType          '::' 'new'
     * 	;
     */
    @Override
    public void exitMethodReference_lfno_primary( @NotNull Java8Parser.MethodReference_lfno_primaryContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.Identifier() != null ) {
            TigerNode child = new TigerNode( ctx.Identifier().getText() );
            setStartLocation( child, ctx.Identifier().getSymbol() );
            setEndLocation( child, ctx.Identifier().getSymbol() );
            parent.addChild( child );
        }


        if ( ctx.typeArguments() != null ) {
            for ( int i = 0; i < ctx.typeArguments().typeArgumentList().typeArgument().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        parent.addChild( stack.pop() );    // expressonName, referenceType, etc

        stack.push( parent );
    }


    /**
     * arrayCreationExpression
     * 	:	'new' primitiveType dimExprs dims?
     * 	|	'new' classOrInterfaceType dimExprs dims?
     * 	|	'new' primitiveType dims arrayInitializer
     * 	|	'new' classOrInterfaceType dims arrayInitializer
     * 	;
     */
    @Override
    public void exitArrayCreationExpression( @NotNull Java8Parser.ArrayCreationExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.arrayInitializer() != null ) {

            // last 2 choices
            parent.addChild( stack.pop() );    // arrayInitializer
            parent.addChild( stack.pop() );    // dims
        }
        else if ( ctx.dimExprs() != null ) {
            if ( ctx.dims() != null ) {
                parent.addChild( stack.pop() );    // dims?
            }


            parent.addChild( stack.pop() );    // dimExprs
        }


        parent.addChild( stack.pop() );    // primitiveType or classOrInterfaceType
        stack.push( parent );
    }


    /**
     * dimExprs
     * 	:	dimExpr dimExpr*
     * 	;
     */
    @Override
    public void exitDimExprs( @NotNull Java8Parser.DimExprsContext ctx ) {

        // individual dimExpr are already on the stack
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.dimExpr() != null ) {
            for ( int i = 0; i < ctx.dimExpr().size(); i++ ) {
                parent.addChild( stack.pop() );
            }
        }


        stack.push( parent );
    }


    /**
     * dimExpr
     * 	:	annotation* '[' expression ']'
     * 	;
     */
    @Override
    public void exitDimExpr( @NotNull Java8Parser.DimExprContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.annotation() != null ) {
            for ( int i = 0; i < ctx.annotation().size(); i++ ) {
                TigerNode annotation = stack.pop();
                parent.addChild( annotation );
            }
        }


        stack.push( parent );
    }


    /**
     * lambdaExpression
     * 	:	lambdaParameters '->' lambdaBody
     * 	;
     */
    @Override
    public void exitLambdaExpression( @NotNull Java8Parser.LambdaExpressionContext ctx ) {
        TigerNode parent = new TigerNode( "lambdaExpression" );
        if ( ctx.lambdaBody() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.lambdaParameters() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * lambdaParameters
     * 	:	Identifier
     * 	|	'(' formalParameterList? ')'
     * 	|	'(' inferredFormalParameterList ')'
     * 	;
     */
    @Override
    public void exitLambdaParameters( @NotNull Java8Parser.LambdaParametersContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.inferredFormalParameterList() != null ) {
            parent.addChild( stack.pop() );
        }
        else if ( ctx.formalParameterList() != null ) {
            parent.addChild( stack.pop() );
        }
        else if ( ctx.Identifier() != null ) {
            TerminalNode id = ctx.Identifier();
            TigerNode child = new TigerNode( id.getText() );
            setStartLocation( child, id.getSymbol() );
            setEndLocation( child, id.getSymbol() );
            parent.addChild( child );
        }


        stack.push( parent );
    }


    /**
     * inferredFormalParameterList
     * 	:	Identifier (',' Identifier)*
     * 	;
     */
    @Override
    public void exitInferredFormalParameterList( @NotNull Java8Parser.InferredFormalParameterListContext ctx ) {
        if ( ctx.Identifier() != null ) {
            TigerNode parent = new TigerNode( ctx.getText() );
            setStartLocation( parent, ctx );
            setEndLocation( parent, ctx );
            for ( TerminalNode id : ctx.Identifier() ) {
                TigerNode child = new TigerNode( id.getText() );
                setStartLocation( child, id.getSymbol() );
                setEndLocation( child, id.getSymbol() );
                parent.addChild( child );
            }
            stack.push( parent );
        }
    }


    /**
     * conditionalExpression
     * 	:	conditionalOrExpression
     * 	|	conditionalOrExpression '?' expression ':' conditionalExpression
     * 	|   conditionalOrExpression '?' expression ':' lambdaExpression
     * 	;
     */
    @Override
    public void exitConditionalExpression( @NotNull Java8Parser.ConditionalExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.conditionalExpression() != null || ctx.lambdaExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.expression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.conditionalOrExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * conditionalOrExpression
     * 	:	conditionalAndExpression
     * 	|	conditionalOrExpression '||' conditionalAndExpression
     * 	;
     */
    @Override
    public void exitConditionalOrExpression( @NotNull Java8Parser.ConditionalOrExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.conditionalAndExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.conditionalOrExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * conditionalAndExpression
     * 	:	inclusiveOrExpression
     * 	|	conditionalAndExpression '&&' inclusiveOrExpression
     * 	;
     */
    @Override
    public void exitConditionalAndExpression( @NotNull Java8Parser.ConditionalAndExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.inclusiveOrExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.conditionalAndExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * inclusiveOrExpression
     * 	:	exclusiveOrExpression
     * 	|	inclusiveOrExpression '|' exclusiveOrExpression
     * 	;
     */
    @Override
    public void exitInclusiveOrExpression( @NotNull Java8Parser.InclusiveOrExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.exclusiveOrExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.inclusiveOrExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * exclusiveOrExpression
     * 	:	andExpression
     * 	|	exclusiveOrExpression '^' andExpression
     * 	;
     */
    @Override
    public void exitExclusiveOrExpression( @NotNull Java8Parser.ExclusiveOrExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.andExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.exclusiveOrExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * andExpression
     * 	:	equalityExpression
     * 	|	andExpression '&' equalityExpression
     * 	;
     */
    @Override
    public void exitAndExpression( @NotNull Java8Parser.AndExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.equalityExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.andExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * equalityExpression
     * 	:	relationalExpression
     * 	|	equalityExpression '==' relationalExpression
     * 	|	equalityExpression '!=' relationalExpression
     * 	;
     */
    @Override
    public void exitEqualityExpression( @NotNull Java8Parser.EqualityExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.relationalExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.equalityExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * relationalExpression
     * 	:	shiftExpression
     * 	|	relationalExpression relationalOperator shiftExpression
     * 	|	relationalExpression relationalOperator referenceType
     * 	;
     */
    @Override
    public void exitRelationalExpression( @NotNull Java8Parser.RelationalExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.shiftExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.referenceType() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.relationalExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * shiftExpression
     * 	:	additiveExpression
     * 	|	shiftExpression shiftOperator additiveExpression
     * 	;
     */
    @Override
    public void exitShiftExpression( @NotNull Java8Parser.ShiftExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.additiveExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.shiftExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * additiveExpression
     * 	:	multiplicativeExpression
     * 	|	additiveExpression additiveOperator multiplicativeExpression
     * 	|	additiveExpression additiveOperator multiplicativeExpression
     * 	;
     */
    @Override
    public void exitAdditiveExpression( @NotNull Java8Parser.AdditiveExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.multiplicativeExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.additiveExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * multiplicativeExpression
     * 	:	unaryExpression
     * 	|	multiplicativeExpression multiplicativeOperator unaryExpression
     * 	;
     */
    @Override
    public void exitMultiplicativeExpression( @NotNull Java8Parser.MultiplicativeExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.unaryExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        if ( ctx.multiplicativeExpression() != null ) {
            parent.addChild( stack.pop() );
        }


        stack.push( parent );
    }


    /**
     * postfixExpression
     * 	:	(	primary
     * 		|	expressionName
     * 		)
     * 		(	postIncrementExpression_lf_postfixExpression
     * 		|	postDecrementExpression_lf_postfixExpression
     * 		)*
     * 	;
     */
    @Override
    public void exitPostfixExpression( @NotNull Java8Parser.PostfixExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        int size = 0;
        if ( ctx.postIncrementExpression_lf_postfixExpression() != null ) {
            size = ctx.postIncrementExpression_lf_postfixExpression().size();
        }


        if ( ctx.postDecrementExpression_lf_postfixExpression() != null ) {
            size += ctx.postDecrementExpression_lf_postfixExpression().size();
        }


        for ( int i = 0; i < size; i++ ) {
            parent.addChild( stack.pop() );
        }
        parent.addChild( stack.pop() );    // primary or expressionName
        stack.push( parent );
    }


    /**
     * castExpression
     * 	:	'(' primitiveType ')' unaryExpression
     * 	|	'(' referenceType additionalBound* ')' unaryExpressionNotPlusMinus
     * 	|	'(' referenceType additionalBound* ')' lambdaExpression
     * 	;
     */
    @Override
    public void exitCastExpression( @NotNull Java8Parser.CastExpressionContext ctx ) {
        TigerNode parent = new TigerNode( ctx.getText() );
        setStartLocation( parent, ctx );
        setEndLocation( parent, ctx );
        if ( ctx.unaryExpression() != null ) {

            // first choice
            parent.addChild( stack.pop() );    //unaryExpression
            parent.addChild( stack.pop() );    // primitiveType
        }
        else {

            // second and third choices
            if ( ctx.unaryExpressionNotPlusMinus() != null || ctx.lambdaExpression() != null ) {
                parent.addChild( stack.pop() );    // expression
                if ( ctx.additionalBound() != null ) {
                    for ( int i = 0; i < ctx.additionalBound().size(); i++ ) {
                        parent.addChild( stack.pop() );
                    }
                }


                parent.addChild( stack.pop() );    // referenceType
            }
        }


        stack.push( parent );
    }


    @Override
    public void visitErrorNode( @NotNull ErrorNode node ) {

        // TODO: add these errors to the error list
        System.out.println( "+++++ error node: " + node );
    }
}
