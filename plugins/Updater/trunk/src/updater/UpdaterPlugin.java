package updater;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

import updater.UrlUtils.ProgressHandler;

public class UpdaterPlugin extends EditPlugin
{
	private static final int BAD_VERSION_STRING = -100;
	static private UpdaterPlugin instance;
	private File home;
	private JTextArea text;

	@Override
	public void start()
	{
		instance = this;
		home = getPluginHome();
		if ((home != null) && (! home.exists()))
			home.mkdir();
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

	private boolean runInstaller(File installerFile)
	{
		String installDir = jEdit.getJEditHome();
		String [] args = new String[] { "java", "-cp",
			getPluginJAR().getFile().getAbsolutePath(),
			InstallLauncher.class.getCanonicalName(),
			installerFile.getAbsolutePath(), installDir };
		try {
			Runtime.getRuntime().exec(args);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void appendText(final String s)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				text.append(s);
				if (! s.endsWith("\n"))
					text.append("\n");
				
			}
		});
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
		JDialog dialog = new JDialog((JDialog)null, false);
		dialog.setTitle(jEdit.getProperty("updater.msg.updateDialogTitle"));
		dialog.setLayout(new BorderLayout());
		text = new JTextArea(8, 80);
		dialog.add(text, BorderLayout.CENTER);
		appendText(jEdit.getProperty("updater.msg.findLatestVersion"));
		dialog.pack();
		dialog.setVisible(true);

		Thread updateThread = new Thread() {
			@Override
			public void run() {
				String installedVersion = source.getInstalledVersion();
				String latestVersion = source.getLatestVersion();
				if (latestVersion == null)
				{
					appendText(jEdit.getProperty("updater.msg.cannotFindLatestVersion"));
					return;
				}
				int comparison = source.compareVersions(latestVersion,
					installedVersion);
				if (comparison == BAD_VERSION_STRING)
				{
					appendText(jEdit.getProperty("updater.msg.unknownVersionString"));
					return;
				}
				if (comparison <= 0)
				{
					appendText(jEdit.getProperty("updater.msg.noNewerVersion"));
					return;
				}
				appendText(jEdit.getProperty("updater.msg.fetchingDownloadPage"));
				String link = source.getDownloadLink();
				if (link == null)
				{
					appendText(jEdit.getProperty("updater.msg.downloadLinkNotFound"));
					return;
				}
				appendText(jEdit.getProperty("updater.msg.downloadingNewVersion"));
				final int pos = text.getCaretPosition();
				ProgressHandler progress = new ProgressHandler()
				{
					public void bytesRead(final int numBytes)
					{
						SwingUtilities.invokeLater(new Runnable() {
							public void run()
							{
								text.replaceRange(String.valueOf(numBytes) +
									" bytes read", pos, text.getText().length());
							}
						});
					}
				};
				String savePath = home.getAbsoluteFile() + File.separator +
					"jeditInstall.jar";
				File installerFile = UrlUtils.downloadFile(link, savePath, progress); 
				appendText("");	// Newline after "bytes read" message
				if (installerFile == null)
				{
					appendText(jEdit.getProperty("updater.msg.downloadFailed"));
					return;
				}
				appendText(jEdit.getProperty("updater.msg.runningInstaller"));
				if (! runInstaller(installerFile))
				{
					appendText(jEdit.getProperty("updater.msg.installerFailed"));
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
