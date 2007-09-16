package ctags.sidekick;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.jEdit;

public class MapperManager {
	
	static public String MAPPER_OPTION = "options.CtagsSideKick.mapper";
	static private HashMap<String, ITreeMapper> mappers = new HashMap<String, ITreeMapper>();

	// Registers built-in mappers. Called from plugin.start().
	static public void start() {
		registerBuiltIn();
	}
	// Deregisters all mappers. Called from plugin.stop().
	static public void stop() {
		mappers.clear();
	}
	private static void registerBuiltIn() {
		register(new AttributeValueTreeMapper());
		register(new FlatNamespaceTreeMapper());
		register(new KindTreeMapper());
		register(new NamespaceTreeMapper());
	}
	static public void propertiesChanged() {
		stop();
		start();
	}
	static public ITreeMapper getMapper(String name) {
		ITreeMapper mapper = mappers.get(name);
		if (mapper == null)
			mapper = load(name);
		return mapper;
	}
	public static ITreeMapper getDefaultMapper() {
		String mapper = jEdit.getProperty(GeneralOptionPane.MAPPER);
		return getMapper(mapper);
	}
	static public ITreeMapper getMapperForMode(String mode) {
		String mapper = ModeOptionsPane.getProperty(mode, GeneralOptionPane.MAPPER);
		return getMapper(mapper);
	}
	static public void setMapperForMode(String mode, ITreeMapper mapper) {
		String name = getMapperName(mapper);
		ModeOptionsPane.setProperty(mode, GeneralOptionPane.MAPPER, name);
		save(name, mapper);
	}
	static public void setDefaultMapper(ITreeMapper mapper) {
		String name = getMapperName(mapper);
		jEdit.setProperty(GeneralOptionPane.MAPPER, name);
		save(name, mapper);
	}
	private static String getMapperName(ITreeMapper mapper) {
		String name = mapper.getName();
		if (name == null || name.length() == 0)
			name = "current";
		return name;
	}
	static public void register(String name, ITreeMapper mapper) {
		mappers.put(name, mapper);
	}
	static private void register(ITreeMapper mapper) {
		mappers.put(mapper.getName(), mapper);
	}
	static private void error(String err) {
		JOptionPane.showMessageDialog(null, err);
	}
	static private void save(String name, ITreeMapper mapper) {
		mapper.save(name);
	}
	static private ITreeMapper load(String mapper) {
		ITreeMapper tm = loadParameterizedMapper(mapper);
		if (tm != null)
			mappers.put(mapper, tm);
		else {
			tm = loadListMapper(mapper);
			if (tm != null)
				mappers.put(mapper, tm);
			else
				error("Could not load mapper " + mapper);
		}
		return tm;
	}
	private static ITreeMapper loadListMapper(String mapper) {
		ListTreeMapper ltm = new ListTreeMapper(mapper);
		int size = jEdit.getIntegerProperty(
			MAPPER_OPTION + "." + mapper + ".size", 0);
		ITreeMapper tm;
		for (int i = 0; i < size; i++) {
			tm = loadParameterizedMapper(mapper + "." + i);
			if (tm != null)
				ltm.add(tm);
		}
		return ltm;
	}
	private static ITreeMapper loadParameterizedMapper(String prefix) {
		String base = jEdit.getProperty(MAPPER_OPTION + "." + prefix + ".base");
		if (base != null) {
			String params = jEdit.getProperty(MAPPER_OPTION + "." + prefix + ".params");
			ITreeMapper baseMapper = getMapper(base);
			if (baseMapper != null)
				return baseMapper.getMapper(params);
			else
				error("Base mapper " + base + " for paramed mapper " + prefix + " not found");
		}
		return null;
	}
	public static Vector<String> getMapperNames() {
		Vector<String> names = new Vector<String>();
		names.addAll(mappers.keySet());
		return names;
	}
}
