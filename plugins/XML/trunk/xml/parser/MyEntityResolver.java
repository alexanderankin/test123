/*
 * MyEntityResolver.java - EntityResolver2 using xml.Resolver
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

import static xml.Debug.DEBUG_RESOLVER;

import java.io.IOException;
import java.io.StringReader;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import xml.Resolver;
import errorlist.ErrorSource;

//{{{ MyEntityResolver class
/**
 * EntityResolver2 backed by xml.Resolver for built-in, cached, etc. resources.
 * returns empty String on last resort, instead of null !
 * 
 * setDocumentLocator() must be called before any error occurs.
 * */
class MyEntityResolver implements EntityResolver2
{

	// {{{ members
	Buffer buffer;

	ErrorListErrorHandler errorHandler;
	Locator loc;

	// }}}
	// {{{ Handler constructor
	MyEntityResolver(Buffer buffer, ErrorListErrorHandler errorHandler)
	{
		this.buffer = buffer;
		this.errorHandler = errorHandler;
	} // }}}

	
	//{{{ setDocumentLocator() method
	public void setDocumentLocator(Locator locator)
	{
		loc = locator;
	} //}}}

	
    public InputSource getExternalSubset(String name, String baseURI)
            throws SAXException, IOException {
        return null;
    }

	
	
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