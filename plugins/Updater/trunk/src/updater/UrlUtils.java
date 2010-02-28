/*
 * UrlUtils.java - Misc. functions for handling URLs.
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
import java.net.URLConnection;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils
{
	interface UrlLineHandler
	{
		// Return true to continue, false to abort.
		boolean process(String line);
	}

	// Fetches a text file from urlPath, calling the handler on each line
	public static boolean processUrl(String urlPath, UrlLineHandler handler)
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

	// Searches a text file fetched from urlPath for the single occurrence
	// of pattern.
	public static String extractSingleOccurrencePattern(String urlPath,
		final String pattern)
	{
		class PatternExtractor implements UrlLineHandler
		{
			private Pattern p = Pattern.compile(pattern);
			String s = null;
			public boolean process(String line)
			{
				Matcher m = p.matcher(line);
				if (! m.find())
					return true;
				s = m.group(1);
				return false;
			}
		}
		PatternExtractor extractor = new PatternExtractor();
		if (processUrl(urlPath, extractor))
			return extractor.s;
		return null;
	}

	// Searches a text file fetched from urlPath for the multiple occurrence
	// pattern specified by pattern.
	public static Vector<String> extractMultiOccurrencePattern(String urlPath,
		final String pattern)
	{
		class PatternExtractor implements UrlLineHandler
		{
			private Pattern p = Pattern.compile(pattern);
			Vector<String> lines = new Vector<String>();
			public boolean process(String line)
			{
				Matcher m = p.matcher(line);
				if (m.find())
					lines.add(m.group(1));
				return true;
			}
		}
		PatternExtractor extractor = new PatternExtractor();
		if (processUrl(urlPath, extractor))
			return extractor.lines;
		return null;
	}

	public interface ProgressHandler
	{
		void setSize(int size);
		void bytesRead(int numBytes);
		void done();
		boolean isAborted();
	}

	// Downloads the file in urlString to path downloadTo, updating the
	// progress every 1 sec.
	private static final int BUFFER = 2048;
	public static File downloadFile(String urlString, String downloadTo,
		ProgressHandler progress)
	{
		File targetFile = null;
		URL url;
		InputStream in = null;
		BufferedInputStream bin = null;
		FileOutputStream out = null;
		BufferedOutputStream bout = null;
		try
		{
			url = new URL(urlString);
			URLConnection conn = url.openConnection();
			progress.setSize(conn.getContentLength());
			in = conn.getInputStream();
			bin = new BufferedInputStream(in);
			targetFile = new File(downloadTo);
			out = new FileOutputStream(targetFile);
			bout = new BufferedOutputStream(out);
			byte[] buffer = new byte[BUFFER];
			int bytesRead;
			int totalBytes = 0;
			long time = System.currentTimeMillis();
			while ((bytesRead = bin.read(buffer)) > 0)
			{
				totalBytes += bytesRead;
				bout.write(buffer, 0, bytesRead);
				long time1 = System.currentTimeMillis();
				if (time1 - time >= 1000)
				{
					if (progress.isAborted())
						return null;
					time = time1;
					progress.bytesRead(totalBytes);
				}
			}
		}
		catch(IOException e)
		{
			targetFile = null;
		}
		finally
		{
			safelyClose(bin, in);
			safelyClose(bout, out);
			progress.done();
		}
		return targetFile;
	}

	/* Close an output stream, optionally composed of a buffered output
	 * stream on top of another output stream.
	 */
	public static void safelyClose(BufferedOutputStream bout, OutputStream out)
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

	/* Close an input stream, optionally composed of a reader or a buffered
	 * input stream on top of another input stream.
	 */
	public static void safelyClose(Object bin, InputStream in)
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
