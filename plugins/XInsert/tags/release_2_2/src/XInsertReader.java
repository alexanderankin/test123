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
import com.microstar.xml.*;

public class XInsertReader {
  public XInsertReader() { }

  public static boolean read(XTree tree, InputStream fileName, String file) {
    InputStreamReader reader = new InputStreamReader(fileName);
    XInsertHandler xmh = new XInsertHandler(tree);
    XmlParser parser = new XmlParser();
    parser.setHandler(xmh);
    try {
      parser.parse(XInsertReader.class.getResource("xinsert.dtd").toString(), null, reader);
    } catch (XmlException e) {
      Log.log(Log.ERROR,tree,"XInsert: Error parsing grammar " + fileName);
      Log.log(Log.ERROR,tree,"XInsert: Error occured at line " + e.getLine() +
                         ", column " + e.getColumn());
      Log.log(Log.ERROR,tree,"XInsert: " + e.getMessage());
      return false;
    } catch (Exception e) {
      // Should NEVER happend !
      e.printStackTrace();
      return false;
    }
		try {
      fileName.close();
      reader.close();
    } catch (IOException ioe) { 
			ioe.printStackTrace(); 
		}
    return true;
  }
}

// End of XInsertReader.java

