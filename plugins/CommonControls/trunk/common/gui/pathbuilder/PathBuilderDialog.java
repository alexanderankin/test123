package common.gui.pathbuilder;

/*
 * PathBuilderDialog.java
 * Part of the JSwat plugin for the jEdit text editor
 * Copyright (C) 2001 David Taylor
 * dtaylo11@bigpond.net.au
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;
import org.gjt.sp.jedit.gui.EnhancedDialog;

/**
 * A generic dialog to hold a PathBuilder component.<p>
 *
 * This dialog can be used from anywhere to allow the user to create a path.
 * The dialog should be initialised with the current value of the path, and
 * if the getResult method returns true, the getPath method will return
 * the value of the path created by the user.<p>
 *
 * Usage:<p><code><pre>
 *
 * String classpath = ...;
 * dialog = new PathBuilderDialog(parent, "Build Classpath", classPath);
 * dialog.pack();
 * dialog.setLocationRelativeTo(parent);
 * dialog.show();
 * if(dialog.getResult())
 *     classpath = dialog.getPath();
 * </pre></code>
 */
public class PathBuilderDialog extends JDialog implements ActionListener {
    /**
     * true if okay button pressed, otherwise false.
     */
    private boolean result = false;

    /**
     * The PathBuilder component.
     */
    private PathBuilder pathBuilder;

    /**
     * Create a new PathBuilderDialog.<p>
     *
     * @param parent the parent of the dialog.
     * @param title the title to give the dialog.
     */
    public PathBuilderDialog(Dialog parent, String title) {
        super(parent, title, true);
        init();
    }

    /**
     * Create a new PathBuilderDialog.<p>
     *
     * @param parent the parent of the dialog.
     * @param title the title to give the dialog.
     */
    public PathBuilderDialog(Frame parent, String title) {
        super(parent, title, true);
        init();
    }

    /**
     * Create the dialog controls.<p>
     */
    private void init() {
        Panel panel = new Panel();
        JButton jok = new JButton("Ok");
        JButton jcancel = new JButton("Cancel");
        panel.add(jok);
        panel.add(jcancel);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        pathBuilder = new PathBuilder();
        contentPane.add(pathBuilder, BorderLayout.CENTER);

        contentPane.add(panel, BorderLayout.SOUTH);
        jok.addActionListener(this);
        jcancel.addActionListener(this);
    }

    /**
     * Determine whether the user pressed okay or cancel.<p>
     *
     * @return true if the user pressed okay, otherwise false.
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Returns the path builder component.<p>
     *
     * @return the path builder component.
     */
    public PathBuilder getPathBuilder() {
        return pathBuilder;
    }

    /**
     * Process button presses.<p>
     *
     * @param event a GUI event.
     */
    public void actionPerformed(ActionEvent event) {
        if(event.getActionCommand().equalsIgnoreCase("ok")) {
            result = true;
            dispose();
        } else if(event.getActionCommand().equalsIgnoreCase("cancel")) {
            dispose();
        }
    }
}

