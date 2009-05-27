package elementmatcher;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ElementsManagerTest {

    @Test
    public void testMultilineElementIterator() {
        final SortedMap<Integer, List<Element<?>>> map = new TreeMap<Integer, List<Element<?>>>();
        map.put(3, getDummyElements());
        map.put(5, getDummyElements("five"));
        map.put(6, getDummyElements("six", "six again"));
        final List<String> result = new ArrayList<String>();
        for (ElementManager.MultilineElementIterator it = new ElementManager.MultilineElementIterator(map); it.hasNext(); ) {
            result.add((String)it.next().data);
        }
        assertArrayEquals(new Object[] { "five", "six", "six again"}, result.toArray());
    }

    private List<Element<?>> getDummyElements(String... data) {
        final List<Element<?>> list = new ArrayList<Element<?>>(data.length);
        for (String string : data) {
            list.add(new Element<Object>(null, -1, -1, -1, string));
        }
        return list;
    }

}