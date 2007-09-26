package ctags.sidekick.options;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ctags.sidekick.FilterManager;
import ctags.sidekick.MapperManager;
import ctags.sidekick.SorterManager;

@SuppressWarnings("serial")
public class ModeOptionsPane extends ModeOptionPanelGroup {

	public ModeOptionsPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		CtagsCmdOptionsPane invocationPane = new CtagsCmdOptionsPane();
		invocationPane.setMaximumSize(invocationPane.getPreferredSize());
		invocationPane.setAlignmentX(LEFT_ALIGNMENT);
		add(invocationPane);
		addModePanel(invocationPane);
		
		JPanel optionPanes = new JPanel(new GridLayout(1, 0));
		optionPanes.setAlignmentX(LEFT_ALIGNMENT);
		add(optionPanes);

		ObjectProcessorListEditor mapperPane =
			new ObjectProcessorListEditor(MapperManager.getInstance());
		optionPanes.add(mapperPane);
		addModePanel(mapperPane);
		
		ObjectProcessorListEditor sorterPane =
			new ObjectProcessorListEditor(SorterManager.getInstance());
		optionPanes.add(sorterPane);
		addModePanel(sorterPane);
		
		ObjectProcessorListEditor filterPane =
			new ObjectProcessorListEditor(FilterManager.getInstance());
		optionPanes.add(filterPane);
		addModePanel(filterPane);
	}
}
