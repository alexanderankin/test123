/*
 * ClassAccess.java
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
package net.sourceforge.phpdt.internal.compiler.ast;

import gatchan.phpparser.parser.PHPParser;
import gatchan.phpparser.parser.PHPParserConstants;
import net.sourceforge.phpdt.internal.compiler.ast.declarations.VariableUsage;

import java.util.List;

/**
 * Any class access.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class ClassAccess extends AbstractVariable
{

	private final Expression prefix;

	/**
	 * the suffix.
	 */
	private final Expression suffix;

	/**
	 * the accessType of access.
	 */
	private final int accessType;
	private static final long serialVersionUID = -7498661310867405981L;


	/**
	 * Instantiate a class access.
	 *
	 * @param prefix    usualy the class name
	 * @param suffix    the field or method called (it can be null in case of parse error)
	 * @param type      the accessType of access
	 * @param sourceEnd the end offset
	 * @param endLine   the end line
	 * @param endColumn the end column
	 */
	public ClassAccess(Expression prefix,
			   Expression suffix,
			   int type,
			   int sourceEnd,
			   int endLine,
			   int endColumn)
	{
		super(Type.UNKNOWN, prefix.getSourceStart(), sourceEnd, prefix.getBeginLine(), endLine, prefix.getBeginColumn(), endColumn);
		this.prefix = prefix;
		this.suffix = suffix;
		accessType = type;
	}

	private String toStringOperator()
	{
		return PHPParserConstants.tokenImage[accessType];
	}

	/**
	 * Return the expression as String.
	 *
	 * @return the expression
	 */
	@Override
	public String toStringExpression()
	{
		String prefixString = prefix.toStringExpression();
		String operatorString = toStringOperator();
		StringBuilder buff = new StringBuilder(prefixString.length() + operatorString.length() + 100);
		buff.append(prefixString);
		buff.append(operatorString);
		if (suffix != null)
		{
			String suffixString = suffix.toStringExpression();
			buff.append(suffixString);
		}
		return buff.toString();
	}

	/**
	 * Returns the name of the class. todo: find a better way to handle this
	 *
	 * @return the name of the variable
	 */
	@Override
	public String getName()
	{
		if (prefix instanceof AbstractVariable)
		{
			return ((AbstractVariable) prefix).getName();
		}
		return prefix.toStringExpression();
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
		prefix.getUsedVariable(list);
		if (suffix != null)
		{
			suffix.getUsedVariable(list);
		}
	}

	@Override
	public AstNode subNodeAt(int line, int column)
	{
		if (prefix.isAt(line, column)) return prefix;
		if (suffix != null && suffix.isAt(line, column)) return suffix;
		return null;
	}

	@Override
	public void visitSubNodes(NodeVisitor visitor)
	{
		visitor.visit(prefix);
		visitor.visit(suffix);
	}

	@Override
	public void analyzeCode(PHPParser parser)
	{
		prefix.analyzeCode(parser);
		if (suffix != null) suffix.analyzeCode(parser);
	}

	public Expression getPrefix()
	{
		return prefix;
	}

    public Expression getSuffix()
    {
        return suffix;
    }
}
