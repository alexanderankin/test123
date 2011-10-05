/*
 * HTMLHyperlinkSource.java - Hyperlink source from the XML plugin
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Eric Le Lay
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package xml.hyperlinks;

import java.io.Reader;
import java.net.URI;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import sidekick.SideKickParsedData;
import sidekick.IAsset;
import sidekick.util.ElementUtil;
import sidekick.util.Location;
import sidekick.util.SideKickAsset;
import sidekick.util.SideKickElement;

import gatchan.jedit.hyperlinks.*;

import xml.Resolver;
import xml.CharSequenceReader;
import xml.XmlParsedData;
import sidekick.html.parser.html.*;
import static sidekick.html.parser.html.HtmlDocument.*;
import xml.completion.ElementDecl;
import xml.completion.IDDecl;

import static xml.hyperlinks.XMLHyperlinkSource.createOffset;
/**
 * Provides hyperlinks from HTML attributes.
 * Supported hyperlinks are all attributes with type URI
 * in the HTML 4.01 spec.
 * Links to other documents and anchors inside document
 * are supported, but fragment identifiers in other documents
 * are not.
 * the HTML/HEAD/BASE element is used to resolve URIs
 * if present.
 * No reparsing is required, contrary to XMLHyperlinkSource
 * which reparses the start tag.
 *
 * @author Eric Le Lay
 * @version $Id$
 */
public class HTMLHyperlinkSource implements HyperlinkSource
{
	/**
	 * Returns the hyperlink for the given offset.
	 * returns an hyperlink as soon as pointer enters the attribute's value
	 *
	 * @param buffer the buffer
	 * @param offset the offset
	 * @return the hyperlink (or null if there is no hyperlink)
	 */
	public Hyperlink getHyperlink(Buffer buffer, int offset){
		View view = jEdit.getActiveView();
		SideKickParsedData _data = SideKickParsedData.getParsedData(view);
		
		if(!(_data instanceof XmlParsedData))
		{
			return null;
		}
		
		XmlParsedData data = (XmlParsedData)_data;
		
		IAsset asset = data.getAssetAtOffset(offset);
		if(asset == null){
			Log.log(Log.DEBUG, HTMLHyperlinkSource.class,"no Sidekick asset here");
			return null;
		} else {
			int wantedLine = buffer.getLineOfOffset(offset);
			int wantedLineOffset = buffer.getVirtualWidth(wantedLine, offset - buffer.getLineStartOffset(wantedLine));

			SideKickElement elt = ((SideKickAsset)asset).getElement();
			
			Tag startTag;
			if(elt instanceof TagBlock){
				startTag = ((TagBlock)elt).startTag;
			}else if(elt instanceof Tag){
				startTag = (Tag)elt;
			}else{
				System.err.println("unexpected asset type: "+elt.getClass());
				startTag = null;
				return null;
			}

			System.err.println("startL="+startTag.getStartLocation()+",endL="+startTag.getEndLocation());
			int start = ElementUtil.createStartPosition(buffer,startTag).getOffset();
			int end= ElementUtil.createEndPosition(buffer,startTag).getOffset();
			System.err.println("start="+start+",end"+end);
			/* if the offset is inside start tag */
			if(offset <= end)
			{
				System.err.println("inside open tag");
				AttributeList al = startTag.attributeList;
				if(al == null){
					System.err.println("no attributes");
					return null;
				}else{
					for(Attribute att: al.attributes){
						// offset is inside attribute's value
						if(  (    att.getValueStartLocation().line < wantedLine+1
						       || (att.getValueStartLocation().line == wantedLine+1
							   && att.getValueStartLocation().column <= wantedLineOffset)
						      )
						      &&
						      (    att.getEndLocation().line > wantedLine+1
						       || (att.getEndLocation().line == wantedLine+1
							   && att.getEndLocation().column > wantedLineOffset)
						      )
						  )
						{
							System.err.println("inside attribute "+att.name+"="+att.value);
							return getHyperlinkForAttribute(buffer, offset, data, startTag, att);
						}
					}
					System.err.println("not inside attributes");
					return null;
				}
			}else{
				return null;
			}
		}
	}
	
