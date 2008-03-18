/*
 * jEditLogSystem.java
 * Copyright (c) 2002 Calvin Yu
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
package velocity;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.runtime.RuntimeServices;
import org.gjt.sp.util.Log;

/**
 * A velocity log system to integrate into jEdit's log facilities.
 */
public class jEditLogSystem implements LogSystem
{

   /**
    * Initialize the log system.
    */
   public void init(RuntimeServices services)
   {
   }

   /**
    * Log a message from velocity.
    */
   public void logVelocityMessage(int level, String message)
   {
      int jeditLevel = Log.DEBUG;
      switch (level) {
         case DEBUG_ID:
            jeditLevel = Log.DEBUG;
            break;
         case ERROR_ID:
            jeditLevel = Log.ERROR;
            break;
         case INFO_ID:
            jeditLevel = Log.MESSAGE;
            break;
         case WARN_ID:
            jeditLevel = Log.WARNING;
            break;
      }
      Log.log(jeditLevel, Velocity.class, message);
   }

}

