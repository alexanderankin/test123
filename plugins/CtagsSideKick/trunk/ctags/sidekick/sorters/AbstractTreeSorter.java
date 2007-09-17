package ctags.sidekick.sorters;

public abstract class AbstractTreeSorter implements ITreeSorter {

	public String getParams() {
		return null;
	}

	public ITreeSorter getSorter(String params) {
		return this;
	}

	public String toString() {
		return getName();
	}
}
