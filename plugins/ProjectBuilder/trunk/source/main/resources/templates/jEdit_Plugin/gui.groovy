import groovy.swing.SwingBuilder
import java.awt.GridBagConstraints as GBC
import javax.swing.SwingConstants as SC
import org.gjt.sp.jedit.browser.VFSFileChooserDialog
import org.gjt.sp.jedit.browser.VFSBrowser

def form = swing.panel() {
	gridBagLayout()
	def gbc = swing.gbc(gridx:0, gridy:0, weightx:0.0f, weighty:0.0f, gridwidth:1, gridheight:1, fill:GBC.HORIZONTAL)
	// Set up labels
	gbc.insets = [10, 10, 0, 0]
	label(text:'Build Support', horizontalAlignment:SC.RIGHT, constraints:gbc)
	gbc.gridy++
	gbc.gridx++
	label(text:'<html><small>* The following are necessary for building user documentation</small></html>', constraints:gbc)
	gbc.gridy++
	gbc.gridx--
	label(text:'Docbook XSL', horizontalAlignment:SC.RIGHT, constraints:gbc)
	gbc.gridy++
	label(text:'DTD Catalog', horizontalAlignment:SC.RIGHT, constraints:gbc)
	// Set up fields
	gbc.weightx = 1.0f
	gbc.weighty = 0.0f
	gbc.insets = [10, 5, 0, 0]
	gbc.gridy = 0
	gbc.gridx = 1
	textField(id:'build_support_field', columns:30, constraints:gbc)
	gbc.gridy += 2
	textField(id:'docbook_xsl_field', columns:30, constraints:gbc)
	gbc.gridy++
	textField(id:'dtd_catalog_field', columns:30, constraints:gbc)
	// Add a browse button
	gbc.gridy = 0
	gbc.gridx = 2
	button(text:'...', constraints:gbc, actionPerformed: {
		VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, project.getRootPath(), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false, true)
		build_support_field.text = chooser.selectedFiles?.getAt(0)
	})
	gbc.gridy += 2
	button(text:'...', constraints:gbc, actionPerformed: {
		VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, project.getRootPath(), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false, true)
		docbook_xsl_field.text = chooser.selectedFiles?.getAt(0)
	})
	gbc.gridy++
	button(text:'...', constraints:gbc, actionPerformed: {
		VFSFileChooserDialog chooser = new VFSFileChooserDialog(view, project.getRootPath(), VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false, true)
		dtd_catalog_field.text = chooser.selectedFiles?.getAt(0)
	})
}

return form
