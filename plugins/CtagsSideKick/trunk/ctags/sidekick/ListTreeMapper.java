package ctags.sidekick;
import java.util.Vector;

public class ListTreeMapper extends AbstractTreeMapper {

	Vector <ITreeMapper> mappers; 
	
	public ListTreeMapper() {
		mappers = new Vector<ITreeMapper>();
	}
	
	public void add(ITreeMapper mapper) {
		mappers.add(mapper);
	}
	
	public Vector<Object> getPath(Tag tag) {
		Vector<Object> path = new Vector<Object>();
		for (int i = 0; i < mappers.size(); i++) {
			ITreeMapper mapper = mappers.get(i);
			path.addAll(mapper.getPath(tag));
		}
		return path;
	}

	public void setLang(String lang) {
		for (int i = 0; i < mappers.size(); i++) {
			ITreeMapper mapper = mappers.get(i);
			mapper.setLang(lang);
		}
	}
	public Vector <ITreeMapper> getComponents() {
		return mappers;
	}

	public String getName() {
		return "Composite";
	}
}
