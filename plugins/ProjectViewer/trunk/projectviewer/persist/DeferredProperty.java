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
package projectviewer.persist;

//{{{ Imports
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.gjt.sp.util.Log;

import projectviewer.PVActions;
//}}}

/**
 *	Defers the loading of a property until it's requested. This avoids
 *	ClassCastExceptions and ClassNotFoundErrors when loading PV, since
 *	the plugins that set "object" properties might not be available.
 *
 *	<p>Since in a perfect world only the plugin that set the property
 *	will try to load it, the implementing class of the serialized object
 *	will most probably be available when the property is requested from
 *	the project.</p>
 *
 *	<p>This requires special treatment in VPTProject.</p>
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 2.1.2
 */
public class DeferredProperty {

	private String data;
	private String name;

	public DeferredProperty(String data, String name) {
		this.data = data;
		this.name = name;
	}

	/**
	 *	Returns the original string with the serialized object. This will
	 *	be null if the object was instantiated.
	 */
	protected String getData() {
		return this.data;
	}

	/**
	 *	Tries to load the object represented by the serialized data;
	 *	returns the object, or "this" is loading failed. An error message
	 *	is logged to the activity log in the latter case.
	 */
	public Object getValue() {
		try {
			byte[] bytes = PVActions.decodeBase64(data);
			ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream(bytes) );
			return ois.readObject();
		} catch (Exception e) {
			Log.log(Log.ERROR, this, "Error loading property of name " + name
									 + " : " + e.getClass().getName()
									 + " : " + e.getMessage());
		}
		return this;
	}

}

