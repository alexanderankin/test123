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
import java.util.Vector;


public class ASFormatterBeanInfo extends SimpleBeanInfo {

    public ASFormatterBeanInfo() {
        super();
        propertyDescriptors = new Vector();

        // 1) add property descriptions from superclass ASBeautifier:

        addPropertyDescription(
            "useTabs",
            "Use Tabs",
            "Whether code should be indented using tabs or spaces."
        );

        addPropertyDescription(
            "tabIndentation",
            "Tab width",
            "Assume each tab is this number of spaces long."
        );

        addPropertyDescription(
            "spaceIndentation",
            "Space indent",
            "Number of spaces to indent with."
        );

        addPropertyDescription(
            "maxInStatementIndentLength",
            "Max. multi-line statement indent",
            "Maximum indentation between two lines in a multi-line statement."
        );

        addPropertyDescription(
            "minConditionalIndentLength",
            "Min. multi-line statement indent",
            "Minumum indentation between two lines in a multi-line condition."
        );

        addPropertyDescription(
            "classIndent",
            "Indent classes",
            "Whether class definitions should be indented."
        );

        addPropertyDescription(
            "switchIndent",
            "Indent switch blocks",
            "Whether switch blocks should be indented."
        );

        addPropertyDescription(
            "caseIndent",
            "Indent case statements",
            "Whether case statements should be indented."
        );

        addPropertyDescription(
            "bracketIndent",
            "Indent brackets",
            "Whether brackets should be indented."
        );

        addPropertyDescription(
            "blockIndent",
            "Indent blocks",
            "If checked, entire blocks will be indented one additional indent."
        );

        addPropertyDescription(
            "namespaceIndent",
            "Indent namespaces",
            "Whether namespace blocks should be indented (C++ only)."
        );

        addPropertyDescription(
            "labelIndent",
            "Indent labels",
            "If checked, labels will be indented one indent LESS than " +
            "the current indentation level."
        );

        addPropertyDescription(
            "emptyLineFill",
            "Fill empty lines",
            "Whether empty lines should be filled with whitespace."
        );

        addPropertyDescription(
            "preprocessorIndent",
            "Indent preprocessor lines",
            "Whether multiline #define statements should be indented."
        );

        addPropertyDescription(
            "cStyle",
            "C/C++ file",
            "Set this to true, if you want to beautify a C/C++ file; " +
            "false, if you want to beautify a Java file. " +
            "If true, the beautifier performs additional indenting on " +
            "on templates and precompiler instructions, among other " +
            "things."
        );

        // 2) add property descriptions from class ASFormatter:

        addPropertyDescription(
            "bracketFormatMode",
            "Bracket format mode",
            "Choose between ANSI C/C++ style, Java/K&R style or Linux/GNU " +
            "style bracket placement or no formatting of brackets.",
            BracketFormatModePropertyEditor.class
        );

        addPropertyDescription(
            "breakClosingHeaderBracketsMode",
            "Break before closing headers",
            "If true, brackets just before closing headers (e.g. 'else', " +
            "'catch') will be broken, even if standard brackets are " +
            "attached. Otherwise, closing header brackets will be treated " +
            "as standard brackets."
        );

        addPropertyDescription(
            "breakElseIfsMode",
            "Break 'else if'",
            "Whether 'else' should be broken from their succeeding 'if'."
        );

        addPropertyDescription(
            "operatorPaddingMode",
            "Pad operators",
            "Whether operators should be padded with whitespace."
        );

        addPropertyDescription(
            "parenthesisPaddingMode",
            "Pad parenthesis",
            "Whether parenthesis should be padded with whitespace."
        );

        addPropertyDescription(
            "breakOneLineBlocksMode",
            "Break one-line blocks",
            "Whether there should be a break before one-line blocks."
        );

        addPropertyDescription(
            "singleStatementsMode",
            "Break multiple statement lines",
            "Whether lines consisting of multiple statements should be " +
            "breaked into single statement lines."
        );

        addPropertyDescription(
            "tabSpaceConversionMode",
            "Convert tabs to spaces",
            "Whether tabs should be converted to spaces."
        );

        addPropertyDescription(
            "breakBlocksMode",
            "Separate unrelated blocks",
            "Whether unrelated blocks of code should be separated with " +
            "empty lines."
        );

        addPropertyDescription(
            "breakClosingHeaderBlocksMode",
            "Separate 'else'/'catch' blocks",
            "Whether closing header blocks such as 'else', 'catch', etc. " +
            "should be separated with empty lines."
        );

        // Note: the property "CStyle" is not settable via this BeanInfo,
        // because it is set automatically by AStyle.
    }


    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(ASFormatter.class);
    }


    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] pds = new PropertyDescriptor[propertyDescriptors.size()];
        propertyDescriptors.copyInto(pds);
        return pds;
    }


    private void addPropertyDescription(String name, String displayName, String description) {
        addPropertyDescription(name, displayName, description, null, ASFormatter.class);
    }


    private void addPropertyDescription(String name, String displayName, String description, Class propertyEditorClass) {
        addPropertyDescription(name, displayName, description, propertyEditorClass, ASFormatter.class);
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


    protected Vector propertyDescriptors;


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


    private static final String[] bracketTypeTags = new String[4];


    static {
        bracketTypeTags[ASResource.BREAK_MODE] = "Gnu / ANSI C/C++";
        bracketTypeTags[ASResource.ATTACH_MODE] = "Java / K&R";
        bracketTypeTags[ASResource.BDAC_MODE] = "Linux";
        bracketTypeTags[ASResource.NONE_MODE] = "None";
    }

}
