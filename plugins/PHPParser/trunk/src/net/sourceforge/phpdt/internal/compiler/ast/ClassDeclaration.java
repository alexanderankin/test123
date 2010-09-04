/*
 * ClassDeclaration.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003-2010 Matthieu Casanova
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

import java.util.*;

import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;
import net.sourceforge.phpdt.internal.compiler.parser.Outlineable;
import gatchan.phpparser.project.itemfinder.PHPItem;
import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.PHPParseErrorEvent;
import gatchan.phpparser.parser.PHPParseMessageEvent;
import sidekick.IAsset;

import javax.swing.text.Position;
import javax.swing.*;

import org.gjt.sp.jedit.GUIUtilities;


/**
 * This class is my ClassDeclaration declaration for php. It is similar to org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 * It directly extends AstNode because a class cannot appear anywhere in php
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class ClassDeclaration extends Statement implements Outlineable, IAsset
{

	private final ClassHeader classHeader;

	private int bodyLineStart;
	private int bodyColumnStart;
	private int bodyLineEnd;
	private int bodyColumnEnd;

	/**
	 * The constructor of the class.
	 */
	private MethodDeclaration constructor;

	private final Collection<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	private final transient Outlineable parent;
	/**
	 * The outlineable children (those will be in the node array too.
	 */
	private final List<Outlineable> children = new ArrayList<Outlineable>();

	private transient Position start;
	private transient Position end;
	private static Icon icon;

	/**
	 * Create a class giving starting and ending offset.
	 *
	 * @param sourceStart starting offset
	 * @param sourceEnd   ending offset
	 */
	public ClassDeclaration(Outlineable parent,
				ClassHeader classHeader,
				int sourceStart,
				int sourceEnd,
				int beginLine,
				int endLine,
				int beginColumn,
				int endColumn)
	{
		super(sourceStart, sourceEnd, beginLine, endLine, beginColumn, endColumn);
		this.parent = parent;
		this.classHeader = classHeader;
	}

	/**
	 * Add a method to the class.
	 *
	 * @param method the method declaration
	 */
	public void addMethod(MethodDeclaration method)
	{
		classHeader.addMethod(method.getMethodHeader());
		methods.add(method);
		add(method);
		if (method.getName().equals(classHeader.getName()))
		{
			constructor = method;
		}
	}

	/**
	 * Add a method to the class.
	 *
	 * @param field the method declaration
	 */
	public void addField(FieldDeclaration field)
	{
		VariableDeclaration c = field.getVariable();
		children.add(c);
		classHeader.addField(field);
	}

	/**
	 * Add a new constant to the class.
	 *
	 * @param constant the constant
	 */
	public void addConstant(ClassConstant constant)
	{
		children.add(constant);
		classHeader.addConstant(constant);
	}

	public boolean add(Outlineable o)
	{
		return children.add(o);
	}

	/**
	 * Tell if the class has a constructor.
	 *
	 * @return a boolean
	 */
	public boolean hasConstructor()
	{
		return constructor != null;
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
		return classHeader.toString(tab) + toStringBody(tab);
	}

	public String toString()
	{
		return classHeader.toString();
	}

	/**
	 * Return the body of the class as String.
	 *
	 * @param tab how many tabs before the body of the class
	 * @return the body as String
	 */
	private String toStringBody(int tab)
	{
		StringBuilder buff = new StringBuilder(" {");//$NON-NLS-1$
		List<FieldDeclaration> fields = classHeader.getFields();
		if (fields != null)
		{
			for (FieldDeclaration field : fields)
			{
				buff.append('\n'); //$NON-NLS-1$
				buff.append(field.toString(tab + 1));
				buff.append(';');//$NON-NLS-1$
			}
		}
		for (MethodDeclaration o : methods)
		{
			buff.append('\n');//$NON-NLS-1$
			buff.append(o.toString(tab + 1));
		}
		buff.append('\n').append(tabString(tab)).append('}'); //$NON-NLS-2$ //$NON-NLS-1$
		return buff.toString();
	}

	public Outlineable getParent()
	{
		return parent;
	}

	public Outlineable get(int index)
	{
		return children.get(index);
	}

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
	}

	/**
	 * get the modified variables.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getModifiedVariable(List<VariableUsage> list)
	{
	}

	/**
	 * Get the variables used.
	 *
	 * @param list the list where we will put variables
	 */
	@Override
	public void getUsedVariable(List<VariableUsage> list)
	{
	}

	public String getName()
	{
		return classHeader.getName();
	}

	public int getItemType()
	{
		return PHPItem.CLASS;
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

	public ClassHeader getClassHeader()
	{
		return classHeader;
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

	public Icon getIcon()
	{
		if (icon == null)
		{
			//icon = new ImageIcon(ClassAsset.class.getResource("/gatchan/phpparser/icons/class.png"));
			icon = GUIUtilities.loadIcon(ClassDeclaration.class.getResource("/gatchan/phpparser/icons/class.png").toString());
		}
		return icon;
	}

	public String getShortString()
	{
		return toString();
	}

	public String getLongString()
	{
		return toString();
	}

	public Position getStart()
	{
		return start;
	}

	public Position getEnd()
	{
		return end;
	}

	public void setStart(Position start)
	{
		this.start = start;
	}

	public void setEnd(Position end)
	{
		this.end = end;
	}

	public void setName(String name)
	{
	}

	@Override
	public Expression expressionAt(int line, int column)
	{
		for (MethodDeclaration methodDeclaration : methods)
		{
			if (methodDeclaration.isAt(line, column))
				return methodDeclaration.expressionAt(line, column);
		}
		List<FieldDeclaration> fields = classHeader.getFields();
		for (FieldDeclaration field : fields)
		{
			if (field.isAt(line, column))
				return field.expressionAt(line, column);
		}
		return null;
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		List<FieldDeclaration> fields = classHeader.getFields();
		Collection<String> methodsNames = new HashSet<String>(methods.size());
		Collection<String> fieldNames = new HashSet<String>(fields.size());
		for (MethodDeclaration methodDeclaration : methods)
		{
			methodDeclaration.analyzeCode(parser);
			String name = methodDeclaration.getName();
			checkMethod(methodsNames, name, methodDeclaration.getMethodHeader(), parser,
				"this method name is already used by another field or method in the class : ");
		}

		for (FieldDeclaration fieldDeclaration : fields)
		{
			String name = fieldDeclaration.getName();
			checkField(methodsNames, fieldNames, name, fieldDeclaration, parser);
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

	private static void checkField(Collection<String> methodNames, Collection<String> fieldNames, String name, AstNode node, PHPParser parser)
	{
		if (!fieldNames.add(name))
		{
			// the method name already exists in this class this is an error
			parser.fireParseError(new PHPParseErrorEvent(PHPParser.ERROR,
				parser.getPath(),
				"this field name is already used by another field or method in the class : " + name,
				node.getSourceStart(),
				node.getSourceEnd(),
				node.getBeginLine(),
				node.getEndLine(),
				node.getBeginColumn(),
				node.getEndColumn()));
		}
		else if (methodNames.contains(name))
		{
			parser.fireParseMessage(new PHPParseMessageEvent(PHPParser.WARNING,
				PHPParseMessageEvent.MESSAGE_METHOD_FIELD_WITH_SAME_NAME,
				parser.getPath(),
				"a method is defined with the same name " + name,
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
