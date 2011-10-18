/*
* jEdit - Programmer's Text Editor
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright © 2011 Matthieu Casanova
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package gatchan.phpparser.hyperlink;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.jedit.hyperlinks.HyperlinkSource;
import gatchan.phpparser.sidekick.PHPSideKickParser;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import net.sourceforge.phpdt.internal.compiler.ast.PHPDocument;
import org.gjt.sp.jedit.Buffer;

/**
 * @author Matthieu Casanova
 */
public class PHPHyperlinkSource implements HyperlinkSource
{
	private DefaultNodeDecoder defaultNodeDecoder;

	public PHPHyperlinkSource()
	{
		defaultNodeDecoder = new DefaultNodeDecoder();
	}

	//{{{ getHyperlink() method
	@Override
	public Hyperlink getHyperlink(Buffer buffer, int offset)
	{
		PHPDocument phpDocument = (PHPDocument) buffer.getProperty(PHPSideKickParser.PHPDOCUMENT_PROPERTY);
		if (phpDocument == null)
		{
			return null;
		}
		int line = buffer.getLineOfOffset(offset);
		int lineStartOffset = buffer.getLineStartOffset(line);
		int lineOffset = offset - lineStartOffset;
		AstNode statement = phpDocument.getNodeAt(line + 1  /* +1 because first line in my parser is 1*/,
							    lineOffset);
		if (statement == null)
			return null;
		return defaultNodeDecoder.getHyperlink(statement, buffer, line, lineOffset);
	} //}}}
}
