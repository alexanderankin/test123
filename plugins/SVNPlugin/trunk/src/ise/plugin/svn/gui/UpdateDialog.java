package ise.plugin.svn.gui;

import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.data.UpdateData;
import ise.java.awt.KappaLayout;


public class UpdateDialog extends JDialog {

    private View view = null;
    private List<String> paths = null;
    private UpdateData data = null;

    /**
     * @param view the parent frame
     * @param path the local working file to diff against.
     */
    public UpdateDialog(View view, UpdateData data) {
        super( ( JFrame ) view, "Update", true );
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.view = view;
        this.data = data;

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        final RevisionSelectionPanel rsp = new RevisionSelectionPanel("Update To:");

        final JCheckBox recursive_cb = new JCheckBox("Recursive");
        recursive_cb.setSelected(data.getRecursive());

        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        // get revision to update to
                        getData().setSVNRevision( rsp.getRevision() );

                        getData().setRecursive(recursive_cb.isSelected());

                        UpdateDialog.this.setVisible( false );
                        UpdateDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        UpdateDialog.this.data = null;
                        UpdateDialog.this.setVisible( false );
                        UpdateDialog.this.dispose();
                    }
                }
                                    );

        panel.add(rsp, "0, 0, 1, 1, W, w");
        panel.add(KappaLayout.createVerticalStrut(6), "0, 1, 1, 1");
        panel.add(recursive_cb, "0, 2, 1, 1, W, w, 3");
        panel.add(KappaLayout.createVerticalStrut(11), "0, 3, 1, 1");
        panel.add(btn_panel, "0, 4, 1, 1, E");
        setContentPane(panel);
        pack();
    }

    public UpdateData getData() {
        return data;
    }
}
