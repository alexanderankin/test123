/*
 *  Classpath.java - File to figure out which classpath should be used by the
 *  JImporter plugin.  
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.classpath;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.gjt.sp.jedit.jEdit;

/** 
 * This class returns the classpath that should be used by JImporter to locate a
 * fully qualified class name.  It gets it's information based on the JImporter
 * options of the Global options.
 *
 * @author Matthew Flower
 */
public class Classpath {
    //--------------------------------------------------------------------------
    // Static methods and variables
    //--------------------------------------------------------------------------
    /**
     * This variable should contain all of the possible classpath instances.  It
     * is used to produce lists of classpaths (such as for the option dialog).
     */
    private static ArrayList classpathList = new ArrayList();
    private static EventListenerList classpathChangeListeners = new EventListenerList();
    /**
     * This is the name of the property that is used to store which classpath we
     * are going to use.
     */
    private static String JEDIT_CLASSPATH_TYPE_PROPERTY = "jimporter.classpath.source";
    /**
     * The option that will contain whether we are going to append rt.jar
     * automatically to the classpath.
     */
    private static String APPEND_RT_JAR_PROPERTY = "jimporter.classpath.appendrtjar";
    private static boolean APPEND_RT_JAR_DEFAULT = true;
    public static String APPEND_RT_JAR_LABEL_PROPERTY = "options.jimporter.classpath.appendrtjar.label";
    
    /**
     * Should the classpath object automatically append rt.jar to the classpath.
     *
     * @return a <code>boolean</code> value that indicates that rt.jar should be
     * added to the classpath.
     */
    public static boolean isAppendRTJar() {
        boolean toReturn = APPEND_RT_JAR_DEFAULT;
        String isAppendRTJarProperty = jEdit.getProperty(APPEND_RT_JAR_PROPERTY);
        
        if (isAppendRTJarProperty != null) {
            toReturn = Boolean.valueOf(isAppendRTJarProperty).booleanValue();
        }
        
        return toReturn;
    }
    
    /**
     * Indicate whether the classpath objects should automatically append rt.jar
     * to the classpath.
     *
     * @param state a <code>boolean</code> value indicating whether rt.jar should
     * automatically be appended.
     */
    public static void setAppendRTJar(boolean state) {
        jEdit.setProperty(APPEND_RT_JAR_PROPERTY, new Boolean(state).toString());
    }
    
    /**
     * This classpath object will fetch the classpath from a path that the user
     * entered in the options dialog, specifically for JImporter.
     */
    public static Classpath USE_SPECIFIED_CLASSPATH = new Classpath("usespecified", "jimporter.classpath.usespecified", "options.jimporter.classpath.usethis.label");
    /**
     * This classpath object will fetch classpath information from Speedjava.
     */
    public static Classpath USE_SPEEDJAVA_CLASSPATH = new Classpath("usespeedjava", "speedjava.classpath", "options.jimporter.classpath.usespeedjava.label");
    /**
     * This classpath object will fetch classpath information from the jcompiler options.
     */
    public static Classpath USE_JCOMPILER_CLASSPATH = new Classpath("usejcompiler", "jcompiler.classpath", "options.jimporter.classpath.usejcompiler.label");
    /**
     * This classpath object will fetch classpath information from the system 
     * classpath currently being used by the JEdit JVM.
     */
    public static Classpath USE_SYSTEM_CLASSPATH = new SystemClasspath();
    /**
     * This is the classpath that will be used if one has not been explicitly set
     * yet.
     */
    public static Classpath DEFAULT = USE_SYSTEM_CLASSPATH;
    
    /**
     * Specialized classpath object that looks up the classpath from the currently
     * running VM.
     */
    static class SystemClasspath extends Classpath {
        /**
         * Default constructor
         */
        SystemClasspath() {
            super("usesystem", "", "options.jimporter.classpath.usesystem.label");
        }
        
