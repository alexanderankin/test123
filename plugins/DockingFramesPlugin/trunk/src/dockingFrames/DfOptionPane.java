package dockingFrames;

import javax.swing.JComboBox;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.layout.ThemeMap;

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
		return jEdit.getProperty(THEME_PROP, "eclipse");
	}
	
	@Override
	protected void _init() {
		View view = jEdit.getActiveView();
		if (view != null)
		{
			DockableWindowManager dwm = view.getDockableWindowManager();
			theme = null;
			if (dwm instanceof DfWindowManager)
			{
				DfWindowManager dfm = (DfWindowManager) dwm;
				CControl control = dfm.getControl();
				if (control != null)
				{
					ThemeMap themes = control.getThemes();
					theme = new JComboBox();
			        for (int i = 0; i < themes.size(); i++)
			        	theme.addItem(themes.getKey(i));
			        String selected = getThemeName();
			        theme.setSelectedItem(selected);
					addComponent(jEdit.getProperty(THEME_LABEL), theme);
				}
			}
		}
	}

	@Override
	protected void _save() {
		if (theme != null)
			jEdit.setProperty(THEME_PROP, theme.getSelectedItem().toString());
		jEdit.propertiesChanged();
	}

}
