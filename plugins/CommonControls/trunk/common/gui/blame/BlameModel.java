/*
* Copyright (c) 2008, Dale Anson
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package common.gui.blame;

import java.util.List;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/** A data model that stores 2 lists of Strings, where indices
	in these list correspond to physical lines in the textArea's buffer.  	
	One list is for the BlamePane to display, and the other is for toolTips. 	
 */
public class BlameModel {

    /**
     * Blame is actually attached to a Buffer, but is displayed in a JEditTextArea,
     * so need a reference to the text area.
     */
    private JEditTextArea textArea = null;

    /**
     * The lines of blame returned from the SVN "blame" command.  There is one
     * entry in the list per line in the file.
     */
    private List<String> blame = null;
    
    /** Additional information you might want to display for each line in a tooltip */
    private List<String> tooltips = null;

    /**
     * The svn blame action checks if the local working file has been modified,
     * if it has, sets this field to true, and later checks this flag to decide if it should
     * issue a warning. Git blame doesn't use this member at all since individual blame lines
     * indicate whether they were locally modified or not.   
     */
    private boolean outOfDate = false;
    
    /**
     * Default constructor. Text area and blame should be set later.    
     */
    public BlameModel() {

    }
    
    /**
     * @param textarea The JEditTextArea that this blame is for.
     * @param blame A list of strings containing the blame per line in the text area.
     * This doesn't have to be "blame", just something that is desired to be displayed
     * for each line. There should be one item in this list for each line in the
     * text area.
     */
    public BlameModel(JEditTextArea textarea, List<String> blame) {
        this( textarea, blame, null );
    }
    
    /**
     * @param textarea The JEditTextArea that this blame is for.
     * @param blame A list of strings containing the blame per line in the text area.
     * This doesn't have to be "blame", just something that is desired to be displayed
     * for each line. There should be one item in this list for each line in the
     * text area.
     * @param tooltips A list of strings for tooltips, again, one per line in the 
     * text area. 
     */
    public BlameModel( JEditTextArea textarea, List<String> blame, List<String> tooltips) {
        this.textArea = textarea;
        this.blame = blame;
        this.tooltips = tooltips;
    }

    public void setTextArea(JEditTextArea textarea) {
        textArea = textarea;
    }

    public JEditTextArea getTextArea() {
        return textArea;
    }

    public void setBlame(List<String> blame) {
        this.blame = blame;
    }

    public List<String> getBlame() {
        return blame;
    }
    
    public void setToolTips(List<String> tooltips) {
        this.tooltips = tooltips;
    }

    public List<String> getToolTips() {
        return tooltips;
    }
    
    /**
     * Use this to indicate that the blame model no longer accurately represents
     * the lines in the text area, for example, it could be that lines were added
     * to the text area after the model was produced.
     * @param b <code>true</code> indicates the model is out of date.
     */
    public void setOutOfDate(boolean b) {
        outOfDate = b;
    }

    public boolean outOfDate() {
        return outOfDate;
    }
    
    /**
     * @param index A line number or other index to fetch a specific blame string.
     * @return The blame associated with the line or index or null if the index is
     * out of range.
     */
    public String getItem(int index) {
        if (blame == null) {
            return null;   
        }
        if (index < 0 || index >= blame.size()) {
            return null;
        }
        return blame.get(index);
    }
    
    /**
     * @param index A line number or index to fetch a specific tooltip string.
     * @return The tooltip text associated with the line or index, or null if the 
     * index is out of range.
     */
    public String getToolTipText(int index) {
        if (tooltips == null) {
            return null;   
        }
        if (index < 0 || index >= tooltips.size()) {
            return null;
        }
        return tooltips.get(index);
    }
}