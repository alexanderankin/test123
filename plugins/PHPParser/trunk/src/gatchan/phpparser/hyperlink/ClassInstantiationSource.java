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
import gatchan.phpparser.project.Project;
import gatchan.phpparser.project.ProjectManager;
import net.sourceforge.phpdt.internal.compiler.ast.AstNode;
import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.ClassInstantiation;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;

/**
 * @author Matthieu Casanova
 */
public class ClassInstantiationSource implements HyperlinkDecoder<ClassInstantiation>
{
	@Override
	public boolean accept(AstNode node)
	{
		return node instanceof ClassInstantiation;
	}

	@Override
	public Hyperlink getHyperlink(ClassInstantiation classInstantiation, Buffer buffer, int line, int lineOffset)
	{
		String className = classInstantiation.getType().getClassName();
		if (className == null)
			return null;
		Project project = ProjectManager.getInstance().getProject();
		ClassHeader classHeader = project.getClass(className);
		if (classHeader == null)
			return null;
		CharSequence lineSegment = buffer.getLineSegment(line);
		String noWordSep = buffer.getStringProperty("noWordSep");
		int wordStart = TextUtilities.findWordStart(lineSegment, lineOffset, noWordSep);
		int wordEnd = TextUtilities.findWordEnd(lineSegment, lineOffset, noWordSep);
		int lineStartOffset = buffer.getLineStartOffset(line);
		Hyperlink hyperlink = new PHPHyperlink(classHeader.getName(), classHeader.getPath(), classHeader.getBeginLine(), lineStartOffset + wordStart, lineStartOffset + wordEnd, line);
		return hyperlink;
	}
}
