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


public class Java9SideKickListener extends Java8BaseListener {

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
    
    public void dumpStack() {
        Iterator<TigerNode> it = stack.iterator();
        while(it.hasNext()) {
            System.out.println(it.next());   
        }
    }
    
    public void dumpStack(String name) {
        System.out.println("+++++ dump: " + name);
        dumpStack();
        System.out.println("+++++ +++++ +++++");
    }
    
    /**
     * Identifier
     * 	:	JavaLetter JavaLetterOrDigit*
     * 	;
     */
    @Override public void exitIdentifier(@NotNull Java8Parser.IdentifierContext ctx) {
        // nothing to do
    }
    
    /**
     * literal
     * 	:	IntegerLiteral
     * 	|	FloatingPointLiteral
     * 	|	BooleanLiteral
     * 	|	CharacterLiteral
     * 	|	StringLiteral
     * 	|	NullLiteral
     * 	;
     */
    @Override public void exitLiteral(@NotNull Java8Parser.LiteralContext ctx) { 
        // nothing to do
    }

    /**
     * type
     * 	:	primitiveType
     * 	|	referenceType
     * 	;
     */
    @Override public void exitType(@NotNull Java8Parser.TypeContext ctx) { 
        // nothing to do, one of the choices should already be on the stack
    }

    /**
     * primitiveType
     * 	:	annotation* numericType
     * 	|	annotation* 'boolean'
     * 	;
     */
    @Override public void exitPrimitiveType(@NotNull Java8Parser.PrimitiveTypeContext ctx) { 
        if (ctx.annotation() != null) {
            TigerNode type = stack.pop();
            setLocations(type, ctx);
            for (int i = 0; i < ctx.annotation().size(); i++) {
                type.addAnnotation((AnnotationNode)stack.pop());   
            }
            stack.push(type);
        }
    }

