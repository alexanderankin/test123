/*
 * XSLTUtilities.java - Utilities for performing XSL Transformations
 *
 * Copyright (c) 2002, 2003 Robert McKinnon
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

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.URIResolver;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Utilities for performing XSL Transformations
 *
 *@author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XSLTUtilities {

  static final String SAX_PARSER_FACTORY = "javax.xml.parsers.SAXParserFactory";
  static final String SAX_DRIVER = "org.xml.sax.driver";
  
  static final String XSLT_FACTORY_PROP = "xslt.factory";
  
  private static String indentAmount = "2";


  private XSLTUtilities() {
  }


  /*
   * FIXME : shouldn't let this plugin override these global properties !
   */
  public static void setXmlSystemProperties(String saxParserFactory, String saxDriver) {
    System.setProperty(SAX_PARSER_FACTORY, saxParserFactory);
    System.setProperty(SAX_DRIVER, saxDriver);
    logXmlSystemProperties();
  }


  public static void logXmlSystemProperties() {
    Log.log(Log.DEBUG, XSLTPlugin.class, SAX_PARSER_FACTORY + "=" + System.getProperty(SAX_PARSER_FACTORY));
    Log.log(Log.DEBUG, XSLTPlugin.class, SAX_DRIVER + "=" + System.getProperty(SAX_DRIVER));
  }


  public static void setIndentAmount(String indentAmount) {
    XSLTUtilities.indentAmount = indentAmount;
  }


  /**
   * Transforms input file by applying the supplied stylesheets, writing the result to the given result file.
   *
   * @param inputFile            name of file to be transformed
   * @param stylesheets          ordered array of names of stylesheets to be applied
   * @param stylesheetParameters map of stylesheet parameters
   * @param result           	 where to output the result
   * @param errorListener        where the errors will show up (may not be null)
   * @exception Exception        if a problem occurs during the transformation
   */
  public static void transform(InputSource inputFile, Object[] stylesheets, Map stylesheetParameters, Result result, ErrorListenerToErrorList errorListener) throws Exception {
  	Log.log(Log.DEBUG,XSLTUtilities.class,"transform("
  		+"src="+inputFile.getSystemId()
  		+",stylesheets="+Arrays.asList(stylesheets)
  		+",params="+stylesheetParameters
  		+",res="+result.getSystemId()+")");
    logXmlSystemProperties();
    TransformerHandler[] handlers = getTransformerHandlers(stylesheets, stylesheetParameters, errorListener);
        
    int lastIndex = handlers.length - 1;
    handlers[lastIndex].setResult(result);
    
    XMLReader reader = XMLReaderFactory.createXMLReader();
    // allow URLs relative to the input document to be resolved
    handlers[0].setSystemId(inputFile.getSystemId());
    reader.setContentHandler(handlers[0]);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handlers[0]);

    // let built-in DTDs be resolved (see test_data/resolver for an example) 
    EntityResolver entityResolver = xml.Resolver.instance();
    reader.setEntityResolver(entityResolver);

    reader.parse(inputFile);
   
  }


  private static TransformerHandler[] getTransformerHandlers(Object[] stylesheets,
  	  Map stylesheetParameters, ErrorListenerToErrorList errorListener)
  		throws IOException, TransformerConfigurationException,SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    SAXTransformerFactory saxFactory = null;

    String factoryClass = jEdit.getProperty(XSLT_FACTORY_PROP);
    try {
      saxFactory = (SAXTransformerFactory)Class.forName(factoryClass).newInstance();
    } catch(ClassCastException exception) {
    	throw new TransformerConfigurationException(exception);
    } catch(ClassNotFoundException cnfe){
    	throw new TransformerConfigurationException("class not found:"+factoryClass);
    }
    
    saxFactory.setErrorListener(errorListener);
    saxFactory.setURIResolver(new URIResolverImpl());
    
    TransformerHandler[] handlers = new TransformerHandler[stylesheets.length];

    for(int i = 0; i < stylesheets.length; i++) {
      final Source stylesheetSource = getSource((String)stylesheets[i]);
      
      errorListener.setCurrentStylesheet(stylesheetSource.getSystemId());
      
      handlers[i] = saxFactory.newTransformerHandler(stylesheetSource);
      
      if(handlers[i]==null){
      	  throw new TransformerConfigurationException("error creating "+stylesheets[i],new SourceLocator(){
      	  		  public int getColumnNumber(){ return -1;}
      	  		  public int getLineNumber() { return -1; }
      	  		  public String getPublicId(){ return null;}
      	  		  public String getSystemId(){return stylesheetSource.getSystemId();} 
      	  });
      }
      Transformer transformer = handlers[i].getTransformer();

	  transformer.setErrorListener(errorListener);

      if(i == 0) {
        setParameters(transformer, stylesheetParameters);
      } else {
        handlers[i - 1].getTransformer().setOutputProperty(OutputKeys.INDENT, "no");
        handlers[i - 1].setResult(new SAXResult(handlers[i]));
      }

      if(i == stylesheets.length - 1) {
        String indentAmountProperty = XsltOutputProperties.getInstance().getKeyIndentAmount();
        transformer.setOutputProperty(indentAmountProperty, XSLTUtilities.indentAmount);
      }
    }

    return handlers;
  }

  public static void compileStylesheet(String stylesheet, ErrorListenerToErrorList errorListener)
  		throws IOException, TransformerConfigurationException,SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException
  {
    SAXTransformerFactory saxFactory = null;

    String factoryClass = jEdit.getProperty(XSLT_FACTORY_PROP);
    try {
      saxFactory = (SAXTransformerFactory)Class.forName(factoryClass).newInstance();
    } catch(ClassCastException exception) {
    	throw new TransformerConfigurationException(exception);
    } catch(ClassNotFoundException cnfe){
    	throw new TransformerConfigurationException("class not found:"+factoryClass);
    }
    
    saxFactory.setErrorListener(errorListener);
    saxFactory.setURIResolver(new URIResolverImpl());
    
    final Source stylesheetSource = getSource((String)stylesheet);
  
    errorListener.setCurrentStylesheet(stylesheetSource.getSystemId());
      
    saxFactory.newTransformer(stylesheetSource);
  }

  private static Source getSource(String fileName) throws org.xml.sax.SAXException,IOException {
    Source source = new SAXSource(xml.Resolver.instance().resolveEntity(/*publicId=*/null, fileName));
    System.out.println("source="+source.getSystemId());
    return source;
  }


  private static void setParameters(Transformer transformer, Map parameterMap) {
    Iterator iterator = parameterMap.keySet().iterator();

    while(iterator.hasNext()) {
      String name = (String)iterator.next();
      String value = (String)parameterMap.get(name);
      transformer.setParameter(name, value);
    }
  }


  public static String removeIn(String sourceString, char character) {
    StringBuffer resultBuffer = new StringBuffer();

    for(int i = 0; i < sourceString.length(); i++) {
      if(sourceString.charAt(i) != character) {
        resultBuffer.append(sourceString.charAt(i));
      }
    }

    return resultBuffer.toString();
  }
  
  public static int getXSLTProcessorVersion() {
  	  
  	  // TODO : could use <xsl:value-of select="system-property('xsl:version')"/>,
  	  //        but it seems overkill just to select the template in three-way mode
  	  String factoryClass = jEdit.getProperty(XSLT_FACTORY_PROP);
  	  
  	  return "net.sf.saxon.TransformerFactoryImpl".equals(factoryClass) ? 2 : 1;
  }

  public static class URIResolverImpl implements URIResolver{
  	   public Source resolve(String href, String base)
  	   	throws TransformerException
  	   {
  	   	   Log.log(Log.DEBUG,this,"resolve(href="+href+",base="+base+")");
  	   	   if("".equals(href)) {
  	   	   	   // they are refering to the stylesheet,
  	   	   	   // return null and let the processor return the stylesheet
  	   	   	   return null;
  	   	   } else {
			   try{
				   return new SAXSource(xml.Resolver.instance().resolveEntity(
					   /*name=*/null,
					   /*publicId=*/null,
					   base,
					   href));
			   }catch(Exception e){
				   throw new TransformerException("error resolving {"+base+","+href+"}",e);
			   }
		   }
  	   }
  }
}
