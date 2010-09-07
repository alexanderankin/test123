package net.sourceforge.phpdt.internal.compiler.ast;


/**
 * An expression.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public abstract class Expression extends Statement
{

	protected Type type = Type.UNKNOWN;

	/**
	 * Create an expression giving starting and ending offset
	 *
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 */
	protected Expression(Type type,
			     int sourceStart,
			     int sourceEnd,
			     int beginLine,
			     int endLine,
			     int beginColumn,
			     int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.type = type;
	}

	protected Expression()
	{
	}

	/**
	 * Return the expression with a number of spaces before.
	 *
	 * @param tab how many spaces before the expression
	 * @return a string representing the expression
	 */
	@Override
	public String toString(int tab)
	{
		return tabString(tab) + toStringExpression();
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	public abstract String toStringExpression();

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public String toString()
	{
		return getClass().getName() + '[' + toStringExpression() + ',' + type + ']';
	}

}
