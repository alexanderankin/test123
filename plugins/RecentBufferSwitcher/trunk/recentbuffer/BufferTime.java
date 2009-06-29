package recentbuffer;

import org.gjt.sp.jedit.Buffer;

/**
 * BufferTime class associates a time with a vector which can be sorted upon
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1.1.1 $ $Date: 2005/10/06 13:51:34 $
 */
public class BufferTime implements Comparable<BufferTime> {
	public long time = 0;
	public Buffer buffer;		
	
	public BufferTime() {
		time = 0;
	}	
	
	public BufferTime(long deTime) {
		time = deTime;
	}	
	
	/**
	 * Sorts by time of objects
	 */	
	public int compareTo(BufferTime o) {
		if (o.time == 0)
			return 0;
		return o.time > time ? 1 : -1;
	}
	
	public String toString() {			
		return "Buffer: "+buffer+", Time: "+time;
	}			
}

