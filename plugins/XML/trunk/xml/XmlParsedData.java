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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.View;

import sidekick.SideKickParsedData;
import sidekick.ExpansionModel;
import sidekick.SideKickUpdate;
import sidekick.IAsset;

import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.completion.IDDecl;
import xml.parser.TagParser;
import xml.parser.XmlTag;

import com.thaiopensource.xml.util.Name;

import java.util.Enumeration;


//}}}

/**
 * Encapsulates the results of parsing a buffer, either using Xerces or the
 * Swing HTML parser.
 */
public class XmlParsedData extends SideKickParsedData
{
	// sorting values
	public static final int SORT_BY_NAME = 0;
	public static final int SORT_BY_LINE = 1;
	public static final int SORT_BY_TYPE = 2;
	
	private static int sortBy = SORT_BY_LINE;
	protected static boolean sortDown = true;
	
	public boolean html;
	
	/** indicate that all xmlns: attributes appear only on the root element
	 *  so there's no need to find the exact namespace context of the parent node.
	 *  the namespace context of the root element is sufficient
	 */
	public boolean allNamespacesBindingsAtTop;

	/**
	 * A mapping of namespace to CompletionInfo objects.
	 *  namespace of "" is the default namespace.
	 */
	private Map<String, CompletionInfo> mappings;
	
	/**
	 *  A list of all identifiers encountered during the parse.
	 *  Used in XMLInsert IDs pane and for Hyperlinks navigation
	 */
	public List<IDDecl> ids;

	public List<EntityDecl> entities;
	public Map entityHash;
	
	/** entities are added to the noNamespaceCompletionInfo, so if a schema is used
	 * on top of DTD, the entities are lost.
	 * To prevent this, the entities are copied into the parsed data
	 */
	public void setCompletionInfo(String namespace, CompletionInfo info) {
		if(namespace == null)namespace = "";
		mappings.put(namespace, info);

		// if this append, should remove entities declared in this CompletionInfo
		// and nowhere else.
		if(info == null)throw new UnsupportedOperationException("setCompletionInfo("+namespace+",null");
		for(EntityDecl en : info.entities)
		{
			// avoid duplicates of &lt;, &amp; etc.
			if(!entityHash.containsKey(en.name)) addEntity(en);
		}
	}
	
