//{{{ imports
import java.util.*;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.GUIUtilities;
//}}}

public class GfxViewPlugin extends EditPlugin {
    public static final String NAME = "gfxview";
    public static final String OPTION_PREFIX = "gfxview.options.";

	// 4.1 API Interface
	public void createMenuItems(Vector menuItems) {
			menuItems.addElement(GUIUtilities.loadMenu("gfview.menu"));
	}
	public void createOptionPanes(OptionsDialog od) {
        od.addOptionPane(new GfxViewOptionPane());
	}
}

/* :folding=explicit:tabSize=2:indentSize=2: */