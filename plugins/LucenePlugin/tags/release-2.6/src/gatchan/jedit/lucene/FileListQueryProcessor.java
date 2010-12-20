package gatchan.jedit.lucene;

import org.apache.lucene.search.Query;

/*
 * A query result processor that collects the files containing results
 */
public class FileListQueryProcessor implements ResultProcessor
{
	private java.util.List<Object> files;
	private int max;
	public FileListQueryProcessor(Index index,
		java.util.List<Object> files, int max)
	{
		this.files = files;
		this.max = max;
	}
	public boolean process(Query query, float score, Result result)
	{
		String s = result.getPath();
		files.add(s);
		return (files.size() < max);
	}
}
