package sidekick.json.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.util.Location;
import sidekick.util.SideKickPosition;

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
    ImageIcon arrayIcon = EclipseIconsPlugin.getIcon("scope_obj.gif");
    

    public JSONNode getRoot() {
        return root;
    }

    @Override public void enterJson( @NotNull JSONParser.JsonContext ctx ) {
        root = new JSONNode("root");
        root.setStartLocation( getStartLocation( ctx ) );
        root.setEndLocation( getEndLocation( ctx ) );
        stack.push( root );
    }
    
    @Override public void exitJson( @NotNull JSONParser.JsonContext ctx ) {
        stack.pop();    // stack should be empty at this point
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
        JSONNode node = new JSONNode("object");
        node.setIsObject(true);
        node.setIcon(objectIcon);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.LBRACE().getSymbol().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.RBRACE().getSymbol().getStopIndex()));
        stack.push( node );
    }
    
    @Override public void exitObject( @NotNull JSONParser.ObjectContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        parent.addChild(node);
    }
    
    @Override public void enterArray( @NotNull JSONParser.ArrayContext ctx ) {
        JSONNode node = new JSONNode("array");
        node.setIsArray(true);
        node.setIcon(arrayIcon);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.LSQUARE().getSymbol().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.RSQUARE().getSymbol().getStopIndex()));
        stack.push( node );
    } 

    @Override public void exitArray( @NotNull JSONParser.ArrayContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        parent.addChild(node);
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
        JSONNode value = node.getFirstChild();  
        node.setEndPosition(value.getEndPosition());
        node.removeChildren();
        if (value != null ) {
            if (value.isNumberOrString()) {
                node.setName(node.getName() + ": " + value.getName());  
                node.setIcon(pairIcon);
            } else if (value.isObject()) {
                //node.addChild(value);
                node.setIcon(objectIcon);   
                if (value.hasChildren()) {
                    for (JSONNode newKid : value.getChildren()) {
                        node.addChild(newKid);   
                    }
                }
            } else if (value.isArray()) {
                //node.addChild(value);
                node.setIcon(arrayIcon);   
                if (value.hasChildren()) {
                    for (JSONNode newKid : value.getChildren()) {
                        node.addChild(newKid);   
                    }
                }
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
            node.setStartLocation( getStartLocation( ctx ) );
            node.setEndLocation( getEndLocation( ctx ) );
            stack.push( node );
        } else if (ctx.STRING() != null) {
            TerminalNode value = ctx.STRING();
            node.setName(value.getText());
            node.setIsNumberOrString(true);
            node.setStartPosition(new SideKickPosition(value.getSymbol().getStartIndex()));
            node.setEndPosition(new SideKickPosition(value.getSymbol().getStopIndex()));
            node.setStartLocation( getStartLocation( ctx ) );
            node.setEndLocation( getEndLocation( ctx ) );
            stack.push( node );
        } 
    }
    
    @Override public void exitValue( @NotNull JSONParser.ValueContext ctx ) {
        JSONNode node = stack.peek();
        if (node.isNumberOrString()) {
            node = stack.pop();
            JSONNode parent = stack.peek();
            parent.addChild(node);
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