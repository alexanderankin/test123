/*
* 19:34:16 10/09/99
*
* XInsertHandler.java - Handles xml-insert files for Jext
* Copyright (C) 1999 Romain Guy - powerteam@chez.com
* Portions Copyright (C) 2000 Dominic Stolerman - dominic@sspd.org.uk
* added REFERENCE_TYPE (c) 2006 Martin Raspe - hertzhaft@biblhertz.it
* www.chez.com/powerteam
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

import java.util.Stack;
import java.util.Vector;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;

import org.gjt.sp.util.Log;

public class XInsertHandler extends DefaultHandler {

  // private members

  private XTree tree;
  private Stack stateStack;
  private int type = XTreeItem.TEXT_TYPE;
  private String lastAttr, lastName, lastAttrValue, propValue;
  private StringBuilder lastValue;

  public XInsertHandler(XTree tree) { this.tree = tree; }

  public void attribute(String aname, String value) {
    // TYPE attribute
    if (aname.equalsIgnoreCase("TYPE")) {
      if(value.equalsIgnoreCase("MACRO"))
        type = XTreeItem.MACRO_TYPE;
      else if(value.equalsIgnoreCase("XINSERT_SCRIPT"))
        type = XTreeItem.XINSERT_SCRIPT_TYPE;
      else if(value.equalsIgnoreCase("TEXT"))
        type = XTreeItem.TEXT_TYPE;
      // new data type added here				
      else if(value.equalsIgnoreCase("NAMED_MACRO"))
        type = XTreeItem.NAMED_MACRO_TYPE;	
      // new data types added: hertzhaft 				
      else if(value.equalsIgnoreCase("ACTION"))
        type = XTreeItem.ACTION_TYPE;	
      else if(value.equalsIgnoreCase("REFERENCE"))
        type = XTreeItem.REFERENCE_TYPE;	
      else {
        type = XTreeItem.UNKNOWN_TYPE;
        Log.log(Log.WARNING, this, "Invalid value for attribute \"type\": " + value);
        }
      }
    else if (aname.equalsIgnoreCase("NAME")) {
    // NAME attribute
      lastAttr = aname;
      lastAttrValue = value;
      }
    else if (aname.equalsIgnoreCase("VALUE"))
    // VALUE attribute
      propValue = value;
    }

  @Override
  public void characters(char[] c, int off, int len) {
    // ITEM content
    if ("ITEM".equalsIgnoreCase(((String) stateStack.peek())))
      // calling append since we get value one bit at a time
      lastValue.append(new String(c, off, len));
    }

  @Override
  public void startElement(String ns, String localName, String qName, Attributes attributes) {
	// handle the attributes before pushing the element
	// (this is what the old com.microstar.xml XmlParser implementation did)
	for(int i=0;i<attributes.getLength();i++)
	{
		String aname = attributes.getLocalName(i);
		String value = attributes.getValue(i);
		attribute(aname, value);
	}
	// reset String contents
	lastValue = new StringBuilder();
	
    if ("NAME".equalsIgnoreCase(lastAttr)) {
      // named MENU
      if ("MENU".equalsIgnoreCase(localName)) {
        //Log.log(Log.DEBUG, this, "adding Menu: " + lastAttrValue);  
        tree.addMenu(lastAttrValue);
        }
      // named VARIABLE
      if ("VARIABLE".equalsIgnoreCase(localName)) {
        if(propValue == null || lastAttrValue == null)
          Log.log(Log.WARNING, this, "Can not set XInsert property");
        else
          tree.addVariable(lastAttrValue, propValue);
      }
    }
    stateStack.push(localName);
  }

  @Override
  public void endElement(String ns, String localName, String qName) {

    if (localName == null) return;
    String lastStartTag = (String) stateStack.peek();
    if (localName.equalsIgnoreCase(lastStartTag)) {
      // MENU end
      if (lastStartTag.equalsIgnoreCase("MENU"))
        tree.closeMenu();
      // ITEM end
      else if (lastStartTag.equalsIgnoreCase("ITEM")) {
        tree.addInsert(lastAttrValue, lastValue.toString(), type);
        type = XTreeItem.TEXT_TYPE;
        }
      stateStack.pop();
      } else
    System.err.println("Unclosed tag: " + stateStack.peek());
    lastAttr = null;
    lastAttrValue = null;
  }

  @Override
  public void startDocument() {
      stateStack = new Stack();
      stateStack.push(null);
  }

}
// End of XInsertHandler.java

