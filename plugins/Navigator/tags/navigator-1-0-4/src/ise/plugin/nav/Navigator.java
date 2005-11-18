package ise.plugin.nav;

import java.awt.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
//import org.gjt.sp.jedit.buffer.JEditBuffer;

/**
 * @version   $Revision$
 */
public class Navigator extends JPanel implements Navable {
    /** Description of the Field */
    private Nav nav = null;

    /** Description of the Field */
    private boolean on_toolbar = false;

    /** Description of the Field */
    private View view;

    /** Description of the Field */
    private Box toolbar;

    public Navigator( View view ) {
        this( view, "" );
    }

    /**
     * Constructor for Navigator
     *
     * @param vw
     * @param position
     */
    public Navigator( View vw, String position ) {
        this.view = vw;

        // create a Nav and make sure the plugin knows about it
        nav = new Nav( this );
        NavigatorPlugin.addNavigator( view, this );
        add( nav );

        // add a mouse listener to the view. Each mouse click on a text area in
        // the view is stored for the Nav.
        view.getTextArea().getPainter().addMouseListener(
            new MouseAdapter() {
                public void mouseClicked( MouseEvent ce ) {
                    Buffer b = view.getTextArea().getBuffer();
                    //JEditBuffer b = view.getBuffer();     // for jEdit 4.3
                    int cp = view.getTextArea().getCaretPosition();
                    nav.update( new NavPosition( b, cp ) );
                }
            }
        );
        nav.update( new NavPosition( view.getTextArea().getBuffer(), view.getTextArea().getCaretPosition() ) );

    }

    /**
     * Sets the position of the cursor to the given NavPosition.
     *
     * @param o  The new NavPosition value
     */
    public void setPosition( Object o ) {
        NavPosition np = ( NavPosition ) o;
        Buffer buffer = np.buffer;
        //JEditBuffer buffer = np.buffer;   // for jEdit 4.3
        int caret = np.caret;
        
        if (buffer.equals(view.getBuffer())) {
            // nav in current buffer, just set cursor position
            view.getTextArea().setCaretPosition(caret, true);
            return;
        }
        
        // check if buffer is open
        Buffer[] buffers = jEdit.getBuffers();
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].equals(buffer)) {
                // found it
                view.goToBuffer(buffer);
                view.getTextArea().setCaretPosition(caret, true);
                return;
            }
        }
        
        // buffer isn't open
        String path = buffer.getPath();
        buffer = jEdit.openFile(view, path);
        
        if ( buffer == null ) {
            nav.remove(np);
            return ;   // nowhere to go, maybe the file got deleted?
        }

        if ( caret >= buffer.getLength() ) {
            caret = buffer.getLength() - 1;
        }
        if ( caret < 0 ) {
            caret = 0;
        }
        try {
            view.getTextArea().setCaretPosition( caret, true );
        }
        catch ( NullPointerException npe ) {
            // sometimes Buffer.markTokens throws a NPE here, catch it 
            // and silently ignore it.
        }
        view.getTextArea().requestFocus();
    }


    /**
     * Gets the Nav attribute of the Navigator object
     *
     * @return   The Nav value
     */
    public Nav getNav() {
        return nav;
    }

    /** go back one in the history list */
    public void goBack() {
        nav.goBack();
    }

    /** go forward one in the history list */
    public void goForward() {
        nav.goForward();
    }

}

