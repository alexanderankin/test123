package elementmatcher.elements.internet;

import elementmatcher.AbstractRegexElementProvider;
import org.gjt.sp.jedit.jEdit;
import org.apache.commons.collections15.iterators.ObjectArrayIterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class URNXMLElementProvider extends AbstractRegexElementProvider<String> {

    private static final Pattern PATTERN = Pattern.compile("\\burn:ietf:params:xml:([a-z\\-:]+)\\b");
    private final OpenInJEditAction openInJEditAction = new OpenInJEditAction();
    private final Action[] actions = new Action[] { openInJEditAction };
    private final ObjectArrayIterator<Action> actionIterator = new ObjectArrayIterator<Action>(actions);

    public URNXMLElementProvider() {
        super("URNXMLElementProvider");
    }

    protected Pattern getRegex() {
        return PATTERN;
    }

    protected String getElement(MatchResult match) {
        return match.group(1);
    }

    public Iterator<Action> getActions(String data) {
        openInJEditAction.reset(data);
        actionIterator.reset();
        return actionIterator;
    }

    public String getToolTip(String data) {
        return "IANA XML: urn:ietf:params:xml:" + data;
    }

    private class OpenInJEditAction extends AbstractAction {

        private String uriTail;

        private void reset(String uriTail) {
            this.uriTail = uriTail;
            putValue(NAME, "Open urn:ietf:params:xml:" + uriTail + " in jEdit");
        }

        public void actionPerformed(ActionEvent evt) {
            jEdit.openFile(jEdit.getActiveView(), "http://www.iana.org/assignments/xml-registry/" + uriTail.replace(':', '/') + ".txt");
        }

    }

}