package trie;

import java.util.Collection;
import java.util.LinkedList;


public class Match<T> {
	private final String matchingText;
	private final LinkedList<T> elements;

	public Match(String matchingText, Collection<T> elements) {
		this.matchingText = matchingText;
		this.elements = new LinkedList<T>(elements);
	}
	
	public Collection<T> getElements() {
		return elements;
	}

	public String getMatchingText() {
		return matchingText;
	}

	public int size() {
		return elements.size();
	}

	public T getFirst() {
		return elements.getFirst();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}
}
