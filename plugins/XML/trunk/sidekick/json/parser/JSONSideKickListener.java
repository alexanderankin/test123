package sidekick.json.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.util.Location;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.ImageIcon;

import eclipseicons.EclipseIconsPlugin;

public class JSONSideKickListener extends JSONBaseListener {

    Deque<JSONNode> stack = new ArrayDeque<JSONNode>();
    JSONNode root = null;
    
    ImageIcon pairIcon = EclipseIconsPlugin.getIcon("field_public_obj.gif");
    ImageIcon objectIcon = EclipseIconsPlugin.getIcon("javaassist_co.gif");
    ImageIcon arrayIcon = EclipseIconsPlugin.getIcon("packd_obj.gif");
    

    public JSONNode getRoot() {
        return root;
    }
    @Override public void enterPair( @NotNull JSONParser.PairContext ctx ) {
        JSONNode node = new JSONNode( ctx.STRING().getText() );
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setIcon(pairIcon);
        stack.push( node );
    }
    @Override public void exitPair( @NotNull JSONParser.PairContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        parent.addChild( node );
    }
    @Override public void enterJson( @NotNull JSONParser.JsonContext ctx ) {
        root = new JSONNode();
        root.setStartLocation( getStartLocation( ctx ) );
        root.setEndLocation( getEndLocation( ctx ) );
        stack.push( root );
    }
    @Override public void exitJson( @NotNull JSONParser.JsonContext ctx ) {
        stack.pop();
    }
    @Override public void enterValue( @NotNull JSONParser.ValueContext ctx ) {
        // if the value is a number, which is defined as a number, true, false, or null,
        // then go ahead and add it to the name of the parent node. If it is an object
        // or an array, go ahead and recurse.
        JSONNode node = new JSONNode();
        TerminalNode number = ctx.NUMBER();
        if ( number != null ) {
            JSONNode parent = stack.peek();
            parent.setName(parent.getName() + ": " + number.getText());
        } else {
            number = ctx.STRING();
            if ( number != null ) {
                JSONNode parent = stack.peek();
                parent.setName(parent.getName() + ": " + number.getText());
            }
        }
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        stack.push( node );
    }
    @Override public void exitValue( @NotNull JSONParser.ValueContext ctx ) {
        JSONNode node = stack.pop();
        String name = node.getName();
        // numbers and strings are already part of the parent node name, this
        // next 'if' adds object and array nodes to the parent.
        if (name == null || name.isEmpty()) {
            JSONNode parent = stack.peek();
            parent.addChild( node );
        }
    }
    @Override public void enterObject( @NotNull JSONParser.ObjectContext ctx ) {
        JSONNode node = new JSONNode();
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setIcon(objectIcon);
        stack.push( node );
    }
    @Override public void exitObject( @NotNull JSONParser.ObjectContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        parent.addChild( node );
    }
    @Override public void enterArray( @NotNull JSONParser.ArrayContext ctx ) {
        JSONNode node = new JSONNode();
        node.setIsArray(true);
        node.setStartLocation( getStartLocation( ctx ) );
        node.setEndLocation( getEndLocation( ctx ) );
        node.setIcon(arrayIcon);
        stack.push( node );
    } 
    @Override public void exitArray( @NotNull JSONParser.ArrayContext ctx ) {
        JSONNode node = stack.pop();
        JSONNode parent = stack.peek();
        parent.addChild( node );
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