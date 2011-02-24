/*
 * PHPParserTextAreaExtension.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2010 Matthieu Casanova
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
package gatchan.phpparser;

import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import net.sourceforge.phpdt.internal.compiler.ast.PHPDocument;
import net.sourceforge.phpdt.internal.compiler.ast.Statement;
import net.sourceforge.phpdt.internal.compiler.ast.Expression;
import gatchan.phpparser.sidekick.PHPSideKickParser;

/**
 * @author Matthieu Casanova
 */
public class PHPParserTextAreaExtension extends TextAreaExtension
{
	private final JEditTextArea textArea;

	public PHPParserTextAreaExtension(JEditTextArea textArea)
	{
		this.textArea = textArea;
	}

	public String getToolTipText(int x, int y)
	{
		PHPDocument phpDocument = (PHPDocument) textArea.getBuffer().getProperty(PHPSideKickParser.PHPDOCUMENT_PROPERTY);
		if (phpDocument == null)
		{
			return null;
		}
		int offset = textArea.xyToOffset(x, y);
		if (offset == -1)
		{
			return null;
		}
		int line = textArea.getLineOfOffset(offset);
		int column = offset - textArea.getLineStartOffset(line);
		Statement statement = phpDocument.getStatementAt(line + 1, column);
		if (statement != null)
		{
			Expression expression = statement.expressionAt(line + 1, column);
			if (expression != null)
			{
				return expression.getType().toString();
			}
		}
		return null;
	}
}
