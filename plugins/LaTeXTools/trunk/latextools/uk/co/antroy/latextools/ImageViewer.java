// \includegraphics*[width=7cm]{graphics\complexes.png}    
// \hfill\includegraphics[width=.3\textwidth]{graphics/cchalfspace1.png}\hfill
// \includegraphics[width=.3\textwidth]{graphics/cchalfspace2.png}\hfill\textcolor{white}{.}
//  :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.io.File;

import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.textarea.JEditTextArea;


public class ImageViewer {
   
    private JPanel imagePanel;

    private static final ImageViewer imageViewer = new ImageViewer();
    
    private ImageViewer(){
       imagePanel = new JPanel(new BorderLayout());
    }
    
    private File findFileName(JEditTextArea textArea, Buffer buffer) {
        int line = textArea.getCaretLine();
        String currentLine = buffer.getLineText(line);
        RE pattern = null;

        try {
            pattern = new RE("\\\\includegraphic(?:x|s).*?\\{(.+?)\\}");
        } catch (REException e) {
            e.printStackTrace();
        }

        REMatch match = pattern.getMatch(currentLine);
        String filename;

        if (match != null) {
            filename = match.toString(1);
        } else {

            return null;
        }

        File imageFile = new File(filename);

        if (!imageFile.exists()) {
            String tex = LaTeXMacros.getMainTeXPath(buffer);
            File texFile = new File(tex);
            imageFile = new File(texFile.getParent(), filename);
        }

        if (!imageFile.exists()) {

            return null;
        }

        return imageFile;
    }

    public static ImageViewer getInstance(){
       return imageViewer;
    }
    
    public JPanel showImage() {

        return imagePanel;
    }

    //          \includegraphics*[width=7cm]{graphics\complexes.png}
    // \hfill\includegraphics[width=.3\textwidth]{graphics/cchalfspace1.png}\hfill
    // \includegraphics[width=.3\textwidth]{graphics/cchalfspace2.png}\hfill\textcolor{white}{.}

    public void setImageFromBuffer(JEditTextArea textArea, 
                                          Buffer buffer) {
        File file = findFileName(textArea, buffer);

        if (file == null) {

            return;
        }

        imagePanel.removeAll(); //= new JPanel();
        JLabel imageLabel = new JLabel(file.getPath());
        JScrollPane scrollpane = new JScrollPane(
                                         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ImageIcon image = null;

        if (file.exists()) {

            try {
                image = new ImageIcon(file.toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            image = new ImageIcon();
        }

        JLabel label = new JLabel(image);
        JViewport viewport = new JViewport();
        viewport.setView(label);
        scrollpane.setViewport(viewport);
        imagePanel.add(scrollpane, BorderLayout.CENTER);
        imagePanel.add(imageLabel, BorderLayout.SOUTH);
        int imageHeight = image.getIconHeight();
        int imageWidth = image.getIconWidth();

        imagePanel.setPreferredSize(new Dimension(imageWidth + 12, imageHeight + 12));
     }

}
