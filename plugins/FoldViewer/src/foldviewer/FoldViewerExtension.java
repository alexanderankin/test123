/*
 * FoldViewerExtension.java 
 * Copyright (c) Sun Aug 27 MSD 2006 Denis Koryavov
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

package foldviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.util.StandardUtilities;

import static org.gjt.sp.jedit.TextUtilities.tabsToSpaces;
import static javax.swing.SwingUtilities.computeStringWidth;


public class FoldViewerExtension extends TextAreaExtension {
        private int wWidth = 0;
        private int wHeight = 0;
        
        //{{{ getToolTipText method.
        public String getToolTipText(int x, int y) {
                JEditTextArea textArea = jEdit.getActiveView().getEditPane().getTextArea();
                int sLine = textArea.getScreenLineOfOffset(textArea.xyToOffset(x, y));
                int pLine = textArea.getPhysicalLineOfScreenLine(sLine);
                
                if (textArea.getBuffer().isFoldStart(pLine) 
                        && !textArea.getDisplayManager().isLineVisible(pLine + 1)) 
                {
                        Point coords = getLocation(textArea);
                        if (enshureLocation(textArea, pLine, x)) {
                                FoldWindow window = new FoldWindow(textArea);
                                window.showText(coords.x, coords.y + y, 
                                        getText(textArea, pLine), wWidth + 10, wHeight + 3);
                        }
                }
                return null;
        } 
        //}}}
        
        //{{{ enshureLocation method.
        private boolean enshureLocation(JEditTextArea textArea, int pLine, int x) {
                String line = textArea.getBuffer().getLineText(pLine);
                int lines[] = textArea.getBuffer().getFoldAtLine(pLine);
                FontMetrics fm = textArea.getPainter().getFontMetrics();
                
                String sLine = tabsToSpaces(line + StandardUtilities.getLeadingWhiteSpace(line),
                        textArea.getBuffer().getTabSize());
                
                int start = computeStringWidth(fm, sLine);
                int end = start + computeStringWidth(fm, "[" 
                        + (lines[1] - lines[0]) + " lines]");
                
                if ((start < x) && (x < end)) return true;
                return false;
        } 
        //}}}
        
        //{{{ getText method.
        private String getText(JEditTextArea textArea, int physicalLine) {
                int lines[] = textArea.getBuffer().getFoldAtLine(physicalLine);
                int tabSize = textArea.getBuffer().getTabSize();
                FontMetrics fm = textArea.getPainter().getFontMetrics();
                wHeight = fm.getHeight()  * (1 + (lines[1] - lines[0]));
                
                String result   = "";
                String lineText = "";
                int maxLen = 0;
                wWidth = 0; // reset previous size.
                for(int i = lines[0]; i < lines[1] + 1; i++) {
                        lineText = tabsToSpaces(textArea.getLineText(i), tabSize);
                        maxLen = computeStringWidth(fm, lineText);
                        wWidth = (wWidth < maxLen) ? maxLen : wWidth; 
                        
                        if (i < lines[1]) {
                                result += lineText + "\n";
                        } else {
                                result += lineText;
                        }
                        
                }
                return tabsToSpaces(result, textArea.getBuffer().getTabSize());
        } 
        //}}}
       
       //{{{ getLocation method.
       private Point getLocation(JEditTextArea textArea) {
               Point point = new Point(0, 0);
               point.x = textArea.getGutter().getBounds().x;
               point.y = textArea.getGutter().getBounds().y;
               FontMetrics fm = textArea.getPainter().getFontMetrics();
               point.y += fm.getHeight();
               SwingUtilities.convertPointToScreen(point, textArea.getPainter());
               return point;
       } 
       //}}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}

