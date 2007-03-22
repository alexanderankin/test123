/*
 * ParserRuleSet.java - A set of parser rules
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 1999 mike dillon
 * Portions copyright (C) 2001 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
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
package code2html.syntax;

import java.util.*;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.syntax.KeywordMap;
import org.gjt.sp.jedit.syntax.ParserRule;
///import org.gjt.sp.jedit.syntax.ParserRuleFactory;
import code2html.Mode;

/**
 *  A set of parser rules.
 *
 * @author     mike dillon
 * @version    $Id$
 * @todo       Use the actual jEdit code
 */
public class ParserRuleSet {
    private byte defaultToken;
    private Segment escapePattern;

    private ParserRule escapeRule;
    private boolean highlightDigits;
    private boolean ignoreCase = true;

    private KeywordMap keywords;
    private Mode mode;

    private String name;
    private Hashtable props;

    private ParserRule[] ruleMapFirst;
    private ParserRule[] ruleMapLast;
    private int terminateChar = -1;  //}}}

    //{{{ Private members
    private final static int RULE_BUCKET_COUNT = 32;
    //{{{ ParserRuleSet constructor

    /**
     *  ParserRuleSet Constructor
     *
     * @param  name
     * @param  mode
     */
    public ParserRuleSet(String name, Mode mode) {
        this.name = name;
        this.mode = mode;
        ruleMapFirst = new ParserRule[RULE_BUCKET_COUNT];
        ruleMapLast = new ParserRule[RULE_BUCKET_COUNT];
    }  //}}}

    //{{{ setDefault() method
    /**
     *  Sets the default of the object
     *
     * @param  def  The new default value
     */
    public void setDefault(byte def) {
        defaultToken = def;
    }  //}}}

    //{{{ setEscape() method
    /**
     *  Sets the escape of the object
     *
     * @param  esc  The new escape value
     */
    public void setEscape(String esc) {
        ///if (esc == null)
        ///{
        ///	escapeRule = null;
        ///}
        ///else
        ///{
        ///	escapeRule = ParserRuleFactory.createEscapeRule(esc);
        ///}
        escapePattern = null;
    }  //}}}

    //{{{ setHighlightDigits() method
    /**
     *  Sets the highlight digits of the object
     *
     * @param  highlightDigits  The new highlight digits value
     */
    public void setHighlightDigits(boolean highlightDigits) {
        this.highlightDigits = highlightDigits;
    }  //}}}

    //{{{ setIgnoreCase() method
    /**
     *  Sets the ignore case of the object
     *
     * @param  b  The new ignore case value
     */
    public void setIgnoreCase(boolean b) {
        ignoreCase = b;
    }  //}}}

    //{{{ setKeywords() method
    /**
     *  Sets the keywords of the object
     *
     * @param  km  The new keywords value
     */
    public void setKeywords(KeywordMap km) {
        keywords = km;
    }  //}}}

    //{{{ setProperties() method
    /**
     *  Sets the properties of the object
     *
     * @param  props  The new properties value
     */
    public void setProperties(Hashtable props) {
        this.props = props;
    }  //}}}

    //{{{ setTerminateChar() method
    /**
     *  Sets the terminate char of the object
     *
     * @param  atChar  The new terminate char value
     */
    public void setTerminateChar(int atChar) {
        terminateChar = (atChar >= 0) ? atChar : -1;
    }  //}}}

    //{{{ getDefault() method
    /**
     *  Gets the default of the object
     *
     * @return    The default value
     */
    public byte getDefault() {
        return defaultToken;
    }  //}}}

    //{{{ getEscapePattern() method
    /**
     *  Gets the escape pattern of the object
     *
     * @return    The escape pattern value
     */
    public Segment getEscapePattern() {
        if (escapePattern == null && escapeRule != null) {
            ///escapePattern = new Segment(escapeRule.searchChars, 0,
            ///	escapeRule.sequenceLengths[0]);
        }
        return escapePattern;
    }  //}}}

    //{{{ getEscapeRule() method
    /**
     *  Gets the escape rule of the object
     *
     * @return    The escape rule value
     */
    public ParserRule getEscapeRule() {
        return escapeRule;
    }  //}}}

    //{{{ getHighlightDigits() method
    /**
     *  Gets the highlight digits of the object
     *
     * @return    The highlight digits value
     */
    public boolean getHighlightDigits() {
        return highlightDigits;
    }  //}}}

    //{{{ getIgnoreCase() method
    /**
     *  Gets the ignore case of the object
     *
     * @return    The ignore case value
     */
    public boolean getIgnoreCase() {
        return ignoreCase;
    }  //}}}

    //{{{ getKeywords() method
    /**
     *  Gets the keywords of the object
     *
     * @return    The keywords value
     */
    public KeywordMap getKeywords() {
        return keywords;
    }  //}}}

    //{{{ getMode() method
    /**
     *  Gets the mode of the object
     *
     * @return    The mode value
     */
    public Mode getMode() {
        return mode;
    }  //}}}

    //{{{ getProperties() method
    /**
     *  Gets the properties of the object
     *
     * @return    The properties value
     */
    public Hashtable getProperties() {
        return props;
    }  //}}}

    //{{{ getRules() method
    /**
     *  Gets the rules of the object
     *
     * @param  ch
     * @return     The rules value
     */
    public ParserRule getRules(char ch) {
        int key = Character.toUpperCase(ch) % RULE_BUCKET_COUNT;
        return ruleMapFirst[key];
    }  //}}}

    //{{{ getTerminateChar() method
    /**
     *  Gets the terminate char of the object
     *
     * @return    The terminate char value
     */
    public int getTerminateChar() {
        return terminateChar;
    }  //}}}

    //{{{ addRule() method
    /**
     *  Adds a feature to the rule of the object
     *
     * @param  r  The feature to be added to the rule
     */
    public void addRule(ParserRule r) {
        ///int key = Character.toUpperCase(r.searchChars[0])
        ///	% RULE_BUCKET_COUNT;
        int key = Character.toUpperCase(r.start[0]) % RULE_BUCKET_COUNT;
        ParserRule last = ruleMapLast[key];
        if (last == null) {
            ruleMapFirst[key] = ruleMapLast[key] = r;
        } else {
            last.next = r;
            ruleMapLast[key] = r;
        }
    }  //}}}

    //{{{ toString() method
    /**
     * @return
     */
    public String toString() {
        return getClass().getName() + "[" + mode.getName() + "::"
             + name + "]";
    }
    //}}}
}

