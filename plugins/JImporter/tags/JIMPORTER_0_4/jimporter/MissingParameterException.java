package jimporter;

/**
 * This exception is meant to be thrown when someone calls a method without 
 * setting a prerequisite variable.
 */
public class MissingParameterException extends RuntimeException {
    /** 
     * Constructor that allows you to specify the variable name that was not set
     * prior to calling a method.
     * 
     * @param requisiteVariableName The name of the variable that the user forgot
     * to set prior to calling the method.
     * @param method The name of the method that the user called without first 
     * setting a variable.
     */    
    public MissingParameterException(String requisiteVariableName, String method) {
        super("Internal Error: You must set the " + requisiteVariableName + " variable " +
          "prior to calling " + method);
    }
}
