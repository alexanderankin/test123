package uk.co.antroy.latextools; 

import gnu.regexp.RE;

import java.awt.BorderLayout;

import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

/*:folding=indent:
 * AbstractToolPanel.java - Abstract class representing a tool panel.
 * Copyright (C) 2002 Anthony Roy
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
import javax.swing.JPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;


public abstract class AbstractToolPanel
  extends JPanel
  implements EBComponent {

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @param message ¤
   */
  public abstract void handleMessage(EBMessage message);

  /**
   * ¤
   */
  public abstract void refresh();

  /**
   * ¤
   */
  public abstract void reload();

  /**
   * ¤
   * 
   * @param b ¤
   * @return ¤
   */
  public boolean isMainFile(Buffer b) {

    if (isTeXFile(b)) {

      try {

        RE dc = new RE("\\w*\\\\document(?:class)|(?:style).*");

        for (int i = 0; i < 10; i++) {

          if (dc.isMatch(b.getLineText(i)))
            ;

          return true;
        }
      } catch (Exception e) {
      }

      return false;
    } else {

      return false;
    }
  }

  /**
   * ¤
   * 
   * @param b ¤
   * @return ¤
   */
  public boolean isTeXFile(Buffer b) {
    log("" + b.getMode());

    String s = b.getMode().getName();
    boolean out = s.equals("tex");
    log("" + out);

    return out;
  }

  protected void displayNotTeX(String position) {

    JPanel p = new JPanel();
    StringTokenizer st = new StringTokenizer(jEdit.getProperty(
                                                   "navigation.nottex"), "*");
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

    while (st.hasMoreTokens()) {

      String s = st.nextToken();
      p.add(new JLabel(s));
    }

    setLayout(new BorderLayout());
    add(p, position);
  }

  protected void log(String s) {
    Log.log(Log.DEBUG, this, s);
  }

  protected void log(int i) {
    log("" + i);
  }

  protected void log() {
    log("Green Eggs and Ham");
  }
}
