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

import java.awt.Rectangle;

import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.util.Log;

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

    public static void writeToShell(final String text) {
        Runnable r = new Runnable() {
            public void run() {
                DockableWindowManager mgr = jEdit.getActiveView().getDockableWindowManager();
                Console console = (Console) mgr.getDockable("console");
                if (console == null) {
                    jEdit.getAction("console").invoke(jEdit.getActiveView());
                    console = (Console) mgr.getDockable("console");
                }

                console.setShell(Shell.getShell(NAME));
                console.getOutput().print(console.getInfoColor(), text);

                JComponent output = console.getConsolePane();
                Rectangle end = new Rectangle(0, output.getHeight() - 2,
                                              output.getWidth(), 1);
                output.scrollRectToVisible(end);
            }
        };
        SwingUtilities.invokeLater(r);
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
        stopAnimation(console);
    }

    public void execute(Console console, String input, Output output,
                        Output error, String command)
    {
        stopAnimation(console);
    }

    private void stopAnimation(final Console console) {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    console.stopAnimation();
                }
            }
        );
    }

}

