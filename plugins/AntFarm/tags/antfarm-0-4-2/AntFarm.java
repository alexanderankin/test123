/*
 * AntFarm.java - Ant build utility plugin for jEdit
 * Copyright (C) 2000 Chris Scott
 * Other contributors: Rick Gibbs
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999, 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 * any, must include the following acknowlegement:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowlegement may appear in the software itself,
 * if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 * Foundation" must not be used to endorse or promote products derived
 * from this software without prior written permission. For written
 * permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 * nor may "Apache" appear in their names without prior written
 * permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */


import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
//import javax.swing.plaf.*;
import javax.swing.event.*;

/**
 *  Description of the Class
 *
 *@author     steinbeck
 *@created    2. August 2001
 */
public class AntFarm extends JPanel implements DockableWindow, ActionListener, KeyListener {
	private HistoryTextField buildField = new HistoryTextField("build file");
	private JButton fileChooser = new JButton("Browse");
	private JLabel target = new JLabel("Target: ");
	private JComboBox targetBox = new JComboBox();

	private JButton build = new JButton("Build");
	private JButton edit = new JButton("Edit");

	//private static JTextArea buildResults = new JTextArea(10, 20);
	private JList buildResults;
	private DefaultListModel listModel = new DefaultListModel();

	private AntFarmPlugin parent;

	private View view;


	/**
	 *  Constructor for the AntFarm object
	 *
	 *@param  afp   Description of Parameter
	 *@param  view  Description of Parameter
	 */
	public AntFarm(AntFarmPlugin afp, View view) {
		parent = afp;
		this.view = view;

		setLayout(new BorderLayout());

		GridBagLayout gl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		JPanel pane = new JPanel();
		pane.setLayout(gl);

		JPanel topPanel = new JPanel(new GridLayout(1, 2));

		JPanel leftPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(1, 8, 1, 8);
		leftPanel.add(new JLabel("Build File:  "), c);
		c.gridx = 1;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		leftPanel.add(buildField, c);
		// get the most recent build file and put it in the text field
		if (buildField.getModel().getSize() >= 1) {
			buildField.setText(buildField.getModel().getItem(0));
			populateTargetBox();
		}
		c.gridx = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		fileChooser.addActionListener(this);
		fileChooser.addKeyListener(this);
		leftPanel.add(fileChooser, c);

		JPanel rightPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.weightx = 0;
		edit.addActionListener(this);
		edit.addKeyListener(this);
		rightPanel.add(edit, c);
		c.gridx = 1;
		c.weightx = 0;
		rightPanel.add(target, c);
		c.gridx = 2;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		rightPanel.add(targetBox, c);
		c.gridx = 3;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		buildField.addActionListener(this);
		buildField.addKeyListener(this);
		build.addActionListener(this);
		build.addKeyListener(this);
		build.setNextFocusableComponent(buildField);
		rightPanel.add(build, c);

		topPanel.add(leftPanel);
		topPanel.add(rightPanel);

		c.gridx = 0;
		c.weightx = 100;
		c.fill = GridBagConstraints.HORIZONTAL;
		pane.add(topPanel, c);

		buildResults = new JList(listModel);
		buildResults.setCellRenderer(new AntCellRenderer());
		buildResults.setRequestFocusEnabled(false);
		JScrollPane jsp = new JScrollPane(buildResults);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 100;
		c.weighty = 100;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 2, 2, 2);
		pane.add(jsp, c);

