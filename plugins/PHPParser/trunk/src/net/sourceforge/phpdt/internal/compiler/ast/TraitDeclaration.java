/*
 * jEdit - Programmer's Text Editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright © 2017 Matthieu Casanova
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

import gatchan.phpparser.parser.PHPParseErrorEvent;
import gatchan.phpparser.parser.PHPParser;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import sidekick.IAsset;

import javax.swing.*;
import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * This class is my TraitDeclaration declaration for php. It is similar to org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 * It directly extends AstNode because a class cannot appear anywhere in php
 *
 * @author Matthieu Casanova
 * @version $Id: ClassDeclaration.java 20426 2011-11-26 19:25:03Z kpouer $
 */
public class TraitDeclaration extends Statement implements Outlineable, IAsset
{
	private final TraitHeader traitHeader;

	private int bodyLineStart;
	private int bodyColumnStart;
	private int bodyLineEnd;
	private int bodyColumnEnd;

	private final Collection<MethodDeclaration> methods = new ArrayList<>();
	private final transient Outlineable parent;
	/**
	 * The outlineable children (those will be in the node array too.
	 */
	private final List<Outlineable> children = new ArrayList<>();

	private transient Position start;
	private transient Position end;

	/**
	 * Create a class giving starting and ending offset.
	 *
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 */
	public TraitDeclaration(Outlineable parent,
                          TraitHeader traitHeader,
                          int sourceStart,
                          int sourceEnd,
                          int beginLine,
                          int endLine,
                          int beginColumn,
                          int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.parent = parent;
		this.traitHeader = traitHeader;
	}

	/**
	 * Add a method to the class.
	 *
	 * @param method the method declaration
	 */
	public void addMethod(MethodDeclaration method)
	{
		String name = traitHeader.getName();
		Type type = new Type(Type.OBJECT_INT, name);
		method.visitSubNodes(new NodeVisitor()
		{
			@Override
			public void visit(AstNode node)
			{
				if (node instanceof Variable)
				{
					Variable variable = (Variable) node;
					if ("this".equals(variable.getName()))
					{
						variable.setType(type);
					}
				}
				if (node != null)
					node.visitSubNodes(this);
			}
		});
		traitHeader.addMethod(method.getMethodHeader());
		methods.add(method);
		add(method);
	}

	public boolean add(Outlineable o)
	{
		return children.add(o);
	}

	/**
	 * Return the class as String.
	 *
	 * @param tab how many tabs before the class
	 * @return the code of this class into String
	 */
	@Override
	public String toString(int tab)
	{
		return traitHeader.toString(tab) + toStringBody(tab);
	}

	public String toString()
	{
		return traitHeader.toString();
	}

	/**
	 * Return the body of the class as String.
	 *
	 * @param tab how many tabs before the body of the class
	 * @return the body as String
	 */
	private String toStringBody(int tab)
	{
		StringBuilder buff = new StringBuilder(" {");
		for (MethodDeclaration o : methods)
		{
			buff.append('\n');
			buff.append(o.toString(tab + 1));
		}
		buff.append('\n').append(tabString(tab)).append('}');
		return buff.toString();
	}

	@Override
  public Outlineable getParent()
	{
		return parent;
	}

	@Override
  public Outlineable get(int index)
	{
		return children.get(index);
	}

	@Override
  public int size()
	{
		return children.size();
	}

	/**
	 * Get the variables from outside (parameters, globals ...)
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getOutsideVariable(List<VariableUsage> list)
	{
	  //do nothing
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
    //do nothing
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
    //do nothing
	}

	public String getName()
	{
		return traitHeader.getName();
	}

	public int getItemType()
	{
		return traitHeader.getItemType();
	}

	public MethodDeclaration insideWichMethodIsThisOffset(int line, int column)
	{
		for (MethodDeclaration methodDeclaration : methods)
		{
			if (line == methodDeclaration.getBodyLineStart() && column > methodDeclaration.getBodyColumnStart())
				return methodDeclaration;
			if (line == methodDeclaration.getBodyLineEnd() && column < methodDeclaration.getBodyColumnEnd())
				return methodDeclaration;
			if (line > methodDeclaration.getBodyLineStart() && line < methodDeclaration.getBodyLineEnd())
				return methodDeclaration;
		}
		return null;
	}

	public TraitHeader getTraitHeader()
	{
		return traitHeader;
	}

	public int getBodyLineStart()
	{
		return bodyLineStart;
	}

	public void setBodyLineStart(int bodyLineStart)
	{
		this.bodyLineStart = bodyLineStart;
	}

	public int getBodyColumnStart()
	{
		return bodyColumnStart;
	}

	public void setBodyColumnStart(int bodyColumnStart)
	{
		this.bodyColumnStart = bodyColumnStart;
	}

	public int getBodyLineEnd()
	{
		return bodyLineEnd;
	}

	public void setBodyLineEnd(int bodyLineEnd)
	{
		this.bodyLineEnd = bodyLineEnd;
		setEndLine(bodyLineEnd);
	}

	public int getBodyColumnEnd()
	{
		return bodyColumnEnd;
	}

	public void setBodyColumnEnd(int bodyColumnEnd)
	{
		this.bodyColumnEnd = bodyColumnEnd;
		setEndColumn(bodyColumnEnd);
	}

	@Override
  public Icon getIcon()
	{
	  return traitHeader.getIcon();
	}

	@Override
  public String getShortString()
	{
		return toString();
	}

	@Override
  public String getLongString()
	{
		return toString();
	}

	@Override
  public Position getStart()
	{
		return start;
	}

	@Override
  public Position getEnd()
	{
		return end;
	}

	@Override
  public void setStart(Position start)
	{
		this.start = start;
	}

	@Override
  public void setEnd(Position end)
	{
		this.end = end;
	}

	@Override
  public void setName(String name)
	{
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (traitHeader.isAt(line, column))
			return traitHeader;
		for (MethodDeclaration methodDeclaration : methods)
		{
			if (methodDeclaration.isAt(line, column))
				return methodDeclaration;
		}
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		Collection<String> methodsNames = new HashSet<String>(methods.size());
		for (MethodDeclaration methodDeclaration : methods)
		{
			methodDeclaration.analyzeCode(parser);
			String name = methodDeclaration.getName();
			checkMethod(methodsNames, name, methodDeclaration.getMethodHeader(), parser,
				"this method name is already used by another field or method in the class : ");
		}
	}

	private static void checkMethod(Collection<String> itemNames, String name, AstNode node, PHPParser parser, String msg)
	{
		if (!itemNames.add(name))
		{
			// the method name already exists in this class this is an error
			parser.fireParseError(new PHPParseErrorEvent(PHPParser.ERROR,
				parser.getPath(),
				msg + name,
				node.getSourceStart(),
				node.getSourceEnd(),
				node.getBeginLine(),
				node.getEndLine(),
				node.getBeginColumn(),
				node.getEndColumn()));
		}
	}

	@Override
	public void visitSubNodes(NodeVisitor visitor)
	{
		for (MethodDeclaration method : methods)
		{
			visitor.visit(method);
			method.visitSubNodes(visitor);
		}
	}
}
