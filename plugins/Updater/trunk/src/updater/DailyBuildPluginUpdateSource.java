package updater;

import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

public class DailyBuildPluginUpdateSource implements UpdateSource
{
	private static final String INSTALLED_BUILD_PROP = "updater.dailyBuildInstalledVersion";
	private static String VERSION_PAGE_PROP = "updater.dailyBuildPluginPage";
	private static final String VERSION_CHECK_PATTERN_PROP = "updater.dailyBuildPluginVersionCheckPattern";
	private static final String DOWNLOAD_LINK_PATTERN_PROP = "updater.dailyBuildPluginPageLinkPattern";
	private static final String DOWNLOAD_LINK_PREFIX_PROP = "updater.dailyBuildDownloadLinkPrefix";
	private String plugin;

	public DailyBuildPluginUpdateSource(String plugin)
	{
		this.plugin = plugin;
	}

	public int compareVersions(String latest, String installed)
	{
		if (installed.length() == 0)
			return 1;
		if (latest.length() == 0)
			return BAD_VERSION_STRING;
		String [] latestVer = latest.split("\\D+");
		String [] installedVer = installed.split("\\D+");
		return UpdaterPlugin.compareNumericVersionArray(latestVer, installedVer);
	}

	public String getDownloadLink()
	{
		String versionPage = jEdit.getProperty(VERSION_PAGE_PROP,
			new String[] {plugin});
		Vector<String> versions = UrlUtils.extractMultiOccurrencePattern(
			versionPage, jEdit.getProperty(VERSION_CHECK_PATTERN_PROP,
				new String[] {plugin}));
		if (versions.size() == 0)
			return null;
		String downloadPage = versionPage + versions.lastElement();
		String link = UrlUtils.extractSingleOccurrencePattern(downloadPage,
			jEdit.getProperty(DOWNLOAD_LINK_PATTERN_PROP, new String[] {plugin}));
		if (link == null)
			return null;
		return jEdit.getProperty(DOWNLOAD_LINK_PREFIX_PROP) + link;
	}

	public String getInstalledVersion()
	{
		return jEdit.getProperty(INSTALLED_BUILD_PROP + "." + plugin, "");
	}

	public String getLatestVersion()
	{
		Vector<String> versions = UrlUtils.extractMultiOccurrencePattern(
			jEdit.getProperty(VERSION_PAGE_PROP, new String[] {plugin}),
			jEdit.getProperty(VERSION_CHECK_PATTERN_PROP, new String[] {plugin}));
		if (versions.size() == 0)
			return null;
		return versions.lastElement();
	}

	public void setInstalledVersion(String version)
	{
		String prop = INSTALLED_BUILD_PROP + "." + plugin;
		if (version == null)
			jEdit.resetProperty(prop);
		else
			jEdit.setProperty(prop, version);
	}

}
