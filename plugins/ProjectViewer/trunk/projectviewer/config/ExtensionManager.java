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
package projectviewer.config;

import java.lang.ref.WeakReference;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.ServiceManager;

/**
 *	A central location for managing ProjectViewer extensions (deployed
 *	as jEdit services). Allows for instantiation, cleanup, configuration
 *	for enabling/disabling extensions, etc.
 *
 *	@author		Marcelo Vanzin
 *	@since		PV 3.0.0
 *	@version	$Id$
 */
public class ExtensionManager
{

	//{{{ Singleton
	private static ExtensionManager instance = new ExtensionManager();

	public static ExtensionManager getInstance() {
		return instance;
	}
	//}}}

	private List<WeakReference<ManagedService>> services;

	private ExtensionManager()
	{
		services = new LinkedList<WeakReference<ManagedService>>();
	}

	/** Registers the given managed service with the manager. */
	public void register(ManagedService service)
	{
		services.add(new WeakReference<ManagedService>(service));
		service.updateExtensions(loadExtensions(service.getServiceClass()));
	}

	/** Unregisters the given managed service. */
	public void unregister(ManagedService service)
	{
		for (Iterator<WeakReference<ManagedService>> it = services.iterator();
			 it.hasNext(); ) {
			WeakReference<ManagedService> ref = it.next();
			if (ref.get() == service || ref.get() == null) {
				it.remove();
				break;
			}
		}
	}

	/**
	 * 	Called by the PV plugin instance to refresh the extensions of
	 * 	all the currently registered managed services.
	 */
	public void reloadExtensions()
	{
		for (Iterator<WeakReference<ManagedService>> it = services.iterator();
			 it.hasNext(); ) {
			WeakReference<ManagedService> ref = it.next();
			if (ref.get() != null) {
				ref.get().updateExtensions(loadExtensions(ref.get().getServiceClass()));
			} else {
				it.remove();
			}
		}
	}

	private List<Object> loadExtensions(Class clazz)
	{
		String[] extensions = ServiceManager.getServiceNames(clazz.getName());
		List<Object> lst = null;
		for (String ext : extensions) {
			// XXX: check if extension is disabled
			Object svc = ServiceManager.getService(clazz.getName(), ext);
			if (svc != null) {
				if (lst == null) {
					lst = new LinkedList<Object>();
				}
				lst.add(svc);
			}
		}
		return lst;
	}

	/**
	 *	Classes that use extensions should implement this interface
	 *  and register themselves by calling
	 *  {@link #register(ManagedService)}, to receive events about
	 *	changes in the service configuration.
	 */
	public static interface ManagedService
	{

		/** Returns the base class of the jEdit service. */
		public Class getServiceClass();

		/**
 		 *	Notifies the instance that a new list of extensions has
		 *	been created in response to some event (such as a plugin
		 *	being loaded or unloaded). The instance should free the
		 *	old extension list and use the new one.
		 */
		public void updateExtensions(List<Object> l);

	}

}

