package filesync;

import java.io.File;
import java.util.regex.*;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.gjt.sp.util.StandardUtilities;

/**
 * A glob file filter. This is a little better than the wildcard filter provided
 * with commons.io in that the * and/or ? can appear anywhere and will be recognized.
 * This filter is automatically case-insensitive on Windows, is case-senstive on
 * all other operating systems. This uses jEdit's StandardUtilities to convert
 * a glob to a regex.
 * <p>
 * This also works differently than the commons.io RegexFileFilter. The RegexFileFilter
 * only matches agains the file name itself and does not consider the file path.
 * This filter also considers the path, which makes exclusion by directory simple
 * to define with a glob pattern.
 */
public class GlobFileFilter extends AbstractFileFilter {

    private Pattern pattern;
    
    public GlobFileFilter(String filter) {
        super();
        if (filter == null || filter.isEmpty()) {
            throw new IllegalArgumentException("Filter cannot be empty.");
        }
        
        // convert the glob filter string to a regex
        filter = StandardUtilities.globToRE(filter);
        if (IOCase.SYSTEM.isCaseSensitive()) {
            pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile(filter);
        }
    }
    
    public boolean accept(File file) {
        String filename = file.getAbsolutePath();
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }
    
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));   
    }
    
    public String toString() {
        return "GlobFileFilter[" + pattern == null ? "" : pattern.toString() + ']';
    }
}