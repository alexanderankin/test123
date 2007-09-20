/*
 * ColumnInsertDialog.java - a Java class for the jEdit text editor
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.Container;

public class ColumnInsertDialog extends JDialog
{
	private JTextField text;
        
	//This is a test.  I want to see how well jedit responds to my typing.
	public ColumnInsertDialog(KeyListener listener){
		Container cPane = this.getContentPane();
		text = new JTextField(40);
		text.addKeyListener(listener);
		text.setText("Input Text here");
		text.selectAll();
		cPane.add(text);
		this.pack();
		this.setVisible(true);
	}
    
	public String getText(){
		return text.getText();
	}
}