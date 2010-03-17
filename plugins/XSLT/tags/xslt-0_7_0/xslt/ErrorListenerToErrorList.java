/*
 * ErrorListenerToErrorList.java - redirects XSLT errors to ErrorList
 *
 * Copyright 2010 Eric Le Lay
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package xslt;

import java.net.URL;
import java.io.File;
import org.gjt.sp.util.Log;

import xml.PathUtilities;

import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;

import org.xml.sax.SAXParseException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.ErrorListener;


/**
 * Redirects XSLT errors to ErrorList
 *
 * @author Eric Le Lay
 */
public class ErrorListenerToErrorList implements ErrorListener{
		
	/** defaults to the stylesheet if there is no location for the error */
	private String stylesheetPath;
	
	private boolean hasSignaledError = false;;
	
	public ErrorListenerToErrorList(String stylesheetPath){
		this.stylesheetPath = stylesheetPath;
	}
	
	
	public void setCurrentStylesheet(String stylesheetPath){
		this.stylesheetPath = stylesheetPath;
	}
	
	public void error(TransformerException exception) throws TransformerException{
		Log.log(Log.ERROR, this, "Error:"+exception.toString());
		sendError(exception, ErrorSource.ERROR);
		hasSignaledError = true;
		// shouldn't continue
		throw exception;
	}

	public void fatalError(TransformerException exception) throws TransformerException{
		if(hasSignaledError){
			Log.log(Log.DEBUG,this,"skipping as fatalError: "+exception);
		}else{
			hasSignaledError = true;
			Log.log(Log.ERROR, this, "fatalError:"+exception.toString());
			sendError(exception, ErrorSource.ERROR);
		}
		// shouldn't continue
		throw exception;
	}
	
	public void warning(TransformerException exception) {
		Log.log(Log.DEBUG, this,"warning :"+exception.toString());
		sendError(exception, ErrorSource.WARNING);
	}
	
	public void sendSAXError(SAXParseException exception) {
		Log.log(Log.DEBUG, this,"sendSAXError :"+exception.toString());
		int line=exception.getLineNumber()-1;
		int col=exception.getColumnNumber()-1;
		if(line < 0) line=0;
		if(col < 0) col=0;
		// TODO: Handle VFS URLs
		String systemId=exception.getSystemId();
		String path;
		if(systemId == null){
			Log.log(Log.DEBUG,this,"NULL systemId");
			path = PathUtilities.urlToPath(stylesheetPath);
		}else{
			path = PathUtilities.urlToPath(systemId);
		}
		path = PathUtilities.urlToPath(stylesheetPath);
		XSLTPlugin.getErrorSource().addError(new DefaultErrorSource.DefaultError(XSLTPlugin.getErrorSource(),
		ErrorSource.ERROR,path,line,0,col,
		"(SAX error) "+exception.getMessage()));
	}

	private void sendError(TransformerException exception, int level) {
		SourceLocator locator=exception.getLocator();
		String path;
		if(locator != null) {
			int line=locator.getLineNumber()-1;
			int col=locator.getColumnNumber()-1;
			if(line < 0) line=0;
			if(col < 0) col=0;
			// TODO: Handle VFS URLs
			String systemId=locator.getSystemId();
			if(systemId == null){
				Log.log(Log.DEBUG,this,"NULL systemId");
				path = PathUtilities.urlToPath(stylesheetPath);
			}else{
				path = PathUtilities.urlToPath(systemId);
			}
			XSLTPlugin.getErrorSource().addError(new DefaultErrorSource.DefaultError(XSLTPlugin.getErrorSource(),
			level,path,line,0,col,
			"(XSLT error) "+exception.getMessage()));
		} else {
			path = PathUtilities.urlToPath(stylesheetPath);
			XSLTPlugin.getErrorSource().addError(new DefaultErrorSource.DefaultError(XSLTPlugin.getErrorSource(),
			level,path,1,0,0,
			"(XSLT error) "+exception.getMessage()));
		}
	}
}
