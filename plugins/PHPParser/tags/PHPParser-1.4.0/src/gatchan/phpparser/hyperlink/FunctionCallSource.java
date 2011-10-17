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

import java.util.List;

import gatchan.jedit.hyperlinks.Hyperlink;
import gatchan.phpparser.project.Project;
import gatchan.phpparser.project.ProjectManager;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import net.sourceforge.phpdt.internal.compiler.ast.ClassIdentifier;
import net.sourceforge.phpdt.internal.compiler.ast.Expression;
import net.sourceforge.phpdt.internal.compiler.ast.FunctionCall;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;

/**
 * @author Matthieu Casanova
 */
public class FunctionCallSource implements HyperlinkDecoder<FunctionCall>
{
	private HyperlinkDecoder<AstNode> defaultDecoder;

	public FunctionCallSource(DefaultNodeDecoder decoder)
	{
		defaultDecoder = decoder;
	}

	@Override
	public boolean accept(AstNode node)
	{
		return node instanceof FunctionCall;
	}

	@Override
	public Hyperlink getHyperlink(FunctionCall functionCall, Buffer buffer, int line, int lineOffset)
	{
		Project project = ProjectManager.getInstance().getProject();
		if (project == null)
			return null;
		if (functionCall.getFunctionName() instanceof ClassIdentifier)
		{
			return defaultDecoder.getHyperlink(functionCall.getFunctionName(), buffer, line, lineOffset);
		}
		Expression functionName = functionCall.getFunctionName();
		AstNode expressionAt = functionCall.subNodeAt(line + 1, lineOffset);
		if (expressionAt != functionName)
		{
			if (expressionAt == null)
				return null;
			// the cursor is not on the function name
			return defaultDecoder.getHyperlink(expressionAt, buffer, line, lineOffset);
		}
		String name = functionName.toString().toLowerCase();

		Object o = project.getMethods().get(name);
		if (o == null)
			return null;
		MethodHeader header = null;
		if (o instanceof List)
		{
			List<MethodHeader> headers = (List<MethodHeader>) o;
			for (MethodHeader methodHeader : headers)
			{
				header = methodHeader;
				break;
			}
		}
		else
		{
			header = (MethodHeader) o;
		}
		if (header == null)
			return null;
		CharSequence lineSegment = buffer.getLineSegment(line);
		String noWordSep = buffer.getStringProperty("noWordSep");
		int wordStart = TextUtilities.findWordStart(lineSegment, lineOffset, noWordSep);
		int wordEnd = TextUtilities.findWordEnd(lineSegment, lineOffset, noWordSep);
		int lineStartOffset = buffer.getLineStartOffset(line);
		Hyperlink hyperlink = new PHPHyperlink(header.getName(), header.getPath(), header.getBeginLine(), lineStartOffset + wordStart, lineStartOffset + wordEnd, line);
		return hyperlink;
	}
}
