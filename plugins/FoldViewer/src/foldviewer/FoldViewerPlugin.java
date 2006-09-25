/*
 * FoldViewerPlugin.java 
 * Copyright (c) Sun Aug 20 MSD 2006 Denis Koryavov
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


package foldviewer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;
import org.gjt.sp.jedit.View;

public class FoldViewerPlugin extends EBPlugin {
        
        //{{{ start method.
        public void start() {
                View view = jEdit.getFirstView();
		while(view != null) {
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++) {
				JEditTextArea textArea = panes[i].getTextArea();
				addExtension(textArea);
			}
			view = view.getNext();
		}
        } 
        //}}}
        
        //{{{ handleMessage method.
        public void handleMessage(EBMessage message) {
                if (message instanceof EditPaneUpdate) {
                        EditPaneUpdate epu = (EditPaneUpdate)message;
			if(epu.getWhat() == EditPaneUpdate.CREATED) {
				addExtension(epu.getEditPane().getTextArea());
			}
                }
        } 
        //}}}
        
        //{{{ addExtension method.
        private void addExtension(JEditTextArea textArea) {
                TextAreaExtension extension = new FoldViewerExtension();
                textArea.getPainter().addExtension(extension);
                textArea.putClientProperty(FoldViewerExtension.class, extension);
        } 
        //}}}
        
        //{{{ stop() method
	public void stop() {
		View view = jEdit.getFirstView();
		while(view != null) {
			EditPane[] panes = view.getEditPanes();
			for(int i = 0; i < panes.length; i++) {
				JEditTextArea textArea = panes[i].getTextArea();
				removeExtension(textArea);
			}
			view = view.getNext();
		}
	}
        //}}}
        
        //{{{ removeExtension method
        private void removeExtension(JEditTextArea textArea) {
                TextAreaExtension extension = (FoldViewerExtension)textArea
                .getClientProperty(FoldViewerExtension.class);
                
                if(extension != null) {
			textArea.getPainter().removeExtension(extension);
		}
        }
        // }}}
        
        // :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:
}
