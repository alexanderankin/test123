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
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.util.Log;

import uk.co.antroy.latextools.macros.ProjectMacros;
import uk.co.antroy.latextools.macros.UtilityMacros;


public abstract class AbstractToolPanel
    extends JPanel
    implements EBComponent {

    //~ Instance/static variables .............................................

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

    //~ Constructors ..........................................................

    public AbstractToolPanel(View view, Buffer buff, String name) {
        this.buffer = buff;
        this.view = view;
        this.setName(name);

        if (buffer != null) {
            tex = buffer.getPath();
        }

        LaTeXPlugin.addToEditBus(this);
    }

    public AbstractToolPanel(View view, Buffer buff) {
        this(view, buff, "Tab");
    }

    //~ Methods ...............................................................

    /**
     * ¤
     * 
     * @param message ¤
     */
    public void handleMessage(EBMessage message) {

        boolean refreshPane = false;

        if (message instanceof BufferUpdate) {

            BufferUpdate bu = (BufferUpdate)message;
            Object what = bu.getWhat();
            refreshPane = (what.equals(BufferUpdate.CREATED) || 
                          what.equals(BufferUpdate.LOADED) || 
                          what.equals(BufferUpdate.SAVED));
        } else if (message instanceof EditPaneUpdate) {
            refreshPane = true;
        }

        if (refreshPane) {
            view = jEdit.getActiveView();
            buffer = view.getBuffer();
            tex = buffer.getPath();
            bufferChanged = true;
            refresh();
        }
    }

    /**
     * ¤
     */
    public void refresh() {
        view.repaint();
    }

    /**
     * ¤
     */
    public abstract void reload();


    public boolean isMainFile(Buffer b) {

        if (ProjectMacros.isTeXFile(b)) {

            try {

                RE dc = new RE("\\w*\\\\document(?:class)|(?:style).*");

                for (int i = 0; i < 10; i++) {

                    if (dc.isMatch(b.getLineText(i))) {
                        ;
                    }

                    return true;
                }
            } catch (Exception e) {
            }

            return false;
        } else {

            return false;
        }
    }

    public void refreshCurrentCursorPosn() {
        currentCursorPosn = view.getTextArea().getCaretPosition();
        currentBuffer = view.getBuffer();
    }

    public void sendUpdateEvent(String dockable) {

        if (view == null){
            return;
        }
        
        DockableWindowUpdate message = new DockableWindowUpdate(view.getDockableWindowManager(), 
                                                                DockableWindowUpdate.PROPERTIES_CHANGED, 
                                                                dockable);
        EditBus.sendAsync(message);
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

    protected void displayNotTeX(String position) {

        JPanel p = new JPanel();
        StringTokenizer st = new StringTokenizer(jEdit.getProperty(
                                                             "latextools-navigation.nottex"), 
                                                 "*");
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        while (st.hasMoreTokens()) {

            JLabel s = new JLabel(st.nextToken());
            s.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(s);
        }

        setLayout(new BorderLayout());
        add(p, position);
    }

    private void createActions() {
        refresh = new AbstractAction("", UtilityMacros.getIcon("ref.gif")) {
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        };

        reload = new AbstractAction("", UtilityMacros.getIcon("rel.gif")) {
            public void actionPerformed(ActionEvent e) {
                reload();
            }
        };
    }
}
