// Generated from ANTLRv4Parser.g4 by ANTLR 4.4

    package sidekick.antlr4.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ANTLRv4Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BEGIN_ARG_ACTION=7, ERRCHAR=52, MODE=23, LT=31, RULE_REF=2, FINALLY=22, 
		TOKEN_REF=1, FRAGMENT=11, UNTERMINATED_CHAR_SET=55, ID=46, PARSER=13, 
		LEXER=12, UNTERMINATED_STRING_LITERAL=49, PLUS_ASSIGN=37, LPAREN=28, DOT=40, 
		RPAREN=29, OPTIONS=8, NOT=44, BLOCK_COMMENT=5, LINE_COMMENT=6, AT=42, 
		RBRACE=45, PUBLIC=16, THROWS=20, DOLLAR=39, PLUS=36, RANGE=41, LOCALS=19, 
		SEMI=27, TOKENS=9, DOC_COMMENT=4, COLONCOLON=25, ASSIGN=33, QUESTION=34, 
		INT=47, IMPORT=10, CATCH=21, PROTECTED=15, WS=50, LEXER_CHAR_SET=3, COMMA=26, 
		POUND=43, OR=38, RETURNS=18, ARG_ACTION=53, COLON=24, UNTERMINATED_ARG_ACTION=54, 
		GT=32, PRIVATE=17, RARROW=30, STAR=35, ACTION=51, GRAMMAR=14, STRING_LITERAL=48;
	public static final String[] tokenNames = {
		"<INVALID>", "TOKEN_REF", "RULE_REF", "LEXER_CHAR_SET", "DOC_COMMENT", 
		"BLOCK_COMMENT", "LINE_COMMENT", "BEGIN_ARG_ACTION", "OPTIONS", "TOKENS", 
		"'import'", "'fragment'", "'lexer'", "'parser'", "'grammar'", "'protected'", 
		"'public'", "'private'", "'returns'", "'locals'", "'throws'", "'catch'", 
		"'finally'", "'mode'", "':'", "'::'", "','", "';'", "'('", "')'", "'->'", 
		"'<'", "'>'", "'='", "'?'", "'*'", "'+'", "'+='", "'|'", "'$'", "'.'", 
		"'..'", "'@'", "'#'", "'~'", "'}'", "ID", "INT", "STRING_LITERAL", "UNTERMINATED_STRING_LITERAL", 
		"WS", "ACTION", "ERRCHAR", "ARG_ACTION", "UNTERMINATED_ARG_ACTION", "UNTERMINATED_CHAR_SET"
	};
	public static final int
		RULE_grammarSpec = 0, RULE_grammarType = 1, RULE_prequelConstruct = 2, 
		RULE_optionsSpec = 3, RULE_option = 4, RULE_optionValue = 5, RULE_delegateGrammars = 6, 
		RULE_delegateGrammar = 7, RULE_tokensSpec = 8, RULE_action = 9, RULE_actionScopeName = 10, 
		RULE_modeSpec = 11, RULE_rules = 12, RULE_ruleSpec = 13, RULE_parserRuleSpec = 14, 
		RULE_exceptionGroup = 15, RULE_exceptionHandler = 16, RULE_finallyClause = 17, 
		RULE_rulePrequel = 18, RULE_ruleReturns = 19, RULE_throwsSpec = 20, RULE_localsSpec = 21, 
		RULE_ruleAction = 22, RULE_ruleModifiers = 23, RULE_ruleModifier = 24, 
		RULE_ruleBlock = 25, RULE_ruleAltList = 26, RULE_labeledAlt = 27, RULE_lexerRule = 28, 
		RULE_lexerRuleBlock = 29, RULE_lexerAltList = 30, RULE_lexerAlt = 31, 
		RULE_lexerElements = 32, RULE_lexerElement = 33, RULE_labeledLexerElement = 34, 
		RULE_lexerBlock = 35, RULE_lexerCommands = 36, RULE_lexerCommand = 37, 
		RULE_lexerCommandName = 38, RULE_lexerCommandExpr = 39, RULE_altList = 40, 
		RULE_alternative = 41, RULE_element = 42, RULE_labeledElement = 43, RULE_ebnf = 44, 
		RULE_blockSuffix = 45, RULE_ebnfSuffix = 46, RULE_lexerAtom = 47, RULE_atom = 48, 
		RULE_notSet = 49, RULE_blockSet = 50, RULE_setElement = 51, RULE_block = 52, 
		RULE_ruleref = 53, RULE_range = 54, RULE_terminal = 55, RULE_elementOptions = 56, 
		RULE_elementOption = 57, RULE_id = 58;
	public static final String[] ruleNames = {
		"grammarSpec", "grammarType", "prequelConstruct", "optionsSpec", "option", 
		"optionValue", "delegateGrammars", "delegateGrammar", "tokensSpec", "action", 
		"actionScopeName", "modeSpec", "rules", "ruleSpec", "parserRuleSpec", 
		"exceptionGroup", "exceptionHandler", "finallyClause", "rulePrequel", 
		"ruleReturns", "throwsSpec", "localsSpec", "ruleAction", "ruleModifiers", 
		"ruleModifier", "ruleBlock", "ruleAltList", "labeledAlt", "lexerRule", 
		"lexerRuleBlock", "lexerAltList", "lexerAlt", "lexerElements", "lexerElement", 
		"labeledLexerElement", "lexerBlock", "lexerCommands", "lexerCommand", 
		"lexerCommandName", "lexerCommandExpr", "altList", "alternative", "element", 
		"labeledElement", "ebnf", "blockSuffix", "ebnfSuffix", "lexerAtom", "atom", 
		"notSet", "blockSet", "setElement", "block", "ruleref", "range", "terminal", 
		"elementOptions", "elementOption", "id"
	};

	@Override
	public String getGrammarFileName() { return "ANTLRv4Parser.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ANTLRv4Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class GrammarSpecContext extends ParserRuleContext {
		public List<PrequelConstructContext> prequelConstruct() {
			return getRuleContexts(PrequelConstructContext.class);
		}
		public TerminalNode DOC_COMMENT() { return getToken(ANTLRv4Parser.DOC_COMMENT, 0); }
		public GrammarTypeContext grammarType() {
			return getRuleContext(GrammarTypeContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(ANTLRv4Parser.SEMI, 0); }
		public TerminalNode EOF() { return getToken(ANTLRv4Parser.EOF, 0); }
		public PrequelConstructContext prequelConstruct(int i) {
			return getRuleContext(PrequelConstructContext.class,i);
		}
		public RulesContext rules() {
			return getRuleContext(RulesContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public ModeSpecContext modeSpec(int i) {
			return getRuleContext(ModeSpecContext.class,i);
		}
		public List<ModeSpecContext> modeSpec() {
			return getRuleContexts(ModeSpecContext.class);
		}
		public GrammarSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_grammarSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterGrammarSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitGrammarSpec(this);
		}
	}

	public final GrammarSpecContext grammarSpec() throws RecognitionException {
		GrammarSpecContext _localctx = new GrammarSpecContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_grammarSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			_la = _input.LA(1);
			if (_la==DOC_COMMENT) {
				{
				setState(118); match(DOC_COMMENT);
				}
			}

			setState(121); grammarType();
			setState(122); id();
			setState(123); match(SEMI);
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTIONS) | (1L << TOKENS) | (1L << IMPORT) | (1L << AT))) != 0)) {
				{
				{
				setState(124); prequelConstruct();
				}
				}
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(130); rules();
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MODE) {
				{
				{
				setState(131); modeSpec();
				}
				}
				setState(136);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(137); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GrammarTypeContext extends ParserRuleContext {
		public TerminalNode PARSER() { return getToken(ANTLRv4Parser.PARSER, 0); }
		public TerminalNode GRAMMAR() { return getToken(ANTLRv4Parser.GRAMMAR, 0); }
		public TerminalNode LEXER() { return getToken(ANTLRv4Parser.LEXER, 0); }
		public GrammarTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_grammarType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterGrammarType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitGrammarType(this);
		}
	}

	public final GrammarTypeContext grammarType() throws RecognitionException {
		GrammarTypeContext _localctx = new GrammarTypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_grammarType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			switch (_input.LA(1)) {
			case LEXER:
				{
				setState(139); match(LEXER);
				setState(140); match(GRAMMAR);
				}
				break;
			case PARSER:
				{
				setState(141); match(PARSER);
				setState(142); match(GRAMMAR);
				}
				break;
			case GRAMMAR:
				{
				setState(143); match(GRAMMAR);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrequelConstructContext extends ParserRuleContext {
		public DelegateGrammarsContext delegateGrammars() {
			return getRuleContext(DelegateGrammarsContext.class,0);
		}
		public TokensSpecContext tokensSpec() {
			return getRuleContext(TokensSpecContext.class,0);
		}
		public OptionsSpecContext optionsSpec() {
			return getRuleContext(OptionsSpecContext.class,0);
		}
		public ActionContext action() {
			return getRuleContext(ActionContext.class,0);
		}
		public PrequelConstructContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prequelConstruct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterPrequelConstruct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitPrequelConstruct(this);
		}
	}

	public final PrequelConstructContext prequelConstruct() throws RecognitionException {
		PrequelConstructContext _localctx = new PrequelConstructContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_prequelConstruct);
		try {
			setState(150);
			switch (_input.LA(1)) {
			case OPTIONS:
				enterOuterAlt(_localctx, 1);
				{
				setState(146); optionsSpec();
				}
				break;
			case IMPORT:
				enterOuterAlt(_localctx, 2);
				{
				setState(147); delegateGrammars();
				}
				break;
			case TOKENS:
				enterOuterAlt(_localctx, 3);
				{
				setState(148); tokensSpec();
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 4);
				{
				setState(149); action();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptionsSpecContext extends ParserRuleContext {
		public TerminalNode RBRACE() { return getToken(ANTLRv4Parser.RBRACE, 0); }
		public OptionContext option(int i) {
			return getRuleContext(OptionContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(ANTLRv4Parser.SEMI); }
		public List<OptionContext> option() {
			return getRuleContexts(OptionContext.class);
		}
		public TerminalNode SEMI(int i) {
			return getToken(ANTLRv4Parser.SEMI, i);
		}
		public TerminalNode OPTIONS() { return getToken(ANTLRv4Parser.OPTIONS, 0); }
		public OptionsSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionsSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterOptionsSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitOptionsSpec(this);
		}
	}

	public final OptionsSpecContext optionsSpec() throws RecognitionException {
		OptionsSpecContext _localctx = new OptionsSpecContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_optionsSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152); match(OPTIONS);
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TOKEN_REF || _la==RULE_REF) {
				{
				{
				setState(153); option();
				setState(154); match(SEMI);
				}
				}
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(161); match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptionContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(ANTLRv4Parser.ASSIGN, 0); }
		public OptionValueContext optionValue() {
			return getRuleContext(OptionValueContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public OptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_option; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitOption(this);
		}
	}

	public final OptionContext option() throws RecognitionException {
		OptionContext _localctx = new OptionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_option);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163); id();
			setState(164); match(ASSIGN);
			setState(165); optionValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptionValueContext extends ParserRuleContext {
		public List<TerminalNode> DOT() { return getTokens(ANTLRv4Parser.DOT); }
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public TerminalNode ACTION() { return getToken(ANTLRv4Parser.ACTION, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(ANTLRv4Parser.STRING_LITERAL, 0); }
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public TerminalNode INT() { return getToken(ANTLRv4Parser.INT, 0); }
		public TerminalNode DOT(int i) {
			return getToken(ANTLRv4Parser.DOT, i);
		}
		public OptionValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterOptionValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitOptionValue(this);
		}
	}

	public final OptionValueContext optionValue() throws RecognitionException {
		OptionValueContext _localctx = new OptionValueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_optionValue);
		int _la;
		try {
			setState(178);
			switch (_input.LA(1)) {
			case TOKEN_REF:
			case RULE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(167); id();
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(168); match(DOT);
					setState(169); id();
					}
					}
					setState(174);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(175); match(STRING_LITERAL);
				}
				break;
			case ACTION:
				enterOuterAlt(_localctx, 3);
				{
				setState(176); match(ACTION);
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 4);
				{
				setState(177); match(INT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DelegateGrammarsContext extends ParserRuleContext {
		public TerminalNode SEMI() { return getToken(ANTLRv4Parser.SEMI, 0); }
		public List<TerminalNode> COMMA() { return getTokens(ANTLRv4Parser.COMMA); }
		public List<DelegateGrammarContext> delegateGrammar() {
			return getRuleContexts(DelegateGrammarContext.class);
		}
		public TerminalNode IMPORT() { return getToken(ANTLRv4Parser.IMPORT, 0); }
		public DelegateGrammarContext delegateGrammar(int i) {
			return getRuleContext(DelegateGrammarContext.class,i);
		}
		public TerminalNode COMMA(int i) {
			return getToken(ANTLRv4Parser.COMMA, i);
		}
		public DelegateGrammarsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delegateGrammars; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterDelegateGrammars(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitDelegateGrammars(this);
		}
	}

	public final DelegateGrammarsContext delegateGrammars() throws RecognitionException {
		DelegateGrammarsContext _localctx = new DelegateGrammarsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_delegateGrammars);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180); match(IMPORT);
			setState(181); delegateGrammar();
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(182); match(COMMA);
				setState(183); delegateGrammar();
				}
				}
				setState(188);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(189); match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DelegateGrammarContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(ANTLRv4Parser.ASSIGN, 0); }
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public DelegateGrammarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_delegateGrammar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterDelegateGrammar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitDelegateGrammar(this);
		}
	}

	public final DelegateGrammarContext delegateGrammar() throws RecognitionException {
		DelegateGrammarContext _localctx = new DelegateGrammarContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_delegateGrammar);
		try {
			setState(196);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(191); id();
				setState(192); match(ASSIGN);
				setState(193); id();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(195); id();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TokensSpecContext extends ParserRuleContext {
		public TerminalNode RBRACE() { return getToken(ANTLRv4Parser.RBRACE, 0); }
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ANTLRv4Parser.COMMA); }
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public TerminalNode TOKENS() { return getToken(ANTLRv4Parser.TOKENS, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(ANTLRv4Parser.COMMA, i);
		}
		public TokensSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tokensSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterTokensSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitTokensSpec(this);
		}
	}

	public final TokensSpecContext tokensSpec() throws RecognitionException {
		TokensSpecContext _localctx = new TokensSpecContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_tokensSpec);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(198); match(TOKENS);
			setState(199); id();
			setState(204);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(200); match(COMMA);
					setState(201); id();
					}
					} 
				}
				setState(206);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			setState(208);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(207); match(COMMA);
				}
			}

			setState(210); match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActionContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(ANTLRv4Parser.AT, 0); }
		public TerminalNode ACTION() { return getToken(ANTLRv4Parser.ACTION, 0); }
		public ActionScopeNameContext actionScopeName() {
			return getRuleContext(ActionScopeNameContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode COLONCOLON() { return getToken(ANTLRv4Parser.COLONCOLON, 0); }
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitAction(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_action);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212); match(AT);
			setState(216);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(213); actionScopeName();
				setState(214); match(COLONCOLON);
				}
				break;
			}
			setState(218); id();
			setState(219); match(ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActionScopeNameContext extends ParserRuleContext {
		public TerminalNode PARSER() { return getToken(ANTLRv4Parser.PARSER, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode LEXER() { return getToken(ANTLRv4Parser.LEXER, 0); }
		public ActionScopeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actionScopeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterActionScopeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitActionScopeName(this);
		}
	}

	public final ActionScopeNameContext actionScopeName() throws RecognitionException {
		ActionScopeNameContext _localctx = new ActionScopeNameContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_actionScopeName);
		try {
			setState(224);
			switch (_input.LA(1)) {
			case TOKEN_REF:
			case RULE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(221); id();
				}
				break;
			case LEXER:
				enterOuterAlt(_localctx, 2);
				{
				setState(222); match(LEXER);
				}
				break;
			case PARSER:
				enterOuterAlt(_localctx, 3);
				{
				setState(223); match(PARSER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModeSpecContext extends ParserRuleContext {
		public TerminalNode SEMI() { return getToken(ANTLRv4Parser.SEMI, 0); }
		public List<LexerRuleContext> lexerRule() {
			return getRuleContexts(LexerRuleContext.class);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public LexerRuleContext lexerRule(int i) {
			return getRuleContext(LexerRuleContext.class,i);
		}
		public TerminalNode MODE() { return getToken(ANTLRv4Parser.MODE, 0); }
		public ModeSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modeSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterModeSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitModeSpec(this);
		}
	}

	public final ModeSpecContext modeSpec() throws RecognitionException {
		ModeSpecContext _localctx = new ModeSpecContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_modeSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226); match(MODE);
			setState(227); id();
			setState(228); match(SEMI);
			setState(232);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOKEN_REF) | (1L << DOC_COMMENT) | (1L << FRAGMENT))) != 0)) {
				{
				{
				setState(229); lexerRule();
				}
				}
				setState(234);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RulesContext extends ParserRuleContext {
		public List<RuleSpecContext> ruleSpec() {
			return getRuleContexts(RuleSpecContext.class);
		}
		public RuleSpecContext ruleSpec(int i) {
			return getRuleContext(RuleSpecContext.class,i);
		}
		public RulesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rules; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRules(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRules(this);
		}
	}

	public final RulesContext rules() throws RecognitionException {
		RulesContext _localctx = new RulesContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_rules);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOKEN_REF) | (1L << RULE_REF) | (1L << DOC_COMMENT) | (1L << FRAGMENT) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << PRIVATE))) != 0)) {
				{
				{
				setState(235); ruleSpec();
				}
				}
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleSpecContext extends ParserRuleContext {
		public ParserRuleSpecContext parserRuleSpec() {
			return getRuleContext(ParserRuleSpecContext.class,0);
		}
		public LexerRuleContext lexerRule() {
			return getRuleContext(LexerRuleContext.class,0);
		}
		public RuleSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleSpec(this);
		}
	}

	public final RuleSpecContext ruleSpec() throws RecognitionException {
		RuleSpecContext _localctx = new RuleSpecContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_ruleSpec);
		try {
			setState(243);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(241); parserRuleSpec();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(242); lexerRule();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParserRuleSpecContext extends ParserRuleContext {
		public TerminalNode DOC_COMMENT() { return getToken(ANTLRv4Parser.DOC_COMMENT, 0); }
		public ExceptionGroupContext exceptionGroup() {
			return getRuleContext(ExceptionGroupContext.class,0);
		}
		public TerminalNode RULE_REF() { return getToken(ANTLRv4Parser.RULE_REF, 0); }
		public TerminalNode COLON() { return getToken(ANTLRv4Parser.COLON, 0); }
		public RuleModifiersContext ruleModifiers() {
			return getRuleContext(RuleModifiersContext.class,0);
		}
		public RuleReturnsContext ruleReturns() {
			return getRuleContext(RuleReturnsContext.class,0);
		}
		public ThrowsSpecContext throwsSpec() {
			return getRuleContext(ThrowsSpecContext.class,0);
		}
		public RuleBlockContext ruleBlock() {
			return getRuleContext(RuleBlockContext.class,0);
		}
		public LocalsSpecContext localsSpec() {
			return getRuleContext(LocalsSpecContext.class,0);
		}
		public TerminalNode ARG_ACTION() { return getToken(ANTLRv4Parser.ARG_ACTION, 0); }
		public RulePrequelContext rulePrequel(int i) {
			return getRuleContext(RulePrequelContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(ANTLRv4Parser.SEMI, 0); }
		public List<RulePrequelContext> rulePrequel() {
			return getRuleContexts(RulePrequelContext.class);
		}
		public ParserRuleSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parserRuleSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterParserRuleSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitParserRuleSpec(this);
		}
	}

	public final ParserRuleSpecContext parserRuleSpec() throws RecognitionException {
		ParserRuleSpecContext _localctx = new ParserRuleSpecContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_parserRuleSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			_la = _input.LA(1);
			if (_la==DOC_COMMENT) {
				{
				setState(245); match(DOC_COMMENT);
				}
			}

			setState(249);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FRAGMENT) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << PRIVATE))) != 0)) {
				{
				setState(248); ruleModifiers();
				}
			}

			setState(251); match(RULE_REF);
			setState(253);
			_la = _input.LA(1);
			if (_la==ARG_ACTION) {
				{
				setState(252); match(ARG_ACTION);
				}
			}

			setState(256);
			_la = _input.LA(1);
			if (_la==RETURNS) {
				{
				setState(255); ruleReturns();
				}
			}

			setState(259);
			_la = _input.LA(1);
			if (_la==THROWS) {
				{
				setState(258); throwsSpec();
				}
			}

			setState(262);
			_la = _input.LA(1);
			if (_la==LOCALS) {
				{
				setState(261); localsSpec();
				}
			}

			setState(267);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPTIONS || _la==AT) {
				{
				{
				setState(264); rulePrequel();
				}
				}
				setState(269);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(270); match(COLON);
			setState(271); ruleBlock();
			setState(272); match(SEMI);
			setState(273); exceptionGroup();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExceptionGroupContext extends ParserRuleContext {
		public ExceptionHandlerContext exceptionHandler(int i) {
			return getRuleContext(ExceptionHandlerContext.class,i);
		}
		public List<ExceptionHandlerContext> exceptionHandler() {
			return getRuleContexts(ExceptionHandlerContext.class);
		}
		public FinallyClauseContext finallyClause() {
			return getRuleContext(FinallyClauseContext.class,0);
		}
		public ExceptionGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exceptionGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterExceptionGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitExceptionGroup(this);
		}
	}

	public final ExceptionGroupContext exceptionGroup() throws RecognitionException {
		ExceptionGroupContext _localctx = new ExceptionGroupContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_exceptionGroup);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CATCH) {
				{
				{
				setState(275); exceptionHandler();
				}
				}
				setState(280);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(282);
			_la = _input.LA(1);
			if (_la==FINALLY) {
				{
				setState(281); finallyClause();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExceptionHandlerContext extends ParserRuleContext {
		public TerminalNode ARG_ACTION() { return getToken(ANTLRv4Parser.ARG_ACTION, 0); }
		public TerminalNode ACTION() { return getToken(ANTLRv4Parser.ACTION, 0); }
		public TerminalNode CATCH() { return getToken(ANTLRv4Parser.CATCH, 0); }
		public ExceptionHandlerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exceptionHandler; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterExceptionHandler(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitExceptionHandler(this);
		}
	}

	public final ExceptionHandlerContext exceptionHandler() throws RecognitionException {
		ExceptionHandlerContext _localctx = new ExceptionHandlerContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_exceptionHandler);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284); match(CATCH);
			setState(285); match(ARG_ACTION);
			setState(286); match(ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FinallyClauseContext extends ParserRuleContext {
		public TerminalNode ACTION() { return getToken(ANTLRv4Parser.ACTION, 0); }
		public TerminalNode FINALLY() { return getToken(ANTLRv4Parser.FINALLY, 0); }
		public FinallyClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_finallyClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterFinallyClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitFinallyClause(this);
		}
	}

	public final FinallyClauseContext finallyClause() throws RecognitionException {
		FinallyClauseContext _localctx = new FinallyClauseContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_finallyClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288); match(FINALLY);
			setState(289); match(ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RulePrequelContext extends ParserRuleContext {
		public RuleActionContext ruleAction() {
			return getRuleContext(RuleActionContext.class,0);
		}
		public OptionsSpecContext optionsSpec() {
			return getRuleContext(OptionsSpecContext.class,0);
		}
		public RulePrequelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rulePrequel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRulePrequel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRulePrequel(this);
		}
	}

	public final RulePrequelContext rulePrequel() throws RecognitionException {
		RulePrequelContext _localctx = new RulePrequelContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_rulePrequel);
		try {
			setState(293);
			switch (_input.LA(1)) {
			case OPTIONS:
				enterOuterAlt(_localctx, 1);
				{
				setState(291); optionsSpec();
				}
				break;
			case AT:
				enterOuterAlt(_localctx, 2);
				{
				setState(292); ruleAction();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleReturnsContext extends ParserRuleContext {
		public TerminalNode ARG_ACTION() { return getToken(ANTLRv4Parser.ARG_ACTION, 0); }
		public TerminalNode RETURNS() { return getToken(ANTLRv4Parser.RETURNS, 0); }
		public RuleReturnsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleReturns; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleReturns(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleReturns(this);
		}
	}

	public final RuleReturnsContext ruleReturns() throws RecognitionException {
		RuleReturnsContext _localctx = new RuleReturnsContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_ruleReturns);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(295); match(RETURNS);
			setState(296); match(ARG_ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ThrowsSpecContext extends ParserRuleContext {
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ANTLRv4Parser.COMMA); }
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public TerminalNode THROWS() { return getToken(ANTLRv4Parser.THROWS, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(ANTLRv4Parser.COMMA, i);
		}
		public ThrowsSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throwsSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterThrowsSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitThrowsSpec(this);
		}
	}

	public final ThrowsSpecContext throwsSpec() throws RecognitionException {
		ThrowsSpecContext _localctx = new ThrowsSpecContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_throwsSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298); match(THROWS);
			setState(299); id();
			setState(304);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(300); match(COMMA);
				setState(301); id();
				}
				}
				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LocalsSpecContext extends ParserRuleContext {
		public TerminalNode ARG_ACTION() { return getToken(ANTLRv4Parser.ARG_ACTION, 0); }
		public TerminalNode LOCALS() { return getToken(ANTLRv4Parser.LOCALS, 0); }
		public LocalsSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localsSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLocalsSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLocalsSpec(this);
		}
	}

	public final LocalsSpecContext localsSpec() throws RecognitionException {
		LocalsSpecContext _localctx = new LocalsSpecContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_localsSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(307); match(LOCALS);
			setState(308); match(ARG_ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleActionContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(ANTLRv4Parser.AT, 0); }
		public TerminalNode ACTION() { return getToken(ANTLRv4Parser.ACTION, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public RuleActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleAction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleAction(this);
		}
	}

	public final RuleActionContext ruleAction() throws RecognitionException {
		RuleActionContext _localctx = new RuleActionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_ruleAction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310); match(AT);
			setState(311); id();
			setState(312); match(ACTION);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleModifiersContext extends ParserRuleContext {
		public RuleModifierContext ruleModifier(int i) {
			return getRuleContext(RuleModifierContext.class,i);
		}
		public List<RuleModifierContext> ruleModifier() {
			return getRuleContexts(RuleModifierContext.class);
		}
		public RuleModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleModifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleModifiers(this);
		}
	}

	public final RuleModifiersContext ruleModifiers() throws RecognitionException {
		RuleModifiersContext _localctx = new RuleModifiersContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_ruleModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(315); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(314); ruleModifier();
				}
				}
				setState(317); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FRAGMENT) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << PRIVATE))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleModifierContext extends ParserRuleContext {
		public TerminalNode FRAGMENT() { return getToken(ANTLRv4Parser.FRAGMENT, 0); }
		public TerminalNode PROTECTED() { return getToken(ANTLRv4Parser.PROTECTED, 0); }
		public TerminalNode PUBLIC() { return getToken(ANTLRv4Parser.PUBLIC, 0); }
		public TerminalNode PRIVATE() { return getToken(ANTLRv4Parser.PRIVATE, 0); }
		public RuleModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleModifier(this);
		}
	}

	public final RuleModifierContext ruleModifier() throws RecognitionException {
		RuleModifierContext _localctx = new RuleModifierContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_ruleModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FRAGMENT) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << PRIVATE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleBlockContext extends ParserRuleContext {
		public RuleAltListContext ruleAltList() {
			return getRuleContext(RuleAltListContext.class,0);
		}
		public RuleBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleBlock(this);
		}
	}

	public final RuleBlockContext ruleBlock() throws RecognitionException {
		RuleBlockContext _localctx = new RuleBlockContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_ruleBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(321); ruleAltList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RuleAltListContext extends ParserRuleContext {
		public LabeledAltContext labeledAlt(int i) {
			return getRuleContext(LabeledAltContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(ANTLRv4Parser.OR); }
		public TerminalNode OR(int i) {
			return getToken(ANTLRv4Parser.OR, i);
		}
		public List<LabeledAltContext> labeledAlt() {
			return getRuleContexts(LabeledAltContext.class);
		}
		public RuleAltListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleAltList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleAltList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleAltList(this);
		}
	}

	public final RuleAltListContext ruleAltList() throws RecognitionException {
		RuleAltListContext _localctx = new RuleAltListContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_ruleAltList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(323); labeledAlt();
			setState(328);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(324); match(OR);
				setState(325); labeledAlt();
				}
				}
				setState(330);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabeledAltContext extends ParserRuleContext {
		public AlternativeContext alternative() {
			return getRuleContext(AlternativeContext.class,0);
		}
		public TerminalNode POUND() { return getToken(ANTLRv4Parser.POUND, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public LabeledAltContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledAlt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLabeledAlt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLabeledAlt(this);
		}
	}

	public final LabeledAltContext labeledAlt() throws RecognitionException {
		LabeledAltContext _localctx = new LabeledAltContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_labeledAlt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331); alternative();
			setState(334);
			_la = _input.LA(1);
			if (_la==POUND) {
				{
				setState(332); match(POUND);
				setState(333); id();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerRuleContext extends ParserRuleContext {
		public TerminalNode DOC_COMMENT() { return getToken(ANTLRv4Parser.DOC_COMMENT, 0); }
		public TerminalNode FRAGMENT() { return getToken(ANTLRv4Parser.FRAGMENT, 0); }
		public TerminalNode TOKEN_REF() { return getToken(ANTLRv4Parser.TOKEN_REF, 0); }
		public TerminalNode SEMI() { return getToken(ANTLRv4Parser.SEMI, 0); }
		public TerminalNode COLON() { return getToken(ANTLRv4Parser.COLON, 0); }
		public LexerRuleBlockContext lexerRuleBlock() {
			return getRuleContext(LexerRuleBlockContext.class,0);
		}
		public LexerRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerRule(this);
		}
	}

	public final LexerRuleContext lexerRule() throws RecognitionException {
		LexerRuleContext _localctx = new LexerRuleContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_lexerRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			_la = _input.LA(1);
			if (_la==DOC_COMMENT) {
				{
				setState(336); match(DOC_COMMENT);
				}
			}

			setState(340);
			_la = _input.LA(1);
			if (_la==FRAGMENT) {
				{
				setState(339); match(FRAGMENT);
				}
			}

			setState(342); match(TOKEN_REF);
			setState(343); match(COLON);
			setState(344); lexerRuleBlock();
			setState(345); match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerRuleBlockContext extends ParserRuleContext {
		public LexerAltListContext lexerAltList() {
			return getRuleContext(LexerAltListContext.class,0);
		}
		public LexerRuleBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerRuleBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerRuleBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerRuleBlock(this);
		}
	}

	public final LexerRuleBlockContext lexerRuleBlock() throws RecognitionException {
		LexerRuleBlockContext _localctx = new LexerRuleBlockContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_lexerRuleBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(347); lexerAltList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerAltListContext extends ParserRuleContext {
		public List<LexerAltContext> lexerAlt() {
			return getRuleContexts(LexerAltContext.class);
		}
		public List<TerminalNode> OR() { return getTokens(ANTLRv4Parser.OR); }
		public LexerAltContext lexerAlt(int i) {
			return getRuleContext(LexerAltContext.class,i);
		}
		public TerminalNode OR(int i) {
			return getToken(ANTLRv4Parser.OR, i);
		}
		public LexerAltListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerAltList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerAltList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerAltList(this);
		}
	}

	public final LexerAltListContext lexerAltList() throws RecognitionException {
		LexerAltListContext _localctx = new LexerAltListContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_lexerAltList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349); lexerAlt();
			setState(354);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(350); match(OR);
				setState(351); lexerAlt();
				}
				}
				setState(356);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerAltContext extends ParserRuleContext {
		public LexerElementsContext lexerElements() {
			return getRuleContext(LexerElementsContext.class,0);
		}
		public LexerCommandsContext lexerCommands() {
			return getRuleContext(LexerCommandsContext.class,0);
		}
		public LexerAltContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerAlt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerAlt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerAlt(this);
		}
	}

	public final LexerAltContext lexerAlt() throws RecognitionException {
		LexerAltContext _localctx = new LexerAltContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_lexerAlt);
		int _la;
		try {
			setState(362);
			switch (_input.LA(1)) {
			case TOKEN_REF:
			case RULE_REF:
			case LEXER_CHAR_SET:
			case LPAREN:
			case DOT:
			case NOT:
			case STRING_LITERAL:
			case ACTION:
				enterOuterAlt(_localctx, 1);
				{
				setState(357); lexerElements();
				setState(359);
				_la = _input.LA(1);
				if (_la==RARROW) {
					{
					setState(358); lexerCommands();
					}
				}

				}
				break;
			case SEMI:
			case RPAREN:
			case OR:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerElementsContext extends ParserRuleContext {
		public List<LexerElementContext> lexerElement() {
			return getRuleContexts(LexerElementContext.class);
		}
		public LexerElementContext lexerElement(int i) {
			return getRuleContext(LexerElementContext.class,i);
		}
		public LexerElementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerElements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerElements(this);
		}
	}

	public final LexerElementsContext lexerElements() throws RecognitionException {
		LexerElementsContext _localctx = new LexerElementsContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_lexerElements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(364); lexerElement();
				}
				}
				setState(367); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOKEN_REF) | (1L << RULE_REF) | (1L << LEXER_CHAR_SET) | (1L << LPAREN) | (1L << DOT) | (1L << NOT) | (1L << STRING_LITERAL) | (1L << ACTION))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerElementContext extends ParserRuleContext {
		public EbnfSuffixContext ebnfSuffix() {
			return getRuleContext(EbnfSuffixContext.class,0);
		}
		public TerminalNode ACTION() { return getToken(ANTLRv4Parser.ACTION, 0); }
		public LabeledLexerElementContext labeledLexerElement() {
			return getRuleContext(LabeledLexerElementContext.class,0);
		}
		public LexerAtomContext lexerAtom() {
			return getRuleContext(LexerAtomContext.class,0);
		}
		public LexerBlockContext lexerBlock() {
			return getRuleContext(LexerBlockContext.class,0);
		}
		public TerminalNode QUESTION() { return getToken(ANTLRv4Parser.QUESTION, 0); }
		public LexerElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerElement(this);
		}
	}

	public final LexerElementContext lexerElement() throws RecognitionException {
		LexerElementContext _localctx = new LexerElementContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_lexerElement);
		int _la;
		try {
			setState(385);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(369); labeledLexerElement();
				setState(371);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << QUESTION) | (1L << STAR) | (1L << PLUS))) != 0)) {
					{
					setState(370); ebnfSuffix();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(373); lexerAtom();
				setState(375);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << QUESTION) | (1L << STAR) | (1L << PLUS))) != 0)) {
					{
					setState(374); ebnfSuffix();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(377); lexerBlock();
				setState(379);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << QUESTION) | (1L << STAR) | (1L << PLUS))) != 0)) {
					{
					setState(378); ebnfSuffix();
					}
				}

				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(381); match(ACTION);
				setState(383);
				_la = _input.LA(1);
				if (_la==QUESTION) {
					{
					setState(382); match(QUESTION);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabeledLexerElementContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(ANTLRv4Parser.ASSIGN, 0); }
		public TerminalNode PLUS_ASSIGN() { return getToken(ANTLRv4Parser.PLUS_ASSIGN, 0); }
		public LexerAtomContext lexerAtom() {
			return getRuleContext(LexerAtomContext.class,0);
		}
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public LabeledLexerElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledLexerElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLabeledLexerElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLabeledLexerElement(this);
		}
	}

	public final LabeledLexerElementContext labeledLexerElement() throws RecognitionException {
		LabeledLexerElementContext _localctx = new LabeledLexerElementContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_labeledLexerElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(387); id();
			setState(388);
			_la = _input.LA(1);
			if ( !(_la==ASSIGN || _la==PLUS_ASSIGN) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(391);
			switch (_input.LA(1)) {
			case TOKEN_REF:
			case RULE_REF:
			case LEXER_CHAR_SET:
			case DOT:
			case NOT:
			case STRING_LITERAL:
				{
				setState(389); lexerAtom();
				}
				break;
			case LPAREN:
				{
				setState(390); block();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerBlockContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(ANTLRv4Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(ANTLRv4Parser.RPAREN, 0); }
		public LexerAltListContext lexerAltList() {
			return getRuleContext(LexerAltListContext.class,0);
		}
		public LexerBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerBlock(this);
		}
	}

	public final LexerBlockContext lexerBlock() throws RecognitionException {
		LexerBlockContext _localctx = new LexerBlockContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_lexerBlock);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(393); match(LPAREN);
			setState(394); lexerAltList();
			setState(395); match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerCommandsContext extends ParserRuleContext {
		public List<LexerCommandContext> lexerCommand() {
			return getRuleContexts(LexerCommandContext.class);
		}
		public LexerCommandContext lexerCommand(int i) {
			return getRuleContext(LexerCommandContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(ANTLRv4Parser.COMMA); }
		public TerminalNode RARROW() { return getToken(ANTLRv4Parser.RARROW, 0); }
		public TerminalNode COMMA(int i) {
			return getToken(ANTLRv4Parser.COMMA, i);
		}
		public LexerCommandsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerCommands; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerCommands(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerCommands(this);
		}
	}

	public final LexerCommandsContext lexerCommands() throws RecognitionException {
		LexerCommandsContext _localctx = new LexerCommandsContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_lexerCommands);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397); match(RARROW);
			setState(398); lexerCommand();
			setState(403);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(399); match(COMMA);
				setState(400); lexerCommand();
				}
				}
				setState(405);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerCommandContext extends ParserRuleContext {
		public LexerCommandExprContext lexerCommandExpr() {
			return getRuleContext(LexerCommandExprContext.class,0);
		}
		public LexerCommandNameContext lexerCommandName() {
			return getRuleContext(LexerCommandNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(ANTLRv4Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(ANTLRv4Parser.RPAREN, 0); }
		public LexerCommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerCommand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerCommand(this);
		}
	}

	public final LexerCommandContext lexerCommand() throws RecognitionException {
		LexerCommandContext _localctx = new LexerCommandContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_lexerCommand);
		try {
			setState(412);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(406); lexerCommandName();
				setState(407); match(LPAREN);
				setState(408); lexerCommandExpr();
				setState(409); match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(411); lexerCommandName();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerCommandNameContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode MODE() { return getToken(ANTLRv4Parser.MODE, 0); }
		public LexerCommandNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerCommandName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerCommandName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerCommandName(this);
		}
	}

	public final LexerCommandNameContext lexerCommandName() throws RecognitionException {
		LexerCommandNameContext _localctx = new LexerCommandNameContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_lexerCommandName);
		try {
			setState(416);
			switch (_input.LA(1)) {
			case TOKEN_REF:
			case RULE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(414); id();
				}
				break;
			case MODE:
				enterOuterAlt(_localctx, 2);
				{
				setState(415); match(MODE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerCommandExprContext extends ParserRuleContext {
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public TerminalNode INT() { return getToken(ANTLRv4Parser.INT, 0); }
		public LexerCommandExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerCommandExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerCommandExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerCommandExpr(this);
		}
	}

	public final LexerCommandExprContext lexerCommandExpr() throws RecognitionException {
		LexerCommandExprContext _localctx = new LexerCommandExprContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_lexerCommandExpr);
		try {
			setState(420);
			switch (_input.LA(1)) {
			case TOKEN_REF:
			case RULE_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(418); id();
				}
				break;
			case INT:
				enterOuterAlt(_localctx, 2);
				{
				setState(419); match(INT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AltListContext extends ParserRuleContext {
		public AlternativeContext alternative(int i) {
			return getRuleContext(AlternativeContext.class,i);
		}
		public List<AlternativeContext> alternative() {
			return getRuleContexts(AlternativeContext.class);
		}
		public List<TerminalNode> OR() { return getTokens(ANTLRv4Parser.OR); }
		public TerminalNode OR(int i) {
			return getToken(ANTLRv4Parser.OR, i);
		}
		public AltListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_altList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterAltList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitAltList(this);
		}
	}

	public final AltListContext altList() throws RecognitionException {
		AltListContext _localctx = new AltListContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_altList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(422); alternative();
			setState(427);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(423); match(OR);
				setState(424); alternative();
				}
				}
				setState(429);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AlternativeContext extends ParserRuleContext {
		public ElementOptionsContext elementOptions() {
			return getRuleContext(ElementOptionsContext.class,0);
		}
		public List<ElementContext> element() {
			return getRuleContexts(ElementContext.class);
		}
		public ElementContext element(int i) {
			return getRuleContext(ElementContext.class,i);
		}
		public AlternativeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_alternative; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterAlternative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitAlternative(this);
		}
	}

	public final AlternativeContext alternative() throws RecognitionException {
		AlternativeContext _localctx = new AlternativeContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_alternative);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(430); elementOptions();
				}
			}

			setState(436);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOKEN_REF) | (1L << RULE_REF) | (1L << LPAREN) | (1L << DOT) | (1L << NOT) | (1L << STRING_LITERAL) | (1L << ACTION))) != 0)) {
				{
				{
				setState(433); element();
				}
				}
				setState(438);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElementContext extends ParserRuleContext {
		public EbnfSuffixContext ebnfSuffix() {
			return getRuleContext(EbnfSuffixContext.class,0);
		}
		public TerminalNode ACTION() { return getToken(ANTLRv4Parser.ACTION, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public LabeledElementContext labeledElement() {
			return getRuleContext(LabeledElementContext.class,0);
		}
		public TerminalNode QUESTION() { return getToken(ANTLRv4Parser.QUESTION, 0); }
		public EbnfContext ebnf() {
			return getRuleContext(EbnfContext.class,0);
		}
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitElement(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_element);
		int _la;
		try {
			setState(454);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(439); labeledElement();
				setState(442);
				switch (_input.LA(1)) {
				case QUESTION:
				case STAR:
				case PLUS:
					{
					setState(440); ebnfSuffix();
					}
					break;
				case TOKEN_REF:
				case RULE_REF:
				case SEMI:
				case LPAREN:
				case RPAREN:
				case OR:
				case DOT:
				case POUND:
				case NOT:
				case STRING_LITERAL:
				case ACTION:
					{
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(444); atom();
				setState(447);
				switch (_input.LA(1)) {
				case QUESTION:
				case STAR:
				case PLUS:
					{
					setState(445); ebnfSuffix();
					}
					break;
				case TOKEN_REF:
				case RULE_REF:
				case SEMI:
				case LPAREN:
				case RPAREN:
				case OR:
				case DOT:
				case POUND:
				case NOT:
				case STRING_LITERAL:
				case ACTION:
					{
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(449); ebnf();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(450); match(ACTION);
				setState(452);
				_la = _input.LA(1);
				if (_la==QUESTION) {
					{
					setState(451); match(QUESTION);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabeledElementContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(ANTLRv4Parser.ASSIGN, 0); }
		public TerminalNode PLUS_ASSIGN() { return getToken(ANTLRv4Parser.PLUS_ASSIGN, 0); }
		public IdContext id() {
			return getRuleContext(IdContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public LabeledElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLabeledElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLabeledElement(this);
		}
	}

	public final LabeledElementContext labeledElement() throws RecognitionException {
		LabeledElementContext _localctx = new LabeledElementContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_labeledElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(456); id();
			setState(457);
			_la = _input.LA(1);
			if ( !(_la==ASSIGN || _la==PLUS_ASSIGN) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(460);
			switch (_input.LA(1)) {
			case TOKEN_REF:
			case RULE_REF:
			case DOT:
			case NOT:
			case STRING_LITERAL:
				{
				setState(458); atom();
				}
				break;
			case LPAREN:
				{
				setState(459); block();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EbnfContext extends ParserRuleContext {
		public BlockSuffixContext blockSuffix() {
			return getRuleContext(BlockSuffixContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public EbnfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ebnf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterEbnf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitEbnf(this);
		}
	}

	public final EbnfContext ebnf() throws RecognitionException {
		EbnfContext _localctx = new EbnfContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_ebnf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(462); block();
			setState(464);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << QUESTION) | (1L << STAR) | (1L << PLUS))) != 0)) {
				{
				setState(463); blockSuffix();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockSuffixContext extends ParserRuleContext {
		public EbnfSuffixContext ebnfSuffix() {
			return getRuleContext(EbnfSuffixContext.class,0);
		}
		public BlockSuffixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockSuffix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterBlockSuffix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitBlockSuffix(this);
		}
	}

	public final BlockSuffixContext blockSuffix() throws RecognitionException {
		BlockSuffixContext _localctx = new BlockSuffixContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_blockSuffix);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466); ebnfSuffix();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EbnfSuffixContext extends ParserRuleContext {
		public TerminalNode QUESTION(int i) {
			return getToken(ANTLRv4Parser.QUESTION, i);
		}
		public TerminalNode PLUS() { return getToken(ANTLRv4Parser.PLUS, 0); }
		public TerminalNode STAR() { return getToken(ANTLRv4Parser.STAR, 0); }
		public List<TerminalNode> QUESTION() { return getTokens(ANTLRv4Parser.QUESTION); }
		public EbnfSuffixContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ebnfSuffix; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterEbnfSuffix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitEbnfSuffix(this);
		}
	}

	public final EbnfSuffixContext ebnfSuffix() throws RecognitionException {
		EbnfSuffixContext _localctx = new EbnfSuffixContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_ebnfSuffix);
		int _la;
		try {
			setState(480);
			switch (_input.LA(1)) {
			case QUESTION:
				enterOuterAlt(_localctx, 1);
				{
				setState(468); match(QUESTION);
				setState(470);
				_la = _input.LA(1);
				if (_la==QUESTION) {
					{
					setState(469); match(QUESTION);
					}
				}

				}
				break;
			case STAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(472); match(STAR);
				setState(474);
				_la = _input.LA(1);
				if (_la==QUESTION) {
					{
					setState(473); match(QUESTION);
					}
				}

				}
				break;
			case PLUS:
				enterOuterAlt(_localctx, 3);
				{
				setState(476); match(PLUS);
				setState(478);
				_la = _input.LA(1);
				if (_la==QUESTION) {
					{
					setState(477); match(QUESTION);
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LexerAtomContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(ANTLRv4Parser.DOT, 0); }
		public ElementOptionsContext elementOptions() {
			return getRuleContext(ElementOptionsContext.class,0);
		}
		public TerminalNode RULE_REF() { return getToken(ANTLRv4Parser.RULE_REF, 0); }
		public TerminalContext terminal() {
			return getRuleContext(TerminalContext.class,0);
		}
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public TerminalNode LEXER_CHAR_SET() { return getToken(ANTLRv4Parser.LEXER_CHAR_SET, 0); }
		public NotSetContext notSet() {
			return getRuleContext(NotSetContext.class,0);
		}
		public LexerAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lexerAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterLexerAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitLexerAtom(this);
		}
	}

	public final LexerAtomContext lexerAtom() throws RecognitionException {
		LexerAtomContext _localctx = new LexerAtomContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_lexerAtom);
		int _la;
		try {
			setState(491);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(482); range();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(483); terminal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(484); match(RULE_REF);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(485); notSet();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(486); match(LEXER_CHAR_SET);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(487); match(DOT);
				setState(489);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(488); elementOptions();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public RulerefContext ruleref() {
			return getRuleContext(RulerefContext.class,0);
		}
		public TerminalNode DOT() { return getToken(ANTLRv4Parser.DOT, 0); }
		public ElementOptionsContext elementOptions() {
			return getRuleContext(ElementOptionsContext.class,0);
		}
		public TerminalContext terminal() {
			return getRuleContext(TerminalContext.class,0);
		}
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public NotSetContext notSet() {
			return getRuleContext(NotSetContext.class,0);
		}
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_atom);
		int _la;
		try {
			setState(501);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(493); range();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(494); terminal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(495); ruleref();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(496); notSet();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(497); match(DOT);
				setState(499);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(498); elementOptions();
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NotSetContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(ANTLRv4Parser.NOT, 0); }
		public SetElementContext setElement() {
			return getRuleContext(SetElementContext.class,0);
		}
		public BlockSetContext blockSet() {
			return getRuleContext(BlockSetContext.class,0);
		}
		public NotSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_notSet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterNotSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitNotSet(this);
		}
	}

	public final NotSetContext notSet() throws RecognitionException {
		NotSetContext _localctx = new NotSetContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_notSet);
		try {
			setState(507);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(503); match(NOT);
				setState(504); setElement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(505); match(NOT);
				setState(506); blockSet();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockSetContext extends ParserRuleContext {
		public SetElementContext setElement(int i) {
			return getRuleContext(SetElementContext.class,i);
		}
		public List<SetElementContext> setElement() {
			return getRuleContexts(SetElementContext.class);
		}
		public TerminalNode LPAREN() { return getToken(ANTLRv4Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(ANTLRv4Parser.RPAREN, 0); }
		public List<TerminalNode> OR() { return getTokens(ANTLRv4Parser.OR); }
		public TerminalNode OR(int i) {
			return getToken(ANTLRv4Parser.OR, i);
		}
		public BlockSetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockSet; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterBlockSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitBlockSet(this);
		}
	}

	public final BlockSetContext blockSet() throws RecognitionException {
		BlockSetContext _localctx = new BlockSetContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_blockSet);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509); match(LPAREN);
			setState(510); setElement();
			setState(515);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(511); match(OR);
				setState(512); setElement();
				}
				}
				setState(517);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(518); match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetElementContext extends ParserRuleContext {
		public ElementOptionsContext elementOptions() {
			return getRuleContext(ElementOptionsContext.class,0);
		}
		public TerminalNode TOKEN_REF() { return getToken(ANTLRv4Parser.TOKEN_REF, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(ANTLRv4Parser.STRING_LITERAL, 0); }
		public RangeContext range() {
			return getRuleContext(RangeContext.class,0);
		}
		public TerminalNode LEXER_CHAR_SET() { return getToken(ANTLRv4Parser.LEXER_CHAR_SET, 0); }
		public SetElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterSetElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitSetElement(this);
		}
	}

	public final SetElementContext setElement() throws RecognitionException {
		SetElementContext _localctx = new SetElementContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_setElement);
		int _la;
		try {
			setState(530);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(520); match(TOKEN_REF);
				setState(522);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(521); elementOptions();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(524); match(STRING_LITERAL);
				setState(526);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(525); elementOptions();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(528); range();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(529); match(LEXER_CHAR_SET);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public AltListContext altList() {
			return getRuleContext(AltListContext.class,0);
		}
		public RuleActionContext ruleAction(int i) {
			return getRuleContext(RuleActionContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(ANTLRv4Parser.LPAREN, 0); }
		public TerminalNode COLON() { return getToken(ANTLRv4Parser.COLON, 0); }
		public List<RuleActionContext> ruleAction() {
			return getRuleContexts(RuleActionContext.class);
		}
		public TerminalNode RPAREN() { return getToken(ANTLRv4Parser.RPAREN, 0); }
		public OptionsSpecContext optionsSpec() {
			return getRuleContext(OptionsSpecContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(532); match(LPAREN);
			setState(543);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OPTIONS) | (1L << COLON) | (1L << AT))) != 0)) {
				{
				setState(534);
				_la = _input.LA(1);
				if (_la==OPTIONS) {
					{
					setState(533); optionsSpec();
					}
				}

				setState(539);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(536); ruleAction();
					}
					}
					setState(541);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(542); match(COLON);
				}
			}

			setState(545); altList();
			setState(546); match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RulerefContext extends ParserRuleContext {
		public TerminalNode ARG_ACTION() { return getToken(ANTLRv4Parser.ARG_ACTION, 0); }
		public ElementOptionsContext elementOptions() {
			return getRuleContext(ElementOptionsContext.class,0);
		}
		public TerminalNode RULE_REF() { return getToken(ANTLRv4Parser.RULE_REF, 0); }
		public RulerefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ruleref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRuleref(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRuleref(this);
		}
	}

	public final RulerefContext ruleref() throws RecognitionException {
		RulerefContext _localctx = new RulerefContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_ruleref);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(548); match(RULE_REF);
			setState(550);
			_la = _input.LA(1);
			if (_la==ARG_ACTION) {
				{
				setState(549); match(ARG_ACTION);
				}
			}

			setState(553);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(552); elementOptions();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RangeContext extends ParserRuleContext {
		public TerminalNode RANGE() { return getToken(ANTLRv4Parser.RANGE, 0); }
		public List<TerminalNode> STRING_LITERAL() { return getTokens(ANTLRv4Parser.STRING_LITERAL); }
		public TerminalNode STRING_LITERAL(int i) {
			return getToken(ANTLRv4Parser.STRING_LITERAL, i);
		}
		public RangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitRange(this);
		}
	}

	public final RangeContext range() throws RecognitionException {
		RangeContext _localctx = new RangeContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_range);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(555); match(STRING_LITERAL);
			setState(556); match(RANGE);
			setState(557); match(STRING_LITERAL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TerminalContext extends ParserRuleContext {
		public ElementOptionsContext elementOptions() {
			return getRuleContext(ElementOptionsContext.class,0);
		}
		public TerminalNode TOKEN_REF() { return getToken(ANTLRv4Parser.TOKEN_REF, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(ANTLRv4Parser.STRING_LITERAL, 0); }
		public TerminalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terminal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterTerminal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitTerminal(this);
		}
	}

	public final TerminalContext terminal() throws RecognitionException {
		TerminalContext _localctx = new TerminalContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_terminal);
		int _la;
		try {
			setState(567);
			switch (_input.LA(1)) {
			case TOKEN_REF:
				enterOuterAlt(_localctx, 1);
				{
				setState(559); match(TOKEN_REF);
				setState(561);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(560); elementOptions();
					}
				}

				}
				break;
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(563); match(STRING_LITERAL);
				setState(565);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(564); elementOptions();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElementOptionsContext extends ParserRuleContext {
		public List<TerminalNode> COMMA() { return getTokens(ANTLRv4Parser.COMMA); }
		public TerminalNode LT() { return getToken(ANTLRv4Parser.LT, 0); }
		public TerminalNode GT() { return getToken(ANTLRv4Parser.GT, 0); }
		public List<ElementOptionContext> elementOption() {
			return getRuleContexts(ElementOptionContext.class);
		}
		public ElementOptionContext elementOption(int i) {
			return getRuleContext(ElementOptionContext.class,i);
		}
		public TerminalNode COMMA(int i) {
			return getToken(ANTLRv4Parser.COMMA, i);
		}
		public ElementOptionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementOptions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterElementOptions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitElementOptions(this);
		}
	}

	public final ElementOptionsContext elementOptions() throws RecognitionException {
		ElementOptionsContext _localctx = new ElementOptionsContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_elementOptions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(569); match(LT);
			setState(570); elementOption();
			setState(575);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(571); match(COMMA);
				setState(572); elementOption();
				}
				}
				setState(577);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(578); match(GT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElementOptionContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(ANTLRv4Parser.ASSIGN, 0); }
		public IdContext id(int i) {
			return getRuleContext(IdContext.class,i);
		}
		public TerminalNode STRING_LITERAL() { return getToken(ANTLRv4Parser.STRING_LITERAL, 0); }
		public List<IdContext> id() {
			return getRuleContexts(IdContext.class);
		}
		public ElementOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementOption; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterElementOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitElementOption(this);
		}
	}

	public final ElementOptionContext elementOption() throws RecognitionException {
		ElementOptionContext _localctx = new ElementOptionContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_elementOption);
		try {
			setState(587);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(580); id();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(581); id();
				setState(582); match(ASSIGN);
				setState(585);
				switch (_input.LA(1)) {
				case TOKEN_REF:
				case RULE_REF:
					{
					setState(583); id();
					}
					break;
				case STRING_LITERAL:
					{
					setState(584); match(STRING_LITERAL);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdContext extends ParserRuleContext {
		public TerminalNode TOKEN_REF() { return getToken(ANTLRv4Parser.TOKEN_REF, 0); }
		public TerminalNode RULE_REF() { return getToken(ANTLRv4Parser.RULE_REF, 0); }
		public IdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_id; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).enterId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4ParserListener ) ((ANTLRv4ParserListener)listener).exitId(this);
		}
	}

	public final IdContext id() throws RecognitionException {
		IdContext _localctx = new IdContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_id);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(589);
			_la = _input.LA(1);
			if ( !(_la==TOKEN_REF || _la==RULE_REF) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\39\u0252\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\3\2\5"+
		"\2z\n\2\3\2\3\2\3\2\3\2\7\2\u0080\n\2\f\2\16\2\u0083\13\2\3\2\3\2\7\2"+
		"\u0087\n\2\f\2\16\2\u008a\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\5\3\u0093\n"+
		"\3\3\4\3\4\3\4\3\4\5\4\u0099\n\4\3\5\3\5\3\5\3\5\7\5\u009f\n\5\f\5\16"+
		"\5\u00a2\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\7\7\u00ad\n\7\f\7\16"+
		"\7\u00b0\13\7\3\7\3\7\3\7\5\7\u00b5\n\7\3\b\3\b\3\b\3\b\7\b\u00bb\n\b"+
		"\f\b\16\b\u00be\13\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\5\t\u00c7\n\t\3\n\3\n"+
		"\3\n\3\n\7\n\u00cd\n\n\f\n\16\n\u00d0\13\n\3\n\5\n\u00d3\n\n\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\5\13\u00db\n\13\3\13\3\13\3\13\3\f\3\f\3\f\5\f\u00e3"+
		"\n\f\3\r\3\r\3\r\3\r\7\r\u00e9\n\r\f\r\16\r\u00ec\13\r\3\16\7\16\u00ef"+
		"\n\16\f\16\16\16\u00f2\13\16\3\17\3\17\5\17\u00f6\n\17\3\20\5\20\u00f9"+
		"\n\20\3\20\5\20\u00fc\n\20\3\20\3\20\5\20\u0100\n\20\3\20\5\20\u0103\n"+
		"\20\3\20\5\20\u0106\n\20\3\20\5\20\u0109\n\20\3\20\7\20\u010c\n\20\f\20"+
		"\16\20\u010f\13\20\3\20\3\20\3\20\3\20\3\20\3\21\7\21\u0117\n\21\f\21"+
		"\16\21\u011a\13\21\3\21\5\21\u011d\n\21\3\22\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\24\3\24\5\24\u0128\n\24\3\25\3\25\3\25\3\26\3\26\3\26\3\26\7\26"+
		"\u0131\n\26\f\26\16\26\u0134\13\26\3\27\3\27\3\27\3\30\3\30\3\30\3\30"+
		"\3\31\6\31\u013e\n\31\r\31\16\31\u013f\3\32\3\32\3\33\3\33\3\34\3\34\3"+
		"\34\7\34\u0149\n\34\f\34\16\34\u014c\13\34\3\35\3\35\3\35\5\35\u0151\n"+
		"\35\3\36\5\36\u0154\n\36\3\36\5\36\u0157\n\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\37\3\37\3 \3 \3 \7 \u0163\n \f \16 \u0166\13 \3!\3!\5!\u016a\n!\3!"+
		"\5!\u016d\n!\3\"\6\"\u0170\n\"\r\"\16\"\u0171\3#\3#\5#\u0176\n#\3#\3#"+
		"\5#\u017a\n#\3#\3#\5#\u017e\n#\3#\3#\5#\u0182\n#\5#\u0184\n#\3$\3$\3$"+
		"\3$\5$\u018a\n$\3%\3%\3%\3%\3&\3&\3&\3&\7&\u0194\n&\f&\16&\u0197\13&\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\5\'\u019f\n\'\3(\3(\5(\u01a3\n(\3)\3)\5)\u01a7"+
		"\n)\3*\3*\3*\7*\u01ac\n*\f*\16*\u01af\13*\3+\5+\u01b2\n+\3+\7+\u01b5\n"+
		"+\f+\16+\u01b8\13+\3,\3,\3,\5,\u01bd\n,\3,\3,\3,\5,\u01c2\n,\3,\3,\3,"+
		"\5,\u01c7\n,\5,\u01c9\n,\3-\3-\3-\3-\5-\u01cf\n-\3.\3.\5.\u01d3\n.\3/"+
		"\3/\3\60\3\60\5\60\u01d9\n\60\3\60\3\60\5\60\u01dd\n\60\3\60\3\60\5\60"+
		"\u01e1\n\60\5\60\u01e3\n\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\5\61\u01ec"+
		"\n\61\5\61\u01ee\n\61\3\62\3\62\3\62\3\62\3\62\3\62\5\62\u01f6\n\62\5"+
		"\62\u01f8\n\62\3\63\3\63\3\63\3\63\5\63\u01fe\n\63\3\64\3\64\3\64\3\64"+
		"\7\64\u0204\n\64\f\64\16\64\u0207\13\64\3\64\3\64\3\65\3\65\5\65\u020d"+
		"\n\65\3\65\3\65\5\65\u0211\n\65\3\65\3\65\5\65\u0215\n\65\3\66\3\66\5"+
		"\66\u0219\n\66\3\66\7\66\u021c\n\66\f\66\16\66\u021f\13\66\3\66\5\66\u0222"+
		"\n\66\3\66\3\66\3\66\3\67\3\67\5\67\u0229\n\67\3\67\5\67\u022c\n\67\3"+
		"8\38\38\38\39\39\59\u0234\n9\39\39\59\u0238\n9\59\u023a\n9\3:\3:\3:\3"+
		":\7:\u0240\n:\f:\16:\u0243\13:\3:\3:\3;\3;\3;\3;\3;\5;\u024c\n;\5;\u024e"+
		"\n;\3<\3<\3<\2\2=\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62"+
		"\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtv\2\5\4\2\r\r\21\23\4\2##\'\'\3"+
		"\2\3\4\u027a\2y\3\2\2\2\4\u0092\3\2\2\2\6\u0098\3\2\2\2\b\u009a\3\2\2"+
		"\2\n\u00a5\3\2\2\2\f\u00b4\3\2\2\2\16\u00b6\3\2\2\2\20\u00c6\3\2\2\2\22"+
		"\u00c8\3\2\2\2\24\u00d6\3\2\2\2\26\u00e2\3\2\2\2\30\u00e4\3\2\2\2\32\u00f0"+
		"\3\2\2\2\34\u00f5\3\2\2\2\36\u00f8\3\2\2\2 \u0118\3\2\2\2\"\u011e\3\2"+
		"\2\2$\u0122\3\2\2\2&\u0127\3\2\2\2(\u0129\3\2\2\2*\u012c\3\2\2\2,\u0135"+
		"\3\2\2\2.\u0138\3\2\2\2\60\u013d\3\2\2\2\62\u0141\3\2\2\2\64\u0143\3\2"+
		"\2\2\66\u0145\3\2\2\28\u014d\3\2\2\2:\u0153\3\2\2\2<\u015d\3\2\2\2>\u015f"+
		"\3\2\2\2@\u016c\3\2\2\2B\u016f\3\2\2\2D\u0183\3\2\2\2F\u0185\3\2\2\2H"+
		"\u018b\3\2\2\2J\u018f\3\2\2\2L\u019e\3\2\2\2N\u01a2\3\2\2\2P\u01a6\3\2"+
		"\2\2R\u01a8\3\2\2\2T\u01b1\3\2\2\2V\u01c8\3\2\2\2X\u01ca\3\2\2\2Z\u01d0"+
		"\3\2\2\2\\\u01d4\3\2\2\2^\u01e2\3\2\2\2`\u01ed\3\2\2\2b\u01f7\3\2\2\2"+
		"d\u01fd\3\2\2\2f\u01ff\3\2\2\2h\u0214\3\2\2\2j\u0216\3\2\2\2l\u0226\3"+
		"\2\2\2n\u022d\3\2\2\2p\u0239\3\2\2\2r\u023b\3\2\2\2t\u024d\3\2\2\2v\u024f"+
		"\3\2\2\2xz\7\6\2\2yx\3\2\2\2yz\3\2\2\2z{\3\2\2\2{|\5\4\3\2|}\5v<\2}\u0081"+
		"\7\35\2\2~\u0080\5\6\4\2\177~\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3"+
		"\2\2\2\u0081\u0082\3\2\2\2\u0082\u0084\3\2\2\2\u0083\u0081\3\2\2\2\u0084"+
		"\u0088\5\32\16\2\u0085\u0087\5\30\r\2\u0086\u0085\3\2\2\2\u0087\u008a"+
		"\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008b\3\2\2\2\u008a"+
		"\u0088\3\2\2\2\u008b\u008c\7\2\2\3\u008c\3\3\2\2\2\u008d\u008e\7\16\2"+
		"\2\u008e\u0093\7\20\2\2\u008f\u0090\7\17\2\2\u0090\u0093\7\20\2\2\u0091"+
		"\u0093\7\20\2\2\u0092\u008d\3\2\2\2\u0092\u008f\3\2\2\2\u0092\u0091\3"+
		"\2\2\2\u0093\5\3\2\2\2\u0094\u0099\5\b\5\2\u0095\u0099\5\16\b\2\u0096"+
		"\u0099\5\22\n\2\u0097\u0099\5\24\13\2\u0098\u0094\3\2\2\2\u0098\u0095"+
		"\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0097\3\2\2\2\u0099\7\3\2\2\2\u009a"+
		"\u00a0\7\n\2\2\u009b\u009c\5\n\6\2\u009c\u009d\7\35\2\2\u009d\u009f\3"+
		"\2\2\2\u009e\u009b\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0"+
		"\u00a1\3\2\2\2\u00a1\u00a3\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a3\u00a4\7/"+
		"\2\2\u00a4\t\3\2\2\2\u00a5\u00a6\5v<\2\u00a6\u00a7\7#\2\2\u00a7\u00a8"+
		"\5\f\7\2\u00a8\13\3\2\2\2\u00a9\u00ae\5v<\2\u00aa\u00ab\7*\2\2\u00ab\u00ad"+
		"\5v<\2\u00ac\u00aa\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae"+
		"\u00af\3\2\2\2\u00af\u00b5\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b5\7\62"+
		"\2\2\u00b2\u00b5\7\65\2\2\u00b3\u00b5\7\61\2\2\u00b4\u00a9\3\2\2\2\u00b4"+
		"\u00b1\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b3\3\2\2\2\u00b5\r\3\2\2\2"+
		"\u00b6\u00b7\7\f\2\2\u00b7\u00bc\5\20\t\2\u00b8\u00b9\7\34\2\2\u00b9\u00bb"+
		"\5\20\t\2\u00ba\u00b8\3\2\2\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2"+
		"\u00bc\u00bd\3\2\2\2\u00bd\u00bf\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf\u00c0"+
		"\7\35\2\2\u00c0\17\3\2\2\2\u00c1\u00c2\5v<\2\u00c2\u00c3\7#\2\2\u00c3"+
		"\u00c4\5v<\2\u00c4\u00c7\3\2\2\2\u00c5\u00c7\5v<\2\u00c6\u00c1\3\2\2\2"+
		"\u00c6\u00c5\3\2\2\2\u00c7\21\3\2\2\2\u00c8\u00c9\7\13\2\2\u00c9\u00ce"+
		"\5v<\2\u00ca\u00cb\7\34\2\2\u00cb\u00cd\5v<\2\u00cc\u00ca\3\2\2\2\u00cd"+
		"\u00d0\3\2\2\2\u00ce\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d2\3\2"+
		"\2\2\u00d0\u00ce\3\2\2\2\u00d1\u00d3\7\34\2\2\u00d2\u00d1\3\2\2\2\u00d2"+
		"\u00d3\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d5\7/\2\2\u00d5\23\3\2\2\2"+
		"\u00d6\u00da\7,\2\2\u00d7\u00d8\5\26\f\2\u00d8\u00d9\7\33\2\2\u00d9\u00db"+
		"\3\2\2\2\u00da\u00d7\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc"+
		"\u00dd\5v<\2\u00dd\u00de\7\65\2\2\u00de\25\3\2\2\2\u00df\u00e3\5v<\2\u00e0"+
		"\u00e3\7\16\2\2\u00e1\u00e3\7\17\2\2\u00e2\u00df\3\2\2\2\u00e2\u00e0\3"+
		"\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\27\3\2\2\2\u00e4\u00e5\7\31\2\2\u00e5"+
		"\u00e6\5v<\2\u00e6\u00ea\7\35\2\2\u00e7\u00e9\5:\36\2\u00e8\u00e7\3\2"+
		"\2\2\u00e9\u00ec\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb"+
		"\31\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ed\u00ef\5\34\17\2\u00ee\u00ed\3\2"+
		"\2\2\u00ef\u00f2\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1"+
		"\33\3\2\2\2\u00f2\u00f0\3\2\2\2\u00f3\u00f6\5\36\20\2\u00f4\u00f6\5:\36"+
		"\2\u00f5\u00f3\3\2\2\2\u00f5\u00f4\3\2\2\2\u00f6\35\3\2\2\2\u00f7\u00f9"+
		"\7\6\2\2\u00f8\u00f7\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb\3\2\2\2\u00fa"+
		"\u00fc\5\60\31\2\u00fb\u00fa\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\u00fd\3"+
		"\2\2\2\u00fd\u00ff\7\4\2\2\u00fe\u0100\7\67\2\2\u00ff\u00fe\3\2\2\2\u00ff"+
		"\u0100\3\2\2\2\u0100\u0102\3\2\2\2\u0101\u0103\5(\25\2\u0102\u0101\3\2"+
		"\2\2\u0102\u0103\3\2\2\2\u0103\u0105\3\2\2\2\u0104\u0106\5*\26\2\u0105"+
		"\u0104\3\2\2\2\u0105\u0106\3\2\2\2\u0106\u0108\3\2\2\2\u0107\u0109\5,"+
		"\27\2\u0108\u0107\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010d\3\2\2\2\u010a"+
		"\u010c\5&\24\2\u010b\u010a\3\2\2\2\u010c\u010f\3\2\2\2\u010d\u010b\3\2"+
		"\2\2\u010d\u010e\3\2\2\2\u010e\u0110\3\2\2\2\u010f\u010d\3\2\2\2\u0110"+
		"\u0111\7\32\2\2\u0111\u0112\5\64\33\2\u0112\u0113\7\35\2\2\u0113\u0114"+
		"\5 \21\2\u0114\37\3\2\2\2\u0115\u0117\5\"\22\2\u0116\u0115\3\2\2\2\u0117"+
		"\u011a\3\2\2\2\u0118\u0116\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011c\3\2"+
		"\2\2\u011a\u0118\3\2\2\2\u011b\u011d\5$\23\2\u011c\u011b\3\2\2\2\u011c"+
		"\u011d\3\2\2\2\u011d!\3\2\2\2\u011e\u011f\7\27\2\2\u011f\u0120\7\67\2"+
		"\2\u0120\u0121\7\65\2\2\u0121#\3\2\2\2\u0122\u0123\7\30\2\2\u0123\u0124"+
		"\7\65\2\2\u0124%\3\2\2\2\u0125\u0128\5\b\5\2\u0126\u0128\5.\30\2\u0127"+
		"\u0125\3\2\2\2\u0127\u0126\3\2\2\2\u0128\'\3\2\2\2\u0129\u012a\7\24\2"+
		"\2\u012a\u012b\7\67\2\2\u012b)\3\2\2\2\u012c\u012d\7\26\2\2\u012d\u0132"+
		"\5v<\2\u012e\u012f\7\34\2\2\u012f\u0131\5v<\2\u0130\u012e\3\2\2\2\u0131"+
		"\u0134\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133+\3\2\2\2"+
		"\u0134\u0132\3\2\2\2\u0135\u0136\7\25\2\2\u0136\u0137\7\67\2\2\u0137-"+
		"\3\2\2\2\u0138\u0139\7,\2\2\u0139\u013a\5v<\2\u013a\u013b\7\65\2\2\u013b"+
		"/\3\2\2\2\u013c\u013e\5\62\32\2\u013d\u013c\3\2\2\2\u013e\u013f\3\2\2"+
		"\2\u013f\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140\61\3\2\2\2\u0141\u0142"+
		"\t\2\2\2\u0142\63\3\2\2\2\u0143\u0144\5\66\34\2\u0144\65\3\2\2\2\u0145"+
		"\u014a\58\35\2\u0146\u0147\7(\2\2\u0147\u0149\58\35\2\u0148\u0146\3\2"+
		"\2\2\u0149\u014c\3\2\2\2\u014a\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b"+
		"\67\3\2\2\2\u014c\u014a\3\2\2\2\u014d\u0150\5T+\2\u014e\u014f\7-\2\2\u014f"+
		"\u0151\5v<\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u01519\3\2\2\2\u0152"+
		"\u0154\7\6\2\2\u0153\u0152\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0156\3\2"+
		"\2\2\u0155\u0157\7\r\2\2\u0156\u0155\3\2\2\2\u0156\u0157\3\2\2\2\u0157"+
		"\u0158\3\2\2\2\u0158\u0159\7\3\2\2\u0159\u015a\7\32\2\2\u015a\u015b\5"+
		"<\37\2\u015b\u015c\7\35\2\2\u015c;\3\2\2\2\u015d\u015e\5> \2\u015e=\3"+
		"\2\2\2\u015f\u0164\5@!\2\u0160\u0161\7(\2\2\u0161\u0163\5@!\2\u0162\u0160"+
		"\3\2\2\2\u0163\u0166\3\2\2\2\u0164\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165"+
		"?\3\2\2\2\u0166\u0164\3\2\2\2\u0167\u0169\5B\"\2\u0168\u016a\5J&\2\u0169"+
		"\u0168\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016d\3\2\2\2\u016b\u016d\3\2"+
		"\2\2\u016c\u0167\3\2\2\2\u016c\u016b\3\2\2\2\u016dA\3\2\2\2\u016e\u0170"+
		"\5D#\2\u016f\u016e\3\2\2\2\u0170\u0171\3\2\2\2\u0171\u016f\3\2\2\2\u0171"+
		"\u0172\3\2\2\2\u0172C\3\2\2\2\u0173\u0175\5F$\2\u0174\u0176\5^\60\2\u0175"+
		"\u0174\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0184\3\2\2\2\u0177\u0179\5`"+
		"\61\2\u0178\u017a\5^\60\2\u0179\u0178\3\2\2\2\u0179\u017a\3\2\2\2\u017a"+
		"\u0184\3\2\2\2\u017b\u017d\5H%\2\u017c\u017e\5^\60\2\u017d\u017c\3\2\2"+
		"\2\u017d\u017e\3\2\2\2\u017e\u0184\3\2\2\2\u017f\u0181\7\65\2\2\u0180"+
		"\u0182\7$\2\2\u0181\u0180\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0184\3\2"+
		"\2\2\u0183\u0173\3\2\2\2\u0183\u0177\3\2\2\2\u0183\u017b\3\2\2\2\u0183"+
		"\u017f\3\2\2\2\u0184E\3\2\2\2\u0185\u0186\5v<\2\u0186\u0189\t\3\2\2\u0187"+
		"\u018a\5`\61\2\u0188\u018a\5j\66\2\u0189\u0187\3\2\2\2\u0189\u0188\3\2"+
		"\2\2\u018aG\3\2\2\2\u018b\u018c\7\36\2\2\u018c\u018d\5> \2\u018d\u018e"+
		"\7\37\2\2\u018eI\3\2\2\2\u018f\u0190\7 \2\2\u0190\u0195\5L\'\2\u0191\u0192"+
		"\7\34\2\2\u0192\u0194\5L\'\2\u0193\u0191\3\2\2\2\u0194\u0197\3\2\2\2\u0195"+
		"\u0193\3\2\2\2\u0195\u0196\3\2\2\2\u0196K\3\2\2\2\u0197\u0195\3\2\2\2"+
		"\u0198\u0199\5N(\2\u0199\u019a\7\36\2\2\u019a\u019b\5P)\2\u019b\u019c"+
		"\7\37\2\2\u019c\u019f\3\2\2\2\u019d\u019f\5N(\2\u019e\u0198\3\2\2\2\u019e"+
		"\u019d\3\2\2\2\u019fM\3\2\2\2\u01a0\u01a3\5v<\2\u01a1\u01a3\7\31\2\2\u01a2"+
		"\u01a0\3\2\2\2\u01a2\u01a1\3\2\2\2\u01a3O\3\2\2\2\u01a4\u01a7\5v<\2\u01a5"+
		"\u01a7\7\61\2\2\u01a6\u01a4\3\2\2\2\u01a6\u01a5\3\2\2\2\u01a7Q\3\2\2\2"+
		"\u01a8\u01ad\5T+\2\u01a9\u01aa\7(\2\2\u01aa\u01ac\5T+\2\u01ab\u01a9\3"+
		"\2\2\2\u01ac\u01af\3\2\2\2\u01ad\u01ab\3\2\2\2\u01ad\u01ae\3\2\2\2\u01ae"+
		"S\3\2\2\2\u01af\u01ad\3\2\2\2\u01b0\u01b2\5r:\2\u01b1\u01b0\3\2\2\2\u01b1"+
		"\u01b2\3\2\2\2\u01b2\u01b6\3\2\2\2\u01b3\u01b5\5V,\2\u01b4\u01b3\3\2\2"+
		"\2\u01b5\u01b8\3\2\2\2\u01b6\u01b4\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7U"+
		"\3\2\2\2\u01b8\u01b6\3\2\2\2\u01b9\u01bc\5X-\2\u01ba\u01bd\5^\60\2\u01bb"+
		"\u01bd\3\2\2\2\u01bc\u01ba\3\2\2\2\u01bc\u01bb\3\2\2\2\u01bd\u01c9\3\2"+
		"\2\2\u01be\u01c1\5b\62\2\u01bf\u01c2\5^\60\2\u01c0\u01c2\3\2\2\2\u01c1"+
		"\u01bf\3\2\2\2\u01c1\u01c0\3\2\2\2\u01c2\u01c9\3\2\2\2\u01c3\u01c9\5Z"+
		".\2\u01c4\u01c6\7\65\2\2\u01c5\u01c7\7$\2\2\u01c6\u01c5\3\2\2\2\u01c6"+
		"\u01c7\3\2\2\2\u01c7\u01c9\3\2\2\2\u01c8\u01b9\3\2\2\2\u01c8\u01be\3\2"+
		"\2\2\u01c8\u01c3\3\2\2\2\u01c8\u01c4\3\2\2\2\u01c9W\3\2\2\2\u01ca\u01cb"+
		"\5v<\2\u01cb\u01ce\t\3\2\2\u01cc\u01cf\5b\62\2\u01cd\u01cf\5j\66\2\u01ce"+
		"\u01cc\3\2\2\2\u01ce\u01cd\3\2\2\2\u01cfY\3\2\2\2\u01d0\u01d2\5j\66\2"+
		"\u01d1\u01d3\5\\/\2\u01d2\u01d1\3\2\2\2\u01d2\u01d3\3\2\2\2\u01d3[\3\2"+
		"\2\2\u01d4\u01d5\5^\60\2\u01d5]\3\2\2\2\u01d6\u01d8\7$\2\2\u01d7\u01d9"+
		"\7$\2\2\u01d8\u01d7\3\2\2\2\u01d8\u01d9\3\2\2\2\u01d9\u01e3\3\2\2\2\u01da"+
		"\u01dc\7%\2\2\u01db\u01dd\7$\2\2\u01dc\u01db\3\2\2\2\u01dc\u01dd\3\2\2"+
		"\2\u01dd\u01e3\3\2\2\2\u01de\u01e0\7&\2\2\u01df\u01e1\7$\2\2\u01e0\u01df"+
		"\3\2\2\2\u01e0\u01e1\3\2\2\2\u01e1\u01e3\3\2\2\2\u01e2\u01d6\3\2\2\2\u01e2"+
		"\u01da\3\2\2\2\u01e2\u01de\3\2\2\2\u01e3_\3\2\2\2\u01e4\u01ee\5n8\2\u01e5"+
		"\u01ee\5p9\2\u01e6\u01ee\7\4\2\2\u01e7\u01ee\5d\63\2\u01e8\u01ee\7\5\2"+
		"\2\u01e9\u01eb\7*\2\2\u01ea\u01ec\5r:\2\u01eb\u01ea\3\2\2\2\u01eb\u01ec"+
		"\3\2\2\2\u01ec\u01ee\3\2\2\2\u01ed\u01e4\3\2\2\2\u01ed\u01e5\3\2\2\2\u01ed"+
		"\u01e6\3\2\2\2\u01ed\u01e7\3\2\2\2\u01ed\u01e8\3\2\2\2\u01ed\u01e9\3\2"+
		"\2\2\u01eea\3\2\2\2\u01ef\u01f8\5n8\2\u01f0\u01f8\5p9\2\u01f1\u01f8\5"+
		"l\67\2\u01f2\u01f8\5d\63\2\u01f3\u01f5\7*\2\2\u01f4\u01f6\5r:\2\u01f5"+
		"\u01f4\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f8\3\2\2\2\u01f7\u01ef\3\2"+
		"\2\2\u01f7\u01f0\3\2\2\2\u01f7\u01f1\3\2\2\2\u01f7\u01f2\3\2\2\2\u01f7"+
		"\u01f3\3\2\2\2\u01f8c\3\2\2\2\u01f9\u01fa\7.\2\2\u01fa\u01fe\5h\65\2\u01fb"+
		"\u01fc\7.\2\2\u01fc\u01fe\5f\64\2\u01fd\u01f9\3\2\2\2\u01fd\u01fb\3\2"+
		"\2\2\u01fee\3\2\2\2\u01ff\u0200\7\36\2\2\u0200\u0205\5h\65\2\u0201\u0202"+
		"\7(\2\2\u0202\u0204\5h\65\2\u0203\u0201\3\2\2\2\u0204\u0207\3\2\2\2\u0205"+
		"\u0203\3\2\2\2\u0205\u0206\3\2\2\2\u0206\u0208\3\2\2\2\u0207\u0205\3\2"+
		"\2\2\u0208\u0209\7\37\2\2\u0209g\3\2\2\2\u020a\u020c\7\3\2\2\u020b\u020d"+
		"\5r:\2\u020c\u020b\3\2\2\2\u020c\u020d\3\2\2\2\u020d\u0215\3\2\2\2\u020e"+
		"\u0210\7\62\2\2\u020f\u0211\5r:\2\u0210\u020f\3\2\2\2\u0210\u0211\3\2"+
		"\2\2\u0211\u0215\3\2\2\2\u0212\u0215\5n8\2\u0213\u0215\7\5\2\2\u0214\u020a"+
		"\3\2\2\2\u0214\u020e\3\2\2\2\u0214\u0212\3\2\2\2\u0214\u0213\3\2\2\2\u0215"+
		"i\3\2\2\2\u0216\u0221\7\36\2\2\u0217\u0219\5\b\5\2\u0218\u0217\3\2\2\2"+
		"\u0218\u0219\3\2\2\2\u0219\u021d\3\2\2\2\u021a\u021c\5.\30\2\u021b\u021a"+
		"\3\2\2\2\u021c\u021f\3\2\2\2\u021d\u021b\3\2\2\2\u021d\u021e\3\2\2\2\u021e"+
		"\u0220\3\2\2\2\u021f\u021d\3\2\2\2\u0220\u0222\7\32\2\2\u0221\u0218\3"+
		"\2\2\2\u0221\u0222\3\2\2\2\u0222\u0223\3\2\2\2\u0223\u0224\5R*\2\u0224"+
		"\u0225\7\37\2\2\u0225k\3\2\2\2\u0226\u0228\7\4\2\2\u0227\u0229\7\67\2"+
		"\2\u0228\u0227\3\2\2\2\u0228\u0229\3\2\2\2\u0229\u022b\3\2\2\2\u022a\u022c"+
		"\5r:\2\u022b\u022a\3\2\2\2\u022b\u022c\3\2\2\2\u022cm\3\2\2\2\u022d\u022e"+
		"\7\62\2\2\u022e\u022f\7+\2\2\u022f\u0230\7\62\2\2\u0230o\3\2\2\2\u0231"+
		"\u0233\7\3\2\2\u0232\u0234\5r:\2\u0233\u0232\3\2\2\2\u0233\u0234\3\2\2"+
		"\2\u0234\u023a\3\2\2\2\u0235\u0237\7\62\2\2\u0236\u0238\5r:\2\u0237\u0236"+
		"\3\2\2\2\u0237\u0238\3\2\2\2\u0238\u023a\3\2\2\2\u0239\u0231\3\2\2\2\u0239"+
		"\u0235\3\2\2\2\u023aq\3\2\2\2\u023b\u023c\7!\2\2\u023c\u0241\5t;\2\u023d"+
		"\u023e\7\34\2\2\u023e\u0240\5t;\2\u023f\u023d\3\2\2\2\u0240\u0243\3\2"+
		"\2\2\u0241\u023f\3\2\2\2\u0241\u0242\3\2\2\2\u0242\u0244\3\2\2\2\u0243"+
		"\u0241\3\2\2\2\u0244\u0245\7\"\2\2\u0245s\3\2\2\2\u0246\u024e\5v<\2\u0247"+
		"\u0248\5v<\2\u0248\u024b\7#\2\2\u0249\u024c\5v<\2\u024a\u024c\7\62\2\2"+
		"\u024b\u0249\3\2\2\2\u024b\u024a\3\2\2\2\u024c\u024e\3\2\2\2\u024d\u0246"+
		"\3\2\2\2\u024d\u0247\3\2\2\2\u024eu\3\2\2\2\u024f\u0250\t\4\2\2\u0250"+
		"w\3\2\2\2Ry\u0081\u0088\u0092\u0098\u00a0\u00ae\u00b4\u00bc\u00c6\u00ce"+
		"\u00d2\u00da\u00e2\u00ea\u00f0\u00f5\u00f8\u00fb\u00ff\u0102\u0105\u0108"+
		"\u010d\u0118\u011c\u0127\u0132\u013f\u014a\u0150\u0153\u0156\u0164\u0169"+
		"\u016c\u0171\u0175\u0179\u017d\u0181\u0183\u0189\u0195\u019e\u01a2\u01a6"+
		"\u01ad\u01b1\u01b6\u01bc\u01c1\u01c6\u01c8\u01ce\u01d2\u01d8\u01dc\u01e0"+
		"\u01e2\u01eb\u01ed\u01f5\u01f7\u01fd\u0205\u020c\u0210\u0214\u0218\u021d"+
		"\u0221\u0228\u022b\u0233\u0237\u0239\u0241\u024b\u024d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}