        /** 
         * Get the correct classpath that should be used to find fully qualified class
         * names.
         *
         * @return A <CODE>String</CODE> value that contains the classpath that JImporter should
         * use to find files.
         */    
        public String getClasspath() {
            return appendRuntimeJarLocation(System.getProperty("java.class.path", ".")); 
        }
    }
    
    /**
     * Return a list of all the current classpath instances.  Classpaths are 
     * designed to be singletons, so this should be all of the instances in the
     * system.  (Unless someone mucks around a bit.)
     *
     * @return a <code>List</code> object containing all available classpath 
     * objects.
     */
    public static List getClasspaths() {
        return classpathList;
    }
    
    /**
     * Given a unique identifier, find the classpath that is associated with it.
     */
    public static Classpath getForID(String uniqueIdentifier) {
        Classpath classpathToReturn = DEFAULT;
        Iterator it = getClasspaths().iterator();
        
        while (it.hasNext()) {
            Classpath classpathToTest = (Classpath)it.next();
            
            if (classpathToTest.getUniqueIdentifier().equals(uniqueIdentifier)) {
                classpathToReturn = classpathToTest;
            }
        }
        
        return classpathToReturn;
    }
    
    /**
     * Get the classpath that is currently marked as "current" by jEdit.
     *
     * @return The <code>Classpath</code> variable that should be currently 
     * used by the system.
     */
    public static Classpath getCurrent() {
        return getForID(jEdit.getProperty(JEDIT_CLASSPATH_TYPE_PROPERTY));
    }
    
    /**
     * Set the current classpath.
     *
     * @param typeToSet A new <code>Classpath</code>.
     */
    public static void setCurrent(Classpath typeToSet) {
        //Grab this now, before the classpath object changes it to the new value.
        Classpath oldClasspath = getCurrent();
        
        //Store the new value
        typeToSet.store();
        
        //Notify the listeners
        fireClasspathChange(oldClasspath, typeToSet);
    }
    
    /**
     * Allow a class to be notified when the current classpath type changes.
     */
    public static void addClasspathChangeListener(ClasspathChangeListener l) {
        classpathChangeListeners.add(ClasspathChangeListener.class, l);
    }
    
    /**
     * Remove classpath from the list of classpaths to be notified when the 
     * current classpath changes.
     */
    public static void removeClasspathChangeListener(ClasspathChangeListener l) {
        classpathChangeListeners.remove(ClasspathChangeListener.class, l);
    }
    
    /** 
     * Get the list of all listeners that want to know when the classpath changes.
     */
    public static ClasspathChangeListener[] getClasspathChangeListeners() {
        return (ClasspathChangeListener[])classpathChangeListeners.getListeners(
            ClasspathChangeListener.class);
    }
    
    /**
     * Signal all of the <code>ClasspathChangeListener</code>s that the classpath
     * has changed.
     */
    protected static void fireClasspathChange(Classpath oldClasspath, Classpath newClasspath) {
       ClasspathChangeListener[] listeners = (ClasspathChangeListener[])
           classpathChangeListeners.getListeners(ClasspathChangeListener.class);
           
       for (int i = 0; i < listeners.length; i++) {
           listeners[i].classpathChanged(oldClasspath, newClasspath);
       }
    }
    
