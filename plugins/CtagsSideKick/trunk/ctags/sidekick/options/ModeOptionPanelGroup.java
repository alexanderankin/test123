package ctags.sidekick.options;

import java.util.Vector;

@SuppressWarnings("serial")
public class ModeOptionPanelGroup extends ModeOptionPanel {

	Vector<ModeOptionPanel> panels;
	Vector<Object> props;
	
	public ModeOptionPanelGroup() {
		panels = new Vector<ModeOptionPanel>();
		props = new Vector<Object>();
	}
	
	protected void addModePanel(ModeOptionPanel panel) {
		panels.add(panel);
	}
	
	@Override
	protected Object createModeProps(String mode) {
		props = new Vector<Object>();
		for (int i = 0; i < panels.size(); i++)
			props.add(panels.get(i).createModeProps(mode));
		return props;
	}

	@Override
	protected void resetModeProps(String mode) {
		for (int i = 0; i < panels.size(); i++)
			panels.get(i).resetModeProps(mode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void saveModeProps(String mode, Object props) {
		Vector<Object> v = (Vector<Object>) props;
		for (int i = 0; i < panels.size(); i++)
			panels.get(i).saveModeProps(mode, v.get(i));

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updatePropsFromUI(Object props) {
		Vector<Object> v = (Vector<Object>) props;
		for (int i = 0; i < panels.size(); i++)
			panels.get(i).updatePropsFromUI(v.get(i));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateUIFromProps(Object props) {
		Vector<Object> v = (Vector<Object>) props;
		for (int i = 0; i < panels.size(); i++)
			panels.get(i).updateUIFromProps(v.get(i));
	}
}
