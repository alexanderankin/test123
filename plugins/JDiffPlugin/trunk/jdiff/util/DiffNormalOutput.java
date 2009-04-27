/*
 * DiffNormalOutput.java
 * Copyright (c) 2001, 2002 Andre Kaplan
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


package jdiff.util;

import java.io.IOException;


/** Print a change list in the standard diff format. */
public class DiffNormalOutput extends DiffOutput {
    public DiffNormalOutput(Object[] a, Object[] b) {
        super(a, b);
    }


    public void writeScript(Diff.Change script) throws IOException {
        Diff.Change hunk = script;

        for (; hunk != null; hunk = hunk.next) {
            this.writeHunk(hunk);
        }

        this.out.flush();
    }


    /** Writes a hunk of a normal diff. */
    protected void writeHunk(Diff.Change hunk) throws IOException {
        int deletes = hunk.lines0;
        int inserts = hunk.lines1;

        if (deletes == 0 && inserts == 0) {
            return;
        }

        // Determine range of line numbers involved in each file.
        int first0 = hunk.first0;
        int first1 = hunk.first1;
        int last0 = hunk.first0 + hunk.lines0 - 1;
        int last1 = hunk.first1 + hunk.lines1 - 1;

        // Write out the line number header for this hunk
        this.writeNumberRange(',', first0, last0);
        out.write(DiffOutput.changeLetter(inserts, deletes));
        this.writeNumberRange(',', first1, last1);
        out.write(this.lineSeparator);

        // Write the lines that the first file has.
        if (deletes != 0) {
            for (int i = first0; i <= last0; i++) {
                this.writeLine("< ", this.file0[i]);
            }
        }

        if (inserts != 0 && deletes != 0) {
            out.write("---" + this.lineSeparator);
        }

        // Write the lines that the second file has.
        if (inserts != 0) {
            for (int i = first1; i <= last1; i++) {
                this.writeLine("> ", this.file1[i]);
            }
        }
    }
}

