package nested.manager ;

import java.awt.Color;

import org.gjt.sp.jedit.jEdit;

public class NestedObject {
	
	private String mode ;
	private String submode; 
	private Color color ;
	private static Color DEFAULT_COLOR = Color.decode( jEdit.getProperty("view.bgColor") ) ;
	
	/**
	 * Constructor for a NestedObject using the default color
	 * @param mode mode of the buffer
	 * @param submode mode of the token that should be rendered differently
	 */
	public NestedObject( String mode, String submode ){
		this( mode, submode, DEFAULT_COLOR ) ;
	}
	
	/**
	 * Constructor for a NestedObject using the default color
	 * @param mode mode of the buffer
	 * @param submode mode of the token that should be rendered differently
	 */
	public NestedObject( String mode, String submode, Color color ){
		this.mode = mode ;
		this.submode = submode; 
		this.color = color ;
	}
	
	/**
	 * Getter function for the field color
	 */ 
	public Color getColor() {
		return color;
	}
	
	/**
	 * Getter function for the field mode
	 */ 
	public String getMode() {
		return mode;
	}
	
	/**
	 * Getter function for the field subMode
	 */ 
	public String getSubMode() {
		return submode;
	}
	
	/**
	 * Setter function for the field color
	 */
	public void setColor(Color color){
		this.color = color;
	}
	
	
}
