package ctags.sidekick;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class ModeOptionsPane extends sidekick.ModeOptionsPane {

	static private final String CTAGS_MODE_OPTIONS_LABEL = "options.CtagsSideKick.mode.ctags_options_label";
	
	private JTextField ctagsOptions;
	private JComboBox mapper;

	public ModeOptionsPane() 
	{
		super("CtagsSideKick.mode");
	}
		
	protected void _init() {
		JPanel panel = new JPanel();
		JLabel optionsLabel = new JLabel(jEdit.getProperty(CTAGS_MODE_OPTIONS_LABEL));
		ctagsOptions = new JTextField(30);
		panel.add(optionsLabel);
		panel.add(ctagsOptions);
		addComponent(panel);
		Vector<String> mappers = new Vector<String>();
		mappers.add(jEdit.getProperty(OptionPane.KIND_MAPPER_NAME));
		mappers.add(jEdit.getProperty(OptionPane.NAMESPACE_MAPPER_NAME));
		mappers.add(jEdit.getProperty(OptionPane.FLAT_NAMESPACE_MAPPER_NAME));
		JPanel mapperPanel = new JPanel();
		JLabel mapperLabel = new JLabel(jEdit.getProperty(OptionPane.MAPPER +
				OptionPane.LABEL));
		mapperPanel.add(mapperLabel);
		mapper = new JComboBox(mappers);
		mapperPanel.add(mapper);
		addComponent(mapperPanel);
		_load();
	}
	
	protected void _load() 
	{
		ctagsOptions.setText(getProperty(Plugin.CTAGS_MODE_OPTIONS));
		mapper.setSelectedItem(getProperty(OptionPane.MAPPER));
	}
	
	protected void _save() 
	{
		setProperty(Plugin.CTAGS_MODE_OPTIONS, ctagsOptions.getText());
		setProperty(OptionPane.MAPPER, (String)mapper.getSelectedItem());
		jEdit.getAction(jEdit.getProperty(OptionPane.PARSE_ACTION_PROP)).invoke(jEdit.getActiveView());
	}

	protected void _reset()
	{
		clearModeProperty(Plugin.CTAGS_MODE_OPTIONS);
		clearModeProperty(OptionPane.MAPPER);
	}	

}
