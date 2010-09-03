package groovy;
/**
 * @author Damien Radtke
 * class GroovyProviderOptionPane
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
public class GroovyProviderOptionPane extends AbstractOptionPane {
	
	private JRadioButton jarButton;
	private JRadioButton fullButton;
	private JRadioButton customButton;
	private ButtonGroup group;
	
	private JTextField downloadPath;
	private JCheckBox forceDownload;
	private JCheckBox removeDownloaded;
	
	private JButton browse;
	private JTextField customText;
	
	private boolean isDownloaded;
	
	public GroovyProviderOptionPane() {
		super("groovy-provider");
	}
	
	protected void _init() {
		// Radio buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		jarButton = new JRadioButton(
				jEdit.getProperty("options.groovy.jar-label"));
		fullButton = new JRadioButton(
				jEdit.getProperty("options.groovy.full-label"));
		customButton = new JRadioButton(
				jEdit.getProperty("options.groovy.custom-label"));
		buttonPanel.add(jarButton);
		buttonPanel.add(fullButton);
		buttonPanel.add(customButton);
		group = new ButtonGroup();
		group.add(jarButton);
		group.add(fullButton);
		group.add(customButton);
		addComponent(buttonPanel);
		addSeparator();
		
		// "is downloaded" message
		String fullDownloadInfo = "<html>"+jEdit.getProperty(
				"options.groovy.downloaded-label");
		isDownloaded = jEdit.getBooleanProperty(
			"options.groovy.groovy-downloaded"); 
		if (isDownloaded) {
			fullDownloadInfo += "<strong><font color=\"green\">"+
				jEdit.getProperty("options.groovy.downloaded-label.yes")+
				"</font></strong>";
			fullDownloadInfo += " ( in "+jEdit.getPlugin(
				"groovy.GroovyPlugin").getPluginHome()+")";
		} else {
			fullDownloadInfo += "<strong><font color=\"red\">"+
				jEdit.getProperty("options.groovy.downloaded-label.no")
				+"</font></strong>";
		}
		fullDownloadInfo += "</html>";
		addComponent(new JLabel(fullDownloadInfo));
		
		// Download url
		addComponent(jEdit.getProperty("options.groovy.downloadpath-label"),
			downloadPath = new JTextField(jEdit.getProperty(
				"options.groovy.groovy-url")));
		
		// Force re-download
		addComponent(forceDownload = new JCheckBox(jEdit.getProperty(
			"options.groovy.forcedownload-label"), false));
		
		// Remove existing download
		addComponent(removeDownloaded = new JCheckBox(jEdit.getProperty(
			"options.groovy.removedownload-label"), false));
		addSeparator();
		
		// Custom installation path chooser
		customText = new JTextField(jEdit.getProperty(
				"options.groovy.custom-path"));
		browse = new JButton(jEdit.getProperty(
				"options.groovy.browse-label"));
		browse.addActionListener(new BrowseHandler());
		JPanel custom = new JPanel();
		custom.setLayout(new BoxLayout(custom, BoxLayout.X_AXIS));
		custom.add(customText);
		custom.add(browse);
		addComponent(jEdit.getProperty("options.groovy.custompath-label"),
			custom);
		
		// Select the appropriate button
		String mode = jEdit.getProperty("options.groovy.install");
		if (mode.equals("full")) {
			fullButton.setSelected(true);
			customText.setEnabled(false);
			browse.setEnabled(false);
			removeDownloaded.setEnabled(false);
			forceDownload.setEnabled(isDownloaded);
		} else if (mode.equals("custom")) {
			customButton.setSelected(true);
			downloadPath.setEnabled(false);
			removeDownloaded.setEnabled(isDownloaded);
			forceDownload.setEnabled(false);
			customText.setText(jEdit.getProperty(
				"options.groovy.groovy-path"));
		} else {
			jarButton.setSelected(true);
			customText.setEnabled(false);
			browse.setEnabled(false);
			downloadPath.setEnabled(false);
			removeDownloaded.setEnabled(isDownloaded);
			forceDownload.setEnabled(false);
		}
		
		ButtonHandler handler = new ButtonHandler();
		jarButton.addActionListener(handler);
		fullButton.addActionListener(handler);
		customButton.addActionListener(handler);
	}
	
	protected void _save() {
		GroovyPlugin plugin = (GroovyPlugin) jEdit.getPlugin(
			"groovy.GroovyPlugin");
		if (downloadPath.isEnabled()) {
			jEdit.setProperty("options.groovy.download-path",
				downloadPath.getText());
		}
		if (removeDownloaded.isEnabled() && removeDownloaded.isSelected()) {
			plugin.clearGroovy();
		}
		if (jarButton.isSelected()) {
			jEdit.setProperty("options.groovy.install", "jar");
			plugin.setGroovyJar();
		} else if (fullButton.isSelected()) {
			if (!isDownloaded || forceDownload.isSelected()) {
				// Ask download confirmation
				int answer = JOptionPane.showConfirmDialog(
					jEdit.getActiveView(),
					jEdit.getProperty(
						"options.groovy.download-groovy-prompt.message"),
					jEdit.getProperty(
						"options.groovy.download-groovy-prompt.title"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
				if (answer != JOptionPane.YES_OPTION) {
					return;
				}
				plugin.downloadGroovy();
			}
			jEdit.setProperty("options.groovy.install", "full");
		} else {
			String customPath = customText.getText();
			File jar = plugin.getEmbeddableJar(customPath);
			if (jar == null) {
				GUIUtilities.error(jEdit.getActiveView(),
					"options.groovy.invalid-custom", new String[] {customPath});
			} else {
				plugin.setGroovyJar(jar.getPath());
				jEdit.setProperty("options.groovy.install", "custom");
				jEdit.setProperty("options.groovy.groovy-path", customPath);
			}
		}
		plugin.setVars();
	}
	
	class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			boolean isCustom = customButton.isSelected();
			customText.setEnabled(isCustom);
			browse.setEnabled(isCustom);
			
			boolean isFull = fullButton.isSelected();
			downloadPath.setEnabled(isFull);
			forceDownload.setEnabled(isFull && isDownloaded);
			removeDownloaded.setEnabled(!isFull && isDownloaded);
		}
	}
	
	class BrowseHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			VFSFileChooserDialog dialog = new VFSFileChooserDialog(
				jEdit.getActiveView(), System.getProperty("user.dir"),
				VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false, true);
			String[] files = dialog.getSelectedFiles();
			if (files != null) {
				customText.setText(files[0]);
			}
		}
	}
}
