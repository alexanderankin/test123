package net.jakubholy.jedit.autocomplete;

import junit.framework.TestCase;

/**
 * Parent of all jEdit test cases that need running jEdit.
 * @author Jakub Holy
 *
 */
public abstract class AbstractJEditTest extends TestCase
{

	public AbstractJEditTest(String arg0)
	{
		super(arg0);
	}

	/** Start jEdit if not running. */
	protected void setUp() throws Exception
	{
		super.setUp();
		TestUtils.startJEdit();
	}

}
