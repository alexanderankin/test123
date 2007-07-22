/*
Copyright (c) 2007, Dale Anson
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


package ise.plugin.svn.gui;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import ise.java.awt.KappaLayout;

import org.tmatesoft.svn.core.wc.SVNRevision;


/**
 * A panel to let the user pick a revision by number or date.  HEAD and BASE
 * are also offered as choices.
 */
public class RevisionSelectionPanel extends JPanel {

    private ButtonGroup bg = null;
    private JRadioButton revision_number_rb = null;
    private JRadioButton date_rb = null;
    private JRadioButton head_rb = null;
    private JRadioButton base_rb = null;
    private JSpinner revision_number = null;
    private JSpinner date_spinner = null;

    private SVNRevision revision = SVNRevision.HEAD;

    /**
     * @param title this panel is displayed in an etched border with a title.
     */
    public RevisionSelectionPanel( String title ) {
        super( new KappaLayout() );
        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title ) );

        revision_number_rb = new JRadioButton( "Revision number:" );
        date_rb = new JRadioButton( "Date:" );
        head_rb = new JRadioButton( "HEAD" );
        base_rb = new JRadioButton( "BASE" );

        bg = new ButtonGroup();
        bg.add( revision_number_rb );
        bg.add( date_rb );
        bg.add( head_rb );
        bg.add( base_rb );

        head_rb.setSelected(true);

        // set up the revision number chooser
        revision_number = new JSpinner();
        revision_number.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent ce ) {
                        if ( RevisionSelectionPanel.this.revision_number.isEnabled() ) {
                            Number number = ( Number ) revision_number.getValue();
                            RevisionSelectionPanel.this.revision = SVNRevision.create( number.longValue() );
                        }
                    }
                }
                                         );

        // set up date chooser
        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        Date latestDate = calendar.getTime();
        calendar.add( Calendar.YEAR, -10 );
        Date earliestDate = calendar.getTime();
        SpinnerDateModel model = new SpinnerDateModel( initDate, earliestDate, latestDate, Calendar.DAY_OF_MONTH );
        date_spinner = new JSpinner( model );
        JSpinner.DateEditor date_editor = new JSpinner.DateEditor( date_spinner, "dd/MM/yyyy hh:mm" );
        date_spinner.setEditor( date_editor );
        date_spinner.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent ce ) {
                        if ( RevisionSelectionPanel.this.date_spinner.isEnabled() ) {
                            Date date = ( Date ) date_spinner.getValue();
                            RevisionSelectionPanel.this.setRevision( SVNRevision.create( date ) );
                        }
                    }
                }
                                      );

        // add action listeners
        ActionListener al = new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        revision_number.setEnabled ( RevisionSelectionPanel.this.revision_number_rb.isSelected() );
                        date_spinner.setEnabled ( RevisionSelectionPanel.this.date_rb.isSelected() );
                    }
                };

        revision_number_rb.addActionListener( al );
        date_rb.addActionListener( al );
        head_rb.addActionListener( al );
        base_rb.addActionListener( al );

        revision_number_rb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        RevisionSelectionPanel.this.revision_number.setEnabled( RevisionSelectionPanel.this.revision_number_rb.isSelected() );
                        if ( RevisionSelectionPanel.this.revision_number.isEnabled() ) {
                            Number number = ( Number ) revision_number.getValue();
                            RevisionSelectionPanel.this.setRevision( SVNRevision.create( number.longValue() ) );
                        }
                    }
                }
                                            );
        date_rb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        RevisionSelectionPanel.this.date_spinner.setEnabled( RevisionSelectionPanel.this.date_rb.isSelected() );
                        if ( RevisionSelectionPanel.this.date_spinner.isEnabled() ) {
                            Date date = ( Date ) date_spinner.getValue();
                            RevisionSelectionPanel.this.setRevision( SVNRevision.create( date ) );
                        }
                    }
                }
                                 );
        head_rb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        RevisionSelectionPanel.this.setRevision( SVNRevision.HEAD );
                    }
                }
                                 );
        base_rb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        RevisionSelectionPanel.this.setRevision( SVNRevision.BASE );
                    }
                }
                                 );

        date_spinner.setEnabled( false );


        add( "0, 0, 2, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );
        add( "0, 1, 1, 1, W,  , 3", revision_number_rb );
        add( "1, 1, 1, 1, W, w, 3", revision_number );
        add( "0, 2, 1, 1, W,  , 3", date_rb );
        add( "1, 2, 1, 1, W, w, 3", date_spinner );
        add( "0, 3, 2, 1, W,  , 3", head_rb );
        add( "0, 4, 2, 1, W,  , 3", base_rb );

    }

    public void setRevision( SVNRevision revision ) {
        this.revision = revision;
    }

    public SVNRevision getRevision() {
        return revision;
    }

    @Override
    public void setEnabled( boolean b ) {
        super.setEnabled( b );
        base_rb.setEnabled( b );
        date_rb.setEnabled( b );
        date_spinner.setEnabled( false );
        head_rb.setEnabled( b );
        revision_number.setEnabled( b );
        revision_number_rb.setEnabled( b );
    }
}
