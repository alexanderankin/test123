package dockingFrames;

import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.ThemeFactory;

@SuppressWarnings("serial")
public class DfOptionPane extends AbstractOptionPane {

	private static final String PREFIX = "options.dockingFrames.";
	private static final String THEME_PROP = PREFIX + "theme";
	private static final String THEME_LABEL = THEME_PROP + ".label";
	private JComboBox theme;

	public DfOptionPane() {
		super("dockingFrames");
	}

	public static String getThemeName() {
		return jEdit.getProperty(THEME_PROP, "Eclipse");
	}
	
	@Override
	protected void _init() {
		theme = new JComboBox();
        ThemeFactory[] themes = DockUI.getDefaultDockUI().getThemes();
        for (ThemeFactory t: themes)
        	theme.addItem(t.getName());
        String selected = getThemeName();
        theme.setSelectedItem(selected);
		addComponent(jEdit.getProperty(THEME_LABEL), theme);
	}

	@Override
	protected void _save() {
		jEdit.setProperty(THEME_PROP, theme.getSelectedItem().toString());
		jEdit.propertiesChanged();
	}

}
