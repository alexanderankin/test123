package superabbrevs;

/**
 * @author sune
 * Created on 27. januar 2007, 21:58
 *
 */
public class Abbrev implements Comparable {
    
    /**
     * A short name of the abbreviation.
     */
    public String name;
    
    /**
     * The abbreviation that will get expanded.
     */
    public String abbrev;    
    
    /**
     * The expansion of the abbreviation.
     */
    public String expansion;
    
    /** 
     * Creates a new instance of Abbrev 
     */
    public Abbrev(String name, String abbrevs, String expansion) {
        this.name = name; 
        this.abbrev = abbrevs;
        this.expansion = expansion;
    }
    
    public String toString() { 
        return name;
    }
    
    public int compareTo(Object o) {
        if(o instanceof Abbrev) {
            return name.compareTo(((Abbrev)o).name);
        } else {
            return 1;
        }
    }
}
