/*
 * XmlParsedData.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

//{{{ Imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.StringList;

import sidekick.SideKickParsedData;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.completion.IDDecl;
import xml.parser.TagParser;
import xml.parser.XmlTag;

import com.thaiopensource.xml.util.Name;
//}}}

/**
 * Encapsulates the results of parsing a buffer, either using Xerces or the
 * Swing HTML parser.
 */
public class XmlParsedData extends SideKickParsedData
{
	
	public boolean html;
	/**
	 * A mapping of namespace to CompletionInfo objects.
	 *  namespace of "" is the default namespace.
	 */
	private Map<String, CompletionInfo> mappings;
	
	/**
	 *  A list of all identifiers encountered during the parse?
	 */
	public List<IDDecl> ids;

	public void setCompletionInfo(String namespace, CompletionInfo info) {
		if(namespace == null)namespace = "";
		mappings.put(namespace, info);
	}
	
	//{{{ XmlParsedData constructor
	public XmlParsedData(String fileName, boolean html)
	{
		super(fileName);
		this.html = html;
		mappings = new HashMap<String, CompletionInfo>();
		ids = new ArrayList<IDDecl>();
	} //}}}

	//{{{ getNoNamespaceCompletionInfo() method
	public CompletionInfo getNoNamespaceCompletionInfo()
	{
		CompletionInfo info = mappings.get("");
		if(info == null)
		{
			info = new CompletionInfo();
			mappings.put("",info);
		}

		return info;
	} //}}}

	//{{{ getElementDecl() method
	public ElementDecl getElementDecl(String name, int pos)
	{
		if(html)
			name = name.toLowerCase();

		String prefix = getElementNamePrefix(name);
		
		CompletionInfo info;
		
		// simple case where we don't need to get the namespace
		if(mappings.size() == 1)
		{
			info = mappings.values().iterator().next();
		}
		else
		{
			String ns = getNamespaceForPrefix(prefix,pos);
			info = mappings.get(ns);
		}

		if(info == null)
			return null;
		else
		{
			String lName;
			int prefixLen = prefix.length();
			if(prefixLen == 0)
				lName = name;
			else
				lName = name.substring(prefixLen + 1);

			ElementDecl decl = info.elementHash.get(lName);
			if(decl == null)
				return null;
			else
				return decl.withPrefix(prefix);
		}
	} //}}}

	//{{{ getElementDecl() method
	public ElementDecl getElementDecl(DefaultMutableTreeNode node,XmlTag tag)
	{
		CompletionInfo info = mappings.get(tag.namespace);

		if(info == null)
			return null;
		else
		{
			String prefix =  tag.getPrefix();
			String lName = tag.getLocalName();

			ElementDecl decl;
			if(info.elementHash != null) decl = info.elementHash.get(lName);
			else decl = null;
			if(decl == null)
			{
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
				if(parentNode.getUserObject() instanceof XmlTag)
				{
					XmlTag parentTag = (XmlTag)parentNode.getUserObject();
					ElementDecl parentDecl = getElementDecl(parentNode,parentTag);
					if(parentDecl.elementHash!=null && parentDecl.elementHash.containsKey(lName))
					{
						return parentDecl.elementHash.get(lName).withPrefix(prefix);
					}
				}
				return null;
			}
			else
				return decl.withPrefix(prefix);
		}
	} //}}}

