package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class ThrowStatement extends Statement
{
	private final Expression throwed;

	public ThrowStatement(Expression throwed,
			      int sourceStart,
			      int sourceEnd,
			      int beginLine,
			      int endLine,
			      int beginColumn,
			      int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.throwed = throwed;
	}

	@Override
	public String toString(int tab)
	{
		return tabString(tab) + "throw " + throwed.toStringExpression();
	}

	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
		throwed.getOutsideVariable(list);
	}

	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
		throwed.getOutsideVariable(list);
	}

	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
		throwed.getUsedVariable(list);
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		return throwed.isAt(line, column) ? throwed : null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		throwed.analyzeCode(parser);
	}
}
