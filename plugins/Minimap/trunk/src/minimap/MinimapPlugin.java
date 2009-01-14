package minimap;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditPane;
import org.gjt.sp.jedit.View;

public class MinimapPlugin extends EBPlugin {

	@Override
	public void handleMessage(EBMessage message) {
		// TODO Auto-generated method stub
		super.handleMessage(message);
	}

	public void stop()
	{
	}

	public void start()
	{
	}
	static public void show(View view) {
		EditPane editPane = view.getEditPane();
		editPane.add(new Minimap(editPane));
		editPane.validate();
	}

}
