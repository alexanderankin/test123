/*
Copyright (c) 2012, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package tasklist.options;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

import ise.java.awt.KappaLayout;

import projectviewer.vpt.VPTProject;

public class PVOptionPane extends AbstractOptionPane {

    private VPTProject project;
    private static final String internalName = "tasklist.pv.options";

    private JCheckBox autoscan;

    public PVOptionPane( VPTProject project ) {
        super( internalName );
        this.project = project;
        setLayout( new KappaLayout() );
    }

    /** Initialises the option pane. */
    protected void _init() {
        initComponents();
    }

    private void initComponents() {
        setBorder( BorderFactory.createEmptyBorder(11, 11, 11, 11 ) );

        autoscan = new JCheckBox( jEdit.getProperty("tasklist.pv.autoscan", "Automatically scan for tasks when project opens") );
        autoscan.setSelected( jEdit.getBooleanProperty( "tasklist.pv.autoscan." + project.getName(), true ) );

        add( "0, 0, 1, 1, W, wh, 3", autoscan );
    }


    /** Saves properties from the option pane. */
    protected void _save() {
        jEdit.setBooleanProperty("tasklist.pv.autoscan." + project.getName(), autoscan.isSelected());
    }

}