/*
Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)
This file is part of Follow (http://follow.sf.net).
Follow is free software; you can redistribute it and/or modify
it under the terms of version 2 of the GNU General Public
License as published by the Free Software Foundation.
Follow is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with Follow; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package ghm.follow;

import java.util.*;
import java.util.regex.*;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import logviewer.LogType;


/**
 * Implementation of {@link OutputDestination} which appends Strings to a {@link
 * JTextArea}.
 *
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 * @version   $Revision$
 * @see       OutputDestination
 * @see       JTextArea
 */
public class JTextAreaDestination extends OutputDestinationComponent {

    private boolean wrapFind = false;
    
    private LogType logType = null;

    /**
     * Construct a new JTextAreaDestination.
     *
     * @param jTextArea          text will be appended to this text area
     * @param autoPositionCaret  if true, caret will be automatically moved to
     *      the bottom of the text area when text is appended
     */
    public JTextAreaDestination(JTextArea jTextArea, boolean autoPositionCaret) {
        this(jTextArea, autoPositionCaret, null);
    }
    
    public JTextAreaDestination(JTextArea jTextArea, boolean autoPositionCaret, LogType logType) {
        jTextArea_ = jTextArea;
        autoPositionCaret_ = autoPositionCaret;
        this.logType = logType;
    }

    /**
     * Gets the jTextArea attribute of the JTextAreaDestination object
     *
     * @return   The jTextArea value
     */
    public JTextArea getJTextArea() {
        return jTextArea_;
    }

    /**
     * Sets the jTextArea attribute of the JTextAreaDestination object
     *
     * @param jTextArea  The new jTextArea value
     */
    public void setJTextArea(JTextArea jTextArea) {
        jTextArea_ = jTextArea;
    }

    /**
     * @return   whether caret will be automatically moved to the bottom of the
     *      text area when text is appended
     */
    public boolean autoPositionCaret() {
        return autoPositionCaret_;
    }

    /**
     * @param autoPositionCaret  if true, caret will be automatically moved to
     *      the bottom of the text area when text is appended
     */
    public void setAutoPositionCaret(boolean autoPositionCaret) {
        autoPositionCaret_ = autoPositionCaret;
    }
    
    public void toggleAutoPositionCaret() {
        autoPositionCaret_ = !autoPositionCaret_;   
    }

    /** Description of the Method */
    public void toggleWordWrap() {
        jTextArea_.setWrapStyleWord(!jTextArea_.getWrapStyleWord());
        jTextArea_.setLineWrap(!jTextArea_.getLineWrap());
    }

    /**
     * Gets the wordWrap attribute of the JTextAreaDestination object
     *
     * @return   The wordWrap value
     */
    public boolean getWordWrap() {
        return jTextArea_.getLineWrap();
    }

    /**
     * Sets the wordWrap attribute of the JTextAreaDestination object
     *
     * @param b  The new wordWrap value
     */
    public void setWordWrap(boolean b) {
        jTextArea_.setWrapStyleWord(b);
        jTextArea_.setLineWrap(b);

    }

    /**
     * Description of the Method
     *
     * @param toFind
     */
    public void find(String toFind) {
        if (toFind == null || toFind.length() == 0) {
            return;
        }
        try {
            String doc = jTextArea_.getDocument().getText(0, jTextArea_.getDocument().getLength());
            Pattern pattern = Pattern.compile(toFind, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(doc);
            if (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                jTextArea_.setCaretPosition(start);
                jTextArea_.select(start, end);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description of the Method
     *
     * @param toFind
     */
    public void findNext(String toFind) {
        if (toFind == null || toFind.length() == 0) {
            return;
        }
        try {
            int initial_caret = jTextArea_.getCaretPosition();
            int caret = initial_caret;
            String doc = jTextArea_.getDocument().getText(0, jTextArea_.getDocument().getLength());
            Pattern pattern = Pattern.compile(toFind, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(doc);
            if (matcher.find()) {
                boolean done = false;
                while (!done) {
                    matcher = pattern.matcher(doc);
                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                        if (start < caret) {
                            continue;
                        }
                        caret = end;
                        jTextArea_.setCaretPosition(start);
                        jTextArea_.select(start, end);
                        done = true;
                        break;
                    }
                    if (wrapFind) {
                        caret = 0;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the wrapFind attribute of the JTextAreaDestination object
     *
     * @param wrap  The new wrapFind value
     */
    public void setWrapFind(boolean wrap) {
        wrapFind = wrap;
    }

    /** Description of the Method */
    public void clear() {
        jTextArea_.setText("");
    }
    
    public void print( String[] s ) {
        for ( int i = 0; i < s.length; i++ ) {
            print( s[i] );
        }
    }

    /**
     * Description of the Method
     *
     * @param s
     */
    public void print(String s) {
        
        jTextArea_.append(s);
        if (autoPositionCaret_) {
            jTextArea_.setCaretPosition(jTextArea_.getDocument().getLength());
        }
    }
    
	/**
	 * @deprecated    
	 */
    private String removeRowsByRegex(String s) {
        if (logType == null)
            return s;
        String regex = logType.getRowRegex();
        if (regex == null)
            return s;
        
        boolean include = logType.getRowInclude();
        int flags = logType.getRowFlags();
        Pattern p = Pattern.compile(regex, flags);
        Matcher m = p.matcher(s);
        if (m.matches() && include) {
            return s;
        }
        else if (!m.matches() && !include) {
            return s;
        }
        return "";
    }

    protected JTextArea jTextArea_;
    protected boolean autoPositionCaret_;

}

