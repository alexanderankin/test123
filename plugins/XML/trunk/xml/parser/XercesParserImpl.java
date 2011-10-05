package xml.parser;

// {{{ imports
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import sidekick.IAsset;
import sidekick.SideKickParsedData;
import xml.CharSequenceReader;
import xml.AntXmlParsedData;
import xml.XmlParsedData;
import xml.XmlPlugin;
import xml.SchemaMappingManager;
import xml.completion.CompletionInfo;
import xml.gui.XmlModeToolBar;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import static xml.Debug.*;
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
						Log.log(Log.ERROR,this,ioe);
					}
					catch(URISyntaxException ioe)
					{
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
		//}}}
		// {{{ and parse again to get the SideKick tree (required to get the xi:include elements in the tree)
		
		ConstructTreeHandler treeHandler = new ConstructTreeHandler(this, buffer, text, errorHandler, data, resolver);
		reader = null;
		try
		{
			// One has to explicitely require the parser from XercesPlugin, otherwise
			// one gets the crimson version bundled in the JRE and the rest fails
			// miserably (see Plugin Bug #2950392)
			reader = new org.apache.xerces.parsers.SAXParser();
			
			// no validation: it has already been done once
			reader.setFeature("http://xml.org/sax/features/validation",false);
			// turn on/off namespace support.
			// For some legacy documents, namespaces must be disabled
			reader.setFeature("http://xml.org/sax/features/namespaces",
				!buffer.getBooleanProperty("xml.namespaces.disable"));
			// always use EntityResolver2 so that built-in DTDs can be found
			reader.setFeature("http://xml.org/sax/features/use-entity-resolver2",
				true);
			
			reader.setProperty("http://apache.org/xml/properties/internal/grammar-pool",
					new CachedGrammarPool(buffer));

			// XInclude support disabled: we want the xi:include elements to show up in the tree
			reader.setFeature("http://apache.org/xml/features/xinclude",false);
			
			reader.setContentHandler(treeHandler);
			reader.setEntityResolver(resolver);
			
		}
		catch(SAXException se)
		{
			se.printStackTrace();
			Log.log(Log.ERROR,this,se);
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
		catch(Exception e)
		{
			Log.log(Log.ERROR,this,"error upon snd reparse",e);
		}
		
		// }}}
		
		
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
		StoppedException()
		{
			super("Parsing stopped");
		}
	} //}}}
} // }}}
