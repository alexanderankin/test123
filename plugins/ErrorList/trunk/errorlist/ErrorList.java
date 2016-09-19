
/*
 * Copyright (c) 2007, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package errorlist;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.View;


/**
 */
public class ErrorList extends JPanel {

    private ErrorListPanel panel;


    public ErrorList( View view ) {
        super( new BorderLayout() );
        panel = new ErrorListPanel( view );
        panel.setIsActive( true );
        JScrollPane scroller = new JScrollPane( panel );
        JScrollBar bar = scroller.getVerticalScrollBar();
        bar.setUnitIncrement( 15 );
        add(panel);

    }


    public void unload() {
        panel.unload();
    }
    
    // {{{actions, see actions.xml
    //{{{ collapseAll() method
	/**
	 * Collapse All the nodes on the ErrorList.
	 */
	public void collapseAll()
	{
        panel.collapseAll();
	} //}}}

	//{{{ expandAll() method
	/**
	 * Recursively expand all the nodes on the ErrorList.
	 */
	public void expandAll()
	{
	    panel.expandAll();
	} //}}}

	//{{{ copySelectedNodeToClipboard() method
	public void copySelectedNodeToClipboard()
	{
	    panel.copySelectedNodeToClipboard();
	} //}}}


	//{{{ copyAllNodesToClipboard() method
	public void copyAllNodesToClipboard()
	{
	    panel.copyAllNodesToClipboard();
	} //}}}

	//{{{ toggleErrors() method
	public void toggleErrors()
	{
	    panel.toggleErrors();
	} //}}}

	//{{{ toggleWarnings() method
	public void toggleWarnings()
	{
	    panel.toggleWarnings();
	} //}}}
	//}}}
}
