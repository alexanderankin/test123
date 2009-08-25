package lcm.providers.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import jdiff.util.Diff;
import jdiff.util.Diff.Change;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;


public class BufferFileDiff
{
	private Buffer b;

	public BufferFileDiff(Buffer b)
	{
		this.b = b;
	}

	public Vector<Range> getDiff()
	{
		int nBuffer = b.getLineCount();
		String [] bufferLines = new String[nBuffer];
		for (int i = 0; i < nBuffer; i++)
			bufferLines[i] = b.getLineText(i);
		String [] fileLines = readFile(b.getPath());
		if (fileLines == null)
			return null;
		Diff diff = new Diff(fileLines, bufferLines);
		Change edit = diff.diff_2();
		Vector<Range> ranges = new Vector<Range>();
		for (; edit != null; edit = edit.next)
			ranges.add(new Range(edit.first1, edit.lines1));
		return ranges;
	}

	private String[] readFile(String path)
	{
		VFS vfs = VFSManager.getVFSForPath(path);
		Object session = null;
		VFSFile file = null;
		BufferedReader reader = null;
		String [] ret = null;
		try
		{
			session = vfs.createVFSSession(path, jEdit.getActiveView());
			file = vfs._getFile(session, path, jEdit.getActiveView());
			reader = new BufferedReader(new InputStreamReader(
				file.getVFS()._createInputStream(session, file.getPath(),
				false,jEdit.getActiveView())));
			Vector<String> lines = new Vector<String>();
			String line;
			while ((line = reader.readLine()) != null)
				lines.add(line);
			ret = new String[lines.size()];
			lines.toArray(ret);
			return ret;
		}
		catch (IOException e)
		{
			Log.log(Log.ERROR, this, "Unable to read file " + path, e);
		}
		finally
		{
			try
			{
				IOUtilities.closeQuietly(reader);
				vfs._endVFSSession(session, jEdit.getActiveView());
			}
			catch (IOException e)
			{
			}
		}
		return ret;
	}
}
