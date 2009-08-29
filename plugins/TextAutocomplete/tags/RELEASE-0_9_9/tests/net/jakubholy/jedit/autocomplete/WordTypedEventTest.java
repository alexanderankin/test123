package net.jakubholy.jedit.autocomplete;

import junit.framework.TestCase;

public class WordTypedEventTest extends TestCase
{

	/*
	 * Test method for 'net.jakubholy.jedit.autocomplete.WordTypedEvent.equals(Object)'
	 */
	public void testEquals()
	{
		StringBuffer word1 = new StringBuffer("word"); 
		StringBuffer word2 = new StringBuffer(word1);
		String ins1 = "a";
		String ins2 = new String(ins1);
		
		WordTypedEvent e1 = new WordTypedEvent(
				WordTypedEvent.INSIDE, word1, ins1);
		WordTypedEvent e2 = new WordTypedEvent(
				WordTypedEvent.INSIDE, word2, ins2);
		
		assertFalse("Should != null", e1.equals(null));
		assertFalse("Should != a StringBuffer", e1.equals(word1));
		assertTrue("Should == self", e1.equals(e1));
		assertTrue("Should == (same constructor)", e1.equals(e2));
		assertTrue("Should == (same constructor, reflexivity)", e2.equals(e1));
		
		// with nulls
		e1 = new WordTypedEvent(
				WordTypedEvent.INSIDE, word1, null);
		e2 = new WordTypedEvent(
				WordTypedEvent.INSIDE, word2, null);
		assertTrue("Should == (same constructor with null)", e1.equals(e2));
		
		// with nulls 2
		e1 = new WordTypedEvent(
				WordTypedEvent.INSIDE, word1, "dummy");
		e2 = new WordTypedEvent(
				WordTypedEvent.INSIDE, word2, null);
		assertFalse("Should != if 1 msg is null", e1.equals(e2));
		assertFalse("Should != if 1 msg is null", e2.equals(e1));

	}

}
