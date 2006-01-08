/******************************************************************************
*	Copyright 2002 BITart Gerd Knops. All rights reserved.
*
*	Project	: CodeBrowser
*	File	: CodeBrowserOptionPane.java
*	Author	: Gerd Knops gerti@BITart.com
*
*******************************************************************************
*                                    :mode=java:folding=indent:collapseFolds=1:
*	History:
*	020511 Creation of file
*
*******************************************************************************
*
*	Description:
*	Simple option pane that lets the user set the path to the ctags binary
*
*	$Id$
*
*******************************************************************************
*
* DISCLAIMER
*
* BITart and Gerd Knops make no warranties, representations or commitments
* with regard to the contents of this software. BITart and Gerd Knops
* specifically disclaim any and all warranties, wether express, implied or
* statutory, including, but not limited to, any warranty of merchantability
* or fitness for a particular purpose, and non-infringement. Under no
* circumstances will BITart or Gerd Knops be liable for loss of data,
* special, incidental or consequential damages out of the use of this
* software, even if those damages were forseeable, or BITart or Gerd Knops
* was informed of their potential.
*
******************************************************************************/
package com.bitart.codebrowser;
/******************************************************************************
* Imports
******************************************************************************/

	import java.awt.*;
	import java.awt.event.*;
	import javax.swing.*;
	import javax.swing.border.*;
	
	import org.gjt.sp.jedit.*;
	
/*****************************************************************************/
public class CodeBrowserOptionPane extends AbstractOptionPane 
{
/******************************************************************************
* Vars
******************************************************************************/

    private JTextField     ctagsPathTF;
		private JTextField		parserHistoryTextField;
	
/******************************************************************************
* Factory methods
******************************************************************************/
public CodeBrowserOptionPane() 
	{
        super("codebrowser");
        setBorder(new EmptyBorder(5,5,5,5));
		
		JTextArea ta=new JTextArea(jEdit.getProperty("options.codebrowser.ctags_path_note"),0,60);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setBackground(Color.yellow);
		
		addComponent(ta);
		
		addSeparator();

        addComponent(
			jEdit.getProperty("options.codebrowser.ctags_path_label"),
			ctagsPathTF=new JTextField(
				jEdit.getProperty("options.codebrowser.ctags_path"),
				40
			)
		);
		
		addComponent(
			jEdit.getProperty("options.codebrowser.parser_history"),
			parserHistoryTextField=new JTextField(
				jEdit.getProperty("options.codebrowser.parser_history.value"),
				10
			)
		);
		
 		addSeparator();
   }

/******************************************************************************
* Implementation
******************************************************************************/
public void save() 
	{
		jEdit.setProperty("options.codebrowser.ctags_path", ctagsPathTF.getText());
		try {
			Integer.parseInt(parserHistoryTextField.getText());
			jEdit.setProperty("options.codebrowser.parser_history.value",
				parserHistoryTextField.getText());
		}
		catch(NumberFormatException e)
		{
			// don't allow an invalid property
			jEdit.resetProperty("options.codebrowser.parser_history.value");
		}
	}
}
/*************************************************************************EOF*/

