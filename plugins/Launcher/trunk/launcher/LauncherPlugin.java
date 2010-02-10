/*
 *  LauncherPlugin.java - Launcher plugin
 *  Copyright (C) 2010 François Rey
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package launcher;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JMenuItem;

import launcher.browser.BrowserLauncherType;
import launcher.exec.ExecutableFileLauncherType;
import launcher.extapp.ExternalApplicationLauncherType;
import launcher.keyword.KeywordSearchLauncherType;
import launcher.sysapp.SystemApplicationLauncherType;
import launcher.text.TextLauncherType;
import launcher.text.selected.SelectedTextLauncherType;

import org.gjt.sp.jedit.ActionContext;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.EditBus.EBHandler;
import org.gjt.sp.jedit.msg.PluginUpdate;


/**
 *  A plugin for launching external applications
 *
 *@author    François Rey
 */
public class LauncherPlugin extends EditPlugin
{
	public static final String NAME = "Launcher";
	public static final String PROP_PREFIX = LauncherPlugin.class.getPackage().getName();
	public static final String LABEL_SUFFIX = ".label";
	public static final String OPT_BASE_PREFIX = "options.";
	public static final String OPT_PREFIX = OPT_BASE_PREFIX + PROP_PREFIX;
	public static final String ERR_PREFIX = PROP_PREFIX + ".error";
	public static final String ERR_EXCEPTION = ERR_PREFIX + ".exception";
	public static final String LABEL =
		jEdit.getProperty(PROP_PREFIX + LABEL_SUFFIX);
	public static final String SUBMENU_LABEL =
		jEdit.getProperty(PROP_PREFIX + ".submenu" + LABEL_SUFFIX);
	
	private static Comparator<EditAction> editActionLabelComparator =
		new Comparator<EditAction>(){
			public int compare(EditAction a1, EditAction a2) {
				return a1.getLabel().compareTo(a2.getLabel());
			}	
		};
	
	@Override
	public void start() {
		super.start();
		loadServices();
		EditBus.addToBus(this);
	}
	
	public void loadServices() {
		// Register LauncherTypes for this plugin.
		// These could be defined in services.xml, however having them
		// here gives us the benefit of compiler checking and fewer places
		// where to modify text
		ServiceManager.registerService(LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
				ExternalApplicationLauncherType.SERVICE_NAME,
				ExternalApplicationLauncherType.class.getName() + ".INSTANCE;",
				getPluginJAR());
		ServiceManager.registerService(LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
				BrowserLauncherType.SERVICE_NAME,
				BrowserLauncherType.class.getName() + ".INSTANCE;",
				getPluginJAR());
		ServiceManager.registerService(LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
				KeywordSearchLauncherType.SERVICE_NAME,
				KeywordSearchLauncherType.class.getName() + ".INSTANCE;",
				getPluginJAR());
		ServiceManager.registerService(LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
				SystemApplicationLauncherType.SERVICE_NAME,
				SystemApplicationLauncherType.class.getName() + ".INSTANCE;",
				getPluginJAR());
		ServiceManager.registerService(LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
				ExecutableFileLauncherType.SERVICE_NAME,
				ExecutableFileLauncherType.class.getName() + ".INSTANCE;",
				getPluginJAR());
		ServiceManager.registerService(LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
				TextLauncherType.SERVICE_NAME,
				TextLauncherType.class.getName() + ".INSTANCE;",
				getPluginJAR());
		ServiceManager.registerService(LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
				SelectedTextLauncherType.SERVICE_NAME,
				SelectedTextLauncherType.class.getName() + ".INSTANCE;",
				getPluginJAR());

		registerLaunchers();
	}
	
	public boolean launch(String launcherName, Object resource) {
		ActionContext[] contexts = getActionContextFor(resource);
		for (ActionContext context: contexts) {
			EditAction action = context.getAction(launcherName);
			if (action != null) {
				context.invokeAction(null, action);
			}
			return true;
		}
		return false;
	}

	public void registerLaunchers() {
		for(String launcherTypeName : ServiceManager.getServiceNames(
								LauncherType.LAUNCHER_TYPE_SERVICE_NAME)) {
			LauncherType launcherType = (LauncherType)ServiceManager.getService(
					LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
					launcherTypeName);
			if (launcherType !=null)
				launcherType.registerLaunchers();
		}
	}

	public void reload() {
		for(String launcherTypeName : ServiceManager.getServiceNames(
								LauncherType.LAUNCHER_TYPE_SERVICE_NAME)) {
			LauncherType launcherType = (LauncherType)ServiceManager.getService(
					LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
					launcherTypeName);
			if (launcherType !=null)
				launcherType.reload();
		}
	}
	
	public ActionContext[] getActionContextFor(Object resource) {
		String[] launcherTypeNames = ServiceManager.getServiceNames(
				LauncherType.LAUNCHER_TYPE_SERVICE_NAME);
		ActionContext[] contexts = new ActionContext[launcherTypeNames.length];
		ActionContext launcherActionContextClone = null;
		int i = 0;
		for(String launcherTypeName : launcherTypeNames) {
			LauncherType launcherType = (LauncherType)ServiceManager.getService(
					LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
					launcherTypeName);
			ActionContext context = launcherType.getActionContext();
			if (context == LauncherActionContext.INSTANCE) {
				if (launcherActionContextClone == null) {
					launcherActionContextClone = launcherType.buildActionContextFor(resource);
				}
				context = launcherActionContextClone;
			} else
				context = launcherType.buildActionContextFor(resource);
			contexts[i++] = context;
		}
		return contexts;
	}
	
	public void getActionsFor(Object resource, 
			Set<EditAction> level1Actions,
			Set<EditAction> level2Actions,
			Map<EditAction,ActionContext> actionContexts) {
		String[] launcherTypeNames = ServiceManager.getServiceNames(
				LauncherType.LAUNCHER_TYPE_SERVICE_NAME);
		for(String launcherTypeName : launcherTypeNames) {
			LauncherType launcherType = (LauncherType)ServiceManager.getService(
					LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
					launcherTypeName);
			launcherType.getActionsFor(resource, level1Actions, level2Actions, actionContexts);
		}
	}
	
	public JMenuItem[] getMenuItemsFor(Object resource) {
		if (resource == null)
			return null;
		Set<EditAction> level1Actions =
			new TreeSet<EditAction>(editActionLabelComparator);
		Set<EditAction> level2Actions =
			new TreeSet<EditAction>(editActionLabelComparator);
		Map<EditAction,ActionContext> actionContexts =
				new HashMap<EditAction,ActionContext>();
		getActionsFor(resource, level1Actions, level2Actions, actionContexts);
		JMenuItem[] items = LauncherUtils.buildMenuItemsWith(
				NAME,
				SUBMENU_LABEL,
				level1Actions,
				level2Actions,
				actionContexts);
		return items;
	}

	@Override
	public void stop() {
		EditBus.removeFromBus(this);
		super.stop();
	}

	//{{{ handlePluginUpdate() method
	@EBHandler
	public void handlePluginUpdate(PluginUpdate msg)
	{
		reload();
	}
	//}}}

}

