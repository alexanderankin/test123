/*
 *  UnusedImportCallout.java -   
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  textArea.getPainter().addExtension(new jimporter.utilization.UnusedImportCallout(30, "Hello World", textArea))
 */
package jimporter.utilization;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.Graphics2D;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

/**
 * This class is designed to decorate a line of text with a strikethrough line in
 * order to indicate that something is wrong with that line of text.  In JImporter's
 * case, what is wrong is that the import statement that line of text is pointing
 * to is not in use anywhere in the source file.  (Or it is redundant because it
 * is a class that is already in the package.)
 *
 * This class also gives you the opportunity to supply a tooltip that describes
 * why the line is being struck through.
 *
 * @author Matthew Flower
 * @since 0.5
 */
public class UnusedImportCallout extends TextAreaExtension {
    private int lineNumber;
    private String unusedReason;
    private JEditTextArea textArea;
    
    /**
     * Standard constructor.
     * 
     * @param lineNumber a <code>int</code> value indicating the line number that
     * we should be striking through.
     * @param unusedReason a <code>String</code> value that will be displayed in 
     * a tooltip when the user hovers over that line.
     * @param textArea a <code>JEditTextArea</code> value that identifies which
     * text area we are going to do this strikethrough.
     */
    public UnusedImportCallout(int lineNumber, String unusedReason, JEditTextArea textArea) {
        this.lineNumber = lineNumber-1;
        this.unusedReason = unusedReason;
        this.textArea = textArea;
    }
    
    public void paintValidLine(final Graphics2D gfx, final int screenLine, final int physicalLine, final int start, final int end, final int y) {
            if (physicalLine != lineNumber) {
                return;
            } else {
                //Get height of the line
                int lineHeight = textArea.getPainter().getFontMetrics().getHeight();
                int linePositionY = y+(lineHeight/2);
                int linePositionXEnd = textArea.offsetToXY(textArea.getLineEndOffset(lineNumber)-1).x;
                
                Line2D invalidationLine = new Line2D.Double(0, linePositionY, 
                    linePositionXEnd, linePositionY);
                gfx.setPaint(Color.LIGHT_GRAY);
                gfx.draw(invalidationLine);
            }
    }

            
    public String getToolTipText(int x, int y) {
        int offset = textArea.xyToOffset(x,y);
        int hoverLineNumber = textArea.getLineOfOffset(offset);
        
        if (hoverLineNumber == lineNumber) {
            return unusedReason;
        } else {
            return null;
        }
    }
}

