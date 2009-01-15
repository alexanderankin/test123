/*
Copyright (C) 2009  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package minimap;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.textarea.JEditTextArea;

@SuppressWarnings("serial")
public class Minimap extends JPanel {

	EditPane editPane;
	MinimapTextArea miniMap;
	Component child;
	
	public Minimap(EditPane editPane) {
		setLayout(new GridLayout(1, 1));
		this.editPane = editPane;
		JEditTextArea textArea = editPane.getTextArea();
		miniMap = new MinimapTextArea(textArea);
		Container c = textArea.getParent();
		child = textArea;
		while (! (c instanceof EditPane)) {
			child = c;
			c = c.getParent();
		}
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitter.add(miniMap);
		splitter.add(child);
		add(splitter);
		miniMap.setBuffer(textArea.getBuffer());
	}
	
	public void start() {
		miniMap.start();
		editPane.add(this);
		editPane.validate();
	}
	public void stop() {
		miniMap.stop();
		editPane.remove(this);
		editPane.add(child);
		editPane.validate();
	}
}
