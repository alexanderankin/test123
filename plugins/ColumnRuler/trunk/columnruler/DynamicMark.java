package columnruler;

import org.gjt.sp.jedit.*;

/**
 * A ruler mark which can move automatically in response to events.
 */
public abstract class DynamicMark extends Mark {
	public DynamicMark(String name) {
		super(name);
	}

	/**
	 * Called by the ruler when it starts using this mark.
	 */
	public abstract void activate(ColumnRuler ruler);

	/**
	 * Called by the ruler when it is done using this mark.
	 */
	public abstract void deactivate();

	/**
	 * Called by the ruler in response to events which will require marks to update themselves.
	 * This should prevent most marks from needing to implement EBComponent.
	 */
	public abstract void update();
}
