package jdiff.component.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import jdiff.MergeControl;
import jdiff.component.*;

public class BasicDiffLineOverviewUI extends DiffLineOverviewUI {

    private DiffLineOverview diffLineOverview = null;
    private MergeControl mergeControl = null;
    private LineRendererPane lineRendererPane = null;

    public static ComponentUI createUI( JComponent c ) {
        return new BasicDiffLineOverviewUI();
    }

    public void installUI( JComponent c ) {
        diffLineOverview = ( DiffLineOverview ) c;
        diffLineOverview.setLayout( createLayoutManager() );
        diffLineOverview.setBorder( new EmptyBorder( 1, 1, 1, 1 ) );

        installDefaults();
        installComponents();
        installListeners();
    }

    public void uninstallUI( JComponent c ) {
        c.setLayout( null );
        uninstallListeners();
        uninstallComponents();
        uninstallDefaults();

        diffLineOverview = null;
    }

    public void installDefaults() {}

    public void installComponents() {
        mergeControl = new MergeControl( diffLineOverview.getView() );
        diffLineOverview.add( mergeControl, BorderLayout.NORTH );
        lineRendererPane = new LineRendererPane( );
        diffLineOverview.add( lineRendererPane, BorderLayout.CENTER );
    }

    public void installListeners() {}

    public void uninstallDefaults() {
        diffLineOverview.remove( mergeControl );
        diffLineOverview.remove( lineRendererPane );
        diffLineOverview = null;
    }

    public void uninstallComponents() {}

    public void uninstallListeners() {}

    protected LayoutManager createLayoutManager() {
        return new BorderLayout();
    }

    public class LineRendererPane extends JPanel {

        private int leftMargin = 6;

        public LineRendererPane( ) {
            setBorder( BorderFactory.createLineBorder( Color.black ) );
        }

        public Dimension getPreferredSize() {
            return new Dimension( 600, 100 );
        }

        public void paintComponent( Graphics gfx ) {
            super.paintComponent( gfx );

            // clear the display area
            Rectangle all = getBounds();
            gfx.setColor( diffLineOverview.getBackground() );
            gfx.fillRect( 0, 0, all.width, all.height );

            DiffLineModel model = diffLineOverview.getModel();
            if (model == null || model.getLeftCharacters() == null || model.getRightCharacters() == null) {
                return;
            }
            String leftLine = model.getLeftLine();
            String rightLine = model.getRightLine();

            // set up the font
            Font font = diffLineOverview.getFont();
            Font bold = font.deriveFont( Font.BOLD );
            gfx.setFont( font );
            FontMetrics fm = gfx.getFontMetrics();

            // draw the characters, left line above the right line
            // draw "Left" and bounding line with start and end ticks
            int x = leftMargin;
            gfx.setColor( Color.BLACK );
            gfx.drawString( "Left", x, fm.getHeight() );
            int left_width = fm.stringWidth( leftLine );
            int tick_height = fm.getHeight() / 2;
            int y = fm.getHeight() + tick_height;
            gfx.drawLine( x, y, x + left_width, y );
            gfx.drawLine( x, y, x, y + tick_height );
            gfx.drawLine( x + left_width, y, x + left_width, y + tick_height );

            // draw text of left line.  How to draw font with anti-aliasing?
            Color color;
            x = leftMargin;
            int y0 = 3 * fm.getHeight();
            int y1 = y0 + fm.getHeight() + 3;
            java.util.List<Character> leftChars = model.getLeftCharacters();
            java.util.List<Color> leftColors = model.getLeftColors();
            for ( int i = 0; i < leftChars.size(); i++ ) {
                char c = leftChars.get( i );
                color = leftColors.get( i );
                gfx.setColor( color );
                gfx.setFont( color == Color.BLACK ? font : bold );
                gfx.drawString( String.valueOf( c ), x, y0 );
                x += fm.charWidth( c );
            }

            // draw text of right line
            x = leftMargin;
            java.util.List<Character> rightChars = model.getRightCharacters();
            java.util.List<Color> rightColors = model.getRightColors();
            for ( int i = 0; i < rightChars.size(); i++ ) {
                char c = rightChars.get( i );
                color = rightColors.get( i );
                gfx.setColor( color );
                gfx.setFont( color == Color.BLACK ? font : bold );
                gfx.drawString( String.valueOf( c ), x, y1 );
                x += fm.charWidth( c );
            }

            // draw "Right" and bounding line with start and end ticks
            x = leftMargin;
            gfx.setColor( Color.BLACK );
            gfx.setFont( font );
            gfx.drawString( "Right", x, 6 * fm.getHeight() );
            int right_width = fm.stringWidth( rightLine );
            y = y1 + ( fm.getHeight() / 2 );
            gfx.drawLine( x, y, x + right_width, y );
            gfx.drawLine( x, y, x, y - tick_height );
            gfx.drawLine( x + right_width, y, x + right_width, y - tick_height );
        }
    }
}
