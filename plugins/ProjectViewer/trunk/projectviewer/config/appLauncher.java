package projectviewer.config;

import java.io.*;
import java.text.*;
import java.util.*;

/**
* @author payne
*
* To change this generated comment edit the template variable "typecomment":
* Window>Preferences>Java>Templates.
* To enable and disable the creation of type comments go to
* Window>Preferences>Java>Code Generation.

*/


public class appLauncher {

    TreeMap appCol;
    String SettingsFileName;

    public appLauncher(String fileName) {
        SettingsFileName = fileName;
	appCol = new TreeMap();
        try {this.loadExts();} catch (java.io.IOException e){ ;; }
        
    }

    public String getExec(String fileExt) {

        if (fileExt.length() > 0)

            return (String)appCol.get(fileExt.trim());
        else

            return "";
    }

    public Set getAppList() {
       //return all the values
        return appCol.entrySet(); 
        
    }
    
    
    public void addAppExt(String fileExt, String execPath) {

        if (fileExt.trim().length() > 0)
            appCol.put(fileExt.trim(), execPath);
    }
    
     public void removeAppExt(String fileExt) {
        appCol.remove(fileExt);
    }
    
    public int getCount() {
       return appCol.size();
    }
    
    /** load extension properties from file **/
    public void loadExts()
                  throws IOException {
                  
        
        Properties props = new Properties();
        FileInputStream inprops = new FileInputStream(SettingsFileName);
        
        appCol.clear();
        props.load(inprops);
        Set appSet = props.entrySet();
        Iterator iter = appSet.iterator();

        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry)iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            this.addAppExt((String)key, (String)value);
        }

        inprops.close();
    }

    public void storeExts()
                   throws IOException {

        Properties props = new Properties();
        PrintWriter out = new PrintWriter(new FileWriter(SettingsFileName));
        Set appSet = appCol.entrySet();
        Iterator iter = appSet.iterator();

        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry)iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            out.println(key + "=" + value);
        }

        out.println("");
        out.close();    
        //props.save(outprops,"ext links");
    }
    
	// was private, but gets called from TreeContextMenuListener.java::LaunchExternal()
    public void launchApp(String ext,String sFileName) {
        String executable = (String)appCol.get(ext);
        
            if (appCol.containsKey(ext)) {
            Runtime rt = Runtime.getRuntime();
            String[] callAndArgs = { executable, sFileName};
            try {
               Process child = rt.exec(callAndArgs);
               child.wait(4);
               System.out.println("Process exit code is: " + child.exitValue());
               }
            catch(java.io.IOException e) {
            System.err.println(
            "IOException starting process!");
            }
            catch(InterruptedException e) {
               System.err.println(
               "Interrupted waiting for process!");
            }
      
            } else {
		javax.swing.JOptionPane.showMessageDialog(null, "No application set for this extension!");    
	    }
            
    
    }
    
}

