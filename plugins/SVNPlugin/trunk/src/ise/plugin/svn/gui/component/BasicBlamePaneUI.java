/*
* Copyright (c) 2008, Dale Anson
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

package ise.plugin.svn.gui.component;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ComponentUI;

public class BasicBlamePaneUI extends BlamePaneUI implements ChangeListener {

    private BlamePane blamePane;
    private BlameRendererPane blameRendererPane;

    public static ComponentUI createUI( JComponent c ) {
        return new BasicBlamePaneUI();
    }

    public void installUI( JComponent c ) {
        blamePane = ( BlamePane ) c;
        blamePane.setLayout( createLayoutManager() );
        installDefaults();
        installComponents();
        installListeners();

    }

    public void uninstallUI( JComponent c ) {
        c.setLayout( null );
        uninstallListeners();
        uninstallComponents();
        uninstallDefaults();

        blamePane = null;
    }

    public void installDefaults() {}

    public void installComponents() {
        blameRendererPane = new BlameRendererPane();
        blamePane.add( blameRendererPane, BorderLayout.CENTER );
    }

    public void installListeners() {
        blamePane.addChangeListener( this );
    }

    public void uninstallDefaults() {}

    public void uninstallComponents() {
        blamePane.remove( blameRendererPane );
        blamePane = null;
    }

    public void uninstallListeners() {
        blamePane.removeChangeListener( this );
    }

    protected LayoutManager createLayoutManager() {
        return new BorderLayout();
    }

    public void stateChanged( ChangeEvent event ) {
        blameRendererPane.repaint();
    }

    public class BlameRendererPane extends JPanel {

        public BlameRendererPane( ) {
            BlameModel model = blamePane.getModel();
            Dimension dim = getPreferredSize();
            if ( model == null ) {
                dim.width = 60;
                setPreferredSize( dim );
            }
            else {
                int max_width = 0;
                FontMetrics fm = model.getTextArea().getPainter().getFontMetrics();
                for ( String line : model.getBlame() ) {
                    int width = fm.stringWidth( line );
                    max_width = width > max_width ? width : max_width;
                }
                dim.width = max_width + 3;
                setPreferredSize( dim );
            }
        }

        public void paintComponent( Graphics gfx ) {
            super.paintComponent( gfx );

            Rectangle size = getBounds();
            gfx.setColor( getBackground() );
            gfx.fillRect( 0, 0, size.width, size.height );

            // get the visible lines, draw the corresponding blame lines
            BlameModel model = blamePane.getModel();
            int pixelsPerLine = model.getTextArea().getPainter().getFontMetrics().getHeight();
            int firstLine = model.getTextArea().getFirstPhysicalLine();
            int lastLine = model.getTextArea().getLastPhysicalLine();
            gfx.setColor( Color.BLACK );
            java.util.List<String> blame = model.getBlame();
            for ( int i = firstLine; i <= lastLine; i++ ) {
                if ( i >= 0 && i < blame.size() ) {
                    gfx.drawString( blame.get( i ), 3, ( i - firstLine + 1 ) * pixelsPerLine );
                }
            }
        }
    }
}