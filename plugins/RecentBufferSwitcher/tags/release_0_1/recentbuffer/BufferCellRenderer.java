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
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.util.Log;
/**
 * Allows a list of buffers to be rendered with jEdits colors
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1 $ $Date: 2005-10-21 11:48:39 $
 */
public class BufferCellRenderer extends DefaultListCellRenderer {
	/**
	 * Constructor
	 */	
	public BufferCellRenderer()	{	
		super();				
	}	
	
	public Component getListCellRendererComponent(JList jlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {		
		super.getListCellRendererComponent(jlist, value, index, isSelected, cellHasFocus);
		Buffer buf = (Buffer)value;
		setIcon(buf.getIcon());
		java.awt.Color color = VFS.getDefaultColorFor(buf.getName());		
		setForeground(color);
		return this;
	}	
}

