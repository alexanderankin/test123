/*
* XmlIndenterPlugin.java - EditPlugin implementation for the XML Indenter plugin
*
* Copyright (c) 2003 Robert McKinnon
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
package xml.indent;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.util.Log;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;

import beauty.BeautyPlugin;

/**
 * EditPlugin implementation for the XML Indenter plugin.
 *
 * @author Robert McKinnon - robmckinnon@users.sourceforge.net
 */
public class XmlIndenterPlugin {

    private static final String XSL_TEXT_ELEMENT = "xsl:text";
    private static final String SVG_TEXT_ELEMENT = "text";
    private static final String SVG_TSPAN_ELEMENT = "tspan";

    private static final IndentingTransformerImpl TRANSFORMER = new IndentingTransformerImpl();

    public void start() {
        String modified = jEdit.getProperty( "xmlindenter.preserve-whitespace-element.modified" );
        boolean settingModified = ( modified != null );
        if ( !settingModified ) {
            jEdit.setProperty( "xmlindenter.preserve-whitespace-element.0", XSL_TEXT_ELEMENT );
            jEdit.setProperty( "xmlindenter.preserve-whitespace-element.1", SVG_TEXT_ELEMENT );
            jEdit.setProperty( "xmlindenter.preserve-whitespace-element.2", SVG_TSPAN_ELEMENT );
        }
    }

    /**
     * Displays a user-friendly error message to go with the supplied exception.
     */
    static void processException( Exception e, String message, Component component ) {
        StringWriter writer = new StringWriter();
        e.printStackTrace( new PrintWriter( writer ) );
        Log.log( Log.DEBUG, Thread.currentThread(), writer.toString() );
        String msg = MessageFormat.format( jEdit.getProperty( "xmlindenter.message.error" ),
                new Object[] {message, e.getMessage() } );
        JOptionPane.showMessageDialog( component, msg.toString() );
    }


    static void showMessageDialog( String property, Component component ) {
        String message = jEdit.getProperty( property );
        JOptionPane.showMessageDialog( component, message );
    }

    public static void toggleSplitAttributes( View view ) {
        boolean split = jEdit.getBooleanProperty( "xmlindenter.splitAttributes", false );
        jEdit.setBooleanProperty( "xmlindenter.splitAttributes", !split );
        BeautyPlugin.beautify(view.getBuffer(), view, true);
    }


    /**
     * Indents XML in current buffer.
     * @param view
     */
    public static void indentXml( View view ) {
        Buffer buffer = view.getBuffer();
        boolean indentWithTabs = getIndentWithTabs( buffer );
        int indentAmount = getIndentAmount( indentWithTabs, buffer );

        buffer.writeLock();
        buffer.beginCompoundEdit();

        try {
            // remember the caret position
            int caretPosition = view.getTextArea().getCaretPosition();

            // do the indenting
            String inputString = buffer.getText( 0, buffer.getLength() );
            String resultString = XmlIndenterPlugin.indent( inputString, indentAmount, indentWithTabs );

            // insert the indented text into the buffer
            buffer.remove( 0, buffer.getLength() );
            buffer.insert( 0, resultString );

            // reset caret position as close to where it was as possible
            if ( caretPosition > resultString.length() - 1 ) {
                // old position was beyond the end of the reformatted buffer, so
                // set to end of buffer
                caretPosition = resultString.length() - 1;
            }
            else {
                // make sure caret isn't in the middle of a tag to prevent xml
                // autocomplete from popping up after a reformat
                char c = resultString.charAt( caretPosition );
                while ( caretPosition < buffer.getLength() && !( c == '>' || c == '<' ) ) {
                    caretPosition++;
                    c = resultString.charAt( caretPosition );
                }
                if ( c == '>' ) {
                    caretPosition++;
                }
            }

            // double check caret position is within the bounds of the buffer
            if (caretPosition < 0) {
                caretPosition = 0;
            }
            else if (caretPosition > buffer.getLength() - 1) {
                caretPosition = buffer.getLength() - 1;
            }

            // finally, set the caret position
            view.getTextArea().setCaretPosition( caretPosition );
        }
        catch ( Exception e ) {
            Log.log( Log.ERROR, IndentingTransformerImpl.class, e );
            String message = jEdit.getProperty( "xmlindenter.indent.message.failure" );
            XmlIndenterPlugin.processException( e, message, view );
        }
        finally {
            if ( buffer.insideCompoundEdit() ) {
                buffer.endCompoundEdit();
            }
            buffer.writeUnlock();
        }
    }


    private static boolean getIndentWithTabs( Buffer buffer ) {
        boolean tabSizeAppropriate = buffer.getTabSize() <= buffer.getIndentSize();
        return !buffer.getBooleanProperty( "noTabs" ) && tabSizeAppropriate;
    }


    private static int getIndentAmount( boolean indentWithTabs, Buffer buffer ) {
        if ( indentWithTabs ) {
            return buffer.getIndentSize() / buffer.getTabSize();
        }
        else {
            return buffer.getIndentSize();
        }
    }


    protected static String indent( String inputString, int indentAmount, boolean indentWithTabs ) throws Exception {
        List preserveWhitespaceList = getEnumeratedProperty( "xmlindenter.preserve-whitespace-element" );
        StringWriter writer = new StringWriter();
        TRANSFORMER.indentXml( inputString, writer, indentAmount, indentWithTabs, preserveWhitespaceList );
        String resultString = writer.toString();
        //    return removeIn(resultString, '\r'); //remove '\r' to temporarily fix a bug in the display of results in Windows
        return resultString;
    }

    public static List getEnumeratedProperty( String key ) {
        List<String> values = new ArrayList<String>();
        int i = 0;
        String value;
        while ( ( value = jEdit.getProperty( key + "." + i ) ) != null ) {
            values.add( value );
            i++;
        }
        return values;
    }

}
