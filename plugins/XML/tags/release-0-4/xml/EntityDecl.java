/*
 * EntityDecl.java
 * Copyright (C) 2001 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

class EntityDecl
{
	static final int INTERNAL = 0;
	static final int EXTERNAL = 1;

	int type;

	String name;
	String value;
	String publicId;
	String systemId;

	EntityDecl(int type, String name, String value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
	}

	EntityDecl(int type, String name, String publicId, String systemId)
	{
		this.type = type;
		this.name = name;
		this.publicId = publicId;
		this.systemId = systemId;
	}

	public String toString()
	{
		if(type == INTERNAL)
			return getClass().getName() + "[" + name + "," + value + "]";
		else if(type == EXTERNAL)
			return getClass().getName() + "[" + name
				+ "," + publicId + "," + systemId + "]";
		else
			return null;
	}
}
