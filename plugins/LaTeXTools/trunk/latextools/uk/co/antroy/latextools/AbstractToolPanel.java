package uk.co.antroy.latextools;

import gnu.regexp.RE;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;

/*:folding=indent:
 * AbstractToolPanel.java - Abstract class representing a tool panel.
 * Copyright (C) 2002 Anthony Roy
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
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.util.Log;


public abstract class AbstractToolPanel
    extends JPanel
    implements EBComponent {
    protected Action refresh; //Binary flags: reload-refresh
    protected Action reload;
    public static final int REFRESH = 1; //           1 1
    public static final int RELOAD = 2;
    public static final int RELOAD_AND_REFRESH = 3;
    protected Buffer buffer;
    protected String tex;
    protected boolean bufferChanged = false;
    protected int currentCursorPosn;
    protected Buffer currentBuffer;
    protected View view;

    public AbstractToolPanel(View view, Buffer buff, String name) {
        this.buffer = buff;
        this.view = view;
        this.setName(name);
        tex = buffer.getPath();
        EditBus.addToBus(this);
    }

    public AbstractToolPanel(View view, Buffer buff) {
        this(view, buff, "Tab");
    }

    //~ Methods .................................................................

    /**
   * ¤
   * 
   * @param message ¤
   */
    public void handleMessage(EBMessage message) {
        boolean bufferLoaded = false;

        if (message instanceof BufferUpdate) {
            BufferUpdate bu = (BufferUpdate) message;
            Object what = bu.getWhat();
            bufferLoaded = (what == BufferUpdate.CREATED || 
                           what == BufferUpdate.LOADED || 
                           what == BufferUpdate.SAVED);
        }

        if ((message instanceof EditPaneUpdate) || bufferLoaded) {
            buffer = view.getBuffer();
            tex = buffer.getPath();
            bufferChanged = true;
            refresh();
        }
    }

    /**
   * ¤
   */
    public abstract void refresh();

    /**
   * ¤
   */
    public abstract void reload();

    /**
   * ¤
   * 
   * @param b ¤
   * @return ¤
   */
    public boolean isMainFile(Buffer b) {

        if (isTeXFile(b)) {

            try {
                RE dc = new RE("\\w*\\\\document(?:class)|(?:style).*");

                for (int i = 0; i < 10; i++) {

                    if (dc.isMatch(b.getLineText(i)))
                        ;

                    return true;
                }
            } catch (Exception e) {
            }

            return false;
        } else {

            return false;
        }
    }

    /**
   * ¤
   * 
   * @param b ¤
   * @return ¤
   */
    public boolean isTeXFile(Buffer b) {
        log("" + b.getMode());
        String s = b.getMode().getName();
        boolean out = s.equals("tex");
        log("" + out);

        return out;
    }

    protected void displayNotTeX(String position) {
        JPanel p = new JPanel();
        StringTokenizer st = new StringTokenizer(jEdit.getProperty(
                                                         "navigation.nottex"), 
                                                 "*");
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        while (st.hasMoreTokens()) {
            JLabel s = new JLabel(st.nextToken());
            s.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(s);
        }

        setLayout(new BorderLayout());
        add(p, position);

        //    if (position.equals(BorderLayout.SOUTH)){
        //      add(createButtonPanel(REFRESH),BorderLayout.NORTH);
        //    }else{
        //      add(createButtonPanel(REFRESH),BorderLayout.SOUTH);
        //    }
    }

    protected void log(String s) {
        Log.log(Log.DEBUG, this, s);
    }

    protected void log(int i) {
        log("" + i);
    }

    protected void log() {
        log("Green Eggs and Ham");
    }

    protected JPanel createButtonPanel(int buttonTypes) {
        JPanel jp = new JPanel();
        createActions();

        if ((buttonTypes & REFRESH) == REFRESH) {
            JButton b = new JButton(refresh);
            b.setPreferredSize(new Dimension(20, 20));
            b.setToolTipText(jEdit.getProperty("panel.text.refresh"));
            jp.add(b);
        }

        if ((buttonTypes & RELOAD) == RELOAD) {
            JButton b = new JButton(reload);
            b.setPreferredSize(new Dimension(20, 20));
            b.setToolTipText(jEdit.getProperty("panel.text.reload"));
            jp.add(b);
        }

        return jp;
    }

    private void createActions() {
        refresh = new AbstractAction("", loadIcon("/images/ref.gif")) {
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        };

        reload = new AbstractAction("", loadIcon("/images/rel.gif")) {
            public void actionPerformed(ActionEvent e) {
                reload();
            }
        };
    }

    static ImageIcon loadIcon(String filename) {

        return new ImageIcon(AbstractToolPanel.class.getResource(filename));
    }

    public void refreshCurrentCursorPosn() {
        currentCursorPosn = view.getTextArea().getCaretPosition();
        currentBuffer = view.getTextArea().getBuffer();
    }
}
