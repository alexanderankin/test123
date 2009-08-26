package lcm.providers.md5;
import org.gjt.sp.jedit.Buffer;

import lcm.BufferHandler;
import lcm.DirtyLineProvider;


public class ChecksumDirtyLineProvider implements DirtyLineProvider
{

	public BufferHandler attach(Buffer buffer)
	{
		return new ChecksumBufferHandler(buffer);
	}

	public void detach(Buffer buffer, BufferHandler handler)
	{
	}

}
