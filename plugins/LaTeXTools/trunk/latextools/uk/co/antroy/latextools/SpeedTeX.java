package uk.co.antroy.latextools; 
 
//import java.awt.*; 
//import java.awt.geom.*; 
import javax.swing.*; 
import java.awt.event.*; 
import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import java.util.*; 
//import java.text.*; 
 
public class SpeedTeX { 
 
 private Collection texBuffers = new HashSet();
 // Listener to handle characters added to buffer
 private BufferChangeListener bufferListener = new DocumentHandler(this);
 private SpeedTeXPopUp popup;
 
  //Listener to reset SpeedTeX on a mouse press.
 protected MouseListener mouseListener = new MouseAdapter(){
	 public void mousePressed(){
//		 reset();
	 }
 	};
 protected StringBuffer stringContents = new StringBuffer("");
 
 		public SpeedTeX(){
			popup = new SpeedTeXPopUp(this);
			popup.add("Test");
			popup.add("Z");
		}
	 
 
/*  Use the addBufferHandler() method of Buffer to listen for keypresses.
 SpeedJava uses a hashtable to keep a record of buffers - is this necessary? 
 Perhaps keep it simple to start with.
 */

 //Add current buffer to list
 public void addBuffer(Buffer buffer){
    texBuffers.add(buffer);
    buffer.addBufferChangeListener(bufferListener);
 }
 
 public void removeBuffer(Buffer buffer){
    texBuffers.remove(buffer);
    buffer.removeBufferChangeListener(bufferListener);
 }
 
 //close all dialogs, reset all character lists etc.
 public void reset(){
	 stringContents.delete(0,stringContents.length());
 }
 
 // See if the string matches the start of any of the words in the Command list.
 //Return any matching commands.
 public String[] lookupCommand(String lett){
   return null;
 }

 // See if the string matches the start of any of the words in the Environment list.
 //Return any matching commands.
 public String[] lookupEnv(String lett){
   return null;
 }
 
 
 
 //Inner class to handle the actual keypresses.
 class DocumentHandler extends BufferChangeAdapter{
	 
	 private SpeedTeX sptex;
	 
	 public DocumentHandler(SpeedTeX spt){
		 sptex = spt;
	 }
	 
	 //Keep a StringBuffer of characters pressed since the last \.
	 public void contentInserted(Buffer buffer, int startLine, 
                            	     int offset, int numLines, int length){
					     
//	    String inputString;
//	    char inputChar;
//	    Character inputCharacter;
//	    if (length==1) {
//		    inputString = buffer.getText(offset-1,1);
//		    inputChar = inputString.toCharArray()[0];
//		    inputCharacter = new Character(inputChar);
//	    }
//	    else {
//	       reset();
//	       return;
//	    }
//	    
//	    if (sptex.stringContents.length()==0){
//		    if (inputString.equals("\\")){
//			    sptex.stringContents.append(inputChar);
//		    }
//	    }
//	    else if (Character.isLetterOrDigit(inputChar)){
//		    sptex.stringContents.append(inputChar);
//		    Log.log(Log.MESSAGE, this, "String is: "+sptex.stringContents.toString());
//				
//				if (inputChar == '|') popup.show();
//				
//	    }
//	    else{
//		    reset();
//	    }
//	    
	}

																	 
//textArea.offsetToXY(int offset)
 }
 


} 
