// $Id$
/*
 * TemplatesPlugin.java - Plugin for importing code templates
 * Copyright (C) 1999 Steve Jakob
 *
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
package templates;

import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.util.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.util.Log;
import velocity.BufferWriter;
import velocity.jEditContext;
import velocity.VelocityConstants;

/**
 * A jEdit plugin for adding a templating function.
 */
public class TemplatesPlugin extends EditPlugin
    implements RuntimeConstants, VelocityConstants
{
	// private static TemplatesAction myAction = null;
	private static String defaultTemplateDir;
	private static String defaultVelocityDir;
	private static String sepChar;		// System-dependant separator character
	private static TemplateDir templates = null;
    private static VelocityEngine engine;
	
	/**
	 * Returns the root TemplateDir object, which represents templates as a  
	 * hierarchical tree of TemplateDir and TemplateFile objects.
	 * @return The current TemplateDir object.
	 */
	public static TemplateDir getTemplates() { return templates; }
	
	/**
	 * Sets the root TemplateDir object to another value.
	 * @param newTemplates The new TemplateDir object
	 */
	public static void setTemplates(TemplateDir newTemplates) {
		templates = newTemplates;
	}
	
	/**
	 * Returns the directory where templates are stored
	 * @return A string containing the template directory path.
	 */
	public static String getTemplateDir() {
		String templateDir = jEdit.getProperty("plugin.TemplatesPlugin.templateDir.0","");
		if (templateDir.equals("")) {
			templateDir = defaultTemplateDir;
		}
		return templateDir;
	}
	
	/** 
	 * Change the directory where templates are stored
	 * @param templateDirVal The new templates directory
	 */
	public static void setTemplateDir(String templateDirVal) {
		if (!templateDirVal.endsWith(sepChar)) {
			templateDirVal = templateDirVal + sepChar;
		}
		if (defaultTemplateDir.equals(templateDirVal)) {
			jEdit.unsetProperty("plugin.TemplatesPlugin.templateDir.0");
		} else {
			jEdit.setProperty("plugin.TemplatesPlugin.templateDir.0",templateDirVal);
		}
		templates = new TemplateDir(null, new File(templateDirVal));
		TemplatesPlugin.refreshTemplates();
	}
	
	public static boolean getAcceleratorPassThruFlag() {
		return jEdit.getBooleanProperty(
				"plugin.TemplatesPlugin.accelPassThruFlag", false);
	}
	
	public static void setAcceleratorPassThruFlag(boolean accelPassThru) {
		if (false == accelPassThru) {
			jEdit.unsetProperty("plugin.TemplatesPlugin.accelPassThruFlag");
		} else {
			jEdit.setBooleanProperty("plugin.TemplatesPlugin.accelPassThruFlag", true);
		}
	}
	
   /**
    * Returns the velocity engine.
    */
   public static VelocityEngine getEngine()
      throws Exception
   {
      if (engine == null) {
         engine = new VelocityEngine();
         Properties props = loadVelocityProperties();
         props.setProperty(RUNTIME_LOG, getVelocityLog());
         props.setProperty(FILE_RESOURCE_LOADER_PATH, TemplatesPlugin.getTemplateDir());
         engine.init(props);
      }
      return engine;
   }

   /**
    * Returns the path to the velocity directory.
    */
   public static String getVelocityDirectory()
   {
		String velocityDir = jEdit.getProperty("plugin.TemplatesPlugin.velocityDir.0","");
		if (velocityDir.equals("")) {
			velocityDir = defaultVelocityDir;
		}
		return velocityDir;
   }

	/** 
	 * Change the directory where velocity resources are stored
	 * @param velocityDirVal The new velocity directory
	 */
	public static void setVelocityDir(String velocityDirVal) {
		if (!velocityDirVal.endsWith(sepChar)) {
			velocityDirVal = velocityDirVal + sepChar;
		}
		if (defaultVelocityDir.equals(velocityDirVal)) {
			jEdit.unsetProperty("plugin.TemplatesPlugin.velocityDir.0");
		} else {
			jEdit.setProperty("plugin.TemplatesPlugin.velocityDir.0",velocityDirVal);
		}
	}
	
   /**
    * Returns the file for velocity logging messages.
    */
   private static String getVelocityLog()
   {
      return MiscUtilities.constructPath(getVelocityDirectory(),
         "velocity.log");
   }

   /**
    * Return the path to the velocity properties file.
    */
   private static String getVelocityPropertiesPath()
   {
      return MiscUtilities.constructPath(getVelocityDirectory(),
         "velocity.properties");
   }

   /**
    * Load velocity properties.
    */
   private static Properties loadVelocityProperties()
   {
      Properties props = new Properties();
      InputStream in = null;
	  TemplatesPlugin thePlugin = (TemplatesPlugin) jEdit.getPlugin(
		 			"templates.TemplatesPlugin");
	 // Load the normal Velocity properties
      try {
		 in = thePlugin.getClass().getClassLoader().
				getResourceAsStream("velocity/velocity.properties");
         props.load(in);
      } catch (IOException e) {
         Log.log(Log.ERROR, thePlugin, "Error loading normal velocity properties");
      } finally {
         IO.close(in);
      }
	 // Load user's custom Velocity properties, if present
      try {
         File f = new File(getVelocityPropertiesPath());
         if (f.exists()) {
            in = new FileInputStream(f);
			props.load(in);
         }
      } catch (IOException e) {
         Log.log(Log.ERROR, thePlugin, "Error loading custom velocity properties");
      } finally {
         IO.close(in);
      }
      return props;
   }

   //{{{ EditPlugin Methods
   /**
    * Start this plugin.
    */
   public void start()
   {
      sepChar = System.getProperty("file.separator");
      defaultTemplateDir = jEdit.getSettingsDirectory() + sepChar +
				"templates" + sepChar;
      defaultVelocityDir = jEdit.getSettingsDirectory() + sepChar +
				"velocity" + sepChar;
      File velocityDir = new File(getVelocityDirectory());
      if (velocityDir.isFile()) {
         Log.log(Log.DEBUG, this, "'" + getVelocityDirectory() + "' is a file");
      }
      if (!velocityDir.exists() && !velocityDir.mkdirs()) {
         Log.log(Log.DEBUG, this, "Cannot make directory '" + getVelocityDirectory() + "'");
      }

      File templatesDir = new File(TemplatesPlugin.getTemplateDir());
      if (!templatesDir.exists() && !templatesDir.mkdirs()) {
         Log.log(Log.DEBUG, this, "Cannot make directory '" + TemplatesPlugin.getTemplateDir() + "'");
      }
      // templates = new TemplateDir(new File(this.getTemplateDir()));
      templates = new TemplateDir(null, templatesDir);
      templates.refreshTemplates();
   }

	/**
	 * Not used.
	 */
	public void stop()
	{
      AcceleratorManager.getInstance().save();
	}

	/**
	 * Create the "Templates" menu item.
	 * @param menuItems Used to add menus and menu items
	 */
	public void createMenuItems(Vector menuItems) {
		TemplatesMenu templatesMenu = new TemplatesMenu();
		menuItems.addElement(templatesMenu);
		templatesMenu.addNotify();
	}
	
	/**
	 * Create the plugins option pane
	 * @param optionsDialog The dialog in which the OptionPane is to be displayed.
	 */
	public void createOptionPanes(OptionsDialog optionsDialog) {
		// optionsDialog.addOptionPane(new TemplatesOptionPane());
		OptionGroup grp = new OptionGroup("Templates");
		grp.addOptionPane(new TemplatesOptionPane());
		grp.addOptionPane(new AcceleratorOptionPane());
		optionsDialog.addOptionGroup(grp);
	}
	
	/**
	 * Scans the templates directory and sends an EditBus message to all 
	 * TemplatesMenu objects to update themselves. Backup files are ignored
	 * based on the values of the backup prefix and suffix in the "Global
	 * Options" settings.
	 */
	public static void refreshTemplates() {
		String templateDirStr = getTemplateDir();
		File templateDir = new File(templateDirStr);
		try {
			if (!templateDir.exists()) {	// If the template directory doesn't exist
				Log.log(Log.DEBUG,
						jEdit.getPlugin(jEdit.getProperty("plugin.TemplatesPlugin.name")),
						"Attempting to create templates directory: " + templateDirStr);
				templateDir.mkdir();		// then create it
				if (!templateDir.exists())	// If insufficent privileges to create it
					throw new java.lang.SecurityException();
			}
			setTemplates(new TemplateDir(null, templateDir));
			getTemplates().refreshTemplates();
			buildAllMenus();
		} catch (java.lang.SecurityException se) {
			Log.log(Log.ERROR,
				jEdit.getPlugin(jEdit.getProperty("plugin.TemplatesPlugin.name")),
				jEdit.getProperty("plugin.TemplatesPlugin.error.create-dir") + templateDir
				);
		}
	}
	
	private static void buildAllMenus() {
		EditBus.send(new TemplatesChanged());
	}
	
	/**
	 * Prompt the user for a template file and load it into the view from
	 * which the request was initiated.
	 * @param view The view from which the "Edit Template" request was made.
	 */
	public static void editTemplate(View view) {
		JFileChooser chooser = new JFileChooser(getTemplateDir());
		int retVal = chooser.showOpenDialog(view);
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			if(file != null)
			{
				try
				{
					// Load file into jEdit
					jEdit.openFile(view, file.getCanonicalPath());
				}
				catch(IOException e)
				{
					// shouldn't happen
				}
			}
		}
	}

	/**
	 * Save the current buffer as a template. The file chooser displayed
	 * uses the Templates directory as the default.
	 * @param view The view from which the "Save Template" request was made.
	 */
	public static void saveTemplate(View view) {
		JFileChooser chooser = new JFileChooser(getTemplateDir());
		int retVal = chooser.showSaveDialog(view);
		if(retVal == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			if(file != null)
			{
				try
				{
					// Save file
					view.getBuffer().save(view, file.getCanonicalPath());
				}
				catch(IOException e)
				{
					// shouldn't happen
				}
			}
		}
	}
	
	/**
	 * Process the template file indicated by the given path string, and
	 * insert its text into the given view.
	 * @param path The absolute path to the desired template file.
	 * @param view The view into which the template text is to be inserted.
	 */
