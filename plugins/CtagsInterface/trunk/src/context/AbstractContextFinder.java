package context;

import org.gjt.sp.jedit.Buffer;

import ctags.Tag;

public abstract class AbstractContextFinder {
	protected Buffer buffer;
	protected int pos;
	protected int line;
	AbstractContextFinder(Buffer buffer, int pos)
	{
		this.buffer = buffer;
		this.pos = pos;
		line = buffer.getLineOfOffset(pos);
	}
	abstract public String findExpressionBeforePos();
	abstract public String findExpressionAtPos();
	abstract public Object[] parseExpression(String expression);
	abstract public Object findFirstContext(Object firstExpressionPart);
	abstract public Object findNextContext(Object prevContext, Object expressionPart);
	abstract public Tag getCurrentContext();
	abstract public Tag getContextTag(Object context);
	
	private Tag resolveExpression(String expr)
	{
		if (expr == null)
			return null;
		Object[] parts = parseExpression(expr);
		if (parts == null)
			return null;
		Object current = findFirstContext(parts[0]);
		for (int i = 1; i < parts.length; i++)
		{
			current = findNextContext(current, parts[i]);
			if (current == null)
				return null;
		}
		return getContextTag(current);
	}
	public Tag getContext()
	{
		String expr = findExpressionBeforePos();
		if (expr == null)
			return getCurrentContext();
		return resolveExpression(expr);
	}
	public Tag getTag()
	{
		String expr = findExpressionAtPos();
		if (expr == null)
			return null;
		return resolveExpression(expr);
	}
}
