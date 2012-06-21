package ise.plugin.nav;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.msg.*;


public class NavToolBar extends JToolBar {
    private JButton back;
    private JButton forward;

    public NavToolBar(Navigator client) {
        super();
        installComponents(client);
        installListeners(client);
    }
    
    public void installComponents(Navigator client) {
        back = new SquareButton(GUIUtilities.loadIcon("ArrowL.png"));
        back.setToolTipText(jEdit.getProperty("navigator.back.label", "Back"));
        forward = new SquareButton(GUIUtilities.loadIcon("ArrowR.png"));
        forward.setToolTipText(jEdit.getProperty("navigator.forward.label", "Forward"));
        updateComponents(client);
        add(back);
        add(forward);
    }
    
    public void installListeners(final Navigator client) {
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    NavHistoryPopup popup = NavigatorPlugin.backList(client.getView());
                    GUIUtilities.showPopupMenu(popup, back, me.getX(), me.getY());
                }
            }
        } );

        forward.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    NavHistoryPopup popup = NavigatorPlugin.forwardList(client.getView());
                    GUIUtilities.showPopupMenu(popup, forward, me.getX(), me.getY());
                }
            }
        } );
    }
    
    public void updateComponents(Navigator client) {
        back.setModel(client.getBackModel());
        forward.setModel(client.getForwardModel());
    }
}