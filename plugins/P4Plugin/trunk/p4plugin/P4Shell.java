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
package p4plugin;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;

import console.Console;
import console.Output;
import console.Shell;

/**
 *  A simple console shell to show perforce messages.
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.1
 */
public class P4Shell extends Shell {

    public static final String NAME = jEdit.getProperty("p4plugin.shell_name");

    public static void writeToShell(String text) {
        DockableWindowManager mgr = jEdit.getActiveView().getDockableWindowManager();
        Console console = (Console) mgr.getDockable("console");
        if (console == null) {
            jEdit.getAction("console").invoke(jEdit.getActiveView());
            console = (Console) mgr.getDockable("console");
        }
        P4Shell shell = (P4Shell) Shell.getShell(NAME);
        console.setShell(shell);
        console.getOutput().print(console.getInfoColor(), text);
    }

    public P4Shell() {
        super(NAME);
    }

    public void printInfoMessage(Output output) {
        DockableWindowManager mgr = jEdit.getActiveView().getDockableWindowManager();
        Console console = ( Console ) mgr.getDockable( "console" );
        output.print(console.getInfoColor(),
                     jEdit.getProperty("p4plugin.shell_info"));
    }

    public void stop(Console console) {
        // nothing to stop
    }

    public boolean waitFor(Console console) {
        // nothing to wait for.
        return true;
    }

    public void execute(Console console, Output output, String command) {
        // do nothing.
    }

    public void execute(Console console, String input, Output output,
                        Output error, String command)
    {
        // do nothing.
    }

}

