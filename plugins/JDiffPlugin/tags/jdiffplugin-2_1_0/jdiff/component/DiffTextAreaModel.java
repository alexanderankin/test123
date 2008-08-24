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


package jdiff.component;

import java.util.*;

import jdiff.*;
import jdiff.text.FileLine;
import jdiff.util.Diff;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class DiffTextAreaModel {

    private DualDiff dualDiff;
    private Diff.Change edits = null;
    private HashMap<Integer, Diff.Change> leftHunkMap = null;
    private HashMap<Integer, Diff.Change> rightHunkMap = null;

    private int leftLineCount;
    private int rightLineCount;
    private JEditTextArea leftTextArea;
    private JEditTextArea rightTextArea;

    public DiffTextAreaModel(DualDiff dualDiff) {
        this.dualDiff = dualDiff;
        prepData();
    }

    public Diff.Change getEdits() {
        return edits;
    }

    public int getLeftLineCount() {
        return leftLineCount;
    }

    public int getRightLineCount() {
        return rightLineCount;
    }

    public JEditTextArea getLeftTextArea() {
        return leftTextArea;
    }

    public JEditTextArea getRightTextArea() {
        return rightTextArea;
    }

    public HashMap<Integer, Diff.Change> getLeftHunkMap() {
        return leftHunkMap;
    }

    public HashMap<Integer, Diff.Change> getRightHunkMap() {
        return rightHunkMap;
    }

    private void prepData() {
        EditPane[] editPanes = dualDiff.getView().getEditPanes();
        Buffer buf0 = editPanes[0].getBuffer();
        Buffer buf1 = editPanes[1].getBuffer();
        leftTextArea = editPanes[0].getTextArea();
        rightTextArea = editPanes[1].getTextArea();

        if ( !buf0.isLoaded() || !buf1.isLoaded() ) {
            edits = null;
            leftLineCount = ( ( buf0.isLoaded() ) ? buf0.getLineCount() : 1 );
            rightLineCount = ( ( buf1.isLoaded() ) ? buf1.getLineCount() : 1 );
        }
        else {
            FileLine[] fileLines0 = this.getFileLines( buf0 );
            FileLine[] fileLines1 = this.getFileLines( buf1 );

            Diff d = new Diff( fileLines0, fileLines1 );
            edits = d.diff_2( false );
            leftHunkMap = new HashMap<Integer, Diff.Change>();
            rightHunkMap = new HashMap<Integer, Diff.Change>();
            Diff.Change hunk = edits;
            for ( ; hunk != null; hunk = hunk.link ) {
                for (int i = 0; i < Math.max(1, hunk.deleted); i++) {
                    leftHunkMap.put(hunk.line0 + i, hunk);
                }
                for (int i = 0; i < Math.max(1, hunk.inserted); i++) {
                    rightHunkMap.put(hunk.line1 + i, hunk);
                }
            }
            /*
            System.out.println("++++++++++++++++++++++++++++++++++");
            Set<Map.Entry<Integer, Diff.Change>> entries = leftHunkMap.entrySet();
            for (Map.Entry<Integer, Diff.Change> entry : entries) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            System.out.println("++++++++++++++++++++++++++++++++++");
            entries = rightHunkMap.entrySet();
            for (Map.Entry<Integer, Diff.Change> entry : entries) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            System.out.println("++++++++++++++++++++++++++++++++++");
            */

            leftLineCount = fileLines0.length;
            rightLineCount = fileLines1.length;
        }

    }

    private FileLine[] getFileLines( Buffer buffer ) {
        FileLine[] lines = new FileLine[ buffer.getLineCount() ];

        for ( int i = buffer.getLineCount() - 1; i >= 0; i-- ) {
            int start = buffer.getLineStartOffset( i );
            int end = buffer.getLineEndOffset( i );

            // We get the line i without the line separator (always
            // \n)
            int len = ( end - 1 ) - start;
            if ( len == 0 ) {
                lines[ i ] = new FileLine( "", "" );
                continue;
            }

            String text = "";
            String canonical = "";

            text = buffer.getText( start, len );
            canonical = text;
            if ( DualDiff.getIgnoreCaseFor(dualDiff.getView()) ) {
                canonical = canonical.toUpperCase();
            }
            if ( DualDiff.getTrimWhitespaceFor(dualDiff.getView()) && !DualDiff.getIgnoreAllWhitespaceFor(dualDiff.getView()) ) {
                canonical = dualDiff.trimWhitespaces( canonical );
            }
            if ( DualDiff.getIgnoreAmountOfWhitespaceFor(dualDiff.getView()) && !DualDiff.getIgnoreAllWhitespaceFor(dualDiff.getView()) ) {
                canonical = dualDiff.squeezeRepeatedWhitespaces( canonical );
            }
            if ( DualDiff.getIgnoreAllWhitespaceFor(dualDiff.getView()) ) {
                canonical = dualDiff.removeWhitespaces( canonical );
            }

            lines[ i ] = new FileLine( text, canonical );
        }

        return lines;
    }
}
