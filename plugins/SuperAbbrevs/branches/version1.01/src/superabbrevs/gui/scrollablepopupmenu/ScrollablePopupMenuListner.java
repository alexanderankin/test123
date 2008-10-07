/*
 * ScrollablePopupMenuListner.java
 *
 * Created on 16. juni 2007, 21:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package superabbrevs.gui.scrollablepopupmenu;

import java.util.EventListener;

/**
 *
 * @author Sune Simonsen
 */
public interface ScrollablePopupMenuListner<T> extends EventListener {
    public void selectedMenuItem(ScrollablePopupMenuEvent<T> event);
}
