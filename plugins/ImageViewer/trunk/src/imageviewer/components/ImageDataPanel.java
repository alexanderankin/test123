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

package imageviewer.components;

import java.awt.FontMetrics;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.text.BreakIterator;
import java.util.*;

// displays a little data about an image, the filename and the image size
public class ImageDataPanel extends JPanel {
    private JLabel filenameLabel;
    private JLabel imagesizeLabel;
    
    public ImageDataPanel() {
        setLayout(new GridLayout(2, 1 ));
        filenameLabel = new JLabel();
        imagesizeLabel = new JLabel();
        add(filenameLabel);
        add(imagesizeLabel);
    }
    
    public void setFilename(String filename) {
        filenameLabel.setText(compressFilename(filename));   
    }
    
    public String getFilename() {
        return filenameLabel.getText();   
    }
    
    public void setImageSize(int width, int height) {
        imagesizeLabel.setText(width + "x" + height);   
    }
    
    public void clear() {
        filenameLabel.setText("");
        imagesizeLabel.setText("");
    }
    
    /**
     * Takes a long filename and converts it to a smaller string with portions removed
     * from the middle so the filename fits in a display area of a certain width.  For
     * example, given a name like "/home/username/mypictures/myfamilypictures/awesomegrouphug.jpg",
     * this method might return "/home/username/.../awesomegrouphug.jpg".  The filename
     * itself will never be removed.
     * @param filename The filename to compress, if necessary, with an ellipsis in the middle.
     */
    private String compressFilename( String filename ) {
        if ( filename == null ) {
            return "";
        }
        int width = filenameLabel.getWidth() - 6;
        FontMetrics fm = getGraphics().getFontMetrics();
        if ( fm == null ) {
            return filename;
        }
        int stringWidth = fm.stringWidth( filename );
        if ( stringWidth <= width ) {
            return filename;
        }

        BreakIterator bi = BreakIterator.getWordInstance();
        bi.setText( filename );
        java.util.List<String> parts = new ArrayList<String>();
        int start = bi.first();
        for ( int end = bi.next(); end != BreakIterator.DONE; start = end, end = bi.next() ) {
            parts.add( filename.substring( start, end ) );
        }

        int middle = parts.size() / 2;
        java.util.List<String> startList = new ArrayList<String>( parts.subList(0, middle ) );
        java.util.List<String> endList = new ArrayList<String>( parts.subList( middle, parts.size() ) );
        String beginning = listToString( startList );
        String ending = listToString( endList );

        String combined = beginning + "..." + ending;
        while ( fm.stringWidth( combined ) > width ) {
            if ( beginning.length() >= ending.length() || endList.size() == 1 ) {
                startList.remove( startList.size() - 1 );
                beginning = listToString( startList );
            } else {
                endList.remove(0 );
                ending = listToString( endList );
            }
            combined = beginning + "..." + ending;
        }
        return combined;
    }

    private String listToString( java.util.List<String> list ) {
        StringBuilder sb = new StringBuilder();
        for ( String part : list ) {
            sb.append( part );
        }
        return sb.toString();
    }
    
}