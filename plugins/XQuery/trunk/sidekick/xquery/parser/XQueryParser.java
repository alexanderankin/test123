// Generated from XQueryParser.g4 by ANTLR 4.4
package sidekick.xquery.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class XQueryParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		KW_EMPTY=67, KW_DOCUMENT_NODE=64, CDATA=11, STAR=22, Apos=7, LANGLE=34, 
		KW_ENCODING=69, KW_VALIDATE=130, KW_DEFAULT=58, KW_ELEMENT=65, KW_EMPTY_SEQUENCE=68, 
		KW_WHERE=133, KW_NE=98, KW_LET=93, LBRACKET=18, VBAR=33, KW_SELF=118, 
		RPAREN=17, NOT_EQUAL=15, KW_PARENT=108, KW_LEAST=92, KW_DESCENDANT_OR_SELF=60, 
		KW_FOLLOWING_SIBLING=75, KW_THEN=124, KW_BASE_URI=46, RANGLE=35, COMMENT=8, 
		XQComment=139, Quot=6, KW_EQ=70, KW_SCHEMA=115, KW_OPTION=103, KW_NO_INHERIT=99, 
		KW_PRESERVE=111, KW_FUNCTION=77, DDOT=27, RBRACE=21, KW_ANCESTOR=39, KW_SCHEMA_ATTR=116, 
		KW_LT=94, KW_MODULE=96, SEMICOLON=30, KW_IMPORT=83, KW_DIV=62, KW_LE=91, 
		KW_GREATEST=79, KW_INSTANCE=86, KW_SOME=119, FullQName=135, WS=13, KW_MOD=95, 
		KW_EVERY=71, KW_SATISFIES=114, KW_TO=125, KW_BY=48, PI=10, KW_DOCUMENT=63, 
		KW_STABLE=120, DSLASH=32, KW_CASTABLE=51, KW_VARIABLE=131, DoubleLiteral=3, 
		KW_ELSE=66, KW_TEXT=123, DOLLAR=38, NCName=138, KW_PRECEDING=109, KW_UNORDERED=129, 
		KW_ASCENDING=43, KW_XQUERY=134, KW_TYPESWITCH=127, CharRef=5, KW_SCHEMA_ELEM=117, 
		KW_LAX=90, KW_ORDERED=106, KW_AND=41, LBRACE=20, KW_INHERIT=85, PRAGMA=12, 
		KW_STRICT=121, NCNameWithPrefixWildcard=137, ContentChar=140, LPAREN=16, 
		AT=37, KW_NODE=101, SLASH=31, KW_NO_PRESERVE=100, KW_EXTERNAL=73, KW_ITEM=89, 
		COMMA=25, KW_FOR=76, EQUAL=14, KW_RETURN=113, KW_ORDERING=107, KW_IN=84, 
		KW_CONSTRUCTION=55, PLUS=23, KW_TREAT=126, KW_IS=88, KW_IDIV=81, RBRACKET=19, 
		DOT=26, KW_IF=82, KW_ORDER=105, KW_ATTRIBUTE=45, COLON_EQ=29, KW_UNION=128, 
		KW_FOLLOWING=74, KW_CHILD=52, KW_COPY_NS=56, KW_CAST=50, KW_STRIP=122, 
		KW_DESCENDANT=59, KW_GT=80, KW_INTERSECT=87, KW_PI=112, XMLDECL=9, KW_EXCEPT=72, 
		KW_COLLATION=53, MINUS=24, DecimalLiteral=2, KW_CASE=49, COLON=28, QUESTION=36, 
		KW_OR=104, KW_VERSION=132, KW_ANCESTOR_OR_SELF=40, PredefinedEntityRef=4, 
		KW_NAMESPACE=97, KW_BOUNDARY_SPACE=47, KW_AS=42, KW_AT=44, KW_PRECEDING_SIBLING=110, 
		KW_COMMENT=54, KW_OF=102, KW_DESCENDING=61, IntegerLiteral=1, KW_DECLARE=57, 
		KW_GE=78, NCNameWithLocalWildcard=136;
	public static final String[] tokenNames = {
		"<INVALID>", "IntegerLiteral", "DecimalLiteral", "DoubleLiteral", "PredefinedEntityRef", 
		"CharRef", "'\"'", "'''", "COMMENT", "XMLDECL", "PI", "CDATA", "PRAGMA", 
		"WS", "'='", "'!='", "'('", "')'", "'['", "']'", "'{'", "'}'", "'*'", 
		"'+'", "'-'", "','", "'.'", "'..'", "':'", "':='", "';'", "'/'", "'//'", 
		"'|'", "'<'", "'>'", "'?'", "'@'", "'$'", "'ancestor'", "'ancestor-or-self'", 
		"'and'", "'as'", "'ascending'", "'at'", "'attribute'", "'base-uri'", "'boundary-space'", 
		"'by'", "'case'", "'cast'", "'castable'", "'child'", "'collation'", "'comment'", 
		"'construction'", "'copy-namespaces'", "'declare'", "'default'", "'descendant'", 
		"'descendant-or-self'", "'descending'", "'div'", "'document'", "'document-node'", 
		"'element'", "'else'", "'empty'", "'empty-sequence'", "'encoding'", "'eq'", 
		"'every'", "'except'", "'external'", "'following'", "'following-sibling'", 
		"'for'", "'function'", "'ge'", "'greatest'", "'gt'", "'idiv'", "'if'", 
		"'import'", "'in'", "'inherit'", "'instance'", "'intersect'", "'is'", 
		"'item'", "'lax'", "'le'", "'least'", "'let'", "'lt'", "'mod'", "'module'", 
		"'namespace'", "'ne'", "'no-inherit'", "'no-preserve'", "'node'", "'of'", 
		"'option'", "'or'", "'order'", "'ordered'", "'ordering'", "'parent'", 
		"'preceding'", "'preceding-sibling'", "'preserve'", "'processing-instruction'", 
		"'return'", "'satisfies'", "'schema'", "'schema-attribute'", "'schema-element'", 
		"'self'", "'some'", "'stable'", "'strict'", "'strip'", "'text'", "'then'", 
		"'to'", "'treat'", "'typeswitch'", "'union'", "'unordered'", "'validate'", 
		"'variable'", "'version'", "'where'", "'xquery'", "FullQName", "NCNameWithLocalWildcard", 
		"NCNameWithPrefixWildcard", "NCName", "XQComment", "ContentChar"
	};
	public static final int
		RULE_module = 0, RULE_versionDecl = 1, RULE_mainModule = 2, RULE_libraryModule = 3, 
		RULE_moduleDecl = 4, RULE_prolog = 5, RULE_defaultNamespaceDecl = 6, RULE_setter = 7, 
		RULE_namespaceDecl = 8, RULE_schemaImport = 9, RULE_moduleImport = 10, 
		RULE_varDecl = 11, RULE_functionDecl = 12, RULE_optionDecl = 13, RULE_param = 14, 
		RULE_expr = 15, RULE_exprSingle = 16, RULE_flworExpr = 17, RULE_forClause = 18, 
		RULE_forVar = 19, RULE_letClause = 20, RULE_letVar = 21, RULE_orderByClause = 22, 
		RULE_orderSpec = 23, RULE_quantifiedExpr = 24, RULE_quantifiedVar = 25, 
		RULE_typeswitchExpr = 26, RULE_caseClause = 27, RULE_ifExpr = 28, RULE_orExpr = 29, 
		RULE_primaryExpr = 30, RULE_relativePathExpr = 31, RULE_stepExpr = 32, 
		RULE_axisStep = 33, RULE_forwardStep = 34, RULE_forwardAxis = 35, RULE_abbrevForwardStep = 36, 
		RULE_reverseStep = 37, RULE_reverseAxis = 38, RULE_abbrevReverseStep = 39, 
		RULE_nodeTest = 40, RULE_nameTest = 41, RULE_filterExpr = 42, RULE_predicateList = 43, 
		RULE_constructor = 44, RULE_directConstructor = 45, RULE_dirElemConstructorOpenClose = 46, 
		RULE_dirElemConstructorSingleTag = 47, RULE_dirAttributeList = 48, RULE_dirAttributeValue = 49, 
		RULE_dirElemContent = 50, RULE_commonContent = 51, RULE_computedConstructor = 52, 
		RULE_singleType = 53, RULE_typeDeclaration = 54, RULE_sequenceType = 55, 
		RULE_itemType = 56, RULE_kindTest = 57, RULE_documentTest = 58, RULE_elementTest = 59, 
		RULE_attributeTest = 60, RULE_schemaElementTest = 61, RULE_schemaAttributeTest = 62, 
		RULE_piTest = 63, RULE_commentTest = 64, RULE_textTest = 65, RULE_anyKindTest = 66, 
		RULE_qName = 67, RULE_ncName = 68, RULE_functionName = 69, RULE_keyword = 70, 
		RULE_keywordNotOKForFunction = 71, RULE_keywordOKForFunction = 72, RULE_stringLiteral = 73, 
		RULE_noQuotesNoBracesNoAmpNoLAng = 74;
	public static final String[] ruleNames = {
		"module", "versionDecl", "mainModule", "libraryModule", "moduleDecl", 
		"prolog", "defaultNamespaceDecl", "setter", "namespaceDecl", "schemaImport", 
		"moduleImport", "varDecl", "functionDecl", "optionDecl", "param", "expr", 
		"exprSingle", "flworExpr", "forClause", "forVar", "letClause", "letVar", 
		"orderByClause", "orderSpec", "quantifiedExpr", "quantifiedVar", "typeswitchExpr", 
		"caseClause", "ifExpr", "orExpr", "primaryExpr", "relativePathExpr", "stepExpr", 
		"axisStep", "forwardStep", "forwardAxis", "abbrevForwardStep", "reverseStep", 
		"reverseAxis", "abbrevReverseStep", "nodeTest", "nameTest", "filterExpr", 
		"predicateList", "constructor", "directConstructor", "dirElemConstructorOpenClose", 
		"dirElemConstructorSingleTag", "dirAttributeList", "dirAttributeValue", 
		"dirElemContent", "commonContent", "computedConstructor", "singleType", 
		"typeDeclaration", "sequenceType", "itemType", "kindTest", "documentTest", 
		"elementTest", "attributeTest", "schemaElementTest", "schemaAttributeTest", 
		"piTest", "commentTest", "textTest", "anyKindTest", "qName", "ncName", 
		"functionName", "keyword", "keywordNotOKForFunction", "keywordOKForFunction", 
		"stringLiteral", "noQuotesNoBracesNoAmpNoLAng"
	};

	@Override
	public String getGrammarFileName() { return "XQueryParser.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public XQueryParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ModuleContext extends ParserRuleContext {
		public MainModuleContext mainModule() {
			return getRuleContext(MainModuleContext.class,0);
		}
		public LibraryModuleContext libraryModule() {
			return getRuleContext(LibraryModuleContext.class,0);
		}
		public VersionDeclContext versionDecl() {
			return getRuleContext(VersionDeclContext.class,0);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitModule(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_module);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(150); versionDecl();
				}
				break;
			}
			setState(155);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(153); libraryModule();
				}
				break;
			case 2:
				{
				setState(154); mainModule();
				}
				break;
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

	public static class VersionDeclContext extends ParserRuleContext {
		public StringLiteralContext version;
		public StringLiteralContext encoding;
		public StringLiteralContext stringLiteral(int i) {
			return getRuleContext(StringLiteralContext.class,i);
		}
		public List<StringLiteralContext> stringLiteral() {
			return getRuleContexts(StringLiteralContext.class);
		}
		public VersionDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_versionDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterVersionDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitVersionDecl(this);
		}
	}

	public final VersionDeclContext versionDecl() throws RecognitionException {
		VersionDeclContext _localctx = new VersionDeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_versionDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157); match(KW_XQUERY);
			setState(158); match(KW_VERSION);
			setState(159); ((VersionDeclContext)_localctx).version = stringLiteral();
			setState(162);
			_la = _input.LA(1);
			if (_la==KW_ENCODING) {
				{
				setState(160); match(KW_ENCODING);
				setState(161); ((VersionDeclContext)_localctx).encoding = stringLiteral();
				}
			}

			setState(164); match(SEMICOLON);
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

	public static class MainModuleContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PrologContext prolog() {
			return getRuleContext(PrologContext.class,0);
		}
		public MainModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mainModule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterMainModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitMainModule(this);
		}
	}

	public final MainModuleContext mainModule() throws RecognitionException {
		MainModuleContext _localctx = new MainModuleContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_mainModule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166); prolog();
			setState(167); expr();
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

	public static class LibraryModuleContext extends ParserRuleContext {
		public PrologContext prolog() {
			return getRuleContext(PrologContext.class,0);
		}
		public ModuleDeclContext moduleDecl() {
			return getRuleContext(ModuleDeclContext.class,0);
		}
		public LibraryModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_libraryModule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterLibraryModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitLibraryModule(this);
		}
	}

	public final LibraryModuleContext libraryModule() throws RecognitionException {
		LibraryModuleContext _localctx = new LibraryModuleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_libraryModule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169); moduleDecl();
			setState(170); prolog();
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

	public static class ModuleDeclContext extends ParserRuleContext {
		public NcNameContext prefix;
		public StringLiteralContext uri;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public NcNameContext ncName() {
			return getRuleContext(NcNameContext.class,0);
		}
		public ModuleDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterModuleDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitModuleDecl(this);
		}
	}

	public final ModuleDeclContext moduleDecl() throws RecognitionException {
		ModuleDeclContext _localctx = new ModuleDeclContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_moduleDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172); match(KW_MODULE);
			setState(173); match(KW_NAMESPACE);
			setState(174); ((ModuleDeclContext)_localctx).prefix = ncName();
			setState(175); match(EQUAL);
			setState(176); ((ModuleDeclContext)_localctx).uri = stringLiteral();
			setState(177); match(SEMICOLON);
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

	public static class PrologContext extends ParserRuleContext {
		public List<SchemaImportContext> schemaImport() {
			return getRuleContexts(SchemaImportContext.class);
		}
		public NamespaceDeclContext namespaceDecl(int i) {
			return getRuleContext(NamespaceDeclContext.class,i);
		}
		public VarDeclContext varDecl(int i) {
			return getRuleContext(VarDeclContext.class,i);
		}
		public SchemaImportContext schemaImport(int i) {
			return getRuleContext(SchemaImportContext.class,i);
		}
		public SetterContext setter(int i) {
			return getRuleContext(SetterContext.class,i);
		}
		public ModuleImportContext moduleImport(int i) {
			return getRuleContext(ModuleImportContext.class,i);
		}
		public List<DefaultNamespaceDeclContext> defaultNamespaceDecl() {
			return getRuleContexts(DefaultNamespaceDeclContext.class);
		}
		public OptionDeclContext optionDecl(int i) {
			return getRuleContext(OptionDeclContext.class,i);
		}
		public List<OptionDeclContext> optionDecl() {
			return getRuleContexts(OptionDeclContext.class);
		}
		public FunctionDeclContext functionDecl(int i) {
			return getRuleContext(FunctionDeclContext.class,i);
		}
		public List<VarDeclContext> varDecl() {
			return getRuleContexts(VarDeclContext.class);
		}
		public List<NamespaceDeclContext> namespaceDecl() {
			return getRuleContexts(NamespaceDeclContext.class);
		}
		public List<FunctionDeclContext> functionDecl() {
			return getRuleContexts(FunctionDeclContext.class);
		}
		public DefaultNamespaceDeclContext defaultNamespaceDecl(int i) {
			return getRuleContext(DefaultNamespaceDeclContext.class,i);
		}
		public List<SetterContext> setter() {
			return getRuleContexts(SetterContext.class);
		}
		public List<ModuleImportContext> moduleImport() {
			return getRuleContexts(ModuleImportContext.class);
		}
		public PrologContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prolog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterProlog(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitProlog(this);
		}
	}

	public final PrologContext prolog() throws RecognitionException {
		PrologContext _localctx = new PrologContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_prolog);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(184);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						setState(179); defaultNamespaceDecl();
						}
						break;
					case 2:
						{
						setState(180); setter();
						}
						break;
					case 3:
						{
						setState(181); namespaceDecl();
						}
						break;
					case 4:
						{
						setState(182); schemaImport();
						}
						break;
					case 5:
						{
						setState(183); moduleImport();
						}
						break;
					}
					setState(186); match(SEMICOLON);
					}
					} 
				}
				setState(192);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			setState(202);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(196);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						setState(193); varDecl();
						}
						break;
					case 2:
						{
						setState(194); functionDecl();
						}
						break;
					case 3:
						{
						setState(195); optionDecl();
						}
						break;
					}
					setState(198); match(SEMICOLON);
					}
					} 
				}
				setState(204);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
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

	public static class DefaultNamespaceDeclContext extends ParserRuleContext {
		public Token type;
		public StringLiteralContext uri;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public DefaultNamespaceDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultNamespaceDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDefaultNamespaceDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDefaultNamespaceDecl(this);
		}
	}

	public final DefaultNamespaceDeclContext defaultNamespaceDecl() throws RecognitionException {
		DefaultNamespaceDeclContext _localctx = new DefaultNamespaceDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_defaultNamespaceDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205); match(KW_DECLARE);
			setState(206); match(KW_DEFAULT);
			setState(207);
			((DefaultNamespaceDeclContext)_localctx).type = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==KW_ELEMENT || _la==KW_FUNCTION) ) {
				((DefaultNamespaceDeclContext)_localctx).type = (Token)_errHandler.recoverInline(this);
			}
			consume();
			setState(208); match(KW_NAMESPACE);
			setState(209); ((DefaultNamespaceDeclContext)_localctx).uri = stringLiteral();
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

	public static class SetterContext extends ParserRuleContext {
		public SetterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setter; }
	 
		public SetterContext() { }
		public void copyFrom(SetterContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class OrderingModeDeclContext extends SetterContext {
		public Token type;
		public OrderingModeDeclContext(SetterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterOrderingModeDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitOrderingModeDecl(this);
		}
	}
	public static class ConstructionDeclContext extends SetterContext {
		public Token type;
		public ConstructionDeclContext(SetterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterConstructionDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitConstructionDecl(this);
		}
	}
	public static class DefaultCollationDeclContext extends SetterContext {
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public DefaultCollationDeclContext(SetterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDefaultCollationDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDefaultCollationDecl(this);
		}
	}
	public static class CopyNamespacesDeclContext extends SetterContext {
		public Token preserve;
		public Token inherit;
		public CopyNamespacesDeclContext(SetterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCopyNamespacesDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCopyNamespacesDecl(this);
		}
	}
	public static class BaseURIDeclContext extends SetterContext {
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public BaseURIDeclContext(SetterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterBaseURIDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitBaseURIDecl(this);
		}
	}
	public static class BoundaryDeclContext extends SetterContext {
		public Token type;
		public BoundaryDeclContext(SetterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterBoundaryDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitBoundaryDecl(this);
		}
	}
	public static class EmptyOrderDeclContext extends SetterContext {
		public Token type;
		public EmptyOrderDeclContext(SetterContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterEmptyOrderDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitEmptyOrderDecl(this);
		}
	}

	public final SetterContext setter() throws RecognitionException {
		SetterContext _localctx = new SetterContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_setter);
		int _la;
		try {
			setState(237);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new BoundaryDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(211); match(KW_DECLARE);
				setState(212); match(KW_BOUNDARY_SPACE);
				setState(213);
				((BoundaryDeclContext)_localctx).type = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_PRESERVE || _la==KW_STRIP) ) {
					((BoundaryDeclContext)_localctx).type = (Token)_errHandler.recoverInline(this);
				}
				consume();
				}
				break;
			case 2:
				_localctx = new DefaultCollationDeclContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(214); match(KW_DECLARE);
				setState(215); match(KW_DEFAULT);
				setState(216); match(KW_COLLATION);
				setState(217); stringLiteral();
				}
				break;
			case 3:
				_localctx = new BaseURIDeclContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(218); match(KW_DECLARE);
				setState(219); match(KW_BASE_URI);
				setState(220); stringLiteral();
				}
				break;
			case 4:
				_localctx = new ConstructionDeclContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(221); match(KW_DECLARE);
				setState(222); match(KW_CONSTRUCTION);
				setState(223);
				((ConstructionDeclContext)_localctx).type = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_PRESERVE || _la==KW_STRIP) ) {
					((ConstructionDeclContext)_localctx).type = (Token)_errHandler.recoverInline(this);
				}
				consume();
				}
				break;
			case 5:
				_localctx = new OrderingModeDeclContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(224); match(KW_DECLARE);
				setState(225); match(KW_ORDERING);
				setState(226);
				((OrderingModeDeclContext)_localctx).type = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_ORDERED || _la==KW_UNORDERED) ) {
					((OrderingModeDeclContext)_localctx).type = (Token)_errHandler.recoverInline(this);
				}
				consume();
				}
				break;
			case 6:
				_localctx = new EmptyOrderDeclContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(227); match(KW_DECLARE);
				setState(228); match(KW_DEFAULT);
				setState(229); match(KW_ORDER);
				setState(230); match(KW_EMPTY);
				setState(231);
				((EmptyOrderDeclContext)_localctx).type = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_GREATEST || _la==KW_LEAST) ) {
					((EmptyOrderDeclContext)_localctx).type = (Token)_errHandler.recoverInline(this);
				}
				consume();
				}
				break;
			case 7:
				_localctx = new CopyNamespacesDeclContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(232); match(KW_DECLARE);
				setState(233); match(KW_COPY_NS);
				setState(234);
				((CopyNamespacesDeclContext)_localctx).preserve = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_NO_PRESERVE || _la==KW_PRESERVE) ) {
					((CopyNamespacesDeclContext)_localctx).preserve = (Token)_errHandler.recoverInline(this);
				}
				consume();
				setState(235); match(COMMA);
				setState(236);
				((CopyNamespacesDeclContext)_localctx).inherit = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_INHERIT || _la==KW_NO_INHERIT) ) {
					((CopyNamespacesDeclContext)_localctx).inherit = (Token)_errHandler.recoverInline(this);
				}
				consume();
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

	public static class NamespaceDeclContext extends ParserRuleContext {
		public NcNameContext prefix;
		public StringLiteralContext uri;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public NcNameContext ncName() {
			return getRuleContext(NcNameContext.class,0);
		}
		public NamespaceDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_namespaceDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterNamespaceDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitNamespaceDecl(this);
		}
	}

	public final NamespaceDeclContext namespaceDecl() throws RecognitionException {
		NamespaceDeclContext _localctx = new NamespaceDeclContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_namespaceDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239); match(KW_DECLARE);
			setState(240); match(KW_NAMESPACE);
			setState(241); ((NamespaceDeclContext)_localctx).prefix = ncName();
			setState(242); match(EQUAL);
			setState(243); ((NamespaceDeclContext)_localctx).uri = stringLiteral();
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

	public static class SchemaImportContext extends ParserRuleContext {
		public NcNameContext prefix;
		public StringLiteralContext nsURI;
		public StringLiteralContext stringLiteral;
		public List<StringLiteralContext> locations = new ArrayList<StringLiteralContext>();
		public StringLiteralContext stringLiteral(int i) {
			return getRuleContext(StringLiteralContext.class,i);
		}
		public List<StringLiteralContext> stringLiteral() {
			return getRuleContexts(StringLiteralContext.class);
		}
		public NcNameContext ncName() {
			return getRuleContext(NcNameContext.class,0);
		}
		public SchemaImportContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schemaImport; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterSchemaImport(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitSchemaImport(this);
		}
	}

	public final SchemaImportContext schemaImport() throws RecognitionException {
		SchemaImportContext _localctx = new SchemaImportContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_schemaImport);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(245); match(KW_IMPORT);
			setState(246); match(KW_SCHEMA);
			setState(254);
			switch (_input.LA(1)) {
			case KW_NAMESPACE:
				{
				setState(247); match(KW_NAMESPACE);
				setState(248); ((SchemaImportContext)_localctx).prefix = ncName();
				setState(249); match(EQUAL);
				}
				break;
			case KW_DEFAULT:
				{
				setState(251); match(KW_DEFAULT);
				setState(252); match(KW_ELEMENT);
				setState(253); match(KW_NAMESPACE);
				}
				break;
			case Quot:
			case Apos:
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(256); ((SchemaImportContext)_localctx).nsURI = stringLiteral();
			setState(266);
			_la = _input.LA(1);
			if (_la==KW_AT) {
				{
				setState(257); match(KW_AT);
				setState(258); ((SchemaImportContext)_localctx).stringLiteral = stringLiteral();
				((SchemaImportContext)_localctx).locations.add(((SchemaImportContext)_localctx).stringLiteral);
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(259); match(COMMA);
					setState(260); ((SchemaImportContext)_localctx).stringLiteral = stringLiteral();
					((SchemaImportContext)_localctx).locations.add(((SchemaImportContext)_localctx).stringLiteral);
					}
					}
					setState(265);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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

	public static class ModuleImportContext extends ParserRuleContext {
		public NcNameContext prefix;
		public StringLiteralContext nsURI;
		public StringLiteralContext stringLiteral;
		public List<StringLiteralContext> locations = new ArrayList<StringLiteralContext>();
		public StringLiteralContext stringLiteral(int i) {
			return getRuleContext(StringLiteralContext.class,i);
		}
		public List<StringLiteralContext> stringLiteral() {
			return getRuleContexts(StringLiteralContext.class);
		}
		public NcNameContext ncName() {
			return getRuleContext(NcNameContext.class,0);
		}
		public ModuleImportContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleImport; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterModuleImport(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitModuleImport(this);
		}
	}

	public final ModuleImportContext moduleImport() throws RecognitionException {
		ModuleImportContext _localctx = new ModuleImportContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_moduleImport);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(268); match(KW_IMPORT);
			setState(269); match(KW_MODULE);
			setState(274);
			_la = _input.LA(1);
			if (_la==KW_NAMESPACE) {
				{
				setState(270); match(KW_NAMESPACE);
				setState(271); ((ModuleImportContext)_localctx).prefix = ncName();
				setState(272); match(EQUAL);
				}
			}

			setState(276); ((ModuleImportContext)_localctx).nsURI = stringLiteral();
			setState(286);
			_la = _input.LA(1);
			if (_la==KW_AT) {
				{
				setState(277); match(KW_AT);
				setState(278); ((ModuleImportContext)_localctx).stringLiteral = stringLiteral();
				((ModuleImportContext)_localctx).locations.add(((ModuleImportContext)_localctx).stringLiteral);
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(279); match(COMMA);
					setState(280); ((ModuleImportContext)_localctx).stringLiteral = stringLiteral();
					((ModuleImportContext)_localctx).locations.add(((ModuleImportContext)_localctx).stringLiteral);
					}
					}
					setState(285);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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

	public static class VarDeclContext extends ParserRuleContext {
		public QNameContext name;
		public TypeDeclarationContext type;
		public ExprSingleContext value;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public TypeDeclarationContext typeDeclaration() {
			return getRuleContext(TypeDeclarationContext.class,0);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public VarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterVarDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitVarDecl(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288); match(KW_DECLARE);
			setState(289); match(KW_VARIABLE);
			setState(290); match(DOLLAR);
			setState(291); ((VarDeclContext)_localctx).name = qName();
			setState(293);
			_la = _input.LA(1);
			if (_la==KW_AS) {
				{
				setState(292); ((VarDeclContext)_localctx).type = typeDeclaration();
				}
			}

			setState(298);
			switch (_input.LA(1)) {
			case COLON_EQ:
				{
				setState(295); match(COLON_EQ);
				setState(296); ((VarDeclContext)_localctx).value = exprSingle();
				}
				break;
			case KW_EXTERNAL:
				{
				setState(297); match(KW_EXTERNAL);
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

	public static class FunctionDeclContext extends ParserRuleContext {
		public QNameContext name;
		public ParamContext param;
		public List<ParamContext> params = new ArrayList<ParamContext>();
		public SequenceTypeContext type;
		public ExprContext body;
		public SequenceTypeContext sequenceType() {
			return getRuleContext(SequenceTypeContext.class,0);
		}
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public FunctionDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterFunctionDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitFunctionDecl(this);
		}
	}

	public final FunctionDeclContext functionDecl() throws RecognitionException {
		FunctionDeclContext _localctx = new FunctionDeclContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_functionDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300); match(KW_DECLARE);
			setState(301); match(KW_FUNCTION);
			setState(302); ((FunctionDeclContext)_localctx).name = qName();
			setState(303); match(LPAREN);
			setState(312);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(304); ((FunctionDeclContext)_localctx).param = param();
				((FunctionDeclContext)_localctx).params.add(((FunctionDeclContext)_localctx).param);
				setState(309);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(305); match(COMMA);
					setState(306); ((FunctionDeclContext)_localctx).param = param();
					((FunctionDeclContext)_localctx).params.add(((FunctionDeclContext)_localctx).param);
					}
					}
					setState(311);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(314); match(RPAREN);
			setState(317);
			_la = _input.LA(1);
			if (_la==KW_AS) {
				{
				setState(315); match(KW_AS);
				setState(316); ((FunctionDeclContext)_localctx).type = sequenceType();
				}
			}

			setState(324);
			switch (_input.LA(1)) {
			case LBRACE:
				{
				setState(319); match(LBRACE);
				setState(320); ((FunctionDeclContext)_localctx).body = expr();
				setState(321); match(RBRACE);
				}
				break;
			case KW_EXTERNAL:
				{
				setState(323); match(KW_EXTERNAL);
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

	public static class OptionDeclContext extends ParserRuleContext {
		public QNameContext name;
		public StringLiteralContext value;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public OptionDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optionDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterOptionDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitOptionDecl(this);
		}
	}

	public final OptionDeclContext optionDecl() throws RecognitionException {
		OptionDeclContext _localctx = new OptionDeclContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_optionDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326); match(KW_DECLARE);
			setState(327); match(KW_OPTION);
			setState(328); ((OptionDeclContext)_localctx).name = qName();
			setState(329); ((OptionDeclContext)_localctx).value = stringLiteral();
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

	public static class ParamContext extends ParserRuleContext {
		public QNameContext name;
		public TypeDeclarationContext type;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public TypeDeclarationContext typeDeclaration() {
			return getRuleContext(TypeDeclarationContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitParam(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331); match(DOLLAR);
			setState(332); ((ParamContext)_localctx).name = qName();
			setState(334);
			_la = _input.LA(1);
			if (_la==KW_AS) {
				{
				setState(333); ((ParamContext)_localctx).type = typeDeclaration();
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

	public static class ExprContext extends ParserRuleContext {
		public ExprSingleContext exprSingle(int i) {
			return getRuleContext(ExprSingleContext.class,i);
		}
		public List<ExprSingleContext> exprSingle() {
			return getRuleContexts(ExprSingleContext.class);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(336); exprSingle();
			setState(341);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(337); match(COMMA);
				setState(338); exprSingle();
				}
				}
				setState(343);
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

	public static class ExprSingleContext extends ParserRuleContext {
		public IfExprContext ifExpr() {
			return getRuleContext(IfExprContext.class,0);
		}
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public QuantifiedExprContext quantifiedExpr() {
			return getRuleContext(QuantifiedExprContext.class,0);
		}
		public FlworExprContext flworExpr() {
			return getRuleContext(FlworExprContext.class,0);
		}
		public TypeswitchExprContext typeswitchExpr() {
			return getRuleContext(TypeswitchExprContext.class,0);
		}
		public ExprSingleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprSingle; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterExprSingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitExprSingle(this);
		}
	}

	public final ExprSingleContext exprSingle() throws RecognitionException {
		ExprSingleContext _localctx = new ExprSingleContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_exprSingle);
		try {
			setState(349);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(344); flworExpr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(345); quantifiedExpr();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(346); typeswitchExpr();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(347); ifExpr();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(348); orExpr(0);
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

	public static class FlworExprContext extends ParserRuleContext {
		public ExprSingleContext whereExpr;
		public ExprSingleContext returnExpr;
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
		}
		public List<ForClauseContext> forClause() {
			return getRuleContexts(ForClauseContext.class);
		}
		public ExprSingleContext exprSingle(int i) {
			return getRuleContext(ExprSingleContext.class,i);
		}
		public List<LetClauseContext> letClause() {
			return getRuleContexts(LetClauseContext.class);
		}
		public LetClauseContext letClause(int i) {
			return getRuleContext(LetClauseContext.class,i);
		}
		public List<ExprSingleContext> exprSingle() {
			return getRuleContexts(ExprSingleContext.class);
		}
		public ForClauseContext forClause(int i) {
			return getRuleContext(ForClauseContext.class,i);
		}
		public FlworExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flworExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterFlworExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitFlworExpr(this);
		}
	}

	public final FlworExprContext flworExpr() throws RecognitionException {
		FlworExprContext _localctx = new FlworExprContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_flworExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(353); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(353);
				switch (_input.LA(1)) {
				case KW_FOR:
					{
					setState(351); forClause();
					}
					break;
				case KW_LET:
					{
					setState(352); letClause();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(355); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==KW_FOR || _la==KW_LET );
			setState(359);
			_la = _input.LA(1);
			if (_la==KW_WHERE) {
				{
				setState(357); match(KW_WHERE);
				setState(358); ((FlworExprContext)_localctx).whereExpr = exprSingle();
				}
			}

			setState(362);
			_la = _input.LA(1);
			if (_la==KW_ORDER || _la==KW_STABLE) {
				{
				setState(361); orderByClause();
				}
			}

			setState(364); match(KW_RETURN);
			setState(365); ((FlworExprContext)_localctx).returnExpr = exprSingle();
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

	public static class ForClauseContext extends ParserRuleContext {
		public ForVarContext forVar;
		public List<ForVarContext> vars = new ArrayList<ForVarContext>();
		public ForVarContext forVar(int i) {
			return getRuleContext(ForVarContext.class,i);
		}
		public List<ForVarContext> forVar() {
			return getRuleContexts(ForVarContext.class);
		}
		public ForClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterForClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitForClause(this);
		}
	}

	public final ForClauseContext forClause() throws RecognitionException {
		ForClauseContext _localctx = new ForClauseContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_forClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(367); match(KW_FOR);
			setState(368); ((ForClauseContext)_localctx).forVar = forVar();
			((ForClauseContext)_localctx).vars.add(((ForClauseContext)_localctx).forVar);
			setState(373);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(369); match(COMMA);
				setState(370); ((ForClauseContext)_localctx).forVar = forVar();
				((ForClauseContext)_localctx).vars.add(((ForClauseContext)_localctx).forVar);
				}
				}
				setState(375);
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

	public static class ForVarContext extends ParserRuleContext {
		public QNameContext name;
		public TypeDeclarationContext type;
		public QNameContext pvar;
		public ExprSingleContext in;
		public List<QNameContext> qName() {
			return getRuleContexts(QNameContext.class);
		}
		public TypeDeclarationContext typeDeclaration() {
			return getRuleContext(TypeDeclarationContext.class,0);
		}
		public QNameContext qName(int i) {
			return getRuleContext(QNameContext.class,i);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public ForVarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forVar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterForVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitForVar(this);
		}
	}

	public final ForVarContext forVar() throws RecognitionException {
		ForVarContext _localctx = new ForVarContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_forVar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(376); match(DOLLAR);
			setState(377); ((ForVarContext)_localctx).name = qName();
			setState(379);
			_la = _input.LA(1);
			if (_la==KW_AS) {
				{
				setState(378); ((ForVarContext)_localctx).type = typeDeclaration();
				}
			}

			setState(384);
			_la = _input.LA(1);
			if (_la==KW_AT) {
				{
				setState(381); match(KW_AT);
				setState(382); match(DOLLAR);
				setState(383); ((ForVarContext)_localctx).pvar = qName();
				}
			}

			setState(386); match(KW_IN);
			setState(387); ((ForVarContext)_localctx).in = exprSingle();
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

	public static class LetClauseContext extends ParserRuleContext {
		public LetVarContext letVar;
		public List<LetVarContext> vars = new ArrayList<LetVarContext>();
		public LetVarContext letVar(int i) {
			return getRuleContext(LetVarContext.class,i);
		}
		public List<LetVarContext> letVar() {
			return getRuleContexts(LetVarContext.class);
		}
		public LetClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_letClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterLetClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitLetClause(this);
		}
	}

	public final LetClauseContext letClause() throws RecognitionException {
		LetClauseContext _localctx = new LetClauseContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_letClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(389); match(KW_LET);
			setState(390); ((LetClauseContext)_localctx).letVar = letVar();
			((LetClauseContext)_localctx).vars.add(((LetClauseContext)_localctx).letVar);
			setState(395);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(391); match(COMMA);
				setState(392); ((LetClauseContext)_localctx).letVar = letVar();
				((LetClauseContext)_localctx).vars.add(((LetClauseContext)_localctx).letVar);
				}
				}
				setState(397);
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

	public static class LetVarContext extends ParserRuleContext {
		public QNameContext name;
		public TypeDeclarationContext type;
		public ExprSingleContext value;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public TypeDeclarationContext typeDeclaration() {
			return getRuleContext(TypeDeclarationContext.class,0);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public LetVarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_letVar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterLetVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitLetVar(this);
		}
	}

	public final LetVarContext letVar() throws RecognitionException {
		LetVarContext _localctx = new LetVarContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_letVar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(398); match(DOLLAR);
			setState(399); ((LetVarContext)_localctx).name = qName();
			setState(401);
			_la = _input.LA(1);
			if (_la==KW_AS) {
				{
				setState(400); ((LetVarContext)_localctx).type = typeDeclaration();
				}
			}

			setState(403); match(COLON_EQ);
			setState(404); ((LetVarContext)_localctx).value = exprSingle();
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

	public static class OrderByClauseContext extends ParserRuleContext {
		public OrderSpecContext orderSpec;
		public List<OrderSpecContext> specs = new ArrayList<OrderSpecContext>();
		public OrderSpecContext orderSpec(int i) {
			return getRuleContext(OrderSpecContext.class,i);
		}
		public List<OrderSpecContext> orderSpec() {
			return getRuleContexts(OrderSpecContext.class);
		}
		public OrderByClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderByClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterOrderByClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitOrderByClause(this);
		}
	}

	public final OrderByClauseContext orderByClause() throws RecognitionException {
		OrderByClauseContext _localctx = new OrderByClauseContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_orderByClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(407);
			_la = _input.LA(1);
			if (_la==KW_STABLE) {
				{
				setState(406); match(KW_STABLE);
				}
			}

			setState(409); match(KW_ORDER);
			setState(410); match(KW_BY);
			setState(411); ((OrderByClauseContext)_localctx).orderSpec = orderSpec();
			((OrderByClauseContext)_localctx).specs.add(((OrderByClauseContext)_localctx).orderSpec);
			setState(416);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(412); match(COMMA);
				setState(413); ((OrderByClauseContext)_localctx).orderSpec = orderSpec();
				((OrderByClauseContext)_localctx).specs.add(((OrderByClauseContext)_localctx).orderSpec);
				}
				}
				setState(418);
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

	public static class OrderSpecContext extends ParserRuleContext {
		public ExprSingleContext value;
		public Token order;
		public Token empty;
		public StringLiteralContext collation;
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public OrderSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterOrderSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitOrderSpec(this);
		}
	}

	public final OrderSpecContext orderSpec() throws RecognitionException {
		OrderSpecContext _localctx = new OrderSpecContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_orderSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(419); ((OrderSpecContext)_localctx).value = exprSingle();
			setState(421);
			_la = _input.LA(1);
			if (_la==KW_ASCENDING || _la==KW_DESCENDING) {
				{
				setState(420);
				((OrderSpecContext)_localctx).order = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_ASCENDING || _la==KW_DESCENDING) ) {
					((OrderSpecContext)_localctx).order = (Token)_errHandler.recoverInline(this);
				}
				consume();
				}
			}

			setState(425);
			_la = _input.LA(1);
			if (_la==KW_EMPTY) {
				{
				setState(423); match(KW_EMPTY);
				setState(424);
				((OrderSpecContext)_localctx).empty = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==KW_GREATEST || _la==KW_LEAST) ) {
					((OrderSpecContext)_localctx).empty = (Token)_errHandler.recoverInline(this);
				}
				consume();
				}
			}

			setState(429);
			_la = _input.LA(1);
			if (_la==KW_COLLATION) {
				{
				setState(427); match(KW_COLLATION);
				setState(428); ((OrderSpecContext)_localctx).collation = stringLiteral();
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

	public static class QuantifiedExprContext extends ParserRuleContext {
		public Token quantifier;
		public QuantifiedVarContext quantifiedVar;
		public List<QuantifiedVarContext> vars = new ArrayList<QuantifiedVarContext>();
		public ExprSingleContext value;
		public QuantifiedVarContext quantifiedVar(int i) {
			return getRuleContext(QuantifiedVarContext.class,i);
		}
		public List<QuantifiedVarContext> quantifiedVar() {
			return getRuleContexts(QuantifiedVarContext.class);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public QuantifiedExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quantifiedExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterQuantifiedExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitQuantifiedExpr(this);
		}
	}

	public final QuantifiedExprContext quantifiedExpr() throws RecognitionException {
		QuantifiedExprContext _localctx = new QuantifiedExprContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_quantifiedExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			((QuantifiedExprContext)_localctx).quantifier = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==KW_EVERY || _la==KW_SOME) ) {
				((QuantifiedExprContext)_localctx).quantifier = (Token)_errHandler.recoverInline(this);
			}
			consume();
			setState(432); ((QuantifiedExprContext)_localctx).quantifiedVar = quantifiedVar();
			((QuantifiedExprContext)_localctx).vars.add(((QuantifiedExprContext)_localctx).quantifiedVar);
			setState(437);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(433); match(COMMA);
				setState(434); ((QuantifiedExprContext)_localctx).quantifiedVar = quantifiedVar();
				((QuantifiedExprContext)_localctx).vars.add(((QuantifiedExprContext)_localctx).quantifiedVar);
				}
				}
				setState(439);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(440); match(KW_SATISFIES);
			setState(441); ((QuantifiedExprContext)_localctx).value = exprSingle();
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

	public static class QuantifiedVarContext extends ParserRuleContext {
		public QNameContext name;
		public TypeDeclarationContext type;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public TypeDeclarationContext typeDeclaration() {
			return getRuleContext(TypeDeclarationContext.class,0);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public QuantifiedVarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quantifiedVar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterQuantifiedVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitQuantifiedVar(this);
		}
	}

	public final QuantifiedVarContext quantifiedVar() throws RecognitionException {
		QuantifiedVarContext _localctx = new QuantifiedVarContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_quantifiedVar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(443); match(DOLLAR);
			setState(444); ((QuantifiedVarContext)_localctx).name = qName();
			setState(446);
			_la = _input.LA(1);
			if (_la==KW_AS) {
				{
				setState(445); ((QuantifiedVarContext)_localctx).type = typeDeclaration();
				}
			}

			setState(448); match(KW_IN);
			setState(449); exprSingle();
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

	public static class TypeswitchExprContext extends ParserRuleContext {
		public ExprContext switchExpr;
		public CaseClauseContext clauses;
		public QNameContext var;
		public ExprSingleContext returnExpr;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public List<CaseClauseContext> caseClause() {
			return getRuleContexts(CaseClauseContext.class);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public CaseClauseContext caseClause(int i) {
			return getRuleContext(CaseClauseContext.class,i);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public TypeswitchExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeswitchExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterTypeswitchExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitTypeswitchExpr(this);
		}
	}

	public final TypeswitchExprContext typeswitchExpr() throws RecognitionException {
		TypeswitchExprContext _localctx = new TypeswitchExprContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_typeswitchExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451); match(KW_TYPESWITCH);
			setState(452); match(LPAREN);
			setState(453); ((TypeswitchExprContext)_localctx).switchExpr = expr();
			setState(454); match(RPAREN);
			setState(456); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(455); ((TypeswitchExprContext)_localctx).clauses = caseClause();
				}
				}
				setState(458); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==KW_CASE );
			setState(460); match(KW_DEFAULT);
			setState(463);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(461); match(DOLLAR);
				setState(462); ((TypeswitchExprContext)_localctx).var = qName();
				}
			}

			setState(465); match(KW_RETURN);
			setState(466); ((TypeswitchExprContext)_localctx).returnExpr = exprSingle();
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

	public static class CaseClauseContext extends ParserRuleContext {
		public QNameContext var;
		public SequenceTypeContext type;
		public ExprSingleContext returnExpr;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public SequenceTypeContext sequenceType() {
			return getRuleContext(SequenceTypeContext.class,0);
		}
		public ExprSingleContext exprSingle() {
			return getRuleContext(ExprSingleContext.class,0);
		}
		public CaseClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_caseClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCaseClause(this);
		}
	}

	public final CaseClauseContext caseClause() throws RecognitionException {
		CaseClauseContext _localctx = new CaseClauseContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_caseClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468); match(KW_CASE);
			setState(473);
			_la = _input.LA(1);
			if (_la==DOLLAR) {
				{
				setState(469); match(DOLLAR);
				setState(470); ((CaseClauseContext)_localctx).var = qName();
				setState(471); match(KW_AS);
				}
			}

			setState(475); ((CaseClauseContext)_localctx).type = sequenceType();
			setState(476); match(KW_RETURN);
			setState(477); ((CaseClauseContext)_localctx).returnExpr = exprSingle();
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

	public static class IfExprContext extends ParserRuleContext {
		public ExprContext conditionExpr;
		public ExprSingleContext thenExpr;
		public ExprSingleContext elseExpr;
		public ExprSingleContext exprSingle(int i) {
			return getRuleContext(ExprSingleContext.class,i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<ExprSingleContext> exprSingle() {
			return getRuleContexts(ExprSingleContext.class);
		}
		public IfExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterIfExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitIfExpr(this);
		}
	}

	public final IfExprContext ifExpr() throws RecognitionException {
		IfExprContext _localctx = new IfExprContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_ifExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(479); match(KW_IF);
			setState(480); match(LPAREN);
			setState(481); ((IfExprContext)_localctx).conditionExpr = expr();
			setState(482); match(RPAREN);
			setState(483); match(KW_THEN);
			setState(484); ((IfExprContext)_localctx).thenExpr = exprSingle();
			setState(485); match(KW_ELSE);
			setState(486); ((IfExprContext)_localctx).elseExpr = exprSingle();
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

	public static class OrExprContext extends ParserRuleContext {
		public OrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orExpr; }
	 
		public OrExprContext() { }
		public void copyFrom(OrExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CastableContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public SingleTypeContext r;
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public CastableContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCastable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCastable(this);
		}
	}
	public static class RootedPathContext extends OrExprContext {
		public RelativePathExprContext relativePathExpr() {
			return getRuleContext(RelativePathExprContext.class,0);
		}
		public RootedPathContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterRootedPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitRootedPath(this);
		}
	}
	public static class MultContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public MultContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitMult(this);
		}
	}
	public static class RangeContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public RangeContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitRange(this);
		}
	}
	public static class OrContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public OrContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitOr(this);
		}
	}
	public static class InstanceOfContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public SequenceTypeContext r;
		public SequenceTypeContext sequenceType() {
			return getRuleContext(SequenceTypeContext.class,0);
		}
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public InstanceOfContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterInstanceOf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitInstanceOf(this);
		}
	}
	public static class AddContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public AddContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAdd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAdd(this);
		}
	}
	public static class RelativeContext extends OrExprContext {
		public RelativePathExprContext relativePathExpr() {
			return getRuleContext(RelativePathExprContext.class,0);
		}
		public RelativeContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterRelative(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitRelative(this);
		}
	}
	public static class ValidateContext extends OrExprContext {
		public Token vMode;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ValidateContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterValidate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitValidate(this);
		}
	}
	public static class ComparisonContext extends OrExprContext {
		public OrExprContext l;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public ComparisonContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitComparison(this);
		}
	}
	public static class AndContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public AndContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAnd(this);
		}
	}
	public static class UnaryContext extends OrExprContext {
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public UnaryContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitUnary(this);
		}
	}
	public static class ExtensionContext extends OrExprContext {
		public List<TerminalNode> PRAGMA() { return getTokens(XQueryParser.PRAGMA); }
		public TerminalNode PRAGMA(int i) {
			return getToken(XQueryParser.PRAGMA, i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExtensionContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterExtension(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitExtension(this);
		}
	}
	public static class CastContext extends OrExprContext {
		public Token op;
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public CastContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCast(this);
		}
	}
	public static class IntersectContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public IntersectContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterIntersect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitIntersect(this);
		}
	}
	public static class UnionContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public OrExprContext r;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public TerminalNode KW_UNION() { return getToken(XQueryParser.KW_UNION, 0); }
		public UnionContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterUnion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitUnion(this);
		}
	}
	public static class AllDescPathContext extends OrExprContext {
		public RelativePathExprContext relativePathExpr() {
			return getRuleContext(RelativePathExprContext.class,0);
		}
		public AllDescPathContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAllDescPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAllDescPath(this);
		}
	}
	public static class TreatContext extends OrExprContext {
		public OrExprContext l;
		public Token op;
		public SequenceTypeContext r;
		public SequenceTypeContext sequenceType() {
			return getRuleContext(SequenceTypeContext.class,0);
		}
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public TreatContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterTreat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitTreat(this);
		}
	}

	public final OrExprContext orExpr() throws RecognitionException {
		return orExpr(0);
	}

	private OrExprContext orExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		OrExprContext _localctx = new OrExprContext(_ctx, _parentState);
		OrExprContext _prevctx = _localctx;
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_orExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(516);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				_localctx = new UnaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(489);
				_la = _input.LA(1);
				if ( !(_la==PLUS || _la==MINUS) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(490); orExpr(18);
				}
				break;
			case 2:
				{
				_localctx = new ValidateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(491); match(KW_VALIDATE);
				setState(493);
				_la = _input.LA(1);
				if (_la==KW_LAX || _la==KW_STRICT) {
					{
					setState(492);
					((ValidateContext)_localctx).vMode = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==KW_LAX || _la==KW_STRICT) ) {
						((ValidateContext)_localctx).vMode = (Token)_errHandler.recoverInline(this);
					}
					consume();
					}
				}

				setState(495); match(LBRACE);
				setState(496); expr();
				setState(497); match(RBRACE);
				}
				break;
			case 3:
				{
				_localctx = new ExtensionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(500); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(499); match(PRAGMA);
					}
					}
					setState(502); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==PRAGMA );
				setState(504); match(LBRACE);
				setState(506);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << DecimalLiteral) | (1L << DoubleLiteral) | (1L << Quot) | (1L << Apos) | (1L << COMMENT) | (1L << PI) | (1L << PRAGMA) | (1L << LPAREN) | (1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DOT) | (1L << DDOT) | (1L << SLASH) | (1L << DSLASH) | (1L << LANGLE) | (1L << AT) | (1L << DOLLAR) | (1L << KW_ANCESTOR) | (1L << KW_ANCESTOR_OR_SELF) | (1L << KW_AND) | (1L << KW_AS) | (1L << KW_ASCENDING) | (1L << KW_AT) | (1L << KW_ATTRIBUTE) | (1L << KW_BASE_URI) | (1L << KW_BOUNDARY_SPACE) | (1L << KW_BY) | (1L << KW_CASE) | (1L << KW_CAST) | (1L << KW_CASTABLE) | (1L << KW_CHILD) | (1L << KW_COLLATION) | (1L << KW_COMMENT) | (1L << KW_CONSTRUCTION) | (1L << KW_COPY_NS) | (1L << KW_DECLARE) | (1L << KW_DEFAULT) | (1L << KW_DESCENDANT) | (1L << KW_DESCENDANT_OR_SELF) | (1L << KW_DESCENDING) | (1L << KW_DIV) | (1L << KW_DOCUMENT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (KW_DOCUMENT_NODE - 64)) | (1L << (KW_ELEMENT - 64)) | (1L << (KW_ELSE - 64)) | (1L << (KW_EMPTY - 64)) | (1L << (KW_EMPTY_SEQUENCE - 64)) | (1L << (KW_ENCODING - 64)) | (1L << (KW_EQ - 64)) | (1L << (KW_EVERY - 64)) | (1L << (KW_EXCEPT - 64)) | (1L << (KW_EXTERNAL - 64)) | (1L << (KW_FOLLOWING - 64)) | (1L << (KW_FOLLOWING_SIBLING - 64)) | (1L << (KW_FOR - 64)) | (1L << (KW_FUNCTION - 64)) | (1L << (KW_GE - 64)) | (1L << (KW_GREATEST - 64)) | (1L << (KW_GT - 64)) | (1L << (KW_IDIV - 64)) | (1L << (KW_IF - 64)) | (1L << (KW_IMPORT - 64)) | (1L << (KW_IN - 64)) | (1L << (KW_INHERIT - 64)) | (1L << (KW_INSTANCE - 64)) | (1L << (KW_INTERSECT - 64)) | (1L << (KW_IS - 64)) | (1L << (KW_ITEM - 64)) | (1L << (KW_LAX - 64)) | (1L << (KW_LE - 64)) | (1L << (KW_LEAST - 64)) | (1L << (KW_LET - 64)) | (1L << (KW_LT - 64)) | (1L << (KW_MOD - 64)) | (1L << (KW_MODULE - 64)) | (1L << (KW_NAMESPACE - 64)) | (1L << (KW_NE - 64)) | (1L << (KW_NO_INHERIT - 64)) | (1L << (KW_NO_PRESERVE - 64)) | (1L << (KW_NODE - 64)) | (1L << (KW_OF - 64)) | (1L << (KW_OPTION - 64)) | (1L << (KW_OR - 64)) | (1L << (KW_ORDER - 64)) | (1L << (KW_ORDERED - 64)) | (1L << (KW_ORDERING - 64)) | (1L << (KW_PARENT - 64)) | (1L << (KW_PRECEDING - 64)) | (1L << (KW_PRECEDING_SIBLING - 64)) | (1L << (KW_PRESERVE - 64)) | (1L << (KW_PI - 64)) | (1L << (KW_RETURN - 64)) | (1L << (KW_SATISFIES - 64)) | (1L << (KW_SCHEMA - 64)) | (1L << (KW_SCHEMA_ATTR - 64)) | (1L << (KW_SCHEMA_ELEM - 64)) | (1L << (KW_SELF - 64)) | (1L << (KW_SOME - 64)) | (1L << (KW_STABLE - 64)) | (1L << (KW_STRICT - 64)) | (1L << (KW_STRIP - 64)) | (1L << (KW_TEXT - 64)) | (1L << (KW_THEN - 64)) | (1L << (KW_TO - 64)) | (1L << (KW_TREAT - 64)) | (1L << (KW_TYPESWITCH - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (KW_UNION - 128)) | (1L << (KW_UNORDERED - 128)) | (1L << (KW_VALIDATE - 128)) | (1L << (KW_VARIABLE - 128)) | (1L << (KW_VERSION - 128)) | (1L << (KW_WHERE - 128)) | (1L << (KW_XQUERY - 128)) | (1L << (FullQName - 128)) | (1L << (NCNameWithLocalWildcard - 128)) | (1L << (NCNameWithPrefixWildcard - 128)) | (1L << (NCName - 128)))) != 0)) {
					{
					setState(505); expr();
					}
				}

				setState(508); match(RBRACE);
				}
				break;
			case 4:
				{
				_localctx = new RootedPathContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(509); match(SLASH);
				setState(511);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(510); relativePathExpr();
					}
					break;
				}
				}
				break;
			case 5:
				{
				_localctx = new AllDescPathContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(513); match(DSLASH);
				setState(514); relativePathExpr();
				}
				break;
			case 6:
				{
				_localctx = new RelativeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(515); relativePathExpr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(580);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(578);
					switch ( getInterpreter().adaptivePredict(_input,48,_ctx) ) {
					case 1:
						{
						_localctx = new IntersectContext(new OrExprContext(_parentctx, _parentState));
						((IntersectContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(518);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(519);
						((IntersectContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==KW_EXCEPT || _la==KW_INTERSECT) ) {
							((IntersectContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(520); ((IntersectContext)_localctx).r = orExpr(14);
						}
						break;
					case 2:
						{
						_localctx = new UnionContext(new OrExprContext(_parentctx, _parentState));
						((UnionContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(521);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(522);
						((UnionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==VBAR || _la==KW_UNION) ) {
							((UnionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(523); ((UnionContext)_localctx).r = orExpr(13);
						}
						break;
					case 3:
						{
						_localctx = new MultContext(new OrExprContext(_parentctx, _parentState));
						((MultContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(524);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(525);
						((MultContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==STAR || _la==KW_DIV || _la==KW_IDIV || _la==KW_MOD) ) {
							((MultContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(526); ((MultContext)_localctx).r = orExpr(12);
						}
						break;
					case 4:
						{
						_localctx = new AddContext(new OrExprContext(_parentctx, _parentState));
						((AddContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(527);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(528);
						((AddContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((AddContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(529); ((AddContext)_localctx).r = orExpr(11);
						}
						break;
					case 5:
						{
						_localctx = new RangeContext(new OrExprContext(_parentctx, _parentState));
						((RangeContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(530);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(531); ((RangeContext)_localctx).op = match(KW_TO);
						setState(532); ((RangeContext)_localctx).r = orExpr(10);
						}
						break;
					case 6:
						{
						_localctx = new ComparisonContext(new OrExprContext(_parentctx, _parentState));
						((ComparisonContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(533);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(553);
						switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
						case 1:
							{
							setState(534); match(KW_EQ);
							}
							break;
						case 2:
							{
							setState(535); match(KW_NE);
							}
							break;
						case 3:
							{
							setState(536); match(KW_LT);
							}
							break;
						case 4:
							{
							setState(537); match(KW_LE);
							}
							break;
						case 5:
							{
							setState(538); match(KW_GT);
							}
							break;
						case 6:
							{
							setState(539); match(KW_GE);
							}
							break;
						case 7:
							{
							setState(540); match(EQUAL);
							}
							break;
						case 8:
							{
							setState(541); match(NOT_EQUAL);
							}
							break;
						case 9:
							{
							setState(542); match(LANGLE);
							}
							break;
						case 10:
							{
							setState(543); match(LANGLE);
							setState(544); match(EQUAL);
							}
							break;
						case 11:
							{
							setState(545); match(RANGLE);
							}
							break;
						case 12:
							{
							setState(546); match(RANGLE);
							setState(547); match(EQUAL);
							}
							break;
						case 13:
							{
							setState(548); match(KW_IS);
							}
							break;
						case 14:
							{
							setState(549); match(LANGLE);
							setState(550); match(LANGLE);
							}
							break;
						case 15:
							{
							setState(551); match(RANGLE);
							setState(552); match(RANGLE);
							}
							break;
						}
						setState(555); ((ComparisonContext)_localctx).r = orExpr(9);
						}
						break;
					case 7:
						{
						_localctx = new AndContext(new OrExprContext(_parentctx, _parentState));
						((AndContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(556);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(557); ((AndContext)_localctx).op = match(KW_AND);
						setState(558); ((AndContext)_localctx).r = orExpr(8);
						}
						break;
					case 8:
						{
						_localctx = new OrContext(new OrExprContext(_parentctx, _parentState));
						((OrContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(559);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(560); ((OrContext)_localctx).op = match(KW_OR);
						setState(561); ((OrContext)_localctx).r = orExpr(7);
						}
						break;
					case 9:
						{
						_localctx = new CastContext(new OrExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(562);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(563); ((CastContext)_localctx).op = match(KW_CAST);
						setState(564); match(KW_AS);
						setState(565); singleType();
						}
						break;
					case 10:
						{
						_localctx = new CastableContext(new OrExprContext(_parentctx, _parentState));
						((CastableContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(566);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(567); ((CastableContext)_localctx).op = match(KW_CASTABLE);
						setState(568); match(KW_AS);
						setState(569); ((CastableContext)_localctx).r = singleType();
						}
						break;
					case 11:
						{
						_localctx = new TreatContext(new OrExprContext(_parentctx, _parentState));
						((TreatContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(570);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(571); ((TreatContext)_localctx).op = match(KW_TREAT);
						setState(572); match(KW_AS);
						setState(573); ((TreatContext)_localctx).r = sequenceType();
						}
						break;
					case 12:
						{
						_localctx = new InstanceOfContext(new OrExprContext(_parentctx, _parentState));
						((InstanceOfContext)_localctx).l = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
						setState(574);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(575); ((InstanceOfContext)_localctx).op = match(KW_INSTANCE);
						setState(576); match(KW_OF);
						setState(577); ((InstanceOfContext)_localctx).r = sequenceType();
						}
						break;
					}
					} 
				}
				setState(582);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PrimaryExprContext extends ParserRuleContext {
		public PrimaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpr; }
	 
		public PrimaryExprContext() { }
		public void copyFrom(PrimaryExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FuncallContext extends PrimaryExprContext {
		public ExprSingleContext exprSingle;
		public List<ExprSingleContext> args = new ArrayList<ExprSingleContext>();
		public ExprSingleContext exprSingle(int i) {
			return getRuleContext(ExprSingleContext.class,i);
		}
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public List<ExprSingleContext> exprSingle() {
			return getRuleContexts(ExprSingleContext.class);
		}
		public FuncallContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterFuncall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitFuncall(this);
		}
	}
	public static class CtorContext extends PrimaryExprContext {
		public ConstructorContext constructor() {
			return getRuleContext(ConstructorContext.class,0);
		}
		public CtorContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCtor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCtor(this);
		}
	}
	public static class IntegerContext extends PrimaryExprContext {
		public TerminalNode IntegerLiteral() { return getToken(XQueryParser.IntegerLiteral, 0); }
		public IntegerContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitInteger(this);
		}
	}
	public static class OrderedContext extends PrimaryExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public OrderedContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterOrdered(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitOrdered(this);
		}
	}
	public static class VarContext extends PrimaryExprContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public VarContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitVar(this);
		}
	}
	public static class StringContext extends PrimaryExprContext {
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public StringContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitString(this);
		}
	}
	public static class CurrentContext extends PrimaryExprContext {
		public CurrentContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCurrent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCurrent(this);
		}
	}
	public static class ParenContext extends PrimaryExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterParen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitParen(this);
		}
	}
	public static class DoubleContext extends PrimaryExprContext {
		public TerminalNode DoubleLiteral() { return getToken(XQueryParser.DoubleLiteral, 0); }
		public DoubleContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDouble(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDouble(this);
		}
	}
	public static class UnorderedContext extends PrimaryExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public UnorderedContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterUnordered(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitUnordered(this);
		}
	}
	public static class DecimalContext extends PrimaryExprContext {
		public TerminalNode DecimalLiteral() { return getToken(XQueryParser.DecimalLiteral, 0); }
		public DecimalContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDecimal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDecimal(this);
		}
	}

	public final PrimaryExprContext primaryExpr() throws RecognitionException {
		PrimaryExprContext _localctx = new PrimaryExprContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_primaryExpr);
		int _la;
		try {
			setState(620);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				_localctx = new IntegerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(583); match(IntegerLiteral);
				}
				break;
			case 2:
				_localctx = new DecimalContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(584); match(DecimalLiteral);
				}
				break;
			case 3:
				_localctx = new DoubleContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(585); match(DoubleLiteral);
				}
				break;
			case 4:
				_localctx = new StringContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(586); stringLiteral();
				}
				break;
			case 5:
				_localctx = new VarContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(587); match(DOLLAR);
				setState(588); qName();
				}
				break;
			case 6:
				_localctx = new ParenContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(589); match(LPAREN);
				setState(591);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << DecimalLiteral) | (1L << DoubleLiteral) | (1L << Quot) | (1L << Apos) | (1L << COMMENT) | (1L << PI) | (1L << PRAGMA) | (1L << LPAREN) | (1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DOT) | (1L << DDOT) | (1L << SLASH) | (1L << DSLASH) | (1L << LANGLE) | (1L << AT) | (1L << DOLLAR) | (1L << KW_ANCESTOR) | (1L << KW_ANCESTOR_OR_SELF) | (1L << KW_AND) | (1L << KW_AS) | (1L << KW_ASCENDING) | (1L << KW_AT) | (1L << KW_ATTRIBUTE) | (1L << KW_BASE_URI) | (1L << KW_BOUNDARY_SPACE) | (1L << KW_BY) | (1L << KW_CASE) | (1L << KW_CAST) | (1L << KW_CASTABLE) | (1L << KW_CHILD) | (1L << KW_COLLATION) | (1L << KW_COMMENT) | (1L << KW_CONSTRUCTION) | (1L << KW_COPY_NS) | (1L << KW_DECLARE) | (1L << KW_DEFAULT) | (1L << KW_DESCENDANT) | (1L << KW_DESCENDANT_OR_SELF) | (1L << KW_DESCENDING) | (1L << KW_DIV) | (1L << KW_DOCUMENT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (KW_DOCUMENT_NODE - 64)) | (1L << (KW_ELEMENT - 64)) | (1L << (KW_ELSE - 64)) | (1L << (KW_EMPTY - 64)) | (1L << (KW_EMPTY_SEQUENCE - 64)) | (1L << (KW_ENCODING - 64)) | (1L << (KW_EQ - 64)) | (1L << (KW_EVERY - 64)) | (1L << (KW_EXCEPT - 64)) | (1L << (KW_EXTERNAL - 64)) | (1L << (KW_FOLLOWING - 64)) | (1L << (KW_FOLLOWING_SIBLING - 64)) | (1L << (KW_FOR - 64)) | (1L << (KW_FUNCTION - 64)) | (1L << (KW_GE - 64)) | (1L << (KW_GREATEST - 64)) | (1L << (KW_GT - 64)) | (1L << (KW_IDIV - 64)) | (1L << (KW_IF - 64)) | (1L << (KW_IMPORT - 64)) | (1L << (KW_IN - 64)) | (1L << (KW_INHERIT - 64)) | (1L << (KW_INSTANCE - 64)) | (1L << (KW_INTERSECT - 64)) | (1L << (KW_IS - 64)) | (1L << (KW_ITEM - 64)) | (1L << (KW_LAX - 64)) | (1L << (KW_LE - 64)) | (1L << (KW_LEAST - 64)) | (1L << (KW_LET - 64)) | (1L << (KW_LT - 64)) | (1L << (KW_MOD - 64)) | (1L << (KW_MODULE - 64)) | (1L << (KW_NAMESPACE - 64)) | (1L << (KW_NE - 64)) | (1L << (KW_NO_INHERIT - 64)) | (1L << (KW_NO_PRESERVE - 64)) | (1L << (KW_NODE - 64)) | (1L << (KW_OF - 64)) | (1L << (KW_OPTION - 64)) | (1L << (KW_OR - 64)) | (1L << (KW_ORDER - 64)) | (1L << (KW_ORDERED - 64)) | (1L << (KW_ORDERING - 64)) | (1L << (KW_PARENT - 64)) | (1L << (KW_PRECEDING - 64)) | (1L << (KW_PRECEDING_SIBLING - 64)) | (1L << (KW_PRESERVE - 64)) | (1L << (KW_PI - 64)) | (1L << (KW_RETURN - 64)) | (1L << (KW_SATISFIES - 64)) | (1L << (KW_SCHEMA - 64)) | (1L << (KW_SCHEMA_ATTR - 64)) | (1L << (KW_SCHEMA_ELEM - 64)) | (1L << (KW_SELF - 64)) | (1L << (KW_SOME - 64)) | (1L << (KW_STABLE - 64)) | (1L << (KW_STRICT - 64)) | (1L << (KW_STRIP - 64)) | (1L << (KW_TEXT - 64)) | (1L << (KW_THEN - 64)) | (1L << (KW_TO - 64)) | (1L << (KW_TREAT - 64)) | (1L << (KW_TYPESWITCH - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (KW_UNION - 128)) | (1L << (KW_UNORDERED - 128)) | (1L << (KW_VALIDATE - 128)) | (1L << (KW_VARIABLE - 128)) | (1L << (KW_VERSION - 128)) | (1L << (KW_WHERE - 128)) | (1L << (KW_XQUERY - 128)) | (1L << (FullQName - 128)) | (1L << (NCNameWithLocalWildcard - 128)) | (1L << (NCNameWithPrefixWildcard - 128)) | (1L << (NCName - 128)))) != 0)) {
					{
					setState(590); expr();
					}
				}

				setState(593); match(RPAREN);
				}
				break;
			case 7:
				_localctx = new CurrentContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(594); match(DOT);
				}
				break;
			case 8:
				_localctx = new FuncallContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(595); functionName();
				setState(596); match(LPAREN);
				setState(605);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << DecimalLiteral) | (1L << DoubleLiteral) | (1L << Quot) | (1L << Apos) | (1L << COMMENT) | (1L << PI) | (1L << PRAGMA) | (1L << LPAREN) | (1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DOT) | (1L << DDOT) | (1L << SLASH) | (1L << DSLASH) | (1L << LANGLE) | (1L << AT) | (1L << DOLLAR) | (1L << KW_ANCESTOR) | (1L << KW_ANCESTOR_OR_SELF) | (1L << KW_AND) | (1L << KW_AS) | (1L << KW_ASCENDING) | (1L << KW_AT) | (1L << KW_ATTRIBUTE) | (1L << KW_BASE_URI) | (1L << KW_BOUNDARY_SPACE) | (1L << KW_BY) | (1L << KW_CASE) | (1L << KW_CAST) | (1L << KW_CASTABLE) | (1L << KW_CHILD) | (1L << KW_COLLATION) | (1L << KW_COMMENT) | (1L << KW_CONSTRUCTION) | (1L << KW_COPY_NS) | (1L << KW_DECLARE) | (1L << KW_DEFAULT) | (1L << KW_DESCENDANT) | (1L << KW_DESCENDANT_OR_SELF) | (1L << KW_DESCENDING) | (1L << KW_DIV) | (1L << KW_DOCUMENT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (KW_DOCUMENT_NODE - 64)) | (1L << (KW_ELEMENT - 64)) | (1L << (KW_ELSE - 64)) | (1L << (KW_EMPTY - 64)) | (1L << (KW_EMPTY_SEQUENCE - 64)) | (1L << (KW_ENCODING - 64)) | (1L << (KW_EQ - 64)) | (1L << (KW_EVERY - 64)) | (1L << (KW_EXCEPT - 64)) | (1L << (KW_EXTERNAL - 64)) | (1L << (KW_FOLLOWING - 64)) | (1L << (KW_FOLLOWING_SIBLING - 64)) | (1L << (KW_FOR - 64)) | (1L << (KW_FUNCTION - 64)) | (1L << (KW_GE - 64)) | (1L << (KW_GREATEST - 64)) | (1L << (KW_GT - 64)) | (1L << (KW_IDIV - 64)) | (1L << (KW_IF - 64)) | (1L << (KW_IMPORT - 64)) | (1L << (KW_IN - 64)) | (1L << (KW_INHERIT - 64)) | (1L << (KW_INSTANCE - 64)) | (1L << (KW_INTERSECT - 64)) | (1L << (KW_IS - 64)) | (1L << (KW_ITEM - 64)) | (1L << (KW_LAX - 64)) | (1L << (KW_LE - 64)) | (1L << (KW_LEAST - 64)) | (1L << (KW_LET - 64)) | (1L << (KW_LT - 64)) | (1L << (KW_MOD - 64)) | (1L << (KW_MODULE - 64)) | (1L << (KW_NAMESPACE - 64)) | (1L << (KW_NE - 64)) | (1L << (KW_NO_INHERIT - 64)) | (1L << (KW_NO_PRESERVE - 64)) | (1L << (KW_NODE - 64)) | (1L << (KW_OF - 64)) | (1L << (KW_OPTION - 64)) | (1L << (KW_OR - 64)) | (1L << (KW_ORDER - 64)) | (1L << (KW_ORDERED - 64)) | (1L << (KW_ORDERING - 64)) | (1L << (KW_PARENT - 64)) | (1L << (KW_PRECEDING - 64)) | (1L << (KW_PRECEDING_SIBLING - 64)) | (1L << (KW_PRESERVE - 64)) | (1L << (KW_PI - 64)) | (1L << (KW_RETURN - 64)) | (1L << (KW_SATISFIES - 64)) | (1L << (KW_SCHEMA - 64)) | (1L << (KW_SCHEMA_ATTR - 64)) | (1L << (KW_SCHEMA_ELEM - 64)) | (1L << (KW_SELF - 64)) | (1L << (KW_SOME - 64)) | (1L << (KW_STABLE - 64)) | (1L << (KW_STRICT - 64)) | (1L << (KW_STRIP - 64)) | (1L << (KW_TEXT - 64)) | (1L << (KW_THEN - 64)) | (1L << (KW_TO - 64)) | (1L << (KW_TREAT - 64)) | (1L << (KW_TYPESWITCH - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (KW_UNION - 128)) | (1L << (KW_UNORDERED - 128)) | (1L << (KW_VALIDATE - 128)) | (1L << (KW_VARIABLE - 128)) | (1L << (KW_VERSION - 128)) | (1L << (KW_WHERE - 128)) | (1L << (KW_XQUERY - 128)) | (1L << (FullQName - 128)) | (1L << (NCNameWithLocalWildcard - 128)) | (1L << (NCNameWithPrefixWildcard - 128)) | (1L << (NCName - 128)))) != 0)) {
					{
					setState(597); ((FuncallContext)_localctx).exprSingle = exprSingle();
					((FuncallContext)_localctx).args.add(((FuncallContext)_localctx).exprSingle);
					setState(602);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(598); match(COMMA);
						setState(599); ((FuncallContext)_localctx).exprSingle = exprSingle();
						((FuncallContext)_localctx).args.add(((FuncallContext)_localctx).exprSingle);
						}
						}
						setState(604);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(607); match(RPAREN);
				}
				break;
			case 9:
				_localctx = new OrderedContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(609); match(KW_ORDERED);
				setState(610); match(LBRACE);
				setState(611); expr();
				setState(612); match(RBRACE);
				}
				break;
			case 10:
				_localctx = new UnorderedContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(614); match(KW_UNORDERED);
				setState(615); match(LBRACE);
				setState(616); expr();
				setState(617); match(RBRACE);
				}
				break;
			case 11:
				_localctx = new CtorContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(619); constructor();
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

	public static class RelativePathExprContext extends ParserRuleContext {
		public Token sep;
		public StepExprContext stepExpr(int i) {
			return getRuleContext(StepExprContext.class,i);
		}
		public List<StepExprContext> stepExpr() {
			return getRuleContexts(StepExprContext.class);
		}
		public RelativePathExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relativePathExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterRelativePathExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitRelativePathExpr(this);
		}
	}

	public final RelativePathExprContext relativePathExpr() throws RecognitionException {
		RelativePathExprContext _localctx = new RelativePathExprContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_relativePathExpr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(622); stepExpr();
			setState(627);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(623);
					((RelativePathExprContext)_localctx).sep = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==SLASH || _la==DSLASH) ) {
						((RelativePathExprContext)_localctx).sep = (Token)_errHandler.recoverInline(this);
					}
					consume();
					setState(624); stepExpr();
					}
					} 
				}
				setState(629);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,54,_ctx);
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

	public static class StepExprContext extends ParserRuleContext {
		public FilterExprContext filterExpr() {
			return getRuleContext(FilterExprContext.class,0);
		}
		public AxisStepContext axisStep() {
			return getRuleContext(AxisStepContext.class,0);
		}
		public StepExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stepExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterStepExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitStepExpr(this);
		}
	}

	public final StepExprContext stepExpr() throws RecognitionException {
		StepExprContext _localctx = new StepExprContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_stepExpr);
		try {
			setState(632);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(630); axisStep();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(631); filterExpr();
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

	public static class AxisStepContext extends ParserRuleContext {
		public PredicateListContext predicateList() {
			return getRuleContext(PredicateListContext.class,0);
		}
		public ReverseStepContext reverseStep() {
			return getRuleContext(ReverseStepContext.class,0);
		}
		public ForwardStepContext forwardStep() {
			return getRuleContext(ForwardStepContext.class,0);
		}
		public AxisStepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_axisStep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAxisStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAxisStep(this);
		}
	}

	public final AxisStepContext axisStep() throws RecognitionException {
		AxisStepContext _localctx = new AxisStepContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_axisStep);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(636);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				{
				setState(634); reverseStep();
				}
				break;
			case 2:
				{
				setState(635); forwardStep();
				}
				break;
			}
			setState(638); predicateList();
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

	public static class ForwardStepContext extends ParserRuleContext {
		public ForwardAxisContext forwardAxis() {
			return getRuleContext(ForwardAxisContext.class,0);
		}
		public NodeTestContext nodeTest() {
			return getRuleContext(NodeTestContext.class,0);
		}
		public AbbrevForwardStepContext abbrevForwardStep() {
			return getRuleContext(AbbrevForwardStepContext.class,0);
		}
		public ForwardStepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forwardStep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterForwardStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitForwardStep(this);
		}
	}

	public final ForwardStepContext forwardStep() throws RecognitionException {
		ForwardStepContext _localctx = new ForwardStepContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_forwardStep);
		try {
			setState(644);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(640); forwardAxis();
				setState(641); nodeTest();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(643); abbrevForwardStep();
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

	public static class ForwardAxisContext extends ParserRuleContext {
		public ForwardAxisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forwardAxis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterForwardAxis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitForwardAxis(this);
		}
	}

	public final ForwardAxisContext forwardAxis() throws RecognitionException {
		ForwardAxisContext _localctx = new ForwardAxisContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_forwardAxis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(646);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << KW_ATTRIBUTE) | (1L << KW_CHILD) | (1L << KW_DESCENDANT) | (1L << KW_DESCENDANT_OR_SELF))) != 0) || ((((_la - 74)) & ~0x3f) == 0 && ((1L << (_la - 74)) & ((1L << (KW_FOLLOWING - 74)) | (1L << (KW_FOLLOWING_SIBLING - 74)) | (1L << (KW_SELF - 74)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(647); match(COLON);
			setState(648); match(COLON);
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

	public static class AbbrevForwardStepContext extends ParserRuleContext {
		public NodeTestContext nodeTest() {
			return getRuleContext(NodeTestContext.class,0);
		}
		public AbbrevForwardStepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_abbrevForwardStep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAbbrevForwardStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAbbrevForwardStep(this);
		}
	}

	public final AbbrevForwardStepContext abbrevForwardStep() throws RecognitionException {
		AbbrevForwardStepContext _localctx = new AbbrevForwardStepContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_abbrevForwardStep);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651);
			_la = _input.LA(1);
			if (_la==AT) {
				{
				setState(650); match(AT);
				}
			}

			setState(653); nodeTest();
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

	public static class ReverseStepContext extends ParserRuleContext {
		public ReverseAxisContext reverseAxis() {
			return getRuleContext(ReverseAxisContext.class,0);
		}
		public AbbrevReverseStepContext abbrevReverseStep() {
			return getRuleContext(AbbrevReverseStepContext.class,0);
		}
		public NodeTestContext nodeTest() {
			return getRuleContext(NodeTestContext.class,0);
		}
		public ReverseStepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reverseStep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterReverseStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitReverseStep(this);
		}
	}

	public final ReverseStepContext reverseStep() throws RecognitionException {
		ReverseStepContext _localctx = new ReverseStepContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_reverseStep);
		try {
			setState(659);
			switch (_input.LA(1)) {
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
				enterOuterAlt(_localctx, 1);
				{
				setState(655); reverseAxis();
				setState(656); nodeTest();
				}
				break;
			case DDOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(658); abbrevReverseStep();
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

	public static class ReverseAxisContext extends ParserRuleContext {
		public ReverseAxisContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_reverseAxis; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterReverseAxis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitReverseAxis(this);
		}
	}

	public final ReverseAxisContext reverseAxis() throws RecognitionException {
		ReverseAxisContext _localctx = new ReverseAxisContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_reverseAxis);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(661);
			_la = _input.LA(1);
			if ( !(_la==KW_ANCESTOR || _la==KW_ANCESTOR_OR_SELF || ((((_la - 108)) & ~0x3f) == 0 && ((1L << (_la - 108)) & ((1L << (KW_PARENT - 108)) | (1L << (KW_PRECEDING - 108)) | (1L << (KW_PRECEDING_SIBLING - 108)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(662); match(COLON);
			setState(663); match(COLON);
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

	public static class AbbrevReverseStepContext extends ParserRuleContext {
		public AbbrevReverseStepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_abbrevReverseStep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAbbrevReverseStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAbbrevReverseStep(this);
		}
	}

	public final AbbrevReverseStepContext abbrevReverseStep() throws RecognitionException {
		AbbrevReverseStepContext _localctx = new AbbrevReverseStepContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_abbrevReverseStep);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(665); match(DDOT);
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

	public static class NodeTestContext extends ParserRuleContext {
		public NameTestContext nameTest() {
			return getRuleContext(NameTestContext.class,0);
		}
		public KindTestContext kindTest() {
			return getRuleContext(KindTestContext.class,0);
		}
		public NodeTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nodeTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterNodeTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitNodeTest(this);
		}
	}

	public final NodeTestContext nodeTest() throws RecognitionException {
		NodeTestContext _localctx = new NodeTestContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_nodeTest);
		try {
			setState(669);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(667); nameTest();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(668); kindTest();
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

	public static class NameTestContext extends ParserRuleContext {
		public NameTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameTest; }
	 
		public NameTestContext() { }
		public void copyFrom(NameTestContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AllNamesContext extends NameTestContext {
		public AllNamesContext(NameTestContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAllNames(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAllNames(this);
		}
	}
	public static class ExactMatchContext extends NameTestContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public ExactMatchContext(NameTestContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterExactMatch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitExactMatch(this);
		}
	}
	public static class AllWithNSContext extends NameTestContext {
		public TerminalNode NCNameWithLocalWildcard() { return getToken(XQueryParser.NCNameWithLocalWildcard, 0); }
		public AllWithNSContext(NameTestContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAllWithNS(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAllWithNS(this);
		}
	}
	public static class AllWithLocalContext extends NameTestContext {
		public TerminalNode NCNameWithPrefixWildcard() { return getToken(XQueryParser.NCNameWithPrefixWildcard, 0); }
		public AllWithLocalContext(NameTestContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAllWithLocal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAllWithLocal(this);
		}
	}

	public final NameTestContext nameTest() throws RecognitionException {
		NameTestContext _localctx = new NameTestContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_nameTest);
		try {
			setState(675);
			switch (_input.LA(1)) {
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_AND:
			case KW_AS:
			case KW_ASCENDING:
			case KW_AT:
			case KW_ATTRIBUTE:
			case KW_BASE_URI:
			case KW_BOUNDARY_SPACE:
			case KW_BY:
			case KW_CASE:
			case KW_CAST:
			case KW_CASTABLE:
			case KW_CHILD:
			case KW_COLLATION:
			case KW_COMMENT:
			case KW_CONSTRUCTION:
			case KW_COPY_NS:
			case KW_DECLARE:
			case KW_DEFAULT:
			case KW_DESCENDANT:
			case KW_DESCENDANT_OR_SELF:
			case KW_DESCENDING:
			case KW_DIV:
			case KW_DOCUMENT:
			case KW_DOCUMENT_NODE:
			case KW_ELEMENT:
			case KW_ELSE:
			case KW_EMPTY:
			case KW_EMPTY_SEQUENCE:
			case KW_ENCODING:
			case KW_EQ:
			case KW_EVERY:
			case KW_EXCEPT:
			case KW_EXTERNAL:
			case KW_FOLLOWING:
			case KW_FOLLOWING_SIBLING:
			case KW_FOR:
			case KW_FUNCTION:
			case KW_GE:
			case KW_GREATEST:
			case KW_GT:
			case KW_IDIV:
			case KW_IF:
			case KW_IMPORT:
			case KW_IN:
			case KW_INHERIT:
			case KW_INSTANCE:
			case KW_INTERSECT:
			case KW_IS:
			case KW_ITEM:
			case KW_LAX:
			case KW_LE:
			case KW_LEAST:
			case KW_LET:
			case KW_LT:
			case KW_MOD:
			case KW_MODULE:
			case KW_NAMESPACE:
			case KW_NE:
			case KW_NO_INHERIT:
			case KW_NO_PRESERVE:
			case KW_NODE:
			case KW_OF:
			case KW_OPTION:
			case KW_OR:
			case KW_ORDER:
			case KW_ORDERED:
			case KW_ORDERING:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
			case KW_PRESERVE:
			case KW_PI:
			case KW_RETURN:
			case KW_SATISFIES:
			case KW_SCHEMA:
			case KW_SCHEMA_ATTR:
			case KW_SCHEMA_ELEM:
			case KW_SELF:
			case KW_SOME:
			case KW_STABLE:
			case KW_STRICT:
			case KW_STRIP:
			case KW_TEXT:
			case KW_THEN:
			case KW_TO:
			case KW_TREAT:
			case KW_TYPESWITCH:
			case KW_UNION:
			case KW_UNORDERED:
			case KW_VALIDATE:
			case KW_VARIABLE:
			case KW_VERSION:
			case KW_WHERE:
			case KW_XQUERY:
			case FullQName:
			case NCName:
				_localctx = new ExactMatchContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(671); qName();
				}
				break;
			case STAR:
				_localctx = new AllNamesContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(672); match(STAR);
				}
				break;
			case NCNameWithLocalWildcard:
				_localctx = new AllWithNSContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(673); match(NCNameWithLocalWildcard);
				}
				break;
			case NCNameWithPrefixWildcard:
				_localctx = new AllWithLocalContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(674); match(NCNameWithPrefixWildcard);
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

	public static class FilterExprContext extends ParserRuleContext {
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public PredicateListContext predicateList() {
			return getRuleContext(PredicateListContext.class,0);
		}
		public FilterExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filterExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterFilterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitFilterExpr(this);
		}
	}

	public final FilterExprContext filterExpr() throws RecognitionException {
		FilterExprContext _localctx = new FilterExprContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_filterExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(677); primaryExpr();
			setState(678); predicateList();
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

	public static class PredicateListContext extends ParserRuleContext {
		public ExprContext expr;
		public List<ExprContext> predicates = new ArrayList<ExprContext>();
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public PredicateListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicateList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterPredicateList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitPredicateList(this);
		}
	}

	public final PredicateListContext predicateList() throws RecognitionException {
		PredicateListContext _localctx = new PredicateListContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_predicateList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(686);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(680); match(LBRACKET);
					setState(681); ((PredicateListContext)_localctx).expr = expr();
					((PredicateListContext)_localctx).predicates.add(((PredicateListContext)_localctx).expr);
					setState(682); match(RBRACKET);
					}
					} 
				}
				setState(688);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
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

	public static class ConstructorContext extends ParserRuleContext {
		public ComputedConstructorContext computedConstructor() {
			return getRuleContext(ComputedConstructorContext.class,0);
		}
		public DirectConstructorContext directConstructor() {
			return getRuleContext(DirectConstructorContext.class,0);
		}
		public ConstructorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitConstructor(this);
		}
	}

	public final ConstructorContext constructor() throws RecognitionException {
		ConstructorContext _localctx = new ConstructorContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_constructor);
		try {
			setState(691);
			switch (_input.LA(1)) {
			case COMMENT:
			case PI:
			case LANGLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(689); directConstructor();
				}
				break;
			case KW_ATTRIBUTE:
			case KW_COMMENT:
			case KW_DOCUMENT:
			case KW_ELEMENT:
			case KW_PI:
			case KW_TEXT:
				enterOuterAlt(_localctx, 2);
				{
				setState(690); computedConstructor();
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

	public static class DirectConstructorContext extends ParserRuleContext {
		public TerminalNode PI() { return getToken(XQueryParser.PI, 0); }
		public DirElemConstructorOpenCloseContext dirElemConstructorOpenClose() {
			return getRuleContext(DirElemConstructorOpenCloseContext.class,0);
		}
		public TerminalNode COMMENT() { return getToken(XQueryParser.COMMENT, 0); }
		public DirElemConstructorSingleTagContext dirElemConstructorSingleTag() {
			return getRuleContext(DirElemConstructorSingleTagContext.class,0);
		}
		public DirectConstructorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directConstructor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDirectConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDirectConstructor(this);
		}
	}

	public final DirectConstructorContext directConstructor() throws RecognitionException {
		DirectConstructorContext _localctx = new DirectConstructorContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_directConstructor);
		int _la;
		try {
			setState(696);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(693); dirElemConstructorOpenClose();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(694); dirElemConstructorSingleTag();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(695);
				_la = _input.LA(1);
				if ( !(_la==COMMENT || _la==PI) ) {
				_errHandler.recoverInline(this);
				}
				consume();
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

	public static class DirElemConstructorOpenCloseContext extends ParserRuleContext {
		public QNameContext openName;
		public Token endOpen;
		public Token startClose;
		public Token slashClose;
		public QNameContext closeName;
		public List<QNameContext> qName() {
			return getRuleContexts(QNameContext.class);
		}
		public QNameContext qName(int i) {
			return getRuleContext(QNameContext.class,i);
		}
		public DirAttributeListContext dirAttributeList() {
			return getRuleContext(DirAttributeListContext.class,0);
		}
		public DirElemContentContext dirElemContent(int i) {
			return getRuleContext(DirElemContentContext.class,i);
		}
		public List<DirElemContentContext> dirElemContent() {
			return getRuleContexts(DirElemContentContext.class);
		}
		public DirElemConstructorOpenCloseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dirElemConstructorOpenClose; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDirElemConstructorOpenClose(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDirElemConstructorOpenClose(this);
		}
	}

	public final DirElemConstructorOpenCloseContext dirElemConstructorOpenClose() throws RecognitionException {
		DirElemConstructorOpenCloseContext _localctx = new DirElemConstructorOpenCloseContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_dirElemConstructorOpenClose);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(698); match(LANGLE);
			setState(699); ((DirElemConstructorOpenCloseContext)_localctx).openName = qName();
			setState(700); dirAttributeList();
			setState(701); ((DirElemConstructorOpenCloseContext)_localctx).endOpen = match(RANGLE);
			setState(705);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(702); dirElemContent();
					}
					} 
				}
				setState(707);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,65,_ctx);
			}
			setState(708); ((DirElemConstructorOpenCloseContext)_localctx).startClose = match(LANGLE);
			setState(709); ((DirElemConstructorOpenCloseContext)_localctx).slashClose = match(SLASH);
			setState(710); ((DirElemConstructorOpenCloseContext)_localctx).closeName = qName();
			setState(711); match(RANGLE);
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

	public static class DirElemConstructorSingleTagContext extends ParserRuleContext {
		public QNameContext openName;
		public Token slashClose;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public DirAttributeListContext dirAttributeList() {
			return getRuleContext(DirAttributeListContext.class,0);
		}
		public DirElemConstructorSingleTagContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dirElemConstructorSingleTag; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDirElemConstructorSingleTag(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDirElemConstructorSingleTag(this);
		}
	}

	public final DirElemConstructorSingleTagContext dirElemConstructorSingleTag() throws RecognitionException {
		DirElemConstructorSingleTagContext _localctx = new DirElemConstructorSingleTagContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_dirElemConstructorSingleTag);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(713); match(LANGLE);
			setState(714); ((DirElemConstructorSingleTagContext)_localctx).openName = qName();
			setState(715); dirAttributeList();
			setState(716); ((DirElemConstructorSingleTagContext)_localctx).slashClose = match(SLASH);
			setState(717); match(RANGLE);
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

	public static class DirAttributeListContext extends ParserRuleContext {
		public List<QNameContext> qName() {
			return getRuleContexts(QNameContext.class);
		}
		public List<DirAttributeValueContext> dirAttributeValue() {
			return getRuleContexts(DirAttributeValueContext.class);
		}
		public QNameContext qName(int i) {
			return getRuleContext(QNameContext.class,i);
		}
		public DirAttributeValueContext dirAttributeValue(int i) {
			return getRuleContext(DirAttributeValueContext.class,i);
		}
		public DirAttributeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dirAttributeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDirAttributeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDirAttributeList(this);
		}
	}

	public final DirAttributeListContext dirAttributeList() throws RecognitionException {
		DirAttributeListContext _localctx = new DirAttributeListContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_dirAttributeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(725);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 39)) & ~0x3f) == 0 && ((1L << (_la - 39)) & ((1L << (KW_ANCESTOR - 39)) | (1L << (KW_ANCESTOR_OR_SELF - 39)) | (1L << (KW_AND - 39)) | (1L << (KW_AS - 39)) | (1L << (KW_ASCENDING - 39)) | (1L << (KW_AT - 39)) | (1L << (KW_ATTRIBUTE - 39)) | (1L << (KW_BASE_URI - 39)) | (1L << (KW_BOUNDARY_SPACE - 39)) | (1L << (KW_BY - 39)) | (1L << (KW_CASE - 39)) | (1L << (KW_CAST - 39)) | (1L << (KW_CASTABLE - 39)) | (1L << (KW_CHILD - 39)) | (1L << (KW_COLLATION - 39)) | (1L << (KW_COMMENT - 39)) | (1L << (KW_CONSTRUCTION - 39)) | (1L << (KW_COPY_NS - 39)) | (1L << (KW_DECLARE - 39)) | (1L << (KW_DEFAULT - 39)) | (1L << (KW_DESCENDANT - 39)) | (1L << (KW_DESCENDANT_OR_SELF - 39)) | (1L << (KW_DESCENDING - 39)) | (1L << (KW_DIV - 39)) | (1L << (KW_DOCUMENT - 39)) | (1L << (KW_DOCUMENT_NODE - 39)) | (1L << (KW_ELEMENT - 39)) | (1L << (KW_ELSE - 39)) | (1L << (KW_EMPTY - 39)) | (1L << (KW_EMPTY_SEQUENCE - 39)) | (1L << (KW_ENCODING - 39)) | (1L << (KW_EQ - 39)) | (1L << (KW_EVERY - 39)) | (1L << (KW_EXCEPT - 39)) | (1L << (KW_EXTERNAL - 39)) | (1L << (KW_FOLLOWING - 39)) | (1L << (KW_FOLLOWING_SIBLING - 39)) | (1L << (KW_FOR - 39)) | (1L << (KW_FUNCTION - 39)) | (1L << (KW_GE - 39)) | (1L << (KW_GREATEST - 39)) | (1L << (KW_GT - 39)) | (1L << (KW_IDIV - 39)) | (1L << (KW_IF - 39)) | (1L << (KW_IMPORT - 39)) | (1L << (KW_IN - 39)) | (1L << (KW_INHERIT - 39)) | (1L << (KW_INSTANCE - 39)) | (1L << (KW_INTERSECT - 39)) | (1L << (KW_IS - 39)) | (1L << (KW_ITEM - 39)) | (1L << (KW_LAX - 39)) | (1L << (KW_LE - 39)) | (1L << (KW_LEAST - 39)) | (1L << (KW_LET - 39)) | (1L << (KW_LT - 39)) | (1L << (KW_MOD - 39)) | (1L << (KW_MODULE - 39)) | (1L << (KW_NAMESPACE - 39)) | (1L << (KW_NE - 39)) | (1L << (KW_NO_INHERIT - 39)) | (1L << (KW_NO_PRESERVE - 39)) | (1L << (KW_NODE - 39)) | (1L << (KW_OF - 39)))) != 0) || ((((_la - 103)) & ~0x3f) == 0 && ((1L << (_la - 103)) & ((1L << (KW_OPTION - 103)) | (1L << (KW_OR - 103)) | (1L << (KW_ORDER - 103)) | (1L << (KW_ORDERED - 103)) | (1L << (KW_ORDERING - 103)) | (1L << (KW_PARENT - 103)) | (1L << (KW_PRECEDING - 103)) | (1L << (KW_PRECEDING_SIBLING - 103)) | (1L << (KW_PRESERVE - 103)) | (1L << (KW_PI - 103)) | (1L << (KW_RETURN - 103)) | (1L << (KW_SATISFIES - 103)) | (1L << (KW_SCHEMA - 103)) | (1L << (KW_SCHEMA_ATTR - 103)) | (1L << (KW_SCHEMA_ELEM - 103)) | (1L << (KW_SELF - 103)) | (1L << (KW_SOME - 103)) | (1L << (KW_STABLE - 103)) | (1L << (KW_STRICT - 103)) | (1L << (KW_STRIP - 103)) | (1L << (KW_TEXT - 103)) | (1L << (KW_THEN - 103)) | (1L << (KW_TO - 103)) | (1L << (KW_TREAT - 103)) | (1L << (KW_TYPESWITCH - 103)) | (1L << (KW_UNION - 103)) | (1L << (KW_UNORDERED - 103)) | (1L << (KW_VALIDATE - 103)) | (1L << (KW_VARIABLE - 103)) | (1L << (KW_VERSION - 103)) | (1L << (KW_WHERE - 103)) | (1L << (KW_XQUERY - 103)) | (1L << (FullQName - 103)) | (1L << (NCName - 103)))) != 0)) {
				{
				{
				setState(719); qName();
				setState(720); match(EQUAL);
				setState(721); dirAttributeValue();
				}
				}
				setState(727);
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

	public static class DirAttributeValueContext extends ParserRuleContext {
		public TerminalNode Apos(int i) {
			return getToken(XQueryParser.Apos, i);
		}
		public List<CommonContentContext> commonContent() {
			return getRuleContexts(CommonContentContext.class);
		}
		public CommonContentContext commonContent(int i) {
			return getRuleContext(CommonContentContext.class,i);
		}
		public NoQuotesNoBracesNoAmpNoLAngContext noQuotesNoBracesNoAmpNoLAng(int i) {
			return getRuleContext(NoQuotesNoBracesNoAmpNoLAngContext.class,i);
		}
		public List<NoQuotesNoBracesNoAmpNoLAngContext> noQuotesNoBracesNoAmpNoLAng() {
			return getRuleContexts(NoQuotesNoBracesNoAmpNoLAngContext.class);
		}
		public List<TerminalNode> Apos() { return getTokens(XQueryParser.Apos); }
		public List<TerminalNode> Quot() { return getTokens(XQueryParser.Quot); }
		public TerminalNode Quot(int i) {
			return getToken(XQueryParser.Quot, i);
		}
		public DirAttributeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dirAttributeValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDirAttributeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDirAttributeValue(this);
		}
	}

	public final DirAttributeValueContext dirAttributeValue() throws RecognitionException {
		DirAttributeValueContext _localctx = new DirAttributeValueContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_dirAttributeValue);
		try {
			int _alt;
			setState(752);
			switch (_input.LA(1)) {
			case Quot:
				enterOuterAlt(_localctx, 1);
				{
				setState(728); match(Quot);
				setState(736);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						setState(734);
						switch (_input.LA(1)) {
						case PredefinedEntityRef:
						case CharRef:
						case LBRACE:
						case RBRACE:
							{
							setState(729); commonContent();
							}
							break;
						case Quot:
							{
							setState(730); match(Quot);
							setState(731); match(Quot);
							}
							break;
						case Apos:
							{
							setState(732); match(Apos);
							}
							break;
						case IntegerLiteral:
						case DecimalLiteral:
						case DoubleLiteral:
						case PRAGMA:
						case EQUAL:
						case NOT_EQUAL:
						case LPAREN:
						case RPAREN:
						case LBRACKET:
						case RBRACKET:
						case STAR:
						case PLUS:
						case MINUS:
						case COMMA:
						case DOT:
						case DDOT:
						case COLON:
						case COLON_EQ:
						case SEMICOLON:
						case SLASH:
						case DSLASH:
						case VBAR:
						case RANGLE:
						case QUESTION:
						case AT:
						case DOLLAR:
						case KW_ANCESTOR:
						case KW_ANCESTOR_OR_SELF:
						case KW_AND:
						case KW_AS:
						case KW_ASCENDING:
						case KW_AT:
						case KW_ATTRIBUTE:
						case KW_BASE_URI:
						case KW_BOUNDARY_SPACE:
						case KW_BY:
						case KW_CASE:
						case KW_CAST:
						case KW_CASTABLE:
						case KW_CHILD:
						case KW_COLLATION:
						case KW_COMMENT:
						case KW_CONSTRUCTION:
						case KW_COPY_NS:
						case KW_DECLARE:
						case KW_DEFAULT:
						case KW_DESCENDANT:
						case KW_DESCENDANT_OR_SELF:
						case KW_DESCENDING:
						case KW_DIV:
						case KW_DOCUMENT:
						case KW_DOCUMENT_NODE:
						case KW_ELEMENT:
						case KW_ELSE:
						case KW_EMPTY:
						case KW_EMPTY_SEQUENCE:
						case KW_ENCODING:
						case KW_EQ:
						case KW_EVERY:
						case KW_EXCEPT:
						case KW_EXTERNAL:
						case KW_FOLLOWING:
						case KW_FOLLOWING_SIBLING:
						case KW_FOR:
						case KW_FUNCTION:
						case KW_GE:
						case KW_GREATEST:
						case KW_GT:
						case KW_IDIV:
						case KW_IF:
						case KW_IMPORT:
						case KW_IN:
						case KW_INHERIT:
						case KW_INSTANCE:
						case KW_INTERSECT:
						case KW_IS:
						case KW_ITEM:
						case KW_LAX:
						case KW_LE:
						case KW_LEAST:
						case KW_LET:
						case KW_LT:
						case KW_MOD:
						case KW_MODULE:
						case KW_NAMESPACE:
						case KW_NE:
						case KW_NO_INHERIT:
						case KW_NO_PRESERVE:
						case KW_NODE:
						case KW_OF:
						case KW_OPTION:
						case KW_OR:
						case KW_ORDER:
						case KW_ORDERED:
						case KW_ORDERING:
						case KW_PARENT:
						case KW_PRECEDING:
						case KW_PRECEDING_SIBLING:
						case KW_PRESERVE:
						case KW_PI:
						case KW_RETURN:
						case KW_SATISFIES:
						case KW_SCHEMA:
						case KW_SCHEMA_ATTR:
						case KW_SCHEMA_ELEM:
						case KW_SELF:
						case KW_SOME:
						case KW_STABLE:
						case KW_STRICT:
						case KW_STRIP:
						case KW_TEXT:
						case KW_THEN:
						case KW_TO:
						case KW_TREAT:
						case KW_TYPESWITCH:
						case KW_UNION:
						case KW_UNORDERED:
						case KW_VALIDATE:
						case KW_VARIABLE:
						case KW_VERSION:
						case KW_WHERE:
						case KW_XQUERY:
						case FullQName:
						case NCNameWithLocalWildcard:
						case NCNameWithPrefixWildcard:
						case NCName:
						case ContentChar:
							{
							setState(733); noQuotesNoBracesNoAmpNoLAng();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(738);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,68,_ctx);
				}
				setState(739); match(Quot);
				}
				break;
			case Apos:
				enterOuterAlt(_localctx, 2);
				{
				setState(740); match(Apos);
				setState(748);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						setState(746);
						switch (_input.LA(1)) {
						case PredefinedEntityRef:
						case CharRef:
						case LBRACE:
						case RBRACE:
							{
							setState(741); commonContent();
							}
							break;
						case Apos:
							{
							setState(742); match(Apos);
							setState(743); match(Apos);
							}
							break;
						case Quot:
							{
							setState(744); match(Quot);
							}
							break;
						case IntegerLiteral:
						case DecimalLiteral:
						case DoubleLiteral:
						case PRAGMA:
						case EQUAL:
						case NOT_EQUAL:
						case LPAREN:
						case RPAREN:
						case LBRACKET:
						case RBRACKET:
						case STAR:
						case PLUS:
						case MINUS:
						case COMMA:
						case DOT:
						case DDOT:
						case COLON:
						case COLON_EQ:
						case SEMICOLON:
						case SLASH:
						case DSLASH:
						case VBAR:
						case RANGLE:
						case QUESTION:
						case AT:
						case DOLLAR:
						case KW_ANCESTOR:
						case KW_ANCESTOR_OR_SELF:
						case KW_AND:
						case KW_AS:
						case KW_ASCENDING:
						case KW_AT:
						case KW_ATTRIBUTE:
						case KW_BASE_URI:
						case KW_BOUNDARY_SPACE:
						case KW_BY:
						case KW_CASE:
						case KW_CAST:
						case KW_CASTABLE:
						case KW_CHILD:
						case KW_COLLATION:
						case KW_COMMENT:
						case KW_CONSTRUCTION:
						case KW_COPY_NS:
						case KW_DECLARE:
						case KW_DEFAULT:
						case KW_DESCENDANT:
						case KW_DESCENDANT_OR_SELF:
						case KW_DESCENDING:
						case KW_DIV:
						case KW_DOCUMENT:
						case KW_DOCUMENT_NODE:
						case KW_ELEMENT:
						case KW_ELSE:
						case KW_EMPTY:
						case KW_EMPTY_SEQUENCE:
						case KW_ENCODING:
						case KW_EQ:
						case KW_EVERY:
						case KW_EXCEPT:
						case KW_EXTERNAL:
						case KW_FOLLOWING:
						case KW_FOLLOWING_SIBLING:
						case KW_FOR:
						case KW_FUNCTION:
						case KW_GE:
						case KW_GREATEST:
						case KW_GT:
						case KW_IDIV:
						case KW_IF:
						case KW_IMPORT:
						case KW_IN:
						case KW_INHERIT:
						case KW_INSTANCE:
						case KW_INTERSECT:
						case KW_IS:
						case KW_ITEM:
						case KW_LAX:
						case KW_LE:
						case KW_LEAST:
						case KW_LET:
						case KW_LT:
						case KW_MOD:
						case KW_MODULE:
						case KW_NAMESPACE:
						case KW_NE:
						case KW_NO_INHERIT:
						case KW_NO_PRESERVE:
						case KW_NODE:
						case KW_OF:
						case KW_OPTION:
						case KW_OR:
						case KW_ORDER:
						case KW_ORDERED:
						case KW_ORDERING:
						case KW_PARENT:
						case KW_PRECEDING:
						case KW_PRECEDING_SIBLING:
						case KW_PRESERVE:
						case KW_PI:
						case KW_RETURN:
						case KW_SATISFIES:
						case KW_SCHEMA:
						case KW_SCHEMA_ATTR:
						case KW_SCHEMA_ELEM:
						case KW_SELF:
						case KW_SOME:
						case KW_STABLE:
						case KW_STRICT:
						case KW_STRIP:
						case KW_TEXT:
						case KW_THEN:
						case KW_TO:
						case KW_TREAT:
						case KW_TYPESWITCH:
						case KW_UNION:
						case KW_UNORDERED:
						case KW_VALIDATE:
						case KW_VARIABLE:
						case KW_VERSION:
						case KW_WHERE:
						case KW_XQUERY:
						case FullQName:
						case NCNameWithLocalWildcard:
						case NCNameWithPrefixWildcard:
						case NCName:
						case ContentChar:
							{
							setState(745); noQuotesNoBracesNoAmpNoLAng();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(750);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,70,_ctx);
				}
				setState(751); match(Apos);
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

	public static class DirElemContentContext extends ParserRuleContext {
		public TerminalNode CDATA() { return getToken(XQueryParser.CDATA, 0); }
		public CommonContentContext commonContent() {
			return getRuleContext(CommonContentContext.class,0);
		}
		public NoQuotesNoBracesNoAmpNoLAngContext noQuotesNoBracesNoAmpNoLAng() {
			return getRuleContext(NoQuotesNoBracesNoAmpNoLAngContext.class,0);
		}
		public DirectConstructorContext directConstructor() {
			return getRuleContext(DirectConstructorContext.class,0);
		}
		public TerminalNode Apos() { return getToken(XQueryParser.Apos, 0); }
		public TerminalNode Quot() { return getToken(XQueryParser.Quot, 0); }
		public DirElemContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dirElemContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDirElemContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDirElemContent(this);
		}
	}

	public final DirElemContentContext dirElemContent() throws RecognitionException {
		DirElemContentContext _localctx = new DirElemContentContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_dirElemContent);
		try {
			setState(760);
			switch (_input.LA(1)) {
			case COMMENT:
			case PI:
			case LANGLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(754); directConstructor();
				}
				break;
			case PredefinedEntityRef:
			case CharRef:
			case LBRACE:
			case RBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(755); commonContent();
				}
				break;
			case CDATA:
				enterOuterAlt(_localctx, 3);
				{
				setState(756); match(CDATA);
				}
				break;
			case Quot:
				enterOuterAlt(_localctx, 4);
				{
				setState(757); match(Quot);
				}
				break;
			case Apos:
				enterOuterAlt(_localctx, 5);
				{
				setState(758); match(Apos);
				}
				break;
			case IntegerLiteral:
			case DecimalLiteral:
			case DoubleLiteral:
			case PRAGMA:
			case EQUAL:
			case NOT_EQUAL:
			case LPAREN:
			case RPAREN:
			case LBRACKET:
			case RBRACKET:
			case STAR:
			case PLUS:
			case MINUS:
			case COMMA:
			case DOT:
			case DDOT:
			case COLON:
			case COLON_EQ:
			case SEMICOLON:
			case SLASH:
			case DSLASH:
			case VBAR:
			case RANGLE:
			case QUESTION:
			case AT:
			case DOLLAR:
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_AND:
			case KW_AS:
			case KW_ASCENDING:
			case KW_AT:
			case KW_ATTRIBUTE:
			case KW_BASE_URI:
			case KW_BOUNDARY_SPACE:
			case KW_BY:
			case KW_CASE:
			case KW_CAST:
			case KW_CASTABLE:
			case KW_CHILD:
			case KW_COLLATION:
			case KW_COMMENT:
			case KW_CONSTRUCTION:
			case KW_COPY_NS:
			case KW_DECLARE:
			case KW_DEFAULT:
			case KW_DESCENDANT:
			case KW_DESCENDANT_OR_SELF:
			case KW_DESCENDING:
			case KW_DIV:
			case KW_DOCUMENT:
			case KW_DOCUMENT_NODE:
			case KW_ELEMENT:
			case KW_ELSE:
			case KW_EMPTY:
			case KW_EMPTY_SEQUENCE:
			case KW_ENCODING:
			case KW_EQ:
			case KW_EVERY:
			case KW_EXCEPT:
			case KW_EXTERNAL:
			case KW_FOLLOWING:
			case KW_FOLLOWING_SIBLING:
			case KW_FOR:
			case KW_FUNCTION:
			case KW_GE:
			case KW_GREATEST:
			case KW_GT:
			case KW_IDIV:
			case KW_IF:
			case KW_IMPORT:
			case KW_IN:
			case KW_INHERIT:
			case KW_INSTANCE:
			case KW_INTERSECT:
			case KW_IS:
			case KW_ITEM:
			case KW_LAX:
			case KW_LE:
			case KW_LEAST:
			case KW_LET:
			case KW_LT:
			case KW_MOD:
			case KW_MODULE:
			case KW_NAMESPACE:
			case KW_NE:
			case KW_NO_INHERIT:
			case KW_NO_PRESERVE:
			case KW_NODE:
			case KW_OF:
			case KW_OPTION:
			case KW_OR:
			case KW_ORDER:
			case KW_ORDERED:
			case KW_ORDERING:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
			case KW_PRESERVE:
			case KW_PI:
			case KW_RETURN:
			case KW_SATISFIES:
			case KW_SCHEMA:
			case KW_SCHEMA_ATTR:
			case KW_SCHEMA_ELEM:
			case KW_SELF:
			case KW_SOME:
			case KW_STABLE:
			case KW_STRICT:
			case KW_STRIP:
			case KW_TEXT:
			case KW_THEN:
			case KW_TO:
			case KW_TREAT:
			case KW_TYPESWITCH:
			case KW_UNION:
			case KW_UNORDERED:
			case KW_VALIDATE:
			case KW_VARIABLE:
			case KW_VERSION:
			case KW_WHERE:
			case KW_XQUERY:
			case FullQName:
			case NCNameWithLocalWildcard:
			case NCNameWithPrefixWildcard:
			case NCName:
			case ContentChar:
				enterOuterAlt(_localctx, 6);
				{
				setState(759); noQuotesNoBracesNoAmpNoLAng();
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

	public static class CommonContentContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CharRef() { return getToken(XQueryParser.CharRef, 0); }
		public TerminalNode PredefinedEntityRef() { return getToken(XQueryParser.PredefinedEntityRef, 0); }
		public CommonContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commonContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCommonContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCommonContent(this);
		}
	}

	public final CommonContentContext commonContent() throws RecognitionException {
		CommonContentContext _localctx = new CommonContentContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_commonContent);
		int _la;
		try {
			setState(771);
			switch ( getInterpreter().adaptivePredict(_input,73,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(762);
				_la = _input.LA(1);
				if ( !(_la==PredefinedEntityRef || _la==CharRef) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(763); match(LBRACE);
				setState(764); match(LBRACE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(765); match(RBRACE);
				setState(766); match(RBRACE);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(767); match(LBRACE);
				setState(768); expr();
				setState(769); match(RBRACE);
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

	public static class ComputedConstructorContext extends ParserRuleContext {
		public ComputedConstructorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_computedConstructor; }
	 
		public ComputedConstructorContext() { }
		public void copyFrom(ComputedConstructorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TextConstructorContext extends ComputedConstructorContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TextConstructorContext(ComputedConstructorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterTextConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitTextConstructor(this);
		}
	}
	public static class DocConstructorContext extends ComputedConstructorContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DocConstructorContext(ComputedConstructorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDocConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDocConstructor(this);
		}
	}
	public static class PiConstructorContext extends ComputedConstructorContext {
		public NcNameContext piName;
		public ExprContext piExpr;
		public ExprContext contentExpr;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public NcNameContext ncName() {
			return getRuleContext(NcNameContext.class,0);
		}
		public PiConstructorContext(ComputedConstructorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterPiConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitPiConstructor(this);
		}
	}
	public static class AttrConstructorContext extends ComputedConstructorContext {
		public QNameContext attrName;
		public ExprContext attrExpr;
		public ExprContext contentExpr;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public AttrConstructorContext(ComputedConstructorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAttrConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAttrConstructor(this);
		}
	}
	public static class ElementConstructorContext extends ComputedConstructorContext {
		public QNameContext elementName;
		public ExprContext elementExpr;
		public ExprContext contentExpr;
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ElementConstructorContext(ComputedConstructorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterElementConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitElementConstructor(this);
		}
	}
	public static class CommentConstructorContext extends ComputedConstructorContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public CommentConstructorContext(ComputedConstructorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCommentConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCommentConstructor(this);
		}
	}

	public final ComputedConstructorContext computedConstructor() throws RecognitionException {
		ComputedConstructorContext _localctx = new ComputedConstructorContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_computedConstructor);
		int _la;
		try {
			setState(830);
			switch (_input.LA(1)) {
			case KW_DOCUMENT:
				_localctx = new DocConstructorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(773); match(KW_DOCUMENT);
				setState(774); match(LBRACE);
				setState(775); expr();
				setState(776); match(RBRACE);
				}
				break;
			case KW_ELEMENT:
				_localctx = new ElementConstructorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(778); match(KW_ELEMENT);
				setState(784);
				switch (_input.LA(1)) {
				case KW_ANCESTOR:
				case KW_ANCESTOR_OR_SELF:
				case KW_AND:
				case KW_AS:
				case KW_ASCENDING:
				case KW_AT:
				case KW_ATTRIBUTE:
				case KW_BASE_URI:
				case KW_BOUNDARY_SPACE:
				case KW_BY:
				case KW_CASE:
				case KW_CAST:
				case KW_CASTABLE:
				case KW_CHILD:
				case KW_COLLATION:
				case KW_COMMENT:
				case KW_CONSTRUCTION:
				case KW_COPY_NS:
				case KW_DECLARE:
				case KW_DEFAULT:
				case KW_DESCENDANT:
				case KW_DESCENDANT_OR_SELF:
				case KW_DESCENDING:
				case KW_DIV:
				case KW_DOCUMENT:
				case KW_DOCUMENT_NODE:
				case KW_ELEMENT:
				case KW_ELSE:
				case KW_EMPTY:
				case KW_EMPTY_SEQUENCE:
				case KW_ENCODING:
				case KW_EQ:
				case KW_EVERY:
				case KW_EXCEPT:
				case KW_EXTERNAL:
				case KW_FOLLOWING:
				case KW_FOLLOWING_SIBLING:
				case KW_FOR:
				case KW_FUNCTION:
				case KW_GE:
				case KW_GREATEST:
				case KW_GT:
				case KW_IDIV:
				case KW_IF:
				case KW_IMPORT:
				case KW_IN:
				case KW_INHERIT:
				case KW_INSTANCE:
				case KW_INTERSECT:
				case KW_IS:
				case KW_ITEM:
				case KW_LAX:
				case KW_LE:
				case KW_LEAST:
				case KW_LET:
				case KW_LT:
				case KW_MOD:
				case KW_MODULE:
				case KW_NAMESPACE:
				case KW_NE:
				case KW_NO_INHERIT:
				case KW_NO_PRESERVE:
				case KW_NODE:
				case KW_OF:
				case KW_OPTION:
				case KW_OR:
				case KW_ORDER:
				case KW_ORDERED:
				case KW_ORDERING:
				case KW_PARENT:
				case KW_PRECEDING:
				case KW_PRECEDING_SIBLING:
				case KW_PRESERVE:
				case KW_PI:
				case KW_RETURN:
				case KW_SATISFIES:
				case KW_SCHEMA:
				case KW_SCHEMA_ATTR:
				case KW_SCHEMA_ELEM:
				case KW_SELF:
				case KW_SOME:
				case KW_STABLE:
				case KW_STRICT:
				case KW_STRIP:
				case KW_TEXT:
				case KW_THEN:
				case KW_TO:
				case KW_TREAT:
				case KW_TYPESWITCH:
				case KW_UNION:
				case KW_UNORDERED:
				case KW_VALIDATE:
				case KW_VARIABLE:
				case KW_VERSION:
				case KW_WHERE:
				case KW_XQUERY:
				case FullQName:
				case NCName:
					{
					setState(779); ((ElementConstructorContext)_localctx).elementName = qName();
					}
					break;
				case LBRACE:
					{
					setState(780); match(LBRACE);
					setState(781); ((ElementConstructorContext)_localctx).elementExpr = expr();
					setState(782); match(RBRACE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(786); match(LBRACE);
				setState(788);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << DecimalLiteral) | (1L << DoubleLiteral) | (1L << Quot) | (1L << Apos) | (1L << COMMENT) | (1L << PI) | (1L << PRAGMA) | (1L << LPAREN) | (1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DOT) | (1L << DDOT) | (1L << SLASH) | (1L << DSLASH) | (1L << LANGLE) | (1L << AT) | (1L << DOLLAR) | (1L << KW_ANCESTOR) | (1L << KW_ANCESTOR_OR_SELF) | (1L << KW_AND) | (1L << KW_AS) | (1L << KW_ASCENDING) | (1L << KW_AT) | (1L << KW_ATTRIBUTE) | (1L << KW_BASE_URI) | (1L << KW_BOUNDARY_SPACE) | (1L << KW_BY) | (1L << KW_CASE) | (1L << KW_CAST) | (1L << KW_CASTABLE) | (1L << KW_CHILD) | (1L << KW_COLLATION) | (1L << KW_COMMENT) | (1L << KW_CONSTRUCTION) | (1L << KW_COPY_NS) | (1L << KW_DECLARE) | (1L << KW_DEFAULT) | (1L << KW_DESCENDANT) | (1L << KW_DESCENDANT_OR_SELF) | (1L << KW_DESCENDING) | (1L << KW_DIV) | (1L << KW_DOCUMENT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (KW_DOCUMENT_NODE - 64)) | (1L << (KW_ELEMENT - 64)) | (1L << (KW_ELSE - 64)) | (1L << (KW_EMPTY - 64)) | (1L << (KW_EMPTY_SEQUENCE - 64)) | (1L << (KW_ENCODING - 64)) | (1L << (KW_EQ - 64)) | (1L << (KW_EVERY - 64)) | (1L << (KW_EXCEPT - 64)) | (1L << (KW_EXTERNAL - 64)) | (1L << (KW_FOLLOWING - 64)) | (1L << (KW_FOLLOWING_SIBLING - 64)) | (1L << (KW_FOR - 64)) | (1L << (KW_FUNCTION - 64)) | (1L << (KW_GE - 64)) | (1L << (KW_GREATEST - 64)) | (1L << (KW_GT - 64)) | (1L << (KW_IDIV - 64)) | (1L << (KW_IF - 64)) | (1L << (KW_IMPORT - 64)) | (1L << (KW_IN - 64)) | (1L << (KW_INHERIT - 64)) | (1L << (KW_INSTANCE - 64)) | (1L << (KW_INTERSECT - 64)) | (1L << (KW_IS - 64)) | (1L << (KW_ITEM - 64)) | (1L << (KW_LAX - 64)) | (1L << (KW_LE - 64)) | (1L << (KW_LEAST - 64)) | (1L << (KW_LET - 64)) | (1L << (KW_LT - 64)) | (1L << (KW_MOD - 64)) | (1L << (KW_MODULE - 64)) | (1L << (KW_NAMESPACE - 64)) | (1L << (KW_NE - 64)) | (1L << (KW_NO_INHERIT - 64)) | (1L << (KW_NO_PRESERVE - 64)) | (1L << (KW_NODE - 64)) | (1L << (KW_OF - 64)) | (1L << (KW_OPTION - 64)) | (1L << (KW_OR - 64)) | (1L << (KW_ORDER - 64)) | (1L << (KW_ORDERED - 64)) | (1L << (KW_ORDERING - 64)) | (1L << (KW_PARENT - 64)) | (1L << (KW_PRECEDING - 64)) | (1L << (KW_PRECEDING_SIBLING - 64)) | (1L << (KW_PRESERVE - 64)) | (1L << (KW_PI - 64)) | (1L << (KW_RETURN - 64)) | (1L << (KW_SATISFIES - 64)) | (1L << (KW_SCHEMA - 64)) | (1L << (KW_SCHEMA_ATTR - 64)) | (1L << (KW_SCHEMA_ELEM - 64)) | (1L << (KW_SELF - 64)) | (1L << (KW_SOME - 64)) | (1L << (KW_STABLE - 64)) | (1L << (KW_STRICT - 64)) | (1L << (KW_STRIP - 64)) | (1L << (KW_TEXT - 64)) | (1L << (KW_THEN - 64)) | (1L << (KW_TO - 64)) | (1L << (KW_TREAT - 64)) | (1L << (KW_TYPESWITCH - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (KW_UNION - 128)) | (1L << (KW_UNORDERED - 128)) | (1L << (KW_VALIDATE - 128)) | (1L << (KW_VARIABLE - 128)) | (1L << (KW_VERSION - 128)) | (1L << (KW_WHERE - 128)) | (1L << (KW_XQUERY - 128)) | (1L << (FullQName - 128)) | (1L << (NCNameWithLocalWildcard - 128)) | (1L << (NCNameWithPrefixWildcard - 128)) | (1L << (NCName - 128)))) != 0)) {
					{
					setState(787); ((ElementConstructorContext)_localctx).contentExpr = expr();
					}
				}

				setState(790); match(RBRACE);
				}
				break;
			case KW_ATTRIBUTE:
				_localctx = new AttrConstructorContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(792); match(KW_ATTRIBUTE);
				setState(798);
				switch (_input.LA(1)) {
				case KW_ANCESTOR:
				case KW_ANCESTOR_OR_SELF:
				case KW_AND:
				case KW_AS:
				case KW_ASCENDING:
				case KW_AT:
				case KW_ATTRIBUTE:
				case KW_BASE_URI:
				case KW_BOUNDARY_SPACE:
				case KW_BY:
				case KW_CASE:
				case KW_CAST:
				case KW_CASTABLE:
				case KW_CHILD:
				case KW_COLLATION:
				case KW_COMMENT:
				case KW_CONSTRUCTION:
				case KW_COPY_NS:
				case KW_DECLARE:
				case KW_DEFAULT:
				case KW_DESCENDANT:
				case KW_DESCENDANT_OR_SELF:
				case KW_DESCENDING:
				case KW_DIV:
				case KW_DOCUMENT:
				case KW_DOCUMENT_NODE:
				case KW_ELEMENT:
				case KW_ELSE:
				case KW_EMPTY:
				case KW_EMPTY_SEQUENCE:
				case KW_ENCODING:
				case KW_EQ:
				case KW_EVERY:
				case KW_EXCEPT:
				case KW_EXTERNAL:
				case KW_FOLLOWING:
				case KW_FOLLOWING_SIBLING:
				case KW_FOR:
				case KW_FUNCTION:
				case KW_GE:
				case KW_GREATEST:
				case KW_GT:
				case KW_IDIV:
				case KW_IF:
				case KW_IMPORT:
				case KW_IN:
				case KW_INHERIT:
				case KW_INSTANCE:
				case KW_INTERSECT:
				case KW_IS:
				case KW_ITEM:
				case KW_LAX:
				case KW_LE:
				case KW_LEAST:
				case KW_LET:
				case KW_LT:
				case KW_MOD:
				case KW_MODULE:
				case KW_NAMESPACE:
				case KW_NE:
				case KW_NO_INHERIT:
				case KW_NO_PRESERVE:
				case KW_NODE:
				case KW_OF:
				case KW_OPTION:
				case KW_OR:
				case KW_ORDER:
				case KW_ORDERED:
				case KW_ORDERING:
				case KW_PARENT:
				case KW_PRECEDING:
				case KW_PRECEDING_SIBLING:
				case KW_PRESERVE:
				case KW_PI:
				case KW_RETURN:
				case KW_SATISFIES:
				case KW_SCHEMA:
				case KW_SCHEMA_ATTR:
				case KW_SCHEMA_ELEM:
				case KW_SELF:
				case KW_SOME:
				case KW_STABLE:
				case KW_STRICT:
				case KW_STRIP:
				case KW_TEXT:
				case KW_THEN:
				case KW_TO:
				case KW_TREAT:
				case KW_TYPESWITCH:
				case KW_UNION:
				case KW_UNORDERED:
				case KW_VALIDATE:
				case KW_VARIABLE:
				case KW_VERSION:
				case KW_WHERE:
				case KW_XQUERY:
				case FullQName:
				case NCName:
					{
					setState(793); ((AttrConstructorContext)_localctx).attrName = qName();
					}
					break;
				case LBRACE:
					{
					{
					setState(794); match(LBRACE);
					setState(795); ((AttrConstructorContext)_localctx).attrExpr = expr();
					setState(796); match(RBRACE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(800); match(LBRACE);
				setState(802);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << DecimalLiteral) | (1L << DoubleLiteral) | (1L << Quot) | (1L << Apos) | (1L << COMMENT) | (1L << PI) | (1L << PRAGMA) | (1L << LPAREN) | (1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DOT) | (1L << DDOT) | (1L << SLASH) | (1L << DSLASH) | (1L << LANGLE) | (1L << AT) | (1L << DOLLAR) | (1L << KW_ANCESTOR) | (1L << KW_ANCESTOR_OR_SELF) | (1L << KW_AND) | (1L << KW_AS) | (1L << KW_ASCENDING) | (1L << KW_AT) | (1L << KW_ATTRIBUTE) | (1L << KW_BASE_URI) | (1L << KW_BOUNDARY_SPACE) | (1L << KW_BY) | (1L << KW_CASE) | (1L << KW_CAST) | (1L << KW_CASTABLE) | (1L << KW_CHILD) | (1L << KW_COLLATION) | (1L << KW_COMMENT) | (1L << KW_CONSTRUCTION) | (1L << KW_COPY_NS) | (1L << KW_DECLARE) | (1L << KW_DEFAULT) | (1L << KW_DESCENDANT) | (1L << KW_DESCENDANT_OR_SELF) | (1L << KW_DESCENDING) | (1L << KW_DIV) | (1L << KW_DOCUMENT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (KW_DOCUMENT_NODE - 64)) | (1L << (KW_ELEMENT - 64)) | (1L << (KW_ELSE - 64)) | (1L << (KW_EMPTY - 64)) | (1L << (KW_EMPTY_SEQUENCE - 64)) | (1L << (KW_ENCODING - 64)) | (1L << (KW_EQ - 64)) | (1L << (KW_EVERY - 64)) | (1L << (KW_EXCEPT - 64)) | (1L << (KW_EXTERNAL - 64)) | (1L << (KW_FOLLOWING - 64)) | (1L << (KW_FOLLOWING_SIBLING - 64)) | (1L << (KW_FOR - 64)) | (1L << (KW_FUNCTION - 64)) | (1L << (KW_GE - 64)) | (1L << (KW_GREATEST - 64)) | (1L << (KW_GT - 64)) | (1L << (KW_IDIV - 64)) | (1L << (KW_IF - 64)) | (1L << (KW_IMPORT - 64)) | (1L << (KW_IN - 64)) | (1L << (KW_INHERIT - 64)) | (1L << (KW_INSTANCE - 64)) | (1L << (KW_INTERSECT - 64)) | (1L << (KW_IS - 64)) | (1L << (KW_ITEM - 64)) | (1L << (KW_LAX - 64)) | (1L << (KW_LE - 64)) | (1L << (KW_LEAST - 64)) | (1L << (KW_LET - 64)) | (1L << (KW_LT - 64)) | (1L << (KW_MOD - 64)) | (1L << (KW_MODULE - 64)) | (1L << (KW_NAMESPACE - 64)) | (1L << (KW_NE - 64)) | (1L << (KW_NO_INHERIT - 64)) | (1L << (KW_NO_PRESERVE - 64)) | (1L << (KW_NODE - 64)) | (1L << (KW_OF - 64)) | (1L << (KW_OPTION - 64)) | (1L << (KW_OR - 64)) | (1L << (KW_ORDER - 64)) | (1L << (KW_ORDERED - 64)) | (1L << (KW_ORDERING - 64)) | (1L << (KW_PARENT - 64)) | (1L << (KW_PRECEDING - 64)) | (1L << (KW_PRECEDING_SIBLING - 64)) | (1L << (KW_PRESERVE - 64)) | (1L << (KW_PI - 64)) | (1L << (KW_RETURN - 64)) | (1L << (KW_SATISFIES - 64)) | (1L << (KW_SCHEMA - 64)) | (1L << (KW_SCHEMA_ATTR - 64)) | (1L << (KW_SCHEMA_ELEM - 64)) | (1L << (KW_SELF - 64)) | (1L << (KW_SOME - 64)) | (1L << (KW_STABLE - 64)) | (1L << (KW_STRICT - 64)) | (1L << (KW_STRIP - 64)) | (1L << (KW_TEXT - 64)) | (1L << (KW_THEN - 64)) | (1L << (KW_TO - 64)) | (1L << (KW_TREAT - 64)) | (1L << (KW_TYPESWITCH - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (KW_UNION - 128)) | (1L << (KW_UNORDERED - 128)) | (1L << (KW_VALIDATE - 128)) | (1L << (KW_VARIABLE - 128)) | (1L << (KW_VERSION - 128)) | (1L << (KW_WHERE - 128)) | (1L << (KW_XQUERY - 128)) | (1L << (FullQName - 128)) | (1L << (NCNameWithLocalWildcard - 128)) | (1L << (NCNameWithPrefixWildcard - 128)) | (1L << (NCName - 128)))) != 0)) {
					{
					setState(801); ((AttrConstructorContext)_localctx).contentExpr = expr();
					}
				}

				setState(804); match(RBRACE);
				}
				break;
			case KW_TEXT:
				_localctx = new TextConstructorContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(806); match(KW_TEXT);
				setState(807); match(LBRACE);
				setState(808); expr();
				setState(809); match(RBRACE);
				}
				break;
			case KW_COMMENT:
				_localctx = new CommentConstructorContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(811); match(KW_COMMENT);
				setState(812); match(LBRACE);
				setState(813); expr();
				setState(814); match(RBRACE);
				}
				break;
			case KW_PI:
				_localctx = new PiConstructorContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(816); match(KW_PI);
				setState(822);
				switch (_input.LA(1)) {
				case KW_ANCESTOR:
				case KW_ANCESTOR_OR_SELF:
				case KW_AND:
				case KW_AS:
				case KW_ASCENDING:
				case KW_AT:
				case KW_ATTRIBUTE:
				case KW_BASE_URI:
				case KW_BOUNDARY_SPACE:
				case KW_BY:
				case KW_CASE:
				case KW_CAST:
				case KW_CASTABLE:
				case KW_CHILD:
				case KW_COLLATION:
				case KW_COMMENT:
				case KW_CONSTRUCTION:
				case KW_COPY_NS:
				case KW_DECLARE:
				case KW_DEFAULT:
				case KW_DESCENDANT:
				case KW_DESCENDANT_OR_SELF:
				case KW_DESCENDING:
				case KW_DIV:
				case KW_DOCUMENT:
				case KW_DOCUMENT_NODE:
				case KW_ELEMENT:
				case KW_ELSE:
				case KW_EMPTY:
				case KW_EMPTY_SEQUENCE:
				case KW_ENCODING:
				case KW_EQ:
				case KW_EVERY:
				case KW_EXCEPT:
				case KW_EXTERNAL:
				case KW_FOLLOWING:
				case KW_FOLLOWING_SIBLING:
				case KW_FOR:
				case KW_FUNCTION:
				case KW_GE:
				case KW_GREATEST:
				case KW_GT:
				case KW_IDIV:
				case KW_IF:
				case KW_IMPORT:
				case KW_IN:
				case KW_INHERIT:
				case KW_INSTANCE:
				case KW_INTERSECT:
				case KW_IS:
				case KW_ITEM:
				case KW_LAX:
				case KW_LE:
				case KW_LEAST:
				case KW_LET:
				case KW_LT:
				case KW_MOD:
				case KW_MODULE:
				case KW_NAMESPACE:
				case KW_NE:
				case KW_NO_INHERIT:
				case KW_NO_PRESERVE:
				case KW_NODE:
				case KW_OF:
				case KW_OPTION:
				case KW_OR:
				case KW_ORDER:
				case KW_ORDERED:
				case KW_ORDERING:
				case KW_PARENT:
				case KW_PRECEDING:
				case KW_PRECEDING_SIBLING:
				case KW_PRESERVE:
				case KW_PI:
				case KW_RETURN:
				case KW_SATISFIES:
				case KW_SCHEMA:
				case KW_SCHEMA_ATTR:
				case KW_SCHEMA_ELEM:
				case KW_SELF:
				case KW_SOME:
				case KW_STABLE:
				case KW_STRICT:
				case KW_STRIP:
				case KW_TEXT:
				case KW_THEN:
				case KW_TO:
				case KW_TREAT:
				case KW_TYPESWITCH:
				case KW_UNION:
				case KW_UNORDERED:
				case KW_VALIDATE:
				case KW_VARIABLE:
				case KW_VERSION:
				case KW_WHERE:
				case KW_XQUERY:
				case NCName:
					{
					setState(817); ((PiConstructorContext)_localctx).piName = ncName();
					}
					break;
				case LBRACE:
					{
					setState(818); match(LBRACE);
					setState(819); ((PiConstructorContext)_localctx).piExpr = expr();
					setState(820); match(RBRACE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(824); match(LBRACE);
				setState(826);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << DecimalLiteral) | (1L << DoubleLiteral) | (1L << Quot) | (1L << Apos) | (1L << COMMENT) | (1L << PI) | (1L << PRAGMA) | (1L << LPAREN) | (1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << DOT) | (1L << DDOT) | (1L << SLASH) | (1L << DSLASH) | (1L << LANGLE) | (1L << AT) | (1L << DOLLAR) | (1L << KW_ANCESTOR) | (1L << KW_ANCESTOR_OR_SELF) | (1L << KW_AND) | (1L << KW_AS) | (1L << KW_ASCENDING) | (1L << KW_AT) | (1L << KW_ATTRIBUTE) | (1L << KW_BASE_URI) | (1L << KW_BOUNDARY_SPACE) | (1L << KW_BY) | (1L << KW_CASE) | (1L << KW_CAST) | (1L << KW_CASTABLE) | (1L << KW_CHILD) | (1L << KW_COLLATION) | (1L << KW_COMMENT) | (1L << KW_CONSTRUCTION) | (1L << KW_COPY_NS) | (1L << KW_DECLARE) | (1L << KW_DEFAULT) | (1L << KW_DESCENDANT) | (1L << KW_DESCENDANT_OR_SELF) | (1L << KW_DESCENDING) | (1L << KW_DIV) | (1L << KW_DOCUMENT))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (KW_DOCUMENT_NODE - 64)) | (1L << (KW_ELEMENT - 64)) | (1L << (KW_ELSE - 64)) | (1L << (KW_EMPTY - 64)) | (1L << (KW_EMPTY_SEQUENCE - 64)) | (1L << (KW_ENCODING - 64)) | (1L << (KW_EQ - 64)) | (1L << (KW_EVERY - 64)) | (1L << (KW_EXCEPT - 64)) | (1L << (KW_EXTERNAL - 64)) | (1L << (KW_FOLLOWING - 64)) | (1L << (KW_FOLLOWING_SIBLING - 64)) | (1L << (KW_FOR - 64)) | (1L << (KW_FUNCTION - 64)) | (1L << (KW_GE - 64)) | (1L << (KW_GREATEST - 64)) | (1L << (KW_GT - 64)) | (1L << (KW_IDIV - 64)) | (1L << (KW_IF - 64)) | (1L << (KW_IMPORT - 64)) | (1L << (KW_IN - 64)) | (1L << (KW_INHERIT - 64)) | (1L << (KW_INSTANCE - 64)) | (1L << (KW_INTERSECT - 64)) | (1L << (KW_IS - 64)) | (1L << (KW_ITEM - 64)) | (1L << (KW_LAX - 64)) | (1L << (KW_LE - 64)) | (1L << (KW_LEAST - 64)) | (1L << (KW_LET - 64)) | (1L << (KW_LT - 64)) | (1L << (KW_MOD - 64)) | (1L << (KW_MODULE - 64)) | (1L << (KW_NAMESPACE - 64)) | (1L << (KW_NE - 64)) | (1L << (KW_NO_INHERIT - 64)) | (1L << (KW_NO_PRESERVE - 64)) | (1L << (KW_NODE - 64)) | (1L << (KW_OF - 64)) | (1L << (KW_OPTION - 64)) | (1L << (KW_OR - 64)) | (1L << (KW_ORDER - 64)) | (1L << (KW_ORDERED - 64)) | (1L << (KW_ORDERING - 64)) | (1L << (KW_PARENT - 64)) | (1L << (KW_PRECEDING - 64)) | (1L << (KW_PRECEDING_SIBLING - 64)) | (1L << (KW_PRESERVE - 64)) | (1L << (KW_PI - 64)) | (1L << (KW_RETURN - 64)) | (1L << (KW_SATISFIES - 64)) | (1L << (KW_SCHEMA - 64)) | (1L << (KW_SCHEMA_ATTR - 64)) | (1L << (KW_SCHEMA_ELEM - 64)) | (1L << (KW_SELF - 64)) | (1L << (KW_SOME - 64)) | (1L << (KW_STABLE - 64)) | (1L << (KW_STRICT - 64)) | (1L << (KW_STRIP - 64)) | (1L << (KW_TEXT - 64)) | (1L << (KW_THEN - 64)) | (1L << (KW_TO - 64)) | (1L << (KW_TREAT - 64)) | (1L << (KW_TYPESWITCH - 64)))) != 0) || ((((_la - 128)) & ~0x3f) == 0 && ((1L << (_la - 128)) & ((1L << (KW_UNION - 128)) | (1L << (KW_UNORDERED - 128)) | (1L << (KW_VALIDATE - 128)) | (1L << (KW_VARIABLE - 128)) | (1L << (KW_VERSION - 128)) | (1L << (KW_WHERE - 128)) | (1L << (KW_XQUERY - 128)) | (1L << (FullQName - 128)) | (1L << (NCNameWithLocalWildcard - 128)) | (1L << (NCNameWithPrefixWildcard - 128)) | (1L << (NCName - 128)))) != 0)) {
					{
					setState(825); ((PiConstructorContext)_localctx).contentExpr = expr();
					}
				}

				setState(828); match(RBRACE);
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

	public static class SingleTypeContext extends ParserRuleContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public SingleTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterSingleType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitSingleType(this);
		}
	}

	public final SingleTypeContext singleType() throws RecognitionException {
		SingleTypeContext _localctx = new SingleTypeContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_singleType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(832); qName();
			setState(834);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				{
				setState(833); match(QUESTION);
				}
				break;
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

	public static class TypeDeclarationContext extends ParserRuleContext {
		public SequenceTypeContext sequenceType() {
			return getRuleContext(SequenceTypeContext.class,0);
		}
		public TypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterTypeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitTypeDeclaration(this);
		}
	}

	public final TypeDeclarationContext typeDeclaration() throws RecognitionException {
		TypeDeclarationContext _localctx = new TypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_typeDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(836); match(KW_AS);
			setState(837); sequenceType();
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

	public static class SequenceTypeContext extends ParserRuleContext {
		public Token occurrence;
		public ItemTypeContext itemType() {
			return getRuleContext(ItemTypeContext.class,0);
		}
		public SequenceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sequenceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterSequenceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitSequenceType(this);
		}
	}

	public final SequenceTypeContext sequenceType() throws RecognitionException {
		SequenceTypeContext _localctx = new SequenceTypeContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_sequenceType);
		int _la;
		try {
			setState(846);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(839); match(KW_EMPTY_SEQUENCE);
				setState(840); match(LPAREN);
				setState(841); match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(842); itemType();
				setState(844);
				switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
				case 1:
					{
					setState(843);
					((SequenceTypeContext)_localctx).occurrence = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << STAR) | (1L << PLUS) | (1L << QUESTION))) != 0)) ) {
						((SequenceTypeContext)_localctx).occurrence = (Token)_errHandler.recoverInline(this);
					}
					consume();
					}
					break;
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

	public static class ItemTypeContext extends ParserRuleContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public KindTestContext kindTest() {
			return getRuleContext(KindTestContext.class,0);
		}
		public ItemTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_itemType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterItemType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitItemType(this);
		}
	}

	public final ItemTypeContext itemType() throws RecognitionException {
		ItemTypeContext _localctx = new ItemTypeContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_itemType);
		try {
			setState(853);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(848); kindTest();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(849); match(KW_ITEM);
				setState(850); match(LPAREN);
				setState(851); match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(852); qName();
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

	public static class KindTestContext extends ParserRuleContext {
		public AnyKindTestContext anyKindTest() {
			return getRuleContext(AnyKindTestContext.class,0);
		}
		public DocumentTestContext documentTest() {
			return getRuleContext(DocumentTestContext.class,0);
		}
		public CommentTestContext commentTest() {
			return getRuleContext(CommentTestContext.class,0);
		}
		public SchemaElementTestContext schemaElementTest() {
			return getRuleContext(SchemaElementTestContext.class,0);
		}
		public ElementTestContext elementTest() {
			return getRuleContext(ElementTestContext.class,0);
		}
		public SchemaAttributeTestContext schemaAttributeTest() {
			return getRuleContext(SchemaAttributeTestContext.class,0);
		}
		public AttributeTestContext attributeTest() {
			return getRuleContext(AttributeTestContext.class,0);
		}
		public PiTestContext piTest() {
			return getRuleContext(PiTestContext.class,0);
		}
		public TextTestContext textTest() {
			return getRuleContext(TextTestContext.class,0);
		}
		public KindTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kindTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterKindTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitKindTest(this);
		}
	}

	public final KindTestContext kindTest() throws RecognitionException {
		KindTestContext _localctx = new KindTestContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_kindTest);
		try {
			setState(864);
			switch (_input.LA(1)) {
			case KW_DOCUMENT_NODE:
				enterOuterAlt(_localctx, 1);
				{
				setState(855); documentTest();
				}
				break;
			case KW_ELEMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(856); elementTest();
				}
				break;
			case KW_ATTRIBUTE:
				enterOuterAlt(_localctx, 3);
				{
				setState(857); attributeTest();
				}
				break;
			case KW_SCHEMA_ELEM:
				enterOuterAlt(_localctx, 4);
				{
				setState(858); schemaElementTest();
				}
				break;
			case KW_SCHEMA_ATTR:
				enterOuterAlt(_localctx, 5);
				{
				setState(859); schemaAttributeTest();
				}
				break;
			case KW_PI:
				enterOuterAlt(_localctx, 6);
				{
				setState(860); piTest();
				}
				break;
			case KW_COMMENT:
				enterOuterAlt(_localctx, 7);
				{
				setState(861); commentTest();
				}
				break;
			case KW_TEXT:
				enterOuterAlt(_localctx, 8);
				{
				setState(862); textTest();
				}
				break;
			case KW_NODE:
				enterOuterAlt(_localctx, 9);
				{
				setState(863); anyKindTest();
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

	public static class DocumentTestContext extends ParserRuleContext {
		public SchemaElementTestContext schemaElementTest() {
			return getRuleContext(SchemaElementTestContext.class,0);
		}
		public ElementTestContext elementTest() {
			return getRuleContext(ElementTestContext.class,0);
		}
		public DocumentTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_documentTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterDocumentTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitDocumentTest(this);
		}
	}

	public final DocumentTestContext documentTest() throws RecognitionException {
		DocumentTestContext _localctx = new DocumentTestContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_documentTest);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(866); match(KW_DOCUMENT_NODE);
			setState(867); match(LPAREN);
			setState(870);
			switch (_input.LA(1)) {
			case KW_ELEMENT:
				{
				setState(868); elementTest();
				}
				break;
			case KW_SCHEMA_ELEM:
				{
				setState(869); schemaElementTest();
				}
				break;
			case RPAREN:
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(872); match(RPAREN);
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

	public static class ElementTestContext extends ParserRuleContext {
		public QNameContext name;
		public Token wildcard;
		public QNameContext type;
		public Token optional;
		public List<QNameContext> qName() {
			return getRuleContexts(QNameContext.class);
		}
		public QNameContext qName(int i) {
			return getRuleContext(QNameContext.class,i);
		}
		public ElementTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterElementTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitElementTest(this);
		}
	}

	public final ElementTestContext elementTest() throws RecognitionException {
		ElementTestContext _localctx = new ElementTestContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_elementTest);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(874); match(KW_ELEMENT);
			setState(875); match(LPAREN);
			setState(887);
			_la = _input.LA(1);
			if (((((_la - 22)) & ~0x3f) == 0 && ((1L << (_la - 22)) & ((1L << (STAR - 22)) | (1L << (KW_ANCESTOR - 22)) | (1L << (KW_ANCESTOR_OR_SELF - 22)) | (1L << (KW_AND - 22)) | (1L << (KW_AS - 22)) | (1L << (KW_ASCENDING - 22)) | (1L << (KW_AT - 22)) | (1L << (KW_ATTRIBUTE - 22)) | (1L << (KW_BASE_URI - 22)) | (1L << (KW_BOUNDARY_SPACE - 22)) | (1L << (KW_BY - 22)) | (1L << (KW_CASE - 22)) | (1L << (KW_CAST - 22)) | (1L << (KW_CASTABLE - 22)) | (1L << (KW_CHILD - 22)) | (1L << (KW_COLLATION - 22)) | (1L << (KW_COMMENT - 22)) | (1L << (KW_CONSTRUCTION - 22)) | (1L << (KW_COPY_NS - 22)) | (1L << (KW_DECLARE - 22)) | (1L << (KW_DEFAULT - 22)) | (1L << (KW_DESCENDANT - 22)) | (1L << (KW_DESCENDANT_OR_SELF - 22)) | (1L << (KW_DESCENDING - 22)) | (1L << (KW_DIV - 22)) | (1L << (KW_DOCUMENT - 22)) | (1L << (KW_DOCUMENT_NODE - 22)) | (1L << (KW_ELEMENT - 22)) | (1L << (KW_ELSE - 22)) | (1L << (KW_EMPTY - 22)) | (1L << (KW_EMPTY_SEQUENCE - 22)) | (1L << (KW_ENCODING - 22)) | (1L << (KW_EQ - 22)) | (1L << (KW_EVERY - 22)) | (1L << (KW_EXCEPT - 22)) | (1L << (KW_EXTERNAL - 22)) | (1L << (KW_FOLLOWING - 22)) | (1L << (KW_FOLLOWING_SIBLING - 22)) | (1L << (KW_FOR - 22)) | (1L << (KW_FUNCTION - 22)) | (1L << (KW_GE - 22)) | (1L << (KW_GREATEST - 22)) | (1L << (KW_GT - 22)) | (1L << (KW_IDIV - 22)) | (1L << (KW_IF - 22)) | (1L << (KW_IMPORT - 22)) | (1L << (KW_IN - 22)) | (1L << (KW_INHERIT - 22)))) != 0) || ((((_la - 86)) & ~0x3f) == 0 && ((1L << (_la - 86)) & ((1L << (KW_INSTANCE - 86)) | (1L << (KW_INTERSECT - 86)) | (1L << (KW_IS - 86)) | (1L << (KW_ITEM - 86)) | (1L << (KW_LAX - 86)) | (1L << (KW_LE - 86)) | (1L << (KW_LEAST - 86)) | (1L << (KW_LET - 86)) | (1L << (KW_LT - 86)) | (1L << (KW_MOD - 86)) | (1L << (KW_MODULE - 86)) | (1L << (KW_NAMESPACE - 86)) | (1L << (KW_NE - 86)) | (1L << (KW_NO_INHERIT - 86)) | (1L << (KW_NO_PRESERVE - 86)) | (1L << (KW_NODE - 86)) | (1L << (KW_OF - 86)) | (1L << (KW_OPTION - 86)) | (1L << (KW_OR - 86)) | (1L << (KW_ORDER - 86)) | (1L << (KW_ORDERED - 86)) | (1L << (KW_ORDERING - 86)) | (1L << (KW_PARENT - 86)) | (1L << (KW_PRECEDING - 86)) | (1L << (KW_PRECEDING_SIBLING - 86)) | (1L << (KW_PRESERVE - 86)) | (1L << (KW_PI - 86)) | (1L << (KW_RETURN - 86)) | (1L << (KW_SATISFIES - 86)) | (1L << (KW_SCHEMA - 86)) | (1L << (KW_SCHEMA_ATTR - 86)) | (1L << (KW_SCHEMA_ELEM - 86)) | (1L << (KW_SELF - 86)) | (1L << (KW_SOME - 86)) | (1L << (KW_STABLE - 86)) | (1L << (KW_STRICT - 86)) | (1L << (KW_STRIP - 86)) | (1L << (KW_TEXT - 86)) | (1L << (KW_THEN - 86)) | (1L << (KW_TO - 86)) | (1L << (KW_TREAT - 86)) | (1L << (KW_TYPESWITCH - 86)) | (1L << (KW_UNION - 86)) | (1L << (KW_UNORDERED - 86)) | (1L << (KW_VALIDATE - 86)) | (1L << (KW_VARIABLE - 86)) | (1L << (KW_VERSION - 86)) | (1L << (KW_WHERE - 86)) | (1L << (KW_XQUERY - 86)) | (1L << (FullQName - 86)) | (1L << (NCName - 86)))) != 0)) {
				{
				setState(878);
				switch (_input.LA(1)) {
				case KW_ANCESTOR:
				case KW_ANCESTOR_OR_SELF:
				case KW_AND:
				case KW_AS:
				case KW_ASCENDING:
				case KW_AT:
				case KW_ATTRIBUTE:
				case KW_BASE_URI:
				case KW_BOUNDARY_SPACE:
				case KW_BY:
				case KW_CASE:
				case KW_CAST:
				case KW_CASTABLE:
				case KW_CHILD:
				case KW_COLLATION:
				case KW_COMMENT:
				case KW_CONSTRUCTION:
				case KW_COPY_NS:
				case KW_DECLARE:
				case KW_DEFAULT:
				case KW_DESCENDANT:
				case KW_DESCENDANT_OR_SELF:
				case KW_DESCENDING:
				case KW_DIV:
				case KW_DOCUMENT:
				case KW_DOCUMENT_NODE:
				case KW_ELEMENT:
				case KW_ELSE:
				case KW_EMPTY:
				case KW_EMPTY_SEQUENCE:
				case KW_ENCODING:
				case KW_EQ:
				case KW_EVERY:
				case KW_EXCEPT:
				case KW_EXTERNAL:
				case KW_FOLLOWING:
				case KW_FOLLOWING_SIBLING:
				case KW_FOR:
				case KW_FUNCTION:
				case KW_GE:
				case KW_GREATEST:
				case KW_GT:
				case KW_IDIV:
				case KW_IF:
				case KW_IMPORT:
				case KW_IN:
				case KW_INHERIT:
				case KW_INSTANCE:
				case KW_INTERSECT:
				case KW_IS:
				case KW_ITEM:
				case KW_LAX:
				case KW_LE:
				case KW_LEAST:
				case KW_LET:
				case KW_LT:
				case KW_MOD:
				case KW_MODULE:
				case KW_NAMESPACE:
				case KW_NE:
				case KW_NO_INHERIT:
				case KW_NO_PRESERVE:
				case KW_NODE:
				case KW_OF:
				case KW_OPTION:
				case KW_OR:
				case KW_ORDER:
				case KW_ORDERED:
				case KW_ORDERING:
				case KW_PARENT:
				case KW_PRECEDING:
				case KW_PRECEDING_SIBLING:
				case KW_PRESERVE:
				case KW_PI:
				case KW_RETURN:
				case KW_SATISFIES:
				case KW_SCHEMA:
				case KW_SCHEMA_ATTR:
				case KW_SCHEMA_ELEM:
				case KW_SELF:
				case KW_SOME:
				case KW_STABLE:
				case KW_STRICT:
				case KW_STRIP:
				case KW_TEXT:
				case KW_THEN:
				case KW_TO:
				case KW_TREAT:
				case KW_TYPESWITCH:
				case KW_UNION:
				case KW_UNORDERED:
				case KW_VALIDATE:
				case KW_VARIABLE:
				case KW_VERSION:
				case KW_WHERE:
				case KW_XQUERY:
				case FullQName:
				case NCName:
					{
					setState(876); ((ElementTestContext)_localctx).name = qName();
					}
					break;
				case STAR:
					{
					setState(877); ((ElementTestContext)_localctx).wildcard = match(STAR);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(885);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(880); match(COMMA);
					setState(881); ((ElementTestContext)_localctx).type = qName();
					setState(883);
					_la = _input.LA(1);
					if (_la==QUESTION) {
						{
						setState(882); ((ElementTestContext)_localctx).optional = match(QUESTION);
						}
					}

					}
				}

				}
			}

			setState(889); match(RPAREN);
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

	public static class AttributeTestContext extends ParserRuleContext {
		public QNameContext name;
		public Token wildcard;
		public QNameContext type;
		public List<QNameContext> qName() {
			return getRuleContexts(QNameContext.class);
		}
		public QNameContext qName(int i) {
			return getRuleContext(QNameContext.class,i);
		}
		public AttributeTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAttributeTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAttributeTest(this);
		}
	}

	public final AttributeTestContext attributeTest() throws RecognitionException {
		AttributeTestContext _localctx = new AttributeTestContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_attributeTest);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(891); match(KW_ATTRIBUTE);
			setState(892); match(LPAREN);
			setState(901);
			_la = _input.LA(1);
			if (((((_la - 22)) & ~0x3f) == 0 && ((1L << (_la - 22)) & ((1L << (STAR - 22)) | (1L << (KW_ANCESTOR - 22)) | (1L << (KW_ANCESTOR_OR_SELF - 22)) | (1L << (KW_AND - 22)) | (1L << (KW_AS - 22)) | (1L << (KW_ASCENDING - 22)) | (1L << (KW_AT - 22)) | (1L << (KW_ATTRIBUTE - 22)) | (1L << (KW_BASE_URI - 22)) | (1L << (KW_BOUNDARY_SPACE - 22)) | (1L << (KW_BY - 22)) | (1L << (KW_CASE - 22)) | (1L << (KW_CAST - 22)) | (1L << (KW_CASTABLE - 22)) | (1L << (KW_CHILD - 22)) | (1L << (KW_COLLATION - 22)) | (1L << (KW_COMMENT - 22)) | (1L << (KW_CONSTRUCTION - 22)) | (1L << (KW_COPY_NS - 22)) | (1L << (KW_DECLARE - 22)) | (1L << (KW_DEFAULT - 22)) | (1L << (KW_DESCENDANT - 22)) | (1L << (KW_DESCENDANT_OR_SELF - 22)) | (1L << (KW_DESCENDING - 22)) | (1L << (KW_DIV - 22)) | (1L << (KW_DOCUMENT - 22)) | (1L << (KW_DOCUMENT_NODE - 22)) | (1L << (KW_ELEMENT - 22)) | (1L << (KW_ELSE - 22)) | (1L << (KW_EMPTY - 22)) | (1L << (KW_EMPTY_SEQUENCE - 22)) | (1L << (KW_ENCODING - 22)) | (1L << (KW_EQ - 22)) | (1L << (KW_EVERY - 22)) | (1L << (KW_EXCEPT - 22)) | (1L << (KW_EXTERNAL - 22)) | (1L << (KW_FOLLOWING - 22)) | (1L << (KW_FOLLOWING_SIBLING - 22)) | (1L << (KW_FOR - 22)) | (1L << (KW_FUNCTION - 22)) | (1L << (KW_GE - 22)) | (1L << (KW_GREATEST - 22)) | (1L << (KW_GT - 22)) | (1L << (KW_IDIV - 22)) | (1L << (KW_IF - 22)) | (1L << (KW_IMPORT - 22)) | (1L << (KW_IN - 22)) | (1L << (KW_INHERIT - 22)))) != 0) || ((((_la - 86)) & ~0x3f) == 0 && ((1L << (_la - 86)) & ((1L << (KW_INSTANCE - 86)) | (1L << (KW_INTERSECT - 86)) | (1L << (KW_IS - 86)) | (1L << (KW_ITEM - 86)) | (1L << (KW_LAX - 86)) | (1L << (KW_LE - 86)) | (1L << (KW_LEAST - 86)) | (1L << (KW_LET - 86)) | (1L << (KW_LT - 86)) | (1L << (KW_MOD - 86)) | (1L << (KW_MODULE - 86)) | (1L << (KW_NAMESPACE - 86)) | (1L << (KW_NE - 86)) | (1L << (KW_NO_INHERIT - 86)) | (1L << (KW_NO_PRESERVE - 86)) | (1L << (KW_NODE - 86)) | (1L << (KW_OF - 86)) | (1L << (KW_OPTION - 86)) | (1L << (KW_OR - 86)) | (1L << (KW_ORDER - 86)) | (1L << (KW_ORDERED - 86)) | (1L << (KW_ORDERING - 86)) | (1L << (KW_PARENT - 86)) | (1L << (KW_PRECEDING - 86)) | (1L << (KW_PRECEDING_SIBLING - 86)) | (1L << (KW_PRESERVE - 86)) | (1L << (KW_PI - 86)) | (1L << (KW_RETURN - 86)) | (1L << (KW_SATISFIES - 86)) | (1L << (KW_SCHEMA - 86)) | (1L << (KW_SCHEMA_ATTR - 86)) | (1L << (KW_SCHEMA_ELEM - 86)) | (1L << (KW_SELF - 86)) | (1L << (KW_SOME - 86)) | (1L << (KW_STABLE - 86)) | (1L << (KW_STRICT - 86)) | (1L << (KW_STRIP - 86)) | (1L << (KW_TEXT - 86)) | (1L << (KW_THEN - 86)) | (1L << (KW_TO - 86)) | (1L << (KW_TREAT - 86)) | (1L << (KW_TYPESWITCH - 86)) | (1L << (KW_UNION - 86)) | (1L << (KW_UNORDERED - 86)) | (1L << (KW_VALIDATE - 86)) | (1L << (KW_VARIABLE - 86)) | (1L << (KW_VERSION - 86)) | (1L << (KW_WHERE - 86)) | (1L << (KW_XQUERY - 86)) | (1L << (FullQName - 86)) | (1L << (NCName - 86)))) != 0)) {
				{
				setState(895);
				switch (_input.LA(1)) {
				case KW_ANCESTOR:
				case KW_ANCESTOR_OR_SELF:
				case KW_AND:
				case KW_AS:
				case KW_ASCENDING:
				case KW_AT:
				case KW_ATTRIBUTE:
				case KW_BASE_URI:
				case KW_BOUNDARY_SPACE:
				case KW_BY:
				case KW_CASE:
				case KW_CAST:
				case KW_CASTABLE:
				case KW_CHILD:
				case KW_COLLATION:
				case KW_COMMENT:
				case KW_CONSTRUCTION:
				case KW_COPY_NS:
				case KW_DECLARE:
				case KW_DEFAULT:
				case KW_DESCENDANT:
				case KW_DESCENDANT_OR_SELF:
				case KW_DESCENDING:
				case KW_DIV:
				case KW_DOCUMENT:
				case KW_DOCUMENT_NODE:
				case KW_ELEMENT:
				case KW_ELSE:
				case KW_EMPTY:
				case KW_EMPTY_SEQUENCE:
				case KW_ENCODING:
				case KW_EQ:
				case KW_EVERY:
				case KW_EXCEPT:
				case KW_EXTERNAL:
				case KW_FOLLOWING:
				case KW_FOLLOWING_SIBLING:
				case KW_FOR:
				case KW_FUNCTION:
				case KW_GE:
				case KW_GREATEST:
				case KW_GT:
				case KW_IDIV:
				case KW_IF:
				case KW_IMPORT:
				case KW_IN:
				case KW_INHERIT:
				case KW_INSTANCE:
				case KW_INTERSECT:
				case KW_IS:
				case KW_ITEM:
				case KW_LAX:
				case KW_LE:
				case KW_LEAST:
				case KW_LET:
				case KW_LT:
				case KW_MOD:
				case KW_MODULE:
				case KW_NAMESPACE:
				case KW_NE:
				case KW_NO_INHERIT:
				case KW_NO_PRESERVE:
				case KW_NODE:
				case KW_OF:
				case KW_OPTION:
				case KW_OR:
				case KW_ORDER:
				case KW_ORDERED:
				case KW_ORDERING:
				case KW_PARENT:
				case KW_PRECEDING:
				case KW_PRECEDING_SIBLING:
				case KW_PRESERVE:
				case KW_PI:
				case KW_RETURN:
				case KW_SATISFIES:
				case KW_SCHEMA:
				case KW_SCHEMA_ATTR:
				case KW_SCHEMA_ELEM:
				case KW_SELF:
				case KW_SOME:
				case KW_STABLE:
				case KW_STRICT:
				case KW_STRIP:
				case KW_TEXT:
				case KW_THEN:
				case KW_TO:
				case KW_TREAT:
				case KW_TYPESWITCH:
				case KW_UNION:
				case KW_UNORDERED:
				case KW_VALIDATE:
				case KW_VARIABLE:
				case KW_VERSION:
				case KW_WHERE:
				case KW_XQUERY:
				case FullQName:
				case NCName:
					{
					setState(893); ((AttributeTestContext)_localctx).name = qName();
					}
					break;
				case STAR:
					{
					setState(894); ((AttributeTestContext)_localctx).wildcard = match(STAR);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(899);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(897); match(COMMA);
					setState(898); ((AttributeTestContext)_localctx).type = qName();
					}
				}

				}
			}

			setState(903); match(RPAREN);
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

	public static class SchemaElementTestContext extends ParserRuleContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public SchemaElementTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schemaElementTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterSchemaElementTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitSchemaElementTest(this);
		}
	}

	public final SchemaElementTestContext schemaElementTest() throws RecognitionException {
		SchemaElementTestContext _localctx = new SchemaElementTestContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_schemaElementTest);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(905); match(KW_SCHEMA_ELEM);
			setState(906); match(LPAREN);
			setState(907); qName();
			setState(908); match(RPAREN);
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

	public static class SchemaAttributeTestContext extends ParserRuleContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public SchemaAttributeTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schemaAttributeTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterSchemaAttributeTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitSchemaAttributeTest(this);
		}
	}

	public final SchemaAttributeTestContext schemaAttributeTest() throws RecognitionException {
		SchemaAttributeTestContext _localctx = new SchemaAttributeTestContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_schemaAttributeTest);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(910); match(KW_SCHEMA_ATTR);
			setState(911); match(LPAREN);
			setState(912); qName();
			setState(913); match(RPAREN);
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

	public static class PiTestContext extends ParserRuleContext {
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public NcNameContext ncName() {
			return getRuleContext(NcNameContext.class,0);
		}
		public PiTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_piTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterPiTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitPiTest(this);
		}
	}

	public final PiTestContext piTest() throws RecognitionException {
		PiTestContext _localctx = new PiTestContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_piTest);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(915); match(KW_PI);
			setState(916); match(LPAREN);
			setState(919);
			switch (_input.LA(1)) {
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_AND:
			case KW_AS:
			case KW_ASCENDING:
			case KW_AT:
			case KW_ATTRIBUTE:
			case KW_BASE_URI:
			case KW_BOUNDARY_SPACE:
			case KW_BY:
			case KW_CASE:
			case KW_CAST:
			case KW_CASTABLE:
			case KW_CHILD:
			case KW_COLLATION:
			case KW_COMMENT:
			case KW_CONSTRUCTION:
			case KW_COPY_NS:
			case KW_DECLARE:
			case KW_DEFAULT:
			case KW_DESCENDANT:
			case KW_DESCENDANT_OR_SELF:
			case KW_DESCENDING:
			case KW_DIV:
			case KW_DOCUMENT:
			case KW_DOCUMENT_NODE:
			case KW_ELEMENT:
			case KW_ELSE:
			case KW_EMPTY:
			case KW_EMPTY_SEQUENCE:
			case KW_ENCODING:
			case KW_EQ:
			case KW_EVERY:
			case KW_EXCEPT:
			case KW_EXTERNAL:
			case KW_FOLLOWING:
			case KW_FOLLOWING_SIBLING:
			case KW_FOR:
			case KW_FUNCTION:
			case KW_GE:
			case KW_GREATEST:
			case KW_GT:
			case KW_IDIV:
			case KW_IF:
			case KW_IMPORT:
			case KW_IN:
			case KW_INHERIT:
			case KW_INSTANCE:
			case KW_INTERSECT:
			case KW_IS:
			case KW_ITEM:
			case KW_LAX:
			case KW_LE:
			case KW_LEAST:
			case KW_LET:
			case KW_LT:
			case KW_MOD:
			case KW_MODULE:
			case KW_NAMESPACE:
			case KW_NE:
			case KW_NO_INHERIT:
			case KW_NO_PRESERVE:
			case KW_NODE:
			case KW_OF:
			case KW_OPTION:
			case KW_OR:
			case KW_ORDER:
			case KW_ORDERED:
			case KW_ORDERING:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
			case KW_PRESERVE:
			case KW_PI:
			case KW_RETURN:
			case KW_SATISFIES:
			case KW_SCHEMA:
			case KW_SCHEMA_ATTR:
			case KW_SCHEMA_ELEM:
			case KW_SELF:
			case KW_SOME:
			case KW_STABLE:
			case KW_STRICT:
			case KW_STRIP:
			case KW_TEXT:
			case KW_THEN:
			case KW_TO:
			case KW_TREAT:
			case KW_TYPESWITCH:
			case KW_UNION:
			case KW_UNORDERED:
			case KW_VALIDATE:
			case KW_VARIABLE:
			case KW_VERSION:
			case KW_WHERE:
			case KW_XQUERY:
			case NCName:
				{
				setState(917); ncName();
				}
				break;
			case Quot:
			case Apos:
				{
				setState(918); stringLiteral();
				}
				break;
			case RPAREN:
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(921); match(RPAREN);
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

	public static class CommentTestContext extends ParserRuleContext {
		public CommentTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commentTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterCommentTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitCommentTest(this);
		}
	}

	public final CommentTestContext commentTest() throws RecognitionException {
		CommentTestContext _localctx = new CommentTestContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_commentTest);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(923); match(KW_COMMENT);
			setState(924); match(LPAREN);
			setState(925); match(RPAREN);
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

	public static class TextTestContext extends ParserRuleContext {
		public TextTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_textTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterTextTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitTextTest(this);
		}
	}

	public final TextTestContext textTest() throws RecognitionException {
		TextTestContext _localctx = new TextTestContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_textTest);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(927); match(KW_TEXT);
			setState(928); match(LPAREN);
			setState(929); match(RPAREN);
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

	public static class AnyKindTestContext extends ParserRuleContext {
		public AnyKindTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anyKindTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterAnyKindTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitAnyKindTest(this);
		}
	}

	public final AnyKindTestContext anyKindTest() throws RecognitionException {
		AnyKindTestContext _localctx = new AnyKindTestContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_anyKindTest);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(931); match(KW_NODE);
			setState(932); match(LPAREN);
			setState(933); match(RPAREN);
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

	public static class QNameContext extends ParserRuleContext {
		public TerminalNode FullQName() { return getToken(XQueryParser.FullQName, 0); }
		public NcNameContext ncName() {
			return getRuleContext(NcNameContext.class,0);
		}
		public QNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterQName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitQName(this);
		}
	}

	public final QNameContext qName() throws RecognitionException {
		QNameContext _localctx = new QNameContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_qName);
		try {
			setState(937);
			switch (_input.LA(1)) {
			case FullQName:
				enterOuterAlt(_localctx, 1);
				{
				setState(935); match(FullQName);
				}
				break;
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_AND:
			case KW_AS:
			case KW_ASCENDING:
			case KW_AT:
			case KW_ATTRIBUTE:
			case KW_BASE_URI:
			case KW_BOUNDARY_SPACE:
			case KW_BY:
			case KW_CASE:
			case KW_CAST:
			case KW_CASTABLE:
			case KW_CHILD:
			case KW_COLLATION:
			case KW_COMMENT:
			case KW_CONSTRUCTION:
			case KW_COPY_NS:
			case KW_DECLARE:
			case KW_DEFAULT:
			case KW_DESCENDANT:
			case KW_DESCENDANT_OR_SELF:
			case KW_DESCENDING:
			case KW_DIV:
			case KW_DOCUMENT:
			case KW_DOCUMENT_NODE:
			case KW_ELEMENT:
			case KW_ELSE:
			case KW_EMPTY:
			case KW_EMPTY_SEQUENCE:
			case KW_ENCODING:
			case KW_EQ:
			case KW_EVERY:
			case KW_EXCEPT:
			case KW_EXTERNAL:
			case KW_FOLLOWING:
			case KW_FOLLOWING_SIBLING:
			case KW_FOR:
			case KW_FUNCTION:
			case KW_GE:
			case KW_GREATEST:
			case KW_GT:
			case KW_IDIV:
			case KW_IF:
			case KW_IMPORT:
			case KW_IN:
			case KW_INHERIT:
			case KW_INSTANCE:
			case KW_INTERSECT:
			case KW_IS:
			case KW_ITEM:
			case KW_LAX:
			case KW_LE:
			case KW_LEAST:
			case KW_LET:
			case KW_LT:
			case KW_MOD:
			case KW_MODULE:
			case KW_NAMESPACE:
			case KW_NE:
			case KW_NO_INHERIT:
			case KW_NO_PRESERVE:
			case KW_NODE:
			case KW_OF:
			case KW_OPTION:
			case KW_OR:
			case KW_ORDER:
			case KW_ORDERED:
			case KW_ORDERING:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
			case KW_PRESERVE:
			case KW_PI:
			case KW_RETURN:
			case KW_SATISFIES:
			case KW_SCHEMA:
			case KW_SCHEMA_ATTR:
			case KW_SCHEMA_ELEM:
			case KW_SELF:
			case KW_SOME:
			case KW_STABLE:
			case KW_STRICT:
			case KW_STRIP:
			case KW_TEXT:
			case KW_THEN:
			case KW_TO:
			case KW_TREAT:
			case KW_TYPESWITCH:
			case KW_UNION:
			case KW_UNORDERED:
			case KW_VALIDATE:
			case KW_VARIABLE:
			case KW_VERSION:
			case KW_WHERE:
			case KW_XQUERY:
			case NCName:
				enterOuterAlt(_localctx, 2);
				{
				setState(936); ncName();
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

	public static class NcNameContext extends ParserRuleContext {
		public KeywordContext keyword() {
			return getRuleContext(KeywordContext.class,0);
		}
		public TerminalNode NCName() { return getToken(XQueryParser.NCName, 0); }
		public NcNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ncName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterNcName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitNcName(this);
		}
	}

	public final NcNameContext ncName() throws RecognitionException {
		NcNameContext _localctx = new NcNameContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_ncName);
		try {
			setState(941);
			switch (_input.LA(1)) {
			case NCName:
				enterOuterAlt(_localctx, 1);
				{
				setState(939); match(NCName);
				}
				break;
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_AND:
			case KW_AS:
			case KW_ASCENDING:
			case KW_AT:
			case KW_ATTRIBUTE:
			case KW_BASE_URI:
			case KW_BOUNDARY_SPACE:
			case KW_BY:
			case KW_CASE:
			case KW_CAST:
			case KW_CASTABLE:
			case KW_CHILD:
			case KW_COLLATION:
			case KW_COMMENT:
			case KW_CONSTRUCTION:
			case KW_COPY_NS:
			case KW_DECLARE:
			case KW_DEFAULT:
			case KW_DESCENDANT:
			case KW_DESCENDANT_OR_SELF:
			case KW_DESCENDING:
			case KW_DIV:
			case KW_DOCUMENT:
			case KW_DOCUMENT_NODE:
			case KW_ELEMENT:
			case KW_ELSE:
			case KW_EMPTY:
			case KW_EMPTY_SEQUENCE:
			case KW_ENCODING:
			case KW_EQ:
			case KW_EVERY:
			case KW_EXCEPT:
			case KW_EXTERNAL:
			case KW_FOLLOWING:
			case KW_FOLLOWING_SIBLING:
			case KW_FOR:
			case KW_FUNCTION:
			case KW_GE:
			case KW_GREATEST:
			case KW_GT:
			case KW_IDIV:
			case KW_IF:
			case KW_IMPORT:
			case KW_IN:
			case KW_INHERIT:
			case KW_INSTANCE:
			case KW_INTERSECT:
			case KW_IS:
			case KW_ITEM:
			case KW_LAX:
			case KW_LE:
			case KW_LEAST:
			case KW_LET:
			case KW_LT:
			case KW_MOD:
			case KW_MODULE:
			case KW_NAMESPACE:
			case KW_NE:
			case KW_NO_INHERIT:
			case KW_NO_PRESERVE:
			case KW_NODE:
			case KW_OF:
			case KW_OPTION:
			case KW_OR:
			case KW_ORDER:
			case KW_ORDERED:
			case KW_ORDERING:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
			case KW_PRESERVE:
			case KW_PI:
			case KW_RETURN:
			case KW_SATISFIES:
			case KW_SCHEMA:
			case KW_SCHEMA_ATTR:
			case KW_SCHEMA_ELEM:
			case KW_SELF:
			case KW_SOME:
			case KW_STABLE:
			case KW_STRICT:
			case KW_STRIP:
			case KW_TEXT:
			case KW_THEN:
			case KW_TO:
			case KW_TREAT:
			case KW_TYPESWITCH:
			case KW_UNION:
			case KW_UNORDERED:
			case KW_VALIDATE:
			case KW_VARIABLE:
			case KW_VERSION:
			case KW_WHERE:
			case KW_XQUERY:
				enterOuterAlt(_localctx, 2);
				{
				setState(940); keyword();
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

	public static class FunctionNameContext extends ParserRuleContext {
		public TerminalNode FullQName() { return getToken(XQueryParser.FullQName, 0); }
		public TerminalNode NCName() { return getToken(XQueryParser.NCName, 0); }
		public KeywordOKForFunctionContext keywordOKForFunction() {
			return getRuleContext(KeywordOKForFunctionContext.class,0);
		}
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitFunctionName(this);
		}
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_functionName);
		try {
			setState(946);
			switch (_input.LA(1)) {
			case FullQName:
				enterOuterAlt(_localctx, 1);
				{
				setState(943); match(FullQName);
				}
				break;
			case NCName:
				enterOuterAlt(_localctx, 2);
				{
				setState(944); match(NCName);
				}
				break;
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_AND:
			case KW_AS:
			case KW_ASCENDING:
			case KW_AT:
			case KW_BASE_URI:
			case KW_BOUNDARY_SPACE:
			case KW_BY:
			case KW_CASE:
			case KW_CAST:
			case KW_CASTABLE:
			case KW_CHILD:
			case KW_COLLATION:
			case KW_CONSTRUCTION:
			case KW_COPY_NS:
			case KW_DECLARE:
			case KW_DEFAULT:
			case KW_DESCENDANT:
			case KW_DESCENDANT_OR_SELF:
			case KW_DESCENDING:
			case KW_DIV:
			case KW_DOCUMENT:
			case KW_ELSE:
			case KW_EMPTY:
			case KW_ENCODING:
			case KW_EQ:
			case KW_EVERY:
			case KW_EXCEPT:
			case KW_EXTERNAL:
			case KW_FOLLOWING:
			case KW_FOLLOWING_SIBLING:
			case KW_FOR:
			case KW_FUNCTION:
			case KW_GE:
			case KW_GREATEST:
			case KW_GT:
			case KW_IDIV:
			case KW_IMPORT:
			case KW_IN:
			case KW_INHERIT:
			case KW_INSTANCE:
			case KW_INTERSECT:
			case KW_IS:
			case KW_LAX:
			case KW_LE:
			case KW_LEAST:
			case KW_LET:
			case KW_LT:
			case KW_MOD:
			case KW_MODULE:
			case KW_NAMESPACE:
			case KW_NE:
			case KW_NO_INHERIT:
			case KW_NO_PRESERVE:
			case KW_OF:
			case KW_OPTION:
			case KW_OR:
			case KW_ORDER:
			case KW_ORDERED:
			case KW_ORDERING:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
			case KW_PRESERVE:
			case KW_RETURN:
			case KW_SATISFIES:
			case KW_SCHEMA:
			case KW_SELF:
			case KW_SOME:
			case KW_STABLE:
			case KW_STRICT:
			case KW_STRIP:
			case KW_THEN:
			case KW_TO:
			case KW_TREAT:
			case KW_UNION:
			case KW_UNORDERED:
			case KW_VALIDATE:
			case KW_VARIABLE:
			case KW_VERSION:
			case KW_WHERE:
			case KW_XQUERY:
				enterOuterAlt(_localctx, 3);
				{
				setState(945); keywordOKForFunction();
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

	public static class KeywordContext extends ParserRuleContext {
		public KeywordNotOKForFunctionContext keywordNotOKForFunction() {
			return getRuleContext(KeywordNotOKForFunctionContext.class,0);
		}
		public KeywordOKForFunctionContext keywordOKForFunction() {
			return getRuleContext(KeywordOKForFunctionContext.class,0);
		}
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitKeyword(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_keyword);
		try {
			setState(950);
			switch (_input.LA(1)) {
			case KW_ANCESTOR:
			case KW_ANCESTOR_OR_SELF:
			case KW_AND:
			case KW_AS:
			case KW_ASCENDING:
			case KW_AT:
			case KW_BASE_URI:
			case KW_BOUNDARY_SPACE:
			case KW_BY:
			case KW_CASE:
			case KW_CAST:
			case KW_CASTABLE:
			case KW_CHILD:
			case KW_COLLATION:
			case KW_CONSTRUCTION:
			case KW_COPY_NS:
			case KW_DECLARE:
			case KW_DEFAULT:
			case KW_DESCENDANT:
			case KW_DESCENDANT_OR_SELF:
			case KW_DESCENDING:
			case KW_DIV:
			case KW_DOCUMENT:
			case KW_ELSE:
			case KW_EMPTY:
			case KW_ENCODING:
			case KW_EQ:
			case KW_EVERY:
			case KW_EXCEPT:
			case KW_EXTERNAL:
			case KW_FOLLOWING:
			case KW_FOLLOWING_SIBLING:
			case KW_FOR:
			case KW_FUNCTION:
			case KW_GE:
			case KW_GREATEST:
			case KW_GT:
			case KW_IDIV:
			case KW_IMPORT:
			case KW_IN:
			case KW_INHERIT:
			case KW_INSTANCE:
			case KW_INTERSECT:
			case KW_IS:
			case KW_LAX:
			case KW_LE:
			case KW_LEAST:
			case KW_LET:
			case KW_LT:
			case KW_MOD:
			case KW_MODULE:
			case KW_NAMESPACE:
			case KW_NE:
			case KW_NO_INHERIT:
			case KW_NO_PRESERVE:
			case KW_OF:
			case KW_OPTION:
			case KW_OR:
			case KW_ORDER:
			case KW_ORDERED:
			case KW_ORDERING:
			case KW_PARENT:
			case KW_PRECEDING:
			case KW_PRECEDING_SIBLING:
			case KW_PRESERVE:
			case KW_RETURN:
			case KW_SATISFIES:
			case KW_SCHEMA:
			case KW_SELF:
			case KW_SOME:
			case KW_STABLE:
			case KW_STRICT:
			case KW_STRIP:
			case KW_THEN:
			case KW_TO:
			case KW_TREAT:
			case KW_UNION:
			case KW_UNORDERED:
			case KW_VALIDATE:
			case KW_VARIABLE:
			case KW_VERSION:
			case KW_WHERE:
			case KW_XQUERY:
				enterOuterAlt(_localctx, 1);
				{
				setState(948); keywordOKForFunction();
				}
				break;
			case KW_ATTRIBUTE:
			case KW_COMMENT:
			case KW_DOCUMENT_NODE:
			case KW_ELEMENT:
			case KW_EMPTY_SEQUENCE:
			case KW_IF:
			case KW_ITEM:
			case KW_NODE:
			case KW_PI:
			case KW_SCHEMA_ATTR:
			case KW_SCHEMA_ELEM:
			case KW_TEXT:
			case KW_TYPESWITCH:
				enterOuterAlt(_localctx, 2);
				{
				setState(949); keywordNotOKForFunction();
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

	public static class KeywordNotOKForFunctionContext extends ParserRuleContext {
		public TerminalNode KW_TEXT() { return getToken(XQueryParser.KW_TEXT, 0); }
		public TerminalNode KW_ATTRIBUTE() { return getToken(XQueryParser.KW_ATTRIBUTE, 0); }
		public TerminalNode KW_NODE() { return getToken(XQueryParser.KW_NODE, 0); }
		public TerminalNode KW_SCHEMA_ELEM() { return getToken(XQueryParser.KW_SCHEMA_ELEM, 0); }
		public TerminalNode KW_PI() { return getToken(XQueryParser.KW_PI, 0); }
		public TerminalNode KW_IF() { return getToken(XQueryParser.KW_IF, 0); }
		public TerminalNode KW_ELEMENT() { return getToken(XQueryParser.KW_ELEMENT, 0); }
		public TerminalNode KW_DOCUMENT_NODE() { return getToken(XQueryParser.KW_DOCUMENT_NODE, 0); }
		public TerminalNode KW_COMMENT() { return getToken(XQueryParser.KW_COMMENT, 0); }
		public TerminalNode KW_EMPTY_SEQUENCE() { return getToken(XQueryParser.KW_EMPTY_SEQUENCE, 0); }
		public TerminalNode KW_TYPESWITCH() { return getToken(XQueryParser.KW_TYPESWITCH, 0); }
		public TerminalNode KW_ITEM() { return getToken(XQueryParser.KW_ITEM, 0); }
		public TerminalNode KW_SCHEMA_ATTR() { return getToken(XQueryParser.KW_SCHEMA_ATTR, 0); }
		public KeywordNotOKForFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keywordNotOKForFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterKeywordNotOKForFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitKeywordNotOKForFunction(this);
		}
	}

	public final KeywordNotOKForFunctionContext keywordNotOKForFunction() throws RecognitionException {
		KeywordNotOKForFunctionContext _localctx = new KeywordNotOKForFunctionContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_keywordNotOKForFunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(952);
			_la = _input.LA(1);
			if ( !(_la==KW_ATTRIBUTE || _la==KW_COMMENT || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (KW_DOCUMENT_NODE - 64)) | (1L << (KW_ELEMENT - 64)) | (1L << (KW_EMPTY_SEQUENCE - 64)) | (1L << (KW_IF - 64)) | (1L << (KW_ITEM - 64)) | (1L << (KW_NODE - 64)) | (1L << (KW_PI - 64)) | (1L << (KW_SCHEMA_ATTR - 64)) | (1L << (KW_SCHEMA_ELEM - 64)) | (1L << (KW_TEXT - 64)) | (1L << (KW_TYPESWITCH - 64)))) != 0)) ) {
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

	public static class KeywordOKForFunctionContext extends ParserRuleContext {
		public TerminalNode KW_COLLATION() { return getToken(XQueryParser.KW_COLLATION, 0); }
		public TerminalNode KW_INSTANCE() { return getToken(XQueryParser.KW_INSTANCE, 0); }
		public TerminalNode KW_AT() { return getToken(XQueryParser.KW_AT, 0); }
		public TerminalNode KW_PRECEDING() { return getToken(XQueryParser.KW_PRECEDING, 0); }
		public TerminalNode KW_MOD() { return getToken(XQueryParser.KW_MOD, 0); }
		public TerminalNode KW_EQ() { return getToken(XQueryParser.KW_EQ, 0); }
		public TerminalNode KW_MODULE() { return getToken(XQueryParser.KW_MODULE, 0); }
		public TerminalNode KW_LET() { return getToken(XQueryParser.KW_LET, 0); }
		public TerminalNode KW_ANCESTOR_OR_SELF() { return getToken(XQueryParser.KW_ANCESTOR_OR_SELF, 0); }
		public TerminalNode KW_VARIABLE() { return getToken(XQueryParser.KW_VARIABLE, 0); }
		public TerminalNode KW_LE() { return getToken(XQueryParser.KW_LE, 0); }
		public TerminalNode KW_OR() { return getToken(XQueryParser.KW_OR, 0); }
		public TerminalNode KW_NE() { return getToken(XQueryParser.KW_NE, 0); }
		public TerminalNode KW_NO_INHERIT() { return getToken(XQueryParser.KW_NO_INHERIT, 0); }
		public TerminalNode KW_STRICT() { return getToken(XQueryParser.KW_STRICT, 0); }
		public TerminalNode KW_UNION() { return getToken(XQueryParser.KW_UNION, 0); }
		public TerminalNode KW_ORDER() { return getToken(XQueryParser.KW_ORDER, 0); }
		public TerminalNode KW_DIV() { return getToken(XQueryParser.KW_DIV, 0); }
		public TerminalNode KW_UNORDERED() { return getToken(XQueryParser.KW_UNORDERED, 0); }
		public TerminalNode KW_DEFAULT() { return getToken(XQueryParser.KW_DEFAULT, 0); }
		public TerminalNode KW_ORDERING() { return getToken(XQueryParser.KW_ORDERING, 0); }
		public TerminalNode KW_PRESERVE() { return getToken(XQueryParser.KW_PRESERVE, 0); }
		public TerminalNode KW_VALIDATE() { return getToken(XQueryParser.KW_VALIDATE, 0); }
		public TerminalNode KW_SELF() { return getToken(XQueryParser.KW_SELF, 0); }
		public TerminalNode KW_IN() { return getToken(XQueryParser.KW_IN, 0); }
		public TerminalNode KW_EXTERNAL() { return getToken(XQueryParser.KW_EXTERNAL, 0); }
		public TerminalNode KW_SOME() { return getToken(XQueryParser.KW_SOME, 0); }
		public TerminalNode KW_AND() { return getToken(XQueryParser.KW_AND, 0); }
		public TerminalNode KW_PARENT() { return getToken(XQueryParser.KW_PARENT, 0); }
		public TerminalNode KW_BOUNDARY_SPACE() { return getToken(XQueryParser.KW_BOUNDARY_SPACE, 0); }
		public TerminalNode KW_PRECEDING_SIBLING() { return getToken(XQueryParser.KW_PRECEDING_SIBLING, 0); }
		public TerminalNode KW_WHERE() { return getToken(XQueryParser.KW_WHERE, 0); }
		public TerminalNode KW_STABLE() { return getToken(XQueryParser.KW_STABLE, 0); }
		public TerminalNode KW_CAST() { return getToken(XQueryParser.KW_CAST, 0); }
		public TerminalNode KW_NAMESPACE() { return getToken(XQueryParser.KW_NAMESPACE, 0); }
		public TerminalNode KW_DESCENDANT_OR_SELF() { return getToken(XQueryParser.KW_DESCENDANT_OR_SELF, 0); }
		public TerminalNode KW_RETURN() { return getToken(XQueryParser.KW_RETURN, 0); }
		public TerminalNode KW_ANCESTOR() { return getToken(XQueryParser.KW_ANCESTOR, 0); }
		public TerminalNode KW_CASE() { return getToken(XQueryParser.KW_CASE, 0); }
		public TerminalNode KW_ASCENDING() { return getToken(XQueryParser.KW_ASCENDING, 0); }
		public TerminalNode KW_COPY_NS() { return getToken(XQueryParser.KW_COPY_NS, 0); }
		public TerminalNode KW_IMPORT() { return getToken(XQueryParser.KW_IMPORT, 0); }
		public TerminalNode KW_DESCENDING() { return getToken(XQueryParser.KW_DESCENDING, 0); }
		public TerminalNode KW_INHERIT() { return getToken(XQueryParser.KW_INHERIT, 0); }
		public TerminalNode KW_INTERSECT() { return getToken(XQueryParser.KW_INTERSECT, 0); }
		public TerminalNode KW_GT() { return getToken(XQueryParser.KW_GT, 0); }
		public TerminalNode KW_NO_PRESERVE() { return getToken(XQueryParser.KW_NO_PRESERVE, 0); }
		public TerminalNode KW_OF() { return getToken(XQueryParser.KW_OF, 0); }
		public TerminalNode KW_LEAST() { return getToken(XQueryParser.KW_LEAST, 0); }
		public TerminalNode KW_IDIV() { return getToken(XQueryParser.KW_IDIV, 0); }
		public TerminalNode KW_AS() { return getToken(XQueryParser.KW_AS, 0); }
		public TerminalNode KW_GE() { return getToken(XQueryParser.KW_GE, 0); }
		public TerminalNode KW_EMPTY() { return getToken(XQueryParser.KW_EMPTY, 0); }
		public TerminalNode KW_SATISFIES() { return getToken(XQueryParser.KW_SATISFIES, 0); }
		public TerminalNode KW_THEN() { return getToken(XQueryParser.KW_THEN, 0); }
		public TerminalNode KW_EVERY() { return getToken(XQueryParser.KW_EVERY, 0); }
		public TerminalNode KW_DOCUMENT() { return getToken(XQueryParser.KW_DOCUMENT, 0); }
		public TerminalNode KW_OPTION() { return getToken(XQueryParser.KW_OPTION, 0); }
		public TerminalNode KW_BY() { return getToken(XQueryParser.KW_BY, 0); }
		public TerminalNode KW_IS() { return getToken(XQueryParser.KW_IS, 0); }
		public TerminalNode KW_LT() { return getToken(XQueryParser.KW_LT, 0); }
		public TerminalNode KW_DESCENDANT() { return getToken(XQueryParser.KW_DESCENDANT, 0); }
		public TerminalNode KW_LAX() { return getToken(XQueryParser.KW_LAX, 0); }
		public TerminalNode KW_TREAT() { return getToken(XQueryParser.KW_TREAT, 0); }
		public TerminalNode KW_CASTABLE() { return getToken(XQueryParser.KW_CASTABLE, 0); }
		public TerminalNode KW_ORDERED() { return getToken(XQueryParser.KW_ORDERED, 0); }
		public TerminalNode KW_TO() { return getToken(XQueryParser.KW_TO, 0); }
		public TerminalNode KW_SCHEMA() { return getToken(XQueryParser.KW_SCHEMA, 0); }
		public TerminalNode KW_DECLARE() { return getToken(XQueryParser.KW_DECLARE, 0); }
		public TerminalNode KW_BASE_URI() { return getToken(XQueryParser.KW_BASE_URI, 0); }
		public TerminalNode KW_GREATEST() { return getToken(XQueryParser.KW_GREATEST, 0); }
		public TerminalNode KW_CHILD() { return getToken(XQueryParser.KW_CHILD, 0); }
		public TerminalNode KW_ELSE() { return getToken(XQueryParser.KW_ELSE, 0); }
		public TerminalNode KW_EXCEPT() { return getToken(XQueryParser.KW_EXCEPT, 0); }
		public TerminalNode KW_FOLLOWING_SIBLING() { return getToken(XQueryParser.KW_FOLLOWING_SIBLING, 0); }
		public TerminalNode KW_STRIP() { return getToken(XQueryParser.KW_STRIP, 0); }
		public TerminalNode KW_FOR() { return getToken(XQueryParser.KW_FOR, 0); }
		public TerminalNode KW_CONSTRUCTION() { return getToken(XQueryParser.KW_CONSTRUCTION, 0); }
		public TerminalNode KW_FOLLOWING() { return getToken(XQueryParser.KW_FOLLOWING, 0); }
		public TerminalNode KW_VERSION() { return getToken(XQueryParser.KW_VERSION, 0); }
		public TerminalNode KW_XQUERY() { return getToken(XQueryParser.KW_XQUERY, 0); }
		public TerminalNode KW_FUNCTION() { return getToken(XQueryParser.KW_FUNCTION, 0); }
		public TerminalNode KW_ENCODING() { return getToken(XQueryParser.KW_ENCODING, 0); }
		public KeywordOKForFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keywordOKForFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterKeywordOKForFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitKeywordOKForFunction(this);
		}
	}

	public final KeywordOKForFunctionContext keywordOKForFunction() throws RecognitionException {
		KeywordOKForFunctionContext _localctx = new KeywordOKForFunctionContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_keywordOKForFunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(954);
			_la = _input.LA(1);
			if ( !(((((_la - 39)) & ~0x3f) == 0 && ((1L << (_la - 39)) & ((1L << (KW_ANCESTOR - 39)) | (1L << (KW_ANCESTOR_OR_SELF - 39)) | (1L << (KW_AND - 39)) | (1L << (KW_AS - 39)) | (1L << (KW_ASCENDING - 39)) | (1L << (KW_AT - 39)) | (1L << (KW_BASE_URI - 39)) | (1L << (KW_BOUNDARY_SPACE - 39)) | (1L << (KW_BY - 39)) | (1L << (KW_CASE - 39)) | (1L << (KW_CAST - 39)) | (1L << (KW_CASTABLE - 39)) | (1L << (KW_CHILD - 39)) | (1L << (KW_COLLATION - 39)) | (1L << (KW_CONSTRUCTION - 39)) | (1L << (KW_COPY_NS - 39)) | (1L << (KW_DECLARE - 39)) | (1L << (KW_DEFAULT - 39)) | (1L << (KW_DESCENDANT - 39)) | (1L << (KW_DESCENDANT_OR_SELF - 39)) | (1L << (KW_DESCENDING - 39)) | (1L << (KW_DIV - 39)) | (1L << (KW_DOCUMENT - 39)) | (1L << (KW_ELSE - 39)) | (1L << (KW_EMPTY - 39)) | (1L << (KW_ENCODING - 39)) | (1L << (KW_EQ - 39)) | (1L << (KW_EVERY - 39)) | (1L << (KW_EXCEPT - 39)) | (1L << (KW_EXTERNAL - 39)) | (1L << (KW_FOLLOWING - 39)) | (1L << (KW_FOLLOWING_SIBLING - 39)) | (1L << (KW_FOR - 39)) | (1L << (KW_FUNCTION - 39)) | (1L << (KW_GE - 39)) | (1L << (KW_GREATEST - 39)) | (1L << (KW_GT - 39)) | (1L << (KW_IDIV - 39)) | (1L << (KW_IMPORT - 39)) | (1L << (KW_IN - 39)) | (1L << (KW_INHERIT - 39)) | (1L << (KW_INSTANCE - 39)) | (1L << (KW_INTERSECT - 39)) | (1L << (KW_IS - 39)) | (1L << (KW_LAX - 39)) | (1L << (KW_LE - 39)) | (1L << (KW_LEAST - 39)) | (1L << (KW_LET - 39)) | (1L << (KW_LT - 39)) | (1L << (KW_MOD - 39)) | (1L << (KW_MODULE - 39)) | (1L << (KW_NAMESPACE - 39)) | (1L << (KW_NE - 39)) | (1L << (KW_NO_INHERIT - 39)) | (1L << (KW_NO_PRESERVE - 39)) | (1L << (KW_OF - 39)))) != 0) || ((((_la - 103)) & ~0x3f) == 0 && ((1L << (_la - 103)) & ((1L << (KW_OPTION - 103)) | (1L << (KW_OR - 103)) | (1L << (KW_ORDER - 103)) | (1L << (KW_ORDERED - 103)) | (1L << (KW_ORDERING - 103)) | (1L << (KW_PARENT - 103)) | (1L << (KW_PRECEDING - 103)) | (1L << (KW_PRECEDING_SIBLING - 103)) | (1L << (KW_PRESERVE - 103)) | (1L << (KW_RETURN - 103)) | (1L << (KW_SATISFIES - 103)) | (1L << (KW_SCHEMA - 103)) | (1L << (KW_SELF - 103)) | (1L << (KW_SOME - 103)) | (1L << (KW_STABLE - 103)) | (1L << (KW_STRICT - 103)) | (1L << (KW_STRIP - 103)) | (1L << (KW_THEN - 103)) | (1L << (KW_TO - 103)) | (1L << (KW_TREAT - 103)) | (1L << (KW_UNION - 103)) | (1L << (KW_UNORDERED - 103)) | (1L << (KW_VALIDATE - 103)) | (1L << (KW_VARIABLE - 103)) | (1L << (KW_VERSION - 103)) | (1L << (KW_WHERE - 103)) | (1L << (KW_XQUERY - 103)))) != 0)) ) {
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

	public static class StringLiteralContext extends ParserRuleContext {
		public TerminalNode Apos(int i) {
			return getToken(XQueryParser.Apos, i);
		}
		public List<TerminalNode> LBRACE() { return getTokens(XQueryParser.LBRACE); }
		public TerminalNode RBRACE(int i) {
			return getToken(XQueryParser.RBRACE, i);
		}
		public TerminalNode CharRef(int i) {
			return getToken(XQueryParser.CharRef, i);
		}
		public TerminalNode LANGLE(int i) {
			return getToken(XQueryParser.LANGLE, i);
		}
		public List<TerminalNode> PredefinedEntityRef() { return getTokens(XQueryParser.PredefinedEntityRef); }
		public List<TerminalNode> LANGLE() { return getTokens(XQueryParser.LANGLE); }
		public List<TerminalNode> RBRACE() { return getTokens(XQueryParser.RBRACE); }
		public List<TerminalNode> Quot() { return getTokens(XQueryParser.Quot); }
		public TerminalNode Quot(int i) {
			return getToken(XQueryParser.Quot, i);
		}
		public TerminalNode PredefinedEntityRef(int i) {
			return getToken(XQueryParser.PredefinedEntityRef, i);
		}
		public List<TerminalNode> CharRef() { return getTokens(XQueryParser.CharRef); }
		public NoQuotesNoBracesNoAmpNoLAngContext noQuotesNoBracesNoAmpNoLAng(int i) {
			return getRuleContext(NoQuotesNoBracesNoAmpNoLAngContext.class,i);
		}
		public List<NoQuotesNoBracesNoAmpNoLAngContext> noQuotesNoBracesNoAmpNoLAng() {
			return getRuleContexts(NoQuotesNoBracesNoAmpNoLAngContext.class);
		}
		public List<TerminalNode> Apos() { return getTokens(XQueryParser.Apos); }
		public TerminalNode LBRACE(int i) {
			return getToken(XQueryParser.LBRACE, i);
		}
		public StringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitStringLiteral(this);
		}
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_stringLiteral);
		try {
			int _alt;
			setState(988);
			switch (_input.LA(1)) {
			case Quot:
				enterOuterAlt(_localctx, 1);
				{
				setState(956); match(Quot);
				setState(968);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						setState(966);
						switch (_input.LA(1)) {
						case Quot:
							{
							setState(957); match(Quot);
							setState(958); match(Quot);
							}
							break;
						case PredefinedEntityRef:
							{
							setState(959); match(PredefinedEntityRef);
							}
							break;
						case CharRef:
							{
							setState(960); match(CharRef);
							}
							break;
						case Apos:
							{
							setState(961); match(Apos);
							}
							break;
						case LBRACE:
							{
							setState(962); match(LBRACE);
							}
							break;
						case RBRACE:
							{
							setState(963); match(RBRACE);
							}
							break;
						case LANGLE:
							{
							setState(964); match(LANGLE);
							}
							break;
						case IntegerLiteral:
						case DecimalLiteral:
						case DoubleLiteral:
						case PRAGMA:
						case EQUAL:
						case NOT_EQUAL:
						case LPAREN:
						case RPAREN:
						case LBRACKET:
						case RBRACKET:
						case STAR:
						case PLUS:
						case MINUS:
						case COMMA:
						case DOT:
						case DDOT:
						case COLON:
						case COLON_EQ:
						case SEMICOLON:
						case SLASH:
						case DSLASH:
						case VBAR:
						case RANGLE:
						case QUESTION:
						case AT:
						case DOLLAR:
						case KW_ANCESTOR:
						case KW_ANCESTOR_OR_SELF:
						case KW_AND:
						case KW_AS:
						case KW_ASCENDING:
						case KW_AT:
						case KW_ATTRIBUTE:
						case KW_BASE_URI:
						case KW_BOUNDARY_SPACE:
						case KW_BY:
						case KW_CASE:
						case KW_CAST:
						case KW_CASTABLE:
						case KW_CHILD:
						case KW_COLLATION:
						case KW_COMMENT:
						case KW_CONSTRUCTION:
						case KW_COPY_NS:
						case KW_DECLARE:
						case KW_DEFAULT:
						case KW_DESCENDANT:
						case KW_DESCENDANT_OR_SELF:
						case KW_DESCENDING:
						case KW_DIV:
						case KW_DOCUMENT:
						case KW_DOCUMENT_NODE:
						case KW_ELEMENT:
						case KW_ELSE:
						case KW_EMPTY:
						case KW_EMPTY_SEQUENCE:
						case KW_ENCODING:
						case KW_EQ:
						case KW_EVERY:
						case KW_EXCEPT:
						case KW_EXTERNAL:
						case KW_FOLLOWING:
						case KW_FOLLOWING_SIBLING:
						case KW_FOR:
						case KW_FUNCTION:
						case KW_GE:
						case KW_GREATEST:
						case KW_GT:
						case KW_IDIV:
						case KW_IF:
						case KW_IMPORT:
						case KW_IN:
						case KW_INHERIT:
						case KW_INSTANCE:
						case KW_INTERSECT:
						case KW_IS:
						case KW_ITEM:
						case KW_LAX:
						case KW_LE:
						case KW_LEAST:
						case KW_LET:
						case KW_LT:
						case KW_MOD:
						case KW_MODULE:
						case KW_NAMESPACE:
						case KW_NE:
						case KW_NO_INHERIT:
						case KW_NO_PRESERVE:
						case KW_NODE:
						case KW_OF:
						case KW_OPTION:
						case KW_OR:
						case KW_ORDER:
						case KW_ORDERED:
						case KW_ORDERING:
						case KW_PARENT:
						case KW_PRECEDING:
						case KW_PRECEDING_SIBLING:
						case KW_PRESERVE:
						case KW_PI:
						case KW_RETURN:
						case KW_SATISFIES:
						case KW_SCHEMA:
						case KW_SCHEMA_ATTR:
						case KW_SCHEMA_ELEM:
						case KW_SELF:
						case KW_SOME:
						case KW_STABLE:
						case KW_STRICT:
						case KW_STRIP:
						case KW_TEXT:
						case KW_THEN:
						case KW_TO:
						case KW_TREAT:
						case KW_TYPESWITCH:
						case KW_UNION:
						case KW_UNORDERED:
						case KW_VALIDATE:
						case KW_VARIABLE:
						case KW_VERSION:
						case KW_WHERE:
						case KW_XQUERY:
						case FullQName:
						case NCNameWithLocalWildcard:
						case NCNameWithPrefixWildcard:
						case NCName:
						case ContentChar:
							{
							setState(965); noQuotesNoBracesNoAmpNoLAng();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(970);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,100,_ctx);
				}
				setState(971); match(Quot);
				}
				break;
			case Apos:
				enterOuterAlt(_localctx, 2);
				{
				setState(972); match(Apos);
				setState(984);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,102,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						setState(982);
						switch (_input.LA(1)) {
						case Apos:
							{
							setState(973); match(Apos);
							setState(974); match(Apos);
							}
							break;
						case PredefinedEntityRef:
							{
							setState(975); match(PredefinedEntityRef);
							}
							break;
						case CharRef:
							{
							setState(976); match(CharRef);
							}
							break;
						case Quot:
							{
							setState(977); match(Quot);
							}
							break;
						case LBRACE:
							{
							setState(978); match(LBRACE);
							}
							break;
						case RBRACE:
							{
							setState(979); match(RBRACE);
							}
							break;
						case LANGLE:
							{
							setState(980); match(LANGLE);
							}
							break;
						case IntegerLiteral:
						case DecimalLiteral:
						case DoubleLiteral:
						case PRAGMA:
						case EQUAL:
						case NOT_EQUAL:
						case LPAREN:
						case RPAREN:
						case LBRACKET:
						case RBRACKET:
						case STAR:
						case PLUS:
						case MINUS:
						case COMMA:
						case DOT:
						case DDOT:
						case COLON:
						case COLON_EQ:
						case SEMICOLON:
						case SLASH:
						case DSLASH:
						case VBAR:
						case RANGLE:
						case QUESTION:
						case AT:
						case DOLLAR:
						case KW_ANCESTOR:
						case KW_ANCESTOR_OR_SELF:
						case KW_AND:
						case KW_AS:
						case KW_ASCENDING:
						case KW_AT:
						case KW_ATTRIBUTE:
						case KW_BASE_URI:
						case KW_BOUNDARY_SPACE:
						case KW_BY:
						case KW_CASE:
						case KW_CAST:
						case KW_CASTABLE:
						case KW_CHILD:
						case KW_COLLATION:
						case KW_COMMENT:
						case KW_CONSTRUCTION:
						case KW_COPY_NS:
						case KW_DECLARE:
						case KW_DEFAULT:
						case KW_DESCENDANT:
						case KW_DESCENDANT_OR_SELF:
						case KW_DESCENDING:
						case KW_DIV:
						case KW_DOCUMENT:
						case KW_DOCUMENT_NODE:
						case KW_ELEMENT:
						case KW_ELSE:
						case KW_EMPTY:
						case KW_EMPTY_SEQUENCE:
						case KW_ENCODING:
						case KW_EQ:
						case KW_EVERY:
						case KW_EXCEPT:
						case KW_EXTERNAL:
						case KW_FOLLOWING:
						case KW_FOLLOWING_SIBLING:
						case KW_FOR:
						case KW_FUNCTION:
						case KW_GE:
						case KW_GREATEST:
						case KW_GT:
						case KW_IDIV:
						case KW_IF:
						case KW_IMPORT:
						case KW_IN:
						case KW_INHERIT:
						case KW_INSTANCE:
						case KW_INTERSECT:
						case KW_IS:
						case KW_ITEM:
						case KW_LAX:
						case KW_LE:
						case KW_LEAST:
						case KW_LET:
						case KW_LT:
						case KW_MOD:
						case KW_MODULE:
						case KW_NAMESPACE:
						case KW_NE:
						case KW_NO_INHERIT:
						case KW_NO_PRESERVE:
						case KW_NODE:
						case KW_OF:
						case KW_OPTION:
						case KW_OR:
						case KW_ORDER:
						case KW_ORDERED:
						case KW_ORDERING:
						case KW_PARENT:
						case KW_PRECEDING:
						case KW_PRECEDING_SIBLING:
						case KW_PRESERVE:
						case KW_PI:
						case KW_RETURN:
						case KW_SATISFIES:
						case KW_SCHEMA:
						case KW_SCHEMA_ATTR:
						case KW_SCHEMA_ELEM:
						case KW_SELF:
						case KW_SOME:
						case KW_STABLE:
						case KW_STRICT:
						case KW_STRIP:
						case KW_TEXT:
						case KW_THEN:
						case KW_TO:
						case KW_TREAT:
						case KW_TYPESWITCH:
						case KW_UNION:
						case KW_UNORDERED:
						case KW_VALIDATE:
						case KW_VARIABLE:
						case KW_VERSION:
						case KW_WHERE:
						case KW_XQUERY:
						case FullQName:
						case NCNameWithLocalWildcard:
						case NCNameWithPrefixWildcard:
						case NCName:
						case ContentChar:
							{
							setState(981); noQuotesNoBracesNoAmpNoLAng();
							}
							break;
						default:
							throw new NoViableAltException(this);
						}
						} 
					}
					setState(986);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,102,_ctx);
				}
				setState(987); match(Apos);
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

	public static class NoQuotesNoBracesNoAmpNoLAngContext extends ParserRuleContext {
		public List<TerminalNode> COLON_EQ() { return getTokens(XQueryParser.COLON_EQ); }
		public TerminalNode DOLLAR(int i) {
			return getToken(XQueryParser.DOLLAR, i);
		}
		public List<TerminalNode> QUESTION() { return getTokens(XQueryParser.QUESTION); }
		public TerminalNode RANGLE(int i) {
			return getToken(XQueryParser.RANGLE, i);
		}
		public TerminalNode SEMICOLON(int i) {
			return getToken(XQueryParser.SEMICOLON, i);
		}
		public List<KeywordContext> keyword() {
			return getRuleContexts(KeywordContext.class);
		}
		public TerminalNode DDOT(int i) {
			return getToken(XQueryParser.DDOT, i);
		}
		public TerminalNode NCNameWithLocalWildcard(int i) {
			return getToken(XQueryParser.NCNameWithLocalWildcard, i);
		}
		public List<TerminalNode> DOLLAR() { return getTokens(XQueryParser.DOLLAR); }
		public TerminalNode LBRACKET(int i) {
			return getToken(XQueryParser.LBRACKET, i);
		}
		public List<TerminalNode> NOT_EQUAL() { return getTokens(XQueryParser.NOT_EQUAL); }
		public List<TerminalNode> RBRACKET() { return getTokens(XQueryParser.RBRACKET); }
		public TerminalNode NCNameWithPrefixWildcard(int i) {
			return getToken(XQueryParser.NCNameWithPrefixWildcard, i);
		}
		public List<TerminalNode> DoubleLiteral() { return getTokens(XQueryParser.DoubleLiteral); }
		public TerminalNode EQUAL(int i) {
			return getToken(XQueryParser.EQUAL, i);
		}
		public List<TerminalNode> DecimalLiteral() { return getTokens(XQueryParser.DecimalLiteral); }
		public TerminalNode STAR(int i) {
			return getToken(XQueryParser.STAR, i);
		}
		public List<TerminalNode> DDOT() { return getTokens(XQueryParser.DDOT); }
		public TerminalNode RPAREN(int i) {
			return getToken(XQueryParser.RPAREN, i);
		}
		public TerminalNode IntegerLiteral(int i) {
			return getToken(XQueryParser.IntegerLiteral, i);
		}
		public TerminalNode VBAR(int i) {
			return getToken(XQueryParser.VBAR, i);
		}
		public TerminalNode NCName(int i) {
			return getToken(XQueryParser.NCName, i);
		}
		public List<TerminalNode> EQUAL() { return getTokens(XQueryParser.EQUAL); }
		public List<TerminalNode> AT() { return getTokens(XQueryParser.AT); }
		public List<TerminalNode> LPAREN() { return getTokens(XQueryParser.LPAREN); }
		public TerminalNode NOT_EQUAL(int i) {
			return getToken(XQueryParser.NOT_EQUAL, i);
		}
		public TerminalNode DOT(int i) {
			return getToken(XQueryParser.DOT, i);
		}
		public TerminalNode PLUS(int i) {
			return getToken(XQueryParser.PLUS, i);
		}
		public TerminalNode MINUS(int i) {
			return getToken(XQueryParser.MINUS, i);
		}
		public TerminalNode AT(int i) {
			return getToken(XQueryParser.AT, i);
		}
		public List<TerminalNode> DOT() { return getTokens(XQueryParser.DOT); }
		public TerminalNode QUESTION(int i) {
			return getToken(XQueryParser.QUESTION, i);
		}
		public TerminalNode FullQName(int i) {
			return getToken(XQueryParser.FullQName, i);
		}
		public TerminalNode DSLASH(int i) {
			return getToken(XQueryParser.DSLASH, i);
		}
		public List<TerminalNode> IntegerLiteral() { return getTokens(XQueryParser.IntegerLiteral); }
		public List<TerminalNode> NCNameWithPrefixWildcard() { return getTokens(XQueryParser.NCNameWithPrefixWildcard); }
		public List<TerminalNode> COLON() { return getTokens(XQueryParser.COLON); }
		public List<TerminalNode> FullQName() { return getTokens(XQueryParser.FullQName); }
		public List<TerminalNode> NCNameWithLocalWildcard() { return getTokens(XQueryParser.NCNameWithLocalWildcard); }
		public List<TerminalNode> STAR() { return getTokens(XQueryParser.STAR); }
		public List<TerminalNode> VBAR() { return getTokens(XQueryParser.VBAR); }
		public List<TerminalNode> PRAGMA() { return getTokens(XQueryParser.PRAGMA); }
		public TerminalNode SLASH(int i) {
			return getToken(XQueryParser.SLASH, i);
		}
		public List<TerminalNode> NCName() { return getTokens(XQueryParser.NCName); }
		public TerminalNode DecimalLiteral(int i) {
			return getToken(XQueryParser.DecimalLiteral, i);
		}
		public List<TerminalNode> ContentChar() { return getTokens(XQueryParser.ContentChar); }
		public TerminalNode COLON(int i) {
			return getToken(XQueryParser.COLON, i);
		}
		public List<TerminalNode> PLUS() { return getTokens(XQueryParser.PLUS); }
		public TerminalNode RBRACKET(int i) {
			return getToken(XQueryParser.RBRACKET, i);
		}
		public List<TerminalNode> SEMICOLON() { return getTokens(XQueryParser.SEMICOLON); }
		public TerminalNode PRAGMA(int i) {
			return getToken(XQueryParser.PRAGMA, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(XQueryParser.MINUS); }
		public TerminalNode ContentChar(int i) {
			return getToken(XQueryParser.ContentChar, i);
		}
		public List<TerminalNode> LBRACKET() { return getTokens(XQueryParser.LBRACKET); }
		public TerminalNode COMMA(int i) {
			return getToken(XQueryParser.COMMA, i);
		}
		public List<TerminalNode> RANGLE() { return getTokens(XQueryParser.RANGLE); }
		public TerminalNode LPAREN(int i) {
			return getToken(XQueryParser.LPAREN, i);
		}
		public KeywordContext keyword(int i) {
			return getRuleContext(KeywordContext.class,i);
		}
		public List<TerminalNode> DSLASH() { return getTokens(XQueryParser.DSLASH); }
		public List<TerminalNode> COMMA() { return getTokens(XQueryParser.COMMA); }
		public List<TerminalNode> RPAREN() { return getTokens(XQueryParser.RPAREN); }
		public TerminalNode DoubleLiteral(int i) {
			return getToken(XQueryParser.DoubleLiteral, i);
		}
		public TerminalNode COLON_EQ(int i) {
			return getToken(XQueryParser.COLON_EQ, i);
		}
		public List<TerminalNode> SLASH() { return getTokens(XQueryParser.SLASH); }
		public NoQuotesNoBracesNoAmpNoLAngContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_noQuotesNoBracesNoAmpNoLAng; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).enterNoQuotesNoBracesNoAmpNoLAng(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof XQueryParserListener ) ((XQueryParserListener)listener).exitNoQuotesNoBracesNoAmpNoLAng(this);
		}
	}

	public final NoQuotesNoBracesNoAmpNoLAngContext noQuotesNoBracesNoAmpNoLAng() throws RecognitionException {
		NoQuotesNoBracesNoAmpNoLAngContext _localctx = new NoQuotesNoBracesNoAmpNoLAngContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_noQuotesNoBracesNoAmpNoLAng);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(992); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(992);
					switch (_input.LA(1)) {
					case KW_ANCESTOR:
					case KW_ANCESTOR_OR_SELF:
					case KW_AND:
					case KW_AS:
					case KW_ASCENDING:
					case KW_AT:
					case KW_ATTRIBUTE:
					case KW_BASE_URI:
					case KW_BOUNDARY_SPACE:
					case KW_BY:
					case KW_CASE:
					case KW_CAST:
					case KW_CASTABLE:
					case KW_CHILD:
					case KW_COLLATION:
					case KW_COMMENT:
					case KW_CONSTRUCTION:
					case KW_COPY_NS:
					case KW_DECLARE:
					case KW_DEFAULT:
					case KW_DESCENDANT:
					case KW_DESCENDANT_OR_SELF:
					case KW_DESCENDING:
					case KW_DIV:
					case KW_DOCUMENT:
					case KW_DOCUMENT_NODE:
					case KW_ELEMENT:
					case KW_ELSE:
					case KW_EMPTY:
					case KW_EMPTY_SEQUENCE:
					case KW_ENCODING:
					case KW_EQ:
					case KW_EVERY:
					case KW_EXCEPT:
					case KW_EXTERNAL:
					case KW_FOLLOWING:
					case KW_FOLLOWING_SIBLING:
					case KW_FOR:
					case KW_FUNCTION:
					case KW_GE:
					case KW_GREATEST:
					case KW_GT:
					case KW_IDIV:
					case KW_IF:
					case KW_IMPORT:
					case KW_IN:
					case KW_INHERIT:
					case KW_INSTANCE:
					case KW_INTERSECT:
					case KW_IS:
					case KW_ITEM:
					case KW_LAX:
					case KW_LE:
					case KW_LEAST:
					case KW_LET:
					case KW_LT:
					case KW_MOD:
					case KW_MODULE:
					case KW_NAMESPACE:
					case KW_NE:
					case KW_NO_INHERIT:
					case KW_NO_PRESERVE:
					case KW_NODE:
					case KW_OF:
					case KW_OPTION:
					case KW_OR:
					case KW_ORDER:
					case KW_ORDERED:
					case KW_ORDERING:
					case KW_PARENT:
					case KW_PRECEDING:
					case KW_PRECEDING_SIBLING:
					case KW_PRESERVE:
					case KW_PI:
					case KW_RETURN:
					case KW_SATISFIES:
					case KW_SCHEMA:
					case KW_SCHEMA_ATTR:
					case KW_SCHEMA_ELEM:
					case KW_SELF:
					case KW_SOME:
					case KW_STABLE:
					case KW_STRICT:
					case KW_STRIP:
					case KW_TEXT:
					case KW_THEN:
					case KW_TO:
					case KW_TREAT:
					case KW_TYPESWITCH:
					case KW_UNION:
					case KW_UNORDERED:
					case KW_VALIDATE:
					case KW_VARIABLE:
					case KW_VERSION:
					case KW_WHERE:
					case KW_XQUERY:
						{
						setState(990); keyword();
						}
						break;
					case IntegerLiteral:
					case DecimalLiteral:
					case DoubleLiteral:
					case PRAGMA:
					case EQUAL:
					case NOT_EQUAL:
					case LPAREN:
					case RPAREN:
					case LBRACKET:
					case RBRACKET:
					case STAR:
					case PLUS:
					case MINUS:
					case COMMA:
					case DOT:
					case DDOT:
					case COLON:
					case COLON_EQ:
					case SEMICOLON:
					case SLASH:
					case DSLASH:
					case VBAR:
					case RANGLE:
					case QUESTION:
					case AT:
					case DOLLAR:
					case FullQName:
					case NCNameWithLocalWildcard:
					case NCNameWithPrefixWildcard:
					case NCName:
					case ContentChar:
						{
						setState(991);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << DecimalLiteral) | (1L << DoubleLiteral) | (1L << PRAGMA) | (1L << EQUAL) | (1L << NOT_EQUAL) | (1L << LPAREN) | (1L << RPAREN) | (1L << LBRACKET) | (1L << RBRACKET) | (1L << STAR) | (1L << PLUS) | (1L << MINUS) | (1L << COMMA) | (1L << DOT) | (1L << DDOT) | (1L << COLON) | (1L << COLON_EQ) | (1L << SEMICOLON) | (1L << SLASH) | (1L << DSLASH) | (1L << VBAR) | (1L << RANGLE) | (1L << QUESTION) | (1L << AT) | (1L << DOLLAR))) != 0) || ((((_la - 135)) & ~0x3f) == 0 && ((1L << (_la - 135)) & ((1L << (FullQName - 135)) | (1L << (NCNameWithLocalWildcard - 135)) | (1L << (NCNameWithPrefixWildcard - 135)) | (1L << (NCName - 135)) | (1L << (ContentChar - 135)))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						consume();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(994); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,105,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 29: return orExpr_sempred((OrExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean orExpr_sempred(OrExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return precpred(_ctx, 13);
		case 1: return precpred(_ctx, 12);
		case 2: return precpred(_ctx, 11);
		case 3: return precpred(_ctx, 10);
		case 4: return precpred(_ctx, 9);
		case 5: return precpred(_ctx, 8);
		case 6: return precpred(_ctx, 7);
		case 7: return precpred(_ctx, 6);
		case 8: return precpred(_ctx, 17);
		case 9: return precpred(_ctx, 16);
		case 10: return precpred(_ctx, 15);
		case 11: return precpred(_ctx, 14);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\u008e\u03e7\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\3\2\5\2\u009a\n\2\3\2\3\2\5\2\u009e\n\2\3\3\3\3"+
		"\3\3\3\3\3\3\5\3\u00a5\n\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\5\7\u00bb\n\7\3\7\3\7\7\7\u00bf\n"+
		"\7\f\7\16\7\u00c2\13\7\3\7\3\7\3\7\5\7\u00c7\n\7\3\7\3\7\7\7\u00cb\n\7"+
		"\f\7\16\7\u00ce\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\5\t\u00f0\n\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\5\13\u0101\n\13\3\13\3\13\3\13\3\13\3\13\7\13\u0108"+
		"\n\13\f\13\16\13\u010b\13\13\5\13\u010d\n\13\3\f\3\f\3\f\3\f\3\f\3\f\5"+
		"\f\u0115\n\f\3\f\3\f\3\f\3\f\3\f\7\f\u011c\n\f\f\f\16\f\u011f\13\f\5\f"+
		"\u0121\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u0128\n\r\3\r\3\r\3\r\5\r\u012d\n\r"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u0136\n\16\f\16\16\16\u0139\13"+
		"\16\5\16\u013b\n\16\3\16\3\16\3\16\5\16\u0140\n\16\3\16\3\16\3\16\3\16"+
		"\3\16\5\16\u0147\n\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\5\20\u0151"+
		"\n\20\3\21\3\21\3\21\7\21\u0156\n\21\f\21\16\21\u0159\13\21\3\22\3\22"+
		"\3\22\3\22\3\22\5\22\u0160\n\22\3\23\3\23\6\23\u0164\n\23\r\23\16\23\u0165"+
		"\3\23\3\23\5\23\u016a\n\23\3\23\5\23\u016d\n\23\3\23\3\23\3\23\3\24\3"+
		"\24\3\24\3\24\7\24\u0176\n\24\f\24\16\24\u0179\13\24\3\25\3\25\3\25\5"+
		"\25\u017e\n\25\3\25\3\25\3\25\5\25\u0183\n\25\3\25\3\25\3\25\3\26\3\26"+
		"\3\26\3\26\7\26\u018c\n\26\f\26\16\26\u018f\13\26\3\27\3\27\3\27\5\27"+
		"\u0194\n\27\3\27\3\27\3\27\3\30\5\30\u019a\n\30\3\30\3\30\3\30\3\30\3"+
		"\30\7\30\u01a1\n\30\f\30\16\30\u01a4\13\30\3\31\3\31\5\31\u01a8\n\31\3"+
		"\31\3\31\5\31\u01ac\n\31\3\31\3\31\5\31\u01b0\n\31\3\32\3\32\3\32\3\32"+
		"\7\32\u01b6\n\32\f\32\16\32\u01b9\13\32\3\32\3\32\3\32\3\33\3\33\3\33"+
		"\5\33\u01c1\n\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\6\34\u01cb\n"+
		"\34\r\34\16\34\u01cc\3\34\3\34\3\34\5\34\u01d2\n\34\3\34\3\34\3\34\3\35"+
		"\3\35\3\35\3\35\3\35\5\35\u01dc\n\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\5\37\u01f0\n\37"+
		"\3\37\3\37\3\37\3\37\3\37\6\37\u01f7\n\37\r\37\16\37\u01f8\3\37\3\37\5"+
		"\37\u01fd\n\37\3\37\3\37\3\37\5\37\u0202\n\37\3\37\3\37\3\37\5\37\u0207"+
		"\n\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u022c\n\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\7\37\u0245\n\37\f\37\16\37\u0248\13\37"+
		"\3 \3 \3 \3 \3 \3 \3 \3 \5 \u0252\n \3 \3 \3 \3 \3 \3 \3 \7 \u025b\n "+
		"\f \16 \u025e\13 \5 \u0260\n \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \3 \5"+
		" \u026f\n \3!\3!\3!\7!\u0274\n!\f!\16!\u0277\13!\3\"\3\"\5\"\u027b\n\""+
		"\3#\3#\5#\u027f\n#\3#\3#\3$\3$\3$\3$\5$\u0287\n$\3%\3%\3%\3%\3&\5&\u028e"+
		"\n&\3&\3&\3\'\3\'\3\'\3\'\5\'\u0296\n\'\3(\3(\3(\3(\3)\3)\3*\3*\5*\u02a0"+
		"\n*\3+\3+\3+\3+\5+\u02a6\n+\3,\3,\3,\3-\3-\3-\3-\7-\u02af\n-\f-\16-\u02b2"+
		"\13-\3.\3.\5.\u02b6\n.\3/\3/\3/\5/\u02bb\n/\3\60\3\60\3\60\3\60\3\60\7"+
		"\60\u02c2\n\60\f\60\16\60\u02c5\13\60\3\60\3\60\3\60\3\60\3\60\3\61\3"+
		"\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\7\62\u02d6\n\62\f\62\16\62"+
		"\u02d9\13\62\3\63\3\63\3\63\3\63\3\63\3\63\7\63\u02e1\n\63\f\63\16\63"+
		"\u02e4\13\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\7\63\u02ed\n\63\f\63\16"+
		"\63\u02f0\13\63\3\63\5\63\u02f3\n\63\3\64\3\64\3\64\3\64\3\64\3\64\5\64"+
		"\u02fb\n\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\5\65\u0306\n"+
		"\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\5\66\u0313"+
		"\n\66\3\66\3\66\5\66\u0317\n\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66"+
		"\5\66\u0321\n\66\3\66\3\66\5\66\u0325\n\66\3\66\3\66\3\66\3\66\3\66\3"+
		"\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\5\66\u0339"+
		"\n\66\3\66\3\66\5\66\u033d\n\66\3\66\3\66\5\66\u0341\n\66\3\67\3\67\5"+
		"\67\u0345\n\67\38\38\38\39\39\39\39\39\59\u034f\n9\59\u0351\n9\3:\3:\3"+
		":\3:\3:\5:\u0358\n:\3;\3;\3;\3;\3;\3;\3;\3;\3;\5;\u0363\n;\3<\3<\3<\3"+
		"<\5<\u0369\n<\3<\3<\3=\3=\3=\3=\5=\u0371\n=\3=\3=\3=\5=\u0376\n=\5=\u0378"+
		"\n=\5=\u037a\n=\3=\3=\3>\3>\3>\3>\5>\u0382\n>\3>\3>\5>\u0386\n>\5>\u0388"+
		"\n>\3>\3>\3?\3?\3?\3?\3?\3@\3@\3@\3@\3@\3A\3A\3A\3A\5A\u039a\nA\3A\3A"+
		"\3B\3B\3B\3B\3C\3C\3C\3C\3D\3D\3D\3D\3E\3E\5E\u03ac\nE\3F\3F\5F\u03b0"+
		"\nF\3G\3G\3G\5G\u03b5\nG\3H\3H\5H\u03b9\nH\3I\3I\3J\3J\3K\3K\3K\3K\3K"+
		"\3K\3K\3K\3K\3K\7K\u03c9\nK\fK\16K\u03cc\13K\3K\3K\3K\3K\3K\3K\3K\3K\3"+
		"K\3K\3K\7K\u03d9\nK\fK\16K\u03dc\13K\3K\5K\u03df\nK\3L\3L\6L\u03e3\nL"+
		"\rL\16L\u03e4\3L\2\3<M\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,"+
		".\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086"+
		"\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\2\30\4\2CCOO\4\2qq||"+
		"\4\2ll\u0083\u0083\4\2QQ^^\4\2ffqq\4\2WWee\4\2--??\4\2IIyy\3\2\31\32\4"+
		"\2\\\\{{\4\2JJYY\4\2##\u0082\u0082\6\2\30\30@@SSaa\3\2!\"\7\2//\66\66"+
		"=>LMxx\4\2)*np\4\2\n\n\f\f\3\2\6\7\4\2\30\31&&\r\2//88BCFFTT[[ggrrvw}"+
		"}\u0081\u0081\16\2).\60\679ADEGSUZ\\fhqsux|~\u0080\u0082\u0088\t\2\3\5"+
		"\16\16\20\25\30#%(\u0089\u008c\u008e\u008e\u045e\2\u0099\3\2\2\2\4\u009f"+
		"\3\2\2\2\6\u00a8\3\2\2\2\b\u00ab\3\2\2\2\n\u00ae\3\2\2\2\f\u00c0\3\2\2"+
		"\2\16\u00cf\3\2\2\2\20\u00ef\3\2\2\2\22\u00f1\3\2\2\2\24\u00f7\3\2\2\2"+
		"\26\u010e\3\2\2\2\30\u0122\3\2\2\2\32\u012e\3\2\2\2\34\u0148\3\2\2\2\36"+
		"\u014d\3\2\2\2 \u0152\3\2\2\2\"\u015f\3\2\2\2$\u0163\3\2\2\2&\u0171\3"+
		"\2\2\2(\u017a\3\2\2\2*\u0187\3\2\2\2,\u0190\3\2\2\2.\u0199\3\2\2\2\60"+
		"\u01a5\3\2\2\2\62\u01b1\3\2\2\2\64\u01bd\3\2\2\2\66\u01c5\3\2\2\28\u01d6"+
		"\3\2\2\2:\u01e1\3\2\2\2<\u0206\3\2\2\2>\u026e\3\2\2\2@\u0270\3\2\2\2B"+
		"\u027a\3\2\2\2D\u027e\3\2\2\2F\u0286\3\2\2\2H\u0288\3\2\2\2J\u028d\3\2"+
		"\2\2L\u0295\3\2\2\2N\u0297\3\2\2\2P\u029b\3\2\2\2R\u029f\3\2\2\2T\u02a5"+
		"\3\2\2\2V\u02a7\3\2\2\2X\u02b0\3\2\2\2Z\u02b5\3\2\2\2\\\u02ba\3\2\2\2"+
		"^\u02bc\3\2\2\2`\u02cb\3\2\2\2b\u02d7\3\2\2\2d\u02f2\3\2\2\2f\u02fa\3"+
		"\2\2\2h\u0305\3\2\2\2j\u0340\3\2\2\2l\u0342\3\2\2\2n\u0346\3\2\2\2p\u0350"+
		"\3\2\2\2r\u0357\3\2\2\2t\u0362\3\2\2\2v\u0364\3\2\2\2x\u036c\3\2\2\2z"+
		"\u037d\3\2\2\2|\u038b\3\2\2\2~\u0390\3\2\2\2\u0080\u0395\3\2\2\2\u0082"+
		"\u039d\3\2\2\2\u0084\u03a1\3\2\2\2\u0086\u03a5\3\2\2\2\u0088\u03ab\3\2"+
		"\2\2\u008a\u03af\3\2\2\2\u008c\u03b4\3\2\2\2\u008e\u03b8\3\2\2\2\u0090"+
		"\u03ba\3\2\2\2\u0092\u03bc\3\2\2\2\u0094\u03de\3\2\2\2\u0096\u03e2\3\2"+
		"\2\2\u0098\u009a\5\4\3\2\u0099\u0098\3\2\2\2\u0099\u009a\3\2\2\2\u009a"+
		"\u009d\3\2\2\2\u009b\u009e\5\b\5\2\u009c\u009e\5\6\4\2\u009d\u009b\3\2"+
		"\2\2\u009d\u009c\3\2\2\2\u009e\3\3\2\2\2\u009f\u00a0\7\u0088\2\2\u00a0"+
		"\u00a1\7\u0086\2\2\u00a1\u00a4\5\u0094K\2\u00a2\u00a3\7G\2\2\u00a3\u00a5"+
		"\5\u0094K\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a6\3\2\2"+
		"\2\u00a6\u00a7\7 \2\2\u00a7\5\3\2\2\2\u00a8\u00a9\5\f\7\2\u00a9\u00aa"+
		"\5 \21\2\u00aa\7\3\2\2\2\u00ab\u00ac\5\n\6\2\u00ac\u00ad\5\f\7\2\u00ad"+
		"\t\3\2\2\2\u00ae\u00af\7b\2\2\u00af\u00b0\7c\2\2\u00b0\u00b1\5\u008aF"+
		"\2\u00b1\u00b2\7\20\2\2\u00b2\u00b3\5\u0094K\2\u00b3\u00b4\7 \2\2\u00b4"+
		"\13\3\2\2\2\u00b5\u00bb\5\16\b\2\u00b6\u00bb\5\20\t\2\u00b7\u00bb\5\22"+
		"\n\2\u00b8\u00bb\5\24\13\2\u00b9\u00bb\5\26\f\2\u00ba\u00b5\3\2\2\2\u00ba"+
		"\u00b6\3\2\2\2\u00ba\u00b7\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00b9\3\2"+
		"\2\2\u00bb\u00bc\3\2\2\2\u00bc\u00bd\7 \2\2\u00bd\u00bf\3\2\2\2\u00be"+
		"\u00ba\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00c1\3\2"+
		"\2\2\u00c1\u00cc\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c3\u00c7\5\30\r\2\u00c4"+
		"\u00c7\5\32\16\2\u00c5\u00c7\5\34\17\2\u00c6\u00c3\3\2\2\2\u00c6\u00c4"+
		"\3\2\2\2\u00c6\u00c5\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\7 \2\2\u00c9"+
		"\u00cb\3\2\2\2\u00ca\u00c6\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ca\3\2"+
		"\2\2\u00cc\u00cd\3\2\2\2\u00cd\r\3\2\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d0"+
		"\7;\2\2\u00d0\u00d1\7<\2\2\u00d1\u00d2\t\2\2\2\u00d2\u00d3\7c\2\2\u00d3"+
		"\u00d4\5\u0094K\2\u00d4\17\3\2\2\2\u00d5\u00d6\7;\2\2\u00d6\u00d7\7\61"+
		"\2\2\u00d7\u00f0\t\3\2\2\u00d8\u00d9\7;\2\2\u00d9\u00da\7<\2\2\u00da\u00db"+
		"\7\67\2\2\u00db\u00f0\5\u0094K\2\u00dc\u00dd\7;\2\2\u00dd\u00de\7\60\2"+
		"\2\u00de\u00f0\5\u0094K\2\u00df\u00e0\7;\2\2\u00e0\u00e1\79\2\2\u00e1"+
		"\u00f0\t\3\2\2\u00e2\u00e3\7;\2\2\u00e3\u00e4\7m\2\2\u00e4\u00f0\t\4\2"+
		"\2\u00e5\u00e6\7;\2\2\u00e6\u00e7\7<\2\2\u00e7\u00e8\7k\2\2\u00e8\u00e9"+
		"\7E\2\2\u00e9\u00f0\t\5\2\2\u00ea\u00eb\7;\2\2\u00eb\u00ec\7:\2\2\u00ec"+
		"\u00ed\t\6\2\2\u00ed\u00ee\7\33\2\2\u00ee\u00f0\t\7\2\2\u00ef\u00d5\3"+
		"\2\2\2\u00ef\u00d8\3\2\2\2\u00ef\u00dc\3\2\2\2\u00ef\u00df\3\2\2\2\u00ef"+
		"\u00e2\3\2\2\2\u00ef\u00e5\3\2\2\2\u00ef\u00ea\3\2\2\2\u00f0\21\3\2\2"+
		"\2\u00f1\u00f2\7;\2\2\u00f2\u00f3\7c\2\2\u00f3\u00f4\5\u008aF\2\u00f4"+
		"\u00f5\7\20\2\2\u00f5\u00f6\5\u0094K\2\u00f6\23\3\2\2\2\u00f7\u00f8\7"+
		"U\2\2\u00f8\u0100\7u\2\2\u00f9\u00fa\7c\2\2\u00fa\u00fb\5\u008aF\2\u00fb"+
		"\u00fc\7\20\2\2\u00fc\u0101\3\2\2\2\u00fd\u00fe\7<\2\2\u00fe\u00ff\7C"+
		"\2\2\u00ff\u0101\7c\2\2\u0100\u00f9\3\2\2\2\u0100\u00fd\3\2\2\2\u0100"+
		"\u0101\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u010c\5\u0094K\2\u0103\u0104"+
		"\7.\2\2\u0104\u0109\5\u0094K\2\u0105\u0106\7\33\2\2\u0106\u0108\5\u0094"+
		"K\2\u0107\u0105\3\2\2\2\u0108\u010b\3\2\2\2\u0109\u0107\3\2\2\2\u0109"+
		"\u010a\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2\2\2\u010c\u0103\3\2"+
		"\2\2\u010c\u010d\3\2\2\2\u010d\25\3\2\2\2\u010e\u010f\7U\2\2\u010f\u0114"+
		"\7b\2\2\u0110\u0111\7c\2\2\u0111\u0112\5\u008aF\2\u0112\u0113\7\20\2\2"+
		"\u0113\u0115\3\2\2\2\u0114\u0110\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0116"+
		"\3\2\2\2\u0116\u0120\5\u0094K\2\u0117\u0118\7.\2\2\u0118\u011d\5\u0094"+
		"K\2\u0119\u011a\7\33\2\2\u011a\u011c\5\u0094K\2\u011b\u0119\3\2\2\2\u011c"+
		"\u011f\3\2\2\2\u011d\u011b\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u0121\3\2"+
		"\2\2\u011f\u011d\3\2\2\2\u0120\u0117\3\2\2\2\u0120\u0121\3\2\2\2\u0121"+
		"\27\3\2\2\2\u0122\u0123\7;\2\2\u0123\u0124\7\u0085\2\2\u0124\u0125\7("+
		"\2\2\u0125\u0127\5\u0088E\2\u0126\u0128\5n8\2\u0127\u0126\3\2\2\2\u0127"+
		"\u0128\3\2\2\2\u0128\u012c\3\2\2\2\u0129\u012a\7\37\2\2\u012a\u012d\5"+
		"\"\22\2\u012b\u012d\7K\2\2\u012c\u0129\3\2\2\2\u012c\u012b\3\2\2\2\u012d"+
		"\31\3\2\2\2\u012e\u012f\7;\2\2\u012f\u0130\7O\2\2\u0130\u0131\5\u0088"+
		"E\2\u0131\u013a\7\22\2\2\u0132\u0137\5\36\20\2\u0133\u0134\7\33\2\2\u0134"+
		"\u0136\5\36\20\2\u0135\u0133\3\2\2\2\u0136\u0139\3\2\2\2\u0137\u0135\3"+
		"\2\2\2\u0137\u0138\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u013a"+
		"\u0132\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013f\7\23"+
		"\2\2\u013d\u013e\7,\2\2\u013e\u0140\5p9\2\u013f\u013d\3\2\2\2\u013f\u0140"+
		"\3\2\2\2\u0140\u0146\3\2\2\2\u0141\u0142\7\26\2\2\u0142\u0143\5 \21\2"+
		"\u0143\u0144\7\27\2\2\u0144\u0147\3\2\2\2\u0145\u0147\7K\2\2\u0146\u0141"+
		"\3\2\2\2\u0146\u0145\3\2\2\2\u0147\33\3\2\2\2\u0148\u0149\7;\2\2\u0149"+
		"\u014a\7i\2\2\u014a\u014b\5\u0088E\2\u014b\u014c\5\u0094K\2\u014c\35\3"+
		"\2\2\2\u014d\u014e\7(\2\2\u014e\u0150\5\u0088E\2\u014f\u0151\5n8\2\u0150"+
		"\u014f\3\2\2\2\u0150\u0151\3\2\2\2\u0151\37\3\2\2\2\u0152\u0157\5\"\22"+
		"\2\u0153\u0154\7\33\2\2\u0154\u0156\5\"\22\2\u0155\u0153\3\2\2\2\u0156"+
		"\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158!\3\2\2\2"+
		"\u0159\u0157\3\2\2\2\u015a\u0160\5$\23\2\u015b\u0160\5\62\32\2\u015c\u0160"+
		"\5\66\34\2\u015d\u0160\5:\36\2\u015e\u0160\5<\37\2\u015f\u015a\3\2\2\2"+
		"\u015f\u015b\3\2\2\2\u015f\u015c\3\2\2\2\u015f\u015d\3\2\2\2\u015f\u015e"+
		"\3\2\2\2\u0160#\3\2\2\2\u0161\u0164\5&\24\2\u0162\u0164\5*\26\2\u0163"+
		"\u0161\3\2\2\2\u0163\u0162\3\2\2\2\u0164\u0165\3\2\2\2\u0165\u0163\3\2"+
		"\2\2\u0165\u0166\3\2\2\2\u0166\u0169\3\2\2\2\u0167\u0168\7\u0087\2\2\u0168"+
		"\u016a\5\"\22\2\u0169\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016c\3"+
		"\2\2\2\u016b\u016d\5.\30\2\u016c\u016b\3\2\2\2\u016c\u016d\3\2\2\2\u016d"+
		"\u016e\3\2\2\2\u016e\u016f\7s\2\2\u016f\u0170\5\"\22\2\u0170%\3\2\2\2"+
		"\u0171\u0172\7N\2\2\u0172\u0177\5(\25\2\u0173\u0174\7\33\2\2\u0174\u0176"+
		"\5(\25\2\u0175\u0173\3\2\2\2\u0176\u0179\3\2\2\2\u0177\u0175\3\2\2\2\u0177"+
		"\u0178\3\2\2\2\u0178\'\3\2\2\2\u0179\u0177\3\2\2\2\u017a\u017b\7(\2\2"+
		"\u017b\u017d\5\u0088E\2\u017c\u017e\5n8\2\u017d\u017c\3\2\2\2\u017d\u017e"+
		"\3\2\2\2\u017e\u0182\3\2\2\2\u017f\u0180\7.\2\2\u0180\u0181\7(\2\2\u0181"+
		"\u0183\5\u0088E\2\u0182\u017f\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0184"+
		"\3\2\2\2\u0184\u0185\7V\2\2\u0185\u0186\5\"\22\2\u0186)\3\2\2\2\u0187"+
		"\u0188\7_\2\2\u0188\u018d\5,\27\2\u0189\u018a\7\33\2\2\u018a\u018c\5,"+
		"\27\2\u018b\u0189\3\2\2\2\u018c\u018f\3\2\2\2\u018d\u018b\3\2\2\2\u018d"+
		"\u018e\3\2\2\2\u018e+\3\2\2\2\u018f\u018d\3\2\2\2\u0190\u0191\7(\2\2\u0191"+
		"\u0193\5\u0088E\2\u0192\u0194\5n8\2\u0193\u0192\3\2\2\2\u0193\u0194\3"+
		"\2\2\2\u0194\u0195\3\2\2\2\u0195\u0196\7\37\2\2\u0196\u0197\5\"\22\2\u0197"+
		"-\3\2\2\2\u0198\u019a\7z\2\2\u0199\u0198\3\2\2\2\u0199\u019a\3\2\2\2\u019a"+
		"\u019b\3\2\2\2\u019b\u019c\7k\2\2\u019c\u019d\7\62\2\2\u019d\u01a2\5\60"+
		"\31\2\u019e\u019f\7\33\2\2\u019f\u01a1\5\60\31\2\u01a0\u019e\3\2\2\2\u01a1"+
		"\u01a4\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a2\u01a3\3\2\2\2\u01a3/\3\2\2\2"+
		"\u01a4\u01a2\3\2\2\2\u01a5\u01a7\5\"\22\2\u01a6\u01a8\t\b\2\2\u01a7\u01a6"+
		"\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01ab\3\2\2\2\u01a9\u01aa\7E\2\2\u01aa"+
		"\u01ac\t\5\2\2\u01ab\u01a9\3\2\2\2\u01ab\u01ac\3\2\2\2\u01ac\u01af\3\2"+
		"\2\2\u01ad\u01ae\7\67\2\2\u01ae\u01b0\5\u0094K\2\u01af\u01ad\3\2\2\2\u01af"+
		"\u01b0\3\2\2\2\u01b0\61\3\2\2\2\u01b1\u01b2\t\t\2\2\u01b2\u01b7\5\64\33"+
		"\2\u01b3\u01b4\7\33\2\2\u01b4\u01b6\5\64\33\2\u01b5\u01b3\3\2\2\2\u01b6"+
		"\u01b9\3\2\2\2\u01b7\u01b5\3\2\2\2\u01b7\u01b8\3\2\2\2\u01b8\u01ba\3\2"+
		"\2\2\u01b9\u01b7\3\2\2\2\u01ba\u01bb\7t\2\2\u01bb\u01bc\5\"\22\2\u01bc"+
		"\63\3\2\2\2\u01bd\u01be\7(\2\2\u01be\u01c0\5\u0088E\2\u01bf\u01c1\5n8"+
		"\2\u01c0\u01bf\3\2\2\2\u01c0\u01c1\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2\u01c3"+
		"\7V\2\2\u01c3\u01c4\5\"\22\2\u01c4\65\3\2\2\2\u01c5\u01c6\7\u0081\2\2"+
		"\u01c6\u01c7\7\22\2\2\u01c7\u01c8\5 \21\2\u01c8\u01ca\7\23\2\2\u01c9\u01cb"+
		"\58\35\2\u01ca\u01c9\3\2\2\2\u01cb\u01cc\3\2\2\2\u01cc\u01ca\3\2\2\2\u01cc"+
		"\u01cd\3\2\2\2\u01cd\u01ce\3\2\2\2\u01ce\u01d1\7<\2\2\u01cf\u01d0\7(\2"+
		"\2\u01d0\u01d2\5\u0088E\2\u01d1\u01cf\3\2\2\2\u01d1\u01d2\3\2\2\2\u01d2"+
		"\u01d3\3\2\2\2\u01d3\u01d4\7s\2\2\u01d4\u01d5\5\"\22\2\u01d5\67\3\2\2"+
		"\2\u01d6\u01db\7\63\2\2\u01d7\u01d8\7(\2\2\u01d8\u01d9\5\u0088E\2\u01d9"+
		"\u01da\7,\2\2\u01da\u01dc\3\2\2\2\u01db\u01d7\3\2\2\2\u01db\u01dc\3\2"+
		"\2\2\u01dc\u01dd\3\2\2\2\u01dd\u01de\5p9\2\u01de\u01df\7s\2\2\u01df\u01e0"+
		"\5\"\22\2\u01e09\3\2\2\2\u01e1\u01e2\7T\2\2\u01e2\u01e3\7\22\2\2\u01e3"+
		"\u01e4\5 \21\2\u01e4\u01e5\7\23\2\2\u01e5\u01e6\7~\2\2\u01e6\u01e7\5\""+
		"\22\2\u01e7\u01e8\7D\2\2\u01e8\u01e9\5\"\22\2\u01e9;\3\2\2\2\u01ea\u01eb"+
		"\b\37\1\2\u01eb\u01ec\t\n\2\2\u01ec\u0207\5<\37\24\u01ed\u01ef\7\u0084"+
		"\2\2\u01ee\u01f0\t\13\2\2\u01ef\u01ee\3\2\2\2\u01ef\u01f0\3\2\2\2\u01f0"+
		"\u01f1\3\2\2\2\u01f1\u01f2\7\26\2\2\u01f2\u01f3\5 \21\2\u01f3\u01f4\7"+
		"\27\2\2\u01f4\u0207\3\2\2\2\u01f5\u01f7\7\16\2\2\u01f6\u01f5\3\2\2\2\u01f7"+
		"\u01f8\3\2\2\2\u01f8\u01f6\3\2\2\2\u01f8\u01f9\3\2\2\2\u01f9\u01fa\3\2"+
		"\2\2\u01fa\u01fc\7\26\2\2\u01fb\u01fd\5 \21\2\u01fc\u01fb\3\2\2\2\u01fc"+
		"\u01fd\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u0207\7\27\2\2\u01ff\u0201\7"+
		"!\2\2\u0200\u0202\5@!\2\u0201\u0200\3\2\2\2\u0201\u0202\3\2\2\2\u0202"+
		"\u0207\3\2\2\2\u0203\u0204\7\"\2\2\u0204\u0207\5@!\2\u0205\u0207\5@!\2"+
		"\u0206\u01ea\3\2\2\2\u0206\u01ed\3\2\2\2\u0206\u01f6\3\2\2\2\u0206\u01ff"+
		"\3\2\2\2\u0206\u0203\3\2\2\2\u0206\u0205\3\2\2\2\u0207\u0246\3\2\2\2\u0208"+
		"\u0209\f\17\2\2\u0209\u020a\t\f\2\2\u020a\u0245\5<\37\20\u020b\u020c\f"+
		"\16\2\2\u020c\u020d\t\r\2\2\u020d\u0245\5<\37\17\u020e\u020f\f\r\2\2\u020f"+
		"\u0210\t\16\2\2\u0210\u0245\5<\37\16\u0211\u0212\f\f\2\2\u0212\u0213\t"+
		"\n\2\2\u0213\u0245\5<\37\r\u0214\u0215\f\13\2\2\u0215\u0216\7\177\2\2"+
		"\u0216\u0245\5<\37\f\u0217\u022b\f\n\2\2\u0218\u022c\7H\2\2\u0219\u022c"+
		"\7d\2\2\u021a\u022c\7`\2\2\u021b\u022c\7]\2\2\u021c\u022c\7R\2\2\u021d"+
		"\u022c\7P\2\2\u021e\u022c\7\20\2\2\u021f\u022c\7\21\2\2\u0220\u022c\7"+
		"$\2\2\u0221\u0222\7$\2\2\u0222\u022c\7\20\2\2\u0223\u022c\7%\2\2\u0224"+
		"\u0225\7%\2\2\u0225\u022c\7\20\2\2\u0226\u022c\7Z\2\2\u0227\u0228\7$\2"+
		"\2\u0228\u022c\7$\2\2\u0229\u022a\7%\2\2\u022a\u022c\7%\2\2\u022b\u0218"+
		"\3\2\2\2\u022b\u0219\3\2\2\2\u022b\u021a\3\2\2\2\u022b\u021b\3\2\2\2\u022b"+
		"\u021c\3\2\2\2\u022b\u021d\3\2\2\2\u022b\u021e\3\2\2\2\u022b\u021f\3\2"+
		"\2\2\u022b\u0220\3\2\2\2\u022b\u0221\3\2\2\2\u022b\u0223\3\2\2\2\u022b"+
		"\u0224\3\2\2\2\u022b\u0226\3\2\2\2\u022b\u0227\3\2\2\2\u022b\u0229\3\2"+
		"\2\2\u022c\u022d\3\2\2\2\u022d\u0245\5<\37\13\u022e\u022f\f\t\2\2\u022f"+
		"\u0230\7+\2\2\u0230\u0245\5<\37\n\u0231\u0232\f\b\2\2\u0232\u0233\7j\2"+
		"\2\u0233\u0245\5<\37\t\u0234\u0235\f\23\2\2\u0235\u0236\7\64\2\2\u0236"+
		"\u0237\7,\2\2\u0237\u0245\5l\67\2\u0238\u0239\f\22\2\2\u0239\u023a\7\65"+
		"\2\2\u023a\u023b\7,\2\2\u023b\u0245\5l\67\2\u023c\u023d\f\21\2\2\u023d"+
		"\u023e\7\u0080\2\2\u023e\u023f\7,\2\2\u023f\u0245\5p9\2\u0240\u0241\f"+
		"\20\2\2\u0241\u0242\7X\2\2\u0242\u0243\7h\2\2\u0243\u0245\5p9\2\u0244"+
		"\u0208\3\2\2\2\u0244\u020b\3\2\2\2\u0244\u020e\3\2\2\2\u0244\u0211\3\2"+
		"\2\2\u0244\u0214\3\2\2\2\u0244\u0217\3\2\2\2\u0244\u022e\3\2\2\2\u0244"+
		"\u0231\3\2\2\2\u0244\u0234\3\2\2\2\u0244\u0238\3\2\2\2\u0244\u023c\3\2"+
		"\2\2\u0244\u0240\3\2\2\2\u0245\u0248\3\2\2\2\u0246\u0244\3\2\2\2\u0246"+
		"\u0247\3\2\2\2\u0247=\3\2\2\2\u0248\u0246\3\2\2\2\u0249\u026f\7\3\2\2"+
		"\u024a\u026f\7\4\2\2\u024b\u026f\7\5\2\2\u024c\u026f\5\u0094K\2\u024d"+
		"\u024e\7(\2\2\u024e\u026f\5\u0088E\2\u024f\u0251\7\22\2\2\u0250\u0252"+
		"\5 \21\2\u0251\u0250\3\2\2\2\u0251\u0252\3\2\2\2\u0252\u0253\3\2\2\2\u0253"+
		"\u026f\7\23\2\2\u0254\u026f\7\34\2\2\u0255\u0256\5\u008cG\2\u0256\u025f"+
		"\7\22\2\2\u0257\u025c\5\"\22\2\u0258\u0259\7\33\2\2\u0259\u025b\5\"\22"+
		"\2\u025a\u0258\3\2\2\2\u025b\u025e\3\2\2\2\u025c\u025a\3\2\2\2\u025c\u025d"+
		"\3\2\2\2\u025d\u0260\3\2\2\2\u025e\u025c\3\2\2\2\u025f\u0257\3\2\2\2\u025f"+
		"\u0260\3\2\2\2\u0260\u0261\3\2\2\2\u0261\u0262\7\23\2\2\u0262\u026f\3"+
		"\2\2\2\u0263\u0264\7l\2\2\u0264\u0265\7\26\2\2\u0265\u0266\5 \21\2\u0266"+
		"\u0267\7\27\2\2\u0267\u026f\3\2\2\2\u0268\u0269\7\u0083\2\2\u0269\u026a"+
		"\7\26\2\2\u026a\u026b\5 \21\2\u026b\u026c\7\27\2\2\u026c\u026f\3\2\2\2"+
		"\u026d\u026f\5Z.\2\u026e\u0249\3\2\2\2\u026e\u024a\3\2\2\2\u026e\u024b"+
		"\3\2\2\2\u026e\u024c\3\2\2\2\u026e\u024d\3\2\2\2\u026e\u024f\3\2\2\2\u026e"+
		"\u0254\3\2\2\2\u026e\u0255\3\2\2\2\u026e\u0263\3\2\2\2\u026e\u0268\3\2"+
		"\2\2\u026e\u026d\3\2\2\2\u026f?\3\2\2\2\u0270\u0275\5B\"\2\u0271\u0272"+
		"\t\17\2\2\u0272\u0274\5B\"\2\u0273\u0271\3\2\2\2\u0274\u0277\3\2\2\2\u0275"+
		"\u0273\3\2\2\2\u0275\u0276\3\2\2\2\u0276A\3\2\2\2\u0277\u0275\3\2\2\2"+
		"\u0278\u027b\5D#\2\u0279\u027b\5V,\2\u027a\u0278\3\2\2\2\u027a\u0279\3"+
		"\2\2\2\u027bC\3\2\2\2\u027c\u027f\5L\'\2\u027d\u027f\5F$\2\u027e\u027c"+
		"\3\2\2\2\u027e\u027d\3\2\2\2\u027f\u0280\3\2\2\2\u0280\u0281\5X-\2\u0281"+
		"E\3\2\2\2\u0282\u0283\5H%\2\u0283\u0284\5R*\2\u0284\u0287\3\2\2\2\u0285"+
		"\u0287\5J&\2\u0286\u0282\3\2\2\2\u0286\u0285\3\2\2\2\u0287G\3\2\2\2\u0288"+
		"\u0289\t\20\2\2\u0289\u028a\7\36\2\2\u028a\u028b\7\36\2\2\u028bI\3\2\2"+
		"\2\u028c\u028e\7\'\2\2\u028d\u028c\3\2\2\2\u028d\u028e\3\2\2\2\u028e\u028f"+
		"\3\2\2\2\u028f\u0290\5R*\2\u0290K\3\2\2\2\u0291\u0292\5N(\2\u0292\u0293"+
		"\5R*\2\u0293\u0296\3\2\2\2\u0294\u0296\5P)\2\u0295\u0291\3\2\2\2\u0295"+
		"\u0294\3\2\2\2\u0296M\3\2\2\2\u0297\u0298\t\21\2\2\u0298\u0299\7\36\2"+
		"\2\u0299\u029a\7\36\2\2\u029aO\3\2\2\2\u029b\u029c\7\35\2\2\u029cQ\3\2"+
		"\2\2\u029d\u02a0\5T+\2\u029e\u02a0\5t;\2\u029f\u029d\3\2\2\2\u029f\u029e"+
		"\3\2\2\2\u02a0S\3\2\2\2\u02a1\u02a6\5\u0088E\2\u02a2\u02a6\7\30\2\2\u02a3"+
		"\u02a6\7\u008a\2\2\u02a4\u02a6\7\u008b\2\2\u02a5\u02a1\3\2\2\2\u02a5\u02a2"+
		"\3\2\2\2\u02a5\u02a3\3\2\2\2\u02a5\u02a4\3\2\2\2\u02a6U\3\2\2\2\u02a7"+
		"\u02a8\5> \2\u02a8\u02a9\5X-\2\u02a9W\3\2\2\2\u02aa\u02ab\7\24\2\2\u02ab"+
		"\u02ac\5 \21\2\u02ac\u02ad\7\25\2\2\u02ad\u02af\3\2\2\2\u02ae\u02aa\3"+
		"\2\2\2\u02af\u02b2\3\2\2\2\u02b0\u02ae\3\2\2\2\u02b0\u02b1\3\2\2\2\u02b1"+
		"Y\3\2\2\2\u02b2\u02b0\3\2\2\2\u02b3\u02b6\5\\/\2\u02b4\u02b6\5j\66\2\u02b5"+
		"\u02b3\3\2\2\2\u02b5\u02b4\3\2\2\2\u02b6[\3\2\2\2\u02b7\u02bb\5^\60\2"+
		"\u02b8\u02bb\5`\61\2\u02b9\u02bb\t\22\2\2\u02ba\u02b7\3\2\2\2\u02ba\u02b8"+
		"\3\2\2\2\u02ba\u02b9\3\2\2\2\u02bb]\3\2\2\2\u02bc\u02bd\7$\2\2\u02bd\u02be"+
		"\5\u0088E\2\u02be\u02bf\5b\62\2\u02bf\u02c3\7%\2\2\u02c0\u02c2\5f\64\2"+
		"\u02c1\u02c0\3\2\2\2\u02c2\u02c5\3\2\2\2\u02c3\u02c1\3\2\2\2\u02c3\u02c4"+
		"\3\2\2\2\u02c4\u02c6\3\2\2\2\u02c5\u02c3\3\2\2\2\u02c6\u02c7\7$\2\2\u02c7"+
		"\u02c8\7!\2\2\u02c8\u02c9\5\u0088E\2\u02c9\u02ca\7%\2\2\u02ca_\3\2\2\2"+
		"\u02cb\u02cc\7$\2\2\u02cc\u02cd\5\u0088E\2\u02cd\u02ce\5b\62\2\u02ce\u02cf"+
		"\7!\2\2\u02cf\u02d0\7%\2\2\u02d0a\3\2\2\2\u02d1\u02d2\5\u0088E\2\u02d2"+
		"\u02d3\7\20\2\2\u02d3\u02d4\5d\63\2\u02d4\u02d6\3\2\2\2\u02d5\u02d1\3"+
		"\2\2\2\u02d6\u02d9\3\2\2\2\u02d7\u02d5\3\2\2\2\u02d7\u02d8\3\2\2\2\u02d8"+
		"c\3\2\2\2\u02d9\u02d7\3\2\2\2\u02da\u02e2\7\b\2\2\u02db\u02e1\5h\65\2"+
		"\u02dc\u02dd\7\b\2\2\u02dd\u02e1\7\b\2\2\u02de\u02e1\7\t\2\2\u02df\u02e1"+
		"\5\u0096L\2\u02e0\u02db\3\2\2\2\u02e0\u02dc\3\2\2\2\u02e0\u02de\3\2\2"+
		"\2\u02e0\u02df\3\2\2\2\u02e1\u02e4\3\2\2\2\u02e2\u02e0\3\2\2\2\u02e2\u02e3"+
		"\3\2\2\2\u02e3\u02e5\3\2\2\2\u02e4\u02e2\3\2\2\2\u02e5\u02f3\7\b\2\2\u02e6"+
		"\u02ee\7\t\2\2\u02e7\u02ed\5h\65\2\u02e8\u02e9\7\t\2\2\u02e9\u02ed\7\t"+
		"\2\2\u02ea\u02ed\7\b\2\2\u02eb\u02ed\5\u0096L\2\u02ec\u02e7\3\2\2\2\u02ec"+
		"\u02e8\3\2\2\2\u02ec\u02ea\3\2\2\2\u02ec\u02eb\3\2\2\2\u02ed\u02f0\3\2"+
		"\2\2\u02ee\u02ec\3\2\2\2\u02ee\u02ef\3\2\2\2\u02ef\u02f1\3\2\2\2\u02f0"+
		"\u02ee\3\2\2\2\u02f1\u02f3\7\t\2\2\u02f2\u02da\3\2\2\2\u02f2\u02e6\3\2"+
		"\2\2\u02f3e\3\2\2\2\u02f4\u02fb\5\\/\2\u02f5\u02fb\5h\65\2\u02f6\u02fb"+
		"\7\r\2\2\u02f7\u02fb\7\b\2\2\u02f8\u02fb\7\t\2\2\u02f9\u02fb\5\u0096L"+
		"\2\u02fa\u02f4\3\2\2\2\u02fa\u02f5\3\2\2\2\u02fa\u02f6\3\2\2\2\u02fa\u02f7"+
		"\3\2\2\2\u02fa\u02f8\3\2\2\2\u02fa\u02f9\3\2\2\2\u02fbg\3\2\2\2\u02fc"+
		"\u0306\t\23\2\2\u02fd\u02fe\7\26\2\2\u02fe\u0306\7\26\2\2\u02ff\u0300"+
		"\7\27\2\2\u0300\u0306\7\27\2\2\u0301\u0302\7\26\2\2\u0302\u0303\5 \21"+
		"\2\u0303\u0304\7\27\2\2\u0304\u0306\3\2\2\2\u0305\u02fc\3\2\2\2\u0305"+
		"\u02fd\3\2\2\2\u0305\u02ff\3\2\2\2\u0305\u0301\3\2\2\2\u0306i\3\2\2\2"+
		"\u0307\u0308\7A\2\2\u0308\u0309\7\26\2\2\u0309\u030a\5 \21\2\u030a\u030b"+
		"\7\27\2\2\u030b\u0341\3\2\2\2\u030c\u0312\7C\2\2\u030d\u0313\5\u0088E"+
		"\2\u030e\u030f\7\26\2\2\u030f\u0310\5 \21\2\u0310\u0311\7\27\2\2\u0311"+
		"\u0313\3\2\2\2\u0312\u030d\3\2\2\2\u0312\u030e\3\2\2\2\u0313\u0314\3\2"+
		"\2\2\u0314\u0316\7\26\2\2\u0315\u0317\5 \21\2\u0316\u0315\3\2\2\2\u0316"+
		"\u0317\3\2\2\2\u0317\u0318\3\2\2\2\u0318\u0319\7\27\2\2\u0319\u0341\3"+
		"\2\2\2\u031a\u0320\7/\2\2\u031b\u0321\5\u0088E\2\u031c\u031d\7\26\2\2"+
		"\u031d\u031e\5 \21\2\u031e\u031f\7\27\2\2\u031f\u0321\3\2\2\2\u0320\u031b"+
		"\3\2\2\2\u0320\u031c\3\2\2\2\u0321\u0322\3\2\2\2\u0322\u0324\7\26\2\2"+
		"\u0323\u0325\5 \21\2\u0324\u0323\3\2\2\2\u0324\u0325\3\2\2\2\u0325\u0326"+
		"\3\2\2\2\u0326\u0327\7\27\2\2\u0327\u0341\3\2\2\2\u0328\u0329\7}\2\2\u0329"+
		"\u032a\7\26\2\2\u032a\u032b\5 \21\2\u032b\u032c\7\27\2\2\u032c\u0341\3"+
		"\2\2\2\u032d\u032e\78\2\2\u032e\u032f\7\26\2\2\u032f\u0330\5 \21\2\u0330"+
		"\u0331\7\27\2\2\u0331\u0341\3\2\2\2\u0332\u0338\7r\2\2\u0333\u0339\5\u008a"+
		"F\2\u0334\u0335\7\26\2\2\u0335\u0336\5 \21\2\u0336\u0337\7\27\2\2\u0337"+
		"\u0339\3\2\2\2\u0338\u0333\3\2\2\2\u0338\u0334\3\2\2\2\u0339\u033a\3\2"+
		"\2\2\u033a\u033c\7\26\2\2\u033b\u033d\5 \21\2\u033c\u033b\3\2\2\2\u033c"+
		"\u033d\3\2\2\2\u033d\u033e\3\2\2\2\u033e\u033f\7\27\2\2\u033f\u0341\3"+
		"\2\2\2\u0340\u0307\3\2\2\2\u0340\u030c\3\2\2\2\u0340\u031a\3\2\2\2\u0340"+
		"\u0328\3\2\2\2\u0340\u032d\3\2\2\2\u0340\u0332\3\2\2\2\u0341k\3\2\2\2"+
		"\u0342\u0344\5\u0088E\2\u0343\u0345\7&\2\2\u0344\u0343\3\2\2\2\u0344\u0345"+
		"\3\2\2\2\u0345m\3\2\2\2\u0346\u0347\7,\2\2\u0347\u0348\5p9\2\u0348o\3"+
		"\2\2\2\u0349\u034a\7F\2\2\u034a\u034b\7\22\2\2\u034b\u0351\7\23\2\2\u034c"+
		"\u034e\5r:\2\u034d\u034f\t\24\2\2\u034e\u034d\3\2\2\2\u034e\u034f\3\2"+
		"\2\2\u034f\u0351\3\2\2\2\u0350\u0349\3\2\2\2\u0350\u034c\3\2\2\2\u0351"+
		"q\3\2\2\2\u0352\u0358\5t;\2\u0353\u0354\7[\2\2\u0354\u0355\7\22\2\2\u0355"+
		"\u0358\7\23\2\2\u0356\u0358\5\u0088E\2\u0357\u0352\3\2\2\2\u0357\u0353"+
		"\3\2\2\2\u0357\u0356\3\2\2\2\u0358s\3\2\2\2\u0359\u0363\5v<\2\u035a\u0363"+
		"\5x=\2\u035b\u0363\5z>\2\u035c\u0363\5|?\2\u035d\u0363\5~@\2\u035e\u0363"+
		"\5\u0080A\2\u035f\u0363\5\u0082B\2\u0360\u0363\5\u0084C\2\u0361\u0363"+
		"\5\u0086D\2\u0362\u0359\3\2\2\2\u0362\u035a\3\2\2\2\u0362\u035b\3\2\2"+
		"\2\u0362\u035c\3\2\2\2\u0362\u035d\3\2\2\2\u0362\u035e\3\2\2\2\u0362\u035f"+
		"\3\2\2\2\u0362\u0360\3\2\2\2\u0362\u0361\3\2\2\2\u0363u\3\2\2\2\u0364"+
		"\u0365\7B\2\2\u0365\u0368\7\22\2\2\u0366\u0369\5x=\2\u0367\u0369\5|?\2"+
		"\u0368\u0366\3\2\2\2\u0368\u0367\3\2\2\2\u0368\u0369\3\2\2\2\u0369\u036a"+
		"\3\2\2\2\u036a\u036b\7\23\2\2\u036bw\3\2\2\2\u036c\u036d\7C\2\2\u036d"+
		"\u0379\7\22\2\2\u036e\u0371\5\u0088E\2\u036f\u0371\7\30\2\2\u0370\u036e"+
		"\3\2\2\2\u0370\u036f\3\2\2\2\u0371\u0377\3\2\2\2\u0372\u0373\7\33\2\2"+
		"\u0373\u0375\5\u0088E\2\u0374\u0376\7&\2\2\u0375\u0374\3\2\2\2\u0375\u0376"+
		"\3\2\2\2\u0376\u0378\3\2\2\2\u0377\u0372\3\2\2\2\u0377\u0378\3\2\2\2\u0378"+
		"\u037a\3\2\2\2\u0379\u0370\3\2\2\2\u0379\u037a\3\2\2\2\u037a\u037b\3\2"+
		"\2\2\u037b\u037c\7\23\2\2\u037cy\3\2\2\2\u037d\u037e\7/\2\2\u037e\u0387"+
		"\7\22\2\2\u037f\u0382\5\u0088E\2\u0380\u0382\7\30\2\2\u0381\u037f\3\2"+
		"\2\2\u0381\u0380\3\2\2\2\u0382\u0385\3\2\2\2\u0383\u0384\7\33\2\2\u0384"+
		"\u0386\5\u0088E\2\u0385\u0383\3\2\2\2\u0385\u0386\3\2\2\2\u0386\u0388"+
		"\3\2\2\2\u0387\u0381\3\2\2\2\u0387\u0388\3\2\2\2\u0388\u0389\3\2\2\2\u0389"+
		"\u038a\7\23\2\2\u038a{\3\2\2\2\u038b\u038c\7w\2\2\u038c\u038d\7\22\2\2"+
		"\u038d\u038e\5\u0088E\2\u038e\u038f\7\23\2\2\u038f}\3\2\2\2\u0390\u0391"+
		"\7v\2\2\u0391\u0392\7\22\2\2\u0392\u0393\5\u0088E\2\u0393\u0394\7\23\2"+
		"\2\u0394\177\3\2\2\2\u0395\u0396\7r\2\2\u0396\u0399\7\22\2\2\u0397\u039a"+
		"\5\u008aF\2\u0398\u039a\5\u0094K\2\u0399\u0397\3\2\2\2\u0399\u0398\3\2"+
		"\2\2\u0399\u039a\3\2\2\2\u039a\u039b\3\2\2\2\u039b\u039c\7\23\2\2\u039c"+
		"\u0081\3\2\2\2\u039d\u039e\78\2\2\u039e\u039f\7\22\2\2\u039f\u03a0\7\23"+
		"\2\2\u03a0\u0083\3\2\2\2\u03a1\u03a2\7}\2\2\u03a2\u03a3\7\22\2\2\u03a3"+
		"\u03a4\7\23\2\2\u03a4\u0085\3\2\2\2\u03a5\u03a6\7g\2\2\u03a6\u03a7\7\22"+
		"\2\2\u03a7\u03a8\7\23\2\2\u03a8\u0087\3\2\2\2\u03a9\u03ac\7\u0089\2\2"+
		"\u03aa\u03ac\5\u008aF\2\u03ab\u03a9\3\2\2\2\u03ab\u03aa\3\2\2\2\u03ac"+
		"\u0089\3\2\2\2\u03ad\u03b0\7\u008c\2\2\u03ae\u03b0\5\u008eH\2\u03af\u03ad"+
		"\3\2\2\2\u03af\u03ae\3\2\2\2\u03b0\u008b\3\2\2\2\u03b1\u03b5\7\u0089\2"+
		"\2\u03b2\u03b5\7\u008c\2\2\u03b3\u03b5\5\u0092J\2\u03b4\u03b1\3\2\2\2"+
		"\u03b4\u03b2\3\2\2\2\u03b4\u03b3\3\2\2\2\u03b5\u008d\3\2\2\2\u03b6\u03b9"+
		"\5\u0092J\2\u03b7\u03b9\5\u0090I\2\u03b8\u03b6\3\2\2\2\u03b8\u03b7\3\2"+
		"\2\2\u03b9\u008f\3\2\2\2\u03ba\u03bb\t\25\2\2\u03bb\u0091\3\2\2\2\u03bc"+
		"\u03bd\t\26\2\2\u03bd\u0093\3\2\2\2\u03be\u03ca\7\b\2\2\u03bf\u03c0\7"+
		"\b\2\2\u03c0\u03c9\7\b\2\2\u03c1\u03c9\7\6\2\2\u03c2\u03c9\7\7\2\2\u03c3"+
		"\u03c9\7\t\2\2\u03c4\u03c9\7\26\2\2\u03c5\u03c9\7\27\2\2\u03c6\u03c9\7"+
		"$\2\2\u03c7\u03c9\5\u0096L\2\u03c8\u03bf\3\2\2\2\u03c8\u03c1\3\2\2\2\u03c8"+
		"\u03c2\3\2\2\2\u03c8\u03c3\3\2\2\2\u03c8\u03c4\3\2\2\2\u03c8\u03c5\3\2"+
		"\2\2\u03c8\u03c6\3\2\2\2\u03c8\u03c7\3\2\2\2\u03c9\u03cc\3\2\2\2\u03ca"+
		"\u03c8\3\2\2\2\u03ca\u03cb\3\2\2\2\u03cb\u03cd\3\2\2\2\u03cc\u03ca\3\2"+
		"\2\2\u03cd\u03df\7\b\2\2\u03ce\u03da\7\t\2\2\u03cf\u03d0\7\t\2\2\u03d0"+
		"\u03d9\7\t\2\2\u03d1\u03d9\7\6\2\2\u03d2\u03d9\7\7\2\2\u03d3\u03d9\7\b"+
		"\2\2\u03d4\u03d9\7\26\2\2\u03d5\u03d9\7\27\2\2\u03d6\u03d9\7$\2\2\u03d7"+
		"\u03d9\5\u0096L\2\u03d8\u03cf\3\2\2\2\u03d8\u03d1\3\2\2\2\u03d8\u03d2"+
		"\3\2\2\2\u03d8\u03d3\3\2\2\2\u03d8\u03d4\3\2\2\2\u03d8\u03d5\3\2\2\2\u03d8"+
		"\u03d6\3\2\2\2\u03d8\u03d7\3\2\2\2\u03d9\u03dc\3\2\2\2\u03da\u03d8\3\2"+
		"\2\2\u03da\u03db\3\2\2\2\u03db\u03dd\3\2\2\2\u03dc\u03da\3\2\2\2\u03dd"+
		"\u03df\7\t\2\2\u03de\u03be\3\2\2\2\u03de\u03ce\3\2\2\2\u03df\u0095\3\2"+
		"\2\2\u03e0\u03e3\5\u008eH\2\u03e1\u03e3\t\27\2\2\u03e2\u03e0\3\2\2\2\u03e2"+
		"\u03e1\3\2\2\2\u03e3\u03e4\3\2\2\2\u03e4\u03e2\3\2\2\2\u03e4\u03e5\3\2"+
		"\2\2\u03e5\u0097\3\2\2\2l\u0099\u009d\u00a4\u00ba\u00c0\u00c6\u00cc\u00ef"+
		"\u0100\u0109\u010c\u0114\u011d\u0120\u0127\u012c\u0137\u013a\u013f\u0146"+
		"\u0150\u0157\u015f\u0163\u0165\u0169\u016c\u0177\u017d\u0182\u018d\u0193"+
		"\u0199\u01a2\u01a7\u01ab\u01af\u01b7\u01c0\u01cc\u01d1\u01db\u01ef\u01f8"+
		"\u01fc\u0201\u0206\u022b\u0244\u0246\u0251\u025c\u025f\u026e\u0275\u027a"+
		"\u027e\u0286\u028d\u0295\u029f\u02a5\u02b0\u02b5\u02ba\u02c3\u02d7\u02e0"+
		"\u02e2\u02ec\u02ee\u02f2\u02fa\u0305\u0312\u0316\u0320\u0324\u0338\u033c"+
		"\u0340\u0344\u034e\u0350\u0357\u0362\u0368\u0370\u0375\u0377\u0379\u0381"+
		"\u0385\u0387\u0399\u03ab\u03af\u03b4\u03b8\u03c8\u03ca\u03d8\u03da\u03de"+
		"\u03e2\u03e4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}