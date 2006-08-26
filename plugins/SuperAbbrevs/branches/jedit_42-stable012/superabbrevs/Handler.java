package superabbrevs;

import org.gjt.sp.jedit.buffer.*;
import org.gjt.sp.jedit.Buffer;
import java.util.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;
import superabbrevs.template.*;

public class Handler extends BufferChangeAdapter {
	private boolean disabled;
	private JEditTextArea textArea;
	private boolean justEdited = false;
	private int caret;
	private int oldTemplateLength;
	private Buffer buffer;
	private boolean inCompoundEdit = false;
	
	//{{{ field Template template
	private Template template;
	/**
	 * Getter function for the field template
	 */ 
	public Template getTemplate() {
		return template;
	}
	//}}}
	
	
	public Handler(Template template, JEditTextArea textArea){
		this.textArea = textArea; 
		buffer = textArea.getBuffer();
		this.template = template;
	}
	
	public boolean justEdited(){
		return justEdited;
	}
		
	public boolean isDisabled(){
		return disabled;
	}
	
	public void contentInserted(Buffer buffer, int startLine, int offset, 
								int numLines, int length){
		
		if (!disabled){
			String insertedText = buffer.getText(offset, length);
			
			try{
				
				if(justEdited){
					offset = caret;
					oldTemplateLength += length;
				} else {
					oldTemplateLength = template.getLength()+length;
				}
								
				int fieldOffset = template.getCurrentField().getOffset();
				
				template.insert(offset,insertedText);
				
				int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
				caret = offset + offsetChanged + length; 
				
				justEdited = true;
			} catch (WriteOutsideTemplateException e) {
				removeHandler(buffer);
				//System.out.println("Handler removed "+e.getMessage());
			} 
		}
	}
	
	public void contentRemoved(Buffer buffer, int startLine, int offset, 
							   int numLines, int length){
		
		if (!disabled){
			try{
				oldTemplateLength = template.getLength()-length;
				
				int fieldOffset = template.getCurrentField().getOffset();
				template.delete(offset,length);
				int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
				caret = offset + offsetChanged;
				
				justEdited = true;
				
				textArea.setCaretPosition(caret);
			} catch (WriteOutsideTemplateException e) {
				removeHandler(buffer);
				//System.out.println("Handler removed "+e.getMessage());
			}
		}
	} 
	
	/**
	 * Method postEdit()
	 */
	public void postEdit() {
		disabled = true;
		
		buffer.writeLock();
		
		TemplateCaretListener listener = TemplateCaretListener.removeCaretListener(textArea);
		Handler handler = removeHandler(buffer);
		
		//remove the old templape
		buffer.remove(template.getOffset(),oldTemplateLength);
		
		//insert the new templape
		buffer.insert(template.getOffset(),template.toString());
		
		putHandler(buffer,handler);
		TemplateCaretListener.putCaretListener(textArea,listener);
		
		buffer.writeUnlock();
		
		textArea.setCaretPosition(caret);
		
		disabled = false;
		
		justEdited = false;
	}
	
	//{{{ Handler management
	
	private static Hashtable handlers = new Hashtable();
		
	public static void putHandler(Buffer buffer, Handler t){
		Handler h = getHandler(buffer);
		buffer.removeBufferChangeListener(h);
		buffer.addBufferChangeListener(t);
		handlers.put(buffer,t);
	}
	
	public static Handler getHandler(Buffer buffer){
		return (Handler)handlers.get(buffer);
	}
	
	public static Handler removeHandler(Buffer buffer){
		Handler h = getHandler(buffer);
		buffer.removeBufferChangeListener(h);
		handlers.remove(buffer);
		return h;
	}
	
	public static boolean enabled(Buffer buffer){
		return null != handlers.get(buffer);
	}
	
	//}}}
} 
