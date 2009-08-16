package updater;
import installer.ConsoleProgress;
import installer.Install;
import installer.InstallThread;
import installer.OperatingSystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

public class UpdaterPlugin extends EditPlugin
{
	private static final int BUFFER = 2048;
	static public final String downloadPage = "http://jedit.org/index.php?page=download";
	static private UpdaterPlugin instance;
	private File home;

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

	public String getDownloadLink()
	{
		String link = null;
		URL url;
		InputStream in = null;
		BufferedReader bin = null;
		try
		{
			url = new URL(downloadPage);
			in = url.openStream();
			bin = new BufferedReader(new InputStreamReader(in));
			String line;
			Pattern downloadLinkPattern = Pattern.compile(
				".*<a href=\"([^\"]+)\">Java-based installer.*");
			while((line = bin.readLine()) != null)
			{
				Matcher m = downloadLinkPattern.matcher(line);
				if (m.find())
				{
					link = m.group(1);
					break;
				}
			}
		}
		catch(IOException e)
		{
		}
		finally
		{
			safelyClose(bin, in);
		}
		return link;
	}

	public File downloadFile(String urlString)
	{
		File installerFile = null;
		URL url;
		InputStream in = null;
		BufferedInputStream bin = null;
		FileOutputStream out = null;
		BufferedOutputStream bout = null;
		try
		{
			url = new URL(urlString);
			in = url.openStream();
			bin = new BufferedInputStream(in);
			installerFile = new File(home.getAbsoluteFile() +
				File.separator + "jeditInstall.jar");
			out = new FileOutputStream(installerFile);
			bout = new BufferedOutputStream(out);
			byte[] buffer = new byte[BUFFER];
			int bytesRead;
			while ((bytesRead = bin.read(buffer)) > 0)
				bout.write(buffer, 0, bytesRead);
		}
		catch(IOException e)
		{
			installerFile = null;
		}
		finally
		{
			safelyClose(bin, in);
			safelyClose(bout, out);
		}
		return installerFile;
	}

	private boolean extractFiles(File installerFile)
	{
		Install installer = new Install();
		ConsoleProgress progress = new ConsoleProgress();
		String installDir = jEdit.getJEditHome();
		// TODO: Add OS tasks
		OperatingSystem.OSTask[] osTasks = new OperatingSystem.OSTask[0];
		Vector<String> components = new Vector<String>();
		components.add("jedit-program");
		components.add("jedit-macros");
		components.add("jedit-api");
		InstallThread thread = new InstallThread(installer,
			progress, installDir, osTasks, 0 /* XXX */,components);
		thread.start();
		return true;
	}

	public void updateVersion()
	{
		String link = getDownloadLink();
		if (link == null)
		{
			System.err.println("Download link not found");
			return;
		}
		System.err.println("Download link: " + link);
		File installerFile = downloadFile(link); 
		if (installerFile == null)
		{
			System.err.println("Download failed");
			return;
		}
		System.err.println("Downloaded to: " + installerFile.getAbsolutePath());
		if (! extractFiles(installerFile))
		{
			System.err.println("Download failed");
			return;
		}
		System.err.println("Extracted");
	}

	private void safelyClose(BufferedOutputStream bout, OutputStream out)
	{
		if (bout != null)
		{
			try
			{
				bout.close();
				out = null;
			}
			catch (IOException e)
			{
			}
		}
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	private void safelyClose(Object bin, InputStream in)
	{
		if (bin != null)
		{
			try
			{
				if (bin instanceof Reader)
					((Reader) bin).close();
				else if (bin instanceof InputStream)
					((InputStream) bin).close();
				in = null;
			}
			catch (IOException e)
			{
			}
		}
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (IOException e)
			{
			}
		}
	}

}
