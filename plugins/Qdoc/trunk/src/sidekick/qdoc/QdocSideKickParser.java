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
     * @param buffer the buffer to parse
     * @param errorSource where to send any error messages
     * @return data for the tree
     */
    public SideKickParsedData parse(Buffer buffer, DefaultErrorSource errorSource) {
        String filename = buffer.getPath();
        SideKickParsedData parsedData = new QdocSideKickParsedData(filename);
        DefaultMutableTreeNode root = parsedData.root;
        ImageIcon section1Icon = EclipseIconsPlugin.getIcon("action1.gif");
        ImageIcon section2Icon = EclipseIconsPlugin.getIcon("action2.gif");
        ImageIcon section3Icon = EclipseIconsPlugin.getIcon("action3.gif");
        ImageIcon section4Icon = EclipseIconsPlugin.getIcon("activity.gif");
        try {
            if (buffer.getLength() <= 0) {
                return parsedData;
            }
            // parse the buffer
            int lineCount = buffer.getLineCount();
            List<QdocNode> nodes = new ArrayList<QdocNode>();
            Location startLocation;
            SideKickPosition startPosition;

            for (int lineNumber = 0; lineNumber < lineCount; lineNumber++) {
                String lineText = buffer.getLineText(lineNumber);
                int index = lineText.indexOf("\\section");

                if (index > -1) {
                    String section = lineText.substring(index, "\\sectionX".length());
                    String title = lineText.substring(index + "\\sectionX".length(), lineText.length());
                    startLocation = new Location(lineNumber, index);
                    startPosition = new SideKickPosition(buffer.getLineStartOffset(lineNumber) + index);
                    QdocNode node = new QdocNode(title);
                    node.setStartLocation(startLocation);
                    node.setStartPosition(startPosition);
                    node.setEndLocation(new Location(lineNumber, buffer.getLineEndOffset(lineNumber)));
                    node.setEndPosition(new SideKickPosition(buffer.getLineEndOffset(lineNumber)));
                    switch (section) {
                        case "\\section1":
                            node.setIcon(section1Icon);
                            break;
                        case "\\section2":
                            node.setIcon(section2Icon);
                            break;
                        case "\\section3":
                            node.setIcon(section3Icon);
                            break;
                        case "\\section4":
                            node.setIcon(section4Icon);
                            break;
                    }
                    nodes.add(node);
                }
            }

            if (nodes.size() > 0) {
                DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Qdoc");
                root.add(treeNode);

                for ( QdocNode node : nodes) {
                    treeNode.add(new DefaultMutableTreeNode(node));
                }
            }
        }
        catch ( Exception e) {
            e.printStackTrace();
        }
        return parsedData;
    }
}