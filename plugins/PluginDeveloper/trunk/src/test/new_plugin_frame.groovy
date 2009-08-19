import groovy.swing.SwingBuilder
import java.awt.Color
import java.awt.GridBagConstraints as GBC
import javax.swing.JTabbedPane
import javax.swing.WindowConstants as WC
import javax.swing.SwingConstants as SC
import plugindeveloper.PluginConfiguration
import javax.swing.DefaultListModel

URL propsUrl = getClass().getResource("/PluginDeveloper.props");
def config = PluginConfiguration.load(propsUrl.getPath());

def builder = new SwingBuilder();
def frame = builder.frame(id: "frame", title: "Create a new Plugin", defaultCloseOperation: WC.DISPOSE_ON_CLOSE) {
    gridBagLayout()
    def gbc = gbc(gridx: 0, gridy: 0, weightx: 1.0f, weighty: 1.0f, gridwidth: 1, gridheight: 1, fill: GBC.BOTH, )
    tabbedPane(constraints: gbc) {
        panel(title: "Information") {
            gridBagLayout()
            // set up the labels.
            gbc.insets = [10, 10, 0, 0]
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 0.0f
            gbc.weighty = 0.0f
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
        panel(title: "Menus and Option Panes") {
            gridBagLayout()
            // set up the labels.
            gbc.insets = [10, 10, 0, 0]
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 0.0f
            gbc.weighty = 0.0f
            label(text: "Menu Items:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.insets = [5, 10, 0, 0]
            gbc.gridy++
            gbc.gridy++
            label(text: "Menu Item Code:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.gridy++
            label(text: "Option Panes:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.gridy++
            label(text: "Option Pane Code:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)

            // set up fields
            gbc.gridx = 1
            gbc.gridy = 0
            gbc.weightx = 1.0f
            gbc.weighty = 0.0f
            gbc.insets = [10, 5, 0, 10]
            def menuItemListModel = new DefaultListModel()
            menuItemListModel.addElement("1")
            menuItemListModel.addElement("2")
            menuItemListModel.addElement("3")
            menuItemListModel.addElement("-")
            menuItemListModel.addElement("4")
            menuItemListModel.addElement("5")
            scrollPane(constraints: gbc) {
                list(id: "menu_item_field", model: menuItemListModel, visibleRowCount: 4)
            }
            gbc.gridy++
            gbc.weighty = 0.0f
            def row = gbc.gridy
            def col = gbc.gridx
            gbc.insets = [2, 5, 0, 10]
            // The button panel for menu items.
            panel(constraints: gbc) {
                gridBagLayout()
                gbc.gridx = 0
                gbc.gridy = 0
                gbc.weightx = 1.0f
                label(text: "", constraints: gbc)
                gbc.gridx++
                gbc.weightx = 0.0f
                button(text: "+", constraints: gbc)
                gbc.gridx++
                button(text: "-", constraints: gbc)
                gbc.gridx++
                button(text: "<", constraints: gbc)
                gbc.gridx++
                button(text: ">", constraints: gbc)
            }
            gbc.gridx = col
            gbc.gridy = row + 1
            gbc.weightx = 1.0f
            gbc.weighty = 1.0f
            gbc.insets = [10, 5, 0, 10]
            scrollPane(constraints: gbc) {
                textArea(id: "menu_item_code_field", columns: 40, rows: 5, constraints: gbc)
            }
            gbc.gridy++
            gbc.weighty = 0.0f
            def optionPaneListModel = new DefaultListModel()
            optionPaneListModel.addElement("1")
            optionPaneListModel.addElement("2")
            optionPaneListModel.addElement("3")
            optionPaneListModel.addElement("-")
            optionPaneListModel.addElement("4")
            optionPaneListModel.addElement("5")
            scrollPane(constraints: gbc) {
                list(id: "option_pane_field", model: optionPaneListModel, visibleRowCount: 4)
            }
            gbc.gridy++
            gbc.weighty = 1.0f
            scrollPane(constraints: gbc) {
                textArea(id: "option_pane_code_field", columns: 40, rows: 5, constraints: gbc)
            }

        }
        panel(title: "Dependencies") {
            gridBagLayout()
            // set up the labels.
            gbc.insets = [10, 10, 0, 0]
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 0.0f
            gbc.weighty = 0.0f
            label(text: "jEdit:", horizontalAlignment: SC.RIGHT, constraints: gbc)
            gbc.insets = [5, 10, 0, 0]
            gbc.gridy++
            label(text: "Java:", horizontalAlignment: SC.RIGHT, constraints: gbc)
            gbc.gridy++
            label(text: "Jars:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.gridy++
            label(text: "Files:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.gridy++
            label(text: "Plugins:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)
            gbc.gridy++
            label(text: "Optional Plugins:", horizontalAlignment: SC.RIGHT, verticalAlignment: SC.TOP, constraints: gbc)

            // set up fields
            gbc.gridx = 1
            gbc.gridy = 0
            gbc.weightx = 1.0f
            gbc.insets = [10, 5, 0, 10]
            textField(id: "jedit_version_field", text: "04.03.17.00", constraints: gbc)
            gbc.insets = [5, 5, 0, 10]
            gbc.gridy++
            comboBox(id: "java_version_field", items: ["1.4", "5", "6", "7"], constraints: gbc)
            gbc.gridy++
            scrollPane(constraints: gbc) {
                textArea(id: "jars_field", text: "", columns: 40, rows: 4, lineWrap: false, wrapStyleWord: false, editable: false)
            }
            gbc.gridy++
            scrollPane(constraints: gbc) {
                textArea(id: "files_field", text: "", columns: 40, rows: 4, lineWrap: false, wrapStyleWord: false, editable: false)
            }
            gbc.gridy++
            gbc.weighty = 1.0f
            scrollPane(constraints: gbc) {
                textArea(id: "plugins_field", text: "", columns: 40, rows: 4, lineWrap: false, wrapStyleWord: false, editable: false)
            }
            gbc.gridy++
            gbc.weighty = 0.0f
            gbc.insets = [5, 5, 10, 10]
            scrollPane(constraints: gbc) {
                textArea(id: "opt_plugins_field", text: "", columns: 40, rows: 4, lineWrap: false, wrapStyleWord: false, editable: false)
            }
        }
    }
	
    // add the buttons
    gbc.gridy++
    gbc.gridx = 0
    gbc.weighty = 0.0f
    panel(constraints: gbc) {
        gridBagLayout()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0f
        gbc.weighty = 0.0f
        gbc.gridwidth = 1
        gbc.gridheight = 1
        gbc.fill = GBC.BOTH
        gbc.insets = [5, 10, 10, 0]
        label(text: "", constraints: gbc)
        gbc.gridx++
        gbc.weightx = 0.0f
        button(text: "Save", constraints: gbc)
        gbc.gridx++
        button(text: "Cancel", constraints: gbc)
    }
	
}
frame.pack()
frame.setLocationRelativeTo(null)
frame.show()
