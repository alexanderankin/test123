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

package buildtools;

/**
A static implementation of a Logger.  See Logger for JavaDoc

@see Logger
@author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
@version $Id$
*/

public class StaticLogger {
    
    private static Logger logger = new Logger("STATIC");
    
    public static void log(String log) {
        logger.log(log, 0);
    }

    public static void log(String log, int level) {
        logger.log(log, level);        
    }

    public static void setLevel(int level) {
        logger.setLevel(level);
    }
    
    public static int getLevel() {
        return logger.getLevel();
    }

}
