/*
 *  Copyright (c) 2009, Eric Berry <elberry@gmail.com>
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  	* Redistributions of source code must retain the above copyright notice, this
 *  	  list of conditions and the following disclaimer.
 *  	* Redistributions in binary form must reproduce the above copyright notice,
 *  	  this list of conditions and the following disclaimer in the documentation
 *  	  and/or other materials provided with the distribution.
 *  	* Neither the name of the Organization (TellurianRing.com) nor the names of
 *  	  its contributors may be used to endorse or promote products derived from
 *  	  this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package plugindeveloper.ui
import javax.swing.JPanel
import plugindeveloper.PluginConfiguration
import groovy.swing.SwingBuilder

/**
 *
 * @author eberry
 */
public class PluginInformationPanel {

    private PluginConfiguration config
    private ResourceBundle messages
    
    public PluginInformationPanel(ResourceBundle messages) {
        this(null, messages)
    }

    public PluginInformationPanel(String pluginPropsPath, ResourceBundle messages) {
        config = PluginConfiguration.load(propsPath)
        this.messages = messages
    }

    public JPanel create() {
        def builder = new SwingBuilder();
        def panel = builder.panel(title: "Information") {
            gridBagLayout()
            // set up the labels.
            gbc.insets = [10, 10, 0, 0]
            label(text: "Name:", horizontalAlignment: SC.RIGHT, constraints: gbc)
            gbc.insets = [5, 10, 0, 0]
            gbc.gridy++
            label(text: "Version:", horizontalAlignment: SC.RIGHT, constraints: gbc)
            gbc.gridy++
            label(text: "Author:", horizontalAlignment: SC.RIGHT, constraints: gbc)
            gbc.gridy++
            label(text: "Storage:", horizontalAlignment: SC.RIGHT, constraints: gbc)
            gbc.gridy++
            label(text: "Activation:", horizontalAlignment: SC.RIGHT, constraints: gbc)
            gbc.gridy++
            label(text: "Startup Properties:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.gridy++
            label(text: "Summary:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.gridy++
            label(text: "Description:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)

            // set up fields
            gbc.gridx = 1
            gbc.gridy = 0
            gbc.weightx = 1.0f
            gbc.insets = [10, 5, 0, 10]
            textField(id: "name_field", text: config.pluginName, constraints: gbc)
            bind(source: name_field, sourceProperty: 'text', target: config, targetProperty: 'pluginName')
            gbc.insets = [5, 5, 0, 10]
            gbc.gridy++
            textField(id: "version_field", text: config.version, constraints: gbc)
            gbc.gridy++
            textField(id: "author_field", text: config.author, constraints: gbc)
            gbc.gridy++
            checkBox(id: "storage_field", text: "<html>This plugin saves user settings, and other data to disk.</html>", horizontalTextPosition: SC.RIGHT, constraints: gbc)
            gbc.gridy++
            comboBox(id: "activation_field", items: ["The first time plugin is used.", "When jEdit starts up.", "When the following properties are set."], constraints: gbc, itemStateChanged: {
                    def useProperties = activation_field.selectedIndex == 2
                    builder.start_props_field.editable = useProperties
                })
            gbc.gridy++
            scrollPane(constraints: gbc) {
                textArea(id: "start_props_field", text: "", columns: 40, rows: 4, lineWrap: true, wrapStyleWord: true, editable: false)
            }
            gbc.gridy++
            scrollPane(constraints: gbc) {
                textArea(id: "summary_field", text: "", columns: 40, rows: 4, lineWrap: true, wrapStyleWord: true, editable: false)
            }
            gbc.gridy++
            gbc.weighty = 1.0f
            gbc.insets = [5, 5, 10, 10]
            scrollPane(constraints: gbc) {
                textArea(id: "description_field", text: "", columns: 40, rows: 8, lineWrap: true, wrapStyleWord: true, editable: false)
            }
        }
        
        return panel;
    }
}

