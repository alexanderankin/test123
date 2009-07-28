/*
 * OpenIt jEdit Plugin (JavaSourcePathFile.java) 
 *  
 * Copyright (C) 2003 Matt Etheridge (matt@etheridge.org)
 * Copyright (C) 2003 Denis Koryavov
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

import java.io.File;

import org.etheridge.common.utility.JavaUtilities;

/**
 * A java source path file.
 */
public class JavaSourcePathFile extends SourcePathFile implements Comparable {
        // the outer-most class (or interface) name of this java source file
        private String mClassName;
        
        // the fully qualified package name of this class
        private String mPackageName;
        
        //{{{ Constructor
        /**
         * Constructor
         *
         * @param sourcePathElement the sourcePathElement that this file is a part of
         * (eg. D:\source)
         * @param file the Java File that represents this Java source file in the
         * filesystem.
         */
        public JavaSourcePathFile(SourcePathElement sourcePathElement, File file) {
                super(sourcePathElement, file);
                
                initialize();
        } 
        //}}}
        
        //{{{ getClassName method.
        public String getClassName() {
                return mClassName;    
        } //}}}
        
        //{{{ getPackageName method.
        public String getPackageName()
        {
                return mPackageName;
        } //}}}
        
        //{{{ toString method.
        public String toString() {
                return mClassName + ((mPackageName.length() != 0) ? " (" + mPackageName + ")" : "");
        } //}}}
        
        //{{{ compareTo method.
        public int compareTo(Object o) {
                int superComparison = super.compareTo(o);
                
                // if they are not equal, then just return now
                if (superComparison != 0) {
                        return superComparison;
                }
                
                // otherwise, compare the java class and package names
                JavaSourcePathFile compareObject = (JavaSourcePathFile) o;
                
                // if the classnames are not equal return the comparison
                int classNameComparison = 
                getClassName().compareTo(compareObject.getClassName());
                if (classNameComparison != 0) {
                        return classNameComparison;
                }
                
                // if the classnames ARE equal, then compare the package names
                return (getPackageName().compareTo(compareObject.getPackageName()));
        } //}}}
        
        //{{{ initialize method.
        private void initialize() {
                // calculate class name from file name
                int end = getFullName().lastIndexOf('.');
                mClassName = getFullName().substring(0, (end == -1) ? getFullName().length() : end);
                
                // calculate package name from file name
                String packageName = getDirectoryString();
                packageName = packageName.substring(getSourcePathElement().getName().length());
                packageName = packageName.substring(0, packageName.indexOf(getFullName()));
                
                // what's left is the package name, so just convert to a dot package
                mPackageName = JavaUtilities.convertPathPackageToDotPackage(packageName);
                
        } //}}}

        
}
