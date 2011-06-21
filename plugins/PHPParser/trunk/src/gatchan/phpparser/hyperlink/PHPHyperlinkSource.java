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
        Statement statement = phpDocument.getStatementAt(line + 1  /* +1 because first line in my parser is 1*/,
            lineOffset);
        if (statement == null)
            return null;
        return processStatement(buffer, line, lineOffset, statement);
    }

    private static Hyperlink processStatement(Buffer buffer, int line, int lineOffset, Statement statement)
    {
        while (true)
        {
            if (statement instanceof FunctionCall)
            {
                FunctionCall functionCall = (FunctionCall) statement;
                return processFunctionCall(functionCall, buffer, line, lineOffset);
            }
            else if (statement instanceof ClassAccess)
            {
                ClassAccess classAccess = (ClassAccess) statement;
                return processClassAccess(classAccess, buffer, line, lineOffset);
            }
            else if (statement instanceof ClassInstantiation)
            {
                ClassInstantiation classInstantiation = (ClassInstantiation) statement;
                return processClassInstantiation(classInstantiation, buffer, line, lineOffset);
            }
            Expression expression = statement.expressionAt(line + 1, lineOffset);
            if (expression == null || expression == statement)
                return null;
            statement = expression;
        }
    }

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
    }

    private static Hyperlink processFunctionCall(FunctionCall functionCall, Buffer buffer, int line, int lineOffset)
    {
        Expression functionName = functionCall.getFunctionName();
        Expression expressionAt = functionCall.expressionAt(line + 1, lineOffset);
        if (expressionAt != functionName)
        {
            if (expressionAt == null)
                return null;
            // the cursor is not on the function name
            return processStatement(buffer, line, lineOffset, expressionAt);
        }
        String name = functionName.toString();
        Project project = ProjectManager.getInstance().getProject();
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

    private static Hyperlink processClassAccess(ClassAccess classAccess, Buffer buffer, int line, int lineOffset)
    {
        String noWordSep = buffer.getStringProperty("noWordSep");
        CharSequence lineSegment = buffer.getLineSegment(line);
        int wordStart = TextUtilities.findWordStart(lineSegment, lineOffset, noWordSep);
        int wordEnd = TextUtilities.findWordEnd(lineSegment, lineOffset, noWordSep);

        Expression prefix = classAccess.getPrefix();
        ClassHeader classHeader = null;
        if (prefix instanceof ConstantIdentifier)
        {
            Project project = ProjectManager.getInstance().getProject();
            classHeader = project.getClass(classAccess.getName());
        }
        else if (prefix instanceof Variable)
        {
            Variable variable = (Variable) prefix;
            Type type = variable.getType();
            String className = type.getClassName();
            if (className != null)
            {
                Project project = ProjectManager.getInstance().getProject();
                classHeader = project.getClass(className);
            }
        }
        if (classHeader == null)
            return null;
         int lineStartOffset = buffer.getLineStartOffset(line);
        Expression expression = classAccess.expressionAt(line + 1, lineOffset);
        if (expression == classAccess.getPrefix())
        {
            Hyperlink hyperlink = new PHPHyperlink(classHeader.getName(), classHeader.getPath(), classHeader.getBeginLine(), lineStartOffset + wordStart, lineStartOffset + wordEnd, line);
            return hyperlink;
        }
        Expression suffix = classAccess.getSuffix();
        if (suffix instanceof FunctionCall)
        {
            FunctionCall functionCall = (FunctionCall) suffix;
            Expression functionName = functionCall.getFunctionName();
            Expression expr = functionCall.expressionAt(line + 1, lineOffset);
            if (expr != functionName)
            {
                return processStatement(buffer, line, lineOffset, expr);
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
        System.out.println(suffix);
        return null;
    }
}
