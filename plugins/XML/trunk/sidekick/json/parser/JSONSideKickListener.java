package sidekick.json.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.util.Location;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.ImageIcon;
import javax.swing.text.Position;

import eclipseicons.EclipseIconsPlugin;

// Locations and Positions for all assets are set here
public class JSONSideKickListener extends JSONBaseListener {

    Deque<JSONNode> stack = new ArrayDeque<JSONNode>();
    JSONNode root = null;
    
    ImageIcon pairIcon = EclipseIconsPlugin.getIcon("field_public_obj.gif");
    ImageIcon objectIcon = EclipseIconsPlugin.getIcon("javaassist_co.gif");
    ImageIcon arrayIcon = EclipseIconsPlugin.getIcon("packd_obj.gif");
    

    public JSONNode getRoot() {
        return root;
    }

    @Override public void enterJson( @NotNull JSONParser.JsonContext ctx ) {
        root = new JSONNode();
        root.setStartLocation( getStartLocation( ctx ) );
        root.setEndLocation( getEndLocation( ctx ) );
        stack.push( root );
    }
    
    @Override public void exitJson( @NotNull JSONParser.JsonContext ctx ) {
        stack.pop();    // stack is empty at this point
        JSONNode child = root.getFirstChild();
        Position start;
        Position end;
        if (child != null) {
            start = child.getStartPosition();
            end = child.getEndPosition();
        } else {
            start = new SideKickPosition(0);
            end = start;
        }
        root.setStartPosition(start);
        root.setEndPosition(end);
    }

    @Override public void enterObject( @NotNull JSONParser.ObjectContext ctx ) {
        JSONNode node = new JSONNode();
        node.setIsObject(true);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.LBRACE().getSymbol().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.RBRACE().getSymbol().getStopIndex()));
        stack.push( node );
    }
    
    @Override public void exitObject( @NotNull JSONParser.ObjectContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        if (node.hasChildren()) {
            for (JSONNode newKid : node.getChildren()) {
                parent.addChild(newKid);   
            }
        }
    }
    
    @Override public void enterArray( @NotNull JSONParser.ArrayContext ctx ) {
        JSONNode node = new JSONNode();
        node.setIsArray(true);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.LSQUARE().getSymbol().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.RSQUARE().getSymbol().getStopIndex()));
        stack.push( node );
    } 

    @Override public void exitArray( @NotNull JSONParser.ArrayContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        if (node.hasChildren()) {
            for (JSONNode newKid : node.getChildren()) {
                parent.addChild(newKid);   
            }
        }
    }

    @Override public void enterPair( @NotNull JSONParser.PairContext ctx ) {
        TerminalNode string = ctx.STRING();
        JSONNode node = new JSONNode( string.getText() );
        node.setIsPair(true);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(string.getSymbol().getStartIndex()));
        stack.push( node );
    }
    
    @Override public void exitPair( @NotNull JSONParser.PairContext ctx ) {
        JSONNode node = stack.pop();            
        JSONNode child = node.getFirstChild();  
        node.setEndPosition(child.getEndPosition());
        node.removeChildren();
        if (child != null ) {
            if (child.isNumberOrString()) {
                node.setName(node.getName() + ": " + child.getName());  
                node.setIcon(pairIcon);
            } else if (child.isObject()) {
                if (child.hasChildren()) {
                    for (JSONNode newKid : child.getChildren()) {
                        node.addChild(newKid);   
                    }
                }
                node.setIcon(objectIcon);   
            } else if (child.isArray()) {
                if (child.hasChildren()) {
                    for (JSONNode newKid : child.getChildren()) {
                        node.addChild(newKid);   
                    }
                }
                node.setIcon(arrayIcon);   
            }
        }
        JSONNode parent = stack.peek();
        parent.addChild( node );
    }
    
    @Override public void enterValue( @NotNull JSONParser.ValueContext ctx ) {
        JSONNode node = new JSONNode();
        if (ctx.NUMBER() != null) {
            TerminalNode value = ctx.NUMBER();
            node.setName(value.getText());
            node.setIsNumberOrString(true);
            node.setStartPosition(new SideKickPosition(value.getSymbol().getStartIndex()));
            node.setEndPosition(new SideKickPosition(value.getSymbol().getStopIndex()));
        } else if (ctx.STRING() != null) {
            TerminalNode value = ctx.STRING();
            node.setName(value.getText());
            node.setIsNumberOrString(true);
            node.setStartPosition(new SideKickPosition(value.getSymbol().getStartIndex()));
            node.setEndPosition(new SideKickPosition(value.getSymbol().getStopIndex()));
        } else if (ctx.object() != null) {
            JSONParser.ObjectContext value = ctx.object();
            node.setIsObject(true);
            node.setStartPosition(new SideKickPosition(value.LBRACE().getSymbol().getStartIndex()));
            node.setEndPosition(new SideKickPosition(value.RBRACE().getSymbol().getStopIndex()));
        } else if (ctx.array() != null) {
            JSONParser.ArrayContext value = ctx.array();
            node.setIsArray(true);
            node.setStartPosition(new SideKickPosition(value.LSQUARE().getSymbol().getStartIndex()));
            node.setEndPosition(new SideKickPosition(value.RSQUARE().getSymbol().getStopIndex()));
        }
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        stack.push( node );
    }
    
    @Override public void exitValue( @NotNull JSONParser.ValueContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        parent.addChild(node);
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