    /**
     * numericType
     * 	:	integralType
     * 	|	floatingPointType
     * 	;
     */
    @Override public void exitNumericType(@NotNull Java8Parser.NumericTypeContext ctx) {
        // nothing to do, one of the choices should already be on the stack
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
    @Override public void exitIntegralType(@NotNull Java8Parser.IntegralTypeContext ctx) {
        TigerNode node = stack.pop();   // TerminalNode added to stack in visitTerminal
        Type type = new Type(node.getName());
        setLocations(type, ctx);
        stack.push(type);
    }

    /**
     * floatingPointType
     * 	:	'float'
     * 	|	'double'
     * 	;
     */
    @Override public void exitFloatingPointType(@NotNull Java8Parser.FloatingPointTypeContext ctx) { 
        TigerNode node = stack.pop();   // TerminalNode added to stack in visitTerminal
        Type type = new Type(node.getName());
        setLocations(type, ctx);
        stack.push(type);
    }

    /**
     * referenceType
     * 	:	classOrInterfaceType
     * 	|	typeVariable
     * 	|	arrayType
     * 	;
     */
    @Override public void exitReferenceType(@NotNull Java8Parser.ReferenceTypeContext ctx) { 
        // nothing to do
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
    @Override public void exitClassOrInterfaceType(@NotNull Java8Parser.ClassOrInterfaceTypeContext ctx) {
        TigerNode temp = null;
        if (ctx.classType_lf_classOrInterfaceType() != null) {
            temp = new TigerNode();
            for (int i = 0; i < ctx.classType_lf_classOrInterfaceType().size(); i++) {
                temp.addChild(stack.pop());   
            }
        }
        if (ctx.interfaceType_lf_classOrInterfaceType() != null) {
            if (temp == null) {
                temp = new TigerNode();   
            }
            for (int i = 0; i < ctx.interfaceType_lf_classOrInterfaceType().size(); i++) {
                temp.addChild(stack.pop());   
            }
        }
        TigerNode type = stack.pop();  // classType_lfno_classOrInterfaceType or interfaceType_lfno_classOrInterfaceType
        setLocations(type, ctx);
        if (temp != null) {
            type.addChildren(temp.getChildren());   
        }
        stack.push(type);
    }

    /**
     * classType
     * 	:	                         annotationIdentifier typeArguments?
     * 	|	classOrInterfaceType '.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override public void exitClassType(@NotNull Java8Parser.ClassTypeContext ctx) {
        TigerNode typeArgs = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode id = stack.pop(); // annotationIdentifier
        setLocations(id, ctx);
        id.addChild(typeArgs);
        if (ctx.classOrInterfaceType() != null) {
            stack.pop();    // .
            TigerNode type = stack.pop();
            id.addChild(type);
        }
        stack.push(id);
    }

    /**
     * classType_lf_classOrInterfaceType
     * 	:	'.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override public void exitClassType_lf_classOrInterfaceType(@NotNull Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        TigerNode typeArgs = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode id = stack.pop(); // annotationIdentifier
        setLocations(id, ctx);
        id.addChild(typeArgs);
        stack.pop();    // .
        stack.push(id);
    }

    /**
     * classType_lfno_classOrInterfaceType
     * 	:	annotationIdentifier typeArguments?
     * 	;
     */
    @Override public void exitClassType_lfno_classOrInterfaceType(@NotNull Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) { 
        TigerNode typeArgs = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode id = stack.pop(); // annotationIdentifier
        setLocations(id, ctx);
        id.addChild(typeArgs);
        stack.push(id);
    }

    /**
     * interfaceType
     * 	:	classType
     * 	;
     */
    @Override public void exitInterfaceType(@NotNull Java8Parser.InterfaceTypeContext ctx) { 
        // nothing to do
    }

    /**
     * interfaceType_lf_classOrInterfaceType
     * 	:	classType_lf_classOrInterfaceType
     * 	;
     */
    @Override public void exitInterfaceType_lf_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) {
        // nothing to do
    }

    /**
     * interfaceType_lfno_classOrInterfaceType
     * 	:	classType_lfno_classOrInterfaceType
     * 	;
     */
    @Override public void exitInterfaceType_lfno_classOrInterfaceType(@NotNull Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
        // nothing to do
    }

    /**
     * typeVariable
     * 	:	annotationIdentifier
     * 	;
     */
    @Override public void exitTypeVariable(@NotNull Java8Parser.TypeVariableContext ctx) { 
        // nothing to do
    }
    
    /**
     * arrayType
     * 	:	primitiveType dims
     * 	|	classOrInterfaceType dims
     * 	|	typeVariable dims
     * 	;
     * Pushes a Type node onto the stack.
     */
    @Override public void exitArrayType(@NotNull Java8Parser.ArrayTypeContext ctx) {
        TigerNode dims = stack.pop();
        TigerNode arrayType = stack.pop();
        Type at = new Type(arrayType.getName());
        setLocations(at, ctx);
        at.addChild(dims);
        stack.push(at);
    }
    
    /**
     * dims
     * 	:	annotationDim (annotationDim)*
     * 	;
     */
    @Override public void exitDims(@NotNull Java8Parser.DimsContext ctx) { 
        TigerNode dims = new TigerNode();
        setLocations(dims, ctx);
        for (int i = 0; i < ctx.annotationDim().size(); i++) {
            dims.addChild(stack.pop());   
        }
        stack.push(dims);
    }
    
    /**
     * typeParameter
     * 	:	typeParameterModifier* identifier typeBound?
     * 	;
     * Push a Type node onto the stack. Only child will be possibly typeBound.
     */
    @Override public void exitTypeParameter(@NotNull Java8Parser.TypeParameterContext ctx) {
        TigerNode typeBound = ctx.typeBound() == null ? null : stack.pop();
        TigerNode identifier = stack.pop();  
        Type type = new Type(identifier.getName());
        setLocations(type, ctx);
        type.addChild(typeBound);   
        
        ModifierSet m = new ModifierSet();
        if (ctx.typeParameterModifier() != null) {
            for (int i = 0; i < ctx.typeParameterModifier().size(); i++) {
                TigerNode mod = stack.pop();   // annotation or modifier
                if (mod instanceof AnnotationNode) {
                    type.addAnnotation((AnnotationNode)mod);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(mod.getName());   
                }
            }
        }
        type.setModifiers(m.getModifiers());
        
        stack.push(type);
    }

    /**
     * typeParameterModifier
     * 	:	annotation
     * 	;
     */
    @Override public void exitTypeParameterModifier(@NotNull Java8Parser.TypeParameterModifierContext ctx) { 
        // nothing to do
    }

    /**
     * typeBound
     * 	:	'extends' typeVariable
     * 	|	'extends' classOrInterfaceType additionalBound*
     * 	;
     */
    @Override public void exitTypeBound(@NotNull Java8Parser.TypeBoundContext ctx) { 
        if (ctx.typeVariable() != null) {
            TigerNode type = stack.pop();
            setLocations(type, ctx);
            stack.pop();    // extends
            stack.push(type);
        }
        else {
            TigerNode temp = new TigerNode();
            if (ctx.additionalBound() != null) {
                for (int i = 0; i < ctx.additionalBound().size(); i++) {
                    temp.addChild(stack.pop());   
                }
            }
            TigerNode type = stack.pop();   // classOrInterfaceType
            setLocations(type, ctx);
            type.addChildren(temp.getChildren());
            stack.pop();    // extends
            stack.push(type);
        }
    }
    
    /**
     * additionalBound
     * 	:	'&' interfaceType
     * 	;
     */
    @Override public void exitAdditionalBound(@NotNull Java8Parser.AdditionalBoundContext ctx) { 
        TigerNode type = stack.pop();
        stack.pop();    // &
        stack.push(type);
    }
    
    /**
     * typeArguments
     * 	:	'<' typeArgumentList '>'
     * 	;
     */
    @Override public void exitTypeArguments(@NotNull Java8Parser.TypeArgumentsContext ctx) { 
        stack.pop();    // >
        TigerNode list = stack.pop();
        setLocations(list, ctx);
        stack.pop();    // <
        stack.push(list);
    }
    
    /**
     * typeArgumentList
     * 	:	typeArgument (',' typeArgument)*
     * 	;
     */
    @Override public void exitTypeArgumentList(@NotNull Java8Parser.TypeArgumentListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        if (ctx.typeArgument() != null) {
            for (int i = 0; i < ctx.typeArgument().size(); i++) {
                list.addChild(stack.pop());
                if (i < ctx.typeArgument().size() - 1) {
                    stack.pop();    // ,   
                }
            }
        }
        stack.push(list);
    }
    
    /**
     * typeArgument
     * 	:	referenceType
     * 	|	wildcard
     * 	;
     */
    @Override public void exitTypeArgument(@NotNull Java8Parser.TypeArgumentContext ctx) { 
        // nothing to do
    }
    
    /**
     * wildcard
     * 	:	annotation* '?' wildcardBounds?
     * 	;
     */
    @Override public void exitWildcard(@NotNull Java8Parser.WildcardContext ctx) { 
        TigerNode wildcard = new TigerNode();
        setLocations(wildcard, ctx);
        if (ctx.wildcardBounds() != null) {
            wildcard.addChild(stack.pop());   
        }
        
        stack.pop();    // ?
        
        if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                wildcard.addAnnotation((AnnotationNode)stack.pop());   
            }
        }
        stack.push(wildcard);
    }
    
    /**
     * wildcardBounds
     * 	:	'extends' referenceType
     * 	|	    SUPER referenceType
     * 	;
     */
    @Override public void exitWildcardBounds(@NotNull Java8Parser.WildcardBoundsContext ctx) { 
        TigerNode referenceType = stack.pop();
        setLocations(referenceType, ctx);
        stack.pop();    // 'extends' or SUPER
        stack.push(referenceType);
    }

    /**
     * packageName
     * 	:	                identifier
     * 	|	packageName '.' identifier
     * 	;
     */
    @Override public void exitPackageName(@NotNull Java8Parser.PackageNameContext ctx) {
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.packageName() != null) {
            stack.pop();    // .
            String name = stack.pop().getName();
            identifier.setName(name + '.' + identifier.getName());
        }
        stack.push(identifier);   
    }

    /**
     * typeName
     * 	:	                      identifier
     * 	|	packageOrTypeName '.' identifier
     * 	;
     */
    @Override public void exitTypeName(@NotNull Java8Parser.TypeNameContext ctx) { 
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.packageOrTypeName() != null) {
            stack.pop();    // .
            String name = stack.pop().getName();
            identifier.setName(name + '.' + identifier.getName());
        }
        stack.push(identifier);   
    }

    /**
     * packageOrTypeName
     * 	:	                      identifier
     * 	|	packageOrTypeName '.' identifier
     * 	;
     */
    @Override public void exitPackageOrTypeName(@NotNull Java8Parser.PackageOrTypeNameContext ctx) { 
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.packageOrTypeName() != null) {
            stack.pop();    // .
            TigerNode name = stack.pop();
            identifier.setName(name.getName() + '.' + identifier.getName());
        }
        stack.push(identifier);   
    }

    /**
     * expressionName
     * 	:	                  Identifier
     * 	|	ambiguousName '.' Identifier
     * 	;
     */
    @Override public void exitExpressionName(@NotNull Java8Parser.ExpressionNameContext ctx) { 
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.ambiguousName() != null) {
            stack.pop();    // .
            TigerNode an = stack.pop();
            String name = an.getName();
            identifier.setName(name + '.' + identifier.getName());
        }
        stack.push(identifier);   
    }

    /**
     * methodName
     * 	:	Identifier
     * 	;
     */
    @Override public void exitMethodName(@NotNull Java8Parser.MethodNameContext ctx) { 
        // nothing to do
    }

    /**
     * ambiguousName
     * 	:	                  Identifier
     * 	|	ambiguousName '.' Identifier
     * 	;
     */
    @Override public void exitAmbiguousName(@NotNull Java8Parser.AmbiguousNameContext ctx) {
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.ambiguousName() != null) {
            stack.pop();    // .
            TigerNode name = stack.pop();
            identifier.setName(name.getName() + '.' + identifier.getName());
        }
        stack.push(identifier);   
    }

     /**
      * compilationUnit
      * 	:                                          packageDeclaration EOF    
      * 	|	packageDeclaration? importDeclaration* typeDeclaration*   EOF
      * 	|                       importDeclaration* moduleDeclaration  EOF    
      * 	;
      */
    @Override public void exitCompilationUnit(@NotNull Java8Parser.CompilationUnitContext ctx) { 
        cuNode = new CUNode();
        setLocations(cuNode, ctx);
        
        stack.pop();    // EOF
        
        // module declaration
        if (ctx.moduleDeclaration() != null) {
            cuNode.addChild(stack.pop());
        }
        
        // type declarations
        if (ctx.typeDeclaration() != null) {
            for (int i = 0; i < ctx.typeDeclaration().size(); i++) {
                TigerNode child = stack.pop();
                // System.out.println("+++++ dump child: " + child.dump());
                cuNode.addChild(child);   
            }
        }
        
        // import declarations
        if (ctx.importDeclaration() != null) {
            ImportNode importNode = new ImportNode("Imports");
            Location importStart = new Location();
            Location importEnd = new Location();
            for (int i = 0; i < ctx.importDeclaration().size(); i++) {
                TigerNode child = stack.pop();
                importNode.addChild(child);
                if (importStart.compareTo(child.getStartLocation()) > 0) {
                    importStart = child.getStartLocation();
                }
                if (importEnd.compareTo(child.getEndLocation()) < 0) {
                    importEnd = child.getEndLocation();   
                }
            }
            importNode.setStartLocation(importStart);
            importNode.setEndLocation(importEnd);
            cuNode.setImportNode(importNode);
        }
        
        // package declaration
        if (ctx.packageDeclaration() != null) {
            TigerNode packageNode = stack.pop();
            cuNode.setPackage(packageNode);
        }
    }

    /**
     * packageDeclaration
     * 	:	packageModifier* 'package' Identifier ('.' Identifier)* ';'
     * 	;
     */
    @Override public void exitPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) { 
        stack.pop();    // ;
        TigerNode packageNode = new TigerNode();
        setLocations(packageNode, ctx);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ctx.identifier().size(); i++) {
            TigerNode tn = stack.pop();
            sb.insert(0, tn.getName());
            if (i < ctx.identifier().size() - 1) {
                stack.pop();    // .
                sb.insert(0, '.');   // .   
            }
        }
        packageNode.setName(sb.toString());
        
        stack.pop();    // package
        
        ModifierSet m = new ModifierSet();
        if (ctx.packageModifier() != null) {
            for (int i = 0; i < ctx.packageModifier().size(); i++) {
                TigerNode mod = stack.pop();   // annotation or modifier
                if (mod instanceof AnnotationNode) {
                    packageNode.addAnnotation((AnnotationNode)mod);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(mod.getName());   
                }
            }
        }
        packageNode.setModifiers(m.getModifiers());
        
        stack.push(packageNode);
    }

    /**
     * packageModifier
     * 	:	annotation
     * 	;
     */
    @Override public void exitPackageModifier(@NotNull Java8Parser.PackageModifierContext ctx) { 
        // nothing to do
    }

    /**
     * importDeclaration
     * 	:	singleTypeImportDeclaration
     * 	|	typeImportOnDemandDeclaration
     * 	|	singleStaticImportDeclaration
     * 	|	staticImportOnDemandDeclaration
     * 	;
     */
    @Override public void exitImportDeclaration(@NotNull Java8Parser.ImportDeclarationContext ctx) {
        // nothing to do
    }

    /**
     * singleTypeImportDeclaration
     * 	:	'import' typeName ';'
     * 	;
     */
    @Override public void exitSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) {
        stack.pop();    // ;
        TigerNode typeName = stack.pop();
        stack.pop();    // import
        ImportNode importNode = new ImportNode(typeName.getName());
        setLocations(importNode, ctx);
        stack.push(importNode);
    }

    /**
     * typeImportOnDemandDeclaration
     * 	:	'import' packageOrTypeName '.' '*' ';'
     * 	;
     */
    @Override public void exitTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) { 
        stack.pop();    // ;
        stack.pop();    // *
        stack.pop();    // .
        TigerNode typeName = stack.pop();
        stack.pop();    // import
        ImportNode importNode = new ImportNode(typeName.getName() + ".*");
        setLocations(importNode, ctx);
        stack.push(importNode);
    }

    /**
     * singleStaticImportDeclaration
     * 	:	'import' 'static' typeName '.' Identifier ';'
     * 	;
     */
    @Override public void exitSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) { 
        stack.pop();    // ;
        TigerNode identifier = stack.pop();
        stack.pop();    // .
        TigerNode typeName = stack.pop();
        stack.pop();    // static
        stack.pop();    // import
        ImportNode importNode = new ImportNode("static " + typeName.getName() + '.' + identifier.getName());
        setLocations(importNode, ctx);
        stack.push(importNode);
    }

    /**
     * staticImportOnDemandDeclaration
     * 	:	'import' 'static' typeName '.' '*' ';'
     * 	;
     */
    @Override public void exitStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) { 
        stack.pop();    // ;
        stack.pop();    // *
        stack.pop();    // .
        TigerNode typeName = stack.pop();
        stack.pop();    // static
        stack.pop();    // import
        ImportNode importNode = new ImportNode("static " + typeName.getName() + ".*");
        setLocations(importNode, ctx);
        stack.push(importNode);
    }

    /**
     * typeDeclaration
     * 	:	classDeclaration
     * 	|	interfaceDeclaration
     * 	|	';'
     * 	;
     */
    @Override public void exitTypeDeclaration(@NotNull Java8Parser.TypeDeclarationContext ctx) { 
        if (";".equals(ctx.getText())) {
            stack.pop();    // ;   
        }
    }

    /**
     * moduleDeclaration
     *     :   annotation* 'open'? 'module' Identifier ('.' Identifier)* '{' moduleStatement* '}'
     *     ;
     * TODO: this parses correctly but nothing shows in the sidekick tree
     */
    @Override public void exitModuleDeclaration(@NotNull Java8Parser.ModuleDeclarationContext ctx) { 
        stack.pop();    // }
        
        // pop the module statements
        int statementCount = ctx.moduleStatement() == null ? 0 : ctx.moduleStatement().size();
        List<TigerNode> statements = new ArrayList(statementCount);
        if (statementCount > 0) {
            for (int i = 0; i < statementCount; i++) {
                statements.add(stack.pop());
            }
        }
        stack.pop();    // {
        

        // pop the identifiers and build the module name
        StringBuilder sb = new StringBuilder();
        int identifierCount = ctx.Identifier().size();
        for (int i = 0; i < identifierCount; i++) {
            TigerNode identifier = stack.pop();
            sb.append(identifier.getName());
            if (i < identifierCount - 1) {
                stack.pop();    // .
                sb.append('.');   
            }
        }

        // pop 'module' and possibly 'open'
        stack.pop();    // module
        TigerNode openNode = stack.pop();
        if (!"open".equals(openNode.getName())) {
             stack.push(openNode);  // wasn't actually 'open', so put it back   
        }

        // create the module node
        ModuleNode moduleNode = new ModuleNode(sb.toString());
        moduleNode.setVisible(true);
        setLocations(moduleNode, ctx);
        for (TigerNode statement : statements) {
            statement.setVisible(true);
            statement.setModifiers(ModifierSet.PUBLIC);
            moduleNode.addChild(statement);   
        }

        // add annotations, if any
        if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                moduleNode.addAnnotation((AnnotationNode)stack.pop());   
            }
        }

        stack.push(moduleNode);
    }

    /**
     * moduleStatement
     *     :   'requires' requiresModifier* moduleName ';'
     *     |   'exports' packageName ('to' moduleName (',' moduleName)*)? ';'
     *     |   'opens'   packageName ('to' moduleName (',' moduleName)*)? ';'
     *     |   'uses' typeName ';'
     *     |   'provides' typeName 'with' typeName (',' typeName)* ';'
     *     ;
     */
    @Override public void exitModuleStatement(@NotNull Java8Parser.ModuleStatementContext ctx) {
        stack.pop();    // ;
        String text = ctx.getText();
        
        if (text.startsWith("requires")) {
            stack.pop(); // moduleName
            if (ctx.requiresModifier() != null) {
                for (int i = 0; i < ctx.requiresModifier().size(); i++) {
                    stack.pop();   
                }
            }
            stack.pop();    // requires
        }
        else if (text.startsWith("exports") || text.startsWith("opens")) {
            if (ctx.moduleName() != null && ctx.moduleName().size() > 0) {
                for (int i = 0; i < ctx.moduleName().size(); i++) {
                    stack.pop();    // moduleName
                    if (i < ctx.moduleName().size() - 1) {
                        stack.pop();    // ,   
                    }
                }
                stack.pop();    // to
            }
            stack.pop();    // packageName
            stack.pop();    // exports or opens
        }
        else if (text.startsWith("uses")) {
            stack.pop();    // typeName
            stack.pop();    // uses
        }
        else if (text.startsWith("provides")) {
            if (ctx.typeName() != null) {
                // there will be at least 2 typeNames, pop all but the first one
                int size = ctx.typeName().size() - 1;
                for (int i = 0; i < size; i++) {
                    stack.pop();    // typeName
                    if (i < size - 1) {
                        stack.pop();    // ,   
                    }
                }
            }
            stack.pop();    // with
            stack.pop();    // first typeName
            stack.pop();    // provides
        }
        
        // TODO: the text from ctx is poorly formatted, build a string from above processing
        TigerNode moduleStatement = new TigerNode(text);
        setLocations(moduleStatement, ctx);
        stack.push(moduleStatement);
    }

    /**
     * requiresModifier
     *     :   'transitive'
     *     |   'static'
     *     ;
     */
    @Override public void exitRequiresModifier(@NotNull Java8Parser.RequiresModifierContext ctx) {
        // nothing to do
    }

    /**
     * moduleName
     *     :                  Identifier
     *     |   moduleName '.' Identifier
     *     ;
     */
    @Override public void exitModuleName(@NotNull Java8Parser.ModuleNameContext ctx) { 
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.moduleName() != null) {
            stack.pop();    // .
            TigerNode name = stack.pop();
            identifier.setName(name.getName() + '.' + identifier.getName());
        }
        stack.push(identifier);   
    }
    
    /**
     * classDeclaration
     * 	:	normalClassDeclaration
     * 	|	enumDeclaration
     * 	;
     */
    @Override public void exitClassDeclaration(@NotNull Java8Parser.ClassDeclarationContext ctx) { 
        // nothing to do
    }
    
    /**
     * normalClassDeclaration
     * 	:	classModifiers 'class' identifier typeParameters? superclass? superinterfaces? classBody
     * 	;
     */
    @Override public void exitNormalClassDeclaration(@NotNull Java8Parser.NormalClassDeclarationContext ctx) { 
        TigerNode body = stack.pop();
        TigerNode superInterfaces = ctx.superinterfaces() == null ? null : stack.pop();
        TigerNode superClass = ctx.superclass() == null ? null : stack.pop();
        TigerNode params = ctx.typeParameters() == null ? null : stack.pop();
        TigerNode id = stack.pop();
        stack.pop();    // class
        TigerNode mods = stack.pop();
        ClassNode node = new ClassNode(id.getName(), mods.getModifiers());
        setLocations(node, ctx);
        if (mods.getChildCount() > 0) {
            for (int i = 0; i < mods.getChildCount(); i++) {
                TigerNode child = mods.getChildAt(i);
                if (child instanceof AnnotationNode) {
                    node.addAnnotation((AnnotationNode)child);   
                }
            }
        }
        if (superClass != null) {
            node.setExtends(superClass);
        }
        if (superInterfaces != null) {
            node.setImplementsList(superInterfaces.getChildren());
        }
        node.addChild(params);
        node.addChildren(body.getChildren());
        stack.push(node);
    }
    
    /**
     * classModifiers
     *     :   classModifier*
     *     ;
     */
    @Override public void exitClassModifiers(@NotNull Java8Parser.ClassModifiersContext ctx) { 
        TigerNode list = new TigerNode();
        if (ctx.classModifier() != null) {
            setLocations(list, ctx);
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.classModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    list.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            list.setModifiers(m.getModifiers());
        }
        stack.push(list);
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
        // nothing to do
    }
    
    /**
     * typeParameters
     * 	:	'<' typeParameterList '>'
     * 	;
     */
    @Override public void exitTypeParameters(@NotNull Java8Parser.TypeParametersContext ctx) {
        stack.pop();    // >
        TigerNode list = stack.pop();
        setLocations(list, ctx);
        stack.pop();    // <
        stack.push(list);
    }
    
    /**
     * typeParameterList
     * 	:	typeParameter (',' typeParameter)*
     * 	;
     */
    @Override public void exitTypeParameterList(@NotNull Java8Parser.TypeParameterListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.typeParameter().size(); i++) {
            list.addChild(stack.pop());
            if (i < ctx.typeParameter().size() - 1) {
                stack.pop();    // ,   
            }
        }
        stack.push(list);
    }
    
    /**
     * superclass
     * 	:	'extends' classType
     * 	;
     */
    @Override public void exitSuperclass(@NotNull Java8Parser.SuperclassContext ctx) {
        TigerNode type = stack.pop();
        setLocations(type, ctx);
        stack.pop();    // extends
        stack.push(type);
    }
    
    /**
     * superinterfaces
     * 	:	'implements' interfaceTypeList
     * 	;
     */
    @Override public void exitSuperinterfaces(@NotNull Java8Parser.SuperinterfacesContext ctx) { 
        TigerNode list = stack.pop();
        setLocations(list, ctx);
        stack.pop();    // implements
        stack.push(list);
    }
    
    /**
     * interfaceTypeList
     * 	:	interfaceType (',' interfaceType)*
     * 	;
     * Pushes a TigerNode with Type children onto the stack.
     */
    @Override public void exitInterfaceTypeList(@NotNull Java8Parser.InterfaceTypeListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.interfaceType().size(); i++) {
            TigerNode child = stack.pop();
            Type type = new Type(child.getName());
            list.addChild(type);
            if (i < ctx.interfaceType().size() - 1) {
                stack.pop();    // ,   
            }
        }
        stack.push(list);
    }
    
    /**
     * classBody
     * 	:	'{' classBodyDeclaration* '}'
     * 	;
     */
    @Override public void exitClassBody(@NotNull Java8Parser.ClassBodyContext ctx) { 
        stack.pop();    // }
        BlockNode block = new BlockNode();
        setLocations(block, ctx);
        if (ctx.classBodyDeclaration() != null) {
            for (int i = 0; i < ctx.classBodyDeclaration().size(); i++) {
                TigerNode child = stack.pop();
                block.addChild(child);   
            }
        }
        stack.pop();    // {
        stack.push(block);
    }
    
    /**
     * classBodyDeclaration
     * 	:	classMemberDeclaration
     * 	|	instanceInitializer
     * 	|	staticInitializer
     * 	|	constructorDeclaration
     * 	;
     */
    @Override public void exitClassBodyDeclaration(@NotNull Java8Parser.ClassBodyDeclarationContext ctx) {
        // nothing to do
    }
    
    /**
     * classMemberDeclaration
     * 	:	fieldDeclaration
     * 	|	methodDeclaration
     * 	|	classDeclaration
     * 	|	interfaceDeclaration
     * 	|	';'
     * 	;
     */
    @Override public void exitClassMemberDeclaration(@NotNull Java8Parser.ClassMemberDeclarationContext ctx) { 
        if (";".equals(ctx.getText())) {
            stack.pop();    // ;   
        }
    }
    
    /**
     * fieldDeclaration
     * 	:	fieldModifiers unannType variableDeclaratorList ';'
     * 	;
     */
    @Override public void exitFieldDeclaration(@NotNull Java8Parser.FieldDeclarationContext ctx) { 
        stack.pop();    // ;
        TigerNode list = stack.pop();
        
        TigerNode unannType = stack.pop();
        Type type = new Type(unannType.getName());
        type.setStartLocation(unannType.getStartLocation());
        type.setEndLocation(unannType.getEndLocation());

        // modifiers
        TigerNode mods = stack.pop();
        int modifiers = mods.getModifiers();
        
        // variable declarations, make a field node per variable
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < list.getChildCount(); i++) {
            names.append(list.getChildAt(i).getName());
            if (i < list.getChildCount() - 1) {
                names.append(", ");   
            }
        }
                
        FieldNode fn = new FieldNode(names.toString(), modifiers, type);
        setLocations(fn, ctx);
        fn.addAnnotations(mods.getAnnotation());
        if (fn.isPrimitive())
          results.incPrimitiveFieldCount();
        else
          results.incReferenceFieldCount();
        results.incReferenceFieldCount();
        stack.push(fn);
    }
    
    /**
     * fieldModifiers
     *     :   fieldModifier*
     *     ;
     * TODO: fix this, see methodModifiers
     * Pushes a TigerNode with the modifiers set.
     */
    @Override public void exitFieldModifiers(@NotNull Java8Parser.FieldModifiersContext ctx) { 
        TigerNode list = new TigerNode();
        if (ctx.fieldModifier() != null) {
            setLocations(list, ctx);
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.fieldModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    list.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            list.setModifiers(m.getModifiers());
        }
        stack.push(list);
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
    @Override public void exitFieldModifier(@NotNull Java8Parser.FieldModifierContext ctx) {
        // nothing to do
    }
    
    /**
     * variableDeclaratorList
     * 	:	variableDeclarator (',' variableDeclarator)*
     * 	;
     * Pushes a TigerNode whose children are VariableDeclarator nodes.
     */
    @Override public void exitVariableDeclaratorList(@NotNull Java8Parser.VariableDeclaratorListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.variableDeclarator().size(); i++) {
            list.addChild(stack.pop());
            if (i < ctx.variableDeclarator().size() - 1) {
                stack.pop();    // ,   
            }
        }
        stack.push(list);
        
    }
    
    /**
     * variableDeclarator
     * 	:	variableDeclaratorId ('=' variableInitializer)?
     * 	;
     */
    @Override public void exitVariableDeclarator(@NotNull Java8Parser.VariableDeclaratorContext ctx) {
        TigerNode value = null;
        if (ctx.variableInitializer() != null) {
            value = stack.pop();
            stack.pop();    // =
        }
        TigerNode id = stack.pop();
        VariableDeclarator vd = new VariableDeclarator(id.getName());
        setLocations(vd, ctx);
        vd.addChild(value);
        stack.push(vd);
    }
    
    /**
     * variableDeclaratorId
     * 	:	identifier dims?
     * 	;
     */
    @Override public void exitVariableDeclaratorId(@NotNull Java8Parser.VariableDeclaratorIdContext ctx) { 
        TigerNode dims = ctx.dims() == null ? null : stack.pop();
        TigerNode id = stack.pop();
        setLocations(id, ctx);
        id.addChild(dims);
        stack.push(id);
    }
    
    /**
     * variableInitializer
     * 	:	expression
     * 	|	arrayInitializer
     * 	;
     */
    @Override public void exitVariableInitializer(@NotNull Java8Parser.VariableInitializerContext ctx) { 
        // nothing to do
    }
    
    /**
     * unannType
     * 	:	unannPrimitiveType
     * 	|	unannReferenceType
     * 	;
     * Wrap whatever is on the top of the stack as a Type
     */
    @Override public void exitUnannType(@NotNull Java8Parser.UnannTypeContext ctx) { 
        // convert the type to a Type
        TigerNode tn = stack.pop();
        Type type = new Type(tn.getName());
        setLocations(type, ctx);
        type.addChildren(tn.getChildren());
        stack.push(type);
    }
    
    /**
     * unannPrimitiveType
     * 	:	numericType
     * 	|	'boolean'
     * 	;
     * Rolls up to unannType.
     */
    @Override public void exitUnannPrimitiveType(@NotNull Java8Parser.UnannPrimitiveTypeContext ctx) { 
        // nothing to do
    }
    
    /**
     * unannReferenceType
     * 	:	unannClassOrInterfaceType
     * 	|	unannTypeVariable
     * 	|	unannArrayType
     * 	;
     */
    @Override public void exitUnannReferenceType(@NotNull Java8Parser.UnannReferenceTypeContext ctx) { 
        // nothing to do
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
     * Rolls up to unannType.
     */
    @Override public void exitUnannClassOrInterfaceType(@NotNull Java8Parser.UnannClassOrInterfaceTypeContext ctx) { 
        TigerNode temp = null;
        if (ctx.unannInterfaceType_lf_unannClassOrInterfaceType() != null) {
            temp = new TigerNode();
            for (int i = 0; i < ctx.unannInterfaceType_lf_unannClassOrInterfaceType().size(); i++) {
                temp.addChild(stack.pop());   
            }
        }
        if (ctx.unannClassType_lf_unannClassOrInterfaceType() != null) {
            if (temp == null) {
                temp = new TigerNode();   
            }
            for (int i = 0; i < ctx.unannClassType_lf_unannClassOrInterfaceType().size(); i++) {
                temp.addChild(stack.pop());   
            }
        }
        TigerNode type = stack.pop();  // unannClassType_lfno_unannClassOrInterfaceType or unannInterfaceType_lfno_unannClassOrInterfaceType
        setLocations(type, ctx);
        if (temp != null) {
            type.addChildren(temp.getChildren());   
        }
        stack.push(type);
    }
    
    /**
     * unannClassType
     * 	:	                                        identifier typeArguments?
     * 	|	unannClassOrInterfaceType '.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override public void exitUnannClassType(@NotNull Java8Parser.UnannClassTypeContext ctx) { 
        TigerNode args = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode id = stack.pop();
        setLocations(id, ctx);
        if (ctx.unannClassOrInterfaceType() != null) {
            stack.pop();    // .
            TigerNode type = stack.pop();
            id.addChild(type);
        }
        id.addChild(args);
        stack.push(id);
    }
    
    /**
     * unannClassType_lf_unannClassOrInterfaceType
     * 	:	'.' annotationIdentifier typeArguments?
     * 	;
     */
    @Override public void exitUnannClassType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) { 
        TigerNode args = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode id = stack.pop();
        stack.pop();    // .
        TigerNode node = new TigerNode();
        setLocations(node, ctx);
        node.addChildren(id, args);
        stack.push(node);
    }
    
    /**
     * unannClassType_lfno_unannClassOrInterfaceType
     * 	:	identifier typeArguments?
     * 	;
     */
    @Override public void exitUnannClassType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        TigerNode args = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode id = stack.pop();
        setLocations(id, ctx);
        id.addChild(args);
        stack.push(id);
    }
    
    /**
     * unannInterfaceType
     * 	:	unannClassType
     * 	;
     */
    @Override public void exitUnannInterfaceType(@NotNull Java8Parser.UnannInterfaceTypeContext ctx) { 
        // nothing to do
    }
    
    /**
     * unannInterfaceType_lf_unannClassOrInterfaceType
     * 	:	unannClassType_lf_unannClassOrInterfaceType
     * 	;
     */
    @Override public void exitUnannInterfaceType_lf_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) { 
        // nothing to do
    }
    
    /**
     * unannInterfaceType_lfno_unannClassOrInterfaceType
     * 	:	unannClassType_lfno_unannClassOrInterfaceType
     * 	;
     */
    @Override public void exitUnannInterfaceType_lfno_unannClassOrInterfaceType(@NotNull Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) { 
        // nothing to do
    }
    
    /**
     * unannTypeVariable
     * 	:	identifier
     * 	;
     * Rolls up to unannType.
     */
    @Override public void exitUnannTypeVariable(@NotNull Java8Parser.UnannTypeVariableContext ctx) { 
        // nothing to do
    }
    
    /**
     * unannArrayType
     * 	:	unannPrimitiveType dims
     * 	|	unannClassOrInterfaceType dims
     * 	|	unannTypeVariable dims
     * 	;
     * Rolls up to unannType.
     */
    @Override public void exitUnannArrayType(@NotNull Java8Parser.UnannArrayTypeContext ctx) { 
        TigerNode dims = stack.pop();
        TigerNode type = stack.pop();
        setLocations(type, ctx);
        type.addChild(dims);
        stack.push(type);
    }
    
    /**
     * methodDeclaration
     * 	:	methodModifiers methodHeader methodBody
     * 	;
     */
    @Override public void exitMethodDeclaration(@NotNull Java8Parser.MethodDeclarationContext ctx) { 
        
        TigerNode body = stack.pop();
        MethodNode header = (MethodNode)stack.pop();
        TigerNode modifiers = stack.pop();
        MethodNode method = new MethodNode();
        method.setName(header.getName());
        method.setModifiers(modifiers.getModifiers());
        method.setReturnType(header.getReturnType());
        setLocations(method, ctx);
        if (header.hasChildren()) {
            // need throws and annotations
            List<ThrowsNode> throwsList = new ArrayList<ThrowsNode>();
            for (int i = 0; i < header.getChildCount(); i++) {
                TigerNode child = header.getChildAt(i);  
                if (child instanceof ThrowsNode) {
                    throwsList.add((ThrowsNode)child);
                }
                else if (child instanceof AnnotationNode) {
                    method.addAnnotation((AnnotationNode)child);   
                }
            }
            method.setThrows(throwsList);
        }
        method.addChildren(body.getChildren());
        if (modifiers.getChildCount() > 0) {
            for (int i = 0; i < modifiers.getChildCount(); i++) {
                TigerNode child = modifiers.getChildAt(i);
                if (child instanceof AnnotationNode) {
                    method.addAnnotation((AnnotationNode)child);   
                }
            }
        }
        
        stack.push(method);
    }
    
    /**
     * methodModifiers
     *     :   methodModifier*
     *     ;
     * TODO: fix this. This is not necessary, and is a work-around for a flaw in the grammar. See the Java8.g4 file for details.
     */
    @Override public void exitMethodModifiers(@NotNull Java8Parser.MethodModifiersContext ctx) { 
        TigerNode list = new TigerNode();
        if (ctx.methodModifier() != null) {
            setLocations(list, ctx);
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.methodModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    list.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            list.setModifiers(m.getModifiers());
        }
        stack.push(list);
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
    @Override public void exitMethodModifier(@NotNull Java8Parser.MethodModifierContext ctx) { 
        // nothing to do here
    }
    
    /**
     * methodHeader
     * 	:	                           result methodDeclarator throws_?
     * 	|	typeParameters annotation* result methodDeclarator throws_?
     * 	;
     */
    @Override public void exitMethodHeader(@NotNull Java8Parser.MethodHeaderContext ctx) { 
        TigerNode throws_ = ctx.throws_() == null ? null : stack.pop();
        MethodNode decl = (MethodNode)stack.pop();
        setLocations(decl, ctx);
        TigerNode result = stack.pop();
        Type returnType = new Type(result.getName());
        decl.setReturnType(returnType);
        if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                decl.addAnnotation((AnnotationNode)stack.pop());   
            }
        }
        TigerNode params = ctx.typeParameters() == null ? null : stack.pop();
        decl.addChild(params);
        if (throws_ != null) {
            decl.setThrows(throws_.getChildren());   
        }
        stack.push(decl);
    }
    
    /**
     * result
     * 	:	unannType
     * 	|	'void'
     * 	;
     */
    @Override public void exitResult(@NotNull Java8Parser.ResultContext ctx) { 
        // nothing to do here
        
    }
    
    /**
     * methodDeclarator
     * 	:	identifier '(' formalParameterList? ')' dims?
     * 	;
     */
    @Override public void exitMethodDeclarator(@NotNull Java8Parser.MethodDeclaratorContext ctx) {
        
        TigerNode dims = ctx.dims() == null ? null : stack.pop();
        stack.pop();    // )
        TigerNode params = ctx.formalParameterList() == null ? null : stack.pop();
        stack.pop();    // (
        TigerNode id = stack.pop();
        MethodNode m = new MethodNode();
        setLocations(m, ctx);
        m.setName(id.getName());
        m.addChildren(params, dims);
        stack.push(m);
    }
    
    /**
     * formalParameterList
     * 	:	formalParameters ',' lastFormalParameter
     * 	|	                     lastFormalParameter
     * 	;
     */
    @Override public void exitFormalParameterList(@NotNull Java8Parser.FormalParameterListContext ctx) {
        // only need to handle the first choice
        if (ctx.formalParameters() != null) {
            TigerNode last = stack.pop();
            stack.pop();    // ,
            TigerNode params = stack.pop();
            TigerNode list = new TigerNode();
            setLocations(list, ctx);
            list.addChildren(params.getChildren());
            list.addChild(last);
            stack.push(list);
        }
    }
    
    /**
     * formalParameters
     * 	:	  formalParameter (',' formalParameter)*
     * 	|	receiverParameter (',' formalParameter)*
     * 	;
     */
    @Override public void exitFormalParameters(@NotNull Java8Parser.FormalParametersContext ctx) { 
        TigerNode params = new TigerNode();
        setLocations(params, ctx);
        int size = ctx.formalParameter() == null ? 0 : ctx.formalParameter().size();
        if (ctx.receiverParameter() != null) {
            size += 1;
        }
        for (int i = 0; i < size; i++) {
            params.addChild(stack.pop());
            if (i < size - 1) {
                stack.pop();    // ,   
            }
        }
        stack.push(params);
    }

    /**
     * formalParameter
     * 	:	variableModifier* unannType variableDeclaratorId
     * 	;
     */
    @Override public void exitFormalParameter(@NotNull Java8Parser.FormalParameterContext ctx) { 
        TigerNode var = stack.pop();
        setLocations(var, ctx);
        TigerNode unannType = stack.pop();
        if (ctx.variableModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.variableModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    unannType.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            var.setModifiers(m.getModifiers());
        }
        var.setType((Type)unannType);
        stack.push(var);
    }
    
    /**
     * variableModifier
     * 	:	annotation
     * 	|	'final'
     * 	;
     */
    @Override public void exitVariableModifier(@NotNull Java8Parser.VariableModifierContext ctx) { 
        // nothing to do here
    }
    
    /**
     * lastFormalParameter
     * 	:	variableModifier* unannType annotation* '...' variableDeclaratorId
     * 	|	formalParameter
     * 	;
     */
    @Override public void exitLastFormalParameter(@NotNull Java8Parser.LastFormalParameterContext ctx) { 
        if (ctx.variableDeclaratorId() != null) {
            // first choice
            TigerNode id = stack.pop();
            setLocations(id, ctx);
            stack.pop();    // ...
            List<AnnotationNode> annotations = null;
            if (ctx.annotation() != null) {
                annotations = new ArrayList<AnnotationNode>();
                for (int i = 0; i < ctx.annotation().size(); i++) {
                    annotations.add((AnnotationNode)stack.pop());   
                }
            }
            TigerNode type = stack.pop();
            if (annotations != null) {
                id.addAnnotations(annotations);   
            }
            id.setType((Type)type);
            stack.push(id);
        }
        // nothing to do for formalParameter
    }
    
    /**
     * receiverParameter
     * 	:	annotation* unannType (identifier '.')? 'this'
     * 	;
     */
    @Override public void exitReceiverParameter(@NotNull Java8Parser.ReceiverParameterContext ctx) { 
        TigerNode param = stack.pop();
        setLocations(param, ctx);
        TigerNode identifier = null;
        if (ctx.identifier() != null) {
            stack.pop();    // .
            identifier = stack.pop();
            param.setName(identifier.getName() + '.' + param.getName());
        }
        TigerNode type = stack.pop();
        param.setType((Type)type);
        if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                param.addAnnotation((AnnotationNode)stack.pop()); 
            }
        }
        stack.push(param);
    }
    
    /**
     * throws_
     * 	:	'throws' exceptionTypeList
     * 	;
     * Push a TigerNode whose only child is a list of ThrowsNodes onto the stack.
     */
    @Override public void exitThrows_(@NotNull Java8Parser.Throws_Context ctx) { 
        TigerNode list = stack.pop();
        setLocations(list, ctx);
        stack.pop();    // throws
        stack.push(list);
    }
    
    /**
     * exceptionTypeList
     * 	:	exceptionType (',' exceptionType)*
     * 	;
     */
    @Override public void exitExceptionTypeList(@NotNull Java8Parser.ExceptionTypeListContext ctx) { 
        TigerNode list = new TigerNode("exceptionTypeList");
        setLocations(list, ctx);
        int count = ctx.exceptionType().size();
        for (int i = 0; i < count; i++) {
            TigerNode type = stack.pop();
            if (count > 1 && i < count - 1) {
                stack.pop();    // ,   
            }
            ThrowsNode tn = new ThrowsNode(type.getName());
            tn.setStartLocation(type.getStartLocation());
            tn.setEndLocation(type.getEndLocation());
            list.addChild(tn);   
        }
        stack.push(list);
    }
    
    /**
     * exceptionType
     * 	:	classType
     * 	|	typeVariable
     * 	;
     */
    @Override public void exitExceptionType(@NotNull Java8Parser.ExceptionTypeContext ctx) { 
        // nothing to do
    }
    
    /**
     * methodBody
     * 	:	block
     * 	|	';'
     * 	;
     */
    @Override public void exitMethodBody(@NotNull Java8Parser.MethodBodyContext ctx) { 
        //if (";".equals(ctx.getText())) {
        //    stack.pop();    // ;   
        //}
        // methodBody must put something on the stack, even if it's just ';'
    }
    
    /**
     * instanceInitializer
     * 	:	block
     * 	;
     */
    @Override public void exitInstanceInitializer(@NotNull Java8Parser.InstanceInitializerContext ctx) {
        // nothing to do
    }
    
    /**
     * staticInitializer
     * 	:	'static' block
     * 	;
     */
    @Override public void exitStaticInitializer(@NotNull Java8Parser.StaticInitializerContext ctx) { 
        TigerNode block = stack.pop();
        setLocations(block, ctx);
        stack.pop();    // static
        block.setModifiers(ModifierSet.getModifiers("static"));
        stack.push(block);
    }
    
    /**
     * constructorDeclaration
     * 	:	constructorModifiers constructorDeclarator throws_? constructorBody
     * 	;
     */
    @Override public void exitConstructorDeclaration(@NotNull Java8Parser.ConstructorDeclarationContext ctx) { 
        ConstructorNode constructor = new ConstructorNode();
        setLocations(constructor, ctx);
        TigerNode body = stack.pop();   // body
        TigerNode throws_ = ctx.throws_() == null ? null : stack.pop();
        if (throws_ != null) {
            constructor.setThrows(throws_.getChildren());   
        }
        TigerNode declarator = stack.pop();
        constructor.setName(declarator.getName());
        TigerNode modifiers = stack.pop();
        constructor.addAnnotations(modifiers.getAnnotation());
        constructor.setModifiers(modifiers.getModifiers());
        constructor.addChildren(declarator, body);
        stack.push(constructor);
    }

    /**
     * constructorModifiers
     *     :   constructorModifier*
     *     ;
     */
    @Override public void exitConstructorModifiers(@NotNull Java8Parser.ConstructorModifiersContext ctx) { 
        TigerNode list = new TigerNode();
        if (ctx.constructorModifier() != null) {
            setLocations(list, ctx);
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.constructorModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    list.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            list.setModifiers(m.getModifiers());
        }
        stack.push(list);
    }
    
    /**
     * constructorModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'protected'
     * 	|	'private'
     * 	;
     */
    @Override public void exitConstructorModifier(@NotNull Java8Parser.ConstructorModifierContext ctx) {
        // nothing to do
    }
    
    /**
     * constructorDeclarator
     * 	:	typeParameters? simpleTypeName '(' formalParameterList? ')'
     * 	;
     */
    @Override public void exitConstructorDeclarator(@NotNull Java8Parser.ConstructorDeclaratorContext ctx) { 
        stack.pop();    // )
        TigerNode list = ctx.formalParameterList() == null ? null : stack.pop();
        stack.pop();    // (
        TigerNode name = stack.pop();
        setLocations(name, ctx);
        TigerNode parameters = ctx.typeParameters() == null ? null : stack.pop();
        name.addChildren(list, parameters);
        stack.push(name);
    }
    
    /**
     * simpleTypeName
     * 	:	identifier
     * 	;
     */
    @Override public void exitSimpleTypeName(@NotNull Java8Parser.SimpleTypeNameContext ctx) { 
        // nothing to do
    }
    
    /**
     * constructorBody
     * 	:	'{' explicitConstructorInvocation? blockStatements? '}'
     * 	;
     */
    @Override public void exitConstructorBody(@NotNull Java8Parser.ConstructorBodyContext ctx) {
        BlockNode block = new BlockNode();
        setLocations(block, ctx);
        stack.pop();    // }
        if (ctx.blockStatements() != null) {
            TigerNode stmts = stack.pop();
            block.addChildren(stmts.getChildren());
        }
        TigerNode inv = ctx.explicitConstructorInvocation() == null ? null : stack.pop(); 
        block.addChild(inv);
        stack.pop();    // {
        stack.push(block);
    }
    
    /**
     * explicitConstructorInvocation
     * 	:	                   typeArguments? 'this' '(' argumentList? ')' ';'
     * 	|	                   typeArguments? SUPER  '(' argumentList? ')' ';'
     * 	|	expressionName '.' typeArguments? SUPER  '(' argumentList? ')' ';'
     * 	|	primary        '.' typeArguments? SUPER  '(' argumentList? ')' ';'
     * 	;
     */
    @Override public void exitExplicitConstructorInvocation(@NotNull Java8Parser.ExplicitConstructorInvocationContext ctx) { 
        stack.pop();    // ;
        stack.pop();    // )
        TigerNode list = ctx.argumentList() == null ? null : stack.pop();
        stack.pop();    // (
        stack.pop();    // 'this' or SUPER
        TigerNode args = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode name = null;
        if (ctx.expressionName() != null || ctx.primary() != null) {
            stack.pop();    // .
            name = stack.pop();
        }
        else {
            name = new TigerNode();
        }
        setLocations(name, ctx);
        name.addChildren(args, list);
        stack.push(name);
    }
    
    /**
     * enumDeclaration
     * 	:	classModifiers 'enum' identifier superinterfaces? enumBody
     * 	;
     */
    @Override public void exitEnumDeclaration(@NotNull Java8Parser.EnumDeclarationContext ctx) { 
        TigerNode body = stack.pop();
        TigerNode interfaces = ctx.superinterfaces() == null ? null : stack.pop();
        TigerNode identifier = stack.pop();
        stack.pop();    // enum
        TigerNode enum_ = new TigerNode(identifier.getName());
        setLocations(enum_, ctx);
        TigerNode mods = stack.pop();
        enum_.setModifiers(mods.getModifiers());
        enum_.addAnnotations(mods.getAnnotation());
        enum_.addChildren(identifier, interfaces, body);
        stack.push(enum_);
    }
    
    /**
     * enumBody
     * 	:	'{' enumConstantList? COMMA? enumBodyDeclarations? '}'
     * 	;
     */
    @Override public void exitEnumBody(@NotNull Java8Parser.EnumBodyContext ctx) { 
        // TODO: this should make a BlockNode
        TigerNode enumBody = new TigerNode();
        setLocations(enumBody, ctx);
        stack.pop();    // }
        TigerNode declarations = ctx.enumBodyDeclarations() == null ? null : stack.pop();
        if (ctx.COMMA() != null){
            stack.pop();    // COMMA
        }
        TigerNode list = ctx.enumConstantList() == null ? null : stack.pop();
        stack.pop();    // {
        enumBody.addChildren(declarations, list);
        stack.push(enumBody);
    }
    
    /**
     * enumConstantList
     * 	:	enumConstant (',' enumConstant)*
     * 	;
     */
    @Override public void exitEnumConstantList(@NotNull Java8Parser.EnumConstantListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.enumConstant().size(); i++) {
            list.addChild(stack.pop());
            if (i < ctx.enumConstant().size() - 1) {
                stack.pop();    // ,   
            }
        }
    }
    
    /**
     * enumConstant
     * 	:	enumConstantModifier* identifier ('(' argumentList? ')')? classBody?
     * 	;
     */
    @Override public void exitEnumConstant(@NotNull Java8Parser.EnumConstantContext ctx) { 
        TigerNode body = ctx.classBody() == null ? null : stack.pop();
        TigerNode list = null;
        if (ctx.argumentList() != null) {
            stack.pop();    // )
            list = stack.pop();
            stack.pop();    // (
        }
        TigerNode id = stack.pop();
        setLocations(id, ctx);
        if (ctx.enumConstantModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.enumConstantModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation
                if (node instanceof AnnotationNode) {
                    id.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            id.setModifiers(m.getModifiers());
        }
        id.addChildren(list, body);
        stack.push(id);
    }
    
    /**
     * enumConstantModifier
     * 	:	annotation
     * 	;
     */
    @Override public void exitEnumConstantModifier(@NotNull Java8Parser.EnumConstantModifierContext ctx) { 
        // nothing to do
    }
    
    /**
     * enumBodyDeclarations
     * 	:	';' classBodyDeclaration*
     * 	;
     */
    @Override public void exitEnumBodyDeclarations(@NotNull Java8Parser.EnumBodyDeclarationsContext ctx) { 
        if (ctx.classBodyDeclaration() != null) {
            TigerNode list = new TigerNode();
            setLocations(list, ctx);
            for (int i = 0; i < ctx.classBodyDeclaration().size(); i++) {
                list.addChild(stack.pop());   
            }
            stack.pop();    // ;
            stack.push(list);
        }
        // enumBodyDeclarations must push something on the stack, even if it's just ';'
    }
    
    /**
     * interfaceDeclaration
     * 	:	normalInterfaceDeclaration
     * 	|	annotationTypeDeclaration
     * 	;
     */
    @Override public void exitInterfaceDeclaration(@NotNull Java8Parser.InterfaceDeclarationContext ctx) {
        // nothing to do
    }
    
    /**
     * normalInterfaceDeclaration
     * 	:	interfaceModifiers 'interface' identifier typeParameters? extendsInterfaces? interfaceBody
     * 	;
     * TODO: this should be:
     *      interfaceModifier* 'interface' identifier typeParameters? extendsInterfaces? interfaceBody
     * There is no need for interfaceModifiers
     */
    @Override public void exitNormalInterfaceDeclaration(@NotNull Java8Parser.NormalInterfaceDeclarationContext ctx) {
        TigerNode body = stack.pop();
        TigerNode extendsInterfaces = ctx.extendsInterfaces() == null ? null : stack.pop();
        TigerNode typeParameters = ctx.typeParameters() == null ? null : stack.pop();
        TigerNode identifier = stack.pop();
        InterfaceNode node = new InterfaceNode(identifier.getName());
        setLocations(node, ctx);
        stack.pop();    // interface
        TigerNode modifiers = stack.pop();
        node.addAnnotations(modifiers.getAnnotation());
        node.setModifiers(modifiers.getModifiers());
        node.addChildren(typeParameters, extendsInterfaces, body);
        stack.push(node);
    }
    
    /**
     * interfaceModifiers
     *     : interfaceModifier*
     *     ;
     * TODO: fix this. This is not necessary, and is a work-around for a flaw in the grammar. See the Java8.g4 file for details.
     */
    @Override public void exitInterfaceModifiers(@NotNull Java8Parser.InterfaceModifiersContext ctx) { 
        TigerNode list = new TigerNode();
        if (ctx.interfaceModifier() != null) {
            setLocations(list, ctx);
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.interfaceModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    list.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            list.setModifiers(m.getModifiers());
        }
        stack.push(list);
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
    @Override public void exitInterfaceModifier(@NotNull Java8Parser.InterfaceModifierContext ctx) { 
        // nothing to do
    }
    
    /**
     * extendsInterfaces
     * 	:	'extends' interfaceTypeList
     * 	;
     */
    @Override public void exitExtendsInterfaces(@NotNull Java8Parser.ExtendsInterfacesContext ctx) { 
        TigerNode list = stack.pop();
        setLocations(list, ctx);
        stack.pop();    // extends
        stack.push(list);
    }
    
    /**
     * interfaceBody
     * 	:	'{' interfaceMemberDeclaration* '}'
     * 	;
     */
    @Override public void exitInterfaceBody(@NotNull Java8Parser.InterfaceBodyContext ctx) {
        stack.pop();    // }
        BlockNode block = new BlockNode();
        setLocations(block, ctx);
        if (ctx.interfaceMemberDeclaration() != null) {
            for (int i = 0; i < ctx.interfaceMemberDeclaration().size(); i++) {
                block.addChild(stack.pop());
            }
        }
        stack.pop();    // {
        stack.push(block);
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
    @Override public void exitInterfaceMemberDeclaration(@NotNull Java8Parser.InterfaceMemberDeclarationContext ctx) { 
        if (";".equals(ctx.getText())) {
            stack.pop();    // ;   
        }
    }
    
    /**
     * constantDeclaration
     * 	:	constantModifier* unannType variableDeclaratorList ';'
     * 	;
     */
    @Override public void exitConstantDeclaration(@NotNull Java8Parser.ConstantDeclarationContext ctx) { 
        stack.pop();    // ;
        TigerNode list = stack.pop();
        TigerNode type = stack.pop();
        VariableDeclarator vd = new VariableDeclarator();
        setLocations(vd, ctx);
        vd.setType((Type)type);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.getChildCount(); i++) {
            TigerNode child = list.getChildAt(i);
            sb.append(child.getName());
            if (i < list.getChildCount() - 1) {
                sb.append(", ");   
            }
        }
        vd.setName(sb.toString());
        
        if (ctx.constantModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.constantModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    vd.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            vd.setModifiers(m.getModifiers());
        }
        stack.push(vd);
    }
    
    /**
     * constantModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'static'
     * 	|	'final'
     * 	;
     */
    @Override public void exitConstantModifier(@NotNull Java8Parser.ConstantModifierContext ctx) {
        // nothing to do
    }

    /**
     * interfaceMethodDeclaration
     * 	:	interfaceMethodModifier* methodHeader methodBody
     * 	;
     */
    @Override public void exitInterfaceMethodDeclaration(@NotNull Java8Parser.InterfaceMethodDeclarationContext ctx) {
        TigerNode body = stack.pop();
        TigerNode header = stack.pop();
        setLocations(header, ctx);
        if (ctx.interfaceMethodModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.interfaceMethodModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    header.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            header.setModifiers(m.getModifiers());
        }
        header.addChild(body);
        stack.push(header);
    }
    
    /**
     * interfaceMethodModifier
     * 	:	annotation
     * 	|	'public'
     *  |   'private'       // danson, java 9 allows private interface methods 	
     * 	|	'abstract'
     * 	|	'default'
     * 	|	'static'
     * 	|	'strictfp'
     * 	;
     */
    @Override public void exitInterfaceMethodModifier(@NotNull Java8Parser.InterfaceMethodModifierContext ctx) { 
        // nothing to do
    }
    
    /**
     * annotationTypeDeclaration
     * 	:	interfaceModifier* '@' 'interface' identifier annotationTypeBody
     * 	;
     */
    @Override public void exitAnnotationTypeDeclaration(@NotNull Java8Parser.AnnotationTypeDeclarationContext ctx) { 
        TigerNode annotationTypeBody = stack.pop();
        TigerNode identifier = stack.pop();
        stack.pop();    // interface
        stack.pop();    // @
        InterfaceNode in = new InterfaceNode(identifier.getName());
        setLocations(in, ctx);
        if (ctx.interfaceModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.interfaceModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    in.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            in.setModifiers(m.getModifiers());
        }
        in.addChildren(annotationTypeBody.getChildren());
        stack.push(in);
    }
    
    /**
     * annotationTypeBody
     * 	:	'{' annotationTypeMemberDeclaration* '}'
     * 	;
     */
    @Override public void exitAnnotationTypeBody(@NotNull Java8Parser.AnnotationTypeBodyContext ctx) { 
        stack.pop();    // }
        BlockNode block = new BlockNode();
        setLocations(block, ctx);
        if (ctx.annotationTypeMemberDeclaration() != null) {
            for (int i = 0; i < ctx.annotationTypeMemberDeclaration().size(); i++) {
                block.addChild(stack.pop());   
            }
        }
        stack.pop();    // {
        stack.push(block);
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
    @Override public void exitAnnotationTypeMemberDeclaration(@NotNull Java8Parser.AnnotationTypeMemberDeclarationContext ctx) {
        if (";".equals(ctx.getText())) {
            stack.pop();    // ;   
        }
    }
    
    /**
     * annotationTypeElementDeclaration
     * 	:	annotationTypeElementModifier* unannType identifier '(' ')' dims? defaultValue? ';'
     * 	;
     */
    @Override public void exitAnnotationTypeElementDeclaration(@NotNull Java8Parser.AnnotationTypeElementDeclarationContext ctx) { 
        stack.pop();    // ;
        TigerNode value = ctx.defaultValue() == null ? null : stack.pop();
        TigerNode dims = ctx.dims() == null ? null : stack.pop();
        stack.pop();    // )
        stack.pop();    // (
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        TigerNode type = stack.pop();
        identifier.setType((Type)type);
        if (ctx.annotationTypeElementModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.annotationTypeElementModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    identifier.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            identifier.setModifiers(m.getModifiers());
        }
        identifier.addChildren(dims, value);
        stack.push(identifier);
    }
    
    /**
     * annotationTypeElementModifier
     * 	:	annotation
     * 	|	'public'
     * 	|	'abstract'
     * 	;
     */
    @Override public void exitAnnotationTypeElementModifier(@NotNull Java8Parser.AnnotationTypeElementModifierContext ctx) { 
        // nothing to do
    }
    
    /**
     * defaultValue
     * 	:	'default' elementValue
     * 	;
     */
    @Override public void exitDefaultValue(@NotNull Java8Parser.DefaultValueContext ctx) { 
        TigerNode value = stack.pop();
        setLocations(value, ctx);
        stack.pop();    // default
        stack.push(value);
    }

    /**
     * annotation
     * 	:	normalAnnotation
     * 	|	markerAnnotation
     * 	|	singleElementAnnotation
     * 	;
     */
    @Override public void exitAnnotation(@NotNull Java8Parser.AnnotationContext ctx) { 
        // nothing to do
    }

    /**
     * annotationIdentifier
     *     :   annotation* Identifier
     *     ;
     */
    @Override public void exitAnnotationIdentifier(@NotNull Java8Parser.AnnotationIdentifierContext ctx) { 
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                AnnotationNode an = (AnnotationNode)stack.pop();
                identifier.addAnnotation(an);
            }
        }
        stack.push(identifier);
    }

    /**
     * annotationDim
     *     : annotation* squareBrackets
     *     ;
     */
    @Override public void exitAnnotationDim(@NotNull Java8Parser.AnnotationDimContext ctx) { 
        stack.pop();    // []
        TigerNode node = new TigerNode();
        setLocations(node, ctx);
        StringBuilder sb = new StringBuilder();
        if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                AnnotationNode an = (AnnotationNode)stack.pop();
                node.addAnnotation(an);
                sb.append(an.getName()).append(' ');
            }
        }
        node.setName(sb.toString());
        stack.push(node);
    }

    /**
     * normalAnnotation
     * 	:	'@' typeName '(' elementValuePairList? ')'
     * 	;
     */
    @Override public void exitNormalAnnotation(@NotNull Java8Parser.NormalAnnotationContext ctx) { 
        stack.pop();    // )
        TigerNode pairs = stack.pop();
        stack.pop();    // (
        TigerNode typeName = stack.pop();
        stack.pop();    // @
        AnnotationNode an = new AnnotationNode(typeName.getName());
        if (pairs.hasChildren()) {
            an.addChildren(pairs.getChildren());   
        }
        setLocations(an, ctx);
        stack.push(an);
    }

    /**
     * elementValuePairList
     * 	:	elementValuePair (',' elementValuePair)*
     * 	;
     */
    @Override public void exitElementValuePairList(@NotNull Java8Parser.ElementValuePairListContext ctx) {
        TigerNode list = new TigerNode();
        if (ctx.elementValuePair() != null) {
            for (int i = 0; i < ctx.elementValuePair().size(); i++) {
                TigerNode pair = stack.pop();
                if (i < ctx.elementValuePair().size() - 1) {
                    stack.pop();    // ,   
                }
                list.addChild(pair);
            }
        }
        stack.push(list);
    }

    /**
     * elementValuePair
     * 	:	Identifier '=' elementValue
     * 	;
     */
    @Override public void exitElementValuePair(@NotNull Java8Parser.ElementValuePairContext ctx) {
        TigerNode elementValue = stack.pop();
        stack.pop();    // =
        TigerNode identifier = stack.pop();
        identifier.addChild(elementValue);
        setLocations(identifier, ctx);
        stack.push(identifier);
    }

    /**
     * elementValue
     * 	:	conditionalExpression
     * 	|	elementValueArrayInitializer
     * 	|	annotation
     * 	;
     */
    @Override public void exitElementValue(@NotNull Java8Parser.ElementValueContext ctx) { 
        // nothing to do
    }

    /**
     * elementValueArrayInitializer
     * 	:	'{' elementValueList? COMMA? '}'
     * 	;
     */
    @Override public void exitElementValueArrayInitializer(@NotNull Java8Parser.ElementValueArrayInitializerContext ctx) {
        BlockNode block = new BlockNode();
        setLocations(block, ctx);
        stack.pop();    // }
        if (ctx.COMMA() != null) {
            stack.pop();    // ,   
        }
        TigerNode list = stack.pop();
        block.addChildren(list.getChildren());
        stack.pop();    // {
        stack.push(block);
    }

    /**
     * elementValueList
     * 	:	elementValue (',' elementValue)*
     * 	;
     */
    @Override public void exitElementValueList(@NotNull Java8Parser.ElementValueListContext ctx) { 
        TigerNode node = new TigerNode();
        setLocations(node, ctx);
        if (ctx.elementValue() != null) {
            for (int i = 0; i < ctx.elementValue().size(); i++) {
                TigerNode value = stack.pop();
                node.addChild(value);
                if (i < ctx.elementValue().size() - 1) {
                    stack.pop();    // ,   
                }
            }
        }
        stack.push(node);
    }

    /**
     * markerAnnotation
     * 	:	'@' typeName
     * 	;
     */
    @Override public void exitMarkerAnnotation(@NotNull Java8Parser.MarkerAnnotationContext ctx) {
        TigerNode typeName = stack.pop();
        stack.pop();    // @
        AnnotationNode an = new AnnotationNode(typeName.getName());
        setLocations(an, ctx);
        stack.push(an);
    }

    /**
     * singleElementAnnotation
     * 	:	'@' typeName '(' elementValue ')'
     * 	;
     */
    @Override public void exitSingleElementAnnotation(@NotNull Java8Parser.SingleElementAnnotationContext ctx) { 
        stack.pop();    // )
        TigerNode elementValue = stack.pop();
        stack.pop();    // (
        TigerNode typeName = stack.pop();
        stack.pop();    // @
        AnnotationNode an = new AnnotationNode(typeName.getName());
        setLocations(an, ctx);
        an.addChild(elementValue);
        stack.push(an);
    }
    
    /**
     * arrayInitializer
     * 	:	'{' variableInitializerList? COMMA? '}'
     * 	;
     */
    @Override public void exitArrayInitializer(@NotNull Java8Parser.ArrayInitializerContext ctx) { 
        BlockNode block = new BlockNode();
        setLocations(block, ctx);
        
        stack.pop();    // }
        if (ctx.COMMA() != null) {
            stack.pop();    // COMMA   
        }
        TigerNode list = ctx.variableInitializerList() == null ? null : stack.pop();
        if (list != null) {
            block.addChildren(list.getChildren());   
        }
        stack.pop();    // {
        stack.push(block);
    }
    
    /**
     * variableInitializerList
     * 	:	variableInitializer (',' variableInitializer)*
     * 	;
     */
    @Override public void exitVariableInitializerList(@NotNull Java8Parser.VariableInitializerListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.variableInitializer().size(); i++) {
            list.addChild(stack.pop());
            if (i < ctx.variableInitializer().size() - 1) {
                stack.pop();    // ,   
            }
        }
        stack.push(list);
    }
    
    /**
     * block
     * 	:	'{' blockStatements? '}'
     * 	;
     */
    @Override public void exitBlock(@NotNull Java8Parser.BlockContext ctx) {
        BlockNode block = new BlockNode();
        setLocations(block, ctx);
        stack.pop();    // }
        TigerNode list = ctx.blockStatements() == null ? null : stack.pop();
        if (list != null) {
            block.addChildren(list.getChildren());   
        }
        stack.pop();    // {
        stack.push(block);
    }
    
    /**
     * blockStatements
     * 	:	blockStatement blockStatement*
     * 	;
     * Pushes a BlockNode onto the stack;
     */
    @Override public void exitBlockStatements(@NotNull Java8Parser.BlockStatementsContext ctx) { 
        BlockNode block = new BlockNode();
        for (int i = 0; i < ctx.blockStatement().size(); i++) {
            TigerNode child = stack.pop();
            // TODO: add the children of the block or just the block itself? children
            if (child instanceof BlockNode) {
                block.addChildren(child.getChildren());
            }
            else {
                block.addChild(child);
            }
        }
        setLocations(block, ctx);
        stack.push(block);
    }
    
    /**
     * blockStatement
     * 	:	localVariableDeclarationStatement
     * 	|	classDeclaration
     * 	|	statement
     * 	;
     */
    @Override public void exitBlockStatement(@NotNull Java8Parser.BlockStatementContext ctx) {
        // nothing to do
    }
    
    /**
     * localVariableDeclarationStatement
     * 	:	localVariableDeclaration ';'
     * 	;
     */
    @Override public void exitLocalVariableDeclarationStatement(@NotNull Java8Parser.LocalVariableDeclarationStatementContext ctx) { 
        stack.pop();    // ;
    }
    
    /**
     * localVariableDeclaration
     * 	:	variableModifier* unannType variableDeclaratorList
     * 	;
     * NOTE: the .jj version created a separate node for each variable in the list.
     * This method does the same, but puts the various variable declaration nodes
     * as children of a block node.
     * Change this to have just one node...
     */
    @Override public void exitLocalVariableDeclaration(@NotNull Java8Parser.LocalVariableDeclarationContext ctx) { 
        TigerNode list = stack.pop();   // children of this list will be VariableDeclarator nodes
        Type unannType = (Type)stack.pop();
        ModifierSet m = new ModifierSet();
        List<AnnotationNode> annotations = new ArrayList<AnnotationNode>();
        if (ctx.variableModifier() != null) {
            for (int i = 0; i < ctx.variableModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    m.addModifier("annotation");
                    annotations.add((AnnotationNode)node);
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
        }
        VariableDeclarator vd = new VariableDeclarator();
        vd.setModifiers(m.getModifiers());
        vd.addAnnotations(annotations);
        vd.setType(unannType);
        setLocations(vd, ctx);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.getChildCount(); i++) {
            TigerNode node = list.getChildAt(i);
            sb.append(node.getName());
            if (i < list.getChildCount() - 1) {
                sb.append(", ");    
            }
        }
        vd.setName(sb.toString());
        stack.push(vd);
    }
    
    /**
     * statement
     * 	:	statementWithoutTrailingSubstatement
     * 	|	labeledStatement
     * 	|	ifThenStatement
     * 	|	ifThenElseStatement
     * 	|	whileStatement
     * 	|	forStatement
     * 	;
     */
    @Override public void exitStatement(@NotNull Java8Parser.StatementContext ctx) { 
        // nothing to do
    }
    
    /**
     * statementNoShortIf
     * 	:	statementWithoutTrailingSubstatement
     * 	|	labeledStatementNoShortIf
     * 	|	ifThenElseStatementNoShortIf
     * 	|	whileStatementNoShortIf
     * 	|	forStatementNoShortIf
     * 	;
     */
    @Override public void exitStatementNoShortIf(@NotNull Java8Parser.StatementNoShortIfContext ctx) { 
        // nothing to do
    }
    
    /**
     * statementWithoutTrailingSubstatement
     * 	:	block
     * 	|	emptyStatement
     * 	|	expressionStatement
     * 	|	assertStatement
     * 	|	switchStatement
     * 	|	doStatement
     * 	|	breakStatement
     * 	|	continueStatement
     * 	|	returnStatement
     * 	|	synchronizedStatement
     * 	|	throwStatement
     * 	|	tryStatement
     * 	;
     */
    @Override public void exitStatementWithoutTrailingSubstatement(@NotNull Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
        // nothing to do
    }
    
    /**
     * emptyStatement
     * 	:	';'
     * 	;
     */
    @Override public void exitEmptyStatement(@NotNull Java8Parser.EmptyStatementContext ctx) { 
        //stack.pop();    // ;
    }
    
    /**
     * labeledStatement
     * 	:	identifier ':' statement
     * 	;
     */
    @Override public void exitLabeledStatement(@NotNull Java8Parser.LabeledStatementContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // :
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        identifier.addChild(stmt);
        stack.push(identifier);
    }

    
    /**
     * labeledStatementNoShortIf
     * 	:	identifier ':' statementNoShortIf
     * 	;
     */
    @Override public void exitLabeledStatementNoShortIf(@NotNull Java8Parser.LabeledStatementNoShortIfContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // :
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        identifier.addChild(stmt);
        stack.push(identifier);
    }
    
    /**
     * expressionStatement
     * 	:	statementExpression ';'
     * 	;
     */
    @Override public void exitExpressionStatement(@NotNull Java8Parser.ExpressionStatementContext ctx) {
        stack.pop();    // ;
    }
    
    /**
     * statementExpression
     * 	:	assignment
     * 	|	preIncrementExpression
     * 	|	preDecrementExpression
     * 	|	postIncrementExpression
     * 	|	postDecrementExpression
     * 	|	methodInvocation
     * 	|	classInstanceCreationExpression
     */
    @Override public void exitStatementExpression(@NotNull Java8Parser.StatementExpressionContext ctx) {
        // nothing to do 
    }
    
    /**
     * ifThenStatement
     * 	:	'if' '(' expression ')' statement
     * 	;
     */
    @Override public void exitIfThenStatement(@NotNull Java8Parser.IfThenStatementContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // if
        TigerNode node = new TigerNode("if");
        setLocations(node, ctx);
        node.addChildren(expression, stmt);
        stack.push(node);
    }
    
    /**
     * ifThenElseStatement
     * 	:	'if' '(' expression ')' statementNoShortIf 'else' statement
     * 	;
     */
    @Override public void exitIfThenElseStatement(@NotNull Java8Parser.IfThenElseStatementContext ctx) { 
        TigerNode stmt2 = stack.pop();
        stack.pop();    // else
        TigerNode stmt1 = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // if
        TigerNode node = new TigerNode("if");
        setLocations(node, ctx);
        node.addChildren(expression, stmt1, stmt2);
        stack.push(node);
    }
    
    /**
     * ifThenElseStatementNoShortIf
     * 	:	'if' '(' expression ')' statementNoShortIf 'else' statementNoShortIf
     * 	;
     */
    @Override public void exitIfThenElseStatementNoShortIf(@NotNull Java8Parser.IfThenElseStatementNoShortIfContext ctx) { 
        TigerNode stmt2 = stack.pop();
        stack.pop();    // else
        TigerNode stmt1 = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // if
        TigerNode node = new TigerNode("if");
        setLocations(node, ctx);
        node.addChildren(expression, stmt1, stmt2);
        stack.push(node);
    }

    /**
     * assertStatement
     * 	:	'assert' expression ';'
     * 	|	'assert' expression ':' expression ';'
     * 	;
     */
    @Override public void exitAssertStatement(@NotNull Java8Parser.AssertStatementContext ctx) {
        stack.pop();    // ;
        TigerNode expression1 = stack.pop();
        TigerNode expression2 = null;
        if (ctx.expression().size() > 1) {
            stack.pop();    // :
            expression2 = stack.pop();
        }
        stack.pop();    // assert
        TigerNode node = new TigerNode("assert");
        setLocations(node, ctx);
        node.addChildren(expression2, expression1);
        stack.push(node);
    }
    
    /**
     * switchStatement
     * 	:	'switch' '(' expression ')' switchBlock
     * 	;
     */
    @Override public void exitSwitchStatement(@NotNull Java8Parser.SwitchStatementContext ctx) { 
        TigerNode block = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // switch
        TigerNode node = new TigerNode("switch");
        setLocations(node, ctx);
        node.addChild(expression);
        node.addChildren(block.getChildren());
        stack.push(node);
    }
    
    /**
     * switchBlock
     * 	:	'{' switchBlockStatementGroup* switchLabel* '}'
     * 	;
     */
    @Override public void exitSwitchBlock(@NotNull Java8Parser.SwitchBlockContext ctx) { 
        TigerNode block = new BlockNode();
        setLocations(block, ctx);

        stack.pop();    // }
        
        if (ctx.switchLabel() != null) {
            for (int i = 0; i < ctx.switchLabel().size(); i++) {
                block.addChild(stack.pop());
            }
        }
        
        if (ctx.switchBlockStatementGroup() != null) {
            for (int i = 0; i < ctx.switchBlockStatementGroup().size(); i++) {
                TigerNode labels = stack.pop();
                block.addChildren(labels.getChildren());
            }
        }
        
        stack.pop();    // {
        stack.push(block);
    }
    
    /**
     * switchBlockStatementGroup
     * 	:	switchLabels blockStatements
     * 	;
     */
    @Override public void exitSwitchBlockStatementGroup(@NotNull Java8Parser.SwitchBlockStatementGroupContext ctx) { 
        TigerNode block = stack.pop();
        TigerNode labels = stack.pop();
        setLocations(labels, ctx);
        labels.addChildren(block.getChildren());
        stack.push(labels);
    }
    
    /**
     * switchLabels
     * 	:	switchLabel switchLabel*
     * 	;
     */
    @Override public void exitSwitchLabels(@NotNull Java8Parser.SwitchLabelsContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.switchLabel().size(); i++) {
            list.addChild(stack.pop());   
        }
        stack.push(list);
    }
    
    /**
     * switchLabel
     * 	:	'case' constantExpression ':'
     * 	|	'case' enumConstantName ':'
     * 	|	'default' ':'
     * 	;
     */
    @Override public void exitSwitchLabel(@NotNull Java8Parser.SwitchLabelContext ctx) { 
        stack.pop();    // :
        if (ctx.constantExpression() != null || ctx.enumConstantName() != null) {
            TigerNode expression = stack.pop();
            setLocations(expression, ctx);
            stack.pop();    // case
            TigerNode case_ = new TigerNode("case");
            case_.addChild(expression);
            stack.push(case_);
        }
        else {
            stack.pop();    // default
            TigerNode default_ = new TigerNode("default");
            setLocations(default_, ctx);
            stack.push(default_);
        }
    }
    
    /**
     * enumConstantName
     * 	:	identifier
     * 	;
     */
    @Override public void exitEnumConstantName(@NotNull Java8Parser.EnumConstantNameContext ctx) {
        // nothing to do here
    }
    
    /**
     * whileStatement
     * 	:	'while' '(' expression ')' statement
     * 	;
     */
    @Override public void exitWhileStatement(@NotNull Java8Parser.WhileStatementContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // while
        TigerNode while_ = new TigerNode("while");
        setLocations(while_, ctx);
        while_.addChildren(expression, stmt);
        stack.push(while_);
    }
    
    /**
     * whileStatementNoShortIf
     * 	:	'while' '(' expression ')' statementNoShortIf
     * 	;
     */
    @Override public void exitWhileStatementNoShortIf(@NotNull Java8Parser.WhileStatementNoShortIfContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // while
        TigerNode while_ = new TigerNode("while");
        setLocations(while_, ctx);
        while_.addChildren(expression, stmt);
        stack.push(while_);
    }
    
    /**
     * doStatement
     * 	:	'do' statement 'while' '(' expression ')' ';'
     * 	;
     */
    @Override public void exitDoStatement(@NotNull Java8Parser.DoStatementContext ctx) { 
        stack.pop();    // ;
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // while
        TigerNode statement = stack.pop();
        stack.pop();    // do
        TigerNode do_ = new TigerNode("do");
        setLocations(do_, ctx);
        do_.addChildren(statement, expression);
        stack.push(do_);
    }
    
    /**
     * forStatement
     * 	:	basicForStatement
     * 	|	enhancedForStatement
     * 	;
     */
    @Override public void exitForStatement(@NotNull Java8Parser.ForStatementContext ctx) { 
        // nothing to do here
    }
    
    /**
     * forStatementNoShortIf
     * 	:	basicForStatementNoShortIf
     * 	|	enhancedForStatementNoShortIf
     * 	;
     */
    @Override public void exitForStatementNoShortIf(@NotNull Java8Parser.ForStatementNoShortIfContext ctx) { 
        // nothing to do here
    }
    
    /**
     * basicForStatement
     * 	:	'for' '(' forInit? ';' expression? ';' forUpdate? ')' statement
     * 	;
     */
    @Override public void exitBasicForStatement(@NotNull Java8Parser.BasicForStatementContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // )
        TigerNode forUpdate = null;
        if (ctx.forUpdate() != null) {
            forUpdate = stack.pop();
        }
        stack.pop();    // ;
        TigerNode expression = null;
        if (ctx.expression() != null) {
            expression = stack.pop();   
        }
        stack.pop();    // ;
        TigerNode forInit = null;
        if (ctx.forInit() != null) {
            forInit = stack.pop();   
        }
        stack.pop();    // (
        stack.pop();    // for
        TigerNode for_ = new TigerNode("for");
        setLocations(for_, ctx);
        for_.addChildren(forInit, expression, forUpdate, stmt);
        stack.push(for_);
    }
    
    /**
     * basicForStatementNoShortIf
     * 	:	'for' '(' forInit? ';' expression? ';' forUpdate? ')' statementNoShortIf
     * 	;
     */
    @Override public void exitBasicForStatementNoShortIf(@NotNull Java8Parser.BasicForStatementNoShortIfContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // )
        TigerNode forUpdate = null;
        if (ctx.forUpdate() != null) {
            forUpdate = stack.pop();
        }
        stack.pop();    // ;
        TigerNode expression = null;
        if (ctx.expression() != null) {
            expression = stack.pop();   
        }
        stack.pop();    // ;
        TigerNode forInit = null;
        if (ctx.forInit() != null) {
            forInit = stack.pop();   
        }
        stack.pop();    // (
        stack.pop();    // for
        TigerNode for_ = new TigerNode("for");
        setLocations(for_, ctx);
        for_.addChildren(forInit, expression, forUpdate, stmt);
        stack.push(for_);
    }
    
    /**
     * forInit
     * 	:	statementExpressionList
     * 	|	localVariableDeclaration
     * 	;
     */
    @Override public void exitForInit(@NotNull Java8Parser.ForInitContext ctx) { 
        // nothing to do
    }
    
    /**
     * forUpdate
     * 	:	statementExpressionList
     * 	;
     */
    @Override public void exitForUpdate(@NotNull Java8Parser.ForUpdateContext ctx) {
        // nothing to do
    }
    
    /**
     * statementExpressionList
     * 	:	statementExpression (',' statementExpression)*
     * 	;
     */
    @Override public void exitStatementExpressionList(@NotNull Java8Parser.StatementExpressionListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.statementExpression().size(); i++) {
            list.addChild(stack.pop()); 
            if (i < ctx.statementExpression().size() - 1) {
                stack.pop();    // ,   
            }
        }
        stack.push(list);
    }
    
    /**
     * enhancedForStatement
     * 	:	'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statement
     * 	;
     */
    @Override public void exitEnhancedForStatement(@NotNull Java8Parser.EnhancedForStatementContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // ;
        TigerNode id = stack.pop();
        TigerNode unannType = stack.pop();
        id.setType((Type)unannType);
        if (ctx.variableModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.variableModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    unannType.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            unannType.setModifiers(m.getModifiers());
        }
        stack.pop();    // (
        stack.pop();    // for
        TigerNode for_ = new TigerNode("for");
        setLocations(for_, ctx);
        for_.addChildren(id, expression, stmt);
        stack.push(for_);
    }
    
    /**
     * enhancedForStatementNoShortIf
     * 	:	'for' '(' variableModifier* unannType variableDeclaratorId ':' expression ')' statementNoShortIf
     * 	;
     */
    @Override public void exitEnhancedForStatementNoShortIf(@NotNull Java8Parser.EnhancedForStatementNoShortIfContext ctx) { 
        TigerNode stmt = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // ;
        TigerNode id = stack.pop();
        TigerNode unannType = stack.pop();
        id.setType((Type)unannType);
        if (ctx.variableModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.variableModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    unannType.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            unannType.setModifiers(m.getModifiers());
        }
        stack.pop();    // (
        stack.pop();    // for
        TigerNode for_ = new TigerNode("for");
        setLocations(for_, ctx);
        for_.addChildren(id, expression, stmt);
        stack.push(for_);
    }
    
    /**
     * breakStatement
     * 	:	'break' identifier? ';'
     * 	;
     */
    @Override public void exitBreakStatement(@NotNull Java8Parser.BreakStatementContext ctx) {
        stack.pop();    // ;
        TigerNode identifier = ctx.identifier() == null ? null : stack.pop();
        stack.pop();    // break
        // TODO: a break node or just use the identifier?
        TigerNode break_ = new TigerNode("break");
        setLocations(break_, ctx);
        break_.addChild(identifier);
        stack.push(break_);
    }
    
    /**
     * continueStatement
     * 	:	'continue' identifier? ';'
     * 	;
     */
    @Override public void exitContinueStatement(@NotNull Java8Parser.ContinueStatementContext ctx) { 
        stack.pop();    // ;
        TigerNode identifier = ctx.identifier() == null ? null : stack.pop();
        stack.pop();    // continue
        // TODO: a continue node or just use the identifier
        TigerNode continue_ = new TigerNode("continue");
        setLocations(continue_, ctx);
        continue_.addChild(identifier);
        stack.push(continue_);
    }
    
    /**
     * returnStatement
     * 	:	'return' expression? ';'
     * 	;
     */
    @Override public void exitReturnStatement(@NotNull Java8Parser.ReturnStatementContext ctx) { 
        
        stack.pop();    // ;
        TigerNode expression = ctx.expression() == null ? null : stack.pop();
        stack.pop();    // return
        TigerNode rtn = new TigerNode("return");
        setLocations(rtn, ctx);
        rtn.addChild(expression);
        stack.push(rtn);
        
    }
    
    /**
     * throwStatement
     * 	:	'throw' expression ';'
     * 	;
     */
    @Override public void exitThrowStatement(@NotNull Java8Parser.ThrowStatementContext ctx) { 
        stack.pop();    // ;
        TigerNode expression = stack.pop();
        stack.pop();    // throw
        TigerNode throw_ = new TigerNode("throw");
        setLocations(throw_, ctx);
        throw_.addChild(expression);
        stack.push(throw_);
    }
    
    /**
     * synchronizedStatement
     * 	:	'synchronized' '(' expression ')' block
     * 	;
     */
    @Override public void exitSynchronizedStatement(@NotNull Java8Parser.SynchronizedStatementContext ctx) {
        TigerNode block = stack.pop();
        stack.pop();    // )
        TigerNode expression = stack.pop();
        stack.pop();    // (
        stack.pop();    // synchronized
        TigerNode sync = new TigerNode("synchronized");
        setLocations(sync, ctx);
        sync.addChildren(expression, block);
        stack.push(sync);
    }
    
    /**
     * tryStatement
     * 	:	'try' block catches
     * 	|	'try' block catches? finally_
     * 	|	tryWithResourcesStatement
     * 	;
     */
    @Override public void exitTryStatement(@NotNull Java8Parser.TryStatementContext ctx) { 
        // 3rd choice
        if (ctx.tryWithResourcesStatement() != null) {
            // nothing to do
            return;   
        }
        
        // 2nd choice
        if (ctx.finally_() != null) {
            TigerNode finally_ = stack.pop();
            TigerNode catches = null;
            if (ctx.catches() != null) {
                catches = stack.pop();   
            }
            TigerNode block = stack.pop();
            stack.pop();    // try
            TigerNode try_ = new TigerNode("try");
            setLocations(try_, ctx);
            try_.addChildren(block, catches, finally_);
            stack.push(try_);
            return;
        }
        
        // 1st choice
        TigerNode catches = stack.pop();
        TigerNode block = stack.pop();
        stack.pop();    // try
        TigerNode try_ = new TigerNode("try");
        setLocations(try_, ctx);
        try_.addChildren(block, catches);
        stack.push(try_);
    }
    
    /**
     * catches
     * 	:	catchClause catchClause*
     * 	;
     */
    @Override public void exitCatches(@NotNull Java8Parser.CatchesContext ctx) {
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.catchClause().size(); i++) {
            list.addChild(stack.pop());   
        }
        stack.push(list);
    }
    
    /**
     * catchClause
     * 	:	'catch' '(' catchFormalParameter ')' block
     * 	;
     */
    @Override public void exitCatchClause(@NotNull Java8Parser.CatchClauseContext ctx) { 
        TigerNode block = stack.pop();
        stack.pop();    // )
        TigerNode catchFormalParameter = stack.pop();
        stack.pop();    // (
        stack.pop();    // catch
        TigerNode catch_ = new TigerNode("catch");
        setLocations(catch_, ctx);
        catch_.addChild(catchFormalParameter);
        catch_.addChild(block);
        stack.push(catch_);
    }
    
    /**
     * catchFormalParameter
     * 	:	variableModifier* catchType variableDeclaratorId
     * 	;
     */
    @Override public void exitCatchFormalParameter(@NotNull Java8Parser.CatchFormalParameterContext ctx) { 
        TigerNode variableDeclaratorId = stack.pop();
        TigerNode catchType = stack.pop();
        setLocations(catchType, ctx);
        if (ctx.variableModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.variableModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    catchType.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            catchType.setModifiers(m.getModifiers());
        }
        catchType.addChild(variableDeclaratorId);
        stack.push(catchType);
    }
    
    /**
     * catchType
     * 	:	unannClassType ('|' classType)*
     * 	;
     */
    @Override public void exitCatchType(@NotNull Java8Parser.CatchTypeContext ctx) { 
        TigerNode classType = null;
        if (ctx.classType() != null) {
            classType = new TigerNode();
            for (int i = 0; i < ctx.classType().size(); i++) {
                classType.addChild(stack.pop());     // classType
                stack.pop();    // |   
            }
        }
        TigerNode unannClassType = stack.pop();
        setLocations(unannClassType, ctx);
        unannClassType.addChildren(classType.getChildren());
        stack.push(unannClassType);
    }
    
    /**
     * finally_
     * 	:	'finally' block
     * 	;
     */
    @Override public void exitFinally_(@NotNull Java8Parser.Finally_Context ctx) {
        TigerNode finally_ = new TigerNode("finally");
        setLocations(finally_, ctx);
        finally_.addChild(stack.pop());     // block
        stack.pop();    // finally
        stack.push(finally_);
    }
    
    /**
     * tryWithResourcesStatement
     * 	:	'try' resourceSpecification block catches? finally_?
     * 	;
     */
    @Override public void exitTryWithResourcesStatement(@NotNull Java8Parser.TryWithResourcesStatementContext ctx) { 
        TigerNode finally_ = ctx.finally_() == null ? null : stack.pop();
        TigerNode catches = ctx.catches() == null ? null : stack.pop();
        TigerNode block = stack.pop();
        TigerNode resourceSpec = stack.pop();
        stack.pop();    // try
        TigerNode try_ = new TigerNode("try");
        setLocations(try_, ctx);
        try_.addChild(resourceSpec);
        try_.addChild(block);
        try_.addChild(catches);
        try_.addChild(finally_);
        stack.push(try_);
    }
    
    /**
     * resourceSpecification
     * 	:	'(' resourceList SEMI? ')'
     * 	;
     */
    @Override public void exitResourceSpecification(@NotNull Java8Parser.ResourceSpecificationContext ctx) { 
        stack.pop();    // )
        if (ctx.SEMI() != null) {
            stack.pop();    // SEMI   
        }
        TigerNode list = stack.pop();
        setLocations(list, ctx);
        stack.pop();    // (
        stack.push(list);
    }
    
    /**
     * resourceList
     * 	:	resource (';' resource)*
     * 	;
     */
    @Override public void exitResourceList(@NotNull Java8Parser.ResourceListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.resource().size(); i++) {
            list.addChild(stack.pop());
            if (i < ctx.resource().size() - 1) {
                stack.pop();    // ;   
            }
        }
        stack.push(list);
    }
    
    /**
     * resource
     * 	:	variableModifier* unannType variableDeclaratorId '=' expression
     * 	|   variableAccess    // danson, java 9
     * 	;
     */
    @Override public void exitResource(@NotNull Java8Parser.ResourceContext ctx) { 
        if (ctx.variableAccess() != null) {
            return;   
        }
        TigerNode expression = stack.pop();
        stack.pop();    // =
        TigerNode variable = stack.pop();
        TigerNode unannType = stack.pop();
        setLocations(unannType, ctx);
        unannType.addChild(variable);
        unannType.addChild(expression);
        if (ctx.variableModifier() != null) {
            ModifierSet m = new ModifierSet();
            for (int i = 0; i < ctx.variableModifier().size(); i++) {
                TigerNode node = stack.pop();   // annotation or modifier
                if (node instanceof AnnotationNode) {
                    unannType.addAnnotation((AnnotationNode)node);   
                    m.addModifier("annotation");
                }
                else {
                    m.addModifier(node.getName());   
                }
            }
            unannType.setModifiers(m.getModifiers());
        }
    }
    
    /**
     * variableAccess
     *     :   expressionName
     *     |   fieldAccess
     *     ;
     */
    @Override public void exitVariableAccess(@NotNull Java8Parser.VariableAccessContext ctx) { 
        // nothing to do
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
    @Override public void exitPrimary(@NotNull Java8Parser.PrimaryContext ctx) { 
        TigerNode endList = null;
        if (ctx.primaryNoNewArray_lf_primary() != null) {
            endList = new TigerNode();
            for (int i = 0; i < ctx.primaryNoNewArray_lf_primary().size(); i++) {
                TigerNode ugly = stack.pop();
                endList.addChild(ugly);
            }
        }
        TigerNode name = stack.pop();
        setLocations(name, ctx);
        name.addChildren(endList.getChildren());
        stack.push(name);
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
    @Override public void exitPrimaryNoNewArray(@NotNull Java8Parser.PrimaryNoNewArrayContext ctx) { 
        if (ctx.literal() != null) {
            // nothing to do, literal should be on the top of the stack
            return;
        }
        if (ctx.expression() != null) {
            stack.pop();    // )
            TigerNode expression = stack.pop();
            stack.pop();    // (
            setLocations(expression, ctx);
            stack.push(expression);
            return;
        }
        if (ctx.classInstanceCreationExpression() != null || ctx.fieldAccess() != null || ctx.arrayAccess() != null || ctx.methodInvocation() != null || ctx.methodReference() != null) {
            // nothing to do
            return;
        }
        if (ctx.typeName() != null) {
            stack.pop();    // class or this
            stack.pop();    // .
            if (ctx.squareBrackets() != null) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    stack.pop();    // []          
                }
            }
            // nothing else to do, typeName should be on the top of the stack now
            return;
        }
        TigerNode t = stack.peek();
        if (t.getName().equals("this")) {
            stack.pop();    // this  
        }
        else {
            stack.pop();   // class
            stack.pop();   // .
            stack.pop();   // void
        }
    }
    
    /**
     * primaryNoNewArray_lf_arrayAccess
     * 	:
     * 	;
     */
    @Override public void exitPrimaryNoNewArray_lf_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) {
        // nothing to do
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
    @Override public void exitPrimaryNoNewArray_lfno_arrayAccess(@NotNull Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) { 
        if (ctx.literal() != null) {
            // nothing to do, literal should be on the top of the stack
            return;
        }
        if (ctx.expression() != null) {
            stack.pop();    // )
            TigerNode expression = stack.pop();
            stack.pop();    // (
            setLocations(expression, ctx);
            stack.push(expression);
            return;
        }
        if (ctx.classInstanceCreationExpression() != null || ctx.fieldAccess() != null || ctx.methodInvocation() != null || ctx.methodReference() != null) {
            // nothing to do
            return;
        }
        if (ctx.typeName() != null) {
            stack.pop();    // class or this
            stack.pop();    // .
            if (ctx.squareBrackets() != null) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    stack.pop();    // []          
                }
            }
            // nothing else to do, typeName should be on the top of the stack now
            return;
        }
        TigerNode t = stack.peek();
        if (t.getName().equals("this")) {
            stack.pop();    // this  
        }
        else {
            stack.pop();   // class
            stack.pop();   // .
            stack.pop();   // void
        }
    }
    
    /**
     * primaryNoNewArray_lf_primary
     * 	:	classInstanceCreationExpression_lf_primary
     * 	|	fieldAccess_lf_primary
     * 	|	arrayAccess_lf_primary
     * 	|	methodInvocation_lf_primary
     * 	|	methodReference_lf_primary
     * 	;
     */
    @Override public void exitPrimaryNoNewArray_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) { 
        // nothing to do
    }
    
    /**
     * primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary
     * 	:
     * 	;
     */
    @Override public void exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) { 
        // nothing to do
    }
    
    /**
     * primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary
     * 	:	classInstanceCreationExpression_lf_primary
     * 	|	fieldAccess_lf_primary
     * 	|	methodInvocation_lf_primary
     * 	|	methodReference_lf_primary
     * 	;
     */
    @Override public void exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(@NotNull Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {
        // nothing to do
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
    @Override public void exitPrimaryNoNewArray_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) { 
        if (ctx.literal() != null || 
            "this".equals(ctx.getText()) || 
        ctx.classInstanceCreationExpression_lfno_primary() != null || 
        ctx.fieldAccess_lfno_primary() != null || 
        ctx.arrayAccess_lfno_primary() != null || 
        ctx.methodInvocation_lfno_primary() != null || 
        ctx.methodReference_lfno_primary() != null) {
            // nothing to do, one of these should already be on the stack
            return;
        }
        if (ctx.expression() != null) {
            stack.pop();    // )
            TigerNode expression = stack.pop();
            stack.pop();    // (
            setLocations(expression, ctx);
            stack.push(expression);
            return;
        }
        if (ctx.unannPrimitiveType() != null) {
            stack.pop();    // class
            stack.pop();    // .
            if (ctx.squareBrackets() != null) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    stack.pop();    // []          
                }
            }
            // nothing else to do, unannPrimitiveType should be on the top of the stack now
            return;
        }
        if (ctx.typeName() != null) {
            stack.pop();    // class or this
            stack.pop();    // .
            if (ctx.squareBrackets() != null) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    stack.pop();    // []          
                }
            }
            // nothing else to do, typeName should be on the top of the stack now
            return;
        }
        stack.pop();   // class
        stack.pop();   // .
        stack.pop();   // void
    }
    
    /**
     * primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary
     * 	:
     * 	;
     */
    @Override public void exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) { 
        // nothing to do
    }
    
    /**
     * primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary
     * 	:	-literal
     * 	|	-typeName (squareBrackets)* '.' 'class'
     * 	|	-unannPrimitiveType (squareBrackets)* '.' 'class'
     * 	|	'void' '.' 'class'
     * 	|	'this'
     * 	|	-typeName '.' 'this'
     * 	|	-'(' expression ')'
     * 	|	-classInstanceCreationExpression_lfno_primary
     * 	|	-fieldAccess_lfno_primary
     * 	|	-methodInvocation_lfno_primary
     * 	|	-methodReference_lfno_primary
     * 	;
     */
    @Override public void exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(@NotNull Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) { 
        if (ctx.literal() != null) {
            // nothing to do, literal should be on the top of the stack
            return;
        }
        if (ctx.expression() != null) {
            stack.pop();    // )
            TigerNode expression = stack.pop();
            stack.pop();    // (
            setLocations(expression, ctx);
            stack.push(expression);
            return;
        }
        if (ctx.classInstanceCreationExpression_lfno_primary() != null || ctx.fieldAccess_lfno_primary() != null || ctx.methodInvocation_lfno_primary() != null || ctx.methodReference_lfno_primary() != null) {
            // nothing to do
            return;
        }
        if (ctx.unannPrimitiveType() != null) {
            stack.pop();    // class
            stack.pop();    // .
            if (ctx.squareBrackets() != null) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    stack.pop();    // []          
                }
            }
            // nothing else to do, unannPrimitiveType should be on the top of the stack now
            return;
        }
        if (ctx.typeName() != null) {
            stack.pop();    // class or this
            stack.pop();    // .
            if (ctx.squareBrackets() != null) {
                for (int i = 0; i < ctx.squareBrackets().size(); i++) {
                    stack.pop();    // []          
                }
            }
            // nothing else to do, typeName should be on the top of the stack now
            return;
        }
        TigerNode t = stack.peek();
        if (t.getName().equals("this")) {
            stack.pop();    // this  
        }
        else {
            stack.pop();   // class
            stack.pop();   // .
            stack.pop();   // void
        }
    }
    
    /**
     * classInstanceCreationExpression
     * 	:	                   'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	expressionName '.' 'new' typeArguments? annotationIdentifier                             typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	primary        '.' 'new' typeArguments? annotationIdentifier                             typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override public void exitClassInstanceCreationExpression(@NotNull Java8Parser.ClassInstanceCreationExpressionContext ctx) { 
        TigerNode classBody = ctx.classBody() == null ? null : stack.pop();
        stack.pop();    // )
        TigerNode argumentList = ctx.argumentList() == null ? null : stack.pop();
        stack.pop();    // (
        TigerNode diamond = ctx.typeArgumentsOrDiamond() == null ? null : stack.pop();
        TigerNode annotationIdentifiers = new TigerNode();
        for (int i = 0; i < ctx.annotationIdentifier().size(); i++) {
            annotationIdentifiers.addChild(stack.pop());
            if (i < ctx.annotationIdentifier().size() - 1) {
                stack.pop();    // .   
            }
        }
        TigerNode typeArguments = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode new_ = stack.pop();
        TigerNode name;
        if (ctx.expressionName() != null || ctx.primary() != null) {
            stack.pop();    // .
            name = stack.pop();     // expressionName or primary
            name.addChild(new_);
        }
        else {
            name = new_;   
        }
        setLocations(name, ctx);
        name.addChild(typeArguments);
        name.addChildren(annotationIdentifiers.getChildren());
        name.addChild(diamond);
        name.addChild(argumentList);
        name.addChild(classBody);
        stack.push(name);
    }
    
    /**
     * classInstanceCreationExpression_lf_primary
     * 	:	'.' 'new' typeArguments? annotationIdentifier typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override public void exitClassInstanceCreationExpression_lf_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) { 
        TigerNode classBody = ctx.classBody() == null ? null : stack.pop();
        stack.pop();    // )
        TigerNode argumentList = ctx.argumentList() == null ? null : stack.pop();
        stack.pop();    // (
        TigerNode diamond = ctx.typeArgumentsOrDiamond() == null ? null : stack.pop();
        TigerNode annotationIdentifier = stack.pop();
        TigerNode typeArguments = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode name = stack.pop();   // new
        stack.pop();    // .
        setLocations(name, ctx);
        name.addChild(typeArguments);
        name.addChild(annotationIdentifier);
        name.addChild(diamond);
        name.addChild(argumentList);
        name.addChild(classBody);
        stack.push(name);
    }
    
    /**
     * classInstanceCreationExpression_lfno_primary
     * 	:	                   'new' typeArguments? annotationIdentifier ('.' annotationIdentifier)* typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	|	expressionName '.' 'new' typeArguments? annotationIdentifier                             typeArgumentsOrDiamond? '(' argumentList? ')' classBody?
     * 	;
     */
    @Override public void exitClassInstanceCreationExpression_lfno_primary(@NotNull Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        TigerNode classBody = ctx.classBody() == null ? null : stack.pop();
        stack.pop();    // )
        TigerNode argumentList = ctx.argumentList() == null ? null : stack.pop();
        stack.pop();    // (
        TigerNode diamond = ctx.typeArgumentsOrDiamond() == null ? null : stack.pop();
        TigerNode annotationIdentifiers = new TigerNode();
        for (int i = 0; i < ctx.annotationIdentifier().size(); i++) {
            annotationIdentifiers.addChild(stack.pop());
            if (i < ctx.annotationIdentifier().size() - 1) {
                stack.pop();    // .   
            }
        }
        TigerNode typeArguments = ctx.typeArguments() == null ? null : stack.pop();
        TigerNode new_ = stack.pop();
        TigerNode name;
        if (ctx.expressionName() != null) {
            stack.pop();    // .
            name = stack.pop();
            name.addChild(new_);
        }
        else {
            name = new_;   
        }
        setLocations(name, ctx);
        name.addChild(typeArguments);
        name.addChildren(annotationIdentifiers.getChildren());
        name.addChild(diamond);
        name.addChild(argumentList);
        name.addChild(classBody);
        stack.push(name);
    }
    
    /**
     * typeArgumentsOrDiamond
     * 	:	typeArguments
     * 	|	'<>'
     * 	;
     */
    @Override public void exitTypeArgumentsOrDiamond(@NotNull Java8Parser.TypeArgumentsOrDiamondContext ctx) { 
        // nothing to do
    }
    
    /**
     * fieldAccess
     * 	:	           primary '.' identifier
     * 	|	             SUPER '.' identifier
     * 	|	typeName '.' SUPER '.' identifier
     * 	;
     */
    @Override public void exitFieldAccess(@NotNull Java8Parser.FieldAccessContext ctx) { 
        TigerNode identifier = stack.pop();
        stack.pop();    // .
        TigerNode primary = stack.pop();    // primary or SUPER
        if (ctx.typeName() != null) {
            stack.pop();    // .
            TigerNode typeName = stack.pop();
            setLocations(typeName, ctx);
            typeName.addChild(primary);
            typeName.addChild(identifier);
            stack.push(typeName);
        }
        else {
            setLocations(primary, ctx);
            primary.addChild(identifier);
            stack.push(primary);
        }
    }
    
    /**
     * fieldAccess_lf_primary
     * 	:	'.' identifier
     * 	;
     */
    @Override public void exitFieldAccess_lf_primary(@NotNull Java8Parser.FieldAccess_lf_primaryContext ctx) { 
        TigerNode identifier = stack.pop();
        stack.pop();    // .
        setLocations(identifier, ctx);
        stack.push(identifier);
    }
    
    /**
     * fieldAccess_lfno_primary
     * 	:	SUPER '.' identifier
     * 	|	typeName '.' SUPER '.' identifier
     * 	;
     */
    @Override public void exitFieldAccess_lfno_primary(@NotNull Java8Parser.FieldAccess_lfno_primaryContext ctx) { 
        TigerNode identifier = stack.pop();
        stack.pop();    // .
        TigerNode super_ = stack.pop();
        if (ctx.typeName() != null) {
            stack.pop();    // .
            TigerNode typeName = stack.pop();
            setLocations(typeName, ctx);
            typeName.addChild(super_);
            typeName.addChild(identifier);
            stack.push(typeName);
        }
        else {
            setLocations(super_, ctx);
            super_.addChild(identifier);
            stack.push(super_);
        }
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
    @Override public void exitArrayAccess(@NotNull Java8Parser.ArrayAccessContext ctx) { 
        TigerNode endList = null;
        if (ctx.primaryNoNewArray_lf_arrayAccess() != null) {
            endList = new TigerNode();
            for (int i = 0; i < ctx.primaryNoNewArray_lf_arrayAccess().size(); i++) {
                stack.pop();    // ]
                TigerNode expression = stack.pop();
                stack.pop();    // [
                TigerNode ugly = stack.pop();
                ugly.addChild(expression);
                endList.addChild(ugly);
            }
        }
        stack.pop();    // ]
        TigerNode expression = stack.pop();
        stack.pop();    // [
        TigerNode name = stack.pop();
        setLocations(name, ctx);
        name.addChild(expression);
        name.addChildren(endList.getChildren());
        stack.push(name);
    }
    
    /**
     * arrayAccess_lf_primary
     * 	:	(	primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary '[' expression ']'
     * 		)*
     * 	;
     */
    @Override public void exitArrayAccess_lf_primary(@NotNull Java8Parser.ArrayAccess_lf_primaryContext ctx) { 
        TigerNode endList = null;
        if (ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary() != null) {
            endList = new TigerNode();
            for (int i = 0; i < ctx.primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary().size(); i++) {
                stack.pop();    // ]
                TigerNode expression = stack.pop();
                stack.pop();    // [
                TigerNode ugly = stack.pop();
                ugly.addChild(expression);
                endList.addChild(ugly);
            }
        }
        stack.pop();    // ]
        TigerNode expression = stack.pop();
        stack.pop();    // [
        TigerNode name = stack.pop();
        setLocations(name, ctx);
        name.addChild(expression);
        name.addChildren(endList.getChildren());
        stack.push(name);
    }
    
    /**
     * arrayAccess_lfno_primary
     * 	:	(	expressionName                                               '[' expression ']'
     * 		|	primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary '[' expression ']'
     * 		)
     * 		(	primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary '[' expression ']'
     * 		)*
     * 	;
     * These are ugly names!
     */
    @Override public void exitArrayAccess_lfno_primary(@NotNull Java8Parser.ArrayAccess_lfno_primaryContext ctx) {
        TigerNode endList = null;
        if (ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary() != null) {
            endList = new TigerNode();
            for (int i = 0; i < ctx.primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary().size(); i++) {
                stack.pop();    // ]
                TigerNode expression = stack.pop();
                stack.pop();    // [
                TigerNode ugly = stack.pop();
                ugly.addChild(expression);
                endList.addChild(ugly);
            }
        }
        stack.pop();    // ]
        TigerNode expression = stack.pop();
        stack.pop();    // [
        TigerNode name = stack.pop();
        setLocations(name, ctx);
        name.addChild(expression);
        name.addChildren(endList.getChildren());
        stack.push(name);
    }
    
    /**
     * methodInvocation
     * 	:	methodName                                       '(' argumentList? ')'
     * 	|	typeName           '.' typeArguments? identifier '(' argumentList? ')'
     * 	|	expressionName     '.' typeArguments? identifier '(' argumentList? ')'
     * 	|	primary            '.' typeArguments? identifier '(' argumentList? ')'
     * 	|	SUPER              '.' typeArguments? identifier '(' argumentList? ')'
     * 	|	typeName '.' SUPER '.' typeArguments? identifier '(' argumentList? ')'  
     * 	;
     */
    @Override public void exitMethodInvocation(@NotNull Java8Parser.MethodInvocationContext ctx) { 
        stack.pop();    // )
        TigerNode argumentList = ctx.argumentList() == null ? null : stack.pop();
        stack.pop();    // (
        if (ctx.methodName() != null) {
            // first choice
            TigerNode name = stack.pop();
            setLocations(name, ctx);
            name.addChild(argumentList);
            stack.push(name);
            return;
        }
        
        TigerNode identifier = stack.pop();
        TigerNode typeArguments = ctx.typeArguments() == null ? null : stack.pop();
        stack.pop();    // .
        
        if (ctx.SUPER() != null && ctx.typeName() != null) {
            // last choice 
            TigerNode super_ = stack.pop();
            if (ctx.typeName() != null) {
                stack.pop();    // .
                TigerNode name = stack.pop();
                setLocations(name, ctx);
                name.addChild(super_);
                name.addChild(typeArguments);
                name.addChild(identifier);
                name.addChild(argumentList);
                stack.push(name);
            }
            else {
                setLocations(super_, ctx);
                super_.addChild(typeArguments);
                super_.addChild(identifier);
                super_.addChild(argumentList);
                stack.push(super_);
            }
        }
        else {
            // all other choices
            TigerNode name = stack.pop();
            setLocations(name, ctx);
            name.addChild(typeArguments);
            name.addChild(identifier);
            name.addChild(argumentList);
            stack.push(name);
        }
    }
    
    /**
     * methodInvocation_lf_primary
     * 	:	'.' typeArguments? identifier '(' argumentList? ')'
     * 	;
     */
    @Override public void exitMethodInvocation_lf_primary(@NotNull Java8Parser.MethodInvocation_lf_primaryContext ctx) { 
        stack.pop();    // )
        TigerNode argumentList = ctx.argumentList() == null ? null : stack.pop();
        stack.pop();    // (
        TigerNode identifier = stack.pop();
        TigerNode typeArguments = ctx.typeArguments() == null ? null : stack.pop();
        stack.pop();    // .
        TigerNode primary = new TigerNode();
        setLocations(primary, ctx);
        primary.addChild(typeArguments);
        primary.addChild(identifier);
        primary.addChild(argumentList);
        stack.push(primary);
    }
    
    /**
     * methodInvocation_lfno_primary
     * 	:	methodName                                       '(' argumentList? ')'
     * 	|	typeName           '.' typeArguments? identifier '(' argumentList? ')'
     * 	|	expressionName     '.' typeArguments? identifier '(' argumentList? ')'
     * 	|	SUPER              '.' typeArguments? identifier '(' argumentList? ')'
     * 	|	typeName '.' SUPER '.' typeArguments? identifier '(' argumentList? ')'
     * 	;
     */
    @Override public void exitMethodInvocation_lfno_primary(@NotNull Java8Parser.MethodInvocation_lfno_primaryContext ctx) { 
        stack.pop();    // )
        TigerNode argumentList = ctx.argumentList() == null ? null : stack.pop();
        stack.pop();    // (
        if (ctx.methodName() != null) {
            // first choice
            TigerNode methodName = stack.pop();
            setLocations(methodName, ctx);
            methodName.addChild(argumentList);
            stack.push(methodName);
            return;
        }   
        
        // next 4 choices
        TigerNode identifier = stack.pop();
        TigerNode typeArguments = ctx.typeArguments() == null ? null : stack.pop();
        stack.pop();    // .
        
        if (ctx.SUPER() != null) {
            // 4th and 5th choices
            TigerNode super_ = stack.pop();
            if (ctx.typeName() != null) {
                stack.pop();    // .
                TigerNode typeName = stack.pop();
                setLocations(typeName, ctx);
                typeName.addChild(identifier);
                typeName.addChild(typeArguments);
                typeName.addChild(super_);
                typeName.addChild(argumentList);
                stack.push(typeName);
            }
            else {
                setLocations(super_, ctx);
                super_.addChild(argumentList);
                stack.push(super_);
            }
        }
        else {
            // 2nd and 3rd choices
            TigerNode name = stack.pop();   // typeName or expressionName
            setLocations(name, ctx);
            name.addChild(identifier);
            name.addChild(typeArguments);
            name.addChild(argumentList);
            stack.push(name);
        }
    }
    
    /**
     * argumentList
     * 	:	expression (',' expression)*
     * 	;
     */
    @Override public void exitArgumentList(@NotNull Java8Parser.ArgumentListContext ctx) { 
        TigerNode list = new TigerNode();
        setLocations(list, ctx);
        for (int i = 0; i < ctx.expression().size(); i++) {
            list.addChild(stack.pop());
            if (i < ctx.expression().size() - 1) {
                stack.pop();    // ,   
            }
        }
        stack.push(list);
    }
    
    /**
     * methodReference
     * 	:	expressionName '::' typeArguments? identifier
     * 	|	referenceType '::' typeArguments? identifier
     * 	|	primary '::' typeArguments? identifier
     * 	|	SUPER '::' typeArguments? identifier
     * 	|	typeName '.' SUPER '::' typeArguments? identifier
     * 	|	classType '::' typeArguments? 'new'
     * 	|	arrayType '::' 'new'                               
     * 	;
     */
    @Override public void exitMethodReference(@NotNull Java8Parser.MethodReferenceContext ctx) {
        if (ctx.expressionName() != null || ctx.referenceType() != null || ctx.primary() != null) {
            TigerNode identifier = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode expressionName = stack.pop(); // or referenceType
            setLocations(expressionName, ctx);
            if (typeArguments != null) {
                expressionName.addChild(typeArguments);   
            }
            expressionName.addChild(identifier);
            stack.push(expressionName);
        }
        else if (ctx.typeName() != null) {
            TigerNode identifier = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode sup = stack.pop();    // SUPER
            TigerNode typeName = stack.pop(); 
            setLocations(typeName, ctx);
            if (typeArguments != null) {
                typeName.addChild(typeArguments);   
            }
            typeName.addChild(identifier);
            typeName.addChild(sup);
            stack.push(typeName);
        }
        else if (ctx.classType() != null) {
            TigerNode new_ = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode classType = stack.pop(); 
            setLocations(classType, ctx);
            if (typeArguments != null) {
                classType.addChild(typeArguments);   
            }
            classType.addChild(new_);
            stack.push(classType);
        }
        else if (ctx.arrayType() != null) {
            TigerNode new_ = stack.pop();
            stack.pop();    // ::
            TigerNode arrayType = stack.pop(); 
            setLocations(arrayType, ctx);
            arrayType.addChild(new_);
            stack.push(arrayType);
        }
        else {
            // SUPER choice   
            TigerNode identifier = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode sup = stack.pop();    // SUPER
            setLocations(sup, ctx);
            if (typeArguments != null) {
                sup.addChild(typeArguments);   
            }
            sup.addChild(identifier);
            stack.push(sup);
        }
    }
    
    /**
     * methodReference_lf_primary
     * 	:	'::' typeArguments? identifier
     * 	;
     */
    @Override public void exitMethodReference_lf_primary(@NotNull Java8Parser.MethodReference_lf_primaryContext ctx) {
        TigerNode identifier = stack.pop();
        setLocations(identifier, ctx);
        if (ctx.typeArguments() != null) {
            identifier.addChild(stack.pop());   
        }
        stack.pop();    // ::
        stack.push(identifier);
    }
    
    /**
     * methodReference_lfno_primary
     * 	:	expressionName '::' typeArguments? identifier
     * 	|	referenceType '::' typeArguments? identifier
     * 	|	SUPER '::' typeArguments? identifier
     * 	|	typeName '.' SUPER '::' typeArguments? identifier
     * 	|	classType '::' typeArguments? 'new'
     * 	|	arrayType '::' 'new'
     * 	;
     */
    @Override public void exitMethodReference_lfno_primary(@NotNull Java8Parser.MethodReference_lfno_primaryContext ctx) { 
        if (ctx.expressionName() != null || ctx.referenceType() != null) {
            TigerNode identifier = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode expressionName = stack.pop(); // or referenceType
            setLocations(expressionName, ctx);
            if (typeArguments != null) {
                expressionName.addChild(typeArguments);   
            }
            expressionName.addChild(identifier);
            stack.push(expressionName);
        }
        else if (ctx.typeName() != null) {
            TigerNode identifier = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode sup = stack.pop();    // SUPER
            TigerNode typeName = stack.pop(); 
            setLocations(typeName, ctx);
            if (typeArguments != null) {
                typeName.addChild(typeArguments);   
            }
            typeName.addChild(identifier);
            typeName.addChild(sup);
            stack.push(typeName);
        }
        else if (ctx.classType() != null) {
            TigerNode new_ = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode classType = stack.pop(); 
            setLocations(classType, ctx);
            if (typeArguments != null) {
                classType.addChild(typeArguments);   
            }
            classType.addChild(new_);
            stack.push(classType);
        }
        else if (ctx.arrayType() != null) {
            TigerNode new_ = stack.pop();
            stack.pop();    // ::
            TigerNode arrayType = stack.pop(); 
            setLocations(arrayType, ctx);
            arrayType.addChild(new_);
            stack.push(arrayType);
        }
        else {
            // SUPER choice   
            TigerNode identifier = stack.pop();
            TigerNode typeArguments = null;
            if (ctx.typeArguments() != null) {
                typeArguments = stack.pop();   
            }
            stack.pop();    // ::
            TigerNode sup = stack.pop();    // SUPER
            setLocations(sup, ctx);
            if (typeArguments != null) {
                sup.addChild(typeArguments);   
            }
            sup.addChild(identifier);
            stack.push(sup);
        }
    }
    
    /**
     * arrayCreationExpression
     * 	:	'new' primitiveType dimExprs dims?
     * 	|	'new' classOrInterfaceType dimExprs dims?
     * 	|	'new' primitiveType dims arrayInitializer
     * 	|	'new' classOrInterfaceType dims arrayInitializer
     * 	;
     */
    @Override public void exitArrayCreationExpression(@NotNull Java8Parser.ArrayCreationExpressionContext ctx) { 
        if (ctx.arrayInitializer() != null) {
            // last 2 choices
            TigerNode arrayInitializer = stack.pop();
            TigerNode dims = stack.pop();
            TigerNode type = stack.pop();
            setLocations(type, ctx);
            stack.pop();    // new
            type.addChild(dims);
            type.addChild(arrayInitializer);
            stack.push(type);
        }
        else {
            // first 2 choices
            TigerNode dims = null;
            if (ctx.dims() != null) {
                dims = stack.pop();
            }
            TigerNode dimExprs = stack.pop();
            TigerNode type = stack.pop();
            setLocations(type, ctx);
            stack.pop();    // new
            type.addChild(dimExprs);
            if (dims != null) {
                type.addChild(dims);   
            }
            stack.push(type);
        }
    }
    
    /**
     * dimExprs
     * 	:	dimExpr dimExpr*
     * 	;
     */
    @Override public void exitDimExprs(@NotNull Java8Parser.DimExprsContext ctx) { 
        TigerNode exprs = new TigerNode();
        setLocations(exprs, ctx);
        for (int i = 0; i < ctx.dimExpr().size(); i++) {
            exprs.addChild(stack.pop());   
        }
        stack.push(exprs);
    }
    
    /**
     * dimExpr
     * 	:	annotation* '[' expression ']'
     * 	;
     */
    @Override public void exitDimExpr(@NotNull Java8Parser.DimExprContext ctx) { 
        stack.pop();    // ]
        TigerNode expression = stack.pop();
        setLocations(expression, ctx);
        stack.pop();    // [
        if (ctx.annotation() != null) {
            for (int i = 0; i < ctx.annotation().size(); i++) {
                expression.addAnnotation((AnnotationNode)stack.pop());   
            }
        }
        stack.push(expression);
    }
    
    /**
     * constantExpression
     * 	:	expression
     * 	;
     */
    @Override public void exitConstantExpression(@NotNull Java8Parser.ConstantExpressionContext ctx) { 
        // nothing to do
    }
    
    /**
     * expression
     * 	:	lambdaExpression
     * 	|	assignmentExpression
     * 	;
     */
    @Override public void exitExpression(@NotNull Java8Parser.ExpressionContext ctx) { 
        // nothing to do
    }
    
    /**
     * lambdaExpression
     * 	:	lambdaParameters '->' lambdaBody
     * 	;
     */
    @Override public void exitLambdaExpression(@NotNull Java8Parser.LambdaExpressionContext ctx) { 
        TigerNode body = stack.pop();
        stack.pop();    // ->
        TigerNode parameters = stack.pop();
        TigerNode expression = new TigerNode();
        setLocations(expression, ctx);
        expression.addChild(parameters);
        expression.addChild(body);
        stack.push(expression);
    }
    
    /**
     * lambdaParameters
     * 	:	identifier
     * 	|	'(' formalParameterList? ')'
     * 	|	'(' inferredFormalParameterList ')'
     * 	;
     */
    @Override public void exitLambdaParameters(@NotNull Java8Parser.LambdaParametersContext ctx) { 
        if (ctx.identifier() == null) {
            stack.pop();    // )
            TigerNode list = stack.pop();
            setLocations(list, ctx);
            stack.pop();    // (
            stack.push(list);
        }
    }
    
    /**
     * inferredFormalParameterList
     * 	:	identifier (',' identifier)*
     * 	;
     */
    @Override public void exitInferredFormalParameterList(@NotNull Java8Parser.InferredFormalParameterListContext ctx) { 
        if (ctx.identifier() != null) {
            TigerNode list = new TigerNode();
            setLocations(list, ctx);
            for (int i = 0; i < ctx.identifier().size(); i++) {
                list.addChild(stack.pop());
            }
            stack.push(list);
        }
    }
    
    /**
     * lambdaBody
     * 	:	expression
     * 	|	block
     * 	;
     */
    @Override public void exitLambdaBody(@NotNull Java8Parser.LambdaBodyContext ctx) { 
        // nothing to do
    }
    
    /**
     * assignmentExpression
     * 	:	conditionalExpression
     * 	|	assignment
     * 	;
     */
    @Override public void exitAssignmentExpression(@NotNull Java8Parser.AssignmentExpressionContext ctx) {
        // nothing to do
    }
    
    /**
     * assignment
     * 	:	leftHandSide assignmentOperator expression
     * 	;
     */
    @Override public void exitAssignment(@NotNull Java8Parser.AssignmentContext ctx) { 
        TigerNode expression = stack.pop();
        stack.pop();    // assignmentOperator
        TigerNode leftHandSide = stack.pop();
        setLocations(leftHandSide, ctx);
        leftHandSide.addChild(expression);
        stack.push(leftHandSide);
    }
    
    /**
     * leftHandSide
     * 	:	variableAccess    // this includes expressionName and fieldAccess
     * 	|	arrayAccess
     * 	;
     */
    @Override public void exitLeftHandSide(@NotNull Java8Parser.LeftHandSideContext ctx) { 
        // nothing to do
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
    @Override public void exitAssignmentOperator(@NotNull Java8Parser.AssignmentOperatorContext ctx) {
        // nothing to do
    }
    
    /**
     * additiveOperator
     *     :   '+'
     *     |   '-'
     *     ;
     */
    @Override public void exitAdditiveOperator(@NotNull Java8Parser.AdditiveOperatorContext ctx) {
        // nothing to do
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
    @Override public void exitRelationalOperator(@NotNull Java8Parser.RelationalOperatorContext ctx) { 
        // nothing to do
    }
    
    /**
     * multiplicativeOperator
     *     :   '*'
     *     |   '/'
     *     |   '%'
     *     ;
     */
    @Override public void exitMultiplicativeOperator(@NotNull Java8Parser.MultiplicativeOperatorContext ctx) { 
        // nothing to do
    }
    
    /**
     * squareBrackets
     *     :   '[]'
     *     ;
     */
    @Override public void exitSquareBrackets(@NotNull Java8Parser.SquareBracketsContext ctx) { 
        // nothing to do
    }
    
    /**
     * conditionalExpression
     * 	:	conditionalOrExpression
     * 	|	conditionalOrExpression '?' expression ':' conditionalExpression
     * 	|   conditionalOrExpression '?' expression ':' lambdaExpression
     * 	;
     */
    @Override public void exitConditionalExpression(@NotNull Java8Parser.ConditionalExpressionContext ctx) { 
        TigerNode conditionalExpression = stack.pop();  // conditionalExpression or lambdaExpression
        if (ctx.expression() != null) {
            stack.pop();    // :
            TigerNode expression = stack.pop();
            stack.pop();    // ?
            TigerNode conditionalOrExpression = stack.pop();
            setLocations(conditionalOrExpression, ctx);
            conditionalOrExpression.addChild(expression);
            conditionalOrExpression.addChild(conditionalExpression);
            stack.push(conditionalOrExpression);
        }
        else {
            setLocations(conditionalExpression, ctx);
            stack.push(conditionalExpression);
        }
    }
    
    /**
     * conditionalOrExpression
     * 	:	conditionalAndExpression
     * 	|	conditionalOrExpression '||' conditionalAndExpression
     * 	;
     */
    @Override public void exitConditionalOrExpression(@NotNull Java8Parser.ConditionalOrExpressionContext ctx) { 
        TigerNode conditionalAndExpression = stack.pop();
        if (ctx.conditionalOrExpression() != null) {
            stack.pop();    // ||
            TigerNode conditionalOrExpression = stack.pop();
            setLocations(conditionalOrExpression, ctx);
            conditionalOrExpression.addChild(conditionalAndExpression);
            stack.push(conditionalOrExpression);
        }
        else {
            setLocations(conditionalAndExpression, ctx);
            stack.push(conditionalAndExpression);
        }
    }
    
    /**
     * conditionalAndExpression
     * 	:	inclusiveOrExpression
     * 	|	conditionalAndExpression '&&' inclusiveOrExpression
     * 	;
     */
    @Override public void exitConditionalAndExpression(@NotNull Java8Parser.ConditionalAndExpressionContext ctx) { 
        TigerNode inclusiveOrExpression = stack.pop();
        if (ctx.conditionalAndExpression() != null) {
            stack.pop();    // &&
            TigerNode conditionalAndExpression = stack.pop();
            setLocations(conditionalAndExpression, ctx);
            conditionalAndExpression.addChild(inclusiveOrExpression);
            stack.push(conditionalAndExpression);
        }
        else {
            setLocations(inclusiveOrExpression, ctx);
            stack.push(inclusiveOrExpression);
        }
    }
    
    /**
     * inclusiveOrExpression
     * 	:	exclusiveOrExpression
     * 	|	inclusiveOrExpression '|' exclusiveOrExpression
     * 	;
     */
    @Override public void exitInclusiveOrExpression(@NotNull Java8Parser.InclusiveOrExpressionContext ctx) { 
        TigerNode exclusiveOrExpression = stack.pop();
        if (ctx.inclusiveOrExpression() != null) {
            stack.pop();    // |
            TigerNode inclusiveOrExpression = stack.pop();
            setLocations(inclusiveOrExpression, ctx);
            inclusiveOrExpression.addChild(exclusiveOrExpression);
            stack.push(inclusiveOrExpression);
        }
        else {
            setLocations(exclusiveOrExpression, ctx);
            stack.push(exclusiveOrExpression);
        }
    }
    
    /**
     * exclusiveOrExpression
     * 	:	andExpression
     * 	|	exclusiveOrExpression '^' andExpression
     * 	;
     */
    @Override public void exitExclusiveOrExpression(@NotNull Java8Parser.ExclusiveOrExpressionContext ctx) { 
        TigerNode andExpression = stack.pop();
        if (ctx.exclusiveOrExpression() != null) {
            stack.pop();    // ^
            TigerNode exclusiveOrExpression = stack.pop();
            setLocations(exclusiveOrExpression, ctx);
            exclusiveOrExpression.addChild(andExpression);
            stack.push(exclusiveOrExpression);
        }
        else {
            setLocations(andExpression, ctx);
            stack.push(andExpression);
        }
    }
    
    /**
     * andExpression
     * 	:	equalityExpression
     * 	|	andExpression '&' equalityExpression
     * 	;
     */
    @Override public void exitAndExpression(@NotNull Java8Parser.AndExpressionContext ctx) { 
        TigerNode equalityExpression = stack.pop();
        if (ctx.andExpression() != null) {
            stack.pop();    // &
            TigerNode andExpression = stack.pop();
            setLocations(andExpression, ctx);
            andExpression.addChild(equalityExpression);
            stack.push(andExpression);
        }
        else {
            setLocations(equalityExpression, ctx);
            stack.push(equalityExpression);
        }
    }
    
    /**
     * equalityExpression
     * 	:	relationalExpression
     * 	|	equalityExpression '==' relationalExpression
     * 	|	equalityExpression '!=' relationalExpression
     * 	;
     */
    @Override public void exitEqualityExpression(@NotNull Java8Parser.EqualityExpressionContext ctx) {
        TigerNode relationalExpression = stack.pop();
        if (ctx.equalityExpression() != null) {
            stack.pop();    // == or !=
            TigerNode equalityExpression = stack.pop();
            setLocations(equalityExpression, ctx);
            equalityExpression.addChild(relationalExpression);
            stack.push(equalityExpression);
        }
        else {
            setLocations(relationalExpression, ctx);
            stack.push(relationalExpression);
        }
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
    @Override public void exitRelationalExpression(@NotNull Java8Parser.RelationalExpressionContext ctx) { 
        TigerNode shiftExpression = stack.pop();    // shiftExpression or referenceType
        if (ctx.relationalOperator() != null) {
            stack.pop();    // operator
            TigerNode expression = stack.pop();
            setLocations(expression, ctx);
            expression.addChild(shiftExpression);
            stack.push(expression);
        }
        else {
            setLocations(shiftExpression, ctx);
            stack.push(shiftExpression);
        }
    }
    
    /**
     * shiftExpression
     * 	:	additiveExpression
     * 	|	shiftExpression shiftOperator additiveExpression
     * 	|	shiftExpression shiftOperator additiveExpression
     * 	|	shiftExpression shiftOperator additiveExpression
     * 	;
     */
    @Override public void exitShiftExpression(@NotNull Java8Parser.ShiftExpressionContext ctx) { 
        TigerNode additiveExpression = stack.pop();
        if (ctx.shiftOperator() != null) {
            stack.pop();    // operator
            TigerNode expression = stack.pop();
            setLocations(expression, ctx);
            expression.addChild(additiveExpression);
            stack.push(expression);
        }
        else {
            setLocations(additiveExpression, ctx);
            stack.push(additiveExpression);
        }
    }
    
    /**
     * shiftOperator
     *     :   '<' '<'
     *     |   '>' '>'  
     *     |   '>' '>' '>'
     *     ;
     */
    @Override public void exitShiftOperator(@NotNull Java8Parser.ShiftOperatorContext ctx) { 
        // nothing to do
    }
    
    /**
     * additiveExpression
     * 	:	multiplicativeExpression
     * 	|	additiveExpression additiveOperator multiplicativeExpression
     * 	|	additiveExpression additiveOperator multiplicativeExpression
     * 	;
     */
    @Override public void exitAdditiveExpression(@NotNull Java8Parser.AdditiveExpressionContext ctx) { 
        TigerNode multiplicativeExpression = stack.pop();
        if (ctx.additiveOperator() != null) {
            stack.pop();    // operator
            TigerNode expression = stack.pop();
            setLocations(expression, ctx);
            expression.addChild(multiplicativeExpression);
            stack.push(expression);
        }
        else {
            setLocations(multiplicativeExpression, ctx);
            stack.push(multiplicativeExpression);
        }
    }
    
    /**
     * multiplicativeExpression
     * 	:	unaryExpression
     * 	|	multiplicativeExpression multiplicativeOperator unaryExpression
     * 	|	multiplicativeExpression multiplicativeOperator unaryExpression
     * 	|	multiplicativeExpression multiplicativeOperator unaryExpression
     * 	;
     */
    @Override public void exitMultiplicativeExpression(@NotNull Java8Parser.MultiplicativeExpressionContext ctx) { 
        TigerNode unaryExpression = stack.pop();
        if (ctx.multiplicativeOperator() != null) {
            stack.pop();    // operator
            TigerNode expression = stack.pop();
            setLocations(expression, ctx);
            expression.addChild(unaryExpression);
            stack.push(expression);
        }
        else {
            setLocations(unaryExpression, ctx);
            stack.push(unaryExpression);
        }
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
    @Override public void exitUnaryExpression(@NotNull Java8Parser.UnaryExpressionContext ctx) { 
        TigerNode unaryExpression = null;
        if (ctx.unaryExpression() != null) {
            unaryExpression = stack.pop();   
        }
        TigerNode expression = stack.pop();
        setLocations(expression, ctx);
        if (unaryExpression != null) {
            expression.addChild(unaryExpression);   
        }
        stack.push(expression);
    }
    
    /**
     * preIncrementExpression
     * 	:	'++' unaryExpression
     * 	;
     */
    @Override public void exitPreIncrementExpression(@NotNull Java8Parser.PreIncrementExpressionContext ctx) { 
        TigerNode unaryExpression = stack.pop();
        setLocations(unaryExpression, ctx);
        stack.pop();    // ++
        stack.push(unaryExpression);
    }
    
    /**
     * preDecrementExpression
     * 	:	'--' unaryExpression
     * 	;
     */
    @Override public void exitPreDecrementExpression(@NotNull Java8Parser.PreDecrementExpressionContext ctx) {
        TigerNode unaryExpression = stack.pop();
        setLocations(unaryExpression, ctx);
        stack.pop();    // --
        stack.push(unaryExpression);
    }
    
    /**
     * unaryExpressionNotPlusMinus
     * 	:	postfixExpression
     * 	|	'~' unaryExpression
     * 	|	'!' unaryExpression
     * 	|	castExpression
     * 	;
     */
    @Override public void exitUnaryExpressionNotPlusMinus(@NotNull Java8Parser.UnaryExpressionNotPlusMinusContext ctx) { 
        TigerNode unaryExpressionNotPlusMinus = stack.pop();
        setLocations(unaryExpressionNotPlusMinus, ctx);
        if (ctx.unaryExpression() != null) {
            stack.pop();    // ~ or !   
        }
        stack.push(unaryExpressionNotPlusMinus);
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
    @Override public void exitPostfixExpression(@NotNull Java8Parser.PostfixExpressionContext ctx) {
        TigerNode temp = null;
        if (ctx.postIncrementExpression_lf_postfixExpression() != null) {
            for (int i = 0; i < ctx.postIncrementExpression_lf_postfixExpression().size(); i++) {
                temp.addChild(stack.pop());   
            }
        }
        if (ctx.postDecrementExpression_lf_postfixExpression() != null) {
            for (int i = 0; i < ctx.postDecrementExpression_lf_postfixExpression().size(); i++) {
                temp.addChild(stack.pop());   
            }
        }
        TigerNode postfixExpression = stack.pop();  // primary or expressionName
        setLocations(postfixExpression, ctx);
        if (temp != null) {
            postfixExpression.addChildren(temp.getChildren());   
        }
        stack.push(postfixExpression);
    }
    
    /**
     * postIncrementExpression
     * 	:	postfixExpression '++'
     * 	;
     */
    @Override public void exitPostIncrementExpression(@NotNull Java8Parser.PostIncrementExpressionContext ctx) { 
        stack.pop();    // ++
        TigerNode postfixExpression = stack.pop();
        setLocations(postfixExpression, ctx);
        stack.push(postfixExpression);
    }
    
    /**
     * postIncrementExpression_lf_postfixExpression
     * 	:	'++'
     * 	;
     */
    @Override public void exitPostIncrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) { 
        // nothing to do
    }
    
    /**
     * postDecrementExpression
     * 	:	postfixExpression '--'
     * 	;
     */
    @Override public void exitPostDecrementExpression(@NotNull Java8Parser.PostDecrementExpressionContext ctx) {
        stack.pop();    // --
        TigerNode postfixExpression = stack.pop();
        setLocations(postfixExpression, ctx);
        stack.push(postfixExpression);
    }
    
    /**
     * postDecrementExpression_lf_postfixExpression
     * 	:	'--'
     * 	;
     */
    @Override public void exitPostDecrementExpression_lf_postfixExpression(@NotNull Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) { 
        // nothing to do?
    }
    
    /**
     * castExpression
     * 	:	'(' primitiveType ')' unaryExpression
     * 	|	'(' referenceType additionalBound* ')' unaryExpressionNotPlusMinus
     * 	|	'(' referenceType additionalBound* ')' lambdaExpression                
     * 	;                                                                         
     */
    @Override public void exitCastExpression(@NotNull Java8Parser.CastExpressionContext ctx) { 
        TigerNode expression = stack.pop();   // unaryExpression, unaryExpressionNotPlusMinus, or lambdaExpression
        setLocations(expression, ctx);
        stack.pop();    // )
        if (ctx.additionalBound() != null) {
            for (int i = 0; i < ctx.additionalBound().size(); i++) {
                expression.addChild(stack.pop()); // additionalBound   
            }
        }
        TigerNode cast = stack.pop(); // primitiveType or referenceType
        expression.addChild(cast);
        stack.pop();    // (
        stack.push(expression);
    }

    @Override public void exitEveryRule(@NotNull ParserRuleContext ctx) { }

    @Override public void visitTerminal(@NotNull TerminalNode node) { 
        TigerNode tn = new TigerNode(node.getText());
        stack.push(tn);
    }

    @Override public void visitErrorNode(@NotNull ErrorNode node) { }
}