package synchroscroll;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.TextArea;
import java.util.HashMap;

public class SynchroScrollPlugin extends EBPlugin {

    // key for component property that stores the initial top line of the text area
    static final String BASELINE = "synchroscroll.BaseLine";

    // key for component property to store a reference to the current scroll
    // handler for a text area. This wouldn't be necessary if text area had a
    // "getScrollListeners" method.
    static final String SCROLLHANDLER = "synchroscroll.ScrollHandler";

    // map to keep track of which views are currently using syncroscrolling
    private static HashMap<View, Boolean> scrollingMap = new HashMap<View, Boolean>();

    public void start() { }

    public void stop() {
        // remove scrollhandlers from all text areas and reset client properties
        View[] views = jEdit.getViews();
        for (View view : views) {
            EditPane[] editPanes = view.getEditPanes();
            for (EditPane editPane : editPanes) {
                TextArea textArea = editPane.getTextArea();
                removeScrollHandler( textArea );
                textArea.putClientProperty( BASELINE, null );
            }
        }
        scrollingMap.clear();
    }

    public void handleMessage( EBMessage message ) {
        if ( message instanceof ViewUpdate ) {
            ViewUpdate vu = ( ViewUpdate ) message;
            if ( vu.CLOSED.equals( vu.getWhat() ) ) {
                scrollingMap.remove( vu.getView() );
            }
        } else if ( message instanceof EditPaneUpdate ) {
            EditPaneUpdate epu = ( EditPaneUpdate ) message;
            if ( epu.CREATED.equals( epu.getWhat() ) ) {
                EditPane editPane = epu.getEditPane();
                View view = editPane.getView();
                if ( scrollingMap.containsKey( view ) && scrollingMap.get( view ) ) {
                    TextArea textArea = editPane.getTextArea();
                    removeScrollHandler( textArea );
                    ScrollHandler scrollHandler = new ScrollHandler( view );
                    textArea.addScrollListener( scrollHandler );
                    textArea.putClientProperty( SCROLLHANDLER, scrollHandler );
                    textArea.putClientProperty( BASELINE, textArea.getFirstLine() );
                }
            }
        }
    }

    public static void toggleSynchroScroll( View view ) {
        boolean scrolling = false;
        if ( scrollingMap.containsKey( view ) ) {
            scrolling = !scrollingMap.get( view );
            scrollingMap.put( view, scrolling );
        } else {
            scrolling = true;
            scrollingMap.put( view, scrolling );
        }
        EditPane[] editPanes = view.getEditPanes();
        for ( EditPane editPane : editPanes ) {
            TextArea textArea = editPane.getTextArea();
            removeScrollHandler( textArea );
            if ( scrolling ) {
                ScrollHandler scrollHandler = new ScrollHandler( view );
                textArea.addScrollListener( scrollHandler );
                textArea.putClientProperty( SCROLLHANDLER, scrollHandler );
                textArea.putClientProperty( BASELINE, textArea.getFirstLine() );
            } else {
                textArea.putClientProperty( BASELINE, null );
            }
        }
    }

    private static void removeScrollHandler( TextArea textArea ) {
        ScrollHandler scrollHandler = ( ScrollHandler ) textArea.getClientProperty( SCROLLHANDLER );
        if ( scrollHandler != null ) {
            textArea.removeScrollListener( scrollHandler );
        }
        textArea.putClientProperty( SCROLLHANDLER, null );
    }
}