/*
 * :tabSize=4:indentSize=4:noTabs=true:
 * :folding=explicit:collapseFolds=1:
 *
 * (c) 2007 Marcelo Vanzin
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
package poptions;

import java.util.LinkedList;
import java.util.List;

import org.gjt.sp.jedit.jEdit;
import common.gui.EasyOptionPane;
import projectviewer.config.ProjectOptions;

/**
 *  Base class for the child option panes (implements some common code).
 *
 *  @author     Marcelo Vanzin
 *  @version    $Id$
 *  @since      POP 0.1.0
 */
public abstract class ChildOptionPane extends EasyOptionPane
{

    public ChildOptionPane(String name)
    {
        super(name);
        setPropertyStore(ProjectOptions.getProject().getProperties());
    }

    public void _save()
    {
        // detect whether custom options are enabled
        boolean enabled = "true".equalsIgnoreCase(getProperty("poptions.enabled", "false"));
        if (enabled) {
            super._save();
        } else {
            cleanup();
        }
    }

}

