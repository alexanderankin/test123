
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
package imageviewer.components;


import imageviewer.*;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;


public class ImageViewport extends JScrollPane {

    private ImageViewer imageViewer;
    private JLabel imageLabel;
    private BufferedImage image;
    private float originalWidth;
    private float originalHeight;
    private float zoomWidth;
    private float zoomHeight;


    public ImageViewport( ImageViewer parent ) {
        imageViewer = parent;
        installViewport();
    }


    private void installViewport() {
        imageLabel = new JLabel();
        imageLabel.setVerticalTextPosition( JLabel.BOTTOM );
        imageLabel.setHorizontalTextPosition( JLabel.CENTER );
        imageLabel.setHorizontalAlignment( JLabel.CENTER );
        update();
    }


    public JLabel getImageLabel() {
        return imageLabel;
    }


    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image) {
        this.image = (BufferedImage)image;
        Icon icon = new ImageIcon( image );
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        imageLabel.setIcon( icon );
        imageLabel.setSize( width, height );
        update();
    }
    
    public void update() {
        getViewport().setView(imageLabel);  
    }
    
    public Dimension getOriginalSize() {
        Dimension dim = new Dimension();
        dim.setSize(originalWidth, originalHeight);
        return dim;    
    }
    
    public Dimension getCurrentSize() {
        Icon icon = imageLabel.getIcon();
        return new Dimension(icon.getIconWidth(), icon.getIconHeight());
    }


    /**
     * Display the image in this ImageViewer.
     * @param filename the name of the image file to display.
     */
    public void showImage( String filename ) {
        showImage( filename, false );
    }


    /**
     * Display the image in this ImageViewer.
     * @param filename the name of the image file to display.
     * @param reload reload the image from disk.
     */
    public void showImage( String filename, boolean reload ) {
        if ( !reload && filename.equals( imageViewer.getImageDataPanel().getFilename() ) ) {
            // already showing this image
            return;
        }


        if ( ImageViewer.isValidFilename( filename ) ) {
            image = null;
            try
            {
                image = ImageIO.read( new File( filename ) );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
            ImageIcon icon = new ImageIcon( image );
            originalWidth = ( float )icon.getIconWidth();
            originalHeight = ( float )icon.getIconHeight();
            zoomWidth = originalWidth;
            zoomHeight = originalHeight;
            imageLabel.setIcon( icon );
            imageLabel.setSize( ( int )originalWidth, ( int )originalHeight );
            ImageDataPanel dataPanel = imageViewer.getImageDataPanel();
            dataPanel.setFilename( filename );
            dataPanel.setImageSize( ( int )originalWidth, ( int )originalHeight );
            Rectangle viewportRect = getViewport().getViewRect();
            float viewportHeight = ( float )viewportRect.getHeight();
            if ( originalHeight > viewportHeight ) {
                zoomHeight = viewportHeight;
                zoomWidth = originalWidth *= viewportHeight / originalHeight;
                originalWidth = zoomWidth;
                originalHeight = zoomHeight;
                imageViewer.zoom( zoomWidth, zoomHeight );
            }


            imageLabel.invalidate();
            imageLabel.validate();
            imageViewer.refresh();
        }
    }
}
