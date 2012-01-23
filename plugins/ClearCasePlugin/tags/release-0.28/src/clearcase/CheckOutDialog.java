package clearcase;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import java.awt.Dimension;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.util.StringTokenizer;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.util.Log;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class CheckOutDialog extends JDialog implements ActionListener, FocusListener
{
    /**
    * The exit value for this dialog "Cancel" or "Ok"
    */
    String exitValue = "Cancel";
    String comment = "";
	public static final String DEFAULT_COMMENT = "I am modifying this because...";
	
    View view;
    BorderLayout mainPanelLayout = new BorderLayout();
    TitledBorder titledBorder1;
    BorderLayout centerPanelLayout = new BorderLayout();
    JPanel mainPanel = new JPanel();
    BorderLayout borderLayout3 = new BorderLayout();
    JPanel centerPanel = new JPanel();
    BorderLayout borderLayout4 = new BorderLayout();
    JPanel southPanel = new JPanel();
    JButton buttonCancel = new JButton();
    JButton buttonOk = new JButton();
    JCheckBox keepCheckOut = new JCheckBox();
    JTextArea commentText = new JTextArea();

    public CheckOutDialog(View view)
    {
		super(view, "Checkout", true);
        this.view = view;
		view.showWaitCursor();
        init();
		pack();
		setLocationRelativeTo(view);
		view.hideWaitCursor();
		show();
    }
/*
    public static void main(String[] args)
    {
        CheckOutDialog CheckOutDialog = new CheckOutDialog();
        CheckOutDialog.pack();
        CheckOutDialog.show();
        System.exit(0);
    }
*/
    private void init()
    {
        titledBorder1 = new TitledBorder("Checkout Comment");
        setContentPane(mainPanel);
        
        mainPanel.setLayout(borderLayout3);
        centerPanel.setLayout(centerPanelLayout);
        centerPanel.setBorder(titledBorder1);
        southPanel.setPreferredSize(new Dimension(40, 40));

        commentText.setText(DEFAULT_COMMENT);
        commentText.setTabSize(0);
		commentText.addFocusListener(this);
		
        buttonOk.setText("Ok");
        buttonOk.addActionListener(this);
        buttonOk.setPreferredSize(new Dimension(73, 27));
        
        buttonCancel.setText("Cancel");
        buttonCancel.addActionListener(this);
        buttonCancel.setPreferredSize(new Dimension(73, 27));
        
        mainPanel.setPreferredSize(new Dimension(200, 80));
        keepCheckOut.setText("Reserved");
        
        mainPanel.add(centerPanel,  BorderLayout.CENTER);
        centerPanel.add(commentText, BorderLayout.CENTER);
        centerPanel.add(keepCheckOut,  BorderLayout.SOUTH);
        
        mainPanel.add(southPanel,  BorderLayout.SOUTH);
        mainPanel.setPreferredSize(new Dimension(300, 200));
        southPanel.add(buttonOk, null);
        southPanel.add(buttonCancel, null);
    }

    public String getComment()
    {
        return comment;
    }
    
    public boolean getReserved()
    {
        return keepCheckOut.isSelected();
    }
    
    public String getExitValue()
    {
        return exitValue;
    }
    
    public void actionPerformed(ActionEvent event) 
    {
        String command = event.getActionCommand();

        if(command.equals("Ok"))
        {
            StringTokenizer newlineStripper = new StringTokenizer (commentText.getText(), "\n");
			StringBuffer strippedComment = new StringBuffer();
			while (newlineStripper.hasMoreElements())
			{
				strippedComment.append(newlineStripper.nextElement() + " ");
			}

			comment = strippedComment.toString();
            exitValue = "Ok";
            dispose();
        }
        else
        {
            dispose();
        }
    }
	
	public void focusGained(FocusEvent evt) 
	{
		String tempComment = commentText.getText();
		if(tempComment.equals (DEFAULT_COMMENT))
		{
			commentText.selectAll();
		}	
    }

    public void focusLost(FocusEvent evt) 
	{
    }
}