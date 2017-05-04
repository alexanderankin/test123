// Generated from /home/danson/src/jedit/plugins/JavaSideKick/src/sidekick/java/parser/antlr/Java8.g4 by ANTLR 4.x

package sidekick.java.parser.antlr;

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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, ABSTRACT=13, ASSERT=14, BOOLEAN=15, BREAK=16, 
		BYTE=17, CASE=18, CATCH=19, CHAR=20, CLASS=21, CONST=22, CONTINUE=23, 
		DEFAULT=24, DO=25, DOUBLE=26, ELSE=27, ENUM=28, EXTENDS=29, FINAL=30, 
		FINALLY=31, FLOAT=32, FOR=33, IF=34, GOTO=35, IMPLEMENTS=36, IMPORT=37, 
		INSTANCEOF=38, INT=39, INTERFACE=40, LONG=41, NATIVE=42, NEW=43, PACKAGE=44, 
		PRIVATE=45, PROTECTED=46, PUBLIC=47, RETURN=48, SHORT=49, STATIC=50, STRICTFP=51, 
		SUPER=52, SWITCH=53, SYNCHRONIZED=54, THIS=55, THROW=56, THROWS=57, TRANSIENT=58, 
		TRY=59, VOID=60, VOLATILE=61, WHILE=62, IntegerLiteral=63, FloatingPointLiteral=64, 
		BooleanLiteral=65, CharacterLiteral=66, StringLiteral=67, NullLiteral=68, 
		LPAREN=69, RPAREN=70, LBRACE=71, RBRACE=72, LBRACK=73, RBRACK=74, SEMI=75, 
		COMMA=76, DOT=77, ASSIGN=78, GT=79, LT=80, BANG=81, TILDE=82, QUESTION=83, 
		COLON=84, EQUAL=85, LE=86, GE=87, NOTEQUAL=88, AND=89, OR=90, INC=91, 
		DEC=92, ADD=93, SUB=94, MUL=95, DIV=96, BITAND=97, BITOR=98, CARET=99, 
		MOD=100, ARROW=101, COLONCOLON=102, ADD_ASSIGN=103, SUB_ASSIGN=104, MUL_ASSIGN=105, 
		DIV_ASSIGN=106, AND_ASSIGN=107, OR_ASSIGN=108, XOR_ASSIGN=109, MOD_ASSIGN=110, 
		LSHIFT_ASSIGN=111, RSHIFT_ASSIGN=112, URSHIFT_ASSIGN=113, Identifier=114, 
		AT=115, ELLIPSIS=116, WS=117, DOC_COMMENT=118, COMMENT=119, LINE_COMMENT=120;
	public static final int
		RULE_identifier = 0, RULE_literal = 1, RULE_type = 2, RULE_primitiveType = 3, 
		RULE_numericType = 4, RULE_integralType = 5, RULE_floatingPointType = 6, 
		RULE_referenceType = 7, RULE_classOrInterfaceType = 8, RULE_classType = 9, 
		RULE_classType_lf_classOrInterfaceType = 10, RULE_classType_lfno_classOrInterfaceType = 11, 
		RULE_interfaceType = 12, RULE_interfaceType_lf_classOrInterfaceType = 13, 
		RULE_interfaceType_lfno_classOrInterfaceType = 14, RULE_typeVariable = 15, 
		RULE_arrayType = 16, RULE_dims = 17, RULE_typeParameter = 18, RULE_typeParameterModifier = 19, 
		RULE_typeBound = 20, RULE_additionalBound = 21, RULE_typeArguments = 22, 
		RULE_typeArgumentList = 23, RULE_typeArgument = 24, RULE_wildcard = 25, 
		RULE_wildcardBounds = 26, RULE_packageName = 27, RULE_typeName = 28, RULE_packageOrTypeName = 29, 
		RULE_expressionName = 30, RULE_methodName = 31, RULE_ambiguousName = 32, 
		RULE_compilationUnit = 33, RULE_packageDeclaration = 34, RULE_packageModifier = 35, 
		RULE_importDeclaration = 36, RULE_singleTypeImportDeclaration = 37, RULE_typeImportOnDemandDeclaration = 38, 
		RULE_singleStaticImportDeclaration = 39, RULE_staticImportOnDemandDeclaration = 40, 
		RULE_typeDeclaration = 41, RULE_moduleDeclaration = 42, RULE_moduleStatement = 43, 
		RULE_requiresModifier = 44, RULE_moduleName = 45, RULE_classDeclaration = 46, 
		RULE_normalClassDeclaration = 47, RULE_classModifiers = 48, RULE_classModifier = 49, 
		RULE_typeParameters = 50, RULE_typeParameterList = 51, RULE_superclass = 52, 
		RULE_superinterfaces = 53, RULE_interfaceTypeList = 54, RULE_classBody = 55, 
		RULE_classBodyDeclaration = 56, RULE_classMemberDeclaration = 57, RULE_fieldDeclaration = 58, 
		RULE_fieldModifiers = 59, RULE_fieldModifier = 60, RULE_variableDeclaratorList = 61, 
		RULE_variableDeclarator = 62, RULE_variableDeclaratorId = 63, RULE_variableInitializer = 64, 
		RULE_unannType = 65, RULE_unannPrimitiveType = 66, RULE_unannReferenceType = 67, 
		RULE_unannClassOrInterfaceType = 68, RULE_unannClassType = 69, RULE_unannClassType_lf_unannClassOrInterfaceType = 70, 
		RULE_unannClassType_lfno_unannClassOrInterfaceType = 71, RULE_unannInterfaceType = 72, 
		RULE_unannInterfaceType_lf_unannClassOrInterfaceType = 73, RULE_unannInterfaceType_lfno_unannClassOrInterfaceType = 74, 
		RULE_unannTypeVariable = 75, RULE_unannArrayType = 76, RULE_methodDeclaration = 77, 
		RULE_methodModifiers = 78, RULE_methodModifier = 79, RULE_methodHeader = 80, 
		RULE_result = 81, RULE_methodDeclarator = 82, RULE_formalParameterList = 83, 
		RULE_formalParameters = 84, RULE_formalParameter = 85, RULE_variableModifier = 86, 
		RULE_lastFormalParameter = 87, RULE_receiverParameter = 88, RULE_throws_ = 89, 
		RULE_exceptionTypeList = 90, RULE_exceptionType = 91, RULE_methodBody = 92, 
		RULE_instanceInitializer = 93, RULE_staticInitializer = 94, RULE_constructorDeclaration = 95, 
		RULE_constructorModifiers = 96, RULE_constructorModifier = 97, RULE_constructorDeclarator = 98, 
		RULE_simpleTypeName = 99, RULE_constructorBody = 100, RULE_explicitConstructorInvocation = 101, 
		RULE_enumDeclaration = 102, RULE_enumBody = 103, RULE_enumConstantList = 104, 
		RULE_enumConstant = 105, RULE_enumConstantModifier = 106, RULE_enumBodyDeclarations = 107, 
		RULE_interfaceDeclaration = 108, RULE_normalInterfaceDeclaration = 109, 
		RULE_interfaceModifiers = 110, RULE_interfaceModifier = 111, RULE_extendsInterfaces = 112, 
		RULE_interfaceBody = 113, RULE_interfaceMemberDeclaration = 114, RULE_constantDeclaration = 115, 
		RULE_constantModifier = 116, RULE_interfaceMethodDeclaration = 117, RULE_interfaceMethodModifier = 118, 
		RULE_annotationTypeDeclaration = 119, RULE_annotationTypeBody = 120, RULE_annotationTypeMemberDeclaration = 121, 
		RULE_annotationTypeElementDeclaration = 122, RULE_annotationTypeElementModifier = 123, 
		RULE_defaultValue = 124, RULE_annotation = 125, RULE_annotationIdentifier = 126, 
		RULE_annotationDim = 127, RULE_normalAnnotation = 128, RULE_elementValuePairList = 129, 
		RULE_elementValuePair = 130, RULE_elementValue = 131, RULE_elementValueArrayInitializer = 132, 
		RULE_elementValueList = 133, RULE_markerAnnotation = 134, RULE_singleElementAnnotation = 135, 
		RULE_arrayInitializer = 136, RULE_variableInitializerList = 137, RULE_block = 138, 
		RULE_blockStatements = 139, RULE_blockStatement = 140, RULE_localVariableDeclarationStatement = 141, 
		RULE_localVariableDeclaration = 142, RULE_statement = 143, RULE_statementNoShortIf = 144, 
		RULE_statementWithoutTrailingSubstatement = 145, RULE_emptyStatement = 146, 
		RULE_labeledStatement = 147, RULE_labeledStatementNoShortIf = 148, RULE_expressionStatement = 149, 
		RULE_statementExpression = 150, RULE_ifThenStatement = 151, RULE_ifThenElseStatement = 152, 
		RULE_ifThenElseStatementNoShortIf = 153, RULE_assertStatement = 154, RULE_switchStatement = 155, 
		RULE_switchBlock = 156, RULE_switchBlockStatementGroup = 157, RULE_switchLabels = 158, 
		RULE_switchLabel = 159, RULE_enumConstantName = 160, RULE_whileStatement = 161, 
		RULE_whileStatementNoShortIf = 162, RULE_doStatement = 163, RULE_forStatement = 164, 
		RULE_forStatementNoShortIf = 165, RULE_basicForStatement = 166, RULE_basicForStatementNoShortIf = 167, 
		RULE_forInit = 168, RULE_forUpdate = 169, RULE_statementExpressionList = 170, 
		RULE_enhancedForStatement = 171, RULE_enhancedForStatementNoShortIf = 172, 
		RULE_breakStatement = 173, RULE_continueStatement = 174, RULE_returnStatement = 175, 
		RULE_throwStatement = 176, RULE_synchronizedStatement = 177, RULE_tryStatement = 178, 
		RULE_catches = 179, RULE_catchClause = 180, RULE_catchFormalParameter = 181, 
		RULE_catchType = 182, RULE_finally_ = 183, RULE_tryWithResourcesStatement = 184, 
		RULE_resourceSpecification = 185, RULE_resourceList = 186, RULE_resource = 187, 
		RULE_variableAccess = 188, RULE_primary = 189, RULE_primaryNoNewArray = 190, 
		RULE_primaryNoNewArray_lf_arrayAccess = 191, RULE_primaryNoNewArray_lfno_arrayAccess = 192, 
		RULE_primaryNoNewArray_lf_primary = 193, RULE_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary = 194, 
		RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary = 195, RULE_primaryNoNewArray_lfno_primary = 196, 
		RULE_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary = 197, 
		RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary = 198, 
		RULE_classInstanceCreationExpression = 199, RULE_classInstanceCreationExpression_lf_primary = 200, 
		RULE_classInstanceCreationExpression_lfno_primary = 201, RULE_typeArgumentsOrDiamond = 202, 
		RULE_fieldAccess = 203, RULE_fieldAccess_lf_primary = 204, RULE_fieldAccess_lfno_primary = 205, 
		RULE_arrayAccess = 206, RULE_arrayAccess_lf_primary = 207, RULE_arrayAccess_lfno_primary = 208, 
		RULE_methodInvocation = 209, RULE_methodInvocation_lf_primary = 210, RULE_methodInvocation_lfno_primary = 211, 
		RULE_argumentList = 212, RULE_methodReference = 213, RULE_methodReference_lf_primary = 214, 
		RULE_methodReference_lfno_primary = 215, RULE_arrayCreationExpression = 216, 
		RULE_dimExprs = 217, RULE_dimExpr = 218, RULE_constantExpression = 219, 
		RULE_expression = 220, RULE_lambdaExpression = 221, RULE_lambdaParameters = 222, 
		RULE_inferredFormalParameterList = 223, RULE_lambdaBody = 224, RULE_assignmentExpression = 225, 
		RULE_assignment = 226, RULE_leftHandSide = 227, RULE_assignmentOperator = 228, 
		RULE_additiveOperator = 229, RULE_relationalOperator = 230, RULE_multiplicativeOperator = 231, 
		RULE_squareBrackets = 232, RULE_conditionalExpression = 233, RULE_conditionalOrExpression = 234, 
		RULE_conditionalAndExpression = 235, RULE_inclusiveOrExpression = 236, 
		RULE_exclusiveOrExpression = 237, RULE_andExpression = 238, RULE_equalityExpression = 239, 
		RULE_relationalExpression = 240, RULE_shiftExpression = 241, RULE_shiftOperator = 242, 
		RULE_additiveExpression = 243, RULE_multiplicativeExpression = 244, RULE_unaryExpression = 245, 
		RULE_preIncrementExpression = 246, RULE_preDecrementExpression = 247, 
		RULE_unaryExpressionNotPlusMinus = 248, RULE_postfixExpression = 249, 
		RULE_postIncrementExpression = 250, RULE_postIncrementExpression_lf_postfixExpression = 251, 
		RULE_postDecrementExpression = 252, RULE_postDecrementExpression_lf_postfixExpression = 253, 
		RULE_castExpression = 254;
	public static final String[] ruleNames = {
		"identifier", "literal", "type", "primitiveType", "numericType", "integralType", 
		"floatingPointType", "referenceType", "classOrInterfaceType", "classType", 
		"classType_lf_classOrInterfaceType", "classType_lfno_classOrInterfaceType", 
		"interfaceType", "interfaceType_lf_classOrInterfaceType", "interfaceType_lfno_classOrInterfaceType", 
		"typeVariable", "arrayType", "dims", "typeParameter", "typeParameterModifier", 
		"typeBound", "additionalBound", "typeArguments", "typeArgumentList", "typeArgument", 
		"wildcard", "wildcardBounds", "packageName", "typeName", "packageOrTypeName", 
		"expressionName", "methodName", "ambiguousName", "compilationUnit", "packageDeclaration", 
		"packageModifier", "importDeclaration", "singleTypeImportDeclaration", 
		"typeImportOnDemandDeclaration", "singleStaticImportDeclaration", "staticImportOnDemandDeclaration", 
		"typeDeclaration", "moduleDeclaration", "moduleStatement", "requiresModifier", 
		"moduleName", "classDeclaration", "normalClassDeclaration", "classModifiers", 
		"classModifier", "typeParameters", "typeParameterList", "superclass", 
		"superinterfaces", "interfaceTypeList", "classBody", "classBodyDeclaration", 
		"classMemberDeclaration", "fieldDeclaration", "fieldModifiers", "fieldModifier", 
		"variableDeclaratorList", "variableDeclarator", "variableDeclaratorId", 
		"variableInitializer", "unannType", "unannPrimitiveType", "unannReferenceType", 
		"unannClassOrInterfaceType", "unannClassType", "unannClassType_lf_unannClassOrInterfaceType", 
		"unannClassType_lfno_unannClassOrInterfaceType", "unannInterfaceType", 
		"unannInterfaceType_lf_unannClassOrInterfaceType", "unannInterfaceType_lfno_unannClassOrInterfaceType", 
		"unannTypeVariable", "unannArrayType", "methodDeclaration", "methodModifiers", 
		"methodModifier", "methodHeader", "result", "methodDeclarator", "formalParameterList", 
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
		"switchLabels", "switchLabel", "enumConstantName", "whileStatement", "whileStatementNoShortIf", 
		"doStatement", "forStatement", "forStatementNoShortIf", "basicForStatement", 
		"basicForStatementNoShortIf", "forInit", "forUpdate", "statementExpressionList", 
		"enhancedForStatement", "enhancedForStatementNoShortIf", "breakStatement", 
		"continueStatement", "returnStatement", "throwStatement", "synchronizedStatement", 
		"tryStatement", "catches", "catchClause", "catchFormalParameter", "catchType", 
		"finally_", "tryWithResourcesStatement", "resourceSpecification", "resourceList", 
		"resource", "variableAccess", "primary", "primaryNoNewArray", "primaryNoNewArray_lf_arrayAccess", 
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
		null, "'open'", "'module'", "'requires'", "'transitive'", "'exports'", 
		"'opens'", "'to'", "'uses'", "'provides'", "'with'", "'<>'", "'[]'", "'abstract'", 
		"'assert'", "'boolean'", "'break'", "'byte'", "'case'", "'catch'", "'char'", 
		"'class'", "'const'", "'continue'", "'default'", "'do'", "'double'", "'else'", 
		"'enum'", "'extends'", "'final'", "'finally'", "'float'", "'for'", "'if'", 
		"'goto'", "'implements'", "'import'", "'instanceof'", "'int'", "'interface'", 
		"'long'", "'native'", "'new'", "'package'", "'private'", "'protected'", 
		"'public'", "'return'", "'short'", "'static'", "'strictfp'", "'super'", 
		"'switch'", "'synchronized'", "'this'", "'throw'", "'throws'", "'transient'", 
		"'try'", "'void'", "'volatile'", "'while'", null, null, null, null, null, 
		"'null'", "'('", "')'", "'{'", "'}'", "'['", "']'", "';'", "','", "'.'", 
		"'='", "'>'", "'<'", "'!'", "'~'", "'?'", "':'", "'=='", "'<='", "'>='", 
		"'!='", "'&&'", "'||'", "'++'", "'--'", "'+'", "'-'", "'*'", "'/'", "'&'", 
		"'|'", "'^'", "'%'", "'->'", "'::'", "'+='", "'-='", "'*='", "'/='", "'&='", 
		"'|='", "'^='", "'%='", "'<<='", "'>>='", "'>>>='", null, "'@'", "'...'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", 
		"CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", 
		"ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "IF", "GOTO", "IMPLEMENTS", 
		"IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", "NEW", "PACKAGE", 
		"PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", "STRICTFP", 
		"SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", "TRANSIENT", 
		"TRY", "VOID", "VOLATILE", "WHILE", "IntegerLiteral", "FloatingPointLiteral", 
		"BooleanLiteral", "CharacterLiteral", "StringLiteral", "NullLiteral", 
		"LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACK", "RBRACK", "SEMI", "COMMA", 
		"DOT", "ASSIGN", "GT", "LT", "BANG", "TILDE", "QUESTION", "COLON", "EQUAL", 
		"LE", "GE", "NOTEQUAL", "AND", "OR", "INC", "DEC", "ADD", "SUB", "MUL", 
		"DIV", "BITAND", "BITOR", "CARET", "MOD", "ARROW", "COLONCOLON", "ADD_ASSIGN", 
		"SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", 
		"MOD_ASSIGN", "LSHIFT_ASSIGN", "RSHIFT_ASSIGN", "URSHIFT_ASSIGN", "Identifier", 
		"AT", "ELLIPSIS", "WS", "DOC_COMMENT", "COMMENT", "LINE_COMMENT"
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
	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitIdentifier(this);
		}
	}

	@RuleVersion(0)
	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(510);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) ) {
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
		enterRule(_localctx, 2, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			_la = _input.LA(1);
			if ( !(((((_la - 63)) & ~0x3f) == 0 && ((1L << (_la - 63)) & ((1L << (IntegerLiteral - 63)) | (1L << (FloatingPointLiteral - 63)) | (1L << (BooleanLiteral - 63)) | (1L << (CharacterLiteral - 63)) | (1L << (StringLiteral - 63)) | (1L << (NullLiteral - 63)))) != 0)) ) {
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
		enterRule(_localctx, 4, RULE_type);
		try {
			setState(516);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(514);
				primitiveType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(515);
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
		enterRule(_localctx, 6, RULE_primitiveType);
		int _la;
		try {
			setState(532);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(521);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(518);
					annotation();
					}
					}
					setState(523);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(524);
				numericType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(528);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(525);
					annotation();
					}
					}
					setState(530);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(531);
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
		enterRule(_localctx, 8, RULE_numericType);
		try {
			setState(536);
			switch (_input.LA(1)) {
			case BYTE:
			case CHAR:
			case INT:
			case LONG:
			case SHORT:
				enterOuterAlt(_localctx, 1);
				{
				setState(534);
				integralType();
				}
				break;
			case DOUBLE:
			case FLOAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(535);
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
		enterRule(_localctx, 10, RULE_integralType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(538);
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
		enterRule(_localctx, 12, RULE_floatingPointType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
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
		enterRule(_localctx, 14, RULE_referenceType);
		try {
			setState(545);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(542);
				classOrInterfaceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(543);
				typeVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(544);
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
		enterRule(_localctx, 16, RULE_classOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(549);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(547);
				classType_lfno_classOrInterfaceType();
				}
				break;
			case 2:
				{
				setState(548);
				interfaceType_lfno_classOrInterfaceType();
				}
				break;
			}
			setState(555);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(553);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						setState(551);
						classType_lf_classOrInterfaceType();
						}
						break;
					case 2:
						{
						setState(552);
						interfaceType_lf_classOrInterfaceType();
						}
						break;
					}
					} 
				}
				setState(557);
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
		enterRule(_localctx, 18, RULE_classType);
		int _la;
		try {
			setState(568);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(558);
				annotationIdentifier();
				setState(560);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(559);
					typeArguments();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(562);
				classOrInterfaceType();
				setState(563);
				match(DOT);
				setState(564);
				annotationIdentifier();
				setState(566);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(565);
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
		enterRule(_localctx, 20, RULE_classType_lf_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(570);
			match(DOT);
			setState(571);
			annotationIdentifier();
			setState(573);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(572);
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
		enterRule(_localctx, 22, RULE_classType_lfno_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(575);
			annotationIdentifier();
			setState(577);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(576);
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
		enterRule(_localctx, 24, RULE_interfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(579);
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
		enterRule(_localctx, 26, RULE_interfaceType_lf_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(581);
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
		enterRule(_localctx, 28, RULE_interfaceType_lfno_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(583);
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
		enterRule(_localctx, 30, RULE_typeVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
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
		enterRule(_localctx, 32, RULE_arrayType);
		try {
			setState(596);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(587);
				primitiveType();
				setState(588);
				dims();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(590);
				classOrInterfaceType();
				setState(591);
				dims();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(593);
				typeVariable();
				setState(594);
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
		enterRule(_localctx, 34, RULE_dims);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(598);
			annotationDim();
			setState(602);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(599);
					annotationDim();
					}
					} 
				}
				setState(604);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 36, RULE_typeParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(608);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(605);
				typeParameterModifier();
				}
				}
				setState(610);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(611);
			identifier();
			setState(613);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(612);
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
		enterRule(_localctx, 38, RULE_typeParameterModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(615);
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
		enterRule(_localctx, 40, RULE_typeBound);
		int _la;
		try {
			setState(627);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(617);
				match(EXTENDS);
				setState(618);
				typeVariable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(619);
				match(EXTENDS);
				setState(620);
				classOrInterfaceType();
				setState(624);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(621);
					additionalBound();
					}
					}
					setState(626);
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
		enterRule(_localctx, 42, RULE_additionalBound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(629);
			match(BITAND);
			setState(630);
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
		enterRule(_localctx, 44, RULE_typeArguments);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(632);
			match(LT);
			setState(633);
			typeArgumentList();
			setState(634);
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
		enterRule(_localctx, 46, RULE_typeArgumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(636);
			typeArgument();
			setState(641);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(637);
				match(COMMA);
				setState(638);
				typeArgument();
				}
				}
				setState(643);
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
		enterRule(_localctx, 48, RULE_typeArgument);
		try {
			setState(646);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(644);
				referenceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(645);
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
		enterRule(_localctx, 50, RULE_wildcard);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(651);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(648);
				annotation();
				}
				}
				setState(653);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(654);
			match(QUESTION);
			setState(656);
			_la = _input.LA(1);
			if (_la==EXTENDS || _la==SUPER) {
				{
				setState(655);
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
		enterRule(_localctx, 52, RULE_wildcardBounds);
		try {
			setState(662);
			switch (_input.LA(1)) {
			case EXTENDS:
				enterOuterAlt(_localctx, 1);
				{
				setState(658);
				match(EXTENDS);
				setState(659);
				referenceType();
				}
				break;
			case SUPER:
				enterOuterAlt(_localctx, 2);
				{
				setState(660);
				match(SUPER);
				setState(661);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		int _startState = 54;
		enterRecursionRule(_localctx, 54, RULE_packageName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(665);
			identifier();
			}
			_ctx.stop = _input.LT(-1);
			setState(672);
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
					setState(667);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(668);
					match(DOT);
					setState(669);
					identifier();
					}
					} 
				}
				setState(674);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 56, RULE_typeName);
		try {
			setState(680);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(675);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(676);
				packageOrTypeName(0);
				setState(677);
				match(DOT);
				setState(678);
				identifier();
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_packageOrTypeName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(683);
			identifier();
			}
			_ctx.stop = _input.LT(-1);
			setState(690);
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
					setState(685);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(686);
					match(DOT);
					setState(687);
					identifier();
					}
					} 
				}
				setState(692);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 60, RULE_expressionName);
		try {
			setState(698);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(693);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(694);
				ambiguousName(0);
				setState(695);
				match(DOT);
				setState(696);
				identifier();
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 62, RULE_methodName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(700);
			identifier();
			}
		}
		catch (RecognitionException re) {
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_ambiguousName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(703);
			identifier();
			}
			_ctx.stop = _input.LT(-1);
			setState(710);
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
					setState(705);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(706);
					match(DOT);
					setState(707);
					identifier();
					}
					} 
				}
				setState(712);
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
		public PackageDeclarationContext packageDeclaration() {
			return getRuleContext(PackageDeclarationContext.class,0);
		}
		public TerminalNode EOF() { return getToken(Java8Parser.EOF, 0); }
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
		public ModuleDeclarationContext moduleDeclaration() {
			return getRuleContext(ModuleDeclarationContext.class,0);
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
		enterRule(_localctx, 66, RULE_compilationUnit);
		int _la;
		try {
			setState(741);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(713);
				packageDeclaration();
				setState(714);
				match(EOF);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(717);
				switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
				case 1:
					{
					setState(716);
					packageDeclaration();
					}
					break;
				}
				setState(722);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IMPORT) {
					{
					{
					setState(719);
					importDeclaration();
					}
					}
					setState(724);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(728);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << CLASS) | (1L << ENUM) | (1L << FINAL) | (1L << INTERFACE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==SEMI || _la==AT) {
					{
					{
					setState(725);
					typeDeclaration();
					}
					}
					setState(730);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(731);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(735);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IMPORT) {
					{
					{
					setState(732);
					importDeclaration();
					}
					}
					setState(737);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(738);
				moduleDeclaration();
				setState(739);
				match(EOF);
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

	public static class PackageDeclarationContext extends ParserRuleContext {
		public List<? extends IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
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
		enterRule(_localctx, 68, RULE_packageDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(746);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(743);
				packageModifier();
				}
				}
				setState(748);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(749);
			match(PACKAGE);
			setState(750);
			identifier();
			setState(755);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(751);
				match(DOT);
				setState(752);
				identifier();
				}
				}
				setState(757);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(758);
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
		enterRule(_localctx, 70, RULE_packageModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(760);
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
		enterRule(_localctx, 72, RULE_importDeclaration);
		try {
			setState(766);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(762);
				singleTypeImportDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(763);
				typeImportOnDemandDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(764);
				singleStaticImportDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(765);
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
		enterRule(_localctx, 74, RULE_singleTypeImportDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(768);
			match(IMPORT);
			setState(769);
			typeName();
			setState(770);
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
		enterRule(_localctx, 76, RULE_typeImportOnDemandDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(772);
			match(IMPORT);
			setState(773);
			packageOrTypeName(0);
			setState(774);
			match(DOT);
			setState(775);
			match(MUL);
			setState(776);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 78, RULE_singleStaticImportDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(778);
			match(IMPORT);
			setState(779);
			match(STATIC);
			setState(780);
			typeName();
			setState(781);
			match(DOT);
			setState(782);
			identifier();
			setState(783);
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
		enterRule(_localctx, 80, RULE_staticImportOnDemandDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(785);
			match(IMPORT);
			setState(786);
			match(STATIC);
			setState(787);
			typeName();
			setState(788);
			match(DOT);
			setState(789);
			match(MUL);
			setState(790);
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
		enterRule(_localctx, 82, RULE_typeDeclaration);
		try {
			setState(795);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(792);
				classDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(793);
				interfaceDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(794);
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

	public static class ModuleDeclarationContext extends ParserRuleContext {
		public List<? extends TerminalNode> Identifier() { return getTokens(Java8Parser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(Java8Parser.Identifier, i);
		}
		public List<? extends AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public List<? extends ModuleStatementContext> moduleStatement() {
			return getRuleContexts(ModuleStatementContext.class);
		}
		public ModuleStatementContext moduleStatement(int i) {
			return getRuleContext(ModuleStatementContext.class,i);
		}
		public ModuleDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterModuleDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitModuleDeclaration(this);
		}
	}

	@RuleVersion(0)
	public final ModuleDeclarationContext moduleDeclaration() throws RecognitionException {
		ModuleDeclarationContext _localctx = new ModuleDeclarationContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_moduleDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(800);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(797);
				annotation();
				}
				}
				setState(802);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(804);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(803);
				match(T__0);
				}
			}

			setState(806);
			match(T__1);
			setState(807);
			match(Identifier);
			setState(812);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(808);
				match(DOT);
				setState(809);
				match(Identifier);
				}
				}
				setState(814);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(815);
			match(LBRACE);
			setState(819);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << T__5) | (1L << T__7) | (1L << T__8))) != 0)) {
				{
				{
				setState(816);
				moduleStatement();
				}
				}
				setState(821);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(822);
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

	public static class ModuleStatementContext extends ParserRuleContext {
		public List<? extends ModuleNameContext> moduleName() {
			return getRuleContexts(ModuleNameContext.class);
		}
		public ModuleNameContext moduleName(int i) {
			return getRuleContext(ModuleNameContext.class,i);
		}
		public List<? extends RequiresModifierContext> requiresModifier() {
			return getRuleContexts(RequiresModifierContext.class);
		}
		public RequiresModifierContext requiresModifier(int i) {
			return getRuleContext(RequiresModifierContext.class,i);
		}
		public PackageNameContext packageName() {
			return getRuleContext(PackageNameContext.class,0);
		}
		public List<? extends TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public ModuleStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterModuleStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitModuleStatement(this);
		}
	}

	@RuleVersion(0)
	public final ModuleStatementContext moduleStatement() throws RecognitionException {
		ModuleStatementContext _localctx = new ModuleStatementContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_moduleStatement);
		int _la;
		try {
			setState(881);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				setState(824);
				match(T__2);
				setState(828);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3 || _la==STATIC) {
					{
					{
					setState(825);
					requiresModifier();
					}
					}
					setState(830);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(831);
				moduleName(0);
				setState(832);
				match(SEMI);
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				setState(834);
				match(T__4);
				setState(835);
				packageName(0);
				setState(845);
				_la = _input.LA(1);
				if (_la==T__6) {
					{
					setState(836);
					match(T__6);
					setState(837);
					moduleName(0);
					setState(842);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(838);
						match(COMMA);
						setState(839);
						moduleName(0);
						}
						}
						setState(844);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(847);
				match(SEMI);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 3);
				{
				setState(849);
				match(T__5);
				setState(850);
				packageName(0);
				setState(860);
				_la = _input.LA(1);
				if (_la==T__6) {
					{
					setState(851);
					match(T__6);
					setState(852);
					moduleName(0);
					setState(857);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(853);
						match(COMMA);
						setState(854);
						moduleName(0);
						}
						}
						setState(859);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(862);
				match(SEMI);
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(864);
				match(T__7);
				setState(865);
				typeName();
				setState(866);
				match(SEMI);
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 5);
				{
				setState(868);
				match(T__8);
				setState(869);
				typeName();
				setState(870);
				match(T__9);
				setState(871);
				typeName();
				setState(876);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(872);
					match(COMMA);
					setState(873);
					typeName();
					}
					}
					setState(878);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(879);
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

	public static class RequiresModifierContext extends ParserRuleContext {
		public RequiresModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_requiresModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterRequiresModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitRequiresModifier(this);
		}
	}

	@RuleVersion(0)
	public final RequiresModifierContext requiresModifier() throws RecognitionException {
		RequiresModifierContext _localctx = new RequiresModifierContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_requiresModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(883);
			_la = _input.LA(1);
			if ( !(_la==T__3 || _la==STATIC) ) {
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

	public static class ModuleNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public ModuleNameContext moduleName() {
			return getRuleContext(ModuleNameContext.class,0);
		}
		public ModuleNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterModuleName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitModuleName(this);
		}
	}

	@RuleVersion(0)
	public final ModuleNameContext moduleName() throws RecognitionException {
		return moduleName(0);
	}

	private ModuleNameContext moduleName(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ModuleNameContext _localctx = new ModuleNameContext(_ctx, _parentState);
		ModuleNameContext _prevctx = _localctx;
		int _startState = 90;
		enterRecursionRule(_localctx, 90, RULE_moduleName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(886);
			match(Identifier);
			}
			_ctx.stop = _input.LT(-1);
			setState(893);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ModuleNameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_moduleName);
					setState(888);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(889);
					match(DOT);
					setState(890);
					match(Identifier);
					}
					} 
				}
				setState(895);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,50,_ctx);
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
		enterRule(_localctx, 92, RULE_classDeclaration);
		try {
			setState(898);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(896);
				normalClassDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(897);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 94, RULE_normalClassDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(900);
			classModifiers();
			setState(901);
			match(CLASS);
			setState(902);
			identifier();
			setState(904);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(903);
				typeParameters();
				}
			}

			setState(907);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(906);
				superclass();
				}
			}

			setState(910);
			_la = _input.LA(1);
			if (_la==IMPLEMENTS) {
				{
				setState(909);
				superinterfaces();
				}
			}

			setState(912);
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
		enterRule(_localctx, 96, RULE_classModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(917);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << FINAL) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(914);
				classModifier();
				}
				}
				setState(919);
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
		enterRule(_localctx, 98, RULE_classModifier);
		try {
			setState(928);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(920);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(921);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(922);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(923);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(924);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(925);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(926);
				match(FINAL);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 8);
				{
				setState(927);
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
		enterRule(_localctx, 100, RULE_typeParameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(930);
			match(LT);
			setState(931);
			typeParameterList();
			setState(932);
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
		enterRule(_localctx, 102, RULE_typeParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(934);
			typeParameter();
			setState(939);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(935);
				match(COMMA);
				setState(936);
				typeParameter();
				}
				}
				setState(941);
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
		enterRule(_localctx, 104, RULE_superclass);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(942);
			match(EXTENDS);
			setState(943);
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
		enterRule(_localctx, 106, RULE_superinterfaces);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(945);
			match(IMPLEMENTS);
			setState(946);
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
		enterRule(_localctx, 108, RULE_interfaceTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(948);
			interfaceType();
			setState(953);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(949);
				match(COMMA);
				setState(950);
				interfaceType();
				}
				}
				setState(955);
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
		enterRule(_localctx, 110, RULE_classBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(956);
			match(LBRACE);
			setState(960);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (LBRACE - 71)) | (1L << (SEMI - 71)) | (1L << (LT - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				{
				setState(957);
				classBodyDeclaration();
				}
				}
				setState(962);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(963);
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
		enterRule(_localctx, 112, RULE_classBodyDeclaration);
		try {
			setState(969);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(965);
				classMemberDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(966);
				instanceInitializer();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(967);
				staticInitializer();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(968);
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
		enterRule(_localctx, 114, RULE_classMemberDeclaration);
		try {
			setState(976);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(971);
				fieldDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(972);
				methodDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(973);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(974);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(975);
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
		enterRule(_localctx, 116, RULE_fieldDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(978);
			fieldModifiers();
			setState(979);
			unannType();
			setState(980);
			variableDeclaratorList();
			setState(981);
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
		enterRule(_localctx, 118, RULE_fieldModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(986);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FINAL) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << TRANSIENT) | (1L << VOLATILE))) != 0) || _la==AT) {
				{
				{
				setState(983);
				fieldModifier();
				}
				}
				setState(988);
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
		enterRule(_localctx, 120, RULE_fieldModifier);
		try {
			setState(997);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(989);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(990);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(991);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(992);
				match(PRIVATE);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 5);
				{
				setState(993);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 6);
				{
				setState(994);
				match(FINAL);
				}
				break;
			case TRANSIENT:
				enterOuterAlt(_localctx, 7);
				{
				setState(995);
				match(TRANSIENT);
				}
				break;
			case VOLATILE:
				enterOuterAlt(_localctx, 8);
				{
				setState(996);
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
		enterRule(_localctx, 122, RULE_variableDeclaratorList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(999);
			variableDeclarator();
			setState(1004);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1000);
				match(COMMA);
				setState(1001);
				variableDeclarator();
				}
				}
				setState(1006);
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
		enterRule(_localctx, 124, RULE_variableDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1007);
			variableDeclaratorId();
			setState(1010);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(1008);
				match(ASSIGN);
				setState(1009);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 126, RULE_variableDeclaratorId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1012);
			identifier();
			setState(1014);
			_la = _input.LA(1);
			if (_la==T__11 || _la==AT) {
				{
				setState(1013);
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
		enterRule(_localctx, 128, RULE_variableInitializer);
		try {
			setState(1018);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
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
				setState(1016);
				expression();
				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1017);
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
		enterRule(_localctx, 130, RULE_unannType);
		try {
			setState(1022);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1020);
				unannPrimitiveType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1021);
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
		enterRule(_localctx, 132, RULE_unannPrimitiveType);
		try {
			setState(1026);
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
				setState(1024);
				numericType();
				}
				break;
			case BOOLEAN:
				enterOuterAlt(_localctx, 2);
				{
				setState(1025);
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
		enterRule(_localctx, 134, RULE_unannReferenceType);
		try {
			setState(1031);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1028);
				unannClassOrInterfaceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1029);
				unannTypeVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1030);
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
		enterRule(_localctx, 136, RULE_unannClassOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1035);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				{
				setState(1033);
				unannClassType_lfno_unannClassOrInterfaceType();
				}
				break;
			case 2:
				{
				setState(1034);
				unannInterfaceType_lfno_unannClassOrInterfaceType();
				}
				break;
			}
			setState(1041);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(1039);
					switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
					case 1:
						{
						setState(1037);
						unannClassType_lf_unannClassOrInterfaceType();
						}
						break;
					case 2:
						{
						setState(1038);
						unannInterfaceType_lf_unannClassOrInterfaceType();
						}
						break;
					}
					} 
				}
				setState(1043);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 138, RULE_unannClassType);
		int _la;
		try {
			setState(1054);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1044);
				identifier();
				setState(1046);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1045);
					typeArguments();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1048);
				unannClassOrInterfaceType();
				setState(1049);
				match(DOT);
				setState(1050);
				annotationIdentifier();
				setState(1052);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1051);
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
		enterRule(_localctx, 140, RULE_unannClassType_lf_unannClassOrInterfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1056);
			match(DOT);
			setState(1057);
			annotationIdentifier();
			setState(1059);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1058);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 142, RULE_unannClassType_lfno_unannClassOrInterfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1061);
			identifier();
			setState(1063);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1062);
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
		enterRule(_localctx, 144, RULE_unannInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1065);
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
		enterRule(_localctx, 146, RULE_unannInterfaceType_lf_unannClassOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1067);
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
		enterRule(_localctx, 148, RULE_unannInterfaceType_lfno_unannClassOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1069);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 150, RULE_unannTypeVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1071);
			identifier();
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 152, RULE_unannArrayType);
		try {
			setState(1082);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1073);
				unannPrimitiveType();
				setState(1074);
				dims();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1076);
				unannClassOrInterfaceType();
				setState(1077);
				dims();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1079);
				unannTypeVariable();
				setState(1080);
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
		enterRule(_localctx, 154, RULE_methodDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1084);
			methodModifiers();
			setState(1085);
			methodHeader();
			setState(1086);
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
		enterRule(_localctx, 156, RULE_methodModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1091);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << FINAL) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED))) != 0) || _la==AT) {
				{
				{
				setState(1088);
				methodModifier();
				}
				}
				setState(1093);
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
		enterRule(_localctx, 158, RULE_methodModifier);
		try {
			setState(1104);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1094);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1095);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1096);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1097);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1098);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(1099);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(1100);
				match(FINAL);
				}
				break;
			case SYNCHRONIZED:
				enterOuterAlt(_localctx, 8);
				{
				setState(1101);
				match(SYNCHRONIZED);
				}
				break;
			case NATIVE:
				enterOuterAlt(_localctx, 9);
				{
				setState(1102);
				match(NATIVE);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 10);
				{
				setState(1103);
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
		enterRule(_localctx, 160, RULE_methodHeader);
		int _la;
		try {
			setState(1123);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
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
				setState(1106);
				result();
				setState(1107);
				methodDeclarator();
				setState(1109);
				_la = _input.LA(1);
				if (_la==THROWS) {
					{
					setState(1108);
					throws_();
					}
				}

				}
				break;
			case LT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1111);
				typeParameters();
				setState(1115);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(1112);
					annotation();
					}
					}
					setState(1117);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1118);
				result();
				setState(1119);
				methodDeclarator();
				setState(1121);
				_la = _input.LA(1);
				if (_la==THROWS) {
					{
					setState(1120);
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
		enterRule(_localctx, 162, RULE_result);
		try {
			setState(1127);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
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
				setState(1125);
				unannType();
				}
				break;
			case VOID:
				enterOuterAlt(_localctx, 2);
				{
				setState(1126);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 164, RULE_methodDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1129);
			identifier();
			setState(1130);
			match(LPAREN);
			setState(1132);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1131);
				formalParameterList();
				}
			}

			setState(1134);
			match(RPAREN);
			setState(1136);
			_la = _input.LA(1);
			if (_la==T__11 || _la==AT) {
				{
				setState(1135);
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
		enterRule(_localctx, 166, RULE_formalParameterList);
		try {
			setState(1143);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1138);
				formalParameters();
				setState(1139);
				match(COMMA);
				setState(1140);
				lastFormalParameter();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1142);
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
		enterRule(_localctx, 168, RULE_formalParameters);
		try {
			int _alt;
			setState(1161);
			switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1145);
				formalParameter();
				setState(1150);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1146);
						match(COMMA);
						setState(1147);
						formalParameter();
						}
						} 
					}
					setState(1152);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1153);
				receiverParameter();
				setState(1158);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,91,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1154);
						match(COMMA);
						setState(1155);
						formalParameter();
						}
						} 
					}
					setState(1160);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,91,_ctx);
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
		enterRule(_localctx, 170, RULE_formalParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1163);
				variableModifier();
				}
				}
				setState(1168);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1169);
			unannType();
			setState(1170);
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
		enterRule(_localctx, 172, RULE_variableModifier);
		try {
			setState(1174);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1172);
				annotation();
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1173);
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
		enterRule(_localctx, 174, RULE_lastFormalParameter);
		int _la;
		try {
			setState(1193);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1179);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FINAL || _la==AT) {
					{
					{
					setState(1176);
					variableModifier();
					}
					}
					setState(1181);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1182);
				unannType();
				setState(1186);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(1183);
					annotation();
					}
					}
					setState(1188);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1189);
				match(ELLIPSIS);
				setState(1190);
				variableDeclaratorId();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1192);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 176, RULE_receiverParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1195);
				annotation();
				}
				}
				setState(1200);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1201);
			unannType();
			setState(1205);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) {
				{
				setState(1202);
				identifier();
				setState(1203);
				match(DOT);
				}
			}

			setState(1207);
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
		enterRule(_localctx, 178, RULE_throws_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1209);
			match(THROWS);
			setState(1210);
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
		enterRule(_localctx, 180, RULE_exceptionTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1212);
			exceptionType();
			setState(1217);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1213);
				match(COMMA);
				setState(1214);
				exceptionType();
				}
				}
				setState(1219);
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
		enterRule(_localctx, 182, RULE_exceptionType);
		try {
			setState(1222);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1220);
				classType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1221);
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
		enterRule(_localctx, 184, RULE_methodBody);
		try {
			setState(1226);
			switch (_input.LA(1)) {
			case LBRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1224);
				block();
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
				setState(1225);
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
		enterRule(_localctx, 186, RULE_instanceInitializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1228);
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
		enterRule(_localctx, 188, RULE_staticInitializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1230);
			match(STATIC);
			setState(1231);
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
		enterRule(_localctx, 190, RULE_constructorDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1233);
			constructorModifiers();
			setState(1234);
			constructorDeclarator();
			setState(1236);
			_la = _input.LA(1);
			if (_la==THROWS) {
				{
				setState(1235);
				throws_();
				}
			}

			setState(1238);
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
		enterRule(_localctx, 192, RULE_constructorModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC))) != 0) || _la==AT) {
				{
				{
				setState(1240);
				constructorModifier();
				}
				}
				setState(1245);
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
		enterRule(_localctx, 194, RULE_constructorModifier);
		try {
			setState(1250);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1246);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1247);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1248);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1249);
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
		enterRule(_localctx, 196, RULE_constructorDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1253);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1252);
				typeParameters();
				}
			}

			setState(1255);
			simpleTypeName();
			setState(1256);
			match(LPAREN);
			setState(1258);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1257);
				formalParameterList();
				}
			}

			setState(1260);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 198, RULE_simpleTypeName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1262);
			identifier();
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 200, RULE_constructorBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1264);
			match(LBRACE);
			setState(1266);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				{
				setState(1265);
				explicitConstructorInvocation();
				}
				break;
			}
			setState(1269);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1268);
				blockStatements();
				}
			}

			setState(1271);
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
		enterRule(_localctx, 202, RULE_explicitConstructorInvocation);
		int _la;
		try {
			setState(1319);
			switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1274);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1273);
					typeArguments();
					}
				}

				setState(1276);
				match(THIS);
				setState(1277);
				match(LPAREN);
				setState(1279);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(1278);
					argumentList();
					}
				}

				setState(1281);
				match(RPAREN);
				setState(1282);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1284);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1283);
					typeArguments();
					}
				}

				setState(1286);
				match(SUPER);
				setState(1287);
				match(LPAREN);
				setState(1289);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(1288);
					argumentList();
					}
				}

				setState(1291);
				match(RPAREN);
				setState(1292);
				match(SEMI);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1293);
				expressionName();
				setState(1294);
				match(DOT);
				setState(1296);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1295);
					typeArguments();
					}
				}

				setState(1298);
				match(SUPER);
				setState(1299);
				match(LPAREN);
				setState(1301);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(1300);
					argumentList();
					}
				}

				setState(1303);
				match(RPAREN);
				setState(1304);
				match(SEMI);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1306);
				primary();
				setState(1307);
				match(DOT);
				setState(1309);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1308);
					typeArguments();
					}
				}

				setState(1311);
				match(SUPER);
				setState(1312);
				match(LPAREN);
				setState(1314);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(1313);
					argumentList();
					}
				}

				setState(1316);
				match(RPAREN);
				setState(1317);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 204, RULE_enumDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1321);
			classModifiers();
			setState(1322);
			match(ENUM);
			setState(1323);
			identifier();
			setState(1325);
			_la = _input.LA(1);
			if (_la==IMPLEMENTS) {
				{
				setState(1324);
				superinterfaces();
				}
			}

			setState(1327);
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
		enterRule(_localctx, 206, RULE_enumBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1329);
			match(LBRACE);
			setState(1331);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1330);
				enumConstantList();
				}
			}

			setState(1334);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1333);
				match(COMMA);
				}
			}

			setState(1337);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(1336);
				enumBodyDeclarations();
				}
			}

			setState(1339);
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
		enterRule(_localctx, 208, RULE_enumConstantList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1341);
			enumConstant();
			setState(1346);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1342);
					match(COMMA);
					setState(1343);
					enumConstant();
					}
					} 
				}
				setState(1348);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 210, RULE_enumConstant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1352);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1349);
				enumConstantModifier();
				}
				}
				setState(1354);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1355);
			identifier();
			setState(1361);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(1356);
				match(LPAREN);
				setState(1358);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(1357);
					argumentList();
					}
				}

				setState(1360);
				match(RPAREN);
				}
			}

			setState(1364);
			_la = _input.LA(1);
			if (_la==LBRACE) {
				{
				setState(1363);
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
		enterRule(_localctx, 212, RULE_enumConstantModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1366);
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
		enterRule(_localctx, 214, RULE_enumBodyDeclarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1368);
			match(SEMI);
			setState(1372);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE))) != 0) || ((((_la - 71)) & ~0x3f) == 0 && ((1L << (_la - 71)) & ((1L << (LBRACE - 71)) | (1L << (SEMI - 71)) | (1L << (LT - 71)) | (1L << (Identifier - 71)) | (1L << (AT - 71)))) != 0)) {
				{
				{
				setState(1369);
				classBodyDeclaration();
				}
				}
				setState(1374);
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
		enterRule(_localctx, 216, RULE_interfaceDeclaration);
		try {
			setState(1377);
			switch ( getInterpreter().adaptivePredict(_input,129,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1375);
				normalInterfaceDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1376);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 218, RULE_normalInterfaceDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1379);
			interfaceModifiers();
			setState(1380);
			match(INTERFACE);
			setState(1381);
			identifier();
			setState(1383);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1382);
				typeParameters();
				}
			}

			setState(1386);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(1385);
				extendsInterfaces();
				}
			}

			setState(1388);
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
		enterRule(_localctx, 220, RULE_interfaceModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1393);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(1390);
				interfaceModifier();
				}
				}
				setState(1395);
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
		enterRule(_localctx, 222, RULE_interfaceModifier);
		try {
			setState(1403);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1396);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1397);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1398);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1399);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1400);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(1401);
				match(STATIC);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 7);
				{
				setState(1402);
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
		enterRule(_localctx, 224, RULE_extendsInterfaces);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1405);
			match(EXTENDS);
			setState(1406);
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
		enterRule(_localctx, 226, RULE_interfaceBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1408);
			match(LBRACE);
			setState(1412);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DEFAULT) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << VOID))) != 0) || ((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & ((1L << (SEMI - 75)) | (1L << (LT - 75)) | (1L << (Identifier - 75)) | (1L << (AT - 75)))) != 0)) {
				{
				{
				setState(1409);
				interfaceMemberDeclaration();
				}
				}
				setState(1414);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1415);
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
		enterRule(_localctx, 228, RULE_interfaceMemberDeclaration);
		try {
			setState(1422);
			switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1417);
				constantDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1418);
				interfaceMethodDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1419);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1420);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1421);
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
		enterRule(_localctx, 230, RULE_constantDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1427);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FINAL) | (1L << PUBLIC) | (1L << STATIC))) != 0) || _la==AT) {
				{
				{
				setState(1424);
				constantModifier();
				}
				}
				setState(1429);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1430);
			unannType();
			setState(1431);
			variableDeclaratorList();
			setState(1432);
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
		enterRule(_localctx, 232, RULE_constantModifier);
		try {
			setState(1438);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1434);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1435);
				match(PUBLIC);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 3);
				{
				setState(1436);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(1437);
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
		enterRule(_localctx, 234, RULE_interfaceMethodDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1443);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << DEFAULT) | (1L << PRIVATE) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(1440);
				interfaceMethodModifier();
				}
				}
				setState(1445);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1446);
			methodHeader();
			setState(1447);
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
		enterRule(_localctx, 236, RULE_interfaceMethodModifier);
		try {
			setState(1456);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1449);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1450);
				match(PUBLIC);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(1451);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 4);
				{
				setState(1452);
				match(ABSTRACT);
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1453);
				match(DEFAULT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(1454);
				match(STATIC);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 7);
				{
				setState(1455);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 238, RULE_annotationTypeDeclaration);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1461);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1458);
					interfaceModifier();
					}
					} 
				}
				setState(1463);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
			}
			setState(1464);
			match(AT);
			setState(1465);
			match(INTERFACE);
			setState(1466);
			identifier();
			setState(1467);
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
		enterRule(_localctx, 240, RULE_annotationTypeBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1469);
			match(LBRACE);
			setState(1473);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP))) != 0) || ((((_la - 75)) & ~0x3f) == 0 && ((1L << (_la - 75)) & ((1L << (SEMI - 75)) | (1L << (Identifier - 75)) | (1L << (AT - 75)))) != 0)) {
				{
				{
				setState(1470);
				annotationTypeMemberDeclaration();
				}
				}
				setState(1475);
				_errHandler.sync(this);
				_la = _input.LA(1);
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
		enterRule(_localctx, 242, RULE_annotationTypeMemberDeclaration);
		try {
			setState(1483);
			switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1478);
				annotationTypeElementDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1479);
				constantDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1480);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1481);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1482);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 244, RULE_annotationTypeElementDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1488);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ABSTRACT || _la==PUBLIC || _la==AT) {
				{
				{
				setState(1485);
				annotationTypeElementModifier();
				}
				}
				setState(1490);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1491);
			unannType();
			setState(1492);
			identifier();
			setState(1493);
			match(LPAREN);
			setState(1494);
			match(RPAREN);
			setState(1496);
			_la = _input.LA(1);
			if (_la==T__11 || _la==AT) {
				{
				setState(1495);
				dims();
				}
			}

			setState(1499);
			_la = _input.LA(1);
			if (_la==DEFAULT) {
				{
				setState(1498);
				defaultValue();
				}
			}

			setState(1501);
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
		enterRule(_localctx, 246, RULE_annotationTypeElementModifier);
		try {
			setState(1506);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1503);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1504);
				match(PUBLIC);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1505);
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
		enterRule(_localctx, 248, RULE_defaultValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1508);
			match(DEFAULT);
			setState(1509);
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
		enterRule(_localctx, 250, RULE_annotation);
		try {
			setState(1514);
			switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1511);
				normalAnnotation();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1512);
				markerAnnotation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1513);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 252, RULE_annotationIdentifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1519);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1516);
				annotation();
				}
				}
				setState(1521);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1522);
			identifier();
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 254, RULE_annotationDim);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1527);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1524);
				annotation();
				}
				}
				setState(1529);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1530);
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
		enterRule(_localctx, 256, RULE_normalAnnotation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1532);
			match(AT);
			setState(1533);
			typeName();
			setState(1534);
			match(LPAREN);
			setState(1536);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) {
				{
				setState(1535);
				elementValuePairList();
				}
			}

			setState(1538);
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
		enterRule(_localctx, 258, RULE_elementValuePairList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1540);
			elementValuePair();
			setState(1545);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1541);
				match(COMMA);
				setState(1542);
				elementValuePair();
				}
				}
				setState(1547);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 260, RULE_elementValuePair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1548);
			identifier();
			setState(1549);
			match(ASSIGN);
			setState(1550);
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
		enterRule(_localctx, 262, RULE_elementValue);
		try {
			setState(1555);
			switch ( getInterpreter().adaptivePredict(_input,152,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1552);
				conditionalExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1553);
				elementValueArrayInitializer();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1554);
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
		enterRule(_localctx, 264, RULE_elementValueArrayInitializer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1557);
			match(LBRACE);
			setState(1559);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1558);
				elementValueList();
				}
			}

			setState(1562);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1561);
				match(COMMA);
				}
			}

			setState(1564);
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
		enterRule(_localctx, 266, RULE_elementValueList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1566);
			elementValue();
			setState(1571);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1567);
					match(COMMA);
					setState(1568);
					elementValue();
					}
					} 
				}
				setState(1573);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
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
		enterRule(_localctx, 268, RULE_markerAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1574);
			match(AT);
			setState(1575);
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
		enterRule(_localctx, 270, RULE_singleElementAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1577);
			match(AT);
			setState(1578);
			typeName();
			setState(1579);
			match(LPAREN);
			setState(1580);
			elementValue();
			setState(1581);
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
		enterRule(_localctx, 272, RULE_arrayInitializer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1583);
			match(LBRACE);
			setState(1585);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1584);
				variableInitializerList();
				}
			}

			setState(1588);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1587);
				match(COMMA);
				}
			}

			setState(1590);
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
		enterRule(_localctx, 274, RULE_variableInitializerList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1592);
			variableInitializer();
			setState(1597);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1593);
					match(COMMA);
					setState(1594);
					variableInitializer();
					}
					} 
				}
				setState(1599);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
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
		enterRule(_localctx, 276, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1600);
			match(LBRACE);
			setState(1602);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1601);
				blockStatements();
				}
			}

			setState(1604);
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
		enterRule(_localctx, 278, RULE_blockStatements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1606);
			blockStatement();
			setState(1610);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID) | (1L << WHILE) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				{
				setState(1607);
				blockStatement();
				}
				}
				setState(1612);
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
		enterRule(_localctx, 280, RULE_blockStatement);
		try {
			setState(1616);
			switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1613);
				localVariableDeclarationStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1614);
				classDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1615);
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
		enterRule(_localctx, 282, RULE_localVariableDeclarationStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1618);
			localVariableDeclaration();
			setState(1619);
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
		enterRule(_localctx, 284, RULE_localVariableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1624);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1621);
				variableModifier();
				}
				}
				setState(1626);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1627);
			unannType();
			setState(1628);
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
		enterRule(_localctx, 286, RULE_statement);
		try {
			setState(1636);
			switch ( getInterpreter().adaptivePredict(_input,163,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1630);
				statementWithoutTrailingSubstatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1631);
				labeledStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1632);
				ifThenStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1633);
				ifThenElseStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1634);
				whileStatement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1635);
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
		enterRule(_localctx, 288, RULE_statementNoShortIf);
		try {
			setState(1643);
			switch ( getInterpreter().adaptivePredict(_input,164,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1638);
				statementWithoutTrailingSubstatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1639);
				labeledStatementNoShortIf();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1640);
				ifThenElseStatementNoShortIf();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1641);
				whileStatementNoShortIf();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1642);
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
		enterRule(_localctx, 290, RULE_statementWithoutTrailingSubstatement);
		try {
			setState(1657);
			switch (_input.LA(1)) {
			case LBRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1645);
				block();
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
				setState(1646);
				emptyStatement();
				}
				break;
			case T__0:
			case T__1:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
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
				setState(1647);
				expressionStatement();
				}
				break;
			case ASSERT:
				enterOuterAlt(_localctx, 4);
				{
				setState(1648);
				assertStatement();
				}
				break;
			case SWITCH:
				enterOuterAlt(_localctx, 5);
				{
				setState(1649);
				switchStatement();
				}
				break;
			case DO:
				enterOuterAlt(_localctx, 6);
				{
				setState(1650);
				doStatement();
				}
				break;
			case BREAK:
				enterOuterAlt(_localctx, 7);
				{
				setState(1651);
				breakStatement();
				}
				break;
			case CONTINUE:
				enterOuterAlt(_localctx, 8);
				{
				setState(1652);
				continueStatement();
				}
				break;
			case RETURN:
				enterOuterAlt(_localctx, 9);
				{
				setState(1653);
				returnStatement();
				}
				break;
			case SYNCHRONIZED:
				enterOuterAlt(_localctx, 10);
				{
				setState(1654);
				synchronizedStatement();
				}
				break;
			case THROW:
				enterOuterAlt(_localctx, 11);
				{
				setState(1655);
				throwStatement();
				}
				break;
			case TRY:
				enterOuterAlt(_localctx, 12);
				{
				setState(1656);
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
		enterRule(_localctx, 292, RULE_emptyStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1659);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 294, RULE_labeledStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1661);
			identifier();
			setState(1662);
			match(COLON);
			setState(1663);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 296, RULE_labeledStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1665);
			identifier();
			setState(1666);
			match(COLON);
			setState(1667);
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
		enterRule(_localctx, 298, RULE_expressionStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1669);
			statementExpression();
			setState(1670);
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
		enterRule(_localctx, 300, RULE_statementExpression);
		try {
			setState(1679);
			switch ( getInterpreter().adaptivePredict(_input,166,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1672);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1673);
				preIncrementExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1674);
				preDecrementExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1675);
				postIncrementExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1676);
				postDecrementExpression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1677);
				methodInvocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1678);
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
		enterRule(_localctx, 302, RULE_ifThenStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1681);
			match(IF);
			setState(1682);
			match(LPAREN);
			setState(1683);
			expression();
			setState(1684);
			match(RPAREN);
			setState(1685);
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
		enterRule(_localctx, 304, RULE_ifThenElseStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1687);
			match(IF);
			setState(1688);
			match(LPAREN);
			setState(1689);
			expression();
			setState(1690);
			match(RPAREN);
			setState(1691);
			statementNoShortIf();
			setState(1692);
			match(ELSE);
			setState(1693);
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
		enterRule(_localctx, 306, RULE_ifThenElseStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1695);
			match(IF);
			setState(1696);
			match(LPAREN);
			setState(1697);
			expression();
			setState(1698);
			match(RPAREN);
			setState(1699);
			statementNoShortIf();
			setState(1700);
			match(ELSE);
			setState(1701);
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
		enterRule(_localctx, 308, RULE_assertStatement);
		try {
			setState(1713);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1703);
				match(ASSERT);
				setState(1704);
				expression();
				setState(1705);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1707);
				match(ASSERT);
				setState(1708);
				expression();
				setState(1709);
				match(COLON);
				setState(1710);
				expression();
				setState(1711);
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
		enterRule(_localctx, 310, RULE_switchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1715);
			match(SWITCH);
			setState(1716);
			match(LPAREN);
			setState(1717);
			expression();
			setState(1718);
			match(RPAREN);
			setState(1719);
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
		enterRule(_localctx, 312, RULE_switchBlock);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1721);
			match(LBRACE);
			setState(1725);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,168,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1722);
					switchBlockStatementGroup();
					}
					} 
				}
				setState(1727);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,168,_ctx);
			}
			setState(1731);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CASE || _la==DEFAULT) {
				{
				{
				setState(1728);
				switchLabel();
				}
				}
				setState(1733);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1734);
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
		enterRule(_localctx, 314, RULE_switchBlockStatementGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1736);
			switchLabels();
			setState(1737);
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
		enterRule(_localctx, 316, RULE_switchLabels);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1739);
			switchLabel();
			setState(1743);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CASE || _la==DEFAULT) {
				{
				{
				setState(1740);
				switchLabel();
				}
				}
				setState(1745);
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
		enterRule(_localctx, 318, RULE_switchLabel);
		try {
			setState(1756);
			switch ( getInterpreter().adaptivePredict(_input,171,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1746);
				match(CASE);
				setState(1747);
				constantExpression();
				setState(1748);
				match(COLON);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1750);
				match(CASE);
				setState(1751);
				enumConstantName();
				setState(1752);
				match(COLON);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1754);
				match(DEFAULT);
				setState(1755);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 320, RULE_enumConstantName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1758);
			identifier();
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 322, RULE_whileStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1760);
			match(WHILE);
			setState(1761);
			match(LPAREN);
			setState(1762);
			expression();
			setState(1763);
			match(RPAREN);
			setState(1764);
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
		enterRule(_localctx, 324, RULE_whileStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1766);
			match(WHILE);
			setState(1767);
			match(LPAREN);
			setState(1768);
			expression();
			setState(1769);
			match(RPAREN);
			setState(1770);
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
		enterRule(_localctx, 326, RULE_doStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1772);
			match(DO);
			setState(1773);
			statement();
			setState(1774);
			match(WHILE);
			setState(1775);
			match(LPAREN);
			setState(1776);
			expression();
			setState(1777);
			match(RPAREN);
			setState(1778);
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
		enterRule(_localctx, 328, RULE_forStatement);
		try {
			setState(1782);
			switch ( getInterpreter().adaptivePredict(_input,172,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1780);
				basicForStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1781);
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
		enterRule(_localctx, 330, RULE_forStatementNoShortIf);
		try {
			setState(1786);
			switch ( getInterpreter().adaptivePredict(_input,173,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1784);
				basicForStatementNoShortIf();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1785);
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
		enterRule(_localctx, 332, RULE_basicForStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1788);
			match(FOR);
			setState(1789);
			match(LPAREN);
			setState(1791);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1790);
				forInit();
				}
			}

			setState(1793);
			match(SEMI);
			setState(1795);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1794);
				expression();
				}
			}

			setState(1797);
			match(SEMI);
			setState(1799);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1798);
				forUpdate();
				}
			}

			setState(1801);
			match(RPAREN);
			setState(1802);
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
		enterRule(_localctx, 334, RULE_basicForStatementNoShortIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1804);
			match(FOR);
			setState(1805);
			match(LPAREN);
			setState(1807);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1806);
				forInit();
				}
			}

			setState(1809);
			match(SEMI);
			setState(1811);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1810);
				expression();
				}
			}

			setState(1813);
			match(SEMI);
			setState(1815);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1814);
				forUpdate();
				}
			}

			setState(1817);
			match(RPAREN);
			setState(1818);
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
		enterRule(_localctx, 336, RULE_forInit);
		try {
			setState(1822);
			switch ( getInterpreter().adaptivePredict(_input,180,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1820);
				statementExpressionList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1821);
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
		enterRule(_localctx, 338, RULE_forUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1824);
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
		enterRule(_localctx, 340, RULE_statementExpressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1826);
			statementExpression();
			setState(1831);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1827);
				match(COMMA);
				setState(1828);
				statementExpression();
				}
				}
				setState(1833);
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
		enterRule(_localctx, 342, RULE_enhancedForStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1834);
			match(FOR);
			setState(1835);
			match(LPAREN);
			setState(1839);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1836);
				variableModifier();
				}
				}
				setState(1841);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1842);
			unannType();
			setState(1843);
			variableDeclaratorId();
			setState(1844);
			match(COLON);
			setState(1845);
			expression();
			setState(1846);
			match(RPAREN);
			setState(1847);
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
		enterRule(_localctx, 344, RULE_enhancedForStatementNoShortIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1849);
			match(FOR);
			setState(1850);
			match(LPAREN);
			setState(1854);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1851);
				variableModifier();
				}
				}
				setState(1856);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1857);
			unannType();
			setState(1858);
			variableDeclaratorId();
			setState(1859);
			match(COLON);
			setState(1860);
			expression();
			setState(1861);
			match(RPAREN);
			setState(1862);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 346, RULE_breakStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1864);
			match(BREAK);
			setState(1866);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) {
				{
				setState(1865);
				identifier();
				}
			}

			setState(1868);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 348, RULE_continueStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1870);
			match(CONTINUE);
			setState(1872);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) {
				{
				setState(1871);
				identifier();
				}
			}

			setState(1874);
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
		enterRule(_localctx, 350, RULE_returnStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1876);
			match(RETURN);
			setState(1878);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1877);
				expression();
				}
			}

			setState(1880);
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
		enterRule(_localctx, 352, RULE_throwStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1882);
			match(THROW);
			setState(1883);
			expression();
			setState(1884);
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
		enterRule(_localctx, 354, RULE_synchronizedStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1886);
			match(SYNCHRONIZED);
			setState(1887);
			match(LPAREN);
			setState(1888);
			expression();
			setState(1889);
			match(RPAREN);
			setState(1890);
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
		enterRule(_localctx, 356, RULE_tryStatement);
		int _la;
		try {
			setState(1904);
			switch ( getInterpreter().adaptivePredict(_input,188,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1892);
				match(TRY);
				setState(1893);
				block();
				setState(1894);
				catches();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1896);
				match(TRY);
				setState(1897);
				block();
				setState(1899);
				_la = _input.LA(1);
				if (_la==CATCH) {
					{
					setState(1898);
					catches();
					}
				}

				setState(1901);
				finally_();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1903);
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
		enterRule(_localctx, 358, RULE_catches);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1906);
			catchClause();
			setState(1910);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CATCH) {
				{
				{
				setState(1907);
				catchClause();
				}
				}
				setState(1912);
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
		enterRule(_localctx, 360, RULE_catchClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1913);
			match(CATCH);
			setState(1914);
			match(LPAREN);
			setState(1915);
			catchFormalParameter();
			setState(1916);
			match(RPAREN);
			setState(1917);
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
		enterRule(_localctx, 362, RULE_catchFormalParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1922);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1919);
				variableModifier();
				}
				}
				setState(1924);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1925);
			catchType();
			setState(1926);
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
		enterRule(_localctx, 364, RULE_catchType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1928);
			unannClassType();
			setState(1933);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BITOR) {
				{
				{
				setState(1929);
				match(BITOR);
				setState(1930);
				classType();
				}
				}
				setState(1935);
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
		enterRule(_localctx, 366, RULE_finally_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1936);
			match(FINALLY);
			setState(1937);
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
		enterRule(_localctx, 368, RULE_tryWithResourcesStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1939);
			match(TRY);
			setState(1940);
			resourceSpecification();
			setState(1941);
			block();
			setState(1943);
			_la = _input.LA(1);
			if (_la==CATCH) {
				{
				setState(1942);
				catches();
				}
			}

			setState(1946);
			_la = _input.LA(1);
			if (_la==FINALLY) {
				{
				setState(1945);
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
		enterRule(_localctx, 370, RULE_resourceSpecification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1948);
			match(LPAREN);
			setState(1949);
			resourceList();
			setState(1951);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(1950);
				match(SEMI);
				}
			}

			setState(1953);
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
		enterRule(_localctx, 372, RULE_resourceList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1955);
			resource();
			setState(1960);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,195,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1956);
					match(SEMI);
					setState(1957);
					resource();
					}
					} 
				}
				setState(1962);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,195,_ctx);
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
		public VariableAccessContext variableAccess() {
			return getRuleContext(VariableAccessContext.class,0);
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
		enterRule(_localctx, 374, RULE_resource);
		int _la;
		try {
			setState(1975);
			switch ( getInterpreter().adaptivePredict(_input,197,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1966);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FINAL || _la==AT) {
					{
					{
					setState(1963);
					variableModifier();
					}
					}
					setState(1968);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1969);
				unannType();
				setState(1970);
				variableDeclaratorId();
				setState(1971);
				match(ASSIGN);
				setState(1972);
				expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1974);
				variableAccess();
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

	public static class VariableAccessContext extends ParserRuleContext {
		public ExpressionNameContext expressionName() {
			return getRuleContext(ExpressionNameContext.class,0);
		}
		public FieldAccessContext fieldAccess() {
			return getRuleContext(FieldAccessContext.class,0);
		}
		public VariableAccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableAccess; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterVariableAccess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitVariableAccess(this);
		}
	}

	@RuleVersion(0)
	public final VariableAccessContext variableAccess() throws RecognitionException {
		VariableAccessContext _localctx = new VariableAccessContext(_ctx, getState());
		enterRule(_localctx, 376, RULE_variableAccess);
		try {
			setState(1979);
			switch ( getInterpreter().adaptivePredict(_input,198,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1977);
				expressionName();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1978);
				fieldAccess();
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
		enterRule(_localctx, 378, RULE_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1983);
			switch ( getInterpreter().adaptivePredict(_input,199,_ctx) ) {
			case 1:
				{
				setState(1981);
				primaryNoNewArray_lfno_primary();
				}
				break;
			case 2:
				{
				setState(1982);
				arrayCreationExpression();
				}
				break;
			}
			setState(1988);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,200,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1985);
					primaryNoNewArray_lf_primary();
					}
					} 
				}
				setState(1990);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,200,_ctx);
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
		enterRule(_localctx, 380, RULE_primaryNoNewArray);
		int _la;
		try {
			setState(2019);
			switch ( getInterpreter().adaptivePredict(_input,202,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1991);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1992);
				typeName();
				setState(1996);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(1993);
					squareBrackets();
					}
					}
					setState(1998);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1999);
				match(DOT);
				setState(2000);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2002);
				match(VOID);
				setState(2003);
				match(DOT);
				setState(2004);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2005);
				match(THIS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2006);
				typeName();
				setState(2007);
				match(DOT);
				setState(2008);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2010);
				match(LPAREN);
				setState(2011);
				expression();
				setState(2012);
				match(RPAREN);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2014);
				classInstanceCreationExpression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2015);
				fieldAccess();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2016);
				arrayAccess();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2017);
				methodInvocation();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2018);
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
		enterRule(_localctx, 382, RULE_primaryNoNewArray_lf_arrayAccess);
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
		enterRule(_localctx, 384, RULE_primaryNoNewArray_lfno_arrayAccess);
		int _la;
		try {
			setState(2050);
			switch ( getInterpreter().adaptivePredict(_input,204,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2023);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2024);
				typeName();
				setState(2028);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(2025);
					squareBrackets();
					}
					}
					setState(2030);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2031);
				match(DOT);
				setState(2032);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2034);
				match(VOID);
				setState(2035);
				match(DOT);
				setState(2036);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2037);
				match(THIS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2038);
				typeName();
				setState(2039);
				match(DOT);
				setState(2040);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2042);
				match(LPAREN);
				setState(2043);
				expression();
				setState(2044);
				match(RPAREN);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2046);
				classInstanceCreationExpression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2047);
				fieldAccess();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2048);
				methodInvocation();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2049);
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
		enterRule(_localctx, 386, RULE_primaryNoNewArray_lf_primary);
		try {
			setState(2057);
			switch ( getInterpreter().adaptivePredict(_input,205,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2052);
				classInstanceCreationExpression_lf_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2053);
				fieldAccess_lf_primary();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2054);
				arrayAccess_lf_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2055);
				methodInvocation_lf_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2056);
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
		enterRule(_localctx, 388, RULE_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary);
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
		enterRule(_localctx, 390, RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary);
		try {
			setState(2065);
			switch ( getInterpreter().adaptivePredict(_input,206,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2061);
				classInstanceCreationExpression_lf_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2062);
				fieldAccess_lf_primary();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2063);
				methodInvocation_lf_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2064);
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
		enterRule(_localctx, 392, RULE_primaryNoNewArray_lfno_primary);
		int _la;
		try {
			setState(2105);
			switch ( getInterpreter().adaptivePredict(_input,209,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2067);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2068);
				typeName();
				setState(2072);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(2069);
					squareBrackets();
					}
					}
					setState(2074);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2075);
				match(DOT);
				setState(2076);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2078);
				unannPrimitiveType();
				setState(2082);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(2079);
					squareBrackets();
					}
					}
					setState(2084);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2085);
				match(DOT);
				setState(2086);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2088);
				match(VOID);
				setState(2089);
				match(DOT);
				setState(2090);
				match(CLASS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2091);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2092);
				typeName();
				setState(2093);
				match(DOT);
				setState(2094);
				match(THIS);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2096);
				match(LPAREN);
				setState(2097);
				expression();
				setState(2098);
				match(RPAREN);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2100);
				classInstanceCreationExpression_lfno_primary();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2101);
				fieldAccess_lfno_primary();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2102);
				arrayAccess_lfno_primary();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2103);
				methodInvocation_lfno_primary();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2104);
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
		enterRule(_localctx, 394, RULE_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary);
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
		enterRule(_localctx, 396, RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary);
		int _la;
		try {
			setState(2146);
			switch ( getInterpreter().adaptivePredict(_input,212,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2109);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2110);
				typeName();
				setState(2114);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(2111);
					squareBrackets();
					}
					}
					setState(2116);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2117);
				match(DOT);
				setState(2118);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2120);
				unannPrimitiveType();
				setState(2124);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(2121);
					squareBrackets();
					}
					}
					setState(2126);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2127);
				match(DOT);
				setState(2128);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2130);
				match(VOID);
				setState(2131);
				match(DOT);
				setState(2132);
				match(CLASS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2133);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2134);
				typeName();
				setState(2135);
				match(DOT);
				setState(2136);
				match(THIS);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2138);
				match(LPAREN);
				setState(2139);
				expression();
				setState(2140);
				match(RPAREN);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2142);
				classInstanceCreationExpression_lfno_primary();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2143);
				fieldAccess_lfno_primary();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2144);
				methodInvocation_lfno_primary();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2145);
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
		enterRule(_localctx, 398, RULE_classInstanceCreationExpression);
		int _la;
		try {
			setState(2207);
			switch ( getInterpreter().adaptivePredict(_input,226,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2148);
				match(NEW);
				setState(2150);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2149);
					typeArguments();
					}
				}

				setState(2152);
				annotationIdentifier();
				setState(2157);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(2153);
					match(DOT);
					setState(2154);
					annotationIdentifier();
					}
					}
					setState(2159);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2161);
				_la = _input.LA(1);
				if (_la==T__10 || _la==LT) {
					{
					setState(2160);
					typeArgumentsOrDiamond();
					}
				}

				setState(2163);
				match(LPAREN);
				setState(2165);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2164);
					argumentList();
					}
				}

				setState(2167);
				match(RPAREN);
				setState(2169);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2168);
					classBody();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2171);
				expressionName();
				setState(2172);
				match(DOT);
				setState(2173);
				match(NEW);
				setState(2175);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2174);
					typeArguments();
					}
				}

				setState(2177);
				annotationIdentifier();
				setState(2179);
				_la = _input.LA(1);
				if (_la==T__10 || _la==LT) {
					{
					setState(2178);
					typeArgumentsOrDiamond();
					}
				}

				setState(2181);
				match(LPAREN);
				setState(2183);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2182);
					argumentList();
					}
				}

				setState(2185);
				match(RPAREN);
				setState(2187);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2186);
					classBody();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2189);
				primary();
				setState(2190);
				match(DOT);
				setState(2191);
				match(NEW);
				setState(2193);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2192);
					typeArguments();
					}
				}

				setState(2195);
				annotationIdentifier();
				setState(2197);
				_la = _input.LA(1);
				if (_la==T__10 || _la==LT) {
					{
					setState(2196);
					typeArgumentsOrDiamond();
					}
				}

				setState(2199);
				match(LPAREN);
				setState(2201);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2200);
					argumentList();
					}
				}

				setState(2203);
				match(RPAREN);
				setState(2205);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2204);
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
		enterRule(_localctx, 400, RULE_classInstanceCreationExpression_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2209);
			match(DOT);
			setState(2210);
			match(NEW);
			setState(2212);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2211);
				typeArguments();
				}
			}

			setState(2214);
			annotationIdentifier();
			setState(2216);
			_la = _input.LA(1);
			if (_la==T__10 || _la==LT) {
				{
				setState(2215);
				typeArgumentsOrDiamond();
				}
			}

			setState(2218);
			match(LPAREN);
			setState(2220);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(2219);
				argumentList();
				}
			}

			setState(2222);
			match(RPAREN);
			setState(2224);
			switch ( getInterpreter().adaptivePredict(_input,230,_ctx) ) {
			case 1:
				{
				setState(2223);
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
		enterRule(_localctx, 402, RULE_classInstanceCreationExpression_lfno_primary);
		int _la;
		try {
			setState(2267);
			switch (_input.LA(1)) {
			case NEW:
				enterOuterAlt(_localctx, 1);
				{
				setState(2226);
				match(NEW);
				setState(2228);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2227);
					typeArguments();
					}
				}

				setState(2230);
				annotationIdentifier();
				setState(2235);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(2231);
					match(DOT);
					setState(2232);
					annotationIdentifier();
					}
					}
					setState(2237);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2239);
				_la = _input.LA(1);
				if (_la==T__10 || _la==LT) {
					{
					setState(2238);
					typeArgumentsOrDiamond();
					}
				}

				setState(2241);
				match(LPAREN);
				setState(2243);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2242);
					argumentList();
					}
				}

				setState(2245);
				match(RPAREN);
				setState(2247);
				switch ( getInterpreter().adaptivePredict(_input,235,_ctx) ) {
				case 1:
					{
					setState(2246);
					classBody();
					}
					break;
				}
				}
				break;
			case T__0:
			case T__1:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2249);
				expressionName();
				setState(2250);
				match(DOT);
				setState(2251);
				match(NEW);
				setState(2253);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2252);
					typeArguments();
					}
				}

				setState(2255);
				annotationIdentifier();
				setState(2257);
				_la = _input.LA(1);
				if (_la==T__10 || _la==LT) {
					{
					setState(2256);
					typeArgumentsOrDiamond();
					}
				}

				setState(2259);
				match(LPAREN);
				setState(2261);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2260);
					argumentList();
					}
				}

				setState(2263);
				match(RPAREN);
				setState(2265);
				switch ( getInterpreter().adaptivePredict(_input,239,_ctx) ) {
				case 1:
					{
					setState(2264);
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
		enterRule(_localctx, 404, RULE_typeArgumentsOrDiamond);
		try {
			setState(2271);
			switch (_input.LA(1)) {
			case LT:
				enterOuterAlt(_localctx, 1);
				{
				setState(2269);
				typeArguments();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 2);
				{
				setState(2270);
				match(T__10);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 406, RULE_fieldAccess);
		try {
			setState(2286);
			switch ( getInterpreter().adaptivePredict(_input,242,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2273);
				primary();
				setState(2274);
				match(DOT);
				setState(2275);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2277);
				match(SUPER);
				setState(2278);
				match(DOT);
				setState(2279);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2280);
				typeName();
				setState(2281);
				match(DOT);
				setState(2282);
				match(SUPER);
				setState(2283);
				match(DOT);
				setState(2284);
				identifier();
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 408, RULE_fieldAccess_lf_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2288);
			match(DOT);
			setState(2289);
			identifier();
			}
		}
		catch (RecognitionException re) {
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 410, RULE_fieldAccess_lfno_primary);
		try {
			setState(2300);
			switch (_input.LA(1)) {
			case SUPER:
				enterOuterAlt(_localctx, 1);
				{
				setState(2291);
				match(SUPER);
				setState(2292);
				match(DOT);
				setState(2293);
				identifier();
				}
				break;
			case T__0:
			case T__1:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case Identifier:
				enterOuterAlt(_localctx, 2);
				{
				setState(2294);
				typeName();
				setState(2295);
				match(DOT);
				setState(2296);
				match(SUPER);
				setState(2297);
				match(DOT);
				setState(2298);
				identifier();
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
		enterRule(_localctx, 412, RULE_arrayAccess);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2312);
			switch ( getInterpreter().adaptivePredict(_input,244,_ctx) ) {
			case 1:
				{
				setState(2302);
				expressionName();
				setState(2303);
				match(LBRACK);
				setState(2304);
				expression();
				setState(2305);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2307);
				primaryNoNewArray_lfno_arrayAccess();
				setState(2308);
				match(LBRACK);
				setState(2309);
				expression();
				setState(2310);
				match(RBRACK);
				}
				break;
			}
			setState(2321);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(2314);
				primaryNoNewArray_lf_arrayAccess();
				setState(2315);
				match(LBRACK);
				setState(2316);
				expression();
				setState(2317);
				match(RBRACK);
				}
				}
				setState(2323);
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
		enterRule(_localctx, 414, RULE_arrayAccess_lf_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2324);
			primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary();
			setState(2325);
			match(LBRACK);
			setState(2326);
			expression();
			setState(2327);
			match(RBRACK);
			}
			setState(2336);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,246,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2329);
					primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary();
					setState(2330);
					match(LBRACK);
					setState(2331);
					expression();
					setState(2332);
					match(RBRACK);
					}
					} 
				}
				setState(2338);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,246,_ctx);
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
		enterRule(_localctx, 416, RULE_arrayAccess_lfno_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2349);
			switch ( getInterpreter().adaptivePredict(_input,247,_ctx) ) {
			case 1:
				{
				setState(2339);
				expressionName();
				setState(2340);
				match(LBRACK);
				setState(2341);
				expression();
				setState(2342);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2344);
				primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary();
				setState(2345);
				match(LBRACK);
				setState(2346);
				expression();
				setState(2347);
				match(RBRACK);
				}
				break;
			}
			setState(2358);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,248,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2351);
					primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary();
					setState(2352);
					match(LBRACK);
					setState(2353);
					expression();
					setState(2354);
					match(RBRACK);
					}
					} 
				}
				setState(2360);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,248,_ctx);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 418, RULE_methodInvocation);
		int _la;
		try {
			setState(2430);
			switch ( getInterpreter().adaptivePredict(_input,260,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2361);
				methodName();
				setState(2362);
				match(LPAREN);
				setState(2364);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2363);
					argumentList();
					}
				}

				setState(2366);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2368);
				typeName();
				setState(2369);
				match(DOT);
				setState(2371);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2370);
					typeArguments();
					}
				}

				setState(2373);
				identifier();
				setState(2374);
				match(LPAREN);
				setState(2376);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2375);
					argumentList();
					}
				}

				setState(2378);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2380);
				expressionName();
				setState(2381);
				match(DOT);
				setState(2383);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2382);
					typeArguments();
					}
				}

				setState(2385);
				identifier();
				setState(2386);
				match(LPAREN);
				setState(2388);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2387);
					argumentList();
					}
				}

				setState(2390);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2392);
				primary();
				setState(2393);
				match(DOT);
				setState(2395);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2394);
					typeArguments();
					}
				}

				setState(2397);
				identifier();
				setState(2398);
				match(LPAREN);
				setState(2400);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2399);
					argumentList();
					}
				}

				setState(2402);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2404);
				match(SUPER);
				setState(2405);
				match(DOT);
				setState(2407);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2406);
					typeArguments();
					}
				}

				setState(2409);
				identifier();
				setState(2410);
				match(LPAREN);
				setState(2412);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2411);
					argumentList();
					}
				}

				setState(2414);
				match(RPAREN);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2416);
				typeName();
				setState(2417);
				match(DOT);
				setState(2418);
				match(SUPER);
				setState(2419);
				match(DOT);
				setState(2421);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2420);
					typeArguments();
					}
				}

				setState(2423);
				identifier();
				setState(2424);
				match(LPAREN);
				setState(2426);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2425);
					argumentList();
					}
				}

				setState(2428);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 420, RULE_methodInvocation_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2432);
			match(DOT);
			setState(2434);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2433);
				typeArguments();
				}
			}

			setState(2436);
			identifier();
			setState(2437);
			match(LPAREN);
			setState(2439);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(2438);
				argumentList();
				}
			}

			setState(2441);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 422, RULE_methodInvocation_lfno_primary);
		int _la;
		try {
			setState(2500);
			switch ( getInterpreter().adaptivePredict(_input,272,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2443);
				methodName();
				setState(2444);
				match(LPAREN);
				setState(2446);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2445);
					argumentList();
					}
				}

				setState(2448);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2450);
				typeName();
				setState(2451);
				match(DOT);
				setState(2453);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2452);
					typeArguments();
					}
				}

				setState(2455);
				identifier();
				setState(2456);
				match(LPAREN);
				setState(2458);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2457);
					argumentList();
					}
				}

				setState(2460);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2462);
				expressionName();
				setState(2463);
				match(DOT);
				setState(2465);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2464);
					typeArguments();
					}
				}

				setState(2467);
				identifier();
				setState(2468);
				match(LPAREN);
				setState(2470);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2469);
					argumentList();
					}
				}

				setState(2472);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2474);
				match(SUPER);
				setState(2475);
				match(DOT);
				setState(2477);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2476);
					typeArguments();
					}
				}

				setState(2479);
				identifier();
				setState(2480);
				match(LPAREN);
				setState(2482);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2481);
					argumentList();
					}
				}

				setState(2484);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2486);
				typeName();
				setState(2487);
				match(DOT);
				setState(2488);
				match(SUPER);
				setState(2489);
				match(DOT);
				setState(2491);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2490);
					typeArguments();
					}
				}

				setState(2493);
				identifier();
				setState(2494);
				match(LPAREN);
				setState(2496);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID) | (1L << IntegerLiteral))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (BANG - 64)) | (1L << (TILDE - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (ADD - 64)) | (1L << (SUB - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
					{
					setState(2495);
					argumentList();
					}
				}

				setState(2498);
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
		enterRule(_localctx, 424, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2502);
			expression();
			setState(2507);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2503);
				match(COMMA);
				setState(2504);
				expression();
				}
				}
				setState(2509);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 426, RULE_methodReference);
		int _la;
		try {
			setState(2557);
			switch ( getInterpreter().adaptivePredict(_input,280,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2510);
				expressionName();
				setState(2511);
				match(COLONCOLON);
				setState(2513);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2512);
					typeArguments();
					}
				}

				setState(2515);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2517);
				referenceType();
				setState(2518);
				match(COLONCOLON);
				setState(2520);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2519);
					typeArguments();
					}
				}

				setState(2522);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2524);
				primary();
				setState(2525);
				match(COLONCOLON);
				setState(2527);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2526);
					typeArguments();
					}
				}

				setState(2529);
				identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2531);
				match(SUPER);
				setState(2532);
				match(COLONCOLON);
				setState(2534);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2533);
					typeArguments();
					}
				}

				setState(2536);
				identifier();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2537);
				typeName();
				setState(2538);
				match(DOT);
				setState(2539);
				match(SUPER);
				setState(2540);
				match(COLONCOLON);
				setState(2542);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2541);
					typeArguments();
					}
				}

				setState(2544);
				identifier();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2546);
				classType();
				setState(2547);
				match(COLONCOLON);
				setState(2549);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2548);
					typeArguments();
					}
				}

				setState(2551);
				match(NEW);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2553);
				arrayType();
				setState(2554);
				match(COLONCOLON);
				setState(2555);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 428, RULE_methodReference_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2559);
			match(COLONCOLON);
			setState(2561);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2560);
				typeArguments();
				}
			}

			setState(2563);
			identifier();
			}
		}
		catch (RecognitionException re) {
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 430, RULE_methodReference_lfno_primary);
		int _la;
		try {
			setState(2605);
			switch ( getInterpreter().adaptivePredict(_input,287,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2565);
				expressionName();
				setState(2566);
				match(COLONCOLON);
				setState(2568);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2567);
					typeArguments();
					}
				}

				setState(2570);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2572);
				referenceType();
				setState(2573);
				match(COLONCOLON);
				setState(2575);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2574);
					typeArguments();
					}
				}

				setState(2577);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2579);
				match(SUPER);
				setState(2580);
				match(COLONCOLON);
				setState(2582);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2581);
					typeArguments();
					}
				}

				setState(2584);
				identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2585);
				typeName();
				setState(2586);
				match(DOT);
				setState(2587);
				match(SUPER);
				setState(2588);
				match(COLONCOLON);
				setState(2590);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2589);
					typeArguments();
					}
				}

				setState(2592);
				identifier();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2594);
				classType();
				setState(2595);
				match(COLONCOLON);
				setState(2597);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2596);
					typeArguments();
					}
				}

				setState(2599);
				match(NEW);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2601);
				arrayType();
				setState(2602);
				match(COLONCOLON);
				setState(2603);
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
		enterRule(_localctx, 432, RULE_arrayCreationExpression);
		try {
			setState(2629);
			switch ( getInterpreter().adaptivePredict(_input,290,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2607);
				match(NEW);
				setState(2608);
				primitiveType();
				setState(2609);
				dimExprs();
				setState(2611);
				switch ( getInterpreter().adaptivePredict(_input,288,_ctx) ) {
				case 1:
					{
					setState(2610);
					dims();
					}
					break;
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2613);
				match(NEW);
				setState(2614);
				classOrInterfaceType();
				setState(2615);
				dimExprs();
				setState(2617);
				switch ( getInterpreter().adaptivePredict(_input,289,_ctx) ) {
				case 1:
					{
					setState(2616);
					dims();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2619);
				match(NEW);
				setState(2620);
				primitiveType();
				setState(2621);
				dims();
				setState(2622);
				arrayInitializer();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2624);
				match(NEW);
				setState(2625);
				classOrInterfaceType();
				setState(2626);
				dims();
				setState(2627);
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
		enterRule(_localctx, 434, RULE_dimExprs);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2631);
			dimExpr();
			setState(2635);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,291,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2632);
					dimExpr();
					}
					} 
				}
				setState(2637);
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
		enterRule(_localctx, 436, RULE_dimExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2641);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(2638);
				annotation();
				}
				}
				setState(2643);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(2644);
			match(LBRACK);
			setState(2645);
			expression();
			setState(2646);
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
		enterRule(_localctx, 438, RULE_constantExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2648);
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
		enterRule(_localctx, 440, RULE_expression);
		try {
			setState(2652);
			switch ( getInterpreter().adaptivePredict(_input,293,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2650);
				lambdaExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2651);
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
		enterRule(_localctx, 442, RULE_lambdaExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2654);
			lambdaParameters();
			setState(2655);
			match(ARROW);
			setState(2656);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 444, RULE_lambdaParameters);
		int _la;
		try {
			setState(2668);
			switch ( getInterpreter().adaptivePredict(_input,295,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2658);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2659);
				match(LPAREN);
				setState(2661);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
					{
					setState(2660);
					formalParameterList();
					}
				}

				setState(2663);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2664);
				match(LPAREN);
				setState(2665);
				inferredFormalParameterList();
				setState(2666);
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
		public List<? extends IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
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
		enterRule(_localctx, 446, RULE_inferredFormalParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2670);
			identifier();
			setState(2675);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2671);
				match(COMMA);
				setState(2672);
				identifier();
				}
				}
				setState(2677);
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
		enterRule(_localctx, 448, RULE_lambdaBody);
		try {
			setState(2680);
			switch (_input.LA(1)) {
			case T__0:
			case T__1:
			case T__2:
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
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
				setState(2678);
				expression();
				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(2679);
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
		enterRule(_localctx, 450, RULE_assignmentExpression);
		try {
			setState(2684);
			switch ( getInterpreter().adaptivePredict(_input,298,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2682);
				conditionalExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2683);
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
		enterRule(_localctx, 452, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2686);
			leftHandSide();
			setState(2687);
			assignmentOperator();
			setState(2688);
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
		public VariableAccessContext variableAccess() {
			return getRuleContext(VariableAccessContext.class,0);
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
		enterRule(_localctx, 454, RULE_leftHandSide);
		try {
			setState(2692);
			switch ( getInterpreter().adaptivePredict(_input,299,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2690);
				variableAccess();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2691);
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
		enterRule(_localctx, 456, RULE_assignmentOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2694);
			_la = _input.LA(1);
			if ( !(((((_la - 78)) & ~0x3f) == 0 && ((1L << (_la - 78)) & ((1L << (ASSIGN - 78)) | (1L << (ADD_ASSIGN - 78)) | (1L << (SUB_ASSIGN - 78)) | (1L << (MUL_ASSIGN - 78)) | (1L << (DIV_ASSIGN - 78)) | (1L << (AND_ASSIGN - 78)) | (1L << (OR_ASSIGN - 78)) | (1L << (XOR_ASSIGN - 78)) | (1L << (MOD_ASSIGN - 78)) | (1L << (LSHIFT_ASSIGN - 78)) | (1L << (RSHIFT_ASSIGN - 78)) | (1L << (URSHIFT_ASSIGN - 78)))) != 0)) ) {
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
		enterRule(_localctx, 458, RULE_additiveOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2696);
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
		enterRule(_localctx, 460, RULE_relationalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2698);
			_la = _input.LA(1);
			if ( !(((((_la - 38)) & ~0x3f) == 0 && ((1L << (_la - 38)) & ((1L << (INSTANCEOF - 38)) | (1L << (GT - 38)) | (1L << (LT - 38)) | (1L << (LE - 38)) | (1L << (GE - 38)))) != 0)) ) {
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
		enterRule(_localctx, 462, RULE_multiplicativeOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2700);
			_la = _input.LA(1);
			if ( !(((((_la - 95)) & ~0x3f) == 0 && ((1L << (_la - 95)) & ((1L << (MUL - 95)) | (1L << (DIV - 95)) | (1L << (MOD - 95)))) != 0)) ) {
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
		enterRule(_localctx, 464, RULE_squareBrackets);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2702);
			match(T__11);
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 466, RULE_conditionalExpression);
		try {
			setState(2717);
			switch ( getInterpreter().adaptivePredict(_input,300,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2704);
				conditionalOrExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2705);
				conditionalOrExpression(0);
				setState(2706);
				match(QUESTION);
				setState(2707);
				expression();
				setState(2708);
				match(COLON);
				setState(2709);
				conditionalExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2711);
				conditionalOrExpression(0);
				setState(2712);
				match(QUESTION);
				setState(2713);
				expression();
				setState(2714);
				match(COLON);
				setState(2715);
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
		int _startState = 468;
		enterRecursionRule(_localctx, 468, RULE_conditionalOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2720);
			conditionalAndExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2727);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,301,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionalOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_conditionalOrExpression);
					setState(2722);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2723);
					match(OR);
					setState(2724);
					conditionalAndExpression(0);
					}
					} 
				}
				setState(2729);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,301,_ctx);
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
		int _startState = 470;
		enterRecursionRule(_localctx, 470, RULE_conditionalAndExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2731);
			inclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2738);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,302,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionalAndExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_conditionalAndExpression);
					setState(2733);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2734);
					match(AND);
					setState(2735);
					inclusiveOrExpression(0);
					}
					} 
				}
				setState(2740);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,302,_ctx);
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
		int _startState = 472;
		enterRecursionRule(_localctx, 472, RULE_inclusiveOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2742);
			exclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2749);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,303,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new InclusiveOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_inclusiveOrExpression);
					setState(2744);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2745);
					match(BITOR);
					setState(2746);
					exclusiveOrExpression(0);
					}
					} 
				}
				setState(2751);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,303,_ctx);
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
		int _startState = 474;
		enterRecursionRule(_localctx, 474, RULE_exclusiveOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2753);
			andExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2760);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,304,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExclusiveOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_exclusiveOrExpression);
					setState(2755);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2756);
					match(CARET);
					setState(2757);
					andExpression(0);
					}
					} 
				}
				setState(2762);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,304,_ctx);
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
		int _startState = 476;
		enterRecursionRule(_localctx, 476, RULE_andExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2764);
			equalityExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2771);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,305,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AndExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_andExpression);
					setState(2766);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2767);
					match(BITAND);
					setState(2768);
					equalityExpression(0);
					}
					} 
				}
				setState(2773);
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
		int _startState = 478;
		enterRecursionRule(_localctx, 478, RULE_equalityExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2775);
			relationalExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2785);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,307,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2783);
					switch ( getInterpreter().adaptivePredict(_input,306,_ctx) ) {
					case 1:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2777);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2778);
						match(EQUAL);
						setState(2779);
						relationalExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2780);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2781);
						match(NOTEQUAL);
						setState(2782);
						relationalExpression(0);
						}
						break;
					}
					} 
				}
				setState(2787);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,307,_ctx);
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
		int _startState = 480;
		enterRecursionRule(_localctx, 480, RULE_relationalExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2789);
			shiftExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2813);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,309,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2811);
					switch ( getInterpreter().adaptivePredict(_input,308,_ctx) ) {
					case 1:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2791);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(2792);
						relationalOperator();
						setState(2793);
						shiftExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2795);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(2796);
						relationalOperator();
						setState(2797);
						shiftExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2799);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2800);
						relationalOperator();
						setState(2801);
						shiftExpression(0);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2803);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2804);
						relationalOperator();
						setState(2805);
						shiftExpression(0);
						}
						break;
					case 5:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2807);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2808);
						relationalOperator();
						setState(2809);
						referenceType();
						}
						break;
					}
					} 
				}
				setState(2815);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,309,_ctx);
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
		int _startState = 482;
		enterRecursionRule(_localctx, 482, RULE_shiftExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2817);
			additiveExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2833);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,311,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2831);
					switch ( getInterpreter().adaptivePredict(_input,310,_ctx) ) {
					case 1:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2819);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2820);
						shiftOperator();
						setState(2821);
						additiveExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2823);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2824);
						shiftOperator();
						setState(2825);
						additiveExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2827);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2828);
						shiftOperator();
						setState(2829);
						additiveExpression(0);
						}
						break;
					}
					} 
				}
				setState(2835);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,311,_ctx);
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
		enterRule(_localctx, 484, RULE_shiftOperator);
		try {
			setState(2843);
			switch ( getInterpreter().adaptivePredict(_input,312,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2836);
				match(LT);
				setState(2837);
				match(LT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2838);
				match(GT);
				setState(2839);
				match(GT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2840);
				match(GT);
				setState(2841);
				match(GT);
				setState(2842);
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
		int _startState = 486;
		enterRecursionRule(_localctx, 486, RULE_additiveExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2846);
			multiplicativeExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2858);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,314,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2856);
					switch ( getInterpreter().adaptivePredict(_input,313,_ctx) ) {
					case 1:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2848);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2849);
						additiveOperator();
						setState(2850);
						multiplicativeExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2852);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2853);
						additiveOperator();
						setState(2854);
						multiplicativeExpression(0);
						}
						break;
					}
					} 
				}
				setState(2860);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,314,_ctx);
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
		int _startState = 488;
		enterRecursionRule(_localctx, 488, RULE_multiplicativeExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2862);
			unaryExpression();
			}
			_ctx.stop = _input.LT(-1);
			setState(2878);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,316,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2876);
					switch ( getInterpreter().adaptivePredict(_input,315,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2864);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2865);
						multiplicativeOperator();
						setState(2866);
						unaryExpression();
						}
						break;
					case 2:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2868);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2869);
						multiplicativeOperator();
						setState(2870);
						unaryExpression();
						}
						break;
					case 3:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2872);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2873);
						multiplicativeOperator();
						setState(2874);
						unaryExpression();
						}
						break;
					}
					} 
				}
				setState(2880);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,316,_ctx);
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
		enterRule(_localctx, 490, RULE_unaryExpression);
		try {
			setState(2890);
			switch ( getInterpreter().adaptivePredict(_input,317,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2881);
				preIncrementExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2882);
				preDecrementExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2883);
				additiveOperator();
				setState(2884);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2886);
				additiveOperator();
				setState(2887);
				unaryExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2889);
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
		enterRule(_localctx, 492, RULE_preIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2892);
			match(INC);
			setState(2893);
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
		enterRule(_localctx, 494, RULE_preDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2895);
			match(DEC);
			setState(2896);
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
		enterRule(_localctx, 496, RULE_unaryExpressionNotPlusMinus);
		try {
			setState(2904);
			switch ( getInterpreter().adaptivePredict(_input,318,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2898);
				postfixExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2899);
				match(TILDE);
				setState(2900);
				unaryExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2901);
				match(BANG);
				setState(2902);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2903);
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
		enterRule(_localctx, 498, RULE_postfixExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2908);
			switch ( getInterpreter().adaptivePredict(_input,319,_ctx) ) {
			case 1:
				{
				setState(2906);
				primary();
				}
				break;
			case 2:
				{
				setState(2907);
				expressionName();
				}
				break;
			}
			setState(2914);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,321,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(2912);
					switch (_input.LA(1)) {
					case INC:
						{
						setState(2910);
						postIncrementExpression_lf_postfixExpression();
						}
						break;
					case DEC:
						{
						setState(2911);
						postDecrementExpression_lf_postfixExpression();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(2916);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,321,_ctx);
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
		enterRule(_localctx, 500, RULE_postIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2917);
			postfixExpression();
			setState(2918);
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
		enterRule(_localctx, 502, RULE_postIncrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2920);
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
		enterRule(_localctx, 504, RULE_postDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2922);
			postfixExpression();
			setState(2923);
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
		enterRule(_localctx, 506, RULE_postDecrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2925);
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
		enterRule(_localctx, 508, RULE_castExpression);
		int _la;
		try {
			setState(2954);
			switch ( getInterpreter().adaptivePredict(_input,324,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2927);
				match(LPAREN);
				setState(2928);
				primitiveType();
				setState(2929);
				match(RPAREN);
				setState(2930);
				unaryExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2932);
				match(LPAREN);
				setState(2933);
				referenceType();
				setState(2937);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2934);
					additionalBound();
					}
					}
					setState(2939);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2940);
				match(RPAREN);
				setState(2941);
				unaryExpressionNotPlusMinus();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2943);
				match(LPAREN);
				setState(2944);
				referenceType();
				setState(2948);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2945);
					additionalBound();
					}
					}
					setState(2950);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2951);
				match(RPAREN);
				setState(2952);
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
		case 27:
			return packageName_sempred((PackageNameContext)_localctx, predIndex);
		case 29:
			return packageOrTypeName_sempred((PackageOrTypeNameContext)_localctx, predIndex);
		case 32:
			return ambiguousName_sempred((AmbiguousNameContext)_localctx, predIndex);
		case 45:
			return moduleName_sempred((ModuleNameContext)_localctx, predIndex);
		case 234:
			return conditionalOrExpression_sempred((ConditionalOrExpressionContext)_localctx, predIndex);
		case 235:
			return conditionalAndExpression_sempred((ConditionalAndExpressionContext)_localctx, predIndex);
		case 236:
			return inclusiveOrExpression_sempred((InclusiveOrExpressionContext)_localctx, predIndex);
		case 237:
			return exclusiveOrExpression_sempred((ExclusiveOrExpressionContext)_localctx, predIndex);
		case 238:
			return andExpression_sempred((AndExpressionContext)_localctx, predIndex);
		case 239:
			return equalityExpression_sempred((EqualityExpressionContext)_localctx, predIndex);
		case 240:
			return relationalExpression_sempred((RelationalExpressionContext)_localctx, predIndex);
		case 241:
			return shiftExpression_sempred((ShiftExpressionContext)_localctx, predIndex);
		case 243:
			return additiveExpression_sempred((AdditiveExpressionContext)_localctx, predIndex);
		case 244:
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
	private boolean moduleName_sempred(ModuleNameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean conditionalOrExpression_sempred(ConditionalOrExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean conditionalAndExpression_sempred(ConditionalAndExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean inclusiveOrExpression_sempred(InclusiveOrExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean exclusiveOrExpression_sempred(ExclusiveOrExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean andExpression_sempred(AndExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 8:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean equalityExpression_sempred(EqualityExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 9:
			return precpred(_ctx, 2);
		case 10:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean relationalExpression_sempred(RelationalExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 11:
			return precpred(_ctx, 5);
		case 12:
			return precpred(_ctx, 4);
		case 13:
			return precpred(_ctx, 3);
		case 14:
			return precpred(_ctx, 2);
		case 15:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean shiftExpression_sempred(ShiftExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 16:
			return precpred(_ctx, 3);
		case 17:
			return precpred(_ctx, 2);
		case 18:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean additiveExpression_sempred(AdditiveExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 19:
			return precpred(_ctx, 2);
		case 20:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean multiplicativeExpression_sempred(MultiplicativeExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 21:
			return precpred(_ctx, 3);
		case 22:
			return precpred(_ctx, 2);
		case 23:
			return precpred(_ctx, 1);
		}
		return true;
	}

	private static final int _serializedATNSegments = 2;
	private static final String _serializedATNSegment0 =
		"\3\uaf6f\u8320\u479d\ub75c\u4880\u1605\u191c\uab37\3z\u0b8f\4\2\t\2\4"+
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
		"\t\u00fa\4\u00fb\t\u00fb\4\u00fc\t\u00fc\4\u00fd\t\u00fd\4\u00fe\t\u00fe"+
		"\4\u00ff\t\u00ff\4\u0100\t\u0100\3\2\3\2\3\3\3\3\3\4\3\4\5\4\u0207\n\4"+
		"\3\5\7\5\u020a\n\5\f\5\16\5\u020d\13\5\3\5\3\5\7\5\u0211\n\5\f\5\16\5"+
		"\u0214\13\5\3\5\5\5\u0217\n\5\3\6\3\6\5\6\u021b\n\6\3\7\3\7\3\b\3\b\3"+
		"\t\3\t\3\t\5\t\u0224\n\t\3\n\3\n\5\n\u0228\n\n\3\n\3\n\7\n\u022c\n\n\f"+
		"\n\16\n\u022f\13\n\3\13\3\13\5\13\u0233\n\13\3\13\3\13\3\13\3\13\5\13"+
		"\u0239\n\13\5\13\u023b\n\13\3\f\3\f\3\f\5\f\u0240\n\f\3\r\3\r\5\r\u0244"+
		"\n\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\5\22\u0257\n\22\3\23\3\23\7\23\u025b\n\23\f\23\16"+
		"\23\u025e\13\23\3\24\7\24\u0261\n\24\f\24\16\24\u0264\13\24\3\24\3\24"+
		"\5\24\u0268\n\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26\7\26\u0271\n\26\f"+
		"\26\16\26\u0274\13\26\5\26\u0276\n\26\3\27\3\27\3\27\3\30\3\30\3\30\3"+
		"\30\3\31\3\31\3\31\7\31\u0282\n\31\f\31\16\31\u0285\13\31\3\32\3\32\5"+
		"\32\u0289\n\32\3\33\7\33\u028c\n\33\f\33\16\33\u028f\13\33\3\33\3\33\5"+
		"\33\u0293\n\33\3\34\3\34\3\34\3\34\5\34\u0299\n\34\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\7\35\u02a1\n\35\f\35\16\35\u02a4\13\35\3\36\3\36\3\36\3\36"+
		"\3\36\5\36\u02ab\n\36\3\37\3\37\3\37\3\37\3\37\3\37\7\37\u02b3\n\37\f"+
		"\37\16\37\u02b6\13\37\3 \3 \3 \3 \3 \5 \u02bd\n \3!\3!\3\"\3\"\3\"\3\""+
		"\3\"\3\"\7\"\u02c7\n\"\f\"\16\"\u02ca\13\"\3#\3#\3#\3#\5#\u02d0\n#\3#"+
		"\7#\u02d3\n#\f#\16#\u02d6\13#\3#\7#\u02d9\n#\f#\16#\u02dc\13#\3#\3#\7"+
		"#\u02e0\n#\f#\16#\u02e3\13#\3#\3#\3#\5#\u02e8\n#\3$\7$\u02eb\n$\f$\16"+
		"$\u02ee\13$\3$\3$\3$\3$\7$\u02f4\n$\f$\16$\u02f7\13$\3$\3$\3%\3%\3&\3"+
		"&\3&\3&\5&\u0301\n&\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3"+
		")\3)\3*\3*\3*\3*\3*\3*\3*\3+\3+\3+\5+\u031e\n+\3,\7,\u0321\n,\f,\16,\u0324"+
		"\13,\3,\5,\u0327\n,\3,\3,\3,\3,\7,\u032d\n,\f,\16,\u0330\13,\3,\3,\7,"+
		"\u0334\n,\f,\16,\u0337\13,\3,\3,\3-\3-\7-\u033d\n-\f-\16-\u0340\13-\3"+
		"-\3-\3-\3-\3-\3-\3-\3-\3-\7-\u034b\n-\f-\16-\u034e\13-\5-\u0350\n-\3-"+
		"\3-\3-\3-\3-\3-\3-\3-\7-\u035a\n-\f-\16-\u035d\13-\5-\u035f\n-\3-\3-\3"+
		"-\3-\3-\3-\3-\3-\3-\3-\3-\3-\7-\u036d\n-\f-\16-\u0370\13-\3-\3-\5-\u0374"+
		"\n-\3.\3.\3/\3/\3/\3/\3/\3/\7/\u037e\n/\f/\16/\u0381\13/\3\60\3\60\5\60"+
		"\u0385\n\60\3\61\3\61\3\61\3\61\5\61\u038b\n\61\3\61\5\61\u038e\n\61\3"+
		"\61\5\61\u0391\n\61\3\61\3\61\3\62\7\62\u0396\n\62\f\62\16\62\u0399\13"+
		"\62\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u03a3\n\63\3\64\3\64"+
		"\3\64\3\64\3\65\3\65\3\65\7\65\u03ac\n\65\f\65\16\65\u03af\13\65\3\66"+
		"\3\66\3\66\3\67\3\67\3\67\38\38\38\78\u03ba\n8\f8\168\u03bd\138\39\39"+
		"\79\u03c1\n9\f9\169\u03c4\139\39\39\3:\3:\3:\3:\5:\u03cc\n:\3;\3;\3;\3"+
		";\3;\5;\u03d3\n;\3<\3<\3<\3<\3<\3=\7=\u03db\n=\f=\16=\u03de\13=\3>\3>"+
		"\3>\3>\3>\3>\3>\3>\5>\u03e8\n>\3?\3?\3?\7?\u03ed\n?\f?\16?\u03f0\13?\3"+
		"@\3@\3@\5@\u03f5\n@\3A\3A\5A\u03f9\nA\3B\3B\5B\u03fd\nB\3C\3C\5C\u0401"+
		"\nC\3D\3D\5D\u0405\nD\3E\3E\3E\5E\u040a\nE\3F\3F\5F\u040e\nF\3F\3F\7F"+
		"\u0412\nF\fF\16F\u0415\13F\3G\3G\5G\u0419\nG\3G\3G\3G\3G\5G\u041f\nG\5"+
		"G\u0421\nG\3H\3H\3H\5H\u0426\nH\3I\3I\5I\u042a\nI\3J\3J\3K\3K\3L\3L\3"+
		"M\3M\3N\3N\3N\3N\3N\3N\3N\3N\3N\5N\u043d\nN\3O\3O\3O\3O\3P\7P\u0444\n"+
		"P\fP\16P\u0447\13P\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\5Q\u0453\nQ\3R\3R\3R"+
		"\5R\u0458\nR\3R\3R\7R\u045c\nR\fR\16R\u045f\13R\3R\3R\3R\5R\u0464\nR\5"+
		"R\u0466\nR\3S\3S\5S\u046a\nS\3T\3T\3T\5T\u046f\nT\3T\3T\5T\u0473\nT\3"+
		"U\3U\3U\3U\3U\5U\u047a\nU\3V\3V\3V\7V\u047f\nV\fV\16V\u0482\13V\3V\3V"+
		"\3V\7V\u0487\nV\fV\16V\u048a\13V\5V\u048c\nV\3W\7W\u048f\nW\fW\16W\u0492"+
		"\13W\3W\3W\3W\3X\3X\5X\u0499\nX\3Y\7Y\u049c\nY\fY\16Y\u049f\13Y\3Y\3Y"+
		"\7Y\u04a3\nY\fY\16Y\u04a6\13Y\3Y\3Y\3Y\3Y\5Y\u04ac\nY\3Z\7Z\u04af\nZ\f"+
		"Z\16Z\u04b2\13Z\3Z\3Z\3Z\3Z\5Z\u04b8\nZ\3Z\3Z\3[\3[\3[\3\\\3\\\3\\\7\\"+
		"\u04c2\n\\\f\\\16\\\u04c5\13\\\3]\3]\5]\u04c9\n]\3^\3^\5^\u04cd\n^\3_"+
		"\3_\3`\3`\3`\3a\3a\3a\5a\u04d7\na\3a\3a\3b\7b\u04dc\nb\fb\16b\u04df\13"+
		"b\3c\3c\3c\3c\5c\u04e5\nc\3d\5d\u04e8\nd\3d\3d\3d\5d\u04ed\nd\3d\3d\3"+
		"e\3e\3f\3f\5f\u04f5\nf\3f\5f\u04f8\nf\3f\3f\3g\5g\u04fd\ng\3g\3g\3g\5"+
		"g\u0502\ng\3g\3g\3g\5g\u0507\ng\3g\3g\3g\5g\u050c\ng\3g\3g\3g\3g\3g\5"+
		"g\u0513\ng\3g\3g\3g\5g\u0518\ng\3g\3g\3g\3g\3g\3g\5g\u0520\ng\3g\3g\3"+
		"g\5g\u0525\ng\3g\3g\3g\5g\u052a\ng\3h\3h\3h\3h\5h\u0530\nh\3h\3h\3i\3"+
		"i\5i\u0536\ni\3i\5i\u0539\ni\3i\5i\u053c\ni\3i\3i\3j\3j\3j\7j\u0543\n"+
		"j\fj\16j\u0546\13j\3k\7k\u0549\nk\fk\16k\u054c\13k\3k\3k\3k\5k\u0551\n"+
		"k\3k\5k\u0554\nk\3k\5k\u0557\nk\3l\3l\3m\3m\7m\u055d\nm\fm\16m\u0560\13"+
		"m\3n\3n\5n\u0564\nn\3o\3o\3o\3o\5o\u056a\no\3o\5o\u056d\no\3o\3o\3p\7"+
		"p\u0572\np\fp\16p\u0575\13p\3q\3q\3q\3q\3q\3q\3q\5q\u057e\nq\3r\3r\3r"+
		"\3s\3s\7s\u0585\ns\fs\16s\u0588\13s\3s\3s\3t\3t\3t\3t\3t\5t\u0591\nt\3"+
		"u\7u\u0594\nu\fu\16u\u0597\13u\3u\3u\3u\3u\3v\3v\3v\3v\5v\u05a1\nv\3w"+
		"\7w\u05a4\nw\fw\16w\u05a7\13w\3w\3w\3w\3x\3x\3x\3x\3x\3x\3x\5x\u05b3\n"+
		"x\3y\7y\u05b6\ny\fy\16y\u05b9\13y\3y\3y\3y\3y\3y\3z\3z\7z\u05c2\nz\fz"+
		"\16z\u05c5\13z\3z\3z\3{\3{\3{\3{\3{\5{\u05ce\n{\3|\7|\u05d1\n|\f|\16|"+
		"\u05d4\13|\3|\3|\3|\3|\3|\5|\u05db\n|\3|\5|\u05de\n|\3|\3|\3}\3}\3}\5"+
		"}\u05e5\n}\3~\3~\3~\3\177\3\177\3\177\5\177\u05ed\n\177\3\u0080\7\u0080"+
		"\u05f0\n\u0080\f\u0080\16\u0080\u05f3\13\u0080\3\u0080\3\u0080\3\u0081"+
		"\7\u0081\u05f8\n\u0081\f\u0081\16\u0081\u05fb\13\u0081\3\u0081\3\u0081"+
		"\3\u0082\3\u0082\3\u0082\3\u0082\5\u0082\u0603\n\u0082\3\u0082\3\u0082"+
		"\3\u0083\3\u0083\3\u0083\7\u0083\u060a\n\u0083\f\u0083\16\u0083\u060d"+
		"\13\u0083\3\u0084\3\u0084\3\u0084\3\u0084\3\u0085\3\u0085\3\u0085\5\u0085"+
		"\u0616\n\u0085\3\u0086\3\u0086\5\u0086\u061a\n\u0086\3\u0086\5\u0086\u061d"+
		"\n\u0086\3\u0086\3\u0086\3\u0087\3\u0087\3\u0087\7\u0087\u0624\n\u0087"+
		"\f\u0087\16\u0087\u0627\13\u0087\3\u0088\3\u0088\3\u0088\3\u0089\3\u0089"+
		"\3\u0089\3\u0089\3\u0089\3\u0089\3\u008a\3\u008a\5\u008a\u0634\n\u008a"+
		"\3\u008a\5\u008a\u0637\n\u008a\3\u008a\3\u008a\3\u008b\3\u008b\3\u008b"+
		"\7\u008b\u063e\n\u008b\f\u008b\16\u008b\u0641\13\u008b\3\u008c\3\u008c"+
		"\5\u008c\u0645\n\u008c\3\u008c\3\u008c\3\u008d\3\u008d\7\u008d\u064b\n"+
		"\u008d\f\u008d\16\u008d\u064e\13\u008d\3\u008e\3\u008e\3\u008e\5\u008e"+
		"\u0653\n\u008e\3\u008f\3\u008f\3\u008f\3\u0090\7\u0090\u0659\n\u0090\f"+
		"\u0090\16\u0090\u065c\13\u0090\3\u0090\3\u0090\3\u0090\3\u0091\3\u0091"+
		"\3\u0091\3\u0091\3\u0091\3\u0091\5\u0091\u0667\n\u0091\3\u0092\3\u0092"+
		"\3\u0092\3\u0092\3\u0092\5\u0092\u066e\n\u0092\3\u0093\3\u0093\3\u0093"+
		"\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093"+
		"\5\u0093\u067c\n\u0093\3\u0094\3\u0094\3\u0095\3\u0095\3\u0095\3\u0095"+
		"\3\u0096\3\u0096\3\u0096\3\u0096\3\u0097\3\u0097\3\u0097\3\u0098\3\u0098"+
		"\3\u0098\3\u0098\3\u0098\3\u0098\3\u0098\5\u0098\u0692\n\u0098\3\u0099"+
		"\3\u0099\3\u0099\3\u0099\3\u0099\3\u0099\3\u009a\3\u009a\3\u009a\3\u009a"+
		"\3\u009a\3\u009a\3\u009a\3\u009a\3\u009b\3\u009b\3\u009b\3\u009b\3\u009b"+
		"\3\u009b\3\u009b\3\u009b\3\u009c\3\u009c\3\u009c\3\u009c\3\u009c\3\u009c"+
		"\3\u009c\3\u009c\3\u009c\3\u009c\5\u009c\u06b4\n\u009c\3\u009d\3\u009d"+
		"\3\u009d\3\u009d\3\u009d\3\u009d\3\u009e\3\u009e\7\u009e\u06be\n\u009e"+
		"\f\u009e\16\u009e\u06c1\13\u009e\3\u009e\7\u009e\u06c4\n\u009e\f\u009e"+
		"\16\u009e\u06c7\13\u009e\3\u009e\3\u009e\3\u009f\3\u009f\3\u009f\3\u00a0"+
		"\3\u00a0\7\u00a0\u06d0\n\u00a0\f\u00a0\16\u00a0\u06d3\13\u00a0\3\u00a1"+
		"\3\u00a1\3\u00a1\3\u00a1\3\u00a1\3\u00a1\3\u00a1\3\u00a1\3\u00a1\3\u00a1"+
		"\5\u00a1\u06df\n\u00a1\3\u00a2\3\u00a2\3\u00a3\3\u00a3\3\u00a3\3\u00a3"+
		"\3\u00a3\3\u00a3\3\u00a4\3\u00a4\3\u00a4\3\u00a4\3\u00a4\3\u00a4\3\u00a5"+
		"\3\u00a5\3\u00a5\3\u00a5\3\u00a5\3\u00a5\3\u00a5\3\u00a5\3\u00a6\3\u00a6"+
		"\5\u00a6\u06f9\n\u00a6\3\u00a7\3\u00a7\5\u00a7\u06fd\n\u00a7\3\u00a8\3"+
		"\u00a8\3\u00a8\5\u00a8\u0702\n\u00a8\3\u00a8\3\u00a8\5\u00a8\u0706\n\u00a8"+
		"\3\u00a8\3\u00a8\5\u00a8\u070a\n\u00a8\3\u00a8\3\u00a8\3\u00a8\3\u00a9"+
		"\3\u00a9\3\u00a9\5\u00a9\u0712\n\u00a9\3\u00a9\3\u00a9\5\u00a9\u0716\n"+
		"\u00a9\3\u00a9\3\u00a9\5\u00a9\u071a\n\u00a9\3\u00a9\3\u00a9\3\u00a9\3"+
		"\u00aa\3\u00aa\5\u00aa\u0721\n\u00aa\3\u00ab\3\u00ab\3\u00ac\3\u00ac\3"+
		"\u00ac\7\u00ac\u0728\n\u00ac\f\u00ac\16\u00ac\u072b\13\u00ac\3\u00ad\3"+
		"\u00ad\3\u00ad\7\u00ad\u0730\n\u00ad\f\u00ad\16\u00ad\u0733\13\u00ad\3"+
		"\u00ad\3\u00ad\3\u00ad\3\u00ad\3\u00ad\3\u00ad\3\u00ad\3\u00ae\3\u00ae"+
		"\3\u00ae\7\u00ae\u073f\n\u00ae\f\u00ae\16\u00ae\u0742\13\u00ae\3\u00ae"+
		"\3\u00ae\3\u00ae\3\u00ae\3\u00ae\3\u00ae\3\u00ae\3\u00af\3\u00af\5\u00af"+
		"\u074d\n\u00af\3\u00af\3\u00af\3\u00b0\3\u00b0\5\u00b0\u0753\n\u00b0\3"+
		"\u00b0\3\u00b0\3\u00b1\3\u00b1\5\u00b1\u0759\n\u00b1\3\u00b1\3\u00b1\3"+
		"\u00b2\3\u00b2\3\u00b2\3\u00b2\3\u00b3\3\u00b3\3\u00b3\3\u00b3\3\u00b3"+
		"\3\u00b3\3\u00b4\3\u00b4\3\u00b4\3\u00b4\3\u00b4\3\u00b4\3\u00b4\5\u00b4"+
		"\u076e\n\u00b4\3\u00b4\3\u00b4\3\u00b4\5\u00b4\u0773\n\u00b4\3\u00b5\3"+
		"\u00b5\7\u00b5\u0777\n\u00b5\f\u00b5\16\u00b5\u077a\13\u00b5\3\u00b6\3"+
		"\u00b6\3\u00b6\3\u00b6\3\u00b6\3\u00b6\3\u00b7\7\u00b7\u0783\n\u00b7\f"+
		"\u00b7\16\u00b7\u0786\13\u00b7\3\u00b7\3\u00b7\3\u00b7\3\u00b8\3\u00b8"+
		"\3\u00b8\7\u00b8\u078e\n\u00b8\f\u00b8\16\u00b8\u0791\13\u00b8\3\u00b9"+
		"\3\u00b9\3\u00b9\3\u00ba\3\u00ba\3\u00ba\3\u00ba\5\u00ba\u079a\n\u00ba"+
		"\3\u00ba\5\u00ba\u079d\n\u00ba\3\u00bb\3\u00bb\3\u00bb\5\u00bb\u07a2\n"+
		"\u00bb\3\u00bb\3\u00bb\3\u00bc\3\u00bc\3\u00bc\7\u00bc\u07a9\n\u00bc\f"+
		"\u00bc\16\u00bc\u07ac\13\u00bc\3\u00bd\7\u00bd\u07af\n\u00bd\f\u00bd\16"+
		"\u00bd\u07b2\13\u00bd\3\u00bd\3\u00bd\3\u00bd\3\u00bd\3\u00bd\3\u00bd"+
		"\5\u00bd\u07ba\n\u00bd\3\u00be\3\u00be\5\u00be\u07be\n\u00be\3\u00bf\3"+
		"\u00bf\5\u00bf\u07c2\n\u00bf\3\u00bf\7\u00bf\u07c5\n\u00bf\f\u00bf\16"+
		"\u00bf\u07c8\13\u00bf\3\u00c0\3\u00c0\3\u00c0\7\u00c0\u07cd\n\u00c0\f"+
		"\u00c0\16\u00c0\u07d0\13\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0"+
		"\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0"+
		"\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\3\u00c0\5\u00c0\u07e6\n\u00c0"+
		"\3\u00c1\3\u00c1\3\u00c2\3\u00c2\3\u00c2\7\u00c2\u07ed\n\u00c2\f\u00c2"+
		"\16\u00c2\u07f0\13\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2"+
		"\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2\3\u00c2"+
		"\3\u00c2\3\u00c2\3\u00c2\3\u00c2\5\u00c2\u0805\n\u00c2\3\u00c3\3\u00c3"+
		"\3\u00c3\3\u00c3\3\u00c3\5\u00c3\u080c\n\u00c3\3\u00c4\3\u00c4\3\u00c5"+
		"\3\u00c5\3\u00c5\3\u00c5\5\u00c5\u0814\n\u00c5\3\u00c6\3\u00c6\3\u00c6"+
		"\7\u00c6\u0819\n\u00c6\f\u00c6\16\u00c6\u081c\13\u00c6\3\u00c6\3\u00c6"+
		"\3\u00c6\3\u00c6\3\u00c6\7\u00c6\u0823\n\u00c6\f\u00c6\16\u00c6\u0826"+
		"\13\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6"+
		"\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6\3\u00c6"+
		"\3\u00c6\3\u00c6\3\u00c6\5\u00c6\u083c\n\u00c6\3\u00c7\3\u00c7\3\u00c8"+
		"\3\u00c8\3\u00c8\7\u00c8\u0843\n\u00c8\f\u00c8\16\u00c8\u0846\13\u00c8"+
		"\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8\7\u00c8\u084d\n\u00c8\f\u00c8"+
		"\16\u00c8\u0850\13\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8"+
		"\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8\3\u00c8"+
		"\3\u00c8\3\u00c8\3\u00c8\3\u00c8\5\u00c8\u0865\n\u00c8\3\u00c9\3\u00c9"+
		"\5\u00c9\u0869\n\u00c9\3\u00c9\3\u00c9\3\u00c9\7\u00c9\u086e\n\u00c9\f"+
		"\u00c9\16\u00c9\u0871\13\u00c9\3\u00c9\5\u00c9\u0874\n\u00c9\3\u00c9\3"+
		"\u00c9\5\u00c9\u0878\n\u00c9\3\u00c9\3\u00c9\5\u00c9\u087c\n\u00c9\3\u00c9"+
		"\3\u00c9\3\u00c9\3\u00c9\5\u00c9\u0882\n\u00c9\3\u00c9\3\u00c9\5\u00c9"+
		"\u0886\n\u00c9\3\u00c9\3\u00c9\5\u00c9\u088a\n\u00c9\3\u00c9\3\u00c9\5"+
		"\u00c9\u088e\n\u00c9\3\u00c9\3\u00c9\3\u00c9\3\u00c9\5\u00c9\u0894\n\u00c9"+
		"\3\u00c9\3\u00c9\5\u00c9\u0898\n\u00c9\3\u00c9\3\u00c9\5\u00c9\u089c\n"+
		"\u00c9\3\u00c9\3\u00c9\5\u00c9\u08a0\n\u00c9\5\u00c9\u08a2\n\u00c9\3\u00ca"+
		"\3\u00ca\3\u00ca\5\u00ca\u08a7\n\u00ca\3\u00ca\3\u00ca\5\u00ca\u08ab\n"+
		"\u00ca\3\u00ca\3\u00ca\5\u00ca\u08af\n\u00ca\3\u00ca\3\u00ca\5\u00ca\u08b3"+
		"\n\u00ca\3\u00cb\3\u00cb\5\u00cb\u08b7\n\u00cb\3\u00cb\3\u00cb\3\u00cb"+
		"\7\u00cb\u08bc\n\u00cb\f\u00cb\16\u00cb\u08bf\13\u00cb\3\u00cb\5\u00cb"+
		"\u08c2\n\u00cb\3\u00cb\3\u00cb\5\u00cb\u08c6\n\u00cb\3\u00cb\3\u00cb\5"+
		"\u00cb\u08ca\n\u00cb\3\u00cb\3\u00cb\3\u00cb\3\u00cb\5\u00cb\u08d0\n\u00cb"+
		"\3\u00cb\3\u00cb\5\u00cb\u08d4\n\u00cb\3\u00cb\3\u00cb\5\u00cb\u08d8\n"+
		"\u00cb\3\u00cb\3\u00cb\5\u00cb\u08dc\n\u00cb\5\u00cb\u08de\n\u00cb\3\u00cc"+
		"\3\u00cc\5\u00cc\u08e2\n\u00cc\3\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd"+
		"\3\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\3\u00cd\5\u00cd"+
		"\u08f1\n\u00cd\3\u00ce\3\u00ce\3\u00ce\3\u00cf\3\u00cf\3\u00cf\3\u00cf"+
		"\3\u00cf\3\u00cf\3\u00cf\3\u00cf\3\u00cf\5\u00cf\u08ff\n\u00cf\3\u00d0"+
		"\3\u00d0\3\u00d0\3\u00d0\3\u00d0\3\u00d0\3\u00d0\3\u00d0\3\u00d0\3\u00d0"+
		"\5\u00d0\u090b\n\u00d0\3\u00d0\3\u00d0\3\u00d0\3\u00d0\3\u00d0\7\u00d0"+
		"\u0912\n\u00d0\f\u00d0\16\u00d0\u0915\13\u00d0\3\u00d1\3\u00d1\3\u00d1"+
		"\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\3\u00d1\7\u00d1\u0921"+
		"\n\u00d1\f\u00d1\16\u00d1\u0924\13\u00d1\3\u00d2\3\u00d2\3\u00d2\3\u00d2"+
		"\3\u00d2\3\u00d2\3\u00d2\3\u00d2\3\u00d2\3\u00d2\5\u00d2\u0930\n\u00d2"+
		"\3\u00d2\3\u00d2\3\u00d2\3\u00d2\3\u00d2\7\u00d2\u0937\n\u00d2\f\u00d2"+
		"\16\u00d2\u093a\13\u00d2\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u093f\n\u00d3"+
		"\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u0946\n\u00d3\3\u00d3"+
		"\3\u00d3\3\u00d3\5\u00d3\u094b\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3"+
		"\3\u00d3\5\u00d3\u0952\n\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u0957\n"+
		"\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u095e\n\u00d3\3"+
		"\u00d3\3\u00d3\3\u00d3\5\u00d3\u0963\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3"+
		"\u00d3\3\u00d3\5\u00d3\u096a\n\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u096f"+
		"\n\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3"+
		"\u0978\n\u00d3\3\u00d3\3\u00d3\3\u00d3\5\u00d3\u097d\n\u00d3\3\u00d3\3"+
		"\u00d3\5\u00d3\u0981\n\u00d3\3\u00d4\3\u00d4\5\u00d4\u0985\n\u00d4\3\u00d4"+
		"\3\u00d4\3\u00d4\5\u00d4\u098a\n\u00d4\3\u00d4\3\u00d4\3\u00d5\3\u00d5"+
		"\3\u00d5\5\u00d5\u0991\n\u00d5\3\u00d5\3\u00d5\3\u00d5\3\u00d5\3\u00d5"+
		"\5\u00d5\u0998\n\u00d5\3\u00d5\3\u00d5\3\u00d5\5\u00d5\u099d\n\u00d5\3"+
		"\u00d5\3\u00d5\3\u00d5\3\u00d5\3\u00d5\5\u00d5\u09a4\n\u00d5\3\u00d5\3"+
		"\u00d5\3\u00d5\5\u00d5\u09a9\n\u00d5\3\u00d5\3\u00d5\3\u00d5\3\u00d5\3"+
		"\u00d5\5\u00d5\u09b0\n\u00d5\3\u00d5\3\u00d5\3\u00d5\5\u00d5\u09b5\n\u00d5"+
		"\3\u00d5\3\u00d5\3\u00d5\3\u00d5\3\u00d5\3\u00d5\3\u00d5\5\u00d5\u09be"+
		"\n\u00d5\3\u00d5\3\u00d5\3\u00d5\5\u00d5\u09c3\n\u00d5\3\u00d5\3\u00d5"+
		"\5\u00d5\u09c7\n\u00d5\3\u00d6\3\u00d6\3\u00d6\7\u00d6\u09cc\n\u00d6\f"+
		"\u00d6\16\u00d6\u09cf\13\u00d6\3\u00d7\3\u00d7\3\u00d7\5\u00d7\u09d4\n"+
		"\u00d7\3\u00d7\3\u00d7\3\u00d7\3\u00d7\3\u00d7\5\u00d7\u09db\n\u00d7\3"+
		"\u00d7\3\u00d7\3\u00d7\3\u00d7\3\u00d7\5\u00d7\u09e2\n\u00d7\3\u00d7\3"+
		"\u00d7\3\u00d7\3\u00d7\3\u00d7\5\u00d7\u09e9\n\u00d7\3\u00d7\3\u00d7\3"+
		"\u00d7\3\u00d7\3\u00d7\3\u00d7\5\u00d7\u09f1\n\u00d7\3\u00d7\3\u00d7\3"+
		"\u00d7\3\u00d7\3\u00d7\5\u00d7\u09f8\n\u00d7\3\u00d7\3\u00d7\3\u00d7\3"+
		"\u00d7\3\u00d7\3\u00d7\5\u00d7\u0a00\n\u00d7\3\u00d8\3\u00d8\5\u00d8\u0a04"+
		"\n\u00d8\3\u00d8\3\u00d8\3\u00d9\3\u00d9\3\u00d9\5\u00d9\u0a0b\n\u00d9"+
		"\3\u00d9\3\u00d9\3\u00d9\3\u00d9\3\u00d9\5\u00d9\u0a12\n\u00d9\3\u00d9"+
		"\3\u00d9\3\u00d9\3\u00d9\3\u00d9\5\u00d9\u0a19\n\u00d9\3\u00d9\3\u00d9"+
		"\3\u00d9\3\u00d9\3\u00d9\3\u00d9\5\u00d9\u0a21\n\u00d9\3\u00d9\3\u00d9"+
		"\3\u00d9\3\u00d9\3\u00d9\5\u00d9\u0a28\n\u00d9\3\u00d9\3\u00d9\3\u00d9"+
		"\3\u00d9\3\u00d9\3\u00d9\5\u00d9\u0a30\n\u00d9\3\u00da\3\u00da\3\u00da"+
		"\3\u00da\5\u00da\u0a36\n\u00da\3\u00da\3\u00da\3\u00da\3\u00da\5\u00da"+
		"\u0a3c\n\u00da\3\u00da\3\u00da\3\u00da\3\u00da\3\u00da\3\u00da\3\u00da"+
		"\3\u00da\3\u00da\3\u00da\5\u00da\u0a48\n\u00da\3\u00db\3\u00db\7\u00db"+
		"\u0a4c\n\u00db\f\u00db\16\u00db\u0a4f\13\u00db\3\u00dc\7\u00dc\u0a52\n"+
		"\u00dc\f\u00dc\16\u00dc\u0a55\13\u00dc\3\u00dc\3\u00dc\3\u00dc\3\u00dc"+
		"\3\u00dd\3\u00dd\3\u00de\3\u00de\5\u00de\u0a5f\n\u00de\3\u00df\3\u00df"+
		"\3\u00df\3\u00df\3\u00e0\3\u00e0\3\u00e0\5\u00e0\u0a68\n\u00e0\3\u00e0"+
		"\3\u00e0\3\u00e0\3\u00e0\3\u00e0\5\u00e0\u0a6f\n\u00e0\3\u00e1\3\u00e1"+
		"\3\u00e1\7\u00e1\u0a74\n\u00e1\f\u00e1\16\u00e1\u0a77\13\u00e1\3\u00e2"+
		"\3\u00e2\5\u00e2\u0a7b\n\u00e2\3\u00e3\3\u00e3\5\u00e3\u0a7f\n\u00e3\3"+
		"\u00e4\3\u00e4\3\u00e4\3\u00e4\3\u00e5\3\u00e5\5\u00e5\u0a87\n\u00e5\3"+
		"\u00e6\3\u00e6\3\u00e7\3\u00e7\3\u00e8\3\u00e8\3\u00e9\3\u00e9\3\u00ea"+
		"\3\u00ea\3\u00eb\3\u00eb\3\u00eb\3\u00eb\3\u00eb\3\u00eb\3\u00eb\3\u00eb"+
		"\3\u00eb\3\u00eb\3\u00eb\3\u00eb\3\u00eb\5\u00eb\u0aa0\n\u00eb\3\u00ec"+
		"\3\u00ec\3\u00ec\3\u00ec\3\u00ec\3\u00ec\7\u00ec\u0aa8\n\u00ec\f\u00ec"+
		"\16\u00ec\u0aab\13\u00ec\3\u00ed\3\u00ed\3\u00ed\3\u00ed\3\u00ed\3\u00ed"+
		"\7\u00ed\u0ab3\n\u00ed\f\u00ed\16\u00ed\u0ab6\13\u00ed\3\u00ee\3\u00ee"+
		"\3\u00ee\3\u00ee\3\u00ee\3\u00ee\7\u00ee\u0abe\n\u00ee\f\u00ee\16\u00ee"+
		"\u0ac1\13\u00ee\3\u00ef\3\u00ef\3\u00ef\3\u00ef\3\u00ef\3\u00ef\7\u00ef"+
		"\u0ac9\n\u00ef\f\u00ef\16\u00ef\u0acc\13\u00ef\3\u00f0\3\u00f0\3\u00f0"+
		"\3\u00f0\3\u00f0\3\u00f0\7\u00f0\u0ad4\n\u00f0\f\u00f0\16\u00f0\u0ad7"+
		"\13\u00f0\3\u00f1\3\u00f1\3\u00f1\3\u00f1\3\u00f1\3\u00f1\3\u00f1\3\u00f1"+
		"\3\u00f1\7\u00f1\u0ae2\n\u00f1\f\u00f1\16\u00f1\u0ae5\13\u00f1\3\u00f2"+
		"\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2"+
		"\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2\3\u00f2"+
		"\3\u00f2\3\u00f2\3\u00f2\3\u00f2\7\u00f2\u0afe\n\u00f2\f\u00f2\16\u00f2"+
		"\u0b01\13\u00f2\3\u00f3\3\u00f3\3\u00f3\3\u00f3\3\u00f3\3\u00f3\3\u00f3"+
		"\3\u00f3\3\u00f3\3\u00f3\3\u00f3\3\u00f3\3\u00f3\3\u00f3\3\u00f3\7\u00f3"+
		"\u0b12\n\u00f3\f\u00f3\16\u00f3\u0b15\13\u00f3\3\u00f4\3\u00f4\3\u00f4"+
		"\3\u00f4\3\u00f4\3\u00f4\3\u00f4\5\u00f4\u0b1e\n\u00f4\3\u00f5\3\u00f5"+
		"\3\u00f5\3\u00f5\3\u00f5\3\u00f5\3\u00f5\3\u00f5\3\u00f5\3\u00f5\3\u00f5"+
		"\7\u00f5\u0b2b\n\u00f5\f\u00f5\16\u00f5\u0b2e\13\u00f5\3\u00f6\3\u00f6"+
		"\3\u00f6\3\u00f6\3\u00f6\3\u00f6\3\u00f6\3\u00f6\3\u00f6\3\u00f6\3\u00f6"+
		"\3\u00f6\3\u00f6\3\u00f6\3\u00f6\7\u00f6\u0b3f\n\u00f6\f\u00f6\16\u00f6"+
		"\u0b42\13\u00f6\3\u00f7\3\u00f7\3\u00f7\3\u00f7\3\u00f7\3\u00f7\3\u00f7"+
		"\3\u00f7\3\u00f7\5\u00f7\u0b4d\n\u00f7\3\u00f8\3\u00f8\3\u00f8\3\u00f9"+
		"\3\u00f9\3\u00f9\3\u00fa\3\u00fa\3\u00fa\3\u00fa\3\u00fa\3\u00fa\5\u00fa"+
		"\u0b5b\n\u00fa\3\u00fb\3\u00fb\5\u00fb\u0b5f\n\u00fb\3\u00fb\3\u00fb\7"+
		"\u00fb\u0b63\n\u00fb\f\u00fb\16\u00fb\u0b66\13\u00fb\3\u00fc\3\u00fc\3"+
		"\u00fc\3\u00fd\3\u00fd\3\u00fe\3\u00fe\3\u00fe\3\u00ff\3\u00ff\3\u0100"+
		"\3\u0100\3\u0100\3\u0100\3\u0100\3\u0100\3\u0100\3\u0100\7\u0100\u0b7a"+
		"\n\u0100\f\u0100\16\u0100\u0b7d\13\u0100\3\u0100\3\u0100\3\u0100\3\u0100"+
		"\3\u0100\3\u0100\7\u0100\u0b85\n\u0100\f\u0100\16\u0100\u0b88\13\u0100"+
		"\3\u0100\3\u0100\3\u0100\5\u0100\u0b8d\n\u0100\3\u0100\2\2\208<B\\\u01d6"+
		"\u01d8\u01da\u01dc\u01de\u01e0\u01e2\u01e4\u01e8\u01ea\u0101\2\2\4\2\6"+
		"\2\b\2\n\2\f\2\16\2\20\2\22\2\24\2\26\2\30\2\32\2\34\2\36\2 \2\"\2$\2"+
		"&\2(\2*\2,\2.\2\60\2\62\2\64\2\66\28\2:\2<\2>\2@\2B\2D\2F\2H\2J\2L\2N"+
		"\2P\2R\2T\2V\2X\2Z\2\\\2^\2`\2b\2d\2f\2h\2j\2l\2n\2p\2r\2t\2v\2x\2z\2"+
		"|\2~\2\u0080\2\u0082\2\u0084\2\u0086\2\u0088\2\u008a\2\u008c\2\u008e\2"+
		"\u0090\2\u0092\2\u0094\2\u0096\2\u0098\2\u009a\2\u009c\2\u009e\2\u00a0"+
		"\2\u00a2\2\u00a4\2\u00a6\2\u00a8\2\u00aa\2\u00ac\2\u00ae\2\u00b0\2\u00b2"+
		"\2\u00b4\2\u00b6\2\u00b8\2\u00ba\2\u00bc\2\u00be\2\u00c0\2\u00c2\2\u00c4"+
		"\2\u00c6\2\u00c8\2\u00ca\2\u00cc\2\u00ce\2\u00d0\2\u00d2\2\u00d4\2\u00d6"+
		"\2\u00d8\2\u00da\2\u00dc\2\u00de\2\u00e0\2\u00e2\2\u00e4\2\u00e6\2\u00e8"+
		"\2\u00ea\2\u00ec\2\u00ee\2\u00f0\2\u00f2\2\u00f4\2\u00f6\2\u00f8\2\u00fa"+
		"\2\u00fc\2\u00fe\2\u0100\2\u0102\2\u0104\2\u0106\2\u0108\2\u010a\2\u010c"+
		"\2\u010e\2\u0110\2\u0112\2\u0114\2\u0116\2\u0118\2\u011a\2\u011c\2\u011e"+
		"\2\u0120\2\u0122\2\u0124\2\u0126\2\u0128\2\u012a\2\u012c\2\u012e\2\u0130"+
		"\2\u0132\2\u0134\2\u0136\2\u0138\2\u013a\2\u013c\2\u013e\2\u0140\2\u0142"+
		"\2\u0144\2\u0146\2\u0148\2\u014a\2\u014c\2\u014e\2\u0150\2\u0152\2\u0154"+
		"\2\u0156\2\u0158\2\u015a\2\u015c\2\u015e\2\u0160\2\u0162\2\u0164\2\u0166"+
		"\2\u0168\2\u016a\2\u016c\2\u016e\2\u0170\2\u0172\2\u0174\2\u0176\2\u0178"+
		"\2\u017a\2\u017c\2\u017e\2\u0180\2\u0182\2\u0184\2\u0186\2\u0188\2\u018a"+
		"\2\u018c\2\u018e\2\u0190\2\u0192\2\u0194\2\u0196\2\u0198\2\u019a\2\u019c"+
		"\2\u019e\2\u01a0\2\u01a2\2\u01a4\2\u01a6\2\u01a8\2\u01aa\2\u01ac\2\u01ae"+
		"\2\u01b0\2\u01b2\2\u01b4\2\u01b6\2\u01b8\2\u01ba\2\u01bc\2\u01be\2\u01c0"+
		"\2\u01c2\2\u01c4\2\u01c6\2\u01c8\2\u01ca\2\u01cc\2\u01ce\2\u01d0\2\u01d2"+
		"\2\u01d4\2\u01d6\2\u01d8\2\u01da\2\u01dc\2\u01de\2\u01e0\2\u01e2\2\u01e4"+
		"\2\u01e6\2\u01e8\2\u01ea\2\u01ec\2\u01ee\2\u01f0\2\u01f2\2\u01f4\2\u01f6"+
		"\2\u01f8\2\u01fa\2\u01fc\2\u01fe\2\2\13\4\2\3\ftt\3\2AF\7\2\23\23\26\26"+
		"))++\63\63\4\2\34\34\"\"\4\2\6\6\64\64\4\2PPis\3\2_`\5\2((QRXY\4\2abf"+
		"f\u0c75\2\u0200\3\2\2\2\4\u0202\3\2\2\2\6\u0206\3\2\2\2\b\u0216\3\2\2"+
		"\2\n\u021a\3\2\2\2\f\u021c\3\2\2\2\16\u021e\3\2\2\2\20\u0223\3\2\2\2\22"+
		"\u0227\3\2\2\2\24\u023a\3\2\2\2\26\u023c\3\2\2\2\30\u0241\3\2\2\2\32\u0245"+
		"\3\2\2\2\34\u0247\3\2\2\2\36\u0249\3\2\2\2 \u024b\3\2\2\2\"\u0256\3\2"+
		"\2\2$\u0258\3\2\2\2&\u0262\3\2\2\2(\u0269\3\2\2\2*\u0275\3\2\2\2,\u0277"+
		"\3\2\2\2.\u027a\3\2\2\2\60\u027e\3\2\2\2\62\u0288\3\2\2\2\64\u028d\3\2"+
		"\2\2\66\u0298\3\2\2\28\u029a\3\2\2\2:\u02aa\3\2\2\2<\u02ac\3\2\2\2>\u02bc"+
		"\3\2\2\2@\u02be\3\2\2\2B\u02c0\3\2\2\2D\u02e7\3\2\2\2F\u02ec\3\2\2\2H"+
		"\u02fa\3\2\2\2J\u0300\3\2\2\2L\u0302\3\2\2\2N\u0306\3\2\2\2P\u030c\3\2"+
		"\2\2R\u0313\3\2\2\2T\u031d\3\2\2\2V\u0322\3\2\2\2X\u0373\3\2\2\2Z\u0375"+
		"\3\2\2\2\\\u0377\3\2\2\2^\u0384\3\2\2\2`\u0386\3\2\2\2b\u0397\3\2\2\2"+
		"d\u03a2\3\2\2\2f\u03a4\3\2\2\2h\u03a8\3\2\2\2j\u03b0\3\2\2\2l\u03b3\3"+
		"\2\2\2n\u03b6\3\2\2\2p\u03be\3\2\2\2r\u03cb\3\2\2\2t\u03d2\3\2\2\2v\u03d4"+
		"\3\2\2\2x\u03dc\3\2\2\2z\u03e7\3\2\2\2|\u03e9\3\2\2\2~\u03f1\3\2\2\2\u0080"+
		"\u03f6\3\2\2\2\u0082\u03fc\3\2\2\2\u0084\u0400\3\2\2\2\u0086\u0404\3\2"+
		"\2\2\u0088\u0409\3\2\2\2\u008a\u040d\3\2\2\2\u008c\u0420\3\2\2\2\u008e"+
		"\u0422\3\2\2\2\u0090\u0427\3\2\2\2\u0092\u042b\3\2\2\2\u0094\u042d\3\2"+
		"\2\2\u0096\u042f\3\2\2\2\u0098\u0431\3\2\2\2\u009a\u043c\3\2\2\2\u009c"+
		"\u043e\3\2\2\2\u009e\u0445\3\2\2\2\u00a0\u0452\3\2\2\2\u00a2\u0465\3\2"+
		"\2\2\u00a4\u0469\3\2\2\2\u00a6\u046b\3\2\2\2\u00a8\u0479\3\2\2\2\u00aa"+
		"\u048b\3\2\2\2\u00ac\u0490\3\2\2\2\u00ae\u0498\3\2\2\2\u00b0\u04ab\3\2"+
		"\2\2\u00b2\u04b0\3\2\2\2\u00b4\u04bb\3\2\2\2\u00b6\u04be\3\2\2\2\u00b8"+
		"\u04c8\3\2\2\2\u00ba\u04cc\3\2\2\2\u00bc\u04ce\3\2\2\2\u00be\u04d0\3\2"+
		"\2\2\u00c0\u04d3\3\2\2\2\u00c2\u04dd\3\2\2\2\u00c4\u04e4\3\2\2\2\u00c6"+
		"\u04e7\3\2\2\2\u00c8\u04f0\3\2\2\2\u00ca\u04f2\3\2\2\2\u00cc\u0529\3\2"+
		"\2\2\u00ce\u052b\3\2\2\2\u00d0\u0533\3\2\2\2\u00d2\u053f\3\2\2\2\u00d4"+
		"\u054a\3\2\2\2\u00d6\u0558\3\2\2\2\u00d8\u055a\3\2\2\2\u00da\u0563\3\2"+
		"\2\2\u00dc\u0565\3\2\2\2\u00de\u0573\3\2\2\2\u00e0\u057d\3\2\2\2\u00e2"+
		"\u057f\3\2\2\2\u00e4\u0582\3\2\2\2\u00e6\u0590\3\2\2\2\u00e8\u0595\3\2"+
		"\2\2\u00ea\u05a0\3\2\2\2\u00ec\u05a5\3\2\2\2\u00ee\u05b2\3\2\2\2\u00f0"+
		"\u05b7\3\2\2\2\u00f2\u05bf\3\2\2\2\u00f4\u05cd\3\2\2\2\u00f6\u05d2\3\2"+
		"\2\2\u00f8\u05e4\3\2\2\2\u00fa\u05e6\3\2\2\2\u00fc\u05ec\3\2\2\2\u00fe"+
		"\u05f1\3\2\2\2\u0100\u05f9\3\2\2\2\u0102\u05fe\3\2\2\2\u0104\u0606\3\2"+
		"\2\2\u0106\u060e\3\2\2\2\u0108\u0615\3\2\2\2\u010a\u0617\3\2\2\2\u010c"+
		"\u0620\3\2\2\2\u010e\u0628\3\2\2\2\u0110\u062b\3\2\2\2\u0112\u0631\3\2"+
		"\2\2\u0114\u063a\3\2\2\2\u0116\u0642\3\2\2\2\u0118\u0648\3\2\2\2\u011a"+
		"\u0652\3\2\2\2\u011c\u0654\3\2\2\2\u011e\u065a\3\2\2\2\u0120\u0666\3\2"+
		"\2\2\u0122\u066d\3\2\2\2\u0124\u067b\3\2\2\2\u0126\u067d\3\2\2\2\u0128"+
		"\u067f\3\2\2\2\u012a\u0683\3\2\2\2\u012c\u0687\3\2\2\2\u012e\u0691\3\2"+
		"\2\2\u0130\u0693\3\2\2\2\u0132\u0699\3\2\2\2\u0134\u06a1\3\2\2\2\u0136"+
		"\u06b3\3\2\2\2\u0138\u06b5\3\2\2\2\u013a\u06bb\3\2\2\2\u013c\u06ca\3\2"+
		"\2\2\u013e\u06cd\3\2\2\2\u0140\u06de\3\2\2\2\u0142\u06e0\3\2\2\2\u0144"+
		"\u06e2\3\2\2\2\u0146\u06e8\3\2\2\2\u0148\u06ee\3\2\2\2\u014a\u06f8\3\2"+
		"\2\2\u014c\u06fc\3\2\2\2\u014e\u06fe\3\2\2\2\u0150\u070e\3\2\2\2\u0152"+
		"\u0720\3\2\2\2\u0154\u0722\3\2\2\2\u0156\u0724\3\2\2\2\u0158\u072c\3\2"+
		"\2\2\u015a\u073b\3\2\2\2\u015c\u074a\3\2\2\2\u015e\u0750\3\2\2\2\u0160"+
		"\u0756\3\2\2\2\u0162\u075c\3\2\2\2\u0164\u0760\3\2\2\2\u0166\u0772\3\2"+
		"\2\2\u0168\u0774\3\2\2\2\u016a\u077b\3\2\2\2\u016c\u0784\3\2\2\2\u016e"+
		"\u078a\3\2\2\2\u0170\u0792\3\2\2\2\u0172\u0795\3\2\2\2\u0174\u079e\3\2"+
		"\2\2\u0176\u07a5\3\2\2\2\u0178\u07b9\3\2\2\2\u017a\u07bd\3\2\2\2\u017c"+
		"\u07c1\3\2\2\2\u017e\u07e5\3\2\2\2\u0180\u07e7\3\2\2\2\u0182\u0804\3\2"+
		"\2\2\u0184\u080b\3\2\2\2\u0186\u080d\3\2\2\2\u0188\u0813\3\2\2\2\u018a"+
		"\u083b\3\2\2\2\u018c\u083d\3\2\2\2\u018e\u0864\3\2\2\2\u0190\u08a1\3\2"+
		"\2\2\u0192\u08a3\3\2\2\2\u0194\u08dd\3\2\2\2\u0196\u08e1\3\2\2\2\u0198"+
		"\u08f0\3\2\2\2\u019a\u08f2\3\2\2\2\u019c\u08fe\3\2\2\2\u019e\u090a\3\2"+
		"\2\2\u01a0\u0916\3\2\2\2\u01a2\u092f\3\2\2\2\u01a4\u0980\3\2\2\2\u01a6"+
		"\u0982\3\2\2\2\u01a8\u09c6\3\2\2\2\u01aa\u09c8\3\2\2\2\u01ac\u09ff\3\2"+
		"\2\2\u01ae\u0a01\3\2\2\2\u01b0\u0a2f\3\2\2\2\u01b2\u0a47\3\2\2\2\u01b4"+
		"\u0a49\3\2\2\2\u01b6\u0a53\3\2\2\2\u01b8\u0a5a\3\2\2\2\u01ba\u0a5e\3\2"+
		"\2\2\u01bc\u0a60\3\2\2\2\u01be\u0a6e\3\2\2\2\u01c0\u0a70\3\2\2\2\u01c2"+
		"\u0a7a\3\2\2\2\u01c4\u0a7e\3\2\2\2\u01c6\u0a80\3\2\2\2\u01c8\u0a86\3\2"+
		"\2\2\u01ca\u0a88\3\2\2\2\u01cc\u0a8a\3\2\2\2\u01ce\u0a8c\3\2\2\2\u01d0"+
		"\u0a8e\3\2\2\2\u01d2\u0a90\3\2\2\2\u01d4\u0a9f\3\2\2\2\u01d6\u0aa1\3\2"+
		"\2\2\u01d8\u0aac\3\2\2\2\u01da\u0ab7\3\2\2\2\u01dc\u0ac2\3\2\2\2\u01de"+
		"\u0acd\3\2\2\2\u01e0\u0ad8\3\2\2\2\u01e2\u0ae6\3\2\2\2\u01e4\u0b02\3\2"+
		"\2\2\u01e6\u0b1d\3\2\2\2\u01e8\u0b1f\3\2\2\2\u01ea\u0b2f\3\2\2\2\u01ec"+
		"\u0b4c\3\2\2\2\u01ee\u0b4e\3\2\2\2\u01f0\u0b51\3\2\2\2\u01f2\u0b5a\3\2"+
		"\2\2\u01f4\u0b5e\3\2\2\2\u01f6\u0b67\3\2\2\2\u01f8\u0b6a\3\2\2\2\u01fa"+
		"\u0b6c\3\2\2\2\u01fc\u0b6f\3\2\2\2\u01fe\u0b8c\3\2\2\2\u0200\u0201\t\2"+
		"\2\2\u0201\3\3\2\2\2\u0202\u0203\t\3\2\2\u0203\5\3\2\2\2\u0204\u0207\5"+
		"\b\5\2\u0205\u0207\5\20\t\2\u0206\u0204\3\2\2\2\u0206\u0205\3\2\2\2\u0207"+
		"\7\3\2\2\2\u0208\u020a\5\u00fc\177\2\u0209\u0208\3\2\2\2\u020a\u020d\3"+
		"\2\2\2\u020b\u0209\3\2\2\2\u020b\u020c\3\2\2\2\u020c\u020e\3\2\2\2\u020d"+
		"\u020b\3\2\2\2\u020e\u0217\5\n\6\2\u020f\u0211\5\u00fc\177\2\u0210\u020f"+
		"\3\2\2\2\u0211\u0214\3\2\2\2\u0212\u0210\3\2\2\2\u0212\u0213\3\2\2\2\u0213"+
		"\u0215\3\2\2\2\u0214\u0212\3\2\2\2\u0215\u0217\7\21\2\2\u0216\u020b\3"+
		"\2\2\2\u0216\u0212\3\2\2\2\u0217\t\3\2\2\2\u0218\u021b\5\f\7\2\u0219\u021b"+
		"\5\16\b\2\u021a\u0218\3\2\2\2\u021a\u0219\3\2\2\2\u021b\13\3\2\2\2\u021c"+
		"\u021d\t\4\2\2\u021d\r\3\2\2\2\u021e\u021f\t\5\2\2\u021f\17\3\2\2\2\u0220"+
		"\u0224\5\22\n\2\u0221\u0224\5 \21\2\u0222\u0224\5\"\22\2\u0223\u0220\3"+
		"\2\2\2\u0223\u0221\3\2\2\2\u0223\u0222\3\2\2\2\u0224\21\3\2\2\2\u0225"+
		"\u0228\5\30\r\2\u0226\u0228\5\36\20\2\u0227\u0225\3\2\2\2\u0227\u0226"+
		"\3\2\2\2\u0228\u022d\3\2\2\2\u0229\u022c\5\26\f\2\u022a\u022c\5\34\17"+
		"\2\u022b\u0229\3\2\2\2\u022b\u022a\3\2\2\2\u022c\u022f\3\2\2\2\u022d\u022b"+
		"\3\2\2\2\u022d\u022e\3\2\2\2\u022e\23\3\2\2\2\u022f\u022d\3\2\2\2\u0230"+
		"\u0232\5\u00fe\u0080\2\u0231\u0233\5.\30\2\u0232\u0231\3\2\2\2\u0232\u0233"+
		"\3\2\2\2\u0233\u023b\3\2\2\2\u0234\u0235\5\22\n\2\u0235\u0236\7O\2\2\u0236"+
		"\u0238\5\u00fe\u0080\2\u0237\u0239\5.\30\2\u0238\u0237\3\2\2\2\u0238\u0239"+
		"\3\2\2\2\u0239\u023b\3\2\2\2\u023a\u0230\3\2\2\2\u023a\u0234\3\2\2\2\u023b"+
		"\25\3\2\2\2\u023c\u023d\7O\2\2\u023d\u023f\5\u00fe\u0080\2\u023e\u0240"+
		"\5.\30\2\u023f\u023e\3\2\2\2\u023f\u0240\3\2\2\2\u0240\27\3\2\2\2\u0241"+
		"\u0243\5\u00fe\u0080\2\u0242\u0244\5.\30\2\u0243\u0242\3\2\2\2\u0243\u0244"+
		"\3\2\2\2\u0244\31\3\2\2\2\u0245\u0246\5\24\13\2\u0246\33\3\2\2\2\u0247"+
		"\u0248\5\26\f\2\u0248\35\3\2\2\2\u0249\u024a\5\30\r\2\u024a\37\3\2\2\2"+
		"\u024b\u024c\5\u00fe\u0080\2\u024c!\3\2\2\2\u024d\u024e\5\b\5\2\u024e"+
		"\u024f\5$\23\2\u024f\u0257\3\2\2\2\u0250\u0251\5\22\n\2\u0251\u0252\5"+
		"$\23\2\u0252\u0257\3\2\2\2\u0253\u0254\5 \21\2\u0254\u0255\5$\23\2\u0255"+
		"\u0257\3\2\2\2\u0256\u024d\3\2\2\2\u0256\u0250\3\2\2\2\u0256\u0253\3\2"+
		"\2\2\u0257#\3\2\2\2\u0258\u025c\5\u0100\u0081\2\u0259\u025b\5\u0100\u0081"+
		"\2\u025a\u0259\3\2\2\2\u025b\u025e\3\2\2\2\u025c\u025a\3\2\2\2\u025c\u025d"+
		"\3\2\2\2\u025d%\3\2\2\2\u025e\u025c\3\2\2\2\u025f\u0261\5(\25\2\u0260"+
		"\u025f\3\2\2\2\u0261\u0264\3\2\2\2\u0262\u0260\3\2\2\2\u0262\u0263\3\2"+
		"\2\2\u0263\u0265\3\2\2\2\u0264\u0262\3\2\2\2\u0265\u0267\5\2\2\2\u0266"+
		"\u0268\5*\26\2\u0267\u0266\3\2\2\2\u0267\u0268\3\2\2\2\u0268\'\3\2\2\2"+
		"\u0269\u026a\5\u00fc\177\2\u026a)\3\2\2\2\u026b\u026c\7\37\2\2\u026c\u0276"+
		"\5 \21\2\u026d\u026e\7\37\2\2\u026e\u0272\5\22\n\2\u026f\u0271\5,\27\2"+
		"\u0270\u026f\3\2\2\2\u0271\u0274\3\2\2\2\u0272\u0270\3\2\2\2\u0272\u0273"+
		"\3\2\2\2\u0273\u0276\3\2\2\2\u0274\u0272\3\2\2\2\u0275\u026b\3\2\2\2\u0275"+
		"\u026d\3\2\2\2\u0276+\3\2\2\2\u0277\u0278\7c\2\2\u0278\u0279\5\32\16\2"+
		"\u0279-\3\2\2\2\u027a\u027b\7R\2\2\u027b\u027c\5\60\31\2\u027c\u027d\7"+
		"Q\2\2\u027d/\3\2\2\2\u027e\u0283\5\62\32\2\u027f\u0280\7N\2\2\u0280\u0282"+
		"\5\62\32\2\u0281\u027f\3\2\2\2\u0282\u0285\3\2\2\2\u0283\u0281\3\2\2\2"+
		"\u0283\u0284\3\2\2\2\u0284\61\3\2\2\2\u0285\u0283\3\2\2\2\u0286\u0289"+
		"\5\20\t\2\u0287\u0289\5\64\33\2\u0288\u0286\3\2\2\2\u0288\u0287\3\2\2"+
		"\2\u0289\63\3\2\2\2\u028a\u028c\5\u00fc\177\2\u028b\u028a\3\2\2\2\u028c"+
		"\u028f\3\2\2\2\u028d\u028b\3\2\2\2\u028d\u028e\3\2\2\2\u028e\u0290\3\2"+
		"\2\2\u028f\u028d\3\2\2\2\u0290\u0292\7U\2\2\u0291\u0293\5\66\34\2\u0292"+
		"\u0291\3\2\2\2\u0292\u0293\3\2\2\2\u0293\65\3\2\2\2\u0294\u0295\7\37\2"+
		"\2\u0295\u0299\5\20\t\2\u0296\u0297\7\66\2\2\u0297\u0299\5\20\t\2\u0298"+
		"\u0294\3\2\2\2\u0298\u0296\3\2\2\2\u0299\67\3\2\2\2\u029a\u029b\b\35\1"+
		"\2\u029b\u029c\5\2\2\2\u029c\u02a2\3\2\2\2\u029d\u029e\f\3\2\2\u029e\u029f"+
		"\7O\2\2\u029f\u02a1\5\2\2\2\u02a0\u029d\3\2\2\2\u02a1\u02a4\3\2\2\2\u02a2"+
		"\u02a0\3\2\2\2\u02a2\u02a3\3\2\2\2\u02a39\3\2\2\2\u02a4\u02a2\3\2\2\2"+
		"\u02a5\u02ab\5\2\2\2\u02a6\u02a7\5<\37\2\u02a7\u02a8\7O\2\2\u02a8\u02a9"+
		"\5\2\2\2\u02a9\u02ab\3\2\2\2\u02aa\u02a5\3\2\2\2\u02aa\u02a6\3\2\2\2\u02ab"+
		";\3\2\2\2\u02ac\u02ad\b\37\1\2\u02ad\u02ae\5\2\2\2\u02ae\u02b4\3\2\2\2"+
		"\u02af\u02b0\f\3\2\2\u02b0\u02b1\7O\2\2\u02b1\u02b3\5\2\2\2\u02b2\u02af"+
		"\3\2\2\2\u02b3\u02b6\3\2\2\2\u02b4\u02b2\3\2\2\2\u02b4\u02b5\3\2\2\2\u02b5"+
		"=\3\2\2\2\u02b6\u02b4\3\2\2\2\u02b7\u02bd\5\2\2\2\u02b8\u02b9\5B\"\2\u02b9"+
		"\u02ba\7O\2\2\u02ba\u02bb\5\2\2\2\u02bb\u02bd\3\2\2\2\u02bc\u02b7\3\2"+
		"\2\2\u02bc\u02b8\3\2\2\2\u02bd?\3\2\2\2\u02be\u02bf\5\2\2\2\u02bfA\3\2"+
		"\2\2\u02c0\u02c1\b\"\1\2\u02c1\u02c2\5\2\2\2\u02c2\u02c8\3\2\2\2\u02c3"+
		"\u02c4\f\3\2\2\u02c4\u02c5\7O\2\2\u02c5\u02c7\5\2\2\2\u02c6\u02c3\3\2"+
		"\2\2\u02c7\u02ca\3\2\2\2\u02c8\u02c6\3\2\2\2\u02c8\u02c9\3\2\2\2\u02c9"+
		"C\3\2\2\2\u02ca\u02c8\3\2\2\2\u02cb\u02cc\5F$\2\u02cc\u02cd\7\2\2\3\u02cd"+
		"\u02e8\3\2\2\2\u02ce\u02d0\5F$\2\u02cf\u02ce\3\2\2\2\u02cf\u02d0\3\2\2"+
		"\2\u02d0\u02d4\3\2\2\2\u02d1\u02d3\5J&\2\u02d2\u02d1\3\2\2\2\u02d3\u02d6"+
		"\3\2\2\2\u02d4\u02d2\3\2\2\2\u02d4\u02d5\3\2\2\2\u02d5\u02da\3\2\2\2\u02d6"+
		"\u02d4\3\2\2\2\u02d7\u02d9\5T+\2\u02d8\u02d7\3\2\2\2\u02d9\u02dc\3\2\2"+
		"\2\u02da\u02d8\3\2\2\2\u02da\u02db\3\2\2\2\u02db\u02dd\3\2\2\2\u02dc\u02da"+
		"\3\2\2\2\u02dd\u02e8\7\2\2\3\u02de\u02e0\5J&\2\u02df\u02de\3\2\2\2\u02e0"+
		"\u02e3\3\2\2\2\u02e1\u02df\3\2\2\2\u02e1\u02e2\3\2\2\2\u02e2\u02e4\3\2"+
		"\2\2\u02e3\u02e1\3\2\2\2\u02e4\u02e5\5V,\2\u02e5\u02e6\7\2\2\3\u02e6\u02e8"+
		"\3\2\2\2\u02e7\u02cb\3\2\2\2\u02e7\u02cf\3\2\2\2\u02e7\u02e1\3\2\2\2\u02e8"+
		"E\3\2\2\2\u02e9\u02eb\5H%\2\u02ea\u02e9\3\2\2\2\u02eb\u02ee\3\2\2\2\u02ec"+
		"\u02ea\3\2\2\2\u02ec\u02ed\3\2\2\2\u02ed\u02ef\3\2\2\2\u02ee\u02ec\3\2"+
		"\2\2\u02ef\u02f0\7.\2\2\u02f0\u02f5\5\2\2\2\u02f1\u02f2\7O\2\2\u02f2\u02f4"+
		"\5\2\2\2\u02f3\u02f1\3\2\2\2\u02f4\u02f7\3\2\2\2\u02f5\u02f3\3\2\2\2\u02f5"+
		"\u02f6\3\2\2\2\u02f6\u02f8\3\2\2\2\u02f7\u02f5\3\2\2\2\u02f8\u02f9\7M"+
		"\2\2\u02f9G\3\2\2\2\u02fa\u02fb\5\u00fc\177\2\u02fbI\3\2\2\2\u02fc\u0301"+
		"\5L\'\2\u02fd\u0301\5N(\2\u02fe\u0301\5P)\2\u02ff\u0301\5R*\2\u0300\u02fc"+
		"\3\2\2\2\u0300\u02fd\3\2\2\2\u0300\u02fe\3\2\2\2\u0300\u02ff\3\2\2\2\u0301"+
		"K\3\2\2\2\u0302\u0303\7\'\2\2\u0303\u0304\5:\36\2\u0304\u0305\7M\2\2\u0305"+
		"M\3\2\2\2\u0306\u0307\7\'\2\2\u0307\u0308\5<\37\2\u0308\u0309\7O\2\2\u0309"+
		"\u030a\7a\2\2\u030a\u030b\7M\2\2\u030bO\3\2\2\2\u030c\u030d\7\'\2\2\u030d"+
		"\u030e\7\64\2\2\u030e\u030f\5:\36\2\u030f\u0310\7O\2\2\u0310\u0311\5\2"+
		"\2\2\u0311\u0312\7M\2\2\u0312Q\3\2\2\2\u0313\u0314\7\'\2\2\u0314\u0315"+
		"\7\64\2\2\u0315\u0316\5:\36\2\u0316\u0317\7O\2\2\u0317\u0318\7a\2\2\u0318"+
		"\u0319\7M\2\2\u0319S\3\2\2\2\u031a\u031e\5^\60\2\u031b\u031e\5\u00dan"+
		"\2\u031c\u031e\7M\2\2\u031d\u031a\3\2\2\2\u031d\u031b\3\2\2\2\u031d\u031c"+
		"\3\2\2\2\u031eU\3\2\2\2\u031f\u0321\5\u00fc\177\2\u0320\u031f\3\2\2\2"+
		"\u0321\u0324\3\2\2\2\u0322\u0320\3\2\2\2\u0322\u0323\3\2\2\2\u0323\u0326"+
		"\3\2\2\2\u0324\u0322\3\2\2\2\u0325\u0327\7\3\2\2\u0326\u0325\3\2\2\2\u0326"+
		"\u0327\3\2\2\2\u0327\u0328\3\2\2\2\u0328\u0329\7\4\2\2\u0329\u032e\7t"+
		"\2\2\u032a\u032b\7O\2\2\u032b\u032d\7t\2\2\u032c\u032a\3\2\2\2\u032d\u0330"+
		"\3\2\2\2\u032e\u032c\3\2\2\2\u032e\u032f\3\2\2\2\u032f\u0331\3\2\2\2\u0330"+
		"\u032e\3\2\2\2\u0331\u0335\7I\2\2\u0332\u0334\5X-\2\u0333\u0332\3\2\2"+
		"\2\u0334\u0337\3\2\2\2\u0335\u0333\3\2\2\2\u0335\u0336\3\2\2\2\u0336\u0338"+
		"\3\2\2\2\u0337\u0335\3\2\2\2\u0338\u0339\7J\2\2\u0339W\3\2\2\2\u033a\u033e"+
		"\7\5\2\2\u033b\u033d\5Z.\2\u033c\u033b\3\2\2\2\u033d\u0340\3\2\2\2\u033e"+
		"\u033c\3\2\2\2\u033e\u033f\3\2\2\2\u033f\u0341\3\2\2\2\u0340\u033e\3\2"+
		"\2\2\u0341\u0342\5\\/\2\u0342\u0343\7M\2\2\u0343\u0374\3\2\2\2\u0344\u0345"+
		"\7\7\2\2\u0345\u034f\58\35\2\u0346\u0347\7\t\2\2\u0347\u034c\5\\/\2\u0348"+
		"\u0349\7N\2\2\u0349\u034b\5\\/\2\u034a\u0348\3\2\2\2\u034b\u034e\3\2\2"+
		"\2\u034c\u034a\3\2\2\2\u034c\u034d\3\2\2\2\u034d\u0350\3\2\2\2\u034e\u034c"+
		"\3\2\2\2\u034f\u0346\3\2\2\2\u034f\u0350\3\2\2\2\u0350\u0351\3\2\2\2\u0351"+
		"\u0352\7M\2\2\u0352\u0374\3\2\2\2\u0353\u0354\7\b\2\2\u0354\u035e\58\35"+
		"\2\u0355\u0356\7\t\2\2\u0356\u035b\5\\/\2\u0357\u0358\7N\2\2\u0358\u035a"+
		"\5\\/\2\u0359\u0357\3\2\2\2\u035a\u035d\3\2\2\2\u035b\u0359\3\2\2\2\u035b"+
		"\u035c\3\2\2\2\u035c\u035f\3\2\2\2\u035d\u035b\3\2\2\2\u035e\u0355\3\2"+
		"\2\2\u035e\u035f\3\2\2\2\u035f\u0360\3\2\2\2\u0360\u0361\7M\2\2\u0361"+
		"\u0374\3\2\2\2\u0362\u0363\7\n\2\2\u0363\u0364\5:\36\2\u0364\u0365\7M"+
		"\2\2\u0365\u0374\3\2\2\2\u0366\u0367\7\13\2\2\u0367\u0368\5:\36\2\u0368"+
		"\u0369\7\f\2\2\u0369\u036e\5:\36\2\u036a\u036b\7N\2\2\u036b\u036d\5:\36"+
		"\2\u036c\u036a\3\2\2\2\u036d\u0370\3\2\2\2\u036e\u036c\3\2\2\2\u036e\u036f"+
		"\3\2\2\2\u036f\u0371\3\2\2\2\u0370\u036e\3\2\2\2\u0371\u0372\7M\2\2\u0372"+
		"\u0374\3\2\2\2\u0373\u033a\3\2\2\2\u0373\u0344\3\2\2\2\u0373\u0353\3\2"+
		"\2\2\u0373\u0362\3\2\2\2\u0373\u0366\3\2\2\2\u0374Y\3\2\2\2\u0375\u0376"+
		"\t\6\2\2\u0376[\3\2\2\2\u0377\u0378\b/\1\2\u0378\u0379\7t\2\2\u0379\u037f"+
		"\3\2\2\2\u037a\u037b\f\3\2\2\u037b\u037c\7O\2\2\u037c\u037e\7t\2\2\u037d"+
		"\u037a\3\2\2\2\u037e\u0381\3\2\2\2\u037f\u037d\3\2\2\2\u037f\u0380\3\2"+
		"\2\2\u0380]\3\2\2\2\u0381\u037f\3\2\2\2\u0382\u0385\5`\61\2\u0383\u0385"+
		"\5\u00ceh\2\u0384\u0382\3\2\2\2\u0384\u0383\3\2\2\2\u0385_\3\2\2\2\u0386"+
		"\u0387\5b\62\2\u0387\u0388\7\27\2\2\u0388\u038a\5\2\2\2\u0389\u038b\5"+
		"f\64\2\u038a\u0389\3\2\2\2\u038a\u038b\3\2\2\2\u038b\u038d\3\2\2\2\u038c"+
		"\u038e\5j\66\2\u038d\u038c\3\2\2\2\u038d\u038e\3\2\2\2\u038e\u0390\3\2"+
		"\2\2\u038f\u0391\5l\67\2\u0390\u038f\3\2\2\2\u0390\u0391\3\2\2\2\u0391"+
		"\u0392\3\2\2\2\u0392\u0393\5p9\2\u0393a\3\2\2\2\u0394\u0396\5d\63\2\u0395"+
		"\u0394\3\2\2\2\u0396\u0399\3\2\2\2\u0397\u0395\3\2\2\2\u0397\u0398\3\2"+
		"\2\2\u0398c\3\2\2\2\u0399\u0397\3\2\2\2\u039a\u03a3\5\u00fc\177\2\u039b"+
		"\u03a3\7\61\2\2\u039c\u03a3\7\60\2\2\u039d\u03a3\7/\2\2\u039e\u03a3\7"+
		"\17\2\2\u039f\u03a3\7\64\2\2\u03a0\u03a3\7 \2\2\u03a1\u03a3\7\65\2\2\u03a2"+
		"\u039a\3\2\2\2\u03a2\u039b\3\2\2\2\u03a2\u039c\3\2\2\2\u03a2\u039d\3\2"+
		"\2\2\u03a2\u039e\3\2\2\2\u03a2\u039f\3\2\2\2\u03a2\u03a0\3\2\2\2\u03a2"+
		"\u03a1\3\2\2\2\u03a3e\3\2\2\2\u03a4\u03a5\7R\2\2\u03a5\u03a6\5h\65\2\u03a6"+
		"\u03a7\7Q\2\2\u03a7g\3\2\2\2\u03a8\u03ad\5&\24\2\u03a9\u03aa\7N\2\2\u03aa"+
		"\u03ac\5&\24\2\u03ab\u03a9\3\2\2\2\u03ac\u03af\3\2\2\2\u03ad\u03ab\3\2"+
		"\2\2\u03ad\u03ae\3\2\2\2\u03aei\3\2\2\2\u03af\u03ad\3\2\2\2\u03b0\u03b1"+
		"\7\37\2\2\u03b1\u03b2\5\24\13\2\u03b2k\3\2\2\2\u03b3\u03b4\7&\2\2\u03b4"+
		"\u03b5\5n8\2\u03b5m\3\2\2\2\u03b6\u03bb\5\32\16\2\u03b7\u03b8\7N\2\2\u03b8"+
		"\u03ba\5\32\16\2\u03b9\u03b7\3\2\2\2\u03ba\u03bd\3\2\2\2\u03bb\u03b9\3"+
		"\2\2\2\u03bb\u03bc\3\2\2\2\u03bco\3\2\2\2\u03bd\u03bb\3\2\2\2\u03be\u03c2"+
		"\7I\2\2\u03bf\u03c1\5r:\2\u03c0\u03bf\3\2\2\2\u03c1\u03c4\3\2\2\2\u03c2"+
		"\u03c0\3\2\2\2\u03c2\u03c3\3\2\2\2\u03c3\u03c5\3\2\2\2\u03c4\u03c2\3\2"+
		"\2\2\u03c5\u03c6\7J\2\2\u03c6q\3\2\2\2\u03c7\u03cc\5t;\2\u03c8\u03cc\5"+
		"\u00bc_\2\u03c9\u03cc\5\u00be`\2\u03ca\u03cc\5\u00c0a\2\u03cb\u03c7\3"+
		"\2\2\2\u03cb\u03c8\3\2\2\2\u03cb\u03c9\3\2\2\2\u03cb\u03ca\3\2\2\2\u03cc"+
		"s\3\2\2\2\u03cd\u03d3\5v<\2\u03ce\u03d3\5\u009cO\2\u03cf\u03d3\5^\60\2"+
		"\u03d0\u03d3\5\u00dan\2\u03d1\u03d3\7M\2\2\u03d2\u03cd\3\2\2\2\u03d2\u03ce"+
		"\3\2\2\2\u03d2\u03cf\3\2\2\2\u03d2\u03d0\3\2\2\2\u03d2\u03d1\3\2\2\2\u03d3"+
		"u\3\2\2\2\u03d4\u03d5\5x=\2\u03d5\u03d6\5\u0084C\2\u03d6\u03d7\5|?\2\u03d7"+
		"\u03d8\7M\2\2\u03d8w\3\2\2\2\u03d9\u03db\5z>\2\u03da\u03d9\3\2\2\2\u03db"+
		"\u03de\3\2\2\2\u03dc\u03da\3\2\2\2\u03dc\u03dd\3\2\2\2\u03ddy\3\2\2\2"+
		"\u03de\u03dc\3\2\2\2\u03df\u03e8\5\u00fc\177\2\u03e0\u03e8\7\61\2\2\u03e1"+
		"\u03e8\7\60\2\2\u03e2\u03e8\7/\2\2\u03e3\u03e8\7\64\2\2\u03e4\u03e8\7"+
		" \2\2\u03e5\u03e8\7<\2\2\u03e6\u03e8\7?\2\2\u03e7\u03df\3\2\2\2\u03e7"+
		"\u03e0\3\2\2\2\u03e7\u03e1\3\2\2\2\u03e7\u03e2\3\2\2\2\u03e7\u03e3\3\2"+
		"\2\2\u03e7\u03e4\3\2\2\2\u03e7\u03e5\3\2\2\2\u03e7\u03e6\3\2\2\2\u03e8"+
		"{\3\2\2\2\u03e9\u03ee\5~@\2\u03ea\u03eb\7N\2\2\u03eb\u03ed\5~@\2\u03ec"+
		"\u03ea\3\2\2\2\u03ed\u03f0\3\2\2\2\u03ee\u03ec\3\2\2\2\u03ee\u03ef\3\2"+
		"\2\2\u03ef}\3\2\2\2\u03f0\u03ee\3\2\2\2\u03f1\u03f4\5\u0080A\2\u03f2\u03f3"+
		"\7P\2\2\u03f3\u03f5\5\u0082B\2\u03f4\u03f2\3\2\2\2\u03f4\u03f5\3\2\2\2"+
		"\u03f5\177\3\2\2\2\u03f6\u03f8\5\2\2\2\u03f7\u03f9\5$\23\2\u03f8\u03f7"+
		"\3\2\2\2\u03f8\u03f9\3\2\2\2\u03f9\u0081\3\2\2\2\u03fa\u03fd\5\u01ba\u00de"+
		"\2\u03fb\u03fd\5\u0112\u008a\2\u03fc\u03fa\3\2\2\2\u03fc\u03fb\3\2\2\2"+
		"\u03fd\u0083\3\2\2\2\u03fe\u0401\5\u0086D\2\u03ff\u0401\5\u0088E\2\u0400"+
		"\u03fe\3\2\2\2\u0400\u03ff\3\2\2\2\u0401\u0085\3\2\2\2\u0402\u0405\5\n"+
		"\6\2\u0403\u0405\7\21\2\2\u0404\u0402\3\2\2\2\u0404\u0403\3\2\2\2\u0405"+
		"\u0087\3\2\2\2\u0406\u040a\5\u008aF\2\u0407\u040a\5\u0098M\2\u0408\u040a"+
		"\5\u009aN\2\u0409\u0406\3\2\2\2\u0409\u0407\3\2\2\2\u0409\u0408\3\2\2"+
		"\2\u040a\u0089\3\2\2\2\u040b\u040e\5\u0090I\2\u040c\u040e\5\u0096L\2\u040d"+
		"\u040b\3\2\2\2\u040d\u040c\3\2\2\2\u040e\u0413\3\2\2\2\u040f\u0412\5\u008e"+
		"H\2\u0410\u0412\5\u0094K\2\u0411\u040f\3\2\2\2\u0411\u0410\3\2\2\2\u0412"+
		"\u0415\3\2\2\2\u0413\u0411\3\2\2\2\u0413\u0414\3\2\2\2\u0414\u008b\3\2"+
		"\2\2\u0415\u0413\3\2\2\2\u0416\u0418\5\2\2\2\u0417\u0419\5.\30\2\u0418"+
		"\u0417\3\2\2\2\u0418\u0419\3\2\2\2\u0419\u0421\3\2\2\2\u041a\u041b\5\u008a"+
		"F\2\u041b\u041c\7O\2\2\u041c\u041e\5\u00fe\u0080\2\u041d\u041f\5.\30\2"+
		"\u041e\u041d\3\2\2\2\u041e\u041f\3\2\2\2\u041f\u0421\3\2\2\2\u0420\u0416"+
		"\3\2\2\2\u0420\u041a\3\2\2\2\u0421\u008d\3\2\2\2\u0422\u0423\7O\2\2\u0423"+
		"\u0425\5\u00fe\u0080\2\u0424\u0426\5.\30\2\u0425\u0424\3\2\2\2\u0425\u0426"+
		"\3\2\2\2\u0426\u008f\3\2\2\2\u0427\u0429\5\2\2\2\u0428\u042a\5.\30\2\u0429"+
		"\u0428\3\2\2\2\u0429\u042a\3\2\2\2\u042a\u0091\3\2\2\2\u042b\u042c\5\u008c"+
		"G\2\u042c\u0093\3\2\2\2\u042d\u042e\5\u008eH\2\u042e\u0095\3\2\2\2\u042f"+
		"\u0430\5\u0090I\2\u0430\u0097\3\2\2\2\u0431\u0432\5\2\2\2\u0432\u0099"+
		"\3\2\2\2\u0433\u0434\5\u0086D\2\u0434\u0435\5$\23\2\u0435\u043d\3\2\2"+
		"\2\u0436\u0437\5\u008aF\2\u0437\u0438\5$\23\2\u0438\u043d\3\2\2\2\u0439"+
		"\u043a\5\u0098M\2\u043a\u043b\5$\23\2\u043b\u043d\3\2\2\2\u043c\u0433"+
		"\3\2\2\2\u043c\u0436\3\2\2\2\u043c\u0439\3\2\2\2\u043d\u009b\3\2\2\2\u043e"+
		"\u043f\5\u009eP\2\u043f\u0440\5\u00a2R\2\u0440\u0441\5\u00ba^\2\u0441"+
		"\u009d\3\2\2\2\u0442\u0444\5\u00a0Q\2\u0443\u0442\3\2\2\2\u0444\u0447"+
		"\3\2\2\2\u0445\u0443\3\2\2\2\u0445\u0446\3\2\2\2\u0446\u009f\3\2\2\2\u0447"+
		"\u0445\3\2\2\2\u0448\u0453\5\u00fc\177\2\u0449\u0453\7\61\2\2\u044a\u0453"+
		"\7\60\2\2\u044b\u0453\7/\2\2\u044c\u0453\7\17\2\2\u044d\u0453\7\64\2\2"+
		"\u044e\u0453\7 \2\2\u044f\u0453\78\2\2\u0450\u0453\7,\2\2\u0451\u0453"+
		"\7\65\2\2\u0452\u0448\3\2\2\2\u0452\u0449\3\2\2\2\u0452\u044a\3\2\2\2"+
		"\u0452\u044b\3\2\2\2\u0452\u044c\3\2\2\2\u0452\u044d\3\2\2\2\u0452\u044e"+
		"\3\2\2\2\u0452\u044f\3\2\2\2\u0452\u0450\3\2\2\2\u0452\u0451\3\2\2\2\u0453"+
		"\u00a1\3\2\2\2\u0454\u0455\5\u00a4S\2\u0455\u0457\5\u00a6T\2\u0456\u0458"+
		"\5\u00b4[\2\u0457\u0456\3\2\2\2\u0457\u0458\3\2\2\2\u0458\u0466\3\2\2"+
		"\2\u0459\u045d\5f\64\2\u045a\u045c\5\u00fc\177\2\u045b\u045a\3\2\2\2\u045c"+
		"\u045f\3\2\2\2\u045d\u045b\3\2\2\2\u045d\u045e\3\2\2\2\u045e\u0460\3\2"+
		"\2\2\u045f\u045d\3\2\2\2\u0460\u0461\5\u00a4S\2\u0461\u0463\5\u00a6T\2"+
		"\u0462\u0464\5\u00b4[\2\u0463\u0462\3\2\2\2\u0463\u0464\3\2\2\2\u0464"+
		"\u0466\3\2\2\2\u0465\u0454\3\2\2\2\u0465\u0459\3\2\2\2\u0466\u00a3\3\2"+
		"\2\2\u0467\u046a\5\u0084C\2\u0468\u046a\7>\2\2\u0469\u0467\3\2\2\2\u0469"+
		"\u0468\3\2\2\2\u046a\u00a5\3\2\2\2\u046b\u046c\5\2\2\2\u046c\u046e\7G"+
		"\2\2\u046d\u046f\5\u00a8U\2\u046e\u046d\3\2\2\2\u046e\u046f\3\2\2\2\u046f"+
		"\u0470\3\2\2\2\u0470\u0472\7H\2\2\u0471\u0473\5$\23\2\u0472\u0471\3\2"+
		"\2\2\u0472\u0473\3\2\2\2\u0473\u00a7\3\2\2\2\u0474\u0475\5\u00aaV\2\u0475"+
		"\u0476\7N\2\2\u0476\u0477\5\u00b0Y\2\u0477\u047a\3\2\2\2\u0478\u047a\5"+
		"\u00b0Y\2\u0479\u0474\3\2\2\2\u0479\u0478\3\2\2\2\u047a\u00a9\3\2\2\2"+
		"\u047b\u0480\5\u00acW\2\u047c\u047d\7N\2\2\u047d\u047f\5\u00acW\2\u047e"+
		"\u047c\3\2\2\2\u047f\u0482\3\2\2\2\u0480\u047e\3\2\2\2\u0480\u0481\3\2"+
		"\2\2\u0481\u048c\3\2\2\2\u0482\u0480\3\2\2\2\u0483\u0488\5\u00b2Z\2\u0484"+
		"\u0485\7N\2\2\u0485\u0487\5\u00acW\2\u0486\u0484\3\2\2\2\u0487\u048a\3"+
		"\2\2\2\u0488\u0486\3\2\2\2\u0488\u0489\3\2\2\2\u0489\u048c\3\2\2\2\u048a"+
		"\u0488\3\2\2\2\u048b\u047b\3\2\2\2\u048b\u0483\3\2\2\2\u048c\u00ab\3\2"+
		"\2\2\u048d\u048f\5\u00aeX\2\u048e\u048d\3\2\2\2\u048f\u0492\3\2\2\2\u0490"+
		"\u048e\3\2\2\2\u0490\u0491\3\2\2\2\u0491\u0493\3\2\2\2\u0492\u0490\3\2"+
		"\2\2\u0493\u0494\5\u0084C\2\u0494\u0495\5\u0080A\2\u0495\u00ad\3\2\2\2"+
		"\u0496\u0499\5\u00fc\177\2\u0497\u0499\7 \2\2\u0498\u0496\3\2\2\2\u0498"+
		"\u0497\3\2\2\2\u0499\u00af\3\2\2\2\u049a\u049c\5\u00aeX\2\u049b\u049a"+
		"\3\2\2\2\u049c\u049f\3\2\2\2\u049d\u049b\3\2\2\2\u049d\u049e\3\2\2\2\u049e"+
		"\u04a0\3\2\2\2\u049f\u049d\3\2\2\2\u04a0\u04a4\5\u0084C\2\u04a1\u04a3"+
		"\5\u00fc\177\2\u04a2\u04a1\3\2\2\2\u04a3\u04a6\3\2\2\2\u04a4\u04a2\3\2"+
		"\2\2\u04a4\u04a5\3\2\2\2\u04a5\u04a7\3\2\2\2\u04a6\u04a4\3\2\2\2\u04a7"+
		"\u04a8\7v\2\2\u04a8\u04a9\5\u0080A\2\u04a9\u04ac\3\2\2\2\u04aa\u04ac\5"+
		"\u00acW\2\u04ab\u049d\3\2\2\2\u04ab\u04aa\3\2\2\2\u04ac\u00b1\3\2\2\2"+
		"\u04ad\u04af\5\u00fc\177\2\u04ae\u04ad\3\2\2\2\u04af\u04b2\3\2\2\2\u04b0"+
		"\u04ae\3\2\2\2\u04b0\u04b1\3\2\2\2\u04b1\u04b3\3\2\2\2\u04b2\u04b0\3\2"+
		"\2\2\u04b3\u04b7\5\u0084C\2\u04b4\u04b5\5\2\2\2\u04b5\u04b6\7O\2\2\u04b6"+
		"\u04b8\3\2\2\2\u04b7\u04b4\3\2\2\2\u04b7\u04b8\3\2\2\2\u04b8\u04b9\3\2"+
		"\2\2\u04b9\u04ba\79\2\2\u04ba\u00b3\3\2\2\2\u04bb\u04bc\7;\2\2\u04bc\u04bd"+
		"\5\u00b6\\\2\u04bd\u00b5\3\2\2\2\u04be\u04c3\5\u00b8]\2\u04bf\u04c0\7"+
		"N\2\2\u04c0\u04c2\5\u00b8]\2\u04c1\u04bf\3\2\2\2\u04c2\u04c5\3\2\2\2\u04c3"+
		"\u04c1\3\2\2\2\u04c3\u04c4\3\2\2\2\u04c4\u00b7\3\2\2\2\u04c5\u04c3\3\2"+
		"\2\2\u04c6\u04c9\5\24\13\2\u04c7\u04c9\5 \21\2\u04c8\u04c6\3\2\2\2\u04c8"+
		"\u04c7\3\2\2\2\u04c9\u00b9\3\2\2\2\u04ca\u04cd\5\u0116\u008c\2\u04cb\u04cd"+
		"\7M\2\2\u04cc\u04ca\3\2\2\2\u04cc\u04cb\3\2\2\2\u04cd\u00bb\3\2\2\2\u04ce"+
		"\u04cf\5\u0116\u008c\2\u04cf\u00bd\3\2\2\2\u04d0\u04d1\7\64\2\2\u04d1"+
		"\u04d2\5\u0116\u008c\2\u04d2\u00bf\3\2\2\2\u04d3\u04d4\5\u00c2b\2\u04d4"+
		"\u04d6\5\u00c6d\2\u04d5\u04d7\5\u00b4[\2\u04d6\u04d5\3\2\2\2\u04d6\u04d7"+
		"\3\2\2\2\u04d7\u04d8\3\2\2\2\u04d8\u04d9\5\u00caf\2\u04d9\u00c1\3\2\2"+
		"\2\u04da\u04dc\5\u00c4c\2\u04db\u04da\3\2\2\2\u04dc\u04df\3\2\2\2\u04dd"+
		"\u04db\3\2\2\2\u04dd\u04de\3\2\2\2\u04de\u00c3\3\2\2\2\u04df\u04dd\3\2"+
		"\2\2\u04e0\u04e5\5\u00fc\177\2\u04e1\u04e5\7\61\2\2\u04e2\u04e5\7\60\2"+
		"\2\u04e3\u04e5\7/\2\2\u04e4\u04e0\3\2\2\2\u04e4\u04e1\3\2\2\2\u04e4\u04e2"+
		"\3\2\2\2\u04e4\u04e3\3\2\2\2\u04e5\u00c5\3\2\2\2\u04e6\u04e8\5f\64\2\u04e7"+
		"\u04e6\3\2\2\2\u04e7\u04e8\3\2\2\2\u04e8\u04e9\3\2\2\2\u04e9\u04ea\5\u00c8"+
		"e\2\u04ea\u04ec\7G\2\2\u04eb\u04ed\5\u00a8U\2\u04ec\u04eb\3\2\2\2\u04ec"+
		"\u04ed\3\2\2\2\u04ed\u04ee\3\2\2\2\u04ee\u04ef\7H\2\2\u04ef\u00c7\3\2"+
		"\2\2\u04f0\u04f1\5\2\2\2\u04f1\u00c9\3\2\2\2\u04f2\u04f4\7I\2\2\u04f3"+
		"\u04f5\5\u00ccg\2\u04f4\u04f3\3\2\2\2\u04f4\u04f5\3\2\2\2\u04f5\u04f7"+
		"\3\2\2\2\u04f6\u04f8\5\u0118\u008d\2\u04f7\u04f6\3\2\2\2\u04f7\u04f8\3"+
		"\2\2\2\u04f8\u04f9\3\2\2\2\u04f9\u04fa\7J\2\2\u04fa\u00cb\3\2\2\2\u04fb"+
		"\u04fd\5.\30\2\u04fc\u04fb\3\2\2\2\u04fc\u04fd\3\2\2\2\u04fd\u04fe\3\2"+
		"\2\2\u04fe\u04ff\79\2\2\u04ff\u0501\7G\2\2\u0500\u0502\5\u01aa\u00d6\2"+
		"\u0501\u0500\3\2\2\2\u0501\u0502\3\2\2\2\u0502\u0503\3\2\2\2\u0503\u0504"+
		"\7H\2\2\u0504\u052a\7M\2\2\u0505\u0507\5.\30\2\u0506\u0505\3\2\2\2\u0506"+
		"\u0507\3\2\2\2\u0507\u0508\3\2\2\2\u0508\u0509\7\66\2\2\u0509\u050b\7"+
		"G\2\2\u050a\u050c\5\u01aa\u00d6\2\u050b\u050a\3\2\2\2\u050b\u050c\3\2"+
		"\2\2\u050c\u050d\3\2\2\2\u050d\u050e\7H\2\2\u050e\u052a\7M\2\2\u050f\u0510"+
		"\5> \2\u0510\u0512\7O\2\2\u0511\u0513\5.\30\2\u0512\u0511\3\2\2\2\u0512"+
		"\u0513\3\2\2\2\u0513\u0514\3\2\2\2\u0514\u0515\7\66\2\2\u0515\u0517\7"+
		"G\2\2\u0516\u0518\5\u01aa\u00d6\2\u0517\u0516\3\2\2\2\u0517\u0518\3\2"+
		"\2\2\u0518\u0519\3\2\2\2\u0519\u051a\7H\2\2\u051a\u051b\7M\2\2\u051b\u052a"+
		"\3\2\2\2\u051c\u051d\5\u017c\u00bf\2\u051d\u051f\7O\2\2\u051e\u0520\5"+
		".\30\2\u051f\u051e\3\2\2\2\u051f\u0520\3\2\2\2\u0520\u0521\3\2\2\2\u0521"+
		"\u0522\7\66\2\2\u0522\u0524\7G\2\2\u0523\u0525\5\u01aa\u00d6\2\u0524\u0523"+
		"\3\2\2\2\u0524\u0525\3\2\2\2\u0525\u0526\3\2\2\2\u0526\u0527\7H\2\2\u0527"+
		"\u0528\7M\2\2\u0528\u052a\3\2\2\2\u0529\u04fc\3\2\2\2\u0529\u0506\3\2"+
		"\2\2\u0529\u050f\3\2\2\2\u0529\u051c\3\2\2\2\u052a\u00cd\3\2\2\2\u052b"+
		"\u052c\5b\62\2\u052c\u052d\7\36\2\2\u052d\u052f\5\2\2\2\u052e\u0530\5"+
		"l\67\2\u052f\u052e\3\2\2\2\u052f\u0530\3\2\2\2\u0530\u0531\3\2\2\2\u0531"+
		"\u0532\5\u00d0i\2\u0532\u00cf\3\2\2\2\u0533\u0535\7I\2\2\u0534\u0536\5"+
		"\u00d2j\2\u0535\u0534\3\2\2\2\u0535\u0536\3\2\2\2\u0536\u0538\3\2\2\2"+
		"\u0537\u0539\7N\2\2\u0538\u0537\3\2\2\2\u0538\u0539\3\2\2\2\u0539\u053b"+
		"\3\2\2\2\u053a\u053c\5\u00d8m\2\u053b\u053a\3\2\2\2\u053b\u053c\3\2\2"+
		"\2\u053c\u053d\3\2\2\2\u053d\u053e\7J\2\2\u053e\u00d1\3\2\2\2\u053f\u0544"+
		"\5\u00d4k\2\u0540\u0541\7N\2\2\u0541\u0543\5\u00d4k\2\u0542\u0540\3\2"+
		"\2\2\u0543\u0546\3\2\2\2\u0544\u0542\3\2\2\2\u0544\u0545\3\2\2\2\u0545"+
		"\u00d3\3\2\2\2\u0546\u0544\3\2\2\2\u0547\u0549\5\u00d6l\2\u0548\u0547"+
		"\3\2\2\2\u0549\u054c\3\2\2\2\u054a\u0548\3\2\2\2\u054a\u054b\3\2\2\2\u054b"+
		"\u054d\3\2\2\2\u054c\u054a\3\2\2\2\u054d\u0553\5\2\2\2\u054e\u0550\7G"+
		"\2\2\u054f\u0551\5\u01aa\u00d6\2\u0550\u054f\3\2\2\2\u0550\u0551\3\2\2"+
		"\2\u0551\u0552\3\2\2\2\u0552\u0554\7H\2\2\u0553\u054e\3\2\2\2\u0553\u0554"+
		"\3\2\2\2\u0554\u0556\3\2\2\2\u0555\u0557\5p9\2\u0556\u0555\3\2\2\2\u0556"+
		"\u0557\3\2\2\2\u0557\u00d5\3\2\2\2\u0558\u0559\5\u00fc\177\2\u0559\u00d7"+
		"\3\2\2\2\u055a\u055e\7M\2\2\u055b\u055d\5r:\2\u055c\u055b\3\2\2\2\u055d"+
		"\u0560\3\2\2\2\u055e\u055c\3\2\2\2\u055e\u055f\3\2\2\2\u055f\u00d9\3\2"+
		"\2\2\u0560\u055e\3\2\2\2\u0561\u0564\5\u00dco\2\u0562\u0564\5\u00f0y\2"+
		"\u0563\u0561\3\2\2\2\u0563\u0562\3\2\2\2\u0564\u00db\3\2\2\2\u0565\u0566"+
		"\5\u00dep\2\u0566\u0567\7*\2\2\u0567\u0569\5\2\2\2\u0568\u056a\5f\64\2"+
		"\u0569\u0568\3\2\2\2\u0569\u056a\3\2\2\2\u056a\u056c\3\2\2\2\u056b\u056d"+
		"\5\u00e2r\2\u056c\u056b\3\2\2\2\u056c\u056d\3\2\2\2\u056d\u056e\3\2\2"+
		"\2\u056e\u056f\5\u00e4s\2\u056f\u00dd\3\2\2\2\u0570\u0572\5\u00e0q\2\u0571"+
		"\u0570\3\2\2\2\u0572\u0575\3\2\2\2\u0573\u0571\3\2\2\2\u0573\u0574\3\2"+
		"\2\2\u0574\u00df\3\2\2\2\u0575\u0573\3\2\2\2\u0576\u057e\5\u00fc\177\2"+
		"\u0577\u057e\7\61\2\2\u0578\u057e\7\60\2\2\u0579\u057e\7/\2\2\u057a\u057e"+
		"\7\17\2\2\u057b\u057e\7\64\2\2\u057c\u057e\7\65\2\2\u057d\u0576\3\2\2"+
		"\2\u057d\u0577\3\2\2\2\u057d\u0578\3\2\2\2\u057d\u0579\3\2\2\2\u057d\u057a"+
		"\3\2\2\2\u057d\u057b\3\2\2\2\u057d\u057c\3\2\2\2\u057e\u00e1\3\2\2\2\u057f"+
		"\u0580\7\37\2\2\u0580\u0581\5n8\2\u0581\u00e3\3\2\2\2\u0582\u0586\7I\2"+
		"\2\u0583\u0585\5\u00e6t\2\u0584\u0583\3\2\2\2\u0585\u0588\3\2\2\2\u0586"+
		"\u0584\3\2\2\2\u0586\u0587\3\2\2\2\u0587\u0589\3\2\2\2\u0588\u0586\3\2"+
		"\2\2\u0589\u058a\7J\2\2\u058a\u00e5\3\2\2\2\u058b\u0591\5\u00e8u\2\u058c"+
		"\u0591\5\u00ecw\2\u058d\u0591\5^\60\2\u058e\u0591\5\u00dan\2\u058f\u0591"+
		"\7M\2\2\u0590\u058b\3\2\2\2\u0590\u058c\3\2\2\2\u0590\u058d\3\2\2\2\u0590"+
		"\u058e\3\2\2\2\u0590\u058f\3\2\2\2\u0591\u00e7\3\2\2\2\u0592\u0594\5\u00ea"+
		"v\2\u0593\u0592\3\2\2\2\u0594\u0597\3\2\2\2\u0595\u0593\3\2\2\2\u0595"+
		"\u0596\3\2\2\2\u0596\u0598\3\2\2\2\u0597\u0595\3\2\2\2\u0598\u0599\5\u0084"+
		"C\2\u0599\u059a\5|?\2\u059a\u059b\7M\2\2\u059b\u00e9\3\2\2\2\u059c\u05a1"+
		"\5\u00fc\177\2\u059d\u05a1\7\61\2\2\u059e\u05a1\7\64\2\2\u059f\u05a1\7"+
		" \2\2\u05a0\u059c\3\2\2\2\u05a0\u059d\3\2\2\2\u05a0\u059e\3\2\2\2\u05a0"+
		"\u059f\3\2\2\2\u05a1\u00eb\3\2\2\2\u05a2\u05a4\5\u00eex\2\u05a3\u05a2"+
		"\3\2\2\2\u05a4\u05a7\3\2\2\2\u05a5\u05a3\3\2\2\2\u05a5\u05a6\3\2\2\2\u05a6"+
		"\u05a8\3\2\2\2\u05a7\u05a5\3\2\2\2\u05a8\u05a9\5\u00a2R\2\u05a9\u05aa"+
		"\5\u00ba^\2\u05aa\u00ed\3\2\2\2\u05ab\u05b3\5\u00fc\177\2\u05ac\u05b3"+
		"\7\61\2\2\u05ad\u05b3\7/\2\2\u05ae\u05b3\7\17\2\2\u05af\u05b3\7\32\2\2"+
		"\u05b0\u05b3\7\64\2\2\u05b1\u05b3\7\65\2\2\u05b2\u05ab\3\2\2\2\u05b2\u05ac"+
		"\3\2\2\2\u05b2\u05ad\3\2\2\2\u05b2\u05ae\3\2\2\2\u05b2\u05af\3\2\2\2\u05b2"+
		"\u05b0\3\2\2\2\u05b2\u05b1\3\2\2\2\u05b3\u00ef\3\2\2\2\u05b4\u05b6\5\u00e0"+
		"q\2\u05b5\u05b4\3\2\2\2\u05b6\u05b9\3\2\2\2\u05b7\u05b5\3\2\2\2\u05b7"+
		"\u05b8\3\2\2\2\u05b8\u05ba\3\2\2\2\u05b9\u05b7\3\2\2\2\u05ba\u05bb\7u"+
		"\2\2\u05bb\u05bc\7*\2\2\u05bc\u05bd\5\2\2\2\u05bd\u05be\5\u00f2z\2\u05be"+
		"\u00f1\3\2\2\2\u05bf\u05c3\7I\2\2\u05c0\u05c2\5\u00f4{\2\u05c1\u05c0\3"+
		"\2\2\2\u05c2\u05c5\3\2\2\2\u05c3\u05c1\3\2\2\2\u05c3\u05c4\3\2\2\2\u05c4"+
		"\u05c6\3\2\2\2\u05c5\u05c3\3\2\2\2\u05c6\u05c7\7J\2\2\u05c7\u00f3\3\2"+
		"\2\2\u05c8\u05ce\5\u00f6|\2\u05c9\u05ce\5\u00e8u\2\u05ca\u05ce\5^\60\2"+
		"\u05cb\u05ce\5\u00dan\2\u05cc\u05ce\7M\2\2\u05cd\u05c8\3\2\2\2\u05cd\u05c9"+
		"\3\2\2\2\u05cd\u05ca\3\2\2\2\u05cd\u05cb\3\2\2\2\u05cd\u05cc\3\2\2\2\u05ce"+
		"\u00f5\3\2\2\2\u05cf\u05d1\5\u00f8}\2\u05d0\u05cf\3\2\2\2\u05d1\u05d4"+
		"\3\2\2\2\u05d2\u05d0\3\2\2\2\u05d2\u05d3\3\2\2\2\u05d3\u05d5\3\2\2\2\u05d4"+
		"\u05d2\3\2\2\2\u05d5\u05d6\5\u0084C\2\u05d6\u05d7\5\2\2\2\u05d7\u05d8"+
		"\7G\2\2\u05d8\u05da\7H\2\2\u05d9\u05db\5$\23\2\u05da\u05d9\3\2\2\2\u05da"+
		"\u05db\3\2\2\2\u05db\u05dd\3\2\2\2\u05dc\u05de\5\u00fa~\2\u05dd\u05dc"+
		"\3\2\2\2\u05dd\u05de\3\2\2\2\u05de\u05df\3\2\2\2\u05df\u05e0\7M\2\2\u05e0"+
		"\u00f7\3\2\2\2\u05e1\u05e5\5\u00fc\177\2\u05e2\u05e5\7\61\2\2\u05e3\u05e5"+
		"\7\17\2\2\u05e4\u05e1\3\2\2\2\u05e4\u05e2\3\2\2\2\u05e4\u05e3\3\2\2\2"+
		"\u05e5\u00f9\3\2\2\2\u05e6\u05e7\7\32\2\2\u05e7\u05e8\5\u0108\u0085\2"+
		"\u05e8\u00fb\3\2\2\2\u05e9\u05ed\5\u0102\u0082\2\u05ea\u05ed\5\u010e\u0088"+
		"\2\u05eb\u05ed\5\u0110\u0089\2\u05ec\u05e9\3\2\2\2\u05ec\u05ea\3\2\2\2"+
		"\u05ec\u05eb\3\2\2\2\u05ed\u00fd\3\2\2\2\u05ee\u05f0\5\u00fc\177\2\u05ef"+
		"\u05ee\3\2\2\2\u05f0\u05f3\3\2\2\2\u05f1\u05ef\3\2\2\2\u05f1\u05f2\3\2"+
		"\2\2\u05f2\u05f4\3\2\2\2\u05f3\u05f1\3\2\2\2\u05f4\u05f5\5\2\2\2\u05f5"+
		"\u00ff\3\2\2\2\u05f6\u05f8\5\u00fc\177\2\u05f7\u05f6\3\2\2\2\u05f8\u05fb"+
		"\3\2\2\2\u05f9\u05f7\3\2\2\2\u05f9\u05fa\3\2\2\2\u05fa\u05fc\3\2\2\2\u05fb"+
		"\u05f9\3\2\2\2\u05fc\u05fd\5\u01d2\u00ea\2\u05fd\u0101\3\2\2\2\u05fe\u05ff"+
		"\7u\2\2\u05ff\u0600\5:\36\2\u0600\u0602\7G\2\2\u0601\u0603\5\u0104\u0083"+
		"\2\u0602\u0601\3\2\2\2\u0602\u0603\3\2\2\2\u0603\u0604\3\2\2\2\u0604\u0605"+
		"\7H\2\2\u0605\u0103\3\2\2\2\u0606\u060b\5\u0106\u0084\2\u0607\u0608\7"+
		"N\2\2\u0608\u060a\5\u0106\u0084\2\u0609\u0607\3\2\2\2\u060a\u060d\3\2"+
		"\2\2\u060b\u0609\3\2\2\2\u060b\u060c\3\2\2\2\u060c\u0105\3\2\2\2\u060d"+
		"\u060b\3\2\2\2\u060e\u060f\5\2\2\2\u060f\u0610\7P\2\2\u0610\u0611\5\u0108"+
		"\u0085\2\u0611\u0107\3\2\2\2\u0612\u0616\5\u01d4\u00eb\2\u0613\u0616\5"+
		"\u010a\u0086\2\u0614\u0616\5\u00fc\177\2\u0615\u0612\3\2\2\2\u0615\u0613"+
		"\3\2\2\2\u0615\u0614\3\2\2\2\u0616\u0109\3\2\2\2\u0617\u0619\7I\2\2\u0618"+
		"\u061a\5\u010c\u0087\2\u0619\u0618\3\2\2\2\u0619\u061a\3\2\2\2\u061a\u061c"+
		"\3\2\2\2\u061b\u061d\7N\2\2\u061c\u061b\3\2\2\2\u061c\u061d\3\2\2\2\u061d"+
		"\u061e\3\2\2\2\u061e\u061f\7J\2\2\u061f\u010b\3\2\2\2\u0620\u0625\5\u0108"+
		"\u0085\2\u0621\u0622\7N\2\2\u0622\u0624\5\u0108\u0085\2\u0623\u0621\3"+
		"\2\2\2\u0624\u0627\3\2\2\2\u0625\u0623\3\2\2\2\u0625\u0626\3\2\2\2\u0626"+
		"\u010d\3\2\2\2\u0627\u0625\3\2\2\2\u0628\u0629\7u\2\2\u0629\u062a\5:\36"+
		"\2\u062a\u010f\3\2\2\2\u062b\u062c\7u\2\2\u062c\u062d\5:\36\2\u062d\u062e"+
		"\7G\2\2\u062e\u062f\5\u0108\u0085\2\u062f\u0630\7H\2\2\u0630\u0111\3\2"+
		"\2\2\u0631\u0633\7I\2\2\u0632\u0634\5\u0114\u008b\2\u0633\u0632\3\2\2"+
		"\2\u0633\u0634\3\2\2\2\u0634\u0636\3\2\2\2\u0635\u0637\7N\2\2\u0636\u0635"+
		"\3\2\2\2\u0636\u0637\3\2\2\2\u0637\u0638\3\2\2\2\u0638\u0639\7J\2\2\u0639"+
		"\u0113\3\2\2\2\u063a\u063f\5\u0082B\2\u063b\u063c\7N\2\2\u063c\u063e\5"+
		"\u0082B\2\u063d\u063b\3\2\2\2\u063e\u0641\3\2\2\2\u063f\u063d\3\2\2\2"+
		"\u063f\u0640\3\2\2\2\u0640\u0115\3\2\2\2\u0641\u063f\3\2\2\2\u0642\u0644"+
		"\7I\2\2\u0643\u0645\5\u0118\u008d\2\u0644\u0643\3\2\2\2\u0644\u0645\3"+
		"\2\2\2\u0645\u0646\3\2\2\2\u0646\u0647\7J\2\2\u0647\u0117\3\2\2\2\u0648"+
		"\u064c\5\u011a\u008e\2\u0649\u064b\5\u011a\u008e\2\u064a\u0649\3\2\2\2"+
		"\u064b\u064e\3\2\2\2\u064c\u064a\3\2\2\2\u064c\u064d\3\2\2\2\u064d\u0119"+
		"\3\2\2\2\u064e\u064c\3\2\2\2\u064f\u0653\5\u011c\u008f\2\u0650\u0653\5"+
		"^\60\2\u0651\u0653\5\u0120\u0091\2\u0652\u064f\3\2\2\2\u0652\u0650\3\2"+
		"\2\2\u0652\u0651\3\2\2\2\u0653\u011b\3\2\2\2\u0654\u0655\5\u011e\u0090"+
		"\2\u0655\u0656\7M\2\2\u0656\u011d\3\2\2\2\u0657\u0659\5\u00aeX\2\u0658"+
		"\u0657\3\2\2\2\u0659\u065c\3\2\2\2\u065a\u0658\3\2\2\2\u065a\u065b\3\2"+
		"\2\2\u065b\u065d\3\2\2\2\u065c\u065a\3\2\2\2\u065d\u065e\5\u0084C\2\u065e"+
		"\u065f\5|?\2\u065f\u011f\3\2\2\2\u0660\u0667\5\u0124\u0093\2\u0661\u0667"+
		"\5\u0128\u0095\2\u0662\u0667\5\u0130\u0099\2\u0663\u0667\5\u0132\u009a"+
		"\2\u0664\u0667\5\u0144\u00a3\2\u0665\u0667\5\u014a\u00a6\2\u0666\u0660"+
		"\3\2\2\2\u0666\u0661\3\2\2\2\u0666\u0662\3\2\2\2\u0666\u0663\3\2\2\2\u0666"+
		"\u0664\3\2\2\2\u0666\u0665\3\2\2\2\u0667\u0121\3\2\2\2\u0668\u066e\5\u0124"+
		"\u0093\2\u0669\u066e\5\u012a\u0096\2\u066a\u066e\5\u0134\u009b\2\u066b"+
		"\u066e\5\u0146\u00a4\2\u066c\u066e\5\u014c\u00a7\2\u066d\u0668\3\2\2\2"+
		"\u066d\u0669\3\2\2\2\u066d\u066a\3\2\2\2\u066d\u066b\3\2\2\2\u066d\u066c"+
		"\3\2\2\2\u066e\u0123\3\2\2\2\u066f\u067c\5\u0116\u008c\2\u0670\u067c\5"+
		"\u0126\u0094\2\u0671\u067c\5\u012c\u0097\2\u0672\u067c\5\u0136\u009c\2"+
		"\u0673\u067c\5\u0138\u009d\2\u0674\u067c\5\u0148\u00a5\2\u0675\u067c\5"+
		"\u015c\u00af\2\u0676\u067c\5\u015e\u00b0\2\u0677\u067c\5\u0160\u00b1\2"+
		"\u0678\u067c\5\u0164\u00b3\2\u0679\u067c\5\u0162\u00b2\2\u067a\u067c\5"+
		"\u0166\u00b4\2\u067b\u066f\3\2\2\2\u067b\u0670\3\2\2\2\u067b\u0671\3\2"+
		"\2\2\u067b\u0672\3\2\2\2\u067b\u0673\3\2\2\2\u067b\u0674\3\2\2\2\u067b"+
		"\u0675\3\2\2\2\u067b\u0676\3\2\2\2\u067b\u0677\3\2\2\2\u067b\u0678\3\2"+
		"\2\2\u067b\u0679\3\2\2\2\u067b\u067a\3\2\2\2\u067c\u0125\3\2\2\2\u067d"+
		"\u067e\7M\2\2\u067e\u0127\3\2\2\2\u067f\u0680\5\2\2\2\u0680\u0681\7V\2"+
		"\2\u0681\u0682\5\u0120\u0091\2\u0682\u0129\3\2\2\2\u0683\u0684\5\2\2\2"+
		"\u0684\u0685\7V\2\2\u0685\u0686\5\u0122\u0092\2\u0686\u012b\3\2\2\2\u0687"+
		"\u0688\5\u012e\u0098\2\u0688\u0689\7M\2\2\u0689\u012d\3\2\2\2\u068a\u0692"+
		"\5\u01c6\u00e4\2\u068b\u0692\5\u01ee\u00f8\2\u068c\u0692\5\u01f0\u00f9"+
		"\2\u068d\u0692\5\u01f6\u00fc\2\u068e\u0692\5\u01fa\u00fe\2\u068f\u0692"+
		"\5\u01a4\u00d3\2\u0690\u0692\5\u0190\u00c9\2\u0691\u068a\3\2\2\2\u0691"+
		"\u068b\3\2\2\2\u0691\u068c\3\2\2\2\u0691\u068d\3\2\2\2\u0691\u068e\3\2"+
		"\2\2\u0691\u068f\3\2\2\2\u0691\u0690\3\2\2\2\u0692\u012f\3\2\2\2\u0693"+
		"\u0694\7$\2\2\u0694\u0695\7G\2\2\u0695\u0696\5\u01ba\u00de\2\u0696\u0697"+
		"\7H\2\2\u0697\u0698\5\u0120\u0091\2\u0698\u0131\3\2\2\2\u0699\u069a\7"+
		"$\2\2\u069a\u069b\7G\2\2\u069b\u069c\5\u01ba\u00de\2\u069c\u069d\7H\2"+
		"\2\u069d\u069e\5\u0122\u0092\2\u069e\u069f\7\35\2\2\u069f\u06a0\5\u0120"+
		"\u0091\2\u06a0\u0133\3\2\2\2\u06a1\u06a2\7$\2\2\u06a2\u06a3\7G\2\2\u06a3"+
		"\u06a4\5\u01ba\u00de\2\u06a4\u06a5\7H\2\2\u06a5\u06a6\5\u0122\u0092\2"+
		"\u06a6\u06a7\7\35\2\2\u06a7\u06a8\5\u0122\u0092\2\u06a8\u0135\3\2\2\2"+
		"\u06a9\u06aa\7\20\2\2\u06aa\u06ab\5\u01ba\u00de\2\u06ab\u06ac\7M\2\2\u06ac"+
		"\u06b4\3\2\2\2\u06ad\u06ae\7\20\2\2\u06ae\u06af\5\u01ba\u00de\2\u06af"+
		"\u06b0\7V\2\2\u06b0\u06b1\5\u01ba\u00de\2\u06b1\u06b2\7M\2\2\u06b2\u06b4"+
		"\3\2\2\2\u06b3\u06a9\3\2\2\2\u06b3\u06ad\3\2\2\2\u06b4\u0137\3\2\2\2\u06b5"+
		"\u06b6\7\67\2\2\u06b6\u06b7\7G\2\2\u06b7\u06b8\5\u01ba\u00de\2\u06b8\u06b9"+
		"\7H\2\2\u06b9\u06ba\5\u013a\u009e\2\u06ba\u0139\3\2\2\2\u06bb\u06bf\7"+
		"I\2\2\u06bc\u06be\5\u013c\u009f\2\u06bd\u06bc\3\2\2\2\u06be\u06c1\3\2"+
		"\2\2\u06bf\u06bd\3\2\2\2\u06bf\u06c0\3\2\2\2\u06c0\u06c5\3\2\2\2\u06c1"+
		"\u06bf\3\2\2\2\u06c2\u06c4\5\u0140\u00a1\2\u06c3\u06c2\3\2\2\2\u06c4\u06c7"+
		"\3\2\2\2\u06c5\u06c3\3\2\2\2\u06c5\u06c6\3\2\2\2\u06c6\u06c8\3\2\2\2\u06c7"+
		"\u06c5\3\2\2\2\u06c8\u06c9\7J\2\2\u06c9\u013b\3\2\2\2\u06ca\u06cb\5\u013e"+
		"\u00a0\2\u06cb\u06cc\5\u0118\u008d\2\u06cc\u013d\3\2\2\2\u06cd\u06d1\5"+
		"\u0140\u00a1\2\u06ce\u06d0\5\u0140\u00a1\2\u06cf\u06ce\3\2\2\2\u06d0\u06d3"+
		"\3\2\2\2\u06d1\u06cf\3\2\2\2\u06d1\u06d2\3\2\2\2\u06d2\u013f\3\2\2\2\u06d3"+
		"\u06d1\3\2\2\2\u06d4\u06d5\7\24\2\2\u06d5\u06d6\5\u01b8\u00dd\2\u06d6"+
		"\u06d7\7V\2\2\u06d7\u06df\3\2\2\2\u06d8\u06d9\7\24\2\2\u06d9\u06da\5\u0142"+
		"\u00a2\2\u06da\u06db\7V\2\2\u06db\u06df\3\2\2\2\u06dc\u06dd\7\32\2\2\u06dd"+
		"\u06df\7V\2\2\u06de\u06d4\3\2\2\2\u06de\u06d8\3\2\2\2\u06de\u06dc\3\2"+
		"\2\2\u06df\u0141\3\2\2\2\u06e0\u06e1\5\2\2\2\u06e1\u0143\3\2\2\2\u06e2"+
		"\u06e3\7@\2\2\u06e3\u06e4\7G\2\2\u06e4\u06e5\5\u01ba\u00de\2\u06e5\u06e6"+
		"\7H\2\2\u06e6\u06e7\5\u0120\u0091\2\u06e7\u0145\3\2\2\2\u06e8\u06e9\7"+
		"@\2\2\u06e9\u06ea\7G\2\2\u06ea\u06eb\5\u01ba\u00de\2\u06eb\u06ec\7H\2"+
		"\2\u06ec\u06ed\5\u0122\u0092\2\u06ed\u0147\3\2\2\2\u06ee\u06ef\7\33\2"+
		"\2\u06ef\u06f0\5\u0120\u0091\2\u06f0\u06f1\7@\2\2\u06f1\u06f2\7G\2\2\u06f2"+
		"\u06f3\5\u01ba\u00de\2\u06f3\u06f4\7H\2\2\u06f4\u06f5\7M\2\2\u06f5\u0149"+
		"\3\2\2\2\u06f6\u06f9\5\u014e\u00a8\2\u06f7\u06f9\5\u0158\u00ad\2\u06f8"+
		"\u06f6\3\2\2\2\u06f8\u06f7\3\2\2\2\u06f9\u014b\3\2\2\2\u06fa\u06fd\5\u0150"+
		"\u00a9\2\u06fb\u06fd\5\u015a\u00ae\2\u06fc\u06fa\3\2\2\2\u06fc\u06fb\3"+
		"\2\2\2\u06fd\u014d\3\2\2\2\u06fe\u06ff\7#\2\2\u06ff\u0701\7G\2\2\u0700"+
		"\u0702\5\u0152\u00aa\2\u0701\u0700\3\2\2\2\u0701\u0702\3\2\2\2\u0702\u0703"+
		"\3\2\2\2\u0703\u0705\7M\2\2\u0704\u0706\5\u01ba\u00de\2\u0705\u0704\3"+
		"\2\2\2\u0705\u0706\3\2\2\2\u0706\u0707\3\2\2\2\u0707\u0709\7M\2\2\u0708"+
		"\u070a\5\u0154\u00ab\2\u0709\u0708\3\2\2\2\u0709\u070a\3\2\2\2\u070a\u070b"+
		"\3\2\2\2\u070b\u070c\7H\2\2\u070c\u070d\5\u0120\u0091\2\u070d\u014f\3"+
		"\2\2\2\u070e\u070f\7#\2\2\u070f\u0711\7G\2\2\u0710\u0712\5\u0152\u00aa"+
		"\2\u0711\u0710\3\2\2\2\u0711\u0712\3\2\2\2\u0712\u0713\3\2\2\2\u0713\u0715"+
		"\7M\2\2\u0714\u0716\5\u01ba\u00de\2\u0715\u0714\3\2\2\2\u0715\u0716\3"+
		"\2\2\2\u0716\u0717\3\2\2\2\u0717\u0719\7M\2\2\u0718\u071a\5\u0154\u00ab"+
		"\2\u0719\u0718\3\2\2\2\u0719\u071a\3\2\2\2\u071a\u071b\3\2\2\2\u071b\u071c"+
		"\7H\2\2\u071c\u071d\5\u0122\u0092\2\u071d\u0151\3\2\2\2\u071e\u0721\5"+
		"\u0156\u00ac\2\u071f\u0721\5\u011e\u0090\2\u0720\u071e\3\2\2\2\u0720\u071f"+
		"\3\2\2\2\u0721\u0153\3\2\2\2\u0722\u0723\5\u0156\u00ac\2\u0723\u0155\3"+
		"\2\2\2\u0724\u0729\5\u012e\u0098\2\u0725\u0726\7N\2\2\u0726\u0728\5\u012e"+
		"\u0098\2\u0727\u0725\3\2\2\2\u0728\u072b\3\2\2\2\u0729\u0727\3\2\2\2\u0729"+
		"\u072a\3\2\2\2\u072a\u0157\3\2\2\2\u072b\u0729\3\2\2\2\u072c\u072d\7#"+
		"\2\2\u072d\u0731\7G\2\2\u072e\u0730\5\u00aeX\2\u072f\u072e\3\2\2\2\u0730"+
		"\u0733\3\2\2\2\u0731\u072f\3\2\2\2\u0731\u0732\3\2\2\2\u0732\u0734\3\2"+
		"\2\2\u0733\u0731\3\2\2\2\u0734\u0735\5\u0084C\2\u0735\u0736\5\u0080A\2"+
		"\u0736\u0737\7V\2\2\u0737\u0738\5\u01ba\u00de\2\u0738\u0739\7H\2\2\u0739"+
		"\u073a\5\u0120\u0091\2\u073a\u0159\3\2\2\2\u073b\u073c\7#\2\2\u073c\u0740"+
		"\7G\2\2\u073d\u073f\5\u00aeX\2\u073e\u073d\3\2\2\2\u073f\u0742\3\2\2\2"+
		"\u0740\u073e\3\2\2\2\u0740\u0741\3\2\2\2\u0741\u0743\3\2\2\2\u0742\u0740"+
		"\3\2\2\2\u0743\u0744\5\u0084C\2\u0744\u0745\5\u0080A\2\u0745\u0746\7V"+
		"\2\2\u0746\u0747\5\u01ba\u00de\2\u0747\u0748\7H\2\2\u0748\u0749\5\u0122"+
		"\u0092\2\u0749\u015b\3\2\2\2\u074a\u074c\7\22\2\2\u074b\u074d\5\2\2\2"+
		"\u074c\u074b\3\2\2\2\u074c\u074d\3\2\2\2\u074d\u074e\3\2\2\2\u074e\u074f"+
		"\7M\2\2\u074f\u015d\3\2\2\2\u0750\u0752\7\31\2\2\u0751\u0753\5\2\2\2\u0752"+
		"\u0751\3\2\2\2\u0752\u0753\3\2\2\2\u0753\u0754\3\2\2\2\u0754\u0755\7M"+
		"\2\2\u0755\u015f\3\2\2\2\u0756\u0758\7\62\2\2\u0757\u0759\5\u01ba\u00de"+
		"\2\u0758\u0757\3\2\2\2\u0758\u0759\3\2\2\2\u0759\u075a\3\2\2\2\u075a\u075b"+
		"\7M\2\2\u075b\u0161\3\2\2\2\u075c\u075d\7:\2\2\u075d\u075e\5\u01ba\u00de"+
		"\2\u075e\u075f\7M\2\2\u075f\u0163\3\2\2\2\u0760\u0761\78\2\2\u0761\u0762"+
		"\7G\2\2\u0762\u0763\5\u01ba\u00de\2\u0763\u0764\7H\2\2\u0764\u0765\5\u0116"+
		"\u008c\2\u0765\u0165\3\2\2\2\u0766\u0767\7=\2\2\u0767\u0768\5\u0116\u008c"+
		"\2\u0768\u0769\5\u0168\u00b5\2\u0769\u0773\3\2\2\2\u076a\u076b\7=\2\2"+
		"\u076b\u076d\5\u0116\u008c\2\u076c\u076e\5\u0168\u00b5\2\u076d\u076c\3"+
		"\2\2\2\u076d\u076e\3\2\2\2\u076e\u076f\3\2\2\2\u076f\u0770\5\u0170\u00b9"+
		"\2\u0770\u0773\3\2\2\2\u0771\u0773\5\u0172\u00ba\2\u0772\u0766\3\2\2\2"+
		"\u0772\u076a\3\2\2\2\u0772\u0771\3\2\2\2\u0773\u0167\3\2\2\2\u0774\u0778"+
		"\5\u016a\u00b6\2\u0775\u0777\5\u016a\u00b6\2\u0776\u0775\3\2\2\2\u0777"+
		"\u077a\3\2\2\2\u0778\u0776\3\2\2\2\u0778\u0779\3\2\2\2\u0779\u0169\3\2"+
		"\2\2\u077a\u0778\3\2\2\2\u077b\u077c\7\25\2\2\u077c\u077d\7G\2\2\u077d"+
		"\u077e\5\u016c\u00b7\2\u077e\u077f\7H\2\2\u077f\u0780\5\u0116\u008c\2"+
		"\u0780\u016b\3\2\2\2\u0781\u0783\5\u00aeX\2\u0782\u0781\3\2\2\2\u0783"+
		"\u0786\3\2\2\2\u0784\u0782\3\2\2\2\u0784\u0785\3\2\2\2\u0785\u0787\3\2"+
		"\2\2\u0786\u0784\3\2\2\2\u0787\u0788\5\u016e\u00b8\2\u0788\u0789\5\u0080"+
		"A\2\u0789\u016d\3\2\2\2\u078a\u078f\5\u008cG\2\u078b\u078c\7d\2\2\u078c"+
		"\u078e\5\24\13\2\u078d\u078b\3\2\2\2\u078e\u0791\3\2\2\2\u078f\u078d\3"+
		"\2\2\2\u078f\u0790\3\2\2\2\u0790\u016f\3\2\2\2\u0791\u078f\3\2\2\2\u0792"+
		"\u0793\7!\2\2\u0793\u0794\5\u0116\u008c\2\u0794\u0171\3\2\2\2\u0795\u0796"+
		"\7=\2\2\u0796\u0797\5\u0174\u00bb\2\u0797\u0799\5\u0116\u008c\2\u0798"+
		"\u079a\5\u0168\u00b5\2\u0799\u0798\3\2\2\2\u0799\u079a\3\2\2\2\u079a\u079c"+
		"\3\2\2\2\u079b\u079d\5\u0170\u00b9\2\u079c\u079b\3\2\2\2\u079c\u079d\3"+
		"\2\2\2\u079d\u0173\3\2\2\2\u079e\u079f\7G\2\2\u079f\u07a1\5\u0176\u00bc"+
		"\2\u07a0\u07a2\7M\2\2\u07a1\u07a0\3\2\2\2\u07a1\u07a2\3\2\2\2\u07a2\u07a3"+
		"\3\2\2\2\u07a3\u07a4\7H\2\2\u07a4\u0175\3\2\2\2\u07a5\u07aa\5\u0178\u00bd"+
		"\2\u07a6\u07a7\7M\2\2\u07a7\u07a9\5\u0178\u00bd\2\u07a8\u07a6\3\2\2\2"+
		"\u07a9\u07ac\3\2\2\2\u07aa\u07a8\3\2\2\2\u07aa\u07ab\3\2\2\2\u07ab\u0177"+
		"\3\2\2\2\u07ac\u07aa\3\2\2\2\u07ad\u07af\5\u00aeX\2\u07ae\u07ad\3\2\2"+
		"\2\u07af\u07b2\3\2\2\2\u07b0\u07ae\3\2\2\2\u07b0\u07b1\3\2\2\2\u07b1\u07b3"+
		"\3\2\2\2\u07b2\u07b0\3\2\2\2\u07b3\u07b4\5\u0084C\2\u07b4\u07b5\5\u0080"+
		"A\2\u07b5\u07b6\7P\2\2\u07b6\u07b7\5\u01ba\u00de\2\u07b7\u07ba\3\2\2\2"+
		"\u07b8\u07ba\5\u017a\u00be\2\u07b9\u07b0\3\2\2\2\u07b9\u07b8\3\2\2\2\u07ba"+
		"\u0179\3\2\2\2\u07bb\u07be\5> \2\u07bc\u07be\5\u0198\u00cd\2\u07bd\u07bb"+
		"\3\2\2\2\u07bd\u07bc\3\2\2\2\u07be\u017b\3\2\2\2\u07bf\u07c2\5\u018a\u00c6"+
		"\2\u07c0\u07c2\5\u01b2\u00da\2\u07c1\u07bf\3\2\2\2\u07c1\u07c0\3\2\2\2"+
		"\u07c2\u07c6\3\2\2\2\u07c3\u07c5\5\u0184\u00c3\2\u07c4\u07c3\3\2\2\2\u07c5"+
		"\u07c8\3\2\2\2\u07c6\u07c4\3\2\2\2\u07c6\u07c7\3\2\2\2\u07c7\u017d\3\2"+
		"\2\2\u07c8\u07c6\3\2\2\2\u07c9\u07e6\5\4\3\2\u07ca\u07ce\5:\36\2\u07cb"+
		"\u07cd\5\u01d2\u00ea\2\u07cc\u07cb\3\2\2\2\u07cd\u07d0\3\2\2\2\u07ce\u07cc"+
		"\3\2\2\2\u07ce\u07cf\3\2\2\2\u07cf\u07d1\3\2\2\2\u07d0\u07ce\3\2\2\2\u07d1"+
		"\u07d2\7O\2\2\u07d2\u07d3\7\27\2\2\u07d3\u07e6\3\2\2\2\u07d4\u07d5\7>"+
		"\2\2\u07d5\u07d6\7O\2\2\u07d6\u07e6\7\27\2\2\u07d7\u07e6\79\2\2\u07d8"+
		"\u07d9\5:\36\2\u07d9\u07da\7O\2\2\u07da\u07db\79\2\2\u07db\u07e6\3\2\2"+
		"\2\u07dc\u07dd\7G\2\2\u07dd\u07de\5\u01ba\u00de\2\u07de\u07df\7H\2\2\u07df"+
		"\u07e6\3\2\2\2\u07e0\u07e6\5\u0190\u00c9\2\u07e1\u07e6\5\u0198\u00cd\2"+
		"\u07e2\u07e6\5\u019e\u00d0\2\u07e3\u07e6\5\u01a4\u00d3\2\u07e4\u07e6\5"+
		"\u01ac\u00d7\2\u07e5\u07c9\3\2\2\2\u07e5\u07ca\3\2\2\2\u07e5\u07d4\3\2"+
		"\2\2\u07e5\u07d7\3\2\2\2\u07e5\u07d8\3\2\2\2\u07e5\u07dc\3\2\2\2\u07e5"+
		"\u07e0\3\2\2\2\u07e5\u07e1\3\2\2\2\u07e5\u07e2\3\2\2\2\u07e5\u07e3\3\2"+
		"\2\2\u07e5\u07e4\3\2\2\2\u07e6\u017f\3\2\2\2\u07e7\u07e8\3\2\2\2\u07e8"+
		"\u0181\3\2\2\2\u07e9\u0805\5\4\3\2\u07ea\u07ee\5:\36\2\u07eb\u07ed\5\u01d2"+
		"\u00ea\2\u07ec\u07eb\3\2\2\2\u07ed\u07f0\3\2\2\2\u07ee\u07ec\3\2\2\2\u07ee"+
		"\u07ef\3\2\2\2\u07ef\u07f1\3\2\2\2\u07f0\u07ee\3\2\2\2\u07f1\u07f2\7O"+
		"\2\2\u07f2\u07f3\7\27\2\2\u07f3\u0805\3\2\2\2\u07f4\u07f5\7>\2\2\u07f5"+
		"\u07f6\7O\2\2\u07f6\u0805\7\27\2\2\u07f7\u0805\79\2\2\u07f8\u07f9\5:\36"+
		"\2\u07f9\u07fa\7O\2\2\u07fa\u07fb\79\2\2\u07fb\u0805\3\2\2\2\u07fc\u07fd"+
		"\7G\2\2\u07fd\u07fe\5\u01ba\u00de\2\u07fe\u07ff\7H\2\2\u07ff\u0805\3\2"+
		"\2\2\u0800\u0805\5\u0190\u00c9\2\u0801\u0805\5\u0198\u00cd\2\u0802\u0805"+
		"\5\u01a4\u00d3\2\u0803\u0805\5\u01ac\u00d7\2\u0804\u07e9\3\2\2\2\u0804"+
		"\u07ea\3\2\2\2\u0804\u07f4\3\2\2\2\u0804\u07f7\3\2\2\2\u0804\u07f8\3\2"+
		"\2\2\u0804\u07fc\3\2\2\2\u0804\u0800\3\2\2\2\u0804\u0801\3\2\2\2\u0804"+
		"\u0802\3\2\2\2\u0804\u0803\3\2\2\2\u0805\u0183\3\2\2\2\u0806\u080c\5\u0192"+
		"\u00ca\2\u0807\u080c\5\u019a\u00ce\2\u0808\u080c\5\u01a0\u00d1\2\u0809"+
		"\u080c\5\u01a6\u00d4\2\u080a\u080c\5\u01ae\u00d8\2\u080b\u0806\3\2\2\2"+
		"\u080b\u0807\3\2\2\2\u080b\u0808\3\2\2\2\u080b\u0809\3\2\2\2\u080b\u080a"+
		"\3\2\2\2\u080c\u0185\3\2\2\2\u080d\u080e\3\2\2\2\u080e\u0187\3\2\2\2\u080f"+
		"\u0814\5\u0192\u00ca\2\u0810\u0814\5\u019a\u00ce\2\u0811\u0814\5\u01a6"+
		"\u00d4\2\u0812\u0814\5\u01ae\u00d8\2\u0813\u080f\3\2\2\2\u0813\u0810\3"+
		"\2\2\2\u0813\u0811\3\2\2\2\u0813\u0812\3\2\2\2\u0814\u0189\3\2\2\2\u0815"+
		"\u083c\5\4\3\2\u0816\u081a\5:\36\2\u0817\u0819\5\u01d2\u00ea\2\u0818\u0817"+
		"\3\2\2\2\u0819\u081c\3\2\2\2\u081a\u0818\3\2\2\2\u081a\u081b\3\2\2\2\u081b"+
		"\u081d\3\2\2\2\u081c\u081a\3\2\2\2\u081d\u081e\7O\2\2\u081e\u081f\7\27"+
		"\2\2\u081f\u083c\3\2\2\2\u0820\u0824\5\u0086D\2\u0821\u0823\5\u01d2\u00ea"+
		"\2\u0822\u0821\3\2\2\2\u0823\u0826\3\2\2\2\u0824\u0822\3\2\2\2\u0824\u0825"+
		"\3\2\2\2\u0825\u0827\3\2\2\2\u0826\u0824\3\2\2\2\u0827\u0828\7O\2\2\u0828"+
		"\u0829\7\27\2\2\u0829\u083c\3\2\2\2\u082a\u082b\7>\2\2\u082b\u082c\7O"+
		"\2\2\u082c\u083c\7\27\2\2\u082d\u083c\79\2\2\u082e\u082f\5:\36\2\u082f"+
		"\u0830\7O\2\2\u0830\u0831\79\2\2\u0831\u083c\3\2\2\2\u0832\u0833\7G\2"+
		"\2\u0833\u0834\5\u01ba\u00de\2\u0834\u0835\7H\2\2\u0835\u083c\3\2\2\2"+
		"\u0836\u083c\5\u0194\u00cb\2\u0837\u083c\5\u019c\u00cf\2\u0838\u083c\5"+
		"\u01a2\u00d2\2\u0839\u083c\5\u01a8\u00d5\2\u083a\u083c\5\u01b0\u00d9\2"+
		"\u083b\u0815\3\2\2\2\u083b\u0816\3\2\2\2\u083b\u0820\3\2\2\2\u083b\u082a"+
		"\3\2\2\2\u083b\u082d\3\2\2\2\u083b\u082e\3\2\2\2\u083b\u0832\3\2\2\2\u083b"+
		"\u0836\3\2\2\2\u083b\u0837\3\2\2\2\u083b\u0838\3\2\2\2\u083b\u0839\3\2"+
		"\2\2\u083b\u083a\3\2\2\2\u083c\u018b\3\2\2\2\u083d\u083e\3\2\2\2\u083e"+
		"\u018d\3\2\2\2\u083f\u0865\5\4\3\2\u0840\u0844\5:\36\2\u0841\u0843\5\u01d2"+
		"\u00ea\2\u0842\u0841\3\2\2\2\u0843\u0846\3\2\2\2\u0844\u0842\3\2\2\2\u0844"+
		"\u0845\3\2\2\2\u0845\u0847\3\2\2\2\u0846\u0844\3\2\2\2\u0847\u0848\7O"+
		"\2\2\u0848\u0849\7\27\2\2\u0849\u0865\3\2\2\2\u084a\u084e\5\u0086D\2\u084b"+
		"\u084d\5\u01d2\u00ea\2\u084c\u084b\3\2\2\2\u084d\u0850\3\2\2\2\u084e\u084c"+
		"\3\2\2\2\u084e\u084f\3\2\2\2\u084f\u0851\3\2\2\2\u0850\u084e\3\2\2\2\u0851"+
		"\u0852\7O\2\2\u0852\u0853\7\27\2\2\u0853\u0865\3\2\2\2\u0854\u0855\7>"+
		"\2\2\u0855\u0856\7O\2\2\u0856\u0865\7\27\2\2\u0857\u0865\79\2\2\u0858"+
		"\u0859\5:\36\2\u0859\u085a\7O\2\2\u085a\u085b\79\2\2\u085b\u0865\3\2\2"+
		"\2\u085c\u085d\7G\2\2\u085d\u085e\5\u01ba\u00de\2\u085e\u085f\7H\2\2\u085f"+
		"\u0865\3\2\2\2\u0860\u0865\5\u0194\u00cb\2\u0861\u0865\5\u019c\u00cf\2"+
		"\u0862\u0865\5\u01a8\u00d5\2\u0863\u0865\5\u01b0\u00d9\2\u0864\u083f\3"+
		"\2\2\2\u0864\u0840\3\2\2\2\u0864\u084a\3\2\2\2\u0864\u0854\3\2\2\2\u0864"+
		"\u0857\3\2\2\2\u0864\u0858\3\2\2\2\u0864\u085c\3\2\2\2\u0864\u0860\3\2"+
		"\2\2\u0864\u0861\3\2\2\2\u0864\u0862\3\2\2\2\u0864\u0863\3\2\2\2\u0865"+
		"\u018f\3\2\2\2\u0866\u0868\7-\2\2\u0867\u0869\5.\30\2\u0868\u0867\3\2"+
		"\2\2\u0868\u0869\3\2\2\2\u0869\u086a\3\2\2\2\u086a\u086f\5\u00fe\u0080"+
		"\2\u086b\u086c\7O\2\2\u086c\u086e\5\u00fe\u0080\2\u086d\u086b\3\2\2\2"+
		"\u086e\u0871\3\2\2\2\u086f\u086d\3\2\2\2\u086f\u0870\3\2\2\2\u0870\u0873"+
		"\3\2\2\2\u0871\u086f\3\2\2\2\u0872\u0874\5\u0196\u00cc\2\u0873\u0872\3"+
		"\2\2\2\u0873\u0874\3\2\2\2\u0874\u0875\3\2\2\2\u0875\u0877\7G\2\2\u0876"+
		"\u0878\5\u01aa\u00d6\2\u0877\u0876\3\2\2\2\u0877\u0878\3\2\2\2\u0878\u0879"+
		"\3\2\2\2\u0879\u087b\7H\2\2\u087a\u087c\5p9\2\u087b\u087a\3\2\2\2\u087b"+
		"\u087c\3\2\2\2\u087c\u08a2\3\2\2\2\u087d\u087e\5> \2\u087e\u087f\7O\2"+
		"\2\u087f\u0881\7-\2\2\u0880\u0882\5.\30\2\u0881\u0880\3\2\2\2\u0881\u0882"+
		"\3\2\2\2\u0882\u0883\3\2\2\2\u0883\u0885\5\u00fe\u0080\2\u0884\u0886\5"+
		"\u0196\u00cc\2\u0885\u0884\3\2\2\2\u0885\u0886\3\2\2\2\u0886\u0887\3\2"+
		"\2\2\u0887\u0889\7G\2\2\u0888\u088a\5\u01aa\u00d6\2\u0889\u0888\3\2\2"+
		"\2\u0889\u088a\3\2\2\2\u088a\u088b\3\2\2\2\u088b\u088d\7H\2\2\u088c\u088e"+
		"\5p9\2\u088d\u088c\3\2\2\2\u088d\u088e\3\2\2\2\u088e\u08a2\3\2\2\2\u088f"+
		"\u0890\5\u017c\u00bf\2\u0890\u0891\7O\2\2\u0891\u0893\7-\2\2\u0892\u0894"+
		"\5.\30\2\u0893\u0892\3\2\2\2\u0893\u0894\3\2\2\2\u0894\u0895\3\2\2\2\u0895"+
		"\u0897\5\u00fe\u0080\2\u0896\u0898\5\u0196\u00cc\2\u0897\u0896\3\2\2\2"+
		"\u0897\u0898\3\2\2\2\u0898\u0899\3\2\2\2\u0899\u089b\7G\2\2\u089a\u089c"+
		"\5\u01aa\u00d6\2\u089b\u089a\3\2\2\2\u089b\u089c\3\2\2\2\u089c\u089d\3"+
		"\2\2\2\u089d\u089f\7H\2\2\u089e\u08a0\5p9\2\u089f\u089e\3\2\2\2\u089f"+
		"\u08a0\3\2\2\2\u08a0\u08a2\3\2\2\2\u08a1\u0866\3\2\2\2\u08a1\u087d\3\2"+
		"\2\2\u08a1\u088f\3\2\2\2\u08a2\u0191\3\2\2\2\u08a3\u08a4\7O\2\2\u08a4"+
		"\u08a6\7-\2\2\u08a5\u08a7\5.\30\2\u08a6\u08a5\3\2\2\2\u08a6\u08a7\3\2"+
		"\2\2\u08a7\u08a8\3\2\2\2\u08a8\u08aa\5\u00fe\u0080\2\u08a9\u08ab\5\u0196"+
		"\u00cc\2\u08aa\u08a9\3\2\2\2\u08aa\u08ab\3\2\2\2\u08ab\u08ac\3\2\2\2\u08ac"+
		"\u08ae\7G\2\2\u08ad\u08af\5\u01aa\u00d6\2\u08ae\u08ad\3\2\2\2\u08ae\u08af"+
		"\3\2\2\2\u08af\u08b0\3\2\2\2\u08b0\u08b2\7H\2\2\u08b1\u08b3\5p9\2\u08b2"+
		"\u08b1\3\2\2\2\u08b2\u08b3\3\2\2\2\u08b3\u0193\3\2\2\2\u08b4\u08b6\7-"+
		"\2\2\u08b5\u08b7\5.\30\2\u08b6\u08b5\3\2\2\2\u08b6\u08b7\3\2\2\2\u08b7"+
		"\u08b8\3\2\2\2\u08b8\u08bd\5\u00fe\u0080\2\u08b9\u08ba\7O\2\2\u08ba\u08bc"+
		"\5\u00fe\u0080\2\u08bb\u08b9\3\2\2\2\u08bc\u08bf\3\2\2\2\u08bd\u08bb\3"+
		"\2\2\2\u08bd\u08be\3\2\2\2\u08be\u08c1\3\2\2\2\u08bf\u08bd\3\2\2\2\u08c0"+
		"\u08c2\5\u0196\u00cc\2\u08c1\u08c0\3\2\2\2\u08c1\u08c2\3\2\2\2\u08c2\u08c3"+
		"\3\2\2\2\u08c3\u08c5\7G\2\2\u08c4\u08c6\5\u01aa\u00d6\2\u08c5\u08c4\3"+
		"\2\2\2\u08c5\u08c6\3\2\2\2\u08c6\u08c7\3\2\2\2\u08c7\u08c9\7H\2\2\u08c8"+
		"\u08ca\5p9\2\u08c9\u08c8\3\2\2\2\u08c9\u08ca\3\2\2\2\u08ca\u08de\3\2\2"+
		"\2\u08cb\u08cc\5> \2\u08cc\u08cd\7O\2\2\u08cd\u08cf\7-\2\2\u08ce\u08d0"+
		"\5.\30\2\u08cf\u08ce\3\2\2\2\u08cf\u08d0\3\2\2\2\u08d0\u08d1\3\2\2\2\u08d1"+
		"\u08d3\5\u00fe\u0080\2\u08d2\u08d4\5\u0196\u00cc\2\u08d3\u08d2\3\2\2\2"+
		"\u08d3\u08d4\3\2\2\2\u08d4\u08d5\3\2\2\2\u08d5\u08d7\7G\2\2\u08d6\u08d8"+
		"\5\u01aa\u00d6\2\u08d7\u08d6\3\2\2\2\u08d7\u08d8\3\2\2\2\u08d8\u08d9\3"+
		"\2\2\2\u08d9\u08db\7H\2\2\u08da\u08dc\5p9\2\u08db\u08da\3\2\2\2\u08db"+
		"\u08dc\3\2\2\2\u08dc\u08de\3\2\2\2\u08dd\u08b4\3\2\2\2\u08dd\u08cb\3\2"+
		"\2\2\u08de\u0195\3\2\2\2\u08df\u08e2\5.\30\2\u08e0\u08e2\7\r\2\2\u08e1"+
		"\u08df\3\2\2\2\u08e1\u08e0\3\2\2\2\u08e2\u0197\3\2\2\2\u08e3\u08e4\5\u017c"+
		"\u00bf\2\u08e4\u08e5\7O\2\2\u08e5\u08e6\5\2\2\2\u08e6\u08f1\3\2\2\2\u08e7"+
		"\u08e8\7\66\2\2\u08e8\u08e9\7O\2\2\u08e9\u08f1\5\2\2\2\u08ea\u08eb\5:"+
		"\36\2\u08eb\u08ec\7O\2\2\u08ec\u08ed\7\66\2\2\u08ed\u08ee\7O\2\2\u08ee"+
		"\u08ef\5\2\2\2\u08ef\u08f1\3\2\2\2\u08f0\u08e3\3\2\2\2\u08f0\u08e7\3\2"+
		"\2\2\u08f0\u08ea\3\2\2\2\u08f1\u0199\3\2\2\2\u08f2\u08f3\7O\2\2\u08f3"+
		"\u08f4\5\2\2\2\u08f4\u019b\3\2\2\2\u08f5\u08f6\7\66\2\2\u08f6\u08f7\7"+
		"O\2\2\u08f7\u08ff\5\2\2\2\u08f8\u08f9\5:\36\2\u08f9\u08fa\7O\2\2\u08fa"+
		"\u08fb\7\66\2\2\u08fb\u08fc\7O\2\2\u08fc\u08fd\5\2\2\2\u08fd\u08ff\3\2"+
		"\2\2\u08fe\u08f5\3\2\2\2\u08fe\u08f8\3\2\2\2\u08ff\u019d\3\2\2\2\u0900"+
		"\u0901\5> \2\u0901\u0902\7K\2\2\u0902\u0903\5\u01ba\u00de\2\u0903\u0904"+
		"\7L\2\2\u0904\u090b\3\2\2\2\u0905\u0906\5\u0182\u00c2\2\u0906\u0907\7"+
		"K\2\2\u0907\u0908\5\u01ba\u00de\2\u0908\u0909\7L\2\2\u0909\u090b\3\2\2"+
		"\2\u090a\u0900\3\2\2\2\u090a\u0905\3\2\2\2\u090b\u0913\3\2\2\2\u090c\u090d"+
		"\5\u0180\u00c1\2\u090d\u090e\7K\2\2\u090e\u090f\5\u01ba\u00de\2\u090f"+
		"\u0910\7L\2\2\u0910\u0912\3\2\2\2\u0911\u090c\3\2\2\2\u0912\u0915\3\2"+
		"\2\2\u0913\u0911\3\2\2\2\u0913\u0914\3\2\2\2\u0914\u019f\3\2\2\2\u0915"+
		"\u0913\3\2\2\2\u0916\u0917\5\u0188\u00c5\2\u0917\u0918\7K\2\2\u0918\u0919"+
		"\5\u01ba\u00de\2\u0919\u091a\7L\2\2\u091a\u0922\3\2\2\2\u091b\u091c\5"+
		"\u0186\u00c4\2\u091c\u091d\7K\2\2\u091d\u091e\5\u01ba\u00de\2\u091e\u091f"+
		"\7L\2\2\u091f\u0921\3\2\2\2\u0920\u091b\3\2\2\2\u0921\u0924\3\2\2\2\u0922"+
		"\u0920\3\2\2\2\u0922\u0923\3\2\2\2\u0923\u01a1\3\2\2\2\u0924\u0922\3\2"+
		"\2\2\u0925\u0926\5> \2\u0926\u0927\7K\2\2\u0927\u0928\5\u01ba\u00de\2"+
		"\u0928\u0929\7L\2\2\u0929\u0930\3\2\2\2\u092a\u092b\5\u018e\u00c8\2\u092b"+
		"\u092c\7K\2\2\u092c\u092d\5\u01ba\u00de\2\u092d\u092e\7L\2\2\u092e\u0930"+
		"\3\2\2\2\u092f\u0925\3\2\2\2\u092f\u092a\3\2\2\2\u0930\u0938\3\2\2\2\u0931"+
		"\u0932\5\u018c\u00c7\2\u0932\u0933\7K\2\2\u0933\u0934\5\u01ba\u00de\2"+
		"\u0934\u0935\7L\2\2\u0935\u0937\3\2\2\2\u0936\u0931\3\2\2\2\u0937\u093a"+
		"\3\2\2\2\u0938\u0936\3\2\2\2\u0938\u0939\3\2\2\2\u0939\u01a3\3\2\2\2\u093a"+
		"\u0938\3\2\2\2\u093b\u093c\5@!\2\u093c\u093e\7G\2";
	private static final String _serializedATNSegment1 =
		"\2\u093d\u093f\5\u01aa\u00d6\2\u093e\u093d\3\2\2\2\u093e\u093f\3\2\2\2"+
		"\u093f\u0940\3\2\2\2\u0940\u0941\7H\2\2\u0941\u0981\3\2\2\2\u0942\u0943"+
		"\5:\36\2\u0943\u0945\7O\2\2\u0944\u0946\5.\30\2\u0945\u0944\3\2\2\2\u0945"+
		"\u0946\3\2\2\2\u0946\u0947\3\2\2\2\u0947\u0948\5\2\2\2\u0948\u094a\7G"+
		"\2\2\u0949\u094b\5\u01aa\u00d6\2\u094a\u0949\3\2\2\2\u094a\u094b\3\2\2"+
		"\2\u094b\u094c\3\2\2\2\u094c\u094d\7H\2\2\u094d\u0981\3\2\2\2\u094e\u094f"+
		"\5> \2\u094f\u0951\7O\2\2\u0950\u0952\5.\30\2\u0951\u0950\3\2\2\2\u0951"+
		"\u0952\3\2\2\2\u0952\u0953\3\2\2\2\u0953\u0954\5\2\2\2\u0954\u0956\7G"+
		"\2\2\u0955\u0957\5\u01aa\u00d6\2\u0956\u0955\3\2\2\2\u0956\u0957\3\2\2"+
		"\2\u0957\u0958\3\2\2\2\u0958\u0959\7H\2\2\u0959\u0981\3\2\2\2\u095a\u095b"+
		"\5\u017c\u00bf\2\u095b\u095d\7O\2\2\u095c\u095e\5.\30\2\u095d\u095c\3"+
		"\2\2\2\u095d\u095e\3\2\2\2\u095e\u095f\3\2\2\2\u095f\u0960\5\2\2\2\u0960"+
		"\u0962\7G\2\2\u0961\u0963\5\u01aa\u00d6\2\u0962\u0961\3\2\2\2\u0962\u0963"+
		"\3\2\2\2\u0963\u0964\3\2\2\2\u0964\u0965\7H\2\2\u0965\u0981\3\2\2\2\u0966"+
		"\u0967\7\66\2\2\u0967\u0969\7O\2\2\u0968\u096a\5.\30\2\u0969\u0968\3\2"+
		"\2\2\u0969\u096a\3\2\2\2\u096a\u096b\3\2\2\2\u096b\u096c\5\2\2\2\u096c"+
		"\u096e\7G\2\2\u096d\u096f\5\u01aa\u00d6\2\u096e\u096d\3\2\2\2\u096e\u096f"+
		"\3\2\2\2\u096f\u0970\3\2\2\2\u0970\u0971\7H\2\2\u0971\u0981\3\2\2\2\u0972"+
		"\u0973\5:\36\2\u0973\u0974\7O\2\2\u0974\u0975\7\66\2\2\u0975\u0977\7O"+
		"\2\2\u0976\u0978\5.\30\2\u0977\u0976\3\2\2\2\u0977\u0978\3\2\2\2\u0978"+
		"\u0979\3\2\2\2\u0979\u097a\5\2\2\2\u097a\u097c\7G\2\2\u097b\u097d\5\u01aa"+
		"\u00d6\2\u097c\u097b\3\2\2\2\u097c\u097d\3\2\2\2\u097d\u097e\3\2\2\2\u097e"+
		"\u097f\7H\2\2\u097f\u0981\3\2\2\2\u0980\u093b\3\2\2\2\u0980\u0942\3\2"+
		"\2\2\u0980\u094e\3\2\2\2\u0980\u095a\3\2\2\2\u0980\u0966\3\2\2\2\u0980"+
		"\u0972\3\2\2\2\u0981\u01a5\3\2\2\2\u0982\u0984\7O\2\2\u0983\u0985\5.\30"+
		"\2\u0984\u0983\3\2\2\2\u0984\u0985\3\2\2\2\u0985\u0986\3\2\2\2\u0986\u0987"+
		"\5\2\2\2\u0987\u0989\7G\2\2\u0988\u098a\5\u01aa\u00d6\2\u0989\u0988\3"+
		"\2\2\2\u0989\u098a\3\2\2\2\u098a\u098b\3\2\2\2\u098b\u098c\7H\2\2\u098c"+
		"\u01a7\3\2\2\2\u098d\u098e\5@!\2\u098e\u0990\7G\2\2\u098f\u0991\5\u01aa"+
		"\u00d6\2\u0990\u098f\3\2\2\2\u0990\u0991\3\2\2\2\u0991\u0992\3\2\2\2\u0992"+
		"\u0993\7H\2\2\u0993\u09c7\3\2\2\2\u0994\u0995\5:\36\2\u0995\u0997\7O\2"+
		"\2\u0996\u0998\5.\30\2\u0997\u0996\3\2\2\2\u0997\u0998\3\2\2\2\u0998\u0999"+
		"\3\2\2\2\u0999\u099a\5\2\2\2\u099a\u099c\7G\2\2\u099b\u099d\5\u01aa\u00d6"+
		"\2\u099c\u099b\3\2\2\2\u099c\u099d\3\2\2\2\u099d\u099e\3\2\2\2\u099e\u099f"+
		"\7H\2\2\u099f\u09c7\3\2\2\2\u09a0\u09a1\5> \2\u09a1\u09a3\7O\2\2\u09a2"+
		"\u09a4\5.\30\2\u09a3\u09a2\3\2\2\2\u09a3\u09a4\3\2\2\2\u09a4\u09a5\3\2"+
		"\2\2\u09a5\u09a6\5\2\2\2\u09a6\u09a8\7G\2\2\u09a7\u09a9\5\u01aa\u00d6"+
		"\2\u09a8\u09a7\3\2\2\2\u09a8\u09a9\3\2\2\2\u09a9\u09aa\3\2\2\2\u09aa\u09ab"+
		"\7H\2\2\u09ab\u09c7\3\2\2\2\u09ac\u09ad\7\66\2\2\u09ad\u09af\7O\2\2\u09ae"+
		"\u09b0\5.\30\2\u09af\u09ae\3\2\2\2\u09af\u09b0\3\2\2\2\u09b0\u09b1\3\2"+
		"\2\2\u09b1\u09b2\5\2\2\2\u09b2\u09b4\7G\2\2\u09b3\u09b5\5\u01aa\u00d6"+
		"\2\u09b4\u09b3\3\2\2\2\u09b4\u09b5\3\2\2\2\u09b5\u09b6\3\2\2\2\u09b6\u09b7"+
		"\7H\2\2\u09b7\u09c7\3\2\2\2\u09b8\u09b9\5:\36\2\u09b9\u09ba\7O\2\2\u09ba"+
		"\u09bb\7\66\2\2\u09bb\u09bd\7O\2\2\u09bc\u09be\5.\30\2\u09bd\u09bc\3\2"+
		"\2\2\u09bd\u09be\3\2\2\2\u09be\u09bf\3\2\2\2\u09bf\u09c0\5\2\2\2\u09c0"+
		"\u09c2\7G\2\2\u09c1\u09c3\5\u01aa\u00d6\2\u09c2\u09c1\3\2\2\2\u09c2\u09c3"+
		"\3\2\2\2\u09c3\u09c4\3\2\2\2\u09c4\u09c5\7H\2\2\u09c5\u09c7\3\2\2\2\u09c6"+
		"\u098d\3\2\2\2\u09c6\u0994\3\2\2\2\u09c6\u09a0\3\2\2\2\u09c6\u09ac\3\2"+
		"\2\2\u09c6\u09b8\3\2\2\2\u09c7\u01a9\3\2\2\2\u09c8\u09cd\5\u01ba\u00de"+
		"\2\u09c9\u09ca\7N\2\2\u09ca\u09cc\5\u01ba\u00de\2\u09cb\u09c9\3\2\2\2"+
		"\u09cc\u09cf\3\2\2\2\u09cd\u09cb\3\2\2\2\u09cd\u09ce\3\2\2\2\u09ce\u01ab"+
		"\3\2\2\2\u09cf\u09cd\3\2\2\2\u09d0\u09d1\5> \2\u09d1\u09d3\7h\2\2\u09d2"+
		"\u09d4\5.\30\2\u09d3\u09d2\3\2\2\2\u09d3\u09d4\3\2\2\2\u09d4\u09d5\3\2"+
		"\2\2\u09d5\u09d6\5\2\2\2\u09d6\u0a00\3\2\2\2\u09d7\u09d8\5\20\t\2\u09d8"+
		"\u09da\7h\2\2\u09d9\u09db\5.\30\2\u09da\u09d9\3\2\2\2\u09da\u09db\3\2"+
		"\2\2\u09db\u09dc\3\2\2\2\u09dc\u09dd\5\2\2\2\u09dd\u0a00\3\2\2\2\u09de"+
		"\u09df\5\u017c\u00bf\2\u09df\u09e1\7h\2\2\u09e0\u09e2\5.\30\2\u09e1\u09e0"+
		"\3\2\2\2\u09e1\u09e2\3\2\2\2\u09e2\u09e3\3\2\2\2\u09e3\u09e4\5\2\2\2\u09e4"+
		"\u0a00\3\2\2\2\u09e5\u09e6\7\66\2\2\u09e6\u09e8\7h\2\2\u09e7\u09e9\5."+
		"\30\2\u09e8\u09e7\3\2\2\2\u09e8\u09e9\3\2\2\2\u09e9\u09ea\3\2\2\2\u09ea"+
		"\u0a00\5\2\2\2\u09eb\u09ec\5:\36\2\u09ec\u09ed\7O\2\2\u09ed\u09ee\7\66"+
		"\2\2\u09ee\u09f0\7h\2\2\u09ef\u09f1\5.\30\2\u09f0\u09ef\3\2\2\2\u09f0"+
		"\u09f1\3\2\2\2\u09f1\u09f2\3\2\2\2\u09f2\u09f3\5\2\2\2\u09f3\u0a00\3\2"+
		"\2\2\u09f4\u09f5\5\24\13\2\u09f5\u09f7\7h\2\2\u09f6\u09f8\5.\30\2\u09f7"+
		"\u09f6\3\2\2\2\u09f7\u09f8\3\2\2\2\u09f8\u09f9\3\2\2\2\u09f9\u09fa\7-"+
		"\2\2\u09fa\u0a00\3\2\2\2\u09fb\u09fc\5\"\22\2\u09fc\u09fd\7h\2\2\u09fd"+
		"\u09fe\7-\2\2\u09fe\u0a00\3\2\2\2\u09ff\u09d0\3\2\2\2\u09ff\u09d7\3\2"+
		"\2\2\u09ff\u09de\3\2\2\2\u09ff\u09e5\3\2\2\2\u09ff\u09eb\3\2\2\2\u09ff"+
		"\u09f4\3\2\2\2\u09ff\u09fb\3\2\2\2\u0a00\u01ad\3\2\2\2\u0a01\u0a03\7h"+
		"\2\2\u0a02\u0a04\5.\30\2\u0a03\u0a02\3\2\2\2\u0a03\u0a04\3\2\2\2\u0a04"+
		"\u0a05\3\2\2\2\u0a05\u0a06\5\2\2\2\u0a06\u01af\3\2\2\2\u0a07\u0a08\5>"+
		" \2\u0a08\u0a0a\7h\2\2\u0a09\u0a0b\5.\30\2\u0a0a\u0a09\3\2\2\2\u0a0a\u0a0b"+
		"\3\2\2\2\u0a0b\u0a0c\3\2\2\2\u0a0c\u0a0d\5\2\2\2\u0a0d\u0a30\3\2\2\2\u0a0e"+
		"\u0a0f\5\20\t\2\u0a0f\u0a11\7h\2\2\u0a10\u0a12\5.\30\2\u0a11\u0a10\3\2"+
		"\2\2\u0a11\u0a12\3\2\2\2\u0a12\u0a13\3\2\2\2\u0a13\u0a14\5\2\2\2\u0a14"+
		"\u0a30\3\2\2\2\u0a15\u0a16\7\66\2\2\u0a16\u0a18\7h\2\2\u0a17\u0a19\5."+
		"\30\2\u0a18\u0a17\3\2\2\2\u0a18\u0a19\3\2\2\2\u0a19\u0a1a\3\2\2\2\u0a1a"+
		"\u0a30\5\2\2\2\u0a1b\u0a1c\5:\36\2\u0a1c\u0a1d\7O\2\2\u0a1d\u0a1e\7\66"+
		"\2\2\u0a1e\u0a20\7h\2\2\u0a1f\u0a21\5.\30\2\u0a20\u0a1f\3\2\2\2\u0a20"+
		"\u0a21\3\2\2\2\u0a21\u0a22\3\2\2\2\u0a22\u0a23\5\2\2\2\u0a23\u0a30\3\2"+
		"\2\2\u0a24\u0a25\5\24\13\2\u0a25\u0a27\7h\2\2\u0a26\u0a28\5.\30\2\u0a27"+
		"\u0a26\3\2\2\2\u0a27\u0a28\3\2\2\2\u0a28\u0a29\3\2\2\2\u0a29\u0a2a\7-"+
		"\2\2\u0a2a\u0a30\3\2\2\2\u0a2b\u0a2c\5\"\22\2\u0a2c\u0a2d\7h\2\2\u0a2d"+
		"\u0a2e\7-\2\2\u0a2e\u0a30\3\2\2\2\u0a2f\u0a07\3\2\2\2\u0a2f\u0a0e\3\2"+
		"\2\2\u0a2f\u0a15\3\2\2\2\u0a2f\u0a1b\3\2\2\2\u0a2f\u0a24\3\2\2\2\u0a2f"+
		"\u0a2b\3\2\2\2\u0a30\u01b1\3\2\2\2\u0a31\u0a32\7-\2\2\u0a32\u0a33\5\b"+
		"\5\2\u0a33\u0a35\5\u01b4\u00db\2\u0a34\u0a36\5$\23\2\u0a35\u0a34\3\2\2"+
		"\2\u0a35\u0a36\3\2\2\2\u0a36\u0a48\3\2\2\2\u0a37\u0a38\7-\2\2\u0a38\u0a39"+
		"\5\22\n\2\u0a39\u0a3b\5\u01b4\u00db\2\u0a3a\u0a3c\5$\23\2\u0a3b\u0a3a"+
		"\3\2\2\2\u0a3b\u0a3c\3\2\2\2\u0a3c\u0a48\3\2\2\2\u0a3d\u0a3e\7-\2\2\u0a3e"+
		"\u0a3f\5\b\5\2\u0a3f\u0a40\5$\23\2\u0a40\u0a41\5\u0112\u008a\2\u0a41\u0a48"+
		"\3\2\2\2\u0a42\u0a43\7-\2\2\u0a43\u0a44\5\22\n\2\u0a44\u0a45\5$\23\2\u0a45"+
		"\u0a46\5\u0112\u008a\2\u0a46\u0a48\3\2\2\2\u0a47\u0a31\3\2\2\2\u0a47\u0a37"+
		"\3\2\2\2\u0a47\u0a3d\3\2\2\2\u0a47\u0a42\3\2\2\2\u0a48\u01b3\3\2\2\2\u0a49"+
		"\u0a4d\5\u01b6\u00dc\2\u0a4a\u0a4c\5\u01b6\u00dc\2\u0a4b\u0a4a\3\2\2\2"+
		"\u0a4c\u0a4f\3\2\2\2\u0a4d\u0a4b\3\2\2\2\u0a4d\u0a4e\3\2\2\2\u0a4e\u01b5"+
		"\3\2\2\2\u0a4f\u0a4d\3\2\2\2\u0a50\u0a52\5\u00fc\177\2\u0a51\u0a50\3\2"+
		"\2\2\u0a52\u0a55\3\2\2\2\u0a53\u0a51\3\2\2\2\u0a53\u0a54\3\2\2\2\u0a54"+
		"\u0a56\3\2\2\2\u0a55\u0a53\3\2\2\2\u0a56\u0a57\7K\2\2\u0a57\u0a58\5\u01ba"+
		"\u00de\2\u0a58\u0a59\7L\2\2\u0a59\u01b7\3\2\2\2\u0a5a\u0a5b\5\u01ba\u00de"+
		"\2\u0a5b\u01b9\3\2\2\2\u0a5c\u0a5f\5\u01bc\u00df\2\u0a5d\u0a5f\5\u01c4"+
		"\u00e3\2\u0a5e\u0a5c\3\2\2\2\u0a5e\u0a5d\3\2\2\2\u0a5f\u01bb\3\2\2\2\u0a60"+
		"\u0a61\5\u01be\u00e0\2\u0a61\u0a62\7g\2\2\u0a62\u0a63\5\u01c2\u00e2\2"+
		"\u0a63\u01bd\3\2\2\2\u0a64\u0a6f\5\2\2\2\u0a65\u0a67\7G\2\2\u0a66\u0a68"+
		"\5\u00a8U\2\u0a67\u0a66\3\2\2\2\u0a67\u0a68\3\2\2\2\u0a68\u0a69\3\2\2"+
		"\2\u0a69\u0a6f\7H\2\2\u0a6a\u0a6b\7G\2\2\u0a6b\u0a6c\5\u01c0\u00e1\2\u0a6c"+
		"\u0a6d\7H\2\2\u0a6d\u0a6f\3\2\2\2\u0a6e\u0a64\3\2\2\2\u0a6e\u0a65\3\2"+
		"\2\2\u0a6e\u0a6a\3\2\2\2\u0a6f\u01bf\3\2\2\2\u0a70\u0a75\5\2\2\2\u0a71"+
		"\u0a72\7N\2\2\u0a72\u0a74\5\2\2\2\u0a73\u0a71\3\2\2\2\u0a74\u0a77\3\2"+
		"\2\2\u0a75\u0a73\3\2\2\2\u0a75\u0a76\3\2\2\2\u0a76\u01c1\3\2\2\2\u0a77"+
		"\u0a75\3\2\2\2\u0a78\u0a7b\5\u01ba\u00de\2\u0a79\u0a7b\5\u0116\u008c\2"+
		"\u0a7a\u0a78\3\2\2\2\u0a7a\u0a79\3\2\2\2\u0a7b\u01c3\3\2\2\2\u0a7c\u0a7f"+
		"\5\u01d4\u00eb\2\u0a7d\u0a7f\5\u01c6\u00e4\2\u0a7e\u0a7c\3\2\2\2\u0a7e"+
		"\u0a7d\3\2\2\2\u0a7f\u01c5\3\2\2\2\u0a80\u0a81\5\u01c8\u00e5\2\u0a81\u0a82"+
		"\5\u01ca\u00e6\2\u0a82\u0a83\5\u01ba\u00de\2\u0a83\u01c7\3\2\2\2\u0a84"+
		"\u0a87\5\u017a\u00be\2\u0a85\u0a87\5\u019e\u00d0\2\u0a86\u0a84\3\2\2\2"+
		"\u0a86\u0a85\3\2\2\2\u0a87\u01c9\3\2\2\2\u0a88\u0a89\t\7\2\2\u0a89\u01cb"+
		"\3\2\2\2\u0a8a\u0a8b\t\b\2\2\u0a8b\u01cd\3\2\2\2\u0a8c\u0a8d\t\t\2\2\u0a8d"+
		"\u01cf\3\2\2\2\u0a8e\u0a8f\t\n\2\2\u0a8f\u01d1\3\2\2\2\u0a90\u0a91\7\16"+
		"\2\2\u0a91\u01d3\3\2\2\2\u0a92\u0aa0\5\u01d6\u00ec\2\u0a93\u0a94\5\u01d6"+
		"\u00ec\2\u0a94\u0a95\7U\2\2\u0a95\u0a96\5\u01ba\u00de\2\u0a96\u0a97\7"+
		"V\2\2\u0a97\u0a98\5\u01d4\u00eb\2\u0a98\u0aa0\3\2\2\2\u0a99\u0a9a\5\u01d6"+
		"\u00ec\2\u0a9a\u0a9b\7U\2\2\u0a9b\u0a9c\5\u01ba\u00de\2\u0a9c\u0a9d\7"+
		"V\2\2\u0a9d\u0a9e\5\u01bc\u00df\2\u0a9e\u0aa0\3\2\2\2\u0a9f\u0a92\3\2"+
		"\2\2\u0a9f\u0a93\3\2\2\2\u0a9f\u0a99\3\2\2\2\u0aa0\u01d5\3\2\2\2\u0aa1"+
		"\u0aa2\b\u00ec\1\2\u0aa2\u0aa3\5\u01d8\u00ed\2\u0aa3\u0aa9\3\2\2\2\u0aa4"+
		"\u0aa5\f\3\2\2\u0aa5\u0aa6\7\\\2\2\u0aa6\u0aa8\5\u01d8\u00ed\2\u0aa7\u0aa4"+
		"\3\2\2\2\u0aa8\u0aab\3\2\2\2\u0aa9\u0aa7\3\2\2\2\u0aa9\u0aaa\3\2\2\2\u0aaa"+
		"\u01d7\3\2\2\2\u0aab\u0aa9\3\2\2\2\u0aac\u0aad\b\u00ed\1\2\u0aad\u0aae"+
		"\5\u01da\u00ee\2\u0aae\u0ab4\3\2\2\2\u0aaf\u0ab0\f\3\2\2\u0ab0\u0ab1\7"+
		"[\2\2\u0ab1\u0ab3\5\u01da\u00ee\2\u0ab2\u0aaf\3\2\2\2\u0ab3\u0ab6\3\2"+
		"\2\2\u0ab4\u0ab2\3\2\2\2\u0ab4\u0ab5\3\2\2\2\u0ab5\u01d9\3\2\2\2\u0ab6"+
		"\u0ab4\3\2\2\2\u0ab7\u0ab8\b\u00ee\1\2\u0ab8\u0ab9\5\u01dc\u00ef\2\u0ab9"+
		"\u0abf\3\2\2\2\u0aba\u0abb\f\3\2\2\u0abb\u0abc\7d\2\2\u0abc\u0abe\5\u01dc"+
		"\u00ef\2\u0abd\u0aba\3\2\2\2\u0abe\u0ac1\3\2\2\2\u0abf\u0abd\3\2\2\2\u0abf"+
		"\u0ac0\3\2\2\2\u0ac0\u01db\3\2\2\2\u0ac1\u0abf\3\2\2\2\u0ac2\u0ac3\b\u00ef"+
		"\1\2\u0ac3\u0ac4\5\u01de\u00f0\2\u0ac4\u0aca\3\2\2\2\u0ac5\u0ac6\f\3\2"+
		"\2\u0ac6\u0ac7\7e\2\2\u0ac7\u0ac9\5\u01de\u00f0\2\u0ac8\u0ac5\3\2\2\2"+
		"\u0ac9\u0acc\3\2\2\2\u0aca\u0ac8\3\2\2\2\u0aca\u0acb\3\2\2\2\u0acb\u01dd"+
		"\3\2\2\2\u0acc\u0aca\3\2\2\2\u0acd\u0ace\b\u00f0\1\2\u0ace\u0acf\5\u01e0"+
		"\u00f1\2\u0acf\u0ad5\3\2\2\2\u0ad0\u0ad1\f\3\2\2\u0ad1\u0ad2\7c\2\2\u0ad2"+
		"\u0ad4\5\u01e0\u00f1\2\u0ad3\u0ad0\3\2\2\2\u0ad4\u0ad7\3\2\2\2\u0ad5\u0ad3"+
		"\3\2\2\2\u0ad5\u0ad6\3\2\2\2\u0ad6\u01df\3\2\2\2\u0ad7\u0ad5\3\2\2\2\u0ad8"+
		"\u0ad9\b\u00f1\1\2\u0ad9\u0ada\5\u01e2\u00f2\2\u0ada\u0ae3\3\2\2\2\u0adb"+
		"\u0adc\f\4\2\2\u0adc\u0add\7W\2\2\u0add\u0ae2\5\u01e2\u00f2\2\u0ade\u0adf"+
		"\f\3\2\2\u0adf\u0ae0\7Z\2\2\u0ae0\u0ae2\5\u01e2\u00f2\2\u0ae1\u0adb\3"+
		"\2\2\2\u0ae1\u0ade\3\2\2\2\u0ae2\u0ae5\3\2\2\2\u0ae3\u0ae1\3\2\2\2\u0ae3"+
		"\u0ae4\3\2\2\2\u0ae4\u01e1\3\2\2\2\u0ae5\u0ae3\3\2\2\2\u0ae6\u0ae7\b\u00f2"+
		"\1\2\u0ae7\u0ae8\5\u01e4\u00f3\2\u0ae8\u0aff\3\2\2\2\u0ae9\u0aea\f\7\2"+
		"\2\u0aea\u0aeb\5\u01ce\u00e8\2\u0aeb\u0aec\5\u01e4\u00f3\2\u0aec\u0afe"+
		"\3\2\2\2\u0aed\u0aee\f\6\2\2\u0aee\u0aef\5\u01ce\u00e8\2\u0aef\u0af0\5"+
		"\u01e4\u00f3\2\u0af0\u0afe\3\2\2\2\u0af1\u0af2\f\5\2\2\u0af2\u0af3\5\u01ce"+
		"\u00e8\2\u0af3\u0af4\5\u01e4\u00f3\2\u0af4\u0afe\3\2\2\2\u0af5\u0af6\f"+
		"\4\2\2\u0af6\u0af7\5\u01ce\u00e8\2\u0af7\u0af8\5\u01e4\u00f3\2\u0af8\u0afe"+
		"\3\2\2\2\u0af9\u0afa\f\3\2\2\u0afa\u0afb\5\u01ce\u00e8\2\u0afb\u0afc\5"+
		"\20\t\2\u0afc\u0afe\3\2\2\2\u0afd\u0ae9\3\2\2\2\u0afd\u0aed\3\2\2\2\u0afd"+
		"\u0af1\3\2\2\2\u0afd\u0af5\3\2\2\2\u0afd\u0af9\3\2\2\2\u0afe\u0b01\3\2"+
		"\2\2\u0aff\u0afd\3\2\2\2\u0aff\u0b00\3\2\2\2\u0b00\u01e3\3\2\2\2\u0b01"+
		"\u0aff\3\2\2\2\u0b02\u0b03\b\u00f3\1\2\u0b03\u0b04\5\u01e8\u00f5\2\u0b04"+
		"\u0b13\3\2\2\2\u0b05\u0b06\f\5\2\2\u0b06\u0b07\5\u01e6\u00f4\2\u0b07\u0b08"+
		"\5\u01e8\u00f5\2\u0b08\u0b12\3\2\2\2\u0b09\u0b0a\f\4\2\2\u0b0a\u0b0b\5"+
		"\u01e6\u00f4\2\u0b0b\u0b0c\5\u01e8\u00f5\2\u0b0c\u0b12\3\2\2\2\u0b0d\u0b0e"+
		"\f\3\2\2\u0b0e\u0b0f\5\u01e6\u00f4\2\u0b0f\u0b10\5\u01e8\u00f5\2\u0b10"+
		"\u0b12\3\2\2\2\u0b11\u0b05\3\2\2\2\u0b11\u0b09\3\2\2\2\u0b11\u0b0d\3\2"+
		"\2\2\u0b12\u0b15\3\2\2\2\u0b13\u0b11\3\2\2\2\u0b13\u0b14\3\2\2\2\u0b14"+
		"\u01e5\3\2\2\2\u0b15\u0b13\3\2\2\2\u0b16\u0b17\7R\2\2\u0b17\u0b1e\7R\2"+
		"\2\u0b18\u0b19\7Q\2\2\u0b19\u0b1e\7Q\2\2\u0b1a\u0b1b\7Q\2\2\u0b1b\u0b1c"+
		"\7Q\2\2\u0b1c\u0b1e\7Q\2\2\u0b1d\u0b16\3\2\2\2\u0b1d\u0b18\3\2\2\2\u0b1d"+
		"\u0b1a\3\2\2\2\u0b1e\u01e7\3\2\2\2\u0b1f\u0b20\b\u00f5\1\2\u0b20\u0b21"+
		"\5\u01ea\u00f6\2\u0b21\u0b2c\3\2\2\2\u0b22\u0b23\f\4\2\2\u0b23\u0b24\5"+
		"\u01cc\u00e7\2\u0b24\u0b25\5\u01ea\u00f6\2\u0b25\u0b2b\3\2\2\2\u0b26\u0b27"+
		"\f\3\2\2\u0b27\u0b28\5\u01cc\u00e7\2\u0b28\u0b29\5\u01ea\u00f6\2\u0b29"+
		"\u0b2b\3\2\2\2\u0b2a\u0b22\3\2\2\2\u0b2a\u0b26\3\2\2\2\u0b2b\u0b2e\3\2"+
		"\2\2\u0b2c\u0b2a\3\2\2\2\u0b2c\u0b2d\3\2\2\2\u0b2d\u01e9\3\2\2\2\u0b2e"+
		"\u0b2c\3\2\2\2\u0b2f\u0b30\b\u00f6\1\2\u0b30\u0b31\5\u01ec\u00f7\2\u0b31"+
		"\u0b40\3\2\2\2\u0b32\u0b33\f\5\2\2\u0b33\u0b34\5\u01d0\u00e9\2\u0b34\u0b35"+
		"\5\u01ec\u00f7\2\u0b35\u0b3f\3\2\2\2\u0b36\u0b37\f\4\2\2\u0b37\u0b38\5"+
		"\u01d0\u00e9\2\u0b38\u0b39\5\u01ec\u00f7\2\u0b39\u0b3f\3\2\2\2\u0b3a\u0b3b"+
		"\f\3\2\2\u0b3b\u0b3c\5\u01d0\u00e9\2\u0b3c\u0b3d\5\u01ec\u00f7\2\u0b3d"+
		"\u0b3f\3\2\2\2\u0b3e\u0b32\3\2\2\2\u0b3e\u0b36\3\2\2\2\u0b3e\u0b3a\3\2"+
		"\2\2\u0b3f\u0b42\3\2\2\2\u0b40\u0b3e\3\2\2\2\u0b40\u0b41\3\2\2\2\u0b41"+
		"\u01eb\3\2\2\2\u0b42\u0b40\3\2\2\2\u0b43\u0b4d\5\u01ee\u00f8\2\u0b44\u0b4d"+
		"\5\u01f0\u00f9\2\u0b45\u0b46\5\u01cc\u00e7\2\u0b46\u0b47\5\u01ec\u00f7"+
		"\2\u0b47\u0b4d\3\2\2\2\u0b48\u0b49\5\u01cc\u00e7\2\u0b49\u0b4a\5\u01ec"+
		"\u00f7\2\u0b4a\u0b4d\3\2\2\2\u0b4b\u0b4d\5\u01f2\u00fa\2\u0b4c\u0b43\3"+
		"\2\2\2\u0b4c\u0b44\3\2\2\2\u0b4c\u0b45\3\2\2\2\u0b4c\u0b48\3\2\2\2\u0b4c"+
		"\u0b4b\3\2\2\2\u0b4d\u01ed\3\2\2\2\u0b4e\u0b4f\7]\2\2\u0b4f\u0b50\5\u01ec"+
		"\u00f7\2\u0b50\u01ef\3\2\2\2\u0b51\u0b52\7^\2\2\u0b52\u0b53\5\u01ec\u00f7"+
		"\2\u0b53\u01f1\3\2\2\2\u0b54\u0b5b\5\u01f4\u00fb\2\u0b55\u0b56\7T\2\2"+
		"\u0b56\u0b5b\5\u01ec\u00f7\2\u0b57\u0b58\7S\2\2\u0b58\u0b5b\5\u01ec\u00f7"+
		"\2\u0b59\u0b5b\5\u01fe\u0100\2\u0b5a\u0b54\3\2\2\2\u0b5a\u0b55\3\2\2\2"+
		"\u0b5a\u0b57\3\2\2\2\u0b5a\u0b59\3\2\2\2\u0b5b\u01f3\3\2\2\2\u0b5c\u0b5f"+
		"\5\u017c\u00bf\2\u0b5d\u0b5f\5> \2\u0b5e\u0b5c\3\2\2\2\u0b5e\u0b5d\3\2"+
		"\2\2\u0b5f\u0b64\3\2\2\2\u0b60\u0b63\5\u01f8\u00fd\2\u0b61\u0b63\5\u01fc"+
		"\u00ff\2\u0b62\u0b60\3\2\2\2\u0b62\u0b61\3\2\2\2\u0b63\u0b66\3\2\2\2\u0b64"+
		"\u0b62\3\2\2\2\u0b64\u0b65\3\2\2\2\u0b65\u01f5\3\2\2\2\u0b66\u0b64\3\2"+
		"\2\2\u0b67\u0b68\5\u01f4\u00fb\2\u0b68\u0b69\7]\2\2\u0b69\u01f7\3\2\2"+
		"\2\u0b6a\u0b6b\7]\2\2\u0b6b\u01f9\3\2\2\2\u0b6c\u0b6d\5\u01f4\u00fb\2"+
		"\u0b6d\u0b6e\7^\2\2\u0b6e\u01fb\3\2\2\2\u0b6f\u0b70\7^\2\2\u0b70\u01fd"+
		"\3\2\2\2\u0b71\u0b72\7G\2\2\u0b72\u0b73\5\b\5\2\u0b73\u0b74\7H\2\2\u0b74"+
		"\u0b75\5\u01ec\u00f7\2\u0b75\u0b8d\3\2\2\2\u0b76\u0b77\7G\2\2\u0b77\u0b7b"+
		"\5\20\t\2\u0b78\u0b7a\5,\27\2\u0b79\u0b78\3\2\2\2\u0b7a\u0b7d\3\2\2\2"+
		"\u0b7b\u0b79\3\2\2\2\u0b7b\u0b7c\3\2\2\2\u0b7c\u0b7e\3\2\2\2\u0b7d\u0b7b"+
		"\3\2\2\2\u0b7e\u0b7f\7H\2\2\u0b7f\u0b80\5\u01f2\u00fa\2\u0b80\u0b8d\3"+
		"\2\2\2\u0b81\u0b82\7G\2\2\u0b82\u0b86\5\20\t\2\u0b83\u0b85\5,\27\2\u0b84"+
		"\u0b83\3\2\2\2\u0b85\u0b88\3\2\2\2\u0b86\u0b84\3\2\2\2\u0b86\u0b87\3\2"+
		"\2\2\u0b87\u0b89\3\2\2\2\u0b88\u0b86\3\2\2\2\u0b89\u0b8a\7H\2\2\u0b8a"+
		"\u0b8b\5\u01bc\u00df\2\u0b8b\u0b8d\3\2\2\2\u0b8c\u0b71\3\2\2\2\u0b8c\u0b76"+
		"\3\2\2\2\u0b8c\u0b81\3\2\2\2\u0b8d\u01ff\3\2\2\2\u0147\u0206\u020b\u0212"+
		"\u0216\u021a\u0223\u0227\u022b\u022d\u0232\u0238\u023a\u023f\u0243\u0256"+
		"\u025c\u0262\u0267\u0272\u0275\u0283\u0288\u028d\u0292\u0298\u02a2\u02aa"+
		"\u02b4\u02bc\u02c8\u02cf\u02d4\u02da\u02e1\u02e7\u02ec\u02f5\u0300\u031d"+
		"\u0322\u0326\u032e\u0335\u033e\u034c\u034f\u035b\u035e\u036e\u0373\u037f"+
		"\u0384\u038a\u038d\u0390\u0397\u03a2\u03ad\u03bb\u03c2\u03cb\u03d2\u03dc"+
		"\u03e7\u03ee\u03f4\u03f8\u03fc\u0400\u0404\u0409\u040d\u0411\u0413\u0418"+
		"\u041e\u0420\u0425\u0429\u043c\u0445\u0452\u0457\u045d\u0463\u0465\u0469"+
		"\u046e\u0472\u0479\u0480\u0488\u048b\u0490\u0498\u049d\u04a4\u04ab\u04b0"+
		"\u04b7\u04c3\u04c8\u04cc\u04d6\u04dd\u04e4\u04e7\u04ec\u04f4\u04f7\u04fc"+
		"\u0501\u0506\u050b\u0512\u0517\u051f\u0524\u0529\u052f\u0535\u0538\u053b"+
		"\u0544\u054a\u0550\u0553\u0556\u055e\u0563\u0569\u056c\u0573\u057d\u0586"+
		"\u0590\u0595\u05a0\u05a5\u05b2\u05b7\u05c3\u05cd\u05d2\u05da\u05dd\u05e4"+
		"\u05ec\u05f1\u05f9\u0602\u060b\u0615\u0619\u061c\u0625\u0633\u0636\u063f"+
		"\u0644\u064c\u0652\u065a\u0666\u066d\u067b\u0691\u06b3\u06bf\u06c5\u06d1"+
		"\u06de\u06f8\u06fc\u0701\u0705\u0709\u0711\u0715\u0719\u0720\u0729\u0731"+
		"\u0740\u074c\u0752\u0758\u076d\u0772\u0778\u0784\u078f\u0799\u079c\u07a1"+
		"\u07aa\u07b0\u07b9\u07bd\u07c1\u07c6\u07ce\u07e5\u07ee\u0804\u080b\u0813"+
		"\u081a\u0824\u083b\u0844\u084e\u0864\u0868\u086f\u0873\u0877\u087b\u0881"+
		"\u0885\u0889\u088d\u0893\u0897\u089b\u089f\u08a1\u08a6\u08aa\u08ae\u08b2"+
		"\u08b6\u08bd\u08c1\u08c5\u08c9\u08cf\u08d3\u08d7\u08db\u08dd\u08e1\u08f0"+
		"\u08fe\u090a\u0913\u0922\u092f\u0938\u093e\u0945\u094a\u0951\u0956\u095d"+
		"\u0962\u0969\u096e\u0977\u097c\u0980\u0984\u0989\u0990\u0997\u099c\u09a3"+
		"\u09a8\u09af\u09b4\u09bd\u09c2\u09c6\u09cd\u09d3\u09da\u09e1\u09e8\u09f0"+
		"\u09f7\u09ff\u0a03\u0a0a\u0a11\u0a18\u0a20\u0a27\u0a2f\u0a35\u0a3b\u0a47"+
		"\u0a4d\u0a53\u0a5e\u0a67\u0a6e\u0a75\u0a7a\u0a7e\u0a86\u0a9f\u0aa9\u0ab4"+
		"\u0abf\u0aca\u0ad5\u0ae1\u0ae3\u0afd\u0aff\u0b11\u0b13\u0b1d\u0b2a\u0b2c"+
		"\u0b3e\u0b40\u0b4c\u0b5a\u0b5e\u0b62\u0b64\u0b7b\u0b86\u0b8c";
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