    /**
     * Append the location of rt.jar to the classpath if the user has selected
     * this option.  This option is available because java no longer requires
     * rt.jar to be in the classpath.  Thanks to Jigar Patel for pointing this
     * out.
     *
     * @param classpath a <code>String</code> value containing a base classpath.
     * @return a <code>String</code> value containing a classpath.  If the user
     * has set the APPEND_RT_JAR option, then rt.jar will be appended to the
     * path as well.
     */
    private static String appendRuntimeJarLocation(String classpath) {
        String classpathToReturn = classpath;
        
        //If we should be appending rt.jar and it isn't already in the classpath,
        //add it.
        if ((isAppendRTJar()) && (classpath.indexOf(File.separator+"rt.jar") == -1)) {
            String rtjarlocation = System.getProperty("java.home") + File.separator 
              + "lib" + File.separator + "rt.jar";
              
            File testRTJarLocation = new File(rtjarlocation);
              
            if (!testRTJarLocation.exists()) {
                rtjarlocation = System.getProperty("java.home") + File.separator 
                    + "jre" + File.separator + "lib" + File.separator + "rt.jar";
                    
                testRTJarLocation = new File(rtjarlocation);
                
                if (!testRTJarLocation.exists()) {
                    System.out.println("JImporter -- unable to find rt.jar!!!");
                }
            }
                          
            classpathToReturn = classpathToReturn + File.pathSeparator + rtjarlocation;
        }
        
        System.out.println("Returning classpath = " + classpathToReturn);
        return classpathToReturn;
    }
        
    
    //--------------------------------------------------------------------------
    // Implementation methods and variables
    //--------------------------------------------------------------------------
    
    /** 
     * The String that JImporter will store as "jimporter.classpath.source" to
     * identify the source of the classpath.
     */
    private String uniqueIdentifier;
    /**
     * The property where the classpath object can find a classpath.  This is
     * only necessary if you are using the default implementation of classpath
     * which fetches a classpath from a JEdit property.
     */
    private String classpathSourcePropertyID;
    
    /**
     * The property that will used in jEdit to lookup a human-readable name.
     */
    private String labelProperty;
    
    /**
     * This private constructor disallows others to instantiate Classpath objects.
     * (Well, mostly, at least.)
     *
     * @param uniqueIdentifier a <code>String</code> value that unique identifies this
     * classpath.
     * @param classpathSourcePropertyID In the default implementation, this is
     * used to look up a classpath in the form of 
     * @param labelProperty the property used to lookup a human-readable form of  
     * the classpath source.
     * <code>jEdit.getProperty(classpathSourcePropertyID);</code>
     */
    private Classpath(String uniqueIdentifier, String classpathSourcePropertyID, String labelProperty) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.classpathSourcePropertyID = classpathSourcePropertyID;
        this.labelProperty = labelProperty;
        
        classpathList.add(this);
    }
    
    /**
     * Default constructor.
     */
    private Classpath() {
        classpathList.add(this);
    }
    
    public String getUniqueIdentifier() {
       return this.uniqueIdentifier;
    }
    
    /** 
     * Get the correct classpath that should be used to find fully qualified class
     * names.
     *
     * @return A <CODE>String</CODE> value that contains the classpath that JImporter should
     * use to find files.
     */    
    public String getClasspath() {
        return appendRuntimeJarLocation(jEdit.getProperty(classpathSourcePropertyID));
    }
    
    /**
     * Store the classpath that this classpath variable will return.  Most of the
     * time this involves setting a jEdit property.
     *
     * @param classpath a <code>String</code> value that indicates what the 
     * classpath is.
     */
    public void setClasspath(String classpath) {
        jEdit.setProperty(classpathSourcePropertyID, classpath);
    }
    
    /**
     * Get the human-readable name of this classpath source.
     * 
     * @return a <code>String</code> containing the human-readable form of this
     * classpath name.
     */
    public String getLabel() {
        return jEdit.getProperty(labelProperty);
    }
 
    /** 
     * Indicate to jEdit that this is the current classpath type and store any
     * additional information that would be needed for this class to be loaded
     * again.
     */
    public void store() {
        jEdit.setProperty(JEDIT_CLASSPATH_TYPE_PROPERTY, getUniqueIdentifier());
    }
   
    /**
     * Determine if the parameter <code>toCompare</code> is equivalent to the 
     * current classpath instance.
     *
     * @param toCompare a <code>Object</code> value to compare to the current
     * object.
     */
    public boolean equals(Object toCompare) {
        boolean isEqual = true;
        
        if (!(toCompare instanceof Classpath)) {
            isEqual = false;
        } else {
           isEqual = (((Classpath)toCompare).getUniqueIdentifier().equals(this.getUniqueIdentifier()));  
        }
        
        return isEqual;
    }
}
