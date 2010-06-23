package beauty.beautifiers;

import java.util.regex.*;

// This is a customization of the default beautifier specifically for java
// code.  This is used by the jsp parser to beautify java scriptlets.
public class JavaLineBeautifier extends DefaultBeautifier {
    
    private Pattern genericPattern;
    
    public JavaLineBeautifier() {
        super("java");   
        genericPattern = Pattern.compile("\\s*[<]\\s*(.*?)\\s*[>]");
    }
    
    @Override
    public String beautify(String text) {
        String s = super.beautify(text);   
        s = adjustGenerics(s);
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