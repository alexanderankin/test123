/*
 * Created on Dec 9, 2003
 * @author Wim Le Page
 * @author Pieter Wellens
 *
 */
package xquery;

/**
 * @author Wim Le Page
 * @author Pieter Wellens
 * @version 0.3.0
 *
 */
public class XQueryException extends RuntimeException
{

	public XQueryException(String s, Throwable throwable)
	{
		super(s, throwable);
	}

	public XQueryException(String s)
	{
		super(s);
	}

	public XQueryException(Throwable throwable)
	{
		super(throwable);
	}
}
