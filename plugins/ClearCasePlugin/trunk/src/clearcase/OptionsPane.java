package clearcase;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class OptionsPane extends AbstractOptionPane implements ActionListener
{
    JPanel mainPanel = new JPanel();
    JLabel pathLabel = new JLabel();
    JTextField pathField = new JTextField();
    JLabel reloadDelayLabel = new JLabel();
    SpinnerNumberModel theModel = new SpinnerNumberModel (3, 0, 20, 1);
	JSpinner reloadDelayField = new JSpinner(theModel);
    JButton saveButton = new JButton();
	JPanel reloadDelayPanel = new JPanel();
	
    public OptionsPane()
    {
        super("ClearCase");
    }
/*
    public static void main(String[] args)
    {
        OptionsPane options = new OptionsPane();
        OptionsPane.pack();
        OptionsPane.show();
        System.exit(0);
    }
*/
    protected void _init()
    {
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(350, 200));
        pathLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        pathLabel.setText("ClearTool Path (leave empty if in path)");
        pathLabel.setBounds(new Rectangle(18, 13, 317, 17));
        pathField.setBounds(new Rectangle(18, 32, 267, 25));
        
        reloadDelayLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        reloadDelayLabel.setText("File Reload Delay (seconds)");
		reloadDelayPanel.setLayout(new BorderLayout());
        reloadDelayPanel.setBounds(new Rectangle(18, 59, 267, 25));
        reloadDelayPanel.add(reloadDelayField, BorderLayout.EAST);
        reloadDelayPanel.add(reloadDelayLabel, BorderLayout.WEST);
        
        String path = jEdit.getProperty("clearcase.path");
        String reloadDelay = jEdit.getProperty("clearcase.reloadDelay");
        /*
        // Get CLEARCASE_ROOT from environment not possible... java -D= is
        if (path == null || "".equals(path))
        {
            path = System.getProperty("CLEARCASE_ROOT");
        }
        */
        pathField.setText(path);
        reloadDelayField.setValue(new Integer(reloadDelay));

        saveButton.setText("...");
        saveButton.setBounds(new Rectangle(288, 33, 36, 24));
        saveButton.setActionCommand("Browse");
        saveButton.addActionListener(this);
        
        mainPanel.add(pathField, null);
        mainPanel.add(pathLabel, null);
        mainPanel.add(reloadDelayPanel, null);
        mainPanel.add(saveButton, null);
        addComponent(mainPanel);
    }
    
    protected void _save()
    {
        jEdit.setProperty("clearcase.path", pathField.getText());
        try
		{	// Attempt to parse the value to see if it's valid. If not, don't save it and send an error message to the log.
			jEdit.setProperty("clearcase.reloadDelay", reloadDelayField.getValue().toString());
		}
		catch (NumberFormatException ex)
		{
            Log.log(Log.ERROR, this, "clearcase.reloadDelay = " + reloadDelayField.getValue() + " caused exception. " + ex);
		}

    }
    
    public void actionPerformed(ActionEvent evt) 
    {
        String command = evt.getActionCommand();
        
        if ( command.indexOf("Browse") != -1 ) 
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) 
            {
                pathField.setText(chooser.getSelectedFile().getPath());
            }
        }
    }
            
}