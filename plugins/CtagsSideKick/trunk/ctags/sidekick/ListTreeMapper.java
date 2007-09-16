package ctags.sidekick;
import java.util.Vector;

import org.gjt.sp.jedit.jEdit;



public class ListTreeMapper extends AbstractTreeMapper {

	Vector <ITreeMapper> mappers; 
	
	public ListTreeMapper(String name) {
		super(name);
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

	public void save(String name) {
		jEdit.setIntegerProperty(
			MapperManager.MAPPER_OPTION + "." + name + ".size",
			mappers.size());
		for (int i = 0; i < mappers.size(); i++) {
			ITreeMapper mapper = mappers.get(i);
			mapper.save(name + "." + i);
		}
	}
}
