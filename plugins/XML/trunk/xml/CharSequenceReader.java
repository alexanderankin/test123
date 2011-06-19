package xml;

import java.io.*;

/**
 * implement a Reader on top of CharSequence, not only String
 * like java.io.StringReader.
 *
 * mark() and reset() are not implemented but could be implemented,
 * since the whole contents are accessible all the time.
 *
 * This class is not thread safe : concurrent reads
 * could get twice the same data or hit exceptions
 * when going after the end of the source sequence
 **/
public class CharSequenceReader extends Reader
{
	/** the source of the characters read */
	private CharSequence src;

	/** current position */
	private int pos;

	/**
	 * @param src the source of characters to be read
	 * */
	public CharSequenceReader(CharSequence src)
	{
		super();
		this.src = src;
		pos = 0;
	}

	@Override
	public int read()
	{
		if(pos >= src.length())return -1;

		return src.charAt(pos++);
	}
	
	@Override
	public int read(char[] buff, int off, int len) throws IOException
	{
		if(pos >= src.length())return -1;
		if(off+len > buff.length)throw new IOException("error : off+len="+off+" > buff.length= "+len);
		for(int i=0;i<len;i++)
		{
			buff[i+off] = src.charAt(pos++);
			if(pos >= src.length())return i+1;
		}
		return len;
	}

	@Override
	public boolean ready()
	{
		return true;
	}

	@Override
	public void close(){}
}
