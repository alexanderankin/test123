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

package jdiff.util.patch.unified;

import java.util.List;

public class UnifiedChunk {
    public int oldStartLine = 0;
    public int oldRange = 1;
    public int newStartLine = 0;
    public int newRange = 1;
    public List<String> lines;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("UnifiedChunk[")
        .append("oldStartLine=").append(oldStartLine)
        .append(",oldRange=").append(oldRange)
        .append(",newStartLine=").append(newStartLine)
        .append(",newRange=").append(newRange)
        .append("lines=").append(lines.toString()).append("]");
        return sb.toString();
    }
}