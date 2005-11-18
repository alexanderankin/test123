package logviewer;


import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;


public class LogTypeParser {

    /**
     * Gets the default LogType to use when no other LogTypes are found.
     *
     * @return   The default LogType value
     */
    public LogType getDefaultLogType() {
        LogType defaultType = new LogType("default");
        defaultType.addColumn("Message");
        return defaultType;
    }

    public List parse(InputStream in ) {
        
        // the list of LogTypes to return
        List list = new ArrayList();
        
        // parse the input stream and create a list of LogTypes -- 
        // this is ugly...
        try {

            // read the xml file
            InputSource source = new InputSource(in);
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
                                    
                                    // regular expression to separate log entries
                                    else if ("entry_separator".equals(nodeName))
                                        type.setRowSeparatorRegex(nodeValue);

                                    // row_regex has the regular expression as a CTEXT section,
                                    // group and flags are attributes. Flags are the same flags as used
                                    // in java.util.regex.Pattern, except all are lowercase.
                                    else if ("entry_regex".equals(nodeName)) {
                                        NamedNodeMap regexAttrs = node.getAttributes();
                                        boolean include = true;
                                        int flags = 0;
                                        if (regexAttrs != null && regexAttrs.getLength() > 0) {
                                            Node attrNode = regexAttrs.getNamedItem("include");
                                            String value;
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    include = StringUtils.stringToBoolean(value);
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("case_insensitive");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.CASE_INSENSITIVE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("dotall");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.DOTALL;
                                                }
                                            }
                                            else {
                                                // default for dotall is true
                                                flags += Pattern.DOTALL;   
                                            }
                                            attrNode = regexAttrs.getNamedItem("multiline");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.MULTILINE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("unicode_case");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.UNICODE_CASE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("canon_eq");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.CANON_EQ;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("unix_lines");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.UNIX_LINES;
                                                }
                                            }
                                        }
                                        type.setRowRegex(nodeValue, include, flags);
                                    }

                                    // column_regex has the regular expression as a CTEXT section,
                                    // group and flags are attributes. Flags are the same flags as used
                                    // in java.util.regex.Pattern, except all are lowercase.
                                    else if ("column_regex".equals(nodeName)) {
                                        NamedNodeMap regexAttrs = node.getAttributes();
                                        String groups = null;
                                        int flags = 0;
                                        if (regexAttrs != null && regexAttrs.getLength() > 0) {
                                            Node attrNode = regexAttrs.getNamedItem("groups");
                                            String value;
                                            if (attrNode != null ) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    groups = value;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("case_insensitive");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.CASE_INSENSITIVE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("dotall");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.DOTALL;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("multiline");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.MULTILINE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("unicode_case");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.UNICODE_CASE;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("canon_eq");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
                                                        flags += Pattern.CANON_EQ;
                                                }
                                            }
                                            attrNode = regexAttrs.getNamedItem("unix_lines");
                                            if (attrNode != null) {
                                                value = attrNode.getNodeValue();
                                                if (value != null && value.length() > 0) {
                                                    if (StringUtils.stringToBoolean(value))
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
                                                if (width >= -1 && offset >= 0) {
                                                    type.addColumn(columnName, offset, width);   
                                                }
                                                else if (width >= -1) {
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
                            ///System.out.println(type.toString());
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
}