	/**
	 * get an hyperlink for an identified HTML attribute
	 * @param	buffer	current buffer
	 * @param	offset	offset where an hyperlink is required in current buffer
	 * @param	data		sidekick tree for current buffer
	 * @param	asset		element containing offset
	 * @param	att		parsed attribute
	 */
	public Hyperlink getHyperlinkForAttribute(
		Buffer buffer, int offset, XmlParsedData data, 
		Tag startTag, Attribute att)
	{
		String tagLocalName = startTag.tagName;
		
		String localName = att.getName();
		String value = att.getValue();
		boolean quoted;
		if((value.startsWith("\"")&&value.endsWith("\""))
			||(value.startsWith("'")&&value.endsWith("'")))
		{
			value = value.substring(1,value.length()-1);
			quoted = true;
		}else{
			quoted = false;
		}
		
		System.err.println("inside "+tagLocalName+" @"+localName);
		
		Hyperlink h = getHyperlinkForAttribute(buffer, offset,
			tagLocalName, localName, value,
			data, startTag, att, quoted);
		
		if(h == null) {
		
			ElementDecl eltDecl  = data.getElementDecl(localName,offset);
			if(eltDecl == null){
				System.err.println("no element declaration for "+tagLocalName);
			}else{
				ElementDecl.AttributeDecl attDecl = eltDecl.attributeHash.get(localName);
				if(attDecl == null){
					System.err.println("no attribute declaration for "+localName);
					return null;
				}else{
					if("IDREF".equals(attDecl.type)){
						System.err.println("found IDREF");
						return getHyperlinkForIDREF(buffer, data, value, att, quoted);
					}else if("anyURI".equals(attDecl.type)){
						String href =  resolve(value, buffer, offset, data);
						if(href!=null){
							return newJEditOpenFileHyperlink(buffer, att, href, quoted);
						}
					}else if("IDREFS".equals(attDecl.type)){
						System.err.println("found IDREFS");
						return getHyperlinkForIDREFS(buffer, offset, data, value, att, quoted);
					}
					System.err.println("attDecl.type="+attDecl.type);
					return null;
				}
			}
			
		} else {
			return h;
		}
		return null;
	}
	
	/**
	 * creates an hyperlink to the location of the element with id (in same or another buffer).
	 * @param	buffer	current buffer
	 * @param	data		sidekick tree
	 * @param	id		id we are looking for
	 * @param	att		parsed attribute (for hyperlink boundaries)
	 * @param	quoted	is the value inside quotes ?
	 */
	public Hyperlink getHyperlinkForIDREF(Buffer buffer,
		XmlParsedData data, String id, Attribute att,
		boolean quoted)
	{
		IDDecl idDecl = data.getIDDecl(id);
		if(idDecl == null){
			return null;
		} else{
			return newJEditOpenFileAndGotoHyperlink(buffer, att,
			idDecl.uri, idDecl.line, idDecl.column, quoted);
		}
	}
	
	// {{{ getHyperlinkForIDREFS() method
	private static final Pattern noWSPattern = Pattern.compile("[^\\s]+");
	/**
	 * creates an hyperlink to the location of the element with id (in same or another buffer)
	 * @param	buffer	current buffer
	 * @param	offset	offset of required hyperlink
	 * @param	data		sidekick tree
	 * @param	attValue		ids in the attribute
	 * @param	att		parsed attribute (for hyperlink boundaries)
	 * @param	quoted	is the value inside quotes ?
	 */
	public Hyperlink getHyperlinkForIDREFS(Buffer buffer, int offset,
		XmlParsedData data, String attValue, Attribute att,
		boolean quoted)
	{
		int attStart = createOffset(buffer, att.getValueStartLocation());
		// +1 for the quote around the attribute value
		if(quoted)attStart++;
		
		Matcher m = noWSPattern.matcher(attValue);
		while(m.find()){
			int st = m.start(0);
			int nd = m.end(0);
			if(attStart + st <= offset && attStart + nd >= offset){
				System.err.println("idref="+m.group(0));
				System.err.println("ids="+data.ids);
				IDDecl idDecl = data.getIDDecl(m.group(0));
				if(idDecl==null)return null;
				int start = attStart + st;
				int end= attStart + nd;
				int line = buffer.getLineOfOffset(start);
				return new jEditOpenFileAndGotoHyperlink(start, end, line, idDecl.uri, idDecl.line, idDecl.column);
			}
		}
		return null;
	}//}}}
	
	//{{{ getHyperlinkForAttribute(tagName,attName) method
	private static final Map<String,Set<String>> uriAttributes = new HashMap<String,Set<String>>();
	static{
		HashSet<String> h;
		
		h = new HashSet<String>();
		h.add("href");
		uriAttributes.put("a", h);
		uriAttributes.put("area", h);
		uriAttributes.put("link", h);
		
		h = new HashSet<String>();
		h.add("longdesc");
		h.add("usemap");
		uriAttributes.put("img", h);
		
		h = new HashSet<String>();
		h.add("cite");
		uriAttributes.put("q", h);
		uriAttributes.put("blockquote", h);
		uriAttributes.put("ins", h);
		uriAttributes.put("del", h);
		
		h = new HashSet<String>();
		h.add("usemap");
		uriAttributes.put("input", h);
		uriAttributes.put("object", h);
		
		h = new HashSet<String>();
		h.add("src");
		uriAttributes.put("script", h);
	}
	
