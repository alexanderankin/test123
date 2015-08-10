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
	public ElementDecl getElementDecl(String name)
	{
		if(html)
			name = name.toLowerCase();

		String prefix = getElementNamePrefix(name);
		CompletionInfo info = mappings.get(prefix);

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

		TagParser.Tag parentTag = null;
		try {
			parentTag = TagParser.findLastOpenTag(buffer.getText(0,pos),pos,this);
		}
		catch (Exception e) {}
			

		if(parentTag == null)
		{
			// add everything
			Iterator iter = mappings.keySet().iterator();
			while(iter.hasNext())
			{
				String prefix = (String)iter.next();
				CompletionInfo info = (CompletionInfo)
				mappings.get(prefix);
				info.getAllElements(prefix, returnValue);
			}
		}
		else
		{
			String parentPrefix = getElementNamePrefix(parentTag.tag);
			ElementDecl parentDecl = getElementDecl(parentTag.tag);
			if(parentDecl != null)
				returnValue.addAll(parentDecl.getChildElements(parentPrefix));

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
			ElementDecl startParentDecl = getElementDecl(startParentTag.tag);

			String endParentPrefix = getElementNamePrefix(endParentTag.tag);
			ElementDecl endParentDecl = getElementDecl(endParentTag.tag);

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
