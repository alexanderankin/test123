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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gjt.sp.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Utilities for performing XSL Transformations
 *
 *@author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XSLTUtilities {

  static final String TRANSFORMER_FACTORY = "javax.xml.transform.TransformerFactory";
  static final String SAX_PARSER_FACTORY = "javax.xml.parsers.SAXParserFactory";
  static final String SAX_DRIVER = "org.xml.sax.driver";

  private static String indentAmount = "2";


  private XSLTUtilities() {
  }


  public static void setXmlSystemProperties(String transformerFactory, String saxParserFactory, String saxDriver) {
    System.setProperty(TRANSFORMER_FACTORY, transformerFactory);
    System.setProperty(SAX_PARSER_FACTORY, saxParserFactory);
    System.setProperty(SAX_DRIVER, saxDriver);
    logXmlSystemProperties();
  }


  public static void logXmlSystemProperties() {
    Log.log(Log.DEBUG, XSLTPlugin.class, TRANSFORMER_FACTORY + "=" + System.getProperty(TRANSFORMER_FACTORY));
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
   * @param resultFile           name of the file that final result is written to
   * @exception Exception        if a problem occurs during the transformation
   */
  public static void transform(InputSource inputFile, Object[] stylesheets, Map stylesheetParameters, File resultFile) throws Exception {
    logXmlSystemProperties();
    TransformerHandler[] handlers = getTransformerHandlers(stylesheets, stylesheetParameters);

    FileWriter writer = new FileWriter(resultFile);
    Result result = new StreamResult(writer);
        
    int lastIndex = handlers.length - 1;
    handlers[lastIndex].setResult(result);
    
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(handlers[0]);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handlers[0]);

    //EntityResolver entityResolver = new EntityResolverImpl(inputFile);
    //reader.setEntityResolver(entityResolver);

    reader.parse(inputFile);
   
  }


  private static TransformerHandler[] getTransformerHandlers(Object[] stylesheets, Map stylesheetParameters) throws FileNotFoundException, TransformerConfigurationException {
    SAXTransformerFactory saxFactory = null;

    try {
      saxFactory = (SAXTransformerFactory)TransformerFactory.newInstance();
    } catch(ClassCastException exception) {
      Log.log(Log.ERROR, XSLTUtilities.class, "class cast exception " + exception.toString());
      throw new TransformerConfigurationException(XSLTPlugin.getOldXalanJarMessage());
    }

    TransformerHandler[] handlers = new TransformerHandler[stylesheets.length];

    for(int i = 0; i < stylesheets.length; i++) {
      Source stylesheetSource = getSource((String)stylesheets[i]);
      handlers[i] = saxFactory.newTransformerHandler(stylesheetSource);
      

      Transformer transformer = handlers[i].getTransformer();

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


  private static Source getSource(String fileName) throws FileNotFoundException {
    Source source = new StreamSource(new FileReader(fileName));
    source.setSystemId(fileName);
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

}
