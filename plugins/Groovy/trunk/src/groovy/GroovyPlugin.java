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
		"jars/"+jEdit.getProperty("options.groovy.groovy-jar"));

	private String installedJar = null;
	private String oldBin = null;

	public void start() {
		installedJar = new String(includedJarPath);
		setVars();
		// Add the groovy-all jar
		if (!jEdit.getProperty("options.groovy.install").equals("jar")) {
			String installPath = jEdit.getProperty(
				"options.groovy.groovy-path");
			setGroovyJar(getEmbeddableJar(installPath).getPath());
		}
	}
	public void stop() {}

	/**
	 * Set the loaded embeddable groovy jar
	 * @param path the path of the new groovy jar
	 */
	public void setGroovyJar(String path) {
		if (installedJar != null) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(installedJar), false);
		}
		jEdit.addPluginJAR(path);
		installedJar = path;
	}

	public void setGroovyJar() {
		setGroovyJar(includedJarPath);
	}

	/**
	 * Downloads groovy from the official website in a new thread
	 */
	public void downloadGroovy() {
		clearGroovy();
		download("groovy");
	}

	/**
	 * Downloads the documentation from the official website in a new thread
	 */
	public void downloadDocs() {
		clearDocs();
		download("docs");
	}

	/**
	 * Utility download method
	 */
	public void download(final String name) {
		final String url = jEdit.getProperty("options.groovy."+name+"-url");
		final View view = jEdit.getActiveView();
		new Thread() {
			public void run() {
				try {
					File temp = File.createTempFile("jedit", "temp.zip");
					URLConnection con = new URL(url).openConnection();
					InputStream in = new BufferedInputStream(
						con.getInputStream());
					OutputStream out = new BufferedOutputStream(
						new FileOutputStream(temp));
					GroovyDownloadObserver obs = new GroovyDownloadObserver(
						view, in, out,
						jEdit.getProperty("options.groovy.download-"+name));
					IOUtilities.copyStream(obs, in, out, true);
					IOUtilities.closeQuietly(out);
					IOUtilities.closeQuietly(in);
					if (obs.isCanceled()) {
						temp.delete();
						view.getStatus().setMessageAndClear(
							"Download canceled.");
						return;
					}
					// Extract
					obs.setMessage(jEdit.getProperty(
						"options.groovy.extracting"));
					ZipFile zfile = new ZipFile(temp);
					File dest = new File(home, name);
					dest.getParentFile().mkdirs();
					Enumeration e = zfile.entries();
					while (e.hasMoreElements()) {
						if (obs.isCanceled()) {
							delete(dest);
							temp.delete();
							view.getStatus().setMessageAndClear(
								"Download canceled.");
							return;
						}
						ZipEntry ze = (ZipEntry) e.nextElement();
						obs.setMessage(jEdit.getProperty(
							"options.groovy.extracting")+" "+
							MiscUtilities.getFileName(ze.getName()));
						if (ze.isDirectory()) {
							new File(dest, ze.getName()).mkdirs();
						} else {
							InputStream zin = zfile.getInputStream(ze);
							OutputStream zout = new FileOutputStream(
								new File(dest, ze.getName()));
							IOUtilities.copyStream(null, zin, zout, false);
							IOUtilities.closeQuietly(zout);
						}
					}
					zfile.close();
					temp.delete();
					jEdit.setBooleanProperty("options.groovy."+name+
						"-downloaded", true);
					obs.setMessage(jEdit.getProperty(
						"options.groovy.download-done"));
					obs.done();
					String[] paths = dest.list(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.startsWith("groovy-");
							}
					});
					if (name.equals("groovy") && jEdit.getProperty(
						"options.groovy.install").equals("full"))
					{
						jEdit.setProperty("options.groovy.groovy-path",
							MiscUtilities.constructPath(
								dest.getPath(), paths[0]));
						jEdit.setProperty("options.groovy.install", "full");
						setVars();
					} else if (name.equals("docs") &&
						jEdit.getPlugin("javadoc.JavadocPlugin") != null)
					{
						// Add the new docs to Javadoc's list
						String path = MiscUtilities.constructPath(
							dest.getPath(), paths[0]);
						String api1 = MiscUtilities.constructPath(
							path, "html/api");
						String api2 = MiscUtilities.constructPath(
							path, "html/gapi");
						String api3 = MiscUtilities.constructPath(
							path, "html/groovy-jdk");
						String sep = File.pathSeparator;
						javadoc.JavadocPlugin.addApi(api1+sep+api2+sep+api3);
					}
				} catch (Exception e) {
					e.printStackTrace();
					view.getStatus().setMessage(jEdit.getProperty(
						"options.groovy.download-error"));
				}
			}
		}.start();
	}

	/**
	 * Removes any downloaded Groovy installation
	 */
	public void clearGroovy() {
		delete(new File(home, "groovy"));
		jEdit.setBooleanProperty("options.groovy.groovy-downloaded", false);
	}

	/**
	 * Removes any downloaded documentation
	 */
	public void clearDocs() {
		if (jEdit.getPlugin("javadoc.JavadocPlugin") != null) {
			String path = jEdit.getProperty("options.javadoc.path");
			StringTokenizer tokenizer = new StringTokenizer(path,
				File.pathSeparator);
			ArrayList<String> toRemove = new ArrayList<String>();
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.startsWith(home)) {
					toRemove.add(token);
				}
			}
			for (String api : toRemove) {
				javadoc.JavadocPlugin.removeApi(api);
			}
		}
		delete(new File(home, "docs"));
		jEdit.setBooleanProperty("options.groovy.docs-downloaded", false);
	}

	/**
	 * If Console is installed, set some environment variables
	 * - Set GROOVY_HOME to the configured path of Groovy
	 * - Add GROOVY_HOME/bin to the system path
	 */
	public void setVars() {
		String groovyHome = null;
		if (!jEdit.getProperty("options.groovy.install").equals("jar")) {
			groovyHome = jEdit.getProperty("options.groovy.groovy-path");
		}
		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			String path = console.ConsolePlugin.getSystemShellVariableValue(
				null, "PATH");
			if (path == null) path = "";
			if (oldBin != null && path.indexOf(oldBin) != -1) {
				int index = path.indexOf(oldBin);
				path = path.substring(0, index)+path.substring(
					index+oldBin.length());
			}
			if (groovyHome != null && groovyHome.length() > 0) {
				console.ConsolePlugin.setSystemShellVariableValue(
					"GROOVY_HOME", groovyHome);
				String bin = MiscUtilities.constructPath(groovyHome, "bin")
					+File.pathSeparator;
				path = bin+path;
				oldBin = bin;
			} else {
				console.ConsolePlugin.setSystemShellVariableValue(
					"GROOVY_HOME", System.getenv("GROOVY_HOME"));
			}
			console.ConsolePlugin.setSystemShellVariableValue(
					"PATH", path);
		}
	}

	/**
	 * Given a groovy install directory, return the name of the groovy-all jar
	 */
	public File getEmbeddableJar(String groovy) {
		File embeddable = new File(groovy, "embeddable");
		if (!embeddable.exists()) {
			return null;
		}
		File[] jars = embeddable.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith("groovy-all-") &&
							name.endsWith(".jar");
				}
		});
		if (jars.length < 1) {
			return null;
		}
		return jars[0];
	}

	/**
	 * Recursively deletes files
	 */
	private void delete(File f) {
		if (f.isDirectory()) {
			File[] children = f.listFiles();
			for (int i = 0; i<children.length; i++) {
				delete(children[i]);
			}
		}
		f.delete();
	}

	public static boolean isFullInstallation() {
		return !jEdit.getProperty("options.groovy.install").equals("jar");
	}
}