/*	public static void processTemplate(String path, View view) {
		File templateFile = new File(path);
		Template template = new Template(templateFile);
		template.processTemplate(view);
	} */

   /**
    * Process a specified template.
    */
   public static void processTemplate(String template, JEditTextArea textArea)
   {
      processTemplate(template, GUIUtilities.getView(textArea), textArea);
   }

   /**
    * Process a specified template.
    */
   public static void processTemplate(String template, View view, JEditTextArea textArea)
   {
      processTemplate(template, new VelocityContext(), view, textArea);
   }

   /**
    * Process a specified template.
    */
   public static void processTemplate(String template, Context ctx,
                               View view, JEditTextArea textArea)
   {
      try {
         if (!getEngine().templateExists(template)) {
            GUIUtilities.error(view, "plugin.velocity.error.no-template-found",
               new String[]{template});
         }
         ctx = new jEditContext(view, textArea, ctx);
         ((jEditContext) ctx).captureSelection();
         Writer out = new BufferWriter(textArea.getBuffer(),
            textArea.getCaretPosition());
         getEngine().mergeTemplate(template, ctx, out);
         if (ctx.get(CARET) != null) {
            Integer pos = (Integer) ctx.get(CARET);
            textArea.setCaretPosition(pos.intValue());
         }
      } catch (Exception e) {
         TemplatesPlugin thePlugin = (TemplatesPlugin) jEdit.getPlugin(
		 			"templates.TemplatesPlugin");
         Log.log(Log.ERROR, thePlugin, "Error processing template '" + template + "'");
         Log.log(Log.ERROR, thePlugin, e);
      }
   }

}
	/*
	 * Change Log:
	 * $Log$
	 * Revision 1.5  2003/01/17 05:35:09  sjakob
	 * BUG FIX: use of a custom velocity properties file would result in the normal properties
	 * not being loaded. Now custom properties just override the normal ones.
	 *
	 * Revision 1.4  2002/09/11 18:57:35  sjakob
	 * Added ability (configurable in Global Options) to "pass through" abbreviations from
	 * the "Expand Accelerator" action to jEdit's abbreviation expansion function, in cases
	 * where no matching template accelerator is found.
	 *
	 * Revision 1.3  2002/08/24 16:14:12  cyu
	 * Added $_jeditSelection which holds the current buffer selection
	 *
	 * Revision 1.2  2002/07/24 15:33:33  sjakob
	 * Changed hard-coded Velocity resource directory to a jEdit property.
	 *
	 * Revision 1.1  2002/04/30 19:26:10  sjakob
	 * Integrated Calvin Yu's Velocity plugin into Templates to support dynamic templates.
	 *
	 * Revision 1.7  2002/02/26 03:36:46  sjakob
	 * BUGFIX: Templates directory path is no longer stored in a jEdit property if
	 * it is equal to the default Templates path (requested by Mike Dillon).
	 *
	 * Revision 1.6  2002/02/25 21:53:58  sjakob
	 * BUGFIX: Fixed problem where templates directory was not being
	 * created (as reported by Will Sargent).
	 *
	 * Revision 1.5  2002/02/22 02:34:36  sjakob
	 * Updated Templates for jEdit 4.0 actions API changes.
	 * Selection of template menu items can now be recorded in macros.
	 *
	 * Revision 1.4  2001/07/18 13:36:16  sjakob
	 * Removed unnecessary call to TemplatesMenu.addNotify().
	 *
	 * Revision 1.3  2001/07/16 19:10:13  sjakob
	 * BUG FIX: updated TemplatesPlugin to use createMenuItems(Vector menuItems),
	 * rather than the deprecated createMenuItems(View view, Vector menus,
	 * Vector menuItems), which caused startup errors.
	 * Added Mike Dillon's makefile.jmk.
	 *
	 * Revision 1.2  2001/02/23 19:31:39  sjakob
	 * Added "Edit Template" function to Templates menu.
	 * Some Javadoc cleanup.
	 *
	 * Revision 1.1.1.1  2000/04/21 05:05:51  sjakob
	 * Initial import of rel-1.0.0
	 *
	 * Revision 1.6  2000/03/08 06:55:47  sjakob
	 * Use org.gjt.sp.util.Log instead of System.out.println.
	 * Update documentation.
	 * Add sample template files to project.
	 *
	 * Revision 1.5  2000/03/03 06:25:43  sjakob
	 * Redesigned the plugin to fix a bug where only the most recent view had a
	 * Templates menu. Added TemplateFile and TemplateDir classes to handle
	 * files and directories in the Templates directory tree. Templates menus for
	 * all views are refreshed simultaneously.
	 *
	 * Revision 1.4  2000/01/10 21:16:55  sjakob
	 * Made changes suggested by Mike Dillon (Plugin Central maintainer):
	 * - changed comments style in Templates.props
	 * - Templates submenu now added to menus vector, not menuItems
	 *
	 * Revision 1.3  1999/12/21 05:00:52  sjakob
	 * Added options pane for "Plugin options" to allow user to select template directory.
	 * Recursively scan templates directory and subdirectories.
	 * Add subdirectories to "Templates" menu as submenus.
	 * Added online documentation, as well as README.txt and CHANGES.txt.
	 *
	 * Revision 1.2  1999/12/12 06:38:37  sjakob
	 * Modified TemplatesPlugin.java to fix strange Windows ClassCastException.
	 * Cleanup of Javadoc comments in prep for posting source.
	 * Updated web page.
	 *
	 * Revision 1.1  1999/12/12 05:21:04  sjakob
	 * Renamed files CodeTemplates*.* to Templates*.*
	 * New files are Templates.props, TemplatesPlugin.java, TemplatesAction.java
	 *
	 * Revision 1.4  1999/12/10 21:39:56  sjakob
	 * Removed hard-coded string for templates directory.
	 * Using jEdit.getSettingDirectory() instead.
	 * Check for existence of templates directory and, if not present, create it.
	 * Filter out jEdit backup files when scanning templates directory.
	 *
	 * Revision 1.3  1999/12/09 18:52:33  sjakob
	 * Changed menu label "Code Templates" to "Templates".
	 * Now use dedicated templates directory ($HOME/.jedit/templates).
	 *
	 * Revision 1.2  1999/12/09 06:45:48  sjakob
	 * Changed menu labels from hard-coded strings to properties.
	 * Implemented basic template import facility.
	 *
	 * Revision 1.1.1.1  1999/12/09 05:22:21  sjakob
	 * Basic code template plugin framework.
	 * Implemented dynamic menus.
	 *
	 */

