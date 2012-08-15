/*
 * NamespaceBindings.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2012 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 *
 */
package xml;

import java.util.*;

import org.xml.sax.helpers.NamespaceSupport;

/** namespace to prefix map. 
 *  Remove ambiguities and mistakes due to confusion on what is the key or the value
 *  when using Map&lt;String,String&gt; for namespace bindings.
 *  */
public class NamespaceBindings {
	// namespace -> prefix
	private HashMap<String,String> bindings;
	
	public NamespaceBindings(){
		bindings = new HashMap<String,String>();
	}
	
	public NamespaceBindings(NamespaceBindings namespacesToInsert) {
		bindings = new HashMap<String, String>(namespacesToInsert.bindings);
	}

	public void put(String namespace, String prefix){
		bindings.put(namespace, prefix);
	}
	
	public String getPrefix(String namespace){
		return bindings.get(namespace);
	}
	
	public String getNamespace(String prefix){
		for(Map.Entry<String,String> en: bindings.entrySet()){
			if(en.getValue().equals(prefix))return en.getKey();
		}
		return "";
	}
	
	public boolean containsNamespace(String namespace){
		return bindings.containsKey(namespace);
	}
	
	public boolean containsPrefix(String prefix){
		return bindings.containsValue(prefix);
	}

	/**
	 * append xmlns:XX="..." namespace bindings for each item to buf
	 * @param buf					buffer to append to
	 */
	public void appendNamespaces(StringBuilder buf)
	{
		if(bindings.isEmpty())return;
		
		for(Map.Entry<String, String> en: bindings.entrySet())
		{
			buf.append(' ');
			buf.append("xmlns:").append(en.getValue()).append("=\"").append(en.getKey()).append('"');
		}
	}

	public void putAll(NamespaceBindings namespaceBindings) {
		bindings.putAll(namespaceBindings.bindings);
	}

	public boolean isEmpty() {
		return bindings.isEmpty();
	}

	/**
	 * generate a new prefix, unique among namespaces bindings
	 * 
	 * @param namespaces	namespaces bindings not to override
	 * @return	new prefix
	 */
	public static String generatePrefix(NamespaceBindings ... namespaces) {
		String pre;
		for(int i=0;;i++){
			pre = "ns"+i;
			boolean notSeen = true;
			for(NamespaceBindings namespace: namespaces){
				notSeen &= !namespace.containsPrefix(pre);
				if(!notSeen)break;
			}
			if(notSeen)return pre;
		}
	}

	/**
	 * create a qualified name from localname, ns and given namespaces bindings.
	 * If no namespace, returns localname directly.
	 * Otherwise, pre:localname is returned, where 
	 * pre is taken from namespacesToInsert then if not found from namespaces, then is generated to be unique
	 * among namespaces and namespacesToInsert (always of the form nsNUMBER).
	 * @param localname				local name
	 * @param ns					namespace localname is in
	 * @param namespaces			already declared namespaces
	 * @param namespacesToInsert	new namespaces (IN/OUT: a new binding may be added)
	 * @param emptyPrefixIsOK		is empty prefix OK for non null namespace ? (false for attributes
	 * @return	the qualified name
	 */
	public static String composeName(String localname, String ns, NamespaceBindings namespaces, NamespaceBindings namespacesToInsert, boolean emptyPrefixIsOK){
	
		if(ns == null || "".equals(ns))
		{
			return localname;
		}
		else
		{
			// prefer getting from namespacesToInsert, for the case when EditTagDialog is called on a tag redefining a namespace
			String pre = namespacesToInsert.getPrefix(ns);
			if(pre == null || (!emptyPrefixIsOK && "".equals(pre)))
			{
					pre = namespaces.getPrefix(ns);
			}
			if(pre == null || (!emptyPrefixIsOK && "".equals(pre)))
			{
				// special case for the predefined XML namespace
				if(NamespaceSupport.XMLNS.equals(ns))
				{
					pre = "xml";
				}
				else
				{
					pre = NamespaceBindings.generatePrefix(namespaces, namespacesToInsert);
					namespacesToInsert.put(ns,pre);
				}
			}
	
			if("".equals(pre)){
				return localname;
			}else{
				return pre + ":" + localname;
			}
	
		}
	}

}