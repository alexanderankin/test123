/*
 * Created on Dec 9, 2003
 * @author Wim Le Page
 * @author Pieter Wellens
 *
 */
package xquery;

import java.util.Properties;

/**
 * @author Wim Le Page
 * @author Pieter Wellens
 * @version 0.6.0
 *
 * This is the interface that adapterwriters have to implement
 */
public interface Adapter
{

	/*
	 * @param the adapter properties
	 */
	public abstract void setProperties(Properties prop)
		throws AdapterException;
	
	/*
	 * @param the string containing the base uri
	 */
	public abstract void setBaseUri(String uri)
		throws AdapterException;
		
	/*
	 * @param the string containing the path to the context document
	 */
	public abstract void loadContextFromFile(String path)
		throws AdapterException;
		
	/*
	 * @param the string containing the context document
	 */
	public abstract void loadContextFromString(String context)
		throws AdapterException;
	
	/*
	 * @param the string containing the XQuery
	 */
	public abstract String evaluateFromString(String xquery)
		throws AdapterException;
	
	/*
	 * @param the string containing the path to the file containing the XQuery
	 */
	public abstract String evaluateFromFile(String path)
		throws AdapterException;
	
	/*
	 * @description enables the performance monitoring
	 */
	public abstract void setPerformanceEnabled(boolean enabled)
		throws AdapterException;
	/*
	 * @description gets the performance
	 */
	public abstract String getPerformance()
		throws AdapterException;
}

