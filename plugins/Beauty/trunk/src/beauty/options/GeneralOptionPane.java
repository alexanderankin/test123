package beauty.options;

import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.msg.*;

import ise.java.awt.*;

/**
 * General beautifier options.
 *
 */
public class GeneralOptionPane extends AbstractOptionPane {

    private JCheckBox showErrorDialogs;

    public GeneralOptionPane() {
        super("beauty.general");
    }

    // called when this class is first accessed
    public void _init() {
        installComponents();
    }


    // create the user interface components and do the layout
    private void installComponents() {
        setLayout(new KappaLayout());
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // create the components
        JLabel description = new JLabel("<html><b>General Options");

        showErrorDialogs = new JCheckBox("Show error dialogs");
        showErrorDialogs.setSelected(jEdit.getBooleanProperty("beauty.general.showErrorDialogs", true));

        add("0, 0, 1, 1, W, w, 3", description);
        add("0, 1, 1, 1, W, w, 3", showErrorDialogs);
    }

    public void _save() {
        jEdit.setBooleanProperty("beauty.general.showErrorDialogs", showErrorDialogs.isSelected());
    }
}