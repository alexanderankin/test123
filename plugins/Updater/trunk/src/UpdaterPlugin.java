import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gjt.sp.jedit.EditPlugin;

public class UpdaterPlugin extends EditPlugin
{
	static public final String downloadPage = "http://jedit.org/index.php?page=download";
	static private UpdaterPlugin instance;
	private File tempInstallerFile;

	@Override
	public void start()
	{
		instance = this;
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
			if (bin != null)
			{
				try
				{
					bin.close();
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
		return link;
	}

	public void downloadFile(String urlString)
	{
		URL url;
		InputStream in = null;
		BufferedInputStream bin = null;
		FileOutputStream out = null;
		try
		{
			url = new URL(urlString);
			in = url.openStream();
			bin = new BufferedInputStream(in);
			tempInstallerFile = File.createTempFile("jedit", "jar");
			System.err.println("Downloading to: " + tempInstallerFile.getAbsolutePath());
			out = new FileOutputStream(tempInstallerFile);
			int i;
			while((i = bin.read()) != -1)
				out.write(i);
		}
		catch(IOException e)
		{
		}
		finally
		{
			if (bin != null)
			{
				try
				{
					bin.close();
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
		downloadFile(link);
	}
}
