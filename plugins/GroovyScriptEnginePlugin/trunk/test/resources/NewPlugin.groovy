import groovy.swing.*;
import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;

// used when executing through ScriptEnginePlugin - can be removed when turned into actual plugin.
SCRIPTVALUE = ""

def newPluginData = {}

def fieldLength = 40
def summaryRowLength = 4
def descriptionRowLength = 8
def rowGap = "5"

def swingBuilder = SwingBuilder.build() {
	// creating new frame.
	frame(id: "newPluginForm", title:"Create a new jEdit Plugin", defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE) {
		// creating panel to house our form.
		panel(layout:new MigLayout('fill')) {
			// Plugin name label and field.
			label(text:"Plugin Name:", constraints: "alignx right, aligny top, gapBottom 5")
			textField(id:"pluginNameField", columns:fieldLength, constraints: "span 2, wrap, growx, gapBottom 5", keyReleased: {
				packageField.text = pluginNameField.text.toLowerCase()
			})
			
			// Package name label and field.
			label(text:"Package:", constraints: "alignx right, aligny top, gapBottom 5")
			textField(id:"packageField", columns:fieldLength, constraints: "span 2, wrap, growx, gapBottom 5")
			
			// Summary label and field.
			label(text:"Summary:", constraints: "alignx right, aligny top, gapBottom 5")
			textArea(id:"summaryField", columns:fieldLength, rows: summaryRowLength, constraints: "span 2, wrap, grow, gapBottom 5")
			
			// Description label and field.
			label(text:"Description:", constraints: "alignx right, aligny top, gapBottom 5")
			textArea(id:"descriptionField", columns:fieldLength, rows: descriptionRowLength, constraints: "span 2, wrap, grow, gapBottom 5")
			
			// Action buttons.
			button(text:"Create", constraints: "span 2, align right", actionPerformed: {
				println "Plugin Name: ${newPluginData.pluginName}"
				println "    Package: ${newPluginData.packageName}"
				println "    Summary: ${newPluginData.summary}"
				println "Description: ${newPluginData.description}"
				newPluginForm.dispose()
			})
			button(text:"Cancel", constraints: "align right", actionPerformed: {
				newPluginForm.dispose()
			})
		}
	}
	// using pack, and locationRelativeTo null, to center the frame.
	newPluginForm.pack()
	newPluginForm.locationRelativeTo = null
	newPluginForm.visible = true
	
	// binding the field values to newPluginData.
	bind(source:pluginNameField, sourceProperty:"text", target:newPluginData, targetProperty:"pluginName")
	bind(source:packageField, sourceProperty:"text", target:newPluginData, targetProperty:"packageName")
	bind(source:summaryField, sourceProperty:"text", target:newPluginData, targetProperty:"summary")
	bind(source:descriptionField, sourceProperty:"text", target:newPluginData, targetProperty:"description")
}
