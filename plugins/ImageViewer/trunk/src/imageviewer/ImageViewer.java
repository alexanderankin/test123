/*
Copyright (c) 2009, Dale Anson
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

package imageviewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;

/**
 * A simple image viewer.  Provides scrolling and zooming, and that's it. The
 * zoom in is unrestricted, which means is is possible to run out of memory.
 * Zoom out stops at 2 x 2 pixels.
 * TODO: is a zoom percent spinner necessary?  Right now, all images are loaded
 * at 100%.  With a percent spinner, the initial size could be based on the
 * current spinner value, which might be handy when scanning a directory of
 * small icons or large images.
 */
public class ImageViewer extends JPanel {
    private JLabel imageLabel;
    private JLabel filenameLabel;
    private JButton zoomIn;
    private JButton zoomOut;
    private JButton clear;
    private float originalWidth = 0.0f;
    private float originalHeight = 0.0f;

    private Image image = null;

    public ImageViewer() {
        installComponents();
        installListeners();
    }

    // create and layout the components
    private void installComponents() {
        setBorder( BorderFactory.createEmptyBorder( 6, 6, 6, 6 ) );
        setLayout( new BorderLayout() );

        // use a JLabel to actually display the image
        imageLabel = new JLabel();
        imageLabel.setVerticalTextPosition( JLabel.BOTTOM );
        imageLabel.setHorizontalTextPosition( JLabel.CENTER );
        imageLabel.setHorizontalAlignment( JLabel.CENTER );
        add( new JScrollPane( imageLabel ), BorderLayout.CENTER );

        // use another JLabel to show the name of the file being shown
        filenameLabel = new JLabel();

        // set up the zoom buttons
        clear = new JButton( GUIUtilities.loadIcon( "22x22/actions/edit-clear.png" ) );
        clear.setToolTipText( jEdit.getProperty( "imageviewer.clear", "Clear" ) );
        zoomIn = new JButton( GUIUtilities.loadIcon( "22x22/actions/zoom-in.png" ) );
        zoomIn.setToolTipText( jEdit.getProperty( "imageviewer.zoomin", "Zoom In" ) );
        zoomOut = new JButton( GUIUtilities.loadIcon( "22x22/actions/zoom-out.png" ) );
        zoomOut.setToolTipText( jEdit.getProperty( "imageviewer.zoomout", "Zoom Out" ) );
        JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 3 ) );
        btnPanel.add( clear );
        btnPanel.add( zoomIn );
        btnPanel.add( zoomOut );

        // create a panel for the toolbar
        JPanel toolbar = new JPanel( new BorderLayout() );
        toolbar.add( filenameLabel, BorderLayout.WEST );
        toolbar.add( btnPanel, BorderLayout.EAST );

        // add the toolbar panel
        add( toolbar, BorderLayout.NORTH );
    }

    // add any listeners necessary for the installed components
    private void installListeners() {
        clear.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    imageLabel.setIcon( null );
                    ImageViewer.this.invalidate();
                    ImageViewer.this.validate();
                }
            }
        );
        zoomIn.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    float width = originalWidth * 1.1f;
                    float height = originalHeight * 1.1f;
                    originalWidth = width;
                    originalHeight = height;
                    if ( width > 0 && height > 0 ) {
                        Image zoomImage = getScaledImage( image, ( int ) width, ( int ) height );
                        ImageIcon icon = new ImageIcon( zoomImage );
                        imageLabel.setIcon( icon );
                        imageLabel.setSize( ( int ) width, ( int ) height );
                        ImageViewer.this.invalidate();
                        ImageViewer.this.validate();
                    }
                }
            }
        );
        zoomOut.addActionListener(
            new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    float width = Math.max( 2, ( int ) ( ( float ) originalWidth * 0.9f ) );
                    float height = Math.max( 2, ( int ) ( ( float ) originalHeight * 0.9f ) );
                    originalWidth = width;
                    originalHeight = height;
                    if ( width > 0 && height > 0 ) {
                        Image zoomImage = getScaledImage( image, ( int ) width, ( int ) height );
                        ImageIcon icon = new ImageIcon( zoomImage );
                        imageLabel.setIcon( icon );
                        imageLabel.setSize( ( int ) width, ( int ) height );
                        ImageViewer.this.invalidate();
                        ImageViewer.this.validate();
                    }
                }
            }
        );
    }
    
    /**
     * Display the image in this ImageViewer.
     * @param filename the name of the image file to display.
     */
    public void showImage( String filename ) {
        if ( isValidFilename( filename ) ) {
            if (filename.equals(filenameLabel.getText())) {
                // already showing this image
                return;
            }
            filenameLabel.setText( filename );
            ImageIcon icon = new ImageIcon( filename );
            image = icon.getImage();
            originalWidth = ( float ) icon.getIconWidth();
            originalHeight = ( float ) icon.getIconHeight();
            imageLabel.setIcon( icon );
            imageLabel.setSize( ( int ) originalWidth, ( int ) originalHeight );
            invalidate();
            validate();
        }
    }
    
    /**
     * @return true if the filename, regardless of case, ends with .jpg, .gif, or .png.    
     */
    public static boolean isValidFilename( String filename ) {
        if ( filename == null ) {
            return false;
        }
        String name = filename.toLowerCase();
        return name.endsWith( ".jpg" ) || name.endsWith( ".gif" ) || name.endsWith( ".png" );
    }

    /**
     * Resizes an image to the given width and height.
     * @param image source image to scale
     * @param w desired width
     * @param h desired height
     * @return the resized image
     */
    private Image getScaledImage( Image image, int w, int h ) {
        BufferedImage resizedImage = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = resizedImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        g2.drawImage( image, 0, 0, w, h, null );
        g2.dispose();
        return resizedImage;
    }
}