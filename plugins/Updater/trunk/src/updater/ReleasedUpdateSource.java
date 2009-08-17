package updater;

import org.gjt.sp.jedit.jEdit;

public class ReleasedUpdateSource implements UpdateSource
{
	private static String VERSION_PAGE_PROP = "updater.versionPage";
	private static final String VERSION_CHECK_PATTERN_PROP = "updater.versionCheckPattern";
	private static final String DOWNLOAD_PAGE_PROP = "updater.downloadPage";
	private static final String DOWNLOAD_LINK_PATTERN_PROP = "updater.downloadLinkPattern";

	public int compareVersions(String latest, String installed)
	{
		String [] latestVer = latest.split("\\.");
		String [] installedVer = installed.split("\\.");
		return UpdaterPlugin.compareNumericVersionArray(latestVer, installedVer);
	}

	public String getDownloadLink()
	{
		return UrlUtils.extractSingleOccurrencePattern(DOWNLOAD_PAGE_PROP, DOWNLOAD_LINK_PATTERN_PROP);
	}

	public String getInstalledVersion()
	{
		return jEdit.getBuild();
	}

	public String getLatestVersion()
	{
		return UrlUtils.extractSingleOccurrencePattern(VERSION_PAGE_PROP, VERSION_CHECK_PATTERN_PROP);
	}

}
