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

import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.OptionPane;

import java.util.Enumeration;
import java.util.Vector;


/**
 *   builders must be plugins.  This is because the ProjectManager uses querys edit
 *   for all
 *
 *   subclass from this to create a builder
 */
abstract public class Builder extends EBPlugin {
    final public static Object builderListTag = new Object();
    
    /**
     *  @return the type that this Builder builds.  Should only be one type (for now!!)
     *    example "java" or "pl"  this should be the file extention
     */ 
    abstract public String getBuildType();
    
    /**
     *  for each builder in the universe this must give a unique name.  Override
     *  it if you want:  especially useful if you think you may change the classname!
     */
    abstract public String getBuilderName();
    
    /**
     *  this returns a default build options for a given builder.
     *  the parameters are not guarenteed to be setup correctly.  You
     *  can think of this as a factory for those of you who want to implement
     *  their own entire subclass of BuildOptions.  This is called every time
     *  the BuildOptions
     */
    public BuildOptions getDefaultBuildOptions() {
        return new BuildOptions( getBuildType() );
    }

    /**
     *  given a list of files, compile them to targets.
     *  note that build all is equivalent to removeTargets (probably 
     *  better to call removeAllTargets) and build
     *
     *  if buildListener is null then it does not give feedback
     *  destDir may be a jar file if it is desired!
     */
    abstract public Vector build(Enumeration files, String targetDir, BuildOptions bo, BuildProgressListener buildListener);

    /**
     *  This will be called by ProjectManager when a build all is required.  The default behavior is
     *  to just remove all targets in the location, though who knows?
     */
    public void removeTargets(Enumeration files, String targetDir, BuildOptions bo) {
        removeAllTargets(targetDir, bo);
    }
    
    /**
     *
     */
    abstract public void removeAllTargets(String targetDir, BuildOptions bo);
    
    /**
     *  I assume that we will not use JPanels for options pane
     */
    abstract public OptionPane createBuildOptionsPane(BuildOptions options);

    /**
     *  Return true here if the builder generated an exception while compiling files.
     *
     * @author <A HREF="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
     * @version $Id$
     */
    public boolean hasGeneratedErrors() {
        return false;
    }
    

}

