package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * A While statement.
 *
 * @author Matthieu Casanova
 */
public class WhileStatement extends Statement
{

	/**
	 * The condition expression.
	 */
	private final Expression condition;
	/**
	 * The action of the while. (it could be a block)
	 */
	private final Statement action;

	/**
	 * Create a While statement.
	 *
	 * @param condition   the condition
	 * @param action      the action
	 * @param sourceStart the starting offset
	 * @param sourceEnd   the ending offset
	 */
	public WhileStatement(Expression condition,
			      Statement action,
			      int sourceStart,
			      int sourceEnd,
			      int beginLine,
			      int endLine,
			      int beginColumn,
			      int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.condition = condition;
		this.action = action;
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
		String s = tabString(tab);
		StringBuilder buff = new StringBuilder(s).append("while ("); //$NON-NLS-1$
		buff.append(condition.toStringExpression()).append(')');	 //$NON-NLS-1$
		if (action == null)
		{
			buff.append(" {} ;"); //$NON-NLS-1$
		}
		else
		{
			buff.append('\n').append(action.toString(tab + 1)); //$NON-NLS-1$
		}
		return buff.toString();
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
		condition.getOutsideVariable(list); // todo: check if unuseful
		if (action != null)
		{
			action.getOutsideVariable(list);
		}
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		condition.getModifiedVariable(list);
		if (action != null)
		{
			action.getModifiedVariable(list);
		}
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		condition.getUsedVariable(list);
		if (action != null)
		{
			action.getUsedVariable(list);
		}
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		if (condition.isAt(line, column)) return condition;
		if (action != null && action.isAt(line, column)) return action.expressionAt(line, column);
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		condition.analyzeCode(parser);
		if (action != null)
		{
			action.analyzeCode(parser);
		}
	}
}
