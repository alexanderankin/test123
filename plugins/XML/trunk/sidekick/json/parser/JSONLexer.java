// Generated from /home/danson/src/jedit/plugins/XML/sidekick/json/parser/JSON.g4 by ANTLR 4.4

    package sidekick.json.parser;

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
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, STRING=3, NUMBER=4, WS=5, LBRACE=6, RBRACE=7, LSQUARE=8, 
		RSQUARE=9;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'"
	};
	public static final String[] ruleNames = {
		"T__1", "T__0", "STRING", "ESC", "UNICODE", "HEX", "NUMBER", "INT", "EXP", 
		"WS", "LBRACE", "RBRACE", "LSQUARE", "RSQUARE"
	};


	public JSONLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "JSON.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\13\177\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\3\3\3\3\4\3\4\3\4"+
		"\7\4\'\n\4\f\4\16\4*\13\4\3\4\3\4\3\5\3\5\3\5\5\5\61\n\5\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\b\5\b<\n\b\3\b\3\b\3\b\6\bA\n\b\r\b\16\bB\3\b\5\b"+
		"F\n\b\3\b\5\bI\n\b\3\b\3\b\3\b\3\b\5\bO\n\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b_\n\b\3\t\3\t\3\t\7\td\n\t\f\t\16\t"+
		"g\13\t\5\ti\n\t\3\n\3\n\5\nm\n\n\3\n\3\n\3\13\6\13r\n\13\r\13\16\13s\3"+
		"\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\2\2\20\3\3\5\4\7\5\t\2\13"+
		"\2\r\2\17\6\21\2\23\2\25\7\27\b\31\t\33\n\35\13\3\2\n\4\2$$^^\n\2$$\61"+
		"\61^^ddhhppttvv\5\2\62;CHch\3\2\62;\3\2\63;\4\2GGgg\4\2--//\5\2\13\f\17"+
		"\17\"\"\u008a\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\17\3\2\2\2\2\25\3"+
		"\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\3\37\3\2\2"+
		"\2\5!\3\2\2\2\7#\3\2\2\2\t-\3\2\2\2\13\62\3\2\2\2\r8\3\2\2\2\17^\3\2\2"+
		"\2\21h\3\2\2\2\23j\3\2\2\2\25q\3\2\2\2\27w\3\2\2\2\31y\3\2\2\2\33{\3\2"+
		"\2\2\35}\3\2\2\2\37 \7.\2\2 \4\3\2\2\2!\"\7<\2\2\"\6\3\2\2\2#(\7$\2\2"+
		"$\'\5\t\5\2%\'\n\2\2\2&$\3\2\2\2&%\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2"+
		"\2\2)+\3\2\2\2*(\3\2\2\2+,\7$\2\2,\b\3\2\2\2-\60\7^\2\2.\61\t\3\2\2/\61"+
		"\5\13\6\2\60.\3\2\2\2\60/\3\2\2\2\61\n\3\2\2\2\62\63\7w\2\2\63\64\5\r"+
		"\7\2\64\65\5\r\7\2\65\66\5\r\7\2\66\67\5\r\7\2\67\f\3\2\2\289\t\4\2\2"+
		"9\16\3\2\2\2:<\7/\2\2;:\3\2\2\2;<\3\2\2\2<=\3\2\2\2=>\5\21\t\2>@\7\60"+
		"\2\2?A\t\5\2\2@?\3\2\2\2AB\3\2\2\2B@\3\2\2\2BC\3\2\2\2CE\3\2\2\2DF\5\23"+
		"\n\2ED\3\2\2\2EF\3\2\2\2F_\3\2\2\2GI\7/\2\2HG\3\2\2\2HI\3\2\2\2IJ\3\2"+
		"\2\2JK\5\21\t\2KL\5\23\n\2L_\3\2\2\2MO\7/\2\2NM\3\2\2\2NO\3\2\2\2OP\3"+
		"\2\2\2P_\5\21\t\2QR\7v\2\2RS\7t\2\2ST\7w\2\2T_\7g\2\2UV\7h\2\2VW\7c\2"+
		"\2WX\7n\2\2XY\7u\2\2Y_\7g\2\2Z[\7p\2\2[\\\7w\2\2\\]\7n\2\2]_\7n\2\2^;"+
		"\3\2\2\2^H\3\2\2\2^N\3\2\2\2^Q\3\2\2\2^U\3\2\2\2^Z\3\2\2\2_\20\3\2\2\2"+
		"`i\7\62\2\2ae\t\6\2\2bd\t\5\2\2cb\3\2\2\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2"+
		"\2fi\3\2\2\2ge\3\2\2\2h`\3\2\2\2ha\3\2\2\2i\22\3\2\2\2jl\t\7\2\2km\t\b"+
		"\2\2lk\3\2\2\2lm\3\2\2\2mn\3\2\2\2no\5\21\t\2o\24\3\2\2\2pr\t\t\2\2qp"+
		"\3\2\2\2rs\3\2\2\2sq\3\2\2\2st\3\2\2\2tu\3\2\2\2uv\b\13\2\2v\26\3\2\2"+
		"\2wx\7}\2\2x\30\3\2\2\2yz\7\177\2\2z\32\3\2\2\2{|\7]\2\2|\34\3\2\2\2}"+
		"~\7_\2\2~\36\3\2\2\2\20\2&(\60;BEHN^ehls\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}