/*
 * ReleasedUpdateSource.java - The "latest (released) development version" update source
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
		return UrlUtils.extractSingleOccurrencePattern(
			jEdit.getProperty(DOWNLOAD_PAGE_PROP),
			jEdit.getProperty(DOWNLOAD_LINK_PATTERN_PROP));
	}

	public String getInstalledVersion()
	{
		return jEdit.getBuild();
	}

	public String getLatestVersion()
	{
		return UrlUtils.extractSingleOccurrencePattern(
			jEdit.getProperty(VERSION_PAGE_PROP),
			jEdit.getProperty(VERSION_CHECK_PATTERN_PROP));
	}

}
