/*
*  Copyright (C) 2003 Don Brown (mrdon@techie.com)
*  Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)
*  This file is part of Log Viewer, a plugin for jEdit (http://www.jedit.org).
*  It is heavily  based off Follow (http://follow.sf.net).
*  Log Viewer is free software; you can redistribute it and/or modify
*  it under the terms of version 2 of the GNU General Public
*  License as published by the Free Software Foundation.
*  Log Viewer is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*  You should have received a copy of the GNU General Public License
*  along with Log Viewer; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package logviewer;

import ghm.follow.FileFollower;
import ghm.follow.JTextAreaDestination;
import ghm.follow.OutputDestination;
import java.awt.Insets;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.border.EmptyBorder;

/**
 *  A component which allows one to view a text file to which information is
 *  being asynchronously appended.
 *
 * @author    <a href="mailto:mrdon@techie.com">Don Brown</a>
 * @author    <a href="mailto:greghmerrill@yahoo.com">Greg Merrill</a>
 */
public class FileFollowingPane extends JScrollPane {

   /**  FileFollower used to print to this component */
   protected FileFollower fileFollower_;

   /**  Text area into which followed file's contents are printed */
   protected JTextArea textArea_;

   /**  OutputDestination used w/FileFollower */
   protected JTextAreaDestination destination_;

   private JCheckBox lock_box;
   private int locked_value = 0;

   /**
    *  Constructor
    *
    * @param  file               text file which is to be followed
    * @param  bufferSize         size of the character buffer inside the
    *      FileFollower used to follow the supplied file
    * @param  latency            latency of the FileFollower used to follow the
    *      supplied file
    * @param  autoPositionCaret  Whether to autoposition the caret
    */
   public FileFollowingPane( File file, int bufferSize, int latency, boolean autoPositionCaret ) {
      textArea_ = new JTextArea();
      textArea_.setEditable( false );
      textArea_.setWrapStyleWord( true );
      destination_ = new JTextAreaDestination( textArea_, autoPositionCaret );
      fileFollower_ = new FileFollower(
               file,
               bufferSize,
               latency,
               new OutputDestination[] {destination_}
            );

      lock_box = new JCheckBox();
      lock_box.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
      lock_box.setSelected(false);
      lock_box.setToolTipText("Lock the horizontal scroll bar.");
      setCorner( JScrollPane.LOWER_RIGHT_CORNER, lock_box );
      getHorizontalScrollBar().setModel( new javax.swing.DefaultBoundedRangeModel() {
               public int getValue() {
                  return lock_box.isSelected() ? locked_value : super.getValue();
               }
               public void setValue(int value) {
                  if (lock_box.isSelected())
                     return;
                  locked_value = value;  
                  super.setValue(value);
               }
            }
                                       );
      this.getViewport().setView( textArea_ );

   }

   /**
    *  Returns the text area to which the followed file's contents are being
    *  printed.
    *
    * @return    text area containing followed file's contents
    */
   public JTextArea getTextArea() {
      return textArea_;
   }

   public void setWordWrap( boolean value ) {
      textArea_.setLineWrap( value );
   }

   public boolean getWordWrap() {
      return textArea_.getLineWrap();
   }

   public void toggleWordWrap() {
      textArea_.setLineWrap( !textArea_.getLineWrap() );
   }

   /**
    *  Returns whether caret is automatically repositioned to the end of the
    *  text area when text is appended to the followed file
    *
    * @return    whether caret is automatically repositioned on append
    */
   public boolean autoPositionCaret() {
      return destination_.autoPositionCaret();
   }

   /**
    *  Sets whether caret is automatically repositioned to the end of the text
    *  area when text is appended to the followed file
    *
    * @param  value  whether caret is automatically repositioned on append
    */
   public void setAutoPositionCaret( boolean value ) {
      destination_.setAutoPositionCaret( value );
   }
   
   public void toggleAutoPositionCaret(){
      setAutoPositionCaret(!autoPositionCaret());
   }

   /**
    *  Returns the FileFollower which is being used to print information in
    *  this component.
    *
    * @return    FileFollower used by this component
    */
   public FileFollower getFileFollower() {
      return fileFollower_;
   }

   /**
    *  Convenience method; equivalent to calling
    *  getFileFollower().getFollowedFile()
    *
    * @return    The followedFile value
    */
   public File getFollowedFile() {
      return fileFollower_.getFollowedFile();
   }

   /**  Convenience method; equivalent to calling getFileFollower().start() */
   public void startFollowing() {
      fileFollower_.start();
   }

   /**  Convenience method; equivalent to calling getFileFollower().stop() */
   public void stopFollowing() {
      fileFollower_.stop();
   }

   /**
    *  Convenience method; equivalent to calling
    *  getFileFollower().stopAndWait()
    *
    * @exception  InterruptedException  If something goes wrong
    */
   public void stopFollowingAndWait()
   throws InterruptedException {
      fileFollower_.stopAndWait();
   }

   /**
    *  Clears the contents of this FileFollowingPane synchronously.
    *
    * @exception  IOException  If something goes wrong
    */
   public void clear()
   throws IOException {
      if ( fileFollower_.getFollowedFile().length() == 0L ) {
         return ;
      }
      synchronized ( fileFollower_ ) {
         try {
            fileFollower_.stopAndWait();
         }
         catch ( InterruptedException interruptedException ) {
            // Handle this better later
            interruptedException.printStackTrace( System.err );
         }

         // This has the effect of clearing the contents of the followed file
         BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(
                  fileFollower_.getFollowedFile()
               ) );
         bos.close();

         // Update textarea contents to reflect freshly cleared file
         Document doc = textArea_.getDocument();
         try {
            doc.remove( 0, doc.getLength() );
         }
         catch ( BadLocationException badLocationException ) {
            // Handle this better later
            badLocationException.printStackTrace( System.err );
         }

         fileFollower_.start();
      }
   }


}

