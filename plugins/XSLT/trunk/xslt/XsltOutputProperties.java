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
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;

/**
 * Holds logic to retrieve output properties for transformers
 *
 *@author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XsltOutputProperties {

  private static XsltOutputProperties singletonInstance;
  private XSLTProcessor processor;
  private boolean messageDisplayed = false;


  private XsltOutputProperties(XSLTProcessor processor) {
    this.processor = processor;
  }


  static void initialize(XSLTProcessor processor) {
    if(singletonInstance == null) {
      singletonInstance = new XsltOutputProperties(processor);
    } else {
      throw new IllegalStateException("XsltOutputProperties can only be initialized once");
    }
  }


  static XsltOutputProperties getInstance() {
    return singletonInstance;
  }


  String getKeyIndentAmount() {
    String indentProperty = null;

    try {
      indentProperty = OutputProperties.S_KEY_INDENT_AMOUNT;
    } catch(NoSuchFieldError e) {
      if(!messageDisplayed) {
        String userPluginsDir = MiscUtilities.constructPath(jEdit.getSettingsDirectory(),"jars");
        String userEndorsedDir = MiscUtilities.constructPath(userPluginsDir, "endorsed");

        String systemPluginsDir = MiscUtilities.constructPath(jEdit.getJEditHome(),"jars");
        String systemEndorsedDir = MiscUtilities.constructPath(systemPluginsDir, "endorsed");


        String[] args = {userPluginsDir, systemPluginsDir, userEndorsedDir, systemEndorsedDir};
        XSLTPlugin.showMessageDialog("xslt.old-jar.message", args, processor);
        messageDisplayed = true;
      }

      indentProperty = "{http://xml.apache.org/xslt}indent-amount";
    }

    return indentProperty;
  }
}
