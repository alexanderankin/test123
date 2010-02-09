package ctags.sidekick;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import ctags.sidekick.options.GeneralOptionPane;

@SuppressWarnings("serial")
public class ToolBar extends JPanel {

	private JToolBar toolbar;
	private JMenuBar menubar;
	
	public ToolBar() {
		toolbar = new JToolBar();
		toolbar.setLayout(new GridLayout(0, 1));
		toolbar.setFloatable(false);
		menubar = new JMenuBar();
		toolbar.add(menubar);
		update();
		add(toolbar);
	}
	
	private void updateSelector(final ObjectProcessorManager manager,
			String option, String name)
	{
		ObjectProcessorMenu m = new ObjectProcessorMenu(manager, name);
		if (jEdit.getBooleanProperty(option, true) && m.getItemCount() > 0)
			menubar.add(m);
	}
	
	public void update() {
		Runnable r = new Runnable() {
			public void run() {
				menubar.removeAll();
				updateSelector(MapperManager.getInstance(),
						GeneralOptionPane.SHOW_GROUP_SELECTOR, "Grouping");
				updateSelector(SorterManager.getInstance(),
						GeneralOptionPane.SHOW_SORT_SELECTOR, "Sorting");
				updateSelector(FilterManager.getInstance(),
						GeneralOptionPane.SHOW_FILTER_SELECTOR, "Filtering");
				updateSelector(TextProviderManager.getInstance(),
						GeneralOptionPane.SHOW_TEXT_PROVIDER_SELECTOR, "Text");
				updateSelector(IconProviderManager.getInstance(),
						GeneralOptionPane.SHOW_ICON_PROVIDER_SELECTOR, "Icon");
			}
		};
		if (SwingUtilities.isEventDispatchThread())
			r.run();
		else
			SwingUtilities.invokeLater(r);
	}
	
	static class ObjectProcessorMenu extends JMenu {
		
		private ObjectProcessorManager manager;
		
		public ObjectProcessorMenu(ObjectProcessorManager manager, String name)
		{
			super(name);
			this.manager = manager;
			populate();
		}

		private String getMode()
		{
			View view = jEdit.getActiveView();
			if (view != null) {
				Buffer buffer = view.getBuffer();
				if (buffer != null) {
					Mode mode = buffer.getMode();
					if (mode != null)
						return mode.getName();
				}
			}
			return null;
		}
		
		public void populate()
		{
 			final String mode = getMode();
			Vector<String> items = manager.getProcessorNames();
			for (int i = 0; i < items.size(); i++)
			{
				final String item = items.get(i);
				final IObjectProcessor op = manager.getProcessor(item);
				if (! op.takesParameters())
				{
					JMenuItem mi = new JMenuItem(item);
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							ListObjectProcessor lop =
								manager.createProcessorForMode(mode);
							lop.add(op);
							manager.setProcessorForMode(mode, lop);
							jEdit.getAction(jEdit.getProperty(GeneralOptionPane.PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
						}
					});
					this.add(mi);
				}
			}
		}
	}
}
