/*
Copyright (C) 2007  Shlomy Reinstein

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package perl.output;

import perl.core.CommandManager;
import perl.core.Debugger;
import perl.core.GdbState;
import perl.core.GdbState.State;
import perl.core.GdbState.StateListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import console.Console;
import console.ConsolePlugin;
import console.Output;
import console.Shell;
import debugger.jedit.Plugin;

public abstract class BaseShell extends Shell {

	protected static final String DEBUGGER_NOT_STARTED = Plugin.MESSAGE_PREFIX + "debugger_not_started";
	private Output output = null;
	
	public BaseShell(String arg0) {
		super(arg0);
		getConsole().setShell(this);	// The only way to create an Output
		output = getConsole().getOutput();
		setGdbStateListener();
	}

	private void setGdbStateListener() {
		GdbState.addStateListener(new StateListener() {
			public void stateChanged(State prev, State current) {
				if (current == GdbState.State.RUNNING) {
					getConsole().startAnimation();
				} else {
					getConsole().stopAnimation();
				}
			}
		});
	}
	
	protected Console getConsole() {
		View v = jEdit.getActiveView();
		Console c = ConsolePlugin.getConsole(v);
		if (c == null) {
			v.getDockableWindowManager().showDockableWindow("console");
			c = ConsolePlugin.getConsole(v);
		}
		return c;
	}
	protected CommandManager getCommandManager() {
		return Debugger.getInstance().getCommandManager();
	}
	protected Output getOutput() {
		return output;
	}
	@Override
	public void stop(Console arg0) {
		jEdit.getAction(Debugger.KILL_ACTION).invoke(jEdit.getActiveView());
		getOutput().commandDone();
	}
	protected void print(String s) {
		getOutput().print(getConsole().getPlainColor(), s);
	}
	protected void printError(String s) {
		getOutput().print(getConsole().getErrorColor(), s);
	}
	public void clear() {
		getConsole().setShell(this);
		getConsole().clear();
	}
}
