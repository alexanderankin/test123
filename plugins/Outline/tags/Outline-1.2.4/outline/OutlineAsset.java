package outline;

import javax.swing.*;
import javax.swing.text.*;

import sidekick.*;

/**
 * OutlineAssets correspond to folds.
 *
 * @author     Brad Mace
 * @version    $Revision: 1.3 $ $Date: 2004/03/01 00:12:13 $
 */
public class OutlineAsset extends Asset {
	private String description = "";

	public OutlineAsset(String name, int start) {
		super(name);
		this.start = new AssetPosition(start);
	}

	public void setEnd(int i) {
		this.end = new AssetPosition(i);
	}

	public void setDescription(String desc) {
		description = desc;
	}

	//{{{ Accessors

	public Icon getIcon() {
		return null;
	}

	public String getShortString() {
		return name;
	}

	public String getLongString() {
		return description;
	}

	//}}}

	/**
	 *  AssetPositions are used to specify the start and end of an asset.
	 *
	 * @author     Brad Mace
	 * @version    $Revision: 1.3 $ $Date: 2004/03/01 00:12:13 $
	 */
	class AssetPosition implements Position {
		private int pos;

		AssetPosition(int pos) {
			this.pos = pos;
		}

		public int getOffset() {
			return pos;
		}
	}
}

