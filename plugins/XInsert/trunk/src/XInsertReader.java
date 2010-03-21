/*
 * 13:44:51 17/06/99
 *
 * XInsertReader.java - Reads xml-insert files
 * Copyright (C) 1999 Romain Guy
 * www.chez.com/powerteam
 * powerteam@chez.com
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

import java.io.*;
import org.gjt.sp.util.Log;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XInsertReader {
  public XInsertReader() { }

  public static boolean read(XTree tree, InputStream inputStream, String file) {
    InputStreamReader reader = new InputStreamReader(inputStream);
    XInsertHandler xmh = new XInsertHandler(tree);
	try
	{
		XMLReader parser = XMLReaderFactory.createXMLReader();
		parser.setErrorHandler(xmh);
		parser.setContentHandler(xmh);
		parser.setEntityResolver(xmh);
		parser.parse(new InputSource(reader));
	} catch(SAXParseException e) {
      Log.log(Log.ERROR,tree,"XInsert: Error parsing grammar " + file);
      Log.log(Log.ERROR,tree,"XInsert: Error occured at line " + e.getLineNumber() +
                         ", column " + e.getColumnNumber());
      Log.log(Log.ERROR,tree,"XInsert: " + e.getMessage());
      return false;
    } catch(IOException ioe) {
    	Log.log(Log.ERROR,tree,"XInsert: Error parsing grammar " + file);
    	Log.log(Log.ERROR,tree,ioe);
    	return false;
    } catch (Exception e) {
      // Should NEVER happend !
      e.printStackTrace();
      return false;
    } finally {
		try {
		  reader.close();
		} catch (IOException ioe) { 
				ioe.printStackTrace(); 
		}
	}
    return true;
  }
}

// End of XInsertReader.java

