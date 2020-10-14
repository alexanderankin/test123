/*
 * Created by JFormDesigner on Sun Sep 09 17:05:03 EDT 2018
 */

package com.illcode.jedit.inputreplace;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jesse Pavel
 */
public class OptionPanel extends JPanel {
    public OptionPanel() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label2 = new JLabel();
        lengthSpinner = new JSpinner();

        //======== this ========
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

        //---- label2 ----
        label2.setText("Maximum Replace Length:");
        add(label2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

        //---- lengthSpinner ----
        lengthSpinner.setModel(new SpinnerNumberModel(3, 2, 24, 1));
        add(lengthSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    JLabel label2;
    JSpinner lengthSpinner;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
