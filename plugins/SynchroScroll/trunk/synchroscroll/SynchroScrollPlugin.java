package synchroscroll;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;
import org.gjt.sp.jedit.textarea.TextArea;
import java.util.HashMap;

public class SynchroScrollPlugin extends EBPlugin {

    static final String BASELINE = "synchroscroll.BaseLine";
    
    private static HashMap<View, Boolean> scrollingMap = new HashMap<View, Boolean>();
    private static HashMap<TextArea, ScrollHandler> scrollHandlerMap = new HashMap<TextArea, ScrollHandler>(); 

    public void start() { }

    public void stop() { 
        for (TextArea textArea : scrollHandlerMap.keySet()) {
            textArea.removeScrollListener(scrollHandlerMap.get(textArea)); 
            textArea.putClientProperty(BASELINE, null);
        }
        scrollHandlerMap.clear();
        scrollingMap.clear();
    }

    public void handleMessage( EBMessage message ) {
        if (message instanceof ViewUpdate) {
            ViewUpdate vu = (ViewUpdate)message;
            if (vu.CLOSED.equals(vu.getWhat())) {
                scrollingMap.remove(vu.getView());   
            }
        }
        else if (message instanceof EditPaneUpdate) {
            EditPaneUpdate epu = (EditPaneUpdate)message;
            if (epu.DESTROYED.equals(epu.getWhat())) {
                scrollHandlerMap.remove(epu.getEditPane().getTextArea());   
            }
            else if (epu.CREATED.equals(epu.getWhat())) {
                EditPane editPane = epu.getEditPane();
                View view = editPane.getView();
                if (scrollingMap.containsKey(view) && scrollingMap.get(view)) {
                    TextArea textArea = editPane.getTextArea();
                    ScrollHandler scrollHandler = new ScrollHandler(view);
                    textArea.addScrollListener(scrollHandler);
                    textArea.putClientProperty(BASELINE, textArea.getFirstLine());
                    scrollHandlerMap.put(textArea, scrollHandler);
                }
            }
        }
    }

    public static void toggleSynchroScroll( View view ) {
        boolean scrolling = false;
        if ( scrollingMap.containsKey( view ) ) {
            scrolling = !scrollingMap.get( view );
            scrollingMap.put(view, scrolling);
        }
        else {
            scrolling = true;
            scrollingMap.put(view, scrolling);
        }
        EditPane[] editPanes = view.getEditPanes();
        for ( EditPane editPane : editPanes ) {
            TextArea textArea = editPane.getTextArea();
            if ( scrolling ) {
                ScrollHandler scrollHandler = new ScrollHandler(view);
                textArea.addScrollListener(scrollHandler);
                textArea.putClientProperty(BASELINE, textArea.getFirstLine());
                scrollHandlerMap.put(textArea, scrollHandler);
            } else {
                ScrollHandler scrollHandler = scrollHandlerMap.get(textArea);
                if (scrollHandler != null) {
                    textArea.removeScrollListener(scrollHandler);
                    textArea.putClientProperty(BASELINE, null);
                    scrollHandlerMap.remove(textArea);
                }
            }
        }
    }
}