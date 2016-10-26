
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

    private void setStartLocation(TigerNode tn, ParserRuleContext ctx) {
        tn.setStartLocation(getStartLocation(ctx));   
    }
    
    private void setEndLocation(TigerNode tn, ParserRuleContext ctx) {
        tn.setEndLocation(getEndLocation(ctx));   
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
        stack.push(new TigerNode(ctx.getText()));
    }

    
    /**
     * floatingPointType
     * 	:	'float'
     * 	|	'double'
     * 	;
     */
    @Override
    public void exitFloatingPointType( @NotNull Java8Parser.FloatingPointTypeContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }


    @Override
    public void enterClassOrInterfaceType( @NotNull Java8Parser.ClassOrInterfaceTypeContext ctx ) {
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
    }


    @Override
    public void enterClassType( @NotNull Java8Parser.ClassTypeContext ctx ) {
    }

    
    /**
     * classType
     * 	:	annotationIdentifier typeArguments?
     * 	|	classOrInterfaceType '.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitClassType( @NotNull Java8Parser.ClassTypeContext ctx ) {
    }


    @Override
    public void enterClassType_lf_classOrInterfaceType( @NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx ) {
    }

    
    /**
     * classType_lf_classOrInterfaceType
     * 	:	'.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitClassType_lf_classOrInterfaceType( @NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void enterClassType_lfno_classOrInterfaceType( @NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx ) {
    }

    
    /**
     * classType_lfno_classOrInterfaceType
     * 	:	annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitClassType_lfno_classOrInterfaceType( @NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx ) {
    }


     @Override
    public void enterArrayType( @NotNull Java8Parser.ArrayTypeContext ctx ) {
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
    }


    @Override
    public void enterDims( @NotNull Java8Parser.DimsContext ctx ) {
    }

    
    /**
     * dims
     * 	:	annotationDim (annotationDim)*
     * 	;
     */
    @Override
    public void exitDims( @NotNull Java8Parser.DimsContext ctx ) {
    }


    @Override
    public void enterTypeParameter( @NotNull Java8Parser.TypeParameterContext ctx ) {
    }

    
    /**
     * typeParameter
     * 	:	typeParameterModifier* Identifier typeBound?
     * 	;
     * typeParameterModifier is annotation
     */
    @Override
    public void exitTypeParameter( @NotNull Java8Parser.TypeParameterContext ctx ) {
    }


    @Override
    public void enterTypeBound( @NotNull Java8Parser.TypeBoundContext ctx ) {
    }

    
    /**
     * typeBound
     * 	:	'extends' typeVariable
     * 	|	'extends' classOrInterfaceType additionalBound*
     * 	;
     */
    @Override
    public void exitTypeBound( @NotNull Java8Parser.TypeBoundContext ctx ) {
    }


    @Override
    public void enterAdditionalBound( @NotNull Java8Parser.AdditionalBoundContext ctx ) {
    }

    
    /**
     * additionalBound
     * 	:	'&' interfaceType
     * 	;
     */
    @Override
    public void exitAdditionalBound( @NotNull Java8Parser.AdditionalBoundContext ctx ) {
    }


    @Override
    public void enterTypeArguments( @NotNull Java8Parser.TypeArgumentsContext ctx ) {
    }

    
    /**
     * typeArguments
     * 	:	'<' typeArgumentList '>'
     * 	;
     */
    @Override
    public void exitTypeArguments( @NotNull Java8Parser.TypeArgumentsContext ctx ) {
    }


    @Override
    public void enterTypeArgumentList( @NotNull Java8Parser.TypeArgumentListContext ctx ) {
    }

    
    /**
     * typeArgumentList
     * 	:	typeArgument (',' typeArgument)*
     * 	;
     */
    @Override
    public void exitTypeArgumentList( @NotNull Java8Parser.TypeArgumentListContext ctx ) {
    }


    @Override
    public void enterWildcard( @NotNull Java8Parser.WildcardContext ctx ) {
    }

    
    /**
     * wildcard
     * 	:	annotation* '?' wildcardBounds?
     * 	;
     */
    @Override
    public void exitWildcard( @NotNull Java8Parser.WildcardContext ctx ) {
    }


    @Override
    public void enterWildcardBounds( @NotNull Java8Parser.WildcardBoundsContext ctx ) {
    }

    
    /**
     * wildcardBounds
     * 	:	'extends' referenceType
     * 	|	SUPER referenceType
     * 	;
     */
    @Override
    public void exitWildcardBounds( @NotNull Java8Parser.WildcardBoundsContext ctx ) {
    }


/* =============================================================================
 * Productions from §6 (Names)
 */
    @Override
    public void enterTypeName( @NotNull Java8Parser.TypeNameContext ctx ) {
    }

    
    /**
     * typeName
     * 	:	Identifier
     * 	|	packageOrTypeName '.' Identifier
     * 	;
     */
    @Override
    public void exitTypeName( @NotNull Java8Parser.TypeNameContext ctx ) {
    }


    @Override
    public void enterPackageOrTypeName( @NotNull Java8Parser.PackageOrTypeNameContext ctx ) {
    }

    
    /**
     * packageOrTypeName
     * 	:	Identifier
     * 	|	packageOrTypeName '.' Identifier
     * 	;
     */
    @Override
    public void exitPackageOrTypeName( @NotNull Java8Parser.PackageOrTypeNameContext ctx ) {
    }


    @Override
    public void enterExpressionName( @NotNull Java8Parser.ExpressionNameContext ctx ) {
    }

    
    /**
     * expressionName
     * 	:	Identifier
     * 	|	ambiguousName '.' Identifier
     * 	;
     */
    @Override
    public void exitExpressionName( @NotNull Java8Parser.ExpressionNameContext ctx ) {
    }


    @Override
    public void enterMethodName( @NotNull Java8Parser.MethodNameContext ctx ) {
    }

    
    /**
     * methodName
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitMethodName( @NotNull Java8Parser.MethodNameContext ctx ) {
    }


    @Override
    public void enterAmbiguousName( @NotNull Java8Parser.AmbiguousNameContext ctx ) {
    }

    
    /**
     * ambiguousName
     * 	:	Identifier
     * 	|	ambiguousName '.' Identifier
     * 	;
     */
    @Override
    public void exitAmbiguousName( @NotNull Java8Parser.AmbiguousNameContext ctx ) {
    }


/* =============================================================================
 * Productions from §7 (Packages)
 */
    @Override
    public void enterCompilationUnit( @NotNull Java8Parser.CompilationUnitContext ctx ) {
        cuNode = new CUNode();
    }

    
    /**
     * compilationUnit
     * 	:	packageDeclaration? importDeclaration* typeDeclaration* EOF
     * 	;
     */
    @Override
    public void exitCompilationUnit( @NotNull Java8Parser.CompilationUnitContext ctx ) {
        ImportNode importNode = cuNode.getImportNode();
        if (importNode != null) {
            int importsCount = importNode.getChildCount();
            if ( importsCount > 0 ) {
                importNode.setStartLocation( importNode.getChildAt( 0 ).getStartLocation() );
                importNode.setEndLocation( importNode.getChildAt( importsCount - 1 ).getEndLocation() );
            }
            else {
                cuNode.setImportNode( null );
            }
        }
        
        List typeDeclarations = ctx.typeDeclaration();
        if (typeDeclarations != null) {
            for (int i = 0; i < typeDeclarations.size() && !stack.isEmpty(); i++) {
                TigerNode child = stack.pop();
                if (child != null) {
                    cuNode.addChild(child);
                }
            }
        }
    }

    /**
     * packageDeclaration
     * 	:	packageModifier* 'package' Identifier ('.' Identifier)* ';'
     * 	;
     */
    @Override
    public void exitPackageDeclaration( @NotNull Java8Parser.PackageDeclarationContext ctx ) {
        TigerNode packageNode = new TigerNode();
        
        String identifiers = "";
        if ( ctx.Identifier() != null ) {
            StringBuilder sb = new StringBuilder();
            for (TerminalNode node : ctx.Identifier()) {
                sb.append(node.getSymbol().getText()).append('.');   
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);     // deletes the trailing .   
            }
            identifiers = sb.toString();
        }
        packageNode.setName( identifiers );

        if ( ctx.packageModifier() != null && !ctx.packageModifier().isEmpty() ) {
            AnnotationNode annotation = (AnnotationNode)stack.pop();
            packageNode.addAnnotation( annotation );
        }

        packageNode.setStartLocation( getStartLocation( ctx ) );
        packageNode.setEndLocation( getEndLocation( ctx ) );
        cuNode.setPackage(packageNode);
    }


    /**
	 * SingleTypeImportDeclaration
	 * TypeImportOnDemandDeclaration
	 * SingleStaticImportDeclaration
	 * StaticImportOnDemandDeclaration
     */
    @Override
    public void enterImportDeclaration( @NotNull Java8Parser.ImportDeclarationContext ctx ) {
        // make sure the cuNode has an import node
        ImportNode parent = cuNode.getImportNode();
        if (parent == null) {
            parent = new ImportNode("Imports");     // TODO: localization
            cuNode.setImportNode(parent);
        }
    }

    
    /**
     * singleTypeImportDeclaration
     * 	:	'import' typeName ';'
     * 	;
     */
    @Override
    public void exitSingleTypeImportDeclaration( @NotNull Java8Parser.SingleTypeImportDeclarationContext ctx ) {
        ImportNode parent = cuNode.getImportNode();
        Java8Parser.TypeNameContext tnc = ctx.typeName();
        StringBuilder importName = new StringBuilder();
        if (tnc.packageOrTypeName() != null) {
            importName.append(tnc.packageOrTypeName().getText());   
        }
        if (tnc.Identifier() != null) {
            importName.append('.').append(tnc.Identifier().getSymbol().getText());
        }
        ImportNode node = new ImportNode(importName.toString());
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        parent.addChild(node);
    }
    
    /**
     * typeImportOnDemandDeclaration
     * 	:	'import' packageOrTypeName '.' '*' ';'
     * 	;
     */
    @Override
    public void exitTypeImportOnDemandDeclaration( @NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx ) {
        ImportNode parent = cuNode.getImportNode();
        String importName = "";
        if (ctx.packageOrTypeName() != null) {
            importName = ctx.packageOrTypeName().getText() + ".*";   
        }
        ImportNode node = new ImportNode(importName);
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        parent.addChild(node);
    }
    
    /**
     * singleStaticImportDeclaration
     * 	:	'import' 'static' typeName '.' Identifier ';'
     * 	;
     */
    @Override
    public void exitSingleStaticImportDeclaration( @NotNull Java8Parser.SingleStaticImportDeclarationContext ctx ) {
        ImportNode parent = cuNode.getImportNode();
        Java8Parser.TypeNameContext tnc = ctx.typeName();
        StringBuilder importName = new StringBuilder();
        if (tnc.packageOrTypeName() != null) {
            importName.append(tnc.packageOrTypeName().getText());   
        }
        if (tnc.Identifier() != null) {
            importName.append( '.').append(tnc.Identifier().getSymbol().getText());
        }
        ImportNode node = new ImportNode(importName.toString());
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        parent.addChild(node);
   }

   
   /**
    * staticImportOnDemandDeclaration
    * 	:	'import' 'static' typeName '.' '*' ';'
    * 	;
    */
    @Override
    public void exitStaticImportOnDemandDeclaration( @NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx ) {
        ImportNode parent = cuNode.getImportNode();
        Java8Parser.TypeNameContext tnc = ctx.typeName();
        String importName = "";
        if (tnc.packageOrTypeName() != null) {
            importName = tnc.packageOrTypeName().getText() + ".*";   
        }
        ImportNode node = new ImportNode(importName);
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        parent.addChild(node);
    }


    /**
     * typeDeclaration
     * 	:	classDeclaration
     * 	|	interfaceDeclaration
     * 	|	';'
     * 	;
     */
    @Override
    public void exitTypeDeclaration( @NotNull Java8Parser.TypeDeclarationContext ctx ) {
    }


