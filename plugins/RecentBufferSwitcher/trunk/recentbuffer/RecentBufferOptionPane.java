package recentbuffer;

// from Java:
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * The RecentBufferSwitcher plugins options
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1.1.1 $ $Date: 2005/10/06 13:51:34 $
 */
@SuppressWarnings("serial")
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
			Integer.parseInt(numRows.getText());
			jEdit.setProperty(RecentBufferSwitcherPlugin.OPTION_PREFIX + "numberofvisiblerows", numRows.getText());
		} catch(NumberFormatException e) {}
	}
}
