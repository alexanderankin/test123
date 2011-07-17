/*
 * XMLHyperlinkSource.java - Hyperlink source from the XML plugin
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

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import sidekick.SideKickParsedData;
import sidekick.IAsset;
import sidekick.util.ElementUtil;
import sidekick.util.Location;

import gatchan.jedit.hyperlinks.*;

import xml.Resolver;
import xml.CharSequenceReader;
import xml.XmlParsedData;
import xml.parser.javacc.*;
import xml.parser.XmlTag;
import xml.completion.ElementDecl;
import xml.completion.IDDecl;

/**
 * Provides hyperlinks from XML attributes.
 * Supported hyperlinks are :
 * <ul>
 * 	<li>XInclude (&lt;xi:include href="..."/&gt;). Fragments are not supported:
 *	      the buffer is opened and that's all.
 *	</li>
 *	<li>simple XLinks (&lt;myelt xlink:href="..."/&gt;). Only simple links are supported:
 *	      the buffer is opened and that's all.
 *	</li>
 *	<li>IDREF attributes, when the buffer has been parsed and the attribute has the
 *	      IDEF datatype. This includes docbook 4.x links (&lt;xref linkend="id"/&gt;).
 *	</li>
 *	<li>XML Schema (XSD) schema location (&lt;myelt xsi:schemaLocation="ns1 url1..."&gt;)
 *	      and no namespace schema location (&lt;myelt xsi:noNamespaceSchemaLocation="url"&gt;).
 *	</li>
 *	<li>attributes with datatype anyURI
 *	      eg. XSD include and import (&lt;xs:include schemaLocation="url"/&gt;).
 *	</li>
 * </ul>
 *
 * @todo	HTML attributes
 * @todo	HTML BASE tag (beware: it's in HTML/HEAD)
 *
 * @author Eric Le Lay
 * @version $Id$
 */
