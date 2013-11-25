package imageviewer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * A mouse adapter that also listens to motion and wheel events. I added this
 * because Java 1.5 doesn't have such a thing and I'd inadvertently used the
 * Java 1.6 version of MouseAdapter, which means this plugin wouldn't have
 * ran on Java 1.5.  This provides Java 1.5 compatibility.
 * TODO: Java 7 has been out for some time, jEdit now requires at least Java 6, 
 * so this can be removed and use the newer java.awt.event.MouseAdapter.
 */
public abstract class MMouseAdapter extends MouseAdapter implements MouseMotionListener, MouseWheelListener {
    
    public void mouseDragged( MouseEvent me ) {}
    
    public void mouseMoved( MouseEvent me ) {}

    public void mouseWheelMoved( MouseWheelEvent me ) {}
}