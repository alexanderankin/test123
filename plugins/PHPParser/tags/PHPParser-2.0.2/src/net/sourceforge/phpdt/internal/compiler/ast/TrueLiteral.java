package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.Token;
import gatchan.phpparser.parser.PHPParser;

/**
 * the true literal.
 *
 * @author Matthieu Casanova
 */
public class TrueLiteral extends MagicLiteral
{

	public TrueLiteral(Token token)
	{
		super(Type.BOOLEAN, token.sourceStart, token.sourceEnd, token.beginLine, token.endLine, token.beginColumn, token.endColumn);
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		return "true";
	}

	public String toString()
	{
		return "true";
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}

}
