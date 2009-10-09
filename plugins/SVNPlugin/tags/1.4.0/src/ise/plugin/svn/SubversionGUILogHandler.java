/*
Copyright (c) 2007, Dale Anson
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/


package ise.plugin.svn;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import ise.plugin.svn.gui.StopPanel;
import ise.plugin.svn.library.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.RolloverButton;

/**
 * Borrowed from Antelope:
 *
 * A simple log handler for SVNPlugin that shows the SVN output in a GUI.
 * The output can either be in a separate frame, or applications can get
 * the textarea to position themselves.<p>
 * Level.INFO is logged in blue text.<br>
 * Level.WARNING is logged in green text.<br>
 * Level.SEVERE is logged in red text.<br>
 * All other levels are logged in black text.<br>
 *
 * @author    Dale Anson, danson@germane-software.com
 * @created   July 22, 2002
 */
public class SubversionGUILogHandler extends Handler implements Serializable {

    /**
     * The output area.
     */
    private JTextPane _text;

    private boolean _tail = true;

    private JPanel _content_pane;

    private StopPanel stopPanel = null;

    private LinkedList<StyledMessage> messageQueue = new LinkedList<StyledMessage>();

    /**
     * Optional frame
     */
    private JFrame _frame;
    /**
     * Green
     */
    private Color GREEN = new Color( 0, 153, 51 );

    private Color foreground = Color.BLACK;
    /**
     * Current font
     */
    private Font _font = null;

    /**
     * Constructor for the AntelopeGUILogHandler object
     */
    public SubversionGUILogHandler() {
        this( false );
    }

