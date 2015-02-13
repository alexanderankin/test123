package clojure;
/**
 * @author Damien Radtke
 * class ClojureLibsOptionPane
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
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;
//}}}

public class ClojureLibsOptionPane extends AbstractOptionPane {
	private ArrayList<String> availableLibs;
	private HashMap<String, ArrayList<String>> availableLibVersions;
	private JButton addLibBtn;
	private ArrayList<Lib> libPanels;

	private ClojurePlugin plugin;

	public ClojureLibsOptionPane() {
		super("clojure-libs");
		plugin = (ClojurePlugin) jEdit.getPlugin("clojure.ClojurePlugin");
		ThreadUtilities.runInBackground(new Runnable() {
			public void run() {
				availableLibs = ClojureDownloader.getAvailableLibs();
				availableLibs.add(0, "");

				for (String lib : jEdit.getProperty("clojure.libs", "").split(" ")) {
					if ("".equals(lib)) {
						continue;
					}
					Log.log(Log.DEBUG, this, "Initializing with lib: " + lib);
					libPanels.add(new Lib(lib, jEdit.getProperty("clojure." + lib + ".version")));
				}

				draw();
			}
		});
	}

	protected void _init() {
		addLibBtn = new JButton("Add Library", GUIUtilities.loadIcon(jEdit.getProperty("options.toolbar.add.icon")));
		addLibBtn.addActionListener(new AddListener());
		libPanels = new ArrayList<Lib>(); // TODO: populate with existing

		draw();
	}

	protected void _save() {
		for (String lib : jEdit.getProperty("clojure.libs", "").split(" ")) {
			if ("".equals(lib)) {
				continue;
			}
			Log.log(Log.DEBUG, this, "Deleting lib: " + lib);
			new File(jEdit.getProperty("clojure." + lib + ".path")).delete();
			jEdit.unsetProperty("clojure." + lib + ".version");
			jEdit.unsetProperty("clojure." + lib + ".path");
		}

		StringBuilder libsBuilder = new StringBuilder();
		for (Lib l : libPanels) {
			String lib = l.getSelectedLibrary();
			String version = l.getSelectedVersion();
			if (lib == null || lib.equals("") || version == null || version.equals("")) {
				continue;
			}
			libsBuilder.append(lib + " ");
			ClojureDownloader.downloadLib(lib, version);
		}

		jEdit.setProperty("clojure.libs", libsBuilder.toString().trim());
	}

	private void draw() {
		removeAll();
		if (libPanels.size() > 0) {
			for (Lib l : libPanels) {
				addComponent(l.panel, GridBagConstraints.HORIZONTAL);
			}
			addSeparator();
		}
		addComponent(addLibBtn, GridBagConstraints.HORIZONTAL);

		revalidate();
		repaint();
	}

	class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			libPanels.add(new Lib());
			draw();
		}
	}

	class Lib {
		Lib self;
		public JPanel panel;
		JComboBox libBox;
		JComboBox versionBox;
		JButton removeBtn;

		public Lib() {
			self = this;
			this.panel = new JPanel();
			this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.X_AXIS));

			this.libBox = new JComboBox(availableLibs.toArray());
			this.versionBox = new JComboBox(new String[] {});
			this.removeBtn = new JButton(GUIUtilities.loadIcon(jEdit.getProperty("options.toolbar.remove.icon")));

			JPanel libPanel = new JPanel();
			libPanel.setLayout(new GridLayout(1, 2, 6, 0));
			libPanel.add(libBox);
			libPanel.add(versionBox);

			this.panel.add(libPanel);
			this.panel.add(Box.createHorizontalStrut(6));
			this.panel.add(removeBtn);

			this.libBox.addItemListener(new LibChangeListener());
			this.removeBtn.addActionListener(new LibRemoveListener());
		}

		public Lib(String name, String version) {
			this();
			this.libBox.setSelectedItem(name);
			this.versionBox.setSelectedItem(version);
		}

		public String getSelectedLibrary() {
			return (String)this.libBox.getSelectedItem();
		}

		public String getSelectedVersion() {
			return (String)this.versionBox.getSelectedItem();
		}

		class LibChangeListener implements ItemListener {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String lib = (String)e.getItem();
					if (lib == null || lib.equals("")) {
						versionBox.setModel(new DefaultComboBoxModel(new String[] {}));
						return;
					}

					if (availableLibVersions == null) {
						availableLibVersions = new HashMap<String, ArrayList<String>>();
					}

					ArrayList<String> versions;
					if (!availableLibVersions.containsKey(lib)) {
						versions = ClojureDownloader.getAvailableVersions(ClojureRepository.ROOT_URL + lib + "/").stable;
						availableLibVersions.put(lib, versions);
					} else {
						versions = availableLibVersions.get(lib);
					}

					versionBox.setModel(new DefaultComboBoxModel(versions.toArray()));
				}
			}
		}

		class LibRemoveListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				libPanels.remove(self);
				draw();
			}
		}
	}
}
