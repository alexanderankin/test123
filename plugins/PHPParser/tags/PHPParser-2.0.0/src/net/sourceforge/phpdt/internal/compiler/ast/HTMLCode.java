package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;


/**
 * It's html code. It will contains some html, javascript, css ...
 *
 * @author Matthieu Casanova
 */
public class HTMLCode extends Statement
{
	/**
	 * The html Code.
	 */
	private final String htmlCode;

	/**
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 * @param beginLine   begin line
	 * @param endLine     ending line
	 * @param beginColumn begin column
	 * @param endColumn   ending column
	 */
	public HTMLCode(String htmlCode,
			int sourceStart,
			int sourceEnd,
			int beginLine,
			int endLine,
			int beginColumn,
			int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.htmlCode = htmlCode;
	}

	/**
	 * I don't process tabs, it will only return the html inside.
	 *
	 * @return the text of the block
	 */
	public String toString()
	{
		return htmlCode;
	}

	/**
	 * I don't process tabs, it will only return the html inside.
	 *
	 * @param tab how many tabs before this html
	 * @return the text of the block
	 */
	@Override
	public String toString(int tab)
	{
		return htmlCode + ' ';
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}
}
