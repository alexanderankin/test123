package sidekick.antlr4.parser;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.util.Location;
import eclipseicons.EclipseIconsPlugin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import javax.swing.ImageIcon;

public class AntlrSideKickListener extends ANTLRv4ParserBaseListener {
    
    Deque<AntlrNode> stack = new ArrayDeque<AntlrNode>();
    List<AntlrNode> lexerRules = new ArrayList<AntlrNode>();
    List<AntlrNode> parserRules = new ArrayList<AntlrNode>();
    
    ImageIcon lexerIcon = EclipseIconsPlugin.getIcon("field_public_obj.gif");
    ImageIcon parserIcon = EclipseIconsPlugin.getIcon("elements_obj.gif");
    
    public List<AntlrNode> getLexerRules() {
        Collections.sort(lexerRules);
        return lexerRules;   
    }
    
    public List<AntlrNode> getParserRules() {
        Collections.sort(parserRules);
        return parserRules;   
    }
    
    // field_public_obj.gif
    // NOTE: could use Positions, the Token has a getStartIndex and getEndIndex which are offsets from the start of the input stream
	@Override public void enterLexerRule(@NotNull ANTLRv4Parser.LexerRuleContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.TOKEN_REF().getText());
        Location startLocation;
        TerminalNode fragment = ctx.FRAGMENT();
        if (fragment != null) {
            startLocation = new Location(ctx.FRAGMENT().getSymbol().getLine(), ctx.FRAGMENT().getSymbol().getCharPositionInLine());
        } else {
            startLocation = new Location(ctx.TOKEN_REF().getSymbol().getLine(), ctx.TOKEN_REF().getSymbol().getCharPositionInLine());   
        }
        node.setStartLocation(startLocation);
        node.setEndLocation(new Location(ctx.SEMI().getSymbol().getLine(), ctx.SEMI().getSymbol().getCharPositionInLine() + 1));
        node.setIcon(lexerIcon);
        lexerRules.add(node);
	}
	
	// elements_obj.gif
	@Override public void enterParserRuleSpec(@NotNull ANTLRv4Parser.ParserRuleSpecContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.RULE_REF().getText());
        node.setStartLocation(new Location(ctx.RULE_REF().getSymbol().getLine(), ctx.RULE_REF().getSymbol().getCharPositionInLine()));
        node.setEndLocation(new Location(ctx.SEMI().getSymbol().getLine(), ctx.SEMI().getSymbol().getCharPositionInLine() + 1));
        node.setIcon(parserIcon);
        parserRules.add(node);
	}
    
}