package rename;

import java.util.*;

import org.gjt.sp.jedit.*;

/**
 *  Description of the Class
 *
 * @author     mace0031
 * @created    February 2, 2004
 * @version    $Revision$
 * @updated    $Date$ by $Author$
 */
public class OptionPaneOptionPane extends PropertyOptionPane {
	public OptionPaneOptionPane() {
		super("OptionPane", "label");
		ArrayList panes = new ArrayList();

		// Query plugins for option panes
		EditPlugin[] plugins = jEdit.getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			EditPlugin ep = plugins[i];
			if (ep instanceof EditPlugin.Broken) {
				continue;
			}

			String className = ep.getClassName();
			if (jEdit.getProperty("plugin." + className + ".activate") != null) {
				String optionPane = jEdit.getProperty("plugin." + className + ".option-pane");
				if (optionPane != null) {
					panes.add(optionPane);
					pluginsGroup.addOptionPane(optionPane);
				} else {
					String options = jEdit.getProperty("plugin." + className + ".option-group");
					if (options != null) {
						String[] optionPanes = options.split(" ");
						for (int i = 0; i < optionPanes.length; i++) {
							if (optionPanes.equals("-"))
								continue;
							
							panes.add(optionPanes[i]);
						}
					}
				}
			}
		}
		
		idArray = new String[panes.size()];

	}
}

