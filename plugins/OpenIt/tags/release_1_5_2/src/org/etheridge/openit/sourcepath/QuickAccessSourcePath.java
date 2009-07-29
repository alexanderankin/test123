/*
 * OpenIt jEdit Plugin (QuickAccessSourcePath.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
 * Copyright (C) 2006 Denis Koryavov 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.etheridge.openit.sourcepath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.etheridge.openit.OpenItProperties;
import org.etheridge.openit.sourcepath.SourcePath;
import org.etheridge.openit.sourcepath.SourcePathElement;
import org.etheridge.openit.sourcepath.SourcePathFile;
import org.etheridge.openit.utility.OpenItRE;

import org.gjt.sp.jedit.jEdit;

import projectviewer.*;

import projectviewer.vpt.*;

/**
* This class is a wrapper around a SourcePath that provides quicker access to 
* the elements in a source path.
*
* On construction it will take all of the elements in the source path and store
* them in data structures that provide quick access to the underlying source
* path files.
*
* NOTE: this wrapper is provided to provide for better GUI response time.
*/
public class QuickAccessSourcePath
{
        // the wrapped source path
        private SourcePath mWrappedSourcePath;
        private ArrayList allFiles = null;
        
        // quick access data map - this is where the "quick" access is implemented.
        // maps between each letter of the alphabet (a-z) and a list of source path
        // files that begin with that letter.
        //
        // Theoretically this should increase data access 26 times faster, however 
        // letter distribution is not quite so simple ;)
        //
        // NOTE: if there are NO classes starting with a particular letter, there will
        // be NO entry in the map.  
        //
        // For example:
        //
        // a -> [Alpha.java, Animal.java, Allo.java]
        // b -> [Bob.java, Builder.java]
        // ...
        // z -> [Zebra.java]
        
        private Map<String, List> mQuickAccessMap;
	      private HashMap<String, Boolean> projectsFiles;
        
        /**
         * Substring index size - this integer determines how many characters should
         * be used as a substring index into the quick access map.  
         * 
         * For example, if the index size is 2, and the file to be indexed is 
         * "this.java", the following indexes will be added to the quick access map
         * if they do not already exist (and "this.java" will be mapped):
         *
         * th - this.java
         * hi - this.java
         * is - this.java
         * s. - this.java
         * .j - this.java
         * ja - this.java
         * av - this.java
         * va - this.java
         *
         * The higher the substring index size the more potential indexes there will
         * be, meaning it will take longer to index, but will be quicker doing lookups.
         *
         * This value *could* be user-configurable, to allow the user to control this
         * ratio.
         */
        private static final int msIndexSize = 2;
        
        /**
         * Constructs a QuickAccessSourcePath
         *
         * NOTE: this is a *BUSY* constructor - it will potentially take a while!
         */
        public QuickAccessSourcePath(SourcePath sourcePath)
        {
                mWrappedSourcePath = sourcePath;
                
                initialize();
        }
        
        /**
         * Gets a list of all SourceFiles starting with the specified character.
         */
        public List getSourceFilesStartingWith(char ch)
        {
                List sourceFileList = mQuickAccessMap.get(String.valueOf(ch).toLowerCase());
                
                if(sourceFileList == null) {
                        return new ArrayList();
                }
                
                return Collections.unmodifiableList(sourceFileList);
        }
        
        /**
         * Gets a list of all SourceFiles containing the specified substring.
         */
        public List getSourceFilesContaining(String string)
        {
                if (string.length() > msIndexSize) {
                        string = string.substring(0,msIndexSize);
                }
                
                List sourceFileList = mQuickAccessMap.get(string.toLowerCase());
                
                if(sourceFileList == null) {
                        return new ArrayList();
                }
                
                return Collections.unmodifiableList(sourceFileList);
        }
        
        /**
         * Returns all files for the project.
         */
        public List getAllFiles() {
                if (allFiles != null) return allFiles;
                Collection<List> sourceFileCollection = mQuickAccessMap.values();
                ArrayList sourceFileList = new ArrayList();
                for(List list : sourceFileCollection) {
                        sourceFileList.addAll(list);
                }
                return Collections.unmodifiableList(sourceFileList);
        }
        
        //
        // private helper methods
        //
        
        private void initialize() {
                // create OpenItRE
		projectsFiles = getProjectsFiles();
                OpenItRE regularExpression = new OpenItRE(
                        jEdit.getProperty(OpenItProperties.EXCLUDES_REGULAR_EXPRESSION),
                        !jEdit.getBooleanProperty(OpenItProperties.IGNORE_CASE_EXCLUDES_FILE_REGULAR_EXPRESSION, false));
                
                
                // initialize the quick access map
                mQuickAccessMap = new HashMap<String, List>();
                
                // for each element in the source path, go through its list of classes
                // and 
                for (Iterator i = mWrappedSourcePath.getSourcePathElements().iterator(); i.hasNext();) {
                        SourcePathElement sourcePathElement = (SourcePathElement) i.next();
                        
                        // iterate through files and store in quick access map
                        for (Iterator j = sourcePathElement.getSourcePathFiles().iterator(); j.hasNext();) {
                                SourcePathFile sourcePathFile = (SourcePathFile) j.next();
                                
                                // if the filename does not match the excludes regular expression then
                                // add it to the quick access map, otherwise ignore it.
                                if (!regularExpression.isMatch(sourcePathFile.getFullName()) 
					&& includeFile(sourcePathFile)) 
				{
                                        // get first letter
                                        String firstLetter = sourcePathFile.getFullName().toLowerCase().substring(0,1);
                                        List<SourcePathFile> currentLetterList = mQuickAccessMap.get(firstLetter);
                                        if (currentLetterList == null) {
                                                currentLetterList = new ArrayList<SourcePathFile>();
                                                mQuickAccessMap.put(firstLetter, currentLetterList);
                                        }
                                        if (!currentLetterList.contains(sourcePathFile)) {
                                                currentLetterList.add(sourcePathFile);
                                        }
                                }
                        }
                }
                
                sort();
                allFiles = new ArrayList(getAllFiles());
        }
	
	// sort each list in the quick access map
	private void sort() {
                for (Iterator<List> i = mQuickAccessMap.values().iterator(); i.hasNext();) {
                        List currentList = i.next();
                        Collections.sort(currentList);
                }
	}
	
	
	private boolean includeFile(SourcePathFile sourcePathFile) {
		if (projectsFiles == null || 
			projectsFiles.get(sourcePathFile.getDirectoryString())!=null)
		{
			return true;	
		}
		return false;
	}
        
        // Returns the current ProjectViewer project files.
        // If ProjectViewer not installed returns null;
        private HashMap<String, Boolean> getProjectsFiles() {
                HashMap<String, Boolean> result = null;
		
                if (jEdit.getBooleanProperty(OpenItProperties.IMPORT_FILES_FROM_CURRENT_PROJECT)) {
			result = new HashMap<String, Boolean>();
                        VPTProject currentProject = ProjectViewer.getActiveProject(jEdit.getActiveView());
                        if (currentProject != null) {
                                Collection nodes  = currentProject.getOpenableNodes();
                                Iterator iter = nodes.iterator();
                                
                                while (iter.hasNext()) {
                                        VPTFile vptFile = (VPTFile)iter.next();
                                        result.put(vptFile.getNodePath(), true);
                                }
                        }
                }
		return result;
        }
}

// :collapseFolds=1:tabSize=8:indentSize=8:folding=explicit:

