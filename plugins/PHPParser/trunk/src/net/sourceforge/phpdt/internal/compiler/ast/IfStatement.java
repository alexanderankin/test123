package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * This is a if statement.
 * if (condition)
 * statement
 * (elseif statement)*
 * else statement
 *
 * @author Matthieu Casanova
 */
public class IfStatement extends Statement
{

	private final Expression condition;
	private final Statement statement;
	private final ElseIf[] elseifs;
	private final Else els;

	/**
	 * Create a new If statement.
	 *
	 * @param condition   the condition
	 * @param statement   a statement or a block of statements
	 * @param elseifs     the elseifs
	 * @param els	 the else (or null)
	 * @param sourceStart the starting position
	 * @param sourceEnd   the ending offset
	 */
	public IfStatement(Expression condition,
			   Statement statement,
			   ElseIf[] elseifs,
			   Else els,
			   int sourceStart,
			   int sourceEnd,
			   int beginLine,
			   int endLine,
			   int beginColumn,
			   int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.condition = condition;
		this.statement = statement;
		this.elseifs = elseifs;
		this.els = els;
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
		buff.append("if (");
		buff.append(condition.toStringExpression()).append(") ");
		if (statement != null)
		{
			buff.append(statement.toString(tab + 1));
		}
		for (int i = 0; i < elseifs.length; i++)
		{
			buff.append(elseifs[i].toString(tab + 1));
			buff.append('\n');
		}
		if (els != null)
		{
			buff.append(els.toString(tab + 1));
			buff.append('\n');
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
		if (statement != null)
		{
			statement.getOutsideVariable(list);
		}
		for (int i = 0; i < elseifs.length; i++)
		{
			elseifs[i].getOutsideVariable(list);
		}
		if (els != null)
		{
			els.getOutsideVariable(list);
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
		if (statement != null)
		{
			statement.getModifiedVariable(list);
		}
		for (int i = 0; i < elseifs.length; i++)
		{
			elseifs[i].getModifiedVariable(list);
		}
		if (els != null)
		{
			els.getModifiedVariable(list);
		}
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		condition.analyzeCode(parser);
		if (statement != null)
		{
			statement.analyzeCode(parser);
		}
		for (int i = 0; i < elseifs.length; i++)
		{
			elseifs[i].analyzeCode(parser);
		}
		if (els != null)
		{
			els.analyzeCode(parser);
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
		if (statement != null)
		{
			statement.getUsedVariable(list);
		}
		for (int i = 0; i < elseifs.length; i++)
		{
			elseifs[i].getUsedVariable(list);
		}
		if (els != null)
		{
			els.getUsedVariable(list);
		}
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (condition.isAt(line, column)) return condition;
		if (statement != null && statement.isAt(line, column)) return statement.subNodeAt(line, column);
		for (int i = 0; i < elseifs.length; i++)
		{
			ElseIf elseif = elseifs[i];
			if (elseif.isAt(line, column)) return elseif.subNodeAt(line, column);
		}
		if (els != null && els.isAt(line, column)) return els.subNodeAt(line, column);
		return null;
	}

}
