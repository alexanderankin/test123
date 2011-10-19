package gatchan.jedit.lucene;

import org.gjt.sp.jedit.io.VFSFile;

import gatchan.jedit.lucene.Index.FileProvider;

public class FileArrayProvider implements FileProvider
{
	private final VFSFile[] fileArray;
	private int index;

	public FileArrayProvider(VFSFile[] files)
	{
		fileArray = files;
	}

	@Override
	public VFSFile next()
	{
		if (index >= fileArray.length)
			return null;
		return fileArray[index++];
	}

	@Override
	public int size()
	{
		return fileArray.length;
	}
}
