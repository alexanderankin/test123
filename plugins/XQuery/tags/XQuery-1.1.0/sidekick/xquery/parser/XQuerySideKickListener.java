package sidekick.xquery.parser;

import javax.swing.ImageIcon;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import sidekick.util.Location;
import sidekick.util.SideKickPosition;
import eclipseicons.EclipseIconsPlugin;

public class XQuerySideKickListener extends XQueryParserBaseListener {

    final XQueryNode root;

    final XQueryNode fnRoot;
    final XQueryNode importRoot;
    final XQueryNode moduleRoot;
    final XQueryNode varRoot;

    boolean hasFnRoot;
    boolean hasImportRoot;
    boolean hasModuleRoot;
    boolean hasVarRoot;
    
    final ImageIcon functionIcon = EclipseIconsPlugin.getIcon("searchm_obj.gif");
    final ImageIcon varIcon = EclipseIconsPlugin.getIcon("generic_element.gif");
    final ImageIcon schemaIcon = EclipseIconsPlugin.getIcon("ant_import.png");
    final ImageIcon moduleIcon = EclipseIconsPlugin.getIcon("importpref_obj.gif");

    public XQuerySideKickListener(String fileName) {
    	root = new XQueryNode(fileName);
    	root.setStartLocation(new Location(0,0));
    	root.setEndLocation(new Location(0,0));
    	root.setStartPosition(new SideKickPosition(0));
    	root.setEndPosition(new SideKickPosition(0));

    	importRoot = new XQueryNode("import schema");
    	importRoot.setStartLocation(new Location(0,0));
    	importRoot.setEndLocation(new Location(0,0));
    	importRoot.setStartPosition(new SideKickPosition(0));
    	importRoot.setEndPosition(new SideKickPosition(0));
    	hasImportRoot = false;

    	moduleRoot = new XQueryNode("import module");
    	moduleRoot.setStartLocation(new Location(0,0));
    	moduleRoot.setEndLocation(new Location(0,0));
    	moduleRoot.setStartPosition(new SideKickPosition(0));
    	moduleRoot.setEndPosition(new SideKickPosition(0));
    	hasModuleRoot = false;

    	varRoot = new XQueryNode("variable");
    	varRoot.setStartLocation(new Location(0,0));
    	varRoot.setEndLocation(new Location(0,0));
    	varRoot.setStartPosition(new SideKickPosition(0));
    	varRoot.setEndPosition(new SideKickPosition(0));
    	hasVarRoot = false;
  	
    	fnRoot = new XQueryNode("function");
    	fnRoot.setStartLocation(new Location(0,0));
    	fnRoot.setEndLocation(new Location(0,0));
    	fnRoot.setStartPosition(new SideKickPosition(0));
    	fnRoot.setEndPosition(new SideKickPosition(0));
    	hasFnRoot = false;

    }
	
    public XQueryNode getRoot() {
        return root;
    }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterFunctionDecl(@NotNull XQueryParser.FunctionDeclContext ctx) { 
        XQueryNode node = new XQueryNode("function");
        node.setIsObject(true);
        node.setIcon(functionIcon);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        
        String name = ctx.name.getChild(0).toString();
        node.setName(name);

        node.setStartPosition(new SideKickPosition(ctx.getStart().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.getStop().getStartIndex()));
       
        fnRoot.addChild(node);
        
        if (!hasFnRoot) {
        	root.addChild(fnRoot);
        	hasFnRoot=true;
        }
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterVarDecl(@NotNull XQueryParser.VarDeclContext ctx) { 
        XQueryNode node = new XQueryNode("var");
        node.setIsObject(true);
        node.setIcon(varIcon);

        String name = ctx.name.getChild(0).toString();
        node.setName(name);
        
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.getStart().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.getStop().getStartIndex()));

        varRoot.addChild(node);
 
        if (!hasVarRoot) {
        	root.addChild(varRoot);
        	hasVarRoot=true;
        }

	}
	
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */

	@Override public void enterModuleImport(@NotNull XQueryParser.ModuleImportContext ctx) { 
        XQueryNode node = new XQueryNode();
        node.setIsObject(true);
        node.setIcon(moduleIcon);

        StringBuffer name = new StringBuffer();
        for (int i = 0; i < ctx.getChildCount()-1; i++) {
        	if (ctx.getChild(i).getText().equals("namespace")) {
        		name.append(ctx.getChild(i+1).getText());
        		name.append(" ");
        	}      	
        	if (ctx.getChild(i).getText().equals("at")) {
        		name.append(ctx.getChild(i+1).getText());
        	}      	
        }

        node.setName(name.toString().replaceAll("\"", ""));
        
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.getStart().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.getStop().getStartIndex()));

        moduleRoot.addChild(node);

        if (!hasModuleRoot) {
        	root.addChild(moduleRoot);
        	hasModuleRoot=true;
        }

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterSchemaImport(@NotNull XQueryParser.SchemaImportContext ctx) { 
        XQueryNode node = new XQueryNode();
        node.setIsObject(true);
        node.setIcon(schemaIcon);
        
        StringBuffer name = new StringBuffer();

        for (int i = 0; i < ctx.getChildCount(); i++) {
        	if (ctx.getChild(i) instanceof XQueryParser.StringLiteralContext) {
        		name.append(ctx.getChild(i).getText());
        		name.append(" ");
        	}      	
        }
        
    	name.trimToSize();
        node.setName(name.toString().replaceAll("\"", ""));
        
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.getStart().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.getStop().getStartIndex()));

        importRoot.addChild(node);

        if (!hasImportRoot) {
        	root.addChild(importRoot);
        	hasImportRoot=true;
        }

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

}
