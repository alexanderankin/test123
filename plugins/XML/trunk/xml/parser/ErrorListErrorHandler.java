/*
 * ErrorListErrorHandler.java
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2010 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */
package xml.parser;

// {{{ imports
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import org.gjt.sp.util.Log;

import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;

import static xml.Debug.*;
import xml.PathUtilities;
// }}}

// {{{ class ErrorListErrorHandler
/**
 * An ErrorHandler redirecting errors to ErrorList
 *
 * @author kerik-sf
 * @version $Id$
 */
public class ErrorListErrorHandler implements ErrorHandler
{
	private DefaultErrorSource errorSource;
	
	private String defaultPath;
	
	// {{{ ErrorListErrorHandler constructor
	public ErrorListErrorHandler(DefaultErrorSource errorSource, String defaultPath)
	{
		this.errorSource = errorSource;
		this.defaultPath = defaultPath;
	} // }}}
	
	// {{{ addError() method
	private void addError(int type, SAXParseException exception)
	{
		Log.log(Log.DEBUG, this,"addError :"+exception.toString());
		int line=exception.getLineNumber()-1;
		int col=exception.getColumnNumber()-1;
		if(line < 0) line=0;
		if(col < 0) col=0;
		// TODO: Handle VFS URLs
		String systemId=exception.getSystemId();
		String path;
		if(systemId == null){
			Log.log(Log.DEBUG,this,"NULL systemId");
			path = PathUtilities.urlToPath(defaultPath);
		}else{
			path = PathUtilities.urlToPath(systemId);
		}
		errorSource.addError(type,path,line,0,col,exception.getMessage());
	}// }}}
	
	//{{{ error() method
	public void error(SAXParseException spe)
	{
		addError(ErrorSource.ERROR,spe);
	} //}}}
	
	//{{{ warning() method
	public void warning(SAXParseException spe)
	{
		addError(ErrorSource.WARNING,spe);
	} //}}}
	
	//{{{ fatalError() method
	public void fatalError(SAXParseException spe)
	throws SAXParseException
	{
		addError(ErrorSource.ERROR,spe);
	} //}}}
	
	//{{{ get/set defaultPath
	public void setDefaultPath(String defaultPath){
		this.defaultPath = defaultPath;
	}
	
	public String getDefaultPath(){
		return defaultPath;
	}
	//}}}
	
	//{{{ getErrorSource() method
	public DefaultErrorSource getErrorSource()
	{
		return errorSource;
	}
	//}}}
}//}}}
