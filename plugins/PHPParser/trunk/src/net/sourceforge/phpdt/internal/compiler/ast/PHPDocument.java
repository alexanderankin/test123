/*
 * PHPDocument.java
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.phpdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import net.sourceforge.phpdt.internal.compiler.parser.OutlineableWithChildren;
import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParser;
import sidekick.IAsset;

import javax.swing.text.Position;
import javax.swing.*;

/**
 * It's a php document. This class is an outlineable object It will contains html and php
 *
 * @author Matthieu Casanova
 */
public class PHPDocument implements OutlineableWithChildren, IAsset
{
	/**
	 * The nodes. It will include html nodes or php nodes
	 */
	private AstNode[] nodes;

	private final String name;

	/**
	 * The outlineable children (those will be in the node array too.
	 */
	private final List<Outlineable> children = new ArrayList<Outlineable>();

	private transient Position start;
	private transient Position end;

	/**
	 * Create the PHPDocument.
	 *
	 * @param name the name f the document
	 */
	public PHPDocument(String name)
	{
		this.name = name;
	}

	/**
	 * Return the php document as String.
	 *
	 * @return a string representation of the object.
	 */
	public String toString()
	{
		StringBuilder buff = new StringBuilder();
		if (nodes != null)
		{
			for (int i = 0; i < nodes.length; i++)
			{
				AstNode node = nodes[i];
				if (node == null)
				{
					break;
				}
				buff.append(node.toString(0));
				if (node instanceof HTMLCode)
				{
					buff.append('\n');
				}
				else
				{
					buff.append(";\n");
				}
			}
		}
		return buff.toString();
	}

	/**
	 * Add an outlineable object.
	 *
	 * @param o the new outlineable
	 * @return does the addition worked ?
	 */
	public boolean add(Outlineable o)
	{
		return children.add(o);
	}

	/**
	 * Return the outlineable at the index.
	 *
	 * @param index the index
	 * @return an outlineable object
	 */
	public Outlineable get(int index)
	{
		return children.get(index);
	}

	/**
	 * The number of outlineable children.
	 *
	 * @return the number of children that are outlineable
	 */
	public int size()
	{
		return children.size();
	}


	/**
	 * Get the parent of the object.
	 *
	 * @return null
	 */
	public OutlineableWithChildren getParent()
	{
		return null;
	}

	/**
	 * Analyze the code of a php document.
	 *
	 * @param phpParser the php parser
	 */
	public void analyzeCode(PHPParser phpParser)
	{
		if (nodes != null)
		{
			for (int i = 0; i < nodes.length; i++)
			{
				AstNode node = nodes[i];
				if (node == null) break;
				node.analyzeCode(phpParser);
			}
		}
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Give the method at the line and column given. It will returns null if no method can be found at the offset.
	 *
	 * @param line   the line
	 * @param column the offset
	 * @return the method at the offset or null
	 */
	public ClassDeclaration classAtOffset(int line, int column)
	{
		for (int i = 0; i < children.size(); i++)
		{
			Outlineable outlineable = children.get(i);
			if (outlineable.getItemType() == PHPItem.CLASS)
			{
				ClassDeclaration classDeclaration = (ClassDeclaration) outlineable;
				if (line == classDeclaration.getBodyLineStart() && column > classDeclaration.getBodyColumnStart())
					return classDeclaration;
				if (line == classDeclaration.getBodyLineEnd() && column < classDeclaration.getBodyColumnEnd())
					return classDeclaration;
				if (line > classDeclaration.getBodyLineStart() && line < classDeclaration.getBodyLineEnd())
					return classDeclaration;
			}
		}
		return null;
	}

	/**
	 * Give the method at the line and column given. It will returns null if no method can be found at the offset.
	 *
	 * @param line   the line
	 * @param column the offset
	 * @return the method at the offset or null
	 */
	public MethodDeclaration methodAtOffset(int line, int column)
	{
		for (int i = 0; i < children.size(); i++)
		{
			Outlineable outlineable = children.get(i);
			if (outlineable.getItemType() == PHPItem.METHOD)
			{
				MethodDeclaration methodDeclaration = (MethodDeclaration) outlineable;
				if (line == methodDeclaration.getBodyLineStart() && column > methodDeclaration.getBodyColumnStart())
					return methodDeclaration;
				if (line == methodDeclaration.getBodyLineEnd() && column < methodDeclaration.getBodyColumnEnd())
					return methodDeclaration;
				if (line > methodDeclaration.getBodyLineStart() && line < methodDeclaration.getBodyLineEnd())
					return methodDeclaration;
			}
		}
		return null;
	}

	/**
	 * Returns the statement at the given position.
	 *
	 * @param line   the line
	 * @param column the column
	 * @return the statement at the position
	 */
	public Statement getStatementAt(int line, int column)
	{
		Statement statement = null;
		for (int i = 0; i < nodes.length; i++)
		{
			statement = (Statement) nodes[i];
			if (statement == null) break;

			if (line == statement.getBeginLine() && column > statement.getBeginColumn()) return statement;
			if (line == statement.getEndLine() && column < statement.getEndColumn()) return statement;
			if (line > statement.getBeginLine() && line < statement.getEndLine()) return statement;
		}
		return statement;
	}

	/**
	 * Set the nodes of the document.
	 *
	 * @param nodes the nodes
	 */
	public void setNodes(AstNode[] nodes)
	{
		this.nodes = nodes;
	}

	public int getItemType()
	{
		return PHPItem.DOCUMENT;
	}

	public Position getEnd()
	{
		return end;
	}

	public void setEnd(Position end)
	{
		this.end = end;
	}

	public Position getStart()
	{
		return start;
	}

	public void setStart(Position start)
	{
		this.start = start;
	}

	public Icon getIcon()
	{
		return null;
	}

	public String getShortString()
	{
		return "/";
	}

	public String getLongString()
	{
		return "/";
	}

	public void setName(String name)
	{
	}

}