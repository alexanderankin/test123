package com.addictedtor.orchestra;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.af.commons.widgets.MultiLineLabel;
import org.af.commons.widgets.WidgetFactory;
import org.af.commons.widgets.buttons.HorizontalButtonPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FinishedDialog extends JDialog implements ActionListener {
    public FinishedDialog(File shortCutDir, File startScript) {
        super(jEdit.getActiveView());
        setModal(true);
        setTitle("Orchestra installation succesful.");


        JPanel cp = new JPanel();
        String cols = "fill:pref:grow";
        String rows = "pref, 10dlu, pref, 5dlu, pref, 30dlu, pref";
        FormLayout layout = new FormLayout(cols, rows);
        cp.setLayout(layout);
        CellConstraints cc = new CellConstraints();

        MultiLineLabel ta = new MultiLineLabel(
                "Orchestra successfully installed.\nPlease exit JEdit now and do one of the following to start Jedit as an IDE for R:\n" +                "- Click on the created shortcut on your Desktop (recommended)\n" +
                "- Bring up R and type:\n" +
                "  source('<file>')\n" +
                "- Start the script with Rscript\n\n"
        );
        JTextField tf = new JTextField(startScript.getAbsolutePath().replace("\\", "/"));
        tf.setEditable(false);                        

        cp.add(ta, cc.xy(1, 1));
        cp.add( new JLabel("Path to start script:"), cc.xy(1, 3));
        cp.add(tf, cc.xy(1, 5));
        cp.add(new JLabel("Would you like to exit JEdit now?"), cc.xy(1, 7));
        cp = WidgetFactory.makeDialogPanelWithButtons(cp, this);
        setContentPane(cp);
        
        pack();
        setLocationRelativeTo(jEdit.getActiveView());
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(HorizontalButtonPane.OK_CMD)) {
            jEdit.exit(jEdit.getActiveView(), true);
        }
        if (e.getActionCommand().equals(HorizontalButtonPane.CANCEL_CMD)) {
            dispose();
        }
    }
}

