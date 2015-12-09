package xml.parser;

// {{{ imports
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import sidekick.IAsset;
import sidekick.SideKickParsedData;
import xml.CharSequenceReader;
import xml.AntXmlParsedData;
import xml.XmlParsedData;
import xml.SchemaMappingManager;
import xml.completion.CompletionInfo;
import xml.gui.XmlModeToolBar;
import xml.parser.MyEntityResolver.IOExceptionWithLocation;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
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
	/**
	 * a buffer read lock is hold arround parse()
	 */
	public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource)
	{
		long start = System.currentTimeMillis();
		Log.log(Log.NOTICE,XercesParserImpl.class,"parsing started @"+start);
		stopped = false;
		CharSequence text;

		text = buffer.getSegment(0,buffer.getLength());

		XmlParsedData data = createXmlParsedData(buffer.getName(), buffer.getMode().toString(), false);

		if(text.length() == 0)return data;


		ErrorListErrorHandler errorHandler = new ErrorListErrorHandler(
				 errorSource
				,buffer.getPath()
			);

		SchemaMapping mapping;
		if(SchemaMappingManager.isSchemaMappingEnabled(buffer))
		{
			mapping = SchemaMappingManager.getSchemaMappingForBuffer(buffer, errorHandler);
		}
		else
		{
			mapping = null;
		}

		MyEntityResolver resolver = new MyEntityResolver(buffer, errorHandler);

		// {{{ parse one time to get CompletionInfo, Ids, and any error found by Xerces
		GrabIdsAndCompletionInfoHandler handler = new GrabIdsAndCompletionInfoHandler(this, buffer,errorHandler,data, resolver);


		XMLReader reader = null;
		SchemaAutoLoader schemaLoader = null;
		try
		{
			// One has to explicitely require the parser from XercesPlugin, otherwise
			// one gets the crimson version bundled in the JRE and the rest fails
			// miserably (see Plugin Bug #2950392)
			// using EntityMgrFixerConfiguration until XERCESJ-1205 is fixed (see Plugin bug #3393297)
			reader = new org.apache.xerces.parsers.SAXParser(new EntityMgrFixerConfiguration(null, new CachedGrammarPool(buffer)));

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

			schemaLoader = new SchemaAutoLoader(reader,mapping,buffer);

			schemaLoader.setErrorHandler(errorHandler);
			schemaLoader.setContentHandler(handler);
			schemaLoader.setEntityResolver(resolver);

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
						Log.log(Log.WARNING,this,"I/O error loading forced schema: "+ioe.getClass()+": "+ioe.getMessage());
					}
					catch(URISyntaxException e)
					{
						Log.log(Log.WARNING,this,"forced schema URL is invalid: "+e.getMessage());
					}
				}
			}
		}
		catch(SAXException se)
		{
			// not likely : messed jars or sthing
			Log.log(Log.ERROR,this,"unexpected error preparing XML parser, please report",se);
		}

		CompletionInfo info = CompletionInfo.getCompletionInfoForBuffer(
			buffer);
		if(info != null)
			data.setCompletionInfo("",info);

		// don't log the same error twice when reparsing to construct the sidekick tree
		Exception errorParsing = null;

		InputSource source = new InputSource();

		String rootDocument = buffer.getStringProperty("xml.root");
		if(rootDocument != null)
		{
			Log.log(Log.NOTICE,this,"rootDocument specified; "
				+ "parsing " + rootDocument);
			rootDocument = MiscUtilities.constructPath(
				MiscUtilities.getParentOfPath(buffer.getPath()), rootDocument);
			// using an URL as systemId (see else block for details)
			source.setSystemId(xml.PathUtilities.pathToURL(rootDocument));
		}
		else
		{
			source.setCharacterStream(new CharSequenceReader(text));
			// must set the systemId to an URL and not a path
			// otherwise, get errors when opening a DTD specified as a relative path
			// somehow, xerces doesn't call File.toURI.toURL and the URL
			// is incorrect : file://server/share instead of file://///server/share
			// and the DTD can't be found
			source.setSystemId(xml.PathUtilities.pathToURL(buffer.getPath()));
		}

		try
		{
			reader.parse(source);
		}
		catch(StoppedException e) // interrupted parsing: don't parse a second time
		{
			errorParsing = e;
		}
		catch(IOExceptionWithLocation ioe)
		{
			// same as IOException, but with correct line number
			String msg = "I/O error while parsing: "+ioe.getMessage()+", caused by "+ioe.getCause().getClass().getName()+": "+ioe.getCause().getMessage();
			//Log.log(Log.WARNING,this,msg);
			errorSource.addError(ErrorSource.ERROR, ioe.path, ioe.line, 0, 0,
				msg);
			errorParsing = ioe;
		}
		catch(IOException ioe)
		{
			// Log.log(Log.WARNING,this,"I/O error while parsing: "+ioe.getClass().getName()+": "+ioe.getMessage());
			errorSource.addError(ErrorSource.ERROR, buffer.getPath(), 0, 0, 0,
				ioe.getClass()+": "+ioe.getMessage());
			errorParsing = ioe;
		}
		catch(SAXParseException spe)
		{
			// don't print it: already handled
			errorParsing = spe;
		}
		catch(SAXException se)
		{
			String msg = "SAX exception while parsing";
			Throwable t = se.getException();
			if(msg != null){
				msg+=": "+se.getMessage();
			}
			if(t!=null){
				msg+=" caused by "+t;
			}
//			Log.log(Log.WARNING, this, msg);
			errorSource.addError(ErrorSource.ERROR,buffer.getPath(),
				0,0,0,msg);
			errorParsing = se;
		}
		finally
		{
			//set this property for the xml-open-schema action to work
			String url = null;

			if(schemaLoader != null && schemaLoader.getSchemaURL()!=null)
			{
				url = schemaLoader.getSchemaURL();
			}
			else if(handler.xsdSchemaURLs != null && !handler.xsdSchemaURLs.isEmpty())
			{
				url = handler.xsdSchemaURLs.get(0);
			}

			if(url !=null){
				buffer.setStringProperty(SchemaMappingManager.BUFFER_AUTO_SCHEMA_PROP,
					url);
			}
		}
		//}}}

		// {{{ and parse again to get the SideKick tree (required to get the xi:include elements in the tree)
		if(!(errorParsing instanceof StoppedException)){

			// don't print a warning if rootDocument == null,
			// because it may be that we didn't see because there was an error before first element, parsing an empty XML document, etc..
			if(!handler.seenBuffer && rootDocument!=null){
				String msg = "Buffer is not reachable from root document. Check that you didn't forget to xi:include it.";
//				Log.log(Log.WARNING, this, msg);
				errorSource.addError(ErrorSource.WARNING,buffer.getPath(),
					0,0,0,msg);
			}

			ConstructTreeHandler treeHandler = new ConstructTreeHandler(this, buffer, text, errorHandler, data, resolver);
			reader = null;
			try
			{
				// One has to explicitely require the parser from XercesPlugin, otherwise
				// one gets the crimson version bundled in the JRE and the rest fails
				// miserably (see Plugin Bug #2950392)
				// using EntityMgrFixerConfiguration until XERCESJ-1205 is fixed (see Plugin bug #3393297)
				reader = new org.apache.xerces.parsers.SAXParser(new EntityMgrFixerConfiguration(null, new CachedGrammarPool(buffer)));

				// no validation: it has already been done once
				reader.setFeature("http://xml.org/sax/features/validation",false);
				// turn on/off namespace support.
				// For some legacy documents, namespaces must be disabled
				reader.setFeature("http://xml.org/sax/features/namespaces",
					!buffer.getBooleanProperty("xml.namespaces.disable"));
				// always use EntityResolver2 so that built-in DTDs can be found
				reader.setFeature("http://xml.org/sax/features/use-entity-resolver2",
					true);

				// XInclude support disabled: we want the xi:include elements to show up in the tree
				reader.setFeature("http://apache.org/xml/features/xinclude",false);

				reader.setContentHandler(treeHandler);
				reader.setEntityResolver(resolver);

				if(handler.seenBuffer || rootDocument == null){
					// if already parsed, set a no-op ErrorHandler
					reader.setErrorHandler(new DefaultHandler());
				}else{
					// report errors and warnings correctly if not referenced from root document
					// (this may be temporary, whatever...)
					reader.setErrorHandler(errorHandler);
				}


			}
			catch(SAXException se)
			{
				// not likely : messed jars or sthing
				Log.log(Log.ERROR,this,"error preparing to parse 2nd pass), please report !",se);
			}

			source = new InputSource();

			source.setCharacterStream(new CharSequenceReader(text));
			// must set the systemId to an URL and not a path
			// otherwise, get errors when opening a DTD specified as a relative path
			// somehow, xerces doesn't call File.toURI.toURL and the URL
			// is incorrect : file://server/share instead of file://///server/share
			// and the DTD can't be found
			source.setSystemId(xml.PathUtilities.pathToURL(buffer.getPath()));

			try
			{
				reader.parse(source);
			}
			catch(StoppedException e) //NOPMD interrupted parsing
			{
			}
			catch(IOException e)
			{
				// don't repeat yourself
				if(errorParsing == null
						|| !e.getClass().equals(errorParsing.getClass())
						|| (!e.toString().equals(errorParsing.toString())))
				{
					Log.log(Log.ERROR,this,"I/O error upon snd reparse :"+e.getClass()+": "+e.getMessage());
				}
			}
			catch(SAXParseException se) // NOPMD: got it in the first parse or, if buffer not parsed because of xml.root, caught by ErrorListErrorHandler
			{
			}
			catch(SAXException se)
			{
				// don't repeat yourself
				if(errorParsing == null
						|| !se.getClass().equals(errorParsing.getClass())
						|| (!se.toString().equals(errorParsing.toString())))
				{
					String msg = "SAX exception while parsing (constructing sidekick tree)";
					Throwable t = se.getException();
					if(msg != null){
						msg+=": "+se.getMessage();
					}
					if(t!=null){
						msg+=" caused by "+t;
					}
					// Log.log(Log.WARNING, this, msg);
					errorSource.addError(ErrorSource.ERROR,buffer.getPath(),
						0,0,0,msg);
				}
			}
			// }}}
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
                Class<?> dataClass = Class.forName(dataClassName);
                Constructor<?> con = dataClass.getConstructor(String.class, Boolean.TYPE);
                return (XmlParsedData)con.newInstance(filename, html);
            }
            catch (Exception e) {
                 // ignored, just return an XmlParsedData if this fails
                 Log.log(Log.ERROR, this, "createXmlParsedData()", e);
            }
        }
        return new XmlParsedData(filename, html);
	}

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

	//{{{ StoppedException class
	static class StoppedException extends SAXException
	{
		private static final long serialVersionUID = 1L;

		StoppedException()
		{
			super("Parsing stopped");
		}
	} //}}}
} // }}}
