/**
 * 
 */
package net.jakubholy.jedit.autocomplete;

/**
 * Some assumptions the test makes about the tested code are 
 * not valid anymore and it's necessary to update the code of 
 * the test.
 * 
 * @author Jakub Holy
 */
public class TestOutOfDateException extends RuntimeException
{

	/**
	 * @param message
	 */
	public TestOutOfDateException(String message)
	{
		super(message);
	}
}
