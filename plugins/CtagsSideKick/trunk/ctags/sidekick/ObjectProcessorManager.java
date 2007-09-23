package ctags.sidekick;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import ctags.sidekick.options.ModeOptionsPane;

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
		int size = ModeOptionsPane.getIntegerProperty(
			mode, optionPath + ".size", 0);
		for (int i = 0; i < size; i++) {
			String prefix = optionPath + "." + i + ".";
			String name = ModeOptionsPane.getProperty(mode, prefix + "name");
			if (name == null) {
				error("" + i + "'th" + type + "not specified for mode " + mode);
				continue;
			}
			IObjectProcessor p = getProcessor(name);
			if (p == null) {
				error("Unknown" + type + name + " used for mode " + mode);
				continue;
			}
			String params = ModeOptionsPane.getProperty(mode, prefix + "params");
			p = p.getClone();
			p.setParams(params);
			lop.add(p);
		}
		return lop;
	}

	public void setProcessorForMode(String mode, ListObjectProcessor lop) {
		Vector<IObjectProcessor> ops = lop.getProcessors();
		String optionPath = getProcessorOptionPath();
		ModeOptionsPane.setIntegerProperty(mode, optionPath + ".size", ops.size());
		for (int i = 0; i < ops.size(); i++) {
			String prefix = optionPath + "." + i + ".";
			IObjectProcessor p = ops.get(i);
			ModeOptionsPane.setProperty(mode, prefix + "name", p.getName());
			String params = p.getParams();
			if (params != null && params.length() > 0)
				ModeOptionsPane.setProperty(mode, prefix + "params", params);
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
