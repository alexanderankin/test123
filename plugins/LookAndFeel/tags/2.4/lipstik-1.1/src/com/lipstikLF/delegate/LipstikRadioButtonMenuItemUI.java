package com.lipstikLF.delegate;


import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseEvent;


public class LipstikRadioButtonMenuItemUI extends LipstikMenuItemUI
{
    protected String getPropertyPrefix() { return "RadioButtonMenuItem"; }
    
    /**
     * Create the UI delegate for the given component
     *
     * @param c The component for which to create the ui delegate
     * @return The created ui delegate
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LipstikRadioButtonMenuItemUI();
    }

    public void processMouseEvent(JMenuItem item, MouseEvent e, MenuElement path[], MenuSelectionManager manager) 
    {
        Point p = e.getPoint();
        if (p.x >= 0 && p.x < item.getWidth() && 
            p.y >= 0 && p.y < item.getHeight()) {
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                manager.clearSelectedPath();
                item.doClick(0);
                item.setArmed(false);
            } else
                manager.setSelectedPath(path);
        } else if (item.getModel().isArmed()) {
            MenuElement newPath[] = new MenuElement[path.length - 1];
            int i, c;
            for (i = 0, c = path.length - 1; i < c; i++)
                newPath[i] = path[i];
            manager.setSelectedPath(newPath);
        }
    }    
    
}
