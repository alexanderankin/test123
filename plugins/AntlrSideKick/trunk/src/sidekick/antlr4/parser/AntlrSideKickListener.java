package sidekick.antlr4.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

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
	@Override public void enterLexerRule(@NotNull ANTLRv4Parser.LexerRuleContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.TOKEN_REF().getText());
        node.setStartLocation(new Location(ctx.TOKEN_REF().getSymbol().getLine(), ctx.TOKEN_REF().getSymbol().getStartIndex()));
        node.setEndLocation(getEndLocation(ctx));
        node.setIcon(lexerIcon);
        lexerRules.add(node);
	}
	
	// elements_obj.gif
	@Override public void enterParserRuleSpec(@NotNull ANTLRv4Parser.ParserRuleSpecContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.RULE_REF().getText());
        node.setStartLocation(new Location(ctx.RULE_REF().getSymbol().getLine(), ctx.RULE_REF().getSymbol().getStartIndex()));
        node.setEndLocation(getEndLocation(ctx));
        node.setIcon(parserIcon);
        parserRules.add(node);
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

}