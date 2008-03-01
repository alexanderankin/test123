package superabbrevs.model;

public class DynamicVariable implements Variable {

    private String name;
    private String code;
    
    public String getName() {
        return name;
    }

    public String getValue() {
        return "";
    }

    public DynamicVariable(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
