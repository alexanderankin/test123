// Generated from /home/danson/src/jedit/plugins/JavaSideKick/src/sidekick/java/parser/antlr/Java8.g4 by ANTLR 4.10.1

package sidekick.java.parser.antlr;

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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, ABSTRACT=15, ASSERT=16, 
		BOOLEAN=17, BREAK=18, BYTE=19, CASE=20, CATCH=21, CHAR=22, CLASS=23, CONST=24, 
		CONTINUE=25, DEFAULT=26, DO=27, DOUBLE=28, ELSE=29, ENUM=30, EXTENDS=31, 
		FINAL=32, FINALLY=33, FLOAT=34, FOR=35, IF=36, GOTO=37, IMPLEMENTS=38, 
		IMPORT=39, INSTANCEOF=40, INT=41, INTERFACE=42, LONG=43, NATIVE=44, NEW=45, 
		PACKAGE=46, PRIVATE=47, PROTECTED=48, PUBLIC=49, RETURN=50, SHORT=51, 
		STATIC=52, STRICTFP=53, SUPER=54, SWITCH=55, SYNCHRONIZED=56, THIS=57, 
		THROW=58, THROWS=59, TRANSIENT=60, TRY=61, VOID=62, VOLATILE=63, WHILE=64, 
		IntegerLiteral=65, FloatingPointLiteral=66, BooleanLiteral=67, CharacterLiteral=68, 
		StringLiteral=69, NullLiteral=70, LPAREN=71, RPAREN=72, LBRACE=73, RBRACE=74, 
		LBRACK=75, RBRACK=76, SEMI=77, COMMA=78, DOT=79, ASSIGN=80, GT=81, LT=82, 
		BANG=83, TILDE=84, QUESTION=85, COLON=86, EQUAL=87, LE=88, GE=89, NOTEQUAL=90, 
		AND=91, OR=92, INC=93, DEC=94, ADD=95, MUL=96, DIV=97, BITAND=98, BITOR=99, 
		CARET=100, MOD=101, ARROW=102, COLONCOLON=103, ADD_ASSIGN=104, SUB_ASSIGN=105, 
		MUL_ASSIGN=106, DIV_ASSIGN=107, AND_ASSIGN=108, OR_ASSIGN=109, XOR_ASSIGN=110, 
		MOD_ASSIGN=111, LSHIFT_ASSIGN=112, RSHIFT_ASSIGN=113, URSHIFT_ASSIGN=114, 
		Identifier=115, AT=116, ELLIPSIS=117, WS=118, DOC_COMMENT=119, COMMENT=120, 
		LINE_COMMENT=121;
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
		RULE_localVariableDeclaration = 142, RULE_localVariableType = 143, RULE_statement = 144, 
		RULE_statementNoShortIf = 145, RULE_statementWithoutTrailingSubstatement = 146, 
		RULE_emptyStatement = 147, RULE_labeledStatement = 148, RULE_labeledStatementNoShortIf = 149, 
		RULE_expressionStatement = 150, RULE_statementExpression = 151, RULE_ifThenStatement = 152, 
		RULE_ifThenElseStatement = 153, RULE_ifThenElseStatementNoShortIf = 154, 
		RULE_assertStatement = 155, RULE_switchStatement = 156, RULE_switchBlock = 157, 
		RULE_switchBlockStatementGroup = 158, RULE_switchLabels = 159, RULE_switchLabel = 160, 
		RULE_enumConstantName = 161, RULE_whileStatement = 162, RULE_whileStatementNoShortIf = 163, 
		RULE_doStatement = 164, RULE_forStatement = 165, RULE_forStatementNoShortIf = 166, 
		RULE_basicForStatement = 167, RULE_basicForStatementNoShortIf = 168, RULE_forInit = 169, 
		RULE_forUpdate = 170, RULE_statementExpressionList = 171, RULE_enhancedForStatement = 172, 
		RULE_enhancedForStatementNoShortIf = 173, RULE_breakStatement = 174, RULE_continueStatement = 175, 
		RULE_returnStatement = 176, RULE_throwStatement = 177, RULE_synchronizedStatement = 178, 
		RULE_tryStatement = 179, RULE_catches = 180, RULE_catchClause = 181, RULE_catchFormalParameter = 182, 
		RULE_catchType = 183, RULE_finally_ = 184, RULE_tryWithResourcesStatement = 185, 
		RULE_resourceSpecification = 186, RULE_resourceList = 187, RULE_resource = 188, 
		RULE_variableAccess = 189, RULE_primary = 190, RULE_primaryNoNewArray = 191, 
		RULE_primaryNoNewArray_lf_arrayAccess = 192, RULE_primaryNoNewArray_lfno_arrayAccess = 193, 
		RULE_primaryNoNewArray_lf_primary = 194, RULE_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary = 195, 
		RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary = 196, RULE_primaryNoNewArray_lfno_primary = 197, 
		RULE_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary = 198, 
		RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary = 199, 
		RULE_classInstanceCreationExpression = 200, RULE_classInstanceCreationExpression_lf_primary = 201, 
		RULE_classInstanceCreationExpression_lfno_primary = 202, RULE_typeArgumentsOrDiamond = 203, 
		RULE_fieldAccess = 204, RULE_fieldAccess_lf_primary = 205, RULE_fieldAccess_lfno_primary = 206, 
		RULE_arrayAccess = 207, RULE_arrayAccess_lf_primary = 208, RULE_arrayAccess_lfno_primary = 209, 
		RULE_methodInvocation = 210, RULE_methodInvocation_lf_primary = 211, RULE_methodInvocation_lfno_primary = 212, 
		RULE_argumentList = 213, RULE_methodReference = 214, RULE_methodReference_lf_primary = 215, 
		RULE_methodReference_lfno_primary = 216, RULE_arrayCreationExpression = 217, 
		RULE_dimExprs = 218, RULE_dimExpr = 219, RULE_constantExpression = 220, 
		RULE_expression = 221, RULE_lambdaExpression = 222, RULE_lambdaParameters = 223, 
		RULE_inferredFormalParameterList = 224, RULE_lambdaBody = 225, RULE_assignmentExpression = 226, 
		RULE_assignment = 227, RULE_leftHandSide = 228, RULE_assignmentOperator = 229, 
		RULE_additiveOperator = 230, RULE_relationalOperator = 231, RULE_multiplicativeOperator = 232, 
		RULE_squareBrackets = 233, RULE_conditionalExpression = 234, RULE_conditionalOrExpression = 235, 
		RULE_conditionalAndExpression = 236, RULE_inclusiveOrExpression = 237, 
		RULE_exclusiveOrExpression = 238, RULE_andExpression = 239, RULE_equalityExpression = 240, 
		RULE_relationalExpression = 241, RULE_shiftExpression = 242, RULE_shiftOperator = 243, 
		RULE_additiveExpression = 244, RULE_multiplicativeExpression = 245, RULE_unaryExpression = 246, 
		RULE_preIncrementExpression = 247, RULE_preDecrementExpression = 248, 
		RULE_unaryExpressionNotPlusMinus = 249, RULE_postfixExpression = 250, 
		RULE_postIncrementExpression = 251, RULE_postIncrementExpression_lf_postfixExpression = 252, 
		RULE_postDecrementExpression = 253, RULE_postDecrementExpression_lf_postfixExpression = 254, 
		RULE_castExpression = 255;
	private static String[] makeRuleNames() {
		return new String[] {
			"identifier", "literal", "type", "primitiveType", "numericType", "integralType", 
			"floatingPointType", "referenceType", "classOrInterfaceType", "classType", 
			"classType_lf_classOrInterfaceType", "classType_lfno_classOrInterfaceType", 
			"interfaceType", "interfaceType_lf_classOrInterfaceType", "interfaceType_lfno_classOrInterfaceType", 
			"typeVariable", "arrayType", "dims", "typeParameter", "typeParameterModifier", 
			"typeBound", "additionalBound", "typeArguments", "typeArgumentList", 
			"typeArgument", "wildcard", "wildcardBounds", "packageName", "typeName", 
			"packageOrTypeName", "expressionName", "methodName", "ambiguousName", 
			"compilationUnit", "packageDeclaration", "packageModifier", "importDeclaration", 
			"singleTypeImportDeclaration", "typeImportOnDemandDeclaration", "singleStaticImportDeclaration", 
			"staticImportOnDemandDeclaration", "typeDeclaration", "moduleDeclaration", 
			"moduleStatement", "requiresModifier", "moduleName", "classDeclaration", 
			"normalClassDeclaration", "classModifiers", "classModifier", "typeParameters", 
			"typeParameterList", "superclass", "superinterfaces", "interfaceTypeList", 
			"classBody", "classBodyDeclaration", "classMemberDeclaration", "fieldDeclaration", 
			"fieldModifiers", "fieldModifier", "variableDeclaratorList", "variableDeclarator", 
			"variableDeclaratorId", "variableInitializer", "unannType", "unannPrimitiveType", 
			"unannReferenceType", "unannClassOrInterfaceType", "unannClassType", 
			"unannClassType_lf_unannClassOrInterfaceType", "unannClassType_lfno_unannClassOrInterfaceType", 
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
			"localVariableDeclaration", "localVariableType", "statement", "statementNoShortIf", 
			"statementWithoutTrailingSubstatement", "emptyStatement", "labeledStatement", 
			"labeledStatementNoShortIf", "expressionStatement", "statementExpression", 
			"ifThenStatement", "ifThenElseStatement", "ifThenElseStatementNoShortIf", 
			"assertStatement", "switchStatement", "switchBlock", "switchBlockStatementGroup", 
			"switchLabels", "switchLabel", "enumConstantName", "whileStatement", 
			"whileStatementNoShortIf", "doStatement", "forStatement", "forStatementNoShortIf", 
			"basicForStatement", "basicForStatementNoShortIf", "forInit", "forUpdate", 
			"statementExpressionList", "enhancedForStatement", "enhancedForStatementNoShortIf", 
			"breakStatement", "continueStatement", "returnStatement", "throwStatement", 
			"synchronizedStatement", "tryStatement", "catches", "catchClause", "catchFormalParameter", 
			"catchType", "finally_", "tryWithResourcesStatement", "resourceSpecification", 
			"resourceList", "resource", "variableAccess", "primary", "primaryNoNewArray", 
			"primaryNoNewArray_lf_arrayAccess", "primaryNoNewArray_lfno_arrayAccess", 
			"primaryNoNewArray_lf_primary", "primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary", 
			"primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary", "primaryNoNewArray_lfno_primary", 
			"primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary", "primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary", 
			"classInstanceCreationExpression", "classInstanceCreationExpression_lf_primary", 
			"classInstanceCreationExpression_lfno_primary", "typeArgumentsOrDiamond", 
			"fieldAccess", "fieldAccess_lf_primary", "fieldAccess_lfno_primary", 
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
			null, "'open'", "'module'", "'requires'", "'transitive'", "'exports'", 
			"'opens'", "'to'", "'uses'", "'provides'", "'with'", "'var'", "'<>'", 
			"'-'", "'[]'", "'abstract'", "'assert'", "'boolean'", "'break'", "'byte'", 
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
			"'*'", "'/'", "'&'", "'|'", "'^'", "'%'", "'->'", "'::'", "'+='", "'-='", 
			"'*='", "'/='", "'&='", "'|='", "'^='", "'%='", "'<<='", "'>>='", "'>>>='", 
			null, "'@'", "'...'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
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
			"ADD", "MUL", "DIV", "BITAND", "BITOR", "CARET", "MOD", "ARROW", "COLONCOLON", 
			"ADD_ASSIGN", "SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "LSHIFT_ASSIGN", "RSHIFT_ASSIGN", 
			"URSHIFT_ASSIGN", "Identifier", "AT", "ELLIPSIS", "WS", "DOC_COMMENT", 
			"COMMENT", "LINE_COMMENT"
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

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_identifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(512);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) ) {
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
		enterRule(_localctx, 2, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(514);
			_la = _input.LA(1);
			if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)))) != 0)) ) {
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
		enterRule(_localctx, 4, RULE_type);
		try {
			setState(518);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(516);
				primitiveType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(517);
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
		enterRule(_localctx, 6, RULE_primitiveType);
		int _la;
		try {
			setState(534);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(523);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(520);
					annotation();
					}
					}
					setState(525);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(526);
				numericType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(530);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(527);
					annotation();
					}
					}
					setState(532);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(533);
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
		enterRule(_localctx, 8, RULE_numericType);
		try {
			setState(538);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BYTE:
			case CHAR:
			case INT:
			case LONG:
			case SHORT:
				enterOuterAlt(_localctx, 1);
				{
				setState(536);
				integralType();
				}
				break;
			case DOUBLE:
			case FLOAT:
				enterOuterAlt(_localctx, 2);
				{
				setState(537);
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
		enterRule(_localctx, 10, RULE_integralType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
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
		enterRule(_localctx, 12, RULE_floatingPointType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
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
		enterRule(_localctx, 14, RULE_referenceType);
		try {
			setState(547);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(544);
				classOrInterfaceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(545);
				typeVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(546);
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
		enterRule(_localctx, 16, RULE_classOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(551);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(549);
				classType_lfno_classOrInterfaceType();
				}
				break;
			case 2:
				{
				setState(550);
				interfaceType_lfno_classOrInterfaceType();
				}
				break;
			}
			setState(557);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(555);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						setState(553);
						classType_lf_classOrInterfaceType();
						}
						break;
					case 2:
						{
						setState(554);
						interfaceType_lf_classOrInterfaceType();
						}
						break;
					}
					} 
				}
				setState(559);
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
		enterRule(_localctx, 18, RULE_classType);
		int _la;
		try {
			setState(570);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(560);
				annotationIdentifier();
				setState(562);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(561);
					typeArguments();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(564);
				classOrInterfaceType();
				setState(565);
				match(DOT);
				setState(566);
				annotationIdentifier();
				setState(568);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(567);
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
		enterRule(_localctx, 20, RULE_classType_lf_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(572);
			match(DOT);
			setState(573);
			annotationIdentifier();
			setState(575);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(574);
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
		enterRule(_localctx, 22, RULE_classType_lfno_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(577);
			annotationIdentifier();
			setState(579);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(578);
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
		enterRule(_localctx, 24, RULE_interfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(581);
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
		enterRule(_localctx, 26, RULE_interfaceType_lf_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(583);
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
		enterRule(_localctx, 28, RULE_interfaceType_lfno_classOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(585);
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
		enterRule(_localctx, 30, RULE_typeVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(587);
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
		enterRule(_localctx, 32, RULE_arrayType);
		try {
			setState(598);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(589);
				primitiveType();
				setState(590);
				dims();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(592);
				classOrInterfaceType();
				setState(593);
				dims();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(595);
				typeVariable();
				setState(596);
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
		enterRule(_localctx, 34, RULE_dims);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(600);
			annotationDim();
			setState(604);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(601);
					annotationDim();
					}
					} 
				}
				setState(606);
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
		enterRule(_localctx, 36, RULE_typeParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(610);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(607);
				typeParameterModifier();
				}
				}
				setState(612);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(613);
			identifier();
			setState(615);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(614);
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
		enterRule(_localctx, 38, RULE_typeParameterModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(617);
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
		enterRule(_localctx, 40, RULE_typeBound);
		int _la;
		try {
			setState(629);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(619);
				match(EXTENDS);
				setState(620);
				typeVariable();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(621);
				match(EXTENDS);
				setState(622);
				classOrInterfaceType();
				setState(626);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(623);
					additionalBound();
					}
					}
					setState(628);
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
		enterRule(_localctx, 42, RULE_additionalBound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(631);
			match(BITAND);
			setState(632);
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
		enterRule(_localctx, 44, RULE_typeArguments);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(634);
			match(LT);
			setState(635);
			typeArgumentList();
			setState(636);
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
		enterRule(_localctx, 46, RULE_typeArgumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			typeArgument();
			setState(643);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(639);
				match(COMMA);
				setState(640);
				typeArgument();
				}
				}
				setState(645);
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
		enterRule(_localctx, 48, RULE_typeArgument);
		try {
			setState(648);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(646);
				referenceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(647);
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
		enterRule(_localctx, 50, RULE_wildcard);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(653);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(650);
				annotation();
				}
				}
				setState(655);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(656);
			match(QUESTION);
			setState(658);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS || _la==SUPER) {
				{
				setState(657);
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
		enterRule(_localctx, 52, RULE_wildcardBounds);
		try {
			setState(664);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case EXTENDS:
				enterOuterAlt(_localctx, 1);
				{
				setState(660);
				match(EXTENDS);
				setState(661);
				referenceType();
				}
				break;
			case SUPER:
				enterOuterAlt(_localctx, 2);
				{
				setState(662);
				match(SUPER);
				setState(663);
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
		int _startState = 54;
		enterRecursionRule(_localctx, 54, RULE_packageName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(667);
			identifier();
			}
			_ctx.stop = _input.LT(-1);
			setState(674);
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
					setState(669);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(670);
					match(DOT);
					setState(671);
					identifier();
					}
					} 
				}
				setState(676);
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
		enterRule(_localctx, 56, RULE_typeName);
		try {
			setState(682);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(677);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(678);
				packageOrTypeName(0);
				setState(679);
				match(DOT);
				setState(680);
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
		int _startState = 58;
		enterRecursionRule(_localctx, 58, RULE_packageOrTypeName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(685);
			identifier();
			}
			_ctx.stop = _input.LT(-1);
			setState(692);
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
					setState(687);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(688);
					match(DOT);
					setState(689);
					identifier();
					}
					} 
				}
				setState(694);
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
		enterRule(_localctx, 60, RULE_expressionName);
		try {
			setState(700);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(695);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(696);
				ambiguousName(0);
				setState(697);
				match(DOT);
				setState(698);
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

	public final MethodNameContext methodName() throws RecognitionException {
		MethodNameContext _localctx = new MethodNameContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_methodName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(702);
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
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_ambiguousName, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(705);
			identifier();
			}
			_ctx.stop = _input.LT(-1);
			setState(712);
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
					setState(707);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(708);
					match(DOT);
					setState(709);
					identifier();
					}
					} 
				}
				setState(714);
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

	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_compilationUnit);
		int _la;
		try {
			setState(743);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(715);
				packageDeclaration();
				setState(716);
				match(EOF);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(719);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
				case 1:
					{
					setState(718);
					packageDeclaration();
					}
					break;
				}
				setState(724);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IMPORT) {
					{
					{
					setState(721);
					importDeclaration();
					}
					}
					setState(726);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(730);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << CLASS) | (1L << ENUM) | (1L << FINAL) | (1L << INTERFACE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==SEMI || _la==AT) {
					{
					{
					setState(727);
					typeDeclaration();
					}
					}
					setState(732);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(733);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(737);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IMPORT) {
					{
					{
					setState(734);
					importDeclaration();
					}
					}
					setState(739);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(740);
				moduleDeclaration();
				setState(741);
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
		public TerminalNode PACKAGE() { return getToken(Java8Parser.PACKAGE, 0); }
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
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
		enterRule(_localctx, 68, RULE_packageDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(748);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(745);
				packageModifier();
				}
				}
				setState(750);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(751);
			match(PACKAGE);
			setState(752);
			identifier();
			setState(757);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(753);
				match(DOT);
				setState(754);
				identifier();
				}
				}
				setState(759);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(760);
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
		enterRule(_localctx, 70, RULE_packageModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(762);
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
		enterRule(_localctx, 72, RULE_importDeclaration);
		try {
			setState(768);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(764);
				singleTypeImportDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(765);
				typeImportOnDemandDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(766);
				singleStaticImportDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(767);
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
		enterRule(_localctx, 74, RULE_singleTypeImportDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(770);
			match(IMPORT);
			setState(771);
			typeName();
			setState(772);
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
		enterRule(_localctx, 76, RULE_typeImportOnDemandDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(774);
			match(IMPORT);
			setState(775);
			packageOrTypeName(0);
			setState(776);
			match(DOT);
			setState(777);
			match(MUL);
			setState(778);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 78, RULE_singleStaticImportDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(780);
			match(IMPORT);
			setState(781);
			match(STATIC);
			setState(782);
			typeName();
			setState(783);
			match(DOT);
			setState(784);
			identifier();
			setState(785);
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
		enterRule(_localctx, 80, RULE_staticImportOnDemandDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(787);
			match(IMPORT);
			setState(788);
			match(STATIC);
			setState(789);
			typeName();
			setState(790);
			match(DOT);
			setState(791);
			match(MUL);
			setState(792);
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
		enterRule(_localctx, 82, RULE_typeDeclaration);
		try {
			setState(797);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(794);
				classDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(795);
				interfaceDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(796);
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
		public List<TerminalNode> Identifier() { return getTokens(Java8Parser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(Java8Parser.Identifier, i);
		}
		public TerminalNode LBRACE() { return getToken(Java8Parser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(Java8Parser.RBRACE, 0); }
		public List<AnnotationContext> annotation() {
			return getRuleContexts(AnnotationContext.class);
		}
		public AnnotationContext annotation(int i) {
			return getRuleContext(AnnotationContext.class,i);
		}
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
		public List<ModuleStatementContext> moduleStatement() {
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

	public final ModuleDeclarationContext moduleDeclaration() throws RecognitionException {
		ModuleDeclarationContext _localctx = new ModuleDeclarationContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_moduleDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(802);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(799);
				annotation();
				}
				}
				setState(804);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(806);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(805);
				match(T__0);
				}
			}

			setState(808);
			match(T__1);
			setState(809);
			match(Identifier);
			setState(814);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(810);
				match(DOT);
				setState(811);
				match(Identifier);
				}
				}
				setState(816);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(817);
			match(LBRACE);
			setState(821);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__4) | (1L << T__5) | (1L << T__7) | (1L << T__8))) != 0)) {
				{
				{
				setState(818);
				moduleStatement();
				}
				}
				setState(823);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(824);
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
		public List<ModuleNameContext> moduleName() {
			return getRuleContexts(ModuleNameContext.class);
		}
		public ModuleNameContext moduleName(int i) {
			return getRuleContext(ModuleNameContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
		public List<RequiresModifierContext> requiresModifier() {
			return getRuleContexts(RequiresModifierContext.class);
		}
		public RequiresModifierContext requiresModifier(int i) {
			return getRuleContext(RequiresModifierContext.class,i);
		}
		public PackageNameContext packageName() {
			return getRuleContext(PackageNameContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(Java8Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(Java8Parser.COMMA, i);
		}
		public List<TypeNameContext> typeName() {
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

	public final ModuleStatementContext moduleStatement() throws RecognitionException {
		ModuleStatementContext _localctx = new ModuleStatementContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_moduleStatement);
		int _la;
		try {
			setState(883);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				setState(826);
				match(T__2);
				setState(830);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3 || _la==STATIC) {
					{
					{
					setState(827);
					requiresModifier();
					}
					}
					setState(832);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(833);
				moduleName(0);
				setState(834);
				match(SEMI);
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				setState(836);
				match(T__4);
				setState(837);
				packageName(0);
				setState(847);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__6) {
					{
					setState(838);
					match(T__6);
					setState(839);
					moduleName(0);
					setState(844);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(840);
						match(COMMA);
						setState(841);
						moduleName(0);
						}
						}
						setState(846);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(849);
				match(SEMI);
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 3);
				{
				setState(851);
				match(T__5);
				setState(852);
				packageName(0);
				setState(862);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__6) {
					{
					setState(853);
					match(T__6);
					setState(854);
					moduleName(0);
					setState(859);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(855);
						match(COMMA);
						setState(856);
						moduleName(0);
						}
						}
						setState(861);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(864);
				match(SEMI);
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(866);
				match(T__7);
				setState(867);
				typeName();
				setState(868);
				match(SEMI);
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 5);
				{
				setState(870);
				match(T__8);
				setState(871);
				typeName();
				setState(872);
				match(T__9);
				setState(873);
				typeName();
				setState(878);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(874);
					match(COMMA);
					setState(875);
					typeName();
					}
					}
					setState(880);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(881);
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
		public TerminalNode STATIC() { return getToken(Java8Parser.STATIC, 0); }
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

	public final RequiresModifierContext requiresModifier() throws RecognitionException {
		RequiresModifierContext _localctx = new RequiresModifierContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_requiresModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(885);
			_la = _input.LA(1);
			if ( !(_la==T__3 || _la==STATIC) ) {
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

	public static class ModuleNameContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(Java8Parser.Identifier, 0); }
		public ModuleNameContext moduleName() {
			return getRuleContext(ModuleNameContext.class,0);
		}
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
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
			setState(888);
			match(Identifier);
			}
			_ctx.stop = _input.LT(-1);
			setState(895);
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
					setState(890);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(891);
					match(DOT);
					setState(892);
					match(Identifier);
					}
					} 
				}
				setState(897);
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

	public final ClassDeclarationContext classDeclaration() throws RecognitionException {
		ClassDeclarationContext _localctx = new ClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_classDeclaration);
		try {
			setState(900);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(898);
				normalClassDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(899);
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

	public final NormalClassDeclarationContext normalClassDeclaration() throws RecognitionException {
		NormalClassDeclarationContext _localctx = new NormalClassDeclarationContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_normalClassDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(902);
			classModifiers();
			setState(903);
			match(CLASS);
			setState(904);
			identifier();
			setState(906);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(905);
				typeParameters();
				}
			}

			setState(909);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(908);
				superclass();
				}
			}

			setState(912);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPLEMENTS) {
				{
				setState(911);
				superinterfaces();
				}
			}

			setState(914);
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
		enterRule(_localctx, 96, RULE_classModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(919);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << FINAL) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(916);
				classModifier();
				}
				}
				setState(921);
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
		enterRule(_localctx, 98, RULE_classModifier);
		try {
			setState(930);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(922);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(923);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(924);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(925);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(926);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(927);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(928);
				match(FINAL);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 8);
				{
				setState(929);
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
		enterRule(_localctx, 100, RULE_typeParameters);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(932);
			match(LT);
			setState(933);
			typeParameterList();
			setState(934);
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
		enterRule(_localctx, 102, RULE_typeParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(936);
			typeParameter();
			setState(941);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(937);
				match(COMMA);
				setState(938);
				typeParameter();
				}
				}
				setState(943);
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
		enterRule(_localctx, 104, RULE_superclass);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(944);
			match(EXTENDS);
			setState(945);
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
		enterRule(_localctx, 106, RULE_superinterfaces);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(947);
			match(IMPLEMENTS);
			setState(948);
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
		enterRule(_localctx, 108, RULE_interfaceTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(950);
			interfaceType();
			setState(955);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(951);
				match(COMMA);
				setState(952);
				interfaceType();
				}
				}
				setState(957);
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
		enterRule(_localctx, 110, RULE_classBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(958);
			match(LBRACE);
			setState(962);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE))) != 0) || ((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & ((1L << (LBRACE - 73)) | (1L << (SEMI - 73)) | (1L << (LT - 73)) | (1L << (Identifier - 73)) | (1L << (AT - 73)))) != 0)) {
				{
				{
				setState(959);
				classBodyDeclaration();
				}
				}
				setState(964);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(965);
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
		enterRule(_localctx, 112, RULE_classBodyDeclaration);
		try {
			setState(971);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(967);
				classMemberDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(968);
				instanceInitializer();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(969);
				staticInitializer();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(970);
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
		enterRule(_localctx, 114, RULE_classMemberDeclaration);
		try {
			setState(978);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(973);
				fieldDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(974);
				methodDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(975);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(976);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(977);
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
		enterRule(_localctx, 116, RULE_fieldDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(980);
			fieldModifiers();
			setState(981);
			unannType();
			setState(982);
			variableDeclaratorList();
			setState(983);
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
		enterRule(_localctx, 118, RULE_fieldModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(988);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FINAL) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << TRANSIENT) | (1L << VOLATILE))) != 0) || _la==AT) {
				{
				{
				setState(985);
				fieldModifier();
				}
				}
				setState(990);
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
		enterRule(_localctx, 120, RULE_fieldModifier);
		try {
			setState(999);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(991);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(992);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(993);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(994);
				match(PRIVATE);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 5);
				{
				setState(995);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 6);
				{
				setState(996);
				match(FINAL);
				}
				break;
			case TRANSIENT:
				enterOuterAlt(_localctx, 7);
				{
				setState(997);
				match(TRANSIENT);
				}
				break;
			case VOLATILE:
				enterOuterAlt(_localctx, 8);
				{
				setState(998);
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
		enterRule(_localctx, 122, RULE_variableDeclaratorList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1001);
			variableDeclarator();
			setState(1006);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1002);
				match(COMMA);
				setState(1003);
				variableDeclarator();
				}
				}
				setState(1008);
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
		enterRule(_localctx, 124, RULE_variableDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1009);
			variableDeclaratorId();
			setState(1012);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(1010);
				match(ASSIGN);
				setState(1011);
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

	public final VariableDeclaratorIdContext variableDeclaratorId() throws RecognitionException {
		VariableDeclaratorIdContext _localctx = new VariableDeclaratorIdContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_variableDeclaratorId);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1014);
			identifier();
			setState(1016);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13 || _la==AT) {
				{
				setState(1015);
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
		enterRule(_localctx, 128, RULE_variableInitializer);
		try {
			setState(1020);
			_errHandler.sync(this);
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
			case T__12:
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
			case Identifier:
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1018);
				expression();
				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(1019);
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
		enterRule(_localctx, 130, RULE_unannType);
		try {
			setState(1024);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1022);
				unannPrimitiveType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1023);
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
		enterRule(_localctx, 132, RULE_unannPrimitiveType);
		try {
			setState(1028);
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
				setState(1026);
				numericType();
				}
				break;
			case BOOLEAN:
				enterOuterAlt(_localctx, 2);
				{
				setState(1027);
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
		enterRule(_localctx, 134, RULE_unannReferenceType);
		try {
			setState(1033);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1030);
				unannClassOrInterfaceType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1031);
				unannTypeVariable();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1032);
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
		enterRule(_localctx, 136, RULE_unannClassOrInterfaceType);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1037);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				{
				setState(1035);
				unannClassType_lfno_unannClassOrInterfaceType();
				}
				break;
			case 2:
				{
				setState(1036);
				unannInterfaceType_lfno_unannClassOrInterfaceType();
				}
				break;
			}
			setState(1043);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,73,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(1041);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
					case 1:
						{
						setState(1039);
						unannClassType_lf_unannClassOrInterfaceType();
						}
						break;
					case 2:
						{
						setState(1040);
						unannInterfaceType_lf_unannClassOrInterfaceType();
						}
						break;
					}
					} 
				}
				setState(1045);
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
		enterRule(_localctx, 138, RULE_unannClassType);
		int _la;
		try {
			setState(1056);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1046);
				identifier();
				setState(1048);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1047);
					typeArguments();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1050);
				unannClassOrInterfaceType();
				setState(1051);
				match(DOT);
				setState(1052);
				annotationIdentifier();
				setState(1054);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1053);
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
		enterRule(_localctx, 140, RULE_unannClassType_lf_unannClassOrInterfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1058);
			match(DOT);
			setState(1059);
			annotationIdentifier();
			setState(1061);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1060);
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

	public final UnannClassType_lfno_unannClassOrInterfaceTypeContext unannClassType_lfno_unannClassOrInterfaceType() throws RecognitionException {
		UnannClassType_lfno_unannClassOrInterfaceTypeContext _localctx = new UnannClassType_lfno_unannClassOrInterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_unannClassType_lfno_unannClassOrInterfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1063);
			identifier();
			setState(1065);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1064);
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
		enterRule(_localctx, 144, RULE_unannInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1067);
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
		enterRule(_localctx, 146, RULE_unannInterfaceType_lf_unannClassOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1069);
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
		enterRule(_localctx, 148, RULE_unannInterfaceType_lfno_unannClassOrInterfaceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1071);
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

	public final UnannTypeVariableContext unannTypeVariable() throws RecognitionException {
		UnannTypeVariableContext _localctx = new UnannTypeVariableContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_unannTypeVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1073);
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

	public final UnannArrayTypeContext unannArrayType() throws RecognitionException {
		UnannArrayTypeContext _localctx = new UnannArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_unannArrayType);
		try {
			setState(1084);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1075);
				unannPrimitiveType();
				setState(1076);
				dims();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1078);
				unannClassOrInterfaceType();
				setState(1079);
				dims();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1081);
				unannTypeVariable();
				setState(1082);
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
		enterRule(_localctx, 154, RULE_methodDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1086);
			methodModifiers();
			setState(1087);
			methodHeader();
			setState(1088);
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
		enterRule(_localctx, 156, RULE_methodModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1093);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << FINAL) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED))) != 0) || _la==AT) {
				{
				{
				setState(1090);
				methodModifier();
				}
				}
				setState(1095);
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
		enterRule(_localctx, 158, RULE_methodModifier);
		try {
			setState(1106);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1096);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1097);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1098);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1099);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1100);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(1101);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 7);
				{
				setState(1102);
				match(FINAL);
				}
				break;
			case SYNCHRONIZED:
				enterOuterAlt(_localctx, 8);
				{
				setState(1103);
				match(SYNCHRONIZED);
				}
				break;
			case NATIVE:
				enterOuterAlt(_localctx, 9);
				{
				setState(1104);
				match(NATIVE);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 10);
				{
				setState(1105);
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
		enterRule(_localctx, 160, RULE_methodHeader);
		int _la;
		try {
			setState(1125);
			_errHandler.sync(this);
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
				setState(1108);
				result();
				setState(1109);
				methodDeclarator();
				setState(1111);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==THROWS) {
					{
					setState(1110);
					throws_();
					}
				}

				}
				break;
			case LT:
				enterOuterAlt(_localctx, 2);
				{
				setState(1113);
				typeParameters();
				setState(1117);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(1114);
					annotation();
					}
					}
					setState(1119);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1120);
				result();
				setState(1121);
				methodDeclarator();
				setState(1123);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==THROWS) {
					{
					setState(1122);
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
		enterRule(_localctx, 162, RULE_result);
		try {
			setState(1129);
			_errHandler.sync(this);
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
				setState(1127);
				unannType();
				}
				break;
			case VOID:
				enterOuterAlt(_localctx, 2);
				{
				setState(1128);
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
		enterRule(_localctx, 164, RULE_methodDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1131);
			identifier();
			setState(1132);
			match(LPAREN);
			setState(1134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1133);
				formalParameterList();
				}
			}

			setState(1136);
			match(RPAREN);
			setState(1138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13 || _la==AT) {
				{
				setState(1137);
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
		enterRule(_localctx, 166, RULE_formalParameterList);
		try {
			setState(1145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1140);
				formalParameters();
				setState(1141);
				match(COMMA);
				setState(1142);
				lastFormalParameter();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1144);
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
		enterRule(_localctx, 168, RULE_formalParameters);
		try {
			int _alt;
			setState(1163);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1147);
				formalParameter();
				setState(1152);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1148);
						match(COMMA);
						setState(1149);
						formalParameter();
						}
						} 
					}
					setState(1154);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,90,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1155);
				receiverParameter();
				setState(1160);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,91,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(1156);
						match(COMMA);
						setState(1157);
						formalParameter();
						}
						} 
					}
					setState(1162);
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
		enterRule(_localctx, 170, RULE_formalParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1168);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1165);
				variableModifier();
				}
				}
				setState(1170);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1171);
			unannType();
			setState(1172);
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
		enterRule(_localctx, 172, RULE_variableModifier);
		try {
			setState(1176);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1174);
				annotation();
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(1175);
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
		enterRule(_localctx, 174, RULE_lastFormalParameter);
		int _la;
		try {
			setState(1195);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1181);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FINAL || _la==AT) {
					{
					{
					setState(1178);
					variableModifier();
					}
					}
					setState(1183);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1184);
				unannType();
				setState(1188);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==AT) {
					{
					{
					setState(1185);
					annotation();
					}
					}
					setState(1190);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1191);
				match(ELLIPSIS);
				setState(1192);
				variableDeclaratorId();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1194);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 176, RULE_receiverParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1197);
				annotation();
				}
				}
				setState(1202);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1203);
			unannType();
			setState(1207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) {
				{
				setState(1204);
				identifier();
				setState(1205);
				match(DOT);
				}
			}

			setState(1209);
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
		enterRule(_localctx, 178, RULE_throws_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1211);
			match(THROWS);
			setState(1212);
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
		enterRule(_localctx, 180, RULE_exceptionTypeList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1214);
			exceptionType();
			setState(1219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1215);
				match(COMMA);
				setState(1216);
				exceptionType();
				}
				}
				setState(1221);
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
		enterRule(_localctx, 182, RULE_exceptionType);
		try {
			setState(1224);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,101,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1222);
				classType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1223);
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
		enterRule(_localctx, 184, RULE_methodBody);
		try {
			setState(1228);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1226);
				block();
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
				setState(1227);
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
		enterRule(_localctx, 186, RULE_instanceInitializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1230);
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
		enterRule(_localctx, 188, RULE_staticInitializer);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1232);
			match(STATIC);
			setState(1233);
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
		enterRule(_localctx, 190, RULE_constructorDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1235);
			constructorModifiers();
			setState(1236);
			constructorDeclarator();
			setState(1238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==THROWS) {
				{
				setState(1237);
				throws_();
				}
			}

			setState(1240);
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
		enterRule(_localctx, 192, RULE_constructorModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC))) != 0) || _la==AT) {
				{
				{
				setState(1242);
				constructorModifier();
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
		enterRule(_localctx, 194, RULE_constructorModifier);
		try {
			setState(1252);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1248);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1249);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1250);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1251);
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
		enterRule(_localctx, 196, RULE_constructorDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1254);
				typeParameters();
				}
			}

			setState(1257);
			simpleTypeName();
			setState(1258);
			match(LPAREN);
			setState(1260);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1259);
				formalParameterList();
				}
			}

			setState(1262);
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

	public final SimpleTypeNameContext simpleTypeName() throws RecognitionException {
		SimpleTypeNameContext _localctx = new SimpleTypeNameContext(_ctx, getState());
		enterRule(_localctx, 198, RULE_simpleTypeName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1264);
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
		enterRule(_localctx, 200, RULE_constructorBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1266);
			match(LBRACE);
			setState(1268);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				{
				setState(1267);
				explicitConstructorInvocation();
				}
				break;
			}
			setState(1271);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (WHILE - 64)) | (1L << (IntegerLiteral - 64)) | (1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1270);
				blockStatements();
				}
			}

			setState(1273);
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
		enterRule(_localctx, 202, RULE_explicitConstructorInvocation);
		int _la;
		try {
			setState(1321);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1276);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1275);
					typeArguments();
					}
				}

				setState(1278);
				match(THIS);
				setState(1279);
				match(LPAREN);
				setState(1281);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(1280);
					argumentList();
					}
				}

				setState(1283);
				match(RPAREN);
				setState(1284);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1286);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1285);
					typeArguments();
					}
				}

				setState(1288);
				match(SUPER);
				setState(1289);
				match(LPAREN);
				setState(1291);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(1290);
					argumentList();
					}
				}

				setState(1293);
				match(RPAREN);
				setState(1294);
				match(SEMI);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1295);
				expressionName();
				setState(1296);
				match(DOT);
				setState(1298);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1297);
					typeArguments();
					}
				}

				setState(1300);
				match(SUPER);
				setState(1301);
				match(LPAREN);
				setState(1303);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(1302);
					argumentList();
					}
				}

				setState(1305);
				match(RPAREN);
				setState(1306);
				match(SEMI);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1308);
				primary();
				setState(1309);
				match(DOT);
				setState(1311);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(1310);
					typeArguments();
					}
				}

				setState(1313);
				match(SUPER);
				setState(1314);
				match(LPAREN);
				setState(1316);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(1315);
					argumentList();
					}
				}

				setState(1318);
				match(RPAREN);
				setState(1319);
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

	public final EnumDeclarationContext enumDeclaration() throws RecognitionException {
		EnumDeclarationContext _localctx = new EnumDeclarationContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_enumDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1323);
			classModifiers();
			setState(1324);
			match(ENUM);
			setState(1325);
			identifier();
			setState(1327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IMPLEMENTS) {
				{
				setState(1326);
				superinterfaces();
				}
			}

			setState(1329);
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
		enterRule(_localctx, 206, RULE_enumBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1331);
			match(LBRACE);
			setState(1333);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier || _la==AT) {
				{
				setState(1332);
				enumConstantList();
				}
			}

			setState(1336);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1335);
				match(COMMA);
				}
			}

			setState(1339);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(1338);
				enumBodyDeclarations();
				}
			}

			setState(1341);
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
		enterRule(_localctx, 208, RULE_enumConstantList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1343);
			enumConstant();
			setState(1348);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,123,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1344);
					match(COMMA);
					setState(1345);
					enumConstant();
					}
					} 
				}
				setState(1350);
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
		enterRule(_localctx, 210, RULE_enumConstant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1354);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1351);
				enumConstantModifier();
				}
				}
				setState(1356);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1357);
			identifier();
			setState(1363);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(1358);
				match(LPAREN);
				setState(1360);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(1359);
					argumentList();
					}
				}

				setState(1362);
				match(RPAREN);
				}
			}

			setState(1366);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LBRACE) {
				{
				setState(1365);
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
		enterRule(_localctx, 212, RULE_enumConstantModifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1368);
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
		enterRule(_localctx, 214, RULE_enumBodyDeclarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1370);
			match(SEMI);
			setState(1374);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << NATIVE) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SYNCHRONIZED) | (1L << TRANSIENT) | (1L << VOID) | (1L << VOLATILE))) != 0) || ((((_la - 73)) & ~0x3f) == 0 && ((1L << (_la - 73)) & ((1L << (LBRACE - 73)) | (1L << (SEMI - 73)) | (1L << (LT - 73)) | (1L << (Identifier - 73)) | (1L << (AT - 73)))) != 0)) {
				{
				{
				setState(1371);
				classBodyDeclaration();
				}
				}
				setState(1376);
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
		enterRule(_localctx, 216, RULE_interfaceDeclaration);
		try {
			setState(1379);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,129,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1377);
				normalInterfaceDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1378);
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

	public final NormalInterfaceDeclarationContext normalInterfaceDeclaration() throws RecognitionException {
		NormalInterfaceDeclarationContext _localctx = new NormalInterfaceDeclarationContext(_ctx, getState());
		enterRule(_localctx, 218, RULE_normalInterfaceDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1381);
			interfaceModifiers();
			setState(1382);
			match(INTERFACE);
			setState(1383);
			identifier();
			setState(1385);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(1384);
				typeParameters();
				}
			}

			setState(1388);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(1387);
				extendsInterfaces();
				}
			}

			setState(1390);
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
		enterRule(_localctx, 220, RULE_interfaceModifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1395);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(1392);
				interfaceModifier();
				}
				}
				setState(1397);
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
		enterRule(_localctx, 222, RULE_interfaceModifier);
		try {
			setState(1405);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1398);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1399);
				match(PUBLIC);
				}
				break;
			case PROTECTED:
				enterOuterAlt(_localctx, 3);
				{
				setState(1400);
				match(PROTECTED);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 4);
				{
				setState(1401);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1402);
				match(ABSTRACT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(1403);
				match(STATIC);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 7);
				{
				setState(1404);
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
		enterRule(_localctx, 224, RULE_extendsInterfaces);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1407);
			match(EXTENDS);
			setState(1408);
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
		enterRule(_localctx, 226, RULE_interfaceBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1410);
			match(LBRACE);
			setState(1414);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DEFAULT) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << VOID))) != 0) || ((((_la - 77)) & ~0x3f) == 0 && ((1L << (_la - 77)) & ((1L << (SEMI - 77)) | (1L << (LT - 77)) | (1L << (Identifier - 77)) | (1L << (AT - 77)))) != 0)) {
				{
				{
				setState(1411);
				interfaceMemberDeclaration();
				}
				}
				setState(1416);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1417);
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
		enterRule(_localctx, 228, RULE_interfaceMemberDeclaration);
		try {
			setState(1424);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,135,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1419);
				constantDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1420);
				interfaceMethodDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1421);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1422);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1423);
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
		enterRule(_localctx, 230, RULE_constantDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1429);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FINAL) | (1L << PUBLIC) | (1L << STATIC))) != 0) || _la==AT) {
				{
				{
				setState(1426);
				constantModifier();
				}
				}
				setState(1431);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1432);
			unannType();
			setState(1433);
			variableDeclaratorList();
			setState(1434);
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
		enterRule(_localctx, 232, RULE_constantModifier);
		try {
			setState(1440);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1436);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1437);
				match(PUBLIC);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 3);
				{
				setState(1438);
				match(STATIC);
				}
				break;
			case FINAL:
				enterOuterAlt(_localctx, 4);
				{
				setState(1439);
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
		enterRule(_localctx, 234, RULE_interfaceMethodDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1445);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ABSTRACT) | (1L << DEFAULT) | (1L << PRIVATE) | (1L << PUBLIC) | (1L << STATIC) | (1L << STRICTFP))) != 0) || _la==AT) {
				{
				{
				setState(1442);
				interfaceMethodModifier();
				}
				}
				setState(1447);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1448);
			methodHeader();
			setState(1449);
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
		public TerminalNode PRIVATE() { return getToken(Java8Parser.PRIVATE, 0); }
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
		enterRule(_localctx, 236, RULE_interfaceMethodModifier);
		try {
			setState(1458);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1451);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1452);
				match(PUBLIC);
				}
				break;
			case PRIVATE:
				enterOuterAlt(_localctx, 3);
				{
				setState(1453);
				match(PRIVATE);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 4);
				{
				setState(1454);
				match(ABSTRACT);
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 5);
				{
				setState(1455);
				match(DEFAULT);
				}
				break;
			case STATIC:
				enterOuterAlt(_localctx, 6);
				{
				setState(1456);
				match(STATIC);
				}
				break;
			case STRICTFP:
				enterOuterAlt(_localctx, 7);
				{
				setState(1457);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 238, RULE_annotationTypeDeclaration);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1463);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1460);
					interfaceModifier();
					}
					} 
				}
				setState(1465);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,140,_ctx);
			}
			setState(1466);
			match(AT);
			setState(1467);
			match(INTERFACE);
			setState(1468);
			identifier();
			setState(1469);
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
		enterRule(_localctx, 240, RULE_annotationTypeBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1471);
			match(LBRACE);
			setState(1475);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << ABSTRACT) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << INTERFACE) | (1L << LONG) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP))) != 0) || ((((_la - 77)) & ~0x3f) == 0 && ((1L << (_la - 77)) & ((1L << (SEMI - 77)) | (1L << (Identifier - 77)) | (1L << (AT - 77)))) != 0)) {
				{
				{
				setState(1472);
				annotationTypeMemberDeclaration();
				}
				}
				setState(1477);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1478);
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
		enterRule(_localctx, 242, RULE_annotationTypeMemberDeclaration);
		try {
			setState(1485);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,142,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1480);
				annotationTypeElementDeclaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1481);
				constantDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1482);
				classDeclaration();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1483);
				interfaceDeclaration();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1484);
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
		enterRule(_localctx, 244, RULE_annotationTypeElementDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1490);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ABSTRACT || _la==PUBLIC || _la==AT) {
				{
				{
				setState(1487);
				annotationTypeElementModifier();
				}
				}
				setState(1492);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1493);
			unannType();
			setState(1494);
			identifier();
			setState(1495);
			match(LPAREN);
			setState(1496);
			match(RPAREN);
			setState(1498);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13 || _la==AT) {
				{
				setState(1497);
				dims();
				}
			}

			setState(1501);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DEFAULT) {
				{
				setState(1500);
				defaultValue();
				}
			}

			setState(1503);
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
		enterRule(_localctx, 246, RULE_annotationTypeElementModifier);
		try {
			setState(1508);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(1505);
				annotation();
				}
				break;
			case PUBLIC:
				enterOuterAlt(_localctx, 2);
				{
				setState(1506);
				match(PUBLIC);
				}
				break;
			case ABSTRACT:
				enterOuterAlt(_localctx, 3);
				{
				setState(1507);
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
		enterRule(_localctx, 248, RULE_defaultValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1510);
			match(DEFAULT);
			setState(1511);
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
		enterRule(_localctx, 250, RULE_annotation);
		try {
			setState(1516);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,147,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1513);
				normalAnnotation();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1514);
				markerAnnotation();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1515);
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
		enterRule(_localctx, 252, RULE_annotationIdentifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1521);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1518);
				annotation();
				}
				}
				setState(1523);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1524);
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
		enterRule(_localctx, 254, RULE_annotationDim);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1529);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(1526);
				annotation();
				}
				}
				setState(1531);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1532);
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
		enterRule(_localctx, 256, RULE_normalAnnotation);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1534);
			match(AT);
			setState(1535);
			typeName();
			setState(1536);
			match(LPAREN);
			setState(1538);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) {
				{
				setState(1537);
				elementValuePairList();
				}
			}

			setState(1540);
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
		enterRule(_localctx, 258, RULE_elementValuePairList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1542);
			elementValuePair();
			setState(1547);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1543);
				match(COMMA);
				setState(1544);
				elementValuePair();
				}
				}
				setState(1549);
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
		enterRule(_localctx, 260, RULE_elementValuePair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1550);
			identifier();
			setState(1551);
			match(ASSIGN);
			setState(1552);
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
		enterRule(_localctx, 262, RULE_elementValue);
		try {
			setState(1557);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,152,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1554);
				conditionalExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1555);
				elementValueArrayInitializer();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1556);
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
		enterRule(_localctx, 264, RULE_elementValueArrayInitializer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1559);
			match(LBRACE);
			setState(1561);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (LBRACE - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1560);
				elementValueList();
				}
			}

			setState(1564);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1563);
				match(COMMA);
				}
			}

			setState(1566);
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
		enterRule(_localctx, 266, RULE_elementValueList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1568);
			elementValue();
			setState(1573);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,155,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1569);
					match(COMMA);
					setState(1570);
					elementValue();
					}
					} 
				}
				setState(1575);
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
		enterRule(_localctx, 268, RULE_markerAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1576);
			match(AT);
			setState(1577);
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
		enterRule(_localctx, 270, RULE_singleElementAnnotation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1579);
			match(AT);
			setState(1580);
			typeName();
			setState(1581);
			match(LPAREN);
			setState(1582);
			elementValue();
			setState(1583);
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
		enterRule(_localctx, 272, RULE_arrayInitializer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1585);
			match(LBRACE);
			setState(1587);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (LBRACE - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1586);
				variableInitializerList();
				}
			}

			setState(1590);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(1589);
				match(COMMA);
				}
			}

			setState(1592);
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
		enterRule(_localctx, 274, RULE_variableInitializerList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1594);
			variableInitializer();
			setState(1599);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,158,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1595);
					match(COMMA);
					setState(1596);
					variableInitializer();
					}
					} 
				}
				setState(1601);
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
		enterRule(_localctx, 276, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1602);
			match(LBRACE);
			setState(1604);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (WHILE - 64)) | (1L << (IntegerLiteral - 64)) | (1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				setState(1603);
				blockStatements();
				}
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
		enterRule(_localctx, 278, RULE_blockStatements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1608);
			blockStatement();
			setState(1612);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << ABSTRACT) | (1L << ASSERT) | (1L << BOOLEAN) | (1L << BREAK) | (1L << BYTE) | (1L << CHAR) | (1L << CLASS) | (1L << CONTINUE) | (1L << DO) | (1L << DOUBLE) | (1L << ENUM) | (1L << FINAL) | (1L << FLOAT) | (1L << FOR) | (1L << IF) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << PRIVATE) | (1L << PROTECTED) | (1L << PUBLIC) | (1L << RETURN) | (1L << SHORT) | (1L << STATIC) | (1L << STRICTFP) | (1L << SUPER) | (1L << SWITCH) | (1L << SYNCHRONIZED) | (1L << THIS) | (1L << THROW) | (1L << TRY) | (1L << VOID))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (WHILE - 64)) | (1L << (IntegerLiteral - 64)) | (1L << (FloatingPointLiteral - 64)) | (1L << (BooleanLiteral - 64)) | (1L << (CharacterLiteral - 64)) | (1L << (StringLiteral - 64)) | (1L << (NullLiteral - 64)) | (1L << (LPAREN - 64)) | (1L << (LBRACE - 64)) | (1L << (SEMI - 64)) | (1L << (INC - 64)) | (1L << (DEC - 64)) | (1L << (Identifier - 64)) | (1L << (AT - 64)))) != 0)) {
				{
				{
				setState(1609);
				blockStatement();
				}
				}
				setState(1614);
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
		enterRule(_localctx, 280, RULE_blockStatement);
		try {
			setState(1618);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,161,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1615);
				localVariableDeclarationStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1616);
				classDeclaration();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1617);
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
		enterRule(_localctx, 282, RULE_localVariableDeclarationStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1620);
			localVariableDeclaration();
			setState(1621);
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
		public LocalVariableTypeContext localVariableType() {
			return getRuleContext(LocalVariableTypeContext.class,0);
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
		enterRule(_localctx, 284, RULE_localVariableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1626);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1623);
				variableModifier();
				}
				}
				setState(1628);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1629);
			localVariableType();
			setState(1630);
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

	public static class LocalVariableTypeContext extends ParserRuleContext {
		public UnannTypeContext unannType() {
			return getRuleContext(UnannTypeContext.class,0);
		}
		public LocalVariableTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localVariableType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).enterLocalVariableType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java8Listener ) ((Java8Listener)listener).exitLocalVariableType(this);
		}
	}

	public final LocalVariableTypeContext localVariableType() throws RecognitionException {
		LocalVariableTypeContext _localctx = new LocalVariableTypeContext(_ctx, getState());
		enterRule(_localctx, 286, RULE_localVariableType);
		try {
			setState(1634);
			_errHandler.sync(this);
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
				setState(1632);
				unannType();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 2);
				{
				setState(1633);
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
		enterRule(_localctx, 288, RULE_statement);
		try {
			setState(1642);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,164,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1636);
				statementWithoutTrailingSubstatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1637);
				labeledStatement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1638);
				ifThenStatement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1639);
				ifThenElseStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1640);
				whileStatement();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1641);
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
		enterRule(_localctx, 290, RULE_statementNoShortIf);
		try {
			setState(1649);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,165,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1644);
				statementWithoutTrailingSubstatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1645);
				labeledStatementNoShortIf();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1646);
				ifThenElseStatementNoShortIf();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1647);
				whileStatementNoShortIf();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1648);
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
		enterRule(_localctx, 292, RULE_statementWithoutTrailingSubstatement);
		try {
			setState(1663);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACE:
				enterOuterAlt(_localctx, 1);
				{
				setState(1651);
				block();
				}
				break;
			case SEMI:
				enterOuterAlt(_localctx, 2);
				{
				setState(1652);
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
				setState(1653);
				expressionStatement();
				}
				break;
			case ASSERT:
				enterOuterAlt(_localctx, 4);
				{
				setState(1654);
				assertStatement();
				}
				break;
			case SWITCH:
				enterOuterAlt(_localctx, 5);
				{
				setState(1655);
				switchStatement();
				}
				break;
			case DO:
				enterOuterAlt(_localctx, 6);
				{
				setState(1656);
				doStatement();
				}
				break;
			case BREAK:
				enterOuterAlt(_localctx, 7);
				{
				setState(1657);
				breakStatement();
				}
				break;
			case CONTINUE:
				enterOuterAlt(_localctx, 8);
				{
				setState(1658);
				continueStatement();
				}
				break;
			case RETURN:
				enterOuterAlt(_localctx, 9);
				{
				setState(1659);
				returnStatement();
				}
				break;
			case SYNCHRONIZED:
				enterOuterAlt(_localctx, 10);
				{
				setState(1660);
				synchronizedStatement();
				}
				break;
			case THROW:
				enterOuterAlt(_localctx, 11);
				{
				setState(1661);
				throwStatement();
				}
				break;
			case TRY:
				enterOuterAlt(_localctx, 12);
				{
				setState(1662);
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
		enterRule(_localctx, 294, RULE_emptyStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1665);
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
		enterRule(_localctx, 296, RULE_labeledStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1667);
			identifier();
			setState(1668);
			match(COLON);
			setState(1669);
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
		enterRule(_localctx, 298, RULE_labeledStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1671);
			identifier();
			setState(1672);
			match(COLON);
			setState(1673);
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
		enterRule(_localctx, 300, RULE_expressionStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1675);
			statementExpression();
			setState(1676);
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
		enterRule(_localctx, 302, RULE_statementExpression);
		try {
			setState(1685);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,167,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1678);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1679);
				preIncrementExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1680);
				preDecrementExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1681);
				postIncrementExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(1682);
				postDecrementExpression();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(1683);
				methodInvocation();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(1684);
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
		enterRule(_localctx, 304, RULE_ifThenStatement);
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
		enterRule(_localctx, 306, RULE_ifThenElseStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1693);
			match(IF);
			setState(1694);
			match(LPAREN);
			setState(1695);
			expression();
			setState(1696);
			match(RPAREN);
			setState(1697);
			statementNoShortIf();
			setState(1698);
			match(ELSE);
			setState(1699);
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
		enterRule(_localctx, 308, RULE_ifThenElseStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1701);
			match(IF);
			setState(1702);
			match(LPAREN);
			setState(1703);
			expression();
			setState(1704);
			match(RPAREN);
			setState(1705);
			statementNoShortIf();
			setState(1706);
			match(ELSE);
			setState(1707);
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
		enterRule(_localctx, 310, RULE_assertStatement);
		try {
			setState(1719);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,168,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1709);
				match(ASSERT);
				setState(1710);
				expression();
				setState(1711);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1713);
				match(ASSERT);
				setState(1714);
				expression();
				setState(1715);
				match(COLON);
				setState(1716);
				expression();
				setState(1717);
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
		enterRule(_localctx, 312, RULE_switchStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1721);
			match(SWITCH);
			setState(1722);
			match(LPAREN);
			setState(1723);
			expression();
			setState(1724);
			match(RPAREN);
			setState(1725);
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
		enterRule(_localctx, 314, RULE_switchBlock);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1727);
			match(LBRACE);
			setState(1731);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,169,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1728);
					switchBlockStatementGroup();
					}
					} 
				}
				setState(1733);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,169,_ctx);
			}
			setState(1737);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CASE || _la==DEFAULT) {
				{
				{
				setState(1734);
				switchLabel();
				}
				}
				setState(1739);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1740);
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
		enterRule(_localctx, 316, RULE_switchBlockStatementGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1742);
			switchLabels();
			setState(1743);
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
		enterRule(_localctx, 318, RULE_switchLabels);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1745);
			switchLabel();
			setState(1749);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CASE || _la==DEFAULT) {
				{
				{
				setState(1746);
				switchLabel();
				}
				}
				setState(1751);
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
		enterRule(_localctx, 320, RULE_switchLabel);
		try {
			setState(1762);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,172,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1752);
				match(CASE);
				setState(1753);
				constantExpression();
				setState(1754);
				match(COLON);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1756);
				match(CASE);
				setState(1757);
				enumConstantName();
				setState(1758);
				match(COLON);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1760);
				match(DEFAULT);
				setState(1761);
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

	public final EnumConstantNameContext enumConstantName() throws RecognitionException {
		EnumConstantNameContext _localctx = new EnumConstantNameContext(_ctx, getState());
		enterRule(_localctx, 322, RULE_enumConstantName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1764);
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
		enterRule(_localctx, 324, RULE_whileStatement);
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
		enterRule(_localctx, 326, RULE_whileStatementNoShortIf);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1772);
			match(WHILE);
			setState(1773);
			match(LPAREN);
			setState(1774);
			expression();
			setState(1775);
			match(RPAREN);
			setState(1776);
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
		enterRule(_localctx, 328, RULE_doStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1778);
			match(DO);
			setState(1779);
			statement();
			setState(1780);
			match(WHILE);
			setState(1781);
			match(LPAREN);
			setState(1782);
			expression();
			setState(1783);
			match(RPAREN);
			setState(1784);
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
		enterRule(_localctx, 330, RULE_forStatement);
		try {
			setState(1788);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,173,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1786);
				basicForStatement();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1787);
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
		enterRule(_localctx, 332, RULE_forStatementNoShortIf);
		try {
			setState(1792);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,174,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1790);
				basicForStatementNoShortIf();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1791);
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
		enterRule(_localctx, 334, RULE_basicForStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1794);
			match(FOR);
			setState(1795);
			match(LPAREN);
			setState(1797);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1796);
				forInit();
				}
			}

			setState(1799);
			match(SEMI);
			setState(1801);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1800);
				expression();
				}
			}

			setState(1803);
			match(SEMI);
			setState(1805);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1804);
				forUpdate();
				}
			}

			setState(1807);
			match(RPAREN);
			setState(1808);
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
		enterRule(_localctx, 336, RULE_basicForStatementNoShortIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1810);
			match(FOR);
			setState(1811);
			match(LPAREN);
			setState(1813);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1812);
				forInit();
				}
			}

			setState(1815);
			match(SEMI);
			setState(1817);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1816);
				expression();
				}
			}

			setState(1819);
			match(SEMI);
			setState(1821);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1820);
				forUpdate();
				}
			}

			setState(1823);
			match(RPAREN);
			setState(1824);
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
		enterRule(_localctx, 338, RULE_forInit);
		try {
			setState(1828);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,181,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1826);
				statementExpressionList();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1827);
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
		enterRule(_localctx, 340, RULE_forUpdate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1830);
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
		enterRule(_localctx, 342, RULE_statementExpressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1832);
			statementExpression();
			setState(1837);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(1833);
				match(COMMA);
				setState(1834);
				statementExpression();
				}
				}
				setState(1839);
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
		enterRule(_localctx, 344, RULE_enhancedForStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1840);
			match(FOR);
			setState(1841);
			match(LPAREN);
			setState(1845);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1842);
				variableModifier();
				}
				}
				setState(1847);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1848);
			unannType();
			setState(1849);
			variableDeclaratorId();
			setState(1850);
			match(COLON);
			setState(1851);
			expression();
			setState(1852);
			match(RPAREN);
			setState(1853);
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
		enterRule(_localctx, 346, RULE_enhancedForStatementNoShortIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1855);
			match(FOR);
			setState(1856);
			match(LPAREN);
			setState(1860);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1857);
				variableModifier();
				}
				}
				setState(1862);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1863);
			unannType();
			setState(1864);
			variableDeclaratorId();
			setState(1865);
			match(COLON);
			setState(1866);
			expression();
			setState(1867);
			match(RPAREN);
			setState(1868);
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

	public final BreakStatementContext breakStatement() throws RecognitionException {
		BreakStatementContext _localctx = new BreakStatementContext(_ctx, getState());
		enterRule(_localctx, 348, RULE_breakStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1870);
			match(BREAK);
			setState(1872);
			_errHandler.sync(this);
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

	public static class ContinueStatementContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(Java8Parser.CONTINUE, 0); }
		public TerminalNode SEMI() { return getToken(Java8Parser.SEMI, 0); }
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

	public final ContinueStatementContext continueStatement() throws RecognitionException {
		ContinueStatementContext _localctx = new ContinueStatementContext(_ctx, getState());
		enterRule(_localctx, 350, RULE_continueStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1876);
			match(CONTINUE);
			setState(1878);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9))) != 0) || _la==Identifier) {
				{
				setState(1877);
				identifier();
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
		enterRule(_localctx, 352, RULE_returnStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1882);
			match(RETURN);
			setState(1884);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(1883);
				expression();
				}
			}

			setState(1886);
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
		enterRule(_localctx, 354, RULE_throwStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1888);
			match(THROW);
			setState(1889);
			expression();
			setState(1890);
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
		enterRule(_localctx, 356, RULE_synchronizedStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1892);
			match(SYNCHRONIZED);
			setState(1893);
			match(LPAREN);
			setState(1894);
			expression();
			setState(1895);
			match(RPAREN);
			setState(1896);
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
		enterRule(_localctx, 358, RULE_tryStatement);
		int _la;
		try {
			setState(1910);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,189,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1898);
				match(TRY);
				setState(1899);
				block();
				setState(1900);
				catches();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1902);
				match(TRY);
				setState(1903);
				block();
				setState(1905);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==CATCH) {
					{
					setState(1904);
					catches();
					}
				}

				setState(1907);
				finally_();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1909);
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
		enterRule(_localctx, 360, RULE_catches);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1912);
			catchClause();
			setState(1916);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CATCH) {
				{
				{
				setState(1913);
				catchClause();
				}
				}
				setState(1918);
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
		enterRule(_localctx, 362, RULE_catchClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1919);
			match(CATCH);
			setState(1920);
			match(LPAREN);
			setState(1921);
			catchFormalParameter();
			setState(1922);
			match(RPAREN);
			setState(1923);
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
		enterRule(_localctx, 364, RULE_catchFormalParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1928);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FINAL || _la==AT) {
				{
				{
				setState(1925);
				variableModifier();
				}
				}
				setState(1930);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(1931);
			catchType();
			setState(1932);
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
		enterRule(_localctx, 366, RULE_catchType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1934);
			unannClassType();
			setState(1939);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BITOR) {
				{
				{
				setState(1935);
				match(BITOR);
				setState(1936);
				classType();
				}
				}
				setState(1941);
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
		enterRule(_localctx, 368, RULE_finally_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1942);
			match(FINALLY);
			setState(1943);
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
		enterRule(_localctx, 370, RULE_tryWithResourcesStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1945);
			match(TRY);
			setState(1946);
			resourceSpecification();
			setState(1947);
			block();
			setState(1949);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CATCH) {
				{
				setState(1948);
				catches();
				}
			}

			setState(1952);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FINALLY) {
				{
				setState(1951);
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
		enterRule(_localctx, 372, RULE_resourceSpecification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1954);
			match(LPAREN);
			setState(1955);
			resourceList();
			setState(1957);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMI) {
				{
				setState(1956);
				match(SEMI);
				}
			}

			setState(1959);
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
		enterRule(_localctx, 374, RULE_resourceList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1961);
			resource();
			setState(1966);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,196,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1962);
					match(SEMI);
					setState(1963);
					resource();
					}
					} 
				}
				setState(1968);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,196,_ctx);
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

	public final ResourceContext resource() throws RecognitionException {
		ResourceContext _localctx = new ResourceContext(_ctx, getState());
		enterRule(_localctx, 376, RULE_resource);
		int _la;
		try {
			setState(1981);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,198,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1972);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==FINAL || _la==AT) {
					{
					{
					setState(1969);
					variableModifier();
					}
					}
					setState(1974);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(1975);
				unannType();
				setState(1976);
				variableDeclaratorId();
				setState(1977);
				match(ASSIGN);
				setState(1978);
				expression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1980);
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

	public final VariableAccessContext variableAccess() throws RecognitionException {
		VariableAccessContext _localctx = new VariableAccessContext(_ctx, getState());
		enterRule(_localctx, 378, RULE_variableAccess);
		try {
			setState(1985);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,199,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1983);
				expressionName();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1984);
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
		enterRule(_localctx, 380, RULE_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(1989);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,200,_ctx) ) {
			case 1:
				{
				setState(1987);
				primaryNoNewArray_lfno_primary();
				}
				break;
			case 2:
				{
				setState(1988);
				arrayCreationExpression();
				}
				break;
			}
			setState(1994);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,201,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(1991);
					primaryNoNewArray_lf_primary();
					}
					} 
				}
				setState(1996);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,201,_ctx);
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
		enterRule(_localctx, 382, RULE_primaryNoNewArray);
		int _la;
		try {
			setState(2025);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,203,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1997);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1998);
				typeName();
				setState(2002);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__13) {
					{
					{
					setState(1999);
					squareBrackets();
					}
					}
					setState(2004);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2005);
				match(DOT);
				setState(2006);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2008);
				match(VOID);
				setState(2009);
				match(DOT);
				setState(2010);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2011);
				match(THIS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2012);
				typeName();
				setState(2013);
				match(DOT);
				setState(2014);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2016);
				match(LPAREN);
				setState(2017);
				expression();
				setState(2018);
				match(RPAREN);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2020);
				classInstanceCreationExpression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2021);
				fieldAccess();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2022);
				arrayAccess();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2023);
				methodInvocation();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2024);
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
		enterRule(_localctx, 384, RULE_primaryNoNewArray_lf_arrayAccess);
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
		enterRule(_localctx, 386, RULE_primaryNoNewArray_lfno_arrayAccess);
		int _la;
		try {
			setState(2056);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,205,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2029);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2030);
				typeName();
				setState(2034);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__13) {
					{
					{
					setState(2031);
					squareBrackets();
					}
					}
					setState(2036);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2037);
				match(DOT);
				setState(2038);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2040);
				match(VOID);
				setState(2041);
				match(DOT);
				setState(2042);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2043);
				match(THIS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2044);
				typeName();
				setState(2045);
				match(DOT);
				setState(2046);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2048);
				match(LPAREN);
				setState(2049);
				expression();
				setState(2050);
				match(RPAREN);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2052);
				classInstanceCreationExpression();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2053);
				fieldAccess();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2054);
				methodInvocation();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2055);
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
		enterRule(_localctx, 388, RULE_primaryNoNewArray_lf_primary);
		try {
			setState(2063);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,206,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2058);
				classInstanceCreationExpression_lf_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2059);
				fieldAccess_lf_primary();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2060);
				arrayAccess_lf_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2061);
				methodInvocation_lf_primary();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2062);
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
		enterRule(_localctx, 390, RULE_primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary);
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
		enterRule(_localctx, 392, RULE_primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary);
		try {
			setState(2071);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,207,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2067);
				classInstanceCreationExpression_lf_primary();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2068);
				fieldAccess_lf_primary();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2069);
				methodInvocation_lf_primary();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2070);
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
		enterRule(_localctx, 394, RULE_primaryNoNewArray_lfno_primary);
		int _la;
		try {
			setState(2111);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,210,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2073);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2074);
				typeName();
				setState(2078);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__13) {
					{
					{
					setState(2075);
					squareBrackets();
					}
					}
					setState(2080);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2081);
				match(DOT);
				setState(2082);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2084);
				unannPrimitiveType();
				setState(2088);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__13) {
					{
					{
					setState(2085);
					squareBrackets();
					}
					}
					setState(2090);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2091);
				match(DOT);
				setState(2092);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2094);
				match(VOID);
				setState(2095);
				match(DOT);
				setState(2096);
				match(CLASS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2097);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2098);
				typeName();
				setState(2099);
				match(DOT);
				setState(2100);
				match(THIS);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2102);
				match(LPAREN);
				setState(2103);
				expression();
				setState(2104);
				match(RPAREN);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2106);
				classInstanceCreationExpression_lfno_primary();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2107);
				fieldAccess_lfno_primary();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2108);
				arrayAccess_lfno_primary();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2109);
				methodInvocation_lfno_primary();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(2110);
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
		enterRule(_localctx, 396, RULE_primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary);
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
		enterRule(_localctx, 398, RULE_primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary);
		int _la;
		try {
			setState(2152);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,213,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2115);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2116);
				typeName();
				setState(2120);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__13) {
					{
					{
					setState(2117);
					squareBrackets();
					}
					}
					setState(2122);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2123);
				match(DOT);
				setState(2124);
				match(CLASS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2126);
				unannPrimitiveType();
				setState(2130);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__13) {
					{
					{
					setState(2127);
					squareBrackets();
					}
					}
					setState(2132);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2133);
				match(DOT);
				setState(2134);
				match(CLASS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2136);
				match(VOID);
				setState(2137);
				match(DOT);
				setState(2138);
				match(CLASS);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2139);
				match(THIS);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2140);
				typeName();
				setState(2141);
				match(DOT);
				setState(2142);
				match(THIS);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2144);
				match(LPAREN);
				setState(2145);
				expression();
				setState(2146);
				match(RPAREN);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(2148);
				classInstanceCreationExpression_lfno_primary();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(2149);
				fieldAccess_lfno_primary();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(2150);
				methodInvocation_lfno_primary();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(2151);
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
		enterRule(_localctx, 400, RULE_classInstanceCreationExpression);
		int _la;
		try {
			setState(2213);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,227,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2154);
				match(NEW);
				setState(2156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2155);
					typeArguments();
					}
				}

				setState(2158);
				annotationIdentifier();
				setState(2163);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(2159);
					match(DOT);
					setState(2160);
					annotationIdentifier();
					}
					}
					setState(2165);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2167);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || _la==LT) {
					{
					setState(2166);
					typeArgumentsOrDiamond();
					}
				}

				setState(2169);
				match(LPAREN);
				setState(2171);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2170);
					argumentList();
					}
				}

				setState(2173);
				match(RPAREN);
				setState(2175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2174);
					classBody();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2177);
				expressionName();
				setState(2178);
				match(DOT);
				setState(2179);
				match(NEW);
				setState(2181);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2180);
					typeArguments();
					}
				}

				setState(2183);
				annotationIdentifier();
				setState(2185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || _la==LT) {
					{
					setState(2184);
					typeArgumentsOrDiamond();
					}
				}

				setState(2187);
				match(LPAREN);
				setState(2189);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2188);
					argumentList();
					}
				}

				setState(2191);
				match(RPAREN);
				setState(2193);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2192);
					classBody();
					}
				}

				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2195);
				primary();
				setState(2196);
				match(DOT);
				setState(2197);
				match(NEW);
				setState(2199);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2198);
					typeArguments();
					}
				}

				setState(2201);
				annotationIdentifier();
				setState(2203);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || _la==LT) {
					{
					setState(2202);
					typeArgumentsOrDiamond();
					}
				}

				setState(2205);
				match(LPAREN);
				setState(2207);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2206);
					argumentList();
					}
				}

				setState(2209);
				match(RPAREN);
				setState(2211);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LBRACE) {
					{
					setState(2210);
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
		enterRule(_localctx, 402, RULE_classInstanceCreationExpression_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2215);
			match(DOT);
			setState(2216);
			match(NEW);
			setState(2218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2217);
				typeArguments();
				}
			}

			setState(2220);
			annotationIdentifier();
			setState(2222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11 || _la==LT) {
				{
				setState(2221);
				typeArgumentsOrDiamond();
				}
			}

			setState(2224);
			match(LPAREN);
			setState(2226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(2225);
				argumentList();
				}
			}

			setState(2228);
			match(RPAREN);
			setState(2230);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,231,_ctx) ) {
			case 1:
				{
				setState(2229);
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
		enterRule(_localctx, 404, RULE_classInstanceCreationExpression_lfno_primary);
		int _la;
		try {
			setState(2273);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NEW:
				enterOuterAlt(_localctx, 1);
				{
				setState(2232);
				match(NEW);
				setState(2234);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2233);
					typeArguments();
					}
				}

				setState(2236);
				annotationIdentifier();
				setState(2241);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==DOT) {
					{
					{
					setState(2237);
					match(DOT);
					setState(2238);
					annotationIdentifier();
					}
					}
					setState(2243);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2245);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || _la==LT) {
					{
					setState(2244);
					typeArgumentsOrDiamond();
					}
				}

				setState(2247);
				match(LPAREN);
				setState(2249);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2248);
					argumentList();
					}
				}

				setState(2251);
				match(RPAREN);
				setState(2253);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,236,_ctx) ) {
				case 1:
					{
					setState(2252);
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
				setState(2255);
				expressionName();
				setState(2256);
				match(DOT);
				setState(2257);
				match(NEW);
				setState(2259);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2258);
					typeArguments();
					}
				}

				setState(2261);
				annotationIdentifier();
				setState(2263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11 || _la==LT) {
					{
					setState(2262);
					typeArgumentsOrDiamond();
					}
				}

				setState(2265);
				match(LPAREN);
				setState(2267);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2266);
					argumentList();
					}
				}

				setState(2269);
				match(RPAREN);
				setState(2271);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,240,_ctx) ) {
				case 1:
					{
					setState(2270);
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
		enterRule(_localctx, 406, RULE_typeArgumentsOrDiamond);
		try {
			setState(2277);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LT:
				enterOuterAlt(_localctx, 1);
				{
				setState(2275);
				typeArguments();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(2276);
				match(T__11);
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

	public final FieldAccessContext fieldAccess() throws RecognitionException {
		FieldAccessContext _localctx = new FieldAccessContext(_ctx, getState());
		enterRule(_localctx, 408, RULE_fieldAccess);
		try {
			setState(2292);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,243,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2279);
				primary();
				setState(2280);
				match(DOT);
				setState(2281);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2283);
				match(SUPER);
				setState(2284);
				match(DOT);
				setState(2285);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2286);
				typeName();
				setState(2287);
				match(DOT);
				setState(2288);
				match(SUPER);
				setState(2289);
				match(DOT);
				setState(2290);
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
		public TerminalNode DOT() { return getToken(Java8Parser.DOT, 0); }
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

	public final FieldAccess_lf_primaryContext fieldAccess_lf_primary() throws RecognitionException {
		FieldAccess_lf_primaryContext _localctx = new FieldAccess_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 410, RULE_fieldAccess_lf_primary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2294);
			match(DOT);
			setState(2295);
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
		public List<TerminalNode> DOT() { return getTokens(Java8Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(Java8Parser.DOT, i);
		}
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

	public final FieldAccess_lfno_primaryContext fieldAccess_lfno_primary() throws RecognitionException {
		FieldAccess_lfno_primaryContext _localctx = new FieldAccess_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 412, RULE_fieldAccess_lfno_primary);
		try {
			setState(2306);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SUPER:
				enterOuterAlt(_localctx, 1);
				{
				setState(2297);
				match(SUPER);
				setState(2298);
				match(DOT);
				setState(2299);
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
				setState(2300);
				typeName();
				setState(2301);
				match(DOT);
				setState(2302);
				match(SUPER);
				setState(2303);
				match(DOT);
				setState(2304);
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
		enterRule(_localctx, 414, RULE_arrayAccess);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2318);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,245,_ctx) ) {
			case 1:
				{
				setState(2308);
				expressionName();
				setState(2309);
				match(LBRACK);
				setState(2310);
				expression();
				setState(2311);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2313);
				primaryNoNewArray_lfno_arrayAccess();
				setState(2314);
				match(LBRACK);
				setState(2315);
				expression();
				setState(2316);
				match(RBRACK);
				}
				break;
			}
			setState(2327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(2320);
				primaryNoNewArray_lf_arrayAccess();
				setState(2321);
				match(LBRACK);
				setState(2322);
				expression();
				setState(2323);
				match(RBRACK);
				}
				}
				setState(2329);
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
		enterRule(_localctx, 416, RULE_arrayAccess_lf_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2330);
			primaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary();
			setState(2331);
			match(LBRACK);
			setState(2332);
			expression();
			setState(2333);
			match(RBRACK);
			}
			setState(2342);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,247,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2335);
					primaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary();
					setState(2336);
					match(LBRACK);
					setState(2337);
					expression();
					setState(2338);
					match(RBRACK);
					}
					} 
				}
				setState(2344);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,247,_ctx);
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
		enterRule(_localctx, 418, RULE_arrayAccess_lfno_primary);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2355);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,248,_ctx) ) {
			case 1:
				{
				setState(2345);
				expressionName();
				setState(2346);
				match(LBRACK);
				setState(2347);
				expression();
				setState(2348);
				match(RBRACK);
				}
				break;
			case 2:
				{
				setState(2350);
				primaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary();
				setState(2351);
				match(LBRACK);
				setState(2352);
				expression();
				setState(2353);
				match(RBRACK);
				}
				break;
			}
			setState(2364);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,249,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2357);
					primaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary();
					setState(2358);
					match(LBRACK);
					setState(2359);
					expression();
					setState(2360);
					match(RBRACK);
					}
					} 
				}
				setState(2366);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,249,_ctx);
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

	public final MethodInvocationContext methodInvocation() throws RecognitionException {
		MethodInvocationContext _localctx = new MethodInvocationContext(_ctx, getState());
		enterRule(_localctx, 420, RULE_methodInvocation);
		int _la;
		try {
			setState(2436);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,261,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2367);
				methodName();
				setState(2368);
				match(LPAREN);
				setState(2370);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2369);
					argumentList();
					}
				}

				setState(2372);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2374);
				typeName();
				setState(2375);
				match(DOT);
				setState(2377);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2376);
					typeArguments();
					}
				}

				setState(2379);
				identifier();
				setState(2380);
				match(LPAREN);
				setState(2382);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2381);
					argumentList();
					}
				}

				setState(2384);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2386);
				expressionName();
				setState(2387);
				match(DOT);
				setState(2389);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2388);
					typeArguments();
					}
				}

				setState(2391);
				identifier();
				setState(2392);
				match(LPAREN);
				setState(2394);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2393);
					argumentList();
					}
				}

				setState(2396);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2398);
				primary();
				setState(2399);
				match(DOT);
				setState(2401);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2400);
					typeArguments();
					}
				}

				setState(2403);
				identifier();
				setState(2404);
				match(LPAREN);
				setState(2406);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2405);
					argumentList();
					}
				}

				setState(2408);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2410);
				match(SUPER);
				setState(2411);
				match(DOT);
				setState(2413);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2412);
					typeArguments();
					}
				}

				setState(2415);
				identifier();
				setState(2416);
				match(LPAREN);
				setState(2418);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2417);
					argumentList();
					}
				}

				setState(2420);
				match(RPAREN);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2422);
				typeName();
				setState(2423);
				match(DOT);
				setState(2424);
				match(SUPER);
				setState(2425);
				match(DOT);
				setState(2427);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2426);
					typeArguments();
					}
				}

				setState(2429);
				identifier();
				setState(2430);
				match(LPAREN);
				setState(2432);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2431);
					argumentList();
					}
				}

				setState(2434);
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
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
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
		enterRule(_localctx, 422, RULE_methodInvocation_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2438);
			match(DOT);
			setState(2440);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2439);
				typeArguments();
				}
			}

			setState(2442);
			identifier();
			setState(2443);
			match(LPAREN);
			setState(2445);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
				{
				setState(2444);
				argumentList();
				}
			}

			setState(2447);
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

	public final MethodInvocation_lfno_primaryContext methodInvocation_lfno_primary() throws RecognitionException {
		MethodInvocation_lfno_primaryContext _localctx = new MethodInvocation_lfno_primaryContext(_ctx, getState());
		enterRule(_localctx, 424, RULE_methodInvocation_lfno_primary);
		int _la;
		try {
			setState(2506);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,273,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2449);
				methodName();
				setState(2450);
				match(LPAREN);
				setState(2452);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2451);
					argumentList();
					}
				}

				setState(2454);
				match(RPAREN);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2456);
				typeName();
				setState(2457);
				match(DOT);
				setState(2459);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2458);
					typeArguments();
					}
				}

				setState(2461);
				identifier();
				setState(2462);
				match(LPAREN);
				setState(2464);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2463);
					argumentList();
					}
				}

				setState(2466);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2468);
				expressionName();
				setState(2469);
				match(DOT);
				setState(2471);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2470);
					typeArguments();
					}
				}

				setState(2473);
				identifier();
				setState(2474);
				match(LPAREN);
				setState(2476);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2475);
					argumentList();
					}
				}

				setState(2478);
				match(RPAREN);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2480);
				match(SUPER);
				setState(2481);
				match(DOT);
				setState(2483);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2482);
					typeArguments();
					}
				}

				setState(2485);
				identifier();
				setState(2486);
				match(LPAREN);
				setState(2488);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2487);
					argumentList();
					}
				}

				setState(2490);
				match(RPAREN);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2492);
				typeName();
				setState(2493);
				match(DOT);
				setState(2494);
				match(SUPER);
				setState(2495);
				match(DOT);
				setState(2497);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2496);
					typeArguments();
					}
				}

				setState(2499);
				identifier();
				setState(2500);
				match(LPAREN);
				setState(2502);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__12) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << NEW) | (1L << SHORT) | (1L << SUPER) | (1L << THIS) | (1L << VOID))) != 0) || ((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (IntegerLiteral - 65)) | (1L << (FloatingPointLiteral - 65)) | (1L << (BooleanLiteral - 65)) | (1L << (CharacterLiteral - 65)) | (1L << (StringLiteral - 65)) | (1L << (NullLiteral - 65)) | (1L << (LPAREN - 65)) | (1L << (BANG - 65)) | (1L << (TILDE - 65)) | (1L << (INC - 65)) | (1L << (DEC - 65)) | (1L << (ADD - 65)) | (1L << (Identifier - 65)) | (1L << (AT - 65)))) != 0)) {
					{
					setState(2501);
					argumentList();
					}
				}

				setState(2504);
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
		enterRule(_localctx, 426, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2508);
			expression();
			setState(2513);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2509);
				match(COMMA);
				setState(2510);
				expression();
				}
				}
				setState(2515);
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
		enterRule(_localctx, 428, RULE_methodReference);
		int _la;
		try {
			setState(2563);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,281,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2516);
				expressionName();
				setState(2517);
				match(COLONCOLON);
				setState(2519);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2518);
					typeArguments();
					}
				}

				setState(2521);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2523);
				referenceType();
				setState(2524);
				match(COLONCOLON);
				setState(2526);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2525);
					typeArguments();
					}
				}

				setState(2528);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2530);
				primary();
				setState(2531);
				match(COLONCOLON);
				setState(2533);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2532);
					typeArguments();
					}
				}

				setState(2535);
				identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2537);
				match(SUPER);
				setState(2538);
				match(COLONCOLON);
				setState(2540);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2539);
					typeArguments();
					}
				}

				setState(2542);
				identifier();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2543);
				typeName();
				setState(2544);
				match(DOT);
				setState(2545);
				match(SUPER);
				setState(2546);
				match(COLONCOLON);
				setState(2548);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2547);
					typeArguments();
					}
				}

				setState(2550);
				identifier();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2552);
				classType();
				setState(2553);
				match(COLONCOLON);
				setState(2555);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2554);
					typeArguments();
					}
				}

				setState(2557);
				match(NEW);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(2559);
				arrayType();
				setState(2560);
				match(COLONCOLON);
				setState(2561);
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

	public final MethodReference_lf_primaryContext methodReference_lf_primary() throws RecognitionException {
		MethodReference_lf_primaryContext _localctx = new MethodReference_lf_primaryContext(_ctx, getState());
		enterRule(_localctx, 430, RULE_methodReference_lf_primary);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2565);
			match(COLONCOLON);
			setState(2567);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(2566);
				typeArguments();
				}
			}

			setState(2569);
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
		public TerminalNode COLONCOLON() { return getToken(Java8Parser.COLONCOLON, 0); }
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
		enterRule(_localctx, 432, RULE_methodReference_lfno_primary);
		int _la;
		try {
			setState(2611);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,288,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2571);
				expressionName();
				setState(2572);
				match(COLONCOLON);
				setState(2574);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2573);
					typeArguments();
					}
				}

				setState(2576);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2578);
				referenceType();
				setState(2579);
				match(COLONCOLON);
				setState(2581);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2580);
					typeArguments();
					}
				}

				setState(2583);
				identifier();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2585);
				match(SUPER);
				setState(2586);
				match(COLONCOLON);
				setState(2588);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2587);
					typeArguments();
					}
				}

				setState(2590);
				identifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2591);
				typeName();
				setState(2592);
				match(DOT);
				setState(2593);
				match(SUPER);
				setState(2594);
				match(COLONCOLON);
				setState(2596);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2595);
					typeArguments();
					}
				}

				setState(2598);
				identifier();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2600);
				classType();
				setState(2601);
				match(COLONCOLON);
				setState(2603);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==LT) {
					{
					setState(2602);
					typeArguments();
					}
				}

				setState(2605);
				match(NEW);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(2607);
				arrayType();
				setState(2608);
				match(COLONCOLON);
				setState(2609);
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
		enterRule(_localctx, 434, RULE_arrayCreationExpression);
		try {
			setState(2635);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,291,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2613);
				match(NEW);
				setState(2614);
				primitiveType();
				setState(2615);
				dimExprs();
				setState(2617);
				_errHandler.sync(this);
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
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2619);
				match(NEW);
				setState(2620);
				classOrInterfaceType();
				setState(2621);
				dimExprs();
				setState(2623);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,290,_ctx) ) {
				case 1:
					{
					setState(2622);
					dims();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2625);
				match(NEW);
				setState(2626);
				primitiveType();
				setState(2627);
				dims();
				setState(2628);
				arrayInitializer();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2630);
				match(NEW);
				setState(2631);
				classOrInterfaceType();
				setState(2632);
				dims();
				setState(2633);
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
		enterRule(_localctx, 436, RULE_dimExprs);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2637);
			dimExpr();
			setState(2641);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,292,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(2638);
					dimExpr();
					}
					} 
				}
				setState(2643);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,292,_ctx);
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
		enterRule(_localctx, 438, RULE_dimExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2647);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AT) {
				{
				{
				setState(2644);
				annotation();
				}
				}
				setState(2649);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(2650);
			match(LBRACK);
			setState(2651);
			expression();
			setState(2652);
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
		enterRule(_localctx, 440, RULE_constantExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2654);
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
		enterRule(_localctx, 442, RULE_expression);
		try {
			setState(2658);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,294,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2656);
				lambdaExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2657);
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
		enterRule(_localctx, 444, RULE_lambdaExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2660);
			lambdaParameters();
			setState(2661);
			match(ARROW);
			setState(2662);
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
		enterRule(_localctx, 446, RULE_lambdaParameters);
		int _la;
		try {
			setState(2674);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,296,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2664);
				identifier();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2665);
				match(LPAREN);
				setState(2667);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << BOOLEAN) | (1L << BYTE) | (1L << CHAR) | (1L << DOUBLE) | (1L << FINAL) | (1L << FLOAT) | (1L << INT) | (1L << LONG) | (1L << SHORT))) != 0) || _la==Identifier || _la==AT) {
					{
					setState(2666);
					formalParameterList();
					}
				}

				setState(2669);
				match(RPAREN);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2670);
				match(LPAREN);
				setState(2671);
				inferredFormalParameterList();
				setState(2672);
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
		public List<IdentifierContext> identifier() {
			return getRuleContexts(IdentifierContext.class);
		}
		public IdentifierContext identifier(int i) {
			return getRuleContext(IdentifierContext.class,i);
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
		enterRule(_localctx, 448, RULE_inferredFormalParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2676);
			identifier();
			setState(2681);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(2677);
				match(COMMA);
				setState(2678);
				identifier();
				}
				}
				setState(2683);
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
		enterRule(_localctx, 450, RULE_lambdaBody);
		try {
			setState(2686);
			_errHandler.sync(this);
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
			case T__12:
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
			case Identifier:
			case AT:
				enterOuterAlt(_localctx, 1);
				{
				setState(2684);
				expression();
				}
				break;
			case LBRACE:
				enterOuterAlt(_localctx, 2);
				{
				setState(2685);
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
		enterRule(_localctx, 452, RULE_assignmentExpression);
		try {
			setState(2690);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,299,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2688);
				conditionalExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2689);
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
		enterRule(_localctx, 454, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2692);
			leftHandSide();
			setState(2693);
			assignmentOperator();
			setState(2694);
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

	public final LeftHandSideContext leftHandSide() throws RecognitionException {
		LeftHandSideContext _localctx = new LeftHandSideContext(_ctx, getState());
		enterRule(_localctx, 456, RULE_leftHandSide);
		try {
			setState(2698);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,300,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2696);
				variableAccess();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2697);
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
		enterRule(_localctx, 458, RULE_assignmentOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2700);
			_la = _input.LA(1);
			if ( !(((((_la - 80)) & ~0x3f) == 0 && ((1L << (_la - 80)) & ((1L << (ASSIGN - 80)) | (1L << (ADD_ASSIGN - 80)) | (1L << (SUB_ASSIGN - 80)) | (1L << (MUL_ASSIGN - 80)) | (1L << (DIV_ASSIGN - 80)) | (1L << (AND_ASSIGN - 80)) | (1L << (OR_ASSIGN - 80)) | (1L << (XOR_ASSIGN - 80)) | (1L << (MOD_ASSIGN - 80)) | (1L << (LSHIFT_ASSIGN - 80)) | (1L << (RSHIFT_ASSIGN - 80)) | (1L << (URSHIFT_ASSIGN - 80)))) != 0)) ) {
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
		enterRule(_localctx, 460, RULE_additiveOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2702);
			_la = _input.LA(1);
			if ( !(_la==T__12 || _la==ADD) ) {
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
		enterRule(_localctx, 462, RULE_relationalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2704);
			_la = _input.LA(1);
			if ( !(((((_la - 40)) & ~0x3f) == 0 && ((1L << (_la - 40)) & ((1L << (INSTANCEOF - 40)) | (1L << (GT - 40)) | (1L << (LT - 40)) | (1L << (LE - 40)) | (1L << (GE - 40)))) != 0)) ) {
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
		enterRule(_localctx, 464, RULE_multiplicativeOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2706);
			_la = _input.LA(1);
			if ( !(((((_la - 96)) & ~0x3f) == 0 && ((1L << (_la - 96)) & ((1L << (MUL - 96)) | (1L << (DIV - 96)) | (1L << (MOD - 96)))) != 0)) ) {
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
		enterRule(_localctx, 466, RULE_squareBrackets);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2708);
			match(T__13);
			}
		}
		catch (RecognitionException re) {
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
		enterRule(_localctx, 468, RULE_conditionalExpression);
		try {
			setState(2723);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,301,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2710);
				conditionalOrExpression(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
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
				conditionalExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2717);
				conditionalOrExpression(0);
				setState(2718);
				match(QUESTION);
				setState(2719);
				expression();
				setState(2720);
				match(COLON);
				setState(2721);
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
		int _startState = 470;
		enterRecursionRule(_localctx, 470, RULE_conditionalOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2726);
			conditionalAndExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2733);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,302,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionalOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_conditionalOrExpression);
					setState(2728);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2729);
					match(OR);
					setState(2730);
					conditionalAndExpression(0);
					}
					} 
				}
				setState(2735);
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
		int _startState = 472;
		enterRecursionRule(_localctx, 472, RULE_conditionalAndExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2737);
			inclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2744);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,303,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionalAndExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_conditionalAndExpression);
					setState(2739);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2740);
					match(AND);
					setState(2741);
					inclusiveOrExpression(0);
					}
					} 
				}
				setState(2746);
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
		int _startState = 474;
		enterRecursionRule(_localctx, 474, RULE_inclusiveOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2748);
			exclusiveOrExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2755);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,304,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new InclusiveOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_inclusiveOrExpression);
					setState(2750);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2751);
					match(BITOR);
					setState(2752);
					exclusiveOrExpression(0);
					}
					} 
				}
				setState(2757);
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
		int _startState = 476;
		enterRecursionRule(_localctx, 476, RULE_exclusiveOrExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2759);
			andExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2766);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,305,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExclusiveOrExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_exclusiveOrExpression);
					setState(2761);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2762);
					match(CARET);
					setState(2763);
					andExpression(0);
					}
					} 
				}
				setState(2768);
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
		int _startState = 478;
		enterRecursionRule(_localctx, 478, RULE_andExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2770);
			equalityExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2777);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,306,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new AndExpressionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_andExpression);
					setState(2772);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(2773);
					match(BITAND);
					setState(2774);
					equalityExpression(0);
					}
					} 
				}
				setState(2779);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,306,_ctx);
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
		int _startState = 480;
		enterRecursionRule(_localctx, 480, RULE_equalityExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2781);
			relationalExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2791);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,308,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2789);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,307,_ctx) ) {
					case 1:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2783);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2784);
						match(EQUAL);
						setState(2785);
						relationalExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new EqualityExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_equalityExpression);
						setState(2786);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2787);
						match(NOTEQUAL);
						setState(2788);
						relationalExpression(0);
						}
						break;
					}
					} 
				}
				setState(2793);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,308,_ctx);
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
		int _startState = 482;
		enterRecursionRule(_localctx, 482, RULE_relationalExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2795);
			shiftExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2819);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,310,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2817);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,309,_ctx) ) {
					case 1:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2797);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(2798);
						relationalOperator();
						setState(2799);
						shiftExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2801);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(2802);
						relationalOperator();
						setState(2803);
						shiftExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2805);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2806);
						relationalOperator();
						setState(2807);
						shiftExpression(0);
						}
						break;
					case 4:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2809);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2810);
						relationalOperator();
						setState(2811);
						shiftExpression(0);
						}
						break;
					case 5:
						{
						_localctx = new RelationalExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_relationalExpression);
						setState(2813);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2814);
						relationalOperator();
						setState(2815);
						referenceType();
						}
						break;
					}
					} 
				}
				setState(2821);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,310,_ctx);
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
		int _startState = 484;
		enterRecursionRule(_localctx, 484, RULE_shiftExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2823);
			additiveExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2839);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,312,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2837);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,311,_ctx) ) {
					case 1:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2825);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2826);
						shiftOperator();
						setState(2827);
						additiveExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2829);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2830);
						shiftOperator();
						setState(2831);
						additiveExpression(0);
						}
						break;
					case 3:
						{
						_localctx = new ShiftExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_shiftExpression);
						setState(2833);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2834);
						shiftOperator();
						setState(2835);
						additiveExpression(0);
						}
						break;
					}
					} 
				}
				setState(2841);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,312,_ctx);
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
		enterRule(_localctx, 486, RULE_shiftOperator);
		try {
			setState(2849);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,313,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2842);
				match(LT);
				setState(2843);
				match(LT);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2844);
				match(GT);
				setState(2845);
				match(GT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2846);
				match(GT);
				setState(2847);
				match(GT);
				setState(2848);
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
		int _startState = 488;
		enterRecursionRule(_localctx, 488, RULE_additiveExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2852);
			multiplicativeExpression(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(2864);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,315,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2862);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,314,_ctx) ) {
					case 1:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2854);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2855);
						additiveOperator();
						setState(2856);
						multiplicativeExpression(0);
						}
						break;
					case 2:
						{
						_localctx = new AdditiveExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_additiveExpression);
						setState(2858);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2859);
						additiveOperator();
						setState(2860);
						multiplicativeExpression(0);
						}
						break;
					}
					} 
				}
				setState(2866);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,315,_ctx);
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
		int _startState = 490;
		enterRecursionRule(_localctx, 490, RULE_multiplicativeExpression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(2868);
			unaryExpression();
			}
			_ctx.stop = _input.LT(-1);
			setState(2884);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,317,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(2882);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,316,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2870);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(2871);
						multiplicativeOperator();
						setState(2872);
						unaryExpression();
						}
						break;
					case 2:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2874);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(2875);
						multiplicativeOperator();
						setState(2876);
						unaryExpression();
						}
						break;
					case 3:
						{
						_localctx = new MultiplicativeExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpression);
						setState(2878);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(2879);
						multiplicativeOperator();
						setState(2880);
						unaryExpression();
						}
						break;
					}
					} 
				}
				setState(2886);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,317,_ctx);
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
		enterRule(_localctx, 492, RULE_unaryExpression);
		try {
			setState(2896);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,318,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2887);
				preIncrementExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2888);
				preDecrementExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2889);
				additiveOperator();
				setState(2890);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2892);
				additiveOperator();
				setState(2893);
				unaryExpression();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(2895);
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
		enterRule(_localctx, 494, RULE_preIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2898);
			match(INC);
			setState(2899);
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
		enterRule(_localctx, 496, RULE_preDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2901);
			match(DEC);
			setState(2902);
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
		enterRule(_localctx, 498, RULE_unaryExpressionNotPlusMinus);
		try {
			setState(2910);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,319,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2904);
				postfixExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2905);
				match(TILDE);
				setState(2906);
				unaryExpression();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2907);
				match(BANG);
				setState(2908);
				unaryExpression();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(2909);
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
		enterRule(_localctx, 500, RULE_postfixExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(2914);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,320,_ctx) ) {
			case 1:
				{
				setState(2912);
				primary();
				}
				break;
			case 2:
				{
				setState(2913);
				expressionName();
				}
				break;
			}
			setState(2920);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,322,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(2918);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case INC:
						{
						setState(2916);
						postIncrementExpression_lf_postfixExpression();
						}
						break;
					case DEC:
						{
						setState(2917);
						postDecrementExpression_lf_postfixExpression();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					} 
				}
				setState(2922);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,322,_ctx);
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
		enterRule(_localctx, 502, RULE_postIncrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2923);
			postfixExpression();
			setState(2924);
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
		enterRule(_localctx, 504, RULE_postIncrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2926);
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
		enterRule(_localctx, 506, RULE_postDecrementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2928);
			postfixExpression();
			setState(2929);
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
		enterRule(_localctx, 508, RULE_postDecrementExpression_lf_postfixExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(2931);
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
		enterRule(_localctx, 510, RULE_castExpression);
		int _la;
		try {
			setState(2960);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,325,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(2933);
				match(LPAREN);
				setState(2934);
				primitiveType();
				setState(2935);
				match(RPAREN);
				setState(2936);
				unaryExpression();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(2938);
				match(LPAREN);
				setState(2939);
				referenceType();
				setState(2943);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2940);
					additionalBound();
					}
					}
					setState(2945);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2946);
				match(RPAREN);
				setState(2947);
				unaryExpressionNotPlusMinus();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(2949);
				match(LPAREN);
				setState(2950);
				referenceType();
				setState(2954);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==BITAND) {
					{
					{
					setState(2951);
					additionalBound();
					}
					}
					setState(2956);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(2957);
				match(RPAREN);
				setState(2958);
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
		case 235:
			return conditionalOrExpression_sempred((ConditionalOrExpressionContext)_localctx, predIndex);
		case 236:
			return conditionalAndExpression_sempred((ConditionalAndExpressionContext)_localctx, predIndex);
		case 237:
			return inclusiveOrExpression_sempred((InclusiveOrExpressionContext)_localctx, predIndex);
		case 238:
			return exclusiveOrExpression_sempred((ExclusiveOrExpressionContext)_localctx, predIndex);
		case 239:
			return andExpression_sempred((AndExpressionContext)_localctx, predIndex);
		case 240:
			return equalityExpression_sempred((EqualityExpressionContext)_localctx, predIndex);
		case 241:
			return relationalExpression_sempred((RelationalExpressionContext)_localctx, predIndex);
		case 242:
			return shiftExpression_sempred((ShiftExpressionContext)_localctx, predIndex);
		case 244:
			return additiveExpression_sempred((AdditiveExpressionContext)_localctx, predIndex);
		case 245:
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

	private static final String _serializedATNSegment0 =
		"\u0004\u0001y\u0b93\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
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
		"\u0002\u00f9\u0007\u00f9\u0002\u00fa\u0007\u00fa\u0002\u00fb\u0007\u00fb"+
		"\u0002\u00fc\u0007\u00fc\u0002\u00fd\u0007\u00fd\u0002\u00fe\u0007\u00fe"+
		"\u0002\u00ff\u0007\u00ff\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0003\u0002\u0207\b\u0002\u0001\u0003\u0005\u0003"+
		"\u020a\b\u0003\n\u0003\f\u0003\u020d\t\u0003\u0001\u0003\u0001\u0003\u0005"+
		"\u0003\u0211\b\u0003\n\u0003\f\u0003\u0214\t\u0003\u0001\u0003\u0003\u0003"+
		"\u0217\b\u0003\u0001\u0004\u0001\u0004\u0003\u0004\u021b\b\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0003\u0007\u0224\b\u0007\u0001\b\u0001\b\u0003\b\u0228\b\b\u0001"+
		"\b\u0001\b\u0005\b\u022c\b\b\n\b\f\b\u022f\t\b\u0001\t\u0001\t\u0003\t"+
		"\u0233\b\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u0239\b\t\u0003\t\u023b"+
		"\b\t\u0001\n\u0001\n\u0001\n\u0003\n\u0240\b\n\u0001\u000b\u0001\u000b"+
		"\u0003\u000b\u0244\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0003\u0010\u0257\b\u0010\u0001\u0011\u0001\u0011\u0005\u0011\u025b\b"+
		"\u0011\n\u0011\f\u0011\u025e\t\u0011\u0001\u0012\u0005\u0012\u0261\b\u0012"+
		"\n\u0012\f\u0012\u0264\t\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u0268"+
		"\b\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0005\u0014\u0271\b\u0014\n\u0014\f\u0014\u0274\t\u0014"+
		"\u0003\u0014\u0276\b\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0005\u0017\u0282\b\u0017\n\u0017\f\u0017\u0285\t\u0017\u0001\u0018\u0001"+
		"\u0018\u0003\u0018\u0289\b\u0018\u0001\u0019\u0005\u0019\u028c\b\u0019"+
		"\n\u0019\f\u0019\u028f\t\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0293"+
		"\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001a\u0299"+
		"\b\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0005\u001b\u02a1\b\u001b\n\u001b\f\u001b\u02a4\t\u001b\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u02ab\b\u001c"+
		"\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0005\u001d\u02b3\b\u001d\n\u001d\f\u001d\u02b6\t\u001d\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u02bd\b\u001e\u0001"+
		"\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001 \u0001 \u0001 \u0005 \u02c7"+
		"\b \n \f \u02ca\t \u0001!\u0001!\u0001!\u0001!\u0003!\u02d0\b!\u0001!"+
		"\u0005!\u02d3\b!\n!\f!\u02d6\t!\u0001!\u0005!\u02d9\b!\n!\f!\u02dc\t!"+
		"\u0001!\u0001!\u0005!\u02e0\b!\n!\f!\u02e3\t!\u0001!\u0001!\u0001!\u0003"+
		"!\u02e8\b!\u0001\"\u0005\"\u02eb\b\"\n\"\f\"\u02ee\t\"\u0001\"\u0001\""+
		"\u0001\"\u0001\"\u0005\"\u02f4\b\"\n\"\f\"\u02f7\t\"\u0001\"\u0001\"\u0001"+
		"#\u0001#\u0001$\u0001$\u0001$\u0001$\u0003$\u0301\b$\u0001%\u0001%\u0001"+
		"%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001\'\u0001\'\u0001"+
		"\'\u0001\'\u0001\'\u0001\'\u0001\'\u0001(\u0001(\u0001(\u0001(\u0001("+
		"\u0001(\u0001(\u0001)\u0001)\u0001)\u0003)\u031e\b)\u0001*\u0005*\u0321"+
		"\b*\n*\f*\u0324\t*\u0001*\u0003*\u0327\b*\u0001*\u0001*\u0001*\u0001*"+
		"\u0005*\u032d\b*\n*\f*\u0330\t*\u0001*\u0001*\u0005*\u0334\b*\n*\f*\u0337"+
		"\t*\u0001*\u0001*\u0001+\u0001+\u0005+\u033d\b+\n+\f+\u0340\t+\u0001+"+
		"\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0005+\u034b"+
		"\b+\n+\f+\u034e\t+\u0003+\u0350\b+\u0001+\u0001+\u0001+\u0001+\u0001+"+
		"\u0001+\u0001+\u0001+\u0005+\u035a\b+\n+\f+\u035d\t+\u0003+\u035f\b+\u0001"+
		"+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001"+
		"+\u0001+\u0005+\u036d\b+\n+\f+\u0370\t+\u0001+\u0001+\u0003+\u0374\b+"+
		"\u0001,\u0001,\u0001-\u0001-\u0001-\u0001-\u0001-\u0001-\u0005-\u037e"+
		"\b-\n-\f-\u0381\t-\u0001.\u0001.\u0003.\u0385\b.\u0001/\u0001/\u0001/"+
		"\u0001/\u0003/\u038b\b/\u0001/\u0003/\u038e\b/\u0001/\u0003/\u0391\b/"+
		"\u0001/\u0001/\u00010\u00050\u0396\b0\n0\f0\u0399\t0\u00011\u00011\u0001"+
		"1\u00011\u00011\u00011\u00011\u00011\u00031\u03a3\b1\u00012\u00012\u0001"+
		"2\u00012\u00013\u00013\u00013\u00053\u03ac\b3\n3\f3\u03af\t3\u00014\u0001"+
		"4\u00014\u00015\u00015\u00015\u00016\u00016\u00016\u00056\u03ba\b6\n6"+
		"\f6\u03bd\t6\u00017\u00017\u00057\u03c1\b7\n7\f7\u03c4\t7\u00017\u0001"+
		"7\u00018\u00018\u00018\u00018\u00038\u03cc\b8\u00019\u00019\u00019\u0001"+
		"9\u00019\u00039\u03d3\b9\u0001:\u0001:\u0001:\u0001:\u0001:\u0001;\u0005"+
		";\u03db\b;\n;\f;\u03de\t;\u0001<\u0001<\u0001<\u0001<\u0001<\u0001<\u0001"+
		"<\u0001<\u0003<\u03e8\b<\u0001=\u0001=\u0001=\u0005=\u03ed\b=\n=\f=\u03f0"+
		"\t=\u0001>\u0001>\u0001>\u0003>\u03f5\b>\u0001?\u0001?\u0003?\u03f9\b"+
		"?\u0001@\u0001@\u0003@\u03fd\b@\u0001A\u0001A\u0003A\u0401\bA\u0001B\u0001"+
		"B\u0003B\u0405\bB\u0001C\u0001C\u0001C\u0003C\u040a\bC\u0001D\u0001D\u0003"+
		"D\u040e\bD\u0001D\u0001D\u0005D\u0412\bD\nD\fD\u0415\tD\u0001E\u0001E"+
		"\u0003E\u0419\bE\u0001E\u0001E\u0001E\u0001E\u0003E\u041f\bE\u0003E\u0421"+
		"\bE\u0001F\u0001F\u0001F\u0003F\u0426\bF\u0001G\u0001G\u0003G\u042a\b"+
		"G\u0001H\u0001H\u0001I\u0001I\u0001J\u0001J\u0001K\u0001K\u0001L\u0001"+
		"L\u0001L\u0001L\u0001L\u0001L\u0001L\u0001L\u0001L\u0003L\u043d\bL\u0001"+
		"M\u0001M\u0001M\u0001M\u0001N\u0005N\u0444\bN\nN\fN\u0447\tN\u0001O\u0001"+
		"O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0001O\u0003O\u0453"+
		"\bO\u0001P\u0001P\u0001P\u0003P\u0458\bP\u0001P\u0001P\u0005P\u045c\b"+
		"P\nP\fP\u045f\tP\u0001P\u0001P\u0001P\u0003P\u0464\bP\u0003P\u0466\bP"+
		"\u0001Q\u0001Q\u0003Q\u046a\bQ\u0001R\u0001R\u0001R\u0003R\u046f\bR\u0001"+
		"R\u0001R\u0003R\u0473\bR\u0001S\u0001S\u0001S\u0001S\u0001S\u0003S\u047a"+
		"\bS\u0001T\u0001T\u0001T\u0005T\u047f\bT\nT\fT\u0482\tT\u0001T\u0001T"+
		"\u0001T\u0005T\u0487\bT\nT\fT\u048a\tT\u0003T\u048c\bT\u0001U\u0005U\u048f"+
		"\bU\nU\fU\u0492\tU\u0001U\u0001U\u0001U\u0001V\u0001V\u0003V\u0499\bV"+
		"\u0001W\u0005W\u049c\bW\nW\fW\u049f\tW\u0001W\u0001W\u0005W\u04a3\bW\n"+
		"W\fW\u04a6\tW\u0001W\u0001W\u0001W\u0001W\u0003W\u04ac\bW\u0001X\u0005"+
		"X\u04af\bX\nX\fX\u04b2\tX\u0001X\u0001X\u0001X\u0001X\u0003X\u04b8\bX"+
		"\u0001X\u0001X\u0001Y\u0001Y\u0001Y\u0001Z\u0001Z\u0001Z\u0005Z\u04c2"+
		"\bZ\nZ\fZ\u04c5\tZ\u0001[\u0001[\u0003[\u04c9\b[\u0001\\\u0001\\\u0003"+
		"\\\u04cd\b\\\u0001]\u0001]\u0001^\u0001^\u0001^\u0001_\u0001_\u0001_\u0003"+
		"_\u04d7\b_\u0001_\u0001_\u0001`\u0005`\u04dc\b`\n`\f`\u04df\t`\u0001a"+
		"\u0001a\u0001a\u0001a\u0003a\u04e5\ba\u0001b\u0003b\u04e8\bb\u0001b\u0001"+
		"b\u0001b\u0003b\u04ed\bb\u0001b\u0001b\u0001c\u0001c\u0001d\u0001d\u0003"+
		"d\u04f5\bd\u0001d\u0003d\u04f8\bd\u0001d\u0001d\u0001e\u0003e\u04fd\b"+
		"e\u0001e\u0001e\u0001e\u0003e\u0502\be\u0001e\u0001e\u0001e\u0003e\u0507"+
		"\be\u0001e\u0001e\u0001e\u0003e\u050c\be\u0001e\u0001e\u0001e\u0001e\u0001"+
		"e\u0003e\u0513\be\u0001e\u0001e\u0001e\u0003e\u0518\be\u0001e\u0001e\u0001"+
		"e\u0001e\u0001e\u0001e\u0003e\u0520\be\u0001e\u0001e\u0001e\u0003e\u0525"+
		"\be\u0001e\u0001e\u0001e\u0003e\u052a\be\u0001f\u0001f\u0001f\u0001f\u0003"+
		"f\u0530\bf\u0001f\u0001f\u0001g\u0001g\u0003g\u0536\bg\u0001g\u0003g\u0539"+
		"\bg\u0001g\u0003g\u053c\bg\u0001g\u0001g\u0001h\u0001h\u0001h\u0005h\u0543"+
		"\bh\nh\fh\u0546\th\u0001i\u0005i\u0549\bi\ni\fi\u054c\ti\u0001i\u0001"+
		"i\u0001i\u0003i\u0551\bi\u0001i\u0003i\u0554\bi\u0001i\u0003i\u0557\b"+
		"i\u0001j\u0001j\u0001k\u0001k\u0005k\u055d\bk\nk\fk\u0560\tk\u0001l\u0001"+
		"l\u0003l\u0564\bl\u0001m\u0001m\u0001m\u0001m\u0003m\u056a\bm\u0001m\u0003"+
		"m\u056d\bm\u0001m\u0001m\u0001n\u0005n\u0572\bn\nn\fn\u0575\tn\u0001o"+
		"\u0001o\u0001o\u0001o\u0001o\u0001o\u0001o\u0003o\u057e\bo\u0001p\u0001"+
		"p\u0001p\u0001q\u0001q\u0005q\u0585\bq\nq\fq\u0588\tq\u0001q\u0001q\u0001"+
		"r\u0001r\u0001r\u0001r\u0001r\u0003r\u0591\br\u0001s\u0005s\u0594\bs\n"+
		"s\fs\u0597\ts\u0001s\u0001s\u0001s\u0001s\u0001t\u0001t\u0001t\u0001t"+
		"\u0003t\u05a1\bt\u0001u\u0005u\u05a4\bu\nu\fu\u05a7\tu\u0001u\u0001u\u0001"+
		"u\u0001v\u0001v\u0001v\u0001v\u0001v\u0001v\u0001v\u0003v\u05b3\bv\u0001"+
		"w\u0005w\u05b6\bw\nw\fw\u05b9\tw\u0001w\u0001w\u0001w\u0001w\u0001w\u0001"+
		"x\u0001x\u0005x\u05c2\bx\nx\fx\u05c5\tx\u0001x\u0001x\u0001y\u0001y\u0001"+
		"y\u0001y\u0001y\u0003y\u05ce\by\u0001z\u0005z\u05d1\bz\nz\fz\u05d4\tz"+
		"\u0001z\u0001z\u0001z\u0001z\u0001z\u0003z\u05db\bz\u0001z\u0003z\u05de"+
		"\bz\u0001z\u0001z\u0001{\u0001{\u0001{\u0003{\u05e5\b{\u0001|\u0001|\u0001"+
		"|\u0001}\u0001}\u0001}\u0003}\u05ed\b}\u0001~\u0005~\u05f0\b~\n~\f~\u05f3"+
		"\t~\u0001~\u0001~\u0001\u007f\u0005\u007f\u05f8\b\u007f\n\u007f\f\u007f"+
		"\u05fb\t\u007f\u0001\u007f\u0001\u007f\u0001\u0080\u0001\u0080\u0001\u0080"+
		"\u0001\u0080\u0003\u0080\u0603\b\u0080\u0001\u0080\u0001\u0080\u0001\u0081"+
		"\u0001\u0081\u0001\u0081\u0005\u0081\u060a\b\u0081\n\u0081\f\u0081\u060d"+
		"\t\u0081\u0001\u0082\u0001\u0082\u0001\u0082\u0001\u0082\u0001\u0083\u0001"+
		"\u0083\u0001\u0083\u0003\u0083\u0616\b\u0083\u0001\u0084\u0001\u0084\u0003"+
		"\u0084\u061a\b\u0084\u0001\u0084\u0003\u0084\u061d\b\u0084\u0001\u0084"+
		"\u0001\u0084\u0001\u0085\u0001\u0085\u0001\u0085\u0005\u0085\u0624\b\u0085"+
		"\n\u0085\f\u0085\u0627\t\u0085\u0001\u0086\u0001\u0086\u0001\u0086\u0001"+
		"\u0087\u0001\u0087\u0001\u0087\u0001\u0087\u0001\u0087\u0001\u0087\u0001"+
		"\u0088\u0001\u0088\u0003\u0088\u0634\b\u0088\u0001\u0088\u0003\u0088\u0637"+
		"\b\u0088\u0001\u0088\u0001\u0088\u0001\u0089\u0001\u0089\u0001\u0089\u0005"+
		"\u0089\u063e\b\u0089\n\u0089\f\u0089\u0641\t\u0089\u0001\u008a\u0001\u008a"+
		"\u0003\u008a\u0645\b\u008a\u0001\u008a\u0001\u008a\u0001\u008b\u0001\u008b"+
		"\u0005\u008b\u064b\b\u008b\n\u008b\f\u008b\u064e\t\u008b\u0001\u008c\u0001"+
		"\u008c\u0001\u008c\u0003\u008c\u0653\b\u008c\u0001\u008d\u0001\u008d\u0001"+
		"\u008d\u0001\u008e\u0005\u008e\u0659\b\u008e\n\u008e\f\u008e\u065c\t\u008e"+
		"\u0001\u008e\u0001\u008e\u0001\u008e\u0001\u008f\u0001\u008f\u0003\u008f"+
		"\u0663\b\u008f\u0001\u0090\u0001\u0090\u0001\u0090\u0001\u0090\u0001\u0090"+
		"\u0001\u0090\u0003\u0090\u066b\b\u0090\u0001\u0091\u0001\u0091\u0001\u0091"+
		"\u0001\u0091\u0001\u0091\u0003\u0091\u0672\b\u0091\u0001\u0092\u0001\u0092"+
		"\u0001\u0092\u0001\u0092\u0001\u0092\u0001\u0092\u0001\u0092\u0001\u0092"+
		"\u0001\u0092\u0001\u0092\u0001\u0092\u0001\u0092\u0003\u0092\u0680\b\u0092"+
		"\u0001\u0093\u0001\u0093\u0001\u0094\u0001\u0094\u0001\u0094\u0001\u0094"+
		"\u0001\u0095\u0001\u0095\u0001\u0095\u0001\u0095\u0001\u0096\u0001\u0096"+
		"\u0001\u0096\u0001\u0097\u0001\u0097\u0001\u0097\u0001\u0097\u0001\u0097"+
		"\u0001\u0097\u0001\u0097\u0003\u0097\u0696\b\u0097\u0001\u0098\u0001\u0098"+
		"\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0098\u0001\u0099\u0001\u0099"+
		"\u0001\u0099\u0001\u0099\u0001\u0099\u0001\u0099\u0001\u0099\u0001\u0099"+
		"\u0001\u009a\u0001\u009a\u0001\u009a\u0001\u009a\u0001\u009a\u0001\u009a"+
		"\u0001\u009a\u0001\u009a\u0001\u009b\u0001\u009b\u0001\u009b\u0001\u009b"+
		"\u0001\u009b\u0001\u009b\u0001\u009b\u0001\u009b\u0001\u009b\u0001\u009b"+
		"\u0003\u009b\u06b8\b\u009b\u0001\u009c\u0001\u009c\u0001\u009c\u0001\u009c"+
		"\u0001\u009c\u0001\u009c\u0001\u009d\u0001\u009d\u0005\u009d\u06c2\b\u009d"+
		"\n\u009d\f\u009d\u06c5\t\u009d\u0001\u009d\u0005\u009d\u06c8\b\u009d\n"+
		"\u009d\f\u009d\u06cb\t\u009d\u0001\u009d\u0001\u009d\u0001\u009e\u0001"+
		"\u009e\u0001\u009e\u0001\u009f\u0001\u009f\u0005\u009f\u06d4\b\u009f\n"+
		"\u009f\f\u009f\u06d7\t\u009f\u0001\u00a0\u0001\u00a0\u0001\u00a0\u0001"+
		"\u00a0\u0001\u00a0\u0001\u00a0\u0001\u00a0\u0001\u00a0\u0001\u00a0\u0001"+
		"\u00a0\u0003\u00a0\u06e3\b\u00a0\u0001\u00a1\u0001\u00a1\u0001\u00a2\u0001"+
		"\u00a2\u0001\u00a2\u0001\u00a2\u0001\u00a2\u0001\u00a2\u0001\u00a3\u0001"+
		"\u00a3\u0001\u00a3\u0001\u00a3\u0001\u00a3\u0001\u00a3\u0001\u00a4\u0001"+
		"\u00a4\u0001\u00a4\u0001\u00a4\u0001\u00a4\u0001\u00a4\u0001\u00a4\u0001"+
		"\u00a4\u0001\u00a5\u0001\u00a5\u0003\u00a5\u06fd\b\u00a5\u0001\u00a6\u0001"+
		"\u00a6\u0003\u00a6\u0701\b\u00a6\u0001\u00a7\u0001\u00a7\u0001\u00a7\u0003"+
		"\u00a7\u0706\b\u00a7\u0001\u00a7\u0001\u00a7\u0003\u00a7\u070a\b\u00a7"+
		"\u0001\u00a7\u0001\u00a7\u0003\u00a7\u070e\b\u00a7\u0001\u00a7\u0001\u00a7"+
		"\u0001\u00a7\u0001\u00a8\u0001\u00a8\u0001\u00a8\u0003\u00a8\u0716\b\u00a8"+
		"\u0001\u00a8\u0001\u00a8\u0003\u00a8\u071a\b\u00a8\u0001\u00a8\u0001\u00a8"+
		"\u0003\u00a8\u071e\b\u00a8\u0001\u00a8\u0001\u00a8\u0001\u00a8\u0001\u00a9"+
		"\u0001\u00a9\u0003\u00a9\u0725\b\u00a9\u0001\u00aa\u0001\u00aa\u0001\u00ab"+
		"\u0001\u00ab\u0001\u00ab\u0005\u00ab\u072c\b\u00ab\n\u00ab\f\u00ab\u072f"+
		"\t\u00ab\u0001\u00ac\u0001\u00ac\u0001\u00ac\u0005\u00ac\u0734\b\u00ac"+
		"\n\u00ac\f\u00ac\u0737\t\u00ac\u0001\u00ac\u0001\u00ac\u0001\u00ac\u0001"+
		"\u00ac\u0001\u00ac\u0001\u00ac\u0001\u00ac\u0001\u00ad\u0001\u00ad\u0001"+
		"\u00ad\u0005\u00ad\u0743\b\u00ad\n\u00ad\f\u00ad\u0746\t\u00ad\u0001\u00ad"+
		"\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad\u0001\u00ad"+
		"\u0001\u00ae\u0001\u00ae\u0003\u00ae\u0751\b\u00ae\u0001\u00ae\u0001\u00ae"+
		"\u0001\u00af\u0001\u00af\u0003\u00af\u0757\b\u00af\u0001\u00af\u0001\u00af"+
		"\u0001\u00b0\u0001\u00b0\u0003\u00b0\u075d\b\u00b0\u0001\u00b0\u0001\u00b0"+
		"\u0001\u00b1\u0001\u00b1\u0001\u00b1\u0001\u00b1\u0001\u00b2\u0001\u00b2"+
		"\u0001\u00b2\u0001\u00b2\u0001\u00b2\u0001\u00b2\u0001\u00b3\u0001\u00b3"+
		"\u0001\u00b3\u0001\u00b3\u0001\u00b3\u0001\u00b3\u0001\u00b3\u0003\u00b3"+
		"\u0772\b\u00b3\u0001\u00b3\u0001\u00b3\u0001\u00b3\u0003\u00b3\u0777\b"+
		"\u00b3\u0001\u00b4\u0001\u00b4\u0005\u00b4\u077b\b\u00b4\n\u00b4\f\u00b4"+
		"\u077e\t\u00b4\u0001\u00b5\u0001\u00b5\u0001\u00b5\u0001\u00b5\u0001\u00b5"+
		"\u0001\u00b5\u0001\u00b6\u0005\u00b6\u0787\b\u00b6\n\u00b6\f\u00b6\u078a"+
		"\t\u00b6\u0001\u00b6\u0001\u00b6\u0001\u00b6\u0001\u00b7\u0001\u00b7\u0001"+
		"\u00b7\u0005\u00b7\u0792\b\u00b7\n\u00b7\f\u00b7\u0795\t\u00b7\u0001\u00b8"+
		"\u0001\u00b8\u0001\u00b8\u0001\u00b9\u0001\u00b9\u0001\u00b9\u0001\u00b9"+
		"\u0003\u00b9\u079e\b\u00b9\u0001\u00b9\u0003\u00b9\u07a1\b\u00b9\u0001"+
		"\u00ba\u0001\u00ba\u0001\u00ba\u0003\u00ba\u07a6\b\u00ba\u0001\u00ba\u0001"+
		"\u00ba\u0001\u00bb\u0001\u00bb\u0001\u00bb\u0005\u00bb\u07ad\b\u00bb\n"+
		"\u00bb\f\u00bb\u07b0\t\u00bb\u0001\u00bc\u0005\u00bc\u07b3\b\u00bc\n\u00bc"+
		"\f\u00bc\u07b6\t\u00bc\u0001\u00bc\u0001\u00bc\u0001\u00bc\u0001\u00bc"+
		"\u0001\u00bc\u0001\u00bc\u0003\u00bc\u07be\b\u00bc\u0001\u00bd\u0001\u00bd"+
		"\u0003\u00bd\u07c2\b\u00bd\u0001\u00be\u0001\u00be\u0003\u00be\u07c6\b"+
		"\u00be\u0001\u00be\u0005\u00be\u07c9\b\u00be\n\u00be\f\u00be\u07cc\t\u00be"+
		"\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0005\u00bf\u07d1\b\u00bf\n\u00bf"+
		"\f\u00bf\u07d4\t\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf"+
		"\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf"+
		"\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf"+
		"\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0001\u00bf\u0003\u00bf\u07ea\b\u00bf"+
		"\u0001\u00c0\u0001\u00c0\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0005\u00c1"+
		"\u07f1\b\u00c1\n\u00c1\f\u00c1\u07f4\t\u00c1\u0001\u00c1\u0001\u00c1\u0001"+
		"\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001"+
		"\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001"+
		"\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0001\u00c1\u0003\u00c1\u0809"+
		"\b\u00c1\u0001\u00c2\u0001\u00c2\u0001\u00c2\u0001\u00c2\u0001\u00c2\u0003"+
		"\u00c2\u0810\b\u00c2\u0001\u00c3\u0001\u00c3\u0001\u00c4\u0001\u00c4\u0001"+
		"\u00c4\u0001\u00c4\u0003\u00c4\u0818\b\u00c4\u0001\u00c5\u0001\u00c5\u0001"+
		"\u00c5\u0005\u00c5\u081d\b\u00c5\n\u00c5\f\u00c5\u0820\t\u00c5\u0001\u00c5"+
		"\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0005\u00c5\u0827\b\u00c5"+
		"\n\u00c5\f\u00c5\u082a\t\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001"+
		"\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001"+
		"\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001"+
		"\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0001\u00c5\u0003\u00c5\u0840"+
		"\b\u00c5\u0001\u00c6\u0001\u00c6\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0005"+
		"\u00c7\u0847\b\u00c7\n\u00c7\f\u00c7\u084a\t\u00c7\u0001\u00c7\u0001\u00c7"+
		"\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0005\u00c7\u0851\b\u00c7\n\u00c7"+
		"\f\u00c7\u0854\t\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7"+
		"\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7"+
		"\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0001\u00c7"+
		"\u0001\u00c7\u0001\u00c7\u0001\u00c7\u0003\u00c7\u0869\b\u00c7\u0001\u00c8"+
		"\u0001\u00c8\u0003\u00c8\u086d\b\u00c8\u0001\u00c8\u0001\u00c8\u0001\u00c8"+
		"\u0005\u00c8\u0872\b\u00c8\n\u00c8\f\u00c8\u0875\t\u00c8\u0001\u00c8\u0003"+
		"\u00c8\u0878\b\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u087c\b\u00c8"+
		"\u0001\u00c8\u0001\u00c8\u0003\u00c8\u0880\b\u00c8\u0001\u00c8\u0001\u00c8"+
		"\u0001\u00c8\u0001\u00c8\u0003\u00c8\u0886\b\u00c8\u0001\u00c8\u0001\u00c8"+
		"\u0003\u00c8\u088a\b\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u088e\b"+
		"\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u0892\b\u00c8\u0001\u00c8\u0001"+
		"\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u0898\b\u00c8\u0001\u00c8\u0001"+
		"\u00c8\u0003\u00c8\u089c\b\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u08a0"+
		"\b\u00c8\u0001\u00c8\u0001\u00c8\u0003\u00c8\u08a4\b\u00c8\u0003\u00c8"+
		"\u08a6\b\u00c8\u0001\u00c9\u0001\u00c9\u0001\u00c9\u0003\u00c9\u08ab\b"+
		"\u00c9\u0001\u00c9\u0001\u00c9\u0003\u00c9\u08af\b\u00c9\u0001\u00c9\u0001"+
		"\u00c9\u0003\u00c9\u08b3\b\u00c9\u0001\u00c9\u0001\u00c9\u0003\u00c9\u08b7"+
		"\b\u00c9\u0001\u00ca\u0001\u00ca\u0003\u00ca\u08bb\b\u00ca\u0001\u00ca"+
		"\u0001\u00ca\u0001\u00ca\u0005\u00ca\u08c0\b\u00ca\n\u00ca\f\u00ca\u08c3"+
		"\t\u00ca\u0001\u00ca\u0003\u00ca\u08c6\b\u00ca\u0001\u00ca\u0001\u00ca"+
		"\u0003\u00ca\u08ca\b\u00ca\u0001\u00ca\u0001\u00ca\u0003\u00ca\u08ce\b"+
		"\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0001\u00ca\u0003\u00ca\u08d4"+
		"\b\u00ca\u0001\u00ca\u0001\u00ca\u0003\u00ca\u08d8\b\u00ca\u0001\u00ca"+
		"\u0001\u00ca\u0003\u00ca\u08dc\b\u00ca\u0001\u00ca\u0001\u00ca\u0003\u00ca"+
		"\u08e0\b\u00ca\u0003\u00ca\u08e2\b\u00ca\u0001\u00cb\u0001\u00cb\u0003"+
		"\u00cb\u08e6\b\u00cb\u0001\u00cc\u0001\u00cc\u0001\u00cc\u0001\u00cc\u0001"+
		"\u00cc\u0001\u00cc\u0001\u00cc\u0001\u00cc\u0001\u00cc\u0001\u00cc\u0001"+
		"\u00cc\u0001\u00cc\u0001\u00cc\u0003\u00cc\u08f5\b\u00cc\u0001\u00cd\u0001"+
		"\u00cd\u0001\u00cd\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0001"+
		"\u00ce\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0001\u00ce\u0003\u00ce\u0903"+
		"\b\u00ce\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001"+
		"\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0003\u00cf\u090f"+
		"\b\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0001\u00cf\u0005"+
		"\u00cf\u0916\b\u00cf\n\u00cf\f\u00cf\u0919\t\u00cf\u0001\u00d0\u0001\u00d0"+
		"\u0001\u00d0\u0001\u00d0\u0001\u00d0\u0001\u00d0\u0001\u00d0\u0001\u00d0"+
		"\u0001\u00d0\u0001\u00d0\u0005\u00d0\u0925\b\u00d0\n\u00d0\f\u00d0\u0928"+
		"\t\u00d0\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001"+
		"\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0003\u00d1\u0934"+
		"\b\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0001\u00d1\u0005"+
		"\u00d1\u093b\b\u00d1\n\u00d1\f\u00d1\u093e\t\u00d1\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u0943\b\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0001\u00d2\u0003\u00d2\u094a\b\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u094f\b\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0001\u00d2\u0003\u00d2\u0956\b\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u095b\b\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0001\u00d2\u0003\u00d2\u0962\b\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u0967\b\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0001\u00d2\u0003\u00d2\u096e\b\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u0973\b\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0003\u00d2\u097c\b\u00d2"+
		"\u0001\u00d2\u0001\u00d2\u0001\u00d2\u0003\u00d2\u0981\b\u00d2\u0001\u00d2"+
		"\u0001\u00d2\u0003\u00d2\u0985\b\u00d2\u0001\u00d3\u0001\u00d3\u0003\u00d3"+
		"\u0989\b\u00d3\u0001\u00d3\u0001\u00d3\u0001\u00d3\u0003\u00d3\u098e\b"+
		"\u00d3\u0001\u00d3\u0001\u00d3\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0003"+
		"\u00d4\u0995\b\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001"+
		"\u00d4\u0003\u00d4\u099c\b\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0003"+
		"\u00d4\u09a1\b\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001"+
		"\u00d4\u0003\u00d4\u09a8\b\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0003"+
		"\u00d4\u09ad\b\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001"+
		"\u00d4\u0003\u00d4\u09b4\b\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0003"+
		"\u00d4\u09b9\b\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001\u00d4\u0001"+
		"\u00d4\u0001\u00d4\u0001\u00d4\u0003\u00d4\u09c2\b\u00d4\u0001\u00d4\u0001"+
		"\u00d4\u0001\u00d4\u0003\u00d4\u09c7\b\u00d4\u0001\u00d4\u0001\u00d4\u0003"+
		"\u00d4\u09cb\b\u00d4\u0001\u00d5\u0001\u00d5\u0001\u00d5\u0005\u00d5\u09d0"+
		"\b\u00d5\n\u00d5\f\u00d5\u09d3\t\u00d5\u0001\u00d6\u0001\u00d6\u0001\u00d6"+
		"\u0003\u00d6\u09d8\b\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6"+
		"\u0001\u00d6\u0003\u00d6\u09df\b\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6"+
		"\u0001\u00d6\u0001\u00d6\u0003\u00d6\u09e6\b\u00d6\u0001\u00d6\u0001\u00d6"+
		"\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0003\u00d6\u09ed\b\u00d6\u0001\u00d6"+
		"\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0003\u00d6"+
		"\u09f5\b\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6"+
		"\u0003\u00d6\u09fc\b\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6\u0001\u00d6"+
		"\u0001\u00d6\u0001\u00d6\u0003\u00d6\u0a04\b\u00d6\u0001\u00d7\u0001\u00d7"+
		"\u0003\u00d7\u0a08\b\u00d7\u0001\u00d7\u0001\u00d7\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0003\u00d8\u0a0f\b\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0003\u00d8\u0a16\b\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0003\u00d8\u0a1d\b\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0003\u00d8"+
		"\u0a25\b\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0003\u00d8\u0a2c\b\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8\u0001\u00d8"+
		"\u0001\u00d8\u0001\u00d8\u0003\u00d8\u0a34\b\u00d8\u0001\u00d9\u0001\u00d9"+
		"\u0001\u00d9\u0001\u00d9\u0003\u00d9\u0a3a\b\u00d9\u0001\u00d9\u0001\u00d9"+
		"\u0001\u00d9\u0001\u00d9\u0003\u00d9\u0a40\b\u00d9\u0001\u00d9\u0001\u00d9"+
		"\u0001\u00d9\u0001\u00d9\u0001\u00d9\u0001\u00d9\u0001\u00d9\u0001\u00d9"+
		"\u0001\u00d9\u0001\u00d9\u0003\u00d9\u0a4c\b\u00d9\u0001\u00da\u0001\u00da"+
		"\u0005\u00da\u0a50\b\u00da\n\u00da\f\u00da\u0a53\t\u00da\u0001\u00db\u0005"+
		"\u00db\u0a56\b\u00db\n\u00db\f\u00db\u0a59\t\u00db\u0001\u00db\u0001\u00db"+
		"\u0001\u00db\u0001\u00db\u0001\u00dc\u0001\u00dc\u0001\u00dd\u0001\u00dd"+
		"\u0003\u00dd\u0a63\b\u00dd\u0001\u00de\u0001\u00de\u0001\u00de\u0001\u00de"+
		"\u0001\u00df\u0001\u00df\u0001\u00df\u0003\u00df\u0a6c\b\u00df\u0001\u00df"+
		"\u0001\u00df\u0001\u00df\u0001\u00df\u0001\u00df\u0003\u00df\u0a73\b\u00df"+
		"\u0001\u00e0\u0001\u00e0\u0001\u00e0\u0005\u00e0\u0a78\b\u00e0\n\u00e0"+
		"\f\u00e0\u0a7b\t\u00e0\u0001\u00e1\u0001\u00e1\u0003\u00e1\u0a7f\b\u00e1"+
		"\u0001\u00e2\u0001\u00e2\u0003\u00e2\u0a83\b\u00e2\u0001\u00e3\u0001\u00e3"+
		"\u0001\u00e3\u0001\u00e3\u0001\u00e4\u0001\u00e4\u0003\u00e4\u0a8b\b\u00e4"+
		"\u0001\u00e5\u0001\u00e5\u0001\u00e6\u0001\u00e6\u0001\u00e7\u0001\u00e7"+
		"\u0001\u00e8\u0001\u00e8\u0001\u00e9\u0001\u00e9\u0001\u00ea\u0001\u00ea"+
		"\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea"+
		"\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0001\u00ea\u0003\u00ea"+
		"\u0aa4\b\u00ea\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb\u0001\u00eb"+
		"\u0001\u00eb\u0005\u00eb\u0aac\b\u00eb\n\u00eb\f\u00eb\u0aaf\t\u00eb\u0001"+
		"\u00ec\u0001\u00ec\u0001\u00ec\u0001\u00ec\u0001\u00ec\u0001\u00ec\u0005"+
		"\u00ec\u0ab7\b\u00ec\n\u00ec\f\u00ec\u0aba\t\u00ec\u0001\u00ed\u0001\u00ed"+
		"\u0001\u00ed\u0001\u00ed\u0001\u00ed\u0001\u00ed\u0005\u00ed\u0ac2\b\u00ed"+
		"\n\u00ed\f\u00ed\u0ac5\t\u00ed\u0001\u00ee\u0001\u00ee\u0001\u00ee\u0001"+
		"\u00ee\u0001\u00ee\u0001\u00ee\u0005\u00ee\u0acd\b\u00ee\n\u00ee\f\u00ee"+
		"\u0ad0\t\u00ee\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00ef\u0001\u00ef"+
		"\u0001\u00ef\u0005\u00ef\u0ad8\b\u00ef\n\u00ef\f\u00ef\u0adb\t\u00ef\u0001"+
		"\u00f0\u0001\u00f0\u0001\u00f0\u0001\u00f0\u0001\u00f0\u0001\u00f0\u0001"+
		"\u00f0\u0001\u00f0\u0001\u00f0\u0005\u00f0\u0ae6\b\u00f0\n\u00f0\f\u00f0"+
		"\u0ae9\t\u00f0\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1"+
		"\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1"+
		"\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1"+
		"\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1\u0001\u00f1"+
		"\u0005\u00f1\u0b02\b\u00f1\n\u00f1\f\u00f1\u0b05\t\u00f1\u0001\u00f2\u0001"+
		"\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001"+
		"\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001\u00f2\u0001"+
		"\u00f2\u0001\u00f2\u0005\u00f2\u0b16\b\u00f2\n\u00f2\f\u00f2\u0b19\t\u00f2"+
		"\u0001\u00f3\u0001\u00f3\u0001\u00f3\u0001\u00f3\u0001\u00f3\u0001\u00f3"+
		"\u0001\u00f3\u0003\u00f3\u0b22\b\u00f3\u0001\u00f4\u0001\u00f4\u0001\u00f4"+
		"\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0001\u00f4\u0001\u00f4"+
		"\u0001\u00f4\u0001\u00f4\u0005\u00f4\u0b2f\b\u00f4\n\u00f4\f\u00f4\u0b32"+
		"\t\u00f4\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0001"+
		"\u00f5\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0001"+
		"\u00f5\u0001\u00f5\u0001\u00f5\u0001\u00f5\u0005\u00f5\u0b43\b\u00f5\n"+
		"\u00f5\f\u00f5\u0b46\t\u00f5\u0001\u00f6\u0001\u00f6\u0001\u00f6\u0001"+
		"\u00f6\u0001\u00f6\u0001\u00f6\u0001\u00f6\u0001\u00f6\u0001\u00f6\u0003"+
		"\u00f6\u0b51\b\u00f6\u0001\u00f7\u0001\u00f7\u0001\u00f7\u0001\u00f8\u0001"+
		"\u00f8\u0001\u00f8\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001\u00f9\u0001"+
		"\u00f9\u0001\u00f9\u0003\u00f9\u0b5f\b\u00f9\u0001\u00fa\u0001\u00fa\u0003"+
		"\u00fa\u0b63\b\u00fa\u0001\u00fa\u0001\u00fa\u0005\u00fa\u0b67\b\u00fa"+
		"\n\u00fa\f\u00fa\u0b6a\t\u00fa\u0001\u00fb\u0001\u00fb\u0001\u00fb\u0001"+
		"\u00fc\u0001\u00fc\u0001\u00fd\u0001\u00fd\u0001\u00fd\u0001\u00fe\u0001"+
		"\u00fe\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0005\u00ff\u0b7e\b\u00ff\n\u00ff\f\u00ff"+
		"\u0b81\t\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff\u0001\u00ff"+
		"\u0001\u00ff\u0005\u00ff\u0b89\b\u00ff\n\u00ff\f\u00ff\u0b8c\t\u00ff\u0001"+
		"\u00ff\u0001\u00ff\u0001\u00ff\u0003\u00ff\u0b91\b\u00ff\u0001\u00ff\u0000"+
		"\u000e6:@Z\u01d6\u01d8\u01da\u01dc\u01de\u01e0\u01e2\u01e4\u01e8\u01ea"+
		"\u0100\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"+
		"\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080"+
		"\u0082\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098"+
		"\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0"+
		"\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8"+
		"\u00ca\u00cc\u00ce\u00d0\u00d2\u00d4\u00d6\u00d8\u00da\u00dc\u00de\u00e0"+
		"\u00e2\u00e4\u00e6\u00e8\u00ea\u00ec\u00ee\u00f0\u00f2\u00f4\u00f6\u00f8"+
		"\u00fa\u00fc\u00fe\u0100\u0102\u0104\u0106\u0108\u010a\u010c\u010e\u0110"+
		"\u0112\u0114\u0116\u0118\u011a\u011c\u011e\u0120\u0122\u0124\u0126\u0128"+
		"\u012a\u012c\u012e\u0130\u0132\u0134\u0136\u0138\u013a\u013c\u013e\u0140"+
		"\u0142\u0144\u0146\u0148\u014a\u014c\u014e\u0150\u0152\u0154\u0156\u0158"+
		"\u015a\u015c\u015e\u0160\u0162\u0164\u0166\u0168\u016a\u016c\u016e\u0170"+
		"\u0172\u0174\u0176\u0178\u017a\u017c\u017e\u0180\u0182\u0184\u0186\u0188"+
		"\u018a\u018c\u018e\u0190\u0192\u0194\u0196\u0198\u019a\u019c\u019e\u01a0"+
		"\u01a2\u01a4\u01a6\u01a8\u01aa\u01ac\u01ae\u01b0\u01b2\u01b4\u01b6\u01b8"+
		"\u01ba\u01bc\u01be\u01c0\u01c2\u01c4\u01c6\u01c8\u01ca\u01cc\u01ce\u01d0"+
		"\u01d2\u01d4\u01d6\u01d8\u01da\u01dc\u01de\u01e0\u01e2\u01e4\u01e6\u01e8"+
		"\u01ea\u01ec\u01ee\u01f0\u01f2\u01f4\u01f6\u01f8\u01fa\u01fc\u01fe\u0000"+
		"\t\u0002\u0000\u0001\nss\u0001\u0000AF\u0005\u0000\u0013\u0013\u0016\u0016"+
		"))++33\u0002\u0000\u001c\u001c\"\"\u0002\u0000\u0004\u000444\u0002\u0000"+
		"PPhr\u0002\u0000\r\r__\u0003\u0000((QRXY\u0002\u0000`aee\u0c79\u0000\u0200"+
		"\u0001\u0000\u0000\u0000\u0002\u0202\u0001\u0000\u0000\u0000\u0004\u0206"+
		"\u0001\u0000\u0000\u0000\u0006\u0216\u0001\u0000\u0000\u0000\b\u021a\u0001"+
		"\u0000\u0000\u0000\n\u021c\u0001\u0000\u0000\u0000\f\u021e\u0001\u0000"+
		"\u0000\u0000\u000e\u0223\u0001\u0000\u0000\u0000\u0010\u0227\u0001\u0000"+
		"\u0000\u0000\u0012\u023a\u0001\u0000\u0000\u0000\u0014\u023c\u0001\u0000"+
		"\u0000\u0000\u0016\u0241\u0001\u0000\u0000\u0000\u0018\u0245\u0001\u0000"+
		"\u0000\u0000\u001a\u0247\u0001\u0000\u0000\u0000\u001c\u0249\u0001\u0000"+
		"\u0000\u0000\u001e\u024b\u0001\u0000\u0000\u0000 \u0256\u0001\u0000\u0000"+
		"\u0000\"\u0258\u0001\u0000\u0000\u0000$\u0262\u0001\u0000\u0000\u0000"+
		"&\u0269\u0001\u0000\u0000\u0000(\u0275\u0001\u0000\u0000\u0000*\u0277"+
		"\u0001\u0000\u0000\u0000,\u027a\u0001\u0000\u0000\u0000.\u027e\u0001\u0000"+
		"\u0000\u00000\u0288\u0001\u0000\u0000\u00002\u028d\u0001\u0000\u0000\u0000"+
		"4\u0298\u0001\u0000\u0000\u00006\u029a\u0001\u0000\u0000\u00008\u02aa"+
		"\u0001\u0000\u0000\u0000:\u02ac\u0001\u0000\u0000\u0000<\u02bc\u0001\u0000"+
		"\u0000\u0000>\u02be\u0001\u0000\u0000\u0000@\u02c0\u0001\u0000\u0000\u0000"+
		"B\u02e7\u0001\u0000\u0000\u0000D\u02ec\u0001\u0000\u0000\u0000F\u02fa"+
		"\u0001\u0000\u0000\u0000H\u0300\u0001\u0000\u0000\u0000J\u0302\u0001\u0000"+
		"\u0000\u0000L\u0306\u0001\u0000\u0000\u0000N\u030c\u0001\u0000\u0000\u0000"+
		"P\u0313\u0001\u0000\u0000\u0000R\u031d\u0001\u0000\u0000\u0000T\u0322"+
		"\u0001\u0000\u0000\u0000V\u0373\u0001\u0000\u0000\u0000X\u0375\u0001\u0000"+
		"\u0000\u0000Z\u0377\u0001\u0000\u0000\u0000\\\u0384\u0001\u0000\u0000"+
		"\u0000^\u0386\u0001\u0000\u0000\u0000`\u0397\u0001\u0000\u0000\u0000b"+
		"\u03a2\u0001\u0000\u0000\u0000d\u03a4\u0001\u0000\u0000\u0000f\u03a8\u0001"+
		"\u0000\u0000\u0000h\u03b0\u0001\u0000\u0000\u0000j\u03b3\u0001\u0000\u0000"+
		"\u0000l\u03b6\u0001\u0000\u0000\u0000n\u03be\u0001\u0000\u0000\u0000p"+
		"\u03cb\u0001\u0000\u0000\u0000r\u03d2\u0001\u0000\u0000\u0000t\u03d4\u0001"+
		"\u0000\u0000\u0000v\u03dc\u0001\u0000\u0000\u0000x\u03e7\u0001\u0000\u0000"+
		"\u0000z\u03e9\u0001\u0000\u0000\u0000|\u03f1\u0001\u0000\u0000\u0000~"+
		"\u03f6\u0001\u0000\u0000\u0000\u0080\u03fc\u0001\u0000\u0000\u0000\u0082"+
		"\u0400\u0001\u0000\u0000\u0000\u0084\u0404\u0001\u0000\u0000\u0000\u0086"+
		"\u0409\u0001\u0000\u0000\u0000\u0088\u040d\u0001\u0000\u0000\u0000\u008a"+
		"\u0420\u0001\u0000\u0000\u0000\u008c\u0422\u0001\u0000\u0000\u0000\u008e"+
		"\u0427\u0001\u0000\u0000\u0000\u0090\u042b\u0001\u0000\u0000\u0000\u0092"+
		"\u042d\u0001\u0000\u0000\u0000\u0094\u042f\u0001\u0000\u0000\u0000\u0096"+
		"\u0431\u0001\u0000\u0000\u0000\u0098\u043c\u0001\u0000\u0000\u0000\u009a"+
		"\u043e\u0001\u0000\u0000\u0000\u009c\u0445\u0001\u0000\u0000\u0000\u009e"+
		"\u0452\u0001\u0000\u0000\u0000\u00a0\u0465\u0001\u0000\u0000\u0000\u00a2"+
		"\u0469\u0001\u0000\u0000\u0000\u00a4\u046b\u0001\u0000\u0000\u0000\u00a6"+
		"\u0479\u0001\u0000\u0000\u0000\u00a8\u048b\u0001\u0000\u0000\u0000\u00aa"+
		"\u0490\u0001\u0000\u0000\u0000\u00ac\u0498\u0001\u0000\u0000\u0000\u00ae"+
		"\u04ab\u0001\u0000\u0000\u0000\u00b0\u04b0\u0001\u0000\u0000\u0000\u00b2"+
		"\u04bb\u0001\u0000\u0000\u0000\u00b4\u04be\u0001\u0000\u0000\u0000\u00b6"+
		"\u04c8\u0001\u0000\u0000\u0000\u00b8\u04cc\u0001\u0000\u0000\u0000\u00ba"+
		"\u04ce\u0001\u0000\u0000\u0000\u00bc\u04d0\u0001\u0000\u0000\u0000\u00be"+
		"\u04d3\u0001\u0000\u0000\u0000\u00c0\u04dd\u0001\u0000\u0000\u0000\u00c2"+
		"\u04e4\u0001\u0000\u0000\u0000\u00c4\u04e7\u0001\u0000\u0000\u0000\u00c6"+
		"\u04f0\u0001\u0000\u0000\u0000\u00c8\u04f2\u0001\u0000\u0000\u0000\u00ca"+
		"\u0529\u0001\u0000\u0000\u0000\u00cc\u052b\u0001\u0000\u0000\u0000\u00ce"+
		"\u0533\u0001\u0000\u0000\u0000\u00d0\u053f\u0001\u0000\u0000\u0000\u00d2"+
		"\u054a\u0001\u0000\u0000\u0000\u00d4\u0558\u0001\u0000\u0000\u0000\u00d6"+
		"\u055a\u0001\u0000\u0000\u0000\u00d8\u0563\u0001\u0000\u0000\u0000\u00da"+
		"\u0565\u0001\u0000\u0000\u0000\u00dc\u0573\u0001\u0000\u0000\u0000\u00de"+
		"\u057d\u0001\u0000\u0000\u0000\u00e0\u057f\u0001\u0000\u0000\u0000\u00e2"+
		"\u0582\u0001\u0000\u0000\u0000\u00e4\u0590\u0001\u0000\u0000\u0000\u00e6"+
		"\u0595\u0001\u0000\u0000\u0000\u00e8\u05a0\u0001\u0000\u0000\u0000\u00ea"+
		"\u05a5\u0001\u0000\u0000\u0000\u00ec\u05b2\u0001\u0000\u0000\u0000\u00ee"+
		"\u05b7\u0001\u0000\u0000\u0000\u00f0\u05bf\u0001\u0000\u0000\u0000\u00f2"+
		"\u05cd\u0001\u0000\u0000\u0000\u00f4\u05d2\u0001\u0000\u0000\u0000\u00f6"+
		"\u05e4\u0001\u0000\u0000\u0000\u00f8\u05e6\u0001\u0000\u0000\u0000\u00fa"+
		"\u05ec\u0001\u0000\u0000\u0000\u00fc\u05f1\u0001\u0000\u0000\u0000\u00fe"+
		"\u05f9\u0001\u0000\u0000\u0000\u0100\u05fe\u0001\u0000\u0000\u0000\u0102"+
		"\u0606\u0001\u0000\u0000\u0000\u0104\u060e\u0001\u0000\u0000\u0000\u0106"+
		"\u0615\u0001\u0000\u0000\u0000\u0108\u0617\u0001\u0000\u0000\u0000\u010a"+
		"\u0620\u0001\u0000\u0000\u0000\u010c\u0628\u0001\u0000\u0000\u0000\u010e"+
		"\u062b\u0001\u0000\u0000\u0000\u0110\u0631\u0001\u0000\u0000\u0000\u0112"+
		"\u063a\u0001\u0000\u0000\u0000\u0114\u0642\u0001\u0000\u0000\u0000\u0116"+
		"\u0648\u0001\u0000\u0000\u0000\u0118\u0652\u0001\u0000\u0000\u0000\u011a"+
		"\u0654\u0001\u0000\u0000\u0000\u011c\u065a\u0001\u0000\u0000\u0000\u011e"+
		"\u0662\u0001\u0000\u0000\u0000\u0120\u066a\u0001\u0000\u0000\u0000\u0122"+
		"\u0671\u0001\u0000\u0000\u0000\u0124\u067f\u0001\u0000\u0000\u0000\u0126"+
		"\u0681\u0001\u0000\u0000\u0000\u0128\u0683\u0001\u0000\u0000\u0000\u012a"+
		"\u0687\u0001\u0000\u0000\u0000\u012c\u068b\u0001\u0000\u0000\u0000\u012e"+
		"\u0695\u0001\u0000\u0000\u0000\u0130\u0697\u0001\u0000\u0000\u0000\u0132"+
		"\u069d\u0001\u0000\u0000\u0000\u0134\u06a5\u0001\u0000\u0000\u0000\u0136"+
		"\u06b7\u0001\u0000\u0000\u0000\u0138\u06b9\u0001\u0000\u0000\u0000\u013a"+
		"\u06bf\u0001\u0000\u0000\u0000\u013c\u06ce\u0001\u0000\u0000\u0000\u013e"+
		"\u06d1\u0001\u0000\u0000\u0000\u0140\u06e2\u0001\u0000\u0000\u0000\u0142"+
		"\u06e4\u0001\u0000\u0000\u0000\u0144\u06e6\u0001\u0000\u0000\u0000\u0146"+
		"\u06ec\u0001\u0000\u0000\u0000\u0148\u06f2\u0001\u0000\u0000\u0000\u014a"+
		"\u06fc\u0001\u0000\u0000\u0000\u014c\u0700\u0001\u0000\u0000\u0000\u014e"+
		"\u0702\u0001\u0000\u0000\u0000\u0150\u0712\u0001\u0000\u0000\u0000\u0152"+
		"\u0724\u0001\u0000\u0000\u0000\u0154\u0726\u0001\u0000\u0000\u0000\u0156"+
		"\u0728\u0001\u0000\u0000\u0000\u0158\u0730\u0001\u0000\u0000\u0000\u015a"+
		"\u073f\u0001\u0000\u0000\u0000\u015c\u074e\u0001\u0000\u0000\u0000\u015e"+
		"\u0754\u0001\u0000\u0000\u0000\u0160\u075a\u0001\u0000\u0000\u0000\u0162"+
		"\u0760\u0001\u0000\u0000\u0000\u0164\u0764\u0001\u0000\u0000\u0000\u0166"+
		"\u0776\u0001\u0000\u0000\u0000\u0168\u0778\u0001\u0000\u0000\u0000\u016a"+
		"\u077f\u0001\u0000\u0000\u0000\u016c\u0788\u0001\u0000\u0000\u0000\u016e"+
		"\u078e\u0001\u0000\u0000\u0000\u0170\u0796\u0001\u0000\u0000\u0000\u0172"+
		"\u0799\u0001\u0000\u0000\u0000\u0174\u07a2\u0001\u0000\u0000\u0000\u0176"+
		"\u07a9\u0001\u0000\u0000\u0000\u0178\u07bd\u0001\u0000\u0000\u0000\u017a"+
		"\u07c1\u0001\u0000\u0000\u0000\u017c\u07c5\u0001\u0000\u0000\u0000\u017e"+
		"\u07e9\u0001\u0000\u0000\u0000\u0180\u07eb\u0001\u0000\u0000\u0000\u0182"+
		"\u0808\u0001\u0000\u0000\u0000\u0184\u080f\u0001\u0000\u0000\u0000\u0186"+
		"\u0811\u0001\u0000\u0000\u0000\u0188\u0817\u0001\u0000\u0000\u0000\u018a"+
		"\u083f\u0001\u0000\u0000\u0000\u018c\u0841\u0001\u0000\u0000\u0000\u018e"+
		"\u0868\u0001\u0000\u0000\u0000\u0190\u08a5\u0001\u0000\u0000\u0000\u0192"+
		"\u08a7\u0001\u0000\u0000\u0000\u0194\u08e1\u0001\u0000\u0000\u0000\u0196"+
		"\u08e5\u0001\u0000\u0000\u0000\u0198\u08f4\u0001\u0000\u0000\u0000\u019a"+
		"\u08f6\u0001\u0000\u0000\u0000\u019c\u0902\u0001\u0000\u0000\u0000\u019e"+
		"\u090e\u0001\u0000\u0000\u0000\u01a0\u091a\u0001\u0000\u0000\u0000\u01a2"+
		"\u0933\u0001\u0000\u0000\u0000\u01a4\u0984\u0001\u0000\u0000\u0000\u01a6"+
		"\u0986\u0001\u0000\u0000\u0000\u01a8\u09ca\u0001\u0000\u0000\u0000\u01aa"+
		"\u09cc\u0001\u0000\u0000\u0000\u01ac\u0a03\u0001\u0000\u0000\u0000\u01ae"+
		"\u0a05\u0001\u0000\u0000\u0000\u01b0\u0a33\u0001\u0000\u0000\u0000\u01b2"+
		"\u0a4b\u0001\u0000\u0000\u0000\u01b4\u0a4d\u0001\u0000\u0000\u0000\u01b6"+
		"\u0a57\u0001\u0000\u0000\u0000\u01b8\u0a5e\u0001\u0000\u0000\u0000\u01ba"+
		"\u0a62\u0001\u0000\u0000\u0000\u01bc\u0a64\u0001\u0000\u0000\u0000\u01be"+
		"\u0a72\u0001\u0000\u0000\u0000\u01c0\u0a74\u0001\u0000\u0000\u0000\u01c2"+
		"\u0a7e\u0001\u0000\u0000\u0000\u01c4\u0a82\u0001\u0000\u0000\u0000\u01c6"+
		"\u0a84\u0001\u0000\u0000\u0000\u01c8\u0a8a\u0001\u0000\u0000\u0000\u01ca"+
		"\u0a8c\u0001\u0000\u0000\u0000\u01cc\u0a8e\u0001\u0000\u0000\u0000\u01ce"+
		"\u0a90\u0001\u0000\u0000\u0000\u01d0\u0a92\u0001\u0000\u0000\u0000\u01d2"+
		"\u0a94\u0001\u0000\u0000\u0000\u01d4\u0aa3\u0001\u0000\u0000\u0000\u01d6"+
		"\u0aa5\u0001\u0000\u0000\u0000\u01d8\u0ab0\u0001\u0000\u0000\u0000\u01da"+
		"\u0abb\u0001\u0000\u0000\u0000\u01dc\u0ac6\u0001\u0000\u0000\u0000\u01de"+
		"\u0ad1\u0001\u0000\u0000\u0000\u01e0\u0adc\u0001\u0000\u0000\u0000\u01e2"+
		"\u0aea\u0001\u0000\u0000\u0000\u01e4\u0b06\u0001\u0000\u0000\u0000\u01e6"+
		"\u0b21\u0001\u0000\u0000\u0000\u01e8\u0b23\u0001\u0000\u0000\u0000\u01ea"+
		"\u0b33\u0001\u0000\u0000\u0000\u01ec\u0b50\u0001\u0000\u0000\u0000\u01ee"+
		"\u0b52\u0001\u0000\u0000\u0000\u01f0\u0b55\u0001\u0000\u0000\u0000\u01f2"+
		"\u0b5e\u0001\u0000\u0000\u0000\u01f4\u0b62\u0001\u0000\u0000\u0000\u01f6"+
		"\u0b6b\u0001\u0000\u0000\u0000\u01f8\u0b6e\u0001\u0000\u0000\u0000\u01fa"+
		"\u0b70\u0001\u0000\u0000\u0000\u01fc\u0b73\u0001\u0000\u0000\u0000\u01fe"+
		"\u0b90\u0001\u0000\u0000\u0000\u0200\u0201\u0007\u0000\u0000\u0000\u0201"+
		"\u0001\u0001\u0000\u0000\u0000\u0202\u0203\u0007\u0001\u0000\u0000\u0203"+
		"\u0003\u0001\u0000\u0000\u0000\u0204\u0207\u0003\u0006\u0003\u0000\u0205"+
		"\u0207\u0003\u000e\u0007\u0000\u0206\u0204\u0001\u0000\u0000\u0000\u0206"+
		"\u0205\u0001\u0000\u0000\u0000\u0207\u0005\u0001\u0000\u0000\u0000\u0208"+
		"\u020a\u0003\u00fa}\u0000\u0209\u0208\u0001\u0000\u0000\u0000\u020a\u020d"+
		"\u0001\u0000\u0000\u0000\u020b\u0209\u0001\u0000\u0000\u0000\u020b\u020c"+
		"\u0001\u0000\u0000\u0000\u020c\u020e\u0001\u0000\u0000\u0000\u020d\u020b"+
		"\u0001\u0000\u0000\u0000\u020e\u0217\u0003\b\u0004\u0000\u020f\u0211\u0003"+
		"\u00fa}\u0000\u0210\u020f\u0001\u0000\u0000\u0000\u0211\u0214\u0001\u0000"+
		"\u0000\u0000\u0212\u0210\u0001\u0000\u0000\u0000\u0212\u0213\u0001\u0000"+
		"\u0000\u0000\u0213\u0215\u0001\u0000\u0000\u0000\u0214\u0212\u0001\u0000"+
		"\u0000\u0000\u0215\u0217\u0005\u0011\u0000\u0000\u0216\u020b\u0001\u0000"+
		"\u0000\u0000\u0216\u0212\u0001\u0000\u0000\u0000\u0217\u0007\u0001\u0000"+
		"\u0000\u0000\u0218\u021b\u0003\n\u0005\u0000\u0219\u021b\u0003\f\u0006"+
		"\u0000\u021a\u0218\u0001\u0000\u0000\u0000\u021a\u0219\u0001\u0000\u0000"+
		"\u0000\u021b\t\u0001\u0000\u0000\u0000\u021c\u021d\u0007\u0002\u0000\u0000"+
		"\u021d\u000b\u0001\u0000\u0000\u0000\u021e\u021f\u0007\u0003\u0000\u0000"+
		"\u021f\r\u0001\u0000\u0000\u0000\u0220\u0224\u0003\u0010\b\u0000\u0221"+
		"\u0224\u0003\u001e\u000f\u0000\u0222\u0224\u0003 \u0010\u0000\u0223\u0220"+
		"\u0001\u0000\u0000\u0000\u0223\u0221\u0001\u0000\u0000\u0000\u0223\u0222"+
		"\u0001\u0000\u0000\u0000\u0224\u000f\u0001\u0000\u0000\u0000\u0225\u0228"+
		"\u0003\u0016\u000b\u0000\u0226\u0228\u0003\u001c\u000e\u0000\u0227\u0225"+
		"\u0001\u0000\u0000\u0000\u0227\u0226\u0001\u0000\u0000\u0000\u0228\u022d"+
		"\u0001\u0000\u0000\u0000\u0229\u022c\u0003\u0014\n\u0000\u022a\u022c\u0003"+
		"\u001a\r\u0000\u022b\u0229\u0001\u0000\u0000\u0000\u022b\u022a\u0001\u0000"+
		"\u0000\u0000\u022c\u022f\u0001\u0000\u0000\u0000\u022d\u022b\u0001\u0000"+
		"\u0000\u0000\u022d\u022e\u0001\u0000\u0000\u0000\u022e\u0011\u0001\u0000"+
		"\u0000\u0000\u022f\u022d\u0001\u0000\u0000\u0000\u0230\u0232\u0003\u00fc"+
		"~\u0000\u0231\u0233\u0003,\u0016\u0000\u0232\u0231\u0001\u0000\u0000\u0000"+
		"\u0232\u0233\u0001\u0000\u0000\u0000\u0233\u023b\u0001\u0000\u0000\u0000"+
		"\u0234\u0235\u0003\u0010\b\u0000\u0235\u0236\u0005O\u0000\u0000\u0236"+
		"\u0238\u0003\u00fc~\u0000\u0237\u0239\u0003,\u0016\u0000\u0238\u0237\u0001"+
		"\u0000\u0000\u0000\u0238\u0239\u0001\u0000\u0000\u0000\u0239\u023b\u0001"+
		"\u0000\u0000\u0000\u023a\u0230\u0001\u0000\u0000\u0000\u023a\u0234\u0001"+
		"\u0000\u0000\u0000\u023b\u0013\u0001\u0000\u0000\u0000\u023c\u023d\u0005"+
		"O\u0000\u0000\u023d\u023f\u0003\u00fc~\u0000\u023e\u0240\u0003,\u0016"+
		"\u0000\u023f\u023e\u0001\u0000\u0000\u0000\u023f\u0240\u0001\u0000\u0000"+
		"\u0000\u0240\u0015\u0001\u0000\u0000\u0000\u0241\u0243\u0003\u00fc~\u0000"+
		"\u0242\u0244\u0003,\u0016\u0000\u0243\u0242\u0001\u0000\u0000\u0000\u0243"+
		"\u0244\u0001\u0000\u0000\u0000\u0244\u0017\u0001\u0000\u0000\u0000\u0245"+
		"\u0246\u0003\u0012\t\u0000\u0246\u0019\u0001\u0000\u0000\u0000\u0247\u0248"+
		"\u0003\u0014\n\u0000\u0248\u001b\u0001\u0000\u0000\u0000\u0249\u024a\u0003"+
		"\u0016\u000b\u0000\u024a\u001d\u0001\u0000\u0000\u0000\u024b\u024c\u0003"+
		"\u00fc~\u0000\u024c\u001f\u0001\u0000\u0000\u0000\u024d\u024e\u0003\u0006"+
		"\u0003\u0000\u024e\u024f\u0003\"\u0011\u0000\u024f\u0257\u0001\u0000\u0000"+
		"\u0000\u0250\u0251\u0003\u0010\b\u0000\u0251\u0252\u0003\"\u0011\u0000"+
		"\u0252\u0257\u0001\u0000\u0000\u0000\u0253\u0254\u0003\u001e\u000f\u0000"+
		"\u0254\u0255\u0003\"\u0011\u0000\u0255\u0257\u0001\u0000\u0000\u0000\u0256"+
		"\u024d\u0001\u0000\u0000\u0000\u0256\u0250\u0001\u0000\u0000\u0000\u0256"+
		"\u0253\u0001\u0000\u0000\u0000\u0257!\u0001\u0000\u0000\u0000\u0258\u025c"+
		"\u0003\u00fe\u007f\u0000\u0259\u025b\u0003\u00fe\u007f\u0000\u025a\u0259"+
		"\u0001\u0000\u0000\u0000\u025b\u025e\u0001\u0000\u0000\u0000\u025c\u025a"+
		"\u0001\u0000\u0000\u0000\u025c\u025d\u0001\u0000\u0000\u0000\u025d#\u0001"+
		"\u0000\u0000\u0000\u025e\u025c\u0001\u0000\u0000\u0000\u025f\u0261\u0003"+
		"&\u0013\u0000\u0260\u025f\u0001\u0000\u0000\u0000\u0261\u0264\u0001\u0000"+
		"\u0000\u0000\u0262\u0260\u0001\u0000\u0000\u0000\u0262\u0263\u0001\u0000"+
		"\u0000\u0000\u0263\u0265\u0001\u0000\u0000\u0000\u0264\u0262\u0001\u0000"+
		"\u0000\u0000\u0265\u0267\u0003\u0000\u0000\u0000\u0266\u0268\u0003(\u0014"+
		"\u0000\u0267\u0266\u0001\u0000\u0000\u0000\u0267\u0268\u0001\u0000\u0000"+
		"\u0000\u0268%\u0001\u0000\u0000\u0000\u0269\u026a\u0003\u00fa}\u0000\u026a"+
		"\'\u0001\u0000\u0000\u0000\u026b\u026c\u0005\u001f\u0000\u0000\u026c\u0276"+
		"\u0003\u001e\u000f\u0000\u026d\u026e\u0005\u001f\u0000\u0000\u026e\u0272"+
		"\u0003\u0010\b\u0000\u026f\u0271\u0003*\u0015\u0000\u0270\u026f\u0001"+
		"\u0000\u0000\u0000\u0271\u0274\u0001\u0000\u0000\u0000\u0272\u0270\u0001"+
		"\u0000\u0000\u0000\u0272\u0273\u0001\u0000\u0000\u0000\u0273\u0276\u0001"+
		"\u0000\u0000\u0000\u0274\u0272\u0001\u0000\u0000\u0000\u0275\u026b\u0001"+
		"\u0000\u0000\u0000\u0275\u026d\u0001\u0000\u0000\u0000\u0276)\u0001\u0000"+
		"\u0000\u0000\u0277\u0278\u0005b\u0000\u0000\u0278\u0279\u0003\u0018\f"+
		"\u0000\u0279+\u0001\u0000\u0000\u0000\u027a\u027b\u0005R\u0000\u0000\u027b"+
		"\u027c\u0003.\u0017\u0000\u027c\u027d\u0005Q\u0000\u0000\u027d-\u0001"+
		"\u0000\u0000\u0000\u027e\u0283\u00030\u0018\u0000\u027f\u0280\u0005N\u0000"+
		"\u0000\u0280\u0282\u00030\u0018\u0000\u0281\u027f\u0001\u0000\u0000\u0000"+
		"\u0282\u0285\u0001\u0000\u0000\u0000\u0283\u0281\u0001\u0000\u0000\u0000"+
		"\u0283\u0284\u0001\u0000\u0000\u0000\u0284/\u0001\u0000\u0000\u0000\u0285"+
		"\u0283\u0001\u0000\u0000\u0000\u0286\u0289\u0003\u000e\u0007\u0000\u0287"+
		"\u0289\u00032\u0019\u0000\u0288\u0286\u0001\u0000\u0000\u0000\u0288\u0287"+
		"\u0001\u0000\u0000\u0000\u02891\u0001\u0000\u0000\u0000\u028a\u028c\u0003"+
		"\u00fa}\u0000\u028b\u028a\u0001\u0000\u0000\u0000\u028c\u028f\u0001\u0000"+
		"\u0000\u0000\u028d\u028b\u0001\u0000\u0000\u0000\u028d\u028e\u0001\u0000"+
		"\u0000\u0000\u028e\u0290\u0001\u0000\u0000\u0000\u028f\u028d\u0001\u0000"+
		"\u0000\u0000\u0290\u0292\u0005U\u0000\u0000\u0291\u0293\u00034\u001a\u0000"+
		"\u0292\u0291\u0001\u0000\u0000\u0000\u0292\u0293\u0001\u0000\u0000\u0000"+
		"\u02933\u0001\u0000\u0000\u0000\u0294\u0295\u0005\u001f\u0000\u0000\u0295"+
		"\u0299\u0003\u000e\u0007\u0000\u0296\u0297\u00056\u0000\u0000\u0297\u0299"+
		"\u0003\u000e\u0007\u0000\u0298\u0294\u0001\u0000\u0000\u0000\u0298\u0296"+
		"\u0001\u0000\u0000\u0000\u02995\u0001\u0000\u0000\u0000\u029a\u029b\u0006"+
		"\u001b\uffff\uffff\u0000\u029b\u029c\u0003\u0000\u0000\u0000\u029c\u02a2"+
		"\u0001\u0000\u0000\u0000\u029d\u029e\n\u0001\u0000\u0000\u029e\u029f\u0005"+
		"O\u0000\u0000\u029f\u02a1\u0003\u0000\u0000\u0000\u02a0\u029d\u0001\u0000"+
		"\u0000\u0000\u02a1\u02a4\u0001\u0000\u0000\u0000\u02a2\u02a0\u0001\u0000"+
		"\u0000\u0000\u02a2\u02a3\u0001\u0000\u0000\u0000\u02a37\u0001\u0000\u0000"+
		"\u0000\u02a4\u02a2\u0001\u0000\u0000\u0000\u02a5\u02ab\u0003\u0000\u0000"+
		"\u0000\u02a6\u02a7\u0003:\u001d\u0000\u02a7\u02a8\u0005O\u0000\u0000\u02a8"+
		"\u02a9\u0003\u0000\u0000\u0000\u02a9\u02ab\u0001\u0000\u0000\u0000\u02aa"+
		"\u02a5\u0001\u0000\u0000\u0000\u02aa\u02a6\u0001\u0000\u0000\u0000\u02ab"+
		"9\u0001\u0000\u0000\u0000\u02ac\u02ad\u0006\u001d\uffff\uffff\u0000\u02ad"+
		"\u02ae\u0003\u0000\u0000\u0000\u02ae\u02b4\u0001\u0000\u0000\u0000\u02af"+
		"\u02b0\n\u0001\u0000\u0000\u02b0\u02b1\u0005O\u0000\u0000\u02b1\u02b3"+
		"\u0003\u0000\u0000\u0000\u02b2\u02af\u0001\u0000\u0000\u0000\u02b3\u02b6"+
		"\u0001\u0000\u0000\u0000\u02b4\u02b2\u0001\u0000\u0000\u0000\u02b4\u02b5"+
		"\u0001\u0000\u0000\u0000\u02b5;\u0001\u0000\u0000\u0000\u02b6\u02b4\u0001"+
		"\u0000\u0000\u0000\u02b7\u02bd\u0003\u0000\u0000\u0000\u02b8\u02b9\u0003"+
		"@ \u0000\u02b9\u02ba\u0005O\u0000\u0000\u02ba\u02bb\u0003\u0000\u0000"+
		"\u0000\u02bb\u02bd\u0001\u0000\u0000\u0000\u02bc\u02b7\u0001\u0000\u0000"+
		"\u0000\u02bc\u02b8\u0001\u0000\u0000\u0000\u02bd=\u0001\u0000\u0000\u0000"+
		"\u02be\u02bf\u0003\u0000\u0000\u0000\u02bf?\u0001\u0000\u0000\u0000\u02c0"+
		"\u02c1\u0006 \uffff\uffff\u0000\u02c1\u02c2\u0003\u0000\u0000\u0000\u02c2"+
		"\u02c8\u0001\u0000\u0000\u0000\u02c3\u02c4\n\u0001\u0000\u0000\u02c4\u02c5"+
		"\u0005O\u0000\u0000\u02c5\u02c7\u0003\u0000\u0000\u0000\u02c6\u02c3\u0001"+
		"\u0000\u0000\u0000\u02c7\u02ca\u0001\u0000\u0000\u0000\u02c8\u02c6\u0001"+
		"\u0000\u0000\u0000\u02c8\u02c9\u0001\u0000\u0000\u0000\u02c9A\u0001\u0000"+
		"\u0000\u0000\u02ca\u02c8\u0001\u0000\u0000\u0000\u02cb\u02cc\u0003D\""+
		"\u0000\u02cc\u02cd\u0005\u0000\u0000\u0001\u02cd\u02e8\u0001\u0000\u0000"+
		"\u0000\u02ce\u02d0\u0003D\"\u0000\u02cf\u02ce\u0001\u0000\u0000\u0000"+
		"\u02cf\u02d0\u0001\u0000\u0000\u0000\u02d0\u02d4\u0001\u0000\u0000\u0000"+
		"\u02d1\u02d3\u0003H$\u0000\u02d2\u02d1\u0001\u0000\u0000\u0000\u02d3\u02d6"+
		"\u0001\u0000\u0000\u0000\u02d4\u02d2\u0001\u0000\u0000\u0000\u02d4\u02d5"+
		"\u0001\u0000\u0000\u0000\u02d5\u02da\u0001\u0000\u0000\u0000\u02d6\u02d4"+
		"\u0001\u0000\u0000\u0000\u02d7\u02d9\u0003R)\u0000\u02d8\u02d7\u0001\u0000"+
		"\u0000\u0000\u02d9\u02dc\u0001\u0000\u0000\u0000\u02da\u02d8\u0001\u0000"+
		"\u0000\u0000\u02da\u02db\u0001\u0000\u0000\u0000\u02db\u02dd\u0001\u0000"+
		"\u0000\u0000\u02dc\u02da\u0001\u0000\u0000\u0000\u02dd\u02e8\u0005\u0000"+
		"\u0000\u0001\u02de\u02e0\u0003H$\u0000\u02df\u02de\u0001\u0000\u0000\u0000"+
		"\u02e0\u02e3\u0001\u0000\u0000\u0000\u02e1\u02df\u0001\u0000\u0000\u0000"+
		"\u02e1\u02e2\u0001\u0000\u0000\u0000\u02e2\u02e4\u0001\u0000\u0000\u0000"+
		"\u02e3\u02e1\u0001\u0000\u0000\u0000\u02e4\u02e5\u0003T*\u0000\u02e5\u02e6"+
		"\u0005\u0000\u0000\u0001\u02e6\u02e8\u0001\u0000\u0000\u0000\u02e7\u02cb"+
		"\u0001\u0000\u0000\u0000\u02e7\u02cf\u0001\u0000\u0000\u0000\u02e7\u02e1"+
		"\u0001\u0000\u0000\u0000\u02e8C\u0001\u0000\u0000\u0000\u02e9\u02eb\u0003"+
		"F#\u0000\u02ea\u02e9\u0001\u0000\u0000\u0000\u02eb\u02ee\u0001\u0000\u0000"+
		"\u0000\u02ec\u02ea\u0001\u0000\u0000\u0000\u02ec\u02ed\u0001\u0000\u0000"+
		"\u0000\u02ed\u02ef\u0001\u0000\u0000\u0000\u02ee\u02ec\u0001\u0000\u0000"+
		"\u0000\u02ef\u02f0\u0005.\u0000\u0000\u02f0\u02f5\u0003\u0000\u0000\u0000"+
		"\u02f1\u02f2\u0005O\u0000\u0000\u02f2\u02f4\u0003\u0000\u0000\u0000\u02f3"+
		"\u02f1\u0001\u0000\u0000\u0000\u02f4\u02f7\u0001\u0000\u0000\u0000\u02f5"+
		"\u02f3\u0001\u0000\u0000\u0000\u02f5\u02f6\u0001\u0000\u0000\u0000\u02f6"+
		"\u02f8\u0001\u0000\u0000\u0000\u02f7\u02f5\u0001\u0000\u0000\u0000\u02f8"+
		"\u02f9\u0005M\u0000\u0000\u02f9E\u0001\u0000\u0000\u0000\u02fa\u02fb\u0003"+
		"\u00fa}\u0000\u02fbG\u0001\u0000\u0000\u0000\u02fc\u0301\u0003J%\u0000"+
		"\u02fd\u0301\u0003L&\u0000\u02fe\u0301\u0003N\'\u0000\u02ff\u0301\u0003"+
		"P(\u0000\u0300\u02fc\u0001\u0000\u0000\u0000\u0300\u02fd\u0001\u0000\u0000"+
		"\u0000\u0300\u02fe\u0001\u0000\u0000\u0000\u0300\u02ff\u0001\u0000\u0000"+
		"\u0000\u0301I\u0001\u0000\u0000\u0000\u0302\u0303\u0005\'\u0000\u0000"+
		"\u0303\u0304\u00038\u001c\u0000\u0304\u0305\u0005M\u0000\u0000\u0305K"+
		"\u0001\u0000\u0000\u0000\u0306\u0307\u0005\'\u0000\u0000\u0307\u0308\u0003"+
		":\u001d\u0000\u0308\u0309\u0005O\u0000\u0000\u0309\u030a\u0005`\u0000"+
		"\u0000\u030a\u030b\u0005M\u0000\u0000\u030bM\u0001\u0000\u0000\u0000\u030c"+
		"\u030d\u0005\'\u0000\u0000\u030d\u030e\u00054\u0000\u0000\u030e\u030f"+
		"\u00038\u001c\u0000\u030f\u0310\u0005O\u0000\u0000\u0310\u0311\u0003\u0000"+
		"\u0000\u0000\u0311\u0312\u0005M\u0000\u0000\u0312O\u0001\u0000\u0000\u0000"+
		"\u0313\u0314\u0005\'\u0000\u0000\u0314\u0315\u00054\u0000\u0000\u0315"+
		"\u0316\u00038\u001c\u0000\u0316\u0317\u0005O\u0000\u0000\u0317\u0318\u0005"+
		"`\u0000\u0000\u0318\u0319\u0005M\u0000\u0000\u0319Q\u0001\u0000\u0000"+
		"\u0000\u031a\u031e\u0003\\.\u0000\u031b\u031e\u0003\u00d8l\u0000\u031c"+
		"\u031e\u0005M\u0000\u0000\u031d\u031a\u0001\u0000\u0000\u0000\u031d\u031b"+
		"\u0001\u0000\u0000\u0000\u031d\u031c\u0001\u0000\u0000\u0000\u031eS\u0001"+
		"\u0000\u0000\u0000\u031f\u0321\u0003\u00fa}\u0000\u0320\u031f\u0001\u0000"+
		"\u0000\u0000\u0321\u0324\u0001\u0000\u0000\u0000\u0322\u0320\u0001\u0000"+
		"\u0000\u0000\u0322\u0323\u0001\u0000\u0000\u0000\u0323\u0326\u0001\u0000"+
		"\u0000\u0000\u0324\u0322\u0001\u0000\u0000\u0000\u0325\u0327\u0005\u0001"+
		"\u0000\u0000\u0326\u0325\u0001\u0000\u0000\u0000\u0326\u0327\u0001\u0000"+
		"\u0000\u0000\u0327\u0328\u0001\u0000\u0000\u0000\u0328\u0329\u0005\u0002"+
		"\u0000\u0000\u0329\u032e\u0005s\u0000\u0000\u032a\u032b\u0005O\u0000\u0000"+
		"\u032b\u032d\u0005s\u0000\u0000\u032c\u032a\u0001\u0000\u0000\u0000\u032d"+
		"\u0330\u0001\u0000\u0000\u0000\u032e\u032c\u0001\u0000\u0000\u0000\u032e"+
		"\u032f\u0001\u0000\u0000\u0000\u032f\u0331\u0001\u0000\u0000\u0000\u0330"+
		"\u032e\u0001\u0000\u0000\u0000\u0331\u0335\u0005I\u0000\u0000\u0332\u0334"+
		"\u0003V+\u0000\u0333\u0332\u0001\u0000\u0000\u0000\u0334\u0337\u0001\u0000"+
		"\u0000\u0000\u0335\u0333\u0001\u0000\u0000\u0000\u0335\u0336\u0001\u0000"+
		"\u0000\u0000\u0336\u0338\u0001\u0000\u0000\u0000\u0337\u0335\u0001\u0000"+
		"\u0000\u0000\u0338\u0339\u0005J\u0000\u0000\u0339U\u0001\u0000\u0000\u0000"+
		"\u033a\u033e\u0005\u0003\u0000\u0000\u033b\u033d\u0003X,\u0000\u033c\u033b"+
		"\u0001\u0000\u0000\u0000\u033d\u0340\u0001\u0000\u0000\u0000\u033e\u033c"+
		"\u0001\u0000\u0000\u0000\u033e\u033f\u0001\u0000\u0000\u0000\u033f\u0341"+
		"\u0001\u0000\u0000\u0000\u0340\u033e\u0001\u0000\u0000\u0000\u0341\u0342"+
		"\u0003Z-\u0000\u0342\u0343\u0005M\u0000\u0000\u0343\u0374\u0001\u0000"+
		"\u0000\u0000\u0344\u0345\u0005\u0005\u0000\u0000\u0345\u034f\u00036\u001b"+
		"\u0000\u0346\u0347\u0005\u0007\u0000\u0000\u0347\u034c\u0003Z-\u0000\u0348"+
		"\u0349\u0005N\u0000\u0000\u0349\u034b\u0003Z-\u0000\u034a\u0348\u0001"+
		"\u0000\u0000\u0000\u034b\u034e\u0001\u0000\u0000\u0000\u034c\u034a\u0001"+
		"\u0000\u0000\u0000\u034c\u034d\u0001\u0000\u0000\u0000\u034d\u0350\u0001"+
		"\u0000\u0000\u0000\u034e\u034c\u0001\u0000\u0000\u0000\u034f\u0346\u0001"+
		"\u0000\u0000\u0000\u034f\u0350\u0001\u0000\u0000\u0000\u0350\u0351\u0001"+
		"\u0000\u0000\u0000\u0351\u0352\u0005M\u0000\u0000\u0352\u0374\u0001\u0000"+
		"\u0000\u0000\u0353\u0354\u0005\u0006\u0000\u0000\u0354\u035e\u00036\u001b"+
		"\u0000\u0355\u0356\u0005\u0007\u0000\u0000\u0356\u035b\u0003Z-\u0000\u0357"+
		"\u0358\u0005N\u0000\u0000\u0358\u035a\u0003Z-\u0000\u0359\u0357\u0001"+
		"\u0000\u0000\u0000\u035a\u035d\u0001\u0000\u0000\u0000\u035b\u0359\u0001"+
		"\u0000\u0000\u0000\u035b\u035c\u0001\u0000\u0000\u0000\u035c\u035f\u0001"+
		"\u0000\u0000\u0000\u035d\u035b\u0001\u0000\u0000\u0000\u035e\u0355\u0001"+
		"\u0000\u0000\u0000\u035e\u035f\u0001\u0000\u0000\u0000\u035f\u0360\u0001"+
		"\u0000\u0000\u0000\u0360\u0361\u0005M\u0000\u0000\u0361\u0374\u0001\u0000"+
		"\u0000\u0000\u0362\u0363\u0005\b\u0000\u0000\u0363\u0364\u00038\u001c"+
		"\u0000\u0364\u0365\u0005M\u0000\u0000\u0365\u0374\u0001\u0000\u0000\u0000"+
		"\u0366\u0367\u0005\t\u0000\u0000\u0367\u0368\u00038\u001c\u0000\u0368"+
		"\u0369\u0005\n\u0000\u0000\u0369\u036e\u00038\u001c\u0000\u036a\u036b"+
		"\u0005N\u0000\u0000\u036b\u036d\u00038\u001c\u0000\u036c\u036a\u0001\u0000"+
		"\u0000\u0000\u036d\u0370\u0001\u0000\u0000\u0000\u036e\u036c\u0001\u0000"+
		"\u0000\u0000\u036e\u036f\u0001\u0000\u0000\u0000\u036f\u0371\u0001\u0000"+
		"\u0000\u0000\u0370\u036e\u0001\u0000\u0000\u0000\u0371\u0372\u0005M\u0000"+
		"\u0000\u0372\u0374\u0001\u0000\u0000\u0000\u0373\u033a\u0001\u0000\u0000"+
		"\u0000\u0373\u0344\u0001\u0000\u0000\u0000\u0373\u0353\u0001\u0000\u0000"+
		"\u0000\u0373\u0362\u0001\u0000\u0000\u0000\u0373\u0366\u0001\u0000\u0000"+
		"\u0000\u0374W\u0001\u0000\u0000\u0000\u0375\u0376\u0007\u0004\u0000\u0000"+
		"\u0376Y\u0001\u0000\u0000\u0000\u0377\u0378\u0006-\uffff\uffff\u0000\u0378"+
		"\u0379\u0005s\u0000\u0000\u0379\u037f\u0001\u0000\u0000\u0000\u037a\u037b"+
		"\n\u0001\u0000\u0000\u037b\u037c\u0005O\u0000\u0000\u037c\u037e\u0005"+
		"s\u0000\u0000\u037d\u037a\u0001\u0000\u0000\u0000\u037e\u0381\u0001\u0000"+
		"\u0000\u0000\u037f\u037d\u0001\u0000\u0000\u0000\u037f\u0380\u0001\u0000"+
		"\u0000\u0000\u0380[\u0001\u0000\u0000\u0000\u0381\u037f\u0001\u0000\u0000"+
		"\u0000\u0382\u0385\u0003^/\u0000\u0383\u0385\u0003\u00ccf\u0000\u0384"+
		"\u0382\u0001\u0000\u0000\u0000\u0384\u0383\u0001\u0000\u0000\u0000\u0385"+
		"]\u0001\u0000\u0000\u0000\u0386\u0387\u0003`0\u0000\u0387\u0388\u0005"+
		"\u0017\u0000\u0000\u0388\u038a\u0003\u0000\u0000\u0000\u0389\u038b\u0003"+
		"d2\u0000\u038a\u0389\u0001\u0000\u0000\u0000\u038a\u038b\u0001\u0000\u0000"+
		"\u0000\u038b\u038d\u0001\u0000\u0000\u0000\u038c\u038e\u0003h4\u0000\u038d"+
		"\u038c\u0001\u0000\u0000\u0000\u038d\u038e\u0001\u0000\u0000\u0000\u038e"+
		"\u0390\u0001\u0000\u0000\u0000\u038f\u0391\u0003j5\u0000\u0390\u038f\u0001"+
		"\u0000\u0000\u0000\u0390\u0391\u0001\u0000\u0000\u0000\u0391\u0392\u0001"+
		"\u0000\u0000\u0000\u0392\u0393\u0003n7\u0000\u0393_\u0001\u0000\u0000"+
		"\u0000\u0394\u0396\u0003b1\u0000\u0395\u0394\u0001\u0000\u0000\u0000\u0396"+
		"\u0399\u0001\u0000\u0000\u0000\u0397\u0395\u0001\u0000\u0000\u0000\u0397"+
		"\u0398\u0001\u0000\u0000\u0000\u0398a\u0001\u0000\u0000\u0000\u0399\u0397"+
		"\u0001\u0000\u0000\u0000\u039a\u03a3\u0003\u00fa}\u0000\u039b\u03a3\u0005"+
		"1\u0000\u0000\u039c\u03a3\u00050\u0000\u0000\u039d\u03a3\u0005/\u0000"+
		"\u0000\u039e\u03a3\u0005\u000f\u0000\u0000\u039f\u03a3\u00054\u0000\u0000"+
		"\u03a0\u03a3\u0005 \u0000\u0000\u03a1\u03a3\u00055\u0000\u0000\u03a2\u039a"+
		"\u0001\u0000\u0000\u0000\u03a2\u039b\u0001\u0000\u0000\u0000\u03a2\u039c"+
		"\u0001\u0000\u0000\u0000\u03a2\u039d\u0001\u0000\u0000\u0000\u03a2\u039e"+
		"\u0001\u0000\u0000\u0000\u03a2\u039f\u0001\u0000\u0000\u0000\u03a2\u03a0"+
		"\u0001\u0000\u0000\u0000\u03a2\u03a1\u0001\u0000\u0000\u0000\u03a3c\u0001"+
		"\u0000\u0000\u0000\u03a4\u03a5\u0005R\u0000\u0000\u03a5\u03a6\u0003f3"+
		"\u0000\u03a6\u03a7\u0005Q\u0000\u0000\u03a7e\u0001\u0000\u0000\u0000\u03a8"+
		"\u03ad\u0003$\u0012\u0000\u03a9\u03aa\u0005N\u0000\u0000\u03aa\u03ac\u0003"+
		"$\u0012\u0000\u03ab\u03a9\u0001\u0000\u0000\u0000\u03ac\u03af\u0001\u0000"+
		"\u0000\u0000\u03ad\u03ab\u0001\u0000\u0000\u0000\u03ad\u03ae\u0001\u0000"+
		"\u0000\u0000\u03aeg\u0001\u0000\u0000\u0000\u03af\u03ad\u0001\u0000\u0000"+
		"\u0000\u03b0\u03b1\u0005\u001f\u0000\u0000\u03b1\u03b2\u0003\u0012\t\u0000"+
		"\u03b2i\u0001\u0000\u0000\u0000\u03b3\u03b4\u0005&\u0000\u0000\u03b4\u03b5"+
		"\u0003l6\u0000\u03b5k\u0001\u0000\u0000\u0000\u03b6\u03bb\u0003\u0018"+
		"\f\u0000\u03b7\u03b8\u0005N\u0000\u0000\u03b8\u03ba\u0003\u0018\f\u0000"+
		"\u03b9\u03b7\u0001\u0000\u0000\u0000\u03ba\u03bd\u0001\u0000\u0000\u0000"+
		"\u03bb\u03b9\u0001\u0000\u0000\u0000\u03bb\u03bc\u0001\u0000\u0000\u0000"+
		"\u03bcm\u0001\u0000\u0000\u0000\u03bd\u03bb\u0001\u0000\u0000\u0000\u03be"+
		"\u03c2\u0005I\u0000\u0000\u03bf\u03c1\u0003p8\u0000\u03c0\u03bf\u0001"+
		"\u0000\u0000\u0000\u03c1\u03c4\u0001\u0000\u0000\u0000\u03c2\u03c0\u0001"+
		"\u0000\u0000\u0000\u03c2\u03c3\u0001\u0000\u0000\u0000\u03c3\u03c5\u0001"+
		"\u0000\u0000\u0000\u03c4\u03c2\u0001\u0000\u0000\u0000\u03c5\u03c6\u0005"+
		"J\u0000\u0000\u03c6o\u0001\u0000\u0000\u0000\u03c7\u03cc\u0003r9\u0000"+
		"\u03c8\u03cc\u0003\u00ba]\u0000\u03c9\u03cc\u0003\u00bc^\u0000\u03ca\u03cc"+
		"\u0003\u00be_\u0000\u03cb\u03c7\u0001\u0000\u0000\u0000\u03cb\u03c8\u0001"+
		"\u0000\u0000\u0000\u03cb\u03c9\u0001\u0000\u0000\u0000\u03cb\u03ca\u0001"+
		"\u0000\u0000\u0000\u03ccq\u0001\u0000\u0000\u0000\u03cd\u03d3\u0003t:"+
		"\u0000\u03ce\u03d3\u0003\u009aM\u0000\u03cf\u03d3\u0003\\.\u0000\u03d0"+
		"\u03d3\u0003\u00d8l\u0000\u03d1\u03d3\u0005M\u0000\u0000\u03d2\u03cd\u0001"+
		"\u0000\u0000\u0000\u03d2\u03ce\u0001\u0000\u0000\u0000\u03d2\u03cf\u0001"+
		"\u0000\u0000\u0000\u03d2\u03d0\u0001\u0000\u0000\u0000\u03d2\u03d1\u0001"+
		"\u0000\u0000\u0000\u03d3s\u0001\u0000\u0000\u0000\u03d4\u03d5\u0003v;"+
		"\u0000\u03d5\u03d6\u0003\u0082A\u0000\u03d6\u03d7\u0003z=\u0000\u03d7"+
		"\u03d8\u0005M\u0000\u0000\u03d8u\u0001\u0000\u0000\u0000\u03d9\u03db\u0003"+
		"x<\u0000\u03da\u03d9\u0001\u0000\u0000\u0000\u03db\u03de\u0001\u0000\u0000"+
		"\u0000\u03dc\u03da\u0001\u0000\u0000\u0000\u03dc\u03dd\u0001\u0000\u0000"+
		"\u0000\u03ddw\u0001\u0000\u0000\u0000\u03de\u03dc\u0001\u0000\u0000\u0000"+
		"\u03df\u03e8\u0003\u00fa}\u0000\u03e0\u03e8\u00051\u0000\u0000\u03e1\u03e8"+
		"\u00050\u0000\u0000\u03e2\u03e8\u0005/\u0000\u0000\u03e3\u03e8\u00054"+
		"\u0000\u0000\u03e4\u03e8\u0005 \u0000\u0000\u03e5\u03e8\u0005<\u0000\u0000"+
		"\u03e6\u03e8\u0005?\u0000\u0000\u03e7\u03df\u0001\u0000\u0000\u0000\u03e7"+
		"\u03e0\u0001\u0000\u0000\u0000\u03e7\u03e1\u0001\u0000\u0000\u0000\u03e7"+
		"\u03e2\u0001\u0000\u0000\u0000\u03e7\u03e3\u0001\u0000\u0000\u0000\u03e7"+
		"\u03e4\u0001\u0000\u0000\u0000\u03e7\u03e5\u0001\u0000\u0000\u0000\u03e7"+
		"\u03e6\u0001\u0000\u0000\u0000\u03e8y\u0001\u0000\u0000\u0000\u03e9\u03ee"+
		"\u0003|>\u0000\u03ea\u03eb\u0005N\u0000\u0000\u03eb\u03ed\u0003|>\u0000"+
		"\u03ec\u03ea\u0001\u0000\u0000\u0000\u03ed\u03f0\u0001\u0000\u0000\u0000"+
		"\u03ee\u03ec\u0001\u0000\u0000\u0000\u03ee\u03ef\u0001\u0000\u0000\u0000"+
		"\u03ef{\u0001\u0000\u0000\u0000\u03f0\u03ee\u0001\u0000\u0000\u0000\u03f1"+
		"\u03f4\u0003~?\u0000\u03f2\u03f3\u0005P\u0000\u0000\u03f3\u03f5\u0003"+
		"\u0080@\u0000\u03f4\u03f2\u0001\u0000\u0000\u0000\u03f4\u03f5\u0001\u0000"+
		"\u0000\u0000\u03f5}\u0001\u0000\u0000\u0000\u03f6\u03f8\u0003\u0000\u0000"+
		"\u0000\u03f7\u03f9\u0003\"\u0011\u0000\u03f8\u03f7\u0001\u0000\u0000\u0000"+
		"\u03f8\u03f9\u0001\u0000\u0000\u0000\u03f9\u007f\u0001\u0000\u0000\u0000"+
		"\u03fa\u03fd\u0003\u01ba\u00dd\u0000\u03fb\u03fd\u0003\u0110\u0088\u0000"+
		"\u03fc\u03fa\u0001\u0000\u0000\u0000\u03fc\u03fb\u0001\u0000\u0000\u0000"+
		"\u03fd\u0081\u0001\u0000\u0000\u0000\u03fe\u0401\u0003\u0084B\u0000\u03ff"+
		"\u0401\u0003\u0086C\u0000\u0400\u03fe\u0001\u0000\u0000\u0000\u0400\u03ff"+
		"\u0001\u0000\u0000\u0000\u0401\u0083\u0001\u0000\u0000\u0000\u0402\u0405"+
		"\u0003\b\u0004\u0000\u0403\u0405\u0005\u0011\u0000\u0000\u0404\u0402\u0001"+
		"\u0000\u0000\u0000\u0404\u0403\u0001\u0000\u0000\u0000\u0405\u0085\u0001"+
		"\u0000\u0000\u0000\u0406\u040a\u0003\u0088D\u0000\u0407\u040a\u0003\u0096"+
		"K\u0000\u0408\u040a\u0003\u0098L\u0000\u0409\u0406\u0001\u0000\u0000\u0000"+
		"\u0409\u0407\u0001\u0000\u0000\u0000\u0409\u0408\u0001\u0000\u0000\u0000"+
		"\u040a\u0087\u0001\u0000\u0000\u0000\u040b\u040e\u0003\u008eG\u0000\u040c"+
		"\u040e\u0003\u0094J\u0000\u040d\u040b\u0001\u0000\u0000\u0000\u040d\u040c"+
		"\u0001\u0000\u0000\u0000\u040e\u0413\u0001\u0000\u0000\u0000\u040f\u0412"+
		"\u0003\u008cF\u0000\u0410\u0412\u0003\u0092I\u0000\u0411\u040f\u0001\u0000"+
		"\u0000\u0000\u0411\u0410\u0001\u0000\u0000\u0000\u0412\u0415\u0001\u0000"+
		"\u0000\u0000\u0413\u0411\u0001\u0000\u0000\u0000\u0413\u0414\u0001\u0000"+
		"\u0000\u0000\u0414\u0089\u0001\u0000\u0000\u0000\u0415\u0413\u0001\u0000"+
		"\u0000\u0000\u0416\u0418\u0003\u0000\u0000\u0000\u0417\u0419\u0003,\u0016"+
		"\u0000\u0418\u0417\u0001\u0000\u0000\u0000\u0418\u0419\u0001\u0000\u0000"+
		"\u0000\u0419\u0421\u0001\u0000\u0000\u0000\u041a\u041b\u0003\u0088D\u0000"+
		"\u041b\u041c\u0005O\u0000\u0000\u041c\u041e\u0003\u00fc~\u0000\u041d\u041f"+
		"\u0003,\u0016\u0000\u041e\u041d\u0001\u0000\u0000\u0000\u041e\u041f\u0001"+
		"\u0000\u0000\u0000\u041f\u0421\u0001\u0000\u0000\u0000\u0420\u0416\u0001"+
		"\u0000\u0000\u0000\u0420\u041a\u0001\u0000\u0000\u0000\u0421\u008b\u0001"+
		"\u0000\u0000\u0000\u0422\u0423\u0005O\u0000\u0000\u0423\u0425\u0003\u00fc"+
		"~\u0000\u0424\u0426\u0003,\u0016\u0000\u0425\u0424\u0001\u0000\u0000\u0000"+
		"\u0425\u0426\u0001\u0000\u0000\u0000\u0426\u008d\u0001\u0000\u0000\u0000"+
		"\u0427\u0429\u0003\u0000\u0000\u0000\u0428\u042a\u0003,\u0016\u0000\u0429"+
		"\u0428\u0001\u0000\u0000\u0000\u0429\u042a\u0001\u0000\u0000\u0000\u042a"+
		"\u008f\u0001\u0000\u0000\u0000\u042b\u042c\u0003\u008aE\u0000\u042c\u0091"+
		"\u0001\u0000\u0000\u0000\u042d\u042e\u0003\u008cF\u0000\u042e\u0093\u0001"+
		"\u0000\u0000\u0000\u042f\u0430\u0003\u008eG\u0000\u0430\u0095\u0001\u0000"+
		"\u0000\u0000\u0431\u0432\u0003\u0000\u0000\u0000\u0432\u0097\u0001\u0000"+
		"\u0000\u0000\u0433\u0434\u0003\u0084B\u0000\u0434\u0435\u0003\"\u0011"+
		"\u0000\u0435\u043d\u0001\u0000\u0000\u0000\u0436\u0437\u0003\u0088D\u0000"+
		"\u0437\u0438\u0003\"\u0011\u0000\u0438\u043d\u0001\u0000\u0000\u0000\u0439"+
		"\u043a\u0003\u0096K\u0000\u043a\u043b\u0003\"\u0011\u0000\u043b\u043d"+
		"\u0001\u0000\u0000\u0000\u043c\u0433\u0001\u0000\u0000\u0000\u043c\u0436"+
		"\u0001\u0000\u0000\u0000\u043c\u0439\u0001\u0000\u0000\u0000\u043d\u0099"+
		"\u0001\u0000\u0000\u0000\u043e\u043f\u0003\u009cN\u0000\u043f\u0440\u0003"+
		"\u00a0P\u0000\u0440\u0441\u0003\u00b8\\\u0000\u0441\u009b\u0001\u0000"+
		"\u0000\u0000\u0442\u0444\u0003\u009eO\u0000\u0443\u0442\u0001\u0000\u0000"+
		"\u0000\u0444\u0447\u0001\u0000\u0000\u0000\u0445\u0443\u0001\u0000\u0000"+
		"\u0000\u0445\u0446\u0001\u0000\u0000\u0000\u0446\u009d\u0001\u0000\u0000"+
		"\u0000\u0447\u0445\u0001\u0000\u0000\u0000\u0448\u0453\u0003\u00fa}\u0000"+
		"\u0449\u0453\u00051\u0000\u0000\u044a\u0453\u00050\u0000\u0000\u044b\u0453"+
		"\u0005/\u0000\u0000\u044c\u0453\u0005\u000f\u0000\u0000\u044d\u0453\u0005"+
		"4\u0000\u0000\u044e\u0453\u0005 \u0000\u0000\u044f\u0453\u00058\u0000"+
		"\u0000\u0450\u0453\u0005,\u0000\u0000\u0451\u0453\u00055\u0000\u0000\u0452"+
		"\u0448\u0001\u0000\u0000\u0000\u0452\u0449\u0001\u0000\u0000\u0000\u0452"+
		"\u044a\u0001\u0000\u0000\u0000\u0452\u044b\u0001\u0000\u0000\u0000\u0452"+
		"\u044c\u0001\u0000\u0000\u0000\u0452\u044d\u0001\u0000\u0000\u0000\u0452"+
		"\u044e\u0001\u0000\u0000\u0000\u0452\u044f\u0001\u0000\u0000\u0000\u0452"+
		"\u0450\u0001\u0000\u0000\u0000\u0452\u0451\u0001\u0000\u0000\u0000\u0453"+
		"\u009f\u0001\u0000\u0000\u0000\u0454\u0455\u0003\u00a2Q\u0000\u0455\u0457"+
		"\u0003\u00a4R\u0000\u0456\u0458\u0003\u00b2Y\u0000\u0457\u0456\u0001\u0000"+
		"\u0000\u0000\u0457\u0458\u0001\u0000\u0000\u0000\u0458\u0466\u0001\u0000"+
		"\u0000\u0000\u0459\u045d\u0003d2\u0000\u045a\u045c\u0003\u00fa}\u0000"+
		"\u045b\u045a\u0001\u0000\u0000\u0000\u045c\u045f\u0001\u0000\u0000\u0000"+
		"\u045d\u045b\u0001\u0000\u0000\u0000\u045d\u045e\u0001\u0000\u0000\u0000"+
		"\u045e\u0460\u0001\u0000\u0000\u0000\u045f\u045d\u0001\u0000\u0000\u0000"+
		"\u0460\u0461\u0003\u00a2Q\u0000\u0461\u0463\u0003\u00a4R\u0000\u0462\u0464"+
		"\u0003\u00b2Y\u0000\u0463\u0462\u0001\u0000\u0000\u0000\u0463\u0464\u0001"+
		"\u0000\u0000\u0000\u0464\u0466\u0001\u0000\u0000\u0000\u0465\u0454\u0001"+
		"\u0000\u0000\u0000\u0465\u0459\u0001\u0000\u0000\u0000\u0466\u00a1\u0001"+
		"\u0000\u0000\u0000\u0467\u046a\u0003\u0082A\u0000\u0468\u046a\u0005>\u0000"+
		"\u0000\u0469\u0467\u0001\u0000\u0000\u0000\u0469\u0468\u0001\u0000\u0000"+
		"\u0000\u046a\u00a3\u0001\u0000\u0000\u0000\u046b\u046c\u0003\u0000\u0000"+
		"\u0000\u046c\u046e\u0005G\u0000\u0000\u046d\u046f\u0003\u00a6S\u0000\u046e"+
		"\u046d\u0001\u0000\u0000\u0000\u046e\u046f\u0001\u0000\u0000\u0000\u046f"+
		"\u0470\u0001\u0000\u0000\u0000\u0470\u0472\u0005H\u0000\u0000\u0471\u0473"+
		"\u0003\"\u0011\u0000\u0472\u0471\u0001\u0000\u0000\u0000\u0472\u0473\u0001"+
		"\u0000\u0000\u0000\u0473\u00a5\u0001\u0000\u0000\u0000\u0474\u0475\u0003"+
		"\u00a8T\u0000\u0475\u0476\u0005N\u0000\u0000\u0476\u0477\u0003\u00aeW"+
		"\u0000\u0477\u047a\u0001\u0000\u0000\u0000\u0478\u047a\u0003\u00aeW\u0000"+
		"\u0479\u0474\u0001\u0000\u0000\u0000\u0479\u0478\u0001\u0000\u0000\u0000"+
		"\u047a\u00a7\u0001\u0000\u0000\u0000\u047b\u0480\u0003\u00aaU\u0000\u047c"+
		"\u047d\u0005N\u0000\u0000\u047d\u047f\u0003\u00aaU\u0000\u047e\u047c\u0001"+
		"\u0000\u0000\u0000\u047f\u0482\u0001\u0000\u0000\u0000\u0480\u047e\u0001"+
		"\u0000\u0000\u0000\u0480\u0481\u0001\u0000\u0000\u0000\u0481\u048c\u0001"+
		"\u0000\u0000\u0000\u0482\u0480\u0001\u0000\u0000\u0000\u0483\u0488\u0003"+
		"\u00b0X\u0000\u0484\u0485\u0005N\u0000\u0000\u0485\u0487\u0003\u00aaU"+
		"\u0000\u0486\u0484\u0001\u0000\u0000\u0000\u0487\u048a\u0001\u0000\u0000"+
		"\u0000\u0488\u0486\u0001\u0000\u0000\u0000\u0488\u0489\u0001\u0000\u0000"+
		"\u0000\u0489\u048c\u0001\u0000\u0000\u0000\u048a\u0488\u0001\u0000\u0000"+
		"\u0000\u048b\u047b\u0001\u0000\u0000\u0000\u048b\u0483\u0001\u0000\u0000"+
		"\u0000\u048c\u00a9\u0001\u0000\u0000\u0000\u048d\u048f\u0003\u00acV\u0000"+
		"\u048e\u048d\u0001\u0000\u0000\u0000\u048f\u0492\u0001\u0000\u0000\u0000"+
		"\u0490\u048e\u0001\u0000\u0000\u0000\u0490\u0491\u0001\u0000\u0000\u0000"+
		"\u0491\u0493\u0001\u0000\u0000\u0000\u0492\u0490\u0001\u0000\u0000\u0000"+
		"\u0493\u0494\u0003\u0082A\u0000\u0494\u0495\u0003~?\u0000\u0495\u00ab"+
		"\u0001\u0000\u0000\u0000\u0496\u0499\u0003\u00fa}\u0000\u0497\u0499\u0005"+
		" \u0000\u0000\u0498\u0496\u0001\u0000\u0000\u0000\u0498\u0497\u0001\u0000"+
		"\u0000\u0000\u0499\u00ad\u0001\u0000\u0000\u0000\u049a\u049c\u0003\u00ac"+
		"V\u0000\u049b\u049a\u0001\u0000\u0000\u0000\u049c\u049f\u0001\u0000\u0000"+
		"\u0000\u049d\u049b\u0001\u0000\u0000\u0000\u049d\u049e\u0001\u0000\u0000"+
		"\u0000\u049e\u04a0\u0001\u0000\u0000\u0000\u049f\u049d\u0001\u0000\u0000"+
		"\u0000\u04a0\u04a4\u0003\u0082A\u0000\u04a1\u04a3\u0003\u00fa}\u0000\u04a2"+
		"\u04a1\u0001\u0000\u0000\u0000\u04a3\u04a6\u0001\u0000\u0000\u0000\u04a4"+
		"\u04a2\u0001\u0000\u0000\u0000\u04a4\u04a5\u0001\u0000\u0000\u0000\u04a5"+
		"\u04a7\u0001\u0000\u0000\u0000\u04a6\u04a4\u0001\u0000\u0000\u0000\u04a7"+
		"\u04a8\u0005u\u0000\u0000\u04a8\u04a9\u0003~?\u0000\u04a9\u04ac\u0001"+
		"\u0000\u0000\u0000\u04aa\u04ac\u0003\u00aaU\u0000\u04ab\u049d\u0001\u0000"+
		"\u0000\u0000\u04ab\u04aa\u0001\u0000\u0000\u0000\u04ac\u00af\u0001\u0000"+
		"\u0000\u0000\u04ad\u04af\u0003\u00fa}\u0000\u04ae\u04ad\u0001\u0000\u0000"+
		"\u0000\u04af\u04b2\u0001\u0000\u0000\u0000\u04b0\u04ae\u0001\u0000\u0000"+
		"\u0000\u04b0\u04b1\u0001\u0000\u0000\u0000\u04b1\u04b3\u0001\u0000\u0000"+
		"\u0000\u04b2\u04b0\u0001\u0000\u0000\u0000\u04b3\u04b7\u0003\u0082A\u0000"+
		"\u04b4\u04b5\u0003\u0000\u0000\u0000\u04b5\u04b6\u0005O\u0000\u0000\u04b6"+
		"\u04b8\u0001\u0000\u0000\u0000\u04b7\u04b4\u0001\u0000\u0000\u0000\u04b7"+
		"\u04b8\u0001\u0000\u0000\u0000\u04b8\u04b9\u0001\u0000\u0000\u0000\u04b9"+
		"\u04ba\u00059\u0000\u0000\u04ba\u00b1\u0001\u0000\u0000\u0000\u04bb\u04bc"+
		"\u0005;\u0000\u0000\u04bc\u04bd\u0003\u00b4Z\u0000\u04bd\u00b3\u0001\u0000"+
		"\u0000\u0000\u04be\u04c3\u0003\u00b6[\u0000\u04bf\u04c0\u0005N\u0000\u0000"+
		"\u04c0\u04c2\u0003\u00b6[\u0000\u04c1\u04bf\u0001\u0000\u0000\u0000\u04c2"+
		"\u04c5\u0001\u0000\u0000\u0000\u04c3\u04c1\u0001\u0000\u0000\u0000\u04c3"+
		"\u04c4\u0001\u0000\u0000\u0000\u04c4\u00b5\u0001\u0000\u0000\u0000\u04c5"+
		"\u04c3\u0001\u0000\u0000\u0000\u04c6\u04c9\u0003\u0012\t\u0000\u04c7\u04c9"+
		"\u0003\u001e\u000f\u0000\u04c8\u04c6\u0001\u0000\u0000\u0000\u04c8\u04c7"+
		"\u0001\u0000\u0000\u0000\u04c9\u00b7\u0001\u0000\u0000\u0000\u04ca\u04cd"+
		"\u0003\u0114\u008a\u0000\u04cb\u04cd\u0005M\u0000\u0000\u04cc\u04ca\u0001"+
		"\u0000\u0000\u0000\u04cc\u04cb\u0001\u0000\u0000\u0000\u04cd\u00b9\u0001"+
		"\u0000\u0000\u0000\u04ce\u04cf\u0003\u0114\u008a\u0000\u04cf\u00bb\u0001"+
		"\u0000\u0000\u0000\u04d0\u04d1\u00054\u0000\u0000\u04d1\u04d2\u0003\u0114"+
		"\u008a\u0000\u04d2\u00bd\u0001\u0000\u0000\u0000\u04d3\u04d4\u0003\u00c0"+
		"`\u0000\u04d4\u04d6\u0003\u00c4b\u0000\u04d5\u04d7\u0003\u00b2Y\u0000"+
		"\u04d6\u04d5\u0001\u0000\u0000\u0000\u04d6\u04d7\u0001\u0000\u0000\u0000"+
		"\u04d7\u04d8\u0001\u0000\u0000\u0000\u04d8\u04d9\u0003\u00c8d\u0000\u04d9"+
		"\u00bf\u0001\u0000\u0000\u0000\u04da\u04dc\u0003\u00c2a\u0000\u04db\u04da"+
		"\u0001\u0000\u0000\u0000\u04dc\u04df\u0001\u0000\u0000\u0000\u04dd\u04db"+
		"\u0001\u0000\u0000\u0000\u04dd\u04de\u0001\u0000\u0000\u0000\u04de\u00c1"+
		"\u0001\u0000\u0000\u0000\u04df\u04dd\u0001\u0000\u0000\u0000\u04e0\u04e5"+
		"\u0003\u00fa}\u0000\u04e1\u04e5\u00051\u0000\u0000\u04e2\u04e5\u00050"+
		"\u0000\u0000\u04e3\u04e5\u0005/\u0000\u0000\u04e4\u04e0\u0001\u0000\u0000"+
		"\u0000\u04e4\u04e1\u0001\u0000\u0000\u0000\u04e4\u04e2\u0001\u0000\u0000"+
		"\u0000\u04e4\u04e3\u0001\u0000\u0000\u0000\u04e5\u00c3\u0001\u0000\u0000"+
		"\u0000\u04e6\u04e8\u0003d2\u0000\u04e7\u04e6\u0001\u0000\u0000\u0000\u04e7"+
		"\u04e8\u0001\u0000\u0000\u0000\u04e8\u04e9\u0001\u0000\u0000\u0000\u04e9"+
		"\u04ea\u0003\u00c6c\u0000\u04ea\u04ec\u0005G\u0000\u0000\u04eb\u04ed\u0003"+
		"\u00a6S\u0000\u04ec\u04eb\u0001\u0000\u0000\u0000\u04ec\u04ed\u0001\u0000"+
		"\u0000\u0000\u04ed\u04ee\u0001\u0000\u0000\u0000\u04ee\u04ef\u0005H\u0000"+
		"\u0000\u04ef\u00c5\u0001\u0000\u0000\u0000\u04f0\u04f1\u0003\u0000\u0000"+
		"\u0000\u04f1\u00c7\u0001\u0000\u0000\u0000\u04f2\u04f4\u0005I\u0000\u0000"+
		"\u04f3\u04f5\u0003\u00cae\u0000\u04f4\u04f3\u0001\u0000\u0000\u0000\u04f4"+
		"\u04f5\u0001\u0000\u0000\u0000\u04f5\u04f7\u0001\u0000\u0000\u0000\u04f6"+
		"\u04f8\u0003\u0116\u008b\u0000\u04f7\u04f6\u0001\u0000\u0000\u0000\u04f7"+
		"\u04f8\u0001\u0000\u0000\u0000\u04f8\u04f9\u0001\u0000\u0000\u0000\u04f9"+
		"\u04fa\u0005J\u0000\u0000\u04fa\u00c9\u0001\u0000\u0000\u0000\u04fb\u04fd"+
		"\u0003,\u0016\u0000\u04fc\u04fb\u0001\u0000\u0000\u0000\u04fc\u04fd\u0001"+
		"\u0000\u0000\u0000\u04fd\u04fe\u0001\u0000\u0000\u0000\u04fe\u04ff\u0005"+
		"9\u0000\u0000\u04ff\u0501\u0005G\u0000\u0000\u0500\u0502\u0003\u01aa\u00d5"+
		"\u0000\u0501\u0500\u0001\u0000\u0000\u0000\u0501\u0502\u0001\u0000\u0000"+
		"\u0000\u0502\u0503\u0001\u0000\u0000\u0000\u0503\u0504\u0005H\u0000\u0000"+
		"\u0504\u052a\u0005M\u0000\u0000\u0505\u0507\u0003,\u0016\u0000\u0506\u0505"+
		"\u0001\u0000\u0000\u0000\u0506\u0507\u0001\u0000\u0000\u0000\u0507\u0508"+
		"\u0001\u0000\u0000\u0000\u0508\u0509\u00056\u0000\u0000\u0509\u050b\u0005"+
		"G\u0000\u0000\u050a\u050c\u0003\u01aa\u00d5\u0000\u050b\u050a\u0001\u0000"+
		"\u0000\u0000\u050b\u050c\u0001\u0000\u0000\u0000\u050c\u050d\u0001\u0000"+
		"\u0000\u0000\u050d\u050e\u0005H\u0000\u0000\u050e\u052a\u0005M\u0000\u0000"+
		"\u050f\u0510\u0003<\u001e\u0000\u0510\u0512\u0005O\u0000\u0000\u0511\u0513"+
		"\u0003,\u0016\u0000\u0512\u0511\u0001\u0000\u0000\u0000\u0512\u0513\u0001"+
		"\u0000\u0000\u0000\u0513\u0514\u0001\u0000\u0000\u0000\u0514\u0515\u0005"+
		"6\u0000\u0000\u0515\u0517\u0005G\u0000\u0000\u0516\u0518\u0003\u01aa\u00d5"+
		"\u0000\u0517\u0516\u0001\u0000\u0000\u0000\u0517\u0518\u0001\u0000\u0000"+
		"\u0000\u0518\u0519\u0001\u0000\u0000\u0000\u0519\u051a\u0005H\u0000\u0000"+
		"\u051a\u051b\u0005M\u0000\u0000\u051b\u052a\u0001\u0000\u0000\u0000\u051c"+
		"\u051d\u0003\u017c\u00be\u0000\u051d\u051f\u0005O\u0000\u0000\u051e\u0520"+
		"\u0003,\u0016\u0000\u051f\u051e\u0001\u0000\u0000\u0000\u051f\u0520\u0001"+
		"\u0000\u0000\u0000\u0520\u0521\u0001\u0000\u0000\u0000\u0521\u0522\u0005"+
		"6\u0000\u0000\u0522\u0524\u0005G\u0000\u0000\u0523\u0525\u0003\u01aa\u00d5"+
		"\u0000\u0524\u0523\u0001\u0000\u0000\u0000\u0524\u0525\u0001\u0000\u0000"+
		"\u0000\u0525\u0526\u0001\u0000\u0000\u0000\u0526\u0527\u0005H\u0000\u0000"+
		"\u0527\u0528\u0005M\u0000\u0000\u0528\u052a\u0001\u0000\u0000\u0000\u0529"+
		"\u04fc\u0001\u0000\u0000\u0000\u0529\u0506\u0001\u0000\u0000\u0000\u0529"+
		"\u050f\u0001\u0000\u0000\u0000\u0529\u051c\u0001\u0000\u0000\u0000\u052a"+
		"\u00cb\u0001\u0000\u0000\u0000\u052b\u052c\u0003`0\u0000\u052c\u052d\u0005"+
		"\u001e\u0000\u0000\u052d\u052f\u0003\u0000\u0000\u0000\u052e\u0530\u0003"+
		"j5\u0000\u052f\u052e\u0001\u0000\u0000\u0000\u052f\u0530\u0001\u0000\u0000"+
		"\u0000\u0530\u0531\u0001\u0000\u0000\u0000\u0531\u0532\u0003\u00ceg\u0000"+
		"\u0532\u00cd\u0001\u0000\u0000\u0000\u0533\u0535\u0005I\u0000\u0000\u0534"+
		"\u0536\u0003\u00d0h\u0000\u0535\u0534\u0001\u0000\u0000\u0000\u0535\u0536"+
		"\u0001\u0000\u0000\u0000\u0536\u0538\u0001\u0000\u0000\u0000\u0537\u0539"+
		"\u0005N\u0000\u0000\u0538\u0537\u0001\u0000\u0000\u0000\u0538\u0539\u0001"+
		"\u0000\u0000\u0000\u0539\u053b\u0001\u0000\u0000\u0000\u053a\u053c\u0003"+
		"\u00d6k\u0000\u053b\u053a\u0001\u0000\u0000\u0000\u053b\u053c\u0001\u0000"+
		"\u0000\u0000\u053c\u053d\u0001\u0000\u0000\u0000\u053d\u053e\u0005J\u0000"+
		"\u0000\u053e\u00cf\u0001\u0000\u0000\u0000\u053f\u0544\u0003\u00d2i\u0000"+
		"\u0540\u0541\u0005N\u0000\u0000\u0541\u0543\u0003\u00d2i\u0000\u0542\u0540"+
		"\u0001\u0000\u0000\u0000\u0543\u0546\u0001\u0000\u0000\u0000\u0544\u0542"+
		"\u0001\u0000\u0000\u0000\u0544\u0545\u0001\u0000\u0000\u0000\u0545\u00d1"+
		"\u0001\u0000\u0000\u0000\u0546\u0544\u0001\u0000\u0000\u0000\u0547\u0549"+
		"\u0003\u00d4j\u0000\u0548\u0547\u0001\u0000\u0000\u0000\u0549\u054c\u0001"+
		"\u0000\u0000\u0000\u054a\u0548\u0001\u0000\u0000\u0000\u054a\u054b\u0001"+
		"\u0000\u0000\u0000\u054b\u054d\u0001\u0000\u0000\u0000\u054c\u054a\u0001"+
		"\u0000\u0000\u0000\u054d\u0553\u0003\u0000\u0000\u0000\u054e\u0550\u0005"+
		"G\u0000\u0000\u054f\u0551\u0003\u01aa\u00d5\u0000\u0550\u054f\u0001\u0000"+
		"\u0000\u0000\u0550\u0551\u0001\u0000\u0000\u0000\u0551\u0552\u0001\u0000"+
		"\u0000\u0000\u0552\u0554\u0005H\u0000\u0000\u0553\u054e\u0001\u0000\u0000"+
		"\u0000\u0553\u0554\u0001\u0000\u0000\u0000\u0554\u0556\u0001\u0000\u0000"+
		"\u0000\u0555\u0557\u0003n7\u0000\u0556\u0555\u0001\u0000\u0000\u0000\u0556"+
		"\u0557\u0001\u0000\u0000\u0000\u0557\u00d3\u0001\u0000\u0000\u0000\u0558"+
		"\u0559\u0003\u00fa}\u0000\u0559\u00d5\u0001\u0000\u0000\u0000\u055a\u055e"+
		"\u0005M\u0000\u0000\u055b\u055d\u0003p8\u0000\u055c\u055b\u0001\u0000"+
		"\u0000\u0000\u055d\u0560\u0001\u0000\u0000\u0000\u055e\u055c\u0001\u0000"+
		"\u0000\u0000\u055e\u055f\u0001\u0000\u0000\u0000\u055f\u00d7\u0001\u0000"+
		"\u0000\u0000\u0560\u055e\u0001\u0000\u0000\u0000\u0561\u0564\u0003\u00da"+
		"m\u0000\u0562\u0564\u0003\u00eew\u0000\u0563\u0561\u0001\u0000\u0000\u0000"+
		"\u0563\u0562\u0001\u0000\u0000\u0000\u0564\u00d9\u0001\u0000\u0000\u0000"+
		"\u0565\u0566\u0003\u00dcn\u0000\u0566\u0567\u0005*\u0000\u0000\u0567\u0569"+
		"\u0003\u0000\u0000\u0000\u0568\u056a\u0003d2\u0000\u0569\u0568\u0001\u0000"+
		"\u0000\u0000\u0569\u056a\u0001\u0000\u0000\u0000\u056a\u056c\u0001\u0000"+
		"\u0000\u0000\u056b\u056d\u0003\u00e0p\u0000\u056c\u056b\u0001\u0000\u0000"+
		"\u0000\u056c\u056d\u0001\u0000\u0000\u0000\u056d\u056e\u0001\u0000\u0000"+
		"\u0000\u056e\u056f\u0003\u00e2q\u0000\u056f\u00db\u0001\u0000\u0000\u0000"+
		"\u0570\u0572\u0003\u00deo\u0000\u0571\u0570\u0001\u0000\u0000\u0000\u0572"+
		"\u0575\u0001\u0000\u0000\u0000\u0573\u0571\u0001\u0000\u0000\u0000\u0573"+
		"\u0574\u0001\u0000\u0000\u0000\u0574\u00dd\u0001\u0000\u0000\u0000\u0575"+
		"\u0573\u0001\u0000\u0000\u0000\u0576\u057e\u0003\u00fa}\u0000\u0577\u057e"+
		"\u00051\u0000\u0000\u0578\u057e\u00050\u0000\u0000\u0579\u057e\u0005/"+
		"\u0000\u0000\u057a\u057e\u0005\u000f\u0000\u0000\u057b\u057e\u00054\u0000"+
		"\u0000\u057c\u057e\u00055\u0000\u0000\u057d\u0576\u0001\u0000\u0000\u0000"+
		"\u057d\u0577\u0001\u0000\u0000\u0000\u057d\u0578\u0001\u0000\u0000\u0000"+
		"\u057d\u0579\u0001\u0000\u0000\u0000\u057d\u057a\u0001\u0000\u0000\u0000"+
		"\u057d\u057b\u0001\u0000\u0000\u0000\u057d\u057c\u0001\u0000\u0000\u0000"+
		"\u057e\u00df\u0001\u0000\u0000\u0000\u057f\u0580\u0005\u001f\u0000\u0000"+
		"\u0580\u0581\u0003l6\u0000\u0581\u00e1\u0001\u0000\u0000\u0000\u0582\u0586"+
		"\u0005I\u0000\u0000\u0583\u0585\u0003\u00e4r\u0000\u0584\u0583\u0001\u0000"+
		"\u0000\u0000\u0585\u0588\u0001\u0000\u0000\u0000\u0586\u0584\u0001\u0000"+
		"\u0000\u0000\u0586\u0587\u0001\u0000\u0000\u0000\u0587\u0589\u0001\u0000"+
		"\u0000\u0000\u0588\u0586\u0001\u0000\u0000\u0000\u0589\u058a\u0005J\u0000"+
		"\u0000\u058a\u00e3\u0001\u0000\u0000\u0000\u058b\u0591\u0003\u00e6s\u0000"+
		"\u058c\u0591\u0003\u00eau\u0000\u058d\u0591\u0003\\.\u0000\u058e\u0591"+
		"\u0003\u00d8l\u0000\u058f\u0591\u0005M\u0000\u0000\u0590\u058b\u0001\u0000"+
		"\u0000\u0000\u0590\u058c\u0001\u0000\u0000\u0000\u0590\u058d\u0001\u0000"+
		"\u0000\u0000\u0590\u058e\u0001\u0000\u0000\u0000\u0590\u058f\u0001\u0000"+
		"\u0000\u0000\u0591\u00e5\u0001\u0000\u0000\u0000\u0592\u0594\u0003\u00e8"+
		"t\u0000\u0593\u0592\u0001\u0000\u0000\u0000\u0594\u0597\u0001\u0000\u0000"+
		"\u0000\u0595\u0593\u0001\u0000\u0000\u0000\u0595\u0596\u0001\u0000\u0000"+
		"\u0000\u0596\u0598\u0001\u0000\u0000\u0000\u0597\u0595\u0001\u0000\u0000"+
		"\u0000\u0598\u0599\u0003\u0082A\u0000\u0599\u059a\u0003z=\u0000\u059a"+
		"\u059b\u0005M\u0000\u0000\u059b\u00e7\u0001\u0000\u0000\u0000\u059c\u05a1"+
		"\u0003\u00fa}\u0000\u059d\u05a1\u00051\u0000\u0000\u059e\u05a1\u00054"+
		"\u0000\u0000\u059f\u05a1\u0005 \u0000\u0000\u05a0\u059c\u0001\u0000\u0000"+
		"\u0000\u05a0\u059d\u0001\u0000\u0000\u0000\u05a0\u059e\u0001\u0000\u0000"+
		"\u0000\u05a0\u059f\u0001\u0000\u0000\u0000\u05a1\u00e9\u0001\u0000\u0000"+
		"\u0000\u05a2\u05a4\u0003\u00ecv\u0000\u05a3\u05a2\u0001\u0000\u0000\u0000"+
		"\u05a4\u05a7\u0001\u0000\u0000\u0000\u05a5\u05a3\u0001\u0000\u0000\u0000"+
		"\u05a5\u05a6\u0001\u0000\u0000\u0000\u05a6\u05a8\u0001\u0000\u0000\u0000"+
		"\u05a7\u05a5\u0001\u0000\u0000\u0000\u05a8\u05a9\u0003\u00a0P\u0000\u05a9"+
		"\u05aa\u0003\u00b8\\\u0000\u05aa\u00eb\u0001\u0000\u0000\u0000\u05ab\u05b3"+
		"\u0003\u00fa}\u0000\u05ac\u05b3\u00051\u0000\u0000\u05ad\u05b3\u0005/"+
		"\u0000\u0000\u05ae\u05b3\u0005\u000f\u0000\u0000\u05af\u05b3\u0005\u001a"+
		"\u0000\u0000\u05b0\u05b3\u00054\u0000\u0000\u05b1\u05b3\u00055\u0000\u0000"+
		"\u05b2\u05ab\u0001\u0000\u0000\u0000\u05b2\u05ac\u0001\u0000\u0000\u0000"+
		"\u05b2\u05ad\u0001\u0000\u0000\u0000\u05b2\u05ae\u0001\u0000\u0000\u0000"+
		"\u05b2\u05af\u0001\u0000\u0000\u0000\u05b2\u05b0\u0001\u0000\u0000\u0000"+
		"\u05b2\u05b1\u0001\u0000\u0000\u0000\u05b3\u00ed\u0001\u0000\u0000\u0000"+
		"\u05b4\u05b6\u0003\u00deo\u0000\u05b5\u05b4\u0001\u0000\u0000\u0000\u05b6"+
		"\u05b9\u0001\u0000\u0000\u0000\u05b7\u05b5\u0001\u0000\u0000\u0000\u05b7"+
		"\u05b8\u0001\u0000\u0000\u0000\u05b8\u05ba\u0001\u0000\u0000\u0000\u05b9"+
		"\u05b7\u0001\u0000\u0000\u0000\u05ba\u05bb\u0005t\u0000\u0000\u05bb\u05bc"+
		"\u0005*\u0000\u0000\u05bc\u05bd\u0003\u0000\u0000\u0000\u05bd\u05be\u0003"+
		"\u00f0x\u0000\u05be\u00ef\u0001\u0000\u0000\u0000\u05bf\u05c3\u0005I\u0000"+
		"\u0000\u05c0\u05c2\u0003\u00f2y\u0000\u05c1\u05c0\u0001\u0000\u0000\u0000"+
		"\u05c2\u05c5\u0001\u0000\u0000\u0000\u05c3\u05c1\u0001\u0000\u0000\u0000"+
		"\u05c3\u05c4\u0001\u0000\u0000\u0000\u05c4\u05c6\u0001\u0000\u0000\u0000"+
		"\u05c5\u05c3\u0001\u0000\u0000\u0000\u05c6\u05c7\u0005J\u0000\u0000\u05c7"+
		"\u00f1\u0001\u0000\u0000\u0000\u05c8\u05ce\u0003\u00f4z\u0000\u05c9\u05ce"+
		"\u0003\u00e6s\u0000\u05ca\u05ce\u0003\\.\u0000\u05cb\u05ce\u0003\u00d8"+
		"l\u0000\u05cc\u05ce\u0005M\u0000\u0000\u05cd\u05c8\u0001\u0000\u0000\u0000"+
		"\u05cd\u05c9\u0001\u0000\u0000\u0000\u05cd\u05ca\u0001\u0000\u0000\u0000"+
		"\u05cd\u05cb\u0001\u0000\u0000\u0000\u05cd\u05cc\u0001\u0000\u0000\u0000"+
		"\u05ce\u00f3\u0001\u0000\u0000\u0000\u05cf\u05d1\u0003\u00f6{\u0000\u05d0"+
		"\u05cf\u0001\u0000\u0000\u0000\u05d1\u05d4\u0001\u0000\u0000\u0000\u05d2"+
		"\u05d0\u0001\u0000\u0000\u0000\u05d2\u05d3\u0001\u0000\u0000\u0000\u05d3"+
		"\u05d5\u0001\u0000\u0000\u0000\u05d4\u05d2\u0001\u0000\u0000\u0000\u05d5"+
		"\u05d6\u0003\u0082A\u0000\u05d6\u05d7\u0003\u0000\u0000\u0000\u05d7\u05d8"+
		"\u0005G\u0000\u0000\u05d8\u05da\u0005H\u0000\u0000\u05d9\u05db\u0003\""+
		"\u0011\u0000\u05da\u05d9\u0001\u0000\u0000\u0000\u05da\u05db\u0001\u0000"+
		"\u0000\u0000\u05db\u05dd\u0001\u0000\u0000\u0000\u05dc\u05de\u0003\u00f8"+
		"|\u0000\u05dd\u05dc\u0001\u0000\u0000\u0000\u05dd\u05de\u0001\u0000\u0000"+
		"\u0000\u05de\u05df\u0001\u0000\u0000\u0000\u05df\u05e0\u0005M\u0000\u0000"+
		"\u05e0\u00f5\u0001\u0000\u0000\u0000\u05e1\u05e5\u0003\u00fa}\u0000\u05e2"+
		"\u05e5\u00051\u0000\u0000\u05e3\u05e5\u0005\u000f\u0000\u0000\u05e4\u05e1"+
		"\u0001\u0000\u0000\u0000\u05e4\u05e2\u0001\u0000\u0000\u0000\u05e4\u05e3"+
		"\u0001\u0000\u0000\u0000\u05e5\u00f7\u0001\u0000\u0000\u0000\u05e6\u05e7"+
		"\u0005\u001a\u0000\u0000\u05e7\u05e8\u0003\u0106\u0083\u0000\u05e8\u00f9"+
		"\u0001\u0000\u0000\u0000\u05e9\u05ed\u0003\u0100\u0080\u0000\u05ea\u05ed"+
		"\u0003\u010c\u0086\u0000\u05eb\u05ed\u0003\u010e\u0087\u0000\u05ec\u05e9"+
		"\u0001\u0000\u0000\u0000\u05ec\u05ea\u0001\u0000\u0000\u0000\u05ec\u05eb"+
		"\u0001\u0000\u0000\u0000\u05ed\u00fb\u0001\u0000\u0000\u0000\u05ee\u05f0"+
		"\u0003\u00fa}\u0000\u05ef\u05ee\u0001\u0000\u0000\u0000\u05f0\u05f3\u0001"+
		"\u0000\u0000\u0000\u05f1\u05ef\u0001\u0000\u0000\u0000\u05f1\u05f2\u0001"+
		"\u0000\u0000\u0000\u05f2\u05f4\u0001\u0000\u0000\u0000\u05f3\u05f1\u0001"+
		"\u0000\u0000\u0000\u05f4\u05f5\u0003\u0000\u0000\u0000\u05f5\u00fd\u0001"+
		"\u0000\u0000\u0000\u05f6\u05f8\u0003\u00fa}\u0000\u05f7\u05f6\u0001\u0000"+
		"\u0000\u0000\u05f8\u05fb\u0001\u0000\u0000\u0000\u05f9\u05f7\u0001\u0000"+
		"\u0000\u0000\u05f9\u05fa\u0001\u0000\u0000\u0000\u05fa\u05fc\u0001\u0000"+
		"\u0000\u0000\u05fb\u05f9\u0001\u0000\u0000\u0000\u05fc\u05fd\u0003\u01d2"+
		"\u00e9\u0000\u05fd\u00ff\u0001\u0000\u0000\u0000\u05fe\u05ff\u0005t\u0000"+
		"\u0000\u05ff\u0600\u00038\u001c\u0000\u0600\u0602\u0005G\u0000\u0000\u0601"+
		"\u0603\u0003\u0102\u0081\u0000\u0602\u0601\u0001\u0000\u0000\u0000\u0602"+
		"\u0603\u0001\u0000\u0000\u0000\u0603\u0604\u0001\u0000\u0000\u0000\u0604"+
		"\u0605\u0005H\u0000\u0000\u0605\u0101\u0001\u0000\u0000\u0000\u0606\u060b"+
		"\u0003\u0104\u0082\u0000\u0607\u0608\u0005N\u0000\u0000\u0608\u060a\u0003"+
		"\u0104\u0082\u0000\u0609\u0607\u0001\u0000\u0000\u0000\u060a\u060d\u0001"+
		"\u0000\u0000\u0000\u060b\u0609\u0001\u0000\u0000\u0000\u060b\u060c\u0001"+
		"\u0000\u0000\u0000\u060c\u0103\u0001\u0000\u0000\u0000\u060d\u060b\u0001"+
		"\u0000\u0000\u0000\u060e\u060f\u0003\u0000\u0000\u0000\u060f\u0610\u0005"+
		"P\u0000\u0000\u0610\u0611\u0003\u0106\u0083\u0000\u0611\u0105\u0001\u0000"+
		"\u0000\u0000\u0612\u0616\u0003\u01d4\u00ea\u0000\u0613\u0616\u0003\u0108"+
		"\u0084\u0000\u0614\u0616\u0003\u00fa}\u0000\u0615\u0612\u0001\u0000\u0000"+
		"\u0000\u0615\u0613\u0001\u0000\u0000\u0000\u0615\u0614\u0001\u0000\u0000"+
		"\u0000\u0616\u0107\u0001\u0000\u0000\u0000\u0617\u0619\u0005I\u0000\u0000"+
		"\u0618\u061a\u0003\u010a\u0085\u0000\u0619\u0618\u0001\u0000\u0000\u0000"+
		"\u0619\u061a\u0001\u0000\u0000\u0000\u061a\u061c\u0001\u0000\u0000\u0000"+
		"\u061b\u061d\u0005N\u0000\u0000\u061c\u061b\u0001\u0000\u0000\u0000\u061c"+
		"\u061d\u0001\u0000\u0000\u0000\u061d\u061e\u0001\u0000\u0000\u0000\u061e"+
		"\u061f\u0005J\u0000\u0000\u061f\u0109\u0001\u0000\u0000\u0000\u0620\u0625"+
		"\u0003\u0106\u0083\u0000\u0621\u0622\u0005N\u0000\u0000\u0622\u0624\u0003"+
		"\u0106\u0083\u0000\u0623\u0621\u0001\u0000\u0000\u0000\u0624\u0627\u0001"+
		"\u0000\u0000\u0000\u0625\u0623\u0001\u0000\u0000\u0000\u0625\u0626\u0001"+
		"\u0000\u0000\u0000\u0626\u010b\u0001\u0000\u0000\u0000\u0627\u0625\u0001"+
		"\u0000\u0000\u0000\u0628\u0629\u0005t\u0000\u0000\u0629\u062a\u00038\u001c"+
		"\u0000\u062a\u010d\u0001\u0000\u0000\u0000\u062b\u062c\u0005t\u0000\u0000"+
		"\u062c\u062d\u00038\u001c\u0000\u062d\u062e\u0005G\u0000\u0000\u062e\u062f"+
		"\u0003\u0106\u0083\u0000\u062f\u0630\u0005H\u0000\u0000\u0630\u010f\u0001"+
		"\u0000\u0000\u0000\u0631\u0633\u0005I\u0000\u0000\u0632\u0634\u0003\u0112"+
		"\u0089\u0000\u0633\u0632\u0001\u0000\u0000\u0000\u0633\u0634\u0001\u0000"+
		"\u0000\u0000\u0634\u0636\u0001\u0000\u0000\u0000\u0635\u0637\u0005N\u0000"+
		"\u0000\u0636\u0635\u0001\u0000\u0000\u0000\u0636\u0637\u0001\u0000\u0000"+
		"\u0000\u0637\u0638\u0001\u0000\u0000\u0000\u0638\u0639\u0005J\u0000\u0000"+
		"\u0639\u0111\u0001\u0000\u0000\u0000\u063a\u063f\u0003\u0080@\u0000\u063b"+
		"\u063c\u0005N\u0000\u0000\u063c\u063e\u0003\u0080@\u0000\u063d\u063b\u0001"+
		"\u0000\u0000\u0000\u063e\u0641\u0001\u0000\u0000\u0000\u063f\u063d\u0001"+
		"\u0000\u0000\u0000\u063f\u0640\u0001\u0000\u0000\u0000\u0640\u0113\u0001"+
		"\u0000\u0000\u0000\u0641\u063f\u0001\u0000\u0000\u0000\u0642\u0644\u0005"+
		"I\u0000\u0000\u0643\u0645\u0003\u0116\u008b\u0000\u0644\u0643\u0001\u0000"+
		"\u0000\u0000\u0644\u0645\u0001\u0000\u0000\u0000\u0645\u0646\u0001\u0000"+
		"\u0000\u0000\u0646\u0647\u0005J\u0000\u0000\u0647\u0115\u0001\u0000\u0000"+
		"\u0000\u0648\u064c\u0003\u0118\u008c\u0000\u0649\u064b\u0003\u0118\u008c"+
		"\u0000\u064a\u0649\u0001\u0000\u0000\u0000\u064b\u064e\u0001\u0000\u0000"+
		"\u0000\u064c\u064a\u0001\u0000\u0000\u0000\u064c\u064d\u0001\u0000\u0000"+
		"\u0000\u064d\u0117\u0001\u0000\u0000\u0000\u064e\u064c\u0001\u0000\u0000"+
		"\u0000\u064f\u0653\u0003\u011a\u008d\u0000\u0650\u0653\u0003\\.\u0000"+
		"\u0651\u0653\u0003\u0120\u0090\u0000\u0652\u064f\u0001\u0000\u0000\u0000"+
		"\u0652\u0650\u0001\u0000\u0000\u0000\u0652\u0651\u0001\u0000\u0000\u0000"+
		"\u0653\u0119\u0001\u0000\u0000\u0000\u0654\u0655\u0003\u011c\u008e\u0000"+
		"\u0655\u0656\u0005M\u0000\u0000\u0656\u011b\u0001\u0000\u0000\u0000\u0657"+
		"\u0659\u0003\u00acV\u0000\u0658\u0657\u0001\u0000\u0000\u0000\u0659\u065c"+
		"\u0001\u0000\u0000\u0000\u065a\u0658\u0001\u0000\u0000\u0000\u065a\u065b"+
		"\u0001\u0000\u0000\u0000\u065b\u065d\u0001\u0000\u0000\u0000\u065c\u065a"+
		"\u0001\u0000\u0000\u0000\u065d\u065e\u0003\u011e\u008f\u0000\u065e\u065f"+
		"\u0003z=\u0000\u065f\u011d\u0001\u0000\u0000\u0000\u0660\u0663\u0003\u0082"+
		"A\u0000\u0661\u0663\u0005\u000b\u0000\u0000\u0662\u0660\u0001\u0000\u0000"+
		"\u0000\u0662\u0661\u0001\u0000\u0000\u0000\u0663\u011f\u0001\u0000\u0000"+
		"\u0000\u0664\u066b\u0003\u0124\u0092\u0000\u0665\u066b\u0003\u0128\u0094"+
		"\u0000\u0666\u066b\u0003\u0130\u0098\u0000\u0667\u066b\u0003\u0132\u0099"+
		"\u0000\u0668\u066b\u0003\u0144\u00a2\u0000\u0669\u066b\u0003\u014a\u00a5"+
		"\u0000\u066a\u0664\u0001\u0000\u0000\u0000\u066a\u0665\u0001\u0000\u0000"+
		"\u0000\u066a\u0666\u0001\u0000\u0000\u0000\u066a\u0667\u0001\u0000\u0000"+
		"\u0000\u066a\u0668\u0001\u0000\u0000\u0000\u066a\u0669\u0001\u0000\u0000"+
		"\u0000\u066b\u0121\u0001\u0000\u0000\u0000\u066c\u0672\u0003\u0124\u0092"+
		"\u0000\u066d\u0672\u0003\u012a\u0095\u0000\u066e\u0672\u0003\u0134\u009a"+
		"\u0000\u066f\u0672\u0003\u0146\u00a3\u0000\u0670\u0672\u0003\u014c\u00a6"+
		"\u0000\u0671\u066c\u0001\u0000\u0000\u0000\u0671\u066d\u0001\u0000\u0000"+
		"\u0000\u0671\u066e\u0001\u0000\u0000\u0000\u0671\u066f\u0001\u0000\u0000"+
		"\u0000\u0671\u0670\u0001\u0000\u0000\u0000\u0672\u0123\u0001\u0000\u0000"+
		"\u0000\u0673\u0680\u0003\u0114\u008a\u0000\u0674\u0680\u0003\u0126\u0093"+
		"\u0000\u0675\u0680\u0003\u012c\u0096\u0000\u0676\u0680\u0003\u0136\u009b"+
		"\u0000\u0677\u0680\u0003\u0138\u009c\u0000\u0678\u0680\u0003\u0148\u00a4"+
		"\u0000\u0679\u0680\u0003\u015c\u00ae\u0000\u067a\u0680\u0003\u015e\u00af"+
		"\u0000\u067b\u0680\u0003\u0160\u00b0\u0000\u067c\u0680\u0003\u0164\u00b2"+
		"\u0000\u067d\u0680\u0003\u0162\u00b1\u0000\u067e\u0680\u0003\u0166\u00b3"+
		"\u0000\u067f\u0673\u0001\u0000\u0000\u0000\u067f\u0674\u0001\u0000\u0000"+
		"\u0000\u067f\u0675\u0001\u0000\u0000\u0000\u067f\u0676\u0001\u0000\u0000"+
		"\u0000\u067f\u0677\u0001\u0000\u0000\u0000\u067f\u0678\u0001\u0000\u0000"+
		"\u0000\u067f\u0679\u0001\u0000\u0000\u0000\u067f\u067a\u0001\u0000\u0000"+
		"\u0000\u067f\u067b\u0001\u0000\u0000\u0000\u067f\u067c\u0001\u0000\u0000"+
		"\u0000\u067f\u067d\u0001\u0000\u0000\u0000\u067f\u067e\u0001\u0000\u0000"+
		"\u0000\u0680\u0125\u0001\u0000\u0000\u0000\u0681\u0682\u0005M\u0000\u0000"+
		"\u0682\u0127\u0001\u0000\u0000\u0000\u0683\u0684\u0003\u0000\u0000\u0000"+
		"\u0684\u0685\u0005V\u0000\u0000\u0685\u0686\u0003\u0120\u0090\u0000\u0686"+
		"\u0129\u0001\u0000\u0000\u0000\u0687\u0688\u0003\u0000\u0000\u0000\u0688"+
		"\u0689\u0005V\u0000\u0000\u0689\u068a\u0003\u0122\u0091\u0000\u068a\u012b"+
		"\u0001\u0000\u0000\u0000\u068b\u068c\u0003\u012e\u0097\u0000\u068c\u068d"+
		"\u0005M\u0000\u0000\u068d\u012d\u0001\u0000\u0000\u0000\u068e\u0696\u0003"+
		"\u01c6\u00e3\u0000\u068f\u0696\u0003\u01ee\u00f7\u0000\u0690\u0696\u0003"+
		"\u01f0\u00f8\u0000\u0691\u0696\u0003\u01f6\u00fb\u0000\u0692\u0696\u0003"+
		"\u01fa\u00fd\u0000\u0693\u0696\u0003\u01a4\u00d2\u0000\u0694\u0696\u0003"+
		"\u0190\u00c8\u0000\u0695\u068e\u0001\u0000\u0000\u0000\u0695\u068f\u0001"+
		"\u0000\u0000\u0000\u0695\u0690\u0001\u0000\u0000\u0000\u0695\u0691\u0001"+
		"\u0000\u0000\u0000\u0695\u0692\u0001\u0000\u0000\u0000\u0695\u0693\u0001"+
		"\u0000\u0000\u0000\u0695\u0694\u0001\u0000\u0000\u0000\u0696\u012f\u0001"+
		"\u0000\u0000\u0000\u0697\u0698\u0005$\u0000\u0000\u0698\u0699\u0005G\u0000"+
		"\u0000\u0699\u069a\u0003\u01ba\u00dd\u0000\u069a\u069b\u0005H\u0000\u0000"+
		"\u069b\u069c\u0003\u0120\u0090\u0000\u069c\u0131\u0001\u0000\u0000\u0000"+
		"\u069d\u069e\u0005$\u0000\u0000\u069e\u069f\u0005G\u0000\u0000\u069f\u06a0"+
		"\u0003\u01ba\u00dd\u0000\u06a0\u06a1\u0005H\u0000\u0000\u06a1\u06a2\u0003"+
		"\u0122\u0091\u0000\u06a2\u06a3\u0005\u001d\u0000\u0000\u06a3\u06a4\u0003"+
		"\u0120\u0090\u0000\u06a4\u0133\u0001\u0000\u0000\u0000\u06a5\u06a6\u0005"+
		"$\u0000\u0000\u06a6\u06a7\u0005G\u0000\u0000\u06a7\u06a8\u0003\u01ba\u00dd"+
		"\u0000\u06a8\u06a9\u0005H\u0000\u0000\u06a9\u06aa\u0003\u0122\u0091\u0000"+
		"\u06aa\u06ab\u0005\u001d\u0000\u0000\u06ab\u06ac\u0003\u0122\u0091\u0000"+
		"\u06ac\u0135\u0001\u0000\u0000\u0000\u06ad\u06ae\u0005\u0010\u0000\u0000"+
		"\u06ae\u06af\u0003\u01ba\u00dd\u0000\u06af\u06b0\u0005M\u0000\u0000\u06b0"+
		"\u06b8\u0001\u0000\u0000\u0000\u06b1\u06b2\u0005\u0010\u0000\u0000\u06b2"+
		"\u06b3\u0003\u01ba\u00dd\u0000\u06b3\u06b4\u0005V\u0000\u0000\u06b4\u06b5"+
		"\u0003\u01ba\u00dd\u0000\u06b5\u06b6\u0005M\u0000\u0000\u06b6\u06b8\u0001"+
		"\u0000\u0000\u0000\u06b7\u06ad\u0001\u0000\u0000\u0000\u06b7\u06b1\u0001"+
		"\u0000\u0000\u0000\u06b8\u0137\u0001\u0000\u0000\u0000\u06b9\u06ba\u0005"+
		"7\u0000\u0000\u06ba\u06bb\u0005G\u0000\u0000\u06bb\u06bc\u0003\u01ba\u00dd"+
		"\u0000\u06bc\u06bd\u0005H\u0000\u0000\u06bd\u06be\u0003\u013a\u009d\u0000"+
		"\u06be\u0139\u0001\u0000\u0000\u0000\u06bf\u06c3\u0005I\u0000\u0000\u06c0"+
		"\u06c2\u0003\u013c\u009e\u0000\u06c1\u06c0\u0001\u0000\u0000\u0000\u06c2"+
		"\u06c5\u0001\u0000\u0000\u0000\u06c3\u06c1\u0001\u0000\u0000\u0000\u06c3"+
		"\u06c4\u0001\u0000\u0000\u0000\u06c4\u06c9\u0001\u0000\u0000\u0000\u06c5"+
		"\u06c3\u0001\u0000\u0000\u0000\u06c6\u06c8\u0003\u0140\u00a0\u0000\u06c7"+
		"\u06c6\u0001\u0000\u0000\u0000\u06c8\u06cb\u0001\u0000\u0000\u0000\u06c9"+
		"\u06c7\u0001\u0000\u0000\u0000\u06c9\u06ca\u0001\u0000\u0000\u0000\u06ca"+
		"\u06cc\u0001\u0000\u0000\u0000\u06cb\u06c9\u0001\u0000\u0000\u0000\u06cc"+
		"\u06cd\u0005J\u0000\u0000\u06cd\u013b\u0001\u0000\u0000\u0000\u06ce\u06cf"+
		"\u0003\u013e\u009f\u0000\u06cf\u06d0\u0003\u0116\u008b\u0000\u06d0\u013d"+
		"\u0001\u0000\u0000\u0000\u06d1\u06d5\u0003\u0140\u00a0\u0000\u06d2\u06d4"+
		"\u0003\u0140\u00a0\u0000\u06d3\u06d2\u0001\u0000\u0000\u0000\u06d4\u06d7"+
		"\u0001\u0000\u0000\u0000\u06d5\u06d3\u0001\u0000\u0000\u0000\u06d5\u06d6"+
		"\u0001\u0000\u0000\u0000\u06d6\u013f\u0001\u0000\u0000\u0000\u06d7\u06d5"+
		"\u0001\u0000\u0000\u0000\u06d8\u06d9\u0005\u0014\u0000\u0000\u06d9\u06da"+
		"\u0003\u01b8\u00dc\u0000\u06da\u06db\u0005V\u0000\u0000\u06db\u06e3\u0001"+
		"\u0000\u0000\u0000\u06dc\u06dd\u0005\u0014\u0000\u0000\u06dd\u06de\u0003"+
		"\u0142\u00a1\u0000\u06de\u06df\u0005V\u0000\u0000\u06df\u06e3\u0001\u0000"+
		"\u0000\u0000\u06e0\u06e1\u0005\u001a\u0000\u0000\u06e1\u06e3\u0005V\u0000"+
		"\u0000\u06e2\u06d8\u0001\u0000\u0000\u0000\u06e2\u06dc\u0001\u0000\u0000"+
		"\u0000\u06e2\u06e0\u0001\u0000\u0000\u0000\u06e3\u0141\u0001\u0000\u0000"+
		"\u0000\u06e4\u06e5\u0003\u0000\u0000\u0000\u06e5\u0143\u0001\u0000\u0000"+
		"\u0000\u06e6\u06e7\u0005@\u0000\u0000\u06e7\u06e8\u0005G\u0000\u0000\u06e8"+
		"\u06e9\u0003\u01ba\u00dd\u0000\u06e9\u06ea\u0005H\u0000\u0000\u06ea\u06eb"+
		"\u0003\u0120\u0090\u0000\u06eb\u0145\u0001\u0000\u0000\u0000\u06ec\u06ed"+
		"\u0005@\u0000\u0000\u06ed\u06ee\u0005G\u0000\u0000\u06ee\u06ef\u0003\u01ba"+
		"\u00dd\u0000\u06ef\u06f0\u0005H\u0000\u0000\u06f0\u06f1\u0003\u0122\u0091"+
		"\u0000\u06f1\u0147\u0001\u0000\u0000\u0000\u06f2\u06f3\u0005\u001b\u0000"+
		"\u0000\u06f3\u06f4\u0003\u0120\u0090\u0000\u06f4\u06f5\u0005@\u0000\u0000"+
		"\u06f5\u06f6\u0005G\u0000\u0000\u06f6\u06f7\u0003\u01ba\u00dd\u0000\u06f7"+
		"\u06f8\u0005H\u0000\u0000\u06f8\u06f9\u0005M\u0000\u0000\u06f9\u0149\u0001"+
		"\u0000\u0000\u0000\u06fa\u06fd\u0003\u014e\u00a7\u0000\u06fb\u06fd\u0003"+
		"\u0158\u00ac\u0000\u06fc\u06fa\u0001\u0000\u0000\u0000\u06fc\u06fb\u0001"+
		"\u0000\u0000\u0000\u06fd\u014b\u0001\u0000\u0000\u0000\u06fe\u0701\u0003"+
		"\u0150\u00a8\u0000\u06ff\u0701\u0003\u015a\u00ad\u0000\u0700\u06fe\u0001"+
		"\u0000\u0000\u0000\u0700\u06ff\u0001\u0000\u0000\u0000\u0701\u014d\u0001"+
		"\u0000\u0000\u0000\u0702\u0703\u0005#\u0000\u0000\u0703\u0705\u0005G\u0000"+
		"\u0000\u0704\u0706\u0003\u0152\u00a9\u0000\u0705\u0704\u0001\u0000\u0000"+
		"\u0000\u0705\u0706\u0001\u0000\u0000\u0000\u0706\u0707\u0001\u0000\u0000"+
		"\u0000\u0707\u0709\u0005M\u0000\u0000\u0708\u070a\u0003\u01ba\u00dd\u0000"+
		"\u0709\u0708\u0001\u0000\u0000\u0000\u0709\u070a\u0001\u0000\u0000\u0000"+
		"\u070a\u070b\u0001\u0000\u0000\u0000\u070b\u070d\u0005M\u0000\u0000\u070c"+
		"\u070e\u0003\u0154\u00aa\u0000\u070d\u070c\u0001\u0000\u0000\u0000\u070d"+
		"\u070e\u0001\u0000\u0000\u0000\u070e\u070f\u0001\u0000\u0000\u0000\u070f"+
		"\u0710\u0005H\u0000\u0000\u0710\u0711\u0003\u0120\u0090\u0000\u0711\u014f"+
		"\u0001\u0000\u0000\u0000\u0712\u0713\u0005#\u0000\u0000\u0713\u0715\u0005"+
		"G\u0000\u0000\u0714\u0716\u0003\u0152\u00a9\u0000\u0715\u0714\u0001\u0000"+
		"\u0000\u0000\u0715\u0716\u0001\u0000\u0000\u0000\u0716\u0717\u0001\u0000"+
		"\u0000\u0000\u0717\u0719\u0005M\u0000\u0000\u0718\u071a\u0003\u01ba\u00dd"+
		"\u0000\u0719\u0718\u0001\u0000\u0000\u0000\u0719\u071a\u0001\u0000\u0000"+
		"\u0000\u071a\u071b\u0001\u0000\u0000\u0000\u071b\u071d\u0005M\u0000\u0000"+
		"\u071c\u071e\u0003\u0154\u00aa\u0000\u071d\u071c\u0001\u0000\u0000\u0000"+
		"\u071d\u071e\u0001\u0000\u0000\u0000\u071e\u071f\u0001\u0000\u0000\u0000"+
		"\u071f\u0720\u0005H\u0000\u0000\u0720\u0721\u0003\u0122\u0091\u0000\u0721"+
		"\u0151\u0001\u0000\u0000\u0000\u0722\u0725\u0003\u0156\u00ab\u0000\u0723"+
		"\u0725\u0003\u011c\u008e\u0000\u0724\u0722\u0001\u0000\u0000\u0000\u0724"+
		"\u0723\u0001\u0000\u0000\u0000\u0725\u0153\u0001\u0000\u0000\u0000\u0726"+
		"\u0727\u0003\u0156\u00ab\u0000\u0727\u0155\u0001\u0000\u0000\u0000\u0728"+
		"\u072d\u0003\u012e\u0097\u0000\u0729\u072a\u0005N\u0000\u0000\u072a\u072c"+
		"\u0003\u012e\u0097\u0000\u072b\u0729\u0001\u0000\u0000\u0000\u072c\u072f"+
		"\u0001\u0000\u0000\u0000\u072d\u072b\u0001\u0000\u0000\u0000\u072d\u072e"+
		"\u0001\u0000\u0000\u0000\u072e\u0157\u0001\u0000\u0000\u0000\u072f\u072d"+
		"\u0001\u0000\u0000\u0000\u0730\u0731\u0005#\u0000\u0000\u0731\u0735\u0005"+
		"G\u0000\u0000\u0732\u0734\u0003\u00acV\u0000\u0733\u0732\u0001\u0000\u0000"+
		"\u0000\u0734\u0737\u0001\u0000\u0000\u0000\u0735\u0733\u0001\u0000\u0000"+
		"\u0000\u0735\u0736\u0001\u0000\u0000\u0000\u0736\u0738\u0001\u0000\u0000"+
		"\u0000\u0737\u0735\u0001\u0000\u0000\u0000\u0738\u0739\u0003\u0082A\u0000"+
		"\u0739\u073a\u0003~?\u0000\u073a\u073b\u0005V\u0000\u0000\u073b\u073c"+
		"\u0003\u01ba\u00dd\u0000\u073c\u073d\u0005H\u0000\u0000\u073d\u073e\u0003"+
		"\u0120\u0090\u0000\u073e\u0159\u0001\u0000\u0000\u0000\u073f\u0740\u0005"+
		"#\u0000\u0000\u0740\u0744\u0005G\u0000\u0000\u0741\u0743\u0003\u00acV"+
		"\u0000\u0742\u0741\u0001\u0000\u0000\u0000\u0743\u0746\u0001\u0000\u0000"+
		"\u0000\u0744\u0742\u0001\u0000\u0000\u0000\u0744\u0745\u0001\u0000\u0000"+
		"\u0000\u0745\u0747\u0001\u0000\u0000\u0000\u0746\u0744\u0001\u0000\u0000"+
		"\u0000\u0747\u0748\u0003\u0082A\u0000\u0748\u0749\u0003~?\u0000\u0749"+
		"\u074a\u0005V\u0000\u0000\u074a\u074b\u0003\u01ba\u00dd\u0000\u074b\u074c"+
		"\u0005H\u0000\u0000\u074c\u074d\u0003\u0122\u0091\u0000\u074d\u015b\u0001"+
		"\u0000\u0000\u0000\u074e\u0750\u0005\u0012\u0000\u0000\u074f\u0751\u0003"+
		"\u0000\u0000\u0000\u0750\u074f\u0001\u0000\u0000\u0000\u0750\u0751\u0001"+
		"\u0000\u0000\u0000\u0751\u0752\u0001\u0000\u0000\u0000\u0752\u0753\u0005"+
		"M\u0000\u0000\u0753\u015d\u0001\u0000\u0000\u0000\u0754\u0756\u0005\u0019"+
		"\u0000\u0000\u0755\u0757\u0003\u0000\u0000\u0000\u0756\u0755\u0001\u0000"+
		"\u0000\u0000\u0756\u0757\u0001\u0000\u0000\u0000\u0757\u0758\u0001\u0000"+
		"\u0000\u0000\u0758\u0759\u0005M\u0000\u0000\u0759\u015f\u0001\u0000\u0000"+
		"\u0000\u075a\u075c\u00052\u0000\u0000\u075b\u075d\u0003\u01ba\u00dd\u0000"+
		"\u075c\u075b\u0001\u0000\u0000\u0000\u075c\u075d\u0001\u0000\u0000\u0000"+
		"\u075d\u075e\u0001\u0000\u0000\u0000\u075e\u075f\u0005M\u0000\u0000\u075f"+
		"\u0161\u0001\u0000\u0000\u0000\u0760\u0761\u0005:\u0000\u0000\u0761\u0762"+
		"\u0003\u01ba\u00dd\u0000\u0762\u0763\u0005M\u0000\u0000\u0763\u0163\u0001"+
		"\u0000\u0000\u0000\u0764\u0765\u00058\u0000\u0000\u0765\u0766\u0005G\u0000"+
		"\u0000\u0766\u0767\u0003\u01ba\u00dd\u0000\u0767\u0768\u0005H\u0000\u0000"+
		"\u0768\u0769\u0003\u0114\u008a\u0000\u0769\u0165\u0001\u0000\u0000\u0000"+
		"\u076a\u076b\u0005=\u0000\u0000\u076b\u076c\u0003\u0114\u008a\u0000\u076c"+
		"\u076d\u0003\u0168\u00b4\u0000\u076d\u0777\u0001\u0000\u0000\u0000\u076e"+
		"\u076f\u0005=\u0000\u0000\u076f\u0771\u0003\u0114\u008a\u0000\u0770\u0772"+
		"\u0003\u0168\u00b4\u0000\u0771\u0770\u0001\u0000\u0000\u0000\u0771\u0772"+
		"\u0001\u0000\u0000\u0000\u0772\u0773\u0001\u0000\u0000\u0000\u0773\u0774"+
		"\u0003\u0170\u00b8\u0000\u0774\u0777\u0001\u0000\u0000\u0000\u0775\u0777"+
		"\u0003\u0172\u00b9\u0000\u0776\u076a\u0001\u0000\u0000\u0000\u0776\u076e"+
		"\u0001\u0000\u0000\u0000\u0776\u0775\u0001\u0000\u0000\u0000\u0777\u0167"+
		"\u0001\u0000\u0000\u0000\u0778\u077c\u0003\u016a\u00b5\u0000\u0779\u077b"+
		"\u0003\u016a\u00b5\u0000\u077a\u0779\u0001\u0000\u0000\u0000\u077b\u077e"+
		"\u0001\u0000\u0000\u0000\u077c\u077a\u0001\u0000\u0000\u0000\u077c\u077d"+
		"\u0001\u0000\u0000\u0000\u077d\u0169\u0001\u0000\u0000\u0000\u077e\u077c"+
		"\u0001\u0000\u0000\u0000\u077f\u0780\u0005\u0015\u0000\u0000\u0780\u0781"+
		"\u0005G\u0000\u0000\u0781\u0782\u0003\u016c\u00b6\u0000\u0782\u0783\u0005"+
		"H\u0000\u0000\u0783\u0784\u0003\u0114\u008a\u0000\u0784\u016b\u0001\u0000"+
		"\u0000\u0000\u0785\u0787\u0003\u00acV\u0000\u0786\u0785\u0001\u0000\u0000"+
		"\u0000\u0787\u078a\u0001\u0000\u0000\u0000\u0788\u0786\u0001\u0000\u0000"+
		"\u0000\u0788\u0789\u0001\u0000\u0000\u0000\u0789\u078b\u0001\u0000\u0000"+
		"\u0000\u078a\u0788\u0001\u0000\u0000\u0000\u078b\u078c\u0003\u016e\u00b7"+
		"\u0000\u078c\u078d\u0003~?\u0000\u078d\u016d\u0001\u0000\u0000\u0000\u078e"+
		"\u0793\u0003\u008aE\u0000\u078f\u0790\u0005c\u0000\u0000\u0790\u0792\u0003"+
		"\u0012\t\u0000\u0791\u078f\u0001\u0000\u0000\u0000\u0792\u0795\u0001\u0000"+
		"\u0000\u0000\u0793\u0791\u0001\u0000\u0000\u0000\u0793\u0794\u0001\u0000"+
		"\u0000\u0000\u0794\u016f\u0001\u0000\u0000\u0000\u0795\u0793\u0001\u0000"+
		"\u0000\u0000\u0796\u0797\u0005!\u0000\u0000\u0797\u0798\u0003\u0114\u008a"+
		"\u0000\u0798\u0171\u0001\u0000\u0000\u0000\u0799\u079a\u0005=\u0000\u0000"+
		"\u079a\u079b\u0003\u0174\u00ba\u0000\u079b\u079d\u0003\u0114\u008a\u0000"+
		"\u079c\u079e\u0003\u0168\u00b4\u0000\u079d\u079c\u0001\u0000\u0000\u0000"+
		"\u079d\u079e\u0001\u0000\u0000\u0000\u079e\u07a0\u0001\u0000\u0000\u0000"+
		"\u079f\u07a1\u0003\u0170\u00b8\u0000\u07a0\u079f\u0001\u0000\u0000\u0000"+
		"\u07a0\u07a1\u0001\u0000\u0000\u0000\u07a1\u0173\u0001\u0000\u0000\u0000"+
		"\u07a2\u07a3\u0005G\u0000\u0000\u07a3\u07a5\u0003\u0176\u00bb\u0000\u07a4"+
		"\u07a6\u0005M\u0000\u0000\u07a5\u07a4\u0001\u0000\u0000\u0000\u07a5\u07a6"+
		"\u0001\u0000\u0000\u0000\u07a6\u07a7\u0001\u0000\u0000\u0000\u07a7\u07a8"+
		"\u0005H\u0000\u0000\u07a8\u0175\u0001\u0000\u0000\u0000\u07a9\u07ae\u0003"+
		"\u0178\u00bc\u0000\u07aa\u07ab\u0005M\u0000\u0000\u07ab\u07ad\u0003\u0178"+
		"\u00bc\u0000\u07ac\u07aa\u0001\u0000\u0000\u0000\u07ad\u07b0\u0001\u0000"+
		"\u0000\u0000\u07ae\u07ac\u0001\u0000\u0000\u0000\u07ae\u07af\u0001\u0000"+
		"\u0000\u0000\u07af\u0177\u0001\u0000\u0000\u0000\u07b0\u07ae\u0001\u0000"+
		"\u0000\u0000\u07b1\u07b3\u0003\u00acV\u0000\u07b2\u07b1\u0001\u0000\u0000"+
		"\u0000\u07b3\u07b6\u0001\u0000\u0000\u0000\u07b4\u07b2\u0001\u0000\u0000"+
		"\u0000\u07b4\u07b5\u0001\u0000\u0000\u0000\u07b5\u07b7\u0001\u0000\u0000"+
		"\u0000\u07b6\u07b4\u0001\u0000\u0000\u0000\u07b7\u07b8\u0003\u0082A\u0000"+
		"\u07b8\u07b9\u0003~?\u0000\u07b9\u07ba\u0005P\u0000\u0000\u07ba\u07bb"+
		"\u0003\u01ba\u00dd\u0000\u07bb\u07be\u0001\u0000\u0000\u0000\u07bc\u07be"+
		"\u0003\u017a\u00bd\u0000\u07bd\u07b4\u0001\u0000\u0000\u0000\u07bd\u07bc"+
		"\u0001\u0000\u0000\u0000\u07be\u0179\u0001\u0000\u0000\u0000\u07bf\u07c2"+
		"\u0003<\u001e\u0000\u07c0\u07c2\u0003\u0198\u00cc\u0000\u07c1\u07bf\u0001"+
		"\u0000\u0000\u0000\u07c1\u07c0\u0001\u0000\u0000\u0000\u07c2\u017b\u0001"+
		"\u0000\u0000\u0000\u07c3\u07c6\u0003\u018a\u00c5\u0000\u07c4\u07c6\u0003"+
		"\u01b2\u00d9\u0000\u07c5\u07c3\u0001\u0000\u0000\u0000\u07c5\u07c4\u0001"+
		"\u0000\u0000\u0000\u07c6\u07ca\u0001\u0000\u0000\u0000\u07c7\u07c9\u0003"+
		"\u0184\u00c2\u0000\u07c8\u07c7\u0001\u0000\u0000\u0000\u07c9\u07cc\u0001"+
		"\u0000\u0000\u0000\u07ca\u07c8\u0001\u0000\u0000\u0000\u07ca\u07cb\u0001"+
		"\u0000\u0000\u0000\u07cb\u017d\u0001\u0000\u0000\u0000\u07cc\u07ca\u0001"+
		"\u0000\u0000\u0000\u07cd\u07ea\u0003\u0002\u0001\u0000\u07ce\u07d2\u0003"+
		"8\u001c\u0000\u07cf\u07d1\u0003\u01d2\u00e9\u0000\u07d0\u07cf\u0001\u0000"+
		"\u0000\u0000\u07d1\u07d4\u0001\u0000\u0000\u0000\u07d2\u07d0\u0001\u0000"+
		"\u0000\u0000\u07d2\u07d3\u0001\u0000\u0000\u0000\u07d3\u07d5\u0001\u0000"+
		"\u0000\u0000\u07d4\u07d2\u0001\u0000\u0000\u0000\u07d5\u07d6\u0005O\u0000"+
		"\u0000\u07d6\u07d7\u0005\u0017\u0000\u0000\u07d7\u07ea\u0001\u0000\u0000"+
		"\u0000\u07d8\u07d9\u0005>\u0000\u0000\u07d9\u07da\u0005O\u0000\u0000\u07da"+
		"\u07ea\u0005\u0017\u0000\u0000\u07db\u07ea\u00059\u0000\u0000\u07dc\u07dd"+
		"\u00038\u001c\u0000\u07dd\u07de\u0005O\u0000\u0000\u07de\u07df\u00059"+
		"\u0000\u0000\u07df\u07ea\u0001\u0000\u0000\u0000\u07e0\u07e1\u0005G\u0000"+
		"\u0000\u07e1\u07e2\u0003\u01ba\u00dd\u0000\u07e2\u07e3\u0005H\u0000\u0000"+
		"\u07e3\u07ea\u0001\u0000\u0000\u0000\u07e4\u07ea\u0003\u0190\u00c8\u0000"+
		"\u07e5\u07ea\u0003\u0198\u00cc\u0000\u07e6\u07ea\u0003\u019e\u00cf\u0000"+
		"\u07e7\u07ea\u0003\u01a4\u00d2\u0000\u07e8\u07ea\u0003\u01ac\u00d6\u0000"+
		"\u07e9\u07cd\u0001\u0000\u0000\u0000\u07e9\u07ce\u0001\u0000\u0000\u0000"+
		"\u07e9\u07d8\u0001\u0000\u0000\u0000\u07e9\u07db\u0001\u0000\u0000\u0000"+
		"\u07e9\u07dc\u0001\u0000\u0000\u0000\u07e9\u07e0\u0001\u0000\u0000\u0000"+
		"\u07e9\u07e4\u0001\u0000\u0000\u0000\u07e9\u07e5\u0001\u0000\u0000\u0000"+
		"\u07e9\u07e6\u0001\u0000\u0000\u0000\u07e9\u07e7\u0001\u0000\u0000\u0000"+
		"\u07e9\u07e8\u0001\u0000\u0000\u0000\u07ea\u017f\u0001\u0000\u0000\u0000"+
		"\u07eb\u07ec\u0001\u0000\u0000\u0000\u07ec\u0181\u0001\u0000\u0000\u0000"+
		"\u07ed\u0809\u0003\u0002\u0001\u0000\u07ee\u07f2\u00038\u001c\u0000\u07ef"+
		"\u07f1\u0003\u01d2\u00e9\u0000\u07f0\u07ef\u0001\u0000\u0000\u0000\u07f1"+
		"\u07f4\u0001\u0000\u0000\u0000\u07f2\u07f0\u0001\u0000\u0000\u0000\u07f2"+
		"\u07f3\u0001\u0000\u0000\u0000\u07f3\u07f5\u0001\u0000\u0000\u0000\u07f4"+
		"\u07f2\u0001\u0000\u0000\u0000\u07f5\u07f6\u0005O\u0000\u0000\u07f6\u07f7"+
		"\u0005\u0017\u0000\u0000\u07f7\u0809\u0001\u0000\u0000\u0000\u07f8\u07f9"+
		"\u0005>\u0000\u0000\u07f9\u07fa\u0005O\u0000\u0000\u07fa\u0809\u0005\u0017"+
		"\u0000\u0000\u07fb\u0809\u00059\u0000\u0000\u07fc\u07fd\u00038\u001c\u0000"+
		"\u07fd\u07fe\u0005O\u0000\u0000\u07fe\u07ff\u00059\u0000\u0000\u07ff\u0809"+
		"\u0001\u0000\u0000\u0000\u0800\u0801\u0005G\u0000\u0000\u0801\u0802\u0003"+
		"\u01ba\u00dd\u0000\u0802\u0803\u0005H\u0000\u0000\u0803\u0809\u0001\u0000"+
		"\u0000\u0000\u0804\u0809\u0003\u0190\u00c8\u0000\u0805\u0809\u0003\u0198"+
		"\u00cc\u0000\u0806\u0809\u0003\u01a4\u00d2\u0000\u0807\u0809\u0003\u01ac"+
		"\u00d6\u0000\u0808\u07ed\u0001\u0000\u0000\u0000\u0808\u07ee\u0001\u0000"+
		"\u0000\u0000\u0808\u07f8\u0001\u0000\u0000\u0000\u0808\u07fb\u0001\u0000"+
		"\u0000\u0000\u0808\u07fc\u0001\u0000\u0000\u0000\u0808\u0800\u0001\u0000"+
		"\u0000\u0000\u0808\u0804\u0001\u0000\u0000\u0000\u0808\u0805\u0001\u0000"+
		"\u0000\u0000\u0808\u0806\u0001\u0000\u0000\u0000\u0808\u0807\u0001\u0000"+
		"\u0000\u0000\u0809\u0183\u0001\u0000\u0000\u0000\u080a\u0810\u0003\u0192"+
		"\u00c9\u0000\u080b\u0810\u0003\u019a\u00cd\u0000\u080c\u0810\u0003\u01a0"+
		"\u00d0\u0000\u080d\u0810\u0003\u01a6\u00d3\u0000\u080e\u0810\u0003\u01ae"+
		"\u00d7\u0000\u080f\u080a\u0001\u0000\u0000\u0000\u080f\u080b\u0001\u0000"+
		"\u0000\u0000\u080f\u080c\u0001\u0000\u0000\u0000\u080f\u080d\u0001\u0000"+
		"\u0000\u0000\u080f\u080e\u0001\u0000\u0000\u0000\u0810\u0185\u0001\u0000"+
		"\u0000\u0000\u0811\u0812\u0001\u0000\u0000\u0000\u0812\u0187\u0001\u0000"+
		"\u0000\u0000\u0813\u0818\u0003\u0192\u00c9\u0000\u0814\u0818\u0003\u019a"+
		"\u00cd\u0000\u0815\u0818\u0003\u01a6\u00d3\u0000\u0816\u0818\u0003\u01ae"+
		"\u00d7\u0000\u0817\u0813\u0001\u0000\u0000\u0000\u0817\u0814\u0001\u0000"+
		"\u0000\u0000\u0817\u0815\u0001\u0000\u0000\u0000\u0817\u0816\u0001\u0000"+
		"\u0000\u0000\u0818\u0189\u0001\u0000\u0000\u0000\u0819\u0840\u0003\u0002"+
		"\u0001\u0000\u081a\u081e\u00038\u001c\u0000\u081b\u081d\u0003\u01d2\u00e9"+
		"\u0000\u081c\u081b\u0001\u0000\u0000\u0000\u081d\u0820\u0001\u0000\u0000"+
		"\u0000\u081e\u081c\u0001\u0000\u0000\u0000\u081e\u081f\u0001\u0000\u0000"+
		"\u0000\u081f\u0821\u0001\u0000\u0000\u0000\u0820\u081e\u0001\u0000\u0000"+
		"\u0000\u0821\u0822\u0005O\u0000\u0000\u0822\u0823\u0005\u0017\u0000\u0000"+
		"\u0823\u0840\u0001\u0000\u0000\u0000\u0824\u0828\u0003\u0084B\u0000\u0825"+
		"\u0827\u0003\u01d2\u00e9\u0000\u0826\u0825\u0001\u0000\u0000\u0000\u0827"+
		"\u082a\u0001\u0000\u0000\u0000\u0828\u0826\u0001\u0000\u0000\u0000\u0828"+
		"\u0829\u0001\u0000\u0000\u0000\u0829\u082b\u0001\u0000\u0000\u0000\u082a"+
		"\u0828\u0001\u0000\u0000\u0000\u082b\u082c\u0005O\u0000\u0000\u082c\u082d"+
		"\u0005\u0017\u0000\u0000\u082d\u0840\u0001\u0000\u0000\u0000\u082e\u082f"+
		"\u0005>\u0000\u0000\u082f\u0830\u0005O\u0000\u0000\u0830\u0840\u0005\u0017"+
		"\u0000\u0000\u0831\u0840\u00059\u0000\u0000\u0832\u0833\u00038\u001c\u0000"+
		"\u0833\u0834\u0005O\u0000\u0000\u0834\u0835\u00059\u0000\u0000\u0835\u0840"+
		"\u0001\u0000\u0000\u0000\u0836\u0837\u0005G\u0000\u0000\u0837\u0838\u0003"+
		"\u01ba\u00dd\u0000\u0838\u0839\u0005H\u0000\u0000\u0839\u0840\u0001\u0000"+
		"\u0000\u0000\u083a\u0840\u0003\u0194\u00ca\u0000\u083b\u0840\u0003\u019c"+
		"\u00ce\u0000\u083c\u0840\u0003\u01a2\u00d1\u0000\u083d\u0840\u0003\u01a8"+
		"\u00d4\u0000\u083e\u0840\u0003\u01b0\u00d8\u0000\u083f\u0819\u0001\u0000"+
		"\u0000\u0000\u083f\u081a\u0001\u0000\u0000\u0000\u083f\u0824\u0001\u0000"+
		"\u0000\u0000\u083f\u082e\u0001\u0000\u0000\u0000\u083f\u0831\u0001\u0000"+
		"\u0000\u0000\u083f\u0832\u0001\u0000\u0000\u0000\u083f\u0836\u0001\u0000"+
		"\u0000\u0000\u083f\u083a\u0001\u0000\u0000\u0000\u083f\u083b\u0001\u0000"+
		"\u0000\u0000\u083f\u083c\u0001\u0000\u0000\u0000\u083f\u083d\u0001\u0000"+
		"\u0000\u0000\u083f\u083e\u0001\u0000\u0000\u0000\u0840\u018b\u0001\u0000"+
		"\u0000\u0000\u0841\u0842\u0001\u0000\u0000\u0000\u0842\u018d\u0001\u0000"+
		"\u0000\u0000\u0843\u0869\u0003\u0002\u0001\u0000\u0844\u0848\u00038\u001c"+
		"\u0000\u0845\u0847\u0003\u01d2\u00e9\u0000\u0846\u0845\u0001\u0000\u0000"+
		"\u0000\u0847\u084a\u0001\u0000\u0000\u0000\u0848\u0846\u0001\u0000\u0000"+
		"\u0000\u0848\u0849\u0001\u0000\u0000\u0000\u0849\u084b\u0001\u0000\u0000"+
		"\u0000\u084a\u0848\u0001\u0000\u0000\u0000\u084b\u084c\u0005O\u0000\u0000"+
		"\u084c\u084d\u0005\u0017\u0000\u0000\u084d\u0869\u0001\u0000\u0000\u0000"+
		"\u084e\u0852\u0003\u0084B\u0000\u084f\u0851\u0003\u01d2\u00e9\u0000\u0850"+
		"\u084f\u0001\u0000\u0000\u0000\u0851\u0854\u0001\u0000\u0000\u0000\u0852"+
		"\u0850\u0001\u0000\u0000\u0000\u0852\u0853\u0001\u0000\u0000\u0000\u0853"+
		"\u0855\u0001\u0000\u0000\u0000\u0854\u0852\u0001\u0000\u0000\u0000\u0855"+
		"\u0856\u0005O\u0000\u0000\u0856\u0857\u0005\u0017\u0000\u0000\u0857\u0869"+
		"\u0001\u0000\u0000\u0000\u0858\u0859\u0005>\u0000\u0000\u0859\u085a\u0005"+
		"O\u0000\u0000\u085a\u0869\u0005\u0017\u0000\u0000\u085b\u0869\u00059\u0000"+
		"\u0000\u085c\u085d\u00038\u001c\u0000\u085d\u085e\u0005O\u0000\u0000\u085e"+
		"\u085f\u00059\u0000\u0000\u085f\u0869\u0001\u0000\u0000\u0000\u0860\u0861"+
		"\u0005G\u0000\u0000\u0861\u0862\u0003\u01ba\u00dd\u0000\u0862\u0863\u0005"+
		"H\u0000\u0000\u0863\u0869\u0001\u0000\u0000\u0000\u0864\u0869\u0003\u0194"+
		"\u00ca\u0000\u0865\u0869\u0003\u019c\u00ce\u0000\u0866\u0869\u0003\u01a8"+
		"\u00d4\u0000\u0867\u0869\u0003\u01b0\u00d8\u0000\u0868\u0843\u0001\u0000"+
		"\u0000\u0000\u0868\u0844\u0001\u0000\u0000\u0000\u0868\u084e\u0001\u0000"+
		"\u0000\u0000\u0868\u0858\u0001\u0000\u0000\u0000\u0868\u085b\u0001\u0000"+
		"\u0000\u0000\u0868\u085c\u0001\u0000\u0000\u0000\u0868\u0860\u0001\u0000"+
		"\u0000\u0000\u0868\u0864\u0001\u0000\u0000\u0000\u0868\u0865\u0001\u0000"+
		"\u0000\u0000\u0868\u0866\u0001\u0000\u0000\u0000\u0868\u0867\u0001\u0000"+
		"\u0000\u0000\u0869\u018f\u0001\u0000\u0000\u0000\u086a\u086c\u0005-\u0000"+
		"\u0000\u086b\u086d\u0003,\u0016\u0000\u086c\u086b\u0001\u0000\u0000\u0000"+
		"\u086c\u086d\u0001\u0000\u0000\u0000\u086d\u086e\u0001\u0000\u0000\u0000"+
		"\u086e\u0873\u0003\u00fc~\u0000\u086f\u0870\u0005O\u0000\u0000\u0870\u0872"+
		"\u0003\u00fc~\u0000\u0871\u086f\u0001\u0000\u0000\u0000\u0872\u0875\u0001"+
		"\u0000\u0000\u0000\u0873\u0871\u0001\u0000\u0000\u0000\u0873\u0874\u0001"+
		"\u0000\u0000\u0000\u0874\u0877\u0001\u0000\u0000\u0000\u0875\u0873\u0001"+
		"\u0000\u0000\u0000\u0876\u0878\u0003\u0196\u00cb\u0000\u0877\u0876\u0001"+
		"\u0000\u0000\u0000\u0877\u0878\u0001\u0000\u0000\u0000\u0878\u0879\u0001"+
		"\u0000\u0000\u0000\u0879\u087b\u0005G\u0000\u0000\u087a\u087c\u0003\u01aa"+
		"\u00d5\u0000\u087b\u087a\u0001\u0000\u0000\u0000\u087b\u087c\u0001\u0000"+
		"\u0000\u0000\u087c\u087d\u0001\u0000\u0000\u0000\u087d\u087f\u0005H\u0000"+
		"\u0000\u087e\u0880\u0003n7\u0000\u087f\u087e\u0001\u0000\u0000\u0000\u087f"+
		"\u0880\u0001\u0000\u0000\u0000\u0880\u08a6\u0001\u0000\u0000\u0000\u0881"+
		"\u0882\u0003<\u001e\u0000\u0882\u0883\u0005O\u0000\u0000\u0883\u0885\u0005"+
		"-\u0000\u0000\u0884\u0886\u0003,\u0016\u0000\u0885\u0884\u0001\u0000\u0000"+
		"\u0000\u0885\u0886\u0001\u0000\u0000\u0000\u0886\u0887\u0001\u0000\u0000"+
		"\u0000\u0887\u0889\u0003\u00fc~\u0000\u0888\u088a\u0003\u0196\u00cb\u0000"+
		"\u0889\u0888\u0001\u0000\u0000\u0000\u0889\u088a\u0001\u0000\u0000\u0000"+
		"\u088a\u088b\u0001\u0000\u0000\u0000\u088b\u088d\u0005G\u0000\u0000\u088c"+
		"\u088e\u0003\u01aa\u00d5\u0000\u088d\u088c\u0001\u0000\u0000\u0000\u088d"+
		"\u088e\u0001\u0000\u0000\u0000\u088e\u088f\u0001\u0000\u0000\u0000\u088f"+
		"\u0891\u0005H\u0000\u0000\u0890\u0892\u0003n7\u0000\u0891\u0890\u0001"+
		"\u0000\u0000\u0000\u0891\u0892\u0001\u0000\u0000\u0000\u0892\u08a6\u0001"+
		"\u0000\u0000\u0000\u0893\u0894\u0003\u017c\u00be\u0000\u0894\u0895\u0005"+
		"O\u0000\u0000\u0895\u0897\u0005-\u0000\u0000\u0896\u0898\u0003,\u0016"+
		"\u0000\u0897\u0896\u0001\u0000\u0000\u0000\u0897\u0898\u0001\u0000\u0000"+
		"\u0000\u0898\u0899\u0001\u0000\u0000\u0000\u0899\u089b\u0003\u00fc~\u0000"+
		"\u089a\u089c\u0003\u0196\u00cb\u0000\u089b\u089a\u0001\u0000\u0000\u0000"+
		"\u089b\u089c\u0001\u0000\u0000\u0000\u089c\u089d\u0001\u0000\u0000\u0000"+
		"\u089d\u089f\u0005G\u0000\u0000\u089e\u08a0\u0003\u01aa\u00d5\u0000\u089f"+
		"\u089e\u0001\u0000\u0000\u0000\u089f\u08a0\u0001\u0000\u0000\u0000\u08a0"+
		"\u08a1\u0001\u0000\u0000\u0000\u08a1\u08a3\u0005H\u0000\u0000\u08a2\u08a4"+
		"\u0003n7\u0000\u08a3\u08a2\u0001\u0000\u0000\u0000\u08a3\u08a4\u0001\u0000"+
		"\u0000\u0000\u08a4\u08a6\u0001\u0000\u0000\u0000\u08a5\u086a\u0001\u0000"+
		"\u0000\u0000\u08a5\u0881\u0001\u0000\u0000\u0000\u08a5\u0893\u0001\u0000"+
		"\u0000\u0000\u08a6\u0191\u0001\u0000\u0000\u0000\u08a7\u08a8\u0005O\u0000"+
		"\u0000\u08a8\u08aa\u0005-\u0000\u0000\u08a9\u08ab\u0003,\u0016\u0000\u08aa"+
		"\u08a9\u0001\u0000\u0000\u0000\u08aa\u08ab\u0001\u0000\u0000\u0000\u08ab"+
		"\u08ac\u0001\u0000\u0000\u0000\u08ac\u08ae\u0003\u00fc~\u0000\u08ad\u08af"+
		"\u0003\u0196\u00cb\u0000\u08ae\u08ad\u0001\u0000\u0000\u0000\u08ae\u08af"+
		"\u0001\u0000\u0000\u0000\u08af\u08b0\u0001\u0000\u0000\u0000\u08b0\u08b2"+
		"\u0005G\u0000\u0000\u08b1\u08b3\u0003\u01aa\u00d5\u0000\u08b2\u08b1\u0001"+
		"\u0000\u0000\u0000\u08b2\u08b3\u0001\u0000\u0000\u0000\u08b3\u08b4\u0001"+
		"\u0000\u0000\u0000\u08b4\u08b6\u0005H\u0000\u0000\u08b5\u08b7\u0003n7"+
		"\u0000\u08b6\u08b5\u0001\u0000\u0000\u0000\u08b6\u08b7\u0001\u0000\u0000"+
		"\u0000\u08b7\u0193\u0001\u0000\u0000\u0000\u08b8\u08ba\u0005-\u0000\u0000"+
		"\u08b9\u08bb\u0003,\u0016\u0000\u08ba\u08b9\u0001\u0000\u0000\u0000\u08ba"+
		"\u08bb\u0001\u0000\u0000\u0000\u08bb\u08bc\u0001\u0000\u0000\u0000\u08bc"+
		"\u08c1\u0003\u00fc~\u0000\u08bd\u08be\u0005O\u0000\u0000\u08be\u08c0\u0003"+
		"\u00fc~\u0000\u08bf\u08bd\u0001\u0000\u0000\u0000\u08c0\u08c3\u0001\u0000"+
		"\u0000\u0000\u08c1\u08bf\u0001\u0000\u0000\u0000\u08c1\u08c2\u0001\u0000"+
		"\u0000\u0000\u08c2\u08c5\u0001\u0000\u0000\u0000\u08c3\u08c1\u0001\u0000"+
		"\u0000\u0000\u08c4\u08c6\u0003\u0196\u00cb\u0000\u08c5\u08c4\u0001\u0000"+
		"\u0000\u0000\u08c5\u08c6\u0001\u0000\u0000\u0000\u08c6\u08c7\u0001\u0000"+
		"\u0000\u0000\u08c7\u08c9\u0005G\u0000\u0000\u08c8\u08ca\u0003\u01aa\u00d5"+
		"\u0000\u08c9\u08c8\u0001\u0000\u0000\u0000\u08c9\u08ca\u0001\u0000\u0000"+
		"\u0000\u08ca\u08cb\u0001\u0000\u0000\u0000\u08cb\u08cd\u0005H\u0000\u0000"+
		"\u08cc\u08ce\u0003n7\u0000\u08cd\u08cc\u0001\u0000\u0000\u0000\u08cd\u08ce"+
		"\u0001\u0000\u0000\u0000\u08ce\u08e2\u0001\u0000\u0000\u0000\u08cf\u08d0"+
		"\u0003<\u001e\u0000\u08d0\u08d1\u0005O\u0000\u0000\u08d1\u08d3\u0005-"+
		"\u0000\u0000\u08d2\u08d4\u0003,\u0016\u0000\u08d3\u08d2\u0001\u0000\u0000"+
		"\u0000\u08d3\u08d4\u0001\u0000\u0000\u0000\u08d4\u08d5\u0001\u0000\u0000"+
		"\u0000\u08d5\u08d7\u0003\u00fc~\u0000\u08d6\u08d8\u0003\u0196\u00cb\u0000"+
		"\u08d7\u08d6\u0001\u0000\u0000\u0000\u08d7\u08d8\u0001\u0000\u0000\u0000"+
		"\u08d8\u08d9\u0001\u0000\u0000\u0000\u08d9\u08db\u0005G\u0000\u0000\u08da"+
		"\u08dc\u0003\u01aa\u00d5\u0000\u08db\u08da\u0001\u0000\u0000\u0000\u08db"+
		"\u08dc\u0001\u0000\u0000\u0000\u08dc\u08dd\u0001\u0000\u0000\u0000\u08dd"+
		"\u08df\u0005H\u0000\u0000\u08de\u08e0\u0003n7\u0000\u08df\u08de\u0001"+
		"\u0000\u0000\u0000\u08df\u08e0\u0001\u0000\u0000\u0000\u08e0\u08e2\u0001"+
		"\u0000\u0000\u0000\u08e1\u08b8\u0001\u0000\u0000\u0000\u08e1\u08cf\u0001"+
		"\u0000\u0000\u0000\u08e2\u0195\u0001\u0000\u0000\u0000\u08e3\u08e6\u0003"+
		",\u0016\u0000\u08e4\u08e6\u0005\f\u0000\u0000\u08e5\u08e3\u0001\u0000"+
		"\u0000\u0000\u08e5\u08e4\u0001\u0000\u0000\u0000\u08e6\u0197\u0001\u0000"+
		"\u0000\u0000\u08e7\u08e8\u0003\u017c\u00be\u0000\u08e8\u08e9\u0005O\u0000"+
		"\u0000\u08e9\u08ea\u0003\u0000\u0000\u0000\u08ea\u08f5\u0001\u0000\u0000"+
		"\u0000\u08eb\u08ec\u00056\u0000\u0000\u08ec\u08ed\u0005O\u0000\u0000\u08ed"+
		"\u08f5\u0003\u0000\u0000\u0000\u08ee\u08ef\u00038\u001c\u0000\u08ef\u08f0"+
		"\u0005O\u0000\u0000\u08f0\u08f1\u00056\u0000\u0000\u08f1\u08f2\u0005O"+
		"\u0000\u0000\u08f2\u08f3\u0003\u0000\u0000\u0000\u08f3\u08f5\u0001\u0000"+
		"\u0000\u0000\u08f4\u08e7\u0001\u0000\u0000\u0000\u08f4\u08eb\u0001\u0000"+
		"\u0000\u0000\u08f4\u08ee\u0001\u0000\u0000\u0000\u08f5\u0199\u0001\u0000"+
		"\u0000\u0000\u08f6\u08f7\u0005O\u0000\u0000\u08f7\u08f8\u0003\u0000\u0000"+
		"\u0000\u08f8\u019b\u0001\u0000\u0000\u0000\u08f9\u08fa\u00056\u0000\u0000"+
		"\u08fa\u08fb\u0005O\u0000\u0000\u08fb\u0903\u0003\u0000\u0000\u0000\u08fc"+
		"\u08fd\u00038\u001c\u0000\u08fd\u08fe\u0005O\u0000\u0000\u08fe\u08ff\u0005"+
		"6\u0000\u0000\u08ff\u0900\u0005O\u0000\u0000\u0900\u0901\u0003\u0000\u0000"+
		"\u0000\u0901\u0903\u0001\u0000\u0000\u0000\u0902\u08f9\u0001\u0000\u0000"+
		"\u0000\u0902\u08fc\u0001\u0000\u0000\u0000\u0903\u019d\u0001\u0000\u0000"+
		"\u0000\u0904\u0905\u0003<\u001e\u0000\u0905\u0906\u0005K\u0000\u0000\u0906"+
		"\u0907\u0003\u01ba\u00dd\u0000\u0907\u0908\u0005L\u0000\u0000\u0908\u090f"+
		"\u0001\u0000\u0000\u0000\u0909\u090a\u0003\u0182\u00c1\u0000\u090a\u090b"+
		"\u0005K\u0000\u0000\u090b\u090c\u0003\u01ba\u00dd\u0000\u090c\u090d\u0005"+
		"L\u0000\u0000\u090d\u090f\u0001\u0000\u0000\u0000\u090e\u0904\u0001\u0000"+
		"\u0000\u0000\u090e\u0909\u0001\u0000\u0000\u0000\u090f\u0917\u0001\u0000"+
		"\u0000\u0000\u0910\u0911\u0003\u0180\u00c0\u0000\u0911\u0912\u0005K\u0000"+
		"\u0000\u0912\u0913\u0003\u01ba\u00dd\u0000\u0913\u0914\u0005L\u0000\u0000"+
		"\u0914\u0916\u0001\u0000\u0000\u0000\u0915\u0910\u0001\u0000\u0000\u0000"+
		"\u0916\u0919\u0001\u0000\u0000\u0000\u0917\u0915\u0001\u0000\u0000\u0000"+
		"\u0917\u0918\u0001\u0000\u0000\u0000\u0918\u019f\u0001\u0000\u0000\u0000"+
		"\u0919\u0917\u0001\u0000\u0000\u0000\u091a\u091b\u0003\u0188\u00c4\u0000"+
		"\u091b\u091c\u0005K\u0000\u0000\u091c\u091d\u0003\u01ba\u00dd\u0000\u091d"+
		"\u091e\u0005L\u0000\u0000\u091e\u0926\u0001\u0000\u0000\u0000\u091f\u0920"+
		"\u0003\u0186\u00c3\u0000\u0920\u0921\u0005K\u0000\u0000\u0921\u0922\u0003"+
		"\u01ba\u00dd\u0000\u0922\u0923\u0005L\u0000\u0000\u0923\u0925\u0001\u0000"+
		"\u0000\u0000\u0924\u091f\u0001\u0000\u0000\u0000\u0925\u0928\u0001\u0000"+
		"\u0000\u0000\u0926\u0924\u0001\u0000\u0000\u0000\u0926\u0927\u0001\u0000"+
		"\u0000\u0000\u0927\u01a1\u0001\u0000\u0000\u0000\u0928\u0926\u0001\u0000"+
		"\u0000\u0000\u0929\u092a\u0003<\u001e\u0000\u092a\u092b\u0005K\u0000\u0000"+
		"\u092b\u092c\u0003\u01ba\u00dd\u0000\u092c\u092d\u0005L\u0000\u0000\u092d"+
		"\u0934\u0001\u0000\u0000\u0000\u092e\u092f\u0003\u018e\u00c7\u0000\u092f"+
		"\u0930\u0005K\u0000\u0000\u0930\u0931\u0003\u01ba\u00dd\u0000\u0931\u0932"+
		"\u0005L\u0000\u0000\u0932\u0934\u0001\u0000\u0000\u0000\u0933\u0929\u0001"+
		"\u0000\u0000\u0000\u0933\u092e\u0001\u0000\u0000\u0000\u0934\u093c\u0001"+
		"\u0000\u0000\u0000\u0935\u0936\u0003\u018c\u00c6\u0000\u0936\u0937\u0005"+
		"K\u0000\u0000\u0937\u0938\u0003\u01ba\u00dd\u0000\u0938\u0939\u0005L\u0000"+
		"\u0000\u0939\u093b\u0001\u0000\u0000\u0000\u093a\u0935\u0001\u0000\u0000"+
		"\u0000\u093b\u093e\u0001\u0000\u0000\u0000\u093c\u093a\u0001\u0000\u0000"+
		"\u0000\u093c\u093d\u0001\u0000\u0000\u0000\u093d\u01a3\u0001\u0000\u0000"+
		"\u0000\u093e\u093c\u0001\u0000\u0000\u0000\u093f\u0940\u0003>\u001f\u0000"+
		"\u0940\u0942\u0005G\u0000\u0000\u0941\u0943\u0003\u01aa\u00d5\u0000\u0942"+
		"\u0941\u0001\u0000\u0000\u0000\u0942\u0943\u0001\u0000\u0000\u0000\u0943"+
		"\u0944\u0001\u0000\u0000\u0000\u0944\u0945\u0005H\u0000\u0000\u0945\u0985"+
		"\u0001\u0000\u0000\u0000\u0946\u0947\u00038\u001c\u0000\u0947\u0949\u0005"+
		"O\u0000\u0000\u0948\u094a\u0003,\u0016\u0000\u0949\u0948\u0001\u0000\u0000"+
		"\u0000\u0949\u094a\u0001\u0000\u0000\u0000\u094a\u094b\u0001\u0000\u0000"+
		"\u0000\u094b\u094c\u0003\u0000\u0000\u0000\u094c\u094e\u0005G\u0000\u0000"+
		"\u094d\u094f\u0003\u01aa\u00d5\u0000\u094e\u094d\u0001\u0000\u0000\u0000"+
		"\u094e\u094f\u0001\u0000\u0000\u0000\u094f\u0950\u0001\u0000\u0000\u0000"+
		"\u0950\u0951\u0005H\u0000\u0000\u0951\u0985\u0001\u0000\u0000\u0000\u0952"+
		"\u0953\u0003<\u001e\u0000\u0953\u0955\u0005O\u0000\u0000\u0954\u0956\u0003"+
		",\u0016\u0000\u0955\u0954\u0001\u0000\u0000\u0000\u0955\u0956\u0001\u0000"+
		"\u0000\u0000\u0956\u0957\u0001\u0000\u0000\u0000\u0957\u0958\u0003\u0000"+
		"\u0000\u0000\u0958\u095a\u0005G\u0000\u0000\u0959\u095b\u0003\u01aa\u00d5"+
		"\u0000\u095a\u0959\u0001\u0000\u0000\u0000\u095a\u095b\u0001\u0000\u0000"+
		"\u0000\u095b\u095c\u0001\u0000\u0000\u0000\u095c\u095d\u0005H\u0000\u0000"+
		"\u095d\u0985\u0001\u0000\u0000\u0000\u095e\u095f\u0003";
	private static final String _serializedATNSegment1 =
		"\u017c\u00be\u0000\u095f\u0961\u0005O\u0000\u0000\u0960\u0962\u0003,\u0016"+
		"\u0000\u0961\u0960\u0001\u0000\u0000\u0000\u0961\u0962\u0001\u0000\u0000"+
		"\u0000\u0962\u0963\u0001\u0000\u0000\u0000\u0963\u0964\u0003\u0000\u0000"+
		"\u0000\u0964\u0966\u0005G\u0000\u0000\u0965\u0967\u0003\u01aa\u00d5\u0000"+
		"\u0966\u0965\u0001\u0000\u0000\u0000\u0966\u0967\u0001\u0000\u0000\u0000"+
		"\u0967\u0968\u0001\u0000\u0000\u0000\u0968\u0969\u0005H\u0000\u0000\u0969"+
		"\u0985\u0001\u0000\u0000\u0000\u096a\u096b\u00056\u0000\u0000\u096b\u096d"+
		"\u0005O\u0000\u0000\u096c\u096e\u0003,\u0016\u0000\u096d\u096c\u0001\u0000"+
		"\u0000\u0000\u096d\u096e\u0001\u0000\u0000\u0000\u096e\u096f\u0001\u0000"+
		"\u0000\u0000\u096f\u0970\u0003\u0000\u0000\u0000\u0970\u0972\u0005G\u0000"+
		"\u0000\u0971\u0973\u0003\u01aa\u00d5\u0000\u0972\u0971\u0001\u0000\u0000"+
		"\u0000\u0972\u0973\u0001\u0000\u0000\u0000\u0973\u0974\u0001\u0000\u0000"+
		"\u0000\u0974\u0975\u0005H\u0000\u0000\u0975\u0985\u0001\u0000\u0000\u0000"+
		"\u0976\u0977\u00038\u001c\u0000\u0977\u0978\u0005O\u0000\u0000\u0978\u0979"+
		"\u00056\u0000\u0000\u0979\u097b\u0005O\u0000\u0000\u097a\u097c\u0003,"+
		"\u0016\u0000\u097b\u097a\u0001\u0000\u0000\u0000\u097b\u097c\u0001\u0000"+
		"\u0000\u0000\u097c\u097d\u0001\u0000\u0000\u0000\u097d\u097e\u0003\u0000"+
		"\u0000\u0000\u097e\u0980\u0005G\u0000\u0000\u097f\u0981\u0003\u01aa\u00d5"+
		"\u0000\u0980\u097f\u0001\u0000\u0000\u0000\u0980\u0981\u0001\u0000\u0000"+
		"\u0000\u0981\u0982\u0001\u0000\u0000\u0000\u0982\u0983\u0005H\u0000\u0000"+
		"\u0983\u0985\u0001\u0000\u0000\u0000\u0984\u093f\u0001\u0000\u0000\u0000"+
		"\u0984\u0946\u0001\u0000\u0000\u0000\u0984\u0952\u0001\u0000\u0000\u0000"+
		"\u0984\u095e\u0001\u0000\u0000\u0000\u0984\u096a\u0001\u0000\u0000\u0000"+
		"\u0984\u0976\u0001\u0000\u0000\u0000\u0985\u01a5\u0001\u0000\u0000\u0000"+
		"\u0986\u0988\u0005O\u0000\u0000\u0987\u0989\u0003,\u0016\u0000\u0988\u0987"+
		"\u0001\u0000\u0000\u0000\u0988\u0989\u0001\u0000\u0000\u0000\u0989\u098a"+
		"\u0001\u0000\u0000\u0000\u098a\u098b\u0003\u0000\u0000\u0000\u098b\u098d"+
		"\u0005G\u0000\u0000\u098c\u098e\u0003\u01aa\u00d5\u0000\u098d\u098c\u0001"+
		"\u0000\u0000\u0000\u098d\u098e\u0001\u0000\u0000\u0000\u098e\u098f\u0001"+
		"\u0000\u0000\u0000\u098f\u0990\u0005H\u0000\u0000\u0990\u01a7\u0001\u0000"+
		"\u0000\u0000\u0991\u0992\u0003>\u001f\u0000\u0992\u0994\u0005G\u0000\u0000"+
		"\u0993\u0995\u0003\u01aa\u00d5\u0000\u0994\u0993\u0001\u0000\u0000\u0000"+
		"\u0994\u0995\u0001\u0000\u0000\u0000\u0995\u0996\u0001\u0000\u0000\u0000"+
		"\u0996\u0997\u0005H\u0000\u0000\u0997\u09cb\u0001\u0000\u0000\u0000\u0998"+
		"\u0999\u00038\u001c\u0000\u0999\u099b\u0005O\u0000\u0000\u099a\u099c\u0003"+
		",\u0016\u0000\u099b\u099a\u0001\u0000\u0000\u0000\u099b\u099c\u0001\u0000"+
		"\u0000\u0000\u099c\u099d\u0001\u0000\u0000\u0000\u099d\u099e\u0003\u0000"+
		"\u0000\u0000\u099e\u09a0\u0005G\u0000\u0000\u099f\u09a1\u0003\u01aa\u00d5"+
		"\u0000\u09a0\u099f\u0001\u0000\u0000\u0000\u09a0\u09a1\u0001\u0000\u0000"+
		"\u0000\u09a1\u09a2\u0001\u0000\u0000\u0000\u09a2\u09a3\u0005H\u0000\u0000"+
		"\u09a3\u09cb\u0001\u0000\u0000\u0000\u09a4\u09a5\u0003<\u001e\u0000\u09a5"+
		"\u09a7\u0005O\u0000\u0000\u09a6\u09a8\u0003,\u0016\u0000\u09a7\u09a6\u0001"+
		"\u0000\u0000\u0000\u09a7\u09a8\u0001\u0000\u0000\u0000\u09a8\u09a9\u0001"+
		"\u0000\u0000\u0000\u09a9\u09aa\u0003\u0000\u0000\u0000\u09aa\u09ac\u0005"+
		"G\u0000\u0000\u09ab\u09ad\u0003\u01aa\u00d5\u0000\u09ac\u09ab\u0001\u0000"+
		"\u0000\u0000\u09ac\u09ad\u0001\u0000\u0000\u0000\u09ad\u09ae\u0001\u0000"+
		"\u0000\u0000\u09ae\u09af\u0005H\u0000\u0000\u09af\u09cb\u0001\u0000\u0000"+
		"\u0000\u09b0\u09b1\u00056\u0000\u0000\u09b1\u09b3\u0005O\u0000\u0000\u09b2"+
		"\u09b4\u0003,\u0016\u0000\u09b3\u09b2\u0001\u0000\u0000\u0000\u09b3\u09b4"+
		"\u0001\u0000\u0000\u0000\u09b4\u09b5\u0001\u0000\u0000\u0000\u09b5\u09b6"+
		"\u0003\u0000\u0000\u0000\u09b6\u09b8\u0005G\u0000\u0000\u09b7\u09b9\u0003"+
		"\u01aa\u00d5\u0000\u09b8\u09b7\u0001\u0000\u0000\u0000\u09b8\u09b9\u0001"+
		"\u0000\u0000\u0000\u09b9\u09ba\u0001\u0000\u0000\u0000\u09ba\u09bb\u0005"+
		"H\u0000\u0000\u09bb\u09cb\u0001\u0000\u0000\u0000\u09bc\u09bd\u00038\u001c"+
		"\u0000\u09bd\u09be\u0005O\u0000\u0000\u09be\u09bf\u00056\u0000\u0000\u09bf"+
		"\u09c1\u0005O\u0000\u0000\u09c0\u09c2\u0003,\u0016\u0000\u09c1\u09c0\u0001"+
		"\u0000\u0000\u0000\u09c1\u09c2\u0001\u0000\u0000\u0000\u09c2\u09c3\u0001"+
		"\u0000\u0000\u0000\u09c3\u09c4\u0003\u0000\u0000\u0000\u09c4\u09c6\u0005"+
		"G\u0000\u0000\u09c5\u09c7\u0003\u01aa\u00d5\u0000\u09c6\u09c5\u0001\u0000"+
		"\u0000\u0000\u09c6\u09c7\u0001\u0000\u0000\u0000\u09c7\u09c8\u0001\u0000"+
		"\u0000\u0000\u09c8\u09c9\u0005H\u0000\u0000\u09c9\u09cb\u0001\u0000\u0000"+
		"\u0000\u09ca\u0991\u0001\u0000\u0000\u0000\u09ca\u0998\u0001\u0000\u0000"+
		"\u0000\u09ca\u09a4\u0001\u0000\u0000\u0000\u09ca\u09b0\u0001\u0000\u0000"+
		"\u0000\u09ca\u09bc\u0001\u0000\u0000\u0000\u09cb\u01a9\u0001\u0000\u0000"+
		"\u0000\u09cc\u09d1\u0003\u01ba\u00dd\u0000\u09cd\u09ce\u0005N\u0000\u0000"+
		"\u09ce\u09d0\u0003\u01ba\u00dd\u0000\u09cf\u09cd\u0001\u0000\u0000\u0000"+
		"\u09d0\u09d3\u0001\u0000\u0000\u0000\u09d1\u09cf\u0001\u0000\u0000\u0000"+
		"\u09d1\u09d2\u0001\u0000\u0000\u0000\u09d2\u01ab\u0001\u0000\u0000\u0000"+
		"\u09d3\u09d1\u0001\u0000\u0000\u0000\u09d4\u09d5\u0003<\u001e\u0000\u09d5"+
		"\u09d7\u0005g\u0000\u0000\u09d6\u09d8\u0003,\u0016\u0000\u09d7\u09d6\u0001"+
		"\u0000\u0000\u0000\u09d7\u09d8\u0001\u0000\u0000\u0000\u09d8\u09d9\u0001"+
		"\u0000\u0000\u0000\u09d9\u09da\u0003\u0000\u0000\u0000\u09da\u0a04\u0001"+
		"\u0000\u0000\u0000\u09db\u09dc\u0003\u000e\u0007\u0000\u09dc\u09de\u0005"+
		"g\u0000\u0000\u09dd\u09df\u0003,\u0016\u0000\u09de\u09dd\u0001\u0000\u0000"+
		"\u0000\u09de\u09df\u0001\u0000\u0000\u0000\u09df\u09e0\u0001\u0000\u0000"+
		"\u0000\u09e0\u09e1\u0003\u0000\u0000\u0000\u09e1\u0a04\u0001\u0000\u0000"+
		"\u0000\u09e2\u09e3\u0003\u017c\u00be\u0000\u09e3\u09e5\u0005g\u0000\u0000"+
		"\u09e4\u09e6\u0003,\u0016\u0000\u09e5\u09e4\u0001\u0000\u0000\u0000\u09e5"+
		"\u09e6\u0001\u0000\u0000\u0000\u09e6\u09e7\u0001\u0000\u0000\u0000\u09e7"+
		"\u09e8\u0003\u0000\u0000\u0000\u09e8\u0a04\u0001\u0000\u0000\u0000\u09e9"+
		"\u09ea\u00056\u0000\u0000\u09ea\u09ec\u0005g\u0000\u0000\u09eb\u09ed\u0003"+
		",\u0016\u0000\u09ec\u09eb\u0001\u0000\u0000\u0000\u09ec\u09ed\u0001\u0000"+
		"\u0000\u0000\u09ed\u09ee\u0001\u0000\u0000\u0000\u09ee\u0a04\u0003\u0000"+
		"\u0000\u0000\u09ef\u09f0\u00038\u001c\u0000\u09f0\u09f1\u0005O\u0000\u0000"+
		"\u09f1\u09f2\u00056\u0000\u0000\u09f2\u09f4\u0005g\u0000\u0000\u09f3\u09f5"+
		"\u0003,\u0016\u0000\u09f4\u09f3\u0001\u0000\u0000\u0000\u09f4\u09f5\u0001"+
		"\u0000\u0000\u0000\u09f5\u09f6\u0001\u0000\u0000\u0000\u09f6\u09f7\u0003"+
		"\u0000\u0000\u0000\u09f7\u0a04\u0001\u0000\u0000\u0000\u09f8\u09f9\u0003"+
		"\u0012\t\u0000\u09f9\u09fb\u0005g\u0000\u0000\u09fa\u09fc\u0003,\u0016"+
		"\u0000\u09fb\u09fa\u0001\u0000\u0000\u0000\u09fb\u09fc\u0001\u0000\u0000"+
		"\u0000\u09fc\u09fd\u0001\u0000\u0000\u0000\u09fd\u09fe\u0005-\u0000\u0000"+
		"\u09fe\u0a04\u0001\u0000\u0000\u0000\u09ff\u0a00\u0003 \u0010\u0000\u0a00"+
		"\u0a01\u0005g\u0000\u0000\u0a01\u0a02\u0005-\u0000\u0000\u0a02\u0a04\u0001"+
		"\u0000\u0000\u0000\u0a03\u09d4\u0001\u0000\u0000\u0000\u0a03\u09db\u0001"+
		"\u0000\u0000\u0000\u0a03\u09e2\u0001\u0000\u0000\u0000\u0a03\u09e9\u0001"+
		"\u0000\u0000\u0000\u0a03\u09ef\u0001\u0000\u0000\u0000\u0a03\u09f8\u0001"+
		"\u0000\u0000\u0000\u0a03\u09ff\u0001\u0000\u0000\u0000\u0a04\u01ad\u0001"+
		"\u0000\u0000\u0000\u0a05\u0a07\u0005g\u0000\u0000\u0a06\u0a08\u0003,\u0016"+
		"\u0000\u0a07\u0a06\u0001\u0000\u0000\u0000\u0a07\u0a08\u0001\u0000\u0000"+
		"\u0000\u0a08\u0a09\u0001\u0000\u0000\u0000\u0a09\u0a0a\u0003\u0000\u0000"+
		"\u0000\u0a0a\u01af\u0001\u0000\u0000\u0000\u0a0b\u0a0c\u0003<\u001e\u0000"+
		"\u0a0c\u0a0e\u0005g\u0000\u0000\u0a0d\u0a0f\u0003,\u0016\u0000\u0a0e\u0a0d"+
		"\u0001\u0000\u0000\u0000\u0a0e\u0a0f\u0001\u0000\u0000\u0000\u0a0f\u0a10"+
		"\u0001\u0000\u0000\u0000\u0a10\u0a11\u0003\u0000\u0000\u0000\u0a11\u0a34"+
		"\u0001\u0000\u0000\u0000\u0a12\u0a13\u0003\u000e\u0007\u0000\u0a13\u0a15"+
		"\u0005g\u0000\u0000\u0a14\u0a16\u0003,\u0016\u0000\u0a15\u0a14\u0001\u0000"+
		"\u0000\u0000\u0a15\u0a16\u0001\u0000\u0000\u0000\u0a16\u0a17\u0001\u0000"+
		"\u0000\u0000\u0a17\u0a18\u0003\u0000\u0000\u0000\u0a18\u0a34\u0001\u0000"+
		"\u0000\u0000\u0a19\u0a1a\u00056\u0000\u0000\u0a1a\u0a1c\u0005g\u0000\u0000"+
		"\u0a1b\u0a1d\u0003,\u0016\u0000\u0a1c\u0a1b\u0001\u0000\u0000\u0000\u0a1c"+
		"\u0a1d\u0001\u0000\u0000\u0000\u0a1d\u0a1e\u0001\u0000\u0000\u0000\u0a1e"+
		"\u0a34\u0003\u0000\u0000\u0000\u0a1f\u0a20\u00038\u001c\u0000\u0a20\u0a21"+
		"\u0005O\u0000\u0000\u0a21\u0a22\u00056\u0000\u0000\u0a22\u0a24\u0005g"+
		"\u0000\u0000\u0a23\u0a25\u0003,\u0016\u0000\u0a24\u0a23\u0001\u0000\u0000"+
		"\u0000\u0a24\u0a25\u0001\u0000\u0000\u0000\u0a25\u0a26\u0001\u0000\u0000"+
		"\u0000\u0a26\u0a27\u0003\u0000\u0000\u0000\u0a27\u0a34\u0001\u0000\u0000"+
		"\u0000\u0a28\u0a29\u0003\u0012\t\u0000\u0a29\u0a2b\u0005g\u0000\u0000"+
		"\u0a2a\u0a2c\u0003,\u0016\u0000\u0a2b\u0a2a\u0001\u0000\u0000\u0000\u0a2b"+
		"\u0a2c\u0001\u0000\u0000\u0000\u0a2c\u0a2d\u0001\u0000\u0000\u0000\u0a2d"+
		"\u0a2e\u0005-\u0000\u0000\u0a2e\u0a34\u0001\u0000\u0000\u0000\u0a2f\u0a30"+
		"\u0003 \u0010\u0000\u0a30\u0a31\u0005g\u0000\u0000\u0a31\u0a32\u0005-"+
		"\u0000\u0000\u0a32\u0a34\u0001\u0000\u0000\u0000\u0a33\u0a0b\u0001\u0000"+
		"\u0000\u0000\u0a33\u0a12\u0001\u0000\u0000\u0000\u0a33\u0a19\u0001\u0000"+
		"\u0000\u0000\u0a33\u0a1f\u0001\u0000\u0000\u0000\u0a33\u0a28\u0001\u0000"+
		"\u0000\u0000\u0a33\u0a2f\u0001\u0000\u0000\u0000\u0a34\u01b1\u0001\u0000"+
		"\u0000\u0000\u0a35\u0a36\u0005-\u0000\u0000\u0a36\u0a37\u0003\u0006\u0003"+
		"\u0000\u0a37\u0a39\u0003\u01b4\u00da\u0000\u0a38\u0a3a\u0003\"\u0011\u0000"+
		"\u0a39\u0a38\u0001\u0000\u0000\u0000\u0a39\u0a3a\u0001\u0000\u0000\u0000"+
		"\u0a3a\u0a4c\u0001\u0000\u0000\u0000\u0a3b\u0a3c\u0005-\u0000\u0000\u0a3c"+
		"\u0a3d\u0003\u0010\b\u0000\u0a3d\u0a3f\u0003\u01b4\u00da\u0000\u0a3e\u0a40"+
		"\u0003\"\u0011\u0000\u0a3f\u0a3e\u0001\u0000\u0000\u0000\u0a3f\u0a40\u0001"+
		"\u0000\u0000\u0000\u0a40\u0a4c\u0001\u0000\u0000\u0000\u0a41\u0a42\u0005"+
		"-\u0000\u0000\u0a42\u0a43\u0003\u0006\u0003\u0000\u0a43\u0a44\u0003\""+
		"\u0011\u0000\u0a44\u0a45\u0003\u0110\u0088\u0000\u0a45\u0a4c\u0001\u0000"+
		"\u0000\u0000\u0a46\u0a47\u0005-\u0000\u0000\u0a47\u0a48\u0003\u0010\b"+
		"\u0000\u0a48\u0a49\u0003\"\u0011\u0000\u0a49\u0a4a\u0003\u0110\u0088\u0000"+
		"\u0a4a\u0a4c\u0001\u0000\u0000\u0000\u0a4b\u0a35\u0001\u0000\u0000\u0000"+
		"\u0a4b\u0a3b\u0001\u0000\u0000\u0000\u0a4b\u0a41\u0001\u0000\u0000\u0000"+
		"\u0a4b\u0a46\u0001\u0000\u0000\u0000\u0a4c\u01b3\u0001\u0000\u0000\u0000"+
		"\u0a4d\u0a51\u0003\u01b6\u00db\u0000\u0a4e\u0a50\u0003\u01b6\u00db\u0000"+
		"\u0a4f\u0a4e\u0001\u0000\u0000\u0000\u0a50\u0a53\u0001\u0000\u0000\u0000"+
		"\u0a51\u0a4f\u0001\u0000\u0000\u0000\u0a51\u0a52\u0001\u0000\u0000\u0000"+
		"\u0a52\u01b5\u0001\u0000\u0000\u0000\u0a53\u0a51\u0001\u0000\u0000\u0000"+
		"\u0a54\u0a56\u0003\u00fa}\u0000\u0a55\u0a54\u0001\u0000\u0000\u0000\u0a56"+
		"\u0a59\u0001\u0000\u0000\u0000\u0a57\u0a55\u0001\u0000\u0000\u0000\u0a57"+
		"\u0a58\u0001\u0000\u0000\u0000\u0a58\u0a5a\u0001\u0000\u0000\u0000\u0a59"+
		"\u0a57\u0001\u0000\u0000\u0000\u0a5a\u0a5b\u0005K\u0000\u0000\u0a5b\u0a5c"+
		"\u0003\u01ba\u00dd\u0000\u0a5c\u0a5d\u0005L\u0000\u0000\u0a5d\u01b7\u0001"+
		"\u0000\u0000\u0000\u0a5e\u0a5f\u0003\u01ba\u00dd\u0000\u0a5f\u01b9\u0001"+
		"\u0000\u0000\u0000\u0a60\u0a63\u0003\u01bc\u00de\u0000\u0a61\u0a63\u0003"+
		"\u01c4\u00e2\u0000\u0a62\u0a60\u0001\u0000\u0000\u0000\u0a62\u0a61\u0001"+
		"\u0000\u0000\u0000\u0a63\u01bb\u0001\u0000\u0000\u0000\u0a64\u0a65\u0003"+
		"\u01be\u00df\u0000\u0a65\u0a66\u0005f\u0000\u0000\u0a66\u0a67\u0003\u01c2"+
		"\u00e1\u0000\u0a67\u01bd\u0001\u0000\u0000\u0000\u0a68\u0a73\u0003\u0000"+
		"\u0000\u0000\u0a69\u0a6b\u0005G\u0000\u0000\u0a6a\u0a6c\u0003\u00a6S\u0000"+
		"\u0a6b\u0a6a\u0001\u0000\u0000\u0000\u0a6b\u0a6c\u0001\u0000\u0000\u0000"+
		"\u0a6c\u0a6d\u0001\u0000\u0000\u0000\u0a6d\u0a73\u0005H\u0000\u0000\u0a6e"+
		"\u0a6f\u0005G\u0000\u0000\u0a6f\u0a70\u0003\u01c0\u00e0\u0000\u0a70\u0a71"+
		"\u0005H\u0000\u0000\u0a71\u0a73\u0001\u0000\u0000\u0000\u0a72\u0a68\u0001"+
		"\u0000\u0000\u0000\u0a72\u0a69\u0001\u0000\u0000\u0000\u0a72\u0a6e\u0001"+
		"\u0000\u0000\u0000\u0a73\u01bf\u0001\u0000\u0000\u0000\u0a74\u0a79\u0003"+
		"\u0000\u0000\u0000\u0a75\u0a76\u0005N\u0000\u0000\u0a76\u0a78\u0003\u0000"+
		"\u0000\u0000\u0a77\u0a75\u0001\u0000\u0000\u0000\u0a78\u0a7b\u0001\u0000"+
		"\u0000\u0000\u0a79\u0a77\u0001\u0000\u0000\u0000\u0a79\u0a7a\u0001\u0000"+
		"\u0000\u0000\u0a7a\u01c1\u0001\u0000\u0000\u0000\u0a7b\u0a79\u0001\u0000"+
		"\u0000\u0000\u0a7c\u0a7f\u0003\u01ba\u00dd\u0000\u0a7d\u0a7f\u0003\u0114"+
		"\u008a\u0000\u0a7e\u0a7c\u0001\u0000\u0000\u0000\u0a7e\u0a7d\u0001\u0000"+
		"\u0000\u0000\u0a7f\u01c3\u0001\u0000\u0000\u0000\u0a80\u0a83\u0003\u01d4"+
		"\u00ea\u0000\u0a81\u0a83\u0003\u01c6\u00e3\u0000\u0a82\u0a80\u0001\u0000"+
		"\u0000\u0000\u0a82\u0a81\u0001\u0000\u0000\u0000\u0a83\u01c5\u0001\u0000"+
		"\u0000\u0000\u0a84\u0a85\u0003\u01c8\u00e4\u0000\u0a85\u0a86\u0003\u01ca"+
		"\u00e5\u0000\u0a86\u0a87\u0003\u01ba\u00dd\u0000\u0a87\u01c7\u0001\u0000"+
		"\u0000\u0000\u0a88\u0a8b\u0003\u017a\u00bd\u0000\u0a89\u0a8b\u0003\u019e"+
		"\u00cf\u0000\u0a8a\u0a88\u0001\u0000\u0000\u0000\u0a8a\u0a89\u0001\u0000"+
		"\u0000\u0000\u0a8b\u01c9\u0001\u0000\u0000\u0000\u0a8c\u0a8d\u0007\u0005"+
		"\u0000\u0000\u0a8d\u01cb\u0001\u0000\u0000\u0000\u0a8e\u0a8f\u0007\u0006"+
		"\u0000\u0000\u0a8f\u01cd\u0001\u0000\u0000\u0000\u0a90\u0a91\u0007\u0007"+
		"\u0000\u0000\u0a91\u01cf\u0001\u0000\u0000\u0000\u0a92\u0a93\u0007\b\u0000"+
		"\u0000\u0a93\u01d1\u0001\u0000\u0000\u0000\u0a94\u0a95\u0005\u000e\u0000"+
		"\u0000\u0a95\u01d3\u0001\u0000\u0000\u0000\u0a96\u0aa4\u0003\u01d6\u00eb"+
		"\u0000\u0a97\u0a98\u0003\u01d6\u00eb\u0000\u0a98\u0a99\u0005U\u0000\u0000"+
		"\u0a99\u0a9a\u0003\u01ba\u00dd\u0000\u0a9a\u0a9b\u0005V\u0000\u0000\u0a9b"+
		"\u0a9c\u0003\u01d4\u00ea\u0000\u0a9c\u0aa4\u0001\u0000\u0000\u0000\u0a9d"+
		"\u0a9e\u0003\u01d6\u00eb\u0000\u0a9e\u0a9f\u0005U\u0000\u0000\u0a9f\u0aa0"+
		"\u0003\u01ba\u00dd\u0000\u0aa0\u0aa1\u0005V\u0000\u0000\u0aa1\u0aa2\u0003"+
		"\u01bc\u00de\u0000\u0aa2\u0aa4\u0001\u0000\u0000\u0000\u0aa3\u0a96\u0001"+
		"\u0000\u0000\u0000\u0aa3\u0a97\u0001\u0000\u0000\u0000\u0aa3\u0a9d\u0001"+
		"\u0000\u0000\u0000\u0aa4\u01d5\u0001\u0000\u0000\u0000\u0aa5\u0aa6\u0006"+
		"\u00eb\uffff\uffff\u0000\u0aa6\u0aa7\u0003\u01d8\u00ec\u0000\u0aa7\u0aad"+
		"\u0001\u0000\u0000\u0000\u0aa8\u0aa9\n\u0001\u0000\u0000\u0aa9\u0aaa\u0005"+
		"\\\u0000\u0000\u0aaa\u0aac\u0003\u01d8\u00ec\u0000\u0aab\u0aa8\u0001\u0000"+
		"\u0000\u0000\u0aac\u0aaf\u0001\u0000\u0000\u0000\u0aad\u0aab\u0001\u0000"+
		"\u0000\u0000\u0aad\u0aae\u0001\u0000\u0000\u0000\u0aae\u01d7\u0001\u0000"+
		"\u0000\u0000\u0aaf\u0aad\u0001\u0000\u0000\u0000\u0ab0\u0ab1\u0006\u00ec"+
		"\uffff\uffff\u0000\u0ab1\u0ab2\u0003\u01da\u00ed\u0000\u0ab2\u0ab8\u0001"+
		"\u0000\u0000\u0000\u0ab3\u0ab4\n\u0001\u0000\u0000\u0ab4\u0ab5\u0005["+
		"\u0000\u0000\u0ab5\u0ab7\u0003\u01da\u00ed\u0000\u0ab6\u0ab3\u0001\u0000"+
		"\u0000\u0000\u0ab7\u0aba\u0001\u0000\u0000\u0000\u0ab8\u0ab6\u0001\u0000"+
		"\u0000\u0000\u0ab8\u0ab9\u0001\u0000\u0000\u0000\u0ab9\u01d9\u0001\u0000"+
		"\u0000\u0000\u0aba\u0ab8\u0001\u0000\u0000\u0000\u0abb\u0abc\u0006\u00ed"+
		"\uffff\uffff\u0000\u0abc\u0abd\u0003\u01dc\u00ee\u0000\u0abd\u0ac3\u0001"+
		"\u0000\u0000\u0000\u0abe\u0abf\n\u0001\u0000\u0000\u0abf\u0ac0\u0005c"+
		"\u0000\u0000\u0ac0\u0ac2\u0003\u01dc\u00ee\u0000\u0ac1\u0abe\u0001\u0000"+
		"\u0000\u0000\u0ac2\u0ac5\u0001\u0000\u0000\u0000\u0ac3\u0ac1\u0001\u0000"+
		"\u0000\u0000\u0ac3\u0ac4\u0001\u0000\u0000\u0000\u0ac4\u01db\u0001\u0000"+
		"\u0000\u0000\u0ac5\u0ac3\u0001\u0000\u0000\u0000\u0ac6\u0ac7\u0006\u00ee"+
		"\uffff\uffff\u0000\u0ac7\u0ac8\u0003\u01de\u00ef\u0000\u0ac8\u0ace\u0001"+
		"\u0000\u0000\u0000\u0ac9\u0aca\n\u0001\u0000\u0000\u0aca\u0acb\u0005d"+
		"\u0000\u0000\u0acb\u0acd\u0003\u01de\u00ef\u0000\u0acc\u0ac9\u0001\u0000"+
		"\u0000\u0000\u0acd\u0ad0\u0001\u0000\u0000\u0000\u0ace\u0acc\u0001\u0000"+
		"\u0000\u0000\u0ace\u0acf\u0001\u0000\u0000\u0000\u0acf\u01dd\u0001\u0000"+
		"\u0000\u0000\u0ad0\u0ace\u0001\u0000\u0000\u0000\u0ad1\u0ad2\u0006\u00ef"+
		"\uffff\uffff\u0000\u0ad2\u0ad3\u0003\u01e0\u00f0\u0000\u0ad3\u0ad9\u0001"+
		"\u0000\u0000\u0000\u0ad4\u0ad5\n\u0001\u0000\u0000\u0ad5\u0ad6\u0005b"+
		"\u0000\u0000\u0ad6\u0ad8\u0003\u01e0\u00f0\u0000\u0ad7\u0ad4\u0001\u0000"+
		"\u0000\u0000\u0ad8\u0adb\u0001\u0000\u0000\u0000\u0ad9\u0ad7\u0001\u0000"+
		"\u0000\u0000\u0ad9\u0ada\u0001\u0000\u0000\u0000\u0ada\u01df\u0001\u0000"+
		"\u0000\u0000\u0adb\u0ad9\u0001\u0000\u0000\u0000\u0adc\u0add\u0006\u00f0"+
		"\uffff\uffff\u0000\u0add\u0ade\u0003\u01e2\u00f1\u0000\u0ade\u0ae7\u0001"+
		"\u0000\u0000\u0000\u0adf\u0ae0\n\u0002\u0000\u0000\u0ae0\u0ae1\u0005W"+
		"\u0000\u0000\u0ae1\u0ae6\u0003\u01e2\u00f1\u0000\u0ae2\u0ae3\n\u0001\u0000"+
		"\u0000\u0ae3\u0ae4\u0005Z\u0000\u0000\u0ae4\u0ae6\u0003\u01e2\u00f1\u0000"+
		"\u0ae5\u0adf\u0001\u0000\u0000\u0000\u0ae5\u0ae2\u0001\u0000\u0000\u0000"+
		"\u0ae6\u0ae9\u0001\u0000\u0000\u0000\u0ae7\u0ae5\u0001\u0000\u0000\u0000"+
		"\u0ae7\u0ae8\u0001\u0000\u0000\u0000\u0ae8\u01e1\u0001\u0000\u0000\u0000"+
		"\u0ae9\u0ae7\u0001\u0000\u0000\u0000\u0aea\u0aeb\u0006\u00f1\uffff\uffff"+
		"\u0000\u0aeb\u0aec\u0003\u01e4\u00f2\u0000\u0aec\u0b03\u0001\u0000\u0000"+
		"\u0000\u0aed\u0aee\n\u0005\u0000\u0000\u0aee\u0aef\u0003\u01ce\u00e7\u0000"+
		"\u0aef\u0af0\u0003\u01e4\u00f2\u0000\u0af0\u0b02\u0001\u0000\u0000\u0000"+
		"\u0af1\u0af2\n\u0004\u0000\u0000\u0af2\u0af3\u0003\u01ce\u00e7\u0000\u0af3"+
		"\u0af4\u0003\u01e4\u00f2\u0000\u0af4\u0b02\u0001\u0000\u0000\u0000\u0af5"+
		"\u0af6\n\u0003\u0000\u0000\u0af6\u0af7\u0003\u01ce\u00e7\u0000\u0af7\u0af8"+
		"\u0003\u01e4\u00f2\u0000\u0af8\u0b02\u0001\u0000\u0000\u0000\u0af9\u0afa"+
		"\n\u0002\u0000\u0000\u0afa\u0afb\u0003\u01ce\u00e7\u0000\u0afb\u0afc\u0003"+
		"\u01e4\u00f2\u0000\u0afc\u0b02\u0001\u0000\u0000\u0000\u0afd\u0afe\n\u0001"+
		"\u0000\u0000\u0afe\u0aff\u0003\u01ce\u00e7\u0000\u0aff\u0b00\u0003\u000e"+
		"\u0007\u0000\u0b00\u0b02\u0001\u0000\u0000\u0000\u0b01\u0aed\u0001\u0000"+
		"\u0000\u0000\u0b01\u0af1\u0001\u0000\u0000\u0000\u0b01\u0af5\u0001\u0000"+
		"\u0000\u0000\u0b01\u0af9\u0001\u0000\u0000\u0000\u0b01\u0afd\u0001\u0000"+
		"\u0000\u0000\u0b02\u0b05\u0001\u0000\u0000\u0000\u0b03\u0b01\u0001\u0000"+
		"\u0000\u0000\u0b03\u0b04\u0001\u0000\u0000\u0000\u0b04\u01e3\u0001\u0000"+
		"\u0000\u0000\u0b05\u0b03\u0001\u0000\u0000\u0000\u0b06\u0b07\u0006\u00f2"+
		"\uffff\uffff\u0000\u0b07\u0b08\u0003\u01e8\u00f4\u0000\u0b08\u0b17\u0001"+
		"\u0000\u0000\u0000\u0b09\u0b0a\n\u0003\u0000\u0000\u0b0a\u0b0b\u0003\u01e6"+
		"\u00f3\u0000\u0b0b\u0b0c\u0003\u01e8\u00f4\u0000\u0b0c\u0b16\u0001\u0000"+
		"\u0000\u0000\u0b0d\u0b0e\n\u0002\u0000\u0000\u0b0e\u0b0f\u0003\u01e6\u00f3"+
		"\u0000\u0b0f\u0b10\u0003\u01e8\u00f4\u0000\u0b10\u0b16\u0001\u0000\u0000"+
		"\u0000\u0b11\u0b12\n\u0001\u0000\u0000\u0b12\u0b13\u0003\u01e6\u00f3\u0000"+
		"\u0b13\u0b14\u0003\u01e8\u00f4\u0000\u0b14\u0b16\u0001\u0000\u0000\u0000"+
		"\u0b15\u0b09\u0001\u0000\u0000\u0000\u0b15\u0b0d\u0001\u0000\u0000\u0000"+
		"\u0b15\u0b11\u0001\u0000\u0000\u0000\u0b16\u0b19\u0001\u0000\u0000\u0000"+
		"\u0b17\u0b15\u0001\u0000\u0000\u0000\u0b17\u0b18\u0001\u0000\u0000\u0000"+
		"\u0b18\u01e5\u0001\u0000\u0000\u0000\u0b19\u0b17\u0001\u0000\u0000\u0000"+
		"\u0b1a\u0b1b\u0005R\u0000\u0000\u0b1b\u0b22\u0005R\u0000\u0000\u0b1c\u0b1d"+
		"\u0005Q\u0000\u0000\u0b1d\u0b22\u0005Q\u0000\u0000\u0b1e\u0b1f\u0005Q"+
		"\u0000\u0000\u0b1f\u0b20\u0005Q\u0000\u0000\u0b20\u0b22\u0005Q\u0000\u0000"+
		"\u0b21\u0b1a\u0001\u0000\u0000\u0000\u0b21\u0b1c\u0001\u0000\u0000\u0000"+
		"\u0b21\u0b1e\u0001\u0000\u0000\u0000\u0b22\u01e7\u0001\u0000\u0000\u0000"+
		"\u0b23\u0b24\u0006\u00f4\uffff\uffff\u0000\u0b24\u0b25\u0003\u01ea\u00f5"+
		"\u0000\u0b25\u0b30\u0001\u0000\u0000\u0000\u0b26\u0b27\n\u0002\u0000\u0000"+
		"\u0b27\u0b28\u0003\u01cc\u00e6\u0000\u0b28\u0b29\u0003\u01ea\u00f5\u0000"+
		"\u0b29\u0b2f\u0001\u0000\u0000\u0000\u0b2a\u0b2b\n\u0001\u0000\u0000\u0b2b"+
		"\u0b2c\u0003\u01cc\u00e6\u0000\u0b2c\u0b2d\u0003\u01ea\u00f5\u0000\u0b2d"+
		"\u0b2f\u0001\u0000\u0000\u0000\u0b2e\u0b26\u0001\u0000\u0000\u0000\u0b2e"+
		"\u0b2a\u0001\u0000\u0000\u0000\u0b2f\u0b32\u0001\u0000\u0000\u0000\u0b30"+
		"\u0b2e\u0001\u0000\u0000\u0000\u0b30\u0b31\u0001\u0000\u0000\u0000\u0b31"+
		"\u01e9\u0001\u0000\u0000\u0000\u0b32\u0b30\u0001\u0000\u0000\u0000\u0b33"+
		"\u0b34\u0006\u00f5\uffff\uffff\u0000\u0b34\u0b35\u0003\u01ec\u00f6\u0000"+
		"\u0b35\u0b44\u0001\u0000\u0000\u0000\u0b36\u0b37\n\u0003\u0000\u0000\u0b37"+
		"\u0b38\u0003\u01d0\u00e8\u0000\u0b38\u0b39\u0003\u01ec\u00f6\u0000\u0b39"+
		"\u0b43\u0001\u0000\u0000\u0000\u0b3a\u0b3b\n\u0002\u0000\u0000\u0b3b\u0b3c"+
		"\u0003\u01d0\u00e8\u0000\u0b3c\u0b3d\u0003\u01ec\u00f6\u0000\u0b3d\u0b43"+
		"\u0001\u0000\u0000\u0000\u0b3e\u0b3f\n\u0001\u0000\u0000\u0b3f\u0b40\u0003"+
		"\u01d0\u00e8\u0000\u0b40\u0b41\u0003\u01ec\u00f6\u0000\u0b41\u0b43\u0001"+
		"\u0000\u0000\u0000\u0b42\u0b36\u0001\u0000\u0000\u0000\u0b42\u0b3a\u0001"+
		"\u0000\u0000\u0000\u0b42\u0b3e\u0001\u0000\u0000\u0000\u0b43\u0b46\u0001"+
		"\u0000\u0000\u0000\u0b44\u0b42\u0001\u0000\u0000\u0000\u0b44\u0b45\u0001"+
		"\u0000\u0000\u0000\u0b45\u01eb\u0001\u0000\u0000\u0000\u0b46\u0b44\u0001"+
		"\u0000\u0000\u0000\u0b47\u0b51\u0003\u01ee\u00f7\u0000\u0b48\u0b51\u0003"+
		"\u01f0\u00f8\u0000\u0b49\u0b4a\u0003\u01cc\u00e6\u0000\u0b4a\u0b4b\u0003"+
		"\u01ec\u00f6\u0000\u0b4b\u0b51\u0001\u0000\u0000\u0000\u0b4c\u0b4d\u0003"+
		"\u01cc\u00e6\u0000\u0b4d\u0b4e\u0003\u01ec\u00f6\u0000\u0b4e\u0b51\u0001"+
		"\u0000\u0000\u0000\u0b4f\u0b51\u0003\u01f2\u00f9\u0000\u0b50\u0b47\u0001"+
		"\u0000\u0000\u0000\u0b50\u0b48\u0001\u0000\u0000\u0000\u0b50\u0b49\u0001"+
		"\u0000\u0000\u0000\u0b50\u0b4c\u0001\u0000\u0000\u0000\u0b50\u0b4f\u0001"+
		"\u0000\u0000\u0000\u0b51\u01ed\u0001\u0000\u0000\u0000\u0b52\u0b53\u0005"+
		"]\u0000\u0000\u0b53\u0b54\u0003\u01ec\u00f6\u0000\u0b54\u01ef\u0001\u0000"+
		"\u0000\u0000\u0b55\u0b56\u0005^\u0000\u0000\u0b56\u0b57\u0003\u01ec\u00f6"+
		"\u0000\u0b57\u01f1\u0001\u0000\u0000\u0000\u0b58\u0b5f\u0003\u01f4\u00fa"+
		"\u0000\u0b59\u0b5a\u0005T\u0000\u0000\u0b5a\u0b5f\u0003\u01ec\u00f6\u0000"+
		"\u0b5b\u0b5c\u0005S\u0000\u0000\u0b5c\u0b5f\u0003\u01ec\u00f6\u0000\u0b5d"+
		"\u0b5f\u0003\u01fe\u00ff\u0000\u0b5e\u0b58\u0001\u0000\u0000\u0000\u0b5e"+
		"\u0b59\u0001\u0000\u0000\u0000\u0b5e\u0b5b\u0001\u0000\u0000\u0000\u0b5e"+
		"\u0b5d\u0001\u0000\u0000\u0000\u0b5f\u01f3\u0001\u0000\u0000\u0000\u0b60"+
		"\u0b63\u0003\u017c\u00be\u0000\u0b61\u0b63\u0003<\u001e\u0000\u0b62\u0b60"+
		"\u0001\u0000\u0000\u0000\u0b62\u0b61\u0001\u0000\u0000\u0000\u0b63\u0b68"+
		"\u0001\u0000\u0000\u0000\u0b64\u0b67\u0003\u01f8\u00fc\u0000\u0b65\u0b67"+
		"\u0003\u01fc\u00fe\u0000\u0b66\u0b64\u0001\u0000\u0000\u0000\u0b66\u0b65"+
		"\u0001\u0000\u0000\u0000\u0b67\u0b6a\u0001\u0000\u0000\u0000\u0b68\u0b66"+
		"\u0001\u0000\u0000\u0000\u0b68\u0b69\u0001\u0000\u0000\u0000\u0b69\u01f5"+
		"\u0001\u0000\u0000\u0000\u0b6a\u0b68\u0001\u0000\u0000\u0000\u0b6b\u0b6c"+
		"\u0003\u01f4\u00fa\u0000\u0b6c\u0b6d\u0005]\u0000\u0000\u0b6d\u01f7\u0001"+
		"\u0000\u0000\u0000\u0b6e\u0b6f\u0005]\u0000\u0000\u0b6f\u01f9\u0001\u0000"+
		"\u0000\u0000\u0b70\u0b71\u0003\u01f4\u00fa\u0000\u0b71\u0b72\u0005^\u0000"+
		"\u0000\u0b72\u01fb\u0001\u0000\u0000\u0000\u0b73\u0b74\u0005^\u0000\u0000"+
		"\u0b74\u01fd\u0001\u0000\u0000\u0000\u0b75\u0b76\u0005G\u0000\u0000\u0b76"+
		"\u0b77\u0003\u0006\u0003\u0000\u0b77\u0b78\u0005H\u0000\u0000\u0b78\u0b79"+
		"\u0003\u01ec\u00f6\u0000\u0b79\u0b91\u0001\u0000\u0000\u0000\u0b7a\u0b7b"+
		"\u0005G\u0000\u0000\u0b7b\u0b7f\u0003\u000e\u0007\u0000\u0b7c\u0b7e\u0003"+
		"*\u0015\u0000\u0b7d\u0b7c\u0001\u0000\u0000\u0000\u0b7e\u0b81\u0001\u0000"+
		"\u0000\u0000\u0b7f\u0b7d\u0001\u0000\u0000\u0000\u0b7f\u0b80\u0001\u0000"+
		"\u0000\u0000\u0b80\u0b82\u0001\u0000\u0000\u0000\u0b81\u0b7f\u0001\u0000"+
		"\u0000\u0000\u0b82\u0b83\u0005H\u0000\u0000\u0b83\u0b84\u0003\u01f2\u00f9"+
		"\u0000\u0b84\u0b91\u0001\u0000\u0000\u0000\u0b85\u0b86\u0005G\u0000\u0000"+
		"\u0b86\u0b8a\u0003\u000e\u0007\u0000\u0b87\u0b89\u0003*\u0015\u0000\u0b88"+
		"\u0b87\u0001\u0000\u0000\u0000\u0b89\u0b8c\u0001\u0000\u0000\u0000\u0b8a"+
		"\u0b88\u0001\u0000\u0000\u0000\u0b8a\u0b8b\u0001\u0000\u0000\u0000\u0b8b"+
		"\u0b8d\u0001\u0000\u0000\u0000\u0b8c\u0b8a\u0001\u0000\u0000\u0000\u0b8d"+
		"\u0b8e\u0005H\u0000\u0000\u0b8e\u0b8f\u0003\u01bc\u00de\u0000\u0b8f\u0b91"+
		"\u0001\u0000\u0000\u0000\u0b90\u0b75\u0001\u0000\u0000\u0000\u0b90\u0b7a"+
		"\u0001\u0000\u0000\u0000\u0b90\u0b85\u0001\u0000\u0000\u0000\u0b91\u01ff"+
		"\u0001\u0000\u0000\u0000\u0146\u0206\u020b\u0212\u0216\u021a\u0223\u0227"+
		"\u022b\u022d\u0232\u0238\u023a\u023f\u0243\u0256\u025c\u0262\u0267\u0272"+
		"\u0275\u0283\u0288\u028d\u0292\u0298\u02a2\u02aa\u02b4\u02bc\u02c8\u02cf"+
		"\u02d4\u02da\u02e1\u02e7\u02ec\u02f5\u0300\u031d\u0322\u0326\u032e\u0335"+
		"\u033e\u034c\u034f\u035b\u035e\u036e\u0373\u037f\u0384\u038a\u038d\u0390"+
		"\u0397\u03a2\u03ad\u03bb\u03c2\u03cb\u03d2\u03dc\u03e7\u03ee\u03f4\u03f8"+
		"\u03fc\u0400\u0404\u0409\u040d\u0411\u0413\u0418\u041e\u0420\u0425\u0429"+
		"\u043c\u0445\u0452\u0457\u045d\u0463\u0465\u0469\u046e\u0472\u0479\u0480"+
		"\u0488\u048b\u0490\u0498\u049d\u04a4\u04ab\u04b0\u04b7\u04c3\u04c8\u04cc"+
		"\u04d6\u04dd\u04e4\u04e7\u04ec\u04f4\u04f7\u04fc\u0501\u0506\u050b\u0512"+
		"\u0517\u051f\u0524\u0529\u052f\u0535\u0538\u053b\u0544\u054a\u0550\u0553"+
		"\u0556\u055e\u0563\u0569\u056c\u0573\u057d\u0586\u0590\u0595\u05a0\u05a5"+
		"\u05b2\u05b7\u05c3\u05cd\u05d2\u05da\u05dd\u05e4\u05ec\u05f1\u05f9\u0602"+
		"\u060b\u0615\u0619\u061c\u0625\u0633\u0636\u063f\u0644\u064c\u0652\u065a"+
		"\u0662\u066a\u0671\u067f\u0695\u06b7\u06c3\u06c9\u06d5\u06e2\u06fc\u0700"+
		"\u0705\u0709\u070d\u0715\u0719\u071d\u0724\u072d\u0735\u0744\u0750\u0756"+
		"\u075c\u0771\u0776\u077c\u0788\u0793\u079d\u07a0\u07a5\u07ae\u07b4\u07bd"+
		"\u07c1\u07c5\u07ca\u07d2\u07e9\u07f2\u0808\u080f\u0817\u081e\u0828\u083f"+
		"\u0848\u0852\u0868\u086c\u0873\u0877\u087b\u087f\u0885\u0889\u088d\u0891"+
		"\u0897\u089b\u089f\u08a3\u08a5\u08aa\u08ae\u08b2\u08b6\u08ba\u08c1\u08c5"+
		"\u08c9\u08cd\u08d3\u08d7\u08db\u08df\u08e1\u08e5\u08f4\u0902\u090e\u0917"+
		"\u0926\u0933\u093c\u0942\u0949\u094e\u0955\u095a\u0961\u0966\u096d\u0972"+
		"\u097b\u0980\u0984\u0988\u098d\u0994\u099b\u09a0\u09a7\u09ac\u09b3\u09b8"+
		"\u09c1\u09c6\u09ca\u09d1\u09d7\u09de\u09e5\u09ec\u09f4\u09fb\u0a03\u0a07"+
		"\u0a0e\u0a15\u0a1c\u0a24\u0a2b\u0a33\u0a39\u0a3f\u0a4b\u0a51\u0a57\u0a62"+
		"\u0a6b\u0a72\u0a79\u0a7e\u0a82\u0a8a\u0aa3\u0aad\u0ab8\u0ac3\u0ace\u0ad9"+
		"\u0ae5\u0ae7\u0b01\u0b03\u0b15\u0b17\u0b21\u0b2e\u0b30\u0b42\u0b44\u0b50"+
		"\u0b5e\u0b62\u0b66\u0b68\u0b7f\u0b8a\u0b90";
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