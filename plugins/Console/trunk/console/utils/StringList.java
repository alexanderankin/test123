/*
 * Created on 19-Jul-2005
 *
 */
package console.utils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Analogous to the QStringList, with some special helpers for working with
 * Strings. Also supports my favorite operations, split and join, making it a
 * very convenient class for dealing with tabular data.
 * 
 * @author sae at uvic dot ca
 *
 */
public class StringList extends LinkedList /* <String> */{
	private static final long serialVersionUID = 4287704227878304132L;

	public StringList(Object[] array) {
		for (int i=0; i<array.length; ++i) {
			add(array[i].toString());
		}
	}
    
    public StringList() {}
    
    public static StringList split(String orig, Object delim) {
    	if (orig == null) return new StringList();
        return new StringList(orig.split(delim.toString()));
    }

    public String toString() {
        return join("\n");
    }
    
    public static String join(Collection c, String delim) {
        StringList sl = new StringList();
        Iterator itr = c.iterator();
        while (itr.hasNext()) {
            Object o = itr.next();
            String s = o.toString();
            sl.add(s);
        }
        return sl.join(delim);
    }
    
    public String join(String delim) {
        int s = size();
        if (s < 1) return "";
        if (s == 1) return get(0).toString();
        else {
           StringBuffer retval = new StringBuffer();
           retval.append(get(0));
           for (int i=1; i<s; ++i) 
                retval.append(delim + get(i));
           return retval.toString();
        }

    }
    
    public static void main(String args[]) {
           String teststr = "a,b,c,d,e,f";
           StringList sl = StringList.split(teststr, ",");
           String joinstr = sl.join(",");
    //       assert(teststr.equals(joinstr));
           System.out.println ("Test Passed");
        
    }

}
