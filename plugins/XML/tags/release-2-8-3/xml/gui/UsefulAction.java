/*
 * UsefulAction.java - Convenience abstract GUI action
 *
 * Copyright (C) 2003 Robert McKinnon
 * Copyright (C) 2010 Eric Le Lay
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

package xml.gui;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

import java.awt.Dimension;
import java.net.URL;

/**
 * Convenience abstract GUI action.
 * copied from the XSLT Plugin : xslt/XsltAction.java
 *
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public abstract class UsefulAction extends AbstractAction {
	private String actionType;

  public UsefulAction(String actionType) {
    this.actionType = actionType;
    
    String actionName = jEdit.getProperty(actionType + ".name");
    String shortcut = jEdit.getProperty(actionType + ".shortcut");
    String shortDescription = jEdit.getProperty(actionType + ".short-desc");
    String iconName = jEdit.getProperty(actionType + ".small-icon");

    shortDescription = (shortcut != null) ? shortDescription + " - " + shortcut : shortDescription;


    putValue(Action.ACTION_COMMAND_KEY, actionType);
    putValue(Action.NAME, actionName);
    putValue(Action.SHORT_DESCRIPTION, shortDescription);
    if(iconName != null){
    	Icon icon;
    	if(iconName.startsWith("/")){
    		URL u = getClass().getResource(iconName);
    		icon = new ImageIcon(u);
    	}else{
    		icon = GUIUtilities.loadIcon(iconName);
    	}
    	putValue(Action.SMALL_ICON, icon);
    }
  }


  public JButton getButton() {
    JButton button = new JButton(this);
    button.setText("");
    button.setName(actionType);
    Dimension dimension = getButtonDimension();

    button.setMinimumSize(dimension);
    button.setPreferredSize(dimension);
    button.setMaximumSize(dimension);

    return button;
  }
  
  public JRadioButton getRadioButton(String text) {
	JRadioButton button = new JRadioButton(this);
	button.setName(text);
	button.setText(jEdit.getProperty(text));

	return button;
  }


  protected Dimension getButtonDimension() {
    Dimension dimension = new Dimension(30, 30);
    return dimension;
  }


  public JMenuItem getMenuItem() {
    JMenuItem item = new JMenuItem(this);
    item.setIcon(null);
    return item;
  }


  public static JPopupMenu initMenu(Object[] actions) {
    JPopupMenu menu = new JPopupMenu();

    for(int i = 0; i < actions.length; i++) {
      Object action = actions[i];
      if(action == null) {
        menu.addSeparator();
      } else {
        menu.add(((UsefulAction)action).getMenuItem());
      }
    }

    return menu;
  }
}
