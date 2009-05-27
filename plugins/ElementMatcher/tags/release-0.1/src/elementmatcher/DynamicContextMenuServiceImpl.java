package elementmatcher;

import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.jedit.gui.DynamicContextMenuService;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicContextMenuServiceImpl extends DynamicContextMenuService {

    private static final JMenuItem NO_ACTIONS_MENU_ITEM = new JMenuItem(new NoActionsAction());

    public JMenuItem[] createMenu(JEditTextArea jEditTextArea, MouseEvent mouseEvent) {
        if (mouseEvent == null) {
            return null;
        }
        final int offset = jEditTextArea.xyToOffset(mouseEvent.getX(), mouseEvent.getY());
        if (offset == -1) {
            return null;
        }
        return getElementsMenuItems(jEditTextArea.getBuffer(), offset);
    }

    public JMenuItem[] getElementsMenuItems(JEditBuffer buffer, int offset) {
        final Iterator<Element<?>> elements = ElementMatcherPlugin.getInstance().getElementManager(buffer).findElement(offset);
        if (!elements.hasNext()) {
            return null;
        }
        final List<JMenuItem> menuItems = new ArrayList<JMenuItem>();
        do {
            menuItems.add(createElementMenu(elements.next()));
        } while (elements.hasNext());
        return menuItems.toArray(new JMenuItem[menuItems.size()]);
    }

    private JMenuItem createElementMenu(Element<?> element) {
        final JMenuItem menu = new JMenu(element.getToolTip());
        final Iterator<Action> actions = element.getActions();
        if (!actions.hasNext()) {
            menu.add(NO_ACTIONS_MENU_ITEM);
            return menu;
        }
        do {
            final Action action = actions.next();
            menu.add(new JMenuItem(action));
        } while (actions.hasNext());
        return menu;
    }

    private static class NoActionsAction extends AbstractAction {

        private NoActionsAction() {
            super("No actions");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
        }

    }

}