	//{{{ XmlParsedData constructor
	public XmlParsedData(String fileName, boolean html)
	{
		super(fileName);
		this.html = html;
		mappings = new HashMap<String, CompletionInfo>();
		ids = new ArrayList<IDDecl>();
		allNamespacesBindingsAtTop = true;
		entities = new ArrayList<EntityDecl>();
		entityHash = new HashMap();
		setCompletionInfo("",getNoNamespaceCompletionInfo());//register NoNamespaceCompletionInfo at least once for the common entities
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

	//{{{ getElementDecl(String,int) method
	// FIXME: pass buffer as parameter, 
	public ElementDecl getElementDecl(String name, int pos)
	{
		if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
			"getElementDecl("+name+","+pos+")");
		
		ElementDecl decl = null;
		String prefix = getElementNamePrefix(name);
		String localName = "".equals(prefix) ? name : name.substring(prefix.length()+1);
	
	
		if(html)
		{
			decl = getElementDeclInternal(name,pos);
		}
		else
		{
			TreePath path = null;
			Map<String,String> bindings = null;
			
			if(allNamespacesBindingsAtTop){
				if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
					"allNamespacesBindingsAtTop");
				bindings = getRootNamespaceBindings();
			}else {
				path = getTreePathForPosition(pos);
				bindings = getNamespaceBindings(path);
			}
	
			// find parent's CompletionInfo
			CompletionInfo info;
			
			if(mappings.isEmpty()){
				if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
					"no completionInfo");
				return null;
			}else if(mappings.size() == 1){
				info = mappings.values().iterator().next();
				if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
					"only 1 completionInfo, for ns "+info.namespace);
			}else{
				String NS = getNS(bindings,prefix);
				info = mappings.get(NS);
				if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
					"many completionInfos ("+mappings.keySet()+"); getting for NS ="+NS);
			}
			
			if(info == null){
				if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
					"CompletionInfo not found");
			}else {
				if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
					"got CompletionInfo for "+info.namespace);
				
				// find elementdecl inside CompletionInfo
				if(info.nameConflict){
					if(path == null)path = getTreePathForPosition(pos);
					
					// find the elements tree leading to parent in the document
	
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)path.getLastPathComponent();
					XmlTag parentXmlTag = (XmlTag)parentNode.getUserObject();

					if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
						"parentXmlTag="+parentXmlTag.getName());
					
					// SideKick tree is in sync
					if(name.equals(parentXmlTag.getName()))
					{
						if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
							"SideKick tree is in sync");
						decl = getElementDecl(parentNode,parentXmlTag);
					}
					else 
					{
						// it's not the parent ; let's say it's an ancestor
						/* wrong approach : don't know which one to return. 
						   So either
						    - keep the Sidekick tree in sync (ie Parse on Keystroke) !
						    - parse backward to reconstruct ancestry to the last Sidekick node
						   Don't do this :
							ElementDecl ancestorDecl = getElementDecl(parentNode,parentXmlTag);
							
							String NS = getNS(bindings,prefix);
							decl = findElementDeclInDescendants(ancestorDecl,NS,localName, new ArrayList<ElementDecl>());
						*/
						if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
							"Sidekick tree out of sync ; giving up");
						decl = null;
					}
				}else{
					if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
						"no nameConflict");
					if(info.elementHash.containsKey(localName)){
						if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
							"global declaration");
						decl = info.elementHash.get(localName);
					}else{
						if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
							"local declaration");
						decl = info.getElementDeclLocal(localName);
					}
				}
			}
		}
		return decl;
	}
	/**
	 * @param	ancestorDecl	ElementDecl to inspect; never the one looked for
	 * @param	visitedDecls	avoid lopping by storing visited decls
	 */
	ElementDecl findElementDeclInDescendants(ElementDecl ancestorDecl,String namespace,String localName, List<ElementDecl> visitedDecls){
		if(visitedDecls.contains(ancestorDecl))return null;
		if(ancestorDecl.elementHash != null){
			for(ElementDecl child : ancestorDecl.elementHash.values()){
				ElementDecl res = null;
				if(child.name.equals(localName) && namespace.equals(child.completionInfo.namespace)){
					return child;
				}else{
					visitedDecls.add(ancestorDecl);
					res = findElementDeclInDescendants(child,namespace,localName,visitedDecls);
					if(res != null)return res;
					else visitedDecls.remove(visitedDecls.size()-1);
				}
			}
		}
		return null;
	}
	//}}}
	
	//{{{ getElementDeclInternal(name, pos) method
	/**
	 * finds a global declaration of an element, returns it with correct prefix
	 * @param	name	qualified name (with prefix) of an element
	 * @param	pos	used to get the namespace bindings
	 */
	public ElementDecl getElementDeclInternal(String name,int pos)
	{
		if(html)
			name = name.toLowerCase();

		String prefix = getElementNamePrefix(name);
		
		CompletionInfo info;
		
		if(mappings.isEmpty())
		{
			return null;
		}	
		// simple case where we don't need to get the namespace
		else if(mappings.size() == 1)
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
		if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
			"getElementDecl("+node+","+tag.getName()+")");
		CompletionInfo info = mappings.get(tag.namespace);

		if(info == null)
			return null;
		else
		{
			String prefix =  tag.getPrefix();
			String lName = tag.getLocalName();

			ElementDecl decl;
			if(!info.nameConflict && info.elementHash != null) {
				decl = info.elementHash.get(lName);
				return decl.withPrefix(prefix);
			}
			else
			{
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
				if(parentNode.getUserObject() instanceof XmlTag)
				{
					XmlTag parentTag = (XmlTag)parentNode.getUserObject();
					ElementDecl parentDecl = getElementDecl(parentNode,parentTag);
					if(parentDecl != null && parentDecl.elementHash!=null && parentDecl.elementHash.containsKey(lName))
					{
						return parentDecl.elementHash.get(lName).withPrefix(prefix);
					}
				}
				else if(info.elementHash != null)
				{
					// at root node : allowed to use a global ElementDecl
					decl = info.elementHash.get(lName);
					return decl.withPrefix(prefix);
				}
				return null;
			}
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
		StringBuilder xpath = new StringBuilder();
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
			StringBuilder prefix = new StringBuilder(curTag.getPrefix());
			
			assert(ns != null);
			
			rootName = new Name(ns,lname);
			
			prefixToNS.put(prefix.toString(), ns);
			nsToPrefix.put(ns, prefix.toString());
			
			for(int i=2;i<steps.length;i++)
			{
				DefaultMutableTreeNode cur=(DefaultMutableTreeNode)steps[i];

				curTag = (XmlTag)cur.getUserObject();
				ns = curTag.namespace;
				lname = curTag.getLocalName();
				prefix = new StringBuilder(curTag.getPrefix());
				
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
							prefix.append(' ');
						}
						while(prefixToNS.containsKey(prefix.toString() + uniq))
						{
							uniq++;
						}
						prefix.append(uniq);
					}
					prefixToNS.put(prefix.toString(), ns);
					nsToPrefix.put(ns, prefix.toString());
				}
				
				parent = cur;
			}
			
			prefix = new StringBuilder(nsToPrefix.get(rootName.getNamespaceUri()));
			xpath.append('/') ;
			if(prefix.length() > 0) 
				xpath.append(prefix).append(':');
			xpath.append(rootName.getLocalName());
			
			for(int i=0;i<preXPath.length;i++)
			{
				prefix = new StringBuilder(nsToPrefix.get(preXPath[i].getNamespaceUri()));
				xpath.append('/');
				if(prefix.length() > 0)
					xpath.append(prefix).append(':');
				xpath.append(preXPath[i].getLocalName());
				xpath.append('[').append(preXPathIndexes[i]).append(']'); 
			}
		}
		return xpath == null ? null : xpath.toString();
	}
	//}}}
	
	//{{{ getAllowedElements() method
	/** @return a list containing Elements or Attributes */
	public List<ElementDecl> getAllowedElements(Buffer buffer, int pos)
	{
		IAsset asset = getAssetAtOffset(pos);
		System.err.println("asset at "+pos+" is :"+asset+" ("+asset.getStart().getOffset()+","+asset.getEnd().getOffset()+")");
		List<ElementDecl> returnValue = new LinkedList<ElementDecl>();

		TagParser.Tag parentTag = null;
		try
		{
			parentTag = TagParser.findLastOpenTag(buffer.getText(0,pos),pos,this);
		}
		catch (Exception e) {}
			
		if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
			"parentTag="+parentTag);
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
			ElementDecl parentDecl = null;
			//String parentPrefix = getElementNamePrefix(parentTag.tag);
			//String parentLocalName = "".equals(parentPrefix) ? parentTag.tag : parentTag.tag.substring(parentPrefix.length()+1);


			if(html)
			{
				parentDecl = getElementDeclInternal(parentTag.tag,pos);
				returnValue.addAll(parentDecl.getChildElements(Collections.<String,String>emptyMap()));
			}
			else
			{
				TreePath path = null;
				Map<String,String> bindings = null;
				
				if(allNamespacesBindingsAtTop){
					if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class, "allNamespacesBindingsAtTop");
					bindings = getRootNamespaceBindings();
				}else {
					path = getTreePathForPosition(pos);
					bindings = getNamespaceBindings(path);
				}

				parentDecl = getElementDecl(parentTag.tag,parentTag.start+1);
				if(parentDecl != null){
					if(bindings == null)bindings = Collections.<String,String>emptyMap();
					returnValue.addAll(parentDecl.getChildElements(bindings));
				}
			}
		}
		Collections.sort(returnValue, new ElementDecl.Compare());
		return returnValue;
	} //}}}

	//{{{ getAllowedElements() method
	/* called by updateTagList only */
	// FIXME: use new algorithms
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
	
	//{{{ getRootNamespaceBindings() method
	public Map<String,String> getRootNamespaceBindings(){
		Map<String,String> bindings;
		
		if(!html && root != null && root.getChildCount() > 0){
			TreeNode node = root.getChildAt(0);
			DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)node;
			Object o = dmtn.getUserObject();
			if(o instanceof XmlTag){
				bindings = ((XmlTag)o).namespaceBindings;
			}else {
				bindings = Collections.emptyMap();
			}
		}else {
			bindings = Collections.emptyMap();
		}
		if(Debug.DEBUG_COMPLETION)Log.log(Log.DEBUG,XmlParsedData.class,
			"getRootNamespaceBindings()=>"+bindings);
		return bindings;
	}
	//}}}

	//{{{ getNamespaceForPrefix() method
	public String getNamespaceForPrefix(String prefix, int pos){
		
		if(html)return null;
		
		if(allNamespacesBindingsAtTop){
			Map<String,String> bindings=getRootNamespaceBindings();
			if(bindings != null){
				String ns = getNS(bindings,prefix);
				if(ns != null)return ns;
			}
		}else{
			TreePath path = getTreePathForPosition(pos);
			if(path == null)return null;
			
			Object[] pathObjs = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObjectPath();
			for(int i=pathObjs.length-1;i>0;i--){  //first object (i==0) is a SourceAsset for the file
				XmlTag t = (XmlTag)pathObjs[i];
				if(t.namespaceBindings != null){
					for(Map.Entry<String,String> binding: t.namespaceBindings.entrySet()){
						if(binding.getValue().equals(prefix))return binding.getKey();
					}
				}
			}
		}
		// no namespace is "" in the mappings
		if("".equals(prefix))return "";
		else return null;
	}
	//}}}
	
	//{{{ getNS() method
	public String getNS(Map<String,String> nsToPrefix, String prefix){
		for(Map.Entry<String,String> en:nsToPrefix.entrySet()){
			if(prefix.equals(en.getValue())){
				return en.getKey();
			}
		}
		return "";
	}
	//}}}
	
	//{{{ done() method
	/**
 	 * Causes node sorting to be done.  Subclasse can override for their own 
 	 * purposes, for example, the TldXmlParsedData class renames nodes based 
 	 * on child nodes. 
 	 */
	public void done(View view) {
		sort(view);	
	}
	//}}}
	
	//{{{ setSortBy(int) method
	public void setSortBy(int by) {
		switch (by) {
		    case SORT_BY_NAME:
		    case SORT_BY_LINE:
		    case SORT_BY_TYPE:
			sortBy = by;
			break;
		}
	}
	//}}}
	
	//{{{ getSortBy() method
	public int getSortBy() {
		return sortBy;
	}
	//}}}
	
	//{{{ setSortDirection(boolean) method
	public void setSortDirection(boolean down) {
		sortDown = down;	
	}
	//}}}
	
	//{{{ sort(view) method
	public void sort(final View view) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sortChildren((DefaultMutableTreeNode)root);
				tree.reload();
				expansionModel = createExpansionModel().getModel();
				EditBus.send(new SideKickUpdate(view));
			}
		} );
	}
	//}}}
	
	//{{{ sortChildren(node) method
	private void sortChildren(DefaultMutableTreeNode node) {
		List<DefaultMutableTreeNode> children = new ArrayList<DefaultMutableTreeNode>();
		Enumeration en = node.children();
		while(en.hasMoreElements()) {
			children.add((DefaultMutableTreeNode)en.nextElement());   
		}
		Collections.sort(children, getSorter());
		node.removeAllChildren();
		for (DefaultMutableTreeNode child : children) {
			node.add(child);
			sortChildren(child);
		}
	}
	//}}}

	//{{{ getSorter() method
	protected Comparator<DefaultMutableTreeNode> getSorter() {
		return new Comparator<DefaultMutableTreeNode>() {
			public int compare(DefaultMutableTreeNode tna, DefaultMutableTreeNode tnb) {
			    int sortBy = getSortBy();
			    switch (sortBy) {                // NOPMD, no breaks are necessary here
				case SORT_BY_LINE:
				    Integer my_line = new Integer(((XmlTag)tna.getUserObject()).getStart().getOffset());
				    Integer other_line = new Integer(((XmlTag)tnb.getUserObject()).getStart().getOffset());
				    return my_line.compareTo(other_line) * (sortDown ? 1 : -1);
				case SORT_BY_TYPE:
				    String my_on = ((XmlTag)tna.getUserObject()).getName();
				    String other_on = ((XmlTag)tnb.getUserObject()).getName();
				    int comp = my_on.compareTo(other_on) * (sortDown ? 1 : -1);
				    return comp == 0 ? compareNames(tna, tnb) : comp;
				case SORT_BY_NAME:
				default:
				    return compareNames(tna, tnb);
			    }
			}
			
			private int compareNames(DefaultMutableTreeNode tna, DefaultMutableTreeNode tnb) {
			    // sort by name
			    String my_name = ((XmlTag)tna.getUserObject()).getLongString();
			    String other_name = ((XmlTag)tnb.getUserObject()).getLongString();
			    return my_name.compareTo(other_name) * (sortDown ? 1 : -1);
			}
		} ;
	}//}}}
	
	//{{{ createExpansionModel() method
	protected ExpansionModel createExpansionModel() {
		ExpansionModel em = new ExpansionModel();
		em.add();   // root (filename node)
		em.add();   // document node
		if (root.getChildCount() != 0) {
			for (int i = 0; i < root.getChildAt(0).getChildCount(); i++) {
			    em.inc();   // first level children.  Is this enough?
			}
		}
		return em;
	}
	//}}}
	
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

	//{{{ getObjectsTo(pos) method
	public Object[] getObjectsTo(int pos){
			TreePath path = getTreePathForPosition(pos);
			if(path == null)return null;
			
			Object[] pathObjs = ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObjectPath();
			return pathObjs;
	}//}}}
	
	//{{{ getIDDecl(id) method
	/**
	 * convenience method to find an IDDecl by name
	 * @return	found IDDecl or null
	 */
	public IDDecl getIDDecl(String id){
		if(ids == null)return null;
		
		for(IDDecl idDecl : ids){
			if(idDecl.id.equals(id)){
				return idDecl;
			}
		}
		return null;
	}//}}}
	
	//{{{ Private members

	//{{{ getElementNamePrefix() method
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
