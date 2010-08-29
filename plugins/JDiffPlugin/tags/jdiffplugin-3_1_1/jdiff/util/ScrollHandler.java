package jdiff.util;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.ScrollListener;
import org.gjt.sp.jedit.textarea.TextArea;
import jdiff.DualDiff;
import jdiff.DualDiffManager;

public class ScrollHandler implements ScrollListener, FocusListener, MouseListener {

    private DualDiff parent = null;

    private Runnable syncWithRightVert;
    private Runnable syncWithLeftVert;
    private Runnable syncWithRightHoriz;
    private Runnable syncWithLeftHoriz;

    public ScrollHandler( DualDiff parent ) {
        if ( parent == null ) {
            throw new IllegalArgumentException( "Constructor must have valid DualDiff parameter." );
        }
        this.parent = parent;
        syncWithRightVert = new Runnable() {
                    public void run() {
                        ScrollHandler.this.parent.getDiffOverview0().repaint();
                        ScrollHandler.this.parent.getDiffOverview0().synchroScrollRight();
                        ScrollHandler.this.parent.getDiffOverview1().repaint();
                    }
                };

        syncWithLeftVert = new Runnable() {
                    public void run() {
                        ScrollHandler.this.parent.getDiffOverview1().repaint();
                        ScrollHandler.this.parent.getDiffOverview1().synchroScrollLeft();
                        ScrollHandler.this.parent.getDiffOverview0().repaint();
                    }
                };

        syncWithRightHoriz = new Runnable() {
                    public void run() {
                        ScrollHandler.this.parent.getTextArea1().setHorizontalOffset( ScrollHandler.this.parent.getTextArea0()
                                .getHorizontalOffset() );
                    }
                };

        syncWithLeftHoriz = new Runnable() {
                    public void run() {
                        ScrollHandler.this.parent.getTextArea0().setHorizontalOffset( ScrollHandler.this.parent.getTextArea1()
                                .getHorizontalOffset() );
                    }
                };

    }

    public void scrolledHorizontally( TextArea textArea ) {
        if ( textArea == parent.getTextArea0() ) {
            SwingUtilities.invokeLater( syncWithRightHoriz );
        }
        else if ( textArea == parent.getTextArea1() ) {
            SwingUtilities.invokeLater( syncWithLeftHoriz );
        }
    }

    public void scrolledVertically( TextArea textArea ) {
        if ( textArea == parent.getTextArea0() ) {
            SwingUtilities.invokeLater( syncWithRightVert );
        }
        else if ( textArea == parent.getTextArea1() ) {
            SwingUtilities.invokeLater( syncWithLeftVert );
        }
    }

    public void focusGained( FocusEvent e ) {
        //Log.log( Log.DEBUG, this, "**** focusGained " + e );
        if ( jEdit.getBooleanProperty( "jdiff.auto-show-dockable" ) ) {
            if ( !parent.getView().getDockableWindowManager().isDockableWindowVisible( DualDiffManager.JDIFF_LINES ) ) {  // NOPMD ifs on separate lines for readability
                parent.getView().getDockableWindowManager().showDockableWindow( DualDiffManager.JDIFF_LINES );
            }
        }
    }

    public void focusLost( FocusEvent e ) {
        //Log.log( Log.DEBUG, this, "**** focusLost " + e );
    }

    public void mouseClicked( MouseEvent e ) {}

    public void mouseEntered( MouseEvent e ) {}

    public void mouseExited( MouseEvent e ) {}

    public void mousePressed( MouseEvent e ) {
        //Log.log( Log.DEBUG, this, "**** mousePressed " + e );
    }

    public void mouseReleased( MouseEvent e ) {
        //Log.log( Log.DEBUG, this, "**** mouseReleased " + e );
    }

}