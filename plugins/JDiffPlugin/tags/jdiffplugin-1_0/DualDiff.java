/*
 * DualDiff.java
 * Copyright (c) 2000 Andre Kaplan
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


import java.awt.Component;
import java.awt.Container;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import jdiff.text.FileData;
import jdiff.text.FileLine;
import jdiff.util.Diff;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public class DualDiff {
    private static boolean ignoreCase = jEdit.getBooleanProperty("jdiff.ignore-case", false);
    private static Hashtable dualDiffs = new Hashtable();

    private View view;
    private EditPane      editPane0;
    private EditPane      editPane1;
    private JEditTextArea textArea0;
    private JEditTextArea textArea1;

    private Diff.change   edits;

    private DiffOverview  diffOverview0;
    private DiffOverview  diffOverview1;
    private JScrollBar    vertical0;
    private JScrollBar    vertical1;
    private Box           box0;
    private Box           box1;

    private AdjustHandler adjustHandler;


    public DualDiff(View view) {
        this.view = view;

        EditPane[] editPanes = this.view.getEditPanes();

        this.editPane0 = editPanes[0];
        this.editPane1 = editPanes[1];

        Buffer buf0 = this.editPane0.getBuffer();
        Buffer buf1 = this.editPane1.getBuffer();

        this.textArea0 = this.editPane0.getTextArea();
        this.textArea1 = this.editPane1.getTextArea();

        FileData fileData0 = this.getFileData(buf0);
        FileData fileData1 = this.getFileData(buf1);

        Diff d = new Diff(fileData0.getLines(), fileData1.getLines());
        this.edits = d.diff_2(false);

        int lineCount0 = fileData0.getLines().length;
        int lineCount1 = fileData1.getLines().length;

        this.diffOverview0 = new DiffLocalOverview(this.edits, lineCount0, lineCount1, this.textArea0, this.textArea1);
        this.diffOverview1 = new DiffGlobalOverview(this.edits, lineCount0, lineCount1, this.textArea0, this.textArea1);

        this.box0      = new Box(BoxLayout.X_AXIS);
        this.vertical0 = this.findVerticalScrollBar(this.textArea0);

        this.box1      = new Box(BoxLayout.X_AXIS);
        this.vertical1 = this.findVerticalScrollBar(this.textArea1);

        this.adjustHandler = new AdjustHandler();

        this.textArea0.remove(this.vertical0);
        this.box0.add(this.diffOverview0);
        this.box0.add(this.vertical0);
        // JEditTextArea.RIGHT is private...
        // "right" == JEditTextArea.RIGHT
        this.textArea0.add("right", this.box0);

        this.textArea1.remove(this.vertical1);
        this.box1.add(this.diffOverview1);
        this.box1.add(this.vertical1);
        // JEditTextArea.RIGHT is private...
        // "right" == JEditTextArea.RIGHT
        this.textArea1.add("right", this.box1);
    }


    private void enableHighlighters() {
        DiffHighlight diffHighlight0 =
             (DiffHighlight) DiffHighlight.getHighlightFor(this.editPane0);

        if (diffHighlight0 == null) {
            diffHighlight0 = (DiffHighlight)
                DiffHighlight.addHighlightTo(this.editPane0, this.edits, DiffHighlight.LEFT);
            this.textArea0.getPainter().addCustomHighlight(diffHighlight0);
        } else {
            diffHighlight0.setEdits(this.edits);
            diffHighlight0.setPosition(DiffHighlight.LEFT);
        }

        DiffHighlight diffHighlight1 =
            (DiffHighlight) DiffHighlight.getHighlightFor(this.editPane1);

        if (diffHighlight1 == null) {
            diffHighlight1 = (DiffHighlight)
                DiffHighlight.addHighlightTo(this.editPane1, this.edits, DiffHighlight.RIGHT);
            this.textArea1.getPainter().addCustomHighlight(diffHighlight1);
        } else {
            diffHighlight1.setEdits(this.edits);
            diffHighlight1.setPosition(DiffHighlight.RIGHT);
        }

        diffHighlight0.setEnabled(true);
        diffHighlight0.updateTextArea();
        diffHighlight1.setEnabled(true);
        diffHighlight1.updateTextArea();
    }


    private void disableHighlighters() {
        DiffHighlight diffHighlight0 =
             (DiffHighlight) DiffHighlight.getHighlightFor(this.editPane0);

        if (diffHighlight0 != null) {
            diffHighlight0.setEnabled(false);
            diffHighlight0.updateTextArea();
        }

        DiffHighlight diffHighlight1 =
            (DiffHighlight) DiffHighlight.getHighlightFor(this.editPane1);

        if (diffHighlight1 != null) {
            diffHighlight1.setEnabled(false);
            diffHighlight1.updateTextArea();
        }
    }


    private void addHandlers() {
        this.vertical0.addAdjustmentListener((AdjustmentListener) this.adjustHandler);
        this.vertical0.addMouseListener((MouseListener) this.adjustHandler);
        this.textArea0.addFocusListener((FocusListener) this.adjustHandler);

        this.vertical1.addAdjustmentListener((AdjustmentListener) this.adjustHandler);
        this.vertical1.addMouseListener((MouseListener) this.adjustHandler);
        this.textArea1.addFocusListener((FocusListener) this.adjustHandler);
    }


    private void removeHandlers() {
        this.vertical0.removeAdjustmentListener((AdjustmentListener) this.adjustHandler);
        this.vertical0.removeMouseListener((MouseListener) this.adjustHandler);
        this.textArea0.removeFocusListener((FocusListener) this.adjustHandler);

        this.vertical1.removeAdjustmentListener((AdjustmentListener) this.adjustHandler);
        this.vertical1.removeMouseListener((MouseListener) this.adjustHandler);
        this.textArea1.removeFocusListener((FocusListener) this.adjustHandler);
    }


    public FileData getFileData(Buffer buffer) {
        Element map = buffer.getDefaultRootElement();

        FileLine[] lines = new FileLine[map.getElementCount()];

        for (int i = map.getElementCount() - 1; i >= 0; i--) {
            Element line = map.getElement(i);
            int start = line.getStartOffset();
            int end   = line.getEndOffset();

            // We get the line i without the line separator (always \n)
            int len = (end - 1) - start;
            if (len == 0) {
                lines[i] = new FileLine("", ignoreCase);
                continue;
            }

            String text = "";
            try {
                text = buffer.getText(start, len);
            } catch (BadLocationException ble) {
                Log.log(Log.ERROR, this, ble);
            } finally {
                lines[i] = new FileLine(text, ignoreCase);
            }
        }

        return new FileData(buffer.getName(), lines);
    }


    public JScrollBar findVerticalScrollBar(Container container) {
        Component[] comps = container.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (   (comps[i] instanceof JScrollBar)
                && (((JScrollBar) comps[i]).getOrientation() == JScrollBar.VERTICAL)
            ) {
                return (JScrollBar) comps[i];
            }
        }

        return null;
    }


    public Box findBox(Container container) {
        Component[] comps = container.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof Box) {
                return (Box) comps[i];
            }
        }

        return null;
    }


    public static boolean isEnabledFor(View view) {
        return (dualDiffs.get(view) != null);
    }


    public static void editPaneCreated(View view, EditPane editPane) {
        DualDiff.removeFrom(view);
    }

    public static void editPaneDestroyed(View view, EditPane editPane) {
        DualDiff.removeFrom(view);
        DiffHighlight.removeHighlightFrom(editPane);
    }

    public static void editPaneBufferChanged(View view, EditPane editPane) {
        DualDiff.removeFrom(view);
        DualDiff.addTo(view);
    }


    public static void toggle(View view) {
        EditPane[] editPanes = view.getEditPanes();
        if (editPanes.length != 2) {
            Log.log(Log.DEBUG, DualDiff.class, "Splitting The view has to be split in two");
            if (editPanes.length > 2) {
                view.unsplit();
            }
            view.splitVertically();
        }

        if (DualDiff.isEnabledFor(view)) {
            DualDiff.removeFrom(view);
        } else {
            DualDiff.addTo(view);
        }

        view.invalidate();
        view.validate();
    }


    private static void addTo(View view) {
        DualDiff dualDiff = new DualDiff(view);

        dualDiff.enableHighlighters();
        dualDiff.addHandlers();

        dualDiff.diffOverview0.synchroScrollRight();

        dualDiff.diffOverview1.repaint();

        dualDiffs.put(view, dualDiff);
    }


    private static void removeFrom(View view) {
        DualDiff dualDiff = (DualDiff) dualDiffs.get(view);
        if (dualDiff != null) {
            dualDiff.removeHandlers();
            dualDiff.disableHighlighters();

            dualDiff.textArea0.remove(dualDiff.box0);
            // JEditTextArea.RIGHT is private...
            // "right" == JEditTextArea.RIGHT
            dualDiff.textArea0.add("right", dualDiff.vertical0);

            dualDiff.textArea1.remove(dualDiff.box1);
            // JEditTextArea.RIGHT is private...
            // "right" == JEditTextArea.RIGHT
            dualDiff.textArea1.add("right", dualDiff.vertical1);

            dualDiffs.remove(view);
        }
    }


    private class AdjustHandler
            implements AdjustmentListener, FocusListener, MouseListener
    {
        private Object source = null;

        private Runnable syncWithRight = new Runnable() {
            public void run() {
                DualDiff.this.diffOverview0.repaint();
                DualDiff.this.diffOverview0.synchroScrollRight();

                DualDiff.this.diffOverview1.repaint();
            }
        };

        private Runnable syncWithLeft = new Runnable() {
            public void run() {
                DualDiff.this.diffOverview1.repaint();
                DualDiff.this.diffOverview1.synchroScrollLeft();

                DualDiff.this.diffOverview0.repaint();
            }
        };


        public AdjustHandler() {}


        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (this.source == null) {
                this.source = e.getSource();
            }
            // Log.log(Log.DEBUG, this, "**** Adjustment " + e);

            if (this.source == DualDiff.this.vertical0) {
                SwingUtilities.invokeLater(this.syncWithRight);
            } else if (this.source == DualDiff.this.vertical1) {
                SwingUtilities.invokeLater(this.syncWithLeft);
            } else {}
        }


        public void focusGained(FocusEvent e) {
            Log.log(Log.DEBUG, this, "**** focusGained " + e);

            if (e.getSource() == DualDiff.this.textArea0) {
                this.source = DualDiff.this.vertical0;
            } else if (e.getSource() == DualDiff.this.textArea1) {
                this.source = DualDiff.this.vertical1;
            } else {}
        }


        public void focusLost(FocusEvent e) {
            Log.log(Log.DEBUG, this, "**** focusLost " + e);
        }


        public void mouseClicked(MouseEvent e) {}


        public void mouseEntered(MouseEvent e) {}


        public void mouseExited(MouseEvent e) {}


        public void mousePressed(MouseEvent e) {
            Log.log(Log.DEBUG, this, "**** mousePressed " + e);
            this.source = e.getSource();
        }


        public void mouseReleased(MouseEvent e) {
            Log.log(Log.DEBUG, this, "**** mouseReleased " + e);
            // this.source = null;
        }
    }


    public static void propertiesChanged() {
        boolean newIgnoreCase = jEdit.getBooleanProperty("jdiff.ignore-case", false);
        if (newIgnoreCase != ignoreCase) {
            ignoreCase = newIgnoreCase;
            for (Enumeration e = dualDiffs.keys(); e.hasMoreElements(); ) {
                View view = (View) e.nextElement();
                DualDiff.removeFrom(view);
                DualDiff.addTo(view);
            }
        }
    }
}
