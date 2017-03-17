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

package imageviewer.actions;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JViewport;
import imageviewer.ImageViewer;

public class CenterAction extends MouseAdapter {
    
    private final ImageViewer imageViewer;
    
    public CenterAction(ImageViewer imageViewer) {
        this.imageViewer = imageViewer;    
    }
    
    public void mouseClicked(MouseEvent me) {
        center(me.getPoint());   
    }
    
    // TODO: this is supposed to center the image in the viewport on the mouse pointer,
    // but it doesn't take into account the location of the image label inside the viewport,
    // sok this isn't doing what it's supposed to do.
    private void center( Point p ) {
        JViewport viewport = imageViewer.getViewport();
        
        int cx = viewport.getWidth() / 2;
        int cy = viewport.getHeight() / 2;

        int dx = p.x - cx;
        int dy = p.y - cy;

        Point current = viewport.getViewPosition();
        Point to = new Point( current.x + dx, current.y + dy );
        viewport.setViewPosition( to );
    }
    
}