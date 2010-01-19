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

import java.io.Writer;
import java.io.IOException;

import org.xml.sax.Attributes;

import projectviewer.vpt.VPTFilterData;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

/**
 *	Handler for filter configuration nodes.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 *	@since		PV 3.0.0
 */
public class FilterNodeHandler extends NodeHandler
{

	private final static String NODE_NAME	= "filter";
	private final static String NAME_ATTR	= "name";
	private final static String GLOB_ATTR	= "glob";


	/** Returns the name of the filter node in the config file. */
	public String getNodeName()
	{
		return NODE_NAME;
	}


	/** Filter nodes are handled differently; returns null. */
	public Class getNodeClass()
	{
		return null;
	}


	/** Filters are not children of any node. */
	public boolean isChild()
	{
		return false;
	}


	/** Filters don't have children. */
	public boolean hasChildren()
	{
		return false;
	}


	/** Instantiates a filter based on the given attributes. */
	public VPTNode createNode(Attributes attrs,
							  VPTProject project)
	{
		String name = attrs.getValue(NAME_ATTR);
		String glob = attrs.getValue(GLOB_ATTR);

		if (name != null && glob != null) {
			VPTFilterData data = new VPTFilterData(name, glob);
			project.addFilter(data);
		}
		return null;
	}


	/** Saving filters is handled differently. */
	public void saveNode(VPTNode node, Writer out) throws IOException {

	}

	/** Saves the filter to the output. */
	public void saveNode(VPTFilterData filter,
						 Writer out)
		throws IOException
	{
		startElement(out);
		writeAttr(NAME_ATTR, filter.getName(), out);
		writeAttr(GLOB_ATTR, filter.getGlob(), out);
		out.write(" />\n");
	}

}
