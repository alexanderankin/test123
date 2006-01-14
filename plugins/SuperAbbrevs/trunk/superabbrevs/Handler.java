package superabbrevs;

import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.jedit.Buffer;
import java.util.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class Handler extends BufferChangeAdapter {
	
	private Template template;	
	private boolean disabled;
	private JEditTextArea textArea;
	private boolean justDeleted = false;
	private int caret;
	
	public Handler(Template template, JEditTextArea textArea){
		this.textArea = textArea; 
		this.template = template;
	}
	
	public boolean justDeleted(){
		return justDeleted;
	}
	
	public void moveCaret(){
		justDeleted = false;
	}
		
	public int getCaret(){
		return caret;
	}
	
	public boolean isDisabled(){
		return disabled;
	}
	
	public void contentInserted(Buffer buffer, int startLine, int offset, 
								int numLines, int length){
		if (!disabled){
			disabled = true;
			
			String insertedText = buffer.getText(offset, length);
			
			//System.out.println("Insert: "+justDeleted+" "+insertedText+" "+offset+" "+length);
			
			try{
				
				if(justDeleted){
					offset = caret;
				}
				
				int oldTemplateLength = template.getLength();
								
				int fieldOffset = template.getCurrentField().getOffset();
				
				template.insert(offset,insertedText);
				
				int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
				caret = offset + offsetChanged + length; 
				
				//remove the old templape
				buffer.remove(template.getOffset(),oldTemplateLength+length);
				
				//insert the new templape
				buffer.insert(template.getOffset(),template.toString());
				
				textArea.setCaretPosition(caret);
				
			} catch (WriteOutsideTemplateException e) {
				SuperAbbrevs.removeHandler(buffer);
				//System.out.println("Handler removed "+e.getMessage());
			} finally {
				disabled = false;
			}
		} else{
			//System.out.println("Insert disabled");
		}
	}
	
	public void contentRemoved(Buffer buffer, int startLine, int offset, 
							   int numLines, int length){
		if (!disabled){
			
			disabled = true;
			
			try{
				//System.out.println("Delete: "+template.getOffset()+" "+(template.getLength()-length));
				int oldTemplateLength = template.getLength();
				
				int fieldOffset = template.getCurrentField().getOffset();
				template.delete(offset,length);
				int offsetChanged = template.getCurrentField().getOffset() - fieldOffset;
				caret = offset + offsetChanged;
				
				
				int templateLength = oldTemplateLength-length;
				int bufferLength = buffer.getLength();
				//remove the old templape
				buffer.remove(template.getOffset(),templateLength);
				
				
				//insert the new templape
				buffer.insert(template.getOffset(),template.toString());
				
				System.out.println("Delete : Set Caret "+caret);
				textArea.setCaretPosition(caret);
				
				justDeleted = true;
			} catch (WriteOutsideTemplateException e) {
				SuperAbbrevs.removeHandler(buffer);
				//System.out.println("Handler removed "+e.getMessage());
			} finally {
				disabled = false;
			}
		} else{
			System.out.println("Delete disabled");
		}
	} 
	
	/**
	 * Returns the value of template.
	 */
	public Template getTemplate(){
		return template;
	}
} 
