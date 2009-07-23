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

import java.util.LinkedList;
import java.util.List;

import common.gui.EasyOptionPane;

/**
 *  The global plugin configuration pane.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4GlobalOptionPane extends EasyOptionPane {

    public P4GlobalOptionPane() {
        super("p4plugin.config.global_option_pane");

        List spec = new LinkedList();
        spec.add("file,p4plugin.global_cfg.p4_path,"
                    + P4GlobalConfig.P4BINARY_OPT);
        spec.add("file,p4plugin.global_cfg.editor_cmd,"
                    + P4GlobalConfig.P4EDITOR_OPT);
        spec.add("file,p4plugin.global_cfg.diff_cmd,"
                    + P4GlobalConfig.P4DIFF_OPT);
        spec.add("checkbox,p4plugin.global_cfg.ignore_diff_output,"
                    + P4GlobalConfig.P4DIFF_IGN_OPT);
        spec.add("checkbox,p4plugin.global_cfg.monitor_files,"
                    + P4GlobalConfig.P4FMON_OPT);
        setComponentSpec(spec);
    }

}

