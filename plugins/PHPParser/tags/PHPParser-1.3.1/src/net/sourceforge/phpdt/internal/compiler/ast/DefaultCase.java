package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;

/**
 * A default case for a switch.
 * it's default : .....;
 *
 * @author Matthieu Casanova
 */
public class DefaultCase extends AbstractCase
{

	/**
	 * Create a default case.
	 *
	 * @param statements  the statements
	 * @param sourceStart the starting offset
	 * @param sourceEnd   the ending offset
	 */
	public DefaultCase(Statement[] statements,
			   int sourceStart,
			   int sourceEnd,
			   int beginLine,
			   int endLine,
			   int beginColumn,
			   int endColumn)
	{
		super(statements, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	/**
	 * Return the object into String.
	 *
	 * @param tab how many tabs (not used here
	 * @return a String
	 */
	@Override
	public String toString(int tab)
	{
		StringBuilder buff = new StringBuilder(tabString(tab));
		buff.append("default : \n"); //$NON-NLS-1$
		for (int i = 0; i < statements.length; i++)
		{
			Statement statement = statements[i];
			buff.append(statement.toString(tab + 9));
		}
		return buff.toString();
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		for (int i = 0; i < statements.length; i++)
		{
			statements[i].analyzeCode(parser);
		}
	}
}
