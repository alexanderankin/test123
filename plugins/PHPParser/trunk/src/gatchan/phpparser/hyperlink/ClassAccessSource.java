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
import net.sourceforge.phpdt.internal.compiler.ast.ClassAccess;
import net.sourceforge.phpdt.internal.compiler.ast.ClassHeader;
import net.sourceforge.phpdt.internal.compiler.ast.ConstantIdentifier;
import net.sourceforge.phpdt.internal.compiler.ast.Expression;
import net.sourceforge.phpdt.internal.compiler.ast.FieldDeclaration;
import net.sourceforge.phpdt.internal.compiler.ast.FunctionCall;
import net.sourceforge.phpdt.internal.compiler.ast.MethodHeader;
import net.sourceforge.phpdt.internal.compiler.ast.Type;
import net.sourceforge.phpdt.internal.compiler.ast.Variable;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;

/**
 * @author Matthieu Casanova
 */
public class ClassAccessSource implements HyperlinkDecoder<ClassAccess>
{
	private HyperlinkDecoder<AstNode> defaultDecoder;

	public ClassAccessSource(DefaultNodeDecoder decoder)
	{
		defaultDecoder = decoder;
	}

	@Override
	public boolean accept(AstNode node)
	{
		return node instanceof FunctionCall;
	}

	@Override
	public Hyperlink getHyperlink(ClassAccess classAccess, Buffer buffer, int line, int lineOffset)
	{
		Project project = ProjectManager.getInstance().getProject();
		if (project == null)
			return null;

		String noWordSep = buffer.getStringProperty("noWordSep");
		CharSequence lineSegment = buffer.getLineSegment(line);
		int wordStart = TextUtilities.findWordStart(lineSegment, lineOffset, noWordSep);
		int wordEnd = TextUtilities.findWordEnd(lineSegment, lineOffset, noWordSep);

		Expression prefix = classAccess.getPrefix();

		ClassHeader classHeader = null;
		if (prefix instanceof ConstantIdentifier)
		{
			classHeader = project.getClass(classAccess.getName().toLowerCase());
		}
		else if (prefix instanceof Variable)
		{
			Variable variable = (Variable) prefix;
			Type type = variable.getType();
			String className = type.getClassName();
			if (className != null)
				classHeader = project.getClass(className.toLowerCase());
		}
		if (classHeader == null)
			return null;
		int lineStartOffset = buffer.getLineStartOffset(line);
		AstNode expression = classAccess.subNodeAt(line + 1, lineOffset);
		if (expression == classAccess.getPrefix())
		{
			Hyperlink hyperlink = new PHPHyperlink(classHeader.getName(),
				classHeader.getPath(),
				classHeader.getBeginLine(), lineStartOffset +
				wordStart, lineStartOffset + wordEnd, line);
			return hyperlink;
		}
		Expression suffix = classAccess.getSuffix();
		if (suffix instanceof FunctionCall)
		{
			FunctionCall functionCall = (FunctionCall) suffix;
			Expression functionName = functionCall.getFunctionName();
			AstNode expr = functionCall.subNodeAt(line + 1, lineOffset);
			if (expr != functionName)
			{
				return defaultDecoder.getHyperlink(expr, buffer, line, lineOffset);
			}
			List<MethodHeader> methodsHeaders = classHeader.getMethodsHeaders();
			MethodHeader header = null;
			for (MethodHeader methodsHeader : methodsHeaders)
			{
				if (methodsHeader.getName().equals(functionName.toString()))
				{
					header = methodsHeader;
					break;
				}
			}
			if (header == null)
				return null;

			Hyperlink hyperlink = new PHPHyperlink(header.getName(), header.getPath(), header.getBeginLine(), lineStartOffset + wordStart, lineStartOffset + wordEnd, line);
			return hyperlink;
		}
		else if (suffix instanceof ConstantIdentifier)
		{
			List<FieldDeclaration> fields = classHeader.getFields();
			String fieldName = suffix.toString();
			for (FieldDeclaration field : fields)
			{
				if (field.getName().equals(fieldName))
				{
					Hyperlink hyperlink = new PHPHyperlink(classHeader.getName()+"."+fieldName,
									       classHeader.getPath(),
									       field.getBeginLine(),
									       lineStartOffset + wordStart,
									       lineStartOffset + wordEnd, line);
					return hyperlink;
				}
			}
		}
		return null;
	}
}
