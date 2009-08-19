/*
* TaskListPlugin.java - TaskList plugin
* Copyright (C) 2001-2003 Oliver Rutherfurd
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
*
* $Id$
*/

package tasklist;

/*{{{ TODOS...
 WON'T DO: need a text area change listener for re-parsing.  Buffer gets reparsed
    on save.  On text area change is too much and hurts performance.
 TODO: ensure task highlights are repainted when buffer reloaded, etc...
 DONE: are there portions of the code which are not thread safe?
 DONE: allow for displaying all buffers or only current ones
 
 DONE: remove all references to TaskListener.  None exist anywhere in this 
 plugin any more.  TaskListModel used to be the only classes that was also
 a TaskListener, but I've removed that implementation.
}}}*/

//{{{ imports
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.List;

import javax.swing.*;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.Token;

import org.gjt.sp.util.Log;
//}}}

/**
 * The main plugin class for the TaskList plugin,
 * conforming to the jEdit Plugin API.
 */
public class TaskListPlugin extends EditPlugin {
    public static boolean DEBUG = false;

    private static Color highlightColor = Color.blue;
    private static boolean allowSingleClickSelection = false;

    //{{{ start() method
    /**
     * Adds TaskHighlights
     */
    public void start() {
        DEBUG = jEdit.getBooleanProperty( "tasklist.debug", false );

        Log.log( Log.DEBUG, this, "adding TaskHighlights" );

        View view = jEdit.getFirstView();
        while ( view != null ) {
            EditPane[] panes = view.getEditPanes();
            for ( int i = 0; i < panes.length; i++ ) {
                JEditTextArea textArea = panes[ i ].getTextArea();
                initTextArea( textArea );
            }
            view = view.getNext();
        }

        propertiesChanged();
    } //}}}

    //{{{ stop() method
    /**
     * Unregister TaskHighlights
     */
    public void stop() {
        Log.log( Log.DEBUG, this, "removing TaskHighlights" );
        View view = jEdit.getFirstView();
        while ( view != null ) {
            EditPane[] panes = view.getEditPanes();
            for ( int i = 0; i < panes.length; i++ ) {
                JEditTextArea textArea = panes[ i ].getTextArea();
                uninitTextArea( textArea );
            }
            view = view.getNext();
        }
    } //}}}

    //{{{ initTextArea() method
    /**
     * Adds TaskHighlights
     */
    private void initTextArea( JEditTextArea textArea ) {
        TaskHighlight highlight = new TaskHighlight( textArea );
        textArea.getPainter().addExtension( highlight );
        textArea.putClientProperty( TaskHighlight.class, highlight );
    } //}}}

    //{{{ uninitTextArea() method
    /**
     * Removes TaskHighlights
     */
    private void uninitTextArea( JEditTextArea textArea ) {
        TaskHighlight highlight = ( TaskHighlight ) textArea.
                getClientProperty( TaskHighlight.class );
        if ( highlight != null ) {
            textArea.getPainter().removeExtension( highlight );
            textArea.putClientProperty( TaskHighlight.class, null );
        }
    } //}}}

    //{{{ toggleHighlights() method
    /**
     * Enables/disables TaskHighlights
     */
    private void toggleHighlights( boolean enabled ) {
        View view = jEdit.getFirstView();
        while ( view != null ) {
            EditPane[] panes = view.getEditPanes();
            for ( int i = 0; i < panes.length; i++ ) {
                JEditTextArea textArea = panes[ i ].getTextArea();
                TaskHighlight highlight = ( TaskHighlight ) textArea
                        .getClientProperty( TaskHighlight.class );
                if ( highlight != null ) {
                    highlight.setEnabled( enabled );
                }
            }
            view = view.getNext();
        }
    } //}}}

    //{{{ getHighlightColor() method
    /**
     * Returns the user-specified color used for highlighting task items
     * in the current buffer.
     * @return a Color representing the highlighting color.
     */
    public static Color getHighlightColor() {
        return highlightColor;
    } //}}}

    //{{{ getAllowSingleClickSelection() method
    /**
    * Returns whether single-clicks will display a task.
    */
    public static boolean getAllowSingleClickSelection() {
        return allowSingleClickSelection;
    } //}}}

    //{{{ addTaskType() method
    /**
     * Adds a TaskType object to the list maintained by the plugin object.
     * @param taskType the TaskType object to be added
     */
    public static void addTaskType( TaskType taskType ) {
        taskTypes.add( taskType );
    } //}}}

