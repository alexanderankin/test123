package columnruler;

import org.gjt.sp.jedit.*;

public abstract class DynamicMark extends Mark implements EBComponent {
	public DynamicMark(String name) {
		super(name);
	}

	public abstract void activate(ColumnRuler ruler);

	public abstract void deactivate();

	public abstract void update();
}
