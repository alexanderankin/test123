/*
 * XPathAdapter.java - all the operations required for the XPath tool
 *
 * Copyright (c) 2010 Eric Le Lay
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
package xslt;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.util.Map;

/**
 * interface embodying all the services required for the XPath Tool
 */
public interface XPathAdapter {
	
	/**
	 * evaluate expression against doc, given prefixes
	 * @param	doc	source document
	 * @param	prefixes	prefix->namespace bindings
	 * @param	expression	xpath expression to evaluate
	 * @return	result of the evaluation
	 */
	public Result evaluateExpression(Document doc, Map<String,String> prefixes, String expression) throws Exception;
	
	/**
	 * result of an evaluation.
	 * Provides methods to get type, size and contents of the result.
	 */
	public static interface Result{
		/** @return a String description */
		public String getType() throws Exception;
		/** @return is this Result a node-set */
		public boolean isNodeSet();
		/** @return the string value of the result */
		public String getStringValue() throws Exception;
		/** @return number of items in the node-set/sequence */
		public int size() throws Exception;
		
		/**
		 * @param	i	index of the wanted item. Contrary to positions, i starts at 0
		 * @return ith item of the result
		 */
		public XPathNode get(int i) throws Exception;
		
		/**
		 * @return an XMLFragmentsString configured to represent this result
		 */
		public XMLFragmentsString toXMLFragmentsString() throws Exception;
	}
	
	/** one component of the Result, used in the Node-set summary table of the XPath tool */
	public static interface XPathNode{
		/** @return has got a name (e.g. an element has a name) */
		public boolean hasExpandedName();
		/** @return has got a value (here, an element or document don't have a value) */
		public boolean hasDomValue();
		/** @return string representation of the type of this item */
		public String getType();
		/**
		 * shouldn't be called if hasExpandedName() returns false !
		 * @return name or empty-string.*/
		public String getName();
		/**
		 * @return string value of this item
		 */
		public String getDomValue()throws Exception;
	}
}
