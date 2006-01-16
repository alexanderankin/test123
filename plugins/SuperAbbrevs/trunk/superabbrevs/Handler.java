package superabbrevs;

import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.jedit.Buffer;
import java.util.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class Handler extends BufferChangeAdapter {
	
	private Template template;	
	private boolean disabled;
	private JEditTextArea textArea;
	private boolean justEdited = false;
	private int caret;
	private int oldTemplateLength;
	private Buffer buffer;
	
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
			
			//System.out.println("Insert: "+justEdited+" "+insertedText+" "+offset+" "+length);
			
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
				SuperAbbrevs.removeHandler(buffer);
				//System.out.println("Handler removed "+e.getMessage());
			} 
		} 
		/* else{
			System.out.println("Insert disabled");
		} */
	}
	
	public void contentRemoved(Buffer buffer, int startLine, int offset, 
							   int numLines, int length){
								   
		if (!disabled){
			try{
				oldTemplateLength = template.getLength()-length;
				//System.out.println("Delete: "+template.getOffset()+" "+oldTemplateLength);
				
				int fieldOffset = template.getCurrentField().getOffset();
				template.delete(offset,length);
				int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
				caret = offset + offsetChanged;
				
				//System.out.println("Delete : Set Caret "+caret);
				
				justEdited = true;
				
				textArea.setCaretPosition(caret);
			} catch (WriteOutsideTemplateException e) {
				SuperAbbrevs.removeHandler(buffer);
				//System.out.println("Handler removed "+e.getMessage());
			}
		} 
		/*else{
			System.out.println("Delete disabled");
		}*/
	} 
	
	/**
	 * Method postEdit()
	 */
	public void postEdit() {
		
		disabled = true;
		
		//remove the old templape
		buffer.remove(template.getOffset(),oldTemplateLength);
		
		//insert the new templape
		buffer.insert(template.getOffset(),template.toString());
		
		textArea.setCaretPosition(caret);
		
		disabled = false;
		
		justEdited = false;
	}
	
	/**
	 * Returns the value of template.
	 */
	public Template getTemplate(){
		return template;
	}
} 
