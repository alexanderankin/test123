package xml.parser;

// {{{ imports
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JPanel;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import sidekick.IAsset;
import sidekick.SideKickParsedData;
import xml.Resolver;
import xml.AntXmlParsedData;
import xml.XmlParsedData;
import xml.XmlPlugin;
import xml.SchemaMappingManager;
import xml.completion.CompletionInfo;
import xml.completion.ElementDecl;
import xml.completion.EntityDecl;
import xml.completion.IDDecl;
import xml.gui.XmlModeToolBar;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import static xml.Debug.*;
import xml.cache.Cache;
import xml.cache.CacheEntry;
// }}}
// {{{ class XercesParserImpl
/**
 * A SideKick XML parser that uses this under the covers:
 * reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
 *
 * @author kerik-sf
 * @version $Id$
 */

public class XercesParserImpl extends XmlParser
{
	public static String COMPLETION_INFO_CACHE_ENTRY = "CompletionInfo";
	
    private View view = null;
    
    // cache the toolbar panels per view
    private Map<View, JPanel> panels = new HashMap<View, JPanel>();
    
	//{{{ XercesParserImpl constructor
	public XercesParserImpl()
	{
		super("xml");
	} //}}}
	
	@Override
	public void activate(View view) {
	    this.view = view;   
	}
	
	//{{{ parse() method
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
	{
		long start = System.currentTimeMillis();
		Log.log(Log.NOTICE,XercesParserImpl.class,"parsing started @"+start);
		stopped = false;
		String text;
		try
		{
			buffer.readLock();
			text = buffer.getText(0,buffer.getLength());
		}
		finally
		{
			buffer.readUnlock();
		}

		
		XmlParsedData data = createXmlParsedData(buffer.getName(), buffer.getMode().toString(), false);
		
		if(text.length() == 0)return data;
		
		SchemaMapping mapping;
		if(SchemaMappingManager.isSchemaMappingEnabled(buffer))
		{
			mapping = SchemaMappingManager.getSchemaMappingForBuffer(buffer);
		}
		else
		{
			mapping = null;
		}

		ErrorListErrorHandler errorHandler = new ErrorListErrorHandler(
				 errorSource
				,buffer.getPath()
			);
		Handler handler = new Handler(buffer,text,errorHandler,data);


		XMLReader reader = null;
		SchemaAutoLoader schemaLoader = null;
		try
		{
			// One has to explicitely require the parser from XercesPlugin, otherwise
			// one gets the crimson version bundled in the JRE and the rest fails
			// miserably (see Plugin Bug #2950392)
			reader = new org.apache.xerces.parsers.SAXParser();
			
			// customize validation
			reader.setFeature("http://xml.org/sax/features/validation",
				buffer.getBooleanProperty("xml.validate"));
			// to enable documents without schemas
			reader.setFeature("http://apache.org/xml/features/validation/dynamic",
				buffer.getBooleanProperty("xml.validate"));
			// for Schema validation (eg in slackerdoc/index.xml
			reader.setFeature("http://apache.org/xml/features/validation/schema",
				buffer.getBooleanProperty("xml.validate"));
			// for documents using dtd for entities and schemas for validation
			if(buffer.getBooleanProperty("xml.validate.ignore-dtd"))
			{
				reader.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			}

			// turn on/off namespace support.
			// For some legacy documents, namespaces must be disabled
			reader.setFeature("http://xml.org/sax/features/namespaces",
				!buffer.getBooleanProperty("xml.namespaces.disable"));
			
			// always use EntityResolver2 so that built-in DTDs can be found
			reader.setFeature("http://xml.org/sax/features/use-entity-resolver2",
				true);
			
			// XInclude support
			reader.setFeature("http://apache.org/xml/features/xinclude",
				buffer.getBooleanProperty("xml.xinclude"));
			reader.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris",
				buffer.getBooleanProperty("xml.xinclude.fixup-base-uris"));
			
			//get access to the DTD
			reader.setProperty("http://xml.org/sax/properties/declaration-handler",handler);
			reader.setProperty("http://xml.org/sax/properties/lexical-handler",handler);
			
			reader.setProperty("http://apache.org/xml/properties/internal/grammar-pool",
				new CachedGrammarPool(buffer));
			
			schemaLoader = new SchemaAutoLoader(reader,mapping,buffer);

			schemaLoader.setErrorHandler(errorHandler);
			schemaLoader.setContentHandler(handler);
			schemaLoader.setEntityResolver(handler);

			//get access to the RNG schema
			handler.setSchemaAutoLoader(schemaLoader);
			reader = schemaLoader;
			
			//schemas.xml are disabled
			if(!SchemaMappingManager.isSchemaMappingEnabled(buffer)){
				String schemaFromProp = buffer.getStringProperty(SchemaMappingManager.BUFFER_SCHEMA_PROP);
				if(schemaFromProp != null){
					// the user has set the schema manually
					String baseURI = xml.PathUtilities.pathToURL(buffer.getPath());
					Log.log(Log.NOTICE, this,"forcing schema to {"+baseURI+","+schemaFromProp+"}");
					// schemas URLs are resolved against the buffer
					try
					{
						schemaLoader.forceSchema( baseURI,schemaFromProp);
					}
					catch(IOException ioe)
					{
						ioe.printStackTrace();
						Log.log(Log.ERROR,this,ioe);
					}
				}
			}
		}
		catch(SAXException se)
		{
			se.printStackTrace();
			Log.log(Log.ERROR,this,se);
		}

