/*
 *  $Id$
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.event;

import java.util.EventListener;

/**
 * A listener of project events.
 */
public interface ProjectListener extends EventListener {

   /**
    * Notification that a project file has been opened.
    *
    * @param evt  Description of Parameter
    */
   public void fileOpened( ProjectEvent evt );

   /**
    * Notification that a project file has been closed.
    *
    * @param evt  Description of Parameter
    */
   public void fileClosed( ProjectEvent evt );

   /**
    * Notification that a project file has been removed.
    *
    * @param evt  Description of Parameter
    */
   public void fileRemoved( ProjectEvent evt );

   /**
    * Notification that a project directory has been removed.
    *
    * @param evt  Description of Parameter
    */
   public void directoryRemoved( ProjectEvent evt );

   /**
    * Notification that a project directory has been added.
    *
    * @param evt  Description of Parameter
    */
   public void directoryAdded( ProjectEvent evt );

   /**
    * Notification that a project file has been added.
    *
    * @param evt  Description of Parameter
    */
   public void fileAdded( ProjectEvent evt );

   /**
    * Notification that a build/make file has been selected for the Project.
    *
    * @param evt  Description of Parameter
    */
   public void buildFileSelected( ProjectEvent evt );

}

