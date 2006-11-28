package configurablefoldhandler;
/*
 * ConfigurableFoldHandler.java
 *
 * :folding=custom:collapseFolds=0:noTabs=false:
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import javax.swing.text.Segment;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.FoldHandler;

/**
 * fold handler that allows the user to specify a pair of strings that define
 * the start and end of a fold
 */
public class ConfigurableFoldHandler extends FoldHandler
{
	public ConfigurableFoldHandler()
	{
		super("custom");
	}
	
	/**
	 * Returns the fold level of the specified line.
	 * @param buffer The buffer in questionTO
	 * @param lineIndex The line index
	 * @param seg A segment the fold handler can use to obtain any
	 * text from the buffer, if necessary
	 * @return The fold level of the specified line
	 */
	public int getFoldLevel(Buffer buffer, int lineIndex, Segment seg)
	{
		/*
		 * fold arrows are displayed on a line if its fold level is greater than
		 * that of the line preceeding it. so the line with the fold is the
		 * first at the new level. all subsequent lines at that level or higher
		 * are hidden.
		 *
		 * fold ends that occur on a line before fold starts are effectively
		 * counted as belonging to the line before and not the one on which they
		 * occur. this allows lines like } else { to be folded correctly
		 * (although having that in the comment upsets the folding of this file.
		 * oh well...).
		 */
		 
		if(lineIndex == 0)
			return 0;
		
		FoldCounter counter = ConfigurableFoldHandlerPlugin.getInstance()
			.getCounter(buffer);
		
		if(counter == null)
			return 0;
		
		buffer.getLineText(lineIndex - 1, seg);
		counter.count(seg);
		int folds = counter.getStarts();
		int c1    = counter.getLeadingCloses();
		
		buffer.getLineText(lineIndex, seg);
		counter.count(seg);
		int c2 = counter.getLeadingCloses();
		
		int foldLevel = buffer.getFoldLevel(lineIndex - 1) + folds + c1 - c2;
		return Math.max(0, foldLevel);
	}
}
