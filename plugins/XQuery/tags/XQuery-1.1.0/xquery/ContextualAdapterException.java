/*
 * Created on Mar 11, 2004
 * @author Wim Le Page
 * @author Pieter Wellens
 *
 */
package xquery;

/**
 * @author Wim Le Page
 * @author Pieter Wellens
 * @version Mar 11, 2004
 *
 */
public class ContextualAdapterException extends AdapterException {

	private int line;
	private int start;
	private int end;
	
	/**
	 * @param s
	 * @param throwable
	 */
	public ContextualAdapterException(String s, Throwable throwable, int line, int start, int end) {
		super(s, throwable);
		this.line = line;
		this.start = start;
		this.end = end;
	}
	
	/**
	 * @param s
	 */
	public ContextualAdapterException(String s, int line, int start, int end) {
		super(s);
		this.line = line;
		this.start = start;
		this.end = end;
	}

	/**
	 * @return
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @param line
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * @return
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start
	 */
	public void setStart(int start) {
		this.start = start;
	}

}
