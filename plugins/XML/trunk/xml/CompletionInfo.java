/*
 * CompletionInfo.java
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

import java.util.Hashtable;
import java.util.Vector;

class CompletionInfo
{
	// if true, HTML syntax is supported (eg, case-insensitive tag names,
	// attributes with no values)
	boolean html;

	Vector elements;
	Hashtable elementHash;
	Vector entities;
	Hashtable entityHash;
	Vector ids;

	CompletionInfo(boolean html, Vector elements, Hashtable elementHash,
		Vector entities, Hashtable entityHash, Vector ids)
	{
		this.html = html;
		this.elements = elements;
		this.elementHash = elementHash;
		this.entities = entities;
		this.entityHash = entityHash;
		this.ids = ids;
	}
}