	//{{{ getXPathForPosition() method
	public String getXPathForPosition(int pos)
	{
		TreePath path = getTreePathForPosition(pos);
		if(path == null)return null;
		
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode)path.getLastPathComponent();
		TreeNode[]steps = tn.getPath();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)steps[0];
		String xpath = "";
		if(steps.length == 1)
		{
			//there is only the node with the file name
			xpath = null;
		}
		else
		{
			parent = (DefaultMutableTreeNode)steps[1];
			if( ! (parent.getUserObject() instanceof XmlTag))
			{
				// TODO: maybe implement it also for HTML
				return null;
			}
			Map<String,String> prefixToNS = new HashMap<String,String>();
			Map<String,String> nsToPrefix = new HashMap<String,String>();
			
			Name[] preXPath = new Name[steps.length-2];
			int[]  preXPathIndexes = new int[steps.length-2];
			
			Name rootName;

			XmlTag curTag = (XmlTag)parent.getUserObject();
			String lname = curTag.getLocalName();
			String ns = curTag.namespace;
			String prefix = curTag.getPrefix();
			
			assert(ns != null);
			
			rootName = new Name(ns,lname);
			
			prefixToNS.put(prefix,ns);
			nsToPrefix.put(ns,prefix);
			
			for(int i=2;i<steps.length;i++)
			{
				DefaultMutableTreeNode cur=(DefaultMutableTreeNode)steps[i];

				curTag = (XmlTag)cur.getUserObject();
				ns = curTag.namespace;
				lname = curTag.getLocalName();
				prefix = curTag.getPrefix();
				
				int jCur = parent.getIndex(cur);
				int cntChild = 0;
				for(int j=0;j<=jCur;j++)
				{
					DefaultMutableTreeNode aChild = (DefaultMutableTreeNode)parent.getChildAt(j);
					XmlTag aTag = (XmlTag)aChild.getUserObject();
					if(lname.equals(aTag.getLocalName())
						  && ns.equals(aTag.namespace))
					{
						cntChild++;
					}
				}
				preXPath[i-2] = new Name(ns,lname);
				preXPathIndexes[i-2] = cntChild;
				
				/* implementation choice here :
				   I think the XPath will be more usable
				   if the same prefix is re-used, even if it's 
				   not the one in the document.
				*/
				if(!nsToPrefix.containsKey(ns))
				{
					// same prefix, other ns
					if(    prefixToNS.containsKey(prefix)
					   && !prefixToNS.get(prefix).equals(ns))
					{
						/* keep the prefix close
						   to what was in the document
						   (only a suffixed number)
						 */
						int uniq=0;
						// special case for default
						// prefix, since a prefix must
						// begin with a letter
						if("".equals(prefix))
						{
							prefix+= "_";
						}
						while(prefixToNS.containsKey(prefix+String.valueOf(uniq)))
						{
							uniq++;
						}
						prefix += String.valueOf(uniq);
					}
					prefixToNS.put(prefix,ns);
					nsToPrefix.put(ns,prefix);
				}
				
				parent = cur;
			}
			
			prefix = nsToPrefix.get(rootName.getNamespaceUri());
			xpath = "/" ;
			if(!"".equals(prefix)) xpath += prefix + ":";
			xpath += rootName.getLocalName();
			
			for(int i=0;i<preXPath.length;i++)
			{
				prefix = nsToPrefix.get(preXPath[i].getNamespaceUri());
				xpath += "/";
				if(!"".equals(prefix))xpath+= prefix + ":";
				xpath += preXPath[i].getLocalName();
				xpath += "[" + preXPathIndexes[i] + "]"; 
			}
		}
		return xpath;
	}
	//}}}
	
	//{{{ getAllowedElements() method
	/** @returns a list containing Elements or Attributes */
	public List<ElementDecl> getAllowedElements(Buffer buffer, int pos)
	{
		
		List<ElementDecl> returnValue = new LinkedList<ElementDecl>();

		String text = buffer.getText(0,pos);
		
		// make sure we are not inside a tag
		if(TagParser.isInsideTag(text,pos))
		{
			return returnValue;
		}
		
		TagParser.Tag parentTag = null;
		try
		{
			parentTag = TagParser.findLastOpenTag(buffer.getText(0,pos),pos,this);
		}
		catch (Exception e) {}
			
		System.err.println("parentTag="+parentTag);
		if(parentTag == null)
		{
			// add everything
			Iterator iter = mappings.keySet().iterator();
			while(iter.hasNext())
			{
				String ns = (String)iter.next();
				CompletionInfo info = (CompletionInfo)
				mappings.get(ns);
				info.getAllElements("", returnValue);
			}
		}
		else
		{
			ElementDecl parentDecl;
			String parentPrefix = getElementNamePrefix(parentTag.tag);

			TreePath path = getTreePathForPosition(pos);
			Map<String,String> bindings = getNamespaceBindings(path);

			if(html)
			{
				parentDecl = getElementDecl(parentTag.tag, parentTag.start+1);// +1 to be inside the tag
			}
			else
			{
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)path.getLastPathComponent();
				XmlTag parentXmlTag = (XmlTag)parentNode.getUserObject();
	
				if(parentTag.tag.equals(parentXmlTag.getName()))
				{
					parentDecl = getElementDecl(parentNode,parentXmlTag);
				}
				else if(parentNode.getParent()!=null)
				{
					parentNode = (DefaultMutableTreeNode)parentNode.getParent();
					parentXmlTag = (XmlTag)parentNode.getUserObject();
					
					if(parentTag.tag.equals(parentXmlTag.getName()))
					{
						parentDecl = getElementDecl(parentNode,parentXmlTag);
					}
					else
					{
						parentDecl = getElementDecl(parentTag.tag, parentTag.start+1);// +1 to be inside the tag
					}
				}
				else
				{
					parentDecl = getElementDecl(parentTag.tag, parentTag.start+1);// +1 to be inside the tag
				}
			}
			
			if(parentDecl != null)
			{
				returnValue.addAll(parentDecl.getChildElements(bindings));
			}
			/*
			// don't need this, do we ?
			else
			{
				// add everything but the parent's prefix now
				Iterator iter = mappings.keySet().iterator();
				while(iter.hasNext())
				{
					String prefix = (String)iter.next();
					if(!prefix.equals(parentPrefix))
					{
						CompletionInfo info = (CompletionInfo)
							mappings.get(prefix);
						info.getAllElements(prefix,returnValue);
					}
				}
			}*/
		}
		Collections.sort(returnValue, new ElementDecl.Compare());
		return returnValue;
	} //}}}

	//{{{ getAllowedElements() method
	/* called by updateTagList only */
	public List getAllowedElements(Buffer buffer, int startPos, int endPos)
	{
		ArrayList returnValue = new ArrayList();

		// make sure we are not inside a tag
		if(TagParser.isInsideTag(buffer.getText(0,startPos),startPos)) {
			return returnValue;
		}

		// make sure we are not inside a tag
		if(TagParser.isInsideTag(buffer.getText(0,endPos),endPos)) {
			return returnValue;
		}

		TagParser.Tag startParentTag = TagParser.findLastOpenTag(
			buffer.getText(0,startPos),startPos,this);

		TagParser.Tag endParentTag = TagParser.findLastOpenTag(
			buffer.getText(0,endPos),endPos,this);

		if(startParentTag == null) { 
			if(endParentTag == null) {
				// add everything
				Iterator iter = mappings.keySet().iterator();
				while(iter.hasNext())
				{
					String prefix = (String)iter.next();
					CompletionInfo info = (CompletionInfo)
					mappings.get(prefix);
					info.getAllElements(prefix,returnValue);
				}
			}
			else
				return returnValue;
		}
		else if(endParentTag == null) {
			return returnValue;
		}
		else
		{
			String startParentPrefix = getElementNamePrefix(startParentTag.tag);
			ElementDecl startParentDecl = getElementDecl(startParentTag.tag,startParentTag.start+1);

			String endParentPrefix = getElementNamePrefix(endParentTag.tag);
			ElementDecl endParentDecl = getElementDecl(endParentTag.tag,endParentTag.start+1);

			if(startParentDecl == null)
				return returnValue;
			else if(endParentDecl == null)
				return returnValue;
			else if(!startParentPrefix.equals(endParentPrefix))
				return returnValue;
			else
			{

				if(startParentDecl != null)
					returnValue.addAll(startParentDecl.getChildElements(startParentPrefix));

				// add everything but the parent's prefix now
				Iterator iter = mappings.keySet().iterator();
				while(iter.hasNext())
				{
					String prefix = (String)iter.next();
					if(!prefix.equals(startParentPrefix))
					{
						CompletionInfo info = (CompletionInfo)
							mappings.get(prefix);
						info.getAllElements(prefix,returnValue);
					}
				}
			}
		}

		Collections.sort(returnValue,new ElementDecl.Compare());
		return returnValue;
	} //}}}

	//{{{ getNamespaceBindings() method
	public Map<String,String> getNamespaceBindings(TreePath path){
		Map<String,String> bindings = new HashMap<String,String>();
		
		if(path == null)return bindings;
		if(html)return bindings;
		
		Object[] pathObjs = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObjectPath();
		for(int i=1;i<pathObjs.length;i++){ // first step is the name of the file
			XmlTag t = (XmlTag)pathObjs[i];
			if(t.namespaceBindings != null){
				bindings.putAll(t.namespaceBindings);
			}
		}
		return bindings;
	}
	//}}}
	
	//{{{ getNamespaceForPrefix() method
	public String getNamespaceForPrefix(String prefix, int pos){
		
		if(html)return null;
		
		TreePath path = getTreePathForPosition(pos);
		if(path == null)return null;
		
		Object[] pathObjs = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObjectPath();
		for(int i=pathObjs.length-1;i>0;i--){  //first object is a SourceAsset for the file
			XmlTag t = (XmlTag)pathObjs[i];
			if(t.namespaceBindings != null){
				for(Map.Entry<String,String> binding: t.namespaceBindings.entrySet()){
					if(binding.getValue().equals(prefix))return binding.getKey();
				}
			}
		}
		// no namespace is "" in the mappings
		if("".equals(prefix))return "";
		else return null;
	}
	//}}}

	//{{{ Private members

	//{{{ getElementPrefix() method
	private static String getElementNamePrefix(String name)
	{
		int index = name.indexOf(':');
		if(index == -1)
			return "";
		else
			return name.substring(0,index);
	} //}}}

	//}}}
}
