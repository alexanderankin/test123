package superabbrevs;

import org.gjt.sp.jedit.buffer.BufferChangeAdapter;
import org.gjt.sp.jedit.Buffer;
import java.util.*;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class Handler extends BufferChangeAdapter {
	
	private Template template;	
	private boolean disabled;
	private JEditTextArea textArea;
	
	public Handler(Template template, JEditTextArea textArea){
		this.textArea = textArea; 
		this.template = template;
	}
	
	public void contentInserted(Buffer buffer, int startLine, int offset, 
								int numLines, int length){
		try{
			if (!disabled){
				disabled = true;
								
				Range range = template.getField(offset);
				int start = offset - range.getFrom();
				
				template.insert(offset,length);
				
				String insertedText = buffer.getText(offset, length);
				
				ArrayList fields = template.getFields(range.getIndex());
		
				Iterator iter = fields.iterator();
				while (iter.hasNext()) {
					Range r = (Range) iter.next();
					if (range != r){
						buffer.insert(r.getFrom()+start,insertedText);
					}
				} 
				
				disabled = false;
			}
			
		}catch (OutOffRangeException e){
			SuperAbbrevs.removeHandler(buffer);
		}
	}
	
	public void contentRemoved(Buffer buffer, int startLine, int offset, 
							   int numLines, int length){
		try{
			if (!disabled){
				
				disabled = true;
				Range range = template.getField(offset);
				int start = offset - range.getFrom();
				
				template.delete(offset,length);
								
				ArrayList fields = template.getFields(range.getIndex());
				
				Iterator iter = fields.iterator();
				while (iter.hasNext()) {
					Range r = (Range) iter.next();
					if (range != r){
						buffer.remove(r.getFrom()+start,length);
					}
				} 
				
				disabled = false;
			}
		}catch (OutOffRangeException e){
			SuperAbbrevs.removeHandler(buffer);
		}
	} 
	
	/**
	 * Returns the value of template.
	 */
	public Template getTemplate(){
		return template;
	}
} 
