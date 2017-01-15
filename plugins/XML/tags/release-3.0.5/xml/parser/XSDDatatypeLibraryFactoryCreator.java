/*
 * XSDDatatypeLibraryFactoryCreator.java - jing's XSD datatypes
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2016 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

import org.relaxng.datatype.DatatypeLibraryFactory;

import com.thaiopensource.datatype.xsd.DatatypeLibraryFactoryImpl;
import com.thaiopensource.datatype.xsd.regex.java.RegexEngineImpl;

public class XSDDatatypeLibraryFactoryCreator {

	public static DatatypeLibraryFactory createXSDFactory() {
		return new DatatypeLibraryFactoryImpl(new RegexEngineImpl());
	}
}