package tags;

import java.util.Vector;

public class AttributeValueFilter {
	private String attr;
	private String val;
	public AttributeValueFilter(String attr, String val) {
		this.attr = attr;
		this.val = val;
	}
	public boolean pass(TagLine tag) {
		Vector<ExuberantInfoItem> items = tag.getExuberantInfoItems();
		for (int i = 0; i < items.size(); i++) {
			ExuberantInfoItem item = (ExuberantInfoItem) items.get(i);
			String [] parts = item.toString().split(":", 2);
			if (parts.length < 2)
				continue;
			if (parts[0].equals(attr) && parts[1].equals(val))
				return true;
		}
		return false;
	}
}