		//TODO
		CompletionInfo info = CompletionInfo.getCompletionInfoForBuffer(
			buffer);
		if(info != null)
			data.setCompletionInfo("",info);


		InputSource source = new InputSource();

		String rootDocument = buffer.getStringProperty("xml.root");
		if(rootDocument != null)
		{
			Log.log(Log.NOTICE,this,"rootDocument specified; "
				+ "parsing " + rootDocument);
			rootDocument = MiscUtilities.constructPath(
				MiscUtilities.getParentOfPath(buffer.getPath()), rootDocument);
			source.setSystemId(rootDocument);
		}
		else
		{
			source.setCharacterStream(new StringReader(text));
			source.setSystemId(buffer.getPath());
		}

		try
		{
			reader.parse(source);
		}
		catch(StoppedException e) //NOPMD interrupted parsing
		{
		}
		catch(IOException ioe)
		{
			Log.log(Log.ERROR,this,ioe);
			ioe.printStackTrace();
			errorSource.addError(ErrorSource.ERROR, buffer.getPath(), 0, 0, 0,
				ioe.toString());
		}
		catch(SAXParseException spe) //NOPMD already handled
		{
		}
		catch(SAXException se)
		{
			Log.log(Log.ERROR, this, se.getException());
			se.printStackTrace();
			if(se.getMessage() != null)
			{
				errorSource.addError(ErrorSource.ERROR,buffer.getPath(),
					0,0,0,se.getMessage());
			}
		}
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,e);
			e.printStackTrace();
		}
		finally
		{
			//set this property for the xml-open-schema action to work
			if(schemaLoader != null && schemaLoader.getSchemaURL()!=null)
			{
				buffer.setStringProperty(SchemaMappingManager.BUFFER_AUTO_SCHEMA_PROP,
					schemaLoader.getSchemaURL());
			}
		}
		
		// danson, a hack(?) to switch the buffer mode to 'ant'.  The first line glob
		// in the catalog file doesn't necessarily work for Ant files.  If the root
		// node is "project" and the mode is "xml", switch to "ant" mode.  I checked
		// through the current catalog file, and at the moment, Ant files are the 
		// only xml files that need this sort of extra check, so I think this hack
		// is pretty safe.
		DefaultMutableTreeNode root = data.root;
		IAsset rootAsset = (IAsset)root.getUserObject();
		if ("project".equals(rootAsset.getName())) {
		     buffer.setMode("ant");
		     AntXmlParsedData pd = new AntXmlParsedData(buffer.getName(), false);
		     pd.root = data.root;
		     pd.tree = data.tree;
		     pd.expansionModel = data.expansionModel;
		     data = pd;
		}

		Collections.sort(data.ids,new IDDecl.Compare());
		data.done(view);

		long end = System.currentTimeMillis();
		Log.log(Log.NOTICE,XercesParserImpl.class,"parsing has taken "+(end-start)+"ms");
		return data;
	} //}}}

	//{{{ Private members
	
	private XmlParsedData createXmlParsedData(String filename, String modeName, boolean html) {
        String dataClassName = jEdit.getProperty("xml.xmlparseddata." + modeName);
        if (dataClassName != null) {
            try {
                Class dataClass = Class.forName(dataClassName);
                java.lang.reflect.Constructor con = dataClass.getConstructor(String.class, Boolean.TYPE);
                return (XmlParsedData)con.newInstance(filename, html);
            }
            catch (Exception e) {
                 // ignored, just return an XmlParsedData if this fails   
                 e.printStackTrace();
            }
        }
        return new XmlParsedData(filename, html);   
	}

	//{{{ ...
	public XmlTag findParent(EditPane editPane, int caret){

		if (caret == 0)
			return null;
		SideKickParsedData _data = SideKickParsedData
			.getParsedData(editPane.getView());
		if(!(_data instanceof XmlParsedData))
			return null;
		if(XmlPlugin.isDelegated(editPane.getTextArea()))
			return null;
		XMLReader reader = null;
		try
		{
			// One has to explicitely require the parser from XercesPlugin, otherwise
			// one gets the crimson version bundled in the JRE and the rest fails
			// miserably (at least on Mac OS X, JDK 5)
			reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			reader.setFeature("http://xml.org/sax/features/validation",false);
			reader.setFeature("http://xml.org/sax/features/namespaces",true);
			reader.setFeature("http://xml.org/sax/features/use-entity-resolver2", true);
			reader.setFeature("http://apache.org/xml/features/xinclude",false);
			//reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error",true);
		}
		catch(SAXException se)
		{
			se.printStackTrace();
			Log.log(Log.ERROR,this,se);
		}


		XmlParsedData data = (XmlParsedData)_data;

		Buffer buffer = editPane.getBuffer();
		
		String text = buffer.getText(0,caret);

		FastHandler handler = new FastHandler(buffer,data);

		InputSource source = new InputSource();

		source.setCharacterStream(new StringReader(text));
		source.setSystemId(buffer.getPath());

		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		reader.setEntityResolver(handler);
		try
		{
			reader.parse(source);
		}
		catch(StoppedException e) // NOPMD interrupted parsing
		{
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch(SAXParseException spe) // NOPMD already handled
		{
		}
		catch(SAXException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	} //}}}
	
	//}}}

    //{{{ getPanel() method	
	public JPanel getPanel() {
	    if (view != null) {
	        String mode = view.getBuffer().getMode().toString();
	        String supported = jEdit.getProperty("xml.xmltoolbar.modes");
	        if (supported.indexOf(mode) > -1) {
                JPanel panel = panels.get(view);
                if (panel != null) {
                     return panel;   
                }
                XmlModeToolBar toolbar = new XmlModeToolBar(view);
                panels.put(view, toolbar);
                return toolbar;
            }
        }
        return null;
		
	}
	//}}}

	//{{{ Handler class
	class Handler extends DefaultHandler2 implements DeclHandler, LexicalHandler, ErrorHandler
	{
	    // {{{ members
		Buffer buffer;

		ErrorListErrorHandler errorHandler;
		String text;
		XmlParsedData data;

		HashMap<String, String> declaredPrefixes;
		Stack<DefaultMutableTreeNode> currentNodeStack;
		Locator loc;
		boolean empty;
		/** at root of document (no startElement() seen yet)*/
		boolean root = true;
		
		/** used to retrieve CompletionInfos for different namespaces (RNG) */
		SchemaAutoLoader schemaAutoLoader;
		
		// }}}
		// {{{ Handler constructor
		Handler(Buffer buffer, String text, ErrorListErrorHandler errorHandler,
			XmlParsedData data)
		{
			this.buffer = buffer;
			this.text = text;
			this.errorHandler = errorHandler;
			this.data = data;
			this.currentNodeStack = new Stack<DefaultMutableTreeNode>();
			this.empty = true;
			this.schemaAutoLoader = null;

		} // }}}

		private void setSchemaAutoLoader(SchemaAutoLoader sal){
			this.schemaAutoLoader = sal;
		}
		
		//{{{ endDocument() method
		public void endDocument() throws SAXException
		{
			// don't retrieve the null namespace schema
			// via getModelForNamespace(null), as it returns null, anyway !
		} //}}}

		//{{{ setDocumentLocator() method
		public void setDocumentLocator(Locator locator)
		{
			loc = locator;
		} //}}}

		//{{{ resolveEntity() method
		/**
		 * If you do this:
		 * reader.setProperty("use-entity-resolver2", true)
		 * Then this method should be called.
		 */
		public InputSource resolveEntity (String name, String publicId, String baseURI, String systemId)
			throws SAXException, java.io.IOException {

			if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"resolveEntity("+name+","+publicId+","+baseURI+","+systemId+")");

			InputSource source = null;

			try {
				source = Resolver.instance().resolveEntity(name, publicId, baseURI, systemId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				errorHandler.getErrorSource().addError(ErrorSource.ERROR,
					buffer.getPath(),
					Math.max(0,loc.getLineNumber()-1),0,0,
					e.getMessage());
			}

			if(source == null)
			{
				Log.log(Log.ERROR,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " cannot be resolved");
				// TODO: not sure whether it's the best thing to do :
				// it prints a cryptic "premature end of file"
				// error message
				InputSource dummy = new InputSource(systemId);
				dummy.setPublicId(publicId);
				dummy.setCharacterStream(new StringReader("<!-- -->"));
				return dummy;
			}
			else
			{
				if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " resolved to " + source.getSystemId());
				return source;
			}
		} //}}}

		//{{{ startPrefixMapping() method
		public void startPrefixMapping(String prefix, String uri)
		{
			if(declaredPrefixes == null)declaredPrefixes = new HashMap<String,String>();
			declaredPrefixes.put(uri,prefix);

			if(!root){
				data.allNamespacesBindingsAtTop = false;
			}
			
			// check for built-in completion info for this URI
			// (eg, XSL, XSD, XHTML has this).
			if(uri != null)
			{
				CompletionInfo info = CompletionInfo
					.getCompletionInfoForNamespace(uri);
				if(info != null)
				{
					Log.log(Log.DEBUG,XercesParserImpl.class,"using built-in completion info for "+uri);
					data.setCompletionInfo(uri,info);
					return;
				}
				if(schemaAutoLoader != null 
					&& schemaAutoLoader.getCompletionInfo() != null
						&& schemaAutoLoader.getCompletionInfo().containsKey(uri))
				{
					info = schemaAutoLoader.getCompletionInfo().get(uri);
					Log.log(Log.DEBUG,XercesParserImpl.class,"setting completionInfo for '"+prefix+"' : "+info.namespace+")");
					if(DEBUG_RNG_SCHEMA)Log.log(Log.DEBUG,XercesParserImpl.class,info);
					data.setCompletionInfo(uri,info);
				}
			}
			// don't retrieve schema based on prefix mapping anymore (for XSD).
			// see endElement(), where we use the PSVI
		} //}}}

		//{{{ endPrefixMapping() method
		public void endPrefixMapping(String prefix)
		{
		} //}}}
		
		private void setCompletionInfoFromSchema(String ns, String location, String schemaLocation, String nonsSchemaLocation){
			if(DEBUG_XSD_SCHEMA)Log.log(Log.DEBUG,Handler.class,"setCompletionInfoFromSchema("+ns+","+location+","+schemaLocation+","+nonsSchemaLocation+")");
			Map<String,CompletionInfo> infos = XSDSchemaToCompletion.getCompletionInfoFromSchema(location,schemaLocation, nonsSchemaLocation, errorHandler, buffer);
			for(Map.Entry<String,CompletionInfo> en: infos.entrySet()){
				String nsC = en.getKey();
				Log.log(Log.DEBUG,Handler.class,"setting completion info for :'"+nsC+"'");
				data.setCompletionInfo(nsC, en.getValue());
			}
		}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			if(root){
				// root element :
				
				// retrieve no-namespace CompletionInfo
				if(schemaAutoLoader != null 
					&& schemaAutoLoader.getCompletionInfo() != null
						&& schemaAutoLoader.getCompletionInfo().containsKey(""))
				{
					data.setCompletionInfo("",
						schemaAutoLoader.getCompletionInfo().get(""));
					// TODO: what about no-namespace ?
				}
				
				// retrieve schema grammar if available
				String schemaLocation = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
				String noNamespaceSchemaLocation = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation");
				// there may be a better implementation in Xerces in XMLSchemaLoader
				if(schemaLocation != null){
					String[] nsLocationPairs = schemaLocation.split("\\s+");
					if(nsLocationPairs.length % 2 == 0){
						
						for(int i=0;i<nsLocationPairs.length;i+=2){
							String ns = nsLocationPairs[i];
							String location = nsLocationPairs[i+1];
							setCompletionInfoFromSchema(ns,location, schemaLocation,noNamespaceSchemaLocation);
						}
					}
				}
				if(noNamespaceSchemaLocation != null){
					setCompletionInfoFromSchema(null,noNamespaceSchemaLocation, schemaLocation,noNamespaceSchemaLocation);
				}


				// unset root flag
				root = false;
			}
			
			empty = true;

			String currentURI = xml.PathUtilities.urlToPath(loc.getSystemId());

			// what do we do in this case?
			if(loc.getLineNumber() == -1){
				Log.log(Log.WARNING,XercesParserImpl.class,"no location for "+qName);
				return;
			}

			// add all attributes with type "ID" to the ids vector
			for(int i = 0; i < attrs.getLength(); i++)
			{
				if(attrs.getType(i).equals("ID")
					// as in http://www.w3.org/TR/xml-id/
					|| attrs.getQName(i).equals("xml:id"))
				{
					data.ids.add(new IDDecl(currentURI,
						attrs.getValue(i),qName,
						loc.getLineNumber() - 1,
						loc.getColumnNumber() - 1));
				}
			}

			if(!buffer.getPath().equals(currentURI))
				return;

			buffer.readLock();

			try
			{
				int line = Math.min(buffer.getLineCount() - 1,
					loc.getLineNumber() - 1);
				int column = loc.getColumnNumber() - 1;
				int offset = Math.min(text.length() - 1,
					buffer.getLineStartOffset(line)
					+ column - 1);

				offset = findTagStart(offset);
				Position pos = buffer.createPosition(offset);

				XmlTag newTag = createTag(qName, namespaceURI==null ? "" : namespaceURI, pos, attrs);
				newTag.namespaceBindings = declaredPrefixes;
				declaredPrefixes = null;
				
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTag);

				if(!currentNodeStack.isEmpty())
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						currentNodeStack.peek();

					node.insert(newNode,node.getChildCount());
				}
				else
					data.root.insert(newNode,0);

				currentNodeStack.push(newNode);
			}
			finally
			{
				buffer.readUnlock();
			}
		} //}}}
		
		private XmlTag createTag(String qname, String namespaceURI, Position pos, Attributes attrs) {
		    String tagClassName = jEdit.getProperty("xml.xmltag." + buffer.getMode().toString());
		    if (tagClassName != null) {
		        try {
		            Class tagClass = Class.forName(tagClassName);
		            java.lang.reflect.Constructor con = tagClass.getConstructor(String.class, String.class, Position.class, Attributes.class);
		            return (XmlTag)con.newInstance(qname, namespaceURI, pos, attrs);
		        }
		        catch (Exception e) {
		             // ignored, just return an XmlTag if this fails   
		             e.printStackTrace();
		        }
		    }
		    return new XmlTag(qname, namespaceURI, pos, attrs);   
		}

		//{{{ endElement() method
		public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
			) throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			if(!buffer.getPath().equals(xml.PathUtilities.urlToPath(loc.getSystemId())))
				return;
                  
			// what do we do in this case?
			if(loc.getLineNumber() == -1)
				return;

			buffer.readLock();

			try
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)
					currentNodeStack.peek();
				XmlTag tag = (XmlTag)node.getUserObject();
				if(tag.getName().equals(qName))
				{
					int line = Math.min(buffer.getLineCount() - 1,
						loc.getLineNumber() - 1);
					int column = loc.getColumnNumber() - 1;
					int offset = Math.min(buffer.getLength(),
						buffer.getLineStartOffset(line)
						+ column);

					tag.setEnd(buffer.createPosition(offset));
					tag.empty = empty;
					currentNodeStack.pop();
				}
			}
			finally
			{
				buffer.readUnlock();
			}

			empty = false;
		} //}}}

		//{{{ characters() method
		public void characters (char ch[], int start, int length)
			throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			empty = false;
			// currentNodeStack is empty for compound documents in the "root" document
			// where text appears in nodes that are not kept in the Sidekick tree
			// see test_data/compound_documents
			if(!currentNodeStack.isEmpty()){
				DefaultMutableTreeNode node = currentNodeStack.peek();
				XmlTag tag = (XmlTag)node.getUserObject();
				if (tag.canAddCharacters()) {
					 char[] chBis = new char[length];
					 System.arraycopy(ch,start,chBis,0,length);
					 tag.addCharacters(chBis);   
				}
			}
		} //}}}

		//{{{ DTD related methods
		
		//{{{ startDTD() method
		/**
		 * cache CompletionInfo for DTD (doesn't work for composite DTDs if a part changes)
		 */
		@Override
		public void startDTD(String name, String publicId, String systemId) throws SAXException
		{
			if(DEBUG_DTD)Log.log(Log.DEBUG,Handler.class,"startDTD("+name+","+publicId+","+systemId+")");
			if(publicId == null && systemId == null)
			{
				// DTD in the document itself, don't cache it as it will parsed again anyway
				if(DEBUG_CACHE)Log.log(Log.DEBUG,Handler.class,"DTD in the document, not caching");
			}
			else
			{
				try
				{
					String realLocation = Resolver.instance().resolveEntityToPath(null, publicId, buffer.getPath(), systemId);
					CacheEntry ce = Cache.instance().get(realLocation,COMPLETION_INFO_CACHE_ENTRY);
					if(ce == null)
					{
						if(DEBUG_CACHE)Log.log(Log.DEBUG,Handler.class,"CompletionInfo not in cache for DTD, caching");
						ce = Cache.instance().put(realLocation,COMPLETION_INFO_CACHE_ENTRY,data.getNoNamespaceCompletionInfo());
						ce.getRequestingBuffers().add(buffer);
					}
					else
					{
						if(DEBUG_CACHE)Log.log(Log.DEBUG,Handler.class,"CompletionInfo in cache for DTD, reusing");
						ce.getRequestingBuffers().add(buffer);
						data.setCompletionInfo("", (CompletionInfo)ce.getCachedItem());
					}
				}
				catch(IOException ioe)
				{
					throw new SAXException("error resolving DTD path",ioe);
				}
			}
		}
		//}}}
		
		//{{{ elementDecl() method
		public void elementDecl(String name, String model)
		{
			if(DEBUG_DTD)Log.log(Log.DEBUG,XercesParserImpl.class,"elementDecl("+name+","+model+")");
			ElementDecl element = data.getElementDecl(name,0);
			if(element == null)
			{
				CompletionInfo info = data.getNoNamespaceCompletionInfo();
				element = new ElementDecl(info,name,model);
				info.addElement(element);
			}
			else
				element.setContent(model);
		} //}}}

		//{{{ attributeDecl() method
		public void attributeDecl(String eName, String aName,
			String type, String valueDefault, String value)
		{
			if(DEBUG_DTD)Log.log(Log.DEBUG,XercesParserImpl.class,"attributeDecl("+eName+","+aName+","+type+","+valueDefault+","+value+")");
			ElementDecl element = data.getElementDecl(eName,0);
			if(element == null)
			{
				CompletionInfo info = data.getNoNamespaceCompletionInfo();
				element = new ElementDecl(info,eName,null);
				info.addElement(element);
			}

			// as per the XML spec
			if(element.getAttribute(aName) != null)
				return;

			ArrayList<String> values;

			if(type.startsWith("("))
			{
				values = new ArrayList<String>();

				StringTokenizer st = new StringTokenizer(
					type.substring(1,type.length() - 1),"|");
				while(st.hasMoreTokens())
				{
					values.add(st.nextToken());
				}
			}
			else
				values = null;

			boolean required = "#REQUIRED".equals(valueDefault);

			element.addAttribute(new ElementDecl.AttributeDecl(
				aName,null,value,values,type,required));
		} //}}}

		//{{{ internalEntityDecl() method
		public void internalEntityDecl(String name, String value)
		{
			// this is a bit of a hack
			if(name.startsWith("%"))
				return;

			data.getNoNamespaceCompletionInfo()
				.addEntity(EntityDecl.INTERNAL,name,value);
		} //}}}

		//{{{ externalEntityDecl() method
		public void externalEntityDecl(String name, String publicId,
			String systemId)
		{
			if(name.startsWith("%"))
				return;

			data.getNoNamespaceCompletionInfo()
				.addEntity(EntityDecl.EXTERNAL,name,
				publicId,systemId);
		} //}}}
		//}}}
		
		//{{{ findTagStart() method
		private int findTagStart(int offset)
		{
			for(int i = offset; i >= 0; i--)
			{
				if(text.charAt(i) == '<')
					return i;
			}

			return 0;
		} //}}}

		// {{{
		/* (non-Javadoc)
		 * @see org.xml.sax.ext.DefaultHandler2#resolveEntity(java.lang.String, java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
		{
			if(DEBUG_RESOLVER)Log.log(Log.DEBUG,XercesParserImpl.class,"simple resolveEnt("+publicId+","+systemId+")");
			return resolveEntity(null, publicId, null, systemId);
		}// }}}
	}// }}}

	//{{{ FastHandler class
	class FastHandler extends DefaultHandler2 implements DeclHandler, ErrorHandler
	{
	    // {{{ members
		Buffer buffer;

		XmlParsedData data;

		HashMap<String, String> activePrefixes;
		Stack<DefaultMutableTreeNode> currentNodeStack;
		Locator loc;
		boolean empty;
		// }}}
		// {{{ FastHandler constructor
		FastHandler(Buffer buffer,XmlParsedData data)
		{
			this.buffer = buffer;
			this.data = data;
			this.activePrefixes = new HashMap<String, String>();
			this.currentNodeStack = new Stack<DefaultMutableTreeNode>();
		} // }}}

		//{{{ setDocumentLocator() method
		public void setDocumentLocator(Locator locator)
		{
			loc = locator;
		} //}}}

		//{{{ resolveEntity() method
		/**
		 * If you do this:
		 * reader.setProperty("use-entity-resolver2", true)
		 * Then this method should be called.
		 */
		public InputSource resolveEntity (String name, String publicId, String baseURI, String systemId)
			throws SAXException, java.io.IOException {

			if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"resolveEntity("+name+","+publicId+","+baseURI+","+systemId+")");

			InputSource source = null;

			try {
				source = Resolver.instance().resolveEntity(name, publicId, baseURI, systemId);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			if(source == null)
			{
				Log.log(Log.ERROR,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " cannot be resolved");
				// TODO: not sure whether it's the best thing to do :
				// it prints a cryptic "premature end of file"
				// error message
			}
			else
			{
				if(DEBUG_RESOLVER)Log.log(Log.DEBUG,this,"PUBLIC=" + publicId
					+ ", SYSTEM=" + systemId
					+ " resolved to " + source.getSystemId());
			}
			return source;
		} //}}}

		//{{{ startPrefixMapping() method
		public void startPrefixMapping(String prefix, String uri)
		{
			activePrefixes.put(prefix,uri);
		} //}}}

		//{{{ endPrefixMapping() method
		public void endPrefixMapping(String prefix)
		{
		} //}}}

		//{{{ startElement() method
		public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException
		{
			if(stopped)
				throw new StoppedException();
		} //}}}

		//{{{ endElement() method
		public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
			) throws SAXException
		{
			if(stopped)
				throw new StoppedException();

		} //}}}

		//{{{ characters() method
		public void characters (char ch[], int start, int length)
			throws SAXException
		{
			if(stopped)
				throw new StoppedException();

			empty = false;
		} //}}}

		//{{{ findTagStart() method
		// TODO: this isn't used, remove?
		private int findTagStart(int offset)
		{
			/*for(int i = offset; i >= 0; i--)
			{
				if(text.charAt(i) == '<')
					return i;
			}
			*/
			return 0;
		} //}}}

		// {{{
		/* (non-Javadoc)
		 * @see org.xml.sax.ext.DefaultHandler2#resolveEntity(java.lang.String, java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
		{
			throw new UnsupportedOperationException("please use resolveEntity(#4)");
		}// }}}
	}// }}}

	//{{{ StoppedException class
	static class StoppedException extends SAXException
	{
		StoppedException()
		{
			super("Parsing stopped");
		}
	} //}}}
} // }}}
