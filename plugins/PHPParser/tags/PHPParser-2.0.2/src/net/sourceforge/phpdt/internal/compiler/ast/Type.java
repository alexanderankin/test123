/*
 * Type.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2012 Matthieu Casanova
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

//{{{ Imports
import org.gjt.sp.util.Log;

import java.io.Serializable;
//}}}

/**
 * The php types.
 *
 * @author Matthieu Casanova
 * @version $Id$
 */
public class Type implements Serializable
{
	public static final int UNKNOWN_INT = 0;
	public static final int BOOLEAN_INT = 1;
	public static final int FLOAT_INT = 4;
	public static final int INTEGER_INT = 5;
	public static final int OBJECT_INT = 6;
	public static final int NULL_INT = 7;
	public static final int STRING_INT = 8;
	public static final int ARRAY_INT = 9;
	public static final int RESOURCE_INT = 10;
	public static final int VOID_INT = 11;
	public static final int NUMBER_INT = 12;
	public static final int DOUBLE_INT = 13;

	private final int type;

	public static final Type UNKNOWN = new Type(UNKNOWN_INT);
	public static final Type BOOLEAN = new Type(BOOLEAN_INT);
	public static final Type FLOAT = new Type(FLOAT_INT);
	public static final Type INTEGER = new Type(INTEGER_INT);
	public static final Type OBJECT = new Type(OBJECT_INT);
	public static final Type NULL = new Type(NULL_INT);
	public static final Type STRING = new Type(STRING_INT);
	public static final Type ARRAY = new Type(ARRAY_INT);
	public static final Type RESOURCE = new Type(RESOURCE_INT);
	public static final Type VOID = new Type(VOID_INT);
	public static final Type NUMBER = new Type(NUMBER_INT);
	public static final Type DOUBLE = new Type(DOUBLE_INT);


	//{{{ toString() method
	public String toString()
	{
		switch (type)
		{
			case UNKNOWN_INT:
				return "unknown";
			case BOOLEAN_INT:
				return "boolean";
			case FLOAT_INT:
				return "float";
			case INTEGER_INT:
				return "integer";
			case OBJECT_INT:
				if (className == null) return "object (unknown)";
				return "object (" + className + ')';
			case NULL_INT:
				return "null";
			case STRING_INT:
				return "string";
			case ARRAY_INT:
				return "array";
			case RESOURCE_INT:
				return "resource";
			case VOID_INT:
				return "void";
			case NUMBER_INT:
				return "numbers";
			case DOUBLE_INT:
				return "double";
			default:
				Log.log(Log.ERROR, this, "net.sourceforge.phpdt.internal.compiler.ast.Type unknown : " + type);
				return null;
		}
	} //}}}

	//{{{ fromString() method
	public static Type fromString(String type)
	{
		if ("boolean".equals(type) || "bool".equals(type))
			return BOOLEAN;
		if ("float".equals(type))
			return FLOAT;
		if ("integer".equals(type) || "int".equals(type))
			return INTEGER;
		if ("null".equals(type))
			return NULL;
		if ("string".equals(type))
			return STRING;
		if ("array".equals(type))
			return ARRAY;
		if ("mixed".equals(type) || "unknown".equals(type))
			return UNKNOWN;
		if ("object".equals(type))
			return OBJECT;
		if ("resource".equals(type))
			return RESOURCE;
		if ("void".equals(type))
			return VOID;
		if ("number".equals(type))
			return NUMBER;
		return new Type(OBJECT_INT, type);
	} //}}}

	private final String className;

	//{{{ Type constructors
	public Type(int type)
	{
		this(type, null);
	}

	public Type(int type, String className)
	{
		this.type = type;
		this.className = className;
	} //}}}

	//{{{ getType() method 
	public int getType()
	{
		return type;
	} //}}}

	//{{{ getClassName() method
	public String getClassName()
	{
		return className;
	} //}}}

	//{{{ isCompatibleWith() method
	public boolean isCompatibleWith(Type type)
	{
		if (type == this)
			return true;
		if (this == UNKNOWN)
			return true;
		if (type == UNKNOWN)
			return true;
		if (this == NUMBER)
		{
			if (type == NUMBER || type == FLOAT || type == INTEGER || type == DOUBLE)
				return true;
		}
		if (this == FLOAT)
		{
			if (type == DOUBLE || type == INTEGER)
				return true;
		}
		return false;
	} //}}}

	//{{{ isEmpty() method
	/**
	 * Tell if the type is empty.
	 *
	 * @return true if the type is unknown or null
	 */
	public boolean isEmpty()
	{
		return type == UNKNOWN_INT || type == NULL_INT;
	} //}}}
}
