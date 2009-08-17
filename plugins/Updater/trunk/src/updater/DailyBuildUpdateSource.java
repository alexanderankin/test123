package updater;

import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

public class DailyBuildUpdateSource implements UpdateSource
{
	private static final String INSTALLED_BUILD_PROP = "updater.dailyBuildInstalledVersion";
	private static String VERSION_PAGE_PROP = "updater.dailyBuildVersionPage";
	private static final String VERSION_CHECK_PATTERN_PROP = "updater.dailyBuildVersionCheckPattern";
	private static final String DOWNLOAD_LINK_PATTERN_PROP = "updater.dailyBuildDownloadLink";

	public int compareVersions(String latest, String installed)
	{
		if (installed.length() == 0)
			return 1;
		if (latest.length() == 0)
			return BAD_VERSION_STRING;
		String [] latestVer = latest.split("[\\-_])");
		String [] installedVer = latest.split("[\\-_])");
		return UpdaterPlugin.compareNumericVersionArray(latestVer, installedVer);
	}

	public String getDownloadLink()
	{
		Vector<String> versions = UrlUtils.extractMultiOccurrencePattern(
				VERSION_PAGE_PROP, DOWNLOAD_LINK_PATTERN_PROP);
		if (versions.size() == 0)
			return null;
		return jEdit.getProperty(VERSION_PAGE_PROP) + versions.lastElement();
	}

	public String getInstalledVersion()
	{
		return jEdit.getProperty(INSTALLED_BUILD_PROP, "");
	}

	public String getLatestVersion()
	{
		Vector<String> versions = UrlUtils.extractMultiOccurrencePattern(
			VERSION_PAGE_PROP, VERSION_CHECK_PATTERN_PROP);
		if (versions.size() == 0)
			return null;
		return versions.lastElement();
	}

}
