package make;

public interface BuildfileProvider {
	/**
	 * Returns true if the provided filename is a valid name
	 * for this buildfile. This is only a suggestion, though,
	 * and is only used in MakePlugin.getBuildfileForPath().
	 */
	public boolean accept(String filename);
	
	public Buildfile createFor(String dir, String filename);
}
