/*
 * UpdateSource.java - An interface for update sources.
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
