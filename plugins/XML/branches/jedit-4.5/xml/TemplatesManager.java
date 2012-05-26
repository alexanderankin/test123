/*
 * TemplatesManager.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2011 Eric Le Lay
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.PluginJAR;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ProgressObserver;

import templates.TemplatesPlugin;

/**
 * install templates bundled with XMLPlugin into the relevant directory
 * and tell Templates plugin to refresh the list.
 *  - Requires the Templates plugin to be installed (watch for ClassNotFoundException !)
 *  - synchronous (is wrapped in a Task in XmlPlugin)
 *  @version $Id$
 */
public class TemplatesManager {
	private static final String TEMPLATES_SUBDIR="XMLPlugin";
	private static final Pattern TEMPLATES_PATTERN = Pattern.compile("templates/(.+)");
	/**
	 * Install templates in an XMLPlugin subdirectory
	 */
	public static void installTemplates(ProgressObserver observer, PluginJAR me){
		try{
			// create the templates subdirectory
			String templateDir = TemplatesPlugin.getTemplateDir();
			File templateSubDir = new File(templateDir, TEMPLATES_SUBDIR);
			if(!templateSubDir.exists()){
				templateSubDir.mkdirs();
			}
			// install the templates
			for(String template : me.getResources()){
				Matcher m = TEMPLATES_PATTERN.matcher(template);
				if(m.matches()){
					Log.log(Log.DEBUG,TemplatesManager.class,"installing template "+template);
					File templateFile = new File(templateDir,TEMPLATES_SUBDIR + File.separator + m.group(1));
					InputStream in = TemplatesManager.class.getClassLoader().getResourceAsStream(template);
					OutputStream out = new FileOutputStream(templateFile);
					boolean saved = IOUtilities.copyStream(observer,in,out,true);
					// clean partially copied file
					if(!saved){
						templateFile.delete();
						Log.log(Log.WARNING, TemplatesManager.class, "installing template "+template+" interrupted, giving up on rest templates");
					}
				}
			}
			// refresh templates list
			SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						TemplatesPlugin.refreshTemplates();
					}
			});
		}catch(IOException e){
			Log.log(Log.ERROR,TemplatesManager.class, "unable to install xml templates", e);
		}
	}
}
