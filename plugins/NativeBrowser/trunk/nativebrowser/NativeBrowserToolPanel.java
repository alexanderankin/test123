package nativebrowser;
/*
 * NativeBrowserToolPanel.java
 * part of the NativeBrowser plugin for the jEdit text editor
 * Copyright (C) 2010 Francois Rey
 * jedit at francois . rey . name
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
 *
 * $Id$
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

public class NativeBrowserToolPanel extends JPanel {
	private NativeBrowser browser;
	private JCheckBox menuBarCheckBox;

	public NativeBrowserToolPanel(final NativeBrowser browser) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.browser = browser;

	    menuBarCheckBox = new JCheckBox(jEdit.getProperty(
				NativeBrowserPlugin.OPTION_PREFIX + "menu-bar"), true);
	    menuBarCheckBox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent e) {
	    	  NativeBrowserToolPanel.this.browser.setMenuBarVisible(e.getStateChange() == ItemEvent.SELECTED);
	      }
	    });
	    add(menuBarCheckBox);

	    add(makeCustomButton("nativebrowser.home",
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						NativeBrowserToolPanel.this.browser.home();
					}
				}));
		
		add(makeCustomButton("nativebrowser.render-buffer",
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						NativeBrowserToolPanel.this.browser.renderBuffer(jEdit.getActiveView());
					}
				}));
	}

	void propertiesChanged() {
		menuBarCheckBox.setSelected(browser.isMenuBarVisible());
	}

	private AbstractButton makeCustomButton(String name, ActionListener listener) {
		String toolTip = jEdit.getProperty(name.concat(".label"));
		AbstractButton b = new RolloverButton(GUIUtilities.loadIcon(jEdit
				.getProperty(name + ".icon")));
		if (listener != null) {
			b.addActionListener(listener);
			b.setEnabled(true);
		} else {
			b.setEnabled(false);
		}
		b.setToolTipText(toolTip);
		return b;
	}

}
