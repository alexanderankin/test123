package ise.plugin.svn.gui;

// imports
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.browser.VFSBrowser;

import projectviewer.ProjectViewer;
import projectviewer.config.ProjectOptions;
import ise.java.awt.KappaLayout;
import ise.plugin.svn.data.LogData;
import ise.plugin.svn.library.PasswordHandler;
import ise.plugin.svn.library.PasswordHandlerException;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Dialog for getting revision date ranges to use when calling the Log command.
 */
public class LogDialog extends JDialog {
    // instance fields
    private View view = null;
    private LogData data = null;

    private boolean recursive = false;

    private SVNRevision startRevision = SVNRevision.create( 0L );
    private SVNRevision endRevision = SVNRevision.HEAD;

    private boolean cancelled = false;

    public LogDialog( View view, LogData data ) {
        super( ( JFrame ) view, "Log Settings", true );
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.view = view;
        this.data = data;
        _init();
    }

    /** Initialises the option pane. */
    protected void _init() {

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        // list the selected files
        JLabel file_label = new JLabel( "Show log for these files:" );
        final JPanel file_panel = new JPanel( new GridLayout( 0, 1, 2, 3 ) );
        file_panel.setBackground( Color.WHITE );
        file_panel.setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        for ( String path : data.getPaths() ) {
            JCheckBox cb = new JCheckBox( path );
            cb.setSelected( true );
            cb.setBackground( Color.WHITE );
            file_panel.add( cb );
        }

        // ask if directories should be recursed
        final JCheckBox recursive_cb = new JCheckBox( "Recurse subdirectories?" );
        recursive_cb.setSelected( recursive );
        recursive_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        data.setRecursive( recursive_cb.isSelected() );
                    }
                }
                                      );

        // radio buttons to choose revision range, all, by number, or by date
        final JRadioButton show_all = new JRadioButton( "All" );
        show_all.setSelected( true );
        show_all.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        startRevision = SVNRevision.create( 0L );
                        endRevision = SVNRevision.HEAD;
                    }
                }
                                  );

        JRadioButton revision_range = new JRadioButton( "By range:" );

        ButtonGroup revision_group = new ButtonGroup();
        revision_group.add( show_all );
        revision_group.add( revision_range );

        // revision chooser panels
        final RevisionSelectionPanel start_revision_panel = new RevisionSelectionPanel( "Start Revision" );
        final RevisionSelectionPanel end_revision_panel = new RevisionSelectionPanel( "End Revision" );
        start_revision_panel.setEnabled( false );
        end_revision_panel.setEnabled( false );

        revision_range.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        AbstractButton btn = ( AbstractButton ) ae.getSource();
                        start_revision_panel.setEnabled( btn.isSelected() );
                        end_revision_panel.setEnabled( btn.isSelected() );
                    }
                }
                                        );

        show_all.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        AbstractButton btn = ( AbstractButton ) ae.getSource();
                        start_revision_panel.setEnabled( !btn.isSelected() );
                        end_revision_panel.setEnabled( !btn.isSelected() );
                    }
                }
                                  );

        final JSpinner max_logs = new JSpinner();
        ((JSpinner.NumberEditor)max_logs.getEditor()).getModel().setMinimum(new Integer(1));
        ((JSpinner.NumberEditor)max_logs.getEditor()).getModel().setValue(new Integer(100));

        // buttons
        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        // get the paths
                        List<String> paths = new ArrayList<String>();
                        Component[] files = file_panel.getComponents();
                        for ( Component file : files ) {
                            JCheckBox cb = ( JCheckBox ) file;
                            if ( cb.isSelected() ) {
                                paths.add( cb.getText() );
                            }
                        }
                        if ( paths.size() == 0 ) {
                            // nothing to commit, bail out
                            data = null;
                        }
                        else {
                            data.setPaths( paths );
                        }
                        if ( show_all.isSelected() ) {
                            data.setStartRevision( SVNRevision.create( 0L ) );
                            data.setEndRevision( SVNRevision.HEAD );
                        }
                        else {
                            data.setStartRevision( start_revision_panel.getRevision() );
                            data.setEndRevision( end_revision_panel.getRevision() );
                        }
                        data.setMaxLogs(((Integer)max_logs.getValue()).intValue());
                        LogDialog.this.setVisible( false );
                        LogDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        data = null;
                        LogDialog.this.setVisible( false );
                        LogDialog.this.dispose();
                    }
                }
                                    );

        // add the components to the option panel
        panel.add( "0, 0, 2, 1, W,  , 3", file_label );
        panel.add( "0, 1, 2, 1, W, w, 3", new JScrollPane( file_panel ) );
        panel.add( "4, 1, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 120, true ) );

        panel.add( "0, 2, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 3, 2, 1, W,  , 3", recursive_cb );

        panel.add( "0, 4, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 5, 2, 1, W,  , 3", new JLabel( "Revision Range:" ) );
        panel.add( "0, 6, 2, 1, W,  , 3", show_all );
        panel.add( "0, 7, 2, 1, W,  , 3", revision_range );
        panel.add( "0, 8, 1, 1, W,  , 3", start_revision_panel );
        panel.add( "1, 8, 1, 1, E   , 3", end_revision_panel );
        panel.add( "0, 9, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 6, true ) );

        panel.add( "0, 10, 1, 1, E,  , 3", new JLabel( "Maximum log entries to show:" ) );
        panel.add( "1, 10, 1, 1, W,  , 3", max_logs );

        panel.add( "0, 11, 1, 1, 0,  , 0", KappaLayout.createVerticalStrut( 11, true ) );

        panel.add( "0, 12, 2, 1, E,  , 0", btn_panel );

        setContentPane( panel );
        pack();

    }

    public class RevisionSelectionPanel extends JPanel {

        private ButtonGroup bg = null;
        private JRadioButton revision_number_rb = null;
        private JRadioButton date_rb = null;
        private JRadioButton head_rb = null;
        private JRadioButton base_rb = null;
        private JSpinner revision_number = null;
        private JSpinner date_spinner = null;

        private SVNRevision revision = SVNRevision.HEAD;

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

            revision_number_rb.setSelected(true);

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

            date_spinner.setEnabled(false);


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

    public LogData getData() {
        return data;
    }
}
