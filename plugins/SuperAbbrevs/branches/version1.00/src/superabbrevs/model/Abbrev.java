package superabbrevs.model;

/**
 * @author sune
 * Created on 27. januar 2007, 21:58
 *
 */
public class Abbrev implements Comparable {
        
    public enum InputTypes {
        NO_INPUT ("No input"),
        BUFFER ("Buffer"),
        LINE ("Line"),
        WORD ("Word"),
        CHAR ("Character");
        
        private String label;
        InputTypes(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    public enum InputSelectionTypes {
        NO_INPUT ("No input"),
        SELECTION ("Selection"),
        SELECTED_LINES ("Selected lines");
        
        private String label;
        InputSelectionTypes(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    public enum ReplacementTypes {
        AT_CARET ("At caret"),
        BUFFER ("Buffer"),
        LINE ("Line"),
        WORD ("Word"),
        CHAR ("Character"),
        SELECTION ("Selection"),
        SELECTED_LINES ("Selected lines");
        
        private String label;
        ReplacementTypes(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    /**
     * A short name of the abbreviation.
     */
    public String name;
    
    /**
     * The abbreviation that will get expanded.
     */
    public String abbreviation;    
    
    /**
     * The expansion of the abbreviation.
     */
    public String expansion;
    
    /**
     * The type of input provided for the abbreviation generation.
     */
    public InputTypes inputType = InputTypes.NO_INPUT;
    
    public static class InvokedAsCommand {
        /**
         * The type of input provided for the abbreviation generation.
         */
        public InputTypes inputType = InputTypes.NO_INPUT;
    
        /**
         * The type of input provided for the abbreviation generation when text 
         * is selected in the buffer.
         */
        public InputSelectionTypes inputSelectionType = 
                InputSelectionTypes.NO_INPUT;

        /**
         * The type of replacement that will be performed when abbriviation is 
         * inserted.
         */
        public ReplacementTypes replacementType = 
                ReplacementTypes.AT_CARET;
    }
    
    public InvokedAsCommand whenInvokedAsCommand = new InvokedAsCommand();
    
    /** 
     * Creates a new instance of Abbrev 
     */
    public Abbrev(String name, String abbreviation, String expansion) {
        this.name = name; 
        this.abbreviation = abbreviation;
        this.expansion = expansion;
    }
    
    @Override
    public String toString() { 
        return name + " (" + abbreviation + ")";
    }
    
    public int compareTo(Object o) {
        if(o instanceof Abbrev) {
            return name.compareToIgnoreCase(((Abbrev)o).name);
        } else {
            return 1;
        }
    }
}
