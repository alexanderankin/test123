package activator;

import java.io.File;
import java.util.*;
import static activator.PluginManager.*;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.PluginJAR;

// TODO: comments, please.
public class Plugin {
    
    private PluginJAR jar = null;
    private File file = null;
    int hashValue = -1;
    private Set<String> dependsOnMe = new HashSet<String>();
    private Set<String> optionallyDependsOnMe = new HashSet<String>();
    private Set<String> iDependOn = new HashSet<String>();
    
    
    public Plugin( PluginJAR jar ) {
        this.jar = jar;
        file = jar.getFile();
        String[] depends = jar.getDependentPlugins();
        for (String dep : depends) {
            dependsOnMe.add(dep);   
        }
        String[] optionals = jar.getOptionallyDependentPlugins();
        for (String dep : optionals) {
            optionallyDependsOnMe.add(dep);   
        }
        Set<String> required = jar.getRequiredJars();
        PluginList pl = PluginList.getInstance();
        for (String req : required) {
            iDependOn.add(req);
            Plugin plugin = pl.get(req);
            if (plugin != null) {
                plugin.addDependent(file.getAbsolutePath());
            }
        }
    }
    
    public Plugin( File file ) {
        this.file = file;
    }

    public PluginJAR getJAR() {
        return jar;
    }

    public File getFile() {
        return file;
    }
    
    public String getPath() {
        return file.getAbsolutePath();   
    }
    
    public int getStatus() {
        return PluginManager.getStatus(jar);       
    }
    
    /**
     * @return Is this plugin loaded?    
     */
    public boolean isLoaded() {
        if (jar == null) {
            return false;   
        }
        if (jar.getPlugin() == null) {
            return false;   
        }
        if ( jar.getPlugin() instanceof EditPlugin.Deferred ) {
            return true;   
        }
        if ( jar.getPlugin() instanceof EditPlugin.Broken ) {
            return false;   
        }
        if (isActivated()) {
            return true;   
        }
        return false;
    }
    
    /**
     * @return Is this plugin activated?    
     */
    public boolean isActivated() {
        if (jar == null) {
            return false;   
        }
        if ( jar.getPlugin() == null) {
            return false;   
        }
        if ( jar.getPlugin() instanceof EditPlugin.Deferred ) {
            return false;   
        }
        if ( jar.getPlugin() instanceof EditPlugin.Broken ) {
            return false;   
        }
        return true;
    }

    public boolean isLibrary() {
        if ( jar == null ) {
            return false;
        }
        return jar.getPlugin() == null;
    }

    /**
     * @return true if the plugin should be activated on jEdit startup.
     */
    public boolean isLoadOnStartup() {
        if ( jar == null ) {
            return false;
        }
        if ( jar.getPlugin() == null ) {
            return false;
        }
        String activate = jEdit.getProperty( "plugin." + jar.getPlugin().getClassName() + ".activate", "defer" );
        return "startup".equals( activate );
    }
    
    /**
     * Sets the plugin "activate" property to either "startup" or "defer".
     * @param b If true, set the "activate" property to "startup", otherwise, set it to "defer".
     */
    public void setLoadOnStartup( boolean b ) {
        if ( jar == null ) {
            return;
        }
        if ( jar.getPlugin() == null ) {
            return;
        }
        jEdit.setProperty( "plugin." + jar.getPlugin().getClassName() + ".activate", b ? "startup" : "defer" );
    }

    public boolean canLoadOnStartup() {
        if ( jar == null ) {
            return false;
        }
        if ( jar.getPlugin() == null ) {
            return false;
        }
        return true;
    }

    public void addDependent(String dep) {
        if (dep != null) {
            dependsOnMe.add(dep);    
        }
    }
    
    public void setDependentPlugins(Set<String> dependents) {
        dependsOnMe = dependents;   
    }
    
    public void addOptionalDependent(String dep) {
        if (dep != null) {
            optionallyDependsOnMe.add(dep);    
        }
    }
    
    public void setOptionallyDependentPlugins(Set<String> optionals) {
        optionallyDependsOnMe = optionals;
    }
    
    public Set<String> getAllDependentPlugins() {
        HashSet<String> all = new HashSet<String>();
        all.addAll(dependsOnMe);
        all.addAll(optionallyDependsOnMe);
        return all;   
    }
    
    /**
     * This is used to keep track of previously known dependencies that may not
     * also be known at plugin reload time. This copies the dependencies of this
     * plugin to the given plugin, but does nothing with the PluginJAR.
     * @param plugin The plugin to copy dependencies to.
     */
    public Plugin copyTo(Plugin plugin) {
        plugin.setDependentPlugins(dependsOnMe);
        plugin.setOptionallyDependentPlugins(optionallyDependsOnMe);
        return plugin;
    }
    
    public String toString() {
        if ( jar == null ) {
            return file.getName();
        }
        if ( jar.getPlugin() == null ) {
            return jar.getFile().getName();
        }
        if ( jar.getPlugin() instanceof EditPlugin.Deferred ) {
            return jar.getFile().getName();
        } else {
            return jEdit.getProperty( "plugin." + jar.getPlugin().getClassName() + ".name", "No name property" );
        }
    }

    @Override 
    public int hashCode() {
        return file.getAbsolutePath().hashCode();
    }

    public boolean equals( Object obj ) {
        // check for reference equality
        if ( this == obj ) {
            return true;
        }

        // type check
        if ( ! ( obj instanceof Plugin ) ) {
            return false;
        }

        // cast to correct type
        Plugin other = ( Plugin ) obj;

        // check fields
        return getFile().equals( other.getFile() );
    }

}// }}}
