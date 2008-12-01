package gatchan.phpparser.project.dockablepanel;

import gatchan.phpparser.project.Project;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The option panel that will be shown for each project.
 *
 * @author Matthieu Casanova
 * @author $Id$
 */
public class ProjectOptionsPanel extends JPanel {
  private Project project;

  private final JTextField rootField = new JTextField();
  private final JButton browse = new JButton("...");

  private final JButton excludedBrowse = new JButton("...");
  private final JButton save = new JButton(GUIUtilities.loadIcon("Save.png"));
  private final JButton reparse = new JButton(GUIUtilities.loadIcon("Parse.png"));
  private final ProjectStatsPanel projectStatsPanel = new ProjectStatsPanel();
  private final DefaultListModel excludedListModel;
  private JPopupMenu menu;

  public ProjectOptionsPanel() {
    super(new GridBagLayout());
    rootField.setEditable(false);
    JLabel rootLabel = new JLabel("root : ");
    JLabel excludedLabel = new JLabel("excluded : ");
    MyActionListener actionListener = new MyActionListener();
    rootField.addKeyListener(new MyKeyAdapter());
    rootField.setEditable(false);

    browse.addActionListener(actionListener);
    browse.setToolTipText("Browse to find the root of your project");

    excludedListModel = new DefaultListModel();

    final JList excludedList = new JList(excludedListModel);

    excludedList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (GUIUtilities.isRightButton(e.getModifiers())) {
          if (menu == null) {
            menu = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem("remove");
            menuItem.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Object[] selectedValues = excludedList.getSelectedValues();
                if (selectedValues != null) {
                  for (int i = 0; i < selectedValues.length; i++) {
                    project.removeExcludedFolder((String) selectedValues[i]);
                    excludedListModel.removeElement(selectedValues[i]);
                  }
                }
              }
            });
            menu.add(menuItem);
          }
          GUIUtilities.showPopupMenu(menu, excludedList, e.getX(), e.getY());
        }
      }
    });

    excludedBrowse.addActionListener(actionListener);
    excludedBrowse.setToolTipText("Browse to find the root of your project");
    save.setEnabled(false);
    save.addActionListener(actionListener);
    save.setToolTipText("Save the project");

    reparse.setToolTipText("reparse project");
    reparse.addActionListener(actionListener);

      GridBagConstraints cons = new GridBagConstraints();
    cons.anchor = GridBagConstraints.NORTHWEST;
    add(rootLabel, cons);
    cons.fill = GridBagConstraints.BOTH;
    cons.weightx = 1.0;
    add(rootField, cons);
    cons.fill = GridBagConstraints.NONE;
    cons.weightx = 0.0;
    add(browse, cons);

    int visibleRows = 3;
    cons.gridheight = visibleRows;
    cons.weighty = 1.0;
    int line = 0;
    cons.gridy = ++line;
    add(excludedLabel, cons);
    cons.fill = GridBagConstraints.BOTH;
    cons.weightx = 1.0;
    add(new JScrollPane(excludedList), cons);
    cons.fill = GridBagConstraints.NONE;
    cons.weightx = 0.0;
    add(excludedBrowse, cons);
    cons.gridheight = 1;
    cons.weighty = 0.0;

    line += visibleRows;
    cons.gridy = line;
    JToolBar toolBar = new JToolBar();
    toolBar.add(save);
    toolBar.add(reparse);
    toolBar.setFloatable(false);
    toolBar.setBorderPainted(false);
    cons.gridwidth = GridBagConstraints.REMAINDER;
    add(toolBar, cons);

    cons.gridy = ++line;
    add(Box.createVerticalStrut(3), cons);

    cons.gridy = ++line;
    cons.fill = GridBagConstraints.HORIZONTAL;
    add(projectStatsPanel, cons);
    cons.gridy = 99;
    cons.weighty = 1.0;
    add(Box.createGlue(), cons);
  }

  /**
   * This method is called by {@link ProjectTabbedPane#setProject(Project)}
   *
   * @param project the project
   */
  public void setProject(Project project) {
    this.project = project;
    excludedListModel.removeAllElements();
    if (project == null) {
      rootField.setText(null);
      setEnabled(false);
    } else {
      rootField.setText(project.getRoot());
      Object[] excludedFolders = project.getExcludedFolders();
      for (int i = 0; i < excludedFolders.length; i++) {
        excludedListModel.addElement(excludedFolders[i]);
      }
      setEnabled(true);
    }
    projectStatsPanel.updateProject(project);
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    browse.setEnabled(enabled);
    excludedBrowse.setEnabled(enabled);
    rootField.setEditable(enabled);
    save.setEnabled(enabled);
    reparse.setEnabled(enabled);
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String currentPath = rootField.getText();
      if (e.getSource() == reparse) {
        project.rebuildProject();
      } else if (e.getSource() == browse) {
        String[] choosenFolder = GUIUtilities.showVFSFileDialog(null, currentPath, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
        if (choosenFolder != null) {
          rootField.setText(choosenFolder[0]);
          project.setRoot(choosenFolder[0]);
          save.setEnabled(true);
        }
      } else if (e.getSource() == excludedBrowse) {
        String[] choosenFolder = GUIUtilities.showVFSFileDialog(null, currentPath, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, true);
        if (choosenFolder != null) {
          for (int i = 0; i < choosenFolder.length; i++) {
            String path = choosenFolder[i];
            if (project.addExcludedFolder(path)) {
              excludedListModel.addElement(path);
            }
          }
          save.setEnabled(true);
        }
      } else if (e.getSource() == save) {
        project.setRoot(currentPath);
        project.save();
        save.setEnabled(false);
      }
    }
  }

  private class MyKeyAdapter extends KeyAdapter {
    public void keyTyped(KeyEvent e) {
      save.setEnabled(true);
    }
  }
}
