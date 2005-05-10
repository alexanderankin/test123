/*
 * RDocViewer.java -
 *
 * Copyright 2005 Robert McKinnon
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
package org.jedit.ruby.ri;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.DefaultFocusComponent;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import org.gjt.sp.jedit.jEdit;
import org.jedit.ruby.cache.RubyCache;
import org.jedit.ruby.ast.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;
import java.util.List;

/**
 * @author robmckinnon at users.sourceforge.net
 */
public class RDocViewer extends JPanel
        implements DefaultFocusComponent, ListSelectionListener, DocumentListener {

    private static final int MAX_MISMATCHED_CHARACTERS = 3;

    private static final Map<RDocViewer, RDocViewer> viewers = new WeakHashMap<RDocViewer, RDocViewer>();

    private View view;
    private JTextField searchField;
    private JList resultList;
    private JTextPane documentationPane;
    private int mismatchCharacters;
    private RDocViewerKeyHandler keyHandler;
    private JScrollPane documentationScrollPane;

    public RDocViewer(View view, String position) {
        super(new BorderLayout());
        this.view = view;
        mismatchCharacters = 0;
        keyHandler = new RDocViewerKeyHandler(this);
        searchField = initSearchField(keyHandler);
        resultList = initResultList();
        documentationPane = initDocumentationPane();

        JPanel searchPanel = initSearchPanel(searchField, resultList);
        documentationScrollPane = wrapInScrollPane(documentationPane);
        add(initSplitPane(position, searchPanel, documentationScrollPane));
        viewers.put(this, null);

        setListData(RubyCache.instance().getAllImmediateMembers());
    }

    public static void setMethod(Method method) {
        for (RDocViewer viewer : viewers.keySet()) {
            if (viewer.isVisible()) {
                viewer.setMember(method);
            }
        }
    }

    private void setMember(Member member) {
        setListData(RubyCache.instance().getAllImmediateMembers());
        resultList.setSelectedValue(member, true);
        handleSelection();
    }

    private void setListData(final List members) {
        resultList.setModel (
            new AbstractListModel() {
                public int getSize() { return members.size(); }
                public Object getElementAt(int i) { return members.get(i); }
            }
        );
    }

    public void focusOnDefaultComponent() {
        if (!searchField.hasFocus()) {
            searchField.requestFocusInWindow();
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            handleSelection();
        }
    }

    void handleSelection() {
        Member member = (Member)resultList.getSelectedValue();
        if (member != null) {
            String documentation = member.getDocumentation();

            if (documentation.length() == 0) {
                documentation = jEdit.getProperty("ruby.rdoc-viewer.no-description.label");
            }

            documentationPane.setText(documentation);
            documentationPane.setCaretPosition(0);
        }
    }

    private JTextField initSearchField(RDocViewerKeyHandler keyHandler) {
        final JTextField textField = new JTextField();
        textField.addKeyListener(keyHandler);
        textField.getDocument().addDocumentListener(this);
        configureAppearence(textField);
        textField.setCaretColor(textField.getForeground());
        return textField;
    }

    public void insertUpdate(DocumentEvent e) {
        handleSearchTermEntered();
    }

    public void removeUpdate(DocumentEvent e) {
        handleSearchTermEntered();
    }

    public void changedUpdate(DocumentEvent e) {
        handleSearchTermEntered();
    }

    private void handleSearchTermEntered() {
        if (searchField != null) {
            String text = searchField.getText();
            boolean noTerm = text.length() == 0;
            final List<Member> members = noTerm ? RubyCache.instance().getAllImmediateMembers() : getMatchingMembers(text);

            if (members.size() == 0) {
                if(!keyHandler.lastWasBackspace()
                    && mismatchCharacters < MAX_MISMATCHED_CHARACTERS) {
                    mismatchCharacters++;
                }
                searchField.setForeground(Color.red);
            } else {
                setListData(members);
                if (searchField.getForeground() == Color.red) {
                    searchField.setForeground(resultList.getForeground());
                }
            }

            setSelected(0);
        }
    }

    private List<Member> getMatchingMembers(String text) {
        boolean matchLength = false;
        if (text.length() > 0) {
            matchLength = Character.isSpaceChar(text.charAt(text.length() - 1));
        }
        text = text.toLowerCase().trim();

        int dotIndex = text.indexOf('.');
        String instanceMethod = "";
        String classMethod = "";
        boolean containsDot = dotIndex != -1;

        if (containsDot) {
            boolean dotAtEnd = (dotIndex == text.length() - 1);
            String start = text.substring(0, dotIndex);
            String end = dotAtEnd ? "" : text.substring(dotIndex + 1);
            instanceMethod = start + '#' + end;
            classMethod = start + "::" + end;
        }

        List<Member> members = new ArrayList<Member>();
        if (containsDot) {
            populateMatches(instanceMethod, classMethod, matchLength, members);
        } else {
            populateMatches(text, matchLength, members);
        }
        return members;
    }

    private void populateMatches(String text, boolean matchLength, List<Member> members) {
        for (Member member : RubyCache.instance().getAllImmediateMembers()) {
            if (isMatch(member.getFullName(), text, matchLength)) {
                members.add(member);

            } else if (isMatch(member.getName(), text, matchLength)) {
                members.add(member);
            }
        }
    }

    private void populateMatches(final String instanceMethod, final String classMethod, final boolean matchLength, final List<Member> members) {
        for (Member member : RubyCache.instance().getAllImmediateMembers()) {
            member.accept(new MemberVisitorAdapter() {
                public void handleMethod(Method method) {
                    String text = method.isClassMethod() ? classMethod : instanceMethod;
                    if (isMatch(method.getFullName(), text, matchLength)) {
                        members.add(method);
                    }
                }
            });
        }
    }

    private boolean isMatch(String memberName, String text, boolean matchLength) {
        boolean matched = false;

        if (memberName.toLowerCase().startsWith(text)) {
            if (!matchLength) {
                matched = true;
            } else if (memberName.length() == text.length()) {
                matched = true;
            }
        }

        return matched;
    }

    private JList initResultList() {
        JList list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                searchField.requestFocusInWindow();
            }
        });
        configureAppearence(list);
        return list;
    }

    private JTextPane initDocumentationPane() {
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.setStyleSheet(new RDocStyleSheet(view));
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setEditorKit(kit);
        configureAppearence(pane);
        return pane;
    }

    private void configureAppearence(JComponent component) {
        component.setFont(jEdit.getFontProperty("view.font"));
        component.setBackground(jEdit.getColorProperty("view.bgColor"));
        component.setForeground(jEdit.getColorProperty("view.fgColor"));
    }

    private JScrollPane wrapInScrollPane(JComponent component) {
        return new JScrollPane(component, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    private JPanel initSearchPanel(JTextField searchField, JList resultList) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(searchField, BorderLayout.NORTH);
        panel.add(wrapInScrollPane(resultList));
        Dimension size = panel.getPreferredSize();
        size.width = 200;
        panel.setPreferredSize(size);
        panel.setMinimumSize(size);
        return panel;
    }

    private static JSplitPane initSplitPane(String position, JPanel searchPanel, JScrollPane documentationPanel) {
        boolean useHorizontalLayout = position.equals(DockableWindowManager.TOP)
                || position.equals(DockableWindowManager.BOTTOM)
                || position.equals(DockableWindowManager.FLOATING);

        if (useHorizontalLayout) {
            return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, searchPanel, documentationPanel);
        } else {
            return new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchPanel, documentationPanel);
        }
    }

    public int getVisibleRowCount() {
        return 8;
    }

    public boolean handleAltPressedWith(char keyChar) {
        return false;
    }

    public void handleBackSpacePressed() {
        if (mismatchCharacters > 0) {
            mismatchCharacters--;
        }
    }

    public int getListSize() {
        return resultList.getModel().getSize();
    }

    public void incrementSelection(int increment) {
        int index = resultList.getSelectionModel().getLeadSelectionIndex();
        int size = resultList.getModel().getSize();

        index += increment;
        index = index < 0 ? 0 : index;
        index = index >= size ? size - 1 : index;

        setSelected(index);
    }

    private void setSelected(int index) {
        Object item = resultList.getModel().getElementAt(index);
        resultList.setSelectedValue(item, true);
    }

    public void handleEscapePressed() {
        view.getTextArea().requestFocus();
    }

    public boolean consumeKeyEvent(char typed) {
        String selectedChars = searchField.getSelectedText();

        if (mismatchCharacters == MAX_MISMATCHED_CHARACTERS) {
            if (selectedChars != null && selectedChars.length() >= mismatchCharacters) {
                mismatchCharacters -= (selectedChars.length());
                mismatchCharacters = mismatchCharacters < 0 ? 0 : mismatchCharacters;
                return false;
            } else {
                return true;
            }
        } else if (selectedChars != null && selectedChars.endsWith(" ") && typed == ' ') {
            return true;
        } else if (typed == '\t') {
            documentationPane.requestFocusInWindow();
            return true;
        } else {
            return false;
        }
    }

    private int getBlockIncrement() {
        return documentationPane.getVisibleRect().height;
    }

    public void pageDown() {
        moveScrollBar(getBlockIncrement());
    }

    public void pageUp() {
        moveScrollBar(-1 * getBlockIncrement());
    }

    public void home() {
        moveScrollBar(Integer.MIN_VALUE);
    }

    public void end() {
        moveScrollBar(Integer.MAX_VALUE);
    }

    private void moveScrollBar(int blockIncrement) {
        JScrollBar scrollBar = documentationScrollPane.getVerticalScrollBar();
        if (scrollBar != null) {
            if (blockIncrement == Integer.MAX_VALUE) {
                scrollBar.setValue(scrollBar.getMaximum());
            } else {
                scrollBar.setValue(scrollBar.getValue() + blockIncrement);
            }
        }
    }

}
