package xml;

import java.io.File;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;
import org.jedit.migration.OneTimeMigrationService;

/** Moves the password cache from a non-standard location (jEdit settings/cache)
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
