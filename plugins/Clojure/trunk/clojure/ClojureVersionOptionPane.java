package clojure;
/**
 * @author Damien Radtke
 * class ClojureVersionOptionPane
 * An option pane that can be used to configure the clojure jars
 */
//{{{ Imports
import clojure.ClojurePlugin;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.util.ThreadUtilities;
//}}}

public class ClojureVersionOptionPane extends AbstractOptionPane {
	private ClojureDownloader.AvailableVersions versions;

	private JComboBox channelBox;
	private JComboBox versionBox;

	private ClojurePlugin plugin;

	public ClojureVersionOptionPane() {
		super("clojure-version");
		plugin = (ClojurePlugin) jEdit.getPlugin("clojure.ClojurePlugin");

		channelBox = new JComboBox(new String[] { "Stable", "RC", "Beta", "Alpha" });
		channelBox.setEnabled(false);
		versionBox = new JComboBox(new String[] {});
		versionBox.setEnabled(false);

		channelBox.addItemListener(new ChannelListener());

		ThreadUtilities.runInBackground(new Runnable() {
			public void run() {
				versions = ClojureDownloader.getAvailableVersions(ClojureRepository.CLOJURE_URL);
				updateVersions(jEdit.getProperty("clojure.channel", "Stable"));
				versionBox.setSelectedItem(jEdit.getProperty("clojure.version"));
				channelBox.setEnabled(true);
				versionBox.setEnabled(true);
			}
		});
	}

	private void updateVersions(String channel) {
		if (versions == null) {
			return;
		}

		if ("Stable".equals(channel)) {
			versionBox.setModel(new DefaultComboBoxModel(versions.stable.toArray()));
		} else if ("RC".equals(channel)) {
			versionBox.setModel(new DefaultComboBoxModel(versions.rc.toArray()));
		} else if ("Beta".equals(channel)) {
			versionBox.setModel(new DefaultComboBoxModel(versions.beta.toArray()));
		} else if ("Alpha".equals(channel)) {
			versionBox.setModel(new DefaultComboBoxModel(versions.alpha.toArray()));
		}
	}

	protected void _init() {
		addComponent("Channel:", channelBox);
		addComponent("Version:", versionBox);
	}

	protected void _save() {
		String version = (String)versionBox.getSelectedItem();
		String channel = (String)channelBox.getSelectedItem();
		jEdit.setProperty("clojure.channel", channel);
		jEdit.setProperty("clojure.version", version);
		ClojureDownloader.download(version);

		plugin.setVars();
	}

	class ChannelListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				updateVersions((String)e.getItem());
			}
		}
	}
}
