package org.jedit.plugins.columnruler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.ColorWellButton;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;

/**
 *  Dialog for creating new marks/guides.
 *
 * @author     Brad Mace
 * @version    $Revision: 1.3 $ $Date: 2006-10-08 08:21:53 $
 */
public class MarkDialog extends JDialog implements ActionListener {
    private StaticMark mark;
    private JTextField name;
    private JTextField column;
    private ColorWellButton color;
    private JButton ok;
    private JButton cancel;

    /*
	 * Creates dialog for adding a new mark.
    */
    public MarkDialog( int column ) {
        super( jEdit.getActiveView(), "Add Mark" );
        name = new JTextField(5 );
        this.column = new JTextField( column + "" );
        color = new ColorWellButton( Color.WHITE );
        init();
    }

    /**
     * Creates dialog for editing a mark.
     */
    public MarkDialog( StaticMark m, String title ) {
        super( jEdit.getActiveView(), title, true );
        this.mark = m;
        if ( m == null ) {
            name = new JTextField(5 );
            column = new JTextField(4 );
            color = new ColorWellButton( Color.WHITE );
        } else {
            name = new JTextField( m.getName() );
            column = new JTextField( m.getColumn() + "" );
            color = new ColorWellButton( m.getColor() );
        }
        init();
    }

    private void init() {
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder(11, 11, 11, 11 ) );
        ok = new JButton( "OK" );
        ok.addActionListener( this );
        cancel = new JButton( "Cancel" );
        cancel.addActionListener( this );
        panel.setLayout( new GridLayout(4, 2, 6, 6 ) );
        panel.add( new JLabel( "Name" ) );
        panel.add( name );
        panel.add( new JLabel( "Column" ) );
        panel.add( column );
        panel.add( new JLabel( "Color" ) );
        panel.add( color );
        panel.add( ok );
        panel.add( cancel );
        setContentPane( panel );
        pack();
        ColumnRulerPlugin.center( jEdit.getActiveView(), this );
        setVisible( true );
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == ok ) {
            if ( mark == null ) {
                mark = new StaticMark( name.getText() );
            } else {
                mark.setName( name.getText() );
            }
            mark.setColumn( Integer.parseInt( column.getText() ) );
            mark.setColor( color.getSelectedColor() );
            if ( !MarkManager.getInstance().containsMark( mark ) ) {
                Log.log( Log.DEBUG, this, "Adding mark to MarkContainer" );
                MarkManager.getInstance().addMark( mark );
            }
            TextArea textArea = jEdit.getActiveView().getTextArea();
            if ( ColumnRulerPlugin.getColumnRulerForTextArea( textArea ) != null ) {
                ColumnRulerPlugin.getColumnRulerForTextArea( textArea ).repaint();
            }
            if ( mark.isGuideVisible() ) {
                textArea.repaint();
            }
        }
        MarkManager.getInstance().fireMarksUpdated();
        dispose();
    }
}
