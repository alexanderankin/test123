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

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.StringWriter;

import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import jdiff.text.FileLine;
import jdiff.util.Diff;
import jdiff.util.DiffOutput;
import jdiff.util.DiffNormalOutput;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import org.gjt.sp.util.Log;


public class DualDiff {
    private static boolean ignoreCaseDefault  =
        jEdit.getBooleanProperty("jdiff.ignore-case", false);
    private static boolean trimWhitespaceDefault =
        jEdit.getBooleanProperty("jdiff.trim-whitespace", false);
    private static boolean ignoreWhitespaceDefault =
        jEdit.getBooleanProperty("jdiff.ignore-whitespace", false);
    private static Hashtable dualDiffs = new Hashtable();

    private boolean       ignoreCase;
    private boolean       trimWhitespace;
    private boolean       ignoreWhitespace;

    private View          view;
    private EditPane      editPane0;
    private EditPane      editPane1;
    private JEditTextArea textArea0;
    private JEditTextArea textArea1;

    private Diff.change   edits;

    private JScrollBar    horizontal0;
    private JScrollBar    horizontal1;

    private DiffOverview  diffOverview0;
    private DiffOverview  diffOverview1;
    private JScrollBar    vertical0;
    private JScrollBar    vertical1;
    private Box           box0;
    private Box           box1;

    private HorizontalAdjustHandler horizontalAdjust;
    private VerticalAdjustHandler   verticalAdjust;


    public DualDiff(View view) {
        this(view, ignoreCaseDefault, trimWhitespaceDefault, ignoreWhitespaceDefault);
    }


    public DualDiff(View view, boolean ignoreCase,
            boolean trimWhitespace, boolean ignoreWhiteSpace
    ) {
        this.ignoreCase       = ignoreCase;
        this.trimWhitespace   = trimWhitespace;
        this.ignoreWhitespace = ignoreWhiteSpace;

        this.view = view;

        EditPane[] editPanes = this.view.getEditPanes();

        this.editPane0 = editPanes[0];
        this.editPane1 = editPanes[1];

        this.textArea0 = this.editPane0.getTextArea();
        this.textArea1 = this.editPane1.getTextArea();

        this.horizontal0 = this.findHorizontalScrollBar(this.textArea0);
        this.horizontal1 = this.findHorizontalScrollBar(this.textArea1);

        this.box0      = new Box(BoxLayout.X_AXIS);
        this.vertical0 = this.findVerticalScrollBar(this.textArea0);

        this.box1      = new Box(BoxLayout.X_AXIS);
        this.vertical1 = this.findVerticalScrollBar(this.textArea1);

        this.initOverviews();
        this.addOverviews();
    }


