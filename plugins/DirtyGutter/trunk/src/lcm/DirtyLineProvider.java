package lcm;

import org.gjt.sp.jedit.Buffer;

public interface DirtyLineProvider
{
	/*
	 * Attach the dirty line provider to the given buffer.
	 * Returns the buffer listener that updates the buffer's dirty state when
	 * the content is changed.
	 */
	BufferHandler attach(Buffer buffer);
	/*
	 * Detach the buffer listener that was previously returned by 'attach'
	 * from the given buffer.
	 */
	void detach(Buffer buffer, BufferHandler handler);
}
