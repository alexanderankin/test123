/*
Copyright (c) 2012, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

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
    
    /**
     * @param filter A glob string pattern to define files for this filter to accept.    
     */
    public GlobFileFilter(String filter) {
        super();
        if (filter == null || filter.isEmpty()) {
            throw new IllegalArgumentException(jEdit.getProperty("filesync.Filter_cannot_be_empty.", "Filter cannot be empty."));
        }
        
        // convert the glob filter string to a regex
        filter = StandardUtilities.globToRE(filter);
        if (IOCase.SYSTEM.isCaseSensitive()) {
            pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
        } else {
            pattern = Pattern.compile(filter);
        }
    }
    
    /**
     * @param file A file to check for acceptance by this filter.
     * @return <code>true</code> if the file is accepted by this filter.
     */
    public boolean accept(File file) {
        String filename = file.getAbsolutePath();
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }
    
    /**
     * @param dir The directory for the file.
     * @param file A file to check for acceptance by this filter.
     * @return <code>true</code> if the file is accepted by this filter.
     */
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));   
    }
    
    public String toString() {
        return "GlobFileFilter[" + pattern == null ? "" : pattern.toString() + ']';
    }
}