    public boolean getIgnoreCase() {
        return this.ignoreCase;
    }


    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }


    public void toggleIgnoreCase() {
        this.ignoreCase = !this.ignoreCase;
    }


    public boolean getTrimWhitespace() {
        return this.trimWhitespace;
    }


    public void setTrimWhitespace(boolean trimWhitespace) {
        this.trimWhitespace = trimWhitespace;
    }


    public void toggleTrimWhitespace() {
        this.trimWhitespace = !this.trimWhitespace;
    }


    public boolean getIgnoreWhitespace() {
        return this.ignoreWhitespace;
    }


    public void setIgnoreWhitespace(boolean ignoreWhitespace) {
        this.ignoreWhitespace = ignoreWhitespace;
    }


    public void toggleIgnoreWhitespace() {
        this.ignoreWhitespace = !this.ignoreWhitespace;
    }


    private void initOverviews() {
        Buffer buf0 = this.editPane0.getBuffer();
        Buffer buf1 = this.editPane1.getBuffer();

        FileLine[] fileLines0 = this.getFileLines(buf0);
        FileLine[] fileLines1 = this.getFileLines(buf1);

        Diff d = new Diff(fileLines0, fileLines1);
        this.edits = d.diff_2(false);

        int lineCount0 = fileLines0.length;
        int lineCount1 = fileLines1.length;

        this.diffOverview0 = new DiffLocalOverview(this.edits, lineCount0, lineCount1, this.textArea0, this.textArea1);
        this.diffOverview1 = new DiffGlobalOverview(this.edits, lineCount0, lineCount1, this.textArea0, this.textArea1);

        this.horizontalAdjust = new HorizontalAdjustHandler();
        this.verticalAdjust   = new VerticalAdjustHandler();
    }


    private void addOverviews() {
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


    private void removeOverviews() {
        this.box0.remove(this.vertical0);
        this.box0.remove(this.diffOverview0);
        this.textArea0.remove(this.box0);
        // JEditTextArea.RIGHT is private...
        // "right" == JEditTextArea.RIGHT
        this.textArea0.add("right", this.vertical0);

        this.box1.remove(this.vertical1);
        this.box1.remove(this.diffOverview1);
        this.textArea1.remove(this.box1);
        // JEditTextArea.RIGHT is private...
        // "right" == JEditTextArea.RIGHT
        this.textArea1.add("right", this.vertical1);
    }


    private void refresh() {
        this.removeHandlers();
        this.disableHighlighters();

        this.removeOverviews();
        this.initOverviews();
        this.addOverviews();

        this.enableHighlighters();
        this.addHandlers();

        this.diffOverview0.synchroScrollRight();
        this.diffOverview1.repaint();
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
        this.horizontal0.addAdjustmentListener((AdjustmentListener) this.horizontalAdjust);
        this.horizontal0.addMouseListener((MouseListener) this.horizontalAdjust);
        this.textArea0.addFocusListener((FocusListener) this.horizontalAdjust);
        this.textArea0.addCaretListener((CaretListener) this.horizontalAdjust);

        this.horizontal1.addAdjustmentListener((AdjustmentListener) this.horizontalAdjust);
        this.horizontal1.addMouseListener((MouseListener) this.horizontalAdjust);
        this.textArea1.addFocusListener((FocusListener) this.horizontalAdjust);
        this.textArea1.addCaretListener((CaretListener) this.horizontalAdjust);

        this.vertical0.addAdjustmentListener((AdjustmentListener) this.verticalAdjust);
        this.vertical0.addMouseListener((MouseListener) this.verticalAdjust);
        this.textArea0.addFocusListener((FocusListener) this.verticalAdjust);
        this.textArea0.addCaretListener((CaretListener) this.verticalAdjust);

        this.vertical1.addAdjustmentListener((AdjustmentListener) this.verticalAdjust);
        this.vertical1.addMouseListener((MouseListener) this.verticalAdjust);
        this.textArea1.addFocusListener((FocusListener) this.verticalAdjust);
        this.textArea1.addCaretListener((CaretListener) this.verticalAdjust);
    }


    private void removeHandlers() {
        this.horizontal0.removeAdjustmentListener((AdjustmentListener) this.horizontalAdjust);
        this.horizontal0.removeMouseListener((MouseListener) this.horizontalAdjust);
        this.textArea0.removeFocusListener((FocusListener) this.horizontalAdjust);
        this.textArea0.removeCaretListener((CaretListener) this.horizontalAdjust);

        this.horizontal1.removeAdjustmentListener((AdjustmentListener) this.horizontalAdjust);
        this.horizontal1.removeMouseListener((MouseListener) this.horizontalAdjust);
        this.textArea1.removeFocusListener((FocusListener) this.horizontalAdjust);
        this.textArea1.removeCaretListener((CaretListener) this.horizontalAdjust);

        this.vertical0.removeAdjustmentListener((AdjustmentListener) this.verticalAdjust);
        this.vertical0.removeMouseListener((MouseListener) this.verticalAdjust);
        this.textArea0.removeFocusListener((FocusListener) this.verticalAdjust);
        this.textArea0.removeCaretListener((CaretListener) this.verticalAdjust);

        this.vertical1.removeAdjustmentListener((AdjustmentListener) this.verticalAdjust);
        this.vertical1.removeMouseListener((MouseListener) this.verticalAdjust);
        this.textArea1.removeFocusListener((FocusListener) this.verticalAdjust);
        this.textArea1.removeCaretListener((CaretListener) this.verticalAdjust);
    }


    private FileLine[] getFileLines(Buffer buffer) {
        Element map = buffer.getDefaultRootElement();

        FileLine[] lines = new FileLine[map.getElementCount()];

        for (int i = map.getElementCount() - 1; i >= 0; i--) {
            Element line = map.getElement(i);
            int start = line.getStartOffset();
            int end   = line.getEndOffset();

            // We get the line i without the line separator (always \n)
            int len = (end - 1) - start;
            if (len == 0) {
                lines[i] = new FileLine("", "");
                continue;
            }

            String text = "";
            String canonical = "";
            try {
                text = buffer.getText(start, len);
                canonical = text;
                if (ignoreCase) {
                    canonical = canonical.toUpperCase();
                }
                if (trimWhitespace) {
                    canonical = trimWhitespaces(canonical);
                }
                if (ignoreWhitespace) {
                    canonical = squeezeRepeatedWhitespaces(canonical);
                }
            } catch (BadLocationException ble) {
                Log.log(Log.ERROR, this, ble);
            } finally {
                lines[i] = new FileLine(text, canonical);
            }
        }

        return lines;
    }


    public static String squeezeRepeatedWhitespaces(String str) {
        int inLen     = str.length();
        int outLen    = 0;
        char[] inStr  = new char[inLen];
        char[] outStr = new char[inLen];
        str.getChars(0, inLen, inStr, 0);

        boolean space = false;

        int idx = 0;
        // Skip leading whitespaces
        while (idx < inLen && Character.isWhitespace(inStr[idx])) { idx++; }

        for (; idx < inLen; idx++) {
            if (Character.isWhitespace(inStr[idx])) {
                space = true;
                continue;
            }

            if (space) {
                outStr[outLen++] = ' ';
                space = false;
            }
            outStr[outLen++] = inStr[idx];
        }

        return new String(outStr, 0, outLen);
    }


    public static String trimWhitespaces(String str) {
        int inLen     = str.length();
        char[] inStr  = new char[inLen];
        str.getChars(0, inLen, inStr, 0);

        // Skip leading whitespaces
        int startIdx = 0;
        while ((startIdx < inLen) && Character.isWhitespace(inStr[startIdx])) {
            startIdx++;
        }

        // Skip trailing whitespaces
        int endIdx = inLen - 1;
        while ((endIdx >= startIdx) && Character.isWhitespace(inStr[endIdx])) {
            endIdx--;
        }

        if ((startIdx > 0) || (endIdx < inLen - 1)) {
            return new String(inStr, startIdx, endIdx - startIdx + 1);
        } else {
            return str;
        }
    }


    public JScrollBar findScrollBar(Container container, int orientation) {
        Component[] comps = container.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (   (comps[i] instanceof JScrollBar)
                && (((JScrollBar) comps[i]).getOrientation() == orientation)
            ) {
                return (JScrollBar) comps[i];
            }
        }

        return null;
    }


    public JScrollBar findHorizontalScrollBar(Container container) {
        return this.findScrollBar(container, JScrollBar.HORIZONTAL);
    }


    public JScrollBar findVerticalScrollBar(Container container) {
        return this.findScrollBar(container, JScrollBar.VERTICAL);
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


    public static DualDiff getDualDiffFor(View view) {
        return (DualDiff) dualDiffs.get(view);
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
        DualDiff.refreshFor(view);
    }


    public static void toggleFor(View view) {
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


    public static void refreshFor(View view) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(view);
        if (dualDiff != null) {
            dualDiff.refresh();

            view.invalidate();
            view.validate();
        } else {
            view.getToolkit().beep();
        }
    }


    public static boolean getIgnoreCaseFor(View view) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(view);
        if (dualDiff == null) {
            return false;
        }

        return dualDiff.getIgnoreCase();
    }


    public static void toggleIgnoreCaseFor(View view) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(view);
        if (dualDiff != null) {
            dualDiff.toggleIgnoreCase();
            dualDiff.refresh();

            view.invalidate();
            view.validate();
        } else {
            view.getToolkit().beep();
        }
    }


    public static boolean getTrimWhitespaceFor(View view) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(view);
        if (dualDiff == null) {
            return false;
        }

        return dualDiff.getTrimWhitespace();
    }


    public static void toggleTrimWhitespaceFor(View view) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(view);
        if (dualDiff != null) {
            dualDiff.toggleTrimWhitespace();
            dualDiff.refresh();

            view.invalidate();
            view.validate();
        } else {
            view.getToolkit().beep();
        }
    }


    public static boolean getIgnoreWhitespaceFor(View view) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(view);
        if (dualDiff == null) {
            return false;
        }

        return dualDiff.getIgnoreWhitespace();
    }


    public static void toggleIgnoreWhitespaceFor(View view) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(view);
        if (dualDiff != null) {
            dualDiff.toggleIgnoreWhitespace();
            dualDiff.refresh();

            view.invalidate();
            view.validate();
        } else {
            view.getToolkit().beep();
        }
    }


    public static void nextDiff(EditPane editPane) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(editPane.getView());
        if (dualDiff == null) {
            editPane.getToolkit().beep();
            return;
        }

        if (dualDiff.editPane0 == editPane) {
            dualDiff.nextDiff0();
        } else if (dualDiff.editPane1 == editPane) {
            dualDiff.nextDiff1();
        } else {
            editPane.getToolkit().beep();
        }
    }


    public static void prevDiff(EditPane editPane) {
        DualDiff dualDiff = DualDiff.getDualDiffFor(editPane.getView());
        if (dualDiff == null) {
            editPane.getToolkit().beep();
            return;
        }

        if (dualDiff.editPane0 == editPane) {
            dualDiff.prevDiff0();
        } else if (dualDiff.editPane1 == editPane) {
            dualDiff.prevDiff1();
        } else {
            editPane.getToolkit().beep();
        }
    }


    public static void diffNormalOutput(View view) {
        if (!DualDiff.isEnabledFor(view)) {
            view.getToolkit().beep();
            return;
        }

        DualDiff dualDiff = DualDiff.getDualDiffFor(view);

        // Generate the script
        Buffer buf0 = dualDiff.editPane0.getBuffer();
        Buffer buf1 = dualDiff.editPane1.getBuffer();

        FileLine[] fileLines0 = dualDiff.getFileLines(buf0);
        FileLine[] fileLines1 = dualDiff.getFileLines(buf1);

        Diff d = new Diff(fileLines0, fileLines1);
        Diff.change script = d.diff_2(false);

        // Files are identical: return
        if (script == null) {
            GUIUtilities.message(view, "jdiff.identical-files", null);
            return;
        }

        // Generate the normal output
        StringWriter sw = new StringWriter();
        DiffOutput diffOutput = new DiffNormalOutput(fileLines0, fileLines1);
        diffOutput.setOut(new BufferedWriter(sw));
        diffOutput.setLineSeparator("\n");
        try {
            diffOutput.writeScript(script);
        } catch (IOException ioe) {
            Log.log(Log.DEBUG, DualDiff.class, ioe);
        }

        // Get/create the output view and create a new buffer
        View outputView = jEdit.getFirstView();
        for (; outputView != null; outputView = outputView.getNext()) {
            if (!DualDiff.isEnabledFor(outputView)) {
                break;
            }
        }
        if (outputView == null) {
            outputView = jEdit.newView(view, view.getBuffer());
        }
        Buffer outputBuffer = jEdit.newFile(outputView);

        // Insert the normal output into the buffer
        try {
            String s = sw.toString();
            outputBuffer.insertString(0, s, null);
            // When the string ends with a newline, the generated buffer
            // adds one extra newline so we remove it
            if (s.endsWith("\n") && outputBuffer.getLength() > 0) {
                outputBuffer.remove(outputBuffer.getLength() - 1, 1);
            }
        } catch (BadLocationException ble) {
            Log.log(Log.DEBUG, DualDiff.class, ble);
        }
    }


    private void nextDiff0() {
        this.horizontalAdjust.source = this.horizontal0;
        this.verticalAdjust.source = this.vertical0;

        Diff.change hunk = this.edits;
        int firstLine = this.textArea0.getFirstLine();
        for (; hunk != null; hunk = hunk.link) {
            if (hunk.line0 > firstLine + ((hunk.deleted == 0) ? 1 : 0)) {
                int line = 0;
                if (hunk.deleted == 0 && hunk.line0 > 0) {
                    line = hunk.line0 - 1;
                } else {
                    line = hunk.line0;
                }
                this.textArea0._setFirstLine(line);
                if (this.textArea0.getFirstLine() != line) {
                    this.textArea0.getToolkit().beep();
                }
                return;
            }
        }

        this.textArea1.getToolkit().beep();
    }


    private void nextDiff1() {
        this.horizontalAdjust.source = this.horizontal1;
        this.verticalAdjust.source = this.vertical1;

        Diff.change hunk = this.edits;
        int firstLine = this.textArea1.getFirstLine();
        for (; hunk != null; hunk = hunk.link) {
            if (hunk.line1 > firstLine + ((hunk.inserted == 0) ? 1 : 0)) {
                int line = 0;
                if (hunk.inserted == 0 && hunk.line1 > 0) {
                    line = hunk.line1 - 1;
                } else {
                    line = hunk.line1;
                }
                this.textArea1._setFirstLine(line);
                if (this.textArea1.getFirstLine() != line) {
                    this.textArea1.getToolkit().beep();
                }
                return;
            }
        }

        this.textArea1.getToolkit().beep();
    }


    private void prevDiff0() {
        this.horizontalAdjust.source = this.horizontal0;
        this.verticalAdjust.source = this.vertical0;

        Diff.change hunk = this.edits;
        int firstLine = this.textArea0.getFirstLine();
        for (; hunk != null; hunk = hunk.link) {
            if (hunk.line0 < firstLine) {
                if (hunk.link == null || hunk.link.line0 >= firstLine) {
                    int line = 0;
                    if (hunk.deleted == 0 && hunk.line0 > 0) {
                        line = hunk.line0 - 1;
                    } else {
                        line = hunk.line0;
                    }
                    this.textArea0._setFirstLine(line);
                    if (this.textArea0.getFirstLine() != line) {
                        this.textArea0.getToolkit().beep();
                    }
                    return;
                }
            }
        }

        this.textArea0.getToolkit().beep();
    }


    private void prevDiff1() {
        this.horizontalAdjust.source = this.horizontal1;
        this.verticalAdjust.source = this.vertical1;

        Diff.change hunk = this.edits;
        int firstLine = this.textArea1.getFirstLine();
        for (; hunk != null; hunk = hunk.link) {
            if (hunk.line1 < firstLine) {
                if (hunk.link == null || hunk.link.line1 >= firstLine) {
                    int line = 0;
                    if (hunk.inserted == 0 && hunk.line1 > 0) {
                        line = hunk.line1 - 1;
                    } else {
                        line = hunk.line1;
                    }
                    this.textArea1._setFirstLine(line);
                    if (this.textArea1.getFirstLine() != line) {
                        this.textArea1.getToolkit().beep();
                    }
                    return;
                }
            }
        }

        this.textArea1.getToolkit().beep();
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

            dualDiff.removeOverviews();

            dualDiffs.remove(view);
        }
    }


    private class VerticalAdjustHandler
            implements AdjustmentListener, CaretListener, FocusListener, MouseListener
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


        public VerticalAdjustHandler() {}


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


        public void caretUpdate(CaretEvent e) {
            if (e.getSource() == DualDiff.this.textArea0) {
                this.source = DualDiff.this.vertical0;
            } else if (e.getSource() == DualDiff.this.textArea1) {
                this.source = DualDiff.this.vertical1;
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
        }
    }


    private class HorizontalAdjustHandler
            implements AdjustmentListener, CaretListener, FocusListener, MouseListener
    {
        private Object source = null;

        private Runnable syncWithRight = new Runnable() {
            public void run() {
                // DualDiff.this.diffOverview0.repaint();
                DualDiff.this.textArea1._setHorizontalOffset(
                    DualDiff.this.textArea0.getHorizontalOffset()
                );

                // DualDiff.this.diffOverview1.repaint();
            }
        };

        private Runnable syncWithLeft = new Runnable() {
            public void run() {
                // DualDiff.this.diffOverview1.repaint();
                DualDiff.this.textArea0._setHorizontalOffset(
                    DualDiff.this.textArea1.getHorizontalOffset()
                );

                // DualDiff.this.diffOverview0.repaint();
            }
        };


        public HorizontalAdjustHandler() {}


        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (this.source == null) {
                this.source = e.getSource();
            }
            // Log.log(Log.DEBUG, this, "**** Adjustment " + e);

            if (this.source == DualDiff.this.horizontal0) {
                SwingUtilities.invokeLater(this.syncWithRight);
            } else if (this.source == DualDiff.this.horizontal1) {
                SwingUtilities.invokeLater(this.syncWithLeft);
            } else {}
        }


        public void caretUpdate(CaretEvent e) {
            if (e.getSource() == DualDiff.this.textArea0) {
                this.source = DualDiff.this.horizontal0;
            } else if (e.getSource() == DualDiff.this.textArea1) {
                this.source = DualDiff.this.horizontal1;
            } else {}
        }


        public void focusGained(FocusEvent e) {
            Log.log(Log.DEBUG, this, "**** focusGained " + e);

            if (e.getSource() == DualDiff.this.textArea0) {
                this.source = DualDiff.this.horizontal0;
            } else if (e.getSource() == DualDiff.this.textArea1) {
                this.source = DualDiff.this.horizontal1;
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
        }
    }


    public static void propertiesChanged() {
        boolean newIgnoreCaseDefault =
            jEdit.getBooleanProperty("jdiff.ignore-case", false);
        boolean newTrimWhitespaceDefault =
            jEdit.getBooleanProperty("jdiff.trim-whitespace", false);
        boolean newIgnoreWhitespaceDefault =
            jEdit.getBooleanProperty("jdiff.ignore-whitespace", false);

        if (   (newIgnoreCaseDefault       != ignoreCaseDefault)
            || (newTrimWhitespaceDefault   != trimWhitespaceDefault)
            || (newIgnoreWhitespaceDefault != ignoreWhitespaceDefault)
        ) {
            ignoreCaseDefault       = newIgnoreCaseDefault;
            trimWhitespaceDefault   = newTrimWhitespaceDefault;
            ignoreWhitespaceDefault = newIgnoreWhitespaceDefault;
        }
    }
}
