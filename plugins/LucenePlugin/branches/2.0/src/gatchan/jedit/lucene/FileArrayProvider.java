package gatchan.jedit.lucene;

import org.gjt.sp.jedit.io.VFSFile;

import gatchan.jedit.lucene.Index.FileProvider;

public class FileArrayProvider implements FileProvider
{
	private VFSFile[] fileArray;
	private int index = 0;

	public FileArrayProvider(VFSFile[] files)
	{
		fileArray = files;
	}

	public VFSFile next()
	{
		if (index >= fileArray.length)
			return null;
		return fileArray[index++];
	}

}
