/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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
package textfilter;

//{{{ Imports
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.util.StringTokenizer;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.msg.DynamicMenuChanged;

import org.gjt.sp.util.Log;
//}}}

/**
 *  Manages the actions registered in the plugin, and takes care of saving and
 *	loading them. Also, talks with jEdit to make it aware of these actions.
 *
 *	@author		Marcelo Vanzin
 *  @version	$Id$
 */
public final class ActionManager {

	//{{{ Static members
	private static final String ACTION_SET_NAME = "textfilter.action_set_name";
	private static final ActionManager instance = new ActionManager();

	//{{{ +_getInstance()_ : ActionManager
	public static ActionManager getInstance() {
		return instance;
	} //}}}

	//}}}

	//{{{ Private members
	private ActionSet actionSet;
	//}}}

	//{{{ -ActionManager() : <init>
	private ActionManager() {
		actionSet = new ActionSet(jEdit.getProperty(ACTION_SET_NAME));
		// load actions from config file
		try {
			InputStream ins = new FileInputStream(jEdit.getSettingsDirectory() + "/textfilter.conf");
			BufferedReader in = new BufferedReader(new InputStreamReader(ins, "UTF-8"));

			String line;
			while ((line = in.readLine()) != null) {
				StringTokenizer vals = new StringTokenizer(line, ",");

				String name = vals.nextToken();
				String command = vals.nextToken();
				int dataSourceType = Integer.parseInt(vals.nextToken());
				int textSource = Integer.parseInt(vals.nextToken());
				int destination = Integer.parseInt(vals.nextToken());

				FilterAction fa = new FilterAction(name, command, dataSourceType,
													textSource, destination);
				actionSet.addAction(fa);
			}

			in.close();
		} catch (FileNotFoundException fnfe) {
			// ignore
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}

		// finish initialization
		if (actionSet.getActionCount() > 0) {
			jEdit.addActionSet(actionSet);
			actionSet.initKeyBindings();
		}
	} //}}}

	//{{{ +hasAction(String) : boolean
	public boolean hasAction(String name) {
		return actionSet.contains(name);
	} //}}}

	//{{{ +addAction(FilterAction) : void
	public void addAction(FilterAction action) {
		actionSet.addAction(action);

		if (actionSet.getActionCount() == 1) {
			jEdit.addActionSet(actionSet);
		}

		actionSet.initKeyBindings();
		saveConfig();
		fireMenuChange();
	} //}}}

	//{{{ +removeAction(FilterAction) : void
	public void removeAction(FilterAction action) {
		actionSet.removeAction(action.getName());

		if (actionSet.getActionCount() == 0)
			jEdit.removeActionSet(actionSet);

		saveConfig();
		fireMenuChange();
	} //}}}

	//{{{ +getActionSet() : ActionSet
	public ActionSet getActionSet() {
		return actionSet;
	} //}}}

	//{{{ -saveConfig() : void
	private void saveConfig() {
		if (actionSet.getActionCount() > 0)
			jEdit.setProperty("textfilter.has_actions", "true");
		else
			jEdit.unsetProperty("textfilter.has_actions");

		try {
			OutputStream outs = new FileOutputStream(jEdit.getSettingsDirectory() + "/textfilter.conf");
			OutputStreamWriter out = new OutputStreamWriter(outs, "UTF-8");

			EditAction[] actions = actionSet.getActions();
			for (int i = 0; i < actions.length; i++) {
				FilterAction fa = (FilterAction) actions[i];
				out.write(fa.getLabel());
				out.write(",");
				out.write(fa.getCommand());
				out.write(",");
				out.write(String.valueOf(fa.getDataSourceType()));
				out.write(",");
				out.write(String.valueOf(fa.getTextSource()));
				out.write(",");
				out.write(String.valueOf(fa.getDestination()));
				if (i < actions.length - 1)
					out.write("\n");
			}

			out.close();
		} catch (IOException ioe) {
			Log.log(Log.ERROR, this, ioe);
		}
	} //}}}

	//{{{ -fireMenuChange() : void
	private void fireMenuChange() {
		DynamicMenuChanged msg = new DynamicMenuChanged("plugin.textfilter.TextFilterPlugin.menu");
		EditBus.send(msg);
	} //}}}

	//{{{ +unload() : void
	/** To be called when jEdit unloads the plugin. */
	public void unload() {
		saveConfig();
		jEdit.removeActionSet(actionSet);
	} //}}}

}

