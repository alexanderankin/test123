package sidekick.antlr4.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import sidekick.util.Location;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class AntlrSideKickListener extends ANTLRv4ParserBaseListener {
    
    Deque<AntlrNode> stack = new ArrayDeque<AntlrNode>();
    List<AntlrNode> lexerRules = new ArrayList<AntlrNode>();
    List<AntlrNode> parserRules = new ArrayList<AntlrNode>();
    
    public List<AntlrNode> getLexerRules() {
        Collections.sort(lexerRules);
        return lexerRules;   
    }
    
    public List<AntlrNode> getParserRules() {
        Collections.sort(parserRules);
        return parserRules;   
    }
    
	@Override public void enterLexerRule(@NotNull ANTLRv4Parser.LexerRuleContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.TOKEN_REF().getText());
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
        lexerRules.add(node);
	}
	@Override public void enterParserRuleSpec(@NotNull ANTLRv4Parser.ParserRuleSpecContext ctx) {
        AntlrNode node = new AntlrNode();
        node.setName(ctx.RULE_REF().getText());
        node.setStartLocation(getStartLocation(ctx));
        node.setEndLocation(getEndLocation(ctx));
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