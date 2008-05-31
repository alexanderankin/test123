package superabbrevs.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {

	private List template;  
	private List fieldList;
	private int currentField = 0;
	
	private int offset = 0;
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
		updateOffsets();
	}
	
	private int length;
	
	public int getLength() {
		return length;
	}

	public Template(List template, List fieldList){
		this.template = template;
		this.fieldList = fieldList;
	}
	
	public String toString() {
		StringBuffer output = new StringBuffer();
		Iterator iter = template.iterator();
		while (iter.hasNext()) {
			Field field = (Field) iter.next();
			output.append(field.toString());
		}
		return output.toString();
	}
	
	private void updateOffsets() {
		int offset = this.offset;
		
		Iterator iter = template.iterator();
		while (iter.hasNext()) {
			Field field = (Field) iter.next();
			
			if (field instanceof SelectableField) {
				SelectableField selectableField = (SelectableField) field;
				selectableField.setOffset(offset);
			}
			
			offset += field.getLength();
		}
		length = offset-this.offset;
	}
	
	public void insert(int at, String s) throws WriteOutsideTemplateException{
		SelectableField field = getCurrentField();
		if (field instanceof VariableField) {
			VariableField variableField = (VariableField) field;
			variableField.insert(at,s);
			updateOffsets();
		} else {
			throw new WriteOutsideTemplateException("Insert in $end field");
		}
	}
	
	public void delete(int at, int length) throws WriteOutsideTemplateException{
		SelectableField field = getCurrentField();
		if (field instanceof VariableField) {
			VariableField variableField = (VariableField) field;
			variableField.delete(at,length);
			updateOffsets();
		} else {
			throw new WriteOutsideTemplateException("Delete in $end field");
		}
		
	}

	public SelectableField getCurrentField(){
		return (SelectableField)fieldList.get(currentField); 		
	}
	
	public boolean inCurrentField(int pos){
		SelectableField field = getCurrentField();
		return field.inField(pos);
	}
	
	public void nextField(){
		currentField++;
		
		// there is always the $end field 
		if(fieldList.size() <= currentField){
			currentField = 0;
	 	}
	}
	
	public void prevField(){
		currentField--;
		
		// there is always the $end field 
		if(currentField < 0 ){
			currentField = fieldList.size()-1;
	 	}
	}
}
