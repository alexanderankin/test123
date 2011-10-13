package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class ConstantIdentifier extends Expression
{

	private final String name;

	public ConstantIdentifier(String name,
				  int sourceStart,
				  int sourceEnd,
				  int beginLine,
				  int endLine,
				  int beginColumn,
				  int endColumn)
	{
		super(Type.STRING, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.name = name;
	}

	public ConstantIdentifier(Token token)
	{
		super(Type.STRING, token.sourceStart, token.sourceEnd, token.beginLine, token.endLine, token.beginColumn, token.endColumn);
		name = token.image;
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		return name;
	}

	public String toString()
	{
		return name;
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
