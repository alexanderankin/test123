/*
 *  AbstractIndentingTransformer.java - Indents XML elements, by adding whitespace where appropriate.
 *
 *  Copyright (c) 2002, 2003 Robert McKinnon
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *  email: robmckinnon@users.sourceforge.net
 */

package xmlindenter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;
import java.io.Writer;


/**
 * Indents elements, by adding whitespace where appropriate.
 * Does not remove blank lines between nodes.
 * Does not remove new lines within text nodes.
 * Puts element tags immediately following mixed content text on the same line as the text.
 *
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public abstract class IndentingTransformer implements TransformerHandler, DeclHandler {

  /** buffer to hold character data */
  private Writer writer;

  private String xml;
  private char[] chars;
  private boolean isContinue = true;
  private boolean isClosingTag = false;
  private boolean isEmptyElement = false;
  private boolean isDocType = false;

  public void characters(char ch[], int start, int length) throws SAXException {
    try {
      if(isDocType) {
        isDocType = false;
      } else {
        writer.write(ch, start, length);
      }
    } catch(IOException e) {
      throw new SAXException(e);
    }
  }


  public void comment(char ch[], int start, int length) throws SAXException {
    try {
      writer.write("<!--");
      writer.write(ch, start, length);
      writer.write("-->");
    } catch(IOException e) {
      throw new SAXException(e);
    }
  }


  public void processingInstruction(String target, String data) throws SAXException {
    try {
      writer.write("<?");
      writer.write(target);
      writer.write(" ");
      writer.write(data);
      writer.write("?>");
    } catch(IOException e) {
      throw new SAXException(e);
    }
  }


  public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    try {
      if(isClosingTag) {
        writer.write("</");
        writer.write(qName);
        writer.write(">");
        isClosingTag = false;
      }
    } catch(IOException e) {
      throw new SAXException(e);
    }
  }


  public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      throws SAXException {

    try {
      writer.write("<");
      writer.write(qName);

      for(int i = 0; i < atts.getLength(); i++) {
        writer.write(' ');
        writer.write(atts.getQName(i));
        writer.write("=\"");
        writer.write(atts.getValue(i));
        writer.write('\"');
      }

      if(isEmptyElement) {
        writer.write("/>");
      } else {
        writer.write(">");
      }
    } catch(IOException e) {
      throw new SAXException(e);
    }

  }


  protected String indentXml(final String xmlString, final Writer outputWriter) throws IOException, SAXException {
    this.writer = outputWriter;
    this.xml = xmlString;
    this.chars = xml.toCharArray();

    int start = 0;
    int end = 0;

    while(isContinue) {
      end = xml.indexOf('<', start);
      writeTextPrecedingLessThan(start, end);

      if(isContinue) {
        start = end;

        if(xml.startsWith("<!--", start)) {
          end = writeComment(start);

        } else if(xml.startsWith("<?", start)) {
          end = writeXmlDeclarationOrProcessingInstruction(start);

        } else if(xml.startsWith("<!", start)) {
          if(xml.startsWith("<![CDATA[", start)) {
            end = writeCData(start);
          } else {
            end = writeDocType(start);
          }
        } else if(xml.startsWith("</", start)) {
          end = writeClosingTag(start);

        } else if(Character.isWhitespace(chars[start+1])) {
          throw new SAXException("The content of elements must consist of well-formed character data or markup.");

        } else {
          end = writeElement(start);
        }

        start = end;
      }
    }

    return outputWriter.toString();
  }


  private int writeCData(int start) throws IOException, SAXException {
    int end = xml.indexOf("]]>", start);
    writeRemaining(start, end);
    if(isContinue) {
      startCDATA();
      end = end + 3;
      writer.write(xml, start, end - start);
      endCDATA();
    }

    return end;
  }


  private int writeElement(int start) throws IOException, SAXException {
    int end = getStartTagEnd(start);
    writeRemaining(start, end);

    if(isContinue) {
      int offset = 1;
      while(Character.isWhitespace(chars[end - offset])) {
        offset++;
      }
      isEmptyElement = (chars[end - offset] == '/');

      if(isEmptyElement) {
        end = end - offset;
      }

      AttributesImpl attributes = new AttributesImpl();
      String elementName = getElementNameAndPopulateAttributes(start, end, attributes);

      startElement("", "", elementName, attributes);

      if(isEmptyElement) {
        endElement("", "", elementName);
        end = end + offset + 1;
        isEmptyElement = false;
      } else {
        end = end + 1;
      }
    }

    return end;
  }


  /**
   * Ignores '>' characters that are inside of attribute values.
   */
  private int getStartTagEnd(int start) {
    int end = -1;
    int index = start;

    while(index < chars.length && end == -1) {
      char aChar = chars[index];
      index++;

      if(aChar == '\"') {
        while(chars[index] != '\"') {
          index++;
        }
        index++;
      } else if(aChar == '\'') {
        while(chars[index] != '\'') {
          index++;
        }
        index++;
//      } else if(aChar == '/') {
//        while(chars[index] != '>') {
//          index++;
//        }
      } else if(aChar == '>') {
        end = index -1;
//        end = index;
      }
    }
    return end;
  }


  private String getElementNameAndPopulateAttributes(int start, int end, AttributesImpl attributes) throws SAXException {
    int nameEnd = xml.indexOf(' ', start);

    if(nameEnd == -1 || end < nameEnd) {
      nameEnd = end;
    } else if(nameEnd + 1 != end) {
      char[] chars = xml.substring(nameEnd + 1, end).toCharArray();
      populateAttributes(chars, attributes);
    }

    StringBuffer elementName = new StringBuffer();
    int index = start + 1;

    while(!Character.isWhitespace(chars[index]) && chars[index] != '>' && chars[index] != '/') {
      elementName.append(chars[index++]);
    }

    return elementName.toString();
  }


  private void populateAttributes(char[] chars, AttributesImpl attributes) throws SAXException {
    StringBuffer qName = new StringBuffer();
    StringBuffer value = new StringBuffer();
    boolean isLastSpace = false;
    char quote = '\"';
    int i = 0;

    while(i < chars.length) {
      qName.setLength(0);
      value.setLength(0);

      while(i < chars.length && Character.isWhitespace(chars[i])) {
        i++;
      }

      if(i < chars.length) {
        while(i < chars.length && chars[i] != '=') {
          qName.append(chars[i]);
          i++;
        }
        i++; // get past equals

        while(i < chars.length && Character.isWhitespace(chars[i])) {
          i++;
        }

        if(i < chars.length) {
          if(chars[i] != '\"' && chars[i] != '\'') {
            throw new SAXException("value for attribute " + qName.toString().trim() + " must be in quotes");
          } else {
            quote = chars[i];
            i++;
          }
        }

        while(i < chars.length && chars[i] != quote) {
          if(Character.isWhitespace(chars[i])) {
            if(!isLastSpace) {
              if(Character.isSpaceChar(chars[i])) {
                value.append(' ');
              }
              isLastSpace = true;
            } else {
              // don't add consequtive space
            }
          } else {
            value.append(chars[i]);
            isLastSpace = false;
          }

          i++;
        }

        i++; // get past quote

        attributes.addAttribute("", "", qName.toString().trim(), "", value.toString().trim());
      }
    }
  }


  private int writeClosingTag(int start) throws IOException, SAXException {
    int end = xml.indexOf('>', start);
    writeRemaining(start, end);

    if(isContinue) {
      isClosingTag = true;
      endElement("", "", xml.substring(start + 2, end).trim());
      end = end + 1;
    }
    return end;
  }


  private int writeDocType(int start) throws IOException {
    int end = xml.indexOf('>', start);
    int bracketStart = xml.indexOf('[', start);

    if(bracketStart != -1 && bracketStart < end) {
      int bracketEnd = xml.indexOf(']', bracketStart);
      end = xml.indexOf('>', bracketEnd);
    }

    writeRemaining(start, end);

    if(isContinue) {
      end = end + 1;
      writer.write("\n");
      int length = end - start;
      writer.write(xml, start, length);
      isDocType = true;
    }

    return end;
  }


  private int writeXmlDeclarationOrProcessingInstruction(int start) throws IOException, SAXException {
    int end;

    if(xml.startsWith("<?xml ", start)) {
      end = writeXmlDeclaration(start);
    } else {
      end = writeProcessingInstruction(start);
    }

    return end;
  }


  private int writeProcessingInstruction(int start) throws IOException, SAXException {
    int end = xml.indexOf("?>", start);
    writeRemaining(start, end);

    if(isContinue) {
      int targetEnd = xml.indexOf(" ", start);
      String target = xml.substring(start + "<?".length(), targetEnd);
      String data = xml.substring(targetEnd + 1, end);
      processingInstruction(target, data);
      end = end + "?>".length();
    }

    return end;
  }


  private int writeXmlDeclaration(int start) throws IOException {
    int end = xml.indexOf("?>", start);
    writeRemaining(start, end);

    if(isContinue) {
      end = end + "?>".length();
      int length = end - start;
      writer.write(xml, start, length);
    }

    return end;
  }


  private int writeComment(int start) throws IOException, SAXException {
    int end = xml.indexOf("-->", start);
    writeRemaining(start, end);

    if(isContinue) {
      int commentTextStart = start + "<!--".length();
      int commentTextLength = end - commentTextStart;
      comment(chars, commentTextStart, commentTextLength);
      end = end + "-->".length();
    }

    return end;
  }


  private void writeTextPrecedingLessThan(int start, int end) throws IOException, SAXException {
    writeRemaining(start, end);

    if(isContinue && end > start) {
      int length = end - start;
      characters(chars, start, length);
    }
  }


  private void writeRemaining(int start, int end) throws IOException {
    if(end == -1) {
      int length = xml.length() - start;
      writer.write(xml, start, length);
      isContinue = false;
    }
  }


}
