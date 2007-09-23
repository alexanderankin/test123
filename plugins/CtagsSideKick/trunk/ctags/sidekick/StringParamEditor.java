package ctags.sidekick;

import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class StringParamEditor extends AbstractObjectEditor {

	private JTextField paramsTF;
	
	public StringParamEditor(IObjectProcessor processor, String label) {
		super(processor);
		add(new JLabel(label));
		paramsTF = new JTextField(40);
		add(paramsTF);
	}

	@Override
	public void save() {
		processor.setParams(paramsTF.getText());
	}

}
