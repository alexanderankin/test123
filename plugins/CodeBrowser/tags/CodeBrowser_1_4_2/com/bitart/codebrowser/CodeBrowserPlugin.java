/******************************************************************************
*	Copyright 2002 BITart Gerd Knops. All rights reserved.
*
*	Project	: CodeBrowser
*	File	: CodeBrowserPlugin.java
*	Author	: Gerd Knops gerti@BITart.com
*
*******************************************************************************
*                                    :mode=java:folding=indent:collapseFolds=1:
*	History:
*	020510 Creation of file
*
*******************************************************************************
*
*	Description:
*	Simple jEdit plugin class.
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
	import java.io.*;
	import java.util.*;
	import javax.swing.*;
	
	import org.gjt.sp.jedit.*;
	import org.gjt.sp.jedit.msg.*;
	import org.gjt.sp.jedit.textarea.*;
	import org.gjt.sp.util.*;
	import org.gjt.sp.jedit.gui.*;
	
/*****************************************************************************/
public class CodeBrowserPlugin extends EditPlugin
{
/******************************************************************************
* Vars
******************************************************************************/

    public static final String NAME="codebrowser";
    public static final String MENU="codebrowser.menu";
	
/******************************************************************************
* Implementation
******************************************************************************/
public void createMenuItems(Vector menuItems)
	{
        //menuItems.addElement(GUIUtilities.loadMenu(MENU));
		menuItems.addElement(GUIUtilities.loadMenuItem(NAME));
    }
	
public void createOptionPanes(OptionsDialog od)
	{
        od.addOptionPane(new CodeBrowserOptionPane());
    }
	
}
/*************************************************************************EOF*/

