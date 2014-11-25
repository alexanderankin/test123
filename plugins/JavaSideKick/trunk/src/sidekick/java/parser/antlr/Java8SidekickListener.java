package sidekick.java.parser.antlr;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.java.node.*;

import sidekick.util.Location;

public class Java8SidekickListener extends Java8BaseListener {
    
    // accumulates relevant nodes to describe a java file
    private CUNode cuNode;
    
   // accumulates counts of classes, interfaces, methods and fields.
   private Results results = new Results();

    
    public CUNode getCompilationUnit() {
        return cuNode;   
    }
    
    @Override 
    public void enterCompilationUnit( @NotNull Java8Parser.CompilationUnitContext ctx ) {
        cuNode = new CUNode();
        cuNode.setStartLocation(getStartLocation(ctx));
        cuNode.setEndLocation(getEndLocation(ctx));
    }
    
	@Override public void enterPackageDeclaration(@NotNull Java8Parser.PackageDeclarationContext ctx) { 
	    List<TerminalNode> identifiers = ctx.Identifier();
	    StringBuilder sb = new StringBuilder();
	    for (TerminalNode tn : identifiers) {
	        String text = tn.getText();
	        if ("package".equals(text)) {
	             continue;   
	        }
	        sb.append(text).append('.');   
	    }
	    if (sb.length() > 0) {
	        sb.deleteCharAt(sb.length() - 1);
	    }
	    cuNode.setPackageName(sb.toString());
	}

	// e.g. import java.util.List;
    @Override public void enterSingleTypeImportDeclaration(@NotNull Java8Parser.SingleTypeImportDeclarationContext ctx) {
        ImportNode in = new ImportNode(ctx.typeName().packageOrTypeName().getText() + '.' + ctx.typeName().Identifier());
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        cuNode.addImport(in);
    }
    
    // e.g. import java.util.*;
	@Override public void enterTypeImportOnDemandDeclaration(@NotNull Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
        ImportNode in = new ImportNode(ctx.packageOrTypeName().getText() + ".*");
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        cuNode.addImport(in);
    }
    
    // e.g. import static java.lang.Math.PI;
	@Override public void enterSingleStaticImportDeclaration(@NotNull Java8Parser.SingleStaticImportDeclarationContext ctx) {
        ImportNode in = new ImportNode(ctx.typeName().packageOrTypeName().getText() + '.' + ctx.typeName().Identifier());
        in.setModifiers(ModifierSet.STATIC);
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        cuNode.addImport(in);
    }
    
    // e.g. import static java.lang.Math.*;
	@Override public void enterStaticImportOnDemandDeclaration(@NotNull Java8Parser.StaticImportOnDemandDeclarationContext ctx) {
        ImportNode in = new ImportNode(ctx.typeName().getText() + ".*");
        in.setModifiers(ModifierSet.STATIC);
        in.setStartLocation(getStartLocation(ctx));
        in.setEndLocation(getEndLocation(ctx));
        cuNode.addImport(in);
    }

    
    @Override 
    public void enterConstructorDeclaration( @NotNull Java8Parser.ConstructorDeclarationContext ctx ) {
        List<Java8Parser.ConstructorModifierContext> modifiers = ctx.constructorModifier();
        int modifier = 0;
        for (Java8Parser.ConstructorModifierContext mod : modifiers) {
            String text = mod.getText();
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
        }
        
        ConstructorNode node = new ConstructorNode();
        node.setName(ctx.constructorDeclarator().simpleTypeName().getText());
        node.setModifiers(modifier);
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        cuNode.addChild(node);
    }
    
    @Override 
    public void enterMethodDeclaration( @NotNull Java8Parser.MethodDeclarationContext ctx ) {
        List<Java8Parser.MethodModifierContext> modifiers = ctx.methodModifier();
        int modifier = 0;
        for (Java8Parser.MethodModifierContext mod : modifiers) {
            String text = mod.getText();
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
        }
        
        MethodNode node = new MethodNode();
        node.setName(ctx.methodHeader().methodDeclarator().Identifier().getText());
        node.setModifiers(modifier);
        node.setReturnType(new Type(ctx.methodHeader().result().getText()));
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        cuNode.addChild(node);
        results.incMethodCount();
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