package com.addictedtor.orchestra ;

import org.af.jhlir.tools.DirectoryGuesser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;

/**
 * Options panel
 *
 * @author Bernd Bischl <bernd_bischl@gmx.net>
 * @author Romain Francois <francoisromain@free.fr>
 */

@SuppressWarnings("serial")
public class OrchestraOptionPane extends AbstractOptionPane implements ActionListener {
    protected static final Log logger = LogFactory.getLog(OrchestraOptionPane.class);

    public static final String OPTION_PREFIX = "options.orchestra.";
    private Hashtable<String, JTextField> pathNames = new Hashtable<String, JTextField>();
    private JTextField tfRHome = new JTextField();
    private JTextField tfShortcut = new JTextField();

    public OrchestraOptionPane() {
        super("orchestra-optionpane");
    }

    public void _init() {
        addPathPanel("rhome", tfRHome);
        addPathPanel("shortcut", tfShortcut);
        guessDirs();
    }

    private void addPathPanel(String name, JTextField tf) {
        pathNames.put(name, tf);
        addSeparator(OPTION_PREFIX + name + ".title");
        addComponent("", makePathPanel(name, tf));
    }

    private void guessDirs() {
//        if (tfJavaHome.getText().trim().equals(""))
//            tfJavaHome.setText(DirectoryGuesser.guessJavaHome());
        if (tfRHome.getText().trim().equals("")) {
            String rHome = DirectoryGuesser.guessRHome();
            logger.info("Guessing R_HOME : " + rHome);
            tfRHome.setText(rHome);
        }
        if (tfShortcut.getText().trim().equals("")) {
            String desktop = DirectoryGuesser.guessDesktop();
            logger.info("Guessing shortcut dir : " + desktop);
            tfShortcut.setText(desktop);
        }
    }

    private JPanel makePathPanel(String name, JTextField tf) {
        tf.setText(jEdit.getProperty(OPTION_PREFIX + name + ".path"));
        JButton pickPath = new JButton(jEdit.getProperty(OPTION_PREFIX + "choose-dir"));
        pickPath.setActionCommand("choose-" + name);
        JPanel pathPanel = new JPanel(new BorderLayout(0, 0));
        pathPanel.add(tf, BorderLayout.CENTER);
        pathPanel.add(pickPath, BorderLayout.EAST);
        pickPath.addActionListener(this);
        return pathPanel;
    }



    private void selectDir(String name) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            String dir = fc.getSelectedFile().getAbsolutePath();
            pathNames.get(name).setText(dir);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if (ac.startsWith("choose-")) {
            selectDir(ac.substring(7));
        }
    }



    public void _save() {

        logger.info("Saving options.");

        String rhome = tfRHome.getText();
        @SuppressWarnings("unused")
		String javahome = System.getProperty("java.home");
        String shortcut = tfShortcut.getText();

        File rhomeFile = new File(rhome);
        File shortcutFile = new File(shortcut);

        String err = null;
        if (!rhomeFile.exists() || !rhomeFile.isDirectory()) {
            err = "R home directory does not exist or is not a proper directory!";
        }
        if (!shortcutFile.exists() || !shortcutFile.isDirectory()) {
            err = "Directory for shortcut does not exist or is not a proper directory!";
        }

        if (err != null) {
            JOptionPane.showMessageDialog(this, err);
        } else {
            try {
//                RCmdBatch rCmdBatch = new RCmdBatch(rhome);
//                rCmdBatch.retrieveRInfo();
//                RPackage rp = rCmdBatch.getInstalledPackInfo("rJava");
                Installer i = new Installer(
                        jEdit.getJEditHome(),
                        OrchestraPlugin.getPluginHomePath(), 
                        rhome,
//                        rp.getLibpath().getAbsolutePath(),
//                        rCmdBatch.getLibPaths(),
//                        javahome,
                        shortcut
                );
                i.install();
            } catch (Exception e) {
                // todo handle
                e.printStackTrace();
            }
        }
        logger.info("Saving options done.");
    }
}
