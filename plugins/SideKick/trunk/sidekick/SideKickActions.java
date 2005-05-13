/*
 * SideKickActions.java
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2003, 2005 Slava Pestov
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

package sidekick;

//{{{ Import statements
import javax.swing.tree.*;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.*;
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.*;
//}}}

public class SideKickActions
{
        //{{{ keyComplete() method
        public static void keyComplete(View view)
        {
                if(timer != null)
                        timer.stop();

                if(!completeInstant)
                        return;

                complete(view,COMPLETE_INSTANT_KEY);
        } //}}}

        //{{{ keyCompleteWithDelay() method
        public static void keyCompleteWithDelay(final View view)
        {
                if(!completeDelay)
                        return;

                if(timer != null)
                        timer.stop();

                final JEditTextArea textArea = view.getTextArea();

                caretWhenCompleteKeyPressed = textArea.getCaretPosition();

                if(timer == null)
                {
                        timer = new Timer(0,new ActionListener()
                        {
                                public void actionPerformed(ActionEvent evt)
                                {
                                        if(caretWhenCompleteKeyPressed == textArea.getCaretPosition())
                                                complete(view,COMPLETE_DELAY_KEY);
                                }
                        });

                        timer.setInitialDelay(delay);
                        timer.setRepeats(false);
                }

                timer.start();
        } //}}}

        //{{{ complete() method
        public static final int COMPLETE_COMMAND = 0;
        public static final int COMPLETE_DELAY_KEY = 1;
        public static final int COMPLETE_INSTANT_KEY = 2;

        public static void complete(View view, int mode)
        {
                EditPane editPane = view.getEditPane();
                Buffer buffer = editPane.getBuffer();
                JEditTextArea textArea = editPane.getTextArea();

                SideKickParser parser = SideKickPlugin
                        .getParserForBuffer(buffer);
                SideKickParsedData data = SideKickParsedData
                        .getParsedData(view);

                SideKickCompletion complete = null;

                if(buffer.isEditable()
                        && data != null && parser != null
                        && parser.supportsCompletion())
                {
                        complete = parser.complete(editPane,
                                textArea.getCaretPosition());
                }

                if(complete == null || complete.size() == 0)
                {
                        if(mode == COMPLETE_INSTANT_KEY
                                || mode == COMPLETE_DELAY_KEY)
                        {
                                // don't bother user with beep if eg
                                // they press < in XML mode
                                return;
                        }
                        else
                        {
                                view.getToolkit().beep();
                                return;
                        }
                }
                else if(complete.size() == 1)
                {
                        // if user invokes complete explicitly, insert the
                        // completion immediately.
                        //
                        // if the user eg enters </ in XML mode, there will
                        // only be one completion and / is an instant complete
                        // key, so we insert it
                        if(mode == COMPLETE_COMMAND
                                || mode == COMPLETE_INSTANT_KEY)
                        {
                                complete.insert(0);
                                return;
                        }
                }

                // show the popup if
                // - complete has one element and user invoked with delay key
                // - or complete has multiple elements
                // and popup is not already shown because of explicit invocation
                // of the complete action during the trigger delay
                if(popup != null)
                        return;

                popup = new SideKickCompletionPopup(view,parser,
                        textArea.getCaretPosition(), complete)
                {
                        /** forget reference to this popup when it is disposed */
                        public void dispose()
                        {
                                super.dispose();
                                popup = null;
                        }
                };
        } //}}}

        //{{{ selectAsset() method
        public static void selectAsset(View view)
        {
                SideKickParsedData data = SideKickParsedData.getParsedData(view);
                if(data == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                JEditTextArea textArea = view.getTextArea();

                IAsset asset = data.getAssetAtOffset(
                        textArea.getCaretPosition());

                if(asset == null || asset.getEnd() == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                textArea.setCaretPosition(asset.getEnd().getOffset());
                textArea.addToSelection(
                        new Selection.Range(
                                asset.getStart().getOffset(),
                                asset.getEnd().getOffset()));
        } //}}}

        //{{{ narrowToAsset() method
        public static void narrowToAsset(View view)
        {
                SideKickParsedData data = SideKickParsedData.getParsedData(view);
                if(data == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                JEditTextArea textArea = view.getTextArea();

                TreePath path = data.getTreePathForPosition(textArea.getCaretPosition());
                if(path == null)
                {
                        view.getToolkit().beep();
                        return;
                }
                IAsset asset = (IAsset)((DefaultMutableTreeNode)path
                        .getLastPathComponent()).getUserObject();

                if(asset == null || asset.getEnd() == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                textArea.getDisplayManager().narrow(
                        textArea.getLineOfOffset(asset.getStart().getOffset()),
                        textArea.getLineOfOffset(asset.getStart().getOffset()));
        } //}}}

        //{{{ goToPrevAsset() method
        public static void goToPrevAsset(View view)
        {
                SideKickParsedData data = SideKickParsedData.getParsedData(view);
                if(data == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                JEditTextArea textArea = view.getTextArea();

                int caret = textArea.getCaretPosition();
                TreePath path = data.getTreePathForPosition(caret);
                if(path == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        path.getLastPathComponent();

                // see if caret is at the end of a child of the current asset
                for(int i = 0; i < node.getChildCount(); i++)
                {
                        IAsset asset = (IAsset)((DefaultMutableTreeNode)node.getChildAt(i))
                                .getUserObject();
                        if(asset.getEnd() != null && caret == asset.getEnd().getOffset())
                        {
                                textArea.setCaretPosition(asset.getStart().getOffset());
                                return;
                        }
                }

                IAsset asset = ((IAsset)node.getUserObject());
                if(caret == asset.getStart().getOffset())
                {
                        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)
                                node.getParent();
                        for(int i = 0; i < parent.getChildCount(); i++)
                        {
                                if(node == parent.getChildAt(i))
                                {
                                        if(i == 0)
                                        {
                                                if(parent.getUserObject() instanceof IAsset)
                                                {
                                                        textArea.setCaretPosition(
                                                                ((IAsset)parent.getUserObject())
                                                                .getStart().getOffset());
                                                }
                                        }
                                        else
                                        {
                                                IAsset prevAsset = (IAsset)((DefaultMutableTreeNode)
                                                        parent.getChildAt(i - 1)).getUserObject();
                                                if(prevAsset.getEnd() != null)
                                                        textArea.setCaretPosition(prevAsset.getEnd().getOffset());
                                        }
                                        return;
                                }
                        }
                }
                else
                        textArea.setCaretPosition(asset.getStart().getOffset());
        } //}}}

        //{{{ goToNextAsset() method
        public static void goToNextAsset(View view)
        {
                SideKickParsedData data = SideKickParsedData.getParsedData(view);
                if(data == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                JEditTextArea textArea = view.getTextArea();

                int caret = textArea.getCaretPosition();
                TreePath path = data.getTreePathForPosition(caret);
                if(path == null)
                {
                        view.getToolkit().beep();
                        return;
                }

                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        path.getLastPathComponent();

                // see if caret is at the end of a child of the current asset
                for(int i = 0; i < node.getChildCount(); i++)
                {
                        IAsset asset = (IAsset)((DefaultMutableTreeNode)node.getChildAt(i))
                                .getUserObject();
                        if(caret == asset.getEnd().getOffset())
                        {
                                if(i != node.getChildCount() - 1)
                                {
                                        IAsset nextAsset = (IAsset)((DefaultMutableTreeNode)
                                                node.getChildAt(i + 1)).getUserObject();
                                        textArea.setCaretPosition(nextAsset.getStart().getOffset());
                                        return;
                                }
                                else
                                        break;
                        }
                }

                textArea.setCaretPosition(((IAsset)node.getUserObject()).getEnd().getOffset());
        } //}}}

        //{{{ propertiesChanged() method
        public static void propertiesChanged()
        {
                completeDelay = jEdit.getBooleanProperty("sidekick.complete-delay.toggle");
                completeInstant = jEdit.getBooleanProperty("sidekick.complete-instant.toggle");
                delay = jEdit.getIntegerProperty("sidekick.complete-delay",500);
                if(timer != null)
                        timer.setInitialDelay(delay);
        } //}}}

        //{{{ Private members
        private static boolean completeDelay;
        private static boolean completeInstant;
        private static int delay;
        private static int caretWhenCompleteKeyPressed;
        private static Timer timer;
        private static SideKickCompletionPopup popup;
        //}}}
}
