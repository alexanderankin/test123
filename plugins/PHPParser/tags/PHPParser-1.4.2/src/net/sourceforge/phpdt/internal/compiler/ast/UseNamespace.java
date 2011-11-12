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
package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.List;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import org.gjt.sp.util.StringList;

/**
 * @author Matthieu Casanova
 */
public class UseNamespace extends Statement
{
	private List<ConstantIdentifier> namespaces;

	public UseNamespace(List<ConstantIdentifier> namespaces, int sourceStart, int sourceEnd, int beginLine, int endLine, int beginColumn,
			    int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.namespaces = namespaces;
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		return null;
	}

	@Override
	public String toString()
	{
		StringList sl = new StringList();
		for (ConstantIdentifier namespace : namespaces)
		{
			sl.add(namespace.toString());
		}
		return "use " + sl.join(", ") + ';';
	}

	@Override
	public String toString(int tab)
	{
		return tabString(tab) + toString();
	}

	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	}

	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	}

	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
	}
}
