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

import org.apache.xalan.templates.OutputProperties;
import org.gjt.sp.util.Log;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * XSLTUtilities.java - Utilities for performing XSL Transformations
 *
 *@author Robert McKinnon
 */
public class XSLTUtilities {

  private static String indentAmount = "2";


  private XSLTUtilities() {
  }


  public static void setIndentAmount(String indentAmount) {
    XSLTUtilities.indentAmount = indentAmount;
  }


  /**
   * Transforms inputFile, by piping it through the supplied stylesheets.
   *
   *@param inputFile      name of file to be transformed
   *@param stylesheets    ordered array of names of stylesheets to be applied
   *@return               string containing result of the transformation
   *@exception Exception  if a problem occurs during the transformation
   */
  public static String transform(String inputFile, Object[] stylesheets, Map parameterMap) throws Exception {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    String resultString = null;

    if(transformerFactory.getFeature(SAXSource.FEATURE) && transformerFactory.getFeature(SAXResult.FEATURE)) {
      SAXTransformerFactory saxFactory = (SAXTransformerFactory)transformerFactory;
      resultString = saxTransform(saxFactory, inputFile, stylesheets, parameterMap);
    } else {
      for(int i = 0; i < stylesheets.length; i++) {
        Source inputSource;

        if(resultString == null) {
          inputSource = getSource(inputFile);
        } else {
          inputSource = getSourceFromString(resultString);
        }

        Source stylesheetSource = getSource((String)stylesheets[i]);
        boolean isLast = (i == stylesheets.length - 1);

        resultString = transform(transformerFactory, inputSource, stylesheetSource, isLast);
      }
    }

    return resultString;
  }


  /**
   * Transforms inputFile, by piping it through the supplied stylesheets.
   *
   *@param inputString    string containing input XML
   *@param xsltString     string containing stylesheet file
   *@return               string containing result of the transformation
   *@exception Exception  if a problem occurs during the transformation
   */
  public static String transform(String inputString, String xsltString) throws Exception {
    TransformerFactory factory = TransformerFactory.newInstance();

    return transform(factory, getSourceFromString(inputString), getSourceFromString(xsltString), true);
  }


  private static Source getSource(String fileName) throws FileNotFoundException {
    Source source = new StreamSource(new FileReader(fileName));
    source.setSystemId(fileName);
    return source;
  }


  static Source getSourceFromString(String string) {
    return new StreamSource(new StringReader(string));
  }


  private static String transform(TransformerFactory factory, Source inputSource,
                                  Source xsltSource, boolean indent) throws Exception {
    Templates templates = factory.newTemplates(xsltSource);
    Transformer transformer = templates.newTransformer();

    if(indent) {
      transformer.setOutputProperty(OutputProperties.S_KEY_INDENT_AMOUNT, XSLTUtilities.indentAmount);
    } else {
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
    }

    StringWriter writer = new StringWriter();
    Result result = new StreamResult(writer);

    transformer.transform(inputSource, result);
    String resultString = writer.toString();
    return removeIn(resultString, '\r'); //remove '\r' to temporarily fix a bug in the display of results in Windows
  }


  private static String saxTransform(SAXTransformerFactory saxFactory, String inputFile, Object[] stylesheets, Map parameterMap) throws Exception {
    TransformerHandler[] handlers = new TransformerHandler[stylesheets.length];

    for(int i = 0; i < stylesheets.length; i++) {
      Source stylesheetSource = getSource((String)stylesheets[i]);
      handlers[i] = saxFactory.newTransformerHandler(stylesheetSource);

      if(i == 0) {
        Transformer transformer = handlers[0].getTransformer();
        setParameters(transformer, parameterMap);
      } else {
        handlers[i - 1].getTransformer().setOutputProperty(OutputKeys.INDENT, "no");
        handlers[i - 1].setResult(new SAXResult(handlers[i]));
      }
    }

    XMLReader reader = XMLReaderFactory.createXMLReader();
    Log.log(Log.DEBUG, XSLTUtilities.class, "XMLReader=" + reader.getClass().getName());
    reader.setContentHandler(handlers[0]);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handlers[0]);

//    String inputPath = inputFile.substring(0, inputFile.lastIndexOf(File.separatorChar));
    EntityResolver entityResolver = new EntityResolverImpl(inputFile);
    reader.setEntityResolver(entityResolver);

    int lastIndex = stylesheets.length - 1;
    Transformer lastTransformer = handlers[lastIndex].getTransformer();
    lastTransformer.setOutputProperty(OutputProperties.S_KEY_INDENT_AMOUNT, XSLTUtilities.indentAmount);

    StringWriter writer = new StringWriter();
    Result result = new StreamResult(writer);

    handlers[lastIndex].setResult(result);

    reader.parse(inputFile);
    String resultString = writer.toString();
    return removeIn(resultString, '\r'); //remove '\r' to temporarily fix a bug in the display of results in Windows
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

