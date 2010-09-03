package groovy;
/**
 * @author Damien Radtke
 * class GroovyDocOptionPane
 * TODO: comment
 */
//{{{ Imports
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
//}}}
public class GroovyDocOptionPane extends AbstractOptionPane {

	private boolean isDownloaded;
	private JCheckBox downloadBox;
	private JCheckBox forceDownload;
	private JTextField downloadPath;
	private JLabel downloadStatus;

	private JButton downloadBtn;

	private GroovyPlugin plugin;

	public GroovyDocOptionPane() {
		super("groovy-doc");
		plugin = (GroovyPlugin) jEdit.getPlugin("groovy.GroovyPlugin");
	}

	protected void _init() {
		ActionHandler handler = new ActionHandler();
		downloadBtn = new JButton();

		isDownloaded = jEdit.getBooleanProperty(
			"options.groovy.docs-downloaded");

		// Status label
		downloadStatus = new JLabel();
		addComponent(downloadStatus);
		updateStatus();

		// Download url
		addComponent(jEdit.getProperty("options.groovy.downloadpath-label"),
			downloadPath = new JTextField(jEdit.getProperty(
				"options.groovy.docs-url")));

		downloadBtn.addActionListener(handler);
		addComponent(downloadBtn);

		addComponent(new JLabel(
			jEdit.getProperty("options.groovy.javadoc-msg")));

		/*
		// Download box
		downloadBox = new JCheckBox(jEdit.getProperty(
			"options.groovy.downloaddoc-label"), false);
		downloadBox.setSelected(isDownloaded);
		addComponent(downloadBox);

		// Force re-download
		forceDownload = new JCheckBox(jEdit.getProperty(
			"options.groovy.forcedownload-label"), false);
		forceDownload.setEnabled(isDownloaded);
		addComponent(forceDownload);
		addSeparator();

		addComponent(new JLabel("Check the above box and click OK to download the Groovy documentation."));
		addComponent(new JLabel("You can later remove it from your hard disk by un-ticking the box."));

		ActionHandler handler = new ActionHandler();
		downloadBox.addActionListener(handler);
		*/
	}

	protected void _save() {
		if (downloadBox.isSelected() &&
			(!isDownloaded || forceDownload.isSelected()))
		{
			// Ask download confirmation
			int answer = JOptionPane.showConfirmDialog(
				jEdit.getActiveView(),
				jEdit.getProperty(
					"options.groovy.download-docs-prompt.message"),
				jEdit.getProperty(
					"options.groovy.download-docs-prompt.title"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			if (answer == JOptionPane.NO_OPTION) {
				return;
			}
			plugin.downloadDocs();
		} else if (!downloadBox.isSelected() && isDownloaded) {
			int answer = JOptionPane.showConfirmDialog(
				jEdit.getActiveView(),
				jEdit.getProperty(
					"options.groovy.remove-docs-prompt.message"),
				jEdit.getProperty(
					"options.groovy.remove-docs-prompt.title"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			if (answer == JOptionPane.NO_OPTION) {
				return;
			}
			plugin.clearDocs();
		}
	}

	private void updateStatus() {
		String docDownloadInfo = "<html>"+jEdit.getProperty(
				"options.groovy.docdownloaded-label");
		if (isDownloaded) {
			downloadBtn.setText("Remove Documentation");
			docDownloadInfo += "<strong><font color=\"green\">"+
				jEdit.getProperty("options.groovy.downloaded-label.yes")+
				"</font></strong>";
			docDownloadInfo += " ( in "+jEdit.getPlugin(
				"groovy.GroovyPlugin").getPluginHome()+")";
		} else {
			downloadBtn.setText("Download Documentation");
			docDownloadInfo += "<strong><font color=\"red\">"+
				jEdit.getProperty("options.groovy.downloaded-label.no")
				+"</font></strong>";
		}
		docDownloadInfo += "</html>";
		downloadStatus.setText(docDownloadInfo);
	}

	class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			/*
			forceDownload.setEnabled(downloadBox.isSelected() &&
				isDownloaded);
			*/
			Object source = e.getSource();
			if (source == downloadBtn) {
				downloadBtn.setEnabled(false);
				if (!isDownloaded) {
					// Prompt to download
					int answer = JOptionPane.showConfirmDialog(
						jEdit.getActiveView(),
						jEdit.getProperty(
							"options.groovy.download-docs-prompt.message"),
						jEdit.getProperty(
							"options.groovy.download-docs-prompt.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
					if (answer != JOptionPane.YES_OPTION) {
						downloadBtn.setEnabled(true);
						return;
					}
					plugin.downloadDocs();
					downloadBtn.setText("Downloading...");
					// New thread to wait for the download to finish
					new Thread() {
						public void run() {
							while (!jEdit.getBooleanProperty(
								"options.groovy.docs-downloaded"))
							{
								try {
									Thread.sleep(500);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							downloadBtn.setText("Remove Documentation");
							downloadBtn.setEnabled(true);
							isDownloaded = true;
							updateStatus();
						}
					}.start();
				} else {
					// Prompt to remove documentation
					int answer = JOptionPane.showConfirmDialog(
						jEdit.getActiveView(),
						jEdit.getProperty(
							"options.groovy.remove-docs-prompt.message"),
						jEdit.getProperty(
							"options.groovy.remove-docs-prompt.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
					if (answer != JOptionPane.YES_OPTION) {
						downloadBtn.setEnabled(true);
						return;
					}
					// No new thread needed, this method is single-threaded
					plugin.clearDocs();
					downloadBtn.setText("Download Documentation");
					downloadBtn.setEnabled(true);
					isDownloaded = false;
					updateStatus();
				}
			}
		}
	}
}
