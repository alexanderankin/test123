/*
 *  SearchMethod.java - A Enumeration class of methods to search for a class
 *  from a classpath.  This class also defines the static method to do the same.
 *  Copyright (C) 2002  Matthew Flower (MattFlower@yahoo.com)
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jimporter.searchmethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.gjt.sp.jedit.jEdit;
import java.util.StringTokenizer;
import java.io.File;

/**
 * A class that lists all of the available search methods.
 *
 * @author Matthew Flower
 */
public abstract class SearchMethod {
    private static ArrayList searchMethods = new ArrayList();
    private static String JEDIT_SEARCH_METHOD_PROPERTY = "jimporter.searchmethod";
    /**
     * ArrayList of Strings used to store the individual elements of a classpath.
     */
    protected ArrayList classPath = new ArrayList();

    /**
     * A SearchMethod instance that indicates that we should search for a class
     * using brute force.
     */
    public static SearchMethod USE_BRUTE_FORCE = new BruteForceClassFinder();
    //public static SearchMethod USE_BUFFERED_CLASSFINDER = new BufferedClassFinder();
    /**
     * This SearchMethod instance is the default that we should use if the user
     * has not saved an explicit selection.
     */
    public static SearchMethod DEFAULT_METHOD = USE_BRUTE_FORCE;


    //--------------------------------------------------------------------------
    private String id;
    private String nameProperty;

    /**
     * Constructor that sets a couple of properties and stores this SearchMethod
     * in the list of all available search methods.
     *
     * @param propertyID    a <code>String</code> value which contains the unique
     * identifier for this search method.
     * @param nameProperty  a <code>String</code> value which contains the jEdit
     * property that is used to look up a human-readable name for this
     * search method.
     */
    protected SearchMethod(String propertyID, String nameProperty) {
        this.id = propertyID;
        this.nameProperty = nameProperty;
        searchMethods.add(this);
    }

    /**
     * Set the Search Method that we should use to search for classes.
     *
     * @param newCurrent  a <code>SearchMethod</code> that will become the
     * "selected" search method.
     * @see #getCurrent
     */
    public static void setCurrent(SearchMethod newCurrent) {
        jEdit.setProperty(JEDIT_SEARCH_METHOD_PROPERTY, newCurrent.getUniqueIdentifier());
    }

    /**
     * Gets the SearchMethod for a given unique identifier.
     *
     * @param uniqueID a <code>String</code> value that uniquely identifies a
     * SearchMethod.
     * @return A <code>SearchMethod</code> object that matches the id provided
     * as a parameter.
     */
    public static SearchMethod getForID(String uniqueID) {
        SearchMethod searchMethodToReturn = DEFAULT_METHOD;
        Iterator it = searchMethods.iterator();

        while (it.hasNext()) {
            SearchMethod testSearchMethod = (SearchMethod) it.next();

            if (testSearchMethod.getUniqueIdentifier().equals(uniqueID)) {
                searchMethodToReturn = testSearchMethod;
            }
        }

        return searchMethodToReturn;
    }

    /**
     * Gets all possible search methods that have been defined.
     *
     * @return a list of all available search methods.
     */
    public static List getSearchMethods() {
        return searchMethods;
    }

    /**
     * Gets the current attribute of the SearchMethod class.
     *
     * @return The search method we should use to search for classes.
     * @see #setCurrent
     */
    public static SearchMethod getCurrent() {
        return getForID(jEdit.getProperty(JEDIT_SEARCH_METHOD_PROPERTY));
    }

    /**
     * Erase any exiting classpath and set the new classpath based on the string
     * being passed to this function. The individual components of the classpath
     * are being separated by looking for File.pathSeparator. If you are doing
     * something strange (like using a UNIX filepath on a windows box) you'll
     * probably have some trouble.
     *
     * @param classpathToSet  a <code>String</code> value containing a number of
     * classpath elements delimited by the path separator of the operating
     * system the code is running on.
     * @see #appendClassPath
     */
    public void setClassPath(String classpathToSet) {
        classPath.clear();

        //Use existing code to prevent duplication
        appendClassPath(classpathToSet);
    }

    /**
     * Gets a string that uniquely identifies this Search Method.  No two search
     * methods can have the same identifier.
     *
     * @return   The propertyID value
     */
    public String getUniqueIdentifier() {
        return id;
    }

    /**
     * Gets the name attribute of the SearchMethod object.
     *
     * @return The name value
     */
    public String getName() {
        return jEdit.getProperty(nameProperty);
    }

    /**
     * Add the classpath items embedded in the parameter to the classpath
     * without erasing the existing classpath.
     *
     * @param classpathToSet  a <code>String</code> value containing a number of
     * classpath elements delimited by the path separator of the operating
     * system the code is running on.
     * @see #setClassPath
     */
    public void appendClassPath(String classpathToSet) {
        //Use the string tokenizer class to separate colon or semicolon delimited
        //Strings.
        StringTokenizer tok = new StringTokenizer(classpathToSet, File.pathSeparator);

        while (tok.hasMoreTokens()) {
            classPath.add(tok.nextToken());
        }
    }

    /**
     * Find a fully qualified classname given the short class name.
     *
     * @param className  The short name of the class that you'd like to find.
     * @return a <code>List</code> value containing Strings of fully-qualified
     * class names.
     */
    public abstract List findFullyQualifiedClassName(String className);

    /**
     * Implemented according to Java spec.
     *
     * @param objToCompare An object that we are trying to determine matches the
     * current instance of <code>SearchMethod</code>.
     * @return a <code>boolean</code> value indicating whether the two objects
     * are equal.
     */
    public boolean equal(Object objToCompare) {
        boolean isEqual = true;

        if (!(objToCompare instanceof SearchMethod)) {
            isEqual = false;
        } else {
            isEqual = (this.getUniqueIdentifier().equals(((SearchMethod) objToCompare).getUniqueIdentifier()));
        }

        return isEqual;
    }
}
