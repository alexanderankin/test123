
/*
 * Copyright (c) 2009, Dale Anson
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
 * Neither the name of the author nor the names of its contributors
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
package imageviewer;


import imageviewer.actions.*;
import imageviewer.components.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.text.*;
import java.util.*;

import javax.swing.*;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;


/**
 * A simple image viewer.  Provides scrolling and zooming, and that's it. The
 * zoom in is unrestricted, which means is is possible to run out of memory.
 * Zoom out stops at 2 x 2 pixels.
 */
public class ImageViewer extends JPanel {

    private String filename;
    private ImageViewport imageViewport;
    private ImageDataPanel dataPanel;
    private ImageMetaDataPanel metadataPanel;
    private JButton zoomIn;
    private JButton zoomOut;
    private JButton rotateCCW;
    private JButton rotateCW;
    private JButton reload;
    private JButton clear;
    private JButton copy;
    private JToggleButton info;
    public static final double CW90 = 90.0;

    public ImageViewer() {
        installComponents();
        installListeners();
    }

    // create and layout the components
    private void installComponents() {
        setBorder( BorderFactory.createEmptyBorder( 0, 6, 6, 6 ) );
        setLayout( new BorderLayout() );

        // create and add image view port
        imageViewport = new ImageViewport( this );
        add( imageViewport, BorderLayout.CENTER );

        // set up the buttons
        clear = new JButton( GUIUtilities.loadIcon( "22x22/actions/edit-clear.png" ) );
        clear.setToolTipText( jEdit.getProperty( "imageviewer.clear", "Clear" ) );

        copy = new JButton( GUIUtilities.loadIcon( "22x22/actions/edit-copy.png" ) );
        copy.setToolTipText( jEdit.getProperty( "imageviewer.copy", "Copy" ) );

        zoomIn = new JButton( GUIUtilities.loadIcon( "22x22/actions/zoom-in.png" ) );
        zoomIn.setToolTipText( jEdit.getProperty( "imageviewer.zoomin", "Zoom In" ) );

        zoomOut = new JButton( GUIUtilities.loadIcon( "22x22/actions/zoom-out.png" ) );
        zoomOut.setToolTipText( jEdit.getProperty( "imageviewer.zoomout", "Zoom Out" ) );

        ImageIcon cwRotateIcon = ( ImageIcon )GUIUtilities.loadIcon( "22x22/actions/edit-redo.png" );
        ImageIcon ccwRotateIcon = new ImageIcon( mirror( cwRotateIcon.getImage() ) );
        rotateCCW = new JButton( ccwRotateIcon );
        rotateCCW.setToolTipText( jEdit.getProperty( "imageviewer.rotateCCW", "Rotate counter-clockwise" ) );
        rotateCW = new JButton( cwRotateIcon );
        rotateCW.setToolTipText( jEdit.getProperty( "imageviewer.rotateCW", "Rotate clockwise" ) );

        reload = new JButton( GUIUtilities.loadIcon( "22x22/actions/view-refresh.png" ) );
        reload.setToolTipText( jEdit.getProperty( "imageviewer.reload", "Reload" ) );

        info = new JToggleButton( GUIUtilities.loadIcon( "22x22/apps/help-browser.png" ) );
        info.setToolTipText( jEdit.getProperty( "imageviewer.info", "Image information" ) );
        info.setSelected( false );

        // create toolbar
        JToolBar buttonPanel = new JToolBar();
        buttonPanel.setFloatable( false );
        buttonPanel.add( clear );
        buttonPanel.add( copy );
        buttonPanel.add( zoomIn );
        buttonPanel.add( zoomOut );
        buttonPanel.add( rotateCCW );
        buttonPanel.add( rotateCW );
        buttonPanel.add( reload );
        buttonPanel.add( info );

        // inner panel for the filename and image size
        dataPanel = new ImageDataPanel();

        // create a panel for the toolbar
        JPanel toolbar = new JPanel( new BorderLayout() );
        toolbar.add( dataPanel, BorderLayout.CENTER );
        toolbar.add( buttonPanel, BorderLayout.EAST );

        // add the toolbar panel
        add( toolbar, BorderLayout.NORTH );

        // metadata panel
        metadataPanel = new ImageMetaDataPanel();
        metadataPanel.setVisible( false );
        add( metadataPanel, BorderLayout.EAST );
    }

