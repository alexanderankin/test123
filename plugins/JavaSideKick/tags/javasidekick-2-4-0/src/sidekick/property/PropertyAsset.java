package sidekick.property;

import sidekick.Asset;
import javax.swing.Icon;
import sidekick.property.parser.property.*;

public class PropertyAsset extends Asset {
    
    private Property property;
    
    public PropertyAsset(Property prop) {
        super(prop.getKey());
        property = prop;        
    }
	public Icon getIcon() { return null; }

    public String getShortString() { return property.getKey(); }

	public String getLongString() { return property.toString(); }

	
}
