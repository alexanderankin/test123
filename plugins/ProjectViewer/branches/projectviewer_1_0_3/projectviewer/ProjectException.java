/*
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
package projectviewer;

import java.io.*;


/**
 * An exception in a project.
 */
public class ProjectException extends Exception {

   private Throwable rootCause;

   /**
    * Create a new <code>ProjectException</code>.
    */
   public ProjectException(String msg) {
      super(msg);
   }

   /**
    * Create a new <code>ProjectException</code>.
    */
   public ProjectException(String msg, Throwable t) {
      super(msg);
      rootCause = t;
   }

   /**
    * Print the stack trace.
    */
   public void printStackTrace() {
      printStackTrace(System.err);
   }

   /**
    * Print the stack trace.
    */
   public void printStackTrace(PrintStream out) {
      printStackTrace(new PrintWriter(out, true));
   }

   /**
    * Print the stack trace.
    */
   public void printStackTrace(PrintWriter out) {
      super.printStackTrace(out);
      if (rootCause != null) {
         out.println("Root Cause:");
         rootCause.printStackTrace(out);
      }
   }

}
