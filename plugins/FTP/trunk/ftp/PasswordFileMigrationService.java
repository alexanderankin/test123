package ftp;

import java.io.File;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;
import org.jedit.migration.OneTimeMigrationService;

/** Moves the password cache from a non-standard location (jEdit settings/cache)
 *  into a proper place under getPluginHome().
 *  
 * @author ezust
 *
 */
public class PasswordFileMigrationService extends OneTimeMigrationService {

	public PasswordFileMigrationService() {
		super("passwordcache");
	}
	
	@Override
	public void migrate() {
		String oldCacheFile = MiscUtilities.concatPath(jEdit.getSettingsDirectory(), "cache/password-cache");
		String newCacheFile = MiscUtilities.concatPath(FtpPlugin.getPluginHome(FtpPlugin.class).toString(), "password-cache");

		File nf = new File(newCacheFile);
		if (nf.isFile()) return;
		File oldf = new File(oldCacheFile);
		if (!oldf.exists()) return;
		oldf.renameTo(nf);
	}

}
