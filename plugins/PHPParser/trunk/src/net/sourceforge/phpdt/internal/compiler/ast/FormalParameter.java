package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;
import java.io.Serializable;

/**
 * @author Matthieu Casanova
 */
public class FormalParameter extends Expression implements Serializable
{

	private String name;

	private boolean reference;

	private String defaultValue;

	public FormalParameter()
	{
	}

	public FormalParameter(String name,
			       boolean reference,
			       String defaultValue,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		super(Type.UNKNOWN, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.name = name;
		this.reference = reference;
		this.defaultValue = defaultValue;
	}

	public FormalParameter(String name,
			       boolean reference,
			       int sourceStart,
			       int sourceEnd,
			       int beginLine,
			       int endLine,
			       int beginColumn,
			       int endColumn)
	{
		this(name, reference, null, sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
	}

	@Override
	public String toStringExpression()
	{
		StringBuilder buff = new StringBuilder(200);
		if (reference)
		{
			buff.append('&');
		}
		buff.append('$').append(name);
		if (defaultValue != null)
		{
			buff.append('=');
			buff.append(defaultValue);
		}
		return buff.toString();
	}

	public String toString()
	{
		return toStringExpression();
	}

	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	}

	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	public String getName()
	{
		return name;
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
