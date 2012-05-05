/*
 * ElementUtil.java - replacement for sidekick.util.ElementUtil
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012, Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;

import org.gjt.sp.jedit.Buffer;

import sidekick.util.Location;

public class ElementUtil {

	/*
	 * adapted from sidekick.util.ElementUtil.createEndPosition()
	 */
	public static int createOffset(Buffer buffer, Location loc){
		int line = Math.max(
		                   Math.min(loc.line - 1, buffer.getLineCount() - 1)
		                 , 0);
		int line_offset = buffer.getLineStartOffset(line);
		int[] totalVirtualWidth = new int[ 1 ];
		int column_offset = buffer.getOffsetOfVirtualColumn(
			    Math.max( line, 0 ),
			    Math.max( loc.column - 1, 0 ),
			    totalVirtualWidth );
		if ( column_offset == -1 ) {
			//Log.log(Log.DEBUG,ElementUtil.class,"wanted virtual column "+(loc.column-1)+", totalVirtualWitdth="+totalVirtualWidth[0]);
			if(loc.column-1 == totalVirtualWidth[ 0 ]){
				//Log.log(Log.DEBUG,ElementUtil.class,"setting offset to real end of line offset");
				column_offset = buffer.getLineLength(line);
			}else {
				//Log.log(Log.DEBUG,ElementUtil.class,"setting offset to virtual width");
				column_offset = totalVirtualWidth[ 0 ];
			}
			//Log.log(Log.DEBUG,ElementUtil.class,"changed column_offset:"+column_offset);
		}
		return line_offset + column_offset ;
	}

}