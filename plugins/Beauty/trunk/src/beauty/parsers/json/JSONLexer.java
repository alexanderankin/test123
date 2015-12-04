// Generated from /home/danson/src/jedit/plugins/Beauty/src/beauty/parsers/json/JSON.g4 by ANTLR 4.x

    package beauty.parsers.json;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

public class JSONLexer extends Lexer {
	public static final int
		T__0=1, T__1=2, STRING=3, NUMBER=4, WS=5, LBRACE=6, RBRACE=7, LSQUARE=8, 
		RSQUARE=9, DQUOTE=10;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "STRING", "ESC", "UNICODE", "HEX", "NUMBER", "INT", "EXP", 
		"WS", "LBRACE", "RBRACE", "LSQUARE", "RSQUARE", "DQUOTE"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", "':'", null, null, null, "'{'", "'}'", "'['", "']'", "'\"'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "STRING", "NUMBER", "WS", "LBRACE", "RBRACE", "LSQUARE", 
		"RSQUARE", "DQUOTE"
	};
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
	@NotNull
	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public JSONLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN);
	}

	@Override
	public String getGrammarFileName() { return "JSON.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	public static final String _serializedATN =
		"\3\uaf6f\u8320\u479d\ub75c\u4880\u1605\u191c\uab37\2\f\u0083\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\3\3\3\3"+
		"\4\3\4\3\4\7\4)\n\4\f\4\16\4,\13\4\3\4\3\4\3\5\3\5\3\5\5\5\63\n\5\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\5\b>\n\b\3\b\3\b\3\b\6\bC\n\b\r\b\16"+
		"\bD\3\b\5\bH\n\b\3\b\5\bK\n\b\3\b\3\b\3\b\3\b\5\bQ\n\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\ba\n\b\3\t\3\t\3\t\7\tf\n\t"+
		"\f\t\16\ti\13\t\5\tk\n\t\3\n\3\n\5\no\n\n\3\n\3\n\3\13\6\13t\n\13\r\13"+
		"\16\13u\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\2\2\2"+
		"\21\3\2\3\5\2\4\7\2\5\t\2\2\13\2\2\r\2\2\17\2\6\21\2\2\23\2\2\25\2\7\27"+
		"\2\b\31\2\t\33\2\n\35\2\13\37\2\f\3\2\n\4\2$$^^\n\2$$\61\61^^ddhhpptt"+
		"vv\5\2\62;CHch\3\2\62;\3\2\63;\4\2GGgg\4\2--//\5\2\13\f\17\17\"\"\u008e"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\17\3\2\2\2\2\25\3\2\2\2\2\27\3"+
		"\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\3!\3\2\2\2"+
		"\5#\3\2\2\2\7%\3\2\2\2\t/\3\2\2\2\13\64\3\2\2\2\r:\3\2\2\2\17`\3\2\2\2"+
		"\21j\3\2\2\2\23l\3\2\2\2\25s\3\2\2\2\27y\3\2\2\2\31{\3\2\2\2\33}\3\2\2"+
		"\2\35\177\3\2\2\2\37\u0081\3\2\2\2!\"\7.\2\2\"\4\3\2\2\2#$\7<\2\2$\6\3"+
		"\2\2\2%*\5\37\20\2&)\5\t\5\2\')\n\2\2\2(&\3\2\2\2(\'\3\2\2\2),\3\2\2\2"+
		"*(\3\2\2\2*+\3\2\2\2+-\3\2\2\2,*\3\2\2\2-.\5\37\20\2.\b\3\2\2\2/\62\7"+
		"^\2\2\60\63\t\3\2\2\61\63\5\13\6\2\62\60\3\2\2\2\62\61\3\2\2\2\63\n\3"+
		"\2\2\2\64\65\7w\2\2\65\66\5\r\7\2\66\67\5\r\7\2\678\5\r\7\289\5\r\7\2"+
		"9\f\3\2\2\2:;\t\4\2\2;\16\3\2\2\2<>\7/\2\2=<\3\2\2\2=>\3\2\2\2>?\3\2\2"+
		"\2?@\5\21\t\2@B\7\60\2\2AC\t\5\2\2BA\3\2\2\2CD\3\2\2\2DB\3\2\2\2DE\3\2"+
		"\2\2EG\3\2\2\2FH\5\23\n\2GF\3\2\2\2GH\3\2\2\2Ha\3\2\2\2IK\7/\2\2JI\3\2"+
		"\2\2JK\3\2\2\2KL\3\2\2\2LM\5\21\t\2MN\5\23\n\2Na\3\2\2\2OQ\7/\2\2PO\3"+
		"\2\2\2PQ\3\2\2\2QR\3\2\2\2Ra\5\21\t\2ST\7v\2\2TU\7t\2\2UV\7w\2\2Va\7g"+
		"\2\2WX\7h\2\2XY\7c\2\2YZ\7n\2\2Z[\7u\2\2[a\7g\2\2\\]\7p\2\2]^\7w\2\2^"+
		"_\7n\2\2_a\7n\2\2`=\3\2\2\2`J\3\2\2\2`P\3\2\2\2`S\3\2\2\2`W\3\2\2\2`\\"+
		"\3\2\2\2a\20\3\2\2\2bk\7\62\2\2cg\t\6\2\2df\t\5\2\2ed\3\2\2\2fi\3\2\2"+
		"\2ge\3\2\2\2gh\3\2\2\2hk\3\2\2\2ig\3\2\2\2jb\3\2\2\2jc\3\2\2\2k\22\3\2"+
		"\2\2ln\t\7\2\2mo\t\b\2\2nm\3\2\2\2no\3\2\2\2op\3\2\2\2pq\5\21\t\2q\24"+
		"\3\2\2\2rt\t\t\2\2sr\3\2\2\2tu\3\2\2\2us\3\2\2\2uv\3\2\2\2vw\3\2\2\2w"+
		"x\b\13\2\2x\26\3\2\2\2yz\7}\2\2z\30\3\2\2\2{|\7\177\2\2|\32\3\2\2\2}~"+
		"\7]\2\2~\34\3\2\2\2\177\u0080\7_\2\2\u0080\36\3\2\2\2\u0081\u0082\7$\2"+
		"\2\u0082 \3\2\2\2\20\2(*\62=DGJP`gjnu\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
	}
}