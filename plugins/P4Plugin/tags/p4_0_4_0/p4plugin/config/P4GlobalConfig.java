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

    protected static final String   P4BINARY_OPT    = "p4plugin.p4command";
    protected static final String   P4DIFF_OPT      = "p4plugin.p4diff";
    protected static final String   P4DIFF_IGN_OPT  = "p4plugin.p4diff.ignore_output";
    protected static final String   P4EDITOR_OPT    = "p4plugin.p4editor";
    protected static final String   P4FMON_OPT      = "p4plugin.p4fmon";

    public static P4GlobalConfig getInstance() {
        return instance;
    }

    private P4GlobalConfig() {
        // no-op.
    }

    public String getPerforcePath() {
        return jEdit.getProperty(P4BINARY_OPT);
    }

    public String getEditor() {
        return jEdit.getProperty(P4EDITOR_OPT);
    }

    public boolean getMonitorFiles() {
        return jEdit.getBooleanProperty(P4FMON_OPT, false);
    }

    public String getDiffTool() {
        return jEdit.getProperty(P4DIFF_OPT);
    }

    public boolean getIgnoreDiffOutput() {
        return jEdit.getBooleanProperty(P4DIFF_IGN_OPT, false);
    }

}

