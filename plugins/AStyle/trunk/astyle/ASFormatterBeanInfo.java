/*
 * :tabSize=8:indentSize=4:noTabs=true:maxLineLen=0:
 *
 * Copyright (c) 2001 Dirk Moebius. All rights reserved.
 *
 * ASFormatterBeanInfo.java
 * by Dirk Moebius (dmoebius@gmx.net).
 * This file is a part of "Artistic Style" - an indentater and reformatter
 * of C++, C, and Java source files.
 *
 * The "Artistic Style" project, including all files needed to compile it,
 * is free software; you can redistribute it and/or use it and/or modify it
 * under the terms of EITHER the "Artistic License" OR
 * the GNU Library General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 *  version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of EITHER the "Artistic License" or
 * the GNU Library General Public License along with this program.
 */


package astyle;


import java.beans.*;
import java.lang.reflect.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;


public class ASFormatterBeanInfo extends SimpleBeanInfo {

    public ASFormatterBeanInfo() {
        super();
        propertyDescriptors = new Vector();
        resources = ResourceBundle.getBundle("astyle.ASFormatterBeanInfoTexts");

        // 1) add property descriptions from superclass ASBeautifier:
        addPropertyDescription("blockIndent");
        addPropertyDescription("bracketIndent");
        addPropertyDescription("caseIndent");
        addPropertyDescription("classIndent");
        addPropertyDescription("cStyle");
        addPropertyDescription("emptyLineFill");
        addPropertyDescription("forceTabs");
        addPropertyDescription("labelIndent");
        addPropertyDescription("maxInStatementIndentLength");
        addPropertyDescription("minConditionalIndentLength");
        addPropertyDescription("namespaceIndent");
        addPropertyDescription("preprocessorIndent");
        addPropertyDescription("spaceIndentation");
        addPropertyDescription("switchIndent");
        addPropertyDescription("tabIndentation");
        addPropertyDescription("useTabs");

        // 2) add property descriptions from class ASFormatter:
        addPropertyDescription("bracketFormatMode", BracketFormatModePropertyEditor.class);
        addPropertyDescription("breakBlocksMode");
        addPropertyDescription("breakClosingHeaderBlocksMode");
        addPropertyDescription("breakClosingHeaderBracketsMode");
        addPropertyDescription("breakElseIfsMode");
        addPropertyDescription("breakOneLineBlocksMode");
        addPropertyDescription("operatorPaddingMode");
        addPropertyDescription("parenthesisPaddingMode");
        addPropertyDescription("singleStatementsMode");
        addPropertyDescription("tabSpaceConversionMode");
    }


    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(ASFormatter.class);
    }


    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] pds = new PropertyDescriptor[propertyDescriptors.size()];
        propertyDescriptors.copyInto(pds);
        return pds;
    }


    protected void addPropertyDescription(String name, String displayName, String description, Class propertyEditorClass, Class beanClass) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(name, beanClass);
            pd.setDisplayName(displayName);
            pd.setShortDescription(description);
            if (propertyEditorClass != null)
                pd.setPropertyEditorClass(propertyEditorClass);
            propertyDescriptors.addElement(pd);
        }
        catch (IntrospectionException e) {
            System.err.println(e);
        }
    }


    /**
     * Removes the property description for the specified property name.
     * Does nothing if no property description with the specified name
     * is found.
     */
    protected void removePropertyDescription(final String name) {
        for (int i = propertyDescriptors.size() - 1; i >= 0; --i) {
            PropertyDescriptor pd = (PropertyDescriptor) propertyDescriptors.elementAt(i);
            if (name.equals(pd.getName())) {
                propertyDescriptors.removeElementAt(i);
                return;
            }
        }
    }


    private void addPropertyDescription(String key, Class propertyEditorClass) {
        addPropertyDescription(
            key,
            resources.getString(key + ".label"),
            resources.getString(key + ".description"),
            propertyEditorClass,
            ASFormatter.class
        );
    }


    private void addPropertyDescription(String key) {
        addPropertyDescription(key, null);
    }


    protected Vector propertyDescriptors;
    protected ResourceBundle resources;


    private static final String[] bracketTypeTags = new String[4];


    static {
        bracketTypeTags[ASResource.BREAK_MODE] = "ANSI C/C++";
        bracketTypeTags[ASResource.ATTACH_MODE] = "Java / K&R";
        bracketTypeTags[ASResource.BDAC_MODE] = "Linux";
        bracketTypeTags[ASResource.NONE_MODE] = "None";
    }


    public static class BracketFormatModePropertyEditor extends PropertyEditorSupport {

        public BracketFormatModePropertyEditor() {
            super();
        }


        public String[] getTags() {
            return bracketTypeTags;
        }


        public String getAsText() {
            return bracketTypeTags[((Integer)getValue()).intValue()];
        }


        public void setAsText(String text) {
            for (int i = 0; i < 4; ++i) {
                if (bracketTypeTags[i].equals(text)) {
                    setValue(new Integer(i));
                    return;
                }
            }
            throw new IllegalArgumentException("illegal text value: " + text);
        }

    }

}
