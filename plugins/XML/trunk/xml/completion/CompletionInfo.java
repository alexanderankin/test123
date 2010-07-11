/*
 * CompletionInfo.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml.completion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.StandardUtilities;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
//}}}

public class CompletionInfo
{
	public ArrayList<ElementDecl> elements;
	public HashMap<String, ElementDecl> elementHash;
	public ArrayList<EntityDecl> entities;
	/** Appears to implement a bidirectional relationship? */
	public HashMap entityHash;
	
	public ArrayList elementsAllowedAnywhere;

	/** namespace for this completion info */
	public String namespace;
	
	public boolean nameConflict;
	
	//{{{ CompletionInfo constructor
	public CompletionInfo()
	{
		this(new ArrayList<ElementDecl>(), new HashMap<String, ElementDecl>(),
			new ArrayList<EntityDecl>(), new HashMap(),
			new ArrayList());

		addEntity(EntityDecl.INTERNAL,"lt","<");
		addEntity(EntityDecl.INTERNAL,"gt",">");
		addEntity(EntityDecl.INTERNAL,"amp","&");
		addEntity(EntityDecl.INTERNAL,"quot","\"");
		addEntity(EntityDecl.INTERNAL,"apos","'");  
	} //}}}

	//{{{ CompletionInfo constructor
	public CompletionInfo(ArrayList<ElementDecl> elements, 
		HashMap<String, ElementDecl> elementHash,
		ArrayList<EntityDecl> entities, HashMap entityHash,
		ArrayList elementsAllowedAnywhere)
	{
		this.elements = elements;
		this.elementHash = elementHash;
		this.entities = entities;
		this.entityHash = entityHash;
		this.elementsAllowedAnywhere = elementsAllowedAnywhere;
	} //}}}

	//{{{ addEntity() method
	public void addEntity(int type, String name, String value)
	{
		addEntity(new EntityDecl(type,name,value));
	} //}}}

	//{{{ addEntity() method
	public void addEntity(int type, String name, String publicId, String systemId)
	{
		addEntity(new EntityDecl(type,name,publicId,systemId));
	} //}}}

	//{{{ addEntity() method
	public void addEntity(EntityDecl entity)
	{
		entities.add(entity);
		if(entity.type == EntityDecl.INTERNAL
			&& entity.value.length() == 1)
		{
			Character ch = new Character(entity.value.charAt(0));
			entityHash.put(entity.name, ch);
			entityHash.put(ch, entity.name);
		}
	} //}}}

	//{{{ addElement() method
	public void addElement(ElementDecl element)
	{
		elementHash.put(element.name,element);
		elements.add(element);
	} //}}}

	//{{{ getAllElements() method
	public void getAllElements(String prefix, List<ElementDecl> out)
	{
		// only look for global elements, so use elementHash instead of elements
		for(ElementDecl decl: elementHash.values())
		{
			if (decl.isAbstract()) 
			{
				List<ElementDecl> repls = decl.findReplacements(prefix);
				out.addAll(repls);
				
				
			}
			else out.add(decl.withPrefix(prefix));
		}
	} //}}}

	//{{{ getElementDeclLocal(CompletionInfo, localName) method
	/**
	 * @return the first declaration (global or local) found for localName
	 *         or null
	 */
	public ElementDecl getElementDeclLocal(String localName)
	{
		// resorting to the first one in all declarations,
		// even if it's the wrong one
		for(ElementDecl e: elements){
			if(localName.equals(e.name)){
				return e;
			}
		}
		return null;
	}
	//}}}


	//{{{ toString() method
	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		buf.append("<element-list>\n\n");

		for(int i = 0; i < elements.size(); i++)
		{
			buf.append(elements.get(i));
			buf.append('\n');
		}

		buf.append("\n</element-list>\n\n<entity-list>\n\n");

		buf.append("<!-- not implemented yet -->\n");
		/* for(int i = 0; i < entities.size(); i++)
		{
			buf.append(entities.get(i));
			buf.append('\n');
		} */

		buf.append("\n</entity-list>");

		return buf.toString();
	} //}}}

	//{{{ getCompletionInfoForBuffer() method
	public static CompletionInfo getCompletionInfoForBuffer(Buffer buffer)
	{
		Iterator iter = globs.keySet().iterator();
		while(iter.hasNext())
		{
			Pattern re = (Pattern) iter.next();
			Matcher m = re.matcher(buffer.getName());
			if(m.matches())
				return getCompletionInfoFromResource((String)globs.get(re));
		}

		String resource = jEdit.getProperty("mode."
			+ buffer.getMode().getName()
			+ ".xml.completion-info");
		if(resource != null)
		{
			CompletionInfo info = getCompletionInfoFromResource(resource);

			if(info != null)
				return info;
		}

		return null;
	} //}}}

	//{{{ getCompletionInfoForNamespace() method
	public static CompletionInfo getCompletionInfoForNamespace(String namespace)
	{
		synchronized(lock)
		{
			Object obj = completionInfoNamespaces.get(namespace);
			if(obj instanceof String)
			{
				CompletionInfo info = getCompletionInfoFromResource((String)obj);
				info.namespace = namespace;
				completionInfoNamespaces.put(namespace, info);
				return info;
			}
			else
				return (CompletionInfo)obj;
		}
	} //}}}

	//{{{ getCompletionInfoFromResource() method
	public static CompletionInfo getCompletionInfoFromResource(String resource)
	{
		synchronized(lock)
		{
			CompletionInfo info = (CompletionInfo)completionInfoResources.get(resource);
			if(info != null)
				return info;

			Log.log(Log.DEBUG,CompletionInfo.class,"Loading " + resource);

			CompletionInfoHandler handler = new CompletionInfoHandler();

			try
			{
				XMLReader parser = new org.apache.xerces.parsers.SAXParser();
				parser.setFeature("http://apache.org/xml/features/validation/dynamic",true);
				parser.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
				parser.setErrorHandler(handler);
				parser.setEntityResolver(handler);
				parser.setContentHandler(handler);
				parser.parse(resource);
			}
			catch(SAXException se)
			{
				Throwable e = se.getException();
				if(e == null)
					e = se;
				Log.log(Log.ERROR,CompletionInfo.class,e);
			}
			catch(Exception e)
			{
				Log.log(Log.ERROR,CompletionInfo.class,e);
			}

			info = handler.getCompletionInfo();
			completionInfoResources.put (resource, info);

			return info;
		}
	} //}}}

	//{{{ clone() method
	public Object clone()
	{
		return new CompletionInfo(
			(ArrayList)elements.clone(),
			(HashMap)elementHash.clone(),
			(ArrayList)entities.clone(),
			(HashMap)entityHash.clone(),
			(ArrayList)elementsAllowedAnywhere.clone()
		);
	} //}}}

	//{{{ Private members
	private static HashMap<Pattern, String> globs;
	private static HashMap<String, CompletionInfo> completionInfoResources;
	private static HashMap<String, Object> completionInfoNamespaces;
	private static Object lock;

	//{{{ Class initializer
	static
	{
		completionInfoResources = new HashMap<String, CompletionInfo>();
		globs = new HashMap<Pattern, String>();
		int i = 0;
		String glob;
		while((glob = jEdit.getProperty("xml.completion.glob." + i + ".key")) != null)
		{
			String info = jEdit.getProperty("xml.completion.glob." + i + ".value");
			try
			{
				globs.put(Pattern.compile(StandardUtilities.globToRE(glob),
					Pattern.CASE_INSENSITIVE), info);
			}
			catch(PatternSyntaxException pse)
			{
				Log.log(Log.ERROR,CompletionInfo.class,pse);
			}

			i++;
		}

		completionInfoNamespaces = new HashMap<String, Object>();
		i = 0;
		String namespace;
		while((namespace = jEdit.getProperty("xml.completion.namespace." + i + ".key")) != null)
		{
			String info = jEdit.getProperty("xml.completion.namespace." + i + ".value");
			completionInfoNamespaces.put(namespace, info);

			i++;
		}

		lock = new Object();
	} //}}}

	//}}}
}
