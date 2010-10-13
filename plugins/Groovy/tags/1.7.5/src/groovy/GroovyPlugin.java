package groovy;
//{{{ Imports
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
//}}}
public class GroovyPlugin extends EditPlugin {

	public static final String home = EditPlugin.getPluginHome(
		GroovyPlugin.class).getPath();

	public static final String includedJarPath = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("groovy.included-jar"));

	public static final String groovyProp = "groovy.path";

	private String installedJar = null;

	public void start() {
		String path = jEdit.getProperty(groovyProp);
		if (path != null) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(includedJarPath), false);
			jEdit.addPluginJAR(path);
			installedJar = path;
		} else {
			installedJar = includedJarPath;
		}
		setVars();

		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			final File commando = new File(console.ConsolePlugin.getUserCommandDirectory(), "groovy.xml");
			if (!commando.exists()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							InputStream in = getClass().getResourceAsStream("/commands/groovy.xml");
							OutputStream out = new FileOutputStream(commando);
							IOUtilities.copyStream(null, in, out, false);
							IOUtilities.closeQuietly(in);
							IOUtilities.closeQuietly(out);
							jEdit.setProperty("mode.groovy.commando.run", "groovy");
							console.ConsolePlugin.rescanCommands();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}	
		}
	}

	public void stop() {}

	/**
	 * Set the loaded embeddable groovy jar
	 * @param path the path of the new groovy jar
	 */
	public void setGroovyJar(String path) {
		jEdit.setProperty(groovyProp, path);
		jEdit.removePluginJAR(jEdit.getPluginJAR(installedJar), false);
		jEdit.addPluginJAR(path);
		installedJar = path;
	}

	public void setGroovyJar() {
		jEdit.unsetProperty(groovyProp);
		jEdit.removePluginJAR(jEdit.getPluginJAR(installedJar), false);
		jEdit.addPluginJAR(includedJarPath);
		installedJar = includedJarPath;
	}

	/**
	 * If Console is installed, set some environment variables
	 * - Set GROOVY to the path of the jar
	 */
	public void setVars() {
		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			console.ConsolePlugin.setSystemShellVariableValue("GROOVY", getGroovy());
		}
	}

	/**
	 * Returns the path to the groovy jar
	 */
	public String getGroovy() {
		String path = jEdit.getProperty(groovyProp);
		return (path == null) ? includedJarPath : path;
	}

}
