package ctags.sidekick;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import ctags.sidekick.options.SideKickModeOptionsPane;

public abstract class ObjectProcessorManager {
	
	protected HashMap<String, IObjectProcessor> processors;
	
	public void start() {
		processors = new HashMap<String, IObjectProcessor>();
		registerBuiltProcessors();
	}
	
	public void stop() {
		processors.clear();
		processors = null;
	}
	
	public void addProcessor(IObjectProcessor processor) {
		processors.put(processor.getName(), processor);
	}
	
	public IObjectProcessor getProcessor(String name) {
		return processors.get(name);
	}

	public ListObjectProcessor createProcessorForMode(String mode) {
		return getListObjectProcessor();
	}
	
	public ListObjectProcessor getProcessorForMode(String mode) {
		ListObjectProcessor lop = getListObjectProcessor();
		String optionPath = getProcessorOptionPath();
		String type = " " + getProcessorTypeName() + " ";
		int size = SideKickModeOptionsPane.getIntegerProperty(
			mode, optionPath + ".size", 0);
		for (int i = 0; i < size; i++) {
			String prefix = optionPath + "." + i + ".";
			String name = SideKickModeOptionsPane.getProperty(mode, prefix + "name");
			if (name == null) {
				error("" + i + "'th" + type + "not specified for mode " + mode);
				continue;
			}
			IObjectProcessor p = getProcessor(name);
			if (p == null) {
				error("Unknown" + type + name + " used for mode " + mode);
				continue;
			}
			String paramBase = prefix + "params.";
			int nParams = SideKickModeOptionsPane.getIntegerProperty(mode,
					paramBase + "size", 0);
			Vector<String> params = new Vector<String>();
			for (int j = 0; j < nParams; j++)
			{
				String param = SideKickModeOptionsPane.getProperty(mode, paramBase + j);
				params.add(param);
			}
			p = p.getClone();
			p.setParams(params);
			lop.add(p);
		}
		return lop;
	}

	public boolean hasProcessorForMode(String mode) {
		return SideKickModeOptionsPane.modePropertyExists(
			mode, getProcessorOptionPath() + ".size");
	}
	
	public void resetProcessorForMode(String mode) {
		String optionPath = getProcessorOptionPath();
		String sizeOption = optionPath + ".size";
		if (! SideKickModeOptionsPane.modePropertyExists(mode, sizeOption))
			return;
		int size = SideKickModeOptionsPane.getIntegerProperty(mode, sizeOption, 0);
		SideKickModeOptionsPane.clearModeProperty(mode, sizeOption);
		for (int i = 0; i < size; i++) {
			String prefix = optionPath + "." + i + ".";
			SideKickModeOptionsPane.clearModeProperty(mode, prefix + "name");
			SideKickModeOptionsPane.clearModeProperty(mode, prefix + "params");
		}
	}
	
	public void setProcessorForMode(String mode, ListObjectProcessor lop) {
		Vector<IObjectProcessor> ops = lop.getProcessors();
		String optionPath = getProcessorOptionPath();
		SideKickModeOptionsPane.setIntegerProperty(mode, optionPath + ".size", ops.size());
		for (int i = 0; i < ops.size(); i++) {
			String prefix = optionPath + "." + i + ".";
			IObjectProcessor p = ops.get(i);
			SideKickModeOptionsPane.setProperty(mode, prefix + "name", p.getName());
			Vector<String> params = p.getParams();
			if (params != null && params.size() > 0)
			{
				String paramBase = prefix + "params.";
				int nParams = params.size();
				SideKickModeOptionsPane.setIntegerProperty(mode, paramBase + "size", nParams);
				for (int j = 0; j < nParams; j++)
					SideKickModeOptionsPane.setProperty(mode, paramBase + j, params.get(j));
			}
		}
	}

	public Vector<String> getProcessorNames() {
		return new Vector<String>(processors.keySet());
	}

	// Returns the name of the processor type ("mapper" / "sorter" / ...)
	public abstract String getProcessorTypeName();

	// Returns the base path to the processor (e.g. options.CtagsSideKick.mapper)
	abstract protected String getProcessorOptionPath();

	// Returns a list processor into which to read mode processors
	abstract protected ListObjectProcessor getListObjectProcessor();

	// Registers the built-in processors
	abstract protected void registerBuiltProcessors();
	
	private void error(String err) {
		JOptionPane.showMessageDialog(null, err);
	}

}
