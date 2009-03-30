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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

import projectviewer.vpt.VPTProject;

/**
 *  The configuration pane for the P4Plugin, attached to the project
 *  options window.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4OptionPane extends AbstractOptionPane
                          implements ItemListener
{

    private P4Config    config;
    private VPTProject  proj;

    private JComboBox   editorType;
    private JTextField  editorCommand;
    private JTextField  client;
    private JTextField  p4config;
    private JTextField  user;

    public P4OptionPane(VPTProject proj) {
        super("p4-plugin-pv-cfg");
        this.proj = proj;
    }

    //{{{ _init() method
    /** Initializes the option pane. */
    protected void _init() {
        config = P4Config.getProjectConfig(proj);

        if (config == null) {
            config = new P4Config();
        }

        editorType = new JComboBox();
        editorType.addItem(jEdit.getProperty("p4plugin.project_cfg.editor_type.global"));
        editorType.addItem(jEdit.getProperty("p4plugin.project_cfg.editor_type.custom"));
        editorType.addItem(jEdit.getProperty("p4plugin.project_cfg.editor_type.no_editor"));
        editorType.setSelectedIndex(config.getEditorConfig());
        addComponent(jEdit.getProperty("p4plugin.project_cfg.editor_type"), editorType);

        editorCommand = new JTextField(config.getEditor());
        editorCommand.setEnabled(shouldEnableCommandBox());
        addComponent(jEdit.getProperty("p4plugin.project_cfg.editor_cmd"), editorCommand);
        editorType.addItemListener(this);

        KeyHandler khandler = new KeyHandler();

        client = new JTextField(config.getClient());
        client.addKeyListener(khandler);
        client.setEnabled(config.getConfig() == null);
        addComponent(jEdit.getProperty("p4plugin.project_cfg.client"), client);

        p4config = new JTextField(config.getConfig());
        p4config.setEnabled(config.getClient() == null);
        p4config.addKeyListener(khandler);
        addComponent(jEdit.getProperty("p4plugin.project_cfg.p4config"), p4config);

        user = new JTextField(config.getUser());
        addComponent(jEdit.getProperty("p4plugin.project_cfg.user"), user);
    } //}}}

    //{{{ _save() method
    /** Saves the options. */
    protected void _save() {
        config.setEditorConfig(editorType.getSelectedIndex());
        if (editorType.getSelectedIndex() == P4Config.P4EDITOR_USE_CUSTOM)
            config.setEditor(editorCommand.getText());
        else
            config.setEditor(null);
        config.setClient(client.getText());
        config.setConfig(p4config.getText());
        config.setUser(user.getText());
        setProjectConfig(config);
    } //}}}

    private boolean shouldEnableCommandBox() {
        return (editorType.getSelectedIndex() == P4Config.P4EDITOR_USE_CUSTOM);
    }

    public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == ItemEvent.SELECTED) {
            editorCommand.setEnabled(shouldEnableCommandBox());
            if (editorCommand.isEnabled())
                editorCommand.requestFocus();
        }
    }

    private void setProjectConfig(P4Config config) {
        proj.removeProperty(P4Config.KEY);
        if (config != null) {
            config.save(proj.getProperties());
        } else {
            config = new P4Config();
            config.clean(proj.getProperties());
        }
    }

    private class KeyHandler extends KeyAdapter {

        public void keyReleased(KeyEvent ke) {
            if (ke.getSource() == client) {
                p4config.setEnabled(client.getText().length() == 0);
            } else if (ke.getSource() == p4config) {
                client.setEnabled(p4config.getText().length() == 0);
            }
        }

    }

}

