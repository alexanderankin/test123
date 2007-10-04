package ctags.sidekick;

public abstract class AbstractParameterizedObjectProcessor extends
		AbstractObjectProcessor {

	private String params;

	public AbstractParameterizedObjectProcessor(String name, String description) {
		super(name, description);
	}

	@Override
	public AbstractObjectEditor getEditor() {
		return new StringParamEditor(this, "Parameters:");
	}

	@Override
	public String getParams() {
		return params;
	}

	@Override
	public void setParams(String params) {
		this.params = params;
		parseParams(params);
	}

	protected void parseParams(String params) {
	}

	@Override
	public boolean takesParameters() {
		return true;
	}
	
}
