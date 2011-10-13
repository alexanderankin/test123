package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * A do statement.
 *
 * @author Matthieu Casanova
 */
public class DoStatement extends Statement
{
	/**
	 * The condition expression.
	 */
	private final Expression condition;
	/**
	 * The action of the while. (it could be a block)
	 */
	private final Statement action;

	public DoStatement(Expression condition,
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
		String conditionString = condition.toStringExpression();
		StringBuffer buff;
		if (action == null)
		{
			buff = new StringBuffer(17 + tab + conditionString.length());
			buff.append("do ");
			buff.append(" {} ;");
		}
		else
		{
			String actionString = action.toString(tab + 1);
			buff = new StringBuffer(13 + conditionString.length() + actionString.length());
			buff.append("do ");
			buff.append('\n');
			buff.append(actionString);
		}
		buff.append(tabString(tab));
		buff.append(" while (");
		buff.append(conditionString);
		buff.append(')');
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
		action.getOutsideVariable(list);
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
		action.getModifiedVariable(list);
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
		action.getUsedVariable(list);
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (condition.isAt(line, column)) return condition;
		if (action.isAt(line, column)) return action.subNodeAt(line, column);
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		condition.analyzeCode(parser);
		action.analyzeCode(parser);
	}

}
