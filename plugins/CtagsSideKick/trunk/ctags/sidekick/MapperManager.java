package ctags.sidekick;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import ctags.sidekick.mappers.AttributeValueTreeMapper;
import ctags.sidekick.mappers.FlatNamespaceTreeMapper;
import ctags.sidekick.mappers.ITreeMapper;
import ctags.sidekick.mappers.KindTreeMapper;
import ctags.sidekick.mappers.ListTreeMapper;
import ctags.sidekick.mappers.NamespaceTreeMapper;
import ctags.sidekick.options.ModeOptionsPane;

public class MapperManager {
	
	static public String MAPPER_OPTION = "options.CtagsSideKick.mapper";
	static private HashMap<String, ITreeMapper> mappers = new HashMap<String, ITreeMapper>();

	// Registers built-in mappers. Called from plugin.start().
	static public void start() {
		register(new AttributeValueTreeMapper(null));
		register(new FlatNamespaceTreeMapper());
		register(new KindTreeMapper());
		register(new NamespaceTreeMapper());
	}
	// Deregisters all mappers. Called from plugin.stop().
	static public void stop() {
		mappers.clear();
	}
	static public ITreeMapper getMapper(String name) {
		return mappers.get(name);
	}
	static public ITreeMapper getMapperForMode(String mode) {
		ListTreeMapper ltm = new ListTreeMapper();
		int size = ModeOptionsPane.getIntegerProperty(
			mode, MAPPER_OPTION + ".size", 0);
		for (int i = 0; i < size; i++) {
			String prefix = MAPPER_OPTION + "." + i + ".";
			String name = ModeOptionsPane.getProperty(mode, prefix + "name");
			if (name == null) {
				error("" + i + "'th mapper not specified for mode " + mode);
				continue;
			}
			ITreeMapper tm = getMapper(name);
			if (tm == null) {
				error("Unknown mapper " + name + " used for mode " + mode);
				continue;
			}
			String params = ModeOptionsPane.getProperty(
				mode, prefix + "params");
			ltm.add(tm.getMapper(params));
		}
		return ltm;
	}
	static public void setMapperForMode(String mode, ITreeMapper mapper) {
		ListTreeMapper ltm = (ListTreeMapper) mapper;
		Vector<ITreeMapper> tms = ltm.getComponents();
		ModeOptionsPane.setIntegerProperty(mode, MAPPER_OPTION + ".size",
			tms.size());
		for (int i = 0; i < tms.size(); i++) {
			String prefix = MAPPER_OPTION + "." + i + ".";
			ITreeMapper tm = tms.get(i);
			ModeOptionsPane.setProperty(mode, prefix + "name", tm.getName());
			String params = tm.getParams();
			if (params != null && params.length() > 0)
				ModeOptionsPane.setProperty(mode, prefix + "params", params);
		}
	}
	static private void register(ITreeMapper mapper) {
		mappers.put(mapper.getName(), mapper);
	}
	static private void error(String err) {
		JOptionPane.showMessageDialog(null, err);
	}
	public static Vector<String> getMapperNames() {
		Vector<String> names = new Vector<String>();
		names.addAll(mappers.keySet());
		return names;
	}
}
