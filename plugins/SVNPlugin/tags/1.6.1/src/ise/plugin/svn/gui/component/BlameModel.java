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

package ise.plugin.svn.gui.component;

import java.util.List;
import org.gjt.sp.jedit.textarea.JEditTextArea;

/**
 * Simple data model to transport necessary data for the SVN "blame" command.
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

    /**
     * My "blame" command will check if the local working file has been modified,
     * if it has, this field will be set to true;
     */
    private boolean outOfDate = false;

    public BlameModel() {

    }

    public BlameModel(JEditTextArea textarea, List<String> blame) {
        this.textArea = textarea;
        this.blame = blame;
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

    public void setOutOfDate(boolean b) {
        outOfDate = b;
    }

    public boolean outOfDate() {
        return outOfDate;
    }
}