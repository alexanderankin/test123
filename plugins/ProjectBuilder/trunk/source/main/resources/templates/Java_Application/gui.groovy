import groovy.swing.SwingBuilder
import org.gjt.sp.jedit.GUIUtilities
import org.gjt.sp.jedit.browser.VFSFileChooserDialog
import org.gjt.sp.jedit.browser.VFSBrowser
import java.awt.GridBagConstraints as GBC
import javax.swing.SwingConstants as SC
import javax.swing.ScrollPaneConstants as SP

lib_jars = []
dependencies = []
extras = []
def open_path = System.getProperty("user.home")+File.separator

def form = swing.panel() {
	gridBagLayout()
	def gbc = swing.gbc(gridx:0, gridy:0, weightx:0.0f, weighty:0.0f, gridwidth:1, gridheight:1, fill:GBC.HORIZONTAL)
	// Set up labels
	gbc.insets = [10, 10, 0, 0]
	label(text:'Main Class:', horizontalAlignment:SC.RIGHT, constraints:gbc)
	gbc.gridy++
	label(text:'Lib (included w/ app):', horizontalAlignment:SC.RIGHT, constraints:gbc)
	gbc.gridy++
	label(text:'Dependencies:', horizontalAlignment:SC.RIGHT, constraints:gbc)
	gbc.gridy++
	label(text:'Extras:', horizontalAlignment:SC.RIGHT, constraints:gbc)
	// Set up fields
	gbc.weightx = 1.0f
	gbc.weighty = 0.0f
	gbc.insets = [10, 5, 0, 0]
	gbc.gridy = 0
	gbc.gridx = 1
	textField(id:'class_field', columns:30, constraints:gbc)
	gbc.gridy++
	gbc.ipady = 40
	scrollPane(constraints:gbc) {
		textPane(id:'lib_jars_pane', text:"No jars selected")
	}
	gbc.gridy++
	scrollPane(constraints:gbc) {
		textPane(id:'dependencies_pane', text:"No jars selected")
	}
	gbc.gridy++
	scrollPane(constraints:gbc) {
		textPane(id:'extras_pane', text:"None")
	}
	gbc.ipady = 0
	// 'Add' button
	gbc.weightx = 0.0f
	gbc.gridx = 2
	gbc.gridy = 1
	button(icon:GUIUtilities.loadIcon('Plus.png'), constraints:gbc, actionPerformed: {
		VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, open_path, VFSBrowser.OPEN_DIALOG, true, true)
		def jars = chooser.getSelectedFiles()
		if (jars != null) {
			open_path = new File(jars[0]).getParent()+File.separator
			if (lib_jars.size() == 0) lib_jars_pane.text = ''
			for (jar in jars) {
				lib_jars.add(jar)
				lib_jars_pane.text += jar+'\n'
			}
		}
	})
	gbc.gridy++
	button(icon:GUIUtilities.loadIcon('Plus.png'), constraints:gbc, actionPerformed: {
		VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, open_path, VFSBrowser.OPEN_DIALOG, true, true)
		def jars = chooser.getSelectedFiles()
		if (jars != null) {
			open_path = new File(jars[0]).getParent()+File.separator
			if (lib_jars.size() == 0) dependencies_pane.text = ''
			for (jar in jars) {
				dependencies.add(jar)
				dependencies_pane.text += jar+'\n'
			}
		}
	})
	gbc.gridy++
	button(icon:GUIUtilities.loadIcon('Plus.png'), constraints:gbc, actionPerformed: {
		VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, open_path, VFSBrowser.OPEN_DIALOG, true, true)
		def extra_files = chooser.getSelectedFiles()
		if (extra_files != null) {
			open_path = new File(extra_files[0]).getParent()+File.separator
			if (extras.size() == 0) extras_pane.text = ''
			for (extra in extra_files) {
				extras.add(extra)
				extras_pane.text += extra+'\n'
			}
		}
	})
	gbc.gridx++
	button(icon:GUIUtilities.loadIcon('Open.png'), constraints:gbc, actionPerformed: {
		VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, open_path, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, true, true)
		def extra_dirs = chooser.getSelectedFiles()
		if (extra_dirs != null) {
			open_path = new File(extra_dirs[0]).getParent()+File.separator
			if (extras.size() == 0) extras_pane.text = ''
			for (extra in extra_dirs) {
				extras.add(extra)
				extras_pane.text += extra+'\n'
			}
		}
	})
}

return form
