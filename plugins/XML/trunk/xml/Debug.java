/*
 * Debug.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2009 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml;


/**
 * some debug flags for the XML plugin
 * @version $Id$
 */
public final class Debug
{
	
	/** turn on/off messages about resource resolution */
	public static final boolean DEBUG_RESOLVER = true;

	/** turn on/off messages about XSD schema reading (in XercesParserImpl) */
	public static final boolean DEBUG_XSD_SCHEMA = false;
	
	/** turn on/off messages about RNG schemas to CompletionInfo */
	public static final boolean DEBUG_RNG_SCHEMA = true;
	
	/** turn on/off messages about DTD to CompletionInfo (in XercesParserImpl) */
	public static final boolean DEBUG_DTD = false;
	
	/** turn on/off messages about Schema discovery */
	public static final boolean DEBUG_SCHEMA_MAPPING = true;
	
}