    //{{{ loadTaskTypes() method
    /**
     * loads TaskType objects from data maintained in the Properties object
     * currently maintained by the application.
     */
    private static void loadTaskTypes() {
        int i = 0;
        String pattern;
        while ( ( pattern = jEdit.getProperty( "tasklist.tasktype." +
                i + ".pattern" ) ) != null && !pattern.equals( "" ) ) {
            String name = jEdit.getProperty(
                        "tasklist.tasktype." + i + ".name" );
            String iconPath = jEdit.getProperty(
                        "tasklist.tasktype." + i + ".iconpath" );
            String sample = jEdit.getProperty(
                        "tasklist.tasktype." + i + ".sample" );
            boolean ignoreCase = jEdit.getBooleanProperty(
                        "tasklist.tasktype." + i + ".ignorecase" );

            taskTypes.add(
                new TaskType( name, pattern, sample, ignoreCase, iconPath ) );

            i++;
        }
    } //}}}

    //{{{ resetPatterns() method
    /**
     * Clears existing task patterns and reloads default settings
     */
    public static void resetPatterns( View view ) {
        if ( JOptionPane.YES_OPTION == GUIUtilities.confirm( view, "tasklist.reset-query",
                null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE ) ) {
            reloadPatterns();
            GUIUtilities.message( view, "tasklist.reset-complete", null );
            TaskListPlugin.extractTasks( view.getBuffer() );
        }
    } //}}}

    //{{{ reloadPatterns() method
    /**
     * Implements reloading of default task patterns
     */
    private static void reloadPatterns() {
        TaskListPlugin.clearTaskTypes();

        jEdit.setProperty( "tasklist.tasktype.0.name", "DEBUG" );
        jEdit.setProperty( "tasklist.tasktype.0.iconpath", "stock_preferences-16.png" );
        jEdit.setProperty( "tasklist.tasktype.0.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.0.pattern", "\\s(DEBUG)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.0.sample", " DEBUG: [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.1.name", "DONE" );
        jEdit.setProperty( "tasklist.tasktype.1.iconpath", "stock_spellcheck-16.png" );
        jEdit.setProperty( "tasklist.tasktype.1.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.1.pattern", "\\s(DONE)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.1.sample", " DONE: [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.2.name", "FIXME" );
        jEdit.setProperty( "tasklist.tasktype.2.iconpath", "stock_broken_image-16.png" );
        jEdit.setProperty( "tasklist.tasktype.2.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.2.pattern", "\\s(FIXME)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.2.sample", " FIXME: [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.3.name", "IDEA" );
        jEdit.setProperty( "tasklist.tasktype.3.iconpath", "stock_about-16.png" );
        jEdit.setProperty( "tasklist.tasktype.3.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.3.pattern", "\\s(IDEA)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.3.sample", " IDEA: [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.4.name", "NOTE" );
        jEdit.setProperty( "tasklist.tasktype.4.iconpath", "stock_attach-16.png" );
        jEdit.setProperty( "tasklist.tasktype.4.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.4.pattern", "\\s(NOTE)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.4.sample", " NOTE: [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.5.name", "QUESTION" );
        jEdit.setProperty( "tasklist.tasktype.5.iconpath", "stock_help-16.png" );
        jEdit.setProperty( "tasklist.tasktype.5.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.5.pattern", "\\s(QUESTION)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.5.sample", " QUESTION: [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.6.name", "TODO" );
        jEdit.setProperty( "tasklist.tasktype.6.iconpath", "stock_jump-to-16.png" );
        jEdit.setProperty( "tasklist.tasktype.6.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.6.pattern", "\\s(TODO)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.6.sample", " TODO: [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.7.name", "XXX" );
        jEdit.setProperty( "tasklist.tasktype.7.iconpath", "stock_right-16.png" );
        jEdit.setProperty( "tasklist.tasktype.7.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.7.pattern", "\\s(XXX)[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.7.sample", " XXX [comment text]" );

        jEdit.setProperty( "tasklist.tasktype.8.iconpath", "stock_help-16.png" );
        jEdit.setProperty( "tasklist.tasktype.8.ignorecase", "false" );
        jEdit.setProperty( "tasklist.tasktype.8.name", "???" );
        jEdit.setProperty( "tasklist.tasktype.8.pattern", "\\s([?]{3})[:]?\\s+(.+)$" );
        jEdit.setProperty( "tasklist.tasktype.8.sample", " ??? [commented text]" );

        pruneTaskListProperties( 9 );
        loadTaskTypes();
    } //}}}

