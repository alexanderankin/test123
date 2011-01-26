/*
 * @(#)FileList.java   2011.01.15 at 11:09:01 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package hohonuuli.jedit.quickopen;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Given a rootDirectory, this class maintains a list of
 *
 * @author Brian Schlining
 * @since 2011-01-03
 */
public class FileList {

    /**
     * A list of all files (not directories). Sorted by name
     */
    private final List<File> files = new ArrayList<File>();

    /**
     * Contents of files as a list of the names only (no path). They
     * are in the same order as files.
     */
    private final List<String> filenames = new ArrayList<String>();

    /**
     * Specifies the maximum number of files that can be stored. Keeps jEdit
     * from indexing the entire hard drive.
     */
    private final int fileLimit;
    private final File rootDirectory;
    private final boolean includeHidden;
    private final List<String> excludedExtensions;
    private final List<String> excludedDirectories;
    private final boolean matchFirst;

    private int numberOfFiles = 0;


    /**
     * Constructs ...
     *
     * @param rootDirectory
     */
    public FileList(File rootDirectory) {
        if (!rootDirectory.isDirectory()) {
            throw new IllegalArgumentException(rootDirectory.getAbsolutePath() + " is not a directory. " +
                                               "FileList only accepts directories as a constructor argument.");
        }

        Log.log(Log.DEBUG, this, "Calling new FileList(" + rootDirectory + ")");

        this.rootDirectory = rootDirectory;
        fileLimit = jEdit.getIntegerProperty(QuickOpenPlugin.PROP_MAX_FILES, 2000);
        includeHidden = jEdit.getBooleanProperty(QuickOpenPlugin.PROP_INCLUDE_HIDDEN, false);
        excludedExtensions = parseExtensions(jEdit.getProperty(QuickOpenPlugin.PROP_EXCLUDE_EXT, ""));
        excludedDirectories = parseDirectories(jEdit.getProperty(QuickOpenPlugin.PROP_EXCLUDE_DIR, ""));
        matchFirst = jEdit.getBooleanProperty(QuickOpenPlugin.PROP_MATCH_FIRST, false);

        // Recurse through all child directories and add files
        process(rootDirectory);

        // Sort by name
        Collections.sort(files, new FileNameComparator());

        // Maintain a List of strings
        for (File file : files) {
            filenames.add(file.getName());
        }
    }

    private static List<String> parseExtensions(String s) {
        List<String> c = new ArrayList<String>();
        if (s.length() > 0) {
            String[] parts = s.split(",");
            for (String extension : parts) {
                // take from '.' to end of string
                extension = extension.trim();
                if (extension.contains(".")) {
                    extension = extension.substring(extension.indexOf("."), extension.length());
                }
                else {
                    extension = "." + extension;
                }
                c.add(extension);
            }
        }
        Collections.sort(c);
        return c;
    }

    private static List<String> parseDirectories(String s) {
        List<String> c = new ArrayList<String>();
        if (s.length() > 0) {
            String[] exts = s.split(",");
            for (String dir : exts) {
                c.add(dir.trim());
            }
        }
        Collections.sort(c);
        return c;
    }

    private static String parseExt(String name) {
        String ext = null;
        int idx = name.lastIndexOf(".");
        if (idx >= 0) {
            ext = name.substring(idx, name.length());
        }
        return ext;
    }

    /**
     * @return
     */
    public List<String> getFilenames() {
        return new ArrayList<String>(filenames);
    }

    /**
     * @return
     */
    public List<File> getFiles() {
        return new ArrayList<File>(files);
    }

    /**
     * @return
     */
    public File getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Recurse through all child directories and add the files to the list
     * @param directory
     */
    private void process(File directory) {
        File[] allFiles = directory.listFiles();

        for (File file : allFiles) {

            if ((fileLimit > 0) && (numberOfFiles <= fileLimit)) {

                String name = file.getName();
                boolean isHidden = name.startsWith(".");
                boolean include = isHidden ? includeHidden : true;
                if (file.isDirectory() && include && !excludedDirectories.contains(name)) {
                    process(file);
                }
                else if (include && !excludedExtensions.contains(parseExt(name))) {
                    files.add(file);
                    numberOfFiles++;
                }

            }
        }
    }

    /**
     * Searches through the list of files and returns any that match
     * @param searchString
     * @return
     */
    public List<File> search(String searchString) {
        List<File> matches = new ArrayList<File>();
        Pattern pattern = toPattern(searchString);

        for (int i = 0; i < filenames.size(); i++) {
            String name = filenames.get(i);

            if (pattern.matcher(name).matches()) {
                matches.add(files.get(i));
            }
        }

        return matches;
    }

    /**
     * Transforms sequence of characters that a user might type into a searchbox
     * into a regular expresssion patter. For example, typing in "buf" would
     * give the pattern "^.*b.*u.*f"
     * @param searchString
     * @return
     */
    private Pattern toPattern(String searchString) {

        // StringBuilder sb = new StringBuilder("^.*");
        StringBuilder sb = new StringBuilder("^");

        if (!matchFirst) {
            sb.append(".*");
        }

        // Transform string to regular expression
        for (int i = 0; i < searchString.length(); i++) {
            sb.append(searchString.charAt(i)).append(".*");
        }

        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    }

    private class FileNameComparator implements Comparator<File> {

        /**
         *
         * @param o1
         * @param o2
         * @return
         */
        public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
