package ise.plugin.svn.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.data.DiffData;
import ise.java.awt.KappaLayout;


public class DiffDialog extends JDialog {

    private View view = null;
    private String path1 = null;
    private String path2 = null;
    private DiffData data = null;

    /**
     * @param view the parent frame
     * @param path the local working file to diff against.
     */
    public DiffDialog(View view, String path) {
        super( ( JFrame ) view, "Diff", true );
        if ( path == null ) {
            throw new IllegalArgumentException( "path may not be null" );
        }
        this.view = view;
        this.path1 = path;

        data = new DiffData();
        data.addPath(path1);

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        final RevisionSelectionPanel rsp = new RevisionSelectionPanel("Diff Against:");

        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        // get revision to diff against
                        data.setRevision1( rsp.getRevision() );

                        DiffDialog.this.setVisible( false );
                        DiffDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        data = null;
                        DiffDialog.this.setVisible( false );
                        DiffDialog.this.dispose();
                    }
                }
                                    );

        JPanel file_panel = new JPanel(new BorderLayout());
        file_panel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "File to diff:" ) );
        file_panel.add(new JLabel(path), BorderLayout.CENTER);
        panel.add(file_panel, "0, 0, 1, 1, W");
        panel.add(KappaLayout.createVerticalStrut(6), "0, 1, 1, 1");
        panel.add(rsp, "0, 2, 1, 1, W, w");
        panel.add(KappaLayout.createVerticalStrut(11), "0, 3, 1, 1");
        panel.add(btn_panel, "0, 4, 1, 1, E");
        setContentPane(panel);
        pack();
    }

    public DiffData getData() {
        return data;
    }
}
