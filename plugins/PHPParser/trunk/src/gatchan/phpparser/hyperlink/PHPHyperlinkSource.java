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
import gatchan.phpparser.project.Project;
import gatchan.phpparser.project.ProjectManager;
import gatchan.phpparser.sidekick.PHPSideKickParser;
import net.sourceforge.phpdt.internal.compiler.ast.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.TextUtilities;

import java.util.List;

/**
 * @author Matthieu Casanova
 */
public class PHPHyperlinkSource implements HyperlinkSource
{
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
		return processNode(buffer, line, lineOffset, statement);
	} //}}}

	
	//{{{ processNode() method
	private static Hyperlink processNode(Buffer buffer, int line, int lineOffset, AstNode node)
	{
		while (true)
		{
			if (node instanceof FunctionCall)
			{
				FunctionCall functionCall = (FunctionCall) node;
				return processFunctionCall(functionCall, buffer, line, lineOffset);
			}
			else if (node instanceof ClassAccess)
			{
				ClassAccess classAccess = (ClassAccess) node;
				return processClassAccess(classAccess, buffer, line, lineOffset);
			}
			else if (node instanceof ClassInstantiation)
			{
				ClassInstantiation classInstantiation = (ClassInstantiation) node;
				return processClassInstantiation(classInstantiation, buffer, line, lineOffset);
			}
			else if (node instanceof ClassHeader)
			{
				ClassHeader classHeader = (ClassHeader) node;
				return processClassHeader(classHeader, buffer, line, lineOffset);
			}
			AstNode newNode = node.subNodeAt(line + 1, lineOffset);
			if (newNode == null || newNode == node)
				return null;
			node = newNode;
		}
	} //}}}

	private static Hyperlink processClassHeader(ClassHeader classDeclaration, Buffer buffer, int line,
						    int lineOffset)
	{
		return null;
	}

	//{{{ processClassInstantiation() method
	private static Hyperlink processClassInstantiation(ClassInstantiation classInstantiation, Buffer buffer, int line, int lineOffset)
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
	} //}}}

	//{{{ processFunctionCall() method
	private static Hyperlink processFunctionCall(FunctionCall functionCall, Buffer buffer, int line, int lineOffset)
	{
		Project project = ProjectManager.getInstance().getProject();
		if (project == null)
			return null;
		Expression functionName = functionCall.getFunctionName();
		AstNode expressionAt = functionCall.subNodeAt(line + 1, lineOffset);
		if (expressionAt != functionName)
		{
			if (expressionAt == null)
				return null;
			// the cursor is not on the function name
			return processNode(buffer, line, lineOffset, expressionAt);
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
	} //}}}
	
	//{{{ processClassAccess() method
	private static Hyperlink processClassAccess(ClassAccess classAccess, Buffer buffer, int line, int lineOffset)
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
				return processNode(buffer, line, lineOffset, expr);
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
	} //}}}

}
