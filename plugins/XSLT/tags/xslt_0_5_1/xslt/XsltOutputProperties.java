/*
 * XsltOutputProperties.java - Holds logic to retrieve output properties for transformers
 *
 * Copyright (c) 2003 Robert McKinnon
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

/**
 * Holds logic to retrieve output properties for transformers
 *
 *@author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XsltOutputProperties {

  private static XsltOutputProperties singletonInstance;
  private boolean messageDisplayed = false;


  static XsltOutputProperties getInstance() {
    if(singletonInstance == null) {
      singletonInstance = new XsltOutputProperties();
    }

    return singletonInstance;
  }


  String getKeyIndentAmount() {
    String indentProperty = null;

    try {
      indentProperty = OutputProperties.S_KEY_INDENT_AMOUNT;
    } catch(NoSuchFieldError e) {
      if(!messageDisplayed) {
        XSLTPlugin.displayOldXalanJarMessage();
        messageDisplayed = true;
      }

      indentProperty = "{http://xml.apache.org/xslt}indent-amount";
    }

    return indentProperty;
  }


}