	/**
	 * recognize hyperlink attributes by their parent element's
	 * namespace:localname and/or their namespace:localname
	 */
	public Hyperlink getHyperlinkForAttribute(Buffer buffer, int offset,
		String tagLocalName,
		String attLocalName, String attValue,
		XmlParsedData data, Tag tag, Attribute att,
		boolean quoted)
	{
		boolean isHREF = false;
		
		if(uriAttributes.containsKey(tagLocalName)
			&& uriAttributes.get(tagLocalName).contains(attLocalName))
		{
			System.err.println("found "+tagLocalName+" "+attLocalName);
			if(attValue.startsWith("#")){
				Location found = getNamedAnchorLocation(data, attValue.substring(1));
				if(found != null){
					// OpenFileAndGoto expects real line&column,
					// not virtual column
					int toffset = XMLHyperlinkSource.createOffset(buffer, found);
					int line = buffer.getLineOfOffset(toffset);
					int column = toffset - buffer.getLineStartOffset(line);
					// it's OK to have a path
					String href = buffer.getPath();
					return newJEditOpenFileAndGotoHyperlink(
						buffer, att, href, line, column, quoted);
				}
			}else{
				String href = resolve(attValue, buffer, offset, data);
				if(href==null){
					return null;
				}else{
					return newJEditOpenFileHyperlink(
						buffer, att, href, quoted);
				}
			}
		} else if("object".equals(tagLocalName)
			&& ("classid".equals(attLocalName)
				|| "data".equals(attLocalName)))
		{
			/* must resolve against codebase if present */
			String href = resolveRelativeTo(attValue,
				tag.getAttributeValue("codebase"));

			href = resolve(href, buffer, offset, data);
			if(href==null){
				return null;
			}else{
				return newJEditOpenFileHyperlink(
					buffer, att, href, quoted);
			}
		} else if("object".equals(tagLocalName)
			&& "archive".equals(attLocalName))
		{
			// +1 for the quote around the attribute value
			int attStart = createOffset(buffer, att.getValueStartLocation()) +1;
			
			Matcher m = noWSPattern.matcher(attValue);
			while(m.find()){
				int st = m.start(0);
				int nd = m.end(0);
				if(attStart + st <= offset && attStart + nd >= offset){
					/* must resolve against codebase if present */
					String href = resolveRelativeTo(m.group(0),
						tag.getAttributeValue("codebase"));

					href = resolve(href, buffer, offset, data);
					if(href==null)href=m.group(0);
					int start = attStart + st;
					int end= attStart + nd;
					int line = buffer.getLineOfOffset(start);
					return new jEditOpenFileHyperlink(start, end, line, href);
				}
			}
		}
		return null;
	}//}}}
	
	//{{{ resolveRelativeTo(href,base) methode
	/**
	 * resolves using URI.resolve() href against base
	*/	
	String resolveRelativeTo(String href, String base){
		if(base == null || "".equals(base))return href;
		
		try{
			return URI.create(base).resolve(href).toString();
		}catch(IllegalArgumentException iae){
			Log.log(Log.WARNING,HTMLHyperlinkSource.class,"error resolving against codebase",iae);
			return href;
		}
	}//}}}
	
