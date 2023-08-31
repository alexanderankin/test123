package sidekick.json.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.util.Location;
import sidekick.util.SideKickPosition;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.ImageIcon;
import javax.swing.text.Position;

import eclipseicons.EclipseIconsPlugin;

// Locations and Positions for all assets are set here
public class JsonSideKickListener extends JsonBaseListener {

    Deque<JsonNode> stack = new ArrayDeque<JsonNode>();
    JsonNode root = null;
    
    ImageIcon pairIcon = EclipseIconsPlugin.getIcon("field_public_obj.gif");
    ImageIcon objectIcon = EclipseIconsPlugin.getIcon("javaassist_co.gif");
    ImageIcon arrayIcon = EclipseIconsPlugin.getIcon("scope_obj.gif");
    

    public JsonNode getRoot() {
        return root;
    }

    @Override public void enterJson( JsonParser.JsonContext ctx ) {
        root = new JsonNode("root");
        root.setStartLocation( getStartLocation( ctx ) );
        root.setEndLocation( getEndLocation( ctx ) );
        stack.push( root );
    }
    
    @Override public void exitJson( JsonParser.JsonContext ctx ) {
        stack.pop();    // stack should be empty at this point
        JsonNode child = root.getFirstChild();
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

    @Override public void enterObject( JsonParser.ObjectContext ctx ) {
        JsonNode node = new JsonNode("object");
        node.setIsObject(true);
        node.setIcon(objectIcon);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.LBRACE().getSymbol().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.RBRACE().getSymbol().getStopIndex()));
        stack.push( node );
    }
    
    @Override public void exitObject( JsonParser.ObjectContext ctx ) {
        JsonNode node = stack.pop();
        JsonNode parent = stack.peek();
        parent.addChild(node);
    }
    
    @Override public void enterArray( JsonParser.ArrayContext ctx ) {
        JsonNode node = new JsonNode("array");
        node.setIsArray(true);
        node.setIcon(arrayIcon);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(ctx.LSQUARE().getSymbol().getStartIndex()));
        node.setEndPosition(new SideKickPosition(ctx.RSQUARE().getSymbol().getStopIndex()));
        stack.push( node );
    } 

    @Override public void exitArray( JsonParser.ArrayContext ctx ) {
        JsonNode node = stack.pop();
        JsonNode parent = stack.peek();
        parent.addChild(node);
    }

    @Override public void enterPair( JsonParser.PairContext ctx ) {
        TerminalNode string = ctx.STRING();
        JsonNode node = new JsonNode( string.getText() );
        node.setIsPair(true);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setStartPosition(new SideKickPosition(string.getSymbol().getStartIndex()));
        stack.push( node );
    }
    
    @Override public void exitPair( JsonParser.PairContext ctx ) {
        JsonNode node = stack.pop();            
        JsonNode value = node.getFirstChild();  
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
                    for (JsonNode newKid : value.getChildren()) {
                        node.addChild(newKid);   
                    }
                }
            } else if (value.isArray()) {
                //node.addChild(value);
                node.setIcon(arrayIcon);   
                if (value.hasChildren()) {
                    for (JsonNode newKid : value.getChildren()) {
                        node.addChild(newKid);   
                    }
                }
            }
        }
        JsonNode parent = stack.peek();
        parent.addChild( node );
    }
    
    @Override public void enterValue( JsonParser.ValueContext ctx ) {
        JsonNode node = new JsonNode();
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
    
    @Override public void exitValue( JsonParser.ValueContext ctx ) {
        JsonNode node = stack.peek();
        if (node.isNumberOrString()) {
            node = stack.pop();
            JsonNode parent = stack.peek();
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