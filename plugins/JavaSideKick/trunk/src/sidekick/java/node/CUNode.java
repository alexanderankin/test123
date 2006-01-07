package sidekick.java.node;


// an extension of TigerNode for a compilation unit
public class CUNode extends TigerNode {
    
    private String packageName = "";
    
    public CUNode() {
        super( "", 0 );
    }
    
    public void setPackageName(String name) {
        packageName = name;   
    }
    
    public String getPackageName() {
        return packageName;   
    }

    public int getOrdinal() {
        return 0;
    }
}


