/*
 * DocumentUtilities.java
 * Copyright (c) 2000-2001 Andre Kaplan
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


package whitespace;


import javax.swing.text.Segment;
import org.gjt.sp.jedit.buffer.JEditBuffer;

import org.gjt.sp.util.StandardUtilities;


public class DocumentUtilities {
    private DocumentUtilities() {}


    public static void untabifyLeading(JEditBuffer buffer, int tabSize) {
        WhiteSpaceInfo whiteSpaceInfo = new WhiteSpaceInfo();

        for (int i = buffer.getLineCount() - 1; i >= 0; i--) {
            int start = buffer.getLineStartOffset(i);
            int end   = buffer.getLineEndOffset(i);

            // We get the line i without the line separator (always \n)
            int len = (end - 1) - start;
            if (len == 0) { continue; }

            Segment s = new Segment();
            buffer.getText(start, len, s);

            getLeadingWhiteSpaceInfo(s.array, s.offset, s.count, tabSize, whiteSpaceInfo);

            if (whiteSpaceInfo.hasTabs && whiteSpaceInfo.len > 0) {
                String textOut = StandardUtilities.createWhiteSpace(whiteSpaceInfo.expandedLen, 0);

                buffer.remove(start, whiteSpaceInfo.len);
                buffer.insert(start, textOut);
            }
        }
    }


    public static void tabifyLeading(JEditBuffer buffer, int tabSize) {
        WhiteSpaceInfo whiteSpaceInfo = new WhiteSpaceInfo();

        for (int i = buffer.getLineCount() - 1; i >= 0; i--) {
            int start = buffer.getLineStartOffset(i);
            int end   = buffer.getLineEndOffset(i);

            // We get the line i without the line separator (always \n)
            int len = (end - 1) - start;
            if (len == 0) { continue; }

            Segment s = new Segment();
            buffer.getText(start, len, s);

            getLeadingWhiteSpaceInfo(s.array, s.offset, s.count, tabSize, whiteSpaceInfo);

            if (whiteSpaceInfo.hasSpaces && whiteSpaceInfo.len > 0) {
                String textOut = StandardUtilities.createWhiteSpace(whiteSpaceInfo.expandedLen, tabSize);

                buffer.remove(start, whiteSpaceInfo.len);
                buffer.insert(start, textOut);
            }
        }
    }


    public static void removeTrailingWhiteSpace(JEditBuffer buffer, String escapeChars) {
        for (int i = buffer.getLineCount() - 1; i >= 0; i--) {
            int start = buffer.getLineStartOffset(i);
            int end   = buffer.getLineEndOffset(i);

            // We get the line i without the line separator (always \n)
            int len = (end - 1) - start;
            if (len == 0) { continue; }

            Segment s = new Segment();
            buffer.getText(start, len, s);

            int off = s.offset + s.count - 1;
            int cnt = 0;
            for (; off >= s.offset; off--, cnt++) {
                char c = s.array[off];
                if (c != ' ' && c != '\t') {
                    if (escapeChars.indexOf(c) != -1) {
                        if (cnt > 0) { off++; cnt--; }
                    }
                    if (cnt > 0) {
                        buffer.remove((end - 1) - cnt, cnt);
                    }
                    break;
                }

                if (off == s.offset) {
                    // The line contains only whitespaces
                    buffer.remove(start, len);
                    break;
                }
            }
        }
    }


    private static class WhiteSpaceInfo {
        public int len           = 0;
        public int expandedLen   = 0;
        public boolean hasTabs   = false;
        public boolean hasSpaces = false;

        public WhiteSpaceInfo() {
            this(0, 0, false, false);
        }

        public WhiteSpaceInfo(int len, int expandedLen, boolean hasTabs, boolean hasSpaces) {
            this.len         = len;
            this.expandedLen = expandedLen;
            this.hasTabs     = hasTabs;
            this.hasSpaces   = hasSpaces;
        }
    }


    private static void getLeadingWhiteSpaceInfo(char[] array, int off,
            int len, int tabSize, WhiteSpaceInfo results
    ) {
        int resultLen         = 0;
        int resultExpandedLen = 0;
        boolean hasTabs   = false;
        boolean hasSpaces = false;

loop:   for (int i = len - 1; i >= 0; i--, off++) {
            char c = array[off];
            switch (c) {
            case '\t':
                hasTabs = true;
                resultLen++;
                resultExpandedLen += tabSize - (resultExpandedLen % tabSize);
                break;

            case ' ':
                hasSpaces = true;
                resultLen++;
                resultExpandedLen++;
                break;

            default:
                break loop;
            }
        }

        results.len         = resultLen;
        results.expandedLen = resultExpandedLen;
        results.hasTabs     = hasTabs;
        results.hasSpaces   = hasSpaces;
    }
}
