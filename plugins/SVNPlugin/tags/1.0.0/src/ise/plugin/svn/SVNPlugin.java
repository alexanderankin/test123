/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package ise.plugin.svn;

import java.util.*;

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.options.GlobalOptions;
import ise.plugin.svn.gui.OutputPanel;
import ise.plugin.svn.gui.TextAreaContextMenu;
import java.awt.event.*;
import javax.swing.*;

public class SVNPlugin extends EBPlugin {

    public final static String NAME = "subversion";
    private static HashMap<View, OutputPanel> panelMap = null;
    private static HashMap<View, TextAreaContextMenu> menuMap = null;

    public static OutputPanel getOutputPanel( View view ) {
        if ( panelMap == null ) {
            panelMap = new HashMap<View, OutputPanel>();
        }
        OutputPanel panel = panelMap.get( view );
        if ( panel == null ) {
            panel = new OutputPanel();
            panelMap.put( view, panel );
        }
        return panel;
    }

    public void handleMessage( EBMessage message ) {
        addContextMenu( jEdit.getActiveView() );
        if ( message instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) message;
            if ( ViewUpdate.CLOSED == vu.getWhat() && panelMap != null ) {
                panelMap.remove( vu.getView() );
            }
        }
    }

    public void stop() {
        if ( panelMap != null ) {
            panelMap.clear();
            panelMap = null;
        }

        for ( View view : jEdit.getViews() ) {
            removeContextMenu( view );
        }
    }

    public void start() {
    }

    private static TextAreaContextMenu createContextMenu( View view ) {
        if ( menuMap == null ) {
            menuMap = new HashMap<View, TextAreaContextMenu>();
        }
        TextAreaContextMenu menu = menuMap.get( view );
        if ( menu == null ) {
            menu = new TextAreaContextMenu( view );
            menuMap.put( view, menu );
        }
        return menu;
    }

    private static void addContextMenu( View view ) {
        if ( view == null ) {
            return ;
        }
        removeContextMenu( view );
        TextAreaContextMenu context_menu = createContextMenu( view );
        JPopupMenu menu = view.getTextArea().getRightClickPopup();
        if ( !context_menu.equals( menu.getComponent( 0 ) ) ) {
            menu.insert( new JPopupMenu.Separator(), 0 );
            menu.insert( context_menu, 0 );
        }
    }

    private static void removeContextMenu( final View view ) {
        if ( view == null ) {
            return ;
        }
        JPopupMenu popup = GUIUtilities.loadPopupMenu( "view.context" );
        JMenuItem customize = new JMenuItem( jEdit.getProperty( "view.context.customize" ) );
        customize.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent evt ) {
                        new GlobalOptions( view, "context" );
                    }
                }
                                   );
        popup.addSeparator();
        popup.add( customize );
        if ( view.getTextArea() != null ) {
            view.getTextArea().setRightClickPopup( popup );
        }
    }
}
