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


import java.util.Hashtable;

/**
Controls multiple FileChangeMonitors and returns shared instances or a new 
instance and pools it if necessary.


*/
public class FileChangeController {
    
    private Hashtable monitors = new Hashtable();

    
    public FileChangeMonitor getMonitor(String directory, String[] extensions) {
        
        StringBuffer id = new StringBuffer(directory);
        for (int i = 0; i < extensions.length; ++i) {
            id.append( extensions[i] );
        }
        
        FileChangeMonitor monitor = (FileChangeMonitor)this.monitors.get( id.toString() );
        
        if (monitor == null) {
            monitor = new FileChangeMonitor( directory, extensions);
            this.monitors.put( id.toString(), monitor);
        }
        
        return monitor;
        
    }
    
}
