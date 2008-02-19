/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.townsfolkdesigns.swixml.jedit;

import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.StringReader;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSFileChooserDialog;
import org.gjt.sp.jedit.textarea.TextArea;
import org.gjt.sp.util.Log;
import org.swixml.SwingEngine;

/**
 *
 * @author eberry
 */
public class SwiXmlPlugin extends EBPlugin {

   public SwiXmlPlugin() {
   }

   public static void renderBuffer(View view) {
      TextArea textArea = view.getTextArea();
      String bufferText = textArea.getText();
      StringReader reader = new StringReader(bufferText);
      SwingEngine swingEngine = new SwingEngine();
      Container container = null;
      try {
         container = swingEngine.render(reader);
      } catch (Exception e) {
         Log.log(Log.ERROR, SwiXmlPlugin.class, "Error rendering - buffer: " + view.getBuffer().getName(), e);
      }
      showContainer(container);
   }

   public static void renderFile(View view) {
      Buffer buffer = view.getBuffer();
      VFSFileChooserDialog fileChooserDialog = new VFSFileChooserDialog(view, buffer.getDirectory(), JFileChooser.OPEN_DIALOG, false, true);
      String[] selectedFiles = fileChooserDialog.getSelectedFiles();
      if (selectedFiles != null && selectedFiles.length == 1) {
         File selectedFile = new File(selectedFiles[0]);
         SwingEngine swingEngine = new SwingEngine();
         Container container = null;
         try {
            container = swingEngine.render(selectedFile);
         } catch (Exception e) {
            Log.log(Log.ERROR, SwiXmlPlugin.class, "Error rendering - file: " + selectedFile.getPath(), e);
         }
         showContainer(container);
      }
   }

   private static void showContainer(Container container) {
      // if the container isn't an instance of JFrame, put it in one.
      if ((container instanceof JFrame) == false) {
         Dimension containerSize = container.getSize();
         JFrame frame = new JFrame("Test Frame");
         frame.setSize(containerSize);
         frame.setContentPane(container);
         container = frame;
      }
      container.setVisible(true);
   }
}
