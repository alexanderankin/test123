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
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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

/**
  @author Chris Scott, Rick Gibbs
*/

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import java.util.Date;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
//import javax.swing.plaf.*;
import javax.swing.event.*;

public class AntFarm extends JPanel implements DockableWindow, ActionListener, KeyListener
{
  private HistoryTextField buildField = new HistoryTextField("build file");
  private JButton fileChooser = new JButton("Browse");
  private JLabel target = new JLabel("Target: ");
  private HistoryTextField targetField = new HistoryTextField("target");
  private JButton build = new JButton("Build");
  private JButton edit = new JButton("Edit");

  //private static JTextArea buildResults = new JTextArea(10, 20);
  private JList buildResults;
  private DefaultListModel listModel = new DefaultListModel();

  private AntFarmPlugin parent;

  private View view;
  private static AntFarm antfarm;

  private AntFarm(AntFarmPlugin afp, View view)
  {
    parent = afp;
    this.view = view;

    setLayout(new BorderLayout());

    GridBagLayout gl = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();

    JPanel pane = new JPanel();
    pane.setLayout(gl);

    JPanel topPanel = new JPanel(new GridLayout(1,2));

    JPanel leftPanel = new JPanel(new GridBagLayout());
    c.gridx = 0;
    c.weightx = 0;
    c.insets = new Insets(1,8,1,8);
    leftPanel.add(new JLabel("Build File:  "), c);
    c.gridx = 1;
    c.weightx = 100;
    c.fill = GridBagConstraints.HORIZONTAL;
    leftPanel.add(buildField, c);
    // get the most recent build file and put it in the text field
    if (buildField.getModel().getSize() >= 1)
      buildField.setText(buildField.getModel().getItem(0));
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
    rightPanel.add(targetField, c);
    // get the most recent target and put it in the text field
    if (targetField.getModel().getSize() >= 1)
      targetField.setText(targetField.getModel().getItem(0));
    c.gridx = 3;
    c.weightx = 0;
    c.fill = GridBagConstraints.NONE;
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
    c.insets = new Insets(0,2,2,2);
    pane.add(jsp, c);

    add(BorderLayout.CENTER, pane);
  }
  public static synchronized AntFarm getAntFarm()
  {
    return antfarm;
  }
  public static synchronized AntFarm setAntFarm(AntFarmPlugin afp, View view)
  {
    antfarm = new AntFarm(afp, view);
    return antfarm;
  }

  public void appendToTextArea(String message)
  {
    appendToTextArea(message, buildResults.getForeground());
  }
  public void appendToTextArea(String message, Color color)
  {
    ListObject lo = new ListObject(message, color);
    listModel.addElement(lo);
  }

  private class ListObject
  {
    private String message;
    private Color color;

    ListObject(String message, Color color)
    {
      this.message = message;
      this.color = color;
    }

    public Color getColor()
    {
      return color;
    }
    public String toString()
    {
      return message;
    }
  }

  public void build()
  {
    // set the ANT window to view, or load it if it isn't
    view.getDockableWindowManager().addDockableWindow(parent.NAME);

    //clear text area
    listModel.removeAllElements();

    //clear Ant related errors from error box
    parent.clearErrors();

    String buildString = buildField.getText().trim();
    buildField.addCurrentToHistory();
    String targetString = targetField.getText().trim();
    targetField.addCurrentToHistory();

    File buildFile = new File(buildString);
    TargetExecutor executor = new TargetExecutor(parent, buildFile, targetString);

    try
    {
      executor.execute();
    }
    catch (Exception e)
    {
      System.out.println("Error executing build!");
      e.printStackTrace();
    }
  }

  public void edit()
  {
    String buildString = buildField.getText().trim();
    buildField.addCurrentToHistory();

    jEdit.openFile(view, null, buildField.getText(), false, false);
  }

  private void browse()
  {
    JFileChooser chooser = new JFileChooser(buildField.getText().trim());
    chooser.addChoosableFileFilter(new AntFileFilter());
    //  chooser.setFileFilter(filter);

    int returnVal = chooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      File file = chooser.getSelectedFile();

      setBuildFile(file);
    }
  }

  public void setBuildFile(File file)
  {
    buildField.setText(file.getAbsolutePath());
  }

  public void actionPerformed(ActionEvent e)
  {
    Object source = e.getSource();

    if (source == fileChooser)
      browse();
    if (source == build) // || source == buildField || source == targetField)
      build();
    if (source == edit)
      edit();
  }

  public void keyReleased(KeyEvent e)
{}
  public void keyTyped(KeyEvent e)
  {}
  public void keyPressed(KeyEvent e)
  {
    Object source = e.getSource();
    int keyCode = e.getKeyCode();

    if (source == build)
      if (keyCode == KeyEvent.VK_ENTER)
        build();

    if (source == fileChooser)
      if (keyCode == KeyEvent.VK_ENTER)
        browse();

    if (source == edit)
      if (keyCode == KeyEvent.VK_ENTER)
        edit();
  }

  // begin DockableWindow implementation
  public String getName()
  {
    return AntFarmPlugin.NAME;
  }

  public Component getComponent()
  {
    return this;
  }
  // end DockableWindow implementation

  /**
   * This method is called when the dockable window is added to
   * the view, or closed if it is floating.
   */
  public void addNotify()
  {
    super.addNotify();
  }

  /**
   * This method is called when the dockable window is removed from
   * the view, or closed if it is floating.
   */
  public void removeNotify()
  {
    super.removeNotify();
    //  jEdit.setProperty("antfarm.text",textArea.getText());
  }

  // private members
  // private JTextArea textArea;


  private class AntCellRenderer extends JLabel implements ListCellRenderer
  {
    public AntCellRenderer()
    {
      setOpaque(true);
    }
    public Component getListCellRendererComponent (JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus)
    {

      String s = value.toString();
      setText(s);

      if (value instanceof ListObject)
        setForeground(((ListObject)value).getColor());
      else
        setForeground(list.getForeground());

      setBackground(list.getBackground());

      setFont(list.getFont());
      return this;
    }
  }
}
