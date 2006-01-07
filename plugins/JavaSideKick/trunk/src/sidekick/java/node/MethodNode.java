package sidekick.java.node;


import java.util.*;


// an extension of TigerNode for a method
public class MethodNode extends TigerNode {
    
    String typeParams = null;
    List formalParams = null;
    String returnType = null;


    public MethodNode() {
        super();
    }
    
    public MethodNode( String name, int modifiers, String typeParams, List formalParams, String returnType ) {
        super(name, modifiers);
        this.typeParams = typeParams;
        this.formalParams = formalParams;
        setReturnType(returnType);
    }
    
    public int getOrdinal() {
        return METHOD;
    }
    
    public void setFormalParams( List p ) {
        formalParams = p;
    }

    /**
     * @return raw value for formal params    
     */
    public List getFormalParams() {
        return formalParams;
    }

    /**
     * Returns a string showing the formal parameters for this method.  The
     * returned string is a comma separated list of parameter types, if 
     * <code>withNames</code> is true, then the returned string is a comma
     * separated list of type:name.  
     * <p>
     * Example: method is "void getX(int a, int b)",
     * <code>withNames</code> is false, returned string is "int,int".
     * <p>
     * Example: method is "void getX(int a, int b)",
     * <code>withNames</code> is true, returned string is "int a,int b".
     * @param withNames should returned string include the formal parameter names
     * @param typeAsSuffix if true and if withNames is true, name and type will 
     * be reversed, e.g. method is "void getX(int a, int b), returned string is
     * "a : int, b : int"
     * @param includeFinal if true, include any "final" modifier, e.g. method is
     * "void getX(int a, final int b)", returned string is "int, final int", or
     * if withNames is true, "int a, final int b", and if typeAsSuffix is true,
     * "a : int, b : final int"
     * @return parameters as string, see above
     */
    public String getFormalParams( boolean withNames, boolean typeAsSuffix, boolean includeFinal, boolean includeTypeArgs ) {
        
        if (formalParams == null || formalParams.size() == 0)
            return "";
        
        StringBuffer sb = new StringBuffer();
        for (Iterator it = formalParams.iterator(); it.hasNext(); ) {
            Parameter param = (Parameter)it.next();
            if (typeAsSuffix) {
                if (includeFinal && param.isFinal) 
                    sb.append("final ");
                sb.append(param.type.type);
                if (includeTypeArgs)
                    sb.append(param.type.typeArgs);
                if (param.isVarArg)
                    sb.append("...");
                if (withNames)
                    sb.append(" : ").append(param.name);
            }
            else {
                if (withNames)
                    sb.append(param.name).append(" : ");
                if (includeFinal && param.isFinal) 
                    sb.append("final ");
                sb.append(param.type.type);
                if (includeTypeArgs)
                    sb.append(param.type.typeArgs);
                if (param.isVarArg)
                    sb.append("...");
            }
            if (it.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }

    public void setTypeParams( String p ) {
        typeParams = p;
    }
    public String getTypeParams() {
        return typeParams == null ? "" : typeParams;
    }

    public void setThrows( String t ) {
        String[] throwsNames = t.split( "," );
        for ( int i = 0; i < throwsNames.length; i++ ) {
            if ( throwsNames[ i ].length() > 0 )
                addChild( new ThrowsNode( throwsNames[ i ] ) );
        }
    }

    /**
     * Overridden to return true if the node is a ThrowsNode.    
     */
    public boolean canAdd( TigerNode node ) {
        return node.getOrdinal() == TigerNode.THROWS;
    }

    public void setReturnType( String t ) {
        returnType = t;
    }
    public String getReturnType() {
        return returnType == null ? "" : returnType;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( super.toString() );
        if (getTypeParams() != null)
            sb.append(getTypeParams());
        sb.append("(").append( getFormalParams( true, false, true, true ) ).append(")");
        if ( returnType != null )
            sb.append( ": " ).append( returnType );

        return sb.toString();
    }
}


