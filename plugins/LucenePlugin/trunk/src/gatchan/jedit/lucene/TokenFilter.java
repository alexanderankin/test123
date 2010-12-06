package gatchan.jedit.lucene;

import org.gjt.sp.jedit.syntax.Token;

public class TokenFilter {
	private boolean comments;
	private boolean literals;
	public TokenFilter(boolean comments, boolean literals)
	{
		this.comments = comments;
		this.literals = literals;
	}
	public boolean isFiltering()
	{
		return comments || literals;
	}
	public boolean isFiltered(Token token)
	{
		switch (token.id)
		{
		case Token.COMMENT1:
		case Token.COMMENT2:
		case Token.COMMENT3:
		case Token.COMMENT4:
			return comments;
		case Token.LITERAL1:
		case Token.LITERAL2:
		case Token.LITERAL3:
		case Token.LITERAL4:
			return literals;
		}
		return false;
	}
}