package elementmatcher;

import javax.swing.Action;
import java.util.Iterator;

public class Element<T> {

    public final ElementProvider<T> provider;
    public final int line;
    public final int lineOffset0;
    public final int lineOffset1;
    public final T data;

    public Element(ElementProvider<T> provider, int line, int lineOffset0, int lineOffset1, T data) {
        this.provider = provider;
        this.line = line;
        this.lineOffset0 = lineOffset0;
        this.lineOffset1 = lineOffset1;
        this.data = data;
    }

    public Iterator<Action> getActions() {
        return provider.getActions(data);
    }

    public String getToolTip() {
        return provider.getToolTip(data);
    }

}