/**
 * CssSideKickParser.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Jakub Roztocil
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


package sidekick.css;

//{{{ Imports
import errorlist.*;
import java.util.regex.*;
import javax.swing.tree.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;
import sidekick.*;
import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import sidekick.enhanced.*; 
//}}}

/**
 * @deprecated use CSS2SideKickParser instead.
 */
public class CssSideKickParser extends SideKickParser {

	Pattern selRe = Pattern.compile("((^|[}{])\\s*)+(.+?)(\\s*\\{)",  Pattern.DOTALL);
	Pattern commentRe = Pattern.compile("\\s*\\/\\*.*?\\*\\/\\s*|\n",  Pattern.DOTALL);
	
	String text;

	//{{{ CssSideKickParser constructor
	public CssSideKickParser() {
		super("css");
	} //}}}

	//{{{ parse() method
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
		int startPos;
		int endPos;
		
		Matcher selMatcher;		// matcher for selectors
		Matcher commentMatcher;	// matcher for coments
		String[] tlc;			// top level commads - selectors, @rules

		SideKickParsedData data = new SideKickParsedData(buffer.getName());
		text = buffer.getText(0, buffer.getLength() -1);
		
		selMatcher = selRe.matcher(text);

		// selMatcher should find only selectors, but if there are some
		// at-rules like @import or @media, they are matched also.
		// @imports are almost OK, but for all at-rules is startPos incorrect
		// and first selector inside @media rule is not shown
		
		// TODO: correct parsing of at-rulles
		while (selMatcher.find()) {
			
			// Start position
			startPos = selMatcher.start() + selMatcher.group(1).length() + selMatcher.group(3).length();
			
			// Remove comments and redundant white spaces  from selector list: 
			// div#menu  \n   /* this is cool selector */ ul    li ===> div#menu ul li
			commentMatcher = commentRe.matcher(selMatcher.group(3));
			tlc = commentMatcher.replaceAll(" ").trim().split("\\s*;\\s*");
			// Log.log(Log.DEBUG, CssSideKickParser.class, tlc);
			
			for (int i = 0; i < tlc.length; i++) {
				endPos = selMatcher.end() + buffer.getText(selMatcher.end(), buffer.getLength() - 1 - selMatcher.end()).indexOf("}");
				
				if (endPos == -1) {
					endPos = buffer.getLength() -1;
				} 
				
				// Log.log(Log.DEBUG, CssSideKickParser.class, "Start: " + startPos + " End: " + endPos);
				 
				SourceAsset asset = new SourceAsset(tlc[i], getLineNo(startPos), 	buffer.createPosition(startPos));
				IAsset iasset = (IAsset) asset;
				
				// Log.log(Log.DEBUG, CssSideKickParser.class, iasset);
				
				iasset.setStart(buffer.createPosition(startPos));
				iasset.setEnd(buffer.createPosition(endPos)); 
				data.root.add(new DefaultMutableTreeNode(iasset));
			}
		}

		return data;
	} //}}}

	//{{{ getLineNo() method
	public int getLineNo(int start) {
		return text.substring(0, start).split("\n").length;
	} //}}}

	//{{{ supportsCompletion() method
	public boolean supportsCompletion() {
		return true;
	} //}}}
	
	//{{{ canCompleteAnywhere() method
	public boolean canCompleteAnywhere() {
		return true;
	} //}}}
	
	//{{{ complete() method
	public SideKickCompletion complete(EditPane editPane, int caret) {
		CompletionRequest cr = new CompletionRequest(editPane, caret);
		return cr.getSideKickCompletion();
	} //}}}

}
