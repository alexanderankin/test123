package superabbrevs;

/**
 * @author Sune Simonsen
 * class TextUtil
 * Text utility functions 
 */
public class TextUtil {
	public static String escape(String s){
		StringBuffer res = new StringBuffer();
		for(int i=0;i<s.length();i++){
			char c = s.charAt(i);
			
			switch (c){
			case '\\':
				res.append("\\\\");
				break;
			case '\n':
				res.append("\\n");
				break;
			case '\t':
				res.append("\\t");
				break;
			case '\r':
				res.append("\\r");
				break;
			case '\"':
				res.append("\\\"");
				break;
			default:
				res.append(c);
			}
		}
		return res.toString();
	}
}


