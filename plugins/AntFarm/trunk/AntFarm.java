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
import org.gjt.sp.jedit.GUIUtilities;
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
public class AntFarm extends JPanel implements DockableWindow, ActionListener, KeyListener, FocusListener, ItemListener {
	private HistoryTextField buildField = new HistoryTextField("build file");
	//private JButton fileChooser = new JButton("Browse");
	private JLabel target = new JLabel("Target: ");
	private JComboBox targetBox = new JComboBox();

	/*private JButton build = new JButton("Build");
	private JButton edit = new JButton("Edit");*/

	private JButton edit, build, fileChooser, reload;

	//private static JTextArea buildResults = new JTextArea(10, 20);
	private JList buildResults;
	private DefaultListModel listModel = new DefaultListModel();

	private AntFarmPlugin parent;

    // this ensures we do not execute parallel builds
    private boolean buildInProgress;

	private View view;
	private final static Insets ZERO_MARGIN = new Insets(0, 0, 0, 0);

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
		JPanel pane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.weightx = .04;
		gbc.insets = new Insets(3, 1, 3, 1);
		gbc.anchor = gbc.WEST;
		gbc.fill = gbc.NONE;
		pane.add(new JLabel("Build File: "), gbc);

		buildField.addFocusListener(this);
		if (buildField.getModel().getSize() >= 1)
		{
			buildField.setText(buildField.getModel().getItem(0));
		}
		buildField.addActionListener(this);
		buildField.addKeyListener(this);

		gbc.gridx = 1;
		gbc.weightx = .5;
		gbc.fill = gbc.HORIZONTAL;
		pane.add(buildField, gbc);

		fileChooser = createButton("Open24.gif", "Load a build file");
		fileChooser.addActionListener(this);
		fileChooser.addKeyListener(this);
		gbc.gridx = 2;
		gbc.weightx = .04;
		gbc.fill = gbc.NONE;
		pane.add(fileChooser, gbc);

		reload = createButton("Refresh24.gif", "Reload the build file");
		reload.addActionListener(this);
		reload.addKeyListener(this);
		gbc.gridx = 3;
		pane.add(reload, gbc);

		edit = createButton("Edit24.gif", "Edit the build file");
		edit.addActionListener(this);
		edit.addKeyListener(this);
		gbc.gridx = 4;
		pane.add(edit, gbc);

		gbc.gridx = 5;
		gbc.anchor = gbc.EAST;
		pane.add(new JLabel("Target: "), gbc);

		//if (targetField.getModel().getSize() >= 1)
		//	targetField.setText(targetField.getModel().getItem(0));
		targetBox = new JComboBox();
		gbc.gridx = 6;
		gbc.weightx = .26;
		gbc.fill = gbc.HORIZONTAL;
		//pane.add(targetField, gbc);
		pane.add(targetBox, gbc);

		build = createButton("Play24.gif", "Run the given target");
		build.addActionListener(this);
		build.addKeyListener(this);
		build.setNextFocusableComponent(buildField);
		gbc.gridx = 7;
		gbc.weightx = .04;
		gbc.fill = gbc.NONE;
		pane.add(build, gbc);

		add(pane, BorderLayout.NORTH);

		buildResults = new JList(listModel);
		buildResults.setCellRenderer(new AntCellRenderer());
		buildResults.setRequestFocusEnabled(false);
		add(new JScrollPane(buildResults), BorderLayout.CENTER);
		loadBuildFile();

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

		/**
	 *  Returns the currently loaded build file.
	 *
	 *@return    The CurrentBuildFile value
	 *@since
	 */
	private String getCurrentBuildFile() {
		return parent.getAntBridge().getBuildFile();
	}

		/**
	 *  Reload the build file.
	 *
	 *@since
	 */
	public void reload() {
		loadBuildFile();
	}




	// private members
//	private JTextArea textArea;

	/**
	 *  Load the current build file.
	 *
	 *@since
	 */
	private void loadBuildFile() {
		parent.getAntBridge().loadBuildFile(buildField.getText());
		/* String[] targetBox = parent.getAntBridge().getTargets();
		if (this.targetBox.getItemCount() != 0)
		{
			this.targetBox.removeAllItems();
		}
		for (int i = 0; i < targetBox.length; i++)
		{
			this.targetBox.addItem(targetBox[i]);
		}*/
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
        if (buildInProgress) {
            // should problably notify somehow?
            return;
        }


		//clear text area
		listModel.removeAllElements();

		//clear Ant related errors from error box
		parent.clearErrors();

		String buildString = buildField.getText().trim();
		buildField.addCurrentToHistory();
		String targetString = ((String) targetBox.getSelectedItem()).trim();

		File buildFile = new File(buildString);
		final TargetExecutor executor = new TargetExecutor(parent, this, buildFile, targetString, true);

        Runnable executeTask = new Runnable() {
            public void run() {
                try {
                    buildInProgress = true;
                    executor.execute();
                }
                catch (Exception e) {
                    parent.handleBuildMessage(AntFarm.this,new BuildMessage(e.toString()));
                } finally {
                    buildInProgress = false;
                }
            }
        };

        synchronized (this) {
            (new Thread(executeTask)).start();
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
		String[] targets = parent.getAntBridge().getTargets();
/*		TargetParser targetParser = new TargetParser(parent, this, buildFile, false);
		targetParser.parseProject();
		Hashtable targets = targetParser.getTargets();*/
		String defaultTarget = parent.getAntBridge().getDefaultTarget();
		targetBox.removeAllItems();

		for (int f = 0; f < targets.length; f++) {
			if (targets[f] != null )
			{
				target = targets[f].trim();
				targetBox.addItem(target);
				if (target.equals(defaultTarget)) {
					defaultTargetNumber = counter;
				}
				counter++;
			}
		}
		if (defaultTarget != null)
		{
			targetBox.setSelectedIndex(defaultTargetNumber);
		}
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
		if (source == reload)
		{
			// || source == buildField || source == targetBox)
			reload();
		}

		if (source == edit) {
			edit();
		}
		if (source == buildField)
		{
			reload();
		}
	}

	public void itemStateChanged(ItemEvent event)
	{
		reload();
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

		/**
	 *  Handle a focus lost.
	 *
	 *@param  evt  Description of Parameter
	 *@since
	 */
	public void focusLost(FocusEvent evt) {
		if (!buildField.getText().trim().equals(getCurrentBuildFile()))
		{
			loadBuildFile();
		}
	}


	/**
	 *  Handle a focus gained.
	 *
	 *@param  evt  Description of Parameter
	 *@since
	 */
	public void focusGained(FocusEvent evt) {
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
	 *  Browse for the build file.
	 *
	 *@since
	 */
	private void browse() {
		JFileChooser chooser = new JFileChooser(buildField.getText().trim());
		chooser.addChoosableFileFilter(new AntFileFilter());
		int returnVal = chooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			setBuildFile(file);
			loadBuildFile();
		}
	}

	/**
	 *  Create a button.
	 *
	 *@param  imageName  Description of Parameter
	 *@param  toolTip    Description of Parameter
	 *@return            Description of the Returned Value
	 *@since
	 */
	private JButton createButton(String imageName, String toolTip) {
		JButton button = new JButton(GUIUtilities.loadIcon(imageName));
		button.setToolTipText(toolTip);
		button.setMargin(ZERO_MARGIN);
    button.setRolloverEnabled(true);
		return button;
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

