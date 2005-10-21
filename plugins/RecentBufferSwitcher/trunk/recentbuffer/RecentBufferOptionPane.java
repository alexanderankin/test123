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
 * The RecentBufferSwitcher plugins options
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1 $ $Date: 2005-10-21 11:48:39 $
 */
public class RecentBufferOptionPane extends AbstractOptionPane
{	
	private JTextField numRows;
	
	public RecentBufferOptionPane()	{
		super(RecentBufferSwitcherPlugin.NAME);
	}
	
	public void _init() {
		numRows = new JTextField(jEdit.getProperty(RecentBufferSwitcherPlugin.OPTION_PREFIX+ "numberofvisiblerows"));	
		addComponent(jEdit.getProperty(RecentBufferSwitcherPlugin.OPTION_PREFIX+ "numberofvisiblerows.label"), numRows);
	}	
	
	public void _save()	{
		try {
			// if user has not enterred an integer don't set the property	
			int rows = Integer.parseInt(numRows.getText());
			jEdit.setProperty(RecentBufferSwitcherPlugin.OPTION_PREFIX + "numberofvisiblerows", numRows.getText());
		} catch(NumberFormatException e) {}
	}
}
