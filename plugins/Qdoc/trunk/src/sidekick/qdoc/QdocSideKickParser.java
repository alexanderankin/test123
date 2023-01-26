/*
 * Copyright (c) 2014, Dale Anson
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the <ORGANIZATION> nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sidekick.qdoc;

import eclipseicons.EclipseIconsPlugin;

import errorlist.DefaultErrorSource;

import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import sidekick.SideKickParsedData;
import sidekick.SideKickParser;
import static sidekick.qdoc.QdocNode.*;
import sidekick.util.*;


public class QdocSideKickParser extends SideKickParser {
    private static final String NAME = "qdoc";

    private View currentView = null;

    public static boolean showAll = false;


    public QdocSideKickParser() {
        super(NAME);
    }

    /**
     * Parse the current buffer in the current view.
     * TODO: is this used anymore?
     */
    public void parse() {
        if (currentView != null) {
            parse(currentView.getBuffer(), null);
        }
    }

    /**
     * Parse the contents of the given buffer.
     *
     * qdoc files are essentially flat, with no enforced tree structure. It is assumed
     * that "\\title" will be the top level, and below title will be section1's with
     * section2's below that and so on. There is nothing in the specification that
     * requires this, so it is a huge assumption to think this will always be the case,
     * but that is what I'm assuming here. The sidekick tree could look really funky
     * if the qdoc author doesn't follow this hierarchy.
     *
     * @param buffer the buffer to parse
     * @param errorSource where to send any error messages
     * @return data for the tree
     */
    public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
        String filename = buffer.getPath();
        SideKickParsedData parsedData = new QdocSideKickParsedData(filename);
        ImageIcon section1Icon = EclipseIconsPlugin.getIcon("action1.gif");
        ImageIcon section2Icon = EclipseIconsPlugin.getIcon("action2.gif");
        ImageIcon section3Icon = EclipseIconsPlugin.getIcon("action3.gif");
        ImageIcon section4Icon = EclipseIconsPlugin.getIcon("activity.gif");
        try {
            if (buffer.getLength() <= 0) {
                return parsedData;
            }
            int lineCount = buffer.getLineCount();
            // end of buffer location/position
            Location eofLocation = new Location(lineCount - 1, buffer.getLineEndOffset(lineCount - 1));
            SideKickPosition eofPosition = new SideKickPosition(buffer.getLineEndOffset(lineCount - 1));
            List<QdocNode> nodes = new ArrayList<QdocNode>();
            Location startLocation;
            SideKickPosition startPosition;
            int length;
            // there should only be one title node, but of course, the qdoc spec
            // doesn't enforce that, and doesn't require that there is a title node.
            QdocNode titleNode = null;

            // parse the buffer
            for (int lineNumber = 0; lineNumber < lineCount; lineNumber++) {
                String lineText = buffer.getLineText(lineNumber);
                int index = lineText.indexOf("\\section");
                length = "\\sectionx".length();

                if (index == -1) {
                    index = lineText.indexOf("\\title");
                    length = "\\title".length();
                }

                if (index > -1) {
                    String section = lineText.substring(index, length);
                    String title = lineText.substring(index + length, lineText.length());
                    startLocation = new Location(lineNumber, index);
                    startPosition = new SideKickPosition(buffer.getLineStartOffset(lineNumber) + index);
                    QdocNode node = new QdocNode(title);
                    node.setStartLocation(startLocation);
                    node.setStartPosition(startPosition);
                    node.setEndLocation(eofLocation);
                    node.setEndPosition(eofPosition);
                    switch (section) {
                        case "\\title":
                            node.setIcon(null);
                            node.setOrdinal(TITLE);
                            titleNode = node;
                            break;
                        case "\\section1":
                            node.setIcon(section1Icon);
                            node.setOrdinal(SECTION1);
                            break;
                        case "\\section2":
                            node.setIcon(section2Icon);
                            node.setOrdinal(SECTION2);
                            break;
                        case "\\section3":
                            node.setIcon(section3Icon);
                            node.setOrdinal(SECTION3);
                            break;
                        case "\\section4":
                            node.setIcon(section4Icon);
                            node.setOrdinal(SECTION4);
                            break;
                    }
                    nodes.add(node);
                }
            }

            // reset the end location/position of the nodes
            for (int i = 0; i < nodes.size(); i++) {
                QdocNode node = nodes.get(i);
                // get the ordinal of the current node
                int ordinal = node.getOrdinal();

                // go down the list to find the next node with the same or smaller ordinal
                for (int j = i + 1; j < nodes.size(); j++) {
                    QdocNode nextNode = nodes.get(j);

                    if (nextNode.getOrdinal() <= ordinal) {
                        // set the end location/position of the current node to be the same as the 
                        // start location/position of the next node.
                        node.setEndLocation(nextNode.getStartLocation());
                        node.setEndPosition(nextNode.getStartPosition());
                        break;
                    }
                }
            }

            // arrange the parent/child relationship of the nodes
            if (nodes.size() > 0) {
                Deque<QdocNode> deck = new ArrayDeque<QdocNode>();
                QdocNode previous1 = null;
                QdocNode previous2 = null;
                QdocNode previous3 = null;

                for ( QdocNode node : nodes) {
                    if (node.getOrdinal() == TITLE) {
                        // there should only be one title and it's already captured
                        continue;
                    }

                    if (node.getOrdinal() == SECTION1) {
                        previous1 = node;
                        deck.add(node);
                        continue;
                    }

                    if (node.getOrdinal() == SECTION2) {
                        if (previous1 != null) {
                            previous1.addChild(node);
                        }
                        else {
                            deck.add(node);
                        }
                        previous2 = node;
                        continue;
                    }

                    if (node.getOrdinal() == SECTION3) {
                        if (previous2 != null) {
                            previous2.addChild(node);
                        }
                        else {
                            deck.add(node);
                        }
                        previous3 = node;
                        continue;
                    }

                    if (node.getOrdinal() == SECTION4) {
                        if (previous3 != null) {
                            previous3.addChild(node);
                        }
                        else {
                            deck.add(node);
                        }
                        continue;
                    }
                }

                if (titleNode == null) {
                    String title = "Untitled";
                    startLocation = new Location(0, 0);
                    startPosition = new SideKickPosition(buffer.getLineStartOffset(0));
                    titleNode = new QdocNode(title);
                    titleNode.setStartLocation(startLocation);
                    titleNode.setStartPosition(startPosition);
                    titleNode.setEndLocation(new Location(0, 0));    // TODO: set end to end of buffer
                    titleNode.setEndPosition(new SideKickPosition(0));
                }
                // make a tree for sidekick out of the nodes
                DefaultMutableTreeNode root = parsedData.root;
                DefaultMutableTreeNode titleTreeNode = new DefaultMutableTreeNode(titleNode);
                root.add(titleTreeNode);

                for ( QdocNode node : deck) {
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
                    titleTreeNode.add(treeNode);

                    if (node.hasChildren()) {
                        addChildNodes(treeNode);
                    }
                }
            }
        }
        catch ( Exception e) {
            e.printStackTrace();
        }
        return parsedData;
    }

    private void addChildNodes(DefaultMutableTreeNode parent) {
        QdocNode node = (QdocNode) parent.getUserObject();

        for ( QdocNode child : node.getChildren()) {
            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(child);
            parent.add(childTreeNode);

            if (child.hasChildren()) {
                addChildNodes(childTreeNode);
            }
        }
    }
}