package superabbrevs.model;

import java.util.ArrayList;

public class Mode {
    private String name;
    private ArrayList<Abbrev> abbreviations = new ArrayList<Abbrev>();
    private ArrayList<Variable> variables = new ArrayList<Variable>();

    public ArrayList<Abbrev> getAbbreviations() {
        return abbreviations;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }
    
    public Mode(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mode other = (Mode) obj;
        if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    
}
