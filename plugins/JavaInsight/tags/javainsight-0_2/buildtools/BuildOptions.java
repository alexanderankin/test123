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

import java.util.Properties;
import java.util.Vector;

/**
 *  build options are essentially a set of properties, but with 
 *  a nice and accessible interface.  Most Builders will not
 *  need their own subclass of BuildOptions but you are entitled
 *  to implement your own.
 */
public class BuildOptions {
    String builderName;
    int profile;
    
    Vector libraries = new Vector();
    
    Properties props = new Properties();
    
    /**
     *  only Builders should create BuildOptions so
     *  they should initialize the object with their builderName
     */
    public BuildOptions(String _builderName) {
        builderName = _builderName;
    }
    
    public String getBuilderName() {
        return builderName;
    }
    
    /**
     *  the builder profile is a top level interface used to specify which 
     *  high level configuration you want for the builder for this project.
     *
     *  for the JavaBuilder this is used to store what jdk profile to use.
     */
    public void setBuilderProfile(int _profile) {
        profile = _profile;
    }
    
    public int getBuilderProfile() {
        return profile;
    }
    
    /**
     *  support for multiple library modules.  Each one is a string name
     *  alias of some resolved object in the builder
     */
    public void insertLibrary(String lib, int pos) {
        libraries.insertElementAt(lib, pos);
    }
    
    public void removeLibrary(int pos) {
        libraries.removeElementAt(pos);
    }
    
    public int getNumLibraries() {
        return libraries.size();
    }
    
    public int getLibrary(int pos) {
        return ((Integer)libraries.elementAt(pos)).intValue();
    }
    
    /**
     *  general build properties, debug flags or whatever the
     *  builder wants
     */
    public String getProperty(String propName) {
        return props.getProperty(propName);
    }
    

    /*
    NOT JDK 1.1 complaint!!! setProperty not available
    public void setProperty(String propName, String value) {
        props.setProperty(propName, value);
    }
    
    */
    /** 
     *  persistence mechanism.  Since project files are going to be properties
     * files, lets just
     */
    public void save(Properties saveDest) {
        //  TO DO:
    }
    
      //  TO DO:  rewrite this to slurp all options
    public void load(Properties loadProps) {
        profile = Integer.parseInt(loadProps.getProperty(("options." + builderName + ".profile")));
        int i = 0;
        while(true) {
            String lib = loadProps.getProperty("options." + builderName + ".lib." + i);
            if(lib == null)
                break;
            
            libraries.addElement(new Integer(lib));
            
            i++;
        }
        
        
    }
    
    public String toString() {
        return "BuildOptions for: " + builderName + " using profile " + profile;
    }
}
