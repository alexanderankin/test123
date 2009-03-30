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
package pvdebug;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.io.VFSFile;

import projectviewer.config.OptionsService;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTProject;


/**
 *	A dummy version control service.
 *
 *	@author		Marcelo Vanzin
 *	@version	$Id$
 */
public class DummyOptions implements OptionsService
{

    public OptionPane getOptionPane(VPTProject proj)
    {
        return new DummyOptionPane("dummy");
    }

    public OptionGroup getOptionGroup(VPTProject proj)
    {
        return null;
    }

}

