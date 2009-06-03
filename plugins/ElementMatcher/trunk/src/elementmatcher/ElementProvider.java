package elementmatcher;

import javax.swing.Action;
import javax.swing.text.Segment;
import java.util.Iterator;
import java.awt.Color;

public interface ElementProvider<T> {

    public static String ENABLED_PROPERTY = "enabled";
    public static String COLOR_PROPERTY = "color";
    public static String UNDERLINE_PROPERTY = "underline";

    public Iterator<Element<T>> getElements(int line, Segment segment);

    public Iterator<Action> getActions(T data);

    public String getToolTip(T data);

    public boolean isEnabled();

    public void setEnabled(boolean enabled);

    public Color getColor();

    public void setColor(Color color);

    public boolean isUnderline();

    public void setUnderline(boolean underline);

    public String getName();

    public String getOptionsPrefix();

}