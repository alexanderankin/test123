package elementmatcher;

import org.junit.Test;
import javax.swing.Action;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractRegexElementProviderTest extends AbstractElementProviderTest {

    @Test
    public void testFallback() {
        testLine(new FirstCharElementProvider(), "AB", "A", "B");
    }

    @Test
    public void testExpr() {
        testLine(new FirstCharElementProvider(), "A ", "A");
    }

    private class FirstCharElementProvider extends AbstractRegexElementProvider<String> {

        private FirstCharElementProvider() {
            super("FirstCharElementProvider");
        }

        protected Pattern getRegex() {
            return Pattern.compile("\\w+");
        }

        protected String getElement(MatchResult match) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Element<String> getElementInternal(int line, Matcher match) {
            return new Element<String>(this, line, match.start(), match.start() + 1, match.group().substring(0, 1));
        }

        public Iterator<Action> getActions(String data) {
            throw new UnsupportedOperationException();
        }

        public String getToolTip(String data) {
            throw new UnsupportedOperationException();
        }

    }

}