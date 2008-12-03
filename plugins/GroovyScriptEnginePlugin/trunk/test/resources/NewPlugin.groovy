import groovy.swing.*;
import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;

SCRIPTVALUE = ""

def formData = {}

def swingBuilder = SwingBuilder.build() {
	frame(title:"Create a new jEdit Plugin", pack:true, visible:true, defaultCloseOperation:JFrame.DISPOSE_ON_CLOSE) {
		panel(layout:new MigLayout('fill')) {
			label(text:"Plugin Name:", horizontalAlignment: JLabel.RIGHT, constraints: "grow")
			textField(id:"pluginNameField", columns:20, constraints: "span 2, wrap, grow")
			label(text:"Package:", horizontalAlignment: JLabel.RIGHT, constraints: "grow")
			textField(id:"packageField", columns:20, constraints: "span 2, wrap, grow")
			label(text:"Summary:", horizontalAlignment: JLabel.RIGHT, constraints: "grow")
			textField(id:"summaryField", columns:20, constraints: "span 2, wrap, grow")
			label(text:"Description:", horizontalAlignment: JLabel.RIGHT, constraints: "grow")
			textField(id:"descriptionField", columns:20, constraints: "span 2, wrap, grow")
			button(text:"Create", constraints: "span 2, align right", actionPerformed: {
				println formData.pluginName
				println formData.packageName
			})
			button(text:"Cancel", constraints: "align right")
		}
	}
	bind(source:pluginNameField, sourceProperty:"text", target:formData, targetProperty:"pluginName")
	bind(source:packageField, sourceProperty:"text", target:formData, targetProperty:"packageName")
};
