package elementmatcher;

import org.gjt.sp.jedit.jEdit;
import java.awt.Color;
import java.text.MessageFormat;

public abstract class AbstractElementProvider<T> implements ElementProvider<T> {

    public static final String ENABLED_PROPERTY = "enabled";
    public static final String COLOR_PROPERTY = "color";
    public static final String UNDERLINE_PROPERTY = "underline";

    private final String name;
    private boolean enabled;
    private Color color;
    private boolean underline;

    protected AbstractElementProvider(String name) {
        this.name = name;
        enabled = jEdit.getBooleanProperty(getOptionsPrefix() + ENABLED_PROPERTY, true);
        color = jEdit.getColorProperty(getOptionsPrefix() + COLOR_PROPERTY, Color.BLUE);
        underline = jEdit.getBooleanProperty(getOptionsPrefix() + UNDERLINE_PROPERTY, true);
    }

    protected String getOptionsPrefix() {
        return MessageFormat.format("{0}providers.{1}.", ElementMatcherPlugin.OPTION_PREFIX, name);
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        jEdit.setBooleanProperty(getOptionsPrefix() + ENABLED_PROPERTY, true);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        jEdit.setColorProperty(getOptionsPrefix() + COLOR_PROPERTY, color);
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
        jEdit.setBooleanProperty(getOptionsPrefix() + UNDERLINE_PROPERTY, underline);
    }

}