		add(BorderLayout.CENTER, pane);
	}


	/**
	 *  Sets the BuildFile attribute of the AntFarm object
	 *
	 *@param  file  The new BuildFile value
	 */
	public void setBuildFile(File file) {
		buildField.setText(file.getAbsolutePath());
		populateTargetBox();
	}


	// begin DockableWindow implementation
	/**
	 *  Gets the Name attribute of the AntFarm object
	 *
	 *@return    The Name value
	 */
	public String getName() {
		return AntFarmPlugin.NAME;
	}


	/**
	 *  Gets the Component attribute of the AntFarm object
	 *
	 *@return    The Component value
	 */
	public Component getComponent() {
		return this;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  message  Description of Parameter
	 */
	public void appendToTextArea(String message) {
		appendToTextArea(message, buildResults.getForeground());
	}


	/**
	 *  Description of the Method
	 *
	 *@param  message  Description of Parameter
	 *@param  color    Description of Parameter
	 */
	public void appendToTextArea(String message, Color color) {
		ListObject lo = new ListObject(message, color);
		listModel.addElement(lo);
	}


	/**
	 *  Description of the Method
	 */
	public void build() {
		//clear text area
		listModel.removeAllElements();

		//clear Ant related errors from error box
		parent.clearErrors();

		String buildString = buildField.getText().trim();
		buildField.addCurrentToHistory();
		String targetString = ((String) targetBox.getSelectedItem()).trim();

		File buildFile = new File(buildString);
		TargetExecutor executor = new TargetExecutor(parent, this, buildFile, targetString, true);

		try {
			executor.execute();
		}
		catch (Exception e) {
			parent.handleBuildMessage(this,new BuildMessage(e.toString()));
		}
	}


	/**
	 *  Description of the Method
	 */
	public void edit() {
		String buildString = buildField.getText().trim();
		buildField.addCurrentToHistory();

		jEdit.openFile(view, buildField.getText());
	}


	/**
	 *  Uses the TargetParser class to retrieve a list of
	 *  targets from the build file, specified by the string in
	 *  buildField, and fill the targetBox with these values.
	 *  It the identifies the default target and sets it selected.
	 */
	public void populateTargetBox() {
		String buildString = buildField.getText().trim();
		File buildFile = new File(buildString);
		int defaultTargetNumber = 0;
		int counter = 0;
		String target = null;
		TargetParser targetParser = new TargetParser(parent, this, buildFile, false);
		targetParser.parseProject();
		Hashtable targets = targetParser.getTargets();
		String defaultTarget = targetParser.getDefaultTarget();
		targetBox.removeAllItems();

		for (Enumeration e = targets.keys(); e.hasMoreElements(); ) {
			target = ((String) e.nextElement()).trim();
			targetBox.addItem(target);
			if (target.equals(defaultTarget)) {
				defaultTargetNumber = counter;
			}
			counter++;
		}
		targetBox.setSelectedIndex(defaultTargetNumber);
	}

	/**
	 *  An action was performed in one the AntFarm gui components
	 *
	 *@param  e  The ActionEvent that contains more details regarding what happend
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == fileChooser) {
			browse();
		}
		if (source == build) {
			// || source == buildField || source == targetBox)
			build();
		}
		if (source == edit) {
			edit();
		}
		if (source == buildField)
		{
			populateTargetBox();
		}
	}

	/**
	 *  A key was released on one the Antfarm gui components
	 *
	 *@param  e  The KeyEvent specifying that exactly happend
	 */
	public void keyReleased(KeyEvent e) {
		Object source = e.getSource();
		/* If someone changes the path to the build file manually,
		 * we give a visual feedback wheather current string in the
		 * the buildField points to a valid file.
		 */
		if (source == buildField) {
			if (new File(buildField.getText().trim()).exists()) {
				populateTargetBox();
				/* These colors should be customizable */
				buildField.setForeground(Color.black);
			}
			else {
				/* These colors should be customizable */
				buildField.setForeground(Color.red);
			}
		}

	}


	/**
	 *  A key was typed on one the Antfarm gui components
	 *
	 *@param  e  The KeyEvent specifying that exactly happend
	 */
	public void keyTyped(KeyEvent e) {
	}


	/**
	 *  A key was pressed on one the Antfarm gui components
	 *
	 *@param  e  The KeyEvent specifying that exactly happend
	 */
	public void keyPressed(KeyEvent e) {
		Object source = e.getSource();
		int keyCode = e.getKeyCode();

		if (source == build) {
			if (keyCode == KeyEvent.VK_ENTER) {
				build();
			}
		}

		if (source == fileChooser) {
			if (keyCode == KeyEvent.VK_ENTER) {
				browse();
			}
		}

		if (source == edit) {
			if (keyCode == KeyEvent.VK_ENTER) {
				edit();
			}
		}
	}

	// end DockableWindow implementation

	/**
	 *  This method is called when the dockable window is added to the view, or
	 *  closed if it is floating.
	 */
	public void addNotify() {
		super.addNotify();
	}


	/**
	 *  This method is called when the dockable window is removed from the view, or
	 *  closed if it is floating.
	 */
	public void removeNotify() {
		super.removeNotify();
		//  jEdit.setProperty("antfarm.text",textArea.getText());
	}


	/**
	 *  Description of the Method
	 */
	private void browse() {
		JFileChooser chooser = new JFileChooser(buildField.getText().trim());
		chooser.addChoosableFileFilter(new AntFileFilter());
		//  chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			setBuildFile(file);
		}
	}


	private class ListObject {
		private String message;
		private Color color;


		/**
		 *  Constructor for the ListObject object
		 *
		 *@param  message  Description of Parameter
		 *@param  color    Description of Parameter
		 */
		ListObject(String message, Color color) {
			this.message = message;
			this.color = color;
		}


		/**
		 *  Gets the Color attribute of the ListObject object
		 *
		 *@return    The Color value
		 */
		public Color getColor() {
			return color;
		}


		/**
		 *  Description of the Method
		 *
		 *@return    Description of the Returned Value
		 */
		public String toString() {
			return message;
		}
	}


	// private members
	// private JTextArea textArea;


	private class AntCellRenderer extends JLabel implements ListCellRenderer {
		/**
		 *  Constructor for the AntCellRenderer object
		 */
		public AntCellRenderer() {
			setOpaque(true);
		}


		/**
		 *  Gets the ListCellRendererComponent attribute of the AntCellRenderer object
		 *
		 *@param  list          Description of Parameter
		 *@param  value         Description of Parameter
		 *@param  index         Description of Parameter
		 *@param  isSelected    Description of Parameter
		 *@param  cellHasFocus  Description of Parameter
		 *@return               The ListCellRendererComponent value
		 */
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			String s = value.toString();
			setText(s);

			if (value instanceof ListObject) {
				setForeground(((ListObject) value).getColor());
			}
			else {
				setForeground(list.getForeground());
			}

			setBackground(list.getBackground());

			setFont(list.getFont());
			return this;
		}
	}
}

