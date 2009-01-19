package superabbrevs.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class IndexedSortedSet<E> implements SortedSet<E> {

	private List<E> list = new ArrayList<E>();
	
	private Comparator<E> comparator = null;

	public IndexedSortedSet() {
		super();
	}
	
	public IndexedSortedSet(Comparator<E> comparator) {
		super();
		this.comparator = comparator;
	}
	
	private IndexedSortedSet(List<E> sortedList) {
		list.addAll(sortedList);
	}

	public boolean add(E o) {
		int index = Collections.binarySearch(list, o, comparator);
		
		if (0 <= index) {
			return false;
		}
		int insertionPoint = -index - 1;
		
		if (insertionPoint >= size()) {
			list.add(o);
		} else {
			list.add(insertionPoint, o);
		}
		return true;
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c) {
			boolean added = add(e);
			changed = changed || added;
		}
		return changed;
	}

	public E set(int index, E element) {
		E removed = list.remove(index);
		add(element);
		return removed;
	}

	public Comparator<? super E> comparator() {
		return comparator;
	}

	public E first() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return list.get(0);
	}

	public SortedSet<E> headSet(E toElement) {
		List<E> subList = list.subList(0, list.indexOf(toElement));
		return new IndexedSortedSet<E>(subList);
	}

	public E last() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return list.get(size()-1);
	}

	public SortedSet<E> subSet(E fromElement, E toElement) {
		List<E> subList = list.subList(list.indexOf(fromElement), list.lastIndexOf(toElement));
		return new IndexedSortedSet<E>(subList);
	}

	public SortedSet<E> tailSet(E fromElement) {
		List<E> subList = list.subList(list.lastIndexOf(fromElement), size());
		return new IndexedSortedSet<E>(subList);
	}

	public void clear() {
		list.clear();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<E> iterator() {
		return list.iterator();
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public int size() {
		return list.size();
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	public E get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public E remove(int index) {
		return list.remove(index);
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
}
