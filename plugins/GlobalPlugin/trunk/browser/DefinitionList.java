package browser;

import java.util.HashMap;

import org.gjt.sp.jedit.View;

@SuppressWarnings("serial")
public class DefinitionList extends GlobalResultsView {

	static private HashMap<View, DefinitionList> viewMap =
		new HashMap<View, DefinitionList>();
	
	static public DefinitionList instanceFor(View view, String position) {
		DefinitionList instance = viewMap.get(view);
		if (instance == null) {
			instance = new DefinitionList(view);
			viewMap.put(view, instance);
		}
		return instance;
	}
	
	private DefinitionList(final View view) {
		super(view);
	}
	
	@Override
	protected String getParam() {
		return "-x";
	}

}
