package recentbuffer;

// from Java:
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.util.Collections;

// from Swing:
import javax.swing.*;
import javax.swing.event.*;

// from jEdit:
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.util.Log;

/**
 * BufferTime class associates a time with a vector which can be sorted upon
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1 $ $Date: 2005-10-21 11:48:39 $
 */
public class BufferTime implements Comparable {
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
	public int compareTo(Object o) {
		if (!(o instanceof BufferTime))
			return 0;
		BufferTime b = (BufferTime)o;
		long diff = b.time - time;
		//long diff = time - b.time;
		if (diff == 0)
			return 0;
		else if (diff < 0)
			return -1;
		return 1;
	}
	
	public String toString() {			
		return "Buffer: "+buffer+", Time: "+time;
	}			
}