public class XMLHyperlinkSource implements HyperlinkSource
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
			Log.log(Log.DEBUG, XMLHyperlinkSource.class,"no Sidekick asset here");
			return null;
		} else {
			int wantedLine = buffer.getLineOfOffset(offset);
			int wantedLineOffset = buffer.getVirtualWidth(wantedLine, offset - buffer.getLineStartOffset(wantedLine));


			/* use the XML javacc parser to parse the start tag.*/
			int max = buffer.getLength();
			int sA = asset.getStart().getOffset();
			int lA = asset.getEnd().getOffset()-sA;
			if(sA < 0 || sA > max){
				return null;
			}
			if(lA < 0 || sA+lA > max){
				return null;
			}
			CharSequence toParse = buffer.getSegment(sA,lA);
			int line = buffer.getLineOfOffset(sA);
			int col = buffer.getVirtualWidth(line, sA-buffer.getLineStartOffset(line)+1);
			System.err.println("line="+line+",col="+col);
			Reader r = new CharSequenceReader(toParse);
			XmlParser parser = new XmlParser(r, line+1, col);
			parser.setTabSize( buffer.getTabSize() );
			try{
				XmlDocument.XmlElement startTag = parser.Tag();
				System.err.println("startL="+startTag.getStartLocation()+",endL="+startTag.getEndLocation());
				int start = ElementUtil.createStartPosition(buffer,startTag).getOffset();
				int end= ElementUtil.createEndPosition(buffer,startTag).getOffset();
				System.err.println("start="+start+",end"+end);
				/* if the offset is inside start tag */
				if(offset <= end)
				{
					System.err.println("inside open tag");
					XmlDocument.AttributeList al = ((XmlDocument.Tag)startTag).attributeList;
					if(al == null){
						System.err.println("no attributes");
						return null;
					}else{
						for(XmlDocument.Attribute att: al.attributes){
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
						        	if(asset instanceof XmlTag) {
						        		return getHyperlinkForAttribute(buffer, offset, data, asset, att);
						        	} else {
						        		System.err.println("TODO: hyperlinks for HTML attributes");
						        	}
						        }
						}
						System.err.println("not inside attributes");
						return null;
					}
				}else{
					return null;
				}
			}catch(ParseException pe){
				Log.log(Log.ERROR, XMLHyperlinkSource.class, "error parsing element", pe);
				return null;
			}
		}
	}
	
	/**
	 * get an hyperlink for an identified XML attribute
	 * @param	buffer	current buffer
	 * @param	offset	offset where an hyperlink is required in current buffer
	 * @param	data		sidekick tree for current buffer
	 * @param	asset		element containing offset
	 * @param	att		parsed attribute
	 */
	public Hyperlink getHyperlinkForAttribute(
		Buffer buffer, int offset, XmlParsedData data, 
		IAsset asset, XmlDocument.Attribute att)
	{
		XmlTag sideKickTag = (XmlTag)asset;
		
		if(sideKickTag.attributes == null){
			System.err.println("Sidekick doesn't have attributes");
			return null;
		}
		
		int attIndex = sideKickTag.attributes.getIndex(att.name);
		if(attIndex < 0){
			System.err.println("Sidekick doesn't have this attribute: "+att.name);
			return null;
		}
		
		String tagNS = sideKickTag.namespace;
		String tagLocalName = sideKickTag.getLocalName();
		
		String ns = sideKickTag.attributes.getURI(attIndex);
		String localName = att.name.contains(":")? sideKickTag.attributes.getLocalName(attIndex) : att.name;
		String value = sideKickTag.attributes.getValue(attIndex);
		
		System.err.println("inside "+tagNS+":"+tagLocalName+" @"+ns+":"+localName);
		
		Hyperlink h = getHyperlinkForAttribute(buffer, offset,
			tagNS,tagLocalName, ns, localName, value,
			data, sideKickTag, att);
		
		if(h == null) {
		
			ElementDecl eltDecl  = data.getElementDecl(sideKickTag.getName(),offset);
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
						return getHyperlinkForIDREF(buffer, data, value, att);
					}else if("anyURI".equals(attDecl.type)){
						// FIXME: shall it use xml:base ?
						String href =  resolve(value, buffer, offset, data, sideKickTag, false);
						if(href!=null){
							return newJEditOpenFileHyperlink(buffer, att, href);
						}
					}else if("IDREFS".equals(attDecl.type)){
						System.err.println("found IDREFS");
						return getHyperlinkForIDREFS(buffer, offset, data, value, att);
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
	 */
	public Hyperlink getHyperlinkForIDREF(Buffer buffer,
		XmlParsedData data, String id, XmlDocument.Attribute att)
	{
		IDDecl idDecl = data.getIDDecl(id);
		if(idDecl == null){
			return null;
		} else{
			return newJEditOpenFileAndGotoHyperlink(buffer, att,
			idDecl.uri, idDecl.line, idDecl.column);
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
	 */
	public Hyperlink getHyperlinkForIDREFS(Buffer buffer, int offset,
		XmlParsedData data, String attValue, XmlDocument.Attribute att)
	{
		int attStart = createOffset(buffer, att.getValueStartLocation());
		// +1 for the quote around the attribute value
		attStart++;
		
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
	
	//{{{ getHyperlinkForAttribute(tagNS,tagName,attNS,attName) method
	private static final Pattern nsURIPairsPattern = Pattern.compile("[^\\s]+\\s+([^\\s]+)");
	/**
	 * recognize hyperlink attributes by their parent element's
	 * namespace:localname and/or their namespace:localname
	 */
	public Hyperlink getHyperlinkForAttribute(Buffer buffer, int offset,
		String tagNS, String tagLocalName,
		String attNS, String attLocalName, String attValue,
		XmlParsedData data, XmlTag tag, XmlDocument.Attribute att)
	{
		String href = null; 
		if("http://www.w3.org/2001/XInclude".equals(tagNS)
			&& "include".equals(tagLocalName)
			&& "href".equals(attLocalName))
		{
			System.err.println("found xi:include");
			href = resolve(attValue, buffer, offset, data, tag, true);
		} else if("http://www.w3.org/1999/xlink".equals(attNS)
			&& "href".equals(attLocalName))
		{
			System.err.println("found xlink");
			href = resolve(attValue, buffer, offset, data, tag, true);
		} else if("http://www.w3.org/2001/XMLSchema-instance".equals(attNS)
			&& "noNamespaceSchemaLocation".equals(attLocalName))
		{
			System.err.println("found xsi:noNamespaceSchemaLocation");
			href = resolve(attValue, buffer, offset, data, tag, false);
		} else if("http://www.w3.org/2001/XMLSchema-instance".equals(attNS)
			&& "schemaLocation".equals(attLocalName))
		{
			System.err.println("found xsi:schemaLocation");
			
			// +1 for the quote around the attribute value
			int attStart = createOffset(buffer, att.getValueStartLocation()) +1;
			
			Matcher m = nsURIPairsPattern.matcher(attValue);
			// find will accept unbalanced pairs of ns->uri
			while(m.find()){
				int st = m.start(1);
				int nd = m.end(1);
				if(attStart + st <= offset && attStart + nd >= offset){
					href = resolve(m.group(1), buffer, offset, data, tag, false);
					if(href==null)href=m.group(1);
					int start = attStart + st;
					int end= attStart + nd;
					int line = buffer.getLineOfOffset(start);
					return new jEditOpenFileHyperlink(start, end, line, href);
				}
			}
		}
		if(href==null){
			return null;
		}else{
			return newJEditOpenFileHyperlink(
				buffer, att, href);
		}
	}//}}}
	
	/**
	 * resolve a potentially relative uri using xml:base attributes,
	 * the buffer's URL, xml.Resolver.
	 * Has the effect of opening the cached document if it's in cache (eg. docbook XSD if not in catalog).
	 * Maybe this is not desirable, because if there is a relative link in this document, it won't work
	 * because the document will be .jedit/dtds/cachexxxxx.xml and not the real url.
	 *
	 * @param	uri		text of uri to reach
	 * @param	buffer	current buffer
	 * @param	offset	offset in current buffer where an hyperlink is required
	 * @param	data		SideKick parsed data
	 * @param	tag		SideKick asset
	 * @param	useXmlBase	should xml:base attribute be used to resolve uri (only for XML!)
	 *
	 * @return	resolved URL
	 */
	public String resolve(String uri, Buffer buffer, int offset, XmlParsedData data, XmlTag tag,
		boolean useXmlBase)
	{
		String href = null;
		String base = xml.PathUtilities.pathToURL(buffer.getPath());
		if(useXmlBase){
			try {
				URI baseURI = URI.create(base);
				Object[] pathObjs = data.getObjectsTo(offset);
				// go down the tree, resolving existing xml:base uri if they exist
				for(int i=1; i<pathObjs.length;i++){  //first object (i==0) is a SourceAsset for the file
					XmlTag t = (XmlTag)pathObjs[i];
					String newBase = t.attributes.getValue("xml:base"); // xml is a reserved prefix
					if(newBase!=null){
						baseURI = baseURI.resolve(newBase);
					}
				}
				if(!base.equals(baseURI.toString())){
					// add a dummy component, otherwise xml.Resolver
					// removes the last part of the xml:base
					// FIXME: review xml.Resolver : it should only be used with URLs
					// for current, now, so could use URI.resolve() instead of removing
					// last part of the path to get the parent...
					baseURI = baseURI.resolve("dummy");
					base = baseURI.toString();
				}
			}catch(IllegalArgumentException e){
				Log.log(Log.WARNING, XMLHyperlinkSource.class, "error resolving uri", e);
			}
		}
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
	}
	
	/**
	 * create an hyperlink for attribute att.
	 * the hyperlink will span whole attribute value
	 * @param	buffer	current buffer
	 * @param	att		parsed attribute
	 * @param	href		uri to open
	 */
	public Hyperlink newJEditOpenFileHyperlink(
		Buffer buffer, XmlDocument.Attribute att, String href)
	{
		int start = createOffset(buffer,att.getValueStartLocation());
		int end= createOffset(buffer,att.getEndLocation());
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
	 */
	public Hyperlink newJEditOpenFileAndGotoHyperlink(
		Buffer buffer, XmlDocument.Attribute att, String href, int gotoLine, int gotoCol)
	{
		int start = createOffset(buffer,att.getValueStartLocation());
		int end= createOffset(buffer,att.getEndLocation());
        	int line = buffer.getLineOfOffset(start);
        	return new jEditOpenFileAndGotoHyperlink(start, end, line, href, gotoLine, gotoCol);
	}

	/*
	 * adapted from sidekick.util.ElementUtil.createEndPosition()
	 * TODO: maybe contribute this to ElementUtil
	 */
	public static int createOffset(Buffer buffer, Location loc){
		int line = Math.max(
		                   Math.min(loc.line - 1, buffer.getLineCount() - 1)
		                 , 0);
		int line_offset = buffer.getLineStartOffset(line);
		int[] totalVirtualWidth = new int[ 1 ];
		int column_offset = buffer.getOffsetOfVirtualColumn(
			    Math.max( line, 0 ),
			    Math.max( loc.column - 1, 0 ),
			    totalVirtualWidth );
		if ( column_offset == -1 ) {
			System.err.println("wanted virtual column "+(loc.column-1)+", totalVirtualWitdth="+totalVirtualWidth[0]);
			if(loc.column-1 == totalVirtualWidth[ 0 ]){
				System.err.println("setting offset to real end of line offset");
				column_offset = buffer.getLineLength(line);
			}else {
				System.err.println("setting offset to virtual width");
				column_offset = totalVirtualWidth[ 0 ];
			}
			System.err.println("changed column_offset:"+column_offset);
		}
		return line_offset + column_offset ;
	}
}
