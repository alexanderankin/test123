
/*
Copyright (C) 2008 Matthew Gilbert

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package voxspellcheck;

import java.util.List;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.FontMetrics;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

class SuggestionDialog extends JDialog implements ActionListener, KeyListener, MouseListener
{
    public String word;
    private JPanel panel;
    private JList list;
    private JScrollPane scroll_pane;
    boolean kp;
    
    private static int width = 120;
    private static int height = 120;
    public SuggestionDialog(View view, boolean modal, List words)
    {
        super(view, "Word Pick", modal);
        this.word = null;
        this.kp = false;
        this.setSize(width, height);
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.list = new JList(words.toArray());
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.scroll_pane = new JScrollPane(this.list);
        this.panel.add(scroll_pane);
        this.setContentPane(this.panel);
        this.list.addKeyListener(this);
        
        TextArea ta = view.getTextArea();
        FontMetrics metrics = ta.getPainter().getFontMetrics();
        Rectangle screen_rect = view.getGraphicsConfiguration().getBounds();
        Selection sel = ta.getSelectionAtOffset(ta.getCaretPosition());
        Point p = ta.offsetToXY(sel.getStart());
        Point loc = ta.getLocationOnScreen();
        int y = p.y + loc.y + metrics.getHeight();
        if ((y + height) > screen_rect.height) {
            y = (p.y + loc.y) - height;
        }
        int x = p.x + loc.x + ta.getGutter().getWidth();
        if ((x + width) > screen_rect.width) {
            x = (p.x + loc.x) - width;
        }
        this.setLocation(x, y);
        this.setUndecorated(true);
    }
    
    public void actionPerformed(ActionEvent ev)
    {
    }
    
    public void keyPressed(KeyEvent ev)
    {
        this.kp = true;
    }
    
    public void keyReleased(KeyEvent ev)
    {
        if (this.kp) {
            switch (ev.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                this.word = (String)this.list.getSelectedValue();
            case KeyEvent.VK_ESCAPE:
                this.dispose();
                break;
            default:
                break;
            }
        }
    }
    
    public void keyTyped(KeyEvent ev)
    {
    }
    
    public void mouseClicked(MouseEvent ev)
    {
    }
    
    public void mouseEntered(MouseEvent ev)
    {
    }
    
    public void mouseExited(MouseEvent ev)
    {
    }
    
    public void mousePressed(MouseEvent ev)
    {
    }
    
    public void mouseReleased(MouseEvent ev)
    {
    }
}