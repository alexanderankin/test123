package ise.plugin.svn.gui;

import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.View;

import ise.plugin.svn.data.DeleteData;
import ise.java.awt.KappaLayout;


public class DeleteDialog extends JDialog {

    private View view = null;
    private List<String> paths = null;
    private DeleteData data = null;

    /**
     * @param view the parent frame
     * @param path the local working file to diff against.
     */
    public DeleteDialog(View view, DeleteData data) {
        super( ( JFrame ) view, "Delete", true );
        if ( data == null ) {
            throw new IllegalArgumentException( "data may not be null" );
        }
        this.view = view;
        this.data = data;

        JPanel panel = new JPanel( new KappaLayout() );
        panel.setBorder( new EmptyBorder( 6, 6, 6, 6 ) );

        final JCheckBox force_cb = new JCheckBox("Force");
        force_cb.setSelected(data.getForce());
        final JCheckBox dry_run_cb = new JCheckBox("Dry run");
        dry_run_cb.setSelected(data.getDryRun());
        final JCheckBox delete_files_cb = new JCheckBox("Delete files from file system");
        delete_files_cb.setSelected(data.getDeleteFiles());

        KappaLayout kl = new KappaLayout();
        JPanel btn_panel = new JPanel( kl );
        JButton ok_btn = new JButton( "Ok" );
        JButton cancel_btn = new JButton( "Cancel" );
        btn_panel.add( "0, 0, 1, 1, 0, w, 3", ok_btn );
        btn_panel.add( "1, 0, 1, 1, 0, w, 3", cancel_btn );
        kl.makeColumnsSameWidth( 0, 1 );

        ok_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        getData().setForce(force_cb.isSelected());
                        getData().setDryRun(dry_run_cb.isSelected());
                        getData().setDeleteFiles(delete_files_cb.isSelected());
                        DeleteDialog.this.setVisible( false );
                        DeleteDialog.this.dispose();
                    }
                }
                                );

        cancel_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        DeleteDialog.this.data = null;
                        DeleteDialog.this.setVisible( false );
                        DeleteDialog.this.dispose();
                    }
                }
                                    );

        panel.add(force_cb, "0, 0, 1, 1, W, w, 3");
        panel.add(dry_run_cb, "0, 1, 1, 1, W, w, 3");
        panel.add(delete_files_cb, "0, 2, 1, 1, W, w, 3");
        panel.add(KappaLayout.createVerticalStrut(6), "0, 3, 1, 1");
        panel.add(btn_panel, "0, 4, 1, 1, E");
        setContentPane(panel);
        pack();
    }

    public DeleteData getData() {
        return data;
    }
}
