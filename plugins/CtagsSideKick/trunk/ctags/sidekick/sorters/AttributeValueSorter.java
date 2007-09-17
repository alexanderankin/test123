package ctags.sidekick.sorters;

import java.util.HashMap;

public class AttributeValueSorter extends AbstractAttributeValueSorter {

	private String attr;
	private String params;
	private HashMap<String, Integer> valueOrder;
	
	public AttributeValueSorter(String params) {
		this.params = params;
		if (params == null) {
			attr = null;
			valueOrder = null;
		} else {
			String [] parts = params.split(" ");
			attr = parts[0];
			valueOrder = new HashMap<String, Integer>();
			for (int i = 1; i < parts.length; i++)
				valueOrder.put(parts[i], Integer.valueOf(i));
		}
	}
	
	protected String getAttributeName() {
		return attr;
	}

	protected int getValueOrder(String value) {
		Integer val = valueOrder.get(value);
		if (val == null)
			return valueOrder.size() + 1;
		return val.intValue();
	}

	public String getName() {
		return "AttributeValue";
	}
	
	public String getParams() {
		return params;
	}
	
	public AttributeValueSorter getSorter(String params) {
		if (params == null)
			return new AttributeValueSorter(params);
		return this;
	}

}
