package gatchan.phpparser.project.dockablepanel;

import gatchan.phpparser.project.Project;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * The option panel that will be shown for each project.
 *
 * @author Matthieu Casanova
 */
public final class ProjectOptionsPanel extends JPanel {

  private Project project;

  private final JTextField rootField = new JTextField();
  private final JButton browse = new JButton("...");
  private final JButton save = new JButton(GUIUtilities.loadIcon("Save.png"));
  private final JButton reparse;
  private final ProjectStatsPanel projectStatsPanel = new ProjectStatsPanel();

  public ProjectOptionsPanel() {
    super(new GridBagLayout());
    rootField.setEditable(false);
    final JLabel label = new JLabel("root : ");
    final MyActionListener actionListener = new MyActionListener();
    rootField.addKeyListener(new MyKeyAdapter());
    rootField.setEditable(false);

    browse.addActionListener(actionListener);
    browse.setToolTipText("Browse to find the root of your project");

    save.setEnabled(false);
    save.addActionListener(actionListener);
    save.setToolTipText("Save the project");

    reparse = new JButton(GUIUtilities.loadIcon("Parse.png"));
    reparse.setToolTipText("reparse project");
    reparse.addActionListener(actionListener);


    final GridBagConstraints cons = new GridBagConstraints();
    add(label, cons);
    cons.fill = GridBagConstraints.BOTH;
    cons.weightx = 1;
    add(rootField, cons);
    cons.fill = GridBagConstraints.NONE;
    cons.weightx = 0;
    add(browse, cons);

    cons.gridy = 1;
    final JToolBar toolBar = new JToolBar();
    toolBar.add(save);
    toolBar.add(reparse);
    toolBar.setFloatable(false);
    toolBar.setBorderPainted(false);
    cons.anchor = GridBagConstraints.WEST;
    cons.gridwidth = GridBagConstraints.REMAINDER;
    add(toolBar, cons);

    cons.gridy = 2;
    add(Box.createVerticalStrut(3), cons);

    cons.gridy = 3;
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
        /*final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Choose");
        fileChooser.setDialogTitle("Choose Project root");
        fileChooser.setFileFilter(new DirectoryFileFilter());
        final int returnVal = fileChooser.showOpenDialog(jEdit.getActiveView());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          final File rootDirectory = fileChooser.getSelectedFile();
          final String absolutePath = rootDirectory.getAbsolutePath();
          rootField.setText(absolutePath);
          project.setRoot(absolutePath);
          save.setEnabled(true);
        }   */
        String[] choosenFolder = GUIUtilities.showVFSFileDialog(null, null, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
        if (choosenFolder != null) {
          rootField.setText(choosenFolder[0]);
          project.setRoot(choosenFolder[0]);
          save.setEnabled(true);
        }
      } else if (e.getSource() == save) {
        final String root = rootField.getText();
        final File f = new File(root);
        if (f.isDirectory()) {
          project.setRoot(root);
          project.save();
        } else {
          JOptionPane.showMessageDialog(ProjectOptionsPanel.this, root + " is not a valid root path for your project");
        }
        save.setEnabled(false);
      }
    }
  }

  /**
   * A FileFilter that accept only directories.
   *
   * @author Matthieu Casanova
   */
  private static final class DirectoryFileFilter extends FileFilter {
    public boolean accept(File f) {
      return f.isDirectory();
    }

    public String getDescription() {
      return "Directories";
    }
  }

  private final class MyKeyAdapter extends KeyAdapter {
    public void keyTyped(KeyEvent e) {
      save.setEnabled(true);
    }
  }
}
