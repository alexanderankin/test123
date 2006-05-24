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

import org.gjt.sp.jedit.jEdit;

/**
 *  Global configuration data for the perforce plugin.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4GlobalConfig {

    private static final P4GlobalConfig instance = new P4GlobalConfig();

    private static final String     P4BINARY_OPT    = "p4plugin.p4command";
    private static final String     P4EDITOR_OPT    = "p4plugin.p4editor";
    private static final String     P4FMON_OPT      = "p4plugin.p4fmon";

    public static P4GlobalConfig getInstance() {
        return instance;
    }

    private P4GlobalConfig() {
        // no-op.
    }

    public String getPerforcePath() {
        return jEdit.getProperty(P4BINARY_OPT);
    }

    public void setPerforcePath(String path) {
        if (path != null && path.length() > 0)
            jEdit.setProperty(P4BINARY_OPT, path);
        else
            jEdit.unsetProperty(P4BINARY_OPT);
    }

    public String getEditor() {
        return jEdit.getProperty(P4EDITOR_OPT);
    }

    public void setEditor(String cmd) {
        if (cmd != null && cmd.length() > 0)
            jEdit.setProperty(P4EDITOR_OPT, cmd);
        else
            jEdit.unsetProperty(P4EDITOR_OPT);
    }

    public boolean getMonitorFiles() {
        return jEdit.getBooleanProperty(P4FMON_OPT, false);
    }

    public void setMonitorFiles(boolean flag) {
        jEdit.setBooleanProperty(P4FMON_OPT, flag);
    }

}

