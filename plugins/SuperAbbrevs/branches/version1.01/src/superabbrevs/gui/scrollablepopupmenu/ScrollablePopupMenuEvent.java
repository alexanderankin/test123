/*
 * ScrollablePopupMenuEvent.java
 *
 * Created on 16. juni 2007, 21:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs.gui.scrollablepopupmenu;

import java.util.EventObject;


/**
 *
 * @author Sune Simonsen
 */
public class ScrollablePopupMenuEvent<T> extends EventObject {
    
    private T object;
    
    public ScrollablePopupMenuEvent(Object source, T selectedObject) {
        super(source);
        this.object = selectedObject;
    }
    
    public T getSelectedObject() {
        return object;
    }
}
