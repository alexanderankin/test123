package ctags.sidekick;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import ctags.sidekick.options.ModeOptionsPane;
import ctags.sidekick.sorters.AccessSorter;
import ctags.sidekick.sorters.AttributeValueSorter;
import ctags.sidekick.sorters.FoldsFirstSorter;
import ctags.sidekick.sorters.ITreeSorter;
import ctags.sidekick.sorters.KindSorter;
import ctags.sidekick.sorters.LineSorter;
import ctags.sidekick.sorters.ListSorter;
import ctags.sidekick.sorters.NameSorter;

public class SorterManager {
	static public String SORTER_OPTION = "options.CtagsSideKick.sorter";
	static private HashMap<String, ITreeSorter> sorters = new HashMap<String, ITreeSorter>();

	// Registers built-in sorters. Called from plugin.start().
	static public void start() {
		register(new AccessSorter());
		register(new AttributeValueSorter(null));
		register(new FoldsFirstSorter());
		register(new KindSorter());
		register(new LineSorter());
		register(new ListSorter());
		register(new NameSorter());
	}
	// Deregisters all sorters. Called from plugin.stop().
	static public void stop() {
		sorters.clear();
	}
	static public ITreeSorter getSorter(String name) {
		return sorters.get(name);
	}
	static public ITreeSorter getSorterForMode(String mode) {
		ListSorter lts = new ListSorter();
		int size = ModeOptionsPane.getIntegerProperty(
			mode, SORTER_OPTION + ".size", 0);
		for (int i = 0; i < size; i++) {
			String prefix = SORTER_OPTION + "." + i + ".";
			String name = ModeOptionsPane.getProperty(mode, prefix + "name");
			if (name == null) {
				error("" + i + "'th sorter not specified for mode " + mode);
				continue;
			}
			ITreeSorter ts = getSorter(name);
			if (ts == null) {
				error("Unknown sorter " + name + " used for mode " + mode);
				continue;
			}
			String params = ModeOptionsPane.getProperty(
				mode, prefix + "params");
			lts.add(ts.getSorter(params));
		}
		return lts;
	}
	static public void setSorterForMode(String mode, ITreeSorter sorter) {
		ListSorter lts = (ListSorter) sorter;
		Vector<ITreeSorter> tss = lts.getComponents();
		ModeOptionsPane.setIntegerProperty(mode, SORTER_OPTION + ".size",
			tss.size());
		for (int i = 0; i < tss.size(); i++) {
			String prefix = SORTER_OPTION + "." + i + ".";
			ITreeSorter ts = tss.get(i);
			ModeOptionsPane.setProperty(mode, prefix + "name", ts.getName());
			String params = ts.getParams();
			if (params != null && params.length() > 0)
				ModeOptionsPane.setProperty(mode, prefix + "params", params);
		}
	}
	static private void register(ITreeSorter sorter) {
		sorters.put(sorter.getName(), sorter);
	}
	static private void error(String err) {
		JOptionPane.showMessageDialog(null, err);
	}
	public static Vector<String> getSorterNames() {
		Vector<String> names = new Vector<String>();
		names.addAll(sorters.keySet());
		return names;
	}

}
