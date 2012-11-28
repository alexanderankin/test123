package superabbrevs.lexer;

import superabbrevs.template.*;
import java.io.*;
import java.util.*;
import org.gjt.sp.jedit.bsh.*;

/**
 * @author Sune Simonsen
 * class TemplateParser
 * Parses the input given by the lexer
 */
public class TemplateParser {

	private TemplateLexer lexer;
	private List template = new ArrayList();
	private TreeMap fieldMap;
	private boolean endFound = false;
	private Interpreter interpreter;
	
	/*
	 * Constructor for Parser
	 */
	public TemplateParser(TemplateLexer lexer, Interpreter interpreter){
		this.lexer = lexer;
		this.interpreter = interpreter;
	}
	
	/**
	 * Method parse()
	 * parse the input from the lexer
	 * @return the constructed template
	 */
	public Template parse() throws IOException {
		fieldMap = new TreeMap();
		
		Token t;
		while (null != (t = lexer.nextToken())){
			switch (t.getType()) {
				case Token.TEXT_FIELD:
					textField((String)t.getValue(0));
				break;
				case Token.FIELD:
					field((Integer)t.getValue(0), (String)t.getValue(1));
				break;
				case Token.FIELD_POINTER:
					fieldPointer((Integer)t.getValue(0));
				break;
				case Token.TRANSFORMATION_FIELD:
					transformationField((Integer)t.getValue(0), (String)t.getValue(1));
				break;
				case Token.END_FIELD:
					endField();
				break;
			}
		}
		// if $end is not found, then insert at the end of the template
		if (!endFound) {
			addEndField();
		}
		
		replaceTempFields();
		
		List fieldList = buildFieldList();
		
		return new Template(template, fieldList);
	}
	
	/**
	 * Method textField(String text)
	 * process a text field
	 */
	public void textField(String text) {
		addTextField(text);
	}
	
	/**
	 * Method field(Integer number, String value)
	 * process a template field
	 */
	public void field(Integer number, String value) {
		// check if the variable is already defined
		if(isDefined(number)){
			// if the variable is aready defined
			// add a pointer to the existing field to the template
			addFieldPointer(getVariableField(number));
		} else {
			// if it's not defined, define it
			addVariableField(number, value);
		}
	}
		
	/**
	 * Method fieldPointer(Integer number)
	 * process a template field pointer
	 */
	public void fieldPointer(Integer number) {
		// check if the variable is already defined
		if(isDefined(number)){
			addFieldPointer(getVariableField(number));
		} else {
			// add a tempfield so we can give it a value in second run
			addTempField(number);
		}
	}
	
	/**
	 * Method endField()
	 * process the template end field
	 */
	public void endField() {
		addEndField();
		endFound = true;
	}
	
	/**
	 * Method transformationField(Integer number, String code)
	 * process a template transformation field 
	 */
	public void transformationField(Integer number, String code) {
		// check if the variable is already defined
		if(isDefined(number)){
			addTransformationField(getVariableField(number),code);
		} else {
			// add a tempfield so we can give it a value in second run
			addTempField(number,code);
		}
	}
	
	
	
	private boolean isDefined(Integer number) {
		return fieldMap.containsKey(number);
	}
	
	private VariableField getVariableField(Integer number) {
		return (VariableField)fieldMap.get(number);
	}

	private void addTempField(Integer number) {
		template.add(new TempField(number));
	}
	
	private void addTempField(Integer number, String code) {
		template.add(new TempTranformationField(number,code));
	}

	private void addVariableField(Integer number, String value) {
		VariableField field;
		field = new VariableField(value);
		fieldMap.put(number,field);
		
		// add the field to the template
		template.add(field);
	}

	private void addFieldPointer(VariableField field) {
		template.add(new VariableFieldPointer(field));
	}
	
	private void addTransformationField(VariableField field, String code) {
		template.add(new TransformationField(field,code,interpreter));
	}

	private void addTextField(String text) {
		TextField textField = new TextField(text);
		template.add(textField);
	}

	private void addEndField() {
		EndField endField = new EndField();
		Integer endFieldNumber = new Integer(Integer.MAX_VALUE);
		fieldMap.put(endFieldNumber,endField);
		template.add(endField);
	}
	
	private List buildFieldList() {
		ArrayList fieldList = new ArrayList();
		// add all the field to the fieldlist in sorted order 
		Iterator iter = fieldMap.values().iterator();
		while (iter.hasNext()) {
			fieldList.add(iter.next());
		}
		return fieldList;
	}

	private void replaceTempFields(){
		// find any tempfield and replace them with real fields
		for (int i=0; i<template.size(); i++){
			Field field = (Field) template.get(i);
			if (field instanceof TempField) {
				TempField tempField = (TempField) field;
				
				Integer number = tempField.getNumber();
				VariableField variableField = getVariableField(number);
				
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
			} else if(field instanceof TempTranformationField) {
				TempTranformationField tempField = (TempTranformationField) field;
				
				Integer number = tempField.getNumber();
				VariableField variableField = getVariableField(number);
				
				if (variableField == null) {
					// if variable is not defined then remove the field
					template.remove(i);
					i--;
				} else {
					// if the variable is aready defined 
					// replace the tempfield with the real field
					String code = tempField.getCode();
					template.set(i, new TransformationField(variableField,code,interpreter));
				}
			}
		}
	}
}
