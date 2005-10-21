package recentbuffer;

// from Java:
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.util.Collections;

// from Swing:
import javax.swing.*;
import javax.swing.event.*;

// from jEdit:
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.io.*;
import org.gjt.sp.jedit.msg.PropertiesChanged;
import org.gjt.sp.jedit.msg.BufferUpdate;
import org.gjt.sp.jedit.msg.EditPaneUpdate;
import org.gjt.sp.util.Log;
/**
 * Implements a vector of BufferTime objects and sorts by most recently accessed
 *
 * @author Michael Thornhill
 * @version   $Revision: 1.1 $ $Date: 2005-10-21 11:48:39 $
 */
public class BufferAccessMonitor extends Vector implements EBComponent {
	/**
	 * Default Constructor for the <tt>BufferAccessMonitor</tt> object, 
	 * maintains a list of currently open buffers and orders them by most recently accessed
	 */
	public BufferAccessMonitor() {			
		//System.out.println("created one and only instance of buffer monitor");
        Buffer[] openBuffers = jEdit.getBuffers();
		for (int i =0 ; i < openBuffers.length; i++) {
			BufferTime bufTime = new BufferTime(openBuffers[i].getLastModified());
			bufTime.buffer = openBuffers[i];
			this.add(bufTime);
		}
		Collections.sort(this);		
		EditBus.addToBus(this);		
	}
		
	
	public void handleMessage(org.gjt.sp.jedit.EBMessage message) {		
		Buffer debuf;				
		if(message instanceof BufferUpdate) {			
			BufferUpdate bufMessage = (BufferUpdate)message;
			if  (bufMessage.getWhat() == BufferUpdate.CLOSED) {         
                debuf = bufMessage.getBuffer();
                this.remove(debuf);
			}
			else if (bufMessage.getWhat() == BufferUpdate.LOADED) {
				debuf = bufMessage.getBuffer();
				this.add(debuf);
			}
		
		}        
        else if(message instanceof EditPaneUpdate) {
			EditPaneUpdate editPaneMessage = (EditPaneUpdate)message;
			//System.out.println(editPaneMessage);
			if (editPaneMessage.getWhat() == EditPaneUpdate.BUFFER_CHANGED) {
				EditPane ep = editPaneMessage.getEditPane();
				debuf = ep.getBuffer();
				this.add(debuf);				
			}
		}
	}
	
	/**
	 * print the vector of BufferTime objects to console
	 */	
	public void printBufferList() {
		for (int i= 0; i < this.size(); i++) {
			System.out.println(this.get(i));
		}		
	}
	
	/**
	 * add a buffer to the vector, update the time if the buffer object already exists
	 */	
	public void add(Buffer debuf) {
		long curTime = System.currentTimeMillis();
        boolean found = false;	
		
		for(int i = 0; i < this.size(); i++) {
			BufferTime deBufTimeObj = (BufferTime)this.get(i);
			if (deBufTimeObj.buffer == debuf) {
				deBufTimeObj.time = curTime;
				found = true;
			}
		}
		if (found==false) {
			BufferTime bufTime = new BufferTime(curTime);
			bufTime.buffer = debuf;			
			this.add(bufTime);
		}			
	}
	
	/**
	 * remove a buffer from the vector
	 */	
	public void remove(Buffer debuf) {
		for(int i = 0; i < this.size(); i++) {
			BufferTime deBufTimeObj = (BufferTime)this.get(i);
			if (deBufTimeObj.buffer == debuf) {
				this.remove(deBufTimeObj);
			}
		}
	}
	
	/**
	 * return an array of buffers ordered by most recently accessed
	 */	
	public Buffer[] getBufferList(Buffer currBuff) {
		Buffer[] tempArray = new Buffer[this.size()-1];
		Collections.sort(this);
		int j=0;
		for(int i = 0; i < this.size(); i++) {
			BufferTime deBufTimeObj = (BufferTime)this.get(i);			
			if (deBufTimeObj.buffer != currBuff) {
				tempArray[j]=deBufTimeObj.buffer;
				j = j+1;
			}
		}
		return tempArray;	
	}
}

