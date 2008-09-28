/*
 *  Copyright (C) 2003 Don Brown (mrdon@techie.com)
 *  Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)
 *  This file is part of Log Viewer, a plugin for jEdit (http://www.jedit.org).
 *  It is heavily  based off Follow (http://follow.sf.net).
 *  Log Viewer is free software; you can redistribute it and/or modify
 *  it under the terms of version 2 of the GNU General Public
 *  License as published by the Free Software Foundation.
 *  Log Viewer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with Log Viewer; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package logviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *  Extension of {@link java.util.Properties} which allows one to specify
 *  property values which are Lists of Strings.
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class EnumeratedProperties {

    /**  Delimiter between property name & list member index */
    protected static char delimiter = '.';

    /**
     *  Returns the List value of the property with the supplied key. Note that
     *  one can call getEnumeratedProperty() for a given key successfully if and
     *  only if setEnumeratedProperty() for that key was called some time
     *  beforehand. All members of the list returned will be Strings.
     *
     * @param  key  lookup of the enumerated property to be retrieved.
     * @return      list containing String values
     */
    public List getEnumeratedProperty(String key) {
        List values = new ArrayList();
        int i = 0;
        String value;
        while ((value = LogViewer.getProperty(key + delimiter + i++)) != null) {
            values.add(value);
        }
        return values;
    }

    /**
     *  Assigns the supplied array of String values to the supplied key.
     *
     * @param  key     property lookup
     * @param  values  values to be associated with the property lookup
     */
    public void setEnumeratedProperty(String key, String[] values) {
        int i;
        for (i = 0; i < values.length; i++) {
            LogViewer.setProperty(key + delimiter + i, values[i]);
        }
        while (LogViewer.getProperty(key + delimiter + i) != null) {
            LogViewer.setProperty(key + delimiter + i, null);
            i++;
        }
    }

    /**
     *  Convenience method; equivalent to calling setEnumeratedProperty(key,
     *  (String[])values.toArray(new String[] {}));
     *
     * @param  key     The new enumeratedProperty value
     * @param  values  The new enumeratedProperty value
     */
    public void setEnumeratedProperty(String key, List values) {
        this.setEnumeratedProperty(key, (String[]) values.toArray(new String[]{}));
    }

}

