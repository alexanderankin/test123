/*
 * EnhancedJEditorPane.java
 * Copyright (C) 2000 Dirk Moebius
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

package infoviewer.workaround;

import java.io.IOException;
import java.net.URL;
import javax.accessibility.*;
import javax.swing.*;


/**
 * this is  workaround class for JEditorPane. It avoids a bug in 
 * accessibility support.
 */
public class EnhancedJEditorPane extends JEditorPane {

    public EnhancedJEditorPane() {
        super();
    }
    
    public EnhancedJEditorPane(String type, String text) {
        super(type, text);
    }
    
    public EnhancedJEditorPane(String url) throws IOException { 
        super(url); 
    }
    
    public EnhancedJEditorPane(URL initialPage) throws IOException {
        super(initialPage); 
    }
    
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new MyAccessibleJEditorPaneHTML();
        }
        return this.accessibleContext;
    }
    
    protected class MyAccessibleJEditorPaneHTML
            extends JEditorPane.AccessibleJEditorPaneHTML {
        public MyAccessibleJEditorPaneHTML() {
            super();
        }
    }
}
