package elementmatcher.elements.internet;

import elementmatcher.AbstractRegexElementProvider;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

// actions: ping, open 'http://<hostname>'
public class HostElementProvider extends AbstractRegexElementProvider<String> {

    private static final Pattern PATTERN = Pattern.compile(
            "\\b(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+(?:[a-z]{2}|com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum)\\b");

    public HostElementProvider() {
        super("HostElementProvider");
    }

    protected Pattern getRegex() {
        return PATTERN;
    }

    protected String getElement(MatchResult match) {
        return match.group();
    }

    public Iterator<Action> getActions(String data) {
        throw new UnsupportedOperationException();
    }

    public String getToolTip(String data) {
        return "Host: " + data;
    }

    private class PingAction extends AbstractAction {

        private String hostname;

        private void reset(String hostname) {
            this.hostname = hostname;
        }

        public void actionPerformed(ActionEvent evt) {
            throw new UnsupportedOperationException();
        }

    }

}