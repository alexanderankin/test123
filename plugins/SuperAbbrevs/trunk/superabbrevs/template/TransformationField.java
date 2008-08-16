package superabbrevs.template;
import superabbrevs.SuperAbbrevsIO;

import org.gjt.sp.jedit.bsh.*;
import java.io.*;

/**
 * @author Sune Simonsen
 * class TransformationField
 * a template field that can transform the field value with abitrarily code
 */
public class TransformationField implements Field {

	private VariableField field;
	private String code;
	private Interpreter interpreter;
	private String lastEvaluated;
	private String lastResult;
	
	/*
	 * Constructor for TransformationField
	 */
	public TransformationField(VariableField field, String code, Interpreter interpreter){
		this.field = field;
		this.code = code;
		this.interpreter = interpreter;
		
		try {
			interpreter.source(SuperAbbrevsIO.getAbbrevsFunctionPath());
		} catch ( TargetError e ) {
			// The script threw an exception
			Throwable t = e.getTarget();
		} catch ( ParseException e ) {
			// Parsing error
			
		} catch ( EvalError e ) {
			// General Error evaluating script
			
		} catch ( FileNotFoundException e) {
			// File not found
		} catch ( IOException e){
			// Input output error
		}
	}
	
	public String toString() {
		String s = field.toString();
				
		try {
			if(!s.equals(lastEvaluated)){
				interpreter.set("s", s);
				
				lastResult = (String)interpreter.eval(code);
				lastEvaluated = s;
			}
		} catch ( TargetError e ) {
			// The script threw an exception
			System.out.println("TargetError");
			System.out.println(e.getMessage());
			lastResult = "<target error>";
			lastEvaluated = "<target error>";
		} catch ( ParseException e ) {
			// Parsing error
			System.out.println("ParseException");
			System.out.println(e.getMessage());
			lastResult = "<pasing error>";
			lastEvaluated = "<pasing error>";
		} catch ( EvalError e ) {
			// General Error evaluating script
			System.out.println("EvalError");
			System.out.println(e.getErrorLineNumber()); 
			System.out.println(e.getMessage());
			lastResult = "<eval error>";
			lastEvaluated = "<eval error>";
		}
		
		return lastResult;
	}
	
	public int getLength() {
		String s = field.toString();
		if(!s.equals(lastEvaluated)){
			toString();
		} 
		
		return lastResult.length();
	}
	
	public String firstUp(String s){
		StringBuffer res = new StringBuffer(s);
		if(0 < res.length()){
			char first = res.charAt(0);
			res.setCharAt(0,Character.toUpperCase(first));
		}
		return res.toString();
	}
}
