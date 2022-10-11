// Generated from /home/danson/src/jedit/plugins/Beauty/src/beauty/parsers/java/antlr/Java8.g4 by ANTLR 4.10.1

package beauty.parsers.java.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Java8Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, ABSTRACT=3, ASSERT=4, BOOLEAN=5, BREAK=6, BYTE=7, CASE=8, 
		CATCH=9, CHAR=10, CLASS=11, CONST=12, CONTINUE=13, DEFAULT=14, DO=15, 
		DOUBLE=16, ELSE=17, ENUM=18, EXTENDS=19, FINAL=20, FINALLY=21, FLOAT=22, 
		FOR=23, IF=24, GOTO=25, IMPLEMENTS=26, IMPORT=27, INSTANCEOF=28, INT=29, 
		INTERFACE=30, LONG=31, NATIVE=32, NEW=33, PACKAGE=34, PRIVATE=35, PROTECTED=36, 
		PUBLIC=37, RETURN=38, SHORT=39, STATIC=40, STRICTFP=41, SUPER=42, SWITCH=43, 
		SYNCHRONIZED=44, THIS=45, THROW=46, THROWS=47, TRANSIENT=48, TRY=49, VOID=50, 
		VOLATILE=51, WHILE=52, IntegerLiteral=53, FloatingPointLiteral=54, BooleanLiteral=55, 
		CharacterLiteral=56, StringLiteral=57, NullLiteral=58, LPAREN=59, RPAREN=60, 
		LBRACE=61, RBRACE=62, LBRACK=63, RBRACK=64, SEMI=65, COMMA=66, DOT=67, 
		ASSIGN=68, GT=69, LT=70, BANG=71, TILDE=72, QUESTION=73, COLON=74, EQUAL=75, 
		LE=76, GE=77, NOTEQUAL=78, AND=79, OR=80, INC=81, DEC=82, ADD=83, SUB=84, 
		MUL=85, DIV=86, BITAND=87, BITOR=88, CARET=89, MOD=90, ARROW=91, COLONCOLON=92, 
		ADD_ASSIGN=93, SUB_ASSIGN=94, MUL_ASSIGN=95, DIV_ASSIGN=96, AND_ASSIGN=97, 
		OR_ASSIGN=98, XOR_ASSIGN=99, MOD_ASSIGN=100, LSHIFT_ASSIGN=101, RSHIFT_ASSIGN=102, 
		URSHIFT_ASSIGN=103, Identifier=104, AT=105, ELLIPSIS=106, WS=107, DOC_COMMENT=108, 
		COMMENT=109, JEDIT_FOLD_MARKER=110, LINE_COMMENT=111;
	public static final int
		RULE_literal = 0, RULE_type = 1, RULE_primitiveType = 2, RULE_numericType = 3, 
		RULE_integralType = 4, RULE_floatingPointType = 5, RULE_referenceType = 6, 
		RULE_classOrInterfaceType = 7, RULE_classType = 8, RULE_classType_lf_classOrInterfaceType = 9, 
		RULE_classType_lfno_classOrInterfaceType = 10, RULE_interfaceType = 11, 
		RULE_interfaceType_lf_classOrInterfaceType = 12, RULE_interfaceType_lfno_classOrInterfaceType = 13, 
		RULE_typeVariable = 14, RULE_arrayType = 15, RULE_dims = 16, RULE_typeParameter = 17, 
		RULE_typeParameterModifier = 18, RULE_typeBound = 19, RULE_additionalBound = 20, 
		RULE_typeArguments = 21, RULE_typeArgumentList = 22, RULE_typeArgument = 23, 
		RULE_wildcard = 24, RULE_wildcardBounds = 25, RULE_packageName = 26, RULE_typeName = 27, 
		RULE_packageOrTypeName = 28, RULE_expressionName = 29, RULE_methodName = 30, 
		RULE_ambiguousName = 31, RULE_compilationUnit = 32, RULE_packageDeclaration = 33, 
		RULE_packageModifier = 34, RULE_importDeclaration = 35, RULE_singleTypeImportDeclaration = 36, 
		RULE_typeImportOnDemandDeclaration = 37, RULE_singleStaticImportDeclaration = 38, 
		RULE_staticImportOnDemandDeclaration = 39, RULE_typeDeclaration = 40, 
		RULE_classDeclaration = 41, RULE_normalClassDeclaration = 42, RULE_classModifiers = 43, 
		RULE_classModifier = 44, RULE_typeParameters = 45, RULE_typeParameterList = 46, 
		RULE_superclass = 47, RULE_superinterfaces = 48, RULE_interfaceTypeList = 49, 
		RULE_classBody = 50, RULE_classBodyDeclaration = 51, RULE_classMemberDeclaration = 52, 
		RULE_fieldDeclaration = 53, RULE_fieldModifiers = 54, RULE_fieldModifier = 55, 
		RULE_variableDeclaratorList = 56, RULE_variableDeclarator = 57, RULE_variableDeclaratorId = 58, 
		RULE_variableInitializer = 59, RULE_unannType = 60, RULE_unannPrimitiveType = 61, 
		RULE_unannReferenceType = 62, RULE_unannClassOrInterfaceType = 63, RULE_unannClassType = 64, 
		RULE_unannClassType_lf_unannClassOrInterfaceType = 65, RULE_unannClassType_lfno_unannClassOrInterfaceType = 66, 
		RULE_unannInterfaceType = 67, RULE_unannInterfaceType_lf_unannClassOrInterfaceType = 68, 
		RULE_unannInterfaceType_lfno_unannClassOrInterfaceType = 69, RULE_unannTypeVariable = 70, 
		RULE_unannArrayType = 71, RULE_methodDeclaration = 72, RULE_methodModifiers = 73, 
		RULE_methodModifier = 74, RULE_methodHeader = 75, RULE_result = 76, RULE_methodDeclarator = 77, 
		RULE_formalParameterList = 78, RULE_formalParameters = 79, RULE_formalParameter = 80, 
		RULE_variableModifier = 81, RULE_lastFormalParameter = 82, RULE_receiverParameter = 83, 
		RULE_throws_ = 84, RULE_exceptionTypeList = 85, RULE_exceptionType = 86, 
		RULE_methodBody = 87, RULE_instanceInitializer = 88, RULE_staticInitializer = 89, 
		RULE_constructorDeclaration = 90, RULE_constructorModifiers = 91, RULE_constructorModifier = 92, 
		RULE_constructorDeclarator = 93, RULE_simpleTypeName = 94, RULE_constructorBody = 95, 
		RULE_explicitConstructorInvocation = 96, RULE_enumDeclaration = 97, RULE_enumBody = 98, 
		RULE_enumConstantList = 99, RULE_enumConstant = 100, RULE_enumConstantModifier = 101, 
		RULE_enumBodyDeclarations = 102, RULE_interfaceDeclaration = 103, RULE_normalInterfaceDeclaration = 104, 
		RULE_interfaceModifiers = 105, RULE_interfaceModifier = 106, RULE_extendsInterfaces = 107, 
		RULE_interfaceBody = 108, RULE_interfaceMemberDeclaration = 109, RULE_constantDeclaration = 110, 
		RULE_constantModifier = 111, RULE_interfaceMethodDeclaration = 112, RULE_interfaceMethodModifier = 113, 
		RULE_annotationTypeDeclaration = 114, RULE_annotationTypeBody = 115, RULE_annotationTypeMemberDeclaration = 116, 
		RULE_annotationTypeElementDeclaration = 117, RULE_annotationTypeElementModifier = 118, 
		RULE_defaultValue = 119, RULE_annotation = 120, RULE_annotationIdentifier = 121, 
		RULE_annotationDim = 122, RULE_normalAnnotation = 123, RULE_elementValuePairList = 124, 
		RULE_elementValuePair = 125, RULE_elementValue = 126, RULE_elementValueArrayInitializer = 127, 
		RULE_elementValueList = 128, RULE_markerAnnotation = 129, RULE_singleElementAnnotation = 130, 
		RULE_arrayInitializer = 131, RULE_variableInitializerList = 132, RULE_block = 133, 
		RULE_blockStatements = 134, RULE_blockStatement = 135, RULE_localVariableDeclarationStatement = 136, 
		RULE_localVariableDeclaration = 137, RULE_statement = 138, RULE_statementNoShortIf = 139, 
		RULE_statementWithoutTrailingSubstatement = 140, RULE_emptyStatement = 141, 
		RULE_labeledStatement = 142, RULE_labeledStatementNoShortIf = 143, RULE_expressionStatement = 144, 
		RULE_statementExpression = 145, RULE_ifThenStatement = 146, RULE_ifThenElseStatement = 147, 
		RULE_ifThenElseStatementNoShortIf = 148, RULE_assertStatement = 149, RULE_switchStatement = 150, 
		RULE_switchBlock = 151, RULE_switchBlockStatementGroup = 152, RULE_switchLabels = 153, 
		RULE_switchLabel = 154, RULE_enumConstantName = 155, RULE_whileStatement = 156, 
		RULE_whileStatementNoShortIf = 157, RULE_doStatement = 158, RULE_forStatement = 159, 
		RULE_forStatementNoShortIf = 160, RULE_basicForStatement = 161, RULE_basicForStatementNoShortIf = 162, 
		RULE_forInit = 163, RULE_forUpdate = 164, RULE_statementExpressionList = 165, 
		RULE_enhancedForStatement = 166, RULE_enhancedForStatementNoShortIf = 167, 
		RULE_breakStatement = 168, RULE_continueStatement = 169, RULE_returnStatement = 170, 
		RULE_throwStatement = 171, RULE_synchronizedStatement = 172, RULE_tryStatement = 173, 
		RULE_catches = 174, RULE_catchClause = 175, RULE_catchFormalParameter = 176, 
		RULE_catchType = 177, RULE_finally_ = 178, RULE_tryWithResourcesStatement = 179, 
		RULE_resourceSpecification = 180, RULE_resourceList = 181, RULE_resource = 182, 
		RULE_primary = 183, RULE_primaryNoNewArray = 184, RULE_primaryNoNewArray_lf_arrayAccess = 185, 
		RULE_primaryNoNewArray_lfno_arrayAccess = 186, RULE_primaryNoNewArray_lf_primary = 187, 
		RULE_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary = 188, RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary = 189, 
		RULE_primaryNoNewArray_lfno_primary = 190, RULE_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary = 191, 
		RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary = 192, 
		RULE_classInstanceCreationExpression = 193, RULE_classInstanceCreationExpression_lf_primary = 194, 
		RULE_classInstanceCreationExpression_lfno_primary = 195, RULE_typeArgumentsOrDiamond = 196, 
		RULE_fieldAccess = 197, RULE_fieldAccess_lf_primary = 198, RULE_fieldAccess_lfno_primary = 199, 
		RULE_arrayAccess = 200, RULE_arrayAccess_lf_primary = 201, RULE_arrayAccess_lfno_primary = 202, 
		RULE_methodInvocation = 203, RULE_methodInvocation_lf_primary = 204, RULE_methodInvocation_lfno_primary = 205, 
		RULE_argumentList = 206, RULE_methodReference = 207, RULE_methodReference_lf_primary = 208, 
		RULE_methodReference_lfno_primary = 209, RULE_arrayCreationExpression = 210, 
		RULE_dimExprs = 211, RULE_dimExpr = 212, RULE_constantExpression = 213, 
		RULE_expression = 214, RULE_lambdaExpression = 215, RULE_lambdaParameters = 216, 
		RULE_inferredFormalParameterList = 217, RULE_lambdaBody = 218, RULE_assignmentExpression = 219, 
		RULE_assignment = 220, RULE_leftHandSide = 221, RULE_assignmentOperator = 222, 
		RULE_additiveOperator = 223, RULE_relationalOperator = 224, RULE_multiplicativeOperator = 225, 
		RULE_squareBrackets = 226, RULE_conditionalExpression = 227, RULE_conditionalOrExpression = 228, 
		RULE_conditionalAndExpression = 229, RULE_inclusiveOrExpression = 230, 
		RULE_exclusiveOrExpression = 231, RULE_andExpression = 232, RULE_equalityExpression = 233, 
		RULE_relationalExpression = 234, RULE_shiftExpression = 235, RULE_shiftOperator = 236, 
		RULE_additiveExpression = 237, RULE_multiplicativeExpression = 238, RULE_unaryExpression = 239, 
		RULE_preIncrementExpression = 240, RULE_preDecrementExpression = 241, 
		RULE_unaryExpressionNotPlusMinus = 242, RULE_postfixExpression = 243, 
		RULE_postIncrementExpression = 244, RULE_postIncrementExpression_lf_postfixExpression = 245, 
		RULE_postDecrementExpression = 246, RULE_postDecrementExpression_lf_postfixExpression = 247, 
		RULE_castExpression = 248;
	private static String[] makeRuleNames() {
		return new String[] {
			"literal", "type", "primitiveType", "numericType", "integralType", "floatingPointType", 
			"referenceType", "classOrInterfaceType", "classType", "classType_lf_classOrInterfaceType", 
			"classType_lfno_classOrInterfaceType", "interfaceType", "interfaceType_lf_classOrInterfaceType", 
			"interfaceType_lfno_classOrInterfaceType", "typeVariable", "arrayType", 
			"dims", "typeParameter", "typeParameterModifier", "typeBound", "additionalBound", 
			"typeArguments", "typeArgumentList", "typeArgument", "wildcard", "wildcardBounds", 
			"packageName", "typeName", "packageOrTypeName", "expressionName", "methodName", 
			"ambiguousName", "compilationUnit", "packageDeclaration", "packageModifier", 
			"importDeclaration", "singleTypeImportDeclaration", "typeImportOnDemandDeclaration", 
			"singleStaticImportDeclaration", "staticImportOnDemandDeclaration", "typeDeclaration", 
			"classDeclaration", "normalClassDeclaration", "classModifiers", "classModifier", 
			"typeParameters", "typeParameterList", "superclass", "superinterfaces", 
			"interfaceTypeList", "classBody", "classBodyDeclaration", "classMemberDeclaration", 
			"fieldDeclaration", "fieldModifiers", "fieldModifier", "variableDeclaratorList", 
			"variableDeclarator", "variableDeclaratorId", "variableInitializer", 
			"unannType", "unannPrimitiveType", "unannReferenceType", "unannClassOrInterfaceType", 
			"unannClassType", "unannClassType_lf_unannClassOrInterfaceType", "unannClassType_lfno_unannClassOrInterfaceType", 
			"unannInterfaceType", "unannInterfaceType_lf_unannClassOrInterfaceType", 
			"unannInterfaceType_lfno_unannClassOrInterfaceType", "unannTypeVariable", 
			"unannArrayType", "methodDeclaration", "methodModifiers", "methodModifier", 
			"methodHeader", "result", "methodDeclarator", "formalParameterList", 
			"formalParameters", "formalParameter", "variableModifier", "lastFormalParameter", 
			"receiverParameter", "throws_", "exceptionTypeList", "exceptionType", 
			"methodBody", "instanceInitializer", "staticInitializer", "constructorDeclaration", 
			"constructorModifiers", "constructorModifier", "constructorDeclarator", 
			"simpleTypeName", "constructorBody", "explicitConstructorInvocation", 
			"enumDeclaration", "enumBody", "enumConstantList", "enumConstant", "enumConstantModifier", 
			"enumBodyDeclarations", "interfaceDeclaration", "normalInterfaceDeclaration", 
			"interfaceModifiers", "interfaceModifier", "extendsInterfaces", "interfaceBody", 
			"interfaceMemberDeclaration", "constantDeclaration", "constantModifier", 
			"interfaceMethodDeclaration", "interfaceMethodModifier", "annotationTypeDeclaration", 
			"annotationTypeBody", "annotationTypeMemberDeclaration", "annotationTypeElementDeclaration", 
			"annotationTypeElementModifier", "defaultValue", "annotation", "annotationIdentifier", 
			"annotationDim", "normalAnnotation", "elementValuePairList", "elementValuePair", 
			"elementValue", "elementValueArrayInitializer", "elementValueList", "markerAnnotation", 
			"singleElementAnnotation", "arrayInitializer", "variableInitializerList", 
			"block", "blockStatements", "blockStatement", "localVariableDeclarationStatement", 
			"localVariableDeclaration", "statement", "statementNoShortIf", "statementWithoutTrailingSubstatement", 
			"emptyStatement", "labeledStatement", "labeledStatementNoShortIf", "expressionStatement", 
			"statementExpression", "ifThenStatement", "ifThenElseStatement", "ifThenElseStatementNoShortIf", 
			"assertStatement", "switchStatement", "switchBlock", "switchBlockStatementGroup", 
			"switchLabels", "switchLabel", "enumConstantName", "whileStatement", 
			"whileStatementNoShortIf", "doStatement", "forStatement", "forStatementNoShortIf", 
			"basicForStatement", "basicForStatementNoShortIf", "forInit", "forUpdate", 
			"statementExpressionList", "enhancedForStatement", "enhancedForStatementNoShortIf", 
			"breakStatement", "continueStatement", "returnStatement", "throwStatement", 
			"synchronizedStatement", "tryStatement", "catches", "catchClause", "catchFormalParameter", 
			"catchType", "finally_", "tryWithResourcesStatement", "resourceSpecification", 
			"resourceList", "resource", "primary", "primaryNoNewArray", "primaryNoNewArray_lf_arrayAccess", 
			"primaryNoNewArray_lfno_arrayAccess", "primaryNoNewArray_lf_primary", 
			"primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary", "primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary", 
			"primaryNoNewArray_lfno_primary", "primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary", 
			"primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary", "classInstanceCreationExpression", 
			"classInstanceCreationExpression_lf_primary", "classInstanceCreationExpression_lfno_primary", 
			"typeArgumentsOrDiamond", "fieldAccess", "fieldAccess_lf_primary", "fieldAccess_lfno_primary", 
			"arrayAccess", "arrayAccess_lf_primary", "arrayAccess_lfno_primary", 
			"methodInvocation", "methodInvocation_lf_primary", "methodInvocation_lfno_primary", 
			"argumentList", "methodReference", "methodReference_lf_primary", "methodReference_lfno_primary", 
			"arrayCreationExpression", "dimExprs", "dimExpr", "constantExpression", 
			"expression", "lambdaExpression", "lambdaParameters", "inferredFormalParameterList", 
			"lambdaBody", "assignmentExpression", "assignment", "leftHandSide", "assignmentOperator", 
			"additiveOperator", "relationalOperator", "multiplicativeOperator", "squareBrackets", 
			"conditionalExpression", "conditionalOrExpression", "conditionalAndExpression", 
			"inclusiveOrExpression", "exclusiveOrExpression", "andExpression", "equalityExpression", 
			"relationalExpression", "shiftExpression", "shiftOperator", "additiveExpression", 
			"multiplicativeExpression", "unaryExpression", "preIncrementExpression", 
			"preDecrementExpression", "unaryExpressionNotPlusMinus", "postfixExpression", 
			"postIncrementExpression", "postIncrementExpression_lf_postfixExpression", 
			"postDecrementExpression", "postDecrementExpression_lf_postfixExpression", 
			"castExpression"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<>'", "'[]'", "'abstract'", "'assert'", "'boolean'", "'break'", 
			"'byte'", "'case'", "'catch'", "'char'", "'class'", "'const'", "'continue'", 
			"'default'", "'do'", "'double'", "'else'", "'enum'", "'extends'", "'final'", 
			"'finally'", "'float'", "'for'", "'if'", "'goto'", "'implements'", "'import'", 
			"'instanceof'", "'int'", "'interface'", "'long'", "'native'", "'new'", 
			"'package'", "'private'", "'protected'", "'public'", "'return'", "'short'", 
			"'static'", "'strictfp'", "'super'", "'switch'", "'synchronized'", "'this'", 
			"'throw'", "'throws'", "'transient'", "'try'", "'void'", "'volatile'", 
			"'while'", null, null, null, null, null, "'null'", "'('", "')'", "'{'", 
			"'}'", "'['", "']'", "';'", "','", "'.'", "'='", "'>'", "'<'", "'!'", 
			"'~'", "'?'", "':'", "'=='", "'<='", "'>='", "'!='", "'&&'", "'||'", 
			"'++'", "'--'", "'+'", "'-'", "'*'", "'/'", "'&'", "'|'", "'^'", "'%'", 
			"'->'", "'::'", "'+='", "'-='", "'*='", "'/='", "'&='", "'|='", "'^='", 
			"'%='", "'<<='", "'>>='", "'>>>='", null, "'@'", "'...'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", 
			"CATCH", "CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", 
			"ELSE", "ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "IF", 
			"GOTO", "IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", 
			"NATIVE", "NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", 
			"SHORT", "STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", 
			"THROW", "THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "IntegerLiteral", 
			"FloatingPointLiteral", "BooleanLiteral", "CharacterLiteral", "StringLiteral", 
			"NullLiteral", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACK", "RBRACK", 
			"SEMI", "COMMA", "DOT", "ASSIGN", "GT", "LT", "BANG", "TILDE", "QUESTION", 
			"COLON", "EQUAL", "LE", "GE", "NOTEQUAL", "AND", "OR", "INC", "DEC", 
			"ADD", "SUB", "MUL", "DIV", "BITAND", "BITOR", "CARET", "MOD", "ARROW", 
			"COLONCOLON", "ADD_ASSIGN", "SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", 
			"AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "LSHIFT_ASSIGN", 
			"RSHIFT_ASSIGN", "URSHIFT_ASSIGN", "Identifier", "AT", "ELLIPSIS", "WS", 
			"DOC_COMMENT", "COMMENT", "JEDIT_FOLD_MARKER", "LINE_COMMENT"
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

	@Override
	public String getGrammarFileName() { return "Java8.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public Java8Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode IntegerLiteral() { return getToken(Java8Parser.IntegerLiteral, 0); }
		public TerminalNode FloatingPointLiteral() { return getToken(Java8Parser.FloatingPointLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(Java8Parser.BooleanLiteral, 0); }
		public TerminalNode CharacterLiteral() { return getToken(Java8Parser.CharacterLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(Java8Parser.StringLiteral, 0); }
		public TerminalNode NullLiteral() { return getToken(Java8Parser.NullLiteral, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(498);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class TypeContext extends ParserRuleContext {
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		try {
			setState(502);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(500);
				primitiveType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(501);
				referenceType();
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

	public static class PrimitiveTypeContext extends ParserRuleContext {
		public NumericTypeContext numericType() {
			return getRuleContext(NumericTypeContext.class,0);
		}
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public TerminalNode BOOLEAN() { return getToken(Java8Parser.BOOLEAN, 0); }
		public PrimitiveTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimitiveType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimitiveType(this);
		}
	}

	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_primitiveType);
		int _la;
		try {
			setState(518);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(507);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(504);
					annotation();
					}
					}
					setState(509);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(510);
				numericType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(514);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(511);
					annotation();
					}
					}
					setState(516);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(517);
				match(BOOLEAN);
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

	public static class NumericTypeContext extends ParserRuleContext {
		public IntegralTypeContext integralType() {
			return getRuleContext(IntegralTypeContext.class,0);
		}
		public FloatingPointTypeContext floatingPointType() {
			return getRuleContext(FloatingPointTypeContext.class,0);
		}
		public NumericTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterNumericType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitNumericType(this);
		}
	}

	public final NumericTypeContext numericType() throws RecognitionException {
		NumericTypeContext _localctx = new NumericTypeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_numericType);
		try {
			setState(522);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BYTE:
			case CHAR:
			case INT:
			case LONG:
			case SHORT:
				enterOuterAlt(_localctx, 1);
				{
				setState(520);
				integralType();
				}
				break;
			case DOUBLE:
			case FLOAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(521);
				floatingPointType();
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

	public static class IntegralTypeContext extends ParserRuleContext {
		public TerminalNode BYTE() { return getToken(Java8Parser.BYTE, 0); }
		public TerminalNode SHORT() { return getToken(Java8Parser.SHORT, 0); }
		public TerminalNode INT() { return getToken(Java8Parser.INT, 0); }
		public TerminalNode LONG() { return getToken(Java8Parser.LONG, 0); }
		public TerminalNode CHAR() { return getToken(Java8Parser.CHAR, 0); }
		public IntegralTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integralType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterIntegralType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitIntegralType(this);
		}
	}

	public final IntegralTypeContext integralType() throws RecognitionException {
		IntegralTypeContext _localctx = new IntegralTypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_integralType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(524);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BYTE) | (1L << CHAR) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class FloatingPointTypeContext extends ParserRuleContext {
		public TerminalNode FLOAT() { return getToken(Java8Parser.FLOAT, 0); }
		public TerminalNode DOUBLE() { return getToken(Java8Parser.DOUBLE, 0); }
		public FloatingPointTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_floatingPointType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFloatingPointType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFloatingPointType(this);
		}
	}

	public final FloatingPointTypeContext floatingPointType() throws RecognitionException {
		FloatingPointTypeContext _localctx = new FloatingPointTypeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_floatingPointType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(526);
			_la = _input.LA(1);
			if ( !(_la==DOUBLE || _la==FLOAT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class ReferenceTypeContext extends ParserRuleContext {
		public ClassOrInterfaceTypeContext classOrInterfaceType() {
			return getRuleContext(ClassOrInterfaceTypeContext.class,0);
		}
		public TypeVariableContext typeVariable() {
			return getRuleContext(TypeVariableContext.class,0);
		}
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public ReferenceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_referenceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterReferenceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitReferenceType(this);
		}
	}

	public final ReferenceTypeContext referenceType() throws RecognitionException {
		ReferenceTypeContext _localctx = new ReferenceTypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_referenceType);
		try {
			setState(531);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(528);
				classOrInterfaceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(529);
				typeVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(530);
				arrayType();
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

	public static class ClassOrInterfaceTypeContext extends ParserRuleContext {
		public ClassType_lfno_classOrInterfaceTypeContext classType_lfno_classOrInterfaceType() {
			return getRuleContext(ClassType_lfno_classOrInterfaceTypeContext.class,0);
		}
		public InterfaceType_lfno_classOrInterfaceTypeContext interfaceType_lfno_classOrInterfaceType() {
			return getRuleContext(InterfaceType_lfno_classOrInterfaceTypeContext.class,0);
		}
		public List<ClassType_lf_classOrInterfaceTypeContext> classType_lf_classOrInterfaceType() {
			return getRuleContexts(ClassType_lf_classOrInterfaceTypeContext.class);
		}
		public ClassType_lf_classOrInterfaceTypeContext classType_lf_classOrInterfaceType(int i) {
			return getRuleContext(ClassType_lf_classOrInterfaceTypeContext.class,i);
		}
		public List<InterfaceType_lf_classOrInterfaceTypeContext> interfaceType_lf_classOrInterfaceType() {
			return getRuleContexts(InterfaceType_lf_classOrInterfaceTypeContext.class);
		}
		public InterfaceType_lf_classOrInterfaceTypeContext interfaceType_lf_classOrInterfaceType(int i) {
			return getRuleContext(InterfaceType_lf_classOrInterfaceTypeContext.class,i);
		}
		public ClassOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassOrInterfaceType(this);
		}
	}

	public final ClassOrInterfaceTypeContext classOrInterfaceType() throws RecognitionException {
		ClassOrInterfaceTypeContext _localctx = new ClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_classOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(535);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(533);
				classType_lfno_classOrInterfaceType();
				}
				break;
			case 2:
				{
				setState(534);
				interfaceType_lfno_classOrInterfaceType();
				}
				break;
			}
			setState(541);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(539);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						setState(537);
						classType_lf_classOrInterfaceType();
						}
						break;
					case 2:
						{
						setState(538);
						interfaceType_lf_classOrInterfaceType();
						}
						break;
					}
					} 
				}
				setState(543);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
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

	public static class ClassTypeContext extends ParserRuleContext {
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ClassOrInterfaceTypeContext classOrInterfaceType() {
			return getRuleContext(ClassOrInterfaceTypeContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public ClassTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassType(this);
		}
	}

	public final ClassTypeContext classType() throws RecognitionException {
		ClassTypeContext _localctx = new ClassTypeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_classType);
		int _la;
		try {
			setState(554);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(544);
				annotationIdentifier();
				setState(546);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(545);
					typeArguments();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(548);
				classOrInterfaceType();
				setState(549);
				match(DOT);
				setState(550);
				annotationIdentifier();
				setState(552);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(551);
					typeArguments();
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

	public static class ClassType_lf_classOrInterfaceTypeContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ClassType_lf_classOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classType_lf_classOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassType_lf_classOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassType_lf_classOrInterfaceType(this);
		}
	}

	public final ClassType_lf_classOrInterfaceTypeContext classType_lf_classOrInterfaceType() throws RecognitionException {
		ClassType_lf_classOrInterfaceTypeContext _localctx = new ClassType_lf_classOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_classType_lf_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(556);
			match(DOT);
			setState(557);
			annotationIdentifier();
			setState(559);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(558);
				typeArguments();
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

	public static class ClassType_lfno_classOrInterfaceTypeContext extends ParserRuleContext {
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ClassType_lfno_classOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classType_lfno_classOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassType_lfno_classOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassType_lfno_classOrInterfaceType(this);
		}
	}

	public final ClassType_lfno_classOrInterfaceTypeContext classType_lfno_classOrInterfaceType() throws RecognitionException {
		ClassType_lfno_classOrInterfaceTypeContext _localctx = new ClassType_lfno_classOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_classType_lfno_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			annotationIdentifier();
			setState(563);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(562);
				typeArguments();
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

	public static class InterfaceTypeContext extends ParserRuleContext {
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public InterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceType(this);
		}
	}

	public final InterfaceTypeContext interfaceType() throws RecognitionException {
		InterfaceTypeContext _localctx = new InterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_interfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(565);
			classType();
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

	public static class InterfaceType_lf_classOrInterfaceTypeContext extends ParserRuleContext {
		public ClassType_lf_classOrInterfaceTypeContext classType_lf_classOrInterfaceType() {
			return getRuleContext(ClassType_lf_classOrInterfaceTypeContext.class,0);
		}
		public InterfaceType_lf_classOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceType_lf_classOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceType_lf_classOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceType_lf_classOrInterfaceType(this);
		}
	}

	public final InterfaceType_lf_classOrInterfaceTypeContext interfaceType_lf_classOrInterfaceType() throws RecognitionException {
		InterfaceType_lf_classOrInterfaceTypeContext _localctx = new InterfaceType_lf_classOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_interfaceType_lf_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(567);
			classType_lf_classOrInterfaceType();
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

	public static class InterfaceType_lfno_classOrInterfaceTypeContext extends ParserRuleContext {
		public ClassType_lfno_classOrInterfaceTypeContext classType_lfno_classOrInterfaceType() {
			return getRuleContext(ClassType_lfno_classOrInterfaceTypeContext.class,0);
		}
		public InterfaceType_lfno_classOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceType_lfno_classOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceType_lfno_classOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceType_lfno_classOrInterfaceType(this);
		}
	}

	public final InterfaceType_lfno_classOrInterfaceTypeContext interfaceType_lfno_classOrInterfaceType() throws RecognitionException {
		InterfaceType_lfno_classOrInterfaceTypeContext _localctx = new InterfaceType_lfno_classOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_interfaceType_lfno_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(569);
			classType_lfno_classOrInterfaceType();
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

	public static class TypeVariableContext extends ParserRuleContext {
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
		public TypeVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeVariable(this);
		}
	}

	public final TypeVariableContext typeVariable() throws RecognitionException {
		TypeVariableContext _localctx = new TypeVariableContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_typeVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(571);
			annotationIdentifier();
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

	public static class ArrayTypeContext extends ParserRuleContext {
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public DimsContext dims() {
			return getRuleContext(DimsContext.class,0);
		}
		public ClassOrInterfaceTypeContext classOrInterfaceType() {
			return getRuleContext(ClassOrInterfaceTypeContext.class,0);
		}
		public TypeVariableContext typeVariable() {
			return getRuleContext(TypeVariableContext.class,0);
		}
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitArrayType(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_arrayType);
		try {
			setState(582);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(573);
				primitiveType();
				setState(574);
				dims();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(576);
				classOrInterfaceType();
				setState(577);
				dims();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(579);
				typeVariable();
				setState(580);
				dims();
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

	public static class DimsContext extends ParserRuleContext {
		public List<AnnotationDimContext> annotationDim() {
			return getRuleContexts(AnnotationDimContext.class);
		}
		public AnnotationDimContext annotationDim(int i) {
			return getRuleContext(AnnotationDimContext.class,i);
		}
		public DimsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dims; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterDims(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitDims(this);
		}
	}

	public final DimsContext dims() throws RecognitionException {
		DimsContext _localctx = new DimsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_dims);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(584);
			annotationDim();
			setState(588);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(585);
					annotationDim();
					}
					} 
				}
				setState(590);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
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

	public static class TypeParameterContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public List<TypeParameterModifierContext> typeParameterModifier() {
			return getRuleContexts(TypeParameterModifierContext.class);
		}
		public TypeParameterModifierContext typeParameterModifier(int i) {
			return getRuleContext(TypeParameterModifierContext.class,i);
		}
		public TypeBoundContext typeBound() {
			return getRuleContext(TypeBoundContext.class,0);
		}
		public TypeParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeParameter(this);
		}
	}

	public final TypeParameterContext typeParameter() throws RecognitionException {
		TypeParameterContext _localctx = new TypeParameterContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_typeParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(594);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(591);
				typeParameterModifier();
				}
				}
				setState(596);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(597);
			match(Identifier);
			setState(599);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(598);
				typeBound();
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

	public static class TypeParameterModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TypeParameterModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameterModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeParameterModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeParameterModifier(this);
		}
	}

	public final TypeParameterModifierContext typeParameterModifier() throws RecognitionException {
		TypeParameterModifierContext _localctx = new TypeParameterModifierContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_typeParameterModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(601);
			annotation();
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

	public static class TypeBoundContext extends ParserRuleContext {
		public TerminalNode EXTENDS() { return getToken(Java8Parser.EXTENDS, 0); }
		public TypeVariableContext typeVariable() {
			return getRuleContext(TypeVariableContext.class,0);
		}
		public ClassOrInterfaceTypeContext classOrInterfaceType() {
			return getRuleContext(ClassOrInterfaceTypeContext.class,0);
		}
		public List<AdditionalBoundContext> additionalBound() {
			return getRuleContexts(AdditionalBoundContext.class);
		}
		public AdditionalBoundContext additionalBound(int i) {
			return getRuleContext(AdditionalBoundContext.class,i);
		}
		public TypeBoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeBound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeBound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeBound(this);
		}
	}

	public final TypeBoundContext typeBound() throws RecognitionException {
		TypeBoundContext _localctx = new TypeBoundContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_typeBound);
		int _la;
		try {
			setState(613);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(603);
				match(EXTENDS);
				setState(604);
				typeVariable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(605);
				match(EXTENDS);
				setState(606);
				classOrInterfaceType();
				setState(610);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(607);
					additionalBound();
					}
					}
					setState(612);
					_errHandler.sync(this);
					_la = _input.LA(1);
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

	public static class AdditionalBoundContext extends ParserRuleContext {
		public TerminalNode BITAND() { return getToken(Java8Parser.BITAND, 0); }
		public InterfaceTypeContext interfaceType() {
			return getRuleContext(InterfaceTypeContext.class,0);
		}
		public AdditionalBoundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additionalBound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAdditionalBound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAdditionalBound(this);
		}
	}

	public final AdditionalBoundContext additionalBound() throws RecognitionException {
		AdditionalBoundContext _localctx = new AdditionalBoundContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_additionalBound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
			match(BITAND);
			setState(616);
			interfaceType();
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

	public static class TypeArgumentsContext extends ParserRuleContext {
		public TerminalNode LT() { return getToken(Java8Parser.LT, 0); }
		public TypeArgumentListContext typeArgumentList() {
			return getRuleContext(TypeArgumentListContext.class,0);
		}
		public TerminalNode GT() { return getToken(Java8Parser.GT, 0); }
		public TypeArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeArguments(this);
		}
	}

	public final TypeArgumentsContext typeArguments() throws RecognitionException {
		TypeArgumentsContext _localctx = new TypeArgumentsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_typeArguments);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(618);
			match(LT);
			setState(619);
			typeArgumentList();
			setState(620);
			match(GT);
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

	public static class TypeArgumentListContext extends ParserRuleContext {
		public List<TypeArgumentContext> typeArgument() {
			return getRuleContexts(TypeArgumentContext.class);
		}
		public TypeArgumentContext typeArgument(int i) {
			return getRuleContext(TypeArgumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public TypeArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArgumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeArgumentList(this);
		}
	}

	public final TypeArgumentListContext typeArgumentList() throws RecognitionException {
		TypeArgumentListContext _localctx = new TypeArgumentListContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_typeArgumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(622);
			typeArgument();
			setState(627);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(623);
				match(COMMA);
				setState(624);
				typeArgument();
				}
				}
				setState(629);
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

	public static class TypeArgumentContext extends ParserRuleContext {
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public WildcardContext wildcard() {
			return getRuleContext(WildcardContext.class,0);
		}
		public TypeArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeArgument(this);
		}
	}

	public final TypeArgumentContext typeArgument() throws RecognitionException {
		TypeArgumentContext _localctx = new TypeArgumentContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_typeArgument);
		try {
			setState(632);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(630);
				referenceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(631);
				wildcard();
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

	public static class WildcardContext extends ParserRuleContext {
		public TerminalNode QUESTION() { return getToken(Java8Parser.QUESTION, 0); }
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public WildcardBoundsContext wildcardBounds() {
			return getRuleContext(WildcardBoundsContext.class,0);
		}
		public WildcardContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wildcard; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterWildcard(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitWildcard(this);
		}
	}

	public final WildcardContext wildcard() throws RecognitionException {
		WildcardContext _localctx = new WildcardContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_wildcard);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(637);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(634);
				annotation();
				}
				}
				setState(639);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(640);
			match(QUESTION);
			setState(642);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS || _la==SUPER) {
				{
				setState(641);
				wildcardBounds();
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

	public static class WildcardBoundsContext extends ParserRuleContext {
		public TerminalNode EXTENDS() { return getToken(Java8Parser.EXTENDS, 0); }
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public WildcardBoundsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wildcardBounds; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterWildcardBounds(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitWildcardBounds(this);
		}
	}

	public final WildcardBoundsContext wildcardBounds() throws RecognitionException {
		WildcardBoundsContext _localctx = new WildcardBoundsContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_wildcardBounds);
		try {
			setState(648);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXTENDS:
				enterOuterAlt(_localctx, 1);
				{
				setState(644);
				match(EXTENDS);
				setState(645);
				referenceType();
				}
				break;
			case SUPER:
				enterOuterAlt(_localctx, 2);
				{
				setState(646);
				match(SUPER);
				setState(647);
				referenceType();
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

	public static class PackageNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public PackageNameContext packageName() {
			return getRuleContext(PackageNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public PackageNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPackageName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPackageName(this);
		}
	}

	public final PackageNameContext packageName() throws RecognitionException {
		return packageName(0);
	}

	private PackageNameContext packageName(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PackageNameContext _localctx = new PackageNameContext(_ctx, _parentState);
		PackageNameContext _prevctx = _localctx;
		int _startState = 52;
		enterRecursionRule(_localctx, 52, RULE_packageName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(651);
			match(Identifier);
			}
			_ctx.stop = _input.LT(-1);
			setState(658);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PackageNameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_packageName);
					setState(653);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(654);
					match(DOT);
					setState(655);
					match(Identifier);
					}
					} 
				}
				setState(660);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
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

	public static class TypeNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public PackageOrTypeNameContext packageOrTypeName() {
			return getRuleContext(PackageOrTypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeName(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_typeName);
		try {
			setState(666);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(661);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(662);
				packageOrTypeName(0);
				setState(663);
				match(DOT);
				setState(664);
				match(Identifier);
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

	public static class PackageOrTypeNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public PackageOrTypeNameContext packageOrTypeName() {
			return getRuleContext(PackageOrTypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public PackageOrTypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageOrTypeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPackageOrTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPackageOrTypeName(this);
		}
	}

	public final PackageOrTypeNameContext packageOrTypeName() throws RecognitionException {
		return packageOrTypeName(0);
	}

	private PackageOrTypeNameContext packageOrTypeName(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PackageOrTypeNameContext _localctx = new PackageOrTypeNameContext(_ctx, _parentState);
		PackageOrTypeNameContext _prevctx = _localctx;
		int _startState = 56;
		enterRecursionRule(_localctx, 56, RULE_packageOrTypeName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(669);
			match(Identifier);
			}
			_ctx.stop = _input.LT(-1);
			setState(676);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PackageOrTypeNameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_packageOrTypeName);
					setState(671);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(672);
					match(DOT);
					setState(673);
					match(Identifier);
					}
					} 
				}
				setState(678);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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

	public static class ExpressionNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public AmbiguousNameContext ambiguousName() {
			return getRuleContext(AmbiguousNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public ExpressionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExpressionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExpressionName(this);
		}
	}

	public final ExpressionNameContext expressionName() throws RecognitionException {
		ExpressionNameContext _localctx = new ExpressionNameContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_expressionName);
		try {
			setState(684);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(679);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(680);
				ambiguousName(0);
				setState(681);
				match(DOT);
				setState(682);
				match(Identifier);
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

	public static class MethodNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public MethodNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodName(this);
		}
	}

	public final MethodNameContext methodName() throws RecognitionException {
		MethodNameContext _localctx = new MethodNameContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_methodName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(686);
			match(Identifier);
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

	public static class AmbiguousNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public AmbiguousNameContext ambiguousName() {
			return getRuleContext(AmbiguousNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public AmbiguousNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ambiguousName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAmbiguousName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAmbiguousName(this);
		}
	}

	public final AmbiguousNameContext ambiguousName() throws RecognitionException {
		return ambiguousName(0);
	}

	private AmbiguousNameContext ambiguousName(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AmbiguousNameContext _localctx = new AmbiguousNameContext(_ctx, _parentState);
		AmbiguousNameContext _prevctx = _localctx;
		int _startState = 62;
		enterRecursionRule(_localctx, 62, RULE_ambiguousName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(689);
			match(Identifier);
			}
			_ctx.stop = _input.LT(-1);
			setState(696);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AmbiguousNameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_ambiguousName);
					setState(691);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(692);
					match(DOT);
					setState(693);
					match(Identifier);
					}
					} 
				}
				setState(698);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
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

	public static class CompilationUnitContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(Java8Parser.EOF, 0); }
		public PackageDeclarationContext packageDeclaration() {
			return getRuleContext(PackageDeclarationContext.class,0);
		}
		public List<ImportDeclarationContext> importDeclaration() {
			return getRuleContexts(ImportDeclarationContext.class);
		}
		public ImportDeclarationContext importDeclaration(int i) {
			return getRuleContext(ImportDeclarationContext.class,i);
		}
		public List<TypeDeclarationContext> typeDeclaration() {
			return getRuleContexts(TypeDeclarationContext.class);
		}
		public TypeDeclarationContext typeDeclaration(int i) {
			return getRuleContext(TypeDeclarationContext.class,i);
		}
		public CompilationUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compilationUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterCompilationUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitCompilationUnit(this);
		}
	}

	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_compilationUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(700);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				setState(699);
				packageDeclaration();
				}
				break;
			}
			setState(705);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(702);
				importDeclaration();
				}
				}
				setState(707);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(711);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << CLASS) | (1L << ENUM) | (1L << FINAL) | (1L << INTERFACE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==SEMI || _la==AT) {
				{
				{
				setState(708);
				typeDeclaration();
				}
				}
				setState(713);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(714);
			match(EOF);
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

	public static class PackageDeclarationContext extends ParserRuleContext {
		public TerminalNode PACKAGE() { return getToken(Java8Parser.PACKAGE, 0); }
		public List<TerminalNode> Identifier() { return getTokens(Java8Parser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(Java8Parser.Identifier, i);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public List<PackageModifierContext> packageModifier() {
			return getRuleContexts(PackageModifierContext.class);
		}
		public PackageModifierContext packageModifier(int i) {
			return getRuleContext(PackageModifierContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public PackageDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPackageDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPackageDeclaration(this);
		}
	}

	public final PackageDeclarationContext packageDeclaration() throws RecognitionException {
		PackageDeclarationContext _localctx = new PackageDeclarationContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_packageDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(719);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(716);
				packageModifier();
				}
				}
				setState(721);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(722);
			match(PACKAGE);
			setState(723);
			match(Identifier);
			setState(728);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(724);
				match(DOT);
				setState(725);
				match(Identifier);
				}
				}
				setState(730);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(731);
			match(SEMI);
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

	public static class PackageModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public PackageModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPackageModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPackageModifier(this);
		}
	}

	public final PackageModifierContext packageModifier() throws RecognitionException {
		PackageModifierContext _localctx = new PackageModifierContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_packageModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(733);
			annotation();
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

	public static class ImportDeclarationContext extends ParserRuleContext {
		public SingleTypeImportDeclarationContext singleTypeImportDeclaration() {
			return getRuleContext(SingleTypeImportDeclarationContext.class,0);
		}
		public TypeImportOnDemandDeclarationContext typeImportOnDemandDeclaration() {
			return getRuleContext(TypeImportOnDemandDeclarationContext.class,0);
		}
		public SingleStaticImportDeclarationContext singleStaticImportDeclaration() {
			return getRuleContext(SingleStaticImportDeclarationContext.class,0);
		}
		public StaticImportOnDemandDeclarationContext staticImportOnDemandDeclaration() {
			return getRuleContext(StaticImportOnDemandDeclarationContext.class,0);
		}
		public ImportDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterImportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitImportDeclaration(this);
		}
	}

	public final ImportDeclarationContext importDeclaration() throws RecognitionException {
		ImportDeclarationContext _localctx = new ImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_importDeclaration);
		try {
			setState(739);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(735);
				singleTypeImportDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(736);
				typeImportOnDemandDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(737);
				singleStaticImportDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(738);
				staticImportOnDemandDeclaration();
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

	public static class SingleTypeImportDeclarationContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(Java8Parser.IMPORT, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public SingleTypeImportDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleTypeImportDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSingleTypeImportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSingleTypeImportDeclaration(this);
		}
	}

	public final SingleTypeImportDeclarationContext singleTypeImportDeclaration() throws RecognitionException {
		SingleTypeImportDeclarationContext _localctx = new SingleTypeImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_singleTypeImportDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(741);
			match(IMPORT);
			setState(742);
			typeName();
			setState(743);
			match(SEMI);
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

	public static class TypeImportOnDemandDeclarationContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(Java8Parser.IMPORT, 0); }
		public PackageOrTypeNameContext packageOrTypeName() {
			return getRuleContext(PackageOrTypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode MUL() { return getToken(Java8Parser.MUL, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public TypeImportOnDemandDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeImportOnDemandDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeImportOnDemandDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeImportOnDemandDeclaration(this);
		}
	}

	public final TypeImportOnDemandDeclarationContext typeImportOnDemandDeclaration() throws RecognitionException {
		TypeImportOnDemandDeclarationContext _localctx = new TypeImportOnDemandDeclarationContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_typeImportOnDemandDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(745);
			match(IMPORT);
			setState(746);
			packageOrTypeName(0);
			setState(747);
			match(DOT);
			setState(748);
			match(MUL);
			setState(749);
			match(SEMI);
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

	public static class SingleStaticImportDeclarationContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(Java8Parser.IMPORT, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public SingleStaticImportDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleStaticImportDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSingleStaticImportDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSingleStaticImportDeclaration(this);
		}
	}

	public final SingleStaticImportDeclarationContext singleStaticImportDeclaration() throws RecognitionException {
		SingleStaticImportDeclarationContext _localctx = new SingleStaticImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_singleStaticImportDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(751);
			match(IMPORT);
			setState(752);
			match(STATIC);
			setState(753);
			typeName();
			setState(754);
			match(DOT);
			setState(755);
			match(Identifier);
			setState(756);
			match(SEMI);
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

	public static class StaticImportOnDemandDeclarationContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(Java8Parser.IMPORT, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode MUL() { return getToken(Java8Parser.MUL, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public StaticImportOnDemandDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_staticImportOnDemandDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterStaticImportOnDemandDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitStaticImportOnDemandDeclaration(this);
		}
	}

	public final StaticImportOnDemandDeclarationContext staticImportOnDemandDeclaration() throws RecognitionException {
		StaticImportOnDemandDeclarationContext _localctx = new StaticImportOnDemandDeclarationContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_staticImportOnDemandDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(758);
			match(IMPORT);
			setState(759);
			match(STATIC);
			setState(760);
			typeName();
			setState(761);
			match(DOT);
			setState(762);
			match(MUL);
			setState(763);
			match(SEMI);
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
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public InterfaceDeclarationContext interfaceDeclaration() {
			return getRuleContext(InterfaceDeclarationContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public TypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeDeclaration(this);
		}
	}

	public final TypeDeclarationContext typeDeclaration() throws RecognitionException {
		TypeDeclarationContext _localctx = new TypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_typeDeclaration);
		try {
			setState(768);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(765);
				classDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(766);
				interfaceDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(767);
				match(SEMI);
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

	public static class ClassDeclarationContext extends ParserRuleContext {
		public NormalClassDeclarationContext normalClassDeclaration() {
			return getRuleContext(NormalClassDeclarationContext.class,0);
		}
		public EnumDeclarationContext enumDeclaration() {
			return getRuleContext(EnumDeclarationContext.class,0);
		}
		public ClassDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassDeclaration(this);
		}
	}

	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_classDeclaration);
		try {
			setState(772);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(770);
				normalClassDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(771);
				enumDeclaration();
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

	public static class NormalClassDeclarationContext extends ParserRuleContext {
		public ClassModifiersContext classModifiers() {
			return getRuleContext(ClassModifiersContext.class,0);
		}
		public TerminalNode CLASS() { return getToken(Java8Parser.CLASS, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public ClassBodyContext classBody() {
			return getRuleContext(ClassBodyContext.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public SuperclassContext superclass() {
			return getRuleContext(SuperclassContext.class,0);
		}
		public SuperinterfacesContext superinterfaces() {
			return getRuleContext(SuperinterfacesContext.class,0);
		}
		public NormalClassDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalClassDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterNormalClassDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitNormalClassDeclaration(this);
		}
	}

	public final NormalClassDeclarationContext normalClassDeclaration() throws RecognitionException {
		NormalClassDeclarationContext _localctx = new NormalClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_normalClassDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(774);
			classModifiers();
			setState(775);
			match(CLASS);
			setState(776);
			match(Identifier);
			setState(778);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(777);
				typeParameters();
				}
			}

			setState(781);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(780);
				superclass();
				}
			}

			setState(784);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPLEMENTS) {
				{
				setState(783);
				superinterfaces();
				}
			}

			setState(786);
			classBody();
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

	public static class ClassModifiersContext extends ParserRuleContext {
		public List<ClassModifierContext> classModifier() {
			return getRuleContexts(ClassModifierContext.class);
		}
		public ClassModifierContext classModifier(int i) {
			return getRuleContext(ClassModifierContext.class,i);
		}
		public ClassModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classModifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassModifiers(this);
		}
	}

	public final ClassModifiersContext classModifiers() throws RecognitionException {
		ClassModifiersContext _localctx = new ClassModifiersContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_classModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(791);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << FINAL) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(788);
				classModifier();
				}
				}
				setState(793);
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

	public static class ClassModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode PROTECTED() { return getToken(Java8Parser.PROTECTED, 0); }
		public TerminalNode PRIVATE() { return getToken(Java8Parser.PRIVATE, 0); }
		public TerminalNode ABSTRACT() { return getToken(Java8Parser.ABSTRACT, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TerminalNode FINAL() { return getToken(Java8Parser.FINAL, 0); }
		public TerminalNode STRICTFP() { return getToken(Java8Parser.STRICTFP, 0); }
		public ClassModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassModifier(this);
		}
	}

	public final ClassModifierContext classModifier() throws RecognitionException {
		ClassModifierContext _localctx = new ClassModifierContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_classModifier);
		try {
			setState(802);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(794);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(795);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(796);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(797);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(798);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(799);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(800);
				match(FINAL);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 8);
				{
				setState(801);
				match(STRICTFP);
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

	public static class TypeParametersContext extends ParserRuleContext {
		public TerminalNode LT() { return getToken(Java8Parser.LT, 0); }
		public TypeParameterListContext typeParameterList() {
			return getRuleContext(TypeParameterListContext.class,0);
		}
		public TerminalNode GT() { return getToken(Java8Parser.GT, 0); }
		public TypeParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeParameters(this);
		}
	}

	public final TypeParametersContext typeParameters() throws RecognitionException {
		TypeParametersContext _localctx = new TypeParametersContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_typeParameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(804);
			match(LT);
			setState(805);
			typeParameterList();
			setState(806);
			match(GT);
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

	public static class TypeParameterListContext extends ParserRuleContext {
		public List<TypeParameterContext> typeParameter() {
			return getRuleContexts(TypeParameterContext.class);
		}
		public TypeParameterContext typeParameter(int i) {
			return getRuleContext(TypeParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public TypeParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeParameterList(this);
		}
	}

	public final TypeParameterListContext typeParameterList() throws RecognitionException {
		TypeParameterListContext _localctx = new TypeParameterListContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_typeParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(808);
			typeParameter();
			setState(813);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(809);
				match(COMMA);
				setState(810);
				typeParameter();
				}
				}
				setState(815);
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

	public static class SuperclassContext extends ParserRuleContext {
		public TerminalNode EXTENDS() { return getToken(Java8Parser.EXTENDS, 0); }
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public SuperclassContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_superclass; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSuperclass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSuperclass(this);
		}
	}

	public final SuperclassContext superclass() throws RecognitionException {
		SuperclassContext _localctx = new SuperclassContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_superclass);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(816);
			match(EXTENDS);
			setState(817);
			classType();
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

	public static class SuperinterfacesContext extends ParserRuleContext {
		public TerminalNode IMPLEMENTS() { return getToken(Java8Parser.IMPLEMENTS, 0); }
		public InterfaceTypeListContext interfaceTypeList() {
			return getRuleContext(InterfaceTypeListContext.class,0);
		}
		public SuperinterfacesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_superinterfaces; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSuperinterfaces(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSuperinterfaces(this);
		}
	}

	public final SuperinterfacesContext superinterfaces() throws RecognitionException {
		SuperinterfacesContext _localctx = new SuperinterfacesContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_superinterfaces);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(819);
			match(IMPLEMENTS);
			setState(820);
			interfaceTypeList();
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

	public static class InterfaceTypeListContext extends ParserRuleContext {
		public List<InterfaceTypeContext> interfaceType() {
			return getRuleContexts(InterfaceTypeContext.class);
		}
		public InterfaceTypeContext interfaceType(int i) {
			return getRuleContext(InterfaceTypeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public InterfaceTypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceTypeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceTypeList(this);
		}
	}

	public final InterfaceTypeListContext interfaceTypeList() throws RecognitionException {
		InterfaceTypeListContext _localctx = new InterfaceTypeListContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_interfaceTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(822);
			interfaceType();
			setState(827);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(823);
				match(COMMA);
				setState(824);
				interfaceType();
				}
				}
				setState(829);
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

	public static class ClassBodyContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public List<ClassBodyDeclarationContext> classBodyDeclaration() {
			return getRuleContexts(ClassBodyDeclarationContext.class);
		}
		public ClassBodyDeclarationContext classBodyDeclaration(int i) {
			return getRuleContext(ClassBodyDeclarationContext.class,i);
		}
		public ClassBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassBody(this);
		}
	}

	public final ClassBodyContext classBody() throws RecognitionException {
		ClassBodyContext _localctx = new ClassBodyContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_classBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(830);
			match(LBRACE);
			setState(834);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE) | (1L << LBRACE))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (SEMI - 65)) | (1L << (LT - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				{
				setState(831);
				classBodyDeclaration();
				}
				}
				setState(836);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(837);
			match(RBRACE);
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

	public static class ClassBodyDeclarationContext extends ParserRuleContext {
		public ClassMemberDeclarationContext classMemberDeclaration() {
			return getRuleContext(ClassMemberDeclarationContext.class,0);
		}
		public InstanceInitializerContext instanceInitializer() {
			return getRuleContext(InstanceInitializerContext.class,0);
		}
		public StaticInitializerContext staticInitializer() {
			return getRuleContext(StaticInitializerContext.class,0);
		}
		public ConstructorDeclarationContext constructorDeclaration() {
			return getRuleContext(ConstructorDeclarationContext.class,0);
		}
		public ClassBodyDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classBodyDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassBodyDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassBodyDeclaration(this);
		}
	}

	public final ClassBodyDeclarationContext classBodyDeclaration() throws RecognitionException {
		ClassBodyDeclarationContext _localctx = new ClassBodyDeclarationContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_classBodyDeclaration);
		try {
			setState(843);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(839);
				classMemberDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(840);
				instanceInitializer();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(841);
				staticInitializer();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(842);
				constructorDeclaration();
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

	public static class ClassMemberDeclarationContext extends ParserRuleContext {
		public FieldDeclarationContext fieldDeclaration() {
			return getRuleContext(FieldDeclarationContext.class,0);
		}
		public MethodDeclarationContext methodDeclaration() {
			return getRuleContext(MethodDeclarationContext.class,0);
		}
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public InterfaceDeclarationContext interfaceDeclaration() {
			return getRuleContext(InterfaceDeclarationContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public ClassMemberDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classMemberDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassMemberDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassMemberDeclaration(this);
		}
	}

	public final ClassMemberDeclarationContext classMemberDeclaration() throws RecognitionException {
		ClassMemberDeclarationContext _localctx = new ClassMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_classMemberDeclaration);
		try {
			setState(850);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(845);
				fieldDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(846);
				methodDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(847);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(848);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(849);
				match(SEMI);
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

	public static class FieldDeclarationContext extends ParserRuleContext {
		public FieldModifiersContext fieldModifiers() {
			return getRuleContext(FieldModifiersContext.class,0);
		}
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorListContext variableDeclaratorList() {
			return getRuleContext(VariableDeclaratorListContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public FieldDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFieldDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFieldDeclaration(this);
		}
	}

	public final FieldDeclarationContext fieldDeclaration() throws RecognitionException {
		FieldDeclarationContext _localctx = new FieldDeclarationContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_fieldDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(852);
			fieldModifiers();
			setState(853);
			unannType();
			setState(854);
			variableDeclaratorList();
			setState(855);
			match(SEMI);
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

	public static class FieldModifiersContext extends ParserRuleContext {
		public List<FieldModifierContext> fieldModifier() {
			return getRuleContexts(FieldModifierContext.class);
		}
		public FieldModifierContext fieldModifier(int i) {
			return getRuleContext(FieldModifierContext.class,i);
		}
		public FieldModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldModifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFieldModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFieldModifiers(this);
		}
	}

	public final FieldModifiersContext fieldModifiers() throws RecognitionException {
		FieldModifiersContext _localctx = new FieldModifiersContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_fieldModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(860);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FINAL) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << TRANSIENT) | (1L << VOLATILE))) != 0) || _la==AT) {
				{
				{
				setState(857);
				fieldModifier();
				}
				}
				setState(862);
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

	public static class FieldModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode PROTECTED() { return getToken(Java8Parser.PROTECTED, 0); }
		public TerminalNode PRIVATE() { return getToken(Java8Parser.PRIVATE, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TerminalNode FINAL() { return getToken(Java8Parser.FINAL, 0); }
		public TerminalNode TRANSIENT() { return getToken(Java8Parser.TRANSIENT, 0); }
		public TerminalNode VOLATILE() { return getToken(Java8Parser.VOLATILE, 0); }
		public FieldModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFieldModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFieldModifier(this);
		}
	}

	public final FieldModifierContext fieldModifier() throws RecognitionException {
		FieldModifierContext _localctx = new FieldModifierContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_fieldModifier);
		try {
			setState(871);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(863);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(864);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(865);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(866);
				match(PRIVATE);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 5);
				{
				setState(867);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 6);
				{
				setState(868);
				match(FINAL);
				}
				break;
			case TRANSIENT:
				enterOuterAlt(_localctx, 7);
				{
				setState(869);
				match(TRANSIENT);
				}
				break;
			case VOLATILE:
				enterOuterAlt(_localctx, 8);
				{
				setState(870);
				match(VOLATILE);
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

	public static class VariableDeclaratorListContext extends ParserRuleContext {
		public List<VariableDeclaratorContext> variableDeclarator() {
			return getRuleContexts(VariableDeclaratorContext.class);
		}
		public VariableDeclaratorContext variableDeclarator(int i) {
			return getRuleContext(VariableDeclaratorContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public VariableDeclaratorListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaratorList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterVariableDeclaratorList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitVariableDeclaratorList(this);
		}
	}

	public final VariableDeclaratorListContext variableDeclaratorList() throws RecognitionException {
		VariableDeclaratorListContext _localctx = new VariableDeclaratorListContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_variableDeclaratorList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(873);
			variableDeclarator();
			setState(878);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(874);
				match(COMMA);
				setState(875);
				variableDeclarator();
				}
				}
				setState(880);
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

	public static class VariableDeclaratorContext extends ParserRuleContext {
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(Java8Parser.ASSIGN, 0); }
		public VariableInitializerContext variableInitializer() {
			return getRuleContext(VariableInitializerContext.class,0);
		}
		public VariableDeclaratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterVariableDeclarator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitVariableDeclarator(this);
		}
	}

	public final VariableDeclaratorContext variableDeclarator() throws RecognitionException {
		VariableDeclaratorContext _localctx = new VariableDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_variableDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(881);
			variableDeclaratorId();
			setState(884);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(882);
				match(ASSIGN);
				setState(883);
				variableInitializer();
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

	public static class VariableDeclaratorIdContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public DimsContext dims() {
			return getRuleContext(DimsContext.class,0);
		}
		public VariableDeclaratorIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaratorId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterVariableDeclaratorId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitVariableDeclaratorId(this);
		}
	}

	public final VariableDeclaratorIdContext variableDeclaratorId() throws RecognitionException {
		VariableDeclaratorIdContext _localctx = new VariableDeclaratorIdContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_variableDeclaratorId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(886);
			match(Identifier);
			setState(888);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1 || _la==AT) {
				{
				setState(887);
				dims();
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

	public static class VariableInitializerContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArrayInitializerContext arrayInitializer() {
			return getRuleContext(ArrayInitializerContext.class,0);
		}
		public VariableInitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableInitializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterVariableInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitVariableInitializer(this);
		}
	}

	public final VariableInitializerContext variableInitializer() throws RecognitionException {
		VariableInitializerContext _localctx = new VariableInitializerContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_variableInitializer);
		try {
			setState(892);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case NEW:
			case SHORT:
			case SUPER:
			case THIS:
			case VOID:
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case CharacterLiteral:
			case StringLiteral:
			case NullLiteral:
			case LPAREN:
			case BANG:
			case TILDE:
			case INC:
			case DEC:
			case ADD:
			case SUB:
			case Identifier:
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(890);
				expression();
				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(891);
				arrayInitializer();
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

	public static class UnannTypeContext extends ParserRuleContext {
		public UnannPrimitiveTypeContext unannPrimitiveType() {
			return getRuleContext(UnannPrimitiveTypeContext.class,0);
		}
		public UnannReferenceTypeContext unannReferenceType() {
			return getRuleContext(UnannReferenceTypeContext.class,0);
		}
		public UnannTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannType(this);
		}
	}

	public final UnannTypeContext unannType() throws RecognitionException {
		UnannTypeContext _localctx = new UnannTypeContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_unannType);
		try {
			setState(896);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(894);
				unannPrimitiveType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(895);
				unannReferenceType();
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

	public static class UnannPrimitiveTypeContext extends ParserRuleContext {
		public NumericTypeContext numericType() {
			return getRuleContext(NumericTypeContext.class,0);
		}
		public TerminalNode BOOLEAN() { return getToken(Java8Parser.BOOLEAN, 0); }
		public UnannPrimitiveTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannPrimitiveType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannPrimitiveType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannPrimitiveType(this);
		}
	}

	public final UnannPrimitiveTypeContext unannPrimitiveType() throws RecognitionException {
		UnannPrimitiveTypeContext _localctx = new UnannPrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_unannPrimitiveType);
		try {
			setState(900);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
				enterOuterAlt(_localctx, 1);
				{
				setState(898);
				numericType();
				}
				break;
			case BOOLEAN:
				enterOuterAlt(_localctx, 2);
				{
				setState(899);
				match(BOOLEAN);
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

	public static class UnannReferenceTypeContext extends ParserRuleContext {
		public UnannClassOrInterfaceTypeContext unannClassOrInterfaceType() {
			return getRuleContext(UnannClassOrInterfaceTypeContext.class,0);
		}
		public UnannTypeVariableContext unannTypeVariable() {
			return getRuleContext(UnannTypeVariableContext.class,0);
		}
		public UnannArrayTypeContext unannArrayType() {
			return getRuleContext(UnannArrayTypeContext.class,0);
		}
		public UnannReferenceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannReferenceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannReferenceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannReferenceType(this);
		}
	}

	public final UnannReferenceTypeContext unannReferenceType() throws RecognitionException {
		UnannReferenceTypeContext _localctx = new UnannReferenceTypeContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_unannReferenceType);
		try {
			setState(905);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,56,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(902);
				unannClassOrInterfaceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(903);
				unannTypeVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(904);
				unannArrayType();
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

	public static class UnannClassOrInterfaceTypeContext extends ParserRuleContext {
		public UnannClassType_lfno_unannClassOrInterfaceTypeContext unannClassType_lfno_unannClassOrInterfaceType() {
			return getRuleContext(UnannClassType_lfno_unannClassOrInterfaceTypeContext.class,0);
		}
		public UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext unannInterfaceType_lfno_unannClassOrInterfaceType() {
			return getRuleContext(UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext.class,0);
		}
		public List<UnannClassType_lf_unannClassOrInterfaceTypeContext> unannClassType_lf_unannClassOrInterfaceType() {
			return getRuleContexts(UnannClassType_lf_unannClassOrInterfaceTypeContext.class);
		}
		public UnannClassType_lf_unannClassOrInterfaceTypeContext unannClassType_lf_unannClassOrInterfaceType(int i) {
			return getRuleContext(UnannClassType_lf_unannClassOrInterfaceTypeContext.class,i);
		}
		public List<UnannInterfaceType_lf_unannClassOrInterfaceTypeContext> unannInterfaceType_lf_unannClassOrInterfaceType() {
			return getRuleContexts(UnannInterfaceType_lf_unannClassOrInterfaceTypeContext.class);
		}
		public UnannInterfaceType_lf_unannClassOrInterfaceTypeContext unannInterfaceType_lf_unannClassOrInterfaceType(int i) {
			return getRuleContext(UnannInterfaceType_lf_unannClassOrInterfaceTypeContext.class,i);
		}
		public UnannClassOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannClassOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannClassOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannClassOrInterfaceType(this);
		}
	}

	public final UnannClassOrInterfaceTypeContext unannClassOrInterfaceType() throws RecognitionException {
		UnannClassOrInterfaceTypeContext _localctx = new UnannClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_unannClassOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(909);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(907);
				unannClassType_lfno_unannClassOrInterfaceType();
				}
				break;
			case 2:
				{
				setState(908);
				unannInterfaceType_lfno_unannClassOrInterfaceType();
				}
				break;
			}
			setState(915);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(913);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
					case 1:
						{
						setState(911);
						unannClassType_lf_unannClassOrInterfaceType();
						}
						break;
					case 2:
						{
						setState(912);
						unannInterfaceType_lf_unannClassOrInterfaceType();
						}
						break;
					}
					} 
				}
				setState(917);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,59,_ctx);
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

	public static class UnannClassTypeContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public UnannClassOrInterfaceTypeContext unannClassOrInterfaceType() {
			return getRuleContext(UnannClassOrInterfaceTypeContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
		public UnannClassTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannClassType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannClassType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannClassType(this);
		}
	}

	public final UnannClassTypeContext unannClassType() throws RecognitionException {
		UnannClassTypeContext _localctx = new UnannClassTypeContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_unannClassType);
		int _la;
		try {
			setState(928);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(918);
				match(Identifier);
				setState(920);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(919);
					typeArguments();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(922);
				unannClassOrInterfaceType();
				setState(923);
				match(DOT);
				setState(924);
				annotationIdentifier();
				setState(926);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(925);
					typeArguments();
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

	public static class UnannClassType_lf_unannClassOrInterfaceTypeContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public UnannClassType_lf_unannClassOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannClassType_lf_unannClassOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannClassType_lf_unannClassOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannClassType_lf_unannClassOrInterfaceType(this);
		}
	}

	public final UnannClassType_lf_unannClassOrInterfaceTypeContext unannClassType_lf_unannClassOrInterfaceType() throws RecognitionException {
		UnannClassType_lf_unannClassOrInterfaceTypeContext _localctx = new UnannClassType_lf_unannClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_unannClassType_lf_unannClassOrInterfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(930);
			match(DOT);
			setState(931);
			annotationIdentifier();
			setState(933);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(932);
				typeArguments();
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

	public static class UnannClassType_lfno_unannClassOrInterfaceTypeContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public UnannClassType_lfno_unannClassOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannClassType_lfno_unannClassOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannClassType_lfno_unannClassOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannClassType_lfno_unannClassOrInterfaceType(this);
		}
	}

	public final UnannClassType_lfno_unannClassOrInterfaceTypeContext unannClassType_lfno_unannClassOrInterfaceType() throws RecognitionException {
		UnannClassType_lfno_unannClassOrInterfaceTypeContext _localctx = new UnannClassType_lfno_unannClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_unannClassType_lfno_unannClassOrInterfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(935);
			match(Identifier);
			setState(937);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(936);
				typeArguments();
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

	public static class UnannInterfaceTypeContext extends ParserRuleContext {
		public UnannClassTypeContext unannClassType() {
			return getRuleContext(UnannClassTypeContext.class,0);
		}
		public UnannInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannInterfaceType(this);
		}
	}

	public final UnannInterfaceTypeContext unannInterfaceType() throws RecognitionException {
		UnannInterfaceTypeContext _localctx = new UnannInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_unannInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(939);
			unannClassType();
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

	public static class UnannInterfaceType_lf_unannClassOrInterfaceTypeContext extends ParserRuleContext {
		public UnannClassType_lf_unannClassOrInterfaceTypeContext unannClassType_lf_unannClassOrInterfaceType() {
			return getRuleContext(UnannClassType_lf_unannClassOrInterfaceTypeContext.class,0);
		}
		public UnannInterfaceType_lf_unannClassOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannInterfaceType_lf_unannClassOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannInterfaceType_lf_unannClassOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannInterfaceType_lf_unannClassOrInterfaceType(this);
		}
	}

	public final UnannInterfaceType_lf_unannClassOrInterfaceTypeContext unannInterfaceType_lf_unannClassOrInterfaceType() throws RecognitionException {
		UnannInterfaceType_lf_unannClassOrInterfaceTypeContext _localctx = new UnannInterfaceType_lf_unannClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_unannInterfaceType_lf_unannClassOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(941);
			unannClassType_lf_unannClassOrInterfaceType();
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

	public static class UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext extends ParserRuleContext {
		public UnannClassType_lfno_unannClassOrInterfaceTypeContext unannClassType_lfno_unannClassOrInterfaceType() {
			return getRuleContext(UnannClassType_lfno_unannClassOrInterfaceTypeContext.class,0);
		}
		public UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannInterfaceType_lfno_unannClassOrInterfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannInterfaceType_lfno_unannClassOrInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannInterfaceType_lfno_unannClassOrInterfaceType(this);
		}
	}

	public final UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext unannInterfaceType_lfno_unannClassOrInterfaceType() throws RecognitionException {
		UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext _localctx = new UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_unannInterfaceType_lfno_unannClassOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(943);
			unannClassType_lfno_unannClassOrInterfaceType();
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

	public static class UnannTypeVariableContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public UnannTypeVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannTypeVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannTypeVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannTypeVariable(this);
		}
	}

	public final UnannTypeVariableContext unannTypeVariable() throws RecognitionException {
		UnannTypeVariableContext _localctx = new UnannTypeVariableContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_unannTypeVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(945);
			match(Identifier);
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

	public static class UnannArrayTypeContext extends ParserRuleContext {
		public UnannPrimitiveTypeContext unannPrimitiveType() {
			return getRuleContext(UnannPrimitiveTypeContext.class,0);
		}
		public DimsContext dims() {
			return getRuleContext(DimsContext.class,0);
		}
		public UnannClassOrInterfaceTypeContext unannClassOrInterfaceType() {
			return getRuleContext(UnannClassOrInterfaceTypeContext.class,0);
		}
		public UnannTypeVariableContext unannTypeVariable() {
			return getRuleContext(UnannTypeVariableContext.class,0);
		}
		public UnannArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unannArrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnannArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnannArrayType(this);
		}
	}

	public final UnannArrayTypeContext unannArrayType() throws RecognitionException {
		UnannArrayTypeContext _localctx = new UnannArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_unannArrayType);
		try {
			setState(956);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(947);
				unannPrimitiveType();
				setState(948);
				dims();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(950);
				unannClassOrInterfaceType();
				setState(951);
				dims();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(953);
				unannTypeVariable();
				setState(954);
				dims();
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

	public static class MethodDeclarationContext extends ParserRuleContext {
		public MethodModifiersContext methodModifiers() {
			return getRuleContext(MethodModifiersContext.class,0);
		}
		public MethodHeaderContext methodHeader() {
			return getRuleContext(MethodHeaderContext.class,0);
		}
		public MethodBodyContext methodBody() {
			return getRuleContext(MethodBodyContext.class,0);
		}
		public MethodDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodDeclaration(this);
		}
	}

	public final MethodDeclarationContext methodDeclaration() throws RecognitionException {
		MethodDeclarationContext _localctx = new MethodDeclarationContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_methodDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(958);
			methodModifiers();
			setState(959);
			methodHeader();
			setState(960);
			methodBody();
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

	public static class MethodModifiersContext extends ParserRuleContext {
		public List<MethodModifierContext> methodModifier() {
			return getRuleContexts(MethodModifierContext.class);
		}
		public MethodModifierContext methodModifier(int i) {
			return getRuleContext(MethodModifierContext.class,i);
		}
		public MethodModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodModifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodModifiers(this);
		}
	}

	public final MethodModifiersContext methodModifiers() throws RecognitionException {
		MethodModifiersContext _localctx = new MethodModifiersContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_methodModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(965);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << FINAL) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED))) != 0) || _la==AT) {
				{
				{
				setState(962);
				methodModifier();
				}
				}
				setState(967);
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

	public static class MethodModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode PROTECTED() { return getToken(Java8Parser.PROTECTED, 0); }
		public TerminalNode PRIVATE() { return getToken(Java8Parser.PRIVATE, 0); }
		public TerminalNode ABSTRACT() { return getToken(Java8Parser.ABSTRACT, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TerminalNode FINAL() { return getToken(Java8Parser.FINAL, 0); }
		public TerminalNode SYNCHRONIZED() { return getToken(Java8Parser.SYNCHRONIZED, 0); }
		public TerminalNode NATIVE() { return getToken(Java8Parser.NATIVE, 0); }
		public TerminalNode STRICTFP() { return getToken(Java8Parser.STRICTFP, 0); }
		public MethodModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodModifier(this);
		}
	}

	public final MethodModifierContext methodModifier() throws RecognitionException {
		MethodModifierContext _localctx = new MethodModifierContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_methodModifier);
		try {
			setState(978);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(968);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(969);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(970);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(971);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(972);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(973);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(974);
				match(FINAL);
				}
				break;
			case SYNCHRONIZED:
				enterOuterAlt(_localctx, 8);
				{
				setState(975);
				match(SYNCHRONIZED);
				}
				break;
			case NATIVE:
				enterOuterAlt(_localctx, 9);
				{
				setState(976);
				match(NATIVE);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 10);
				{
				setState(977);
				match(STRICTFP);
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

	public static class MethodHeaderContext extends ParserRuleContext {
		public ResultContext result() {
			return getRuleContext(ResultContext.class,0);
		}
		public MethodDeclaratorContext methodDeclarator() {
			return getRuleContext(MethodDeclaratorContext.class,0);
		}
		public Throws_Context throws_() {
			return getRuleContext(Throws_Context.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public MethodHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodHeader(this);
		}
	}

	public final MethodHeaderContext methodHeader() throws RecognitionException {
		MethodHeaderContext _localctx = new MethodHeaderContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_methodHeader);
		int _la;
		try {
			setState(997);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
			case VOID:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(980);
				result();
				setState(981);
				methodDeclarator();
				setState(983);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==THROWS) {
					{
					setState(982);
					throws_();
					}
				}

				}
				break;
			case LT:
				enterOuterAlt(_localctx, 2);
				{
				setState(985);
				typeParameters();
				setState(989);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(986);
					annotation();
					}
					}
					setState(991);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(992);
				result();
				setState(993);
				methodDeclarator();
				setState(995);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==THROWS) {
					{
					setState(994);
					throws_();
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

	public static class ResultContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public TerminalNode VOID() { return getToken(Java8Parser.VOID, 0); }
		public ResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitResult(this);
		}
	}

	public final ResultContext result() throws RecognitionException {
		ResultContext _localctx = new ResultContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_result);
		try {
			setState(1001);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(999);
				unannType();
				}
				break;
			case VOID:
				enterOuterAlt(_localctx, 2);
				{
				setState(1000);
				match(VOID);
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

	public static class MethodDeclaratorContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public DimsContext dims() {
			return getRuleContext(DimsContext.class,0);
		}
		public MethodDeclaratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDeclarator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodDeclarator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodDeclarator(this);
		}
	}

	public final MethodDeclaratorContext methodDeclarator() throws RecognitionException {
		MethodDeclaratorContext _localctx = new MethodDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_methodDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1003);
			match(Identifier);
			setState(1004);
			match(LPAREN);
			setState(1006);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1005);
				formalParameterList();
				}
			}

			setState(1008);
			match(RPAREN);
			setState(1010);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1 || _la==AT) {
				{
				setState(1009);
				dims();
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

	public static class FormalParameterListContext extends ParserRuleContext {
		public FormalParametersContext formalParameters() {
			return getRuleContext(FormalParametersContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(Java8Parser.COMMA, 0); }
		public LastFormalParameterContext lastFormalParameter() {
			return getRuleContext(LastFormalParameterContext.class,0);
		}
		public FormalParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFormalParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFormalParameterList(this);
		}
	}

	public final FormalParameterListContext formalParameterList() throws RecognitionException {
		FormalParameterListContext _localctx = new FormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_formalParameterList);
		try {
			setState(1017);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1012);
				formalParameters();
				setState(1013);
				match(COMMA);
				setState(1014);
				lastFormalParameter();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1016);
				lastFormalParameter();
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

	public static class FormalParametersContext extends ParserRuleContext {
		public List<FormalParameterContext> formalParameter() {
			return getRuleContexts(FormalParameterContext.class);
		}
		public FormalParameterContext formalParameter(int i) {
			return getRuleContext(FormalParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public ReceiverParameterContext receiverParameter() {
			return getRuleContext(ReceiverParameterContext.class,0);
		}
		public FormalParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFormalParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFormalParameters(this);
		}
	}

	public final FormalParametersContext formalParameters() throws RecognitionException {
		FormalParametersContext _localctx = new FormalParametersContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_formalParameters);
		try {
			int _alt;
			setState(1035);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1019);
				formalParameter();
				setState(1024);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1020);
						match(COMMA);
						setState(1021);
						formalParameter();
						}
						} 
					}
					setState(1026);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,76,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1027);
				receiverParameter();
				setState(1032);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1028);
						match(COMMA);
						setState(1029);
						formalParameter();
						}
						} 
					}
					setState(1034);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,77,_ctx);
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

	public static class FormalParameterContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public List<VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public FormalParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFormalParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFormalParameter(this);
		}
	}

	public final FormalParameterContext formalParameter() throws RecognitionException {
		FormalParameterContext _localctx = new FormalParameterContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_formalParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1040);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1037);
				variableModifier();
				}
				}
				setState(1042);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1043);
			unannType();
			setState(1044);
			variableDeclaratorId();
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

	public static class VariableModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode FINAL() { return getToken(Java8Parser.FINAL, 0); }
		public VariableModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterVariableModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitVariableModifier(this);
		}
	}

	public final VariableModifierContext variableModifier() throws RecognitionException {
		VariableModifierContext _localctx = new VariableModifierContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_variableModifier);
		try {
			setState(1048);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1046);
				annotation();
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1047);
				match(FINAL);
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

	public static class LastFormalParameterContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public TerminalNode ELLIPSIS() { return getToken(Java8Parser.ELLIPSIS, 0); }
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public List<VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public FormalParameterContext formalParameter() {
			return getRuleContext(FormalParameterContext.class,0);
		}
		public LastFormalParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lastFormalParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLastFormalParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLastFormalParameter(this);
		}
	}

	public final LastFormalParameterContext lastFormalParameter() throws RecognitionException {
		LastFormalParameterContext _localctx = new LastFormalParameterContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_lastFormalParameter);
		int _la;
		try {
			setState(1067);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1053);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FINAL || _la==AT) {
					{
					{
					setState(1050);
					variableModifier();
					}
					}
					setState(1055);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1056);
				unannType();
				setState(1060);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(1057);
					annotation();
					}
					}
					setState(1062);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1063);
				match(ELLIPSIS);
				setState(1064);
				variableDeclaratorId();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1066);
				formalParameter();
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

	public static class ReceiverParameterContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public TerminalNode THIS() { return getToken(Java8Parser.THIS, 0); }
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public ReceiverParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_receiverParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterReceiverParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitReceiverParameter(this);
		}
	}

	public final ReceiverParameterContext receiverParameter() throws RecognitionException {
		ReceiverParameterContext _localctx = new ReceiverParameterContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_receiverParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1072);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1069);
				annotation();
				}
				}
				setState(1074);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1075);
			unannType();
			setState(1078);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1076);
				match(Identifier);
				setState(1077);
				match(DOT);
				}
			}

			setState(1080);
			match(THIS);
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

	public static class Throws_Context extends ParserRuleContext {
		public TerminalNode THROWS() { return getToken(Java8Parser.THROWS, 0); }
		public ExceptionTypeListContext exceptionTypeList() {
			return getRuleContext(ExceptionTypeListContext.class,0);
		}
		public Throws_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throws_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterThrows_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitThrows_(this);
		}
	}

	public final Throws_Context throws_() throws RecognitionException {
		Throws_Context _localctx = new Throws_Context(_ctx, getState());
		enterRule(_localctx, 168, RULE_throws_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1082);
			match(THROWS);
			setState(1083);
			exceptionTypeList();
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

	public static class ExceptionTypeListContext extends ParserRuleContext {
		public List<ExceptionTypeContext> exceptionType() {
			return getRuleContexts(ExceptionTypeContext.class);
		}
		public ExceptionTypeContext exceptionType(int i) {
			return getRuleContext(ExceptionTypeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public ExceptionTypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exceptionTypeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExceptionTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExceptionTypeList(this);
		}
	}

	public final ExceptionTypeListContext exceptionTypeList() throws RecognitionException {
		ExceptionTypeListContext _localctx = new ExceptionTypeListContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_exceptionTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1085);
			exceptionType();
			setState(1090);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1086);
				match(COMMA);
				setState(1087);
				exceptionType();
				}
				}
				setState(1092);
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

	public static class ExceptionTypeContext extends ParserRuleContext {
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public TypeVariableContext typeVariable() {
			return getRuleContext(TypeVariableContext.class,0);
		}
		public ExceptionTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exceptionType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExceptionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExceptionType(this);
		}
	}

	public final ExceptionTypeContext exceptionType() throws RecognitionException {
		ExceptionTypeContext _localctx = new ExceptionTypeContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_exceptionType);
		try {
			setState(1095);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1093);
				classType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1094);
				typeVariable();
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

	public static class MethodBodyContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public MethodBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodBody(this);
		}
	}

	public final MethodBodyContext methodBody() throws RecognitionException {
		MethodBodyContext _localctx = new MethodBodyContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_methodBody);
		try {
			setState(1099);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1097);
				block();
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
				setState(1098);
				match(SEMI);
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

	public static class InstanceInitializerContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public InstanceInitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instanceInitializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInstanceInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInstanceInitializer(this);
		}
	}

	public final InstanceInitializerContext instanceInitializer() throws RecognitionException {
		InstanceInitializerContext _localctx = new InstanceInitializerContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_instanceInitializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1101);
			block();
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

	public static class StaticInitializerContext extends ParserRuleContext {
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public StaticInitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_staticInitializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterStaticInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitStaticInitializer(this);
		}
	}

	public final StaticInitializerContext staticInitializer() throws RecognitionException {
		StaticInitializerContext _localctx = new StaticInitializerContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_staticInitializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1103);
			match(STATIC);
			setState(1104);
			block();
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

	public static class ConstructorDeclarationContext extends ParserRuleContext {
		public ConstructorModifiersContext constructorModifiers() {
			return getRuleContext(ConstructorModifiersContext.class,0);
		}
		public ConstructorDeclaratorContext constructorDeclarator() {
			return getRuleContext(ConstructorDeclaratorContext.class,0);
		}
		public ConstructorBodyContext constructorBody() {
			return getRuleContext(ConstructorBodyContext.class,0);
		}
		public Throws_Context throws_() {
			return getRuleContext(Throws_Context.class,0);
		}
		public ConstructorDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstructorDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstructorDeclaration(this);
		}
	}

	public final ConstructorDeclarationContext constructorDeclaration() throws RecognitionException {
		ConstructorDeclarationContext _localctx = new ConstructorDeclarationContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_constructorDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1106);
			constructorModifiers();
			setState(1107);
			constructorDeclarator();
			setState(1109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==THROWS) {
				{
				setState(1108);
				throws_();
				}
			}

			setState(1111);
			constructorBody();
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

	public static class ConstructorModifiersContext extends ParserRuleContext {
		public List<ConstructorModifierContext> constructorModifier() {
			return getRuleContexts(ConstructorModifierContext.class);
		}
		public ConstructorModifierContext constructorModifier(int i) {
			return getRuleContext(ConstructorModifierContext.class,i);
		}
		public ConstructorModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorModifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstructorModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstructorModifiers(this);
		}
	}

	public final ConstructorModifiersContext constructorModifiers() throws RecognitionException {
		ConstructorModifiersContext _localctx = new ConstructorModifiersContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_constructorModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC))) != 0) || _la==AT) {
				{
				{
				setState(1113);
				constructorModifier();
				}
				}
				setState(1118);
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

	public static class ConstructorModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode PROTECTED() { return getToken(Java8Parser.PROTECTED, 0); }
		public TerminalNode PRIVATE() { return getToken(Java8Parser.PRIVATE, 0); }
		public ConstructorModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstructorModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstructorModifier(this);
		}
	}

	public final ConstructorModifierContext constructorModifier() throws RecognitionException {
		ConstructorModifierContext _localctx = new ConstructorModifierContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_constructorModifier);
		try {
			setState(1123);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1119);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1120);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1121);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1122);
				match(PRIVATE);
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

	public static class ConstructorDeclaratorContext extends ParserRuleContext {
		public SimpleTypeNameContext simpleTypeName() {
			return getRuleContext(SimpleTypeNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public ConstructorDeclaratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorDeclarator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstructorDeclarator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstructorDeclarator(this);
		}
	}

	public final ConstructorDeclaratorContext constructorDeclarator() throws RecognitionException {
		ConstructorDeclaratorContext _localctx = new ConstructorDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_constructorDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1125);
				typeParameters();
				}
			}

			setState(1128);
			simpleTypeName();
			setState(1129);
			match(LPAREN);
			setState(1131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1130);
				formalParameterList();
				}
			}

			setState(1133);
			match(RPAREN);
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

	public static class SimpleTypeNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public SimpleTypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleTypeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSimpleTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSimpleTypeName(this);
		}
	}

	public final SimpleTypeNameContext simpleTypeName() throws RecognitionException {
		SimpleTypeNameContext _localctx = new SimpleTypeNameContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_simpleTypeName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1135);
			match(Identifier);
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

	public static class ConstructorBodyContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public ExplicitConstructorInvocationContext explicitConstructorInvocation() {
			return getRuleContext(ExplicitConstructorInvocationContext.class,0);
		}
		public BlockStatementsContext blockStatements() {
			return getRuleContext(BlockStatementsContext.class,0);
		}
		public ConstructorBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constructorBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstructorBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstructorBody(this);
		}
	}

	public final ConstructorBodyContext constructorBody() throws RecognitionException {
		ConstructorBodyContext _localctx = new ConstructorBodyContext(_ctx, getState());
		enterRule(_localctx, 190, RULE_constructorBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1137);
			match(LBRACE);
			setState(1139);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				{
				setState(1138);
				explicitConstructorInvocation();
				}
				break;
			}
			setState(1142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (SEMI - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1141);
				blockStatements();
				}
			}

			setState(1144);
			match(RBRACE);
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

	public static class ExplicitConstructorInvocationContext extends ParserRuleContext {
		public TerminalNode THIS() { return getToken(Java8Parser.THIS, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ExplicitConstructorInvocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_explicitConstructorInvocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExplicitConstructorInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExplicitConstructorInvocation(this);
		}
	}

	public final ExplicitConstructorInvocationContext explicitConstructorInvocation() throws RecognitionException {
		ExplicitConstructorInvocationContext _localctx = new ExplicitConstructorInvocationContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_explicitConstructorInvocation);
		int _la;
		try {
			setState(1192);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1147);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1146);
					typeArguments();
					}
				}

				setState(1149);
				match(THIS);
				setState(1150);
				match(LPAREN);
				setState(1152);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(1151);
					argumentList();
					}
				}

				setState(1154);
				match(RPAREN);
				setState(1155);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1157);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1156);
					typeArguments();
					}
				}

				setState(1159);
				match(SUPER);
				setState(1160);
				match(LPAREN);
				setState(1162);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(1161);
					argumentList();
					}
				}

				setState(1164);
				match(RPAREN);
				setState(1165);
				match(SEMI);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1166);
				expressionName();
				setState(1167);
				match(DOT);
				setState(1169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1168);
					typeArguments();
					}
				}

				setState(1171);
				match(SUPER);
				setState(1172);
				match(LPAREN);
				setState(1174);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(1173);
					argumentList();
					}
				}

				setState(1176);
				match(RPAREN);
				setState(1177);
				match(SEMI);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1179);
				primary();
				setState(1180);
				match(DOT);
				setState(1182);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1181);
					typeArguments();
					}
				}

				setState(1184);
				match(SUPER);
				setState(1185);
				match(LPAREN);
				setState(1187);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(1186);
					argumentList();
					}
				}

				setState(1189);
				match(RPAREN);
				setState(1190);
				match(SEMI);
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

	public static class EnumDeclarationContext extends ParserRuleContext {
		public ClassModifiersContext classModifiers() {
			return getRuleContext(ClassModifiersContext.class,0);
		}
		public TerminalNode ENUM() { return getToken(Java8Parser.ENUM, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public EnumBodyContext enumBody() {
			return getRuleContext(EnumBodyContext.class,0);
		}
		public SuperinterfacesContext superinterfaces() {
			return getRuleContext(SuperinterfacesContext.class,0);
		}
		public EnumDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnumDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnumDeclaration(this);
		}
	}

	public final EnumDeclarationContext enumDeclaration() throws RecognitionException {
		EnumDeclarationContext _localctx = new EnumDeclarationContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_enumDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1194);
			classModifiers();
			setState(1195);
			match(ENUM);
			setState(1196);
			match(Identifier);
			setState(1198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPLEMENTS) {
				{
				setState(1197);
				superinterfaces();
				}
			}

			setState(1200);
			enumBody();
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

	public static class EnumBodyContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public EnumConstantListContext enumConstantList() {
			return getRuleContext(EnumConstantListContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(Java8Parser.COMMA, 0); }
		public EnumBodyDeclarationsContext enumBodyDeclarations() {
			return getRuleContext(EnumBodyDeclarationsContext.class,0);
		}
		public EnumBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnumBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnumBody(this);
		}
	}

	public final EnumBodyContext enumBody() throws RecognitionException {
		EnumBodyContext _localctx = new EnumBodyContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_enumBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1202);
			match(LBRACE);
			setState(1204);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier || _la==AT) {
				{
				setState(1203);
				enumConstantList();
				}
			}

			setState(1207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1206);
				match(COMMA);
				}
			}

			setState(1210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(1209);
				enumBodyDeclarations();
				}
			}

			setState(1212);
			match(RBRACE);
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

	public static class EnumConstantListContext extends ParserRuleContext {
		public List<EnumConstantContext> enumConstant() {
			return getRuleContexts(EnumConstantContext.class);
		}
		public EnumConstantContext enumConstant(int i) {
			return getRuleContext(EnumConstantContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public EnumConstantListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumConstantList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnumConstantList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnumConstantList(this);
		}
	}

	public final EnumConstantListContext enumConstantList() throws RecognitionException {
		EnumConstantListContext _localctx = new EnumConstantListContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_enumConstantList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1214);
			enumConstant();
			setState(1219);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,109,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1215);
					match(COMMA);
					setState(1216);
					enumConstant();
					}
					} 
				}
				setState(1221);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,109,_ctx);
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

	public static class EnumConstantContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public List<EnumConstantModifierContext> enumConstantModifier() {
			return getRuleContexts(EnumConstantModifierContext.class);
		}
		public EnumConstantModifierContext enumConstantModifier(int i) {
			return getRuleContext(EnumConstantModifierContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ClassBodyContext classBody() {
			return getRuleContext(ClassBodyContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public EnumConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumConstant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnumConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnumConstant(this);
		}
	}

	public final EnumConstantContext enumConstant() throws RecognitionException {
		EnumConstantContext _localctx = new EnumConstantContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_enumConstant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1225);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1222);
				enumConstantModifier();
				}
				}
				setState(1227);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1228);
			match(Identifier);
			setState(1234);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(1229);
				match(LPAREN);
				setState(1231);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(1230);
					argumentList();
					}
				}

				setState(1233);
				match(RPAREN);
				}
			}

			setState(1237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACE) {
				{
				setState(1236);
				classBody();
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

	public static class EnumConstantModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public EnumConstantModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumConstantModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnumConstantModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnumConstantModifier(this);
		}
	}

	public final EnumConstantModifierContext enumConstantModifier() throws RecognitionException {
		EnumConstantModifierContext _localctx = new EnumConstantModifierContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_enumConstantModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1239);
			annotation();
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

	public static class EnumBodyDeclarationsContext extends ParserRuleContext {
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public List<ClassBodyDeclarationContext> classBodyDeclaration() {
			return getRuleContexts(ClassBodyDeclarationContext.class);
		}
		public ClassBodyDeclarationContext classBodyDeclaration(int i) {
			return getRuleContext(ClassBodyDeclarationContext.class,i);
		}
		public EnumBodyDeclarationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumBodyDeclarations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnumBodyDeclarations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnumBodyDeclarations(this);
		}
	}

	public final EnumBodyDeclarationsContext enumBodyDeclarations() throws RecognitionException {
		EnumBodyDeclarationsContext _localctx = new EnumBodyDeclarationsContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_enumBodyDeclarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1241);
			match(SEMI);
			setState(1245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE) | (1L << LBRACE))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (SEMI - 65)) | (1L << (LT - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				{
				setState(1242);
				classBodyDeclaration();
				}
				}
				setState(1247);
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

	public static class InterfaceDeclarationContext extends ParserRuleContext {
		public NormalInterfaceDeclarationContext normalInterfaceDeclaration() {
			return getRuleContext(NormalInterfaceDeclarationContext.class,0);
		}
		public AnnotationTypeDeclarationContext annotationTypeDeclaration() {
			return getRuleContext(AnnotationTypeDeclarationContext.class,0);
		}
		public InterfaceDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceDeclaration(this);
		}
	}

	public final InterfaceDeclarationContext interfaceDeclaration() throws RecognitionException {
		InterfaceDeclarationContext _localctx = new InterfaceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_interfaceDeclaration);
		try {
			setState(1250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1248);
				normalInterfaceDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1249);
				annotationTypeDeclaration();
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

	public static class NormalInterfaceDeclarationContext extends ParserRuleContext {
		public InterfaceModifiersContext interfaceModifiers() {
			return getRuleContext(InterfaceModifiersContext.class,0);
		}
		public TerminalNode INTERFACE() { return getToken(Java8Parser.INTERFACE, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public InterfaceBodyContext interfaceBody() {
			return getRuleContext(InterfaceBodyContext.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public ExtendsInterfacesContext extendsInterfaces() {
			return getRuleContext(ExtendsInterfacesContext.class,0);
		}
		public NormalInterfaceDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalInterfaceDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterNormalInterfaceDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitNormalInterfaceDeclaration(this);
		}
	}

	public final NormalInterfaceDeclarationContext normalInterfaceDeclaration() throws RecognitionException {
		NormalInterfaceDeclarationContext _localctx = new NormalInterfaceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 208, RULE_normalInterfaceDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1252);
			interfaceModifiers();
			setState(1253);
			match(INTERFACE);
			setState(1254);
			match(Identifier);
			setState(1256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1255);
				typeParameters();
				}
			}

			setState(1259);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(1258);
				extendsInterfaces();
				}
			}

			setState(1261);
			interfaceBody();
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

	public static class InterfaceModifiersContext extends ParserRuleContext {
		public List<InterfaceModifierContext> interfaceModifier() {
			return getRuleContexts(InterfaceModifierContext.class);
		}
		public InterfaceModifierContext interfaceModifier(int i) {
			return getRuleContext(InterfaceModifierContext.class,i);
		}
		public InterfaceModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceModifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceModifiers(this);
		}
	}

	public final InterfaceModifiersContext interfaceModifiers() throws RecognitionException {
		InterfaceModifiersContext _localctx = new InterfaceModifiersContext(_ctx, getState());
		enterRule(_localctx, 210, RULE_interfaceModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(1263);
				interfaceModifier();
				}
				}
				setState(1268);
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

	public static class InterfaceModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode PROTECTED() { return getToken(Java8Parser.PROTECTED, 0); }
		public TerminalNode PRIVATE() { return getToken(Java8Parser.PRIVATE, 0); }
		public TerminalNode ABSTRACT() { return getToken(Java8Parser.ABSTRACT, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TerminalNode STRICTFP() { return getToken(Java8Parser.STRICTFP, 0); }
		public InterfaceModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceModifier(this);
		}
	}

	public final InterfaceModifierContext interfaceModifier() throws RecognitionException {
		InterfaceModifierContext _localctx = new InterfaceModifierContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_interfaceModifier);
		try {
			setState(1276);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1269);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1270);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1271);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1272);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1273);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(1274);
				match(STATIC);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 7);
				{
				setState(1275);
				match(STRICTFP);
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

	public static class ExtendsInterfacesContext extends ParserRuleContext {
		public TerminalNode EXTENDS() { return getToken(Java8Parser.EXTENDS, 0); }
		public InterfaceTypeListContext interfaceTypeList() {
			return getRuleContext(InterfaceTypeListContext.class,0);
		}
		public ExtendsInterfacesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extendsInterfaces; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExtendsInterfaces(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExtendsInterfaces(this);
		}
	}

	public final ExtendsInterfacesContext extendsInterfaces() throws RecognitionException {
		ExtendsInterfacesContext _localctx = new ExtendsInterfacesContext(_ctx, getState());
		enterRule(_localctx, 214, RULE_extendsInterfaces);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1278);
			match(EXTENDS);
			setState(1279);
			interfaceTypeList();
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

	public static class InterfaceBodyContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public List<InterfaceMemberDeclarationContext> interfaceMemberDeclaration() {
			return getRuleContexts(InterfaceMemberDeclarationContext.class);
		}
		public InterfaceMemberDeclarationContext interfaceMemberDeclaration(int i) {
			return getRuleContext(InterfaceMemberDeclarationContext.class,i);
		}
		public InterfaceBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceBody(this);
		}
	}

	public final InterfaceBodyContext interfaceBody() throws RecognitionException {
		InterfaceBodyContext _localctx = new InterfaceBodyContext(_ctx, getState());
		enterRule(_localctx, 216, RULE_interfaceBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1281);
			match(LBRACE);
			setState(1285);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DEFAULT) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (SEMI - 65)) | (1L << (LT - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				{
				setState(1282);
				interfaceMemberDeclaration();
				}
				}
				setState(1287);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1288);
			match(RBRACE);
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

	public static class InterfaceMemberDeclarationContext extends ParserRuleContext {
		public ConstantDeclarationContext constantDeclaration() {
			return getRuleContext(ConstantDeclarationContext.class,0);
		}
		public InterfaceMethodDeclarationContext interfaceMethodDeclaration() {
			return getRuleContext(InterfaceMethodDeclarationContext.class,0);
		}
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public InterfaceDeclarationContext interfaceDeclaration() {
			return getRuleContext(InterfaceDeclarationContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public InterfaceMemberDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceMemberDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceMemberDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceMemberDeclaration(this);
		}
	}

	public final InterfaceMemberDeclarationContext interfaceMemberDeclaration() throws RecognitionException {
		InterfaceMemberDeclarationContext _localctx = new InterfaceMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_interfaceMemberDeclaration);
		try {
			setState(1295);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,121,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1290);
				constantDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1291);
				interfaceMethodDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1292);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1293);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1294);
				match(SEMI);
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

	public static class ConstantDeclarationContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorListContext variableDeclaratorList() {
			return getRuleContext(VariableDeclaratorListContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public List<ConstantModifierContext> constantModifier() {
			return getRuleContexts(ConstantModifierContext.class);
		}
		public ConstantModifierContext constantModifier(int i) {
			return getRuleContext(ConstantModifierContext.class,i);
		}
		public ConstantDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstantDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstantDeclaration(this);
		}
	}

	public final ConstantDeclarationContext constantDeclaration() throws RecognitionException {
		ConstantDeclarationContext _localctx = new ConstantDeclarationContext(_ctx, getState());
		enterRule(_localctx, 220, RULE_constantDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1300);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FINAL) | (1L << PUBLIC) | (1L << STATIC))) != 0) || _la==AT) {
				{
				{
				setState(1297);
				constantModifier();
				}
				}
				setState(1302);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1303);
			unannType();
			setState(1304);
			variableDeclaratorList();
			setState(1305);
			match(SEMI);
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

	public static class ConstantModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TerminalNode FINAL() { return getToken(Java8Parser.FINAL, 0); }
		public ConstantModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstantModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstantModifier(this);
		}
	}

	public final ConstantModifierContext constantModifier() throws RecognitionException {
		ConstantModifierContext _localctx = new ConstantModifierContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_constantModifier);
		try {
			setState(1311);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1307);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1308);
				match(PUBLIC);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 3);
				{
				setState(1309);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(1310);
				match(FINAL);
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

	public static class InterfaceMethodDeclarationContext extends ParserRuleContext {
		public MethodHeaderContext methodHeader() {
			return getRuleContext(MethodHeaderContext.class,0);
		}
		public MethodBodyContext methodBody() {
			return getRuleContext(MethodBodyContext.class,0);
		}
		public List<InterfaceMethodModifierContext> interfaceMethodModifier() {
			return getRuleContexts(InterfaceMethodModifierContext.class);
		}
		public InterfaceMethodModifierContext interfaceMethodModifier(int i) {
			return getRuleContext(InterfaceMethodModifierContext.class,i);
		}
		public InterfaceMethodDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceMethodDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceMethodDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceMethodDeclaration(this);
		}
	}

	public final InterfaceMethodDeclarationContext interfaceMethodDeclaration() throws RecognitionException {
		InterfaceMethodDeclarationContext _localctx = new InterfaceMethodDeclarationContext(_ctx, getState());
		enterRule(_localctx, 224, RULE_interfaceMethodDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1316);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << DEFAULT) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(1313);
				interfaceMethodModifier();
				}
				}
				setState(1318);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1319);
			methodHeader();
			setState(1320);
			methodBody();
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

	public static class InterfaceMethodModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode ABSTRACT() { return getToken(Java8Parser.ABSTRACT, 0); }
		public TerminalNode DEFAULT() { return getToken(Java8Parser.DEFAULT, 0); }
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
		public TerminalNode STRICTFP() { return getToken(Java8Parser.STRICTFP, 0); }
		public InterfaceMethodModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceMethodModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInterfaceMethodModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInterfaceMethodModifier(this);
		}
	}

	public final InterfaceMethodModifierContext interfaceMethodModifier() throws RecognitionException {
		InterfaceMethodModifierContext _localctx = new InterfaceMethodModifierContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_interfaceMethodModifier);
		try {
			setState(1328);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1322);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1323);
				match(PUBLIC);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1324);
				match(ABSTRACT);
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 4);
				{
				setState(1325);
				match(DEFAULT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 5);
				{
				setState(1326);
				match(STATIC);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 6);
				{
				setState(1327);
				match(STRICTFP);
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

	public static class AnnotationTypeDeclarationContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(Java8Parser.AT, 0); }
		public TerminalNode INTERFACE() { return getToken(Java8Parser.INTERFACE, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public AnnotationTypeBodyContext annotationTypeBody() {
			return getRuleContext(AnnotationTypeBodyContext.class,0);
		}
		public List<InterfaceModifierContext> interfaceModifier() {
			return getRuleContexts(InterfaceModifierContext.class);
		}
		public InterfaceModifierContext interfaceModifier(int i) {
			return getRuleContext(InterfaceModifierContext.class,i);
		}
		public AnnotationTypeDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotationTypeDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotationTypeDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotationTypeDeclaration(this);
		}
	}

	public final AnnotationTypeDeclarationContext annotationTypeDeclaration() throws RecognitionException {
		AnnotationTypeDeclarationContext _localctx = new AnnotationTypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 228, RULE_annotationTypeDeclaration);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1333);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1330);
					interfaceModifier();
					}
					} 
				}
				setState(1335);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,126,_ctx);
			}
			setState(1336);
			match(AT);
			setState(1337);
			match(INTERFACE);
			setState(1338);
			match(Identifier);
			setState(1339);
			annotationTypeBody();
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

	public static class AnnotationTypeBodyContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public List<AnnotationTypeMemberDeclarationContext> annotationTypeMemberDeclaration() {
			return getRuleContexts(AnnotationTypeMemberDeclarationContext.class);
		}
		public AnnotationTypeMemberDeclarationContext annotationTypeMemberDeclaration(int i) {
			return getRuleContext(AnnotationTypeMemberDeclarationContext.class,i);
		}
		public AnnotationTypeBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotationTypeBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotationTypeBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotationTypeBody(this);
		}
	}

	public final AnnotationTypeBodyContext annotationTypeBody() throws RecognitionException {
		AnnotationTypeBodyContext _localctx = new AnnotationTypeBodyContext(_ctx, getState());
		enterRule(_localctx, 230, RULE_annotationTypeBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1341);
			match(LBRACE);
			setState(1345);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (SEMI - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				{
				setState(1342);
				annotationTypeMemberDeclaration();
				}
				}
				setState(1347);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1348);
			match(RBRACE);
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

	public static class AnnotationTypeMemberDeclarationContext extends ParserRuleContext {
		public AnnotationTypeElementDeclarationContext annotationTypeElementDeclaration() {
			return getRuleContext(AnnotationTypeElementDeclarationContext.class,0);
		}
		public ConstantDeclarationContext constantDeclaration() {
			return getRuleContext(ConstantDeclarationContext.class,0);
		}
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public InterfaceDeclarationContext interfaceDeclaration() {
			return getRuleContext(InterfaceDeclarationContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public AnnotationTypeMemberDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotationTypeMemberDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotationTypeMemberDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotationTypeMemberDeclaration(this);
		}
	}

	public final AnnotationTypeMemberDeclarationContext annotationTypeMemberDeclaration() throws RecognitionException {
		AnnotationTypeMemberDeclarationContext _localctx = new AnnotationTypeMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_annotationTypeMemberDeclaration);
		try {
			setState(1355);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,128,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1350);
				annotationTypeElementDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1351);
				constantDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1352);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1353);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1354);
				match(SEMI);
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

	public static class AnnotationTypeElementDeclarationContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public List<AnnotationTypeElementModifierContext> annotationTypeElementModifier() {
			return getRuleContexts(AnnotationTypeElementModifierContext.class);
		}
		public AnnotationTypeElementModifierContext annotationTypeElementModifier(int i) {
			return getRuleContext(AnnotationTypeElementModifierContext.class,i);
		}
		public DimsContext dims() {
			return getRuleContext(DimsContext.class,0);
		}
		public DefaultValueContext defaultValue() {
			return getRuleContext(DefaultValueContext.class,0);
		}
		public AnnotationTypeElementDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotationTypeElementDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotationTypeElementDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotationTypeElementDeclaration(this);
		}
	}

	public final AnnotationTypeElementDeclarationContext annotationTypeElementDeclaration() throws RecognitionException {
		AnnotationTypeElementDeclarationContext _localctx = new AnnotationTypeElementDeclarationContext(_ctx, getState());
		enterRule(_localctx, 234, RULE_annotationTypeElementDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1360);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ABSTRACT || _la==PUBLIC || _la==AT) {
				{
				{
				setState(1357);
				annotationTypeElementModifier();
				}
				}
				setState(1362);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1363);
			unannType();
			setState(1364);
			match(Identifier);
			setState(1365);
			match(LPAREN);
			setState(1366);
			match(RPAREN);
			setState(1368);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1 || _la==AT) {
				{
				setState(1367);
				dims();
				}
			}

			setState(1371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DEFAULT) {
				{
				setState(1370);
				defaultValue();
				}
			}

			setState(1373);
			match(SEMI);
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

	public static class AnnotationTypeElementModifierContext extends ParserRuleContext {
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public TerminalNode PUBLIC() { return getToken(Java8Parser.PUBLIC, 0); }
		public TerminalNode ABSTRACT() { return getToken(Java8Parser.ABSTRACT, 0); }
		public AnnotationTypeElementModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotationTypeElementModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotationTypeElementModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotationTypeElementModifier(this);
		}
	}

	public final AnnotationTypeElementModifierContext annotationTypeElementModifier() throws RecognitionException {
		AnnotationTypeElementModifierContext _localctx = new AnnotationTypeElementModifierContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_annotationTypeElementModifier);
		try {
			setState(1378);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1375);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1376);
				match(PUBLIC);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1377);
				match(ABSTRACT);
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

	public static class DefaultValueContext extends ParserRuleContext {
		public TerminalNode DEFAULT() { return getToken(Java8Parser.DEFAULT, 0); }
		public ElementValueContext elementValue() {
			return getRuleContext(ElementValueContext.class,0);
		}
		public DefaultValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterDefaultValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitDefaultValue(this);
		}
	}

	public final DefaultValueContext defaultValue() throws RecognitionException {
		DefaultValueContext _localctx = new DefaultValueContext(_ctx, getState());
		enterRule(_localctx, 238, RULE_defaultValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1380);
			match(DEFAULT);
			setState(1381);
			elementValue();
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

	public static class AnnotationContext extends ParserRuleContext {
		public NormalAnnotationContext normalAnnotation() {
			return getRuleContext(NormalAnnotationContext.class,0);
		}
		public MarkerAnnotationContext markerAnnotation() {
			return getRuleContext(MarkerAnnotationContext.class,0);
		}
		public SingleElementAnnotationContext singleElementAnnotation() {
			return getRuleContext(SingleElementAnnotationContext.class,0);
		}
		public AnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotation(this);
		}
	}

	public final AnnotationContext annotation() throws RecognitionException {
		AnnotationContext _localctx = new AnnotationContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_annotation);
		try {
			setState(1386);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,133,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1383);
				normalAnnotation();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1384);
				markerAnnotation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1385);
				singleElementAnnotation();
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

	public static class AnnotationIdentifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public AnnotationIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotationIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotationIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotationIdentifier(this);
		}
	}

	public final AnnotationIdentifierContext annotationIdentifier() throws RecognitionException {
		AnnotationIdentifierContext _localctx = new AnnotationIdentifierContext(_ctx, getState());
		enterRule(_localctx, 242, RULE_annotationIdentifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1391);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1388);
				annotation();
				}
				}
				setState(1393);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1394);
			match(Identifier);
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

	public static class AnnotationDimContext extends ParserRuleContext {
		public SquareBracketsContext squareBrackets() {
			return getRuleContext(SquareBracketsContext.class,0);
		}
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public AnnotationDimContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotationDim; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAnnotationDim(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAnnotationDim(this);
		}
	}

	public final AnnotationDimContext annotationDim() throws RecognitionException {
		AnnotationDimContext _localctx = new AnnotationDimContext(_ctx, getState());
		enterRule(_localctx, 244, RULE_annotationDim);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1399);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1396);
				annotation();
				}
				}
				setState(1401);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1402);
			squareBrackets();
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

	public static class NormalAnnotationContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(Java8Parser.AT, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ElementValuePairListContext elementValuePairList() {
			return getRuleContext(ElementValuePairListContext.class,0);
		}
		public NormalAnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normalAnnotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterNormalAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitNormalAnnotation(this);
		}
	}

	public final NormalAnnotationContext normalAnnotation() throws RecognitionException {
		NormalAnnotationContext _localctx = new NormalAnnotationContext(_ctx, getState());
		enterRule(_localctx, 246, RULE_normalAnnotation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1404);
			match(AT);
			setState(1405);
			typeName();
			setState(1406);
			match(LPAREN);
			setState(1408);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1407);
				elementValuePairList();
				}
			}

			setState(1410);
			match(RPAREN);
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

	public static class ElementValuePairListContext extends ParserRuleContext {
		public List<ElementValuePairContext> elementValuePair() {
			return getRuleContexts(ElementValuePairContext.class);
		}
		public ElementValuePairContext elementValuePair(int i) {
			return getRuleContext(ElementValuePairContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public ElementValuePairListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementValuePairList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterElementValuePairList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitElementValuePairList(this);
		}
	}

	public final ElementValuePairListContext elementValuePairList() throws RecognitionException {
		ElementValuePairListContext _localctx = new ElementValuePairListContext(_ctx, getState());
		enterRule(_localctx, 248, RULE_elementValuePairList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1412);
			elementValuePair();
			setState(1417);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1413);
				match(COMMA);
				setState(1414);
				elementValuePair();
				}
				}
				setState(1419);
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

	public static class ElementValuePairContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode ASSIGN() { return getToken(Java8Parser.ASSIGN, 0); }
		public ElementValueContext elementValue() {
			return getRuleContext(ElementValueContext.class,0);
		}
		public ElementValuePairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementValuePair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterElementValuePair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitElementValuePair(this);
		}
	}

	public final ElementValuePairContext elementValuePair() throws RecognitionException {
		ElementValuePairContext _localctx = new ElementValuePairContext(_ctx, getState());
		enterRule(_localctx, 250, RULE_elementValuePair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1420);
			match(Identifier);
			setState(1421);
			match(ASSIGN);
			setState(1422);
			elementValue();
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

	public static class ElementValueContext extends ParserRuleContext {
		public ConditionalExpressionContext conditionalExpression() {
			return getRuleContext(ConditionalExpressionContext.class,0);
		}
		public ElementValueArrayInitializerContext elementValueArrayInitializer() {
			return getRuleContext(ElementValueArrayInitializerContext.class,0);
		}
		public AnnotationContext annotation() {
			return getRuleContext(AnnotationContext.class,0);
		}
		public ElementValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterElementValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitElementValue(this);
		}
	}

	public final ElementValueContext elementValue() throws RecognitionException {
		ElementValueContext _localctx = new ElementValueContext(_ctx, getState());
		enterRule(_localctx, 252, RULE_elementValue);
		try {
			setState(1427);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,138,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1424);
				conditionalExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1425);
				elementValueArrayInitializer();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1426);
				annotation();
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

	public static class ElementValueArrayInitializerContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public ElementValueListContext elementValueList() {
			return getRuleContext(ElementValueListContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(Java8Parser.COMMA, 0); }
		public ElementValueArrayInitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementValueArrayInitializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterElementValueArrayInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitElementValueArrayInitializer(this);
		}
	}

	public final ElementValueArrayInitializerContext elementValueArrayInitializer() throws RecognitionException {
		ElementValueArrayInitializerContext _localctx = new ElementValueArrayInitializerContext(_ctx, getState());
		enterRule(_localctx, 254, RULE_elementValueArrayInitializer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1429);
			match(LBRACE);
			setState(1431);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				setState(1430);
				elementValueList();
				}
			}

			setState(1434);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1433);
				match(COMMA);
				}
			}

			setState(1436);
			match(RBRACE);
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

	public static class ElementValueListContext extends ParserRuleContext {
		public List<ElementValueContext> elementValue() {
			return getRuleContexts(ElementValueContext.class);
		}
		public ElementValueContext elementValue(int i) {
			return getRuleContext(ElementValueContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public ElementValueListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementValueList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterElementValueList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitElementValueList(this);
		}
	}

	public final ElementValueListContext elementValueList() throws RecognitionException {
		ElementValueListContext _localctx = new ElementValueListContext(_ctx, getState());
		enterRule(_localctx, 256, RULE_elementValueList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1438);
			elementValue();
			setState(1443);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,141,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1439);
					match(COMMA);
					setState(1440);
					elementValue();
					}
					} 
				}
				setState(1445);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,141,_ctx);
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

	public static class MarkerAnnotationContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(Java8Parser.AT, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public MarkerAnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_markerAnnotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMarkerAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMarkerAnnotation(this);
		}
	}

	public final MarkerAnnotationContext markerAnnotation() throws RecognitionException {
		MarkerAnnotationContext _localctx = new MarkerAnnotationContext(_ctx, getState());
		enterRule(_localctx, 258, RULE_markerAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1446);
			match(AT);
			setState(1447);
			typeName();
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

	public static class SingleElementAnnotationContext extends ParserRuleContext {
		public TerminalNode AT() { return getToken(Java8Parser.AT, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ElementValueContext elementValue() {
			return getRuleContext(ElementValueContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public SingleElementAnnotationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleElementAnnotation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSingleElementAnnotation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSingleElementAnnotation(this);
		}
	}

	public final SingleElementAnnotationContext singleElementAnnotation() throws RecognitionException {
		SingleElementAnnotationContext _localctx = new SingleElementAnnotationContext(_ctx, getState());
		enterRule(_localctx, 260, RULE_singleElementAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1449);
			match(AT);
			setState(1450);
			typeName();
			setState(1451);
			match(LPAREN);
			setState(1452);
			elementValue();
			setState(1453);
			match(RPAREN);
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

	public static class ArrayInitializerContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public VariableInitializerListContext variableInitializerList() {
			return getRuleContext(VariableInitializerListContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(Java8Parser.COMMA, 0); }
		public ArrayInitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayInitializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterArrayInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitArrayInitializer(this);
		}
	}

	public final ArrayInitializerContext arrayInitializer() throws RecognitionException {
		ArrayInitializerContext _localctx = new ArrayInitializerContext(_ctx, getState());
		enterRule(_localctx, 262, RULE_arrayInitializer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1455);
			match(LBRACE);
			setState(1457);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				setState(1456);
				variableInitializerList();
				}
			}

			setState(1460);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1459);
				match(COMMA);
				}
			}

			setState(1462);
			match(RBRACE);
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

	public static class VariableInitializerListContext extends ParserRuleContext {
		public List<VariableInitializerContext> variableInitializer() {
			return getRuleContexts(VariableInitializerContext.class);
		}
		public VariableInitializerContext variableInitializer(int i) {
			return getRuleContext(VariableInitializerContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public VariableInitializerListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableInitializerList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterVariableInitializerList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitVariableInitializerList(this);
		}
	}

	public final VariableInitializerListContext variableInitializerList() throws RecognitionException {
		VariableInitializerListContext _localctx = new VariableInitializerListContext(_ctx, getState());
		enterRule(_localctx, 264, RULE_variableInitializerList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1464);
			variableInitializer();
			setState(1469);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,144,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1465);
					match(COMMA);
					setState(1466);
					variableInitializer();
					}
					} 
				}
				setState(1471);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,144,_ctx);
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

	public static class BlockContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public BlockStatementsContext blockStatements() {
			return getRuleContext(BlockStatementsContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 266, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1472);
			match(LBRACE);
			setState(1474);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (SEMI - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1473);
				blockStatements();
				}
			}

			setState(1476);
			match(RBRACE);
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

	public static class BlockStatementsContext extends ParserRuleContext {
		public List<BlockStatementContext> blockStatement() {
			return getRuleContexts(BlockStatementContext.class);
		}
		public BlockStatementContext blockStatement(int i) {
			return getRuleContext(BlockStatementContext.class,i);
		}
		public BlockStatementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockStatements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterBlockStatements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitBlockStatements(this);
		}
	}

	public final BlockStatementsContext blockStatements() throws RecognitionException {
		BlockStatementsContext _localctx = new BlockStatementsContext(_ctx, getState());
		enterRule(_localctx, 268, RULE_blockStatements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1478);
			blockStatement();
			setState(1482);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (SEMI - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				{
				setState(1479);
				blockStatement();
				}
				}
				setState(1484);
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

	public static class BlockStatementContext extends ParserRuleContext {
		public LocalVariableDeclarationStatementContext localVariableDeclarationStatement() {
			return getRuleContext(LocalVariableDeclarationStatementContext.class,0);
		}
		public ClassDeclarationContext classDeclaration() {
			return getRuleContext(ClassDeclarationContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public BlockStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterBlockStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitBlockStatement(this);
		}
	}

	public final BlockStatementContext blockStatement() throws RecognitionException {
		BlockStatementContext _localctx = new BlockStatementContext(_ctx, getState());
		enterRule(_localctx, 270, RULE_blockStatement);
		try {
			setState(1488);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1485);
				localVariableDeclarationStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1486);
				classDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1487);
				statement();
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

	public static class LocalVariableDeclarationStatementContext extends ParserRuleContext {
		public LocalVariableDeclarationContext localVariableDeclaration() {
			return getRuleContext(LocalVariableDeclarationContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public LocalVariableDeclarationStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localVariableDeclarationStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLocalVariableDeclarationStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLocalVariableDeclarationStatement(this);
		}
	}

	public final LocalVariableDeclarationStatementContext localVariableDeclarationStatement() throws RecognitionException {
		LocalVariableDeclarationStatementContext _localctx = new LocalVariableDeclarationStatementContext(_ctx, getState());
		enterRule(_localctx, 272, RULE_localVariableDeclarationStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1490);
			localVariableDeclaration();
			setState(1491);
			match(SEMI);
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

	public static class LocalVariableDeclarationContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorListContext variableDeclaratorList() {
			return getRuleContext(VariableDeclaratorListContext.class,0);
		}
		public List<VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public LocalVariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localVariableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLocalVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLocalVariableDeclaration(this);
		}
	}

	public final LocalVariableDeclarationContext localVariableDeclaration() throws RecognitionException {
		LocalVariableDeclarationContext _localctx = new LocalVariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 274, RULE_localVariableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1496);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1493);
				variableModifier();
				}
				}
				setState(1498);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1499);
			unannType();
			setState(1500);
			variableDeclaratorList();
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

	public static class StatementContext extends ParserRuleContext {
		public StatementWithoutTrailingSubstatementContext statementWithoutTrailingSubstatement() {
			return getRuleContext(StatementWithoutTrailingSubstatementContext.class,0);
		}
		public LabeledStatementContext labeledStatement() {
			return getRuleContext(LabeledStatementContext.class,0);
		}
		public IfThenStatementContext ifThenStatement() {
			return getRuleContext(IfThenStatementContext.class,0);
		}
		public IfThenElseStatementContext ifThenElseStatement() {
			return getRuleContext(IfThenElseStatementContext.class,0);
		}
		public WhileStatementContext whileStatement() {
			return getRuleContext(WhileStatementContext.class,0);
		}
		public ForStatementContext forStatement() {
			return getRuleContext(ForStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 276, RULE_statement);
		try {
			setState(1508);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,149,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1502);
				statementWithoutTrailingSubstatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1503);
				labeledStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1504);
				ifThenStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1505);
				ifThenElseStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1506);
				whileStatement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1507);
				forStatement();
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

	public static class StatementNoShortIfContext extends ParserRuleContext {
		public StatementWithoutTrailingSubstatementContext statementWithoutTrailingSubstatement() {
			return getRuleContext(StatementWithoutTrailingSubstatementContext.class,0);
		}
		public LabeledStatementNoShortIfContext labeledStatementNoShortIf() {
			return getRuleContext(LabeledStatementNoShortIfContext.class,0);
		}
		public IfThenElseStatementNoShortIfContext ifThenElseStatementNoShortIf() {
			return getRuleContext(IfThenElseStatementNoShortIfContext.class,0);
		}
		public WhileStatementNoShortIfContext whileStatementNoShortIf() {
			return getRuleContext(WhileStatementNoShortIfContext.class,0);
		}
		public ForStatementNoShortIfContext forStatementNoShortIf() {
			return getRuleContext(ForStatementNoShortIfContext.class,0);
		}
		public StatementNoShortIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementNoShortIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterStatementNoShortIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitStatementNoShortIf(this);
		}
	}

	public final StatementNoShortIfContext statementNoShortIf() throws RecognitionException {
		StatementNoShortIfContext _localctx = new StatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 278, RULE_statementNoShortIf);
		try {
			setState(1515);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,150,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1510);
				statementWithoutTrailingSubstatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1511);
				labeledStatementNoShortIf();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1512);
				ifThenElseStatementNoShortIf();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1513);
				whileStatementNoShortIf();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1514);
				forStatementNoShortIf();
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

	public static class StatementWithoutTrailingSubstatementContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public EmptyStatementContext emptyStatement() {
			return getRuleContext(EmptyStatementContext.class,0);
		}
		public ExpressionStatementContext expressionStatement() {
			return getRuleContext(ExpressionStatementContext.class,0);
		}
		public AssertStatementContext assertStatement() {
			return getRuleContext(AssertStatementContext.class,0);
		}
		public SwitchStatementContext switchStatement() {
			return getRuleContext(SwitchStatementContext.class,0);
		}
		public DoStatementContext doStatement() {
			return getRuleContext(DoStatementContext.class,0);
		}
		public BreakStatementContext breakStatement() {
			return getRuleContext(BreakStatementContext.class,0);
		}
		public ContinueStatementContext continueStatement() {
			return getRuleContext(ContinueStatementContext.class,0);
		}
		public ReturnStatementContext returnStatement() {
			return getRuleContext(ReturnStatementContext.class,0);
		}
		public SynchronizedStatementContext synchronizedStatement() {
			return getRuleContext(SynchronizedStatementContext.class,0);
		}
		public ThrowStatementContext throwStatement() {
			return getRuleContext(ThrowStatementContext.class,0);
		}
		public TryStatementContext tryStatement() {
			return getRuleContext(TryStatementContext.class,0);
		}
		public StatementWithoutTrailingSubstatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementWithoutTrailingSubstatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterStatementWithoutTrailingSubstatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitStatementWithoutTrailingSubstatement(this);
		}
	}

	public final StatementWithoutTrailingSubstatementContext statementWithoutTrailingSubstatement() throws RecognitionException {
		StatementWithoutTrailingSubstatementContext _localctx = new StatementWithoutTrailingSubstatementContext(_ctx, getState());
		enterRule(_localctx, 280, RULE_statementWithoutTrailingSubstatement);
		try {
			setState(1529);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1517);
				block();
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
				setState(1518);
				emptyStatement();
				}
				break;
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case NEW:
			case SHORT:
			case SUPER:
			case THIS:
			case VOID:
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case CharacterLiteral:
			case StringLiteral:
			case NullLiteral:
			case LPAREN:
			case INC:
			case DEC:
			case Identifier:
			case AT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1519);
				expressionStatement();
				}
				break;
			case ASSERT:
				enterOuterAlt(_localctx, 4);
				{
				setState(1520);
				assertStatement();
				}
				break;
			case SWITCH:
				enterOuterAlt(_localctx, 5);
				{
				setState(1521);
				switchStatement();
				}
				break;
			case DO:
				enterOuterAlt(_localctx, 6);
				{
				setState(1522);
				doStatement();
				}
				break;
			case BREAK:
				enterOuterAlt(_localctx, 7);
				{
				setState(1523);
				breakStatement();
				}
				break;
			case CONTINUE:
				enterOuterAlt(_localctx, 8);
				{
				setState(1524);
				continueStatement();
				}
				break;
			case RETURN:
				enterOuterAlt(_localctx, 9);
				{
				setState(1525);
				returnStatement();
				}
				break;
			case SYNCHRONIZED:
				enterOuterAlt(_localctx, 10);
				{
				setState(1526);
				synchronizedStatement();
				}
				break;
			case THROW:
				enterOuterAlt(_localctx, 11);
				{
				setState(1527);
				throwStatement();
				}
				break;
			case TRY:
				enterOuterAlt(_localctx, 12);
				{
				setState(1528);
				tryStatement();
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

	public static class EmptyStatementContext extends ParserRuleContext {
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public EmptyStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_emptyStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEmptyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEmptyStatement(this);
		}
	}

	public final EmptyStatementContext emptyStatement() throws RecognitionException {
		EmptyStatementContext _localctx = new EmptyStatementContext(_ctx, getState());
		enterRule(_localctx, 282, RULE_emptyStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1531);
			match(SEMI);
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

	public static class LabeledStatementContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode COLON() { return getToken(Java8Parser.COLON, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public LabeledStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLabeledStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLabeledStatement(this);
		}
	}

	public final LabeledStatementContext labeledStatement() throws RecognitionException {
		LabeledStatementContext _localctx = new LabeledStatementContext(_ctx, getState());
		enterRule(_localctx, 284, RULE_labeledStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1533);
			match(Identifier);
			setState(1534);
			match(COLON);
			setState(1535);
			statement();
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

	public static class LabeledStatementNoShortIfContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode COLON() { return getToken(Java8Parser.COLON, 0); }
		public StatementNoShortIfContext statementNoShortIf() {
			return getRuleContext(StatementNoShortIfContext.class,0);
		}
		public LabeledStatementNoShortIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledStatementNoShortIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLabeledStatementNoShortIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLabeledStatementNoShortIf(this);
		}
	}

	public final LabeledStatementNoShortIfContext labeledStatementNoShortIf() throws RecognitionException {
		LabeledStatementNoShortIfContext _localctx = new LabeledStatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 286, RULE_labeledStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1537);
			match(Identifier);
			setState(1538);
			match(COLON);
			setState(1539);
			statementNoShortIf();
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

	public static class ExpressionStatementContext extends ParserRuleContext {
		public StatementExpressionContext statementExpression() {
			return getRuleContext(StatementExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public ExpressionStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExpressionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExpressionStatement(this);
		}
	}

	public final ExpressionStatementContext expressionStatement() throws RecognitionException {
		ExpressionStatementContext _localctx = new ExpressionStatementContext(_ctx, getState());
		enterRule(_localctx, 288, RULE_expressionStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1541);
			statementExpression();
			setState(1542);
			match(SEMI);
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

	public static class StatementExpressionContext extends ParserRuleContext {
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public PreIncrementExpressionContext preIncrementExpression() {
			return getRuleContext(PreIncrementExpressionContext.class,0);
		}
		public PreDecrementExpressionContext preDecrementExpression() {
			return getRuleContext(PreDecrementExpressionContext.class,0);
		}
		public PostIncrementExpressionContext postIncrementExpression() {
			return getRuleContext(PostIncrementExpressionContext.class,0);
		}
		public PostDecrementExpressionContext postDecrementExpression() {
			return getRuleContext(PostDecrementExpressionContext.class,0);
		}
		public MethodInvocationContext methodInvocation() {
			return getRuleContext(MethodInvocationContext.class,0);
		}
		public ClassInstanceCreationExpressionContext classInstanceCreationExpression() {
			return getRuleContext(ClassInstanceCreationExpressionContext.class,0);
		}
		public StatementExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterStatementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitStatementExpression(this);
		}
	}

	public final StatementExpressionContext statementExpression() throws RecognitionException {
		StatementExpressionContext _localctx = new StatementExpressionContext(_ctx, getState());
		enterRule(_localctx, 290, RULE_statementExpression);
		try {
			setState(1551);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,152,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1544);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1545);
				preIncrementExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1546);
				preDecrementExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1547);
				postIncrementExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1548);
				postDecrementExpression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1549);
				methodInvocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1550);
				classInstanceCreationExpression();
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

	public static class IfThenStatementContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(Java8Parser.IF, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public IfThenStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifThenStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterIfThenStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitIfThenStatement(this);
		}
	}

	public final IfThenStatementContext ifThenStatement() throws RecognitionException {
		IfThenStatementContext _localctx = new IfThenStatementContext(_ctx, getState());
		enterRule(_localctx, 292, RULE_ifThenStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1553);
			match(IF);
			setState(1554);
			match(LPAREN);
			setState(1555);
			expression();
			setState(1556);
			match(RPAREN);
			setState(1557);
			statement();
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

	public static class IfThenElseStatementContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(Java8Parser.IF, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementNoShortIfContext statementNoShortIf() {
			return getRuleContext(StatementNoShortIfContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(Java8Parser.ELSE, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public IfThenElseStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifThenElseStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterIfThenElseStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitIfThenElseStatement(this);
		}
	}

	public final IfThenElseStatementContext ifThenElseStatement() throws RecognitionException {
		IfThenElseStatementContext _localctx = new IfThenElseStatementContext(_ctx, getState());
		enterRule(_localctx, 294, RULE_ifThenElseStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1559);
			match(IF);
			setState(1560);
			match(LPAREN);
			setState(1561);
			expression();
			setState(1562);
			match(RPAREN);
			setState(1563);
			statementNoShortIf();
			setState(1564);
			match(ELSE);
			setState(1565);
			statement();
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

	public static class IfThenElseStatementNoShortIfContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(Java8Parser.IF, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public List<StatementNoShortIfContext> statementNoShortIf() {
			return getRuleContexts(StatementNoShortIfContext.class);
		}
		public StatementNoShortIfContext statementNoShortIf(int i) {
			return getRuleContext(StatementNoShortIfContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(Java8Parser.ELSE, 0); }
		public IfThenElseStatementNoShortIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifThenElseStatementNoShortIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterIfThenElseStatementNoShortIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitIfThenElseStatementNoShortIf(this);
		}
	}

	public final IfThenElseStatementNoShortIfContext ifThenElseStatementNoShortIf() throws RecognitionException {
		IfThenElseStatementNoShortIfContext _localctx = new IfThenElseStatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 296, RULE_ifThenElseStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1567);
			match(IF);
			setState(1568);
			match(LPAREN);
			setState(1569);
			expression();
			setState(1570);
			match(RPAREN);
			setState(1571);
			statementNoShortIf();
			setState(1572);
			match(ELSE);
			setState(1573);
			statementNoShortIf();
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

	public static class AssertStatementContext extends ParserRuleContext {
		public TerminalNode ASSERT() { return getToken(Java8Parser.ASSERT, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public TerminalNode COLON() { return getToken(Java8Parser.COLON, 0); }
		public AssertStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assertStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAssertStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAssertStatement(this);
		}
	}

	public final AssertStatementContext assertStatement() throws RecognitionException {
		AssertStatementContext _localctx = new AssertStatementContext(_ctx, getState());
		enterRule(_localctx, 298, RULE_assertStatement);
		try {
			setState(1585);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,153,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1575);
				match(ASSERT);
				setState(1576);
				expression();
				setState(1577);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1579);
				match(ASSERT);
				setState(1580);
				expression();
				setState(1581);
				match(COLON);
				setState(1582);
				expression();
				setState(1583);
				match(SEMI);
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

	public static class SwitchStatementContext extends ParserRuleContext {
		public TerminalNode SWITCH() { return getToken(Java8Parser.SWITCH, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public SwitchBlockContext switchBlock() {
			return getRuleContext(SwitchBlockContext.class,0);
		}
		public SwitchStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSwitchStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSwitchStatement(this);
		}
	}

	public final SwitchStatementContext switchStatement() throws RecognitionException {
		SwitchStatementContext _localctx = new SwitchStatementContext(_ctx, getState());
		enterRule(_localctx, 300, RULE_switchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1587);
			match(SWITCH);
			setState(1588);
			match(LPAREN);
			setState(1589);
			expression();
			setState(1590);
			match(RPAREN);
			setState(1591);
			switchBlock();
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

	public static class SwitchBlockContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public List<SwitchBlockStatementGroupContext> switchBlockStatementGroup() {
			return getRuleContexts(SwitchBlockStatementGroupContext.class);
		}
		public SwitchBlockStatementGroupContext switchBlockStatementGroup(int i) {
			return getRuleContext(SwitchBlockStatementGroupContext.class,i);
		}
		public List<SwitchLabelContext> switchLabel() {
			return getRuleContexts(SwitchLabelContext.class);
		}
		public SwitchLabelContext switchLabel(int i) {
			return getRuleContext(SwitchLabelContext.class,i);
		}
		public SwitchBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSwitchBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSwitchBlock(this);
		}
	}

	public final SwitchBlockContext switchBlock() throws RecognitionException {
		SwitchBlockContext _localctx = new SwitchBlockContext(_ctx, getState());
		enterRule(_localctx, 302, RULE_switchBlock);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1593);
			match(LBRACE);
			setState(1597);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1594);
					switchBlockStatementGroup();
					}
					} 
				}
				setState(1599);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,154,_ctx);
			}
			setState(1603);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CASE || _la==DEFAULT) {
				{
				{
				setState(1600);
				switchLabel();
				}
				}
				setState(1605);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1606);
			match(RBRACE);
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

	public static class SwitchBlockStatementGroupContext extends ParserRuleContext {
		public SwitchLabelsContext switchLabels() {
			return getRuleContext(SwitchLabelsContext.class,0);
		}
		public BlockStatementsContext blockStatements() {
			return getRuleContext(BlockStatementsContext.class,0);
		}
		public SwitchBlockStatementGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchBlockStatementGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSwitchBlockStatementGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSwitchBlockStatementGroup(this);
		}
	}

	public final SwitchBlockStatementGroupContext switchBlockStatementGroup() throws RecognitionException {
		SwitchBlockStatementGroupContext _localctx = new SwitchBlockStatementGroupContext(_ctx, getState());
		enterRule(_localctx, 304, RULE_switchBlockStatementGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1608);
			switchLabels();
			setState(1609);
			blockStatements();
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

	public static class SwitchLabelsContext extends ParserRuleContext {
		public List<SwitchLabelContext> switchLabel() {
			return getRuleContexts(SwitchLabelContext.class);
		}
		public SwitchLabelContext switchLabel(int i) {
			return getRuleContext(SwitchLabelContext.class,i);
		}
		public SwitchLabelsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchLabels; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSwitchLabels(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSwitchLabels(this);
		}
	}

	public final SwitchLabelsContext switchLabels() throws RecognitionException {
		SwitchLabelsContext _localctx = new SwitchLabelsContext(_ctx, getState());
		enterRule(_localctx, 306, RULE_switchLabels);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1611);
			switchLabel();
			setState(1615);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CASE || _la==DEFAULT) {
				{
				{
				setState(1612);
				switchLabel();
				}
				}
				setState(1617);
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

	public static class SwitchLabelContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(Java8Parser.CASE, 0); }
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(Java8Parser.COLON, 0); }
		public EnumConstantNameContext enumConstantName() {
			return getRuleContext(EnumConstantNameContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(Java8Parser.DEFAULT, 0); }
		public SwitchLabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchLabel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSwitchLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSwitchLabel(this);
		}
	}

	public final SwitchLabelContext switchLabel() throws RecognitionException {
		SwitchLabelContext _localctx = new SwitchLabelContext(_ctx, getState());
		enterRule(_localctx, 308, RULE_switchLabel);
		try {
			setState(1628);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,157,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1618);
				match(CASE);
				setState(1619);
				constantExpression();
				setState(1620);
				match(COLON);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1622);
				match(CASE);
				setState(1623);
				enumConstantName();
				setState(1624);
				match(COLON);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1626);
				match(DEFAULT);
				setState(1627);
				match(COLON);
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

	public static class EnumConstantNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public EnumConstantNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumConstantName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnumConstantName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnumConstantName(this);
		}
	}

	public final EnumConstantNameContext enumConstantName() throws RecognitionException {
		EnumConstantNameContext _localctx = new EnumConstantNameContext(_ctx, getState());
		enterRule(_localctx, 310, RULE_enumConstantName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1630);
			match(Identifier);
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

	public static class WhileStatementContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(Java8Parser.WHILE, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterWhileStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitWhileStatement(this);
		}
	}

	public final WhileStatementContext whileStatement() throws RecognitionException {
		WhileStatementContext _localctx = new WhileStatementContext(_ctx, getState());
		enterRule(_localctx, 312, RULE_whileStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1632);
			match(WHILE);
			setState(1633);
			match(LPAREN);
			setState(1634);
			expression();
			setState(1635);
			match(RPAREN);
			setState(1636);
			statement();
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

	public static class WhileStatementNoShortIfContext extends ParserRuleContext {
		public TerminalNode WHILE() { return getToken(Java8Parser.WHILE, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementNoShortIfContext statementNoShortIf() {
			return getRuleContext(StatementNoShortIfContext.class,0);
		}
		public WhileStatementNoShortIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStatementNoShortIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterWhileStatementNoShortIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitWhileStatementNoShortIf(this);
		}
	}

	public final WhileStatementNoShortIfContext whileStatementNoShortIf() throws RecognitionException {
		WhileStatementNoShortIfContext _localctx = new WhileStatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 314, RULE_whileStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1638);
			match(WHILE);
			setState(1639);
			match(LPAREN);
			setState(1640);
			expression();
			setState(1641);
			match(RPAREN);
			setState(1642);
			statementNoShortIf();
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

	public static class DoStatementContext extends ParserRuleContext {
		public TerminalNode DO() { return getToken(Java8Parser.DO, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode WHILE() { return getToken(Java8Parser.WHILE, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public DoStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_doStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterDoStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitDoStatement(this);
		}
	}

	public final DoStatementContext doStatement() throws RecognitionException {
		DoStatementContext _localctx = new DoStatementContext(_ctx, getState());
		enterRule(_localctx, 316, RULE_doStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1644);
			match(DO);
			setState(1645);
			statement();
			setState(1646);
			match(WHILE);
			setState(1647);
			match(LPAREN);
			setState(1648);
			expression();
			setState(1649);
			match(RPAREN);
			setState(1650);
			match(SEMI);
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

	public static class ForStatementContext extends ParserRuleContext {
		public BasicForStatementContext basicForStatement() {
			return getRuleContext(BasicForStatementContext.class,0);
		}
		public EnhancedForStatementContext enhancedForStatement() {
			return getRuleContext(EnhancedForStatementContext.class,0);
		}
		public ForStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterForStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitForStatement(this);
		}
	}

	public final ForStatementContext forStatement() throws RecognitionException {
		ForStatementContext _localctx = new ForStatementContext(_ctx, getState());
		enterRule(_localctx, 318, RULE_forStatement);
		try {
			setState(1654);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,158,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1652);
				basicForStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1653);
				enhancedForStatement();
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

	public static class ForStatementNoShortIfContext extends ParserRuleContext {
		public BasicForStatementNoShortIfContext basicForStatementNoShortIf() {
			return getRuleContext(BasicForStatementNoShortIfContext.class,0);
		}
		public EnhancedForStatementNoShortIfContext enhancedForStatementNoShortIf() {
			return getRuleContext(EnhancedForStatementNoShortIfContext.class,0);
		}
		public ForStatementNoShortIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStatementNoShortIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterForStatementNoShortIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitForStatementNoShortIf(this);
		}
	}

	public final ForStatementNoShortIfContext forStatementNoShortIf() throws RecognitionException {
		ForStatementNoShortIfContext _localctx = new ForStatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 320, RULE_forStatementNoShortIf);
		try {
			setState(1658);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,159,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1656);
				basicForStatementNoShortIf();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1657);
				enhancedForStatementNoShortIf();
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

	public static class BasicForStatementContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(Java8Parser.FOR, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public List<TerminalNode> SEMI() { return getTokens(Java8Parser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(Java8Parser.SEMI, i);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ForInitContext forInit() {
			return getRuleContext(ForInitContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ForUpdateContext forUpdate() {
			return getRuleContext(ForUpdateContext.class,0);
		}
		public BasicForStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basicForStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterBasicForStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitBasicForStatement(this);
		}
	}

	public final BasicForStatementContext basicForStatement() throws RecognitionException {
		BasicForStatementContext _localctx = new BasicForStatementContext(_ctx, getState());
		enterRule(_localctx, 322, RULE_basicForStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1660);
			match(FOR);
			setState(1661);
			match(LPAREN);
			setState(1663);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (INC - 81)) | (1L << (DEC - 81)) | (1L << (Identifier - 81)) | (1L << (AT - 81)))) != 0)) {
				{
				setState(1662);
				forInit();
				}
			}

			setState(1665);
			match(SEMI);
			setState(1667);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				setState(1666);
				expression();
				}
			}

			setState(1669);
			match(SEMI);
			setState(1671);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (INC - 81)) | (1L << (DEC - 81)) | (1L << (Identifier - 81)) | (1L << (AT - 81)))) != 0)) {
				{
				setState(1670);
				forUpdate();
				}
			}

			setState(1673);
			match(RPAREN);
			setState(1674);
			statement();
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

	public static class BasicForStatementNoShortIfContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(Java8Parser.FOR, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public List<TerminalNode> SEMI() { return getTokens(Java8Parser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(Java8Parser.SEMI, i);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementNoShortIfContext statementNoShortIf() {
			return getRuleContext(StatementNoShortIfContext.class,0);
		}
		public ForInitContext forInit() {
			return getRuleContext(ForInitContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ForUpdateContext forUpdate() {
			return getRuleContext(ForUpdateContext.class,0);
		}
		public BasicForStatementNoShortIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basicForStatementNoShortIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterBasicForStatementNoShortIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitBasicForStatementNoShortIf(this);
		}
	}

	public final BasicForStatementNoShortIfContext basicForStatementNoShortIf() throws RecognitionException {
		BasicForStatementNoShortIfContext _localctx = new BasicForStatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 324, RULE_basicForStatementNoShortIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1676);
			match(FOR);
			setState(1677);
			match(LPAREN);
			setState(1679);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (INC - 81)) | (1L << (DEC - 81)) | (1L << (Identifier - 81)) | (1L << (AT - 81)))) != 0)) {
				{
				setState(1678);
				forInit();
				}
			}

			setState(1681);
			match(SEMI);
			setState(1683);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				setState(1682);
				expression();
				}
			}

			setState(1685);
			match(SEMI);
			setState(1687);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 81)) & ~0x3f) == 0 && ((1L << (_la - 81)) & ((1L << (INC - 81)) | (1L << (DEC - 81)) | (1L << (Identifier - 81)) | (1L << (AT - 81)))) != 0)) {
				{
				setState(1686);
				forUpdate();
				}
			}

			setState(1689);
			match(RPAREN);
			setState(1690);
			statementNoShortIf();
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

	public static class ForInitContext extends ParserRuleContext {
		public StatementExpressionListContext statementExpressionList() {
			return getRuleContext(StatementExpressionListContext.class,0);
		}
		public LocalVariableDeclarationContext localVariableDeclaration() {
			return getRuleContext(LocalVariableDeclarationContext.class,0);
		}
		public ForInitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forInit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterForInit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitForInit(this);
		}
	}

	public final ForInitContext forInit() throws RecognitionException {
		ForInitContext _localctx = new ForInitContext(_ctx, getState());
		enterRule(_localctx, 326, RULE_forInit);
		try {
			setState(1694);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1692);
				statementExpressionList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1693);
				localVariableDeclaration();
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

	public static class ForUpdateContext extends ParserRuleContext {
		public StatementExpressionListContext statementExpressionList() {
			return getRuleContext(StatementExpressionListContext.class,0);
		}
		public ForUpdateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forUpdate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterForUpdate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitForUpdate(this);
		}
	}

	public final ForUpdateContext forUpdate() throws RecognitionException {
		ForUpdateContext _localctx = new ForUpdateContext(_ctx, getState());
		enterRule(_localctx, 328, RULE_forUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1696);
			statementExpressionList();
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

	public static class StatementExpressionListContext extends ParserRuleContext {
		public List<StatementExpressionContext> statementExpression() {
			return getRuleContexts(StatementExpressionContext.class);
		}
		public StatementExpressionContext statementExpression(int i) {
			return getRuleContext(StatementExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public StatementExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementExpressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterStatementExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitStatementExpressionList(this);
		}
	}

	public final StatementExpressionListContext statementExpressionList() throws RecognitionException {
		StatementExpressionListContext _localctx = new StatementExpressionListContext(_ctx, getState());
		enterRule(_localctx, 330, RULE_statementExpressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1698);
			statementExpression();
			setState(1703);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1699);
				match(COMMA);
				setState(1700);
				statementExpression();
				}
				}
				setState(1705);
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

	public static class EnhancedForStatementContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(Java8Parser.FOR, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public TerminalNode COLON() { return getToken(Java8Parser.COLON, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public EnhancedForStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enhancedForStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnhancedForStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnhancedForStatement(this);
		}
	}

	public final EnhancedForStatementContext enhancedForStatement() throws RecognitionException {
		EnhancedForStatementContext _localctx = new EnhancedForStatementContext(_ctx, getState());
		enterRule(_localctx, 332, RULE_enhancedForStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1706);
			match(FOR);
			setState(1707);
			match(LPAREN);
			setState(1711);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1708);
				variableModifier();
				}
				}
				setState(1713);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1714);
			unannType();
			setState(1715);
			variableDeclaratorId();
			setState(1716);
			match(COLON);
			setState(1717);
			expression();
			setState(1718);
			match(RPAREN);
			setState(1719);
			statement();
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

	public static class EnhancedForStatementNoShortIfContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(Java8Parser.FOR, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public TerminalNode COLON() { return getToken(Java8Parser.COLON, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public StatementNoShortIfContext statementNoShortIf() {
			return getRuleContext(StatementNoShortIfContext.class,0);
		}
		public List<VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public EnhancedForStatementNoShortIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enhancedForStatementNoShortIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEnhancedForStatementNoShortIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEnhancedForStatementNoShortIf(this);
		}
	}

	public final EnhancedForStatementNoShortIfContext enhancedForStatementNoShortIf() throws RecognitionException {
		EnhancedForStatementNoShortIfContext _localctx = new EnhancedForStatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 334, RULE_enhancedForStatementNoShortIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1721);
			match(FOR);
			setState(1722);
			match(LPAREN);
			setState(1726);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1723);
				variableModifier();
				}
				}
				setState(1728);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1729);
			unannType();
			setState(1730);
			variableDeclaratorId();
			setState(1731);
			match(COLON);
			setState(1732);
			expression();
			setState(1733);
			match(RPAREN);
			setState(1734);
			statementNoShortIf();
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

	public static class BreakStatementContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(Java8Parser.BREAK, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public BreakStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitBreakStatement(this);
		}
	}

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 336, RULE_breakStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1736);
			match(BREAK);
			setState(1738);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1737);
				match(Identifier);
				}
			}

			setState(1740);
			match(SEMI);
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

	public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(Java8Parser.CONTINUE, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public ContinueStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitContinueStatement(this);
		}
	}

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 338, RULE_continueStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1742);
			match(CONTINUE);
			setState(1744);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Identifier) {
				{
				setState(1743);
				match(Identifier);
				}
			}

			setState(1746);
			match(SEMI);
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

	public static class ReturnStatementContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(Java8Parser.RETURN, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitReturnStatement(this);
		}
	}

	public final ReturnStatementContext returnStatement() throws RecognitionException {
		ReturnStatementContext _localctx = new ReturnStatementContext(_ctx, getState());
		enterRule(_localctx, 340, RULE_returnStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1748);
			match(RETURN);
			setState(1750);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				setState(1749);
				expression();
				}
			}

			setState(1752);
			match(SEMI);
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

	public static class ThrowStatementContext extends ParserRuleContext {
		public TerminalNode THROW() { return getToken(Java8Parser.THROW, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public ThrowStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_throwStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterThrowStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitThrowStatement(this);
		}
	}

	public final ThrowStatementContext throwStatement() throws RecognitionException {
		ThrowStatementContext _localctx = new ThrowStatementContext(_ctx, getState());
		enterRule(_localctx, 342, RULE_throwStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1754);
			match(THROW);
			setState(1755);
			expression();
			setState(1756);
			match(SEMI);
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

	public static class SynchronizedStatementContext extends ParserRuleContext {
		public TerminalNode SYNCHRONIZED() { return getToken(Java8Parser.SYNCHRONIZED, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public SynchronizedStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_synchronizedStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSynchronizedStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSynchronizedStatement(this);
		}
	}

	public final SynchronizedStatementContext synchronizedStatement() throws RecognitionException {
		SynchronizedStatementContext _localctx = new SynchronizedStatementContext(_ctx, getState());
		enterRule(_localctx, 344, RULE_synchronizedStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1758);
			match(SYNCHRONIZED);
			setState(1759);
			match(LPAREN);
			setState(1760);
			expression();
			setState(1761);
			match(RPAREN);
			setState(1762);
			block();
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

	public static class TryStatementContext extends ParserRuleContext {
		public TerminalNode TRY() { return getToken(Java8Parser.TRY, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CatchesContext catches() {
			return getRuleContext(CatchesContext.class,0);
		}
		public Finally_Context finally_() {
			return getRuleContext(Finally_Context.class,0);
		}
		public TryWithResourcesStatementContext tryWithResourcesStatement() {
			return getRuleContext(TryWithResourcesStatementContext.class,0);
		}
		public TryStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tryStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTryStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTryStatement(this);
		}
	}

	public final TryStatementContext tryStatement() throws RecognitionException {
		TryStatementContext _localctx = new TryStatementContext(_ctx, getState());
		enterRule(_localctx, 346, RULE_tryStatement);
		int _la;
		try {
			setState(1776);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1764);
				match(TRY);
				setState(1765);
				block();
				setState(1766);
				catches();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1768);
				match(TRY);
				setState(1769);
				block();
				setState(1771);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CATCH) {
					{
					setState(1770);
					catches();
					}
				}

				setState(1773);
				finally_();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1775);
				tryWithResourcesStatement();
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

	public static class CatchesContext extends ParserRuleContext {
		public List<CatchClauseContext> catchClause() {
			return getRuleContexts(CatchClauseContext.class);
		}
		public CatchClauseContext catchClause(int i) {
			return getRuleContext(CatchClauseContext.class,i);
		}
		public CatchesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catches; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterCatches(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitCatches(this);
		}
	}

	public final CatchesContext catches() throws RecognitionException {
		CatchesContext _localctx = new CatchesContext(_ctx, getState());
		enterRule(_localctx, 348, RULE_catches);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1778);
			catchClause();
			setState(1782);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CATCH) {
				{
				{
				setState(1779);
				catchClause();
				}
				}
				setState(1784);
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

	public static class CatchClauseContext extends ParserRuleContext {
		public TerminalNode CATCH() { return getToken(Java8Parser.CATCH, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public CatchFormalParameterContext catchFormalParameter() {
			return getRuleContext(CatchFormalParameterContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CatchClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catchClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterCatchClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitCatchClause(this);
		}
	}

	public final CatchClauseContext catchClause() throws RecognitionException {
		CatchClauseContext _localctx = new CatchClauseContext(_ctx, getState());
		enterRule(_localctx, 350, RULE_catchClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1785);
			match(CATCH);
			setState(1786);
			match(LPAREN);
			setState(1787);
			catchFormalParameter();
			setState(1788);
			match(RPAREN);
			setState(1789);
			block();
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

	public static class CatchFormalParameterContext extends ParserRuleContext {
		public CatchTypeContext catchType() {
			return getRuleContext(CatchTypeContext.class,0);
		}
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public List<VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public CatchFormalParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catchFormalParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterCatchFormalParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitCatchFormalParameter(this);
		}
	}

	public final CatchFormalParameterContext catchFormalParameter() throws RecognitionException {
		CatchFormalParameterContext _localctx = new CatchFormalParameterContext(_ctx, getState());
		enterRule(_localctx, 352, RULE_catchFormalParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1794);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1791);
				variableModifier();
				}
				}
				setState(1796);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1797);
			catchType();
			setState(1798);
			variableDeclaratorId();
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

	public static class CatchTypeContext extends ParserRuleContext {
		public UnannClassTypeContext unannClassType() {
			return getRuleContext(UnannClassTypeContext.class,0);
		}
		public List<TerminalNode> BITOR() { return getTokens(Java8Parser.BITOR); }
		public TerminalNode BITOR(int i) {
			return getToken(Java8Parser.BITOR, i);
		}
		public List<ClassTypeContext> classType() {
			return getRuleContexts(ClassTypeContext.class);
		}
		public ClassTypeContext classType(int i) {
			return getRuleContext(ClassTypeContext.class,i);
		}
		public CatchTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catchType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterCatchType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitCatchType(this);
		}
	}

	public final CatchTypeContext catchType() throws RecognitionException {
		CatchTypeContext _localctx = new CatchTypeContext(_ctx, getState());
		enterRule(_localctx, 354, RULE_catchType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1800);
			unannClassType();
			setState(1805);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BITOR) {
				{
				{
				setState(1801);
				match(BITOR);
				setState(1802);
				classType();
				}
				}
				setState(1807);
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

	public static class Finally_Context extends ParserRuleContext {
		public TerminalNode FINALLY() { return getToken(Java8Parser.FINALLY, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public Finally_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_finally_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFinally_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFinally_(this);
		}
	}

	public final Finally_Context finally_() throws RecognitionException {
		Finally_Context _localctx = new Finally_Context(_ctx, getState());
		enterRule(_localctx, 356, RULE_finally_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1808);
			match(FINALLY);
			setState(1809);
			block();
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

	public static class TryWithResourcesStatementContext extends ParserRuleContext {
		public TerminalNode TRY() { return getToken(Java8Parser.TRY, 0); }
		public ResourceSpecificationContext resourceSpecification() {
			return getRuleContext(ResourceSpecificationContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public CatchesContext catches() {
			return getRuleContext(CatchesContext.class,0);
		}
		public Finally_Context finally_() {
			return getRuleContext(Finally_Context.class,0);
		}
		public TryWithResourcesStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tryWithResourcesStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTryWithResourcesStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTryWithResourcesStatement(this);
		}
	}

	public final TryWithResourcesStatementContext tryWithResourcesStatement() throws RecognitionException {
		TryWithResourcesStatementContext _localctx = new TryWithResourcesStatementContext(_ctx, getState());
		enterRule(_localctx, 358, RULE_tryWithResourcesStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1811);
			match(TRY);
			setState(1812);
			resourceSpecification();
			setState(1813);
			block();
			setState(1815);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CATCH) {
				{
				setState(1814);
				catches();
				}
			}

			setState(1818);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FINALLY) {
				{
				setState(1817);
				finally_();
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

	public static class ResourceSpecificationContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ResourceListContext resourceList() {
			return getRuleContext(ResourceListContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public ResourceSpecificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceSpecification; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterResourceSpecification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitResourceSpecification(this);
		}
	}

	public final ResourceSpecificationContext resourceSpecification() throws RecognitionException {
		ResourceSpecificationContext _localctx = new ResourceSpecificationContext(_ctx, getState());
		enterRule(_localctx, 360, RULE_resourceSpecification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1820);
			match(LPAREN);
			setState(1821);
			resourceList();
			setState(1823);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(1822);
				match(SEMI);
				}
			}

			setState(1825);
			match(RPAREN);
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

	public static class ResourceListContext extends ParserRuleContext {
		public List<ResourceContext> resource() {
			return getRuleContexts(ResourceContext.class);
		}
		public ResourceContext resource(int i) {
			return getRuleContext(ResourceContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(Java8Parser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(Java8Parser.SEMI, i);
		}
		public ResourceListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterResourceList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitResourceList(this);
		}
	}

	public final ResourceListContext resourceList() throws RecognitionException {
		ResourceListContext _localctx = new ResourceListContext(_ctx, getState());
		enterRule(_localctx, 362, RULE_resourceList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1827);
			resource();
			setState(1832);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,181,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1828);
					match(SEMI);
					setState(1829);
					resource();
					}
					} 
				}
				setState(1834);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,181,_ctx);
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

	public static class ResourceContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(Java8Parser.ASSIGN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public ResourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterResource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitResource(this);
		}
	}

	public final ResourceContext resource() throws RecognitionException {
		ResourceContext _localctx = new ResourceContext(_ctx, getState());
		enterRule(_localctx, 364, RULE_resource);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1838);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1835);
				variableModifier();
				}
				}
				setState(1840);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1841);
			unannType();
			setState(1842);
			variableDeclaratorId();
			setState(1843);
			match(ASSIGN);
			setState(1844);
			expression();
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

	public static class PrimaryContext extends ParserRuleContext {
		public PrimaryNoNewArray_lfno_primaryContext primaryNoNewArray_lfno_primary() {
			return getRuleContext(PrimaryNoNewArray_lfno_primaryContext.class,0);
		}
		public ArrayCreationExpressionContext arrayCreationExpression() {
			return getRuleContext(ArrayCreationExpressionContext.class,0);
		}
		public List<PrimaryNoNewArray_lf_primaryContext> primaryNoNewArray_lf_primary() {
			return getRuleContexts(PrimaryNoNewArray_lf_primaryContext.class);
		}
		public PrimaryNoNewArray_lf_primaryContext primaryNoNewArray_lf_primary(int i) {
			return getRuleContext(PrimaryNoNewArray_lf_primaryContext.class,i);
		}
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimary(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 366, RULE_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1848);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,183,_ctx) ) {
			case 1:
				{
				setState(1846);
				primaryNoNewArray_lfno_primary();
				}
				break;
			case 2:
				{
				setState(1847);
				arrayCreationExpression();
				}
				break;
			}
			setState(1853);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,184,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1850);
					primaryNoNewArray_lf_primary();
					}
					} 
				}
				setState(1855);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,184,_ctx);
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

	public static class PrimaryNoNewArrayContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode CLASS() { return getToken(Java8Parser.CLASS, 0); }
		public List<SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public TerminalNode VOID() { return getToken(Java8Parser.VOID, 0); }
		public TerminalNode THIS() { return getToken(Java8Parser.THIS, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ClassInstanceCreationExpressionContext classInstanceCreationExpression() {
			return getRuleContext(ClassInstanceCreationExpressionContext.class,0);
		}
		public FieldAccessContext fieldAccess() {
			return getRuleContext(FieldAccessContext.class,0);
		}
		public ArrayAccessContext arrayAccess() {
			return getRuleContext(ArrayAccessContext.class,0);
		}
		public MethodInvocationContext methodInvocation() {
			return getRuleContext(MethodInvocationContext.class,0);
		}
		public MethodReferenceContext methodReference() {
			return getRuleContext(MethodReferenceContext.class,0);
		}
		public PrimaryNoNewArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray(this);
		}
	}

	public final PrimaryNoNewArrayContext primaryNoNewArray() throws RecognitionException {
		PrimaryNoNewArrayContext _localctx = new PrimaryNoNewArrayContext(_ctx, getState());
		enterRule(_localctx, 368, RULE_primaryNoNewArray);
		int _la;
		try {
			setState(1884);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,186,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1856);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1857);
				typeName();
				setState(1861);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(1858);
					squareBrackets();
					}
					}
					setState(1863);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1864);
				match(DOT);
				setState(1865);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1867);
				match(VOID);
				setState(1868);
				match(DOT);
				setState(1869);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1870);
				match(THIS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1871);
				typeName();
				setState(1872);
				match(DOT);
				setState(1873);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1875);
				match(LPAREN);
				setState(1876);
				expression();
				setState(1877);
				match(RPAREN);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1879);
				classInstanceCreationExpression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1880);
				fieldAccess();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1881);
				arrayAccess();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1882);
				methodInvocation();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1883);
				methodReference();
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

	public static class PrimaryNoNewArray_lf_arrayAccessContext extends ParserRuleContext {
		public PrimaryNoNewArray_lf_arrayAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lf_arrayAccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lf_arrayAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lf_arrayAccess(this);
		}
	}

	public final PrimaryNoNewArray_lf_arrayAccessContext primaryNoNewArray_lf_arrayAccess() throws RecognitionException {
		PrimaryNoNewArray_lf_arrayAccessContext _localctx = new PrimaryNoNewArray_lf_arrayAccessContext(_ctx, getState());
		enterRule(_localctx, 370, RULE_primaryNoNewArray_lf_arrayAccess);
		try {
			enterOuterAlt(_localctx, 1);
			{
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

	public static class PrimaryNoNewArray_lfno_arrayAccessContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode CLASS() { return getToken(Java8Parser.CLASS, 0); }
		public List<SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public TerminalNode VOID() { return getToken(Java8Parser.VOID, 0); }
		public TerminalNode THIS() { return getToken(Java8Parser.THIS, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ClassInstanceCreationExpressionContext classInstanceCreationExpression() {
			return getRuleContext(ClassInstanceCreationExpressionContext.class,0);
		}
		public FieldAccessContext fieldAccess() {
			return getRuleContext(FieldAccessContext.class,0);
		}
		public MethodInvocationContext methodInvocation() {
			return getRuleContext(MethodInvocationContext.class,0);
		}
		public MethodReferenceContext methodReference() {
			return getRuleContext(MethodReferenceContext.class,0);
		}
		public PrimaryNoNewArray_lfno_arrayAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lfno_arrayAccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lfno_arrayAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lfno_arrayAccess(this);
		}
	}

	public final PrimaryNoNewArray_lfno_arrayAccessContext primaryNoNewArray_lfno_arrayAccess() throws RecognitionException {
		PrimaryNoNewArray_lfno_arrayAccessContext _localctx = new PrimaryNoNewArray_lfno_arrayAccessContext(_ctx, getState());
		enterRule(_localctx, 372, RULE_primaryNoNewArray_lfno_arrayAccess);
		int _la;
		try {
			setState(1915);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,188,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1888);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1889);
				typeName();
				setState(1893);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(1890);
					squareBrackets();
					}
					}
					setState(1895);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1896);
				match(DOT);
				setState(1897);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1899);
				match(VOID);
				setState(1900);
				match(DOT);
				setState(1901);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1902);
				match(THIS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1903);
				typeName();
				setState(1904);
				match(DOT);
				setState(1905);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1907);
				match(LPAREN);
				setState(1908);
				expression();
				setState(1909);
				match(RPAREN);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1911);
				classInstanceCreationExpression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1912);
				fieldAccess();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1913);
				methodInvocation();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1914);
				methodReference();
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

	public static class PrimaryNoNewArray_lf_primaryContext extends ParserRuleContext {
		public ClassInstanceCreationExpression_lf_primaryContext classInstanceCreationExpression_lf_primary() {
			return getRuleContext(ClassInstanceCreationExpression_lf_primaryContext.class,0);
		}
		public FieldAccess_lf_primaryContext fieldAccess_lf_primary() {
			return getRuleContext(FieldAccess_lf_primaryContext.class,0);
		}
		public ArrayAccess_lf_primaryContext arrayAccess_lf_primary() {
			return getRuleContext(ArrayAccess_lf_primaryContext.class,0);
		}
		public MethodInvocation_lf_primaryContext methodInvocation_lf_primary() {
			return getRuleContext(MethodInvocation_lf_primaryContext.class,0);
		}
		public MethodReference_lf_primaryContext methodReference_lf_primary() {
			return getRuleContext(MethodReference_lf_primaryContext.class,0);
		}
		public PrimaryNoNewArray_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lf_primary(this);
		}
	}

	public final PrimaryNoNewArray_lf_primaryContext primaryNoNewArray_lf_primary() throws RecognitionException {
		PrimaryNoNewArray_lf_primaryContext _localctx = new PrimaryNoNewArray_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 374, RULE_primaryNoNewArray_lf_primary);
		try {
			setState(1922);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,189,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1917);
				classInstanceCreationExpression_lf_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1918);
				fieldAccess_lf_primary();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1919);
				arrayAccess_lf_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1920);
				methodInvocation_lf_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1921);
				methodReference_lf_primary();
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

	public static class PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext extends ParserRuleContext {
		public PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(this);
		}
	}

	public final PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary() throws RecognitionException {
		PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext _localctx = new PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 376, RULE_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
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

	public static class PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext extends ParserRuleContext {
		public ClassInstanceCreationExpression_lf_primaryContext classInstanceCreationExpression_lf_primary() {
			return getRuleContext(ClassInstanceCreationExpression_lf_primaryContext.class,0);
		}
		public FieldAccess_lf_primaryContext fieldAccess_lf_primary() {
			return getRuleContext(FieldAccess_lf_primaryContext.class,0);
		}
		public MethodInvocation_lf_primaryContext methodInvocation_lf_primary() {
			return getRuleContext(MethodInvocation_lf_primaryContext.class,0);
		}
		public MethodReference_lf_primaryContext methodReference_lf_primary() {
			return getRuleContext(MethodReference_lf_primaryContext.class,0);
		}
		public PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(this);
		}
	}

	public final PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary() throws RecognitionException {
		PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext _localctx = new PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 378, RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary);
		try {
			setState(1930);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,190,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1926);
				classInstanceCreationExpression_lf_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1927);
				fieldAccess_lf_primary();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1928);
				methodInvocation_lf_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1929);
				methodReference_lf_primary();
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

	public static class PrimaryNoNewArray_lfno_primaryContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode CLASS() { return getToken(Java8Parser.CLASS, 0); }
		public List<SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public UnannPrimitiveTypeContext unannPrimitiveType() {
			return getRuleContext(UnannPrimitiveTypeContext.class,0);
		}
		public TerminalNode VOID() { return getToken(Java8Parser.VOID, 0); }
		public TerminalNode THIS() { return getToken(Java8Parser.THIS, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ClassInstanceCreationExpression_lfno_primaryContext classInstanceCreationExpression_lfno_primary() {
			return getRuleContext(ClassInstanceCreationExpression_lfno_primaryContext.class,0);
		}
		public FieldAccess_lfno_primaryContext fieldAccess_lfno_primary() {
			return getRuleContext(FieldAccess_lfno_primaryContext.class,0);
		}
		public ArrayAccess_lfno_primaryContext arrayAccess_lfno_primary() {
			return getRuleContext(ArrayAccess_lfno_primaryContext.class,0);
		}
		public MethodInvocation_lfno_primaryContext methodInvocation_lfno_primary() {
			return getRuleContext(MethodInvocation_lfno_primaryContext.class,0);
		}
		public MethodReference_lfno_primaryContext methodReference_lfno_primary() {
			return getRuleContext(MethodReference_lfno_primaryContext.class,0);
		}
		public PrimaryNoNewArray_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lfno_primary(this);
		}
	}

	public final PrimaryNoNewArray_lfno_primaryContext primaryNoNewArray_lfno_primary() throws RecognitionException {
		PrimaryNoNewArray_lfno_primaryContext _localctx = new PrimaryNoNewArray_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 380, RULE_primaryNoNewArray_lfno_primary);
		int _la;
		try {
			setState(1970);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,193,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1932);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1933);
				typeName();
				setState(1937);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(1934);
					squareBrackets();
					}
					}
					setState(1939);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1940);
				match(DOT);
				setState(1941);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1943);
				unannPrimitiveType();
				setState(1947);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(1944);
					squareBrackets();
					}
					}
					setState(1949);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1950);
				match(DOT);
				setState(1951);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1953);
				match(VOID);
				setState(1954);
				match(DOT);
				setState(1955);
				match(CLASS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1956);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1957);
				typeName();
				setState(1958);
				match(DOT);
				setState(1959);
				match(THIS);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1961);
				match(LPAREN);
				setState(1962);
				expression();
				setState(1963);
				match(RPAREN);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(1965);
				classInstanceCreationExpression_lfno_primary();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(1966);
				fieldAccess_lfno_primary();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(1967);
				arrayAccess_lfno_primary();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(1968);
				methodInvocation_lfno_primary();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(1969);
				methodReference_lfno_primary();
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

	public static class PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext extends ParserRuleContext {
		public PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(this);
		}
	}

	public final PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary() throws RecognitionException {
		PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext _localctx = new PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 382, RULE_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
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

	public static class PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode CLASS() { return getToken(Java8Parser.CLASS, 0); }
		public List<SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public UnannPrimitiveTypeContext unannPrimitiveType() {
			return getRuleContext(UnannPrimitiveTypeContext.class,0);
		}
		public TerminalNode VOID() { return getToken(Java8Parser.VOID, 0); }
		public TerminalNode THIS() { return getToken(Java8Parser.THIS, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ClassInstanceCreationExpression_lfno_primaryContext classInstanceCreationExpression_lfno_primary() {
			return getRuleContext(ClassInstanceCreationExpression_lfno_primaryContext.class,0);
		}
		public FieldAccess_lfno_primaryContext fieldAccess_lfno_primary() {
			return getRuleContext(FieldAccess_lfno_primaryContext.class,0);
		}
		public MethodInvocation_lfno_primaryContext methodInvocation_lfno_primary() {
			return getRuleContext(MethodInvocation_lfno_primaryContext.class,0);
		}
		public MethodReference_lfno_primaryContext methodReference_lfno_primary() {
			return getRuleContext(MethodReference_lfno_primaryContext.class,0);
		}
		public PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(this);
		}
	}

	public final PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary() throws RecognitionException {
		PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext _localctx = new PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 384, RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary);
		int _la;
		try {
			setState(2011);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,196,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1974);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1975);
				typeName();
				setState(1979);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(1976);
					squareBrackets();
					}
					}
					setState(1981);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1982);
				match(DOT);
				setState(1983);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1985);
				unannPrimitiveType();
				setState(1989);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(1986);
					squareBrackets();
					}
					}
					setState(1991);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1992);
				match(DOT);
				setState(1993);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1995);
				match(VOID);
				setState(1996);
				match(DOT);
				setState(1997);
				match(CLASS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1998);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1999);
				typeName();
				setState(2000);
				match(DOT);
				setState(2001);
				match(THIS);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2003);
				match(LPAREN);
				setState(2004);
				expression();
				setState(2005);
				match(RPAREN);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2007);
				classInstanceCreationExpression_lfno_primary();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2008);
				fieldAccess_lfno_primary();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2009);
				methodInvocation_lfno_primary();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2010);
				methodReference_lfno_primary();
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

	public static class ClassInstanceCreationExpressionContext extends ParserRuleContext {
		public TerminalNode NEW() { return getToken(Java8Parser.NEW, 0); }
		public List<AnnotationIdentifierContext> annotationIdentifier() {
			return getRuleContexts(AnnotationIdentifierContext.class);
		}
		public AnnotationIdentifierContext annotationIdentifier(int i) {
			return getRuleContext(AnnotationIdentifierContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public TypeArgumentsOrDiamondContext typeArgumentsOrDiamond() {
			return getRuleContext(TypeArgumentsOrDiamondContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ClassBodyContext classBody() {
			return getRuleContext(ClassBodyContext.class,0);
		}
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ClassInstanceCreationExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classInstanceCreationExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassInstanceCreationExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassInstanceCreationExpression(this);
		}
	}

	public final ClassInstanceCreationExpressionContext classInstanceCreationExpression() throws RecognitionException {
		ClassInstanceCreationExpressionContext _localctx = new ClassInstanceCreationExpressionContext(_ctx, getState());
		enterRule(_localctx, 386, RULE_classInstanceCreationExpression);
		int _la;
		try {
			setState(2072);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,210,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2013);
				match(NEW);
				setState(2015);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2014);
					typeArguments();
					}
				}

				setState(2017);
				annotationIdentifier();
				setState(2022);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(2018);
					match(DOT);
					setState(2019);
					annotationIdentifier();
					}
					}
					setState(2024);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2026);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0 || _la==LT) {
					{
					setState(2025);
					typeArgumentsOrDiamond();
					}
				}

				setState(2028);
				match(LPAREN);
				setState(2030);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2029);
					argumentList();
					}
				}

				setState(2032);
				match(RPAREN);
				setState(2034);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2033);
					classBody();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2036);
				expressionName();
				setState(2037);
				match(DOT);
				setState(2038);
				match(NEW);
				setState(2040);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2039);
					typeArguments();
					}
				}

				setState(2042);
				annotationIdentifier();
				setState(2044);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0 || _la==LT) {
					{
					setState(2043);
					typeArgumentsOrDiamond();
					}
				}

				setState(2046);
				match(LPAREN);
				setState(2048);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2047);
					argumentList();
					}
				}

				setState(2050);
				match(RPAREN);
				setState(2052);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2051);
					classBody();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2054);
				primary();
				setState(2055);
				match(DOT);
				setState(2056);
				match(NEW);
				setState(2058);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2057);
					typeArguments();
					}
				}

				setState(2060);
				annotationIdentifier();
				setState(2062);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0 || _la==LT) {
					{
					setState(2061);
					typeArgumentsOrDiamond();
					}
				}

				setState(2064);
				match(LPAREN);
				setState(2066);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2065);
					argumentList();
					}
				}

				setState(2068);
				match(RPAREN);
				setState(2070);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2069);
					classBody();
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

	public static class ClassInstanceCreationExpression_lf_primaryContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode NEW() { return getToken(Java8Parser.NEW, 0); }
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public TypeArgumentsOrDiamondContext typeArgumentsOrDiamond() {
			return getRuleContext(TypeArgumentsOrDiamondContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ClassBodyContext classBody() {
			return getRuleContext(ClassBodyContext.class,0);
		}
		public ClassInstanceCreationExpression_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classInstanceCreationExpression_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassInstanceCreationExpression_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassInstanceCreationExpression_lf_primary(this);
		}
	}

	public final ClassInstanceCreationExpression_lf_primaryContext classInstanceCreationExpression_lf_primary() throws RecognitionException {
		ClassInstanceCreationExpression_lf_primaryContext _localctx = new ClassInstanceCreationExpression_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 388, RULE_classInstanceCreationExpression_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2074);
			match(DOT);
			setState(2075);
			match(NEW);
			setState(2077);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2076);
				typeArguments();
				}
			}

			setState(2079);
			annotationIdentifier();
			setState(2081);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0 || _la==LT) {
				{
				setState(2080);
				typeArgumentsOrDiamond();
				}
			}

			setState(2083);
			match(LPAREN);
			setState(2085);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				setState(2084);
				argumentList();
				}
			}

			setState(2087);
			match(RPAREN);
			setState(2089);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,214,_ctx) ) {
			case 1:
				{
				setState(2088);
				classBody();
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

	public static class ClassInstanceCreationExpression_lfno_primaryContext extends ParserRuleContext {
		public TerminalNode NEW() { return getToken(Java8Parser.NEW, 0); }
		public List<AnnotationIdentifierContext> annotationIdentifier() {
			return getRuleContexts(AnnotationIdentifierContext.class);
		}
		public AnnotationIdentifierContext annotationIdentifier(int i) {
			return getRuleContext(AnnotationIdentifierContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public TypeArgumentsOrDiamondContext typeArgumentsOrDiamond() {
			return getRuleContext(TypeArgumentsOrDiamondContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public ClassBodyContext classBody() {
			return getRuleContext(ClassBodyContext.class,0);
		}
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public ClassInstanceCreationExpression_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classInstanceCreationExpression_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterClassInstanceCreationExpression_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitClassInstanceCreationExpression_lfno_primary(this);
		}
	}

	public final ClassInstanceCreationExpression_lfno_primaryContext classInstanceCreationExpression_lfno_primary() throws RecognitionException {
		ClassInstanceCreationExpression_lfno_primaryContext _localctx = new ClassInstanceCreationExpression_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 390, RULE_classInstanceCreationExpression_lfno_primary);
		int _la;
		try {
			setState(2132);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NEW:
				enterOuterAlt(_localctx, 1);
				{
				setState(2091);
				match(NEW);
				setState(2093);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2092);
					typeArguments();
					}
				}

				setState(2095);
				annotationIdentifier();
				setState(2100);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(2096);
					match(DOT);
					setState(2097);
					annotationIdentifier();
					}
					}
					setState(2102);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2104);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0 || _la==LT) {
					{
					setState(2103);
					typeArgumentsOrDiamond();
					}
				}

				setState(2106);
				match(LPAREN);
				setState(2108);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2107);
					argumentList();
					}
				}

				setState(2110);
				match(RPAREN);
				setState(2112);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,219,_ctx) ) {
				case 1:
					{
					setState(2111);
					classBody();
					}
					break;
				}
				}
				break;
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2114);
				expressionName();
				setState(2115);
				match(DOT);
				setState(2116);
				match(NEW);
				setState(2118);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2117);
					typeArguments();
					}
				}

				setState(2120);
				annotationIdentifier();
				setState(2122);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0 || _la==LT) {
					{
					setState(2121);
					typeArgumentsOrDiamond();
					}
				}

				setState(2124);
				match(LPAREN);
				setState(2126);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2125);
					argumentList();
					}
				}

				setState(2128);
				match(RPAREN);
				setState(2130);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,223,_ctx) ) {
				case 1:
					{
					setState(2129);
					classBody();
					}
					break;
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

	public static class TypeArgumentsOrDiamondContext extends ParserRuleContext {
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public TypeArgumentsOrDiamondContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArgumentsOrDiamond; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterTypeArgumentsOrDiamond(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitTypeArgumentsOrDiamond(this);
		}
	}

	public final TypeArgumentsOrDiamondContext typeArgumentsOrDiamond() throws RecognitionException {
		TypeArgumentsOrDiamondContext _localctx = new TypeArgumentsOrDiamondContext(_ctx, getState());
		enterRule(_localctx, 392, RULE_typeArgumentsOrDiamond);
		try {
			setState(2136);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LT:
				enterOuterAlt(_localctx, 1);
				{
				setState(2134);
				typeArguments();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(2135);
				match(T__0);
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

	public static class FieldAccessContext extends ParserRuleContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public FieldAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldAccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFieldAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFieldAccess(this);
		}
	}

	public final FieldAccessContext fieldAccess() throws RecognitionException {
		FieldAccessContext _localctx = new FieldAccessContext(_ctx, getState());
		enterRule(_localctx, 394, RULE_fieldAccess);
		try {
			setState(2151);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,226,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2138);
				primary();
				setState(2139);
				match(DOT);
				setState(2140);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2142);
				match(SUPER);
				setState(2143);
				match(DOT);
				setState(2144);
				match(Identifier);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2145);
				typeName();
				setState(2146);
				match(DOT);
				setState(2147);
				match(SUPER);
				setState(2148);
				match(DOT);
				setState(2149);
				match(Identifier);
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

	public static class FieldAccess_lf_primaryContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public FieldAccess_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldAccess_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFieldAccess_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFieldAccess_lf_primary(this);
		}
	}

	public final FieldAccess_lf_primaryContext fieldAccess_lf_primary() throws RecognitionException {
		FieldAccess_lf_primaryContext _localctx = new FieldAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 396, RULE_fieldAccess_lf_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2153);
			match(DOT);
			setState(2154);
			match(Identifier);
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

	public static class FieldAccess_lfno_primaryContext extends ParserRuleContext {
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public FieldAccess_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldAccess_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterFieldAccess_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitFieldAccess_lfno_primary(this);
		}
	}

	public final FieldAccess_lfno_primaryContext fieldAccess_lfno_primary() throws RecognitionException {
		FieldAccess_lfno_primaryContext _localctx = new FieldAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 398, RULE_fieldAccess_lfno_primary);
		try {
			setState(2165);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SUPER:
				enterOuterAlt(_localctx, 1);
				{
				setState(2156);
				match(SUPER);
				setState(2157);
				match(DOT);
				setState(2158);
				match(Identifier);
				}
				break;
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2159);
				typeName();
				setState(2160);
				match(DOT);
				setState(2161);
				match(SUPER);
				setState(2162);
				match(DOT);
				setState(2163);
				match(Identifier);
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

	public static class ArrayAccessContext extends ParserRuleContext {
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public List<TerminalNode> LBRACK() { return getTokens(Java8Parser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(Java8Parser.LBRACK, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(Java8Parser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(Java8Parser.RBRACK, i);
		}
		public PrimaryNoNewArray_lfno_arrayAccessContext primaryNoNewArray_lfno_arrayAccess() {
			return getRuleContext(PrimaryNoNewArray_lfno_arrayAccessContext.class,0);
		}
		public List<PrimaryNoNewArray_lf_arrayAccessContext> primaryNoNewArray_lf_arrayAccess() {
			return getRuleContexts(PrimaryNoNewArray_lf_arrayAccessContext.class);
		}
		public PrimaryNoNewArray_lf_arrayAccessContext primaryNoNewArray_lf_arrayAccess(int i) {
			return getRuleContext(PrimaryNoNewArray_lf_arrayAccessContext.class,i);
		}
		public ArrayAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayAccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterArrayAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitArrayAccess(this);
		}
	}

	public final ArrayAccessContext arrayAccess() throws RecognitionException {
		ArrayAccessContext _localctx = new ArrayAccessContext(_ctx, getState());
		enterRule(_localctx, 400, RULE_arrayAccess);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,228,_ctx) ) {
			case 1:
				{
				setState(2167);
				expressionName();
				setState(2168);
				match(LBRACK);
				setState(2169);
				expression();
				setState(2170);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2172);
				primaryNoNewArray_lfno_arrayAccess();
				setState(2173);
				match(LBRACK);
				setState(2174);
				expression();
				setState(2175);
				match(RBRACK);
				}
				break;
			}
			setState(2186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(2179);
				primaryNoNewArray_lf_arrayAccess();
				setState(2180);
				match(LBRACK);
				setState(2181);
				expression();
				setState(2182);
				match(RBRACK);
				}
				}
				setState(2188);
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

	public static class ArrayAccess_lf_primaryContext extends ParserRuleContext {
		public PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary() {
			return getRuleContext(PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext.class,0);
		}
		public List<TerminalNode> LBRACK() { return getTokens(Java8Parser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(Java8Parser.LBRACK, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(Java8Parser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(Java8Parser.RBRACK, i);
		}
		public List<PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext> primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary() {
			return getRuleContexts(PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext.class);
		}
		public PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(int i) {
			return getRuleContext(PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext.class,i);
		}
		public ArrayAccess_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayAccess_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterArrayAccess_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitArrayAccess_lf_primary(this);
		}
	}

	public final ArrayAccess_lf_primaryContext arrayAccess_lf_primary() throws RecognitionException {
		ArrayAccess_lf_primaryContext _localctx = new ArrayAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 402, RULE_arrayAccess_lf_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2189);
			primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary();
			setState(2190);
			match(LBRACK);
			setState(2191);
			expression();
			setState(2192);
			match(RBRACK);
			}
			setState(2201);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,230,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2194);
					primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary();
					setState(2195);
					match(LBRACK);
					setState(2196);
					expression();
					setState(2197);
					match(RBRACK);
					}
					} 
				}
				setState(2203);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,230,_ctx);
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

	public static class ArrayAccess_lfno_primaryContext extends ParserRuleContext {
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public List<TerminalNode> LBRACK() { return getTokens(Java8Parser.LBRACK); }
		public TerminalNode LBRACK(int i) {
			return getToken(Java8Parser.LBRACK, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RBRACK() { return getTokens(Java8Parser.RBRACK); }
		public TerminalNode RBRACK(int i) {
			return getToken(Java8Parser.RBRACK, i);
		}
		public PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary() {
			return getRuleContext(PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext.class,0);
		}
		public List<PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext> primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary() {
			return getRuleContexts(PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext.class);
		}
		public PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(int i) {
			return getRuleContext(PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext.class,i);
		}
		public ArrayAccess_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayAccess_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterArrayAccess_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitArrayAccess_lfno_primary(this);
		}
	}

	public final ArrayAccess_lfno_primaryContext arrayAccess_lfno_primary() throws RecognitionException {
		ArrayAccess_lfno_primaryContext _localctx = new ArrayAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 404, RULE_arrayAccess_lfno_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2214);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,231,_ctx) ) {
			case 1:
				{
				setState(2204);
				expressionName();
				setState(2205);
				match(LBRACK);
				setState(2206);
				expression();
				setState(2207);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2209);
				primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary();
				setState(2210);
				match(LBRACK);
				setState(2211);
				expression();
				setState(2212);
				match(RBRACK);
				}
				break;
			}
			setState(2223);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,232,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2216);
					primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary();
					setState(2217);
					match(LBRACK);
					setState(2218);
					expression();
					setState(2219);
					match(RBRACK);
					}
					} 
				}
				setState(2225);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,232,_ctx);
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

	public static class MethodInvocationContext extends ParserRuleContext {
		public MethodNameContext methodName() {
			return getRuleContext(MethodNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public MethodInvocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodInvocation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodInvocation(this);
		}
	}

	public final MethodInvocationContext methodInvocation() throws RecognitionException {
		MethodInvocationContext _localctx = new MethodInvocationContext(_ctx, getState());
		enterRule(_localctx, 406, RULE_methodInvocation);
		int _la;
		try {
			setState(2294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,244,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2226);
				methodName();
				setState(2227);
				match(LPAREN);
				setState(2229);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2228);
					argumentList();
					}
				}

				setState(2231);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2233);
				typeName();
				setState(2234);
				match(DOT);
				setState(2236);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2235);
					typeArguments();
					}
				}

				setState(2238);
				match(Identifier);
				setState(2239);
				match(LPAREN);
				setState(2241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2240);
					argumentList();
					}
				}

				setState(2243);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2245);
				expressionName();
				setState(2246);
				match(DOT);
				setState(2248);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2247);
					typeArguments();
					}
				}

				setState(2250);
				match(Identifier);
				setState(2251);
				match(LPAREN);
				setState(2253);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2252);
					argumentList();
					}
				}

				setState(2255);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2257);
				primary();
				setState(2258);
				match(DOT);
				setState(2260);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2259);
					typeArguments();
					}
				}

				setState(2262);
				match(Identifier);
				setState(2263);
				match(LPAREN);
				setState(2265);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2264);
					argumentList();
					}
				}

				setState(2267);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2269);
				match(SUPER);
				setState(2270);
				match(DOT);
				setState(2272);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2271);
					typeArguments();
					}
				}

				setState(2274);
				match(Identifier);
				setState(2275);
				match(LPAREN);
				setState(2277);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2276);
					argumentList();
					}
				}

				setState(2279);
				match(RPAREN);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2280);
				typeName();
				setState(2281);
				match(DOT);
				setState(2282);
				match(SUPER);
				setState(2283);
				match(DOT);
				setState(2285);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2284);
					typeArguments();
					}
				}

				setState(2287);
				match(Identifier);
				setState(2288);
				match(LPAREN);
				setState(2290);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2289);
					argumentList();
					}
				}

				setState(2292);
				match(RPAREN);
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

	public static class MethodInvocation_lf_primaryContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public MethodInvocation_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodInvocation_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodInvocation_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodInvocation_lf_primary(this);
		}
	}

	public final MethodInvocation_lf_primaryContext methodInvocation_lf_primary() throws RecognitionException {
		MethodInvocation_lf_primaryContext _localctx = new MethodInvocation_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 408, RULE_methodInvocation_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2296);
			match(DOT);
			setState(2298);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2297);
				typeArguments();
				}
			}

			setState(2300);
			match(Identifier);
			setState(2301);
			match(LPAREN);
			setState(2303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				setState(2302);
				argumentList();
				}
			}

			setState(2305);
			match(RPAREN);
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

	public static class MethodInvocation_lfno_primaryContext extends ParserRuleContext {
		public MethodNameContext methodName() {
			return getRuleContext(MethodNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public MethodInvocation_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodInvocation_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodInvocation_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodInvocation_lfno_primary(this);
		}
	}

	public final MethodInvocation_lfno_primaryContext methodInvocation_lfno_primary() throws RecognitionException {
		MethodInvocation_lfno_primaryContext _localctx = new MethodInvocation_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 410, RULE_methodInvocation_lfno_primary);
		int _la;
		try {
			setState(2363);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,256,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2307);
				methodName();
				setState(2308);
				match(LPAREN);
				setState(2310);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2309);
					argumentList();
					}
				}

				setState(2312);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2314);
				typeName();
				setState(2315);
				match(DOT);
				setState(2317);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2316);
					typeArguments();
					}
				}

				setState(2319);
				match(Identifier);
				setState(2320);
				match(LPAREN);
				setState(2322);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2321);
					argumentList();
					}
				}

				setState(2324);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2326);
				expressionName();
				setState(2327);
				match(DOT);
				setState(2329);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2328);
					typeArguments();
					}
				}

				setState(2331);
				match(Identifier);
				setState(2332);
				match(LPAREN);
				setState(2334);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2333);
					argumentList();
					}
				}

				setState(2336);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2338);
				match(SUPER);
				setState(2339);
				match(DOT);
				setState(2341);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2340);
					typeArguments();
					}
				}

				setState(2343);
				match(Identifier);
				setState(2344);
				match(LPAREN);
				setState(2346);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2345);
					argumentList();
					}
				}

				setState(2348);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2349);
				typeName();
				setState(2350);
				match(DOT);
				setState(2351);
				match(SUPER);
				setState(2352);
				match(DOT);
				setState(2354);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2353);
					typeArguments();
					}
				}

				setState(2356);
				match(Identifier);
				setState(2357);
				match(LPAREN);
				setState(2359);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (BANG - 71)) | (1L << (TILDE - 71)) | (1L << (INC - 71)) | (1L << (DEC - 71)) | (1L << (ADD - 71)) | (1L << (SUB - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
					{
					setState(2358);
					argumentList();
					}
				}

				setState(2361);
				match(RPAREN);
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

	public static class ArgumentListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterArgumentList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitArgumentList(this);
		}
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 412, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2365);
			expression();
			setState(2370);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2366);
				match(COMMA);
				setState(2367);
				expression();
				}
				}
				setState(2372);
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

	public static class MethodReferenceContext extends ParserRuleContext {
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public TerminalNode COLONCOLON() { return getToken(Java8Parser.COLONCOLON, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public TerminalNode NEW() { return getToken(Java8Parser.NEW, 0); }
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public MethodReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodReference(this);
		}
	}

	public final MethodReferenceContext methodReference() throws RecognitionException {
		MethodReferenceContext _localctx = new MethodReferenceContext(_ctx, getState());
		enterRule(_localctx, 414, RULE_methodReference);
		int _la;
		try {
			setState(2420);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,264,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2373);
				expressionName();
				setState(2374);
				match(COLONCOLON);
				setState(2376);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2375);
					typeArguments();
					}
				}

				setState(2378);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2380);
				referenceType();
				setState(2381);
				match(COLONCOLON);
				setState(2383);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2382);
					typeArguments();
					}
				}

				setState(2385);
				match(Identifier);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2387);
				primary();
				setState(2388);
				match(COLONCOLON);
				setState(2390);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2389);
					typeArguments();
					}
				}

				setState(2392);
				match(Identifier);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2394);
				match(SUPER);
				setState(2395);
				match(COLONCOLON);
				setState(2397);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2396);
					typeArguments();
					}
				}

				setState(2399);
				match(Identifier);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2400);
				typeName();
				setState(2401);
				match(DOT);
				setState(2402);
				match(SUPER);
				setState(2403);
				match(COLONCOLON);
				setState(2405);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2404);
					typeArguments();
					}
				}

				setState(2407);
				match(Identifier);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2409);
				classType();
				setState(2410);
				match(COLONCOLON);
				setState(2412);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2411);
					typeArguments();
					}
				}

				setState(2414);
				match(NEW);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2416);
				arrayType();
				setState(2417);
				match(COLONCOLON);
				setState(2418);
				match(NEW);
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

	public static class MethodReference_lf_primaryContext extends ParserRuleContext {
		public TerminalNode COLONCOLON() { return getToken(Java8Parser.COLONCOLON, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public MethodReference_lf_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodReference_lf_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodReference_lf_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodReference_lf_primary(this);
		}
	}

	public final MethodReference_lf_primaryContext methodReference_lf_primary() throws RecognitionException {
		MethodReference_lf_primaryContext _localctx = new MethodReference_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 416, RULE_methodReference_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2422);
			match(COLONCOLON);
			setState(2424);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2423);
				typeArguments();
				}
			}

			setState(2426);
			match(Identifier);
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

	public static class MethodReference_lfno_primaryContext extends ParserRuleContext {
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public TerminalNode COLONCOLON() { return getToken(Java8Parser.COLONCOLON, 0); }
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TypeArgumentsContext typeArguments() {
			return getRuleContext(TypeArgumentsContext.class,0);
		}
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public TerminalNode SUPER() { return getToken(Java8Parser.SUPER, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public TerminalNode NEW() { return getToken(Java8Parser.NEW, 0); }
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public MethodReference_lfno_primaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodReference_lfno_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMethodReference_lfno_primary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMethodReference_lfno_primary(this);
		}
	}

	public final MethodReference_lfno_primaryContext methodReference_lfno_primary() throws RecognitionException {
		MethodReference_lfno_primaryContext _localctx = new MethodReference_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 418, RULE_methodReference_lfno_primary);
		int _la;
		try {
			setState(2468);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,271,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2428);
				expressionName();
				setState(2429);
				match(COLONCOLON);
				setState(2431);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2430);
					typeArguments();
					}
				}

				setState(2433);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2435);
				referenceType();
				setState(2436);
				match(COLONCOLON);
				setState(2438);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2437);
					typeArguments();
					}
				}

				setState(2440);
				match(Identifier);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2442);
				match(SUPER);
				setState(2443);
				match(COLONCOLON);
				setState(2445);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2444);
					typeArguments();
					}
				}

				setState(2447);
				match(Identifier);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2448);
				typeName();
				setState(2449);
				match(DOT);
				setState(2450);
				match(SUPER);
				setState(2451);
				match(COLONCOLON);
				setState(2453);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2452);
					typeArguments();
					}
				}

				setState(2455);
				match(Identifier);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2457);
				classType();
				setState(2458);
				match(COLONCOLON);
				setState(2460);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2459);
					typeArguments();
					}
				}

				setState(2462);
				match(NEW);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2464);
				arrayType();
				setState(2465);
				match(COLONCOLON);
				setState(2466);
				match(NEW);
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

	public static class ArrayCreationExpressionContext extends ParserRuleContext {
		public TerminalNode NEW() { return getToken(Java8Parser.NEW, 0); }
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public DimExprsContext dimExprs() {
			return getRuleContext(DimExprsContext.class,0);
		}
		public DimsContext dims() {
			return getRuleContext(DimsContext.class,0);
		}
		public ClassOrInterfaceTypeContext classOrInterfaceType() {
			return getRuleContext(ClassOrInterfaceTypeContext.class,0);
		}
		public ArrayInitializerContext arrayInitializer() {
			return getRuleContext(ArrayInitializerContext.class,0);
		}
		public ArrayCreationExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayCreationExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterArrayCreationExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitArrayCreationExpression(this);
		}
	}

	public final ArrayCreationExpressionContext arrayCreationExpression() throws RecognitionException {
		ArrayCreationExpressionContext _localctx = new ArrayCreationExpressionContext(_ctx, getState());
		enterRule(_localctx, 420, RULE_arrayCreationExpression);
		try {
			setState(2492);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,274,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2470);
				match(NEW);
				setState(2471);
				primitiveType();
				setState(2472);
				dimExprs();
				setState(2474);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,272,_ctx) ) {
				case 1:
					{
					setState(2473);
					dims();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2476);
				match(NEW);
				setState(2477);
				classOrInterfaceType();
				setState(2478);
				dimExprs();
				setState(2480);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,273,_ctx) ) {
				case 1:
					{
					setState(2479);
					dims();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2482);
				match(NEW);
				setState(2483);
				primitiveType();
				setState(2484);
				dims();
				setState(2485);
				arrayInitializer();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2487);
				match(NEW);
				setState(2488);
				classOrInterfaceType();
				setState(2489);
				dims();
				setState(2490);
				arrayInitializer();
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

	public static class DimExprsContext extends ParserRuleContext {
		public List<DimExprContext> dimExpr() {
			return getRuleContexts(DimExprContext.class);
		}
		public DimExprContext dimExpr(int i) {
			return getRuleContext(DimExprContext.class,i);
		}
		public DimExprsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dimExprs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterDimExprs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitDimExprs(this);
		}
	}

	public final DimExprsContext dimExprs() throws RecognitionException {
		DimExprsContext _localctx = new DimExprsContext(_ctx, getState());
		enterRule(_localctx, 422, RULE_dimExprs);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2494);
			dimExpr();
			setState(2498);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,275,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2495);
					dimExpr();
					}
					} 
				}
				setState(2500);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,275,_ctx);
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

	public static class DimExprContext extends ParserRuleContext {
		public TerminalNode LBRACK() { return getToken(Java8Parser.LBRACK, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RBRACK() { return getToken(Java8Parser.RBRACK, 0); }
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public DimExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dimExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterDimExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitDimExpr(this);
		}
	}

	public final DimExprContext dimExpr() throws RecognitionException {
		DimExprContext _localctx = new DimExprContext(_ctx, getState());
		enterRule(_localctx, 424, RULE_dimExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2504);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(2501);
				annotation();
				}
				}
				setState(2506);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(2507);
			match(LBRACK);
			setState(2508);
			expression();
			setState(2509);
			match(RBRACK);
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

	public static class ConstantExpressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ConstantExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConstantExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConstantExpression(this);
		}
	}

	public final ConstantExpressionContext constantExpression() throws RecognitionException {
		ConstantExpressionContext _localctx = new ConstantExpressionContext(_ctx, getState());
		enterRule(_localctx, 426, RULE_constantExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2511);
			expression();
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

	public static class ExpressionContext extends ParserRuleContext {
		public LambdaExpressionContext lambdaExpression() {
			return getRuleContext(LambdaExpressionContext.class,0);
		}
		public AssignmentExpressionContext assignmentExpression() {
			return getRuleContext(AssignmentExpressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 428, RULE_expression);
		try {
			setState(2515);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,277,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2513);
				lambdaExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2514);
				assignmentExpression();
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

	public static class LambdaExpressionContext extends ParserRuleContext {
		public LambdaParametersContext lambdaParameters() {
			return getRuleContext(LambdaParametersContext.class,0);
		}
		public TerminalNode ARROW() { return getToken(Java8Parser.ARROW, 0); }
		public LambdaBodyContext lambdaBody() {
			return getRuleContext(LambdaBodyContext.class,0);
		}
		public LambdaExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lambdaExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLambdaExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLambdaExpression(this);
		}
	}

	public final LambdaExpressionContext lambdaExpression() throws RecognitionException {
		LambdaExpressionContext _localctx = new LambdaExpressionContext(_ctx, getState());
		enterRule(_localctx, 430, RULE_lambdaExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2517);
			lambdaParameters();
			setState(2518);
			match(ARROW);
			setState(2519);
			lambdaBody();
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

	public static class LambdaParametersContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public FormalParameterListContext formalParameterList() {
			return getRuleContext(FormalParameterListContext.class,0);
		}
		public InferredFormalParameterListContext inferredFormalParameterList() {
			return getRuleContext(InferredFormalParameterListContext.class,0);
		}
		public LambdaParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lambdaParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLambdaParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLambdaParameters(this);
		}
	}

	public final LambdaParametersContext lambdaParameters() throws RecognitionException {
		LambdaParametersContext _localctx = new LambdaParametersContext(_ctx, getState());
		enterRule(_localctx, 432, RULE_lambdaParameters);
		int _la;
		try {
			setState(2531);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,279,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2521);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2522);
				match(LPAREN);
				setState(2524);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
					{
					setState(2523);
					formalParameterList();
					}
				}

				setState(2526);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2527);
				match(LPAREN);
				setState(2528);
				inferredFormalParameterList();
				setState(2529);
				match(RPAREN);
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

	public static class InferredFormalParameterListContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(Java8Parser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(Java8Parser.Identifier, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public InferredFormalParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inferredFormalParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInferredFormalParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInferredFormalParameterList(this);
		}
	}

	public final InferredFormalParameterListContext inferredFormalParameterList() throws RecognitionException {
		InferredFormalParameterListContext _localctx = new InferredFormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 434, RULE_inferredFormalParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2533);
			match(Identifier);
			setState(2538);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2534);
				match(COMMA);
				setState(2535);
				match(Identifier);
				}
				}
				setState(2540);
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

	public static class LambdaBodyContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public LambdaBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lambdaBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLambdaBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLambdaBody(this);
		}
	}

	public final LambdaBodyContext lambdaBody() throws RecognitionException {
		LambdaBodyContext _localctx = new LambdaBodyContext(_ctx, getState());
		enterRule(_localctx, 436, RULE_lambdaBody);
		try {
			setState(2543);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BOOLEAN:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case NEW:
			case SHORT:
			case SUPER:
			case THIS:
			case VOID:
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case CharacterLiteral:
			case StringLiteral:
			case NullLiteral:
			case LPAREN:
			case BANG:
			case TILDE:
			case INC:
			case DEC:
			case ADD:
			case SUB:
			case Identifier:
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(2541);
				expression();
				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(2542);
				block();
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

	public static class AssignmentExpressionContext extends ParserRuleContext {
		public ConditionalExpressionContext conditionalExpression() {
			return getRuleContext(ConditionalExpressionContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public AssignmentExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignmentExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAssignmentExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAssignmentExpression(this);
		}
	}

	public final AssignmentExpressionContext assignmentExpression() throws RecognitionException {
		AssignmentExpressionContext _localctx = new AssignmentExpressionContext(_ctx, getState());
		enterRule(_localctx, 438, RULE_assignmentExpression);
		try {
			setState(2547);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,282,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2545);
				conditionalExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2546);
				assignment();
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

	public static class AssignmentContext extends ParserRuleContext {
		public LeftHandSideContext leftHandSide() {
			return getRuleContext(LeftHandSideContext.class,0);
		}
		public AssignmentOperatorContext assignmentOperator() {
			return getRuleContext(AssignmentOperatorContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAssignment(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 440, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2549);
			leftHandSide();
			setState(2550);
			assignmentOperator();
			setState(2551);
			expression();
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

	public static class LeftHandSideContext extends ParserRuleContext {
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public FieldAccessContext fieldAccess() {
			return getRuleContext(FieldAccessContext.class,0);
		}
		public ArrayAccessContext arrayAccess() {
			return getRuleContext(ArrayAccessContext.class,0);
		}
		public LeftHandSideContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leftHandSide; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLeftHandSide(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLeftHandSide(this);
		}
	}

	public final LeftHandSideContext leftHandSide() throws RecognitionException {
		LeftHandSideContext _localctx = new LeftHandSideContext(_ctx, getState());
		enterRule(_localctx, 442, RULE_leftHandSide);
		try {
			setState(2556);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,283,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2553);
				expressionName();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2554);
				fieldAccess();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2555);
				arrayAccess();
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

	public static class AssignmentOperatorContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(Java8Parser.ASSIGN, 0); }
		public TerminalNode MUL_ASSIGN() { return getToken(Java8Parser.MUL_ASSIGN, 0); }
		public TerminalNode DIV_ASSIGN() { return getToken(Java8Parser.DIV_ASSIGN, 0); }
		public TerminalNode MOD_ASSIGN() { return getToken(Java8Parser.MOD_ASSIGN, 0); }
		public TerminalNode ADD_ASSIGN() { return getToken(Java8Parser.ADD_ASSIGN, 0); }
		public TerminalNode SUB_ASSIGN() { return getToken(Java8Parser.SUB_ASSIGN, 0); }
		public TerminalNode LSHIFT_ASSIGN() { return getToken(Java8Parser.LSHIFT_ASSIGN, 0); }
		public TerminalNode RSHIFT_ASSIGN() { return getToken(Java8Parser.RSHIFT_ASSIGN, 0); }
		public TerminalNode URSHIFT_ASSIGN() { return getToken(Java8Parser.URSHIFT_ASSIGN, 0); }
		public TerminalNode AND_ASSIGN() { return getToken(Java8Parser.AND_ASSIGN, 0); }
		public TerminalNode XOR_ASSIGN() { return getToken(Java8Parser.XOR_ASSIGN, 0); }
		public TerminalNode OR_ASSIGN() { return getToken(Java8Parser.OR_ASSIGN, 0); }
		public AssignmentOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignmentOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAssignmentOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAssignmentOperator(this);
		}
	}

	public final AssignmentOperatorContext assignmentOperator() throws RecognitionException {
		AssignmentOperatorContext _localctx = new AssignmentOperatorContext(_ctx, getState());
		enterRule(_localctx, 444, RULE_assignmentOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2558);
			_la = _input.LA(1);
			if ( !(((((_la - 68)) & ~0x3f) == 0 && ((1L << (_la - 68)) & ((1L << (ASSIGN - 68)) | (1L << (ADD_ASSIGN - 68)) | (1L << (SUB_ASSIGN - 68)) | (1L << (MUL_ASSIGN - 68)) | (1L << (DIV_ASSIGN - 68)) | (1L << (AND_ASSIGN - 68)) | (1L << (OR_ASSIGN - 68)) | (1L << (XOR_ASSIGN - 68)) | (1L << (MOD_ASSIGN - 68)) | (1L << (LSHIFT_ASSIGN - 68)) | (1L << (RSHIFT_ASSIGN - 68)) | (1L << (URSHIFT_ASSIGN - 68)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class AdditiveOperatorContext extends ParserRuleContext {
		public TerminalNode ADD() { return getToken(Java8Parser.ADD, 0); }
		public TerminalNode SUB() { return getToken(Java8Parser.SUB, 0); }
		public AdditiveOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAdditiveOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAdditiveOperator(this);
		}
	}

	public final AdditiveOperatorContext additiveOperator() throws RecognitionException {
		AdditiveOperatorContext _localctx = new AdditiveOperatorContext(_ctx, getState());
		enterRule(_localctx, 446, RULE_additiveOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2560);
			_la = _input.LA(1);
			if ( !(_la==ADD || _la==SUB) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class RelationalOperatorContext extends ParserRuleContext {
		public TerminalNode LT() { return getToken(Java8Parser.LT, 0); }
		public TerminalNode GT() { return getToken(Java8Parser.GT, 0); }
		public TerminalNode LE() { return getToken(Java8Parser.LE, 0); }
		public TerminalNode GE() { return getToken(Java8Parser.GE, 0); }
		public TerminalNode INSTANCEOF() { return getToken(Java8Parser.INSTANCEOF, 0); }
		public RelationalOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterRelationalOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitRelationalOperator(this);
		}
	}

	public final RelationalOperatorContext relationalOperator() throws RecognitionException {
		RelationalOperatorContext _localctx = new RelationalOperatorContext(_ctx, getState());
		enterRule(_localctx, 448, RULE_relationalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2562);
			_la = _input.LA(1);
			if ( !(((((_la - 28)) & ~0x3f) == 0 && ((1L << (_la - 28)) & ((1L << (INSTANCEOF - 28)) | (1L << (GT - 28)) | (1L << (LT - 28)) | (1L << (LE - 28)) | (1L << (GE - 28)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class MultiplicativeOperatorContext extends ParserRuleContext {
		public TerminalNode MUL() { return getToken(Java8Parser.MUL, 0); }
		public TerminalNode DIV() { return getToken(Java8Parser.DIV, 0); }
		public TerminalNode MOD() { return getToken(Java8Parser.MOD, 0); }
		public MultiplicativeOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMultiplicativeOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMultiplicativeOperator(this);
		}
	}

	public final MultiplicativeOperatorContext multiplicativeOperator() throws RecognitionException {
		MultiplicativeOperatorContext _localctx = new MultiplicativeOperatorContext(_ctx, getState());
		enterRule(_localctx, 450, RULE_multiplicativeOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2564);
			_la = _input.LA(1);
			if ( !(((((_la - 85)) & ~0x3f) == 0 && ((1L << (_la - 85)) & ((1L << (MUL - 85)) | (1L << (DIV - 85)) | (1L << (MOD - 85)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class SquareBracketsContext extends ParserRuleContext {
		public SquareBracketsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_squareBrackets; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterSquareBrackets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitSquareBrackets(this);
		}
	}

	public final SquareBracketsContext squareBrackets() throws RecognitionException {
		SquareBracketsContext _localctx = new SquareBracketsContext(_ctx, getState());
		enterRule(_localctx, 452, RULE_squareBrackets);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2566);
			match(T__1);
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

	public static class ConditionalExpressionContext extends ParserRuleContext {
		public ConditionalOrExpressionContext conditionalOrExpression() {
			return getRuleContext(ConditionalOrExpressionContext.class,0);
		}
		public TerminalNode QUESTION() { return getToken(Java8Parser.QUESTION, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode COLON() { return getToken(Java8Parser.COLON, 0); }
		public ConditionalExpressionContext conditionalExpression() {
			return getRuleContext(ConditionalExpressionContext.class,0);
		}
		public LambdaExpressionContext lambdaExpression() {
			return getRuleContext(LambdaExpressionContext.class,0);
		}
		public ConditionalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConditionalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConditionalExpression(this);
		}
	}

	public final ConditionalExpressionContext conditionalExpression() throws RecognitionException {
		ConditionalExpressionContext _localctx = new ConditionalExpressionContext(_ctx, getState());
		enterRule(_localctx, 454, RULE_conditionalExpression);
		try {
			setState(2581);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,284,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2568);
				conditionalOrExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2569);
				conditionalOrExpression(0);
				setState(2570);
				match(QUESTION);
				setState(2571);
				expression();
				setState(2572);
				match(COLON);
				setState(2573);
				conditionalExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2575);
				conditionalOrExpression(0);
				setState(2576);
				match(QUESTION);
				setState(2577);
				expression();
				setState(2578);
				match(COLON);
				setState(2579);
				lambdaExpression();
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

	public static class ConditionalOrExpressionContext extends ParserRuleContext {
		public ConditionalAndExpressionContext conditionalAndExpression() {
			return getRuleContext(ConditionalAndExpressionContext.class,0);
		}
		public ConditionalOrExpressionContext conditionalOrExpression() {
			return getRuleContext(ConditionalOrExpressionContext.class,0);
		}
		public TerminalNode OR() { return getToken(Java8Parser.OR, 0); }
		public ConditionalOrExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalOrExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConditionalOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConditionalOrExpression(this);
		}
	}

	public final ConditionalOrExpressionContext conditionalOrExpression() throws RecognitionException {
		return conditionalOrExpression(0);
	}

	private ConditionalOrExpressionContext conditionalOrExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ConditionalOrExpressionContext _localctx = new ConditionalOrExpressionContext(_ctx, _parentState);
		ConditionalOrExpressionContext _prevctx = _localctx;
		int _startState = 456;
		enterRecursionRule(_localctx, 456, RULE_conditionalOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2584);
			conditionalAndExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2591);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,285,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionalOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_conditionalOrExpression);
					setState(2586);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2587);
					match(OR);
					setState(2588);
					conditionalAndExpression(0);
					}
					} 
				}
				setState(2593);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,285,_ctx);
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

	public static class ConditionalAndExpressionContext extends ParserRuleContext {
		public InclusiveOrExpressionContext inclusiveOrExpression() {
			return getRuleContext(InclusiveOrExpressionContext.class,0);
		}
		public ConditionalAndExpressionContext conditionalAndExpression() {
			return getRuleContext(ConditionalAndExpressionContext.class,0);
		}
		public TerminalNode AND() { return getToken(Java8Parser.AND, 0); }
		public ConditionalAndExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalAndExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterConditionalAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitConditionalAndExpression(this);
		}
	}

	public final ConditionalAndExpressionContext conditionalAndExpression() throws RecognitionException {
		return conditionalAndExpression(0);
	}

	private ConditionalAndExpressionContext conditionalAndExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ConditionalAndExpressionContext _localctx = new ConditionalAndExpressionContext(_ctx, _parentState);
		ConditionalAndExpressionContext _prevctx = _localctx;
		int _startState = 458;
		enterRecursionRule(_localctx, 458, RULE_conditionalAndExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2595);
			inclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2602);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,286,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionalAndExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_conditionalAndExpression);
					setState(2597);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2598);
					match(AND);
					setState(2599);
					inclusiveOrExpression(0);
					}
					} 
				}
				setState(2604);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,286,_ctx);
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

	public static class InclusiveOrExpressionContext extends ParserRuleContext {
		public ExclusiveOrExpressionContext exclusiveOrExpression() {
			return getRuleContext(ExclusiveOrExpressionContext.class,0);
		}
		public InclusiveOrExpressionContext inclusiveOrExpression() {
			return getRuleContext(InclusiveOrExpressionContext.class,0);
		}
		public TerminalNode BITOR() { return getToken(Java8Parser.BITOR, 0); }
		public InclusiveOrExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inclusiveOrExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterInclusiveOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitInclusiveOrExpression(this);
		}
	}

	public final InclusiveOrExpressionContext inclusiveOrExpression() throws RecognitionException {
		return inclusiveOrExpression(0);
	}

	private InclusiveOrExpressionContext inclusiveOrExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		InclusiveOrExpressionContext _localctx = new InclusiveOrExpressionContext(_ctx, _parentState);
		InclusiveOrExpressionContext _prevctx = _localctx;
		int _startState = 460;
		enterRecursionRule(_localctx, 460, RULE_inclusiveOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2606);
			exclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2613);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,287,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new InclusiveOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_inclusiveOrExpression);
					setState(2608);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2609);
					match(BITOR);
					setState(2610);
					exclusiveOrExpression(0);
					}
					} 
				}
				setState(2615);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,287,_ctx);
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

	public static class ExclusiveOrExpressionContext extends ParserRuleContext {
		public AndExpressionContext andExpression() {
			return getRuleContext(AndExpressionContext.class,0);
		}
		public ExclusiveOrExpressionContext exclusiveOrExpression() {
			return getRuleContext(ExclusiveOrExpressionContext.class,0);
		}
		public TerminalNode CARET() { return getToken(Java8Parser.CARET, 0); }
		public ExclusiveOrExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exclusiveOrExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterExclusiveOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitExclusiveOrExpression(this);
		}
	}

	public final ExclusiveOrExpressionContext exclusiveOrExpression() throws RecognitionException {
		return exclusiveOrExpression(0);
	}

	private ExclusiveOrExpressionContext exclusiveOrExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExclusiveOrExpressionContext _localctx = new ExclusiveOrExpressionContext(_ctx, _parentState);
		ExclusiveOrExpressionContext _prevctx = _localctx;
		int _startState = 462;
		enterRecursionRule(_localctx, 462, RULE_exclusiveOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2617);
			andExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2624);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,288,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExclusiveOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_exclusiveOrExpression);
					setState(2619);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2620);
					match(CARET);
					setState(2621);
					andExpression(0);
					}
					} 
				}
				setState(2626);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,288,_ctx);
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

	public static class AndExpressionContext extends ParserRuleContext {
		public EqualityExpressionContext equalityExpression() {
			return getRuleContext(EqualityExpressionContext.class,0);
		}
		public AndExpressionContext andExpression() {
			return getRuleContext(AndExpressionContext.class,0);
		}
		public TerminalNode BITAND() { return getToken(Java8Parser.BITAND, 0); }
		public AndExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAndExpression(this);
		}
	}

	public final AndExpressionContext andExpression() throws RecognitionException {
		return andExpression(0);
	}

	private AndExpressionContext andExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AndExpressionContext _localctx = new AndExpressionContext(_ctx, _parentState);
		AndExpressionContext _prevctx = _localctx;
		int _startState = 464;
		enterRecursionRule(_localctx, 464, RULE_andExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2628);
			equalityExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2635);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,289,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AndExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_andExpression);
					setState(2630);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2631);
					match(BITAND);
					setState(2632);
					equalityExpression(0);
					}
					} 
				}
				setState(2637);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,289,_ctx);
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

	public static class EqualityExpressionContext extends ParserRuleContext {
		public RelationalExpressionContext relationalExpression() {
			return getRuleContext(RelationalExpressionContext.class,0);
		}
		public EqualityExpressionContext equalityExpression() {
			return getRuleContext(EqualityExpressionContext.class,0);
		}
		public TerminalNode EQUAL() { return getToken(Java8Parser.EQUAL, 0); }
		public TerminalNode NOTEQUAL() { return getToken(Java8Parser.NOTEQUAL, 0); }
		public EqualityExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equalityExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterEqualityExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitEqualityExpression(this);
		}
	}

	public final EqualityExpressionContext equalityExpression() throws RecognitionException {
		return equalityExpression(0);
	}

	private EqualityExpressionContext equalityExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		EqualityExpressionContext _localctx = new EqualityExpressionContext(_ctx, _parentState);
		EqualityExpressionContext _prevctx = _localctx;
		int _startState = 466;
		enterRecursionRule(_localctx, 466, RULE_equalityExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2639);
			relationalExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2649);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,291,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2647);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,290,_ctx) ) {
					case 1:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2641);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2642);
						match(EQUAL);
						setState(2643);
						relationalExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2644);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2645);
						match(NOTEQUAL);
						setState(2646);
						relationalExpression(0);
						}
						break;
					}
					} 
				}
				setState(2651);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,291,_ctx);
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

	public static class RelationalExpressionContext extends ParserRuleContext {
		public ShiftExpressionContext shiftExpression() {
			return getRuleContext(ShiftExpressionContext.class,0);
		}
		public RelationalExpressionContext relationalExpression() {
			return getRuleContext(RelationalExpressionContext.class,0);
		}
		public RelationalOperatorContext relationalOperator() {
			return getRuleContext(RelationalOperatorContext.class,0);
		}
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public RelationalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterRelationalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitRelationalExpression(this);
		}
	}

	public final RelationalExpressionContext relationalExpression() throws RecognitionException {
		return relationalExpression(0);
	}

	private RelationalExpressionContext relationalExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		RelationalExpressionContext _localctx = new RelationalExpressionContext(_ctx, _parentState);
		RelationalExpressionContext _prevctx = _localctx;
		int _startState = 468;
		enterRecursionRule(_localctx, 468, RULE_relationalExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2653);
			shiftExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2677);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,293,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2675);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,292,_ctx) ) {
					case 1:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2655);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(2656);
						relationalOperator();
						setState(2657);
						shiftExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2659);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(2660);
						relationalOperator();
						setState(2661);
						shiftExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2663);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2664);
						relationalOperator();
						setState(2665);
						shiftExpression(0);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2667);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2668);
						relationalOperator();
						setState(2669);
						shiftExpression(0);
						}
						break;
					case 5:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2671);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2672);
						relationalOperator();
						setState(2673);
						referenceType();
						}
						break;
					}
					} 
				}
				setState(2679);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,293,_ctx);
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

	public static class ShiftExpressionContext extends ParserRuleContext {
		public AdditiveExpressionContext additiveExpression() {
			return getRuleContext(AdditiveExpressionContext.class,0);
		}
		public ShiftExpressionContext shiftExpression() {
			return getRuleContext(ShiftExpressionContext.class,0);
		}
		public ShiftOperatorContext shiftOperator() {
			return getRuleContext(ShiftOperatorContext.class,0);
		}
		public ShiftExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shiftExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterShiftExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitShiftExpression(this);
		}
	}

	public final ShiftExpressionContext shiftExpression() throws RecognitionException {
		return shiftExpression(0);
	}

	private ShiftExpressionContext shiftExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ShiftExpressionContext _localctx = new ShiftExpressionContext(_ctx, _parentState);
		ShiftExpressionContext _prevctx = _localctx;
		int _startState = 470;
		enterRecursionRule(_localctx, 470, RULE_shiftExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2681);
			additiveExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2697);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,295,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2695);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,294,_ctx) ) {
					case 1:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2683);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2684);
						shiftOperator();
						setState(2685);
						additiveExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2687);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2688);
						shiftOperator();
						setState(2689);
						additiveExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2691);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2692);
						shiftOperator();
						setState(2693);
						additiveExpression(0);
						}
						break;
					}
					} 
				}
				setState(2699);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,295,_ctx);
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

	public static class ShiftOperatorContext extends ParserRuleContext {
		public List<TerminalNode> LT() { return getTokens(Java8Parser.LT); }
		public TerminalNode LT(int i) {
			return getToken(Java8Parser.LT, i);
		}
		public List<TerminalNode> GT() { return getTokens(Java8Parser.GT); }
		public TerminalNode GT(int i) {
			return getToken(Java8Parser.GT, i);
		}
		public ShiftOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shiftOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterShiftOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitShiftOperator(this);
		}
	}

	public final ShiftOperatorContext shiftOperator() throws RecognitionException {
		ShiftOperatorContext _localctx = new ShiftOperatorContext(_ctx, getState());
		enterRule(_localctx, 472, RULE_shiftOperator);
		try {
			setState(2707);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,296,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2700);
				match(LT);
				setState(2701);
				match(LT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2702);
				match(GT);
				setState(2703);
				match(GT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2704);
				match(GT);
				setState(2705);
				match(GT);
				setState(2706);
				match(GT);
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

	public static class AdditiveExpressionContext extends ParserRuleContext {
		public MultiplicativeExpressionContext multiplicativeExpression() {
			return getRuleContext(MultiplicativeExpressionContext.class,0);
		}
		public AdditiveExpressionContext additiveExpression() {
			return getRuleContext(AdditiveExpressionContext.class,0);
		}
		public AdditiveOperatorContext additiveOperator() {
			return getRuleContext(AdditiveOperatorContext.class,0);
		}
		public AdditiveExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterAdditiveExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitAdditiveExpression(this);
		}
	}

	public final AdditiveExpressionContext additiveExpression() throws RecognitionException {
		return additiveExpression(0);
	}

	private AdditiveExpressionContext additiveExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AdditiveExpressionContext _localctx = new AdditiveExpressionContext(_ctx, _parentState);
		AdditiveExpressionContext _prevctx = _localctx;
		int _startState = 474;
		enterRecursionRule(_localctx, 474, RULE_additiveExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2710);
			multiplicativeExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2722);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,298,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2720);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,297,_ctx) ) {
					case 1:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2712);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2713);
						additiveOperator();
						setState(2714);
						multiplicativeExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2716);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2717);
						additiveOperator();
						setState(2718);
						multiplicativeExpression(0);
						}
						break;
					}
					} 
				}
				setState(2724);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,298,_ctx);
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

	public static class MultiplicativeExpressionContext extends ParserRuleContext {
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public MultiplicativeExpressionContext multiplicativeExpression() {
			return getRuleContext(MultiplicativeExpressionContext.class,0);
		}
		public MultiplicativeOperatorContext multiplicativeOperator() {
			return getRuleContext(MultiplicativeOperatorContext.class,0);
		}
		public MultiplicativeExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterMultiplicativeExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitMultiplicativeExpression(this);
		}
	}

	public final MultiplicativeExpressionContext multiplicativeExpression() throws RecognitionException {
		return multiplicativeExpression(0);
	}

	private MultiplicativeExpressionContext multiplicativeExpression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		MultiplicativeExpressionContext _localctx = new MultiplicativeExpressionContext(_ctx, _parentState);
		MultiplicativeExpressionContext _prevctx = _localctx;
		int _startState = 476;
		enterRecursionRule(_localctx, 476, RULE_multiplicativeExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2726);
			unaryExpression();
			}
			_ctx.stop = _input.LT(-1);
			setState(2742);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,300,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2740);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,299,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2728);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2729);
						multiplicativeOperator();
						setState(2730);
						unaryExpression();
						}
						break;
					case 2:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2732);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2733);
						multiplicativeOperator();
						setState(2734);
						unaryExpression();
						}
						break;
					case 3:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2736);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2737);
						multiplicativeOperator();
						setState(2738);
						unaryExpression();
						}
						break;
					}
					} 
				}
				setState(2744);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,300,_ctx);
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

	public static class UnaryExpressionContext extends ParserRuleContext {
		public PreIncrementExpressionContext preIncrementExpression() {
			return getRuleContext(PreIncrementExpressionContext.class,0);
		}
		public PreDecrementExpressionContext preDecrementExpression() {
			return getRuleContext(PreDecrementExpressionContext.class,0);
		}
		public AdditiveOperatorContext additiveOperator() {
			return getRuleContext(AdditiveOperatorContext.class,0);
		}
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public UnaryExpressionNotPlusMinusContext unaryExpressionNotPlusMinus() {
			return getRuleContext(UnaryExpressionNotPlusMinusContext.class,0);
		}
		public UnaryExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnaryExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnaryExpression(this);
		}
	}

	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 478, RULE_unaryExpression);
		try {
			setState(2754);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,301,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2745);
				preIncrementExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2746);
				preDecrementExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2747);
				additiveOperator();
				setState(2748);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2750);
				additiveOperator();
				setState(2751);
				unaryExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2753);
				unaryExpressionNotPlusMinus();
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

	public static class PreIncrementExpressionContext extends ParserRuleContext {
		public TerminalNode INC() { return getToken(Java8Parser.INC, 0); }
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public PreIncrementExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_preIncrementExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPreIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPreIncrementExpression(this);
		}
	}

	public final PreIncrementExpressionContext preIncrementExpression() throws RecognitionException {
		PreIncrementExpressionContext _localctx = new PreIncrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 480, RULE_preIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2756);
			match(INC);
			setState(2757);
			unaryExpression();
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

	public static class PreDecrementExpressionContext extends ParserRuleContext {
		public TerminalNode DEC() { return getToken(Java8Parser.DEC, 0); }
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public PreDecrementExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_preDecrementExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPreDecrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPreDecrementExpression(this);
		}
	}

	public final PreDecrementExpressionContext preDecrementExpression() throws RecognitionException {
		PreDecrementExpressionContext _localctx = new PreDecrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 482, RULE_preDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2759);
			match(DEC);
			setState(2760);
			unaryExpression();
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

	public static class UnaryExpressionNotPlusMinusContext extends ParserRuleContext {
		public PostfixExpressionContext postfixExpression() {
			return getRuleContext(PostfixExpressionContext.class,0);
		}
		public TerminalNode TILDE() { return getToken(Java8Parser.TILDE, 0); }
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public TerminalNode BANG() { return getToken(Java8Parser.BANG, 0); }
		public CastExpressionContext castExpression() {
			return getRuleContext(CastExpressionContext.class,0);
		}
		public UnaryExpressionNotPlusMinusContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpressionNotPlusMinus; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterUnaryExpressionNotPlusMinus(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitUnaryExpressionNotPlusMinus(this);
		}
	}

	public final UnaryExpressionNotPlusMinusContext unaryExpressionNotPlusMinus() throws RecognitionException {
		UnaryExpressionNotPlusMinusContext _localctx = new UnaryExpressionNotPlusMinusContext(_ctx, getState());
		enterRule(_localctx, 484, RULE_unaryExpressionNotPlusMinus);
		try {
			setState(2768);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,302,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2762);
				postfixExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2763);
				match(TILDE);
				setState(2764);
				unaryExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2765);
				match(BANG);
				setState(2766);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2767);
				castExpression();
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

	public static class PostfixExpressionContext extends ParserRuleContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public List<PostIncrementExpression_lf_postfixExpressionContext> postIncrementExpression_lf_postfixExpression() {
			return getRuleContexts(PostIncrementExpression_lf_postfixExpressionContext.class);
		}
		public PostIncrementExpression_lf_postfixExpressionContext postIncrementExpression_lf_postfixExpression(int i) {
			return getRuleContext(PostIncrementExpression_lf_postfixExpressionContext.class,i);
		}
		public List<PostDecrementExpression_lf_postfixExpressionContext> postDecrementExpression_lf_postfixExpression() {
			return getRuleContexts(PostDecrementExpression_lf_postfixExpressionContext.class);
		}
		public PostDecrementExpression_lf_postfixExpressionContext postDecrementExpression_lf_postfixExpression(int i) {
			return getRuleContext(PostDecrementExpression_lf_postfixExpressionContext.class,i);
		}
		public PostfixExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postfixExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPostfixExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPostfixExpression(this);
		}
	}

	public final PostfixExpressionContext postfixExpression() throws RecognitionException {
		PostfixExpressionContext _localctx = new PostfixExpressionContext(_ctx, getState());
		enterRule(_localctx, 486, RULE_postfixExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2772);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,303,_ctx) ) {
			case 1:
				{
				setState(2770);
				primary();
				}
				break;
			case 2:
				{
				setState(2771);
				expressionName();
				}
				break;
			}
			setState(2778);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,305,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(2776);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case INC:
						{
						setState(2774);
						postIncrementExpression_lf_postfixExpression();
						}
						break;
					case DEC:
						{
						setState(2775);
						postDecrementExpression_lf_postfixExpression();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(2780);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,305,_ctx);
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

	public static class PostIncrementExpressionContext extends ParserRuleContext {
		public PostfixExpressionContext postfixExpression() {
			return getRuleContext(PostfixExpressionContext.class,0);
		}
		public TerminalNode INC() { return getToken(Java8Parser.INC, 0); }
		public PostIncrementExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postIncrementExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPostIncrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPostIncrementExpression(this);
		}
	}

	public final PostIncrementExpressionContext postIncrementExpression() throws RecognitionException {
		PostIncrementExpressionContext _localctx = new PostIncrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 488, RULE_postIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2781);
			postfixExpression();
			setState(2782);
			match(INC);
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

	public static class PostIncrementExpression_lf_postfixExpressionContext extends ParserRuleContext {
		public TerminalNode INC() { return getToken(Java8Parser.INC, 0); }
		public PostIncrementExpression_lf_postfixExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postIncrementExpression_lf_postfixExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPostIncrementExpression_lf_postfixExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPostIncrementExpression_lf_postfixExpression(this);
		}
	}

	public final PostIncrementExpression_lf_postfixExpressionContext postIncrementExpression_lf_postfixExpression() throws RecognitionException {
		PostIncrementExpression_lf_postfixExpressionContext _localctx = new PostIncrementExpression_lf_postfixExpressionContext(_ctx, getState());
		enterRule(_localctx, 490, RULE_postIncrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2784);
			match(INC);
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

	public static class PostDecrementExpressionContext extends ParserRuleContext {
		public PostfixExpressionContext postfixExpression() {
			return getRuleContext(PostfixExpressionContext.class,0);
		}
		public TerminalNode DEC() { return getToken(Java8Parser.DEC, 0); }
		public PostDecrementExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postDecrementExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPostDecrementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPostDecrementExpression(this);
		}
	}

	public final PostDecrementExpressionContext postDecrementExpression() throws RecognitionException {
		PostDecrementExpressionContext _localctx = new PostDecrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 492, RULE_postDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2786);
			postfixExpression();
			setState(2787);
			match(DEC);
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

	public static class PostDecrementExpression_lf_postfixExpressionContext extends ParserRuleContext {
		public TerminalNode DEC() { return getToken(Java8Parser.DEC, 0); }
		public PostDecrementExpression_lf_postfixExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postDecrementExpression_lf_postfixExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterPostDecrementExpression_lf_postfixExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitPostDecrementExpression_lf_postfixExpression(this);
		}
	}

	public final PostDecrementExpression_lf_postfixExpressionContext postDecrementExpression_lf_postfixExpression() throws RecognitionException {
		PostDecrementExpression_lf_postfixExpressionContext _localctx = new PostDecrementExpression_lf_postfixExpressionContext(_ctx, getState());
		enterRule(_localctx, 494, RULE_postDecrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2789);
			match(DEC);
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

	public static class CastExpressionContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(Java8Parser.LPAREN, 0); }
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(Java8Parser.RPAREN, 0); }
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public UnaryExpressionNotPlusMinusContext unaryExpressionNotPlusMinus() {
			return getRuleContext(UnaryExpressionNotPlusMinusContext.class,0);
		}
		public List<AdditionalBoundContext> additionalBound() {
			return getRuleContexts(AdditionalBoundContext.class);
		}
		public AdditionalBoundContext additionalBound(int i) {
			return getRuleContext(AdditionalBoundContext.class,i);
		}
		public LambdaExpressionContext lambdaExpression() {
			return getRuleContext(LambdaExpressionContext.class,0);
		}
		public CastExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_castExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterCastExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitCastExpression(this);
		}
	}

	public final CastExpressionContext castExpression() throws RecognitionException {
		CastExpressionContext _localctx = new CastExpressionContext(_ctx, getState());
		enterRule(_localctx, 496, RULE_castExpression);
		int _la;
		try {
			setState(2818);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,308,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2791);
				match(LPAREN);
				setState(2792);
				primitiveType();
				setState(2793);
				match(RPAREN);
				setState(2794);
				unaryExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2796);
				match(LPAREN);
				setState(2797);
				referenceType();
				setState(2801);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2798);
					additionalBound();
					}
					}
					setState(2803);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2804);
				match(RPAREN);
				setState(2805);
				unaryExpressionNotPlusMinus();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2807);
				match(LPAREN);
				setState(2808);
				referenceType();
				setState(2812);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2809);
					additionalBound();
					}
					}
					setState(2814);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2815);
				match(RPAREN);
				setState(2816);
				lambdaExpression();
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 26:
			return packageName_sempred((PackageNameContext)_localctx, predIndex);
		case 28:
			return packageOrTypeName_sempred((PackageOrTypeNameContext)_localctx, predIndex);
		case 31:
			return ambiguousName_sempred((AmbiguousNameContext)_localctx, predIndex);
		case 228:
			return conditionalOrExpression_sempred((ConditionalOrExpressionContext)_localctx, predIndex);
		case 229:
			return conditionalAndExpression_sempred((ConditionalAndExpressionContext)_localctx, predIndex);
		case 230:
			return inclusiveOrExpression_sempred((InclusiveOrExpressionContext)_localctx, predIndex);
		case 231:
			return exclusiveOrExpression_sempred((ExclusiveOrExpressionContext)_localctx, predIndex);
		case 232:
			return andExpression_sempred((AndExpressionContext)_localctx, predIndex);
		case 233:
			return equalityExpression_sempred((EqualityExpressionContext)_localctx, predIndex);
		case 234:
			return relationalExpression_sempred((RelationalExpressionContext)_localctx, predIndex);
		case 235:
			return shiftExpression_sempred((ShiftExpressionContext)_localctx, predIndex);
		case 237:
			return additiveExpression_sempred((AdditiveExpressionContext)_localctx, predIndex);
		case 238:
			return multiplicativeExpression_sempred((MultiplicativeExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean packageName_sempred(PackageNameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean packageOrTypeName_sempred(PackageOrTypeNameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean ambiguousName_sempred(AmbiguousNameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean conditionalOrExpression_sempred(ConditionalOrExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean conditionalAndExpression_sempred(ConditionalAndExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean inclusiveOrExpression_sempred(InclusiveOrExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean exclusiveOrExpression_sempred(ExclusiveOrExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean andExpression_sempred(AndExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean equalityExpression_sempred(EqualityExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 2);
		case 9:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean relationalExpression_sempred(RelationalExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 10:
			return precpred(_ctx, 5);
		case 11:
			return precpred(_ctx, 4);
		case 12:
			return precpred(_ctx, 3);
		case 13:
			return precpred(_ctx, 2);
		case 14:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean shiftExpression_sempred(ShiftExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 15:
			return precpred(_ctx, 3);
		case 16:
			return precpred(_ctx, 2);
		case 17:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean additiveExpression_sempred(AdditiveExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 18:
			return precpred(_ctx, 2);
		case 19:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean multiplicativeExpression_sempred(MultiplicativeExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 20:
			return precpred(_ctx, 3);
		case 21:
			return precpred(_ctx, 2);
		case 22:
			return precpred(_ctx, 1);
		}
		return true;
	}

	private static final String _serializedATNSegment0 =
		"\u0004\u0001o\u0b05\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007@\u0002"+
		"A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007E\u0002"+
		"F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007J\u0002"+
		"K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007N\u0002O\u0007O\u0002"+
		"P\u0007P\u0002Q\u0007Q\u0002R\u0007R\u0002S\u0007S\u0002T\u0007T\u0002"+
		"U\u0007U\u0002V\u0007V\u0002W\u0007W\u0002X\u0007X\u0002Y\u0007Y\u0002"+
		"Z\u0007Z\u0002[\u0007[\u0002\\\u0007\\\u0002]\u0007]\u0002^\u0007^\u0002"+
		"_\u0007_\u0002`\u0007`\u0002a\u0007a\u0002b\u0007b\u0002c\u0007c\u0002"+
		"d\u0007d\u0002e\u0007e\u0002f\u0007f\u0002g\u0007g\u0002h\u0007h\u0002"+
		"i\u0007i\u0002j\u0007j\u0002k\u0007k\u0002l\u0007l\u0002m\u0007m\u0002"+
		"n\u0007n\u0002o\u0007o\u0002p\u0007p\u0002q\u0007q\u0002r\u0007r\u0002"+
		"s\u0007s\u0002t\u0007t\u0002u\u0007u\u0002v\u0007v\u0002w\u0007w\u0002"+
		"x\u0007x\u0002y\u0007y\u0002z\u0007z\u0002{\u0007{\u0002|\u0007|\u0002"+
		"}\u0007}\u0002~\u0007~\u0002\u007f\u0007\u007f\u0002\u0080\u0007\u0080"+
		"\u0002\u0081\u0007\u0081\u0002\u0082\u0007\u0082\u0002\u0083\u0007\u0083"+
		"\u0002\u0084\u0007\u0084\u0002\u0085\u0007\u0085\u0002\u0086\u0007\u0086"+
		"\u0002\u0087\u0007\u0087\u0002\u0088\u0007\u0088\u0002\u0089\u0007\u0089"+
		"\u0002\u008a\u0007\u008a\u0002\u008b\u0007\u008b\u0002\u008c\u0007\u008c"+
		"\u0002\u008d\u0007\u008d\u0002\u008e\u0007\u008e\u0002\u008f\u0007\u008f"+
		"\u0002\u0090\u0007\u0090\u0002\u0091\u0007\u0091\u0002\u0092\u0007\u0092"+
		"\u0002\u0093\u0007\u0093\u0002\u0094\u0007\u0094\u0002\u0095\u0007\u0095"+
		"\u0002\u0096\u0007\u0096\u0002\u0097\u0007\u0097\u0002\u0098\u0007\u0098"+
		"\u0002\u0099\u0007\u0099\u0002\u009a\u0007\u009a\u0002\u009b\u0007\u009b"+
		"\u0002\u009c\u0007\u009c\u0002\u009d\u0007\u009d\u0002\u009e\u0007\u009e"+
		"\u0002\u009f\u0007\u009f\u0002\u00a0\u0007\u00a0\u0002\u00a1\u0007\u00a1"+
		"\u0002\u00a2\u0007\u00a2\u0002\u00a3\u0007\u00a3\u0002\u00a4\u0007\u00a4"+
		"\u0002\u00a5\u0007\u00a5\u0002\u00a6\u0007\u00a6\u0002\u00a7\u0007\u00a7"+
		"\u0002\u00a8\u0007\u00a8\u0002\u00a9\u0007\u00a9\u0002\u00aa\u0007\u00aa"+
		"\u0002\u00ab\u0007\u00ab\u0002\u00ac\u0007\u00ac\u0002\u00ad\u0007\u00ad"+
		"\u0002\u00ae\u0007\u00ae\u0002\u00af\u0007\u00af\u0002\u00b0\u0007\u00b0"+
		"\u0002\u00b1\u0007\u00b1\u0002\u00b2\u0007\u00b2\u0002\u00b3\u0007\u00b3"+
		"\u0002\u00b4\u0007\u00b4\u0002\u00b5\u0007\u00b5\u0002\u00b6\u0007\u00b6"+
		"\u0002\u00b7\u0007\u00b7\u0002\u00b8\u0007\u00b8\u0002\u00b9\u0007\u00b9"+
		"\u0002\u00ba\u0007\u00ba\u0002\u00bb\u0007\u00bb\u0002\u00bc\u0007\u00bc"+
		"\u0002\u00bd\u0007\u00bd\u0002\u00be\u0007\u00be\u0002\u00bf\u0007\u00bf"+
		"\u0002\u00c0\u0007\u00c0\u0002\u00c1\u0007\u00c1\u0002\u00c2\u0007\u00c2"+
		"\u0002\u00c3\u0007\u00c3\u0002\u00c4\u0007\u00c4\u0002\u00c5\u0007\u00c5"+
		"\u0002\u00c6\u0007\u00c6\u0002\u00c7\u0007\u00c7\u0002\u00c8\u0007\u00c8"+
		"\u0002\u00c9\u0007\u00c9\u0002\u00ca\u0007\u00ca\u0002\u00cb\u0007\u00cb"+
		"\u0002\u00cc\u0007\u00cc\u0002\u00cd\u0007\u00cd\u0002\u00ce\u0007\u00ce"+
		"\u0002\u00cf\u0007\u00cf\u0002\u00d0\u0007\u00d0\u0002\u00d1\u0007\u00d1"+
		"\u0002\u00d2\u0007\u00d2\u0002\u00d3\u0007\u00d3\u0002\u00d4\u0007\u00d4"+
		"\u0002\u00d5\u0007\u00d5\u0002\u00d6\u0007\u00d6\u0002\u00d7\u0007\u00d7"+
		"\u0002\u00d8\u0007\u00d8\u0002\u00d9\u0007\u00d9\u0002\u00da\u0007\u00da"+
		"\u0002\u00db\u0007\u00db\u0002\u00dc\u0007\u00dc\u0002\u00dd\u0007\u00dd"+
		"\u0002\u00de\u0007\u00de\u0002\u00df\u0007\u00df\u0002\u00e0\u0007\u00e0"+
		"\u0002\u00e1\u0007\u00e1\u0002\u00e2\u0007\u00e2\u0002\u00e3\u0007\u00e3"+
		"\u0002\u00e4\u0007\u00e4\u0002\u00e5\u0007\u00e5\u0002\u00e6\u0007\u00e6"+
		"\u0002\u00e7\u0007\u00e7\u0002\u00e8\u0007\u00e8\u0002\u00e9\u0007\u00e9"+
		"\u0002\u00ea\u0007\u00ea\u0002\u00eb\u0007\u00eb\u0002\u00ec\u0007\u00ec"+
		"\u0002\u00ed\u0007\u00ed\u0002\u00ee\u0007\u00ee\u0002\u00ef\u0007\u00ef"+
		"\u0002\u00f0\u0007\u00f0\u0002\u00f1\u0007\u00f1\u0002\u00f2\u0007\u00f2"+
		"\u0002\u00f3\u0007\u00f3\u0002\u00f4\u0007\u00f4\u0002\u00f5\u0007\u00f5"+
		"\u0002\u00f6\u0007\u00f6\u0002\u00f7\u0007\u00f7\u0002\u00f8\u0007\u00f8"+
		"\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u01f7\b\u0001"+
		"\u0001\u0002\u0005\u0002\u01fa\b\u0002\n\u0002\f\u0002\u01fd\t\u0002\u0001"+
		"\u0002\u0001\u0002\u0005\u0002\u0201\b\u0002\n\u0002\f\u0002\u0204\t\u0002"+
		"\u0001\u0002\u0003\u0002\u0207\b\u0002\u0001\u0003\u0001\u0003\u0003\u0003"+
		"\u020b\b\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0003\u0006\u0214\b\u0006\u0001\u0007\u0001\u0007"+
		"\u0003\u0007\u0218\b\u0007\u0001\u0007\u0001\u0007\u0005\u0007\u021c\b"+
		"\u0007\n\u0007\f\u0007\u021f\t\u0007\u0001\b\u0001\b\u0003\b\u0223\b\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u0229\b\b\u0003\b\u022b\b\b\u0001"+
		"\t\u0001\t\u0001\t\u0003\t\u0230\b\t\u0001\n\u0001\n\u0003\n\u0234\b\n"+
		"\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0247\b\u000f\u0001"+
		"\u0010\u0001\u0010\u0005\u0010\u024b\b\u0010\n\u0010\f\u0010\u024e\t\u0010"+
		"\u0001\u0011\u0005\u0011\u0251\b\u0011\n\u0011\f\u0011\u0254\t\u0011\u0001"+
		"\u0011\u0001\u0011\u0003\u0011\u0258\b\u0011\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u0261"+
		"\b\u0013\n\u0013\f\u0013\u0264\t\u0013\u0003\u0013\u0266\b\u0013\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001"+
		"\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u0272\b\u0016\n"+
		"\u0016\f\u0016\u0275\t\u0016\u0001\u0017\u0001\u0017\u0003\u0017\u0279"+
		"\b\u0017\u0001\u0018\u0005\u0018\u027c\b\u0018\n\u0018\f\u0018\u027f\t"+
		"\u0018\u0001\u0018\u0001\u0018\u0003\u0018\u0283\b\u0018\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0289\b\u0019\u0001\u001a\u0001"+
		"\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a\u0291"+
		"\b\u001a\n\u001a\f\u001a\u0294\t\u001a\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0003\u001b\u029b\b\u001b\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0005\u001c\u02a3\b\u001c"+
		"\n\u001c\f\u001c\u02a6\t\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001"+
		"\u001d\u0001\u001d\u0003\u001d\u02ad\b\u001d\u0001\u001e\u0001\u001e\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0005"+
		"\u001f\u02b7\b\u001f\n\u001f\f\u001f\u02ba\t\u001f\u0001 \u0003 \u02bd"+
		"\b \u0001 \u0005 \u02c0\b \n \f \u02c3\t \u0001 \u0005 \u02c6\b \n \f"+
		" \u02c9\t \u0001 \u0001 \u0001!\u0005!\u02ce\b!\n!\f!\u02d1\t!\u0001!"+
		"\u0001!\u0001!\u0001!\u0005!\u02d7\b!\n!\f!\u02da\t!\u0001!\u0001!\u0001"+
		"\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0003#\u02e4\b#\u0001$\u0001$\u0001"+
		"$\u0001$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001&\u0001&\u0001"+
		"&\u0001&\u0001&\u0001&\u0001&\u0001\'\u0001\'\u0001\'\u0001\'\u0001\'"+
		"\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0003(\u0301\b(\u0001)\u0001)\u0003"+
		")\u0305\b)\u0001*\u0001*\u0001*\u0001*\u0003*\u030b\b*\u0001*\u0003*\u030e"+
		"\b*\u0001*\u0003*\u0311\b*\u0001*\u0001*\u0001+\u0005+\u0316\b+\n+\f+"+
		"\u0319\t+\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0003"+
		",\u0323\b,\u0001-\u0001-\u0001-\u0001-\u0001.\u0001.\u0001.\u0005.\u032c"+
		"\b.\n.\f.\u032f\t.\u0001/\u0001/\u0001/\u00010\u00010\u00010\u00011\u0001"+
		"1\u00011\u00051\u033a\b1\n1\f1\u033d\t1\u00012\u00012\u00052\u0341\b2"+
		"\n2\f2\u0344\t2\u00012\u00012\u00013\u00013\u00013\u00013\u00033\u034c"+
		"\b3\u00014\u00014\u00014\u00014\u00014\u00034\u0353\b4\u00015\u00015\u0001"+
		"5\u00015\u00015\u00016\u00056\u035b\b6\n6\f6\u035e\t6\u00017\u00017\u0001"+
		"7\u00017\u00017\u00017\u00017\u00017\u00037\u0368\b7\u00018\u00018\u0001"+
		"8\u00058\u036d\b8\n8\f8\u0370\t8\u00019\u00019\u00019\u00039\u0375\b9"+
		"\u0001:\u0001:\u0003:\u0379\b:\u0001;\u0001;\u0003;\u037d\b;\u0001<\u0001"+
		"<\u0003<\u0381\b<\u0001=\u0001=\u0003=\u0385\b=\u0001>\u0001>\u0001>\u0003"+
		">\u038a\b>\u0001?\u0001?\u0003?\u038e\b?\u0001?\u0001?\u0005?\u0392\b"+
		"?\n?\f?\u0395\t?\u0001@\u0001@\u0003@\u0399\b@\u0001@\u0001@\u0001@\u0001"+
		"@\u0003@\u039f\b@\u0003@\u03a1\b@\u0001A\u0001A\u0001A\u0003A\u03a6\b"+
		"A\u0001B\u0001B\u0003B\u03aa\bB\u0001C\u0001C\u0001D\u0001D\u0001E\u0001"+
		"E\u0001F\u0001F\u0001G\u0001G\u0001G\u0001G\u0001G\u0001G\u0001G\u0001"+
		"G\u0001G\u0003G\u03bd\bG\u0001H\u0001H\u0001H\u0001H\u0001I\u0005I\u03c4"+
		"\bI\nI\fI\u03c7\tI\u0001J\u0001J\u0001J\u0001J\u0001J\u0001J\u0001J\u0001"+
		"J\u0001J\u0001J\u0003J\u03d3\bJ\u0001K\u0001K\u0001K\u0003K\u03d8\bK\u0001"+
		"K\u0001K\u0005K\u03dc\bK\nK\fK\u03df\tK\u0001K\u0001K\u0001K\u0003K\u03e4"+
		"\bK\u0003K\u03e6\bK\u0001L\u0001L\u0003L\u03ea\bL\u0001M\u0001M\u0001"+
		"M\u0003M\u03ef\bM\u0001M\u0001M\u0003M\u03f3\bM\u0001N\u0001N\u0001N\u0001"+
		"N\u0001N\u0003N\u03fa\bN\u0001O\u0001O\u0001O\u0005O\u03ff\bO\nO\fO\u0402"+
		"\tO\u0001O\u0001O\u0001O\u0005O\u0407\bO\nO\fO\u040a\tO\u0003O\u040c\b"+
		"O\u0001P\u0005P\u040f\bP\nP\fP\u0412\tP\u0001P\u0001P\u0001P\u0001Q\u0001"+
		"Q\u0003Q\u0419\bQ\u0001R\u0005R\u041c\bR\nR\fR\u041f\tR\u0001R\u0001R"+
		"\u0005R\u0423\bR\nR\fR\u0426\tR\u0001R\u0001R\u0001R\u0001R\u0003R\u042c"+
		"\bR\u0001S\u0005S\u042f\bS\nS\fS\u0432\tS\u0001S\u0001S\u0001S\u0003S"+
		"\u0437\bS\u0001S\u0001S\u0001T\u0001T\u0001T\u0001U\u0001U\u0001U\u0005"+
		"U\u0441\bU\nU\fU\u0444\tU\u0001V\u0001V\u0003V\u0448\bV\u0001W\u0001W"+
		"\u0003W\u044c\bW\u0001X\u0001X\u0001Y\u0001Y\u0001Y\u0001Z\u0001Z\u0001"+
		"Z\u0003Z\u0456\bZ\u0001Z\u0001Z\u0001[\u0005[\u045b\b[\n[\f[\u045e\t["+
		"\u0001\\\u0001\\\u0001\\\u0001\\\u0003\\\u0464\b\\\u0001]\u0003]\u0467"+
		"\b]\u0001]\u0001]\u0001]\u0003]\u046c\b]\u0001]\u0001]\u0001^\u0001^\u0001"+
		"_\u0001_\u0003_\u0474\b_\u0001_\u0003_\u0477\b_\u0001_\u0001_\u0001`\u0003"+
		"`\u047c\b`\u0001`\u0001`\u0001`\u0003`\u0481\b`\u0001`\u0001`\u0001`\u0003"+
		"`\u0486\b`\u0001`\u0001`\u0001`\u0003`\u048b\b`\u0001`\u0001`\u0001`\u0001"+
		"`\u0001`\u0003`\u0492\b`\u0001`\u0001`\u0001`\u0003`\u0497\b`\u0001`\u0001"+
		"`\u0001`\u0001`\u0001`\u0001`\u0003`\u049f\b`\u0001`\u0001`\u0001`\u0003"+
		"`\u04a4\b`\u0001`\u0001`\u0001`\u0003`\u04a9\b`\u0001a\u0001a\u0001a\u0001"+
		"a\u0003a\u04af\ba\u0001a\u0001a\u0001b\u0001b\u0003b\u04b5\bb\u0001b\u0003"+
		"b\u04b8\bb\u0001b\u0003b\u04bb\bb\u0001b\u0001b\u0001c\u0001c\u0001c\u0005"+
		"c\u04c2\bc\nc\fc\u04c5\tc\u0001d\u0005d\u04c8\bd\nd\fd\u04cb\td\u0001"+
		"d\u0001d\u0001d\u0003d\u04d0\bd\u0001d\u0003d\u04d3\bd\u0001d\u0003d\u04d6"+
		"\bd\u0001e\u0001e\u0001f\u0001f\u0005f\u04dc\bf\nf\ff\u04df\tf\u0001g"+
		"\u0001g\u0003g\u04e3\bg\u0001h\u0001h\u0001h\u0001h\u0003h\u04e9\bh\u0001"+
		"h\u0003h\u04ec\bh\u0001h\u0001h\u0001i\u0005i\u04f1\bi\ni\fi\u04f4\ti"+
		"\u0001j\u0001j\u0001j\u0001j\u0001j\u0001j\u0001j\u0003j\u04fd\bj\u0001"+
		"k\u0001k\u0001k\u0001l\u0001l\u0005l\u0504\bl\nl\fl\u0507\tl\u0001l\u0001"+
		"l\u0001m\u0001m\u0001m\u0001m\u0001m\u0003m\u0510\bm\u0001n\u0005n\u0513"+
		"\bn\nn\fn\u0516\tn\u0001n\u0001n\u0001n\u0001n\u0001o\u0001o\u0001o\u0001"+
		"o\u0003o\u0520\bo\u0001p\u0005p\u0523\bp\np\fp\u0526\tp\u0001p\u0001p"+
		"\u0001p\u0001q\u0001q\u0001q\u0001q\u0001q\u0001q\u0003q\u0531\bq\u0001"+
		"r\u0005r\u0534\br\nr\fr\u0537\tr\u0001r\u0001r\u0001r\u0001r\u0001r\u0001"+
		"s\u0001s\u0005s\u0540\bs\ns\fs\u0543\ts\u0001s\u0001s\u0001t\u0001t\u0001"+
		"t\u0001t\u0001t\u0003t\u054c\bt\u0001u\u0005u\u054f\bu\nu\fu\u0552\tu"+
		"\u0001u\u0001u\u0001u\u0001u\u0001u\u0003u\u0559\bu\u0001u\u0003u\u055c"+
		"\bu\u0001u\u0001u\u0001v\u0001v\u0001v\u0003v\u0563\bv\u0001w\u0001w\u0001"+
		"w\u0001x\u0001x\u0001x\u0003x\u056b\bx\u0001y\u0005y\u056e\by\ny\fy\u0571"+
		"\ty\u0001y\u0001y\u0001z\u0005z\u0576\bz\nz\fz\u0579\tz\u0001z\u0001z"+
		"\u0001{\u0001{\u0001{\u0001{\u0003{\u0581\b{\u0001{\u0001{\u0001|\u0001"+
		"|\u0001|\u0005|\u0588\b|\n|\f|\u058b\t|\u0001}\u0001}\u0001}\u0001}\u0001"+
		"~\u0001~\u0001~\u0003~\u0594\b~\u0001\u007f\u0001\u007f\u0003\u007f\u0598"+
		"\b\u007f\u0001\u007f\u0003\u007f\u059b\b\u007f\u0001\u007f\u0001\u007f"+
		"\u0001\u0080\u0001\u0080\u0001\u0080\u0005\u0080\u05a2\b\u0080\n\u0080"+
		"\f\u0080\u05a5\t\u0080\u0001\u0081\u0001\u0081\u0001\u0081\u0001\u0082"+
		"\u0001\u0082\u0001\u0082\u0001\u0082\u0001\u0082\u0001\u0082\u0001\u0083"+
		"\u0001\u0083\u0003\u0083\u05b2\b\u0083\u0001\u0083\u0003\u0083\u05b5\b"+
		"\u0083\u0001\u0083\u0001\u0083\u0001\u0084\u0001\u0084\u0001\u0084\u0005"+
		"\u0084\u05bc\b\u0084\n\u0084\f\u0084\u05bf\t\u0084\u0001\u0085\u0001\u0085"+
		"\u0003\u0085\u05c3\b\u0085\u0001\u0085\u0001\u0085\u0001\u0086\u0001\u0086"+
		"\u0005\u0086\u05c9\b\u0086\n\u0086\f\u0086\u05cc\t\u0086\u0001\u0087\u0001"+
		"\u0087\u0001\u0087\u0003\u0087\u05d1\b\u0087\u0001\u0088\u0001\u0088\u0001"+
		"\u0088\u0001\u0089\u0005\u0089\u05d7\b\u0089\n\u0089\f\u0089\u05da\t\u0089"+
		"\u0001\u0089\u0001\u0089\u0001\u0089\u0001\u008a\u0001\u008a\u0001\u008a"+
		"\u0001\u008a\u0001\u008a\u0001\u008a\u0003\u008a\u05e5\b\u008a\u0001\u008b"+
		"\u0001\u008b\u0001\u008b\u0001\u008b\u0001\u008b\u0003\u008b\u05ec\b\u008b"+
		"\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c"+
		"\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c\u0001\u008c"+
		"\u0003\u008c\u05fa\b\u008c\u0001\u008d\u0001\u008d\u0001\u008e\u0001\u008e"+
		"\u0001\u008e\u0001\u008e\u0001\u008f\u0001\u008f\u0001\u008f\u0001\u008f"+
		"\u0001\u0090\u0001\u0090\u0001\u0090\u0001\u0091\u0001\u0091\u0001\u0091"+
		"\u0001\u0091\u0001\u0091\u0001\u0091\u0001\u0091\u0003\u0091\u0610\b\u0091"+
		"\u0001\u0092\u0001\u0092\u0001\u0092\u0001\u0092\u0001\u0092\u0001\u0092"+
		"\u0001\u0093\u0001\u0093\u0001\u0093\u0001\u0093\u0001\u0093\u0001\u0093"+
		"\u0001\u0093\u0001\u0093\u0001\u0094\u0001\u0094\u0001\u0094\u0001\u0094"+
		"\u0001\u0094\u0001\u0094\u0001\u0094\u0001\u0094\u0001\u0095\u0001\u0095"+
		"\u0001\u0095\u0001\u0095\u0001\u0095\u0001\u0095\u0001\u0095\u0001\u0095"+
		"\u0001\u0095\u0001\u0095\u0003\u0095\u0632\b\u0095\u0001\u0096\u0001\u0096"+
		"\u0001\u0096\u0001\u0096\u0001\u0096\u0001\u0096\u0001\u0097\u0001\u0097"+
		"\u0005\u0097\u063c\b\u0097\n\u0097\f\u0097\u063f\t\u0097\u0001\u0097\u0005"+
		"\u0097\u0642\b\u0097\n\u0097\f\u0097\u0645\t\u0097\u0001\u0097\u0001\u0097"+
		"\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0099\u0001\u0099\u0005\u0099"+
		"\u064e\b\u0099\n\u0099\f\u0099\u0651\t\u0099\u0001\u009a\u0001\u009a\u0001"+
		"\u009a\u0001\u009a\u0001\u009a\u0001\u009a\u0001\u009a\u0001\u009a\u0001"+
		"\u009a\u0001\u009a\u0003\u009a\u065d\b\u009a\u0001\u009b\u0001\u009b\u0001"+
		"\u009c\u0001\u009c\u0001\u009c\u0001\u009c\u0001\u009c\u0001\u009c\u0001"+
		"\u009d\u0001\u009d\u0001\u009d\u0001\u009d\u0001\u009d\u0001\u009d\u0001"+
		"\u009e\u0001\u009e\u0001\u009e\u0001\u009e\u0001\u009e\u0001\u009e\u0001"+
		"\u009e\u0001\u009e\u0001\u009f\u0001\u009f\u0003\u009f\u0677\b\u009f\u0001"+
		"\u00a0\u0001\u00a0\u0003\u00a0\u067b\b\u00a0\u0001\u00a1\u0001\u00a1\u0001"+
		"\u00a1\u0003\u00a1\u0680\b\u00a1\u0001\u00a1\u0001\u00a1\u0003\u00a1\u0684"+
		"\b\u00a1\u0001\u00a1\u0001\u00a1\u0003\u00a1\u0688\b\u00a1\u0001\u00a1"+
		"\u0001\u00a1\u0001\u00a1\u0001\u00a2\u0001\u00a2\u0001\u00a2\u0003\u00a2"+
		"\u0690\b\u00a2\u0001\u00a2\u0001\u00a2\u0003\u00a2\u0694\b\u00a2\u0001"+
		"\u00a2\u0001\u00a2\u0003\u00a2\u0698\b\u00a2\u0001\u00a2\u0001\u00a2\u0001"+
		"\u00a2\u0001\u00a3\u0001\u00a3\u0003\u00a3\u069f\b\u00a3\u0001\u00a4\u0001"+
		"\u00a4\u0001\u00a5\u0001\u00a5\u0001\u00a5\u0005\u00a5\u06a6\b\u00a5\n"+
		"\u00a5\f\u00a5\u06a9\t\u00a5\u0001\u00a6\u0001\u00a6\u0001\u00a6\u0005"+
		"\u00a6\u06ae\b\u00a6\n\u00a6\f\u00a6\u06b1\t\u00a6\u0001\u00a6\u0001\u00a6"+
		"\u0001\u00a6\u0001\u00a6\u0001\u00a6\u0001\u00a6\u0001\u00a6\u0001\u00a7"+
		"\u0001\u00a7\u0001\u00a7\u0005\u00a7\u06bd\b\u00a7\n\u00a7\f\u00a7\u06c0"+
		"\t\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0001"+
		"\u00a7\u0001\u00a7\u0001\u00a8\u0001\u00a8\u0003\u00a8\u06cb\b\u00a8\u0001"+
		"\u00a8\u0001\u00a8\u0001\u00a9\u0001\u00a9\u0003\u00a9\u06d1\b\u00a9\u0001"+
		"\u00a9\u0001\u00a9\u0001\u00aa\u0001\u00aa\u0003\u00aa\u06d7\b\u00aa\u0001"+
		"\u00aa\u0001\u00aa\u0001\u00ab\u0001\u00ab\u0001\u00ab\u0001\u00ab\u0001"+
		"\u00ac\u0001\u00ac\u0001\u00ac\u0001\u00ac\u0001\u00ac\u0001\u00ac\u0001"+
		"\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001"+
		"\u00ad\u0003\u00ad\u06ec\b\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0003"+
		"\u00ad\u06f1\b\u00ad\u0001\u00ae\u0001\u00ae\u0005\u00ae\u06f5\b\u00ae"+
		"\n\u00ae\f\u00ae\u06f8\t\u00ae\u0001\u00af\u0001\u00af\u0001\u00af\u0001"+
		"\u00af\u0001\u00af\u0001\u00af\u0001\u00b0\u0005\u00b0\u0701\b\u00b0\n"+
		"\u00b0\f\u00b0\u0704\t\u00b0\u0001\u00b0\u0001\u00b0\u0001\u00b0\u0001"+
		"\u00b1\u0001\u00b1\u0001\u00b1\u0005\u00b1\u070c\b\u00b1\n\u00b1\f\u00b1"+
		"\u070f\t\u00b1\u0001\u00b2\u0001\u00b2\u0001\u00b2\u0001\u00b3\u0001\u00b3"+
		"\u0001\u00b3\u0001\u00b3\u0003\u00b3\u0718\b\u00b3\u0001\u00b3\u0003\u00b3"+
		"\u071b\b\u00b3\u0001\u00b4\u0001\u00b4\u0001\u00b4\u0003\u00b4\u0720\b"+
		"\u00b4\u0001\u00b4\u0001\u00b4\u0001\u00b5\u0001\u00b5\u0001\u00b5\u0005"+
		"\u00b5\u0727\b\u00b5\n\u00b5\f\u00b5\u072a\t\u00b5\u0001\u00b6\u0005\u00b6"+
		"\u072d\b\u00b6\n\u00b6\f\u00b6\u0730\t\u00b6\u0001\u00b6\u0001\u00b6\u0001"+
		"\u00b6\u0001\u00b6\u0001\u00b6\u0001\u00b7\u0001\u00b7\u0003\u00b7\u0739"+
		"\b\u00b7\u0001\u00b7\u0005\u00b7\u073c\b\u00b7\n\u00b7\f\u00b7\u073f\t"+
		"\u00b7\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0005\u00b8\u0744\b\u00b8\n"+
		"\u00b8\f\u00b8\u0747\t\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001"+
		"\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001"+
		"\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001"+
		"\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0001\u00b8\u0003\u00b8\u075d"+
		"\b\u00b8\u0001\u00b9\u0001\u00b9\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0005"+
		"\u00ba\u0764\b\u00ba\n\u00ba\f\u00ba\u0767\t\u00ba\u0001\u00ba\u0001\u00ba"+
		"\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba"+
		"\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba"+
		"\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0001\u00ba\u0003\u00ba"+
		"\u077c\b\u00ba\u0001\u00bb\u0001\u00bb\u0001\u00bb\u0001\u00bb\u0001\u00bb"+
		"\u0003\u00bb\u0783\b\u00bb\u0001\u00bc\u0001\u00bc\u0001\u00bd\u0001\u00bd"+
		"\u0001\u00bd\u0001\u00bd\u0003\u00bd\u078b\b\u00bd\u0001\u00be\u0001\u00be"+
		"\u0001\u00be\u0005\u00be\u0790\b\u00be\n\u00be\f\u00be\u0793\t\u00be\u0001"+
		"\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0005\u00be\u079a"+
		"\b\u00be\n\u00be\f\u00be\u079d\t\u00be\u0001\u00be\u0001\u00be\u0001\u00be"+
		"\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be"+
		"\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be"+
		"\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0001\u00be\u0003\u00be"+
		"\u07b3\b\u00be\u0001\u00bf\u0001\u00bf\u0001\u00c0\u0001\u00c0\u0001\u00c0"+
		"\u0005\u00c0\u07ba\b\u00c0\n\u00c0\f\u00c0\u07bd\t\u00c0\u0001\u00c0\u0001"+
		"\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0005\u00c0\u07c4\b\u00c0\n"+
		"\u00c0\f\u00c0\u07c7\t\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001"+
		"\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001"+
		"\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0001"+
		"\u00c0\u0001\u00c0\u0001\u00c0\u0001\u00c0\u0003\u00c0\u07dc\b\u00c0\u0001"+
		"\u00c1\u0001\u00c1\u0003\u00c1\u07e0\b\u00c1\u0001\u00c1\u0001\u00c1\u0001"+
		"\u00c1\u0005\u00c1\u07e5\b\u00c1\n\u00c1\f\u00c1\u07e8\t\u00c1\u0001\u00c1"+
		"\u0003\u00c1\u07eb\b\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u07ef\b"+
		"\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u07f3\b\u00c1\u0001\u00c1\u0001"+
		"\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u07f9\b\u00c1\u0001\u00c1\u0001"+
		"\u00c1\u0003\u00c1\u07fd\b\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u0801"+
		"\b\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u0805\b\u00c1\u0001\u00c1"+
		"\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u080b\b\u00c1\u0001\u00c1"+
		"\u0001\u00c1\u0003\u00c1\u080f\b\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1"+
		"\u0813\b\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u0817\b\u00c1\u0003"+
		"\u00c1\u0819\b\u00c1\u0001\u00c2\u0001\u00c2\u0001\u00c2\u0003\u00c2\u081e"+
		"\b\u00c2\u0001\u00c2\u0001\u00c2\u0003\u00c2\u0822\b\u00c2\u0001\u00c2"+
		"\u0001\u00c2\u0003\u00c2\u0826\b\u00c2\u0001\u00c2\u0001\u00c2\u0003\u00c2"+
		"\u082a\b\u00c2\u0001\u00c3\u0001\u00c3\u0003\u00c3\u082e\b\u00c3\u0001"+
		"\u00c3\u0001\u00c3\u0001\u00c3\u0005\u00c3\u0833\b\u00c3\n\u00c3\f\u00c3"+
		"\u0836\t\u00c3\u0001\u00c3\u0003\u00c3\u0839\b\u00c3\u0001\u00c3\u0001"+
		"\u00c3\u0003\u00c3\u083d\b\u00c3\u0001\u00c3\u0001\u00c3\u0003\u00c3\u0841"+
		"\b\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0001\u00c3\u0003\u00c3\u0847"+
		"\b\u00c3\u0001\u00c3\u0001\u00c3\u0003\u00c3\u084b\b\u00c3\u0001\u00c3"+
		"\u0001\u00c3\u0003\u00c3\u084f\b\u00c3\u0001\u00c3\u0001\u00c3\u0003\u00c3"+
		"\u0853\b\u00c3\u0003\u00c3\u0855\b\u00c3\u0001\u00c4\u0001\u00c4\u0003"+
		"\u00c4\u0859\b\u00c4\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001"+
		"\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001"+
		"\u00c5\u0001\u00c5\u0001\u00c5\u0003\u00c5\u0868\b\u00c5\u0001\u00c6\u0001"+
		"\u00c6\u0001\u00c6\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001"+
		"\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0003\u00c7\u0876"+
		"\b\u00c7\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001"+
		"\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u0882"+
		"\b\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8\u0005"+
		"\u00c8\u0889\b\u00c8\n\u00c8\f\u00c8\u088c\t\u00c8\u0001\u00c9\u0001\u00c9"+
		"\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0001\u00c9"+
		"\u0001\u00c9\u0001\u00c9\u0005\u00c9\u0898\b\u00c9\n\u00c9\f\u00c9\u089b"+
		"\t\u00c9\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0001"+
		"\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0003\u00ca\u08a7"+
		"\b\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0005"+
		"\u00ca\u08ae\b\u00ca\n\u00ca\f\u00ca\u08b1\t\u00ca\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0003\u00cb\u08b6\b\u00cb\u0001\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0001\u00cb\u0003\u00cb\u08bd\b\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0003\u00cb\u08c2\b\u00cb\u0001\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0001\u00cb\u0003\u00cb\u08c9\b\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0003\u00cb\u08ce\b\u00cb\u0001\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0001\u00cb\u0003\u00cb\u08d5\b\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0003\u00cb\u08da\b\u00cb\u0001\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0001\u00cb\u0003\u00cb\u08e1\b\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0003\u00cb\u08e6\b\u00cb\u0001\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0001\u00cb\u0001\u00cb\u0003\u00cb\u08ee\b\u00cb\u0001\u00cb"+
		"\u0001\u00cb\u0001\u00cb\u0003\u00cb\u08f3\b\u00cb\u0001\u00cb\u0001\u00cb"+
		"\u0003\u00cb\u08f7\b\u00cb\u0001\u00cc\u0001\u00cc\u0003\u00cc\u08fb\b"+
		"\u00cc\u0001\u00cc\u0001\u00cc\u0001\u00cc\u0003\u00cc\u0900\b\u00cc\u0001"+
		"\u00cc\u0001\u00cc\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003\u00cd\u0907"+
		"\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003"+
		"\u00cd\u090e\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003\u00cd\u0913"+
		"\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003"+
		"\u00cd\u091a\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003\u00cd\u091f"+
		"\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003"+
		"\u00cd\u0926\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003\u00cd\u092b"+
		"\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0001"+
		"\u00cd\u0003\u00cd\u0933\b\u00cd\u0001\u00cd\u0001\u00cd\u0001\u00cd\u0003"+
		"\u00cd\u0938\b\u00cd\u0001\u00cd\u0001\u00cd\u0003\u00cd\u093c\b\u00cd"+
		"\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0005\u00ce\u0941\b\u00ce\n\u00ce"+
		"\f\u00ce\u0944\t\u00ce\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0003\u00cf"+
		"\u0949\b\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf"+
		"\u0003\u00cf\u0950\b\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf"+
		"\u0001\u00cf\u0003\u00cf\u0957\b\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf"+
		"\u0001\u00cf\u0001\u00cf\u0003\u00cf\u095e\b\u00cf\u0001\u00cf\u0001\u00cf"+
		"\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0003\u00cf\u0966\b\u00cf"+
		"\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0003\u00cf"+
		"\u096d\b\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf"+
		"\u0001\u00cf\u0003\u00cf\u0975\b\u00cf\u0001\u00d0\u0001\u00d0\u0003\u00d0"+
		"\u0979\b\u00d0\u0001\u00d0\u0001\u00d0\u0001\u00d1\u0001\u00d1\u0001\u00d1"+
		"\u0003\u00d1\u0980\b\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1"+
		"\u0001\u00d1\u0003\u00d1\u0987\b\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1"+
		"\u0001\u00d1\u0001\u00d1\u0003\u00d1\u098e\b\u00d1\u0001\u00d1\u0001\u00d1"+
		"\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0003\u00d1\u0996\b\u00d1"+
		"\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0003\u00d1"+
		"\u099d\b\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1"+
		"\u0001\u00d1\u0003\u00d1\u09a5\b\u00d1\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u09ab\b\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u09b1\b\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u09bd\b\u00d2\u0001\u00d3\u0001\u00d3\u0005\u00d3"+
		"\u09c1\b\u00d3\n\u00d3\f\u00d3\u09c4\t\u00d3\u0001\u00d4\u0005\u00d4\u09c7"+
		"\b\u00d4\n\u00d4\f\u00d4\u09ca\t\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4"+
		"\u0001\u00d4\u0001\u00d5\u0001\u00d5\u0001\u00d6\u0001\u00d6\u0003\u00d6"+
		"\u09d4\b\u00d6\u0001\u00d7\u0001\u00d7\u0001\u00d7\u0001\u00d7\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0003\u00d8\u09dd\b\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0003\u00d8\u09e4\b\u00d8\u0001\u00d9"+
		"\u0001\u00d9\u0001\u00d9\u0005\u00d9\u09e9\b\u00d9\n\u00d9\f\u00d9\u09ec"+
		"\t\u00d9\u0001\u00da\u0001\u00da\u0003\u00da\u09f0\b\u00da\u0001\u00db"+
		"\u0001\u00db\u0003\u00db\u09f4\b\u00db\u0001\u00dc\u0001\u00dc\u0001\u00dc"+
		"\u0001\u00dc\u0001\u00dd\u0001\u00dd\u0001\u00dd\u0003\u00dd\u09fd\b\u00dd"+
		"\u0001\u00de\u0001\u00de\u0001\u00df\u0001\u00df\u0001\u00e0\u0001\u00e0"+
		"\u0001\u00e1\u0001\u00e1\u0001\u00e2\u0001\u00e2\u0001\u00e3\u0001\u00e3"+
		"\u0001\u00e3\u0001\u00e3\u0001\u00e3\u0001\u00e3\u0001\u00e3\u0001\u00e3"+
		"\u0001\u00e3\u0001\u00e3\u0001\u00e3\u0001\u00e3\u0001\u00e3\u0003\u00e3"+
		"\u0a16\b\u00e3\u0001\u00e4\u0001\u00e4\u0001\u00e4\u0001\u00e4\u0001\u00e4"+
		"\u0001\u00e4\u0005\u00e4\u0a1e\b\u00e4\n\u00e4\f\u00e4\u0a21\t\u00e4\u0001"+
		"\u00e5\u0001\u00e5\u0001\u00e5\u0001\u00e5\u0001\u00e5\u0001\u00e5\u0005"+
		"\u00e5\u0a29\b\u00e5\n\u00e5\f\u00e5\u0a2c\t\u00e5\u0001\u00e6\u0001\u00e6"+
		"\u0001\u00e6\u0001\u00e6\u0001\u00e6\u0001\u00e6\u0005\u00e6\u0a34\b\u00e6"+
		"\n\u00e6\f\u00e6\u0a37\t\u00e6\u0001\u00e7\u0001\u00e7\u0001\u00e7\u0001"+
		"\u00e7\u0001\u00e7\u0001\u00e7\u0005\u00e7\u0a3f\b\u00e7\n\u00e7\f\u00e7"+
		"\u0a42\t\u00e7\u0001\u00e8\u0001\u00e8\u0001\u00e8\u0001\u00e8\u0001\u00e8"+
		"\u0001\u00e8\u0005\u00e8\u0a4a\b\u00e8\n\u00e8\f\u00e8\u0a4d\t\u00e8\u0001"+
		"\u00e9\u0001\u00e9\u0001\u00e9\u0001\u00e9\u0001\u00e9\u0001\u00e9\u0001"+
		"\u00e9\u0001\u00e9\u0001\u00e9\u0005\u00e9\u0a58\b\u00e9\n\u00e9\f\u00e9"+
		"\u0a5b\t\u00e9\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea"+
		"\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea"+
		"\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea"+
		"\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea"+
		"\u0005\u00ea\u0a74\b\u00ea\n\u00ea\f\u00ea\u0a77\t\u00ea\u0001\u00eb\u0001"+
		"\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001"+
		"\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001"+
		"\u00eb\u0001\u00eb\u0005\u00eb\u0a88\b\u00eb\n\u00eb\f\u00eb\u0a8b\t\u00eb"+
		"\u0001\u00ec\u0001\u00ec\u0001\u00ec\u0001\u00ec\u0001\u00ec\u0001\u00ec"+
		"\u0001\u00ec\u0003\u00ec\u0a94\b\u00ec\u0001\u00ed\u0001\u00ed\u0001\u00ed"+
		"\u0001\u00ed\u0001\u00ed\u0001\u00ed\u0001\u00ed\u0001\u00ed\u0001\u00ed"+
		"\u0001\u00ed\u0001\u00ed\u0005\u00ed\u0aa1\b\u00ed\n\u00ed\f\u00ed\u0aa4"+
		"\t\u00ed\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0001"+
		"\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0001"+
		"\u00ee\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0005\u00ee\u0ab5\b\u00ee\n"+
		"\u00ee\f\u00ee\u0ab8\t\u00ee\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0001"+
		"\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0003"+
		"\u00ef\u0ac3\b\u00ef\u0001\u00f0\u0001\u00f0\u0001\u00f0\u0001\u00f1\u0001"+
		"\u00f1\u0001\u00f1\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001"+
		"\u00f2\u0001\u00f2\u0003\u00f2\u0ad1\b\u00f2\u0001\u00f3\u0001\u00f3\u0003"+
		"\u00f3\u0ad5\b\u00f3\u0001\u00f3\u0001\u00f3\u0005\u00f3\u0ad9\b\u00f3"+
		"\n\u00f3\f\u00f3\u0adc\t\u00f3\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0001"+
		"\u00f5\u0001\u00f5\u0001\u00f6\u0001\u00f6\u0001\u00f6\u0001\u00f7\u0001"+
		"\u00f7\u0001\u00f8\u0001\u00f8\u0001\u00f8\u0001\u00f8\u0001\u00f8\u0001"+
		"\u00f8\u0001\u00f8\u0001\u00f8\u0005\u00f8\u0af0\b\u00f8\n\u00f8\f\u00f8"+
		"\u0af3\t\u00f8\u0001\u00f8\u0001\u00f8\u0001\u00f8\u0001\u00f8\u0001\u00f8"+
		"\u0001\u00f8\u0005\u00f8\u0afb\b\u00f8\n\u00f8\f\u00f8\u0afe\t\u00f8\u0001"+
		"\u00f8\u0001\u00f8\u0001\u00f8\u0003\u00f8\u0b03\b\u00f8\u0001\u00f8\u0000"+
		"\r48>\u01c8\u01ca\u01cc\u01ce\u01d0\u01d2\u01d4\u01d6\u01da\u01dc\u00f9"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082"+
		"\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a"+
		"\u009c\u009e\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2"+
		"\u00b4\u00b6\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca"+
		"\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0\u00e2"+
		"\u00e4\u00e6\u00e8\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8\u00fa"+
		"\u00fc\u00fe\u0100\u0102\u0104\u0106\u0108\u010a\u010c\u010e\u0110\u0112"+
		"\u0114\u0116\u0118\u011a\u011c\u011e\u0120\u0122\u0124\u0126\u0128\u012a"+
		"\u012c\u012e\u0130\u0132\u0134\u0136\u0138\u013a\u013c\u013e\u0140\u0142"+
		"\u0144\u0146\u0148\u014a\u014c\u014e\u0150\u0152\u0154\u0156\u0158\u015a"+
		"\u015c\u015e\u0160\u0162\u0164\u0166\u0168\u016a\u016c\u016e\u0170\u0172"+
		"\u0174\u0176\u0178\u017a\u017c\u017e\u0180\u0182\u0184\u0186\u0188\u018a"+
		"\u018c\u018e\u0190\u0192\u0194\u0196\u0198\u019a\u019c\u019e\u01a0\u01a2"+
		"\u01a4\u01a6\u01a8\u01aa\u01ac\u01ae\u01b0\u01b2\u01b4\u01b6\u01b8\u01ba"+
		"\u01bc\u01be\u01c0\u01c2\u01c4\u01c6\u01c8\u01ca\u01cc\u01ce\u01d0\u01d2"+
		"\u01d4\u01d6\u01d8\u01da\u01dc\u01de\u01e0\u01e2\u01e4\u01e6\u01e8\u01ea"+
		"\u01ec\u01ee\u01f0\u0000\u0007\u0001\u00005:\u0005\u0000\u0007\u0007\n"+
		"\n\u001d\u001d\u001f\u001f\'\'\u0002\u0000\u0010\u0010\u0016\u0016\u0002"+
		"\u0000DD]g\u0001\u0000ST\u0003\u0000\u001c\u001cEFLM\u0002\u0000UVZZ\u0bdd"+
		"\u0000\u01f2\u0001\u0000\u0000\u0000\u0002\u01f6\u0001\u0000\u0000\u0000"+
		"\u0004\u0206\u0001\u0000\u0000\u0000\u0006\u020a\u0001\u0000\u0000\u0000"+
		"\b\u020c\u0001\u0000\u0000\u0000\n\u020e\u0001\u0000\u0000\u0000\f\u0213"+
		"\u0001\u0000\u0000\u0000\u000e\u0217\u0001\u0000\u0000\u0000\u0010\u022a"+
		"\u0001\u0000\u0000\u0000\u0012\u022c\u0001\u0000\u0000\u0000\u0014\u0231"+
		"\u0001\u0000\u0000\u0000\u0016\u0235\u0001\u0000\u0000\u0000\u0018\u0237"+
		"\u0001\u0000\u0000\u0000\u001a\u0239\u0001\u0000\u0000\u0000\u001c\u023b"+
		"\u0001\u0000\u0000\u0000\u001e\u0246\u0001\u0000\u0000\u0000 \u0248\u0001"+
		"\u0000\u0000\u0000\"\u0252\u0001\u0000\u0000\u0000$\u0259\u0001\u0000"+
		"\u0000\u0000&\u0265\u0001\u0000\u0000\u0000(\u0267\u0001\u0000\u0000\u0000"+
		"*\u026a\u0001\u0000\u0000\u0000,\u026e\u0001\u0000\u0000\u0000.\u0278"+
		"\u0001\u0000\u0000\u00000\u027d\u0001\u0000\u0000\u00002\u0288\u0001\u0000"+
		"\u0000\u00004\u028a\u0001\u0000\u0000\u00006\u029a\u0001\u0000\u0000\u0000"+
		"8\u029c\u0001\u0000\u0000\u0000:\u02ac\u0001\u0000\u0000\u0000<\u02ae"+
		"\u0001\u0000\u0000\u0000>\u02b0\u0001\u0000\u0000\u0000@\u02bc\u0001\u0000"+
		"\u0000\u0000B\u02cf\u0001\u0000\u0000\u0000D\u02dd\u0001\u0000\u0000\u0000"+
		"F\u02e3\u0001\u0000\u0000\u0000H\u02e5\u0001\u0000\u0000\u0000J\u02e9"+
		"\u0001\u0000\u0000\u0000L\u02ef\u0001\u0000\u0000\u0000N\u02f6\u0001\u0000"+
		"\u0000\u0000P\u0300\u0001\u0000\u0000\u0000R\u0304\u0001\u0000\u0000\u0000"+
		"T\u0306\u0001\u0000\u0000\u0000V\u0317\u0001\u0000\u0000\u0000X\u0322"+
		"\u0001\u0000\u0000\u0000Z\u0324\u0001\u0000\u0000\u0000\\\u0328\u0001"+
		"\u0000\u0000\u0000^\u0330\u0001\u0000\u0000\u0000`\u0333\u0001\u0000\u0000"+
		"\u0000b\u0336\u0001\u0000\u0000\u0000d\u033e\u0001\u0000\u0000\u0000f"+
		"\u034b\u0001\u0000\u0000\u0000h\u0352\u0001\u0000\u0000\u0000j\u0354\u0001"+
		"\u0000\u0000\u0000l\u035c\u0001\u0000\u0000\u0000n\u0367\u0001\u0000\u0000"+
		"\u0000p\u0369\u0001\u0000\u0000\u0000r\u0371\u0001\u0000\u0000\u0000t"+
		"\u0376\u0001\u0000\u0000\u0000v\u037c\u0001\u0000\u0000\u0000x\u0380\u0001"+
		"\u0000\u0000\u0000z\u0384\u0001\u0000\u0000\u0000|\u0389\u0001\u0000\u0000"+
		"\u0000~\u038d\u0001\u0000\u0000\u0000\u0080\u03a0\u0001\u0000\u0000\u0000"+
		"\u0082\u03a2\u0001\u0000\u0000\u0000\u0084\u03a7\u0001\u0000\u0000\u0000"+
		"\u0086\u03ab\u0001\u0000\u0000\u0000\u0088\u03ad\u0001\u0000\u0000\u0000"+
		"\u008a\u03af\u0001\u0000\u0000\u0000\u008c\u03b1\u0001\u0000\u0000\u0000"+
		"\u008e\u03bc\u0001\u0000\u0000\u0000\u0090\u03be\u0001\u0000\u0000\u0000"+
		"\u0092\u03c5\u0001\u0000\u0000\u0000\u0094\u03d2\u0001\u0000\u0000\u0000"+
		"\u0096\u03e5\u0001\u0000\u0000\u0000\u0098\u03e9\u0001\u0000\u0000\u0000"+
		"\u009a\u03eb\u0001\u0000\u0000\u0000\u009c\u03f9\u0001\u0000\u0000\u0000"+
		"\u009e\u040b\u0001\u0000\u0000\u0000\u00a0\u0410\u0001\u0000\u0000\u0000"+
		"\u00a2\u0418\u0001\u0000\u0000\u0000\u00a4\u042b\u0001\u0000\u0000\u0000"+
		"\u00a6\u0430\u0001\u0000\u0000\u0000\u00a8\u043a\u0001\u0000\u0000\u0000"+
		"\u00aa\u043d\u0001\u0000\u0000\u0000\u00ac\u0447\u0001\u0000\u0000\u0000"+
		"\u00ae\u044b\u0001\u0000\u0000\u0000\u00b0\u044d\u0001\u0000\u0000\u0000"+
		"\u00b2\u044f\u0001\u0000\u0000\u0000\u00b4\u0452\u0001\u0000\u0000\u0000"+
		"\u00b6\u045c\u0001\u0000\u0000\u0000\u00b8\u0463\u0001\u0000\u0000\u0000"+
		"\u00ba\u0466\u0001\u0000\u0000\u0000\u00bc\u046f\u0001\u0000\u0000\u0000"+
		"\u00be\u0471\u0001\u0000\u0000\u0000\u00c0\u04a8\u0001\u0000\u0000\u0000"+
		"\u00c2\u04aa\u0001\u0000\u0000\u0000\u00c4\u04b2\u0001\u0000\u0000\u0000"+
		"\u00c6\u04be\u0001\u0000\u0000\u0000\u00c8\u04c9\u0001\u0000\u0000\u0000"+
		"\u00ca\u04d7\u0001\u0000\u0000\u0000\u00cc\u04d9\u0001\u0000\u0000\u0000"+
		"\u00ce\u04e2\u0001\u0000\u0000\u0000\u00d0\u04e4\u0001\u0000\u0000\u0000"+
		"\u00d2\u04f2\u0001\u0000\u0000\u0000\u00d4\u04fc\u0001\u0000\u0000\u0000"+
		"\u00d6\u04fe\u0001\u0000\u0000\u0000\u00d8\u0501\u0001\u0000\u0000\u0000"+
		"\u00da\u050f\u0001\u0000\u0000\u0000\u00dc\u0514\u0001\u0000\u0000\u0000"+
		"\u00de\u051f\u0001\u0000\u0000\u0000\u00e0\u0524\u0001\u0000\u0000\u0000"+
		"\u00e2\u0530\u0001\u0000\u0000\u0000\u00e4\u0535\u0001\u0000\u0000\u0000"+
		"\u00e6\u053d\u0001\u0000\u0000\u0000\u00e8\u054b\u0001\u0000\u0000\u0000"+
		"\u00ea\u0550\u0001\u0000\u0000\u0000\u00ec\u0562\u0001\u0000\u0000\u0000"+
		"\u00ee\u0564\u0001\u0000\u0000\u0000\u00f0\u056a\u0001\u0000\u0000\u0000"+
		"\u00f2\u056f\u0001\u0000\u0000\u0000\u00f4\u0577\u0001\u0000\u0000\u0000"+
		"\u00f6\u057c\u0001\u0000\u0000\u0000\u00f8\u0584\u0001\u0000\u0000\u0000"+
		"\u00fa\u058c\u0001\u0000\u0000\u0000\u00fc\u0593\u0001\u0000\u0000\u0000"+
		"\u00fe\u0595\u0001\u0000\u0000\u0000\u0100\u059e\u0001\u0000\u0000\u0000"+
		"\u0102\u05a6\u0001\u0000\u0000\u0000\u0104\u05a9\u0001\u0000\u0000\u0000"+
		"\u0106\u05af\u0001\u0000\u0000\u0000\u0108\u05b8\u0001\u0000\u0000\u0000"+
		"\u010a\u05c0\u0001\u0000\u0000\u0000\u010c\u05c6\u0001\u0000\u0000\u0000"+
		"\u010e\u05d0\u0001\u0000\u0000\u0000\u0110\u05d2\u0001\u0000\u0000\u0000"+
		"\u0112\u05d8\u0001\u0000\u0000\u0000\u0114\u05e4\u0001\u0000\u0000\u0000"+
		"\u0116\u05eb\u0001\u0000\u0000\u0000\u0118\u05f9\u0001\u0000\u0000\u0000"+
		"\u011a\u05fb\u0001\u0000\u0000\u0000\u011c\u05fd\u0001\u0000\u0000\u0000"+
		"\u011e\u0601\u0001\u0000\u0000\u0000\u0120\u0605\u0001\u0000\u0000\u0000"+
		"\u0122\u060f\u0001\u0000\u0000\u0000\u0124\u0611\u0001\u0000\u0000\u0000"+
		"\u0126\u0617\u0001\u0000\u0000\u0000\u0128\u061f\u0001\u0000\u0000\u0000"+
		"\u012a\u0631\u0001\u0000\u0000\u0000\u012c\u0633\u0001\u0000\u0000\u0000"+
		"\u012e\u0639\u0001\u0000\u0000\u0000\u0130\u0648\u0001\u0000\u0000\u0000"+
		"\u0132\u064b\u0001\u0000\u0000\u0000\u0134\u065c\u0001\u0000\u0000\u0000"+
		"\u0136\u065e\u0001\u0000\u0000\u0000\u0138\u0660\u0001\u0000\u0000\u0000"+
		"\u013a\u0666\u0001\u0000\u0000\u0000\u013c\u066c\u0001\u0000\u0000\u0000"+
		"\u013e\u0676\u0001\u0000\u0000\u0000\u0140\u067a\u0001\u0000\u0000\u0000"+
		"\u0142\u067c\u0001\u0000\u0000\u0000\u0144\u068c\u0001\u0000\u0000\u0000"+
		"\u0146\u069e\u0001\u0000\u0000\u0000\u0148\u06a0\u0001\u0000\u0000\u0000"+
		"\u014a\u06a2\u0001\u0000\u0000\u0000\u014c\u06aa\u0001\u0000\u0000\u0000"+
		"\u014e\u06b9\u0001\u0000\u0000\u0000\u0150\u06c8\u0001\u0000\u0000\u0000"+
		"\u0152\u06ce\u0001\u0000\u0000\u0000\u0154\u06d4\u0001\u0000\u0000\u0000"+
		"\u0156\u06da\u0001\u0000\u0000\u0000\u0158\u06de\u0001\u0000\u0000\u0000"+
		"\u015a\u06f0\u0001\u0000\u0000\u0000\u015c\u06f2\u0001\u0000\u0000\u0000"+
		"\u015e\u06f9\u0001\u0000\u0000\u0000\u0160\u0702\u0001\u0000\u0000\u0000"+
		"\u0162\u0708\u0001\u0000\u0000\u0000\u0164\u0710\u0001\u0000\u0000\u0000"+
		"\u0166\u0713\u0001\u0000\u0000\u0000\u0168\u071c\u0001\u0000\u0000\u0000"+
		"\u016a\u0723\u0001\u0000\u0000\u0000\u016c\u072e\u0001\u0000\u0000\u0000"+
		"\u016e\u0738\u0001\u0000\u0000\u0000\u0170\u075c\u0001\u0000\u0000\u0000"+
		"\u0172\u075e\u0001\u0000\u0000\u0000\u0174\u077b\u0001\u0000\u0000\u0000"+
		"\u0176\u0782\u0001\u0000\u0000\u0000\u0178\u0784\u0001\u0000\u0000\u0000"+
		"\u017a\u078a\u0001\u0000\u0000\u0000\u017c\u07b2\u0001\u0000\u0000\u0000"+
		"\u017e\u07b4\u0001\u0000\u0000\u0000\u0180\u07db\u0001\u0000\u0000\u0000"+
		"\u0182\u0818\u0001\u0000\u0000\u0000\u0184\u081a\u0001\u0000\u0000\u0000"+
		"\u0186\u0854\u0001\u0000\u0000\u0000\u0188\u0858\u0001\u0000\u0000\u0000"+
		"\u018a\u0867\u0001\u0000\u0000\u0000\u018c\u0869\u0001\u0000\u0000\u0000"+
		"\u018e\u0875\u0001\u0000\u0000\u0000\u0190\u0881\u0001\u0000\u0000\u0000"+
		"\u0192\u088d\u0001\u0000\u0000\u0000\u0194\u08a6\u0001\u0000\u0000\u0000"+
		"\u0196\u08f6\u0001\u0000\u0000\u0000\u0198\u08f8\u0001\u0000\u0000\u0000"+
		"\u019a\u093b\u0001\u0000\u0000\u0000\u019c\u093d\u0001\u0000\u0000\u0000"+
		"\u019e\u0974\u0001\u0000\u0000\u0000\u01a0\u0976\u0001\u0000\u0000\u0000"+
		"\u01a2\u09a4\u0001\u0000\u0000\u0000\u01a4\u09bc\u0001\u0000\u0000\u0000"+
		"\u01a6\u09be\u0001\u0000\u0000\u0000\u01a8\u09c8\u0001\u0000\u0000\u0000"+
		"\u01aa\u09cf\u0001\u0000\u0000\u0000\u01ac\u09d3\u0001\u0000\u0000\u0000"+
		"\u01ae\u09d5\u0001\u0000\u0000\u0000\u01b0\u09e3\u0001\u0000\u0000\u0000"+
		"\u01b2\u09e5\u0001\u0000\u0000\u0000\u01b4\u09ef\u0001\u0000\u0000\u0000"+
		"\u01b6\u09f3\u0001\u0000\u0000\u0000\u01b8\u09f5\u0001\u0000\u0000\u0000"+
		"\u01ba\u09fc\u0001\u0000\u0000\u0000\u01bc\u09fe\u0001\u0000\u0000\u0000"+
		"\u01be\u0a00\u0001\u0000\u0000\u0000\u01c0\u0a02\u0001\u0000\u0000\u0000"+
		"\u01c2\u0a04\u0001\u0000\u0000\u0000\u01c4\u0a06\u0001\u0000\u0000\u0000"+
		"\u01c6\u0a15\u0001\u0000\u0000\u0000\u01c8\u0a17\u0001\u0000\u0000\u0000"+
		"\u01ca\u0a22\u0001\u0000\u0000\u0000\u01cc\u0a2d\u0001\u0000\u0000\u0000"+
		"\u01ce\u0a38\u0001\u0000\u0000\u0000\u01d0\u0a43\u0001\u0000\u0000\u0000"+
		"\u01d2\u0a4e\u0001\u0000\u0000\u0000\u01d4\u0a5c\u0001\u0000\u0000\u0000"+
		"\u01d6\u0a78\u0001\u0000\u0000\u0000\u01d8\u0a93\u0001\u0000\u0000\u0000"+
		"\u01da\u0a95\u0001\u0000\u0000\u0000\u01dc\u0aa5\u0001\u0000\u0000\u0000"+
		"\u01de\u0ac2\u0001\u0000\u0000\u0000\u01e0\u0ac4\u0001\u0000\u0000\u0000"+
		"\u01e2\u0ac7\u0001\u0000\u0000\u0000\u01e4\u0ad0\u0001\u0000\u0000\u0000"+
		"\u01e6\u0ad4\u0001\u0000\u0000\u0000\u01e8\u0add\u0001\u0000\u0000\u0000"+
		"\u01ea\u0ae0\u0001\u0000\u0000\u0000\u01ec\u0ae2\u0001\u0000\u0000\u0000"+
		"\u01ee\u0ae5\u0001\u0000\u0000\u0000\u01f0\u0b02\u0001\u0000\u0000\u0000"+
		"\u01f2\u01f3\u0007\u0000\u0000\u0000\u01f3\u0001\u0001\u0000\u0000\u0000"+
		"\u01f4\u01f7\u0003\u0004\u0002\u0000\u01f5\u01f7\u0003\f\u0006\u0000\u01f6"+
		"\u01f4\u0001\u0000\u0000\u0000\u01f6\u01f5\u0001\u0000\u0000\u0000\u01f7"+
		"\u0003\u0001\u0000\u0000\u0000\u01f8\u01fa\u0003\u00f0x\u0000\u01f9\u01f8"+
		"\u0001\u0000\u0000\u0000\u01fa\u01fd\u0001\u0000\u0000\u0000\u01fb\u01f9"+
		"\u0001\u0000\u0000\u0000\u01fb\u01fc\u0001\u0000\u0000\u0000\u01fc\u01fe"+
		"\u0001\u0000\u0000\u0000\u01fd\u01fb\u0001\u0000\u0000\u0000\u01fe\u0207"+
		"\u0003\u0006\u0003\u0000\u01ff\u0201\u0003\u00f0x\u0000\u0200\u01ff\u0001"+
		"\u0000\u0000\u0000\u0201\u0204\u0001\u0000\u0000\u0000\u0202\u0200\u0001"+
		"\u0000\u0000\u0000\u0202\u0203\u0001\u0000\u0000\u0000\u0203\u0205\u0001"+
		"\u0000\u0000\u0000\u0204\u0202\u0001\u0000\u0000\u0000\u0205\u0207\u0005"+
		"\u0005\u0000\u0000\u0206\u01fb\u0001\u0000\u0000\u0000\u0206\u0202\u0001"+
		"\u0000\u0000\u0000\u0207\u0005\u0001\u0000\u0000\u0000\u0208\u020b\u0003"+
		"\b\u0004\u0000\u0209\u020b\u0003\n\u0005\u0000\u020a\u0208\u0001\u0000"+
		"\u0000\u0000\u020a\u0209\u0001\u0000\u0000\u0000\u020b\u0007\u0001\u0000"+
		"\u0000\u0000\u020c\u020d\u0007\u0001\u0000\u0000\u020d\t\u0001\u0000\u0000"+
		"\u0000\u020e\u020f\u0007\u0002\u0000\u0000\u020f\u000b\u0001\u0000\u0000"+
		"\u0000\u0210\u0214\u0003\u000e\u0007\u0000\u0211\u0214\u0003\u001c\u000e"+
		"\u0000\u0212\u0214\u0003\u001e\u000f\u0000\u0213\u0210\u0001\u0000\u0000"+
		"\u0000\u0213\u0211\u0001\u0000\u0000\u0000\u0213\u0212\u0001\u0000\u0000"+
		"\u0000\u0214\r\u0001\u0000\u0000\u0000\u0215\u0218\u0003\u0014\n\u0000"+
		"\u0216\u0218\u0003\u001a\r\u0000\u0217\u0215\u0001\u0000\u0000\u0000\u0217"+
		"\u0216\u0001\u0000\u0000\u0000\u0218\u021d\u0001\u0000\u0000\u0000\u0219"+
		"\u021c\u0003\u0012\t\u0000\u021a\u021c\u0003\u0018\f\u0000\u021b\u0219"+
		"\u0001\u0000\u0000\u0000\u021b\u021a\u0001\u0000\u0000\u0000\u021c\u021f"+
		"\u0001\u0000\u0000\u0000\u021d\u021b\u0001\u0000\u0000\u0000\u021d\u021e"+
		"\u0001\u0000\u0000\u0000\u021e\u000f\u0001\u0000\u0000\u0000\u021f\u021d"+
		"\u0001\u0000\u0000\u0000\u0220\u0222\u0003\u00f2y\u0000\u0221\u0223\u0003"+
		"*\u0015\u0000\u0222\u0221\u0001\u0000\u0000\u0000\u0222\u0223\u0001\u0000"+
		"\u0000\u0000\u0223\u022b\u0001\u0000\u0000\u0000\u0224\u0225\u0003\u000e"+
		"\u0007\u0000\u0225\u0226\u0005C\u0000\u0000\u0226\u0228\u0003\u00f2y\u0000"+
		"\u0227\u0229\u0003*\u0015\u0000\u0228\u0227\u0001\u0000\u0000\u0000\u0228"+
		"\u0229\u0001\u0000\u0000\u0000\u0229\u022b\u0001\u0000\u0000\u0000\u022a"+
		"\u0220\u0001\u0000\u0000\u0000\u022a\u0224\u0001\u0000\u0000\u0000\u022b"+
		"\u0011\u0001\u0000\u0000\u0000\u022c\u022d\u0005C\u0000\u0000\u022d\u022f"+
		"\u0003\u00f2y\u0000\u022e\u0230\u0003*\u0015\u0000\u022f\u022e\u0001\u0000"+
		"\u0000\u0000\u022f\u0230\u0001\u0000\u0000\u0000\u0230\u0013\u0001\u0000"+
		"\u0000\u0000\u0231\u0233\u0003\u00f2y\u0000\u0232\u0234\u0003*\u0015\u0000"+
		"\u0233\u0232\u0001\u0000\u0000\u0000\u0233\u0234\u0001\u0000\u0000\u0000"+
		"\u0234\u0015\u0001\u0000\u0000\u0000\u0235\u0236\u0003\u0010\b\u0000\u0236"+
		"\u0017\u0001\u0000\u0000\u0000\u0237\u0238\u0003\u0012\t\u0000\u0238\u0019"+
		"\u0001\u0000\u0000\u0000\u0239\u023a\u0003\u0014\n\u0000\u023a\u001b\u0001"+
		"\u0000\u0000\u0000\u023b\u023c\u0003\u00f2y\u0000\u023c\u001d\u0001\u0000"+
		"\u0000\u0000\u023d\u023e\u0003\u0004\u0002\u0000\u023e\u023f\u0003 \u0010"+
		"\u0000\u023f\u0247\u0001\u0000\u0000\u0000\u0240\u0241\u0003\u000e\u0007"+
		"\u0000\u0241\u0242\u0003 \u0010\u0000\u0242\u0247\u0001\u0000\u0000\u0000"+
		"\u0243\u0244\u0003\u001c\u000e\u0000\u0244\u0245\u0003 \u0010\u0000\u0245"+
		"\u0247\u0001\u0000\u0000\u0000\u0246\u023d\u0001\u0000\u0000\u0000\u0246"+
		"\u0240\u0001\u0000\u0000\u0000\u0246\u0243\u0001\u0000\u0000\u0000\u0247"+
		"\u001f\u0001\u0000\u0000\u0000\u0248\u024c\u0003\u00f4z\u0000\u0249\u024b"+
		"\u0003\u00f4z\u0000\u024a\u0249\u0001\u0000\u0000\u0000\u024b\u024e\u0001"+
		"\u0000\u0000\u0000\u024c\u024a\u0001\u0000\u0000\u0000\u024c\u024d\u0001"+
		"\u0000\u0000\u0000\u024d!\u0001\u0000\u0000\u0000\u024e\u024c\u0001\u0000"+
		"\u0000\u0000\u024f\u0251\u0003$\u0012\u0000\u0250\u024f\u0001\u0000\u0000"+
		"\u0000\u0251\u0254\u0001\u0000\u0000\u0000\u0252\u0250\u0001\u0000\u0000"+
		"\u0000\u0252\u0253\u0001\u0000\u0000\u0000\u0253\u0255\u0001\u0000\u0000"+
		"\u0000\u0254\u0252\u0001\u0000\u0000\u0000\u0255\u0257\u0005h\u0000\u0000"+
		"\u0256\u0258\u0003&\u0013\u0000\u0257\u0256\u0001\u0000\u0000\u0000\u0257"+
		"\u0258\u0001\u0000\u0000\u0000\u0258#\u0001\u0000\u0000\u0000\u0259\u025a"+
		"\u0003\u00f0x\u0000\u025a%\u0001\u0000\u0000\u0000\u025b\u025c\u0005\u0013"+
		"\u0000\u0000\u025c\u0266\u0003\u001c\u000e\u0000\u025d\u025e\u0005\u0013"+
		"\u0000\u0000\u025e\u0262\u0003\u000e\u0007\u0000\u025f\u0261\u0003(\u0014"+
		"\u0000\u0260\u025f\u0001\u0000\u0000\u0000\u0261\u0264\u0001\u0000\u0000"+
		"\u0000\u0262\u0260\u0001\u0000\u0000\u0000\u0262\u0263\u0001\u0000\u0000"+
		"\u0000\u0263\u0266\u0001\u0000\u0000\u0000\u0264\u0262\u0001\u0000\u0000"+
		"\u0000\u0265\u025b\u0001\u0000\u0000\u0000\u0265\u025d\u0001\u0000\u0000"+
		"\u0000\u0266\'\u0001\u0000\u0000\u0000\u0267\u0268\u0005W\u0000\u0000"+
		"\u0268\u0269\u0003\u0016\u000b\u0000\u0269)\u0001\u0000\u0000\u0000\u026a"+
		"\u026b\u0005F\u0000\u0000\u026b\u026c\u0003,\u0016\u0000\u026c\u026d\u0005"+
		"E\u0000\u0000\u026d+\u0001\u0000\u0000\u0000\u026e\u0273\u0003.\u0017"+
		"\u0000\u026f\u0270\u0005B\u0000\u0000\u0270\u0272\u0003.\u0017\u0000\u0271"+
		"\u026f\u0001\u0000\u0000\u0000\u0272\u0275\u0001\u0000\u0000\u0000\u0273"+
		"\u0271\u0001\u0000\u0000\u0000\u0273\u0274\u0001\u0000\u0000\u0000\u0274"+
		"-\u0001\u0000\u0000\u0000\u0275\u0273\u0001\u0000\u0000\u0000\u0276\u0279"+
		"\u0003\f\u0006\u0000\u0277\u0279\u00030\u0018\u0000\u0278\u0276\u0001"+
		"\u0000\u0000\u0000\u0278\u0277\u0001\u0000\u0000\u0000\u0279/\u0001\u0000"+
		"\u0000\u0000\u027a\u027c\u0003\u00f0x\u0000\u027b\u027a\u0001\u0000\u0000"+
		"\u0000\u027c\u027f\u0001\u0000\u0000\u0000\u027d\u027b\u0001\u0000\u0000"+
		"\u0000\u027d\u027e\u0001\u0000\u0000\u0000\u027e\u0280\u0001\u0000\u0000"+
		"\u0000\u027f\u027d\u0001\u0000\u0000\u0000\u0280\u0282\u0005I\u0000\u0000"+
		"\u0281\u0283\u00032\u0019\u0000\u0282\u0281\u0001\u0000\u0000\u0000\u0282"+
		"\u0283\u0001\u0000\u0000\u0000\u02831\u0001\u0000\u0000\u0000\u0284\u0285"+
		"\u0005\u0013\u0000\u0000\u0285\u0289\u0003\f\u0006\u0000\u0286\u0287\u0005"+
		"*\u0000\u0000\u0287\u0289\u0003\f\u0006\u0000\u0288\u0284\u0001\u0000"+
		"\u0000\u0000\u0288\u0286\u0001\u0000\u0000\u0000\u02893\u0001\u0000\u0000"+
		"\u0000\u028a\u028b\u0006\u001a\uffff\uffff\u0000\u028b\u028c\u0005h\u0000"+
		"\u0000\u028c\u0292\u0001\u0000\u0000\u0000\u028d\u028e\n\u0001\u0000\u0000"+
		"\u028e\u028f\u0005C\u0000\u0000\u028f\u0291\u0005h\u0000\u0000\u0290\u028d"+
		"\u0001\u0000\u0000\u0000\u0291\u0294\u0001\u0000\u0000\u0000\u0292\u0290"+
		"\u0001\u0000\u0000\u0000\u0292\u0293\u0001\u0000\u0000\u0000\u02935\u0001"+
		"\u0000\u0000\u0000\u0294\u0292\u0001\u0000\u0000\u0000\u0295\u029b\u0005"+
		"h\u0000\u0000\u0296\u0297\u00038\u001c\u0000\u0297\u0298\u0005C\u0000"+
		"\u0000\u0298\u0299\u0005h\u0000\u0000\u0299\u029b\u0001\u0000\u0000\u0000"+
		"\u029a\u0295\u0001\u0000\u0000\u0000\u029a\u0296\u0001\u0000\u0000\u0000"+
		"\u029b7\u0001\u0000\u0000\u0000\u029c\u029d\u0006\u001c\uffff\uffff\u0000"+
		"\u029d\u029e\u0005h\u0000\u0000\u029e\u02a4\u0001\u0000\u0000\u0000\u029f"+
		"\u02a0\n\u0001\u0000\u0000\u02a0\u02a1\u0005C\u0000\u0000\u02a1\u02a3"+
		"\u0005h\u0000\u0000\u02a2\u029f\u0001\u0000\u0000\u0000\u02a3\u02a6\u0001"+
		"\u0000\u0000\u0000\u02a4\u02a2\u0001\u0000\u0000\u0000\u02a4\u02a5\u0001"+
		"\u0000\u0000\u0000\u02a59\u0001\u0000\u0000\u0000\u02a6\u02a4\u0001\u0000"+
		"\u0000\u0000\u02a7\u02ad\u0005h\u0000\u0000\u02a8\u02a9\u0003>\u001f\u0000"+
		"\u02a9\u02aa\u0005C\u0000\u0000\u02aa\u02ab\u0005h\u0000\u0000\u02ab\u02ad"+
		"\u0001\u0000\u0000\u0000\u02ac\u02a7\u0001\u0000\u0000\u0000\u02ac\u02a8"+
		"\u0001\u0000\u0000\u0000\u02ad;\u0001\u0000\u0000\u0000\u02ae\u02af\u0005"+
		"h\u0000\u0000\u02af=\u0001\u0000\u0000\u0000\u02b0\u02b1\u0006\u001f\uffff"+
		"\uffff\u0000\u02b1\u02b2\u0005h\u0000\u0000\u02b2\u02b8\u0001\u0000\u0000"+
		"\u0000\u02b3\u02b4\n\u0001\u0000\u0000\u02b4\u02b5\u0005C\u0000\u0000"+
		"\u02b5\u02b7\u0005h\u0000\u0000\u02b6\u02b3\u0001\u0000\u0000\u0000\u02b7"+
		"\u02ba\u0001\u0000\u0000\u0000\u02b8\u02b6\u0001\u0000\u0000\u0000\u02b8"+
		"\u02b9\u0001\u0000\u0000\u0000\u02b9?\u0001\u0000\u0000\u0000\u02ba\u02b8"+
		"\u0001\u0000\u0000\u0000\u02bb\u02bd\u0003B!\u0000\u02bc\u02bb\u0001\u0000"+
		"\u0000\u0000\u02bc\u02bd\u0001\u0000\u0000\u0000\u02bd\u02c1\u0001\u0000"+
		"\u0000\u0000\u02be\u02c0\u0003F#\u0000\u02bf\u02be\u0001\u0000\u0000\u0000"+
		"\u02c0\u02c3\u0001\u0000\u0000\u0000\u02c1\u02bf\u0001\u0000\u0000\u0000"+
		"\u02c1\u02c2\u0001\u0000\u0000\u0000\u02c2\u02c7\u0001\u0000\u0000\u0000"+
		"\u02c3\u02c1\u0001\u0000\u0000\u0000\u02c4\u02c6\u0003P(\u0000\u02c5\u02c4"+
		"\u0001\u0000\u0000\u0000\u02c6\u02c9\u0001\u0000\u0000\u0000\u02c7\u02c5"+
		"\u0001\u0000\u0000\u0000\u02c7\u02c8\u0001\u0000\u0000\u0000\u02c8\u02ca"+
		"\u0001\u0000\u0000\u0000\u02c9\u02c7\u0001\u0000\u0000\u0000\u02ca\u02cb"+
		"\u0005\u0000\u0000\u0001\u02cbA\u0001\u0000\u0000\u0000\u02cc\u02ce\u0003"+
		"D\"\u0000\u02cd\u02cc\u0001\u0000\u0000\u0000\u02ce\u02d1\u0001\u0000"+
		"\u0000\u0000\u02cf\u02cd\u0001\u0000\u0000\u0000\u02cf\u02d0\u0001\u0000"+
		"\u0000\u0000\u02d0\u02d2\u0001\u0000\u0000\u0000\u02d1\u02cf\u0001\u0000"+
		"\u0000\u0000\u02d2\u02d3\u0005\"\u0000\u0000\u02d3\u02d8\u0005h\u0000"+
		"\u0000\u02d4\u02d5\u0005C\u0000\u0000\u02d5\u02d7\u0005h\u0000\u0000\u02d6"+
		"\u02d4\u0001\u0000\u0000\u0000\u02d7\u02da\u0001\u0000\u0000\u0000\u02d8"+
		"\u02d6\u0001\u0000\u0000\u0000\u02d8\u02d9\u0001\u0000\u0000\u0000\u02d9"+
		"\u02db\u0001\u0000\u0000\u0000\u02da\u02d8\u0001\u0000\u0000\u0000\u02db"+
		"\u02dc\u0005A\u0000\u0000\u02dcC\u0001\u0000\u0000\u0000\u02dd\u02de\u0003"+
		"\u00f0x\u0000\u02deE\u0001\u0000\u0000\u0000\u02df\u02e4\u0003H$\u0000"+
		"\u02e0\u02e4\u0003J%\u0000\u02e1\u02e4\u0003L&\u0000\u02e2\u02e4\u0003"+
		"N\'\u0000\u02e3\u02df\u0001\u0000\u0000\u0000\u02e3\u02e0\u0001\u0000"+
		"\u0000\u0000\u02e3\u02e1\u0001\u0000\u0000\u0000\u02e3\u02e2\u0001\u0000"+
		"\u0000\u0000\u02e4G\u0001\u0000\u0000\u0000\u02e5\u02e6\u0005\u001b\u0000"+
		"\u0000\u02e6\u02e7\u00036\u001b\u0000\u02e7\u02e8\u0005A\u0000\u0000\u02e8"+
		"I\u0001\u0000\u0000\u0000\u02e9\u02ea\u0005\u001b\u0000\u0000\u02ea\u02eb"+
		"\u00038\u001c\u0000\u02eb\u02ec\u0005C\u0000\u0000\u02ec\u02ed\u0005U"+
		"\u0000\u0000\u02ed\u02ee\u0005A\u0000\u0000\u02eeK\u0001\u0000\u0000\u0000"+
		"\u02ef\u02f0\u0005\u001b\u0000\u0000\u02f0\u02f1\u0005(\u0000\u0000\u02f1"+
		"\u02f2\u00036\u001b\u0000\u02f2\u02f3\u0005C\u0000\u0000\u02f3\u02f4\u0005"+
		"h\u0000\u0000\u02f4\u02f5\u0005A\u0000\u0000\u02f5M\u0001\u0000\u0000"+
		"\u0000\u02f6\u02f7\u0005\u001b\u0000\u0000\u02f7\u02f8\u0005(\u0000\u0000"+
		"\u02f8\u02f9\u00036\u001b\u0000\u02f9\u02fa\u0005C\u0000\u0000\u02fa\u02fb"+
		"\u0005U\u0000\u0000\u02fb\u02fc\u0005A\u0000\u0000\u02fcO\u0001\u0000"+
		"\u0000\u0000\u02fd\u0301\u0003R)\u0000\u02fe\u0301\u0003\u00ceg\u0000"+
		"\u02ff\u0301\u0005A\u0000\u0000\u0300\u02fd\u0001\u0000\u0000\u0000\u0300"+
		"\u02fe\u0001\u0000\u0000\u0000\u0300\u02ff\u0001\u0000\u0000\u0000\u0301"+
		"Q\u0001\u0000\u0000\u0000\u0302\u0305\u0003T*\u0000\u0303\u0305\u0003"+
		"\u00c2a\u0000\u0304\u0302\u0001\u0000\u0000\u0000\u0304\u0303\u0001\u0000"+
		"\u0000\u0000\u0305S\u0001\u0000\u0000\u0000\u0306\u0307\u0003V+\u0000"+
		"\u0307\u0308\u0005\u000b\u0000\u0000\u0308\u030a\u0005h\u0000\u0000\u0309"+
		"\u030b\u0003Z-\u0000\u030a\u0309\u0001\u0000\u0000\u0000\u030a\u030b\u0001"+
		"\u0000\u0000\u0000\u030b\u030d\u0001\u0000\u0000\u0000\u030c\u030e\u0003"+
		"^/\u0000\u030d\u030c\u0001\u0000\u0000\u0000\u030d\u030e\u0001\u0000\u0000"+
		"\u0000\u030e\u0310\u0001\u0000\u0000\u0000\u030f\u0311\u0003`0\u0000\u0310"+
		"\u030f\u0001\u0000\u0000\u0000\u0310\u0311\u0001\u0000\u0000\u0000\u0311"+
		"\u0312\u0001\u0000\u0000\u0000\u0312\u0313\u0003d2\u0000\u0313U\u0001"+
		"\u0000\u0000\u0000\u0314\u0316\u0003X,\u0000\u0315\u0314\u0001\u0000\u0000"+
		"\u0000\u0316\u0319\u0001\u0000\u0000\u0000\u0317\u0315\u0001\u0000\u0000"+
		"\u0000\u0317\u0318\u0001\u0000\u0000\u0000\u0318W\u0001\u0000\u0000\u0000"+
		"\u0319\u0317\u0001\u0000\u0000\u0000\u031a\u0323\u0003\u00f0x\u0000\u031b"+
		"\u0323\u0005%\u0000\u0000\u031c\u0323\u0005$\u0000\u0000\u031d\u0323\u0005"+
		"#\u0000\u0000\u031e\u0323\u0005\u0003\u0000\u0000\u031f\u0323\u0005(\u0000"+
		"\u0000\u0320\u0323\u0005\u0014\u0000\u0000\u0321\u0323\u0005)\u0000\u0000"+
		"\u0322\u031a\u0001\u0000\u0000\u0000\u0322\u031b\u0001\u0000\u0000\u0000"+
		"\u0322\u031c\u0001\u0000\u0000\u0000\u0322\u031d\u0001\u0000\u0000\u0000"+
		"\u0322\u031e\u0001\u0000\u0000\u0000\u0322\u031f\u0001\u0000\u0000\u0000"+
		"\u0322\u0320\u0001\u0000\u0000\u0000\u0322\u0321\u0001\u0000\u0000\u0000"+
		"\u0323Y\u0001\u0000\u0000\u0000\u0324\u0325\u0005F\u0000\u0000\u0325\u0326"+
		"\u0003\\.\u0000\u0326\u0327\u0005E\u0000\u0000\u0327[\u0001\u0000\u0000"+
		"\u0000\u0328\u032d\u0003\"\u0011\u0000\u0329\u032a\u0005B\u0000\u0000"+
		"\u032a\u032c\u0003\"\u0011\u0000\u032b\u0329\u0001\u0000\u0000\u0000\u032c"+
		"\u032f\u0001\u0000\u0000\u0000\u032d\u032b\u0001\u0000\u0000\u0000\u032d"+
		"\u032e\u0001\u0000\u0000\u0000\u032e]\u0001\u0000\u0000\u0000\u032f\u032d"+
		"\u0001\u0000\u0000\u0000\u0330\u0331\u0005\u0013\u0000\u0000\u0331\u0332"+
		"\u0003\u0010\b\u0000\u0332_\u0001\u0000\u0000\u0000\u0333\u0334\u0005"+
		"\u001a\u0000\u0000\u0334\u0335\u0003b1\u0000\u0335a\u0001\u0000\u0000"+
		"\u0000\u0336\u033b\u0003\u0016\u000b\u0000\u0337\u0338\u0005B\u0000\u0000"+
		"\u0338\u033a\u0003\u0016\u000b\u0000\u0339\u0337\u0001\u0000\u0000\u0000"+
		"\u033a\u033d\u0001\u0000\u0000\u0000\u033b\u0339\u0001\u0000\u0000\u0000"+
		"\u033b\u033c\u0001\u0000\u0000\u0000\u033cc\u0001\u0000\u0000\u0000\u033d"+
		"\u033b\u0001\u0000\u0000\u0000\u033e\u0342\u0005=\u0000\u0000\u033f\u0341"+
		"\u0003f3\u0000\u0340\u033f\u0001\u0000\u0000\u0000\u0341\u0344\u0001\u0000"+
		"\u0000\u0000\u0342\u0340\u0001\u0000\u0000\u0000\u0342\u0343\u0001\u0000"+
		"\u0000\u0000\u0343\u0345\u0001\u0000\u0000\u0000\u0344\u0342\u0001\u0000"+
		"\u0000\u0000\u0345\u0346\u0005>\u0000\u0000\u0346e\u0001\u0000\u0000\u0000"+
		"\u0347\u034c\u0003h4\u0000\u0348\u034c\u0003\u00b0X\u0000\u0349\u034c"+
		"\u0003\u00b2Y\u0000\u034a\u034c\u0003\u00b4Z\u0000\u034b\u0347\u0001\u0000"+
		"\u0000\u0000\u034b\u0348\u0001\u0000\u0000\u0000\u034b\u0349\u0001\u0000"+
		"\u0000\u0000\u034b\u034a\u0001\u0000\u0000\u0000\u034cg\u0001\u0000\u0000"+
		"\u0000\u034d\u0353\u0003j5\u0000\u034e\u0353\u0003\u0090H\u0000\u034f"+
		"\u0353\u0003R)\u0000\u0350\u0353\u0003\u00ceg\u0000\u0351\u0353\u0005"+
		"A\u0000\u0000\u0352\u034d\u0001\u0000\u0000\u0000\u0352\u034e\u0001\u0000"+
		"\u0000\u0000\u0352\u034f\u0001\u0000\u0000\u0000\u0352\u0350\u0001\u0000"+
		"\u0000\u0000\u0352\u0351\u0001\u0000\u0000\u0000\u0353i\u0001\u0000\u0000"+
		"\u0000\u0354\u0355\u0003l6\u0000\u0355\u0356\u0003x<\u0000\u0356\u0357"+
		"\u0003p8\u0000\u0357\u0358\u0005A\u0000\u0000\u0358k\u0001\u0000\u0000"+
		"\u0000\u0359\u035b\u0003n7\u0000\u035a\u0359\u0001\u0000\u0000\u0000\u035b"+
		"\u035e\u0001\u0000\u0000\u0000\u035c\u035a\u0001\u0000\u0000\u0000\u035c"+
		"\u035d\u0001\u0000\u0000\u0000\u035dm\u0001\u0000\u0000\u0000\u035e\u035c"+
		"\u0001\u0000\u0000\u0000\u035f\u0368\u0003\u00f0x\u0000\u0360\u0368\u0005"+
		"%\u0000\u0000\u0361\u0368\u0005$\u0000\u0000\u0362\u0368\u0005#\u0000"+
		"\u0000\u0363\u0368\u0005(\u0000\u0000\u0364\u0368\u0005\u0014\u0000\u0000"+
		"\u0365\u0368\u00050\u0000\u0000\u0366\u0368\u00053\u0000\u0000\u0367\u035f"+
		"\u0001\u0000\u0000\u0000\u0367\u0360\u0001\u0000\u0000\u0000\u0367\u0361"+
		"\u0001\u0000\u0000\u0000\u0367\u0362\u0001\u0000\u0000\u0000\u0367\u0363"+
		"\u0001\u0000\u0000\u0000\u0367\u0364\u0001\u0000\u0000\u0000\u0367\u0365"+
		"\u0001\u0000\u0000\u0000\u0367\u0366\u0001\u0000\u0000\u0000\u0368o\u0001"+
		"\u0000\u0000\u0000\u0369\u036e\u0003r9\u0000\u036a\u036b\u0005B\u0000"+
		"\u0000\u036b\u036d\u0003r9\u0000\u036c\u036a\u0001\u0000\u0000\u0000\u036d"+
		"\u0370\u0001\u0000\u0000\u0000\u036e\u036c\u0001\u0000\u0000\u0000\u036e"+
		"\u036f\u0001\u0000\u0000\u0000\u036fq\u0001\u0000\u0000\u0000\u0370\u036e"+
		"\u0001\u0000\u0000\u0000\u0371\u0374\u0003t:\u0000\u0372\u0373\u0005D"+
		"\u0000\u0000\u0373\u0375\u0003v;\u0000\u0374\u0372\u0001\u0000\u0000\u0000"+
		"\u0374\u0375\u0001\u0000\u0000\u0000\u0375s\u0001\u0000\u0000\u0000\u0376"+
		"\u0378\u0005h\u0000\u0000\u0377\u0379\u0003 \u0010\u0000\u0378\u0377\u0001"+
		"\u0000\u0000\u0000\u0378\u0379\u0001\u0000\u0000\u0000\u0379u\u0001\u0000"+
		"\u0000\u0000\u037a\u037d\u0003\u01ac\u00d6\u0000\u037b\u037d\u0003\u0106"+
		"\u0083\u0000\u037c\u037a\u0001\u0000\u0000\u0000\u037c\u037b\u0001\u0000"+
		"\u0000\u0000\u037dw\u0001\u0000\u0000\u0000\u037e\u0381\u0003z=\u0000"+
		"\u037f\u0381\u0003|>\u0000\u0380\u037e\u0001\u0000\u0000\u0000\u0380\u037f"+
		"\u0001\u0000\u0000\u0000\u0381y\u0001\u0000\u0000\u0000\u0382\u0385\u0003"+
		"\u0006\u0003\u0000\u0383\u0385\u0005\u0005\u0000\u0000\u0384\u0382\u0001"+
		"\u0000\u0000\u0000\u0384\u0383\u0001\u0000\u0000\u0000\u0385{\u0001\u0000"+
		"\u0000\u0000\u0386\u038a\u0003~?\u0000\u0387\u038a\u0003\u008cF\u0000"+
		"\u0388\u038a\u0003\u008eG\u0000\u0389\u0386\u0001\u0000\u0000\u0000\u0389"+
		"\u0387\u0001\u0000\u0000\u0000\u0389\u0388\u0001\u0000\u0000\u0000\u038a"+
		"}\u0001\u0000\u0000\u0000\u038b\u038e\u0003\u0084B\u0000\u038c\u038e\u0003"+
		"\u008aE\u0000\u038d\u038b\u0001\u0000\u0000\u0000\u038d\u038c\u0001\u0000"+
		"\u0000\u0000\u038e\u0393\u0001\u0000\u0000\u0000\u038f\u0392\u0003\u0082"+
		"A\u0000\u0390\u0392\u0003\u0088D\u0000\u0391\u038f\u0001\u0000\u0000\u0000"+
		"\u0391\u0390\u0001\u0000\u0000\u0000\u0392\u0395\u0001\u0000\u0000\u0000"+
		"\u0393\u0391\u0001\u0000\u0000\u0000\u0393\u0394\u0001\u0000\u0000\u0000"+
		"\u0394\u007f\u0001\u0000\u0000\u0000\u0395\u0393\u0001\u0000\u0000\u0000"+
		"\u0396\u0398\u0005h\u0000\u0000\u0397\u0399\u0003*\u0015\u0000\u0398\u0397"+
		"\u0001\u0000\u0000\u0000\u0398\u0399\u0001\u0000\u0000\u0000\u0399\u03a1"+
		"\u0001\u0000\u0000\u0000\u039a\u039b\u0003~?\u0000\u039b\u039c\u0005C"+
		"\u0000\u0000\u039c\u039e\u0003\u00f2y\u0000\u039d\u039f\u0003*\u0015\u0000"+
		"\u039e\u039d\u0001\u0000\u0000\u0000\u039e\u039f\u0001\u0000\u0000\u0000"+
		"\u039f\u03a1\u0001\u0000\u0000\u0000\u03a0\u0396\u0001\u0000\u0000\u0000"+
		"\u03a0\u039a\u0001\u0000\u0000\u0000\u03a1\u0081\u0001\u0000\u0000\u0000"+
		"\u03a2\u03a3\u0005C\u0000\u0000\u03a3\u03a5\u0003\u00f2y\u0000\u03a4\u03a6"+
		"\u0003*\u0015\u0000\u03a5\u03a4\u0001\u0000\u0000\u0000\u03a5\u03a6\u0001"+
		"\u0000\u0000\u0000\u03a6\u0083\u0001\u0000\u0000\u0000\u03a7\u03a9\u0005"+
		"h\u0000\u0000\u03a8\u03aa\u0003*\u0015\u0000\u03a9\u03a8\u0001\u0000\u0000"+
		"\u0000\u03a9\u03aa\u0001\u0000\u0000\u0000\u03aa\u0085\u0001\u0000\u0000"+
		"\u0000\u03ab\u03ac\u0003\u0080@\u0000\u03ac\u0087\u0001\u0000\u0000\u0000"+
		"\u03ad\u03ae\u0003\u0082A\u0000\u03ae\u0089\u0001\u0000\u0000\u0000\u03af"+
		"\u03b0\u0003\u0084B\u0000\u03b0\u008b\u0001\u0000\u0000\u0000\u03b1\u03b2"+
		"\u0005h\u0000\u0000\u03b2\u008d\u0001\u0000\u0000\u0000\u03b3\u03b4\u0003"+
		"z=\u0000\u03b4\u03b5\u0003 \u0010\u0000\u03b5\u03bd\u0001\u0000\u0000"+
		"\u0000\u03b6\u03b7\u0003~?\u0000\u03b7\u03b8\u0003 \u0010\u0000\u03b8"+
		"\u03bd\u0001\u0000\u0000\u0000\u03b9\u03ba\u0003\u008cF\u0000\u03ba\u03bb"+
		"\u0003 \u0010\u0000\u03bb\u03bd\u0001\u0000\u0000\u0000\u03bc\u03b3\u0001"+
		"\u0000\u0000\u0000\u03bc\u03b6\u0001\u0000\u0000\u0000\u03bc\u03b9\u0001"+
		"\u0000\u0000\u0000\u03bd\u008f\u0001\u0000\u0000\u0000\u03be\u03bf\u0003"+
		"\u0092I\u0000\u03bf\u03c0\u0003\u0096K\u0000\u03c0\u03c1\u0003\u00aeW"+
		"\u0000\u03c1\u0091\u0001\u0000\u0000\u0000\u03c2\u03c4\u0003\u0094J\u0000"+
		"\u03c3\u03c2\u0001\u0000\u0000\u0000\u03c4\u03c7\u0001\u0000\u0000\u0000"+
		"\u03c5\u03c3\u0001\u0000\u0000\u0000\u03c5\u03c6\u0001\u0000\u0000\u0000"+
		"\u03c6\u0093\u0001\u0000\u0000\u0000\u03c7\u03c5\u0001\u0000\u0000\u0000"+
		"\u03c8\u03d3\u0003\u00f0x\u0000\u03c9\u03d3\u0005%\u0000\u0000\u03ca\u03d3"+
		"\u0005$\u0000\u0000\u03cb\u03d3\u0005#\u0000\u0000\u03cc\u03d3\u0005\u0003"+
		"\u0000\u0000\u03cd\u03d3\u0005(\u0000\u0000\u03ce\u03d3\u0005\u0014\u0000"+
		"\u0000\u03cf\u03d3\u0005,\u0000\u0000\u03d0\u03d3\u0005 \u0000\u0000\u03d1"+
		"\u03d3\u0005)\u0000\u0000\u03d2\u03c8\u0001\u0000\u0000\u0000\u03d2\u03c9"+
		"\u0001\u0000\u0000\u0000\u03d2\u03ca\u0001\u0000\u0000\u0000\u03d2\u03cb"+
		"\u0001\u0000\u0000\u0000\u03d2\u03cc\u0001\u0000\u0000\u0000\u03d2\u03cd"+
		"\u0001\u0000\u0000\u0000\u03d2\u03ce\u0001\u0000\u0000\u0000\u03d2\u03cf"+
		"\u0001\u0000\u0000\u0000\u03d2\u03d0\u0001\u0000\u0000\u0000\u03d2\u03d1"+
		"\u0001\u0000\u0000\u0000\u03d3\u0095\u0001\u0000\u0000\u0000\u03d4\u03d5"+
		"\u0003\u0098L\u0000\u03d5\u03d7\u0003\u009aM\u0000\u03d6\u03d8\u0003\u00a8"+
		"T\u0000\u03d7\u03d6\u0001\u0000\u0000\u0000\u03d7\u03d8\u0001\u0000\u0000"+
		"\u0000\u03d8\u03e6\u0001\u0000\u0000\u0000\u03d9\u03dd\u0003Z-\u0000\u03da"+
		"\u03dc\u0003\u00f0x\u0000\u03db\u03da\u0001\u0000\u0000\u0000\u03dc\u03df"+
		"\u0001\u0000\u0000\u0000\u03dd\u03db\u0001\u0000\u0000\u0000\u03dd\u03de"+
		"\u0001\u0000\u0000\u0000\u03de\u03e0\u0001\u0000\u0000\u0000\u03df\u03dd"+
		"\u0001\u0000\u0000\u0000\u03e0\u03e1\u0003\u0098L\u0000\u03e1\u03e3\u0003"+
		"\u009aM\u0000\u03e2\u03e4\u0003\u00a8T\u0000\u03e3\u03e2\u0001\u0000\u0000"+
		"\u0000\u03e3\u03e4\u0001\u0000\u0000\u0000\u03e4\u03e6\u0001\u0000\u0000"+
		"\u0000\u03e5\u03d4\u0001\u0000\u0000\u0000\u03e5\u03d9\u0001\u0000\u0000"+
		"\u0000\u03e6\u0097\u0001\u0000\u0000\u0000\u03e7\u03ea\u0003x<\u0000\u03e8"+
		"\u03ea\u00052\u0000\u0000\u03e9\u03e7\u0001\u0000\u0000\u0000\u03e9\u03e8"+
		"\u0001\u0000\u0000\u0000\u03ea\u0099\u0001\u0000\u0000\u0000\u03eb\u03ec"+
		"\u0005h\u0000\u0000\u03ec\u03ee\u0005;\u0000\u0000\u03ed\u03ef\u0003\u009c"+
		"N\u0000\u03ee\u03ed\u0001\u0000\u0000\u0000\u03ee\u03ef\u0001\u0000\u0000"+
		"\u0000\u03ef\u03f0\u0001\u0000\u0000\u0000\u03f0\u03f2\u0005<\u0000\u0000"+
		"\u03f1\u03f3\u0003 \u0010\u0000\u03f2\u03f1\u0001\u0000\u0000\u0000\u03f2"+
		"\u03f3\u0001\u0000\u0000\u0000\u03f3\u009b\u0001\u0000\u0000\u0000\u03f4"+
		"\u03f5\u0003\u009eO\u0000\u03f5\u03f6\u0005B\u0000\u0000\u03f6\u03f7\u0003"+
		"\u00a4R\u0000\u03f7\u03fa\u0001\u0000\u0000\u0000\u03f8\u03fa\u0003\u00a4"+
		"R\u0000\u03f9\u03f4\u0001\u0000\u0000\u0000\u03f9\u03f8\u0001\u0000\u0000"+
		"\u0000\u03fa\u009d\u0001\u0000\u0000\u0000\u03fb\u0400\u0003\u00a0P\u0000"+
		"\u03fc\u03fd\u0005B\u0000\u0000\u03fd\u03ff\u0003\u00a0P\u0000\u03fe\u03fc"+
		"\u0001\u0000\u0000\u0000\u03ff\u0402\u0001\u0000\u0000\u0000\u0400\u03fe"+
		"\u0001\u0000\u0000\u0000\u0400\u0401\u0001\u0000\u0000\u0000\u0401\u040c"+
		"\u0001\u0000\u0000\u0000\u0402\u0400\u0001\u0000\u0000\u0000\u0403\u0408"+
		"\u0003\u00a6S\u0000\u0404\u0405\u0005B\u0000\u0000\u0405\u0407\u0003\u00a0"+
		"P\u0000\u0406\u0404\u0001\u0000\u0000\u0000\u0407\u040a\u0001\u0000\u0000"+
		"\u0000\u0408\u0406\u0001\u0000\u0000\u0000\u0408\u0409\u0001\u0000\u0000"+
		"\u0000\u0409\u040c\u0001\u0000\u0000\u0000\u040a\u0408\u0001\u0000\u0000"+
		"\u0000\u040b\u03fb\u0001\u0000\u0000\u0000\u040b\u0403\u0001\u0000\u0000"+
		"\u0000\u040c\u009f\u0001\u0000\u0000\u0000\u040d\u040f\u0003\u00a2Q\u0000"+
		"\u040e\u040d\u0001\u0000\u0000\u0000\u040f\u0412\u0001\u0000\u0000\u0000"+
		"\u0410\u040e\u0001\u0000\u0000\u0000\u0410\u0411\u0001\u0000\u0000\u0000"+
		"\u0411\u0413\u0001\u0000\u0000\u0000\u0412\u0410\u0001\u0000\u0000\u0000"+
		"\u0413\u0414\u0003x<\u0000\u0414\u0415\u0003t:\u0000\u0415\u00a1\u0001"+
		"\u0000\u0000\u0000\u0416\u0419\u0003\u00f0x\u0000\u0417\u0419\u0005\u0014"+
		"\u0000\u0000\u0418\u0416\u0001\u0000\u0000\u0000\u0418\u0417\u0001\u0000"+
		"\u0000\u0000\u0419\u00a3\u0001\u0000\u0000\u0000\u041a\u041c\u0003\u00a2"+
		"Q\u0000\u041b\u041a\u0001\u0000\u0000\u0000\u041c\u041f\u0001\u0000\u0000"+
		"\u0000\u041d\u041b\u0001\u0000\u0000\u0000\u041d\u041e\u0001\u0000\u0000"+
		"\u0000\u041e\u0420\u0001\u0000\u0000\u0000\u041f\u041d\u0001\u0000\u0000"+
		"\u0000\u0420\u0424\u0003x<\u0000\u0421\u0423\u0003\u00f0x\u0000\u0422"+
		"\u0421\u0001\u0000\u0000\u0000\u0423\u0426\u0001\u0000\u0000\u0000\u0424"+
		"\u0422\u0001\u0000\u0000\u0000\u0424\u0425\u0001\u0000\u0000\u0000\u0425"+
		"\u0427\u0001\u0000\u0000\u0000\u0426\u0424\u0001\u0000\u0000\u0000\u0427"+
		"\u0428\u0005j\u0000\u0000\u0428\u0429\u0003t:\u0000\u0429\u042c\u0001"+
		"\u0000\u0000\u0000\u042a\u042c\u0003\u00a0P\u0000\u042b\u041d\u0001\u0000"+
		"\u0000\u0000\u042b\u042a\u0001\u0000\u0000\u0000\u042c\u00a5\u0001\u0000"+
		"\u0000\u0000\u042d\u042f\u0003\u00f0x\u0000\u042e\u042d\u0001\u0000\u0000"+
		"\u0000\u042f\u0432\u0001\u0000\u0000\u0000\u0430\u042e\u0001\u0000\u0000"+
		"\u0000\u0430\u0431\u0001\u0000\u0000\u0000\u0431\u0433\u0001\u0000\u0000"+
		"\u0000\u0432\u0430\u0001\u0000\u0000\u0000\u0433\u0436\u0003x<\u0000\u0434"+
		"\u0435\u0005h\u0000\u0000\u0435\u0437\u0005C\u0000\u0000\u0436\u0434\u0001"+
		"\u0000\u0000\u0000\u0436\u0437\u0001\u0000\u0000\u0000\u0437\u0438\u0001"+
		"\u0000\u0000\u0000\u0438\u0439\u0005-\u0000\u0000\u0439\u00a7\u0001\u0000"+
		"\u0000\u0000\u043a\u043b\u0005/\u0000\u0000\u043b\u043c\u0003\u00aaU\u0000"+
		"\u043c\u00a9\u0001\u0000\u0000\u0000\u043d\u0442\u0003\u00acV\u0000\u043e"+
		"\u043f\u0005B\u0000\u0000\u043f\u0441\u0003\u00acV\u0000\u0440\u043e\u0001"+
		"\u0000\u0000\u0000\u0441\u0444\u0001\u0000\u0000\u0000\u0442\u0440\u0001"+
		"\u0000\u0000\u0000\u0442\u0443\u0001\u0000\u0000\u0000\u0443\u00ab\u0001"+
		"\u0000\u0000\u0000\u0444\u0442\u0001\u0000\u0000\u0000\u0445\u0448\u0003"+
		"\u0010\b\u0000\u0446\u0448\u0003\u001c\u000e\u0000\u0447\u0445\u0001\u0000"+
		"\u0000\u0000\u0447\u0446\u0001\u0000\u0000\u0000\u0448\u00ad\u0001\u0000"+
		"\u0000\u0000\u0449\u044c\u0003\u010a\u0085\u0000\u044a\u044c\u0005A\u0000"+
		"\u0000\u044b\u0449\u0001\u0000\u0000\u0000\u044b\u044a\u0001\u0000\u0000"+
		"\u0000\u044c\u00af\u0001\u0000\u0000\u0000\u044d\u044e\u0003\u010a\u0085"+
		"\u0000\u044e\u00b1\u0001\u0000\u0000\u0000\u044f\u0450\u0005(\u0000\u0000"+
		"\u0450\u0451\u0003\u010a\u0085\u0000\u0451\u00b3\u0001\u0000\u0000\u0000"+
		"\u0452\u0453\u0003\u00b6[\u0000\u0453\u0455\u0003\u00ba]\u0000\u0454\u0456"+
		"\u0003\u00a8T\u0000\u0455\u0454\u0001\u0000\u0000\u0000\u0455\u0456\u0001"+
		"\u0000\u0000\u0000\u0456\u0457\u0001\u0000\u0000\u0000\u0457\u0458\u0003"+
		"\u00be_\u0000\u0458\u00b5\u0001\u0000\u0000\u0000\u0459\u045b\u0003\u00b8"+
		"\\\u0000\u045a\u0459\u0001\u0000\u0000\u0000\u045b\u045e\u0001\u0000\u0000"+
		"\u0000\u045c\u045a\u0001\u0000\u0000\u0000\u045c\u045d\u0001\u0000\u0000"+
		"\u0000\u045d\u00b7\u0001\u0000\u0000\u0000\u045e\u045c\u0001\u0000\u0000"+
		"\u0000\u045f\u0464\u0003\u00f0x\u0000\u0460\u0464\u0005%\u0000\u0000\u0461"+
		"\u0464\u0005$\u0000\u0000\u0462\u0464\u0005#\u0000\u0000\u0463\u045f\u0001"+
		"\u0000\u0000\u0000\u0463\u0460\u0001\u0000\u0000\u0000\u0463\u0461\u0001"+
		"\u0000\u0000\u0000\u0463\u0462\u0001\u0000\u0000\u0000\u0464\u00b9\u0001"+
		"\u0000\u0000\u0000\u0465\u0467\u0003Z-\u0000\u0466\u0465\u0001\u0000\u0000"+
		"\u0000\u0466\u0467\u0001\u0000\u0000\u0000\u0467\u0468\u0001\u0000\u0000"+
		"\u0000\u0468\u0469\u0003\u00bc^\u0000\u0469\u046b\u0005;\u0000\u0000\u046a"+
		"\u046c\u0003\u009cN\u0000\u046b\u046a\u0001\u0000\u0000\u0000\u046b\u046c"+
		"\u0001\u0000\u0000\u0000\u046c\u046d\u0001\u0000\u0000\u0000\u046d\u046e"+
		"\u0005<\u0000\u0000\u046e\u00bb\u0001\u0000\u0000\u0000\u046f\u0470\u0005"+
		"h\u0000\u0000\u0470\u00bd\u0001\u0000\u0000\u0000\u0471\u0473\u0005=\u0000"+
		"\u0000\u0472\u0474\u0003\u00c0`\u0000\u0473\u0472\u0001\u0000\u0000\u0000"+
		"\u0473\u0474\u0001\u0000\u0000\u0000\u0474\u0476\u0001\u0000\u0000\u0000"+
		"\u0475\u0477\u0003\u010c\u0086\u0000\u0476\u0475\u0001\u0000\u0000\u0000"+
		"\u0476\u0477\u0001\u0000\u0000\u0000\u0477\u0478\u0001\u0000\u0000\u0000"+
		"\u0478\u0479\u0005>\u0000\u0000\u0479\u00bf\u0001\u0000\u0000\u0000\u047a"+
		"\u047c\u0003*\u0015\u0000\u047b\u047a\u0001\u0000\u0000\u0000\u047b\u047c"+
		"\u0001\u0000\u0000\u0000\u047c\u047d\u0001\u0000\u0000\u0000\u047d\u047e"+
		"\u0005-\u0000\u0000\u047e\u0480\u0005;\u0000\u0000\u047f\u0481\u0003\u019c"+
		"\u00ce\u0000\u0480\u047f\u0001\u0000\u0000\u0000\u0480\u0481\u0001\u0000"+
		"\u0000\u0000\u0481\u0482\u0001\u0000\u0000\u0000\u0482\u0483\u0005<\u0000"+
		"\u0000\u0483\u04a9\u0005A\u0000\u0000\u0484\u0486\u0003*\u0015\u0000\u0485"+
		"\u0484\u0001\u0000\u0000\u0000\u0485\u0486\u0001\u0000\u0000\u0000\u0486"+
		"\u0487\u0001\u0000\u0000\u0000\u0487\u0488\u0005*\u0000\u0000\u0488\u048a"+
		"\u0005;\u0000\u0000\u0489\u048b\u0003\u019c\u00ce\u0000\u048a\u0489\u0001"+
		"\u0000\u0000\u0000\u048a\u048b\u0001\u0000\u0000\u0000\u048b\u048c\u0001"+
		"\u0000\u0000\u0000\u048c\u048d\u0005<\u0000\u0000\u048d\u04a9\u0005A\u0000"+
		"\u0000\u048e\u048f\u0003:\u001d\u0000\u048f\u0491\u0005C\u0000\u0000\u0490"+
		"\u0492\u0003*\u0015\u0000\u0491\u0490\u0001\u0000\u0000\u0000\u0491\u0492"+
		"\u0001\u0000\u0000\u0000\u0492\u0493\u0001\u0000\u0000\u0000\u0493\u0494"+
		"\u0005*\u0000\u0000\u0494\u0496\u0005;\u0000\u0000\u0495\u0497\u0003\u019c"+
		"\u00ce\u0000\u0496\u0495\u0001\u0000\u0000\u0000\u0496\u0497\u0001\u0000"+
		"\u0000\u0000\u0497\u0498\u0001\u0000\u0000\u0000\u0498\u0499\u0005<\u0000"+
		"\u0000\u0499\u049a\u0005A\u0000\u0000\u049a\u04a9\u0001\u0000\u0000\u0000"+
		"\u049b\u049c\u0003\u016e\u00b7\u0000\u049c\u049e\u0005C\u0000\u0000\u049d"+
		"\u049f\u0003*\u0015\u0000\u049e\u049d\u0001\u0000\u0000\u0000\u049e\u049f"+
		"\u0001\u0000\u0000\u0000\u049f\u04a0\u0001\u0000\u0000\u0000\u04a0\u04a1"+
		"\u0005*\u0000\u0000\u04a1\u04a3\u0005;\u0000\u0000\u04a2\u04a4\u0003\u019c"+
		"\u00ce\u0000\u04a3\u04a2\u0001\u0000\u0000\u0000\u04a3\u04a4\u0001\u0000"+
		"\u0000\u0000\u04a4\u04a5\u0001\u0000\u0000\u0000\u04a5\u04a6\u0005<\u0000"+
		"\u0000\u04a6\u04a7\u0005A\u0000\u0000\u04a7\u04a9\u0001\u0000\u0000\u0000"+
		"\u04a8\u047b\u0001\u0000\u0000\u0000\u04a8\u0485\u0001\u0000\u0000\u0000"+
		"\u04a8\u048e\u0001\u0000\u0000\u0000\u04a8\u049b\u0001\u0000\u0000\u0000"+
		"\u04a9\u00c1\u0001\u0000\u0000\u0000\u04aa\u04ab\u0003V+\u0000\u04ab\u04ac"+
		"\u0005\u0012\u0000\u0000\u04ac\u04ae\u0005h\u0000\u0000\u04ad\u04af\u0003"+
		"`0\u0000\u04ae\u04ad\u0001\u0000\u0000\u0000\u04ae\u04af\u0001\u0000\u0000"+
		"\u0000\u04af\u04b0\u0001\u0000\u0000\u0000\u04b0\u04b1\u0003\u00c4b\u0000"+
		"\u04b1\u00c3\u0001\u0000\u0000\u0000\u04b2\u04b4\u0005=\u0000\u0000\u04b3"+
		"\u04b5\u0003\u00c6c\u0000\u04b4\u04b3\u0001\u0000\u0000\u0000\u04b4\u04b5"+
		"\u0001\u0000\u0000\u0000\u04b5\u04b7\u0001\u0000\u0000\u0000\u04b6\u04b8"+
		"\u0005B\u0000\u0000\u04b7\u04b6\u0001\u0000\u0000\u0000\u04b7\u04b8\u0001"+
		"\u0000\u0000\u0000\u04b8\u04ba\u0001\u0000\u0000\u0000\u04b9\u04bb\u0003"+
		"\u00ccf\u0000\u04ba\u04b9\u0001\u0000\u0000\u0000\u04ba\u04bb\u0001\u0000"+
		"\u0000\u0000\u04bb\u04bc\u0001\u0000\u0000\u0000\u04bc\u04bd\u0005>\u0000"+
		"\u0000\u04bd\u00c5\u0001\u0000\u0000\u0000\u04be\u04c3\u0003\u00c8d\u0000"+
		"\u04bf\u04c0\u0005B\u0000\u0000\u04c0\u04c2\u0003\u00c8d\u0000\u04c1\u04bf"+
		"\u0001\u0000\u0000\u0000\u04c2\u04c5\u0001\u0000\u0000\u0000\u04c3\u04c1"+
		"\u0001\u0000\u0000\u0000\u04c3\u04c4\u0001\u0000\u0000\u0000\u04c4\u00c7"+
		"\u0001\u0000\u0000\u0000\u04c5\u04c3\u0001\u0000\u0000\u0000\u04c6\u04c8"+
		"\u0003\u00cae\u0000\u04c7\u04c6\u0001\u0000\u0000\u0000\u04c8\u04cb\u0001"+
		"\u0000\u0000\u0000\u04c9\u04c7\u0001\u0000\u0000\u0000\u04c9\u04ca\u0001"+
		"\u0000\u0000\u0000\u04ca\u04cc\u0001\u0000\u0000\u0000\u04cb\u04c9\u0001"+
		"\u0000\u0000\u0000\u04cc\u04d2\u0005h\u0000\u0000\u04cd\u04cf\u0005;\u0000"+
		"\u0000\u04ce\u04d0\u0003\u019c\u00ce\u0000\u04cf\u04ce\u0001\u0000\u0000"+
		"\u0000\u04cf\u04d0\u0001\u0000\u0000\u0000\u04d0\u04d1\u0001\u0000\u0000"+
		"\u0000\u04d1\u04d3\u0005<\u0000\u0000\u04d2\u04cd\u0001\u0000\u0000\u0000"+
		"\u04d2\u04d3\u0001\u0000\u0000\u0000\u04d3\u04d5\u0001\u0000\u0000\u0000"+
		"\u04d4\u04d6\u0003d2\u0000\u04d5\u04d4\u0001\u0000\u0000\u0000\u04d5\u04d6"+
		"\u0001\u0000\u0000\u0000\u04d6\u00c9\u0001\u0000\u0000\u0000\u04d7\u04d8"+
		"\u0003\u00f0x\u0000\u04d8\u00cb\u0001\u0000\u0000\u0000\u04d9\u04dd\u0005"+
		"A\u0000\u0000\u04da\u04dc\u0003f3\u0000\u04db\u04da\u0001\u0000\u0000"+
		"\u0000\u04dc\u04df\u0001\u0000\u0000\u0000\u04dd\u04db\u0001\u0000\u0000"+
		"\u0000\u04dd\u04de\u0001\u0000\u0000\u0000\u04de\u00cd\u0001\u0000\u0000"+
		"\u0000\u04df\u04dd\u0001\u0000\u0000\u0000\u04e0\u04e3\u0003\u00d0h\u0000"+
		"\u04e1\u04e3\u0003\u00e4r\u0000\u04e2\u04e0\u0001\u0000\u0000\u0000\u04e2"+
		"\u04e1\u0001\u0000\u0000\u0000\u04e3\u00cf\u0001\u0000\u0000\u0000\u04e4"+
		"\u04e5\u0003\u00d2i\u0000\u04e5\u04e6\u0005\u001e\u0000\u0000\u04e6\u04e8"+
		"\u0005h\u0000\u0000\u04e7\u04e9\u0003Z-\u0000\u04e8\u04e7\u0001\u0000"+
		"\u0000\u0000\u04e8\u04e9\u0001\u0000\u0000\u0000\u04e9\u04eb\u0001\u0000"+
		"\u0000\u0000\u04ea\u04ec\u0003\u00d6k\u0000\u04eb\u04ea\u0001\u0000\u0000"+
		"\u0000\u04eb\u04ec\u0001\u0000\u0000\u0000\u04ec\u04ed\u0001\u0000\u0000"+
		"\u0000\u04ed\u04ee\u0003\u00d8l\u0000\u04ee\u00d1\u0001\u0000\u0000\u0000"+
		"\u04ef\u04f1\u0003\u00d4j\u0000\u04f0\u04ef\u0001\u0000\u0000\u0000\u04f1"+
		"\u04f4\u0001\u0000\u0000\u0000\u04f2\u04f0\u0001\u0000\u0000\u0000\u04f2"+
		"\u04f3\u0001\u0000\u0000\u0000\u04f3\u00d3\u0001\u0000\u0000\u0000\u04f4"+
		"\u04f2\u0001\u0000\u0000\u0000\u04f5\u04fd\u0003\u00f0x\u0000\u04f6\u04fd"+
		"\u0005%\u0000\u0000\u04f7\u04fd\u0005$\u0000\u0000\u04f8\u04fd\u0005#"+
		"\u0000\u0000\u04f9\u04fd\u0005\u0003\u0000\u0000\u04fa\u04fd\u0005(\u0000"+
		"\u0000\u04fb\u04fd\u0005)\u0000\u0000\u04fc\u04f5\u0001\u0000\u0000\u0000"+
		"\u04fc\u04f6\u0001\u0000\u0000\u0000\u04fc\u04f7\u0001\u0000\u0000\u0000"+
		"\u04fc\u04f8\u0001\u0000\u0000\u0000\u04fc\u04f9\u0001\u0000\u0000\u0000"+
		"\u04fc\u04fa\u0001\u0000\u0000\u0000\u04fc\u04fb\u0001\u0000\u0000\u0000"+
		"\u04fd\u00d5\u0001\u0000\u0000\u0000\u04fe\u04ff\u0005\u0013\u0000\u0000"+
		"\u04ff\u0500\u0003b1\u0000\u0500\u00d7\u0001\u0000\u0000\u0000\u0501\u0505"+
		"\u0005=\u0000\u0000\u0502\u0504\u0003\u00dam\u0000\u0503\u0502\u0001\u0000"+
		"\u0000\u0000\u0504\u0507\u0001\u0000\u0000\u0000\u0505\u0503\u0001\u0000"+
		"\u0000\u0000\u0505\u0506\u0001\u0000\u0000\u0000\u0506\u0508\u0001\u0000"+
		"\u0000\u0000\u0507\u0505\u0001\u0000\u0000\u0000\u0508\u0509\u0005>\u0000"+
		"\u0000\u0509\u00d9\u0001\u0000\u0000\u0000\u050a\u0510\u0003\u00dcn\u0000"+
		"\u050b\u0510\u0003\u00e0p\u0000\u050c\u0510\u0003R)\u0000\u050d\u0510"+
		"\u0003\u00ceg\u0000\u050e\u0510\u0005A\u0000\u0000\u050f\u050a\u0001\u0000"+
		"\u0000\u0000\u050f\u050b\u0001\u0000\u0000\u0000\u050f\u050c\u0001\u0000"+
		"\u0000\u0000\u050f\u050d\u0001\u0000\u0000\u0000\u050f\u050e\u0001\u0000"+
		"\u0000\u0000\u0510\u00db\u0001\u0000\u0000\u0000\u0511\u0513\u0003\u00de"+
		"o\u0000\u0512\u0511\u0001\u0000\u0000\u0000\u0513\u0516\u0001\u0000\u0000"+
		"\u0000\u0514\u0512\u0001\u0000\u0000\u0000\u0514\u0515\u0001\u0000\u0000"+
		"\u0000\u0515\u0517\u0001\u0000\u0000\u0000\u0516\u0514\u0001\u0000\u0000"+
		"\u0000\u0517\u0518\u0003x<\u0000\u0518\u0519\u0003p8\u0000\u0519\u051a"+
		"\u0005A\u0000\u0000\u051a\u00dd\u0001\u0000\u0000\u0000\u051b\u0520\u0003"+
		"\u00f0x\u0000\u051c\u0520\u0005%\u0000\u0000\u051d\u0520\u0005(\u0000"+
		"\u0000\u051e\u0520\u0005\u0014\u0000\u0000\u051f\u051b\u0001\u0000\u0000"+
		"\u0000\u051f\u051c\u0001\u0000\u0000\u0000\u051f\u051d\u0001\u0000\u0000"+
		"\u0000\u051f\u051e\u0001\u0000\u0000\u0000\u0520\u00df\u0001\u0000\u0000"+
		"\u0000\u0521\u0523\u0003\u00e2q\u0000\u0522\u0521\u0001\u0000\u0000\u0000"+
		"\u0523\u0526\u0001\u0000\u0000\u0000\u0524\u0522\u0001\u0000\u0000\u0000"+
		"\u0524\u0525\u0001\u0000\u0000\u0000\u0525\u0527\u0001\u0000\u0000\u0000"+
		"\u0526\u0524\u0001\u0000\u0000\u0000\u0527\u0528\u0003\u0096K\u0000\u0528"+
		"\u0529\u0003\u00aeW\u0000\u0529\u00e1\u0001\u0000\u0000\u0000\u052a\u0531"+
		"\u0003\u00f0x\u0000\u052b\u0531\u0005%\u0000\u0000\u052c\u0531\u0005\u0003"+
		"\u0000\u0000\u052d\u0531\u0005\u000e\u0000\u0000\u052e\u0531\u0005(\u0000"+
		"\u0000\u052f\u0531\u0005)\u0000\u0000\u0530\u052a\u0001\u0000\u0000\u0000"+
		"\u0530\u052b\u0001\u0000\u0000\u0000\u0530\u052c\u0001\u0000\u0000\u0000"+
		"\u0530\u052d\u0001\u0000\u0000\u0000\u0530\u052e\u0001\u0000\u0000\u0000"+
		"\u0530\u052f\u0001\u0000\u0000\u0000\u0531\u00e3\u0001\u0000\u0000\u0000"+
		"\u0532\u0534\u0003\u00d4j\u0000\u0533\u0532\u0001\u0000\u0000\u0000\u0534"+
		"\u0537\u0001\u0000\u0000\u0000\u0535\u0533\u0001\u0000\u0000\u0000\u0535"+
		"\u0536\u0001\u0000\u0000\u0000\u0536\u0538\u0001\u0000\u0000\u0000\u0537"+
		"\u0535\u0001\u0000\u0000\u0000\u0538\u0539\u0005i\u0000\u0000\u0539\u053a"+
		"\u0005\u001e\u0000\u0000\u053a\u053b\u0005h\u0000\u0000\u053b\u053c\u0003"+
		"\u00e6s\u0000\u053c\u00e5\u0001\u0000\u0000\u0000\u053d\u0541\u0005=\u0000"+
		"\u0000\u053e\u0540\u0003\u00e8t\u0000\u053f\u053e\u0001\u0000\u0000\u0000"+
		"\u0540\u0543\u0001\u0000\u0000\u0000\u0541\u053f\u0001\u0000\u0000\u0000"+
		"\u0541\u0542\u0001\u0000\u0000\u0000\u0542\u0544\u0001\u0000\u0000\u0000"+
		"\u0543\u0541\u0001\u0000\u0000\u0000\u0544\u0545\u0005>\u0000\u0000\u0545"+
		"\u00e7\u0001\u0000\u0000\u0000\u0546\u054c\u0003\u00eau\u0000\u0547\u054c"+
		"\u0003\u00dcn\u0000\u0548\u054c\u0003R)\u0000\u0549\u054c\u0003\u00ce"+
		"g\u0000\u054a\u054c\u0005A\u0000\u0000\u054b\u0546\u0001\u0000\u0000\u0000"+
		"\u054b\u0547\u0001\u0000\u0000\u0000\u054b\u0548\u0001\u0000\u0000\u0000"+
		"\u054b\u0549\u0001\u0000\u0000\u0000\u054b\u054a\u0001\u0000\u0000\u0000"+
		"\u054c\u00e9\u0001\u0000\u0000\u0000\u054d\u054f\u0003\u00ecv\u0000\u054e"+
		"\u054d\u0001\u0000\u0000\u0000\u054f\u0552\u0001\u0000\u0000\u0000\u0550"+
		"\u054e\u0001\u0000\u0000\u0000\u0550\u0551\u0001\u0000\u0000\u0000\u0551"+
		"\u0553\u0001\u0000\u0000\u0000\u0552\u0550\u0001\u0000\u0000\u0000\u0553"+
		"\u0554\u0003x<\u0000\u0554\u0555\u0005h\u0000\u0000\u0555\u0556\u0005"+
		";\u0000\u0000\u0556\u0558\u0005<\u0000\u0000\u0557\u0559\u0003 \u0010"+
		"\u0000\u0558\u0557\u0001\u0000\u0000\u0000\u0558\u0559\u0001\u0000\u0000"+
		"\u0000\u0559\u055b\u0001\u0000\u0000\u0000\u055a\u055c\u0003\u00eew\u0000"+
		"\u055b\u055a\u0001\u0000\u0000\u0000\u055b\u055c\u0001\u0000\u0000\u0000"+
		"\u055c\u055d\u0001\u0000\u0000\u0000\u055d\u055e\u0005A\u0000\u0000\u055e"+
		"\u00eb\u0001\u0000\u0000\u0000\u055f\u0563\u0003\u00f0x\u0000\u0560\u0563"+
		"\u0005%\u0000\u0000\u0561\u0563\u0005\u0003\u0000\u0000\u0562\u055f\u0001"+
		"\u0000\u0000\u0000\u0562\u0560\u0001\u0000\u0000\u0000\u0562\u0561\u0001"+
		"\u0000\u0000\u0000\u0563\u00ed\u0001\u0000\u0000\u0000\u0564\u0565\u0005"+
		"\u000e\u0000\u0000\u0565\u0566\u0003\u00fc~\u0000\u0566\u00ef\u0001\u0000"+
		"\u0000\u0000\u0567\u056b\u0003\u00f6{\u0000\u0568\u056b\u0003\u0102\u0081"+
		"\u0000\u0569\u056b\u0003\u0104\u0082\u0000\u056a\u0567\u0001\u0000\u0000"+
		"\u0000\u056a\u0568\u0001\u0000\u0000\u0000\u056a\u0569\u0001\u0000\u0000"+
		"\u0000\u056b\u00f1\u0001\u0000\u0000\u0000\u056c\u056e\u0003\u00f0x\u0000"+
		"\u056d\u056c\u0001\u0000\u0000\u0000\u056e\u0571\u0001\u0000\u0000\u0000"+
		"\u056f\u056d\u0001\u0000\u0000\u0000\u056f\u0570\u0001\u0000\u0000\u0000"+
		"\u0570\u0572\u0001\u0000\u0000\u0000\u0571\u056f\u0001\u0000\u0000\u0000"+
		"\u0572\u0573\u0005h\u0000\u0000\u0573\u00f3\u0001\u0000\u0000\u0000\u0574"+
		"\u0576\u0003\u00f0x\u0000\u0575\u0574\u0001\u0000\u0000\u0000\u0576\u0579"+
		"\u0001\u0000\u0000\u0000\u0577\u0575\u0001\u0000\u0000\u0000\u0577\u0578"+
		"\u0001\u0000\u0000\u0000\u0578\u057a\u0001\u0000\u0000\u0000\u0579\u0577"+
		"\u0001\u0000\u0000\u0000\u057a\u057b\u0003\u01c4\u00e2\u0000\u057b\u00f5"+
		"\u0001\u0000\u0000\u0000\u057c\u057d\u0005i\u0000\u0000\u057d\u057e\u0003"+
		"6\u001b\u0000\u057e\u0580\u0005;\u0000\u0000\u057f\u0581\u0003\u00f8|"+
		"\u0000\u0580\u057f\u0001\u0000\u0000\u0000\u0580\u0581\u0001\u0000\u0000"+
		"\u0000\u0581\u0582\u0001\u0000\u0000\u0000\u0582\u0583\u0005<\u0000\u0000"+
		"\u0583\u00f7\u0001\u0000\u0000\u0000\u0584\u0589\u0003\u00fa}\u0000\u0585"+
		"\u0586\u0005B\u0000\u0000\u0586\u0588\u0003\u00fa}\u0000\u0587\u0585\u0001"+
		"\u0000\u0000\u0000\u0588\u058b\u0001\u0000\u0000\u0000\u0589\u0587\u0001"+
		"\u0000\u0000\u0000\u0589\u058a\u0001\u0000\u0000\u0000\u058a\u00f9\u0001"+
		"\u0000\u0000\u0000\u058b\u0589\u0001\u0000\u0000\u0000\u058c\u058d\u0005"+
		"h\u0000\u0000\u058d\u058e\u0005D\u0000\u0000\u058e\u058f\u0003\u00fc~"+
		"\u0000\u058f\u00fb\u0001\u0000\u0000\u0000\u0590\u0594\u0003\u01c6\u00e3"+
		"\u0000\u0591\u0594\u0003\u00fe\u007f\u0000\u0592\u0594\u0003\u00f0x\u0000"+
		"\u0593\u0590\u0001\u0000\u0000\u0000\u0593\u0591\u0001\u0000\u0000\u0000"+
		"\u0593\u0592\u0001\u0000\u0000\u0000\u0594\u00fd\u0001\u0000\u0000\u0000"+
		"\u0595\u0597\u0005=\u0000\u0000\u0596\u0598\u0003\u0100\u0080\u0000\u0597"+
		"\u0596\u0001\u0000\u0000\u0000\u0597\u0598\u0001\u0000\u0000\u0000\u0598"+
		"\u059a\u0001\u0000\u0000\u0000\u0599\u059b\u0005B\u0000\u0000\u059a\u0599"+
		"\u0001\u0000\u0000\u0000\u059a\u059b\u0001\u0000\u0000\u0000\u059b\u059c"+
		"\u0001\u0000\u0000\u0000\u059c\u059d\u0005>\u0000\u0000\u059d\u00ff\u0001"+
		"\u0000\u0000\u0000\u059e\u05a3\u0003\u00fc~\u0000\u059f\u05a0\u0005B\u0000"+
		"\u0000\u05a0\u05a2\u0003\u00fc~\u0000\u05a1\u059f\u0001\u0000\u0000\u0000"+
		"\u05a2\u05a5\u0001\u0000\u0000\u0000\u05a3\u05a1\u0001\u0000\u0000\u0000"+
		"\u05a3\u05a4\u0001\u0000\u0000\u0000\u05a4\u0101\u0001\u0000\u0000\u0000"+
		"\u05a5\u05a3\u0001\u0000\u0000\u0000\u05a6\u05a7\u0005i\u0000\u0000\u05a7"+
		"\u05a8\u00036\u001b\u0000\u05a8\u0103\u0001\u0000\u0000\u0000\u05a9\u05aa"+
		"\u0005i\u0000\u0000\u05aa\u05ab\u00036\u001b\u0000\u05ab\u05ac\u0005;"+
		"\u0000\u0000\u05ac\u05ad\u0003\u00fc~\u0000\u05ad\u05ae\u0005<\u0000\u0000"+
		"\u05ae\u0105\u0001\u0000\u0000\u0000\u05af\u05b1\u0005=\u0000\u0000\u05b0"+
		"\u05b2\u0003\u0108\u0084\u0000\u05b1\u05b0\u0001\u0000\u0000\u0000\u05b1"+
		"\u05b2\u0001\u0000\u0000\u0000\u05b2\u05b4\u0001\u0000\u0000\u0000\u05b3"+
		"\u05b5\u0005B\u0000\u0000\u05b4\u05b3\u0001\u0000\u0000\u0000\u05b4\u05b5"+
		"\u0001\u0000\u0000\u0000\u05b5\u05b6\u0001\u0000\u0000\u0000\u05b6\u05b7"+
		"\u0005>\u0000\u0000\u05b7\u0107\u0001\u0000\u0000\u0000\u05b8\u05bd\u0003"+
		"v;\u0000\u05b9\u05ba\u0005B\u0000\u0000\u05ba\u05bc\u0003v;\u0000\u05bb"+
		"\u05b9\u0001\u0000\u0000\u0000\u05bc\u05bf\u0001\u0000\u0000\u0000\u05bd"+
		"\u05bb\u0001\u0000\u0000\u0000\u05bd\u05be\u0001\u0000\u0000\u0000\u05be"+
		"\u0109\u0001\u0000\u0000\u0000\u05bf\u05bd\u0001\u0000\u0000\u0000\u05c0"+
		"\u05c2\u0005=\u0000\u0000\u05c1\u05c3\u0003\u010c\u0086\u0000\u05c2\u05c1"+
		"\u0001\u0000\u0000\u0000\u05c2\u05c3\u0001\u0000\u0000\u0000\u05c3\u05c4"+
		"\u0001\u0000\u0000\u0000\u05c4\u05c5\u0005>\u0000\u0000\u05c5\u010b\u0001"+
		"\u0000\u0000\u0000\u05c6\u05ca\u0003\u010e\u0087\u0000\u05c7\u05c9\u0003"+
		"\u010e\u0087\u0000\u05c8\u05c7\u0001\u0000\u0000\u0000\u05c9\u05cc\u0001"+
		"\u0000\u0000\u0000\u05ca\u05c8\u0001\u0000\u0000\u0000\u05ca\u05cb\u0001"+
		"\u0000\u0000\u0000\u05cb\u010d\u0001\u0000\u0000\u0000\u05cc\u05ca\u0001"+
		"\u0000\u0000\u0000\u05cd\u05d1\u0003\u0110\u0088\u0000\u05ce\u05d1\u0003"+
		"R)\u0000\u05cf\u05d1\u0003\u0114\u008a\u0000\u05d0\u05cd\u0001\u0000\u0000"+
		"\u0000\u05d0\u05ce\u0001\u0000\u0000\u0000\u05d0\u05cf\u0001\u0000\u0000"+
		"\u0000\u05d1\u010f\u0001\u0000\u0000\u0000\u05d2\u05d3\u0003\u0112\u0089"+
		"\u0000\u05d3\u05d4\u0005A\u0000\u0000\u05d4\u0111\u0001\u0000\u0000\u0000"+
		"\u05d5\u05d7\u0003\u00a2Q\u0000\u05d6\u05d5\u0001\u0000\u0000\u0000\u05d7"+
		"\u05da\u0001\u0000\u0000\u0000\u05d8\u05d6\u0001\u0000\u0000\u0000\u05d8"+
		"\u05d9\u0001\u0000\u0000\u0000\u05d9\u05db\u0001\u0000\u0000\u0000\u05da"+
		"\u05d8\u0001\u0000\u0000\u0000\u05db\u05dc\u0003x<\u0000\u05dc\u05dd\u0003"+
		"p8\u0000\u05dd\u0113\u0001\u0000\u0000\u0000\u05de\u05e5\u0003\u0118\u008c"+
		"\u0000\u05df\u05e5\u0003\u011c\u008e\u0000\u05e0\u05e5\u0003\u0124\u0092"+
		"\u0000\u05e1\u05e5\u0003\u0126\u0093\u0000\u05e2\u05e5\u0003\u0138\u009c"+
		"\u0000\u05e3\u05e5\u0003\u013e\u009f\u0000\u05e4\u05de\u0001\u0000\u0000"+
		"\u0000\u05e4\u05df\u0001\u0000\u0000\u0000\u05e4\u05e0\u0001\u0000\u0000"+
		"\u0000\u05e4\u05e1\u0001\u0000\u0000\u0000\u05e4\u05e2\u0001\u0000\u0000"+
		"\u0000\u05e4\u05e3\u0001\u0000\u0000\u0000\u05e5\u0115\u0001\u0000\u0000"+
		"\u0000\u05e6\u05ec\u0003\u0118\u008c\u0000\u05e7\u05ec\u0003\u011e\u008f"+
		"\u0000\u05e8\u05ec\u0003\u0128\u0094\u0000\u05e9\u05ec\u0003\u013a\u009d"+
		"\u0000\u05ea\u05ec\u0003\u0140\u00a0\u0000\u05eb\u05e6\u0001\u0000\u0000"+
		"\u0000\u05eb\u05e7\u0001\u0000\u0000\u0000\u05eb\u05e8\u0001\u0000\u0000"+
		"\u0000\u05eb\u05e9\u0001\u0000\u0000\u0000\u05eb\u05ea\u0001\u0000\u0000"+
		"\u0000\u05ec\u0117\u0001\u0000\u0000\u0000\u05ed\u05fa\u0003\u010a\u0085"+
		"\u0000\u05ee\u05fa\u0003\u011a\u008d\u0000\u05ef\u05fa\u0003\u0120\u0090"+
		"\u0000\u05f0\u05fa\u0003\u012a\u0095\u0000\u05f1\u05fa\u0003\u012c\u0096"+
		"\u0000\u05f2\u05fa\u0003\u013c\u009e\u0000\u05f3\u05fa\u0003\u0150\u00a8"+
		"\u0000\u05f4\u05fa\u0003\u0152\u00a9\u0000\u05f5\u05fa\u0003\u0154\u00aa"+
		"\u0000\u05f6\u05fa\u0003\u0158\u00ac\u0000\u05f7\u05fa\u0003\u0156\u00ab"+
		"\u0000\u05f8\u05fa\u0003\u015a\u00ad\u0000\u05f9\u05ed\u0001\u0000\u0000"+
		"\u0000\u05f9\u05ee\u0001\u0000\u0000\u0000\u05f9\u05ef\u0001\u0000\u0000"+
		"\u0000\u05f9\u05f0\u0001\u0000\u0000\u0000\u05f9\u05f1\u0001\u0000\u0000"+
		"\u0000\u05f9\u05f2\u0001\u0000\u0000\u0000\u05f9\u05f3\u0001\u0000\u0000"+
		"\u0000\u05f9\u05f4\u0001\u0000\u0000\u0000\u05f9\u05f5\u0001\u0000\u0000"+
		"\u0000\u05f9\u05f6\u0001\u0000\u0000\u0000\u05f9\u05f7\u0001\u0000\u0000"+
		"\u0000\u05f9\u05f8\u0001\u0000\u0000\u0000\u05fa\u0119\u0001\u0000\u0000"+
		"\u0000\u05fb\u05fc\u0005A\u0000\u0000\u05fc\u011b\u0001\u0000\u0000\u0000"+
		"\u05fd\u05fe\u0005h\u0000\u0000\u05fe\u05ff\u0005J\u0000\u0000\u05ff\u0600"+
		"\u0003\u0114\u008a\u0000\u0600\u011d\u0001\u0000\u0000\u0000\u0601\u0602"+
		"\u0005h\u0000\u0000\u0602\u0603\u0005J\u0000\u0000\u0603\u0604\u0003\u0116"+
		"\u008b\u0000\u0604\u011f\u0001\u0000\u0000\u0000\u0605\u0606\u0003\u0122"+
		"\u0091\u0000\u0606\u0607\u0005A\u0000\u0000\u0607\u0121\u0001\u0000\u0000"+
		"\u0000\u0608\u0610\u0003\u01b8\u00dc\u0000\u0609\u0610\u0003\u01e0\u00f0"+
		"\u0000\u060a\u0610\u0003\u01e2\u00f1\u0000\u060b\u0610\u0003\u01e8\u00f4"+
		"\u0000\u060c\u0610\u0003\u01ec\u00f6\u0000\u060d\u0610\u0003\u0196\u00cb"+
		"\u0000\u060e\u0610\u0003\u0182\u00c1\u0000\u060f\u0608\u0001\u0000\u0000"+
		"\u0000\u060f\u0609\u0001\u0000\u0000\u0000\u060f\u060a\u0001\u0000\u0000"+
		"\u0000\u060f\u060b\u0001\u0000\u0000\u0000\u060f\u060c\u0001\u0000\u0000"+
		"\u0000\u060f\u060d\u0001\u0000\u0000\u0000\u060f\u060e\u0001\u0000\u0000"+
		"\u0000\u0610\u0123\u0001\u0000\u0000\u0000\u0611\u0612\u0005\u0018\u0000"+
		"\u0000\u0612\u0613\u0005;\u0000\u0000\u0613\u0614\u0003\u01ac\u00d6\u0000"+
		"\u0614\u0615\u0005<\u0000\u0000\u0615\u0616\u0003\u0114\u008a\u0000\u0616"+
		"\u0125\u0001\u0000\u0000\u0000\u0617\u0618\u0005\u0018\u0000\u0000\u0618"+
		"\u0619\u0005;\u0000\u0000\u0619\u061a\u0003\u01ac\u00d6\u0000\u061a\u061b"+
		"\u0005<\u0000\u0000\u061b\u061c\u0003\u0116\u008b\u0000\u061c\u061d\u0005"+
		"\u0011\u0000\u0000\u061d\u061e\u0003\u0114\u008a\u0000\u061e\u0127\u0001"+
		"\u0000\u0000\u0000\u061f\u0620\u0005\u0018\u0000\u0000\u0620\u0621\u0005"+
		";\u0000\u0000\u0621\u0622\u0003\u01ac\u00d6\u0000\u0622\u0623\u0005<\u0000"+
		"\u0000\u0623\u0624\u0003\u0116\u008b\u0000\u0624\u0625\u0005\u0011\u0000"+
		"\u0000\u0625\u0626\u0003\u0116\u008b\u0000\u0626\u0129\u0001\u0000\u0000"+
		"\u0000\u0627\u0628\u0005\u0004\u0000\u0000\u0628\u0629\u0003\u01ac\u00d6"+
		"\u0000\u0629\u062a\u0005A\u0000\u0000\u062a\u0632\u0001\u0000\u0000\u0000"+
		"\u062b\u062c\u0005\u0004\u0000\u0000\u062c\u062d\u0003\u01ac\u00d6\u0000"+
		"\u062d\u062e\u0005J\u0000\u0000\u062e\u062f\u0003\u01ac\u00d6\u0000\u062f"+
		"\u0630\u0005A\u0000\u0000\u0630\u0632\u0001\u0000\u0000\u0000\u0631\u0627"+
		"\u0001\u0000\u0000\u0000\u0631\u062b\u0001\u0000\u0000\u0000\u0632\u012b"+
		"\u0001\u0000\u0000\u0000\u0633\u0634\u0005+\u0000\u0000\u0634\u0635\u0005"+
		";\u0000\u0000\u0635\u0636\u0003\u01ac\u00d6\u0000\u0636\u0637\u0005<\u0000"+
		"\u0000\u0637\u0638\u0003\u012e\u0097\u0000\u0638\u012d\u0001\u0000\u0000"+
		"\u0000\u0639\u063d\u0005=\u0000\u0000\u063a\u063c\u0003\u0130\u0098\u0000"+
		"\u063b\u063a\u0001\u0000\u0000\u0000\u063c\u063f\u0001\u0000\u0000\u0000"+
		"\u063d\u063b\u0001\u0000\u0000\u0000\u063d\u063e\u0001\u0000\u0000\u0000"+
		"\u063e\u0643\u0001\u0000\u0000\u0000\u063f\u063d\u0001\u0000\u0000\u0000"+
		"\u0640\u0642\u0003\u0134\u009a\u0000\u0641\u0640\u0001\u0000\u0000\u0000"+
		"\u0642\u0645\u0001\u0000\u0000\u0000\u0643\u0641\u0001\u0000\u0000\u0000"+
		"\u0643\u0644\u0001\u0000\u0000\u0000\u0644\u0646\u0001\u0000\u0000\u0000"+
		"\u0645\u0643\u0001\u0000\u0000\u0000\u0646\u0647\u0005>\u0000\u0000\u0647"+
		"\u012f\u0001\u0000\u0000\u0000\u0648\u0649\u0003\u0132\u0099\u0000\u0649"+
		"\u064a\u0003\u010c\u0086\u0000\u064a\u0131\u0001\u0000\u0000\u0000\u064b"+
		"\u064f\u0003\u0134\u009a\u0000\u064c\u064e\u0003\u0134\u009a\u0000\u064d"+
		"\u064c\u0001\u0000\u0000\u0000\u064e\u0651\u0001\u0000\u0000\u0000\u064f"+
		"\u064d\u0001\u0000\u0000\u0000\u064f\u0650\u0001\u0000\u0000\u0000\u0650"+
		"\u0133\u0001\u0000\u0000\u0000\u0651\u064f\u0001\u0000\u0000\u0000\u0652"+
		"\u0653\u0005\b\u0000\u0000\u0653\u0654\u0003\u01aa\u00d5\u0000\u0654\u0655"+
		"\u0005J\u0000\u0000\u0655\u065d\u0001\u0000\u0000\u0000\u0656\u0657\u0005"+
		"\b\u0000\u0000\u0657\u0658\u0003\u0136\u009b\u0000\u0658\u0659\u0005J"+
		"\u0000\u0000\u0659\u065d\u0001\u0000\u0000\u0000\u065a\u065b\u0005\u000e"+
		"\u0000\u0000\u065b\u065d\u0005J\u0000\u0000\u065c\u0652\u0001\u0000\u0000"+
		"\u0000\u065c\u0656\u0001\u0000\u0000\u0000\u065c\u065a\u0001\u0000\u0000"+
		"\u0000\u065d\u0135\u0001\u0000\u0000\u0000\u065e\u065f\u0005h\u0000\u0000"+
		"\u065f\u0137\u0001\u0000\u0000\u0000\u0660\u0661\u00054\u0000\u0000\u0661"+
		"\u0662\u0005;\u0000\u0000\u0662\u0663\u0003\u01ac\u00d6\u0000\u0663\u0664"+
		"\u0005<\u0000\u0000\u0664\u0665\u0003\u0114\u008a\u0000\u0665\u0139\u0001"+
		"\u0000\u0000\u0000\u0666\u0667\u00054\u0000\u0000\u0667\u0668\u0005;\u0000"+
		"\u0000\u0668\u0669\u0003\u01ac\u00d6\u0000\u0669\u066a\u0005<\u0000\u0000"+
		"\u066a\u066b\u0003\u0116\u008b\u0000\u066b\u013b\u0001\u0000\u0000\u0000"+
		"\u066c\u066d\u0005\u000f\u0000\u0000\u066d\u066e\u0003\u0114\u008a\u0000"+
		"\u066e\u066f\u00054\u0000\u0000\u066f\u0670\u0005;\u0000\u0000\u0670\u0671"+
		"\u0003\u01ac\u00d6\u0000\u0671\u0672\u0005<\u0000\u0000\u0672\u0673\u0005"+
		"A\u0000\u0000\u0673\u013d\u0001\u0000\u0000\u0000\u0674\u0677\u0003\u0142"+
		"\u00a1\u0000\u0675\u0677\u0003\u014c\u00a6\u0000\u0676\u0674\u0001\u0000"+
		"\u0000\u0000\u0676\u0675\u0001\u0000\u0000\u0000\u0677\u013f\u0001\u0000"+
		"\u0000\u0000\u0678\u067b\u0003\u0144\u00a2\u0000\u0679\u067b\u0003\u014e"+
		"\u00a7\u0000\u067a\u0678\u0001\u0000\u0000\u0000\u067a\u0679\u0001\u0000"+
		"\u0000\u0000\u067b\u0141\u0001\u0000\u0000\u0000\u067c\u067d\u0005\u0017"+
		"\u0000\u0000\u067d\u067f\u0005;\u0000\u0000\u067e\u0680\u0003\u0146\u00a3"+
		"\u0000\u067f\u067e\u0001\u0000\u0000\u0000\u067f\u0680\u0001\u0000\u0000"+
		"\u0000\u0680\u0681\u0001\u0000\u0000\u0000\u0681\u0683\u0005A\u0000\u0000"+
		"\u0682\u0684\u0003\u01ac\u00d6\u0000\u0683\u0682\u0001\u0000\u0000\u0000"+
		"\u0683\u0684\u0001\u0000\u0000\u0000\u0684\u0685\u0001\u0000\u0000\u0000"+
		"\u0685\u0687\u0005A\u0000\u0000\u0686\u0688\u0003\u0148\u00a4\u0000\u0687"+
		"\u0686\u0001\u0000\u0000\u0000\u0687\u0688\u0001\u0000\u0000\u0000\u0688"+
		"\u0689\u0001\u0000\u0000\u0000\u0689\u068a\u0005<\u0000\u0000\u068a\u068b"+
		"\u0003\u0114\u008a\u0000\u068b\u0143\u0001\u0000\u0000\u0000\u068c\u068d"+
		"\u0005\u0017\u0000\u0000\u068d\u068f\u0005;\u0000\u0000\u068e\u0690\u0003"+
		"\u0146\u00a3\u0000\u068f\u068e\u0001\u0000\u0000\u0000\u068f\u0690\u0001"+
		"\u0000\u0000\u0000\u0690\u0691\u0001\u0000\u0000\u0000\u0691\u0693\u0005"+
		"A\u0000\u0000\u0692\u0694\u0003\u01ac\u00d6\u0000\u0693\u0692\u0001\u0000"+
		"\u0000\u0000\u0693\u0694\u0001\u0000\u0000\u0000\u0694\u0695\u0001\u0000"+
		"\u0000\u0000\u0695\u0697\u0005A\u0000\u0000\u0696\u0698\u0003\u0148\u00a4"+
		"\u0000\u0697\u0696\u0001\u0000\u0000\u0000\u0697\u0698\u0001\u0000\u0000"+
		"\u0000\u0698\u0699\u0001\u0000\u0000\u0000\u0699\u069a\u0005<\u0000\u0000"+
		"\u069a\u069b\u0003\u0116\u008b\u0000\u069b\u0145\u0001\u0000\u0000\u0000"+
		"\u069c\u069f\u0003\u014a\u00a5\u0000\u069d\u069f\u0003\u0112\u0089\u0000"+
		"\u069e\u069c\u0001\u0000\u0000\u0000\u069e\u069d\u0001\u0000\u0000\u0000"+
		"\u069f\u0147\u0001\u0000\u0000\u0000\u06a0\u06a1\u0003\u014a\u00a5\u0000"+
		"\u06a1\u0149\u0001\u0000\u0000\u0000\u06a2\u06a7\u0003\u0122\u0091\u0000"+
		"\u06a3\u06a4\u0005B\u0000\u0000\u06a4\u06a6\u0003\u0122\u0091\u0000\u06a5"+
		"\u06a3\u0001\u0000\u0000\u0000\u06a6\u06a9\u0001\u0000\u0000\u0000\u06a7"+
		"\u06a5\u0001\u0000\u0000\u0000\u06a7\u06a8\u0001\u0000\u0000\u0000\u06a8"+
		"\u014b\u0001\u0000\u0000\u0000\u06a9\u06a7\u0001\u0000\u0000\u0000\u06aa"+
		"\u06ab\u0005\u0017\u0000\u0000\u06ab\u06af\u0005;\u0000\u0000\u06ac\u06ae"+
		"\u0003\u00a2Q\u0000\u06ad\u06ac\u0001\u0000\u0000\u0000\u06ae\u06b1\u0001"+
		"\u0000\u0000\u0000\u06af\u06ad\u0001\u0000\u0000\u0000\u06af\u06b0\u0001"+
		"\u0000\u0000\u0000\u06b0\u06b2\u0001\u0000\u0000\u0000\u06b1\u06af\u0001"+
		"\u0000\u0000\u0000\u06b2\u06b3\u0003x<\u0000\u06b3\u06b4\u0003t:\u0000"+
		"\u06b4\u06b5\u0005J\u0000\u0000\u06b5\u06b6\u0003\u01ac\u00d6\u0000\u06b6"+
		"\u06b7\u0005<\u0000\u0000\u06b7\u06b8\u0003\u0114\u008a\u0000\u06b8\u014d"+
		"\u0001\u0000\u0000\u0000\u06b9\u06ba\u0005\u0017\u0000\u0000\u06ba\u06be"+
		"\u0005;\u0000\u0000\u06bb\u06bd\u0003\u00a2Q\u0000\u06bc\u06bb\u0001\u0000"+
		"\u0000\u0000\u06bd\u06c0\u0001\u0000\u0000\u0000\u06be\u06bc\u0001\u0000"+
		"\u0000\u0000\u06be\u06bf\u0001\u0000\u0000\u0000\u06bf\u06c1\u0001\u0000"+
		"\u0000\u0000\u06c0\u06be\u0001\u0000\u0000\u0000\u06c1\u06c2\u0003x<\u0000"+
		"\u06c2\u06c3\u0003t:\u0000\u06c3\u06c4\u0005J\u0000\u0000\u06c4\u06c5"+
		"\u0003\u01ac\u00d6\u0000\u06c5\u06c6\u0005<\u0000\u0000\u06c6\u06c7\u0003"+
		"\u0116\u008b\u0000\u06c7\u014f\u0001\u0000\u0000\u0000\u06c8\u06ca\u0005"+
		"\u0006\u0000\u0000\u06c9\u06cb\u0005h\u0000\u0000\u06ca\u06c9\u0001\u0000"+
		"\u0000\u0000\u06ca\u06cb\u0001\u0000\u0000\u0000\u06cb\u06cc\u0001\u0000"+
		"\u0000\u0000\u06cc\u06cd\u0005A\u0000\u0000\u06cd\u0151\u0001\u0000\u0000"+
		"\u0000\u06ce\u06d0\u0005\r\u0000\u0000\u06cf\u06d1\u0005h\u0000\u0000"+
		"\u06d0\u06cf\u0001\u0000\u0000\u0000\u06d0\u06d1\u0001\u0000\u0000\u0000"+
		"\u06d1\u06d2\u0001\u0000\u0000\u0000\u06d2\u06d3\u0005A\u0000\u0000\u06d3"+
		"\u0153\u0001\u0000\u0000\u0000\u06d4\u06d6\u0005&\u0000\u0000\u06d5\u06d7"+
		"\u0003\u01ac\u00d6\u0000\u06d6\u06d5\u0001\u0000\u0000\u0000\u06d6\u06d7"+
		"\u0001\u0000\u0000\u0000\u06d7\u06d8\u0001\u0000\u0000\u0000\u06d8\u06d9"+
		"\u0005A\u0000\u0000\u06d9\u0155\u0001\u0000\u0000\u0000\u06da\u06db\u0005"+
		".\u0000\u0000\u06db\u06dc\u0003\u01ac\u00d6\u0000\u06dc\u06dd\u0005A\u0000"+
		"\u0000\u06dd\u0157\u0001\u0000\u0000\u0000\u06de\u06df\u0005,\u0000\u0000"+
		"\u06df\u06e0\u0005;\u0000\u0000\u06e0\u06e1\u0003\u01ac\u00d6\u0000\u06e1"+
		"\u06e2\u0005<\u0000\u0000\u06e2\u06e3\u0003\u010a\u0085\u0000\u06e3\u0159"+
		"\u0001\u0000\u0000\u0000\u06e4\u06e5\u00051\u0000\u0000\u06e5\u06e6\u0003"+
		"\u010a\u0085\u0000\u06e6\u06e7\u0003\u015c\u00ae\u0000\u06e7\u06f1\u0001"+
		"\u0000\u0000\u0000\u06e8\u06e9\u00051\u0000\u0000\u06e9\u06eb\u0003\u010a"+
		"\u0085\u0000\u06ea\u06ec\u0003\u015c\u00ae\u0000\u06eb\u06ea\u0001\u0000"+
		"\u0000\u0000\u06eb\u06ec\u0001\u0000\u0000\u0000\u06ec\u06ed\u0001\u0000"+
		"\u0000\u0000\u06ed\u06ee\u0003\u0164\u00b2\u0000\u06ee\u06f1\u0001\u0000"+
		"\u0000\u0000\u06ef\u06f1\u0003\u0166\u00b3\u0000\u06f0\u06e4\u0001\u0000"+
		"\u0000\u0000\u06f0\u06e8\u0001\u0000\u0000\u0000\u06f0\u06ef\u0001\u0000"+
		"\u0000\u0000\u06f1\u015b\u0001\u0000\u0000\u0000\u06f2\u06f6\u0003\u015e"+
		"\u00af\u0000\u06f3\u06f5\u0003\u015e\u00af\u0000\u06f4\u06f3\u0001\u0000"+
		"\u0000\u0000\u06f5\u06f8\u0001\u0000\u0000\u0000\u06f6\u06f4\u0001\u0000"+
		"\u0000\u0000\u06f6\u06f7\u0001\u0000\u0000\u0000\u06f7\u015d\u0001\u0000"+
		"\u0000\u0000\u06f8\u06f6\u0001\u0000\u0000\u0000\u06f9\u06fa\u0005\t\u0000"+
		"\u0000\u06fa\u06fb\u0005;\u0000\u0000\u06fb\u06fc\u0003\u0160\u00b0\u0000"+
		"\u06fc\u06fd\u0005<\u0000\u0000\u06fd\u06fe\u0003\u010a\u0085\u0000\u06fe"+
		"\u015f\u0001\u0000\u0000\u0000\u06ff\u0701\u0003\u00a2Q\u0000\u0700\u06ff"+
		"\u0001\u0000\u0000\u0000\u0701\u0704\u0001\u0000\u0000\u0000\u0702\u0700"+
		"\u0001\u0000\u0000\u0000\u0702\u0703\u0001\u0000\u0000\u0000\u0703\u0705"+
		"\u0001\u0000\u0000\u0000\u0704\u0702\u0001\u0000\u0000\u0000\u0705\u0706"+
		"\u0003\u0162\u00b1\u0000\u0706\u0707\u0003t:\u0000\u0707\u0161\u0001\u0000"+
		"\u0000\u0000\u0708\u070d\u0003\u0080@\u0000\u0709\u070a\u0005X\u0000\u0000"+
		"\u070a\u070c\u0003\u0010\b\u0000\u070b\u0709\u0001\u0000\u0000\u0000\u070c"+
		"\u070f\u0001\u0000\u0000\u0000\u070d\u070b\u0001\u0000\u0000\u0000\u070d"+
		"\u070e\u0001\u0000\u0000\u0000\u070e\u0163\u0001\u0000\u0000\u0000\u070f"+
		"\u070d\u0001\u0000\u0000\u0000\u0710\u0711\u0005\u0015\u0000\u0000\u0711"+
		"\u0712\u0003\u010a\u0085\u0000\u0712\u0165\u0001\u0000\u0000\u0000\u0713"+
		"\u0714\u00051\u0000\u0000\u0714\u0715\u0003\u0168\u00b4\u0000\u0715\u0717"+
		"\u0003\u010a\u0085\u0000\u0716\u0718\u0003\u015c\u00ae\u0000\u0717\u0716"+
		"\u0001\u0000\u0000\u0000\u0717\u0718\u0001\u0000\u0000\u0000\u0718\u071a"+
		"\u0001\u0000\u0000\u0000\u0719\u071b\u0003\u0164\u00b2\u0000\u071a\u0719"+
		"\u0001\u0000\u0000\u0000\u071a\u071b\u0001\u0000\u0000\u0000\u071b\u0167"+
		"\u0001\u0000\u0000\u0000\u071c\u071d\u0005;\u0000\u0000\u071d\u071f\u0003"+
		"\u016a\u00b5\u0000\u071e\u0720\u0005A\u0000\u0000\u071f\u071e\u0001\u0000"+
		"\u0000\u0000\u071f\u0720\u0001\u0000\u0000\u0000\u0720\u0721\u0001\u0000"+
		"\u0000\u0000\u0721\u0722\u0005<\u0000\u0000\u0722\u0169\u0001\u0000\u0000"+
		"\u0000\u0723\u0728\u0003\u016c\u00b6\u0000\u0724\u0725\u0005A\u0000\u0000"+
		"\u0725\u0727\u0003\u016c\u00b6\u0000\u0726\u0724\u0001\u0000\u0000\u0000"+
		"\u0727\u072a\u0001\u0000\u0000\u0000\u0728\u0726\u0001\u0000\u0000\u0000"+
		"\u0728\u0729\u0001\u0000\u0000\u0000\u0729\u016b\u0001\u0000\u0000\u0000"+
		"\u072a\u0728\u0001\u0000\u0000\u0000\u072b\u072d\u0003\u00a2Q\u0000\u072c"+
		"\u072b\u0001\u0000\u0000\u0000\u072d\u0730\u0001\u0000\u0000\u0000\u072e"+
		"\u072c\u0001\u0000\u0000\u0000\u072e\u072f\u0001\u0000\u0000\u0000\u072f"+
		"\u0731\u0001\u0000\u0000\u0000\u0730\u072e\u0001\u0000\u0000\u0000\u0731"+
		"\u0732\u0003x<\u0000\u0732\u0733\u0003t:\u0000\u0733\u0734\u0005D\u0000"+
		"\u0000\u0734\u0735\u0003\u01ac\u00d6\u0000\u0735\u016d\u0001\u0000\u0000"+
		"\u0000\u0736\u0739\u0003\u017c\u00be\u0000\u0737\u0739\u0003\u01a4\u00d2"+
		"\u0000\u0738\u0736\u0001\u0000\u0000\u0000\u0738\u0737\u0001\u0000\u0000"+
		"\u0000\u0739\u073d\u0001\u0000\u0000\u0000\u073a\u073c\u0003\u0176\u00bb"+
		"\u0000\u073b\u073a\u0001\u0000\u0000\u0000\u073c\u073f\u0001\u0000\u0000"+
		"\u0000\u073d\u073b\u0001\u0000\u0000\u0000\u073d\u073e\u0001\u0000\u0000"+
		"\u0000\u073e\u016f\u0001\u0000\u0000\u0000\u073f\u073d\u0001\u0000\u0000"+
		"\u0000\u0740\u075d\u0003\u0000\u0000\u0000\u0741\u0745\u00036\u001b\u0000"+
		"\u0742\u0744\u0003\u01c4\u00e2\u0000\u0743\u0742\u0001\u0000\u0000\u0000"+
		"\u0744\u0747\u0001\u0000\u0000\u0000\u0745\u0743\u0001\u0000\u0000\u0000"+
		"\u0745\u0746\u0001\u0000\u0000\u0000\u0746\u0748\u0001\u0000\u0000\u0000"+
		"\u0747\u0745\u0001\u0000\u0000\u0000\u0748\u0749\u0005C\u0000\u0000\u0749"+
		"\u074a\u0005\u000b\u0000\u0000\u074a\u075d\u0001\u0000\u0000\u0000\u074b"+
		"\u074c\u00052\u0000\u0000\u074c\u074d\u0005C\u0000\u0000\u074d\u075d\u0005"+
		"\u000b\u0000\u0000\u074e\u075d\u0005-\u0000\u0000\u074f\u0750\u00036\u001b"+
		"\u0000\u0750\u0751\u0005C\u0000\u0000\u0751\u0752\u0005-\u0000\u0000\u0752"+
		"\u075d\u0001\u0000\u0000\u0000\u0753\u0754\u0005;\u0000\u0000\u0754\u0755"+
		"\u0003\u01ac\u00d6\u0000\u0755\u0756\u0005<\u0000\u0000\u0756\u075d\u0001"+
		"\u0000\u0000\u0000\u0757\u075d\u0003\u0182\u00c1\u0000\u0758\u075d\u0003"+
		"\u018a\u00c5\u0000\u0759\u075d\u0003\u0190\u00c8\u0000\u075a\u075d\u0003"+
		"\u0196\u00cb\u0000\u075b\u075d\u0003\u019e\u00cf\u0000\u075c\u0740\u0001"+
		"\u0000\u0000\u0000\u075c\u0741\u0001\u0000\u0000\u0000\u075c\u074b\u0001"+
		"\u0000\u0000\u0000\u075c\u074e\u0001\u0000\u0000\u0000\u075c\u074f\u0001"+
		"\u0000\u0000\u0000\u075c\u0753\u0001\u0000\u0000\u0000\u075c\u0757\u0001"+
		"\u0000\u0000\u0000\u075c\u0758\u0001\u0000\u0000\u0000\u075c\u0759\u0001"+
		"\u0000\u0000\u0000\u075c\u075a\u0001\u0000\u0000\u0000\u075c\u075b\u0001"+
		"\u0000\u0000\u0000\u075d\u0171\u0001\u0000\u0000\u0000\u075e\u075f\u0001"+
		"\u0000\u0000\u0000\u075f\u0173\u0001\u0000\u0000\u0000\u0760\u077c\u0003"+
		"\u0000\u0000\u0000\u0761\u0765\u00036\u001b\u0000\u0762\u0764\u0003\u01c4"+
		"\u00e2\u0000\u0763\u0762\u0001\u0000\u0000\u0000\u0764\u0767\u0001\u0000"+
		"\u0000\u0000\u0765\u0763\u0001\u0000\u0000\u0000\u0765\u0766\u0001\u0000"+
		"\u0000\u0000\u0766\u0768\u0001\u0000\u0000\u0000\u0767\u0765\u0001\u0000"+
		"\u0000\u0000\u0768\u0769\u0005C\u0000\u0000\u0769\u076a\u0005\u000b\u0000"+
		"\u0000\u076a\u077c\u0001\u0000\u0000\u0000\u076b\u076c\u00052\u0000\u0000"+
		"\u076c\u076d\u0005C\u0000\u0000\u076d\u077c\u0005\u000b\u0000\u0000\u076e"+
		"\u077c\u0005-\u0000\u0000\u076f\u0770\u00036\u001b\u0000\u0770\u0771\u0005"+
		"C\u0000\u0000\u0771\u0772\u0005-\u0000\u0000\u0772\u077c\u0001\u0000\u0000"+
		"\u0000\u0773\u0774\u0005;\u0000\u0000\u0774\u0775\u0003\u01ac\u00d6\u0000"+
		"\u0775\u0776\u0005<\u0000\u0000\u0776\u077c\u0001\u0000\u0000\u0000\u0777"+
		"\u077c\u0003\u0182\u00c1\u0000\u0778\u077c\u0003\u018a\u00c5\u0000\u0779"+
		"\u077c\u0003\u0196\u00cb\u0000\u077a\u077c\u0003\u019e\u00cf\u0000\u077b"+
		"\u0760\u0001\u0000\u0000\u0000\u077b\u0761\u0001\u0000\u0000\u0000\u077b"+
		"\u076b\u0001\u0000\u0000\u0000\u077b\u076e\u0001\u0000\u0000\u0000\u077b"+
		"\u076f\u0001\u0000\u0000\u0000\u077b\u0773\u0001\u0000\u0000\u0000\u077b"+
		"\u0777\u0001\u0000\u0000\u0000\u077b\u0778\u0001\u0000\u0000\u0000\u077b"+
		"\u0779\u0001\u0000\u0000\u0000\u077b\u077a\u0001\u0000\u0000\u0000\u077c"+
		"\u0175\u0001\u0000\u0000\u0000\u077d\u0783\u0003\u0184\u00c2\u0000\u077e"+
		"\u0783\u0003\u018c\u00c6\u0000\u077f\u0783\u0003\u0192\u00c9\u0000\u0780"+
		"\u0783\u0003\u0198\u00cc\u0000\u0781\u0783\u0003\u01a0\u00d0\u0000\u0782"+
		"\u077d\u0001\u0000\u0000\u0000\u0782\u077e\u0001\u0000\u0000\u0000\u0782"+
		"\u077f\u0001\u0000\u0000\u0000\u0782\u0780\u0001\u0000\u0000\u0000\u0782"+
		"\u0781\u0001\u0000\u0000\u0000\u0783\u0177\u0001\u0000\u0000\u0000\u0784"+
		"\u0785\u0001\u0000\u0000\u0000\u0785\u0179\u0001\u0000\u0000\u0000\u0786"+
		"\u078b\u0003\u0184\u00c2\u0000\u0787\u078b\u0003\u018c\u00c6\u0000\u0788"+
		"\u078b\u0003\u0198\u00cc\u0000\u0789\u078b\u0003\u01a0\u00d0\u0000\u078a"+
		"\u0786\u0001\u0000\u0000\u0000\u078a\u0787\u0001\u0000\u0000\u0000\u078a"+
		"\u0788\u0001\u0000\u0000\u0000\u078a\u0789\u0001\u0000\u0000\u0000\u078b"+
		"\u017b\u0001\u0000\u0000\u0000\u078c\u07b3\u0003\u0000\u0000\u0000\u078d"+
		"\u0791\u00036\u001b\u0000\u078e\u0790\u0003\u01c4\u00e2\u0000\u078f\u078e"+
		"\u0001\u0000\u0000\u0000\u0790\u0793\u0001\u0000\u0000\u0000\u0791\u078f"+
		"\u0001\u0000\u0000\u0000\u0791\u0792\u0001\u0000\u0000\u0000\u0792\u0794"+
		"\u0001\u0000\u0000\u0000\u0793\u0791\u0001\u0000\u0000\u0000\u0794\u0795"+
		"\u0005C\u0000\u0000\u0795\u0796\u0005\u000b\u0000\u0000\u0796\u07b3\u0001"+
		"\u0000\u0000\u0000\u0797\u079b\u0003z=\u0000\u0798\u079a\u0003\u01c4\u00e2"+
		"\u0000\u0799\u0798\u0001\u0000\u0000\u0000\u079a\u079d\u0001\u0000\u0000"+
		"\u0000\u079b\u0799\u0001\u0000\u0000\u0000\u079b\u079c\u0001\u0000\u0000"+
		"\u0000\u079c\u079e\u0001\u0000\u0000\u0000\u079d\u079b\u0001\u0000\u0000"+
		"\u0000\u079e\u079f\u0005C\u0000\u0000\u079f\u07a0\u0005\u000b\u0000\u0000"+
		"\u07a0\u07b3\u0001\u0000\u0000\u0000\u07a1\u07a2\u00052\u0000\u0000\u07a2"+
		"\u07a3\u0005C\u0000\u0000\u07a3\u07b3\u0005\u000b\u0000\u0000\u07a4\u07b3"+
		"\u0005-\u0000\u0000\u07a5\u07a6\u00036\u001b\u0000\u07a6\u07a7\u0005C"+
		"\u0000\u0000\u07a7\u07a8\u0005-\u0000\u0000\u07a8\u07b3\u0001\u0000\u0000"+
		"\u0000\u07a9\u07aa\u0005;\u0000\u0000\u07aa\u07ab\u0003\u01ac\u00d6\u0000"+
		"\u07ab\u07ac\u0005<\u0000\u0000\u07ac\u07b3\u0001\u0000\u0000\u0000\u07ad"+
		"\u07b3\u0003\u0186\u00c3\u0000\u07ae\u07b3\u0003\u018e\u00c7\u0000\u07af"+
		"\u07b3\u0003\u0194\u00ca\u0000\u07b0\u07b3\u0003\u019a\u00cd\u0000\u07b1"+
		"\u07b3\u0003\u01a2\u00d1\u0000\u07b2\u078c\u0001\u0000\u0000\u0000\u07b2"+
		"\u078d\u0001\u0000\u0000\u0000\u07b2\u0797\u0001\u0000\u0000\u0000\u07b2"+
		"\u07a1\u0001\u0000\u0000\u0000\u07b2\u07a4\u0001\u0000\u0000\u0000\u07b2"+
		"\u07a5\u0001\u0000\u0000\u0000\u07b2\u07a9\u0001\u0000\u0000\u0000\u07b2"+
		"\u07ad\u0001\u0000\u0000\u0000\u07b2\u07ae\u0001\u0000\u0000\u0000\u07b2"+
		"\u07af\u0001\u0000\u0000\u0000\u07b2\u07b0\u0001\u0000\u0000\u0000\u07b2"+
		"\u07b1\u0001\u0000\u0000\u0000\u07b3\u017d\u0001\u0000\u0000\u0000\u07b4"+
		"\u07b5\u0001\u0000\u0000\u0000\u07b5\u017f\u0001\u0000\u0000\u0000\u07b6"+
		"\u07dc\u0003\u0000\u0000\u0000\u07b7\u07bb\u00036\u001b\u0000\u07b8\u07ba"+
		"\u0003\u01c4\u00e2\u0000\u07b9\u07b8\u0001\u0000\u0000\u0000\u07ba\u07bd"+
		"\u0001\u0000\u0000\u0000\u07bb\u07b9\u0001\u0000\u0000\u0000\u07bb\u07bc"+
		"\u0001\u0000\u0000\u0000\u07bc\u07be\u0001\u0000\u0000\u0000\u07bd\u07bb"+
		"\u0001\u0000\u0000\u0000\u07be\u07bf\u0005C\u0000\u0000\u07bf\u07c0\u0005"+
		"\u000b\u0000\u0000\u07c0\u07dc\u0001\u0000\u0000\u0000\u07c1\u07c5\u0003"+
		"z=\u0000\u07c2\u07c4\u0003\u01c4\u00e2\u0000\u07c3\u07c2\u0001\u0000\u0000"+
		"\u0000\u07c4\u07c7\u0001\u0000\u0000\u0000\u07c5\u07c3\u0001\u0000\u0000"+
		"\u0000\u07c5\u07c6\u0001\u0000\u0000\u0000\u07c6\u07c8\u0001\u0000\u0000"+
		"\u0000\u07c7\u07c5\u0001\u0000\u0000\u0000\u07c8\u07c9\u0005C\u0000\u0000"+
		"\u07c9\u07ca\u0005\u000b\u0000\u0000\u07ca\u07dc\u0001\u0000\u0000\u0000"+
		"\u07cb\u07cc\u00052\u0000\u0000\u07cc\u07cd\u0005C\u0000\u0000\u07cd\u07dc"+
		"\u0005\u000b\u0000\u0000\u07ce\u07dc\u0005-\u0000\u0000\u07cf\u07d0\u0003"+
		"6\u001b\u0000\u07d0\u07d1\u0005C\u0000\u0000\u07d1\u07d2\u0005-\u0000"+
		"\u0000\u07d2\u07dc\u0001\u0000\u0000\u0000\u07d3\u07d4\u0005;\u0000\u0000"+
		"\u07d4\u07d5\u0003\u01ac\u00d6\u0000\u07d5\u07d6\u0005<\u0000\u0000\u07d6"+
		"\u07dc\u0001\u0000\u0000\u0000\u07d7\u07dc\u0003\u0186\u00c3\u0000\u07d8"+
		"\u07dc\u0003\u018e\u00c7\u0000\u07d9\u07dc\u0003\u019a\u00cd\u0000\u07da"+
		"\u07dc\u0003\u01a2\u00d1\u0000\u07db\u07b6\u0001\u0000\u0000\u0000\u07db"+
		"\u07b7\u0001\u0000\u0000\u0000\u07db\u07c1\u0001\u0000\u0000\u0000\u07db"+
		"\u07cb\u0001\u0000\u0000\u0000\u07db\u07ce\u0001\u0000\u0000\u0000\u07db"+
		"\u07cf\u0001\u0000\u0000\u0000\u07db\u07d3\u0001\u0000\u0000\u0000\u07db"+
		"\u07d7\u0001\u0000\u0000\u0000\u07db\u07d8\u0001\u0000\u0000\u0000\u07db"+
		"\u07d9\u0001\u0000\u0000\u0000\u07db\u07da\u0001\u0000\u0000\u0000\u07dc"+
		"\u0181\u0001\u0000\u0000\u0000\u07dd\u07df\u0005!\u0000\u0000\u07de\u07e0"+
		"\u0003*\u0015\u0000\u07df\u07de\u0001\u0000\u0000\u0000\u07df\u07e0\u0001"+
		"\u0000\u0000\u0000\u07e0\u07e1\u0001\u0000\u0000\u0000\u07e1\u07e6\u0003"+
		"\u00f2y\u0000\u07e2\u07e3\u0005C\u0000\u0000\u07e3\u07e5\u0003\u00f2y"+
		"\u0000\u07e4\u07e2\u0001\u0000\u0000\u0000\u07e5\u07e8\u0001\u0000\u0000"+
		"\u0000\u07e6\u07e4\u0001\u0000\u0000\u0000\u07e6\u07e7\u0001\u0000\u0000"+
		"\u0000\u07e7\u07ea\u0001\u0000\u0000\u0000\u07e8\u07e6\u0001\u0000\u0000"+
		"\u0000\u07e9\u07eb\u0003\u0188\u00c4\u0000\u07ea\u07e9\u0001\u0000\u0000"+
		"\u0000\u07ea\u07eb\u0001\u0000\u0000\u0000\u07eb\u07ec\u0001\u0000\u0000"+
		"\u0000\u07ec\u07ee\u0005;\u0000\u0000\u07ed\u07ef\u0003\u019c\u00ce\u0000"+
		"\u07ee\u07ed\u0001\u0000\u0000\u0000\u07ee\u07ef\u0001\u0000\u0000\u0000"+
		"\u07ef\u07f0\u0001\u0000\u0000\u0000\u07f0\u07f2\u0005<\u0000\u0000\u07f1"+
		"\u07f3\u0003d2\u0000\u07f2\u07f1\u0001\u0000\u0000\u0000\u07f2\u07f3\u0001"+
		"\u0000\u0000\u0000\u07f3\u0819\u0001\u0000\u0000\u0000\u07f4\u07f5\u0003"+
		":\u001d\u0000\u07f5\u07f6\u0005C\u0000\u0000\u07f6\u07f8\u0005!\u0000"+
		"\u0000\u07f7\u07f9\u0003*\u0015\u0000\u07f8\u07f7\u0001\u0000\u0000\u0000"+
		"\u07f8\u07f9\u0001\u0000\u0000\u0000\u07f9\u07fa\u0001\u0000\u0000\u0000"+
		"\u07fa\u07fc\u0003\u00f2y\u0000\u07fb\u07fd\u0003\u0188\u00c4\u0000\u07fc"+
		"\u07fb\u0001\u0000\u0000\u0000\u07fc\u07fd\u0001\u0000\u0000\u0000\u07fd"+
		"\u07fe\u0001\u0000\u0000\u0000\u07fe\u0800\u0005;\u0000\u0000\u07ff\u0801"+
		"\u0003\u019c\u00ce\u0000\u0800\u07ff\u0001\u0000\u0000\u0000\u0800\u0801"+
		"\u0001\u0000\u0000\u0000\u0801\u0802\u0001\u0000\u0000\u0000\u0802\u0804"+
		"\u0005<\u0000\u0000\u0803\u0805\u0003d2\u0000\u0804\u0803\u0001\u0000"+
		"\u0000\u0000\u0804\u0805\u0001\u0000\u0000\u0000\u0805\u0819\u0001\u0000"+
		"\u0000\u0000\u0806\u0807\u0003\u016e\u00b7\u0000\u0807\u0808\u0005C\u0000"+
		"\u0000\u0808\u080a\u0005!\u0000\u0000\u0809\u080b\u0003*\u0015\u0000\u080a"+
		"\u0809\u0001\u0000\u0000\u0000\u080a\u080b\u0001\u0000\u0000\u0000\u080b"+
		"\u080c\u0001\u0000\u0000\u0000\u080c\u080e\u0003\u00f2y\u0000\u080d\u080f"+
		"\u0003\u0188\u00c4\u0000\u080e\u080d\u0001\u0000\u0000\u0000\u080e\u080f"+
		"\u0001\u0000\u0000\u0000\u080f\u0810\u0001\u0000\u0000\u0000\u0810\u0812"+
		"\u0005;\u0000\u0000\u0811\u0813\u0003\u019c\u00ce\u0000\u0812\u0811\u0001"+
		"\u0000\u0000\u0000\u0812\u0813\u0001\u0000\u0000\u0000\u0813\u0814\u0001"+
		"\u0000\u0000\u0000\u0814\u0816\u0005<\u0000\u0000\u0815\u0817\u0003d2"+
		"\u0000\u0816\u0815\u0001\u0000\u0000\u0000\u0816\u0817\u0001\u0000\u0000"+
		"\u0000\u0817\u0819\u0001\u0000\u0000\u0000\u0818\u07dd\u0001\u0000\u0000"+
		"\u0000\u0818\u07f4\u0001\u0000\u0000\u0000\u0818\u0806\u0001\u0000\u0000"+
		"\u0000\u0819\u0183\u0001\u0000\u0000\u0000\u081a\u081b\u0005C\u0000\u0000"+
		"\u081b\u081d\u0005!\u0000\u0000\u081c\u081e\u0003*\u0015\u0000\u081d\u081c"+
		"\u0001\u0000\u0000\u0000\u081d\u081e\u0001\u0000\u0000\u0000\u081e\u081f"+
		"\u0001\u0000\u0000\u0000\u081f\u0821\u0003\u00f2y\u0000\u0820\u0822\u0003"+
		"\u0188\u00c4\u0000\u0821\u0820\u0001\u0000\u0000\u0000\u0821\u0822\u0001"+
		"\u0000\u0000\u0000\u0822\u0823\u0001\u0000\u0000\u0000\u0823\u0825\u0005"+
		";\u0000\u0000\u0824\u0826\u0003\u019c\u00ce\u0000\u0825\u0824\u0001\u0000"+
		"\u0000\u0000\u0825\u0826\u0001\u0000\u0000\u0000\u0826\u0827\u0001\u0000"+
		"\u0000\u0000\u0827\u0829\u0005<\u0000\u0000\u0828\u082a\u0003d2\u0000"+
		"\u0829\u0828\u0001\u0000\u0000\u0000\u0829\u082a\u0001\u0000\u0000\u0000"+
		"\u082a\u0185\u0001\u0000\u0000\u0000\u082b\u082d\u0005!\u0000\u0000\u082c"+
		"\u082e\u0003*\u0015\u0000\u082d\u082c\u0001\u0000\u0000\u0000\u082d\u082e"+
		"\u0001\u0000\u0000\u0000\u082e\u082f\u0001\u0000\u0000\u0000\u082f\u0834"+
		"\u0003\u00f2y\u0000\u0830\u0831\u0005C\u0000\u0000\u0831\u0833\u0003\u00f2"+
		"y\u0000\u0832\u0830\u0001\u0000\u0000\u0000\u0833\u0836\u0001\u0000\u0000"+
		"\u0000\u0834\u0832\u0001\u0000\u0000\u0000\u0834\u0835\u0001\u0000\u0000"+
		"\u0000\u0835\u0838\u0001\u0000\u0000\u0000\u0836\u0834\u0001\u0000\u0000"+
		"\u0000\u0837\u0839\u0003\u0188\u00c4\u0000\u0838\u0837\u0001\u0000\u0000"+
		"\u0000\u0838\u0839\u0001\u0000\u0000\u0000\u0839\u083a\u0001\u0000\u0000"+
		"\u0000\u083a\u083c\u0005;\u0000\u0000\u083b\u083d\u0003\u019c\u00ce\u0000"+
		"\u083c\u083b\u0001\u0000\u0000\u0000\u083c\u083d\u0001\u0000\u0000\u0000"+
		"\u083d\u083e\u0001\u0000\u0000\u0000\u083e\u0840\u0005<\u0000\u0000\u083f"+
		"\u0841\u0003d2\u0000\u0840\u083f\u0001\u0000\u0000\u0000\u0840\u0841\u0001"+
		"\u0000\u0000\u0000\u0841\u0855\u0001\u0000\u0000\u0000\u0842\u0843\u0003"+
		":\u001d\u0000\u0843\u0844\u0005C\u0000\u0000\u0844\u0846\u0005!\u0000"+
		"\u0000\u0845\u0847\u0003*\u0015\u0000\u0846\u0845\u0001\u0000\u0000\u0000"+
		"\u0846\u0847\u0001\u0000\u0000\u0000\u0847\u0848\u0001\u0000\u0000\u0000"+
		"\u0848\u084a\u0003\u00f2y\u0000\u0849\u084b\u0003\u0188\u00c4\u0000\u084a"+
		"\u0849\u0001\u0000\u0000\u0000\u084a\u084b\u0001\u0000\u0000\u0000\u084b"+
		"\u084c\u0001\u0000\u0000\u0000\u084c\u084e\u0005;\u0000\u0000\u084d\u084f"+
		"\u0003\u019c\u00ce\u0000\u084e\u084d\u0001\u0000\u0000\u0000\u084e\u084f"+
		"\u0001\u0000\u0000\u0000\u084f\u0850\u0001\u0000\u0000\u0000\u0850\u0852"+
		"\u0005<\u0000\u0000\u0851\u0853\u0003d2\u0000\u0852\u0851\u0001\u0000"+
		"\u0000\u0000\u0852\u0853\u0001\u0000\u0000\u0000\u0853\u0855\u0001\u0000"+
		"\u0000\u0000\u0854\u082b\u0001\u0000\u0000\u0000\u0854\u0842\u0001\u0000"+
		"\u0000\u0000\u0855\u0187\u0001\u0000\u0000\u0000\u0856\u0859\u0003*\u0015"+
		"\u0000\u0857\u0859\u0005\u0001\u0000\u0000\u0858\u0856\u0001\u0000\u0000"+
		"\u0000\u0858\u0857\u0001\u0000\u0000\u0000\u0859\u0189\u0001\u0000\u0000"+
		"\u0000\u085a\u085b\u0003\u016e\u00b7\u0000\u085b\u085c\u0005C\u0000\u0000"+
		"\u085c\u085d\u0005h\u0000\u0000\u085d\u0868\u0001\u0000\u0000\u0000\u085e"+
		"\u085f\u0005*\u0000\u0000\u085f\u0860\u0005C\u0000\u0000\u0860\u0868\u0005"+
		"h\u0000\u0000\u0861\u0862\u00036\u001b\u0000\u0862\u0863\u0005C\u0000"+
		"\u0000\u0863\u0864\u0005*\u0000\u0000\u0864\u0865\u0005C\u0000\u0000\u0865"+
		"\u0866\u0005h\u0000\u0000\u0866\u0868\u0001\u0000\u0000\u0000\u0867\u085a"+
		"\u0001\u0000\u0000\u0000\u0867\u085e\u0001\u0000\u0000\u0000\u0867\u0861"+
		"\u0001\u0000\u0000\u0000\u0868\u018b\u0001\u0000\u0000\u0000\u0869\u086a"+
		"\u0005C\u0000\u0000\u086a\u086b\u0005h\u0000\u0000\u086b\u018d\u0001\u0000"+
		"\u0000\u0000\u086c\u086d\u0005*\u0000\u0000\u086d\u086e\u0005C\u0000\u0000"+
		"\u086e\u0876\u0005h\u0000\u0000\u086f\u0870\u00036\u001b\u0000\u0870\u0871"+
		"\u0005C\u0000\u0000\u0871\u0872\u0005*\u0000\u0000\u0872\u0873\u0005C"+
		"\u0000\u0000\u0873\u0874\u0005h\u0000\u0000\u0874\u0876\u0001\u0000\u0000"+
		"\u0000\u0875\u086c\u0001\u0000\u0000\u0000\u0875\u086f\u0001\u0000\u0000"+
		"\u0000\u0876\u018f\u0001\u0000\u0000\u0000\u0877\u0878\u0003:\u001d\u0000"+
		"\u0878\u0879\u0005?\u0000\u0000\u0879\u087a\u0003\u01ac\u00d6\u0000\u087a"+
		"\u087b\u0005@\u0000\u0000\u087b\u0882\u0001\u0000\u0000\u0000\u087c\u087d"+
		"\u0003\u0174\u00ba\u0000\u087d\u087e\u0005?\u0000\u0000\u087e\u087f\u0003"+
		"\u01ac\u00d6\u0000\u087f\u0880\u0005@\u0000\u0000\u0880\u0882\u0001\u0000"+
		"\u0000\u0000\u0881\u0877\u0001\u0000\u0000\u0000\u0881\u087c\u0001\u0000"+
		"\u0000\u0000\u0882\u088a\u0001\u0000\u0000\u0000\u0883\u0884\u0003\u0172"+
		"\u00b9\u0000\u0884\u0885\u0005?\u0000\u0000\u0885\u0886\u0003\u01ac\u00d6"+
		"\u0000\u0886\u0887\u0005@\u0000\u0000\u0887\u0889\u0001\u0000\u0000\u0000"+
		"\u0888\u0883\u0001\u0000\u0000\u0000\u0889\u088c\u0001\u0000\u0000\u0000"+
		"\u088a\u0888\u0001\u0000\u0000\u0000\u088a\u088b\u0001\u0000\u0000\u0000"+
		"\u088b\u0191\u0001\u0000\u0000\u0000\u088c\u088a\u0001\u0000\u0000\u0000"+
		"\u088d\u088e\u0003\u017a\u00bd\u0000\u088e\u088f\u0005?\u0000\u0000\u088f"+
		"\u0890\u0003\u01ac\u00d6\u0000\u0890\u0891\u0005@\u0000\u0000\u0891\u0899"+
		"\u0001\u0000\u0000\u0000\u0892\u0893\u0003\u0178\u00bc\u0000\u0893\u0894"+
		"\u0005?\u0000\u0000\u0894\u0895\u0003\u01ac\u00d6\u0000\u0895\u0896\u0005"+
		"@\u0000\u0000\u0896\u0898\u0001\u0000\u0000\u0000\u0897\u0892\u0001\u0000"+
		"\u0000\u0000\u0898\u089b\u0001\u0000\u0000\u0000\u0899\u0897\u0001\u0000"+
		"\u0000\u0000\u0899\u089a\u0001\u0000\u0000\u0000\u089a\u0193\u0001\u0000"+
		"\u0000\u0000\u089b\u0899\u0001\u0000\u0000\u0000\u089c\u089d\u0003:\u001d"+
		"\u0000\u089d\u089e\u0005?\u0000\u0000\u089e\u089f\u0003\u01ac\u00d6\u0000"+
		"\u089f\u08a0\u0005@\u0000\u0000\u08a0\u08a7\u0001\u0000\u0000\u0000\u08a1"+
		"\u08a2\u0003\u0180\u00c0\u0000\u08a2\u08a3\u0005?\u0000\u0000\u08a3\u08a4"+
		"\u0003\u01ac\u00d6\u0000\u08a4\u08a5\u0005@\u0000\u0000\u08a5\u08a7\u0001"+
		"\u0000\u0000\u0000\u08a6\u089c\u0001\u0000\u0000\u0000\u08a6\u08a1\u0001"+
		"\u0000\u0000\u0000\u08a7\u08af\u0001\u0000\u0000\u0000\u08a8\u08a9\u0003"+
		"\u017e\u00bf\u0000\u08a9\u08aa\u0005?\u0000\u0000\u08aa\u08ab\u0003\u01ac"+
		"\u00d6\u0000\u08ab\u08ac\u0005@\u0000\u0000\u08ac\u08ae\u0001\u0000\u0000"+
		"\u0000\u08ad\u08a8\u0001\u0000\u0000\u0000\u08ae\u08b1\u0001\u0000\u0000"+
		"\u0000\u08af\u08ad\u0001\u0000\u0000\u0000\u08af\u08b0\u0001\u0000\u0000"+
		"\u0000\u08b0\u0195\u0001\u0000\u0000\u0000\u08b1\u08af\u0001\u0000\u0000"+
		"\u0000\u08b2\u08b3\u0003<\u001e\u0000\u08b3\u08b5\u0005;\u0000\u0000\u08b4"+
		"\u08b6\u0003\u019c\u00ce\u0000\u08b5\u08b4\u0001\u0000\u0000\u0000\u08b5"+
		"\u08b6\u0001\u0000\u0000\u0000\u08b6\u08b7\u0001\u0000\u0000\u0000\u08b7"+
		"\u08b8\u0005<\u0000\u0000\u08b8\u08f7\u0001\u0000\u0000\u0000\u08b9\u08ba"+
		"\u00036\u001b\u0000\u08ba\u08bc\u0005C\u0000\u0000\u08bb\u08bd\u0003*"+
		"\u0015\u0000\u08bc\u08bb\u0001\u0000\u0000\u0000\u08bc\u08bd\u0001\u0000"+
		"\u0000\u0000\u08bd\u08be\u0001\u0000\u0000\u0000\u08be\u08bf\u0005h\u0000"+
		"\u0000\u08bf\u08c1\u0005;\u0000\u0000\u08c0\u08c2\u0003\u019c\u00ce\u0000"+
		"\u08c1\u08c0\u0001\u0000\u0000\u0000\u08c1\u08c2\u0001\u0000\u0000\u0000"+
		"\u08c2\u08c3\u0001\u0000\u0000\u0000\u08c3\u08c4\u0005<\u0000\u0000\u08c4"+
		"\u08f7\u0001\u0000\u0000\u0000\u08c5\u08c6\u0003:\u001d\u0000\u08c6\u08c8"+
		"\u0005C\u0000\u0000\u08c7\u08c9\u0003*\u0015\u0000\u08c8\u08c7\u0001\u0000"+
		"\u0000\u0000\u08c8\u08c9\u0001\u0000\u0000\u0000\u08c9\u08ca\u0001\u0000"+
		"\u0000\u0000\u08ca\u08cb\u0005h\u0000\u0000\u08cb\u08cd\u0005;\u0000\u0000"+
		"\u08cc\u08ce\u0003\u019c\u00ce\u0000\u08cd\u08cc\u0001\u0000\u0000\u0000"+
		"\u08cd\u08ce\u0001\u0000\u0000\u0000\u08ce\u08cf\u0001\u0000\u0000\u0000"+
		"\u08cf\u08d0\u0005<\u0000\u0000\u08d0\u08f7\u0001\u0000\u0000\u0000\u08d1"+
		"\u08d2\u0003\u016e\u00b7\u0000\u08d2\u08d4\u0005C\u0000\u0000\u08d3\u08d5"+
		"\u0003*\u0015\u0000\u08d4\u08d3\u0001\u0000\u0000\u0000\u08d4\u08d5\u0001"+
		"\u0000\u0000\u0000\u08d5\u08d6\u0001\u0000\u0000\u0000\u08d6\u08d7\u0005"+
		"h\u0000\u0000\u08d7\u08d9\u0005;\u0000\u0000\u08d8\u08da\u0003\u019c\u00ce"+
		"\u0000\u08d9\u08d8\u0001\u0000\u0000\u0000\u08d9\u08da\u0001\u0000\u0000"+
		"\u0000\u08da\u08db\u0001\u0000\u0000\u0000\u08db\u08dc\u0005<\u0000\u0000"+
		"\u08dc\u08f7\u0001\u0000\u0000\u0000\u08dd\u08de\u0005*\u0000\u0000\u08de"+
		"\u08e0\u0005C\u0000\u0000\u08df\u08e1\u0003*\u0015\u0000\u08e0\u08df\u0001"+
		"\u0000\u0000\u0000\u08e0\u08e1\u0001\u0000\u0000\u0000\u08e1\u08e2\u0001"+
		"\u0000\u0000\u0000\u08e2\u08e3\u0005h\u0000\u0000\u08e3\u08e5\u0005;\u0000"+
		"\u0000\u08e4\u08e6\u0003\u019c\u00ce\u0000\u08e5\u08e4\u0001\u0000\u0000"+
		"\u0000\u08e5\u08e6\u0001\u0000\u0000\u0000\u08e6\u08e7\u0001\u0000\u0000"+
		"\u0000\u08e7\u08f7\u0005<\u0000\u0000\u08e8\u08e9\u00036\u001b\u0000\u08e9"+
		"\u08ea\u0005C\u0000\u0000\u08ea\u08eb\u0005*\u0000\u0000\u08eb\u08ed\u0005"+
		"C\u0000\u0000\u08ec\u08ee\u0003*\u0015\u0000\u08ed\u08ec\u0001\u0000\u0000"+
		"\u0000\u08ed\u08ee\u0001\u0000\u0000\u0000\u08ee\u08ef\u0001\u0000\u0000"+
		"\u0000\u08ef\u08f0\u0005h\u0000\u0000\u08f0\u08f2\u0005;\u0000\u0000\u08f1"+
		"\u08f3\u0003\u019c\u00ce\u0000\u08f2\u08f1\u0001\u0000\u0000\u0000\u08f2"+
		"\u08f3\u0001\u0000\u0000\u0000\u08f3\u08f4\u0001\u0000\u0000\u0000\u08f4"+
		"\u08f5\u0005<\u0000\u0000\u08f5\u08f7\u0001\u0000\u0000\u0000\u08f6\u08b2"+
		"\u0001\u0000\u0000\u0000\u08f6\u08b9\u0001\u0000\u0000\u0000\u08f6\u08c5"+
		"\u0001\u0000\u0000\u0000\u08f6\u08d1\u0001\u0000\u0000\u0000\u08f6\u08dd"+
		"\u0001\u0000\u0000\u0000\u08f6\u08e8\u0001\u0000\u0000\u0000\u08f7\u0197"+
		"\u0001\u0000\u0000\u0000\u08f8\u08fa\u0005C\u0000\u0000\u08f9\u08fb\u0003"+
		"*\u0015\u0000\u08fa\u08f9\u0001\u0000\u0000\u0000\u08fa\u08fb\u0001\u0000"+
		"\u0000\u0000\u08fb\u08fc\u0001\u0000\u0000\u0000\u08fc\u08fd\u0005h\u0000"+
		"\u0000\u08fd\u08ff\u0005;\u0000\u0000\u08fe\u0900\u0003\u019c\u00ce\u0000"+
		"\u08ff\u08fe\u0001\u0000\u0000\u0000\u08ff\u0900\u0001\u0000\u0000\u0000"+
		"\u0900\u0901\u0001\u0000\u0000\u0000\u0901\u0902\u0005<\u0000\u0000\u0902"+
		"\u0199\u0001\u0000\u0000\u0000\u0903\u0904\u0003<\u001e\u0000\u0904\u0906"+
		"\u0005;\u0000\u0000\u0905\u0907\u0003\u019c\u00ce\u0000\u0906\u0905\u0001"+
		"\u0000\u0000\u0000\u0906\u0907\u0001\u0000\u0000\u0000\u0907\u0908\u0001"+
		"\u0000\u0000\u0000\u0908\u0909\u0005<\u0000\u0000\u0909\u093c\u0001\u0000"+
		"\u0000\u0000\u090a\u090b\u00036\u001b\u0000\u090b\u090d\u0005C\u0000\u0000"+
		"\u090c\u090e\u0003*\u0015\u0000\u090d\u090c\u0001\u0000\u0000\u0000\u090d"+
		"\u090e\u0001\u0000\u0000\u0000\u090e\u090f\u0001\u0000\u0000\u0000\u090f"+
		"\u0910\u0005h\u0000\u0000\u0910\u0912\u0005;\u0000\u0000\u0911\u0913\u0003"+
		"\u019c\u00ce\u0000\u0912\u0911\u0001\u0000\u0000\u0000\u0912\u0913\u0001"+
		"\u0000\u0000\u0000\u0913\u0914\u0001\u0000\u0000\u0000\u0914\u0915\u0005"+
		"<\u0000\u0000\u0915\u093c\u0001\u0000\u0000\u0000\u0916\u0917\u0003:\u001d"+
		"\u0000\u0917\u0919\u0005C\u0000\u0000\u0918\u091a\u0003*\u0015\u0000\u0919"+
		"\u0918\u0001\u0000\u0000\u0000\u0919\u091a\u0001\u0000\u0000\u0000\u091a"+
		"\u091b\u0001\u0000\u0000\u0000\u091b\u091c\u0005h\u0000\u0000\u091c\u091e"+
		"\u0005;\u0000\u0000\u091d\u091f\u0003\u019c\u00ce\u0000\u091e\u091d\u0001"+
		"\u0000\u0000\u0000\u091e\u091f\u0001\u0000\u0000\u0000\u091f\u0920\u0001"+
		"\u0000\u0000\u0000\u0920\u0921\u0005<\u0000\u0000\u0921\u093c\u0001\u0000"+
		"\u0000\u0000\u0922\u0923\u0005*\u0000\u0000\u0923\u0925\u0005C\u0000\u0000"+
		"\u0924\u0926\u0003*\u0015\u0000\u0925\u0924\u0001\u0000\u0000\u0000\u0925"+
		"\u0926\u0001\u0000\u0000\u0000\u0926\u0927\u0001\u0000\u0000\u0000\u0927"+
		"\u0928\u0005h\u0000\u0000\u0928\u092a\u0005;\u0000\u0000\u0929\u092b\u0003"+
		"\u019c\u00ce\u0000\u092a\u0929\u0001\u0000\u0000\u0000\u092a\u092b\u0001"+
		"\u0000\u0000\u0000\u092b\u092c\u0001\u0000\u0000\u0000\u092c\u093c\u0005"+
		"<\u0000\u0000\u092d\u092e\u00036\u001b\u0000\u092e\u092f\u0005C\u0000"+
		"\u0000\u092f\u0930\u0005*\u0000\u0000\u0930\u0932\u0005C\u0000\u0000\u0931"+
		"\u0933\u0003*\u0015\u0000\u0932\u0931\u0001\u0000\u0000\u0000\u0932\u0933"+
		"\u0001\u0000\u0000\u0000\u0933\u0934\u0001\u0000\u0000\u0000\u0934\u0935"+
		"\u0005h\u0000\u0000\u0935\u0937\u0005;\u0000\u0000\u0936\u0938\u0003\u019c"+
		"\u00ce\u0000\u0937\u0936\u0001\u0000\u0000\u0000\u0937\u0938\u0001\u0000"+
		"\u0000\u0000\u0938\u0939\u0001\u0000\u0000\u0000\u0939\u093a\u0005<\u0000"+
		"\u0000\u093a\u093c\u0001\u0000\u0000\u0000\u093b\u0903\u0001\u0000\u0000"+
		"\u0000\u093b\u090a\u0001\u0000\u0000\u0000\u093b\u0916\u0001\u0000\u0000"+
		"\u0000\u093b\u0922\u0001\u0000\u0000\u0000\u093b\u092d\u0001\u0000\u0000"+
		"\u0000\u093c\u019b\u0001\u0000\u0000\u0000\u093d\u0942\u0003\u01ac\u00d6"+
		"\u0000\u093e\u093f\u0005B\u0000\u0000\u093f\u0941\u0003\u01ac\u00d6\u0000"+
		"\u0940\u093e\u0001\u0000\u0000\u0000\u0941\u0944\u0001\u0000\u0000\u0000"+
		"\u0942\u0940\u0001\u0000\u0000\u0000\u0942\u0943\u0001\u0000\u0000\u0000"+
		"\u0943\u019d\u0001\u0000\u0000\u0000\u0944\u0942\u0001\u0000\u0000\u0000"+
		"\u0945\u0946\u0003:\u001d\u0000\u0946\u0948\u0005\\\u0000\u0000\u0947"+
		"\u0949\u0003*\u0015\u0000\u0948\u0947\u0001\u0000\u0000\u0000\u0948\u0949"+
		"\u0001\u0000\u0000\u0000\u0949\u094a\u0001\u0000\u0000\u0000\u094a\u094b"+
		"\u0005h\u0000\u0000\u094b\u0975\u0001\u0000\u0000\u0000\u094c\u094d\u0003"+
		"\f\u0006\u0000\u094d\u094f\u0005\\\u0000\u0000\u094e\u0950\u0003*\u0015"+
		"\u0000\u094f\u094e\u0001\u0000\u0000\u0000\u094f\u0950\u0001\u0000\u0000"+
		"\u0000\u0950\u0951\u0001\u0000\u0000\u0000\u0951\u0952\u0005h\u0000\u0000"+
		"\u0952\u0975\u0001\u0000\u0000\u0000\u0953\u0954\u0003\u016e\u00b7\u0000"+
		"\u0954\u0956\u0005\\\u0000\u0000\u0955\u0957\u0003*\u0015\u0000\u0956"+
		"\u0955\u0001\u0000\u0000\u0000\u0956\u0957\u0001\u0000\u0000\u0000\u0957"+
		"\u0958\u0001\u0000\u0000\u0000\u0958\u0959\u0005h\u0000\u0000\u0959\u0975"+
		"\u0001\u0000\u0000\u0000\u095a\u095b\u0005*\u0000\u0000\u095b\u095d\u0005"+
		"\\\u0000\u0000\u095c\u095e\u0003*\u0015\u0000\u095d\u095c\u0001\u0000"+
		"\u0000\u0000\u095d\u095e\u0001\u0000\u0000\u0000\u095e\u095f\u0001\u0000"+
		"\u0000\u0000\u095f\u0975\u0005h\u0000\u0000\u0960\u0961\u00036\u001b\u0000"+
		"\u0961\u0962\u0005C\u0000\u0000\u0962\u0963\u0005*\u0000\u0000\u0963\u0965"+
		"\u0005\\\u0000\u0000\u0964\u0966\u0003*\u0015\u0000\u0965\u0964\u0001"+
		"\u0000\u0000\u0000\u0965\u0966\u0001\u0000\u0000\u0000\u0966\u0967\u0001"+
		"\u0000\u0000\u0000\u0967\u0968\u0005h\u0000\u0000\u0968\u0975\u0001\u0000"+
		"\u0000\u0000\u0969\u096a\u0003\u0010\b\u0000\u096a\u096c\u0005\\\u0000"+
		"\u0000\u096b\u096d\u0003*\u0015\u0000\u096c\u096b\u0001\u0000\u0000\u0000"+
		"\u096c\u096d\u0001\u0000\u0000\u0000\u096d\u096e\u0001\u0000\u0000\u0000"+
		"\u096e\u096f\u0005!\u0000\u0000\u096f\u0975\u0001\u0000\u0000\u0000\u0970"+
		"\u0971\u0003\u001e\u000f\u0000\u0971\u0972\u0005\\\u0000\u0000\u0972\u0973"+
		"\u0005!\u0000\u0000\u0973\u0975\u0001\u0000\u0000\u0000\u0974\u0945\u0001"+
		"\u0000\u0000\u0000\u0974\u094c\u0001\u0000\u0000\u0000\u0974\u0953\u0001"+
		"\u0000\u0000\u0000\u0974\u095a\u0001\u0000\u0000\u0000\u0974\u0960\u0001"+
		"\u0000\u0000\u0000\u0974\u0969\u0001\u0000\u0000\u0000\u0974\u0970\u0001"+
		"\u0000\u0000\u0000\u0975\u019f\u0001\u0000\u0000\u0000\u0976\u0978\u0005"+
		"\\\u0000\u0000\u0977\u0979\u0003*\u0015\u0000\u0978\u0977\u0001\u0000"+
		"\u0000\u0000\u0978\u0979\u0001\u0000\u0000\u0000\u0979\u097a\u0001\u0000"+
		"\u0000\u0000\u097a\u097b\u0005h\u0000\u0000\u097b\u01a1\u0001\u0000\u0000"+
		"\u0000\u097c\u097d\u0003:\u001d\u0000";
	private static final String _serializedATNSegment1 =
		"\u097d\u097f\u0005\\\u0000\u0000\u097e\u0980\u0003*\u0015\u0000\u097f"+
		"\u097e\u0001\u0000\u0000\u0000\u097f\u0980\u0001\u0000\u0000\u0000\u0980"+
		"\u0981\u0001\u0000\u0000\u0000\u0981\u0982\u0005h\u0000\u0000\u0982\u09a5"+
		"\u0001\u0000\u0000\u0000\u0983\u0984\u0003\f\u0006\u0000\u0984\u0986\u0005"+
		"\\\u0000\u0000\u0985\u0987\u0003*\u0015\u0000\u0986\u0985\u0001\u0000"+
		"\u0000\u0000\u0986\u0987\u0001\u0000\u0000\u0000\u0987\u0988\u0001\u0000"+
		"\u0000\u0000\u0988\u0989\u0005h\u0000\u0000\u0989\u09a5\u0001\u0000\u0000"+
		"\u0000\u098a\u098b\u0005*\u0000\u0000\u098b\u098d\u0005\\\u0000\u0000"+
		"\u098c\u098e\u0003*\u0015\u0000\u098d\u098c\u0001\u0000\u0000\u0000\u098d"+
		"\u098e\u0001\u0000\u0000\u0000\u098e\u098f\u0001\u0000\u0000\u0000\u098f"+
		"\u09a5\u0005h\u0000\u0000\u0990\u0991\u00036\u001b\u0000\u0991\u0992\u0005"+
		"C\u0000\u0000\u0992\u0993\u0005*\u0000\u0000\u0993\u0995\u0005\\\u0000"+
		"\u0000\u0994\u0996\u0003*\u0015\u0000\u0995\u0994\u0001\u0000\u0000\u0000"+
		"\u0995\u0996\u0001\u0000\u0000\u0000\u0996\u0997\u0001\u0000\u0000\u0000"+
		"\u0997\u0998\u0005h\u0000\u0000\u0998\u09a5\u0001\u0000\u0000\u0000\u0999"+
		"\u099a\u0003\u0010\b\u0000\u099a\u099c\u0005\\\u0000\u0000\u099b\u099d"+
		"\u0003*\u0015\u0000\u099c\u099b\u0001\u0000\u0000\u0000\u099c\u099d\u0001"+
		"\u0000\u0000\u0000\u099d\u099e\u0001\u0000\u0000\u0000\u099e\u099f\u0005"+
		"!\u0000\u0000\u099f\u09a5\u0001\u0000\u0000\u0000\u09a0\u09a1\u0003\u001e"+
		"\u000f\u0000\u09a1\u09a2\u0005\\\u0000\u0000\u09a2\u09a3\u0005!\u0000"+
		"\u0000\u09a3\u09a5\u0001\u0000\u0000\u0000\u09a4\u097c\u0001\u0000\u0000"+
		"\u0000\u09a4\u0983\u0001\u0000\u0000\u0000\u09a4\u098a\u0001\u0000\u0000"+
		"\u0000\u09a4\u0990\u0001\u0000\u0000\u0000\u09a4\u0999\u0001\u0000\u0000"+
		"\u0000\u09a4\u09a0\u0001\u0000\u0000\u0000\u09a5\u01a3\u0001\u0000\u0000"+
		"\u0000\u09a6\u09a7\u0005!\u0000\u0000\u09a7\u09a8\u0003\u0004\u0002\u0000"+
		"\u09a8\u09aa\u0003\u01a6\u00d3\u0000\u09a9\u09ab\u0003 \u0010\u0000\u09aa"+
		"\u09a9\u0001\u0000\u0000\u0000\u09aa\u09ab\u0001\u0000\u0000\u0000\u09ab"+
		"\u09bd\u0001\u0000\u0000\u0000\u09ac\u09ad\u0005!\u0000\u0000\u09ad\u09ae"+
		"\u0003\u000e\u0007\u0000\u09ae\u09b0\u0003\u01a6\u00d3\u0000\u09af\u09b1"+
		"\u0003 \u0010\u0000\u09b0\u09af\u0001\u0000\u0000\u0000\u09b0\u09b1\u0001"+
		"\u0000\u0000\u0000\u09b1\u09bd\u0001\u0000\u0000\u0000\u09b2\u09b3\u0005"+
		"!\u0000\u0000\u09b3\u09b4\u0003\u0004\u0002\u0000\u09b4\u09b5\u0003 \u0010"+
		"\u0000\u09b5\u09b6\u0003\u0106\u0083\u0000\u09b6\u09bd\u0001\u0000\u0000"+
		"\u0000\u09b7\u09b8\u0005!\u0000\u0000\u09b8\u09b9\u0003\u000e\u0007\u0000"+
		"\u09b9\u09ba\u0003 \u0010\u0000\u09ba\u09bb\u0003\u0106\u0083\u0000\u09bb"+
		"\u09bd\u0001\u0000\u0000\u0000\u09bc\u09a6\u0001\u0000\u0000\u0000\u09bc"+
		"\u09ac\u0001\u0000\u0000\u0000\u09bc\u09b2\u0001\u0000\u0000\u0000\u09bc"+
		"\u09b7\u0001\u0000\u0000\u0000\u09bd\u01a5\u0001\u0000\u0000\u0000\u09be"+
		"\u09c2\u0003\u01a8\u00d4\u0000\u09bf\u09c1\u0003\u01a8\u00d4\u0000\u09c0"+
		"\u09bf\u0001\u0000\u0000\u0000\u09c1\u09c4\u0001\u0000\u0000\u0000\u09c2"+
		"\u09c0\u0001\u0000\u0000\u0000\u09c2\u09c3\u0001\u0000\u0000\u0000\u09c3"+
		"\u01a7\u0001\u0000\u0000\u0000\u09c4\u09c2\u0001\u0000\u0000\u0000\u09c5"+
		"\u09c7\u0003\u00f0x\u0000\u09c6\u09c5\u0001\u0000\u0000\u0000\u09c7\u09ca"+
		"\u0001\u0000\u0000\u0000\u09c8\u09c6\u0001\u0000\u0000\u0000\u09c8\u09c9"+
		"\u0001\u0000\u0000\u0000\u09c9\u09cb\u0001\u0000\u0000\u0000\u09ca\u09c8"+
		"\u0001\u0000\u0000\u0000\u09cb\u09cc\u0005?\u0000\u0000\u09cc\u09cd\u0003"+
		"\u01ac\u00d6\u0000\u09cd\u09ce\u0005@\u0000\u0000\u09ce\u01a9\u0001\u0000"+
		"\u0000\u0000\u09cf\u09d0\u0003\u01ac\u00d6\u0000\u09d0\u01ab\u0001\u0000"+
		"\u0000\u0000\u09d1\u09d4\u0003\u01ae\u00d7\u0000\u09d2\u09d4\u0003\u01b6"+
		"\u00db\u0000\u09d3\u09d1\u0001\u0000\u0000\u0000\u09d3\u09d2\u0001\u0000"+
		"\u0000\u0000\u09d4\u01ad\u0001\u0000\u0000\u0000\u09d5\u09d6\u0003\u01b0"+
		"\u00d8\u0000\u09d6\u09d7\u0005[\u0000\u0000\u09d7\u09d8\u0003\u01b4\u00da"+
		"\u0000\u09d8\u01af\u0001\u0000\u0000\u0000\u09d9\u09e4\u0005h\u0000\u0000"+
		"\u09da\u09dc\u0005;\u0000\u0000\u09db\u09dd\u0003\u009cN\u0000\u09dc\u09db"+
		"\u0001\u0000\u0000\u0000\u09dc\u09dd\u0001\u0000\u0000\u0000\u09dd\u09de"+
		"\u0001\u0000\u0000\u0000\u09de\u09e4\u0005<\u0000\u0000\u09df\u09e0\u0005"+
		";\u0000\u0000\u09e0\u09e1\u0003\u01b2\u00d9\u0000\u09e1\u09e2\u0005<\u0000"+
		"\u0000\u09e2\u09e4\u0001\u0000\u0000\u0000\u09e3\u09d9\u0001\u0000\u0000"+
		"\u0000\u09e3\u09da\u0001\u0000\u0000\u0000\u09e3\u09df\u0001\u0000\u0000"+
		"\u0000\u09e4\u01b1\u0001\u0000\u0000\u0000\u09e5\u09ea\u0005h\u0000\u0000"+
		"\u09e6\u09e7\u0005B\u0000\u0000\u09e7\u09e9\u0005h\u0000\u0000\u09e8\u09e6"+
		"\u0001\u0000\u0000\u0000\u09e9\u09ec\u0001\u0000\u0000\u0000\u09ea\u09e8"+
		"\u0001\u0000\u0000\u0000\u09ea\u09eb\u0001\u0000\u0000\u0000\u09eb\u01b3"+
		"\u0001\u0000\u0000\u0000\u09ec\u09ea\u0001\u0000\u0000\u0000\u09ed\u09f0"+
		"\u0003\u01ac\u00d6\u0000\u09ee\u09f0\u0003\u010a\u0085\u0000\u09ef\u09ed"+
		"\u0001\u0000\u0000\u0000\u09ef\u09ee\u0001\u0000\u0000\u0000\u09f0\u01b5"+
		"\u0001\u0000\u0000\u0000\u09f1\u09f4\u0003\u01c6\u00e3\u0000\u09f2\u09f4"+
		"\u0003\u01b8\u00dc\u0000\u09f3\u09f1\u0001\u0000\u0000\u0000\u09f3\u09f2"+
		"\u0001\u0000\u0000\u0000\u09f4\u01b7\u0001\u0000\u0000\u0000\u09f5\u09f6"+
		"\u0003\u01ba\u00dd\u0000\u09f6\u09f7\u0003\u01bc\u00de\u0000\u09f7\u09f8"+
		"\u0003\u01ac\u00d6\u0000\u09f8\u01b9\u0001\u0000\u0000\u0000\u09f9\u09fd"+
		"\u0003:\u001d\u0000\u09fa\u09fd\u0003\u018a\u00c5\u0000\u09fb\u09fd\u0003"+
		"\u0190\u00c8\u0000\u09fc\u09f9\u0001\u0000\u0000\u0000\u09fc\u09fa\u0001"+
		"\u0000\u0000\u0000\u09fc\u09fb\u0001\u0000\u0000\u0000\u09fd\u01bb\u0001"+
		"\u0000\u0000\u0000\u09fe\u09ff\u0007\u0003\u0000\u0000\u09ff\u01bd\u0001"+
		"\u0000\u0000\u0000\u0a00\u0a01\u0007\u0004\u0000\u0000\u0a01\u01bf\u0001"+
		"\u0000\u0000\u0000\u0a02\u0a03\u0007\u0005\u0000\u0000\u0a03\u01c1\u0001"+
		"\u0000\u0000\u0000\u0a04\u0a05\u0007\u0006\u0000\u0000\u0a05\u01c3\u0001"+
		"\u0000\u0000\u0000\u0a06\u0a07\u0005\u0002\u0000\u0000\u0a07\u01c5\u0001"+
		"\u0000\u0000\u0000\u0a08\u0a16\u0003\u01c8\u00e4\u0000\u0a09\u0a0a\u0003"+
		"\u01c8\u00e4\u0000\u0a0a\u0a0b\u0005I\u0000\u0000\u0a0b\u0a0c\u0003\u01ac"+
		"\u00d6\u0000\u0a0c\u0a0d\u0005J\u0000\u0000\u0a0d\u0a0e\u0003\u01c6\u00e3"+
		"\u0000\u0a0e\u0a16\u0001\u0000\u0000\u0000\u0a0f\u0a10\u0003\u01c8\u00e4"+
		"\u0000\u0a10\u0a11\u0005I\u0000\u0000\u0a11\u0a12\u0003\u01ac\u00d6\u0000"+
		"\u0a12\u0a13\u0005J\u0000\u0000\u0a13\u0a14\u0003\u01ae\u00d7\u0000\u0a14"+
		"\u0a16\u0001\u0000\u0000\u0000\u0a15\u0a08\u0001\u0000\u0000\u0000\u0a15"+
		"\u0a09\u0001\u0000\u0000\u0000\u0a15\u0a0f\u0001\u0000\u0000\u0000\u0a16"+
		"\u01c7\u0001\u0000\u0000\u0000\u0a17\u0a18\u0006\u00e4\uffff\uffff\u0000"+
		"\u0a18\u0a19\u0003\u01ca\u00e5\u0000\u0a19\u0a1f\u0001\u0000\u0000\u0000"+
		"\u0a1a\u0a1b\n\u0001\u0000\u0000\u0a1b\u0a1c\u0005P\u0000\u0000\u0a1c"+
		"\u0a1e\u0003\u01ca\u00e5\u0000\u0a1d\u0a1a\u0001\u0000\u0000\u0000\u0a1e"+
		"\u0a21\u0001\u0000\u0000\u0000\u0a1f\u0a1d\u0001\u0000\u0000\u0000\u0a1f"+
		"\u0a20\u0001\u0000\u0000\u0000\u0a20\u01c9\u0001\u0000\u0000\u0000\u0a21"+
		"\u0a1f\u0001\u0000\u0000\u0000\u0a22\u0a23\u0006\u00e5\uffff\uffff\u0000"+
		"\u0a23\u0a24\u0003\u01cc\u00e6\u0000\u0a24\u0a2a\u0001\u0000\u0000\u0000"+
		"\u0a25\u0a26\n\u0001\u0000\u0000\u0a26\u0a27\u0005O\u0000\u0000\u0a27"+
		"\u0a29\u0003\u01cc\u00e6\u0000\u0a28\u0a25\u0001\u0000\u0000\u0000\u0a29"+
		"\u0a2c\u0001\u0000\u0000\u0000\u0a2a\u0a28\u0001\u0000\u0000\u0000\u0a2a"+
		"\u0a2b\u0001\u0000\u0000\u0000\u0a2b\u01cb\u0001\u0000\u0000\u0000\u0a2c"+
		"\u0a2a\u0001\u0000\u0000\u0000\u0a2d\u0a2e\u0006\u00e6\uffff\uffff\u0000"+
		"\u0a2e\u0a2f\u0003\u01ce\u00e7\u0000\u0a2f\u0a35\u0001\u0000\u0000\u0000"+
		"\u0a30\u0a31\n\u0001\u0000\u0000\u0a31\u0a32\u0005X\u0000\u0000\u0a32"+
		"\u0a34\u0003\u01ce\u00e7\u0000\u0a33\u0a30\u0001\u0000\u0000\u0000\u0a34"+
		"\u0a37\u0001\u0000\u0000\u0000\u0a35\u0a33\u0001\u0000\u0000\u0000\u0a35"+
		"\u0a36\u0001\u0000\u0000\u0000\u0a36\u01cd\u0001\u0000\u0000\u0000\u0a37"+
		"\u0a35\u0001\u0000\u0000\u0000\u0a38\u0a39\u0006\u00e7\uffff\uffff\u0000"+
		"\u0a39\u0a3a\u0003\u01d0\u00e8\u0000\u0a3a\u0a40\u0001\u0000\u0000\u0000"+
		"\u0a3b\u0a3c\n\u0001\u0000\u0000\u0a3c\u0a3d\u0005Y\u0000\u0000\u0a3d"+
		"\u0a3f\u0003\u01d0\u00e8\u0000\u0a3e\u0a3b\u0001\u0000\u0000\u0000\u0a3f"+
		"\u0a42\u0001\u0000\u0000\u0000\u0a40\u0a3e\u0001\u0000\u0000\u0000\u0a40"+
		"\u0a41\u0001\u0000\u0000\u0000\u0a41\u01cf\u0001\u0000\u0000\u0000\u0a42"+
		"\u0a40\u0001\u0000\u0000\u0000\u0a43\u0a44\u0006\u00e8\uffff\uffff\u0000"+
		"\u0a44\u0a45\u0003\u01d2\u00e9\u0000\u0a45\u0a4b\u0001\u0000\u0000\u0000"+
		"\u0a46\u0a47\n\u0001\u0000\u0000\u0a47\u0a48\u0005W\u0000\u0000\u0a48"+
		"\u0a4a\u0003\u01d2\u00e9\u0000\u0a49\u0a46\u0001\u0000\u0000\u0000\u0a4a"+
		"\u0a4d\u0001\u0000\u0000\u0000\u0a4b\u0a49\u0001\u0000\u0000\u0000\u0a4b"+
		"\u0a4c\u0001\u0000\u0000\u0000\u0a4c\u01d1\u0001\u0000\u0000\u0000\u0a4d"+
		"\u0a4b\u0001\u0000\u0000\u0000\u0a4e\u0a4f\u0006\u00e9\uffff\uffff\u0000"+
		"\u0a4f\u0a50\u0003\u01d4\u00ea\u0000\u0a50\u0a59\u0001\u0000\u0000\u0000"+
		"\u0a51\u0a52\n\u0002\u0000\u0000\u0a52\u0a53\u0005K\u0000\u0000\u0a53"+
		"\u0a58\u0003\u01d4\u00ea\u0000\u0a54\u0a55\n\u0001\u0000\u0000\u0a55\u0a56"+
		"\u0005N\u0000\u0000\u0a56\u0a58\u0003\u01d4\u00ea\u0000\u0a57\u0a51\u0001"+
		"\u0000\u0000\u0000\u0a57\u0a54\u0001\u0000\u0000\u0000\u0a58\u0a5b\u0001"+
		"\u0000\u0000\u0000\u0a59\u0a57\u0001\u0000\u0000\u0000\u0a59\u0a5a\u0001"+
		"\u0000\u0000\u0000\u0a5a\u01d3\u0001\u0000\u0000\u0000\u0a5b\u0a59\u0001"+
		"\u0000\u0000\u0000\u0a5c\u0a5d\u0006\u00ea\uffff\uffff\u0000\u0a5d\u0a5e"+
		"\u0003\u01d6\u00eb\u0000\u0a5e\u0a75\u0001\u0000\u0000\u0000\u0a5f\u0a60"+
		"\n\u0005\u0000\u0000\u0a60\u0a61\u0003\u01c0\u00e0\u0000\u0a61\u0a62\u0003"+
		"\u01d6\u00eb\u0000\u0a62\u0a74\u0001\u0000\u0000\u0000\u0a63\u0a64\n\u0004"+
		"\u0000\u0000\u0a64\u0a65\u0003\u01c0\u00e0\u0000\u0a65\u0a66\u0003\u01d6"+
		"\u00eb\u0000\u0a66\u0a74\u0001\u0000\u0000\u0000\u0a67\u0a68\n\u0003\u0000"+
		"\u0000\u0a68\u0a69\u0003\u01c0\u00e0\u0000\u0a69\u0a6a\u0003\u01d6\u00eb"+
		"\u0000\u0a6a\u0a74\u0001\u0000\u0000\u0000\u0a6b\u0a6c\n\u0002\u0000\u0000"+
		"\u0a6c\u0a6d\u0003\u01c0\u00e0\u0000\u0a6d\u0a6e\u0003\u01d6\u00eb\u0000"+
		"\u0a6e\u0a74\u0001\u0000\u0000\u0000\u0a6f\u0a70\n\u0001\u0000\u0000\u0a70"+
		"\u0a71\u0003\u01c0\u00e0\u0000\u0a71\u0a72\u0003\f\u0006\u0000\u0a72\u0a74"+
		"\u0001\u0000\u0000\u0000\u0a73\u0a5f\u0001\u0000\u0000\u0000\u0a73\u0a63"+
		"\u0001\u0000\u0000\u0000\u0a73\u0a67\u0001\u0000\u0000\u0000\u0a73\u0a6b"+
		"\u0001\u0000\u0000\u0000\u0a73\u0a6f\u0001\u0000\u0000\u0000\u0a74\u0a77"+
		"\u0001\u0000\u0000\u0000\u0a75\u0a73\u0001\u0000\u0000\u0000\u0a75\u0a76"+
		"\u0001\u0000\u0000\u0000\u0a76\u01d5\u0001\u0000\u0000\u0000\u0a77\u0a75"+
		"\u0001\u0000\u0000\u0000\u0a78\u0a79\u0006\u00eb\uffff\uffff\u0000\u0a79"+
		"\u0a7a\u0003\u01da\u00ed\u0000\u0a7a\u0a89\u0001\u0000\u0000\u0000\u0a7b"+
		"\u0a7c\n\u0003\u0000\u0000\u0a7c\u0a7d\u0003\u01d8\u00ec\u0000\u0a7d\u0a7e"+
		"\u0003\u01da\u00ed\u0000\u0a7e\u0a88\u0001\u0000\u0000\u0000\u0a7f\u0a80"+
		"\n\u0002\u0000\u0000\u0a80\u0a81\u0003\u01d8\u00ec\u0000\u0a81\u0a82\u0003"+
		"\u01da\u00ed\u0000\u0a82\u0a88\u0001\u0000\u0000\u0000\u0a83\u0a84\n\u0001"+
		"\u0000\u0000\u0a84\u0a85\u0003\u01d8\u00ec\u0000\u0a85\u0a86\u0003\u01da"+
		"\u00ed\u0000\u0a86\u0a88\u0001\u0000\u0000\u0000\u0a87\u0a7b\u0001\u0000"+
		"\u0000\u0000\u0a87\u0a7f\u0001\u0000\u0000\u0000\u0a87\u0a83\u0001\u0000"+
		"\u0000\u0000\u0a88\u0a8b\u0001\u0000\u0000\u0000\u0a89\u0a87\u0001\u0000"+
		"\u0000\u0000\u0a89\u0a8a\u0001\u0000\u0000\u0000\u0a8a\u01d7\u0001\u0000"+
		"\u0000\u0000\u0a8b\u0a89\u0001\u0000\u0000\u0000\u0a8c\u0a8d\u0005F\u0000"+
		"\u0000\u0a8d\u0a94\u0005F\u0000\u0000\u0a8e\u0a8f\u0005E\u0000\u0000\u0a8f"+
		"\u0a94\u0005E\u0000\u0000\u0a90\u0a91\u0005E\u0000\u0000\u0a91\u0a92\u0005"+
		"E\u0000\u0000\u0a92\u0a94\u0005E\u0000\u0000\u0a93\u0a8c\u0001\u0000\u0000"+
		"\u0000\u0a93\u0a8e\u0001\u0000\u0000\u0000\u0a93\u0a90\u0001\u0000\u0000"+
		"\u0000\u0a94\u01d9\u0001\u0000\u0000\u0000\u0a95\u0a96\u0006\u00ed\uffff"+
		"\uffff\u0000\u0a96\u0a97\u0003\u01dc\u00ee\u0000\u0a97\u0aa2\u0001\u0000"+
		"\u0000\u0000\u0a98\u0a99\n\u0002\u0000\u0000\u0a99\u0a9a\u0003\u01be\u00df"+
		"\u0000\u0a9a\u0a9b\u0003\u01dc\u00ee\u0000\u0a9b\u0aa1\u0001\u0000\u0000"+
		"\u0000\u0a9c\u0a9d\n\u0001\u0000\u0000\u0a9d\u0a9e\u0003\u01be\u00df\u0000"+
		"\u0a9e\u0a9f\u0003\u01dc\u00ee\u0000\u0a9f\u0aa1\u0001\u0000\u0000\u0000"+
		"\u0aa0\u0a98\u0001\u0000\u0000\u0000\u0aa0\u0a9c\u0001\u0000\u0000\u0000"+
		"\u0aa1\u0aa4\u0001\u0000\u0000\u0000\u0aa2\u0aa0\u0001\u0000\u0000\u0000"+
		"\u0aa2\u0aa3\u0001\u0000\u0000\u0000\u0aa3\u01db\u0001\u0000\u0000\u0000"+
		"\u0aa4\u0aa2\u0001\u0000\u0000\u0000\u0aa5\u0aa6\u0006\u00ee\uffff\uffff"+
		"\u0000\u0aa6\u0aa7\u0003\u01de\u00ef\u0000\u0aa7\u0ab6\u0001\u0000\u0000"+
		"\u0000\u0aa8\u0aa9\n\u0003\u0000\u0000\u0aa9\u0aaa\u0003\u01c2\u00e1\u0000"+
		"\u0aaa\u0aab\u0003\u01de\u00ef\u0000\u0aab\u0ab5\u0001\u0000\u0000\u0000"+
		"\u0aac\u0aad\n\u0002\u0000\u0000\u0aad\u0aae\u0003\u01c2\u00e1\u0000\u0aae"+
		"\u0aaf\u0003\u01de\u00ef\u0000\u0aaf\u0ab5\u0001\u0000\u0000\u0000\u0ab0"+
		"\u0ab1\n\u0001\u0000\u0000\u0ab1\u0ab2\u0003\u01c2\u00e1\u0000\u0ab2\u0ab3"+
		"\u0003\u01de\u00ef\u0000\u0ab3\u0ab5\u0001\u0000\u0000\u0000\u0ab4\u0aa8"+
		"\u0001\u0000\u0000\u0000\u0ab4\u0aac\u0001\u0000\u0000\u0000\u0ab4\u0ab0"+
		"\u0001\u0000\u0000\u0000\u0ab5\u0ab8\u0001\u0000\u0000\u0000\u0ab6\u0ab4"+
		"\u0001\u0000\u0000\u0000\u0ab6\u0ab7\u0001\u0000\u0000\u0000\u0ab7\u01dd"+
		"\u0001\u0000\u0000\u0000\u0ab8\u0ab6\u0001\u0000\u0000\u0000\u0ab9\u0ac3"+
		"\u0003\u01e0\u00f0\u0000\u0aba\u0ac3\u0003\u01e2\u00f1\u0000\u0abb\u0abc"+
		"\u0003\u01be\u00df\u0000\u0abc\u0abd\u0003\u01de\u00ef\u0000\u0abd\u0ac3"+
		"\u0001\u0000\u0000\u0000\u0abe\u0abf\u0003\u01be\u00df\u0000\u0abf\u0ac0"+
		"\u0003\u01de\u00ef\u0000\u0ac0\u0ac3\u0001\u0000\u0000\u0000\u0ac1\u0ac3"+
		"\u0003\u01e4\u00f2\u0000\u0ac2\u0ab9\u0001\u0000\u0000\u0000\u0ac2\u0aba"+
		"\u0001\u0000\u0000\u0000\u0ac2\u0abb\u0001\u0000\u0000\u0000\u0ac2\u0abe"+
		"\u0001\u0000\u0000\u0000\u0ac2\u0ac1\u0001\u0000\u0000\u0000\u0ac3\u01df"+
		"\u0001\u0000\u0000\u0000\u0ac4\u0ac5\u0005Q\u0000\u0000\u0ac5\u0ac6\u0003"+
		"\u01de\u00ef\u0000\u0ac6\u01e1\u0001\u0000\u0000\u0000\u0ac7\u0ac8\u0005"+
		"R\u0000\u0000\u0ac8\u0ac9\u0003\u01de\u00ef\u0000\u0ac9\u01e3\u0001\u0000"+
		"\u0000\u0000\u0aca\u0ad1\u0003\u01e6\u00f3\u0000\u0acb\u0acc\u0005H\u0000"+
		"\u0000\u0acc\u0ad1\u0003\u01de\u00ef\u0000\u0acd\u0ace\u0005G\u0000\u0000"+
		"\u0ace\u0ad1\u0003\u01de\u00ef\u0000\u0acf\u0ad1\u0003\u01f0\u00f8\u0000"+
		"\u0ad0\u0aca\u0001\u0000\u0000\u0000\u0ad0\u0acb\u0001\u0000\u0000\u0000"+
		"\u0ad0\u0acd\u0001\u0000\u0000\u0000\u0ad0\u0acf\u0001\u0000\u0000\u0000"+
		"\u0ad1\u01e5\u0001\u0000\u0000\u0000\u0ad2\u0ad5\u0003\u016e\u00b7\u0000"+
		"\u0ad3\u0ad5\u0003:\u001d\u0000\u0ad4\u0ad2\u0001\u0000\u0000\u0000\u0ad4"+
		"\u0ad3\u0001\u0000\u0000\u0000\u0ad5\u0ada\u0001\u0000\u0000\u0000\u0ad6"+
		"\u0ad9\u0003\u01ea\u00f5\u0000\u0ad7\u0ad9\u0003\u01ee\u00f7\u0000\u0ad8"+
		"\u0ad6\u0001\u0000\u0000\u0000\u0ad8\u0ad7\u0001\u0000\u0000\u0000\u0ad9"+
		"\u0adc\u0001\u0000\u0000\u0000\u0ada\u0ad8\u0001\u0000\u0000\u0000\u0ada"+
		"\u0adb\u0001\u0000\u0000\u0000\u0adb\u01e7\u0001\u0000\u0000\u0000\u0adc"+
		"\u0ada\u0001\u0000\u0000\u0000\u0add\u0ade\u0003\u01e6\u00f3\u0000\u0ade"+
		"\u0adf\u0005Q\u0000\u0000\u0adf\u01e9\u0001\u0000\u0000\u0000\u0ae0\u0ae1"+
		"\u0005Q\u0000\u0000\u0ae1\u01eb\u0001\u0000\u0000\u0000\u0ae2\u0ae3\u0003"+
		"\u01e6\u00f3\u0000\u0ae3\u0ae4\u0005R\u0000\u0000\u0ae4\u01ed\u0001\u0000"+
		"\u0000\u0000\u0ae5\u0ae6\u0005R\u0000\u0000\u0ae6\u01ef\u0001\u0000\u0000"+
		"\u0000\u0ae7\u0ae8\u0005;\u0000\u0000\u0ae8\u0ae9\u0003\u0004\u0002\u0000"+
		"\u0ae9\u0aea\u0005<\u0000\u0000\u0aea\u0aeb\u0003\u01de\u00ef\u0000\u0aeb"+
		"\u0b03\u0001\u0000\u0000\u0000\u0aec\u0aed\u0005;\u0000\u0000\u0aed\u0af1"+
		"\u0003\f\u0006\u0000\u0aee\u0af0\u0003(\u0014\u0000\u0aef\u0aee\u0001"+
		"\u0000\u0000\u0000\u0af0\u0af3\u0001\u0000\u0000\u0000\u0af1\u0aef\u0001"+
		"\u0000\u0000\u0000\u0af1\u0af2\u0001\u0000\u0000\u0000\u0af2\u0af4\u0001"+
		"\u0000\u0000\u0000\u0af3\u0af1\u0001\u0000\u0000\u0000\u0af4\u0af5\u0005"+
		"<\u0000\u0000\u0af5\u0af6\u0003\u01e4\u00f2\u0000\u0af6\u0b03\u0001\u0000"+
		"\u0000\u0000\u0af7\u0af8\u0005;\u0000\u0000\u0af8\u0afc\u0003\f\u0006"+
		"\u0000\u0af9\u0afb\u0003(\u0014\u0000\u0afa\u0af9\u0001\u0000\u0000\u0000"+
		"\u0afb\u0afe\u0001\u0000\u0000\u0000\u0afc\u0afa\u0001\u0000\u0000\u0000"+
		"\u0afc\u0afd\u0001\u0000\u0000\u0000\u0afd\u0aff\u0001\u0000\u0000\u0000"+
		"\u0afe\u0afc\u0001\u0000\u0000\u0000\u0aff\u0b00\u0005<\u0000\u0000\u0b00"+
		"\u0b01\u0003\u01ae\u00d7\u0000\u0b01\u0b03\u0001\u0000\u0000\u0000\u0b02"+
		"\u0ae7\u0001\u0000\u0000\u0000\u0b02\u0aec\u0001\u0000\u0000\u0000\u0b02"+
		"\u0af7\u0001\u0000\u0000\u0000\u0b03\u01f1\u0001\u0000\u0000\u0000\u0135"+
		"\u01f6\u01fb\u0202\u0206\u020a\u0213\u0217\u021b\u021d\u0222\u0228\u022a"+
		"\u022f\u0233\u0246\u024c\u0252\u0257\u0262\u0265\u0273\u0278\u027d\u0282"+
		"\u0288\u0292\u029a\u02a4\u02ac\u02b8\u02bc\u02c1\u02c7\u02cf\u02d8\u02e3"+
		"\u0300\u0304\u030a\u030d\u0310\u0317\u0322\u032d\u033b\u0342\u034b\u0352"+
		"\u035c\u0367\u036e\u0374\u0378\u037c\u0380\u0384\u0389\u038d\u0391\u0393"+
		"\u0398\u039e\u03a0\u03a5\u03a9\u03bc\u03c5\u03d2\u03d7\u03dd\u03e3\u03e5"+
		"\u03e9\u03ee\u03f2\u03f9\u0400\u0408\u040b\u0410\u0418\u041d\u0424\u042b"+
		"\u0430\u0436\u0442\u0447\u044b\u0455\u045c\u0463\u0466\u046b\u0473\u0476"+
		"\u047b\u0480\u0485\u048a\u0491\u0496\u049e\u04a3\u04a8\u04ae\u04b4\u04b7"+
		"\u04ba\u04c3\u04c9\u04cf\u04d2\u04d5\u04dd\u04e2\u04e8\u04eb\u04f2\u04fc"+
		"\u0505\u050f\u0514\u051f\u0524\u0530\u0535\u0541\u054b\u0550\u0558\u055b"+
		"\u0562\u056a\u056f\u0577\u0580\u0589\u0593\u0597\u059a\u05a3\u05b1\u05b4"+
		"\u05bd\u05c2\u05ca\u05d0\u05d8\u05e4\u05eb\u05f9\u060f\u0631\u063d\u0643"+
		"\u064f\u065c\u0676\u067a\u067f\u0683\u0687\u068f\u0693\u0697\u069e\u06a7"+
		"\u06af\u06be\u06ca\u06d0\u06d6\u06eb\u06f0\u06f6\u0702\u070d\u0717\u071a"+
		"\u071f\u0728\u072e\u0738\u073d\u0745\u075c\u0765\u077b\u0782\u078a\u0791"+
		"\u079b\u07b2\u07bb\u07c5\u07db\u07df\u07e6\u07ea\u07ee\u07f2\u07f8\u07fc"+
		"\u0800\u0804\u080a\u080e\u0812\u0816\u0818\u081d\u0821\u0825\u0829\u082d"+
		"\u0834\u0838\u083c\u0840\u0846\u084a\u084e\u0852\u0854\u0858\u0867\u0875"+
		"\u0881\u088a\u0899\u08a6\u08af\u08b5\u08bc\u08c1\u08c8\u08cd\u08d4\u08d9"+
		"\u08e0\u08e5\u08ed\u08f2\u08f6\u08fa\u08ff\u0906\u090d\u0912\u0919\u091e"+
		"\u0925\u092a\u0932\u0937\u093b\u0942\u0948\u094f\u0956\u095d\u0965\u096c"+
		"\u0974\u0978\u097f\u0986\u098d\u0995\u099c\u09a4\u09aa\u09b0\u09bc\u09c2"+
		"\u09c8\u09d3\u09dc\u09e3\u09ea\u09ef\u09f3\u09fc\u0a15\u0a1f\u0a2a\u0a35"+
		"\u0a40\u0a4b\u0a57\u0a59\u0a73\u0a75\u0a87\u0a89\u0a93\u0aa0\u0aa2\u0ab4"+
		"\u0ab6\u0ac2\u0ad0\u0ad4\u0ad8\u0ada\u0af1\u0afc\u0b02";
	public static final String _serializedATN = Utils.join(
		new String[] {
			_serializedATNSegment0,
			_serializedATNSegment1
		},
		""
	);
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}