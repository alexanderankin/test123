package gatchan.phpparser.project.dockablepanel;

import gatchan.phpparser.project.Project;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * The option panel that will be shown for each project.
 *
 * @author Matthieu Casanova
 */
public final class ProjectOptionsPanel extends JPanel {
  private Project project;

  private final JTextField rootField = new JTextField();
  private final JButton browse = new JButton("...");

  private final JButton excludedBrowse = new JButton("...");
  private final JButton save = new JButton(GUIUtilities.loadIcon("Save.png"));
  private final JButton reparse;
  private final ProjectStatsPanel projectStatsPanel = new ProjectStatsPanel();
  private final DefaultListModel excludedListModel;

  public ProjectOptionsPanel() {
    super(new GridBagLayout());
    rootField.setEditable(false);
    final JLabel rootLabel = new JLabel("root : ");
    final JLabel excludedLabel = new JLabel("excluded : ");
    final MyActionListener actionListener = new MyActionListener();
    rootField.addKeyListener(new MyKeyAdapter());
    rootField.setEditable(false);

    browse.addActionListener(actionListener);
    browse.setToolTipText("Browse to find the root of your project");

    excludedListModel = new DefaultListModel();
    JList excludedList = new JList(excludedListModel);
    excludedBrowse.addActionListener(actionListener);
    excludedBrowse.setToolTipText("Browse to find the root of your project");
    save.setEnabled(false);
    save.addActionListener(actionListener);
    save.setToolTipText("Save the project");

    reparse = new JButton(GUIUtilities.loadIcon("Parse.png"));
    reparse.setToolTipText("reparse project");
    reparse.addActionListener(actionListener);

    int line = 0;

    final GridBagConstraints cons = new GridBagConstraints();
    add(rootLabel, cons);
    cons.fill = GridBagConstraints.BOTH;
    cons.weightx = 1;
    add(rootField, cons);
    cons.fill = GridBagConstraints.NONE;
    cons.weightx = 0;
    add(browse, cons);

    cons.gridy = ++line;
    add(excludedLabel, cons);
    cons.fill = GridBagConstraints.BOTH;
    cons.weightx = 1;
    add(new JScrollPane(excludedList), cons);
    cons.fill = GridBagConstraints.NONE;
    cons.weightx = 0;
    add(excludedBrowse, cons);

    cons.gridy = ++line;
    final JToolBar toolBar = new JToolBar();
    toolBar.add(save);
    toolBar.add(reparse);
    toolBar.setFloatable(false);
    toolBar.setBorderPainted(false);
    cons.anchor = GridBagConstraints.WEST;
    cons.gridwidth = GridBagConstraints.REMAINDER;
    add(toolBar, cons);

    cons.gridy = ++line;
    add(Box.createVerticalStrut(3), cons);

    cons.gridy = ++line;
    cons.fill = GridBagConstraints.HORIZONTAL;
    add(projectStatsPanel, cons);
    cons.gridy = 99;
    cons.weighty = 1;
    add(Box.createGlue(), cons);
  }

  /**
   * This method is called by {@link gatchan.phpparser.project.dockablepanel.ProjectTabbedPane#setProject(Project)}
   *
   * @param project the project
   */
  public void setProject(Project project) {
    this.project = project;
    if (project == null) {
      rootField.setText(null);
      setEnabled(false);
    } else {
      rootField.setText(project.getRoot());
      setEnabled(true);
    }
    projectStatsPanel.updateProject(project);
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    browse.setEnabled(enabled);
    rootField.setEditable(enabled);
    save.setEnabled(enabled);
    reparse.setEnabled(enabled);
  }

  private final class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == reparse) {
        project.rebuildProject();
      } else if (e.getSource() == browse) {
        String[] choosenFolder = GUIUtilities.showVFSFileDialog(null, null, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
        if (choosenFolder != null) {
          rootField.setText(choosenFolder[0]);
          project.setRoot(choosenFolder[0]);
          save.setEnabled(true);
        }
      } else if (e.getSource() == browse) {
        String[] choosenFolder = GUIUtilities.showVFSFileDialog(null, null, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
        if (choosenFolder != null) {
          excludedListModel.addElement(choosenFolder[0]);
          save.setEnabled(true);
        }
      } else if (e.getSource() == save) {
        final String root = rootField.getText();
        project.setRoot(root);
        project.save();
        save.setEnabled(false);
      }
    }
  }

  private final class MyKeyAdapter extends KeyAdapter {
    public void keyTyped(KeyEvent e) {
      save.setEnabled(true);
    }
  }
}