    // add any listeners necessary for the installed components
    private void installListeners() {
        clear.addActionListener( new ClearAction( ImageViewer.this ) );

        copy.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    copy();
                }
            }
        );

        zoomIn.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    zoomIn();
                }
            }
        );

        zoomOut.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    zoomOut();
                }
            }
        );

        rotateCCW.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    rotateCCW();
                }
            }
        );

        rotateCW.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    rotateCW();
                }
            }
        );

        reload.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    reload();
                }
            }
        );

        info.addActionListener( new ActionListener(){

                public void actionPerformed( ActionEvent ae ) {
                    metadataPanel.setVisible( !metadataPanel.isVisible() );
                    refresh();
                }
            }
        );

        getViewport().addMouseMotionListener( mouseAdapter );
        getViewport().addMouseListener( mouseAdapter );
        getViewport().addMouseWheelListener( mouseAdapter );

        addComponentListener( new ComponentAdapter(){

                public void componentResized( ComponentEvent ce ) {
                    if ( ce.getComponent().equals( ImageViewer.this ) ) {
                        dataPanel.setFilename( filename );
                    }
                }
            }
        );
    }

    public JViewport getViewport() {
        return imageViewport.getViewport();
    }

    public JLabel getImageLabel() {
        return imageViewport.getImageLabel();
    }

    public ImageDataPanel getImageDataPanel() {
        return dataPanel;
    }

    /**
     * Display the image in this ImageViewer.
     * @param filename the name of the image file to display.
     */
    public void showImage( String filename ) {
        this.filename = filename;
        imageViewport.showImage( filename );
        metadataPanel.setFilename( filename );
    }

    public void refresh() {
        invalidate();
        validate();
    }

    /**
     * @return true if the filename, regardless of case, ends with .jpg, .gif, or .png.
     */
    public static boolean isValidFilename( String filename ) {
        if ( filename == null ) {
            return false;
        }

        String name = filename.toLowerCase();
        return name.endsWith( ".jpg" ) || name.endsWith( ".jpeg" ) || name.endsWith( ".gif" ) || name.endsWith( ".png" );
    }

    /**
     * Action to copy the image to the system clipboard.
     */
    public void copy() {
        Image image = imageViewport.getImage();
        ImageSelection.copyImageToClipboard( image );
    }

    /**
     * Action to reload the current image from disk.
     */
    public void reload() {
        imageViewport.showImage( filename, true );
    }

    /**
     * Action to zoom in 10%.
     */
    public void zoomIn() {
        Dimension dim = imageViewport.getCurrentSize();
        float width = new Float( dim.getWidth() ).floatValue()  *  1.1f;
        float height = new Float( dim.getHeight() ).floatValue()  *  1.1f;
        zoom( width, height );
    }

    /**
     * Action to zoom out 10%.
     */
    public void zoomOut() {
        Dimension dim = imageViewport.getCurrentSize();
        float width = new Float( dim.getWidth() ).floatValue()  *  0.9f;
        float height = new Float( dim.getHeight() ).floatValue()  *  0.9f;
        if ( width < 1.0 || height < 1.0 ) {
            return;
        }

        zoom( width, height );
    }

    /**
     * Zoom an image to the given width and height and refresh the display.
     * @param width the desired width
     * @param height the desired height
     */
    public void zoom( float width, float height ) {
        if ( width > 0 && height > 0 ) {
            Image image = imageViewport.getImage();
            Image zoomImage = getScaledImage( image, ( int )width, ( int )height );
            ImageIcon icon = new ImageIcon( zoomImage );
            JLabel imageLabel = getImageLabel();
            imageLabel.setIcon( icon );
            imageLabel.setSize( ( int )width, ( int )height );
            imageViewport.update();
            refresh();
        }
    }

    /**
     * Resizes an image to the given width and height.
     * @param image source image to scale
     * @param width desired width
     * @param height desired height
     * @return the resized image
     */
    protected Image getScaledImage( Image image, int width, int height ) {
        BufferedImage resizedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = resizedImage.createGraphics();
        Map<RenderingHints.Key, Object> renderingHints = new HashMap<RenderingHints.Key, Object>();
        renderingHints.put( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );
        renderingHints.put( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        g2.setRenderingHints( renderingHints );
        g2.drawImage( image, 0, 0, width, height, null );
        g2.dispose();
        return resizedImage;
    }

    protected void rotateCCW() {

        // TODO: figure out why I can't rotate counter-clockwise.  It seems like
        // the rotation value has to be less than pi and greater than 0.
        // Three clockwise rotations works the same, though.
        rotateCW();
        rotateCW();
        rotateCW();
    }

    protected void rotateCW() {
        Dimension d = imageViewport.getCurrentSize();
        Image rotatedImage = rotate( CW90 );
        imageViewport.setImage( rotatedImage );
        zoom( new Float( d.getWidth() ).floatValue(), new Float( d.getHeight() ).floatValue() );
        imageViewport.update();
        refresh();
    }

    /**
     * This is not a general rotate routine.  The transformations assume 90 degree
     * rotations.
     * @param degrees Rotation in degrees.  Only 90 degrees seems to work well.
     */
    protected Image rotate( double degrees ) {
        Image image = imageViewport.getImage();
        double amount = Math.toRadians( degrees );
        MediaTracker mt = new MediaTracker( this );
        mt.addImage( image, 0 );
        try {
            mt.waitForID( 0 );
        }
        catch ( InterruptedException ie ) {
        }

        BufferedImage sourceBI = new BufferedImage( image.getWidth( null ), image.getHeight( null ), BufferedImage.TYPE_INT_ARGB );

        Graphics2D g = ( Graphics2D )sourceBI.getGraphics();
        g.drawImage( image, 0, 0, null );

        AffineTransform at = new AffineTransform();

        // rotate around image center
        at.rotate( amount, sourceBI.getWidth()  /  2.0, sourceBI.getHeight()  /  2.0 );

        // translate to make sure the rotation doesn't cut off any image data
        AffineTransform translationTransform = findTranslation( at, sourceBI );
        at.preConcatenate( translationTransform );

        // instantiate and apply affine transformation filter
        BufferedImageOp bio = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
        return bio.filter( sourceBI, null );
    }

    // find proper translations to keep rotated image correctly displayed
    private AffineTransform findTranslation( AffineTransform at, BufferedImage bi ) {
        Point2D p2din, p2dout;

        p2din = new Point2D.Double( 0.0, 0.0 );
        p2dout = at.transform( p2din, null );
        double ytrans = p2dout.getY();

        p2din = new Point2D.Double( bi.getWidth(), bi.getHeight() );
        p2dout = at.transform( p2din, null );
        double xtrans = p2dout.getX();

        AffineTransform tat = new AffineTransform();
        tat.translate( -xtrans, -ytrans );
        return tat;
    }

    /**
     * All this is used for is to make the rotate clockwise icon into a
     * rotate counter-clockwise icon.  I suppose I could use it as the action
     * for another button on the toolbar.
     */
    protected Image mirror( Image img ) {
        BufferedImage bufferedImage = new BufferedImage( img.getWidth( null ), img.getHeight( null ), BufferedImage.TYPE_INT_ARGB );
        Graphics gb = bufferedImage.getGraphics();
        gb.drawImage( img, 0, 0, null );
        gb.dispose();

        AffineTransform tx = AffineTransform.getScaleInstance( -1, 1 );
        tx.translate( -img.getWidth( null ), 0 );
        AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        bufferedImage = op.filter( bufferedImage, null );
        return bufferedImage;
    }

    // MouseWheel zooms in/out.
    // Mouse drag moves viewport.
    // Double click centers point clicked.
    MMouseAdapter mouseAdapter = new MMouseAdapter(){

        Point previous = null;
        Cursor oldCursor = null;
        CenterAction centerAction = new CenterAction( ImageViewer.this );

        /**
         * Moves the view port in the parent ImageViewer.
         */
        public void mouseDragged( MouseEvent me ) {
            if ( previous == null ) {
                previous = me.getPoint();
                return;
            }

            Point now = me.getPoint();
            int dx = previous.x - now.x;
            int dy = previous.y - now.y;
            Point current = ImageViewer.this.getViewport().getViewPosition();
            Point to = new Point( current.x + dx, current.y + dy );
            ImageViewer.this.getViewport().setViewPosition( to );
            previous = now;
        }

        /**
         * Centers the image in the parent ImageViewer.
         */
        public void mousePressed( MouseEvent me ) {
            previous = me.getPoint();
            oldCursor = ImageViewer.this.getImageLabel().getCursor();
            ImageViewer.this.getImageLabel().setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
        }

        public void mouseReleased( MouseEvent me ) {
            previous = null;
            ImageViewer.this.getImageLabel().setCursor( oldCursor != null ? oldCursor : Cursor.getDefaultCursor() );
        }

        /**
         * Center image on mouse pointer.
         */
        public void mouseClicked( MouseEvent me ) {
            if ( me.getClickCount() == 2 ) {
                centerAction.mouseClicked( me );
            }
        }

        /**
         * Zoom in or out.
         */
        public void mouseWheelMoved( MouseWheelEvent me ) {
            if ( me.getWheelRotation() > 0 ) {
                ImageViewer.this.zoomIn();
            }
            else {
                ImageViewer.this.zoomOut();
            }
        }
    };
}
