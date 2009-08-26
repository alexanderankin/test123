package lcm.providers.md5;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.TreeMap;

import javax.swing.text.Position;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferAdapter;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import lcm.BufferHandler;
import lcm.LCMPlugin;
import lcm.painters.ColoredRectDirtyMarkPainter;
import lcm.painters.DirtyMarkPainter;

public class ChecksumBufferHandler extends BufferAdapter implements BufferHandler
{
	private Buffer buffer;
	private ColoredRectDirtyMarkPainter painter;
	private TreeMap<Position, byte []> md5;
	
	public ChecksumBufferHandler(Buffer buffer)
	{
		this.buffer = buffer;
		md5 = new TreeMap<Position, byte []>(new PositionComparator());
		painter = new ColoredRectDirtyMarkPainter();
		painter.setColor(Color.YELLOW);
	}

	public void bufferSaved(Buffer buffer)
	{
		md5.clear();
	}

	public DirtyMarkPainter getDirtyMarkPainter(Buffer buffer, int physicalLine)
	{
		byte[] b1 = calculateHash(buffer.getLineText(physicalLine));
		byte[] b2 = md5.get(new FixedPosition(physicalLine));
		if (b2 == null)
			b2 = calculateHash(getFileLine(physicalLine));
		if (b1.length != b2.length)
		{
			Position p = buffer.createPosition(buffer.getLineStartOffset(physicalLine));
			md5.put(p, b2);
			return painter;
		}
		for (int i = 0; i < b1.length; i++)
		{
			if (b1[i] != b2[i])
			{
				Position p = buffer.createPosition(buffer.getLineStartOffset(physicalLine));
				md5.put(p, b2);
				return painter;
			}
		}
		return null;
	}

	private String getFileLine(int line)
	{
		String [] lines = LCMPlugin.getInstance().readFile(buffer.getPath());
		return lines[line];
	}

	public void contentInserted(JEditBuffer buffer, int startLine, int offset,
		int numLines, int length)
	{
	}

	public void contentRemoved(JEditBuffer buffer, int startLine, int offset,
		int numLines, int length)
	{
	}

	private byte[] calculateHash(String line)
	{
		ByteBuffer bb = null;
		int length = line.length();
		bb = ByteBuffer.allocate(length * 2);	// Chars are 2 bytes
		CharBuffer cb = bb.asCharBuffer();
		cb.append(line);
		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(bb);
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class PositionComparator implements Comparator<Position>
	{
		public int compare(Position o1, Position o2)
		{
			int i1 = o1.getOffset();
			int i2 = o2.getOffset();
			if (i1 < i2)
				return (-1);
			if (i1 > i2)
				return 1;
			return 0;
		}
	}
	private class FixedPosition implements Position
	{
		int offset;
		public FixedPosition(int line)
		{
			offset = buffer.getLineStartOffset(line);
		}
		public int getOffset()
		{
			return offset;
		}
	}
}
