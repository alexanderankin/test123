/**
 *
 */
package net.jakubholy.jedit.autocomplete;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * @author Jakub Holy
 *
 */
public class TestUtils
{
	private static boolean isJEditStarted = false;

	/** Holds test properties. */
	static ResourceBundle CONFIG = ResourceBundle.getBundle("junit_test");

	/** Direcotry with jEdit user settings - relative to user.home */
	private static final String jEditSettingsDir =
		System.getProperty("user.home") + File.separator
		+ "test_jedit_settings";

	/** Where jEdit is installed. */
	private static String jeditHome = CONFIG.getString("test.jedit.home");

	/** Start jEdit if it is not running already. */
	public static synchronized void startJEdit()
	{
		// -log=
		// -settings=settingsDir
		if (!isJEditStarted)
		{
			// Check the jEditSettingsDir
			File settings = new File(jEditSettingsDir);
			if(!settings.exists())
			{
				throw new RuntimeException("The jEdit settings directory '"
						+ settings.getAbsolutePath() + "' doesn't exist, "
						+ "create it first, please.");
			}
			else if (!settings.isDirectory())
			{
				throw new RuntimeException("The given jEdit settings directory '"
						+ settings.getAbsolutePath() + "' isn't a directory.");
			}
			else
			{
				// Don't show the splash screen
				File nosplash = new File(jEditSettingsDir,"nosplash");
				try
				{
					nosplash.createNewFile();
				}
				catch (IOException e)
				{} // ignore

				// Check for the plugin
				File pluginJar = new File(jEditSettingsDir,"jars"
						+ File.separator + "TextAutocomplete.jar");
				if(! pluginJar.exists())
				{
					throw new RuntimeException("The tested plugin "
							+ "TextAutocomplete is not installed in the settings "
							+ "directory '"
							+ settings.getAbsolutePath() + "' ");
				}
			}

			// Set jEdit home
			if(System.getProperty("jedit.home") == null)
			{ System.setProperty("jedit.home", jeditHome); }

			// Start jEdit
			String[] args = new String[]{
					"-log=" + Log.DEBUG,
					"-settings=" + jEditSettingsDir,
					"-noserver",
					"-norestore"
			};
			jEdit.main( args );

			isJEditStarted = true;
		}
		else
		{
			// TODO: Reload the plugin?
			// see jEdit.removePluginJAR(PluginJAR jar, boolean exit)
			// jEdit.getPluginJAR(String path)
		} // if-else jEdit not yet started
	} // startJEdit
}
