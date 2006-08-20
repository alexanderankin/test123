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
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
//}}}

/**
 *  The global plugin configuration pane.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4GlobalOptionPane extends AbstractOptionPane {

    private P4Config config;

    private JCheckBox   monitorFiles;
    private JTextField  p4Path;
    private JTextField  editorCommand;

    public P4GlobalOptionPane() {
        super(jEdit.getProperty("p4plugin.config.global_option_pane.name"));
    }

    //{{{ _init() method
    protected void _init() {
        P4GlobalConfig config = P4GlobalConfig.getInstance();

        p4Path = new JTextField(config.getPerforcePath());
        addComponent(jEdit.getProperty("p4plugin.global_cfg.p4_path"), p4Path);

        editorCommand = new JTextField(config.getEditor());
        addComponent(jEdit.getProperty("p4plugin.global_cfg.editor_cmd"), editorCommand);

        monitorFiles = new JCheckBox(jEdit.getProperty("p4plugin.global_cfg.monitor_files"));
        monitorFiles.setToolTipText(jEdit.getProperty("p4plugin.global_cfg.monitor_files.tooltip"));
        monitorFiles.setSelected(config.getMonitorFiles());
        addComponent(monitorFiles);
    } //}}}

    //{{{ _save() method
    protected void _save() {
        P4GlobalConfig config = P4GlobalConfig.getInstance();
        config.setPerforcePath(p4Path.getText());
        config.setEditor(editorCommand.getText());
        config.setMonitorFiles(monitorFiles.isSelected());
    } //}}}

}

