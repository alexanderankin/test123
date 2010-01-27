package launcher;

import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.EventObject;

import org.gjt.sp.jedit.ActionContext;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.Macros;
import org.gjt.sp.jedit.ServiceManager;
import org.gjt.sp.jedit.View;

public class LauncherActionContext extends ActionContext  implements Cloneable {
	
	protected transient WeakReference<LauncherType> weakLauncherType = null;
	protected String launcherTypeName = null;
	protected Object resolvedResource = null;
	
	public static LauncherActionContext INSTANCE =
		new LauncherActionContext();
	
	protected LauncherActionContext() {
		super();
	}
	
	public void init(LauncherType launcherType, Object resource) {
		this.weakLauncherType = launcherType == null ?
				null :
				new WeakReference<LauncherType>(launcherType);
		this.launcherTypeName = launcherType == null ?
				null :
				launcherType.getServiceName();
		this.resolvedResource = resource == null || launcherType==null ?
				null : 
				launcherType.resolve(resource);
	}
	
	public LauncherType getLauncherType() {
		LauncherType launcherType = weakLauncherType.get();
		if (launcherType == null) {
			launcherType = (LauncherType)ServiceManager.getService(
					LauncherType.LAUNCHER_TYPE_SERVICE_NAME,
					launcherTypeName);
		}
		return launcherType;
	}
	
	public Object getResolvedResource() {
		return resolvedResource;
	}
	
	protected void recordAction(View view, EditAction action, Object resolvedResource) {
		Macros.Recorder recorder = view.getMacroRecorder();
		if(recorder != null && !action.noRecord()) {
			String code;
			if (action instanceof Launcher) {
				code = ((Launcher)action).getCode(view, resolvedResource);
			} else
				code = action.getCode();
			recorder.record(code);
		}
	}

	@Override
	public void invokeAction(EventObject evt, EditAction action) {
		if (action instanceof Launcher) {
			View view = GUIUtilities.getView(
					(Component)evt.getSource());
			((Launcher)action).launch(view, resolvedResource);
			recordAction(view, action, resolvedResource);
		}
	}

	public void invokeAction(View view, Launcher launcher) {
		launcher.launch(view, resolvedResource);
		recordAction(view, launcher, resolvedResource);
	}

	@Override
	public LauncherActionContext clone() throws CloneNotSupportedException {
		LauncherActionContext clone= (LauncherActionContext)super.clone();
		clone.init(null, null);
		return clone;
	}
	
}
