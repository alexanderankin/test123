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
		/*
		String [] latestVer = latest.split("\\.");
		String [] installedVer = installed.split("\\.");
		int numCommon = (latestVer.length < installedVer.length ?
			latestVer.length : installedVer.length);
		for (int i = 0; i < numCommon; i++)
		{
			int lat = 0, cur = 0;
			// Make sure that the version string has the expected format
			try
			{
				lat = Integer.parseInt(latestVer[i]);
				cur = Integer.parseInt(installedVer[i]);
			}
			catch (Exception e)
			{
				return BAD_VERSION_STRING;
			}
			if (lat < cur)
				return (-1);
			if (lat > cur)
				return 1;
		}
		if (latestVer.length < installedVer.length)
			return (-1);
		if (latestVer.length > installedVer.length)
			return 1;
		return 0;
		*/
		return 1;
	}

	public String getDownloadLink()
	{
		return UrlUtils.extractPattern(DOWNLOAD_PAGE_PROP, DOWNLOAD_LINK_PATTERN_PROP);
	}

	public String getInstalledVersion()
	{
		return jEdit.getBuild();
	}

	public String getLatestVersion()
	{
		return UrlUtils.extractPattern(VERSION_PAGE_PROP, VERSION_CHECK_PATTERN_PROP);
	}

}
