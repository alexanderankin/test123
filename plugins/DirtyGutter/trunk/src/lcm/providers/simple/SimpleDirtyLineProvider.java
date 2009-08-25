package lcm.providers.simple;

import lcm.BufferHandler;
import lcm.DirtyLineProvider;

import org.gjt.sp.jedit.Buffer;

public class SimpleDirtyLineProvider implements DirtyLineProvider
{
	public BufferHandler attach(Buffer buffer)
	{
		return new BufferChangedLines(buffer);
	}

	public void detach(Buffer buffer, BufferHandler listener)
	{
		((BufferChangedLines) listener).remove();
	}
}
