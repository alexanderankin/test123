package sidekick.java.tools;

/**
 * This is a dummy class to add basic recognition of arrays
 * Using this, a completion like this will work:
 *
 *   String[] strs = new String { "hello", "world" };
 *   strs.|
 *
 * This lets the user complete the 'length' variable as well
 * as methods from the Object class
 */

public class Array {
	public int length;
}
