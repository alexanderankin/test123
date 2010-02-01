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

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

public class NativeFlashPlayerToolPanel extends JPanel {
	public static final String NAME = "nativeflashplayer";
	private NativeFlashPlayer player;
	private JTextField address;

	public NativeFlashPlayerToolPanel(final NativeFlashPlayer player) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.player = player;

	    add(makeCustomButton("nativeflashplayer.open",
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						NativeFlashPlayerToolPanel.this.player.open();
					}
				}));
		
	    add(makeCustomButton("nativeflashplayer.play",
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						NativeFlashPlayerToolPanel.this.player.play();
					}
				}));
		
		add(makeCustomButton("nativeflashplayer.pause",
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						NativeFlashPlayerToolPanel.this.player.pause();
					}
				}));
		
		add(makeCustomButton("nativeflashplayer.stop",
				new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						NativeFlashPlayerToolPanel.this.player.stop();
					}
				}));
		address = new JTextField();
		add(address);
		address.setEditable(false);
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

	public void setAddress(String address) {
		this.address.setText(address);
	}
}
