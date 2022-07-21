package beauty.parsers.json;


public class JSONBeautyListener extends JSONBaseListener {

    private StringBuilder output;
    private int tabCount = 0;
    private String tab;

    public JSONBeautyListener(int initialSize, boolean softTabs, int tabWidth) {
        output = new StringBuilder(initialSize);
        if (softTabs) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tabWidth; i++) {
                sb.append(' ');   
            }
            tab = sb.toString();
        }
        else {
            tab = "\t";   
        }
    }
    
    public String getText() {
        return output.toString();
    } 
    
    @Override public void enterObject( JSONParser.ObjectContext ctx ) {
        outdent();
        if ( output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
            output.append( '\n' );
        }
        indent();
        output.append( "{\n" );
        ++tabCount;
        indent();
    } 
    
    @Override public void exitObject( JSONParser.ObjectContext ctx ) {
        // if there is a trailing comma and whitespace from adding the children
        // of this object, remove the comma and whitespace from the output.
        chopToComma();
        
        output.append('\n');
        --tabCount;
        indent();
        output.append( '}' );
    } 
    
    @Override public void enterArray( JSONParser.ArrayContext ctx ) {
        // if the array has more than one value, format like this:
        // [
        //     values
        // ]
        if (ctx.value() != null && ctx.value().size() > 1) {
            output.append('\n');
            indent();
            output.append( "[\n" );
            ++tabCount;
            indent();
        }
        else {
            // the array only has one value so format like this:
            // [ value ]
            output.append( '[' );   
        }
    } 
    
    @Override public void exitArray( JSONParser.ArrayContext ctx ) {
        // if there is a trailing comma and whitespace from adding the children
        // of this array, remove the comma and whitespace from the output.
        chopToComma();
        
        // if there were multiple children, need to add a new line and outdent
        // before adding the closing ]
        if (ctx.value() != null && ctx.value().size() > 1) {
            output.append('\n');
            --tabCount;
            indent();
        }
        else {
            outdent();   
        }
        output.append( ']' );
    } 
    
    @Override public void enterPair( JSONParser.PairContext ctx ) {
        output.append( ctx.STRING().getText() ).append( ": " );
    } 
    
    @Override public void exitPair( JSONParser.PairContext ctx ) {
        if ( ctx.getParent() instanceof JSONParser.ArrayContext || ctx.getParent() instanceof JSONParser.ObjectContext ) {
            outdent();
            output.append( ",\n" );
            indent();
        }
    } 
    
    @Override public void enterValue( JSONParser.ValueContext ctx ) {
        if ( ctx.STRING() != null ) {
            output.append( ctx.STRING().getText() );
        } else if ( ctx.NUMBER() != null ) {
            output.append( ctx.NUMBER().getText() );
        }
    } 
    
    @Override public void exitValue( JSONParser.ValueContext ctx ) {
        if ( ctx.getParent() instanceof JSONParser.ArrayContext || ctx.getParent() instanceof JSONParser.ObjectContext ) {
            outdent();
            output.append( ",\n" );
            indent();
        }
    }

    private void indent() {
        for ( int i = 0; i < tabCount; i++ ) {
            output.append( tab );
        }
    }
    
    private void outdent() {
        if (output.length() < tab.length()) {
            return;   
        }
        while(output.charAt(output.length() - 1) == '\t' || output.charAt(output.length() - 1) == ' ') {
            output.deleteCharAt(output.length() - 1);   
        }
    }

    // remove the last comma and trailing whitespace, only chops if there is
    // only whitespace following the last comma.
    private void chopToComma() {
        int commaPosition = output.lastIndexOf( "," );
        if ( commaPosition > -1 ) {
            for ( int i = commaPosition + 1; i < output.length(); i++ ) {
                if ( !Character.isWhitespace( output.charAt( i ) ) ) {
                    return;
                }
            }
            output.delete( commaPosition, output.length() - 1 );
        }
    }
}