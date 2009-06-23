/*
Copyright (c) 2002, Dale Anson
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

package ise.plugin.nav;

import java.awt.event.*;
import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * @author Dale Anson
 */
public class OptionPanel extends AbstractOptionPane {

    private static final String name = "navigator";

    private JCheckBox showOnToolbar = null;
    private JCheckBox groupByFile = null;
    private JCheckBox combineLists = null;
    private JCheckBox showLineText = null;
    private JCheckBox showLineTextSyntax = null;
    private JCheckBox showStripes = null;

    private JRadioButton viewScope = null;

    private NumberTextField maxStackSize = null;


    public OptionPanel() {
        super( name );
    }

    public void _init() {
        installComponents();
        installListeners();
    }

    private void installComponents() {
        setName( name );
        setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );

        // title
        addComponent( new JLabel( "<html><h3>Navigator</h3>" ) );
        
        // navigator scope
        addComponent( new JLabel( "Navigator Scope" ) );
        viewScope = new JRadioButton( jEdit.getProperty( "navigator.viewScope.label", "View scope" ) );
        viewScope.setName( "viewScope" );
        JRadioButton editPaneScope = new JRadioButton( jEdit.getProperty( "navigator.editPaneScope.label", "EditPane scope" ) );
        editPaneScope.setName( "editPaneScope" );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( viewScope );
        buttonGroup.add( editPaneScope );
        addComponent( viewScope );
        addComponent( editPaneScope );
        int scope = NavigatorPlugin.getScope();
        viewScope.setSelected( scope == NavigatorPlugin.VIEW_SCOPE );
        editPaneScope.setSelected( scope == NavigatorPlugin.EDITPANE_SCOPE );

        addComponent( Box.createVerticalStrut( 15 ) );
        
        addComponent( new JLabel( "Configuration Options" ) );

        // show on toolbar
        showOnToolbar = new JCheckBox( jEdit.getProperty( "navigator.options.showOnToolbar.label" ) );
        showOnToolbar.setName( "showOnToolbar" );
        showOnToolbar.setSelected( NavigatorPlugin.showOnToolBars() );
        addComponent( showOnToolbar );

        addComponent( Box.createVerticalStrut( 11 ) );

        // group by file
        groupByFile = new JCheckBox( jEdit.getProperty( "navigator.options.groupByFile.label" ) );
        groupByFile.setName( "groupByFile" );
        groupByFile.setSelected( NavigatorPlugin.groupByFile() );
        addComponent( groupByFile );

        addComponent( Box.createVerticalStrut( 11 ) );

        // combine back and forward lists
        combineLists = new JCheckBox( jEdit.getProperty( "navigator.options.combineLists.label", "Combine \"Back\" and \"Forward\" lists" ) );
        combineLists.setName( "combineLists" );
        combineLists.setSelected( NavigatorPlugin.combineLists() );
        addComponent( combineLists );

        // show line text in back and forward lists
        showLineText = new JCheckBox( jEdit.getProperty( "navigator.options.showLineText.label", "Show line text in \"Back\" and \"Forward\" lists" ) );
        showLineText.setName( "showLineText" );
        showLineText.setSelected( jEdit.getBooleanProperty( "navigator.showLineText", true ) );
        addComponent( showLineText );

        // show syntax highlighting in back and forward lists
        showLineTextSyntax = new JCheckBox( jEdit.getProperty( "navigator.options.showLineTextSyntax.label", "Show syntax highlighting in \"Back\" and \"Forward\" lists" ) );
        showLineTextSyntax.setName( "showLineTextSyntax" );
        showLineTextSyntax.setSelected( jEdit.getBooleanProperty( "navigator.showLineText", true ) && jEdit.getBooleanProperty( "navigator.showLineTextSyntax", true ) );
        JPanel syntaxPanel = new JPanel();
        syntaxPanel.add( Box.createHorizontalStrut( 11 ) );
        syntaxPanel.add( showLineTextSyntax );
        addComponent( syntaxPanel );

        // show stripes in back and forward lists
        showStripes = new JCheckBox( jEdit.getProperty( "navigator.options.showStripes.label", "Show stripes in \"Back\" and \"Forward\" lists" ) );
        showStripes.setName( "showStripes" );
        showStripes.setSelected( jEdit.getBooleanProperty( "navigator.showStripes", true ) );
        addComponent( showStripes );

        // max stack size
        addComponent( Box.createVerticalStrut( 11 ) );
        maxStackSize = new NumberTextField();
        maxStackSize.setName( "maxStackSize" );
        maxStackSize.setMinValue( 1 );
        maxStackSize.setValue( jEdit.getIntegerProperty( "maxStackSize", 512 ) );
        addComponent( jEdit.getProperty( "navigator.maxStackSize.label", "Maximum history size:" ), maxStackSize );
    }

    private void installListeners() {
        showLineText.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    showLineTextSyntax.setEnabled( showLineText.isSelected() );
                }
            }
        );
    }

    public void _save() {
        jEdit.setBooleanProperty( name + ".groupByFile", groupByFile.isSelected() );
        jEdit.setBooleanProperty( NavigatorPlugin.showOnToolBarKey, showOnToolbar.isSelected() );
        jEdit.setBooleanProperty( "navigator.combineLists", combineLists.isSelected() );
        jEdit.setBooleanProperty( "navigator.showLineText", showLineText.isSelected() );
        jEdit.setBooleanProperty( "navigator.showLineTextSyntax", showLineTextSyntax.isSelected() );
        jEdit.setBooleanProperty( "navigator.showStripes", showStripes.isSelected() );
        jEdit.setIntegerProperty( name + ".maxStackSize", maxStackSize.getValue() );
        int scope;
        if ( viewScope.isSelected() ) {
            scope = NavigatorPlugin.VIEW_SCOPE;
        }
        else {
            scope = NavigatorPlugin.EDITPANE_SCOPE;
        }
        jEdit.setIntegerProperty( name + ".scope", scope );
    }
}