// Generated from XQueryParser.g4 by ANTLR 4.x
package sidekick.xquery.parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link XQueryParser}.
 */
public interface XQueryParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code allNames}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void enterAllNames(@NotNull XQueryParser.AllNamesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code allNames}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void exitAllNames(@NotNull XQueryParser.AllNamesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exactMatch}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void enterExactMatch(@NotNull XQueryParser.ExactMatchContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exactMatch}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void exitExactMatch(@NotNull XQueryParser.ExactMatchContext ctx);
	/**
	 * Enter a parse tree produced by the {@code allWithLocal}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void enterAllWithLocal(@NotNull XQueryParser.AllWithLocalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code allWithLocal}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void exitAllWithLocal(@NotNull XQueryParser.AllWithLocalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code allWithNS}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void enterAllWithNS(@NotNull XQueryParser.AllWithNSContext ctx);
	/**
	 * Exit a parse tree produced by the {@code allWithNS}
	 * labeled alternative in {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void exitAllWithNS(@NotNull XQueryParser.AllWithNSContext ctx);
	/**
	 * Enter a parse tree produced by the {@code add}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterAdd(@NotNull XQueryParser.AddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code add}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitAdd(@NotNull XQueryParser.AddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code treat}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterTreat(@NotNull XQueryParser.TreatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code treat}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitTreat(@NotNull XQueryParser.TreatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code extension}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterExtension(@NotNull XQueryParser.ExtensionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code extension}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitExtension(@NotNull XQueryParser.ExtensionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mult}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterMult(@NotNull XQueryParser.MultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mult}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitMult(@NotNull XQueryParser.MultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterComparison(@NotNull XQueryParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitComparison(@NotNull XQueryParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code or}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterOr(@NotNull XQueryParser.OrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code or}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitOr(@NotNull XQueryParser.OrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intersect}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterIntersect(@NotNull XQueryParser.IntersectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intersect}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitIntersect(@NotNull XQueryParser.IntersectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code castable}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterCastable(@NotNull XQueryParser.CastableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code castable}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitCastable(@NotNull XQueryParser.CastableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code range}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterRange(@NotNull XQueryParser.RangeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code range}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitRange(@NotNull XQueryParser.RangeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code allDescPath}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterAllDescPath(@NotNull XQueryParser.AllDescPathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code allDescPath}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitAllDescPath(@NotNull XQueryParser.AllDescPathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unary}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnary(@NotNull XQueryParser.UnaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unary}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnary(@NotNull XQueryParser.UnaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code union}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnion(@NotNull XQueryParser.UnionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code union}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnion(@NotNull XQueryParser.UnionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code instanceOf}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterInstanceOf(@NotNull XQueryParser.InstanceOfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code instanceOf}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitInstanceOf(@NotNull XQueryParser.InstanceOfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code rootedPath}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterRootedPath(@NotNull XQueryParser.RootedPathContext ctx);
	/**
	 * Exit a parse tree produced by the {@code rootedPath}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitRootedPath(@NotNull XQueryParser.RootedPathContext ctx);
	/**
	 * Enter a parse tree produced by the {@code cast}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterCast(@NotNull XQueryParser.CastContext ctx);
	/**
	 * Exit a parse tree produced by the {@code cast}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitCast(@NotNull XQueryParser.CastContext ctx);
	/**
	 * Enter a parse tree produced by the {@code and}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterAnd(@NotNull XQueryParser.AndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code and}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitAnd(@NotNull XQueryParser.AndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code validate}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterValidate(@NotNull XQueryParser.ValidateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code validate}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitValidate(@NotNull XQueryParser.ValidateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relative}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelative(@NotNull XQueryParser.RelativeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relative}
	 * labeled alternative in {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelative(@NotNull XQueryParser.RelativeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code docConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void enterDocConstructor(@NotNull XQueryParser.DocConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code docConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void exitDocConstructor(@NotNull XQueryParser.DocConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code attrConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void enterAttrConstructor(@NotNull XQueryParser.AttrConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code attrConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void exitAttrConstructor(@NotNull XQueryParser.AttrConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code elementConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void enterElementConstructor(@NotNull XQueryParser.ElementConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code elementConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void exitElementConstructor(@NotNull XQueryParser.ElementConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commentConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void enterCommentConstructor(@NotNull XQueryParser.CommentConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commentConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void exitCommentConstructor(@NotNull XQueryParser.CommentConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code piConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void enterPiConstructor(@NotNull XQueryParser.PiConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code piConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void exitPiConstructor(@NotNull XQueryParser.PiConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code textConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void enterTextConstructor(@NotNull XQueryParser.TextConstructorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code textConstructor}
	 * labeled alternative in {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void exitTextConstructor(@NotNull XQueryParser.TextConstructorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unordered}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnordered(@NotNull XQueryParser.UnorderedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unordered}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnordered(@NotNull XQueryParser.UnorderedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ordered}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterOrdered(@NotNull XQueryParser.OrderedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ordered}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitOrdered(@NotNull XQueryParser.OrderedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code paren}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterParen(@NotNull XQueryParser.ParenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code paren}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitParen(@NotNull XQueryParser.ParenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code current}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterCurrent(@NotNull XQueryParser.CurrentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code current}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitCurrent(@NotNull XQueryParser.CurrentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcall}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterFuncall(@NotNull XQueryParser.FuncallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcall}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitFuncall(@NotNull XQueryParser.FuncallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code string}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterString(@NotNull XQueryParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code string}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitString(@NotNull XQueryParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code double}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterDouble(@NotNull XQueryParser.DoubleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code double}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitDouble(@NotNull XQueryParser.DoubleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code var}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterVar(@NotNull XQueryParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code var}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitVar(@NotNull XQueryParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ctor}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterCtor(@NotNull XQueryParser.CtorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ctor}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitCtor(@NotNull XQueryParser.CtorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code integer}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterInteger(@NotNull XQueryParser.IntegerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code integer}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitInteger(@NotNull XQueryParser.IntegerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decimal}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterDecimal(@NotNull XQueryParser.DecimalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decimal}
	 * labeled alternative in {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitDecimal(@NotNull XQueryParser.DecimalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code copyNamespacesDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterCopyNamespacesDecl(@NotNull XQueryParser.CopyNamespacesDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code copyNamespacesDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitCopyNamespacesDecl(@NotNull XQueryParser.CopyNamespacesDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boundaryDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterBoundaryDecl(@NotNull XQueryParser.BoundaryDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boundaryDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitBoundaryDecl(@NotNull XQueryParser.BoundaryDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code emptyOrderDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterEmptyOrderDecl(@NotNull XQueryParser.EmptyOrderDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code emptyOrderDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitEmptyOrderDecl(@NotNull XQueryParser.EmptyOrderDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code constructionDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterConstructionDecl(@NotNull XQueryParser.ConstructionDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code constructionDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitConstructionDecl(@NotNull XQueryParser.ConstructionDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orderingModeDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterOrderingModeDecl(@NotNull XQueryParser.OrderingModeDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orderingModeDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitOrderingModeDecl(@NotNull XQueryParser.OrderingModeDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code baseURIDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterBaseURIDecl(@NotNull XQueryParser.BaseURIDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code baseURIDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitBaseURIDecl(@NotNull XQueryParser.BaseURIDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code defaultCollationDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterDefaultCollationDecl(@NotNull XQueryParser.DefaultCollationDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code defaultCollationDecl}
	 * labeled alternative in {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitDefaultCollationDecl(@NotNull XQueryParser.DefaultCollationDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#module}.
	 * @param ctx the parse tree
	 */
	void enterModule(@NotNull XQueryParser.ModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#module}.
	 * @param ctx the parse tree
	 */
	void exitModule(@NotNull XQueryParser.ModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#versionDecl}.
	 * @param ctx the parse tree
	 */
	void enterVersionDecl(@NotNull XQueryParser.VersionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#versionDecl}.
	 * @param ctx the parse tree
	 */
	void exitVersionDecl(@NotNull XQueryParser.VersionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#mainModule}.
	 * @param ctx the parse tree
	 */
	void enterMainModule(@NotNull XQueryParser.MainModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#mainModule}.
	 * @param ctx the parse tree
	 */
	void exitMainModule(@NotNull XQueryParser.MainModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#libraryModule}.
	 * @param ctx the parse tree
	 */
	void enterLibraryModule(@NotNull XQueryParser.LibraryModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#libraryModule}.
	 * @param ctx the parse tree
	 */
	void exitLibraryModule(@NotNull XQueryParser.LibraryModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#moduleDecl}.
	 * @param ctx the parse tree
	 */
	void enterModuleDecl(@NotNull XQueryParser.ModuleDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#moduleDecl}.
	 * @param ctx the parse tree
	 */
	void exitModuleDecl(@NotNull XQueryParser.ModuleDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#prolog}.
	 * @param ctx the parse tree
	 */
	void enterProlog(@NotNull XQueryParser.PrologContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#prolog}.
	 * @param ctx the parse tree
	 */
	void exitProlog(@NotNull XQueryParser.PrologContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#defaultNamespaceDecl}.
	 * @param ctx the parse tree
	 */
	void enterDefaultNamespaceDecl(@NotNull XQueryParser.DefaultNamespaceDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#defaultNamespaceDecl}.
	 * @param ctx the parse tree
	 */
	void exitDefaultNamespaceDecl(@NotNull XQueryParser.DefaultNamespaceDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void enterSetter(@NotNull XQueryParser.SetterContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#setter}.
	 * @param ctx the parse tree
	 */
	void exitSetter(@NotNull XQueryParser.SetterContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#namespaceDecl}.
	 * @param ctx the parse tree
	 */
	void enterNamespaceDecl(@NotNull XQueryParser.NamespaceDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#namespaceDecl}.
	 * @param ctx the parse tree
	 */
	void exitNamespaceDecl(@NotNull XQueryParser.NamespaceDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#schemaImport}.
	 * @param ctx the parse tree
	 */
	void enterSchemaImport(@NotNull XQueryParser.SchemaImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#schemaImport}.
	 * @param ctx the parse tree
	 */
	void exitSchemaImport(@NotNull XQueryParser.SchemaImportContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#moduleImport}.
	 * @param ctx the parse tree
	 */
	void enterModuleImport(@NotNull XQueryParser.ModuleImportContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#moduleImport}.
	 * @param ctx the parse tree
	 */
	void exitModuleImport(@NotNull XQueryParser.ModuleImportContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(@NotNull XQueryParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(@NotNull XQueryParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDecl(@NotNull XQueryParser.FunctionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#functionDecl}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDecl(@NotNull XQueryParser.FunctionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#optionDecl}.
	 * @param ctx the parse tree
	 */
	void enterOptionDecl(@NotNull XQueryParser.OptionDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#optionDecl}.
	 * @param ctx the parse tree
	 */
	void exitOptionDecl(@NotNull XQueryParser.OptionDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(@NotNull XQueryParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(@NotNull XQueryParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull XQueryParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull XQueryParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#exprSingle}.
	 * @param ctx the parse tree
	 */
	void enterExprSingle(@NotNull XQueryParser.ExprSingleContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#exprSingle}.
	 * @param ctx the parse tree
	 */
	void exitExprSingle(@NotNull XQueryParser.ExprSingleContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#flworExpr}.
	 * @param ctx the parse tree
	 */
	void enterFlworExpr(@NotNull XQueryParser.FlworExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#flworExpr}.
	 * @param ctx the parse tree
	 */
	void exitFlworExpr(@NotNull XQueryParser.FlworExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#forClause}.
	 * @param ctx the parse tree
	 */
	void enterForClause(@NotNull XQueryParser.ForClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#forClause}.
	 * @param ctx the parse tree
	 */
	void exitForClause(@NotNull XQueryParser.ForClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#forVar}.
	 * @param ctx the parse tree
	 */
	void enterForVar(@NotNull XQueryParser.ForVarContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#forVar}.
	 * @param ctx the parse tree
	 */
	void exitForVar(@NotNull XQueryParser.ForVarContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#letClause}.
	 * @param ctx the parse tree
	 */
	void enterLetClause(@NotNull XQueryParser.LetClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#letClause}.
	 * @param ctx the parse tree
	 */
	void exitLetClause(@NotNull XQueryParser.LetClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#letVar}.
	 * @param ctx the parse tree
	 */
	void enterLetVar(@NotNull XQueryParser.LetVarContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#letVar}.
	 * @param ctx the parse tree
	 */
	void exitLetVar(@NotNull XQueryParser.LetVarContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#orderByClause}.
	 * @param ctx the parse tree
	 */
	void enterOrderByClause(@NotNull XQueryParser.OrderByClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#orderByClause}.
	 * @param ctx the parse tree
	 */
	void exitOrderByClause(@NotNull XQueryParser.OrderByClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#orderSpec}.
	 * @param ctx the parse tree
	 */
	void enterOrderSpec(@NotNull XQueryParser.OrderSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#orderSpec}.
	 * @param ctx the parse tree
	 */
	void exitOrderSpec(@NotNull XQueryParser.OrderSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#quantifiedExpr}.
	 * @param ctx the parse tree
	 */
	void enterQuantifiedExpr(@NotNull XQueryParser.QuantifiedExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#quantifiedExpr}.
	 * @param ctx the parse tree
	 */
	void exitQuantifiedExpr(@NotNull XQueryParser.QuantifiedExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#quantifiedVar}.
	 * @param ctx the parse tree
	 */
	void enterQuantifiedVar(@NotNull XQueryParser.QuantifiedVarContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#quantifiedVar}.
	 * @param ctx the parse tree
	 */
	void exitQuantifiedVar(@NotNull XQueryParser.QuantifiedVarContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#typeswitchExpr}.
	 * @param ctx the parse tree
	 */
	void enterTypeswitchExpr(@NotNull XQueryParser.TypeswitchExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#typeswitchExpr}.
	 * @param ctx the parse tree
	 */
	void exitTypeswitchExpr(@NotNull XQueryParser.TypeswitchExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#caseClause}.
	 * @param ctx the parse tree
	 */
	void enterCaseClause(@NotNull XQueryParser.CaseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#caseClause}.
	 * @param ctx the parse tree
	 */
	void exitCaseClause(@NotNull XQueryParser.CaseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void enterIfExpr(@NotNull XQueryParser.IfExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void exitIfExpr(@NotNull XQueryParser.IfExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterOrExpr(@NotNull XQueryParser.OrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitOrExpr(@NotNull XQueryParser.OrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(@NotNull XQueryParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(@NotNull XQueryParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#relativePathExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelativePathExpr(@NotNull XQueryParser.RelativePathExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#relativePathExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelativePathExpr(@NotNull XQueryParser.RelativePathExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#stepExpr}.
	 * @param ctx the parse tree
	 */
	void enterStepExpr(@NotNull XQueryParser.StepExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#stepExpr}.
	 * @param ctx the parse tree
	 */
	void exitStepExpr(@NotNull XQueryParser.StepExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#axisStep}.
	 * @param ctx the parse tree
	 */
	void enterAxisStep(@NotNull XQueryParser.AxisStepContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#axisStep}.
	 * @param ctx the parse tree
	 */
	void exitAxisStep(@NotNull XQueryParser.AxisStepContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#forwardStep}.
	 * @param ctx the parse tree
	 */
	void enterForwardStep(@NotNull XQueryParser.ForwardStepContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#forwardStep}.
	 * @param ctx the parse tree
	 */
	void exitForwardStep(@NotNull XQueryParser.ForwardStepContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#forwardAxis}.
	 * @param ctx the parse tree
	 */
	void enterForwardAxis(@NotNull XQueryParser.ForwardAxisContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#forwardAxis}.
	 * @param ctx the parse tree
	 */
	void exitForwardAxis(@NotNull XQueryParser.ForwardAxisContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#abbrevForwardStep}.
	 * @param ctx the parse tree
	 */
	void enterAbbrevForwardStep(@NotNull XQueryParser.AbbrevForwardStepContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#abbrevForwardStep}.
	 * @param ctx the parse tree
	 */
	void exitAbbrevForwardStep(@NotNull XQueryParser.AbbrevForwardStepContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#reverseStep}.
	 * @param ctx the parse tree
	 */
	void enterReverseStep(@NotNull XQueryParser.ReverseStepContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#reverseStep}.
	 * @param ctx the parse tree
	 */
	void exitReverseStep(@NotNull XQueryParser.ReverseStepContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#reverseAxis}.
	 * @param ctx the parse tree
	 */
	void enterReverseAxis(@NotNull XQueryParser.ReverseAxisContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#reverseAxis}.
	 * @param ctx the parse tree
	 */
	void exitReverseAxis(@NotNull XQueryParser.ReverseAxisContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#abbrevReverseStep}.
	 * @param ctx the parse tree
	 */
	void enterAbbrevReverseStep(@NotNull XQueryParser.AbbrevReverseStepContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#abbrevReverseStep}.
	 * @param ctx the parse tree
	 */
	void exitAbbrevReverseStep(@NotNull XQueryParser.AbbrevReverseStepContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#nodeTest}.
	 * @param ctx the parse tree
	 */
	void enterNodeTest(@NotNull XQueryParser.NodeTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#nodeTest}.
	 * @param ctx the parse tree
	 */
	void exitNodeTest(@NotNull XQueryParser.NodeTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void enterNameTest(@NotNull XQueryParser.NameTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void exitNameTest(@NotNull XQueryParser.NameTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#filterExpr}.
	 * @param ctx the parse tree
	 */
	void enterFilterExpr(@NotNull XQueryParser.FilterExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#filterExpr}.
	 * @param ctx the parse tree
	 */
	void exitFilterExpr(@NotNull XQueryParser.FilterExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#predicateList}.
	 * @param ctx the parse tree
	 */
	void enterPredicateList(@NotNull XQueryParser.PredicateListContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#predicateList}.
	 * @param ctx the parse tree
	 */
	void exitPredicateList(@NotNull XQueryParser.PredicateListContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#constructor}.
	 * @param ctx the parse tree
	 */
	void enterConstructor(@NotNull XQueryParser.ConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#constructor}.
	 * @param ctx the parse tree
	 */
	void exitConstructor(@NotNull XQueryParser.ConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#directConstructor}.
	 * @param ctx the parse tree
	 */
	void enterDirectConstructor(@NotNull XQueryParser.DirectConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#directConstructor}.
	 * @param ctx the parse tree
	 */
	void exitDirectConstructor(@NotNull XQueryParser.DirectConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#dirElemConstructorOpenClose}.
	 * @param ctx the parse tree
	 */
	void enterDirElemConstructorOpenClose(@NotNull XQueryParser.DirElemConstructorOpenCloseContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#dirElemConstructorOpenClose}.
	 * @param ctx the parse tree
	 */
	void exitDirElemConstructorOpenClose(@NotNull XQueryParser.DirElemConstructorOpenCloseContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#dirElemConstructorSingleTag}.
	 * @param ctx the parse tree
	 */
	void enterDirElemConstructorSingleTag(@NotNull XQueryParser.DirElemConstructorSingleTagContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#dirElemConstructorSingleTag}.
	 * @param ctx the parse tree
	 */
	void exitDirElemConstructorSingleTag(@NotNull XQueryParser.DirElemConstructorSingleTagContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#dirAttributeList}.
	 * @param ctx the parse tree
	 */
	void enterDirAttributeList(@NotNull XQueryParser.DirAttributeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#dirAttributeList}.
	 * @param ctx the parse tree
	 */
	void exitDirAttributeList(@NotNull XQueryParser.DirAttributeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#dirAttributeValue}.
	 * @param ctx the parse tree
	 */
	void enterDirAttributeValue(@NotNull XQueryParser.DirAttributeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#dirAttributeValue}.
	 * @param ctx the parse tree
	 */
	void exitDirAttributeValue(@NotNull XQueryParser.DirAttributeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#dirElemContent}.
	 * @param ctx the parse tree
	 */
	void enterDirElemContent(@NotNull XQueryParser.DirElemContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#dirElemContent}.
	 * @param ctx the parse tree
	 */
	void exitDirElemContent(@NotNull XQueryParser.DirElemContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#commonContent}.
	 * @param ctx the parse tree
	 */
	void enterCommonContent(@NotNull XQueryParser.CommonContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#commonContent}.
	 * @param ctx the parse tree
	 */
	void exitCommonContent(@NotNull XQueryParser.CommonContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void enterComputedConstructor(@NotNull XQueryParser.ComputedConstructorContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#computedConstructor}.
	 * @param ctx the parse tree
	 */
	void exitComputedConstructor(@NotNull XQueryParser.ComputedConstructorContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#singleType}.
	 * @param ctx the parse tree
	 */
	void enterSingleType(@NotNull XQueryParser.SingleTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#singleType}.
	 * @param ctx the parse tree
	 */
	void exitSingleType(@NotNull XQueryParser.SingleTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(@NotNull XQueryParser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(@NotNull XQueryParser.TypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#sequenceType}.
	 * @param ctx the parse tree
	 */
	void enterSequenceType(@NotNull XQueryParser.SequenceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#sequenceType}.
	 * @param ctx the parse tree
	 */
	void exitSequenceType(@NotNull XQueryParser.SequenceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#itemType}.
	 * @param ctx the parse tree
	 */
	void enterItemType(@NotNull XQueryParser.ItemTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#itemType}.
	 * @param ctx the parse tree
	 */
	void exitItemType(@NotNull XQueryParser.ItemTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#kindTest}.
	 * @param ctx the parse tree
	 */
	void enterKindTest(@NotNull XQueryParser.KindTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#kindTest}.
	 * @param ctx the parse tree
	 */
	void exitKindTest(@NotNull XQueryParser.KindTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#documentTest}.
	 * @param ctx the parse tree
	 */
	void enterDocumentTest(@NotNull XQueryParser.DocumentTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#documentTest}.
	 * @param ctx the parse tree
	 */
	void exitDocumentTest(@NotNull XQueryParser.DocumentTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#elementTest}.
	 * @param ctx the parse tree
	 */
	void enterElementTest(@NotNull XQueryParser.ElementTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#elementTest}.
	 * @param ctx the parse tree
	 */
	void exitElementTest(@NotNull XQueryParser.ElementTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#attributeTest}.
	 * @param ctx the parse tree
	 */
	void enterAttributeTest(@NotNull XQueryParser.AttributeTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#attributeTest}.
	 * @param ctx the parse tree
	 */
	void exitAttributeTest(@NotNull XQueryParser.AttributeTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#schemaElementTest}.
	 * @param ctx the parse tree
	 */
	void enterSchemaElementTest(@NotNull XQueryParser.SchemaElementTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#schemaElementTest}.
	 * @param ctx the parse tree
	 */
	void exitSchemaElementTest(@NotNull XQueryParser.SchemaElementTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#schemaAttributeTest}.
	 * @param ctx the parse tree
	 */
	void enterSchemaAttributeTest(@NotNull XQueryParser.SchemaAttributeTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#schemaAttributeTest}.
	 * @param ctx the parse tree
	 */
	void exitSchemaAttributeTest(@NotNull XQueryParser.SchemaAttributeTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#piTest}.
	 * @param ctx the parse tree
	 */
	void enterPiTest(@NotNull XQueryParser.PiTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#piTest}.
	 * @param ctx the parse tree
	 */
	void exitPiTest(@NotNull XQueryParser.PiTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#commentTest}.
	 * @param ctx the parse tree
	 */
	void enterCommentTest(@NotNull XQueryParser.CommentTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#commentTest}.
	 * @param ctx the parse tree
	 */
	void exitCommentTest(@NotNull XQueryParser.CommentTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#textTest}.
	 * @param ctx the parse tree
	 */
	void enterTextTest(@NotNull XQueryParser.TextTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#textTest}.
	 * @param ctx the parse tree
	 */
	void exitTextTest(@NotNull XQueryParser.TextTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#anyKindTest}.
	 * @param ctx the parse tree
	 */
	void enterAnyKindTest(@NotNull XQueryParser.AnyKindTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#anyKindTest}.
	 * @param ctx the parse tree
	 */
	void exitAnyKindTest(@NotNull XQueryParser.AnyKindTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#qName}.
	 * @param ctx the parse tree
	 */
	void enterQName(@NotNull XQueryParser.QNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#qName}.
	 * @param ctx the parse tree
	 */
	void exitQName(@NotNull XQueryParser.QNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#ncName}.
	 * @param ctx the parse tree
	 */
	void enterNcName(@NotNull XQueryParser.NcNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#ncName}.
	 * @param ctx the parse tree
	 */
	void exitNcName(@NotNull XQueryParser.NcNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(@NotNull XQueryParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(@NotNull XQueryParser.FunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#keyword}.
	 * @param ctx the parse tree
	 */
	void enterKeyword(@NotNull XQueryParser.KeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#keyword}.
	 * @param ctx the parse tree
	 */
	void exitKeyword(@NotNull XQueryParser.KeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#keywordNotOKForFunction}.
	 * @param ctx the parse tree
	 */
	void enterKeywordNotOKForFunction(@NotNull XQueryParser.KeywordNotOKForFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#keywordNotOKForFunction}.
	 * @param ctx the parse tree
	 */
	void exitKeywordNotOKForFunction(@NotNull XQueryParser.KeywordNotOKForFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#keywordOKForFunction}.
	 * @param ctx the parse tree
	 */
	void enterKeywordOKForFunction(@NotNull XQueryParser.KeywordOKForFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#keywordOKForFunction}.
	 * @param ctx the parse tree
	 */
	void exitKeywordOKForFunction(@NotNull XQueryParser.KeywordOKForFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(@NotNull XQueryParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(@NotNull XQueryParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link XQueryParser#noQuotesNoBracesNoAmpNoLAng}.
	 * @param ctx the parse tree
	 */
	void enterNoQuotesNoBracesNoAmpNoLAng(@NotNull XQueryParser.NoQuotesNoBracesNoAmpNoLAngContext ctx);
	/**
	 * Exit a parse tree produced by {@link XQueryParser#noQuotesNoBracesNoAmpNoLAng}.
	 * @param ctx the parse tree
	 */
	void exitNoQuotesNoBracesNoAmpNoLAng(@NotNull XQueryParser.NoQuotesNoBracesNoAmpNoLAngContext ctx);
}