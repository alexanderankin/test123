package ctags.sidekick.sorters;

import ctags.sidekick.AbstractParameterizedObjectProcessor;
import ctags.sidekick.CtagsSideKickTreeNode;
import ctags.sidekick.Tag;

public abstract class AbstractAttributeValueSorter extends AbstractParameterizedObjectProcessor
	implements ITreeSorter
{

	public AbstractAttributeValueSorter(String name, String description) {
		super(name, description);
	}

	public int compare(CtagsSideKickTreeNode a, CtagsSideKickTreeNode b) {
		if ((a.getUserObject() instanceof Tag) &&
			(b.getUserObject() instanceof Tag))
		{
			Tag at = (Tag) a.getUserObject();
			Tag bt = (Tag) b.getUserObject();
			String attr = getAttributeName();
			String aval = at.getField(attr);
			if (aval != null) {
				String bval = bt.getField(attr);
				if (bval != null) {
					int aord = getValueOrder(aval);
					int bord = getValueOrder(bval);
					if (aord < bord)
						return -1;
					if (aord == bord)
						return 0;
					return 1;
				}
			}
		}
		return 0;
	}

	protected abstract String getAttributeName();
	protected abstract int getValueOrder(String value);

}
