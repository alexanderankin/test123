/*
 * XSLTUtilities.java - Utilities for performing XSL Transformations
 *
 * Copyright (c) 2002 Robert McKinnon
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.templates.OutputProperties;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * XSLTUtilities.java - Utilities for performing XSL Transformations
 *
 *@author   Robert McKinnon
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
  public static String transform(String inputFile, Object[] stylesheets) throws Exception {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    String resultString = null;

    if(transformerFactory.getFeature(SAXSource.FEATURE) && transformerFactory.getFeature(SAXResult.FEATURE)) {
      resultString = saxTransform(transformerFactory, inputFile, stylesheets);
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
   *@param xsltFile       name of stylesheet file
   *@param indent         true if result is to be indented
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


  private static Source getSourceFromString(String string) {
    return new StreamSource(new StringReader(string));
  }


  private static Result getResult(OutputStream outputStream, String encoding) throws Exception {
    Writer writer = new OutputStreamWriter(outputStream, encoding);
    return new StreamResult(writer);
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

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Result result = getResult(outputStream, transformer.getOutputProperty("encoding"));

    transformer.transform(inputSource, result);
    String resultString = outputStream.toString();
    return resultString;
  }


  private static String saxTransform(TransformerFactory factory, String inputFile, Object[] stylesheets) throws Exception {
    SAXTransformerFactory saxFactory = ((SAXTransformerFactory)factory);
    TransformerHandler[] handlers = new TransformerHandler[stylesheets.length];

    for(int i = 0; i < stylesheets.length; i++) {
      Source stylesheetSource = getSource((String)stylesheets[i]);
      handlers[i] = saxFactory.newTransformerHandler(stylesheetSource);

      if(i != 0) {
        handlers[i - 1].getTransformer().setOutputProperty(OutputKeys.INDENT, "no");
        handlers[i - 1].setResult(new SAXResult(handlers[i]));
      }
    }

    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(handlers[0]);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handlers[0]);

    int lastIndex = stylesheets.length - 1;
    Transformer lastTransformer = handlers[lastIndex].getTransformer();
    lastTransformer.setOutputProperty(OutputProperties.S_KEY_INDENT_AMOUNT, XSLTUtilities.indentAmount);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Result result = getResult(outputStream, lastTransformer.getOutputProperty("encoding"));
    handlers[lastIndex].setResult(result);

    reader.parse(inputFile);
    return outputStream.toString();
  }
}

