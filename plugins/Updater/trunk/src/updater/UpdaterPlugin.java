package updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;

public class UpdaterPlugin extends EditPlugin
{
	private static final String DOWNLOAD_PAGE_PROP = "updater.downloadPage";
	private static final String DOWNLOAD_LINK_PATTERN_PROP = "updater.downloadLinkPattern";
	private static final int BUFFER = 2048;
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
	interface UrlLineHandler
	{
		// Return true to continue, false to abort.
		boolean process(String line);
	}
	public boolean processUrl(String urlPath, UrlLineHandler handler)
	{
		boolean ret = true;
		URL url;
		InputStream in = null;
		BufferedReader bin = null;
		try
		{
			url = new URL(urlPath);
			in = url.openStream();
			bin = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = bin.readLine()) != null)
			{
				if (! handler.process(line))
					break;
			}
		}
		catch(IOException e)
		{
			ret = false;
		}
		finally
		{
			safelyClose(bin, in);
		}
		return ret;
	}

	public String getDownloadLink()
	{
		class DownloadLinkExtractor implements UrlLineHandler
		{
			private Pattern pattern;
			String link;
			public DownloadLinkExtractor()
			{
				pattern = Pattern.compile(jEdit.getProperty(
					DOWNLOAD_LINK_PATTERN_PROP));
				link = null;
			}
			public boolean process(String line)
			{
				Matcher m = pattern.matcher(line);
				if (! m.find())
					return true;
				link = m.group(1);
				return false;
			}
		}
		DownloadLinkExtractor extractor = new DownloadLinkExtractor();
		if (processUrl(jEdit.getProperty(DOWNLOAD_PAGE_PROP), extractor))
			return extractor.link;
		return null;
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

	public void updateVersion()
	{
		String link = getDownloadLink();
		if (link == null)
		{
			JOptionPane.showMessageDialog(null, jEdit.getProperty(
				"updater.msg.downloadLinkNotFound"));
			return;
		}
		File installerFile = downloadFile(link); 
		if (installerFile == null)
		{
			JOptionPane.showMessageDialog(null, jEdit.getProperty(
				"updater.msg.downloadFailed"));
			return;
		}
		if (! runInstaller(installerFile))
		{
			JOptionPane.showMessageDialog(null, jEdit.getProperty(
				"updater.msg.installerFailed"));
			return;
		}
		JOptionPane.showMessageDialog(null, jEdit.getProperty(
			"updater.msg.installed"));
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
