/*
* AstNode.java
* :tabSize=8:indentSize=8:noTabs=false:
* :folding=explicit:collapseFolds=1:
*
* Copyright (C) 2003, 2010 Matthieu Casanova
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;
import java.io.Serializable;

/**
 * It will be the mother of our own ast tree for php just like the ast tree of Eclipse.
 *
 * @author Matthieu Casanova
 */
public abstract class AstNode implements Serializable
{
	/**
	 * Starting and ending position of the node in the sources.
	 */
	protected int sourceStart;
	protected int sourceEnd;

	protected int beginLine;
	protected int endLine;
	protected int beginColumn;
	protected int endColumn;

	//{{{ AstNode constructors

	protected AstNode()
	{
	}

	/**
	 * Create a node.
	 *
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 * @param beginLine   begin line
	 * @param endLine     ending line
	 * @param beginColumn begin column
	 * @param endColumn   ending column
	 */
	protected AstNode(int sourceStart,
			  int sourceEnd,
			  int beginLine,
			  int endLine,
			  int beginColumn,
			  int endColumn)
	{
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.beginLine = beginLine;
		this.endLine = endLine;
		this.beginColumn = beginColumn;
		this.endColumn = endColumn;
	} //}}}

	//{{{ tabString() method

	/**
	 * Add some tabulations.
	 *
	 * @param tab the number of tabulations
	 * @return a String containing some spaces
	 */
	public static String tabString(int tab)
	{
		if (tab == 0)
			return "";
		StringBuilder s = new StringBuilder(2 * tab);
		for (int i = tab; i > 0; i--)
		{
			s.append("  ");
		}
		return s.toString();
	} //}}}

	//{{{ toString() method

	/**
	 * Return the object into String. It should be overriden
	 *
	 * @return a String
	 */
	public String toString()
	{
		return "****" + super.toString() + "****";
	} //}}}

	/**
	 * Return the object into String.
	 *
	 * @param tab how many tabs (not used here
	 * @return a String
	 */
	public abstract String toString(int tab);

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	public abstract void getOutsideVariable(List<VariableUsage> list);

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	public abstract void getModifiedVariable(List<VariableUsage> list);

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	public abstract void getUsedVariable(List<VariableUsage> list);

	/**
	 * This method will analyze the code. by default it will do nothing
	 */
	public abstract void analyzeCode(PHPParser parser);

	//{{{ arrayContains() method
	/**
	 * Check if the array array contains the object o.
	 *
	 * @param array an array
	 * @param o     an obejct
	 * @return true if the array contained the object o
	 */
	public static boolean arrayContains(Object[] array, Object o)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i].equals(o))
			{
				return true;
			}
		}
		return false;
	} //}}}

	//{{{ getSourceStart() method
	public int getSourceStart()
	{
		return sourceStart;
	} //}}}

	//{{{ getSourceEnd() method
	public int getSourceEnd()
	{
		return sourceEnd;
	} //}}}

	//{{{ getBeginLine() method
	public int getBeginLine()
	{
		return beginLine;
	} //}}}

	//{{{ getEndLine() method
	public int getEndLine()
	{
		return endLine;
	} //}}}

	//{{{ getBeginColumn() method
	public int getBeginColumn()
	{
		return beginColumn;
	} //}}}

	//{{{ getEndColumn() method
	public int getEndColumn()
	{
		return endColumn;
	} //}}}

	//{{{ setSourceEnd() method
	public void setSourceEnd(int sourceEnd)
	{
		this.sourceEnd = sourceEnd;
	} //}}}

	//{{{ setEndLine() method
	public void setEndLine(int endLine)
	{
		this.endLine = endLine;
	} //}}}

	//{{{ setEndColumn() method
	public void setEndColumn(int endColumn)
	{
		this.endColumn = endColumn;
	} //}}}

	//{{{  isAt() methods
	/**
	 * Returns true if the line and column position are contained by this node.
	 *
	 * @param line   the line
	 * @param column the column
	 * @return true if the line and column position are contained by this node.
	 */
	public boolean isAt(int line, int column)
	{
		return isAt(this, line, column);
	}

	/**
	 * Returns true if the line and column position are contained by the given node.
	 *
	 * @param node   the node
	 * @param line   the line
	 * @param column the column
	 * @return true if the line and column position are contained by the given node.
	 */
	public static boolean isAt(AstNode node, int line, int column)
	{
		return (line == node.getBeginLine() && column > node.getBeginColumn()) ||
			(line == node.getEndLine() && column < node.getEndColumn()) ||
			(line > node.getBeginLine() && line < node.getEndLine());
	} //}}}

	public void visitSubNodes(NodeVisitor visitor)
	{
	}
}