/* =============================================================================
 * Productions from §8 (Classes)
 */
 
    @Override
    public void enterNormalClassDeclaration( @NotNull Java8Parser.NormalClassDeclarationContext ctx ) {
    }

    
    /**
     * normalClassDeclaration
     * 	:	classModifiers 'class' Identifier typeParameters? superclass? superinterfaces? classBody
     * 	;
     */
    @Override
    public void exitNormalClassDeclaration( @NotNull Java8Parser.NormalClassDeclarationContext ctx ) {
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
	@Override public void exitClassModifier(@NotNull Java8Parser.ClassModifierContext ctx) {
	    // don't do anything with the annotation since it will already be on the stack
	    if (ctx.annotation() == null) {
	        // one of the modifier keywords
	        TigerNode tn = new TigerNode(ctx.getText());
	        setStartLocation(tn, ctx);
	        setEndLocation(tn, ctx);
	        stack.push(tn);
	    }
	}

    @Override
    public void enterTypeParameters( @NotNull Java8Parser.TypeParametersContext ctx ) {
    }

    
    /**
     * typeParameters
     * 	:	'<' typeParameterList '>'
     * 	;
     */
    @Override
    public void exitTypeParameters( @NotNull Java8Parser.TypeParametersContext ctx ) {
    }


    @Override
    public void enterTypeParameterList( @NotNull Java8Parser.TypeParameterListContext ctx ) {
    }

    
    /**
     * typeParameterList
     * 	:	typeParameter (',' typeParameter)*
     * 	;
     */
    @Override
    public void exitTypeParameterList( @NotNull Java8Parser.TypeParameterListContext ctx ) {
    }


    @Override
    public void enterSuperclass( @NotNull Java8Parser.SuperclassContext ctx ) {
    }

    
    /**
     * superclass
     * 	:	'extends' classType
     * 	;
     */
    @Override
    public void exitSuperclass( @NotNull Java8Parser.SuperclassContext ctx ) {
    }


    @Override
    public void enterSuperinterfaces( @NotNull Java8Parser.SuperinterfacesContext ctx ) {
    }

    
    /**
     * superinterfaces
     * 	:	'implements' interfaceTypeList
     * 	;
     */
    @Override
    public void exitSuperinterfaces( @NotNull Java8Parser.SuperinterfacesContext ctx ) {
    }


    @Override
    public void enterInterfaceTypeList( @NotNull Java8Parser.InterfaceTypeListContext ctx ) {
    }

    
    /**
     * interfaceTypeList
     * 	:	interfaceType (',' interfaceType)*
     * 	;
     */
    @Override
    public void exitInterfaceTypeList( @NotNull Java8Parser.InterfaceTypeListContext ctx ) {
    }


    @Override
    public void enterClassBody( @NotNull Java8Parser.ClassBodyContext ctx ) {
    }

    
    /**
     * classBody
     * 	:	'{' classBodyDeclaration* '}'
     * 	;
     */
    @Override
    public void exitClassBody( @NotNull Java8Parser.ClassBodyContext ctx ) {
    }


    @Override
    public void enterFieldDeclaration( @NotNull Java8Parser.FieldDeclarationContext ctx ) {
    }

    
    /**
     * fieldDeclaration
     * 	:	fieldModifiers unannType variableDeclaratorList ';'
     * 	;
     */
    @Override
    public void exitFieldDeclaration( @NotNull Java8Parser.FieldDeclarationContext ctx ) {
        TigerNode tn = new TigerNode(ctx.getText());
        tn.setStartLocation(getStartLocation(ctx));
        tn.setEndLocation(getEndLocation(ctx));
    }


    @Override
    public void enterFieldModifiers( @NotNull Java8Parser.FieldModifiersContext ctx ) {
    }

    
    /**
     * fieldModifiers
     *     :   fieldModifier*
     *     ;
     */
    @Override
    public void exitFieldModifiers( @NotNull Java8Parser.FieldModifiersContext ctx ) {
    }


    @Override
    public void enterFieldModifier( @NotNull Java8Parser.FieldModifierContext ctx ) {
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
    }


    @Override
    public void enterVariableDeclaratorList( @NotNull Java8Parser.VariableDeclaratorListContext ctx ) {
    }

    
    /**
     * variableDeclaratorList
     * 	:	variableDeclarator (',' variableDeclarator)*
     * 	;
     */
    @Override
    public void exitVariableDeclaratorList( @NotNull Java8Parser.VariableDeclaratorListContext ctx ) {
    }


    @Override
    public void enterVariableDeclarator( @NotNull Java8Parser.VariableDeclaratorContext ctx ) {
    }

    
    /**
     * variableDeclarator
     * 	:	variableDeclaratorId ('=' variableInitializer)?
     * 	;
     */
    @Override
    public void exitVariableDeclarator( @NotNull Java8Parser.VariableDeclaratorContext ctx ) {
    }


    @Override
    public void enterVariableDeclaratorId( @NotNull Java8Parser.VariableDeclaratorIdContext ctx ) {
    }

    
    /**
     * variableDeclaratorId
     * 	:	Identifier dims?
     * 	;
     */
    @Override
    public void exitVariableDeclaratorId( @NotNull Java8Parser.VariableDeclaratorIdContext ctx ) {
    }


    @Override
    public void enterUnannPrimitiveType( @NotNull Java8Parser.UnannPrimitiveTypeContext ctx ) {
    }

    
    /**
     * unannPrimitiveType
     * 	:	numericType
     * 	|	'boolean'
     * 	;
     */
    @Override
    public void exitUnannPrimitiveType( @NotNull Java8Parser.UnannPrimitiveTypeContext ctx ) {
    }


    @Override
    public void enterUnannClassOrInterfaceType( @NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx ) {
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
    }


    @Override
    public void enterUnannClassType( @NotNull Java8Parser.UnannClassTypeContext ctx ) {
    }

    
    /**
     * unannClassType
     * 	:	Identifier typeArguments?
     * 	|	unannClassOrInterfaceType '.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitUnannClassType( @NotNull Java8Parser.UnannClassTypeContext ctx ) {
    }


    @Override
    public void enterUnannClassType_lf_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx ) {
    }

    
    /**
     * unannClassType_lf_unannClassOrInterfaceType
     * 	:	'.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override
    public void exitUnannClassType_lf_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void enterUnannClassType_lfno_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx ) {
    }

    
    /**
     * unannClassType_lfno_unannClassOrInterfaceType
     * 	:	Identifier typeArguments?
     * 	;
     */
    @Override
    public void exitUnannClassType_lfno_unannClassOrInterfaceType( @NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx ) {
    }


    @Override
    public void enterUnannTypeVariable( @NotNull Java8Parser.UnannTypeVariableContext ctx ) {
    }

    
    /**
     * unannTypeVariable
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitUnannTypeVariable( @NotNull Java8Parser.UnannTypeVariableContext ctx ) {
    }


    @Override
    public void enterUnannArrayType( @NotNull Java8Parser.UnannArrayTypeContext ctx ) {
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
    }

    
    @Override
    public void enterMethodDeclaration( @NotNull Java8Parser.MethodDeclarationContext ctx ) {
    }


    /**
     * methodDeclaration
     * 	:	methodModifiers methodHeader methodBody
     * 	;
     */
    @Override
    public void exitMethodDeclaration( @NotNull Java8Parser.MethodDeclarationContext ctx ) {
    }


    @Override
    public void enterMethodModifiers( @NotNull Java8Parser.MethodModifiersContext ctx ) {
    }

    
    /**
     * methodModifiers
     *     :   methodModifier*
     *     ;
     */
    @Override
    public void exitMethodModifiers( @NotNull Java8Parser.MethodModifiersContext ctx ) {
    }


    @Override
    public void enterMethodModifier( @NotNull Java8Parser.MethodModifierContext ctx ) {
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
    }


    @Override
    public void enterMethodHeader( @NotNull Java8Parser.MethodHeaderContext ctx ) {
    }

    
    /**
     * methodHeader
     * 	:	result methodDeclarator throws_?
     * 	|	typeParameters annotation* result methodDeclarator throws_?
     * 	;
     */
    @Override
    public void exitMethodHeader( @NotNull Java8Parser.MethodHeaderContext ctx ) {
    }

    
    @Override
    public void enterResult( @NotNull Java8Parser.ResultContext ctx ) {
    }


    /**
     * result
     * 	:	unannType
     * 	|	'void'
     * 	;
     */
    @Override
    public void exitResult( @NotNull Java8Parser.ResultContext ctx ) {
    }


    @Override
    public void enterMethodDeclarator( @NotNull Java8Parser.MethodDeclaratorContext ctx ) {
    }

    
    /**
     * methodDeclarator
     * 	:	Identifier '(' formalParameterList? ')' dims?
     * 	;
     */
    @Override
    public void exitMethodDeclarator( @NotNull Java8Parser.MethodDeclaratorContext ctx ) {
    }


    @Override
    public void enterFormalParameterList( @NotNull Java8Parser.FormalParameterListContext ctx ) {
    }

    
    /**
     * formalParameterList
     * 	:	formalParameters ',' lastFormalParameter
     * 	|	lastFormalParameter
     * 	;
     */
    @Override
    public void exitFormalParameterList( @NotNull Java8Parser.FormalParameterListContext ctx ) {
    }


    @Override
    public void enterFormalParameters( @NotNull Java8Parser.FormalParametersContext ctx ) {
    }

    
    /**
     * formalParameters
     * 	:	formalParameter (',' formalParameter)*
     * 	|	receiverParameter (',' formalParameter)*
     * 	;
     */
    @Override
    public void exitFormalParameters( @NotNull Java8Parser.FormalParametersContext ctx ) {
    }


    @Override
    public void enterFormalParameter( @NotNull Java8Parser.FormalParameterContext ctx ) {
    }

    
    /**
     * formalParameter
     * 	:	variableModifier* unannType variableDeclaratorId
     * 	;
     */
    @Override
    public void exitFormalParameter( @NotNull Java8Parser.FormalParameterContext ctx ) {
    }


    @Override
    public void enterVariableModifier( @NotNull Java8Parser.VariableModifierContext ctx ) {
    }

    
    /**
     * variableModifier
     * 	:	annotation
     * 	|	'final'
     * 	;
     */
    @Override
    public void exitVariableModifier( @NotNull Java8Parser.VariableModifierContext ctx ) {
    }


    @Override
    public void enterLastFormalParameter( @NotNull Java8Parser.LastFormalParameterContext ctx ) {
    }

    
    /**
     * lastFormalParameter
     * 	:	variableModifier* unannType annotation* '...' variableDeclaratorId
     * 	|	formalParameter
     * 	;
     */
    @Override
    public void exitLastFormalParameter( @NotNull Java8Parser.LastFormalParameterContext ctx ) {
    }


    @Override
    public void enterReceiverParameter( @NotNull Java8Parser.ReceiverParameterContext ctx ) {
    }

    
    /**
     * receiverParameter
     * 	:	annotation* unannType (Identifier '.')? 'this'
     * 	;
     */
    @Override
    public void exitReceiverParameter( @NotNull Java8Parser.ReceiverParameterContext ctx ) {
    }


    @Override
    public void enterThrows_( @NotNull Java8Parser.Throws_Context ctx ) {
    }

    
    /**
     * throws_
     * 	:	'throws' exceptionTypeList
     * 	;
     */
    @Override
    public void exitThrows_( @NotNull Java8Parser.Throws_Context ctx ) {
    }


    @Override
    public void enterExceptionTypeList( @NotNull Java8Parser.ExceptionTypeListContext ctx ) {
    }

    
    /**
     * exceptionTypeList
     * 	:	exceptionType (',' exceptionType)*
     * 	;
     */
    @Override
    public void exitExceptionTypeList( @NotNull Java8Parser.ExceptionTypeListContext ctx ) {
    }


    @Override
    public void enterMethodBody( @NotNull Java8Parser.MethodBodyContext ctx ) {
    }

    
    /**
     * methodBody
     * 	:	block
     * 	|	';'
     * 	;
     */
    @Override
    public void exitMethodBody( @NotNull Java8Parser.MethodBodyContext ctx ) {
    }


    @Override
    public void enterStaticInitializer( @NotNull Java8Parser.StaticInitializerContext ctx ) {
    }

    
    /**
     * staticInitializer
     * 	:	'static' block
     * 	;
     */
    public void exitStaticInitializer( @NotNull Java8Parser.StaticInitializerContext ctx ) {
    }


    @Override
    public void enterConstructorDeclaration( @NotNull Java8Parser.ConstructorDeclarationContext ctx ) {
    }

    
    /**
     * constructorDeclaration
     * 	:	constructorModifiers constructorDeclarator throws_? constructorBody
     * 	;
     */
    @Override
    public void exitConstructorDeclaration( @NotNull Java8Parser.ConstructorDeclarationContext ctx ) {
    }


    @Override
    public void enterConstructorModifiers( @NotNull Java8Parser.ConstructorModifiersContext ctx ) {
    }

    
    /**
     * constructorModifiers
     *     :   constructorModifier*
     *     ;
     */
    @Override
    public void exitConstructorModifiers( @NotNull Java8Parser.ConstructorModifiersContext ctx ) {
    }


    @Override
    public void enterConstructorModifier( @NotNull Java8Parser.ConstructorModifierContext ctx ) {
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
    }


    @Override
    public void enterConstructorDeclarator( @NotNull Java8Parser.ConstructorDeclaratorContext ctx ) {
    }

    
    /**
     * constructorDeclarator
     * 	:	typeParameters? simpleTypeName '(' formalParameterList? ')'
     * 	;
     */
    @Override
    public void exitConstructorDeclarator( @NotNull Java8Parser.ConstructorDeclaratorContext ctx ) {
    }


    @Override
    public void enterSimpleTypeName( @NotNull Java8Parser.SimpleTypeNameContext ctx ) {
    }

    
    /**
     * simpleTypeName
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitSimpleTypeName( @NotNull Java8Parser.SimpleTypeNameContext ctx ) {
    }


    @Override
    public void enterConstructorBody( @NotNull Java8Parser.ConstructorBodyContext ctx ) {
    }

    
    /**
     * constructorBody
     * 	:	'{' explicitConstructorInvocation? blockStatements? '}'
     * 	;
     */
    @Override
    public void exitConstructorBody( @NotNull Java8Parser.ConstructorBodyContext ctx ) {
    }


    @Override
    public void enterExplicitConstructorInvocation( @NotNull Java8Parser.ExplicitConstructorInvocationContext ctx ) {
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
    }


    @Override
    public void enterEnumDeclaration( @NotNull Java8Parser.EnumDeclarationContext ctx ) {
    }

    
    /**
     * enumDeclaration
     * 	:	classModifiers 'enum' Identifier superinterfaces? enumBody
     * 	;
     */
    @Override
    public void exitEnumDeclaration( @NotNull Java8Parser.EnumDeclarationContext ctx ) {
    }


    @Override
    public void enterEnumBody( @NotNull Java8Parser.EnumBodyContext ctx ) {
    }

    
    /**
     * enumBody
     * 	:	'{' enumConstantList? COMMA? enumBodyDeclarations? '}'
     * 	;
     */
    @Override
    public void exitEnumBody( @NotNull Java8Parser.EnumBodyContext ctx ) {
    }


    @Override
    public void enterEnumConstantList( @NotNull Java8Parser.EnumConstantListContext ctx ) {
    }

    
    /**
     * enumConstantList
     * 	:	enumConstant (',' enumConstant)*
     * 	;
     */
    @Override
    public void exitEnumConstantList( @NotNull Java8Parser.EnumConstantListContext ctx ) {
    }


    @Override
    public void enterEnumConstant( @NotNull Java8Parser.EnumConstantContext ctx ) {
    }

    
    /**
     * enumConstant
     * 	:	enumConstantModifier* Identifier ('(' argumentList? ')')? classBody?
     * 	;
     */
    @Override
    public void exitEnumConstant( @NotNull Java8Parser.EnumConstantContext ctx ) {
    }


    @Override
    public void enterEnumConstantModifier( @NotNull Java8Parser.EnumConstantModifierContext ctx ) {
    }

    
    /**
     * enumConstantModifier
     * 	:	annotation
     * 	;
     */
    @Override
    public void exitEnumConstantModifier( @NotNull Java8Parser.EnumConstantModifierContext ctx ) {
    }


    @Override
    public void enterEnumBodyDeclarations( @NotNull Java8Parser.EnumBodyDeclarationsContext ctx ) {
    }

    
    /**
     * enumBodyDeclarations
     * 	:	';' classBodyDeclaration*
     * 	;
     */
    @Override
    public void exitEnumBodyDeclarations( @NotNull Java8Parser.EnumBodyDeclarationsContext ctx ) {
    }

/* =============================================================================
 * Productions from §9 (Interfaces)
 */

    @Override
    public void enterNormalInterfaceDeclaration( @NotNull Java8Parser.NormalInterfaceDeclarationContext ctx ) {
    }

    
    /**
     * normalInterfaceDeclaration
     * 	:	interfaceModifiers 'interface' Identifier typeParameters? extendsInterfaces? interfaceBody
     * 	;
     */
    @Override
    public void exitNormalInterfaceDeclaration( @NotNull Java8Parser.NormalInterfaceDeclarationContext ctx ) {
    }


    @Override
    public void enterInterfaceModifiers( @NotNull Java8Parser.InterfaceModifiersContext ctx ) {
    }

    
    /**
     * interfaceModifiers
     *     : interfaceModifier*
     *     ;
     */
    @Override
    public void exitInterfaceModifiers( @NotNull Java8Parser.InterfaceModifiersContext ctx ) {
    }


    @Override
    public void enterInterfaceModifier( @NotNull Java8Parser.InterfaceModifierContext ctx ) {
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
    }


    @Override
    public void enterExtendsInterfaces( @NotNull Java8Parser.ExtendsInterfacesContext ctx ) {
    }

    
    /**
     * extendsInterfaces
     * 	:	'extends' interfaceTypeList
     * 	;
     */
    @Override
    public void exitExtendsInterfaces( @NotNull Java8Parser.ExtendsInterfacesContext ctx ) {
    }


    @Override
    public void enterInterfaceBody( @NotNull Java8Parser.InterfaceBodyContext ctx ) {
    }

    
    /**
     * interfaceBody
     * 	:	'{' interfaceMemberDeclaration* '}'
     * 	;
     */
    @Override
    public void exitInterfaceBody( @NotNull Java8Parser.InterfaceBodyContext ctx ) {
    }


    @Override
    public void enterInterfaceMemberDeclaration( @NotNull Java8Parser.InterfaceMemberDeclarationContext ctx ) {
    }

    
    /**
     * interfaceMemberDeclaration
     * 	:	constantDeclaration
     * 	|	interfaceMethodDeclaration
     * 	|	classDeclaration
     * 	|	interfaceDeclaration
     * 	|	';'
     * 	;
     */
    @Override
    public void exitInterfaceMemberDeclaration( @NotNull Java8Parser.InterfaceMemberDeclarationContext ctx ) {
    }


    @Override
    public void enterConstantDeclaration( @NotNull Java8Parser.ConstantDeclarationContext ctx ) {
    }

    
    /**
     * constantDeclaration
     * 	:	constantModifier* unannType variableDeclaratorList ';'
     * 	;
     */
    @Override
    public void exitConstantDeclaration( @NotNull Java8Parser.ConstantDeclarationContext ctx ) {
    }


    @Override
    public void enterConstantModifier( @NotNull Java8Parser.ConstantModifierContext ctx ) {
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
    }


    @Override
    public void enterInterfaceMethodDeclaration( @NotNull Java8Parser.InterfaceMethodDeclarationContext ctx ) {
    }

    
    /**
     * interfaceMethodDeclaration
     * 	:	interfaceMethodModifier* methodHeader methodBody
     * 	;
     */
    @Override
    public void exitInterfaceMethodDeclaration( @NotNull Java8Parser.InterfaceMethodDeclarationContext ctx ) {
    }


    @Override
    public void enterInterfaceMethodModifier( @NotNull Java8Parser.InterfaceMethodModifierContext ctx ) {
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
    }


    @Override
    public void enterAnnotationTypeDeclaration( @NotNull Java8Parser.AnnotationTypeDeclarationContext ctx ) {
    }

    
    /**
     * annotationTypeDeclaration
     * 	:	interfaceModifier* '@' 'interface' Identifier annotationTypeBody
     * 	;
     */
    @Override
    public void exitAnnotationTypeDeclaration( @NotNull Java8Parser.AnnotationTypeDeclarationContext ctx ) {
    }


    @Override
    public void enterAnnotationTypeBody( @NotNull Java8Parser.AnnotationTypeBodyContext ctx ) {
    }

    
    /**
     * annotationTypeBody
     * 	:	'{' annotationTypeMemberDeclaration* '}'
     * 	;
     */
    @Override
    public void exitAnnotationTypeBody( @NotNull Java8Parser.AnnotationTypeBodyContext ctx ) {
    }


    @Override
    public void enterAnnotationTypeMemberDeclaration( @NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx ) {
    }

    
    /**
     * annotationTypeMemberDeclaration
     * 	:	annotationTypeElementDeclaration
     * 	|	constantDeclaration
     * 	|	classDeclaration
     * 	|	interfaceDeclaration
     * 	|	';'
     * 	;
     */
    @Override
    public void exitAnnotationTypeMemberDeclaration( @NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx ) {
    }


    @Override
    public void enterAnnotationTypeElementDeclaration( @NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx ) {
    }

    
    /**
     * annotationTypeElementDeclaration
     * 	:	annotationTypeElementModifier* unannType Identifier '(' ')' dims? defaultValue? ';'
     * 	;
     */
    @Override
    public void exitAnnotationTypeElementDeclaration( @NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx ) {
    }


    @Override
    public void enterAnnotationTypeElementModifier( @NotNull Java8Parser.AnnotationTypeElementModifierContext ctx ) {
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
    }


    @Override
    public void enterDefaultValue( @NotNull Java8Parser.DefaultValueContext ctx ) {
    }

    
    /**
     * defaultValue
     * 	:	'default' elementValue
     * 	;
     */
    @Override
    public void exitDefaultValue( @NotNull Java8Parser.DefaultValueContext ctx ) {
    }


    @Override
    public void enterAnnotationIdentifier( @NotNull Java8Parser.AnnotationIdentifierContext ctx ) {
    }

    
    /**
     * annotationIdentifier
     *     :   annotation* Identifier
     *     ;
     */
    @Override
    public void exitAnnotationIdentifier( @NotNull Java8Parser.AnnotationIdentifierContext ctx ) {
    }


    @Override
    public void enterAnnotationDim( @NotNull Java8Parser.AnnotationDimContext ctx ) {
    }

    
    /**
     * annotationDim
     *     : annotation* squareBrackets
     *     ;
     */
    @Override
    public void exitAnnotationDim( @NotNull Java8Parser.AnnotationDimContext ctx ) {
    }


    @Override
    public void enterNormalAnnotation( @NotNull Java8Parser.NormalAnnotationContext ctx ) {
    }

    
    /**
     * normalAnnotation
     * 	:	'@' typeName '(' elementValuePairList? ')'
     * 	;
     */
    @Override
    public void exitNormalAnnotation( @NotNull Java8Parser.NormalAnnotationContext ctx ) {
    }


    @Override
    public void enterElementValuePairList( @NotNull Java8Parser.ElementValuePairListContext ctx ) {
    }

    
    /**
     * elementValuePairList
     * 	:	elementValuePair (',' elementValuePair)*
     * 	;
     */
    @Override
    public void exitElementValuePairList( @NotNull Java8Parser.ElementValuePairListContext ctx ) {
    }


    @Override
    public void enterElementValuePair( @NotNull Java8Parser.ElementValuePairContext ctx ) {
    }

    
    /**
     * elementValuePair
     * 	:	Identifier '=' elementValue
     * 	;
     */
    @Override
    public void exitElementValuePair( @NotNull Java8Parser.ElementValuePairContext ctx ) {
    }


    @Override
    public void enterElementValueArrayInitializer( @NotNull Java8Parser.ElementValueArrayInitializerContext ctx ) {
    }

    
    /**
     * elementValueArrayInitializer
     * 	:	'{' elementValueList? COMMA? '}'
     * 	;
     */
    @Override
    public void exitElementValueArrayInitializer( @NotNull Java8Parser.ElementValueArrayInitializerContext ctx ) {
    }


    @Override
    public void enterElementValueList( @NotNull Java8Parser.ElementValueListContext ctx ) {
    }

    
    /**
     * elementValueList
     * 	:	elementValue (',' elementValue)*
     * 	;
     */
    @Override
    public void exitElementValueList( @NotNull Java8Parser.ElementValueListContext ctx ) {
    }


    @Override
    public void enterMarkerAnnotation( @NotNull Java8Parser.MarkerAnnotationContext ctx ) {
    }

    
    /**
     * markerAnnotation
     * 	:	'@' typeName
     * 	;
     */
    @Override
    public void exitMarkerAnnotation( @NotNull Java8Parser.MarkerAnnotationContext ctx ) {
        stack.push(new AnnotationNode("markerAnnotation"));
    }


    @Override
    public void enterSingleElementAnnotation( @NotNull Java8Parser.SingleElementAnnotationContext ctx ) {
    }

    
    /**
     * singleElementAnnotation
     * 	:	'@' typeName '(' elementValue ')'
     * 	;
     */
    @Override
    public void exitSingleElementAnnotation( @NotNull Java8Parser.SingleElementAnnotationContext ctx ) {
        stack.push(new AnnotationNode("singleElementAnnotation"));
    }

/* =============================================================================
 * Productions from §10 (Arrays)
 */

    @Override
    public void enterArrayInitializer( @NotNull Java8Parser.ArrayInitializerContext ctx ) {
    }

    
    /**
     * arrayInitializer
     * 	:	'{' variableInitializerList? COMMA? '}'
     * 	;
     */
    @Override
    public void exitArrayInitializer( @NotNull Java8Parser.ArrayInitializerContext ctx ) {
    }


    @Override
    public void enterVariableInitializerList( @NotNull Java8Parser.VariableInitializerListContext ctx ) {
    }

    
    /**
     * variableInitializerList
     * 	:	variableInitializer (',' variableInitializer)*
     * 	;
     */
    @Override
    public void exitVariableInitializerList( @NotNull Java8Parser.VariableInitializerListContext ctx ) {
    }

/* =============================================================================
 * Productions from §14 (Blocks and Statements)
 */

    @Override
    public void enterBlock( @NotNull Java8Parser.BlockContext ctx ) {
    }

    
    /**
     * block
     * 	:	'{' blockStatements? '}'
     * 	;
     */
    @Override
    public void exitBlock( @NotNull Java8Parser.BlockContext ctx ) {
    }


    @Override
    public void enterBlockStatements( @NotNull Java8Parser.BlockStatementsContext ctx ) {
    }

    
    /**
     * blockStatements
     * 	:	blockStatement blockStatement*
     * 	;
     */
    @Override
    public void exitBlockStatements( @NotNull Java8Parser.BlockStatementsContext ctx ) {
    }


    @Override
    public void enterLocalVariableDeclarationStatement( @NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx ) {
    }

    
    /**
     * localVariableDeclarationStatement
     * 	:	localVariableDeclaration ';'
     * 	;
     */
    @Override
    public void exitLocalVariableDeclarationStatement( @NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx ) {
    }

    
    @Override
    public void enterLocalVariableDeclaration( @NotNull Java8Parser.LocalVariableDeclarationContext ctx ) {
    }


    /**
     * localVariableDeclaration
     * 	:	variableModifier* unannType variableDeclaratorList
     * 	;
     */
    @Override
    public void exitLocalVariableDeclaration( @NotNull Java8Parser.LocalVariableDeclarationContext ctx ) {
    }


    @Override
    public void enterEmptyStatement( @NotNull Java8Parser.EmptyStatementContext ctx ) {
    }

    
    /**
     * emptyStatement
     * 	:	';'
     * 	;
     */
    @Override
    public void exitEmptyStatement( @NotNull Java8Parser.EmptyStatementContext ctx ) {
    }


    @Override
    public void enterLabeledStatement( @NotNull Java8Parser.LabeledStatementContext ctx ) {
    }

    
    /**
     * labeledStatement
     * 	:	Identifier ':' statement
     * 	;
     */
    @Override
    public void exitLabeledStatement( @NotNull Java8Parser.LabeledStatementContext ctx ) {
    }


    @Override
    public void enterLabeledStatementNoShortIf( @NotNull Java8Parser.LabeledStatementNoShortIfContext ctx ) {
    }

    
    /**
     * labeledStatementNoShortIf
     * 	:	Identifier ':' statementNoShortIf
     * 	;
     */
    @Override
    public void exitLabeledStatementNoShortIf( @NotNull Java8Parser.LabeledStatementNoShortIfContext ctx ) {
    }


    @Override
    public void enterExpressionStatement( @NotNull Java8Parser.ExpressionStatementContext ctx ) {
    }

    
    /**
     * expressionStatement
     * 	:	statementExpression ';'
     * 	;
     */
    @Override
    public void exitExpressionStatement( @NotNull Java8Parser.ExpressionStatementContext ctx ) {
    }


    @Override
    public void enterIfThenStatement( @NotNull Java8Parser.IfThenStatementContext ctx ) {
    }

    
    /**
     * ifThenStatement
     * 	:	'if' '(' expression ')' statement
     * 	;
     */
    @Override
    public void exitIfThenStatement( @NotNull Java8Parser.IfThenStatementContext ctx ) {
    }


    @Override
    public void enterIfThenElseStatement( @NotNull Java8Parser.IfThenElseStatementContext ctx ) {
    }

    
    /**
     * ifThenElseStatement
     * 	:	'if' '(' expression ')' statementNoShortIf 'else' statement
     * 	;
     */
    @Override
    public void exitIfThenElseStatement( @NotNull Java8Parser.IfThenElseStatementContext ctx ) {
    }


    @Override
    public void enterIfThenElseStatementNoShortIf( @NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx ) {
    }

    
    /**
     * ifThenElseStatementNoShortIf
     * 	:	'if' '(' expression ')' statementNoShortIf 'else' statementNoShortIf
     * 	;
     */
    @Override
    public void exitIfThenElseStatementNoShortIf( @NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx ) {
    }


    @Override
    public void enterAssertStatement( @NotNull Java8Parser.AssertStatementContext ctx ) {
    }

    
    /**
     * assertStatement
     * 	:	'assert' expression ';'
     * 	|	'assert' expression ':' expression ';'
     * 	;
     */
    @Override
    public void exitAssertStatement( @NotNull Java8Parser.AssertStatementContext ctx ) {
    }


    @Override
    public void enterSwitchStatement( @NotNull Java8Parser.SwitchStatementContext ctx ) {
    }

    
    /**
     * switchStatement
     * 	:	'switch' '(' expression ')' switchBlock
     * 	;
     */
    @Override
    public void exitSwitchStatement( @NotNull Java8Parser.SwitchStatementContext ctx ) {
    }


    @Override
    public void enterSwitchBlock( @NotNull Java8Parser.SwitchBlockContext ctx ) {
    }

    
    /**
     * switchBlock
     * 	:	'{' switchBlockStatementGroup* switchLabel* '}'
     * 	;
     */
    @Override
    public void exitSwitchBlock( @NotNull Java8Parser.SwitchBlockContext ctx ) {
    }


    @Override
    public void enterSwitchBlockStatementGroup( @NotNull Java8Parser.SwitchBlockStatementGroupContext ctx ) {
    }

    
    /**
     * switchBlockStatementGroup
     * 	:	switchLabels blockStatements
     * 	;
     */
    @Override
    public void exitSwitchBlockStatementGroup( @NotNull Java8Parser.SwitchBlockStatementGroupContext ctx ) {
    }


    @Override
    public void enterSwitchLabels( @NotNull Java8Parser.SwitchLabelsContext ctx ) {
    }

    
    /**
     * switchLabels
     * 	:	switchLabel switchLabel*
     * 	;
     */
    @Override
    public void exitSwitchLabels( @NotNull Java8Parser.SwitchLabelsContext ctx ) {
    }


    @Override
    public void enterSwitchLabel( @NotNull Java8Parser.SwitchLabelContext ctx ) {
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
    }


    @Override
    public void enterEnumConstantName( @NotNull Java8Parser.EnumConstantNameContext ctx ) {
    }

    
    /**
     * enumConstantName
     * 	:	Identifier
     * 	;
     */
    @Override
    public void exitEnumConstantName( @NotNull Java8Parser.EnumConstantNameContext ctx ) {
    }


    @Override
    public void enterWhileStatement( @NotNull Java8Parser.WhileStatementContext ctx ) {
    }

    
    /**
     * whileStatement
     * 	:	'while' '(' expression ')' statement
     * 	;
     */
    @Override
    public void exitWhileStatement( @NotNull Java8Parser.WhileStatementContext ctx ) {
    }


    @Override
    public void enterWhileStatementNoShortIf( @NotNull Java8Parser.WhileStatementNoShortIfContext ctx ) {
    }

    
    /**
     * whileStatementNoShortIf
     * 	:	'while' '(' expression ')' statementNoShortIf
     * 	;
     */
    @Override
    public void exitWhileStatementNoShortIf( @NotNull Java8Parser.WhileStatementNoShortIfContext ctx ) {
    }


    @Override
    public void enterDoStatement( @NotNull Java8Parser.DoStatementContext ctx ) {
    }

    
    /**
     * doStatement
     * 	:	'do' statement 'while' '(' expression ')' ';'
     * 	;
     */
    @Override
    public void exitDoStatement( @NotNull Java8Parser.DoStatementContext ctx ) {
    }


    @Override
    public void enterBasicForStatement( @NotNull Java8Parser.BasicForStatementContext ctx ) {
    }

    
    /**
     * basicForStatement
     * 	:	'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
     * 	;
     */
    @Override
    public void exitBasicForStatement( @NotNull Java8Parser.BasicForStatementContext ctx ) {
    }


    @Override
    public void enterBasicForStatementNoShortIf( @NotNull Java8Parser.BasicForStatementNoShortIfContext ctx ) {
    }

    
    /**
     * basicForStatementNoShortIf
     * 	:	'for' '(' forInit? ';' expression? ';' forUpdate? ')' statementNoShortIf
     * 	;
     */
    @Override
    public void exitBasicForStatementNoShortIf( @NotNull Java8Parser.BasicForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void enterStatementExpressionList( @NotNull Java8Parser.StatementExpressionListContext ctx ) {
    }

    
    /**
     * statementExpressionList
     * 	:	statementExpression (',' statementExpression)*
     * 	;
     */
    @Override
    public void exitStatementExpressionList( @NotNull Java8Parser.StatementExpressionListContext ctx ) {
    }


    @Override
    public void enterEnhancedForStatement( @NotNull Java8Parser.EnhancedForStatementContext ctx ) {
    }

    
    /**
     * enhancedForStatement
     * 	:	'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statement
     * 	;
     */
    @Override
    public void exitEnhancedForStatement( @NotNull Java8Parser.EnhancedForStatementContext ctx ) {
    }


    @Override
    public void enterEnhancedForStatementNoShortIf( @NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx ) {
    }

    
    /**
     * enhancedForStatementNoShortIf
     * 	:	'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statementNoShortIf
     * 	;
     */
    @Override
    public void exitEnhancedForStatementNoShortIf( @NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx ) {
    }


    @Override
    public void enterBreakStatement( @NotNull Java8Parser.BreakStatementContext ctx ) {
    }

    
    /**
     * breakStatement
     * 	:	'break' Identifier? ';'
     * 	;
     */
    @Override
    public void exitBreakStatement( @NotNull Java8Parser.BreakStatementContext ctx ) {
    }


    @Override
    public void enterContinueStatement( @NotNull Java8Parser.ContinueStatementContext ctx ) {
    }

    
    /**
     * continueStatement
     * 	:	'continue' Identifier? ';'
     * 	;
     */
    @Override
    public void exitContinueStatement( @NotNull Java8Parser.ContinueStatementContext ctx ) {
    }


    @Override
    public void enterReturnStatement( @NotNull Java8Parser.ReturnStatementContext ctx ) {
    }

    
    /**
     * returnStatement
     * 	:	'return' expression? ';'
     * 	;
     */
    @Override
    public void exitReturnStatement( @NotNull Java8Parser.ReturnStatementContext ctx ) {
    }


    @Override
    public void enterThrowStatement( @NotNull Java8Parser.ThrowStatementContext ctx ) {
    }

    
    /**
     * throwStatement
     * 	:	'throw' expression ';'
     * 	;
     */
    @Override
    public void exitThrowStatement( @NotNull Java8Parser.ThrowStatementContext ctx ) {
    }


    @Override
    public void enterSynchronizedStatement( @NotNull Java8Parser.SynchronizedStatementContext ctx ) {
    }

    
    /**
     * synchronizedStatement
     * 	:	'synchronized' '(' expression ')' block
     * 	;
     */
    @Override
    public void exitSynchronizedStatement( @NotNull Java8Parser.SynchronizedStatementContext ctx ) {
    }


    @Override
    public void enterTryStatement( @NotNull Java8Parser.TryStatementContext ctx ) {
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
    }


    @Override
    public void enterCatches( @NotNull Java8Parser.CatchesContext ctx ) {
    }

    
    /**
     * catches
     * 	:	catchClause catchClause*
     * 	;
     */
    @Override
    public void exitCatches( @NotNull Java8Parser.CatchesContext ctx ) {
    }


    @Override
    public void enterCatchClause( @NotNull Java8Parser.CatchClauseContext ctx ) {
    }

    
    /**
     * catchClause
     * 	:	'catch' '(' catchFormalParameter ')' block
     * 	;
     */
    @Override
    public void exitCatchClause( @NotNull Java8Parser.CatchClauseContext ctx ) {
    }


    @Override
    public void enterCatchFormalParameter( @NotNull Java8Parser.CatchFormalParameterContext ctx ) {
    }

    
    /**
     * catchFormalParameter
     * 	:	variableModifier* catchType variableDeclaratorId
     * 	;
     */
    @Override
    public void exitCatchFormalParameter( @NotNull Java8Parser.CatchFormalParameterContext ctx ) {
    }


    @Override
    public void enterCatchType( @NotNull Java8Parser.CatchTypeContext ctx ) {
    }

    
    /**
     * catchType
     * 	:	unannClassType ('|' classType)*
     * 	;
     */
    @Override
    public void exitCatchType( @NotNull Java8Parser.CatchTypeContext ctx ) {
    }


    @Override
    public void enterFinally_( @NotNull Java8Parser.Finally_Context ctx ) {
    }

    
    /**
     * finally_
     * 	:	'finally' block
     * 	;
     */
    @Override
    public void exitFinally_( @NotNull Java8Parser.Finally_Context ctx ) {
    }


    @Override
    public void enterTryWithResourcesStatement( @NotNull Java8Parser.TryWithResourcesStatementContext ctx ) {
    }

    
    /**
     * tryWithResourcesStatement
     * 	:	'try' resourceSpecification block catches? finally_?
     * 	;
     */
    @Override
    public void exitTryWithResourcesStatement( @NotNull Java8Parser.TryWithResourcesStatementContext ctx ) {
    }


    @Override
    public void enterResourceSpecification( @NotNull Java8Parser.ResourceSpecificationContext ctx ) {
    }

    
    /**
     * resourceSpecification
     * 	:	'(' resourceList SEMI? ')'
     * 	;
     */
    @Override
    public void exitResourceSpecification( @NotNull Java8Parser.ResourceSpecificationContext ctx ) {
    }


    @Override
    public void enterResourceList( @NotNull Java8Parser.ResourceListContext ctx ) {
    }

    
    /**
     * resourceList
     * 	:	resource (';' resource)*
     * 	;
     */
    @Override
    public void exitResourceList( @NotNull Java8Parser.ResourceListContext ctx ) {
    }


    @Override
    public void enterResource( @NotNull Java8Parser.ResourceContext ctx ) {
    }

    
    /**
     * resource
     * 	:	variableModifier* unannType variableDeclaratorId '=' expression
     * 	;
     */
    @Override
    public void exitResource( @NotNull Java8Parser.ResourceContext ctx ) {
    }

/* =============================================================================
 * Productions from §15 (Expressions)
 */

    @Override
    public void enterPrimary( @NotNull Java8Parser.PrimaryContext ctx ) {
    }

    
    /**
     * primary
     * 	:	(	primaryNoNewArray_lfno_primary
     * 		|	arrayCreationExpression
     * 		)
     * 		(	primaryNoNewArray_lf_primary
     * 		)*
     * 	;
     */
    @Override
    public void exitPrimary( @NotNull Java8Parser.PrimaryContext ctx ) {
    }


    @Override
    public void enterPrimaryNoNewArray( @NotNull Java8Parser.PrimaryNoNewArrayContext ctx ) {
    }

    
    /**
     * primaryNoNewArray
     * 	:	literal
     * 	|	typeName (squareBrackets)* '.' 'class'
     * 	|	'void' '.' 'class'
     * 	|	'this'
     * 	|	typeName '.' 'this'
     * 	|	'(' expression ')'
     * 	|	classInstanceCreationExpression
     * 	|	fieldAccess
     * 	|	arrayAccess
     * 	|	methodInvocation
     * 	|	methodReference
     * 	;
     */
    @Override
    public void exitPrimaryNoNewArray( @NotNull Java8Parser.PrimaryNoNewArrayContext ctx ) {
    }


    @Override
    public void enterPrimaryNoNewArray_lfno_arrayAccess( @NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx ) {
    }

    
    /**
     * primaryNoNewArray_lfno_arrayAccess
     * 	:	literal
     * 	|	typeName (squareBrackets)* '.' 'class'
     * 	|	'void' '.' 'class'
     * 	|	'this'
     * 	|	typeName '.' 'this'
     * 	|	'(' expression ')'
     * 	|	classInstanceCreationExpression
     * 	|	fieldAccess
     * 	|	methodInvocation
     * 	|	methodReference
     * 	;
     */
    @Override
    public void exitPrimaryNoNewArray_lfno_arrayAccess( @NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx ) {
    }


    @Override
    public void enterPrimaryNoNewArray_lfno_primary( @NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx ) {
    }

    
    /**
     * primaryNoNewArray_lfno_primary
     * 	:	literal
     * 	|	typeName (squareBrackets)* '.' 'class'
     * 	|	unannPrimitiveType (squareBrackets)* '.' 'class'
     * 	|	'void' '.' 'class'
     * 	|	'this'
     * 	|	typeName '.' 'this'
     * 	|	'(' expression ')'
     * 	|	classInstanceCreationExpression_lfno_primary
     * 	|	fieldAccess_lfno_primary
     * 	|	arrayAccess_lfno_primary
     * 	|	methodInvocation_lfno_primary
     * 	|	methodReference_lfno_primary
     * 	;
     */
    @Override
    public void exitPrimaryNoNewArray_lfno_primary( @NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx ) {
    }


    @Override
    public void enterPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary( @NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx ) {
    }

    
    /**
     * primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary
     * 	:	literal
     * 	|	typeName (squareBrackets)* '.' 'class'
     * 	|	unannPrimitiveType (squareBrackets)* '.' 'class'
     * 	|	'void' '.' 'class'
     * 	|	'this'
     * 	|	typeName '.' 'this'
     * 	|	'(' expression ')'
     * 	|	classInstanceCreationExpression_lfno_primary
     * 	|	fieldAccess_lfno_primary
     * 	|	methodInvocation_lfno_primary
     * 	|	methodReference_lfno_primary
     * 	;
     */
    @Override
    public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary( @NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void enterClassInstanceCreationExpression( @NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx ) {
    }

    
    /**
     * classInstanceCreationExpression
     * 	:	'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	expressionName '.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	primary '.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override
    public void exitClassInstanceCreationExpression( @NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx ) {
    }


    @Override
    public void enterClassInstanceCreationExpression_lf_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx ) {
    }

    
    /**
     * classInstanceCreationExpression_lf_primary
     * 	:	'.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override
    public void exitClassInstanceCreationExpression_lf_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx ) {
    }


    @Override
    public void enterClassInstanceCreationExpression_lfno_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx ) {
    }

    
    /**
     * classInstanceCreationExpression_lfno_primary
     * 	:	'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	expressionName '.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override
    public void exitClassInstanceCreationExpression_lfno_primary( @NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx ) {
    }


    @Override
    public void enterTypeArgumentsOrDiamond( @NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx ) {
    }

    
    /**
     * typeArgumentsOrDiamond
     * 	:	typeArguments
     * 	|	'<>'
     * 	;
     */
    @Override
    public void exitTypeArgumentsOrDiamond( @NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx ) {
    }


    @Override
    public void enterFieldAccess( @NotNull Java8Parser.FieldAccessContext ctx ) {
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
    }


    @Override
    public void enterFieldAccess_lf_primary( @NotNull Java8Parser.FieldAccess_lf_primaryContext ctx ) {
    }

    
    /**
     * fieldAccess_lf_primary
     * 	:	'.' Identifier
     * 	;
     */
    @Override
    public void exitFieldAccess_lf_primary( @NotNull Java8Parser.FieldAccess_lf_primaryContext ctx ) {
    }


    @Override
    public void enterFieldAccess_lfno_primary( @NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx ) {
    }

    
    /**
     * fieldAccess_lfno_primary
     * 	:	SUPER '.' Identifier
     * 	|	typeName '.' SUPER '.' Identifier
     * 	;
     */
    @Override
    public void exitFieldAccess_lfno_primary( @NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx ) {
    }


    @Override
    public void enterArrayAccess( @NotNull Java8Parser.ArrayAccessContext ctx ) {
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
    }


    @Override
    public void enterArrayAccess_lf_primary( @NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx ) {
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
    }


    @Override
    public void enterArrayAccess_lfno_primary( @NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx ) {
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
    }


    @Override
    public void enterMethodInvocation( @NotNull Java8Parser.MethodInvocationContext ctx ) {
    }

    
    /**
     * methodInvocation
     * 	:	methodName '(' argumentList? ')'
     * 	|	typeName '.' typeArguments? Identifier '(' argumentList? ')'
     * 	|	expressionName '.' typeArguments? Identifier '(' argumentList? ')'
     * 	|	primary '.' typeArguments? Identifier '(' argumentList? ')'
     * 	|	SUPER '.' typeArguments? Identifier '(' argumentList? ')'
     * 	|	typeName '.' SUPER '.' typeArguments? Identifier '(' argumentList? ')'  
     * 	;
     */
    @Override
    public void exitMethodInvocation( @NotNull Java8Parser.MethodInvocationContext ctx ) {
    }


    @Override
    public void enterMethodInvocation_lf_primary( @NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx ) {
    }

    
    /**
     * methodInvocation_lf_primary
     * 	:	'.' typeArguments? Identifier '(' argumentList? ')'
     * 	;
     */
    @Override
    public void exitMethodInvocation_lf_primary( @NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx ) {
    }


    @Override
    public void enterMethodInvocation_lfno_primary( @NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx ) {
    }

    
    /**
     * methodInvocation_lfno_primary
     * 	:	methodName '(' argumentList? ')'
     * 	|	typeName '.' typeArguments? Identifier '(' argumentList? ')'
     * 	|	expressionName '.' typeArguments? Identifier '(' argumentList? ')'
     * 	|	SUPER '.' typeArguments? Identifier '(' argumentList? ')'
     * 	|	typeName '.' SUPER '.' typeArguments? Identifier '(' argumentList? ')'
     * 	;
     */
    @Override
    public void exitMethodInvocation_lfno_primary( @NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx ) {
    }


    @Override
    public void enterArgumentList( @NotNull Java8Parser.ArgumentListContext ctx ) {
    }

    
    /**
     * argumentList
     * 	:	expression (',' expression)*
     * 	;
     */
    @Override
    public void exitArgumentList( @NotNull Java8Parser.ArgumentListContext ctx ) {
    }


    @Override
    public void enterMethodReference( @NotNull Java8Parser.MethodReferenceContext ctx ) {
    }

    
    /**
     * methodReference
     * 	:	expressionName '::' typeArguments? Identifier
     * 	|	referenceType '::' typeArguments? Identifier
     * 	|	primary '::' typeArguments? Identifier
     * 	|	SUPER '::' typeArguments? Identifier
     * 	|	typeName '.' SUPER '::' typeArguments? Identifier
     * 	|	classType '::' typeArguments? 'new'
     * 	|	arrayType '::' 'new'                               
     * 	;
     */
    @Override
    public void exitMethodReference( @NotNull Java8Parser.MethodReferenceContext ctx ) {
    }


    @Override
    public void enterMethodReference_lf_primary( @NotNull Java8Parser.MethodReference_lf_primaryContext ctx ) {
    }

    
    /**
     * methodReference_lf_primary
     * 	:	'::' typeArguments? Identifier
     * 	;
     */
    @Override
    public void exitMethodReference_lf_primary( @NotNull Java8Parser.MethodReference_lf_primaryContext ctx ) {
    }


    @Override
    public void enterMethodReference_lfno_primary( @NotNull Java8Parser.MethodReference_lfno_primaryContext ctx ) {
    }

    
    /**
     * methodReference_lfno_primary
     * 	:	expressionName '::' typeArguments? Identifier
     * 	|	referenceType '::' typeArguments? Identifier
     * 	|	SUPER '::' typeArguments? Identifier
     * 	|	typeName '.' SUPER '::' typeArguments? Identifier
     * 	|	classType '::' typeArguments? 'new'
     * 	|	arrayType '::' 'new'
     * 	;
     */
    @Override
    public void exitMethodReference_lfno_primary( @NotNull Java8Parser.MethodReference_lfno_primaryContext ctx ) {
    }


    @Override
    public void enterArrayCreationExpression( @NotNull Java8Parser.ArrayCreationExpressionContext ctx ) {
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
    }


    @Override
    public void enterDimExprs( @NotNull Java8Parser.DimExprsContext ctx ) {
    }

    
    /**
     * dimExprs
     * 	:	dimExpr dimExpr*
     * 	;
     */
    @Override
    public void exitDimExprs( @NotNull Java8Parser.DimExprsContext ctx ) {
    }


    @Override
    public void enterDimExpr( @NotNull Java8Parser.DimExprContext ctx ) {
    }

    
    /**
     * dimExpr
     * 	:	annotation* '[' expression ']'
     * 	;
     */
    @Override
    public void exitDimExpr( @NotNull Java8Parser.DimExprContext ctx ) {
    }


    @Override
    public void enterLambdaExpression( @NotNull Java8Parser.LambdaExpressionContext ctx ) {
    }

    
    /**
     * lambdaExpression
     * 	:	lambdaParameters '->' lambdaBody
     * 	;
     */
    @Override
    public void exitLambdaExpression( @NotNull Java8Parser.LambdaExpressionContext ctx ) {
    }


    @Override
    public void enterLambdaParameters( @NotNull Java8Parser.LambdaParametersContext ctx ) {
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
    }


    @Override
    public void enterInferredFormalParameterList( @NotNull Java8Parser.InferredFormalParameterListContext ctx ) {
    }

    
    /**
     * inferredFormalParameterList
     * 	:	Identifier (',' Identifier)*
     * 	;
     */
    @Override
    public void exitInferredFormalParameterList( @NotNull Java8Parser.InferredFormalParameterListContext ctx ) {
    }


    @Override
    public void enterAssignment( @NotNull Java8Parser.AssignmentContext ctx ) {
    }

    
    /**
     * assignment
     * 	:	leftHandSide assignmentOperator expression
     * 	;
     */
    @Override
    public void exitAssignment( @NotNull Java8Parser.AssignmentContext ctx ) {
    }
    
    /**
     * assignmentOperator
     * 	:	'='
     * 	|	'*='
     * 	|	'/='
     * 	|	'%='
     * 	|	'+='
     * 	|	'-='
     * 	|	'<<='
     * 	|	'>>='
     * 	|	'>>>='
     * 	|	'&='
     * 	|	'^='
     * 	|	'|='
     * 	;
     */
    @Override
    public void exitAssignmentOperator( @NotNull Java8Parser.AssignmentOperatorContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }
    
    /**
     * additiveOperator
     *     :   '+'
     *     |   '-'
     *     ;
     */
    @Override
    public void exitAdditiveOperator( @NotNull Java8Parser.AdditiveOperatorContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }

    
    /**
     * relationalOperator
     *     :   '<'
     *     |   '>'
     *     |   '<='
     *     |   '>='
     *     |   'instanceof'
     *     ;
     */
    @Override
    public void exitRelationalOperator( @NotNull Java8Parser.RelationalOperatorContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }

    
    /**
     * multiplicativeOperator
     *     :   '*'
     *     |   '/'
     *     |   '%'
     *     ;
     */
    @Override
    public void exitMultiplicativeOperator( @NotNull Java8Parser.MultiplicativeOperatorContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }

    
    /**
     * squareBrackets
     *     :   '[]'
     *     ;
     */
    @Override
    public void exitSquareBrackets( @NotNull Java8Parser.SquareBracketsContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }


    @Override
    public void enterConditionalExpression( @NotNull Java8Parser.ConditionalExpressionContext ctx ) {
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
    }


    @Override
    public void enterConditionalOrExpression( @NotNull Java8Parser.ConditionalOrExpressionContext ctx ) {
    }

    
    /**
     * conditionalOrExpression
     * 	:	conditionalAndExpression
     * 	|	conditionalOrExpression '||' conditionalAndExpression
     * 	;
     */
    @Override
    public void exitConditionalOrExpression( @NotNull Java8Parser.ConditionalOrExpressionContext ctx ) {
    }


    @Override
    public void enterConditionalAndExpression( @NotNull Java8Parser.ConditionalAndExpressionContext ctx ) {
    }

    
    /**
     * conditionalAndExpression
     * 	:	inclusiveOrExpression
     * 	|	conditionalAndExpression '&&' inclusiveOrExpression
     * 	;
     */
    @Override
    public void exitConditionalAndExpression( @NotNull Java8Parser.ConditionalAndExpressionContext ctx ) {
    }


    @Override
    public void enterInclusiveOrExpression( @NotNull Java8Parser.InclusiveOrExpressionContext ctx ) {
    }

    
    /**
     * inclusiveOrExpression
     * 	:	exclusiveOrExpression
     * 	|	inclusiveOrExpression '|' exclusiveOrExpression
     * 	;
     */
    @Override
    public void exitInclusiveOrExpression( @NotNull Java8Parser.InclusiveOrExpressionContext ctx ) {
    }


    @Override
    public void enterExclusiveOrExpression( @NotNull Java8Parser.ExclusiveOrExpressionContext ctx ) {
    }

    
    /**
     * exclusiveOrExpression
     * 	:	andExpression
     * 	|	exclusiveOrExpression '^' andExpression
     * 	;
     */
    @Override
    public void exitExclusiveOrExpression( @NotNull Java8Parser.ExclusiveOrExpressionContext ctx ) {
    }


    @Override
    public void enterAndExpression( @NotNull Java8Parser.AndExpressionContext ctx ) {
    }

    
    /**
     * andExpression
     * 	:	equalityExpression
     * 	|	andExpression '&' equalityExpression
     * 	;
     */
    @Override
    public void exitAndExpression( @NotNull Java8Parser.AndExpressionContext ctx ) {
    }


    @Override
    public void enterEqualityExpression( @NotNull Java8Parser.EqualityExpressionContext ctx ) {
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
    }


    @Override
    public void enterRelationalExpression( @NotNull Java8Parser.RelationalExpressionContext ctx ) {
    }

    
    /**
     * relationalExpression
     * 	:	shiftExpression
     * 	|	relationalExpression relationalOperator shiftExpression
     * 	|	relationalExpression relationalOperator shiftExpression
     * 	|	relationalExpression relationalOperator shiftExpression
     * 	|	relationalExpression relationalOperator shiftExpression
     * 	|	relationalExpression relationalOperator referenceType
     * 	;
     */
    @Override
    public void exitRelationalExpression( @NotNull Java8Parser.RelationalExpressionContext ctx ) {
    }


    @Override
    public void enterShiftExpression( @NotNull Java8Parser.ShiftExpressionContext ctx ) {
    }

    
    /**
     * shiftExpression
     * 	:	additiveExpression
     * 	|	shiftExpression shiftOperator additiveExpression
     * 	|	shiftExpression shiftOperator additiveExpression
     * 	|	shiftExpression shiftOperator additiveExpression
     * 	;
     */
    @Override
    public void exitShiftExpression( @NotNull Java8Parser.ShiftExpressionContext ctx ) {
    }

    
    /**
     * shiftOperator
     *     :   '<' '<'
     *     |   '>' '>'  
     *     |   '>' '>' '>'
     *     ;
     */
    @Override
    public void exitShiftOperator( @NotNull Java8Parser.ShiftOperatorContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }


    @Override
    public void enterAdditiveExpression( @NotNull Java8Parser.AdditiveExpressionContext ctx ) {
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
    }


    @Override
    public void enterMultiplicativeExpression( @NotNull Java8Parser.MultiplicativeExpressionContext ctx ) {
    }

    
    /**
     * multiplicativeExpression
     * 	:	unaryExpression
     * 	|	multiplicativeExpression multiplicativeOperator unaryExpression
     * 	|	multiplicativeExpression multiplicativeOperator unaryExpression
     * 	|	multiplicativeExpression multiplicativeOperator unaryExpression
     * 	;
     */
    @Override
    public void exitMultiplicativeExpression( @NotNull Java8Parser.MultiplicativeExpressionContext ctx ) {
    }


    @Override
    public void enterUnaryExpression( @NotNull Java8Parser.UnaryExpressionContext ctx ) {
    }

    
    /**
     * unaryExpression
     * 	:	preIncrementExpression
     * 	|	preDecrementExpression
     * 	|	additiveOperator unaryExpression
     * 	|	additiveOperator unaryExpression
     * 	|	unaryExpressionNotPlusMinus
     * 	;
     */
    @Override
    public void exitUnaryExpression( @NotNull Java8Parser.UnaryExpressionContext ctx ) {
    }


    @Override
    public void enterPreIncrementExpression( @NotNull Java8Parser.PreIncrementExpressionContext ctx ) {
    }

    
    /**
     * preIncrementExpression
     * 	:	'++' unaryExpression
     * 	;
     */
    @Override
    public void exitPreIncrementExpression( @NotNull Java8Parser.PreIncrementExpressionContext ctx ) {
    }


    @Override
    public void enterPreDecrementExpression( @NotNull Java8Parser.PreDecrementExpressionContext ctx ) {
    }

    
    /**
     * preDecrementExpression
     * 	:	'--' unaryExpression
     * 	;
     */
    @Override
    public void exitPreDecrementExpression( @NotNull Java8Parser.PreDecrementExpressionContext ctx ) {
    }


    @Override
    public void enterUnaryExpressionNotPlusMinus( @NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx ) {
    }

    
    /**
     * unaryExpressionNotPlusMinus
     * 	:	postfixExpression
     * 	|	'~' unaryExpression
     * 	|	'!' unaryExpression
     * 	|	castExpression
     * 	;
     */
    @Override
    public void exitUnaryExpressionNotPlusMinus( @NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx ) {
    }


    @Override
    public void enterPostfixExpression( @NotNull Java8Parser.PostfixExpressionContext ctx ) {
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
    }


    @Override
    public void enterPostIncrementExpression( @NotNull Java8Parser.PostIncrementExpressionContext ctx ) {
    }

    
    /**
     * postIncrementExpression
     * 	:	postfixExpression '++'
     * 	;
     * 
     */
    @Override
    public void exitPostIncrementExpression( @NotNull Java8Parser.PostIncrementExpressionContext ctx ) {
    }


    @Override
    public void enterPostIncrementExpression_lf_postfixExpression( @NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx ) {
    }

    
    /**
     * postIncrementExpression_lf_postfixExpression
     * 	:	'++'
     * 	;
     */
    @Override
    public void exitPostIncrementExpression_lf_postfixExpression( @NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx ) {
    }


    @Override
    public void enterPostDecrementExpression( @NotNull Java8Parser.PostDecrementExpressionContext ctx ) {
    }

    
    /**
     * postDecrementExpression
     * 	:	postfixExpression '--'
     * 	;
     */
    @Override
    public void exitPostDecrementExpression( @NotNull Java8Parser.PostDecrementExpressionContext ctx ) {
    }

    
    /**
     * postDecrementExpression_lf_postfixExpression
     * 	:	'--'
     * 	;
     */
    @Override
    public void exitPostDecrementExpression_lf_postfixExpression( @NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx ) {
        stack.push(new TigerNode(ctx.getText()));
    }


    @Override
    public void enterCastExpression( @NotNull Java8Parser.CastExpressionContext ctx ) {
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
    }


    /**
     * Pushes a node onto the stack, the node will not allow children.
     */
    @Override
    public void visitTerminal( @NotNull TerminalNode node ) {
    }


    @Override
    public void visitErrorNode( @NotNull ErrorNode node ) {
        // TODO: add these errors to the error list
        System.out.println("+++++ error node: " + node);
    }
}
