/*
 * ArchivePlugin.java
 * Copyright (c) 2000, 2001 Andre Kaplan
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


import java.util.Vector;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;

import org.gjt.sp.jedit.io.VFSManager;

import org.gjt.sp.util.Log;


public class ArchivePlugin extends EditPlugin
{
    public void start() {
        VFSManager.registerVFS(ArchiveVFS.PROTOCOL, new ArchiveVFS());
        VFSManager.registerVFS(TarVFS.PROTOCOL,     new TarVFS());
        VFSManager.registerVFS(ZipVFS.PROTOCOL,     new ZipVFS());
    }


    public void stop() {}


    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu("archive-menu"));
    }
}

