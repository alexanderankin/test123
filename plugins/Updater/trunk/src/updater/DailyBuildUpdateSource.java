/*
 * DailyBuildUpdateSource.java - The "daily builds" update source
 *
 * Copyright (C) 2009 Shlomy Reinstein
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package updater;

import java.util.Vector;

import org.gjt.sp.jedit.jEdit;

public class DailyBuildUpdateSource implements UpdateSource
{
	private static final String INSTALLED_BUILD_PROP = "updater.dailyBuildInstalledVersion";
	private static String VERSION_PAGE_PROP = "updater.dailyBuildVersionPage";
	private static final String VERSION_CHECK_PATTERN_PROP = "updater.dailyBuildVersionCheckPattern";
	private static final String VERSION_PAGE_LINK_PATTERN_PROP = "updater.dailyBuildVersionPageLinkPattern";
	private static final String DOWNLOAD_LINK_PATTERN_PROP = "updater.dailyBuildDownloadLinkPattern";
	private static final String DOWNLOAD_LINK_PREFIX_PROP = "updater.dailyBuildDownloadLinkPrefix";

	public int compareVersions(String latest, String installed)
	{
		if (installed.length() == 0)
			return 1;
		if (latest.length() == 0)
			return BAD_VERSION_STRING;
		String [] latestVer = latest.split("\\D+");
		String [] installedVer = latest.split("\\D+");
		return UpdaterPlugin.compareNumericVersionArray(latestVer, installedVer);
	}

	public String getDownloadLink()
	{
		Vector<String> versions = UrlUtils.extractMultiOccurrencePattern(
			jEdit.getProperty(VERSION_PAGE_PROP),
			jEdit.getProperty(VERSION_PAGE_LINK_PATTERN_PROP));
		if (versions.size() == 0)
			return null;
		String downloadPage = jEdit.getProperty(VERSION_PAGE_PROP) +
			versions.lastElement();
		String link = UrlUtils.extractSingleOccurrencePattern(downloadPage,
			jEdit.getProperty(DOWNLOAD_LINK_PATTERN_PROP));
		return jEdit.getProperty(DOWNLOAD_LINK_PREFIX_PROP) + link;
	}

	public String getInstalledVersion()
	{
		return jEdit.getProperty(INSTALLED_BUILD_PROP, "");
	}

	public void setInstalledVersion(String version)
	{
		jEdit.setProperty(INSTALLED_BUILD_PROP, version);
	}

	public String getLatestVersion()
	{
		Vector<String> versions = UrlUtils.extractMultiOccurrencePattern(
			jEdit.getProperty(VERSION_PAGE_PROP),
			jEdit.getProperty(VERSION_CHECK_PATTERN_PROP));
		if (versions.size() == 0)
			return null;
		return versions.lastElement();
	}

}
