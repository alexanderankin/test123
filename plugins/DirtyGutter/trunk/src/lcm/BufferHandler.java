package lcm;

import lcm.painters.DirtyMarkPainter;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.BufferListener;


public interface BufferHandler extends BufferListener
{
	/*
	 * Returns an object to paint the dirty state of the specified buffer line.
	 */
	DirtyMarkPainter getDirtyMarkPainter(Buffer buffer, int physicalLine);
	/*
	 * Clear the buffer's dirty state when the buffer is saved.
	 */
	void bufferSaved(Buffer buffer);

}
