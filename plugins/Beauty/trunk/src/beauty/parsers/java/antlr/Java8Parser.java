// Generated from /home/danson/src/jedit/plugins/Beauty/src/beauty/parsers/java/antlr/Java8.g4 by ANTLR 4.x

package beauty.parsers.java.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class Java8Parser extends Parser {
	public static final int
		T__0=1, ABSTRACT=2, ASSERT=3, BOOLEAN=4, BREAK=5, BYTE=6, CASE=7, CATCH=8, 
		CHAR=9, CLASS=10, CONST=11, CONTINUE=12, DEFAULT=13, DO=14, DOUBLE=15, 
		ELSE=16, ENUM=17, EXTENDS=18, FINAL=19, FINALLY=20, FLOAT=21, FOR=22, 
		IF=23, GOTO=24, IMPLEMENTS=25, IMPORT=26, INSTANCEOF=27, INT=28, INTERFACE=29, 
		LONG=30, NATIVE=31, NEW=32, PACKAGE=33, PRIVATE=34, PROTECTED=35, PUBLIC=36, 
		RETURN=37, SHORT=38, STATIC=39, STRICTFP=40, SUPER=41, SWITCH=42, SYNCHRONIZED=43, 
		THIS=44, THROW=45, THROWS=46, TRANSIENT=47, TRY=48, VOID=49, VOLATILE=50, 
		WHILE=51, IntegerLiteral=52, FloatingPointLiteral=53, BooleanLiteral=54, 
		CharacterLiteral=55, StringLiteral=56, NullLiteral=57, LPAREN=58, RPAREN=59, 
		LBRACE=60, RBRACE=61, LBRACK=62, RBRACK=63, SEMI=64, COMMA=65, DOT=66, 
		ASSIGN=67, GT=68, LT=69, BANG=70, TILDE=71, QUESTION=72, COLON=73, EQUAL=74, 
		LE=75, GE=76, NOTEQUAL=77, AND=78, OR=79, INC=80, DEC=81, ADD=82, SUB=83, 
		MUL=84, DIV=85, BITAND=86, BITOR=87, CARET=88, MOD=89, ARROW=90, COLONCOLON=91, 
		ADD_ASSIGN=92, SUB_ASSIGN=93, MUL_ASSIGN=94, DIV_ASSIGN=95, AND_ASSIGN=96, 
		OR_ASSIGN=97, XOR_ASSIGN=98, MOD_ASSIGN=99, LSHIFT_ASSIGN=100, RSHIFT_ASSIGN=101, 
		URSHIFT_ASSIGN=102, Identifier=103, AT=104, ELLIPSIS=105, WS=106, DOC_COMMENT=107, 
		COMMENT=108, LINE_COMMENT=109;
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
	public static final String[] ruleNames = {
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
		"variableDeclarator", "variableDeclaratorId", "variableInitializer", "unannType", 
		"unannPrimitiveType", "unannReferenceType", "unannClassOrInterfaceType", 
		"unannClassType", "unannClassType_lf_unannClassOrInterfaceType", "unannClassType_lfno_unannClassOrInterfaceType", 
		"unannInterfaceType", "unannInterfaceType_lf_unannClassOrInterfaceType", 
		"unannInterfaceType_lfno_unannClassOrInterfaceType", "unannTypeVariable", 
		"unannArrayType", "methodDeclaration", "methodModifiers", "methodModifier", 
		"methodHeader", "result", "methodDeclarator", "formalParameterList", "formalParameters", 
		"formalParameter", "variableModifier", "lastFormalParameter", "receiverParameter", 
		"throws_", "exceptionTypeList", "exceptionType", "methodBody", "instanceInitializer", 
		"staticInitializer", "constructorDeclaration", "constructorModifiers", 
		"constructorModifier", "constructorDeclarator", "simpleTypeName", "constructorBody", 
		"explicitConstructorInvocation", "enumDeclaration", "enumBody", "enumConstantList", 
		"enumConstant", "enumConstantModifier", "enumBodyDeclarations", "interfaceDeclaration", 
		"normalInterfaceDeclaration", "interfaceModifiers", "interfaceModifier", 
		"extendsInterfaces", "interfaceBody", "interfaceMemberDeclaration", "constantDeclaration", 
		"constantModifier", "interfaceMethodDeclaration", "interfaceMethodModifier", 
		"annotationTypeDeclaration", "annotationTypeBody", "annotationTypeMemberDeclaration", 
		"annotationTypeElementDeclaration", "annotationTypeElementModifier", "defaultValue", 
		"annotation", "annotationIdentifier", "annotationDim", "normalAnnotation", 
		"elementValuePairList", "elementValuePair", "elementValue", "elementValueArrayInitializer", 
		"elementValueList", "markerAnnotation", "singleElementAnnotation", "arrayInitializer", 
		"variableInitializerList", "block", "blockStatements", "blockStatement", 
		"localVariableDeclarationStatement", "localVariableDeclaration", "statement", 
		"statementNoShortIf", "statementWithoutTrailingSubstatement", "emptyStatement", 
		"labeledStatement", "labeledStatementNoShortIf", "expressionStatement", 
		"statementExpression", "ifThenStatement", "ifThenElseStatement", "ifThenElseStatementNoShortIf", 
		"assertStatement", "switchStatement", "switchBlock", "switchBlockStatementGroup", 
		"switchLabels", "switchLabel", "enumConstantName", "whileStatement", "whileStatementNoShortIf", 
		"doStatement", "forStatement", "forStatementNoShortIf", "basicForStatement", 
		"basicForStatementNoShortIf", "forInit", "forUpdate", "statementExpressionList", 
		"enhancedForStatement", "enhancedForStatementNoShortIf", "breakStatement", 
		"continueStatement", "returnStatement", "throwStatement", "synchronizedStatement", 
		"tryStatement", "catches", "catchClause", "catchFormalParameter", "catchType", 
		"finally_", "tryWithResourcesStatement", "resourceSpecification", "resourceList", 
		"resource", "primary", "primaryNoNewArray", "primaryNoNewArray_lf_arrayAccess", 
		"primaryNoNewArray_lfno_arrayAccess", "primaryNoNewArray_lf_primary", 
		"primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary", "primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary", 
		"primaryNoNewArray_lfno_primary", "primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary", 
		"primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary", "classInstanceCreationExpression", 
		"classInstanceCreationExpression_lf_primary", "classInstanceCreationExpression_lfno_primary", 
		"typeArgumentsOrDiamond", "fieldAccess", "fieldAccess_lf_primary", "fieldAccess_lfno_primary", 
		"arrayAccess", "arrayAccess_lf_primary", "arrayAccess_lfno_primary", "methodInvocation", 
		"methodInvocation_lf_primary", "methodInvocation_lfno_primary", "argumentList", 
		"methodReference", "methodReference_lf_primary", "methodReference_lfno_primary", 
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

	private static final String[] _LITERAL_NAMES = {
		null, "'[]'", "'abstract'", "'assert'", "'boolean'", "'break'", "'byte'", 
		"'case'", "'catch'", "'char'", "'class'", "'const'", "'continue'", "'default'", 
		"'do'", "'double'", "'else'", "'enum'", "'extends'", "'final'", "'finally'", 
		"'float'", "'for'", "'if'", "'goto'", "'implements'", "'import'", "'instanceof'", 
		"'int'", "'interface'", "'long'", "'native'", "'new'", "'package'", "'private'", 
		"'protected'", "'public'", "'return'", "'short'", "'static'", "'strictfp'", 
		"'super'", "'switch'", "'synchronized'", "'this'", "'throw'", "'throws'", 
		"'transient'", "'try'", "'void'", "'volatile'", "'while'", null, null, 
		null, null, null, "'null'", "'('", "')'", "'{'", "'}'", "'['", "']'", 
		"';'", "','", "'.'", "'='", "'>'", "'<'", "'!'", "'~'", "'?'", "':'", 
		"'=='", "'<='", "'>='", "'!='", "'&&'", "'||'", "'++'", "'--'", "'+'", 
		"'-'", "'*'", "'/'", "'&'", "'|'", "'^'", "'%'", "'->'", "'::'", "'+='", 
		"'-='", "'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'<<='", "'>>='", 
		"'>>>='", null, "'@'", "'...'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", 
		"CATCH", "CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", 
		"ELSE", "ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "IF", "GOTO", 
		"IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", 
		"NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", 
		"STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", 
		"THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "IntegerLiteral", 
		"FloatingPointLiteral", "BooleanLiteral", "CharacterLiteral", "StringLiteral", 
		"NullLiteral", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACK", "RBRACK", 
		"SEMI", "COMMA", "DOT", "ASSIGN", "GT", "LT", "BANG", "TILDE", "QUESTION", 
		"COLON", "EQUAL", "LE", "GE", "NOTEQUAL", "AND", "OR", "INC", "DEC", "ADD", 
		"SUB", "MUL", "DIV", "BITAND", "BITOR", "CARET", "MOD", "ARROW", "COLONCOLON", 
		"ADD_ASSIGN", "SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", 
		"OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "LSHIFT_ASSIGN", "RSHIFT_ASSIGN", 
		"URSHIFT_ASSIGN", "Identifier", "AT", "ELLIPSIS", "WS", "DOC_COMMENT", 
		"COMMENT", "LINE_COMMENT"
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

	@Override
	public String getGrammarFileName() { return "Java8.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	public Java8Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN);
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

	@RuleVersion(0)
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
			} else {
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

	@RuleVersion(0)
	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		try {
			setState(502);
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
		public List<? extends AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
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

	@RuleVersion(0)
	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_primitiveType);
		int _la;
		try {
			setState(518);
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

	@RuleVersion(0)
	public final NumericTypeContext numericType() throws RecognitionException {
		NumericTypeContext _localctx = new NumericTypeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_numericType);
		try {
			setState(522);
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

	@RuleVersion(0)
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
			} else {
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

	@RuleVersion(0)
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
			} else {
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

	@RuleVersion(0)
	public final ReferenceTypeContext referenceType() throws RecognitionException {
		ReferenceTypeContext _localctx = new ReferenceTypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_referenceType);
		try {
			setState(531);
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
		public List<? extends ClassType_lf_classOrInterfaceTypeContext> classType_lf_classOrInterfaceType() {
			return getRuleContexts(ClassType_lf_classOrInterfaceTypeContext.class);
		}
		public ClassType_lf_classOrInterfaceTypeContext classType_lf_classOrInterfaceType(int i) {
			return getRuleContext(ClassType_lf_classOrInterfaceTypeContext.class,i);
		}
		public List<? extends InterfaceType_lf_classOrInterfaceTypeContext> interfaceType_lf_classOrInterfaceType() {
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

	@RuleVersion(0)
	public final ClassOrInterfaceTypeContext classOrInterfaceType() throws RecognitionException {
		ClassOrInterfaceTypeContext _localctx = new ClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_classOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(535);
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

	@RuleVersion(0)
	public final ClassTypeContext classType() throws RecognitionException {
		ClassTypeContext _localctx = new ClassTypeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_classType);
		int _la;
		try {
			setState(554);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(544);
				annotationIdentifier();
				setState(546);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ClassType_lfno_classOrInterfaceTypeContext classType_lfno_classOrInterfaceType() throws RecognitionException {
		ClassType_lfno_classOrInterfaceTypeContext _localctx = new ClassType_lfno_classOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_classType_lfno_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(561);
			annotationIdentifier();
			setState(563);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_arrayType);
		try {
			setState(582);
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
		public List<? extends AnnotationDimContext> annotationDim() {
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

	@RuleVersion(0)
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
		public List<? extends TypeParameterModifierContext> typeParameterModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
		public TypeVariableContext typeVariable() {
			return getRuleContext(TypeVariableContext.class,0);
		}
		public ClassOrInterfaceTypeContext classOrInterfaceType() {
			return getRuleContext(ClassOrInterfaceTypeContext.class,0);
		}
		public List<? extends AdditionalBoundContext> additionalBound() {
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

	@RuleVersion(0)
	public final TypeBoundContext typeBound() throws RecognitionException {
		TypeBoundContext _localctx = new TypeBoundContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_typeBound);
		int _la;
		try {
			setState(613);
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

	@RuleVersion(0)
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
		public TypeArgumentListContext typeArgumentList() {
			return getRuleContext(TypeArgumentListContext.class,0);
		}
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

	@RuleVersion(0)
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
		public List<? extends TypeArgumentContext> typeArgument() {
			return getRuleContexts(TypeArgumentContext.class);
		}
		public TypeArgumentContext typeArgument(int i) {
			return getRuleContext(TypeArgumentContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final TypeArgumentContext typeArgument() throws RecognitionException {
		TypeArgumentContext _localctx = new TypeArgumentContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_typeArgument);
		try {
			setState(632);
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
		public List<? extends AnnotationContext> annotation() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final WildcardBoundsContext wildcardBounds() throws RecognitionException {
		WildcardBoundsContext _localctx = new WildcardBoundsContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_wildcardBounds);
		try {
			setState(648);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_typeName);
		try {
			setState(666);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ExpressionNameContext expressionName() throws RecognitionException {
		ExpressionNameContext _localctx = new ExpressionNameContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_expressionName);
		try {
			setState(684);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
		public List<? extends ImportDeclarationContext> importDeclaration() {
			return getRuleContexts(ImportDeclarationContext.class);
		}
		public ImportDeclarationContext importDeclaration(int i) {
			return getRuleContext(ImportDeclarationContext.class,i);
		}
		public List<? extends TypeDeclarationContext> typeDeclaration() {
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

	@RuleVersion(0)
	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_compilationUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(700);
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
		public List<? extends TerminalNode> Identifier() { return getTokens(Java8Parser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(Java8Parser.Identifier, i);
		}
		public List<? extends PackageModifierContext> packageModifier() {
			return getRuleContexts(PackageModifierContext.class);
		}
		public PackageModifierContext packageModifier(int i) {
			return getRuleContext(PackageModifierContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ImportDeclarationContext importDeclaration() throws RecognitionException {
		ImportDeclarationContext _localctx = new ImportDeclarationContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_importDeclaration);
		try {
			setState(739);
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
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
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

	@RuleVersion(0)
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
		public PackageOrTypeNameContext packageOrTypeName() {
			return getRuleContext(PackageOrTypeNameContext.class,0);
		}
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

	@RuleVersion(0)
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
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
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

	@RuleVersion(0)
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
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final TypeDeclarationContext typeDeclaration() throws RecognitionException {
		TypeDeclarationContext _localctx = new TypeDeclarationContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_typeDeclaration);
		try {
			setState(768);
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

	@RuleVersion(0)
	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_classDeclaration);
		try {
			setState(772);
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(777);
				typeParameters();
				}
			}

			setState(781);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(780);
				superclass();
				}
			}

			setState(784);
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
		public List<? extends ClassModifierContext> classModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ClassModifierContext classModifier() throws RecognitionException {
		ClassModifierContext _localctx = new ClassModifierContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_classModifier);
		try {
			setState(802);
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
		public TypeParameterListContext typeParameterList() {
			return getRuleContext(TypeParameterListContext.class,0);
		}
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

	@RuleVersion(0)
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
		public List<? extends TypeParameterContext> typeParameter() {
			return getRuleContexts(TypeParameterContext.class);
		}
		public TypeParameterContext typeParameter(int i) {
			return getRuleContext(TypeParameterContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
		public List<? extends InterfaceTypeContext> interfaceType() {
			return getRuleContexts(InterfaceTypeContext.class);
		}
		public InterfaceTypeContext interfaceType(int i) {
			return getRuleContext(InterfaceTypeContext.class,i);
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

	@RuleVersion(0)
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
		public List<? extends ClassBodyDeclarationContext> classBodyDeclaration() {
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

	@RuleVersion(0)
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE) | (1L << LBRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (SEMI - 64)) | (1L << (LT - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
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

	@RuleVersion(0)
	public final ClassBodyDeclarationContext classBodyDeclaration() throws RecognitionException {
		ClassBodyDeclarationContext _localctx = new ClassBodyDeclarationContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_classBodyDeclaration);
		try {
			setState(843);
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

	@RuleVersion(0)
	public final ClassMemberDeclarationContext classMemberDeclaration() throws RecognitionException {
		ClassMemberDeclarationContext _localctx = new ClassMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_classMemberDeclaration);
		try {
			setState(850);
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

	@RuleVersion(0)
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
		public List<? extends FieldModifierContext> fieldModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final FieldModifierContext fieldModifier() throws RecognitionException {
		FieldModifierContext _localctx = new FieldModifierContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_fieldModifier);
		try {
			setState(871);
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
		public List<? extends VariableDeclaratorContext> variableDeclarator() {
			return getRuleContexts(VariableDeclaratorContext.class);
		}
		public VariableDeclaratorContext variableDeclarator(int i) {
			return getRuleContext(VariableDeclaratorContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==T__0 || _la==AT) {
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

	@RuleVersion(0)
	public final VariableInitializerContext variableInitializer() throws RecognitionException {
		VariableInitializerContext _localctx = new VariableInitializerContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_variableInitializer);
		try {
			setState(892);
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

	@RuleVersion(0)
	public final UnannTypeContext unannType() throws RecognitionException {
		UnannTypeContext _localctx = new UnannTypeContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_unannType);
		try {
			setState(896);
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

	@RuleVersion(0)
	public final UnannPrimitiveTypeContext unannPrimitiveType() throws RecognitionException {
		UnannPrimitiveTypeContext _localctx = new UnannPrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_unannPrimitiveType);
		try {
			setState(900);
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

	@RuleVersion(0)
	public final UnannReferenceTypeContext unannReferenceType() throws RecognitionException {
		UnannReferenceTypeContext _localctx = new UnannReferenceTypeContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_unannReferenceType);
		try {
			setState(905);
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
		public List<? extends UnannClassType_lf_unannClassOrInterfaceTypeContext> unannClassType_lf_unannClassOrInterfaceType() {
			return getRuleContexts(UnannClassType_lf_unannClassOrInterfaceTypeContext.class);
		}
		public UnannClassType_lf_unannClassOrInterfaceTypeContext unannClassType_lf_unannClassOrInterfaceType(int i) {
			return getRuleContext(UnannClassType_lf_unannClassOrInterfaceTypeContext.class,i);
		}
		public List<? extends UnannInterfaceType_lf_unannClassOrInterfaceTypeContext> unannInterfaceType_lf_unannClassOrInterfaceType() {
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

	@RuleVersion(0)
	public final UnannClassOrInterfaceTypeContext unannClassOrInterfaceType() throws RecognitionException {
		UnannClassOrInterfaceTypeContext _localctx = new UnannClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_unannClassOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(909);
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

	@RuleVersion(0)
	public final UnannClassTypeContext unannClassType() throws RecognitionException {
		UnannClassTypeContext _localctx = new UnannClassTypeContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_unannClassType);
		int _la;
		try {
			setState(928);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(918);
				match(Identifier);
				setState(920);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final UnannArrayTypeContext unannArrayType() throws RecognitionException {
		UnannArrayTypeContext _localctx = new UnannArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_unannArrayType);
		try {
			setState(956);
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

	@RuleVersion(0)
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
		public List<? extends MethodModifierContext> methodModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final MethodModifierContext methodModifier() throws RecognitionException {
		MethodModifierContext _localctx = new MethodModifierContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_methodModifier);
		try {
			setState(978);
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
		public List<? extends AnnotationContext> annotation() {
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

	@RuleVersion(0)
	public final MethodHeaderContext methodHeader() throws RecognitionException {
		MethodHeaderContext _localctx = new MethodHeaderContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_methodHeader);
		int _la;
		try {
			setState(997);
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

	@RuleVersion(0)
	public final ResultContext result() throws RecognitionException {
		ResultContext _localctx = new ResultContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_result);
		try {
			setState(1001);
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==T__0 || _la==AT) {
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

	@RuleVersion(0)
	public final FormalParameterListContext formalParameterList() throws RecognitionException {
		FormalParameterListContext _localctx = new FormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_formalParameterList);
		try {
			setState(1017);
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
		public List<? extends FormalParameterContext> formalParameter() {
			return getRuleContexts(FormalParameterContext.class);
		}
		public FormalParameterContext formalParameter(int i) {
			return getRuleContext(FormalParameterContext.class,i);
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

	@RuleVersion(0)
	public final FormalParametersContext formalParameters() throws RecognitionException {
		FormalParametersContext _localctx = new FormalParametersContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_formalParameters);
		try {
			int _alt;
			setState(1035);
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
		public List<? extends VariableModifierContext> variableModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final VariableModifierContext variableModifier() throws RecognitionException {
		VariableModifierContext _localctx = new VariableModifierContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_variableModifier);
		try {
			setState(1048);
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
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public List<? extends VariableModifierContext> variableModifier() {
			return getRuleContexts(VariableModifierContext.class);
		}
		public VariableModifierContext variableModifier(int i) {
			return getRuleContext(VariableModifierContext.class,i);
		}
		public List<? extends AnnotationContext> annotation() {
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

	@RuleVersion(0)
	public final LastFormalParameterContext lastFormalParameter() throws RecognitionException {
		LastFormalParameterContext _localctx = new LastFormalParameterContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_lastFormalParameter);
		int _la;
		try {
			setState(1067);
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
		public List<? extends AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
		public List<? extends ExceptionTypeContext> exceptionType() {
			return getRuleContexts(ExceptionTypeContext.class);
		}
		public ExceptionTypeContext exceptionType(int i) {
			return getRuleContext(ExceptionTypeContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ExceptionTypeContext exceptionType() throws RecognitionException {
		ExceptionTypeContext _localctx = new ExceptionTypeContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_exceptionType);
		try {
			setState(1095);
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

	@RuleVersion(0)
	public final MethodBodyContext methodBody() throws RecognitionException {
		MethodBodyContext _localctx = new MethodBodyContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_methodBody);
		try {
			setState(1099);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
		public List<? extends ConstructorModifierContext> constructorModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ConstructorModifierContext constructorModifier() throws RecognitionException {
		ConstructorModifierContext _localctx = new ConstructorModifierContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_constructorModifier);
		try {
			setState(1123);
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

	@RuleVersion(0)
	public final ConstructorDeclaratorContext constructorDeclarator() throws RecognitionException {
		ConstructorDeclaratorContext _localctx = new ConstructorDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_constructorDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1126);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				{
				setState(1138);
				explicitConstructorInvocation();
				}
				break;
			}
			setState(1142);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
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

	@RuleVersion(0)
	public final ExplicitConstructorInvocationContext explicitConstructorInvocation() throws RecognitionException {
		ExplicitConstructorInvocationContext _localctx = new ExplicitConstructorInvocationContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_explicitConstructorInvocation);
		int _la;
		try {
			setState(1192);
			switch ( getInterpreter().adaptivePredict(_input,104,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1147);
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
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
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
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
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
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
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
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==Identifier || _la==AT) {
				{
				setState(1203);
				enumConstantList();
				}
			}

			setState(1207);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1206);
				match(COMMA);
				}
			}

			setState(1210);
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
		public List<? extends EnumConstantContext> enumConstant() {
			return getRuleContexts(EnumConstantContext.class);
		}
		public EnumConstantContext enumConstant(int i) {
			return getRuleContext(EnumConstantContext.class,i);
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

	@RuleVersion(0)
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
		public List<? extends EnumConstantModifierContext> enumConstantModifier() {
			return getRuleContexts(EnumConstantModifierContext.class);
		}
		public EnumConstantModifierContext enumConstantModifier(int i) {
			return getRuleContext(EnumConstantModifierContext.class,i);
		}
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(1229);
				match(LPAREN);
				setState(1231);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
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

	@RuleVersion(0)
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
		public List<? extends ClassBodyDeclarationContext> classBodyDeclaration() {
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

	@RuleVersion(0)
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE) | (1L << LBRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (SEMI - 64)) | (1L << (LT - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
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

	@RuleVersion(0)
	public final InterfaceDeclarationContext interfaceDeclaration() throws RecognitionException {
		InterfaceDeclarationContext _localctx = new InterfaceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_interfaceDeclaration);
		try {
			setState(1250);
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1255);
				typeParameters();
				}
			}

			setState(1259);
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
		public List<? extends InterfaceModifierContext> interfaceModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final InterfaceModifierContext interfaceModifier() throws RecognitionException {
		InterfaceModifierContext _localctx = new InterfaceModifierContext(_ctx, getState());
		enterRule(_localctx, 212, RULE_interfaceModifier);
		try {
			setState(1276);
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

	@RuleVersion(0)
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
		public List<? extends InterfaceMemberDeclarationContext> interfaceMemberDeclaration() {
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

	@RuleVersion(0)
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DEFAULT) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << VOID))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (SEMI - 64)) | (1L << (LT - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
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

	@RuleVersion(0)
	public final InterfaceMemberDeclarationContext interfaceMemberDeclaration() throws RecognitionException {
		InterfaceMemberDeclarationContext _localctx = new InterfaceMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_interfaceMemberDeclaration);
		try {
			setState(1295);
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
		public List<? extends ConstantModifierContext> constantModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ConstantModifierContext constantModifier() throws RecognitionException {
		ConstantModifierContext _localctx = new ConstantModifierContext(_ctx, getState());
		enterRule(_localctx, 222, RULE_constantModifier);
		try {
			setState(1311);
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
		public List<? extends InterfaceMethodModifierContext> interfaceMethodModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final InterfaceMethodModifierContext interfaceMethodModifier() throws RecognitionException {
		InterfaceMethodModifierContext _localctx = new InterfaceMethodModifierContext(_ctx, getState());
		enterRule(_localctx, 226, RULE_interfaceMethodModifier);
		try {
			setState(1328);
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
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public AnnotationTypeBodyContext annotationTypeBody() {
			return getRuleContext(AnnotationTypeBodyContext.class,0);
		}
		public List<? extends InterfaceModifierContext> interfaceModifier() {
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

	@RuleVersion(0)
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
		public List<? extends AnnotationTypeMemberDeclarationContext> annotationTypeMemberDeclaration() {
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

	@RuleVersion(0)
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (SEMI - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
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

	@RuleVersion(0)
	public final AnnotationTypeMemberDeclarationContext annotationTypeMemberDeclaration() throws RecognitionException {
		AnnotationTypeMemberDeclarationContext _localctx = new AnnotationTypeMemberDeclarationContext(_ctx, getState());
		enterRule(_localctx, 232, RULE_annotationTypeMemberDeclaration);
		try {
			setState(1355);
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
		public List<? extends AnnotationTypeElementModifierContext> annotationTypeElementModifier() {
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==T__0 || _la==AT) {
				{
				setState(1367);
				dims();
				}
			}

			setState(1371);
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

	@RuleVersion(0)
	public final AnnotationTypeElementModifierContext annotationTypeElementModifier() throws RecognitionException {
		AnnotationTypeElementModifierContext _localctx = new AnnotationTypeElementModifierContext(_ctx, getState());
		enterRule(_localctx, 236, RULE_annotationTypeElementModifier);
		try {
			setState(1378);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final AnnotationContext annotation() throws RecognitionException {
		AnnotationContext _localctx = new AnnotationContext(_ctx, getState());
		enterRule(_localctx, 240, RULE_annotation);
		try {
			setState(1386);
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
		public List<? extends AnnotationContext> annotation() {
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

	@RuleVersion(0)
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
		public List<? extends AnnotationContext> annotation() {
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

	@RuleVersion(0)
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
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
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

	@RuleVersion(0)
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
		public List<? extends ElementValuePairContext> elementValuePair() {
			return getRuleContexts(ElementValuePairContext.class);
		}
		public ElementValuePairContext elementValuePair(int i) {
			return getRuleContext(ElementValuePairContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ElementValueContext elementValue() throws RecognitionException {
		ElementValueContext _localctx = new ElementValueContext(_ctx, getState());
		enterRule(_localctx, 252, RULE_elementValue);
		try {
			setState(1427);
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
				{
				setState(1430);
				elementValueList();
				}
			}

			setState(1434);
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
		public List<? extends ElementValueContext> elementValue() {
			return getRuleContexts(ElementValueContext.class);
		}
		public ElementValueContext elementValue(int i) {
			return getRuleContext(ElementValueContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public ElementValueContext elementValue() {
			return getRuleContext(ElementValueContext.class,0);
		}
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
				{
				setState(1456);
				variableInitializerList();
				}
			}

			setState(1460);
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
		public List<? extends VariableInitializerContext> variableInitializer() {
			return getRuleContexts(VariableInitializerContext.class);
		}
		public VariableInitializerContext variableInitializer(int i) {
			return getRuleContext(VariableInitializerContext.class,i);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
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
		public List<? extends BlockStatementContext> blockStatement() {
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

	@RuleVersion(0)
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << LBRACE))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
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

	@RuleVersion(0)
	public final BlockStatementContext blockStatement() throws RecognitionException {
		BlockStatementContext _localctx = new BlockStatementContext(_ctx, getState());
		enterRule(_localctx, 270, RULE_blockStatement);
		try {
			setState(1488);
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

	@RuleVersion(0)
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
		public List<? extends VariableModifierContext> variableModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 276, RULE_statement);
		try {
			setState(1508);
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

	@RuleVersion(0)
	public final StatementNoShortIfContext statementNoShortIf() throws RecognitionException {
		StatementNoShortIfContext _localctx = new StatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 278, RULE_statementNoShortIf);
		try {
			setState(1515);
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

	@RuleVersion(0)
	public final StatementWithoutTrailingSubstatementContext statementWithoutTrailingSubstatement() throws RecognitionException {
		StatementWithoutTrailingSubstatementContext _localctx = new StatementWithoutTrailingSubstatementContext(_ctx, getState());
		enterRule(_localctx, 280, RULE_statementWithoutTrailingSubstatement);
		try {
			setState(1529);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final StatementExpressionContext statementExpression() throws RecognitionException {
		StatementExpressionContext _localctx = new StatementExpressionContext(_ctx, getState());
		enterRule(_localctx, 290, RULE_statementExpression);
		try {
			setState(1551);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementNoShortIfContext statementNoShortIf() {
			return getRuleContext(StatementNoShortIfContext.class,0);
		}
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

	@RuleVersion(0)
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<? extends StatementNoShortIfContext> statementNoShortIf() {
			return getRuleContexts(StatementNoShortIfContext.class);
		}
		public StatementNoShortIfContext statementNoShortIf(int i) {
			return getRuleContext(StatementNoShortIfContext.class,i);
		}
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

	@RuleVersion(0)
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
		public List<? extends ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
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

	@RuleVersion(0)
	public final AssertStatementContext assertStatement() throws RecognitionException {
		AssertStatementContext _localctx = new AssertStatementContext(_ctx, getState());
		enterRule(_localctx, 298, RULE_assertStatement);
		try {
			setState(1585);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
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
		public List<? extends SwitchBlockStatementGroupContext> switchBlockStatementGroup() {
			return getRuleContexts(SwitchBlockStatementGroupContext.class);
		}
		public SwitchBlockStatementGroupContext switchBlockStatementGroup(int i) {
			return getRuleContext(SwitchBlockStatementGroupContext.class,i);
		}
		public List<? extends SwitchLabelContext> switchLabel() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
		public List<? extends SwitchLabelContext> switchLabel() {
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

	@RuleVersion(0)
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
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
		}
		public EnumConstantNameContext enumConstantName() {
			return getRuleContext(EnumConstantNameContext.class,0);
		}
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

	@RuleVersion(0)
	public final SwitchLabelContext switchLabel() throws RecognitionException {
		SwitchLabelContext _localctx = new SwitchLabelContext(_ctx, getState());
		enterRule(_localctx, 308, RULE_switchLabel);
		try {
			setState(1628);
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

	@RuleVersion(0)
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
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
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final ForStatementContext forStatement() throws RecognitionException {
		ForStatementContext _localctx = new ForStatementContext(_ctx, getState());
		enterRule(_localctx, 318, RULE_forStatement);
		try {
			setState(1654);
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

	@RuleVersion(0)
	public final ForStatementNoShortIfContext forStatementNoShortIf() throws RecognitionException {
		ForStatementNoShortIfContext _localctx = new ForStatementNoShortIfContext(_ctx, getState());
		enterRule(_localctx, 320, RULE_forStatementNoShortIf);
		try {
			setState(1658);
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & ((1L << (INC - 80)) | (1L << (DEC - 80)) | (1L << (Identifier - 80)) | (1L << (AT - 80)))) != 0)) {
				{
				setState(1662);
				forInit();
				}
			}

			setState(1665);
			match(SEMI);
			setState(1667);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
				{
				setState(1666);
				expression();
				}
			}

			setState(1669);
			match(SEMI);
			setState(1671);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & ((1L << (INC - 80)) | (1L << (DEC - 80)) | (1L << (Identifier - 80)) | (1L << (AT - 80)))) != 0)) {
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & ((1L << (INC - 80)) | (1L << (DEC - 80)) | (1L << (Identifier - 80)) | (1L << (AT - 80)))) != 0)) {
				{
				setState(1678);
				forInit();
				}
			}

			setState(1681);
			match(SEMI);
			setState(1683);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
				{
				setState(1682);
				expression();
				}
			}

			setState(1685);
			match(SEMI);
			setState(1687);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & ((1L << (INC - 80)) | (1L << (DEC - 80)) | (1L << (Identifier - 80)) | (1L << (AT - 80)))) != 0)) {
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

	@RuleVersion(0)
	public final ForInitContext forInit() throws RecognitionException {
		ForInitContext _localctx = new ForInitContext(_ctx, getState());
		enterRule(_localctx, 326, RULE_forInit);
		try {
			setState(1694);
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

	@RuleVersion(0)
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
		public List<? extends StatementExpressionContext> statementExpression() {
			return getRuleContexts(StatementExpressionContext.class);
		}
		public StatementExpressionContext statementExpression(int i) {
			return getRuleContext(StatementExpressionContext.class,i);
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

	@RuleVersion(0)
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
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<? extends VariableModifierContext> variableModifier() {
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

	@RuleVersion(0)
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
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public VariableDeclaratorIdContext variableDeclaratorId() {
			return getRuleContext(VariableDeclaratorIdContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementNoShortIfContext statementNoShortIf() {
			return getRuleContext(StatementNoShortIfContext.class,0);
		}
		public List<? extends VariableModifierContext> variableModifier() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final TryStatementContext tryStatement() throws RecognitionException {
		TryStatementContext _localctx = new TryStatementContext(_ctx, getState());
		enterRule(_localctx, 346, RULE_tryStatement);
		int _la;
		try {
			setState(1776);
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
		public List<? extends CatchClauseContext> catchClause() {
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

	@RuleVersion(0)
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
		public CatchFormalParameterContext catchFormalParameter() {
			return getRuleContext(CatchFormalParameterContext.class,0);
		}
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

	@RuleVersion(0)
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
		public List<? extends VariableModifierContext> variableModifier() {
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

	@RuleVersion(0)
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
		public List<? extends ClassTypeContext> classType() {
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==CATCH) {
				{
				setState(1814);
				catches();
				}
			}

			setState(1818);
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
		public ResourceListContext resourceList() {
			return getRuleContext(ResourceListContext.class,0);
		}
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

	@RuleVersion(0)
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
		public List<? extends ResourceContext> resource() {
			return getRuleContexts(ResourceContext.class);
		}
		public ResourceContext resource(int i) {
			return getRuleContext(ResourceContext.class,i);
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

	@RuleVersion(0)
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<? extends VariableModifierContext> variableModifier() {
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

	@RuleVersion(0)
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
		public List<? extends PrimaryNoNewArray_lf_primaryContext> primaryNoNewArray_lf_primary() {
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

	@RuleVersion(0)
	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 366, RULE_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1848);
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
		public List<? extends SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
	public final PrimaryNoNewArrayContext primaryNoNewArray() throws RecognitionException {
		PrimaryNoNewArrayContext _localctx = new PrimaryNoNewArrayContext(_ctx, getState());
		enterRule(_localctx, 368, RULE_primaryNoNewArray);
		int _la;
		try {
			setState(1884);
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
				while (_la==T__0) {
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

	@RuleVersion(0)
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
		public List<? extends SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
	public final PrimaryNoNewArray_lfno_arrayAccessContext primaryNoNewArray_lfno_arrayAccess() throws RecognitionException {
		PrimaryNoNewArray_lfno_arrayAccessContext _localctx = new PrimaryNoNewArray_lfno_arrayAccessContext(_ctx, getState());
		enterRule(_localctx, 372, RULE_primaryNoNewArray_lfno_arrayAccess);
		int _la;
		try {
			setState(1915);
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
				while (_la==T__0) {
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

	@RuleVersion(0)
	public final PrimaryNoNewArray_lf_primaryContext primaryNoNewArray_lf_primary() throws RecognitionException {
		PrimaryNoNewArray_lf_primaryContext _localctx = new PrimaryNoNewArray_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 374, RULE_primaryNoNewArray_lf_primary);
		try {
			setState(1922);
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

	@RuleVersion(0)
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

	@RuleVersion(0)
	public final PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary() throws RecognitionException {
		PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext _localctx = new PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 378, RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary);
		try {
			setState(1930);
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
		public List<? extends SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public UnannPrimitiveTypeContext unannPrimitiveType() {
			return getRuleContext(UnannPrimitiveTypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
	public final PrimaryNoNewArray_lfno_primaryContext primaryNoNewArray_lfno_primary() throws RecognitionException {
		PrimaryNoNewArray_lfno_primaryContext _localctx = new PrimaryNoNewArray_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 380, RULE_primaryNoNewArray_lfno_primary);
		int _la;
		try {
			setState(1970);
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
				while (_la==T__0) {
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
				while (_la==T__0) {
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

	@RuleVersion(0)
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
		public List<? extends SquareBracketsContext> squareBrackets() {
			return getRuleContexts(SquareBracketsContext.class);
		}
		public SquareBracketsContext squareBrackets(int i) {
			return getRuleContext(SquareBracketsContext.class,i);
		}
		public UnannPrimitiveTypeContext unannPrimitiveType() {
			return getRuleContext(UnannPrimitiveTypeContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
	public final PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary() throws RecognitionException {
		PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext _localctx = new PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 384, RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary);
		int _la;
		try {
			setState(2011);
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
				while (_la==T__0) {
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
				while (_la==T__0) {
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
		public List<? extends AnnotationIdentifierContext> annotationIdentifier() {
			return getRuleContexts(AnnotationIdentifierContext.class);
		}
		public AnnotationIdentifierContext annotationIdentifier(int i) {
			return getRuleContext(AnnotationIdentifierContext.class,i);
		}
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

	@RuleVersion(0)
	public final ClassInstanceCreationExpressionContext classInstanceCreationExpression() throws RecognitionException {
		ClassInstanceCreationExpressionContext _localctx = new ClassInstanceCreationExpressionContext(_ctx, getState());
		enterRule(_localctx, 386, RULE_classInstanceCreationExpression);
		int _la;
		try {
			setState(2072);
			switch ( getInterpreter().adaptivePredict(_input,210,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2013);
				match(NEW);
				setState(2015);
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
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2025);
					typeArgumentsOrDiamond();
					}
				}

				setState(2028);
				match(LPAREN);
				setState(2030);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2029);
					argumentList();
					}
				}

				setState(2032);
				match(RPAREN);
				setState(2034);
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
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2043);
					typeArgumentsOrDiamond();
					}
				}

				setState(2046);
				match(LPAREN);
				setState(2048);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2047);
					argumentList();
					}
				}

				setState(2050);
				match(RPAREN);
				setState(2052);
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
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2061);
					typeArgumentsOrDiamond();
					}
				}

				setState(2064);
				match(LPAREN);
				setState(2066);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2065);
					argumentList();
					}
				}

				setState(2068);
				match(RPAREN);
				setState(2070);
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
		public AnnotationIdentifierContext annotationIdentifier() {
			return getRuleContext(AnnotationIdentifierContext.class,0);
		}
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

	@RuleVersion(0)
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
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2080);
				typeArgumentsOrDiamond();
				}
			}

			setState(2083);
			match(LPAREN);
			setState(2085);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
				{
				setState(2084);
				argumentList();
				}
			}

			setState(2087);
			match(RPAREN);
			setState(2089);
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
		public List<? extends AnnotationIdentifierContext> annotationIdentifier() {
			return getRuleContexts(AnnotationIdentifierContext.class);
		}
		public AnnotationIdentifierContext annotationIdentifier(int i) {
			return getRuleContext(AnnotationIdentifierContext.class,i);
		}
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

	@RuleVersion(0)
	public final ClassInstanceCreationExpression_lfno_primaryContext classInstanceCreationExpression_lfno_primary() throws RecognitionException {
		ClassInstanceCreationExpression_lfno_primaryContext _localctx = new ClassInstanceCreationExpression_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 390, RULE_classInstanceCreationExpression_lfno_primary);
		int _la;
		try {
			setState(2132);
			switch (_input.LA(1)) {
			case NEW:
				enterOuterAlt(_localctx, 1);
				{
				setState(2091);
				match(NEW);
				setState(2093);
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
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2103);
					typeArgumentsOrDiamond();
					}
				}

				setState(2106);
				match(LPAREN);
				setState(2108);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2107);
					argumentList();
					}
				}

				setState(2110);
				match(RPAREN);
				setState(2112);
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
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2121);
					typeArgumentsOrDiamond();
					}
				}

				setState(2124);
				match(LPAREN);
				setState(2126);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2125);
					argumentList();
					}
				}

				setState(2128);
				match(RPAREN);
				setState(2130);
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

	@RuleVersion(0)
	public final TypeArgumentsOrDiamondContext typeArgumentsOrDiamond() throws RecognitionException {
		TypeArgumentsOrDiamondContext _localctx = new TypeArgumentsOrDiamondContext(_ctx, getState());
		enterRule(_localctx, 392, RULE_typeArgumentsOrDiamond);
		try {
			setState(2137);
			switch ( getInterpreter().adaptivePredict(_input,225,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2134);
				typeArguments();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2135);
				match(LT);
				setState(2136);
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

	public static class FieldAccessContext extends ParserRuleContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
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

	@RuleVersion(0)
	public final FieldAccessContext fieldAccess() throws RecognitionException {
		FieldAccessContext _localctx = new FieldAccessContext(_ctx, getState());
		enterRule(_localctx, 394, RULE_fieldAccess);
		try {
			setState(2152);
			switch ( getInterpreter().adaptivePredict(_input,226,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2139);
				primary();
				setState(2140);
				match(DOT);
				setState(2141);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2143);
				match(SUPER);
				setState(2144);
				match(DOT);
				setState(2145);
				match(Identifier);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2146);
				typeName();
				setState(2147);
				match(DOT);
				setState(2148);
				match(SUPER);
				setState(2149);
				match(DOT);
				setState(2150);
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

	@RuleVersion(0)
	public final FieldAccess_lf_primaryContext fieldAccess_lf_primary() throws RecognitionException {
		FieldAccess_lf_primaryContext _localctx = new FieldAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 396, RULE_fieldAccess_lf_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2154);
			match(DOT);
			setState(2155);
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

	@RuleVersion(0)
	public final FieldAccess_lfno_primaryContext fieldAccess_lfno_primary() throws RecognitionException {
		FieldAccess_lfno_primaryContext _localctx = new FieldAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 398, RULE_fieldAccess_lfno_primary);
		try {
			setState(2166);
			switch (_input.LA(1)) {
			case SUPER:
				enterOuterAlt(_localctx, 1);
				{
				setState(2157);
				match(SUPER);
				setState(2158);
				match(DOT);
				setState(2159);
				match(Identifier);
				}
				break;
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2160);
				typeName();
				setState(2161);
				match(DOT);
				setState(2162);
				match(SUPER);
				setState(2163);
				match(DOT);
				setState(2164);
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
		public List<? extends ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public PrimaryNoNewArray_lfno_arrayAccessContext primaryNoNewArray_lfno_arrayAccess() {
			return getRuleContext(PrimaryNoNewArray_lfno_arrayAccessContext.class,0);
		}
		public List<? extends PrimaryNoNewArray_lf_arrayAccessContext> primaryNoNewArray_lf_arrayAccess() {
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

	@RuleVersion(0)
	public final ArrayAccessContext arrayAccess() throws RecognitionException {
		ArrayAccessContext _localctx = new ArrayAccessContext(_ctx, getState());
		enterRule(_localctx, 400, RULE_arrayAccess);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2178);
			switch ( getInterpreter().adaptivePredict(_input,228,_ctx) ) {
			case 1:
				{
				setState(2168);
				expressionName();
				setState(2169);
				match(LBRACK);
				setState(2170);
				expression();
				setState(2171);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2173);
				primaryNoNewArray_lfno_arrayAccess();
				setState(2174);
				match(LBRACK);
				setState(2175);
				expression();
				setState(2176);
				match(RBRACK);
				}
				break;
			}
			setState(2187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(2180);
				primaryNoNewArray_lf_arrayAccess();
				setState(2181);
				match(LBRACK);
				setState(2182);
				expression();
				setState(2183);
				match(RBRACK);
				}
				}
				setState(2189);
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
		public List<? extends ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<? extends PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext> primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary() {
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

	@RuleVersion(0)
	public final ArrayAccess_lf_primaryContext arrayAccess_lf_primary() throws RecognitionException {
		ArrayAccess_lf_primaryContext _localctx = new ArrayAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 402, RULE_arrayAccess_lf_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2190);
			primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary();
			setState(2191);
			match(LBRACK);
			setState(2192);
			expression();
			setState(2193);
			match(RBRACK);
			}
			setState(2202);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,230,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2195);
					primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary();
					setState(2196);
					match(LBRACK);
					setState(2197);
					expression();
					setState(2198);
					match(RBRACK);
					}
					} 
				}
				setState(2204);
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
		public List<? extends ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary() {
			return getRuleContext(PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext.class,0);
		}
		public List<? extends PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext> primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary() {
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

	@RuleVersion(0)
	public final ArrayAccess_lfno_primaryContext arrayAccess_lfno_primary() throws RecognitionException {
		ArrayAccess_lfno_primaryContext _localctx = new ArrayAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 404, RULE_arrayAccess_lfno_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2215);
			switch ( getInterpreter().adaptivePredict(_input,231,_ctx) ) {
			case 1:
				{
				setState(2205);
				expressionName();
				setState(2206);
				match(LBRACK);
				setState(2207);
				expression();
				setState(2208);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2210);
				primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary();
				setState(2211);
				match(LBRACK);
				setState(2212);
				expression();
				setState(2213);
				match(RBRACK);
				}
				break;
			}
			setState(2224);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,232,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2217);
					primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary();
					setState(2218);
					match(LBRACK);
					setState(2219);
					expression();
					setState(2220);
					match(RBRACK);
					}
					} 
				}
				setState(2226);
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
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
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

	@RuleVersion(0)
	public final MethodInvocationContext methodInvocation() throws RecognitionException {
		MethodInvocationContext _localctx = new MethodInvocationContext(_ctx, getState());
		enterRule(_localctx, 406, RULE_methodInvocation);
		int _la;
		try {
			setState(2295);
			switch ( getInterpreter().adaptivePredict(_input,244,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2227);
				methodName();
				setState(2228);
				match(LPAREN);
				setState(2230);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2229);
					argumentList();
					}
				}

				setState(2232);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2234);
				typeName();
				setState(2235);
				match(DOT);
				setState(2237);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2236);
					typeArguments();
					}
				}

				setState(2239);
				match(Identifier);
				setState(2240);
				match(LPAREN);
				setState(2242);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2241);
					argumentList();
					}
				}

				setState(2244);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2246);
				expressionName();
				setState(2247);
				match(DOT);
				setState(2249);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2248);
					typeArguments();
					}
				}

				setState(2251);
				match(Identifier);
				setState(2252);
				match(LPAREN);
				setState(2254);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2253);
					argumentList();
					}
				}

				setState(2256);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2258);
				primary();
				setState(2259);
				match(DOT);
				setState(2261);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2260);
					typeArguments();
					}
				}

				setState(2263);
				match(Identifier);
				setState(2264);
				match(LPAREN);
				setState(2266);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2265);
					argumentList();
					}
				}

				setState(2268);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2270);
				match(SUPER);
				setState(2271);
				match(DOT);
				setState(2273);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2272);
					typeArguments();
					}
				}

				setState(2275);
				match(Identifier);
				setState(2276);
				match(LPAREN);
				setState(2278);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2277);
					argumentList();
					}
				}

				setState(2280);
				match(RPAREN);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2281);
				typeName();
				setState(2282);
				match(DOT);
				setState(2283);
				match(SUPER);
				setState(2284);
				match(DOT);
				setState(2286);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2285);
					typeArguments();
					}
				}

				setState(2288);
				match(Identifier);
				setState(2289);
				match(LPAREN);
				setState(2291);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2290);
					argumentList();
					}
				}

				setState(2293);
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
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
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

	@RuleVersion(0)
	public final MethodInvocation_lf_primaryContext methodInvocation_lf_primary() throws RecognitionException {
		MethodInvocation_lf_primaryContext _localctx = new MethodInvocation_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 408, RULE_methodInvocation_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2297);
			match(DOT);
			setState(2299);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2298);
				typeArguments();
				}
			}

			setState(2301);
			match(Identifier);
			setState(2302);
			match(LPAREN);
			setState(2304);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
				{
				setState(2303);
				argumentList();
				}
			}

			setState(2306);
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
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
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

	@RuleVersion(0)
	public final MethodInvocation_lfno_primaryContext methodInvocation_lfno_primary() throws RecognitionException {
		MethodInvocation_lfno_primaryContext _localctx = new MethodInvocation_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 410, RULE_methodInvocation_lfno_primary);
		int _la;
		try {
			setState(2364);
			switch ( getInterpreter().adaptivePredict(_input,256,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2308);
				methodName();
				setState(2309);
				match(LPAREN);
				setState(2311);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2310);
					argumentList();
					}
				}

				setState(2313);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2315);
				typeName();
				setState(2316);
				match(DOT);
				setState(2318);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2317);
					typeArguments();
					}
				}

				setState(2320);
				match(Identifier);
				setState(2321);
				match(LPAREN);
				setState(2323);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2322);
					argumentList();
					}
				}

				setState(2325);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2327);
				expressionName();
				setState(2328);
				match(DOT);
				setState(2330);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2329);
					typeArguments();
					}
				}

				setState(2332);
				match(Identifier);
				setState(2333);
				match(LPAREN);
				setState(2335);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2334);
					argumentList();
					}
				}

				setState(2337);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2339);
				match(SUPER);
				setState(2340);
				match(DOT);
				setState(2342);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2341);
					typeArguments();
					}
				}

				setState(2344);
				match(Identifier);
				setState(2345);
				match(LPAREN);
				setState(2347);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2346);
					argumentList();
					}
				}

				setState(2349);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2350);
				typeName();
				setState(2351);
				match(DOT);
				setState(2352);
				match(SUPER);
				setState(2353);
				match(DOT);
				setState(2355);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2354);
					typeArguments();
					}
				}

				setState(2357);
				match(Identifier);
				setState(2358);
				match(LPAREN);
				setState(2360);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN))) != 0) || ((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & ((1L << (BANG - 70)) | (1L << (TILDE - 70)) | (1L << (INC - 70)) | (1L << (DEC - 70)) | (1L << (ADD - 70)) | (1L << (SUB - 70)) | (1L << (Identifier - 70)) | (1L << (AT - 70)))) != 0)) {
					{
					setState(2359);
					argumentList();
					}
				}

				setState(2362);
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
		public List<? extends ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
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

	@RuleVersion(0)
	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 412, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2366);
			expression();
			setState(2371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2367);
				match(COMMA);
				setState(2368);
				expression();
				}
				}
				setState(2373);
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
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
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

	@RuleVersion(0)
	public final MethodReferenceContext methodReference() throws RecognitionException {
		MethodReferenceContext _localctx = new MethodReferenceContext(_ctx, getState());
		enterRule(_localctx, 414, RULE_methodReference);
		int _la;
		try {
			setState(2421);
			switch ( getInterpreter().adaptivePredict(_input,264,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2374);
				expressionName();
				setState(2375);
				match(COLONCOLON);
				setState(2377);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2376);
					typeArguments();
					}
				}

				setState(2379);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2381);
				referenceType();
				setState(2382);
				match(COLONCOLON);
				setState(2384);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2383);
					typeArguments();
					}
				}

				setState(2386);
				match(Identifier);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2388);
				primary();
				setState(2389);
				match(COLONCOLON);
				setState(2391);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2390);
					typeArguments();
					}
				}

				setState(2393);
				match(Identifier);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2395);
				match(SUPER);
				setState(2396);
				match(COLONCOLON);
				setState(2398);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2397);
					typeArguments();
					}
				}

				setState(2400);
				match(Identifier);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2401);
				typeName();
				setState(2402);
				match(DOT);
				setState(2403);
				match(SUPER);
				setState(2404);
				match(COLONCOLON);
				setState(2406);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2405);
					typeArguments();
					}
				}

				setState(2408);
				match(Identifier);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2410);
				classType();
				setState(2411);
				match(COLONCOLON);
				setState(2413);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2412);
					typeArguments();
					}
				}

				setState(2415);
				match(NEW);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2417);
				arrayType();
				setState(2418);
				match(COLONCOLON);
				setState(2419);
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

	@RuleVersion(0)
	public final MethodReference_lf_primaryContext methodReference_lf_primary() throws RecognitionException {
		MethodReference_lf_primaryContext _localctx = new MethodReference_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 416, RULE_methodReference_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2423);
			match(COLONCOLON);
			setState(2425);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2424);
				typeArguments();
				}
			}

			setState(2427);
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
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
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

	@RuleVersion(0)
	public final MethodReference_lfno_primaryContext methodReference_lfno_primary() throws RecognitionException {
		MethodReference_lfno_primaryContext _localctx = new MethodReference_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 418, RULE_methodReference_lfno_primary);
		int _la;
		try {
			setState(2469);
			switch ( getInterpreter().adaptivePredict(_input,271,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2429);
				expressionName();
				setState(2430);
				match(COLONCOLON);
				setState(2432);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2431);
					typeArguments();
					}
				}

				setState(2434);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2436);
				referenceType();
				setState(2437);
				match(COLONCOLON);
				setState(2439);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2438);
					typeArguments();
					}
				}

				setState(2441);
				match(Identifier);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2443);
				match(SUPER);
				setState(2444);
				match(COLONCOLON);
				setState(2446);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2445);
					typeArguments();
					}
				}

				setState(2448);
				match(Identifier);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2449);
				typeName();
				setState(2450);
				match(DOT);
				setState(2451);
				match(SUPER);
				setState(2452);
				match(COLONCOLON);
				setState(2454);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2453);
					typeArguments();
					}
				}

				setState(2456);
				match(Identifier);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2458);
				classType();
				setState(2459);
				match(COLONCOLON);
				setState(2461);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2460);
					typeArguments();
					}
				}

				setState(2463);
				match(NEW);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2465);
				arrayType();
				setState(2466);
				match(COLONCOLON);
				setState(2467);
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

	@RuleVersion(0)
	public final ArrayCreationExpressionContext arrayCreationExpression() throws RecognitionException {
		ArrayCreationExpressionContext _localctx = new ArrayCreationExpressionContext(_ctx, getState());
		enterRule(_localctx, 420, RULE_arrayCreationExpression);
		try {
			setState(2493);
			switch ( getInterpreter().adaptivePredict(_input,274,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2471);
				match(NEW);
				setState(2472);
				primitiveType();
				setState(2473);
				dimExprs();
				setState(2475);
				switch ( getInterpreter().adaptivePredict(_input,272,_ctx) ) {
				case 1:
					{
					setState(2474);
					dims();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2477);
				match(NEW);
				setState(2478);
				classOrInterfaceType();
				setState(2479);
				dimExprs();
				setState(2481);
				switch ( getInterpreter().adaptivePredict(_input,273,_ctx) ) {
				case 1:
					{
					setState(2480);
					dims();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2483);
				match(NEW);
				setState(2484);
				primitiveType();
				setState(2485);
				dims();
				setState(2486);
				arrayInitializer();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2488);
				match(NEW);
				setState(2489);
				classOrInterfaceType();
				setState(2490);
				dims();
				setState(2491);
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
		public List<? extends DimExprContext> dimExpr() {
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

	@RuleVersion(0)
	public final DimExprsContext dimExprs() throws RecognitionException {
		DimExprsContext _localctx = new DimExprsContext(_ctx, getState());
		enterRule(_localctx, 422, RULE_dimExprs);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2495);
			dimExpr();
			setState(2499);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,275,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2496);
					dimExpr();
					}
					} 
				}
				setState(2501);
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<? extends AnnotationContext> annotation() {
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

	@RuleVersion(0)
	public final DimExprContext dimExpr() throws RecognitionException {
		DimExprContext _localctx = new DimExprContext(_ctx, getState());
		enterRule(_localctx, 424, RULE_dimExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2505);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(2502);
				annotation();
				}
				}
				setState(2507);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(2508);
			match(LBRACK);
			setState(2509);
			expression();
			setState(2510);
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

	@RuleVersion(0)
	public final ConstantExpressionContext constantExpression() throws RecognitionException {
		ConstantExpressionContext _localctx = new ConstantExpressionContext(_ctx, getState());
		enterRule(_localctx, 426, RULE_constantExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2512);
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

	@RuleVersion(0)
	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 428, RULE_expression);
		try {
			setState(2516);
			switch ( getInterpreter().adaptivePredict(_input,277,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2514);
				lambdaExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2515);
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

	@RuleVersion(0)
	public final LambdaExpressionContext lambdaExpression() throws RecognitionException {
		LambdaExpressionContext _localctx = new LambdaExpressionContext(_ctx, getState());
		enterRule(_localctx, 430, RULE_lambdaExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2518);
			lambdaParameters();
			setState(2519);
			match(ARROW);
			setState(2520);
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

	@RuleVersion(0)
	public final LambdaParametersContext lambdaParameters() throws RecognitionException {
		LambdaParametersContext _localctx = new LambdaParametersContext(_ctx, getState());
		enterRule(_localctx, 432, RULE_lambdaParameters);
		int _la;
		try {
			setState(2532);
			switch ( getInterpreter().adaptivePredict(_input,279,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2522);
				match(Identifier);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2523);
				match(LPAREN);
				setState(2525);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
					{
					setState(2524);
					formalParameterList();
					}
				}

				setState(2527);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2528);
				match(LPAREN);
				setState(2529);
				inferredFormalParameterList();
				setState(2530);
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
		public List<? extends TerminalNode> Identifier() { return getTokens(Java8Parser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(Java8Parser.Identifier, i);
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

	@RuleVersion(0)
	public final InferredFormalParameterListContext inferredFormalParameterList() throws RecognitionException {
		InferredFormalParameterListContext _localctx = new InferredFormalParameterListContext(_ctx, getState());
		enterRule(_localctx, 434, RULE_inferredFormalParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2534);
			match(Identifier);
			setState(2539);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2535);
				match(COMMA);
				setState(2536);
				match(Identifier);
				}
				}
				setState(2541);
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

	@RuleVersion(0)
	public final LambdaBodyContext lambdaBody() throws RecognitionException {
		LambdaBodyContext _localctx = new LambdaBodyContext(_ctx, getState());
		enterRule(_localctx, 436, RULE_lambdaBody);
		try {
			setState(2544);
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
				setState(2542);
				expression();
				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(2543);
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

	@RuleVersion(0)
	public final AssignmentExpressionContext assignmentExpression() throws RecognitionException {
		AssignmentExpressionContext _localctx = new AssignmentExpressionContext(_ctx, getState());
		enterRule(_localctx, 438, RULE_assignmentExpression);
		try {
			setState(2548);
			switch ( getInterpreter().adaptivePredict(_input,282,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2546);
				conditionalExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2547);
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

	@RuleVersion(0)
	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 440, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2550);
			leftHandSide();
			setState(2551);
			assignmentOperator();
			setState(2552);
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

	@RuleVersion(0)
	public final LeftHandSideContext leftHandSide() throws RecognitionException {
		LeftHandSideContext _localctx = new LeftHandSideContext(_ctx, getState());
		enterRule(_localctx, 442, RULE_leftHandSide);
		try {
			setState(2557);
			switch ( getInterpreter().adaptivePredict(_input,283,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2554);
				expressionName();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2555);
				fieldAccess();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2556);
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

	@RuleVersion(0)
	public final AssignmentOperatorContext assignmentOperator() throws RecognitionException {
		AssignmentOperatorContext _localctx = new AssignmentOperatorContext(_ctx, getState());
		enterRule(_localctx, 444, RULE_assignmentOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2559);
			_la = _input.LA(1);
			if ( !(((((_la - 67)) & ~0x3f) == 0 && ((1L << (_la - 67)) & ((1L << (ASSIGN - 67)) | (1L << (ADD_ASSIGN - 67)) | (1L << (SUB_ASSIGN - 67)) | (1L << (MUL_ASSIGN - 67)) | (1L << (DIV_ASSIGN - 67)) | (1L << (AND_ASSIGN - 67)) | (1L << (OR_ASSIGN - 67)) | (1L << (XOR_ASSIGN - 67)) | (1L << (MOD_ASSIGN - 67)) | (1L << (LSHIFT_ASSIGN - 67)) | (1L << (RSHIFT_ASSIGN - 67)) | (1L << (URSHIFT_ASSIGN - 67)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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

	@RuleVersion(0)
	public final AdditiveOperatorContext additiveOperator() throws RecognitionException {
		AdditiveOperatorContext _localctx = new AdditiveOperatorContext(_ctx, getState());
		enterRule(_localctx, 446, RULE_additiveOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2561);
			_la = _input.LA(1);
			if ( !(_la==ADD || _la==SUB) ) {
			_errHandler.recoverInline(this);
			} else {
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

	@RuleVersion(0)
	public final RelationalOperatorContext relationalOperator() throws RecognitionException {
		RelationalOperatorContext _localctx = new RelationalOperatorContext(_ctx, getState());
		enterRule(_localctx, 448, RULE_relationalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2563);
			_la = _input.LA(1);
			if ( !(((((_la - 27)) & ~0x3f) == 0 && ((1L << (_la - 27)) & ((1L << (INSTANCEOF - 27)) | (1L << (GT - 27)) | (1L << (LT - 27)) | (1L << (LE - 27)) | (1L << (GE - 27)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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

	@RuleVersion(0)
	public final MultiplicativeOperatorContext multiplicativeOperator() throws RecognitionException {
		MultiplicativeOperatorContext _localctx = new MultiplicativeOperatorContext(_ctx, getState());
		enterRule(_localctx, 450, RULE_multiplicativeOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2565);
			_la = _input.LA(1);
			if ( !(((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & ((1L << (MUL - 84)) | (1L << (DIV - 84)) | (1L << (MOD - 84)))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
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

	@RuleVersion(0)
	public final SquareBracketsContext squareBrackets() throws RecognitionException {
		SquareBracketsContext _localctx = new SquareBracketsContext(_ctx, getState());
		enterRule(_localctx, 452, RULE_squareBrackets);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2567);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
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
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
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

	@RuleVersion(0)
	public final ConditionalExpressionContext conditionalExpression() throws RecognitionException {
		ConditionalExpressionContext _localctx = new ConditionalExpressionContext(_ctx, getState());
		enterRule(_localctx, 454, RULE_conditionalExpression);
		try {
			setState(2582);
			switch ( getInterpreter().adaptivePredict(_input,284,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2569);
				conditionalOrExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2570);
				conditionalOrExpression(0);
				setState(2571);
				match(QUESTION);
				setState(2572);
				expression();
				setState(2573);
				match(COLON);
				setState(2574);
				conditionalExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2576);
				conditionalOrExpression(0);
				setState(2577);
				match(QUESTION);
				setState(2578);
				expression();
				setState(2579);
				match(COLON);
				setState(2580);
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

	@RuleVersion(0)
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
			setState(2585);
			conditionalAndExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2592);
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
					setState(2587);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2588);
					match(OR);
					setState(2589);
					conditionalAndExpression(0);
					}
					} 
				}
				setState(2594);
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

	@RuleVersion(0)
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
			setState(2596);
			inclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2603);
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
					setState(2598);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2599);
					match(AND);
					setState(2600);
					inclusiveOrExpression(0);
					}
					} 
				}
				setState(2605);
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

	@RuleVersion(0)
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
			setState(2607);
			exclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2614);
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
					setState(2609);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2610);
					match(BITOR);
					setState(2611);
					exclusiveOrExpression(0);
					}
					} 
				}
				setState(2616);
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

	@RuleVersion(0)
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
			setState(2618);
			andExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2625);
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
					setState(2620);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2621);
					match(CARET);
					setState(2622);
					andExpression(0);
					}
					} 
				}
				setState(2627);
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

	@RuleVersion(0)
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
			setState(2629);
			equalityExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2636);
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
					setState(2631);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2632);
					match(BITAND);
					setState(2633);
					equalityExpression(0);
					}
					} 
				}
				setState(2638);
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

	@RuleVersion(0)
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
			setState(2640);
			relationalExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2650);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,291,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2648);
					switch ( getInterpreter().adaptivePredict(_input,290,_ctx) ) {
					case 1:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2642);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2643);
						match(EQUAL);
						setState(2644);
						relationalExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2645);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2646);
						match(NOTEQUAL);
						setState(2647);
						relationalExpression(0);
						}
						break;
					}
					} 
				}
				setState(2652);
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

	@RuleVersion(0)
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
			setState(2654);
			shiftExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2678);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,293,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2676);
					switch ( getInterpreter().adaptivePredict(_input,292,_ctx) ) {
					case 1:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2656);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(2657);
						relationalOperator();
						setState(2658);
						shiftExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2660);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(2661);
						relationalOperator();
						setState(2662);
						shiftExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2664);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2665);
						relationalOperator();
						setState(2666);
						shiftExpression(0);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2668);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2669);
						relationalOperator();
						setState(2670);
						shiftExpression(0);
						}
						break;
					case 5:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2672);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2673);
						relationalOperator();
						setState(2674);
						referenceType();
						}
						break;
					}
					} 
				}
				setState(2680);
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

	@RuleVersion(0)
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
			setState(2682);
			additiveExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2698);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,295,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2696);
					switch ( getInterpreter().adaptivePredict(_input,294,_ctx) ) {
					case 1:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2684);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2685);
						shiftOperator();
						setState(2686);
						additiveExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2688);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2689);
						shiftOperator();
						setState(2690);
						additiveExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2692);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2693);
						shiftOperator();
						setState(2694);
						additiveExpression(0);
						}
						break;
					}
					} 
				}
				setState(2700);
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

	@RuleVersion(0)
	public final ShiftOperatorContext shiftOperator() throws RecognitionException {
		ShiftOperatorContext _localctx = new ShiftOperatorContext(_ctx, getState());
		enterRule(_localctx, 472, RULE_shiftOperator);
		try {
			setState(2708);
			switch ( getInterpreter().adaptivePredict(_input,296,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2701);
				match(LT);
				setState(2702);
				match(LT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2703);
				match(GT);
				setState(2704);
				match(GT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2705);
				match(GT);
				setState(2706);
				match(GT);
				setState(2707);
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

	@RuleVersion(0)
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
			setState(2711);
			multiplicativeExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2723);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,298,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2721);
					switch ( getInterpreter().adaptivePredict(_input,297,_ctx) ) {
					case 1:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2713);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2714);
						additiveOperator();
						setState(2715);
						multiplicativeExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2717);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2718);
						additiveOperator();
						setState(2719);
						multiplicativeExpression(0);
						}
						break;
					}
					} 
				}
				setState(2725);
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

	@RuleVersion(0)
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
			setState(2727);
			unaryExpression();
			}
			_ctx.stop = _input.LT(-1);
			setState(2743);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,300,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2741);
					switch ( getInterpreter().adaptivePredict(_input,299,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2729);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2730);
						multiplicativeOperator();
						setState(2731);
						unaryExpression();
						}
						break;
					case 2:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2733);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2734);
						multiplicativeOperator();
						setState(2735);
						unaryExpression();
						}
						break;
					case 3:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2737);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2738);
						multiplicativeOperator();
						setState(2739);
						unaryExpression();
						}
						break;
					}
					} 
				}
				setState(2745);
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

	@RuleVersion(0)
	public final UnaryExpressionContext unaryExpression() throws RecognitionException {
		UnaryExpressionContext _localctx = new UnaryExpressionContext(_ctx, getState());
		enterRule(_localctx, 478, RULE_unaryExpression);
		try {
			setState(2755);
			switch ( getInterpreter().adaptivePredict(_input,301,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2746);
				preIncrementExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2747);
				preDecrementExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2748);
				additiveOperator();
				setState(2749);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2751);
				additiveOperator();
				setState(2752);
				unaryExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2754);
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

	@RuleVersion(0)
	public final PreIncrementExpressionContext preIncrementExpression() throws RecognitionException {
		PreIncrementExpressionContext _localctx = new PreIncrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 480, RULE_preIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2757);
			match(INC);
			setState(2758);
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

	@RuleVersion(0)
	public final PreDecrementExpressionContext preDecrementExpression() throws RecognitionException {
		PreDecrementExpressionContext _localctx = new PreDecrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 482, RULE_preDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2760);
			match(DEC);
			setState(2761);
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
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
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

	@RuleVersion(0)
	public final UnaryExpressionNotPlusMinusContext unaryExpressionNotPlusMinus() throws RecognitionException {
		UnaryExpressionNotPlusMinusContext _localctx = new UnaryExpressionNotPlusMinusContext(_ctx, getState());
		enterRule(_localctx, 484, RULE_unaryExpressionNotPlusMinus);
		try {
			setState(2769);
			switch ( getInterpreter().adaptivePredict(_input,302,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2763);
				postfixExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2764);
				match(TILDE);
				setState(2765);
				unaryExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2766);
				match(BANG);
				setState(2767);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2768);
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
		public List<? extends PostIncrementExpression_lf_postfixExpressionContext> postIncrementExpression_lf_postfixExpression() {
			return getRuleContexts(PostIncrementExpression_lf_postfixExpressionContext.class);
		}
		public PostIncrementExpression_lf_postfixExpressionContext postIncrementExpression_lf_postfixExpression(int i) {
			return getRuleContext(PostIncrementExpression_lf_postfixExpressionContext.class,i);
		}
		public List<? extends PostDecrementExpression_lf_postfixExpressionContext> postDecrementExpression_lf_postfixExpression() {
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

	@RuleVersion(0)
	public final PostfixExpressionContext postfixExpression() throws RecognitionException {
		PostfixExpressionContext _localctx = new PostfixExpressionContext(_ctx, getState());
		enterRule(_localctx, 486, RULE_postfixExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2773);
			switch ( getInterpreter().adaptivePredict(_input,303,_ctx) ) {
			case 1:
				{
				setState(2771);
				primary();
				}
				break;
			case 2:
				{
				setState(2772);
				expressionName();
				}
				break;
			}
			setState(2779);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,305,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(2777);
					switch (_input.LA(1)) {
					case INC:
						{
						setState(2775);
						postIncrementExpression_lf_postfixExpression();
						}
						break;
					case DEC:
						{
						setState(2776);
						postDecrementExpression_lf_postfixExpression();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(2781);
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

	@RuleVersion(0)
	public final PostIncrementExpressionContext postIncrementExpression() throws RecognitionException {
		PostIncrementExpressionContext _localctx = new PostIncrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 488, RULE_postIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2782);
			postfixExpression();
			setState(2783);
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

	@RuleVersion(0)
	public final PostIncrementExpression_lf_postfixExpressionContext postIncrementExpression_lf_postfixExpression() throws RecognitionException {
		PostIncrementExpression_lf_postfixExpressionContext _localctx = new PostIncrementExpression_lf_postfixExpressionContext(_ctx, getState());
		enterRule(_localctx, 490, RULE_postIncrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2785);
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

	@RuleVersion(0)
	public final PostDecrementExpressionContext postDecrementExpression() throws RecognitionException {
		PostDecrementExpressionContext _localctx = new PostDecrementExpressionContext(_ctx, getState());
		enterRule(_localctx, 492, RULE_postDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2787);
			postfixExpression();
			setState(2788);
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

	@RuleVersion(0)
	public final PostDecrementExpression_lf_postfixExpressionContext postDecrementExpression_lf_postfixExpression() throws RecognitionException {
		PostDecrementExpression_lf_postfixExpressionContext _localctx = new PostDecrementExpression_lf_postfixExpressionContext(_ctx, getState());
		enterRule(_localctx, 494, RULE_postDecrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2790);
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
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public UnaryExpressionContext unaryExpression() {
			return getRuleContext(UnaryExpressionContext.class,0);
		}
		public ReferenceTypeContext referenceType() {
			return getRuleContext(ReferenceTypeContext.class,0);
		}
		public UnaryExpressionNotPlusMinusContext unaryExpressionNotPlusMinus() {
			return getRuleContext(UnaryExpressionNotPlusMinusContext.class,0);
		}
		public List<? extends AdditionalBoundContext> additionalBound() {
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

	@RuleVersion(0)
	public final CastExpressionContext castExpression() throws RecognitionException {
		CastExpressionContext _localctx = new CastExpressionContext(_ctx, getState());
		enterRule(_localctx, 496, RULE_castExpression);
		int _la;
		try {
			setState(2819);
			switch ( getInterpreter().adaptivePredict(_input,308,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2792);
				match(LPAREN);
				setState(2793);
				primitiveType();
				setState(2794);
				match(RPAREN);
				setState(2795);
				unaryExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2797);
				match(LPAREN);
				setState(2798);
				referenceType();
				setState(2802);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2799);
					additionalBound();
					}
					}
					setState(2804);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2805);
				match(RPAREN);
				setState(2806);
				unaryExpressionNotPlusMinus();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2808);
				match(LPAREN);
				setState(2809);
				referenceType();
				setState(2813);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2810);
					additionalBound();
					}
					}
					setState(2815);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2816);
				match(RPAREN);
				setState(2817);
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

	private static final int _serializedATNSegments = 2;
	private static final String _serializedATNSegment0 =
		"\3\uaf6f\u8320\u479d\ub75c\u4880\u1605\u191c\uab37\3o\u0b08\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv\4"+
		"w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t\u0080"+
		"\4\u0081\t\u0081\4\u0082\t\u0082\4\u0083\t\u0083\4\u0084\t\u0084\4\u0085"+
		"\t\u0085\4\u0086\t\u0086\4\u0087\t\u0087\4\u0088\t\u0088\4\u0089\t\u0089"+
		"\4\u008a\t\u008a\4\u008b\t\u008b\4\u008c\t\u008c\4\u008d\t\u008d\4\u008e"+
		"\t\u008e\4\u008f\t\u008f\4\u0090\t\u0090\4\u0091\t\u0091\4\u0092\t\u0092"+
		"\4\u0093\t\u0093\4\u0094\t\u0094\4\u0095\t\u0095\4\u0096\t\u0096\4\u0097"+
		"\t\u0097\4\u0098\t\u0098\4\u0099\t\u0099\4\u009a\t\u009a\4\u009b\t\u009b"+
		"\4\u009c\t\u009c\4\u009d\t\u009d\4\u009e\t\u009e\4\u009f\t\u009f\4\u00a0"+
		"\t\u00a0\4\u00a1\t\u00a1\4\u00a2\t\u00a2\4\u00a3\t\u00a3\4\u00a4\t\u00a4"+
		"\4\u00a5\t\u00a5\4\u00a6\t\u00a6\4\u00a7\t\u00a7\4\u00a8\t\u00a8\4\u00a9"+
		"\t\u00a9\4\u00aa\t\u00aa\4\u00ab\t\u00ab\4\u00ac\t\u00ac\4\u00ad\t\u00ad"+
		"\4\u00ae\t\u00ae\4\u00af\t\u00af\4\u00b0\t\u00b0\4\u00b1\t\u00b1\4\u00b2"+
		"\t\u00b2\4\u00b3\t\u00b3\4\u00b4\t\u00b4\4\u00b5\t\u00b5\4\u00b6\t\u00b6"+
		"\4\u00b7\t\u00b7\4\u00b8\t\u00b8\4\u00b9\t\u00b9\4\u00ba\t\u00ba\4\u00bb"+
		"\t\u00bb\4\u00bc\t\u00bc\4\u00bd\t\u00bd\4\u00be\t\u00be\4\u00bf\t\u00bf"+
		"\4\u00c0\t\u00c0\4\u00c1\t\u00c1\4\u00c2\t\u00c2\4\u00c3\t\u00c3\4\u00c4"+
		"\t\u00c4\4\u00c5\t\u00c5\4\u00c6\t\u00c6\4\u00c7\t\u00c7\4\u00c8\t\u00c8"+
		"\4\u00c9\t\u00c9\4\u00ca\t\u00ca\4\u00cb\t\u00cb\4\u00cc\t\u00cc\4\u00cd"+
		"\t\u00cd\4\u00ce\t\u00ce\4\u00cf\t\u00cf\4\u00d0\t\u00d0\4\u00d1\t\u00d1"+
		"\4\u00d2\t\u00d2\4\u00d3\t\u00d3\4\u00d4\t\u00d4\4\u00d5\t\u00d5\4\u00d6"+
		"\t\u00d6\4\u00d7\t\u00d7\4\u00d8\t\u00d8\4\u00d9\t\u00d9\4\u00da\t\u00da"+
		"\4\u00db\t\u00db\4\u00dc\t\u00dc\4\u00dd\t\u00dd\4\u00de\t\u00de\4\u00df"+
		"\t\u00df\4\u00e0\t\u00e0\4\u00e1\t\u00e1\4\u00e2\t\u00e2\4\u00e3\t\u00e3"+
		"\4\u00e4\t\u00e4\4\u00e5\t\u00e5\4\u00e6\t\u00e6\4\u00e7\t\u00e7\4\u00e8"+
		"\t\u00e8\4\u00e9\t\u00e9\4\u00ea\t\u00ea\4\u00eb\t\u00eb\4\u00ec\t\u00ec"+
		"\4\u00ed\t\u00ed\4\u00ee\t\u00ee\4\u00ef\t\u00ef\4\u00f0\t\u00f0\4\u00f1"+
		"\t\u00f1\4\u00f2\t\u00f2\4\u00f3\t\u00f3\4\u00f4\t\u00f4\4\u00f5\t\u00f5"+
		"\4\u00f6\t\u00f6\4\u00f7\t\u00f7\4\u00f8\t\u00f8\4\u00f9\t\u00f9\4\u00fa"+
		"\t\u00fa\3\2\3\2\3\3\3\3\5\3\u01f9\n\3\3\4\7\4\u01fc\n\4\f\4\16\4\u01ff"+
		"\13\4\3\4\3\4\7\4\u0203\n\4\f\4\16\4\u0206\13\4\3\4\5\4\u0209\n\4\3\5"+
		"\3\5\5\5\u020d\n\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\5\b\u0216\n\b\3\t\3\t\5"+
		"\t\u021a\n\t\3\t\3\t\7\t\u021e\n\t\f\t\16\t\u0221\13\t\3\n\3\n\5\n\u0225"+
		"\n\n\3\n\3\n\3\n\3\n\5\n\u022b\n\n\5\n\u022d\n\n\3\13\3\13\3\13\5\13\u0232"+
		"\n\13\3\f\3\f\5\f\u0236\n\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u0249\n\21\3\22\3\22\7\22"+
		"\u024d\n\22\f\22\16\22\u0250\13\22\3\23\7\23\u0253\n\23\f\23\16\23\u0256"+
		"\13\23\3\23\3\23\5\23\u025a\n\23\3\24\3\24\3\25\3\25\3\25\3\25\3\25\7"+
		"\25\u0263\n\25\f\25\16\25\u0266\13\25\5\25\u0268\n\25\3\26\3\26\3\26\3"+
		"\27\3\27\3\27\3\27\3\30\3\30\3\30\7\30\u0274\n\30\f\30\16\30\u0277\13"+
		"\30\3\31\3\31\5\31\u027b\n\31\3\32\7\32\u027e\n\32\f\32\16\32\u0281\13"+
		"\32\3\32\3\32\5\32\u0285\n\32\3\33\3\33\3\33\3\33\5\33\u028b\n\33\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\7\34\u0293\n\34\f\34\16\34\u0296\13\34\3\35"+
		"\3\35\3\35\3\35\3\35\5\35\u029d\n\35\3\36\3\36\3\36\3\36\3\36\3\36\7\36"+
		"\u02a5\n\36\f\36\16\36\u02a8\13\36\3\37\3\37\3\37\3\37\3\37\5\37\u02af"+
		"\n\37\3 \3 \3!\3!\3!\3!\3!\3!\7!\u02b9\n!\f!\16!\u02bc\13!\3\"\5\"\u02bf"+
		"\n\"\3\"\7\"\u02c2\n\"\f\"\16\"\u02c5\13\"\3\"\7\"\u02c8\n\"\f\"\16\""+
		"\u02cb\13\"\3\"\3\"\3#\7#\u02d0\n#\f#\16#\u02d3\13#\3#\3#\3#\3#\7#\u02d9"+
		"\n#\f#\16#\u02dc\13#\3#\3#\3$\3$\3%\3%\3%\3%\5%\u02e6\n%\3&\3&\3&\3&\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3*\3"+
		"*\3*\5*\u0303\n*\3+\3+\5+\u0307\n+\3,\3,\3,\3,\5,\u030d\n,\3,\5,\u0310"+
		"\n,\3,\5,\u0313\n,\3,\3,\3-\7-\u0318\n-\f-\16-\u031b\13-\3.\3.\3.\3.\3"+
		".\3.\3.\3.\5.\u0325\n.\3/\3/\3/\3/\3\60\3\60\3\60\7\60\u032e\n\60\f\60"+
		"\16\60\u0331\13\60\3\61\3\61\3\61\3\62\3\62\3\62\3\63\3\63\3\63\7\63\u033c"+
		"\n\63\f\63\16\63\u033f\13\63\3\64\3\64\7\64\u0343\n\64\f\64\16\64\u0346"+
		"\13\64\3\64\3\64\3\65\3\65\3\65\3\65\5\65\u034e\n\65\3\66\3\66\3\66\3"+
		"\66\3\66\5\66\u0355\n\66\3\67\3\67\3\67\3\67\3\67\38\78\u035d\n8\f8\16"+
		"8\u0360\138\39\39\39\39\39\39\39\39\59\u036a\n9\3:\3:\3:\7:\u036f\n:\f"+
		":\16:\u0372\13:\3;\3;\3;\5;\u0377\n;\3<\3<\5<\u037b\n<\3=\3=\5=\u037f"+
		"\n=\3>\3>\5>\u0383\n>\3?\3?\5?\u0387\n?\3@\3@\3@\5@\u038c\n@\3A\3A\5A"+
		"\u0390\nA\3A\3A\7A\u0394\nA\fA\16A\u0397\13A\3B\3B\5B\u039b\nB\3B\3B\3"+
		"B\3B\5B\u03a1\nB\5B\u03a3\nB\3C\3C\3C\5C\u03a8\nC\3D\3D\5D\u03ac\nD\3"+
		"E\3E\3F\3F\3G\3G\3H\3H\3I\3I\3I\3I\3I\3I\3I\3I\3I\5I\u03bf\nI\3J\3J\3"+
		"J\3J\3K\7K\u03c6\nK\fK\16K\u03c9\13K\3L\3L\3L\3L\3L\3L\3L\3L\3L\3L\5L"+
		"\u03d5\nL\3M\3M\3M\5M\u03da\nM\3M\3M\7M\u03de\nM\fM\16M\u03e1\13M\3M\3"+
		"M\3M\5M\u03e6\nM\5M\u03e8\nM\3N\3N\5N\u03ec\nN\3O\3O\3O\5O\u03f1\nO\3"+
		"O\3O\5O\u03f5\nO\3P\3P\3P\3P\3P\5P\u03fc\nP\3Q\3Q\3Q\7Q\u0401\nQ\fQ\16"+
		"Q\u0404\13Q\3Q\3Q\3Q\7Q\u0409\nQ\fQ\16Q\u040c\13Q\5Q\u040e\nQ\3R\7R\u0411"+
		"\nR\fR\16R\u0414\13R\3R\3R\3R\3S\3S\5S\u041b\nS\3T\7T\u041e\nT\fT\16T"+
		"\u0421\13T\3T\3T\7T\u0425\nT\fT\16T\u0428\13T\3T\3T\3T\3T\5T\u042e\nT"+
		"\3U\7U\u0431\nU\fU\16U\u0434\13U\3U\3U\3U\5U\u0439\nU\3U\3U\3V\3V\3V\3"+
		"W\3W\3W\7W\u0443\nW\fW\16W\u0446\13W\3X\3X\5X\u044a\nX\3Y\3Y\5Y\u044e"+
		"\nY\3Z\3Z\3[\3[\3[\3\\\3\\\3\\\5\\\u0458\n\\\3\\\3\\\3]\7]\u045d\n]\f"+
		"]\16]\u0460\13]\3^\3^\3^\3^\5^\u0466\n^\3_\5_\u0469\n_\3_\3_\3_\5_\u046e"+
		"\n_\3_\3_\3`\3`\3a\3a\5a\u0476\na\3a\5a\u0479\na\3a\3a\3b\5b\u047e\nb"+
		"\3b\3b\3b\5b\u0483\nb\3b\3b\3b\5b\u0488\nb\3b\3b\3b\5b\u048d\nb\3b\3b"+
		"\3b\3b\3b\5b\u0494\nb\3b\3b\3b\5b\u0499\nb\3b\3b\3b\3b\3b\3b\5b\u04a1"+
		"\nb\3b\3b\3b\5b\u04a6\nb\3b\3b\3b\5b\u04ab\nb\3c\3c\3c\3c\5c\u04b1\nc"+
		"\3c\3c\3d\3d\5d\u04b7\nd\3d\5d\u04ba\nd\3d\5d\u04bd\nd\3d\3d\3e\3e\3e"+
		"\7e\u04c4\ne\fe\16e\u04c7\13e\3f\7f\u04ca\nf\ff\16f\u04cd\13f\3f\3f\3"+
		"f\5f\u04d2\nf\3f\5f\u04d5\nf\3f\5f\u04d8\nf\3g\3g\3h\3h\7h\u04de\nh\f"+
		"h\16h\u04e1\13h\3i\3i\5i\u04e5\ni\3j\3j\3j\3j\5j\u04eb\nj\3j\5j\u04ee"+
		"\nj\3j\3j\3k\7k\u04f3\nk\fk\16k\u04f6\13k\3l\3l\3l\3l\3l\3l\3l\5l\u04ff"+
		"\nl\3m\3m\3m\3n\3n\7n\u0506\nn\fn\16n\u0509\13n\3n\3n\3o\3o\3o\3o\3o\5"+
		"o\u0512\no\3p\7p\u0515\np\fp\16p\u0518\13p\3p\3p\3p\3p\3q\3q\3q\3q\5q"+
		"\u0522\nq\3r\7r\u0525\nr\fr\16r\u0528\13r\3r\3r\3r\3s\3s\3s\3s\3s\3s\5"+
		"s\u0533\ns\3t\7t\u0536\nt\ft\16t\u0539\13t\3t\3t\3t\3t\3t\3u\3u\7u\u0542"+
		"\nu\fu\16u\u0545\13u\3u\3u\3v\3v\3v\3v\3v\5v\u054e\nv\3w\7w\u0551\nw\f"+
		"w\16w\u0554\13w\3w\3w\3w\3w\3w\5w\u055b\nw\3w\5w\u055e\nw\3w\3w\3x\3x"+
		"\3x\5x\u0565\nx\3y\3y\3y\3z\3z\3z\5z\u056d\nz\3{\7{\u0570\n{\f{\16{\u0573"+
		"\13{\3{\3{\3|\7|\u0578\n|\f|\16|\u057b\13|\3|\3|\3}\3}\3}\3}\5}\u0583"+
		"\n}\3}\3}\3~\3~\3~\7~\u058a\n~\f~\16~\u058d\13~\3\177\3\177\3\177\3\177"+
		"\3\u0080\3\u0080\3\u0080\5\u0080\u0596\n\u0080\3\u0081\3\u0081\5\u0081"+
		"\u059a\n\u0081\3\u0081\5\u0081\u059d\n\u0081\3\u0081\3\u0081\3\u0082\3"+
		"\u0082\3\u0082\7\u0082\u05a4\n\u0082\f\u0082\16\u0082\u05a7\13\u0082\3"+
		"\u0083\3\u0083\3\u0083\3\u0084\3\u0084\3\u0084\3\u0084\3\u0084\3\u0084"+
		"\3\u0085\3\u0085\5\u0085\u05b4\n\u0085\3\u0085\5\u0085\u05b7\n\u0085\3"+
		"\u0085\3\u0085\3\u0086\3\u0086\3\u0086\7\u0086\u05be\n\u0086\f\u0086\16"+
		"\u0086\u05c1\13\u0086\3\u0087\3\u0087\5\u0087\u05c5\n\u0087\3\u0087\3"+
		"\u0087\3\u0088\3\u0088\7\u0088\u05cb\n\u0088\f\u0088\16\u0088\u05ce\13"+
		"\u0088\3\u0089\3\u0089\3\u0089\5\u0089\u05d3\n\u0089\3\u008a\3\u008a\3"+
		"\u008a\3\u008b\7\u008b\u05d9\n\u008b\f\u008b\16\u008b\u05dc\13\u008b\3"+
		"\u008b\3\u008b\3\u008b\3\u008c\3\u008c\3\u008c\3\u008c\3\u008c\3\u008c"+
		"\5\u008c\u05e7\n\u008c\3\u008d\3\u008d\3\u008d\3\u008d\3\u008d\5\u008d"+
		"\u05ee\n\u008d\3\u008e\3\u008e\3\u008e\3\u008e\3\u008e\3\u008e\3\u008e"+
		"\3\u008e\3\u008e\3\u008e\3\u008e\3\u008e\5\u008e\u05fc\n\u008e\3\u008f"+
		"\3\u008f\3\u0090\3\u0090\3\u0090\3\u0090\3\u0091\3\u0091\3\u0091\3\u0091"+
		"\3\u0092\3\u0092\3\u0092\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093"+
		"\3\u0093\5\u0093\u0612\n\u0093\3\u0094\3\u0094\3\u0094\3\u0094\3\u0094"+
		"\3\u0094\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095\3\u0095"+
		"\3\u0096\3\u0096\3\u0096\3\u0096\3\u0096\3\u0096\3\u0096\3\u0096\3\u0097"+
		"\3\u0097\3\u0097\3\u0097\3\u0097\3\u0097\3\u0097\3\u0097\3\u0097\3\u0097"+
		"\5\u0097\u0634\n\u0097\3\u0098\3\u0098\3\u0098\3\u0098\3\u0098\3\u0098"+
		"\3\u0099\3\u0099\7\u0099\u063e\n\u0099\f\u0099\16\u0099\u0641\13\u0099"+
		"\3\u0099\7\u0099\u0644\n\u0099\f\u0099\16\u0099\u0647\13\u0099\3\u0099"+
		"\3\u0099\3\u009a\3\u009a\3\u009a\3\u009b\3\u009b\7\u009b\u0650\n\u009b"+
		"\f\u009b\16\u009b\u0653\13\u009b\3\u009c\3\u009c\3\u009c\3\u009c\3\u009c"+
		"\3\u009c\3\u009c\3\u009c\3\u009c\3\u009c\5\u009c\u065f\n\u009c\3\u009d"+
		"\3\u009d\3\u009e\3\u009e\3\u009e\3\u009e\3\u009e\3\u009e\3\u009f\3\u009f"+
		"\3\u009f\3\u009f\3\u009f\3\u009f\3\u00a0\3\u00a0\3\u00a0\3\u00a0\3\u00a0"+
		"\3\u00a0\3\u00a0\3\u00a0\3\u00a1\3\u00a1\5\u00a1\u0679\n\u00a1\3\u00a2"+
		"\3\u00a2\5\u00a2\u067d\n\u00a2\3\u00a3\3\u00a3\3\u00a3\5\u00a3\u0682\n"+
		"\u00a3\3\u00a3\3\u00a3\5\u00a3\u0686\n\u00a3\3\u00a3\3\u00a3\5\u00a3\u068a"+
		"\n\u00a3\3\u00a3\3\u00a3\3\u00a3\3\u00a4\3\u00a4\3\u00a4\5\u00a4\u0692"+
		"\n\u00a4\3\u00a4\3\u00a4\5\u00a4\u0696\n\u00a4\3\u00a4\3\u00a4\5\u00a4"+
		"\u069a\n\u00a4\3\u00a4\3\u00a4\3\u00a4\3\u00a5\3\u00a5\5\u00a5\u06a1\n"+
		"\u00a5\3\u00a6\3\u00a6\3\u00a7\3\u00a7\3\u00a7\7\u00a7\u06a8\n\u00a7\f"+
		"\u00a7\16\u00a7\u06ab\13\u00a7\3\u00a8\3\u00a8\3\u00a8\7\u00a8\u06b0\n"+
		"\u00a8\f\u00a8\16\u00a8\u06b3\13\u00a8\3\u00a8\3\u00a8\3\u00a8\3\u00a8"+
		"\3\u00a8\3\u00a8\3\u00a8\3\u00a9\3\u00a9\3\u00a9\7\u00a9\u06bf\n\u00a9"+
		"\f\u00a9\16\u00a9\u06c2\13\u00a9\3\u00a9\3\u00a9\3\u00a9\3\u00a9\3\u00a9"+
		"\3\u00a9\3\u00a9\3\u00aa\3\u00aa\5\u00aa\u06cd\n\u00aa\3\u00aa\3\u00aa"+
		"\3\u00ab\3\u00ab\5\u00ab\u06d3\n\u00ab\3\u00ab\3\u00ab\3\u00ac\3\u00ac"+
		"\5\u00ac\u06d9\n\u00ac\3\u00ac\3\u00ac\3\u00ad\3\u00ad\3\u00ad\3\u00ad"+
		"\3\u00ae\3\u00ae\3\u00ae\3\u00ae\3\u00ae\3\u00ae\3\u00af\3\u00af\3\u00af"+
		"\3\u00af\3\u00af\3\u00af\3\u00af\5\u00af\u06ee\n\u00af\3\u00af\3\u00af"+
		"\3\u00af\5\u00af\u06f3\n\u00af\3\u00b0\3\u00b0\7\u00b0\u06f7\n\u00b0\f"+
		"\u00b0\16\u00b0\u06fa\13\u00b0\3\u00b1\3\u00b1\3\u00b1\3\u00b1\3\u00b1"+
		"\3\u00b1\3\u00b2\7\u00b2\u0703\n\u00b2\f\u00b2\16\u00b2\u0706\13\u00b2"+
		"\3\u00b2\3\u00b2\3\u00b2\3\u00b3\3\u00b3\3\u00b3\7\u00b3\u070e\n\u00b3"+
		"\f\u00b3\16\u00b3\u0711\13\u00b3\3\u00b4\3\u00b4\3\u00b4\3\u00b5\3\u00b5"+
		"\3\u00b5\3\u00b5\5\u00b5\u071a\n\u00b5\3\u00b5\5\u00b5\u071d\n\u00b5\3"+
		"\u00b6\3\u00b6\3\u00b6\5\u00b6\u0722\n\u00b6\3\u00b6\3\u00b6\3\u00b7\3"+
		"\u00b7\3\u00b7\7\u00b7\u0729\n\u00b7\f\u00b7\16\u00b7\u072c\13\u00b7\3"+
		"\u00b8\7\u00b8\u072f\n\u00b8\f\u00b8\16\u00b8\u0732\13\u00b8\3\u00b8\3"+
		"\u00b8\3\u00b8\3\u00b8\3\u00b8\3\u00b9\3\u00b9\5\u00b9\u073b\n\u00b9\3"+
		"\u00b9\7\u00b9\u073e\n\u00b9\f\u00b9\16\u00b9\u0741\13\u00b9\3\u00ba\3"+
		"\u00ba\3\u00ba\7\u00ba\u0746\n\u00ba\f\u00ba\16\u00ba\u0749\13\u00ba\3"+
		"\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba"+
		"\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba\3\u00ba"+
		"\3\u00ba\3\u00ba\5\u00ba\u075f\n\u00ba\3\u00bb\3\u00bb\3\u00bc\3\u00bc"+
		"\3\u00bc\7\u00bc\u0766\n\u00bc\f\u00bc\16\u00bc\u0769\13\u00bc\3\u00bc"+
		"\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc"+
		"\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc\3\u00bc"+
		"\5\u00bc\u077e\n\u00bc\3\u00bd\3\u00bd\3\u00bd\3\u00bd\3\u00bd\5\u00bd"+
		"\u0785\n\u00bd\3\u00be\3\u00be\3\u00bf\3\u00bf\3\u00bf\3\u00bf\5\u00bf"+
		"\u078d\n\u00bf\3\u00c0\3\u00c0\3\u00c0\7\u00c0\u0792\n\u00c0\f\u00c0\16"+
		"\u00c0\u0795\13\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\7\u00c0"+
		"\u079c\n\u00c0\f\u00c0\16\u00c0\u079f\13\u00c0\3\u00c0\3\u00c0\3\u00c0"+
		"\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0"+
		"\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\5\u00c0"+
		"\u07b5\n\u00c0\3\u00c1\3\u00c1\3\u00c2\3\u00c2\3\u00c2\7\u00c2\u07bc\n"+
		"\u00c2\f\u00c2\16\u00c2\u07bf\13\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2"+
		"\3\u00c2\7\u00c2\u07c6\n\u00c2\f\u00c2\16\u00c2\u07c9\13\u00c2\3\u00c2"+
		"\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2"+
		"\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2"+
		"\5\u00c2\u07de\n\u00c2\3\u00c3\3\u00c3\5\u00c3\u07e2\n\u00c3\3\u00c3\3"+
		"\u00c3\3\u00c3\7\u00c3\u07e7\n\u00c3\f\u00c3\16\u00c3\u07ea\13\u00c3\3"+
		"\u00c3\5\u00c3\u07ed\n\u00c3\3\u00c3\3\u00c3\5\u00c3\u07f1\n\u00c3\3\u00c3"+
		"\3\u00c3\5\u00c3\u07f5\n\u00c3\3\u00c3\3\u00c3\3\u00c3\3\u00c3\5\u00c3"+
		"\u07fb\n\u00c3\3\u00c3\3\u00c3\5\u00c3\u07ff\n\u00c3\3\u00c3\3\u00c3\5"+
		"\u00c3\u0803\n\u00c3\3\u00c3\3\u00c3\5\u00c3\u0807\n\u00c3\3\u00c3\3\u00c3"+
		"\3\u00c3\3\u00c3\5\u00c3\u080d\n\u00c3\3\u00c3\3\u00c3\5\u00c3\u0811\n"+
		"\u00c3\3\u00c3\3\u00c3\5\u00c3\u0815\n\u00c3\3\u00c3\3\u00c3\5\u00c3\u0819"+
		"\n\u00c3\5\u00c3\u081b\n\u00c3\3\u00c4\3\u00c4\3\u00c4\5\u00c4\u0820\n"+
		"\u00c4\3\u00c4\3\u00c4\5\u00c4\u0824\n\u00c4\3\u00c4\3\u00c4\5\u00c4\u0828"+
		"\n\u00c4\3\u00c4\3\u00c4\5\u00c4\u082c\n\u00c4\3\u00c5\3\u00c5\5\u00c5"+
		"\u0830\n\u00c5\3\u00c5\3\u00c5\3\u00c5\7\u00c5\u0835\n\u00c5\f\u00c5\16"+
		"\u00c5\u0838\13\u00c5\3\u00c5\5\u00c5\u083b\n\u00c5\3\u00c5\3\u00c5\5"+
		"\u00c5\u083f\n\u00c5\3\u00c5\3\u00c5\5\u00c5\u0843\n\u00c5\3\u00c5\3\u00c5"+
		"\3\u00c5\3\u00c5\5\u00c5\u0849\n\u00c5\3\u00c5\3\u00c5\5\u00c5\u084d\n"+
		"\u00c5\3\u00c5\3\u00c5\5\u00c5\u0851\n\u00c5\3\u00c5\3\u00c5\5\u00c5\u0855"+
		"\n\u00c5\5\u00c5\u0857\n\u00c5\3\u00c6\3\u00c6\3\u00c6\5\u00c6\u085c\n"+
		"\u00c6\3\u00c7\3\u00c7\3\u00c7\3\u00c7\3\u00c7\3\u00c7\3\u00c7\3\u00c7"+
		"\3\u00c7\3\u00c7\3\u00c7\3\u00c7\3\u00c7\5\u00c7\u086b\n\u00c7\3\u00c8"+
		"\3\u00c8\3\u00c8\3\u00c9\3\u00c9\3\u00c9\3\u00c9\3\u00c9\3\u00c9\3\u00c9"+
		"\3\u00c9\3\u00c9\5\u00c9\u0879\n\u00c9\3\u00ca\3\u00ca\3\u00ca\3\u00ca"+
		"\3\u00ca\3\u00ca\3\u00ca\3\u00ca\3\u00ca\3\u00ca\5\u00ca\u0885\n\u00ca"+
		"\3\u00ca\3\u00ca\3\u00ca\3\u00ca\3\u00ca\7\u00ca\u088c\n\u00ca\f\u00ca"+
		"\16\u00ca\u088f\13\u00ca\3\u00cb\3\u00cb\3\u00cb\3\u00cb\3\u00cb\3\u00cb"+
		"\3\u00cb\3\u00cb\3\u00cb\3\u00cb\7\u00cb\u089b\n\u00cb\f\u00cb\16\u00cb"+
		"\u089e\13\u00cb\3\u00cc\3\u00cc\3\u00cc\3\u00cc\3\u00cc\3\u00cc\3\u00cc"+
		"\3\u00cc\3\u00cc\3\u00cc\5\u00cc\u08aa\n\u00cc\3\u00cc\3\u00cc\3\u00cc"+
		"\3\u00cc\3\u00cc\7\u00cc\u08b1\n\u00cc\f\u00cc\16\u00cc\u08b4\13\u00cc"+
		"\3\u00cd\3\u00cd\3\u00cd\5\u00cd\u08b9\n\u00cd\3\u00cd\3\u00cd\3\u00cd"+
		"\3\u00cd\3\u00cd\5\u00cd\u08c0\n\u00cd\3\u00cd\3\u00cd\3\u00cd\5\u00cd"+
		"\u08c5\n\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\5\u00cd\u08cc\n"+
		"\u00cd\3\u00cd\3\u00cd\3\u00cd\5\u00cd\u08d1\n\u00cd\3\u00cd\3\u00cd\3"+
		"\u00cd\3\u00cd\3\u00cd\5\u00cd\u08d8\n\u00cd\3\u00cd\3\u00cd\3\u00cd\5"+
		"\u00cd\u08dd\n\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\5\u00cd\u08e4"+
		"\n\u00cd\3\u00cd\3\u00cd\3\u00cd\5\u00cd\u08e9\n\u00cd\3\u00cd\3\u00cd"+
		"\3\u00cd\3\u00cd\3\u00cd\3\u00cd\5\u00cd\u08f1\n\u00cd\3\u00cd\3\u00cd"+
		"\3\u00cd\5\u00cd\u08f6\n\u00cd\3\u00cd\3\u00cd\5\u00cd\u08fa\n\u00cd\3"+
		"\u00ce\3\u00ce\5\u00ce\u08fe\n\u00ce\3\u00ce\3\u00ce\3\u00ce\5\u00ce\u0903"+
		"\n\u00ce\3\u00ce\3\u00ce\3\u00cf\3\u00cf\3\u00cf\5\u00cf\u090a\n\u00cf"+
		"\3\u00cf\3\u00cf\3\u00cf\3\u00cf\3\u00cf\5\u00cf\u0911\n\u00cf\3\u00cf"+
		"\3\u00cf\3\u00cf\5\u00cf\u0916\n\u00cf\3\u00cf\3\u00cf\3\u00cf\3\u00cf"+
		"\3\u00cf\5\u00cf\u091d\n\u00cf\3\u00cf\3\u00cf\3\u00cf\5\u00cf\u0922\n"+
		"\u00cf\3\u00cf\3\u00cf\3\u00cf\3\u00cf\3\u00cf\5\u00cf\u0929\n\u00cf\3"+
		"\u00cf\3\u00cf\3\u00cf\5\u00cf\u092e\n\u00cf\3\u00cf\3\u00cf\3\u00cf\3"+
		"\u00cf\3\u00cf\3\u00cf\5\u00cf\u0936\n\u00cf\3\u00cf\3\u00cf\3\u00cf\5"+
		"\u00cf\u093b\n\u00cf\3\u00cf\3\u00cf\5\u00cf\u093f\n\u00cf\3\u00d0\3\u00d0"+
		"\3\u00d0\7\u00d0\u0944\n\u00d0\f\u00d0\16\u00d0\u0947\13\u00d0\3\u00d1"+
		"\3\u00d1\3\u00d1\5\u00d1\u094c\n\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1"+
		"\3\u00d1\5\u00d1\u0953\n\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1"+
		"\5\u00d1\u095a\n\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\5\u00d1"+
		"\u0961\n\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\5\u00d1"+
		"\u0969\n\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\5\u00d1\u0970\n"+
		"\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\5\u00d1\u0978\n"+
		"\u00d1\3\u00d2\3\u00d2\5\u00d2\u097c\n\u00d2\3\u00d2\3\u00d2\3\u00d3\3"+
		"\u00d3\3\u00d3\5\u00d3\u0983\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3"+
		"\u00d3\5\u00d3\u098a\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\5"+
		"\u00d3\u0991\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\5"+
		"\u00d3\u0999\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u09a0"+
		"\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u09a8"+
		"\n\u00d3\3\u00d4\3\u00d4\3\u00d4\3\u00d4\5\u00d4\u09ae\n\u00d4\3\u00d4"+
		"\3\u00d4\3\u00d4\3\u00d4\5\u00d4\u09b4\n\u00d4\3\u00d4\3\u00d4\3\u00d4"+
		"\3\u00d4\3\u00d4\3\u00d4\3\u00d4\3\u00d4\3\u00d4\3\u00d4\5\u00d4\u09c0"+
		"\n\u00d4\3\u00d5\3\u00d5\7\u00d5\u09c4\n\u00d5\f\u00d5\16\u00d5\u09c7"+
		"\13\u00d5\3\u00d6\7\u00d6\u09ca\n\u00d6\f\u00d6\16\u00d6\u09cd\13\u00d6"+
		"\3\u00d6\3\u00d6\3\u00d6\3\u00d6\3\u00d7\3\u00d7\3\u00d8\3\u00d8\5\u00d8"+
		"\u09d7\n\u00d8\3\u00d9\3\u00d9\3\u00d9\3\u00d9\3\u00da\3\u00da\3\u00da"+
		"\5\u00da\u09e0\n\u00da\3\u00da\3\u00da\3\u00da\3\u00da\3\u00da\5\u00da"+
		"\u09e7\n\u00da\3\u00db\3\u00db\3\u00db\7\u00db\u09ec\n\u00db\f\u00db\16"+
		"\u00db\u09ef\13\u00db\3\u00dc\3\u00dc\5\u00dc\u09f3\n\u00dc\3\u00dd\3"+
		"\u00dd\5\u00dd\u09f7\n\u00dd\3\u00de\3\u00de\3\u00de\3\u00de\3\u00df\3"+
		"\u00df\3\u00df\5\u00df\u0a00\n\u00df\3\u00e0\3\u00e0\3\u00e1\3\u00e1\3"+
		"\u00e2\3\u00e2\3\u00e3\3\u00e3\3\u00e4\3\u00e4\3\u00e5\3\u00e5\3\u00e5"+
		"\3\u00e5\3\u00e5\3\u00e5\3\u00e5\3\u00e5\3\u00e5\3\u00e5\3\u00e5\3\u00e5"+
		"\3\u00e5\5\u00e5\u0a19\n\u00e5\3\u00e6\3\u00e6\3\u00e6\3\u00e6\3\u00e6"+
		"\3\u00e6\7\u00e6\u0a21\n\u00e6\f\u00e6\16\u00e6\u0a24\13\u00e6\3\u00e7"+
		"\3\u00e7\3\u00e7\3\u00e7\3\u00e7\3\u00e7\7\u00e7\u0a2c\n\u00e7\f\u00e7"+
		"\16\u00e7\u0a2f\13\u00e7\3\u00e8\3\u00e8\3\u00e8\3\u00e8\3\u00e8\3\u00e8"+
		"\7\u00e8\u0a37\n\u00e8\f\u00e8\16\u00e8\u0a3a\13\u00e8\3\u00e9\3\u00e9"+
		"\3\u00e9\3\u00e9\3\u00e9\3\u00e9\7\u00e9\u0a42\n\u00e9\f\u00e9\16\u00e9"+
		"\u0a45\13\u00e9\3\u00ea\3\u00ea\3\u00ea\3\u00ea\3\u00ea\3\u00ea\7\u00ea"+
		"\u0a4d\n\u00ea\f\u00ea\16\u00ea\u0a50\13\u00ea\3\u00eb\3\u00eb\3\u00eb"+
		"\3\u00eb\3\u00eb\3\u00eb\3\u00eb\3\u00eb\3\u00eb\7\u00eb\u0a5b\n\u00eb"+
		"\f\u00eb\16\u00eb\u0a5e\13\u00eb\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec"+
		"\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec"+
		"\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec"+
		"\7\u00ec\u0a77\n\u00ec\f\u00ec\16\u00ec\u0a7a\13\u00ec\3\u00ed\3\u00ed"+
		"\3\u00ed\3\u00ed\3\u00ed\3\u00ed\3\u00ed\3\u00ed\3\u00ed\3\u00ed\3\u00ed"+
		"\3\u00ed\3\u00ed\3\u00ed\3\u00ed\7\u00ed\u0a8b\n\u00ed\f\u00ed\16\u00ed"+
		"\u0a8e\13\u00ed\3\u00ee\3\u00ee\3\u00ee\3\u00ee\3\u00ee\3\u00ee\3\u00ee"+
		"\5\u00ee\u0a97\n\u00ee\3\u00ef\3\u00ef\3\u00ef\3\u00ef\3\u00ef\3\u00ef"+
		"\3\u00ef\3\u00ef\3\u00ef\3\u00ef\3\u00ef\7\u00ef\u0aa4\n\u00ef\f\u00ef"+
		"\16\u00ef\u0aa7\13\u00ef\3\u00f0\3\u00f0\3\u00f0\3\u00f0\3\u00f0\3\u00f0"+
		"\3\u00f0\3\u00f0\3\u00f0\3\u00f0\3\u00f0\3\u00f0\3\u00f0\3\u00f0\3\u00f0"+
		"\7\u00f0\u0ab8\n\u00f0\f\u00f0\16\u00f0\u0abb\13\u00f0\3\u00f1\3\u00f1"+
		"\3\u00f1\3\u00f1\3\u00f1\3\u00f1\3\u00f1\3\u00f1\3\u00f1\5\u00f1\u0ac6"+
		"\n\u00f1\3\u00f2\3\u00f2\3\u00f2\3\u00f3\3\u00f3\3\u00f3\3\u00f4\3\u00f4"+
		"\3\u00f4\3\u00f4\3\u00f4\3\u00f4\5\u00f4\u0ad4\n\u00f4\3\u00f5\3\u00f5"+
		"\5\u00f5\u0ad8\n\u00f5\3\u00f5\3\u00f5\7\u00f5\u0adc\n\u00f5\f\u00f5\16"+
		"\u00f5\u0adf\13\u00f5\3\u00f6\3\u00f6\3\u00f6\3\u00f7\3\u00f7\3\u00f8"+
		"\3\u00f8\3\u00f8\3\u00f9\3\u00f9\3\u00fa\3\u00fa\3\u00fa\3\u00fa\3\u00fa"+
		"\3\u00fa\3\u00fa\3\u00fa\7\u00fa\u0af3\n\u00fa\f\u00fa\16\u00fa\u0af6"+
		"\13\u00fa\3\u00fa\3\u00fa\3\u00fa\3\u00fa\3\u00fa\3\u00fa\7\u00fa\u0afe"+
		"\n\u00fa\f\u00fa\16\u00fa\u0b01\13\u00fa\3\u00fa\3\u00fa\3\u00fa\5\u00fa"+
		"\u0b06\n\u00fa\3\u00fa\2\2\17\66:@\u01ca\u01cc\u01ce\u01d0\u01d2\u01d4"+
		"\u01d6\u01d8\u01dc\u01de\u00fb\2\2\4\2\6\2\b\2\n\2\f\2\16\2\20\2\22\2"+
		"\24\2\26\2\30\2\32\2\34\2\36\2 \2\"\2$\2&\2(\2*\2,\2.\2\60\2\62\2\64\2"+
		"\66\28\2:\2<\2>\2@\2B\2D\2F\2H\2J\2L\2N\2P\2R\2T\2V\2X\2Z\2\\\2^\2`\2"+
		"b\2d\2f\2h\2j\2l\2n\2p\2r\2t\2v\2x\2z\2|\2~\2\u0080\2\u0082\2\u0084\2"+
		"\u0086\2\u0088\2\u008a\2\u008c\2\u008e\2\u0090\2\u0092\2\u0094\2\u0096"+
		"\2\u0098\2\u009a\2\u009c\2\u009e\2\u00a0\2\u00a2\2\u00a4\2\u00a6\2\u00a8"+
		"\2\u00aa\2\u00ac\2\u00ae\2\u00b0\2\u00b2\2\u00b4\2\u00b6\2\u00b8\2\u00ba"+
		"\2\u00bc\2\u00be\2\u00c0\2\u00c2\2\u00c4\2\u00c6\2\u00c8\2\u00ca\2\u00cc"+
		"\2\u00ce\2\u00d0\2\u00d2\2\u00d4\2\u00d6\2\u00d8\2\u00da\2\u00dc\2\u00de"+
		"\2\u00e0\2\u00e2\2\u00e4\2\u00e6\2\u00e8\2\u00ea\2\u00ec\2\u00ee\2\u00f0"+
		"\2\u00f2\2\u00f4\2\u00f6\2\u00f8\2\u00fa\2\u00fc\2\u00fe\2\u0100\2\u0102"+
		"\2\u0104\2\u0106\2\u0108\2\u010a\2\u010c\2\u010e\2\u0110\2\u0112\2\u0114"+
		"\2\u0116\2\u0118\2\u011a\2\u011c\2\u011e\2\u0120\2\u0122\2\u0124\2\u0126"+
		"\2\u0128\2\u012a\2\u012c\2\u012e\2\u0130\2\u0132\2\u0134\2\u0136\2\u0138"+
		"\2\u013a\2\u013c\2\u013e\2\u0140\2\u0142\2\u0144\2\u0146\2\u0148\2\u014a"+
		"\2\u014c\2\u014e\2\u0150\2\u0152\2\u0154\2\u0156\2\u0158\2\u015a\2\u015c"+
		"\2\u015e\2\u0160\2\u0162\2\u0164\2\u0166\2\u0168\2\u016a\2\u016c\2\u016e"+
		"\2\u0170\2\u0172\2\u0174\2\u0176\2\u0178\2\u017a\2\u017c\2\u017e\2\u0180"+
		"\2\u0182\2\u0184\2\u0186\2\u0188\2\u018a\2\u018c\2\u018e\2\u0190\2\u0192"+
		"\2\u0194\2\u0196\2\u0198\2\u019a\2\u019c\2\u019e\2\u01a0\2\u01a2\2\u01a4"+
		"\2\u01a6\2\u01a8\2\u01aa\2\u01ac\2\u01ae\2\u01b0\2\u01b2\2\u01b4\2\u01b6"+
		"\2\u01b8\2\u01ba\2\u01bc\2\u01be\2\u01c0\2\u01c2\2\u01c4\2\u01c6\2\u01c8"+
		"\2\u01ca\2\u01cc\2\u01ce\2\u01d0\2\u01d2\2\u01d4\2\u01d6\2\u01d8\2\u01da"+
		"\2\u01dc\2\u01de\2\u01e0\2\u01e2\2\u01e4\2\u01e6\2\u01e8\2\u01ea\2\u01ec"+
		"\2\u01ee\2\u01f0\2\u01f2\2\2\t\3\2\66;\7\2\b\b\13\13\36\36  ((\4\2\21"+
		"\21\27\27\4\2EE^h\3\2TU\5\2\35\35FGMN\4\2VW[[\u0be0\2\u01f4\3\2\2\2\4"+
		"\u01f8\3\2\2\2\6\u0208\3\2\2\2\b\u020c\3\2\2\2\n\u020e\3\2\2\2\f\u0210"+
		"\3\2\2\2\16\u0215\3\2\2\2\20\u0219\3\2\2\2\22\u022c\3\2\2\2\24\u022e\3"+
		"\2\2\2\26\u0233\3\2\2\2\30\u0237\3\2\2\2\32\u0239\3\2\2\2\34\u023b\3\2"+
		"\2\2\36\u023d\3\2\2\2 \u0248\3\2\2\2\"\u024a\3\2\2\2$\u0254\3\2\2\2&\u025b"+
		"\3\2\2\2(\u0267\3\2\2\2*\u0269\3\2\2\2,\u026c\3\2\2\2.\u0270\3\2\2\2\60"+
		"\u027a\3\2\2\2\62\u027f\3\2\2\2\64\u028a\3\2\2\2\66\u028c\3\2\2\28\u029c"+
		"\3\2\2\2:\u029e\3\2\2\2<\u02ae\3\2\2\2>\u02b0\3\2\2\2@\u02b2\3\2\2\2B"+
		"\u02be\3\2\2\2D\u02d1\3\2\2\2F\u02df\3\2\2\2H\u02e5\3\2\2\2J\u02e7\3\2"+
		"\2\2L\u02eb\3\2\2\2N\u02f1\3\2\2\2P\u02f8\3\2\2\2R\u0302\3\2\2\2T\u0306"+
		"\3\2\2\2V\u0308\3\2\2\2X\u0319\3\2\2\2Z\u0324\3\2\2\2\\\u0326\3\2\2\2"+
		"^\u032a\3\2\2\2`\u0332\3\2\2\2b\u0335\3\2\2\2d\u0338\3\2\2\2f\u0340\3"+
		"\2\2\2h\u034d\3\2\2\2j\u0354\3\2\2\2l\u0356\3\2\2\2n\u035e\3\2\2\2p\u0369"+
		"\3\2\2\2r\u036b\3\2\2\2t\u0373\3\2\2\2v\u0378\3\2\2\2x\u037e\3\2\2\2z"+
		"\u0382\3\2\2\2|\u0386\3\2\2\2~\u038b\3\2\2\2\u0080\u038f\3\2\2\2\u0082"+
		"\u03a2\3\2\2\2\u0084\u03a4\3\2\2\2\u0086\u03a9\3\2\2\2\u0088\u03ad\3\2"+
		"\2\2\u008a\u03af\3\2\2\2\u008c\u03b1\3\2\2\2\u008e\u03b3\3\2\2\2\u0090"+
		"\u03be\3\2\2\2\u0092\u03c0\3\2\2\2\u0094\u03c7\3\2\2\2\u0096\u03d4\3\2"+
		"\2\2\u0098\u03e7\3\2\2\2\u009a\u03eb\3\2\2\2\u009c\u03ed\3\2\2\2\u009e"+
		"\u03fb\3\2\2\2\u00a0\u040d\3\2\2\2\u00a2\u0412\3\2\2\2\u00a4\u041a\3\2"+
		"\2\2\u00a6\u042d\3\2\2\2\u00a8\u0432\3\2\2\2\u00aa\u043c\3\2\2\2\u00ac"+
		"\u043f\3\2\2\2\u00ae\u0449\3\2\2\2\u00b0\u044d\3\2\2\2\u00b2\u044f\3\2"+
		"\2\2\u00b4\u0451\3\2\2\2\u00b6\u0454\3\2\2\2\u00b8\u045e\3\2\2\2\u00ba"+
		"\u0465\3\2\2\2\u00bc\u0468\3\2\2\2\u00be\u0471\3\2\2\2\u00c0\u0473\3\2"+
		"\2\2\u00c2\u04aa\3\2\2\2\u00c4\u04ac\3\2\2\2\u00c6\u04b4\3\2\2\2\u00c8"+
		"\u04c0\3\2\2\2\u00ca\u04cb\3\2\2\2\u00cc\u04d9\3\2\2\2\u00ce\u04db\3\2"+
		"\2\2\u00d0\u04e4\3\2\2\2\u00d2\u04e6\3\2\2\2\u00d4\u04f4\3\2\2\2\u00d6"+
		"\u04fe\3\2\2\2\u00d8\u0500\3\2\2\2\u00da\u0503\3\2\2\2\u00dc\u0511\3\2"+
		"\2\2\u00de\u0516\3\2\2\2\u00e0\u0521\3\2\2\2\u00e2\u0526\3\2\2\2\u00e4"+
		"\u0532\3\2\2\2\u00e6\u0537\3\2\2\2\u00e8\u053f\3\2\2\2\u00ea\u054d\3\2"+
		"\2\2\u00ec\u0552\3\2\2\2\u00ee\u0564\3\2\2\2\u00f0\u0566\3\2\2\2\u00f2"+
		"\u056c\3\2\2\2\u00f4\u0571\3\2\2\2\u00f6\u0579\3\2\2\2\u00f8\u057e\3\2"+
		"\2\2\u00fa\u0586\3\2\2\2\u00fc\u058e\3\2\2\2\u00fe\u0595\3\2\2\2\u0100"+
		"\u0597\3\2\2\2\u0102\u05a0\3\2\2\2\u0104\u05a8\3\2\2\2\u0106\u05ab\3\2"+
		"\2\2\u0108\u05b1\3\2\2\2\u010a\u05ba\3\2\2\2\u010c\u05c2\3\2\2\2\u010e"+
		"\u05c8\3\2\2\2\u0110\u05d2\3\2\2\2\u0112\u05d4\3\2\2\2\u0114\u05da\3\2"+
		"\2\2\u0116\u05e6\3\2\2\2\u0118\u05ed\3\2\2\2\u011a\u05fb\3\2\2\2\u011c"+
		"\u05fd\3\2\2\2\u011e\u05ff\3\2\2\2\u0120\u0603\3\2\2\2\u0122\u0607\3\2"+
		"\2\2\u0124\u0611\3\2\2\2\u0126\u0613\3\2\2\2\u0128\u0619\3\2\2\2\u012a"+
		"\u0621\3\2\2\2\u012c\u0633\3\2\2\2\u012e\u0635\3\2\2\2\u0130\u063b\3\2"+
		"\2\2\u0132\u064a\3\2\2\2\u0134\u064d\3\2\2\2\u0136\u065e\3\2\2\2\u0138"+
		"\u0660\3\2\2\2\u013a\u0662\3\2\2\2\u013c\u0668\3\2\2\2\u013e\u066e\3\2"+
		"\2\2\u0140\u0678\3\2\2\2\u0142\u067c\3\2\2\2\u0144\u067e\3\2\2\2\u0146"+
		"\u068e\3\2\2\2\u0148\u06a0\3\2\2\2\u014a\u06a2\3\2\2\2\u014c\u06a4\3\2"+
		"\2\2\u014e\u06ac\3\2\2\2\u0150\u06bb\3\2\2\2\u0152\u06ca\3\2\2\2\u0154"+
		"\u06d0\3\2\2\2\u0156\u06d6\3\2\2\2\u0158\u06dc\3\2\2\2\u015a\u06e0\3\2"+
		"\2\2\u015c\u06f2\3\2\2\2\u015e\u06f4\3\2\2\2\u0160\u06fb\3\2\2\2\u0162"+
		"\u0704\3\2\2\2\u0164\u070a\3\2\2\2\u0166\u0712\3\2\2\2\u0168\u0715\3\2"+
		"\2\2\u016a\u071e\3\2\2\2\u016c\u0725\3\2\2\2\u016e\u0730\3\2\2\2\u0170"+
		"\u073a\3\2\2\2\u0172\u075e\3\2\2\2\u0174\u0760\3\2\2\2\u0176\u077d\3\2"+
		"\2\2\u0178\u0784\3\2\2\2\u017a\u0786\3\2\2\2\u017c\u078c\3\2\2\2\u017e"+
		"\u07b4\3\2\2\2\u0180\u07b6\3\2\2\2\u0182\u07dd\3\2\2\2\u0184\u081a\3\2"+
		"\2\2\u0186\u081c\3\2\2\2\u0188\u0856\3\2\2\2\u018a\u085b\3\2\2\2\u018c"+
		"\u086a\3\2\2\2\u018e\u086c\3\2\2\2\u0190\u0878\3\2\2\2\u0192\u0884\3\2"+
		"\2\2\u0194\u0890\3\2\2\2\u0196\u08a9\3\2\2\2\u0198\u08f9\3\2\2\2\u019a"+
		"\u08fb\3\2\2\2\u019c\u093e\3\2\2\2\u019e\u0940\3\2\2\2\u01a0\u0977\3\2"+
		"\2\2\u01a2\u0979\3\2\2\2\u01a4\u09a7\3\2\2\2\u01a6\u09bf\3\2\2\2\u01a8"+
		"\u09c1\3\2\2\2\u01aa\u09cb\3\2\2\2\u01ac\u09d2\3\2\2\2\u01ae\u09d6\3\2"+
		"\2\2\u01b0\u09d8\3\2\2\2\u01b2\u09e6\3\2\2\2\u01b4\u09e8\3\2\2\2\u01b6"+
		"\u09f2\3\2\2\2\u01b8\u09f6\3\2\2\2\u01ba\u09f8\3\2\2\2\u01bc\u09ff\3\2"+
		"\2\2\u01be\u0a01\3\2\2\2\u01c0\u0a03\3\2\2\2\u01c2\u0a05\3\2\2\2\u01c4"+
		"\u0a07\3\2\2\2\u01c6\u0a09\3\2\2\2\u01c8\u0a18\3\2\2\2\u01ca\u0a1a\3\2"+
		"\2\2\u01cc\u0a25\3\2\2\2\u01ce\u0a30\3\2\2\2\u01d0\u0a3b\3\2\2\2\u01d2"+
		"\u0a46\3\2\2\2\u01d4\u0a51\3\2\2\2\u01d6\u0a5f\3\2\2\2\u01d8\u0a7b\3\2"+
		"\2\2\u01da\u0a96\3\2\2\2\u01dc\u0a98\3\2\2\2\u01de\u0aa8\3\2\2\2\u01e0"+
		"\u0ac5\3\2\2\2\u01e2\u0ac7\3\2\2\2\u01e4\u0aca\3\2\2\2\u01e6\u0ad3\3\2"+
		"\2\2\u01e8\u0ad7\3\2\2\2\u01ea\u0ae0\3\2\2\2\u01ec\u0ae3\3\2\2\2\u01ee"+
		"\u0ae5\3\2\2\2\u01f0\u0ae8\3\2\2\2\u01f2\u0b05\3\2\2\2\u01f4\u01f5\t\2"+
		"\2\2\u01f5\3\3\2\2\2\u01f6\u01f9\5\6\4\2\u01f7\u01f9\5\16\b\2\u01f8\u01f6"+
		"\3\2\2\2\u01f8\u01f7\3\2\2\2\u01f9\5\3\2\2\2\u01fa\u01fc\5\u00f2z\2\u01fb"+
		"\u01fa\3\2\2\2\u01fc\u01ff\3\2\2\2\u01fd\u01fb\3\2\2\2\u01fd\u01fe\3\2"+
		"\2\2\u01fe\u0200\3\2\2\2\u01ff\u01fd\3\2\2\2\u0200\u0209\5\b\5\2\u0201"+
		"\u0203\5\u00f2z\2\u0202\u0201\3\2\2\2\u0203\u0206\3\2\2\2\u0204\u0202"+
		"\3\2\2\2\u0204\u0205\3\2\2\2\u0205\u0207\3\2\2\2\u0206\u0204\3\2\2\2\u0207"+
		"\u0209\7\6\2\2\u0208\u01fd\3\2\2\2\u0208\u0204\3\2\2\2\u0209\7\3\2\2\2"+
		"\u020a\u020d\5\n\6\2\u020b\u020d\5\f\7\2\u020c\u020a\3\2\2\2\u020c\u020b"+
		"\3\2\2\2\u020d\t\3\2\2\2\u020e\u020f\t\3\2\2\u020f\13\3\2\2\2\u0210\u0211"+
		"\t\4\2\2\u0211\r\3\2\2\2\u0212\u0216\5\20\t\2\u0213\u0216\5\36\20\2\u0214"+
		"\u0216\5 \21\2\u0215\u0212\3\2\2\2\u0215\u0213\3\2\2\2\u0215\u0214\3\2"+
		"\2\2\u0216\17\3\2\2\2\u0217\u021a\5\26\f\2\u0218\u021a\5\34\17\2\u0219"+
		"\u0217\3\2\2\2\u0219\u0218\3\2\2\2\u021a\u021f\3\2\2\2\u021b\u021e\5\24"+
		"\13\2\u021c\u021e\5\32\16\2\u021d\u021b\3\2\2\2\u021d\u021c\3\2\2\2\u021e"+
		"\u0221\3\2\2\2\u021f\u021d\3\2\2\2\u021f\u0220\3\2\2\2\u0220\21\3\2\2"+
		"\2\u0221\u021f\3\2\2\2\u0222\u0224\5\u00f4{\2\u0223\u0225\5,\27\2\u0224"+
		"\u0223\3\2\2\2\u0224\u0225\3\2\2\2\u0225\u022d\3\2\2\2\u0226\u0227\5\20"+
		"\t\2\u0227\u0228\7D\2\2\u0228\u022a\5\u00f4{\2\u0229\u022b\5,\27\2\u022a"+
		"\u0229\3\2\2\2\u022a\u022b\3\2\2\2\u022b\u022d\3\2\2\2\u022c\u0222\3\2"+
		"\2\2\u022c\u0226\3\2\2\2\u022d\23\3\2\2\2\u022e\u022f\7D\2\2\u022f\u0231"+
		"\5\u00f4{\2\u0230\u0232\5,\27\2\u0231\u0230\3\2\2\2\u0231\u0232\3\2\2"+
		"\2\u0232\25\3\2\2\2\u0233\u0235\5\u00f4{\2\u0234\u0236\5,\27\2\u0235\u0234"+
		"\3\2\2\2\u0235\u0236\3\2\2\2\u0236\27\3\2\2\2\u0237\u0238\5\22\n\2\u0238"+
		"\31\3\2\2\2\u0239\u023a\5\24\13\2\u023a\33\3\2\2\2\u023b\u023c\5\26\f"+
		"\2\u023c\35\3\2\2\2\u023d\u023e\5\u00f4{\2\u023e\37\3\2\2\2\u023f\u0240"+
		"\5\6\4\2\u0240\u0241\5\"\22\2\u0241\u0249\3\2\2\2\u0242\u0243\5\20\t\2"+
		"\u0243\u0244\5\"\22\2\u0244\u0249\3\2\2\2\u0245\u0246\5\36\20\2\u0246"+
		"\u0247\5\"\22\2\u0247\u0249\3\2\2\2\u0248\u023f\3\2\2\2\u0248\u0242\3"+
		"\2\2\2\u0248\u0245\3\2\2\2\u0249!\3\2\2\2\u024a\u024e\5\u00f6|\2\u024b"+
		"\u024d\5\u00f6|\2\u024c\u024b\3\2\2\2\u024d\u0250\3\2\2\2\u024e\u024c"+
		"\3\2\2\2\u024e\u024f\3\2\2\2\u024f#\3\2\2\2\u0250\u024e\3\2\2\2\u0251"+
		"\u0253\5&\24\2\u0252\u0251\3\2\2\2\u0253\u0256\3\2\2\2\u0254\u0252\3\2"+
		"\2\2\u0254\u0255\3\2\2\2\u0255\u0257\3\2\2\2\u0256\u0254\3\2\2\2\u0257"+
		"\u0259\7i\2\2\u0258\u025a\5(\25\2\u0259\u0258\3\2\2\2\u0259\u025a\3\2"+
		"\2\2\u025a%\3\2\2\2\u025b\u025c\5\u00f2z\2\u025c\'\3\2\2\2\u025d\u025e"+
		"\7\24\2\2\u025e\u0268\5\36\20\2\u025f\u0260\7\24\2\2\u0260\u0264\5\20"+
		"\t\2\u0261\u0263\5*\26\2\u0262\u0261\3\2\2\2\u0263\u0266\3\2\2\2\u0264"+
		"\u0262\3\2\2\2\u0264\u0265\3\2\2\2\u0265\u0268\3\2\2\2\u0266\u0264\3\2"+
		"\2\2\u0267\u025d\3\2\2\2\u0267\u025f\3\2\2\2\u0268)\3\2\2\2\u0269\u026a"+
		"\7X\2\2\u026a\u026b\5\30\r\2\u026b+\3\2\2\2\u026c\u026d\7G\2\2\u026d\u026e"+
		"\5.\30\2\u026e\u026f\7F\2\2\u026f-\3\2\2\2\u0270\u0275\5\60\31\2\u0271"+
		"\u0272\7C\2\2\u0272\u0274\5\60\31\2\u0273\u0271\3\2\2\2\u0274\u0277\3"+
		"\2\2\2\u0275\u0273\3\2\2\2\u0275\u0276\3\2\2\2\u0276/\3\2\2\2\u0277\u0275"+
		"\3\2\2\2\u0278\u027b\5\16\b\2\u0279\u027b\5\62\32\2\u027a\u0278\3\2\2"+
		"\2\u027a\u0279\3\2\2\2\u027b\61\3\2\2\2\u027c\u027e\5\u00f2z\2\u027d\u027c"+
		"\3\2\2\2\u027e\u0281\3\2\2\2\u027f\u027d\3\2\2\2\u027f\u0280\3\2\2\2\u0280"+
		"\u0282\3\2\2\2\u0281\u027f\3\2\2\2\u0282\u0284\7J\2\2\u0283\u0285\5\64"+
		"\33\2\u0284\u0283\3\2\2\2\u0284\u0285\3\2\2\2\u0285\63\3\2\2\2\u0286\u0287"+
		"\7\24\2\2\u0287\u028b\5\16\b\2\u0288\u0289\7+\2\2\u0289\u028b\5\16\b\2"+
		"\u028a\u0286\3\2\2\2\u028a\u0288\3\2\2\2\u028b\65\3\2\2\2\u028c\u028d"+
		"\b\34\1\2\u028d\u028e\7i\2\2\u028e\u0294\3\2\2\2\u028f\u0290\f\3\2\2\u0290"+
		"\u0291\7D\2\2\u0291\u0293\7i\2\2\u0292\u028f\3\2\2\2\u0293\u0296\3\2\2"+
		"\2\u0294\u0292\3\2\2\2\u0294\u0295\3\2\2\2\u0295\67\3\2\2\2\u0296\u0294"+
		"\3\2\2\2\u0297\u029d\7i\2\2\u0298\u0299\5:\36\2\u0299\u029a\7D\2\2\u029a"+
		"\u029b\7i\2\2\u029b\u029d\3\2\2\2\u029c\u0297\3\2\2\2\u029c\u0298\3\2"+
		"\2\2\u029d9\3\2\2\2\u029e\u029f\b\36\1\2\u029f\u02a0\7i\2\2\u02a0\u02a6"+
		"\3\2\2\2\u02a1\u02a2\f\3\2\2\u02a2\u02a3\7D\2\2\u02a3\u02a5\7i\2\2\u02a4"+
		"\u02a1\3\2\2\2\u02a5\u02a8\3\2\2\2\u02a6\u02a4\3\2\2\2\u02a6\u02a7\3\2"+
		"\2\2\u02a7;\3\2\2\2\u02a8\u02a6\3\2\2\2\u02a9\u02af\7i\2\2\u02aa\u02ab"+
		"\5@!\2\u02ab\u02ac\7D\2\2\u02ac\u02ad\7i\2\2\u02ad\u02af\3\2\2\2\u02ae"+
		"\u02a9\3\2\2\2\u02ae\u02aa\3\2\2\2\u02af=\3\2\2\2\u02b0\u02b1\7i\2\2\u02b1"+
		"?\3\2\2\2\u02b2\u02b3\b!\1\2\u02b3\u02b4\7i\2\2\u02b4\u02ba\3\2\2\2\u02b5"+
		"\u02b6\f\3\2\2\u02b6\u02b7\7D\2\2\u02b7\u02b9\7i\2\2\u02b8\u02b5\3\2\2"+
		"\2\u02b9\u02bc\3\2\2\2\u02ba\u02b8\3\2\2\2\u02ba\u02bb\3\2\2\2\u02bbA"+
		"\3\2\2\2\u02bc\u02ba\3\2\2\2\u02bd\u02bf\5D#\2\u02be\u02bd\3\2\2\2\u02be"+
		"\u02bf\3\2\2\2\u02bf\u02c3\3\2\2\2\u02c0\u02c2\5H%\2\u02c1\u02c0\3\2\2"+
		"\2\u02c2\u02c5\3\2\2\2\u02c3\u02c1\3\2\2\2\u02c3\u02c4\3\2\2\2\u02c4\u02c9"+
		"\3\2\2\2\u02c5\u02c3\3\2\2\2\u02c6\u02c8\5R*\2\u02c7\u02c6\3\2\2\2\u02c8"+
		"\u02cb\3\2\2\2\u02c9\u02c7\3\2\2\2\u02c9\u02ca\3\2\2\2\u02ca\u02cc\3\2"+
		"\2\2\u02cb\u02c9\3\2\2\2\u02cc\u02cd\7\2\2\3\u02cdC\3\2\2\2\u02ce\u02d0"+
		"\5F$\2\u02cf\u02ce\3\2\2\2\u02d0\u02d3\3\2\2\2\u02d1\u02cf\3\2\2\2\u02d1"+
		"\u02d2\3\2\2\2\u02d2\u02d4\3\2\2\2\u02d3\u02d1\3\2\2\2\u02d4\u02d5\7#"+
		"\2\2\u02d5\u02da\7i\2\2\u02d6\u02d7\7D\2\2\u02d7\u02d9\7i\2\2\u02d8\u02d6"+
		"\3\2\2\2\u02d9\u02dc\3\2\2\2\u02da\u02d8\3\2\2\2\u02da\u02db\3\2\2\2\u02db"+
		"\u02dd\3\2\2\2\u02dc\u02da\3\2\2\2\u02dd\u02de\7B\2\2\u02deE\3\2\2\2\u02df"+
		"\u02e0\5\u00f2z\2\u02e0G\3\2\2\2\u02e1\u02e6\5J&\2\u02e2\u02e6\5L\'\2"+
		"\u02e3\u02e6\5N(\2\u02e4\u02e6\5P)\2\u02e5\u02e1\3\2\2\2\u02e5\u02e2\3"+
		"\2\2\2\u02e5\u02e3\3\2\2\2\u02e5\u02e4\3\2\2\2\u02e6I\3\2\2\2\u02e7\u02e8"+
		"\7\34\2\2\u02e8\u02e9\58\35\2\u02e9\u02ea\7B\2\2\u02eaK\3\2\2\2\u02eb"+
		"\u02ec\7\34\2\2\u02ec\u02ed\5:\36\2\u02ed\u02ee\7D\2\2\u02ee\u02ef\7V"+
		"\2\2\u02ef\u02f0\7B\2\2\u02f0M\3\2\2\2\u02f1\u02f2\7\34\2\2\u02f2\u02f3"+
		"\7)\2\2\u02f3\u02f4\58\35\2\u02f4\u02f5\7D\2\2\u02f5\u02f6\7i\2\2\u02f6"+
		"\u02f7\7B\2\2\u02f7O\3\2\2\2\u02f8\u02f9\7\34\2\2\u02f9\u02fa\7)\2\2\u02fa"+
		"\u02fb\58\35\2\u02fb\u02fc\7D\2\2\u02fc\u02fd\7V\2\2\u02fd\u02fe\7B\2"+
		"\2\u02feQ\3\2\2\2\u02ff\u0303\5T+\2\u0300\u0303\5\u00d0i\2\u0301\u0303"+
		"\7B\2\2\u0302\u02ff\3\2\2\2\u0302\u0300\3\2\2\2\u0302\u0301\3\2\2\2\u0303"+
		"S\3\2\2\2\u0304\u0307\5V,\2\u0305\u0307\5\u00c4c\2\u0306\u0304\3\2\2\2"+
		"\u0306\u0305\3\2\2\2\u0307U\3\2\2\2\u0308\u0309\5X-\2\u0309\u030a\7\f"+
		"\2\2\u030a\u030c\7i\2\2\u030b\u030d\5\\/\2\u030c\u030b\3\2\2\2\u030c\u030d"+
		"\3\2\2\2\u030d\u030f\3\2\2\2\u030e\u0310\5`\61\2\u030f\u030e\3\2\2\2\u030f"+
		"\u0310\3\2\2\2\u0310\u0312\3\2\2\2\u0311\u0313\5b\62\2\u0312\u0311\3\2"+
		"\2\2\u0312\u0313\3\2\2\2\u0313\u0314\3\2\2\2\u0314\u0315\5f\64\2\u0315"+
		"W\3\2\2\2\u0316\u0318\5Z.\2\u0317\u0316\3\2\2\2\u0318\u031b\3\2\2\2\u0319"+
		"\u0317\3\2\2\2\u0319\u031a\3\2\2\2\u031aY\3\2\2\2\u031b\u0319\3\2\2\2"+
		"\u031c\u0325\5\u00f2z\2\u031d\u0325\7&\2\2\u031e\u0325\7%\2\2\u031f\u0325"+
		"\7$\2\2\u0320\u0325\7\4\2\2\u0321\u0325\7)\2\2\u0322\u0325\7\25\2\2\u0323"+
		"\u0325\7*\2\2\u0324\u031c\3\2\2\2\u0324\u031d\3\2\2\2\u0324\u031e\3\2"+
		"\2\2\u0324\u031f\3\2\2\2\u0324\u0320\3\2\2\2\u0324\u0321\3\2\2\2\u0324"+
		"\u0322\3\2\2\2\u0324\u0323\3\2\2\2\u0325[\3\2\2\2\u0326\u0327\7G\2\2\u0327"+
		"\u0328\5^\60\2\u0328\u0329\7F\2\2\u0329]\3\2\2\2\u032a\u032f\5$\23\2\u032b"+
		"\u032c\7C\2\2\u032c\u032e\5$\23\2\u032d\u032b\3\2\2\2\u032e\u0331\3\2"+
		"\2\2\u032f\u032d\3\2\2\2\u032f\u0330\3\2\2\2\u0330_\3\2\2\2\u0331\u032f"+
		"\3\2\2\2\u0332\u0333\7\24\2\2\u0333\u0334\5\22\n\2\u0334a\3\2\2\2\u0335"+
		"\u0336\7\33\2\2\u0336\u0337\5d\63\2\u0337c\3\2\2\2\u0338\u033d\5\30\r"+
		"\2\u0339\u033a\7C\2\2\u033a\u033c\5\30\r\2\u033b\u0339\3\2\2\2\u033c\u033f"+
		"\3\2\2\2\u033d\u033b\3\2\2\2\u033d\u033e\3\2\2\2\u033ee\3\2\2\2\u033f"+
		"\u033d\3\2\2\2\u0340\u0344\7>\2\2\u0341\u0343\5h\65\2\u0342\u0341\3\2"+
		"\2\2\u0343\u0346\3\2\2\2\u0344\u0342\3\2\2\2\u0344\u0345\3\2\2\2\u0345"+
		"\u0347\3\2\2\2\u0346\u0344\3\2\2\2\u0347\u0348\7?\2\2\u0348g\3\2\2\2\u0349"+
		"\u034e\5j\66\2\u034a\u034e\5\u00b2Z\2\u034b\u034e\5\u00b4[\2\u034c\u034e"+
		"\5\u00b6\\\2\u034d\u0349\3\2\2\2\u034d\u034a\3\2\2\2\u034d\u034b\3\2\2"+
		"\2\u034d\u034c\3\2\2\2\u034ei\3\2\2\2\u034f\u0355\5l\67\2\u0350\u0355"+
		"\5\u0092J\2\u0351\u0355\5T+\2\u0352\u0355\5\u00d0i\2\u0353\u0355\7B\2"+
		"\2\u0354\u034f\3\2\2\2\u0354\u0350\3\2\2\2\u0354\u0351\3\2\2\2\u0354\u0352"+
		"\3\2\2\2\u0354\u0353\3\2\2\2\u0355k\3\2\2\2\u0356\u0357\5n8\2\u0357\u0358"+
		"\5z>\2\u0358\u0359\5r:\2\u0359\u035a\7B\2\2\u035am\3\2\2\2\u035b\u035d"+
		"\5p9\2\u035c\u035b\3\2\2\2\u035d\u0360\3\2\2\2\u035e\u035c\3\2\2\2\u035e"+
		"\u035f\3\2\2\2\u035fo\3\2\2\2\u0360\u035e\3\2\2\2\u0361\u036a\5\u00f2"+
		"z\2\u0362\u036a\7&\2\2\u0363\u036a\7%\2\2\u0364\u036a\7$\2\2\u0365\u036a"+
		"\7)\2\2\u0366\u036a\7\25\2\2\u0367\u036a\7\61\2\2\u0368\u036a\7\64\2\2"+
		"\u0369\u0361\3\2\2\2\u0369\u0362\3\2\2\2\u0369\u0363\3\2\2\2\u0369\u0364"+
		"\3\2\2\2\u0369\u0365\3\2\2\2\u0369\u0366\3\2\2\2\u0369\u0367\3\2\2\2\u0369"+
		"\u0368\3\2\2\2\u036aq\3\2\2\2\u036b\u0370\5t;\2\u036c\u036d\7C\2\2\u036d"+
		"\u036f\5t;\2\u036e\u036c\3\2\2\2\u036f\u0372\3\2\2\2\u0370\u036e\3\2\2"+
		"\2\u0370\u0371\3\2\2\2\u0371s\3\2\2\2\u0372\u0370\3\2\2\2\u0373\u0376"+
		"\5v<\2\u0374\u0375\7E\2\2\u0375\u0377\5x=\2\u0376\u0374\3\2\2\2\u0376"+
		"\u0377\3\2\2\2\u0377u\3\2\2\2\u0378\u037a\7i\2\2\u0379\u037b\5\"\22\2"+
		"\u037a\u0379\3\2\2\2\u037a\u037b\3\2\2\2\u037bw\3\2\2\2\u037c\u037f\5"+
		"\u01ae\u00d8\2\u037d\u037f\5\u0108\u0085\2\u037e\u037c\3\2\2\2\u037e\u037d"+
		"\3\2\2\2\u037fy\3\2\2\2\u0380\u0383\5|?\2\u0381\u0383\5~@\2\u0382\u0380"+
		"\3\2\2\2\u0382\u0381\3\2\2\2\u0383{\3\2\2\2\u0384\u0387\5\b\5\2\u0385"+
		"\u0387\7\6\2\2\u0386\u0384\3\2\2\2\u0386\u0385\3\2\2\2\u0387}\3\2\2\2"+
		"\u0388\u038c\5\u0080A\2\u0389\u038c\5\u008eH\2\u038a\u038c\5\u0090I\2"+
		"\u038b\u0388\3\2\2\2\u038b\u0389\3\2\2\2\u038b\u038a\3\2\2\2\u038c\177"+
		"\3\2\2\2\u038d\u0390\5\u0086D\2\u038e\u0390\5\u008cG\2\u038f\u038d\3\2"+
		"\2\2\u038f\u038e\3\2\2\2\u0390\u0395\3\2\2\2\u0391\u0394\5\u0084C\2\u0392"+
		"\u0394\5\u008aF\2\u0393\u0391\3\2\2\2\u0393\u0392\3\2\2\2\u0394\u0397"+
		"\3\2\2\2\u0395\u0393\3\2\2\2\u0395\u0396\3\2\2\2\u0396\u0081\3\2\2\2\u0397"+
		"\u0395\3\2\2\2\u0398\u039a\7i\2\2\u0399\u039b\5,\27\2\u039a\u0399\3\2"+
		"\2\2\u039a\u039b\3\2\2\2\u039b\u03a3\3\2\2\2\u039c\u039d\5\u0080A\2\u039d"+
		"\u039e\7D\2\2\u039e\u03a0\5\u00f4{\2\u039f\u03a1\5,\27\2\u03a0\u039f\3"+
		"\2\2\2\u03a0\u03a1\3\2\2\2\u03a1\u03a3\3\2\2\2\u03a2\u0398\3\2\2\2\u03a2"+
		"\u039c\3\2\2\2\u03a3\u0083\3\2\2\2\u03a4\u03a5\7D\2\2\u03a5\u03a7\5\u00f4"+
		"{\2\u03a6\u03a8\5,\27\2\u03a7\u03a6\3\2\2\2\u03a7\u03a8\3\2\2\2\u03a8"+
		"\u0085\3\2\2\2\u03a9\u03ab\7i\2\2\u03aa\u03ac\5,\27\2\u03ab\u03aa\3\2"+
		"\2\2\u03ab\u03ac\3\2\2\2\u03ac\u0087\3\2\2\2\u03ad\u03ae\5\u0082B\2\u03ae"+
		"\u0089\3\2\2\2\u03af\u03b0\5\u0084C\2\u03b0\u008b\3\2\2\2\u03b1\u03b2"+
		"\5\u0086D\2\u03b2\u008d\3\2\2\2\u03b3\u03b4\7i\2\2\u03b4\u008f\3\2\2\2"+
		"\u03b5\u03b6\5|?\2\u03b6\u03b7\5\"\22\2\u03b7\u03bf\3\2\2\2\u03b8\u03b9"+
		"\5\u0080A\2\u03b9\u03ba\5\"\22\2\u03ba\u03bf\3\2\2\2\u03bb\u03bc\5\u008e"+
		"H\2\u03bc\u03bd\5\"\22\2\u03bd\u03bf\3\2\2\2\u03be\u03b5\3\2\2\2\u03be"+
		"\u03b8\3\2\2\2\u03be\u03bb\3\2\2\2\u03bf\u0091\3\2\2\2\u03c0\u03c1\5\u0094"+
		"K\2\u03c1\u03c2\5\u0098M\2\u03c2\u03c3\5\u00b0Y\2\u03c3\u0093\3\2\2\2"+
		"\u03c4\u03c6\5\u0096L\2\u03c5\u03c4\3\2\2\2\u03c6\u03c9\3\2\2\2\u03c7"+
		"\u03c5\3\2\2\2\u03c7\u03c8\3\2\2\2\u03c8\u0095\3\2\2\2\u03c9\u03c7\3\2"+
		"\2\2\u03ca\u03d5\5\u00f2z\2\u03cb\u03d5\7&\2\2\u03cc\u03d5\7%\2\2\u03cd"+
		"\u03d5\7$\2\2\u03ce\u03d5\7\4\2\2\u03cf\u03d5\7)\2\2\u03d0\u03d5\7\25"+
		"\2\2\u03d1\u03d5\7-\2\2\u03d2\u03d5\7!\2\2\u03d3\u03d5\7*\2\2\u03d4\u03ca"+
		"\3\2\2\2\u03d4\u03cb\3\2\2\2\u03d4\u03cc\3\2\2\2\u03d4\u03cd\3\2\2\2\u03d4"+
		"\u03ce\3\2\2\2\u03d4\u03cf\3\2\2\2\u03d4\u03d0\3\2\2\2\u03d4\u03d1\3\2"+
		"\2\2\u03d4\u03d2\3\2\2\2\u03d4\u03d3\3\2\2\2\u03d5\u0097\3\2\2\2\u03d6"+
		"\u03d7\5\u009aN\2\u03d7\u03d9\5\u009cO\2\u03d8\u03da\5\u00aaV\2\u03d9"+
		"\u03d8\3\2\2\2\u03d9\u03da\3\2\2\2\u03da\u03e8\3\2\2\2\u03db\u03df\5\\"+
		"/\2\u03dc\u03de\5\u00f2z\2\u03dd\u03dc\3\2\2\2\u03de\u03e1\3\2\2\2\u03df"+
		"\u03dd\3\2\2\2\u03df\u03e0\3\2\2\2\u03e0\u03e2\3\2\2\2\u03e1\u03df\3\2"+
		"\2\2\u03e2\u03e3\5\u009aN\2\u03e3\u03e5\5\u009cO\2\u03e4\u03e6\5\u00aa"+
		"V\2\u03e5\u03e4\3\2\2\2\u03e5\u03e6\3\2\2\2\u03e6\u03e8\3\2\2\2\u03e7"+
		"\u03d6\3\2\2\2\u03e7\u03db\3\2\2\2\u03e8\u0099\3\2\2\2\u03e9\u03ec\5z"+
		">\2\u03ea\u03ec\7\63\2\2\u03eb\u03e9\3\2\2\2\u03eb\u03ea\3\2\2\2\u03ec"+
		"\u009b\3\2\2\2\u03ed\u03ee\7i\2\2\u03ee\u03f0\7<\2\2\u03ef\u03f1\5\u009e"+
		"P\2\u03f0\u03ef\3\2\2\2\u03f0\u03f1\3\2\2\2\u03f1\u03f2\3\2\2\2\u03f2"+
		"\u03f4\7=\2\2\u03f3\u03f5\5\"\22\2\u03f4\u03f3\3\2\2\2\u03f4\u03f5\3\2"+
		"\2\2\u03f5\u009d\3\2\2\2\u03f6\u03f7\5\u00a0Q\2\u03f7\u03f8\7C\2\2\u03f8"+
		"\u03f9\5\u00a6T\2\u03f9\u03fc\3\2\2\2\u03fa\u03fc\5\u00a6T\2\u03fb\u03f6"+
		"\3\2\2\2\u03fb\u03fa\3\2\2\2\u03fc\u009f\3\2\2\2\u03fd\u0402\5\u00a2R"+
		"\2\u03fe\u03ff\7C\2\2\u03ff\u0401\5\u00a2R\2\u0400\u03fe\3\2\2\2\u0401"+
		"\u0404\3\2\2\2\u0402\u0400\3\2\2\2\u0402\u0403\3\2\2\2\u0403\u040e\3\2"+
		"\2\2\u0404\u0402\3\2\2\2\u0405\u040a\5\u00a8U\2\u0406\u0407\7C\2\2\u0407"+
		"\u0409\5\u00a2R\2\u0408\u0406\3\2\2\2\u0409\u040c\3\2\2\2\u040a\u0408"+
		"\3\2\2\2\u040a\u040b\3\2\2\2\u040b\u040e\3\2\2\2\u040c\u040a\3\2\2\2\u040d"+
		"\u03fd\3\2\2\2\u040d\u0405\3\2\2\2\u040e\u00a1\3\2\2\2\u040f\u0411\5\u00a4"+
		"S\2\u0410\u040f\3\2\2\2\u0411\u0414\3\2\2\2\u0412\u0410\3\2\2\2\u0412"+
		"\u0413\3\2\2\2\u0413\u0415\3\2\2\2\u0414\u0412\3\2\2\2\u0415\u0416\5z"+
		">\2\u0416\u0417\5v<\2\u0417\u00a3\3\2\2\2\u0418\u041b\5\u00f2z\2\u0419"+
		"\u041b\7\25\2\2\u041a\u0418\3\2\2\2\u041a\u0419\3\2\2\2\u041b\u00a5\3"+
		"\2\2\2\u041c\u041e\5\u00a4S\2\u041d\u041c\3\2\2\2\u041e\u0421\3\2\2\2"+
		"\u041f\u041d\3\2\2\2\u041f\u0420\3\2\2\2\u0420\u0422\3\2\2\2\u0421\u041f"+
		"\3\2\2\2\u0422\u0426\5z>\2\u0423\u0425\5\u00f2z\2\u0424\u0423\3\2\2\2"+
		"\u0425\u0428\3\2\2\2\u0426\u0424\3\2\2\2\u0426\u0427\3\2\2\2\u0427\u0429"+
		"\3\2\2\2\u0428\u0426\3\2\2\2\u0429\u042a\7k\2\2\u042a\u042b\5v<\2\u042b"+
		"\u042e\3\2\2\2\u042c\u042e\5\u00a2R\2\u042d\u041f\3\2\2\2\u042d\u042c"+
		"\3\2\2\2\u042e\u00a7\3\2\2\2\u042f\u0431\5\u00f2z\2\u0430\u042f\3\2\2"+
		"\2\u0431\u0434\3\2\2\2\u0432\u0430\3\2\2\2\u0432\u0433\3\2\2\2\u0433\u0435"+
		"\3\2\2\2\u0434\u0432\3\2\2\2\u0435\u0438\5z>\2\u0436\u0437\7i\2\2\u0437"+
		"\u0439\7D\2\2\u0438\u0436\3\2\2\2\u0438\u0439\3\2\2\2\u0439\u043a\3\2"+
		"\2\2\u043a\u043b\7.\2\2\u043b\u00a9\3\2\2\2\u043c\u043d\7\60\2\2\u043d"+
		"\u043e\5\u00acW\2\u043e\u00ab\3\2\2\2\u043f\u0444\5\u00aeX\2\u0440\u0441"+
		"\7C\2\2\u0441\u0443\5\u00aeX\2\u0442\u0440\3\2\2\2\u0443\u0446\3\2\2\2"+
		"\u0444\u0442\3\2\2\2\u0444\u0445\3\2\2\2\u0445\u00ad\3\2\2\2\u0446\u0444"+
		"\3\2\2\2\u0447\u044a\5\22\n\2\u0448\u044a\5\36\20\2\u0449\u0447\3\2\2"+
		"\2\u0449\u0448\3\2\2\2\u044a\u00af\3\2\2\2\u044b\u044e\5\u010c\u0087\2"+
		"\u044c\u044e\7B\2\2\u044d\u044b\3\2\2\2\u044d\u044c\3\2\2\2\u044e\u00b1"+
		"\3\2\2\2\u044f\u0450\5\u010c\u0087\2\u0450\u00b3\3\2\2\2\u0451\u0452\7"+
		")\2\2\u0452\u0453\5\u010c\u0087\2\u0453\u00b5\3\2\2\2\u0454\u0455\5\u00b8"+
		"]\2\u0455\u0457\5\u00bc_\2\u0456\u0458\5\u00aaV\2\u0457\u0456\3\2\2\2"+
		"\u0457\u0458\3\2\2\2\u0458\u0459\3\2\2\2\u0459\u045a\5\u00c0a\2\u045a"+
		"\u00b7\3\2\2\2\u045b\u045d\5\u00ba^\2\u045c\u045b\3\2\2\2\u045d\u0460"+
		"\3\2\2\2\u045e\u045c\3\2\2\2\u045e\u045f\3\2\2\2\u045f\u00b9\3\2\2\2\u0460"+
		"\u045e\3\2\2\2\u0461\u0466\5\u00f2z\2\u0462\u0466\7&\2\2\u0463\u0466\7"+
		"%\2\2\u0464\u0466\7$\2\2\u0465\u0461\3\2\2\2\u0465\u0462\3\2\2\2\u0465"+
		"\u0463\3\2\2\2\u0465\u0464\3\2\2\2\u0466\u00bb\3\2\2\2\u0467\u0469\5\\"+
		"/\2\u0468\u0467\3\2\2\2\u0468\u0469\3\2\2\2\u0469\u046a\3\2\2\2\u046a"+
		"\u046b\5\u00be`\2\u046b\u046d\7<\2\2\u046c\u046e\5\u009eP\2\u046d\u046c"+
		"\3\2\2\2\u046d\u046e\3\2\2\2\u046e\u046f\3\2\2\2\u046f\u0470\7=\2\2\u0470"+
		"\u00bd\3\2\2\2\u0471\u0472\7i\2\2\u0472\u00bf\3\2\2\2\u0473\u0475\7>\2"+
		"\2\u0474\u0476\5\u00c2b\2\u0475\u0474\3\2\2\2\u0475\u0476\3\2\2\2\u0476"+
		"\u0478\3\2\2\2\u0477\u0479\5\u010e\u0088\2\u0478\u0477\3\2\2\2\u0478\u0479"+
		"\3\2\2\2\u0479\u047a\3\2\2\2\u047a\u047b\7?\2\2\u047b\u00c1\3\2\2\2\u047c"+
		"\u047e\5,\27\2\u047d\u047c\3\2\2\2\u047d\u047e\3\2\2\2\u047e\u047f\3\2"+
		"\2\2\u047f\u0480\7.\2\2\u0480\u0482\7<\2\2\u0481\u0483\5\u019e\u00d0\2"+
		"\u0482\u0481\3\2\2\2\u0482\u0483\3\2\2\2\u0483\u0484\3\2\2\2\u0484\u0485"+
		"\7=\2\2\u0485\u04ab\7B\2\2\u0486\u0488\5,\27\2\u0487\u0486\3\2\2\2\u0487"+
		"\u0488\3\2\2\2\u0488\u0489\3\2\2\2\u0489\u048a\7+\2\2\u048a\u048c\7<\2"+
		"\2\u048b\u048d\5\u019e\u00d0\2\u048c\u048b\3\2\2\2\u048c\u048d\3\2\2\2"+
		"\u048d\u048e\3\2\2\2\u048e\u048f\7=\2\2\u048f\u04ab\7B\2\2\u0490\u0491"+
		"\5<\37\2\u0491\u0493\7D\2\2\u0492\u0494\5,\27\2\u0493\u0492\3\2\2\2\u0493"+
		"\u0494\3\2\2\2\u0494\u0495\3\2\2\2\u0495\u0496\7+\2\2\u0496\u0498\7<\2"+
		"\2\u0497\u0499\5\u019e\u00d0\2\u0498\u0497\3\2\2\2\u0498\u0499\3\2\2\2"+
		"\u0499\u049a\3\2\2\2\u049a\u049b\7=\2\2\u049b\u049c\7B\2\2\u049c\u04ab"+
		"\3\2\2\2\u049d\u049e\5\u0170\u00b9\2\u049e\u04a0\7D\2\2\u049f\u04a1\5"+
		",\27\2\u04a0\u049f\3\2\2\2\u04a0\u04a1\3\2\2\2\u04a1\u04a2\3\2\2\2\u04a2"+
		"\u04a3\7+\2\2\u04a3\u04a5\7<\2\2\u04a4\u04a6\5\u019e\u00d0\2\u04a5\u04a4"+
		"\3\2\2\2\u04a5\u04a6\3\2\2\2\u04a6\u04a7\3\2\2\2\u04a7\u04a8\7=\2\2\u04a8"+
		"\u04a9\7B\2\2\u04a9\u04ab\3\2\2\2\u04aa\u047d\3\2\2\2\u04aa\u0487\3\2"+
		"\2\2\u04aa\u0490\3\2\2\2\u04aa\u049d\3\2\2\2\u04ab\u00c3\3\2\2\2\u04ac"+
		"\u04ad\5X-\2\u04ad\u04ae\7\23\2\2\u04ae\u04b0\7i\2\2\u04af\u04b1\5b\62"+
		"\2\u04b0\u04af\3\2\2\2\u04b0\u04b1\3\2\2\2\u04b1\u04b2\3\2\2\2\u04b2\u04b3"+
		"\5\u00c6d\2\u04b3\u00c5\3\2\2\2\u04b4\u04b6\7>\2\2\u04b5\u04b7\5\u00c8"+
		"e\2\u04b6\u04b5\3\2\2\2\u04b6\u04b7\3\2\2\2\u04b7\u04b9\3\2\2\2\u04b8"+
		"\u04ba\7C\2\2\u04b9\u04b8\3\2\2\2\u04b9\u04ba\3\2\2\2\u04ba\u04bc\3\2"+
		"\2\2\u04bb\u04bd\5\u00ceh\2\u04bc\u04bb\3\2\2\2\u04bc\u04bd\3\2\2\2\u04bd"+
		"\u04be\3\2\2\2\u04be\u04bf\7?\2\2\u04bf\u00c7\3\2\2\2\u04c0\u04c5\5\u00ca"+
		"f\2\u04c1\u04c2\7C\2\2\u04c2\u04c4\5\u00caf\2\u04c3\u04c1\3\2\2\2\u04c4"+
		"\u04c7\3\2\2\2\u04c5\u04c3\3\2\2\2\u04c5\u04c6\3\2\2\2\u04c6\u00c9\3\2"+
		"\2\2\u04c7\u04c5\3\2\2\2\u04c8\u04ca\5\u00ccg\2\u04c9\u04c8\3\2\2\2\u04ca"+
		"\u04cd\3\2\2\2\u04cb\u04c9\3\2\2\2\u04cb\u04cc\3\2\2\2\u04cc\u04ce\3\2"+
		"\2\2\u04cd\u04cb\3\2\2\2\u04ce\u04d4\7i\2\2\u04cf\u04d1\7<\2\2\u04d0\u04d2"+
		"\5\u019e\u00d0\2\u04d1\u04d0\3\2\2\2\u04d1\u04d2\3\2\2\2\u04d2\u04d3\3"+
		"\2\2\2\u04d3\u04d5\7=\2\2\u04d4\u04cf\3\2\2\2\u04d4\u04d5\3\2\2\2\u04d5"+
		"\u04d7\3\2\2\2\u04d6\u04d8\5f\64\2\u04d7\u04d6\3\2\2\2\u04d7\u04d8\3\2"+
		"\2\2\u04d8\u00cb\3\2\2\2\u04d9\u04da\5\u00f2z\2\u04da\u00cd\3\2\2\2\u04db"+
		"\u04df\7B\2\2\u04dc\u04de\5h\65\2\u04dd\u04dc\3\2\2\2\u04de\u04e1\3\2"+
		"\2\2\u04df\u04dd\3\2\2\2\u04df\u04e0\3\2\2\2\u04e0\u00cf\3\2\2\2\u04e1"+
		"\u04df\3\2\2\2\u04e2\u04e5\5\u00d2j\2\u04e3\u04e5\5\u00e6t\2\u04e4\u04e2"+
		"\3\2\2\2\u04e4\u04e3\3\2\2\2\u04e5\u00d1\3\2\2\2\u04e6\u04e7\5\u00d4k"+
		"\2\u04e7\u04e8\7\37\2\2\u04e8\u04ea\7i\2\2\u04e9\u04eb\5\\/\2\u04ea\u04e9"+
		"\3\2\2\2\u04ea\u04eb\3\2\2\2\u04eb\u04ed\3\2\2\2\u04ec\u04ee\5\u00d8m"+
		"\2\u04ed\u04ec\3\2\2\2\u04ed\u04ee\3\2\2\2\u04ee\u04ef\3\2\2\2\u04ef\u04f0"+
		"\5\u00dan\2\u04f0\u00d3\3\2\2\2\u04f1\u04f3\5\u00d6l\2\u04f2\u04f1\3\2"+
		"\2\2\u04f3\u04f6\3\2\2\2\u04f4\u04f2\3\2\2\2\u04f4\u04f5\3\2\2\2\u04f5"+
		"\u00d5\3\2\2\2\u04f6\u04f4\3\2\2\2\u04f7\u04ff\5\u00f2z\2\u04f8\u04ff"+
		"\7&\2\2\u04f9\u04ff\7%\2\2\u04fa\u04ff\7$\2\2\u04fb\u04ff\7\4\2\2\u04fc"+
		"\u04ff\7)\2\2\u04fd\u04ff\7*\2\2\u04fe\u04f7\3\2\2\2\u04fe\u04f8\3\2\2"+
		"\2\u04fe\u04f9\3\2\2\2\u04fe\u04fa\3\2\2\2\u04fe\u04fb\3\2\2\2\u04fe\u04fc"+
		"\3\2\2\2\u04fe\u04fd\3\2\2\2\u04ff\u00d7\3\2\2\2\u0500\u0501\7\24\2\2"+
		"\u0501\u0502\5d\63\2\u0502\u00d9\3\2\2\2\u0503\u0507\7>\2\2\u0504\u0506"+
		"\5\u00dco\2\u0505\u0504\3\2\2\2\u0506\u0509\3\2\2\2\u0507\u0505\3\2\2"+
		"\2\u0507\u0508\3\2\2\2\u0508\u050a\3\2\2\2\u0509\u0507\3\2\2\2\u050a\u050b"+
		"\7?\2\2\u050b\u00db\3\2\2\2\u050c\u0512\5\u00dep\2\u050d\u0512\5\u00e2"+
		"r\2\u050e\u0512\5T+\2\u050f\u0512\5\u00d0i\2\u0510\u0512\7B\2\2\u0511"+
		"\u050c\3\2\2\2\u0511\u050d\3\2\2\2\u0511\u050e\3\2\2\2\u0511\u050f\3\2"+
		"\2\2\u0511\u0510\3\2\2\2\u0512\u00dd\3\2\2\2\u0513\u0515\5\u00e0q\2\u0514"+
		"\u0513\3\2\2\2\u0515\u0518\3\2\2\2\u0516\u0514\3\2\2\2\u0516\u0517\3\2"+
		"\2\2\u0517\u0519\3\2\2\2\u0518\u0516\3\2\2\2\u0519\u051a\5z>\2\u051a\u051b"+
		"\5r:\2\u051b\u051c\7B\2\2\u051c\u00df\3\2\2\2\u051d\u0522\5\u00f2z\2\u051e"+
		"\u0522\7&\2\2\u051f\u0522\7)\2\2\u0520\u0522\7\25\2\2\u0521\u051d\3\2"+
		"\2\2\u0521\u051e\3\2\2\2\u0521\u051f\3\2\2\2\u0521\u0520\3\2\2\2\u0522"+
		"\u00e1\3\2\2\2\u0523\u0525\5\u00e4s\2\u0524\u0523\3\2\2\2\u0525\u0528"+
		"\3\2\2\2\u0526\u0524\3\2\2\2\u0526\u0527\3\2\2\2\u0527\u0529\3\2\2\2\u0528"+
		"\u0526\3\2\2\2\u0529\u052a\5\u0098M\2\u052a\u052b\5\u00b0Y\2\u052b\u00e3"+
		"\3\2\2\2\u052c\u0533\5\u00f2z\2\u052d\u0533\7&\2\2\u052e\u0533\7\4\2\2"+
		"\u052f\u0533\7\17\2\2\u0530\u0533\7)\2\2\u0531\u0533\7*\2\2\u0532\u052c"+
		"\3\2\2\2\u0532\u052d\3\2\2\2\u0532\u052e\3\2\2\2\u0532\u052f\3\2\2\2\u0532"+
		"\u0530\3\2\2\2\u0532\u0531\3\2\2\2\u0533\u00e5\3\2\2\2\u0534\u0536\5\u00d6"+
		"l\2\u0535\u0534\3\2\2\2\u0536\u0539\3\2\2\2\u0537\u0535\3\2\2\2\u0537"+
		"\u0538\3\2\2\2\u0538\u053a\3\2\2\2\u0539\u0537\3\2\2\2\u053a\u053b\7j"+
		"\2\2\u053b\u053c\7\37\2\2\u053c\u053d\7i\2\2\u053d\u053e\5\u00e8u\2\u053e"+
		"\u00e7\3\2\2\2\u053f\u0543\7>\2\2\u0540\u0542\5\u00eav\2\u0541\u0540\3"+
		"\2\2\2\u0542\u0545\3\2\2\2\u0543\u0541\3\2\2\2\u0543\u0544\3\2\2\2\u0544"+
		"\u0546\3\2\2\2\u0545\u0543\3\2\2\2\u0546\u0547\7?\2\2\u0547\u00e9\3\2"+
		"\2\2\u0548\u054e\5\u00ecw\2\u0549\u054e\5\u00dep\2\u054a\u054e\5T+\2\u054b"+
		"\u054e\5\u00d0i\2\u054c\u054e\7B\2\2\u054d\u0548\3\2\2\2\u054d\u0549\3"+
		"\2\2\2\u054d\u054a\3\2\2\2\u054d\u054b\3\2\2\2\u054d\u054c\3\2\2\2\u054e"+
		"\u00eb\3\2\2\2\u054f\u0551\5\u00eex\2\u0550\u054f\3\2\2\2\u0551\u0554"+
		"\3\2\2\2\u0552\u0550\3\2\2\2\u0552\u0553\3\2\2\2\u0553\u0555\3\2\2\2\u0554"+
		"\u0552\3\2\2\2\u0555\u0556\5z>\2\u0556\u0557\7i\2\2\u0557\u0558\7<\2\2"+
		"\u0558\u055a\7=\2\2\u0559\u055b\5\"\22\2\u055a\u0559\3\2\2\2\u055a\u055b"+
		"\3\2\2\2\u055b\u055d\3\2\2\2\u055c\u055e\5\u00f0y\2\u055d\u055c\3\2\2"+
		"\2\u055d\u055e\3\2\2\2\u055e\u055f\3\2\2\2\u055f\u0560\7B\2\2\u0560\u00ed"+
		"\3\2\2\2\u0561\u0565\5\u00f2z\2\u0562\u0565\7&\2\2\u0563\u0565\7\4\2\2"+
		"\u0564\u0561\3\2\2\2\u0564\u0562\3\2\2\2\u0564\u0563\3\2\2\2\u0565\u00ef"+
		"\3\2\2\2\u0566\u0567\7\17\2\2\u0567\u0568\5\u00fe\u0080\2\u0568\u00f1"+
		"\3\2\2\2\u0569\u056d\5\u00f8}\2\u056a\u056d\5\u0104\u0083\2\u056b\u056d"+
		"\5\u0106\u0084\2\u056c\u0569\3\2\2\2\u056c\u056a\3\2\2\2\u056c\u056b\3"+
		"\2\2\2\u056d\u00f3\3\2\2\2\u056e\u0570\5\u00f2z\2\u056f\u056e\3\2\2\2"+
		"\u0570\u0573\3\2\2\2\u0571\u056f\3\2\2\2\u0571\u0572\3\2\2\2\u0572\u0574"+
		"\3\2\2\2\u0573\u0571\3\2\2\2\u0574\u0575\7i\2\2\u0575\u00f5\3\2\2\2\u0576"+
		"\u0578\5\u00f2z\2\u0577\u0576\3\2\2\2\u0578\u057b\3\2\2\2\u0579\u0577"+
		"\3\2\2\2\u0579\u057a\3\2\2\2\u057a\u057c\3\2\2\2\u057b\u0579\3\2\2\2\u057c"+
		"\u057d\5\u01c6\u00e4\2\u057d\u00f7\3\2\2\2\u057e\u057f\7j\2\2\u057f\u0580"+
		"\58\35\2\u0580\u0582\7<\2\2\u0581\u0583\5\u00fa~\2\u0582\u0581\3\2\2\2"+
		"\u0582\u0583\3\2\2\2\u0583\u0584\3\2\2\2\u0584\u0585\7=\2\2\u0585\u00f9"+
		"\3\2\2\2\u0586\u058b\5\u00fc\177\2\u0587\u0588\7C\2\2\u0588\u058a\5\u00fc"+
		"\177\2\u0589\u0587\3\2\2\2\u058a\u058d\3\2\2\2\u058b\u0589\3\2\2\2\u058b"+
		"\u058c\3\2\2\2\u058c\u00fb\3\2\2\2\u058d\u058b\3\2\2\2\u058e\u058f\7i"+
		"\2\2\u058f\u0590\7E\2\2\u0590\u0591\5\u00fe\u0080\2\u0591\u00fd\3\2\2"+
		"\2\u0592\u0596\5\u01c8\u00e5\2\u0593\u0596\5\u0100\u0081\2\u0594\u0596"+
		"\5\u00f2z\2\u0595\u0592\3\2\2\2\u0595\u0593\3\2\2\2\u0595\u0594\3\2\2"+
		"\2\u0596\u00ff\3\2\2\2\u0597\u0599\7>\2\2\u0598\u059a\5\u0102\u0082\2"+
		"\u0599\u0598\3\2\2\2\u0599\u059a\3\2\2\2\u059a\u059c\3\2\2\2\u059b\u059d"+
		"\7C\2\2\u059c\u059b\3\2\2\2\u059c\u059d\3\2\2\2\u059d\u059e\3\2\2\2\u059e"+
		"\u059f\7?\2\2\u059f\u0101\3\2\2\2\u05a0\u05a5\5\u00fe\u0080\2\u05a1\u05a2"+
		"\7C\2\2\u05a2\u05a4\5\u00fe\u0080\2\u05a3\u05a1\3\2\2\2\u05a4\u05a7\3"+
		"\2\2\2\u05a5\u05a3\3\2\2\2\u05a5\u05a6\3\2\2\2\u05a6\u0103\3\2\2\2\u05a7"+
		"\u05a5\3\2\2\2\u05a8\u05a9\7j\2\2\u05a9\u05aa\58\35\2\u05aa\u0105\3\2"+
		"\2\2\u05ab\u05ac\7j\2\2\u05ac\u05ad\58\35\2\u05ad\u05ae\7<\2\2\u05ae\u05af"+
		"\5\u00fe\u0080\2\u05af\u05b0\7=\2\2\u05b0\u0107\3\2\2\2\u05b1\u05b3\7"+
		">\2\2\u05b2\u05b4\5\u010a\u0086\2\u05b3\u05b2\3\2\2\2\u05b3\u05b4\3\2"+
		"\2\2\u05b4\u05b6\3\2\2\2\u05b5\u05b7\7C\2\2\u05b6\u05b5\3\2\2\2\u05b6"+
		"\u05b7\3\2\2\2\u05b7\u05b8\3\2\2\2\u05b8\u05b9\7?\2\2\u05b9\u0109\3\2"+
		"\2\2\u05ba\u05bf\5x=\2\u05bb\u05bc\7C\2\2\u05bc\u05be\5x=\2\u05bd\u05bb"+
		"\3\2\2\2\u05be\u05c1\3\2\2\2\u05bf\u05bd\3\2\2\2\u05bf\u05c0\3\2\2\2\u05c0"+
		"\u010b\3\2\2\2\u05c1\u05bf\3\2\2\2\u05c2\u05c4\7>\2\2\u05c3\u05c5\5\u010e"+
		"\u0088\2\u05c4\u05c3\3\2\2\2\u05c4\u05c5\3\2\2\2\u05c5\u05c6\3\2\2\2\u05c6"+
		"\u05c7\7?\2\2\u05c7\u010d\3\2\2\2\u05c8\u05cc\5\u0110\u0089\2\u05c9\u05cb"+
		"\5\u0110\u0089\2\u05ca\u05c9\3\2\2\2\u05cb\u05ce\3\2\2\2\u05cc\u05ca\3"+
		"\2\2\2\u05cc\u05cd\3\2\2\2\u05cd\u010f\3\2\2\2\u05ce\u05cc\3\2\2\2\u05cf"+
		"\u05d3\5\u0112\u008a\2\u05d0\u05d3\5T+\2\u05d1\u05d3\5\u0116\u008c\2\u05d2"+
		"\u05cf\3\2\2\2\u05d2\u05d0\3\2\2\2\u05d2\u05d1\3\2\2\2\u05d3\u0111\3\2"+
		"\2\2\u05d4\u05d5\5\u0114\u008b\2\u05d5\u05d6\7B\2\2\u05d6\u0113\3\2\2"+
		"\2\u05d7\u05d9\5\u00a4S\2\u05d8\u05d7\3\2\2\2\u05d9\u05dc\3\2\2\2\u05da"+
		"\u05d8\3\2\2\2\u05da\u05db\3\2\2\2\u05db\u05dd\3\2\2\2\u05dc\u05da\3\2"+
		"\2\2\u05dd\u05de\5z>\2\u05de\u05df\5r:\2\u05df\u0115\3\2\2\2\u05e0\u05e7"+
		"\5\u011a\u008e\2\u05e1\u05e7\5\u011e\u0090\2\u05e2\u05e7\5\u0126\u0094"+
		"\2\u05e3\u05e7\5\u0128\u0095\2\u05e4\u05e7\5\u013a\u009e\2\u05e5\u05e7"+
		"\5\u0140\u00a1\2\u05e6\u05e0\3\2\2\2\u05e6\u05e1\3\2\2\2\u05e6\u05e2\3"+
		"\2\2\2\u05e6\u05e3\3\2\2\2\u05e6\u05e4\3\2\2\2\u05e6\u05e5\3\2\2\2\u05e7"+
		"\u0117\3\2\2\2\u05e8\u05ee\5\u011a\u008e\2\u05e9\u05ee\5\u0120\u0091\2"+
		"\u05ea\u05ee\5\u012a\u0096\2\u05eb\u05ee\5\u013c\u009f\2\u05ec\u05ee\5"+
		"\u0142\u00a2\2\u05ed\u05e8\3\2\2\2\u05ed\u05e9\3\2\2\2\u05ed\u05ea\3\2"+
		"\2\2\u05ed\u05eb\3\2\2\2\u05ed\u05ec\3\2\2\2\u05ee\u0119\3\2\2\2\u05ef"+
		"\u05fc\5\u010c\u0087\2\u05f0\u05fc\5\u011c\u008f\2\u05f1\u05fc\5\u0122"+
		"\u0092\2\u05f2\u05fc\5\u012c\u0097\2\u05f3\u05fc\5\u012e\u0098\2\u05f4"+
		"\u05fc\5\u013e\u00a0\2\u05f5\u05fc\5\u0152\u00aa\2\u05f6\u05fc\5\u0154"+
		"\u00ab\2\u05f7\u05fc\5\u0156\u00ac\2\u05f8\u05fc\5\u015a\u00ae\2\u05f9"+
		"\u05fc\5\u0158\u00ad\2\u05fa\u05fc\5\u015c\u00af\2\u05fb\u05ef\3\2\2\2"+
		"\u05fb\u05f0\3\2\2\2\u05fb\u05f1\3\2\2\2\u05fb\u05f2\3\2\2\2\u05fb\u05f3"+
		"\3\2\2\2\u05fb\u05f4\3\2\2\2\u05fb\u05f5\3\2\2\2\u05fb\u05f6\3\2\2\2\u05fb"+
		"\u05f7\3\2\2\2\u05fb\u05f8\3\2\2\2\u05fb\u05f9\3\2\2\2\u05fb\u05fa\3\2"+
		"\2\2\u05fc\u011b\3\2\2\2\u05fd\u05fe\7B\2\2\u05fe\u011d\3\2\2\2\u05ff"+
		"\u0600\7i\2\2\u0600\u0601\7K\2\2\u0601\u0602\5\u0116\u008c\2\u0602\u011f"+
		"\3\2\2\2\u0603\u0604\7i\2\2\u0604\u0605\7K\2\2\u0605\u0606\5\u0118\u008d"+
		"\2\u0606\u0121\3\2\2\2\u0607\u0608\5\u0124\u0093\2\u0608\u0609\7B\2\2"+
		"\u0609\u0123\3\2\2\2\u060a\u0612\5\u01ba\u00de\2\u060b\u0612\5\u01e2\u00f2"+
		"\2\u060c\u0612\5\u01e4\u00f3\2\u060d\u0612\5\u01ea\u00f6\2\u060e\u0612"+
		"\5\u01ee\u00f8\2\u060f\u0612\5\u0198\u00cd\2\u0610\u0612\5\u0184\u00c3"+
		"\2\u0611\u060a\3\2\2\2\u0611\u060b\3\2\2\2\u0611\u060c\3\2\2\2\u0611\u060d"+
		"\3\2\2\2\u0611\u060e\3\2\2\2\u0611\u060f\3\2\2\2\u0611\u0610\3\2\2\2\u0612"+
		"\u0125\3\2\2\2\u0613\u0614\7\31\2\2\u0614\u0615\7<\2\2\u0615\u0616\5\u01ae"+
		"\u00d8\2\u0616\u0617\7=\2\2\u0617\u0618\5\u0116\u008c\2\u0618\u0127\3"+
		"\2\2\2\u0619\u061a\7\31\2\2\u061a\u061b\7<\2\2\u061b\u061c\5\u01ae\u00d8"+
		"\2\u061c\u061d\7=\2\2\u061d\u061e\5\u0118\u008d\2\u061e\u061f\7\22\2\2"+
		"\u061f\u0620\5\u0116\u008c\2\u0620\u0129\3\2\2\2\u0621\u0622\7\31\2\2"+
		"\u0622\u0623\7<\2\2\u0623\u0624\5\u01ae\u00d8\2\u0624\u0625\7=\2\2\u0625"+
		"\u0626\5\u0118\u008d\2\u0626\u0627\7\22\2\2\u0627\u0628\5\u0118\u008d"+
		"\2\u0628\u012b\3\2\2\2\u0629\u062a\7\5\2\2\u062a\u062b\5\u01ae\u00d8\2"+
		"\u062b\u062c\7B\2\2\u062c\u0634\3\2\2\2\u062d\u062e\7\5\2\2\u062e\u062f"+
		"\5\u01ae\u00d8\2\u062f\u0630\7K\2\2\u0630\u0631\5\u01ae\u00d8\2\u0631"+
		"\u0632\7B\2\2\u0632\u0634\3\2\2\2\u0633\u0629\3\2\2\2\u0633\u062d\3\2"+
		"\2\2\u0634\u012d\3\2\2\2\u0635\u0636\7,\2\2\u0636\u0637\7<\2\2\u0637\u0638"+
		"\5\u01ae\u00d8\2\u0638\u0639\7=\2\2\u0639\u063a\5\u0130\u0099\2\u063a"+
		"\u012f\3\2\2\2\u063b\u063f\7>\2\2\u063c\u063e\5\u0132\u009a\2\u063d\u063c"+
		"\3\2\2\2\u063e\u0641\3\2\2\2\u063f\u063d\3\2\2\2\u063f\u0640\3\2\2\2\u0640"+
		"\u0645\3\2\2\2\u0641\u063f\3\2\2\2\u0642\u0644\5\u0136\u009c\2\u0643\u0642"+
		"\3\2\2\2\u0644\u0647\3\2\2\2\u0645\u0643\3\2\2\2\u0645\u0646\3\2\2\2\u0646"+
		"\u0648\3\2\2\2\u0647\u0645\3\2\2\2\u0648\u0649\7?\2\2\u0649\u0131\3\2"+
		"\2\2\u064a\u064b\5\u0134\u009b\2\u064b\u064c\5\u010e\u0088\2\u064c\u0133"+
		"\3\2\2\2\u064d\u0651\5\u0136\u009c\2\u064e\u0650\5\u0136\u009c\2\u064f"+
		"\u064e\3\2\2\2\u0650\u0653\3\2\2\2\u0651\u064f\3\2\2\2\u0651\u0652\3\2"+
		"\2\2\u0652\u0135\3\2\2\2\u0653\u0651\3\2\2\2\u0654\u0655\7\t\2\2\u0655"+
		"\u0656\5\u01ac\u00d7\2\u0656\u0657\7K\2\2\u0657\u065f\3\2\2\2\u0658\u0659"+
		"\7\t\2\2\u0659\u065a\5\u0138\u009d\2\u065a\u065b\7K\2\2\u065b\u065f\3"+
		"\2\2\2\u065c\u065d\7\17\2\2\u065d\u065f\7K\2\2\u065e\u0654\3\2\2\2\u065e"+
		"\u0658\3\2\2\2\u065e\u065c\3\2\2\2\u065f\u0137\3\2\2\2\u0660\u0661\7i"+
		"\2\2\u0661\u0139\3\2\2\2\u0662\u0663\7\65\2\2\u0663\u0664\7<\2\2\u0664"+
		"\u0665\5\u01ae\u00d8\2\u0665\u0666\7=\2\2\u0666\u0667\5\u0116\u008c\2"+
		"\u0667\u013b\3\2\2\2\u0668\u0669\7\65\2\2\u0669\u066a\7<\2\2\u066a\u066b"+
		"\5\u01ae\u00d8\2\u066b\u066c\7=\2\2\u066c\u066d\5\u0118\u008d\2\u066d"+
		"\u013d\3\2\2\2\u066e\u066f\7\20\2\2\u066f\u0670\5\u0116\u008c\2\u0670"+
		"\u0671\7\65\2\2\u0671\u0672\7<\2\2\u0672\u0673\5\u01ae\u00d8\2\u0673\u0674"+
		"\7=\2\2\u0674\u0675\7B\2\2\u0675\u013f\3\2\2\2\u0676\u0679\5\u0144\u00a3"+
		"\2\u0677\u0679\5\u014e\u00a8\2\u0678\u0676\3\2\2\2\u0678\u0677\3\2\2\2"+
		"\u0679\u0141\3\2\2\2\u067a\u067d\5\u0146\u00a4\2\u067b\u067d\5\u0150\u00a9"+
		"\2\u067c\u067a\3\2\2\2\u067c\u067b\3\2\2\2\u067d\u0143\3\2\2\2\u067e\u067f"+
		"\7\30\2\2\u067f\u0681\7<\2\2\u0680\u0682\5\u0148\u00a5\2\u0681\u0680\3"+
		"\2\2\2\u0681\u0682\3\2\2\2\u0682\u0683\3\2\2\2\u0683\u0685\7B\2\2\u0684"+
		"\u0686\5\u01ae\u00d8\2\u0685\u0684\3\2\2\2\u0685\u0686\3\2\2\2\u0686\u0687"+
		"\3\2\2\2\u0687\u0689\7B\2\2\u0688\u068a\5\u014a\u00a6\2\u0689\u0688\3"+
		"\2\2\2\u0689\u068a\3\2\2\2\u068a\u068b\3\2\2\2\u068b\u068c\7=\2\2\u068c"+
		"\u068d\5\u0116\u008c\2\u068d\u0145\3\2\2\2\u068e\u068f\7\30\2\2\u068f"+
		"\u0691\7<\2\2\u0690\u0692\5\u0148\u00a5\2\u0691\u0690\3\2\2\2\u0691\u0692"+
		"\3\2\2\2\u0692\u0693\3\2\2\2\u0693\u0695\7B\2\2\u0694\u0696\5\u01ae\u00d8"+
		"\2\u0695\u0694\3\2\2\2\u0695\u0696\3\2\2\2\u0696\u0697\3\2\2\2\u0697\u0699"+
		"\7B\2\2\u0698\u069a\5\u014a\u00a6\2\u0699\u0698\3\2\2\2\u0699\u069a\3"+
		"\2\2\2\u069a\u069b\3\2\2\2\u069b\u069c\7=\2\2\u069c\u069d\5\u0118\u008d"+
		"\2\u069d\u0147\3\2\2\2\u069e\u06a1\5\u014c\u00a7\2\u069f\u06a1\5\u0114"+
		"\u008b\2\u06a0\u069e\3\2\2\2\u06a0\u069f\3\2\2\2\u06a1\u0149\3\2\2\2\u06a2"+
		"\u06a3\5\u014c\u00a7\2\u06a3\u014b\3\2\2\2\u06a4\u06a9\5\u0124\u0093\2"+
		"\u06a5\u06a6\7C\2\2\u06a6\u06a8\5\u0124\u0093\2\u06a7\u06a5\3\2\2\2\u06a8"+
		"\u06ab\3\2\2\2\u06a9\u06a7\3\2\2\2\u06a9\u06aa\3\2\2\2\u06aa\u014d\3\2"+
		"\2\2\u06ab\u06a9\3\2\2\2\u06ac\u06ad\7\30\2\2\u06ad\u06b1\7<\2\2\u06ae"+
		"\u06b0\5\u00a4S\2\u06af\u06ae\3\2\2\2\u06b0\u06b3\3\2\2\2\u06b1\u06af"+
		"\3\2\2\2\u06b1\u06b2\3\2\2\2\u06b2\u06b4\3\2\2\2\u06b3\u06b1\3\2\2\2\u06b4"+
		"\u06b5\5z>\2\u06b5\u06b6\5v<\2\u06b6\u06b7\7K\2\2\u06b7\u06b8\5\u01ae"+
		"\u00d8\2\u06b8\u06b9\7=\2\2\u06b9\u06ba\5\u0116\u008c\2\u06ba\u014f\3"+
		"\2\2\2\u06bb\u06bc\7\30\2\2\u06bc\u06c0\7<\2\2\u06bd\u06bf\5\u00a4S\2"+
		"\u06be\u06bd\3\2\2\2\u06bf\u06c2\3\2\2\2\u06c0\u06be\3\2\2\2\u06c0\u06c1"+
		"\3\2\2\2\u06c1\u06c3\3\2\2\2\u06c2\u06c0\3\2\2\2\u06c3\u06c4\5z>\2\u06c4"+
		"\u06c5\5v<\2\u06c5\u06c6\7K\2\2\u06c6\u06c7\5\u01ae\u00d8\2\u06c7\u06c8"+
		"\7=\2\2\u06c8\u06c9\5\u0118\u008d\2\u06c9\u0151\3\2\2\2\u06ca\u06cc\7"+
		"\7\2\2\u06cb\u06cd\7i\2\2\u06cc\u06cb\3\2\2\2\u06cc\u06cd\3\2\2\2\u06cd"+
		"\u06ce\3\2\2\2\u06ce\u06cf\7B\2\2\u06cf\u0153\3\2\2\2\u06d0\u06d2\7\16"+
		"\2\2\u06d1\u06d3\7i\2\2\u06d2\u06d1\3\2\2\2\u06d2\u06d3\3\2\2\2\u06d3"+
		"\u06d4\3\2\2\2\u06d4\u06d5\7B\2\2\u06d5\u0155\3\2\2\2\u06d6\u06d8\7\'"+
		"\2\2\u06d7\u06d9\5\u01ae\u00d8\2\u06d8\u06d7\3\2\2\2\u06d8\u06d9\3\2\2"+
		"\2\u06d9\u06da\3\2\2\2\u06da\u06db\7B\2\2\u06db\u0157\3\2\2\2\u06dc\u06dd"+
		"\7/\2\2\u06dd\u06de\5\u01ae\u00d8\2\u06de\u06df\7B\2\2\u06df\u0159\3\2"+
		"\2\2\u06e0\u06e1\7-\2\2\u06e1\u06e2\7<\2\2\u06e2\u06e3\5\u01ae\u00d8\2"+
		"\u06e3\u06e4\7=\2\2\u06e4\u06e5\5\u010c\u0087\2\u06e5\u015b\3\2\2\2\u06e6"+
		"\u06e7\7\62\2\2\u06e7\u06e8\5\u010c\u0087\2\u06e8\u06e9\5\u015e\u00b0"+
		"\2\u06e9\u06f3\3\2\2\2\u06ea\u06eb\7\62\2\2\u06eb\u06ed\5\u010c\u0087"+
		"\2\u06ec\u06ee\5\u015e\u00b0\2\u06ed\u06ec\3\2\2\2\u06ed\u06ee\3\2\2\2"+
		"\u06ee\u06ef\3\2\2\2\u06ef\u06f0\5\u0166\u00b4\2\u06f0\u06f3\3\2\2\2\u06f1"+
		"\u06f3\5\u0168\u00b5\2\u06f2\u06e6\3\2\2\2\u06f2\u06ea\3\2\2\2\u06f2\u06f1"+
		"\3\2\2\2\u06f3\u015d\3\2\2\2\u06f4\u06f8\5\u0160\u00b1\2\u06f5\u06f7\5"+
		"\u0160\u00b1\2\u06f6\u06f5\3\2\2\2\u06f7\u06fa\3\2\2\2\u06f8\u06f6\3\2"+
		"\2\2\u06f8\u06f9\3\2\2\2\u06f9\u015f\3\2\2\2\u06fa\u06f8\3\2\2\2\u06fb"+
		"\u06fc\7\n\2\2\u06fc\u06fd\7<\2\2\u06fd\u06fe\5\u0162\u00b2\2\u06fe\u06ff"+
		"\7=\2\2\u06ff\u0700\5\u010c\u0087\2\u0700\u0161\3\2\2\2\u0701\u0703\5"+
		"\u00a4S\2\u0702\u0701\3\2\2\2\u0703\u0706\3\2\2\2\u0704\u0702\3\2\2\2"+
		"\u0704\u0705\3\2\2\2\u0705\u0707\3\2\2\2\u0706\u0704\3\2\2\2\u0707\u0708"+
		"\5\u0164\u00b3\2\u0708\u0709\5v<\2\u0709\u0163\3\2\2\2\u070a\u070f\5\u0082"+
		"B\2\u070b\u070c\7Y\2\2\u070c\u070e\5\22\n\2\u070d\u070b\3\2\2\2\u070e"+
		"\u0711\3\2\2\2\u070f\u070d\3\2\2\2\u070f\u0710\3\2\2\2\u0710\u0165\3\2"+
		"\2\2\u0711\u070f\3\2\2\2\u0712\u0713\7\26\2\2\u0713\u0714\5\u010c\u0087"+
		"\2\u0714\u0167\3\2\2\2\u0715\u0716\7\62\2\2\u0716\u0717\5\u016a\u00b6"+
		"\2\u0717\u0719\5\u010c\u0087\2\u0718\u071a\5\u015e\u00b0\2\u0719\u0718"+
		"\3\2\2\2\u0719\u071a\3\2\2\2\u071a\u071c\3\2\2\2\u071b\u071d\5\u0166\u00b4"+
		"\2\u071c\u071b\3\2\2\2\u071c\u071d\3\2\2\2\u071d\u0169\3\2\2\2\u071e\u071f"+
		"\7<\2\2\u071f\u0721\5\u016c\u00b7\2\u0720\u0722\7B\2\2\u0721\u0720\3\2"+
		"\2\2\u0721\u0722\3\2\2\2\u0722\u0723\3\2\2\2\u0723\u0724\7=\2\2\u0724"+
		"\u016b\3\2\2\2\u0725\u072a\5\u016e\u00b8\2\u0726\u0727\7B\2\2\u0727\u0729"+
		"\5\u016e\u00b8\2\u0728\u0726\3\2\2\2\u0729\u072c\3\2\2\2\u072a\u0728\3"+
		"\2\2\2\u072a\u072b\3\2\2\2\u072b\u016d\3\2\2\2\u072c\u072a\3\2\2\2\u072d"+
		"\u072f\5\u00a4S\2\u072e\u072d\3\2\2\2\u072f\u0732\3\2\2\2\u0730\u072e"+
		"\3\2\2\2\u0730\u0731\3\2\2\2\u0731\u0733\3\2\2\2\u0732\u0730\3\2\2\2\u0733"+
		"\u0734\5z>\2\u0734\u0735\5v<\2\u0735\u0736\7E\2\2\u0736\u0737\5\u01ae"+
		"\u00d8\2\u0737\u016f\3\2\2\2\u0738\u073b\5\u017e\u00c0\2\u0739\u073b\5"+
		"\u01a6\u00d4\2\u073a\u0738\3\2\2\2\u073a\u0739\3\2\2\2\u073b\u073f\3\2"+
		"\2\2\u073c\u073e\5\u0178\u00bd\2\u073d\u073c\3\2\2\2\u073e\u0741\3\2\2"+
		"\2\u073f\u073d\3\2\2\2\u073f\u0740\3\2\2\2\u0740\u0171\3\2\2\2\u0741\u073f"+
		"\3\2\2\2\u0742\u075f\5\2\2\2\u0743\u0747\58\35\2\u0744\u0746\5\u01c6\u00e4"+
		"\2\u0745\u0744\3\2\2\2\u0746\u0749\3\2\2\2\u0747\u0745\3\2\2\2\u0747\u0748"+
		"\3\2\2\2\u0748\u074a\3\2\2\2\u0749\u0747\3\2\2\2\u074a\u074b\7D\2\2\u074b"+
		"\u074c\7\f\2\2\u074c\u075f\3\2\2\2\u074d\u074e\7\63\2\2\u074e\u074f\7"+
		"D\2\2\u074f\u075f\7\f\2\2\u0750\u075f\7.\2\2\u0751\u0752\58\35\2\u0752"+
		"\u0753\7D\2\2\u0753\u0754\7.\2\2\u0754\u075f\3\2\2\2\u0755\u0756\7<\2"+
		"\2\u0756\u0757\5\u01ae\u00d8\2\u0757\u0758\7=\2\2\u0758\u075f\3\2\2\2"+
		"\u0759\u075f\5\u0184\u00c3\2\u075a\u075f\5\u018c\u00c7\2\u075b\u075f\5"+
		"\u0192\u00ca\2\u075c\u075f\5\u0198\u00cd\2\u075d\u075f\5\u01a0\u00d1\2"+
		"\u075e\u0742\3\2\2\2\u075e\u0743\3\2\2\2\u075e\u074d\3\2\2\2\u075e\u0750"+
		"\3\2\2\2\u075e\u0751\3\2\2\2\u075e\u0755\3\2\2\2\u075e\u0759\3\2\2\2\u075e"+
		"\u075a\3\2\2\2\u075e\u075b\3\2\2\2\u075e\u075c\3\2\2\2\u075e\u075d\3\2"+
		"\2\2\u075f\u0173\3\2\2\2\u0760\u0761\3\2\2\2\u0761\u0175\3\2\2\2\u0762"+
		"\u077e\5\2\2\2\u0763\u0767\58\35\2\u0764\u0766\5\u01c6\u00e4\2\u0765\u0764"+
		"\3\2\2\2\u0766\u0769\3\2\2\2\u0767\u0765\3\2\2\2\u0767\u0768\3\2\2\2\u0768"+
		"\u076a\3\2\2\2\u0769\u0767\3\2\2\2\u076a\u076b\7D\2\2\u076b\u076c\7\f"+
		"\2\2\u076c\u077e\3\2\2\2\u076d\u076e\7\63\2\2\u076e\u076f\7D\2\2\u076f"+
		"\u077e\7\f\2\2\u0770\u077e\7.\2\2\u0771\u0772\58\35\2\u0772\u0773\7D\2"+
		"\2\u0773\u0774\7.\2\2\u0774\u077e\3\2\2\2\u0775\u0776\7<\2\2\u0776\u0777"+
		"\5\u01ae\u00d8\2\u0777\u0778\7=\2\2\u0778\u077e\3\2\2\2\u0779\u077e\5"+
		"\u0184\u00c3\2\u077a\u077e\5\u018c\u00c7\2\u077b\u077e\5\u0198\u00cd\2"+
		"\u077c\u077e\5\u01a0\u00d1\2\u077d\u0762\3\2\2\2\u077d\u0763\3\2\2\2\u077d"+
		"\u076d\3\2\2\2\u077d\u0770\3\2\2\2\u077d\u0771\3\2\2\2\u077d\u0775\3\2"+
		"\2\2\u077d\u0779\3\2\2\2\u077d\u077a\3\2\2\2\u077d\u077b\3\2\2\2\u077d"+
		"\u077c\3\2\2\2\u077e\u0177\3\2\2\2\u077f\u0785\5\u0186\u00c4\2\u0780\u0785"+
		"\5\u018e\u00c8\2\u0781\u0785\5\u0194\u00cb\2\u0782\u0785\5\u019a\u00ce"+
		"\2\u0783\u0785\5\u01a2\u00d2\2\u0784\u077f\3\2\2\2\u0784\u0780\3\2\2\2"+
		"\u0784\u0781\3\2\2\2\u0784\u0782\3\2\2\2\u0784\u0783\3\2\2\2\u0785\u0179"+
		"\3\2\2\2\u0786\u0787\3\2\2\2\u0787\u017b\3\2\2\2\u0788\u078d\5\u0186\u00c4"+
		"\2\u0789\u078d\5\u018e\u00c8\2\u078a\u078d\5\u019a\u00ce\2\u078b\u078d"+
		"\5\u01a2\u00d2\2\u078c\u0788\3\2\2\2\u078c\u0789\3\2\2\2\u078c\u078a\3"+
		"\2\2\2\u078c\u078b\3\2\2\2\u078d\u017d\3\2\2\2\u078e\u07b5\5\2\2\2\u078f"+
		"\u0793\58\35\2\u0790\u0792\5\u01c6\u00e4\2\u0791\u0790\3\2\2\2\u0792\u0795"+
		"\3\2\2\2\u0793\u0791\3\2\2\2\u0793\u0794\3\2\2\2\u0794\u0796\3\2\2\2\u0795"+
		"\u0793\3\2\2\2\u0796\u0797\7D\2\2\u0797\u0798\7\f\2\2\u0798\u07b5\3\2"+
		"\2\2\u0799\u079d\5|?\2\u079a\u079c\5\u01c6\u00e4\2\u079b\u079a\3\2\2\2"+
		"\u079c\u079f\3\2\2\2\u079d\u079b\3\2\2\2\u079d\u079e\3\2\2\2\u079e\u07a0"+
		"\3\2\2\2\u079f\u079d\3\2\2\2\u07a0\u07a1\7D\2\2\u07a1\u07a2\7\f\2\2\u07a2"+
		"\u07b5\3\2\2\2\u07a3\u07a4\7\63\2\2\u07a4\u07a5\7D\2\2\u07a5\u07b5\7\f"+
		"\2\2\u07a6\u07b5\7.\2\2\u07a7\u07a8\58\35\2\u07a8\u07a9\7D\2\2\u07a9\u07aa"+
		"\7.\2\2\u07aa\u07b5\3\2\2\2\u07ab\u07ac\7<\2\2\u07ac\u07ad\5\u01ae\u00d8"+
		"\2\u07ad\u07ae\7=\2\2\u07ae\u07b5\3\2\2\2\u07af\u07b5\5\u0188\u00c5\2"+
		"\u07b0\u07b5\5\u0190\u00c9\2\u07b1\u07b5\5\u0196\u00cc\2\u07b2\u07b5\5"+
		"\u019c\u00cf\2\u07b3\u07b5\5\u01a4\u00d3\2\u07b4\u078e\3\2\2\2\u07b4\u078f"+
		"\3\2\2\2\u07b4\u0799\3\2\2\2\u07b4\u07a3\3\2\2\2\u07b4\u07a6\3\2\2\2\u07b4"+
		"\u07a7\3\2\2\2\u07b4\u07ab\3\2\2\2\u07b4\u07af\3\2\2\2\u07b4\u07b0\3\2"+
		"\2\2\u07b4\u07b1\3\2\2\2\u07b4\u07b2\3\2\2\2\u07b4\u07b3\3\2\2\2\u07b5"+
		"\u017f\3\2\2\2\u07b6\u07b7\3\2\2\2\u07b7\u0181\3\2\2\2\u07b8\u07de\5\2"+
		"\2\2\u07b9\u07bd\58\35\2\u07ba\u07bc\5\u01c6\u00e4\2\u07bb\u07ba\3\2\2"+
		"\2\u07bc\u07bf\3\2\2\2\u07bd\u07bb\3\2\2\2\u07bd\u07be\3\2\2\2\u07be\u07c0"+
		"\3\2\2\2\u07bf\u07bd\3\2\2\2\u07c0\u07c1\7D\2\2\u07c1\u07c2\7\f\2\2\u07c2"+
		"\u07de\3\2\2\2\u07c3\u07c7\5|?\2\u07c4\u07c6\5\u01c6\u00e4\2\u07c5\u07c4"+
		"\3\2\2\2\u07c6\u07c9\3\2\2\2\u07c7\u07c5\3\2\2\2\u07c7\u07c8\3\2\2\2\u07c8"+
		"\u07ca\3\2\2\2\u07c9\u07c7\3\2\2\2\u07ca\u07cb\7D\2\2\u07cb\u07cc\7\f"+
		"\2\2\u07cc\u07de\3\2\2\2\u07cd\u07ce\7\63\2\2\u07ce\u07cf\7D\2\2\u07cf"+
		"\u07de\7\f\2\2\u07d0\u07de\7.\2\2\u07d1\u07d2\58\35\2\u07d2\u07d3\7D\2"+
		"\2\u07d3\u07d4\7.\2\2\u07d4\u07de\3\2\2\2\u07d5\u07d6\7<\2\2\u07d6\u07d7"+
		"\5\u01ae\u00d8\2\u07d7\u07d8\7=\2\2\u07d8\u07de\3\2\2\2\u07d9\u07de\5"+
		"\u0188\u00c5\2\u07da\u07de\5\u0190\u00c9\2\u07db\u07de\5\u019c\u00cf\2"+
		"\u07dc\u07de\5\u01a4\u00d3\2\u07dd\u07b8\3\2\2\2\u07dd\u07b9\3\2\2\2\u07dd"+
		"\u07c3\3\2\2\2\u07dd\u07cd\3\2\2\2\u07dd\u07d0\3\2\2\2\u07dd\u07d1\3\2"+
		"\2\2\u07dd\u07d5\3\2\2\2\u07dd\u07d9\3\2\2\2\u07dd\u07da\3\2\2\2\u07dd"+
		"\u07db\3\2\2\2\u07dd\u07dc\3\2\2\2\u07de\u0183\3\2\2\2\u07df\u07e1\7\""+
		"\2\2\u07e0\u07e2\5,\27\2\u07e1\u07e0\3\2\2\2\u07e1\u07e2\3\2\2\2\u07e2"+
		"\u07e3\3\2\2\2\u07e3\u07e8\5\u00f4{\2\u07e4\u07e5\7D\2\2\u07e5\u07e7\5"+
		"\u00f4{\2\u07e6\u07e4\3\2\2\2\u07e7\u07ea\3\2\2\2\u07e8\u07e6\3\2\2\2"+
		"\u07e8\u07e9\3\2\2\2\u07e9\u07ec\3\2\2\2\u07ea\u07e8\3\2\2\2\u07eb\u07ed"+
		"\5\u018a\u00c6\2\u07ec\u07eb\3\2\2\2\u07ec\u07ed\3\2\2\2\u07ed\u07ee\3"+
		"\2\2\2\u07ee\u07f0\7<\2\2\u07ef\u07f1\5\u019e\u00d0\2\u07f0\u07ef\3\2"+
		"\2\2\u07f0\u07f1\3\2\2\2\u07f1\u07f2\3\2\2\2\u07f2\u07f4\7=\2\2\u07f3"+
		"\u07f5\5f\64\2\u07f4\u07f3\3\2\2\2\u07f4\u07f5\3\2\2\2\u07f5\u081b\3\2"+
		"\2\2\u07f6\u07f7\5<\37\2\u07f7\u07f8\7D\2\2\u07f8\u07fa\7\"\2\2\u07f9"+
		"\u07fb\5,\27\2\u07fa\u07f9\3\2\2\2\u07fa\u07fb\3\2\2\2\u07fb\u07fc\3\2"+
		"\2\2\u07fc\u07fe\5\u00f4{\2\u07fd\u07ff\5\u018a\u00c6\2\u07fe\u07fd\3"+
		"\2\2\2\u07fe\u07ff\3\2\2\2\u07ff\u0800\3\2\2\2\u0800\u0802\7<\2\2\u0801"+
		"\u0803\5\u019e\u00d0\2\u0802\u0801\3\2\2\2\u0802\u0803\3\2\2\2\u0803\u0804"+
		"\3\2\2\2\u0804\u0806\7=\2\2\u0805\u0807\5f\64\2\u0806\u0805\3\2\2\2\u0806"+
		"\u0807\3\2\2\2\u0807\u081b\3\2\2\2\u0808\u0809\5\u0170\u00b9\2\u0809\u080a"+
		"\7D\2\2\u080a\u080c\7\"\2\2\u080b\u080d\5,\27\2\u080c\u080b\3\2\2\2\u080c"+
		"\u080d\3\2\2\2\u080d\u080e\3\2\2\2\u080e\u0810\5\u00f4{\2\u080f\u0811"+
		"\5\u018a\u00c6\2\u0810\u080f\3\2\2\2\u0810\u0811\3\2\2\2\u0811\u0812\3"+
		"\2\2\2\u0812\u0814\7<\2\2\u0813\u0815\5\u019e\u00d0\2\u0814\u0813\3\2"+
		"\2\2\u0814\u0815\3\2\2\2\u0815\u0816\3\2\2\2\u0816\u0818\7=\2\2\u0817"+
		"\u0819\5f\64\2\u0818\u0817\3\2\2\2\u0818\u0819\3\2\2\2\u0819\u081b\3\2"+
		"\2\2\u081a\u07df\3\2\2\2\u081a\u07f6\3\2\2\2\u081a\u0808\3\2\2\2\u081b"+
		"\u0185\3\2\2\2\u081c\u081d\7D\2\2\u081d\u081f\7\"\2\2\u081e\u0820\5,\27"+
		"\2\u081f\u081e\3\2\2\2\u081f\u0820\3\2\2\2\u0820\u0821\3\2\2\2\u0821\u0823"+
		"\5\u00f4{\2\u0822\u0824\5\u018a\u00c6\2\u0823\u0822\3\2\2\2\u0823\u0824"+
		"\3\2\2\2\u0824\u0825\3\2\2\2\u0825\u0827\7<\2\2\u0826\u0828\5\u019e\u00d0"+
		"\2\u0827\u0826\3\2\2\2\u0827\u0828\3\2\2\2\u0828\u0829\3\2\2\2\u0829\u082b"+
		"\7=\2\2\u082a\u082c\5f\64\2\u082b\u082a\3\2\2\2\u082b\u082c\3\2\2\2\u082c"+
		"\u0187\3\2\2\2\u082d\u082f\7\"\2\2\u082e\u0830\5,\27\2\u082f\u082e\3\2"+
		"\2\2\u082f\u0830\3\2\2\2\u0830\u0831\3\2\2\2\u0831\u0836\5\u00f4{\2\u0832"+
		"\u0833\7D\2\2\u0833\u0835\5\u00f4{\2\u0834\u0832\3\2\2\2\u0835\u0838\3"+
		"\2\2\2\u0836\u0834\3\2\2\2\u0836\u0837\3\2\2\2\u0837\u083a\3\2\2\2\u0838"+
		"\u0836\3\2\2\2\u0839\u083b\5\u018a\u00c6\2\u083a\u0839\3\2\2\2\u083a\u083b"+
		"\3\2\2\2\u083b\u083c\3\2\2\2\u083c\u083e\7<\2\2\u083d\u083f\5\u019e\u00d0"+
		"\2\u083e\u083d\3\2\2\2\u083e\u083f\3\2\2\2\u083f\u0840\3\2\2\2\u0840\u0842"+
		"\7=\2\2\u0841\u0843\5f\64\2\u0842\u0841\3\2\2\2\u0842\u0843\3\2\2\2\u0843"+
		"\u0857\3\2\2\2\u0844\u0845\5<\37\2\u0845\u0846\7D\2\2\u0846\u0848\7\""+
		"\2\2\u0847\u0849\5,\27\2\u0848\u0847\3\2\2\2\u0848\u0849\3\2\2\2\u0849"+
		"\u084a\3\2\2\2\u084a\u084c\5\u00f4{\2\u084b\u084d\5\u018a\u00c6\2\u084c"+
		"\u084b\3\2\2\2\u084c\u084d\3\2\2\2\u084d\u084e\3\2\2\2\u084e\u0850\7<"+
		"\2\2\u084f\u0851\5\u019e\u00d0\2\u0850\u084f\3\2\2\2\u0850\u0851\3\2\2"+
		"\2\u0851\u0852\3\2\2\2\u0852\u0854\7=\2\2\u0853\u0855\5f\64\2\u0854\u0853"+
		"\3\2\2\2\u0854\u0855\3\2\2\2\u0855\u0857\3\2\2\2\u0856\u082d\3\2\2\2\u0856"+
		"\u0844\3\2\2\2\u0857\u0189\3\2\2\2\u0858\u085c\5,\27\2\u0859\u085a\7G"+
		"\2\2\u085a\u085c\7F\2\2\u085b\u0858\3\2\2\2\u085b\u0859\3\2\2\2\u085c"+
		"\u018b\3\2\2\2\u085d\u085e\5\u0170\u00b9\2\u085e\u085f\7D\2\2\u085f\u0860"+
		"\7i\2\2\u0860\u086b\3\2\2\2\u0861\u0862\7+\2\2\u0862\u0863\7D\2\2\u0863"+
		"\u086b\7i\2\2\u0864\u0865\58\35\2\u0865\u0866\7D\2\2\u0866\u0867\7+\2"+
		"\2\u0867\u0868\7D\2\2\u0868\u0869\7i\2\2\u0869\u086b\3\2\2\2\u086a\u085d"+
		"\3\2\2\2\u086a\u0861\3\2\2\2\u086a\u0864\3\2\2\2\u086b\u018d\3\2\2\2\u086c"+
		"\u086d\7D\2\2\u086d\u086e\7i\2\2\u086e\u018f\3\2\2\2\u086f\u0870\7+\2"+
		"\2\u0870\u0871\7D\2\2\u0871\u0879\7i\2\2\u0872\u0873\58\35\2\u0873\u0874"+
		"\7D\2\2\u0874\u0875\7+\2\2\u0875\u0876\7D\2\2\u0876\u0877\7i\2\2\u0877"+
		"\u0879\3\2\2\2\u0878\u086f\3\2\2\2\u0878\u0872\3\2\2\2\u0879\u0191\3\2"+
		"\2\2\u087a\u087b\5<\37\2\u087b\u087c\7@\2\2\u087c\u087d\5\u01ae\u00d8"+
		"\2\u087d\u087e\7A\2\2\u087e\u0885\3\2\2\2\u087f\u0880\5\u0176\u00bc\2"+
		"\u0880\u0881\7@\2\2\u0881\u0882\5\u01ae\u00d8\2\u0882\u0883\7A\2\2\u0883"+
		"\u0885\3\2\2\2\u0884\u087a\3\2\2\2\u0884\u087f\3\2\2\2\u0885\u088d\3\2"+
		"\2\2\u0886\u0887\5\u0174\u00bb\2\u0887\u0888\7@\2\2\u0888\u0889\5\u01ae"+
		"\u00d8\2\u0889\u088a\7A\2\2\u088a\u088c\3\2\2\2\u088b\u0886\3\2\2\2\u088c"+
		"\u088f\3\2\2\2\u088d\u088b\3\2\2\2\u088d\u088e\3\2\2\2\u088e\u0193\3\2"+
		"\2\2\u088f\u088d\3\2\2\2\u0890\u0891\5\u017c\u00bf\2\u0891\u0892\7@\2"+
		"\2\u0892\u0893\5\u01ae\u00d8\2\u0893\u0894\7A\2\2\u0894\u089c\3\2\2\2"+
		"\u0895\u0896\5\u017a\u00be\2\u0896\u0897\7@\2\2\u0897\u0898\5\u01ae\u00d8"+
		"\2\u0898\u0899\7A\2\2\u0899\u089b\3\2\2\2\u089a\u0895\3\2\2\2\u089b\u089e"+
		"\3\2\2\2\u089c\u089a\3\2\2\2\u089c\u089d\3\2\2\2\u089d\u0195\3\2\2\2\u089e"+
		"\u089c\3\2\2\2\u089f\u08a0\5<\37\2\u08a0\u08a1\7@\2\2\u08a1\u08a2\5\u01ae"+
		"\u00d8\2\u08a2\u08a3\7A\2\2\u08a3\u08aa\3\2\2\2\u08a4\u08a5\5\u0182\u00c2"+
		"\2\u08a5\u08a6\7@\2\2\u08a6\u08a7\5\u01ae\u00d8\2\u08a7\u08a8\7A\2\2\u08a8"+
		"\u08aa\3\2\2\2\u08a9\u089f\3\2\2\2\u08a9\u08a4\3\2\2\2\u08aa\u08b2\3\2"+
		"\2\2\u08ab\u08ac\5\u0180\u00c1\2\u08ac\u08ad\7@\2\2\u08ad\u08ae\5\u01ae"+
		"\u00d8\2\u08ae\u08af\7A\2\2\u08af\u08b1\3\2\2\2\u08b0\u08ab\3\2\2\2\u08b1"+
		"\u08b4\3\2\2\2\u08b2\u08b0\3\2\2\2\u08b2\u08b3\3\2\2\2\u08b3\u0197\3\2"+
		"\2\2\u08b4\u08b2\3\2\2\2\u08b5\u08b6\5> \2\u08b6\u08b8\7<\2\2\u08b7\u08b9"+
		"\5\u019e\u00d0\2\u08b8\u08b7\3\2\2\2\u08b8\u08b9\3\2\2\2\u08b9\u08ba\3"+
		"\2\2\2\u08ba\u08bb\7=\2\2\u08bb\u08fa\3\2\2\2\u08bc\u08bd\58\35\2\u08bd"+
		"\u08bf\7D\2\2\u08be\u08c0\5,\27\2\u08bf\u08be\3\2\2\2\u08bf\u08c0\3\2"+
		"\2\2\u08c0\u08c1\3\2\2\2\u08c1\u08c2\7i\2\2\u08c2\u08c4\7<\2\2\u08c3\u08c5"+
		"\5\u019e\u00d0\2\u08c4\u08c3\3\2\2\2\u08c4\u08c5\3\2\2\2\u08c5\u08c6\3"+
		"\2\2\2\u08c6\u08c7\7=\2\2\u08c7\u08fa\3\2\2\2\u08c8\u08c9\5<\37\2\u08c9"+
		"\u08cb\7D\2\2\u08ca\u08cc\5,\27\2\u08cb\u08ca\3\2\2\2\u08cb\u08cc\3\2"+
		"\2\2\u08cc\u08cd\3\2\2\2\u08cd\u08ce\7i\2\2\u08ce\u08d0\7<\2\2\u08cf\u08d1"+
		"\5\u019e\u00d0\2\u08d0\u08cf\3\2\2\2\u08d0\u08d1\3\2\2\2\u08d1\u08d2\3"+
		"\2\2\2\u08d2\u08d3\7=\2\2\u08d3\u08fa\3\2\2\2\u08d4\u08d5\5\u0170\u00b9"+
		"\2\u08d5\u08d7\7D\2\2\u08d6\u08d8\5,\27\2\u08d7\u08d6\3\2\2\2\u08d7\u08d8"+
		"\3\2\2\2\u08d8\u08d9\3\2\2\2\u08d9\u08da\7i\2\2\u08da\u08dc\7<\2\2\u08db"+
		"\u08dd\5\u019e\u00d0\2\u08dc\u08db\3\2\2\2\u08dc\u08dd\3\2\2\2\u08dd\u08de"+
		"\3\2\2\2\u08de\u08df\7=\2\2\u08df\u08fa\3\2\2\2\u08e0\u08e1\7+\2\2\u08e1"+
		"\u08e3\7D\2\2\u08e2\u08e4\5,\27\2\u08e3\u08e2\3\2\2\2\u08e3\u08e4\3\2"+
		"\2\2\u08e4\u08e5\3\2\2\2\u08e5\u08e6\7i\2\2\u08e6\u08e8\7<\2\2\u08e7\u08e9"+
		"\5\u019e\u00d0\2\u08e8\u08e7\3\2\2\2\u08e8\u08e9\3\2\2\2\u08e9\u08ea\3"+
		"\2\2\2\u08ea\u08fa\7=\2\2\u08eb\u08ec\58\35\2\u08ec\u08ed\7D\2\2\u08ed"+
		"\u08ee\7+\2\2\u08ee\u08f0\7D\2\2\u08ef\u08f1\5,\27\2\u08f0\u08ef\3\2\2"+
		"\2\u08f0\u08f1\3\2\2\2\u08f1\u08f2\3\2\2\2\u08f2\u08f3\7i\2\2\u08f3\u08f5"+
		"\7<\2\2\u08f4\u08f6\5\u019e\u00d0\2\u08f5\u08f4\3\2\2\2\u08f5\u08f6\3"+
		"\2\2\2\u08f6\u08f7\3\2\2\2\u08f7\u08f8\7=\2\2\u08f8\u08fa\3\2\2\2\u08f9"+
		"\u08b5\3\2\2\2\u08f9\u08bc\3\2\2\2\u08f9\u08c8\3\2\2\2\u08f9\u08d4\3\2"+
		"\2\2\u08f9\u08e0\3\2\2\2\u08f9\u08eb\3\2\2\2\u08fa\u0199\3\2\2\2\u08fb"+
		"\u08fd\7D\2\2\u08fc\u08fe\5,\27\2\u08fd\u08fc\3\2\2\2\u08fd\u08fe\3\2"+
		"\2\2\u08fe\u08ff\3\2\2\2\u08ff\u0900\7i\2\2\u0900\u0902\7<\2\2\u0901\u0903"+
		"\5\u019e\u00d0\2\u0902\u0901\3\2\2\2\u0902\u0903\3\2\2\2\u0903\u0904\3"+
		"\2\2\2\u0904\u0905\7=\2\2\u0905\u019b\3\2\2\2\u0906\u0907\5> \2\u0907"+
		"\u0909\7<\2\2\u0908\u090a\5\u019e\u00d0\2\u0909\u0908\3\2\2\2\u0909\u090a"+
		"\3\2\2\2\u090a\u090b\3\2\2\2\u090b\u090c\7=\2\2\u090c\u093f\3\2\2\2\u090d"+
		"\u090e\58\35\2\u090e\u0910\7D\2\2\u090f\u0911\5,\27\2\u0910\u090f\3\2"+
		"\2\2\u0910\u0911\3\2\2\2\u0911\u0912\3\2\2\2\u0912\u0913\7i\2\2\u0913"+
		"\u0915\7<\2\2\u0914\u0916\5\u019e\u00d0\2\u0915\u0914\3\2\2\2\u0915\u0916"+
		"\3\2\2\2\u0916\u0917\3\2\2\2\u0917\u0918\7=\2\2\u0918\u093f\3\2\2\2\u0919"+
		"\u091a\5<\37\2\u091a\u091c\7D\2\2\u091b\u091d\5,\27\2\u091c\u091b\3\2"+
		"\2\2\u091c\u091d\3\2\2\2\u091d\u091e\3\2\2\2\u091e\u091f\7i\2\2\u091f"+
		"\u0921\7<\2\2\u0920\u0922\5\u019e\u00d0\2\u0921\u0920\3\2\2\2\u0921\u0922"+
		"\3\2\2\2\u0922\u0923\3\2\2\2\u0923\u0924\7=\2\2\u0924\u093f\3\2\2\2\u0925"+
		"\u0926\7+\2\2\u0926\u0928\7D\2\2\u0927\u0929\5,\27\2\u0928\u0927\3\2\2"+
		"\2\u0928\u0929\3\2\2\2\u0929\u092a\3\2\2\2\u092a\u092b\7i\2\2\u092b\u092d"+
		"\7<\2\2\u092c\u092e\5\u019e\u00d0\2\u092d\u092c\3\2\2\2\u092d\u092e\3"+
		"\2\2\2\u092e\u092f\3\2\2\2\u092f\u093f\7=\2\2\u0930\u0931\58\35\2\u0931"+
		"\u0932\7D\2\2\u0932\u0933\7+\2\2\u0933\u0935\7D\2\2\u0934\u0936\5,\27"+
		"\2\u0935\u0934\3\2\2\2\u0935\u0936\3\2\2\2\u0936\u0937\3\2\2\2\u0937\u0938"+
		"\7i\2\2\u0938\u093a\7<\2\2\u0939\u093b\5\u019e\u00d0\2\u093a\u0939\3\2"+
		"\2\2\u093a\u093b\3\2\2\2\u093b\u093c\3\2\2\2\u093c\u093d\7=\2\2\u093d"+
		"\u093f\3\2\2\2\u093e\u0906\3\2\2\2\u093e\u090d\3\2\2\2\u093e\u0919\3\2"+
		"\2\2\u093e\u0925\3\2\2\2\u093e\u0930\3\2\2\2\u093f\u019d\3\2\2\2\u0940"+
		"\u0945\5\u01ae\u00d8\2\u0941\u0942\7C\2\2\u0942\u0944\5\u01ae\u00d8\2"+
		"\u0943\u0941\3\2\2\2\u0944\u0947\3\2\2\2\u0945\u0943\3\2\2\2\u0945\u0946"+
		"\3\2\2\2\u0946\u019f\3\2\2\2\u0947\u0945\3\2\2\2\u0948\u0949\5<\37\2\u0949"+
		"\u094b\7]\2\2\u094a\u094c\5,\27\2\u094b\u094a\3\2\2\2\u094b\u094c\3\2"+
		"\2\2\u094c\u094d\3\2\2\2\u094d\u094e\7i\2\2\u094e\u0978\3\2\2\2\u094f"+
		"\u0950\5\16\b\2\u0950\u0952\7]\2\2\u0951\u0953\5,\27\2\u0952\u0951\3\2"+
		"\2\2\u0952\u0953\3\2\2\2\u0953\u0954\3\2\2\2\u0954\u0955\7i\2\2\u0955"+
		"\u0978\3\2\2\2\u0956\u0957\5\u0170\u00b9\2\u0957\u0959\7]\2\2\u0958\u095a"+
		"\5,\27\2\u0959\u0958\3\2\2\2\u0959\u095a\3\2\2\2\u095a\u095b\3\2\2\2\u095b"+
		"\u095c\7i\2\2\u095c\u0978\3\2\2\2\u095d\u095e\7+\2\2\u095e";
	private static final String _serializedATNSegment1 =
		"\u0960\7]\2\2\u095f\u0961\5,\27\2\u0960\u095f\3\2\2\2\u0960\u0961\3\2"+
		"\2\2\u0961\u0962\3\2\2\2\u0962\u0978\7i\2\2\u0963\u0964\58\35\2\u0964"+
		"\u0965\7D\2\2\u0965\u0966\7+\2\2\u0966\u0968\7]\2\2\u0967\u0969\5,\27"+
		"\2\u0968\u0967\3\2\2\2\u0968\u0969\3\2\2\2\u0969\u096a\3\2\2\2\u096a\u096b"+
		"\7i\2\2\u096b\u0978\3\2\2\2\u096c\u096d\5\22\n\2\u096d\u096f\7]\2\2\u096e"+
		"\u0970\5,\27\2\u096f\u096e\3\2\2\2\u096f\u0970\3\2\2\2\u0970\u0971\3\2"+
		"\2\2\u0971\u0972\7\"\2\2\u0972\u0978\3\2\2\2\u0973\u0974\5 \21\2\u0974"+
		"\u0975\7]\2\2\u0975\u0976\7\"\2\2\u0976\u0978\3\2\2\2\u0977\u0948\3\2"+
		"\2\2\u0977\u094f\3\2\2\2\u0977\u0956\3\2\2\2\u0977\u095d\3\2\2\2\u0977"+
		"\u0963\3\2\2\2\u0977\u096c\3\2\2\2\u0977\u0973\3\2\2\2\u0978\u01a1\3\2"+
		"\2\2\u0979\u097b\7]\2\2\u097a\u097c\5,\27\2\u097b\u097a\3\2\2\2\u097b"+
		"\u097c\3\2\2\2\u097c\u097d\3\2\2\2\u097d\u097e\7i\2\2\u097e\u01a3\3\2"+
		"\2\2\u097f\u0980\5<\37\2\u0980\u0982\7]\2\2\u0981\u0983\5,\27\2\u0982"+
		"\u0981\3\2\2\2\u0982\u0983\3\2\2\2\u0983\u0984\3\2\2\2\u0984\u0985\7i"+
		"\2\2\u0985\u09a8\3\2\2\2\u0986\u0987\5\16\b\2\u0987\u0989\7]\2\2\u0988"+
		"\u098a\5,\27\2\u0989\u0988\3\2\2\2\u0989\u098a\3\2\2\2\u098a\u098b\3\2"+
		"\2\2\u098b\u098c\7i\2\2\u098c\u09a8\3\2\2\2\u098d\u098e\7+\2\2\u098e\u0990"+
		"\7]\2\2\u098f\u0991\5,\27\2\u0990\u098f\3\2\2\2\u0990\u0991\3\2\2\2\u0991"+
		"\u0992\3\2\2\2\u0992\u09a8\7i\2\2\u0993\u0994\58\35\2\u0994\u0995\7D\2"+
		"\2\u0995\u0996\7+\2\2\u0996\u0998\7]\2\2\u0997\u0999\5,\27\2\u0998\u0997"+
		"\3\2\2\2\u0998\u0999\3\2\2\2\u0999\u099a\3\2\2\2\u099a\u099b\7i\2\2\u099b"+
		"\u09a8\3\2\2\2\u099c\u099d\5\22\n\2\u099d\u099f\7]\2\2\u099e\u09a0\5,"+
		"\27\2\u099f\u099e\3\2\2\2\u099f\u09a0\3\2\2\2\u09a0\u09a1\3\2\2\2\u09a1"+
		"\u09a2\7\"\2\2\u09a2\u09a8\3\2\2\2\u09a3\u09a4\5 \21\2\u09a4\u09a5\7]"+
		"\2\2\u09a5\u09a6\7\"\2\2\u09a6\u09a8\3\2\2\2\u09a7\u097f\3\2\2\2\u09a7"+
		"\u0986\3\2\2\2\u09a7\u098d\3\2\2\2\u09a7\u0993\3\2\2\2\u09a7\u099c\3\2"+
		"\2\2\u09a7\u09a3\3\2\2\2\u09a8\u01a5\3\2\2\2\u09a9\u09aa\7\"\2\2\u09aa"+
		"\u09ab\5\6\4\2\u09ab\u09ad\5\u01a8\u00d5\2\u09ac\u09ae\5\"\22\2\u09ad"+
		"\u09ac\3\2\2\2\u09ad\u09ae\3\2\2\2\u09ae\u09c0\3\2\2\2\u09af\u09b0\7\""+
		"\2\2\u09b0\u09b1\5\20\t\2\u09b1\u09b3\5\u01a8\u00d5\2\u09b2\u09b4\5\""+
		"\22\2\u09b3\u09b2\3\2\2\2\u09b3\u09b4\3\2\2\2\u09b4\u09c0\3\2\2\2\u09b5"+
		"\u09b6\7\"\2\2\u09b6\u09b7\5\6\4\2\u09b7\u09b8\5\"\22\2\u09b8\u09b9\5"+
		"\u0108\u0085\2\u09b9\u09c0\3\2\2\2\u09ba\u09bb\7\"\2\2\u09bb\u09bc\5\20"+
		"\t\2\u09bc\u09bd\5\"\22\2\u09bd\u09be\5\u0108\u0085\2\u09be\u09c0\3\2"+
		"\2\2\u09bf\u09a9\3\2\2\2\u09bf\u09af\3\2\2\2\u09bf\u09b5\3\2\2\2\u09bf"+
		"\u09ba\3\2\2\2\u09c0\u01a7\3\2\2\2\u09c1\u09c5\5\u01aa\u00d6\2\u09c2\u09c4"+
		"\5\u01aa\u00d6\2\u09c3\u09c2\3\2\2\2\u09c4\u09c7\3\2\2\2\u09c5\u09c3\3"+
		"\2\2\2\u09c5\u09c6\3\2\2\2\u09c6\u01a9\3\2\2\2\u09c7\u09c5\3\2\2\2\u09c8"+
		"\u09ca\5\u00f2z\2\u09c9\u09c8\3\2\2\2\u09ca\u09cd\3\2\2\2\u09cb\u09c9"+
		"\3\2\2\2\u09cb\u09cc\3\2\2\2\u09cc\u09ce\3\2\2\2\u09cd\u09cb\3\2\2\2\u09ce"+
		"\u09cf\7@\2\2\u09cf\u09d0\5\u01ae\u00d8\2\u09d0\u09d1\7A\2\2\u09d1\u01ab"+
		"\3\2\2\2\u09d2\u09d3\5\u01ae\u00d8\2\u09d3\u01ad\3\2\2\2\u09d4\u09d7\5"+
		"\u01b0\u00d9\2\u09d5\u09d7\5\u01b8\u00dd\2\u09d6\u09d4\3\2\2\2\u09d6\u09d5"+
		"\3\2\2\2\u09d7\u01af\3\2\2\2\u09d8\u09d9\5\u01b2\u00da\2\u09d9\u09da\7"+
		"\\\2\2\u09da\u09db\5\u01b6\u00dc\2\u09db\u01b1\3\2\2\2\u09dc\u09e7\7i"+
		"\2\2\u09dd\u09df\7<\2\2\u09de\u09e0\5\u009eP\2\u09df\u09de\3\2\2\2\u09df"+
		"\u09e0\3\2\2\2\u09e0\u09e1\3\2\2\2\u09e1\u09e7\7=\2\2\u09e2\u09e3\7<\2"+
		"\2\u09e3\u09e4\5\u01b4\u00db\2\u09e4\u09e5\7=\2\2\u09e5\u09e7\3\2\2\2"+
		"\u09e6\u09dc\3\2\2\2\u09e6\u09dd\3\2\2\2\u09e6\u09e2\3\2\2\2\u09e7\u01b3"+
		"\3\2\2\2\u09e8\u09ed\7i\2\2\u09e9\u09ea\7C\2\2\u09ea\u09ec\7i\2\2\u09eb"+
		"\u09e9\3\2\2\2\u09ec\u09ef\3\2\2\2\u09ed\u09eb\3\2\2\2\u09ed\u09ee\3\2"+
		"\2\2\u09ee\u01b5\3\2\2\2\u09ef\u09ed\3\2\2\2\u09f0\u09f3\5\u01ae\u00d8"+
		"\2\u09f1\u09f3\5\u010c\u0087\2\u09f2\u09f0\3\2\2\2\u09f2\u09f1\3\2\2\2"+
		"\u09f3\u01b7\3\2\2\2\u09f4\u09f7\5\u01c8\u00e5\2\u09f5\u09f7\5\u01ba\u00de"+
		"\2\u09f6\u09f4\3\2\2\2\u09f6\u09f5\3\2\2\2\u09f7\u01b9\3\2\2\2\u09f8\u09f9"+
		"\5\u01bc\u00df\2\u09f9\u09fa\5\u01be\u00e0\2\u09fa\u09fb\5\u01ae\u00d8"+
		"\2\u09fb\u01bb\3\2\2\2\u09fc\u0a00\5<\37\2\u09fd\u0a00\5\u018c\u00c7\2"+
		"\u09fe\u0a00\5\u0192\u00ca\2\u09ff\u09fc\3\2\2\2\u09ff\u09fd\3\2\2\2\u09ff"+
		"\u09fe\3\2\2\2\u0a00\u01bd\3\2\2\2\u0a01\u0a02\t\5\2\2\u0a02\u01bf\3\2"+
		"\2\2\u0a03\u0a04\t\6\2\2\u0a04\u01c1\3\2\2\2\u0a05\u0a06\t\7\2\2\u0a06"+
		"\u01c3\3\2\2\2\u0a07\u0a08\t\b\2\2\u0a08\u01c5\3\2\2\2\u0a09\u0a0a\7\3"+
		"\2\2\u0a0a\u01c7\3\2\2\2\u0a0b\u0a19\5\u01ca\u00e6\2\u0a0c\u0a0d\5\u01ca"+
		"\u00e6\2\u0a0d\u0a0e\7J\2\2\u0a0e\u0a0f\5\u01ae\u00d8\2\u0a0f\u0a10\7"+
		"K\2\2\u0a10\u0a11\5\u01c8\u00e5\2\u0a11\u0a19\3\2\2\2\u0a12\u0a13\5\u01ca"+
		"\u00e6\2\u0a13\u0a14\7J\2\2\u0a14\u0a15\5\u01ae\u00d8\2\u0a15\u0a16\7"+
		"K\2\2\u0a16\u0a17\5\u01b0\u00d9\2\u0a17\u0a19\3\2\2\2\u0a18\u0a0b\3\2"+
		"\2\2\u0a18\u0a0c\3\2\2\2\u0a18\u0a12\3\2\2\2\u0a19\u01c9\3\2\2\2\u0a1a"+
		"\u0a1b\b\u00e6\1\2\u0a1b\u0a1c\5\u01cc\u00e7\2\u0a1c\u0a22\3\2\2\2\u0a1d"+
		"\u0a1e\f\3\2\2\u0a1e\u0a1f\7Q\2\2\u0a1f\u0a21\5\u01cc\u00e7\2\u0a20\u0a1d"+
		"\3\2\2\2\u0a21\u0a24\3\2\2\2\u0a22\u0a20\3\2\2\2\u0a22\u0a23\3\2\2\2\u0a23"+
		"\u01cb\3\2\2\2\u0a24\u0a22\3\2\2\2\u0a25\u0a26\b\u00e7\1\2\u0a26\u0a27"+
		"\5\u01ce\u00e8\2\u0a27\u0a2d\3\2\2\2\u0a28\u0a29\f\3\2\2\u0a29\u0a2a\7"+
		"P\2\2\u0a2a\u0a2c\5\u01ce\u00e8\2\u0a2b\u0a28\3\2\2\2\u0a2c\u0a2f\3\2"+
		"\2\2\u0a2d\u0a2b\3\2\2\2\u0a2d\u0a2e\3\2\2\2\u0a2e\u01cd\3\2\2\2\u0a2f"+
		"\u0a2d\3\2\2\2\u0a30\u0a31\b\u00e8\1\2\u0a31\u0a32\5\u01d0\u00e9\2\u0a32"+
		"\u0a38\3\2\2\2\u0a33\u0a34\f\3\2\2\u0a34\u0a35\7Y\2\2\u0a35\u0a37\5\u01d0"+
		"\u00e9\2\u0a36\u0a33\3\2\2\2\u0a37\u0a3a\3\2\2\2\u0a38\u0a36\3\2\2\2\u0a38"+
		"\u0a39\3\2\2\2\u0a39\u01cf\3\2\2\2\u0a3a\u0a38\3\2\2\2\u0a3b\u0a3c\b\u00e9"+
		"\1\2\u0a3c\u0a3d\5\u01d2\u00ea\2\u0a3d\u0a43\3\2\2\2\u0a3e\u0a3f\f\3\2"+
		"\2\u0a3f\u0a40\7Z\2\2\u0a40\u0a42\5\u01d2\u00ea\2\u0a41\u0a3e\3\2\2\2"+
		"\u0a42\u0a45\3\2\2\2\u0a43\u0a41\3\2\2\2\u0a43\u0a44\3\2\2\2\u0a44\u01d1"+
		"\3\2\2\2\u0a45\u0a43\3\2\2\2\u0a46\u0a47\b\u00ea\1\2\u0a47\u0a48\5\u01d4"+
		"\u00eb\2\u0a48\u0a4e\3\2\2\2\u0a49\u0a4a\f\3\2\2\u0a4a\u0a4b\7X\2\2\u0a4b"+
		"\u0a4d\5\u01d4\u00eb\2\u0a4c\u0a49\3\2\2\2\u0a4d\u0a50\3\2\2\2\u0a4e\u0a4c"+
		"\3\2\2\2\u0a4e\u0a4f\3\2\2\2\u0a4f\u01d3\3\2\2\2\u0a50\u0a4e\3\2\2\2\u0a51"+
		"\u0a52\b\u00eb\1\2\u0a52\u0a53\5\u01d6\u00ec\2\u0a53\u0a5c\3\2\2\2\u0a54"+
		"\u0a55\f\4\2\2\u0a55\u0a56\7L\2\2\u0a56\u0a5b\5\u01d6\u00ec\2\u0a57\u0a58"+
		"\f\3\2\2\u0a58\u0a59\7O\2\2\u0a59\u0a5b\5\u01d6\u00ec\2\u0a5a\u0a54\3"+
		"\2\2\2\u0a5a\u0a57\3\2\2\2\u0a5b\u0a5e\3\2\2\2\u0a5c\u0a5a\3\2\2\2\u0a5c"+
		"\u0a5d\3\2\2\2\u0a5d\u01d5\3\2\2\2\u0a5e\u0a5c\3\2\2\2\u0a5f\u0a60\b\u00ec"+
		"\1\2\u0a60\u0a61\5\u01d8\u00ed\2\u0a61\u0a78\3\2\2\2\u0a62\u0a63\f\7\2"+
		"\2\u0a63\u0a64\5\u01c2\u00e2\2\u0a64\u0a65\5\u01d8\u00ed\2\u0a65\u0a77"+
		"\3\2\2\2\u0a66\u0a67\f\6\2\2\u0a67\u0a68\5\u01c2\u00e2\2\u0a68\u0a69\5"+
		"\u01d8\u00ed\2\u0a69\u0a77\3\2\2\2\u0a6a\u0a6b\f\5\2\2\u0a6b\u0a6c\5\u01c2"+
		"\u00e2\2\u0a6c\u0a6d\5\u01d8\u00ed\2\u0a6d\u0a77\3\2\2\2\u0a6e\u0a6f\f"+
		"\4\2\2\u0a6f\u0a70\5\u01c2\u00e2\2\u0a70\u0a71\5\u01d8\u00ed\2\u0a71\u0a77"+
		"\3\2\2\2\u0a72\u0a73\f\3\2\2\u0a73\u0a74\5\u01c2\u00e2\2\u0a74\u0a75\5"+
		"\16\b\2\u0a75\u0a77\3\2\2\2\u0a76\u0a62\3\2\2\2\u0a76\u0a66\3\2\2\2\u0a76"+
		"\u0a6a\3\2\2\2\u0a76\u0a6e\3\2\2\2\u0a76\u0a72\3\2\2\2\u0a77\u0a7a\3\2"+
		"\2\2\u0a78\u0a76\3\2\2\2\u0a78\u0a79\3\2\2\2\u0a79\u01d7\3\2\2\2\u0a7a"+
		"\u0a78\3\2\2\2\u0a7b\u0a7c\b\u00ed\1\2\u0a7c\u0a7d\5\u01dc\u00ef\2\u0a7d"+
		"\u0a8c\3\2\2\2\u0a7e\u0a7f\f\5\2\2\u0a7f\u0a80\5\u01da\u00ee\2\u0a80\u0a81"+
		"\5\u01dc\u00ef\2\u0a81\u0a8b\3\2\2\2\u0a82\u0a83\f\4\2\2\u0a83\u0a84\5"+
		"\u01da\u00ee\2\u0a84\u0a85\5\u01dc\u00ef\2\u0a85\u0a8b\3\2\2\2\u0a86\u0a87"+
		"\f\3\2\2\u0a87\u0a88\5\u01da\u00ee\2\u0a88\u0a89\5\u01dc\u00ef\2\u0a89"+
		"\u0a8b\3\2\2\2\u0a8a\u0a7e\3\2\2\2\u0a8a\u0a82\3\2\2\2\u0a8a\u0a86\3\2"+
		"\2\2\u0a8b\u0a8e\3\2\2\2\u0a8c\u0a8a\3\2\2\2\u0a8c\u0a8d\3\2\2\2\u0a8d"+
		"\u01d9\3\2\2\2\u0a8e\u0a8c\3\2\2\2\u0a8f\u0a90\7G\2\2\u0a90\u0a97\7G\2"+
		"\2\u0a91\u0a92\7F\2\2\u0a92\u0a97\7F\2\2\u0a93\u0a94\7F\2\2\u0a94\u0a95"+
		"\7F\2\2\u0a95\u0a97\7F\2\2\u0a96\u0a8f\3\2\2\2\u0a96\u0a91\3\2\2\2\u0a96"+
		"\u0a93\3\2\2\2\u0a97\u01db\3\2\2\2\u0a98\u0a99\b\u00ef\1\2\u0a99\u0a9a"+
		"\5\u01de\u00f0\2\u0a9a\u0aa5\3\2\2\2\u0a9b\u0a9c\f\4\2\2\u0a9c\u0a9d\5"+
		"\u01c0\u00e1\2\u0a9d\u0a9e\5\u01de\u00f0\2\u0a9e\u0aa4\3\2\2\2\u0a9f\u0aa0"+
		"\f\3\2\2\u0aa0\u0aa1\5\u01c0\u00e1\2\u0aa1\u0aa2\5\u01de\u00f0\2\u0aa2"+
		"\u0aa4\3\2\2\2\u0aa3\u0a9b\3\2\2\2\u0aa3\u0a9f\3\2\2\2\u0aa4\u0aa7\3\2"+
		"\2\2\u0aa5\u0aa3\3\2\2\2\u0aa5\u0aa6\3\2\2\2\u0aa6\u01dd\3\2\2\2\u0aa7"+
		"\u0aa5\3\2\2\2\u0aa8\u0aa9\b\u00f0\1\2\u0aa9\u0aaa\5\u01e0\u00f1\2\u0aaa"+
		"\u0ab9\3\2\2\2\u0aab\u0aac\f\5\2\2\u0aac\u0aad\5\u01c4\u00e3\2\u0aad\u0aae"+
		"\5\u01e0\u00f1\2\u0aae\u0ab8\3\2\2\2\u0aaf\u0ab0\f\4\2\2\u0ab0\u0ab1\5"+
		"\u01c4\u00e3\2\u0ab1\u0ab2\5\u01e0\u00f1\2\u0ab2\u0ab8\3\2\2\2\u0ab3\u0ab4"+
		"\f\3\2\2\u0ab4\u0ab5\5\u01c4\u00e3\2\u0ab5\u0ab6\5\u01e0\u00f1\2\u0ab6"+
		"\u0ab8\3\2\2\2\u0ab7\u0aab\3\2\2\2\u0ab7\u0aaf\3\2\2\2\u0ab7\u0ab3\3\2"+
		"\2\2\u0ab8\u0abb\3\2\2\2\u0ab9\u0ab7\3\2\2\2\u0ab9\u0aba\3\2\2\2\u0aba"+
		"\u01df\3\2\2\2\u0abb\u0ab9\3\2\2\2\u0abc\u0ac6\5\u01e2\u00f2\2\u0abd\u0ac6"+
		"\5\u01e4\u00f3\2\u0abe\u0abf\5\u01c0\u00e1\2\u0abf\u0ac0\5\u01e0\u00f1"+
		"\2\u0ac0\u0ac6\3\2\2\2\u0ac1\u0ac2\5\u01c0\u00e1\2\u0ac2\u0ac3\5\u01e0"+
		"\u00f1\2\u0ac3\u0ac6\3\2\2\2\u0ac4\u0ac6\5\u01e6\u00f4\2\u0ac5\u0abc\3"+
		"\2\2\2\u0ac5\u0abd\3\2\2\2\u0ac5\u0abe\3\2\2\2\u0ac5\u0ac1\3\2\2\2\u0ac5"+
		"\u0ac4\3\2\2\2\u0ac6\u01e1\3\2\2\2\u0ac7\u0ac8\7R\2\2\u0ac8\u0ac9\5\u01e0"+
		"\u00f1\2\u0ac9\u01e3\3\2\2\2\u0aca\u0acb\7S\2\2\u0acb\u0acc\5\u01e0\u00f1"+
		"\2\u0acc\u01e5\3\2\2\2\u0acd\u0ad4\5\u01e8\u00f5\2\u0ace\u0acf\7I\2\2"+
		"\u0acf\u0ad4\5\u01e0\u00f1\2\u0ad0\u0ad1\7H\2\2\u0ad1\u0ad4\5\u01e0\u00f1"+
		"\2\u0ad2\u0ad4\5\u01f2\u00fa\2\u0ad3\u0acd\3\2\2\2\u0ad3\u0ace\3\2\2\2"+
		"\u0ad3\u0ad0\3\2\2\2\u0ad3\u0ad2\3\2\2\2\u0ad4\u01e7\3\2\2\2\u0ad5\u0ad8"+
		"\5\u0170\u00b9\2\u0ad6\u0ad8\5<\37\2\u0ad7\u0ad5\3\2\2\2\u0ad7\u0ad6\3"+
		"\2\2\2\u0ad8\u0add\3\2\2\2\u0ad9\u0adc\5\u01ec\u00f7\2\u0ada\u0adc\5\u01f0"+
		"\u00f9\2\u0adb\u0ad9\3\2\2\2\u0adb\u0ada\3\2\2\2\u0adc\u0adf\3\2\2\2\u0add"+
		"\u0adb\3\2\2\2\u0add\u0ade\3\2\2\2\u0ade\u01e9\3\2\2\2\u0adf\u0add\3\2"+
		"\2\2\u0ae0\u0ae1\5\u01e8\u00f5\2\u0ae1\u0ae2\7R\2\2\u0ae2\u01eb\3\2\2"+
		"\2\u0ae3\u0ae4\7R\2\2\u0ae4\u01ed\3\2\2\2\u0ae5\u0ae6\5\u01e8\u00f5\2"+
		"\u0ae6\u0ae7\7S\2\2\u0ae7\u01ef\3\2\2\2\u0ae8\u0ae9\7S\2\2\u0ae9\u01f1"+
		"\3\2\2\2\u0aea\u0aeb\7<\2\2\u0aeb\u0aec\5\6\4\2\u0aec\u0aed\7=\2\2\u0aed"+
		"\u0aee\5\u01e0\u00f1\2\u0aee\u0b06\3\2\2\2\u0aef\u0af0\7<\2\2\u0af0\u0af4"+
		"\5\16\b\2\u0af1\u0af3\5*\26\2\u0af2\u0af1\3\2\2\2\u0af3\u0af6\3\2\2\2"+
		"\u0af4\u0af2\3\2\2\2\u0af4\u0af5\3\2\2\2\u0af5\u0af7\3\2\2\2\u0af6\u0af4"+
		"\3\2\2\2\u0af7\u0af8\7=\2\2\u0af8\u0af9\5\u01e6\u00f4\2\u0af9\u0b06\3"+
		"\2\2\2\u0afa\u0afb\7<\2\2\u0afb\u0aff\5\16\b\2\u0afc\u0afe\5*\26\2\u0afd"+
		"\u0afc\3\2\2\2\u0afe\u0b01\3\2\2\2\u0aff\u0afd\3\2\2\2\u0aff\u0b00\3\2"+
		"\2\2\u0b00\u0b02\3\2\2\2\u0b01\u0aff\3\2\2\2\u0b02\u0b03\7=\2\2\u0b03"+
		"\u0b04\5\u01b0\u00d9\2\u0b04\u0b06\3\2\2\2\u0b05\u0aea\3\2\2\2\u0b05\u0aef"+
		"\3\2\2\2\u0b05\u0afa\3\2\2\2\u0b06\u01f3\3\2\2\2\u0137\u01f8\u01fd\u0204"+
		"\u0208\u020c\u0215\u0219\u021d\u021f\u0224\u022a\u022c\u0231\u0235\u0248"+
		"\u024e\u0254\u0259\u0264\u0267\u0275\u027a\u027f\u0284\u028a\u0294\u029c"+
		"\u02a6\u02ae\u02ba\u02be\u02c3\u02c9\u02d1\u02da\u02e5\u0302\u0306\u030c"+
		"\u030f\u0312\u0319\u0324\u032f\u033d\u0344\u034d\u0354\u035e\u0369\u0370"+
		"\u0376\u037a\u037e\u0382\u0386\u038b\u038f\u0393\u0395\u039a\u03a0\u03a2"+
		"\u03a7\u03ab\u03be\u03c7\u03d4\u03d9\u03df\u03e5\u03e7\u03eb\u03f0\u03f4"+
		"\u03fb\u0402\u040a\u040d\u0412\u041a\u041f\u0426\u042d\u0432\u0438\u0444"+
		"\u0449\u044d\u0457\u045e\u0465\u0468\u046d\u0475\u0478\u047d\u0482\u0487"+
		"\u048c\u0493\u0498\u04a0\u04a5\u04aa\u04b0\u04b6\u04b9\u04bc\u04c5\u04cb"+
		"\u04d1\u04d4\u04d7\u04df\u04e4\u04ea\u04ed\u04f4\u04fe\u0507\u0511\u0516"+
		"\u0521\u0526\u0532\u0537\u0543\u054d\u0552\u055a\u055d\u0564\u056c\u0571"+
		"\u0579\u0582\u058b\u0595\u0599\u059c\u05a5\u05b3\u05b6\u05bf\u05c4\u05cc"+
		"\u05d2\u05da\u05e6\u05ed\u05fb\u0611\u0633\u063f\u0645\u0651\u065e\u0678"+
		"\u067c\u0681\u0685\u0689\u0691\u0695\u0699\u06a0\u06a9\u06b1\u06c0\u06cc"+
		"\u06d2\u06d8\u06ed\u06f2\u06f8\u0704\u070f\u0719\u071c\u0721\u072a\u0730"+
		"\u073a\u073f\u0747\u075e\u0767\u077d\u0784\u078c\u0793\u079d\u07b4\u07bd"+
		"\u07c7\u07dd\u07e1\u07e8\u07ec\u07f0\u07f4\u07fa\u07fe\u0802\u0806\u080c"+
		"\u0810\u0814\u0818\u081a\u081f\u0823\u0827\u082b\u082f\u0836\u083a\u083e"+
		"\u0842\u0848\u084c\u0850\u0854\u0856\u085b\u086a\u0878\u0884\u088d\u089c"+
		"\u08a9\u08b2\u08b8\u08bf\u08c4\u08cb\u08d0\u08d7\u08dc\u08e3\u08e8\u08f0"+
		"\u08f5\u08f9\u08fd\u0902\u0909\u0910\u0915\u091c\u0921\u0928\u092d\u0935"+
		"\u093a\u093e\u0945\u094b\u0952\u0959\u0960\u0968\u096f\u0977\u097b\u0982"+
		"\u0989\u0990\u0998\u099f\u09a7\u09ad\u09b3\u09bf\u09c5\u09cb\u09d6\u09df"+
		"\u09e6\u09ed\u09f2\u09f6\u09ff\u0a18\u0a22\u0a2d\u0a38\u0a43\u0a4e\u0a5a"+
		"\u0a5c\u0a76\u0a78\u0a8a\u0a8c\u0a96\u0aa3\u0aa5\u0ab7\u0ab9\u0ac5\u0ad3"+
		"\u0ad7\u0adb\u0add\u0af4\u0aff\u0b05";
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
	}
}