    /**
     * Constructor for the AntelopeGUILogHandler object
     *
     * @param use_frame  If true, will show the output in a separate frame.
     */
    public SubversionGUILogHandler( boolean use_frame ) {
        _content_pane = new JPanel( new BorderLayout() );
        _content_pane.putClientProperty( "isCloseable", Boolean.FALSE );

        _text = new JTextPane();
        _text.setName( "svn console output" );
        try {
            LookAndFeel laf = UIManager.getLookAndFeel();
            if ( laf.getID().equals( "Nimbus" ) ) {
                // stupid hack for Nimbus look and feel where JTextPane and
                // JEditorPane don't honor setBackground.
                _text.setUI( new javax.swing.plaf.basic.BasicEditorPaneUI() );
            }
            _text.setBackground( jEdit.getColorProperty( "view.bgColor" ) );
            foreground = jEdit.getColorProperty( "view.fgColor" );
            _text.setForeground( foreground );
        }
        catch ( Exception e ) {
            _text.setBackground( Color.WHITE );
            _text.setForeground( Color.BLACK );
            foreground = Color.BLACK;
        }
        if ( _font == null ) {
            _font = jEdit.getFirstView().getEditPane().getTextArea().getPainter().getFont();
            _text.setFont( _font );
        }
        _text.setCaretPosition( 0 );
        _content_pane.add( new JScrollPane( _text ), BorderLayout.CENTER );
        JPanel bottom_panel = new JPanel( new BorderLayout() );
        bottom_panel.add( getControlPanel(), BorderLayout.EAST );
        bottom_panel.add( getStopPanel(), BorderLayout.WEST );
        _content_pane.add( bottom_panel, BorderLayout.SOUTH );

        if ( use_frame ) {
            _frame = new JFrame( "Subversion Logger" );
            _frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing( WindowEvent we ) {
                        _frame.setVisible( false );
                    }
                }
            );
            _frame.getContentPane().add( _content_pane );
            _frame.setSize( 600, 400 );
            GUIUtils.centerOnScreen( _frame );
        }
        setFormatter( new LogFormatter() );

        messageProcessor.start();
    }

    /**
     * Description of the Class
     */
    public class LogFormatter extends Formatter {
        /**
         * Description of the Method
         *
         * @param record
         * @return Description of the Returned Value
         */
        public String format( LogRecord record ) {
            return record.getMessage() + System.getProperty( "line.separator" );
        }
    }

    /**
     * Gets the textArea attribute of the AntelopeGUILogHandler object
     *
     * @return   The textArea value
     */
    public JTextComponent getTextComponent() {
        return _text;
    }

    public Document getDocument() {
        return _text.getDocument();
    }

    public JPanel getPanel() {
        return _content_pane;
    }

    /**
     * Sets the font attribute of the AntelopeGUILogHandler object
     *
     * @param font  The new font value
     */
    public void setFont( Font font ) {
        _font = font;
    }

    /**
     * Sets the visible attribute of the optional frame.
     *
     * @param b  The new visible value
     */
    public void setVisible( boolean b ) {
        _frame.setVisible( b );
    }

    /**
     * Disposes of the optional frame.
     */
    public void dispose() {
        _frame.dispose();
    }

    /**
     * Sets the location attribute of the optional frame.
     *
     * @param x  The new location value
     * @param y  The new location value
     */
    public void setLocation( int x, int y ) {
        _frame.setLocation( x, y );
    }

    /**
     * Sets the bounds attribute of the optional frame.
     *
     * @param x  The new bounds value
     * @param y  The new bounds value
     * @param w  The new bounds value
     * @param h  The new bounds value
     */
    public void setBounds( int x, int y, int w, int h ) {
        _frame.setBounds( x, y, w, h );
    }

    /**
     * Gets the size attribute of the optional frame.
     *
     * @return   The size value
     */
    public Dimension getSize() {
        return _frame.getSize();
    }

    /**
     * Finish out the log.
     */
    public void close() {
        if ( getFormatter() != null ) {
            publish( new LogRecord( Level.INFO, getFormatter().getTail( this ) ) );
        }
    }

    /**
     * Does nothing.
     */
    public void flush() {
        if ( getFormatter() != null ) {
            publish( new LogRecord( Level.INFO, getFormatter().getTail( this ) ) );
        }
    }

    /**
     * Starts the log.
     */
    public void open() {
        if ( getFormatter() != null ) {
            int index = _text.getDocument().getLength();
            try {
                _text.getDocument().insertString( index, getFormatter().getHead( SubversionGUILogHandler.this ), null );
            }
            catch ( Exception e ) {     // NOPMD
                //Log.log( e );
            }
        }
    }

    /**
     * Appends the given record to the GUI.
     *
     * @param lr  the LogRecord to write.
     */
    public void publish( final LogRecord lr ) {
        String msg = lr.getMessage();
        if ( msg == null )
            return ;
        if ( getFormatter() != null )
            msg = getFormatter().format( lr );
        if ( _text == null ) {
            return ;
        }
        SimpleAttributeSet set = new SimpleAttributeSet();
        if ( _font == null ) {
            StyleConstants.setFontFamily( set, "Monospaced" );
        }
        else {
            StyleConstants.setFontFamily( set, _font.getFamily() );
            StyleConstants.setBold( set, _font.isBold() );
            StyleConstants.setItalic( set, _font.isItalic() );
            StyleConstants.setFontSize( set, _font.getSize() );
        }
        if ( lr.getLevel().equals( Level.WARNING ) ) {
            StyleConstants.setForeground( set, GREEN );
        }
        else if ( lr.getLevel().equals( Level.SEVERE ) ) {
            StyleConstants.setForeground( set, Color.RED );
        }
        else if ( lr.getLevel().equals( Level.INFO ) ) {
            StyleConstants.setForeground( set, foreground );
        }
        else {
            StyleConstants.setForeground( set, foreground );
        }
        queueMessage( new StyledMessage( msg, set ) );
    }

    private void queueMessage( StyledMessage sm ) {
        messageQueue.add( sm );
    }



    private void processMessage( StyledMessage sm ) {
        try {
            int index = _text.getDocument().getLength();
            int caret_position = _text.getCaretPosition();
            _text.getDocument().insertString( index, sm.message, sm.attributes );
            if ( _tail )
                _text.setCaretPosition( index + sm.message.length() );
            else
                _text.setCaretPosition( caret_position );

        }
        catch ( BadLocationException e ) {
            e.printStackTrace();
        }
    }

    public StopPanel getStopPanel() {
        if ( stopPanel == null ) {
            stopPanel = new StopPanel();
        }
        return stopPanel;
    }

    private JPanel getControlPanel() {
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        final JCheckBox tail_cb = new JCheckBox( "Tail" );
        tail_cb.setSelected( true );
        tail_cb.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        _tail = tail_cb.isSelected();
                        if ( _tail )
                            _text.setCaretPosition( _text.getDocument().getLength() );
                    }
                }
                                 );
        RolloverButton clear_btn = new RolloverButton( GUIUtilities.loadIcon( "Clear.png" ) );
        clear_btn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        _text.selectAll();
                        _text.replaceSelection( "" );
                    }
                }
                                   );
        panel.add( tail_cb );
        panel.add( clear_btn );
        return panel;
    }

    public class StyledMessage {
        public String message;
        public SimpleAttributeSet attributes;

        public StyledMessage( String msg, SimpleAttributeSet set ) {
            message = msg;
            attributes = set;
        }
    }


    Thread messageProcessor = new Thread() {
                public void run() {
                    setPriority( Thread.MIN_PRIORITY );
                    while ( true ) {
                        while ( messageQueue.size() > 0 ) {
                            processMessage( messageQueue.remove() );
                            yield();
                        }
                        try {
                            sleep( 500 );
                        }
                        catch ( InterruptedException e ) {
                            // ignored
                        }
                    }
                }
            };
}