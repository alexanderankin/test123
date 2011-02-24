/*
 * Copyright (c) 2001-2002, Marco Hunsicker. All rights reserved.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 */
package de.hunsicker.jalopy.plugin.jedit;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.OptionGroup;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.jedit.msg.ViewUpdate;
import org.gjt.sp.jedit.view.message.MessageView;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;
import de.hunsicker.jalopy.language.antlr.JavaNode;
import de.hunsicker.jalopy.plugin.AbstractPlugin;
import de.hunsicker.jalopy.plugin.Project;
import de.hunsicker.jalopy.plugin.StatusBar;
import de.hunsicker.jalopy.plugin.jedit.option.JalopyOptionPane;
import de.hunsicker.jalopy.swing.SettingsDialog;


/**
 * The Jalopy jEdit Plug-in.
 *
 * @author <a href="http://jalopy.sf.net/contact.html">Marco Hunsicker</a>
 * @version $Revision: 1.9 $
 */
public class JEditPlugin
    extends EditPlugin
{
    //~ Static variables/initializers ----------------------------------------------------

    /** Our format menu item. */
    private static JMenuItem _formatItem;

    /** The actual plug-in implementation. */
    private static PluginImpl _instance;

    /** Indicates the Java Buffer mode. */
    private static final String MODE_JAVA = "java" /* NOI18N */;

    //~ Instance variables ---------------------------------------------------------------

    /** The menu which holds our menu items. */
    private JMenu _menu;

    //~ Constructors ---------------------------------------------------------------------

    /**
     * Creates a new jEditPlugin object.
     */
    public JEditPlugin()
    {
    }

    //~ Methods --------------------------------------------------------------------------

    /**
     * Creates the menu items.
     *
     * @param menuItems holds the menus/menu items created so far.
     */
    public void createMenuItems(Vector menuItems)
    {
        _menu = GUIUtilities.loadMenu("jalopy-menu" /* NOI18N */);

        // we store the Format menu item, because we want to be able to enable/disable
        // this item according to the Buffer mode of the view
        _formatItem = _menu.getItem(0);
        menuItems.addElement(_menu);
    }


    /**
     * Displays the Jalopy settings dialog.
     *
     * @param view current view.
     */
    public static synchronized void displaySettingsDialog(View view)
    {
        setInstance(view);

        _instance.statusBar.statusBar = view.getStatus();

        SettingsDialog dialog = SettingsDialog.create(_instance.getMainWindow());
        dialog.pack();
        dialog.setLocationRelativeTo(_instance.getMainWindow());
        dialog.setVisible(true);
    }


    /**
     * Formats the active buffer. Invoked if the user selects the corresponding menu
     * item.
     *
     * @param view current view.
     */
    public static synchronized void formatActive(View view)
    {
        setInstance(view);

        _instance.statusBar.statusBar = view.getStatus();
        _instance.formatActive();
    }


    /**
     * Formats the open buffers. Invoked if the user selects the corresponding menu item.
     *
     * @param view current view.
     */
    public static synchronized void formatOpen(View view)
    {
        setInstance(view);

        _instance.statusBar.statusBar = view.getStatus();
        _instance.formatOpen();
    }


    /**
     * DOCUMENT ME!
     *
     * @param view DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static synchronized JavaNode parse(View view)
    {
        setInstance(view);

        if (view != null)
        {
            String text = view.getTextArea().getText();
            Jalopy jalopy = _instance.getEngine();
            jalopy.setInput(text, view.getBuffer().getPath());

            return jalopy.parse();
        }

        return null;
    }

    private static void setInstance(View view)
    {
        if (_instance == null)
        {
            _instance = new PluginImpl();
            _instance.statusBar = new JEditStatusBar(view.getStatus());
        }
    }
    /**
     * Creates the option panes for the global options dialog.
     *
     * @param dialog global options dialog.
     */
    public void createOptionPanes(OptionsDialog dialog)
    {
        OptionGroup jalopyGroup = new OptionGroup("jalopy" /* NOI18N */);
        jalopyGroup.addOptionPane(new JalopyOptionPane("jalopy.general"/* NOI18N */, "de.hunsicker.jalopy.swing.GeneralSettingsPage"/* NOI18N */));
        jalopyGroup.addOptionPane(new JalopyOptionPane("jalopy.project"/* NOI18N */, "de.hunsicker.jalopy.swing.ProjectSettingsPage"/* NOI18N */));

        OptionGroup printerGroup = new OptionGroup("jalopy.printer" /* NOI18N */);
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.braces"/* NOI18N */, "de.hunsicker.jalopy.swing.BracesSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.whitespace"/* NOI18N */, "de.hunsicker.jalopy.swing.WhitespaceSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.indentation"/* NOI18N */, "de.hunsicker.jalopy.swing.IndentationSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.wrapping"/* NOI18N */, "de.hunsicker.jalopy.swing.WrappingSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.blanklines"/* NOI18N */, "de.hunsicker.jalopy.swing.BlankLinesSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.comments"/* NOI18N */, "de.hunsicker.jalopy.swing.CommentsSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.imports"/* NOI18N */, "de.hunsicker.jalopy.swing.ImportsSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.environment"/* NOI18N */, "de.hunsicker.jalopy.swing.EnvironmentSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.javadoc"/* NOI18N */, "de.hunsicker.jalopy.swing.JavadocSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.header"/* NOI18N */, "de.hunsicker.jalopy.swing.HeaderSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.footer"/* NOI18N */, "de.hunsicker.jalopy.swing.FooterSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.sort"/* NOI18N */, "de.hunsicker.jalopy.swing.SortingSettingsPage"/* NOI18N */));
        printerGroup.addOptionPane(new JalopyOptionPane("jalopy.printer.misc"/* NOI18N */, "de.hunsicker.jalopy.swing.MiscSettingsPage"/* NOI18N */));

        jalopyGroup.addOptionGroup(printerGroup);

        OptionGroup inspectorGroup = new OptionGroup("jalopy.inspector" /* NOI18N */);
        inspectorGroup.addOptionPane(new JalopyOptionPane("jalopy.inspector.general"/* NOI18N */, "de.hunsicker.jalopy.swing.CodeInspectorSettingsPage"/* NOI18N */));
        inspectorGroup.addOptionPane(new JalopyOptionPane("jalopy.inspector.naming"/* NOI18N */, "de.hunsicker.jalopy.swing.NamingSettingsPage"/* NOI18N */));
        jalopyGroup.addOptionGroup(inspectorGroup);

        jalopyGroup.addOptionPane(new JalopyOptionPane("jalopy.messages"/* NOI18N */, "de.hunsicker.jalopy.swing.MessagesSettingsPage"/* NOI18N */));

        dialog.addOptionGroup(jalopyGroup);
    }


    /**
     * Handles a message sent on the EditBus. Updates the state of the Format menu item
     * according to the message content.
     *
     * @param message the message.
     */
    public void handleMessage(EBMessage message)
    {
        if (_formatItem == null)
        {
            return;
        }

        if (message instanceof EditPaneUpdate)
        {
            EditPaneUpdate update = (EditPaneUpdate) message;
            Object what = update.getWhat();

            if (update.getEditPane() != null)
            {
                if (
                    (what == EditPaneUpdate.BUFFER_CHANGED)
                    || (what == EditPaneUpdate.CREATED))
                {
                    Buffer buffer = update.getEditPane().getBuffer();

                    if (isJava(buffer))
                    {
                        if (!_formatItem.isEnabled())
                        {
                            _formatItem.setEnabled(true);
                        }
                    }
                    else
                    {
                        if (_formatItem.isEnabled())
                        {
                            _formatItem.setEnabled(false);
                        }
                    }
                }
            }
        }
        else if (message instanceof ViewUpdate)
        {
            ViewUpdate update = (ViewUpdate) message;
            Object what = update.getWhat();

            if (what == ViewUpdate.CREATED)
            {
                Buffer buffer = update.getView().getBuffer();

                if (isActiveBuffer(buffer))
                {
                    /**
                     * @todo we can't use isJava() because of a bug in jEdit: the mode
                     *       seems to be only set after the message is issued and
                     *       therefore the buffer is in 'text' mode
                     */
                    if (buffer.getName().endsWith(".java" /* NOI18N */))
                    {
                        if (!_formatItem.isEnabled())
                        {
                            _formatItem.setEnabled(true);
                        }
                    }
                    else
                    {
                        if (_formatItem.isEnabled())
                        {
                            _formatItem.setEnabled(false);
                        }
                    }
                }
            }
        }
        else if (message instanceof BufferUpdate)
        {
            BufferUpdate update = (BufferUpdate) message;
            Object what = update.getWhat();

            if (update.getBuffer() != null)
            {
                if (what == BufferUpdate.PROPERTIES_CHANGED)
                {
                    Buffer buffer = update.getBuffer();

                    if ((update.getView() != null) && isActiveBuffer(buffer))
                    {
                        if (isJava(buffer))
                        {
                            if (!_formatItem.isEnabled())
                            {
                                _formatItem.setEnabled(true);
                            }
                        }
                        else
                        {
                            if (_formatItem.isEnabled())
                            {
                                _formatItem.setEnabled(false);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Determines whether the given buffer is the currently active one.
     *
     * @param buffer buffer to have its state checked.
     *
     * @return <code>true</code> if the given buffer is currently active.
     */
    static boolean isActiveBuffer(Buffer buffer)
    {
        /*
        View view = jEdit.getActiveView();

        boolean result = false;

        if (view != null)
        {
            result = buffer == view.getBuffer();
        }

        return result;

        */
        View[] views = jEdit.getViews();

        for (int i = 0; i < views.length; i++)
        {
            if (buffer == views[i].getBuffer())
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Determines whether the given buffer contains Java sources.
     *
     * @param buffer jEdit buffer.
     *
     * @return <code>true</code> if the given buffer contains Java sources.
     */
    private static boolean isJava(Buffer buffer)
    {
        if (
            (!buffer.isReadOnly())
            && buffer.getMode().getName().equalsIgnoreCase(MODE_JAVA))
        {
            return true;
        }

        return false;
    }

    public static Jalopy getJalopyInstance()
    {
        setInstance(jEdit.getActiveView());
        return _instance.getEngine();
    }

    //~ Inner Classes --------------------------------------------------------------------

    /**
     * The actual Plug-in implementation.
     */
    static class PluginImpl
        extends AbstractPlugin
    {
        /** The current status bar. */
        JEditStatusBar statusBar;

        /** The used project. */
        Project project;

        /**
         * Creates a new PluginImpl object.
         */
        public PluginImpl()
        {
            super(new JEditAppender());
        }

        public Project getActiveProject()
        {
            if (this.project == null)
            {
                this.project = new JEditProject();
            }

            return this.project;
        }

        /**
         *  JCH: jEdit internally handles all line endings as \n - it is
         *  only converted during file write to whatever the buffer setting is,
         *  so UNIX format should always be used.
         */
        public FileFormat getFileFormat()
        {

            return FileFormat.UNIX;
        }


        public Frame getMainWindow()
        {
            return jEdit.getActiveView();
        }


        public StatusBar getStatusBar()
        {
            return this.statusBar;
        }


        /**
         * {@inheritDoc}
         */
        public void afterEnd()
        {
            super.afterEnd();

            MessageView.getInstance().update();
        }


        /**
         * Formats the currently active buffer.
         */
        public void formatActive()
        {
            /**
             * @todo maybe this check will become obsolete one day, if jEdit makes use of
             *       the Java Swing Action framework
             */

            // only perform the action if the current Buffer contains a Java source file
            if (isJava(jEdit.getActiveView().getBuffer()))
            {
                performAction(Action.FORMAT_ACTIVE);
            }
        }


        /**
         * Formats the currently open buffers.
         */
        public void formatOpen()
        {
            performAction(Action.FORMAT_OPEN);
        }
    }
}
