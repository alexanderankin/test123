package net.sourceforge.phpdt.internal.compiler.ast;

/**
 * A continue statement.
 *
 * @author Matthieu Casanova
 */
public class Continue extends BranchStatement
{

	public Continue(Expression expression, int sourceStart, int sourceEnd,
			int beginLine,
			int endLine,
			int beginColumn,
			int endColumn)
	{
		super(expression, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	@Override
	public String toString(int tab)
	{
		String s = tabString(tab);
		if (expression == null)
		{
			return s + "continue";//$NON-NLS-1$
		}
		return s + "continue " + expression.toString();//$NON-NLS-1$
	}
}
