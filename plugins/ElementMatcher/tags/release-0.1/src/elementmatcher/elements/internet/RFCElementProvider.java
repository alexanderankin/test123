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

public class RFCElementProvider extends AbstractRegexElementProvider<Integer> {

    private static final Pattern pattern = Pattern.compile("\\bRFC\\s?(\\d+)\\b", Pattern.CASE_INSENSITIVE);
    private final OpenInJEditAction openInJEditAction = new OpenInJEditAction();
    private final Action[] actions = new Action[] { openInJEditAction };
    private final ObjectArrayIterator<Action> actionIterator = new ObjectArrayIterator<Action>(actions);

    public RFCElementProvider() {
        super("RFCElementProvider");
    }

    protected Pattern getRegex() {
        return pattern;
    }

    protected Integer getElement(MatchResult match) {
        try {
            return Integer.parseInt(match.group(1));
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    public Iterator<Action> getActions(Integer data) {
        openInJEditAction.reset(data);
        actionIterator.reset();
        return actionIterator;
    }

    public String getToolTip(Integer data) {
        return "RFC " + data;
    }

    private class OpenInJEditAction extends AbstractAction {

        private int number;

        private void reset(int number) {
            this.number = number;
            putValue(NAME, "Open RFC " + number + " in JEdit");
        }

        public void actionPerformed(ActionEvent e) {
            jEdit.openFile(jEdit.getActiveView(), "http://www.ietf.org/rfc/rfc" + number + ".txt");
        }

    }

}