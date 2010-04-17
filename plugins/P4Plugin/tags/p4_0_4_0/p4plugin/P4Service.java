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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.io.VFSFile;

import projectviewer.config.VersionControlService;

import projectviewer.importer.ImporterFileFilter;

import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import p4plugin.config.P4Config;
import p4plugin.config.P4OptionPane;


/**
 * Implement's a ProjectViewer version control service. This service
 * currently only handle configuration of P4 features, and doesn't
 * provide any "status icons".
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      P4P 0.3.0
 */
public class P4Service implements VersionControlService
{

	public int getNodeState(VPTNode f)
	{
		return 0;
	}

	public Icon getIcon(int state)
	{
		return null;
	}

	public Class getPlugin()
	{
		return P4Plugin.class;
	}

	public ImporterFileFilter getFilter()
	{
	    return new P4FileFilter();
	}

	public void dissociate(VPTProject proj)
	{
        P4Config config = new P4Config();
        config.clean(proj.getProperties());
	}

    public OptionPane getOptionPane(VPTProject proj)
    {
        return new P4OptionPane(proj);
    }

    public OptionGroup getOptionGroup(VPTProject proj)
    {
        return null;
    }

}

