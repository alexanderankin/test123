package jdiff.component.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import jdiff.component.*;

public class BasicDiffLocalOverviewUI extends DiffLocalOverviewUI {

    private DiffLocalOverview diffLocalOverview;

    public static ComponentUI createUI(JComponent c) {
        return new BasicDiffLocalOverviewUI();
    }

    public void installUI(JComponent c) {
        diffLocalOverview = (DiffLocalOverview) c;
        installDefaults();
        installComponents();
        installListeners();

        c.setLayout(createLayoutManager());
        c.setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    public void uninstallUI(JComponent c) {
        c.setLayout(null);
        uninstallListeners();
        uninstallComponents();
        uninstallDefaults();

        diffLocalOverview = null;
    }

    public void installDefaults() {

    }

    public void installComponents() {

    }

    public void installListeners() {

    }

    public void uninstallDefaults() {

    }

    public void uninstallComponents() {

    }

    public void uninstallListeners() {

    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        this.paintOverview(g);
    }

    protected void paintOverview(Graphics g) {

    }


    protected LayoutManager createLayoutManager() {
        return new DiffLocalOverviewLayout();
    }

    protected class DiffLocalOverviewLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            return new Dimension(0, 0);
        }

        public Dimension minimumLayoutSize(Container c) {
            return this.preferredLayoutSize(c);
        }

        public void layoutContainer(Container c) {

        }
    }
}
