// \includegraphics*[width=7cm]{graphics\complexes.png}    
// \hfill\includegraphics[width=.3\textwidth]{graphics/cchalfspace1.png}\hfill
// \includegraphics[width=.3\textwidth]{graphics/cchalfspace2.png}\hfill\textcolor{white}{.}
//  :latex.root='D:\Projects\Thesis\src\Thesis.tex':
package uk.co.antroy.latextools;

import uk.co.antroy.latextools.macros.*;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.io.File;

import java.net.MalformedURLException;

import javax.swing.*;
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
   
    private JComponent imagePanel;

    private static final ImageViewer imageViewer = new ImageViewer();
    
    private ImageViewer(){
       //imagePanel = new JPanel(new BorderLayout());
    }
    
    private File findFileName(View view, Buffer buffer) {
        JEditTextArea textArea = view.getTextArea();
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
            String tex = ProjectMacros.getMainTeXPath(buffer);
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
    
    public JComponent showImage() {

        return imagePanel;
    }

    public static void showInInfoPane(View view, 
                                          Buffer buffer){
        ImageViewer viewer = ImageViewer.getInstance();
        viewer.setImageFromBuffer(view, buffer);
        JComponent p = viewer.showImage();
        LaTeXDockable.getInstance().setInfoPanel(p, "Image Under Caret:");
    }
    

    public void setImageFromBuffer(View view, 
                                          Buffer buffer) {
        File file = findFileName(view, buffer);

        if (file == null) {
            imagePanel = new JLabel("<html><font color='#dd0000'><b>There are no images under the caret!");
            return;
        }

//        imagePanel.removeAll(); //= new JPanel();
        JLabel imageLabel = new JLabel(file.getPath());
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
        JPanel inner = new JPanel();
        inner.add(label, BorderLayout.CENTER);
        imagePanel = new JScrollPane(inner, 
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

     }

}
