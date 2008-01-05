/*
 * GUIUtils.java
 * Copyright (c) 2002 Calvin Yu
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
package lookandfeel;

import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;

/**
 * GUI convience methods.
 */
public class GUIUtils
{

   /**
    * Create a link label component.
    */
   public static Component createLinkLabelComponent(String msg, String link)
   {
      //JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      Box panel = Box.createHorizontalBox();
      panel.add(new JLabel(msg));
      panel.add(new HrefLinkComponent(" " + link, link));
      //panel.add(new JLabel(link));
      return panel;
   }

}
