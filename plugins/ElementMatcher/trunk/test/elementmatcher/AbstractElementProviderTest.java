package elementmatcher;

import static org.junit.Assert.*;
import javax.swing.text.Segment;
import java.util.Iterator;
import java.text.MessageFormat;

public abstract class AbstractElementProviderTest {

    protected <T> void testLine(ElementProvider<T> provider, String line, T... expectedElements) {
        System.out.println("testLine: matching line '" + line + "' against provider " + provider);
        final Iterator<Element<T>> it = provider.getElements(0, new Segment(line.toCharArray(), 0, line.length()));
        for (int i=0; i<expectedElements.length; ++i) {
            final T expectedElement = expectedElements[i];
            final String elementDescription = MessageFormat.format("Element {0} ''{1}''", i, expectedElement);
            assertTrue(elementDescription + " not found", it.hasNext());
            assertEquals(elementDescription + " incorrect", expectedElement, it.next().data);
            System.out.println("\tmatched: " + expectedElement);
        }
        if (it.hasNext()) {
            fail(expectedElements.length + " elements expected, extra element found: '" + it.next().data);
        }
    }

}