
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


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


// displays a lot of data about an image
public class ImageMetaDataPanel extends JPanel {

    private JTextArea data;
    private String filename;

    public ImageMetaDataPanel() {
        setLayout( new BorderLayout() );
        data = new JTextArea();
        data.setEditable( false );
        data.setLineWrap( true );
        data.setWrapStyleWord( true );
        data.setColumns( 60 );
        JScrollPane scroller = new JScrollPane( data );
        add( scroller, BorderLayout.CENTER );
    }

    public void setFilename( String filename ) {
        this.filename = filename;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata( new File( filename ) );
            print( metadata );
            data.setCaretPosition( 0 );
        }
        catch ( ImageProcessingException e ) {
            // handle exception
        } catch ( IOException e ) {
            // handle exception
        }
    }

    public String getFilename() {
        return filename;
    }

    private void print( Metadata metadata ) {
        StringBuilder sb = new StringBuilder();
        for ( Directory directory : metadata.getDirectories() ) {
            for ( Tag tag : directory.getTags() ) {
                sb.append( tag.toString() ).append( '\n' );
            }
            if ( directory.hasErrors() ) {
                for ( String error : directory.getErrors() ) {
                    sb.append( "ERROR: " ).append( error ).append( '\n' );
                }
            }
        }
        data.setText( sb.toString() );
    }
}
