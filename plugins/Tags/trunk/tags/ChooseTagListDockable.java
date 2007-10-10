/*
 * TagsPlugin.java
 * Copyright (c) 2001, 2002 Kenrick Drew (kdrew@earthlink.net)
 * Copyright (c) 2003, 2004 Ollie Rutherfurd (oliver@jedit.org)
 * Copyright (c) 2007 Shlomy Reinstein (shlomy@users.sourceforge.net)
 *
 * This file is part of the Tags plugin.
 *
 * TagsPlugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * TagsPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA.
 *
 * $Id: TagsPlugin.java 10867 2007-10-10 13:01:00Z shlomy $
 */

package tags;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;

public class ChooseTagListDockable extends JPanel
	implements DefaultFocusComponent {

	private View view;
	private ChooseTagList chooseTagList = null;
	private JScrollPane scroller = null;
	
	public ChooseTagListDockable(View view) {
		super(new BorderLayout());
		this.view = view;
		setTagLines(new Vector());
	}
	
	public void setTagLines(Vector tagLines) {
		if (scroller != null)
			remove(scroller);
		chooseTagList = new ChooseTagList(tagLines);
		scroller = new JScrollPane(chooseTagList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroller, BorderLayout.CENTER);
		chooseTagList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selected();
			}
		});
		chooseTagList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() >= '1' && e.getKeyChar() <= '9') {
					int selected = Character.getNumericValue(e.getKeyChar()) - 1;
					if (selected >= 0 && 
						selected < chooseTagList.getModel().getSize())
					{
						chooseTagList.setSelectedIndex(selected);
						selected();
						e.consume();
					}
				}
			}
			
		});
		revalidate();
	}
	
	private void selected()
	{
		TagLine tagLine = (TagLine)chooseTagList.getSelectedValue();
		TagsPlugin.goToTagLine(view, tagLine, false, tagLine.getTag());
	}

	public void focusOnDefaultComponent() {
		if (chooseTagList != null)
			chooseTagList.requestFocus();
	}
}
