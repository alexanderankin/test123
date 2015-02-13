package clojure;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.util.Log;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.ThreadUtilities;

public class ClojureDownloader {
	// for parsing version numbers from the repo index page
	final static Pattern linkPattern = Pattern.compile("^<a href=\"([^\\.].*?)/\">.*");

	// for parsing the development version number, if any, from the version
	final static Pattern developmentPattern = Pattern.compile("(?<majorversion>.*?)-(((?<alpha>alpha)(?<alphaversion>\\d*?))|((?<beta>beta)(?<betaversion>\\d*?))|((?<rc>RC)(?<rcversion>\\d*?)))$");

	static class AvailableVersions {
		ArrayList<String> stable;
		ArrayList<String> rc;
		ArrayList<String> beta;
		ArrayList<String> alpha;

		public AvailableVersions() {
			this.stable = new ArrayList<String>();
			this.rc = new ArrayList<String>();
			this.beta = new ArrayList<String>();
			this.alpha = new ArrayList<String>();
		}
	}

	/**
 	 * Custom Comparator for correctly sorting versions.
	 * It sorts descending by default, and takes the version number after any development release tag into account.
	 */
	static class VersionComparator implements Comparator<String> {
		public int compare(String v1, String v2) {
			Matcher m1 = developmentPattern.matcher(v1);
			Matcher m2 = developmentPattern.matcher(v2);

			if (!m1.matches() || !m2.matches()) {
				return v2.compareTo(v1);
			}

			if (!m1.group("majorversion").equals(m2.group("majorversion"))) {
				return v2.compareTo(v1);
			}

			if (m1.group("alpha") != null && m2.group("alpha") != null) {
				return Integer.valueOf(m2.group("alphaversion")).compareTo(Integer.valueOf(m1.group("alphaversion")));
			} else if (m1.group("beta") != null && m2.group("beta") != null) {
				return Integer.valueOf(m2.group("betaversion")).compareTo(Integer.valueOf(m1.group("betaversion")));
			} else if (m1.group("rc") != null && m2.group("rc") != null) {
				return Integer.valueOf(m2.group("rcversion")).compareTo(Integer.valueOf(m1.group("rcversion")));
			}

			return v2.compareTo(v1);
		}
	}

	/**
 	 * Attempt to get all of the available Clojure versions. Returns null if an exception is encountered along
	 * the way.
	 */
	public static AvailableVersions getAvailableVersions(String repo) {
		AvailableVersions versions = new AvailableVersions();
		InputStream in = null;

		try {
			in = new URL(repo).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = reader.readLine()) != null) {
				Matcher m = linkPattern.matcher(line);
				if (!m.matches()) {
					continue;
				}
				String v = m.group(1);
				// For now, only split Clojure itself into different channels.
				if (repo == ClojureRepository.CLOJURE_URL) {
					Matcher dm = developmentPattern.matcher(v);
					if (dm.matches()) {
						if (dm.group("rc") != null) versions.rc.add(v);
						else if (dm.group("beta") != null) versions.beta.add(v);
						else if (dm.group("alpha") != null) versions.alpha.add(v);
					} else {
						versions.stable.add(v);
					}
				} else {
					versions.stable.add(v);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			versions = null;
		}
		finally {
			IOUtilities.closeQuietly(in);
		}

		if (versions != null) {
			VersionComparator comparator = new VersionComparator();
			Collections.sort(versions.stable, comparator);
			Collections.sort(versions.rc, comparator);
			Collections.sort(versions.beta, comparator);
			Collections.sort(versions.alpha, comparator);
		}

		return versions;
	}

	public static ArrayList<String> getAvailableLibs() {
		ArrayList<String> libs = new ArrayList<String>();
		InputStream in = null;

		try {
			in = new URL(ClojureRepository.ROOT_URL).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = reader.readLine()) != null) {
				Matcher m = linkPattern.matcher(line);
				if (!m.matches()) {
					continue;
				}
				String lib = m.group(1);
				if (!"clojure".equals(lib)) {
					libs.add(lib);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			libs.clear();
		} finally {
			IOUtilities.closeQuietly(in);
		}

		return libs;
	}

	public static String getDownloadPath(String repo, String jar, String version) {
		return repo + version + "/" + jar + "-" + version + ".jar";
	}

	/**
 	 * This method returns the version number of the most recent stable release of Clojure.
	 * It assumes a channel of "Stable".
	 */
	public static String getBest() {
		AvailableVersions versions = getAvailableVersions(ClojureRepository.CLOJURE_URL);
		return versions.stable.get(0);
	}

	public static void downloadLib(String lib, String version) {
		String dest = MiscUtilities.constructPath(EditPlugin.getPluginHome(jEdit.getPlugin("clojure.ClojurePlugin")).getAbsolutePath(), lib + ".jar");
		jEdit.setProperty("clojure." + lib + ".version", version);
		jEdit.setProperty("clojure." + lib + ".path", dest);
		String url = getDownloadPath(ClojureRepository.forLib(lib), lib, version);
		downloadTo(new File(dest), url);
	}

	/**
 	 * If Clojure doesn't exist, download the most recent version. Otherwise, do nothing.
	 */
	public static void downloadIfMissing() {
		File clj = new File(jEdit.getProperty("clojure.path"));
		if (!clj.exists()) {
			String version = getBest();
			jEdit.setProperty("clojure.channel", "Stable");
			jEdit.setProperty("clojure.version", version);
			downloadTo(clj, getDownloadPath(ClojureRepository.CLOJURE_URL, "clojure", version));
		}
	}

	/**
 	 * Download the given version of Clojure to the path specified by the clojure.path property.
	 */
	public static void download(String version) {
		downloadTo(new File(jEdit.getProperty("clojure.path")), getDownloadPath(ClojureRepository.CLOJURE_URL, "clojure", version));
	}

	/**
 	 * This method handles the actual download by kicking off a background thread.
	 */
	private static void downloadTo(final File dest, final String url) {
		ThreadUtilities.runInBackground(new Runnable() {
			public void run() {
				Log.log(Log.DEBUG, this, "Downloading " + url + " to " + dest.getAbsolutePath());
				InputStream in = null;
				OutputStream out = null;

				try {
					dest.createNewFile();
					in = new URL(url).openStream();
					out = new FileOutputStream(dest);
					if (!IOUtilities.copyStream(null, in, out, false)) {
						Log.log(Log.ERROR, this, "No exception occurred, but the stream failed to copy.");
					}
				} catch (IOException e) {
					Log.log(Log.ERROR, this, "Error while downloading.");
					e.printStackTrace();
				} finally {
					IOUtilities.closeQuietly(in);
					IOUtilities.closeQuietly(out);
				}
			}
		});
	}
}
