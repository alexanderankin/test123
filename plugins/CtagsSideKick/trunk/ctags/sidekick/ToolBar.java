package ctags.sidekick;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.gjt.sp.jedit.jEdit;

import ctags.sidekick.options.GeneralOptionPane;

@SuppressWarnings("serial")
public class ToolBar extends JPanel {

	private HashMap<String, JPanel> selectors;
	private JToolBar toolbar;
	
	public ToolBar() {
		toolbar = new JToolBar();
		toolbar.setLayout(new GridLayout(0, 1));
		toolbar.setFloatable(false);
		selectors = new HashMap<String, JPanel>();
		update();
		add(toolbar);
	}
	
	private void updateSelector(final ObjectProcessorManager manager,
			String option, String name)
	{
		if (! jEdit.getBooleanProperty(option, true))
			return;
		JPanel p = selectors.get(name);
		if (p == null) {
			p = new JPanel();
			selectors.put(name, p);
			p.add(new JLabel(name + ":"));
			final JComboBox cb = new JComboBox();
			cb.addItem("Custom");
			final String mode =
				jEdit.getActiveView().getBuffer().getMode().getName();
			final ListObjectProcessor custom = manager.getProcessorForMode(mode);
			Vector<String> items = manager.getProcessorNames();
			for (int i = 0; i < items.size(); i++)
			{
				String item = items.get(i);
				IObjectProcessor op = manager.getProcessor(item);
				if (! (op instanceof AbstractParameterizedObjectProcessor))
					cb.addItem(item);
			}
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (cb.getSelectedIndex() == 0) {
						manager.setProcessorForMode(mode, custom);
					} else {
						String item = (String) cb.getSelectedItem();
						IObjectProcessor op = manager.getProcessor(item);
						ListObjectProcessor lop = manager.createProcessorForMode(mode);
						lop.add(op);
						manager.setProcessorForMode(mode, lop);
					}
					jEdit.getAction(jEdit.getProperty(GeneralOptionPane.PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
				}
			});
			p.add(cb);
		}
		toolbar.add(p);
	}
	
	public void update() {
		Collection<JPanel> panels = selectors.values();
		Iterator<JPanel> i = panels.iterator();
		while (i.hasNext())
			toolbar.remove(i.next());
		updateSelector(MapperManager.getInstance(),
				GeneralOptionPane.SHOW_GROUP_SELECTOR, "Grouping");
		updateSelector(SorterManager.getInstance(),
				GeneralOptionPane.SHOW_SORT_SELECTOR, "Sorting");
		updateSelector(FilterManager.getInstance(),
				GeneralOptionPane.SHOW_FILTER_SELECTOR, "Filtering");
		updateSelector(TextProviderManager.getInstance(),
				GeneralOptionPane.SHOW_TEXT_PROVIDER_SELECTOR, "Text provider");
	}
}
