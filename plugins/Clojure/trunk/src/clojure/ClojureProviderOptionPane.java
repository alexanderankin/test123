package clojure;
/**
 * @author Damien Radtke
 * class ClojureProviderOptionPane
 * TODO: comment
 */
//{{{ Imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.jEdit;
//}}}
public class ClojureProviderOptionPane extends AbstractOptionPane {
	
	private JRadioButton jarButton;
	private JRadioButton customButton;
	private ButtonGroup group;
	
	private JTextField downloadPath;
	private JCheckBox forceDownload;
	private JCheckBox removeDownloaded;
	
	private JButton browse;
	private JTextField customText;
	
	private boolean isDownloaded;
	
	public ClojureProviderOptionPane() {
		super("clojure-provider");
	}
	
	protected void _init() {
		// Radio buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		jarButton = new JRadioButton(
				jEdit.getProperty("options.clojure.jar-label"));
		customButton = new JRadioButton(
				jEdit.getProperty("options.clojure.custom-label"));
		buttonPanel.add(jarButton);
		buttonPanel.add(customButton);
		group = new ButtonGroup();
		group.add(jarButton);
		group.add(customButton);
		addComponent(buttonPanel);
		addSeparator();
		
		// Custom installation path chooser
		customText = new JTextField(jEdit.getProperty(
				"options.clojure.custom-path"));
		browse = new JButton(jEdit.getProperty(
				"options.clojure.browse-label"));
		browse.addActionListener(new BrowseHandler());
		JPanel custom = new JPanel();
		custom.setLayout(new BoxLayout(custom, BoxLayout.X_AXIS));
		custom.add(customText);
		custom.add(browse);
		addComponent(jEdit.getProperty("options.clojure.path-label"),
			custom);
		
		// Select the appropriate button
		String mode = jEdit.getProperty("options.clojure.install", "jar");
		if (mode.equals("custom")) {
			jarButton.setSelected(false);
			customButton.setSelected(true);
			customText.setText(jEdit.getProperty(
				"options.clojure.clojure-path"));
		} else {
			jarButton.setSelected(true);
			customText.setEnabled(false);
			browse.setEnabled(false);
		}
		
		ButtonHandler handler = new ButtonHandler();
		jarButton.addActionListener(handler);
		customButton.addActionListener(handler);
	}
	
	protected void _save() {
		ClojurePlugin plugin = (ClojurePlugin) jEdit.getPlugin(
			"clojure.ClojurePlugin");
		if (jarButton.isSelected()) {
			jEdit.setProperty("options.clojure.install", "jar");
		} else {
			jEdit.setProperty("options.clojure.install", "custom");
			jEdit.setProperty("options.clojure.download-path",
				downloadPath.getText());
		}
		plugin.setClojureJar();
		plugin.setVars();
	}
	
	class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			boolean isCustom = customButton.isSelected();
			customText.setEnabled(isCustom);
			browse.setEnabled(isCustom);
		}
	}
	
	class BrowseHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			VFSFileChooserDialog dialog = new VFSFileChooserDialog(
				jEdit.getActiveView(), System.getProperty("user.dir")+File.separator,
				VFSBrowser.OPEN_DIALOG, false, true);
			String[] files = dialog.getSelectedFiles();
			if (files != null) {
				customText.setText(files[0]);
			}
		}
	}
}
