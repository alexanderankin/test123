/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2005 Marcelo Vanzin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package p4plugin.config;

//{{{ Imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

import projectviewer.config.ProjectOptions;
import projectviewer.vpt.VPTProject;
//}}}

/**
 *  The configuration pane for the P4Plugin, attached to the project
 *  options window.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4OptionPane extends AbstractOptionPane
                          implements ActionListener,
                                     ItemListener
{

    private P4Config    config;

    private JCheckBox   usePerforce;
    private JComboBox   editorType;
    private JTextField  editorCommand;
    private JTextField  client;
    private JTextField  user;

    public P4OptionPane() {
        super(jEdit.getProperty("p4plugin.config.project_option_pane.name"));
    }

    //{{{ _init() method
    /** Initializes the option pane. */
    protected void _init() {
        config = P4Config.getProjectConfig(ProjectOptions.getProject());

        usePerforce = new JCheckBox(jEdit.getProperty("p4plugin.project_cfg.use_perforce"));
        usePerforce.setSelected(config != null);
        usePerforce.addActionListener(this);
        addComponent(usePerforce);

        if (config == null) {
            config = new P4Config();
        }

        editorType = new JComboBox();
        editorType.addItem(jEdit.getProperty("p4plugin.project_cfg.editor_type.global"));
        editorType.addItem(jEdit.getProperty("p4plugin.project_cfg.editor_type.custom"));
        editorType.addItem(jEdit.getProperty("p4plugin.project_cfg.editor_type.no_editor"));
        editorType.setSelectedIndex(config.getEditorConfig());
        editorType.setEnabled(usePerforce.isSelected());
        addComponent(jEdit.getProperty("p4plugin.project_cfg.editor_type"), editorType);

        editorCommand = new JTextField(config.getEditor());
        editorCommand.setEnabled(shouldEnableCommandBox());
        addComponent(jEdit.getProperty("p4plugin.project_cfg.editor_cmd"), editorCommand);
        editorType.addItemListener(this);

        client = new JTextField(config.getClient());
        client.setEnabled(usePerforce.isSelected());
        addComponent(jEdit.getProperty("p4plugin.project_cfg.client"), client);

        user = new JTextField(config.getUser());
        user.setEnabled(usePerforce.isSelected());
        addComponent(jEdit.getProperty("p4plugin.project_cfg.user"), user);
    } //}}}

    //{{{ _save() method
    /** Saves the options. */
    protected void _save() {
        if (usePerforce.isSelected()) {
            config.setEditorConfig(editorType.getSelectedIndex());
            if (editorType.getSelectedIndex() == P4Config.P4EDITOR_USE_CUSTOM)
                config.setEditor(editorCommand.getText());
            else
                config.setEditor(null);
            config.setClient(client.getText());
            config.setUser(user.getText());
            setProjectConfig(config);
        } else {
            setProjectConfig(null);
        }
    } //}}}

    private boolean shouldEnableCommandBox() {
        return usePerforce.isSelected()
               && editorType.getSelectedIndex() == P4Config.P4EDITOR_USE_CUSTOM;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == usePerforce) {
            editorType.setEnabled(usePerforce.isSelected());
            editorCommand.setEnabled(shouldEnableCommandBox());
            client.setEnabled(usePerforce.isSelected());
            user.setEnabled(usePerforce.isSelected());
        }
    }

    public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == ItemEvent.SELECTED) {
            editorCommand.setEnabled(shouldEnableCommandBox());
            if (editorCommand.isEnabled())
                editorCommand.requestFocus();
        }
    }

    private void setProjectConfig(P4Config config) {
        VPTProject p = ProjectOptions.getProject();
        p.removeProperty(P4Config.KEY);
        if (config != null) {
            _set(p, P4Config.P4CONFIG_EDITOR_TYPE, String.valueOf(config.getEditorConfig()));
            _set(p, P4Config.P4CONFIG_EDITOR, config.getEditor());
            _set(p, P4Config.P4CONFIG_CLIENT, config.getClient());
            _set(p, P4Config.P4CONFIG_USER, config.getUser());
        } else {
            p.removeProperty(P4Config.P4CONFIG_EDITOR_TYPE);
            p.removeProperty(P4Config.P4CONFIG_EDITOR);
            p.removeProperty(P4Config.P4CONFIG_CLIENT);
            p.removeProperty(P4Config.P4CONFIG_USER);
        }
    }

    private void _set(VPTProject p, String prop, String value) {
        if (value != null) {
            p.setProperty(prop, value);
        } else {
            p.removeProperty(prop);
        }
    }

}

