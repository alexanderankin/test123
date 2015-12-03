package xml;

import java.io.File;

import java.util.Hashtable;
import java.util.Properties;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.jedit.migration.OneTimeMigrationService;

/** Moves the xml cache folders from a non-standard location (jEdit settings)
 *  into a proper place under getPluginHome().
 *
 * @author ezust
 *
 */
public class CacheMigrationService extends OneTimeMigrationService {

	public CacheMigrationService() {
		super("xmlcache");
	}

	@Override
	public void migrate() {
		Hashtable<String, String> table = new Hashtable<String, String>();
		Properties p = jEdit.getProperties();
		for (Object ko : p.keySet()) {
			String key = ko.toString();
			if (key.startsWith("xml.cache")) {
				String value = jEdit.getProperty(key);
				String v2 = value.replaceFirst(jEdit.getSettingsDirectory(), XmlPlugin.getSettingsDirectory());
				if (!value.equals(v2))
					table.put(key, v2);
			}
		}
		for (String k : table.keySet()) {
			jEdit.unsetProperty(k);
			jEdit.setProperty(k, table.get(k));
			Log.log(Log.DEBUG, this, "Migrating property " + k + " to value: " + table.get(k));
		}

		String dirsToMove[] = new String[] {"dtds", "cache", "import_schema", "relax_ng"};

		for (String dir: dirsToMove) {
			String oldDir = MiscUtilities.concatPath(jEdit.getSettingsDirectory(), dir);
			String newDir = MiscUtilities.concatPath(XmlPlugin.getSettingsDirectory(), dir);
			File nf = new File(newDir);
			if (nf.isDirectory()) continue;
			File oldf = new File(oldDir);
			if (!oldf.isDirectory()) continue;
			boolean success = oldf.renameTo(nf);
			Log.log (Log.DEBUG, this, "Rename " + oldDir + " to " + newDir + " success: " + success);
		}

	}
}
