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

public class Logger {

    private int level = 0;

    private String PRODUCT = "LOGGER";

    public Logger(String product) {
        this.PRODUCT = product;
    }


    /**
    Log a message with the lowest level.

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void log(String log) {
        log(log, 0);
    }

    /**
    Log a message with the specified level.
    
    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void log(String log, int level) {

        if ( level  >= this.getLevel() ){
            System.err.println( PRODUCT + " DEBUG " + "(" + level + ") " +  log );
        }
        
    }

    /**
    Set the current logging level

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public void setLevel(int level) {
        this.level = level;
    }
    
    /**
    Get the current logging level

    @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
    @version $Id$
    */
    public int getLevel() {
        return this.level;
    }
    
    
}
