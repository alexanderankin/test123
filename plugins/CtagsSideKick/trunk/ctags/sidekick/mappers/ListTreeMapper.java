package ctags.sidekick.mappers;
import java.util.Vector;

import ctags.sidekick.IObjectProcessor;
import ctags.sidekick.ListObjectProcessor;
import ctags.sidekick.Tag;

public class ListTreeMapper extends ListObjectProcessor implements ITreeMapper {

	private static final String NAME = "Composite";
	private static final String DESCRIPTION =
		"A list of tree mappers, each adds its own nodes to the tree path.";
	
	public ListTreeMapper() {
		super(NAME, DESCRIPTION);
	}
	
	public Vector<Object> getPath(Tag tag) {
		Vector<Object> path = new Vector<Object>();
		Vector<IObjectProcessor> processors = getProcessors();
		for (int i = 0; i < processors.size(); i++) {
			ITreeMapper mapper = (ITreeMapper) processors.get(i);
			path.addAll(mapper.getPath(tag));
		}
		return path;
	}

	public void setLang(String lang) {
		Vector<IObjectProcessor> processors = getProcessors();
		for (int i = 0; i < processors.size(); i++) {
			ITreeMapper mapper = (ITreeMapper) processors.get(i);
			mapper.setLang(lang);
		}
	}

	public CollisionHandler getCollisionHandler() {
		Vector<IObjectProcessor> processors = getProcessors();
		CollisionHandler collisionHandler = null;
		for (int i = processors.size() - 1; i >= 0; i--) {
			ITreeMapper mapper = (ITreeMapper) processors.get(i);
			collisionHandler = mapper.getCollisionHandler();
			if (collisionHandler != null)
				break;
		}
		return collisionHandler;
	}
	
	public IObjectProcessor getClone() {
		return new ListTreeMapper();
	}

}
