package ctags.sidekick;

import java.util.Vector;

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
		Vector<String> params = new Vector<String>();
		params.add(paramsTF.getText());
		processor.setParams(params);
	}

}
