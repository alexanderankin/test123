package superabbrevs.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

public class SortedSetTest {

	SortedSet<Integer> sortedSet;
	SortedSet<Integer> sortedSetWithComparator;

	@Before
	public void setup() {
		sortedSet = new IndexedSortedSet<Integer>();
		List<Integer> values = shuffledList(0, 1, 3, 4, 5, 7, 8, 9);
		sortedSet.addAll(values);

		sortedSetWithComparator = new IndexedSortedSet<Integer>(Collections
				.<Integer> reverseOrder());
		sortedSetWithComparator.addAll(values);
	}

	@Test
	public void useNaturalOrderIfComparatorIsNotSet() throws Exception {
		sortedSet.add(2);
		assertValues(sortedSet, 0, 1, 2, 3, 4, 5, 7, 8, 9);
	}

	@Test
	public void addUsesComparator() throws Exception {
		sortedSetWithComparator.add(2);
		assertValues(sortedSetWithComparator, 9, 8, 7, 5, 4, 3, 2, 1, 0);
	}

	@Test
	public void firstTest() throws Exception {
		assertEquals(sortedSet.first(), 0);
	}

	@Test
	public void headSetTest() throws Exception {
		assertValues(sortedSet.headSet(5), 0, 1, 3, 4);
	}

	@Test
	public void lastTest() throws Exception {
		assertEquals(sortedSet.last(), 9);
	}

	@Test
	public void subSetTest() throws Exception {
		assertValues(sortedSet.subSet(3, 8), 3, 4, 5, 7);
	}

	@Test
	public void tailSetTest() throws Exception {
		assertValues(sortedSet.tailSet(5), 5, 7, 8, 9);
	}

	@Test
	public void addTest() throws Exception {
		assertTrue(sortedSet.add(2));
		assertTrue(sortedSet.add(6));
		assertFalse(sortedSet.add(7));
		assertValues(sortedSet, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void addAllTest() throws Exception {
		List<Integer> values = shuffledList(2, 6, 7);
		sortedSet.addAll(values);
		assertValues(sortedSet, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void clearTest() throws Exception {
		assertFalse(sortedSet.isEmpty());
		sortedSet.clear();
		assertTrue(sortedSet.isEmpty());
	}

	@Test
	public void containsTestWithExistingElement() throws Exception {
		assertTrue(sortedSet.contains(5));
	}

	@Test
	public void containsTestWithoutExistingElement() throws Exception {
		assertFalse(sortedSet.contains(2));
	}

	@Test
	public void containsAllTestWithSuccess() throws Exception {
		List<Integer> values = shuffledList(1, 3,7, 8);
		assertTrue(sortedSet.containsAll(values));
	}
	
	@Test
	public void containsAllTestWithFailure() throws Exception {
		List<Integer> values = shuffledList(1, 2, 3,7, 8);
		assertFalse(sortedSet.containsAll(values));
	}

	@Test
	public void removeTest() throws Exception {
		assertTrue(sortedSet.remove(4));
		assertTrue(sortedSet.remove(8));
		assertFalse(sortedSet.remove(10));
		assertValues(sortedSet, 0, 1, 3, 5, 7, 9);
	}

	@Test
	public void removeAllTest() throws Exception {
		List<Integer> values = shuffledList(4,8,10);
		assertTrue(sortedSet.removeAll(values));
		assertValues(sortedSet, 0, 1, 3, 5, 7, 9);
	}

	@Test
	public void retainAllTest() throws Exception {
		List<Integer> values = shuffledList(4,8,10);
		assertTrue(sortedSet.retainAll(values));
		assertValues(sortedSet, 4,8);
	}

	@Test
	public void sizeTest() throws Exception {
		int expectedSize = 8;
		assertEquals(expectedSize, sortedSet.size());
		sortedSet.add(10);
		assertEquals(expectedSize + 1, sortedSet.size());
	}

	@Test
	public void toArrayTest() throws Exception {
		Integer[] array = sortedSet.toArray(new Integer[0]);
		assertValues(Arrays.asList(array), 0, 1, 3, 4, 5, 7, 8, 9);
	}

	protected static void assertValues(Iterable<Integer> actual, int... expected) {
		int i = 0;
		for (int value : actual) {
			assertEquals(expected[i], value);
			i++;
		}
	}

	protected static List<Integer> shuffledList(Integer... values) {
		List<Integer> valueList = Arrays.asList(values);
		Collections.shuffle(valueList);
		return valueList;
	}
}
