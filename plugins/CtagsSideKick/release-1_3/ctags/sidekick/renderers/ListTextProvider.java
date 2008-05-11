package ctags.sidekick.renderers;

import java.util.Vector;

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.ListObjectProcessor;
import ctags.sidekick.Tag;

public class ListTextProvider extends ListObjectProcessor implements
		ITextProvider {

	private static final String NAME = "Composite";
	private static final String DESCRIPTION =
		"A list of tag text providers, each appending its own string.";
	
	public ListTextProvider() {
		super(NAME, DESCRIPTION);
	}

	public String getString(Tag tag) {
		StringBuffer buf = new StringBuffer();
		Vector<IObjectProcessor> processors = getProcessors();
		if (processors.size() == 0)
			processors.add(new NameAndSignatureTextProvider());
		for (int i = 0; i < processors.size(); i++) {
			ITextProvider provider = (ITextProvider) processors.get(i);
			buf.append(provider.getString(tag));
		}
		return buf.toString();
	}

	public IObjectProcessor getClone() {
		return new ListTextProvider();
	}

}
