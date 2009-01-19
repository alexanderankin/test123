package superabbrevs.collections;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class IndexedSortedSetTest extends SortedSetTest {

	public IndexedSortedSet<Integer> indexedSortedSet;
	
	@Before
	public void setup() {
		super.setup();
		indexedSortedSet = (IndexedSortedSet<Integer>) sortedSet;
	}

	@Test
	public void getTest() throws Exception {
		int[] values = { 0, 1, 3, 4, 5, 7, 8, 9 };
		for (int i = 0; i < values.length; i++) {
			assertEquals(values[i], indexedSortedSet.get(i));
		}
	}
	
	@Test
	public void removeIndexTest() throws Exception {
		indexedSortedSet.remove(3);
		assertValues(indexedSortedSet, 0, 1, 3, 5, 7, 8, 9); 
	}
	
	@Test
	public void indexOfTest() throws Exception {
		assertEquals(3, indexedSortedSet.indexOf(4));
	}
	
	@Test
	public void subListTest() throws Exception {
		assertValues(indexedSortedSet.subList(2, 6), 3, 4, 5, 7);
	}

}