    //{{{ pruneTaskListProperties() method
    /**
    * Removes all task patterns >= `start`.
    * @param start the first task pattern to remove, all after will
    * also be removed.
    */
    public static void pruneTaskListProperties( int start ) {
        for ( int i = start;
                jEdit.getProperty( "tasklist.tasktype." + i + ".pattern" ) != null;
                i++ ) {
            jEdit.unsetProperty( "tasklist.tasktype." + i + ".name" );
            jEdit.unsetProperty( "tasklist.tasktype." + i + ".iconpath" );
            jEdit.unsetProperty( "tasklist.tasktype." + i + ".ignorecase" );
            jEdit.unsetProperty( "tasklist.tasktype." + i + ".pattern" );
            jEdit.unsetProperty( "tasklist.tasktype." + i + ".sample" );
        }
    } //}}}

    //{{{ propertiesChanged() method
    /**
     * Causes an update of application data, typically after a change
     * in the plugin's user settings.
     */
    private void propertiesChanged() {
        TaskListPlugin.clearTaskTypes();
        TaskListPlugin.loadTaskTypes();
        TaskListPlugin.loadParseModes();

        highlightColor = GUIUtilities.parseColor( jEdit.getProperty(
                    "tasklist.highlight.color" ) );

        allowSingleClickSelection = jEdit.getBooleanProperty(
                    "tasklist.single-click-selection", false );

        boolean highlightEnabled = jEdit.getBooleanProperty( "tasklist.highlight.tasks" );
        toggleHighlights( highlightEnabled );
    } //}}}

    //{{{ loadParseModes() method
    /**
    * Load which modes are to be parsed and which are not to be parsed.
    */
    public static void loadParseModes() {
        parseModes.clear();

        Mode[] modes = jEdit.getModes();
        for ( int i = 0; i < modes.length; i++ ) {
            Boolean parse = Boolean.valueOf(
                        jEdit.getBooleanProperty(
                            "options.tasklist.parse." + modes[ i ].getName(), true ) );

            parseModes.put( modes[ i ].getName(), parse );
        }
    } //}}}

    //{{{ clearTaskTypes() method
    /**
     * Removes all TaskType data from the collection maintained by the plugin.
     */
    public static void clearTaskTypes() {
        taskTypes.clear();
    } //}}}

    //{{{ private static members
    /**
     * A collection of pending requests to parse buffers, maintained to
     * prevent unnecessary duplication.
     */
    private static Set<Buffer> parseRequests = new HashSet<Buffer>();

    /**
     * A collection of TaskType objects representing the form of comments that
     * will be parsed from a buffer and store as Task objects
     */
    private static List<TaskType> taskTypes = new Vector<TaskType>();

    /**
     * A collection of collections: each member represents a collection of
     * Task objects associated with a particular buffer. The plugin uses this
     * object to keep track of Task objects.
     * Key is path for a buffer.
     * Value is a HashMap with key of line number for Task, value is an actual Task.
     * DONE: replace reference to Buffer with String containing path for Buffer.
     */
    private static Map < String, HashMap < Integer, Task >> bufferMap = new HashMap < String, HashMap < Integer, Task >> ();


    /**
     * A collection to track which buffer modes to parse
     * tasks from and which to skip.
     */
    private static HashMap<String, Boolean> parseModes = new HashMap<String, Boolean>();
    //}}}

    //{{{ requestTasksForBuffer() method
    /**
    * Returns the current set of tasks for the buffer requested, if there is a
    * set.  If there is no set, the buffer is parsed.
    * <p>
    * NOTE: This method will not cause a re-parse of a buffer.
    */
    public synchronized static HashMap<Integer, Task> requestTasksForBuffer( final Buffer buffer ) {
        if ( buffer == null || buffer.isLoaded() == false )
            return null;

        HashMap<Integer, Task> taskMap = bufferMap.get( buffer.getPath() );

        // taskMap should only be null if buffer has never been parsed
        if ( taskMap == null ) {
            extractTasks( buffer );
        }

        return taskMap;

    } //}}}

