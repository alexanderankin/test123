import groovy.swing.*;
import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;

SCRIPTVALUE = ""

def newPluginData = {}

def swingBuilder = SwingBuilder.build() {
	frame(id: "newPluginForm", title:"Create a new jEdit Plugin", pack:true, visible:true, defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE) {
		panel(layout:new MigLayout('fill')) {
			label(text:"Plugin Name:", horizontalAlignment: JLabel.RIGHT, constraints: "growx")
			textField(id:"pluginNameField", columns:20, constraints: "span 2, wrap, growx", keyReleased: {
				packageField.text = pluginNameField.text.toLowerCase();
			})
			label(text:"Package:", horizontalAlignment: JLabel.RIGHT, constraints: "growx")
			textField(id:"packageField", columns:20, constraints: "span 2, wrap, growx")
			label(text:"Summary:", horizontalAlignment: JLabel.RIGHT, constraints: "growx")
			textArea(id:"summaryField", columns:20, constraints: "span 2, wrap, grow")
			label(text:"Description:", horizontalAlignment: JLabel.RIGHT, constraints: "growx")
			textArea(id:"descriptionField", columns:20, constraints: "span 2, wrap, grow")
			button(text:"Create", constraints: "span 2, align right", actionPerformed: {
				println "Plugin Name: ${newPluginData.pluginName}"
				println "    Package: ${newPluginData.packageName}"
				println "    Summary: ${newPluginData.summary}"
				println "Description: ${newPluginData.description}"
				newPluginForm.dispose();
			})
			button(text:"Cancel", constraints: "align right", actionPerformed: {
				newPluginForm.dispose();
			})
		}
	}
	bind(source:pluginNameField, sourceProperty:"text", target:newPluginData, targetProperty:"pluginName")
	bind(source:packageField, sourceProperty:"text", target:newPluginData, targetProperty:"packageName")
	bind(source:summaryField, sourceProperty:"text", target:newPluginData, targetProperty:"summary")
	bind(source:descriptionField, sourceProperty:"text", target:newPluginData, targetProperty:"description")
};
