/*
Copyright (c) 2009, Dale Anson
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

package imageviewer;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import projectviewer.vpt.VPTNode;
import org.gjt.sp.jedit.jEdit;

/**
 * ProjectViewer Action to be added to the PV context menu.  This class serves
 * as menu item to launch the image viewer for the selected file in PV.
 */
public class ImageViewerPVAction extends projectviewer.action.Action {

    private VPTNode node = null;

    public String getText() {
        return jEdit.getProperty( "imageviewer.showimage", "View Image" );
    }

    // called by ProjectViewer to let us know the currently selected node in
    // the PV tree.  This method checks if the selected node is an image file and
    // enables or disables the menuItem accordingly.
    public void prepareForNode( final VPTNode node ) {
        if ( node == null ) {
            return ;
        }
        this.node = node;

        String name = node.getNodePath();
        if ( name != null ) {
            getMenuItem().setEnabled( ImageViewer.isValidFilename( name ) );
        }
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( node == null ) {
            return ;
        }
        ImageViewer imageViewer = ImageViewerPlugin.getImageViewer( viewer.getView() );
        imageViewer.showImage( node.getNodePath() );
        viewer.getView().getDockableWindowManager().showDockableWindow( "imageviewer" );
    }
}