    //{{{ extractTasks() method
    /**
     * Directs the parsing of a buffer for task data if no request for parsing
     * that buffer is currently pending.
     * <p>
     * This method is more effcient than parseBuffer() because it prevents
     * duplicate parse requests.
     * @param buffer the Buffer to be parsed for task data.
     */
    public synchronized static void extractTasks( final Buffer buffer ) {
        if (buffer == null) {
            return;   
        }
        // NOTE: remove this if this method becomes private
        if ( buffer.isLoaded() == false ) {
            return ;
        }
        // if buffer is already in the queue, return
        if ( parseRequests.contains( buffer ) ) {
            return ;
        }

        parseRequests.add( buffer );
        SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        TaskListPlugin.parseBuffer( buffer );
                    }
                }
                                  );
    } //}}}

    //{{{ parseBuffer() method
    /**
     * Parses a Buffer and extracts task item data to be stored in the plugin's
     * collection.
     * @param buffer the Buffer to be parsed
     */
    public synchronized static void parseBuffer( Buffer buffer ) {
        if (buffer == null) {
            return;   
        }
        
        TaskListPlugin.clearTasks( buffer );
        boolean doParse = true;

        // if this file's mode is not to be parsed, skip it
        String mode = buffer.getMode().getName();

        // if mode has been added to jEdit but is not listed
        // in parseModes, then use default (true)
        if ( parseModes.containsKey( mode ) )
            doParse = ( ( Boolean ) parseModes.get( mode ) ).booleanValue();

        if ( !doParse ) {
            // fill with empty HashMap of tasks
            bufferMap.put( buffer.getPath(), new HashMap<Integer, Task>() );

            // remove 'buffer' from parse queue
            parseRequests.remove( buffer );
            return ;
        }

        int firstLine = 0;
        int lastLine = buffer.getLineCount();

        for ( int lineNum = firstLine; lineNum < lastLine; lineNum++ ) {
            int lineStart = buffer.getLineStartOffset( lineNum );

            DefaultTokenHandler tokenHandler = new DefaultTokenHandler();
            buffer.markTokens( lineNum, tokenHandler );
            Token token = tokenHandler.getTokens();
            int tokenStart = lineStart;
            int chunkStart = -1;
            int chunkLength = 0;
            int type = -1;

            while ( token.id != Token.END ) {
                // For 4.2 there are no longer TAB and WHITESPACE tokens
                // but tokens are still broken up by word.
                if ( Token.COMMENT1 <= token.id && token.id <= Token.COMMENT4 ) {
                    type = token.id;
                    chunkStart = tokenStart;
                    chunkLength = token.length;
                    // Ensure the next token is the same as the current
                    // or that the token after the next is the same type.
                    // The second check is to allow detecting tasks
                    // in PHPdoc, where @TODO is a label, so one might have
                    // ` * @TODO foo`.
                    while ( token.next.id == type
                            || ( token.next.id != Token.END
                                 && token.next.id != Token.NULL
                                 && token.next.next.id == type ) ) {
                        token = token.next;
                        chunkLength += token.length;
                    }
                    String text = buffer.getText( chunkStart, chunkLength );

                    // NOTE might want to have task types in an array
                    for ( int i = 0; i < taskTypes.size(); i++ ) {
                        TaskType taskType = taskTypes.get( i );
                        Task task = taskType.extractTask( buffer, text, lineNum, chunkStart - lineStart );
                        if ( task != null ) {
                            TaskListPlugin.addTask( task );
                            break;
                        }
                    }
                }

                tokenStart += token.length;
                token = token.next;
            }

        }

        // after a buffer has been parsed, bufferMap should contain
        // an empty set of tasks, if there are not, not a null set
        // (a null set is used to indicate the buffer has never been parsed)
        if ( bufferMap.get( buffer.getPath() ) == null )
            bufferMap.put( buffer.getPath(), new HashMap<Integer, Task>() );

        if ( TaskListPlugin.DEBUG )
            Log.log( Log.DEBUG, TaskListPlugin.class,
                    "TaskListPlugin.parseBuffer(...) DONE" );

        // remove 'buffer' from parse queue
        parseRequests.remove( buffer );
    } //}}}

    //{{{ addTask() method
    /**
    * Add a Task to the collection maintained by the plugin.
    * @param task the Task to be added.
    */
    private static void addTask( Task task ) {
        if ( TaskListPlugin.DEBUG )
            Log.log( Log.DEBUG, TaskListPlugin.class,
                    "TaskListPlugin.addTask(" + task.toString() + ")" ); //##

        HashMap<Integer, Task> taskMap = bufferMap.get( task.getBufferPath() );

        if ( taskMap == null ) {
            bufferMap.put( task.getBufferPath(), new HashMap<Integer, Task>() );
        }

        Integer _line = Integer.valueOf( task.getLineIndex() );
        if ( taskMap.get( _line ) != null ) {
            Log.log( Log.ERROR, TaskListPlugin.class,
                    "Already a task on line " + task.getLineIndex()
                    + "of buffer: " + task.getBufferPath() ); //##
        }

        taskMap.put( _line, task );
    } //}}}

    //{{{ clearTasks() method
    /**
    * Remove all tasks relating to a given Buffer from the collection
    * of Task objects maintained by the plugin.
    *
    * @param buffer the Buffer whose tasks are to be removed.
    */
    private static void clearTasks( Buffer buffer ) {
        if ( TaskListPlugin.DEBUG )
            Log.log( Log.DEBUG, TaskListPlugin.class,
                    "TaskListPlugin.clearTasks(" + buffer.toString() + ")" ); //##

        HashMap<Integer, Task> taskMap = bufferMap.get( buffer.getPath() );

        if ( taskMap == null ) {
            bufferMap.put( buffer.getPath(), new HashMap<Integer, Task>() );
            return ;
        }
        taskMap.clear();
    } //}}}

    //{{{ removeTask method
    public static void removeTask( View view, Buffer buffer, Task task ) {
        if ( buffer == null || buffer.isReadOnly() || task == null ) {
            view.getToolkit().beep();
            return ;
        }

        // TODO: if whole comment is task, remove comment too
        String text = buffer.getText( task.getStartPosition().getOffset(), task.getText().length() );
        if ( !task.getText().equals( text ) ) {
            GUIUtilities.error( view, "tasklist.buffer-changed", null );
            return ;
        }
        buffer.remove( task.getStartPosition().getOffset(), task.getText().length() );

        // TODO: is this necessary now?
        TaskListPlugin.parseBuffer( buffer );
    } //}}}

    //{{{ removeTaskTag method
    public static void removeTag( View view, Buffer buffer, Task task ) {
        if ( buffer == null || buffer.isReadOnly() || task == null) {
            view.getToolkit().beep();
            return ;
        }

        String text = buffer.getText( task.getStartPosition().getOffset(),
                task.getIdentifier().length() );
        if ( !task.getIdentifier().equals( text ) ) {
            GUIUtilities.error( view, "tasklist.buffer-changed", null );
            return ;
        }
        buffer.remove( task.getStartPosition().getOffset(), task.getIdentifier().length() );

        // TODO: is this necessary now?
        TaskListPlugin.parseBuffer( buffer );
    } //}}}

    //{{{ replaceTaskTag() method
    public static void replaceTag( View view, Buffer buffer, Task task, String newTag ) {
        if ( buffer == null || buffer.isReadOnly() || task == null ) {
            view.getToolkit().beep();
            return ;
        }

        String text = buffer.getText( task.getStartPosition().getOffset(), task.getIdentifier().length() );
        if ( !task.getIdentifier().equals( text ) ) {
            GUIUtilities.error( view, "tasklist.buffer-changed", null );
            return ;
        }
        buffer.beginCompoundEdit();
        buffer.remove( task.getStartPosition().getOffset(), task.getIdentifier().length() );
        buffer.insert( task.getStartPosition().getOffset(), newTag );
        buffer.endCompoundEdit();

        // TODO: is this necessary now?
        TaskListPlugin.parseBuffer( buffer );
    } //}}}
    
    /**
     * Helper method to find the mode for the given file.  This is intended for
     * finding the mode of a temporary buffer.  Actual opened buffers already
     * have a mode.
     * @param bufferPath Full path for a buffer as returned by Buffer.getPath().
     */
    public static Mode getMode( String bufferPath ) {
        try {
            BufferedReader reader = new BufferedReader( new FileReader( bufferPath ) );
            String firstLine = reader.readLine();
            reader.close();
            Mode[] modes = jEdit.getModes();
            for ( Mode mode : modes ) {
                if ( mode.accept( bufferPath, firstLine ) ) {
                    return mode;
                }
            }
        }
        catch ( Exception e ) {}        // NOPMD

        return null;
    }


}