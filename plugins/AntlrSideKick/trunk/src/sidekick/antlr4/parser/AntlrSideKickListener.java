package sidekick.antlr4.parser;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import sidekick.util.Location;
import sidekick.util.SideKickPosition;
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
    
	@Override public void enterLexerRule(ANTLRv4Parser.LexerRuleContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.TOKEN_REF().getText());
        Location startLocation;
        SideKickPosition startPosition;
        TerminalNode fragment = ctx.FRAGMENT();
        if (fragment != null) {
            startLocation = new Location(ctx.FRAGMENT().getSymbol().getLine(), ctx.FRAGMENT().getSymbol().getCharPositionInLine());
            startPosition = new SideKickPosition(ctx.FRAGMENT().getSymbol().getStartIndex());
        } else {
            startLocation = new Location(ctx.TOKEN_REF().getSymbol().getLine(), ctx.TOKEN_REF().getSymbol().getCharPositionInLine());   
            startPosition = new SideKickPosition(ctx.TOKEN_REF().getSymbol().getStartIndex());
        }
        node.setStartLocation(startLocation);
        node.setStartPosition(startPosition);
        node.setEndLocation(new Location(ctx.SEMI().getSymbol().getLine(), ctx.SEMI().getSymbol().getCharPositionInLine() + 1));
        node.setEndPosition(new SideKickPosition(ctx.SEMI().getSymbol().getStopIndex()));
        node.setIcon(lexerIcon);
        lexerRules.add(node);
	}
	
	@Override public void enterParserRuleSpec( ANTLRv4Parser.ParserRuleSpecContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.RULE_REF().getText());
        node.setStartLocation(new Location(ctx.RULE_REF().getSymbol().getLine(), ctx.RULE_REF().getSymbol().getCharPositionInLine()));
        node.setStartPosition(new SideKickPosition(ctx.RULE_REF().getSymbol().getStartIndex()));
        node.setEndLocation(new Location(ctx.SEMI().getSymbol().getLine(), ctx.SEMI().getSymbol().getCharPositionInLine() + 1));
        node.setEndPosition(new SideKickPosition(ctx.SEMI().getSymbol().getStopIndex()));
        node.setIcon(parserIcon);
        parserRules.add(node);
	}
    
}