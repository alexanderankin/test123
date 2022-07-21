// Generated from /home/danson/src/jedit/plugins/Beauty/src/beauty/parsers/json/JSON.g4 by ANTLR 4.10.1

    package beauty.parsers.json;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JSONLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, STRING=3, NUMBER=4, WS=5, LBRACE=6, RBRACE=7, LSQUARE=8, 
		RSQUARE=9, DQUOTE=10;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "STRING", "ESC", "UNICODE", "HEX", "NUMBER", "INT", "EXP", 
			"WS", "LBRACE", "RBRACE", "LSQUARE", "RSQUARE", "DQUOTE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','", "':'", null, null, null, "'{'", "'}'", "'['", "']'", "'\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "STRING", "NUMBER", "WS", "LBRACE", "RBRACE", "LSQUARE", 
			"RSQUARE", "DQUOTE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public JSONLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "JSON.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\n\u0081\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0005\u0002\'\b\u0002\n\u0002\f\u0002*\t\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u00031\b\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0003\u0006<\b\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0004\u0006A\b\u0006\u000b\u0006\f\u0006B\u0001\u0006"+
		"\u0003\u0006F\b\u0006\u0001\u0006\u0003\u0006I\b\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0003\u0006O\b\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0003\u0006_\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0005"+
		"\u0007d\b\u0007\n\u0007\f\u0007g\t\u0007\u0003\u0007i\b\u0007\u0001\b"+
		"\u0001\b\u0003\bm\b\b\u0001\b\u0001\b\u0001\t\u0004\tr\b\t\u000b\t\f\t"+
		"s\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0000\u0000\u000f\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0000\t\u0000\u000b\u0000\r\u0004\u000f"+
		"\u0000\u0011\u0000\u0013\u0005\u0015\u0006\u0017\u0007\u0019\b\u001b\t"+
		"\u001d\n\u0001\u0000\b\u0002\u0000\"\"\\\\\b\u0000\"\"//\\\\bbffnnrrt"+
		"t\u0003\u000009AFaf\u0001\u000009\u0001\u000019\u0002\u0000EEee\u0002"+
		"\u0000++--\u0003\u0000\t\n\r\r  \u008c\u0000\u0001\u0001\u0000\u0000\u0000"+
		"\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000"+
		"\u0000\r\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000"+
		"\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000"+
		"\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000"+
		"\u001d\u0001\u0000\u0000\u0000\u0001\u001f\u0001\u0000\u0000\u0000\u0003"+
		"!\u0001\u0000\u0000\u0000\u0005#\u0001\u0000\u0000\u0000\u0007-\u0001"+
		"\u0000\u0000\u0000\t2\u0001\u0000\u0000\u0000\u000b8\u0001\u0000\u0000"+
		"\u0000\r^\u0001\u0000\u0000\u0000\u000fh\u0001\u0000\u0000\u0000\u0011"+
		"j\u0001\u0000\u0000\u0000\u0013q\u0001\u0000\u0000\u0000\u0015w\u0001"+
		"\u0000\u0000\u0000\u0017y\u0001\u0000\u0000\u0000\u0019{\u0001\u0000\u0000"+
		"\u0000\u001b}\u0001\u0000\u0000\u0000\u001d\u007f\u0001\u0000\u0000\u0000"+
		"\u001f \u0005,\u0000\u0000 \u0002\u0001\u0000\u0000\u0000!\"\u0005:\u0000"+
		"\u0000\"\u0004\u0001\u0000\u0000\u0000#(\u0003\u001d\u000e\u0000$\'\u0003"+
		"\u0007\u0003\u0000%\'\b\u0000\u0000\u0000&$\u0001\u0000\u0000\u0000&%"+
		"\u0001\u0000\u0000\u0000\'*\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000"+
		"\u0000()\u0001\u0000\u0000\u0000)+\u0001\u0000\u0000\u0000*(\u0001\u0000"+
		"\u0000\u0000+,\u0003\u001d\u000e\u0000,\u0006\u0001\u0000\u0000\u0000"+
		"-0\u0005\\\u0000\u0000.1\u0007\u0001\u0000\u0000/1\u0003\t\u0004\u0000"+
		"0.\u0001\u0000\u0000\u00000/\u0001\u0000\u0000\u00001\b\u0001\u0000\u0000"+
		"\u000023\u0005u\u0000\u000034\u0003\u000b\u0005\u000045\u0003\u000b\u0005"+
		"\u000056\u0003\u000b\u0005\u000067\u0003\u000b\u0005\u00007\n\u0001\u0000"+
		"\u0000\u000089\u0007\u0002\u0000\u00009\f\u0001\u0000\u0000\u0000:<\u0005"+
		"-\u0000\u0000;:\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<=\u0001"+
		"\u0000\u0000\u0000=>\u0003\u000f\u0007\u0000>@\u0005.\u0000\u0000?A\u0007"+
		"\u0003\u0000\u0000@?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000\u0000"+
		"B@\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000\u0000CE\u0001\u0000\u0000"+
		"\u0000DF\u0003\u0011\b\u0000ED\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000"+
		"\u0000F_\u0001\u0000\u0000\u0000GI\u0005-\u0000\u0000HG\u0001\u0000\u0000"+
		"\u0000HI\u0001\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000JK\u0003\u000f"+
		"\u0007\u0000KL\u0003\u0011\b\u0000L_\u0001\u0000\u0000\u0000MO\u0005-"+
		"\u0000\u0000NM\u0001\u0000\u0000\u0000NO\u0001\u0000\u0000\u0000OP\u0001"+
		"\u0000\u0000\u0000P_\u0003\u000f\u0007\u0000QR\u0005t\u0000\u0000RS\u0005"+
		"r\u0000\u0000ST\u0005u\u0000\u0000T_\u0005e\u0000\u0000UV\u0005f\u0000"+
		"\u0000VW\u0005a\u0000\u0000WX\u0005l\u0000\u0000XY\u0005s\u0000\u0000"+
		"Y_\u0005e\u0000\u0000Z[\u0005n\u0000\u0000[\\\u0005u\u0000\u0000\\]\u0005"+
		"l\u0000\u0000]_\u0005l\u0000\u0000^;\u0001\u0000\u0000\u0000^H\u0001\u0000"+
		"\u0000\u0000^N\u0001\u0000\u0000\u0000^Q\u0001\u0000\u0000\u0000^U\u0001"+
		"\u0000\u0000\u0000^Z\u0001\u0000\u0000\u0000_\u000e\u0001\u0000\u0000"+
		"\u0000`i\u00050\u0000\u0000ae\u0007\u0004\u0000\u0000bd\u0007\u0003\u0000"+
		"\u0000cb\u0001\u0000\u0000\u0000dg\u0001\u0000\u0000\u0000ec\u0001\u0000"+
		"\u0000\u0000ef\u0001\u0000\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001"+
		"\u0000\u0000\u0000h`\u0001\u0000\u0000\u0000ha\u0001\u0000\u0000\u0000"+
		"i\u0010\u0001\u0000\u0000\u0000jl\u0007\u0005\u0000\u0000km\u0007\u0006"+
		"\u0000\u0000lk\u0001\u0000\u0000\u0000lm\u0001\u0000\u0000\u0000mn\u0001"+
		"\u0000\u0000\u0000no\u0003\u000f\u0007\u0000o\u0012\u0001\u0000\u0000"+
		"\u0000pr\u0007\u0007\u0000\u0000qp\u0001\u0000\u0000\u0000rs\u0001\u0000"+
		"\u0000\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tu\u0001"+
		"\u0000\u0000\u0000uv\u0006\t\u0000\u0000v\u0014\u0001\u0000\u0000\u0000"+
		"wx\u0005{\u0000\u0000x\u0016\u0001\u0000\u0000\u0000yz\u0005}\u0000\u0000"+
		"z\u0018\u0001\u0000\u0000\u0000{|\u0005[\u0000\u0000|\u001a\u0001\u0000"+
		"\u0000\u0000}~\u0005]\u0000\u0000~\u001c\u0001\u0000\u0000\u0000\u007f"+
		"\u0080\u0005\"\u0000\u0000\u0080\u001e\u0001\u0000\u0000\u0000\u000e\u0000"+
		"&(0;BEHN^ehls\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}