package superabbrevs.migration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.jEdit;

import superabbrevs.Paths;
import superabbrevs.model.Abbreviation;
import superabbrevs.model.Mode;
import superabbrevs.repository.ModeRepository;
import superabbrevs.utilities.Log;

public class Migration {

	private final ModeRepository modeRepository;
	
	public Migration(ModeRepository modeRepository) {
		this.modeRepository = modeRepository;
		
	}
	
    public static void Migrate() {
        Log.log(Log.Level.DEBUG, Migration.class, "Migrating old abbreviations");
        //importOldAbbrevs();
        //deleteAbbrevsDir();
        removeOldMacros();
    }
    
    @SuppressWarnings("unused")
	private static void deleteAbbrevsDir() {
        // Remove settings directory
        File oldAbbrevsDir = new File(Paths.OLD_ABBREVS_DIR);
        deleteDirectory(oldAbbrevsDir);    
    }

    @SuppressWarnings("unused")
	private void importOldAbbrevs() {
        for (org.gjt.sp.jedit.Mode mode : jEdit.getModes()) {
            Hashtable<String, ArrayList<Abbreviation>> newAbbrevs = loadAbbrevs(mode.getName());
            Hashtable<String, String> oldAbbrevs = readModeFile(mode.getName());
            
            if (oldAbbrevs != null) {
                importOldAbbrevsForMode(mode.getName(), oldAbbrevs, newAbbrevs);
            }
        }
    }

    private static boolean deleteDirectory(File path) {
        if (path == null) {
            return false;
        }
        
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        
        return (path.delete());
    }

    private void importOldAbbrevsForMode(
            String modeName, Hashtable<String, String> oldAbbrevs,
            Hashtable<String, ArrayList<Abbreviation>> newAbbrevs) {

        for (Entry<String, String> abbrev : oldAbbrevs.entrySet()) {
            insertIfNotExist(abbrev.getKey(), abbrev.getValue(), newAbbrevs);
        }

        Mode mode = new Mode(modeName);
        mode.getAbbreviations().addAll(flatten(newAbbrevs));
            
        // todo migrate variables
        modeRepository.save(mode);
    }

    private static ArrayList<Abbreviation> flatten(
            Hashtable<String, ArrayList<Abbreviation>> abbrevs) {
        ArrayList<Abbreviation> result = new ArrayList<Abbreviation>();

        for (ArrayList<Abbreviation> abbrevsList : abbrevs.values()) {
            for (Abbreviation abbrev : abbrevsList) {
                result.add(abbrev);
            }
        }
        return result;
    }

    private static void insertIfNotExist(String abbrev, String expansion,
            Hashtable<String, ArrayList<Abbreviation>> abbrevs) {
        ArrayList<Abbreviation> abbrevsList = abbrevs.get(abbrev);
        if (abbrevsList == null) {
            abbrevsList = new ArrayList<Abbreviation>();
            abbrevs.put(abbrev, abbrevsList);
        }

        boolean found = false;

        Iterator<Abbreviation> iter = abbrevsList.iterator();
        while (!found && iter.hasNext()) {
            Abbreviation ab = iter.next();
            found = ab.getExpansion().equals(expansion);
        }

        if (!found) {
            Abbreviation ab = new Abbreviation(abbrev, abbrev, expansion);
            abbrevsList.add(ab);
        }
    }

    private Hashtable<String, ArrayList<Abbreviation>> loadAbbrevs(String modeName) {
        Hashtable<String, ArrayList<Abbreviation>> result =
                new Hashtable<String, ArrayList<Abbreviation>>();

        for (Abbreviation abbrev : modeRepository.load(modeName).getAbbreviations()) {
            ArrayList<Abbreviation> abbrevs = result.get(abbrev.getAbbreviationText());
            if (abbrevs == null) {
                abbrevs = new ArrayList<Abbreviation>();
                result.put(abbrev.getAbbreviationText(), abbrevs);
            }

            abbrevs.add(abbrev);
        }

        return result;
    }

    private static Hashtable<String, String> readModeFile(String name) {
        return readObjectFile(getModeFile(name));
    }

    private static File getModeFile(String name) {
        File modeDir = new File(Paths.OLD_ABBREVS_DIR);

        if (!modeDir.exists()) {
            //make the SuperAbbrev settings dir
            modeDir.mkdir();
        }

        File modeFile = new File(MiscUtilities.constructPath(
                Paths.OLD_ABBREVS_DIR, name));

        return modeFile;
    }

    private static void removeOldMacros() {

        File macrosDir = new File(Paths.MACRO_DIR);
        if (macrosDir.exists()) {
            File tabFile = new File(MiscUtilities.constructPath(
                    Paths.MACRO_DIR,
                    Paths.TAB_MACRO));
            tabFile.delete();

            File shiftTabFile = new File(MiscUtilities.constructPath(
                    Paths.MACRO_DIR,
                    Paths.SHIFT_TAB_MACRO));
            shiftTabFile.delete();
            macrosDir.delete();
        }
    }

    @SuppressWarnings("unchecked")
    private static Hashtable<String, String> readObjectFile(File file) {
        if (file.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);
                ObjectInputStream s = new ObjectInputStream(in);
                return (Hashtable<String, String>) s.readObject();
            } catch (FileNotFoundException e) {
                Log.log(Log.Level.ERROR, Migration.class, e);
            } catch (IOException e) {
                Log.log(Log.Level.ERROR, Migration.class, e);
            } catch (ClassNotFoundException e) {
                Log.log(Log.Level.ERROR, Migration.class, e);
            }
        }
        return null;
    }
}
