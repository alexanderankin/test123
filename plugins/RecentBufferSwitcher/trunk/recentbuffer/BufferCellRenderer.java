package recentbuffer;

// from Java:
import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.io.VFS;
/**
 * Allows a list of buffers to be rendered with jEdits colors
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1.1.1 $ $Date: 2005/10/06 13:51:34 $
 */
@SuppressWarnings("serial")
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
		if (isSelected) 
			setForeground( new Color(0xFFFFFF) );
		else
			setForeground( VFS.getDefaultColorFor(buf.getName()) );
		return this;
	}	
}

