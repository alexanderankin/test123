/*
 * UpdaterPlugin.java - The plugin for automating jEdit updates
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

import updater.UrlUtils.ProgressHandler;

public class UpdaterPlugin extends EditPlugin
{
	private static final int BAD_VERSION_STRING = -100;
	static private UpdaterPlugin instance;
	private File home;
	private Process backgroundProcess;
	private OutputStreamWriter writer;
	private boolean startupExecution;

	@Override
	public void start()
	{
		instance = this;
		home = getPluginHome();
		if ((home != null) && (! home.exists()))
			home.mkdir();
		startupExecution = true;
		if (UpdaterOptions.isUpdateOnStartup())
			updateFromDefaultSource();
	}

	@Override
	public void stop()
	{
		instance = null;
	}

	static public UpdaterPlugin getInstance()
	{
		return instance;
	}

	private void updateFromDefaultSource()
	{
		try
		{
			UpdateSource source = (UpdateSource) Class.forName(
				UpdaterOptions.getUpdateSourceClassName()).newInstance();
			updateVersion(source);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean startBackgroundProcess()
	{
		String [] args = new String[] { "java",
			//"-Xdebug", "-Xnoagent", "-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y",
			"-cp", getPluginJAR().getFile().getAbsolutePath(),
			InstallLauncher.class.getCanonicalName() };
		try {
			backgroundProcess = Runtime.getRuntime().exec(args);
			writer = new OutputStreamWriter(backgroundProcess.getOutputStream());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void endExecution(String s)
	{
		appendText(s);
		appendText(InstallLauncher.END_EXECUTION);
	}

	private boolean appendText(String s)
	{
		try
		{
			writer.write(s + "\n");
			writer.flush();
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}

	private boolean runInstaller(File installerFile)
	{
		String installDir = jEdit.getJEditHome();
		appendText("Installer: " + installerFile.getAbsolutePath());
		appendText("Install location: " + installDir);
		appendText(InstallLauncher.LAUNCH_INSTALLER_NOW);
		appendText(installerFile.getAbsolutePath());
		appendText(installDir);
		return true;
	}

	public static int compareNumericVersionArray(String[] v1, String[] v2)
	{
		int numCommon = (v1.length < v2.length ?
				v1.length : v2.length);
		for (int i = 0; i < numCommon; i++)
		{
			int lat = 0, cur = 0;
			// Make sure that the version string has the expected format
			try
			{
				lat = Integer.parseInt(v1[i]);
				cur = Integer.parseInt(v2[i]);
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
		if (v1.length < v2.length)
			return (-1);
		if (v1.length > v2.length)
			return 1;
		return 0;
	}

	public void updateVersion(final UpdateSource source)
	{
		startBackgroundProcess();
		Thread updateThread = new Thread() {
			@Override
			public void run() {
				String installedVersion = source.getInstalledVersion();
				String latestVersion = source.getLatestVersion();
				if (latestVersion == null)
				{
					endExecution(jEdit.getProperty("updater.msg.cannotFindLatestVersion"));
					return;
				}
				appendText("Installed version: " + installedVersion);
				appendText("Latest version: " + latestVersion);
				int comparison = source.compareVersions(latestVersion,
					installedVersion);
				if (comparison == BAD_VERSION_STRING)
				{
					endExecution(jEdit.getProperty("updater.msg.unknownVersionString"));
					return;
				}
				if (comparison <= 0)
				{
					if (startupExecution)
						appendText(InstallLauncher.SILENT_SHUTDOWN);
					else
						endExecution(jEdit.getProperty("updater.msg.noNewerVersion"));
					return;
				}
				startupExecution = false;
				appendText(jEdit.getProperty("updater.msg.fetchingDownloadPage"));
				String link = source.getDownloadLink();
				if (link == null)
				{
					endExecution(jEdit.getProperty("updater.msg.downloadLinkNotFound"));
					return;
				}
				appendText(jEdit.getProperty("updater.msg.downloadingNewVersion") +
					" " + link);
				ProgressHandler progress = new ProgressHandler()
				{
					private String suffix = " bytes read";
					public void setSize(int size)
					{
						if (size > 0)
						{
							DecimalFormat format = new DecimalFormat();
							format.setGroupingSize(3);
							suffix = " (out of " + format.format(size) +
								")" + suffix;
						}
					}
					public void bytesRead(final int numBytes)
					{
						DecimalFormat format = new DecimalFormat();
						format.setGroupingSize(3);
						appendText(InstallLauncher.PROGRESS_INDICATOR +
							format.format(numBytes) + suffix);
					}
				};
				String savePath = home.getAbsoluteFile() + File.separator +
					"jeditInstall.jar";
				File installerFile = UrlUtils.downloadFile(link, savePath, progress); 
				appendText("");	// Newline after "bytes read" message
				if (installerFile == null)
				{
					endExecution(jEdit.getProperty("updater.msg.downloadFailed"));
					return;
				}
				source.setInstalledVersion(latestVersion);
				appendText(jEdit.getProperty("updater.msg.runningInstaller"));
				if (! runInstaller(installerFile))
				{
					source.setInstalledVersion(installedVersion);
					endExecution(jEdit.getProperty("updater.msg.installerFailed"));
					return;
				}
				appendText(jEdit.getProperty("updater.msg.installing"));
			}
		};
		updateThread.start();
	}

	public void updateReleaseVersion()
	{
		updateVersion(new ReleasedUpdateSource());
	}

	public void updateDailyVersion()
	{
		updateVersion(new DailyBuildUpdateSource());
	}
}