	//{{{ resolve(uri, buffer) method
	/**
	 * resolve a potentially relative uri using HTML BASE element,
	 * the buffer's URL, xml.Resolver.
	 * Has the effect of opening the cached document if it's in cache (eg. docbook XSD if not in catalog).
	 * Maybe this is not desirable, because if there is a relative link in this document, it won't work
	 * because the document will be .jedit/dtds/cachexxxxx.xml and not the real url.
	 *
	 * @param	uri		text of uri to reach
	 * @param	buffer	current buffer
	 * @param	offset	offset in current buffer where an hyperlink is required
	 * @param	data		SideKick parsed data
	 *
	 * @return	resolved URL
	 */
	public String resolve(String uri, Buffer buffer, int offset, XmlParsedData data)
	{
		String href = null;
		String base = xml.PathUtilities.pathToURL(buffer.getPath());
		
		// {{{ use html/head/base
		TagBlock html = getHTML(data);
		if(html != null
			&& html.body.size()>0)
		{
			for(Iterator ith =  ((TagBlock)html).body.iterator();ith.hasNext();){
				HtmlElement head = (HtmlElement) ith.next();
				if(head instanceof TagBlock){
					if("head".equalsIgnoreCase(((TagBlock)head).startTag.tagName))
					{
						for(Iterator it =  ((TagBlock)head).body.iterator();it.hasNext();){
							HtmlElement e = (HtmlElement)it.next();
							if(e instanceof Tag
								&& "base".equalsIgnoreCase(((Tag)e).tagName))
							{
								// Base must be absolute in HTML 4.01
								String preBase = ((Tag)e).getAttributeValue("href");
								try{
									// add a dummy component, otherwise xml.Resolver
									// removes the last part of the xml:base
									// FIXME: review xml.Resolver : it should only be used with URLs
									// for current, now, so could use URI.resolve() instead of removing
									// last part of the path to get the parent...
									base = URI.create(preBase).resolve("dummy").toString();
								}catch(IllegalArgumentException iae){
									Log.log(Log.WARNING, XMLHyperlinkSource.class, "error resolving uri", iae);
								}
								break;
							}
						}
					}
					break;
				}
			}
		}//}}}
		
		try{
			href = Resolver.instance().resolveEntityToPath(
				"", /*name*/
				"", /*publicId*/
				base, /*current, augmented by xml:base */
				uri);
		}catch(java.io.IOException ioe){
			Log.log(Log.ERROR,XMLHyperlinkSource.class,"error resolving href="+uri,ioe);
		}
		return href;
	}//}}}
	
	/**
	 * create an hyperlink for attribute att.
	 * the hyperlink will span whole attribute value
	 * @param	buffer	current buffer
	 * @param	att		parsed attribute
	 * @param	href		uri to open
	 * @param	quoted	is the value inside quotes ?
	 */
	public Hyperlink newJEditOpenFileHyperlink(
		Buffer buffer, Attribute att, String href,
		boolean quoted)
	{
		int start = createOffset(buffer,att.getValueStartLocation());
		int end= createOffset(buffer,att.getEndLocation());
		if(quoted){
			start++;
			end--;
		}
        	int line = buffer.getLineOfOffset(start);
        	return new jEditOpenFileHyperlink(start, end, line, href);
	}

	/**
	 * create an hyperlink for attribute att.
	 * the hyperlink will span whole attribute value
	 * @param	buffer	current buffer
	 * @param	att		parsed attribute
	 * @param	href		uri to open
	 * @param	gotoLine	target line in buffer
	 * @param	gotoCol	target column in buffer
	 * @param	quoted	is the value inside quotes ?
	 */
	public Hyperlink newJEditOpenFileAndGotoHyperlink(
		Buffer buffer, Attribute att, String href, int gotoLine, int gotoCol,
		boolean quoted)
	{
		int start = createOffset(buffer,att.getValueStartLocation());
		int end= createOffset(buffer,att.getEndLocation());
		if(quoted){
			start++;
			end--;
		}
        	int line = buffer.getLineOfOffset(start);
        	return new jEditOpenFileAndGotoHyperlink(start, end, line, href, gotoLine, gotoCol);
	}

	TagBlock getHTML(XmlParsedData data) {
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode)data.root;
		
		DefaultMutableTreeNode docRoot = (DefaultMutableTreeNode)tn.getFirstChild();
		
		if(docRoot == null){
			System.err.println("not parsed ??");
			return null;
		}else{
			SideKickElement elt = ((SideKickAsset)data.getAsset(docRoot)).getElement();
			if(elt instanceof TagBlock){
				return (TagBlock)elt;
			}
		}
		return null;
	}
	
	private static class FoundException extends RuntimeException{}
	private static final FoundException foundException = new FoundException();
	
	private static final class NamedAnchorVisitor extends HtmlVisitor{
		private final String searchedAnchor;
		Location foundLoc;
		
		NamedAnchorVisitor(String searched){
			searchedAnchor = searched;
			foundLoc = null;
		}
		
		public void visit(Tag t) {
			String v = null;
			if("a".equalsIgnoreCase(t.tagName)){
				 v = t.getAttributeValue("name");
			}
			if(v == null){
				v = t.getAttributeValue("id");
			}
			if(searchedAnchor.equals(v)){
				foundLoc = t.getStartLocation();
				throw foundException;
			}
		}
	}
	
	public Location getNamedAnchorLocation(XmlParsedData data, String name){
		TagBlock html = getHTML(data);
		if(html != null){
			NamedAnchorVisitor v = new NamedAnchorVisitor(name);
			try{
				html.accept(v);
			}catch(FoundException e){
				return v.foundLoc;
			}
		}
		return null;
	}

	public static HyperlinkSource create(){
		return new FallbackHyperlinkSource(
			Arrays.asList(new HTMLHyperlinkSource(),
				new gatchan.jedit.hyperlinks.url.URLHyperlinkSource()));
	}
}
