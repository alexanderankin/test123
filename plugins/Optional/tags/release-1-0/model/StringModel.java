package model;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A model for a single String, which supports TextListeners. Simpler to use
 * than Document when all you need is to store a single string.
 * 
 * @author ezust
 * 
 */
public class StringModel
{

	String theText = null;

	LinkedList listeners = new LinkedList();

	public void addTextListener(TextListener tl)
	{
		listeners.add(tl);
	}

	void removeTextListener(TextListener tl)
	{
		listeners.remove(tl);
	}

	void fireTextChanged()
	{
		TextEvent te = new TextEvent(this, TextEvent.TEXT_VALUE_CHANGED);
		Iterator itr = listeners.iterator();
		while (itr.hasNext())
		{
			TextListener tl = (TextListener) itr.next();
			tl.textValueChanged(te);
		}
	}

	public String toString()
	{
		return theText;
	}

	public void setText(String newText)
	{
		theText = newText;
		fireTextChanged();
	}
}
