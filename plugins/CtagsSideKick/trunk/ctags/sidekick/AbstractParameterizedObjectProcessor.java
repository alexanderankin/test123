package ctags.sidekick;

import java.util.Vector;

public abstract class AbstractParameterizedObjectProcessor extends
		AbstractObjectProcessor {

	private Vector<String> params;

	public AbstractParameterizedObjectProcessor(String name, String description) {
		super(name, description);
	}

	@Override
	public AbstractObjectEditor getEditor() {
		return new StringParamEditor(this, "Parameters:");
	}

	@Override
	public Vector<String> getParams() {
		return params;
	}

	@Override
	public void setParams(Vector<String> params) {
		this.params = params;
		parseParams(params);
	}

	protected void parseParams(Vector<String> params) {
	}

	@Override
	public boolean takesParameters() {
		return true;
	}
	
}
