package beauty.beautifiers;

import java.util.regex.*;

// This is a customization of the default beautifier specifically for java
// code.  This is used by the jsp parser to beautify java scriptlets.  Java
// scriptlets are often just fragments of java code split up by jsp tags.  The
// standard java parser won't accept such code.  This one will, but it won't
// beautify as well as the standard java parser.
public class JavaLineBeautifier extends DefaultBeautifier {
    
    // pattern to match generics (parameterized types).  
    // This is pretty lame in that it only looks for <sometext>.  That situation
    // is handled well, but nested generics are not. For example:
    // HashMap<String, List<String>> 
    // would end up as
    // HashMap<String, List < String>>
    // where the second < is still treated as an operator.  
    // The regex does handle things like HashMap<String, String> properly, which
    // is about as complex as such things get in jsp files. Declarations like
    // <*> and <?> are not handled at all.
    // Since the intent of this beautifier is to clean up java scriptlets within 
    // a jsp file, I'm  not going to worry about it since people really shouldn't 
    // be using scriptlets much anyway.
    private final static Pattern genericPattern = Pattern.compile("\\s*[<]\\s*((\\w([,]\\s*)?)+)\\s*[>]");
    
    public JavaLineBeautifier() {
        super("java");   
    }
    
    @Override
    public String beautify(String text) {
        String s = super.beautify(text);   
        s = adjustGenerics(s);
        
        // ensure ! is not followed by whitespace
        s = s.replaceAll("[!]\\s*", "!"); 
        return s;
    }
    
    String adjustGenerics(String s) {
        String ls = getLineSeparator();
        String[] lines = s.split(ls);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            Matcher m = genericPattern.matcher(line);
            line = m.replaceAll("<$1>");
            sb.append(line).append(ls);
        }
        return sb.toString();
    }
}