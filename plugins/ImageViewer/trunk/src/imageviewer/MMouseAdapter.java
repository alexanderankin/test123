package imageviewer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.*;
import java.util.*;
import javax.swing.*;

/**
 * A mouse adapter that also listens to motion and wheel events.
 */
public class MMouseAdapter extends MouseAdapter implements MouseMotionListener, MouseWheelListener {
    Point previous = null;
    Cursor oldCursor = null;
    ImageViewer parent = null;
    JViewport viewport = null;
    JLabel imageLabel = null;
    
    public MMouseAdapter(ImageViewer parent) {
        this.parent = parent;
        viewport = parent.getViewport();
        imageLabel = parent.getImageLabel();
    }
    
    
    public void mouseDragged( MouseEvent me ) {
        if ( previous == null ) {
            previous = me.getPoint();
            return ;
        }
        Point now = me.getPoint();
        int dx = previous.x - now.x;
        int dy = previous.y - now.y;
        Point current = viewport.getViewPosition();
        Point to = new Point( current.x + dx, current.y + dy );
        viewport.setViewPosition( to );
        previous = now;
    }

    public void mousePressed( MouseEvent me ) {
        previous = me.getPoint();
        oldCursor = imageLabel.getCursor();
        imageLabel.setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
    }

    public void mouseReleased( MouseEvent me ) {
        previous = null;
        imageLabel.setCursor( oldCursor != null ? oldCursor : Cursor.getDefaultCursor() );
    }

    public void mouseClicked( MouseEvent me ) {
        if ( me.getClickCount() == 2 ) {
            parent.center( me.getPoint() );
        }
    }

    public void mouseMoved( MouseEvent me ) {
    }

    public void mouseWheelMoved( MouseWheelEvent me ) {
        if ( me.getWheelRotation() > 0 ) {
            parent.zoomIn();
        }
        else {
            parent.zoomOut();
        }
    }
}