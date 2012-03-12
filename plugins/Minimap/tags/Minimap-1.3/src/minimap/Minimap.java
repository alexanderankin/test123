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

	private final EditPane editPane;
	private final MinimapTextArea miniMap;
	private Component child;
	private final JSplitPane splitter;
	
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
		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		setSplitterComponents();
		add(splitter);
		miniMap.setBuffer(textArea.getBuffer());
	}

	private void setSplitterComponents() {
		if (Options.getSideProp().equals(Options.LEFT)) {
			splitter.setLeftComponent(miniMap);
			splitter.setRightComponent(child);
		} else {
			splitter.setLeftComponent(child);
			splitter.setRightComponent(miniMap);
		}
	}
	
	public void propertiesChanged() {
		setSplitterComponents();
	}
	
	public void start() {
		miniMap.start();
		editPane.add(this);
		editPane.validate();
	}
	public void stop(boolean restore) {
		miniMap.stop();
		editPane.remove(this);
		if (restore) {
			editPane.add(child);
			editPane.validate();
		}
		remove(splitter);
		splitter.remove(child);
		splitter.remove(miniMap);
	}
	
	public void updateFolds() {
		miniMap.updateFolds();
	}
}
