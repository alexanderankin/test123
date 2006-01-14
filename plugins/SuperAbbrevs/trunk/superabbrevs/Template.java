package superabbrevs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {

	private static Pattern variablePattern = 
		Pattern.compile("\\$\\{(\\d+):(\\w*)\\}|\\$(\\d+)|\\$(\\w+)");
	private List template = new ArrayList();
	private List fieldList = new ArrayList();
	private int currentField;
	
	private int offset;
	
	public int getOffset() {
		return offset;
	}

	private int length;
	
	public int getLength() {
		return length;
	}

	public Template(int at, String templateString){
		
		offset = at;
		
		TreeMap fieldMap = buildTemplate(templateString);	
		
		// updates the fieldMap and the template
		replaceTempFields(fieldMap);	
		
		buildFieldList(fieldMap);
		
		updateOffsets();
		
		currentField = 0;
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
	
	private void buildFieldList(TreeMap fieldMap) {
		// add all the field to the fieldlist in sorted order 
		Iterator iter = fieldMap.values().iterator();
		while (iter.hasNext()) {
			fieldList.add(iter.next());
		}
	}

	private TreeMap replaceTempFields(TreeMap fieldMap){
		// find any tempfield and replace them with real fields
		for (int i=0; i<template.size(); i++){
			Field field = (Field) template.get(i);
			if (field instanceof TempField) {
				TempField tempField = (TempField) field;
				Integer number = tempField.getNumber();
				VariableField variableField = getVariableField(fieldMap, number);
				
				if (variableField == null) {
					// if it's not defined, define it with a empty value
					variableField = new VariableField("");
					fieldMap.put(number,variableField);
					
					// replace the tempfield with the real field 
					template.set(i,variableField);
				} else {
					// if the variable is aready defined 
					// replace the tempfield with the real field 
					template.set(i, new VariableFieldPointer(variableField));
				}
			}
		}
		
		return fieldMap;
	}
	
	private TreeMap buildTemplate(String templateString){
		
		Matcher variableMatcher = variablePattern.matcher(templateString);
		
		TreeMap fieldMap = new TreeMap();
		
		boolean endFound = false;
		
		int start; 
		int end = 0;
		
		while(variableMatcher.find()){
			//TODO fejl escape er ikke lavet rigtigt
			
			start = variableMatcher.start();
			// check if the variable is escaped
			if (0 == start || templateString.charAt(start-1) != '\\'){
			
				// add the text before variable as a static field
				if(end < start){
					String staticText = templateString.substring(end,start);
					addStaticField(staticText);
				}
				
				String defaultVariableNumber = variableMatcher.group(1);
				String normalVariableNumber = variableMatcher.group(3);
				if (isDefined(defaultVariableNumber)) {
					// default variable
					Integer number = new Integer(defaultVariableNumber);
					// check if the variable is already defined
					if(isDefined(fieldMap, number)){
						// if the variable is aready defined
						// add a pointer to the existing field to the template
						addFieldPointer(getVariableField(fieldMap, number));
					} else {
						String defaultVariableValue = variableMatcher.group(2);
						// if it's not defined, define it
						addVariableField(fieldMap, number, defaultVariableValue);
					}
				} else if (isDefined(normalVariableNumber)){
					// normal variable
					Integer number = new Integer(normalVariableNumber);
					
					// check if the variable is already defined
					if(isDefined(fieldMap, number)){
						addFieldPointer(getVariableField(fieldMap, number));
					} else {
						// add a tempfield so we can give it a value in second run
						addTempField(number);
					}
				} else {
					String textVariable = variableMatcher.group(4);
					// text variable
					if (textVariable.equals("end")) {
						addEndField(fieldMap);
						
						endFound = true;
					}
				}
				end = variableMatcher.end();
			}
			
			
		}
		// the last text as a static field
		if(end < templateString.length()){
			String staticText = templateString.substring(end);
			addStaticField(staticText);
		}
		
		// if $end is not found, then insert at the end of the template
		if (!endFound) {
			addEndField(fieldMap);
		}
		
		return fieldMap;
	}

	private boolean isDefined(String defaultVariableNumber) {
		return defaultVariableNumber != null;
	}

	private boolean isDefined(TreeMap fieldMap, Integer number) {
		return fieldMap.containsKey(number);
	}

	private VariableField getVariableField(TreeMap fieldMap, Integer number) {
		return (VariableField)fieldMap.get(number);
	}

	private void addTempField(Integer number) {
		template.add(new TempField(number));
	}

	private void addVariableField(TreeMap fieldMap, Integer number, String value) {
		VariableField field;
		field = new VariableField(value);
		fieldMap.put(number,field);
		
		// add the field to the template
		template.add(field);
	}

	private void addFieldPointer(VariableField field) {
		template.add(new VariableFieldPointer(field));
	}

	private void addStaticField(String staticText) {
		StaticField staticField = 
			new StaticField(staticText);
		template.add(staticField);
	}

	private void addEndField(TreeMap fieldMap) {
		EndField endField = new EndField();
		Integer endFieldNumber = new Integer(Integer.MAX_VALUE);
		fieldMap.put(endFieldNumber,endField);
		template.add(endField);
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
