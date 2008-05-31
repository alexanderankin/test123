package superabbrevs.lexer;

import java.util.*;

/**
 * @author Sune Simonsen
 * class Tokens
 * Token for the lexer
 */
public class Token {
	public static final int TEXT_FIELD = 1;
	public static final int CODE_OUTPUT_FIELD = 2;
	public static final int CODE = 3;
	public static final int FIELD = 4;
	public static final int FIELD_POINTER = 5;
	public static final int END_FIELD = 6;
	public static final int TRANSFORMATION_FIELD = 7;
	
	//{{{ Field ArrayList values
	protected ArrayList values;
	
	/**
	 * Getter function for the field values
	 */ 
	public ArrayList getValues() {
		return values;
	}
	//}}}
	
	public Object getValue(int number) {
		return values.get(number);
	}
	
	/**
	 * Method getStringValue()
	 * gets the value with the specified number and cast it to a string 
	 */
	public String getStringValue(int number) {
		return (String)values.get(number);
	}
	
	public void addValue(Object value) {
		values.add(value);
	}
	
	//{{{ Field int type
	private int type;
	
	/**
	 * Getter function for the field type
	 */ 
	public int getType() {
		return type;
	}
	//}}}
	
	/*
	 * Constructor for Token
	 */
	public Token(int type){
		this.type = type;
		this.values = new ArrayList();
	}
	
	public String toString(){
		
		return "<"+type+","+values+">";
	}
}
