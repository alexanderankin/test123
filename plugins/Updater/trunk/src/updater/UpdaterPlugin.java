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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import javax.swing.Timer;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

import updater.UrlUtils.ProgressHandler;

public class UpdaterPlugin extends EditPlugin
{
	private static final int BAD_VERSION_STRING = -100;
	private static final int MILLIS_PER_UPDATE_PERIOD_UNIT = 10000;
	private static final String LAST_UPDATE_TIME_PROP =
		"updater.values.lastUpdateTime";
	static private UpdaterPlugin instance;
	private File home;
	private Process backgroundProcess;
	private OutputStreamWriter writer;
	private boolean startupExecution;
	private static boolean abort;

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
		int updatePeriod = UpdaterOptions.getUpdatePeriod();
		if (updatePeriod != 0)
		{
			setPeriodicVersionUpdate(updatePeriod);
		}
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

	private void setPeriodicVersionUpdate(int updatePeriod)
	{
		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jEdit.setIntegerProperty(LAST_UPDATE_TIME_PROP, 10);
				updateFromDefaultSource();
			}
		};
		Timer t = new Timer(updatePeriod * MILLIS_PER_UPDATE_PERIOD_UNIT, al);
		t.setRepeats(true);
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
		abort = false;
		String [] args = new String[] { "java",
			//"-Xdebug", "-Xnoagent", "-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y",
			"-cp", getPluginJAR().getFile().getAbsolutePath(),
			InstallLauncher.class.getCanonicalName(),
			UpdaterOptions.getUpdateLogFile() };
		try {
			backgroundProcess = Runtime.getRuntime().exec(args);
			writer = new OutputStreamWriter(backgroundProcess.getOutputStream());
			StreamConsumer sc = new StreamConsumer(backgroundProcess.getInputStream());
			sc.start();
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
		if (! UpdaterOptions.isInteractiveInstall())
		{
			appendText("auto");
			appendText(installDir);
		}
		appendText(InstallLauncher.END_INSTALLER_PARAMS);
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
				boolean calledOnStartup = startupExecution;
				startupExecution = false;
				if (executionAborted())
					return;
				String installedVersion = source.getInstalledVersion();
				String latestVersion = source.getLatestVersion();
				if (latestVersion == null)
				{
					endExecution(jEdit.getProperty("updater.msg.cannotFindLatestVersion"));
					return;
				}
				if (executionAborted())
					return;
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
					if (calledOnStartup)
						appendText(InstallLauncher.SILENT_SHUTDOWN);
					else
						endExecution(jEdit.getProperty("updater.msg.noNewerVersion"));
					return;
				}
				appendText(jEdit.getProperty("updater.msg.fetchingDownloadPage"));
				String link = source.getDownloadLink();
				if (link == null)
				{
					endExecution(jEdit.getProperty("updater.msg.downloadLinkNotFound"));
					return;
				}
				if (executionAborted())
					return;
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
					public boolean isAborted()
					{
						return executionAborted();
					}
				};
				String savePath = home.getAbsoluteFile() + File.separator +
					"jeditInstall.jar";
				File installerFile = UrlUtils.downloadFile(link, savePath, progress); 
				if (executionAborted())
					return;
				appendText("");	// Newline after "bytes read" message
				if (installerFile == null)
				{
					endExecution(jEdit.getProperty("updater.msg.downloadFailed"));
					return;
				}
				source.setInstalledVersion(latestVersion);
				appendText(jEdit.getProperty("updater.msg.runningInstaller"));
				if (executionAborted())
					return;
				if (! runInstaller(installerFile))
				{
					source.setInstalledVersion(installedVersion);
					endExecution(jEdit.getProperty("updater.msg.installerFailed"));
					return;
				}
				// No more output should be shown by this process. The rest
				// will be shown by the install launcher.
			}
		};
		updateThread.start();
	}

	public boolean executionAborted()
	{
		synchronized(this)
		{
			if (abort)
			{
				abort = false;
				endExecution(jEdit.getProperty("updater.msg.executionAborted"));
				return true;
			}
		}
		return false;
	}

	public void updateReleaseVersion()
	{
		updateVersion(new ReleasedUpdateSource());
	}

	public void updateDailyVersion()
	{
		updateVersion(new DailyBuildUpdateSource());
	}

	private static class StreamConsumer extends Thread
	{
		private InputStream is;
		public StreamConsumer(InputStream is) {
			this.is = is;
		}
		public void run() {
			try {
				InputStreamReader reader = new InputStreamReader(is);
				while (reader.read() != -1)
				{
					synchronized(this)
					{
						abort = true;
						return;
					}
				}
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();  
			}
		}
	}
}
