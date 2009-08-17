package updater;

/*
 * Interface for jEdit update sources (daily builds / latest release)
 */
public interface UpdateSource {
	static final int BAD_VERSION_STRING = -100;
	// Returns the installed version.
	String getInstalledVersion();
	// Returns the latest version available for download.
	String getLatestVersion();
	/* Compares the latest available version with the installed version.
	 * Returns:
	 * 1 if latest is newer than installed
	 * 0 if latest is same as installed
	 * -1 if latest is older than installed
	 * BAD_VERSION_STRING if unable to compare.
	 */
	int compareVersions(String latest, String installed);
	// Returns the download link of the latest available version.
	String getDownloadLink();
}
