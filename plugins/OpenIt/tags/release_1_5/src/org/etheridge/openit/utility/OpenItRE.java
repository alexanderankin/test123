/*
 * OpenIt jEdit Plugin (OpenItRE.java) 
 *  
 * Copyright (C) 2004 Matt Etheridge (matt@etheridge.org)
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

package org.etheridge.openit.utility;

import java.util.regex.*;

import org.gjt.sp.util.StandardUtilities;
import org.gjt.sp.util.Log;

/**
 * Encapsulation of the GNU RegularExpression class that can ignore case if
 * required.
 */
public class OpenItRE {
        private Pattern mRegularExpression;
        private boolean mCaseSensitive;
        
        /**
         * Constructor - defaults to being case sensitive.
         *
         * @param reString the string to use to create the regular expression
         */
        public OpenItRE(String reString) {
                this(reString, true);
        }
        
        /**
         * Constructor - allows choice of case sensitivity.
         *
         * @param reString the string to use to create the regular expression
         */
        public OpenItRE(String reString, boolean caseSensitive) {
                mCaseSensitive = caseSensitive;
                
                if (reString != null) {
                        try {
                                if (!mCaseSensitive) {
                                        // if the matching is NOT case sensitive then make the restring lowercase
                                        reString = reString.toLowerCase();
                                }
                                mRegularExpression = Pattern.compile(StandardUtilities.globToRE(reString));
                                
                        } catch (Exception e) {
                                Log.log(Log.MESSAGE, OpenItRE.class, 
                                        "[OpenIt Plugin]: Invalid excludes regular expression: " + reString);
                        }
                }
        }
        
        public boolean isMatch(String stringToMatch) {
                // if the regular expression has not been set, then do not filter
                if (mRegularExpression == null) {
                        return true;
                }
                
                // if this should NOT be case sensitive, then ensure the matching string
                // is converted to lower case.
                if (!mCaseSensitive) {
                        stringToMatch = stringToMatch.toLowerCase();
                }
                
                // if the element string matches the regular expression, then return false
                // return mRegularExpression.isMatch(stringToMatch);
                Matcher matcher = mRegularExpression.matcher(stringToMatch);
                return matcher.matches();
        }
        
}
