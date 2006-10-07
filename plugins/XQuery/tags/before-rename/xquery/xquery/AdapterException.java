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
 * @version 0.6.0
 *
 */
public class AdapterException extends XQueryException
{

	public AdapterException(String s, Throwable throwable)
	{
		super(s, throwable);
	}

	public AdapterException(String s)
	{
		super(s);
	}
}
