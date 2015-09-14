/*
* LookAndFeelOptionPane.java - plugin options pane for LookAndFeel plugin
* (c) 2001, 2002 Dirk Moebius
*
* :mode=java:tabSize=4:indentSize=4:noTabs=false:maxLineLen=0:
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
package javassist;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.AbstractOptionPane;

public class OptionPane extends AbstractOptionPane {

    private JCheckBox allowBeep = null;

    public OptionPane() {
        super( "javassist" );
    }

    public void _init() {
        setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6 ) );
        if ( "true".equals( System.getProperty( "LNFAgentInstalled" ) ) ) {
            addComponent( allowBeep = new JCheckBox( jEdit.getProperty( "javassist.allowBeep.label" ), "true".equals( System.getProperty( "allowBeep" ) ) ) );
        } else {
            addComponent( new JLabel( jEdit.getProperty( "javassist.noagent" ) ) );
        }
    }

    public void _save() {
        if ( allowBeep != null ) {
            System.setProperty( "allowBeep", allowBeep.isSelected() ? "true" : "false" );
        }
    }
}