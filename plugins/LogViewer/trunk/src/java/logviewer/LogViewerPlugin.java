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

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.parsers.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.util.Log;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * The LogViewer plugin
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @version   $Revision$
 */
public class LogViewerPlugin extends EditPlugin {
    /** The name of the plugin */
    public final static String NAME = "logviewer";
    /** The prefix for plugin properties */
    public final static String PROPERTY_PREFIX = "logviewer.";

    public final static String SETTING_FILE = "logviewer_types.xml";

    /**
     * Load log file definitions from an xml file.  Each returned LogType
     * describes how a log file should be parsed to be displayed in a JTable.
     *
     * @return   A list of LogTypes.
     */
    public static List getLogTypes() {
        List list = new ArrayList();
        File f = null;
        LogTypeParser parser = new LogTypeParser();
        String basepath = jEdit.getSettingsDirectory();
        try {
            f = new File(basepath, SETTING_FILE);
            if (!f.exists()) {
                basepath = System.getProperty("user.home");
                f = new File(basepath, SETTING_FILE);
                if (!f.exists()) {
                    list.add(parser.getDefaultLogType());
                    return list;
                }
            }
            return parser.parse(new BufferedInputStream(new FileInputStream(f)));
        }
        catch (Exception e) {
            list.add(parser.getDefaultLogType());
            return list;
        }
    }
    
    public void stop() {
        
    }
}

