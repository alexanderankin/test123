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
    /** The key of the menu property */
    public final static String MENU = "logviewer.menu";
    /** The prefix for plugin properties */
    public final static String PROPERTY_PREFIX = "logviewer.";

    public final static String SETTING_FILE = "logviewer_types.xml";

    /**
     * Creates menu items
     *
     * @param menuItems  The list of current menu items
     */
    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu(MENU));
    }

    /**
     * Load log file definitions from an xml file.  Each returned LogType
     * describes how a log file should be parsed to be displayed in a JTable.
     *
     * @return   A list of LogTypes.
     */
    public static List getLogTypes() {
        List list = new ArrayList();
        File f = null;
        String basepath = jEdit.getSettingsDirectory();
        try {
            f = new File(basepath, SETTING_FILE);
            if (!f.exists()) {
                basepath = System.getProperty("user.home");
                f = new File(basepath, SETTING_FILE);
                if (!f.exists()) {
                    list.add(getDefaultLogType());
                    System.out.println("using default log type only");
                    return list;
                }
            }
        }
        catch (Exception e) {
            list.add(getDefaultLogType());
            return list;
        }
        // parse the file and create a list of LogTypes -- DOM is so ugly...
        try {

            // read the xml file
            InputSource source = new InputSource(new FileReader(f));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(source);
            Node root = doc.getDocumentElement();
            NodeList childNodes = root.getChildNodes();

            // if no settings in file, use default log type
            if (childNodes == null || childNodes.getLength() == 0) {
                //System.out.println("no settings in file");
                list.add(getDefaultLogType());
                return list;
            }

            // parse the xml file
            for (int x = 0; x < childNodes.getLength(); x++) {
                Node logNode = childNodes.item(x);
                String nodeName = logNode.getNodeName();

                // look for "log" nodes
                if ("log".equals(nodeName)) {
                    // "log" nodes must have a "name" attribute
                    NamedNodeMap logAttrs = logNode.getAttributes();
                    if (logAttrs != null && logAttrs.getLength() > 0) {
                        Node nameNode = logAttrs.getNamedItem("name");
                        if (nameNode != null) {
                            String name = nameNode.getNodeValue();
                            if (name == null || name.length() == 0)
                                continue;

                            // got a valid name, so create the log type
                            LogType type = new LogType(name);
                            list.add(type);

                            // get the settings for this log type
                            NodeList childContents = logNode.getChildNodes();
                            String nodeContents = null;
                            if (childContents != null && childContents.getLength() > 0) {
                                for (int i = 0; i < childContents.getLength(); i++) {
                                    // most settings elements have CTEXT data
                                    Node node = childContents.item(i);
                                    nodeName = node.getNodeName();
                                    String nodeValue = node.getFirstChild() == null ? "" : node.getFirstChild().getNodeValue();
                                    //System.out.println("nodeName = " + nodeName + ", nodeValue = " + nodeValue);
                                    
                                    // handle individual child nodes...
                                    // file_name_glob has a CTEXT section only
                                    if ("file_name_glob".equals(nodeName))
                                        type.setFileNameGlob(nodeValue);

                                    // first_line_glob has a CTEXT section only
                                    else if ("first_line_glob".equals(nodeName))
                                        type.setFirstLineGlob(nodeValue);

                                    // column_regex has the regular expression as a CTEXT section,
                                    // group and flags are attributes. Flags are the same flags as used
                                    // in java.util.regex.Pattern, except all are lowercase.
                                    else if ("column_regex".equals(nodeName)) {
                                        NamedNodeMap regexAttrs = node.getAttributes();
                                        String groups = null;
                                        int flags = 0;
                                        if (regexAttrs != null && regexAttrs.getLength() > 0) {
                                            Node attrNode = regexAttrs.getNamedItem("groups");
                                            String value = attrNode.getNodeValue();
                                            if (value != null && value.length() > 0) {
                                                groups = value;
                                            }
                                            attrNode = regexAttrs.getNamedItem("case_insensitive");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (stringToBoolean(value))
                                                        flags += Pattern.CASE_INSENSITIVE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("dotall");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (stringToBoolean(value))
                                                        flags += Pattern.DOTALL;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("multiline");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (stringToBoolean(value))
                                                        flags += Pattern.MULTILINE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("unicode_case");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (stringToBoolean(value))
                                                        flags += Pattern.UNICODE_CASE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("canon_eq");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (stringToBoolean(value))
                                                        flags += Pattern.CANON_EQ;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("unix_lines");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (stringToBoolean(value))
                                                        flags += Pattern.UNIX_LINES;
                                                }
                                            }
                                        }
                                        type.setColumnRegex(nodeValue, groups, flags);
                                    }

                                    // column_delimiter has a CTEXT section only
                                    else if ("column_delimiter".equals(nodeName))
                                        type.setColumnDelimiter(nodeValue);

                                    // the "columns" element has "column" subelements
                                    else if ("columns".equals(nodeName)) {
                                        NodeList columns = node.getChildNodes();
                                        if (columns != null && columns.getLength() > 0) {
                                            for (int j = 0; j < columns.getLength(); j++) {
                                                Node columnNode = columns.item(j);
                                                String columnName = columnNode.getFirstChild() == null ? "" : columnNode.getFirstChild().getNodeValue();
                                                if (columnName.length() == 0)
                                                    continue;

                                                // "column" elements may have "width"  and "offset" attributes
                                                int width = -1;
                                                int offset = -1;
                                                NamedNodeMap columnAttrs = columnNode.getAttributes();
                                                if (columnAttrs != null && columnAttrs.getLength() > 0) {
                                                    Node attrNode = columnAttrs.getNamedItem("width");
                                                    if (attrNode != null) {
                                                        String value = attrNode.getNodeValue();
                                                        if (value != null && value.length() > 0) {
                                                            try {
                                                                width = Integer.parseInt(value);
                                                                if (width < 0)
                                                                    width = -1;
                                                            }
                                                            catch (NumberFormatException e) {
                                                            }
                                                        }
                                                    }
                                                    attrNode = columnAttrs.getNamedItem("offset");
                                                    if (attrNode != null) {
                                                        String value = attrNode.getNodeValue();
                                                        if (value != null && value.length() > 0) {
                                                            try {
                                                                offset = Integer.parseInt(value);
                                                                if (offset < 0)
                                                                    offset = -1;
                                                            }
                                                            catch (NumberFormatException e) {
                                                            }
                                                        }
                                                    }
                                                    
                                                }
                                                if (width >= 0 && offset >= 0) {
                                                    type.addColumn(columnName, offset, width);   
                                                }
                                                else if (width >= 0) {
                                                    type.addColumn(columnName, width);
                                                }
                                                else {
                                                    type.addColumn(columnName);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            System.out.println(type.toString());
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            list = new ArrayList();
            list.add(getDefaultLogType());
        }
        return list;
    }

    /**
     * Gets the defaultLogType attribute of the LogViewerPlugin class
     *
     * @return   The defaultLogType value
     */
    private static LogType getDefaultLogType() {
        LogType defaultType = new LogType("default");
        defaultType.addColumn("Message");
        return defaultType;
    }

    /**
     * @param b
     * @return   false on these conditions: b is null or b starts with "n" or
     *      "f" or equals "off", regardless of case, true for any other input.
     */
    public static boolean stringToBoolean(String b) {
        if (b == null)
            return false;
        String t = b.toLowerCase();
        if (t.startsWith("n") || t.startsWith("f") || t.equals("off"))
            return false;
        return true;
    }

}

