import groovy.swing.SwingBuilder
import java.awt.GridBagConstraints as GBC
import javax.swing.SwingConstants as SC

import org.gjt.sp.jedit.Macros
import org.gjt.sp.jedit.jEdit

def form = swing.panel() {
	gridBagLayout()
	def gbc = swing.gbc(gridx:0, gridy:0, weightx:0.0f, weighty:0.0f, gridwidth:1, gridheight:1, fill:GBC.HORIZONTAL)
	// Set up labels
	gbc.insets = [10, 10, 0, 0]
	label(text:'Main Class', horizontalAlignment:SC.RIGHT, constraints:gbc)
	// Set up fields
	gbc.weightx = 1.0f
	gbc.weighty = 0.0f
	gbc.insets = [10, 5, 0, 0]
	gbc.gridy = 0
	gbc.gridx = 1
	textField(id:'class_field', columns:30, constraints:gbc)
}

return form
