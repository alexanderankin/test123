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

import java.awt.Dimension;
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
    private JRadioButton head_rb = new JRadioButton( "HEAD" );
    private JRadioButton base_rb = new JRadioButton( "BASE" );
    private JRadioButton working_rb = new JRadioButton( "WORKING" );
    private JRadioButton revision_number_rb = new JRadioButton( "Revision:" );
    private JSpinner revision_number = null;
    private JRadioButton date_rb = new JRadioButton( "Date:" );
    private JSpinner date_spinner = null;


    private String title;
    private int direction = SwingConstants.VERTICAL;
    private boolean showHead = true;
    private boolean showWorking = false;
    private boolean showBase = true;
    private boolean showDate = true;
    private boolean showNumber = true;

    private SVNRevision defaultRevision;
    private SVNRevision revision = SVNRevision.HEAD;


    /**
     * Revision selection panel with a vertical layout and doesn't show the
     * "working" revision choice.
     * @param title this panel is displayed in an etched border with a title.
     */
    public RevisionSelectionPanel( String title ) {
        this( title, SwingConstants.VERTICAL, false );
    }

    public RevisionSelectionPanel( String title, int direction, boolean showWorking ) {
        this.title = title;
        this.direction = direction;
        this.showWorking = showWorking;
        init();
    }

    public RevisionSelectionPanel(String title, int direction, boolean showHead, boolean showBase, boolean showNumber, boolean showDate, boolean showWorking ) {
        this.title = title;
        this.direction = direction;
        this.showHead = showHead;
        this.showBase = showBase;
        this.showNumber = showNumber;
        this.showDate = showDate;
        this.showWorking = showWorking;
        init();
    }

    public void init() {
        KappaLayout kl = new KappaLayout();
        setLayout( kl );
        switch ( direction ) {
            case SwingConstants.HORIZONTAL:
            case SwingConstants.VERTICAL:
                break;
            default:
                direction = SwingConstants.VERTICAL;
        }

        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), title ) );

        bg = new ButtonGroup();
        bg.add( revision_number_rb );
        bg.add( date_rb );
        bg.add( head_rb );
        bg.add( base_rb );
        bg.add( working_rb );

        if ( SVNRevision.BASE.equals( defaultRevision ) ) {
            base_rb.setSelected( true );
        }
        else if ( showWorking && SVNRevision.WORKING.equals( defaultRevision ) ) {
            working_rb.setSelected( true );
        }
        else {
            head_rb.setSelected( true );
        }

        // set up the revision number chooser
        revision_number = getRevisionChooser();

        // set up date chooser
        date_spinner = getDateChooser();

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
        if ( showWorking ) {
            working_rb.addActionListener( al );
        }

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
        if ( showWorking ) {
            working_rb.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent ae ) {
                            RevisionSelectionPanel.this.setRevision( SVNRevision.WORKING );
                        }
                    }
                                        );
        }

        revision_number.setEnabled( false );
        date_spinner.setEnabled( false );

        if ( direction == SwingConstants.HORIZONTAL ) {
            add( "0, 0, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );
            if ( showHead ) {
                add( "0, 1, 1, 1, W, , 3", head_rb );
            }
            if ( showBase ) {
                add( "0, 2, 1, 1, W, , 3", base_rb );
            }
            if ( showWorking ) {
                add( "0, 3, 1, 1, W, , 3", working_rb );
            }
            add( "1, 1, 1, 1, 0,  , 0", KappaLayout.createHorizontalStrut( 21, true ) );
            if ( showNumber ) {
                add( "2, 1, 1, 1, W, , 3", revision_number_rb );
                add( "3, 1, 1, 1, W, , 3", getRevisionChooser() );
            }
            if ( showDate ) {
                add( "2, 2, 1, 1, W, , 3", date_rb );
                add( "3, 2, 1, 1, W, , 3", getDateChooser() );
            }
        }
        else {
            add( "0, 0, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );
            if ( showHead ) {
                add( "0, 1, 2, 1, W, , 3", head_rb );
            }
            if ( showBase ) {
                add( "0, 2, 2, 1, W, , 3", base_rb );
            }
            if ( showWorking ) {
                add( "0, 3, 2, 1, W, , 3", working_rb );
            }
            if ( showNumber ) {
                add( "0, 4, 1, 1, W, w, 3", revision_number_rb );
                add( "1, 4, 1, 1, W, , 3", getRevisionChooser() );
            }
            if ( showDate ) {
                add( "0, 5, 1, 1, W, , 3", date_rb );
                add( "1, 5, 1, 1, W, , 3", getDateChooser() );
            }
        }
    }

    private JSpinner getRevisionChooser() {
        SpinnerNumberModel model = new SpinnerNumberModel();
        model.setMinimum( 0 );
        revision_number = new JSpinner( model );
        revision_number.setPreferredSize( new Dimension( 150, revision_number.getPreferredSize().height ) );
        revision_number.setEnabled( false );
        revision_number.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent ce ) {
                        if ( RevisionSelectionPanel.this.revision_number.isEnabled() ) {
                            Number number = ( Number ) revision_number.getValue();
                            RevisionSelectionPanel.this.revision = SVNRevision.create( number.longValue() );
                        }
                    }
                }
                                         );
        return revision_number;
    }


    private JSpinner getDateChooser() {
        Calendar calendar = Calendar.getInstance();
        Date initDate = calendar.getTime();
        Date latestDate = calendar.getTime();
        calendar.add( Calendar.YEAR, -10 );
        Date earliestDate = calendar.getTime();
        SpinnerDateModel model = new SpinnerDateModel( initDate, earliestDate, latestDate, Calendar.DAY_OF_MONTH );
        date_spinner = new JSpinner( model );
        JSpinner.DateEditor date_editor = new JSpinner.DateEditor( date_spinner, "dd MMM yyyy HH:mm" );
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
        date_spinner.setPreferredSize( new Dimension( 150, date_spinner.getPreferredSize().height ) );
        date_spinner.setEnabled( false );
        return date_spinner;
    }

    public void setRevision( SVNRevision revision ) {
        this.revision = revision;
        if ( revision.getNumber() != -1 ) {
            revision_number.getModel().setValue(revision.getNumber());
        }
        else if ( revision.getDate() != null ) {
            date_spinner.getModel().setValue(revision.getDate());
        }
    }

    public SVNRevision getRevision() {
        return revision;
    }

    @Override
    public void setEnabled( boolean b ) {
        super.setEnabled( b );
        head_rb.setEnabled( b );
        base_rb.setEnabled( b );
        revision_number_rb.setEnabled( b );
        date_rb.setEnabled( b );
        working_rb.setEnabled( b );
        revision_number.setEnabled( revision_number_rb.isSelected() );
        date_spinner.setEnabled( date_rb.isSelected() );
    }

    // for testing
    public static void main ( String[] args ) {
        RevisionSelectionPanel panel = new RevisionSelectionPanel( "some title" );
        panel.setEnabled(false);
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setVisible( true );
        SVNRevision revision = panel.getRevision();
        System.out.println( "+++++ RSP, revision = " + revision );
    }
}
