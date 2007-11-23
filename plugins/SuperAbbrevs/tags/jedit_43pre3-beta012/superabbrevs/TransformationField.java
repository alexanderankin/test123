package superabbrevs;


import java.io.*;

import org.gjt.sp.jedit.bsh.EvalError;
import org.gjt.sp.jedit.bsh.Interpreter;
import org.gjt.sp.jedit.bsh.ParseException;
import org.gjt.sp.jedit.bsh.TargetError;

/**
 * @author Sune Simonsen
 * class TransformationField
 * a template field that can transform the field value with arbitrarily code
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
	public TransformationField(VariableField field, String code){
		this.field = field;
		this.code = code;
		interpreter = new Interpreter();
		try {
			interpreter.source(SuperAbbrevsIO.getGlobalFunctionPath());
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
			Throwable t = e.getTarget();
			lastResult = "<target error>";
			lastEvaluated = "<target error>";
		} catch ( ParseException e ) {
			// Parsing error
			lastResult = "<pasing error>";
			lastEvaluated = "<pasing error>";
		} catch ( EvalError e ) {
			// General Error evaluating script
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
